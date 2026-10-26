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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
class MultiRollbackTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void ac32_duplicateRoleReturnsConflictAndRollsBackAllRows() throws Exception {
        mockMvc.perform(post("/api/party/services/createPartyGroup")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partyId\":\"AC32_GROUP\",\"groupName\":\"Rollback Group\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/party/services/createPartyRole/multi")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"partyId\":\"AC32_GROUP\",\"roleTypeId\":\"_NA_\"},"
                                + "{\"partyId\":\"AC32_GROUP\",\"roleTypeId\":\"_NA_\"}]"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].errorMessage").value("ServiceValueFound"));
        mockMvc.perform(get("/api/party/entities/PartyRole")
                        .with(httpBasic("admin", "admin-password"))
                        .param("partyId", "AC32_GROUP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
