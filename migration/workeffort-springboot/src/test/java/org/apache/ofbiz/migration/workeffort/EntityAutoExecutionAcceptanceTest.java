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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.apache.ofbiz.migration.workeffort;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.SingularAttribute;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.apache.ofbiz.migration.workeffort.generated.ServiceCatalog;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:workeffort-entity-auto-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "migration.security.component=workeffort",
        "migration.security.require-https=false",
        "migration.security.extra-entities=RateAmount,PartyRate,StatusItem,StatusValidChange,WorkEffortStatus"
})
class EntityAutoExecutionAcceptanceTest {
    private static final AtomicLong IDS = new AtomicLong();
    private static final List<String> INVOKES = List.of("create", "update", "delete", "expire");

    @Autowired
    private ServiceHandlerRegistry registry;

    @Autowired
    private MigrationServiceDispatcher dispatcher;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private EntityManager entityManager;

    @BeforeAll
    void hasAllNinetyFiveCatalogServices() {
        assertThat(entityAutoDefinitions()).hasSize(95);
    }

    Stream<Arguments> entityAutoServices() {
        return entityAutoDefinitions().stream()
                .map(definition -> Arguments.of(definition.name()));
    }

    @ParameterizedTest(name = "entity-auto {0}")
    @MethodSource("entityAutoServices")
    @WithMockUser(username = "migration-admin", authorities = {
            "OFBTOOLS_VIEW", "WORKEFFORTMGR_ADMIN", "WORKEFFORTMGR_SENSITIVE_VIEW"
    })
    @Transactional
    void invokesEveryEntityAutoService(String name) {
        ServiceHandlerRegistry.ServiceDefinition target = definition(name);
        Map<String, Object> result;
        if ("create".equalsIgnoreCase(target.invoke())) {
            Map<String, Object> input = sampleInput(target, Map.of());
            addMissingPrimaryKeys(target, input);
            if ("createTimeEntry".equals(name)) {
                input.put("timesheetId", persistFixture("Timesheet").get("timesheetId"));
            }
            result = dispatcher.execute("workeffort", name, input);
        } else {
            Map<String, Object> keys = persistFixture(target.entity());
            result = dispatcher.execute("workeffort", name, sampleInput(target, keys));
        }
        assertThat(result).as(name + " result").isNotEmpty();
    }

    private List<ServiceHandlerRegistry.ServiceDefinition> entityAutoDefinitions() {
        return ServiceCatalog.all().values().stream()
                .filter(definition -> definition.entity() != null && !definition.entity().isBlank())
                .filter(definition -> definition.invoke() != null
                        && INVOKES.contains(definition.invoke().toLowerCase()))
                .map(definition -> registry.find("workeffort", definition.name()).orElseThrow())
                .toList();
    }

    private ServiceHandlerRegistry.ServiceDefinition definition(String name) {
        return registry.find("workeffort", name).orElseThrow();
    }

    private Map<String, Object> sampleInput(
            ServiceHandlerRegistry.ServiceDefinition definition, Map<String, ?> existingKeys) {
        Map<String, Object> input = new LinkedHashMap<>(existingKeys);
        Class<?> entityType = entities.entityClass(definition.entity());
        for (ServiceHandlerRegistry.ParamDefinition param : definition.params()) {
            if (!("IN".equalsIgnoreCase(param.mode()) || "INOUT".equalsIgnoreCase(param.mode())
                    || "None".equalsIgnoreCase(param.mode()))
                    || input.containsKey(param.name())) continue;
            input.put(param.name(), sampleValue(param, entityType));
        }
        return input;
    }

    private Map<String, Object> primaryKeys(String entityName, Map<String, Object> values) {
        Map<String, Object> keys = new LinkedHashMap<>();
        for (String key : entities.primaryKeys(entities.entityClass(entityName))) {
            if (values.containsKey(key)) keys.put(key, values.get(key));
        }
        return keys;
    }

    private Map<String, Object> persistFixture(String entityName) {
        Class<?> entityType = entities.entityClass(entityName);
        Object fixture = entities.newEntity(entityType);
        var metadata = entityManager.getMetamodel().entity(entityType);
        for (SingularAttribute<?, ?> attribute : metadata.getSingularAttributes()) {
            String name = attribute.getName();
            Field field = field(entityType, name);
            Column column = field == null ? null : field.getAnnotation(Column.class);
            if (attribute.isOptional() && !attribute.isId()) continue;
            entities.set(fixture, name, fixtureValue(name, attribute.getJavaType(),
                    column == null ? -1 : column.length()));
        }
        if ("Timesheet".equals(entityName)
                && metadata.getSingularAttribute("statusId") != null) {
            entities.set(fixture, "statusId", "TIMESHEET_IN_PROCESS");
        }
        entityManager.persist(fixture);
        entityManager.flush();
        Map<String, Object> keys = primaryKeys(entityName, entities.toMap(fixture));
        assertThat(keys).as(entityName + " primary keys").isNotEmpty();
        assertThat(entities.findByPrimaryKey(entityType, keys))
                .as(entityName + " persisted fixture %s", keys).isNotNull();
        return keys;
    }

    private void addMissingPrimaryKeys(ServiceHandlerRegistry.ServiceDefinition target, Map<String, Object> input) {
        if (!"create".equalsIgnoreCase(target.invoke())) return;
        Class<?> entityType = entities.entityClass(target.entity());
        for (String key : entities.primaryKeys(entityType)) {
            if (input.containsKey(key)) continue;
            Field field = field(entityType, key);
            if (field != null && field.getType() == String.class) {
                input.put(key, "T" + Long.toString(IDS.incrementAndGet(), 36).toUpperCase());
            }
        }
    }

    private static Object sampleValue(ServiceHandlerRegistry.ParamDefinition param, Class<?> entityType) {
        String name = param.name();
        String type = param.type() == null ? "" : param.type().toLowerCase();
        long unique = IDS.incrementAndGet();
        Class<?> entityFieldType = field(entityType, name) == null
                ? String.class : field(entityType, name).getType();
        if (name.toLowerCase().contains("date") || type.contains("date-time") || type.contains("timestamp")) {
            return new Timestamp(System.currentTimeMillis() + unique);
        }
        if (isDecimal(entityFieldType) || type.contains("currency-amount") || type.contains("numeric")) {
            return BigDecimal.ONE;
        }
        if (isInteger(entityFieldType) || type.contains("integer") || type.contains("int") || type.contains("long")) {
            return 1;
        }
        if (isFloating(entityFieldType) || type.contains("floating-point") || type.contains("double")
                || type.contains("float")) return 1.0;
        if (entityFieldType == Boolean.class || entityFieldType == boolean.class) return Boolean.TRUE;
        String sample = switch (name) {
        case "partyId" -> "DemoEmployee";
        case "userLoginId" -> "admin";
        case "workEffortTypeId" -> "TASK";
        case "roleTypeId" -> "CAL_OWNER";
        case "currentStatusId" -> "CAL_CREATED";
        case "statusId" -> "PRTYASGN_ASSIGNED";
        case "scopeEnumId" -> "WES_PUBLIC";
        case "currencyUomId", "rateCurrencyUomId" -> "USD";
        case "uomId" -> "EA";
        default -> "T" + Long.toString(unique, 36).toUpperCase();
        };
        int length = columnLength(entityType, name);
        return length == 1 ? "Y" : length > 0 && sample.length() > length ? sample.substring(0, length) : sample;
    }

    private static Object fixtureValue(String name, Class<?> type, int length) {
        long unique = IDS.incrementAndGet();
        if (type == Timestamp.class) return new Timestamp(System.currentTimeMillis() + unique);
        if (type == java.sql.Date.class) return new java.sql.Date(System.currentTimeMillis());
        if (type == BigDecimal.class) return BigDecimal.ONE;
        if (type == Long.class || type == long.class) return unique;
        if (type == Integer.class || type == int.class) return 1;
        if (type == Short.class || type == short.class) return (short) 1;
        if (type == Double.class || type == double.class) return 1.0d;
        if (type == Float.class || type == float.class) return 1.0f;
        if (type == Boolean.class || type == boolean.class) return Boolean.TRUE;
        String value = switch (name) {
        case "partyId" -> "DemoEmployee";
        case "workEffortTypeId" -> "TASK";
        case "roleTypeId" -> "CAL_OWNER";
        case "currentStatusId" -> "CAL_CREATED";
        case "statusId" -> "PRTYASGN_ASSIGNED";
        default -> "F" + Long.toString(unique, 36).toUpperCase();
        };
        return length == 1 ? "Y" : length > 0 && value.length() > length ? value.substring(0, length) : value;
    }

    private static boolean isDecimal(Class<?> type) {
        return type == BigDecimal.class || type == java.math.BigInteger.class;
    }

    private static boolean isInteger(Class<?> type) {
        return type == Long.class || type == long.class || type == Integer.class || type == int.class
                || type == Short.class || type == short.class;
    }

    private static boolean isFloating(Class<?> type) {
        return type == Double.class || type == double.class || type == Float.class || type == float.class;
    }

    private static Field field(Class<?> type, String name) {
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                continue;
            }
        }
        return null;
    }

    private static int columnLength(Class<?> entityType, String fieldName) {
        for (Class<?> current = entityType; current != null; current = current.getSuperclass()) {
            try {
                Field field = current.getDeclaredField(fieldName);
                Column column = field.getAnnotation(Column.class);
                return column == null ? -1 : column.length();
            } catch (NoSuchFieldException ignored) {
                continue;
            }
        }
        return -1;
    }
}
