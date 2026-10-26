/*
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
 */
package org.apache.ofbiz.migration.product;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
        classes = ProductMigrationApplication.class,
        properties = "migration.security.require-https=false")
@AutoConfigureMockMvc
class PermissionServicesTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void ac01_returnsTrueForTheRequestedFacilityAuthority() throws Exception {
        mockMvc.perform(post("/api/product/services/facilityPermissionCheck")
                        .with(user("facility").authorities(
                                new SimpleGrantedAuthority("OFBTOOLS_VIEW"),
                                new SimpleGrantedAuthority("FACILITY_VIEW"),
                                new SimpleGrantedAuthority("FACILITY_UPDATE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"mainAction":"UPDATE"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasPermission").value(true));
    }

    @Test
    void ac02_returnsFalseAndFailMessageWhenTheRequestedAuthorityIsMissing() throws Exception {
        mockMvc.perform(post("/api/product/services/facilityPermissionCheck")
                        .with(user("facility").authorities(
                                new SimpleGrantedAuthority("OFBTOOLS_VIEW"),
                                new SimpleGrantedAuthority("FACILITY_VIEW")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"mainAction":"UPDATE","resourceDescription":"inventory"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasPermission").value(false))
                .andExpect(jsonPath("$.failMessage").value(
                        org.hamcrest.Matchers.containsString("FACILITY_UPDATE")));
    }

    @Test
    void ac03_returnsTrueForTheAlternatePermission() throws Exception {
        mockMvc.perform(post("/api/product/services/facilityPermissionCheck")
                        .with(user("facility").authorities(
                                new SimpleGrantedAuthority("OFBTOOLS_VIEW"),
                                new SimpleGrantedAuthority("FACILITY_VIEW"),
                                new SimpleGrantedAuthority("CATALOG_UPDATE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"mainAction":"UPDATE","altPermission":"CATALOG"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasPermission").value(true));
    }
}
