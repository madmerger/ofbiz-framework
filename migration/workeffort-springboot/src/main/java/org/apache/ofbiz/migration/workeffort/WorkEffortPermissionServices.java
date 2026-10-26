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

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public final class WorkEffortPermissionServices {
    private final EntityRegistry entities;
    private final WorkEffortSecurityProperties properties;

    public WorkEffortPermissionServices(EntityRegistry entities, WorkEffortSecurityProperties properties) {
        this.entities = entities;
        this.properties = properties;
    }

    public Map<String, Object> workEffortManagerPermission(Map<String, ?> input) {
        String action = string(input.get("mainAction"), "VIEW");
        return result(hasAction(action), "WorkEffort manager permission denied");
    }

    public Map<String, Object> workEffortGenericPermission(Map<String, ?> input) {
        String action = string(input.get("mainAction"), "VIEW").toUpperCase();
        if (hasAction(action)) return result(true, "");
        Set<String> authorities = authorities();
        if (!authorities.contains("WORKEFFORTMGR_ROLE_ADMIN")
                && !authorities.contains("WORKEFFORTMGR_ROLE_" + action)) {
            return result(false, "WorkEffort role permission denied");
        }
        String partyId = currentPartyId(input);
        String workEffortId = string(input.get("workEffortId"), null);
        boolean allowed;
        if ("CREATE".equals(action)) {
            String parentId = string(input.get("workEffortParentId"), null);
            allowed = parentId == null || hasAnyRoleInTree(parentId, partyId);
        } else if ("UPDATE".equals(action)) {
            if (workEffortId == null) return result(false, "Missing workEffortId");
            allowed = hasOwnerRoleInTree(workEffortId, partyId);
            Object workEffort = find("WorkEffort", workEffortId);
            String existingParent = workEffort == null ? null
                    : string(entities.get(workEffort, "workEffortParentId"), null);
            String requestedParent = string(input.get("workEffortParentId"), null);
            if (requestedParent != null && requestedParent.isBlank()) requestedParent = null;
            if (allowed && requestedParent != null && !requestedParent.equals(existingParent)) {
                allowed = hasOwnerRoleInTree(requestedParent, partyId);
            }
            if (!allowed) allowed = hasPartyGroupRoleInTree(workEffortId, partyId);
        } else {
            allowed = true;
        }
        return result(allowed, allowed ? "" : "WorkEffort role permission denied");
    }

    public Map<String, Object> timesheetUpdatePermission(Map<String, ?> input) {
        Map<String, Object> genericInput = new java.util.LinkedHashMap<>(input);
        genericInput.put("mainAction", "UPDATE");
        Map<String, Object> genericResult = workEffortGenericPermission(genericInput);
        if (!Boolean.TRUE.equals(genericResult.get("hasPermission"))) return genericResult;
        String partyId = currentPartyId(input);
        String requestedPartyId = string(input.get("partyId"), null);
        if (requestedPartyId == null || !requestedPartyId.equals(partyId)) {
            return result(false, "Timesheet update permission denied");
        }
        String workEffortId = string(input.get("workEffortId"), null);
        if (workEffortId == null) return result(true, "");
        List<?> assignments = entities.findByEquality(
                entities.entityClass("WorkEffortPartyAssignment"),
                Map.of("workEffortId", workEffortId, "partyId", partyId));
        return assignments.stream().anyMatch(assignment -> isActive(assignment)
                        && "CAL_OWNER".equals(string(entities.get(assignment, "roleTypeId"), "")))
                ? result(true, "") : result(false, "Timesheet update permission denied");
    }

    public Map<String, Object> workEffortICalendarPermission(Map<String, ?> input) {
        String action = string(input.get("mainAction"), "VIEW");
        boolean manager = hasAction(action);
        String workEffortId = string(input.get("workEffortId"), null);
        if (!manager || workEffortId == null) {
            return result(manager, manager ? "" : "iCalendar permission denied");
        }
        Object workEffort = find("WorkEffort", workEffortId);
        if (workEffort == null) return result(false, "iCalendar permission denied");
        boolean owner = false;
        boolean organizer = false;
        boolean delegate = false;
        for (Object assignment : entities.findByEquality(
                entities.entityClass("WorkEffortPartyAssignment"),
                Map.of("workEffortId", workEffortId, "partyId", currentPartyId(input)))) {
            if (!isActive(assignment)
                    || !"PRTYASGN_ASSIGNED".equals(string(entities.get(assignment, "statusId"), ""))) continue;
            String role = string(entities.get(assignment, "roleTypeId"), "");
            owner |= "CAL_OWNER".equals(role);
            organizer |= "CAL_OWNER".equals(role) || "CAL_ORGANIZER".equals(role);
            delegate |= "CAL_OWNER".equals(role) || "CAL_DELEGATE".equals(role);
        }
        boolean publishProperties = "PUBLISH_PROPS".equals(string(entities.get(workEffort, "workEffortTypeId"), ""));
        boolean confidential = "WES_CONFIDENTIAL".equals(string(entities.get(workEffort, "scopeEnumId"), ""));
        boolean allowed = publishProperties ? owner || confidential && delegate : organizer;
        return result(allowed, allowed ? "" : "iCalendar permission denied");
    }

    public void requireGenericPermission(String action, String workEffortId, String parentId) {
        if (hasAction(action)) return;
        if (!authorities().contains("WORKEFFORTMGR_ROLE_ADMIN")
                && !authorities().contains("WORKEFFORTMGR_ROLE_" + action)) {
            throw new MigrationException(HttpStatus.FORBIDDEN, "WorkEffort permission denied");
        }
        Map<String, Object> input = Map.of(
                "mainAction", action,
                "workEffortId", workEffortId == null ? "" : workEffortId,
                "workEffortParentId", parentId == null ? "" : parentId,
                "partyId", currentPartyId(Map.of()));
        if (!Boolean.TRUE.equals(workEffortGenericPermission(input).get("hasPermission"))) {
            throw new MigrationException(HttpStatus.FORBIDDEN, "WorkEffort permission denied");
        }
    }

    private boolean hasAnyRoleInTree(String workEffortId, String partyId) {
        return workEffortTree(workEffortId).stream().anyMatch(id -> hasAssignment(id, partyId, Set.of()));
    }

    private boolean hasOwnerRoleInTree(String workEffortId, String partyId) {
        for (String currentId : workEffortTree(workEffortId)) {
            if (hasAssignment(currentId, partyId, Set.of("CAL_OWNER"))) return true;
        }
        return false;
    }

    private boolean hasPartyGroupRoleInTree(String workEffortId, String partyId) {
        Class<?> assignmentType = entities.entityClass("WorkEffortPartyAssignment");
        for (String currentId : workEffortTree(workEffortId)) {
            for (Object assignment : entities.findByEquality(assignmentType, Map.of("workEffortId", currentId))) {
                if (!isActive(assignment) || !Set.of("CAL_OWNER", "CAL_DELEGATE")
                        .contains(string(entities.get(assignment, "roleTypeId"), ""))) {
                    continue;
                }
                String groupPartyId = string(entities.get(assignment, "partyId"), null);
                if (groupPartyId == null || groupPartyId.equals(partyId)) continue;
                Object group = findParty(groupPartyId);
                if (group == null || !"PARTY_GROUP".equals(string(entities.get(group, "partyTypeId"), ""))) continue;
                List<?> relationships = entities.findByEquality(entities.entityClass("PartyRelationship"),
                        Map.of("partyIdFrom", groupPartyId, "partyIdTo", partyId));
                if (relationships.stream().anyMatch(this::isActive)) return true;
            }
        }
        return false;
    }

    private boolean hasAssignment(String workEffortId, String partyId, Set<String> roleTypes) {
        if (partyId == null) return false;
        for (Object assignment : entities.findByEquality(
                entities.entityClass("WorkEffortPartyAssignment"), Map.of("workEffortId", workEffortId))) {
            if (partyId.equals(entities.get(assignment, "partyId"))
                    && (roleTypes.isEmpty() || roleTypes.contains(string(entities.get(assignment, "roleTypeId"), "")))
                    && isActive(assignment)) return true;
        }
        return false;
    }

    private List<String> workEffortTree(String workEffortId) {
        List<String> workEffortIds = new java.util.ArrayList<>();
        Set<String> seen = new HashSet<>();
        String currentId = workEffortId;
        while (currentId != null && seen.add(currentId)) {
            workEffortIds.add(currentId);
            Object workEffort = find("WorkEffort", currentId);
            currentId = workEffort == null
                    ? null
                    : string(entities.get(workEffort, "workEffortParentId"), null);
        }
        return workEffortIds;
    }

    private Object find(String entityName, String id) {
        return entities.findByPrimaryKey(entities.entityClass(entityName), Map.of("workEffortId", id));
    }

    private Object findParty(String partyId) {
        return entities.findByPrimaryKey(entities.entityClass("Party"), Map.of("partyId", partyId));
    }

    private boolean isActive(Object row) {
        Timestamp now = Timestamp.from(Instant.now());
        Object from = entities.get(row, "fromDate");
        Object thru = entities.get(row, "thruDate");
        return (from == null || !((Timestamp) from).after(now))
                && (thru == null || ((Timestamp) thru).after(now));
    }

    private String currentPartyId(Map<String, ?> input) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String configured = properties.getUserPartyIds().get(authentication.getName());
            if (configured != null) return configured;
        }
        return string(input.get("partyId"), null);
    }

    private static boolean hasAction(String action) {
        Set<String> authorities = authorities();
        return authorities.contains("WORKEFFORTMGR_ADMIN")
                || authorities.contains("WORKEFFORTMGR_" + action.toUpperCase());
    }

    private static Set<String> authorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null
                ? Set.of()
                : AuthorityUtils.authorityListToSet(authentication.getAuthorities());
    }

    private static Map<String, Object> result(boolean allowed, String failure) {
        return Map.of("hasPermission", allowed, "failMessage", failure);
    }

    private static String string(Object value, String fallback) {
        return value == null ? fallback : value.toString();
    }
}
