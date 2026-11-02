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
package org.apache.ofbiz.migration.party;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@WithMockUser(authorities = {"OFBTOOLS_VIEW", "PARTYMGR_ADMIN"})
class PartyNameHistoryEcaTest {
    @Autowired private MigrationServiceDispatcher dispatcher;
    @Autowired private EntityRegistry entities;

    @Test
    void personAndGroupCreationRecordAuthenticatedActorAndInitialStatus() {
        assertCreationMetadata(call("createPerson", Map.of("firstName", "Audit", "lastName", "Person")));
        assertCreationMetadata(call("createPartyGroup", Map.of("groupName", "Audit Group")));
    }

    @Test
    void emptyPersonCreationStillRecordsInitialEnabledStatus() {
        assertCreationMetadata(call("createPerson", Map.of()));
    }

    @Test
    void duplicatePersonDoesNotAddASecondInitialStatus() {
        Object id = call("createPerson", Map.of("firstName", "Existing", "lastName", "Person")).get("partyId");
        assertThrows(MigrationException.class, () -> call("createPerson", Map.of("partyId", id)));
        assertEquals(1, entities.findByEquality(entities.entityClass("PartyStatus"), Map.of("partyId", id)).size());
    }

    private void assertCreationMetadata(Map<String, Object> result) {
        Object id = result.get("partyId");
        Object party = entities.findByPrimaryKey(entities.entityClass("Party"), Map.of("partyId", id));
        assertNotNull(entities.get(party, "createdDate"));
        assertEquals(entities.get(party, "createdDate"), entities.get(party, "lastModifiedDate"));
        assertEquals("user", entities.get(party, "createdByUserLogin"));
        assertEquals("user", entities.get(party, "lastModifiedByUserLogin"));
        var statuses = entities.findByEquality(entities.entityClass("PartyStatus"), Map.of("partyId", id));
        assertEquals(1, statuses.size());
        assertEquals("PARTY_ENABLED", entities.get(statuses.get(0), "statusId"));
        assertEquals("user", entities.get(statuses.get(0), "changeByUserLoginId"));
        assertEquals(entities.get(party, "createdDate"), entities.get(statuses.get(0), "statusDate"));
    }

    @Test
    void personInvokeRuleCapturesPreviousNameAndLeavesUnchangedNamesAlone() {
        Object id = call("createPerson", Map.of("firstName", "Before", "lastName", "Person")).get("partyId");
        call("updatePerson", Map.of("partyId", id, "firstName", "Before", "lastName", "Person"));
        assertEquals(0, entities.findByEquality(entities.entityClass("PartyNameHistory"), Map.of("partyId", id)).size());
        call("updatePerson", Map.of("partyId", id, "firstName", "After", "lastName", "Person"));
        var history = entities.findByEquality(entities.entityClass("PartyNameHistory"), Map.of("partyId", id));
        assertEquals(1, history.size());
        assertEquals("Before", entities.get(history.get(0), "firstName"));
        assertEquals("Person", entities.get(history.get(0), "lastName"));
    }

    @Test
    void groupInvokeRuleCapturesPreviousName() {
        Object id = call("createPartyGroup", Map.of("groupName", "Before Group")).get("partyId");
        call("updatePartyGroup", Map.of("partyId", id, "groupName", "After Group"));
        var history = entities.findByEquality(entities.entityClass("PartyNameHistory"), Map.of("partyId", id));
        assertEquals(1, history.size());
        assertEquals("Before Group", entities.get(history.get(0), "groupName"));
    }

    @Test
    void emptyNameInputDoesNotCreateHistory() {
        assertEquals(Map.of("responseMessage", "success"), call("savePartyNameChange", Map.of()));
    }

    @Test
    void unknownPartyCannotCreateNameHistory() {
        assertThrows(MigrationException.class,
                () -> call("savePartyNameChange", Map.of("partyId", "UNKNOWN", "firstName", "Changed")));
    }

    private Map<String, Object> call(String service, Map<String, ?> input) {
        return dispatcher.execute("party", service, input);
    }
}
