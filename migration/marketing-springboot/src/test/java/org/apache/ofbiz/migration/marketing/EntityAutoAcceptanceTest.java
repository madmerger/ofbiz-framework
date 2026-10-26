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
package org.apache.ofbiz.migration.marketing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.apache.ofbiz.migration.marketing.generated.ServiceCatalog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(classes = MarketingMigrationApplication.class,
        properties = MarketingTestSupport.TEST_PROPERTIES)
@AutoConfigureMockMvc
class EntityAutoAcceptanceTest {

    private static final Timestamp KEY_DATE = Timestamp.valueOf("2026-01-01 00:00:00");

    @Autowired
    private MigrationServiceDispatcher dispatcher;
    @Autowired
    private ServiceHandlerRegistry registry;
    @Autowired
    private EntityRegistry entityRegistry;
    @Autowired
    private TransactionTemplate transactions;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void logout() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void ac01_allInventoryEntitiesAreMapped() throws IOException {
        JsonNode entities = MarketingTestSupport.inventory().get("entities");
        assertEquals(32, entities.size());
        for (JsonNode entity : entities) {
            assertNotNull(entityRegistry.entityClass(entity.get("name").asText()));
        }
    }

    @Test
    void ac02_catalogContainsEveryEntityAutoServiceOfInventory() throws IOException {
        List<String> expected = new ArrayList<>();
        for (JsonNode service : MarketingTestSupport.inventory().get("services")) {
            if ("entity-auto".equals(service.path("engine").asText())) {
                expected.add(service.path("name").asText());
            }
        }
        assertEquals(73, expected.size());
        assertEquals(Set.copyOf(expected), ServiceCatalog.all().keySet());
        for (String name : expected) {
            assertTrue(registry.find(MarketingCatalogRegistrar.COMPONENT, name).isPresent(), name);
        }
    }

    @ParameterizedTest(name = "ac03_catalog_{0}")
    @MethodSource("entityAutoServices")
    void ac03_generatedCatalogMatchesServiceDefinition(
            String name, String entity, String invoke, String permissionAction) {
        ServiceCatalog.ServiceDefinition generated = ServiceCatalog.find(name).orElseThrow();
        assertEquals(entity, generated.entity());
        assertEquals(invoke.toLowerCase(), generated.invoke());
        assertEquals(permissionAction, generated.permissionMainAction());
    }

    @ParameterizedTest(name = "ac04_execute_{0}")
    @MethodSource("entityAutoServices")
    void ac04_everyEntityAutoServiceExecutes(String name, String entity, String invoke, String permissionAction) {
        MarketingTestSupport.loginAdmin();
        ServiceHandlerRegistry.ServiceDefinition definition =
                registry.find(MarketingCatalogRegistrar.COMPONENT, name).orElseThrow();
        Class<?> type = entityRegistry.entityClass(entity);
        Map<String, Object> keys = keyValues(type, name);
        if ("create".equalsIgnoreCase(invoke)) {
            Map<String, Object> input = new HashMap<>(keys);
            input.putAll(requiredNonKeyValues(definition, type));
            Map<String, Object> result = MarketingTestSupport.run(dispatcher, name, input);
            Map<String, Object> created = new HashMap<>(keys);
            for (String key : entityRegistry.primaryKeys(type)) {
                if (result.get(key) != null) {
                    created.put(key, result.get(key));
                }
            }
            assertNotNull(entityRegistry.findByPrimaryKey(type, created), name);
            return;
        }
        transactions.executeWithoutResult(status -> {
            Object row = entityRegistry.newEntity(type);
            keys.forEach((field, value) -> entityRegistry.set(row, field, value));
            entityManager.persist(row);
        });
        Map<String, Object> input = new HashMap<>(keys);
        String field = null;
        if ("update".equalsIgnoreCase(invoke)) {
            field = updatableField(definition, type);
            if (field != null) {
                input.put(field, "Y");
            }
        }
        MarketingTestSupport.run(dispatcher, name, input);
        Object after = entityRegistry.findByPrimaryKey(type, keys);
        switch (invoke.toLowerCase()) {
            case "delete" -> assertNull(after, name);
            case "expire" -> assertNotNull(entityRegistry.get(after, "thruDate"), name);
            default -> {
                assertNotNull(after, name);
                if (field != null) {
                    assertEquals("Y", entityRegistry.get(after, field), name);
                }
            }
        }
    }

    @Test
    void ac05_crudOverHttpForMarketingCampaign() throws Exception {
        String id = JsonPath.read(mockMvc.perform(post("/api/marketing/services/createMarketingCampaign")
                        .with(httpBasic("admin", "admin"))
                        .contentType("application/json")
                        .content("{\"campaignName\":\"秋キャンペーン\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(), "$.marketingCampaignId");
        mockMvc.perform(get("/api/marketing/entities/MarketingCampaign/one")
                        .with(httpBasic("readonly", "readonly"))
                        .param("marketingCampaignId", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.campaignName").value("秋キャンペーン"));
        mockMvc.perform(post("/api/marketing/services/updateMarketingCampaign")
                        .with(httpBasic("marketer", "marketer"))
                        .contentType("application/json")
                        .content("{\"marketingCampaignId\":\"" + id + "\",\"campaignName\":\"冬キャンペーン\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/marketing/entities/MarketingCampaign")
                        .with(httpBasic("readonly", "readonly"))
                        .param("marketingCampaignId", id))
                .andExpect(jsonPath("$[0].campaignName").value("冬キャンペーン"));
        mockMvc.perform(post("/api/marketing/services/deleteMarketingCampaign")
                        .with(httpBasic("marketer", "marketer"))
                        .contentType("application/json")
                        .content("{\"marketingCampaignId\":\"" + id + "\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/marketing/entities/MarketingCampaign")
                        .with(httpBasic("readonly", "readonly"))
                        .param("marketingCampaignId", id))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void ac06_updateRejectsUndeclaredField() throws Exception {
        String id = JsonPath.read(mockMvc.perform(post("/api/marketing/services/createMarketingCampaign")
                        .with(httpBasic("admin", "admin"))
                        .contentType("application/json")
                        .content("{\"campaignName\":\"AC06\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(), "$.marketingCampaignId");
        mockMvc.perform(post("/api/marketing/services/updateMarketingCampaign")
                        .with(httpBasic("admin", "admin"))
                        .contentType("application/json")
                        .content("{\"marketingCampaignId\":\"" + id + "\",\"notAField\":\"x\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void ac07_localProfileTakesPasswordFromEnvironmentOnly() throws IOException {
        Path root = Path.of(System.getProperty("ofbiz.root"), "migration/marketing-springboot");
        String local = Files.readString(root.resolve("src/main/resources/application-local.yml"));
        assertFalse(local.contains("password:"));
        assertTrue(local.contains("require-https: false"));
        assertTrue(Files.readString(root.resolve("run-local.sh")).contains("MIGRATION_DEMO_PASSWORD"));
        String postgres = Files.readString(root.resolve("src/main/resources/application-postgres.yml"));
        assertTrue(postgres.contains("require-https: true"));
    }

    static Stream<Arguments> entityAutoServices() throws IOException {
        List<Arguments> arguments = new ArrayList<>();
        for (JsonNode service : MarketingTestSupport.inventory().get("services")) {
            if (!"entity-auto".equals(service.path("engine").asText())) {
                continue;
            }
            JsonNode permission = service.path("permission_service");
            String action = permission.isMissingNode() || permission.isNull()
                    ? null : permission.path("main_action").asText(null);
            arguments.add(Arguments.of(service.path("name").asText(), service.path("default_entity_name").asText(),
                    service.path("invoke").asText(), action));
        }
        return arguments.stream();
    }

    private Map<String, Object> keyValues(Class<?> type, String service) {
        BeanWrapperImpl wrapper = new BeanWrapperImpl(entityRegistry.newEntity(type));
        String suffix = Integer.toString(Math.abs(service.hashCode()) % 100000);
        Map<String, Object> keys = new HashMap<>();
        int index = 0;
        for (String key : entityRegistry.primaryKeys(type)) {
            Class<?> keyType = wrapper.getPropertyType(key);
            if (keyType == Timestamp.class) {
                keys.put(key, KEY_DATE);
            } else if (keyType == Long.class) {
                keys.put(key, 1L + index);
            } else if (keyType == BigDecimal.class) {
                keys.put(key, BigDecimal.valueOf(1L + index));
            } else {
                keys.put(key, "E" + index + suffix);
            }
            index++;
        }
        return keys;
    }

    private Map<String, Object> requiredNonKeyValues(ServiceHandlerRegistry.ServiceDefinition definition, Class<?> type) {
        BeanWrapperImpl wrapper = new BeanWrapperImpl(entityRegistry.newEntity(type));
        List<String> keys = entityRegistry.primaryKeys(type);
        Map<String, Object> values = new HashMap<>();
        for (ServiceHandlerRegistry.ParamDefinition param : definition.params()) {
            if (param.optional() || keys.contains(param.name()) || "OUT".equalsIgnoreCase(param.mode())) {
                continue;
            }
            Class<?> fieldType = wrapper.getPropertyType(param.name());
            values.put(param.name(), fieldType == Timestamp.class ? KEY_DATE
                    : fieldType == BigDecimal.class ? BigDecimal.ONE : fieldType == Long.class ? 1L : "REQ");
        }
        return values;
    }

    private String updatableField(ServiceHandlerRegistry.ServiceDefinition definition, Class<?> type) {
        BeanWrapperImpl wrapper = new BeanWrapperImpl(entityRegistry.newEntity(type));
        List<String> keys = entityRegistry.primaryKeys(type);
        List<String> candidates = definition.params().stream()
                .filter(param -> "IN".equalsIgnoreCase(param.mode()))
                .map(ServiceHandlerRegistry.ParamDefinition::name)
                .filter(name -> !keys.contains(name) && !"statusId".equals(name))
                .filter(name -> wrapper.isWritableProperty(name) && wrapper.getPropertyType(name) == String.class)
                .toList();
        for (String preferred : List.of("description", "comments", "campaignName")) {
            if (candidates.contains(preferred)) {
                return preferred;
            }
        }
        return candidates.isEmpty() ? null : candidates.get(0);
    }
}
