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
package org.apache.ofbiz.migration.commonext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationSecurityProperties;
import org.apache.ofbiz.migration.datamodel.entity.UserLogin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import jakarta.persistence.EntityManager;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CommonextSecurityTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private EntityRegistry entityRegistry;
    @Autowired
    private MigrationSecurityProperties securityProperties;

    @BeforeEach
    void seed() {
        CommonextTestSupport.seedUserLogins(entityManager);
    }

    @Test
    void ac01apiRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/commonext/services/getSystemInfoNotes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ac02basePermissionFailureReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/commonext/services/getSystemInfoNotes")
                        .with(httpBasic("nosetup", "commonext-test-password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void ac03readonlyUserCannotCreateSystemNotes() throws Exception {
        mockMvc.perform(post("/api/commonext/services/createSystemInfoNote")
                        .with(httpBasic("readonly", "commonext-test-password"))
                        .contentType("application/json")
                        .content("{\"noteInfo\":\"Not allowed\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void ac04sensitiveUserLoginAccessRequiresAuthorityAndRedactsPassword() throws Exception {
        mockMvc.perform(get("/api/commonext/entities/UserLogin/one")
                        .param("userLoginId", "admin")
                        .with(httpBasic("readonly", "commonext-test-password")))
                .andExpect(status().isForbidden());
        assertEquals(
                "LOCAL-SEED-PLACEHOLDER-HASH",
                entityManager.find(UserLogin.class, "admin").getCurrentPassword());
        mockMvc.perform(get("/api/commonext/entities/UserLogin/one")
                        .param("userLoginId", "admin")
                        .with(httpBasic("editor", "commonext-test-password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPassword").value("[REDACTED]"));
        mockMvc.perform(get("/api/commonext/entities/UserLogin/one")
                        .param("userLoginId", "admin")
                        .with(httpBasic("admin", "commonext-test-password")))
                .andExpect(status().isOk());
    }

    @Test
    void ac05everyConfiguredSensitiveEntityResolves() {
        for (String entityName : securityProperties.getSensitiveEntities()) {
            entityRegistry.entityClass(entityName);
        }
    }

    @Test
    void ac06defaultSwaggerIsDisabledAndLocalProfileEnablesIt() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isNotFound());
        String localConfig = Files.readString(Path.of(
                System.getProperty("commonext.ofbiz.root"),
                "migration/commonext-springboot/src/main/resources/application-local.yml"));
        assertTrue(localConfig.contains("enabled: true"));
    }

    @Test
    void ac07postgresProfileRequiresHttpsAndDefaultsToNoTrustedProxies() throws Exception {
        String postgresConfig = Files.readString(Path.of(
                System.getProperty("commonext.ofbiz.root"),
                "migration/commonext-springboot/src/main/resources/application-postgres.yml"));
        assertTrue(postgresConfig.contains("require-https: true"));
        assertTrue(postgresConfig.contains("${MIGRATION_SECURITY_TRUSTED_PROXIES:a^}"));
        assertTrue(securityProperties.getSensitiveEntities().containsAll(List.of(
                "UserLogin", "PaymentGatewayConfig", "X509IssuerProvision")));
    }

    @Test
    void ac08rejectsOtherComponentsAndEntitiesOutsideTheCommonextAllowlist() throws Exception {
        mockMvc.perform(get("/api/humanres/entities/NoteData")
                        .with(httpBasic("admin", "commonext-test-password")))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/commonext/entities/CreditCard")
                        .with(httpBasic("editor", "commonext-test-password")))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/commonext/entities/PayHistory")
                        .with(httpBasic("editor", "commonext-test-password")))
                .andExpect(status().isNotFound());
    }

    @Test
    void ac09everyConfiguredExtraEntityResolves() {
        for (String entityName : securityProperties.getExtraEntities()) {
            entityRegistry.entityClass(entityName);
        }
    }

    @Test
    void ac10noteDataIsNotAvailableThroughEntityApi() throws Exception {
        mockMvc.perform(get("/api/commonext/entities/NoteData")
                        .with(httpBasic("readonly", "commonext-test-password")))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/commonext/entities/NoteData")
                        .with(httpBasic("admin", "commonext-test-password")))
                .andExpect(status().isNotFound());
    }
}
