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
package org.apache.ofbiz.migration.commonext;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.datamodel.entity.CommunicationEvent;
import org.apache.ofbiz.migration.datamodel.entity.CommunicationEventRole;
import org.apache.ofbiz.migration.datamodel.entity.NoteData;
import org.apache.ofbiz.migration.datamodel.entity.UserLogin;
import org.apache.ofbiz.migration.datamodel.entity.WorkEffort;
import org.apache.ofbiz.migration.datamodel.entity.WorkEffortPartyAssignment;
import jakarta.persistence.EntityManager;

final class CommonextTestSupport {
    private CommonextTestSupport() { }

    static void seedUserLogins(EntityManager entityManager) {
        seedUserLogin(entityManager, "admin", "admin");
        seedUserLogin(entityManager, "editor", "DemoEditor");
        seedUserLogin(entityManager, "readonly", "DemoReadonly");
        seedUserLogin(entityManager, "orphan", null);
    }

    static void seedUserLogin(EntityManager entityManager, String userLoginId, String partyId) {
        if (entityManager.find(UserLogin.class, userLoginId) != null) {
            return;
        }
        UserLogin userLogin = new UserLogin();
        userLogin.setUserLoginId(userLoginId);
        userLogin.setPartyId(partyId);
        userLogin.setCurrentPassword("LOCAL-SEED-PLACEHOLDER-HASH");
        entityManager.persist(userLogin);
    }

    static NoteData seedNote(
            EntityManager entityManager, String noteId, String partyId, String name, String info, Instant createdAt) {
        NoteData note = new NoteData();
        note.setNoteId(noteId);
        note.setNoteParty(partyId);
        note.setNoteName(name);
        note.setNoteInfo(info);
        note.setNoteDateTime(Timestamp.from(createdAt));
        entityManager.persist(note);
        return note;
    }

    static void seedEvent(
            EntityManager entityManager, String eventId, String statusId, String partyId, Instant entryDate) {
        CommunicationEvent event = new CommunicationEvent();
        event.setCommunicationEventId(eventId);
        event.setStatusId(statusId);
        event.setEntryDate(Timestamp.from(entryDate));
        entityManager.persist(event);
        CommunicationEventRole role = new CommunicationEventRole();
        role.setCommunicationEventId(eventId);
        role.setPartyId(partyId);
        role.setRoleTypeId("TEST_ROLE");
        role.setStatusId("CRS_ACCEPTED");
        entityManager.persist(role);
    }

    static void seedTaskAssignment(
            EntityManager entityManager,
            String workEffortId,
            String type,
            String status,
            String partyId,
            Instant fromDate,
            Instant thruDate) {
        WorkEffort effort = new WorkEffort();
        effort.setWorkEffortId(workEffortId);
        effort.setWorkEffortTypeId(type);
        entityManager.persist(effort);
        WorkEffortPartyAssignment assignment = new WorkEffortPartyAssignment();
        assignment.setWorkEffortId(workEffortId);
        assignment.setPartyId(partyId);
        assignment.setRoleTypeId("TEST_ROLE");
        assignment.setFromDate(fromDate == null ? null : Timestamp.from(fromDate));
        assignment.setThruDate(thruDate == null ? null : Timestamp.from(thruDate));
        assignment.setStatusId(status);
        entityManager.persist(assignment);
    }

    static void seedNoteAssociation(
            EntityManager entityManager, EntityRegistry entities, String entityName, String noteId) {
        Class<?> type = entities.entityClass(entityName);
        Object association = entities.newEntity(type);
        List<String> keys = entities.primaryKeys(type);
        for (String key : keys) {
            Object value = switch (key) {
            case "noteId" -> noteId;
            case "partyId" -> "admin";
            case "workEffortId" -> "TEST-WORK-EFFORT";
            case "fromDate" -> Timestamp.from(Instant.now());
            case "roleTypeId" -> "TEST_ROLE";
            default -> "TEST-" + entityName + "-" + key;
            };
            entities.set(association, key, value);
        }
        entityManager.persist(association);
    }
}
