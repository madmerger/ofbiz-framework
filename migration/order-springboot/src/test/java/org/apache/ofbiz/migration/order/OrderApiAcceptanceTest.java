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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationSecurityProperties;
import org.apache.ofbiz.migration.order.generated.ServiceCatalog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderApiAcceptanceTest {
    private static final ObjectMapper JSON = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private OrderEntitySupport data;

    @Test
    void ac01_generatedCatalogContainsAll220EntityAutoServices() throws Exception {
        List<String> serviceNames = new ArrayList<>();
        Path root = Path.of(System.getProperty("ofbiz.root", "."));
        try (var definitions = Files.newDirectoryStream(root.resolve("applications/order/servicedef"), "services*.xml")) {
            for (Path definition : definitions) {
                var document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(definition.toFile());
                NodeList services = document.getElementsByTagName("service");
                for (int index = 0; index < services.getLength(); index++) {
                    Element service = (Element) services.item(index);
                    if ("entity-auto".equals(service.getAttribute("engine"))) {
                        serviceNames.add(service.getAttribute("name"));
                    }
                }
            }
        }
        assertEquals(220, serviceNames.size());
        assertEquals(220, serviceNames.stream().filter(ServiceCatalog.all()::containsKey).count());
    }

    @Test
    void ac02_everySensitiveEntityExistsInTheModel() {
        for (String name : List.of(
                "OrderPaymentPreference", "PaymentMethod", "CreditCard", "EftAccount", "GiftCard", "CheckAccount",
                "BillingAccount", "FinAccount", "FinAccountTrans", "FinAccountAuth", "Payment",
                "PaymentGatewayResponse", "PayPalPaymentMethod", "UserLogin", "UserLoginPasswordHistory",
                "PayHistory", "PayrollPreference")) {
            entities.entityClass(name);
        }
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_ADMIN"})
    void ac03_storeApproveShipInvoiceAndExposeHistory() throws Exception {
        String orderId = storeOrder();
        postService("changeOrderStatus", Map.of(
                "orderId", orderId, "statusId", "ORDER_APPROVED", "setItemStatus", "Y"))
                .andExpect(status().isOk());
        MvcResult shipped = postService("quickShipEntireOrder", Map.of("orderId", orderId))
                .andExpect(status().isOk())
                .andReturn();
        assertTrue(JSON.readTree(shipped.getResponse().getContentAsString()).get("shipmentIds").isArray());
        postService("createInvoiceForOrder", Map.of("orderId", orderId)).andExpect(status().isOk());
        postService("createInvoiceForOrder", Map.of("orderId", orderId)).andExpect(status().isConflict());
        mockMvc.perform(get("/api/order/entities/OrderHeader").param("orderId", orderId))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/order/entities/OrderStatus").param("orderId", orderId))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/order/entities/OrderItem").param("orderId", orderId))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_ADMIN"})
    void ac03a_priceSelectionIgnoresFutureEffectiveRows() throws Exception {
        data.create("ProductPrice", Map.of(
                "productId", "DEMO_ORDER_PRODUCT_1",
                "productPriceTypeId", "DEFAULT_PRICE",
                "productPricePurposeId", "PURCHASE",
                "currencyUomId", "USD",
                "productStoreGroupId", "_NA_",
                "fromDate", Timestamp.from(Instant.now().plusSeconds(86_400)),
                "price", "999.00"));
        String orderId = storeOrder();
        Object item = data.find("OrderItem", Map.of("orderId", orderId)).get(0);
        assertEquals(0, new BigDecimal(data.get(item, "unitPrice").toString())
                .compareTo(new BigDecimal("25.00")));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_ADMIN"})
    void ac03b_ordersWith101ItemsShipAndBillEveryItem() throws Exception {
        List<Map<String, Object>> orderItems = new ArrayList<>();
        for (int index = 0; index < 101; index++) {
            orderItems.add(Map.of(
                    "productId", "DEMO_ORDER_PRODUCT_1",
                    "quantity", "1",
                    "unitPrice", "1"));
        }
        String orderId = storeOrder(orderItems);
        postService("changeOrderStatus", Map.of(
                "orderId", orderId, "statusId", "ORDER_APPROVED", "setItemStatus", "Y"))
                .andExpect(status().isOk());
        postService("quickShipEntireOrder", Map.of("orderId", orderId)).andExpect(status().isOk());
        List<?> shipments = data.find("Shipment", Map.of("primaryOrderId", orderId));
        assertEquals(1, shipments.size());
        String shipmentId = data.get(shipments.get(0), "shipmentId").toString();
        assertEquals(101, data.find("ShipmentItem", Map.of("shipmentId", shipmentId)).size());
        postService("createInvoiceForOrder", Map.of("orderId", orderId)).andExpect(status().isOk());
        assertEquals(101, data.find("OrderItemBilling", Map.of("orderId", orderId)).size());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_ADMIN"})
    void ac03c_quickShipCreatesOneShipmentPerShipGroup() throws Exception {
        String orderId = storeOrder(List.of(Map.of(
                "productId", "DEMO_ORDER_PRODUCT_1",
                "quantity", "2",
                "unitPrice", "1")));
        Object firstAssociation = data.find("OrderItemShipGroupAssoc", Map.of(
                "orderId", orderId, "orderItemSeqId", "00001")).get(0);
        data.set(firstAssociation, "quantity", new BigDecimal("1"));
        Object firstGroup = data.find("OrderItemShipGroup", Map.of(
                "orderId", orderId, "shipGroupSeqId", "00001")).get(0);
        Map<String, Object> secondGroup = new LinkedHashMap<>(data.toMap(firstGroup));
        secondGroup.put("shipGroupSeqId", "00002");
        data.create("OrderItemShipGroup", secondGroup);
        data.create("OrderItemShipGroupAssoc", Map.of(
                "orderId", orderId,
                "orderItemSeqId", "00001",
                "shipGroupSeqId", "00002",
                "quantity", new BigDecimal("1")));
        postService("changeOrderStatus", Map.of(
                "orderId", orderId, "statusId", "ORDER_APPROVED", "setItemStatus", "Y"))
                .andExpect(status().isOk());
        postService("quickShipEntireOrder", Map.of("orderId", orderId)).andExpect(status().isOk());
        List<?> shipments = data.find("Shipment", Map.of("primaryOrderId", orderId));
        assertEquals(2, shipments.size());
        assertTrue(shipments.stream().map(shipment -> data.get(shipment, "primaryShipGroupSeqId").toString())
                .toList().containsAll(List.of("00001", "00002")));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_ADMIN"})
    void ac05c_cancellationUpdatesAndDistributesShipGroupQuantities() throws Exception {
        String orderId = storeOrder(List.of(Map.of(
                "productId", "DEMO_ORDER_PRODUCT_1",
                "quantity", "3",
                "unitPrice", "1")));
        Object firstAssociation = data.find("OrderItemShipGroupAssoc", Map.of(
                "orderId", orderId, "orderItemSeqId", "00001")).get(0);
        data.set(firstAssociation, "quantity", new BigDecimal("1"));
        Object firstGroup = data.find("OrderItemShipGroup", Map.of(
                "orderId", orderId, "shipGroupSeqId", "00001")).get(0);
        Map<String, Object> secondGroup = new LinkedHashMap<>(data.toMap(firstGroup));
        secondGroup.put("shipGroupSeqId", "00002");
        data.create("OrderItemShipGroup", secondGroup);
        data.create("OrderItemShipGroupAssoc", Map.of(
                "orderId", orderId,
                "orderItemSeqId", "00001",
                "shipGroupSeqId", "00002",
                "quantity", new BigDecimal("2")));
        postService("cancelOrderItem", Map.of(
                "orderId", orderId, "orderItemSeqId", "00001", "cancelQuantity", "2"))
                .andExpect(status().isOk());
        List<?> associations = data.find("OrderItemShipGroupAssoc", Map.of(
                "orderId", orderId, "orderItemSeqId", "00001"));
        assertEquals(0, new BigDecimal(data.get(associations.get(0), "cancelQuantity").toString())
                .compareTo(BigDecimal.ONE));
        Object secondAssociation = associations.stream()
                .filter(association -> "00002".equals(data.get(association, "shipGroupSeqId")))
                .findFirst().orElseThrow();
        assertEquals(0, new BigDecimal(data.get(secondAssociation, "cancelQuantity").toString())
                .compareTo(BigDecimal.ONE));
        postService("cancelOrderItem", Map.of(
                "orderId", orderId,
                "orderItemSeqId", "00001",
                "shipGroupSeqId", "00002",
                "cancelQuantity", "1")).andExpect(status().isOk());
        assertEquals(0, new BigDecimal(data.get(secondAssociation, "cancelQuantity").toString())
                .compareTo(new BigDecimal("2")));
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_ADMIN"})
    void ac04_invalidStatusAndUnapprovedShipmentReturnConflict() throws Exception {
        String orderId = storeOrder();
        postService("quickShipEntireOrder", Map.of("orderId", orderId)).andExpect(status().isConflict());
        postService("changeOrderStatus", Map.of("orderId", orderId, "statusId", "ORDER_COMPLETED"))
                .andExpect(status().isConflict());
        String unapprovedItemOrder = storeOrder();
        postService("changeOrderStatus", Map.of(
                "orderId", unapprovedItemOrder, "statusId", "ORDER_APPROVED"))
                .andExpect(status().isOk());
        postService("quickShipEntireOrder", Map.of("orderId", unapprovedItemOrder))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_ADMIN"})
    void ac05_cancelAccumulatesAndRejectsOverCancellation() throws Exception {
        String orderId = storeOrder();
        postService("cancelOrderItem", Map.of(
                "orderId", orderId, "orderItemSeqId", "00001", "cancelQuantity", "1"))
                .andExpect(status().isOk());
        postService("cancelOrderItemNoActions", Map.of(
                "orderId", orderId, "orderItemSeqId", "00001", "cancelQuantity", "2"))
                .andExpect(status().isBadRequest());
        postService("cancelOrderItemNoActions", Map.of(
                "orderId", orderId, "orderItemSeqId", "00001", "cancelQuantity", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_ADMIN"})
    void ac05b_shipAndBillOnlyTheRemainingUncancelledQuantity() throws Exception {
        String orderId = storeOrder(List.of(
                Map.of("productId", "DEMO_ORDER_PRODUCT_1", "quantity", "3", "unitPrice", "1"),
                Map.of("productId", "DEMO_ORDER_PRODUCT_2", "quantity", "3", "unitPrice", "1")));
        postService("cancelOrderItem", Map.of(
                "orderId", orderId, "orderItemSeqId", "00001", "cancelQuantity", "3"))
                .andExpect(status().isOk());
        postService("cancelOrderItem", Map.of(
                "orderId", orderId, "orderItemSeqId", "00002", "cancelQuantity", "1"))
                .andExpect(status().isOk());
        postService("changeOrderStatus", Map.of(
                "orderId", orderId, "statusId", "ORDER_APPROVED", "setItemStatus", "Y"))
                .andExpect(status().isOk());
        postService("quickShipEntireOrder", Map.of("orderId", orderId)).andExpect(status().isOk());
        List<?> shipments = data.find("Shipment", Map.of("primaryOrderId", orderId));
        String shipmentId = data.get(shipments.get(0), "shipmentId").toString();
        List<?> shippedItems = data.find("ShipmentItem", Map.of("shipmentId", shipmentId));
        assertEquals(1, shippedItems.size());
        assertEquals(new BigDecimal("2"), data.get(shippedItems.get(0), "quantity"));
        postService("createInvoiceForOrder", Map.of("orderId", orderId)).andExpect(status().isOk());
        List<?> billing = data.find("OrderItemBilling", Map.of("orderId", orderId));
        assertEquals(1, billing.size());
        assertEquals(new BigDecimal("2"), data.get(billing.get(0), "quantity"));
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_ADMIN"})
    void ac06_multiPreservesConflictAndRollsBackEarlierCalls() throws Exception {
        String orderId = storeOrder();
        String body = JSON.writeValueAsString(List.of(
                Map.of("orderId", orderId, "statusId", "ORDER_APPROVED"),
                Map.of("orderId", orderId, "statusId", "NOT_A_STATUS")));
        MvcResult result = mockMvc.perform(post("/api/order/services/changeOrderStatus/multi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andReturn();
        JsonNode bodyJson = JSON.readTree(result.getResponse().getContentAsString());
        assertEquals("error", bodyJson.get("responseMessage").asText());
        mockMvc.perform(get("/api/order/entities/OrderHeader").param("orderId", orderId))
                .andExpect(status().isOk())
                .andExpect(resultValue -> assertEquals(
                        "ORDER_CREATED", JSON.readTree(resultValue.getResponse().getContentAsString())
                                .get(0).get("statusId").asText()));
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_ADMIN"})
    void ac06b_multiReturnsBadRequestForInvalidInput() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/order/services/changeOrderStatus/multi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{}]"))
                .andExpect(status().isBadRequest())
                .andReturn();
        assertEquals(400, JSON.readTree(result.getResponse().getContentAsString())
                .get("errors").get(0).get("status").asInt());
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_ADMIN", "ORDERMGR_CREATE"})
    void ac06f_multiReportsIntegrityViolationAtFailingItem() throws Exception {
        jdbcTemplate.execute("CREATE UNIQUE INDEX AC06F_UQ ON order_item_type(description)");
        try {
            Map<String, Object> first = Map.of(
                    "orderItemTypeId", "AC06F_FIRST", "description", "AC06F_DUPLICATE");
            Map<String, Object> second = Map.of(
                    "orderItemTypeId", "AC06F_SECOND", "description", "AC06F_DUPLICATE");
            MvcResult result = mockMvc.perform(post("/api/order/services/createOrderItemType/multi")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JSON.writeValueAsString(List.of(first, second))))
                    .andExpect(status().isConflict())
                    .andReturn();
            JsonNode error = JSON.readTree(result.getResponse().getContentAsString()).get("errors").get(0);
            assertEquals(1, error.get("index").asInt());
            assertEquals(409, error.get("status").asInt());
            assertEquals("Data integrity violation", error.get("errorMessage").asText());
        } finally {
            jdbcTemplate.execute("DROP INDEX AC06F_UQ");
        }
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_VIEW"})
    void ac06c_readonlyMultiCreateIsForbiddenAndDoesNotPersist() throws Exception {
        String orderItemTypeId = "AC06C_READONLY_CREATE";
        mockMvc.perform(post("/api/order/services/createOrderItemType/multi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsString(List.of(Map.of("orderItemTypeId", orderItemTypeId)))))
                .andExpect(status().isForbidden());
        MvcResult result = mockMvc.perform(get("/api/order/entities/OrderItemType")
                        .param("orderItemTypeId", orderItemTypeId))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals(0, JSON.readTree(result.getResponse().getContentAsString()).size());
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_VIEW"})
    void ac06d_readonlyCanReadCustRequestEntity() throws Exception {
        mockMvc.perform(get("/api/order/entities/CustRequest")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_VIEW"})
    void ac07_sensitiveAccessNeedsSensitiveAuthority() throws Exception {
        mockMvc.perform(get("/api/order/entities/OrderPaymentPreference")
                        .param("orderId", "ORDER-DEMO-PAYMENT"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_VIEW", "ORDERMGR_SENSITIVE_VIEW"})
    void ac08_sensitiveAuthorityCanReadCatalogEntity() throws Exception {
        mockMvc.perform(get("/api/order/entities/OrderPaymentPreference")
                        .param("orderId", "ORDER-DEMO-PAYMENT"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_VIEW"})
    void ac09_componentScopeAndWritePermissionAreEnforced() throws Exception {
        mockMvc.perform(post("/api/order/services/storeOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/not-order/entities/OrderHeader"))
                .andExpect(status().isNotFound());
        mockMvc.perform(post("/api/not-order/services/changeOrderStatus/multi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isNotFound());
    }

    @Test
    void ac10_missingAuthenticationIsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/order/entities/OrderHeader"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_ADMIN"})
    void ac11_entityAutoGeneratesMissingInOutSequenceKey() throws Exception {
        String orderId = storeOrder();
        MvcResult result = postService("createOrderItemShipGroup", Map.of("orderId", orderId))
                .andExpect(status().isOk())
                .andReturn();
        String shipGroupSeqId = JSON.readTree(result.getResponse().getContentAsString())
                .get("shipGroupSeqId")
                .asText();
        assertTrue(shipGroupSeqId.matches("\\d{5}"));
        mockMvc.perform(get("/api/order/entities/OrderItemShipGroup")
                        .param("orderId", orderId)
                        .param("shipGroupSeqId", shipGroupSeqId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_ADMIN"})
    void ac12_entityAutoCrudAndInputValidation() throws Exception {
        Map<String, String> create = Map.of(
                "orderItemTypeId", "AC12_ORDER_ITEM_TYPE", "description", "Before");
        postService("createOrderItemType", create).andExpect(status().isOk());
        postService("createOrderItemType", create).andExpect(status().isConflict());
        postService("updateOrderItemType", Map.of(
                "orderItemTypeId", "AC12_ORDER_ITEM_TYPE", "description", "After"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/order/entities/OrderItemType")
                        .param("orderItemTypeId", "AC12_ORDER_ITEM_TYPE"))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(
                        "After", JSON.readTree(result.getResponse().getContentAsString())
                                .get(0).get("description").asText()));
        postService("updateOrderItemType", Map.of(
                "orderItemTypeId", "AC12_ORDER_ITEM_TYPE", "undeclaredField", "rejected"))
                .andExpect(status().isBadRequest());
        postService("updateOrderItemType", Map.of(
                "orderItemTypeId", "AC12_ABSENT_TYPE", "description", "Missing"))
                .andExpect(status().isNotFound());
        postService("createOrderItemShipGroup", Map.of())
                .andExpect(status().isBadRequest());
        postService("deleteOrderItemType", Map.of("orderItemTypeId", "AC12_ORDER_ITEM_TYPE"))
                .andExpect(status().isOk());
    }

    @ParameterizedTest(name = "ac13_simpleHandler_{0}")
    @MethodSource("simpleServiceNames")
    @WithMockUser(authorities = {
        "OFBTOOLS_VIEW", "ORDERMGR_ADMIN", "ORDERMGR_CRQ_ADMIN", "ORDERMGR_CRQ_CREATE"
    })
    void ac13_everySimpleServiceHasARegisteredHandler(String serviceName) throws Exception {
        int statusCode = postService(serviceName, Map.of()).andReturn().getResponse().getStatus();
        assertTrue(statusCode != 404 && statusCode != 401 && statusCode != 403, serviceName);
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_VIEW"})
    void ac14_sensitiveEntityAutoServiceNeedsSensitiveAuthority() throws Exception {
        postService("createOrderPaymentPreference", Map.of())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "OFBTOOLS_VIEW")
    void ac15_noOrderAuthorityIsForbidden() throws Exception {
        mockMvc.perform(get("/api/order/entities/OrderHeader"))
                .andExpect(status().isForbidden());
    }

    @Test
    void ac16_httpsIsSecureByDefaultAndSwaggerIsDisabledInTestProfile() throws Exception {
        assertTrue(new MigrationSecurityProperties().isRequireHttps());
        mockMvc.perform(get("/v3/api-docs")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_ADMIN"})
    void ac17_entityAutoIgnoresCallerSuppliedOutPrimaryKey() throws Exception {
        MvcResult request = postService("createCustRequest", Map.of(
                "fromPartyId", "DemoCustomer",
                "custRequestName", "OUT primary key regression"))
                .andExpect(status().isOk())
                .andReturn();
        String custRequestId = JSON.readTree(request.getResponse().getContentAsString())
                .get("custRequestId")
                .asText();

        MvcResult createdStatus = postService("createCustRequestStatus", Map.of(
                "custRequestId", custRequestId,
                "statusId", "CRQ_DRAFT",
                "custRequestStatusId", "CALLER_STATUS_ID"))
                .andExpect(status().isOk())
                .andReturn();
        String generatedId = JSON.readTree(createdStatus.getResponse().getContentAsString())
                .get("custRequestStatusId")
                .asText();

        assertNotEquals("CALLER_STATUS_ID", generatedId);
        mockMvc.perform(get("/api/order/entities/CustRequestStatus")
                        .param("custRequestStatusId", generatedId))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(
                        1, JSON.readTree(result.getResponse().getContentAsString()).size()));
        mockMvc.perform(get("/api/order/entities/CustRequestStatus")
                        .param("custRequestStatusId", "CALLER_STATUS_ID"))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(
                        0, JSON.readTree(result.getResponse().getContentAsString()).size()));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {
        "OFBTOOLS_VIEW", "ORDERMGR_ADMIN", "ORDERMGR_CRQ_ADMIN", "ORDERMGR_CRQ_CREATE"
    })
    void ac18_simpleServicesSupportNormalCustomerRequestPaths() throws Exception {
        String orderId = storeOrder();
        postService("orderAdjustmentPermissionCheck", Map.of("mainAction", "CREATE"))
                .andExpect(status().isOk());
        postService("updateOrderStatusFromReceipt", Map.of("orderId", orderId))
                .andExpect(status().isOk());
        postService("returnPermissionCheck", Map.of("mainAction", "CREATE"))
                .andExpect(status().isOk());
        postService("custRequestPermissionCheck", Map.of(
                "fromPartyId", "AC18_OTHER_PARTY", "mainAction", "CREATE"))
                .andExpect(status().isOk());

        String requestId = createCustRequest("AC18_SOURCE_REQUEST", "source story");
        postService("updateCustRequest", Map.of(
                "custRequestId", requestId, "custRequestName", "Updated request"))
                .andExpect(status().isOk());
        postService("copyCustRequestItem", Map.of(
                "custRequestId", requestId, "custRequestItemSeqId", "00001"))
                .andExpect(status().isOk());
        postService("checkStatusCustRequest", Map.of("custRequestId", requestId))
                .andExpect(status().isOk());
        postService("updateCustRequestLastModifiedDate", Map.of("custRequestId", requestId))
                .andExpect(status().isOk());
        MvcResult note = postService("createCustRequestNote", Map.of(
                "custRequestId", requestId, "noteInfo", "Request note"))
                .andExpect(status().isOk())
                .andReturn();
        String noteId = JSON.readTree(note.getResponse().getContentAsString()).get("noteId").asText();
        postService("updateCustRequestNote", Map.of(
                "custRequestId", requestId, "noteId", noteId, "noteInfo", "Updated note"))
                .andExpect(status().isOk());
        postService("createCustRequestItemNote", Map.of(
                "custRequestId", requestId,
                "custRequestItemSeqId", "00001",
                "note", "Item note"))
                .andExpect(status().isOk());

        MvcResult fromCart = postService("createCustRequestFromCart", Map.of(
                "fromPartyId", "DemoCustomer",
                "custRequestName", "Cart request",
                "cartItems", List.of(Map.of(
                        "productId", "DEMO_ORDER_PRODUCT_1", "quantity", BigDecimal.ONE))))
                .andExpect(status().isOk())
                .andReturn();
        String cartRequestId = JSON.readTree(fromCart.getResponse().getContentAsString())
                .get("custRequestId").asText();
        postService("setCustRequestStatus", Map.of(
                "custRequestId", requestId, "statusId", "CRQ_SUBMITTED"))
                .andExpect(status().isOk());

        data.create("ShoppingListType", Map.of(
                "shoppingListTypeId", "AC18_LIST_TYPE", "description", "Acceptance list"));
        data.create("ShoppingList", Map.of(
                "shoppingListId", "AC18_LIST",
                "shoppingListTypeId", "AC18_LIST_TYPE",
                "partyId", "DemoCustomer",
                "listName", "Acceptance shopping list"));
        data.create("ShoppingListItem", Map.of(
                "shoppingListId", "AC18_LIST",
                "shoppingListItemSeqId", "00001",
                "productId", "DEMO_ORDER_PRODUCT_1",
                "quantity", BigDecimal.ONE));
        postService("createCustRequestFromShoppingList", Map.of("shoppingListId", "AC18_LIST"))
                .andExpect(status().isOk());

        data.create("RoleType", Map.of(
                "roleTypeId", "AC18_REQUEST_ROLE", "description", "Acceptance request role"));
        data.create("CustRequestParty", Map.of(
                "custRequestId", requestId,
                "partyId", "DemoCustomer",
                "roleTypeId", "AC18_REQUEST_ROLE",
                "fromDate", Timestamp.from(Instant.now())));
        postService("getCustRequestsByRole", Map.of("roleTypeId", "AC18_REQUEST_ROLE"))
                .andExpect(status().isOk());

        data.create("CommunicationEventType", Map.of(
                "communicationEventTypeId", "AC18_EMAIL", "description", "Acceptance email"));
        data.create("CommunicationEvent", Map.of(
                "communicationEventId", "AC18_EVENT",
                "communicationEventTypeId", "AC18_EMAIL",
                "statusId", "COM_IN_PROGRESS",
                "partyIdFrom", "DemoCustomer",
                "entryDate", Timestamp.from(Instant.now()),
                "subject", "Email request"));
        postService("createCustRequestFromCommEvent", Map.of("communicationEventId", "AC18_EVENT"))
                .andExpect(status().isOk());

        data.create("RequirementType", Map.of(
                "requirementTypeId", "AC18_REQUIREMENT", "description", "Acceptance requirement"));
        postService("createRequirement", Map.of("requirementTypeId", "AC18_REQUIREMENT"))
                .andExpect(status().isOk());

        String deleteRequestId = createCustRequest("AC18_DELETE_REQUEST", null);
        postService("deleteCustRequest", Map.of("custRequestId", deleteRequestId))
                .andExpect(status().isOk());
        assertTrue(!cartRequestId.isBlank());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_ADMIN"})
    void ac18b_deleteCustRequestLeavesItemsLikeOFBiz() throws Exception {
        String requestId = createCustRequest("AC18_DELETE_WITH_ITEM", "request item");
        assertTrue(!data.find("CustRequestItem", Map.of("custRequestId", requestId)).isEmpty());
        postService("deleteCustRequest", Map.of("custRequestId", requestId))
                .andExpect(status().isOk());
        assertTrue(data.find("CustRequest", Map.of("custRequestId", requestId)).isEmpty());
        assertTrue(!data.find("CustRequestItem", Map.of("custRequestId", requestId)).isEmpty());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_ADMIN"})
    void ac19_getCustRequestsByRoleDoesNotAcceptAnotherLogin() throws Exception {
        data.create("UserLogin", Map.of("userLoginId", "AC19_OTHER_LOGIN", "partyId", "Company"));
        MvcResult created = postService("createCustRequest", Map.of(
                "fromPartyId", "Company", "custRequestName", "AC19_OTHER_REQUEST"))
                .andExpect(status().isOk())
                .andReturn();
        String otherRequestId = JSON.readTree(created.getResponse().getContentAsString())
                .get("custRequestId").asText();
        data.create("CustRequestParty", Map.of(
                "custRequestId", otherRequestId,
                "partyId", "Company",
                "roleTypeId", "AC19_ROLE",
                "fromDate", Timestamp.from(Instant.now())));
        MvcResult lookup = postService("getCustRequestsByRole", Map.of(
                "roleTypeId", "AC19_ROLE", "userLoginId", "AC19_OTHER_LOGIN")).andReturn();
        int responseStatus = lookup.getResponse().getStatus();
        assertTrue(responseStatus == 400
                || (responseStatus == 200
                        && !lookup.getResponse().getContentAsString().contains(otherRequestId)));
    }

    @Test
    @Transactional
    @WithMockUser(username = "AC20_NO_LOGIN", authorities = {
        "OFBTOOLS_VIEW", "ORDERMGR_ADMIN", "ORDERMGR_CRQ_ADMIN", "ORDERMGR_CRQ_CREATE"
    })
    void ac20_getCustRequestsByRoleIsEmptyWithoutUserLogin() throws Exception {
        assertTrue(data.find("UserLogin", Map.of("userLoginId", "AC20_NO_LOGIN")).isEmpty());
        data.create("CustRequest", Map.of(
                "custRequestId", "AC20_DEMO_REQUEST",
                "fromPartyId", "DemoCustomer",
                "statusId", "CRQ_DRAFT",
                "custRequestName", "Demo customer request"));
        data.create("RoleType", Map.of(
                "roleTypeId", "AC20_REQUEST_ROLE", "description", "Request role"));
        data.create("CustRequestParty", Map.of(
                "custRequestId", "AC20_DEMO_REQUEST",
                "partyId", "DemoCustomer",
                "roleTypeId", "AC20_REQUEST_ROLE",
                "fromDate", Timestamp.from(Instant.now())));

        MvcResult result = postService("getCustRequestsByRole", Map.of("roleTypeId", "AC20_REQUEST_ROLE"))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals(
                0,
                JSON.readTree(result.getResponse().getContentAsString())
                        .get("custRequestAndRoles")
                        .size());
        postService("createCustRequestFromCart", Map.of(
                "cartItems", List.of(Map.of(
                        "productId", "DEMO_ORDER_PRODUCT_1", "quantity", BigDecimal.ONE))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @WithMockUser(username = "AC21_NO_LOGIN", authorities = {
        "OFBTOOLS_VIEW", "ORDERMGR_ADMIN", "ORDERMGR_CRQ_ADMIN", "ORDERMGR_CRQ_CREATE"
    })
    void ac21_createCustRequestAllowsDraftWithoutFromPartyId() throws Exception {
        assertTrue(data.find("UserLogin", Map.of("userLoginId", "AC21_NO_LOGIN")).isEmpty());
        MvcResult result = postService("createCustRequest", Map.of("custRequestName", "Draft without party"))
                .andExpect(status().isOk())
                .andReturn();
        String custRequestId = JSON.readTree(result.getResponse().getContentAsString())
                .get("custRequestId").asText();
        Object request = data.require("CustRequest", Map.of("custRequestId", custRequestId));
        assertEquals("CRQ_DRAFT", data.get(request, "statusId"));
        assertTrue(data.get(request, "fromPartyId") == null);
    }

    private String createCustRequest(String name, String story) throws Exception {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("fromPartyId", "DemoCustomer");
        input.put("custRequestName", name);
        if (story != null) {
            input.put("story", story);
        }
        MvcResult result = postService("createCustRequest", input)
                .andExpect(status().isOk())
                .andReturn();
        return JSON.readTree(result.getResponse().getContentAsString()).get("custRequestId").asText();
    }

    private static Stream<Arguments> simpleServiceNames() {
        return Stream.of(
                "orderAdjustmentPermissionCheck", "updateOrderStatusFromReceipt", "returnPermissionCheck",
                "custRequestPermissionCheck", "createCustRequest", "updateCustRequest", "deleteCustRequest",
                "copyCustRequestItem", "checkStatusCustRequest", "updateCustRequestLastModifiedDate",
                "createCustRequestNote", "updateCustRequestNote", "createCustRequestItemNote",
                "createCustRequestFromCart", "createCustRequestFromShoppingList", "getCustRequestsByRole",
                "setCustRequestStatus", "createCustRequestFromCommEvent", "createRequirement")
                .map(Arguments::of);
    }

    private String storeOrder() throws Exception {
        return storeOrder(List.of(Map.of(
                "productId", "DEMO_ORDER_PRODUCT_1",
                "quantity", "2")));
    }

    private String storeOrder(List<Map<String, Object>> orderItems) throws Exception {
        MvcResult result = postService("storeOrder", Map.of(
                "partyId", "DemoCustomer",
                "orderTypeId", "SALES_ORDER",
                "productStoreId", "DEMO_ORDER_STORE",
                "orderItems", orderItems))
                .andExpect(status().isOk())
                .andReturn();
        return JSON.readTree(result.getResponse().getContentAsString()).get("orderId").asText();
    }

    private org.springframework.test.web.servlet.ResultActions postService(String name, Map<String, ?> input)
            throws Exception {
        return mockMvc.perform(post("/api/order/services/" + name)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.writeValueAsString(input)));
    }
}
