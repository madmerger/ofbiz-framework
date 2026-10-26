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

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(
        classes = ProductMigrationApplication.class,
        properties = "migration.security.require-https=false")
@AutoConfigureMockMvc
class CustomerDigitalDownloadServicesTest {
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final String SUPPLIER_ERROR =
            "Cannot update this Product: you are not associated with it as the Supplier";
    private static final String CONTENT_ERROR =
            "Cannot add this Content: you are not associated with it as an Owner";
    private static final String FROM_DATE = "2020-01-02 03:04:05";
    private static final String OTHER_FROM_DATE = "2020-01-03 03:04:05";
    private static final String[] SUPPLIER_AUTHORITIES = {
        "OFBTOOLS_VIEW", "CATALOG_VIEW", "CATALOG_UPDATE"
    };
    private static final String[] READONLY_AUTHORITIES = {"OFBTOOLS_VIEW", "CATALOG_VIEW"};

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void ac01_createUsesDefaultCurrencyAndPersistsProductPriceAndSupplier() throws Exception {
        Fixture fixture = fixture();
        seedUser(fixture);
        seedStore(fixture, null);

        ResultActions response = service(
                "createCustomerDigitalDownloadProduct",
                fixture.userLoginId,
                createInput(fixture, "12.50", "Digital product description", null),
                SUPPLIER_AUTHORITIES);
        JsonNode body = JSON.readTree(response.andExpect(status().isOk())
                .andExpect(jsonPath("$.currencyUomId").value("USD"))
                .andExpect(jsonPath("$.minimumOrderQuantity").value(1))
                .andExpect(jsonPath("$.availableFromDate").isNotEmpty())
                .andReturn().getResponse().getContentAsString());
        String productId = body.path("productId").asText();
        assertFalse(productId.isBlank());

        assertCreatedProduct(fixture, productId, "USD", "12.50", "Digital product description");
    }

    @Test
    void ac02_createUsesRequestedCurrencyForPriceAndSupplier() throws Exception {
        Fixture fixture = fixture();
        seedUser(fixture);
        seedStore(fixture, null);

        ResultActions response = service(
                "createCustomerDigitalDownloadProduct",
                fixture.userLoginId,
                createInput(fixture, "17.25", null, "EUR"),
                SUPPLIER_AUTHORITIES);
        JsonNode body = JSON.readTree(response.andExpect(status().isOk())
                .andExpect(jsonPath("$.currencyUomId").value("EUR"))
                .andReturn().getResponse().getContentAsString());

        assertCreatedProduct(fixture, body.path("productId").asText(), "EUR", "17.25", null);
    }

    @Test
    void ac03_createWithUploadCategoryReturns500AndRollsBack() throws Exception {
        Fixture fixture = fixture();
        seedUser(fixture);
        seedStore(fixture, fixture.categoryId);

        expectError(service(
                        "createCustomerDigitalDownloadProduct",
                        fixture.userLoginId,
                        createInput(fixture, "12.50", null, null),
                        SUPPLIER_AUTHORITIES),
                500,
                "Failure in create operation for entity [ProductCategoryMember]: primary key field fromDate is null");

        assertNoProductNamed(fixture.productName);
        assertTrue(rows("SupplierProduct", Map.of("partyId", fixture.partyId)).isEmpty());
    }

    @Test
    void ac04_createRequiresPrice() throws Exception {
        Fixture fixture = fixture();
        seedUser(fixture);
        seedStore(fixture, null);
        Map<String, Object> input = createInput(fixture, "12.50", null, null);
        input.remove("price");

        expectError(service(
                        "createCustomerDigitalDownloadProduct",
                        fixture.userLoginId,
                        input,
                        SUPPLIER_AUTHORITIES),
                400,
                "Missing required service input parameter: price");
    }

    @Test
    void ac05_createRejectsReadonlyUserWithoutPersistingProduct() throws Exception {
        Fixture fixture = fixture();
        seedStore(fixture, null);

        service(
                        "createCustomerDigitalDownloadProduct",
                        fixture.userLoginId,
                        createInput(fixture, "12.50", null, null),
                        READONLY_AUTHORITIES)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.responseMessage").value("error"));

        assertNoProductNamed(fixture.productName);
    }

    @Test
    void ac06_createWithoutUserLoginPartyRollsBack() throws Exception {
        Fixture fixture = fixture();
        seedStore(fixture, null);

        service(
                        "createCustomerDigitalDownloadProduct",
                        fixture.userLoginId,
                        createInput(fixture, "12.50", null, null),
                        SUPPLIER_AUTHORITIES)
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.responseMessage").value("error"));

        assertNoProductNamed(fixture.productName);
    }

    @Test
    void ac07_updateChangesProductFieldsAndPrice() throws Exception {
        Fixture fixture = fixture();
        seedUser(fixture);
        seedProduct(fixture, "Original name", "Original description");
        seedSupplier(fixture.productId, fixture.partyId, "USD", FROM_DATE, "1");
        seedPrice(fixture.productId, "USD", FROM_DATE, "5.00");
        Map<String, Object> input = updateInput(fixture);
        input.put("productName", "Updated name");
        input.put("description", "Updated description");
        input.put("price", new BigDecimal("7.50"));

        service(
                        "updateCustomerDigitalDownloadProduct",
                        fixture.userLoginId,
                        input,
                        SUPPLIER_AUTHORITIES)
                .andExpect(status().isOk());

        Object product = findOne("Product", Map.of("productId", fixture.productId));
        assertEquals("Updated name", entities.get(product, "productName"));
        assertEquals("Updated description", entities.get(product, "description"));
        assertEquals(0, ((BigDecimal) entities.get(
                rows("ProductPrice", Map.of("productId", fixture.productId)).get(0), "price"))
                .compareTo(new BigDecimal("7.50")));
    }

    @Test
    void ac08_updateIgnoresEmptyProductNameAndChangesPrice() throws Exception {
        Fixture fixture = fixture();
        seedUser(fixture);
        seedProduct(fixture, "Original name", "Original description");
        seedSupplier(fixture.productId, fixture.partyId, "USD", FROM_DATE, "1");
        seedPrice(fixture.productId, "USD", FROM_DATE, "5.00");
        Map<String, Object> input = updateInput(fixture);
        input.put("productName", "");
        input.put("price", new BigDecimal("9.25"));

        service(
                        "updateCustomerDigitalDownloadProduct",
                        fixture.userLoginId,
                        input,
                        SUPPLIER_AUTHORITIES)
                .andExpect(status().isOk());

        Object product = findOne("Product", Map.of("productId", fixture.productId));
        assertEquals("Original name", entities.get(product, "productName"));
        assertEquals("Original description", entities.get(product, "description"));
        assertPrice(fixture.productId, "9.25");
    }

    @Test
    void ac09_updateRejectsProductOwnedByAnotherSupplier() throws Exception {
        Fixture fixture = fixture();
        seedUser(fixture);
        seedProduct(fixture, "Original name", "Original description");
        seedSupplier(fixture.productId, fixture.otherPartyId, "USD", FROM_DATE, "1");
        Map<String, Object> input = updateInput(fixture);
        input.put("productName", "Changed name");

        expectError(service(
                        "updateCustomerDigitalDownloadProduct",
                        fixture.userLoginId,
                        input,
                        SUPPLIER_AUTHORITIES),
                400,
                SUPPLIER_ERROR);

        Object product = findOne("Product", Map.of("productId", fixture.productId));
        assertEquals("Original name", entities.get(product, "productName"));
        assertEquals("Original description", entities.get(product, "description"));
    }

    @Test
    void ac10_updateRequiresAvailableFromDate() throws Exception {
        Fixture fixture = fixture();
        seedUser(fixture);
        seedSupplier(fixture.productId, fixture.partyId, "USD", FROM_DATE, "1");
        Map<String, Object> input = updateInput(fixture);
        input.remove("availableFromDate");

        expectError(service(
                        "updateCustomerDigitalDownloadProduct",
                        fixture.userLoginId,
                        input,
                        SUPPLIER_AUTHORITIES),
                400,
                "Missing required service input parameter: availableFromDate");
    }

    @Test
    void ac11_updateRejectsReadonlyUser() throws Exception {
        Fixture fixture = fixture();

        service(
                        "updateCustomerDigitalDownloadProduct",
                        fixture.userLoginId,
                        updateInput(fixture),
                        READONLY_AUTHORITIES)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.responseMessage").value("error"));
    }

    @Test
    void ac12_updateMissingPriceRollsBackEarlierProductChange() throws Exception {
        Fixture fixture = fixture();
        seedUser(fixture);
        seedProduct(fixture, "Original name", "Original description");
        seedSupplier(fixture.productId, fixture.partyId, "USD", FROM_DATE, "1");
        Map<String, Object> input = updateInput(fixture);
        input.put("productName", "Changed name");
        input.put("price", new BigDecimal("7.50"));

        expectError(service(
                        "updateCustomerDigitalDownloadProduct",
                        fixture.userLoginId,
                        input,
                        SUPPLIER_AUTHORITIES),
                404,
                "Entity value not found with name: productPrice");

        Object product = findOne("Product", Map.of("productId", fixture.productId));
        assertEquals("Original name", entities.get(product, "productName"));
        assertEquals("Original description", entities.get(product, "description"));
    }

    @Test
    void ac13_deleteExpiresOnlyTheCurrentSuppliersRows() throws Exception {
        Fixture fixture = fixture();
        seedUser(fixture);
        seedProduct(fixture, "Product", null);
        seedSupplier(fixture.productId, fixture.partyId, "USD", FROM_DATE, "1");
        seedSupplier(fixture.productId, fixture.partyId, "EUR", OTHER_FROM_DATE, "2");
        seedSupplier(fixture.productId, fixture.otherPartyId, "USD", FROM_DATE, "1");
        seedCategoryMember(fixture);

        service(
                        "deleteCustomerDigitalDownloadProduct",
                        fixture.userLoginId,
                        Map.of("productId", fixture.productId),
                        SUPPLIER_AUTHORITIES)
                .andExpect(status().isOk());

        List<?> ownRows = rows("SupplierProduct", Map.of(
                "productId", fixture.productId, "partyId", fixture.partyId));
        assertEquals(2, ownRows.size());
        ownRows.forEach(row -> assertNotNull(entities.get(row, "availableThruDate")));
        List<?> otherRows = rows("SupplierProduct", Map.of(
                "productId", fixture.productId, "partyId", fixture.otherPartyId));
        assertEquals(1, otherRows.size());
        assertNull(entities.get(otherRows.get(0), "availableThruDate"));
        Object membership = rows("ProductCategoryMember", Map.of("productId", fixture.productId)).get(0);
        assertNull(entities.get(membership, "thruDate"));
    }

    @Test
    void ac14_deleteRejectsProductOwnedByAnotherSupplier() throws Exception {
        Fixture fixture = fixture();
        seedUser(fixture);
        seedSupplier(fixture.productId, fixture.otherPartyId, "USD", FROM_DATE, "1");

        expectError(service(
                        "deleteCustomerDigitalDownloadProduct",
                        fixture.userLoginId,
                        Map.of("productId", fixture.productId),
                        SUPPLIER_AUTHORITIES),
                400,
                SUPPLIER_ERROR);
    }

    @Test
    void ac15_deleteRequiresProductId() throws Exception {
        Fixture fixture = fixture();
        seedUser(fixture);

        expectError(service(
                        "deleteCustomerDigitalDownloadProduct",
                        fixture.userLoginId,
                        Map.of(),
                        SUPPLIER_AUTHORITIES),
                400,
                "Missing required service input parameter: productId");
    }

    @Test
    void ac16_deleteRejectsReadonlyUser() throws Exception {
        Fixture fixture = fixture();

        service(
                        "deleteCustomerDigitalDownloadProduct",
                        fixture.userLoginId,
                        Map.of("productId", fixture.productId),
                        READONLY_AUTHORITIES)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.responseMessage").value("error"));
    }

    @Test
    void ac17_addFileCreatesDigitalDownloadProductContent() throws Exception {
        Fixture fixture = fixture();
        seedUser(fixture);
        seedSupplier(fixture.productId, fixture.partyId, "USD", FROM_DATE, "1");
        seedContentRole(fixture.contentId, fixture.partyId, "OWNER");

        service(
                        "addCustomerDigitalDownloadProductFile",
                        fixture.userLoginId,
                        Map.of("contentId", fixture.contentId, "productId", fixture.productId),
                        SUPPLIER_AUTHORITIES)
                .andExpect(status().isOk());

        List<?> productContents = rows("ProductContent", Map.of(
                "contentId", fixture.contentId, "productId", fixture.productId));
        assertEquals(1, productContents.size());
        Object productContent = productContents.get(0);
        assertEquals("DIGITAL_DOWNLOAD", entities.get(productContent, "productContentTypeId"));
        assertNotNull(entities.get(productContent, "fromDate"));
        assertNull(entities.get(productContent, "thruDate"));
    }

    @Test
    void ac18_addFileReportsBothOwnershipErrorsAndCreatesNothing() throws Exception {
        Fixture fixture = fixture();
        seedUser(fixture);

        service(
                        "addCustomerDigitalDownloadProductFile",
                        fixture.userLoginId,
                        Map.of("contentId", fixture.contentId, "productId", fixture.productId),
                        SUPPLIER_AUTHORITIES)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseMessage").value("error"))
                .andExpect(jsonPath("$.errorMessage").value(containsString(SUPPLIER_ERROR)))
                .andExpect(jsonPath("$.errorMessage").value(containsString(CONTENT_ERROR)));

        assertTrue(rows("ProductContent", Map.of(
                "contentId", fixture.contentId, "productId", fixture.productId)).isEmpty());
    }

    @Test
    void ac19_addFileRequiresOwnerRole() throws Exception {
        Fixture fixture = fixture();
        seedUser(fixture);
        seedSupplier(fixture.productId, fixture.partyId, "USD", FROM_DATE, "1");
        seedContentRole(fixture.contentId, fixture.partyId, "CONTENT_ADMIN");

        expectError(service(
                        "addCustomerDigitalDownloadProductFile",
                        fixture.userLoginId,
                        Map.of("contentId", fixture.contentId, "productId", fixture.productId),
                        SUPPLIER_AUTHORITIES),
                400,
                CONTENT_ERROR);
    }

    @Test
    void ac20_addFileRequiresContentId() throws Exception {
        Fixture fixture = fixture();
        seedUser(fixture);
        seedSupplier(fixture.productId, fixture.partyId, "USD", FROM_DATE, "1");
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("productId", fixture.productId);

        expectError(service(
                        "addCustomerDigitalDownloadProductFile",
                        fixture.userLoginId,
                        input,
                        SUPPLIER_AUTHORITIES),
                400,
                "Missing required service input parameter: contentId");
    }

    @Test
    void ac21_addFileRejectsReadonlyUser() throws Exception {
        Fixture fixture = fixture();

        service(
                        "addCustomerDigitalDownloadProductFile",
                        fixture.userLoginId,
                        Map.of("contentId", fixture.contentId, "productId", fixture.productId),
                        READONLY_AUTHORITIES)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.responseMessage").value("error"));
    }

    @Test
    void ac22_removeFileExpiresProductContent() throws Exception {
        Fixture fixture = fixture();
        seedUser(fixture);
        seedSupplier(fixture.productId, fixture.partyId, "USD", FROM_DATE, "1");
        seedProductContent(fixture, FROM_DATE);

        service(
                        "removeCustomerDigitalDownloadProductFile",
                        fixture.userLoginId,
                        removeInput(fixture),
                        SUPPLIER_AUTHORITIES)
                .andExpect(status().isOk());

        Object productContent = findOne("ProductContent", Map.of(
                "contentId", fixture.contentId,
                "productId", fixture.productId,
                "productContentTypeId", "DIGITAL_DOWNLOAD",
                "fromDate", Timestamp.valueOf(FROM_DATE)));
        assertNotNull(entities.get(productContent, "thruDate"));
    }

    @Test
    void ac23_removeFileRejectsProductOwnedByAnotherSupplier() throws Exception {
        Fixture fixture = fixture();
        seedUser(fixture);
        seedProductContent(fixture, FROM_DATE);

        expectError(service(
                        "removeCustomerDigitalDownloadProductFile",
                        fixture.userLoginId,
                        removeInput(fixture),
                        SUPPLIER_AUTHORITIES),
                400,
                SUPPLIER_ERROR);
    }

    @Test
    void ac24_removeFileReturnsNotFoundWhenProductContentIsMissing() throws Exception {
        Fixture fixture = fixture();
        seedUser(fixture);
        seedSupplier(fixture.productId, fixture.partyId, "USD", FROM_DATE, "1");

        expectError(service(
                        "removeCustomerDigitalDownloadProductFile",
                        fixture.userLoginId,
                        removeInput(fixture),
                        SUPPLIER_AUTHORITIES),
                404,
                "Entity value not found with name: productContent");
    }

    @Test
    void ac25_removeFileRequiresFromDate() throws Exception {
        Fixture fixture = fixture();
        seedUser(fixture);
        seedSupplier(fixture.productId, fixture.partyId, "USD", FROM_DATE, "1");
        seedProductContent(fixture, FROM_DATE);
        Map<String, Object> input = removeInput(fixture);
        input.remove("fromDate");

        expectError(service(
                        "removeCustomerDigitalDownloadProductFile",
                        fixture.userLoginId,
                        input,
                        SUPPLIER_AUTHORITIES),
                400,
                "Missing required service input parameter: fromDate");
    }

    @Test
    void ac26_removeFileRejectsReadonlyUser() throws Exception {
        Fixture fixture = fixture();

        service(
                        "removeCustomerDigitalDownloadProductFile",
                        fixture.userLoginId,
                        removeInput(fixture),
                        READONLY_AUTHORITIES)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.responseMessage").value("error"));
    }

    private ResultActions service(
            String serviceName, String username, Map<String, Object> input, String... authorities) throws Exception {
        return mockMvc.perform(post("/api/product/services/" + serviceName)
                .with(user(username).authorities(authoritySet(authorities)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.writeValueAsBytes(input)));
    }

    private static SimpleGrantedAuthority[] authoritySet(String... authorities) {
        return java.util.Arrays.stream(authorities)
                .map(SimpleGrantedAuthority::new)
                .toArray(SimpleGrantedAuthority[]::new);
    }

    private ResultActions expectError(ResultActions response, int statusCode, String message) throws Exception {
        return response.andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.responseMessage").value("error"))
                .andExpect(jsonPath("$.errorMessage").value(message));
    }

    private static Fixture fixture() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        return new Fixture(
                "user" + suffix,
                "party" + suffix,
                "otherparty" + suffix,
                "product" + suffix,
                "content" + suffix,
                "store" + suffix,
                "category" + suffix,
                "Digital product " + suffix);
    }

    private Map<String, Object> createInput(
            Fixture fixture, String price, String description, String currencyUomId) {
        Map<String, Object> input = values(
                "productName", fixture.productName,
                "productStoreId", fixture.storeId,
                "price", new BigDecimal(price));
        if (description != null) {
            input.put("description", description);
        }
        if (currencyUomId != null) {
            input.put("currencyUomId", currencyUomId);
        }
        return input;
    }

    private static Map<String, Object> updateInput(Fixture fixture) {
        return values(
                "productId", fixture.productId,
                "currencyUomId", "USD",
                "minimumOrderQuantity", BigDecimal.ONE,
                "availableFromDate", FROM_DATE);
    }

    private static Map<String, Object> removeInput(Fixture fixture) {
        return values(
                "contentId", fixture.contentId,
                "productId", fixture.productId,
                "productContentTypeId", "DIGITAL_DOWNLOAD",
                "fromDate", FROM_DATE);
    }

    private static Map<String, Object> values(Object... values) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int index = 0; index < values.length; index += 2) {
            result.put((String) values[index], values[index + 1]);
        }
        return result;
    }

    private void seedUser(Fixture fixture) {
        inTransaction(() -> {
            Object userLogin = entity("UserLogin");
            entities.set(userLogin, "userLoginId", fixture.userLoginId);
            entities.set(userLogin, "partyId", fixture.partyId);
            entityManager.persist(userLogin);
            return null;
        });
    }

    private void seedStore(Fixture fixture, String categoryId) {
        inTransaction(() -> {
            Object productStore = entity("ProductStore");
            entities.set(productStore, "productStoreId", fixture.storeId);
            if (categoryId != null) {
                entities.set(productStore, "digProdUploadCategoryId", categoryId);
            }
            entityManager.persist(productStore);
            return null;
        });
    }

    private void seedProduct(Fixture fixture, String productName, String description) {
        inTransaction(() -> {
            Object product = entity("Product");
            entities.set(product, "productId", fixture.productId);
            entities.set(product, "productName", productName);
            entities.set(product, "internalName", productName);
            entities.set(product, "productTypeId", "DIGITAL_GOOD");
            if (description != null) {
                entities.set(product, "description", description);
            }
            entityManager.persist(product);
            return null;
        });
    }

    private void seedSupplier(String productId, String partyId, String currency, String fromDate, String quantity) {
        inTransaction(() -> {
            Object supplierProduct = entity("SupplierProduct");
            entities.set(supplierProduct, "productId", productId);
            entities.set(supplierProduct, "partyId", partyId);
            entities.set(supplierProduct, "availableFromDate", Timestamp.valueOf(fromDate));
            entities.set(supplierProduct, "minimumOrderQuantity", new BigDecimal(quantity));
            entities.set(supplierProduct, "currencyUomId", currency);
            entityManager.persist(supplierProduct);
            return null;
        });
    }

    private void seedPrice(String productId, String currency, String fromDate, String price) {
        inTransaction(() -> {
            Object productPrice = entity("ProductPrice");
            entities.set(productPrice, "productId", productId);
            entities.set(productPrice, "productPriceTypeId", "DEFAULT_PRICE");
            entities.set(productPrice, "productPricePurposeId", "PURCHASE");
            entities.set(productPrice, "currencyUomId", currency);
            entities.set(productPrice, "productStoreGroupId", "_NA_");
            entities.set(productPrice, "fromDate", Timestamp.valueOf(fromDate));
            entities.set(productPrice, "price", new BigDecimal(price));
            entityManager.persist(productPrice);
            return null;
        });
    }

    private void seedContentRole(String contentId, String partyId, String roleTypeId) {
        inTransaction(() -> {
            Object contentRole = entity("ContentRole");
            entities.set(contentRole, "contentId", contentId);
            entities.set(contentRole, "partyId", partyId);
            entities.set(contentRole, "roleTypeId", roleTypeId);
            entities.set(contentRole, "fromDate", Timestamp.valueOf(FROM_DATE));
            entityManager.persist(contentRole);
            return null;
        });
    }

    private void seedProductContent(Fixture fixture, String fromDate) {
        inTransaction(() -> {
            Object productContent = entity("ProductContent");
            entities.set(productContent, "productId", fixture.productId);
            entities.set(productContent, "contentId", fixture.contentId);
            entities.set(productContent, "productContentTypeId", "DIGITAL_DOWNLOAD");
            entities.set(productContent, "fromDate", Timestamp.valueOf(fromDate));
            entityManager.persist(productContent);
            return null;
        });
    }

    private void seedCategoryMember(Fixture fixture) {
        inTransaction(() -> {
            Object member = entity("ProductCategoryMember");
            entities.set(member, "productCategoryId", fixture.categoryId);
            entities.set(member, "productId", fixture.productId);
            entities.set(member, "fromDate", Timestamp.valueOf(FROM_DATE));
            entityManager.persist(member);
            return null;
        });
    }

    private void assertCreatedProduct(
            Fixture fixture, String productId, String currency, String price, String description) {
        assertFalse(productId.isBlank());
        Object product = findOne("Product", Map.of("productId", productId));
        assertEquals(fixture.productName, entities.get(product, "productName"));
        assertEquals(fixture.productName, entities.get(product, "internalName"));
        assertEquals(description, entities.get(product, "description"));
        assertEquals("DIGITAL_GOOD", entities.get(product, "productTypeId"));
        assertEquals(1, rows("ProductPrice", Map.of("productId", productId)).size());
        Object productPrice = rows("ProductPrice", Map.of("productId", productId)).get(0);
        assertEquals("DEFAULT_PRICE", entities.get(productPrice, "productPriceTypeId"));
        assertEquals("PURCHASE", entities.get(productPrice, "productPricePurposeId"));
        assertEquals(currency, entities.get(productPrice, "currencyUomId"));
        assertEquals("_NA_", entities.get(productPrice, "productStoreGroupId"));
        assertNotNull(entities.get(productPrice, "fromDate"));
        assertEquals(0, ((BigDecimal) entities.get(productPrice, "price")).compareTo(new BigDecimal(price)));
        List<?> supplierProducts = rows("SupplierProduct", Map.of(
                "productId", productId, "partyId", fixture.partyId));
        assertEquals(1, supplierProducts.size());
        Object supplierProduct = supplierProducts.get(0);
        assertEquals(currency, entities.get(supplierProduct, "currencyUomId"));
        assertEquals(0, ((BigDecimal) entities.get(supplierProduct, "minimumOrderQuantity"))
                .compareTo(BigDecimal.ONE));
        assertEquals(
                entities.get(productPrice, "fromDate"),
                entities.get(supplierProduct, "availableFromDate"));
        assertTrue(rows("ProductCategoryMember", Map.of("productId", productId)).isEmpty());
    }

    private void assertPrice(String productId, String expectedPrice) {
        Object productPrice = rows("ProductPrice", Map.of("productId", productId)).get(0);
        assertEquals(0, ((BigDecimal) entities.get(productPrice, "price"))
                .compareTo(new BigDecimal(expectedPrice)));
    }

    private void assertNoProductNamed(String productName) {
        assertTrue(rows("Product", Map.of("productName", productName)).isEmpty());
    }

    private Object entity(String entityName) {
        return entities.newEntity(entities.entityClass(entityName));
    }

    private Object findOne(String entityName, Map<String, ?> keys) {
        return inTransaction(() -> entities.findByPrimaryKey(entities.entityClass(entityName), keys));
    }

    private List<?> rows(String entityName, Map<String, ?> fields) {
        return inTransaction(() -> entities.findByEquality(entities.entityClass(entityName), fields));
    }

    private <T> T inTransaction(Supplier<T> action) {
        return new TransactionTemplate(transactionManager).execute(status -> action.get());
    }

    private record Fixture(
            String userLoginId,
            String partyId,
            String otherPartyId,
            String productId,
            String contentId,
            String storeId,
            String categoryId,
            String productName) {
    }
}
