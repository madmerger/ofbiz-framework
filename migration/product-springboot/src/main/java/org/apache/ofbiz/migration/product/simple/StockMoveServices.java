/*******************************************************************************
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
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.apache.ofbiz.migration.product.simple;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.product.ProductPermissions;
import org.apache.ofbiz.migration.product.adapter.ProductUnportedPort;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** Port of applications/product/minilang/product/inventory/StockMoveServices.xml:28-634. */
@Component
public class StockMoveServices extends UnportedSimpleMethodFile {
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;
    private static final Pattern FIELD_REFERENCE = Pattern.compile("\\$\\{([^}]+)}");
    private static final List<String> CREATE_INVENTORY_ITEM_FIELDS = List.of(
            "inventoryItemTypeId", "productId", "partyId", "ownerPartyId", "statusId", "datetimeReceived",
            "datetimeManufactured", "expireDate", "facilityId", "containerId", "lotId", "uomId", "binNumber",
            "locationSeqId", "comments", "accountingQuantityTotal", "serialNumber", "softIdentifier",
            "activationNumber", "activationValidThru", "unitCost", "currencyUomId", "fixedAssetId");
    private static final Set<String> INVENTORY_ITEM_VIEW_EXCLUDES = Set.of("comments");
    private static final Set<String> PRODUCT_VIEW_EXCLUDES = Set.of("facilityId", "inventoryItemTypeId");

    private final ProductUnportedPort unportedPort;
    private final EntityRegistry entities;
    private final EntityManager entityManager;
    private final ProductPermissions permissions;
    private final InventoryServices inventoryServices;

    /** Creates the service with its persistence, permission, and delegation dependencies. */
    public StockMoveServices(
            ProductUnportedPort unportedPort,
            EntityRegistry entities,
            EntityManager entityManager,
            ProductPermissions permissions,
            InventoryServices inventoryServices) {
        super(unportedPort);
        this.unportedPort = unportedPort;
        this.entities = entities;
        this.entityManager = entityManager;
        this.permissions = permissions;
        this.inventoryServices = inventoryServices;
    }

    /** Dispatches the supported stock-move simple-methods and delegates other methods. */
    @Override
    public Map<String, Object> execute(String serviceName, String methodName, Map<String, Object> input) {
        return switch (methodName) {
            case "findStockMovesNeeded" -> findStockMovesNeeded(input);
            case "findStockMovesRecommended" -> findStockMovesRecommended(input);
            case "processPhysicalStockMove" -> processPhysicalStockMove(input);
            case "findProductInventorylocations" -> findProductInventorylocations(input);
            default -> super.execute(serviceName, methodName, input);
        };
    }

    /**
     * Port of applications/product/minilang/product/inventory/StockMoveServices.xml:28-191;
     * missing-relation failure follows GetRelated.java:79-86.
     */
    private Map<String, Object> findStockMovesNeeded(Map<String, Object> input) {
        Map<String, Object> output = new LinkedHashMap<>();
        List<Map<String, Object>> reservations = orderReservationLocationRows(
                Map.of("locationTypeEnumId", "FLT_BULK",
                        "orderItemStatusId", "ITEM_APPROVED",
                        "facilityId", input.get("facilityId")),
                null);
        Map<Object, List<Map<String, Object>>> reservationsByLocation = new LinkedHashMap<>();
        for (Map<String, Object> reservation : reservations) {
            addToListMap(reservationsByLocation, reservation.get("locationSeqId"), reservation);
        }

        Map<Object, Object> stockMoveHandled = new LinkedHashMap<>();
        Map<Object, BigDecimal> targetLocationSimpleMoveQuantity = new LinkedHashMap<>();
        List<Map<String, Object>> moveInfos = null;
        List<String> warnings = null;
        List<String> errors = new ArrayList<>();

        for (Map.Entry<Object, List<Map<String, Object>>> locationEntry : reservationsByLocation.entrySet()) {
            Map<Object, List<Map<String, Object>>> reservationsByProduct = new LinkedHashMap<>();
            for (Map<String, Object> reservation : locationEntry.getValue()) {
                addToListMap(reservationsByProduct, reservation.get("productId"), reservation);
            }

            for (Map.Entry<Object, List<Map<String, Object>>> productEntry : reservationsByProduct.entrySet()) {
                Object productId = productEntry.getKey();
                Object facilityId = input.get("facilityId");
                Object fromLocationSeqId = locationEntry.getKey();
                Map<String, Object> moveInfo = new LinkedHashMap<>();
                Object product = findByPrimaryKey("Product", Map.of("productId", productId));
                Object fromLocation = findByPrimaryKey("FacilityLocation",
                        keys("facilityId", facilityId, "locationSeqId", fromLocationSeqId));
                putEntity(moveInfo, "product", product);
                putEntity(moveInfo, "facilityLocationFrom", fromLocation);

                List<Map<String, Object>> pickLocations = productFacilityLocationViewRows(Map.of(
                        "productId", productId,
                        "facilityId", facilityId,
                        "locationTypeEnumId", "FLT_PICKLOC"));
                if (pickLocations.isEmpty()) {
        warnings = add(warnings, expand(
                            "Error in stock move, could not find a pick/primary location for facility "
                                    + "[${parameters.facilityId}] and product [${productId}]",
                            input, Map.of("productId", productId)));
                    continue;
                }

                Map<String, Object> pickLocation = pickLocations.get(0);
                Object targetFacilityLocation = findByPrimaryKey("FacilityLocation",
                        keys("facilityId", pickLocation.get("facilityId"),
                                "locationSeqId", pickLocation.get("locationSeqId")));
                Object targetProductFacilityLocation = findByPrimaryKey("ProductFacilityLocation",
                        keys("productId", pickLocation.get("productId"),
                                "facilityId", pickLocation.get("facilityId"),
                                "locationSeqId", pickLocation.get("locationSeqId")));
                putEntity(moveInfo, "facilityLocationTo", targetFacilityLocation);
                putEntity(moveInfo, "targetProductFacilityLocation", targetProductFacilityLocation);

                BigDecimal totalQuantity = sum(productEntry.getValue(), "quantity");
                moveInfo.put("totalQuantity", totalQuantity);
                List<Object> inventoryItems = findAll("InventoryItem", Map.of(
                        "productId", productId,
                        "facilityId", facilityId,
                        "locationSeqId", fromLocationSeqId), List.of());
                BigDecimal totalQuantityOnHand = sumEntities(inventoryItems, "quantityOnHandTotal");
                BigDecimal totalAvailableToPromise = sumEntities(inventoryItems, "availableToPromiseTotal");
                moveInfo.put("quantityOnHandTotalFrom", totalQuantityOnHand);
                moveInfo.put("availableToPromiseTotalFrom", totalAvailableToPromise);

                if (less(totalQuantityOnHand, totalQuantity, errors)) {
                    Map<String, Object> formatValues = new LinkedHashMap<>();
                    formatValues.put("parameters", input);
                    formatValues.put("productId", productId);
                    formatValues.put("moveInfo", moveInfo);
                    formatValues.put("totalQuantity", totalQuantity);
                    formatValues.put("totalQuantityOnHand", totalQuantityOnHand);
                    warnings = add(warnings, expand(
                            "Warning in stock move: for facility [${parameters.facilityId}] and product "
                                    + "[${productId}] going from location [${productFacilityLocation.locationSeqId}] "
                                    + "to location [${moveInfo.targetProductFacilityLocation.locationSeqId}] a quantity "
                                    + "of [${moveInfo.totalQuantity}] was needed but there are only "
                                    + "[${totalQuantityOnHand}] on hand (this will be in the pick list with the full "
                                    + "quantity on hand, but note that this will not be enough to prepare for all "
                                    + "orders reserved against this location)",
                            formatValues));
                    moveInfo.put("totalQuantity", totalQuantityOnHand);
                } else {
                    // XML 128-185 would retrieve target InventoryItems, recalculate their totals, and
                    // schedule a minimum-stock transfer.
                    Map<String, Object> targetProductFacilityLocationDetails =
                            entityMap(targetProductFacilityLocation);
                    throw new MigrationException(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Exception thrown while finding related values: Could not find relation for "
                                    + "relationName: InventoryItem for value [ProductFacilityLocation productId="
                                    + targetProductFacilityLocationDetails.get("productId") + ", facilityId="
                                    + targetProductFacilityLocationDetails.get("facilityId") + ", locationSeqId="
                                    + targetProductFacilityLocationDetails.get("locationSeqId") + "]");
                }

                if (moveInfos == null) {
                    moveInfos = new ArrayList<>();
                }
                moveInfos.add(moveInfo);
            }
        }
        putIfPresent(output, "moveByOisgirInfoList", moveInfos);
        putIfPresent(output, "stockMoveHandled", stockMoveHandled.isEmpty() ? null : stockMoveHandled);
        putIfPresent(output, "warningMessageList", warnings);
        throwErrors(errors);
        return output;
    }

    /** Port of applications/product/minilang/product/inventory/StockMoveServices.xml:192-341. */
    private Map<String, Object> findStockMovesRecommended(Map<String, Object> input) {
        Map<String, Object> output = new LinkedHashMap<>();
        Map<Object, Object> stockMoveHandled = input.get("stockMoveHandled") instanceof Map<?, ?> supplied
                ? new LinkedHashMap<>(castMap(supplied))
                : null;
        Map<String, Object> productSave = new LinkedHashMap<>();
        productSave.put("productId", "");
        Map<Object, BigDecimal> fromLocationTotalAvailableToPromise = new LinkedHashMap<>();
        List<Map<String, Object>> moveInfos = null;
        List<String> warnings = null;
        List<String> errors = new ArrayList<>();
        BigDecimal totalQuantityOnHand = zero();

        for (Map<String, Object> pickLocation : productFacilityLocationQuantityRows(
                input.get("facilityId"), "FLT_PICKLOC")) {
            Object minimumStockValue = pickLocation.get("minimumStock");
            Object minimumStock = empty(minimumStockValue) ? BigDecimal.ZERO : minimumStockValue;
            Object moveQuantity = pickLocation.get("moveQuantity");
            Object availableValue = pickLocation.get("availableToPromiseTotal");
            boolean belowMinimum = !empty(moveQuantity)
                    && greater(moveQuantity, BigDecimal.ZERO, errors)
                    && (empty(availableValue)
                            ? greater(minimumStock, BigDecimal.ZERO, errors)
                            : less(availableValue, minimumStock, errors));
            if (!belowMinimum) {
                continue;
            }
            Object locationSeqId = pickLocation.get("locationSeqId");
            Object handled = stockMoveHandled == null ? null : stockMoveHandled.get(locationSeqId);
            if (!(empty(handled) || notEqual(handled, "Y"))) {
                continue;
            }

            Object previousProductId = productSave == null ? null : productSave.get("productId");
            if (notEqual(pickLocation.get("productId"), previousProductId)) {
                Object product = findByPrimaryKey("Product", Map.of("productId", pickLocation.get("productId")));
                productSave = product == null ? null : entityMap(product);
                fromLocationTotalAvailableToPromise.clear();
            }
            Object targetFacilityLocation = findByPrimaryKey("FacilityLocation",
                    keys("facilityId", pickLocation.get("facilityId"), "locationSeqId", locationSeqId));
            List<Map<String, Object>> bulkItems = inventoryItemAndLocationRows(Map.of(
                    "productId", pickLocation.get("productId"),
                    "facilityId", pickLocation.get("facilityId"),
                    "locationTypeEnumId", "FLT_BULK"));
            if (bulkItems.isEmpty()) {
                Map<String, Object> values = new LinkedHashMap<>();
                values.put("productFacilityLocationQuantityTest", pickLocation);
                warnings = add(warnings, expand(
                        "Error in stock move, could not find a bulk location for facility "
                                + "[${productFacilityLocationQuantityTest.facilityId}] and product "
                                + "[${productFacilityLocationQuantityTest.productId}]",
                        values));
                continue;
            }

            Map<Object, List<Map<String, Object>>> bulkItemsByLocation = new LinkedHashMap<>();
            for (Map<String, Object> item : bulkItems) {
                addToListMap(bulkItemsByLocation, item.get("locationSeqId"), item);
            }
            Object targetLocationMoveQuantity = moveQuantity;
            for (Map.Entry<Object, List<Map<String, Object>>> bulkEntry : bulkItemsByLocation.entrySet()) {
                Object bulkLocationSeqId = bulkEntry.getKey();
                BigDecimal totalAvailableToPromise;
                BigDecimal previousAvailable = fromLocationTotalAvailableToPromise.get(bulkLocationSeqId);
                if (empty(previousAvailable)) {
                    totalQuantityOnHand = sum(bulkEntry.getValue(), "quantityOnHandTotal");
                    totalAvailableToPromise = sum(bulkEntry.getValue(), "availableToPromiseTotal");
                } else {
                    totalAvailableToPromise = previousAvailable;
                }

                if (!greater(totalAvailableToPromise, BigDecimal.ZERO, errors)
                        || !greater(targetLocationMoveQuantity, BigDecimal.ZERO, errors)) {
                    continue;
                }

                Map<String, Object> moveInfo = new LinkedHashMap<>();
                moveInfo.put("product", productSave);
                putEntity(moveInfo, "facilityLocationTo", targetFacilityLocation);
                Map<String, Object> firstBulkItem = bulkEntry.getValue().get(0);
                Object sourceFacilityLocation = findByPrimaryKey("FacilityLocation",
                        keys("facilityId", firstBulkItem.get("facilityId"),
                                "locationSeqId", firstBulkItem.get("locationSeqId")));
                putEntity(moveInfo, "facilityLocationFrom", sourceFacilityLocation);
                Object targetProductFacilityLocation = findByPrimaryKey("ProductFacilityLocation",
                        keys("productId", pickLocation.get("productId"),
                                "facilityId", pickLocation.get("facilityId"),
                                "locationSeqId", locationSeqId));
                putEntity(moveInfo, "targetProductFacilityLocation", targetProductFacilityLocation);
                moveInfo.put("availableToPromiseTotalTo", pickLocation.get("availableToPromiseTotal"));
                moveInfo.put("quantityOnHandTotalTo", pickLocation.get("quantityOnHandTotal"));
                moveInfo.put("availableToPromiseTotalFrom", totalAvailableToPromise);
                moveInfo.put("quantityOnHandTotalFrom", totalQuantityOnHand);

                if (less(totalAvailableToPromise, targetLocationMoveQuantity, errors)) {
                    targetLocationMoveQuantity = subtract(
                            calcValue(targetLocationMoveQuantity), totalAvailableToPromise);
                    moveInfo.put("totalQuantity", totalAvailableToPromise);
                    fromLocationTotalAvailableToPromise.put(bulkLocationSeqId, zero());
                } else {
                    moveInfo.put("totalQuantity", targetLocationMoveQuantity);
                    fromLocationTotalAvailableToPromise.put(
                            bulkLocationSeqId, subtract(
                                    totalAvailableToPromise, calcValue(targetLocationMoveQuantity)));
                    targetLocationMoveQuantity = zero();
                }

                if (moveInfos == null) {
                    moveInfos = new ArrayList<>();
                }
                moveInfos.add(moveInfo);
            }
        }
        putIfPresent(output, "moveByPflInfoList", moveInfos);
        putIfPresent(output, "warningMessageList", warnings);
        throwErrors(errors);
        return output;
    }

    /**
     * Port of applications/product/minilang/product/inventory/StockMoveServices.xml:343-425.
     * The non-OISGIR loop at XML:362-418 does not decrement quantityLeftToProcess: each source item
     * with positive ATP independently moves min(quantityLeftToProcess, ATP), then the unchanged
     * positive value triggers the insufficient-inventory warning.
     */
    private Map<String, Object> processPhysicalStockMove(Map<String, Object> input) {
        permissions.require("FACILITY", "CREATE");
        permissions.require("FACILITY", "UPDATE");
        Object quantityMoved = input.get("quantityMoved");
        Map<String, Object> state = new LinkedHashMap<>();
        state.put("parameters", input);
        state.put("quantityLeftToProcess", quantityMoved);
        state.put("warningMessageList", input.get("warningMessageList"));
        state.put("errors", new ArrayList<String>());
        state.put("orderItemStatusId", "ITEM_APPROVED");
        processOisgirMoveByStatusInline(state);
        state.put("orderItemStatusId", "ITEM_CREATED");
        processOisgirMoveByStatusInline(state);

        if (greater(state.get("quantityLeftToProcess"), BigDecimal.ZERO, errors(state))) {
            List<Object> inventoryItems = findAll("InventoryItem", Map.of(
                    "productId", input.get("productId"),
                    "facilityId", input.get("facilityId"),
                    "locationSeqId", input.get("locationSeqId")), List.of("datetimeReceived"));
            for (Object inventoryItem : inventoryItems) {
                if (!greater(state.get("quantityLeftToProcess"), BigDecimal.ZERO, errors(state))) {
                    break;
                }
                Object availableToPromise = entities.get(inventoryItem, "availableToPromiseTotal");
                if (!greater(availableToPromise, BigDecimal.ZERO, errors(state))) {
                    continue;
                }
                Object quantityLeft = state.get("quantityLeftToProcess");
                Object currentQuantityToMove = greater(quantityLeft, availableToPromise, errors(state))
                        ? availableToPromise
                        : quantityLeft;
                Object targetInventoryItem = copyEntity("InventoryItem", inventoryItem);
                entities.set(targetInventoryItem, "locationSeqId", input.get("targetLocationSeqId"));
                Map<String, Object> createdItem = createInventoryItem(inventoryItemServiceFields(targetInventoryItem));
                Object inventoryItemId = createdItem == null ? null : createdItem.get("inventoryItemId");
                if (inventoryItemId != null) {
                    entities.set(targetInventoryItem, "inventoryItemId", inventoryItemId);
                }

                Map<String, Object> targetDetail = new LinkedHashMap<>();
                targetDetail.put("inventoryItemId", inventoryItemId);
                targetDetail.put("availableToPromiseDiff", currentQuantityToMove);
                targetDetail.put("quantityOnHandDiff", currentQuantityToMove);
                callInventoryService("createInventoryItemDetail", targetDetail);

                Map<String, Object> sourceDetail = new LinkedHashMap<>();
                sourceDetail.put("inventoryItemId", entities.get(inventoryItem, "inventoryItemId"));
                sourceDetail.put("availableToPromiseDiff", negative(toDecimal(currentQuantityToMove)));
                sourceDetail.put("quantityOnHandDiff", negative(toDecimal(currentQuantityToMove)));
                callInventoryService("createInventoryItemDetail", sourceDetail);
                refresh("InventoryItem", targetInventoryItem);
            }
        }
        if (greater(state.get("quantityLeftToProcess"), BigDecimal.ZERO, errors(state))) {
            Map<String, Object> values = new LinkedHashMap<>(state);
            values.put("parameters", input);
            state.put("warningMessageList", add(stringList(state.get("warningMessageList")),
                    expand("ERROR: Not enough available inventory found in location "
                            + "[${parameters.locationSeqId}] in facility [${parameters.facilityId}], did not "
                            + "reallocate ${quantityLeftToProcess} of the ${parameters.quantityMoved} reported "
                            + "as physically moved.",
                            values)));
        }
        throwErrors(errors(state));
        Map<String, Object> output = new LinkedHashMap<>();
        putIfPresent(output, "warningMessageList", state.get("warningMessageList"));
        return output;
    }

    /** Port of applications/product/minilang/product/inventory/StockMoveServices.xml:426-624 inline. */
    private void processOisgirMoveByStatusInline(Map<String, Object> state) {
        Map<String, Object> input = castMap((Map<?, ?>) state.get("parameters"));
        List<Map<String, Object>> reservations = orderReservationLocationRows(Map.of(
                "productId", input.get("productId"),
                "facilityId", input.get("facilityId"),
                "locationSeqId", input.get("locationSeqId"),
                "orderItemStatusId", state.get("orderItemStatusId")), "reservedDatetime");
        Map<Object, List<Map<String, Object>>> reservationsByInventoryItem = new LinkedHashMap<>();
        for (Map<String, Object> reservation : reservations) {
            addToListMap(reservationsByInventoryItem, reservation.get("inventoryItemId"), reservation);
        }

        for (Map.Entry<Object, List<Map<String, Object>>> inventoryEntry
                : reservationsByInventoryItem.entrySet()) {
            Object inventoryItem = findByPrimaryKey(
                    "InventoryItem", Map.of("inventoryItemId", inventoryEntry.getKey()));
            if (inventoryItem == null) {
                continue;
            }
            Object inventoryItemTypeId = entities.get(inventoryItem, "inventoryItemTypeId");
            if (equal(inventoryItemTypeId, "SERIALIZED_INV_ITEM")) {
                entities.set(inventoryItem, "locationSeqId", input.get("targetLocationSeqId"));
                store(inventoryItem);
                // Emulates applications/product/entitydef/eecas.xml:46-55: sync updateSerializedInventoryTotals;
                // async checkProductInventoryDiscontinuation is a documented no-op when ATP is nonempty and <= 0.
                callInventoryService("updateSerializedInventoryTotals",
                        Map.of("inventoryItemId", entities.get(inventoryItem, "inventoryItemId")));
            }

            if (!equal(inventoryItemTypeId, "NON_SERIAL_INV_ITEM")) {
                continue;
            }
            Object targetInventoryItem = copyEntity("InventoryItem", inventoryItem);
            entities.set(targetInventoryItem, "locationSeqId", input.get("targetLocationSeqId"));
            Map<String, Object> createdItem = createInventoryItem(inventoryItemServiceFields(targetInventoryItem));
            Object targetInventoryItemId = createdItem == null ? null : createdItem.get("inventoryItemId");
            if (targetInventoryItemId != null) {
                entities.set(targetInventoryItem, "inventoryItemId", targetInventoryItemId);
            }
            targetInventoryItem = refresh("InventoryItem", targetInventoryItem);
            state.put("haveSetIiDetail", "N");
            state.put("remainingQuantityOnHand", entities.get(inventoryItem, "quantityOnHandTotal"));

            for (Map<String, Object> reservationRow : inventoryEntry.getValue()) {
                Object reservation = findByPrimaryKey("OrderItemShipGrpInvRes", keys(
                        "orderId", reservationRow.get("orderId"),
                        "shipGroupSeqId", reservationRow.get("shipGroupSeqId"),
                        "orderItemSeqId", reservationRow.get("orderItemSeqId"),
                        "inventoryItemId", reservationRow.get("inventoryItemId")));
                if (reservation == null) {
                    continue;
                }
                state.remove("reservedQuantityLeftOver");
                state.remove("currentQuantityToMove");
                state.remove("quantityNotAvailableToMove");
                if (!greater(state.get("quantityLeftToProcess"), BigDecimal.ZERO, errors(state))) {
                    continue;
                }

                Object quantityLeft = state.get("quantityLeftToProcess");
                Object reservationQuantity = entities.get(reservation, "quantity");
                Object currentQuantityToMove = less(quantityLeft, reservationQuantity, errors(state))
                        ? quantityLeft
                        : reservationQuantity;
                Object remainingQuantityOnHand = state.get("remainingQuantityOnHand");
                if (greater(currentQuantityToMove, remainingQuantityOnHand, errors(state))) {
                    currentQuantityToMove = remainingQuantityOnHand;
                }
                Object quantityNotAvailableValue = entities.get(reservation, "quantityNotAvailable");
                if (empty(quantityNotAvailableValue)) {
                    quantityNotAvailableValue = BigDecimal.ZERO;
                    entities.set(reservation, "quantityNotAvailable", quantityNotAvailableValue);
                }
                BigDecimal reservedQuantityLeftOver = subtract(
                        subtract(calcValue(reservationQuantity), calcValue(quantityNotAvailableValue)),
                        calcValue(currentQuantityToMove));
                Object quantityNotAvailableToMove;
                if (!empty(reservedQuantityLeftOver)
                        && greater(reservedQuantityLeftOver, BigDecimal.ZERO, errors(state))) {
                    quantityNotAvailableToMove = BigDecimal.ZERO;
                } else {
                    quantityNotAvailableToMove = quantityNotAvailableValue;
                }

                Object targetReservation = copyEntity("OrderItemShipGrpInvRes", reservation);
                entities.set(targetReservation, "inventoryItemId", targetInventoryItemId);
                Map<String, Object> sourceDetail = new LinkedHashMap<>();
                sourceDetail.put("inventoryItemId", entities.get(inventoryItem, "inventoryItemId"));
                Map<String, Object> targetDetail = new LinkedHashMap<>();
                targetDetail.put("inventoryItemId", targetInventoryItemId);
                sourceDetail.put("quantityOnHandDiff", negative(calcValue(currentQuantityToMove)));
                targetDetail.put("quantityOnHandDiff", scale2(calcValue(currentQuantityToMove)));
                if (!empty(entities.get(reservation, "quantityNotAvailable"))
                        && greater(entities.get(reservation, "quantityNotAvailable"), BigDecimal.ZERO, errors(state))) {
                    sourceDetail.put("availableToPromiseDiff", scale2(calcValue(quantityNotAvailableToMove)));
                    targetDetail.put("availableToPromiseDiff", negative(calcValue(quantityNotAvailableToMove)));
                }
                callInventoryService("createInventoryItemDetail", sourceDetail);
                callInventoryService("createInventoryItemDetail", targetDetail);

                entities.set(targetReservation, "quantity",
                        add(calcValue(currentQuantityToMove), calcValue(quantityNotAvailableToMove)));
                entities.set(targetReservation, "quantityNotAvailable",
                        scale2(calcValue(quantityNotAvailableToMove)));
                create(targetReservation);

                BigDecimal newReservationQuantity = subtract(
                        subtract(calcValue(reservationQuantity), calcValue(currentQuantityToMove)),
                        calcValue(quantityNotAvailableToMove));
                BigDecimal newQuantityNotAvailable = subtract(
                        calcValue(entities.get(reservation, "quantityNotAvailable")),
                        calcValue(quantityNotAvailableToMove));
                entities.set(reservation, "quantity", newReservationQuantity);
                entities.set(reservation, "quantityNotAvailable", newQuantityNotAvailable);
                store(reservation);
                if (equal(newReservationQuantity, BigDecimal.ZERO)
                        && equal(newQuantityNotAvailable, BigDecimal.ZERO)) {
                    remove(reservation);
                } else {
                    store(reservation);
                }
                state.put("haveSetIiDetail", "Y");
                state.put("quantityLeftToProcess",
                        subtract(calcValue(quantityLeft), calcValue(currentQuantityToMove)));
                state.put("remainingQuantityOnHand",
                        subtract(calcValue(remainingQuantityOnHand), calcValue(currentQuantityToMove)));
            }
            if (equal(state.get("haveSetIiDetail"), "N")) {
                remove(targetInventoryItem);
            }
        }
    }

    /** Port of applications/product/minilang/product/inventory/StockMoveServices.xml:625-634. */
    private Map<String, Object> findProductInventorylocations(Map<String, Object> input) {
        List<Map<String, Object>> locations = inventoryItemAndLocationRows(Map.of(
                "facilityId", input.get("facilityId"),
                "productId", input.get("productId")));
        locations.removeIf(location -> location.get("locationSeqId") == null);
        return Map.of("LocationList", locations);
    }

    /** Delegates the unported Groovy createInventoryItem service. */
    public Map<String, Object> createInventoryItem(Map<String, Object> serviceInput) {
        return unportedPort.execute("createInventoryItem");
    }

    /** Builds the createInventoryItem input fields selected by Minilang set-service-fields. */
    private Map<String, Object> inventoryItemServiceFields(Object inventoryItem) {
        Map<String, Object> serviceInput = new LinkedHashMap<>();
        for (String field : CREATE_INVENTORY_ITEM_FIELDS) {
            serviceInput.put(field, entities.get(inventoryItem, field));
        }
        serviceInput.put("isReturned", "N");
        return serviceInput;
    }

    /** Invokes an InventoryServices simple-method through its service executor. */
    private void callInventoryService(String serviceName, Map<String, Object> input) {
        inventoryServices.execute(serviceName, serviceName, input);
    }

    /** Merges an entity using the persistence context. */
    private void store(Object entity) {
        entityManager.merge(entity);
    }

    /** Persists a new entity using the persistence context. */
    private void create(Object entity) {
        entityManager.persist(entity);
    }

    /** Removes an entity, attaching it to the persistence context when needed. */
    private void remove(Object entity) {
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
    }

    /** Flushes pending writes and reloads an entity by its primary key. */
    private Object refresh(String entityName, Object entity) {
        entityManager.flush();
        Object refreshed = findByPrimaryKey(entityName, entities.toMap(entity));
        if (refreshed != null) {
            entityManager.refresh(refreshed);
            return refreshed;
        }
        return entity;
    }

    /** Copies writable entity fields into a new generated entity instance. */
    private Object copyEntity(String entityName, Object source) {
        Object copy = entities.newEntity(entities.entityClass(entityName));
        BeanWrapperImpl wrapper = new BeanWrapperImpl(copy);
        entities.toMap(source).forEach((field, value) -> {
            if (wrapper.isWritableProperty(field)) {
                entities.set(copy, field, value);
            }
        });
        return copy;
    }

    /** Finds an entity by its primary-key fields. */
    private Object findByPrimaryKey(String entityName, Map<String, ?> keys) {
        return entities.findByPrimaryKey(entities.entityClass(entityName), keys);
    }

    /** Finds generated entities matching equality filters and sort fields. */
    private List<Object> findAll(String entityName, Map<String, ?> filters, List<String> orderBy) {
        return new ArrayList<>(findAll(entities.entityClass(entityName), filters, orderBy));
    }

    /** Executes a generated-entity query with equality predicates and ordering. */
    private <T> List<T> findAll(Class<T> type, Map<String, ?> filters, List<String> orderBy) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(type);
        Root<T> root = query.from(type);
        List<Predicate> predicates = new ArrayList<>();
        for (Map.Entry<String, ?> filter : filters.entrySet()) {
            Object value = convertFilter(type, filter.getKey(), filter.getValue());
            predicates.add(value == null
                    ? builder.isNull(root.get(filter.getKey()))
                    : builder.equal(root.get(filter.getKey()), value));
        }
        List<Order> orderings = new ArrayList<>();
        for (String field : orderBy) {
            orderings.add(builder.asc(root.get(field)));
        }
        query.select(root).where(predicates.toArray(Predicate[]::new)).orderBy(orderings);
        return entityManager.createQuery(query).getResultList();
    }

    /** Converts filter values to the generated entity property's Java type. */
    private Object convertFilter(Class<?> type, String field, Object value) {
        if (value == null) {
            return null;
        }
        Class<?> propertyType = new BeanWrapperImpl(entities.newEntity(type)).getPropertyType(field);
        if (propertyType == null || propertyType.isInstance(value)) {
            return value;
        }
        String text = value.toString();
        if (propertyType == String.class) {
            return text;
        }
        if (propertyType == BigDecimal.class) {
            return new BigDecimal(text);
        }
        if (propertyType == Long.class || propertyType == long.class) {
            return Long.valueOf(text);
        }
        if (propertyType == Integer.class || propertyType == int.class) {
            return Integer.valueOf(text);
        }
        return value;
    }

    /**
     * Emulates OrderItemShipGrpInvResAndItemLocation from applications/order/entitydef/entitymodel_view.xml:808
     * using view-member processing in
     * framework/entity/src/main/java/org/apache/ofbiz/entity/model/ModelViewEntity.java:795-870.
     */
    private List<Map<String, Object>> orderReservationLocationRows(
            Map<String, ?> filters, String orderBy) {
        StringBuilder jpql = new StringBuilder("select reservation, item, location, orderItem.statusId "
                + "from OrderItemShipGrpInvRes reservation, InventoryItem item, FacilityLocation location, "
                + "OrderItem orderItem where reservation.inventoryItemId = item.inventoryItemId "
                + "and item.facilityId = location.facilityId and item.locationSeqId = location.locationSeqId "
                + "and reservation.orderId = orderItem.orderId "
                + "and reservation.orderItemSeqId = orderItem.orderItemSeqId");
        Map<String, String> fields = Map.of(
                "productId", "item.productId",
                "facilityId", "item.facilityId",
                "locationSeqId", "item.locationSeqId",
                "locationTypeEnumId", "location.locationTypeEnumId",
                "orderItemStatusId", "orderItem.statusId");
        Map<String, Object> parameters = appendFilters(jpql, filters, fields);
        if (orderBy != null) {
            jpql.append(" order by reservation.").append(orderBy);
        }
        TypedQuery<Object[]> query = entityManager.createQuery(jpql.toString(), Object[].class);
        parameters.forEach(query::setParameter);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Object[] tuple : query.getResultList()) {
            Map<String, Object> row = new LinkedHashMap<>();
            appendEntityFields(row, tuple[0], Set.of());
            appendEntityFields(row, tuple[1], Set.of());
            appendEntityFields(row, tuple[2], Set.of());
            row.putIfAbsent("orderItemStatusId", tuple[3]);
            rows.add(row);
        }
        return rows;
    }

    /**
     * Emulates ProductFacilityLocationView from applications/datamodel/entitydef/product-entitymodel.xml:1572
     * using view-member processing in
     * framework/entity/src/main/java/org/apache/ofbiz/entity/model/ModelViewEntity.java:795-870.
     */
    private List<Map<String, Object>> productFacilityLocationViewRows(Map<String, ?> filters) {
        StringBuilder jpql = new StringBuilder("select pfl, location from ProductFacilityLocation pfl, "
                + "FacilityLocation location where pfl.facilityId = location.facilityId "
                + "and pfl.locationSeqId = location.locationSeqId");
        Map<String, String> fields = Map.of(
                "productId", "pfl.productId",
                "facilityId", "pfl.facilityId",
                "locationSeqId", "pfl.locationSeqId",
                "locationTypeEnumId", "location.locationTypeEnumId");
        Map<String, Object> parameters = appendFilters(jpql, filters, fields);
        TypedQuery<Object[]> query = entityManager.createQuery(jpql.toString(), Object[].class);
        parameters.forEach(query::setParameter);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Object[] tuple : query.getResultList()) {
            Map<String, Object> row = new LinkedHashMap<>();
            appendEntityFields(row, tuple[0], Set.of());
            appendEntityFields(row, tuple[1], Set.of());
            rows.add(row);
        }
        return rows;
    }

    /**
     * Emulates ProductFacilityLocationQuantityTest from applications/datamodel/entitydef/product-entitymodel.xml:1538
     * using view-member processing in
     * framework/entity/src/main/java/org/apache/ofbiz/entity/model/ModelViewEntity.java:795-870.
     */
    private List<Map<String, Object>> productFacilityLocationQuantityRows(
            Object facilityId, String locationTypeEnumId) {
        String jpql = "select pfl.productId, pfl.facilityId, pfl.locationSeqId, pfl.minimumStock, "
                + "pfl.moveQuantity, location.locationTypeEnumId, sum(item.availableToPromiseTotal), "
                + "sum(item.quantityOnHandTotal) from ProductFacilityLocation pfl, FacilityLocation location, "
                + "InventoryItem item where pfl.facilityId = location.facilityId "
                + "and pfl.locationSeqId = location.locationSeqId and pfl.productId = item.productId "
                + "and pfl.facilityId = item.facilityId and pfl.locationSeqId = item.locationSeqId "
                + "and pfl.facilityId = :facilityId and location.locationTypeEnumId = :locationTypeEnumId "
                + "group by pfl.productId, pfl.facilityId, pfl.locationSeqId, pfl.minimumStock, "
                + "pfl.moveQuantity, location.locationTypeEnumId order by pfl.productId";
        TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
        query.setParameter("facilityId", facilityId);
        query.setParameter("locationTypeEnumId", locationTypeEnumId);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Object[] tuple : query.getResultList()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("productId", tuple[0]);
            row.put("facilityId", tuple[1]);
            row.put("locationSeqId", tuple[2]);
            row.put("minimumStock", tuple[3]);
            row.put("moveQuantity", tuple[4]);
            row.put("locationTypeEnumId", tuple[5]);
            row.put("availableToPromiseTotal", tuple[6]);
            row.put("quantityOnHandTotal", tuple[7]);
            rows.add(row);
        }
        return rows;
    }

    /**
     * Emulates InventoryItemAndLocation from applications/datamodel/entitydef/product-entitymodel.xml:2045
     * using view-member processing in
     * framework/entity/src/main/java/org/apache/ofbiz/entity/model/ModelViewEntity.java:795-870.
     */
    private List<Map<String, Object>> inventoryItemAndLocationRows(Map<String, ?> filters) {
        Map<String, Object> itemFilters = new LinkedHashMap<>();
        for (String field : List.of("productId", "facilityId", "locationSeqId", "inventoryItemId")) {
            if (filters.containsKey(field)) {
                itemFilters.put(field, filters.get(field));
            }
        }
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Object item : findAll("InventoryItem", itemFilters, List.of())) {
            Object product = null;
            Object productId = entities.get(item, "productId");
            if (!empty(productId)) {
                product = findByPrimaryKey("Product", Map.of("productId", productId));
            }
            Object facilityLocation = null;
            Object facilityId = entities.get(item, "facilityId");
            Object locationSeqId = entities.get(item, "locationSeqId");
            if (!empty(facilityId) && !empty(locationSeqId)) {
                facilityLocation = findByPrimaryKey("FacilityLocation",
                        keys("facilityId", facilityId, "locationSeqId", locationSeqId));
            }
            Map<String, Object> row = new LinkedHashMap<>();
            appendEntityFields(row, item, INVENTORY_ITEM_VIEW_EXCLUDES);
            if (product != null) {
                appendEntityFields(row, product, PRODUCT_VIEW_EXCLUDES);
            }
            if (facilityLocation != null) {
                appendEntityFields(row, facilityLocation, Set.of());
            }
            boolean matches = true;
            for (Map.Entry<String, ?> filter : filters.entrySet()) {
                Object value = row.get(filter.getKey());
                if ("locationSeqId".equals(filter.getKey()) && filter.getValue() == null) {
                    matches &= value != null;
                } else if (!equal(value, filter.getValue())) {
                    matches = false;
                }
            }
            if (matches) {
                rows.add(row);
            }
        }
        return rows;
    }

    /** Appends equality predicates for fields exposed by an emulated entity view. */
    private Map<String, Object> appendFilters(
            StringBuilder jpql, Map<String, ?> filters, Map<String, String> fieldExpressions) {
        Map<String, Object> parameters = new LinkedHashMap<>();
        for (Map.Entry<String, ?> filter : filters.entrySet()) {
            String expression = fieldExpressions.get(filter.getKey());
            if (expression == null) {
                throw new MigrationException(HttpStatus.BAD_REQUEST, "Unknown view field: " + filter.getKey());
            }
            String parameter = "filter" + parameters.size();
            jpql.append(" and ").append(expression).append(" = :").append(parameter);
            parameters.put(parameter, filter.getValue());
        }
        return parameters;
    }

    /** Adds generated entity fields to an emulated entity-view row. */
    private void appendEntityFields(Map<String, Object> row, Object entity, Set<String> excluded) {
        if (entity == null) {
            return;
        }
        for (Field field : entity.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic() || excluded.contains(field.getName())) {
                continue;
            }
            String name = field.getName();
            if (!row.containsKey(name)) {
                row.put(name, entities.get(entity, name));
            }
        }
    }

    /** Converts an entity to a mutable field map, returning an empty map for a missing entity. */
    private Map<String, Object> entityMap(Object entity) {
        if (entity == null) {
            return new LinkedHashMap<>();
        }
        return new LinkedHashMap<>(entities.toMap(entity));
    }

    /** Stores an entity field map in a result under the requested field name. */
    private void putEntity(Map<String, Object> target, String field, Object entity) {
        target.put(field, entity == null ? null : entities.toMap(entity));
    }

    /** Builds a field-name map from alternating name and value arguments. */
    private Map<String, Object> keys(Object... nameValues) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int i = 0; i < nameValues.length; i += 2) {
            result.put((String) nameValues[i], nameValues[i + 1]);
        }
        return result;
    }

    /** Appends a row to the list grouped under its key. */
    private static void addToListMap(
            Map<Object, List<Map<String, Object>>> map, Object key, Map<String, Object> value) {
        map.computeIfAbsent(key, ignored -> new ArrayList<>()).add(value);
    }

    /** Returns a list with the supplied value appended. */
    private static List<String> add(List<String> values, String value) {
        List<String> result = values == null ? new ArrayList<>() : values;
        result.add(value);
        return result;
    }

    /** Copies list values to strings, preserving a missing list as null. */
    private static List<String> stringList(Object value) {
        if (value instanceof List<?> list) {
            List<String> result = new ArrayList<>();
            for (Object item : list) {
                result.add(Objects.toString(item, null));
            }
            return result;
        }
        return null;
    }

    /** Adds a non-null output value to a result map. */
    private static void putIfPresent(Map<String, Object> output, String name, Object value) {
        if (value != null) {
            output.put(name, value);
        }
    }

    /** Sums row fields as Minilang calculate results. */
    private static BigDecimal sum(List<Map<String, Object>> rows, String field) {
        BigDecimal result = zero();
        for (Map<String, Object> row : rows) {
            result = add(result, calcValue(row.get(field)));
        }
        return result;
    }

    /** Sums generated entity fields as Minilang calculate results. */
    private BigDecimal sumEntities(List<Object> rows, String field) {
        BigDecimal result = zero();
        for (Object row : rows) {
            result = add(result, calcValue(entities.get(row, field)));
        }
        return result;
    }

    /** Creates the scaled zero used as a Minilang calculate result. */
    private static BigDecimal zero() {
        return BigDecimal.ZERO.setScale(SCALE, ROUNDING);
    }

    /**
     * Reads a calcop field value, mapping null to zero without scaling it
     * (framework/minilang/src/main/java/org/apache/ofbiz/minilang/method/otherops/Calculate.java:129-137).
     */
    private static BigDecimal calcValue(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return toDecimal(value);
    }

    /**
     * Converts a raw numeric value without changing its scale
     * (framework/minilang/src/main/java/org/apache/ofbiz/minilang/method/conditional/Compare.java:36-48).
     */
    private static BigDecimal toDecimal(Object value) {
        if (value == null) {
            return null;
        }
        return value instanceof BigDecimal number ? number : new BigDecimal(value.toString());
    }

    /**
     * Applies Minilang calculate's scale and rounding mode
     * (framework/minilang/src/main/java/org/apache/ofbiz/minilang/method/otherops/Calculate.java:129-137).
     */
    private static BigDecimal scale2(BigDecimal value) {
        return value.setScale(SCALE, ROUNDING);
    }

    /**
     * Adds calcop operands and scales the calculate result
     * (framework/minilang/src/main/java/org/apache/ofbiz/minilang/method/otherops/Calculate.java:129-137).
     */
    private static BigDecimal add(BigDecimal left, BigDecimal right) {
        return scale2(left.add(right));
    }

    /**
     * Subtracts calcop operands and scales the calculate result
     * (framework/minilang/src/main/java/org/apache/ofbiz/minilang/method/otherops/Calculate.java:129-137).
     */
    private static BigDecimal subtract(BigDecimal left, BigDecimal right) {
        return scale2(left.subtract(right));
    }

    /**
     * Negates a calcop operand and scales the calculate result
     * (framework/minilang/src/main/java/org/apache/ofbiz/minilang/method/otherops/Calculate.java:129-137).
     */
    private static BigDecimal negative(BigDecimal value) {
        return scale2(value.negate());
    }

    /** Checks whether a value is empty using Minilang's empty-value conventions. */
    private static boolean empty(Object value) {
        return value == null
                || value instanceof String text && text.isEmpty()
                || value instanceof Map<?, ?> map && map.isEmpty()
                || value instanceof List<?> list && list.isEmpty();
    }

    /**
     * Compares values for equality without changing their numeric scale
     * (framework/minilang/src/main/java/org/apache/ofbiz/minilang/method/conditional/Compare.java:36-48).
     */
    private static boolean equal(Object left, Object right) {
        if (left == null || right == null) {
            return left == right;
        }
        if (left instanceof Number || right instanceof Number) {
            return toDecimal(left).compareTo(toDecimal(right)) == 0;
        }
        return left.equals(right);
    }

    /**
     * Returns the inverse of Minilang equality
     * (framework/minilang/src/main/java/org/apache/ofbiz/minilang/method/conditional/Compare.java:36-48).
     */
    private static boolean notEqual(Object left, Object right) {
        return !equal(left, right);
    }

    /**
     * Performs a Minilang ordered less-than comparison
     * (framework/minilang/src/main/java/org/apache/ofbiz/minilang/method/conditional/Compare.java:36-48).
     */
    private static boolean less(Object left, Object right, List<String> errors) {
        return compare(left, right, errors) < 0;
    }

    /**
     * Performs a Minilang ordered greater-than comparison
     * (framework/minilang/src/main/java/org/apache/ofbiz/minilang/method/conditional/Compare.java:36-48).
     */
    private static boolean greater(Object left, Object right, List<String> errors) {
        return compare(left, right, errors) > 0;
    }

    /**
     * Compares raw values, collecting the Minilang null-operand error when necessary
     * (framework/minilang/src/main/java/org/apache/ofbiz/minilang/method/conditional/Compare.java:36-48).
     */
    private static int compare(Object left, Object right, List<String> errors) {
        if (left == null) {
            errors.add("Cannot compare: l-value is null");
            return 0;
        }
        if (right == null) {
            errors.add("Cannot compare: r-value is null");
            return 0;
        }
        if (left instanceof Number || right instanceof Number) {
            return toDecimal(left).compareTo(toDecimal(right));
        }
        return left.toString().compareTo(right.toString());
    }

    /** Raises a service failure for errors accumulated by Minilang operations. */
    private static void throwErrors(List<String> errors) {
        if (!errors.isEmpty()) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, String.join(" ", errors));
        }
    }

    /** Retrieves the error list carried in a Minilang state map. */
    private static List<String> errors(Map<String, Object> state) {
        @SuppressWarnings("unchecked")
        List<String> errors = (List<String>) state.get("errors");
        return errors;
    }

    /** Expands a Minilang message using the provided scope maps. */
    private static String expand(String message, Object... scopes) {
        Map<String, Object> scope = new LinkedHashMap<>();
        for (Object item : scopes) {
            if (item instanceof Map<?, ?> map) {
                scope.putAll(castMap(map));
            }
        }
        Matcher matcher = FIELD_REFERENCE.matcher(message);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            Object value = getPath(scope, matcher.group(1));
            String replacement = value instanceof BigDecimal decimal
                    ? decimal.toPlainString()
                    : Objects.toString(value, "");
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /** Resolves a dotted path through nested result maps. */
    private static Object getPath(Map<String, Object> scope, String path) {
        Object value = scope;
        for (String part : path.split("\\.")) {
            if (value instanceof Map<?, ?> map) {
                value = map.get(part);
            } else {
                return null;
            }
        }
        return value;
    }

    /** Reads a field from a Minilang scope map. */
    private static Object get(Map<String, Object> scope, String field) {
        return scope.get(field);
    }

    /** Casts a map whose keys and values are known to be strings and objects. */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> castMap(Map<?, ?> map) {
        return (Map<String, Object>) map;
    }
}
