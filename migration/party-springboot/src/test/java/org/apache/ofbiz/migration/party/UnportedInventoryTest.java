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
package org.apache.ofbiz.migration.party;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.apache.ofbiz.migration.party.generated.ServiceCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@SpringBootTest(classes = {PartyMigrationApplication.class, TestUsersConfiguration.class})
@ActiveProfiles("test")
class UnportedInventoryTest {
    @Autowired
    private ServiceHandlerRegistry registry;

    @Test
    void ac47_everyServiceIsCatalogedHandledOrDocumentedUnported() throws Exception {
        Path root = Path.of(System.getProperty("ofbiz.root"));
        String unported = Files.readString(root.resolve("docs/migration/party/unported.md"));
        File[] definitions = root.resolve("applications/party/servicedef").toFile()
                .listFiles((directory, name) -> name.endsWith(".xml"));
        assertTrue(definitions != null);
        Set<String> names = new HashSet<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        for (File file : definitions) {
            NodeList services = factory.newDocumentBuilder().parse(file).getElementsByTagName("service");
            for (int index = 0; index < services.getLength(); index++) {
                names.add(((Element) services.item(index)).getAttribute("name"));
            }
        }
        for (String name : names) {
            if (ServiceCatalog.find(name).isPresent()) {
                assertTrue(registry.find("party", name).isPresent(), name);
            } else if (registry.handler("party", name).isEmpty()) {
                assertTrue(unported.contains("| " + name + " |"), name);
            }
        }
    }
}
