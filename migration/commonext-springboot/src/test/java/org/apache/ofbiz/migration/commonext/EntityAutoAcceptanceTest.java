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
package org.apache.ofbiz.migration.commonext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.apache.ofbiz.migration.commonext.generated.ServiceCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;

@SpringBootTest
@ActiveProfiles("test")
class EntityAutoAcceptanceTest {
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final List<String> GROOVY_SERVICES = List.of(
            "createSystemInfoNote",
            "deleteSystemInfoNote",
            "deleteAllSystemNotes",
            "getSystemInfoNotes",
            "getLastSystemInfoNote",
            "getSystemInfoStatus");

    @Autowired
    private ServiceHandlerRegistry registry;
    @Autowired
    private MigrationServiceDispatcher dispatcher;

    @Test
    void ac01entityAutoCountMatchesSourceReportAndRegisteredCatalog() throws Exception {
        Path root = Path.of(System.getProperty("commonext.ofbiz.root"));
        Document services = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(root.resolve("applications/commonext/servicedef/services.xml").toFile());
        int entityAutoServices = 0;
        var serviceNodes = services.getElementsByTagName("service");
        for (int index = 0; index < serviceNodes.getLength(); index++) {
            if ("entity-auto".equals(serviceNodes.item(index).getAttributes().getNamedItem("engine").getNodeValue())) {
                entityAutoServices++;
            }
        }
        JsonNode report = JSON.readTree(Path.of(System.getProperty("commonext.report")).toFile());
        int reportedCount = report.path("entity_auto_service_count").asInt(-1);
        long registeredCount = ServiceCatalog.all().keySet().stream()
                .filter(name -> registry.find("commonext", name).isPresent())
                .count();

        assertEquals(entityAutoServices, reportedCount);
        assertEquals(entityAutoServices, registeredCount);
        assertEquals(0, entityAutoServices);
    }

    @Test
    void ac02allGroovyServicesHaveRegisteredHandlers() {
        for (String serviceName : GROOVY_SERVICES) {
            assertTrue(registry.handler("commonext", serviceName).isPresent(), serviceName);
        }
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"SETUP_ADMIN"})
    void ac03registeredEntityAutoCatalogCanBeExercisedGenerically() {
        List<ServiceHandlerRegistry.ServiceDefinition> entityAutoServices = new ArrayList<>();
        for (ServiceCatalog.ServiceDefinition generated : ServiceCatalog.all().values()) {
            if (generated.invoke().equalsIgnoreCase("entity-auto")) {
                entityAutoServices.add(registry.find("commonext", generated.name()).orElseThrow());
            }
        }
        for (ServiceHandlerRegistry.ServiceDefinition definition : entityAutoServices) {
            assertNotNull(dispatcher.execute("commonext", definition.name(), java.util.Map.of()));
        }
    }
}
