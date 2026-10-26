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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/** local プロファイルのみ Swagger 有効、HTTPS 必須設定でのリダイレクトを検証する。 */
class ContentProfileTest {

    @Nested
    @SpringBootTest(classes = ContentMigrationApplication.class, properties = "CONTENT_DEMO_PASSWORD=profile-test-only")
    @ActiveProfiles("local")
    @AutoConfigureMockMvc
    class LocalProfile {
        @Autowired
        private MockMvc mockMvc;

        @Test
        void ac46SwaggerIsEnabledOnlyInLocalProfile() throws Exception {
            mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
            mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
        }
    }

    @Nested
    @SpringBootTest(classes = ContentMigrationApplication.class, properties = "migration.security.require-https=true")
    @ActiveProfiles("test")
    @AutoConfigureMockMvc
    class HttpsRequired {
        @Autowired
        private MockMvc mockMvc;

        @Test
        void ac47CleartextRequestsAreRedirectedToHttps() throws Exception {
            mockMvc.perform(get("/api/content/entities/Content").secure(false))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("https://**"));
        }
    }
}
