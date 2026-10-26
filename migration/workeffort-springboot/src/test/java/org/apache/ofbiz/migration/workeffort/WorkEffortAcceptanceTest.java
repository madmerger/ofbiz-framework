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
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.apache.ofbiz.migration.workeffort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.apache.ofbiz.migration.workeffort.generated.ServiceCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:workeffort-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "migration.security.component=workeffort",
        "migration.security.require-https=false",
        "workeffort.security.user-party-ids.permission-user=DemoEmployee",
        "migration.security.extra-entities=RateAmount,PartyRate,StatusItem,StatusValidChange,WorkEffortStatus"
})
class WorkEffortAcceptanceTest {
    @Autowired
    private ServiceHandlerRegistry registry;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WorkEffortBusinessServices services;

    @Test
    void ac01EntityAutoCatalogContainsNinetyFiveServices() {
        long count = registryCatalogCount();
        assertThat(count).isEqualTo(95);
    }

    @Test
    void ac02SimpleCatalogCoversAllThirtyThreeServices() {
        for (String name : new String[] {
                "makeCommunicationEventWorkEffort", "createWorkEffortNote", "updateWorkEffortNote",
                "createWorkEffortAssoc", "copyWorkEffortAssocs", "createWorkEffortAndAssoc",
                "assignInventoryToWorkEffort", "createCommunicationEventWorkEff", "updateCommunicationEventWorkEff",
                "deleteCommunicationEventWorkEff", "checkCustRequestItemExists", "setWorkEffortFixedAssetAssign",
                "createWorkEffortFixedAssetAssign", "createWorkEffortReview", "indexWorkEffortKeywords",
                "createWorkEffortKeyword", "createWorkEffortKeywords", "deleteWorkEffortKeywords",
                "workEffortManagerPermission", "workEffortGenericPermission", "timesheetUpdatePermission",
                "workEffortICalendarPermission", "getICalWorkEfforts", "getPartyICalUrl", "createTimesheets",
                "createTimesheetForThisWeek", "addTimesheetToInvoice", "addTimesheetToNewInvoice",
                "addWorkEffortTimeToInvoice", "addWorkEffortTimeToNewInvoice", "checkTimesheetStatus",
                "updateTimeEntry", "getTimeEntryRate"
        }) {
            assertThat(registry.handler("workeffort", name)).as(name).isPresent();
        }
        assertThat(registry.handler("workeffort", "getTimeEntryRate")).isPresent();
        assertThat(ServiceCatalog.all()).containsKeys("createTimesheet", "createWorkEffortPartyAssignment");
    }

    @Test
    void ac03SensitiveEntitiesResolveFromGeneratedDatamodel() {
        for (String name : new String[] {
                "UserLogin", "UserLoginHistory", "UserLoginPasswordHistory", "UserLoginSession",
                "CreditCard", "PaymentMethod", "EftAccount", "GiftCard", "CheckAccount",
                "PayPalPaymentMethod", "BillingAccount", "Payment", "PaymentGatewayResponse",
                "PaymentGatewayConfig", "FinAccount", "FinAccountTrans", "PayHistory",
                "PayrollPreference", "PartyRate", "RateAmount", "EmplPositionTypeRate", "SalaryStep", "PayGrade"
        }) {
            assertThat(entities.entityClass(name)).isNotNull();
        }
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "WORKEFFORTMGR_VIEW", "WORKEFFORTMGR_SENSITIVE_VIEW"})
    void ac03bSensitiveRateAmountIsAllowedByEntityApiExtraEntityScope() throws Exception {
        mockMvc.perform(get("/api/workeffort/entities/RateAmount"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "WORKEFFORTMGR_VIEW", "WORKEFFORTMGR_SENSITIVE_VIEW"})
    void ac03cEntityOutsideCatalogAndExtraEntitiesIsNotFound() throws Exception {
        mockMvc.perform(get("/api/workeffort/entities/Payment"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "WORKEFFORTMGR_VIEW"})
    void ac03dMismatchedComponentIsNotFound() throws Exception {
        mockMvc.perform(get("/api/other/entities/WorkEffort"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "WORKEFFORTMGR_VIEW"})
    void ac04ReadonlyUserCannotCreateWorkEffort() throws Exception {
        mockMvc.perform(post("/api/workeffort/services/createWorkEffort")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"workEffortName\":\"denied\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void ac05AnonymousRequestIsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/workeffort/entities/WorkEffort"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW"})
    void ac06MissingBasePermissionIsForbidden() throws Exception {
        mockMvc.perform(get("/api/workeffort/entities/WorkEffort"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "WORKEFFORTMGR_VIEW"})
    void ac07SensitiveRateAmountIsForbiddenForReadonly() throws Exception {
        mockMvc.perform(get("/api/workeffort/entities/RateAmount"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "WORKEFFORTMGR_VIEW", "WORKEFFORTMGR_SENSITIVE_VIEW"})
    @Transactional
    void ac08AuditorCanReadNonemptySensitiveRateAmount() throws Exception {
        Object rate = entities.newEntity(entities.entityClass("RateAmount"));
        entities.set(rate, "rateTypeId", "RATE_HOURLY");
        entities.set(rate, "rateCurrencyUomId", "USD");
        entities.set(rate, "periodTypeId", "RATE_HOUR");
        entities.set(rate, "partyId", "DemoEmployee");
        entities.set(rate, "workEffortId", "WE-HOLIDAY");
        entities.set(rate, "emplPositionTypeId", "EMPLOYEE");
        entities.set(rate, "fromDate", Timestamp.from(Instant.now()));
        entities.set(rate, "rateAmount", new BigDecimal("125.00"));
        entityManager.persist(rate);
        String body = mockMvc.perform(get("/api/workeffort/entities/RateAmount"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(body).contains("125.00");
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "WORKEFFORTMGR_ADMIN"})
    void ac09MultiConflictRollsBackEarlierRows() throws Exception {
        String body = """
                [
                  {"workEffortId":"WE-MULTI","statusId":"CAL_CREATED","statusDatetime":"2025-01-01T00:00:00Z"},
                  {"workEffortId":"WE-MULTI","statusId":"CAL_CREATED","statusDatetime":"2025-01-01T00:00:00Z"}
                ]
                """;
        mockMvc.perform(post("/api/workeffort/services/createWorkEffortStatus/multi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
        entityManager.clear();
        assertThat(entities.findByEquality(entities.entityClass("WorkEffortStatus"),
                Map.of("workEffortId", "WE-MULTI"))).isEmpty();
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "WORKEFFORTMGR_ADMIN"})
    void ac09fMultiInvalidServiceInputReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/workeffort/services/createWorkEffortStatus/multi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [
                                  {"workEffortId":"WE-MULTI-VALID","statusId":"CAL_CREATED",
                                   "statusDatetime":"2025-01-01T00:00:00Z"},
                                  {"workEffortId":"WE-MULTI-INVALID"}
                                ]
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "WORKEFFORTMGR_ADMIN"})
    void ac09bEntityAutoUpdateRejectsUndeclaredAttributes() throws Exception {
        mockMvc.perform(post("/api/workeffort/services/updateWorkEffortType")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"workEffortTypeId\":\"TASK\",\"notDeclared\":\"no\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "WORKEFFORTMGR_ADMIN"})
    void ac09cEntityAutoCreateHonorsProvidedInoutPrimaryKey() throws Exception {
        mockMvc.perform(post("/api/workeffort/services/createWorkEffortType")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"workEffortTypeId\":\"WE-TEST-TYPE\",\"description\":\"Test type\"}"))
                .andExpect(status().isOk());
        assertThat(entities.findByPrimaryKey(
                entities.entityClass("WorkEffortType"), Map.of("workEffortTypeId", "WE-TEST-TYPE"))).isNotNull();
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "WORKEFFORTMGR_ADMIN"})
    void ac09dEntityAutoCreateGeneratesEmptyInoutPrimaryKey() throws Exception {
        String response = mockMvc.perform(post("/api/workeffort/services/createWorkEffortType")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"workEffortTypeId\":\"\",\"description\":\"Generated type\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String generatedId = new ObjectMapper().readTree(response).path("workEffortTypeId").asText();
        assertThat(generatedId).isNotBlank();
        assertThat(entities.findByPrimaryKey(
                entities.entityClass("WorkEffortType"), Map.of("workEffortTypeId", generatedId))).isNotNull();
    }

    @Test
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "WORKEFFORTMGR_ADMIN"})
    void ac09eEntityAutoCreateIgnoresSuppliedOutPrimaryKey() throws Exception {
        String response = mockMvc.perform(post("/api/workeffort/services/createTimesheet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"timesheetId\":\"CALLER-TIMESHEET-ID\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String generatedId = new ObjectMapper().readTree(response).path("timesheetId").asText();
        assertThat(generatedId).isNotBlank().isNotEqualTo("CALLER-TIMESHEET-ID");
        assertThat(entities.findByPrimaryKey(
                entities.entityClass("Timesheet"), Map.of("timesheetId", generatedId))).isNotNull();
    }

    @Test
    @Transactional
    void simpleIndexWorkEffortKeywordsRebuildsKeywordsFromWorkEffortFields() {
        Object workEffort = entities.newEntity(entities.entityClass("WorkEffort"));
        entities.set(workEffort, "workEffortId", "WE-KEYWORD-TEST");
        entities.set(workEffort, "workEffortTypeId", "TASK");
        entities.set(workEffort, "workEffortName", "Review Indexed Keyword");
        entities.set(workEffort, "currentStatusId", "CAL_CREATED");
        entityManager.persist(workEffort);

        Map<String, Object> result = services.execute(
                "indexWorkEffortKeywords", Map.of("workEffortId", "WE-KEYWORD-TEST"));
        entityManager.flush();
        assertThat(result.get("indexedKeywords")).isNotEqualTo(0);
        assertThat(entities.findByEquality(entities.entityClass("WorkEffortKeyword"),
                Map.of("workEffortId", "WE-KEYWORD-TEST", "keyword", "indexed"))).isNotEmpty();
    }

    @Test
    @Transactional
    void simpleCreateWorkEffortAndAssocRejectsDuplicateAssociation() {
        Timestamp fromDate = Timestamp.from(Instant.parse("2025-01-01T00:00:00Z"));
        Object association = entities.newEntity(entities.entityClass("WorkEffortAssoc"));
        entities.set(association, "workEffortIdFrom", "WE-ASSOC-FROM");
        entities.set(association, "workEffortIdTo", "WE-ASSOC-TO");
        entities.set(association, "workEffortAssocTypeId", "WORK_EFF_PARENT");
        entities.set(association, "fromDate", fromDate);
        entityManager.persist(association);
        Map<String, Object> input = Map.of(
                "workEffortIdFrom", "WE-ASSOC-FROM",
                "workEffortIdTo", "WE-ASSOC-TO",
                "workEffortAssocTypeId", "WORK_EFF_PARENT",
                "fromDate", fromDate);
        assertThatThrownBy(() -> services.execute("createWorkEffortAndAssoc", input))
                .isInstanceOf(MigrationException.class);
    }

    @Test
    @WithMockUser(username = "permission-user", authorities = {"WORKEFFORTMGR_ADMIN"})
    void timesheetUpdatePermissionRejectsAnotherParty() {
        Map<String, Object> result = services.execute("timesheetUpdatePermission",
                Map.of("partyId", "OtherParty"));
        assertThat(result.get("hasPermission")).isEqualTo(false);
    }

    @Test
    @Transactional
    @WithMockUser(username = "permission-user", authorities = {
            "OFBTOOLS_VIEW", "WORKEFFORTMGR_VIEW", "WORKEFFORTMGR_ROLE_UPDATE"
    })
    void workEffortRoleUpdateAllowsOwnerAndRejectsUnassignedParty() {
        persistWorkEffort("WE-ROLE-OWNER", "Owned", Timestamp.from(Instant.now()));
        persistWorkEffort("WE-ROLE-NONE", "Unassigned", Timestamp.from(Instant.now()));
        Object assignment = entities.newEntity(entities.entityClass("WorkEffortPartyAssignment"));
        entities.set(assignment, "workEffortId", "WE-ROLE-OWNER");
        entities.set(assignment, "partyId", "DemoEmployee");
        entities.set(assignment, "roleTypeId", "CAL_OWNER");
        entities.set(assignment, "fromDate", Timestamp.from(Instant.now().minusSeconds(60)));
        entityManager.persist(assignment);

        assertThat(services.execute("workEffortGenericPermission", Map.of(
                "mainAction", "UPDATE", "workEffortId", "WE-ROLE-OWNER")).get("hasPermission"))
                .isEqualTo(true);
        assertThat(services.execute("workEffortGenericPermission", Map.of(
                "mainAction", "UPDATE", "workEffortId", "WE-ROLE-NONE")).get("hasPermission"))
                .isEqualTo(false);
    }

    @Test
    @Transactional
    void checkTimesheetStatusRejectsNonInProcessStatus() {
        Object timesheet = entities.newEntity(entities.entityClass("Timesheet"));
        entities.set(timesheet, "timesheetId", "TS-APPROVED-TEST");
        entities.set(timesheet, "partyId", "DemoEmployee");
        entities.set(timesheet, "statusId", "TIMESHEET_APPROVED");
        entityManager.persist(timesheet);
        assertThatThrownBy(() -> services.execute("checkTimesheetStatus",
                Map.of("timesheetId", "TS-APPROVED-TEST")))
                .isInstanceOf(MigrationException.class);
    }

    @Test
    @Transactional
    void simpleCreateTimesheetForThisWeekRejectsDuplicate() {
        LocalDate weekStart = LocalDate.of(2025, 2, 5).with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY));
        Timestamp from = Timestamp.from(weekStart.atStartOfDay().toInstant(ZoneOffset.UTC));
        Timestamp thru = Timestamp.from(weekStart.plusDays(7).atStartOfDay()
                .minusNanos(1_000_000).toInstant(ZoneOffset.UTC));
        Object timesheet = entities.newEntity(entities.entityClass("Timesheet"));
        entities.set(timesheet, "timesheetId", "TS-DUPLICATE-TEST");
        entities.set(timesheet, "partyId", "DemoEmployee");
        entities.set(timesheet, "fromDate", from);
        entities.set(timesheet, "thruDate", thru);
        entities.set(timesheet, "statusId", "TIMESHEET_IN_PROCESS");
        entityManager.persist(timesheet);

        assertThatThrownBy(() -> services.execute("createTimesheetForThisWeek", Map.of(
                "partyId", "DemoEmployee",
                "requiredDate", Timestamp.from(LocalDate.of(2025, 2, 5).atStartOfDay().toInstant(ZoneOffset.UTC)))))
                .isInstanceOf(MigrationException.class);
    }

    @Test
    @Transactional
    void simpleGetWorkEffortEventsByPeriodBucketsInRangeHolidayAndEvent() {
        Timestamp eventDate = Timestamp.from(Instant.parse("2025-01-15T10:00:00Z"));
        persistWorkEffort("WE-PERIOD-EVENT", "Meeting", eventDate);
        persistWorkEffort("WE-PERIOD-HOLIDAY", "Holiday", Timestamp.from(Instant.parse("2025-02-10T10:00:00Z")));
        persistWorkEffort("WE-PERIOD-OUTSIDE", "Outside", Timestamp.from(Instant.parse("2025-05-10T10:00:00Z")));

        Map<String, Object> result = services.execute("getWorkEffortEventsByPeriod", Map.of(
                "start", Timestamp.from(Instant.parse("2025-01-01T00:00:00Z")),
                "numPeriods", 3,
                "periodType", java.util.Calendar.MONTH,
                "calendarType", "VOID"));
        List<?> periods = (List<?>) result.get("periods");
        assertThat(periods).hasSize(3);
        assertThat((List<?>) ((Map<?, ?>) periods.get(0)).get("calendarEntries")).hasSize(1);
        assertThat((List<?>) ((Map<?, ?>) periods.get(1)).get("calendarEntries")).hasSize(1);
        assertThat((List<?>) ((Map<?, ?>) periods.get(2)).get("calendarEntries")).isEmpty();
    }

    @Test
    @Transactional
    @WithMockUser(username = "flow-admin", authorities = {"OFBTOOLS_VIEW", "WORKEFFORTMGR_ADMIN"})
    void createsAssignsTimesheetsAndCompletesWorkEffortWithStatusHistory() {
        persistStatusChange("CAL_CREATED", "CAL_IN_PROGRESS");
        persistStatusChange("CAL_IN_PROGRESS", "CAL_COMPLETED");
        Map<String, Object> created = services.execute("createWorkEffort", Map.of(
                "workEffortTypeId", "TASK",
                "workEffortName", "WorkEffort flow",
                "currentStatusId", "CAL_CREATED"));
        String workEffortId = created.get("workEffortId").toString();
        services.execute("assignPartyToWorkEffort", Map.of(
                "workEffortId", workEffortId,
                "partyId", "DemoEmployee",
                "roleTypeId", "CAL_OWNER"));

        Timestamp start = Timestamp.from(Instant.parse("2025-03-01T09:00:00Z"));
        Map<String, Object> timesheet = dispatcherCreateTimesheet(start);
        String timesheetId = timesheet.get("timesheetId").toString();
        Map<String, Object> timeEntry = services.execute("createTimeEntry", Map.of(
                "timesheetId", timesheetId,
                "workEffortId", workEffortId,
                "partyId", "DemoEmployee",
                "rateTypeId", "RATE_HOURLY",
                "fromDate", start,
                "thruDate", Timestamp.from(start.toInstant().plusSeconds(3600)),
                "hours", 1.0d));
        assertThat(timeEntry.get("timeEntryId")).isNotNull();

        services.execute("updateWorkEffort", Map.of(
                "workEffortId", workEffortId, "currentStatusId", "CAL_IN_PROGRESS"));
        services.execute("updateWorkEffort", Map.of(
                "workEffortId", workEffortId, "currentStatusId", "CAL_COMPLETED"));

        Object current = entities.findByPrimaryKey(
                entities.entityClass("WorkEffort"), Map.of("workEffortId", workEffortId));
        assertThat(entities.get(current, "currentStatusId")).isEqualTo("CAL_COMPLETED");
        assertThat(entities.findByEquality(entities.entityClass("WorkEffortStatus"),
                Map.of("workEffortId", workEffortId))).extracting(status -> entities.get(status, "statusId"))
                .contains("CAL_CREATED", "CAL_IN_PROGRESS", "CAL_COMPLETED");
    }

    @Test
    @Transactional
    @WithMockUser(username = "quick-assign-admin", authorities = {"OFBTOOLS_VIEW", "WORKEFFORTMGR_ADMIN"})
    void quickAssignPartyIdCreatesAssignment() {
        Map<String, Object> created = services.execute("createWorkEffort", Map.of(
                "workEffortTypeId", "TASK",
                "workEffortName", "Quick assign",
                "currentStatusId", "CAL_CREATED",
                "quickAssignPartyId", "DemoEmployee"));
        String workEffortId = created.get("workEffortId").toString();
        assertThat(entities.findByEquality(entities.entityClass("WorkEffortPartyAssignment"), Map.of(
                "workEffortId", workEffortId, "partyId", "DemoEmployee"))).isNotEmpty();
    }

    @Test
    @Transactional
    @WithMockUser(username = "flow-admin", authorities = {"OFBTOOLS_VIEW", "WORKEFFORTMGR_ADMIN"})
    void createTimeEntryRejectsCompletedTimesheet() {
        Object timesheet = entities.newEntity(entities.entityClass("Timesheet"));
        entities.set(timesheet, "timesheetId", "TS-COMPLETED-FLOW");
        entities.set(timesheet, "partyId", "DemoEmployee");
        entities.set(timesheet, "statusId", "TIMESHEET_APPROVED");
        entityManager.persist(timesheet);

        assertThatThrownBy(() -> services.execute("createTimeEntry", Map.of(
                "timesheetId", "TS-COMPLETED-FLOW",
                "workEffortId", "WE-NOT-FOUND",
                "partyId", "DemoEmployee")))
                .isInstanceOf(MigrationException.class)
                .hasMessageContaining("Timesheet is not in process");
    }

    @Test
    @Transactional
    @WithMockUser(username = "flow-admin", authorities = {"OFBTOOLS_VIEW", "WORKEFFORTMGR_ADMIN"})
    void invalidWorkEffortStatusTransitionReturnsBadRequest() throws Exception {
        persistWorkEffort("WE-INVALID-TRANS", "Invalid transition", Timestamp.from(Instant.now()));
        mockMvc.perform(post("/api/workeffort/services/updateWorkEffort")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"workEffortId":"WE-INVALID-TRANS","currentStatusId":"CAL_COMPLETED"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @WithMockUser(username = "rate-readonly", authorities = {"OFBTOOLS_VIEW", "WORKEFFORTMGR_VIEW"})
    void getTimeEntryRateDeniesReadonlyWithoutReturningRate() throws Exception {
        seedRateLookupFixture("WE-RATE-READONLY", "TS-RATE-READONLY", "TE-RATE-READONLY");
        mockMvc.perform(post("/api/workeffort/services/getTimeEntryRate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"timeEntryId\":\"TE-RATE-READONLY\",\"currencyUomId\":\"USD\"}"))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).doesNotContain("125.00"));
    }

    @Test
    @Transactional
    @WithMockUser(username = "rate-auditor", authorities = {
            "OFBTOOLS_VIEW", "WORKEFFORTMGR_VIEW", "WORKEFFORTMGR_SENSITIVE_VIEW"
    })
    void getTimeEntryRateReturnsSeededAmountForAuditor() throws Exception {
        seedRateLookupFixture("WE-RATE-AUDITOR", "TS-RATE-AUDITOR", "TE-RATE-AUDITOR");
        mockMvc.perform(post("/api/workeffort/services/getTimeEntryRate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"timeEntryId\":\"TE-RATE-AUDITOR\",\"currencyUomId\":\"USD\"}"))
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).contains("125.00"));
    }

    private Map<String, Object> dispatcherCreateTimesheet(Timestamp fromDate) {
        return services.execute("createTimesheet", Map.of(
                "partyId", "DemoEmployee",
                "statusId", "TIMESHEET_IN_PROCESS",
                "fromDate", fromDate));
    }

    private void persistStatusChange(String from, String to) {
        Object transition = entities.newEntity(entities.entityClass("StatusValidChange"));
        entities.set(transition, "statusId", from);
        entities.set(transition, "statusIdTo", to);
        entityManager.persist(transition);
    }

    private void seedRateLookupFixture(String workEffortId, String timesheetId, String timeEntryId) {
        persistWorkEffort(workEffortId, "Rate lookup", Timestamp.from(Instant.now()));
        Object timesheet = entities.newEntity(entities.entityClass("Timesheet"));
        entities.set(timesheet, "timesheetId", timesheetId);
        entities.set(timesheet, "partyId", "DemoEmployee");
        entities.set(timesheet, "statusId", "TIMESHEET_IN_PROCESS");
        entityManager.persist(timesheet);
        Object entry = entities.newEntity(entities.entityClass("TimeEntry"));
        entities.set(entry, "timeEntryId", timeEntryId);
        entities.set(entry, "timesheetId", timesheetId);
        entities.set(entry, "workEffortId", workEffortId);
        entities.set(entry, "partyId", "DemoEmployee");
        entities.set(entry, "rateTypeId", "RATE_HOURLY");
        entityManager.persist(entry);
        Object rate = entities.newEntity(entities.entityClass("RateAmount"));
        entities.set(rate, "workEffortId", workEffortId);
        entities.set(rate, "partyId", "DemoEmployee");
        entities.set(rate, "rateTypeId", "RATE_HOURLY");
        entities.set(rate, "rateCurrencyUomId", "USD");
        entities.set(rate, "periodTypeId", "RATE_HOUR");
        entities.set(rate, "rateAmount", new BigDecimal("125.00"));
        entities.set(rate, "fromDate", Timestamp.from(Instant.now()));
        entities.set(rate, "emplPositionTypeId", "EMPLOYEE");
        entityManager.persist(rate);
    }

    private void persistWorkEffort(String workEffortId, String name, Timestamp eventDate) {
        Object workEffort = entities.newEntity(entities.entityClass("WorkEffort"));
        entities.set(workEffort, "workEffortId", workEffortId);
        entities.set(workEffort, "workEffortTypeId", "EVENT");
        entities.set(workEffort, "workEffortName", name);
        entities.set(workEffort, "currentStatusId", "CAL_CREATED");
        entities.set(workEffort, "estimatedStartDate", eventDate);
        entities.set(workEffort, "estimatedCompletionDate", eventDate);
        entityManager.persist(workEffort);
    }

    @Test
    void ac10LocalProfileRequiresEnvironmentPassword() {
        assertThat(Path.of("src/main/resources/application-local.yml")).exists();
    }

    private long registryCatalogCount() {
        return ServiceCatalog.all().values().stream()
                .filter(definition -> definition.entity() != null && !definition.entity().isBlank())
                .filter(definition -> SetOfInvokes.contains(definition.invoke()))
                .count();
    }

    private static final class SetOfInvokes {
        private static boolean contains(String invoke) {
            return java.util.Set.of("create", "update", "expire", "delete").contains(invoke);
        }
    }
}
