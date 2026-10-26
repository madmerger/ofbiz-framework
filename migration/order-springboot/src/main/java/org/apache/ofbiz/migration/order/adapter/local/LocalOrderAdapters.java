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
package org.apache.ofbiz.migration.order.adapter.local;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.ofbiz.migration.common.SequenceService;
import org.apache.ofbiz.migration.order.CommunicationEventPort;
import org.apache.ofbiz.migration.order.InvoicePort;
import org.apache.ofbiz.migration.order.OrderEntitySupport;
import org.apache.ofbiz.migration.order.ProductPricePort;
import org.apache.ofbiz.migration.order.ShipmentPort;
import org.apache.ofbiz.migration.order.ShipmentReceiptPort;
import org.springframework.stereotype.Component;

@Component
public class LocalOrderAdapters implements
        ProductPricePort, ShipmentPort, InvoicePort, ShipmentReceiptPort, CommunicationEventPort {
    private final OrderEntitySupport data;
    private final SequenceService sequences;

    public LocalOrderAdapters(OrderEntitySupport data, SequenceService sequences) {
        this.data = data;
        this.sequences = sequences;
    }

    @Override
    public Optional<BigDecimal> getUnitPrice(
            String productId, String currencyUomId, String productPricePurposeId) {
        List<?> prices = data.find("ProductPrice", Map.of(
                "productId", productId,
                "productPriceTypeId", "DEFAULT_PRICE",
                "productPricePurposeId", productPricePurposeId,
                "currencyUomId", currencyUomId));
        Timestamp now = Timestamp.from(Instant.now());
        return prices.stream()
                .filter(price -> {
                    Object fromDate = data.get(price, "fromDate");
                    Object thruDate = data.get(price, "thruDate");
                    return (fromDate == null || !((Timestamp) fromDate).after(now))
                            && (thruDate == null || ((Timestamp) thruDate).after(now));
                })
                .max(Comparator.comparing(price -> {
                    Object fromDate = data.get(price, "fromDate");
                    return fromDate == null ? Timestamp.from(Instant.EPOCH) : (Timestamp) fromDate;
                }))
                .map(price -> new BigDecimal(data.get(price, "price").toString()));
    }

    @Override
    public List<String> quickShipEntireOrder(String orderId, String userLoginId) {
        data.require("OrderHeader", Map.of("orderId", orderId));
        Map<String, Object> itemsBySequence = new LinkedHashMap<>();
        for (Object item : data.find("OrderItem", Map.of("orderId", orderId))) {
            itemsBySequence.put(data.get(item, "orderItemSeqId").toString(), item);
        }
        Map<String, List<Object>> associationsByItem = new LinkedHashMap<>();
        for (Object association : data.find("OrderItemShipGroupAssoc", Map.of("orderId", orderId))) {
            String itemSequence = data.get(association, "orderItemSeqId").toString();
            associationsByItem.computeIfAbsent(itemSequence, ignored -> new ArrayList<>()).add(association);
        }
        Map<String, List<ShipmentLine>> linesByGroup = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : itemsBySequence.entrySet()) {
            Object item = entry.getValue();
            if ("ITEM_CANCELLED".equals(data.get(item, "statusId")) || remainingQuantity(item).signum() <= 0) {
                continue;
            }
            List<Object> associations = associationsByItem.getOrDefault(entry.getKey(), List.of());
            if (associations.isEmpty()) {
                linesByGroup.computeIfAbsent("00001", ignored -> new ArrayList<>())
                        .add(new ShipmentLine(item, remainingQuantity(item)));
                continue;
            }
            for (Object association : associations) {
                BigDecimal quantity = decimal(data.get(association, "quantity"))
                        .subtract(decimal(data.get(association, "cancelQuantity")));
                if (quantity.signum() > 0) {
                    String group = data.get(association, "shipGroupSeqId").toString();
                    linesByGroup.computeIfAbsent(group, ignored -> new ArrayList<>())
                            .add(new ShipmentLine(item, quantity));
                }
            }
        }
        List<String> shipmentIds = new ArrayList<>();
        for (Map.Entry<String, List<ShipmentLine>> group : linesByGroup.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()).toList()) {
            String shipmentId = sequences.nextId("Shipment");
            Map<String, Object> shipment = new LinkedHashMap<>();
            shipment.put("shipmentId", shipmentId);
            shipment.put("shipmentTypeId", "SALES_SHIPMENT");
            shipment.put("statusId", "SHIPMENT_SHIPPED");
            shipment.put("primaryOrderId", orderId);
            shipment.put("primaryShipGroupSeqId", group.getKey());
            shipment.put("createdDate", Timestamp.from(Instant.now()));
            shipment.put("createdByUserLoginId", userLoginId);
            data.create("Shipment", shipment);
            shipmentIds.add(shipmentId);
            int nextItem = 0;
            for (ShipmentLine line : group.getValue()) {
                Object orderItem = line.orderItem();
                String orderItemSeqId = data.get(orderItem, "orderItemSeqId").toString();
                String shipmentItemSeqId = String.format("%05d", ++nextItem);
                Map<String, Object> shipmentItem = new LinkedHashMap<>();
                shipmentItem.put("shipmentId", shipmentId);
                shipmentItem.put("shipmentItemSeqId", shipmentItemSeqId);
                shipmentItem.put("productId", data.get(orderItem, "productId"));
                shipmentItem.put("quantity", line.quantity());
                data.create("ShipmentItem", shipmentItem);

                Map<String, Object> orderShipment = new LinkedHashMap<>();
                orderShipment.put("orderId", orderId);
                orderShipment.put("orderItemSeqId", orderItemSeqId);
                orderShipment.put("shipGroupSeqId", group.getKey());
                orderShipment.put("shipmentId", shipmentId);
                orderShipment.put("shipmentItemSeqId", shipmentItemSeqId);
                orderShipment.put("quantity", line.quantity());
                data.create("OrderShipment", orderShipment);
            }
        }
        return shipmentIds;
    }

    @Override
    public String createInvoiceForOrder(String orderId) {
        Object order = data.require("OrderHeader", Map.of("orderId", orderId));
        String invoiceId = sequences.nextId("Invoice");
        Object billFrom = roleParty(orderId, "BILL_FROM_VENDOR");
        Object billTo = roleParty(orderId, "BILL_TO_CUSTOMER");
        Map<String, Object> invoice = new LinkedHashMap<>();
        invoice.put("invoiceId", invoiceId);
        invoice.put("invoiceTypeId", "SALES_INVOICE");
        invoice.put("statusId", "INVOICE_IN_PROCESS");
        invoice.put("partyIdFrom", billFrom);
        invoice.put("partyId", billTo == null ? data.get(order, "partyId") : billTo);
        invoice.put("currencyUomId", data.get(order, "currencyUom"));
        invoice.put("invoiceDate", Timestamp.from(Instant.now()));
        data.create("Invoice", invoice);

        int nextItem = 0;
        for (Object orderItem : data.find("OrderItem", Map.of("orderId", orderId))) {
            if ("ITEM_CANCELLED".equals(data.get(orderItem, "statusId"))
                    || remainingQuantity(orderItem).signum() <= 0) {
                continue;
            }
            String orderItemSeqId = data.get(orderItem, "orderItemSeqId").toString();
            if (!data.find("OrderItemBilling", Map.of(
                    "orderId", orderId, "orderItemSeqId", orderItemSeqId)).isEmpty()) {
                continue;
            }
            String invoiceItemSeqId = String.format("%05d", ++nextItem);
            BigDecimal quantity = remainingQuantity(orderItem);
            BigDecimal unitPrice = decimal(data.get(orderItem, "unitPrice"));
            Map<String, Object> invoiceItem = new LinkedHashMap<>();
            invoiceItem.put("invoiceId", invoiceId);
            invoiceItem.put("invoiceItemSeqId", invoiceItemSeqId);
            invoiceItem.put("invoiceItemTypeId", "INV_FPROD_ITEM");
            invoiceItem.put("productId", data.get(orderItem, "productId"));
            invoiceItem.put("quantity", quantity);
            invoiceItem.put("amount", unitPrice);
            data.create("InvoiceItem", invoiceItem);

            Map<String, Object> itemBilling = new LinkedHashMap<>();
            itemBilling.put("invoiceId", invoiceId);
            itemBilling.put("invoiceItemSeqId", invoiceItemSeqId);
            itemBilling.put("orderId", orderId);
            itemBilling.put("orderItemSeqId", orderItemSeqId);
            itemBilling.put("quantity", quantity);
            itemBilling.put("amount", unitPrice.multiply(quantity));
            data.create("OrderItemBilling", itemBilling);
        }
        return invoiceId;
    }

    @Override
    public List<Map<String, Object>> findForOrder(String orderId) {
        return data.find("ShipmentReceipt", Map.of("orderId", orderId)).stream()
                .map(data::toMap)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Map<String, Object>> findById(String communicationEventId) {
        return data.find("CommunicationEvent", Map.of("communicationEventId", communicationEventId)).stream()
                .findFirst()
                .map(data::toMap);
    }

    @Override
    public List<Map<String, Object>> contentAssociations(String communicationEventId) {
        return data.find("CommEventContentAssoc", Map.of("communicationEventId", communicationEventId)).stream()
                .map(data::toMap)
                .collect(Collectors.toList());
    }

    @Override
    public void markCompleted(String communicationEventId, String userLoginId) {
        data.find("CommunicationEvent", Map.of("communicationEventId", communicationEventId)).stream()
                .findFirst()
                .ifPresent(event -> {
                    data.set(event, "statusId", "COM_COMPLETE");
                    data.set(event, "lastModifiedByUserLogin", userLoginId);
                    data.set(event, "lastModifiedDate", Timestamp.from(Instant.now()));
                });
    }

    private Object roleParty(String orderId, String roleTypeId) {
        List<?> roles = data.find("OrderRole", Map.of("orderId", orderId, "roleTypeId", roleTypeId));
        return roles.isEmpty() ? null : data.get(roles.get(0), "partyId");
    }

    private BigDecimal remainingQuantity(Object orderItem) {
        return decimal(data.get(orderItem, "quantity"))
                .subtract(decimal(data.get(orderItem, "cancelQuantity")));
    }

    private static BigDecimal decimal(Object value) {
        return value == null ? BigDecimal.ZERO : new BigDecimal(value.toString());
    }

    private record ShipmentLine(Object orderItem, BigDecimal quantity) { }
}
