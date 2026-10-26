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
package org.apache.ofbiz.migration.humanres;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
        classes = {HumanResMigrationApplication.class, TestUsersConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
            "migration.security.require-https=true",
            "server.forward-headers-strategy=native",
            "server.tomcat.remoteip.internal-proxies=127\\.0\\.0\\.1",
            "server.tomcat.remoteip.protocol-header=X-Forwarded-Proto"
        })
@AutoConfigureMockMvc
class HttpsSecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    void untrustedForwardedHttpsIsIgnoredAndTrustedProxyIsHonored() throws Exception {
        mockMvc.perform(get("/actuator/health")
                        .with(request -> {
                            request.setRemoteAddr("10.0.0.5");
                            return request;
                        })
                        .header("X-Forwarded-Proto", "https"))
                .andExpect(status().is3xxRedirection());
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Forwarded-Proto", "https");
        ResponseEntity<String> response = restTemplate.exchange(
                "http://127.0.0.1:" + port + "/actuator/health",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);
        assertEquals(200, response.getStatusCode().value());
    }
}
