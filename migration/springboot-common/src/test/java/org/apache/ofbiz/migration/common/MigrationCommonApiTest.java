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
package org.apache.ofbiz.migration.common;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
        classes = MigrationCommonApiTest.TestApplication.class,
        properties = {
            "spring.datasource.url=jdbc:h2:mem:common;DB_CLOSE_DELAY=-1",
            "spring.datasource.username=sa",
            "spring.datasource.password=",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.open-in-view=false",
            "migration.security.service-permission-prefix=TEST",
            "migration.security.users[0].username=writer",
            "migration.security.users[0].password=writer-password",
            "migration.security.users[0].authorities[0]=TEST_VIEW",
            "migration.security.users[0].authorities[1]=TEST_CREATE",
            "migration.security.users[0].authorities[2]=TEST_SENSITIVE_VIEW",
            "migration.security.users[1].username=readonly",
            "migration.security.users[1].password=readonly-password",
            "migration.security.users[1].authorities[0]=TEST_VIEW",
            "migration.security.sensitive-entities[0]=Enumeration"
        })
@AutoConfigureMockMvc
class MigrationCommonApiTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ServiceHandlerRegistry serviceRegistry;

    @Autowired
    private EntityRegistry entityRegistry;

    @BeforeEach
    void registerEnumerationServices() {
        List<ServiceHandlerRegistry.ParamDefinition> parameters = List.of(
                new ServiceHandlerRegistry.ParamDefinition("enumId", "IN", false, "id"),
                new ServiceHandlerRegistry.ParamDefinition("enumTypeId", "IN", false, "id"),
                new ServiceHandlerRegistry.ParamDefinition("description", "IN", true, "description"));
        serviceRegistry.registerCatalog("test", Map.of(
                "createEnumeration",
                new ServiceHandlerRegistry.ServiceDefinition(
                        "createEnumeration", "Enumeration", "create", null, parameters)));
    }

    @Test
    void createsAndReadsAnEntity() throws Exception {
        mockMvc.perform(post("/api/test/services/createEnumeration")
                        .with(httpBasic("writer", "writer-password"))
                        .contentType("application/json")
                        .content("{\"enumId\":\"TEST_ENUM\",\"enumTypeId\":\"TEST_TYPE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enumId").value("TEST_ENUM"));

        mockMvc.perform(get("/api/test/entities/Enumeration")
                        .param("enumId", "TEST_ENUM")
                        .with(httpBasic("writer", "writer-password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].enumId").value("TEST_ENUM"));
    }

    @Test
    void readonlyUserCannotCreateAndMultiFailureRollsBackPriorWrites() throws Exception {
        mockMvc.perform(post("/api/test/services/createEnumeration")
                        .with(httpBasic("readonly", "readonly-password"))
                        .contentType("application/json")
                        .content("{\"enumId\":\"DENIED\",\"enumTypeId\":\"TEST_TYPE\"}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/test/services/createEnumeration/multi")
                        .with(httpBasic("writer", "writer-password"))
                        .contentType("application/json")
                        .content("[{\"enumId\":\"ROLLBACK\",\"enumTypeId\":\"TEST_TYPE\"},"
                                + "{\"enumTypeId\":\"TEST_TYPE\"}]"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/test/entities/Enumeration")
                        .param("enumId", "ROLLBACK")
                        .with(httpBasic("writer", "writer-password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
        mockMvc.perform(get("/api/test/entities/Enumeration")
                        .param("enumId", "DENIED")
                        .with(httpBasic("writer", "writer-password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void requiresAuthenticationAndSensitiveViewAuthority() throws Exception {
        mockMvc.perform(get("/api/test/entities/Enumeration"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/test/entities/Enumeration")
                        .with(httpBasic("readonly", "readonly-password")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/test/entities/Enumeration")
                        .with(httpBasic("writer", "writer-password")))
                .andExpect(status().isOk());
    }

    @Test
    void redactsCredentialFieldsFromEntityMaps() {
        var login = new org.apache.ofbiz.migration.datamodel.entity.UserLogin();
        login.setCurrentPassword("sensitive-password");
        login.setPasswordHint("private hint");
        Map<String, Object> data = entityRegistry.toMap(login);
        org.junit.jupiter.api.Assertions.assertEquals("[REDACTED]", data.get("currentPassword"));
        org.junit.jupiter.api.Assertions.assertEquals("[REDACTED]", data.get("passwordHint"));
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @ComponentScan("org.apache.ofbiz.migration.common")
    @EntityScan("org.apache.ofbiz.migration.datamodel.entity")
    @EnableJpaRepositories("org.apache.ofbiz.migration.datamodel.repository")
    static class TestApplication {
    }
}
