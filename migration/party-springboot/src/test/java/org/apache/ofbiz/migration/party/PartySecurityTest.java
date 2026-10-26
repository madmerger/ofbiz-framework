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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = {PartyMigrationApplication.class, TestUsersConfiguration.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PartySecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void ac40_accessRequiresAuthenticationAndPartyAuthority() throws Exception {
        mockMvc.perform(get("/api/party/entities/Party"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/party/entities/Party").with(httpBasic("nopartymgr", "nopartymgr-password")))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/party/services/createPerson")
                        .with(httpBasic("readonly", "readonly-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partyId\":\"AC40_DENIED\",\"firstName\":\"No\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void ac41_sensitiveEntitiesAndResultRequireSensitiveAuthority() throws Exception {
        mockMvc.perform(get("/api/party/entities/UserLogin").with(httpBasic("readonly", "readonly-password")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/party/entities/UserLogin").with(httpBasic("sensitive", "sensitive-password")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/party/entities/UserLogin").with(httpBasic("admin", "admin-password")))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/party/services/createPartyIdentification")
                        .with(httpBasic("writer", "writer-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partyId\":\"SENSITIVE\",\"partyIdentificationTypeId\":\"SSN\","
                                + "\"idValue\":\"123\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void ac42_componentMismatchReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/workeffort/entities/Party")
                        .with(httpBasic("admin", "admin-password")))
                .andExpect(status().isNotFound());
    }
}
