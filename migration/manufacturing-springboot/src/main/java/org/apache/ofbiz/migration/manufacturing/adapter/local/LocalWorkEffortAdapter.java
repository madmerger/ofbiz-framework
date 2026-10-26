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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import jakarta.persistence.EntityManager;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.manufacturing.WorkEffortPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LocalWorkEffortAdapter implements WorkEffortPort {
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
                .sorted(Comparator.comparing(value -> String.valueOf(value.getOrDefault("priority", ""))))
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
        Object entity = entities.newEntity(entities.entityClass(name));
        values.forEach((field, value) -> entities.set(entity, field, value));
        entityManager.persist(entity);
        return entities.toMap(entity);
    }
}
