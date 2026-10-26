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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private JsonNode call(String service, Map<String, Object> body) throws Exception {
        String response = mockMvc.perform(service(service, body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JSON.readTree(response);
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder service(
            String service, Map<String, Object> body) throws Exception {
        return post("/api/product/services/" + service)
                .with(user("admin").authorities(adminAuthorities()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.writeValueAsBytes(body));
    }

    private static SimpleGrantedAuthority[] adminAuthorities() {
        return authorities("OFBTOOLS_VIEW", "PRODUCT_ADMIN", "CATALOG_VIEW", "CATALOG_CREATE",
                "FACILITY_VIEW", "FACILITY_CREATE");
    }

    private static SimpleGrantedAuthority[] authorities(String... values) {
        return java.util.Arrays.stream(values)
                .map(SimpleGrantedAuthority::new)
                .toArray(SimpleGrantedAuthority[]::new);
    }
}
