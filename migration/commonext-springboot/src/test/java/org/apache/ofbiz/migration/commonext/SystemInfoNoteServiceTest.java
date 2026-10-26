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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.commonext.service.SystemInfoNoteService;
import org.apache.ofbiz.migration.datamodel.entity.NoteData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SystemInfoNoteServiceTest {
    @Autowired
    private SystemInfoNoteService service;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private EntityRegistry entityRegistry;

    @BeforeEach
    void setUpUsers() {
        CommonextTestSupport.seedUserLogins(entityManager);
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"SETUP_ADMIN"})
    void ac01createFillsSystemNoteDefaultsAndIgnoresUnknownAndPrimaryKeyFields() {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("noteId", "CLIENT-ID");
        input.put("noteName", "CLIENT-NAME");
        input.put("noteParty", "");
        input.put("noteInfo", "Saved from test");
        input.put("moreInfoUrl", "/setup");
        input.put("createdStamp", "2000-01-01 00:00:00");
        input.put("unknownField", "ignored");

        Map<String, Object> result = service.createSystemInfoNote(input);
        NoteData saved = entityManager.find(NoteData.class, result.get("noteId"));

        assertNotNull(saved);
        assertFalse("CLIENT-ID".equals(saved.getNoteId()));
        assertEquals("SYSTEMNOTE", saved.getNoteName());
        assertEquals("admin", saved.getNoteParty());
        assertEquals("Saved from test", saved.getNoteInfo());
        assertNotNull(saved.getNoteDateTime());
        assertEquals("/setup", saved.getMoreInfoUrl());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"SETUP_ADMIN"})
    void ac02createAcceptsExplicitSamePartyAndSafeHttpUrls() {
        Map<String, Object> input = Map.of(
                "noteParty", "admin",
                "noteInfo", "Link",
                "moreInfoUrl", "https://example.invalid/info");
        Map<String, Object> result = service.createSystemInfoNote(input);

        assertEquals("admin", entityManager.find(NoteData.class, result.get("noteId")).getNoteParty());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"SETUP_ADMIN"})
    void ac03createRejectsHtmlAndScriptText() {
        for (String value : List.of("<script>alert(1)</script>", "onload=alert(1)")) {
            MigrationException exception = assertThrows(
                    MigrationException.class, () -> service.createSystemInfoNote(
                            Map.of("noteInfo", value)));
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        }
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"SETUP_ADMIN"})
    void ac04createRejectsJavascriptAndProtocolRelativeUrls() {
        for (String url : List.of("javascript:alert(1)", "//example.invalid/x", "data:text/html,x")) {
            MigrationException exception = assertThrows(
                    MigrationException.class, () -> service.createSystemInfoNote(
                            Map.of("moreInfoUrl", url)));
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        }
    }

    @Test
    @WithMockUser(username = "editor", authorities = {"SETUP_VIEW", "SETUP_UPDATE"})
    void ac05createForAnotherPartyRequiresPublicPermission() {
        MigrationException exception = assertThrows(
                MigrationException.class, () -> service.createSystemInfoNote(
                        Map.of("noteParty", "admin", "noteInfo", "Denied")));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    @WithMockUser(username = "editor", authorities = {"SETUP_VIEW", "SETUP_UPDATE", "COMMONEXT_PUBLMSG"})
    void ac06publicPermissionAllowsCreatingForAnotherParty() {
        Map<String, Object> result = service.createSystemInfoNote(
                Map.of("noteParty", "DemoReadonly", "noteInfo", "Public message"));

        assertEquals("DemoReadonly", entityManager.find(NoteData.class, result.get("noteId")).getNoteParty());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"SETUP_ADMIN"})
    void ac07deleteRemovesTheNoteAndLinkedPartyAndWorkEffortRows() {
        CommonextTestSupport.seedNote(
                entityManager, "DELETE-1", "admin", "SYSTEMNOTE", "remove", Instant.now());
        CommonextTestSupport.seedNoteAssociation(entityManager, entityRegistry, "PartyNote", "DELETE-1");
        CommonextTestSupport.seedNoteAssociation(entityManager, entityRegistry, "WorkEffortNote", "DELETE-1");
        entityManager.flush();

        service.deleteSystemInfoNote(Map.of("noteId", "DELETE-1"));
        entityManager.flush();

        assertEquals(0L, entityManager.createQuery(
                        "select count(row) from NoteData row where row.noteId = :noteId", Long.class)
                .setParameter("noteId", "DELETE-1")
                .getSingleResult());
        assertEquals(0L, entityManager.createQuery(
                        "select count(row) from PartyNote row where row.noteId = :noteId", Long.class)
                .setParameter("noteId", "DELETE-1")
                .getSingleResult());
        assertEquals(0L, entityManager.createQuery(
                        "select count(row) from WorkEffortNote row where row.noteId = :noteId", Long.class)
                .setParameter("noteId", "DELETE-1")
                .getSingleResult());
    }

    @Test
    @WithMockUser(username = "editor", authorities = {"SETUP_VIEW", "SETUP_UPDATE"})
    void ac08deleteOfAnotherPartysNoteIsForbidden() {
        CommonextTestSupport.seedNote(
                entityManager, "DELETE-2", "admin", "SYSTEMNOTE", "other", Instant.now());

        MigrationException exception = assertThrows(
                MigrationException.class, () -> service.deleteSystemInfoNote(
                        Map.of("noteId", "DELETE-2")));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"SETUP_ADMIN"})
    void ac09adminCanDeleteAnotherPartysNote() {
        CommonextTestSupport.seedNote(
                entityManager, "DELETE-3", "DemoEditor", "SYSTEMNOTE", "other", Instant.now());

        service.deleteSystemInfoNote(Map.of("noteId", "DELETE-3"));

        assertNull(entityManager.find(NoteData.class, "DELETE-3"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"SETUP_ADMIN"})
    void ac28adminCannotDeleteNonSystemNoteAndRowRemains() {
        CommonextTestSupport.seedNote(
                entityManager, "DELETE-NON-SYSTEM", "admin", "OTHER", "keep", Instant.now());

        MigrationException exception = assertThrows(
                MigrationException.class, () -> service.deleteSystemInfoNote(
                        Map.of("noteId", "DELETE-NON-SYSTEM")));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertNotNull(entityManager.find(NoteData.class, "DELETE-NON-SYSTEM"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"SETUP_ADMIN"})
    void ac10deleteReturnsNotFoundAndRequiresNoteId() {
        MigrationException missingId = assertThrows(
                MigrationException.class, () -> service.deleteSystemInfoNote(Map.of()));
        MigrationException missingRow = assertThrows(
                MigrationException.class, () -> service.deleteSystemInfoNote(Map.of("noteId", "UNKNOWN")));

        assertEquals(HttpStatus.BAD_REQUEST, missingId.getStatus());
        assertEquals(HttpStatus.NOT_FOUND, missingRow.getStatus());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"SETUP_ADMIN"})
    void ac11deleteAllRemovesOnlyCurrentPartySystemNotes() {
        CommonextTestSupport.seedNote(entityManager, "ALL-1", "admin", "SYSTEMNOTE", "one", Instant.now());
        CommonextTestSupport.seedNote(entityManager, "ALL-2", "admin", "OTHER", "keep", Instant.now());
        CommonextTestSupport.seedNote(entityManager, "ALL-3", "DemoEditor", "SYSTEMNOTE", "keep", Instant.now());

        Map<String, Object> result = service.deleteAllSystemNotes(Map.of());

        assertEquals(1, result.get("deletedCount"));
        assertNotNull(entityManager.find(NoteData.class, "ALL-2"));
        assertNotNull(entityManager.find(NoteData.class, "ALL-3"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"SETUP_ADMIN"})
    void ac12getNotesOrdersNewestFirstAndReturnsEmptyMapWhenNoRows() {
        CommonextTestSupport.seedNote(
                entityManager, "GET-OLD", "admin", "SYSTEMNOTE", "old", Instant.now().minus(1, ChronoUnit.DAYS));
        CommonextTestSupport.seedNote(
                entityManager, "GET-NEW", "admin", "SYSTEMNOTE", "new", Instant.now());
        CommonextTestSupport.seedNote(
                entityManager, "GET-OTHER", "DemoEditor", "SYSTEMNOTE", "other", Instant.now());

        Map<String, Object> result = service.getSystemInfoNotes(Map.of());
        List<?> notes = (List<?>) result.get("systemInfoNotes");

        assertEquals(2, notes.size());
        assertEquals("new", ((Map<?, ?>) notes.get(0)).get("noteInfo"));
        assertEquals("old", ((Map<?, ?>) notes.get(1)).get("noteInfo"));
        service.deleteAllSystemNotes(Map.of());
        assertEquals(Map.of(), service.getSystemInfoNotes(Map.of()));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"SETUP_ADMIN"})
    void ac13legacyGetLastOffByOneForOneAndTwoNotes() {
        CommonextTestSupport.seedNote(
                entityManager, "LAST-1", "admin", "SYSTEMNOTE", "one", Instant.now());
        assertTrue(service.getLastSystemInfoNote(Map.of()).containsKey("lastSystemInfoNote1"));
        assertFalse(service.getLastSystemInfoNote(Map.of()).containsKey("lastSystemInfoNote2"));

        CommonextTestSupport.seedNote(
                entityManager, "LAST-2", "admin", "SYSTEMNOTE", "two", Instant.now().plus(1, ChronoUnit.SECONDS));
        Map<String, Object> two = service.getLastSystemInfoNote(Map.of());
        assertTrue(two.containsKey("lastSystemInfoNote1"));
        assertFalse(two.containsKey("lastSystemInfoNote2"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"SETUP_ADMIN"})
    void ac14legacyGetLastOffByOneForThreeAndFourNotes() {
        for (int index = 1; index <= 3; index++) {
            CommonextTestSupport.seedNote(
                    entityManager, "LAST-" + index, "admin", "SYSTEMNOTE", "item-" + index,
                    Instant.now().plus(index, ChronoUnit.SECONDS));
        }
        Map<String, Object> three = service.getLastSystemInfoNote(Map.of());
        assertTrue(three.containsKey("lastSystemInfoNote2"));
        assertFalse(three.containsKey("lastSystemInfoNote3"));

        CommonextTestSupport.seedNote(
                entityManager, "LAST-4", "admin", "SYSTEMNOTE", "item-4",
                Instant.now().plus(4, ChronoUnit.SECONDS));
        assertTrue(service.getLastSystemInfoNote(Map.of()).containsKey("lastSystemInfoNote3"));
    }

    @Test
    @WithMockUser(username = "orphan", authorities = {"SETUP_ADMIN"})
    void ac15missingPartyIsForbiddenExceptLastNoteUsesNaSentinel() {
        CommonextTestSupport.seedNote(
                entityManager, "LAST-NA", "_NA_", "SYSTEMNOTE", "sentinel", Instant.now());

        assertEquals(HttpStatus.FORBIDDEN, assertThrows(
                MigrationException.class, () -> service.createSystemInfoNote(Map.of())).getStatus());
        assertEquals(HttpStatus.FORBIDDEN, assertThrows(
                MigrationException.class, () -> service.deleteAllSystemNotes(Map.of())).getStatus());
        assertEquals(HttpStatus.FORBIDDEN, assertThrows(
                MigrationException.class, () -> service.getSystemInfoNotes(Map.of())).getStatus());
        assertEquals(HttpStatus.FORBIDDEN, assertThrows(
                MigrationException.class, () -> service.getSystemInfoStatus(Map.of())).getStatus());
        assertEquals("sentinel", ((Map<?, ?>) service.getLastSystemInfoNote(Map.of())
                .get("lastSystemInfoNote1")).get("noteInfo"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"SETUP_ADMIN"})
    void ac16statusCountsOpenCommunicationEventsAndExcludesCompletedNullAndOtherPartyRows() {
        Instant now = Instant.now();
        CommonextTestSupport.seedEvent(entityManager, "EVENT-OPEN-1", "COM_ENTERED", "admin", now);
        CommonextTestSupport.seedEvent(
                entityManager, "EVENT-OPEN-2", "COM_IN_PROGRESS", "admin", now.minus(1, ChronoUnit.MINUTES));
        CommonextTestSupport.seedEvent(
                entityManager, "EVENT-DONE", "COM_ROLE_COMPLETED", "admin", now.plus(1, ChronoUnit.MINUTES));
        CommonextTestSupport.seedEvent(
                entityManager, "EVENT-OTHER", "COM_ENTERED", "DemoEditor", now.plus(2, ChronoUnit.MINUTES));

        List<?> status = (List<?>) service.getSystemInfoStatus(Map.of()).get("systemInfoStatus");

        assertEquals(1, status.size());
        assertEquals("Open communication events: 2", ((Map<?, ?>) status.get(0)).get("noteInfo"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"SETUP_ADMIN"})
    void ac17statusCountsOnlyCurrentActiveAssignedTasksOfTaskType() {
        Instant now = Instant.now();
        CommonextTestSupport.seedTaskAssignment(
                entityManager, "TASK-1", "TASK", "PAS_ASSIGNED", "admin", now.minus(2, ChronoUnit.HOURS), null);
        CommonextTestSupport.seedTaskAssignment(
                entityManager, "TASK-2", "TASK", "PAS_ASSIGNED", "admin", now.minus(1, ChronoUnit.HOURS), null);
        CommonextTestSupport.seedTaskAssignment(
                entityManager, "TASK-OLD", "TASK", "PAS_ASSIGNED", "admin",
                now.minus(2, ChronoUnit.DAYS), now.minus(1, ChronoUnit.DAYS));
        CommonextTestSupport.seedTaskAssignment(
                entityManager, "TASK-NOT-TASK", "PROJECT", "PAS_ASSIGNED", "admin", now, null);
        CommonextTestSupport.seedTaskAssignment(
                entityManager, "TASK-OTHER", "TASK", "PAS_ASSIGNED", "DemoEditor", now, null);

        List<?> status = (List<?>) service.getSystemInfoStatus(Map.of()).get("systemInfoStatus");

        assertEquals(1, status.size());
        assertEquals("Assigned and not completed tasks: 2", ((Map<?, ?>) status.get(0)).get("noteInfo"));
    }
}
