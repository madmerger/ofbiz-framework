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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ofbiz.migration.common.EntityAutoService;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.SequenceService;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.springframework.stereotype.Component;

@Component
final class ProductEntityAutoService {
    private final EntityAutoService delegate;
    private final EntityRegistry entities;
    private final SequenceService sequences;

    ProductEntityAutoService(
            EntityAutoService delegate, EntityRegistry entities, SequenceService sequences) {
        this.delegate = delegate;
        this.entities = entities;
        this.sequences = sequences;
    }

    Map<String, Object> execute(
            ServiceHandlerRegistry.ServiceDefinition definition, Map<String, Object> input) {
        if (!"create".equalsIgnoreCase(definition.invoke())) {
            return delegate.execute(definition, input);
        }
        Set<String> primaryKeys = Set.copyOf(
                entities.primaryKeys(entities.entityClass(definition.entity())));
        List<ServiceHandlerRegistry.ParamDefinition> generated = definition.params().stream()
                .filter(param -> primaryKeys.contains(param.name()))
                .filter(param -> "OUT".equalsIgnoreCase(param.mode()))
                .filter(param -> !param.name().endsWith("SeqId"))
                .toList();
        if (primaryKeys.size() <= 1 || generated.isEmpty()) {
            return delegate.execute(definition, input);
        }
        Map<String, Object> adaptedInput = new LinkedHashMap<>(input);
        List<ServiceHandlerRegistry.ParamDefinition> adaptedParams = new ArrayList<>();
        for (ServiceHandlerRegistry.ParamDefinition param : definition.params()) {
            if (generated.stream().anyMatch(candidate -> candidate.name().equals(param.name()))) {
                adaptedInput.put(param.name(), sequences.nextId(definition.entity()));
                adaptedParams.add(new ServiceHandlerRegistry.ParamDefinition(
                        param.name(), "IN", param.optional(), param.type()));
            } else {
                adaptedParams.add(param);
            }
        }
        ServiceHandlerRegistry.ServiceDefinition adapted = new ServiceHandlerRegistry.ServiceDefinition(
                definition.name(), definition.entity(), definition.invoke(),
                definition.permissionMainAction(), adaptedParams);
        return delegate.execute(adapted, adaptedInput);
    }
}
