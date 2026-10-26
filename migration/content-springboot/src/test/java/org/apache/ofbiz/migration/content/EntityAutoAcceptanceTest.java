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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

/** content の entity-auto 全サービス（servicedef 由来 176 本）を作成・更新・失効・削除の観点で網羅する。 */
@SpringBootTest(classes = ContentMigrationApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WithMockUser(username = "admin", authorities = {"OFBTOOLS_VIEW", "CONTENTMGR_VIEW", "CONTENTMGR_ADMIN", "CONTENTMGR_SUPER_ADMIN"})
class EntityAutoAcceptanceTest {
    private static final AtomicInteger SEQ = new AtomicInteger();
    private static final Set<String> STAMPS = Set.of("createdStamp", "createdTxStamp", "lastUpdatedStamp", "lastUpdatedTxStamp");

    @Autowired
    private MigrationServiceDispatcher dispatcher;

    @Autowired
    private ContentServiceCatalog catalog;

    @Autowired
    private EntityRegistry entityRegistry;

    @Autowired
    private ContentEntityStore store;

    @Autowired
    private TransactionTemplate transactions;

    @Test
    void ac01CatalogMatchesInventoryEntityAutoServices() throws IOException {
        JsonNode inventory = new ObjectMapper().readTree(Path.of(System.getProperty("ofbiz.root"),
                "docs/migration/content/inventory/inventory.json").toFile());
        Set<String> expected = new TreeSet<>();
        for (JsonNode service : inventory.get("services")) {
            if ("entity-auto".equals(service.path("engine").asText())) {
                expected.add(service.path("name").asText());
            }
        }
        assertEquals(176, expected.size());
        assertEquals(expected, new TreeSet<>(catalog.all().keySet()));
    }

    @ParameterizedTest(name = "ac02_{0}")
    @MethodSource("serviceNames")
    void ac02EveryEntityAutoServiceRuns(String serviceName) {
        ServiceHandlerRegistry.ServiceDefinition definition = catalog.require(serviceName);
        Class<?> type = entityRegistry.entityClass(definition.entity());
        List<String> keys = entityRegistry.primaryKeys(type);
        switch (definition.invoke().toLowerCase()) {
        case "create" -> {
            Map<String, Object> input = new LinkedHashMap<>();
            for (var param : definition.params()) {
                boolean key = keys.contains(param.name());
                if ("IN".equalsIgnoreCase(param.mode()) && (key || !param.optional())) {
                    input.put(param.name(), sample(type, param.name()));
                }
                if (key && "INOUT".equalsIgnoreCase(param.mode()) && (keys.size() > 1 || !param.optional())) {
                    input.put(param.name(), sample(type, param.name()));
                }
            }
            for (String key : keys) {
                if (definition.params().stream().noneMatch(param -> param.name().equals(key))) {
                    input.put(key, sample(type, key));
                }
            }
            Map<String, Object> result = dispatcher.execute("content", serviceName, input);
            Map<String, Object> pk = new LinkedHashMap<>();
            keys.forEach(key -> pk.put(key, result.get(key)));
            assertNotNull(find(type, pk), serviceName + " must persist " + definition.entity());
        }
        case "update" -> {
            Map<String, Object> pk = seed(type, keys);
            String field = updatableField(definition, type, keys);
            Map<String, Object> input = new LinkedHashMap<>(pk);
            Object value = field == null ? null : sample(type, field);
            if (field != null) {
                input.put(field, value);
            }
            dispatcher.execute("content", serviceName, input);
            if (field != null) {
                Object stored = transactions.execute(status -> store.get(store.findOne(type.getSimpleName(), pk), field));
                assertEquals(String.valueOf(value), String.valueOf(stored));
            }
        }
        case "expire" -> {
            Map<String, Object> pk = seed(type, keys);
            dispatcher.execute("content", serviceName, pk);
            assertNotNull(transactions.execute(status ->
                    entityRegistry.get(store.findOne(type.getSimpleName(), pk), "thruDate")));
        }
        case "delete" -> {
            Map<String, Object> pk = seed(type, keys);
            dispatcher.execute("content", serviceName, pk);
            assertNull(find(type, pk), serviceName + " must delete " + definition.entity());
        }
        default -> throw new IllegalStateException("Unexpected invoke " + definition.invoke());
        }
    }

    @Test
    void ac03DuplicateCreateReturnsConflictAndMissingUpdateReturnsNotFound() {
        dispatcher.execute("content", "createContentType", Map.of("contentTypeId", "AC03_TYPE", "description", "AC03"));
        MigrationException duplicate = assertThrows(MigrationException.class, () ->
                dispatcher.execute("content", "createContentType", Map.of("contentTypeId", "AC03_TYPE")));
        assertEquals(409, duplicate.getStatus().value());
        MigrationException missing = assertThrows(MigrationException.class, () ->
                dispatcher.execute("content", "updateContentType", Map.of("contentTypeId", "AC03_ABSENT", "description", "x")));
        assertEquals(404, missing.getStatus().value());
    }

    @Test
    void ac04CreateContentAppliesServicedefDefaultsAndSequence() {
        Map<String, Object> created = dispatcher.execute("content", "createContent", Map.of("contentName", "AC04"));
        assertEquals("DOCUMENT", created.get("contentTypeId"));
        assertEquals("CTNT_IN_PROGRESS", created.get("statusId"));
        assertEquals("admin", created.get("createdByUserLogin"));
        assertNotEquals(null, created.get("contentId"));
    }

    @Test
    void ac05CreateContentAssocDefaultsFromDate() {
        Map<String, Object> created = dispatcher.execute("content", "createContentAssoc", Map.of(
                "contentId", "AC05_FROM", "contentIdTo", "AC05_TO", "contentAssocTypeId", "SUB_CONTENT"));
        assertNotNull(created.get("fromDate"));
    }

    static Stream<String> serviceNames() {
        return org.apache.ofbiz.migration.content.generated.ServiceCatalog.all().keySet().stream().sorted();
    }

    private Object find(Class<?> type, Map<String, Object> pk) {
        return transactions.execute(status -> store.findOne(type.getSimpleName(), pk));
    }

    private Map<String, Object> seed(Class<?> type, List<String> keys) {
        Map<String, Object> pk = new LinkedHashMap<>();
        keys.forEach(key -> pk.put(key, sample(type, key)));
        transactions.executeWithoutResult(status -> store.create(type.getSimpleName(), pk));
        return pk;
    }

    private String updatableField(ServiceHandlerRegistry.ServiceDefinition definition, Class<?> type, List<String> keys) {
        BeanWrapperImpl wrapper = new BeanWrapperImpl(entityRegistry.newEntity(type));
        return definition.params().stream()
                .map(ServiceHandlerRegistry.ParamDefinition::name)
                .filter(name -> !keys.contains(name) && !STAMPS.contains(name) && !"thruDate".equals(name))
                .filter(wrapper::isWritableProperty)
                .filter(name -> wrapper.getPropertyType(name) == String.class)
                .findFirst().orElse(null);
    }

    private Object sample(Class<?> type, String field) {
        BeanWrapperImpl wrapper = new BeanWrapperImpl(entityRegistry.newEntity(type));
        Class<?> fieldType = wrapper.isWritableProperty(field) ? wrapper.getPropertyType(field) : String.class;
        int next = SEQ.incrementAndGet();
        if (fieldType == Timestamp.class) {
            return new Timestamp(1_700_000_000_000L + next * 1000L);
        }
        if (fieldType == Long.class) {
            return (long) next;
        }
        if (fieldType == BigDecimal.class) {
            return BigDecimal.valueOf(next);
        }
        if (fieldType == Double.class) {
            return (double) next;
        }
        if (fieldType == java.sql.Date.class) {
            return java.sql.Date.valueOf("2026-01-01");
        }
        try {
            jakarta.persistence.Column column = type.getDeclaredField(field).getAnnotation(jakarta.persistence.Column.class);
            if (column != null && column.length() > 0 && column.length() < 5) {
                return "Y";
            }
        } catch (NoSuchFieldException ignored) {
            // 継承フィールドは既定値を使う
        }
        return "E" + next;
    }
}
