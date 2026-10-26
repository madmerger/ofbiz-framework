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
import org.apache.ofbiz.migration.common.generated.ServiceCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
class EntityAutoServiceApiTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ServiceHandlerRegistry serviceRegistry;

    @Test
    void registersGeneratedCatalogAndCallsEntityAutoServiceOverHttp() throws Exception {
        serviceRegistry.registerCatalog("test", Map.of(
                "createGeo", ServiceCatalog.find("createGeo").orElseThrow()));

        mockMvc.perform(post("/api/test/services/createGeo")
                        .with(httpBasic("writer", "writer-password"))
                        .contentType("application/json")
                        .content("{\"geoId\":\"CALLER_GEO_ID\",\"geoName\":\"Generated catalog\","
                                + "\"geoTypeId\":\"COUNTRY\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.geoId").value("CALLER_GEO_ID"));

        mockMvc.perform(get("/api/test/entities/Geo")
                        .param("geoId", "CALLER_GEO_ID")
                        .with(httpBasic("writer", "writer-password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].geoName").value("Generated catalog"));
    }

    @Test
    void requiresNonOptionalInoutParametersAndRejectsUndeclaredUpdateFields() throws Exception {
        serviceRegistry.registerCatalog("test", Map.of(
                "createRequiredInout",
                new ServiceHandlerRegistry.ServiceDefinition(
                        "createRequiredInout",
                        "Enumeration",
                        "create",
                        "CREATE",
                        List.of(
                                new ServiceHandlerRegistry.ParamDefinition("enumId", "IN", false, "id"),
                                new ServiceHandlerRegistry.ParamDefinition("enumTypeId", "INOUT", false, "id")))));

        mockMvc.perform(post("/api/test/services/createRequiredInout")
                        .with(httpBasic("writer", "writer-password"))
                        .contentType("application/json")
                        .content("{\"enumId\":\"MISSING_INOUT\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Missing required input: enumTypeId"));

        serviceRegistry.registerCatalog("test", Map.of(
                "createGeo", ServiceCatalog.find("createGeo").orElseThrow(),
                "updateGeo", ServiceCatalog.find("updateGeo").orElseThrow()));
        mockMvc.perform(post("/api/test/services/createGeo")
                        .with(httpBasic("writer", "writer-password"))
                        .contentType("application/json")
                        .content("{\"geoId\":\"UPDATE_GEO\",\"geoName\":\"Original\","
                                + "\"geoTypeId\":\"COUNTRY\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/test/services/updateGeo")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType("application/json")
                        .content("{\"geoId\":\"UPDATE_GEO\",\"createdStamp\":\"2024-01-01T00:00:00\","
                                + "\"geoName\":\"Changed\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Unknown field: createdStamp"));

        mockMvc.perform(get("/api/test/entities/Geo")
                        .param("geoId", "UPDATE_GEO")
                        .with(httpBasic("writer", "writer-password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].geoName").value("Original"));
    }
}
