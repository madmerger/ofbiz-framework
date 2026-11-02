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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.apache.ofbiz.migration.datamodel.entity.Invoice;
import org.apache.ofbiz.migration.datamodel.entity.ItemIssuance;
import org.apache.ofbiz.migration.datamodel.entity.OrderHeader;
import org.apache.ofbiz.migration.datamodel.entity.OrderItemBilling;
import org.apache.ofbiz.migration.datamodel.entity.OrderShipment;
import org.apache.ofbiz.migration.datamodel.entity.Shipment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

class ShipmentInvoiceServicesTest {
    private final EntityRegistry entities = mock(EntityRegistry.class);
    private final MigrationServiceDispatcher dispatcher = mock(MigrationServiceDispatcher.class);
    private ShipmentInvoiceServices services;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        ObjectProvider<MigrationServiceDispatcher> provider = mock(ObjectProvider.class);
        when(provider.getObject()).thenReturn(dispatcher);
        services = new ShipmentInvoiceServices(entities, mock(ServiceHandlerRegistry.class), provider, mock(OrderPort.class));
        Map<String, Class<?>> types = Map.of("Shipment", Shipment.class, "ItemIssuance", ItemIssuance.class,
                "OrderHeader", OrderHeader.class, "OrderItemBilling", OrderItemBilling.class,
                "OrderShipment", OrderShipment.class, "Invoice", Invoice.class);
        when(entities.entityClass(anyString())).thenAnswer(call -> types.get(call.getArgument(0)));
        when(entities.get(any(), anyString())).thenAnswer(call -> ((Map<?, ?>) call.getArgument(0)).get(call.getArgument(1)));
        when(entities.toMap(any())).thenAnswer(call -> call.getArgument(0));
        doReturn(Map.of("shipmentId", "SHIP", "shipmentTypeId", "SALES_SHIPMENT"))
                .when(entities).findByPrimaryKey(Shipment.class, Map.of("shipmentId", "SHIP"));
    }

    @Test
    void groupsUnbilledIssuancesAndMovesTheFirstInProcessInvoiceToReadyThroughServices() {
        Map<String, Object> issue = Map.of("orderId", "ORDER", "orderItemSeqId", "00001", "itemIssuanceId", "ISSUE");
        doReturn(List.of(issue)).when(entities).findByEquality(ItemIssuance.class, Map.of("shipmentId", "SHIP"));
        when(dispatcher.execute(eq("accounting"), eq("createInvoiceForOrder"), anyMap())).thenReturn(Map.of("invoiceId", "INV"));
        assertEquals(Map.of("invoicesCreated", List.of("INV")), services.createFromShipments(Map.of("shipmentIds", List.of("SHIP"))));
        verify(dispatcher).execute("accounting", "createInvoiceForOrder", Map.of("shipmentIds", List.of("SHIP"),
                "orderId", "ORDER", "billItems", List.of(issue)));
        doReturn(List.of(Map.of("invoiceId", "INV"))).when(entities).findByEquality(OrderItemBilling.class, Map.of("orderId", "ORDER"));
        doReturn(Map.of("invoiceId", "INV", "statusId", "INVOICE_IN_PROCESS"))
                .when(entities).findByPrimaryKey(Invoice.class, Map.of("invoiceId", "INV"));
        assertEquals(Map.of(), services.setReady(Map.of("shipmentId", "SHIP")));
        verify(dispatcher).execute("accounting", "setInvoiceStatus", Map.of("invoiceId", "INV", "statusId", "INVOICE_READY"));
    }

    @Test
    void emptyShipmentsAndDisabledPerShipmentInvoicingDoNotCreateInvoices() {
        assertEquals(Map.of(), services.createFromShipments(Map.of("shipmentIds", List.of("SHIP"))));
        assertEquals(Map.of(), services.setReady(Map.of("shipmentId", "SHIP")));
        doReturn(List.of(Map.of("orderId", "ORDER"))).when(entities).findByEquality(OrderShipment.class, Map.of("shipmentId", "SHIP"));
        doReturn(Map.of("invoicePerShipment", "N")).when(entities).findByPrimaryKey(OrderHeader.class, Map.of("orderId", "ORDER"));
        assertEquals(Map.of(), services.createFromShipment(Map.of("shipmentId", "SHIP")));
    }

    @Test
    void duplicateBillingIsSkippedAndUnsupportedOrMissingShipmentsFailExplicitly() {
        doReturn(List.of(Map.of("orderId", "ORDER", "itemIssuanceId", "ISSUE")))
                .when(entities).findByEquality(ItemIssuance.class, Map.of("shipmentId", "SHIP"));
        doReturn(List.of(Map.of("invoiceId", "INV"))).when(entities).findByEquality(OrderItemBilling.class, Map.of("itemIssuanceId", "ISSUE"));
        doReturn(Map.of("statusId", "INVOICE_READY")).when(entities).findByPrimaryKey(Invoice.class, Map.of("invoiceId", "INV"));
        assertEquals(Map.of("invoicesCreated", List.of()), services.createFromShipments(Map.of("shipmentIds", List.of("SHIP"))));
        assertThrows(MigrationException.class, () -> services.createFromShipment(Map.of()));
        assertThrows(MigrationException.class, () -> services.createFromShipment(Map.of("shipmentId", "MISSING")));
        assertThrows(MigrationException.class, () -> services.createFromShipments(Map.of("shipmentIds", List.of())));
        doReturn(Map.of("shipmentTypeId", "PURCHASE_SHIPMENT")).when(entities).findByPrimaryKey(Shipment.class, Map.of("shipmentId", "SHIP"));
        assertThrows(MigrationException.class, () -> services.createFromShipments(Map.of("shipmentIds", List.of("SHIP"))));
    }
}
