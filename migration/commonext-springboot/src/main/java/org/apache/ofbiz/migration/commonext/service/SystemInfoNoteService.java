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
package org.apache.ofbiz.migration.commonext.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.SequenceService;
import org.apache.ofbiz.migration.commonext.port.CurrentPartyPort;
import org.apache.ofbiz.migration.commonext.port.NoteAssociationPort;
import org.apache.ofbiz.migration.commonext.port.SystemInfoStatusPort;
import org.apache.ofbiz.migration.datamodel.entity.NoteData;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;

@Service
@Transactional
public class SystemInfoNoteService implements SystemInfoNoteOperations {
    private static final String SYSTEM_NOTE = "SYSTEMNOTE";
    private static final String PUBLIC_PERMISSION = "COMMONEXT_PUBLMSG";
    private static final String ADMIN_PERMISSION = "SETUP_ADMIN";
    private static final Pattern UNSAFE_TEXT = Pattern.compile("(?i)<[a-z!/]|javascript:|on[a-z]+\\s*=");
    private static final Set<String> STAMP_FIELDS = Set.of(
            "createdStamp", "createdTxStamp", "lastUpdatedStamp", "lastUpdatedTxStamp");

    private final EntityManager entityManager;
    private final EntityRegistry entityRegistry;
    private final SequenceService sequenceService;
    private final CurrentPartyPort currentPartyPort;
    private final NoteAssociationPort noteAssociationPort;
    private final SystemInfoStatusPort statusPort;

    public SystemInfoNoteService(
            EntityManager entityManager,
            EntityRegistry entityRegistry,
            SequenceService sequenceService,
            CurrentPartyPort currentPartyPort,
            NoteAssociationPort noteAssociationPort,
            SystemInfoStatusPort statusPort) {
        this.entityManager = entityManager;
        this.entityRegistry = entityRegistry;
        this.sequenceService = sequenceService;
        this.currentPartyPort = currentPartyPort;
        this.noteAssociationPort = noteAssociationPort;
        this.statusPort = statusPort;
    }

    @Override
    public Map<String, Object> createSystemInfoNote(Map<String, Object> input) {
        String currentPartyId = requireCurrentParty();
        Object requestedParty = input.get("noteParty");
        String noteParty = requestedParty == null || requestedParty.toString().isBlank()
                ? currentPartyId
                : requestedParty.toString();
        if (!noteParty.equals(currentPartyId) && !hasPublicPermission()) {
            throw new MigrationException(HttpStatus.FORBIDDEN, "Cannot create notes for another party");
        }

        NoteData note = new NoteData();
        Set<String> writableFields = entityManager.getMetamodel().entity(NoteData.class).getSingularAttributes().stream()
                .filter(attribute -> !attribute.isId())
                .map(attribute -> attribute.getName())
                .filter(name -> !STAMP_FIELDS.contains(name))
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();
            if (!writableFields.contains(field) || field.equals("noteParty") || value == null) {
                continue;
            }
            if (value instanceof String text && UNSAFE_TEXT.matcher(text).find()) {
                throw new MigrationException(HttpStatus.BAD_REQUEST, "Unsafe text in " + field);
            }
            if (field.equals("moreInfoUrl") && !isSafeUrl(value.toString())) {
                throw new MigrationException(HttpStatus.BAD_REQUEST, "Unsafe moreInfoUrl");
            }
            entityRegistry.set(note, field, value);
        }
        entityRegistry.set(note, "noteParty", noteParty);
        entityRegistry.set(note, "noteName", SYSTEM_NOTE);
        entityRegistry.set(note, "noteDateTime", Timestamp.from(Instant.now()));
        String noteId = sequenceService.nextId("NoteData");
        entityRegistry.set(note, "noteId", noteId);
        entityManager.persist(note);
        return Map.of("noteId", noteId);
    }

    @Override
    public Map<String, Object> deleteSystemInfoNote(Map<String, Object> input) {
        Object suppliedId = input.get("noteId");
        if (suppliedId == null || suppliedId.toString().isBlank()) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "noteId is required");
        }
        String noteId = suppliedId.toString();
        NoteData note = entityManager.find(NoteData.class, noteId);
        if (note == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "System note not found");
        }
        if (!SYSTEM_NOTE.equals(note.getNoteName())) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "System note not found");
        }
        String currentPartyId = currentPartyPort.currentPartyId().orElse(null);
        boolean ownSystemNote = note.getNoteParty() != null
                && note.getNoteParty().equals(currentPartyId);
        if (!ownSystemNote && !hasAdminPermission()) {
            throw new MigrationException(HttpStatus.FORBIDDEN, "Cannot delete this system note");
        }
        noteAssociationPort.removeNoteAssociations(noteId);
        entityManager.remove(note);
        return Map.of();
    }

    @Override
    public Map<String, Object> deleteAllSystemNotes(Map<String, Object> input) {
        String partyId = requireCurrentParty();
        int deletedCount = entityManager.createQuery(
                        "delete from NoteData note where note.noteParty = :partyId and note.noteName = :noteName")
                .setParameter("partyId", partyId)
                .setParameter("noteName", SYSTEM_NOTE)
                .executeUpdate();
        return Map.of("deletedCount", deletedCount);
    }

    @Transactional(readOnly = true)
    @Override
    public Map<String, Object> getSystemInfoNotes(Map<String, Object> input) {
        String partyId = requireCurrentParty();
        List<NoteData> notes = findSystemNotes(partyId);
        if (notes.isEmpty()) {
            return Map.of();
        }
        return Map.of("systemInfoNotes", notes.stream().map(entityRegistry::toMap).toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Map<String, Object> getLastSystemInfoNote(Map<String, Object> input) {
        String partyId = currentPartyPort.currentPartyId().orElse("_NA_");
        List<NoteData> notes = findSystemNotes(partyId);
        Map<String, Object> result = new LinkedHashMap<>();
        if (!notes.isEmpty()) {
            result.put("lastSystemInfoNote1", entityRegistry.toMap(notes.get(0)));
            if (notes.size() > 2) {
                result.put("lastSystemInfoNote2", entityRegistry.toMap(notes.get(1)));
            }
            if (notes.size() > 3) {
                result.put("lastSystemInfoNote3", entityRegistry.toMap(notes.get(2)));
            }
        }
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public Map<String, Object> getSystemInfoStatus(Map<String, Object> input) {
        String partyId = requireCurrentParty();
        Timestamp now = Timestamp.from(Instant.now());
        List<Map<String, Object>> status = new ArrayList<>();
        List<org.apache.ofbiz.migration.datamodel.entity.CommunicationEvent> events =
                statusPort.openCommunicationEvents(partyId);
        if (!events.isEmpty()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("noteInfo", "Open communication events: " + events.size());
            item.put("noteDateTime", events.get(0).getEntryDate());
            status.add(item);
        }
        List<org.apache.ofbiz.migration.datamodel.entity.WorkEffortPartyAssignment> assignments =
                statusPort.openTaskAssignments(partyId, now);
        if (!assignments.isEmpty()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("noteInfo", "Assigned and not completed tasks: " + assignments.size());
            item.put("noteDateTime", assignments.get(0).getFromDate());
            status.add(item);
        }
        return Map.of("systemInfoStatus", status);
    }

    private List<NoteData> findSystemNotes(String partyId) {
        return entityManager.createQuery(
                        "select note from NoteData note where note.noteParty = :partyId "
                                + "and note.noteName = :noteName order by note.noteDateTime desc",
                        NoteData.class)
                .setParameter("partyId", partyId)
                .setParameter("noteName", SYSTEM_NOTE)
                .getResultList();
    }

    private String requireCurrentParty() {
        return currentPartyPort.currentPartyId()
                .orElseThrow(() -> new MigrationException(
                        HttpStatus.FORBIDDEN, "No party is associated with the user"));
    }

    private boolean hasPublicPermission() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        Set<String> authorities = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        return authorities.contains(PUBLIC_PERMISSION) || authorities.contains(ADMIN_PERMISSION);
    }

    private boolean hasAdminPermission() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && AuthorityUtils.authorityListToSet(authentication.getAuthorities()).contains(ADMIN_PERMISSION);
    }

    private static boolean isSafeUrl(String value) {
        return (value.startsWith("/") && !value.startsWith("//"))
                || value.matches("(?i)^https?://[^\\s]+$");
    }
}
