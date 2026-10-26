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
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.manufacturing.adapter.local.LocalProductAdapter;
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
class MultiRollbackTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate rest;
    @Autowired
    private ProductPort products;

    @Test
    void ac06_calendarMultiDuplicateReturns409AndRollsBackFirstRow() {
        String base = "http://localhost:" + port;
        List<Map<String, String>> rows = List.of(
                Map.of("calendarId", "AC06_HTTP_CAL", "description", "first"),
                Map.of("calendarId", "AC06_HTTP_CAL", "description", "duplicate"));
        ResponseEntity<String> response = post(base + "/api/manufacturing/services/createCalendar/multi", rows);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ResponseEntity<String> missing = rest.withBasicAuth("mfgadmin", password()).getForEntity(
                base + "/api/manufacturing/entities/TechDataCalendar/one?calendarId=AC06_HTTP_CAL", String.class);
        assertEquals(HttpStatus.NOT_FOUND, missing.getStatusCode());
    }

    @Test
    void ac06_createBomAssocMultiDuplicateRollsBackEveryRow() {
        String base = "http://localhost:" + port;
        Timestamp fromDate = Timestamp.from(Instant.now());
        Map<String, Object> row = Map.of(
                "productId", "MFG_FG", "productIdTo", "MFG_RAW1",
                "productAssocTypeId", "MANUF_COMPONENT", "fromDate", fromDate);
        ResponseEntity<String> response = post(base + "/api/manufacturing/services/createBOMAssoc/multi",
                List.of(row, row));
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNull(products.findProductAssoc(Map.of(
                "productId", "MFG_FG", "productIdTo", "MFG_RAW1",
                "productAssocTypeId", "MANUF_COMPONENT", "fromDate", fromDate)));
    }

    private ResponseEntity<String> post(String url, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return rest.withBasicAuth("mfgadmin", password()).exchange(
                url, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
    }

    private static String password() {
        return System.getenv("MANUFACTURING_DEMO_PASSWORD");
    }
}
