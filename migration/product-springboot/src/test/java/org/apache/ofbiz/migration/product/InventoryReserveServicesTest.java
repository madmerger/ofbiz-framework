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
package org.apache.ofbiz.migration.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.product.simple.InventoryServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(
        classes = ProductMigrationApplication.class,
        properties = "migration.security.require-https=false")
@AutoConfigureMockMvc
class InventoryReserveServicesTest {
    private static final Timestamp ORDER_DATE = Timestamp.valueOf("2024-01-01 12:30:00");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @MockitoBean
    private InventoryServices inventoryServices;

    private static final AtomicInteger IDENTIFIERS = new AtomicInteger();
    private static final AtomicInteger DETAIL_SEQUENCE = new AtomicInteger();
    private TransactionTemplate transactions;

    @BeforeEach
    void emulateInventoryServiceCalls() {
        transactions = new TransactionTemplate(transactionManager);
        doAnswer(invocation -> {
            String serviceName = invocation.getArgument(0);
            String methodName = invocation.getArgument(1);
            @SuppressWarnings("unchecked")
            Map<String, Object> input = invocation.getArgument(2);
            if ("createInventoryItemDetail".equals(methodName)) {
                createInventoryItemDetail(input);
            } else if ("updateInventoryItem".equals(methodName)) {
                updateInventoryItem(input);
            } else {
                throw new AssertionError("Unexpected InventoryServices call: " + serviceName);
            }
            return Map.of();
        }).when(inventoryServices).execute(anyString(), anyString(), anyMap());
    }

    @Test
    void ac01_reservesNonSerializedStockThroughFacilityAliasAndCalculatesPromiseDate() throws Exception {
        Scenario scenario = scenario(true, true);
        createInventoryItem(scenario, "NON_SERIAL_INV_ITEM", new BigDecimal("3"));

        reserve("reserveProductInventoryByFacility", scenario, new BigDecimal("2"), "Y")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantityNotReserved").value(0.0));

        transactions.executeWithoutResult(status -> {
            Object item = entity("InventoryItem", "inventoryItemId", scenario.inventoryItemId());
            Object reservation = entity("OrderItemShipGrpInvRes",
                    "orderId", scenario.orderId(),
                    "shipGroupSeqId", scenario.shipGroupSeqId(),
                    "orderItemSeqId", scenario.orderItemSeqId(),
                    "inventoryItemId", scenario.inventoryItemId());
            assertDecimalEquals("1", entities.get(item, "availableToPromiseTotal"));
            assertDecimalEquals("2", entities.get(reservation, "quantity"));
            assertEquals(promisedDate(ORDER_DATE, 7), entities.get(reservation, "promisedDatetime"));
            List<?> details = entityManager.createQuery(
                            "select d from InventoryItemDetail d where d.inventoryItemId = :inventoryItemId")
                    .setParameter("inventoryItemId", scenario.inventoryItemId())
                    .getResultList();
            assertEquals(1, details.size());
            assertDecimalEquals("-2", entities.get(details.get(0), "availableToPromiseDiff"));
        });
        verify(inventoryServices, times(1)).execute(
                eq("createInventoryItemDetail"),
                eq("createInventoryItemDetail"),
                argThat(input -> scenario.inventoryItemId().equals(input.get("inventoryItemId"))
                        && decimalEquals("-2", input.get("availableToPromiseDiff"))));
    }

    @Test
    void ac02_reservesSerializedStockByPromisingTheInventoryItem() throws Exception {
        Scenario scenario = scenario(true, true);
        createInventoryItem(scenario, "SERIALIZED_INV_ITEM", BigDecimal.ONE);

        reserve("reserveProductInventory", scenario, BigDecimal.ONE, "Y")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantityNotReserved").value(0.0));

        transactions.executeWithoutResult(status -> {
            Object item = entity("InventoryItem", "inventoryItemId", scenario.inventoryItemId());
            Object reservation = entity("OrderItemShipGrpInvRes",
                    "orderId", scenario.orderId(),
                    "shipGroupSeqId", scenario.shipGroupSeqId(),
                    "orderItemSeqId", scenario.orderItemSeqId(),
                    "inventoryItemId", scenario.inventoryItemId());
            assertEquals("INV_PROMISED", entities.get(item, "statusId"));
            assertDecimalEquals("1", entities.get(reservation, "quantity"));
            assertEquals(promisedDate(ORDER_DATE, 7), entities.get(reservation, "promisedDatetime"));
        });
        verify(inventoryServices).execute(
                eq("updateInventoryItem"),
                eq("updateInventoryItem"),
                argThat(input -> scenario.inventoryItemId().equals(input.get("inventoryItemId"))
                        && "INV_PROMISED".equals(input.get("statusId"))));
    }

    @Test
    void ac03_returnsZeroForANonphysicalProduct() throws Exception {
        Scenario scenario = scenario(false, false);

        reserve("reserveProductInventoryByFacility", scenario, new BigDecimal("4"), "Y")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantityNotReserved").value(0.0));
    }

    @Test
    void ac04_replacesAnExistingSerializedReservationWithTheRequestedItem() throws Exception {
        Scenario scenario = scenario(true, true);
        createInventoryItem(scenario, "SERIALIZED_INV_ITEM", BigDecimal.ONE);
        transactions.executeWithoutResult(status -> {
            Object item = entity("InventoryItem", "inventoryItemId", scenario.inventoryItemId());
            entities.set(item, "serialNumber", "SERIAL-REQUESTED");
            entities.set(item, "statusId", "INV_PROMISED");
            createReservation(scenario, new BigDecimal("1"));
        });
        Map<String, Object> input = values(
                "inventoryItemId", scenario.inventoryItemId(),
                "productId", scenario.productId(),
                "orderId", scenario.orderId(),
                "orderItemSeqId", scenario.orderItemSeqId(),
                "shipGroupSeqId", scenario.shipGroupSeqId(),
                "quantity", BigDecimal.ONE,
                "requireInventory", "Y",
                "promisedDatetime", Timestamp.valueOf("2024-01-08 12:30:00"),
                "serialNumber", "SERIAL-REQUESTED");

        postService("reserveAnInventoryItem", input, administrator())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inventoryItemId").value(scenario.inventoryItemId()));

        transactions.executeWithoutResult(status -> {
            Object item = entity("InventoryItem", "inventoryItemId", scenario.inventoryItemId());
            assertEquals("INV_PROMISED", entities.get(item, "statusId"));
            assertNull(entity("OrderItemShipGrpInvRes",
                    "orderId", scenario.orderId(),
                    "shipGroupSeqId", scenario.shipGroupSeqId(),
                    "orderItemSeqId", scenario.orderItemSeqId(),
                    "inventoryItemId", scenario.inventoryItemId()));
        });
    }

    @Test
    void ac05_createsAndUpdatesOrderItemReservation() throws Exception {
        Scenario scenario = scenario(true, true);
        createInventoryItem(scenario, "NON_SERIAL_INV_ITEM", BigDecimal.ONE);
        Map<String, Object> reservation = values(
                "orderId", scenario.orderId(),
                "shipGroupSeqId", scenario.shipGroupSeqId(),
                "orderItemSeqId", scenario.orderItemSeqId(),
                "inventoryItemId", scenario.inventoryItemId(),
                "quantity", BigDecimal.ONE,
                "quantityNotAvailable", BigDecimal.ZERO,
                "reserveOrderEnumId", "INVRO_FIFO_REC");

        postService("reserveOrderItemInventory", reservation, administrator()).andExpect(status().isOk());
        reservation.put("quantity", new BigDecimal("2"));
        postService("reserveOrderItemInventory", reservation, administrator()).andExpect(status().isOk());

        transactions.executeWithoutResult(status -> {
            Object stored = entity("OrderItemShipGrpInvRes",
                    "orderId", scenario.orderId(),
                    "shipGroupSeqId", scenario.shipGroupSeqId(),
                    "orderItemSeqId", scenario.orderItemSeqId(),
                    "inventoryItemId", scenario.inventoryItemId());
            assertNotNull(stored);
            assertDecimalEquals("3", entities.get(stored, "quantity"));
        });
    }

    @Test
    void ac06_cancelsAllReservationsForAnOrder() throws Exception {
        Scenario scenario = scenario(true, true);
        createInventoryItem(scenario, "SERIALIZED_INV_ITEM", BigDecimal.ONE);
        transactions.executeWithoutResult(status -> {
            Object item = entity("InventoryItem", "inventoryItemId", scenario.inventoryItemId());
            entities.set(item, "statusId", "INV_PROMISED");
            createReservation(scenario, BigDecimal.ONE);
        });

        postService("cancelOrderInventoryReservation",
                values("orderId", scenario.orderId()), administrator())
                .andExpect(status().isOk());

        transactions.executeWithoutResult(status -> {
            Object item = entity("InventoryItem", "inventoryItemId", scenario.inventoryItemId());
            assertEquals("INV_AVAILABLE", entities.get(item, "statusId"));
            assertNull(entity("OrderItemShipGrpInvRes",
                    "orderId", scenario.orderId(),
                    "shipGroupSeqId", scenario.shipGroupSeqId(),
                    "orderItemSeqId", scenario.orderItemSeqId(),
                    "inventoryItemId", scenario.inventoryItemId()));
        });
    }

    @Test
    void ac07_cancelsAPartialNonSerializedReservationAndRestoresAvailableToPromise() throws Exception {
        Scenario scenario = scenario(true, true);
        createInventoryItem(scenario, "NON_SERIAL_INV_ITEM", BigDecimal.ZERO);
        transactions.executeWithoutResult(status -> createReservation(scenario, new BigDecimal("4")));

        postService("cancelOrderItemInvResQty",
                values(
                        "orderId", scenario.orderId(),
                        "orderItemSeqId", scenario.orderItemSeqId(),
                        "shipGroupSeqId", scenario.shipGroupSeqId(),
                        "cancelQuantity", new BigDecimal("2")),
                administrator()).andExpect(status().isOk());

        transactions.executeWithoutResult(status -> {
            Object item = entity("InventoryItem", "inventoryItemId", scenario.inventoryItemId());
            Object reservation = entity("OrderItemShipGrpInvRes",
                    "orderId", scenario.orderId(),
                    "shipGroupSeqId", scenario.shipGroupSeqId(),
                    "orderItemSeqId", scenario.orderItemSeqId(),
                    "inventoryItemId", scenario.inventoryItemId());
            assertDecimalEquals("2", entities.get(item, "availableToPromiseTotal"));
            assertDecimalEquals("2", entities.get(reservation, "quantity"));
        });
    }

    @Test
    void ac08_cancelsASpecificReservationAndReturnsNotFoundForAMissingOne() throws Exception {
        Scenario scenario = scenario(true, true);
        createInventoryItem(scenario, "SERIALIZED_INV_ITEM", BigDecimal.ONE);
        transactions.executeWithoutResult(status -> {
            Object item = entity("InventoryItem", "inventoryItemId", scenario.inventoryItemId());
            entities.set(item, "statusId", "INV_PROMISED");
            createReservation(scenario, BigDecimal.ONE);
        });

        postService("cancelOrderItemShipGrpInvRes",
                reservationKeys(scenario), administrator()).andExpect(status().isOk());
        postService("cancelOrderItemShipGrpInvRes",
                values(
                        "orderId", scenario.orderId(),
                        "shipGroupSeqId", scenario.shipGroupSeqId(),
                        "orderItemSeqId", scenario.orderItemSeqId(),
                        "inventoryItemId", "MISSING-ITEM"),
                administrator()).andExpect(status().isNotFound());
    }

    @Test
    void ac09_rejectsMissingRequiredInputs() throws Exception {
        postService("reserveProductInventoryByFacility", Map.of(), administrator())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(
                        org.hamcrest.Matchers.containsString("facilityId")));
    }

    @Test
    void ac10_deniesAReadOnlyUser() throws Exception {
        postService("reserveProductInventoryByFacility",
                values(
                        "productId", "UNUSED",
                        "facilityId", "UNUSED",
                        "quantity", BigDecimal.ONE,
                        "requireInventory", "Y",
                        "reserveOrderEnumId", "INVRO_FIFO_REC"),
                user("readonly").authorities(
                        new SimpleGrantedAuthority("OFBTOOLS_VIEW"),
                        new SimpleGrantedAuthority("PRODUCT_VIEW")))
                .andExpect(status().isForbidden());
    }

    @Test
    void ac11_rollsBackReservationsWhenRequiredInventoryIsInsufficient() throws Exception {
        Scenario scenario = scenario(true, true);
        createInventoryItem(scenario, "NON_SERIAL_INV_ITEM", BigDecimal.ONE);

        reserve("reserveProductInventoryByFacility", scenario, new BigDecimal("2"), "Y")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage").value(
                        org.hamcrest.Matchers.containsString("not enough quantity left in stock")));

        transactions.executeWithoutResult(status -> {
            assertNull(entity("OrderItemShipGrpInvRes",
                    "orderId", scenario.orderId(),
                    "shipGroupSeqId", scenario.shipGroupSeqId(),
                    "orderItemSeqId", scenario.orderItemSeqId(),
                    "inventoryItemId", scenario.inventoryItemId()));
            Object item = entity("InventoryItem", "inventoryItemId", scenario.inventoryItemId());
            assertDecimalEquals("1", entities.get(item, "availableToPromiseTotal"));
            List<?> details = entityManager.createQuery(
                            "select d from InventoryItemDetail d where d.inventoryItemId = :inventoryItemId")
                    .setParameter("inventoryItemId", scenario.inventoryItemId())
                    .getResultList();
            assertEquals(0, details.size());
        });
    }

    @Test
    void ac12_returnsNotImplementedWhenBackorderingNeedsANewInventoryItem() throws Exception {
        Scenario scenario = scenario(true, false);

        reserve("reserveProductInventoryByFacility", scenario, BigDecimal.ONE, "N")
                .andExpect(status().isNotImplemented())
                .andExpect(jsonPath("$.errorMessage").value(
                        org.hamcrest.Matchers.containsString("createInventoryItem is not ported")));
    }

    @Test
    void ac13_persistsProductionRunReservationUsingTheThreeColumnPrimaryKey() throws Exception {
        Scenario scenario = scenario(true, false);
        String workEffortId = "RSW" + IDENTIFIERS.incrementAndGet();
        createInventoryItem(scenario, "NON_SERIAL_INV_ITEM", new BigDecimal("3"));
        transactions.executeWithoutResult(status ->
                create("WorkEffort", "workEffortId", workEffortId));

        postService("reserveProductInventoryByFacility",
                values(
                        "productId", scenario.productId(),
                        "facilityId", scenario.facilityId(),
                        "workEffortId", workEffortId,
                        "quantity", new BigDecimal("2"),
                        "requireInventory", "Y",
                        "reserveOrderEnumId", "INVRO_FIFO_REC"),
                administrator()).andExpect(status().isOk());

        transactions.executeWithoutResult(status -> {
            Object reservation = entity("WorkEffortInvRes",
                    "workEffortId", workEffortId,
                    "inventoryItemId", scenario.inventoryItemId(),
                    "productId", scenario.productId());
            assertNotNull(reservation);
            assertDecimalEquals("2", entities.get(reservation, "quantity"));
        });
    }

    private org.springframework.test.web.servlet.ResultActions reserve(
            String serviceName, Scenario scenario, BigDecimal quantity, String requireInventory) throws Exception {
        Map<String, Object> input = values(
                "productId", scenario.productId(),
                "facilityId", scenario.facilityId(),
                "orderId", scenario.orderId(),
                "orderItemSeqId", scenario.orderItemSeqId(),
                "shipGroupSeqId", scenario.shipGroupSeqId(),
                "quantity", quantity,
                "requireInventory", requireInventory,
                "reserveOrderEnumId", "INVRO_FIFO_REC");
        return postService(serviceName, input, administrator());
    }

    private org.springframework.test.web.servlet.ResultActions postService(
            String serviceName, Map<String, Object> input, RequestPostProcessor user) throws Exception {
        return mockMvc.perform(post("/api/product/services/" + serviceName)
                .with(user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(input)));
    }

    private Scenario scenario(boolean physical, boolean withOrder) {
        return transactions.execute(status -> {
            String suffix = Integer.toString(IDENTIFIERS.incrementAndGet());
            String productId = "RSP" + suffix;
            String facilityId = "RSF" + suffix;
            String itemId = "RSI" + suffix;
            String orderId = "RSO" + suffix;
            String orderItemSeqId = "00001";
            String shipGroupSeqId = "00001";

            create("ProductType", "productTypeId", "RST" + suffix,
                    "isPhysical", physical ? "Y" : "N");
            create("Product", "productId", productId, "productTypeId", "RST" + suffix);
            create("Facility", "facilityId", facilityId, "defaultDaysToShip", 11L);
            create("ProductFacility", "productId", productId, "facilityId", facilityId, "daysToShip", 7L);
            if (withOrder) {
                create("OrderHeader", "orderId", orderId, "orderDate", ORDER_DATE);
                create("OrderItem", "orderId", orderId, "orderItemSeqId", orderItemSeqId, "productId", productId);
            }
            return new Scenario(productId, facilityId, itemId, orderId, orderItemSeqId, shipGroupSeqId);
        });
    }

    private void createInventoryItem(Scenario scenario, String itemType, BigDecimal available) {
        transactions.executeWithoutResult(status -> create(
                "InventoryItem",
                "inventoryItemId", scenario.inventoryItemId(),
                "inventoryItemTypeId", itemType,
                "productId", scenario.productId(),
                "facilityId", scenario.facilityId(),
                "statusId", "INV_AVAILABLE",
                "availableToPromiseTotal", available,
                "quantityOnHandTotal", available));
    }

    private void createReservation(Scenario scenario, BigDecimal quantity) {
        create("OrderItemShipGrpInvRes",
                "orderId", scenario.orderId(),
                "shipGroupSeqId", scenario.shipGroupSeqId(),
                "orderItemSeqId", scenario.orderItemSeqId(),
                "inventoryItemId", scenario.inventoryItemId(),
                "reserveOrderEnumId", "INVRO_FIFO_REC",
                "quantity", quantity,
                "quantityNotAvailable", BigDecimal.ZERO,
                "reservedDatetime", Timestamp.valueOf("2024-01-01 12:30:00"),
                "createdDatetime", Timestamp.valueOf("2024-01-01 12:30:00"));
    }

    private Object create(String entityName, Object... fields) {
        Object entity = entities.newEntity(entities.entityClass(entityName));
        for (int index = 0; index < fields.length; index += 2) {
            entities.set(entity, (String) fields[index], fields[index + 1]);
        }
        entityManager.persist(entity);
        return entity;
    }

    private Object entity(String entityName, Object... keyValues) {
        Map<String, Object> keys = values(keyValues);
        return entities.findByPrimaryKey(entities.entityClass(entityName), keys);
    }

    private Map<String, Object> reservationKeys(Scenario scenario) {
        return values(
                "orderId", scenario.orderId(),
                "shipGroupSeqId", scenario.shipGroupSeqId(),
                "orderItemSeqId", scenario.orderItemSeqId(),
                "inventoryItemId", scenario.inventoryItemId());
    }

    private void createInventoryItemDetail(Map<String, Object> input) {
        Object item = entity("InventoryItem", "inventoryItemId", input.get("inventoryItemId"));
        if (item == null) {
            throw new AssertionError("InventoryItem missing for detail: " + input.get("inventoryItemId"));
        }
        Object detail = entities.newEntity(entities.entityClass("InventoryItemDetail"));
        entities.set(detail, "inventoryItemId", input.get("inventoryItemId"));
        entities.set(detail, "inventoryItemDetailSeqId",
                String.format("%05d", DETAIL_SEQUENCE.incrementAndGet()));
        entities.set(detail, "effectiveDate", new Timestamp(System.currentTimeMillis()));
        for (String field : List.of(
                "quantityOnHandDiff", "availableToPromiseDiff", "accountingQuantityDiff",
                "orderId", "orderItemSeqId", "shipGroupSeqId", "workEffortId")) {
            if (input.containsKey(field)) {
                entities.set(detail, field, input.get(field));
            }
        }
        applyDifference(item, "quantityOnHandTotal", input.get("quantityOnHandDiff"));
        applyDifference(item, "availableToPromiseTotal", input.get("availableToPromiseDiff"));
        entityManager.persist(detail);
    }

    private void updateInventoryItem(Map<String, Object> input) {
        Object item = entity("InventoryItem", "inventoryItemId", input.get("inventoryItemId"));
        if (item == null) {
            throw new AssertionError("InventoryItem missing for update: " + input.get("inventoryItemId"));
        }
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            if (!"inventoryItemId".equals(entry.getKey())) {
                entities.set(item, entry.getKey(), entry.getValue());
            }
        }
    }

    private void applyDifference(Object entity, String field, Object difference) {
        if (difference != null) {
            Object current = entities.get(entity, field);
            BigDecimal initial = current == null ? BigDecimal.ZERO : new BigDecimal(current.toString());
            entities.set(entity, field, initial.add(new BigDecimal(difference.toString())));
        }
    }

    private static Timestamp promisedDate(Timestamp date, int days) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.add(java.util.Calendar.DAY_OF_YEAR, days);
        return new Timestamp(calendar.getTimeInMillis());
    }

    private static void assertDecimalEquals(String expected, Object actual) {
        assertNotNull(actual);
        assertEquals(0, new BigDecimal(expected).compareTo(new BigDecimal(actual.toString())));
    }

    private static boolean decimalEquals(String expected, Object actual) {
        return actual != null && new BigDecimal(expected).compareTo(new BigDecimal(actual.toString())) == 0;
    }

    private static RequestPostProcessor administrator() {
        return user("inventory").authorities(
                new SimpleGrantedAuthority("OFBTOOLS_VIEW"),
                new SimpleGrantedAuthority("PRODUCT_ADMIN"),
                new SimpleGrantedAuthority("PRODUCT_UPDATE"),
                new SimpleGrantedAuthority("FACILITY_ADMIN"),
                new SimpleGrantedAuthority("FACILITY_UPDATE"),
                new SimpleGrantedAuthority("ORDERMGR_ADMIN"),
                new SimpleGrantedAuthority("ORDERMGR_UPDATE"),
                new SimpleGrantedAuthority("MANUFACTURING_ADMIN"),
                new SimpleGrantedAuthority("MANUFACTURING_UPDATE"));
    }

    private static Map<String, Object> values(Object... fields) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int index = 0; index < fields.length; index += 2) {
            result.put((String) fields[index], fields[index + 1]);
        }
        return result;
    }

    private record Scenario(
            String productId,
            String facilityId,
            String inventoryItemId,
            String orderId,
            String orderItemSeqId,
            String shipGroupSeqId) {
    }
}
