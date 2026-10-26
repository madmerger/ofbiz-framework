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

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.product.generated.ProductServiceMetadata;
import org.apache.ofbiz.migration.product.generated.ServiceCatalog;
import org.springframework.beans.BeanWrapperImpl;

final class ProductTestSupport {
    private ProductTestSupport() { }

    static Map<String, Object> input(
            ServiceCatalog.ServiceDefinition definition, EntityRegistry entities, String variant,
            boolean includeOptional) {
        Class<?> type = entities.entityClass(definition.entity());
        BeanWrapperImpl entityProperties = new BeanWrapperImpl(entities.newEntity(type));
        Set<String> primaryKeys = Set.copyOf(entities.primaryKeys(type));
        Map<String, Object> result = new LinkedHashMap<>();
        for (ServiceCatalog.ParamDefinition parameter : definition.params()) {
            if (!entityProperties.isWritableProperty(parameter.name())) {
                continue;
            }
            if ("create".equalsIgnoreCase(definition.invoke())
                    && primaryKeys.contains(parameter.name())
                    && isGeneratedInoutPrimaryKey(definition, entities, parameter.name())) {
                continue;
            }
            boolean inputParameter = "IN".equalsIgnoreCase(parameter.mode())
                    || "INOUT".equalsIgnoreCase(parameter.mode())
                            && (!"create".equalsIgnoreCase(definition.invoke()) || !parameter.optional());
            if (inputParameter && (includeOptional || !parameter.optional())) {
                result.put(parameter.name(), value(type, parameter.name(), parameter.type(), variant));
            }
        }
        for (String key : entities.primaryKeys(type)) {
            ServiceCatalog.ParamDefinition parameter = definition.params().stream()
                    .filter(candidate -> candidate.name().equals(key))
                    .findFirst().orElse(null);
            if (result.containsKey(key) || parameter != null && "OUT".equalsIgnoreCase(parameter.mode())
                    || parameter != null && isGeneratedInoutPrimaryKey(definition, entities, key)) {
                continue;
            }
            result.put(key, value(type, key, parameter == null ? null : parameter.type(), variant));
        }
        return result;
    }

    static List<String> generatedPrimaryKeys(
            ServiceCatalog.ServiceDefinition definition, EntityRegistry entities, Map<String, ?> input) {
        Class<?> type = entities.entityClass(definition.entity());
        return entities.primaryKeys(type).stream()
                .filter(key -> definition.params().stream().anyMatch(parameter -> {
                    if (!key.equals(parameter.name())) {
                        return false;
                    }
                    return "OUT".equalsIgnoreCase(parameter.mode())
                            || "INOUT".equalsIgnoreCase(parameter.mode())
                                    && !input.containsKey(key)
                                    && isGeneratedInoutPrimaryKey(definition, entities, key);
                }))
                .toList();
    }

    private static boolean isGeneratedInoutPrimaryKey(
            ServiceCatalog.ServiceDefinition definition, EntityRegistry entities, String key) {
        Class<?> type = entities.entityClass(definition.entity());
        List<String> primaryKeys = entities.primaryKeys(type);
        ServiceCatalog.ParamDefinition parameter = definition.params().stream()
                .filter(candidate -> key.equals(candidate.name()))
                .findFirst().orElse(null);
        if (parameter == null || !"INOUT".equalsIgnoreCase(parameter.mode())) {
            return false;
        }
        if (primaryKeys.size() == 1) {
            return true;
        }
        return key.endsWith("SeqId") && primaryKeys.stream()
                .filter(primaryKey -> !primaryKey.equals(key))
                .allMatch(primaryKey -> definition.params().stream()
                        .anyMatch(candidate -> primaryKey.equals(candidate.name())
                                && ("IN".equalsIgnoreCase(candidate.mode())
                                        || "INOUT".equalsIgnoreCase(candidate.mode()))));
    }

    static Map<String, Object> keys(ServiceCatalog.ServiceDefinition definition, EntityRegistry entities,
            Map<String, ?> input) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (String key : entities.primaryKeys(entities.entityClass(definition.entity()))) {
            if (input.containsKey(key)) {
                result.put(key, input.get(key));
            }
        }
        return result;
    }

    static Map<String, Object> resultKeys(
            ServiceCatalog.ServiceDefinition definition, EntityRegistry entities, Map<String, ?> result) {
        Map<String, Object> keys = new LinkedHashMap<>();
        for (String key : entities.primaryKeys(entities.entityClass(definition.entity()))) {
            if (result.containsKey(key)) {
                keys.put(key, result.get(key));
            }
        }
        return keys;
    }

    static Object value(Class<?> entityType, String fieldName, String ofbizType, String variant) {
        Class<?> propertyType = null;
        if (entityType != null) {
            try {
                propertyType = entityType.getMethod("get" + Character.toUpperCase(fieldName.charAt(0))
                        + fieldName.substring(1)).getReturnType();
            } catch (ReflectiveOperationException ignored) {
                propertyType = null;
            }
        }
        String normalized = ofbizType == null ? "" : ofbizType.toLowerCase();
        if (propertyType == Timestamp.class || normalized.equals("date-time")) {
            return "2020-01-02 03:04:05";
        }
        if (propertyType == Date.class || normalized.equals("date")) {
            return "2020-01-02";
        }
        if (propertyType == Time.class || normalized.equals("time")) {
            return "03:04:05";
        }
        if (propertyType == Integer.class || normalized.equals("integer")) {
            return 1;
        }
        if (propertyType == Long.class || normalized.equals("numeric")) {
            return 1L;
        }
        if (propertyType == BigDecimal.class || normalized.startsWith("currency")
                || normalized.equals("fixed-point")) {
            return "1.25";
        }
        if (propertyType == Double.class || normalized.equals("floating-point")) {
            return 1.25;
        }
        if (normalized.equals("indicator")) {
            return "Y";
        }
        if (normalized.equals("very-short")) {
            return "v" + Integer.toString(Math.abs(fieldName.hashCode()), 36);
        }
        if (normalized.equals("credit-card-date")) {
            return "01/2030";
        }
        if (propertyType == byte[].class || normalized.equals("blob")
                || normalized.equals("byte-array") || normalized.equals("object")) {
            return "test-data";
        }
        int variantHash = Math.abs(variant.hashCode());
        int fieldHash = Math.abs(fieldName.hashCode());
        return "p" + Integer.toString(variantHash, 36) + Integer.toString(fieldHash, 36);
    }

    static ServiceCatalog.ServiceDefinition matching(
            String entity, String invoke, String component) {
        return ServiceCatalog.all().values().stream()
                .filter(definition -> entity.equals(definition.entity()))
                .filter(definition -> invoke.equalsIgnoreCase(definition.invoke()))
                .filter(definition -> {
                    ProductServiceMetadata.ServiceMetadata metadata =
                            ProductServiceMetadata.all().get(definition.name());
                    return metadata != null && component.equals(metadata.component());
                })
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No " + invoke + " service for " + entity));
    }
}
