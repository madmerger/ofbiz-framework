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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.manufacturing.generated.ServiceCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
@WithMockUser(authorities = {"OFBTOOLS_VIEW", "MANUFACTURING_ADMIN", "MANUFACTURING_SENSITIVE_VIEW"})
class ManufacturingAcceptanceTest {
    @Autowired
    private MigrationServiceDispatcher dispatcher;
    @Autowired
    private EntityRegistry entities;
    @Autowired
    private ProductPort products;
    @Autowired
    private WorkEffortPort workEfforts;

    @Test
    void ac01_catalogContainsAllEntityAutoServices() {
        long count = ServiceCatalog.all().values().stream()
                .filter(definition -> definition.entity() != null && !definition.entity().isBlank())
                .filter(definition -> definition.invoke() != null && !definition.invoke().isBlank())
                .count();
        assertEquals(18, count);
    }

    @Test
    void ac02_calendarEntityAutoSupportsCreateUpdateAndDelete() {
        call("createCalendar", Map.of("calendarId", "AC02_CAL", "description", "initial"));
        call("updateCalendar", Map.of("calendarId", "AC02_CAL", "description", "updated"));
        Map<String, Object> calendar = entities.toMap(entities.findByPrimaryKey(
                entities.entityClass("TechDataCalendar"), Map.of("calendarId", "AC02_CAL")));
        assertEquals("updated", calendar.get("description"));

        Timestamp date = Timestamp.from(Instant.parse("2025-01-01T00:00:00Z"));
        call("createCalendarExceptionDay", Map.of(
                "calendarId", "AC02_CAL", "exceptionDateStartTime", date, "description", "holiday"));
        call("removeCalendarExceptionDay", Map.of("calendarId", "AC02_CAL", "exceptionDateStartTime", date));
        assertTrue(entities.findByPrimaryKey(entities.entityClass("TechDataCalendarExcDay"),
                Map.of("calendarId", "AC02_CAL", "exceptionDateStartTime", date)) == null);
    }

    @Test
    void ac02_updateRejectsUndeclaredInput() {
        call("createCalendar", Map.of("calendarId", "AC02_FIELDS", "description", "original"));
        MigrationException exception = assertThrows(MigrationException.class,
                () -> call("updateCalendar", Map.of(
                        "calendarId", "AC02_FIELDS", "description", "changed", "createdStamp", "unexpected")));
        assertEquals(400, exception.getStatus().value());
    }

    @Test
    void ac02_createMrpEventTypeGeneratesOrPreservesInoutPrimaryKey() {
        Map<String, Object> generated = call("createMrpEventType", Map.of(
                "mrpEventTypeId", "", "description", "Generated event type"));
        String generatedId = String.valueOf(generated.get("mrpEventTypeId"));
        assertFalse(generatedId.isBlank());
        assertFalse(entities.findByPrimaryKey(entities.entityClass("MrpEventType"),
                Map.of("mrpEventTypeId", generatedId)) == null);

        Map<String, Object> specified = call("createMrpEventType", Map.of(
                "mrpEventTypeId", "AC02_CUSTOM_EVENT", "description", "Specified event type"));
        assertEquals("AC02_CUSTOM_EVENT", specified.get("mrpEventTypeId"));
        assertFalse(entities.findByPrimaryKey(entities.entityClass("MrpEventType"),
                Map.of("mrpEventTypeId", "AC02_CUSTOM_EVENT")) == null);
    }

    @Test
    void ac03_simpleFormulaAndBomValidationMatchTheContract() {
        Map<String, Object> formula = call("linearComponentFormula", Map.of("arguments", Map.of(
                "neededQuantity", new BigDecimal("2"),
                "amount", new BigDecimal("5"),
                "width", new BigDecimal("3"))));
        assertEquals(new BigDecimal("4"), formula.get("quantity"));
        MigrationException selfReference = assertThrows(MigrationException.class,
                () -> call("createBOMAssoc", Map.of("productId", "MFG_FG",
                        "productIdTo", "MFG_FG", "productAssocTypeId", "MANUF_COMPONENT")));
        assertEquals(400, selfReference.getStatus().value());
        Map<String, Object> example = call("exampleComponentFormula", Map.of(
                "arguments", Map.of("neededQuantity", new BigDecimal("3"))));
        assertEquals(new BigDecimal("30"), example.get("quantity"));
    }

    @Test
    void ac04_productionRunCompletesAfterItsTasks() {
        call("createBOMAssoc", Map.of(
                "productId", "MFG_FG",
                "productIdTo", "MFG_RAW1",
                "productAssocTypeId", "MANUF_COMPONENT",
                "fromDate", Timestamp.from(Instant.now()),
                "quantity", new BigDecimal("2"),
                "scrapFactor", BigDecimal.ONE));
        Map<String, Object> created = call("createProductionRun", Map.of(
                "productId", "MFG_FG", "quantityToProduce", new BigDecimal("2"), "facilityId", "MFG_PLANT"));
        String runId = String.valueOf(created.get("productionRunId"));
        call("changeProductionRunStatus", Map.of("productionRunId", runId, "statusId", "PRUN_DOC_PRINTED"));
        List<Map<String, Object>> tasks = workEfforts.findChildTasks(runId);
        assertEquals(2, tasks.size());
        for (Map<String, Object> task : tasks) {
            String taskId = String.valueOf(task.get("workEffortId"));
            call("changeProductionRunTaskStatus", Map.of(
                    "productionRunId", runId, "workEffortId", taskId, "statusId", "PRUN_RUNNING"));
            call("updateProductionRunTask", Map.of(
                    "workEffortId", taskId, "addQuantityProduced", BigDecimal.ONE));
            call("changeProductionRunTaskStatus", Map.of(
                    "productionRunId", runId, "workEffortId", taskId, "statusId", "PRUN_COMPLETED"));
        }
        Map<String, Object> header = workEfforts.findWorkEffort(runId);
        assertEquals("PRUN_COMPLETED", header.get("currentStatusId"));
    }

    @Test
    void ac06_multiDuplicateReturnsConflictAndRollsBackEarlierRows() {
        Timestamp from = Timestamp.from(Instant.now());
        List<Map<String, Object>> rows = List.of(
                Map.of("calendarId", "AC06_FIRST", "description", "should rollback"),
                Map.of("calendarId", "AC06_FIRST", "description", "duplicate"));
        MigrationServiceDispatcher.MultiServiceException exception = assertThrows(
                MigrationServiceDispatcher.MultiServiceException.class,
                () -> dispatcher.executeMulti("manufacturing", "createCalendar", rows));
        assertFalse(exception.getErrors().isEmpty());
        assertTrue(exception.getErrors().stream().anyMatch(error ->
                String.valueOf(error.get("errorMessage")).contains("ServiceValueFound")));
        assertTrue(entities.findByPrimaryKey(entities.entityClass("TechDataCalendar"),
                Map.of("calendarId", "AC06_FIRST")) == null);
    }

    private Map<String, Object> call(String service, Map<String, Object> input) {
        return dispatcher.execute("manufacturing", service, input);
    }
}
