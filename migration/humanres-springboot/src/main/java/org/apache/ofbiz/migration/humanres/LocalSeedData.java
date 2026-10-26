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
package org.apache.ofbiz.migration.humanres;

import java.util.List;
import java.util.Map;
import jakarta.persistence.EntityManager;
import org.apache.ofbiz.migration.humanres.adapter.local.model.PartyGroupRecord;
import org.apache.ofbiz.migration.humanres.adapter.local.model.PartyRecord;
import org.apache.ofbiz.migration.humanres.adapter.local.model.StatusItemRecord;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("local")
public class LocalSeedData implements CommandLineRunner {
    private final EntityManager entityManager;
    private final PartyPort partyPort;
    private final EntityAutoService entityAutoService;

    public LocalSeedData(
            EntityManager entityManager, PartyPort partyPort, EntityAutoService entityAutoService) {
        this.entityManager = entityManager;
        this.partyPort = partyPort;
        this.entityAutoService = entityAutoService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedStatuses();
        if (entityManager.find(PartyGroupRecord.class, "Company") != null) {
            return;
        }
        entityManager.persist(new PartyRecord("Company", "PARTY_GROUP"));
        entityManager.persist(new PartyGroupRecord("Company", "Company"));
        partyPort.ensureRole("Company", "INTERNAL_ORGANIZATIO");
        entityManager.persist(new PartyRecord("DEMO_DEPT", "PARTY_GROUP"));
        entityManager.persist(new PartyGroupRecord("DEMO_DEPT", "Human Resources"));
        partyPort.ensureRole("DEMO_DEPT", "INTERNAL_ORGANIZATIO");
        partyPort.createRelationship(
                "Company", "DEMO_DEPT", "GROUP_ROLLUP",
                "INTERNAL_ORGANIZATIO", "INTERNAL_ORGANIZATIO", null, EntityRegistry.now());

        String employeeId = partyPort.createEmployee(Map.of(
                "firstName", "Demo", "lastName", "Employee"));
        entityAutoService.createEntity("EmplPositionType", Map.of(
                "emplPositionTypeId", "DEMO_HR", "description", "Human Resources"));
        Map<String, Object> position = entityAutoService.createEntity("EmplPosition", Map.of(
                "emplPositionId", "DEMO_POSITION",
                "partyId", "DEMO_DEPT",
                "emplPositionTypeId", "DEMO_HR",
                "statusId", "EMPL_POS_ACTIVE",
                "actualFromDate", EntityRegistry.now()));
        entityAutoService.createEntity("EmplPositionFulfillment", Map.of(
                "emplPositionId", position.get("emplPositionId"),
                "partyId", employeeId,
                "fromDate", EntityRegistry.now()));
    }

    private void seedStatuses() {
        List<Status> statuses = List.of(
                new Status("EMPL_POS_PLANNEDFOR", "EMPL_POSITION_STATUS", "Planned For", "01"),
                new Status("EMPL_POS_ACTIVE", "EMPL_POSITION_STATUS", "Active/Open", "02"),
                new Status("EMPL_POS_INACTIVE", "EMPL_POSITION_STATUS", "Inactive/Closed", "03"),
                new Status("HR_DS_COMPLETE", "HR_DEGREE_STATUS", "Completed", "01"),
                new Status("HR_DS_INCOMPLETE", "HR_DEGREE_STATUS", "Incomplete", "02"),
                new Status("HR_DS_DEFERRED", "HR_DEGREE_STATUS", "Deferred", "03"),
                new Status("HR_JS_FULLTIME", "HR_JOB_STATUS", "Full time", "01"),
                new Status("HR_JS_PARTTIME", "HR_JOB_STATUS", "Part time", "02"),
                new Status("HR_JS_CONTRACTOR", "HR_JOB_STATUS", "Contractor", "03"),
                new Status("PQV_NOT_VERIFIED", "PARTYQUAL_VERIFY", "Not verified", "01"),
                new Status("PQV_VERIFIED", "PARTYQUAL_VERIFY", "Verified", "02"),
                new Status("IJP_APPROVED", "IJP_STATUS", "Approved", null),
                new Status("IJP_REJECTED", "IJP_STATUS", "Rejected", null),
                new Status("STATUS_PENDING", "RELOCATION_STATUS", "Pending", null),
                new Status("STATUS_RELOCATED", "RELOCATION_STATUS", "Relocated", null),
                new Status("TRAINING_APPLIED", "TRAINING_STATUS", "Applied", null),
                new Status("TRAINING_APPROVED", "TRAINING_STATUS", "Approved", null),
                new Status("TRAINING_ASSIGNED", "TRAINING_STATUS", "Assigned", null),
                new Status("TRAINING_PROPOSED", "TRAINING_STATUS", "Proposed", null),
                new Status("TRAINING_REJECTED", "TRAINING_STATUS", "Rejected", null),
                new Status("LEAVE_APPROVED", "LEAVE_STATUS", "Approved", null),
                new Status("LEAVE_CREATED", "LEAVE_STATUS", "Created", null),
                new Status("LEAVE_REJECTED", "LEAVE_STATUS", "Rejected", null));
        for (Status status : statuses) {
            if (entityManager.find(StatusItemRecord.class, status.id()) == null) {
                entityManager.persist(new StatusItemRecord(
                        status.id(), status.type(), status.description(), status.sequenceId()));
            }
        }
    }

    private record Status(String id, String type, String description, String sequenceId) {}
}
