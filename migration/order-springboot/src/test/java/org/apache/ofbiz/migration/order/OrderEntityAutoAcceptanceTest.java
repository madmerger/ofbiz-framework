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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.apache.ofbiz.migration.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.order.generated.ServiceCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderEntityAutoAcceptanceTest {
    @Autowired
    private MigrationServiceDispatcher dispatcher;

    @Autowired
    private EntityRegistry entities;

    @Test
    @Transactional
    @WithMockUser(authorities = {
        "OFBTOOLS_VIEW", "ORDERMGR_ADMIN", "ORDERMGR_SENSITIVE_VIEW"
    })
    void ac01_exercisesEntityAutoOperationsWithAvailableCreateFixtures() {
        List<ServiceCatalog.ServiceDefinition> definitions = ServiceCatalog.all().values().stream()
                .filter(definition -> definition.entity() != null
                        && Set.of("create", "update", "expire", "delete")
                                .contains(definition.invoke().toLowerCase()))
                .sorted(Comparator.comparing(ServiceCatalog.ServiceDefinition::name))
                .toList();
        Map<String, List<ServiceCatalog.ServiceDefinition>> byEntity = new LinkedHashMap<>();
        for (ServiceCatalog.ServiceDefinition definition : definitions) {
            byEntity.computeIfAbsent(definition.entity(), ignored -> new ArrayList<>()).add(definition);
        }
        assertEquals(220, definitions.size());
        assertEquals(81, count(definitions, "create"));
        assertEquals(62, count(definitions, "update"));
        assertEquals(4, count(definitions, "expire"));
        assertEquals(73, count(definitions, "delete"));

        Map<String, Map<String, Object>> createdByEntity = new LinkedHashMap<>();
        int sequence = 1;
        for (ServiceCatalog.ServiceDefinition definition : definitions) {
            if (!"create".equalsIgnoreCase(definition.invoke())) {
                continue;
            }
            Map<String, Object> created = dispatcher.execute(
                    "order", definition.name(), inputFor(definition, Map.of(), sequence++));
            Map<String, Object> keys = primaryKeyValues(definition.entity(), created);
            assertEquals(entities.primaryKeys(entities.entityClass(definition.entity())).size(), keys.size());
            createdByEntity.putIfAbsent(definition.entity(), keys);
        }

        for (ServiceCatalog.ServiceDefinition definition : definitions) {
            String invoke = definition.invoke().toLowerCase();
            if ("create".equals(invoke)) {
                continue;
            }
            Map<String, Object> keys = createdByEntity.get(definition.entity());
            if (keys == null) {
                continue;
            }
            if ("delete".equals(invoke)
                    && hasEntityAutoDefinition(byEntity, definition.entity(), "create")) {
                ServiceCatalog.ServiceDefinition create = findDefinition(byEntity, definition.entity(), "create");
                Map<String, Object> created = dispatcher.execute(
                        "order", create.name(), inputFor(create, Map.of(), sequence++));
                keys = primaryKeyValues(definition.entity(), created);
            }
            Map<String, Object> input = inputFor(definition, keys, sequence++);
            try {
                dispatcher.execute("order", definition.name(), input);
            } catch (RuntimeException exception) {
                throw new AssertionError(definition.name() + " " + input, exception);
            }
        }
    }

    private static long count(List<ServiceCatalog.ServiceDefinition> definitions, String invoke) {
        return definitions.stream()
                .filter(definition -> invoke.equalsIgnoreCase(definition.invoke()))
                .count();
    }

    private static boolean hasEntityAutoDefinition(
            Map<String, List<ServiceCatalog.ServiceDefinition>> byEntity, String entity, String invoke) {
        return byEntity.getOrDefault(entity, List.of()).stream()
                .anyMatch(definition -> invoke.equalsIgnoreCase(definition.invoke()));
    }

    private static ServiceCatalog.ServiceDefinition findDefinition(
            Map<String, List<ServiceCatalog.ServiceDefinition>> byEntity, String entity, String invoke) {
        return byEntity.getOrDefault(entity, List.of()).stream()
                .filter(definition -> invoke.equalsIgnoreCase(definition.invoke()))
                .findFirst()
                .orElseThrow();
    }

    private Map<String, Object> primaryKeyValues(String entityName, Map<String, Object> values) {
        Map<String, Object> keys = new LinkedHashMap<>();
        for (String key : entities.primaryKeys(entities.entityClass(entityName))) {
            assertNotNull(values.get(key), entityName + "." + key);
            keys.put(key, values.get(key));
        }
        return keys;
    }

    private Map<String, Object> inputFor(
            ServiceCatalog.ServiceDefinition definition, Map<String, Object> keys, int sequence) {
        Set<String> primaryKeys = keys.keySet();
        Set<String> createPrimaryKeys = "create".equalsIgnoreCase(definition.invoke())
                ? Set.copyOf(entities.primaryKeys(entities.entityClass(definition.entity())))
                : Set.of();
        Map<String, Object> input = new LinkedHashMap<>();
        int parameterIndex = 1;
        for (ServiceCatalog.ParamDefinition parameter : definition.params()) {
            if ("OUT".equalsIgnoreCase(parameter.mode())) {
                continue;
            }
            if (primaryKeys.contains(parameter.name())) {
                input.put(parameter.name(), keys.get(parameter.name()));
            } else if (createPrimaryKeys.contains(parameter.name())) {
                input.put(parameter.name(), sampleValue(parameter, sequence, parameterIndex));
            } else if (!parameter.optional()
                    && ("IN".equalsIgnoreCase(parameter.mode()) || "INOUT".equalsIgnoreCase(parameter.mode()))) {
                input.put(parameter.name(), sampleValue(parameter, sequence, parameterIndex));
            } else if ("expire".equalsIgnoreCase(definition.invoke()) && "thruDate".equals(parameter.name())) {
                input.put(parameter.name(), sampleValue(parameter, sequence, parameterIndex));
            }
            parameterIndex++;
        }
        return input;
    }

    private static Object sampleValue(ServiceCatalog.ParamDefinition parameter, int sequence, int index) {
        String type = parameter.type() == null ? "" : parameter.type().toLowerCase();
        if (type.contains("date-time") || type.contains("timestamp")) {
            return Timestamp.from(Instant.now().truncatedTo(ChronoUnit.SECONDS));
        }
        if (type.equals("date") || parameter.name().toLowerCase().endsWith("date")) {
            return LocalDate.now();
        }
        if (type.contains("numeric") || type.contains("fixed-point")
                || type.contains("currency") || type.contains("floating")) {
            return BigDecimal.ONE;
        }
        if (type.contains("integer")) {
            return 1;
        }
        if (type.contains("indicator") || type.contains("boolean")) {
            return "N";
        }
        if (parameter.name().endsWith("SeqId") || parameter.name().equals("sequenceId")) {
            return String.format("%05d", sequence % 100000);
        }
        return String.format("A%05d%02d", sequence % 100000, index % 100);
    }
}
