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
package org.apache.ofbiz.migration.product.flow;

import jakarta.annotation.PostConstruct;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry.SimpleServiceHandler;
import org.apache.ofbiz.migration.product.ProductPermissions;
import org.apache.ofbiz.migration.product.ProductServicePolicy;
import org.apache.ofbiz.migration.product.generated.ProductServiceMetadata;
import org.springframework.stereotype.Component;

@Component
public class ProductFlowServiceRegistration {
    private final ServiceHandlerRegistry registry;
    private final ProductServicePolicy policy;
    private final ProductPermissions permissions;
    private final UpdateShipmentFlow updateShipment;
    private final QuickShipEntireOrderFlow quickShipEntireOrder;
    private final CalculateProductPriceFlow calculateProductPrice;

    public ProductFlowServiceRegistration(
            ServiceHandlerRegistry registry,
            ProductServicePolicy policy,
            ProductPermissions permissions,
            UpdateShipmentFlow updateShipment,
            QuickShipEntireOrderFlow quickShipEntireOrder,
            CalculateProductPriceFlow calculateProductPrice) {
        this.registry = registry;
        this.policy = policy;
        this.permissions = permissions;
        this.updateShipment = updateShipment;
        this.quickShipEntireOrder = quickShipEntireOrder;
        this.calculateProductPrice = calculateProductPrice;
    }

    @PostConstruct
    public void registerServices() {
        register("updateShipment", new ProductServiceMetadata.ServiceMetadata(
                "services_shipment.xml", "facility", "checkCanChangeShipmentStatusDelivered"),
                "UPDATE", null, updateShipment::execute);
        register("quickShipEntireOrder", new ProductServiceMetadata.ServiceMetadata(
                "services_shipment.xml", "facility", ""),
                "UPDATE", null, quickShipEntireOrder::execute);
        registry.registerHandler("product", "calculateProductPrice", "OFBTOOLS_VIEW", null,
                calculateProductPrice::execute);
    }

    private void register(
            String serviceName,
            ProductServiceMetadata.ServiceMetadata metadata,
            String action,
            String sensitiveEntity,
            SimpleServiceHandler handler) {
        String authority = policy.requiredAuthority(metadata, policy.action(serviceName, action));
        registry.registerHandler("product", serviceName, authority, sensitiveEntity, input -> {
            policy.require(permissions, authority);
            return handler.execute(input);
        });
    }
}
