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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityAutoService;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationSecurityProperties;
import org.apache.ofbiz.migration.product.generated.ProductServiceMetadata;
import org.apache.ofbiz.migration.product.generated.ServiceCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
        classes = ProductMigrationApplication.class,
        properties = "migration.security.require-https=false")
@AutoConfigureMockMvc
class SecurityTest {
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final GrantedAuthority[] ADMIN = authorities(
            "OFBTOOLS_ADMIN", "CATALOG_ADMIN", "FACILITY_ADMIN", "PRODUCT_ADMIN",
            "PRODUCT_SENSITIVE_VIEW", "CATALOG_PRICE_MAINT", "CATALOG_CREATE", "CATALOG_UPDATE",
            "CATALOG_DELETE", "FACILITY_CREATE", "FACILITY_UPDATE", "FACILITY_DELETE");
    private static final GrantedAuthority[] READONLY = authorities(
            "OFBTOOLS_VIEW", "CATALOG_VIEW", "FACILITY_VIEW", "CATALOG_UPDATE", "FACILITY_CREATE");
    private static final GrantedAuthority[] SENSITIVE = authorities(
            "OFBTOOLS_VIEW", "CATALOG_VIEW", "FACILITY_VIEW", "PRODUCT_SENSITIVE_VIEW",
            "FACILITY_CREATE", "FACILITY_UPDATE");

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private EntityRegistry entities;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MigrationSecurityProperties securityProperties;

    @SpyBean
    private EntityAutoService entityAutoService;

    @Test
    void ac01_unauthenticatedApiRequestReturns401() throws Exception {
        mockMvc.perform(get("/api/product/entities/ProductType"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ac02_readonlyCannotCreateAndHandlerIsNotInvoked() throws Exception {
        ServiceCatalog.ServiceDefinition create =
                ProductTestSupport.matching("ProductType", "create", "catalog");
        Map<String, Object> input = ProductTestSupport.input(create, entities, "readonly", false);
        mockMvc.perform(post("/api/product/services/" + create.name())
                        .with(user("readonly").authorities(authorities(
                                "OFBTOOLS_VIEW", "CATALOG_VIEW", "FACILITY_VIEW")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(input)))
                .andExpect(status().isForbidden());
        verify(entityAutoService, never()).execute(any(), anyMap());
    }

    @Test
    void ac03_nofacilityCannotRunFacilityServiceButCanReadCatalogEntity() throws Exception {
        ServiceCatalog.ServiceDefinition create =
                ProductTestSupport.matching("Shipment", "create", "facility");
        Map<String, Object> input = ProductTestSupport.input(create, entities, "no-facility", false);
        mockMvc.perform(post("/api/product/services/" + create.name())
                        .with(user("nofacility").authorities(authorities("OFBTOOLS_VIEW", "CATALOG_VIEW")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(input)))
                .andExpect(status().isForbidden());
        verify(entityAutoService, never()).execute(any(), anyMap());

        mockMvc.perform(get("/api/product/entities/ProductType")
                        .with(user("nofacility").authorities(authorities("OFBTOOLS_VIEW", "CATALOG_VIEW"))))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void ac04_sensitiveEntityRequiresDedicatedPermissionViaEntityAndServiceEndpoints() throws Exception {
        mockMvc.perform(get("/api/product/entities/ShipmentGatewayConfig")
                        .with(user("readonly").authorities(READONLY)))
                .andExpect(status().isForbidden());
        verify(entities, never()).findByEquality(any(), anyMap());

        ServiceCatalog.ServiceDefinition create =
                ProductTestSupport.matching("ShipmentGatewayConfig", "create", "facility");
        Map<String, Object> input = ProductTestSupport.input(create, entities, "sensitive", false);
        mockMvc.perform(post("/api/product/services/" + create.name())
                        .with(user("readonly").authorities(READONLY))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(input)))
                .andExpect(status().isForbidden());
        verify(entityAutoService, never()).execute(any(), anyMap());

        mockMvc.perform(get("/api/product/entities/ShipmentGatewayConfig")
                        .with(user("sensitive").authorities(SENSITIVE)))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/product/services/" + create.name())
                        .with(user("sensitive").authorities(SENSITIVE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(input)))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void ac05_productStoreDefaultPasswordIsMasked() throws Exception {
        Object store = entities.newEntity(entities.entityClass("ProductStore"));
        entities.set(store, "productStoreId", "SECURITY_STORE");
        entities.set(store, "defaultPassword", "do-not-return");
        entityManager.persist(store);
        entityManager.flush();

        mockMvc.perform(get("/api/product/entities/ProductStore/one?productStoreId=SECURITY_STORE")
                        .with(user("admin").authorities(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("do-not-return"))))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("[REDACTED]")));
    }

    @Test
    void ac06_entityApiOnlyExposesProductAndConfiguredExtraEntities() throws Exception {
        mockMvc.perform(get("/api/product/entities/ProductType").with(user("admin").authorities(ADMIN)))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/product/entities/PartyClassification").with(user("admin").authorities(ADMIN)))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/product/entities/Person").with(user("admin").authorities(ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Test
    void ac07_unknownApiComponentIsNotFound() throws Exception {
        mockMvc.perform(get("/api/other/entities/ProductType").with(user("admin").authorities(ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Test
    void ac08_sensitiveEntitiesIncludeTheGatewayConfig() {
        assertTrue(securityProperties.isSensitiveEntity("ShipmentGatewayConfig"));
    }

    @Test
    void ac09_priceMaintenanceRequiresCatalogActionAsWell() throws Exception {
        ServiceCatalog.ServiceDefinition priceService = ServiceCatalog.all().values().stream()
                .filter(definition -> "productPriceGenericPermission".equals(
                        ProductServiceMetadata.all().get(definition.name()).permissionService()))
                .findFirst().orElseThrow();
        Map<String, Object> input = ProductTestSupport.input(priceService, entities, "price-limited", false);
        mockMvc.perform(post("/api/product/services/" + priceService.name())
                        .with(user("price-limited").authorities(authorities(
                                "OFBTOOLS_VIEW", "CATALOG_VIEW", "PRODUCT_ADMIN", "CATALOG_PRICE_MAINT")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(input)))
                .andExpect(status().isForbidden());
        verify(entityAutoService, never()).execute(any(), anyMap());
    }

    @Test
    void ac10_productAdminDoesNotBypassFacilityCreatePermission() throws Exception {
        ServiceCatalog.ServiceDefinition create = ServiceCatalog.all().get("createShipment");
        Map<String, Object> input = ProductTestSupport.input(create, entities, "product-admin", false);
        mockMvc.perform(post("/api/product/services/createShipment")
                        .with(user("product-admin").authorities(authorities(
                                "OFBTOOLS_VIEW", "CATALOG_VIEW", "PRODUCT_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(input)))
                .andExpect(status().isForbidden());
        verify(entityAutoService, never()).execute(any(), anyMap());
    }

    private static GrantedAuthority[] authorities(String... names) {
        return java.util.Arrays.stream(names).map(SimpleGrantedAuthority::new).toArray(GrantedAuthority[]::new);
    }
}
