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

import jakarta.annotation.PostConstruct;
import java.beans.Introspector;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationSecurityProperties;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.apache.ofbiz.migration.product.generated.ProductServiceMetadata;
import org.apache.ofbiz.migration.product.generated.ServiceCatalog;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ProductEntityAutoRegistration {
    private final ServiceHandlerRegistry registry;
    private final ProductEntityAutoService entityAutoService;
    private final EntityRegistry entityRegistry;
    private final MigrationSecurityProperties securityProperties;
    private final ProductPermissions productPermissions;
    private final ProductServicePolicy servicePolicy;

    public ProductEntityAutoRegistration(
            ServiceHandlerRegistry registry,
            ProductEntityAutoService entityAutoService,
            EntityRegistry entityRegistry,
            MigrationSecurityProperties securityProperties,
            ProductPermissions productPermissions,
            ProductServicePolicy servicePolicy) {
        this.registry = registry;
        this.entityAutoService = entityAutoService;
        this.entityRegistry = entityRegistry;
        this.securityProperties = securityProperties;
        this.productPermissions = productPermissions;
        this.servicePolicy = servicePolicy;
    }

    @PostConstruct
    public void registerServices() {
        Set<String> extraEntities = new TreeSet<>(securityProperties.getExtraEntities());
        extraEntities.addAll(ProductServiceMetadata.entityModelNames());
        ServiceCatalog.all().values().forEach(definition -> {
            ProductServiceMetadata.ServiceMetadata metadata =
                    ProductServiceMetadata.all().get(definition.name());
            if (metadata == null) {
                throw new IllegalStateException("Missing product service metadata for " + definition.name());
            }
            if (definition.entity().isBlank()) {
                throw new IllegalStateException("Missing default entity for " + definition.name());
            }
            String action = servicePolicy.action(definition);
            String requiredAuthority = servicePolicy.requiredAuthority(metadata, action);
            Class<?> entityType = entityRegistry.entityClass(definition.entity());
            BeanWrapperImpl entityProperties = new BeanWrapperImpl(entityRegistry.newEntity(entityType));
            List<ServiceHandlerRegistry.ParamDefinition> params = definition.params().stream()
                    .filter(param -> entityProperties.isWritableProperty(param.name()))
                    .map(param -> new ServiceHandlerRegistry.ParamDefinition(
                            param.name(), param.mode(), param.optional(), param.type()))
                    .toList();
            ServiceHandlerRegistry.ServiceDefinition handlerDefinition =
                    new ServiceHandlerRegistry.ServiceDefinition(
                            definition.name(), definition.entity(), definition.invoke(),
                            definition.permissionMainAction(), params);
            extraEntities.add(definition.entity());
            registry.registerHandler("product", definition.name(), requiredAuthority,
                    definition.entity(), input -> {
                        servicePolicy.require(productPermissions, requiredAuthority);
                        checkShipmentStatus(metadata.permissionService(), input);
                        if ("productPriceGenericPermission".equals(metadata.permissionService())) {
                            productPermissions.require("CATALOG", action);
                        }
                        Map<String, Object> previousValues = previousOutputValues(definition, entityType, input);
                        Map<String, Object> output = new LinkedHashMap<>(
                                entityAutoService.execute(handlerDefinition, input));
                        output.putAll(previousValues);
                        return output;
                    });
        });
        securityProperties.getExtraEntities().clear();
        securityProperties.getExtraEntities().addAll(extraEntities);
    }

    private Map<String, Object> previousOutputValues(
            ServiceCatalog.ServiceDefinition definition, Class<?> entityType, Map<String, Object> input) {
        if (!"update".equalsIgnoreCase(definition.invoke())) {
            return Map.of();
        }
        List<ServiceCatalog.ParamDefinition> previousValueParams = definition.params().stream()
                .filter(param -> "OUT".equalsIgnoreCase(param.mode()))
                .filter(param -> param.name().startsWith("old") && param.name().length() > 3)
                .filter(param -> new BeanWrapperImpl(entityRegistry.newEntity(entityType))
                        .isReadableProperty(Introspector.decapitalize(param.name().substring(3))))
                .toList();
        if (previousValueParams.isEmpty()) {
            return Map.of();
        }
        Object entity = entityRegistry.findByPrimaryKey(entityType, input);
        if (entity == null) {
            return Map.of();
        }
        Map<String, Object> output = new LinkedHashMap<>();
        previousValueParams.forEach(param -> output.put(
                param.name(), entityRegistry.get(entity, Introspector.decapitalize(param.name().substring(3)))));
        return output;
    }

    void checkShipmentStatus(String permissionService, Map<String, Object> input) {
        String fromStatusId = switch (permissionService == null ? "" : permissionService) {
            case "checkCanChangeShipmentStatusPacked" -> "SHIPMENT_PACKED";
            case "checkCanChangeShipmentStatusShipped" -> "SHIPMENT_SHIPPED";
            case "checkCanChangeShipmentStatusDelivered" -> "SHIPMENT_DELIVERED";
            default -> null;
        };
        if (fromStatusId == null || input.get("shipmentId") == null) {
            return;
        }
        Class<?> shipmentType = entityRegistry.entityClass("Shipment");
        Object shipment = entityRegistry.findByPrimaryKey(
                shipmentType, Map.of("shipmentId", input.get("shipmentId")));
        if (shipment == null) {
            return;
        }
        String currentStatus = String.valueOf(entityRegistry.get(shipment, "statusId"));
        boolean denied = currentStatus.equals("SHIPMENT_CANCELLED")
                || currentStatus.equals("SHIPMENT_PACKED") && fromStatusId.equals("SHIPMENT_PACKED")
                || currentStatus.equals("SHIPMENT_SHIPPED")
                        && Set.of("SHIPMENT_PACKED", "SHIPMENT_SHIPPED").contains(fromStatusId)
                || currentStatus.equals("SHIPMENT_DELIVERED")
                        && Set.of("SHIPMENT_PACKED", "SHIPMENT_SHIPPED", "SHIPMENT_DELIVERED")
                                .contains(fromStatusId);
        if (denied) {
            String description = statusDescription(currentStatus);
            String shipmentId = String.valueOf(input.get("shipmentId"));
            throw new MigrationException(HttpStatus.CONFLICT,
                    "Cannot perform this operation when the shipment [" + shipmentId + "] is in the "
                            + description + " [" + currentStatus + "] status.");
        }
    }

    private String statusDescription(String statusId) {
        try {
            Class<?> statusType = entityRegistry.entityClass("StatusItem");
            Object status = entityRegistry.findByPrimaryKey(statusType, Map.of("statusId", statusId));
            if (status != null) {
                Object description = entityRegistry.get(status, "description");
                if (description != null) {
                    return description.toString();
                }
            }
        } catch (MigrationException ignored) {
            return statusId;
        }
        return statusId;
    }
}
