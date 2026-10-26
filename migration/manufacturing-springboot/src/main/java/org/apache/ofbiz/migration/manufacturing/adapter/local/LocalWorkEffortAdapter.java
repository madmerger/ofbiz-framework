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
package org.apache.ofbiz.migration.manufacturing.adapter.local;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jakarta.persistence.EntityManager;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.manufacturing.WorkEffortPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LocalWorkEffortAdapter implements WorkEffortPort {
    private static final Set<String> WORK_EFFORT_FIELDS = Set.of(
            "workEffortId", "workEffortTypeId", "currentStatusId", "lastStatusUpdate", "workEffortPurposeTypeId",
            "workEffortParentId", "scopeEnumId", "priority", "percentComplete", "workEffortName", "showAsEnumId",
            "sendNotificationEmail", "description", "locationDesc", "estimatedStartDate", "estimatedCompletionDate",
            "actualStartDate", "actualCompletionDate", "estimatedMilliSeconds", "estimatedSetupMillis",
            "estimateCalcMethod", "actualMilliSeconds", "actualSetupMillis", "totalMilliSecondsAllowed",
            "totalMoneyAllowed", "moneyUomId", "specialTerms", "timeTransparency", "universalId",
            "sourceReferenceId", "fixedAssetId", "facilityId", "infoUrl", "recurrenceInfoId", "tempExprId",
            "runtimeDataId", "noteId", "serviceLoaderName", "quantityToProduce", "quantityProduced",
            "quantityRejected", "reservPersons", "reserv2ndPPPerc", "reservNthPPPerc", "accommodationMapId",
            "accommodationSpotId", "revisionNumber", "createdDate", "createdByUserLogin", "lastModifiedDate",
            "lastModifiedByUserLogin");
    private static final Set<String> GOOD_STANDARD_FIELDS = Set.of(
            "workEffortId", "productId", "workEffortGoodStdTypeId", "fromDate", "thruDate", "statusId",
            "estimatedQuantity", "estimatedCost");
    private static final Set<String> WORK_EFFORT_ASSOC_FIELDS = Set.of(
            "workEffortIdFrom", "workEffortIdTo", "workEffortAssocTypeId", "sequenceNum", "fromDate", "thruDate");
    private final EntityRegistry entities;
    private final EntityManager entityManager;

    public LocalWorkEffortAdapter(EntityRegistry entities, EntityManager entityManager) {
        this.entities = entities;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> findWorkEffort(String id) {
        Object value = entities.findByPrimaryKey(entities.entityClass("WorkEffort"), Map.of("workEffortId", id));
        return value == null ? null : entities.toMap(value);
    }

    @Override
    public Map<String, Object> createWorkEffort(Map<String, ?> values) {
        return create("WorkEffort", values);
    }

    @Override
    public Map<String, Object> updateWorkEffort(String id, Map<String, ?> values) {
        Object value = entities.findByPrimaryKey(entities.entityClass("WorkEffort"), Map.of("workEffortId", id));
        if (value == null) {
            return null;
        }
        values.forEach((name, field) -> entities.set(value, name, field));
        return entities.toMap(value);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> findChildTasks(String parentId) {
        return entities.findByEquality(entities.entityClass("WorkEffort"),
                Map.of("workEffortParentId", parentId, "workEffortTypeId", "PROD_ORDER_TASK")).stream()
                .map(entities::toMap)
                .sorted(Comparator.comparing(value -> decimal(value.get("priority"))))
                .toList();
    }

    @Override
    public Map<String, Object> createWorkEffortGoodStandard(Map<String, ?> values) {
        return create("WorkEffortGoodStandard", values);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> findWorkEffortGoodStandards(Map<String, ?> filters) {
        return entities.findByEquality(entities.entityClass("WorkEffortGoodStandard"), filters).stream()
                .map(entities::toMap).toList();
    }

    @Override
    public Map<String, Object> createWorkEffortAssoc(Map<String, ?> values) {
        return create("WorkEffortAssoc", values);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> findWorkEffortAssocs(Map<String, ?> filters) {
        return entities.findByEquality(entities.entityClass("WorkEffortAssoc"), filters).stream()
                .map(entities::toMap).toList();
    }

    private Map<String, Object> create(String name, Map<String, ?> values) {
        Set<String> allowed = switch (name) {
            case "WorkEffort" -> WORK_EFFORT_FIELDS;
            case "WorkEffortGoodStandard" -> GOOD_STANDARD_FIELDS;
            case "WorkEffortAssoc" -> WORK_EFFORT_ASSOC_FIELDS;
            default -> Set.of();
        };
        validateFields(name, values, allowed);
        Object entity = entities.newEntity(entities.entityClass(name));
        values.forEach((field, value) -> entities.set(entity, field, value));
        entityManager.persist(entity);
        return entities.toMap(entity);
    }

    private static BigDecimal decimal(Object value) {
        return value == null ? BigDecimal.ZERO : new BigDecimal(value.toString());
    }

    private static void validateFields(String entityName, Map<String, ?> values, Set<String> allowed) {
        Set<String> unknown = new java.util.HashSet<>(values.keySet());
        unknown.removeAll(allowed);
        if (!unknown.isEmpty()) {
            throw new MigrationException(HttpStatus.BAD_REQUEST,
                    "Undeclared " + entityName + " fields: " + unknown);
        }
    }
}
