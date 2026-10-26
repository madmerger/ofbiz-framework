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
package org.apache.ofbiz.migration.manufacturing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
class ManufacturingSecurityTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate rest;

    @Test
    void ac05_apiAuthorizationSensitiveEntityAndSwagger() {
        String base = "http://localhost:" + port;
        assertEquals(HttpStatus.UNAUTHORIZED, rest.getForEntity(base + "/api/manufacturing/entities/Product",
                String.class).getStatusCode());
        assertEquals(HttpStatus.FORBIDDEN, rest.withBasicAuth("ofbtoolsonly", password())
                .getForEntity(base + "/api/manufacturing/entities/Product", String.class).getStatusCode());
        assertEquals(HttpStatus.FORBIDDEN, rest.withBasicAuth("mfgviewer", password())
                .getForEntity(base + "/api/manufacturing/entities/CostComponent", String.class).getStatusCode());
        assertEquals(HttpStatus.OK, rest.withBasicAuth("mfgwriter", password())
                .getForEntity(base + "/api/manufacturing/entities/CostComponent", String.class).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, rest.withBasicAuth("mfgadmin", password())
                .getForEntity(base + "/api/humanres/entities/Product", String.class).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, rest.withBasicAuth("mfgadmin", password())
                .getForEntity(base + "/api/manufacturing/entities/PaymentMethod", String.class).getStatusCode());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("calendarId", "DENIED"), headers);
        ResponseEntity<String> denied = rest.withBasicAuth("mfgviewer", password()).exchange(
                base + "/api/manufacturing/services/createCalendar", HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.FORBIDDEN, denied.getStatusCode());
        assertEquals(HttpStatus.OK, rest.getForEntity(base + "/swagger-ui/index.html", String.class).getStatusCode());
    }

    private static String password() {
        return System.getenv("MANUFACTURING_DEMO_PASSWORD");
    }
}
