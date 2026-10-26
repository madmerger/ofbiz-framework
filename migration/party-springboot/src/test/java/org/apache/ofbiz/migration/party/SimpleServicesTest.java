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
package org.apache.ofbiz.migration.party;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = {PartyMigrationApplication.class, TestUsersConfiguration.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SimpleServicesTest {
    private static final List<String> MIGRATED_SERVICES = List.of(
            "createPersonAndUserLogin",
            "createPartyContactMechs",
            "createPersonRoleAndContactMechs",
            "ensurePartyRole",
            "ensureNaPartyRole",
            "ensurePartyRoleFrom",
            "ensurePartyRoleTo",
            "createPartyContactMech",
            "updatePartyContactMech",
            "deletePartyContactMech",
            "findPartyFromEmailAddress",
            "findPartyFromTelephone",
            "findPartyFromTelephoneComplete",
            "createPartyPostalAddress",
            "updatePartyPostalAddress",
            "createPartyTelecomNumber",
            "updatePartyTelecomNumber",
            "createPartyEmailAddress",
            "updatePartyEmailAddress",
            "createCommEventWorkEffort",
            "createPostalAddressAndPurposes",
            "updatePostalAddressAndPurposes",
            "updateContactMechAndPurposes",
            "createUpdatePartyTelecomNumber",
            "createUpdatePartyEmailAddress",
            "createUpdatePartyPostalAddress");

    @Autowired
    private ServiceHandlerRegistry registry;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private EntityManager entityManager;

    @Test
    void ac20_everySimpleServiceHasARegisteredHandler() {
        assertEquals(26, MIGRATED_SERVICES.size());
        MIGRATED_SERVICES.forEach(name -> assertNotNull(registry.handler("party", name).orElse(null), name));
    }

    @Test
    @Transactional
    void ac22_partyContactOperationsRequirePartyIdAndStayScoped() throws Exception {
        call("createPerson", Map.of("partyId", "AC22_OWNER", "firstName", "Owner", "lastName", "Party"));
        call("createPerson", Map.of("partyId", "AC22_OTHER", "firstName", "Other", "lastName", "Party"));
        Map<String, Object> ownerLink = call("createPartyContactMech", Map.of(
                "partyId", "AC22_OWNER",
                "contactMechTypeId", "EMAIL_ADDRESS",
                "infoString", "shared@example.test"));
        String contactMechId = String.valueOf(ownerLink.get("contactMechId"));
        call("createPartyContactMech", Map.of(
                "partyId", "AC22_OTHER",
                "contactMechId", contactMechId,
                "contactMechTypeId", "EMAIL_ADDRESS"));

        mockMvc.perform(post("/api/party/services/updatePartyContactMech")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(Map.of(
                                "contactMechId", contactMechId, "comments", "must not update"))))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/party/services/deletePartyContactMech")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(Map.of("contactMechId", contactMechId))))
                .andExpect(status().isBadRequest());

        for (String partyId : List.of("AC22_OWNER", "AC22_OTHER")) {
            List<?> links = entities.findByEquality(entities.entityClass("PartyContactMech"),
                    Map.of("partyId", partyId, "contactMechId", contactMechId));
            assertEquals(1, links.size());
            assertNull(entities.get(links.get(0), "thruDate"));
        }
    }

    @Test
    @Transactional
    void ac21_everySimpleServiceExecutesASuccessfulCase() throws Exception {
        call("createPersonAndUserLogin", Map.of(
                "partyId", "AC21_LOGIN_PARTY",
                "firstName", "Simple",
                "lastName", "Login",
                "userLoginId", "ac21-login",
                "currentPassword", "simple-password",
                "currentPasswordVerify", "simple-password"));

        call("createPerson", Map.of("partyId", "AC21_CONTACT_PARTY", "firstName", "Contact", "lastName", "Owner"));
        Map<String, Object> contexts = new LinkedHashMap<>();
        contexts.put("partyId", "AC21_CONTACT_PARTY");
        contexts.put("postalAddressContext", Map.of(
                "address1", "1 Simple Way", "city", "Testville", "postalCode", "10001"));
        contexts.put("telecomNumberContext", Map.of("contactNumber", "5550100", "areaCode", "202"));
        contexts.put("emailAddressContext", Map.of("emailAddress", "context@example.test"));
        call("createPartyContactMechs", contexts);

        call("createPersonRoleAndContactMechs", Map.of(
                "partyId", "AC21_ROLE_PARTY",
                "firstName", "Role",
                "lastName", "Owner",
                "roleTypeId", "CONTACT",
                "emailAddress", "role@example.test"));
        call("ensurePartyRole", Map.of("partyId", "AC21_ROLE_PARTY", "roleTypeId", "CUSTOMER"));
        call("ensureNaPartyRole", Map.of("partyId", "AC21_ROLE_PARTY"));
        call("ensurePartyRoleFrom", Map.of("partyIdFrom", "AC21_ROLE_PARTY", "roleTypeIdFrom", "SUPPLIER"));
        call("ensurePartyRoleTo", Map.of("partyIdTo", "AC21_ROLE_PARTY", "roleTypeIdTo", "EMPLOYEE"));

        Map<String, Object> contactLink = call("createPartyContactMech", Map.of(
                "partyId", "AC21_CONTACT_PARTY",
                "contactMechTypeId", "EMAIL_ADDRESS",
                "infoString", "direct@example.test"));
        String directContactId = String.valueOf(contactLink.get("contactMechId"));
        Map<String, Object> updatedLink = call("updatePartyContactMech", Map.of(
                "partyId", "AC21_CONTACT_PARTY",
                "contactMechId", directContactId,
                "contactMechTypeId", "EMAIL_ADDRESS",
                "infoString", "updated@example.test",
                "comments", "updated link",
                "extension", "test extension",
                "allowSolicitation", "N"));
        String updatedContactId = String.valueOf(updatedLink.get("contactMechId"));
        Object newContactLink = entities.findByEquality(entities.entityClass("PartyContactMech"),
                Map.of("partyId", "AC21_CONTACT_PARTY", "contactMechId", updatedContactId)).get(0);
        assertEquals("updated link", entities.get(newContactLink, "comments"));
        assertEquals("test extension", entities.get(newContactLink, "extension"));
        assertEquals("N", entities.get(newContactLink, "allowSolicitation"));
        call("updatePartyContactMech", Map.of(
                "partyId", "AC21_CONTACT_PARTY",
                "contactMechId", updatedContactId,
                "comments", "updated in place"));
        assertEquals("updated in place", entities.get(newContactLink, "comments"));
        call("deletePartyContactMech", Map.of(
                "partyId", "AC21_CONTACT_PARTY", "contactMechId", updatedContactId));

        call("createPerson", Map.of("partyId", "AC21_EXPIRY_PARTY", "firstName", "Expiry", "lastName", "Check"));
        Map<String, Object> expiryLink = call("createPartyEmailAddress", Map.of(
                "partyId", "AC21_EXPIRY_PARTY", "emailAddress", "expiry@example.test"));
        Timestamp pastDate = Timestamp.from(Instant.parse("2000-01-01T00:00:00Z"));
        call("updatePartyContactMech", Map.of(
                "partyId", "AC21_EXPIRY_PARTY",
                "contactMechId", expiryLink.get("contactMechId"),
                "thruDate", pastDate.toString()));
        Object expiredLink = entities.findByEquality(entities.entityClass("PartyContactMech"),
                Map.of("partyId", "AC21_EXPIRY_PARTY", "contactMechId", expiryLink.get("contactMechId"))).get(0);
        assertEquals(pastDate, entities.get(expiredLink, "thruDate"));
        mockMvc.perform(post("/api/party/services/getPartyEmail")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(Map.of("partyId", "AC21_EXPIRY_PARTY"))))
                .andExpect(status().isNotFound());

        call("createPartyEmailAddress", Map.of(
                "partyId", "AC21_CONTACT_PARTY", "emailAddress", "lookup@example.test"));
        call("findPartyFromEmailAddress", Map.of("address", "lookup@example.test"));
        call("createPartyTelecomNumber", Map.of(
                "partyId", "AC21_CONTACT_PARTY",
                "contactNumber", "5550101",
                "areaCode", "202",
                "countryCode", "1"));
        call("findPartyFromTelephone", Map.of("telno", "5550101"));
        call("findPartyFromTelephoneComplete", Map.of("telno", "+12025550101"));

        Map<String, Object> postalAddress = call("createPartyPostalAddress", Map.of(
                "partyId", "AC21_CONTACT_PARTY",
                "address1", "2 Simple Way",
                "city", "Testville",
                "postalCode", "10002"));
        String postalContactId = String.valueOf(postalAddress.get("contactMechId"));
        call("updatePartyPostalAddress", Map.of(
                "partyId", "AC21_CONTACT_PARTY",
                "contactMechId", postalContactId,
                "address1", "3 Simple Way"));

        Map<String, Object> telephone = call("createPartyTelecomNumber", Map.of(
                "partyId", "AC21_CONTACT_PARTY", "contactNumber", "5550102"));
        String telephoneId = String.valueOf(telephone.get("contactMechId"));
        call("updatePartyTelecomNumber", Map.of(
                "partyId", "AC21_CONTACT_PARTY",
                "contactMechId", telephoneId,
                "contactNumber", "5550103"));

        Map<String, Object> email = call("createPartyEmailAddress", Map.of(
                "partyId", "AC21_CONTACT_PARTY", "emailAddress", "update@example.test"));
        String emailId = String.valueOf(email.get("contactMechId"));
        call("updatePartyEmailAddress", Map.of(
                "partyId", "AC21_CONTACT_PARTY",
                "contactMechId", emailId,
                "emailAddress", "updated-email@example.test"));

        createCommunicationEvent("AC21_COMM_EVENT");
        call("createCommEventWorkEffort", Map.of(
                "communicationEventId", "AC21_COMM_EVENT",
                "currentStatusId", "WORK_EFFORT_CREATED",
                "workEffortName", "Simple service test",
                "workEffortTypeId", "EVENT"));

        Map<String, Object> purposeAddress = call("createPostalAddressAndPurposes", Map.of(
                "partyId", "AC21_CONTACT_PARTY",
                "address1", "4 Simple Way",
                "city", "Testville",
                "postalCode", "10004",
                "setShippingPurpose", "Y"));
        Map<String, Object> updatedPurposeAddress = call("updatePostalAddressAndPurposes", Map.of(
                "partyId", "AC21_CONTACT_PARTY",
                "contactMechId", purposeAddress.get("contactMechId"),
                "address1", "5 Simple Way",
                "setBillingPurpose", "Y"));
        String updatedPurposeAddressId = String.valueOf(updatedPurposeAddress.get("contactMechId"));
        assertEquals(0, entities.findByEquality(entities.entityClass("PartyContactMechPurpose"),
                Map.of("partyId", "AC21_CONTACT_PARTY",
                        "contactMechId", purposeAddress.get("contactMechId"),
                        "contactMechPurposeTypeId", "SHIPPING_LOCATION"))
                .stream().filter(purpose -> entities.get(purpose, "thruDate") == null).count());
        assertEquals(1, entities.findByEquality(entities.entityClass("PartyContactMechPurpose"),
                Map.of("partyId", "AC21_CONTACT_PARTY",
                        "contactMechId", updatedPurposeAddressId,
                        "contactMechPurposeTypeId", "SHIPPING_LOCATION"))
                .stream().filter(purpose -> entities.get(purpose, "thruDate") == null).count());

        Map<String, Object> phoneForPurposes = call("createPartyTelecomNumber", Map.of(
                "partyId", "AC21_CONTACT_PARTY", "contactNumber", "5550104"));
        Map<String, Object> addressForCombinedUpdate = call("createPartyPostalAddress", Map.of(
                "partyId", "AC21_CONTACT_PARTY",
                "address1", "6 Simple Way",
                "city", "Testville",
                "postalCode", "10006"));
        Map<String, Object> combinedUpdate = call("updateContactMechAndPurposes", Map.of(
                "partyId", "AC21_CONTACT_PARTY",
                "contactMechId", addressForCombinedUpdate.get("contactMechId"),
                "phoneContactMechId", phoneForPurposes.get("contactMechId"),
                "address1", "7 Simple Way",
                "city", "Testville",
                "postalCode", "10007",
                "contactNumber", "5550105"));
        Object combinedAddress = entities.findByEquality(entities.entityClass("PostalAddress"),
                Map.of("contactMechId", combinedUpdate.get("contactMechId"))).get(0);
        assertEquals("7 Simple Way", entities.get(combinedAddress, "address1"));
        Object combinedPhone = entities.findByEquality(entities.entityClass("TelecomNumber"),
                Map.of("contactNumber", "5550105")).get(0);
        assertEquals("5550105", entities.get(combinedPhone, "contactNumber"));

        Map<String, Object> addressWithoutPhoneId = call("createPartyPostalAddress", Map.of(
                "partyId", "AC21_CONTACT_PARTY",
                "address1", "8 Simple Way",
                "city", "Testville",
                "postalCode", "10008"));
        call("updateContactMechAndPurposes", Map.of(
                "partyId", "AC21_CONTACT_PARTY",
                "contactMechId", addressWithoutPhoneId.get("contactMechId"),
                "address1", "9 Simple Way",
                "city", "Testville",
                "postalCode", "10009",
                "contactNumber", "5550199"));
        call("createUpdatePartyTelecomNumber", Map.of(
                "partyId", "AC21_CONTACT_PARTY", "contactNumber", "5550106"));
        call("createUpdatePartyEmailAddress", Map.of(
                "partyId", "AC21_CONTACT_PARTY", "emailAddress", "combined@example.test"));
        call("createUpdatePartyPostalAddress", Map.of(
                "partyId", "AC21_CONTACT_PARTY",
                "address1", "6 Simple Way",
                "city", "Testville",
                "postalCode", "10006"));
    }

    private Map<String, Object> call(String service, Map<String, Object> input) throws Exception {
        var response = mockMvc.perform(post("/api/party/services/" + service)
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(input)))
                .andReturn();
        assertEquals(200, response.getResponse().getStatus(),
                service + ": " + response.getResponse().getContentAsString());
        return objectMapper.readValue(response.getResponse().getContentAsByteArray(), new TypeReference<>() {});
    }

    private void createCommunicationEvent(String communicationEventId) {
        Object event = entities.newEntity(entities.entityClass("CommunicationEvent"));
        entities.set(event, "communicationEventId", communicationEventId);
        entities.set(event, "communicationEventTypeId", "EMAIL_COMMUNICATION");
        entities.set(event, "entryDate", Timestamp.from(Instant.now()));
        entityManager.persist(event);
        entityManager.flush();
    }
}
