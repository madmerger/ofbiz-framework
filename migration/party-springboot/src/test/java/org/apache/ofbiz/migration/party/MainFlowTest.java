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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.datamodel.entity.UserLogin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = {PartyMigrationApplication.class, TestUsersConfiguration.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MainFlowTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void ac30_partyGroupRelationshipAndContactFlow() throws Exception {
        mockMvc.perform(post("/api/party/services/createPartyGroup")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partyId\":\"AC30_GROUP\",\"groupName\":\"Example Group\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.partyId").value("AC30_GROUP"));
        mockMvc.perform(post("/api/party/services/createPerson")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partyId\":\"AC30_PERSON\",\"firstName\":\"Aki\",\"lastName\":\"Example\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.partyId").value("AC30_PERSON"));
        mockMvc.perform(post("/api/party/services/ensurePartyRole")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partyId\":\"AC30_GROUP\",\"roleTypeId\":\"_NA_\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/party/services/ensurePartyRole")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partyId\":\"AC30_PERSON\",\"roleTypeId\":\"_NA_\"}"))
                .andExpect(status().isOk());

        String firstRelation = "{\"partyIdFrom\":\"AC30_GROUP\",\"partyIdTo\":\"AC30_PERSON\","
                + "\"roleTypeIdFrom\":\"_NA_\",\"roleTypeIdTo\":\"_NA_\","
                + "\"partyRelationshipTypeId\":\"CONTACT_REL\",\"fromDate\":\"2025-01-01 00:00:00\"}";
        mockMvc.perform(post("/api/party/services/createPartyRelationship")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstRelation))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/party/services/createPartyRelationship")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partyIdFrom\":\"AC30_GROUP\",\"partyIdTo\":\"AC30_PERSON\","
                                + "\"roleTypeIdFrom\":\"_NA_\",\"roleTypeIdTo\":\"_NA_\","
                                + "\"partyRelationshipTypeId\":\"EMPLOYMENT\","
                                + "\"fromDate\":\"2025-01-02 00:00:00\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/party/services/createPartyPostalAddress")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partyId\":\"AC30_PERSON\",\"address1\":\"1 Main St\","
                                + "\"city\":\"Test City\",\"countryGeoId\":\"USA\",\"stateProvinceGeoId\":\"CA\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/party/services/createPartyTelecomNumber")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partyId\":\"AC30_PERSON\",\"contactNumber\":\"5550100\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/party/services/createPartyEmailAddress")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partyId\":\"AC30_PERSON\",\"emailAddress\":\"aki@example.test\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/party/services/getPartyPostalAddress")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partyId\":\"AC30_PERSON\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address1").value("1 Main St"));
        mockMvc.perform(post("/api/party/services/getPartyTelephone")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partyId\":\"AC30_PERSON\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contactNumber").value("5550100"));
        mockMvc.perform(post("/api/party/services/getPartyEmail")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partyId\":\"AC30_PERSON\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.infoString").value("aki@example.test"));

        mockMvc.perform(post("/api/party/services/deletePartyRelationship")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partyIdFrom\":\"AC30_GROUP\",\"partyIdTo\":\"AC30_PERSON\","
                                + "\"roleTypeIdFrom\":\"_NA_\",\"roleTypeIdTo\":\"_NA_\","
                                + "\"partyRelationshipTypeId\":\"CONTACT_REL\","
                                + "\"fromDate\":\"2025-01-01 00:00:00\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/party/entities/PartyRelationship")
                        .with(httpBasic("admin", "admin-password"))
                        .param("partyIdFrom", "AC30_GROUP")
                        .param("partyIdTo", "AC30_PERSON"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].partyRelationshipTypeId").value("EMPLOYMENT"));
    }

    @Test
    void ac34_partyRelationshipFromDateCanBeReusedForDeletion() throws Exception {
        mockMvc.perform(post("/api/party/services/createPartyGroup")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partyId\":\"AC34_GROUP\",\"groupName\":\"Date Group\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/party/services/createPerson")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partyId\":\"AC34_PERSON\",\"firstName\":\"From\",\"lastName\":\"Date\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/party/services/ensurePartyRole")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partyId\":\"AC34_GROUP\",\"roleTypeId\":\"_NA_\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/party/services/ensurePartyRole")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partyId\":\"AC34_PERSON\",\"roleTypeId\":\"_NA_\"}"))
                .andExpect(status().isOk());

        String createRelationship = objectMapper.writeValueAsString(Map.of(
                "partyIdFrom", "AC34_GROUP",
                "partyIdTo", "AC34_PERSON",
                "roleTypeIdFrom", "_NA_",
                "roleTypeIdTo", "_NA_",
                "partyRelationshipTypeId", "CONTACT_REL"));
        String createResponse = mockMvc.perform(post("/api/party/services/createPartyRelationship")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRelationship))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String fromDate = objectMapper.readTree(createResponse).path("fromDate").asText();
        assertTrue(!fromDate.isBlank());
        Map<String, Object> relationship = entities.toMap(entities.findByEquality(
                entities.entityClass("PartyRelationship"),
                Map.of("partyIdFrom", "AC34_GROUP", "partyIdTo", "AC34_PERSON",
                        "partyRelationshipTypeId", "CONTACT_REL")).get(0));
        assertEquals(relationship.get("fromDate"), Timestamp.from(OffsetDateTime.parse(fromDate).toInstant()));

        String deleteRelationship = objectMapper.writeValueAsString(Map.of(
                "partyIdFrom", "AC34_GROUP",
                "partyIdTo", "AC34_PERSON",
                "roleTypeIdFrom", "_NA_",
                "roleTypeIdTo", "_NA_",
                "partyRelationshipTypeId", "CONTACT_REL",
                "fromDate", fromDate));
        mockMvc.perform(post("/api/party/services/deletePartyRelationship")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(deleteRelationship))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/party/entities/PartyRelationship")
                        .with(httpBasic("admin", "admin-password"))
                        .param("partyIdFrom", "AC34_GROUP")
                        .param("partyIdTo", "AC34_PERSON"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void ac31_userLoginIsHashedAndMasked() throws Exception {
        mockMvc.perform(post("/api/party/services/createPersonAndUserLogin")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partyId\":\"AC31_PERSON\",\"firstName\":\"Login\","
                                + "\"lastName\":\"Test\",\"userLoginId\":\"ac31-login\","
                                + "\"currentPassword\":\"secure-password\","
                                + "\"currentPasswordVerify\":\"secure-password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPassword").value("[REDACTED]"))
                .andExpect(jsonPath("$.partyId").value("AC31_PERSON"));

        UserLogin userLogin = entityManager.find(UserLogin.class, "ac31-login");
        assertNotNull(userLogin);
        assertNotEquals("secure-password", userLogin.getCurrentPassword());
        assertTrue(passwordEncoder.matches("secure-password", userLogin.getCurrentPassword()));
    }

    @Test
    void ac33_userLoginIgnoresUnlistedFields() throws Exception {
        mockMvc.perform(post("/api/party/services/createPersonAndUserLogin")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partyId\":\"AC33_PERSON\",\"firstName\":\"Login\","
                                + "\"lastName\":\"Fields\",\"userLoginId\":\"ac33-login\","
                                + "\"currentPassword\":\"secure-password\","
                                + "\"currentPasswordVerify\":\"secure-password\","
                                + "\"isSystem\":\"Y\",\"enabled\":\"N\",\"hasLoggedOut\":\"Y\","
                                + "\"disabledDateTime\":\"2025-01-01 00:00:00\"}"))
                .andExpect(status().isOk());

        UserLogin userLogin = entityManager.find(UserLogin.class, "ac33-login");
        assertNotNull(userLogin);
        assertNotEquals("Y", userLogin.getIsSystem());
        assertNull(userLogin.getEnabled());
        assertNull(userLogin.getHasLoggedOut());
        assertNull(userLogin.getDisabledDateTime());
    }
}
