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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.product.generated.ProductServiceMetadata;
import org.apache.ofbiz.migration.product.generated.ServiceCatalog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
        classes = ProductMigrationApplication.class,
        properties = "migration.security.require-https=false")
@AutoConfigureMockMvc
class EntityAutoAcceptanceTest {
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final String[] ADMIN_AUTHORITIES = {
        "OFBTOOLS_ADMIN", "CATALOG_ADMIN", "FACILITY_ADMIN", "PRODUCT_ADMIN",
        "PRODUCT_SENSITIVE_VIEW", "CATALOG_PRICE_MAINT", "CATALOG_CREATE", "CATALOG_UPDATE",
        "CATALOG_DELETE", "FACILITY_CREATE", "FACILITY_UPDATE", "FACILITY_DELETE"
    };
    private static final Set<String> MANUAL_SEED_SERVICES = Set.of(
            "removeProductContent", "updateProductCategoryLink", "deleteProductCategoryLink",
            "removeCategoryContent", "removeProductConfigItemContent", "updateFacilityLocation",
            "deleteFacilityLocation", "updateInventoryItemLabelAppl", "deleteInventoryItemLabelAppl",
            "updateProductFeatureType", "removeProductFeatureType", "updateProductFeatureApplAttr",
            "removeProductFeatureApplAttr", "deleteProductPriceCond", "deleteProductPromoCond",
            "updateItemIssuance", "deleteItemIssuance", "updateShipmentReceipt",
            "deleteShipmentReceipt", "updateSubscription");
    private static final Set<String> KEY_ONLY_UPDATE_SERVICES = Set.of("updateShipmentItemBilling");

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @ParameterizedTest(name = "ac01_{0}")
    @MethodSource("entityAutoServices")
    @WithMockUser(username = "admin", authorities = {
        "OFBTOOLS_ADMIN", "CATALOG_ADMIN", "FACILITY_ADMIN", "PRODUCT_ADMIN",
        "PRODUCT_SENSITIVE_VIEW", "CATALOG_PRICE_MAINT", "CATALOG_CREATE", "CATALOG_UPDATE",
        "CATALOG_DELETE", "FACILITY_CREATE", "FACILITY_UPDATE", "FACILITY_DELETE"
    })
    @Transactional
    void ac01_eachEntityAutoServiceExecutesThroughItsHttpEndpoint(
            ServiceCatalog.ServiceDefinition definition) throws Exception {
        String sourceComponent = ProductServiceMetadata.all().get(definition.name()).component();
        Map<String, Object> targetInput = ProductTestSupport.input(
                definition, entities, definition.name() + "T", false);

        if ("create".equalsIgnoreCase(definition.invoke())) {
            List<String> generatedKeys = ProductTestSupport.generatedPrimaryKeys(
                    definition, entities, targetInput);
            String body = postService(definition.name(), targetInput)
                    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
            Map<String, Object> output = JSON.readValue(body, new TypeReference<>() { });
            generatedKeys.forEach(key ->
                    assertNotNull(output.get(key), "Expected generated primary key " + key));
            Map<String, Object> keys = ProductTestSupport.resultKeys(definition, entities, output);
            assertNotNull(entities.findByPrimaryKey(entities.entityClass(definition.entity()), keys));
            return;
        }

        ServiceCatalog.ServiceDefinition create = createService(definition, sourceComponent);
        Map<String, Object> primaryKeys;
        if (create == null) {
            assertTrue(MANUAL_SEED_SERVICES.contains(definition.name()),
                    "Unexpected missing entity-auto create service for " + definition.name());
            Map<String, Object> seedInput = ProductTestSupport.input(
                    definition, entities, definition.name() + "C", true);
            seedEntity(definition.entity(), seedInput);
            primaryKeys = ProductTestSupport.keys(definition, entities, seedInput);
        } else {
            Map<String, Object> createInput = ProductTestSupport.input(
                    create, entities, definition.name() + "C", false);
            seedRequiredParent(create, createInput);
            List<String> generatedKeys = ProductTestSupport.generatedPrimaryKeys(create, entities, createInput);
            String body = postService(create.name(), createInput)
                    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
            Map<String, Object> created = JSON.readValue(body, new TypeReference<>() { });
            generatedKeys.forEach(key ->
                    assertNotNull(created.get(key), "Expected generated primary key " + key));
            primaryKeys = ProductTestSupport.resultKeys(create, entities, created);
        }

        targetInput.putAll(primaryKeys);
        switch (definition.invoke().toLowerCase()) {
            case "update" -> {
                Map<String, Object> updateInput = ProductTestSupport.input(
                        definition, entities, definition.name() + "U", true);
                updateInput.putAll(primaryKeys);
                postService(definition.name(), updateInput).andExpect(status().isOk());
                Object current = entities.findByPrimaryKey(entities.entityClass(definition.entity()), primaryKeys);
                assertNotNull(current);
                List<String> updatedFields = updateInput.keySet().stream()
                        .filter(key -> !primaryKeys.containsKey(key))
                        .filter(key -> new BeanWrapperImpl(current).isWritableProperty(key))
                        .toList();
                if (updatedFields.isEmpty()) {
                    assertTrue(KEY_ONLY_UPDATE_SERVICES.contains(definition.name()),
                            "Unexpected update without a writable non-key input: " + definition.name());
                } else {
                    String updatedField = updatedFields.get(0);
                    Object converted = entities.newEntity(entities.entityClass(definition.entity()));
                    entities.set(converted, updatedField, updateInput.get(updatedField));
                    assertEquals(entities.get(converted, updatedField),
                            entities.get(current, updatedField), "Update did not persist " + updatedField);
                }
            }
            case "expire" -> {
                targetInput.putAll(primaryKeys);
                targetInput.put("thruDate", "2020-01-02 03:04:05");
                postService(definition.name(), targetInput).andExpect(status().isOk());
                Object current = entities.findByPrimaryKey(entities.entityClass(definition.entity()), primaryKeys);
                assertNotNull(current);
                assertNotNull(entities.get(current, "thruDate"));
            }
            case "delete" -> {
                postService(definition.name(), primaryKeys).andExpect(status().isOk());
                assertNull(entities.findByPrimaryKey(entities.entityClass(definition.entity()), primaryKeys));
            }
            default -> throw new AssertionError("Unexpected entity-auto action " + definition.invoke());
        }
    }

    @Test
    @WithMockUser(username = "admin", authorities = {
        "OFBTOOLS_ADMIN", "CATALOG_ADMIN", "FACILITY_ADMIN", "PRODUCT_ADMIN",
        "PRODUCT_SENSITIVE_VIEW", "CATALOG_PRICE_MAINT", "CATALOG_CREATE", "CATALOG_UPDATE",
        "CATALOG_DELETE", "FACILITY_CREATE", "FACILITY_UPDATE", "FACILITY_DELETE"
    })
    @Transactional
    void ac02_productTypeSupportsCreateReadUpdateDeleteOverHttp() throws Exception {
        ServiceCatalog.ServiceDefinition create = ProductTestSupport.matching("ProductType", "create", "catalog");
        ServiceCatalog.ServiceDefinition update = ProductTestSupport.matching("ProductType", "update", "catalog");
        ServiceCatalog.ServiceDefinition delete = ProductTestSupport.matching("ProductType", "delete", "catalog");
        Map<String, Object> input = ProductTestSupport.input(create, entities, "crud1", false);
        String body = postService(create.name(), input)
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Map<String, Object> output = JSON.readValue(body, new TypeReference<>() { });
        Map<String, Object> keys = ProductTestSupport.resultKeys(create, entities, output);

        String query = keys.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((left, right) -> left + "&" + right).orElseThrow();
        mockMvc.perform(get("/api/product/entities/ProductType/one?" + query)
                        .with(user("admin").authorities(authorities())))
                .andExpect(status().isOk());

        Map<String, Object> updateInput = new HashMap<>(keys);
        updateInput.put("description", "round-trip-updated");
        postService(update.name(), updateInput).andExpect(status().isOk());
        postService(delete.name(), keys).andExpect(status().isOk());
        assertNull(entities.findByPrimaryKey(entities.entityClass("ProductType"), keys));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {
        "OFBTOOLS_ADMIN", "CATALOG_ADMIN", "FACILITY_ADMIN", "PRODUCT_ADMIN",
        "PRODUCT_SENSITIVE_VIEW", "CATALOG_PRICE_MAINT", "CATALOG_CREATE", "CATALOG_UPDATE",
        "CATALOG_DELETE", "FACILITY_CREATE", "FACILITY_UPDATE", "FACILITY_DELETE"
    })
    @Transactional
    void ac03_createWithOutPrimaryKeyIgnoresCallerValue() throws Exception {
        ServiceCatalog.ServiceDefinition create = ServiceCatalog.all().get("createProductFeatureGroup");
        String suppliedKey = "caller-supplied-feature-group";
        Map<String, Object> input = ProductTestSupport.input(create, entities, "out-pk", false);
        input.put("productFeatureGroupId", suppliedKey);

        String body = postService(create.name(), input)
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Map<String, Object> output = JSON.readValue(body, new TypeReference<>() { });
        Map<String, Object> generatedKeys = ProductTestSupport.resultKeys(create, entities, output);

        assertNotNull(output.get("productFeatureGroupId"));
        assertNotEquals(suppliedKey, output.get("productFeatureGroupId"));
        assertNotNull(entities.findByPrimaryKey(entities.entityClass(create.entity()), generatedKeys));
        assertNull(entities.findByPrimaryKey(
                entities.entityClass(create.entity()), Map.of("productFeatureGroupId", suppliedKey)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {
        "OFBTOOLS_ADMIN", "CATALOG_ADMIN", "FACILITY_ADMIN", "PRODUCT_ADMIN",
        "PRODUCT_SENSITIVE_VIEW", "CATALOG_PRICE_MAINT", "CATALOG_CREATE", "CATALOG_UPDATE",
        "CATALOG_DELETE", "FACILITY_CREATE", "FACILITY_UPDATE", "FACILITY_DELETE"
    })
    @Transactional
    void ac04_updateShipmentItemBillingHasNoWritableNonKeyFields() throws Exception {
        ServiceCatalog.ServiceDefinition update = ServiceCatalog.all().get("updateShipmentItemBilling");
        Map<String, Object> input = ProductTestSupport.input(
                update, entities, "shipment-item-billing", true);
        Set<String> inputFields = update.params().stream()
                .map(ServiceCatalog.ParamDefinition::name)
                .collect(java.util.stream.Collectors.toSet());
        Set<String> primaryKeyFields = Set.copyOf(
                entities.primaryKeys(entities.entityClass(update.entity())));
        assertEquals(primaryKeyFields, inputFields);

        seedEntity(update.entity(), input);
        Map<String, Object> keys = ProductTestSupport.keys(update, entities, input);
        Map<String, Object> before = entities.toMap(
                entities.findByPrimaryKey(entities.entityClass(update.entity()), keys));
        postService(update.name(), input).andExpect(status().isOk());
        Object after = entities.findByPrimaryKey(entities.entityClass(update.entity()), keys);
        assertNotNull(after);
        assertEquals(before, entities.toMap(after));
    }

    private org.springframework.test.web.servlet.ResultActions postService(
            String service, Map<String, Object> input) throws Exception {
        return mockMvc.perform(post("/api/product/services/" + service)
                .with(user("admin").authorities(authorities()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.writeValueAsBytes(input)));
    }

    private static ServiceCatalog.ServiceDefinition createService(
            ServiceCatalog.ServiceDefinition target, String sourceComponent) {
        return ServiceCatalog.all().values().stream()
                .filter(definition -> "create".equalsIgnoreCase(definition.invoke()))
                .filter(definition -> target.entity().equals(definition.entity()))
                .filter(definition -> ProductServiceMetadata.all().get(definition.name()).component()
                        .equals(sourceComponent))
                .findFirst()
                .orElseGet(() -> ServiceCatalog.all().values().stream()
                        .filter(candidate -> "create".equalsIgnoreCase(candidate.invoke()))
                        .filter(candidate -> target.entity().equals(candidate.entity()))
                        .findFirst().orElse(null));
    }

    private void seedRequiredParent(
            ServiceCatalog.ServiceDefinition create, Map<String, Object> input) throws Exception {
        String parentEntity = switch (create.entity()) {
            case "ProductConfigOption" -> "ProductConfigItem";
            case "ProductPromoRule" -> "ProductPromo";
            default -> null;
        };
        if (parentEntity == null) {
            return;
        }
        ServiceCatalog.ServiceDefinition parent = ProductTestSupport.matching(parentEntity, "create", "catalog");
        Map<String, Object> parentInput = ProductTestSupport.input(
                parent, entities, create.name() + "Parent", false);
        String body = postService(parent.name(), parentInput)
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Map<String, Object> parentOutput = JSON.readValue(body, new TypeReference<>() { });
        Map<String, Object> parentKeys = ProductTestSupport.resultKeys(parent, entities, parentOutput);
        if ("ProductConfigOption".equals(create.entity())) {
            input.put("configItemId", parentKeys.get("configItemId"));
        } else {
            input.put("productPromoId", parentKeys.get("productPromoId"));
        }
    }

    private void seedEntity(String entityName, Map<String, Object> values) {
        Object entity = entities.newEntity(entities.entityClass(entityName));
        values.forEach((name, value) -> entities.set(entity, name, value));
        entityManager.persist(entity);
        entityManager.flush();
    }

    private static org.springframework.security.core.GrantedAuthority[] authorities() {
        return Stream.of(ADMIN_AUTHORITIES)
                .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                .toArray(org.springframework.security.core.GrantedAuthority[]::new);
    }

    private static Stream<Arguments> entityAutoServices() {
        return ServiceCatalog.all().values().stream().map(Arguments::of);
    }
}
