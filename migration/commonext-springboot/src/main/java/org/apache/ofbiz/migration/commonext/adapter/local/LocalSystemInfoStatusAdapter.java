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
package org.apache.ofbiz.migration.commonext.adapter.local;

import java.sql.Timestamp;
import java.util.List;
import org.apache.ofbiz.migration.commonext.port.SystemInfoStatusPort;
import org.apache.ofbiz.migration.datamodel.entity.CommunicationEvent;
import org.apache.ofbiz.migration.datamodel.entity.WorkEffortPartyAssignment;
import org.springframework.stereotype.Component;
import jakarta.persistence.EntityManager;

@Component
public class LocalSystemInfoStatusAdapter implements SystemInfoStatusPort {
    private final EntityManager entityManager;

    public LocalSystemInfoStatusAdapter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<CommunicationEvent> openCommunicationEvents(String partyId) {
        return entityManager.createQuery(
                        "select event from CommunicationEvent event "
                                + "join CommunicationEventRole role "
                                + "on role.communicationEventId = event.communicationEventId "
                                + "where role.partyId = :partyId and event.statusId <> 'COM_ROLE_COMPLETED' "
                                + "order by event.entryDate desc",
                        CommunicationEvent.class)
                .setParameter("partyId", partyId)
                .getResultList();
    }

    @Override
    public List<WorkEffortPartyAssignment> openTaskAssignments(String partyId, Timestamp now) {
        return entityManager.createQuery(
                        "select assignment from WorkEffortPartyAssignment assignment "
                                + "join WorkEffort effort on effort.workEffortId = assignment.workEffortId "
                                + "where assignment.partyId = :partyId "
                                + "and assignment.statusId = 'PAS_ASSIGNED' "
                                + "and effort.workEffortTypeId = 'TASK' "
                                + "and (assignment.fromDate is null or assignment.fromDate <= :now) "
                                + "and (assignment.thruDate is null or assignment.thruDate > :now) "
                                + "order by assignment.fromDate desc",
                        WorkEffortPartyAssignment.class)
                .setParameter("partyId", partyId)
                .setParameter("now", now)
                .getResultList();
    }
}
