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
package org.apache.ofbiz.migration.manufacturing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.manufacturing.generated.ServiceCatalog;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
@WithMockUser(authorities = {"OFBTOOLS_VIEW", "MANUFACTURING_ADMIN", "MANUFACTURING_SENSITIVE_VIEW"})
class EntityAutoAcceptanceTest {
    private static final AtomicInteger ID = new AtomicInteger();
    private static final Set<String> EXECUTED_SERVICES = ConcurrentHashMap.newKeySet();
    private static final Pattern ENTITY_AUTO_SERVICE =
            Pattern.compile("<service\\b[^>]*\\bengine=\"entity-auto\"[^>]*>");

    @Autowired
    private MigrationServiceDispatcher dispatcher;
    @Autowired
    private EntityRegistry entities;

    @Test
    void ac01_entityAutoCatalogMatchesServiceDefinitions() throws IOException {
        long definitionCount;
        java.nio.file.Path serviceDirectory = java.nio.file.Path.of(
                System.getProperty("ofbiz.root"), "applications/manufacturing/servicedef");
        try (Stream<java.nio.file.Path> paths = java.nio.file.Files.list(serviceDirectory)) {
            definitionCount = paths.filter(path -> path.toString().endsWith(".xml"))
                    .mapToLong(EntityAutoAcceptanceTest::countEntityAutoServices)
                    .sum();
        }
        assertEquals(definitionCount, ServiceCatalog.all().size());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("entityFamilies")
    void ac02_entityAutoServicesSupportLifecycle(String entityFamily) {
        Fixture fixture = fixture(entityFamily, ID.incrementAndGet());
        if (fixture.prepareCalendarId() != null) {
            call("createCalendar", Map.of("calendarId", fixture.prepareCalendarId()));
            EXECUTED_SERVICES.add("createCalendar");
        }

        call(fixture.createService(), fixture.createInput());
        EXECUTED_SERVICES.add(fixture.createService());
        assertNotNull(find(fixture.entity(), fixture.primaryKeys()));

        call(fixture.updateService(), fixture.updateInput());
        EXECUTED_SERVICES.add(fixture.updateService());
        Map<String, Object> updated = entities.toMap(find(fixture.entity(), fixture.primaryKeys()));
        fixture.updateInput().forEach((name, expected) -> {
            if (!fixture.primaryKeys().containsKey(name)) {
                Object actual = updated.get(name);
                if (expected instanceof BigDecimal expectedNumber) {
                    assertEquals(0, expectedNumber.compareTo(new BigDecimal(actual.toString())));
                } else {
                    assertEquals(expected.toString(), actual.toString());
                }
            }
        });

        if (fixture.deleteService() != null) {
            call(fixture.deleteService(), fixture.deleteInput());
            EXECUTED_SERVICES.add(fixture.deleteService());
            assertNull(find(fixture.entity(), fixture.primaryKeys()));
        }
    }

    @Test
    void ac02_inoutPrimaryKeysAreGeneratedOrPreserved() {
        Map<String, Object> generated = call("createMrpEventType", Map.of(
                "mrpEventTypeId", "", "description", "Generated event type"));
        String generatedId = String.valueOf(generated.get("mrpEventTypeId"));
        assertNotNull(find("MrpEventType", Map.of("mrpEventTypeId", generatedId)));

        Map<String, Object> specified = call("createMrpEventType", Map.of(
                "mrpEventTypeId", "AC02_INOUT_SPECIFIED", "description", "Specified event type"));
        assertEquals("AC02_INOUT_SPECIFIED", specified.get("mrpEventTypeId"));
        assertNotNull(find("MrpEventType", Map.of("mrpEventTypeId", "AC02_INOUT_SPECIFIED")));
    }

    @AfterAll
    static void ac02_allCatalogServicesWereExecuted() {
        assertEquals(ServiceCatalog.all().keySet(), EXECUTED_SERVICES);
    }

    private static Stream<String> entityFamilies() {
        return Stream.of("calendar", "calendarExceptionDay", "calendarExceptionWeek", "calendarWeek",
                "mrpEventType", "mrpRunLog", "workEffortInventoryReservation");
    }

    private static long countEntityAutoServices(java.nio.file.Path path) {
        try {
            return ENTITY_AUTO_SERVICE.matcher(java.nio.file.Files.readString(path)).results().count();
        } catch (IOException exception) {
            throw new java.io.UncheckedIOException(exception);
        }
    }

    private Fixture fixture(String family, int sequence) {
        String suffix = String.valueOf(sequence);
        String calendarId = "AC02_AUTO_CAL_" + suffix;
        Timestamp timestamp = Timestamp.from(Instant.parse("2030-01-01T00:00:00Z").plusSeconds(sequence));
        Date date = Date.valueOf("2030-01-01");
        return switch (family) {
            case "calendar" -> new Fixture("TechDataCalendar", "createCalendar",
                    Map.of("calendarId", calendarId, "description", "before"),
                    Map.of("calendarId", calendarId),
                    "updateCalendar", Map.of("calendarId", calendarId, "description", "after"),
                    null, Map.of(), null);
            case "calendarExceptionDay" -> new Fixture("TechDataCalendarExcDay", "createCalendarExceptionDay",
                    Map.of("calendarId", calendarId, "exceptionDateStartTime", timestamp, "description", "before"),
                    Map.of("calendarId", calendarId, "exceptionDateStartTime", timestamp),
                    "updateCalendarExceptionDay",
                    Map.of("calendarId", calendarId, "exceptionDateStartTime", timestamp, "description", "after"),
                    "removeCalendarExceptionDay",
                    Map.of("calendarId", calendarId, "exceptionDateStartTime", timestamp), calendarId);
            case "calendarExceptionWeek" -> new Fixture("TechDataCalendarExcWeek", "createCalendarExceptionWeek",
                    Map.of("calendarId", calendarId, "exceptionDateStart", date, "description", "before"),
                    Map.of("calendarId", calendarId, "exceptionDateStart", date),
                    "updateCalendarExceptionWeek",
                    Map.of("calendarId", calendarId, "exceptionDateStart", date, "description", "after"),
                    "removeCalendarExceptionWeek",
                    Map.of("calendarId", calendarId, "exceptionDateStart", date), calendarId);
            case "calendarWeek" -> new Fixture("TechDataCalendarWeek", "createCalendarWeek",
                    Map.of("calendarWeekId", "AC02_AUTO_WEEK_" + suffix, "description", "before"),
                    Map.of("calendarWeekId", "AC02_AUTO_WEEK_" + suffix),
                    "updateCalendarWeek",
                    Map.of("calendarWeekId", "AC02_AUTO_WEEK_" + suffix, "description", "after"),
                    null, Map.of(), null);
            case "mrpEventType" -> new Fixture("MrpEventType", "createMrpEventType",
                    Map.of("mrpEventTypeId", "AC02_AUTO_EVENT_" + suffix, "description", "before"),
                    Map.of("mrpEventTypeId", "AC02_AUTO_EVENT_" + suffix),
                    "updateMrpEventType",
                    Map.of("mrpEventTypeId", "AC02_AUTO_EVENT_" + suffix, "description", "after"),
                    "deleteMrpEventType", Map.of("mrpEventTypeId", "AC02_AUTO_EVENT_" + suffix), null);
            case "mrpRunLog" -> new Fixture("MrpRunLog", "createMrpRunLog",
                    Map.of("mrpRunLogId", "AC02_AUTO_MRP_" + suffix),
                    Map.of("mrpRunLogId", "AC02_AUTO_MRP_" + suffix),
                    "updateMrpRunLog", Map.of("mrpRunLogId", "AC02_AUTO_MRP_" + suffix, "mrpName", "after"),
                    null, Map.of(), null);
            case "workEffortInventoryReservation" -> {
                Map<String, Object> keys = new LinkedHashMap<>();
                keys.put("inventoryItemId", "AC02_AUTO_INV_" + suffix);
                keys.put("productId", "MFG_FG");
                keys.put("workEffortId", "MFG_TASK_10");
                yield new Fixture("WorkEffortInvRes", "createWorkEffortInvRes",
                        Map.of("inventoryItemId", "AC02_AUTO_INV_" + suffix, "productId", "MFG_FG",
                                "workEffortId", "MFG_TASK_10", "quantity", BigDecimal.ONE),
                        keys,
                        "updateWorkEffortInvRes",
                        Map.of("inventoryItemId", "AC02_AUTO_INV_" + suffix, "productId", "MFG_FG",
                                "workEffortId", "MFG_TASK_10", "quantity", new BigDecimal("2")),
                        "deleteWorkEffortInvRes", keys, null);
            }
            default -> throw new IllegalArgumentException("Unknown entity family " + family);
        };
    }

    private Map<String, Object> call(String service, Map<String, Object> input) {
        return dispatcher.execute("manufacturing", service, input);
    }

    private Object find(String entity, Map<String, ?> keys) {
        return entities.findByPrimaryKey(entities.entityClass(entity), keys);
    }

    private record Fixture(
            String entity,
            String createService,
            Map<String, Object> createInput,
            Map<String, Object> primaryKeys,
            String updateService,
            Map<String, Object> updateInput,
            String deleteService,
            Map<String, Object> deleteInput,
            String prepareCalendarId) {}
}
