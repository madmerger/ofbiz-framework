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
package org.apache.ofbiz.migration.content;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import org.apache.ofbiz.migration.common.EcaCatalog;
import org.apache.ofbiz.migration.common.EcaEngine;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
class ContentElectronicTextEcaTest {
    @Autowired private MigrationServiceDispatcher dispatcher;
    @Autowired private ContentEntityStore store;
    @Autowired private EcaCatalog catalog;
    @Autowired private EcaEngine engine;

    @Test
    @Transactional
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "CONTENTMGR_ADMIN"})
    void dispatcherCreatesElectronicTextWithoutIdThroughOriginalResourceCreationRule() {
        String ruleId = catalog.rules("service", "createElectronicText", "invoke").get(0).path("id").asText();
        long executions = engine.counters().getOrDefault(ruleId + ":executed", 0L);
        Map<String, Object> response = dispatcher.execute("content", "createElectronicText",
                Map.of("textData", "Real invoke ECA body"));
        assertEquals("success", response.get("responseMessage"));
        String id = (String) response.get("dataResourceId");
        assertNotNull(id);
        Map<String, Object> key = Map.of("dataResourceId", id);
        assertEquals("ELECTRONIC_TEXT", store.findOneMap("DataResource", key).get("dataResourceTypeId"));
        assertEquals("Real invoke ECA body", store.findOneMap("ElectronicText", key).get("textData"));
        assertEquals(executions + 1, engine.counters().get(ruleId + ":executed"));
    }
}
