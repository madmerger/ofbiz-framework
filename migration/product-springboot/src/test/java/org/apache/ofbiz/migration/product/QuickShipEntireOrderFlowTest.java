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
package org.apache.ofbiz.migration.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.product.simple.IssuanceServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
        classes = ProductMigrationApplication.class,
        properties = "migration.security.require-https=false")
@AutoConfigureMockMvc
class QuickShipEntireOrderFlowTest {
    private static final ObjectMapper JSON = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private EntityManager entityManager;

    @SpyBean
    private IssuanceServices issuance;

    @SpyBean
    private MigrationServiceDispatcher dispatcher;

    @Test
    void missingOrderIdReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/product/services/quickShipEntireOrder")
                        .with(user("facility").authorities(authorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(Map.of())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void readOnlyFacilityCannotQuickShip() throws Exception {
        mockMvc.perform(post("/api/product/services/quickShipEntireOrder")
                        .with(user("readonly").authorities(
                                new SimpleGrantedAuthority("OFBTOOLS_VIEW"),
                                new SimpleGrantedAuthority("FACILITY_VIEW")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(Map.of("orderId", "FLOW_ORDER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void seededReservationCreatesShipmentIssuesInventoryAndShipsSuccessfully() throws Exception {
        seedOrderReservation();

        var result = mockMvc.perform(post("/api/product/services/quickShipEntireOrder")
                        .with(user("facility").authorities(authorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(Map.of("orderId", "FLOW_ORDER"))))
                .andExpect(status().isOk())
                .andReturn();

        var response = JSON.readTree(result.getResponse().getContentAsByteArray());
        String shipmentId = response.path("shipmentIds").get(0).asText();
        assertFalse(shipmentId.isBlank());
        verify(dispatcher).execute(eq("product"), eq("createShipment"), anyMap());
        verify(issuance).execute(
                eq("issueOrderItemShipGrpInvResToShipment"),
                eq("issueOrderItemShipGrpInvResToShipment"),
                anyMap());
        var shipments = entities.findByEquality(
                entities.entityClass("Shipment"), Map.of("shipmentId", shipmentId));
        assertEquals(1, shipments.size());
        assertEquals("SHIPMENT_SHIPPED", entities.get(shipments.get(0), "statusId"));
        assertEquals(1, entities.findByEquality(
                entities.entityClass("ShipmentStatus"),
                Map.of("shipmentId", shipmentId, "statusId", "SHIPMENT_SHIPPED")).size());
        assertEquals(1, entities.findByEquality(
                entities.entityClass("ShipmentItem"), Map.of("shipmentId", shipmentId)).size());
        assertEquals(1, entities.findByEquality(
                entities.entityClass("ItemIssuance"),
                Map.of("shipmentId", shipmentId, "orderId", "FLOW_ORDER")).size());
        Object completedOrderItem = entities.findByPrimaryKey(entities.entityClass("OrderItem"),
                Map.of("orderId", "FLOW_ORDER", "orderItemSeqId", "00001"));
        assertEquals("ITEM_COMPLETED", entities.get(completedOrderItem, "statusId"));
        assertEquals(1, entities.findByEquality(entities.entityClass("OrderStatus"),
                Map.of("orderId", "FLOW_ORDER", "orderItemSeqId", "00001", "statusId", "ITEM_COMPLETED")).size());
        assertTrue(entities.findByEquality(
                        entities.entityClass("OrderItemShipGrpInvRes"),
                        Map.of("orderId", "FLOW_ORDER", "orderItemSeqId", "00001",
                                "shipGroupSeqId", "00001", "inventoryItemId", "FLOW_INVENTORY"))
                .isEmpty());
    }

    private void seedOrderReservation() {
        persist("Party", Map.of("partyId", "PARTY_facility", "partyTypeId", "PERSON"));
        persist("UserLogin", Map.of("userLoginId", "facility", "partyId", "PARTY_facility"));
        persist("ProductStore", Map.of(
                "productStoreId", "FLOW_STORE",
                "reserveInventory", "Y",
                "explodeOrderItems", "N"));
        persist("Product", Map.of(
                "productId", "FLOW_PRODUCT",
                "productTypeId", "FINISHED_GOOD"));
        persist("Facility", Map.of("facilityId", "FLOW_FACILITY"));
        persist("OrderHeader", Map.of(
                "orderId", "FLOW_ORDER",
                "orderTypeId", "SALES_ORDER",
                "productStoreId", "FLOW_STORE"));
        persist("OrderItemShipGroup", Map.of(
                "orderId", "FLOW_ORDER",
                "shipGroupSeqId", "00001"));
        persist("OrderItem", Map.of(
                "orderId", "FLOW_ORDER",
                "orderItemSeqId", "00001",
                "productId", "FLOW_PRODUCT",
                "statusId", "ITEM_APPROVED",
                "quantity", BigDecimal.ONE));
        persist("OrderItemShipGroupAssoc", Map.of(
                "orderId", "FLOW_ORDER",
                "orderItemSeqId", "00001",
                "shipGroupSeqId", "00001",
                "quantity", BigDecimal.ONE));
        persist("InventoryItem", Map.of(
                "inventoryItemId", "FLOW_INVENTORY",
                "productId", "FLOW_PRODUCT",
                "facilityId", "FLOW_FACILITY",
                "quantityOnHandTotal", BigDecimal.ONE,
                "availableToPromiseTotal", BigDecimal.ONE));
        persist("OrderItemShipGrpInvRes", Map.of(
                "orderId", "FLOW_ORDER",
                "shipGroupSeqId", "00001",
                "orderItemSeqId", "00001",
                "inventoryItemId", "FLOW_INVENTORY",
                "quantity", BigDecimal.ONE));
        persistTransition("SHIPMENT_INPUT", "SHIPMENT_PACKED");
        persistTransition("SHIPMENT_PACKED", "SHIPMENT_SHIPPED");
        persistTransition("ITEM_APPROVED", "ITEM_COMPLETED");
        entityManager.flush();
    }

    private void persistTransition(String from, String to) {
        if (!entities.findByEquality(entities.entityClass("StatusValidChange"),
                Map.of("statusId", from, "statusIdTo", to)).isEmpty()) {
            return;
        }
        persist("StatusValidChange", Map.of("statusId", from, "statusIdTo", to));
    }

    private void persist(String entityName, Map<String, Object> values) {
        Object entity = entities.newEntity(entities.entityClass(entityName));
        values.forEach((field, value) -> entities.set(entity, field, value));
        entityManager.persist(entity);
    }

    private static SimpleGrantedAuthority[] authorities() {
        return java.util.Arrays.stream(new String[] {
            "OFBTOOLS_VIEW", "FACILITY_VIEW", "FACILITY_CREATE", "FACILITY_UPDATE"
        }).map(SimpleGrantedAuthority::new).toArray(SimpleGrantedAuthority[]::new);
    }
}
