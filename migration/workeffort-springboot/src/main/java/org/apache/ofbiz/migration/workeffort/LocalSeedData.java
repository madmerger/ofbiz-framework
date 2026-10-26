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
import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.SequenceService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("local")
public class LocalSeedData implements ApplicationRunner {
    private final EntityManager entityManager;
    private final EntityRegistry entities;
    private final SequenceService sequences;

    public LocalSeedData(EntityManager entityManager, EntityRegistry entities, SequenceService sequences) {
        this.entityManager = entityManager;
        this.entities = entities;
        this.sequences = sequences;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (String[] status : List.of(
                new String[] {"CAL_CREATED", "CAL_WORK_EFFORT"},
                new String[] {"CAL_IN_PROGRESS", "CAL_WORK_EFFORT"},
                new String[] {"CAL_COMPLETED", "CAL_WORK_EFFORT"},
                new String[] {"PRTYASGN_ASSIGNED", "PARTY_RELATIONSHIP"},
                new String[] {"TIMESHEET_CREATED", "TIMESHEET"},
                new String[] {"TIMESHEET_APPROVED", "TIMESHEET"},
                new String[] {"TIMESHEET_IN_PROCESS", "TIMESHEET"})) {
            seed("StatusItem", Map.of("statusId", status[0], "statusTypeId", status[1]));
        }
        seed("WorkEffortType", Map.of("workEffortTypeId", "TASK", "description", "Task"));
        seed("RoleType", Map.of("roleTypeId", "CAL_OWNER", "description", "Calendar owner"));
        seed("RoleType", Map.of("roleTypeId", "CAL_ATTENDEE", "description", "Calendar attendee"));
        seed("RoleType", Map.of("roleTypeId", "PRTYASGN", "description", "Work effort assignment"));
        for (String partyId : List.of("DemoEmployee", "DemoAuditor")) {
            seed("Party", Map.of("partyId", partyId, "partyTypeId", "PERSON"));
            seed("PartyRole", Map.of("partyId", partyId, "roleTypeId", "CAL_ATTENDEE"));
        }
        seed("StatusValidChange", Map.of("statusId", "CAL_CREATED", "statusIdTo", "CAL_IN_PROGRESS"));
        seed("StatusValidChange", Map.of("statusId", "CAL_IN_PROGRESS", "statusIdTo", "CAL_COMPLETED"));
        seed("WorkEffort", Map.of(
                "workEffortId", "WE-HOLIDAY",
                "workEffortTypeId", "EVENT",
                "workEffortName", "Demo holiday",
                "currentStatusId", "CAL_CREATED",
                "estimatedStartDate", Timestamp.from(Instant.now()),
                "estimatedCompletionDate", Timestamp.from(Instant.now().plusSeconds(86400))));
        Map<String, Object> rate = new LinkedHashMap<>();
        rate.put("workEffortId", "WE-HOLIDAY");
        rate.put("partyId", "DemoEmployee");
        rate.put("rateTypeId", "RATE_HOURLY");
        rate.put("rateCurrencyUomId", "USD");
        rate.put("periodTypeId", "RATE_HOUR");
        rate.put("rateAmount", "125.00");
        rate.put("fromDate", Timestamp.from(Instant.now()));
        rate.put("emplPositionTypeId", "EMPLOYEE");
        seed("RateAmount", rate);
    }

    private void seed(String entityName, Map<String, ?> values) {
        Class<?> type;
        try {
            type = entities.entityClass(entityName);
        } catch (RuntimeException exception) {
            return;
        }
        try {
            if (entities.findByPrimaryKey(type, values) != null) return;
        } catch (RuntimeException ignored) {
            return;
        }
        Object entity = entities.newEntity(type);
        values.forEach((name, value) -> {
            try {
                entityManager.getMetamodel().entity(type).getAttribute(name);
                entities.set(entity, name, value);
            } catch (IllegalArgumentException ignored) {
                return;
            }
        });
        if (entityManager.getMetamodel().entity(type).getSingularAttributes().stream()
                .filter(attribute -> attribute.isId())
                .allMatch(attribute -> {
                    try {
                        return entities.get(entity, attribute.getName()) != null;
                    } catch (RuntimeException exception) {
                        return false;
                    }
                })) {
            entityManager.persist(entity);
        }
    }
}
