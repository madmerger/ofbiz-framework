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

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Attribute;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.ofbiz.migration.common.EntityAutoService;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.common.SequenceService;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.apache.ofbiz.migration.workeffort.port.CommunicationEventPort;
import org.apache.ofbiz.migration.workeffort.port.InvoicePort;
import org.apache.ofbiz.migration.workeffort.port.PartyPort;
import org.apache.ofbiz.migration.workeffort.port.RatePort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public final class WorkEffortBusinessServices {
    private static final Pattern KEYWORD_SEPARATORS = Pattern.compile("[^\\p{L}\\p{N}_]+");
    private final EntityManager entityManager;
    private final EntityRegistry entities;
    private final SequenceService sequences;
    private final MigrationServiceDispatcher dispatcher;
    private final ServiceHandlerRegistry registry;
    private final EntityAutoService entityAuto;
    private final WorkEffortPermissionServices permissions;
    private final WorkEffortSecurityProperties security;
    private final RatePort rates;
    private final InvoicePort invoices;
    private final CommunicationEventPort communicationEvents;
    private final PartyPort parties;

    public WorkEffortBusinessServices(
            EntityManager entityManager,
            EntityRegistry entities,
            SequenceService sequences,
            MigrationServiceDispatcher dispatcher,
            ServiceHandlerRegistry registry,
            EntityAutoService entityAuto,
            WorkEffortPermissionServices permissions,
            WorkEffortSecurityProperties security,
            RatePort rates,
            InvoicePort invoices,
            CommunicationEventPort communicationEvents,
            PartyPort parties) {
        this.entityManager = entityManager;
        this.entities = entities;
        this.sequences = sequences;
        this.dispatcher = dispatcher;
        this.registry = registry;
        this.entityAuto = entityAuto;
        this.permissions = permissions;
        this.security = security;
        this.rates = rates;
        this.invoices = invoices;
        this.communicationEvents = communicationEvents;
        this.parties = parties;
    }

    public Map<String, Object> execute(String name, Map<String, ?> input) {
        return switch (name) {
        case "workEffortManagerPermission" -> permissions.workEffortManagerPermission(input);
        case "workEffortGenericPermission" -> permissions.workEffortGenericPermission(input);
        case "timesheetUpdatePermission" -> permissions.timesheetUpdatePermission(input);
        case "workEffortICalendarPermission" -> permissions.workEffortICalendarPermission(input);
        case "createWorkEffort" -> createWorkEffort(input);
        case "updateWorkEffort" -> updateWorkEffort(input);
        case "deleteWorkEffort" -> deleteWorkEffort(input);
        case "createWorkEffortAndPartyAssign" -> createWorkEffortAndPartyAssign(input);
        case "assignPartyToWorkEffort", "quickAssignPartyToWorkEffort",
                "quickAssignPartyToWorkEffortWithRole" -> assignParty(name, input);
        case "updatePartyToWorkEffortAssignment" -> dispatcher.execute(
                "workeffort", "updateWorkEffortPartyAssignment", input);
        case "getWorkEffort" -> getWorkEffort(input);
        case "getWorkEffortAssignedTasks" -> getWorkEffortAssignedTasks(input);
        case "getWorkEffortEventsByPeriod" -> getWorkEffortEventsByPeriod(input);
        case "createTimeEntry", "updateTimesheet", "deleteTimesheet" -> executeStatusWrappedAuto(name, input);
        case "createWorkEffortNote" -> createWorkEffortNote(input);
        case "updateWorkEffortNote" -> updateWorkEffortNote(input);
        case "makeCommunicationEventWorkEffort", "createCommunicationEventWorkEff" ->
                communicationEventWorkEffort("create", input);
        case "updateCommunicationEventWorkEff" -> communicationEventWorkEffort("update", input);
        case "deleteCommunicationEventWorkEff" -> communicationEventWorkEffort("delete", input);
        case "createWorkEffortAndAssoc" -> createWorkEffortAndAssoc(input);
        case "createWorkEffortAssoc" -> createWorkEffortAssoc(input);
        case "copyWorkEffortAssocs" -> copyWorkEffortAssocs(input);
        case "assignInventoryToWorkEffort" -> assignInventoryToWorkEffort(input);
        case "setWorkEffortFixedAssetAssign" -> setWorkEffortFixedAssetAssign(input);
        case "createWorkEffortFixedAssetAssign" -> createWorkEffortFixedAssetAssign(input);
        case "createWorkEffortReview" -> createWorkEffortReview(input);
        case "indexWorkEffortKeywords" -> indexWorkEffortKeywords(input);
        case "createWorkEffortKeyword" -> createWorkEffortKeyword(input);
        case "createWorkEffortKeywords" -> createWorkEffortKeywords(input);
        case "deleteWorkEffortKeywords" -> deleteWorkEffortKeywords(input);
        case "checkCustRequestItemExists" -> checkCustRequestItemExists(input);
        case "getICalWorkEfforts" -> getICalWorkEfforts(input);
        case "getPartyICalUrl" -> getPartyICalUrl(input);
        case "createTimesheets" -> createTimesheets(input);
        case "createTimesheetForThisWeek" -> createTimesheetForThisWeek(input);
        case "addTimesheetToInvoice", "addTimesheetToNewInvoice" ->
                addTimesheetToInvoice(name, input);
        case "addWorkEffortTimeToInvoice", "addWorkEffortTimeToNewInvoice" ->
                addWorkEffortTimeToInvoice(name, input);
        case "checkTimesheetStatus" -> checkTimesheetStatus(input);
        case "updateTimeEntry" -> updateTimeEntry(input);
        case "getTimeEntryRate" -> getTimeEntryRate(input);
        default -> executeCatalogService(name, input);
        };
    }

    private Map<String, Object> executeCatalogService(String name, Map<String, ?> input) {
        ServiceHandlerRegistry.ServiceDefinition definition = registry.find("workeffort", name)
                .orElseThrow(() -> new MigrationException(HttpStatus.NOT_FOUND, "Unknown workeffort service: " + name));
        Map<String, Object> effectiveInput = new LinkedHashMap<>(input);
        List<ServiceHandlerRegistry.ParamDefinition> effectiveParams = new ArrayList<>();
        Set<String> parameterNames = new HashSet<>();
        Class<?> entityType = entities.entityClass(definition.entity());
        Set<String> entityFields = entityManager.getMetamodel().entity(entityType).getAttributes().stream()
                .map(Attribute::getName).collect(java.util.stream.Collectors.toSet());
        List<ServiceHandlerRegistry.ParamDefinition> params = definition.params().stream()
                .map(param -> {
                    boolean outReminderSequence =
                            param.name().equals("sequenceId") && "createWorkEffortEventReminder".equals(name);
                    boolean implicitEntityInput = entityFields.contains(param.name())
                            && (param.mode() == null || param.mode().isBlank()
                                    || "None".equalsIgnoreCase(param.mode()));
                    return outReminderSequence || implicitEntityInput
                            ? new ServiceHandlerRegistry.ParamDefinition(param.name(), "IN", param.optional(),
                                    param.type())
                            : param;
                })
                .toList();
        for (ServiceHandlerRegistry.ParamDefinition param : params) {
            effectiveParams.add(param);
            parameterNames.add(param.name());
        }
        if ("create".equalsIgnoreCase(definition.invoke())) {
            for (String key : entities.primaryKeys(entityType)) {
                if (!parameterNames.contains(key)) {
                    effectiveParams.add(new ServiceHandlerRegistry.ParamDefinition(key, "IN", false, "String"));
                }
            }
        }
        if ("createWorkEffortEventReminder".equals(name)) {
            effectiveInput.put("sequenceId",
                    sequences.nextSubSequence(definition.entity(), effectiveInput, "sequenceId"));
        }
        ServiceHandlerRegistry.ServiceDefinition effectiveDefinition = effectiveParams.equals(definition.params())
                ? definition
                : new ServiceHandlerRegistry.ServiceDefinition(definition.name(), definition.entity(),
                        definition.invoke(), definition.permissionMainAction(), effectiveParams);
        Map<String, Object> result = entityAuto.execute(effectiveDefinition, effectiveInput);
        if (definition.entity() != null
                && Set.of("WorkEffortAttribute", "WorkEffortContent").contains(definition.entity())
                && Set.of("create", "update").contains(definition.invoke().toLowerCase())) {
            executeIndex(string(input.get("workEffortId")));
        }
        if ("createTimesheetRole".equals(name) && input.get("partyId") != null) {
            parties.ensurePartyRole(string(input.get("partyId")), string(input.get("roleTypeId")));
        }
        return result;
    }

    private Map<String, Object> createWorkEffort(Map<String, ?> input) {
        requireCorePermission("CREATE", null, string(input.get("workEffortParentId")));
        if (input.get("workEffortParentId") != null) {
            find("WorkEffort", Map.of("workEffortId", input.get("workEffortParentId")));
        }
        Map<String, Object> values = serviceInput("createWorkEffort", input);
        values.putIfAbsent("workEffortId", sequences.nextId("WorkEffort"));
        values.putIfAbsent("currentStatusId", "CAL_CREATED");
        Timestamp now = Timestamp.from(Instant.now());
        values.putIfAbsent("lastStatusUpdate", now);
        values.putIfAbsent("lastModifiedDate", now);
        values.putIfAbsent("createdDate", now);
        values.putIfAbsent("revisionNumber", 1L);
        String userLoginId = userLoginId(input);
        if (userLoginId != null) {
            values.putIfAbsent("lastModifiedByUserLogin", userLoginId);
            values.putIfAbsent("createdByUserLogin", userLoginId);
        }
        Object workEffort = createEntity("WorkEffort", values);
        String id = string(entities.get(workEffort, "workEffortId"));
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("workEffortId", id);
        status.put("statusId", value(values, "currentStatusId", "CAL_CREATED"));
        status.put("statusDatetime", now);
        status.put("setByUserLogin", userLoginId);
        createIfEntityExists("WorkEffortStatus", status);
        if (input.get("quickAssignPartyId") != null) {
            assignParty("quickAssignPartyToWorkEffort", Map.of(
                    "workEffortId", id,
                    "partyId", input.get("quickAssignPartyId"),
                    "roleTypeId", value(input, "quickAssignRoleTypeId", "CAL_ATTENDEE")));
        }
        if (input.get("communicationEventId") != null) {
            communicationEventWorkEffort("create", Map.of(
                    "workEffortId", id, "communicationEventId", input.get("communicationEventId")));
        }
        executeIndex(id);
        return entities.toMap(workEffort);
    }

    private Map<String, Object> updateWorkEffort(Map<String, ?> input) {
        String id = string(input.get("workEffortId"));
        Object workEffort = find("WorkEffort", Map.of("workEffortId", id));
        String currentStatus = string(entities.get(workEffort, "currentStatusId"));
        String nextStatus = string(input.get("currentStatusId"));
        requireCorePermission("UPDATE", id, string(entities.get(workEffort, "workEffortParentId")));
        if (nextStatus != null && !nextStatus.equals(currentStatus)
                && entities.findByEquality(entities.entityClass("StatusValidChange"),
                        Map.of("statusId", currentStatus, "statusIdTo", nextStatus)).isEmpty()) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Invalid WorkEffort status transition");
        }
        updateEntity(workEffort, input);
        if (nextStatus != null && !nextStatus.equals(currentStatus)) {
            Map<String, Object> status = new LinkedHashMap<>();
            status.put("workEffortId", id);
            status.put("statusId", nextStatus);
            status.put("statusDatetime", Timestamp.from(Instant.now()));
            createIfEntityExists("WorkEffortStatus", status);
            setIfPresent(workEffort, "lastStatusUpdate", Timestamp.from(Instant.now()));
        }
        executeIndex(id);
        return entities.toMap(workEffort);
    }

    private Map<String, Object> deleteWorkEffort(Map<String, ?> input) {
        String id = string(input.get("workEffortId"));
        Object workEffort = find("WorkEffort", Map.of("workEffortId", id));
        requireCorePermission("DELETE", id, string(entities.get(workEffort, "workEffortParentId")));
        entityManager.remove(workEffort);
        return Map.of("workEffortId", id);
    }

    private Map<String, Object> createWorkEffortAndPartyAssign(Map<String, ?> input) {
        Map<String, Object> created = createWorkEffort(input);
        Map<String, Object> assignment = new LinkedHashMap<>(input);
        assignment.put("workEffortId", created.get("workEffortId"));
        assignment.putIfAbsent("roleTypeId", "CAL_OWNER");
        return assignParty("assignPartyToWorkEffort", assignment);
    }

    private Map<String, Object> assignParty(String name, Map<String, ?> input) {
        String workEffortId = string(input.get("workEffortId"));
        if (workEffortId == null) throw new MigrationException(HttpStatus.BAD_REQUEST, "workEffortId is required");
        requireCorePermission("UPDATE", workEffortId, null);
        Map<String, Object> assignment = new LinkedHashMap<>(input);
        assignment.putIfAbsent("roleTypeId",
                "createWorkEffortAndPartyAssign".equals(name) ? "CAL_OWNER" : "CAL_ATTENDEE");
        assignment.putIfAbsent("fromDate", Timestamp.from(Instant.now()));
        boolean quickAssign = name.startsWith("quickAssignPartyToWorkEffort");
        Object party = quickAssign ? input.get("quickAssignPartyId") : input.get("partyId");
        if (party == null) party = currentPartyId(input);
        assignment.put("partyId", party);
        if (quickAssign) {
            assignment.put("statusId", "quickAssignPartyToWorkEffortWithRole".equals(name)
                    ? "CAL_ACCEPTED" : "PRTYASGN_ASSIGNED");
        }
        String partyId = string(assignment.get("partyId"));
        if (partyId == null) throw new MigrationException(HttpStatus.BAD_REQUEST, "partyId is required");
        if (!"updatePartyToWorkEffortAssignment".equals(name)) {
            List<?> activeAssignments = entities.findByEquality(
                    entities.entityClass("WorkEffortPartyAssignment"),
                    Map.of("workEffortId", workEffortId, "partyId", partyId,
                            "roleTypeId", assignment.get("roleTypeId")));
            if (activeAssignments.stream().anyMatch(this::active)) {
                throw new MigrationException(HttpStatus.BAD_REQUEST, "WorkEffort party assignment already exists");
            }
        }
        parties.ensurePartyRole(partyId, string(assignment.get("roleTypeId")));
        if (assignment.get("statusId") != null) {
            assignment.put("statusDateTime", Timestamp.from(Instant.now()));
        }
        String userLoginId = userLoginId(input);
        if (userLoginId != null) assignment.putIfAbsent("assignedByUserLoginId", userLoginId);
        String autoService = "updatePartyToWorkEffortAssignment".equals(name)
                ? "updateWorkEffortPartyAssignment" : "createWorkEffortPartyAssignment";
        Map<String, Object> result = dispatcher.execute("workeffort", autoService, assignment);
        return quickAssign ? Map.of() : result;
    }

    private Map<String, Object> getWorkEffort(Map<String, ?> input) {
        String id = string(input.get("workEffortId"));
        permissions.requireGenericPermission("VIEW", id, null);
        return entities.toMap(find("WorkEffort", Map.of("workEffortId", id)));
    }

    private Map<String, Object> getWorkEffortAssignedTasks(Map<String, ?> input) {
        String partyId = value(input, "partyId", currentPartyId(input));
        List<Map<String, Object>> tasks = new ArrayList<>();
        for (Object assignment : entities.findByEquality(
                entities.entityClass("WorkEffortPartyAssignment"), Map.of("partyId", partyId))) {
            if (active(assignment)) {
                Object workEffort = find("WorkEffort",
                        Map.of("workEffortId", entities.get(assignment, "workEffortId")));
                tasks.add(entities.toMap(workEffort));
            }
        }
        return Map.of("workEfforts", tasks);
    }

    private Map<String, Object> getWorkEffortEventsByPeriod(Map<String, ?> input) {
        Timestamp start = timestamp(input.get("start"));
        Object rawPeriods = input.get("numPeriods");
        Object rawPeriodType = input.get("periodType");
        if (start == null || rawPeriods == null || rawPeriodType == null) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "start, numPeriods, and periodType are required");
        }
        int count = Integer.parseInt(rawPeriods.toString());
        int periodType = Integer.parseInt(rawPeriodType.toString());
        Calendar cursor = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"));
        cursor.setTime(start);
        Calendar endCursor = (Calendar) cursor.clone();
        endCursor.add(periodType, count);
        Timestamp rangeEnd = new Timestamp(endCursor.getTimeInMillis());
        long totalRange = rangeEnd.getTime() - start.getTime();
        if (count <= 0 || totalRange <= 0) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "numPeriods and periodType must form a positive range");
        }
        String calendarType = value(input, "calendarType", "CAL_PERSONAL");
        Set<String> partyIds = new LinkedHashSet<>();
        Object partyList = input.get("partyIds");
        if (partyList instanceof Iterable<?> partiesList) {
            partiesList.forEach(party -> partyIds.add(String.valueOf(party)));
        }
        if (input.get("partyId") != null) partyIds.add(input.get("partyId").toString());
        if (partyIds.isEmpty() && "CAL_PERSONAL".equals(calendarType) && currentPartyId(input) != null) {
            partyIds.add(currentPartyId(input));
        }
        boolean canceledFilter = Boolean.parseBoolean(value(input, "filterOutCanceledEvents", "false"));
        Set<String> canceled = Set.of("EVENT_CANCELLED", "CAL_CANCELLED", "PRUN_CANCELLED");
        Set<String> eventIds = new LinkedHashSet<>();
        List<Object> validWorkEfforts = new ArrayList<>();
        for (Object workEffort : entities.findByEquality(entities.entityClass("WorkEffort"), Map.of())) {
            if (!matches(workEffort, "facilityId", input.get("facilityId"))
                    || !matches(workEffort, "fixedAssetId", input.get("fixedAssetId"))
                    || !matches(workEffort, "workEffortTypeId", input.get("workEffortTypeId"))) continue;
            if (canceledFilter && canceled.contains(string(property(workEffort, "currentStatusId")))) continue;
            String typeId = string(property(workEffort, "workEffortTypeId"));
            if ("CAL_MANUFACTURING".equals(calendarType)
                    && (typeId == null || !Set.of("PROD_ORDER_HEADER", "PROD_ORDER_TASK").contains(typeId))) continue;
            if ("CAL_PERSONAL".equals(calendarType) && !partyIds.isEmpty()
                    && !isPersonalCalendarEvent(workEffort, partyIds)) continue;
            Timestamp eventStart = preferredDate(workEffort, "actualStartDate", "estimatedStartDate");
            Timestamp eventEnd = preferredDate(workEffort, "actualCompletionDate", "estimatedCompletionDate");
            if (eventStart == null || eventStart.after(rangeEnd)) continue;
            if (eventEnd != null && eventEnd.before(start)) continue;
            String id = string(property(workEffort, "workEffortId"));
            if (id != null && eventIds.add(id)) validWorkEfforts.add(workEffort);
        }
        if (!"CAL_PERSONAL".equals(calendarType) && input.get("fixedAssetId") != null) {
            for (Object assignment : entities.findByEquality(entities.entityClass("WorkEffortFixedAssetAssign"),
                    Map.of("fixedAssetId", input.get("fixedAssetId")))) {
                if (!active(assignment)) continue;
                Object workEffort = findNullable("WorkEffort",
                        Map.of("workEffortId", property(assignment, "workEffortId")));
                if (workEffort != null
                        && matches(workEffort, "facilityId", input.get("facilityId"))
                        && matches(workEffort, "workEffortTypeId", input.get("workEffortTypeId"))
                        && eventInRange(workEffort, start, rangeEnd)
                        && !(canceledFilter && canceled.contains(string(property(workEffort, "currentStatusId"))))
                        && eventIds.add(string(property(workEffort, "workEffortId")))) {
                    validWorkEfforts.add(workEffort);
                }
            }
        }
        List<Map<String, Object>> periodRows = new ArrayList<>();
        int maxConcurrent = 0;
        boolean firstEntry = true;
        Calendar period = (Calendar) cursor.clone();
        for (int index = 0; index < count; index++) {
            Calendar nextPeriod = (Calendar) period.clone();
            nextPeriod.add(periodType, 1);
            Timestamp periodStart = new Timestamp(period.getTimeInMillis());
            Timestamp periodEnd = new Timestamp(nextPeriod.getTimeInMillis() - 1);
            List<Map<String, Object>> entries = new ArrayList<>();
            for (Object workEffort : validWorkEfforts) {
                Timestamp eventStart = preferredDate(workEffort, "actualStartDate", "estimatedStartDate");
                Timestamp eventEnd = preferredDate(workEffort, "actualCompletionDate", "estimatedCompletionDate");
                if (eventEnd == null) eventEnd = eventStart;
                if (eventStart == null || eventStart.after(periodEnd) || eventEnd.before(periodStart)) continue;
                long length = Math.max(0, Math.min(eventEnd.getTime(), rangeEnd.getTime())
                        - Math.max(eventStart.getTime(), start.getTime()));
                long periodLength = nextPeriod.getTimeInMillis() - period.getTimeInMillis();
                int span = Math.max(1, (int) Math.ceil((double) length / periodLength));
                if (length % periodLength == 0 && eventStart.after(periodStart)) span++;
                Map<String, Object> clippedRange = Map.of(
                        "start", eventStart.before(start) ? start : eventStart,
                        "end", eventEnd.after(rangeEnd) ? rangeEnd : eventEnd);
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("workEffort", entities.toMap(workEffort));
                entry.put("periodSpan", span);
                entry.put("calEntryRange", clippedRange);
                entry.put("startOfPeriod", firstEntry || !eventStart.before(periodStart));
                firstEntry = false;
                entries.add(entry);
            }
            maxConcurrent = Math.max(maxConcurrent, entries.size());
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("start", periodStart);
            row.put("end", periodEnd);
            row.put("calendarEntries", entries);
            row.put("calendarEntriesByDateRange", entries);
            periodRows.add(row);
            period = nextPeriod;
        }
        return Map.of("periods", periodRows, "maxConcurrentEntries", maxConcurrent);
    }

    private boolean isPersonalCalendarEvent(Object workEffort, Set<String> partyIds) {
        if ("WES_PUBLIC".equals(string(property(workEffort, "scopeEnumId")))) {
            Object workEffortType = findNullable("WorkEffortType",
                    Map.of("workEffortTypeId", property(workEffort, "workEffortTypeId")));
            if (workEffortType != null && "EVENT".equals(string(property(workEffortType, "parentTypeId")))) return true;
        }
        String workEffortId = string(property(workEffort, "workEffortId"));
        for (Object assignment : entities.findByEquality(
                entities.entityClass("WorkEffortPartyAssignment"), Map.of("workEffortId", workEffortId))) {
            if (active(assignment) && partyIds.contains(string(property(assignment, "partyId")))) return true;
        }
        return false;
    }

    private Timestamp preferredDate(Object entity, String actualField, String estimatedField) {
        Timestamp actual = timestamp(property(entity, actualField));
        return actual == null ? timestamp(property(entity, estimatedField)) : actual;
    }

    private boolean eventInRange(Object workEffort, Timestamp start, Timestamp end) {
        Timestamp eventStart = preferredDate(workEffort, "actualStartDate", "estimatedStartDate");
        Timestamp eventEnd = preferredDate(workEffort, "actualCompletionDate", "estimatedCompletionDate");
        return eventStart != null && !eventStart.after(end) && (eventEnd == null || !eventEnd.before(start));
    }

    private Map<String, Object> createWorkEffortNote(Map<String, ?> input) {
        Map<String, Object> note = new LinkedHashMap<>();
        note.put("noteId", sequences.nextId("NoteData"));
        note.put("noteInfo", input.get("noteInfo"));
        note.put("noteName", input.get("noteName"));
        note.put("noteParty", value(input, "noteParty", currentPartyId(input)));
        note.put("noteDateTime", Timestamp.from(Instant.now()));
        createEntity("NoteData", note);
        Map<String, Object> link = new LinkedHashMap<>();
        link.put("noteId", note.get("noteId"));
        link.put("workEffortId", input.get("workEffortId"));
        link.put("internalNote", input.get("internalNote"));
        Object workEffortNote = createEntity("WorkEffortNote", link);
        executeIndex(string(input.get("workEffortId")));
        return entities.toMap(workEffortNote);
    }

    private Map<String, Object> updateWorkEffortNote(Map<String, ?> input) {
        String noteId = string(input.get("noteId"));
        Object note = find("NoteData", Map.of("noteId", noteId));
        updateEntity(note, input);
        Object link = find("WorkEffortNote", Map.of(
                "workEffortId", input.get("workEffortId"), "noteId", noteId));
        updateEntity(link, input);
        executeIndex(string(input.get("workEffortId")));
        return entities.toMap(link);
    }

    private Map<String, Object> communicationEventWorkEffort(String operation, Map<String, ?> input) {
        Map<String, Object> values = new LinkedHashMap<>(input);
        if (values.get("communicationEventId") == null && "create".equals(operation)) {
            Map<String, Object> event = communicationEvents.createCommunicationEvent(values);
            values.put("communicationEventId", event.get("communicationEventId"));
        }
        Map<String, Object> key = Map.of(
                "workEffortId", values.get("workEffortId"),
                "communicationEventId", values.get("communicationEventId"));
        Object link = findNullable("CommunicationEventWorkEff", key);
        if ("delete".equals(operation)) {
            if (link == null) throw new MigrationException(HttpStatus.NOT_FOUND, "Communication event link not found");
            entityManager.remove(link);
            return Map.of();
        }
        if ("update".equals(operation)) {
            if (link == null) throw new MigrationException(HttpStatus.NOT_FOUND, "Communication event link not found");
            updateEntity(link, values);
        } else if (link == null) {
            Map<String, Object> linkValues = new LinkedHashMap<>();
            linkValues.putAll(key);
            linkValues.put("description", values.get("relationDescription"));
            link = createEntity("CommunicationEventWorkEff", linkValues);
        } else {
            Map<String, Object> update = new LinkedHashMap<>();
            update.put("description", values.get("relationDescription"));
            updateEntity(link, update);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("workEffortId", entities.get(link, "workEffortId"));
        result.put("communicationEventId", entities.get(link, "communicationEventId"));
        return result;
    }

    private Map<String, Object> createWorkEffortAndAssoc(Map<String, ?> input) {
        Map<String, Object> values = new LinkedHashMap<>(input);
        values.putIfAbsent("fromDate", Timestamp.from(Instant.now()));
        if (associationExists(values)) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "WorkEffort association already exists");
        }
        if (values.get("workEffortIdTo") == null || values.get("workEffortIdTo").toString().isBlank()) {
            Map<String, Object> created = createWorkEffort(serviceInput("createWorkEffort", input));
            values.put("workEffortIdTo", created.get("workEffortId"));
        }
        Map<String, Object> association = createWorkEffortAssoc(values);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("workEffortIdFrom", association.get("workEffortIdFrom"));
        result.put("workEffortAssocTypeId", association.get("workEffortAssocTypeId"));
        result.put("workEffortId", values.get("workEffortIdTo"));
        return result;
    }

    private Map<String, Object> createWorkEffortAssoc(Map<String, ?> input) {
        Map<String, Object> values = new LinkedHashMap<>(input);
        values.putIfAbsent("fromDate", Timestamp.from(Instant.now()));
        values.putIfAbsent("sequenceNum", 0L);
        if (associationExists(values)) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "WorkEffort association already exists");
        }
        return create("WorkEffortAssoc", values);
    }

    private boolean associationExists(Map<String, ?> values) {
        Object from = values.get("workEffortIdFrom");
        Object to = values.get("workEffortIdTo");
        Object type = values.get("workEffortAssocTypeId");
        Object date = values.get("fromDate");
        return from != null && to != null && type != null && date != null
                && findNullable("WorkEffortAssoc", associationKey(values)) != null;
    }

    private Map<String, Object> associationKey(Map<String, ?> values) {
        Map<String, Object> key = new LinkedHashMap<>();
        for (String field : List.of("workEffortIdFrom", "workEffortIdTo", "workEffortAssocTypeId", "fromDate")) {
            if (values.get(field) != null) key.put(field, values.get(field));
        }
        return key;
    }

    private Map<String, Object> copyWorkEffortAssocs(Map<String, ?> input) {
        List<Map<String, Object>> copied = new ArrayList<>();
        String sourceId = string(input.get("sourceWorkEffortId"));
        String targetId = string(input.get("targetWorkEffortId"));
        for (Object source : entities.findByEquality(
                entities.entityClass("WorkEffortAssoc"), Map.of("workEffortIdFrom", sourceId))) {
            if ("Y".equalsIgnoreCase(value(input, "excludeExpiredAssocs", "N")) && !active(source)) continue;
            Map<String, Object> values = new LinkedHashMap<>(entities.toMap(source));
            values.remove("createdStamp");
            values.remove("createdTxStamp");
            values.put("workEffortIdFrom", targetId);
            if ("Y".equalsIgnoreCase(value(input, "deepCopy", "N"))) {
                ServiceHandlerRegistry.ServiceDefinition definition = registry.find("workeffort", "copyWorkEffort")
                        .orElse(null);
                if (definition != null) {
                    Map<String, Object> copyInput = new LinkedHashMap<>(input);
                    copyInput.put("sourceWorkEffortId", entities.get(source, "workEffortIdTo"));
                    copyInput.remove("targetWorkEffortId");
                    Map<String, Object> copiedWorkEffort = dispatcher.execute("workeffort", "copyWorkEffort", copyInput);
                    values.put("workEffortIdTo", copiedWorkEffort.get("workEffortId"));
                }
            }
            copied.add(create("WorkEffortAssoc", values));
        }
        return Map.of("associations", copied);
    }

    private Map<String, Object> assignInventoryToWorkEffort(Map<String, ?> input) {
        Map<String, Object> key = Map.of(
                "workEffortId", input.get("workEffortId"),
                "inventoryItemId", input.get("inventoryItemId"));
        Object existing = findNullable("WorkEffortInventoryAssign", key);
        if (existing == null) return create("WorkEffortInventoryAssign", input);
        entities.set(existing, "quantity", number(entities.get(existing, "quantity")) + number(input.get("quantity")));
        return entities.toMap(existing);
    }

    private Map<String, Object> setWorkEffortFixedAssetAssign(Map<String, ?> input) {
        Object workEffort = find("WorkEffort", Map.of("workEffortId", input.get("workEffortId")));
        Object fixedAssetId = entities.get(workEffort, "fixedAssetId");
        if (fixedAssetId == null) return Map.of();
        Map<String, Object> key = Map.of("workEffortId", input.get("workEffortId"), "fixedAssetId", fixedAssetId);
        entities.findByEquality(entities.entityClass("WorkEffortFixedAssetAssign"), key).stream()
                .filter(this::active)
                .forEach(entityManager::remove);
        Map<String, Object> values = new LinkedHashMap<>(key);
        values.put("statusId", entities.get(workEffort, "currentStatusId"));
        values.put("fromDate", entities.get(workEffort, "estimatedStartDate"));
        values.put("thruDate", entities.get(workEffort, "estimatedCompletionDate"));
        return create("WorkEffortFixedAssetAssign", values);
    }

    private Map<String, Object> createWorkEffortFixedAssetAssign(Map<String, ?> input) {
        Map<String, Object> values = new LinkedHashMap<>(input);
        if (values.get("fromDate") == null) {
            Object workEffort = findNullable("WorkEffort", Map.of("workEffortId", input.get("workEffortId")));
            Object fromDate = workEffort == null ? null : entities.get(workEffort, "actualStartDate");
            if (fromDate == null && workEffort != null) {
                fromDate = entities.get(workEffort, "estimatedStartDate");
            }
            values.put("fromDate", fromDate == null ? Timestamp.from(Instant.now()) : fromDate);
        }
        if (findNullable("WorkEffortFixedAssetAssign", values) != null) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "WorkEffort fixed asset assignment already exists");
        }
        return create("WorkEffortFixedAssetAssign", values);
    }

    private Map<String, Object> createWorkEffortReview(Map<String, ?> input) {
        Map<String, Object> values = new LinkedHashMap<>(input);
        Object userLogin = input.get("userLogin");
        if (values.get("userLoginId") == null && userLogin instanceof Map<?, ?> login) {
            values.put("userLoginId", login.get("userLoginId"));
        }
        values.putIfAbsent("reviewDate", Timestamp.from(Instant.now()));
        if (findNullable("WorkEffortReview", values) != null) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "WorkEffort review already exists");
        }
        return create("WorkEffortReview", values);
    }

    private Map<String, Object> createWorkEffortKeyword(Map<String, ?> input) {
        if (input.get("workEffortId") == null || input.get("keyword") == null
                || input.get("keyword").toString().isBlank()) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "workEffortId and keyword are required");
        }
        if (findNullable("WorkEffortKeyword", input) != null) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "WorkEffort keyword already exists");
        }
        return create("WorkEffortKeyword", input);
    }

    private Map<String, Object> createWorkEffortKeywords(Map<String, ?> input) {
        return indexWorkEffortKeywords(input);
    }

    private Map<String, Object> deleteWorkEffortKeywords(Map<String, ?> input) {
        Object workEffort = findNullable("WorkEffort", Map.of("workEffortId", input.get("workEffortId")));
        if (workEffort != null) {
            entities.findByEquality(entities.entityClass("WorkEffortKeyword"),
                    Map.of("workEffortId", input.get("workEffortId"))).forEach(entityManager::remove);
        }
        return Map.of();
    }

    private Map<String, Object> checkCustRequestItemExists(Map<String, ?> input) {
        Object item = findNullable("CustRequestItem", input);
        return item == null ? Map.of() : Map.of("custRequestItemExists", true);
    }

    private Map<String, Object> indexWorkEffortKeywords(Map<String, ?> input) {
        Object workEffort = input.get("workEffort");
        if (workEffort == null && input.get("workEffortId") != null) {
            workEffort = findNullable("WorkEffort", Map.of("workEffortId", input.get("workEffortId")));
        }
        if (workEffort == null) return Map.of();
        String workEffortId = string(entities.get(workEffort, "workEffortId"));
        if (workEffortId == null) return Map.of();
        Map<String, Long> keywords = new HashMap<>();
        addKeywords(keywords, workEffortId);
        for (String field : List.of("workEffortName", "workEffortTypeId", "currentStatusId", "description")) {
            addKeywords(keywords, string(property(workEffort, field)));
        }
        for (Object attribute : entities.findByEquality(entities.entityClass("WorkEffortAttribute"),
                Map.of("workEffortId", workEffortId))) {
            addKeywords(keywords, string(property(attribute, "attrName")));
            addKeywords(keywords, string(property(attribute, "attrValue")));
        }
        for (Object link : entities.findByEquality(entities.entityClass("WorkEffortNote"),
                Map.of("workEffortId", workEffortId))) {
            Object note = findNullable("NoteData", Map.of("noteId", entities.get(link, "noteId")));
            if (note != null) addKeywords(keywords, string(property(note, "noteInfo")));
        }
        for (Object existing : entities.findByEquality(entities.entityClass("WorkEffortKeyword"),
                Map.of("workEffortId", workEffortId))) {
            if (!keywords.containsKey(string(property(existing, "keyword")))) entityManager.remove(existing);
        }
        for (Map.Entry<String, Long> entry : keywords.entrySet()) {
            Map<String, Object> key = Map.of("workEffortId", workEffortId, "keyword", entry.getKey());
            Object existing = findNullable("WorkEffortKeyword", key);
            if (existing == null) {
                createEntity("WorkEffortKeyword", Map.of(
                        "workEffortId", workEffortId, "keyword", entry.getKey(), "relevancyWeight", entry.getValue()));
            } else {
                entities.set(existing, "relevancyWeight", entry.getValue());
            }
        }
        return Map.of("workEffortId", workEffortId, "indexedKeywords", keywords.size());
    }

    private static void addKeywords(Map<String, Long> keywords, String source) {
        if (source == null) return;
        for (String word : KEYWORD_SEPARATORS.split(source.toLowerCase(java.util.Locale.ROOT))) {
            if (!word.isBlank()) keywords.merge(word, 1L, Long::sum);
        }
    }

    private Map<String, Object> getICalWorkEfforts(Map<String, ?> input) {
        String publishWorkEffortId = string(input.get("workEffortId"));
        Set<String> ids = new LinkedHashSet<>();
        for (Object assignment : entities.findByEquality(
                entities.entityClass("WorkEffortPartyAssignment"),
                Map.of("workEffortId", publishWorkEffortId, "roleTypeId", "ICAL_MEMBER"))) {
            if (!active(assignment)) continue;
            String partyId = string(property(assignment, "partyId"));
            for (Object assigned : entities.findByEquality(
                    entities.entityClass("WorkEffortAndPartyAssign"), Map.of("partyId", partyId))) {
                if (icalCandidate(assigned, input)) ids.add(string(property(assigned, "workEffortId")));
            }
        }
        for (Object fixedAsset : entities.findByEquality(
                entities.entityClass("WorkEffortFixedAssetAssign"), Map.of("workEffortId", publishWorkEffortId))) {
            if (!active(fixedAsset)) continue;
            String fixedAssetId = string(property(fixedAsset, "fixedAssetId"));
            for (Object assigned : entities.findByEquality(
                    entities.entityClass("WorkEffortAndFixedAssetAssign"), Map.of("fixedAssetId", fixedAssetId))) {
                if (icalCandidate(assigned, input)) ids.add(string(property(assigned, "workEffortId")));
            }
        }
        Map<String, Object> associationFilter = new LinkedHashMap<>();
        associationFilter.put("workEffortIdFrom", publishWorkEffortId);
        for (Object associated : entities.findByEquality(
                entities.entityClass("WorkEffortAssocToView"), associationFilter)) {
            if (active(associated) && matches(associated, "workEffortTypeId", input.get("workEffortTypeId"))) {
                ids.add(string(property(associated, "workEffortId")));
            }
        }
        List<Map<String, Object>> workEfforts = new ArrayList<>();
        for (String id : ids) {
            if (id == null) continue;
            Object workEffort = findNullable("WorkEffort", Map.of("workEffortId", id));
            if (workEffort != null) workEfforts.add(entities.toMap(workEffort));
        }
        return Map.of("workEfforts", workEfforts);
    }

    private boolean icalCandidate(Object row, Map<String, ?> input) {
        return active(row)
                && "WES_PUBLIC".equals(property(row, "scopeEnumId"))
                && !"PUBLISH_PROPS".equals(property(row, "workEffortTypeId"))
                && matches(row, "workEffortTypeId", input.get("workEffortTypeId"));
    }

    private Map<String, Object> getPartyICalUrl(Map<String, ?> input) {
        String partyId = string(input.get("partyId"));
        List<?> contactMechs = entities.findByEquality(
                entities.entityClass("PartyContactWithPurpose"), Map.of("partyId", partyId));
        Timestamp now = Timestamp.from(Instant.now());
        Object infoString = contactMechs.stream()
                .filter(row -> dateActive(row, "purposeFromDate", "purposeThruDate", now))
                .filter(row -> dateActive(row, "contactFromDate", "contactThruDate", now))
                .filter(row -> Set.of("ICAL_URL", "PRIMARY_EMAIL")
                        .contains(string(property(row, "contactMechPurposeTypeId"))))
                .sorted((left, right) -> string(property(left, "contactMechPurposeTypeId"))
                        .compareTo(string(property(right, "contactMechPurposeTypeId"))))
                .map(row -> property(row, "infoString"))
                .findFirst()
                .orElse(null);
        return infoString == null ? Map.of() : Map.of("iCalUrl", infoString);
    }

    private boolean dateActive(Object entity, String fromField, String thruField, Timestamp now) {
        Timestamp from = timestamp(property(entity, fromField));
        Timestamp thru = timestamp(property(entity, thruField));
        return (from == null || !from.after(now)) && (thru == null || thru.after(now));
    }

    private Map<String, Object> createTimesheets(Map<String, ?> input) {
        Object rawPartyIds = input.get("partyIdList");
        if (!(rawPartyIds instanceof Iterable<?> partyIds)) return Map.of();
        List<Map<String, Object>> created = new ArrayList<>();
        for (Object partyId : partyIds) {
            if (partyId == null) continue;
            Map<String, Object> createParams = serviceInput("createTimesheet", input);
            createParams.put("partyId", partyId);
            created.add(dispatcher.execute("workeffort", "createTimesheet", createParams));
        }
        return Map.of();
    }

    private Map<String, Object> createTimesheetForThisWeek(Map<String, ?> input) {
        Timestamp requiredDate = timestamp(input.get("requiredDate"));
        LocalDate date = requiredDate == null
                ? LocalDate.now(ZoneOffset.UTC)
                : requiredDate.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
        LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        Timestamp from = Timestamp.from(weekStart.atStartOfDay().toInstant(ZoneOffset.UTC));
        Timestamp thru = Timestamp.from(weekStart.plusDays(7).atStartOfDay()
                .minusNanos(1_000_000).toInstant(ZoneOffset.UTC));
        String partyId = input.get("partyId") == null
                ? currentPartyId(input) : string(input.get("partyId"));
        if (partyId == null) throw new MigrationException(HttpStatus.BAD_REQUEST, "partyId is required");
        List<?> existing = entities.findByEquality(entities.entityClass("Timesheet"),
                Map.of("partyId", partyId, "fromDate", from, "thruDate", thru));
        if (!existing.isEmpty()) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Timesheet already exists for this week");
        }
        Map<String, Object> createParams = serviceInput("createTimesheet", input);
        createParams.put("partyId", partyId);
        createParams.put("fromDate", from);
        createParams.put("thruDate", thru);
        return dispatcher.execute("workeffort", "createTimesheet", createParams);
    }

    private Map<String, Object> addTimesheetToInvoice(String service, Map<String, ?> input) {
        Map<String, Object> invoice = "addTimesheetToNewInvoice".equals(service)
                ? invoices.createInvoice(input)
                : invoices.findInvoice(string(input.get("invoiceId")));
        if (invoice.isEmpty()) throw new MigrationException(HttpStatus.NOT_FOUND, "Invoice not found");
        invoices.updateInvoice(Map.of("invoiceId", invoice.get("invoiceId"), "statusId", "INVOICE_IN_PROCESS"));
        invoices.createInvoiceItem(input);
        return invoice;
    }

    private Map<String, Object> addWorkEffortTimeToInvoice(String service, Map<String, ?> input) {
        Map<String, Object> invoice = "addWorkEffortTimeToNewInvoice".equals(service)
                ? invoices.createInvoice(input)
                : invoices.findInvoice(string(input.get("invoiceId")));
        if (invoice.isEmpty()) throw new MigrationException(HttpStatus.NOT_FOUND, "Invoice not found");
        for (Object entry : entities.findByEquality(entities.entityClass("TimeEntry"),
                Map.of("workEffortId", input.get("workEffortId")))) {
            Map<String, Object> item = new LinkedHashMap<>(input);
            item.put("invoiceId", invoice.get("invoiceId"));
            item.put("description", entities.get(entry, "description"));
            invoices.createInvoiceItem(item);
        }
        return invoice;
    }

    private Map<String, Object> checkTimesheetStatus(Map<String, ?> input) {
        Object timesheetId = input.get("timesheetId");
        if (timesheetId == null) return Map.of("statusId", "");
        Object timeEntry = input.get("timeEntryId") == null ? null
                : findNullable("TimeEntry", Map.of("timeEntryId", input.get("timeEntryId")));
        Object timesheet = findNullable("Timesheet", Map.of("timesheetId", timesheetId));
        if (timesheet == null) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Timesheet could not be found");
        }
        String status = string(entities.get(timesheet, "statusId"));
        if (input.get("invoiceId") == null && !"TIMESHEET_IN_PROCESS".equals(status)) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Timesheet is not in process");
        }
        if (timeEntry != null && input.get("timesheetId") == null) {
            timesheetId = entities.get(timeEntry, "timesheetId");
        }
        return Map.of("statusId", status == null ? "" : status);
    }

    private Map<String, Object> updateTimeEntry(Map<String, ?> input) {
        Object entry = find("TimeEntry", input);
        Map<String, Object> statusInput = new LinkedHashMap<>(input);
        statusInput.putIfAbsent("timesheetId", entities.get(entry, "timesheetId"));
        checkTimesheetStatus(statusInput);
        Map<String, Object> fields = new LinkedHashMap<>(input);
        fields.remove("invoiceId");
        fields.remove("invoiceItemSeqId");
        Timestamp newFrom = timestamp(fields.get("fromDate"));
        Timestamp newThru = timestamp(fields.get("thruDate"));
        Timestamp oldFrom = timestamp(entities.get(entry, "fromDate"));
        Timestamp oldThru = timestamp(entities.get(entry, "thruDate"));
        if (fields.get("hours") == null && (newFrom != null || newThru != null)
                && (!java.util.Objects.equals(newFrom, oldFrom) || !java.util.Objects.equals(newThru, oldThru))) {
            Timestamp from = newFrom == null ? oldFrom : newFrom;
            Timestamp thru = newThru == null ? oldThru : newThru;
            if (from != null && thru != null) {
                fields.put("hours", (thru.getTime() - from.getTime()) / 3_600_000.0);
            }
        }
        updateEntity(entry, fields);
        if (input.get("invoiceId") != null && entities.get(entry, "invoiceId") == null) {
            Map<String, Object> invoiceFields = new LinkedHashMap<>();
            invoiceFields.put("invoiceId", input.get("invoiceId"));
            invoiceFields.put("invoiceItemSeqId", input.get("invoiceItemSeqId"));
            updateEntity(entry, invoiceFields);
        }
        return entities.toMap(entry);
    }

    private Map<String, Object> getTimeEntryRate(Map<String, ?> input) {
        Object entry = find("TimeEntry", Map.of("timeEntryId", input.get("timeEntryId")));
        Map<String, Object> criteria = new LinkedHashMap<>();
        criteria.put("workEffortId", entities.get(entry, "workEffortId"));
        criteria.put("partyId", entities.get(entry, "partyId"));
        criteria.put("rateTypeId", entities.get(entry, "rateTypeId"));
        criteria.put("rateCurrencyUomId", input.get("currencyUomId"));
        criteria.put("emplPositionTypeId", input.get("emplPositionTypeId"));
        if (criteria.get("partyId") == null && entities.get(entry, "timesheetId") != null) {
            Object timesheet = findNullable("Timesheet",
                    Map.of("timesheetId", entities.get(entry, "timesheetId")));
            if (timesheet != null) criteria.put("partyId", entities.get(timesheet, "partyId"));
        }
        Map<String, Object> rate = rates.getRateAmount(criteria);
        return Map.of("rateAmount", rate.get("rateAmount"));
    }

    private Map<String, Object> executeStatusWrappedAuto(String name, Map<String, ?> input) {
        checkTimesheetStatus(input);
        String autoService = name;
        ServiceHandlerRegistry.ServiceDefinition definition = registry.find("workeffort", autoService)
                .orElseThrow(() -> new MigrationException(HttpStatus.NOT_FOUND, "Unknown service: " + name));
        return entityAuto.execute(definition, input);
    }

    private void requireCorePermission(String action, String workEffortId, String parentId) {
        permissions.requireGenericPermission(action, workEffortId, parentId);
    }

    private void executeIndex(String workEffortId) {
        if (workEffortId != null) {
            execute("indexWorkEffortKeywords", Map.of("workEffortId", workEffortId));
        }
    }

    private Map<String, Object> serviceInput(String name, Map<String, ?> input) {
        ServiceHandlerRegistry.ServiceDefinition definition = registry.find("workeffort", name)
                .orElseThrow(() -> new MigrationException(HttpStatus.NOT_FOUND, "Unknown service: " + name));
        Map<String, Object> values = new LinkedHashMap<>();
        for (ServiceHandlerRegistry.ParamDefinition param : definition.params()) {
            if (("IN".equalsIgnoreCase(param.mode()) || "INOUT".equalsIgnoreCase(param.mode()))
                    && input.containsKey(param.name())) {
                values.put(param.name(), input.get(param.name()));
            }
        }
        return values;
    }

    private static double number(Object value) {
        if (value == null) return 0;
        if (value instanceof Number number) return number.doubleValue();
        return Double.parseDouble(value.toString());
    }

    private Map<String, Object> create(String entityName, Map<String, ?> values) {
        return entities.toMap(createEntity(entityName, values));
    }

    private Object createEntity(String entityName, Map<String, ?> values) {
        Class<?> type = entities.entityClass(entityName);
        Object entity = entities.newEntity(type);
        values.forEach((name, value) -> setIfPresent(entity, name, value));
        entityManager.persist(entity);
        return entity;
    }

    private void createIfEntityExists(String entityName, Map<String, ?> values) {
        try {
            createEntity(entityName, values);
        } catch (MigrationException exception) {
            if (!exception.getMessage().startsWith("Unknown entity")) throw exception;
        }
    }

    private void updateEntity(Object entity, Map<String, ?> values) {
        values.forEach((name, value) -> setIfPresent(entity, name, value));
    }

    private void setIfPresent(Object entity, String name, Object value) {
        try {
            entityManager.getMetamodel().entity(entity.getClass()).getAttribute(name);
            entities.set(entity, name, value);
        } catch (IllegalArgumentException ignored) {
            return;
        }
    }

    private Object find(String entityName, Map<String, ?> keys) {
        Object entity = findNullable(entityName, keys);
        if (entity == null) throw new MigrationException(HttpStatus.NOT_FOUND, entityName + " not found");
        return entity;
    }

    private Object findNullable(String entityName, Map<String, ?> keys) {
        return entities.findByPrimaryKey(entities.entityClass(entityName), keys);
    }

    private Object property(Object entity, String name) {
        try {
            return entities.get(entity, name);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private boolean active(Object entity) {
        Timestamp now = Timestamp.from(Instant.now());
        Object from = property(entity, "fromDate");
        Object thru = property(entity, "thruDate");
        return (from == null || !((Timestamp) from).after(now))
                && (thru == null || ((Timestamp) thru).after(now));
    }

    private boolean matches(Object entity, String field, Object expected) {
        return expected == null || expected.equals(property(entity, field));
    }

    private String currentPartyId(Map<String, ?> input) {
        var authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (authentication != null && security.getUserPartyIds().containsKey(authentication.getName())) {
            return security.getUserPartyIds().get(authentication.getName());
        }
        return string(input.get("partyId"));
    }

    private static String userLoginId(Map<String, ?> input) {
        Object raw = input.get("userLogin");
        return raw instanceof Map<?, ?> userLogin && userLogin.get("userLoginId") != null
                ? userLogin.get("userLoginId").toString()
                : null;
    }

    private static Timestamp timestamp(Object value) {
        if (value == null) return null;
        if (value instanceof Timestamp timestamp) return timestamp;
        return Timestamp.from(Instant.parse(value.toString()));
    }

    private static String value(Map<String, ?> input, String name, String fallback) {
        return input.get(name) == null ? fallback : input.get(name).toString();
    }

    private static String string(Object value) {
        return value == null ? null : value.toString();
    }
}
