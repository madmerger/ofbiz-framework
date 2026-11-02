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

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ShipmentSettingsServices {
    private final EntityRegistry entities;
    private final ServiceHandlerRegistry registry;

    public ShipmentSettingsServices(EntityRegistry entities, ServiceHandlerRegistry registry) {
        this.entities = entities;
        this.registry = registry;
    }

    @PostConstruct
    void register() {
        registry.registerHandler("product", "setShipmentSettingsFromFacilities", "OFBTOOLS_VIEW", this::fromFacilities);
        registry.registerHandler("product", "setShipmentSettingsFromPrimaryOrder", "OFBTOOLS_VIEW", this::fromOrder);
    }

    public Map<String, Object> fromFacilities(Map<String, Object> input) {
        Object shipment = shipment(input);
        facilityContact(shipment, "originFacilityId", "originContactMechId", List.of("SHIP_ORIG_LOCATION", "PRIMARY_LOCATION"));
        facilityContact(shipment, "originFacilityId", "originTelecomNumberId", List.of("PHONE_SHIP_ORIG", "PRIMARY_PHONE"));
        facilityContact(shipment, "destinationFacilityId", "destinationContactMechId", List.of("SHIPPING_LOCATION", "PRIMARY_LOCATION"));
        facilityContact(shipment, "destinationFacilityId", "destinationTelecomNumberId", List.of("PHONE_SHIPPING", "PRIMARY_PHONE"));
        return Map.of();
    }

    public Map<String, Object> fromOrder(Map<String, Object> input) {
        Object shipment = shipment(input);
        Object orderId = get(shipment, "primaryOrderId");
        if (orderId == null) return Map.of();
        Object order = entities.findByPrimaryKey(entities.entityClass("OrderHeader"), Map.of("orderId", orderId));
        if (order == null) return Map.of();
        party(shipment, "partyIdFrom", orderId, List.of("SHIP_FROM_VENDOR", "VENDOR"));
        party(shipment, "partyIdTo", orderId, List.of("SHIP_TO_CUSTOMER", "CUSTOMER"));
        orderContact(shipment, "destinationContactMechId", orderId, "SHIPPING_LOCATION");
        if (!"PURCHASE_SHIPMENT".equals(get(shipment, "shipmentTypeId"))) orderContact(shipment, "originContactMechId", orderId, "SHIP_ORIG_LOCATION");
        orderContact(shipment, "destinationTelecomNumberId", orderId, "PHONE_SHIPPING");
        orderContact(shipment, "originTelecomNumberId", orderId, "PHONE_SHIP_ORIG");
        if (get(shipment, "destinationTelecomNumberId") == null && get(shipment, "partyIdTo") != null) {
            for (Object contact : find("PartyContactMech", Map.of("partyId", get(shipment, "partyIdTo")))) {
                if (active(contact) && entities.findByPrimaryKey(entities.entityClass("TelecomNumber"),
                        Map.of("contactMechId", get(contact, "contactMechId"))) != null) {
                    set(shipment, "destinationTelecomNumberId", get(contact, "contactMechId"));
                    break;
                }
            }
        }
        if (get(shipment, "primaryShipGroupSeqId") != null && "SALES_ORDER".equals(get(order, "orderTypeId"))) {
            Object group = entities.findByPrimaryKey(entities.entityClass("OrderItemShipGroup"), Map.of("orderId", orderId,
                    "shipGroupSeqId", get(shipment, "primaryShipGroupSeqId")));
            if (group != null) {
                if (get(group, "contactMechId") != null) set(shipment, "destinationContactMechId", get(group, "contactMechId"));
                if (get(group, "telecomContactMechId") != null) set(shipment, "destinationTelecomNumberId", get(group, "telecomContactMechId"));
            }
        }
        if (get(shipment, "estimatedShipCost") == null) {
            BigDecimal amount = BigDecimal.ZERO;
            for (Object adjustment : find("OrderAdjustment", Map.of("orderId", orderId, "orderAdjustmentTypeId", "SHIPPING_CHARGES"))) {
                if (get(adjustment, "amount") != null) amount = amount.add(new BigDecimal(get(adjustment, "amount").toString()));
            }
            set(shipment, "estimatedShipCost", amount);
        }
        return Map.of();
    }

    private void facilityContact(Object shipment, String facilityField, String target, List<String> purposes) {
        if (get(shipment, target) != null || get(shipment, facilityField) == null) return;
        for (String purpose : purposes) {
            for (Object contact : find("FacilityContactMechPurpose", Map.of("facilityId", get(shipment, facilityField), "contactMechPurposeTypeId", purpose))) {
                if (active(contact)) {
                    set(shipment, target, get(contact, "contactMechId"));
                    return;
                }
            }
        }
    }

    private void orderContact(Object shipment, String target, Object orderId, String purpose) {
        if (get(shipment, target) != null) return;
        List<?> contacts = find("OrderContactMech", Map.of("orderId", orderId, "contactMechPurposeTypeId", purpose));
        if (!contacts.isEmpty()) set(shipment, target, get(contacts.get(0), "contactMechId"));
    }

    private void party(Object shipment, String target, Object orderId, List<String> roles) {
        if (get(shipment, target) != null) return;
        for (String role : roles) {
            List<?> matches = find("OrderRole", Map.of("orderId", orderId, "roleTypeId", role));
            if (!matches.isEmpty()) {
                set(shipment, target, get(matches.get(0), "partyId"));
                return;
            }
        }
    }

    private Object shipment(Map<String, Object> input) {
        if (input.get("shipmentId") == null) throw new MigrationException(HttpStatus.BAD_REQUEST, "shipmentId is required");
        Object shipment = entities.findByPrimaryKey(entities.entityClass("Shipment"), Map.of("shipmentId", input.get("shipmentId")));
        if (shipment == null) throw new MigrationException(HttpStatus.NOT_FOUND, "Shipment not found");
        return shipment;
    }

    private boolean active(Object row) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Object from = get(row, "fromDate");
        Object thru = get(row, "thruDate");
        return (from == null || !((Timestamp) from).after(now)) && (thru == null || ((Timestamp) thru).after(now));
    }

    private List<?> find(String name, Map<String, ?> filters) { return entities.findByEquality(entities.entityClass(name), filters); }
    private Object get(Object entity, String field) { return entities.get(entity, field); }
    private void set(Object entity, String field, Object value) { entities.set(entity, field, value); }
}
