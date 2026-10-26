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
 * distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.apache.ofbiz.migration.product;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.product.simple.InventoryServices;
import org.apache.ofbiz.migration.product.simple.StockMoveServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
        classes = ProductMigrationApplication.class,
        properties = "migration.security.require-https=false")
@AutoConfigureMockMvc
@Transactional
class StockMoveServicesTest {
    private static final BigDecimal ZERO = new BigDecimal("0.00");
    private static final BigDecimal ONE = new BigDecimal("1.00");
    private final AtomicInteger detailSequence = new AtomicInteger();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EntityRegistry entities;

    @MockitoSpyBean
    private StockMoveServices stockMoveServices;

    @MockitoSpyBean
    private InventoryServices inventoryServices;

    @BeforeEach
    void stubInventoryServices() {
        doAnswer(invocation -> {
            String serviceName = invocation.getArgument(0);
            Map<String, Object> input = invocation.getArgument(2);
            if ("updateSerializedInventoryTotals".equals(serviceName)) {
                return Map.of();
            }
            if ("createInventoryItemDetail".equals(serviceName)) {
                return createInventoryItemDetail(input);
            }
            return invocation.callRealMethod();
        }).when(inventoryServices).execute(anyString(), anyString(), anyMap());
    }

    @Test
    void findStockMovesNeededCombinesReservationAndPickRestockQuantities() throws Exception {
        Seed seed = seed(
                "NON_SERIAL_INV_ITEM", "ITEM_APPROVED", bd("12"), bd("12"), bd("0"), bd("0"),
                bd("10"), bd("5"), bd("3"));

        postService("findStockMovesNeeded", map("facilityId", seed.facilityId()), "FACILITY_VIEW")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.moveByOisgirInfoList[0].totalQuantity").value(8.0))
                .andExpect(jsonPath("$.moveByOisgirInfoList[0].quantityOnHandTotalFrom").value(12.0))
                .andExpect(jsonPath("$.moveByOisgirInfoList[0].quantityOnHandTotalTo").value(0.0))
                .andExpect(jsonPath("$.stockMoveHandled['" + seed.pickLocationId() + "']").value("Y"))
                .andExpect(jsonPath("$.warningMessageList").doesNotExist());
    }

    @Test
    void findStockMovesNeededReportsInsufficientSourceStock() throws Exception {
        Seed seed = seed(
                "NON_SERIAL_INV_ITEM", "ITEM_APPROVED", bd("2"), bd("2"), bd("0"), bd("0"),
                bd("10"), bd("5"), bd("3"));

        postService("findStockMovesNeeded", map("facilityId", seed.facilityId()), "FACILITY_VIEW")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.moveByOisgirInfoList[0].totalQuantity").value(2.0))
                .andExpect(jsonPath("$.warningMessageList[0]").value(
                        org.hamcrest.Matchers.containsString("there are only [2.00] on hand")));
    }

    @Test
    void findStockMovesNeededWarnsWhenThereIsNoPickLocation() throws Exception {
        Seed seed = seed(
                "NON_SERIAL_INV_ITEM", "ITEM_APPROVED", bd("5"), bd("5"), null, null,
                bd("10"), bd("5"), bd("3"));
        Object pickLocation = entities.findByPrimaryKey(
                entities.entityClass("ProductFacilityLocation"),
                map("productId", seed.productId(),
                        "facilityId", seed.facilityId(),
                        "locationSeqId", seed.pickLocationId()));
        entityManager.remove(pickLocation);

        postService("findStockMovesNeeded", map("facilityId", seed.facilityId()), "FACILITY_VIEW")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.warningMessageList[0]").value(
                        org.hamcrest.Matchers.containsString("could not find a pick/primary location")));
    }

    @Test
    void findStockMovesNeededReturnsBadRequestForReachableNullOrderedComparison() throws Exception {
        Seed seed = seed(
                "NON_SERIAL_INV_ITEM", "ITEM_APPROVED", bd("5"), bd("5"), null, null,
                bd("10"), bd("5"), bd("3"));
        Object pickLocation = entities.findByPrimaryKey(
                entities.entityClass("ProductFacilityLocation"),
                map("productId", seed.productId(),
                        "facilityId", seed.facilityId(),
                        "locationSeqId", seed.pickLocationId()));
        entities.set(pickLocation, "minimumStock", null);

        postService("findStockMovesNeeded", map("facilityId", seed.facilityId()), "FACILITY_VIEW")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Cannot compare: r-value is null"));
    }

    @Test
    void findStockMovesRecommendedUsesAvailableBulkStock() throws Exception {
        Seed seed = seed(
                "NON_SERIAL_INV_ITEM", null, bd("8"), bd("8"), bd("0"), bd("0"),
                bd("10"), bd("4"), null);

        postService("findStockMovesRecommended", map("facilityId", seed.facilityId()), "FACILITY_VIEW")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.moveByPflInfoList[0].totalQuantity").value(4.0))
                .andExpect(jsonPath("$.moveByPflInfoList[0].availableToPromiseTotalFrom").value(8.0))
                .andExpect(jsonPath("$.warningMessageList").doesNotExist());
    }

    @Test
    void findStockMovesRecommendedSkipsHandledPickLocations() throws Exception {
        Seed seed = seed(
                "NON_SERIAL_INV_ITEM", null, bd("8"), bd("8"), bd("0"), bd("0"),
                bd("10"), bd("4"), null);

        postService("findStockMovesRecommended",
                map("facilityId", seed.facilityId(), "stockMoveHandled", Map.of(seed.pickLocationId(), "Y")),
                "FACILITY_VIEW")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.moveByPflInfoList").doesNotExist());
    }

    @Test
    void findStockMovesRecommendedWarnsWhenNoBulkLocationExists() throws Exception {
        Seed seed = seed(
                "NON_SERIAL_INV_ITEM", null, bd("0"), bd("0"), bd("0"), bd("0"),
                bd("10"), bd("4"), null);
        Object bulkInventoryItem = seed.inventoryItems().get(0);
        entityManager.remove(entityManager.contains(bulkInventoryItem)
                ? bulkInventoryItem
                : entityManager.merge(bulkInventoryItem));

        postService("findStockMovesRecommended", map("facilityId", seed.facilityId()), "FACILITY_VIEW")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.warningMessageList[0]").value(
                        org.hamcrest.Matchers.containsString("could not find a bulk location")));
    }

    @Test
    void processPhysicalStockMoveTransfersUnreservedNonSerializedStock() throws Exception {
        Seed seed = seed(
                "NON_SERIAL_INV_ITEM", null, bd("3"), bd("3"), null, null, bd("0"), bd("0"), null);
        stubCreateInventoryItem();

        postService("processPhysicalStockMove", moveInput(seed, bd("2")),
                "FACILITY_VIEW", "FACILITY_CREATE", "FACILITY_UPDATE")
                .andExpect(status().isOk());

        List<?> targetItems = entities.findByEquality(
                entities.entityClass("InventoryItem"),
                Map.of("productId", seed.productId(), "locationSeqId", seed.pickLocationId()));
        org.junit.jupiter.api.Assertions.assertEquals(1, targetItems.size());
        assertDecimalValue("2", entities.get(targetItems.get(0), "quantityOnHandTotal"));
        assertDecimalValue("2", entities.get(targetItems.get(0), "availableToPromiseTotal"));
        assertDecimalValue("1", entities.get(seed.inventoryItems().get(0), "quantityOnHandTotal"));
    }

    @Test
    void processPhysicalStockMoveReportsUnfulfilledQuantity() throws Exception {
        Seed seed = seed(
                "NON_SERIAL_INV_ITEM", null, bd("1"), bd("1"), null, null, bd("0"), bd("0"), null);
        stubCreateInventoryItem();

        postService("processPhysicalStockMove", moveInput(seed, bd("2")),
                "FACILITY_VIEW", "FACILITY_CREATE", "FACILITY_UPDATE")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.warningMessageList[0]").value(
                        org.hamcrest.Matchers.containsString(
                                "did not reallocate 1.00 of the 2.0 reported as physically moved.")));
    }

    @Test
    void processPhysicalStockMoveSplitsReservationOntoTargetInventoryItem() throws Exception {
        Seed seed = seed(
                "NON_SERIAL_INV_ITEM", "ITEM_APPROVED", bd("5"), bd("5"), null, null, bd("10"), bd("5"), bd("3"));
        stubCreateInventoryItem();

        postService("processPhysicalStockMove", moveInput(seed, bd("2")),
                "FACILITY_VIEW", "FACILITY_CREATE", "FACILITY_UPDATE")
                .andExpect(status().isOk());

        List<?> targetItems = entities.findByEquality(
                entities.entityClass("InventoryItem"),
                Map.of("productId", seed.productId(), "locationSeqId", seed.pickLocationId()));
        org.junit.jupiter.api.Assertions.assertEquals(1, targetItems.size());
        String targetInventoryItemId = entities.get(targetItems.get(0), "inventoryItemId").toString();
        Object movedReservation = entities.findByPrimaryKey(
                entities.entityClass("OrderItemShipGrpInvRes"),
                map("orderId", seed.orderId(),
                        "shipGroupSeqId", "00001",
                        "orderItemSeqId", "00001",
                        "inventoryItemId", targetInventoryItemId));
        org.junit.jupiter.api.Assertions.assertNotNull(movedReservation);
        org.junit.jupiter.api.Assertions.assertEquals(bd("2"), entities.get(movedReservation, "quantity"));
    }

    @Test
    void processPhysicalStockMoveMovesSerializedItemAndUpdatesTotals() throws Exception {
        Seed seed = seed(
                "SERIALIZED_INV_ITEM", "ITEM_APPROVED", ONE, ONE, null, null, bd("10"), bd("5"), ONE);

        postService("processPhysicalStockMove", moveInput(seed, ONE),
                "FACILITY_VIEW", "FACILITY_CREATE", "FACILITY_UPDATE")
                .andExpect(status().isOk());

        Object inventoryItem = entities.findByPrimaryKey(
                entities.entityClass("InventoryItem"),
                Map.of("inventoryItemId", entities.get(seed.inventoryItems().get(0), "inventoryItemId")));
        org.junit.jupiter.api.Assertions.assertEquals(
                seed.pickLocationId(), entities.get(inventoryItem, "locationSeqId"));
        verify(inventoryServices, times(1)).execute(
                eq("updateSerializedInventoryTotals"), eq("updateSerializedInventoryTotals"), anyMap());
    }

    @Test
    void processPhysicalStockMoveReturnsNotImplementedWhenCreateSeamIsUnstubbed() throws Exception {
        Seed seed = seed(
                "NON_SERIAL_INV_ITEM", null, bd("3"), bd("3"), null, null, bd("0"), bd("0"), null);
        postService("processPhysicalStockMove", moveInput(seed, ONE),
                "FACILITY_VIEW", "FACILITY_CREATE", "FACILITY_UPDATE")
                .andExpect(status().isNotImplemented());

        entityManager.flush();
        entityManager.clear();
        Object sourceItem = entities.findByPrimaryKey(
                entities.entityClass("InventoryItem"),
                Map.of("inventoryItemId", entities.get(seed.inventoryItems().get(0), "inventoryItemId")));
        assertDecimalValue("3", entities.get(sourceItem, "quantityOnHandTotal"));
        assertDecimalValue("3", entities.get(sourceItem, "availableToPromiseTotal"));
        org.junit.jupiter.api.Assertions.assertTrue(entities.findByEquality(
                entities.entityClass("InventoryItem"),
                Map.of("productId", seed.productId(), "locationSeqId", seed.pickLocationId())).isEmpty());
        org.junit.jupiter.api.Assertions.assertTrue(entities.findByEquality(
                entities.entityClass("InventoryItemDetail"),
                Map.of("inventoryItemId", entities.get(sourceItem, "inventoryItemId"))).isEmpty());
    }

    @Test
    void processPhysicalStockMoveRequiresQuantityMovedAndBothFacilityPermissions() throws Exception {
        Seed seed = seed(
                "NON_SERIAL_INV_ITEM", null, bd("3"), bd("3"), null, null, bd("0"), bd("0"), null);
        postService("processPhysicalStockMove",
                map("productId", seed.productId(),
                        "facilityId", seed.facilityId(),
                        "locationSeqId", seed.bulkLocationId(),
                        "targetLocationSeqId", seed.pickLocationId()),
                "FACILITY_VIEW", "FACILITY_CREATE", "FACILITY_UPDATE")
                .andExpect(status().isBadRequest());

        postService("processPhysicalStockMove", moveInput(seed, ONE), "FACILITY_VIEW", "FACILITY_UPDATE")
                .andExpect(status().isForbidden());
    }

    @Test
    void findProductInventorylocationsExcludesNullLocationSequenceIds() throws Exception {
        Seed seed = seed(
                "NON_SERIAL_INV_ITEM", null, bd("3"), bd("3"), null, null, bd("0"), bd("0"), null);
        Object inventoryItemWithoutLocation = save("InventoryItem", map(
                "inventoryItemId", id("null-location"),
                "productId", seed.productId(),
                "facilityId", seed.facilityId(),
                "inventoryItemTypeId", "NON_SERIAL_INV_ITEM",
                "quantityOnHandTotal", ZERO,
                "availableToPromiseTotal", ZERO));

        postService("findProductInventorylocations",
                map("facilityId", seed.facilityId(), "productId", seed.productId()), "FACILITY_VIEW")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.LocationList.length()").value(1))
                .andExpect(jsonPath("$.LocationList[?(@.locationSeqId == null)]").doesNotExist());
        entityManager.remove(inventoryItemWithoutLocation);
    }

    @Test
    void servicePermissionsAndRequiredInputsAreEnforced() throws Exception {
        Seed seed = seed(
                "NON_SERIAL_INV_ITEM", null, bd("3"), bd("3"), null, null, bd("0"), bd("0"), null);

        postService("findStockMovesNeeded", map("facilityId", seed.facilityId()), "OFBTOOLS_VIEW")
                .andExpect(status().isForbidden());
        postService("findStockMovesNeeded", map(), "FACILITY_VIEW")
                .andExpect(status().isBadRequest());
        postService("findProductInventorylocations", map("facilityId", seed.facilityId()), "FACILITY_VIEW")
                .andExpect(status().isBadRequest());
    }

    private void stubCreateInventoryItem() {
        doAnswer(invocation -> {
            Map<String, Object> serviceInput = invocation.getArgument(0);
            String inventoryItemId = id("created-item");
            Map<String, Object> fields = new LinkedHashMap<>(serviceInput);
            fields.remove("isReturned");
            fields.put("inventoryItemId", inventoryItemId);
            fields.put("quantityOnHandTotal", ZERO);
            fields.put("availableToPromiseTotal", ZERO);
            fields.putIfAbsent("accountingQuantityTotal", ZERO);
            save("InventoryItem", fields);
            return Map.of("inventoryItemId", inventoryItemId);
        }).when(stockMoveServices).createInventoryItem(anyMap());
    }

    private Map<String, Object> createInventoryItemDetail(Map<String, Object> input) {
        String inventoryItemId = input.get("inventoryItemId").toString();
        Object inventoryItem = entities.findByPrimaryKey(
                entities.entityClass("InventoryItem"), Map.of("inventoryItemId", inventoryItemId));
        org.junit.jupiter.api.Assertions.assertNotNull(inventoryItem);
        String sequenceId = String.format("%05d", detailSequence.incrementAndGet());
        Map<String, Object> detail = map(
                "inventoryItemId", inventoryItemId,
                "inventoryItemDetailSeqId", sequenceId,
                "effectiveDate", Timestamp.from(Instant.now()),
                "quantityOnHandDiff", input.getOrDefault("quantityOnHandDiff", ZERO),
                "availableToPromiseDiff", input.getOrDefault("availableToPromiseDiff", ZERO));
        save("InventoryItemDetail", detail);
        entities.set(inventoryItem, "quantityOnHandTotal",
                bd(entities.get(inventoryItem, "quantityOnHandTotal"))
                        .add(bd(input.getOrDefault("quantityOnHandDiff", ZERO))));
        entities.set(inventoryItem, "availableToPromiseTotal",
                bd(entities.get(inventoryItem, "availableToPromiseTotal"))
                        .add(bd(input.getOrDefault("availableToPromiseDiff", ZERO))));
        return Map.of("inventoryItemDetailSeqId", sequenceId);
    }

    private ResultActions postService(String serviceName, Map<String, Object> input, String... authorities)
            throws Exception {
        SimpleGrantedAuthority[] grantedAuthorities = java.util.Arrays.stream(authorities)
                .map(SimpleGrantedAuthority::new)
                .collect(java.util.stream.Collectors.collectingAndThen(
                        java.util.stream.Collectors.toCollection(java.util.ArrayList::new),
                        granted -> {
                            granted.add(0, new SimpleGrantedAuthority("OFBTOOLS_VIEW"));
                            return granted.toArray(SimpleGrantedAuthority[]::new);
                        }));
        return mockMvc.perform(post("/api/product/services/" + serviceName)
                .with(user("stockmove").authorities(grantedAuthorities))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)));
    }

    private Seed seed(
            String inventoryItemTypeId,
            String orderItemStatusId,
            BigDecimal bulkQoh,
            BigDecimal bulkAtp,
            BigDecimal pickQoh,
            BigDecimal pickAtp,
            BigDecimal minimumStock,
            BigDecimal moveQuantity,
            BigDecimal reservedQuantity) {
        String productId = id("product");
        String facilityId = id("facility");
        String bulkLocationId = id("bulk");
        String pickLocationId = id("pick");
        save("Product", map("productId", productId, "productName", "Stock move test"));
        save("Facility", map("facilityId", facilityId, "facilityName", "Stock move test"));
        save("FacilityLocation", map(
                "facilityId", facilityId,
                "locationSeqId", bulkLocationId,
                "locationTypeEnumId", "FLT_BULK"));
        save("FacilityLocation", map(
                "facilityId", facilityId,
                "locationSeqId", pickLocationId,
                "locationTypeEnumId", "FLT_PICKLOC"));
        if (minimumStock != null || moveQuantity != null) {
            save("ProductFacilityLocation", map(
                    "productId", productId,
                    "facilityId", facilityId,
                    "locationSeqId", pickLocationId,
                    "minimumStock", minimumStock,
                    "moveQuantity", moveQuantity));
        }
        Object bulkItem = save("InventoryItem", map(
                "inventoryItemId", id("bulk-item"),
                "productId", productId,
                "facilityId", facilityId,
                "locationSeqId", bulkLocationId,
                "inventoryItemTypeId", inventoryItemTypeId,
                "statusId", "INV_AVAILABLE",
                "quantityOnHandTotal", bulkQoh,
                "availableToPromiseTotal", bulkAtp));
        Object pickItem = null;
        if (pickQoh != null || pickAtp != null) {
            pickItem = save("InventoryItem", map(
                    "inventoryItemId", id("pick-item"),
                    "productId", productId,
                    "facilityId", facilityId,
                    "locationSeqId", pickLocationId,
                    "inventoryItemTypeId", "NON_SERIAL_INV_ITEM",
                    "statusId", "INV_AVAILABLE",
                    "quantityOnHandTotal", pickQoh,
                    "availableToPromiseTotal", pickAtp));
        }
        String orderId = null;
        if (reservedQuantity != null) {
            orderId = id("order");
            save("OrderItem", map(
                    "orderId", orderId,
                    "orderItemSeqId", "00001",
                    "productId", productId,
                    "statusId", orderItemStatusId));
            save("OrderItemShipGrpInvRes", map(
                    "orderId", orderId,
                    "shipGroupSeqId", "00001",
                    "orderItemSeqId", "00001",
                    "inventoryItemId", entities.get(bulkItem, "inventoryItemId"),
                    "quantity", reservedQuantity,
                    "quantityNotAvailable", ZERO,
                    "reservedDatetime", Timestamp.from(Instant.now())));
        }
        return new Seed(productId, facilityId, bulkLocationId, pickLocationId,
                bulkItem, pickItem, orderId);
    }

    private Object save(String entityName, Map<String, Object> fields) {
        Object entity = entities.newEntity(entities.entityClass(entityName));
        fields.forEach((field, value) -> entities.set(entity, field, value));
        entityManager.persist(entity);
        return entity;
    }

    private static Map<String, Object> moveInput(Seed seed, BigDecimal quantityMoved) {
        return map(
                "productId", seed.productId(),
                "facilityId", seed.facilityId(),
                "locationSeqId", seed.bulkLocationId(),
                "targetLocationSeqId", seed.pickLocationId(),
                "quantityMoved", quantityMoved);
    }

    private static Map<String, Object> map(Object... nameValues) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int i = 0; i < nameValues.length; i += 2) {
            result.put((String) nameValues[i], nameValues[i + 1]);
        }
        return result;
    }

    private static BigDecimal bd(String value) {
        return new BigDecimal(value).setScale(2);
    }

    private static BigDecimal bd(Object value) {
        return value == null ? ZERO : value instanceof BigDecimal decimal
                ? decimal
                : new BigDecimal(value.toString());
    }

    private static void assertDecimalValue(String expected, Object actual) {
        org.junit.jupiter.api.Assertions.assertEquals(0, bd(expected).compareTo(bd(actual)));
    }

    private static String id(String prefix) {
        return prefix.substring(0, Math.min(prefix.length(), 3))
                + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    private record Seed(
            String productId,
            String facilityId,
            String bulkLocationId,
            String pickLocationId,
            Object bulkInventoryItem,
            Object pickInventoryItem,
            String orderId) {
        List<Object> inventoryItems() {
            return pickInventoryItem == null ? List.of(bulkInventoryItem)
                    : List.of(bulkInventoryItem, pickInventoryItem);
        }
    }
}
