/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.ofbiz.migration.product.simple;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.product.adapter.ProductUnportedPort;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** Hand-written port of applications/product/minilang/shipment/picklist/PicklistServices.xml. */
@Component
public class PicklistServices extends UnportedSimpleMethodFile {
    private final EntityManager entityManager;
    private final EntityRegistry entities;
    private final ObjectProvider<MigrationServiceDispatcher> dispatcher;
    private final InventoryServices inventoryServices;
    private final InventoryReserveServices inventoryReserveServices;

    /**
     * Creates the Picklist simple-method adapter.
     * @param unportedPort the fallback for methods not ported by this class
     * @param entityManager the JPA persistence context
     * @param entities the generated datamodel registry
     * @param dispatcher the lazily resolved service dispatcher
     * @param inventoryServices the sibling InventoryServices simple-method file
     * @param inventoryReserveServices the sibling InventoryReserveServices simple-method file
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml
     */
    public PicklistServices(
            ProductUnportedPort unportedPort,
            EntityManager entityManager,
            EntityRegistry entities,
            ObjectProvider<MigrationServiceDispatcher> dispatcher,
            InventoryServices inventoryServices,
            InventoryReserveServices inventoryReserveServices) {
        super(unportedPort);
        this.entityManager = entityManager;
        this.entities = entities;
        this.dispatcher = dispatcher;
        this.inventoryServices = inventoryServices;
        this.inventoryReserveServices = inventoryReserveServices;
    }

    /**
     * Dispatches the nine Picklist simple methods ported from XML lines 23-1321.
     * @param serviceName the registered service name
     * @param methodName the simple-method invoke name
     * @param input the validated service parameters
     * @return the service result fields
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:23-1321
     */
    @Override
    public Map<String, Object> execute(String serviceName, String methodName, Map<String, Object> input) {
        Map<String, Object> parameters = input == null ? new LinkedHashMap<>() : input;
        return switch (methodName) {
            case "findOrdersToPickMove" -> findOrdersToPickMove(parameters);
            case "createPicklistFromOrders" -> createPicklistFromOrders(parameters);
            case "printPickSheets" -> printPickSheets(parameters);
            case "editPicklistItem" -> editPicklistItem(parameters);
            case "setPicklistItemToComplete" -> setPicklistItemToComplete(parameters);
            case "getPicklistDisplayInfo" -> getPicklistDisplayInfo(parameters);
            case "getPickAndPackReportInfo" -> getPickAndPackReportInfo(parameters);
            case "checkPicklistBinItemStatuses" -> checkPicklistBinItemStatuses(parameters);
            case "cancelPicklistAndItems" -> cancelPicklistAndItems(parameters);
            default -> super.execute(serviceName, methodName, parameters);
        };
    }

    /**
     * Finds eligible reservations, split into ready-to-pick and stock-move groups.
     * @param parameters the service parameters and optional caller-supplied order list
     * @return pickMoveInfoList and rushOrderInfo
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:23-460
     */
    private Map<String, Object> findOrdersToPickMove(Map<String, Object> parameters) {
        List<Map<String, Object>> orderHeaders;
        if (notEmpty(parameters.get("orderId"))) {
            Map<String, Object> order = first("OrderHeader", eq("orderId", parameters.get("orderId")));
            orderHeaders = order == null ? new ArrayList<>() : new ArrayList<>(List.of(order));
        } else if (notEmpty(parameters.get("orderHeaderList"))) {
            orderHeaders = mapList(parameters.get("orderHeaderList"));
        } else {
            List<Condition> conditions = new ArrayList<>(List.of(
                    eq("orderTypeId", "SALES_ORDER"),
                    eq("statusId", "ORDER_APPROVED")));
            if (notEmpty(parameters.get("isRushOrder"))) {
                conditions.add(eq("isRushOrder", parameters.get("isRushOrder")));
            }
            orderHeaders = select("OrderHeader", conditions, null, List.of("+orderDate"), 0, 0, false);
        }

        Map<String, Object> pickMoveByGroup = new LinkedHashMap<>();
        List<String> groupNames = new ArrayList<>();
        Map<String, Object> rushInfo = new LinkedHashMap<>();
        rushInfo.put("orderReadyToPickInfoList", new ArrayList<>());
        rushInfo.put("orderNeedsStockMoveInfoList", new ArrayList<>());
        List<Map<String, Object>> pickMoveInfoList = new ArrayList<>();
        long numberSoFar = 0;
        Long maximum = longValue(parameters.get("maxNumberOfOrders"));
        String groupName = null;
        String groupName1 = null;
        String groupName2 = null;
        String groupName3 = null;

        for (Map<String, Object> orderHeader : orderHeaders) {
            List<Map<String, Object>> shipGroups = select(
                    "OrderItemShipGroup", List.of(eq("orderId", orderHeader.get("orderId"))),
                    null, List.of("+shipGroupSeqId"), 0, 0, false);
            long orderItemCount = count("OrderItem", List.of(eq("orderId", orderHeader.get("orderId"))), null);
            Set<String> locations = new LinkedHashSet<>();
            String noOfOrderItems = null;
            boolean grouped = notEmpty(parameters.get("groupByShippingMethod"))
                    || notEmpty(parameters.get("groupByWarehouseArea"))
                    || notEmpty(parameters.get("groupByNoOfOrderItems"));
            if (!grouped) {
                groupName = text(orderHeader.get("orderId"));
            } else {
                List<Map<String, Object>> groupRows = select(
                        "OrderHeaderAndItemFacilityLocation",
                        List.of(eq("orderId", orderHeader.get("orderId"))),
                        List.of(eq("locationTypeEnumId", "FLT_PICKLOC"), isNull("locationTypeEnumId")),
                        List.of(), 0, 0, true);
                for (Map<String, Object> groupRow : groupRows) {
                    if (isY(parameters.get("groupByShippingMethod"))) {
                        groupName1 = text(groupRow.get("shipmentMethodTypeId"));
                    }
                    String locationGroupName = null;
                    if (isY(parameters.get("groupByWarehouseArea"))) {
                        Object areaId = groupRow.get("areaId");
                        groupName2 = notEmpty(areaId) ? text(areaId) : "N/A";
                        locationGroupName = text(areaId);
                    }
                    if (isY(parameters.get("groupByNoOfOrderItems"))) {
                        if (orderItemCount < 3) {
                            noOfOrderItems = "Items less than 3";
                            groupName3 = "Items_Less_Than_3";
                        } else {
                            noOfOrderItems = "Items 3 or more";
                            groupName3 = "Items_Three_Or_More";
                        }
                    }
                    // FlexibleStringExpander converts null group parts to empty strings.
                    groupName = nullToEmpty(groupName1) + nullToEmpty(groupName2) + nullToEmpty(groupName3);
                    if (isY(parameters.get("groupByWarehouseArea")) && notEmpty(locationGroupName)) {
                        locations.add(locationGroupName);
                    }
                }
                if (locations.size() > 1) {
                    groupName2 = "MULTI_LOCATIONS";
                    groupName = nullToEmpty(groupName1) + groupName2 + nullToEmpty(groupName3);
                    // ProductUiLabels.FacilityMultipleLocations is "Multiple Locations" in English.
                    groupName2 = "Multiple Locations";
                }
            }

            for (Map<String, Object> shipGroup : shipGroups) {
                Timestamp shipAfter = timestamp(shipGroup.get("shipAfterDate"));
                if (shipAfter != null && shipAfter.after(Timestamp.from(Instant.now()))) {
                    continue;
                }
                List<Map<String, Object>> reservations = select(
                        "OrderItemShipGrpInvRes",
                        List.of(eq("orderId", shipGroup.get("orderId")),
                                eq("shipGroupSeqId", shipGroup.get("shipGroupSeqId"))),
                        null, List.of(), 0, 0, false);
                List<Map<String, Object>> associations = select(
                        "OrderItemAndShipGroupAssoc",
                        List.of(eq("orderId", shipGroup.get("orderId")),
                                eq("shipGroupSeqId", shipGroup.get("shipGroupSeqId"))),
                        null, List.of("+orderItemSeqId"), 0, 0, false);

                boolean pickThisOrder = true;
                boolean needsStockMove = false;
                boolean allPickStarted = true;
                boolean hasStockToPick = false;
                List<Map<String, Object>> remainingReservations = new ArrayList<>();
                List<Map<String, Object>> reservationInfoList = new ArrayList<>();
                for (Map<String, Object> reservation : reservations) {
                    Map<String, Object> orderItem = relatedOne(
                            "OrderItemShipGrpInvRes", reservation, "OrderItem");
                    if (!"ITEM_APPROVED".equals(orderItem == null ? null : orderItem.get("statusId"))) {
                        pickThisOrder = false;
                    }
                    if (!pickThisOrder) {
                        continue;
                    }
                    Map<String, Object> inventoryItem = relatedOne(
                            "OrderItemShipGrpInvRes", reservation, "InventoryItem");
                    List<Map<String, Object>> alreadyPicked = select(
                            "PicklistAndBinAndItem",
                            List.of(eq("orderId", reservation.get("orderId")),
                                    eq("shipGroupSeqId", reservation.get("shipGroupSeqId")),
                                    eq("orderItemSeqId", reservation.get("orderItemSeqId")),
                                    eq("inventoryItemId", reservation.get("inventoryItemId")),
                                    ne("statusId", "PICKLIST_CANCELLED"),
                                    ne("itemStatusId", "PICKITEM_CANCELLED")),
                            null, List.of(), 0, 0, false);
                    BigDecimal pickedQuantity = alreadyPicked.stream()
                            .map(row -> decimal(row.get("quantity")))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal quantity = decimal(reservation.get("quantity"));
                    BigDecimal remaining = quantity.subtract(pickedQuantity);
                    if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                        continue;
                    }
                    allPickStarted = false;
                    Map<String, Object> remainingReservation = new LinkedHashMap<>(reservation);
                    remainingReservation.put("quantity", remaining);
                    remainingReservations.add(remainingReservation);

                    BigDecimal unavailable = nullableDecimal(reservation.get("quantityNotAvailable"));
                    boolean maySplit = "Y".equals(shipGroup.get("maySplit"));
                    if ((!maySplit && unavailable != null && unavailable.compareTo(BigDecimal.ZERO) > 0)
                            || !Objects.equals(parameters.get("facilityId"),
                                    inventoryItem == null ? null : inventoryItem.get("facilityId"))) {
                        pickThisOrder = false;
                        continue;
                    }
                    if (unavailable == null || unavailable.compareTo(BigDecimal.ZERO) == 0
                            || maySplit && remaining.compareTo(unavailable) > 0) {
                        hasStockToPick = true;
                    }

                    Map<String, Object> location = inventoryItem == null
                            ? null : relatedOne("InventoryItem", inventoryItem, "FacilityLocation");
                    if ("FLT_BULK".equals(location == null ? null : location.get("locationTypeEnumId"))) {
                        needsStockMove = true;
                    }
                    Map<String, Object> reservationInfo = new LinkedHashMap<>();
                    reservationInfo.put("orderItemShipGrpInvRes", remainingReservation);
                    reservationInfo.put("inventoryItem", inventoryItem);
                    reservationInfo.put("facilityLocation", location);
                    reservationInfoList.add(reservationInfo);
                }

                if (!hasStockToPick || allPickStarted) {
                    pickThisOrder = false;
                }
                if (notEmpty(parameters.get("shipmentMethodTypeId"))
                        && !Objects.equals(parameters.get("shipmentMethodTypeId"),
                                shipGroup.get("shipmentMethodTypeId"))) {
                    pickThisOrder = false;
                }
                if (maximum != null && numberSoFar >= maximum) {
                    pickThisOrder = false;
                }
                if (pickThisOrder) {
                    Map<String, Object> readyInfo = new LinkedHashMap<>();
                    readyInfo.put("orderHeader", orderHeader);
                    readyInfo.put("orderItemShipGroup", shipGroup);
                    readyInfo.put("orderItemAndShipGroupAssocList", associations);
                    readyInfo.put("orderItemShipGrpInvResList", remainingReservations);
                    readyInfo.put("orderItemShipGrpInvResInfoList", reservationInfoList);

                    @SuppressWarnings("unchecked")
                    Map<String, Object> group = (Map<String, Object>) pickMoveByGroup.computeIfAbsent(
                            nullToEmpty(groupName), ignored -> {
                                Map<String, Object> newGroup = new LinkedHashMap<>();
                                newGroup.put("orderReadyToPickInfoList", new ArrayList<Map<String, Object>>());
                                newGroup.put("orderNeedsStockMoveInfoList", new ArrayList<Map<String, Object>>());
                                return newGroup;
                            });
                    // The XML literally stores the shipment method under pickMoveInfoMap[group][group].
                    @SuppressWarnings("unchecked")
                    Map<String, Object> nestedGroup = (Map<String, Object>) group.computeIfAbsent(
                            nullToEmpty(groupName), ignored -> {
                                Map<String, Object> nested = new LinkedHashMap<>();
                                Map<String, Object> shipmentMethod = relatedOne(
                                        "OrderItemShipGroup", shipGroup, "ShipmentMethodType");
                                nested.put("shipmentMethodType", shipmentMethod);
                                return nested;
                            });
                    nestedGroup.putIfAbsent("shipmentMethodType",
                            relatedOne("OrderItemShipGroup", shipGroup, "ShipmentMethodType"));
                    group.put("groupName", groupName);
                    group.put("groupName1", groupName1);
                    group.put("groupName2", groupName2);
                    group.put("groupName3", noOfOrderItems);
                    String destination = needsStockMove
                            ? "orderNeedsStockMoveInfoList" : "orderReadyToPickInfoList";
                    addMap(group, destination, readyInfo);
                    if ("Y".equals(orderHeader.get("isRushOrder"))) {
                        addMap(rushInfo, destination, readyInfo);
                    }
                    numberSoFar++;
                }
            }

            // Minilang's empty <set> does not overwrite an existing value with null.
            String currentGroup = nullToEmpty(groupName);
            if (!groupNames.contains(currentGroup)) {
                groupNames.add(currentGroup);
            }
            if (maximum != null && numberSoFar >= maximum) {
                break;
            }
        }

        for (String name : groupNames) {
            Map<String, Object> group = map(pickMoveByGroup.get(name));
            if (!group.isEmpty()) {
                pickMoveInfoList.add(group);
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("pickMoveInfoList", pickMoveInfoList);
        result.put("rushOrderInfo", rushInfo);
        return result;
    }

    /**
     * Builds the per-order rows used to create PicklistBins and PicklistItems.
     * @param pickMoveInfoList the ready-to-pick output from findOrdersToPickMove
     * @param facilityId the requested facility
     * @return assembled order and item rows
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:462-610
     */
    private List<Map<String, Object>> assembleOrderHeaderInfo(
            List<Map<String, Object>> pickMoveInfoList, Object facilityId) {
        List<Map<String, Object>> orderHeaderInfoList = new ArrayList<>();
        for (Map<String, Object> pickMoveInfo : pickMoveInfoList) {
            for (Map<String, Object> ready : mapList(pickMoveInfo.get("orderReadyToPickInfoList"))) {
                List<Map<String, Object>> orderItemInfoList = new ArrayList<>();
                for (Map<String, Object> association : mapList(ready.get("orderItemAndShipGroupAssocList"))) {
                    if (!"ITEM_APPROVED".equals(association.get("statusId"))) {
                        continue;
                    }
                    List<Map<String, Object>> perItem = mapList(ready.get("orderItemShipGrpInvResList")).stream()
                            .filter(reservation -> Objects.equals(
                                    reservation.get("orderItemSeqId"), association.get("orderItemSeqId")))
                            .toList();
                    List<Map<String, Object>> validReservations = new ArrayList<>();
                    BigDecimal reservedQuantity = BigDecimal.ZERO;
                    for (Map<String, Object> reservation : perItem) {
                        reservedQuantity = reservedQuantity.add(decimal(reservation.get("quantity")));
                        Map<String, Object> inventoryItem = relatedOne(
                                "OrderItemShipGrpInvRes", reservation, "InventoryItem");
                        if (inventoryItem != null
                                && Objects.equals(inventoryItem.get("facilityId"), facilityId)) {
                            validReservations.add(reservation);
                        }
                    }
                    if (!validReservations.isEmpty()) {
                        Map<String, Object> itemInfo = new LinkedHashMap<>();
                        itemInfo.put("orderItemAndShipGroupAssoc", association);
                        itemInfo.put("orderItemShipGrpInvResList", validReservations);
                        itemInfo.put("product", relatedOne(
                                "OrderItemAndShipGroupAssoc", association, "Product"));
                        orderItemInfoList.add(itemInfo);
                    }
                    List<Map<String, Object>> issuances = relatedMany(
                            "OrderItemAndShipGroupAssoc", association, "ItemIssuance");
                    BigDecimal issuedQuantity = issuances.stream()
                            .map(issuance -> decimal(issuance.get("quantity")))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal reservedIssued = reservedQuantity.add(issuedQuantity);
                    if (reservedIssued.compareTo(decimal(association.get("quantity"))) != 0) {
                        // The XML records this discrepancy for diagnostics but does not return it.
                    }
                }
                if (!orderItemInfoList.isEmpty()) {
                    Map<String, Object> orderHeaderInfo = new LinkedHashMap<>();
                    orderHeaderInfo.put("orderHeader", ready.get("orderHeader"));
                    orderHeaderInfo.put("orderItemShipGroup", ready.get("orderItemShipGroup"));
                    orderHeaderInfo.put("orderItemInfoList", orderItemInfoList);
                    orderHeaderInfoList.add(orderHeaderInfo);
                }
            }
        }
        return orderHeaderInfoList;
    }

    /**
     * Creates a Picklist and its non-empty bins and items.
     * @param parameters the service parameters
     * @return the generated picklistId when a picklist was created
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:615-715
     */
    private Map<String, Object> createPicklistFromOrders(Map<String, Object> parameters) {
        // orderIdList is not read by the source simple-method.
        Map<String, Object> found = callProductService("findOrdersToPickMove",
                filter(parameters, "facilityId", "shipmentMethodTypeId", "orderId", "orderHeaderList",
                        "isRushOrder", "maxNumberOfOrders", "groupByShippingMethod",
                        "groupByWarehouseArea", "groupByNoOfOrderItems"));
        List<Map<String, Object>> orderInfo = assembleOrderHeaderInfo(
                mapList(found.get("pickMoveInfoList")), parameters.get("facilityId"));
        if (orderInfo.isEmpty()) {
            throw badRequest("No orders ready to pick, not creating picklist.");
        }

        Map<String, Object> createPicklist = new LinkedHashMap<>();
        createPicklist.put("facilityId", parameters.get("facilityId"));
        createPicklist.put("shipmentMethodTypeId", parameters.get("shipmentMethodTypeId"));
        // Entity-auto does not apply the servicedef defaults in this migration dispatcher.
        createPicklist.put("statusId", "PICKLIST_INPUT");
        createPicklist.put("picklistDate", Timestamp.from(Instant.now()));
        Map<String, Object> created = callProductService("createPicklist", createPicklist);
        Object picklistId = created.get("picklistId");

        long binLocationNumber = 1;
        for (Map<String, Object> orderHeaderInfo : orderInfo) {
            Map<String, Object> shipGroup = map(orderHeaderInfo.get("orderItemShipGroup"));
            Map<String, Object> binResult = callProductService("createPicklistBin", values(
                    "picklistId", picklistId,
                    "binLocationNumber", binLocationNumber++,
                    "primaryOrderId", shipGroup.get("orderId"),
                    "primaryShipGroupSeqId", shipGroup.get("shipGroupSeqId")));
            Object picklistBinId = binResult.get("picklistBinId");
            long itemsInBin = 0;
            for (Map<String, Object> itemInfo : mapList(orderHeaderInfo.get("orderItemInfoList"))) {
                for (Map<String, Object> reservation :
                        mapList(itemInfo.get("orderItemShipGrpInvResList"))) {
                    BigDecimal quantity = decimal(reservation.get("quantity"));
                    Object unavailableValue = reservation.get("quantityNotAvailable");
                    if (unavailableValue != null && decimal(unavailableValue).compareTo(BigDecimal.ZERO) > 0) {
                        quantity = quantity.subtract(decimal(unavailableValue));
                    }
                    if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
                        continue;
                    }
                    Map<String, Object> createItem = filter(reservation, "orderId", "shipGroupSeqId",
                            "orderItemSeqId", "inventoryItemId");
                    createItem.put("picklistBinId", picklistBinId);
                    createItem.put("quantity", quantity);
                    // createPicklistItem's default-value override is not applied by entity-auto.
                    createItem.put("itemStatusId", "PICKITEM_PENDING");
                    callProductService("createPicklistItem", createItem);
                    itemsInBin++;
                }
            }
            if (itemsInBin == 0) {
                remove("PicklistBin", values("picklistBinId", picklistBinId));
            }
        }
        return values("picklistId", picklistId);
    }

    /**
     * Finds eligible orders and records a pick-sheet timestamp up to the requested limit.
     * @param parameters the service parameters
     * @return the grouped pick-move details
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:716-762
     */
    private Map<String, Object> printPickSheets(Map<String, Object> parameters) {
        Object maximum = parameters.get("maxNumberOfOrdersToPrint");
        if (!notEmpty(maximum)) {
            throw badRequest("The number of orders must not be empty. Please re-enter to print pick sheet.");
        }
        if (decimal(maximum).compareTo(BigDecimal.ZERO) <= 0) {
            throw badRequest("The number of orders must be greater then zero. Please re-enter to print pick sheet.");
        }
        Map<String, Object> found = callProductService("findOrdersToPickMove",
                filter(parameters, "facilityId", "shipmentMethodTypeId", "orderId", "orderHeaderList",
                        "isRushOrder", "maxNumberOfOrders", "groupByShippingMethod",
                        "groupByWarehouseArea", "groupByNoOfOrderItems"));
        List<Map<String, Object>> pickMoveInfoList = mapList(found.get("pickMoveInfoList"));
        List<Map<String, Object>> toPrint = new ArrayList<>();
        if (notEmpty(parameters.get("printGroupName"))) {
            for (Map<String, Object> group : pickMoveInfoList) {
                if (Objects.equals(group.get("groupName"), parameters.get("printGroupName"))) {
                    toPrint.addAll(mapList(group.get("orderReadyToPickInfoList")));
                }
            }
        } else {
            pickMoveInfoList.forEach(group -> toPrint.addAll(mapList(group.get("orderReadyToPickInfoList"))));
        }
        Timestamp now = Timestamp.from(Instant.now());
        long counter = 0;
        long limit = longValue(maximum);
        for (Map<String, Object> item : toPrint) {
            if (counter >= limit) {
                continue;
            }
            Map<String, Object> orderHeader = map(item.get("orderHeader"));
            Map<String, Object> updated = new LinkedHashMap<>(orderHeader);
            updated.put("pickSheetPrintedDate", now);
            // updateOrderHeader is entity-auto in the order component, so preserve its write inline.
            store("OrderHeader", updated);
            counter++;
        }
        return values("pickMoveInfoList", pickMoveInfoList);
    }

    /**
     * Replaces the reservation and matching PicklistItems for a user-selected lot.
     * @param parameters the picklist item, lot, quantity, and facility parameters
     * @return an empty result on success
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:768-963
     */
    private Map<String, Object> editPicklistItem(Map<String, Object> parameters) {
        Map<String, Object> picklistItem = first("PicklistItem", pk("PicklistItem", parameters));
        List<String> errors = new ArrayList<>();
        BigDecimal quantity = decimal(parameters.get("quantity"));
        BigDecimal oldQuantity = picklistItem == null ? null : nullableDecimal(picklistItem.get("quantity"));
        // type=Integer in the source converts BigDecimal values through ObjectType's Integer converter.
        if (oldQuantity != null && quantity.intValue() > oldQuantity.intValue()) {
            errors.add("The quantity entered (" + parameters.get("quantity")
                    + ") is higher than the quantity expected (" + picklistItem.get("quantity") + ").");
        }
        List<Map<String, Object>> lotItems = select(
                "InventoryItem", List.of(eq("lotId", parameters.get("lotId"))), null, List.of(), 0, 0, false);
        boolean linked = lotItems.stream().anyMatch(item -> Objects.equals(
                item.get("productId"), parameters.get("productId")));
        if (!linked) {
            errors.add("There is no link between the lot " + parameters.get("lotId")
                    + " and the product " + parameters.get("productId") + ".");
        }
        checkErrors(errors);

        Map<String, Object> inventoryInput = filter(parameters, "productId", "facilityId", "lotId");
        Map<String, Object> availability = inventoryServices.execute(
                "getInventoryAvailableByFacility", "getProductInventoryAvailable", inventoryInput);
        Object available = availability.get("availableToPromiseTotal");
        if (quantity.intValue() > decimal(available).intValue()) {
            throw badRequest("Not enough stock for this lot.");
        }

        Map<String, Object> oldReservation = first("OrderItemShipGrpInvRes", values(
                "orderId", parameters.get("orderId"),
                "shipGroupSeqId", parameters.get("shipGroupSeqId"),
                "orderItemSeqId", parameters.get("orderItemSeqId"),
                "inventoryItemId", parameters.get("inventoryItemId")));
        if (picklistItem == null) {
            // The source dereferences the missing value at remove-value after its validations and availability call.
            throw notFound("PicklistItem not found");
        }
        remove("PicklistItem", picklistItem);
        Map<String, Object> cancelInput = filter(parameters, "orderId", "shipGroupSeqId",
                "orderItemSeqId", "inventoryItemId");
        inventoryReserveServices.execute(
                "cancelOrderItemShipGrpInvRes", "cancelOrderItemShipGrpInvRes", cancelInput);

        Map<String, Object> reserveInput = values(
                "orderId", parameters.get("orderId"),
                "shipGroupSeqId", parameters.get("shipGroupSeqId"),
                "orderItemSeqId", parameters.get("orderItemSeqId"),
                "productId", parameters.get("productId"),
                "quantity", parameters.get("quantity"),
                "facilityId", parameters.get("facilityId"),
                "requireInventory", "Y",
                "lotId", parameters.get("lotId"),
                "reserveOrderEnumId", oldReservation == null ? null : oldReservation.get("reserveOrderEnumId"));
        inventoryReserveServices.execute(
                "reserveProductInventoryByFacility", "reserveProductInventory", reserveInput);
        if (oldQuantity != null && quantity.intValue() < oldQuantity.intValue()) {
            Map<String, Object> remainderInput = values(
                    "orderId", parameters.get("orderId"),
                    "shipGroupSeqId", parameters.get("shipGroupSeqId"),
                    "orderItemSeqId", parameters.get("orderItemSeqId"),
                    "productId", parameters.get("productId"),
                    "quantity", oldQuantity.subtract(quantity),
                    "facilityId", parameters.get("facilityId"),
                    "requireInventory", "Y",
                    "lotId", parameters.get("oldLotId"),
                    "reserveOrderEnumId", oldReservation == null ? null : oldReservation.get("reserveOrderEnumId"));
            inventoryReserveServices.execute(
                    "reserveProductInventoryByFacility", "reserveProductInventory", remainderInput);
        }

        Object picklistBinId = picklistItem.get("picklistBinId");
        List<Map<String, Object>> reservations = select(
                "OrderItemShipGrpInvRes",
                List.of(eq("orderId", parameters.get("orderId")),
                        eq("shipGroupSeqId", parameters.get("shipGroupSeqId")),
                        eq("orderItemSeqId", parameters.get("orderItemSeqId"))),
                null, List.of(), 0, 0, false);
        List<Map<String, Object>> existingItems = select(
                "PicklistItem",
                List.of(eq("orderId", parameters.get("orderId")),
                        eq("shipGroupSeqId", parameters.get("shipGroupSeqId")),
                        eq("orderItemSeqId", parameters.get("orderItemSeqId")),
                        eq("picklistBinId", picklistBinId)),
                null, List.of(), 0, 0, false);
        for (Map<String, Object> reservation : reservations) {
            BigDecimal itemQuantity = decimal(reservation.get("quantity"));
            if (reservation.get("quantityNotAvailable") != null) {
                itemQuantity = itemQuantity.subtract(decimal(reservation.get("quantityNotAvailable")));
            }
            String action = "new";
            for (Map<String, Object> existing : existingItems) {
                if (Objects.equals(existing.get("inventoryItemId"), reservation.get("inventoryItemId"))) {
                    if (decimal(existing.get("quantity")).compareTo(itemQuantity) == 0) {
                        action = "none";
                    } else {
                        action = "edit";
                        // The source sets currentPli to the literal string "pli"; it is never read.
                    }
                }
            }
            if ("none".equals(action)) {
                continue;
            }
            Map<String, Object> itemInput = values(
                    "orderId", reservation.get("orderId"),
                    "shipGroupSeqId", reservation.get("shipGroupSeqId"),
                    "orderItemSeqId", reservation.get("orderItemSeqId"),
                    "inventoryItemId", reservation.get("inventoryItemId"),
                    "picklistBinId", picklistBinId,
                    "quantity", itemQuantity);
            if ("new".equals(action)) {
                itemInput.put("itemStatusId", "PICKITEM_PENDING");
                callProductService("createPicklistItem", itemInput);
            } else {
                callProductService("updatePicklistItem", itemInput);
            }
        }
        return new LinkedHashMap<>();
    }

    /**
     * Sets one PicklistItem's status to completed using updatePicklistItem.
     * @param parameters the PicklistItem primary-key fields
     * @return an empty result on success
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:964-972
     */
    private Map<String, Object> setPicklistItemToComplete(Map<String, Object> parameters) {
        Map<String, Object> serviceInput = filter(parameters, "picklistBinId", "orderId",
                "shipGroupSeqId", "orderItemSeqId", "inventoryItemId");
        serviceInput.put("itemStatusId", "PICKITEM_COMPLETED");
        callProductService("updatePicklistItem", serviceInput);
        return new LinkedHashMap<>();
    }

    /**
     * Lists non-final Picklists with their related detail and the source paging fields.
     * @param parameters the facility and optional zero-based view index and page size
     * @return picklistInfoList, count, and paging metadata
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:973-1014
     */
    private Map<String, Object> getPicklistDisplayInfo(Map<String, Object> parameters) {
        List<Condition> conditions = List.of(
                eq("facilityId", parameters.get("facilityId")),
                ne("statusId", "PICKLIST_PICKED"),
                ne("statusId", "PICKLIST_CANCELLED"));
        long count = count("Picklist", conditions, null);
        Integer viewIndex = integerValue(parameters.get("viewIndex"));
        Integer viewSize = integerValue(parameters.get("viewSize"));
        // LimitView parses both optional values as integers; absent values fail in the source engine.
        if (viewIndex == null || viewSize == null) {
            throw badRequest("The limit-view view-index and view-size values must be valid integers.");
        }
        if (viewIndex < 0 || viewSize < 0) {
            throw badRequest("The limit-view view-index and view-size values must be non-negative.");
        }
        int offset = viewIndex * viewSize;
        List<Map<String, Object>> picklists = viewSize == 0 ? List.of() : select(
                "Picklist", conditions, null, List.of("+picklistDate"), offset, viewSize, false);
        List<Map<String, Object>> picklistInfoList = new ArrayList<>();
        for (Map<String, Object> picklist : picklists) {
            picklistInfoList.add(getPicklistSingleInfoInline(picklist));
        }
        int lowIndex = viewIndex * viewSize + 1;
        int highIndex = (viewIndex + 1) * viewSize;
        if (highIndex > count) {
            highIndex = (int) count;
        }
        if (viewSize > count) {
            highIndex = (int) count;
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("picklistInfoList", picklistInfoList);
        result.put("viewIndex", viewIndex);
        result.put("viewSize", viewSize);
        result.put("lowIndex", lowIndex);
        result.put("highIndex", highIndex);
        result.put("picklistCount", count);
        return result;
    }

    /**
     * Builds pick-sheet report rows grouped by facility location and by product without a location.
     * @param parameters the requested picklistId
     * @return the inline picklist detail and the two report collections
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:1015-1174
     */
    private Map<String, Object> getPickAndPackReportInfo(Map<String, Object> parameters) {
        Map<String, Object> picklist = first("Picklist", values("picklistId", parameters.get("picklistId")));
        if (picklist == null) {
            // The source errors when dereferencing a missing Picklist value.
            throw notFound("Picklist not found");
        }
        Map<String, Object> picklistInfo = getPicklistSingleInfoInline(picklist);
        Map<String, Object> binsById = new LinkedHashMap<>();
        Map<String, Map<String, Object>> locationsById = new LinkedHashMap<>();
        Map<String, List<Map<String, Object>>> itemsByLocation = new LinkedHashMap<>();
        Map<String, Map<String, Object>> productsById = new LinkedHashMap<>();
        Map<String, List<Map<String, Object>>> itemsByProduct = new LinkedHashMap<>();

        for (Map<String, Object> binInfo : mapList(picklistInfo.get("picklistBinInfoList"))) {
            Map<String, Object> bin = map(binInfo.get("picklistBin"));
            String binId = text(bin.get("picklistBinId"));
            binsById.put(binId, bin);
            for (Map<String, Object> itemInfo : mapList(binInfo.get("picklistItemInfoList"))) {
                Map<String, Object> inventoryLocation = map(itemInfo.get("inventoryItemAndLocation"));
                Object locationSeqId = inventoryLocation.get("locationSeqId");
                if (notEmpty(locationSeqId)) {
                    Map<String, Object> facilityLocation = new LinkedHashMap<>(inventoryLocation);
                    locationsById.put(text(locationSeqId), facilityLocation);
                    itemsByLocation.computeIfAbsent(text(locationSeqId), ignored -> new ArrayList<>())
                            .add(itemInfo);
                } else {
                    Map<String, Object> orderItem = map(itemInfo.get("orderItem"));
                    if (notEmpty(orderItem.get("productId"))) {
                        Object productIdValue = inventoryLocation.get("productId");
                        String productId = text(productIdValue);
                        Map<String, Object> product = productIdValue == null
                                ? null : first("Product", eq("productId", productIdValue));
                        productsById.put(productId, product);
                        itemsByProduct.computeIfAbsent(productId, ignored -> new ArrayList<>()).add(itemInfo);
                    }
                }
            }
        }

        List<Map<String, Object>> facilityLocationInfoList = new ArrayList<>();
        List<Map<String, Object>> sortedLocations = new ArrayList<>(locationsById.values());
        sortedLocations.sort(Comparator.comparing(
                        (Map<String, Object> row) -> text(row.get("areaId")), Comparator.nullsFirst(String::compareTo))
                .thenComparing(row -> text(row.get("aisleId")), Comparator.nullsFirst(String::compareTo))
                .thenComparing(row -> text(row.get("sectionId")), Comparator.nullsFirst(String::compareTo))
                .thenComparing(row -> text(row.get("levelId")), Comparator.nullsFirst(String::compareTo))
                .thenComparing(row -> text(row.get("positionId")), Comparator.nullsFirst(String::compareTo)));
        for (Map<String, Object> location : sortedLocations) {
            String locationSeqId = text(location.get("locationSeqId"));
            List<Map<String, Object>> itemInfos = itemsByLocation.getOrDefault(locationSeqId, List.of());
            BigDecimal pickQuantity = BigDecimal.ZERO;
            Map<String, Object> product = new LinkedHashMap<>();
            Map<String, BigDecimal> byBin = new LinkedHashMap<>();
            String message = null;
            for (Map<String, Object> itemInfo : itemInfos) {
                Map<String, Object> item = map(itemInfo.get("picklistItem"));
                pickQuantity = pickQuantity.add(decimal(item.get("quantity")));
                Map<String, Object> itemProduct = map(itemInfo.get("product"));
                if (product.isEmpty()) {
                    product = itemProduct;
                } else if (!Objects.equals(product.get("productId"), itemProduct.get("productId"))) {
                    String warning = " WARNING: products with different IDs are sharing the same bin location, "
                            + "you must check products and quantities required against orders : "
                            + product.get("productId") + " and " + itemProduct.get("productId");
                    message = message == null ? warning : message + warning;
                }
                String binId = text(map(itemInfo.get("picklistBin")).get("picklistBinId"));
                byBin.merge(binId, decimal(item.get("quantity")), BigDecimal::add);
            }
            Map<String, Object> locationInfo = values(
                    "facilityLocation", location,
                    "product", product,
                    "pickQuantity", pickQuantity.setScale(2, java.math.RoundingMode.HALF_EVEN),
                    "picklistItemInfoList", itemInfos);
            if (message != null) {
                locationInfo.put("message", message);
            }
            locationInfo.put("picklistBinInfoList", binInfos(byBin, binsById));
            facilityLocationInfoList.add(locationInfo);
        }

        List<Map<String, Object>> noLocationProductInfoList = new ArrayList<>();
        for (String productId : productsById.keySet().stream()
                .sorted(Comparator.nullsFirst(String::compareTo)).toList()) {
            List<Map<String, Object>> itemInfos = itemsByProduct.getOrDefault(productId, List.of());
            BigDecimal pickQuantity = BigDecimal.ZERO;
            Map<String, BigDecimal> byBin = new LinkedHashMap<>();
            for (Map<String, Object> itemInfo : itemInfos) {
                Map<String, Object> item = map(itemInfo.get("picklistItem"));
                BigDecimal quantity = decimal(item.get("quantity"));
                pickQuantity = pickQuantity.add(quantity);
                byBin.merge(text(map(itemInfo.get("picklistBin")).get("picklistBinId")),
                        quantity, BigDecimal::add);
            }
            noLocationProductInfoList.add(values(
                    "product", productsById.get(productId),
                    "pickQuantity", pickQuantity.setScale(2, java.math.RoundingMode.HALF_EVEN),
                    "picklistItemInfoList", itemInfos,
                    "picklistBinInfoList", binInfos(byBin, binsById)));
        }
        return values(
                "picklistInfo", picklistInfo,
                "facilityLocationInfoList", facilityLocationInfoList,
                "noLocationProductInfoList", noLocationProductInfoList);
    }

    /**
     * Assembles the complete nested Picklist view shared by display and pack-report services.
     * @param picklist the mapped Picklist entity
     * @return picklist information with related statuses, roles, bins, and items
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:1175-1252
     */
    private Map<String, Object> getPicklistSingleInfoInline(Map<String, Object> picklist) {
        if (picklist == null) {
            // The source errors when dereferencing a missing Picklist value.
            throw notFound("Picklist not found");
        }
        List<Map<String, Object>> roleInfoList = new ArrayList<>();
        for (Map<String, Object> role : relatedMany("Picklist", picklist, "PicklistRole")) {
            roleInfoList.add(values(
                    "picklistRole", role,
                    "partyNameView", relatedOne("PicklistRole", role, "PartyNameView"),
                    "roleType", relatedOne("PicklistRole", role, "RoleType")));
        }
        List<Map<String, Object>> statusInfoList = new ArrayList<>();
        for (Map<String, Object> status : relatedMany("Picklist", picklist, "PicklistStatus")) {
            statusInfoList.add(values(
                    "picklistStatusHistory", status,
                    "statusItem", relatedOne("PicklistStatus", status, "StatusItem"),
                    "statusItemTo", relatedOne("PicklistStatus", status, "ToStatusItem")));
        }

        List<Map<String, Object>> bins = relatedMany("Picklist", picklist, "PicklistBin");
        bins.sort(Comparator.comparing(row -> longValue(row.get("binLocationNumber")),
                Comparator.nullsFirst(Long::compareTo)));
        List<Map<String, Object>> binInfoList = new ArrayList<>();
        for (Map<String, Object> bin : bins) {
            Map<String, Object> orderHeader = relatedOne("PicklistBin", bin, "PrimaryOrderHeader");
            Map<String, Object> itemInfos = new LinkedHashMap<>();
            List<Map<String, Object>> picklistItemInfoList = new ArrayList<>();
            for (Map<String, Object> item : relatedMany("PicklistBin", bin, "PicklistItem")) {
                Map<String, Object> orderItem = relatedOne("PicklistItem", item, "OrderItem");
                Map<String, Object> inventoryLocation = relatedOne(
                        "PicklistItem", item, "InventoryItemAndLocation");
                Map<String, Object> product = orderItem == null ? null
                        : relatedOne("OrderItem", orderItem, "Product");
                List<Map<String, Object>> issuances = relatedMany("PicklistItem", item, "ItemIssuance");
                picklistItemInfoList.add(values(
                        "picklistItem", item,
                        "picklistBin", bin,
                        "orderItem", orderItem,
                        "product", product,
                        "inventoryItemAndLocation", inventoryLocation,
                        "orderItemShipGrpInvRes",
                                relatedOne("PicklistItem", item, "OrderItemShipGrpInvRes"),
                        "itemIssuanceList", issuances));
            }
            itemInfos.put("picklistItemInfoList", picklistItemInfoList);
            binInfoList.add(values(
                    "picklistBin", bin,
                    "primaryOrderHeader", orderHeader,
                    "primaryOrderItemShipGroup", relatedOne(
                            "PicklistBin", bin, "PrimaryOrderItemShipGroup"),
                    "productStore", orderHeader == null ? null
                            : relatedOne("OrderHeader", orderHeader, "ProductStore"),
                    "picklistItemInfoList", itemInfos.get("picklistItemInfoList")));
        }
        return values(
                "picklist", picklist,
                "picklistRoleInfoList", roleInfoList,
                "picklistStatusHistoryInfoList", statusInfoList,
                "picklistBinInfoList", binInfoList,
                "statusItem", relatedOne("Picklist", picklist, "StatusItem"),
                "facility", relatedOne("Picklist", picklist, "Facility"),
                "shipmentMethodType", relatedOne("Picklist", picklist, "ShipmentMethodType"),
                "statusValidChangeToDetailList",
                        relatedMany("Picklist", picklist, "StatusValidChangeToDetail"));
    }

    /**
     * Updates Picklist status from the statuses of all items in the bin's Picklist.
     * @param parameters the PicklistBin primary-key fields
     * @return an empty result
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:1255-1302
     */
    private Map<String, Object> checkPicklistBinItemStatuses(Map<String, Object> parameters) {
        Map<String, Object> bin = first("PicklistBin", pk("PicklistBin", parameters));
        if (bin == null) {
            // The source errors when dereferencing a missing PicklistBin value.
            throw notFound("PicklistBin not found");
        }
        Map<String, Object> picklist = relatedOne("PicklistBin", bin, "Picklist");
        if (picklist == null) {
            // The source errors when dereferencing a missing Picklist value.
            throw notFound("Picklist not found");
        }
        List<Map<String, Object>> items = select(
                "PicklistItemAndBin", List.of(eq("picklistId", picklist.get("picklistId"))),
                null, List.of(), 0, 0, false);
        boolean allCompleteOrCancelled = true;
        boolean allCancelled = true;
        for (Map<String, Object> item : items) {
            if (!"PICKITEM_CANCELLED".equals(item.get("itemStatusId"))) {
                allCancelled = false;
                if (!"PICKITEM_COMPLETED".equals(item.get("itemStatusId"))) {
                    allCompleteOrCancelled = false;
                }
            }
        }
        if (allCancelled) {
            picklist.put("statusId", "PICKLIST_CANCELLED");
            store("Picklist", picklist);
        } else if (allCompleteOrCancelled) {
            picklist.put("statusId", "PICKLIST_PICKED");
            store("Picklist", picklist);
        }
        return new LinkedHashMap<>();
    }

    /**
     * Marks every PicklistItem in each bin of a Picklist as cancelled.
     * @param parameters the picklistId
     * @return an empty result
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:1304-1321
     */
    private Map<String, Object> cancelPicklistAndItems(Map<String, Object> parameters) {
        List<Map<String, Object>> bins = select(
                "PicklistBin", List.of(eq("picklistId", parameters.get("picklistId"))),
                null, List.of(), 0, 0, false);
        for (Map<String, Object> bin : bins) {
            for (Map<String, Object> item : select(
                    "PicklistItem", List.of(eq("picklistBinId", bin.get("picklistBinId"))),
                    null, List.of(), 0, 0, false)) {
                item.put("itemStatusId", "PICKITEM_CANCELLED");
                store("PicklistItem", item);
            }
        }
        return new LinkedHashMap<>();
    }

    /**
     * Returns the bin summary rows ordered by bin location number.
     * @param quantities summed item quantities keyed by picklistBinId
     * @param binsById picklist bins indexed by picklistBinId
     * @return PicklistBinInfo maps
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:1086-1128,1139-1168
     */
    private List<Map<String, Object>> binInfos(
            Map<String, BigDecimal> quantities, Map<String, Object> binsById) {
        List<Map<String, Object>> result = new ArrayList<>();
        quantities.forEach((binId, quantity) -> result.add(values(
                "picklistBin", binsById.get(binId),
                "quantity", quantity.setScale(2, java.math.RoundingMode.HALF_EVEN))));
        result.sort(Comparator.comparing(row -> longValue(
                        map(row.get("picklistBin")).get("binLocationNumber")),
                Comparator.nullsFirst(Long::compareTo)));
        return result;
    }

    /**
     * Calls a product service with only its declared input fields and removes the synthetic response field.
     * @param name the service name
     * @param input the candidate service arguments
     * @return the service outputs
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:618-715,732-760,840-958,965-967
     */
    private Map<String, Object> callProductService(String name, Map<String, ?> input) {
        Set<String> allowed = switch (name) {
            case "findOrdersToPickMove" -> Set.of("facilityId", "shipmentMethodTypeId", "orderId",
                    "orderHeaderList", "isRushOrder", "maxNumberOfOrders", "groupByShippingMethod",
                    "groupByWarehouseArea", "groupByNoOfOrderItems");
            case "createPicklist" -> Set.of("facilityId", "shipmentMethodTypeId", "statusId", "picklistDate");
            case "createPicklistBin" -> Set.of("picklistId", "binLocationNumber",
                    "primaryOrderId", "primaryShipGroupSeqId");
            case "createPicklistItem" -> Set.of("picklistBinId", "orderId", "shipGroupSeqId",
                    "orderItemSeqId", "inventoryItemId", "quantity", "itemStatusId");
            case "updatePicklistItem" -> Set.of("picklistBinId", "orderId", "shipGroupSeqId",
                    "orderItemSeqId", "inventoryItemId", "quantity", "itemStatusId");
            default -> Set.of();
        };
        Map<String, Object> filtered = new LinkedHashMap<>();
        input.forEach((key, value) -> {
            if (allowed.contains(key) && value != null) {
                filtered.put(key, value);
            }
        });
        Map<String, Object> result = dispatcher.getObject().execute("product", name, filtered);
        result.remove("responseMessage");
        return result;
    }

    /**
     * Fetches a JPA entity list using unbounded Criteria queries and OFBiz-style conditions.
     * @param entityName the generated entity or view name
     * @param conditions conditions combined with AND
     * @param anyConditions optional OR group
     * @param orderBy signed field names
     * @param offset first row to return
     * @param limit maximum rows, or zero for unbounded
     * @param distinct whether to select distinct rows
     * @return entity maps
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:73-131,218-258,285-350,976-1001,1023-1251
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private List<Map<String, Object>> select(
            String entityName,
            List<Condition> conditions,
            List<Condition> anyConditions,
            List<String> orderBy,
            int offset,
            int limit,
            boolean distinct) {
        boolean generatedEntity = entityManager.getMetamodel().getEntities().stream()
                .anyMatch(entity -> entity.getJavaType().getSimpleName().equals(entityName));
        if (!generatedEntity) {
            return selectView(entityName, conditions, anyConditions, orderBy, offset, limit, distinct);
        }
        Class<?> type = entities.entityClass(entityName);
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<?> query = builder.createQuery(type);
        Root root = query.from(type);
        query.select(root).distinct(distinct);
        List<Predicate> predicates = predicates(builder, root, conditions);
        if (anyConditions != null && !anyConditions.isEmpty()) {
            predicates.add(builder.or(predicates(builder, root, anyConditions).toArray(Predicate[]::new)));
        }
        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(Predicate[]::new));
        }
        if (orderBy != null && !orderBy.isEmpty()) {
            List<Order> orders = new ArrayList<>();
            for (String order : orderBy) {
                boolean ascending = !order.startsWith("-");
                String field = order.startsWith("+") || order.startsWith("-") ? order.substring(1) : order;
                orders.add(ascending ? builder.asc(root.get(field)) : builder.desc(root.get(field)));
            }
            query.orderBy(orders);
        }
        TypedQuery typedQuery = entityManager.createQuery(query);
        if (offset > 0) {
            typedQuery.setFirstResult(offset);
        }
        if (limit > 0) {
            typedQuery.setMaxResults(limit);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object entity : (List<?>) typedQuery.getResultList()) {
            result.add(entities.toMap(entity));
        }
        return result;
    }

    /**
     * Reproduces view entities omitted from the generated JPA metamodel by joining their declared members.
     * @param entityName the OFBiz view entity
     * @param conditions conditions combined with AND
     * @param anyConditions optional OR group
     * @param orderBy signed field names
     * @param offset first row to return
     * @param limit maximum rows, or zero for unbounded
     * @param distinct whether to select distinct rows
     * @return mapped view rows
     * @see applications/order/entitydef/entitymodel_view.xml,applications/datamodel/entitydef/shipment-entitymodel.xml,applications/datamodel/entitydef/product-entitymodel.xml,applications/datamodel/entitydef/party-entitymodel.xml,framework/common/entitydef/entitymodel.xml
     */
    private List<Map<String, Object>> selectView(
            String entityName,
            List<Condition> conditions,
            List<Condition> anyConditions,
            List<String> orderBy,
            int offset,
            int limit,
            boolean distinct) {
        ViewPlan plan = switch (entityName) {
            case "OrderHeaderAndItemFacilityLocation" -> new ViewPlan(
                    "OrderHeader OH join OrderItemShipGrpInvRes R on R.orderId = OH.orderId"
                            + " join OrderItemShipGroup SG on SG.orderId = R.orderId"
                            + " and SG.shipGroupSeqId = R.shipGroupSeqId"
                            + " join InventoryItem II on II.inventoryItemId = R.inventoryItemId"
                            + " left join FacilityLocation FL on FL.facilityId = II.facilityId"
                            + " and FL.locationSeqId = II.locationSeqId",
                    List.of("OH", "R", "SG", "II", "FL"));
            case "OrderItemAndShipGroupAssoc" -> new ViewPlan(
                    "OrderItem OI join OrderItemShipGroupAssoc OISGA on OISGA.orderId = OI.orderId"
                            + " and OISGA.orderItemSeqId = OI.orderItemSeqId",
                    List.of("OI", "OISGA"));
            case "PicklistAndBinAndItem" -> new ViewPlan(
                    "Picklist PL join PicklistBin PB on PB.picklistId = PL.picklistId"
                            + " join PicklistItem PLI on PLI.picklistBinId = PB.picklistBinId",
                    List.of("PL", "PB", "PLI"));
            case "PicklistItemAndBin" -> new ViewPlan(
                    "PicklistBin PB join PicklistItem PLI on PLI.picklistBinId = PB.picklistBinId",
                    List.of("PB", "PLI"));
            case "InventoryItemAndLocation" -> new ViewPlan(
                    "InventoryItem II left join Product PR on PR.productId = II.productId"
                            + " left join FacilityLocation FL on FL.facilityId = II.facilityId"
                            + " and FL.locationSeqId = II.locationSeqId",
                    List.of("II", "PR", "FL"));
            case "PartyNameView" -> new ViewPlan(
                    "Party PTY left join Person PER on PER.partyId = PTY.partyId"
                            + " left join PartyGroup PTYGRP on PTYGRP.partyId = PTY.partyId",
                    List.of("PTY", "PER", "PTYGRP"));
            case "StatusValidChangeToDetail" -> new ViewPlan(
                    "StatusValidChange SVC join StatusItem SI on SI.statusId = SVC.statusIdTo",
                    List.of("SVC", "SI"));
            default -> {
                entities.entityClass(entityName);
                yield new ViewPlan("", List.of());
            }
        };
        List<String> ordering = orderBy == null ? List.of() : orderBy;
        if (ordering.isEmpty() && "StatusValidChangeToDetail".equals(entityName)) {
            ordering = List.of("+sequenceId");
        }
        boolean projectedDistinct = distinct && "OrderHeaderAndItemFacilityLocation".equals(entityName);
        StringBuilder hql = new StringBuilder("select ");
        if (distinct) {
            hql.append("distinct ");
        }
        if (projectedDistinct) {
            hql.append("SG.shipmentMethodTypeId, FL.areaId");
        } else {
            hql.append(String.join(", ", plan.selections()));
        }
        hql.append(" from ").append(plan.fromClause());
        Map<String, Object> boundValues = new LinkedHashMap<>();
        List<String> where = new ArrayList<>();
        appendViewConditions(entityName, conditions, where, boundValues);
        if (anyConditions != null && !anyConditions.isEmpty()) {
            List<String> any = new ArrayList<>();
            appendViewConditions(entityName, anyConditions, any, boundValues);
            where.add("(" + String.join(" or ", any) + ")");
        }
        if (!where.isEmpty()) {
            hql.append(" where ").append(String.join(" and ", where));
        }
        if (!ordering.isEmpty()) {
            List<String> orderExpressions = new ArrayList<>();
            for (String order : ordering) {
                boolean ascending = !order.startsWith("-");
                String field = order.startsWith("+") || order.startsWith("-") ? order.substring(1) : order;
                orderExpressions.add(viewFieldPath(entityName, field) + (ascending ? " asc" : " desc"));
            }
            hql.append(" order by ").append(String.join(", ", orderExpressions));
        }
        TypedQuery<Object[]> query = entityManager.createQuery(hql.toString(), Object[].class);
        boundValues.forEach(query::setParameter);
        if (offset > 0) {
            query.setFirstResult(offset);
        }
        if (limit > 0) {
            query.setMaxResults(limit);
        }
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Object[] members : query.getResultList()) {
            rows.add(projectedDistinct
                    ? values("shipmentMethodTypeId", members[0], "areaId", members[1])
                    : mapViewRow(entityName, members));
        }
        return rows;
    }

    private void appendViewConditions(
            String entityName, List<Condition> conditions, List<String> expressions,
            Map<String, Object> boundValues) {
        if (conditions == null) {
            return;
        }
        for (Condition condition : conditions) {
            String path = viewFieldPath(entityName, condition.field());
            Object value = condition.value();
            if (condition.operator().equals("is-null") || value == null && condition.operator().equals("=")) {
                expressions.add(path + " is null");
            } else if (condition.operator().equals("is-not-null")
                    || value == null && condition.operator().equals("!=")) {
                expressions.add(path + " is not null");
            } else {
                String parameter = "viewParam" + boundValues.size();
                String operator = switch (condition.operator()) {
                    case "!=" -> "<>";
                    case ">", "<", ">=" -> condition.operator();
                    default -> "=";
                };
                expressions.add(path + " " + operator + " :" + parameter);
                boundValues.put(parameter, value);
            }
        }
    }

    private String viewFieldPath(String entityName, String field) {
        return switch (entityName) {
            case "OrderHeaderAndItemFacilityLocation" -> switch (field) {
                case "orderId" -> "OH.orderId";
                case "orderItemSeqId", "inventoryItemId" -> "R." + field;
                case "shipGroupSeqId", "shipmentMethodTypeId", "carrierPartyId" -> "SG." + field;
                case "productId", "facilityId", "locationSeqId" -> "II." + field;
                case "locationTypeEnumId", "areaId" -> "FL." + field;
                default -> throw new IllegalArgumentException("Unknown view field: " + entityName + "." + field);
            };
            case "OrderItemAndShipGroupAssoc" -> switch (field) {
                case "shipGroupSeqId", "quantity" -> "OISGA." + field;
                case "orderItemQuantity" -> "OI.quantity";
                case "orderItemCancelQuantity" -> "OI.cancelQuantity";
                default -> "OI." + field;
            };
            case "PicklistAndBinAndItem" -> switch (field) {
                case "picklistId", "statusId" -> "PL." + field;
                case "picklistBinId" -> "PB." + field;
                case "orderId", "shipGroupSeqId", "orderItemSeqId", "inventoryItemId",
                        "itemStatusId", "quantity" -> "PLI." + field;
                default -> throw new IllegalArgumentException("Unknown view field: " + entityName + "." + field);
            };
            case "PicklistItemAndBin" -> switch (field) {
                case "picklistId" -> "PB." + field;
                case "picklistBinId" -> "PB." + field;
                default -> "PLI." + field;
            };
            case "InventoryItemAndLocation" -> switch (field) {
                case "areaId", "aisleId", "sectionId", "levelId", "positionId",
                        "locationTypeEnumId" -> "FL." + field;
                default -> "II." + field;
            };
            case "PartyNameView" -> switch (field) {
                case "firstName", "middleName", "lastName", "firstNameLocal", "lastNameLocal",
                        "personalTitle", "suffix" -> "PER." + field;
                case "groupName", "groupNameLocal" -> "PTYGRP." + field;
                default -> "PTY." + field;
            };
            case "StatusValidChangeToDetail" -> switch (field) {
                case "statusId", "statusIdTo" -> "SVC." + field;
                default -> "SI." + field;
            };
            default -> throw new IllegalArgumentException("Unknown view entity: " + entityName);
        };
    }

    private Map<String, Object> mapViewRow(String entityName, Object[] members) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Object member : members) {
            rows.add(member == null ? new LinkedHashMap<>() : entities.toMap(member));
        }
        return switch (entityName) {
            case "OrderHeaderAndItemFacilityLocation" -> values(
                    "orderId", rows.get(0).get("orderId"),
                    "orderItemSeqId", rows.get(1).get("orderItemSeqId"),
                    "inventoryItemId", rows.get(1).get("inventoryItemId"),
                    "shipGroupSeqId", rows.get(2).get("shipGroupSeqId"),
                    "shipmentMethodTypeId", rows.get(2).get("shipmentMethodTypeId"),
                    "carrierPartyId", rows.get(2).get("carrierPartyId"),
                    "productId", rows.get(3).get("productId"),
                    "facilityId", rows.get(3).get("facilityId"),
                    "locationSeqId", rows.get(3).get("locationSeqId"),
                    "locationTypeEnumId", rows.get(4).get("locationTypeEnumId"),
                    "areaId", rows.get(4).get("areaId"));
            case "OrderItemAndShipGroupAssoc" -> {
                Map<String, Object> row = new LinkedHashMap<>(rows.get(0));
                row.remove("quantity");
                row.remove("cancelQuantity");
                row.putAll(rows.get(1));
                row.put("orderItemQuantity", rows.get(0).get("quantity"));
                row.put("orderItemCancelQuantity", rows.get(0).get("cancelQuantity"));
                yield row;
            }
            case "PicklistAndBinAndItem" -> {
                Map<String, Object> row = new LinkedHashMap<>(rows.get(0));
                row.putAll(rows.get(1));
                row.putAll(rows.get(2));
                yield row;
            }
            case "PicklistItemAndBin" -> {
                Map<String, Object> row = new LinkedHashMap<>(rows.get(0));
                row.putAll(rows.get(1));
                yield row;
            }
            case "InventoryItemAndLocation" -> {
                Map<String, Object> row = new LinkedHashMap<>(rows.get(0));
                row.remove("comments");
                rows.get(1).forEach((field, value) -> {
                    if (!"facilityId".equals(field) && !"inventoryItemTypeId".equals(field)) {
                        row.put(field, value);
                    }
                });
                row.putAll(rows.get(2));
                yield row;
            }
            case "PartyNameView" -> values(
                    "partyId", rows.get(0).get("partyId"),
                    "partyTypeId", rows.get(0).get("partyTypeId"),
                    "description", rows.get(0).get("description"),
                    "statusId", rows.get(0).get("statusId"),
                    "firstName", rows.get(1).get("firstName"),
                    "middleName", rows.get(1).get("middleName"),
                    "lastName", rows.get(1).get("lastName"),
                    "firstNameLocal", rows.get(1).get("firstNameLocal"),
                    "lastNameLocal", rows.get(1).get("lastNameLocal"),
                    "personalTitle", rows.get(1).get("personalTitle"),
                    "suffix", rows.get(1).get("suffix"),
                    "groupName", rows.get(2).get("groupName"),
                    "groupNameLocal", rows.get(2).get("groupNameLocal"));
            case "StatusValidChangeToDetail" -> {
                Map<String, Object> row = new LinkedHashMap<>(rows.get(0));
                row.putAll(rows.get(1));
                row.put("statusId", rows.get(0).get("statusId"));
                yield row;
            }
            default -> throw new IllegalArgumentException("Unknown view entity: " + entityName);
        };
    }

    /**
     * Counts all matching rows without applying the former 100-row EntityRegistry limit.
     * @param entityName the generated entity name
     * @param conditions conditions combined with AND
     * @param anyConditions optional OR group
     * @return the complete count
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:98-100,976-1001
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private long count(String entityName, List<Condition> conditions, List<Condition> anyConditions) {
        Class<?> type = entities.entityClass(entityName);
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root root = query.from(type);
        query.select(builder.count(root));
        List<Predicate> predicates = predicates(builder, root, conditions);
        if (anyConditions != null && !anyConditions.isEmpty()) {
            predicates.add(builder.or(predicates(builder, root, anyConditions).toArray(Predicate[]::new)));
        }
        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(Predicate[]::new));
        }
        return entityManager.createQuery(query).getSingleResult();
    }

    /**
     * Converts a condition list into Criteria predicates, including null and not-equals comparisons.
     * @param builder the criteria builder
     * @param root the entity root
     * @param conditions the conditions to combine with AND
     * @return criteria predicates
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:120-126,249-257,976-1001
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private List<Predicate> predicates(CriteriaBuilder builder, Root root, List<Condition> conditions) {
        List<Predicate> result = new ArrayList<>();
        if (conditions == null) {
            return result;
        }
        for (Condition condition : conditions) {
            Path path = root.get(condition.field());
            Object value = convertValue(condition.value(), path.getJavaType());
            switch (condition.operator()) {
                case "is-null" -> result.add(builder.isNull(path));
                case "is-not-null" -> result.add(builder.isNotNull(path));
                case "!=" -> result.add(value == null
                        ? builder.isNotNull(path) : builder.notEqual(path, value));
                case ">" -> result.add(builder.greaterThan((Expression) path, (Comparable) value));
                case "<" -> result.add(builder.lessThan((Expression) path, (Comparable) value));
                case ">=" -> result.add(builder.greaterThanOrEqualTo((Expression) path, (Comparable) value));
                default -> result.add(value == null ? builder.isNull(path) : builder.equal(path, value));
            }
        }
        return result;
    }

    /**
     * Queries an entity and returns its first mapped row.
     * @param entityName the generated entity name
     * @param conditions conditions combined with AND
     * @return the first matching row or null
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:67-69,772,823-833,1023-1025,1257-1269
     */
    private Map<String, Object> first(String entityName, Condition... conditions) {
        List<Map<String, Object>> rows = select(
                entityName, List.of(conditions), null, List.of(), 0, 1, false);
        return rows.isEmpty() ? null : rows.get(0);
    }

    /**
     * Finds the first row matching the supplied key fields.
     * @param entityName the entity name
     * @param keyFields the entity key fields
     * @return the row or null
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:814-858,895-931
     */
    private Map<String, Object> first(String entityName, Map<String, ?> keyFields) {
        List<Condition> conditions = keyFields.entrySet().stream()
                .map(entry -> eq(entry.getKey(), entry.getValue()))
                .toList();
        return first(entityName, conditions.toArray(Condition[]::new));
    }

    /**
     * Retrieves a related entity using the key maps declared in the OFBiz entity model.
     * @param entityName the source entity
     * @param source the source entity map
     * @param relationName the relation title or target entity name
     * @return the related row or null
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:234-240,325,515,1196-1251
     */
    private Map<String, Object> relatedOne(String entityName, Map<String, Object> source, String relationName) {
        Relation relation = relation(entityName, relationName);
        if (relation == null || source == null) {
            return null;
        }
        List<Condition> conditions = new ArrayList<>();
        for (Map.Entry<String, String> keyMap : relation.keys().entrySet()) {
            Object value = source.get(keyMap.getKey());
            if (value == null) {
                return null;
            }
            conditions.add(eq(keyMap.getValue(), value));
        }
        return first(relation.target(), conditions.toArray(Condition[]::new));
    }

    /**
     * Retrieves all rows for a declared OFBiz relation, retaining source relation key maps.
     * @param entityName the source entity
     * @param source the source entity map
     * @param relationName the relation title or target entity name
     * @return related rows
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:543,1193-1251,1305-1316
     */
    private List<Map<String, Object>> relatedMany(
            String entityName, Map<String, Object> source, String relationName) {
        Relation relation = relation(entityName, relationName);
        if (relation == null || source == null) {
            return new ArrayList<>();
        }
        List<Condition> conditions = new ArrayList<>();
        for (Map.Entry<String, String> keyMap : relation.keys().entrySet()) {
            Object value = source.get(keyMap.getKey());
            if (value == null) {
                return new ArrayList<>();
            }
            conditions.add(eq(keyMap.getValue(), value));
        }
        return select(relation.target(), conditions, null, List.of(), 0, 0, false);
    }

    /**
     * Maps the Picklist relations actually used by this minilang file to their model key maps.
     * @param sourceEntity the source entity name
     * @param relationName the relation title or related entity name
     * @return the target entity and source-to-target field mappings
     * @see applications/datamodel/entitydef/shipment-entitymodel.xml,applications/datamodel/entitydef/product-entitymodel.xml
     */
    private Relation relation(String sourceEntity, String relationName) {
        String key = sourceEntity + "." + relationName;
        return switch (key) {
            case "Picklist.StatusItem" -> relation("StatusItem", "statusId", "statusId");
            case "Picklist.Facility" -> relation("Facility", "facilityId", "facilityId");
            case "Picklist.ShipmentMethodType" -> relation("ShipmentMethodType", "shipmentMethodTypeId", "shipmentMethodTypeId");
            case "Picklist.PicklistRole" -> relation("PicklistRole", "picklistId", "picklistId");
            case "Picklist.PicklistStatus" -> relation("PicklistStatus", "picklistId", "picklistId");
            case "Picklist.PicklistBin" -> relation("PicklistBin", "picklistId", "picklistId");
            case "Picklist.StatusValidChangeToDetail" ->
                    relation("StatusValidChangeToDetail", "statusId", "statusId");
            case "PicklistBin.Picklist" -> relation("Picklist", "picklistId", "picklistId");
            case "PicklistBin.PrimaryOrderHeader" -> relation("OrderHeader", "primaryOrderId", "orderId");
            case "PicklistBin.PrimaryOrderItemShipGroup" ->
                    relation("OrderItemShipGroup", "primaryOrderId", "orderId",
                            "primaryShipGroupSeqId", "shipGroupSeqId");
            case "PicklistBin.PicklistItem" -> relation("PicklistItem", "picklistBinId", "picklistBinId");
            case "PicklistRole.PartyNameView" -> relation("PartyNameView", "partyId", "partyId");
            case "PicklistRole.RoleType" -> relation("RoleType", "roleTypeId", "roleTypeId");
            case "PicklistStatus.StatusItem" -> relation("StatusItem", "statusId", "statusId");
            case "PicklistStatus.ToStatusItem" -> relation("StatusItem", "statusIdTo", "statusId");
            case "OrderHeader.ProductStore" -> relation("ProductStore", "productStoreId", "productStoreId");
            case "OrderItem.Product" -> relation("Product", "productId", "productId");
            case "OrderItemShipGrpInvRes.OrderItem" ->
                    relation("OrderItem", "orderId", "orderId", "orderItemSeqId", "orderItemSeqId");
            case "OrderItemShipGrpInvRes.InventoryItem" ->
                    relation("InventoryItem", "inventoryItemId", "inventoryItemId");
            case "OrderItemShipGrpInvRes.FacilityLocation" ->
                    relation("FacilityLocation", "facilityId", "facilityId",
                            "locationSeqId", "locationSeqId");
            case "OrderItemShipGrpInvRes.OrderItemShipGrpInvRes" ->
                    relation("OrderItemShipGrpInvRes", "orderId", "orderId",
                            "shipGroupSeqId", "shipGroupSeqId",
                            "orderItemSeqId", "orderItemSeqId",
                            "inventoryItemId", "inventoryItemId");
            case "OrderItemShipGroup.ShipmentMethodType" ->
                    relation("ShipmentMethodType", "shipmentMethodTypeId", "shipmentMethodTypeId");
            case "OrderItemAndShipGroupAssoc.Product" ->
                    relation("Product", "productId", "productId");
            case "OrderItemAndShipGroupAssoc.ItemIssuance" ->
                    relation("ItemIssuance", "orderId", "orderId",
                            "orderItemSeqId", "orderItemSeqId",
                            "shipGroupSeqId", "shipGroupSeqId");
            case "InventoryItem.FacilityLocation" ->
                    relation("FacilityLocation", "facilityId", "facilityId", "locationSeqId", "locationSeqId");
            case "PicklistItem.OrderItem" ->
                    relation("OrderItem", "orderId", "orderId", "orderItemSeqId", "orderItemSeqId");
            case "PicklistItem.InventoryItemAndLocation" ->
                    relation("InventoryItemAndLocation", "inventoryItemId", "inventoryItemId");
            case "PicklistItem.OrderItemShipGrpInvRes" ->
                    relation("OrderItemShipGrpInvRes", "orderId", "orderId",
                            "shipGroupSeqId", "shipGroupSeqId",
                            "orderItemSeqId", "orderItemSeqId",
                            "inventoryItemId", "inventoryItemId");
            case "PicklistItem.ItemIssuance" ->
                    relation("ItemIssuance", "orderId", "orderId",
                            "orderItemSeqId", "orderItemSeqId",
                            "shipGroupSeqId", "shipGroupSeqId",
                            "inventoryItemId", "inventoryItemId");
            default -> null;
        };
    }

    /**
     * Builds a relation tuple from alternating source and target field names.
     * @param target the related entity
     * @param fieldPairs alternating source and target fields
     * @return a relation descriptor
     * @see applications/datamodel/entitydef/shipment-entitymodel.xml,applications/datamodel/entitydef/product-entitymodel.xml
     */
    private Relation relation(String target, String... fieldPairs) {
        Map<String, String> keys = new LinkedHashMap<>();
        for (int i = 0; i < fieldPairs.length; i += 2) {
            keys.put(fieldPairs[i], fieldPairs[i + 1]);
        }
        return new Relation(target, keys);
    }

    /**
     * Merges a mapped entity's values by its primary key.
     * @param entityName the generated entity name
     * @param values the source map values
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:855-958,1260-1298,1310-1316
     */
    private void store(String entityName, Map<String, Object> values) {
        Class<?> type = entities.entityClass(entityName);
        Map<String, Object> primaryKey = pk(entityName, values);
        Object entity = entities.findByPrimaryKey(type, primaryKey);
        if (entity == null) {
            return;
        }
        values.forEach((field, value) -> entities.set(entity, field, value));
        entityManager.merge(entity);
    }

    /**
     * Removes a mapped entity by primary key.
     * @param entityName the generated entity name
     * @param values the row containing its primary key
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:835-840
     */
    private void remove(String entityName, Map<String, Object> values) {
        Object entity = entities.findByPrimaryKey(entities.entityClass(entityName), pk(entityName, values));
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    /**
     * Creates a primary-key map from the generated entity model.
     * @param entityName the generated entity name
     * @param values values that may contain the primary-key fields
     * @return the primary-key map
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:770-772,1257-1263
     */
    private Map<String, Object> pk(String entityName, Map<String, ?> values) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (String key : entities.primaryKeys(entities.entityClass(entityName))) {
            if (values.containsKey(key)) {
                result.put(key, values.get(key));
            }
        }
        return result;
    }

    /**
     * Applies source-field conversions for Criteria predicates.
     * @param value the supplied condition value
     * @param targetType the mapped property type
     * @return a value of targetType when conversion is supported
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:120-126,249-257,976-1001
     */
    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null || targetType.isInstance(value)) {
            return value;
        }
        String text = value.toString();
        if (targetType == String.class) {
            return text;
        }
        if (targetType == BigDecimal.class) {
            return new BigDecimal(text);
        }
        if (targetType == Integer.class || targetType == int.class) {
            return new BigDecimal(text).intValue();
        }
        if (targetType == Long.class || targetType == long.class) {
            return new BigDecimal(text).longValue();
        }
        if (targetType == Timestamp.class) {
            return timestamp(value);
        }
        return value;
    }

    /**
     * Builds an equality predicate.
     * @param field the field name
     * @param value the field value
     * @return an equality condition
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:73-126,249-257,976-1001
     */
    private static Condition eq(String field, Object value) {
        return new Condition(field, "=", value);
    }

    /**
     * Builds a not-equals predicate.
     * @param field the field name
     * @param value the field value
     * @return a not-equals condition
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:249-257,978-990
     */
    private static Condition ne(String field, Object value) {
        return new Condition(field, "!=", value);
    }

    /**
     * Builds an IS NULL predicate.
     * @param field the field name
     * @return a null condition
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:120-126
     */
    private static Condition isNull(String field) {
        return new Condition(field, "is-null", null);
    }

    /**
     * Raises one bad-request error after all queued add-error messages are joined.
     * @param errors the accumulated minilang errors
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:702-714,774-802,814-825
     */
    private static void checkErrors(List<String> errors) {
        if (!errors.isEmpty()) {
            throw badRequest(String.join("; ", errors));
        }
    }

    /**
     * Creates the migration's HTTP 400 service exception.
     * @param message the minilang error text
     * @return the service exception
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:702-714,774-825
     */
    private static MigrationException badRequest(String message) {
        return new MigrationException(HttpStatus.BAD_REQUEST, message);
    }

    private static MigrationException notFound(String message) {
        return new MigrationException(HttpStatus.NOT_FOUND, message);
    }

    /**
     * Returns an insertion-ordered map while permitting source-compatible null values.
     * @param keyValues alternating string keys and values
     * @return an ordered map
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:23-1321
     */
    private static Map<String, Object> values(Object... keyValues) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            result.put((String) keyValues[i], keyValues[i + 1]);
        }
        return result;
    }

    /**
     * Filters an input map to a set of callee service fields.
     * @param source the source map
     * @param fieldNames allowed service input fields
     * @return a field-filtered map
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:618-619,684-685,839-840
     */
    private static Map<String, Object> filter(Map<String, ?> source, String... fieldNames) {
        Set<String> allowed = Set.of(fieldNames);
        Map<String, Object> result = new LinkedHashMap<>();
        source.forEach((key, value) -> {
            if (allowed.contains(key) && value != null) {
                result.put(key, value);
            }
        });
        return result;
    }

    /**
     * Adds a map to a list-valued entry, creating that list when absent.
     * @param target the parent map
     * @param key the list field
     * @param value the row to append
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:387-407,1179-1244
     */
    @SuppressWarnings("unchecked")
    private static void addMap(Map<String, Object> target, String key, Map<String, Object> value) {
        ((List<Map<String, Object>>) target.computeIfAbsent(key, ignored -> new ArrayList<>())).add(value);
    }

    /**
     * Converts an untyped object into a map with string keys.
     * @param value the object
     * @return the map or an empty map
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:23-1321
     */
    private static Map<String, Object> map(Object value) {
        if (!(value instanceof Map<?, ?> source)) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> result = new LinkedHashMap<>();
        source.forEach((key, fieldValue) -> result.put(String.valueOf(key), fieldValue));
        return result;
    }

    /**
     * Converts an untyped list to mapped rows.
     * @param value the object
     * @return rows whose elements are maps
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:23-1321
     */
    private static List<Map<String, Object>> mapList(Object value) {
        if (!(value instanceof Collection<?> collection)) {
            return new ArrayList<>();
        }
        return collection.stream().map(PicklistServices::map).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Reads a numeric value as BigDecimal; null and blank values are zero for minilang arithmetic.
     * @param value the source value
     * @return the decimal value
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:261-280,513-555,657-675
     */
    private static BigDecimal decimal(Object value) {
        BigDecimal result = nullableDecimal(value);
        return result == null ? BigDecimal.ZERO : result;
    }

    /**
     * Reads an optional numeric value without replacing null with zero.
     * @param value the source value
     * @return the decimal or null
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:261-280,285-323
     */
    private static BigDecimal nullableDecimal(Object value) {
        if (value == null || value.toString().isBlank()) {
            return null;
        }
        return value instanceof BigDecimal decimal ? decimal : new BigDecimal(value.toString());
    }

    /**
     * Converts input values to integer paging fields.
     * @param value the source value
     * @return the integer or null
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:973-1011
     */
    private static Integer integerValue(Object value) {
        return value == null || value.toString().isBlank() ? null : new BigDecimal(value.toString()).intValue();
    }

    /**
     * Converts input values to long counters.
     * @param value the source value
     * @return the long or null
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:88-89,400-445,751-759
     */
    private static Long longValue(Object value) {
        return value == null || value.toString().isBlank() ? null : new BigDecimal(value.toString()).longValue();
    }

    /**
     * Converts supported source values to SQL timestamps.
     * @param value the source value
     * @return the timestamp or null
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:66,731-760
     */
    private static Timestamp timestamp(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Timestamp timestamp) {
            return timestamp;
        }
        if (value instanceof Instant instant) {
            return Timestamp.from(instant);
        }
        String text = value.toString();
        try {
            return Timestamp.valueOf(text);
        } catch (IllegalArgumentException exception) {
            return Timestamp.from(Instant.parse(text));
        }
    }

    /**
     * Checks whether an OFBiz string value is non-null and non-empty.
     * @param value the source value
     * @return true for non-empty source values
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:67-84,738
     */
    private static boolean notEmpty(Object value) {
        return value != null && (!(value instanceof String text) || !text.isEmpty());
    }

    /**
     * Checks the source's Y/N indicator value.
     * @param value the source value
     * @return true when the value is Y
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:135-168
     */
    private static boolean isY(Object value) {
        return "Y".equals(value);
    }

    /**
     * Converts null map components to empty strings for group names.
     * @param value the group component
     * @return the component or an empty string
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:173-199
     */
    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    /**
     * Converts a non-null object to its string representation.
     * @param value the source
     * @return the string or null
     * @see applications/product/minilang/shipment/picklist/PicklistServices.xml:173-199,1135-1166
     */
    private static String text(Object value) {
        return value == null ? null : value.toString();
    }

    private record Condition(String field, String operator, Object value) {
    }

    private record Relation(String target, Map<String, String> keys) {
    }

    private record ViewPlan(String fromClause, List<String> selections) {
    }
}
