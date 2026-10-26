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
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.ofbiz.migration.product;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.persistence.EntityManager;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(
        classes = ProductMigrationApplication.class,
        properties = "migration.security.require-https=false")
@AutoConfigureMockMvc
class RentalServicesTest {
    private static final String CREATE_SERVICE = "createFixedAssetAndLinkToProduct";
    private static final String GET_SERVICE = "getProductFirstRelatedFixedAsset";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private EntityManager entityManager;

    private final TransactionTemplate transactionTemplate;

    @Autowired
    RentalServicesTest(PlatformTransactionManager transactionManager) {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Test
    void ac01_createFixedAssetAndLinkToProductCreatesAssetAndLink() throws Exception {
        String productId = productId();
        seedProduct(productId);

        postService(CREATE_SERVICE, """
                        {"productId":"%s","productName":"Rental Car","internalName":"RC-INT"}
                        """.formatted(productId),
                "OFBTOOLS_VIEW", "CATALOG_VIEW", "CATALOG_UPDATE", "ACCOUNTING_CREATE")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fixedAssetId").doesNotExist());

        List<Map<String, Object>> assets = transactionTemplate.execute(status ->
                entities.findByEquality(entities.entityClass("FixedAsset"), Map.of("instanceOfProductId", productId))
                        .stream()
                        .map(asset -> Map.of(
                                "fixedAssetId", entities.get(asset, "fixedAssetId"),
                                "fixedAssetTypeId", entities.get(asset, "fixedAssetTypeId"),
                                "fixedAssetName", entities.get(asset, "fixedAssetName"),
                                "instanceOfProductId", entities.get(asset, "instanceOfProductId")))
                        .toList());
        assertEquals(1, assets.size());
        assertEquals("PROPERTY", assets.get(0).get("fixedAssetTypeId"));
        assertEquals("RC-INT", assets.get(0).get("fixedAssetName"));
        assertEquals(productId, assets.get(0).get("instanceOfProductId"));

        List<Map<String, Object>> links = transactionTemplate.execute(status ->
                entities.findByEquality(entities.entityClass("FixedAssetProduct"), Map.of("productId", productId))
                        .stream()
                        .map(link -> Map.of(
                                "fixedAssetId", entities.get(link, "fixedAssetId"),
                                "fixedAssetProductTypeId", entities.get(link, "fixedAssetProductTypeId"),
                                "fromDate", entities.get(link, "fromDate")))
                        .toList());
        assertEquals(1, links.size());
        assertEquals("FAPT_USE", links.get(0).get("fixedAssetProductTypeId"));
        assertEquals(assets.get(0).get("fixedAssetId"), links.get(0).get("fixedAssetId"));
        assertNotNull(links.get(0).get("fromDate"));
    }

    @Test
    void ac02_createFixedAssetAndLinkToProductUsesProductNameWhenInternalNameIsMissing() throws Exception {
        String productId = productId();
        seedProduct(productId);

        postService(CREATE_SERVICE, """
                        {"productId":"%s","productName":"Rental Car"}
                        """.formatted(productId),
                "OFBTOOLS_VIEW", "CATALOG_VIEW", "CATALOG_UPDATE", "ACCOUNTING_CREATE")
                .andExpect(status().isOk());

        List<Map<String, Object>> assets = transactionTemplate.execute(status ->
                entities.findByEquality(entities.entityClass("FixedAsset"), Map.of("instanceOfProductId", productId))
                        .stream()
                        .map(asset -> Map.of("fixedAssetName", entities.get(asset, "fixedAssetName")))
                        .toList());
        assertEquals(1, assets.size());
        assertEquals("Rental Car", assets.get(0).get("fixedAssetName"));
    }

    @Test
    void ac03_createFixedAssetAndLinkToProductRequiresProductId() throws Exception {
        postService(CREATE_SERVICE, "{}", "OFBTOOLS_VIEW", "CATALOG_VIEW", "CATALOG_UPDATE", "ACCOUNTING_CREATE")
                .andExpect(status().isBadRequest());
    }

    @Test
    void ac04_createFixedAssetAndLinkToProductRequiresCatalogUpdate() throws Exception {
        String productId = productId();
        seedProduct(productId);

        postService(CREATE_SERVICE, """
                        {"productId":"%s","productName":"Rental Car"}
                        """.formatted(productId),
                "OFBTOOLS_VIEW", "CATALOG_VIEW")
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorMessage").value("Service permission denied"));

        assertNoFixedAssets(productId);
    }

    @Test
    void ac05_createFixedAssetAndLinkToProductRequiresAccountingCreate() throws Exception {
        String productId = productId();
        seedProduct(productId);

        postService(CREATE_SERVICE, """
                        {"productId":"%s","productName":"Rental Car"}
                        """.formatted(productId),
                "OFBTOOLS_VIEW", "CATALOG_VIEW", "CATALOG_UPDATE")
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("ACCOUNTING_CREATE")));

        assertNoFixedAssets(productId);
    }

    @Test
    void ac06_createFixedAssetAndLinkToProductRejectsReadonlyUser() throws Exception {
        String productId = productId();
        seedProduct(productId);

        postService(CREATE_SERVICE, """
                        {"productId":"%s","productName":"Rental Car"}
                        """.formatted(productId),
                "OFBTOOLS_VIEW", "CATALOG_VIEW", "FACILITY_VIEW")
                .andExpect(status().isForbidden());

        assertNoFixedAssets(productId);
    }

    @Test
    void ac07_createFixedAssetAndLinkToProductAcceptsAccountingAdminAuthority() throws Exception {
        String productId = productId();
        seedProduct(productId);

        postService(CREATE_SERVICE, """
                        {"productId":"%s","productName":"Rental Car"}
                        """.formatted(productId),
                "OFBTOOLS_VIEW", "CATALOG_VIEW", "CATALOG_UPDATE", "ACCOUNTING_ADMIN")
                .andExpect(status().isOk());

        assertEquals(1, fixedAssetCount(productId));
        assertEquals(1, fixedAssetProductCount(productId));
    }

    @Test
    void ac01_getProductFirstRelatedFixedAssetReturnsTheFirstRelatedAsset() throws Exception {
        String productId = productId();
        String fixedAssetId = seedProductWithFixedAsset(productId);

        postService(GET_SERVICE, """
                        {"productId":"%s"}
                        """.formatted(productId),
                "OFBTOOLS_VIEW", "CATALOG_VIEW")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fixedAssetId").value(fixedAssetId))
                .andExpect(jsonPath("$.productId").value(productId));
    }

    @Test
    void ac02_getProductFirstRelatedFixedAssetOmitsFixedAssetIdWhenNoLinkExists() throws Exception {
        String productId = productId();
        seedProduct(productId);

        postService(GET_SERVICE, """
                        {"productId":"%s"}
                        """.formatted(productId),
                "OFBTOOLS_VIEW", "CATALOG_VIEW")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.fixedAssetId").doesNotExist());
    }

    @Test
    void ac03_getProductFirstRelatedFixedAssetReturnsNotFoundForUnknownProduct() throws Exception {
        postService(GET_SERVICE, """
                        {"productId":"%s"}
                        """.formatted(productId()),
                "OFBTOOLS_VIEW", "CATALOG_VIEW")
                .andExpect(status().isNotFound());
    }

    @Test
    void ac04_getProductFirstRelatedFixedAssetRequiresProductId() throws Exception {
        postService(GET_SERVICE, "{}", "OFBTOOLS_VIEW", "CATALOG_VIEW")
                .andExpect(status().isBadRequest());
    }

    @Test
    void ac05_getProductFirstRelatedFixedAssetRequiresCatalogView() throws Exception {
        postService(GET_SERVICE, """
                        {"productId":"%s"}
                        """.formatted(productId()),
                "OFBTOOLS_VIEW", "FACILITY_VIEW")
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorMessage").value("Service permission denied"));
    }

    private void seedProduct(String productId) {
        transactionTemplate.executeWithoutResult(status -> {
            Object product = entities.newEntity(entities.entityClass("Product"));
            entities.set(product, "productId", productId);
            entityManager.persist(product);
        });
    }

    private String seedProductWithFixedAsset(String productId) {
        String fixedAssetId = "FA_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        transactionTemplate.executeWithoutResult(status -> {
            Object product = entities.newEntity(entities.entityClass("Product"));
            entities.set(product, "productId", productId);
            entityManager.persist(product);

            Object fixedAsset = entities.newEntity(entities.entityClass("FixedAsset"));
            entities.set(fixedAsset, "fixedAssetId", fixedAssetId);
            entities.set(fixedAsset, "fixedAssetTypeId", "PROPERTY");
            entities.set(fixedAsset, "fixedAssetName", "Rental asset");
            entities.set(fixedAsset, "instanceOfProductId", productId);
            entityManager.persist(fixedAsset);

            Object link = entities.newEntity(entities.entityClass("FixedAssetProduct"));
            entities.set(link, "fixedAssetId", fixedAssetId);
            entities.set(link, "productId", productId);
            entities.set(link, "fixedAssetProductTypeId", "FAPT_USE");
            entities.set(link, "fromDate", Timestamp.from(Instant.now()));
            entityManager.persist(link);
        });
        return fixedAssetId;
    }

    private void assertNoFixedAssets(String productId) {
        assertEquals(0, fixedAssetCount(productId));
    }

    private int fixedAssetCount(String productId) {
        return transactionTemplate.execute(status ->
                entities.findByEquality(entities.entityClass("FixedAsset"), Map.of("instanceOfProductId", productId))
                        .size());
    }

    private int fixedAssetProductCount(String productId) {
        return transactionTemplate.execute(status ->
                entities.findByEquality(
                        entities.entityClass("FixedAssetProduct"), Map.of("productId", productId)).size());
    }

    private org.springframework.test.web.servlet.ResultActions postService(
            String serviceName, String body, String... grantedAuthorities) throws Exception {
        return mockMvc.perform(post("/api/product/services/" + serviceName)
                .with(user("rental").authorities(authorities(grantedAuthorities)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private static GrantedAuthority[] authorities(String... names) {
        return java.util.Arrays.stream(names)
                .map(SimpleGrantedAuthority::new)
                .toArray(GrantedAuthority[]::new);
    }

    private static String productId() {
        return "RENT_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}
