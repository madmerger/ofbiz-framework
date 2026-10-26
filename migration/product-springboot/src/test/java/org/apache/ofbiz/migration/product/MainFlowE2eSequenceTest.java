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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ofbiz.migration.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
        classes = ProductMigrationApplication.class,
        properties = {
            "spring.profiles.active=local",
            "PRODUCT_DEMO_PASSWORD=local-test-only",
            "migration.security.require-https=false"
        })
@AutoConfigureMockMvc
class MainFlowE2eSequenceTest {
    private static final ObjectMapper JSON = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void completesProductToShipmentSequenceOverHttp() throws Exception {
        JsonNode product = call("createProduct", Map.of(
                "productId", "E2E_PROD",
                "productTypeId", "FINISHED_GOOD",
                "internalName", "E2E Demo Product"));
        assertEquals("E2E_PROD", product.path("productId").asText());

        JsonNode receipt = call("receiveInventoryProduct", Map.of(
                "productId", "E2E_PROD",
                "facilityId", "E2E_WH",
                "inventoryItemTypeId", "NON_SERIAL_INV_ITEM",
                "quantityAccepted", 10,
                "quantityRejected", 0,
                "unitCost", 1.25));
        String inventoryItemId = receipt.path("inventoryItemId").asText();
        assertFalse(inventoryItemId.isBlank());
        assertDecimalEquals("10", inventory(inventoryItemId), "availableToPromiseTotal");

        call("reserveProductInventoryByFacility", Map.of(
                "productId", "E2E_PROD",
                "facilityId", "E2E_WH",
                "orderId", "E2E_ORDER",
                "orderItemSeqId", "00001",
                "shipGroupSeqId", "00001",
                "quantity", 2,
                "reserveOrderEnumId", "INVRO_FIFO_REC",
                "requireInventory", "N"));
        assertDecimalEquals("8", inventory(inventoryItemId), "availableToPromiseTotal");

        JsonNode shipmentResult = call("createShipment", Map.of(
                "shipmentTypeId", "SALES_SHIPMENT",
                "statusId", "SHIPMENT_INPUT",
                "originFacilityId", "E2E_WH",
                "primaryOrderId", "E2E_ORDER",
                "primaryShipGroupSeqId", "00001"));
        String shipmentId = shipmentResult.path("shipmentId").asText();
        assertFalse(shipmentId.isBlank());

        call("issueOrderItemShipGrpInvResToShipment", Map.of(
                "shipmentId", shipmentId,
                "orderId", "E2E_ORDER",
                "orderItemSeqId", "00001",
                "shipGroupSeqId", "00001",
                "inventoryItemId", inventoryItemId,
                "quantity", 2));
        Object completedOrderItem = entities.findByPrimaryKey(
                entities.entityClass("OrderItem"),
                Map.of("orderId", "E2E_ORDER", "orderItemSeqId", "00001"));
        assertEquals("ITEM_COMPLETED", entities.get(completedOrderItem, "statusId"));
        List<?> orderStatuses = entities.findByEquality(entities.entityClass("OrderStatus"),
                Map.of("orderId", "E2E_ORDER", "orderItemSeqId", "00001", "statusId", "ITEM_COMPLETED"));
        assertEquals(1, orderStatuses.size());
        assertEquals("admin", entities.get(orderStatuses.get(0), "statusUserLogin"));
        assertDecimalEquals("8", inventory(inventoryItemId), "quantityOnHandTotal");
        assertDecimalEquals("8", inventory(inventoryItemId), "availableToPromiseTotal");
        List<Map<String, Object>> issuances =
                entities.findByEquality(entities.entityClass("ItemIssuance"), Map.of("shipmentId", shipmentId))
                        .stream().map(entities::toMap).toList();
        assertEquals(1, issuances.size());
        assertDecimalEquals("2", issuances.get(0), "quantity");
        List<?> reservations =
                entities.findByEquality(entities.entityClass("OrderItemShipGrpInvRes"),
                        Map.of("orderId", "E2E_ORDER", "orderItemSeqId", "00001", "shipGroupSeqId", "00001"));
        assertFalse(reservations.stream().anyMatch(row -> decimal(entities.toMap(row), "quantity").signum() > 0));

        call("updateShipment", Map.of("shipmentId", shipmentId, "statusId", "SHIPMENT_PACKED"));
        call("updateShipment", Map.of("shipmentId", shipmentId, "statusId", "SHIPMENT_SHIPPED"));
        Map<String, Object> finalShipment = entities.toMap(entities.findByPrimaryKey(
                entities.entityClass("Shipment"), Map.of("shipmentId", shipmentId)));
        assertEquals("SHIPMENT_SHIPPED", finalShipment.get("statusId"));
    }

    @Test
    @Transactional
    void calculatesPriceFromDatedProductPrice() throws Exception {
        call("createProduct", Map.of(
                "productId", "E2E_PRICE_PROD",
                "productTypeId", "FINISHED_GOOD",
                "internalName", "E2E Dated Price Product"));
        Object price = entities.newEntity(entities.entityClass("ProductPrice"));
        entities.set(price, "productId", "E2E_PRICE_PROD");
        entities.set(price, "productPriceTypeId", "DEFAULT_PRICE");
        entities.set(price, "productPricePurposeId", "PURCHASE");
        entities.set(price, "currencyUomId", "USD");
        entities.set(price, "productStoreGroupId", "_NA_");
        entities.set(price, "fromDate", Timestamp.from(Instant.now().minusSeconds(3600)));
        entities.set(price, "price", new BigDecimal("7.50"));
        entityManager.persist(price);
        entityManager.flush();

        JsonNode result = call("calculateProductPrice", Map.of("productId", "E2E_PRICE_PROD"));
        assertEquals(7.5, result.path("price").asDouble());
        assertEquals(true, result.path("validPriceFound").asBoolean());
    }

    private JsonNode call(String service, Map<String, Object> body) throws Exception {
        String response = mockMvc.perform(post("/api/product/services/" + service)
                        .with(httpBasic("admin", "local-test-only"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(body)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JSON.readTree(response);
    }

    private Map<String, Object> inventory(String inventoryItemId) {
        Object item = entities.findByPrimaryKey(
                entities.entityClass("InventoryItem"), Map.of("inventoryItemId", inventoryItemId));
        assertNotNull(item);
        return entities.toMap(item);
    }

    private static BigDecimal decimal(Map<String, Object> values, String field) {
        Object value = values.get(field);
        return value instanceof BigDecimal amount ? amount : new BigDecimal(value.toString());
    }

    private static void assertDecimalEquals(String expected, Map<String, Object> values, String field) {
        assertEquals(0, new BigDecimal(expected).compareTo(decimal(values, field)));
    }

}
