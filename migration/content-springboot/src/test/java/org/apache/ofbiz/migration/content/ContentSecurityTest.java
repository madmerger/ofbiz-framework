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
package org.apache.ofbiz.migration.content;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/** 認証・権限・機密エンティティ・/multi ロールバック・Swagger 既定無効の検証。 */
@SpringBootTest(classes = ContentMigrationApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ContentSecurityTest extends ContentApiTestSupport {

    @Test
    void ac40UnauthenticatedRequestsAreRejected() throws Exception {
        call(null, "createContentType", Map.of("contentTypeId", "AC40")).andExpect(status().isUnauthorized());
        mvc().perform(get("/api/content/entities/Content")).andExpect(status().isUnauthorized());
        mvc().perform(get("/api/content/entities/Content").with(httpBasic("admin", "wrong")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ac41MissingPermissionsAreForbidden() throws Exception {
        entities("nocontent", "Content", Map.of()).andExpect(status().isForbidden());
        call("readonly", "createContentType", Map.of("contentTypeId", "AC41")).andExpect(status().isForbidden());
        call("readonly", "createDataResourceAndText", Map.of("dataResourceTypeId", "ELECTRONIC_TEXT"))
                .andExpect(status().isForbidden());
        call("editor", "createContentType", Map.of("contentTypeId", "AC41_OK")).andExpect(status().isOk());
    }

    @Test
    void ac42SensitiveEntitiesRequireDedicatedPermission() throws Exception {
        entities("readonly", "SurveyResponseAnswer", Map.of()).andExpect(status().isForbidden());
        entities("readonly", "SurveyResponse", Map.of()).andExpect(status().isForbidden());
        // content の catalog/extra-entities 外（UserLogin・CreditCard 等）は Entity API から見えない
        entities("admin", "UserLogin", Map.of()).andExpect(status().isNotFound());
        entities("admin", "CreditCard", Map.of()).andExpect(status().isNotFound());
        entities("auditor", "SurveyResponseAnswer", Map.of()).andExpect(status().isOk());
        entities("admin", "SurveyResponseAnswer", Map.of()).andExpect(status().isOk());
        entities("readonly", "Content", Map.of()).andExpect(status().isOk());
    }

    @Test
    void ac49OtherComponentPathIsNotFound() throws Exception {
        mvc().perform(get("/api/humanres/entities/Content").with(httpBasic("admin", PASSWORD)))
                .andExpect(status().isNotFound());
    }

    @Test
    void ac43MultiConflictRollsBackEveryRow() throws Exception {
        multi("editor", "createContentType", List.of(
                Map.of("contentTypeId", "AC43_A"), Map.of("contentTypeId", "AC43_B"), Map.of("contentTypeId", "AC43_A")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].index").value(2))
                .andExpect(jsonPath("$.errors[0].errorMessage").value("ServiceValueFound"));
        entities("readonly", "ContentType", Map.of("contentTypeId", "AC43_A")).andExpect(jsonPath("$.length()").value(0));
        entities("readonly", "ContentType", Map.of("contentTypeId", "AC43_B")).andExpect(jsonPath("$.length()").value(0));
        multi("editor", "createContentType", List.of(Map.of("contentTypeId", "AC43_C"), Map.of("contentTypeId", "AC43_D")))
                .andExpect(status().isOk());
        entities("readonly", "ContentType", Map.of("contentTypeId", "AC43_D")).andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void ac44SwaggerIsDisabledOutsideLocalProfile() throws Exception {
        mvc().perform(get("/v3/api-docs")).andExpect(status().isNotFound());
    }

    @Test
    void ac45ProfilesKeepSecretsOutAndRequireHttpsInPostgres() throws Exception {
        Path resources = Path.of(System.getProperty("ofbiz.root"), "migration/content-springboot/src/main/resources");
        String local = Files.readString(resources.resolve("application-local.yml"));
        String postgres = Files.readString(resources.resolve("application-postgres.yml"));
        String base = Files.readString(resources.resolve("application.yml"));
        assertTrue(local.contains("${CONTENT_DEMO_PASSWORD}"));
        assertTrue(!local.matches("(?s).*password: (?!\\$\\{).*"));
        assertTrue(postgres.contains("require-https: true"));
        assertTrue(base.contains("MIGRATION_SECURITY_TRUSTED_PROXIES:a^"));
        assertTrue(base.contains("- CONTENTMGR"));
    }
}
