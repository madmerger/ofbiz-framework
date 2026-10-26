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
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.apache.ofbiz.migration.product.simple;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityAutoService;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry.ParamDefinition;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry.ServiceDefinition;
import org.apache.ofbiz.migration.product.ProductPermissions;
import org.apache.ofbiz.migration.product.adapter.ProductUnportedPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Port of applications/product/minilang/product/rental/RentalServices.xml:24-67.
 *
 * <p>The accounting entity-auto services {@code createFixedAsset} and {@code addFixedAssetProduct}
 * (applications/accounting/servicedef/services_fixedasset.xml:28-34, 44-50) are reproduced inline through the
 * common {@link EntityAutoService}, including their {@code fixedAssetPermissionCheck} permission service
 * (accounting basePermissionCheck: {@code ACCOUNTING_<mainAction>} or {@code ACCOUNTING_ADMIN}), which the OFBiz
 * service engine also evaluates for nested {@code call-service} invocations.
 */
@Component
public class RentalServices extends UnportedSimpleMethodFile {
    static final String CATALOG_UPDATE_PERMISSION_ERROR =
            "Security Error: to run  you must have the CATALOG_UPDATE or CATALOG_ADMIN permission";
    static final String CATALOG_VIEW_PERMISSION_ERROR =
            "You do not have permission to view this page. (\"CATALOG_VIEW\" or \"CATALOG_ADMIN\" needed)";

    private static final ServiceDefinition CREATE_FIXED_ASSET = new ServiceDefinition(
            "createFixedAsset", "FixedAsset", "create", "CREATE", List.of(
                    new ParamDefinition("fixedAssetId", "INOUT", true, "id"),
                    new ParamDefinition("fixedAssetTypeId", "IN", false, "id"),
                    new ParamDefinition("fixedAssetName", "IN", true, "name"),
                    new ParamDefinition("instanceOfProductId", "IN", true, "id")));
    private static final ServiceDefinition ADD_FIXED_ASSET_PRODUCT = new ServiceDefinition(
            "addFixedAssetProduct", "FixedAssetProduct", "create", "CREATE", List.of(
                    new ParamDefinition("fixedAssetId", "IN", false, "id"),
                    new ParamDefinition("productId", "IN", false, "id"),
                    new ParamDefinition("fixedAssetProductTypeId", "IN", false, "id"),
                    new ParamDefinition("fromDate", "IN", true, "date-time")));

    private final EntityRegistry entities;
    private final EntityAutoService entityAutoService;
    private final ProductPermissions permissions;
    private final String fixedAssetAutoCreate;

    public RentalServices(
            ProductUnportedPort unportedPort,
            EntityRegistry entities,
            EntityAutoService entityAutoService,
            ProductPermissions permissions,
            @Value("${accounting.fixedasset.autocreate:Y}") String fixedAssetAutoCreate) {
        super(unportedPort);
        this.entities = entities;
        this.entityAutoService = entityAutoService;
        this.permissions = permissions;
        this.fixedAssetAutoCreate = fixedAssetAutoCreate;
    }

    @Override
    public Map<String, Object> execute(String serviceName, String methodName, Map<String, Object> input) {
        return switch (methodName) {
            case "createFixedAssetAndLinkToProduct" -> createFixedAssetAndLinkToProduct(input);
            case "getProductFirstRelatedFixedAsset" -> getProductFirstRelatedFixedAsset(input);
            default -> super.execute(serviceName, methodName, input);
        };
    }

    /**
     * Port of RentalServices.xml:24-50 ({@code createFixedAssetAndLinkToProduct}).
     *
     * <p>{@code accounting.fixedasset.autocreate} comes from accounting.properties (default {@code Y}); any other
     * value returns success without effects. The method returns no result fields.
     */
    private Map<String, Object> createFixedAssetAndLinkToProduct(Map<String, Object> parameters) {
        if (!"Y".equals(fixedAssetAutoCreate)) {
            return new LinkedHashMap<>();
        }
        if (!permissions.hasPermission("CATALOG", "UPDATE")) {
            throw new MigrationException(HttpStatus.FORBIDDEN, CATALOG_UPDATE_PERMISSION_ERROR);
        }

        Map<String, Object> createFixedAsset = new LinkedHashMap<>();
        createFixedAsset.put("fixedAssetTypeId", "PROPERTY");
        if (notEmpty(parameters.get("productName"))) {
            createFixedAsset.put("fixedAssetName", parameters.get("productName"));
        }
        if (notEmpty(parameters.get("internalName"))) {
            createFixedAsset.put("fixedAssetName", parameters.get("internalName"));
        }
        createFixedAsset.put("instanceOfProductId", parameters.get("productId"));
        Map<String, Object> createFixedAssetResult = callAccountingEntityAuto(CREATE_FIXED_ASSET, createFixedAsset);

        Map<String, Object> newLink = new LinkedHashMap<>();
        newLink.put("fixedAssetId", createFixedAssetResult.get("fixedAssetId"));
        newLink.put("fixedAssetProductTypeId", "FAPT_USE");
        newLink.put("productId", parameters.get("productId"));
        callAccountingEntityAuto(ADD_FIXED_ASSET_PRODUCT, newLink);
        return new LinkedHashMap<>();
    }

    /**
     * Port of RentalServices.xml:52-65 ({@code getProductFirstRelatedFixedAsset}).
     *
     * <p>{@code get-related} on a missing Product raises "Entity value not found" in minilang; it is mapped to 404.
     * The relation is read without date filtering or ordering, as in the minilang.
     */
    private Map<String, Object> getProductFirstRelatedFixedAsset(Map<String, Object> parameters) {
        if (!permissions.hasPermission("CATALOG", "VIEW")) {
            throw new MigrationException(HttpStatus.FORBIDDEN, CATALOG_VIEW_PERMISSION_ERROR);
        }
        Object productId = parameters.get("productId");
        Object product = entities.findByPrimaryKey(entities.entityClass("Product"), Map.of("productId", productId));
        if (product == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "Entity value not found with name: product");
        }
        List<?> productFixedAssets = entities.findByEquality(
                entities.entityClass("FixedAssetProduct"), Map.of("productId", productId));
        Map<String, Object> result = new LinkedHashMap<>();
        if (!productFixedAssets.isEmpty()) {
            result.put("fixedAssetId", entities.get(productFixedAssets.get(0), "fixedAssetId"));
        }
        result.put("productId", productId);
        return result;
    }

    /**
     * Inline equivalent of calling an accounting entity-auto service guarded by {@code fixedAssetPermissionCheck}
     * (applications/accounting/minilang/permissions/PermissionServices.xml:27-30).
     */
    private Map<String, Object> callAccountingEntityAuto(ServiceDefinition definition, Map<String, Object> input) {
        if (!permissions.hasPermission("ACCOUNTING", definition.permissionMainAction())) {
            throw new MigrationException(HttpStatus.FORBIDDEN, "You haven't the permission for the service "
                    + definition.name() + ", reason : Security Error: To run this operation you must have the one of "
                    + "the following permissions: ACCOUNTING_" + definition.permissionMainAction()
                    + ", ACCOUNTING_ADMIN");
        }
        return entityAutoService.execute(definition, input);
    }

    private static boolean notEmpty(Object value) {
        return value != null && !value.toString().isEmpty();
    }
}
