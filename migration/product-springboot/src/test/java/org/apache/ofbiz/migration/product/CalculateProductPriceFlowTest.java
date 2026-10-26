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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.ofbiz.migration.product;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
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
        properties = "migration.security.require-https=false")
@AutoConfigureMockMvc
class CalculateProductPriceFlowTest {
    private static final ObjectMapper JSON = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void calculatesEffectiveBasicPrice() throws Exception {
        persistProduct("PRICE_FLOW_01");
        persistPrice("PRICE_FLOW_01", "DEFAULT_PRICE", new BigDecimal("12.50"),
                Timestamp.from(Instant.now().minusSeconds(3600)));

        mockMvc.perform(post("/api/product/services/calculateProductPrice")
                        .with(user("viewer").authorities(authorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(Map.of("productId", "PRICE_FLOW_01"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(12.5))
                .andExpect(jsonPath("$.defaultPrice").value(12.5))
                .andExpect(jsonPath("$.validPriceFound").value(true))
                .andExpect(jsonPath("$.currencyUsed").value("USD"))
                .andExpect(jsonPath("$.orderItemPriceInfos").isArray())
                .andExpect(jsonPath("$.orderItemPriceInfos.length()").value(0));
    }

    @Test
    @Transactional
    void ignoresPriceOutsideEffectiveDateRange() throws Exception {
        persistProduct("PRICE_FLOW_02");
        persistPrice("PRICE_FLOW_02", "DEFAULT_PRICE", new BigDecimal("9.99"),
                Timestamp.from(Instant.now().plusSeconds(3600)));

        mockMvc.perform(post("/api/product/services/calculateProductPrice")
                        .with(user("viewer").authorities(authorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(Map.of("productId", "PRICE_FLOW_02"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.validPriceFound").value(false))
                .andExpect(jsonPath("$.price").value(0));
    }

    @Test
    @Transactional
    void noPriceReturnsValidPriceFoundFalse() throws Exception {
        persistProduct("PRICE_FLOW_03");

        mockMvc.perform(post("/api/product/services/calculateProductPrice")
                        .with(user("viewer").authorities(authorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(Map.of("productId", "PRICE_FLOW_03"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.validPriceFound").value(false));
    }

    @Test
    @Transactional
    void emptyStoreDefaultsFallBackToActiveStoreGroupAndCurrency() throws Exception {
        persistProduct("PRICE_FLOW_STORE");
        persistStore("PRICE_STORE_FLOW_01");
        persistStoreGroup("PRICE_GROUP_FLOW_01");
        Timestamp fromDate = Timestamp.from(Instant.now().minusSeconds(3600));
        persistStoreGroupMember("PRICE_STORE_FLOW_01", "PRICE_GROUP_FLOW_01", fromDate);
        persistPrice("PRICE_FLOW_STORE", "DEFAULT_PRICE", new BigDecimal("15.00"), fromDate,
                "USD", "PRICE_GROUP_FLOW_01");

        mockMvc.perform(post("/api/product/services/calculateProductPrice")
                        .with(user("viewer").authorities(authorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(Map.of(
                                "productId", "PRICE_FLOW_STORE", "productStoreId", "PRICE_STORE_FLOW_01"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(15.0))
                .andExpect(jsonPath("$.currencyUsed").value("USD"))
                .andExpect(jsonPath("$.orderItemPriceInfos").isArray())
                .andExpect(jsonPath("$.orderItemPriceInfos.length()").value(0));
    }

    @Test
    @Transactional
    void minimumPriceClampsDefaultEvenWhenListPriceExists() throws Exception {
        persistProduct("PRICE_FLOW_MINIMUM");
        Timestamp fromDate = Timestamp.from(Instant.now().minusSeconds(3600));
        persistPrice("PRICE_FLOW_MINIMUM", "LIST_PRICE", new BigDecimal("20.00"), fromDate);
        persistPrice("PRICE_FLOW_MINIMUM", "MINIMUM_PRICE", new BigDecimal("5.00"), fromDate);

        mockMvc.perform(post("/api/product/services/calculateProductPrice")
                        .with(user("viewer").authorities(authorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(Map.of("productId", "PRICE_FLOW_MINIMUM"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(5.0))
                .andExpect(jsonPath("$.defaultPrice").value(5.0))
                .andExpect(jsonPath("$.listPrice").value(20.0));
    }

    @Test
    @Transactional
    void listPriceWithoutDefaultOrMinimumLeavesPriceAtZero() throws Exception {
        persistProduct("PRICE_FLOW_LIST_ONLY");
        persistPrice("PRICE_FLOW_LIST_ONLY", "LIST_PRICE", new BigDecimal("20.00"),
                Timestamp.from(Instant.now().minusSeconds(3600)));

        mockMvc.perform(post("/api/product/services/calculateProductPrice")
                        .with(user("viewer").authorities(authorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(Map.of("productId", "PRICE_FLOW_LIST_ONLY"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(0))
                .andExpect(jsonPath("$.listPrice").value(20.0))
                .andExpect(jsonPath("$.validPriceFound").value(false));
    }

    private void persistProduct(String productId) {
        Object product = entities.newEntity(entities.entityClass("Product"));
        entities.set(product, "productId", productId);
        entities.set(product, "productTypeId", "FINISHED_GOOD");
        entities.set(product, "isVirtual", "N");
        entityManager.persist(product);
        entityManager.flush();
    }

    private void persistPrice(String productId, String type, BigDecimal amount, Timestamp fromDate) {
        persistPrice(productId, type, amount, fromDate, "USD", "_NA_");
    }

    private void persistPrice(
            String productId, String type, BigDecimal amount, Timestamp fromDate, String currency, String storeGroup) {
        Object price = entities.newEntity(entities.entityClass("ProductPrice"));
        entities.set(price, "productId", productId);
        entities.set(price, "productPriceTypeId", type);
        entities.set(price, "productPricePurposeId", "PURCHASE");
        entities.set(price, "currencyUomId", currency);
        entities.set(price, "productStoreGroupId", storeGroup);
        entities.set(price, "fromDate", fromDate);
        entities.set(price, "price", amount);
        entityManager.persist(price);
        entityManager.flush();
    }

    private void persistStore(String productStoreId) {
        Object store = entities.newEntity(entities.entityClass("ProductStore"));
        entities.set(store, "productStoreId", productStoreId);
        entities.set(store, "primaryStoreGroupId", "");
        entities.set(store, "defaultCurrencyUomId", "");
        entityManager.persist(store);
        entityManager.flush();
    }

    private void persistStoreGroup(String productStoreGroupId) {
        Object group = entities.newEntity(entities.entityClass("ProductStoreGroup"));
        entities.set(group, "productStoreGroupId", productStoreGroupId);
        entityManager.persist(group);
        entityManager.flush();
    }

    private void persistStoreGroupMember(String productStoreId, String productStoreGroupId, Timestamp fromDate) {
        Object member = entities.newEntity(entities.entityClass("ProductStoreGroupMember"));
        entities.set(member, "productStoreId", productStoreId);
        entities.set(member, "productStoreGroupId", productStoreGroupId);
        entities.set(member, "fromDate", fromDate);
        entities.set(member, "sequenceNum", BigDecimal.ONE);
        entityManager.persist(member);
        entityManager.flush();
    }

    private static SimpleGrantedAuthority[] authorities() {
        return java.util.Arrays.stream(new String[] {"OFBTOOLS_VIEW", "CATALOG_VIEW"})
                .map(SimpleGrantedAuthority::new)
                .toArray(SimpleGrantedAuthority[]::new);
    }
}
