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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
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
        Timestamp start = Timestamp.from(Instant.now());
        call("createBOMAssoc", Map.of(
                "productId", "MFG_FG",
                "productIdTo", "MFG_RAW1",
                "productAssocTypeId", "MANUF_COMPONENT",
                "fromDate", start,
                "quantity", new BigDecimal("2"),
                "scrapFactor", new BigDecimal("10")));
        call("createBOMAssoc", Map.of(
                "productId", "MFG_FG",
                "productIdTo", "MFG_RAW2",
                "productAssocTypeId", "MANUF_COMPONENT",
                "fromDate", start,
                "quantity", new BigDecimal("3"),
                "scrapFactor", new BigDecimal("-10"),
                "routingWorkEffortId", "MFG_TASK_20"));
        Map<String, Object> created = call("createProductionRun", Map.of(
                "productId", "MFG_FG", "pRQuantity", new BigDecimal("2"),
                "startDate", start, "facilityId", "MFG_PLANT"));
        String runId = String.valueOf(created.get("productionRunId"));
        call("changeProductionRunStatus", Map.of("productionRunId", runId, "statusId", "PRUN_DOC_PRINTED"));
        List<Map<String, Object>> tasks = workEfforts.findChildTasks(runId);
        assertEquals(2, tasks.size());
        long expectedDuration = tasks.stream().mapToLong(task ->
                new BigDecimal(task.get("estimatedSetupMillis").toString()).longValue()
                        + new BigDecimal(task.get("estimatedMilliSeconds").toString())
                                .multiply(new BigDecimal(task.get("quantityToProduce").toString())).longValue())
                .sum();
        assertEquals(new Timestamp(start.getTime() + expectedDuration), created.get("estimatedCompletionDate"));
        assertEquals("Preparation", tasks.get(0).get("workEffortName"));
        assertEquals("Assembly", tasks.get(1).get("workEffortName"));
        assertEquals(start, tasks.get(0).get("estimatedStartDate"));
        assertEquals(tasks.get(0).get("estimatedCompletionDate"), tasks.get(1).get("estimatedStartDate"));
        String firstTaskId = String.valueOf(tasks.get(0).get("workEffortId"));
        String secondTaskId = String.valueOf(tasks.get(1).get("workEffortId"));
        assertEquals("MFG_TASK_10", workEfforts.findWorkEffortAssocs(Map.of(
                "workEffortIdTo", firstTaskId, "workEffortAssocTypeId", "WORK_EFF_TEMPLATE"))
                .get(0).get("workEffortIdFrom"));
        assertEquals("MFG_TASK_20", workEfforts.findWorkEffortAssocs(Map.of(
                "workEffortIdTo", secondTaskId, "workEffortAssocTypeId", "WORK_EFF_TEMPLATE"))
                .get(0).get("workEffortIdFrom"));
        assertEquals(0, new BigDecimal("4.4").compareTo(new BigDecimal(workEfforts.findWorkEffortGoodStandards(Map.of(
                "workEffortId", firstTaskId, "workEffortGoodStdTypeId", "PRUNT_PROD_NEEDED"))
                .get(0).get("estimatedQuantity").toString())));
        assertEquals(0, new BigDecimal("5.4").compareTo(new BigDecimal(workEfforts.findWorkEffortGoodStandards(Map.of(
                "workEffortId", secondTaskId, "workEffortGoodStdTypeId", "PRUNT_PROD_NEEDED"))
                .get(0).get("estimatedQuantity").toString())));

        call("changeProductionRunTaskStatus", Map.of(
                "productionRunId", runId, "productionRunTaskId", firstTaskId, "statusId", "PRUN_RUNNING"));
        call("updateProductionRunTask", Map.of(
                "productionRunId", runId, "productionRunTaskId", firstTaskId,
                "addQuantityProduced", BigDecimal.ONE));
        call("changeProductionRunTaskStatus", Map.of(
                "productionRunId", runId, "productionRunTaskId", firstTaskId, "statusId", "PRUN_COMPLETED"));
        call("changeProductionRunTaskStatus", Map.of(
                "productionRunId", runId, "productionRunTaskId", secondTaskId, "statusId", "PRUN_RUNNING"));
        call("updateProductionRunTask", Map.of(
                "productionRunId", runId, "productionRunTaskId", secondTaskId,
                "addQuantityProduced", BigDecimal.ONE));
        call("changeProductionRunTaskStatus", Map.of(
                "productionRunId", runId, "productionRunTaskId", secondTaskId, "statusId", "PRUN_COMPLETED"));
        Map<String, Object> header = workEfforts.findWorkEffort(runId);
        assertEquals("PRUN_COMPLETED", header.get("currentStatusId"));
        call("changeProductionRunStatus", Map.of("productionRunId", runId, "statusId", "PRUN_CLOSED"));
        assertEquals("PRUN_CLOSED", workEfforts.findWorkEffort(runId).get("currentStatusId"));
        assertTrue(workEfforts.findChildTasks(runId).stream()
                .allMatch(task -> "PRUN_CLOSED".equals(task.get("currentStatusId"))));
    }

    private Map<String, Object> call(String service, Map<String, Object> input) {
        return dispatcher.execute("manufacturing", service, input);
    }
}
