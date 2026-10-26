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
package org.apache.ofbiz.migration.manufacturing;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import jakarta.persistence.EntityManager;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("local")
public class LocalSeedData implements CommandLineRunner {
    private final EntityRegistry entities;
    private final EntityManager entityManager;
    private final WorkEffortPort workEfforts;

    public LocalSeedData(EntityRegistry entities, EntityManager entityManager, WorkEffortPort workEfforts) {
        this.entities = entities;
        this.entityManager = entityManager;
        this.workEfforts = workEfforts;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (entities.findByPrimaryKey(entities.entityClass("Product"), Map.of("productId", "MFG_FG")) != null) {
            return;
        }
        seedProduct("MFG_FG", "FINISHED_GOOD", "Demo finished good");
        seedProduct("MFG_RAW1", "RAW_MATERIAL", "Demo raw material one");
        seedProduct("MFG_RAW2", "RAW_MATERIAL", "Demo raw material two");
        create("Facility", Map.of(
                "facilityId", "MFG_PLANT",
                "facilityTypeId", "PLANT",
                "facilityName", "Manufacturing Demo Plant",
                "ownerPartyId", "Company"));

        workEfforts.createWorkEffort(Map.of(
                "workEffortId", "MFG_ROUTING",
                "workEffortTypeId", "ROUTING",
                "workEffortName", "Demo production routing"));
        workEfforts.createWorkEffort(Map.of(
                "workEffortId", "MFG_TASK_10",
                "workEffortTypeId", "ROU_TASK",
                "workEffortName", "Preparation",
                "estimatedSetupMillis", new BigDecimal("1000"),
                "estimatedMilliSeconds", new BigDecimal("2000")));
        workEfforts.createWorkEffort(Map.of(
                "workEffortId", "MFG_TASK_20",
                "workEffortTypeId", "ROU_TASK",
                "workEffortName", "Assembly",
                "estimatedSetupMillis", new BigDecimal("500"),
                "estimatedMilliSeconds", new BigDecimal("3000")));
        Timestamp now = Timestamp.from(Instant.now());
        workEfforts.createWorkEffortAssoc(Map.of(
                "workEffortIdFrom", "MFG_ROUTING",
                "workEffortIdTo", "MFG_TASK_10",
                "workEffortAssocTypeId", "ROUTING_COMPONENT",
                "sequenceNum", new BigDecimal("10"),
                "fromDate", now));
        workEfforts.createWorkEffortAssoc(Map.of(
                "workEffortIdFrom", "MFG_ROUTING",
                "workEffortIdTo", "MFG_TASK_20",
                "workEffortAssocTypeId", "ROUTING_COMPONENT",
                "sequenceNum", new BigDecimal("20"),
                "fromDate", now));
        workEfforts.createWorkEffortGoodStandard(Map.of(
                "workEffortId", "MFG_ROUTING",
                "productId", "MFG_FG",
                "workEffortGoodStdTypeId", "ROU_PROD_TEMPLATE",
                "fromDate", now));
        Map<String, Object> cost = new LinkedHashMap<>();
        cost.put("costComponentId", "MFG_COST_DEMO");
        cost.put("costComponentTypeId", "ACTUAL_MATERIAL_COST");
        cost.put("productId", "MFG_FG");
        cost.put("fromDate", now);
        cost.put("cost", BigDecimal.ONE);
        create("CostComponent", cost);
    }

    private void seedProduct(String id, String type, String name) {
        create("Product", Map.of(
                "productId", id,
                "productTypeId", type,
                "internalName", name,
                "isVirtual", "N"));
    }

    private Object create(String entityName, Map<String, ?> values) {
        Object entity = entities.newEntity(entities.entityClass(entityName));
        values.forEach((field, value) -> entities.set(entity, field, value));
        entityManager.persist(entity);
        return entity;
    }
}
