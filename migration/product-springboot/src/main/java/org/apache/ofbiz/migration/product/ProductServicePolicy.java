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

import java.util.Locale;
import java.util.Set;
import org.apache.ofbiz.migration.product.generated.ProductServiceMetadata;
import org.apache.ofbiz.migration.product.generated.ServiceCatalog;
import org.springframework.stereotype.Component;

@Component
public class ProductServicePolicy {
    private static final Set<String> FACILITY_PERMISSION_SERVICES = Set.of(
            "facilityGenericPermission",
            "facilityPermissionCheck",
            "checkProductFacilityRelatedPermission");
    private static final Set<String> CATALOG_PERMISSION_SERVICES = Set.of(
            "productGenericPermission",
            "catalogPermissionCheck",
            "checkProductRelatedPermission",
            "checkCategoryPermissionWithViewPurchaseAllow",
            "prodCatalogToPartyPermissionCheck",
            "subscriptionPermissionCheck",
            "productStoreGenericPermission",
            "genericContentPermission",
            "contentManagerPermission",
            "productCategoryGenericPermission");

    public String action(ServiceCatalog.ServiceDefinition definition) {
        String declaredAction = definition.permissionMainAction();
        if (declaredAction != null && !declaredAction.isBlank()) {
            return declaredAction.toUpperCase(Locale.ROOT);
        }
        return switch (definition.invoke().toLowerCase(Locale.ROOT)) {
            case "create" -> "CREATE";
            case "update", "expire" -> "UPDATE";
            case "delete" -> "DELETE";
            default -> "VIEW";
        };
    }

    public String action(String serviceName, String declaredAction) {
        if (declaredAction != null && !declaredAction.isBlank()) {
            return declaredAction.toUpperCase(Locale.ROOT);
        }
        String normalized = serviceName.toLowerCase(Locale.ROOT);
        return normalized.startsWith("find") || normalized.startsWith("get") ? "VIEW" : "UPDATE";
    }

    public String component(ProductServiceMetadata.ServiceMetadata metadata) {
        String component = ProductServiceMetadata.componentsByFile().get(metadata.serviceFile());
        if (component == null || !component.equals(metadata.component())) {
            throw new IllegalStateException("Missing product component metadata for " + metadata.serviceFile());
        }
        return component;
    }

    public String requiredAuthority(ProductServiceMetadata.ServiceMetadata metadata, String action) {
        String permissionService = metadata.permissionService();
        if (permissionService == null || permissionService.isBlank()) {
            return component(metadata).toUpperCase(Locale.ROOT) + "_" + action;
        }
        if (permissionService.startsWith("checkCanChangeShipmentStatus")) {
            return "FACILITY_" + action;
        }
        if (FACILITY_PERMISSION_SERVICES.contains(permissionService)) {
            return "FACILITY_" + action;
        }
        if (CATALOG_PERMISSION_SERVICES.contains(permissionService)) {
            return "CATALOG_" + action;
        }
        if ("productPriceGenericPermission".equals(permissionService)) {
            return "CATALOG_PRICE_MAINT";
        }
        throw new IllegalStateException("Unknown product permission service: " + permissionService
                + " for " + metadata.serviceFile());
    }

    public String requiredAuthority(ProductServiceMetadata.SimpleServiceMetadata metadata, String action) {
        return requiredAuthority(new ProductServiceMetadata.ServiceMetadata(
                metadata.serviceFile(), metadata.component(), metadata.permissionService()), action);
    }

    public void require(ProductPermissions permissions, String requiredAuthority) {
        int separator = requiredAuthority.lastIndexOf('_');
        if (separator > 0 && Set.of("CREATE", "UPDATE", "DELETE", "VIEW")
                .contains(requiredAuthority.substring(separator + 1))) {
            permissions.require(requiredAuthority.substring(0, separator), requiredAuthority.substring(separator + 1));
        } else {
            permissions.requireAuthority(requiredAuthority);
        }
    }
}
