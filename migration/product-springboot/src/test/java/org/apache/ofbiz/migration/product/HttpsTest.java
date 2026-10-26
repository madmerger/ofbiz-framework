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
package org.apache.ofbiz.migration.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.apache.ofbiz.migration.common.MigrationSecurityProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(
        classes = ProductMigrationApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HttpsTest {
    @LocalServerPort
    private int port;

    @Autowired
    private MigrationSecurityProperties securityProperties;

    @Test
    void ac01_defaultProfileRequiresHttps() {
        assertTrue(securityProperties.isRequireHttps());
    }

    @Test
    void ac02_httpRequestsAreRedirectedToHttps() throws Exception {
        HttpResponse<String> response = client().send(request().build(), HttpResponse.BodyHandlers.ofString());
        assertSecureRedirect(response);
    }

    @Test
    void ac03_forwardedProtoFromUntrustedLoopbackDoesNotBypassHttps() throws Exception {
        assertEquals("a^", securityProperties.getTrustedProxies());
        HttpResponse<String> response = client().send(
                request().header("X-Forwarded-Proto", "https").build(),
                HttpResponse.BodyHandlers.ofString());
        assertSecureRedirect(response);
    }

    private HttpClient client() {
        return HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER).build();
    }

    private HttpRequest.Builder request() {
        return HttpRequest.newBuilder(URI.create(
                "http://127.0.0.1:" + port + "/api/product/entities/ProductType")).GET();
    }

    private static void assertSecureRedirect(HttpResponse<String> response) {
        assertEquals(302, response.statusCode());
        URI location = URI.create(response.headers().firstValue("location").orElseThrow());
        assertEquals("/api/product/entities/ProductType", location.getPath());
        assertTrue(location.getHost().equals("127.0.0.1"), location.toString());
    }
}
