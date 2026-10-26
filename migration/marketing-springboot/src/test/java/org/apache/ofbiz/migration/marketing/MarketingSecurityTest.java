package org.apache.ofbiz.migration.marketing;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationSecurityProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = {MarketingMigrationApplication.class, TestUsersConfiguration.class},
        properties = MarketingTestSupport.TEST_PROPERTIES)
@AutoConfigureMockMvc
class MarketingSecurityTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MigrationSecurityProperties properties;
    @Autowired
    private EntityRegistry entityRegistry;

    @Test
    void sec01_unauthenticatedRequestIs401() throws Exception {
        mockMvc.perform(get("/api/marketing/entities/MarketingCampaign"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/marketing/services/createMarketingCampaign")
                        .contentType("application/json").content("{\"marketingCampaignId\":\"SEC01\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void sec02_missingBasePermissionIs403() throws Exception {
        mockMvc.perform(get("/api/marketing/entities/MarketingCampaign")
                        .with(httpBasic("nomarketing", "nomarketing")))
                .andExpect(status().isForbidden());
    }

    @Test
    void sec03_readonlyCannotCreateOrRunWriteHandlers() throws Exception {
        mockMvc.perform(post("/api/marketing/services/createMarketingCampaign")
                        .with(httpBasic("readonly", "readonly"))
                        .contentType("application/json").content("{\"marketingCampaignId\":\"SEC03\"}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/marketing/services/signUpForContactList")
                        .with(httpBasic("readonly", "readonly"))
                        .contentType("application/json")
                        .content("{\"contactListId\":\"SEC03\",\"email\":\"sec03@example.com\"}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/marketing/services/arePartiesRelatedThroughSegmentGroup")
                        .with(httpBasic("readonly", "readonly"))
                        .contentType("application/json")
                        .content("{\"partyId\":\"SEC03_A\",\"toPartyId\":\"SEC03_B\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.areRelated").value("N"));
    }

    @Test
    void sec04_sensitiveEntityNeedsDedicatedPermission() throws Exception {
        mockMvc.perform(get("/api/marketing/entities/ContactListPartyStatus")
                        .with(httpBasic("readonly", "readonly")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/marketing/entities/ContactListPartyStatus")
                        .with(httpBasic("sensitive", "sensitive")))
                .andExpect(status().isOk());
    }

    @Test
    void sec05_sensitiveEntitiesExistAndEntityApiIsComponentScoped() throws Exception {
        Assertions.assertTrue(properties.getSensitiveEntities().containsAll(
                List.of("UserLogin", "CreditCard", "PaymentMethod", "EftAccount", "PayHistory",
                        "ContactListPartyStatus")));
        for (String entity : properties.getSensitiveEntities()) {
            Assertions.assertNotNull(entityRegistry.entityClass(entity), entity);
        }
        mockMvc.perform(get("/api/marketing/entities/UserLogin").with(httpBasic("admin", "admin")))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/humanres/entities/MarketingCampaign").with(httpBasic("admin", "admin")))
                .andExpect(status().isNotFound());
        Assertions.assertEquals("a^", properties.getTrustedProxies());
    }

    @Test
    void sec06_multiFailureIs409AndRollsBackEveryItem() throws Exception {
        mockMvc.perform(post("/api/marketing/services/createMarketingCampaign/multi")
                        .with(httpBasic("marketer", "marketer"))
                        .contentType("application/json")
                        .content("[{\"marketingCampaignId\":\"MULTI_A\"},{\"marketingCampaignId\":\"MULTI_B\"},"
                                + "{\"marketingCampaignId\":\"MULTI_A\"}]"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.rolledBack").value(true))
                .andExpect(jsonPath("$.errors[0].index").value(2));
        for (String id : List.of("MULTI_A", "MULTI_B")) {
            mockMvc.perform(get("/api/marketing/entities/MarketingCampaign")
                            .with(httpBasic("readonly", "readonly"))
                            .param("marketingCampaignId", id))
                    .andExpect(jsonPath("$.length()").value(0));
        }
        mockMvc.perform(post("/api/marketing/services/createMarketingCampaign/multi")
                        .with(httpBasic("marketer", "marketer"))
                        .contentType("application/json")
                        .content("[{\"marketingCampaignId\":\"MULTI_C\"},{\"marketingCampaignId\":\"MULTI_D\"}]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
        Assertions.assertNotNull(entityRegistry.findByPrimaryKey(
                entityRegistry.entityClass("MarketingCampaign"), Map.of("marketingCampaignId", "MULTI_D")));
    }
}
