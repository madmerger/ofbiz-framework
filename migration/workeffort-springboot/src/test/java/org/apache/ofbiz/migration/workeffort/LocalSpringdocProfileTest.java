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
package org.apache.ofbiz.migration.workeffort;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
        classes = WorkEffortMigrationApplication.class,
        properties = "migration.security.require-https=false")
@AutoConfigureMockMvc
@ActiveProfiles("local")
class LocalSpringdocProfileTest {
    private static final String TEST_PASSWORD = UUID.randomUUID().toString();

    @DynamicPropertySource
    static void configureTestPasswords(DynamicPropertyRegistry registry) {
        String[] users = {"admin", "planner", "readonly", "auditor", "noworkeffort"};
        for (int user = 0; user < users.length; user++) {
            int index = user;
            registry.add("migration.security.users[" + index + "].username", () -> users[index]);
            registry.add("migration.security.users[" + user + "].password", () -> TEST_PASSWORD);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void docsAndSwaggerUiAreServedWithLocalProfile() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
}
