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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.ofbiz.migration.common.EntityAutoService;
import org.apache.ofbiz.migration.common.SequenceService;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.apache.ofbiz.migration.marketing.generated.ServiceCatalog;
import org.springframework.stereotype.Component;

/**
 * Registers every marketing entity-auto service generated from the OFBiz service definitions.
 * Deprecated aliases declared with {@code <implements>} inherit the parameters of the implemented service.
 */
@Component
public class MarketingCatalogRegistrar {

    public static final String COMPONENT = "marketing";
    static final String SALES_FORECAST_DETAIL = "createSalesForecastDetail";

    public MarketingCatalogRegistrar(
            ServiceHandlerRegistry registry, EntityAutoService entityAutoService, SequenceService sequences) {
        registry.registerCatalog(COMPONENT, ServiceCatalog.all());
        Map<String, ServiceHandlerRegistry.ServiceDefinition> resolved = new LinkedHashMap<>();
        for (ServiceCatalog.ServiceDefinition generated : ServiceCatalog.all().values()) {
            ServiceHandlerRegistry.ServiceDefinition definition =
                    registry.find(COMPONENT, generated.name()).orElseThrow();
            if (definition.params().isEmpty() && !generated.implementations().isEmpty()) {
                ServiceHandlerRegistry.ServiceDefinition implemented = registry.find(
                        COMPONENT, generated.implementations().get(0).get("service")).orElseThrow();
                definition = new ServiceHandlerRegistry.ServiceDefinition(definition.name(), definition.entity(),
                        definition.invoke(), definition.permissionMainAction(), implemented.params());
            }
            resolved.put(definition.name(), definition);
        }
        registry.registerCatalog(COMPONENT, resolved);
        registerSalesForecastDetailCreate(registry, entityAutoService, sequences, resolved.get(SALES_FORECAST_DETAIL));
    }

    /**
     * OFBiz entity-auto assigns a 5 digit sub-sequence to the single OUT primary key of a multi-key entity.
     * The common engine only does that for *SeqId keys, so salesForecastDetailId is assigned here.
     */
    private static void registerSalesForecastDetailCreate(ServiceHandlerRegistry registry,
            EntityAutoService entityAutoService, SequenceService sequences,
            ServiceHandlerRegistry.ServiceDefinition definition) {
        List<ServiceHandlerRegistry.ParamDefinition> params = definition.params().stream()
                .map(param -> "salesForecastDetailId".equals(param.name())
                        ? new ServiceHandlerRegistry.ParamDefinition(param.name(), "INOUT", false, param.type())
                        : param)
                .toList();
        ServiceHandlerRegistry.ServiceDefinition withKey = new ServiceHandlerRegistry.ServiceDefinition(
                definition.name(), definition.entity(), definition.invoke(), definition.permissionMainAction(), params);
        registry.registerHandler(COMPONENT, SALES_FORECAST_DETAIL, "CREATE", input -> {
            Map<String, Object> values = new HashMap<>(input);
            Object detailId = values.get("salesForecastDetailId");
            if (detailId == null || detailId.toString().isBlank()) {
                values.put("salesForecastDetailId", sequences.nextSubSequence("SalesForecastDetail",
                        Map.of("salesForecastId", String.valueOf(values.get("salesForecastId"))),
                        "salesForecastDetailId"));
            }
            Map<String, Object> result = new LinkedHashMap<>(entityAutoService.execute(withKey, values));
            result.put("salesForecastDetailId", values.get("salesForecastDetailId"));
            return result;
        });
    }
}
