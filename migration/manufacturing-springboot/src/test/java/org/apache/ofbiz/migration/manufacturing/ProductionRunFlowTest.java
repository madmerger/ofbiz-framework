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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
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
class ProductionRunFlowTest {
    @Autowired
    private MigrationServiceDispatcher dispatcher;
    @Autowired
    private WorkEffortPort workEfforts;

    @Test
    void ac04_createProductionRunRejectsMissingProduct() {
        MigrationException missing = assertThrows(MigrationException.class, () -> createRun("AC04_MISSING_PRODUCT"));
        assertEquals(400, missing.getStatus().value());
    }

    @Test
    void ac04_createProductionRunRejectsMissingRouting() {
        MigrationException missing = assertThrows(MigrationException.class, () -> createRun("MFG_RAW1"));
        assertEquals(400, missing.getStatus().value());
    }

    @Test
    void ac04_createProductionRunRequiresQuantity() {
        Timestamp start = Timestamp.from(Instant.now());
        MigrationException missing = assertThrows(MigrationException.class, () -> call("createProductionRun", Map.of(
                "productId", "MFG_FG", "startDate", start, "facilityId", "MFG_PLANT")));
        assertEquals(400, missing.getStatus().value());
    }

    @Test
    void ac04_taskCannotStartWhileRunIsCreated() {
        Map<String, Object> run = createRun("MFG_FG");
        String runId = String.valueOf(run.get("productionRunId"));
        String taskId = taskIds(runId).get(0);
        MigrationException rejected = assertThrows(MigrationException.class, () -> call(
                "changeProductionRunTaskStatus", Map.of(
                        "productionRunId", runId, "productionRunTaskId", taskId, "statusId", "PRUN_RUNNING")));
        assertEquals(400, rejected.getStatus().value());
    }

    @Test
    void ac04_laterTaskCannotStartBeforeEarlierTaskCompletes() {
        Map<String, Object> run = createRun("MFG_FG");
        String runId = String.valueOf(run.get("productionRunId"));
        List<String> tasks = taskIds(runId);
        printRun(runId);
        MigrationException rejected = assertThrows(MigrationException.class, () -> call(
                "changeProductionRunTaskStatus", Map.of(
                        "productionRunId", runId, "productionRunTaskId", tasks.get(1), "statusId", "PRUN_RUNNING")));
        assertEquals(400, rejected.getStatus().value());
    }

    @Test
    void ac04_taskQuantityCanOnlyBeRecordedWhileRunning() {
        Map<String, Object> run = createRun("MFG_FG");
        String runId = String.valueOf(run.get("productionRunId"));
        String taskId = taskIds(runId).get(0);
        MigrationException rejected = assertThrows(MigrationException.class, () -> call("updateProductionRunTask",
                Map.of("productionRunId", runId, "productionRunTaskId", taskId,
                        "addQuantityProduced", BigDecimal.ONE)));
        assertEquals(400, rejected.getStatus().value());
    }

    @Test
    void ac04_taskMustBelongToRequestedRun() {
        Map<String, Object> firstRun = createRun("MFG_FG");
        Map<String, Object> secondRun = createRun("MFG_FG");
        String firstRunId = String.valueOf(firstRun.get("productionRunId"));
        String secondTaskId = taskIds(String.valueOf(secondRun.get("productionRunId"))).get(0);
        MigrationException rejected = assertThrows(MigrationException.class, () -> call(
                "changeProductionRunTaskStatus", Map.of(
                        "productionRunId", firstRunId, "productionRunTaskId", secondTaskId,
                        "statusId", "PRUN_RUNNING")));
        assertEquals(400, rejected.getStatus().value());
    }

    @Test
    void ac04_nonRunningTaskCannotBeCompleted() {
        Map<String, Object> run = createRun("MFG_FG");
        String runId = String.valueOf(run.get("productionRunId"));
        String taskId = taskIds(runId).get(0);
        printRun(runId);
        MigrationException rejected = assertThrows(MigrationException.class, () -> call(
                "changeProductionRunTaskStatus", Map.of(
                        "productionRunId", runId, "productionRunTaskId", taskId, "statusId", "PRUN_COMPLETED")));
        assertEquals(400, rejected.getStatus().value());
    }

    @Test
    void ac04_disallowedRunTransitionReturnsCurrentStatus() {
        Map<String, Object> run = createRun("MFG_FG");
        String runId = String.valueOf(run.get("productionRunId"));
        Map<String, Object> response = call("changeProductionRunStatus",
                Map.of("productionRunId", runId, "statusId", "PRUN_CLOSED"));
        assertEquals("PRUN_CREATED", response.get("newStatusId"));
        assertEquals("PRUN_CREATED", workEfforts.findWorkEffort(runId).get("currentStatusId"));
    }

    @Test
    void ac04_runCannotStartUntilRequiredPrecedenceIsComplete() {
        Map<String, Object> run = createRun("MFG_FG");
        String runId = String.valueOf(run.get("productionRunId"));
        String predecessorId = "AC04_PREDECESSOR";
        workEfforts.createWorkEffort(Map.of(
                "workEffortId", predecessorId, "workEffortTypeId", "PROD_ORDER_HEADER",
                "currentStatusId", "PRUN_CREATED"));
        workEfforts.createWorkEffortAssoc(Map.of(
                "workEffortIdFrom", predecessorId, "workEffortIdTo", runId,
                "workEffortAssocTypeId", "WORK_EFF_PRECEDENCY", "fromDate", Timestamp.from(Instant.now())));
        printRun(runId);
        MigrationException rejected = assertThrows(MigrationException.class, () -> call(
                "changeProductionRunStatus", Map.of("productionRunId", runId, "statusId", "PRUN_RUNNING")));
        assertEquals(400, rejected.getStatus().value());

        workEfforts.updateWorkEffort(predecessorId, Map.of("currentStatusId", "PRUN_COMPLETED"));
        Map<String, Object> started = call("changeProductionRunStatus",
                Map.of("productionRunId", runId, "statusId", "PRUN_RUNNING"));
        assertEquals("PRUN_RUNNING", started.get("newStatusId"));
    }

    private Map<String, Object> createRun(String productId) {
        return call("createProductionRun", Map.of(
                "productId", productId,
                "pRQuantity", BigDecimal.ONE,
                "startDate", Timestamp.from(Instant.now()),
                "facilityId", "MFG_PLANT"));
    }

    private void printRun(String runId) {
        call("changeProductionRunStatus", Map.of("productionRunId", runId, "statusId", "PRUN_DOC_PRINTED"));
    }

    private List<String> taskIds(String runId) {
        return workEfforts.findChildTasks(runId).stream()
                .map(task -> String.valueOf(task.get("workEffortId"))).toList();
    }

    private Map<String, Object> call(String service, Map<String, Object> input) {
        return dispatcher.execute("manufacturing", service, input);
    }
}
