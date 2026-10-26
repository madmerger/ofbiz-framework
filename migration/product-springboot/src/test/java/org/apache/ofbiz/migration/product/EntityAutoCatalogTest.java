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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationSecurityProperties;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.apache.ofbiz.migration.product.generated.ProductServiceMetadata;
import org.apache.ofbiz.migration.product.generated.ServiceCatalog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = ProductMigrationApplication.class,
        properties = "migration.security.require-https=false")
@AutoConfigureMockMvc
class EntityAutoCatalogTest {
    private static final Set<String> KNOWN_PERMISSION_SERVICES = Set.of(
            "facilityGenericPermission",
            "facilityPermissionCheck",
            "productGenericPermission",
            "catalogPermissionCheck",
            "checkProductRelatedPermission",
            "checkCategoryPermissionWithViewPurchaseAllow",
            "prodCatalogToPartyPermissionCheck",
            "subscriptionPermissionCheck",
            "productStoreGenericPermission",
            "checkProductFacilityRelatedPermission",
            "genericContentPermission",
            "contentManagerPermission",
            "productCategoryGenericPermission",
            "productPriceGenericPermission");

    @Autowired
    private ServiceHandlerRegistry registry;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private MigrationSecurityProperties securityProperties;

    @Test
    void ac01_catalogContainsAll460EntityAutoServicesAnd21ComponentFiles() {
        assertEquals(460, ServiceCatalog.all().size());
        assertEquals(460, ProductServiceMetadata.all().size());
        assertEquals(21, ProductServiceMetadata.componentsByFile().size());
        assertTrue(registry.entityNames("product").isEmpty());
        Set<String> expectedEntities = new HashSet<>(ProductServiceMetadata.entityModelNames());
        ServiceCatalog.all().values().forEach(definition -> expectedEntities.add(definition.entity()));
        assertEquals(expectedEntities, new HashSet<>(securityProperties.getExtraEntities()));
        assertEquals(expectedEntities.size(), securityProperties.getExtraEntities().size());
    }

    @ParameterizedTest(name = "ac02_{0}")
    @MethodSource("entityAutoServices")
    void ac02_eachServiceResolvesItsEntityComponentAndRequiredAuthority(
            ServiceCatalog.ServiceDefinition definition) {
        ProductServiceMetadata.ServiceMetadata metadata =
                ProductServiceMetadata.all().get(definition.name());
        assertNotNull(metadata, definition.name());
        assertEquals(expectedComponent(metadata.serviceFile()),
                ProductServiceMetadata.componentsByFile().get(metadata.serviceFile()));
        assertEquals(metadata.component(), ProductServiceMetadata.componentsByFile().get(metadata.serviceFile()));
        assertNotNull(entities.entityClass(definition.entity()), definition.entity());
        ServiceHandlerRegistry.HandlerRegistration handler =
                registry.handler("product", definition.name()).orElseThrow();
        assertEquals(expectedAuthority(definition, metadata), handler.requiredPermission());
        assertEquals(definition.entity(), handler.sensitiveResultEntity());
    }

    @ParameterizedTest(name = "ac03_{0}")
    @MethodSource("entityAutoServices")
    void ac03_allPermissionServicesAreExplicitlyMapped(
            ServiceCatalog.ServiceDefinition definition) {
        String permissionService = ProductServiceMetadata.all().get(definition.name()).permissionService();
        if (permissionService == null || permissionService.isBlank()) {
            return;
        }
        assertTrue(KNOWN_PERMISSION_SERVICES.contains(permissionService)
                        || permissionService.startsWith("checkCanChangeShipmentStatus"),
                () -> "Unknown permission service " + permissionService + " on " + definition.name());
    }

    private static String expectedComponent(String serviceFile) {
        return Set.of(
                        "services_facility.xml", "services_inventory.xml", "services_picklist.xml",
                        "services_shipment.xml", "services_shipmentgateway.xml", "services_perm.xml",
                        "services_maint.xml")
                .contains(serviceFile) ? "facility" : "catalog";
    }

    private static String expectedAuthority(
            ServiceCatalog.ServiceDefinition definition, ProductServiceMetadata.ServiceMetadata metadata) {
        String permissionService = metadata.permissionService();
        String action = definition.permissionMainAction();
        if (action == null || action.isBlank()) {
            action = switch (definition.invoke().toLowerCase(Locale.ROOT)) {
                case "create" -> "CREATE";
                case "update", "expire" -> "UPDATE";
                case "delete" -> "DELETE";
                default -> "VIEW";
            };
        }
        if ("productPriceGenericPermission".equals(permissionService)) {
            return "CATALOG_PRICE_MAINT";
        }
        boolean facilityPermission = permissionService != null
                && (permissionService.startsWith("checkCanChangeShipmentStatus")
                        || Set.of("facilityGenericPermission", "facilityPermissionCheck",
                                "checkProductFacilityRelatedPermission").contains(permissionService));
        String component = facilityPermission ? "FACILITY" : metadata.component().toUpperCase(Locale.ROOT);
        return component + "_" + action.toUpperCase(Locale.ROOT);
    }

    private static Stream<Arguments> entityAutoServices() {
        return ServiceCatalog.all().values().stream().map(Arguments::of);
    }
}
