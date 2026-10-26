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

import jakarta.annotation.PostConstruct;
import java.util.Map;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.apache.ofbiz.migration.product.ProductPermissions;
import org.apache.ofbiz.migration.product.ProductServicePolicy;
import org.apache.ofbiz.migration.product.generated.ProductServiceMetadata;
import org.springframework.stereotype.Component;

@Component
public class ProductSimpleServiceRegistration {
    private final ServiceHandlerRegistry registry;
    private final ProductServicePolicy servicePolicy;
    private final ProductPermissions permissions;
    private final ProductSimpleServiceExecutor executor;
    private final Map<String, SimpleMethodFile> files;

    public ProductSimpleServiceRegistration(
            ServiceHandlerRegistry registry,
            ProductServicePolicy servicePolicy,
            ProductPermissions permissions,
            ProductSimpleServiceExecutor executor,
            InventoryServices inventoryServices,
            FacilityContactMechServices facilityContactMechServices,
            PicklistServices picklistServices,
            InventoryReserveServices inventoryReserveServices,
            IssuanceServices issuanceServices,
            CustomerDigitalDownloadServices customerDigitalDownloadServices,
            StockMoveServices stockMoveServices,
            RentalServices rentalServices,
            PermissionServices permissionServices,
            ProductUomFormulas productUomFormulas) {
        this.registry = registry;
        this.servicePolicy = servicePolicy;
        this.permissions = permissions;
        this.executor = executor;
        files = Map.of(
                "InventoryServices.xml", inventoryServices,
                "FacilityContactMechServices.xml", facilityContactMechServices,
                "PicklistServices.xml", picklistServices,
                "InventoryReserveServices.xml", inventoryReserveServices,
                "IssuanceServices.xml", issuanceServices,
                "CustomerDigitalDownloadServices.xml", customerDigitalDownloadServices,
                "StockMoveServices.xml", stockMoveServices,
                "RentalServices.xml", rentalServices,
                "PermissionServices.xml", permissionServices,
                "ProductUomFormulas.xml", productUomFormulas);
    }

    @PostConstruct
    public void registerServices() {
        ProductServiceMetadata.simpleServices().forEach((serviceName, metadata) -> {
            String methodFileName = metadata.location().substring(metadata.location().lastIndexOf('/') + 1);
            SimpleMethodFile methodFile = files.get(methodFileName);
            if (methodFile == null) {
                throw new IllegalStateException("No simple-method file adapter for " + metadata.location());
            }
            String action = servicePolicy.action(serviceName, metadata.permissionMainAction());
            String requiredAuthority = servicePolicy.requiredAuthority(metadata, action);
            registry.registerHandler(
                    "product",
                    serviceName,
                    requiredAuthority,
                    metadata.defaultEntityName(),
                    input -> {
                        servicePolicy.require(permissions, requiredAuthority);
                        return executor.execute(serviceName, metadata, methodFile, input);
                    });
        });
    }
}
