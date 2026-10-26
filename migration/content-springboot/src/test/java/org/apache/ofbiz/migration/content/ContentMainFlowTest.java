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
package org.apache.ofbiz.migration.content;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/** 主要フロー: DataResource/ElectronicText → Content → ContentAssoc → 取得（getContent 相当）。 */
@SpringBootTest(classes = ContentMigrationApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ContentMainFlowTest extends ContentApiTestSupport {

    @Test
    void ac20DataResourceContentAssocAndRetrieval() throws Exception {
        String dataResourceId = body(call("editor", "createDataResourceAndText", Map.of(
                "dataResourceTypeId", "ELECTRONIC_TEXT", "dataResourceName", "AC20 本文", "textData", "こんにちは")))
                .get("dataResourceId").asText();
        JsonNode child = body(call("editor", "createContent", Map.of("contentName", "AC20 子", "dataResourceId", dataResourceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contentTypeId").value("DOCUMENT"))
                .andExpect(jsonPath("$.statusId").value("CTNT_IN_PROGRESS")));
        String childId = child.get("contentId").asText();
        String parentId = body(call("editor", "createContent", Map.of("contentName", "AC20 親"))).get("contentId").asText();
        call("editor", "createContentAssoc", Map.of("contentId", parentId, "contentIdTo", childId,
                "contentAssocTypeId", "SUB_CONTENT", "fromDate", "2026-01-01 00:00:00"))
                .andExpect(status().isOk());

        call("readonly", "getContent", Map.of("contentId", childId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.view.contentName").value("AC20 子"))
                .andExpect(jsonPath("$.view.drDataResourceTypeId").value("ELECTRONIC_TEXT"));
        call("readonly", "getContentAndDataResource", Map.of("contentId", childId))
                .andExpect(jsonPath("$.resultData.electronicText.textData").value("こんにちは"));
        entities("readonly", "ContentAssoc", Map.of("contentId", parentId))
                .andExpect(jsonPath("$[0].contentIdTo").value(childId));

        call("editor", "updateDataResourceAndText", Map.of("dataResourceId", dataResourceId,
                "dataResourceTypeId", "ELECTRONIC_TEXT", "textData", "更新後"))
                .andExpect(status().isOk());
        call("readonly", "getElectronicText", Map.of("contentId", childId))
                .andExpect(jsonPath("$.textData").value("更新後"));

        call("editor", "deactivateContentAssoc", Map.of("contentId", parentId, "contentIdTo", childId,
                "contentAssocTypeId", "SUB_CONTENT", "fromDate", "2026-01-01 00:00:00"))
                .andExpect(status().isOk());
        JsonNode assoc = body(entities("readonly", "ContentAssoc", Map.of("contentId", parentId)));
        assertFalse(assoc.get(0).get("thruDate").isNull());
    }

    @Test
    void ac21GroupAndRouteServices() throws Exception {
        JsonNode created = body(call("editor", "createTextContent", Map.of("contentName", "AC21", "textData", "group text"))
                .andExpect(status().isOk()));
        String contentId = created.get("contentId").asText();
        String dataResourceId = created.get("dataResourceId").asText();
        call("readonly", "getDataResource", Map.of("dataResourceId", dataResourceId))
                .andExpect(jsonPath("$.resultData.dataResource.dataResourceTypeId").value("ELECTRONIC_TEXT"))
                .andExpect(jsonPath("$.resultData.electronicText.textData").value("group text"));
        call("editor", "updateTextContent", Map.of("contentId", contentId, "contentName", "AC21 更新",
                "dataResourceTypeId", "ELECTRONIC_TEXT", "textData", "group updated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contentName").value("AC21 更新"));
        call("readonly", "getElectronicText", Map.of("contentId", contentId))
                .andExpect(jsonPath("$.textData").value("group updated"));

        JsonNode shortText = body(call("editor", "createDataText", Map.of("dataResourceName", "AC21 short")));
        assertEquals("SHORT_TEXT", shortText.get("dataResourceTypeId").asText());
        call("editor", "updateDataText", Map.of("dataResourceId", shortText.get("dataResourceId").asText(),
                "dataResourceName", "AC21 short updated"))
                .andExpect(status().isOk());
    }

    @Test
    void ac22ContentFromDataResourceAndSimpleText() throws Exception {
        call("editor", "createContentFromDataResource", Map.of("dataResourceId", "AC22_ABSENT"))
                .andExpect(status().isNotFound());
        String dataResourceId = body(call("editor", "createDataResource", Map.of(
                "dataResourceTypeId", "SHORT_TEXT", "dataResourceName", "AC22 名前", "partyId", "AC22_PARTY")))
                .get("dataResourceId").asText();
        entities("readonly", "DataResourceRole", Map.of("dataResourceId", dataResourceId))
                .andExpect(jsonPath("$[0].roleTypeId").value("OWNER"));
        String contentId = body(call("editor", "createContentFromDataResource", Map.of("dataResourceId", dataResourceId)))
                .get("contentId").asText();
        entities("readonly", "Content", Map.of("contentId", contentId))
                .andExpect(jsonPath("$[0].contentName").value("AC22 名前"));

        call("editor", "createSimpleTextContent", Map.of("text", "FTL"))
                .andExpect(status().isForbidden());
        JsonNode simple = body(call("admin", "createSimpleTextContent", Map.of("text", "${hello}", "contentName", "AC22 FTL"))
                .andExpect(status().isOk()));
        call("admin", "updateSimpleTextContent", Map.of("textDataResourceId", simple.get("dataResourceId").asText(),
                "text", "${bye}"))
                .andExpect(jsonPath("$.textData").value("${bye}"));
        call("readonly", "getElectronicText", Map.of("contentId", simple.get("contentId").asText()))
                .andExpect(jsonPath("$.textData").value("${bye}"));
    }

    @Test
    void ac23TemplateDataResourceRequiresSuperPermission() throws Exception {
        call("editor", "createDataResource", Map.of("dataResourceTypeId", "ELECTRONIC_TEXT", "dataTemplateTypeId", "FTL"))
                .andExpect(status().isForbidden());
        call("admin", "createDataResource", Map.of("dataResourceTypeId", "ELECTRONIC_TEXT", "dataTemplateTypeId", "FTL"))
                .andExpect(status().isOk());
        JsonNode missing = body(call("readonly", "getContent", Map.of("contentId", "AC23_ABSENT")));
        assertTrue(missing.get("view").isNull());
    }
}
