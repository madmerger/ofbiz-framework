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
package org.apache.ofbiz.migration.accounting;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityAutoService;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ShipmentInvoiceServices {
    private final EntityRegistry entities;
    private final ServiceHandlerRegistry registry;
    private final ObjectProvider<MigrationServiceDispatcher> dispatcher;
    private final OrderPort orders;
    @Autowired
    private ObjectProvider<EntityAutoService> entityAuto;
    @Value("${migration.order.invoice-per-shipment:Y}")
    private String invoicePerShipment = "Y";

    public ShipmentInvoiceServices(EntityRegistry entities, ServiceHandlerRegistry registry,
            ObjectProvider<MigrationServiceDispatcher> dispatcher, OrderPort orders) {
        this.entities = entities;
        this.registry = registry;
        this.dispatcher = dispatcher;
        this.orders = orders;
    }

    @PostConstruct
    void register() {
        registry.registerHandler("accounting", "createInvoicesFromShipment", "OFBTOOLS_VIEW", this::createFromShipment);
        registry.registerHandler("accounting", "createInvoicesFromShipments", "OFBTOOLS_VIEW", this::createFromShipments);
        registry.registerHandler("accounting", "setInvoicesToReadyFromShipment", "OFBTOOLS_VIEW", this::setReady);
        if (registry.componentFor("order", "createOrderItemBilling").isEmpty()) {
            registry.registerHandler("order", "createOrderItemBilling", "OFBTOOLS_VIEW", input -> {
                Map<String, Object> fields = new LinkedHashMap<>();
                for (String name : List.of("orderId", "orderItemSeqId", "invoiceId", "invoiceItemSeqId",
                        "itemIssuanceId", "shipmentReceiptId", "quantity", "amount")) {
                    if (input.get(name) != null) fields.put(name, input.get(name));
                }
                orders.createOrderItemBilling(fields);
                return Map.of();
            });
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    void registerShipmentBilling() {
        var parameters = List.of("invoiceId", "invoiceItemSeqId", "shipmentId", "shipmentItemSeqId").stream()
                .map(name -> new ServiceHandlerRegistry.ParamDefinition(name, "IN", false, "String")).toList();
        var definition = new ServiceHandlerRegistry.ServiceDefinition("createShipmentItemBilling", "ShipmentItemBilling", "create", null, parameters);
        registry.registerHandler("product", "createShipmentItemBilling", "OFBTOOLS_VIEW", input -> entityAuto.getObject().execute(definition, input));
    }

    public Map<String, Object> createFromShipment(Map<String, Object> input) {
        String shipmentId = required(input, "shipmentId");
        require("Shipment", "shipmentId", shipmentId);
        List<?> links = find("OrderShipment", Map.of("shipmentId", shipmentId));
        String setting = null;
        if (!links.isEmpty()) {
            Object order = require("OrderHeader", "orderId", value(links.get(0), "orderId"));
            setting = text(value(order, "invoicePerShipment"));
        }
        if (!"Y".equals(setting == null ? invoicePerShipment : setting)) return Map.of();
        Map<String, Object> parameters = new LinkedHashMap<>(input);
        parameters.put("shipmentIds", List.of(shipmentId));
        return dispatcher.getObject().execute("accounting", "createInvoicesFromShipments", parameters);
    }

    public Map<String, Object> createFromShipments(Map<String, Object> input) {
        if (!(input.get("shipmentIds") instanceof List<?> ids) || ids.isEmpty()) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "shipmentIds must contain at least one shipment");
        }
        Map<String, List<Map<String, Object>>> byOrder = new LinkedHashMap<>();
        boolean issued = false;
        for (Object id : ids) {
            Object shipment = require("Shipment", "shipmentId", id);
            String type = text(value(shipment, "shipmentTypeId"));
            if (type != null && !"SALES_SHIPMENT".equals(type)) {
                throw new MigrationException(HttpStatus.NOT_IMPLEMENTED, "Only sales-shipment invoicing is ported");
            }
            for (Object item : find("ItemIssuance", Map.of("shipmentId", id))) {
                issued = true;
                boolean billed = find("OrderItemBilling", Map.of("itemIssuanceId", value(item, "itemIssuanceId"))).stream()
                        .anyMatch(billing -> !"INVOICE_CANCELLED".equals(value(require("Invoice", "invoiceId",
                                value(billing, "invoiceId")), "statusId")));
                if (!billed) byOrder.computeIfAbsent(value(item, "orderId").toString(), key -> new ArrayList<>())
                        .add(entities.toMap(item));
            }
        }
        if (!issued) return Map.of();
        List<String> created = new ArrayList<>();
        for (var order : byOrder.entrySet()) {
            Map<String, Object> parameters = new LinkedHashMap<>(input);
            parameters.put("orderId", order.getKey());
            parameters.put("billItems", order.getValue());
            Map<String, Object> result = dispatcher.getObject().execute("accounting", "createInvoiceForOrder", parameters);
            if (result.get("invoiceId") != null) created.add(result.get("invoiceId").toString());
        }
        return Map.of("invoicesCreated", created);
    }

    public Map<String, Object> setReady(Map<String, Object> input) {
        String id = required(input, "shipmentId");
        require("Shipment", "shipmentId", id);
        var orderIds = new LinkedHashSet<Object>();
        for (Object item : find("ItemIssuance", Map.of("shipmentId", id))) orderIds.add(value(item, "orderId"));
        var invoiceIds = new LinkedHashSet<Object>();
        for (Object orderId : orderIds) {
            List<?> billings = find("OrderItemBilling", Map.of("orderId", orderId));
            if (!billings.isEmpty()) {
                Object invoice = require("Invoice", "invoiceId", value(billings.get(0), "invoiceId"));
                if ("INVOICE_IN_PROCESS".equals(value(invoice, "statusId"))) invoiceIds.add(value(invoice, "invoiceId"));
            }
        }
        for (Object invoiceId : invoiceIds) dispatcher.getObject().execute("accounting", "setInvoiceStatus",
                Map.of("invoiceId", invoiceId, "statusId", "INVOICE_READY"));
        return Map.of();
    }

    private List<?> find(String entity, Map<String, ?> filters) {
        return entities.findByEquality(entities.entityClass(entity), filters);
    }

    private Object require(String entity, String field, Object id) {
        if (id == null) throw new MigrationException(HttpStatus.BAD_REQUEST, field + " is required");
        Object row = entities.findByPrimaryKey(entities.entityClass(entity), Map.of(field, id));
        if (row == null) throw new MigrationException(HttpStatus.NOT_FOUND, entity + " not found");
        return row;
    }

    private Object value(Object row, String field) { return entities.get(row, field); }
    private static String text(Object value) { return value == null ? null : value.toString(); }
    private static String required(Map<String, Object> input, String field) {
        String result = text(input.get(field));
        if (result == null || result.isBlank()) throw new MigrationException(HttpStatus.BAD_REQUEST, field + " is required");
        return result;
    }
}
