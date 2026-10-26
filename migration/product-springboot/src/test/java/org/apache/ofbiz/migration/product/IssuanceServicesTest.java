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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.apache.ofbiz.migration.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.product.adapter.OrderPort;
import org.apache.ofbiz.migration.product.simple.InventoryReserveServices;
import org.apache.ofbiz.migration.product.simple.InventoryServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
        classes = ProductMigrationApplication.class,
        properties = "migration.security.require-https=false")
@AutoConfigureMockMvc
class IssuanceServicesTest {
    private static final AtomicLong IDS = new AtomicLong();
    private static final String LOGIN_ID = "issuance-test-user";
    private static final String GROUP = "00001";
    private static final String ITEM_SEQ = "00001";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private EntityManager entityManager;

    @MockitoBean
    private InventoryServices inventoryServices;

    @MockitoBean
    private InventoryReserveServices inventoryReserveServices;

    @MockitoBean
    private OrderPort orderPort;

    @BeforeEach
    void stubSiblingServices() {
        when(inventoryServices.execute(anyString(), anyString(), anyMap())).thenReturn(Map.of());
        when(inventoryReserveServices.execute(anyString(), anyString(), anyMap())).thenReturn(Map.of());
        when(orderPort.invoke(anyString(), anyMap())).thenReturn(Map.of());
    }

    @Test
    @Transactional
    void createItemIssuancePersistsRowsAndHandlesSerializedServiceProducts() throws Exception {
        seedUser();
        ProductInventory productInventory = seedInventory("NON_SERIALIZED", "GOOD", "20");
        String shipmentId = seedShipment("SHIPMENT_INPUT");
        Map<String, Object> arguments = map(
                "inventoryItemId", productInventory.inventoryItemId(),
                "shipmentId", shipmentId,
                "quantity", new BigDecimal("2"));

        Map<String, Object> response = response(post("createItemIssuance", arguments)
                .andExpect(status().isOk())
                .andReturn());
        assertEquals(true, response.get("affectAccounting"));
        Object issuance = find("ItemIssuance", Map.of("itemIssuanceId", response.get("itemIssuanceId")));
        assertNotNull(issuance);
        assertNotNull(value(issuance, "issuedDateTime"));
        assertDecimal("2", value(issuance, "quantity"));
        verify(inventoryServices, never()).execute(eq("updateInventoryItem"), anyString(), anyMap());

        ProductInventory serialized = seedInventory("SERIALIZED_INV_ITEM", "SERVICE_PRODUCT", "20");
        Map<String, Object> serializedResponse = response(post("createItemIssuance", map(
                "inventoryItemId", serialized.inventoryItemId(),
                "shipmentId", shipmentId,
                "quantity", new BigDecimal("1")))
                .andExpect(status().isOk())
                .andReturn());
        assertEquals(false, serializedResponse.get("affectAccounting"));
        verify(inventoryServices).execute(
                eq("updateInventoryItem"), anyString(), argThat(input -> "INV_DELIVERED".equals(input.get("statusId"))));

        String packedShipment = seedShipment("SHIPMENT_PACKED");
        post("createItemIssuance", map(
                "inventoryItemId", productInventory.inventoryItemId(),
                "shipmentId", packedShipment,
                "quantity", new BigDecimal("1")))
                .andExpect(status().isConflict());
        postReadonly("createItemIssuance", arguments).andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void issueOrderItemToShipmentCreatesAndUpdatesShipmentRows() throws Exception {
        seedUser();
        ProductInventory inventory = seedInventory("NON_SERIALIZED", "GOOD", "50");
        OrderFixture order = seedOrder("PURCHASE_ORDER", inventory.productId());
        String shipmentId = seedShipment("SHIPMENT_INPUT");
        Map<String, Object> arguments = orderShipmentArguments(order, shipmentId, new BigDecimal("2"));

        Map<String, Object> firstResponse = response(post("issueOrderItemToShipment", arguments)
                .andExpect(status().isOk())
                .andReturn());
        String sequenceId = firstResponse.get("shipmentItemSeqId").toString();
        Object shipmentItem = find("ShipmentItem", Map.of(
                "shipmentId", shipmentId, "shipmentItemSeqId", sequenceId));
        assertNotNull(shipmentItem);
        assertDecimal("2", value(shipmentItem, "quantity"));

        post("issueOrderItemToShipment", orderShipmentArguments(order, shipmentId, new BigDecimal("3")))
                .andExpect(status().isOk());
        List<?> shipmentItems = entities.findByEquality(entities.entityClass("ShipmentItem"), Map.of(
                "shipmentId", shipmentId, "productId", inventory.productId()));
        assertEquals(1, shipmentItems.size());
        assertDecimal("5", value(shipmentItems.get(0), "quantity"));
        Object orderShipment = find("OrderShipment", Map.of(
                "orderId", order.orderId(),
                "orderItemSeqId", ITEM_SEQ,
                "shipGroupSeqId", GROUP,
                "shipmentId", shipmentId,
                "shipmentItemSeqId", sequenceId));
        assertDecimal("5", value(orderShipment, "quantity"));

        OrderFixture salesOrder = seedOrder("SALES_ORDER", inventory.productId());
        post("issueOrderItemToShipment", orderShipmentArguments(salesOrder, shipmentId, BigDecimal.ONE))
                .andExpect(status().isBadRequest());
        Map<String, Object> missingQuantity = orderShipmentArguments(order, shipmentId, BigDecimal.ONE);
        missingQuantity.remove("quantity");
        post("issueOrderItemToShipment", missingQuantity).andExpect(status().isBadRequest());
        postReadonly("issueOrderItemToShipment", arguments).andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void issueReservedOrderQuantityCreatesIssuanceRolesAndInventoryDetails() throws Exception {
        seedUser();
        ProductInventory inventory = seedInventory("NON_SERIALIZED", "GOOD", "50");
        OrderFixture order = seedOrder("SALES_ORDER", inventory.productId());
        String shipmentId = seedShipment("SHIPMENT_SCHEDULED");
        seedReservation(order, inventory.inventoryItemId(), new BigDecimal("5"));

        Map<String, Object> response = response(post("issueOrderItemShipGrpInvResToShipment",
                reservationArguments(order, inventory, shipmentId, new BigDecimal("2")))
                .andExpect(status().isOk())
                .andReturn());
        assertNotNull(response.get("shipmentItemSeqId"));
        Object reservation = find("OrderItemShipGrpInvRes", reservationKey(order, inventory.inventoryItemId()));
        assertDecimal("3", value(reservation, "quantity"));

        Object issuance = find("ItemIssuance", Map.of("itemIssuanceId", response.get("itemIssuanceId")));
        assertEquals(inventory.inventoryItemId(), value(issuance, "inventoryItemId"));
        assertEquals(GROUP, value(issuance, "shipGroupSeqId"));
        assertEquals(LOGIN_ID, value(issuance, "issuedByUserLoginId"));
        assertNotNull(find("PartyRole", Map.of("partyId", "PARTY_" + LOGIN_ID, "roleTypeId", "PACKER")));
        assertNotNull(find("ItemIssuanceRole", Map.of(
                "itemIssuanceId", response.get("itemIssuanceId"),
                "partyId", "PARTY_" + LOGIN_ID,
                "roleTypeId", "PACKER")));

        ArgumentCaptor<Map<String, Object>> detailInput = mapCaptor();
        verify(inventoryServices).execute(eq("createInventoryItemDetail"), anyString(), detailInput.capture());
        assertDecimal("-2.000000", detailInput.getValue().get("quantityOnHandDiff"));
        assertEquals(6, ((BigDecimal) detailInput.getValue().get("quantityOnHandDiff")).scale());
        verify(inventoryReserveServices).execute(
                eq("cancelOrderItemShipGrpInvRes"),
                anyString(),
                argThat(input -> new BigDecimal("3").compareTo((BigDecimal) input.get("cancelQuantity")) == 0));
        verify(inventoryReserveServices).execute(
                eq("reserveProductInventoryByFacility"),
                anyString(),
                argThat(input -> "N".equals(input.get("requireInventory"))
                        && inventory.productId().equals(input.get("productId"))));
        verify(orderPort, never()).invoke(eq("changeOrderItemStatus"), anyMap());
    }

    @Test
    @Transactional
    void issueReservedOrderQuantityValidatesInputsAndCompletesOrdersOnlyWhenAppropriate() throws Exception {
        seedUser();
        ProductInventory inventory = seedInventory("NON_SERIALIZED", "GOOD", "50");
        String shipmentId = seedShipment("SHIPMENT_SCHEDULED");
        OrderFixture nonSales = seedOrder("PURCHASE_ORDER", inventory.productId());
        seedReservation(nonSales, inventory.inventoryItemId(), new BigDecimal("5"));
        post("issueOrderItemShipGrpInvResToShipment",
                reservationArguments(nonSales, inventory, shipmentId, BigDecimal.ONE))
                .andExpect(status().isBadRequest());

        OrderFixture order = seedOrder("SALES_ORDER", inventory.productId());
        seedReservation(order, inventory.inventoryItemId(), new BigDecimal("5"));
        post("issueOrderItemShipGrpInvResToShipment",
                reservationArguments(order, inventory, shipmentId, BigDecimal.ZERO))
                .andExpect(status().isBadRequest());
        post("issueOrderItemShipGrpInvResToShipment",
                reservationArguments(order, inventory, shipmentId, new BigDecimal("6")))
                .andExpect(status().isBadRequest());
        Map<String, Object> missingQuantity = reservationArguments(order, inventory, shipmentId, BigDecimal.ONE);
        missingQuantity.remove("quantity");
        post("issueOrderItemShipGrpInvResToShipment", missingQuantity)
                .andExpect(status().isBadRequest());
        postReadonly("issueOrderItemShipGrpInvResToShipment",
                reservationArguments(order, inventory, shipmentId, BigDecimal.ONE))
                .andExpect(status().isForbidden());

        String otherShipment = seedShipment("SHIPMENT_INPUT");
        OrderFixture differentDestination = seedOrder("SALES_ORDER", inventory.productId());
        seedDestination(differentDestination, "POSTAL_A", "10000");
        seedDestinationForShipment(otherShipment, "SHIPMENT_ORDER", "POSTAL_B", "20000");
        set("Shipment", Map.of("shipmentId", otherShipment), "primaryOrderId", "SHIPMENT_ORDER");
        set("Shipment", Map.of("shipmentId", otherShipment), "primaryShipGroupSeqId", GROUP);
        seedReservation(differentDestination, inventory.inventoryItemId(), new BigDecimal("5"));
        post("issueOrderItemShipGrpInvResToShipment", reservationArguments(
                differentDestination, inventory, otherShipment, BigDecimal.ONE))
                .andExpect(status().isBadRequest());

        OrderFixture scheduledOrder = seedOrder("SALES_ORDER", inventory.productId());
        seedReservation(scheduledOrder, inventory.inventoryItemId(), BigDecimal.ONE);
        post("issueOrderItemShipGrpInvResToShipment",
                reservationArguments(scheduledOrder, inventory, shipmentId, BigDecimal.ONE))
                .andExpect(status().isOk());
        assertNull(find("OrderItemShipGrpInvRes", reservationKey(scheduledOrder, inventory.inventoryItemId())));
        verify(orderPort, never()).invoke(eq("changeOrderItemStatus"), anyMap());

        String inputShipment = seedShipment("SHIPMENT_INPUT");
        OrderFixture completedOrder = seedOrder("SALES_ORDER", inventory.productId());
        seedReservation(completedOrder, inventory.inventoryItemId(), BigDecimal.ONE);
        post("issueOrderItemShipGrpInvResToShipment",
                reservationArguments(completedOrder, inventory, inputShipment, BigDecimal.ONE))
                .andExpect(status().isOk());
        verify(orderPort).invoke(
                eq("changeOrderItemStatus"), argThat(input -> "ITEM_COMPLETED".equals(input.get("statusId"))));
    }

    @Test
    @Transactional
    void issueInventoryToFixedAssetMaintChecksAvailabilityAndWritesDetails() throws Exception {
        seedUser();
        ProductInventory inventory = seedInventory("NON_SERIALIZED", "GOOD", "10");
        String fixedAssetId = id("ASSET");
        String maintHistSeqId = id("MAINT");
        seed("FixedAssetMaint", Map.of(
                "fixedAssetId", fixedAssetId,
                "maintHistSeqId", maintHistSeqId));

        Map<String, Object> response = response(post("issueInventoryItemToFixedAssetMaint", map(
                "inventoryItemId", inventory.inventoryItemId(),
                "fixedAssetId", fixedAssetId,
                "maintHistSeqId", maintHistSeqId,
                "quantity", new BigDecimal("2")))
                .andExpect(status().isOk())
                .andReturn());
        Object issuance = find("ItemIssuance", Map.of("itemIssuanceId", response.get("itemIssuanceId")));
        assertEquals(fixedAssetId, value(issuance, "fixedAssetId"));
        assertEquals(maintHistSeqId, value(issuance, "maintHistSeqId"));
        verify(inventoryServices).execute(
                eq("createInventoryItemDetail"),
                anyString(),
                argThat(input -> new BigDecimal("-2.000000")
                        .compareTo((BigDecimal) input.get("quantityOnHandDiff")) == 0
                        && new BigDecimal("-2.000000")
                                .compareTo((BigDecimal) input.get("availableToPromiseDiff")) == 0));

        post("issueInventoryItemToFixedAssetMaint", map(
                "inventoryItemId", inventory.inventoryItemId(),
                "fixedAssetId", fixedAssetId,
                "maintHistSeqId", maintHistSeqId,
                "quantity", BigDecimal.ZERO))
                .andExpect(status().isBadRequest());
        post("issueInventoryItemToFixedAssetMaint", map(
                "inventoryItemId", inventory.inventoryItemId(),
                "fixedAssetId", fixedAssetId,
                "maintHistSeqId", maintHistSeqId,
                "quantity", new BigDecimal("11")))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void returnFixedAssetIssuanceResetsQuantityAndRejectsMissingId() throws Exception {
        seedUser();
        ProductInventory inventory = seedInventory("NON_SERIALIZED", "GOOD", "10");
        String fixedAssetId = id("ASSET");
        String maintHistSeqId = id("MAINT");
        seed("FixedAssetMaint", Map.of("fixedAssetId", fixedAssetId, "maintHistSeqId", maintHistSeqId));
        String issuanceId = seedIssuance(map(
                "inventoryItemId", inventory.inventoryItemId(),
                "fixedAssetId", fixedAssetId,
                "maintHistSeqId", maintHistSeqId,
                "quantity", new BigDecimal("3")));

        post("returnInventoryItemIssuedToFixedAssetMaint", Map.of("itemIssuanceId", issuanceId))
                .andExpect(status().isOk());
        assertDecimal("0", value(find("ItemIssuance", Map.of("itemIssuanceId", issuanceId)), "quantity"));
        verify(inventoryServices).execute(
                eq("createInventoryItemDetail"),
                anyString(),
                argThat(input -> new BigDecimal("3")
                        .compareTo((BigDecimal) input.get("availableToPromiseDiff")) == 0));
        post("returnInventoryItemIssuedToFixedAssetMaint", Map.of())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void cancellationUpdatesIssuanceAndInventoryAndHandlesItsEarlyReturn() throws Exception {
        seedUser();
        ProductInventory inventory = seedInventory("NON_SERIALIZED", "GOOD", "10");
        String storeId = id("STORE");
        seed("ProductStore", Map.of("productStoreId", storeId, "isImmediatelyFulfilled", "Y"));
        OrderFixture order = seedOrder("SALES_ORDER", inventory.productId(), storeId);
        String shipmentId = seedShipment("SHIPMENT_INPUT");
        String issuanceId = seedIssuance(map(
                "inventoryItemId", inventory.inventoryItemId(),
                "shipmentId", shipmentId,
                "orderId", order.orderId(),
                "orderItemSeqId", ITEM_SEQ,
                "shipGroupSeqId", GROUP,
                "quantity", new BigDecimal("5"),
                "cancelQuantity", BigDecimal.ZERO));

        Map<String, Object> response = response(post("cancelOrderItemIssuanceFromSalesShipment", map(
                "itemIssuanceId", issuanceId,
                "cancelQuantity", new BigDecimal("2")))
                .andExpect(status().isOk())
                .andReturn());
        assertDecimal("2", response.get("canceledQuantity"));
        assertDecimal("2", value(find("ItemIssuance", Map.of("itemIssuanceId", issuanceId)), "cancelQuantity"));
        verify(inventoryServices).execute(
                eq("createInventoryItemDetail"),
                anyString(),
                argThat(input -> new BigDecimal("2")
                        .compareTo((BigDecimal) input.get("quantityOnHandDiff")) == 0));
        verify(inventoryServices).execute(
                eq("reassignInventoryReservations"),
                anyString(),
                argThat(input -> inventory.productId().equals(input.get("productId"))));

        post("cancelOrderItemIssuanceFromSalesShipment", map(
                "itemIssuanceId", issuanceId,
                "cancelQuantity", BigDecimal.ZERO))
                .andExpect(status().isOk())
                .andExpect(result -> assertFalse(result.getResponse().getContentAsString().contains("canceledQuantity")));
        assertDecimal("2", value(find("ItemIssuance", Map.of("itemIssuanceId", issuanceId)), "cancelQuantity"));

        post("cancelOrderItemIssuanceFromSalesShipment", map(
                "itemIssuanceId", issuanceId,
                "cancelQuantity", new BigDecimal("-1")))
                .andExpect(status().isBadRequest());
        post("cancelOrderItemIssuanceFromSalesShipment", map(
                "itemIssuanceId", issuanceId,
                "cancelQuantity", new BigDecimal("4")))
                .andExpect(status().isBadRequest());
        OrderFixture purchase = seedOrder("PURCHASE_ORDER", inventory.productId(), storeId);
        String nonSalesIssuance = seedIssuance(map(
                "inventoryItemId", inventory.inventoryItemId(),
                "shipmentId", shipmentId,
                "orderId", purchase.orderId(),
                "orderItemSeqId", ITEM_SEQ,
                "quantity", BigDecimal.ONE));
        post("cancelOrderItemIssuanceFromSalesShipment", Map.of(
                "itemIssuanceId", nonSalesIssuance,
                "cancelQuantity", BigDecimal.ONE))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void cancellationCallsTheUnportedStoreReservationForNonImmediateStores() throws Exception {
        seedUser();
        ProductInventory inventory = seedInventory("NON_SERIALIZED", "GOOD", "10");
        String storeId = id("STORE");
        seed("ProductStore", Map.of("productStoreId", storeId, "isImmediatelyFulfilled", "N"));
        OrderFixture order = seedOrder("SALES_ORDER", inventory.productId(), storeId);
        String shipmentId = seedShipment("SHIPMENT_INPUT");
        String issuanceId = seedIssuance(map(
                "inventoryItemId", inventory.inventoryItemId(),
                "shipmentId", shipmentId,
                "orderId", order.orderId(),
                "orderItemSeqId", ITEM_SEQ,
                "shipGroupSeqId", GROUP,
                "quantity", BigDecimal.ONE));
        post("cancelOrderItemIssuanceFromSalesShipment", Map.of(
                "itemIssuanceId", issuanceId,
                "cancelQuantity", BigDecimal.ONE))
                .andExpect(status().isNotImplemented());
    }

    @Test
    @Transactional
    void issueInventoryItemToShipmentRequiresMatchingReturnShipmentAndAvailableQuantity() throws Exception {
        seedUser();
        ProductInventory inventory = seedInventory("NON_SERIALIZED", "GOOD", "10");
        String shipmentId = seedShipment("SHIPMENT_INPUT");
        String shipmentItemSeqId = "00001";
        seed("ShipmentItem", Map.of(
                "shipmentId", shipmentId,
                "shipmentItemSeqId", shipmentItemSeqId,
                "productId", inventory.productId(),
                "quantity", new BigDecimal("4")));
        seed("ReturnItemShipment", Map.of(
                "returnId", id("RETURN"),
                "returnItemSeqId", ITEM_SEQ,
                "shipmentId", shipmentId,
                "shipmentItemSeqId", shipmentItemSeqId,
                "quantity", new BigDecimal("4")));
        Map<String, Object> arguments = map(
                "inventoryItemId", inventory.inventoryItemId(),
                "shipmentId", shipmentId,
                "shipmentItemSeqId", shipmentItemSeqId,
                "quantity", new BigDecimal("2"));

        Map<String, Object> response = response(post("issueInventoryItemToShipment", arguments)
                .andExpect(status().isOk())
                .andReturn());
        assertNotNull(find("ItemIssuance", Map.of("itemIssuanceId", response.get("itemIssuanceId"))));
        verify(inventoryServices).execute(eq("createInventoryItemDetail"), anyString(), anyMap());

        ProductInventory otherProduct = seedInventory("NON_SERIALIZED", "GOOD", "10");
        post("issueInventoryItemToShipment", map(
                "inventoryItemId", otherProduct.inventoryItemId(),
                "shipmentId", shipmentId,
                "shipmentItemSeqId", shipmentItemSeqId,
                "quantity", BigDecimal.ONE))
                .andExpect(status().isBadRequest());
        post("issueInventoryItemToShipment", map(
                "inventoryItemId", "MISSING",
                "shipmentId", shipmentId,
                "shipmentItemSeqId", shipmentItemSeqId,
                "quantity", BigDecimal.ONE))
                .andExpect(status().isBadRequest());
        post("issueInventoryItemToShipment", map(
                "inventoryItemId", inventory.inventoryItemId(),
                "shipmentId", shipmentId,
                "shipmentItemSeqId", "MISSING",
                "quantity", BigDecimal.ONE))
                .andExpect(status().isBadRequest());
        String noReturnSeq = "00002";
        seed("ShipmentItem", Map.of(
                "shipmentId", shipmentId,
                "shipmentItemSeqId", noReturnSeq,
                "productId", inventory.productId(),
                "quantity", BigDecimal.ONE));
        post("issueInventoryItemToShipment", map(
                "inventoryItemId", inventory.inventoryItemId(),
                "shipmentId", shipmentId,
                "shipmentItemSeqId", noReturnSeq,
                "quantity", BigDecimal.ONE))
                .andExpect(status().isBadRequest());
        post("issueInventoryItemToShipment", map(
                "inventoryItemId", inventory.inventoryItemId(),
                "shipmentId", shipmentId,
                "shipmentItemSeqId", shipmentItemSeqId,
                "quantity", new BigDecimal("11")))
                .andExpect(status().isBadRequest());
        post("issueInventoryItemToShipment", map(
                "inventoryItemId", inventory.inventoryItemId(),
                "shipmentId", shipmentId,
                "shipmentItemSeqId", shipmentItemSeqId,
                "quantity", new BigDecimal("3")))
                .andExpect(status().isBadRequest());
        Map<String, Object> missingQuantity = map(
                "inventoryItemId", inventory.inventoryItemId(),
                "shipmentId", shipmentId,
                "shipmentItemSeqId", shipmentItemSeqId);
        post("issueInventoryItemToShipment", missingQuantity).andExpect(status().isBadRequest());
        postReadonly("issueInventoryItemToShipment", arguments).andExpect(status().isForbidden());
    }

    private ResultActions post(String service, Map<String, Object> arguments) throws Exception {
        return mockMvc.perform(post("/api/product/services/" + service)
                        .with(user(LOGIN_ID).authorities(
                                new SimpleGrantedAuthority("OFBTOOLS_VIEW"),
                                new SimpleGrantedAuthority("FACILITY_VIEW"),
                                new SimpleGrantedAuthority("FACILITY_CREATE"),
                                new SimpleGrantedAuthority("FACILITY_UPDATE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(arguments)));
    }

    private org.springframework.test.web.servlet.ResultActions postReadonly(
            String service, Map<String, Object> arguments) throws Exception {
        return mockMvc.perform(post("/api/product/services/" + service)
                .with(user(LOGIN_ID).authorities(
                        new SimpleGrantedAuthority("OFBTOOLS_VIEW"),
                        new SimpleGrantedAuthority("FACILITY_VIEW")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(arguments)));
    }

    private Map<String, Object> response(MvcResult result) throws Exception {
        return objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() { });
    }

    private Object seedUser() {
        seed("Party", Map.of("partyId", "PARTY_" + LOGIN_ID, "partyTypeId", "PERSON"));
        return seed("UserLogin", Map.of("userLoginId", LOGIN_ID, "partyId", "PARTY_" + LOGIN_ID));
    }

    private ProductInventory seedInventory(String inventoryType, String productType, String atp) {
        String productId = id("PRODUCT");
        String inventoryItemId = id("INVENTORY");
        seed("Product", Map.of("productId", productId, "productTypeId", productType));
        seed("InventoryItem", map(
                "inventoryItemId", inventoryItemId,
                "productId", productId,
                "inventoryItemTypeId", inventoryType,
                "availableToPromiseTotal", new BigDecimal(atp),
                "facilityId", "FACILITY_" + inventoryItemId));
        return new ProductInventory(productId, inventoryItemId);
    }

    private String seedShipment(String statusId) {
        String shipmentId = id("SHIPMENT");
        seed("Shipment", Map.of("shipmentId", shipmentId, "statusId", statusId));
        return shipmentId;
    }

    private OrderFixture seedOrder(String orderType, String productId) {
        return seedOrder(orderType, productId, null);
    }

    private OrderFixture seedOrder(String orderType, String productId, String productStoreId) {
        String orderId = id("ORDER");
        Map<String, Object> header = map("orderId", orderId, "orderTypeId", orderType);
        if (productStoreId != null) {
            header.put("productStoreId", productStoreId);
        }
        seed("OrderHeader", header);
        seed("OrderItem", Map.of(
                "orderId", orderId,
                "orderItemSeqId", ITEM_SEQ,
                "productId", productId,
                "quantity", new BigDecimal("10")));
        seed("OrderItemShipGroup", Map.of("orderId", orderId, "shipGroupSeqId", GROUP));
        seed("OrderItemShipGroupAssoc", Map.of(
                "orderId", orderId,
                "orderItemSeqId", ITEM_SEQ,
                "shipGroupSeqId", GROUP,
                "quantity", new BigDecimal("10")));
        return new OrderFixture(orderId);
    }

    private void seedReservation(OrderFixture order, String inventoryItemId, BigDecimal quantity) {
        seed("OrderItemShipGrpInvRes", map(
                "orderId", order.orderId(),
                "orderItemSeqId", ITEM_SEQ,
                "shipGroupSeqId", GROUP,
                "inventoryItemId", inventoryItemId,
                "quantity", quantity));
    }

    private void seedDestination(OrderFixture order, String addressId, String postalCode) {
        seed("PostalAddress", Map.of(
                "contactMechId", addressId,
                "postalCode", postalCode,
                "countryGeoId", "USA",
                "stateProvinceGeoId", "CA"));
        set("OrderItemShipGroup", Map.of(
                "orderId", order.orderId(), "shipGroupSeqId", GROUP), "contactMechId", addressId);
    }

    private void seedDestinationForShipment(String orderId, String addressId, String postalCode) {
        seed("OrderItemShipGroup", Map.of("orderId", orderId, "shipGroupSeqId", GROUP));
        seed("PostalAddress", Map.of(
                "contactMechId", addressId,
                "postalCode", postalCode,
                "countryGeoId", "USA",
                "stateProvinceGeoId", "CA"));
        set("OrderItemShipGroup", Map.of(
                "orderId", orderId, "shipGroupSeqId", GROUP), "contactMechId", addressId);
    }

    private String seedIssuance(Map<String, Object> fields) {
        String itemIssuanceId = id("ISSUANCE");
        Map<String, Object> values = new LinkedHashMap<>(fields);
        values.put("itemIssuanceId", itemIssuanceId);
        seed("ItemIssuance", values);
        return itemIssuanceId;
    }

    private Map<String, Object> orderShipmentArguments(
            OrderFixture order, String shipmentId, BigDecimal quantity) {
        return map(
                "orderId", order.orderId(),
                "orderItemSeqId", ITEM_SEQ,
                "shipGroupSeqId", GROUP,
                "shipmentId", shipmentId,
                "quantity", quantity);
    }

    private Map<String, Object> reservationArguments(
            OrderFixture order, ProductInventory inventory, String shipmentId, BigDecimal quantity) {
        return map(
                "orderId", order.orderId(),
                "orderItemSeqId", ITEM_SEQ,
                "shipGroupSeqId", GROUP,
                "inventoryItemId", inventory.inventoryItemId(),
                "shipmentId", shipmentId,
                "quantity", quantity);
    }

    private Map<String, Object> reservationKey(OrderFixture order, String inventoryItemId) {
        return Map.of(
                "orderId", order.orderId(),
                "orderItemSeqId", ITEM_SEQ,
                "shipGroupSeqId", GROUP,
                "inventoryItemId", inventoryItemId);
    }

    private Object seed(String entityName, Map<String, ?> fields) {
        Object entity = entities.newEntity(entities.entityClass(entityName));
        fields.forEach((field, value) -> entities.set(entity, field, value));
        entityManager.persist(entity);
        entityManager.flush();
        return entity;
    }

    private void set(String entityName, Map<String, ?> primaryKey, String field, Object value) {
        Object entity = entities.findByPrimaryKey(entities.entityClass(entityName), primaryKey);
        entities.set(entity, field, value);
        entityManager.flush();
    }

    private Object find(String entityName, Map<String, ?> primaryKey) {
        return entities.findByPrimaryKey(entities.entityClass(entityName), primaryKey);
    }

    private Object value(Object entity, String field) {
        return entities.get(entity, field);
    }

    private Map<String, Object> map(Object... keyValues) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int index = 0; index < keyValues.length; index += 2) {
            result.put(keyValues[index].toString(), keyValues[index + 1]);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<Map<String, Object>> mapCaptor() {
        return (ArgumentCaptor<Map<String, Object>>) (ArgumentCaptor<?>) ArgumentCaptor.forClass(Map.class);
    }

    private String id(String prefix) {
        return prefix + "_" + IDS.incrementAndGet();
    }

    private static void assertDecimal(String expected, Object actual) {
        assertNotNull(actual);
        assertEquals(0, new BigDecimal(expected).compareTo(new BigDecimal(actual.toString())));
    }

    private record ProductInventory(String productId, String inventoryItemId) {
    }

    private record OrderFixture(String orderId) {
    }
}
