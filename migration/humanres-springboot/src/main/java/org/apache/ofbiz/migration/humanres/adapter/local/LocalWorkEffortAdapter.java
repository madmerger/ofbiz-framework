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
package org.apache.ofbiz.migration.humanres.adapter.local;

import java.sql.Timestamp;
import jakarta.persistence.EntityManager;
import org.apache.ofbiz.migration.humanres.SequenceService;
import org.apache.ofbiz.migration.humanres.WorkEffortPort;
import org.apache.ofbiz.migration.humanres.adapter.local.model.WorkEffortPartyAssignmentRecord;
import org.apache.ofbiz.migration.humanres.adapter.local.model.WorkEffortRecord;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class LocalWorkEffortAdapter implements WorkEffortPort {
    private final EntityManager entityManager;
    private final SequenceService sequenceService;

    public LocalWorkEffortAdapter(EntityManager entityManager, SequenceService sequenceService) {
        this.entityManager = entityManager;
        this.sequenceService = sequenceService;
    }

    @Override
    public boolean publicHolidayExists(Timestamp startDate) {
        Long count = entityManager.createQuery(
                        "select count(effort) from LocalWorkEffort effort "
                                + "where effort.workEffortTypeId = 'PUBLIC_HOLIDAY' "
                                + "and effort.estimatedStartDate = :startDate",
                        Long.class)
                .setParameter("startDate", startDate)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public String createPublicHoliday(
            String name, Timestamp startDate, Timestamp endDate, String assignedPartyId) {
        String workEffortId = sequenceService.nextId("WorkEffort");
        entityManager.persist(new WorkEffortRecord(
                workEffortId, "PUBLIC_HOLIDAY", name, startDate, endDate));
        if (assignedPartyId != null && !assignedPartyId.isBlank()) {
            entityManager.persist(new WorkEffortPartyAssignmentRecord(
                    workEffortId, assignedPartyId, "INTERNAL_ORGANIZATIO", startDate));
        }
        return workEffortId;
    }
}
