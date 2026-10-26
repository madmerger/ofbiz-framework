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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
        classes = ProductMigrationApplication.class,
        properties = "migration.security.require-https=false")
@AutoConfigureMockMvc
@Transactional
class InventoryServicesTest {
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final AtomicInteger IDS = new AtomicInteger();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private EntityManager entityManager;

    @Test
    void ac01_inventoryAvailableByFacilityAliasReturnsZeroTotalsForAnEmptyFacility() throws Exception {
        mockMvc.perform(post("/api/product/services/getInventoryAvailableByFacility")
                        .with(user("inventory").authorities(adminAuthorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productId":"inventory-test-product","facilityId":"inventory-test-facility"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantityOnHandTotal").value(0))
                .andExpect(jsonPath("$.availableToPromiseTotal").value(0))
                .andExpect(jsonPath("$.accountingQuantityTotal").value(0));
    }

    @Test
    void ac02_updateInventoryItemRequiresFacilityUpdatePermission() throws Exception {
        mockMvc.perform(post("/api/product/services/updateInventoryItem")
                        .with(user("readonly").authorities(readOnlyAuthorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"inventoryItemId":"missing-inventory-item","description":"updated"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void ac03_createInventoryTransferRequiresFacilityCreatePermission() throws Exception {
        mockMvc.perform(post("/api/product/services/createInventoryTransfer")
                        .with(user("readonly").authorities(readOnlyAuthorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"inventoryItemId":"missing-inventory-item","xferQty":1}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void ac04_createInventoryItemDetailRejectsMissingRequiredInventoryItem() throws Exception {
        mockMvc.perform(post("/api/product/services/createInventoryItemDetail")
                        .with(user("admin").authorities(adminAuthorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"quantityOnHandDiff":1}
                                """))
                    .andExpect(status().isBadRequest());
    }

    @Test
    void ac05_createInventoryTransfersForProductRejectsMissingRequiredInputs() throws Exception {
        mockMvc.perform(post("/api/product/services/createInventoryTransfersForProduct")
                        .with(user("admin").authorities(adminAuthorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void ac06_createInventoryItemCheckSetAtpQohCreatesDetailAndUpdatesTotals() throws Exception {
        Fixture fixture = fixture(true, "NON_SERIAL_INV_ITEM", "INV_AVAILABLE", BigDecimal.ZERO, BigDecimal.ZERO);

        JsonNode response = invoke("createInventoryItemCheckSetAtpQoh", values(
                "inventoryItemId", fixture.inventoryItemId(), "availableToPromiseTotal", 5, "quantityOnHandTotal", 5));

        assertSuccessWithoutOutputs(response);
        Object item = one("InventoryItem", values("inventoryItemId", fixture.inventoryItemId()));
        assertDecimal("5", entities.get(item, "availableToPromiseTotal"));
        assertDecimal("5", entities.get(item, "quantityOnHandTotal"));
        assertEquals(1, count("InventoryItemDetail", values("inventoryItemId", fixture.inventoryItemId())));
    }

    @Test
    void ac07_updateInventoryItemReturnsOldValuesAndPersistsUnitCostAndDetail() throws Exception {
        Fixture fixture = fixture(true, "NON_SERIAL_INV_ITEM", "INV_AVAILABLE", BigDecimal.ZERO, BigDecimal.ZERO);
        Object item = one("InventoryItem", values("inventoryItemId", fixture.inventoryItemId()));
        entities.set(item, "unitCost", new BigDecimal("2.00"));
        entityManager.flush();

        JsonNode response = invoke("updateInventoryItem", values(
                "inventoryItemId", fixture.inventoryItemId(), "unitCost", new BigDecimal("3.50")));

        assertTrue(response.has("oldOwnerPartyId"));
        assertTrue(response.has("oldProductId"));
        assertTrue(response.has("oldStatusId"));
        Object updated = one("InventoryItem", values("inventoryItemId", fixture.inventoryItemId()));
        assertDecimal("3.50", entities.get(updated, "unitCost"));
        assertTrue(count("InventoryItemDetail", values("inventoryItemId", fixture.inventoryItemId())) > 0);
    }

    @Test
    void ac08_updateInventoryItemRejectsNegativeUnitCostWithLabel() throws Exception {
        Fixture fixture = fixture(true, "NON_SERIAL_INV_ITEM", "INV_AVAILABLE", BigDecimal.ZERO, BigDecimal.ZERO);

        MvcResult result = invokeRaw("updateInventoryItem", values(
                "inventoryItemId", fixture.inventoryItemId(), "unitCost", new BigDecimal("-1")));

        assertEquals(400, result.getResponse().getStatus());
        assertEquals("Unit cost for inventory item " + fixture.inventoryItemId() + " cannot be negative.",
                JSON.readTree(result.getResponse().getContentAsByteArray()).path("errorMessage").asText());
    }

    @Test
    void ac09_createInventoryItemStatusEndsPriorStatusAndUsesAuthenticatedUser() throws Exception {
        Fixture fixture = fixture(true, "NON_SERIAL_INV_ITEM", "INV_AVAILABLE", BigDecimal.ZERO, BigDecimal.ZERO);
        seed("UserLogin", values("userLoginId", "inventory"));
        Timestamp oldDate = Timestamp.from(Instant.parse("2020-01-01T00:00:00Z"));
        seed("InventoryItemStatus", values("inventoryItemId", fixture.inventoryItemId(),
                "statusId", "INV_AVAILABLE", "statusDatetime", oldDate));
        seedStatus("INV_ON_HOLD", "Inventory Item On Hold");

        invoke("createInventoryItemStatus", values(
                "inventoryItemId", fixture.inventoryItemId(), "statusId", "INV_ON_HOLD"));

        Object oldStatus = one("InventoryItemStatus", values("inventoryItemId", fixture.inventoryItemId(),
                "statusId", "INV_AVAILABLE", "statusDatetime", oldDate));
        assertNotNull(entities.get(oldStatus, "statusEndDatetime"));
        List<?> newStatuses = entities.findByEquality(entities.entityClass("InventoryItemStatus"),
                values("inventoryItemId", fixture.inventoryItemId(), "statusId", "INV_ON_HOLD"));
        assertEquals(1, newStatuses.size());
        assertEquals(fixture.productId(), entities.get(newStatuses.get(0), "productId"));
        assertEquals("inventory", entities.get(newStatuses.get(0), "changeByUserLoginId"));
    }

    @Test
    void ac10_createInventoryItemDetailUpdatesItemTotalsAndLastCount() throws Exception {
        Fixture fixture = fixture(true, "NON_SERIAL_INV_ITEM", "INV_AVAILABLE", BigDecimal.ZERO, BigDecimal.ZERO);
        seed("ProductFacility", values("productId", fixture.productId(), "facilityId", fixture.facilityId(),
                "lastInventoryCount", BigDecimal.ZERO));

        JsonNode response = invoke("createInventoryItemDetail", values(
                "inventoryItemId", fixture.inventoryItemId(), "availableToPromiseDiff", 4, "quantityOnHandDiff", 6));

        assertTrue(response.hasNonNull("inventoryItemDetailSeqId"));
        Object detail = one("InventoryItemDetail", values("inventoryItemId", fixture.inventoryItemId(),
                "inventoryItemDetailSeqId", response.get("inventoryItemDetailSeqId").asText()));
        assertNotNull(detail);
        Object item = one("InventoryItem", values("inventoryItemId", fixture.inventoryItemId()));
        assertDecimal("4", entities.get(item, "availableToPromiseTotal"));
        assertDecimal("6", entities.get(item, "quantityOnHandTotal"));
        Object productFacility = one("ProductFacility",
                values("productId", fixture.productId(), "facilityId", fixture.facilityId()));
        assertDecimal("4", entities.get(productFacility, "lastInventoryCount"));
    }

    @Test
    void ac11_updateInventoryItemFromDetailSumsExistingRows() throws Exception {
        Fixture fixture = fixture(true, "NON_SERIAL_INV_ITEM", "INV_AVAILABLE", BigDecimal.ZERO, BigDecimal.ZERO);
        seedDetail(fixture.inventoryItemId(), "00001", 2, 3, Timestamp.from(Instant.parse("2020-01-01T00:00:00Z")));
        seedDetail(fixture.inventoryItemId(), "00002", 5, 7, Timestamp.from(Instant.parse("2020-01-02T00:00:00Z")));

        invoke("updateInventoryItemFromDetail", values("inventoryItemId", fixture.inventoryItemId()));

        Object item = one("InventoryItem", values("inventoryItemId", fixture.inventoryItemId()));
        assertDecimal("7", entities.get(item, "availableToPromiseTotal"));
        assertDecimal("10", entities.get(item, "quantityOnHandTotal"));
    }

    @Test
    void ac12_updateSerializedInventoryTotalsUsesStatusTotals() throws Exception {
        Fixture available = fixture(true, "SERIALIZED_INV_ITEM", "INV_AVAILABLE",
                BigDecimal.ZERO, BigDecimal.ZERO);
        Fixture delivered = fixture(true, "SERIALIZED_INV_ITEM", "INV_DELIVERED",
                BigDecimal.ONE, BigDecimal.ONE);

        invoke("updateSerializedInventoryTotals", values("inventoryItemId", available.inventoryItemId()));
        invoke("updateSerializedInventoryTotals", values("inventoryItemId", delivered.inventoryItemId()));

        Object availableItem = one("InventoryItem", values("inventoryItemId", available.inventoryItemId()));
        assertDecimal("1", entities.get(availableItem, "availableToPromiseTotal"));
        assertDecimal("1", entities.get(availableItem, "quantityOnHandTotal"));
        Object deliveredItem = one("InventoryItem", values("inventoryItemId", delivered.inventoryItemId()));
        assertDecimal("0", entities.get(deliveredItem, "availableToPromiseTotal"));
        assertDecimal("0", entities.get(deliveredItem, "quantityOnHandTotal"));
    }

    @Test
    void ac13_checkProductInventoryDiscontinuationClosesActiveAssociations() throws Exception {
        Fixture fixture = fixture(true, "NON_SERIAL_INV_ITEM", "INV_AVAILABLE", BigDecimal.ZERO, BigDecimal.ZERO);
        String categoryId = id();
        seed("ProductCategory", values("productCategoryId", categoryId));
        seed("ProductCategoryMember", values("productCategoryId", categoryId, "productId", fixture.productId(),
                "fromDate", Timestamp.from(Instant.parse("2020-01-01T00:00:00Z"))));
        String associatedProductId = id();
        seed("ProductType", values("productTypeId", id(), "isPhysical", "Y"));
        String associatedTypeId = id();
        seed("ProductType", values("productTypeId", associatedTypeId, "isPhysical", "Y"));
        seed("Product", values("productId", associatedProductId, "productTypeId", associatedTypeId));
        seed("ProductAssocType", values("productAssocTypeId", "TEST_ASSOC"));
        seed("ProductAssoc", values("productId", associatedProductId, "productIdTo", fixture.productId(),
                "productAssocTypeId", "TEST_ASSOC", "fromDate", Timestamp.from(Instant.parse("2020-01-01T00:00:00Z"))));
        Object product = one("Product", values("productId", fixture.productId()));
        entities.set(product, "salesDiscWhenNotAvail", "Y");
        entityManager.flush();

        invoke("checkProductInventoryDiscontinuation", values("productId", fixture.productId()));

        product = one("Product", values("productId", fixture.productId()));
        assertNotNull(entities.get(product, "salesDiscontinuationDate"));
        assertNotNull(entities.get(one("ProductCategoryMember",
                values("productCategoryId", categoryId, "productId", fixture.productId())), "thruDate"));
        assertNotNull(entities.get(one("ProductAssoc",
                values("productId", associatedProductId, "productIdTo", fixture.productId(),
                        "productAssocTypeId", "TEST_ASSOC")), "thruDate"));
    }

    @Test
    void ac14_createInventoryItemVariancePersistsVarianceAndDetail() throws Exception {
        Fixture fixture = fixture(true, "NON_SERIAL_INV_ITEM", "INV_AVAILABLE", BigDecimal.ZERO, BigDecimal.ZERO);
        String physicalId = id();
        seed("PhysicalInventory", values("physicalInventoryId", physicalId));
        seedVarianceReason();

        invoke("createInventoryItemVariance", values("inventoryItemId", fixture.inventoryItemId(),
                "physicalInventoryId", physicalId, "varianceReasonId", "TEST_VARIANCE",
                "availableToPromiseVar", 2, "quantityOnHandVar", 3, "comments", "count"));

        Object variance = one("InventoryItemVariance", values("inventoryItemId", fixture.inventoryItemId(),
                "physicalInventoryId", physicalId));
        assertDecimal("2", entities.get(variance, "availableToPromiseVar"));
        assertEquals(1, count("InventoryItemDetail", values("inventoryItemId", fixture.inventoryItemId())));
    }

    @Test
    void ac15_createInventoryItemVarianceRejectsSerializedItems() throws Exception {
        Fixture fixture = fixture(true, "SERIALIZED_INV_ITEM", "INV_AVAILABLE", BigDecimal.ZERO, BigDecimal.ZERO);
        String physicalId = id();
        seed("PhysicalInventory", values("physicalInventoryId", physicalId));
        seedVarianceReason();

        MvcResult result = invokeRaw("createInventoryItemVariance", values("inventoryItemId", fixture.inventoryItemId(),
                "physicalInventoryId", physicalId, "varianceReasonId", "TEST_VARIANCE",
                "availableToPromiseVar", 1, "quantityOnHandVar", 1));

        assertEquals(400, result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("Non-Serialized Inventory Item"));
    }

    @Test
    void ac16_createPhysicalInventoryAndVariancePersistsBothRows() throws Exception {
        Fixture fixture = fixture(true, "NON_SERIAL_INV_ITEM", "INV_AVAILABLE", BigDecimal.ZERO, BigDecimal.ZERO);
        seedVarianceReason();

        JsonNode response = invoke("createPhysicalInventoryAndVariance", values(
                "inventoryItemId", fixture.inventoryItemId(), "varianceReasonId", "TEST_VARIANCE",
                "availableToPromiseVar", 1, "quantityOnHandVar", 2, "generalComments", "cycle count"));

        String physicalId = response.path("physicalInventoryId").asText();
        assertFalse(physicalId.isEmpty());
        assertEquals(1, count("PhysicalInventory", values("physicalInventoryId", physicalId)));
        assertEquals(1, count("InventoryItemVariance", values("inventoryItemId", fixture.inventoryItemId(),
                "physicalInventoryId", physicalId)));
    }

    @Test
    void ac17_getProductInventoryAvailableFiltersStatuses() throws Exception {
        Fixture fixture = fixture(true, "NON_SERIAL_INV_ITEM", "INV_AVAILABLE", new BigDecimal("2"), new BigDecimal("3"));
        Fixture held = fixtureWithProductAndFacility(fixture.productId(), fixture.facilityId(),
                "NON_SERIAL_INV_ITEM", "INV_ON_HOLD", new BigDecimal("7"), new BigDecimal("8"));
        JsonNode unfiltered = invoke("getProductInventoryAvailable", values("productId", fixture.productId()));
        assertDecimal("2", unfiltered.path("availableToPromiseTotal").decimalValue());
        assertDecimal("3", unfiltered.path("quantityOnHandTotal").decimalValue());
        JsonNode filtered = invoke("getProductInventoryAvailable", values(
                "productId", fixture.productId(), "statusId", "INV_ON_HOLD"));
        assertDecimal("7", filtered.path("availableToPromiseTotal").decimalValue());
        assertDecimal("8", filtered.path("quantityOnHandTotal").decimalValue());
        assertEquals(fixture.productId(), entities.get(one("InventoryItem",
                values("inventoryItemId", held.inventoryItemId())), "productId"));
    }

    @Test
    void ac18_countProductInventoryOnHandIncludesOnlyDetailsBeforeCountDate() throws Exception {
        Fixture fixture = fixture(true, "NON_SERIAL_INV_ITEM", "INV_AVAILABLE", BigDecimal.ZERO, BigDecimal.ZERO);
        seedDetail(fixture.inventoryItemId(), "00001", 0, 3, Timestamp.from(Instant.parse("2020-01-01T00:00:00Z")));
        seedDetail(fixture.inventoryItemId(), "00002", 0, 9, Timestamp.from(Instant.parse("2020-02-01T00:00:00Z")));

        JsonNode response = invoke("countProductInventoryOnHand", values(
                "productId", fixture.productId(), "facilityId", fixture.facilityId(),
                "inventoryCountDate", "2020-01-15 00:00:00.000"));

        assertDecimal("3", response.path("quantityOnHandTotal").decimalValue());
    }

    @Test
    void ac19_countProductInventoryShippedForSalesReturnsPositiveShippedQuantity() throws Exception {
        Fixture fixture = fixture(true, "NON_SERIAL_INV_ITEM", "INV_AVAILABLE", BigDecimal.ZERO, BigDecimal.ZERO);
        seedDetail(fixture.inventoryItemId(), "00001", 0, -4,
                Timestamp.from(Instant.parse("2020-01-02T00:00:00Z")), "ORDER_TEST");
        seedDetail(fixture.inventoryItemId(), "00002", 0, -6,
                Timestamp.from(Instant.parse("2020-02-02T00:00:00Z")), "ORDER_TEST");

        JsonNode response = invoke("countProductInventoryShippedForSales", values(
                "productId", fixture.productId(), "facilityId", fixture.facilityId(),
                "fromDate", "2020-01-01 00:00:00.000",
                "thruDate", "2020-02-01 00:00:00.000"));

        assertDecimal("4", response.path("quantityOnHandTotal").decimalValue());
    }

    @Test
    void ac20_getMktgPackagesAvailableReturnsZeroWithoutComponentsAnd501WithComponents() throws Exception {
        Fixture fixture = fixture(true, "NON_SERIAL_INV_ITEM", "INV_AVAILABLE", BigDecimal.ZERO, BigDecimal.ZERO);

        JsonNode response = invoke("getMktgPackagesAvailable", values("productId", fixture.productId()));

        assertDecimal("0", response.path("availableToPromiseTotal").decimalValue());
        assertDecimal("0", response.path("quantityOnHandTotal").decimalValue());
        String componentId = id();
        String componentTypeId = id();
        seed("ProductType", values("productTypeId", componentTypeId, "isPhysical", "Y"));
        seed("Product", values("productId", componentId, "productTypeId", componentTypeId));
        seed("ProductAssocType", values("productAssocTypeId", "PRODUCT_COMPONENT"));
        seed("ProductAssoc", values("productId", fixture.productId(), "productIdTo", componentId,
                "productAssocTypeId", "PRODUCT_COMPONENT",
                "fromDate", Timestamp.from(Instant.parse("2020-01-01T00:00:00Z"))));

        MvcResult result = invokeRaw("getMktgPackagesAvailable", values("productId", fixture.productId()));
        assertEquals(501, result.getResponse().getStatus());
    }

    @Test
    void ac21_balanceInventoryItemsWithoutReservationsLeavesInventoryUnchanged() throws Exception {
        Fixture fixture = fixture(true, "NON_SERIAL_INV_ITEM", "INV_AVAILABLE", new BigDecimal("5"), new BigDecimal("5"));
        Object before = one("InventoryItem", values("inventoryItemId", fixture.inventoryItemId()));

        JsonNode response = invoke("balanceInventoryItems", values("inventoryItemId", fixture.inventoryItemId()));

        Object after = one("InventoryItem", values("inventoryItemId", fixture.inventoryItemId()));
        assertSuccessWithoutOutputs(response);
        assertDecimal("5", entities.get(after, "availableToPromiseTotal"));
        assertDecimal("5", entities.get(after, "quantityOnHandTotal"));
        assertEquals(entities.get(before, "statusId"), entities.get(after, "statusId"));
    }

    @Test
    void ac22_reassignInventoryReservationsWithoutRowsReturnsEmptyOutputs() throws Exception {
        Fixture fixture = fixture(true, "NON_SERIAL_INV_ITEM", "INV_AVAILABLE", BigDecimal.ONE, BigDecimal.ONE);

        JsonNode response = invoke("reassignInventoryReservations", values(
                "productId", fixture.productId(), "facilityId", fixture.facilityId()));

        assertSuccessWithoutOutputs(response);
    }

    @Test
    void ac23_reassignInventoryReservationsWithMatchingReservationReallocatesIt() throws Exception {
        Fixture fixture = fixture(true, "NON_SERIAL_INV_ITEM", "INV_AVAILABLE", BigDecimal.ONE, BigDecimal.ONE);
        String orderId = id();
        seedDetail(fixture.inventoryItemId(), "00001", 1, 1,
                Timestamp.from(Instant.now().minusSeconds(3600)));
        seedReservation(fixture, orderId, "1");

        JsonNode response = invoke("reassignInventoryReservations", values(
                "productId", fixture.productId(), "facilityId", fixture.facilityId()));

        assertSuccessWithoutOutputs(response);
        Object reservation = one("OrderItemShipGrpInvRes", values(
                "orderId", orderId, "orderItemSeqId", "00001",
                "shipGroupSeqId", "00001", "inventoryItemId", fixture.inventoryItemId()));
        assertDecimal("1", entities.get(reservation, "quantity"));
        assertNull(entities.get(reservation, "quantityNotAvailable"));
        Object inventoryItem = one("InventoryItem", values("inventoryItemId", fixture.inventoryItemId()));
        assertDecimal("1", entities.get(inventoryItem, "availableToPromiseTotal"));
        assertDecimal("1", entities.get(inventoryItem, "quantityOnHandTotal"));
    }

    @Test
    void ac24_reassignInventoryReservationsByAllocationPlanWithNoPlanItemsReturnsEmpty() throws Exception {
        Fixture fixture = fixture(true, "NON_SERIAL_INV_ITEM", "INV_AVAILABLE", BigDecimal.ONE, BigDecimal.ONE);

        JsonNode response = invoke("reassignInventoryReservationsByAllocationPlan", values(
                "productId", fixture.productId(), "facilityId", fixture.facilityId(),
                "allocationPlanAndItemList", List.of()));

        assertSuccessWithoutOutputs(response);
    }

    @Test
    void ac25_balanceOrderItemsWithNegativeReservationsWithoutRowsReturnsEmpty() throws Exception {
        String orderId = id();
        String productStoreId = id();
        seed("ProductStore", values("productStoreId", productStoreId, "balanceResOnOrderCreation", "N"));
        seed("OrderHeader", values("orderId", orderId, "productStoreId", productStoreId));

        JsonNode response = invoke("balanceOrderItemsWithNegativeReservations", values("orderId", orderId));

        assertSuccessWithoutOutputs(response);
    }

    @Test
    void ac26_createInventoryTransferReturnsIdAndPersistsRequestedFields() throws Exception {
        Fixture fixture = fixture(true, "NON_SERIAL_INV_ITEM", "INV_AVAILABLE", new BigDecimal("5"), new BigDecimal("5"));
        String destinationId = id();
        seed("Facility", values("facilityId", destinationId));
        seedStatus("IXF_REQUESTED", "Requested");

        JsonNode response = invoke("createInventoryTransfer", values(
                "inventoryItemId", fixture.inventoryItemId(), "statusId", "IXF_REQUESTED",
                "facilityId", fixture.facilityId(),
                "facilityIdTo", destinationId, "xferQty", 2, "comments", "test transfer"));

        String transferId = response.path("inventoryTransferId").asText();
        assertFalse(transferId.isEmpty());
        Object transfer = one("InventoryTransfer", values("inventoryTransferId", transferId));
        assertEquals(fixture.inventoryItemId(), entities.get(transfer, "inventoryItemId"));
        assertEquals(fixture.facilityId(), entities.get(transfer, "facilityId"));
        assertEquals(destinationId, entities.get(transfer, "facilityIdTo"));
        assertEquals("test transfer", entities.get(transfer, "comments"));
    }

    @Test
    void ac27_updateInventoryTransferAllowsConfiguredStatusAndRejectsInvalidStatus() throws Exception {
        Fixture fixture = fixture(true, "NON_SERIAL_INV_ITEM", "INV_AVAILABLE", BigDecimal.ONE, BigDecimal.ONE);
        String destinationId = id();
        seed("Facility", values("facilityId", destinationId));
        seedStatus("IXF_REQUESTED", "Requested");
        seedStatus("IXF_IN_TRANSIT", "In Transit");
        seedStatus("IXF_RECEIVED", "Received");
        seed("StatusValidChange", values("statusId", "IXF_REQUESTED", "statusIdTo", "IXF_IN_TRANSIT"));
        String transferId = id();
        seed("InventoryTransfer", values("inventoryTransferId", transferId, "statusId", "IXF_REQUESTED",
                "inventoryItemId", fixture.inventoryItemId(), "facilityId", fixture.facilityId(),
                "facilityIdTo", destinationId));

        invoke("updateInventoryTransfer", values("inventoryTransferId", transferId,
                "inventoryItemId", fixture.inventoryItemId(), "statusId", "IXF_IN_TRANSIT"));

        Object transfer = one("InventoryTransfer", values("inventoryTransferId", transferId));
        assertEquals("IXF_IN_TRANSIT", entities.get(transfer, "statusId"));
        MvcResult result = invokeRaw("updateInventoryTransfer",
                values("inventoryTransferId", transferId, "inventoryItemId", fixture.inventoryItemId(),
                        "statusId", "IXF_RECEIVED"));
        assertEquals(400, result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains("Changing the status"));
    }

    @Test
    void ac28_createInventoryTransfersForProductTransfersAvailableQuantity() throws Exception {
        Fixture fixture = fixture(true, "NON_SERIAL_INV_ITEM", "INV_AVAILABLE", new BigDecimal("5"), new BigDecimal("5"));
        String destinationId = id();
        seed("Facility", values("facilityId", destinationId));
        seedStatus("IXF_REQUESTED", "Requested");

        JsonNode response = invoke("createInventoryTransfersForProduct", values(
                "facilityId", fixture.facilityId(), "facilityIdTo", destinationId,
                "productId", fixture.productId(), "quantity", 3));

        assertDecimal("0", response.path("quantityNotTransferred").decimalValue());
        assertEquals(1, count("InventoryTransfer", values("facilityId", fixture.facilityId(),
                "facilityIdTo", destinationId)));
    }

    @Test
    void ac29_createInventoryTransfersForProductRejectsInsufficientAtpAndReturnsZeroForNonPhysical() throws Exception {
        Fixture physical = fixture(true, "NON_SERIAL_INV_ITEM", "INV_AVAILABLE", BigDecimal.ONE, BigDecimal.ONE);
        String destinationId = id();
        seed("Facility", values("facilityId", destinationId));
        seedStatus("IXF_REQUESTED", "Requested");
        MvcResult insufficient = invokeRaw("createInventoryTransfersForProduct", values(
                "facilityId", physical.facilityId(), "facilityIdTo", destinationId,
                "productId", physical.productId(), "quantity", 3));
        assertEquals(400, insufficient.getResponse().getStatus());
        assertTrue(insufficient.getResponse().getContentAsString().contains(
                "Requested quantity [3] could not be transferred because [2] units of inventory is unavailable."),
                insufficient.getResponse().getContentAsString());
        String typeId = id();
        String productId = id();
        String nonPhysicalDestinationId = id();
        seed("ProductType", values("productTypeId", typeId, "isPhysical", "N"));
        seed("Product", values("productId", productId, "productTypeId", typeId));
        JsonNode nonPhysical = invoke("createInventoryTransfersForProduct", values(
                "facilityId", id(), "facilityIdTo", nonPhysicalDestinationId,
                "productId", productId, "quantity", 3));
        assertDecimal("0", nonPhysical.path("quantityNotTransferred").decimalValue());
        assertEquals(0, count("InventoryTransfer", values("facilityIdTo", nonPhysicalDestinationId)));
    }

    @Test
    void ac30_changeOwnerUponIssuanceUpdatesSerializedItemOwner() throws Exception {
        Fixture fixture = fixture(true, "SERIALIZED_INV_ITEM", "INV_AVAILABLE", BigDecimal.ONE, BigDecimal.ONE);
        String orderId = id();
        String partyId = id();
        String storeId = id();
        String issuanceId = id();
        seed("Party", values("partyId", partyId));
        seed("RoleType", values("roleTypeId", "END_USER_CUSTOMER"));
        seed("ProductStore", values("productStoreId", storeId, "setOwnerUponIssuance", "Y"));
        seed("OrderHeader", values("orderId", orderId, "productStoreId", storeId));
        seed("OrderItem", values("orderId", orderId, "orderItemSeqId", "00001",
                "productId", fixture.productId(), "quantity", BigDecimal.ONE));
        seed("OrderRole", values("orderId", orderId, "partyId", partyId, "roleTypeId", "END_USER_CUSTOMER"));
        seed("ItemIssuance", values("itemIssuanceId", issuanceId, "inventoryItemId", fixture.inventoryItemId(),
                "orderId", orderId, "orderItemSeqId", "00001"));

        invoke("changeOwnerUponIssuance", values("itemIssuanceId", issuanceId));

        Object item = one("InventoryItem", values("inventoryItemId", fixture.inventoryItemId()));
        assertEquals(partyId, entities.get(item, "ownerPartyId"));
    }

    @Test
    void ac31_setOrderReservationPriorityDefaultsToTwoAndUpdatesReservations() throws Exception {
        Fixture fixture = fixture(true, "NON_SERIAL_INV_ITEM", "INV_AVAILABLE", BigDecimal.ONE, BigDecimal.ONE);
        String orderId = id();
        seedOrderAndItem(orderId, fixture.productId());
        seedReservation(fixture, orderId, "1");

        JsonNode response = invoke("setOrderReservationPriority", values("orderId", orderId));

        assertSuccessWithoutOutputs(response);
        Object order = one("OrderHeader", values("orderId", orderId));
        assertEquals("2", String.valueOf(entities.get(order, "priority")));
        Object reservation = one("OrderItemShipGrpInvRes", values("orderId", orderId,
                "orderItemSeqId", "00001", "inventoryItemId", fixture.inventoryItemId()));
        assertEquals("2", String.valueOf(entities.get(reservation, "priority")));
    }

    @Test
    void ac32_setLastInventoryCountStoresAvailableToPromiseTotal() throws Exception {
        Fixture fixture = fixture(true, "NON_SERIAL_INV_ITEM", "INV_AVAILABLE", new BigDecimal("6"), new BigDecimal("7"));
        seed("ProductFacility", values("productId", fixture.productId(), "facilityId", fixture.facilityId(),
                "lastInventoryCount", BigDecimal.ZERO));

        invoke("setLastInventoryCount", values("inventoryItemId", fixture.inventoryItemId()));

        Object productFacility = one("ProductFacility",
                values("productId", fixture.productId(), "facilityId", fixture.facilityId()));
        assertDecimal("6", entities.get(productFacility, "lastInventoryCount"));
    }

    @Test
    void ac33_createInventoryItemLabelApplCopiesLabelType() throws Exception {
        Fixture fixture = fixture(true, "NON_SERIAL_INV_ITEM", "INV_AVAILABLE", BigDecimal.ZERO, BigDecimal.ZERO);
        String labelTypeId = id();
        String labelId = id();
        seed("InventoryItemLabelType", values("inventoryItemLabelTypeId", labelTypeId));
        seed("InventoryItemLabel", values("inventoryItemLabelId", labelId, "inventoryItemLabelTypeId", labelTypeId));

        invoke("createInventoryItemLabelAppl", values(
                "inventoryItemId", fixture.inventoryItemId(), "inventoryItemLabelId", labelId));

        Object application = one("InventoryItemLabelAppl",
                values("inventoryItemId", fixture.inventoryItemId(), "inventoryItemLabelTypeId", labelTypeId));
        assertEquals(labelId, entities.get(application, "inventoryItemLabelId"));
    }

    private Fixture fixture(boolean physical, String itemTypeId, String statusId, BigDecimal atp, BigDecimal qoh) {
        String productTypeId = id();
        String productId = id();
        String facilityId = id();
        String inventoryItemId = id();
        seed("ProductType", values("productTypeId", productTypeId, "isPhysical", physical ? "Y" : "N"));
        seed("Product", values("productId", productId, "productTypeId", productTypeId));
        seed("Facility", values("facilityId", facilityId));
        ensure("InventoryItemType", values("inventoryItemTypeId", itemTypeId));
        if (statusId != null) {
            seedStatus(statusId, statusId);
        }
        seed("InventoryItem", values("inventoryItemId", inventoryItemId,
                "inventoryItemTypeId", itemTypeId, "productId", productId, "facilityId", facilityId,
                "statusId", statusId, "availableToPromiseTotal", atp, "quantityOnHandTotal", qoh));
        return new Fixture(productId, facilityId, inventoryItemId);
    }

    private Fixture fixtureWithProductAndFacility(String productId, String facilityId,
            String itemTypeId, String statusId, BigDecimal atp, BigDecimal qoh) {
        String inventoryItemId = id();
        ensure("InventoryItemType", values("inventoryItemTypeId", itemTypeId));
        if (statusId != null) {
            seedStatus(statusId, statusId);
        }
        seed("InventoryItem", values("inventoryItemId", inventoryItemId,
                "inventoryItemTypeId", itemTypeId, "productId", productId, "facilityId", facilityId,
                "statusId", statusId, "availableToPromiseTotal", atp, "quantityOnHandTotal", qoh));
        return new Fixture(productId, facilityId, inventoryItemId);
    }

    private void seedOrderAndItem(String orderId, String productId) {
        ensure("OrderHeader", values("orderId", orderId));
        ensure("OrderItem", values("orderId", orderId, "orderItemSeqId", "00001",
                "productId", productId, "quantity", BigDecimal.ONE));
    }

    private void seedReservation(Fixture fixture, String orderId, String quantityNotAvailable) {
        seedOrderAndItem(orderId, fixture.productId());
        seed("OrderItemShipGrpInvRes", values("orderId", orderId, "shipGroupSeqId", "00001",
                "orderItemSeqId", "00001", "inventoryItemId", fixture.inventoryItemId(),
                "quantity", BigDecimal.ONE, "quantityNotAvailable", new BigDecimal(quantityNotAvailable),
                "priority", "2", "sequenceId", 1));
    }

    private void seedDetail(String inventoryItemId, String sequenceId, Number atp, Number qoh, Timestamp date) {
        seedDetail(inventoryItemId, sequenceId, atp, qoh, date, null);
    }

    private void seedDetail(String inventoryItemId, String sequenceId, Number atp, Number qoh,
            Timestamp date, String orderId) {
        seed("InventoryItemDetail", values("inventoryItemId", inventoryItemId,
                "inventoryItemDetailSeqId", sequenceId, "effectiveDate", date,
                "availableToPromiseDiff", new BigDecimal(atp.toString()),
                "quantityOnHandDiff", new BigDecimal(qoh.toString()),
                "accountingQuantityDiff", BigDecimal.ZERO, "orderId", orderId));
    }

    private void seedStatus(String statusId, String description) {
        if (entities.findByPrimaryKey(entities.entityClass("StatusItem"),
                values("statusId", statusId)) == null) {
            seed("StatusItem", values("statusId", statusId, "description", description));
        }
    }

    private void seedVarianceReason() {
        seed("VarianceReason", values("varianceReasonId", "TEST_VARIANCE"));
        ensure("Enumeration", values("enumId", "TEST_VARIANCE", "description", "Test variance"));
    }

    private void ensure(String entityName, Map<String, Object> fields) {
        if (entities.findByEquality(entities.entityClass(entityName), fields).isEmpty()) {
            seed(entityName, fields);
        }
    }

    private Object seed(String entityName, Map<String, Object> fields) {
        Object entity = entities.newEntity(entities.entityClass(entityName));
        fields.forEach((field, value) -> entities.set(entity, field, value));
        entityManager.persist(entity);
        entityManager.flush();
        return entity;
    }

    private Object one(String entityName, Map<String, Object> fields) {
        List<?> results = entities.findByEquality(entities.entityClass(entityName), fields);
        assertEquals(1, results.size(), entityName + " expected exactly one matching row");
        return results.get(0);
    }

    private int count(String entityName, Map<String, Object> fields) {
        return entities.findByEquality(entities.entityClass(entityName), fields).size();
    }

    private JsonNode invoke(String serviceName, Map<String, Object> input) throws Exception {
        MvcResult result = invokeRaw(serviceName, input);
        assertEquals(200, result.getResponse().getStatus(), result.getResponse().getContentAsString());
        return JSON.readTree(result.getResponse().getContentAsByteArray());
    }

    private MvcResult invokeRaw(String serviceName, Map<String, Object> input) throws Exception {
        return mockMvc.perform(post("/api/product/services/" + serviceName)
                        .with(user("inventory").authorities(adminAuthorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(input)))
                .andReturn();
    }

    private static Map<String, Object> values(Object... pairs) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int index = 0; index < pairs.length; index += 2) {
            result.put((String) pairs[index], pairs[index + 1]);
        }
        return result;
    }

    private static String id() {
        return "ITST_" + String.format("%010d", IDS.incrementAndGet());
    }

    private static void assertDecimal(String expected, Object actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(new BigDecimal(actual.toString())));
    }

    private static void assertSuccessWithoutOutputs(JsonNode response) {
        assertEquals("success", response.path("responseMessage").asText());
    }

    private record Fixture(String productId, String facilityId, String inventoryItemId) {
    }

    private static SimpleGrantedAuthority[] adminAuthorities() {
        return java.util.Arrays.stream(new String[] {"OFBTOOLS_ADMIN", "CATALOG_ADMIN", "FACILITY_ADMIN",
                        "PRODUCT_ADMIN", "PRODUCT_SENSITIVE_VIEW", "CATALOG_PRICE_MAINT", "CATALOG_CREATE",
                        "CATALOG_UPDATE", "CATALOG_DELETE", "FACILITY_CREATE", "FACILITY_UPDATE",
                        "FACILITY_DELETE"})
                .map(SimpleGrantedAuthority::new).toArray(SimpleGrantedAuthority[]::new);
    }

    private static SimpleGrantedAuthority[] readOnlyAuthorities() {
        return new SimpleGrantedAuthority[] {
            new SimpleGrantedAuthority("OFBTOOLS_VIEW"),
            new SimpleGrantedAuthority("FACILITY_VIEW"),
            new SimpleGrantedAuthority("PRODUCT_VIEW")
        };
    }
}
