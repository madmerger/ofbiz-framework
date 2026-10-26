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

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:workeffort-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "migration.security.component=workeffort",
        "migration.security.require-https=false",
        "workeffort.security.user-party-ids.permission-user=DemoEmployee",
        "migration.security.extra-entities=RateAmount,PartyRate,StatusItem,StatusValidChange,WorkEffortStatus"
})
class WorkEffortSimpleServicesTest {
    private static final AtomicLong IDS = new AtomicLong();
    private static final List<String> SIMPLE_SERVICES = List.of(
            "makeCommunicationEventWorkEffort",
            "createWorkEffortNote",
            "updateWorkEffortNote",
            "createWorkEffortAssoc",
            "copyWorkEffortAssocs",
            "createWorkEffortAndAssoc",
            "assignInventoryToWorkEffort",
            "createCommunicationEventWorkEff",
            "updateCommunicationEventWorkEff",
            "deleteCommunicationEventWorkEff",
            "checkCustRequestItemExists",
            "setWorkEffortFixedAssetAssign",
            "createWorkEffortFixedAssetAssign",
            "createWorkEffortReview",
            "indexWorkEffortKeywords",
            "createWorkEffortKeyword",
            "createWorkEffortKeywords",
            "deleteWorkEffortKeywords",
            "workEffortManagerPermission",
            "workEffortGenericPermission",
            "timesheetUpdatePermission",
            "workEffortICalendarPermission",
            "getICalWorkEfforts",
            "getPartyICalUrl",
            "createTimesheets",
            "createTimesheetForThisWeek",
            "addTimesheetToInvoice",
            "addTimesheetToNewInvoice",
            "addWorkEffortTimeToInvoice",
            "addWorkEffortTimeToNewInvoice",
            "checkTimesheetStatus",
            "updateTimeEntry",
            "getTimeEntryRate");

    @Autowired
    private WorkEffortBusinessServices services;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private EntityManager entityManager;

    static Stream<Arguments> simpleServices() {
        return SIMPLE_SERVICES.stream().map(Arguments::of);
    }

    @ParameterizedTest(name = "simple {0}")
    @MethodSource("simpleServices")
    @WithMockUser(username = "permission-user", authorities = {
            "OFBTOOLS_VIEW", "WORKEFFORTMGR_ADMIN", "WORKEFFORTMGR_SENSITIVE_VIEW"
    })
    @Transactional
    void invokesEverySimpleService(String name) {
        String suffix = Long.toString(IDS.incrementAndGet(), 36).toUpperCase();
        String workEffortId = "WE-SIMPLE-" + suffix;
        String targetWorkEffortId = "WE-SIMPLE-TO-" + suffix;
        String timesheetId = "TS-SIMPLE-" + suffix;
        String timeEntryId = "TE-SIMPLE-" + suffix;
        Timestamp now = Timestamp.from(Instant.now());
        seedWorkEffort(workEffortId, "FA-SIMPLE-" + suffix, now);
        seedWorkEffort(targetWorkEffortId, "FA-SIMPLE-TO-" + suffix, now);
        seedAssignment(workEffortId, "CAL_OWNER", now);
        seedAssignment(workEffortId, "ICAL_MEMBER", now);
        seedTimesheet(timesheetId, now);
        seedTimeEntry(timeEntryId, timesheetId, workEffortId, now);
        seedRate(workEffortId, now);
        seedInvoice("INV-SIMPLE-" + suffix, now);
        seedCommunicationEventLink(workEffortId, "CE-SIMPLE-" + suffix);
        seedWorkEffortNote(workEffortId, "NOTE-SIMPLE-" + suffix, now);
        seedAssociation(workEffortId, "WE-SIMPLE-RELATED-" + suffix, now);
        seedCalendarUrl(suffix, now);
        if ("deleteWorkEffortKeywords".equals(name)) {
            Object keyword = entities.newEntity(entities.entityClass("WorkEffortKeyword"));
            entities.set(keyword, "workEffortId", workEffortId);
            entities.set(keyword, "keyword", "obsolete");
            entities.set(keyword, "relevancyWeight", 1L);
            entityManager.persist(keyword);
        }
        Map<String, Object> input = serviceInput(
                name, suffix, workEffortId, targetWorkEffortId, timesheetId, timeEntryId, now);

        Map<String, Object> result = services.execute(name, input);
        assertThat(result).as(name + " result").isNotNull();
        assertMainValue(name, result, workEffortId, timesheetId);
    }

    private Map<String, Object> serviceInput(
            String name,
            String suffix,
            String workEffortId,
            String targetWorkEffortId,
            String timesheetId,
            String timeEntryId,
            Timestamp now) {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("workEffortId", workEffortId);
        input.put("workEffortIdFrom", workEffortId);
        input.put("workEffortIdTo", targetWorkEffortId);
        input.put("sourceWorkEffortId", workEffortId);
        input.put("targetWorkEffortId", targetWorkEffortId);
        input.put("workEffortAssocTypeId", "WORK_EFF_PARENT");
        input.put("sequenceNum", 1L);
        input.put("workEffortTypeId", "TASK");
        input.put("workEffortName", "Simple service " + suffix);
        input.put("currentStatusId", "CAL_CREATED");
        input.put("partyId", "DemoEmployee");
        input.put("partyIdList", List.of("DemoEmployee"));
        input.put("roleTypeId", "CAL_OWNER");
        input.put("fixedAssetId", "FA-SIMPLE-" + suffix);
        input.put("inventoryItemId", "INV-SIMPLE-" + suffix);
        input.put("quantity", BigDecimal.ONE);
        input.put("timesheetId", timesheetId);
        input.put("timeEntryId", timeEntryId);
        input.put("fromDate", now);
        input.put("thruDate", Timestamp.from(now.toInstant().plusSeconds(3600)));
        input.put("requiredDate", Timestamp.from(Instant.parse("2000-01-05T00:00:00Z")));
        input.put("numPeriods", 1);
        input.put("periodType", java.util.Calendar.DAY_OF_MONTH);
        input.put("calendarType", "CAL_PERSONAL");
        input.put("communicationEventId", "CE-SIMPLE-" + suffix);
        input.put("relationDescription", "simple service relation");
        input.put("noteId", "NOTE-SIMPLE-" + suffix);
        input.put("noteInfo", "simple note info");
        input.put("noteName", "simple note");
        input.put("noteParty", "DemoEmployee");
        input.put("internalNote", "N");
        input.put("keyword", "simple");
        input.put("relevancyWeight", 1L);
        input.put("userLoginId", "admin");
        input.put("mainAction", "VIEW");
        input.put("workEffortParentId", targetWorkEffortId);
        input.put("custRequestId", "CR-SIMPLE-" + suffix);
        input.put("custRequestItemSeqId", "00001");
        input.put("invoiceId", "INV-SIMPLE-" + suffix);
        input.put("partyIdFrom", "DemoEmployee");
        input.put("clientPartyId", "DemoEmployee");
        input.put("rateTypeId", "RATE_HOURLY");
        input.put("currencyUomId", "USD");
        input.put("rateCurrencyUomId", "USD");
        input.put("emplPositionTypeId", "EMPLOYEE");
        input.put("hours", 1.0d);
        input.put("combineInvoiceItem", "N");
        input.put("deepCopy", "N");
        input.put("excludeExpiredAssocs", "N");
        input.put("copyRelatedValues", "N");
        input.put("excludeExpiredRelations", "N");
        input.put("filterOutCanceledEvents", "N");
        input.put("workEffortTypeId", "TASK");
        input.put("scopeEnumId", "WES_PUBLIC");
        input.put("iCalUrl", "https://example.invalid/calendar");
        input.put("workEfforts", List.of());
        if ("createWorkEffortAndAssoc".equals(name)) {
            input.put("workEffortIdTo", targetWorkEffortId);
        }
        return input;
    }

    private void seedWorkEffort(String id, String fixedAssetId, Timestamp now) {
        Object workEffort = entities.newEntity(entities.entityClass("WorkEffort"));
        entities.set(workEffort, "workEffortId", id);
        entities.set(workEffort, "workEffortTypeId", "TASK");
        entities.set(workEffort, "workEffortName", id);
        entities.set(workEffort, "currentStatusId", "CAL_CREATED");
        entities.set(workEffort, "scopeEnumId", "WES_PUBLIC");
        entities.set(workEffort, "fixedAssetId", fixedAssetId);
        entities.set(workEffort, "estimatedStartDate", now);
        entities.set(workEffort, "estimatedCompletionDate", Timestamp.from(now.toInstant().plusSeconds(7200)));
        entityManager.persist(workEffort);
    }

    private void seedTimesheet(String id, Timestamp now) {
        Object timesheet = entities.newEntity(entities.entityClass("Timesheet"));
        entities.set(timesheet, "timesheetId", id);
        entities.set(timesheet, "partyId", "DemoEmployee");
        entities.set(timesheet, "statusId", "TIMESHEET_IN_PROCESS");
        entities.set(timesheet, "fromDate", now);
        entities.set(timesheet, "thruDate", Timestamp.from(now.toInstant().plusSeconds(86400)));
        entityManager.persist(timesheet);
    }

    private void seedTimeEntry(String id, String timesheetId, String workEffortId, Timestamp now) {
        Object entry = entities.newEntity(entities.entityClass("TimeEntry"));
        entities.set(entry, "timeEntryId", id);
        entities.set(entry, "timesheetId", timesheetId);
        entities.set(entry, "workEffortId", workEffortId);
        entities.set(entry, "partyId", "DemoEmployee");
        entities.set(entry, "rateTypeId", "RATE_HOURLY");
        entities.set(entry, "fromDate", now);
        entities.set(entry, "thruDate", Timestamp.from(now.toInstant().plusSeconds(3600)));
        entities.set(entry, "hours", 1.0d);
        entityManager.persist(entry);
    }

    private void seedRate(String workEffortId, Timestamp now) {
        Object rate = entities.newEntity(entities.entityClass("RateAmount"));
        entities.set(rate, "workEffortId", workEffortId);
        entities.set(rate, "partyId", "DemoEmployee");
        entities.set(rate, "rateTypeId", "RATE_HOURLY");
        entities.set(rate, "rateCurrencyUomId", "USD");
        entities.set(rate, "periodTypeId", "RATE_HOUR");
        entities.set(rate, "rateAmount", new BigDecimal("125.00"));
        entities.set(rate, "fromDate", now);
        entities.set(rate, "emplPositionTypeId", "EMPLOYEE");
        entityManager.persist(rate);
    }

    private void seedInvoice(String invoiceId, Timestamp now) {
        Object invoice = entities.newEntity(entities.entityClass("Invoice"));
        entities.set(invoice, "invoiceId", invoiceId);
        entities.set(invoice, "invoiceTypeId", "SALES_INVOICE");
        entities.set(invoice, "invoiceDate", now);
        entities.set(invoice, "statusId", "INVOICE_IN_PROCESS");
        entityManager.persist(invoice);
    }

    private void seedCommunicationEventLink(String workEffortId, String communicationEventId) {
        Object link = entities.newEntity(entities.entityClass("CommunicationEventWorkEff"));
        entities.set(link, "workEffortId", workEffortId);
        entities.set(link, "communicationEventId", communicationEventId);
        entities.set(link, "description", "simple service relation");
        entityManager.persist(link);
    }

    private void seedWorkEffortNote(String workEffortId, String noteId, Timestamp now) {
        Object note = entities.newEntity(entities.entityClass("NoteData"));
        entities.set(note, "noteId", noteId);
        entities.set(note, "noteInfo", "simple note info");
        entities.set(note, "noteName", "simple note");
        entities.set(note, "noteParty", "DemoEmployee");
        entities.set(note, "noteDateTime", now);
        entityManager.persist(note);
        Object link = entities.newEntity(entities.entityClass("WorkEffortNote"));
        entities.set(link, "workEffortId", workEffortId);
        entities.set(link, "noteId", noteId);
        entities.set(link, "internalNote", "N");
        entityManager.persist(link);
    }

    private void seedAssociation(String workEffortId, String relatedId, Timestamp now) {
        Object association = entities.newEntity(entities.entityClass("WorkEffortAssoc"));
        entities.set(association, "workEffortIdFrom", workEffortId);
        entities.set(association, "workEffortIdTo", relatedId);
        entities.set(association, "workEffortAssocTypeId", "WORK_EFF_PARENT");
        entities.set(association, "fromDate", now);
        entityManager.persist(association);
    }

    private void seedAssignment(String workEffortId, String roleTypeId, Timestamp now) {
        Object assignment = entities.newEntity(entities.entityClass("WorkEffortPartyAssignment"));
        entities.set(assignment, "workEffortId", workEffortId);
        entities.set(assignment, "partyId", "DemoEmployee");
        entities.set(assignment, "roleTypeId", roleTypeId);
        entities.set(assignment, "statusId", "PRTYASGN_ASSIGNED");
        entities.set(assignment, "fromDate", now);
        entityManager.persist(assignment);
    }

    private void seedCalendarUrl(String suffix, Timestamp now) {
        String contactMechId = "CM-SIMPLE-" + suffix;
        Object contact = entities.newEntity(entities.entityClass("ContactMech"));
        entities.set(contact, "contactMechId", contactMechId);
        entities.set(contact, "contactMechTypeId", "ELECTRONIC_ADDRESS");
        entities.set(contact, "infoString", "https://example.invalid/calendar/" + suffix);
        entityManager.persist(contact);
        Object purpose = entities.newEntity(entities.entityClass("PartyContactMechPurpose"));
        entities.set(purpose, "partyId", "DemoEmployee");
        entities.set(purpose, "contactMechId", contactMechId);
        entities.set(purpose, "contactMechPurposeTypeId", "ICAL_URL");
        entities.set(purpose, "fromDate", now);
        entityManager.persist(purpose);
        Object partyContact = entities.newEntity(entities.entityClass("PartyContactMech"));
        entities.set(partyContact, "partyId", "DemoEmployee");
        entities.set(partyContact, "contactMechId", contactMechId);
        entities.set(partyContact, "fromDate", now);
        entityManager.persist(partyContact);
    }

    private void assertMainValue(
            String name, Map<String, Object> result, String workEffortId, String timesheetId) {
        switch (name) {
        case "makeCommunicationEventWorkEffort", "createCommunicationEventWorkEff",
                "updateCommunicationEventWorkEff" -> assertThat(result)
                        .containsEntry("workEffortId", workEffortId)
                        .containsKey("communicationEventId");
        case "deleteCommunicationEventWorkEff" -> assertThat(result).isEmpty();
        case "createWorkEffortAssoc", "createWorkEffortAndAssoc" -> assertThat(result)
                .containsEntry("workEffortIdFrom", workEffortId)
                .containsKey("workEffortAssocTypeId");
        case "copyWorkEffortAssocs" -> assertThat((List<?>) result.get("associations")).hasSize(1);
        case "assignInventoryToWorkEffort" -> assertThat(result)
                .containsEntry("workEffortId", workEffortId)
                .containsKey("inventoryItemId");
        case "updateWorkEffortNote" -> assertThat(result).containsEntry("workEffortId", workEffortId)
                .containsKey("noteId");
        case "checkCustRequestItemExists" -> assertThat(result).doesNotContainKey("custRequestItemExists");
        case "setWorkEffortFixedAssetAssign", "createWorkEffortFixedAssetAssign", "createWorkEffortReview" ->
                assertThat(result).containsEntry("workEffortId", workEffortId);
        case "getICalWorkEfforts" -> assertThat((List<?>) result.get("workEfforts")).anySatisfy(workEffort ->
                assertThat(((Map<?, ?>) workEffort).get("workEffortId")).isEqualTo(workEffortId));
        case "getPartyICalUrl" -> assertThat(result.get("iCalUrl").toString())
                .startsWith("https://example.invalid/calendar/");
        case "checkTimesheetStatus" -> assertThat(result.get("statusId")).isEqualTo("TIMESHEET_IN_PROCESS");
        case "workEffortGenericPermission", "workEffortManagerPermission", "timesheetUpdatePermission",
                "workEffortICalendarPermission" -> assertThat(result).containsKey("hasPermission");
        case "getTimeEntryRate" -> assertThat(result.get("rateAmount")).isEqualTo(new BigDecimal("125.00"));
        case "indexWorkEffortKeywords", "createWorkEffortKeywords" ->
                assertThat(((Number) result.get("indexedKeywords")).longValue()).isPositive();
        case "createTimesheets" ->
                assertThat(entities.findByEquality(entities.entityClass("Timesheet"),
                        Map.of("partyId", "DemoEmployee"))).hasSize(2);
        case "createWorkEffortNote" -> assertThat(result).containsKey("noteId");
        case "createWorkEffortKeyword" -> assertThat(result.get("workEffortId")).isEqualTo(workEffortId);
        case "deleteWorkEffortKeywords" -> {
            assertThat(result).isEmpty();
            assertThat(entities.findByEquality(entities.entityClass("WorkEffortKeyword"),
                    Map.of("workEffortId", workEffortId))).isEmpty();
        }
        case "createTimesheetForThisWeek" -> assertThat(result).containsKey("timesheetId");
        case "updateTimeEntry" -> assertThat(result.get("timeEntryId")).isNotNull();
        case "addTimesheetToInvoice", "addTimesheetToNewInvoice",
                "addWorkEffortTimeToInvoice", "addWorkEffortTimeToNewInvoice" -> assertThat(result)
                        .containsKey("invoiceId");
        default -> assertThat(result).isNotNull();
        }
    }
}
