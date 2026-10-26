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
package org.apache.ofbiz.migration.workeffort;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.apache.ofbiz.migration.workeffort.generated.ServiceCatalog;
import org.springframework.stereotype.Component;

@Component
public final class WorkEffortServiceRegistration {
    private static final Set<String> SIMPLE_SERVICES = Set.of(
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
    private static final Set<String> BUSINESS_SERVICES = Set.of(
            "createWorkEffort",
            "updateWorkEffort",
            "deleteWorkEffort",
            "createWorkEffortAndPartyAssign",
            "assignPartyToWorkEffort",
            "updatePartyToWorkEffortAssignment",
            "quickAssignPartyToWorkEffort",
            "quickAssignPartyToWorkEffortWithRole",
            "getWorkEffort",
            "getWorkEffortAssignedTasks",
            "getWorkEffortEventsByPeriod");
    private static final Set<String> STATUS_WRAPPERS = Set.of(
            "createTimeEntry", "updateTimesheet", "deleteTimeEntry", "deleteTimesheet");
    private static final Map<String, String> DEFAULT_ACTIONS = Map.ofEntries(
            Map.entry("makeCommunicationEventWorkEffort", "UPDATE"),
            Map.entry("createWorkEffortNote", "CREATE"),
            Map.entry("updateWorkEffortNote", "UPDATE"),
            Map.entry("createWorkEffortAssoc", "CREATE"),
            Map.entry("copyWorkEffortAssocs", "CREATE"),
            Map.entry("createWorkEffortAndAssoc", "CREATE"),
            Map.entry("assignInventoryToWorkEffort", "UPDATE"),
            Map.entry("createCommunicationEventWorkEff", "CREATE"),
            Map.entry("updateCommunicationEventWorkEff", "UPDATE"),
            Map.entry("deleteCommunicationEventWorkEff", "DELETE"),
            Map.entry("checkCustRequestItemExists", "VIEW"),
            Map.entry("setWorkEffortFixedAssetAssign", "UPDATE"),
            Map.entry("createWorkEffortFixedAssetAssign", "CREATE"),
            Map.entry("createWorkEffortReview", "CREATE"),
            Map.entry("indexWorkEffortKeywords", "UPDATE"),
            Map.entry("createWorkEffortKeyword", "CREATE"),
            Map.entry("createWorkEffortKeywords", "CREATE"),
            Map.entry("deleteWorkEffortKeywords", "DELETE"),
            Map.entry("workEffortManagerPermission", "VIEW"),
            Map.entry("workEffortGenericPermission", "VIEW"),
            Map.entry("timesheetUpdatePermission", "UPDATE"),
            Map.entry("workEffortICalendarPermission", "VIEW"),
            Map.entry("getICalWorkEfforts", "VIEW"),
            Map.entry("getPartyICalUrl", "VIEW"),
            Map.entry("createTimesheets", "CREATE"),
            Map.entry("createTimesheetForThisWeek", "CREATE"),
            Map.entry("addTimesheetToInvoice", "UPDATE"),
            Map.entry("addTimesheetToNewInvoice", "CREATE"),
            Map.entry("addWorkEffortTimeToInvoice", "UPDATE"),
            Map.entry("addWorkEffortTimeToNewInvoice", "CREATE"),
            Map.entry("checkTimesheetStatus", "VIEW"),
            Map.entry("updateTimeEntry", "UPDATE"),
            Map.entry("getTimeEntryRate", "VIEW"),
            Map.entry("deleteTimesheet", "DELETE"),
            Map.entry("deleteTimeEntry", "DELETE"),
            Map.entry("createWorkEffortAttribute", "CREATE"),
            Map.entry("updateWorkEffortAttribute", "UPDATE"),
            Map.entry("deleteWorkEffortAttribute", "DELETE"),
            Map.entry("createWorkEffortContent", "CREATE"),
            Map.entry("updateWorkEffortContent", "UPDATE"),
            Map.entry("deleteWorkEffortContent", "DELETE"),
            Map.entry("createTimesheetRole", "CREATE"),
            Map.entry("createWorkEffort", "CREATE"),
            Map.entry("updateWorkEffort", "UPDATE"),
            Map.entry("deleteWorkEffort", "DELETE"),
            Map.entry("createWorkEffortRequest", "CREATE"),
            Map.entry("deleteWorkEffortRequest", "DELETE"),
            Map.entry("createWorkEffortRequestItem", "CREATE"),
            Map.entry("deleteWorkEffortRequestItem", "DELETE"),
            Map.entry("createWorkEffortQuote", "CREATE"),
            Map.entry("deleteWorkEffortQuote", "DELETE"),
            Map.entry("createWorkRequirementFulfillment", "CREATE"),
            Map.entry("deleteWorkRequirementFulfillment", "DELETE"),
            Map.entry("createShoppingListWorkEffort", "CREATE"),
            Map.entry("deleteShoppingListWorkEffort", "DELETE"),
            Map.entry("createOrderHeaderWorkEffort", "CREATE"),
            Map.entry("deleteOrderHeaderWorkEffort", "DELETE"),
            Map.entry("createWorkEffortAndPartyAssign", "CREATE"),
            Map.entry("assignPartyToWorkEffort", "UPDATE"),
            Map.entry("updatePartyToWorkEffortAssignment", "UPDATE"),
            Map.entry("quickAssignPartyToWorkEffort", "UPDATE"),
            Map.entry("quickAssignPartyToWorkEffortWithRole", "UPDATE"),
            Map.entry("getWorkEffort", "VIEW"),
            Map.entry("getWorkEffortAssignedTasks", "VIEW"),
            Map.entry("getWorkEffortEventsByPeriod", "VIEW"),
            Map.entry("createTimeEntry", "CREATE"),
            Map.entry("updateTimesheet", "UPDATE"));

    private final ServiceHandlerRegistry registry;
    private final WorkEffortBusinessServices businessServices;

    public WorkEffortServiceRegistration(
            ServiceHandlerRegistry registry, WorkEffortBusinessServices businessServices) {
        this.registry = registry;
        this.businessServices = businessServices;
    }

    @PostConstruct
    public void register() {
        registry.registerCatalog("workeffort", ServiceCatalog.all());
        SIMPLE_SERVICES.forEach(name -> registerHandler(name, businessServices));
        BUSINESS_SERVICES.forEach(name -> registerHandler(name, businessServices));
        STATUS_WRAPPERS.forEach(name -> registerHandler(name, businessServices));
        ServiceCatalog.all().values().stream()
                .filter(definition -> definition.entity() != null
                        && (Set.of("WorkEffortAttribute", "WorkEffortContent").contains(definition.entity())
                                || "createTimesheetRole".equals(definition.name())
                                || "createWorkEffortEventReminder".equals(definition.name())))
                .forEach(definition -> registerHandler(definition.name(), businessServices));
        ServiceCatalog.all().values().stream()
                .filter(definition -> definition.entity() != null && definition.invoke() != null)
                .filter(definition -> definition.params().stream().anyMatch(param ->
                        param.mode() == null || param.mode().isBlank() || "None".equalsIgnoreCase(param.mode())))
                .forEach(definition -> registerHandler(definition.name(), businessServices));
    }

    void registerHandler(String name, WorkEffortBusinessServices handler) {
        String sensitiveEntity = "getTimeEntryRate".equals(name) ? "RateAmount" : null;
        registry.registerHandler("workeffort", name, actionFor(name), sensitiveEntity,
                input -> handler.execute(name, input));
    }

    private static String actionFor(String name) {
        String action = ServiceCatalog.find(name)
                .map(ServiceCatalog.ServiceDefinition::permissionMainAction)
                .filter(candidateAction -> candidateAction != null && !candidateAction.isBlank())
                .orElse(DEFAULT_ACTIONS.get(name));
        if (action == null) {
            throw new IllegalStateException("Missing explicit permission action for service: " + name);
        }
        return action;
    }
}
