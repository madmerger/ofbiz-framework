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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.product.generated.ServiceCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(
        classes = ProductMigrationApplication.class,
        properties = {"spring.profiles.active=postgres", "spring.jpa.hibernate.ddl-auto=create-drop"})
@AutoConfigureMockMvc
class PostgresSmokeTest {
    private static final ObjectMapper JSON = new ObjectMapper();

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityRegistry entities;

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry properties) {
        properties.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        properties.add("spring.datasource.username", POSTGRES::getUsername);
        properties.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Test
    void ac01_postgresProfileCreatesAndReadsProductType() throws Exception {
        ServiceCatalog.ServiceDefinition create =
                ProductTestSupport.matching("ProductType", "create", "catalog");
        Map<String, Object> input = ProductTestSupport.input(create, entities, "postgres", false);
        String body = mockMvc.perform(post("/api/product/services/" + create.name())
                        .secure(true)
                        .with(user("admin").authorities(authorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(input)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        Map<String, Object> output = JSON.readValue(body, new com.fasterxml.jackson.core.type.TypeReference<>() { });
        Map<String, Object> keys = ProductTestSupport.resultKeys(create, entities, output);
        String query = keys.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((left, right) -> left + "&" + right).orElseThrow();
        mockMvc.perform(get("/api/product/entities/ProductType/one?" + query)
                        .secure(true)
                        .with(user("admin").authorities(authorities())))
                .andExpect(status().isOk());
        assertNotNull(entities.findByPrimaryKey(entities.entityClass("ProductType"), keys));
    }

    private static GrantedAuthority[] authorities() {
        return java.util.Arrays.stream(new String[] {
            "OFBTOOLS_ADMIN", "CATALOG_ADMIN", "PRODUCT_ADMIN", "CATALOG_CREATE"
        }).map(SimpleGrantedAuthority::new).toArray(GrantedAuthority[]::new);
    }
}
