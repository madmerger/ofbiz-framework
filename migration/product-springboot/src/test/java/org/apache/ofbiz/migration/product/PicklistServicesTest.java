/*
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
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.ofbiz.migration.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.product.simple.InventoryReserveServices;
import org.apache.ofbiz.migration.product.simple.InventoryServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(
        classes = ProductMigrationApplication.class,
        properties = "migration.security.require-https=false")
@AutoConfigureMockMvc
class PicklistServicesTest {
    private static final ObjectMapper JSON = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private TransactionTemplate transactions;

    @MockitoBean
    private InventoryServices inventoryServices;

    @MockitoBean
    private InventoryReserveServices inventoryReserveServices;

    @Test
    void findOrdersToPickMoveReturnsReadyAndStockMoveRows() throws Exception {
        Fixture ready = seedOrder("READY");
        Fixture bulk = seedOrder("BULK", ready.facilityId());
        Map<String, Object> readyOrder = orderHeader(ready);
        Map<String, Object> bulkOrder = orderHeader(bulk);
        mockMvc.perform(post("/api/product/services/findOrdersToPickMove")
                        .with(user("picklist").authorities(adminAuthorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(values(
                                "facilityId", ready.facilityId(),
                                "groupByWarehouseArea", "Y",
                                "orderHeaderList", java.util.List.of(readyOrder, bulkOrder)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pickMoveInfoList.length()").value(1))
                .andExpect(jsonPath("$.pickMoveInfoList[0].orderReadyToPickInfoList.length()").value(1))
                .andExpect(jsonPath("$.pickMoveInfoList[0].orderNeedsStockMoveInfoList.length()").value(1))
                .andExpect(jsonPath("$.rushOrderInfo.orderReadyToPickInfoList.length()").value(0));
    }

    @Test
    void createPicklistFromOrdersPersistsPicklistBinAndItem() throws Exception {
        Fixture fixture = seedOrder("CREATE");
        mockMvc.perform(post("/api/product/services/createPicklistFromOrders")
                        .with(user("picklist").authorities(adminAuthorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(values(
                                "facilityId", fixture.facilityId(),
                                "orderHeaderList", java.util.List.of(orderHeader(fixture))))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.picklistId").isNotEmpty());
        assertEquals(1, count("Picklist", "facilityId", fixture.facilityId()));
        assertEquals(1, count("PicklistBin", "primaryOrderId", fixture.orderId()));
        assertEquals(1, count("PicklistItem", "orderId", fixture.orderId()));
    }

    @Test
    void printPickSheetsSetsPrintedDateAndHonorsTheCounterLimit() throws Exception {
        Fixture fixture = seedOrder("PRINT");
        mockMvc.perform(post("/api/product/services/printPickSheets")
                        .with(user("picklist").authorities(adminAuthorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(values(
                                "facilityId", fixture.facilityId(),
                                "orderId", fixture.orderId(),
                                "maxNumberOfOrdersToPrint", 1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pickMoveInfoList.length()").value(1));
        Map<String, Object> order = entities.toMap(entities.findByPrimaryKey(
                entities.entityClass("OrderHeader"), values("orderId", fixture.orderId())));
        org.junit.jupiter.api.Assertions.assertNotNull(order.get("pickSheetPrintedDate"));
    }

    @Test
    void editPicklistItemUsesSiblingInventoryServices() throws Exception {
        Fixture fixture = seedOrder("EDIT");
        String picklistId = "PL_" + fixture.orderId();
        String binId = "BIN_" + fixture.orderId();
        persist("Picklist", values("picklistId", picklistId, "facilityId", fixture.facilityId(),
                "statusId", "PICKLIST_INPUT", "picklistDate", Timestamp.from(Instant.now())));
        persist("PicklistBin", values("picklistBinId", binId, "picklistId", picklistId,
                "binLocationNumber", 1L, "primaryOrderId", fixture.orderId(),
                "primaryShipGroupSeqId", "00001"));
        persist("PicklistItem", values("picklistBinId", binId, "orderId", fixture.orderId(),
                "shipGroupSeqId", "00001", "orderItemSeqId", "00001",
                "inventoryItemId", fixture.inventoryItemId(), "itemStatusId", "PICKITEM_PENDING",
                "quantity", new BigDecimal("2")));
        persist("InventoryItem", values("inventoryItemId", "LOT_" + fixture.orderId(),
                "productId", fixture.productId(), "facilityId", fixture.facilityId(),
                "lotId", "LOT_" + fixture.orderId(), "quantityOnHandTotal", new BigDecimal("5"),
                "inventoryItemTypeId", "NON_SERIAL_INV_ITEM"));
        when(inventoryServices.execute(eq("getInventoryAvailableByFacility"),
                eq("getProductInventoryAvailable"), anyMap()))
                .thenReturn(values("availableToPromiseTotal", new BigDecimal("5"),
                        "quantityOnHandTotal", new BigDecimal("5")));
        doAnswer(invocation -> {
            String serviceName = invocation.getArgument(0);
            Map<String, Object> input = invocation.getArgument(2);
            if ("cancelOrderItemShipGrpInvRes".equals(serviceName)) {
                Object reservation = entities.findByPrimaryKey(
                        entities.entityClass("OrderItemShipGrpInvRes"), input);
                if (reservation != null) {
                    entityManager.remove(reservation);
                }
            } else {
                Object lotRow = entities.findByEquality(
                        entities.entityClass("InventoryItem"),
                        values("lotId", input.get("lotId"))).get(0);
                Map<String, Object> lot = entities.toMap(lotRow);
                persist("OrderItemShipGrpInvRes", values(
                        "orderId", input.get("orderId"),
                        "shipGroupSeqId", input.get("shipGroupSeqId"),
                        "orderItemSeqId", input.get("orderItemSeqId"),
                        "inventoryItemId", lot.get("inventoryItemId"),
                        "quantity", input.get("quantity"),
                        "quantityNotAvailable", BigDecimal.ZERO,
                        "reserveOrderEnumId", input.get("reserveOrderEnumId")));
            }
            return Map.of();
        }).when(inventoryReserveServices).execute(anyString(), anyString(), anyMap());

        mockMvc.perform(post("/api/product/services/editPicklistItem")
                        .with(user("picklist").authorities(adminAuthorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(values(
                                "picklistBinId", binId,
                                "orderId", fixture.orderId(),
                                "shipGroupSeqId", "00001",
                                "orderItemSeqId", "00001",
                                "inventoryItemId", fixture.inventoryItemId(),
                                "lotId", "LOT_" + fixture.orderId(),
                                "quantity", new BigDecimal("2"),
                                "productId", fixture.productId(),
                                "facilityId", fixture.facilityId()))))
                .andExpect(status().isOk());
        verify(inventoryServices).execute(eq("getInventoryAvailableByFacility"),
                eq("getProductInventoryAvailable"), anyMap());
        verify(inventoryReserveServices).execute(eq("cancelOrderItemShipGrpInvRes"),
                eq("cancelOrderItemShipGrpInvRes"), anyMap());
        verify(inventoryReserveServices).execute(eq("reserveProductInventoryByFacility"),
                eq("reserveProductInventory"), anyMap());
        org.junit.jupiter.api.Assertions.assertEquals(
                "LOT_" + fixture.orderId(), first("PicklistItem", "orderId", fixture.orderId())
                        .get("inventoryItemId"));
    }

    @Test
    void setPicklistItemToCompleteUpdatesStatus() throws Exception {
        Fixture fixture = seedOrder("COMPLETE");
        String binId = "BIN_" + fixture.orderId();
        persist("Picklist", values("picklistId", "PL_" + fixture.orderId(),
                "facilityId", fixture.facilityId(), "statusId", "PICKLIST_INPUT"));
        persist("PicklistBin", values("picklistBinId", binId,
                "picklistId", "PL_" + fixture.orderId(), "binLocationNumber", 1L));
        persist("PicklistItem", values("picklistBinId", binId, "orderId", fixture.orderId(),
                "shipGroupSeqId", "00001", "orderItemSeqId", "00001",
                "inventoryItemId", fixture.inventoryItemId(), "itemStatusId", "PICKITEM_PENDING",
                "quantity", new BigDecimal("2")));
        mockMvc.perform(post("/api/product/services/setPicklistItemToComplete")
                        .with(user("picklist").authorities(adminAuthorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(values(
                                "picklistBinId", binId,
                                "orderId", fixture.orderId(),
                                "shipGroupSeqId", "00001",
                                "orderItemSeqId", "00001",
                                "inventoryItemId", fixture.inventoryItemId(),
                                "itemStatusId", "PICKITEM_PENDING",
                                "quantity", new BigDecimal("2")))))
                .andExpect(status().isOk());
        Map<String, Object> item = first("PicklistItem", "picklistBinId", binId);
        org.junit.jupiter.api.Assertions.assertEquals("PICKITEM_COMPLETED", item.get("itemStatusId"));
    }

    @Test
    void getPicklistDisplayInfoReturnsPagingFields() throws Exception {
        Fixture fixture = seedOrder("DISPLAY");
        seedPicklist(fixture, "PICKLIST_INPUT");
        mockMvc.perform(post("/api/product/services/getPicklistDisplayInfo")
                        .with(user("picklist").authorities(adminAuthorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(values(
                                "facilityId", fixture.facilityId(), "viewIndex", 0, "viewSize", 10))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.picklistCount").value(1))
                .andExpect(jsonPath("$.lowIndex").value(1))
                .andExpect(jsonPath("$.highIndex").value(1));
    }

    @Test
    void getPickAndPackReportInfoReturnsBothCollections() throws Exception {
        Fixture fixture = seedOrder("REPORT");
        String picklistId = seedPicklist(fixture, "PICKLIST_INPUT");
        mockMvc.perform(post("/api/product/services/getPickAndPackReportInfo")
                        .with(user("picklist").authorities(adminAuthorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(values("picklistId", picklistId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.picklistInfo.picklist.picklistId").value(picklistId))
                .andExpect(jsonPath("$.facilityLocationInfoList").isArray())
                .andExpect(jsonPath("$.noLocationProductInfoList").isArray());
    }

    @Test
    void checkStatusesAndCancelItemsUpdateTheirRows() throws Exception {
        Fixture fixture = seedOrder("STATUS");
        String picklistId = seedPicklist(fixture, "PICKLIST_INPUT");
        String binId = "BIN_" + fixture.orderId();
        persist("PicklistBin", values("picklistBinId", binId, "picklistId", picklistId,
                "binLocationNumber", 1L, "primaryOrderId", fixture.orderId(),
                "primaryShipGroupSeqId", "00001"));
        persist("PicklistItem", values("picklistBinId", binId, "orderId", fixture.orderId(),
                "shipGroupSeqId", "00001", "orderItemSeqId", "00001",
                "inventoryItemId", fixture.inventoryItemId(), "itemStatusId", "PICKITEM_CANCELLED",
                "quantity", new BigDecimal("2")));
        mockMvc.perform(post("/api/product/services/cancelPicklistAndItems")
                        .with(user("picklist").authorities(adminAuthorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(values("picklistId", picklistId))))
                .andExpect(status().isOk());
        org.junit.jupiter.api.Assertions.assertEquals("PICKITEM_CANCELLED",
                first("PicklistItem", "picklistBinId", binId).get("itemStatusId"));
        mockMvc.perform(post("/api/product/services/checkPicklistBinItemStatuses")
                        .with(user("picklist").authorities(adminAuthorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(values("picklistBinId", binId))))
                .andExpect(status().isOk());
        Map<String, Object> picklist = first("Picklist", "picklistId", picklistId);
        org.junit.jupiter.api.Assertions.assertEquals("PICKLIST_CANCELLED", picklist.get("statusId"));
    }

    @Test
    void createAndPrintValidationErrorsReturnBadRequest() throws Exception {
        Fixture fixture = seedOrder("EMPTY");
        mockMvc.perform(post("/api/product/services/createPicklistFromOrders")
                        .with(user("picklist").authorities(adminAuthorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(values(
                                "facilityId", fixture.facilityId(),
                                "orderHeaderList", java.util.List.of(values("orderId", "MISSING_ORDER"))))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "No orders ready to pick, not creating picklist.")));
        mockMvc.perform(post("/api/product/services/printPickSheets")
                        .with(user("picklist").authorities(adminAuthorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(values("facilityId", fixture.facilityId()))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "The number of orders must not be empty.")));
        mockMvc.perform(post("/api/product/services/printPickSheets")
                        .with(user("picklist").authorities(adminAuthorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(values(
                                "facilityId", fixture.facilityId(), "maxNumberOfOrdersToPrint", 0))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "The number of orders must be greater then zero.")));
    }

    @Test
    void editPicklistItemAccumulatesQuantityAndLotErrors() throws Exception {
        Fixture fixture = seedOrder("EDITERR");
        String binId = "BIN_" + fixture.orderId();
        persist("PicklistItem", values("picklistBinId", binId, "orderId", fixture.orderId(),
                "shipGroupSeqId", "00001", "orderItemSeqId", "00001",
                "inventoryItemId", fixture.inventoryItemId(), "itemStatusId", "PICKITEM_PENDING",
                "quantity", new BigDecimal("2")));
        mockMvc.perform(post("/api/product/services/editPicklistItem")
                        .with(user("picklist").authorities(adminAuthorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(values(
                                "picklistBinId", binId, "orderId", fixture.orderId(),
                                "shipGroupSeqId", "00001", "orderItemSeqId", "00001",
                                "inventoryItemId", fixture.inventoryItemId(),
                                "lotId", "MISSING_LOT", "quantity", new BigDecimal("3"),
                                "productId", fixture.productId(), "facilityId", fixture.facilityId()))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "higher than the quantity expected")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "There is no link between the lot")));
        persist("InventoryItem", values("inventoryItemId", "V" + UUID.randomUUID().toString().substring(0, 8),
                "productId", fixture.productId(), "facilityId", fixture.facilityId(),
                "lotId", "VALID_LOT", "quantityOnHandTotal", new BigDecimal("5"),
                "inventoryItemTypeId", "NON_SERIAL_INV_ITEM"));
        when(inventoryServices.execute(eq("getInventoryAvailableByFacility"),
                eq("getProductInventoryAvailable"), anyMap()))
                .thenReturn(values("availableToPromiseTotal", BigDecimal.ZERO,
                        "quantityOnHandTotal", BigDecimal.ZERO));
        mockMvc.perform(post("/api/product/services/editPicklistItem")
                        .with(user("picklist").authorities(adminAuthorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(values(
                                "picklistBinId", binId, "orderId", fixture.orderId(),
                                "shipGroupSeqId", "00001", "orderItemSeqId", "00001",
                                "inventoryItemId", fixture.inventoryItemId(),
                                "lotId", "VALID_LOT", "quantity", new BigDecimal("2"),
                                "productId", fixture.productId(), "facilityId", fixture.facilityId()))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Not enough stock for this lot.")));
    }

    @Test
    void requiredParametersAreRejectedAndReadOnlyUsersCannotCreatePicklists() throws Exception {
        for (String service : java.util.List.of(
                "findOrdersToPickMove", "createPicklistFromOrders", "printPickSheets",
                "editPicklistItem", "setPicklistItemToComplete", "getPicklistDisplayInfo",
                "getPickAndPackReportInfo", "checkPicklistBinItemStatuses", "cancelPicklistAndItems")) {
            mockMvc.perform(post("/api/product/services/" + service)
                            .with(user("picklist").authorities(adminAuthorities()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
        for (String service : java.util.List.of("createPicklistFromOrders", "printPickSheets",
                "editPicklistItem", "setPicklistItemToComplete", "checkPicklistBinItemStatuses",
                "cancelPicklistAndItems")) {
            mockMvc.perform(post("/api/product/services/" + service)
                            .with(user("readonly").authorities(
                                    new SimpleGrantedAuthority("OFBTOOLS_VIEW")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isForbidden());
        }
    }

    private Fixture seedOrder(String label) {
        return seedOrder(label, null);
    }

    private Fixture seedOrder(String label, String facilityId) {
        String suffix = label + UUID.randomUUID().toString().replace("-", "").substring(0, 7);
        Fixture fixture = new Fixture("O" + suffix,
                facilityId == null ? "F" + suffix : facilityId,
                "I" + suffix, "P" + suffix);
        transactions.executeWithoutResult(status -> {
            persist("OrderHeader", values(
                    "orderId", fixture.orderId(), "orderTypeId", "SALES_ORDER",
                    "statusId", "ORDER_APPROVED", "isRushOrder", "N",
                    "orderDate", Timestamp.from(Instant.now())));
            persist("OrderItemShipGroup", values(
                    "orderId", fixture.orderId(), "shipGroupSeqId", "00001",
                    "maySplit", "Y", "shipmentMethodTypeId", "NO_SHIPPING"));
            persist("OrderItem", values(
                    "orderId", fixture.orderId(), "orderItemSeqId", "00001",
                    "statusId", "ITEM_APPROVED", "productId", fixture.productId(),
                    "quantity", new BigDecimal("2")));
            persist("OrderItemShipGroupAssoc", values(
                    "orderId", fixture.orderId(), "orderItemSeqId", "00001",
                    "shipGroupSeqId", "00001", "quantity", new BigDecimal("2")));
            Map<String, Object> inventoryItem = values(
                    "inventoryItemId", fixture.inventoryItemId(), "productId", fixture.productId(),
                    "facilityId", fixture.facilityId(), "quantityOnHandTotal", new BigDecimal("5"),
                    "inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
            if ("BULK".equals(label)) {
                String locationSeqId = "L" + suffix;
                inventoryItem.put("locationSeqId", locationSeqId);
                persist("FacilityLocation", values(
                        "facilityId", fixture.facilityId(),
                        "locationSeqId", locationSeqId,
                        "locationTypeEnumId", "FLT_BULK"));
            }
            persist("InventoryItem", inventoryItem);
            persist("OrderItemShipGrpInvRes", values(
                    "orderId", fixture.orderId(), "shipGroupSeqId", "00001",
                    "orderItemSeqId", "00001", "inventoryItemId", fixture.inventoryItemId(),
                    "quantity", new BigDecimal("2"), "quantityNotAvailable", BigDecimal.ZERO));
            entityManager.flush();
        });
        return fixture;
    }

    private String seedPicklist(Fixture fixture, String statusId) {
        String picklistId = "PL_" + fixture.orderId();
        persist("Picklist", values("picklistId", picklistId, "facilityId", fixture.facilityId(),
                "statusId", statusId, "picklistDate", Timestamp.from(Instant.now())));
        return picklistId;
    }

    private void persist(String entityName, Map<String, ?> fields) {
        transactions.executeWithoutResult(status -> {
            Object entity = entities.newEntity(entities.entityClass(entityName));
            fields.forEach((field, value) -> entities.set(entity, field, value));
            entityManager.persist(entity);
            entityManager.flush();
        });
    }

    private long count(String entityName, String field, Object value) {
        return entities.findByEquality(entities.entityClass(entityName), Map.of(field, value)).size();
    }

    private Map<String, Object> first(String entityName, String field, Object value) {
        return entities.toMap(entities.findByEquality(
                entities.entityClass(entityName), Map.of(field, value)).get(0));
    }

    private Map<String, Object> orderHeader(Fixture fixture) {
        return values("orderId", fixture.orderId());
    }

    private static GrantedAuthority[] adminAuthorities() {
        return java.util.Arrays.stream(new String[] {
            "OFBTOOLS_ADMIN", "FACILITY_ADMIN", "PRODUCT_ADMIN", "FACILITY_CREATE",
            "FACILITY_UPDATE", "FACILITY_DELETE", "PRODUCT_SENSITIVE_VIEW"
        }).map(SimpleGrantedAuthority::new).toArray(GrantedAuthority[]::new);
    }

    private static Map<String, Object> values(Object... pairs) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int index = 0; index < pairs.length; index += 2) {
            result.put((String) pairs[index], pairs[index + 1]);
        }
        return result;
    }

    private record Fixture(String orderId, String facilityId, String inventoryItemId, String productId) {
    }
}
