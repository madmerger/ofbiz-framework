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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.apache.ofbiz.migration.datamodel.entity.CommunicationEvent;
import org.apache.ofbiz.migration.datamodel.entity.CommunicationEventRole;
import org.apache.ofbiz.migration.datamodel.entity.NoteData;
import org.apache.ofbiz.migration.datamodel.entity.UserLogin;
import org.apache.ofbiz.migration.datamodel.entity.WorkEffort;
import org.apache.ofbiz.migration.datamodel.entity.WorkEffortPartyAssignment;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;

@Component
@Profile("local")
public class LocalSeedData implements ApplicationRunner {
    private final EntityManager entityManager;

    public LocalSeedData(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (entityManager.find(UserLogin.class, "admin") != null) {
            return;
        }
        seedLogin("admin", "admin");
        seedLogin("editor", "DemoEditor");
        seedLogin("readonly", "DemoReadonly");
        seedNote("100", "admin", "Admin system note", Instant.now());
        seedNote("101", "admin", "Older system note", Instant.now().minus(1, ChronoUnit.DAYS));
        seedNote("102", "editor", "Editor system note", Instant.now());
        seedEvents();
        seedAssignments();
    }

    private void seedLogin(String userLoginId, String partyId) {
        UserLogin userLogin = new UserLogin();
        userLogin.setUserLoginId(userLoginId);
        userLogin.setPartyId(partyId);
        userLogin.setCurrentPassword("LOCAL-SEED-PLACEHOLDER-HASH");
        entityManager.persist(userLogin);
    }

    private void seedNote(String noteId, String partyId, String text, Instant createdAt) {
        NoteData note = new NoteData();
        note.setNoteId(noteId);
        note.setNoteParty(partyId);
        note.setNoteName("SYSTEMNOTE");
        note.setNoteInfo(text);
        note.setNoteDateTime(Timestamp.from(createdAt));
        entityManager.persist(note);
    }

    private void seedEvents() {
        seedEvent("LOCAL-COM-OPEN", "COM_ENTERED", Instant.now().minus(1, ChronoUnit.HOURS));
        seedEvent("LOCAL-COM-DONE", "COM_ROLE_COMPLETED", Instant.now().minus(1, ChronoUnit.DAYS));
    }

    private void seedEvent(String eventId, String statusId, Instant entryDate) {
        CommunicationEvent event = new CommunicationEvent();
        event.setCommunicationEventId(eventId);
        event.setStatusId(statusId);
        event.setEntryDate(Timestamp.from(entryDate));
        entityManager.persist(event);
        CommunicationEventRole role = new CommunicationEventRole();
        role.setCommunicationEventId(eventId);
        role.setPartyId("admin");
        role.setRoleTypeId("LOCAL_ROLE");
        role.setStatusId("CRS_ACCEPTED");
        entityManager.persist(role);
    }

    private void seedAssignments() {
        Instant now = Instant.now();
        seedAssignment("LOCAL-TASK-1", "TASK", "PAS_ASSIGNED", now.minus(2, ChronoUnit.HOURS), null);
        seedAssignment("LOCAL-TASK-2", "TASK", "PAS_ASSIGNED", now.minus(1, ChronoUnit.HOURS), null);
        seedAssignment(
                "LOCAL-TASK-EXPIRED", "TASK", "PAS_ASSIGNED",
                now.minus(2, ChronoUnit.DAYS), now.minus(1, ChronoUnit.DAYS));
    }

    private void seedAssignment(
            String workEffortId, String type, String status, Instant fromDate, Instant thruDate) {
        WorkEffort effort = new WorkEffort();
        effort.setWorkEffortId(workEffortId);
        effort.setWorkEffortTypeId(type);
        entityManager.persist(effort);
        WorkEffortPartyAssignment assignment = new WorkEffortPartyAssignment();
        assignment.setWorkEffortId(workEffortId);
        assignment.setPartyId("admin");
        assignment.setRoleTypeId("LOCAL_ROLE");
        assignment.setFromDate(Timestamp.from(fromDate));
        assignment.setStatusId(status);
        assignment.setThruDate(thruDate == null ? null : Timestamp.from(thruDate));
        entityManager.persist(assignment);
    }
}
