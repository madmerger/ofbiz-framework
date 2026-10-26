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
package org.apache.ofbiz.migration.content;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.apache.ofbiz.migration.content.generated.ServiceCatalog;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

/**
 * content の servicedef から生成した entity-auto カタログを共通レジストリへ登録する。
 * 生成ツールが解決できない定義（default-entity-name なし、interface の implements のみで属性を持つもの）は
 * OFBiz の entity-auto 既定（create: pk INOUT / nonpk IN、update: pk IN 必須 / nonpk IN、delete・expire: pk IN 必須）で補完する。
 * mode を持たない override（生成結果 "None"）は、対象 Entity のフィールドなら IN として扱い、それ以外（implements 先の属性）は除く。
 */
@Component
public final class ContentServiceCatalog {
    public static final String COMPONENT = "content";
    private static final Set<String> STAMPS = Set.of("createdStamp", "createdTxStamp", "lastUpdatedStamp", "lastUpdatedTxStamp");
    private static final List<String> VERBS = List.of("create", "update", "delete", "remove", "expire", "add");
    private final Map<String, ServiceHandlerRegistry.ServiceDefinition> definitions;
    private final List<String> completed = new ArrayList<>();

    public ContentServiceCatalog(ServiceHandlerRegistry registry, EntityRegistry entityRegistry) {
        Map<String, ServiceHandlerRegistry.ServiceDefinition> converted = new LinkedHashMap<>();
        ServiceCatalog.all().forEach((name, service) -> {
            String entity = service.entity().isBlank() ? inferEntity(name, entityRegistry) : service.entity();
            Set<String> fields = entity.isBlank() ? Set.of() : fieldNames(entityRegistry, entity);
            List<ServiceHandlerRegistry.ParamDefinition> params = new ArrayList<>(service.params().stream()
                    .filter(param -> !"None".equals(param.mode()) || fields.contains(param.name()))
                    .map(param -> new ServiceHandlerRegistry.ParamDefinition(param.name(),
                            "None".equals(param.mode()) ? "IN" : param.mode(), param.optional(), param.type()))
                    .toList());
            if (complete(service.invoke(), entity, params, entityRegistry)) {
                completed.add(name);
            }
            converted.put(name, new ServiceHandlerRegistry.ServiceDefinition(
                    service.name(), entity, service.invoke(), service.permissionMainAction(), List.copyOf(params)));
        });
        this.definitions = Map.copyOf(converted);
        registry.registerCatalog(COMPONENT, converted);
    }

    public Map<String, ServiceHandlerRegistry.ServiceDefinition> all() {
        return definitions;
    }

    /** 生成カタログの属性を補完したサービス名。 */
    public List<String> completedServices() {
        return List.copyOf(completed);
    }

    public ServiceHandlerRegistry.ServiceDefinition require(String name) {
        ServiceHandlerRegistry.ServiceDefinition definition = definitions.get(name);
        if (definition == null) {
            throw new IllegalStateException("Unknown content entity-auto service: " + name);
        }
        return definition;
    }

    private static Set<String> fieldNames(EntityRegistry entityRegistry, String entity) {
        BeanWrapperImpl wrapper = new BeanWrapperImpl(entityRegistry.newEntity(entityRegistry.entityClass(entity)));
        return java.util.Arrays.stream(wrapper.getPropertyDescriptors())
                .filter(descriptor -> descriptor.getWriteMethod() != null)
                .map(java.beans.PropertyDescriptor::getName)
                .collect(Collectors.toSet());
    }

    private static String inferEntity(String serviceName, EntityRegistry entityRegistry) {
        for (String verb : VERBS) {
            if (serviceName.startsWith(verb)) {
                String candidate = serviceName.substring(verb.length());
                try {
                    return entityRegistry.entityClass(candidate).getSimpleName();
                } catch (RuntimeException ignored) {
                    return "";
                }
            }
        }
        return "";
    }

    private static boolean complete(String invoke, String entity, List<ServiceHandlerRegistry.ParamDefinition> params,
            EntityRegistry entityRegistry) {
        if (entity.isBlank()) {
            return false;
        }
        Class<?> type = entityRegistry.entityClass(entity);
        List<String> keys = entityRegistry.primaryKeys(type);
        Set<String> declared = params.stream().map(ServiceHandlerRegistry.ParamDefinition::name).collect(Collectors.toSet());
        if (declared.containsAll(keys) && declared.size() > keys.size()) {
            return false;
        }
        boolean create = "create".equalsIgnoreCase(invoke);
        for (String key : keys) {
            if (!declared.contains(key)) {
                boolean optional = create && "fromDate".equals(key);
                params.add(new ServiceHandlerRegistry.ParamDefinition(key, create ? "INOUT" : "IN", optional || (create && keys.size() == 1),
                        "id"));
            }
        }
        if (create || "update".equalsIgnoreCase(invoke)) {
            BeanWrapperImpl wrapper = new BeanWrapperImpl(entityRegistry.newEntity(type));
            for (var descriptor : wrapper.getPropertyDescriptors()) {
                String field = descriptor.getName();
                if (descriptor.getWriteMethod() != null && !keys.contains(field) && !STAMPS.contains(field) && !declared.contains(field)) {
                    params.add(new ServiceHandlerRegistry.ParamDefinition(field, "IN", true, "String"));
                }
            }
        }
        return true;
    }
}
