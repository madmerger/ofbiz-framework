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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
@Transactional
class ProductInventoryFlowTest {
    private static final ObjectMapper JSON = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private EntityManager entityManager;

    @Test
    void createProductAllocatesIdAndDefaultsFlagsAndAuditFields() throws Exception {
        JsonNode result = call("createProduct", Map.of(
                "productTypeId", "FINISHED_GOOD", "internalName", "Auto-ID Demo Product"));
        String productId = result.path("productId").asText();
        assertFalse(productId.isBlank());
        Map<String, Object> product = entities.toMap(entities.findByPrimaryKey(
                entities.entityClass("Product"), Map.of("productId", productId)));
        assertEquals("N", product.get("isVirtual"));
        assertEquals("N", product.get("isVariant"));
        assertNotNull(product.get("createdDate"));
        assertEquals("admin", product.get("createdByUserLogin"));
    }

    @Test
    void createProductRejectsInvalidAndDuplicateIds() throws Exception {
        mockMvc.perform(service("createProduct", Map.of(
                        "productId", "invalid id", "productTypeId", "FINISHED_GOOD", "internalName", "Invalid")))
                .andExpect(status().isBadRequest());

        call("createProduct", Map.of(
                "productId", "E2E_DUP_PROD", "productTypeId", "FINISHED_GOOD", "internalName", "Duplicate"));
        mockMvc.perform(service("createProduct", Map.of(
                        "productId", "E2E_DUP_PROD", "productTypeId", "FINISHED_GOOD", "internalName", "Duplicate")))
                .andExpect(status().isConflict());
    }

    @Test
    void createProductRequiresProductTypeAndInternalName() throws Exception {
        mockMvc.perform(service("createProduct", Map.of("productTypeId", "FINISHED_GOOD")))
                .andExpect(status().isBadRequest());
        mockMvc.perform(service("createProduct", Map.of("internalName", "Missing Type")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createInventoryItemDefaultsFacilityOwnerCurrencyAndReceivedDate() throws Exception {
        call("createProduct", Map.of(
                "productId", "E2E_ITEM_PROD",
                "productTypeId", "FINISHED_GOOD",
                "internalName", "Inventory Flow Product"));
        JsonNode result = call("createInventoryItem", Map.of(
                "inventoryItemTypeId", "NON_SERIAL_INV_ITEM",
                "productId", "E2E_ITEM_PROD",
                "facilityId", "E2E_WH",
                "unitCost", 0));
        assertFalse(result.path("inventoryItemId").asText().isBlank());
        Map<String, Object> item = entities.toMap(entities.findByPrimaryKey(
                entities.entityClass("InventoryItem"),
                Map.of("inventoryItemId", result.path("inventoryItemId").asText())));
        assertEquals("E2E_COMPANY", item.get("ownerPartyId"));
        assertEquals("USD", item.get("currencyUomId"));
        assertNotNull(item.get("datetimeReceived"));
    }

    @Test
    void createInventoryItemRequiresInputsAndFacilityPermission() throws Exception {
        mockMvc.perform(service("createInventoryItem", Map.of("productId", "missing")))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/product/services/createInventoryItem")
                        .with(user("readonly").authorities(authorities("OFBTOOLS_VIEW", "FACILITY_VIEW")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(Map.of(
                                "inventoryItemTypeId", "NON_SERIAL_INV_ITEM",
                                "productId", "missing",
                                "facilityId", "E2E_WH",
                                "unitCost", 1))))
                .andExpect(status().isForbidden());
    }

    @Test
    void receiveInventoryProductValidatesQuantityAndRequiredFields() throws Exception {
        mockMvc.perform(service("receiveInventoryProduct", Map.of(
                        "productId", "missing",
                        "facilityId", "E2E_WH",
                        "inventoryItemTypeId", "NON_SERIAL_INV_ITEM",
                        "quantityAccepted", 0,
                        "quantityRejected", 0)))
                .andExpect(status().isBadRequest());
        mockMvc.perform(service("receiveInventoryProduct", Map.of(
                        "productId", "missing",
                        "facilityId", "E2E_WH",
                        "inventoryItemTypeId", "NON_SERIAL_INV_ITEM",
                        "quantityAccepted", 1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void receiveInventoryProductUpdatesAnExistingSerializedItem() throws Exception {
        call("createProduct", Map.of(
                "productId", "E2E_EXISTING_PROD",
                "productTypeId", "FINISHED_GOOD",
                "internalName", "Existing Item Product"));
        JsonNode created = call("createInventoryItem", Map.of(
                "inventoryItemTypeId", "SERIALIZED_INV_ITEM",
                "productId", "E2E_EXISTING_PROD",
                "facilityId", "E2E_WH",
                "unitCost", 1));
        String inventoryItemId = created.path("inventoryItemId").asText();

        JsonNode received = call("receiveInventoryProduct", Map.of(
                "productId", "E2E_EXISTING_PROD",
                "facilityId", "E2E_WH",
                "inventoryItemTypeId", "SERIALIZED_INV_ITEM",
                "currentInventoryItemId", inventoryItemId,
                "quantityAccepted", 1,
                "quantityRejected", 0));
        assertEquals(inventoryItemId, received.path("inventoryItemId").asText());
        Map<String, Object> item = entities.toMap(entities.findByPrimaryKey(
                entities.entityClass("InventoryItem"), Map.of("inventoryItemId", inventoryItemId)));
        assertEquals("INV_AVAILABLE", item.get("statusId"));
        Map<String, Object> receipt = entities.toMap(entities.findByPrimaryKey(
                entities.entityClass("ShipmentReceipt"), Map.of("receiptId", received.path("receiptId").asText())));
        assertEquals(inventoryItemId, receipt.get("inventoryItemId"));

        mockMvc.perform(service("receiveInventoryProduct", Map.of(
                        "productId", "E2E_EXISTING_PROD",
                        "facilityId", "E2E_WH",
                        "inventoryItemTypeId", "SERIALIZED_INV_ITEM",
                        "currentInventoryItemId", "E2E_MISSING_ITEM",
                        "quantityAccepted", 1,
                        "quantityRejected", 0)))
                .andExpect(status().isNotFound());
    }

    @Test
    void receiveSerializedRejectedOnlyCreatesReceiptWithoutInventoryItem() throws Exception {
        createProduct("E2E_REJECTED");

        JsonNode received = call("receiveInventoryProduct", Map.of(
                "productId", "E2E_REJECTED",
                "facilityId", "E2E_WH",
                "inventoryItemTypeId", "SERIALIZED_INV_ITEM",
                "quantityAccepted", 0,
                "quantityRejected", 1));

        assertEquals("", received.path("inventoryItemId").asText());
        assertTrue(entities.findByEquality(entities.entityClass("InventoryItem"),
                Map.of("productId", "E2E_REJECTED")).isEmpty());
        Map<String, Object> receipt = entity("ShipmentReceipt", Map.of(
                "receiptId", received.path("receiptId").asText()));
        assertDecimalEquals("0", receipt.get("quantityAccepted"));
        assertDecimalEquals("1", receipt.get("quantityRejected"));
    }

    @Test
    void receiveSerializedAcceptedQuantityCreatesAvailableItemsWithOneUnitTotals() throws Exception {
        createProduct("E2E_SERIAL");

        call("receiveInventoryProduct", Map.of(
                "productId", "E2E_SERIAL",
                "facilityId", "E2E_WH",
                "inventoryItemTypeId", "SERIALIZED_INV_ITEM",
                "quantityAccepted", 2,
                "quantityRejected", 0,
                "unitCost", 1));

        List<?> items = entities.findByEquality(entities.entityClass("InventoryItem"),
                Map.of("productId", "E2E_SERIAL"));
        assertEquals(2, items.size());
        for (Object item : items) {
            Map<String, Object> fields = entities.toMap(item);
            assertEquals("INV_AVAILABLE", fields.get("statusId"));
            assertDecimalEquals("1", fields.get("quantityOnHandTotal"));
            assertDecimalEquals("1", fields.get("availableToPromiseTotal"));
        }
    }

    @Test
    void receiveSerializedReturnPreservesReturnedStatus() throws Exception {
        createProduct("E2E_RETURN_SERIAL");

        JsonNode received = call("receiveInventoryProduct", Map.of(
                "productId", "E2E_RETURN_SERIAL",
                "facilityId", "E2E_WH",
                "inventoryItemTypeId", "SERIALIZED_INV_ITEM",
                "statusId", "INV_RETURNED",
                "returnId", "E2E_RETURN",
                "quantityAccepted", 1,
                "quantityRejected", 0,
                "unitCost", 1));

        Map<String, Object> item = entity("InventoryItem", Map.of(
                "inventoryItemId", received.path("inventoryItemId").asText()));
        assertEquals("INV_RETURNED", item.get("statusId"));
        assertDecimalEquals("0", item.get("availableToPromiseTotal"));
        assertDecimalEquals("1", item.get("quantityOnHandTotal"));
    }

    @Test
    void receiveInventoryProductPreservesProvidedDatetimeReceived() throws Exception {
        createProduct("E2E_DATED");
        Timestamp receivedAt = Timestamp.from(Instant.parse("2020-01-02T03:04:05Z"));

        JsonNode received = call("receiveInventoryProduct", Map.of(
                "productId", "E2E_DATED",
                "facilityId", "E2E_WH",
                "inventoryItemTypeId", "NON_SERIAL_INV_ITEM",
                "quantityAccepted", 1,
                "quantityRejected", 0,
                "unitCost", 1,
                "datetimeReceived", "2020-01-02T03:04:05Z"));

        Map<String, Object> receipt = entity("ShipmentReceipt", Map.of(
                "receiptId", received.path("receiptId").asText()));
        assertEquals(receivedAt, receipt.get("datetimeReceived"));
        Map<String, Object> item = entity("InventoryItem", Map.of(
                "inventoryItemId", received.path("inventoryItemId").asText()));
        assertEquals(receivedAt, item.get("datetimeReceived"));
    }

    @Test
    void receiveInventoryProductBalancesShortfallReservations() throws Exception {
        createProduct("E2E_SHORTFALL_PROD");
        String inventoryItemId = "E2E_SHORTFALL_ITEM";
        seed("InventoryItem", Map.of(
                "inventoryItemId", inventoryItemId,
                "productId", "E2E_SHORTFALL_PROD",
                "facilityId", "E2E_WH",
                "inventoryItemTypeId", "NON_SERIAL_INV_ITEM",
                "statusId", "INV_AVAILABLE",
                "ownerPartyId", "E2E_COMPANY",
                "currencyUomId", "USD",
                "unitCost", BigDecimal.ONE,
                "availableToPromiseTotal", BigDecimal.ZERO,
                "quantityOnHandTotal", BigDecimal.ZERO));
        String orderId = "E2E_SHORTFALL_ORDER";
        seed("OrderHeader", Map.of("orderId", orderId));
        seed("OrderItem", Map.of(
                "orderId", orderId,
                "orderItemSeqId", "00001",
                "productId", "E2E_SHORTFALL_PROD",
                "quantity", BigDecimal.ONE));
        seed("OrderItemShipGrpInvRes", Map.of(
                "orderId", orderId,
                "shipGroupSeqId", "00001",
                "orderItemSeqId", "00001",
                "inventoryItemId", inventoryItemId,
                "quantity", BigDecimal.ONE,
                "quantityNotAvailable", BigDecimal.ONE,
                "priority", "2",
                "sequenceId", 1));

        call("receiveInventoryProduct", Map.of(
                "productId", "E2E_SHORTFALL_PROD",
                "facilityId", "E2E_WH",
                "inventoryItemTypeId", "NON_SERIAL_INV_ITEM",
                "currentInventoryItemId", inventoryItemId,
                "quantityAccepted", 1,
                "quantityRejected", 0));

        Map<String, Object> reservation = entity("OrderItemShipGrpInvRes", Map.of(
                "orderId", orderId,
                "shipGroupSeqId", "00001",
                "orderItemSeqId", "00001",
                "inventoryItemId", inventoryItemId));
        assertNull(reservation.get("quantityNotAvailable"));
    }

    @Test
    void receiveExistingInventoryItemRequiresUpdatePermissionAndUsesUpdateService() throws Exception {
        createProduct("E2E_UPDATE_ITEM_PROD");
        JsonNode created = call("createInventoryItem", Map.of(
                "inventoryItemTypeId", "NON_SERIAL_INV_ITEM",
                "productId", "E2E_UPDATE_ITEM_PROD",
                "facilityId", "E2E_WH",
                "unitCost", 1));
        String inventoryItemId = created.path("inventoryItemId").asText();
        Object existing = entities.findByPrimaryKey(entities.entityClass("InventoryItem"),
                Map.of("inventoryItemId", inventoryItemId));
        entities.set(existing, "ownerPartyId", null);
        entityManager.flush();
        Map<String, Object> request = Map.of(
                "productId", "E2E_UPDATE_ITEM_PROD",
                "facilityId", "E2E_WH",
                "inventoryItemTypeId", "NON_SERIAL_INV_ITEM",
                "currentInventoryItemId", inventoryItemId,
                "ownerPartyId", "E2E_COMPANY",
                "quantityAccepted", 1,
                "quantityRejected", 0);

        mockMvc.perform(service("receiveInventoryProduct", request,
                        authorities("OFBTOOLS_VIEW", "PRODUCT_ADMIN", "FACILITY_VIEW", "FACILITY_CREATE")))
                .andExpect(status().isForbidden());
        assertNull(entities.get(existing, "ownerPartyId"));
        assertTrue(entities.findByEquality(entities.entityClass("ShipmentReceipt"),
                Map.of("inventoryItemId", inventoryItemId)).isEmpty());

        JsonNode received = call("receiveInventoryProduct", request,
                authorities("OFBTOOLS_VIEW", "PRODUCT_ADMIN", "FACILITY_VIEW",
                        "FACILITY_CREATE", "FACILITY_UPDATE"));

        assertEquals("E2E_COMPANY", received.path("oldOwnerPartyId").asText());
        Map<String, Object> updated = entity("InventoryItem", Map.of("inventoryItemId", inventoryItemId));
        assertEquals("E2E_COMPANY", updated.get("ownerPartyId"));
    }

    @Test
    void createProductAcceptsCatalogRoleCreateButRequiresCatalogCreatePermission() throws Exception {
        Map<String, Object> request = Map.of(
                "productId", "E2E_ROLE_PRODUCT",
                "productTypeId", "FINISHED_GOOD",
                "internalName", "Role-created Product");

        mockMvc.perform(service("createProduct", request,
                        authorities("OFBTOOLS_ADMIN", "PRODUCT_ADMIN", "FACILITY_ADMIN")))
                .andExpect(status().isForbidden());
        assertNull(entities.findByPrimaryKey(entities.entityClass("Product"),
                Map.of("productId", "E2E_ROLE_PRODUCT")));

        org.springframework.test.web.servlet.MvcResult roleResult = mockMvc.perform(service(
                        "createProduct", request,
                        authorities("OFBTOOLS_VIEW", "PRODUCT_ADMIN", "CATALOG_VIEW", "CATALOG_ROLE_CREATE")))
                .andReturn();
        assertEquals(200, roleResult.getResponse().getStatus(), roleResult.getResponse().getContentAsString());
        assertNotNull(entities.findByPrimaryKey(entities.entityClass("Product"),
                Map.of("productId", "E2E_ROLE_PRODUCT")));
    }

    private JsonNode call(String service, Map<String, Object> body) throws Exception {
        return call(service, body, adminAuthorities());
    }

    private JsonNode call(String service, Map<String, Object> body, SimpleGrantedAuthority[] authorities)
            throws Exception {
        org.springframework.test.web.servlet.MvcResult result = mockMvc.perform(service(service, body, authorities))
                .andReturn();
        assertEquals(200, result.getResponse().getStatus(), result.getResponse().getContentAsString());
        String response = result.getResponse().getContentAsString();
        return JSON.readTree(response);
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder service(
            String service, Map<String, Object> body) throws Exception {
        return service(service, body, adminAuthorities());
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder service(
            String service, Map<String, Object> body, SimpleGrantedAuthority[] authorities) throws Exception {
        return post("/api/product/services/" + service)
                .with(user("admin").authorities(authorities))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.writeValueAsBytes(body));
    }

    private Map<String, Object> entity(String entityName, Map<String, ?> keys) {
        Object entity = entities.findByPrimaryKey(entities.entityClass(entityName), keys);
        assertNotNull(entity, entityName + " not found for " + keys);
        return entities.toMap(entity);
    }

    private Object seed(String entityName, Map<String, Object> fields) {
        Object entity = entities.newEntity(entities.entityClass(entityName));
        fields.forEach((field, value) -> entities.set(entity, field, value));
        entityManager.persist(entity);
        entityManager.flush();
        return entity;
    }

    private void createProduct(String productId) throws Exception {
        call("createProduct", Map.of(
                "productId", productId,
                "productTypeId", "FINISHED_GOOD",
                "internalName", "Inventory Flow Product"));
    }

    private static void assertDecimalEquals(String expected, Object actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(new BigDecimal(actual.toString())));
    }

    private static SimpleGrantedAuthority[] adminAuthorities() {
        return authorities("OFBTOOLS_VIEW", "PRODUCT_ADMIN", "CATALOG_VIEW", "CATALOG_CREATE",
                "CATALOG_UPDATE", "FACILITY_VIEW", "FACILITY_CREATE", "FACILITY_UPDATE");
    }

    private static SimpleGrantedAuthority[] authorities(String... values) {
        return java.util.Arrays.stream(values)
                .map(SimpleGrantedAuthority::new)
                .toArray(SimpleGrantedAuthority[]::new);
    }
}
