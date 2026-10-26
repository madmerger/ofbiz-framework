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
package org.apache.ofbiz.migration.common;

import jakarta.persistence.EntityManager;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EntityAutoService {
    private static final Pattern DENIED_TEXT = Pattern.compile(
            "(?i)<script|javascript:|onerror=|system|include|import");
    private final EntityManager entityManager;
    private final EntityRegistry entityRegistry;
    private final SequenceService sequenceService;

    public EntityAutoService(EntityManager entityManager, EntityRegistry entityRegistry, SequenceService sequenceService) {
        this.entityManager = entityManager;
        this.entityRegistry = entityRegistry;
        this.sequenceService = sequenceService;
    }

    @Transactional
    public Map<String, Object> execute(ServiceHandlerRegistry.ServiceDefinition definition, Map<String, ?> input) {
        validateText(input);
        Class<?> type = entityRegistry.entityClass(definition.entity());
        entityRegistry.requireSensitiveView(type);
        return switch (definition.invoke().toLowerCase()) {
            case "create" -> create(definition, type, input);
            case "update" -> update(definition, type, input);
            case "expire" -> expire(definition, type, input);
            case "delete" -> delete(definition, type, input);
            default -> throw new MigrationException(HttpStatus.BAD_REQUEST, "Unsupported invoke: " + definition.invoke());
        };
    }

    private Map<String, Object> create(
            ServiceHandlerRegistry.ServiceDefinition definition, Class<?> type, Map<String, ?> input) {
        Object entity = entityRegistry.newEntity(type);
        Set<String> keys = Set.copyOf(entityRegistry.primaryKeys(type));
        for (var param : definition.params()) {
            if ("IN".equalsIgnoreCase(param.mode()) && input.containsKey(param.name())) {
                entityRegistry.set(entity, param.name(), input.get(param.name()));
            }
        }
        for (String key : keys) {
            if ("fromDate".equals(key) && entityRegistry.get(entity, key) == null) {
                entityRegistry.set(entity, key, Timestamp.from(Instant.now()));
            }
        }
        List<ServiceHandlerRegistry.ParamDefinition> generated = definition.params().stream()
                .filter(param -> keys.contains(param.name()))
                .filter(param -> "OUT".equalsIgnoreCase(param.mode())
                        || ("INOUT".equalsIgnoreCase(param.mode()) && input.get(param.name()) == null))
                .toList();
        if (generated.size() == 1 && keys.size() > 1) {
            entityRegistry.set(entity, generated.get(0).name(),
                    sequenceService.nextSubSequence(definition.entity(), input, generated.get(0).name()));
        } else if (generated.size() == 1) {
            entityRegistry.set(entity, generated.get(0).name(), sequenceService.nextId(definition.entity()));
        }
        validateRequired(definition, input);
        for (String key : keys) {
            if (entityRegistry.get(entity, key) == null) {
                throw new MigrationException(HttpStatus.BAD_REQUEST, "Missing primary key: " + key);
            }
        }
        if (entityRegistry.findByPrimaryKey(type, entityRegistry.toMap(entity)) != null) {
            throw new MigrationException(HttpStatus.CONFLICT, "ServiceValueFound");
        }
        entityManager.persist(entity);
        return entityRegistry.toMap(entity);
    }

    private Map<String, Object> update(
            ServiceHandlerRegistry.ServiceDefinition definition, Class<?> type, Map<String, ?> input) {
        Object entity = requireEntity(type, input);
        Set<String> keys = new HashSet<>(entityRegistry.primaryKeys(type));
        for (var entry : input.entrySet()) {
            if (!keys.contains(entry.getKey())) entityRegistry.set(entity, entry.getKey(), entry.getValue());
        }
        return entityRegistry.toMap(entity);
    }

    private Map<String, Object> expire(
            ServiceHandlerRegistry.ServiceDefinition definition, Class<?> type, Map<String, ?> input) {
        Object entity = requireEntity(type, input);
        Object thruDate = input.containsKey("thruDate") ? input.get("thruDate") : Timestamp.from(Instant.now());
        entityRegistry.set(entity, "thruDate", thruDate);
        return entityRegistry.toMap(entity);
    }

    private Map<String, Object> delete(
            ServiceHandlerRegistry.ServiceDefinition definition, Class<?> type, Map<String, ?> input) {
        Object entity = requireEntity(type, input);
        Map<String, Object> result = entityRegistry.toMap(entity);
        entityManager.remove(entity);
        return result;
    }

    private Object requireEntity(Class<?> type, Map<String, ?> input) {
        Object entity = entityRegistry.findByPrimaryKey(type, input);
        if (entity == null) throw new MigrationException(HttpStatus.NOT_FOUND, "ServiceValueNotFound");
        return entity;
    }

    private static void validateRequired(ServiceHandlerRegistry.ServiceDefinition definition, Map<String, ?> input) {
        for (var param : definition.params()) {
            if ("IN".equalsIgnoreCase(param.mode()) && !param.optional() && !input.containsKey(param.name())) {
                throw new MigrationException(HttpStatus.BAD_REQUEST, "Missing required input: " + param.name());
            }
        }
    }

    private static void validateText(Map<String, ?> input) {
        for (Object value : input.values()) {
            if (value instanceof String text && DENIED_TEXT.matcher(text).find()) {
                throw new MigrationException(HttpStatus.BAD_REQUEST, "Invalid text");
            }
        }
    }
}
