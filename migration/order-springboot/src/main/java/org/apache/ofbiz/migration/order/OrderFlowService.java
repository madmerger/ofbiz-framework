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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.apache.ofbiz.migration.order;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.SequenceService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderFlowService {
    private final OrderEntitySupport data;
    private final SequenceService sequences;
    private final ProductPricePort productPrices;
    private final ShipmentPort shipments;
    private final InvoicePort invoices;
    private final OrderStatusService orderStatuses;
    private final OrderItemStatusService itemStatuses;

    public OrderFlowService(
            OrderEntitySupport data,
            SequenceService sequences,
            ProductPricePort productPrices,
            ShipmentPort shipments,
            InvoicePort invoices,
            OrderStatusService orderStatuses,
            OrderItemStatusService itemStatuses) {
        this.data = data;
        this.sequences = sequences;
        this.productPrices = productPrices;
        this.shipments = shipments;
        this.invoices = invoices;
        this.orderStatuses = orderStatuses;
        this.itemStatuses = itemStatuses;
    }

    public Map<String, Object> storeOrder(Map<String, Object> input) {
        String partyId = requiredText(input, "partyId");
        String orderTypeId = requiredText(input, "orderTypeId");
        if (!List.of("SALES_ORDER", "PURCHASE_ORDER").contains(orderTypeId)) {
            throw badRequest("orderTypeId は SALES_ORDER または PURCHASE_ORDER が必要です");
        }
        data.require("Party", Map.of("partyId", partyId));
        data.require("OrderType", Map.of("orderTypeId", orderTypeId));
        Object productStore = optionalEntity("ProductStore", text(input.get("productStoreId")), "productStoreId");
        String currencyUom = text(input.getOrDefault("currencyUom", "USD"));
        List<Map<String, Object>> items = itemInputs(input.get("orderItems"));
        if (items.isEmpty()) {
            throw badRequest("orderItems は1件以上必要です");
        }
        List<OrderLine> lines = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO;
        for (Map<String, Object> item : items) {
            String productId = requiredText(item, "productId");
            data.require("Product", Map.of("productId", productId));
            BigDecimal quantity = decimal(item.get("quantity"), "quantity");
            if (quantity.signum() <= 0) {
                throw badRequest("quantity は0より大きい値が必要です");
            }
            BigDecimal unitPrice = item.containsKey("unitPrice")
                    ? decimal(item.get("unitPrice"), "unitPrice")
                    : productPrices.getUnitPrice(productId, currencyUom, "PURCHASE")
                            .orElseThrow(() -> badRequest("商品価格がありません: " + productId));
            lines.add(new OrderLine(productId, quantity, unitPrice, text(item.get("itemDescription"))));
            grandTotal = grandTotal.add(quantity.multiply(unitPrice));
        }

        Timestamp now = Timestamp.from(Instant.now());
        String orderId = sequences.nextId("OrderHeader");
        String userLoginId = currentUser();
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("orderId", orderId);
        header.put("orderTypeId", orderTypeId);
        header.put("orderName", input.get("orderName"));
        header.put("orderDate", now);
        header.put("entryDate", now);
        header.put("statusId", "ORDER_CREATED");
        header.put("createdBy", userLoginId);
        header.put("currencyUom", currencyUom);
        header.put("productStoreId", input.get("productStoreId"));
        header.put("grandTotal", grandTotal);
        header.put("remainingSubTotal", grandTotal);
        data.create("OrderHeader", header);

        String shipGroupSeqId = "00001";
        data.create("OrderItemShipGroup", Map.of(
                "orderId", orderId,
                "shipGroupSeqId", shipGroupSeqId));
        int sequence = 0;
        for (OrderLine line : lines) {
            String orderItemSeqId = String.format("%05d", ++sequence);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("orderId", orderId);
            item.put("orderItemSeqId", orderItemSeqId);
            item.put("orderItemTypeId", "PRODUCT_ORDER_ITEM");
            item.put("productId", line.productId());
            item.put("quantity", line.quantity());
            item.put("cancelQuantity", BigDecimal.ZERO);
            item.put("unitPrice", line.unitPrice());
            item.put("unitListPrice", line.unitPrice());
            item.put("itemDescription", line.itemDescription());
            item.put("isPromo", "N");
            item.put("statusId", "ITEM_CREATED");
            item.put("changeByUserLoginId", userLoginId);
            data.create("OrderItem", item);
            data.create("OrderItemShipGroupAssoc", Map.of(
                    "orderId", orderId,
                    "orderItemSeqId", orderItemSeqId,
                    "shipGroupSeqId", shipGroupSeqId,
                    "quantity", line.quantity()));
            orderStatuses.recordInitialStatus(orderId, orderItemSeqId, "ITEM_CREATED");
        }
        orderStatuses.recordInitialStatus(orderId, null, "ORDER_CREATED");
        createOrderRoles(orderId, orderTypeId, partyId, input, productStore);
        createOrderContactMechs(orderId, input.get("orderContactMechs"));

        Map<String, Object> output = new LinkedHashMap<>();
        output.put("orderId", orderId);
        output.put("grandTotal", grandTotal);
        output.put("statusId", "ORDER_CREATED");
        return output;
    }

    public Map<String, Object> changeOrderStatus(Map<String, Object> input) {
        String orderId = requiredText(input, "orderId");
        String statusId = requiredText(input, "statusId");
        return orderStatuses.changeHeaderStatus(
                orderId, statusId, "Y".equalsIgnoreCase(text(input.get("setItemStatus"))), text(input.get("changeReason")));
    }

    public Map<String, Object> changeOrderItemStatus(Map<String, Object> input) {
        return itemStatuses.changeItemStatus(
                requiredText(input, "orderId"),
                text(input.get("orderItemSeqId")),
                text(input.get("fromStatusId")),
                requiredText(input, "statusId"),
                text(input.get("changeReason")));
    }

    public Map<String, Object> cancelOrderItem(Map<String, Object> input) {
        return cancelOrderItems(input);
    }

    public Map<String, Object> cancelOrderItemNoActions(Map<String, Object> input) {
        return cancelOrderItems(input);
    }

    public Map<String, Object> quickShipEntireOrder(Map<String, Object> input) {
        String orderId = requiredText(input, "orderId");
        Object order = data.require("OrderHeader", Map.of("orderId", orderId));
        if (!"ORDER_APPROVED".equals(data.get(order, "statusId"))) {
            throw new MigrationException(HttpStatus.CONFLICT, "出荷には承認済み注文が必要です");
        }
        List<?> activeItems = data.find("OrderItem", Map.of("orderId", orderId)).stream()
                .filter(this::hasRemainingQuantity)
                .filter(item -> !"ITEM_CANCELLED".equals(data.get(item, "statusId")))
                .toList();
        if (activeItems.isEmpty() || activeItems.stream()
                .anyMatch(item -> !"ITEM_APPROVED".equals(data.get(item, "statusId")))) {
            throw new MigrationException(HttpStatus.CONFLICT, "出荷には全明細の承認が必要です");
        }
        List<String> shipmentIds = shipments.quickShipEntireOrder(orderId, currentUser());
        itemStatuses.changeItemStatus(orderId, null, null, "ITEM_COMPLETED", null);
        orderStatuses.changeHeaderStatus(orderId, "ORDER_COMPLETED", false, null);
        return Map.of("orderId", orderId, "shipmentIds", shipmentIds, "statusId", "ORDER_COMPLETED");
    }

    public Map<String, Object> createInvoiceForOrder(Map<String, Object> input) {
        String orderId = requiredText(input, "orderId");
        Object order = data.require("OrderHeader", Map.of("orderId", orderId));
        String statusId = text(data.get(order, "statusId"));
        if (!"ORDER_APPROVED".equals(statusId) && !"ORDER_COMPLETED".equals(statusId)) {
            throw new MigrationException(HttpStatus.CONFLICT, "請求書作成には承認済みまたは完了済み注文が必要です");
        }
        boolean unbilled = data.find("OrderItem", Map.of("orderId", orderId)).stream()
                .filter(this::hasRemainingQuantity)
                .filter(item -> !"ITEM_CANCELLED".equals(data.get(item, "statusId")))
                .anyMatch(item -> data.find("OrderItemBilling", Map.of(
                        "orderId", orderId,
                        "orderItemSeqId", data.get(item, "orderItemSeqId"))).isEmpty());
        if (!unbilled) {
            throw new MigrationException(HttpStatus.CONFLICT, "未請求の注文明細がありません");
        }
        return Map.of("orderId", orderId, "invoiceId", invoices.createInvoiceForOrder(orderId));
    }

    private boolean hasRemainingQuantity(Object item) {
        BigDecimal quantity = decimal(data.get(item, "quantity"), "quantity");
        BigDecimal cancelled = data.get(item, "cancelQuantity") == null
                ? BigDecimal.ZERO : decimal(data.get(item, "cancelQuantity"), "cancelQuantity");
        return quantity.subtract(cancelled).signum() > 0;
    }

    private Map<String, Object> cancelOrderItems(Map<String, Object> input) {
        String orderId = requiredText(input, "orderId");
        data.require("OrderHeader", Map.of("orderId", orderId));
        String itemSeqId = text(input.get("orderItemSeqId"));
        Optional<BigDecimal> requested = input.containsKey("cancelQuantity")
                ? Optional.of(decimal(input.get("cancelQuantity"), "cancelQuantity"))
                : Optional.empty();
        List<?> items = data.find("OrderItem", Map.of("orderId", orderId));
        if (itemSeqId != null) {
            items = items.stream()
                    .filter(item -> itemSeqId.equals(text(data.get(item, "orderItemSeqId"))))
                    .toList();
            if (items.isEmpty()) {
                throw new MigrationException(HttpStatus.NOT_FOUND, "注文明細が見つかりません");
            }
        }
        List<String> cancelledItems = new ArrayList<>();
        for (Object item : items) {
            String currentStatus = text(data.get(item, "statusId"));
            if ("ITEM_COMPLETED".equals(currentStatus) || "ITEM_CANCELLED".equals(currentStatus)) {
                continue;
            }
            BigDecimal quantity = decimal(data.get(item, "quantity"), "quantity");
            BigDecimal alreadyCancelled = decimal(data.get(item, "cancelQuantity"), "cancelQuantity");
            BigDecimal remaining = quantity.subtract(alreadyCancelled);
            BigDecimal cancelQuantity = requested.orElse(remaining);
            if (cancelQuantity.signum() <= 0 || cancelQuantity.compareTo(remaining) > 0) {
                throw badRequest("キャンセル数量が残数量を超えています");
            }
            applyShipGroupCancellation(
                    orderId, text(data.get(item, "orderItemSeqId")), cancelQuantity,
                    text(input.get("shipGroupSeqId")));
            BigDecimal updatedCancelled = alreadyCancelled.add(cancelQuantity);
            data.set(item, "cancelQuantity", updatedCancelled);
            if (updatedCancelled.compareTo(quantity) >= 0) {
                itemStatuses.changeItemStatus(
                        orderId, text(data.get(item, "orderItemSeqId")), currentStatus, "ITEM_CANCELLED",
                        text(input.get("changeReason")));
            }
            cancelledItems.add(text(data.get(item, "orderItemSeqId")));
        }
        List<?> allItems = data.find("OrderItem", Map.of("orderId", orderId));
        boolean fullyCancelled = !allItems.isEmpty() && allItems.stream().allMatch(item ->
                decimal(data.get(item, "cancelQuantity"), "cancelQuantity")
                        .compareTo(decimal(data.get(item, "quantity"), "quantity")) >= 0);
        if (fullyCancelled) {
            orderStatuses.changeHeaderStatus(orderId, "ORDER_CANCELLED", false, text(input.get("changeReason")));
        }
        return Map.of("orderId", orderId, "cancelledOrderItemSeqIds", cancelledItems);
    }

    private void applyShipGroupCancellation(
            String orderId, String itemSeqId, BigDecimal cancelQuantity, String requestedGroup) {
        List<?> associations = data.find("OrderItemShipGroupAssoc", Map.of(
                "orderId", orderId, "orderItemSeqId", itemSeqId)).stream()
                .sorted(java.util.Comparator.comparing(association ->
                        text(data.get(association, "shipGroupSeqId"))))
                .toList();
        if (requestedGroup != null) {
            associations = associations.stream()
                    .filter(association -> requestedGroup.equals(text(data.get(association, "shipGroupSeqId"))))
                    .toList();
        }
        if (associations.isEmpty()) {
            throw badRequest("配送グループの明細が見つかりません");
        }
        BigDecimal remainingToCancel = cancelQuantity;
        for (Object association : associations) {
            BigDecimal groupQuantity = decimal(data.get(association, "quantity"), "quantity");
            BigDecimal groupCancelled = data.get(association, "cancelQuantity") == null
                    ? BigDecimal.ZERO : decimal(data.get(association, "cancelQuantity"), "cancelQuantity");
            BigDecimal available = groupQuantity.subtract(groupCancelled);
            if (available.signum() <= 0) {
                continue;
            }
            BigDecimal cancelledHere = available.min(remainingToCancel);
            data.set(association, "cancelQuantity", groupCancelled.add(cancelledHere));
            remainingToCancel = remainingToCancel.subtract(cancelledHere);
            if (remainingToCancel.signum() == 0) {
                return;
            }
        }
        throw badRequest("配送グループの残数量を超えています");
    }

    private void createOrderRoles(
            String orderId, String orderTypeId, String partyId, Map<String, Object> input, Object productStore) {
        if ("SALES_ORDER".equals(orderTypeId)) {
            createRole(orderId, text(input.getOrDefault("billToCustomerPartyId", partyId)), "BILL_TO_CUSTOMER");
            createRole(orderId, text(input.getOrDefault("placingCustomerPartyId", partyId)), "PLACING_CUSTOMER");
            createRole(orderId, text(input.getOrDefault("shipToCustomerPartyId", partyId)), "SHIP_TO_CUSTOMER");
            createRole(orderId, text(input.getOrDefault("endUserCustomerPartyId", partyId)), "END_USER_CUSTOMER");
        }
        String vendorId = text(input.get("billFromVendorPartyId"));
        if (vendorId == null && productStore != null) {
            vendorId = text(data.get(productStore, "payToPartyId"));
        }
        if (vendorId != null) {
            createRole(orderId, vendorId, "BILL_FROM_VENDOR");
        }
        createRole(orderId, text(input.get("shipFromVendorPartyId")), "SHIP_FROM_VENDOR");
        createRole(orderId, text(input.get("supplierAgentPartyId")), "SUPPLIER_AGENT");
    }

    private void createRole(String orderId, String partyId, String roleTypeId) {
        if (partyId != null) {
            data.create("OrderRole", Map.of(
                    "orderId", orderId,
                    "partyId", partyId,
                    "roleTypeId", roleTypeId));
        }
    }

    private void createOrderContactMechs(String orderId, Object rawContactMechs) {
        if (!(rawContactMechs instanceof List<?> contactMechs)) {
            return;
        }
        for (Object value : contactMechs) {
            if (value instanceof Map<?, ?> contact) {
                String purpose = text(contact.get("contactMechPurposeTypeId"));
                String contactMechId = text(contact.get("contactMechId"));
                if (purpose != null && contactMechId != null) {
                    data.create("OrderContactMech", Map.of(
                            "orderId", orderId,
                            "contactMechPurposeTypeId", purpose,
                            "contactMechId", contactMechId));
                }
            }
        }
    }

    private Object optionalEntity(String entityName, String value, String key) {
        return value == null ? null : data.require(entityName, Map.of(key, value));
    }

    private static List<Map<String, Object>> itemInputs(Object value) {
        if (!(value instanceof List<?> list)) {
            return List.of();
        }
        List<Map<String, Object>> items = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof Map<?, ?> values) {
                Map<String, Object> converted = new LinkedHashMap<>();
                values.forEach((key, fieldValue) -> converted.put(key.toString(), fieldValue));
                items.add(converted);
            } else {
                throw badRequest("orderItems の要素はオブジェクトである必要があります");
            }
        }
        return items;
    }

    private static String requiredText(Map<String, ?> input, String name) {
        String value = text(input.get(name));
        if (value == null || value.isBlank()) {
            throw badRequest("必須項目がありません: " + name);
        }
        return value;
    }

    private static BigDecimal decimal(Object value, String name) {
        if (value == null || value.toString().isBlank()) {
            throw badRequest("必須項目がありません: " + name);
        }
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException exception) {
            throw badRequest("数値が不正です: " + name);
        }
    }

    private static String text(Object value) {
        return value == null || value.toString().isBlank() ? null : value.toString();
    }

    private static String currentUser() {
        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication();
        return authentication == null ? "system" : authentication.getName();
    }

    private static MigrationException badRequest(String message) {
        return new MigrationException(HttpStatus.BAD_REQUEST, message);
    }

    private record OrderLine(String productId, BigDecimal quantity, BigDecimal unitPrice, String itemDescription) { }
}
