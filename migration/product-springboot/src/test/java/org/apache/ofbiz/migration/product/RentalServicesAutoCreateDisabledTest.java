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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.persistence.EntityManager;
import java.util.Map;
import java.util.UUID;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(
        classes = ProductMigrationApplication.class,
        properties = {
            "migration.security.require-https=false",
            "accounting.fixedasset.autocreate=N"
        })
@AutoConfigureMockMvc
class RentalServicesAutoCreateDisabledTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private EntityManager entityManager;

    private final TransactionTemplate transactionTemplate;

    @Autowired
    RentalServicesAutoCreateDisabledTest(PlatformTransactionManager transactionManager) {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Test
    void ac01_createFixedAssetAndLinkToProductSkipsCreationWhenAutocreateIsDisabled() throws Exception {
        String productId = "RENT_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        transactionTemplate.executeWithoutResult(status -> {
            Object product = entities.newEntity(entities.entityClass("Product"));
            entities.set(product, "productId", productId);
            entityManager.persist(product);
        });

        mockMvc.perform(post("/api/product/services/createFixedAssetAndLinkToProduct")
                        .with(user("rental").authorities(
                                new SimpleGrantedAuthority("OFBTOOLS_VIEW"),
                                new SimpleGrantedAuthority("CATALOG_VIEW"),
                                new SimpleGrantedAuthority("CATALOG_UPDATE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productId":"%s","productName":"Rental Car"}
                                """.formatted(productId)))
                .andExpect(status().isOk());

        int fixedAssetCount = transactionTemplate.execute(status ->
                entities.findByEquality(entities.entityClass("FixedAsset"), Map.of("instanceOfProductId", productId))
                        .size());
        assertEquals(0, fixedAssetCount);

        int fixedAssetProductCount = transactionTemplate.execute(status ->
                entities.findByEquality(entities.entityClass("FixedAssetProduct"), Map.of("productId", productId))
                        .size());
        assertEquals(0, fixedAssetProductCount);
    }
}
