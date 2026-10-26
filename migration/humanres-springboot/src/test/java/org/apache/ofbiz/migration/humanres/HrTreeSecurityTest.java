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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import org.apache.ofbiz.migration.humanres.generated.PayHistoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
        classes = {HumanResMigrationApplication.class, TestUsersConfiguration.class},
        properties = "HUMANRES_DEMO_PASSWORD=test-demo-password")
@AutoConfigureMockMvc
@ActiveProfiles("local")
class HrTreeSecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private PartyPort partyPort;

    @MockitoSpyBean
    private EntityAutoService entityAutoService;

    @Autowired
    private PayHistoryRepository payHistoryRepository;

    @Test
    void ac46_swaggerDocsArePublicAndHumanResApiRemainsProtected() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/api/humanres/hr-tree")))
                .andExpect(content().string(containsString("\"basicAuth\"")))
                .andExpect(jsonPath("$.components.securitySchemes.basicAuth.type").value("http"))
                .andExpect(jsonPath("$.components.securitySchemes.basicAuth.scheme").value("basic"))
                .andExpect(jsonPath("$.security[0].basicAuth").isArray());
        mockMvc.perform(get("/api/humanres/hr-tree").param("partyId", "Company"))
                .andExpect(status().isUnauthorized());
        verify(partyPort, never()).isPartyGroup(anyString());
    }

    @Test
    void ac40_anonymousIsRejectedBeforeTreeLookup() throws Exception {
        mockMvc.perform(get("/api/humanres/hr-tree").param("partyId", "Company"))
                .andExpect(status().isUnauthorized());
        verify(partyPort, never()).isPartyGroup(anyString());
    }

    @Test
    void ac41_missingHumanResAuthorityIsRejectedBeforeTreeLookup() throws Exception {
        mockMvc.perform(get("/api/humanres/hr-tree")
                        .param("partyId", "Company")
                        .with(httpBasic("nohr", "nohr")))
                .andExpect(status().isForbidden());
        verify(partyPort, never()).isPartyGroup(anyString());
    }

    @Test
    void ac42_authorizedHumanResViewReachesTreeService() throws Exception {
        mockMvc.perform(get("/api/humanres/hr-tree")
                        .param("partyId", "unknown-party")
                        .with(httpBasic("view", "view")))
                .andExpect(status().isOk());
        verify(partyPort).isPartyGroup("unknown-party");
    }

    @Test
    void ac43_humanResAuthorityWithoutOfbtoolsBaseGateIsRejected() throws Exception {
        mockMvc.perform(get("/api/humanres/hr-tree")
                        .param("partyId", "Company")
                        .with(httpBasic("noofb", "noofb")))
                .andExpect(status().isForbidden());
        verify(partyPort, never()).isPartyGroup(anyString());
    }

    @Test
    void ac44_missingPerServiceActionIsRejectedBeforeEntityAutoDispatch() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/api/humanres/services/createEmplPositionType")
                        .contentType("application/json")
                        .content("{\"emplPositionTypeId\":\"AC44_TYPE\"}")
                        .with(httpBasic("no-create", "no-create")))
                .andExpect(status().isForbidden());
        verify(entityAutoService, never()).execute(
                org.mockito.ArgumentMatchers.eq("createEmplPositionType"), anyMap());
    }

    @Test
    void ac44_readOnlyUserCannotMutateCatalogServicesWithoutPermissionMainAction() throws Exception {
        mockMvc.perform(post("/api/humanres/services/createBenefitType")
                        .contentType("application/json")
                        .content("{\"benefitTypeId\":\"AC44_BENEFIT\"}")
                        .with(httpBasic("view", "view")))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/humanres/services/deleteBenefitType")
                        .contentType("application/json")
                        .content("{\"benefitTypeId\":\"AC44_BENEFIT\"}")
                        .with(httpBasic("view", "view")))
                .andExpect(status().isForbidden());
        verify(entityAutoService, never()).execute(anyString(), anyMap());
    }

    @Test
    void sensitiveEntityReadsRequireSensitiveViewAuthority() throws Exception {
        mockMvc.perform(get("/api/humanres/entities/PayHistory")
                        .with(httpBasic("view", "view")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/humanres/entities/PayHistory")
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk());
    }

    @Test
    void sensitiveEntityAuthorizationUsesResolvedEntityName() throws Exception {
        mockMvc.perform(get(URI.create("/api/humanres/entities/Pay%48istory"))
                        .with(httpBasic("view", "view")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/humanres/entities/PayHistory;variant=1")
                        .with(httpBasic("view", "view")))
                .andExpect(status().is4xxClientError());
        mockMvc.perform(get("/api/humanres/entities/payhistory")
                        .with(httpBasic("view", "view")))
                .andExpect(status().is4xxClientError());
        mockMvc.perform(get("/api/humanres/entities/PayHistory")
                        .with(httpBasic("sensitive", "sensitive")))
                .andExpect(status().isOk());
    }

    @Test
    void sensitiveEntityAutoResponsesRequireSensitiveViewAuthority() throws Exception {
        long countBefore = payHistoryRepository.count();
        mockMvc.perform(post("/api/humanres/services/createPayHistory")
                        .contentType("application/json")
                        .content("{}")
                        .with(httpBasic("writer", "writer")))
                .andExpect(status().isForbidden());
        assertEquals(countBefore, payHistoryRepository.count());
        verify(entityAutoService, never()).execute(eq("createPayHistory"), anyMap());
    }
}
