package org.apache.ofbiz.migration.humanres;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = {HumanResMigrationApplication.class, TestUsersConfiguration.class})
@AutoConfigureMockMvc
class HrTreeSecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private PartyPort partyPort;

    @MockitoSpyBean
    private EntityAutoService entityAutoService;

    @Test
    void ac46_swaggerDocsArePublicAndHumanResApiRemainsProtected() throws Exception {
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
}
