package org.apache.ofbiz.migration.marketing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(classes = MarketingMigrationApplication.class,
        properties = "spring.profiles.active=postgres")
class PostgresSmokeTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private MigrationServiceDispatcher dispatcher;

    @AfterEach
    void logout() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void postgresCampaignContactListAndSignUpSmoke() {
        MarketingTestSupport.loginAdmin();
        String campaignId = (String) MarketingTestSupport.run(dispatcher, "createMarketingCampaign",
                Map.of("campaignName", "PostgreSQL")).get("marketingCampaignId");
        MarketingTestSupport.run(dispatcher, "createContactList", Map.of(
                "contactListId", "PG_CL", "contactMechTypeId", "EMAIL_ADDRESS", "marketingCampaignId", campaignId));
        Map<String, Object> signUp = MarketingTestSupport.run(dispatcher, "signUpForContactList",
                Map.of("contactListId", "PG_CL", "email", "pg@example.com"));
        assertEquals("CLPT_PENDING", signUp.get("statusId"));
        MarketingTestSupport.run(dispatcher, "deleteMarketingCampaign", Map.of("marketingCampaignId", campaignId));
        MigrationException missing = assertThrows(MigrationException.class, () -> MarketingTestSupport.run(
                dispatcher, "updateMarketingCampaign", Map.of("marketingCampaignId", campaignId, "campaignName", "x")));
        assertEquals(404, missing.getStatus().value());
    }
}
