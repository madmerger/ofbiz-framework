/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.ofbiz.migration.product.flow;

import jakarta.persistence.EntityManager;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.common.SequenceService;
import org.apache.ofbiz.migration.product.adapter.OrderPort;
import org.apache.ofbiz.migration.product.simple.IssuanceServices;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class QuickShipEntireOrderFlow {
    private final OrderPort orders;
    private final EntityRegistry entities;
    private final EntityManager entityManager;
    private final MigrationServiceDispatcher dispatcher;
    private final SequenceService sequences;
    private final IssuanceServices issuance;
    private final UpdateShipmentFlow updateShipment;

    public QuickShipEntireOrderFlow(
            OrderPort orders,
            EntityRegistry entities,
            EntityManager entityManager,
            MigrationServiceDispatcher dispatcher,
            SequenceService sequences,
            IssuanceServices issuance,
            UpdateShipmentFlow updateShipment) {
        this.orders = orders;
        this.entities = entities;
        this.entityManager = entityManager;
        this.dispatcher = dispatcher;
        this.sequences = sequences;
        this.issuance = issuance;
        this.updateShipment = updateShipment;
    }

    /**
     * Port of ShipmentServices.groovy:755-805, 895-1051.
     */
    @Transactional
    public Map<String, Object> execute(Map<String, Object> input) {
        String orderId = required(input, "orderId");
        Map<String, Object> order = orders.findOrderHeader(orderId);
        if (order.isEmpty() || value(order.get("productStoreId")) == null) {
            throw new MigrationException(HttpStatus.CONFLICT, "FacilityShipmentMissingProductStore");
        }
        Object store = entities.findByPrimaryKey(entities.entityClass("ProductStore"),
                Map.of("productStoreId", order.get("productStoreId")));
        if (store == null || !"Y".equals(value(entities.get(store, "reserveInventory")))) {
            throw new MigrationException(HttpStatus.CONFLICT, "FacilityShipmentNotCreatedForNotReserveInventory");
        }
        if ("Y".equals(value(entities.get(store, "explodeOrderItems")))) {
            throw new MigrationException(HttpStatus.CONFLICT, "FacilityShipmentNotCreatedForExplodesOrderItems");
        }

        List<Map<String, Object>> reservations = orders.findApprovedReservations(orderId);
        List<Map<String, Object>> associations = orders.findApprovedOrderItemShipGroupAssocs(orderId);
        List<Map<String, Object>> shipGroups = orders.findOrderItemShipGroups(orderId);
        Set<String> facilityIds = new LinkedHashSet<>();
        reservations.stream().map(row -> value(row.get("facilityId")))
                .filter(value -> value != null && (input.get("originFacilityId") == null
                        || value.equals(input.get("originFacilityId").toString())))
                .forEach(facilityIds::add);

        List<Map<String, Object>> shipmentShipGroupFacilityList = new ArrayList<>();
        List<String> shipmentIds = new ArrayList<>();
        List<String> successMessageList = new ArrayList<>();
        List<String> sideEffectNotes = new ArrayList<>();
        for (String facilityId : facilityIds) {
            Object facility = entities.findByPrimaryKey(entities.entityClass("Facility"), Map.of("facilityId", facilityId));
            for (Map<String, Object> shipGroup : shipGroups) {
                String shipGroupSeqId = value(shipGroup.get("shipGroupSeqId"));
                List<Map<String, Object>> groupItems = associations.stream()
                        .filter(row -> shipGroupSeqId.equals(value(row.get("shipGroupSeqId"))))
                        .toList();
                if (groupItems.isEmpty()) {
                    continue;
                }
                Map<String, Object> createShipment = new LinkedHashMap<>();
                createShipment.put("primaryOrderId", orderId);
                createShipment.put("primaryShipGroupSeqId", shipGroupSeqId);
                boolean salesOrder = "SALES_ORDER".equals(value(order.get("orderTypeId")));
                createShipment.put("statusId", salesOrder ? "SHIPMENT_INPUT" : "PURCH_SHIP_CREATED");
                if (salesOrder) {
                    createShipment.put("originFacilityId", facilityId);
                    String partyIdFrom = value(shipGroup.get("vendorPartyId"));
                    if (partyIdFrom == null && facility != null) {
                        partyIdFrom = value(entities.get(facility, "ownerPartyId"));
                    }
                    if (partyIdFrom == null) {
                        partyIdFrom = orderRoleParty(orderId, "SHIP_FROM_VENDOR");
                        if (partyIdFrom == null) {
                            partyIdFrom = orderRoleParty(orderId, "BILL_FROM_VENDOR");
                        }
                    }
                    if (partyIdFrom != null) {
                        createShipment.put("partyIdFrom", partyIdFrom);
                    }
                } else {
                    createShipment.put("destinationFacilityId", facilityId);
                }
                Map<String, Object> shipmentResult = dispatcher.execute("product", "createShipment", createShipment);
                String shipmentId = value(shipmentResult.get("shipmentId"));
                if (shipmentId == null) {
                    throw new MigrationException(HttpStatus.CONFLICT, "Shipment creation returned no shipmentId");
                }

                if (salesOrder) {
                    for (Map<String, Object> reservation : reservations) {
                        if (!facilityId.equals(value(reservation.get("facilityId")))
                                || !shipGroupSeqId.equals(value(reservation.get("shipGroupSeqId")))) {
                            continue;
                        }
                        String itemSeqId = value(reservation.get("orderItemSeqId"));
                        if (orders.findOrderItem(orderId, itemSeqId).isEmpty()) {
                            continue;
                        }
                        Map<String, Object> issueInput = new LinkedHashMap<>();
                        issueInput.put("shipmentId", shipmentId);
                        issueInput.put("orderId", orderId);
                        issueInput.put("orderItemSeqId", itemSeqId);
                        issueInput.put("shipGroupSeqId", shipGroupSeqId);
                        issueInput.put("inventoryItemId", reservation.get("inventoryItemId"));
                        issueInput.put("quantity", reservation.get("quantity"));
                        if (input.get("eventDate") != null) {
                            issueInput.put("eventDate", input.get("eventDate"));
                        }
                        issuance.execute(
                                "issueOrderItemShipGrpInvResToShipment",
                                "issueOrderItemShipGrpInvResToShipment",
                                issueInput);
                    }
                } else {
                    for (Map<String, Object> item : groupItems) {
                        issuance.execute("issueOrderItemToShipment", "issueOrderItemToShipment", Map.of(
                                "shipmentId", shipmentId,
                                "orderId", orderId,
                                "orderItemSeqId", item.get("orderItemSeqId"),
                                "shipGroupSeqId", shipGroupSeqId,
                                "quantity", item.get("quantity")));
                    }
                }
                addIssuedItemsToPackage(shipmentId, orderId, shipGroupSeqId);
                if (salesOrder) {
                    Map<String, Object> packed = new LinkedHashMap<>();
                    packed.put("shipmentId", shipmentId);
                    packed.put("statusId", "SHIPMENT_PACKED");
                    packed.put("eventDate", input.get("eventDate"));
                    Map<String, Object> packedResult = updateShipment.execute(packed);
                    sideEffectNotes.addAll(strings(packedResult.get("sideEffectNotes")));
                    if (!truthy(input.get("setPackedOnly"))) {
                        packed.put("statusId", "SHIPMENT_SHIPPED");
                        Map<String, Object> shippedResult = updateShipment.execute(packed);
                        sideEffectNotes.addAll(strings(shippedResult.get("sideEffectNotes")));
                    }
                } else {
                    Map<String, Object> shipped = new LinkedHashMap<>();
                    shipped.put("shipmentId", shipmentId);
                    shipped.put("statusId", "PURCH_SHIP_SHIPPED");
                    shipped.put("eventDate", input.get("eventDate"));
                    Map<String, Object> shippedResult = updateShipment.execute(shipped);
                    sideEffectNotes.addAll(strings(shippedResult.get("sideEffectNotes")));
                }
                Map<String, Object> info = new LinkedHashMap<>();
                info.put("shipmentId", shipmentId);
                info.put("facilityId", facilityId);
                info.put("shipGroupSeqId", shipGroupSeqId);
                shipmentShipGroupFacilityList.add(info);
                shipmentIds.add(shipmentId);
                successMessageList.add("FacilityShipmentIdCreated " + shipmentId + " "
                        + shipGroupSeqId + " " + facilityId);
            }
        }
        if (shipmentShipGroupFacilityList.isEmpty()) {
            throw new MigrationException(HttpStatus.CONFLICT, "FacilityShipmentNotCreated");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("shipmentShipGroupFacilityList", shipmentShipGroupFacilityList);
        result.put("successMessageList", successMessageList);
        result.put("shipmentIds", shipmentIds);
        result.put("sideEffectNotes", sideEffectNotes);
        return result;
    }

    private void addIssuedItemsToPackage(String shipmentId, String orderId, String shipGroupSeqId) {
        List<?> issuances = entities.findByEquality(entities.entityClass("ItemIssuance"), Map.of(
                "shipmentId", shipmentId, "orderId", orderId, "shipGroupSeqId", shipGroupSeqId));
        if (issuances.isEmpty()) {
            return;
        }
        String packageSeqId = sequences.nextSubSequence(
                "ShipmentPackage", Map.of("shipmentId", shipmentId), "shipmentPackageSeqId");
        Object shipmentPackage = entities.newEntity(entities.entityClass("ShipmentPackage"));
        entities.set(shipmentPackage, "shipmentId", shipmentId);
        entities.set(shipmentPackage, "shipmentPackageSeqId", packageSeqId);
        entities.set(shipmentPackage, "dateCreated", new Timestamp(System.currentTimeMillis()));
        entityManager.persist(shipmentPackage);
        for (Object issuanceRow : issuances) {
            dispatcher.execute("product", "createShipmentPackageContent", Map.of(
                    "shipmentId", shipmentId,
                    "shipmentPackageSeqId", packageSeqId,
                    "shipmentItemSeqId", entities.get(issuanceRow, "shipmentItemSeqId"),
                    "quantity", entities.get(issuanceRow, "quantity")));
        }
    }

    private String orderRoleParty(String orderId, String roleTypeId) {
        return orders.findOrderRoles(orderId).stream()
                .filter(row -> roleTypeId.equals(value(row.get("roleTypeId"))))
                .map(row -> value(row.get("partyId")))
                .findFirst()
                .orElse(null);
    }

    private static List<String> strings(Object value) {
        if (value instanceof List<?> values) {
            return values.stream().map(Object::toString).toList();
        }
        return List.of();
    }

    private static boolean truthy(Object value) {
        return value != null && !value.toString().isEmpty();
    }

    private static String required(Map<String, Object> input, String name) {
        String value = value(input.get(name));
        if (value == null || value.isBlank()) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Missing required input: " + name);
        }
        return value;
    }

    private static String value(Object value) {
        return value == null ? null : value.toString();
    }
}
