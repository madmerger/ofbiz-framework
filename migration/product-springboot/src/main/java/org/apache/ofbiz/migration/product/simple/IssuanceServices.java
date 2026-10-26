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
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.ofbiz.base.util.ObjectType;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.common.SequenceService;
import org.apache.ofbiz.migration.product.ProductPermissions;
import org.apache.ofbiz.migration.product.adapter.OrderPort;
import org.apache.ofbiz.migration.product.adapter.ProductUnportedPort;
import org.apache.ofbiz.migration.product.generated.ProductServiceMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** Port of applications/product/minilang/shipment/issuance/IssuanceServices.xml. */
@Component
public class IssuanceServices extends UnportedSimpleMethodFile {
    private static final Logger LOGGER = LoggerFactory.getLogger(IssuanceServices.class);
    private static final Pattern LABEL_VARIABLE = Pattern.compile("\\$\\{([^}]+)}");
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final ProductUnportedPort unportedPort;
    private final EntityRegistry entities;
    private final EntityManager entityManager;
    private final SequenceService sequences;
    private final MigrationServiceDispatcher dispatcher;
    private final ProductSimpleServiceExecutor executor;
    private final InventoryServices inventoryServices;
    private final InventoryReserveServices inventoryReserveServices;
    private final OrderPort orderPort;
    private final ProductPermissions permissions;

    public IssuanceServices(
            ProductUnportedPort unportedPort,
            EntityRegistry entities,
            EntityManager entityManager,
            SequenceService sequences,
            MigrationServiceDispatcher dispatcher,
            ProductSimpleServiceExecutor executor,
            InventoryServices inventoryServices,
            InventoryReserveServices inventoryReserveServices,
            OrderPort orderPort,
            ProductPermissions permissions) {
        super(unportedPort);
        this.unportedPort = unportedPort;
        this.entities = entities;
        this.entityManager = entityManager;
        this.sequences = sequences;
        this.dispatcher = dispatcher;
        this.executor = executor;
        this.inventoryServices = inventoryServices;
        this.inventoryReserveServices = inventoryReserveServices;
        this.orderPort = orderPort;
        this.permissions = permissions;
    }

    @Override
    public Map<String, Object> execute(String serviceName, String methodName, Map<String, Object> input) {
        Env env = new Env(input, entities, currentUserLogin());
        return switch (serviceName) {
            case "createItemIssuance" -> createItemIssuance(env);
            case "issueOrderItemToShipment" -> issueOrderItemToShipment(env);
            case "issueOrderItemShipGrpInvResToShipment" -> issueOrderItemShipGrpInvResToShipment(env);
            case "issueInventoryItemToFixedAssetMaint" -> issueInventoryItemToFixedAssetMaint(env);
            case "returnInventoryItemIssuedToFixedAssetMaint" -> returnInventoryItemIssuedToFixedAssetMaint(env);
            case "cancelOrderItemIssuanceFromSalesShipment" -> cancelOrderItemIssuanceFromSalesShipment(env);
            case "issueInventoryItemToShipment" -> issueInventoryItemToShipment(env);
            default -> super.execute(serviceName, methodName, input);
        };
    }

    /** This is not registered; the service definition uses the Groovy method with the same purpose. */
    public Map<String, Object> totalIssuedQuantityForOrderItem(Object orderItem) {
        Map<String, Object> filters = new LinkedHashMap<>();
        filters.put("orderId", get(orderItem, "orderId"));
        filters.put("orderItemSeqId", get(orderItem, "orderItemSeqId"));
        List<?> orderShipments = find("OrderShipment", filters, null);
        BigDecimal total = ZERO;
        if (!orderShipments.isEmpty()) {
            for (Object orderShipment : orderShipments) {
                total = total.add(decimal(get(orderShipment, "quantity")));
            }
        } else {
            for (Object issuance : find("ItemIssuance", filters, null)) {
                total = total.add(decimal(get(issuance, "quantity")));
            }
        }
        return Map.of("totalIssuedQuantity", total);
    }

    /** Port of applications/product/minilang/shipment/issuance/IssuanceServices.xml:24-62. */
    private Map<String, Object> createItemIssuance(Env env) {
        requireShipmentStatusPermission(env, "CREATE", get(env.parameters, "shipmentId"));
        Map<String, Object> issuance = new LinkedHashMap<>(env.parameters);
        issuance.put("itemIssuanceId", sequences.nextId("ItemIssuance"));
        issuance.putIfAbsent("issuedDateTime", new Timestamp(System.currentTimeMillis()));
        Object itemIssuance = newEntity("ItemIssuance");
        setNonPrimaryKeyFields(itemIssuance, issuance);
        entities.set(itemIssuance, "itemIssuanceId", issuance.get("itemIssuanceId"));
        if (get(itemIssuance, "issuedDateTime") == null) {
            entities.set(itemIssuance, "issuedDateTime", new Timestamp(System.currentTimeMillis()));
        }
        entityManager.persist(itemIssuance);
        Object inventoryItem = findPrimaryKey("InventoryItem", Map.of(
                "inventoryItemId", Objects.toString(issuance.get("inventoryItemId"), "")));
        Object product = inventoryItem == null
                ? null
                : findPrimaryKey("Product", Map.of("productId", Objects.toString(get(inventoryItem, "productId"), "")));
        boolean affectAccounting = true;
        if ("SERIALIZED_INV_ITEM".equals(get(inventoryItem, "inventoryItemTypeId"))
                && "SERVICE_PRODUCT".equals(get(product, "productTypeId"))) {
            affectAccounting = false;
            executeSibling("updateInventoryItem", inventoryServices, Map.of(
                    "inventoryItemId", issuance.get("inventoryItemId"),
                    "statusId", "INV_DELIVERED"));
        }
        env.result.put("itemIssuanceId", issuance.get("itemIssuanceId"));
        env.result.put("affectAccounting", affectAccounting);
        return env.result;
    }

    /** Port of applications/product/minilang/shipment/issuance/IssuanceServices.xml:65-89. */
    private Map<String, Object> issueOrderItemToShipment(Env env) {
        requireShipmentStatusPermission(env, "CREATE", get(env.parameters, "shipmentId"));
        env.set("orderHeader", findPrimaryKey("OrderHeader", primaryKeyValues("OrderHeader", env.parameters)));
        if ("SALES_ORDER".equals(get(env.get("orderHeader"), "orderTypeId"))) {
            addError(env, "Not issuing Order Item to shipment [" + text(get(env.parameters, "shipmentId"))
                    + "] because the order is a Sales Order for order ["
                    + text(get(env.get("orderHeader"), "orderId")) + "] order item ["
                    + text(get(env.parameters, "orderItemSeqId"))
                    + "] (should call the issueOrderItemShipGrpInvResToShipment service)");
        }
        checkErrors(env);
        env.set("orderItem", findPrimaryKey("OrderItem", primaryKeyValues("OrderItem", env.parameters)));
        env.set("orderItemShipGroupAssoc", findPrimaryKey(
                "OrderItemShipGroupAssoc", primaryKeyValues("OrderItemShipGroupAssoc", env.parameters)));
        env.set("shipment", findPrimaryKey("Shipment", primaryKeyValues("Shipment", env.parameters)));
        env.set("shipmentItem", null);
        findCreateIssueShipmentItem(env);
        env.result.put("shipmentItemSeqId", get(env.get("shipmentItem"), "shipmentItemSeqId"));
        return env.result;
    }

    /** Port of applications/product/minilang/shipment/issuance/IssuanceServices.xml:91-316. */
    private Map<String, Object> issueOrderItemShipGrpInvResToShipment(Env env) {
        requireShipmentStatusPermission(env, "CREATE", get(env.parameters, "shipmentId"));
        env.set("orderItemShipGrpInvRes", findPrimaryKey(
                "OrderItemShipGrpInvRes", primaryKeyValues("OrderItemShipGrpInvRes", env.parameters)));
        env.set("orderHeader", findPrimaryKey("OrderHeader", primaryKeyValues("OrderHeader", env.parameters)));
        LOGGER.info("Issuing order item ship group inventory reservation to shipment {}",
                get(env.parameters, "shipmentId"));
        if (!"SALES_ORDER".equals(get(env.get("orderHeader"), "orderTypeId"))) {
            addError(env, "Not issuing Order Item Ship Group Inventory Reservation to shipment ["
                    + text(get(env.parameters, "shipmentId")) + "] because the order is not a Sales Order for order ["
                    + text(get(env.get("orderItemShipGrpInvRes"), "orderId")) + "] order item ["
                    + text(get(env.get("orderItemShipGrpInvRes"), "orderItemSeqId")) + "] inventoryItem ["
                    + text(get(env.get("orderItemShipGrpInvRes"), "inventoryItemId"))
                    + "] (should call the issueOrderItemToShipment service)");
        }
        env.set("orderItem", findPrimaryKey("OrderItem", primaryKeyValues(
                "OrderItem", from(env, "orderItemShipGrpInvRes", "orderId", "orderItemSeqId"))));
        env.set("orderItemShipGroupAssoc", findPrimaryKey("OrderItemShipGroupAssoc", primaryKeyValues(
                "OrderItemShipGroupAssoc", from(env, "orderItemShipGrpInvRes", "orderId", "orderItemSeqId",
                        "shipGroupSeqId"))));
        env.set("shipment", findPrimaryKey("Shipment", primaryKeyValues("Shipment", env.parameters)));
        if (isEmpty(get(env.parameters, "quantity"))) {
            addError(env, label("ProductNotIssueOrderToShipment", env));
        }
        if (ordered(env, get(env.parameters, "quantity"), ZERO, "less-equals")) {
            addError(env, label("ProductNotIssueOrderToShipmentQuantityLess", env));
        }
        if (ordered(env, get(env.parameters, "quantity"), get(env.get("orderItemShipGrpInvRes"), "quantity"), "greater")) {
            addError(env, label("ProductNotIssueOrderToShipmentQuantityGreater", env));
        }
        checkShipGroupDestination(env);
        checkErrors(env);

        env.set("inventoryItem", findPrimaryKey("InventoryItem", Map.of(
                "inventoryItemId", Objects.toString(get(env.get("orderItemShipGrpInvRes"), "inventoryItemId"), ""))));
        List<?> orderShipments = find("OrderShipment", primaryKeyValues("OrderShipment", env.parameters), null);
        env.set("orderShipment", first(orderShipments));
        calcQtyForShipmentItemInline(env);
        LOGGER.info("qtyForShipmentItem: {}", get(env, "qtyForShipmentItem"));
        if (ordered(env, get(env, "qtyForShipmentItem"), ZERO, "greater-equals")) {
            if (!isEmpty(get(env.get("orderShipment"), "shipmentItemSeqId"))) {
                env.result.put("shipmentItemSeqId", get(env.get("orderShipment"), "shipmentItemSeqId"));
                env.set("shipmentItem", findPrimaryKey(
                        "ShipmentItem", primaryKeyValues("ShipmentItem", env.resultWithParameters())));
            }
            if (!isZero(get(env, "qtyForShipmentItem"))) {
                Object requestedQuantity = get(env.parameters, "quantity");
                set(env, "parameters.quantity", get(env, "qtyForShipmentItem"));
                findCreateIssueShipmentItem(env);
                set(env, "parameters.quantity", requestedQuantity);
            }
        } else {
            env.set("orderShipment.quantity", calculate(
                    calc(get(env.get("orderShipment"), "quantity"), "subtract", 2, get(env.parameters, "quantity"))));
            store(env.get("orderShipment"));
            env.result.put("shipmentItemSeqId", get(env.get("orderShipment"), "shipmentItemSeqId"));
            env.set("shipmentItem", findPrimaryKey("ShipmentItem", primaryKeyValues(
                    "ShipmentItem", from(env, "orderShipment", "shipmentId", "shipmentItemSeqId"))));
        }

        env.set("eventDate", get(env.parameters, "eventDate"));
        env.set("shipmentId", get(env.parameters, "shipmentId"));
        findCreateItemIssuance(env);
        associateIssueRoles(env);

        env.set("orderItemShipGrpInvRes.quantity", calculate(
                calc(null, "subtract", 2, get(env.get("orderItemShipGrpInvRes"), "quantity"),
                        get(env.parameters, "quantity"))));
        if (isZero(get(env.get("orderItemShipGrpInvRes"), "quantity"))) {
            remove(env.get("orderItemShipGrpInvRes"));
            if (!"SHIPMENT_SCHEDULED".equals(get(env.get("shipment"), "statusId"))) {
                List<?> otherReservations = find("OrderItemShipGrpInvRes", Map.of(
                        "orderId", get(env.get("orderItemShipGrpInvRes"), "orderId"),
                        "orderItemSeqId", get(env.get("orderItemShipGrpInvRes"), "orderItemSeqId")), null);
                if (otherReservations.isEmpty()) {
                    orderPort.invoke("changeOrderItemStatus", Map.of(
                            "orderId", get(env.get("orderItemShipGrpInvRes"), "orderId"),
                            "orderItemSeqId", get(env.get("orderItemShipGrpInvRes"), "orderItemSeqId"),
                            "statusId", "ITEM_COMPLETED"));
                }
            } else {
                LOGGER.info("Items issued but can't set order item status to ITEM_COMPLETED because shipment status is SHIPMENT_SCHEDULED");
            }
        } else {
            store(env.get("orderItemShipGrpInvRes"));
        }

        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("inventoryItemId", get(env.get("orderItemShipGrpInvRes"), "inventoryItemId"));
        detail.put("orderId", get(env.get("orderItemShipGrpInvRes"), "orderId"));
        detail.put("orderItemSeqId", get(env.get("orderItemShipGrpInvRes"), "orderItemSeqId"));
        detail.put("shipGroupSeqId", get(env.get("orderItemShipGrpInvRes"), "shipGroupSeqId"));
        detail.put("shipmentId", get(env.parameters, "shipmentId"));
        detail.put("shipmentItemSeqId", get(env.get("shipmentItem"), "shipmentItemSeqId"));
        detail.put("itemIssuanceId", get(env, "itemIssuanceId"));
        detail.put("quantityOnHandDiff", calc(null, "negative", 6, get(env.parameters, "quantity")));
        executeSibling("createInventoryItemDetail", inventoryServices, detail);
        List<?> remainingReservations = find("OrderItemShipGrpInvRes", Map.of(
                "orderId", get(env.get("orderItemShipGrpInvRes"), "orderId"),
                "orderItemSeqId", get(env.get("orderItemShipGrpInvRes"), "orderItemSeqId"),
                "inventoryItemId", get(env.get("orderItemShipGrpInvRes"), "inventoryItemId")), null);
        for (Object reservation : remainingReservations) {
            cancelReservation(reservation);
        }
        for (Object reservation : remainingReservations) {
            rereserveReservation(env, reservation);
        }
        return env.result;
    }

    /** Port of applications/product/minilang/shipment/issuance/IssuanceServices.xml:320-361. */
    private void calcQtyForShipmentItemInline(Env env) {
        Object otherInventoryItemQuantity = ZERO;
        if (!isEmpty(get(env.parameters, "inventoryItemId"))) {
            List<?> issuances = find("ItemIssuance", Map.of(
                    "orderId", get(env.get("orderItemShipGrpInvRes"), "orderId"),
                    "orderItemSeqId", get(env.get("orderItemShipGrpInvRes"), "orderItemSeqId"),
                    "shipGroupSeqId", get(env.get("orderItemShipGrpInvRes"), "shipGroupSeqId"),
                    "shipmentId", get(env.parameters, "shipmentId")), "-issuedDateTime");
            BigDecimal totalOther = ZERO;
            for (Object issuance : issuances) {
                if (notEqual(get(issuance, "inventoryItemId"), get(env.parameters, "inventoryItemId"))) {
                    totalOther = calculate(calc(totalOther, "add", 2, get(issuance, "quantity")));
                }
            }
            otherInventoryItemQuantity = totalOther;
        }
        env.set("otherInventoryItemQuantity", otherInventoryItemQuantity);
        Object orderShipmentAmount = calculate(calc(
                get(env.get("orderShipment"), "quantity"), "subtract", 2, get(env, "otherInventoryItemQuantity")));
        env.set("orderShipmentAmount", orderShipmentAmount);
        env.set("qtyForShipmentItem", calculate(calc(
                get(env.parameters, "quantity"), "subtract", 2, get(env, "orderShipmentAmount"))));
    }

    /** Port of applications/product/minilang/shipment/issuance/IssuanceServices.xml:363-400. */
    private void findCreateIssueShipmentItem(Env env) {
        List<?> shipmentItems = List.of();
        if (!isEmpty(get(env.get("orderItem"), "productId"))) {
            Map<String, Object> filters = new LinkedHashMap<>();
            filters.put("shipmentId", get(env.parameters, "shipmentId"));
            filters.put("productId", get(env.get("orderItem"), "productId"));
            Object sequence = get(env.parameters, "shipmentItemSeqId");
            if (!isEmpty(sequence)) {
                filters.put("shipmentItemSeqId", sequence);
            }
            shipmentItems = find("ShipmentItem", filters, "shipmentItemSeqId");
        }
        env.set("shipmentItem", shipmentItems.isEmpty() ? null : shipmentItems.get(0));
        if (env.get("shipmentItem") == null) {
            Map<String, Object> createInput = new LinkedHashMap<>();
            createInput.put("shipmentId", get(env.parameters, "shipmentId"));
            createInput.put("productId", get(env.get("orderItem"), "productId"));
            createInput.put("quantity", get(env.parameters, "quantity"));
            Map<String, Object> created = dispatch("createShipmentItem", createInput);
            env.set("parameters.shipmentItemSeqId", created.get("shipmentItemSeqId"));
            env.set("shipmentItem", findPrimaryKey("ShipmentItem", primaryKeyValues(
                    "ShipmentItem", env.resultWithParameters())));
        } else {
            env.set("shipmentItem.quantity", calculate(calc(
                    get(env.get("shipmentItem"), "quantity"), "add", 2, get(env.parameters, "quantity"))));
            store(env.get("shipmentItem"));
        }
        env.set("shipmentItemSeqId", get(env.get("shipmentItem"), "shipmentItemSeqId"));
        createOrUpdateOrderShipmentInline(env);
    }

    /** Port of applications/product/minilang/shipment/issuance/IssuanceServices.xml:402-433. */
    private void createOrUpdateOrderShipmentInline(Env env) {
        Map<String, Object> lookup = new LinkedHashMap<>();
        lookup.put("shipmentId", get(env.parameters, "shipmentId"));
        lookup.put("shipmentItemSeqId", get(env.get("shipmentItem"), "shipmentItemSeqId"));
        lookup.put("orderId", get(env.get("orderItem"), "orderId"));
        lookup.put("orderItemSeqId", get(env.get("orderItem"), "orderItemSeqId"));
        lookup.put("shipGroupSeqId", null);
        if (env.get("orderItemShipGroupAssoc") != null) {
            lookup.put("shipGroupSeqId", get(env.get("orderItemShipGroupAssoc"), "shipGroupSeqId"));
        }
        if (env.get("orderItemShipGrpInvRes") != null) {
            lookup.put("shipGroupSeqId", get(env.get("orderItemShipGrpInvRes"), "shipGroupSeqId"));
        }
        env.set("orderShipmentLookupPk", lookup);
        List<?> orderShipments = find("OrderShipment", lookup, null);
        env.set("orderShipment", orderShipments.isEmpty() ? null : orderShipments.get(0));
        if (env.get("orderShipment") == null) {
            Map<String, Object> fields = new LinkedHashMap<>(lookup);
            fields.put("quantity", get(env.parameters, "quantity"));
            Object orderShipment = newEntity("OrderShipment");
            setFields(orderShipment, fields);
            // OrderShipment is created inline because this call is an entity-auto service in the order component.
            entityManager.persist(orderShipment);
            env.set("orderShipment", orderShipment);
        } else {
            env.set("orderShipment.quantity", calculate(calc(
                    get(env.get("orderShipment"), "quantity"), "add", 2, get(env.parameters, "quantity"))));
            store(env.get("orderShipment"));
        }
    }

    /** Port of applications/product/minilang/shipment/issuance/IssuanceServices.xml:435-479. */
    private void findCreateItemIssuance(Env env) {
        if (!"SALES_ORDER".equals(get(env.get("orderHeader"), "orderTypeId"))) {
            Map<String, Object> lookup = new LinkedHashMap<>();
            lookup.put("orderId", get(env.get("orderItem"), "orderId"));
            lookup.put("orderItemSeqId", get(env.get("orderItem"), "orderItemSeqId"));
            lookup.put("shipmentId", get(env.get("shipmentItem"), "shipmentId"));
            lookup.put("shipmentItemSeqId", get(env.get("shipmentItem"), "shipmentItemSeqId"));
            lookup.put("shipGroupSeqId", get(env.get("orderItemShipGroupAssoc"), "shipGroupSeqId"));
            List<?> issuances = find("ItemIssuance", lookup, "-issuedDateTime");
            Object itemIssuance = first(issuances);
            if (itemIssuance != null) {
                entities.set(itemIssuance, "quantity", decimal(get(itemIssuance, "quantity"))
                        .add(decimal(get(env.parameters, "quantity"))));
                store(itemIssuance);
                env.set("itemIssuanceId", get(itemIssuance, "itemIssuanceId"));
                env.result.put("itemIssuanceId", get(env, "itemIssuanceId"));
                return;
            }
        }
        Map<String, Object> itemIssuanceInput = new LinkedHashMap<>();
        itemIssuanceInput.put("quantity", get(env.parameters, "quantity"));
        itemIssuanceInput.put("shipmentId", get(env.get("shipmentItem"), "shipmentId"));
        itemIssuanceInput.put("shipmentItemSeqId", get(env.get("shipmentItem"), "shipmentItemSeqId"));
        itemIssuanceInput.put("orderId", get(env.get("orderItem"), "orderId"));
        itemIssuanceInput.put("orderItemSeqId", get(env.get("orderItem"), "orderItemSeqId"));
        itemIssuanceInput.put("issuedDateTime", get(env, "eventDate"));
        if (env.get("orderItemShipGrpInvRes") != null) {
            itemIssuanceInput.put("inventoryItemId", get(env.get("orderItemShipGrpInvRes"), "inventoryItemId"));
            itemIssuanceInput.put("shipGroupSeqId", get(env.get("orderItemShipGrpInvRes"), "shipGroupSeqId"));
        }
        if (env.get("orderItemShipGroupAssoc") != null) {
            itemIssuanceInput.put("shipGroupSeqId", get(env.get("orderItemShipGroupAssoc"), "shipGroupSeqId"));
        }
        itemIssuanceInput.put("issuedByUserLoginId", get(env.get("userLogin"), "userLoginId"));
        Object result = callCreateItemIssuance(env, itemIssuanceInput);
        env.set("itemIssuanceId", result);
        env.result.put("itemIssuanceId", result);
    }

    /** Port of applications/product/minilang/shipment/issuance/IssuanceServices.xml:480-503. */
    private void associateIssueRoles(Env env) {
        Object userLogin = env.get("userLogin");
        Object partyId = get(userLogin, "partyId");
        if (partyId == null) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "userLogin has no partyId for issuance role association");
        }
        Object partyRole = findPrimaryKey("PartyRole", Map.of(
                "partyId", partyId, "roleTypeId", "PACKER"));
        if (partyRole == null) {
            partyRole = newEntity("PartyRole");
            entities.set(partyRole, "partyId", partyId);
            entities.set(partyRole, "roleTypeId", "PACKER");
            entityManager.persist(partyRole);
        }
        Map<String, Object> roleKey = Map.of(
                "itemIssuanceId", get(env, "itemIssuanceId"),
                "partyId", partyId,
                "roleTypeId", "PACKER");
        if (findPrimaryKey("ItemIssuanceRole", roleKey) == null) {
            Map<String, Object> roleInput = new LinkedHashMap<>(roleKey);
            roleInput.put("shipmentId", get(env, "shipmentId"));
            dispatch("createItemIssuanceRole", roleInput);
        }
    }

    /** Port of applications/product/minilang/shipment/issuance/IssuanceServices.xml:505-548. */
    private Map<String, Object> issueInventoryItemToFixedAssetMaint(Env env) {
        env.set("inventoryItem", findPrimaryKey("InventoryItem", primaryKeyValues("InventoryItem", env.parameters)));
        env.set("fixedAssetMaint", findPrimaryKey("FixedAssetMaint", primaryKeyValues("FixedAssetMaint", env.parameters)));
        if (ordered(env, get(env.parameters, "quantity"), ZERO, "less-equals")) {
            addError(env, label("ProductNotIssueToFixedAssetMaintQuantityLess", env));
        }
        if (ordered(env, get(env.parameters, "quantity"), get(env.get("inventoryItem"), "availableToPromiseTotal"), "greater")) {
            addError(env, label("ProductNotIssueToFixedAssetMaintQuantityGreater", env));
        }
        checkErrors(env);
        Map<String, Object> issuance = new LinkedHashMap<>();
        issuance.put("inventoryItemId", get(env.get("inventoryItem"), "inventoryItemId"));
        issuance.put("fixedAssetId", get(env.get("fixedAssetMaint"), "fixedAssetId"));
        issuance.put("maintHistSeqId", get(env.get("fixedAssetMaint"), "maintHistSeqId"));
        issuance.put("quantity", get(env.parameters, "quantity"));
        issuance.put("issuedByUserLoginId", get(env.get("userLogin"), "userLoginId"));
        env.result.put("itemIssuanceId", callCreateItemIssuance(env, issuance));
        Map<String, Object> detail = inventoryDetail(env, issuance, true);
        executeSibling("createInventoryItemDetail", inventoryServices, detail);
        return env.result;
    }

    /** Port of applications/product/minilang/shipment/issuance/IssuanceServices.xml:550-570. */
    private Map<String, Object> returnInventoryItemIssuedToFixedAssetMaint(Env env) {
        Object itemIssuance = findPrimaryKey(
                "ItemIssuance", primaryKeyValues("ItemIssuance", env.parameters));
        env.set("itemIssuance", itemIssuance);
        Object oldQuantity = get(itemIssuance, "quantity");
        Map<String, Object> update = new LinkedHashMap<>();
        update.put("itemIssuanceId", get(env.parameters, "itemIssuanceId"));
        update.put("quantity", ZERO);
        dispatch("updateItemIssuance", update);
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("inventoryItemId", get(itemIssuance, "inventoryItemId"));
        detail.put("fixedAssetId", get(itemIssuance, "fixedAssetId"));
        detail.put("maintHistSeqId", get(itemIssuance, "maintHistSeqId"));
        detail.put("quantityOnHandDiff", oldQuantity);
        detail.put("availableToPromiseDiff", oldQuantity);
        detail.put("itemIssuanceId", get(itemIssuance, "itemIssuanceId"));
        executeSibling("createInventoryItemDetail", inventoryServices, detail);
        return env.result;
    }

    /** Port of applications/product/minilang/shipment/issuance/IssuanceServices.xml:572-667. */
    private Map<String, Object> cancelOrderItemIssuanceFromSalesShipment(Env env) {
        Object itemIssuance = findPrimaryKey(
                "ItemIssuance", primaryKeyValues("ItemIssuance", env.parameters));
        env.set("itemIssuance", itemIssuance);
        env.set("orderHeader", findPrimaryKey("OrderHeader", primaryKeyValues(
                "OrderHeader", from(env, "itemIssuance", "orderId"))));
        env.set("shipment", findPrimaryKey("Shipment", primaryKeyValues(
                "Shipment", from(env, "itemIssuance", "shipmentId"))));
        if (!"SHIPMENT_CANCELLED".equals(get(env.get("shipment"), "statusId"))) {
            checkCanChangeShipmentStatusPacked(env, "UPDATE", get(itemIssuance, "shipmentId"));
            // Minilang ignores this call's false hasPermission result because the service returned success.
        }
        if (!"SALES_ORDER".equals(get(env.get("orderHeader"), "orderTypeId"))) {
            addError(env, label("ProductNotIssueOrderForNotSalesOrders", env));
        }
        Object quantityLeft = calculate(calc(
                get(itemIssuance, "quantity"), "subtract", 2, get(itemIssuance, "cancelQuantity")));
        env.set("qtyIssuedLeft", quantityLeft);
        Object toCancel = get(env.parameters, "cancelQuantity");
        if (isEmpty(toCancel)) {
            toCancel = quantityLeft;
        }
        env.set("toCancelQuantity", toCancel);
        if (ordered(env, toCancel, ZERO, "less")) {
            addError(env, label("ProductNotIssueOrderQuantityCancelLess", env));
        }
        if (isZero(toCancel)) {
            // OFBiz OUT validation would reject this success response because canceledQuantity is required.
            return env.result;
        }
        if (ordered(env, toCancel, quantityLeft, "greater")) {
            addError(env, label("ProductNotIssueOrderQuantityCancelGreater", env));
        }
        checkErrors(env);

        Object totalCancelled = calculate(
                calc(get(itemIssuance, "cancelQuantity"), "add", 2),
                calc(toCancel, "add", 2));
        dispatch("updateItemIssuance", Map.of(
                "itemIssuanceId", get(itemIssuance, "itemIssuanceId"),
                "cancelQuantity", totalCancelled));
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("inventoryItemId", get(itemIssuance, "inventoryItemId"));
        detail.put("itemIssuanceId", get(itemIssuance, "itemIssuanceId"));
        detail.put("quantityOnHandDiff", toCancel);
        detail.put("availableToPromiseDiff", toCancel);
        executeSibling("createInventoryItemDetail", inventoryServices, detail);
        Object inventoryItem = findPrimaryKey("InventoryItem", primaryKeyValues(
                "InventoryItem", from(env, "itemIssuance", "inventoryItemId")));
        Map<String, Object> reassign = new LinkedHashMap<>();
        reassign.put("productId", get(inventoryItem, "productId"));
        reassign.put("facilityId", get(inventoryItem, "facilityId"));
        executeSibling("reassignInventoryReservations", inventoryServices, reassign);
        Object productStore = findPrimaryKey("ProductStore", primaryKeyValues(
                "ProductStore", from(env, "orderHeader", "productStoreId")));
        if ("Y".equals(get(productStore, "isImmediatelyFulfilled"))) {
            LOGGER.debug("ProductStore with id {}, is immediately fulfilled. Not reserving inventory",
                    get(productStore, "productStoreId"));
        } else {
            unportedPort.execute("reserveStoreInventory");
        }
        env.result.put("canceledQuantity", toCancel);
        return env.result;
    }

    /** Port of applications/product/minilang/shipment/issuance/IssuanceServices.xml:669-758. */
    private Map<String, Object> issueInventoryItemToShipment(Env env) {
        env.set("inventoryItem", findPrimaryKey("InventoryItem", primaryKeyValues("InventoryItem", env.parameters)));
        env.set("shipmentItem", findPrimaryKey("ShipmentItem", primaryKeyValues("ShipmentItem", env.parameters)));
        if (env.get("inventoryItem") == null) {
            addError(env, label("ProductIssueInventoryItemNotFound", env));
        }
        if (env.get("shipmentItem") == null) {
            addError(env, label("ProductIssueInventoryItemShipmentItemNotFound", env));
        }
        checkErrors(env);
        if (notEqual(get(env.get("inventoryItem"), "productId"), get(env.get("shipmentItem"), "productId"))) {
            addError(env, label("ProductIssueInventoryItemProductMismatch", env));
        }
        env.set("returnItemShipments", find("ReturnItemShipment", Map.of(
                "shipmentId", get(env.parameters, "shipmentId"),
                "shipmentItemSeqId", get(env.parameters, "shipmentItemSeqId")), null));
        env.set("returnItemShipment", first(env.get("returnItemShipments")));
        if (env.get("returnItemShipment") == null) {
            addError(env, label("ProductIssueInventoryItemNoReturnItemShipment", env));
        }
        checkErrors(env);
        List<?> issuances = find("ItemIssuance", Map.of(
                "shipmentId", get(env.parameters, "shipmentId"),
                "shipmentItemSeqId", get(env.parameters, "shipmentItemSeqId")), null);
        BigDecimal totalIssued = ZERO;
        for (Object issuance : issuances) {
            totalIssued = totalIssued.add(decimal(get(issuance, "quantity")));
        }
        Object quantityNotIssued = calculate(calc(
                get(env.get("returnItemShipment"), "quantity"), "subtract", 2, totalIssued));
        env.set("quantityNotIssued", quantityNotIssued);
        if (ordered(env, get(env.parameters, "quantity"), ZERO, "less-equals")) {
            addError(env, label("ProductNotIssueOrderToShipmentQuantityLess", env));
        }
        if (ordered(env, get(env.parameters, "quantity"), get(env.get("inventoryItem"), "availableToPromiseTotal"), "greater")) {
            addError(env, label("ProductNotIssueOrderToShipmentQuantityGreater", env));
        }
        if (ordered(env, get(env.parameters, "quantity"), quantityNotIssued, "greater")) {
            addError(env, label("ProductNotIssueOrderToShipmentQuantityReturnGreater", env));
        }
        checkErrors(env);
        Map<String, Object> issuance = new LinkedHashMap<>();
        issuance.put("inventoryItemId", get(env.get("inventoryItem"), "inventoryItemId"));
        issuance.put("shipmentId", get(env.parameters, "shipmentId"));
        issuance.put("shipmentItemSeqId", get(env.parameters, "shipmentItemSeqId"));
        issuance.put("quantity", get(env.parameters, "quantity"));
        issuance.put("issuedByUserLoginId", get(env.get("userLogin"), "userLoginId"));
        env.result.put("itemIssuanceId", callCreateItemIssuance(env, issuance));
        executeSibling("createInventoryItemDetail", inventoryServices, inventoryDetail(env, issuance, true));
        return env.result;
    }

    private void requireShipmentStatusPermission(Env env, String action, Object shipmentId) {
        permissions.require("FACILITY", action);
        Map<String, Object> permission = checkCanChangeShipmentStatusPacked(env, action, shipmentId);
        if (Boolean.FALSE.equals(permission.get("hasPermission"))
                && permission.get("failMessage") != null
                && shipmentId != null) {
            throw new MigrationException(HttpStatus.CONFLICT, permission.get("failMessage").toString());
        }
    }

    private Map<String, Object> checkCanChangeShipmentStatusPacked(Env env, String mainAction, Object shipmentId) {
        boolean hasPermission = permissions.hasPermission("FACILITY",
                mainAction == null || mainAction.isBlank() ? "UPDATE" : mainAction);
        Object shipment = shipmentId == null ? null
                : findPrimaryKey("Shipment", Map.of("shipmentId", shipmentId));
        String statusId = text(get(shipment, "statusId"));
        String failMessage = null;
        if (List.of("SHIPMENT_PACKED", "SHIPMENT_SHIPPED", "SHIPMENT_DELIVERED", "SHIPMENT_CANCELLED")
                .contains(statusId)) {
            hasPermission = false;
            Object description = get(findPrimaryKey("StatusItem", Map.of("statusId", statusId)), "description");
            failMessage = "Cannot perform this operation when the shipment [" + text(shipmentId)
                    + "] is in the " + (isEmpty(description) ? statusId : description) + " [" + statusId + "] status.";
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("hasPermission", hasPermission);
        result.put("failMessage", failMessage);
        return result;
    }

    private void checkShipGroupDestination(Env env) {
        Object shipment = env.get("shipment");
        Object reservation = env.get("orderItemShipGrpInvRes");
        if (shipment == null || reservation == null || isEmpty(get(shipment, "primaryShipGroupSeqId"))
                || Objects.equals(get(shipment, "primaryOrderId"), get(reservation, "orderId"))
                        && Objects.equals(get(shipment, "primaryShipGroupSeqId"), get(reservation, "shipGroupSeqId"))) {
            return;
        }
        Map<String, Object> orderGroupKey = new LinkedHashMap<>();
        orderGroupKey.put("orderId", get(reservation, "orderId"));
        orderGroupKey.put("shipGroupSeqId", get(reservation, "shipGroupSeqId"));
        Object orderGroup = findPrimaryKey("OrderItemShipGroup", orderGroupKey);
        Map<String, Object> shipmentGroupKey = new LinkedHashMap<>();
        shipmentGroupKey.put("orderId", get(shipment, "primaryOrderId"));
        shipmentGroupKey.put("shipGroupSeqId", get(shipment, "primaryShipGroupSeqId"));
        Object shipmentGroup = findPrimaryKey("OrderItemShipGroup", shipmentGroupKey);
        Object orderAddress = findPrimaryKey("PostalAddress", Map.of(
                "contactMechId", Objects.toString(get(orderGroup, "contactMechId"), "")));
        Object shipmentAddress = findPrimaryKey("PostalAddress", Map.of(
                "contactMechId", Objects.toString(get(shipmentGroup, "contactMechId"), "")));
        if (orderAddress == null || shipmentAddress == null) {
            return;
        }
        if (notEqual(get(orderAddress, "countryGeoId"), get(shipmentAddress, "countryGeoId"))
                || notEqual(get(orderAddress, "stateProvinceGeoId"), get(shipmentAddress, "stateProvinceGeoId"))
                || notEqual(get(orderAddress, "postalCode"), get(shipmentAddress, "postalCode"))) {
            addError(env, label("ProductNotIssueOrderToShipmentShipGroupDestinationMismatch", env));
        }
    }

    private void cancelReservation(Object reservation) {
        Map<String, Object> cancel = new LinkedHashMap<>();
        cancel.put("orderId", get(reservation, "orderId"));
        cancel.put("orderItemSeqId", get(reservation, "orderItemSeqId"));
        cancel.put("shipGroupSeqId", get(reservation, "shipGroupSeqId"));
        cancel.put("inventoryItemId", get(reservation, "inventoryItemId"));
        cancel.put("cancelQuantity", get(reservation, "quantity"));
        executeSibling("cancelOrderItemShipGrpInvRes", inventoryReserveServices, cancel);
    }

    private void rereserveReservation(Env env, Object reservation) {
        Object inventoryItem = findPrimaryKey("InventoryItem", Map.of(
                "inventoryItemId", Objects.toString(get(reservation, "inventoryItemId"), "")));
        Map<String, Object> reserve = new LinkedHashMap<>();
        reserve.put("productId", get(env.get("orderItem"), "productId"));
        reserve.put("facilityId", get(inventoryItem, "facilityId"));
        reserve.put("orderId", get(reservation, "orderId"));
        reserve.put("orderItemSeqId", get(reservation, "orderItemSeqId"));
        reserve.put("shipGroupSeqId", get(reservation, "shipGroupSeqId"));
        reserve.put("quantity", get(reservation, "quantity"));
        reserve.put("requireInventory", "N");
        executeSibling("reserveProductInventoryByFacility", inventoryReserveServices, reserve);
    }

    private Map<String, Object> inventoryDetail(Env env, Map<String, Object> issuance, boolean negative) {
        Object quantity = issuance.get("quantity");
        BigDecimal diff = decimal(quantity).setScale(6, RoundingMode.HALF_EVEN);
        if (negative) {
            diff = diff.negate();
        }
        Map<String, Object> detail = new LinkedHashMap<>();
        for (String field : List.of("inventoryItemId", "orderId", "orderItemSeqId", "shipGroupSeqId",
                "shipmentId", "shipmentItemSeqId", "fixedAssetId", "maintHistSeqId")) {
            if (issuance.containsKey(field)) {
                detail.put(field, issuance.get(field));
            }
        }
        detail.put("itemIssuanceId", env.result.get("itemIssuanceId"));
        detail.put("quantityOnHandDiff", diff);
        detail.put("availableToPromiseDiff", diff);
        return detail;
    }

    private Object callCreateItemIssuance(Env parent, Map<String, Object> input) {
        Env child = new Env(input, entities, parent.get("userLogin"));
        Map<String, Object> result = createItemIssuance(child);
        return result.get("itemIssuanceId");
    }

    private Map<String, Object> currentUserLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userLoginId = authentication == null ? null : authentication.getName();
        Object userLogin = userLoginId == null ? null
                : findPrimaryKey("UserLogin", Map.of("userLoginId", userLoginId));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("userLoginId", userLoginId);
        result.put("partyId", get(userLogin, "partyId"));
        return result;
    }

    private Map<String, Object> dispatch(String serviceName, Map<String, Object> input) {
        return dispatcher.execute("product", serviceName, input);
    }

    private Map<String, Object> executeSibling(
            String serviceName, SimpleMethodFile sibling, Map<String, Object> input) {
        ProductServiceMetadata.SimpleServiceMetadata metadata =
                ProductServiceMetadata.simpleServices().get(serviceName);
        return executor.execute(serviceName, metadata, sibling, input);
    }

    private List<?> find(String entityName, Map<String, ?> filters, String orderBy) {
        @SuppressWarnings("unchecked")
        Class<Object> type = (Class<Object>) entities.entityClass(entityName);
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = builder.createQuery(type);
        Root<Object> root = query.from(type);
        List<Predicate> predicates = new ArrayList<>();
        filters.forEach((field, value) -> predicates.add(value == null
                ? builder.isNull(root.get(field))
                : builder.equal(root.get(field), value)));
        query.select(root).where(predicates.toArray(Predicate[]::new));
        if (orderBy != null && !orderBy.isBlank()) {
            boolean descending = orderBy.startsWith("-");
            String field = descending ? orderBy.substring(1) : orderBy;
            Order order = descending ? builder.desc(root.get(field)) : builder.asc(root.get(field));
            query.orderBy(order);
        }
        return entityManager.createQuery(query).getResultList();
    }

    private Object findPrimaryKey(String entityName, Map<String, ?> values) {
        Class<?> type = entities.entityClass(entityName);
        List<String> primaryKeys = entities.primaryKeys(type);
        if (primaryKeys.isEmpty() || primaryKeys.stream().anyMatch(key -> values.get(key) == null)) {
            return null;
        }
        Map<String, Object> filters = new LinkedHashMap<>();
        for (String key : primaryKeys) {
            filters.put(key, values.get(key));
        }
        List<?> found = find(entityName, filters, null);
        return found.isEmpty() ? null : found.get(0);
    }

    private Map<String, Object> primaryKeyValues(String entityName, Map<String, ?> fields) {
        Map<String, Object> values = new LinkedHashMap<>();
        for (String key : entities.primaryKeys(entities.entityClass(entityName))) {
            values.put(key, fields.get(key));
        }
        return values;
    }

    private Object newEntity(String entityName) {
        return entities.newEntity(entities.entityClass(entityName));
    }

    private void setNonPrimaryKeyFields(Object entity, Map<String, ?> fields) {
        List<String> keys = entities.primaryKeys(entity.getClass());
        for (String field : entities.toMap(entity).keySet()) {
            if (!keys.contains(field) && fields.containsKey(field)) {
                entities.set(entity, field, fields.get(field));
            }
        }
    }

    private void setFields(Object entity, Map<String, ?> fields) {
        for (String field : entities.toMap(entity).keySet()) {
            if (fields.containsKey(field)) {
                entities.set(entity, field, fields.get(field));
            }
        }
    }

    private void store(Object entity) {
        if (entity != null) {
            if (!entityManager.contains(entity)) {
                entityManager.merge(entity);
            }
        }
    }

    private void remove(Object entity) {
        if (entity != null) {
            entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
        }
    }

    private Map<String, Object> from(Env env, String source, String... fields) {
        Map<String, Object> values = new LinkedHashMap<>();
        Object object = env.get(source);
        for (String field : fields) {
            values.put(field, get(object, field));
        }
        return values;
    }

    private BigDecimal calculate(BigDecimal... calcops) {
        BigDecimal result = ZERO.setScale(2, RoundingMode.HALF_EVEN);
        for (BigDecimal calcop : calcops) {
            result = result.add(calcop);
        }
        return result.setScale(2, RoundingMode.HALF_EVEN);
    }

    private BigDecimal calc(Object base, String operator, int scale, Object... nested) {
        BigDecimal result = base == null ? null : decimal(base).setScale(scale, RoundingMode.HALF_EVEN);
        for (Object value : nested) {
            BigDecimal operand = decimal(value).setScale(scale, RoundingMode.HALF_EVEN);
            if (result == null) {
                // A missing field initializes a calcop from its first nested value, without applying its operator.
                result = operand;
            } else {
                result = apply(result, operand, operator, scale);
            }
        }
        if (result == null) {
            result = ZERO.setScale(scale, RoundingMode.HALF_EVEN);
        }
        if ("negative".equals(operator)) {
            result = result.negate();
        }
        return result.setScale(scale, RoundingMode.HALF_EVEN);
    }

    private BigDecimal apply(BigDecimal left, BigDecimal right, String operator, int scale) {
        return switch (operator) {
            case "subtract" -> left.subtract(right);
            case "multiply" -> left.multiply(right);
            case "divide" -> left.divide(right, scale, RoundingMode.HALF_EVEN);
            default -> left.add(right);
        };
    }

    private static BigDecimal decimal(Object value) {
        if (value == null) {
            // Minilang's $bigDecimal expansion resolves a missing or null value as zero.
            return ZERO;
        }
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }
        if (value instanceof Number number) {
            return new BigDecimal(number.toString());
        }
        return new BigDecimal(value.toString());
    }

    private boolean ordered(Env env, Object left, Object right, String operator) {
        if (left == null || right == null) {
            addError(env, left == null ? "Cannot compare: l-value is null" : "Cannot compare: r-value is null");
            return false;
        }
        int comparison;
        if (left instanceof Number || right instanceof Number) {
            comparison = decimal(left).compareTo(decimal(right));
        } else if (left instanceof Comparable<?> && left.getClass().isInstance(right)) {
            @SuppressWarnings("unchecked")
            Comparable<Object> comparable = (Comparable<Object>) left;
            comparison = comparable.compareTo(right);
        } else {
            addError(env, "Cannot compare values: " + left + " and " + right);
            return false;
        }
        return switch (operator) {
            case "greater" -> comparison > 0;
            case "greater-equals" -> comparison >= 0;
            case "less" -> comparison < 0;
            case "less-equals" -> comparison <= 0;
            default -> false;
        };
    }

    private static boolean notEqual(Object left, Object right) {
        if (left == null || right == null) {
            return left != right;
        }
        if (left instanceof Number || right instanceof Number) {
            return decimal(left).compareTo(decimal(right)) != 0;
        }
        return !Objects.equals(left, right);
    }

    private static boolean isEmpty(Object value) {
        return ObjectType.isEmpty(value);
    }

    private static boolean isZero(Object value) {
        return value != null && decimal(value).compareTo(ZERO) == 0;
    }

    private static Object first(Object value) {
        return value instanceof List<?> list && !list.isEmpty() ? list.get(0) : null;
    }

    private void checkErrors(Env env) {
        if (!env.errors.isEmpty()) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, String.join("; ", env.errors));
        }
    }

    private void addError(Env env, String message) {
        env.errors.add(message);
    }

    private String label(String key, Env env) {
        String template = switch (key) {
            case "ProductNotIssueOrderToShipment" ->
                    "Not issuing Order Item Ship Group Inventory Reservation to shipment ${parameters.shipmentId} because the quantity to issue is empty for order ${orderItemShipGrpInvRes.orderId} order item ${orderItemShipGrpInvRes.orderItemSeqId} inventoryItem ${orderItemShipGrpInvRes.inventoryItemId}";
            case "ProductNotIssueOrderToShipmentQuantityLess" ->
                    "Not issuing Order Item Ship Group Inventory Reservation to shipment [${parameters.shipmentId}] because the quantity to issue ${parameters.quantity} is less than or equal to 0 for order ${orderItemShipGrpInvRes.orderId} order item ${orderItemShipGrpInvRes.orderItemSeqId} inventoryItem ${orderItemShipGrpInvRes.inventoryItemId}";
            case "ProductNotIssueOrderToShipmentQuantityGreater" ->
                    "Not issuing Order Item Ship Group Inventory Reservation to shipment ${parameters.shipmentId} because the quantity to issue ${parameters.quantity} is greater than the quantity left to issue ${orderItemShipGrpInvRes.quantity} for order ${orderItemShipGrpInvRes.orderId} order item ${orderItemShipGrpInvRes.orderItemSeqId} inventoryItem ${orderItemShipGrpInvRes.inventoryItemId}";
            case "ProductNotIssueOrderToShipmentQuantityReturnGreater" ->
                    "Not issuing Order Item Ship Group Inventory Reservation to shipment ${parameters.shipmentId} because the quantity to issue ${parameters.quantity} is greater than the quantity left to return ${orderItemShipGrpInvRes.quantity} for order ${orderItemShipGrpInvRes.orderId} order item ${orderItemShipGrpInvRes.orderItemSeqId} inventoryItem ${orderItemShipGrpInvRes.inventoryItemId}";
            case "ProductNotIssueOrderToShipmentShipGroupDestinationMismatch" ->
                    "Not issuing Order Item Ship Group Inventory Reservation to shipment ${parameters.shipmentId} because ship group ${orderItemShipGrpInvRes.shipGroupSeqId} for order ${orderItemShipGrpInvRes.orderId} has a different destination than ship group ${shipment.primaryShipGroupSeqId} already associated with this shipment";
            case "ProductNotIssueToFixedAssetMaintQuantityLess" ->
                    "Not issuing InventoryItem to FixedAssetMaint ${fixedAssetMaint.fixedAssetId:fixedAssetMaint.maintHistSeqId} because the quantity to issue ${parameters.quantity} is less than or equal to 0 for inventoryItem ${inventoryItem.inventoryItemId}";
            case "ProductNotIssueToFixedAssetMaintQuantityGreater" ->
                    "Not issuing InventoryItem to FixedAssetMaint ${fixedAssetMaint.fixedAssetId:fixedAssetMaint.maintHistSeqId} because the quantity to issue ${parameters.quantity} is greater than the quantity left to issue (ie the ATP or Available To Promise) ${intentoryItem.availableToPromiseTotal} for inventoryItem ${orderItemShipGrpInvRes.inventoryItemId}";
            case "ProductNotIssueOrderForNotSalesOrders" ->
                    "Not cancelling ItemIssuance because the order is not a Sales Order";
            case "ProductNotIssueOrderQuantityCancelLess" ->
                    "Not cancelling ItemIssuance ${parameters.itemIssuanceId} because the quantity to cancel ${toCancelQuantity} is less than 0";
            case "ProductNotIssueOrderQuantityCancelGreater" ->
                    "Not cancelling ItemIssuance because the quantity to cancel ${toCancelQuantity} is greater than the quantity left ${qtyIssuedLeft} for ItemIssuance ${parameters.itemIssuanceId}";
            case "ProductIssueInventoryItemNotFound" ->
                    "Not issuing inventory item ${parameters.inventoryItemId} to shipment ${parameters.shipmentId} because the inventory item does not exist";
            case "ProductIssueInventoryItemShipmentItemNotFound" ->
                    "Not issuing inventory item ${parameters.inventoryItemId} to shipment ${parameters.shipmentId} because shipment item ${parameters.shipmentItemSeqId} does not exist";
            case "ProductIssueInventoryItemProductMismatch" ->
                    "Not issuing inventory item ${parameters.inventoryItemId} to shipment ${parameters.shipmentId} shipment item ${parameters.shipmentItemSeqId} because the product of the inventory item ${inventoryItem.productId} is not the product of the shipment item ${shipmentItem.productId}";
            case "ProductIssueInventoryItemNoReturnItemShipment" ->
                    "Not issuing inventory item ${parameters.inventoryItemId} to shipment ${parameters.shipmentId} shipment item ${parameters.shipmentItemSeqId} because the shipment item is not associated with a return item";
            default -> "";
        };
        Matcher matcher = LABEL_VARIABLE.matcher(template);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            Object value = null;
            for (String path : matcher.group(1).split(":")) {
                value = getPath(env, path);
                if (!isEmpty(value)) {
                    break;
                }
            }
            matcher.appendReplacement(result, Matcher.quoteReplacement(value == null ? "" : value.toString()));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private static String text(Object value) {
        return value == null ? "" : value.toString();
    }

    private Object get(Object entity, String field) {
        return get(entities, entity, field);
    }

    private static Object get(EntityRegistry entityRegistry, Object entity, String field) {
        if (entity == null) {
            return null;
        }
        if (entity instanceof Map<?, ?> map) {
            return map.get(field);
        }
        return entityRegistry.get(entity, field);
    }

    private static Object getPath(Env env, String path) {
        String[] parts = path.split("\\.");
        Object current = switch (parts[0]) {
            case "parameters" -> env.parameters;
            case "result" -> env.result;
            default -> env.variables.get(parts[0]);
        };
        for (int index = 1; index < parts.length; index++) {
            current = get(env.entities, current, parts[index]);
        }
        return current;
    }

    private static void set(Env env, String path, Object value) {
        String[] parts = path.split("\\.");
        Object current = switch (parts[0]) {
            case "parameters" -> env.parameters;
            case "result" -> env.result;
            default -> env.variables.computeIfAbsent(parts[0], ignored -> new LinkedHashMap<String, Object>());
        };
        if (parts.length == 1) {
            if (current instanceof Map<?, ?> map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> writable = (Map<String, Object>) map;
                writable.put(parts[0], value);
            } else {
                env.variables.put(parts[0], value);
            }
            return;
        }
        for (int index = 1; index < parts.length - 1; index++) {
            Object next = get(env.entities, current, parts[index]);
            if (next == null) {
                next = new LinkedHashMap<String, Object>();
                setField(current, parts[index], next);
            }
            current = next;
        }
        setField(current, parts[parts.length - 1], value);
    }

    private static void setField(Object object, String field, Object value) {
        if (object instanceof Map<?, ?> map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> writable = (Map<String, Object>) map;
            writable.put(field, value);
        } else if (object != null) {
            throw new IllegalArgumentException("Use EntityRegistry to set entity field " + field);
        }
    }

    private static final class Env {
        private final Map<String, Object> parameters;
        private final Map<String, Object> result = new LinkedHashMap<>();
        private final List<String> errors = new ArrayList<>();
        private final Map<String, Object> variables = new LinkedHashMap<>();
        private final EntityRegistry entities;

        private Env(Map<String, ?> input, EntityRegistry entities, Object userLogin) {
            this.parameters = new LinkedHashMap<>(input);
            this.entities = entities;
            variables.put("userLogin", userLogin);
        }

        private Object get(String name) {
            return getPath(this, name);
        }

        private void set(String path, Object value) {
            if (path.startsWith("parameters.") || path.startsWith("result.")) {
                IssuanceServices.set(this, path, value);
                return;
            }
            if (path.contains(".")) {
                String[] parts = path.split("\\.", 2);
                Object object = variables.get(parts[0]);
                if (object instanceof Map<?, ?> map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> writable = (Map<String, Object>) map;
                    writable.put(parts[1], value);
                } else if (object != null) {
                    entities.set(object, parts[1], value);
                }
                return;
            }
            variables.put(path, value);
        }

        private Map<String, Object> resultWithParameters() {
            Map<String, Object> values = new LinkedHashMap<>(parameters);
            values.putAll(result);
            return values;
        }
    }
}
