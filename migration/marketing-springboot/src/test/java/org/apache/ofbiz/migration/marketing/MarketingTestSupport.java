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
package org.apache.ofbiz.migration.marketing;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

final class MarketingTestSupport {

    static final String TEST_PROPERTIES = "spring.profiles.active=test";
    private static final ObjectMapper JSON = new ObjectMapper();

    private MarketingTestSupport() {
    }

    static JsonNode inventory() throws IOException {
        return JSON.readTree(Path.of(System.getProperty("ofbiz.root"),
                "docs/migration/marketing/inventory/inventory.json").toFile());
    }

    static void loginAs(String user, String... authorities) {
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(user, "n/a", authorities));
    }

    static void loginAdmin() {
        loginAs("admin", "OFBTOOLS_VIEW", "MARKETING_VIEW", "MARKETING_ADMIN");
    }

    static Map<String, Object> run(MigrationServiceDispatcher dispatcher, String service, Map<String, ?> input) {
        return dispatcher.execute(MarketingCatalogRegistrar.COMPONENT, service, input);
    }
}
