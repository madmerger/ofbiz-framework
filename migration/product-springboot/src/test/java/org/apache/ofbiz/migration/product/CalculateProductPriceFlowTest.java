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
                .andExpect(jsonPath("$.currencyUsed").value("USD"));
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

    private void persistProduct(String productId) {
        Object product = entities.newEntity(entities.entityClass("Product"));
        entities.set(product, "productId", productId);
        entities.set(product, "productTypeId", "FINISHED_GOOD");
        entities.set(product, "isVirtual", "N");
        entityManager.persist(product);
        entityManager.flush();
    }

    private void persistPrice(String productId, String type, BigDecimal amount, Timestamp fromDate) {
        Object price = entities.newEntity(entities.entityClass("ProductPrice"));
        entities.set(price, "productId", productId);
        entities.set(price, "productPriceTypeId", type);
        entities.set(price, "productPricePurposeId", "PURCHASE");
        entities.set(price, "currencyUomId", "USD");
        entities.set(price, "productStoreGroupId", "_NA_");
        entities.set(price, "fromDate", fromDate);
        entities.set(price, "price", amount);
        entityManager.persist(price);
        entityManager.flush();
    }

    private static SimpleGrantedAuthority[] authorities() {
        return java.util.Arrays.stream(new String[] {"OFBTOOLS_VIEW", "CATALOG_VIEW"})
                .map(SimpleGrantedAuthority::new)
                .toArray(SimpleGrantedAuthority[]::new);
    }
}
