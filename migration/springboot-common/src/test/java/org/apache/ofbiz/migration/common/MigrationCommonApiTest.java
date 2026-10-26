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

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.ofbiz.migration.datamodel.entity.Enumeration;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
        classes = MigrationCommonApiTest.TestApplication.class,
        properties = {
            "spring.datasource.url=jdbc:h2:mem:common;DB_CLOSE_DELAY=-1",
            "spring.datasource.username=sa",
            "spring.datasource.password=",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.open-in-view=false",
            "migration.security.component=test",
            "migration.security.require-https=false",
            "migration.security.service-permission-prefix=TEST",
            "migration.security.users[0].username=writer",
            "migration.security.users[0].password=writer-password",
            "migration.security.users[0].authorities[0]=TEST_VIEW",
            "migration.security.users[0].authorities[1]=TEST_CREATE",
            "migration.security.users[0].authorities[2]=TEST_SENSITIVE_VIEW",
            "migration.security.users[0].authorities[3]=OFBTOOLS_VIEW",
            "migration.security.users[1].username=readonly",
            "migration.security.users[1].password=readonly-password",
            "migration.security.users[1].authorities[0]=TEST_VIEW",
            "migration.security.users[1].authorities[1]=OFBTOOLS_VIEW",
            "migration.security.users[2].username=ofbtools-only",
            "migration.security.users[2].password=ofbtools-password",
            "migration.security.users[2].authorities[0]=OFBTOOLS_VIEW",
            "migration.security.users[3].username=both",
            "migration.security.users[3].password=both-password",
            "migration.security.users[3].authorities[0]=OFBTOOLS_VIEW",
            "migration.security.users[3].authorities[1]=TEST_VIEW",
            "migration.security.users[4].username=admin",
            "migration.security.users[4].password=admin-password",
            "migration.security.users[4].authorities[0]=OFBTOOLS_VIEW",
            "migration.security.users[4].authorities[1]=TEST_ADMIN",
            "migration.security.sensitive-entities[0]=Enumeration",
            "migration.security.sensitive-entities[1]=PayHistory",
            "migration.security.extra-entities[0]=GeoType"
        })
@AutoConfigureMockMvc
class MigrationCommonApiTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ServiceHandlerRegistry serviceRegistry;

    @Autowired
    private EntityRegistry entityRegistry;

    @Autowired
    private EntityManager entityManager;

    private final AtomicBoolean handlerInvoked = new AtomicBoolean();
    private final AtomicBoolean defaultPermissionHandlerInvoked = new AtomicBoolean();
    private final AtomicBoolean sensitiveResultHandlerInvoked = new AtomicBoolean();

    @BeforeEach
    void registerEnumerationServices() {
        handlerInvoked.set(false);
        defaultPermissionHandlerInvoked.set(false);
        sensitiveResultHandlerInvoked.set(false);
        List<ServiceHandlerRegistry.ParamDefinition> parameters = List.of(
                new ServiceHandlerRegistry.ParamDefinition("enumId", "IN", false, "id"),
                new ServiceHandlerRegistry.ParamDefinition("enumTypeId", "IN", false, "id"),
                new ServiceHandlerRegistry.ParamDefinition("description", "IN", true, "description"));
        serviceRegistry.registerCatalog("test", Map.of(
                "createEnumeration",
                new ServiceHandlerRegistry.ServiceDefinition(
                        "createEnumeration", "Enumeration", "create", null, parameters)));
        serviceRegistry.registerHandler("test", "handlerOnly", "UPDATE", input -> {
            handlerInvoked.set(true);
            return Map.of("handled", true);
        });
        serviceRegistry.registerHandler("test", "defaultHandler", input -> {
            defaultPermissionHandlerInvoked.set(true);
            return Map.of("handled", true);
        });
        serviceRegistry.registerHandler("test", "sensitiveHandler", "VIEW", "PayHistory", input -> {
            sensitiveResultHandlerInvoked.set(true);
            return Map.of("handled", true);
        });
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
    void duplicateMultiCreateReturnsConflictAndRollsBackPriorWrites() throws Exception {
        String suffix = Long.toString(System.nanoTime(), 36);
        String existingId = "EX" + suffix;
        String rolledBackId = "RB" + suffix;

        mockMvc.perform(post("/api/test/services/createEnumeration")
                        .with(httpBasic("writer", "writer-password"))
                        .contentType("application/json")
                        .content("{\"enumId\":\"" + existingId + "\",\"enumTypeId\":\"TEST_TYPE\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/test/services/createEnumeration/multi")
                        .with(httpBasic("writer", "writer-password"))
                        .contentType("application/json")
                        .content("[{\"enumId\":\"" + rolledBackId + "\",\"enumTypeId\":\"TEST_TYPE\"},"
                                + "{\"enumId\":\"" + existingId + "\",\"enumTypeId\":\"TEST_TYPE\"}]"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.responseMessage").value("error"))
                .andExpect(jsonPath("$.errorMessage").value("One or more service calls failed"))
                .andExpect(jsonPath("$.errors[0].index").value(1))
                .andExpect(jsonPath("$.errors[0].errorMessage").value("ServiceValueFound"));

        mockMvc.perform(get("/api/test/entities/Enumeration")
                        .param("enumId", rolledBackId)
                        .with(httpBasic("writer", "writer-password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
        mockMvc.perform(get("/api/test/entities/Enumeration")
                        .param("enumId", existingId)
                        .with(httpBasic("writer", "writer-password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].enumId").value(existingId));
    }

    @Test
    @Transactional
    void equalityQueriesAreUnlimitedUnlessMaxResultsIsProvided() {
        String suffix = Long.toString(System.nanoTime(), 36);
        String enumTypeId = "T" + suffix;
        String enumIdPrefix = "E" + suffix + "_";
        for (int index = 0; index < 150; index++) {
            Enumeration enumeration = new Enumeration();
            enumeration.setEnumId(enumIdPrefix + index);
            enumeration.setEnumTypeId(enumTypeId);
            entityManager.persist(enumeration);
        }
        entityManager.flush();

        Map<String, ?> filters = Map.of("enumTypeId", enumTypeId);
        org.junit.jupiter.api.Assertions.assertEquals(
                150, entityRegistry.findByEquality(Enumeration.class, filters).size());
        org.junit.jupiter.api.Assertions.assertEquals(
                100, entityRegistry.findByEquality(Enumeration.class, filters, 100).size());
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
    void requiresEveryConfiguredBaseAndComponentPermission() throws Exception {
        mockMvc.perform(get("/api/test/entities/GeoType")
                        .with(httpBasic("ofbtools-only", "ofbtools-password")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/test/entities/GeoType")
                        .with(httpBasic("both", "both-password")))
                .andExpect(status().isOk());
    }

    @Test
    void handlerOnlyServiceRequiresItsActionAndDefaultsToAdmin() throws Exception {
        mockMvc.perform(post("/api/test/services/handlerOnly")
                        .with(httpBasic("readonly", "readonly-password"))
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isForbidden());
        org.junit.jupiter.api.Assertions.assertFalse(handlerInvoked.get());

        mockMvc.perform(post("/api/test/services/defaultHandler")
                        .with(httpBasic("writer", "writer-password"))
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isForbidden());
        org.junit.jupiter.api.Assertions.assertFalse(defaultPermissionHandlerInvoked.get());

        mockMvc.perform(post("/api/test/services/defaultHandler")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isOk());
        org.junit.jupiter.api.Assertions.assertTrue(defaultPermissionHandlerInvoked.get());
    }

    @Test
    void handlerSensitiveResultRequiresSensitiveViewPermission() throws Exception {
        mockMvc.perform(post("/api/test/services/sensitiveHandler")
                        .with(httpBasic("readonly", "readonly-password"))
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isForbidden());
        org.junit.jupiter.api.Assertions.assertFalse(sensitiveResultHandlerInvoked.get());

        mockMvc.perform(post("/api/test/services/sensitiveHandler")
                        .with(httpBasic("writer", "writer-password"))
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isOk());
        org.junit.jupiter.api.Assertions.assertTrue(sensitiveResultHandlerInvoked.get());
    }

    @Test
    void unknownServiceIsNotFoundAfterAuthentication() throws Exception {
        mockMvc.perform(post("/api/test/services/notRegistered")
                        .with(httpBasic("writer", "writer-password"))
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isNotFound());
        mockMvc.perform(post("/api/test/services/notRegistered/multi")
                        .with(httpBasic("writer", "writer-password"))
                        .contentType("application/json")
                        .content("[]"))
                .andExpect(status().isNotFound());
        mockMvc.perform(post("/api/test/services/notRegistered")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isUnauthorized());
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
