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
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.apache.ofbiz.migration.product.simple;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.product.adapter.ProductUnportedPort;
import org.apache.ofbiz.migration.product.generated.ProductServiceMetadata;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Port of applications/product/minilang/product/inventory/InventoryReserveServices.xml:23-748.
 */
@Component
public class InventoryReserveServices extends UnportedSimpleMethodFile {
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final int INVENTORY_SCALE = 6;

    private final EntityRegistry entities;
    private final EntityManager entityManager;
    private final ProductSimpleServiceExecutor executor;
    private final InventoryServices inventoryServices;

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:23-748. */
    public InventoryReserveServices(
            ProductUnportedPort unportedPort,
            EntityRegistry entities,
            EntityManager entityManager,
            @Lazy ProductSimpleServiceExecutor executor,
            @Lazy InventoryServices inventoryServices) {
        super(unportedPort);
        this.entities = entities;
        this.entityManager = entityManager;
        this.executor = executor;
        this.inventoryServices = inventoryServices;
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:23-748. */
    @Override
    public Map<String, Object> execute(String serviceName, String methodName, Map<String, Object> input) {
        Env env = new Env(input);
        return switch (methodName) {
            case "reserveProductInventory" -> reserveProductInventory(env);
            case "reserveAnInventoryItem" -> reserveAnInventoryItem(env);
            case "reserveOrderItemInventory" -> reserveOrderItemInventory(env);
            case "cancelOrderInventoryReservation" -> cancelOrderInventoryReservation(env);
            case "cancelOrderItemInvResQty" -> cancelOrderItemInvResQty(env);
            case "cancelOrderItemShipGrpInvRes" -> cancelOrderItemShipGrpInvRes(env);
            default -> super.execute(serviceName, methodName, env.parameters);
        };
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:23-348. */
    private Map<String, Object> reserveProductInventory(Env env) {
        env.nowTimestamp = new Timestamp(System.currentTimeMillis());
        env.product = entityOne("Product", values("productId", env.parameters.get("productId")));
        env.facility = entityOne("Facility", values("facilityId", env.parameters.get("facilityId")));
        Object productType = relatedOne(env.product, "ProductType", "product");
        if ("N".equals(text(productType, "isPhysical"))) {
            env.parameters.put("quantityNotReserved", ZERO);
            return result("quantityNotReserved", ZERO);
        }

        if (!empty(env.parameters.get("orderId"))) {
            env.orderHeader = entityOne("OrderHeader", values("orderId", env.parameters.get("orderId")));
        }
        if (!empty(env.parameters.get("workEffortId"))) {
            env.productionRunTask = entityOne(
                    "WorkEffort", values("workEffortId", env.parameters.get("workEffortId")));
        }

        String orderBy = inventoryOrderBy(env.parameters.get("reserveOrderEnumId"), env.parameters);
        env.parameters.put("quantityNotReserved", decimal(env.parameters.get("quantity")));
        for (String locationType : List.of("FLT_PICKLOC", "FLT_BULK")) {
            if (greaterThanZero(env.parameters.get("quantityNotReserved"))) {
                for (Object inventoryItem : findInventoryItems(env, locationType, orderBy)) {
                    if (!greaterThanZero(env.parameters.get("quantityNotReserved"))) {
                        break;
                    }
                    env.inventoryItem = inventoryItem;
                    reserveForInventoryItemInline(env);
                }
            }
        }
        if (greaterThanZero(env.parameters.get("quantityNotReserved"))) {
            for (Object inventoryItem : findInventoryItems(env, null, orderBy)) {
                if (!empty(value(inventoryItem, "locationSeqId"))
                        && greaterThanZero(env.parameters.get("quantityNotReserved"))) {
                    continue;
                }
                if (!greaterThanZero(env.parameters.get("quantityNotReserved"))) {
                    break;
                }
                env.inventoryItem = inventoryItem;
                reserveForInventoryItemInline(env);
            }
        }

        BigDecimal quantityNotReserved = decimal(env.parameters.get("quantityNotReserved"));
        if (quantityNotReserved != null && quantityNotReserved.compareTo(ZERO) != 0) {
            if ("Y".equals(env.parameters.get("requireInventory"))) {
                throw new MigrationException(
                        HttpStatus.CONFLICT,
                        "Cannot reserve product " + env.parameters.get("productId")
                                + ": not enough quantity left in stock for facility "
                                + env.parameters.get("facilityId") + ". Quantity not reserved is "
                                + quantityNotReserved);
            }
            if (env.lastNonSerInventoryItem != null) {
                reserveShortageOnExistingItem(env, env.lastNonSerInventoryItem, quantityNotReserved);
            } else {
                createShortageInventoryItem(env, quantityNotReserved);
            }
            env.parameters.put("quantityNotReserved", ZERO);
        }
        return result("quantityNotReserved", env.parameters.get("quantityNotReserved"));
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:350-460. */
    private Map<String, Object> reserveAnInventoryItem(Env env) {
        Object reservedItem = entityOne(
                "InventoryItem", values("inventoryItemId", env.parameters.get("inventoryItemId")));
        Object facility = value(reservedItem, "facilityId");
        if ("NON_SERIAL_INV_ITEM".equals(value(reservedItem, "inventoryItemTypeId"))) {
            callProductService(env, "createInventoryItemDetail", values(
                    "inventoryItemId", value(reservedItem, "inventoryItemId"),
                    "orderId", env.parameters.get("orderId"),
                    "orderItemSeqId", env.parameters.get("orderItemSeqId"),
                    "quantityOnHandDiff", BigDecimal.ONE.negate(),
                    "availableToPromiseDiff", BigDecimal.ONE.negate(),
                    "workEffortId", env.parameters.get("workEffortId")));
        }
        Map<String, Object> cancelMap = copyFields(env.parameters,
                "orderId", "orderItemSeqId", "shipGroupSeqId", "inventoryItemId");
        putIfPresent(cancelMap, "cancelQuantity", env.parameters.get("quantity"));
        callProductService(env, "cancelOrderItemShipGrpInvRes", cancelMap);

        Map<String, Object> serialFilters = values(
                "productId", env.parameters.get("productId"),
                "inventoryItemTypeId", "SERIALIZED_INV_ITEM",
                "serialNumber", env.parameters.get("serialNumber"));
        List<Object> items = findByAnd("InventoryItem", serialFilters);
        Object inventoryItem = items.isEmpty() ? null : items.get(0);
        if (inventoryItem == null) {
            Map<String, Object> receiveContext = values(
                    "productId", env.parameters.get("productId"),
                    "facilityId", facility,
                    "quantityAccepted", env.parameters.get("quantity"),
                    "quantityRejected", ZERO,
                    "inventoryItemTypeId", "SERIALIZED_INV_ITEM",
                    "serialNumber", env.parameters.get("serialNumber"));
            Map<String, Object> received = super.execute("receiveInventoryProduct", "receiveInventoryProduct",
                    receiveContext);
            inventoryItem = entityOne(
                    "InventoryItem", values("inventoryItemId", received.get("inventoryItemId")));
        }

        List<Object> reservations = findByAnd(
                "OrderItemShipGrpInvRes", values("inventoryItemId", value(inventoryItem, "inventoryItemId")));
        Object otherReservation = reservations.isEmpty() ? null : reservations.get(0);
        if (otherReservation != null) {
            callProductService(env, "cancelOrderItemShipGrpInvRes", copyFields(otherReservation,
                    "orderId", "orderItemSeqId", "shipGroupSeqId", "inventoryItemId"));
            entityManager.refresh(inventoryItem);
            entities.set(inventoryItem, "statusId", "INV_PROMISED");

            Map<String, Object> replacement = values(
                    "orderId", value(otherReservation, "orderId"),
                    "productId", env.parameters.get("productId"),
                    "orderItemSeqId", value(otherReservation, "orderItemSeqId"),
                    "shipGroupSeqId", value(otherReservation, "shipGroupSeqId"),
                    "reserveOrderEnumId", value(otherReservation, "reserveOrderEnumId"),
                    "reservedDatetime", value(otherReservation, "reservedDatetime"),
                    "quantity", BigDecimal.ONE,
                    "requireInventory", env.parameters.get("requireInventory"),
                    "priority", env.parameters.get("priority"));
            putIfPresent(replacement, "sequenceId", value(otherReservation, "sequenceId"));
            callProductService(env, "reserveProductInventory", replacement);
        }

        if ("INV_AVAILABLE".equals(value(inventoryItem, "statusId"))) {
            entities.set(inventoryItem, "statusId", "INV_PROMISED");
        }
        Map<String, Object> ownReservation = values(
                "orderId", env.parameters.get("orderId"),
                "orderItemSeqId", env.parameters.get("orderItemSeqId"),
                "shipGroupSeqId", env.parameters.get("shipGroupSeqId"),
                "inventoryItemId", value(inventoryItem, "inventoryItemId"),
                "reserveOrderEnumId", env.parameters.get("reserveOrderEnumId"),
                "reservedDatetime", env.parameters.get("reservedDatetime"),
                "promisedDatetime", env.promisedDatetime,
                "quantity", BigDecimal.ONE);
        putIfPresent(ownReservation, "sequenceId", env.parameters.get("sequenceId"));

        // These values are always empty in the minilang env for reserveAnInventoryItem.
        if (env.orderHeader != null) {
            putIfPresent(ownReservation, "priority", env.parameters.get("priority"));
            callProductService(env, "reserveOrderItemInventory", ownReservation);
        }
        if (env.productionRunTask != null) {
            Map<String, Object> workEffortReservation = values(
                    "workEffortId", env.parameters.get("workEffortId"),
                    "inventoryItemId", value(inventoryItem, "inventoryItemId"),
                    "reserveOrderEnumId", env.parameters.get("reserveOrderEnumId"),
                    "reservedDatetime", env.parameters.get("reservedDatetime"),
                    "promisedDatetime", env.promisedDatetime,
                    "quantity", BigDecimal.ONE,
                    "productId", env.parameters.get("productId"));
            putIfPresent(workEffortReservation, "sequenceId", env.parameters.get("sequenceId"));
            reserveWorkEffortInventory(workEffortReservation);
        }
        return result("inventoryItemId", value(inventoryItem, "inventoryItemId"));
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:603-634. */
    private Map<String, Object> reserveOrderItemInventory(Env env) {
        env.nowTimestamp = new Timestamp(System.currentTimeMillis());
        Object reservation = entityOne("OrderItemShipGrpInvRes", env.parameters);
        Object orderItem = entityOne("OrderItem", env.parameters);
        setIfNotNull(env.parameters, "promisedDatetime", value(orderItem, "shipBeforeDate"));
        setIfNotNull(env.parameters, "currentPromisedDate", value(orderItem, "shipBeforeDate"));

        if (reservation == null) {
            reservation = entities.newEntity(entities.entityClass("OrderItemShipGrpInvRes"));
            BeanWrapperImpl properties = new BeanWrapperImpl(reservation);
            for (Map.Entry<String, Object> entry : env.parameters.entrySet()) {
                if (properties.isWritableProperty(entry.getKey())) {
                    entities.set(reservation, entry.getKey(), entry.getValue());
                }
            }
            entities.set(reservation, "createdDatetime", env.nowTimestamp);
            entities.set(reservation, "priority", env.parameters.get("priority"));
            if (empty(value(reservation, "reservedDatetime"))) {
                entities.set(reservation, "reservedDatetime", env.nowTimestamp);
            }
            entityManager.persist(reservation);
        } else {
            entities.set(reservation, "quantity", add(
                    value(reservation, "quantity"), env.parameters.get("quantity"), INVENTORY_SCALE));
            entities.set(reservation, "quantityNotAvailable", add(
                    value(reservation, "quantityNotAvailable"),
                    env.parameters.get("quantityNotAvailable"), INVENTORY_SCALE));
        }
        return Map.of();
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:636-664. */
    private Map<String, Object> cancelOrderInventoryReservation(Env env) {
        Map<String, Object> filters = values("orderId", env.parameters.get("orderId"));
        putIfPresent(filters, "orderItemSeqId", env.parameters.get("orderItemSeqId"));
        putIfPresent(filters, "shipGroupSeqId", env.parameters.get("shipGroupSeqId"));
        for (Object reservation : findByAnd("OrderItemShipGrpInvRes", filters)) {
            Map<String, Object> keys = copyFields(
                    reservation, "orderId", "orderItemSeqId", "shipGroupSeqId", "inventoryItemId");
            callProductService(env, "cancelOrderItemShipGrpInvRes", keys);
            checkDecomposeInventoryItem(value(reservation, "inventoryItemId"));
        }
        return Map.of();
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:665-709. */
    private Map<String, Object> cancelOrderItemInvResQty(Env env) {
        if (empty(env.parameters.get("cancelQuantity"))) {
            Map<String, Object> cancelMap = copyFields(
                    env.parameters, "orderId", "orderItemSeqId", "shipGroupSeqId");
            callProductService(env, "cancelOrderInventoryReservation", cancelMap);
            return Map.of();
        }

        BigDecimal toCancelAmount = decimal(env.parameters.get("cancelQuantity"));
        Map<String, Object> filters = copyFields(env.parameters, "orderId", "orderItemSeqId", "shipGroupSeqId");
        Map<String, Object> cancelMap = new LinkedHashMap<>();
        for (Object reservation : findByAnd("OrderItemShipGrpInvRes", filters)) {
            if (toCancelAmount == null || toCancelAmount.compareTo(ZERO) <= 0) {
                break;
            }
            BigDecimal quantity = decimal(value(reservation, "quantity"));
            if (quantity != null) {
                cancelMap.put("cancelQuantity", quantity.compareTo(toCancelAmount) >= 0
                        ? toCancelAmount : quantity);
            }
            cancelMap.putAll(copyFields(
                    reservation, "orderId", "orderItemSeqId", "shipGroupSeqId", "inventoryItemId"));
            callProductService(env, "cancelOrderItemShipGrpInvRes", cancelMap);
            checkDecomposeInventoryItem(value(reservation, "inventoryItemId"));
            toCancelAmount = subtract(toCancelAmount, decimal(cancelMap.get("cancelQuantity")), INVENTORY_SCALE);
        }
        return Map.of();
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:710-748. */
    private Map<String, Object> cancelOrderItemShipGrpInvRes(Env env) {
        Object reservation = entityOne("OrderItemShipGrpInvRes", env.parameters);
        Object inventoryItem = relatedOne(reservation, "InventoryItem", "orderItemShipGrpInvRes");

        if ("SERIALIZED_INV_ITEM".equals(value(inventoryItem, "inventoryItemTypeId"))) {
            entities.set(inventoryItem, "statusId", "INV_AVAILABLE");
            entityManager.remove(reservation);
        } else if ("NON_SERIAL_INV_ITEM".equals(value(inventoryItem, "inventoryItemTypeId"))) {
            Object cancelQuantity = env.parameters.get("cancelQuantity");
            if (empty(cancelQuantity)) {
                cancelQuantity = value(reservation, "quantity");
            }
            Map<String, Object> detail = values(
                    "inventoryItemId", value(inventoryItem, "inventoryItemId"),
                    "orderId", env.parameters.get("orderId"),
                    "orderItemSeqId", env.parameters.get("orderItemSeqId"),
                    "shipGroupSeqId", env.parameters.get("shipGroupSeqId"),
                    "availableToPromiseDiff", cancelQuantity,
                    "workEffortId", env.parameters.get("workEffortId"));
            callProductService(env, "createInventoryItemDetail", detail);

            BigDecimal quantity = decimal(value(reservation, "quantity"));
            BigDecimal cancel = decimal(cancelQuantity);
            if (quantity != null && cancel != null && cancel.compareTo(quantity) < 0) {
                entities.set(reservation, "quantity", subtract(quantity, cancel, INVENTORY_SCALE));
            } else {
                entityManager.remove(reservation);
            }
        }
        return Map.of();
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:462-584. */
    private void reserveForInventoryItemInline(Env env) {
        BigDecimal quantityNotReserved = decimal(env.parameters.get("quantityNotReserved"));
        if (!greaterThanZero(quantityNotReserved)) {
            return;
        }
        if ("SERIALIZED_INV_ITEM".equals(value(env.inventoryItem, "inventoryItemTypeId"))
                && "INV_AVAILABLE".equals(value(env.inventoryItem, "statusId"))) {
            callProductService(env, "updateInventoryItem", values(
                    "inventoryItemId", value(env.inventoryItem, "inventoryItemId"),
                    "statusId", "INV_PROMISED"));
            if (env.orderHeader != null) {
                getPromisedDateTime(env);
                Map<String, Object> reservation = values(
                        "orderId", env.parameters.get("orderId"),
                        "orderItemSeqId", env.parameters.get("orderItemSeqId"),
                        "shipGroupSeqId", env.parameters.get("shipGroupSeqId"),
                        "inventoryItemId", value(env.inventoryItem, "inventoryItemId"),
                        "reserveOrderEnumId", env.parameters.get("reserveOrderEnumId"),
                        "reservedDatetime", env.parameters.get("reservedDatetime"),
                        "promisedDatetime", env.promisedDatetime,
                        "quantity", BigDecimal.ONE);
                putIfPresent(reservation, "sequenceId", env.parameters.get("sequenceId"));
                callProductService(env, "reserveOrderItemInventory", reservation);
            }
            if (env.productionRunTask != null) {
                Map<String, Object> workEffortReservation = values(
                        "workEffortId", env.parameters.get("workEffortId"),
                        "inventoryItemId", value(env.inventoryItem, "inventoryItemId"),
                        "reserveOrderEnumId", env.parameters.get("reserveOrderEnumId"),
                        "reservedDatetime", env.parameters.get("reservedDatetime"),
                        "promisedDatetime", env.promisedDatetime,
                        "quantity", BigDecimal.ONE,
                        "productId", env.parameters.get("productId"));
                putIfPresent(workEffortReservation, "sequenceId", env.parameters.get("sequenceId"));
                reserveWorkEffortInventory(workEffortReservation);
            }
            env.parameters.put("quantityNotReserved",
                    subtract(quantityNotReserved, BigDecimal.ONE, 2));
        }

        if ("NON_SERIAL_INV_ITEM".equals(value(env.inventoryItem, "inventoryItemTypeId"))) {
            Object statusId = value(env.inventoryItem, "statusId");
            BigDecimal available = decimal(value(env.inventoryItem, "availableToPromiseTotal"));
            if (!"INV_NS_ON_HOLD".equals(statusId)
                    && !"INV_NS_DEFECTIVE".equals(statusId)
                    && available != null && available.compareTo(ZERO) > 0) {
                BigDecimal deductAmount = quantityNotReserved.compareTo(available) > 0
                        ? available : quantityNotReserved;
                env.parameters.put("deductAmount", deductAmount);
                callProductService(env, "createInventoryItemDetail", values(
                        "inventoryItemId", value(env.inventoryItem, "inventoryItemId"),
                        "orderId", env.parameters.get("orderId"),
                        "orderItemSeqId", env.parameters.get("orderItemSeqId"),
                        "availableToPromiseDiff", deductAmount.negate().setScale(INVENTORY_SCALE,
                                RoundingMode.HALF_EVEN),
                        "workEffortId", env.parameters.get("workEffortId")));
                if (env.orderHeader != null) {
                    getPromisedDateTime(env);
                    Map<String, Object> reservation = values(
                            "orderId", env.parameters.get("orderId"),
                            "orderItemSeqId", env.parameters.get("orderItemSeqId"),
                            "shipGroupSeqId", env.parameters.get("shipGroupSeqId"),
                            "inventoryItemId", value(env.inventoryItem, "inventoryItemId"),
                            "reserveOrderEnumId", env.parameters.get("reserveOrderEnumId"),
                            "reservedDatetime", env.parameters.get("reservedDatetime"),
                            "quantity", deductAmount,
                            "promisedDatetime", env.promisedDatetime,
                            "priority", env.parameters.get("priority"));
                    putIfPresent(reservation, "sequenceId", env.parameters.get("sequenceId"));
                    callProductService(env, "reserveOrderItemInventory", reservation);
                }
                if (env.productionRunTask != null) {
                    Map<String, Object> workEffortReservation = values(
                            "workEffortId", env.parameters.get("workEffortId"),
                            "inventoryItemId", value(env.inventoryItem, "inventoryItemId"),
                            "reserveOrderEnumId", env.parameters.get("reserveOrderEnumId"),
                            "reservedDatetime", env.parameters.get("reservedDatetime"),
                            "quantity", deductAmount,
                            "promisedDatetime", env.promisedDatetime,
                            "productId", env.parameters.get("productId"));
                    putIfPresent(workEffortReservation, "sequenceId", env.parameters.get("sequenceId"));
                    reserveWorkEffortInventory(workEffortReservation);
                }
                env.parameters.put("quantityNotReserved",
                        subtract(quantityNotReserved, deductAmount, INVENTORY_SCALE));
            }
            env.lastNonSerInventoryItem = env.inventoryItem;
        }
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:586-601. */
    private void getPromisedDateTime(Env env) {
        Object productFacility = relatedOne(env.inventoryItem, "ProductFacility", "inventoryItem");
        setIfNotNull(env, "daysToShip", value(productFacility, "daysToShip"));
        if (empty(env.daysToShip)) {
            setIfNotNull(env, "daysToShip", value(env.facility, "defaultDaysToShip"));
        }
        if (empty(env.daysToShip)) {
            env.daysToShip = 30L;
        }
        Object orderDate = value(env.orderHeader, "orderDate");
        if (orderDate instanceof Timestamp timestamp) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp.getTime());
            calendar.add(Calendar.DAY_OF_YEAR, number(env.daysToShip).intValue());
            env.promisedDatetime = new Timestamp(calendar.getTimeInMillis());
        } else {
            env.promisedDatetime = null;
        }
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:23-348. */
    private void reserveShortageOnExistingItem(Env env, Object inventoryItem, BigDecimal shortage) {
        Map<String, Object> detail = values(
                "inventoryItemId", value(inventoryItem, "inventoryItemId"),
                "orderId", env.parameters.get("orderId"),
                "orderItemSeqId", env.parameters.get("orderItemSeqId"),
                "shipGroupSeqId", env.parameters.get("shipGroupSeqId"),
                "availableToPromiseDiff", shortage.negate().setScale(INVENTORY_SCALE, RoundingMode.HALF_EVEN),
                "workEffortId", env.parameters.get("workEffortId"));
        putIfPresent(detail, "reasonEnumId", env.parameters.get("reserveReasonEnumId"));
        if (empty(env.parameters.get("workEffortId"))) {
            callProductService(env, "createInventoryItemDetail", detail);
        }

        Object productFacility = relatedOne(inventoryItem, "ProductFacility", "lastNonSerInventoryItem");
        env.daysToShip = null;
        setIfNotNull(env, "daysToShip", value(productFacility, "daysToShip"));
        defaultDaysToShip(env);
        if (env.orderHeader != null) {
            adjustPromisedDate(env);
        }
        Map<String, Object> reservation = values(
                "orderId", env.parameters.get("orderId"),
                "orderItemSeqId", env.parameters.get("orderItemSeqId"),
                "shipGroupSeqId", env.parameters.get("shipGroupSeqId"),
                "inventoryItemId", value(inventoryItem, "inventoryItemId"),
                "reserveOrderEnumId", env.parameters.get("reserveOrderEnumId"),
                "quantity", shortage,
                "quantityNotAvailable", shortage,
                "reservedDatetime", env.parameters.get("reservedDatetime"),
                "promisedDatetime", env.promisedDatetime,
                "sequenceId", env.parameters.get("sequenceId"),
                "priority", env.parameters.get("priority"));
        if (env.orderHeader != null) {
            callProductService(env, "reserveOrderItemInventory", reservation);
        }
        if (env.productionRunTask != null) {
            Map<String, Object> workEffortReservation = values(
                    "workEffortId", env.parameters.get("workEffortId"),
                    "inventoryItemId", value(inventoryItem, "inventoryItemId"),
                    "reserveOrderEnumId", env.parameters.get("reserveOrderEnumId"),
                    "quantity", shortage,
                    "quantityNotAvailable", shortage,
                    "reservedDatetime", env.parameters.get("reservedDatetime"),
                    "promisedDatetime", env.promisedDatetime,
                    "sequenceId", env.parameters.get("sequenceId"),
                    "productId", env.parameters.get("productId"));
            reserveWorkEffortInventory(workEffortReservation);
        }
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:23-348. */
    private void createShortageInventoryItem(Env env, BigDecimal shortage) {
        Map<String, Object> createInput = values(
                "productId", env.parameters.get("productId"),
                "facilityId", env.parameters.get("facilityId"),
                "containerId", env.parameters.get("containerId"),
                "inventoryItemTypeId", "NON_SERIAL_INV_ITEM",
                "userLogin", entityOne("UserLogin", values("userLoginId", "system")));
        Map<String, Object> createOutput = super.execute(
                "createInventoryItem", "createInventoryItem", createInput);
        Object inventoryItem = entityOne(
                "InventoryItem", values("inventoryItemId", createOutput.get("inventoryItemId")));
        Map<String, Object> detail = values(
                "inventoryItemId", value(inventoryItem, "inventoryItemId"),
                "orderId", env.parameters.get("orderId"),
                "orderItemSeqId", env.parameters.get("orderItemSeqId"),
                "shipGroupSeqId", env.parameters.get("shipGroupSeqId"),
                "availableToPromiseDiff", shortage.negate().setScale(INVENTORY_SCALE, RoundingMode.HALF_EVEN),
                "workEffortId", env.parameters.get("workEffortId"));
        putIfPresent(detail, "reasonEnumId", env.parameters.get("reserveReasonEnumId"));
        if (empty(env.parameters.get("workEffortId"))) {
            callProductService(env, "createInventoryItemDetail", detail);
        }
        Object productFacility = relatedOne(inventoryItem, "ProductFacility", "newNonSerInventoryItem");
        env.daysToShip = null;
        setIfNotNull(env, "daysToShip", value(productFacility, "daysToShip"));
        defaultDaysToShip(env);
        if (env.orderHeader != null) {
            adjustPromisedDate(env);
        }
        Map<String, Object> reservation = values(
                "orderId", env.parameters.get("orderId"),
                "orderItemSeqId", env.parameters.get("orderItemSeqId"),
                "shipGroupSeqId", env.parameters.get("shipGroupSeqId"),
                "inventoryItemId", value(inventoryItem, "inventoryItemId"),
                "reserveOrderEnumId", env.parameters.get("reserveOrderEnumId"),
                "quantity", shortage,
                "quantityNotAvailable", shortage,
                "reservedDatetime", env.parameters.get("reservedDatetime"),
                "promisedDatetime", env.promisedDatetime,
                "sequenceId", env.parameters.get("sequenceId"),
                "priority", env.parameters.get("priority"));
        if (env.orderHeader != null) {
            callProductService(env, "reserveOrderItemInventory", reservation);
        }
        if (env.productionRunTask != null) {
            Map<String, Object> workEffortReservation = values(
                    "workEffortId", env.parameters.get("workEffortId"),
                    "inventoryItemId", value(inventoryItem, "inventoryItemId"),
                    "reserveOrderEnumId", env.parameters.get("reserveOrderEnumId"),
                    "quantity", shortage,
                    "quantityNotAvailable", shortage,
                    "reservedDatetime", env.parameters.get("reservedDatetime"),
                    "promisedDatetime", env.promisedDatetime,
                    "sequenceId", env.parameters.get("sequenceId"),
                    "productId", env.parameters.get("productId"));
            reserveWorkEffortInventory(workEffortReservation);
        }
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:710-748. */
    private void checkDecomposeInventoryItem(Object inventoryItemId) {
        Object inventoryItem = entityOne("InventoryItem", values("inventoryItemId", inventoryItemId));
        if (inventoryItem == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND,
                    "Error: inventory item with id " + inventoryItemId + " not found.");
        }
        BigDecimal available = decimal(value(inventoryItem, "availableToPromiseTotal"));
        if (available != null && available.compareTo(ZERO) <= 0) {
            return;
        }
        Object product = entityOne("Product", values("productId", value(inventoryItem, "productId")));
        if (product == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND,
                    "Product not found for Product ID " + value(inventoryItem, "productId"));
        }
        Object productTypeId = value(product, "productTypeId");
        while (!empty(productTypeId)) {
            if ("MARKETING_PKG_AUTO".equals(productTypeId)) {
                super.execute("decomposeInventoryItem", "decomposeInventoryItem",
                        values("inventoryItemId", inventoryItemId));
                return;
            }
            Object productType = entityOne("ProductType", values("productTypeId", productTypeId));
            productTypeId = value(productType, "parentTypeId");
        }
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:23-348. */
    private void reserveWorkEffortInventory(Map<String, Object> parameters) {
        Object workEffortId = parameters.get("workEffortId");
        Object inventoryItemId = parameters.get("inventoryItemId");
        Object productId = parameters.get("productId");
        Object inventoryItem = entityOne("InventoryItem", values("inventoryItemId", inventoryItemId));
        if (empty(productId) && inventoryItem != null) {
            productId = value(inventoryItem, "productId");
        }
        if (inventoryItem != null && !empty(productId)
                && !Objects.equals(value(inventoryItem, "productId"), productId)) {
            throw new MigrationException(HttpStatus.BAD_REQUEST,
                    "Product Identity Mismatch: Item [" + inventoryItemId + "] belongs to ["
                            + value(inventoryItem, "productId") + "], but you are reserving for ["
                            + productId + "].");
        }

        Map<String, Object> keys = values(
                "workEffortId", workEffortId,
                "inventoryItemId", inventoryItemId,
                "productId", productId);
        Object reservation = entityOne("WorkEffortInvRes", keys);
        if (reservation == null) {
            reservation = entities.newEntity(entities.entityClass("WorkEffortInvRes"));
            BeanWrapperImpl properties = new BeanWrapperImpl(reservation);
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                if (properties.isWritableProperty(entry.getKey())) {
                    entities.set(reservation, entry.getKey(), entry.getValue());
                }
            }
            entities.set(reservation, "productId", productId);
            entityManager.persist(reservation);
        } else {
            entities.set(reservation, "quantity", add(
                    value(reservation, "quantity"), parameters.get("quantity"), null));
            entities.set(reservation, "quantityNotAvailable", add(
                    value(reservation, "quantityNotAvailable"),
                    parameters.get("quantityNotAvailable"), null));
        }
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:23-348. */
    private List<Object> findInventoryItems(Env env, String locationType, String orderBy) {
        List<String> predicates = new ArrayList<>();
        Map<String, Object> bind = new LinkedHashMap<>();
        if (locationType != null) {
            predicates.add("exists (select fl.facilityId from FacilityLocation fl"
                    + " where fl.facilityId = i.facilityId"
                    + " and fl.locationSeqId = i.locationSeqId"
                    + " and fl.locationTypeEnumId = :locationType)");
            bind.put("locationType", locationType);
        } else {
            predicates.add("i.locationSeqId is null");
        }
        addPredicate(predicates, bind, "i.productId", "productId", env.parameters.get("productId"), false);
        addPredicate(predicates, bind, "i.facilityId", "facilityId", env.parameters.get("facilityId"), true);
        addPredicate(predicates, bind, "i.containerId", "containerId", env.parameters.get("containerId"), true);
        addPredicate(predicates, bind, "i.lotId", "lotId", env.parameters.get("lotId"), true);
        addPredicate(predicates, bind, "i.inventoryItemId", "inventoryItemId",
                env.parameters.get("inventoryItemId"), true);
        predicates.add("i.availableToPromiseTotal > :minimumAvailable");
        bind.put("minimumAvailable", ZERO);
        // EntityFinderUtil.java:247 includes rows with null fields for Minilang not-equals.
        predicates.add("(i.statusId is null or (i.statusId <> :nonSerializedDefective"
                + " and i.statusId <> :defective))");
        bind.put("nonSerializedDefective", "INV_NS_DEFECTIVE");
        bind.put("defective", "INV_DEFECTIVE");
        StringBuilder jpql = new StringBuilder("select i from InventoryItem i where ")
                .append(String.join(" and ", predicates));
        if (orderBy != null && !orderBy.isBlank()) {
            String direction = orderBy.startsWith("-") ? " desc" : " asc";
            String field = orderBy.substring(1);
            if (List.of("unitCost", "expireDate", "datetimeReceived").contains(field)) {
                jpql.append(" order by i.").append(field).append(direction);
            }
        }
        Query query = entityManager.createQuery(jpql.toString());
        bind.forEach(query::setParameter);
        return new ArrayList<>(query.getResultList());
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:23-348. */
    private void defaultDaysToShip(Env env) {
        if (empty(env.daysToShip)) {
            setIfNotNull(env, "daysToShip", value(env.facility, "defaultDaysToShip"));
        }
        if (empty(env.daysToShip)) {
            env.daysToShip = 30L;
        }
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:23-348. */
    private void adjustPromisedDate(Env env) {
        Object orderDate = value(env.orderHeader, "orderDate");
        if (orderDate instanceof Timestamp timestamp) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp.getTime());
            calendar.add(Calendar.DAY_OF_YEAR, number(env.daysToShip).intValue());
            env.promisedDatetime = new Timestamp(calendar.getTimeInMillis());
        }
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:23-348. */
    private String inventoryOrderBy(Object requested, Map<String, Object> parameters) {
        String enumId = text(requested);
        return switch (enumId == null ? "" : enumId) {
            case "INVRO_GUNIT_COST" -> "-unitCost";
            case "INVRO_LUNIT_COST" -> "+unitCost";
            case "INVRO_FIFO_EXP" -> "+expireDate";
            case "INVRO_LIFO_EXP" -> "-expireDate";
            case "INVRO_LIFO_REC" -> "-datetimeReceived";
            default -> {
                parameters.put("reserveOrderEnumId", "INVRO_FIFO_REC");
                yield "+datetimeReceived";
            }
        };
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:350-460. */
    private void callProductService(Env env, String serviceName, Map<String, Object> parameters) {
        ProductServiceMetadata.SimpleServiceMetadata metadata =
                ProductServiceMetadata.simpleServices().get(serviceName);
        if (metadata == null) {
            throw new IllegalStateException("Missing product simple-service metadata for " + serviceName);
        }
        SimpleMethodFile methodFile = switch (serviceName) {
            case "createInventoryItemDetail", "updateInventoryItem" -> inventoryServices;
            default -> this;
        };
        executor.execute(serviceName, metadata, methodFile, parameters);
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:350-460. */
    private List<Object> findByAnd(String entityName, Map<String, ?> filters) {
        StringBuilder jpql = new StringBuilder("select e from ").append(entityName).append(" e");
        List<String> predicates = new ArrayList<>();
        Map<String, Object> bind = new LinkedHashMap<>();
        for (Map.Entry<String, ?> entry : filters.entrySet()) {
            String property = entry.getKey();
            if (entry.getValue() == null) {
                predicates.add("e." + property + " is null");
            } else {
                String parameter = "p" + bind.size();
                predicates.add("e." + property + " = :" + parameter);
                bind.put(parameter, entry.getValue());
            }
        }
        if (!predicates.isEmpty()) {
            jpql.append(" where ").append(String.join(" and ", predicates));
        }
        Query query = entityManager.createQuery(jpql.toString());
        bind.forEach(query::setParameter);
        return new ArrayList<>(query.getResultList());
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:350-460. */
    private Object entityOne(String entityName, Map<String, ?> keys) {
        Class<?> type = entities.entityClass(entityName);
        List<String> primaryKeys = entities.primaryKeys(type);
        if (primaryKeys.stream().anyMatch(key -> empty(keys.get(key)))) {
            return null;
        }
        return entities.findByPrimaryKey(type, keys);
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:23-348. */
    private Object relatedOne(Object entity, String relationName, String valueName) {
        if (entity == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND,
                    "Entity value not found with name: " + valueName);
        }
        return switch (entity.getClass().getSimpleName() + "." + relationName) {
            case "Product.ProductType" -> entityOne(
                    "ProductType", values("productTypeId", value(entity, "productTypeId")));
            case "InventoryItem.ProductFacility" -> entityOne(
                    "ProductFacility", values(
                            "productId", value(entity, "productId"),
                            "facilityId", value(entity, "facilityId")));
            case "OrderItemShipGrpInvRes.InventoryItem" -> entityOne(
                    "InventoryItem", values("inventoryItemId", value(entity, "inventoryItemId")));
            default -> throw new MigrationException(
                    HttpStatus.BAD_REQUEST, "Unknown relation: " + relationName);
        };
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:23-348. */
    private void addPredicate(
            List<String> predicates,
            Map<String, Object> bind,
            String expression,
            String parameter,
            Object value,
            boolean ignoreIfEmpty) {
        if (ignoreIfEmpty && empty(value)) {
            return;
        }
        if (value == null) {
            predicates.add(expression + " is null");
        } else {
            predicates.add(expression + " = :" + parameter);
            bind.put(parameter, value);
        }
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:23-348. */
    private static boolean greaterThanZero(Object value) {
        BigDecimal decimal = decimal(value);
        return decimal != null && decimal.compareTo(ZERO) > 0;
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:23-348. */
    private static boolean empty(Object value) {
        return value == null || value instanceof String text && text.isBlank();
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:23-348. */
    private static BigDecimal decimal(Object value) {
        if (value == null) {
            return null;
        }
        return value instanceof BigDecimal decimal ? decimal : new BigDecimal(value.toString());
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:23-348. */
    private static BigDecimal number(Object value) {
        BigDecimal decimal = decimal(value);
        return decimal == null ? ZERO : decimal;
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:23-348. */
    private static BigDecimal add(Object left, Object right, Integer scale) {
        BigDecimal result = ZERO;
        if (left != null) {
            result = result.add(number(left));
        }
        if (right != null) {
            result = result.add(number(right));
        }
        return scale == null ? result : result.setScale(scale, RoundingMode.HALF_EVEN);
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:23-348. */
    private static BigDecimal subtract(Object left, Object right, int scale) {
        BigDecimal result = number(left);
        if (right != null) {
            result = result.subtract(number(right));
        }
        return result.setScale(scale, RoundingMode.HALF_EVEN);
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:23-348. */
    private Object value(Object entity, String field) {
        return entity == null ? null : entities.get(entity, field);
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:23-348. */
    private String text(Object entity, String field) {
        return Objects.toString(value(entity, field), null);
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:23-348. */
    private static String text(Object value) {
        return Objects.toString(value, null);
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:23-348. */
    private static Map<String, Object> values(Object... pairs) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int index = 0; index < pairs.length; index += 2) {
            putIfPresent(result, (String) pairs[index], pairs[index + 1]);
        }
        return result;
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:350-460. */
    private static Map<String, Object> copyFields(Map<String, Object> source, String... fields) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (String field : fields) {
            putIfPresent(result, field, source.get(field));
        }
        return result;
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:350-460. */
    private Map<String, Object> copyFields(Object source, String... fields) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (String field : fields) {
            putIfPresent(result, field, value(source, field));
        }
        return result;
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:23-348. */
    private static void putIfPresent(Map<String, Object> target, String name, Object value) {
        if (value != null) {
            target.put(name, value);
        }
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:603-634. */
    private static void setIfNotNull(Map<String, Object> target, String name, Object value) {
        if (value != null) {
            target.put(name, value);
        }
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:586-601. */
    private static void setIfNotNull(Env env, String name, Object value) {
        if (value != null) {
            if ("daysToShip".equals(name)) {
                env.daysToShip = value;
            }
        }
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:23-348. */
    private static Map<String, Object> result(String name, Object value) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(name, value);
        return result;
    }

    /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:23-748. */
    private static final class Env {
        private final Map<String, Object> parameters = new LinkedHashMap<>();
        private Object orderHeader;
        private Object productionRunTask;
        private Object facility;
        private Object product;
        private Object inventoryItem;
        private Object lastNonSerInventoryItem;
        private Object daysToShip;
        private Timestamp promisedDatetime;
        private Timestamp nowTimestamp;

        /** applications/product/minilang/product/inventory/InventoryReserveServices.xml:23-748. */
        private Env(Map<String, Object> input) {
            parameters.putAll(input);
            if (input.get("promisedDatetime") instanceof Timestamp timestamp) {
                promisedDatetime = timestamp;
            }
        }
    }
}
