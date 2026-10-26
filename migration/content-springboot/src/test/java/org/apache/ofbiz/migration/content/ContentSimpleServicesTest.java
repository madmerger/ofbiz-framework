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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/** simple（minilang）サービス 7 本の移植テスト。 */
@SpringBootTest(classes = ContentMigrationApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ContentSimpleServicesTest extends ContentApiTestSupport {

    @Test
    void ac30GenericDataResourcePermission() throws Exception {
        call("editor", "genericDataResourcePermission", Map.of("mainAction", "CREATE"))
                .andExpect(jsonPath("$.hasPermission").value(true));
        call("editor", "genericDataResourcePermission", Map.of("mainAction", "CREATE", "dataTemplateTypeId", "FTL"))
                .andExpect(jsonPath("$.hasPermission").value(false));
        call("admin", "genericDataResourcePermission", Map.of("mainAction", "CREATE", "dataTemplateTypeId", "FTL"))
                .andExpect(jsonPath("$.hasPermission").value(true));
        call("readonly", "genericDataResourcePermission", Map.of("mainAction", "UPDATE"))
                .andExpect(jsonPath("$.hasPermission").value(false));
    }

    @Test
    void ac31CreateAndUpdateSurveyQuestion() throws Exception {
        String surveyId = body(call("editor", "createSurvey", Map.of("surveyName", "AC31")).andExpect(status().isOk()))
                .get("surveyId").asText();
        call("editor", "createSurveyQuestion", Map.of("surveyQuestionTypeId", "ENUMERATION", "question", "Q"))
                .andExpect(status().isBadRequest());
        call("editor", "createSurveyQuestion", Map.of("surveyQuestionTypeId", "GEO", "question", "Q"))
                .andExpect(status().isBadRequest());
        String questionId = body(call("editor", "createSurveyQuestion", Map.of(
                "surveyQuestionTypeId", "TEXT_SHORT", "question", "お名前", "surveyId", surveyId))
                .andExpect(status().isOk())).get("surveyQuestionId").asText();
        entities("readonly", "SurveyQuestionAppl", Map.of("surveyId", surveyId))
                .andExpect(jsonPath("$[0].surveyQuestionId").value(questionId));
        call("editor", "updateSurveyQuestion", Map.of("surveyQuestionId", questionId, "question", "氏名"))
                .andExpect(status().isOk());
        entities("readonly", "SurveyQuestion", Map.of("surveyQuestionId", questionId))
                .andExpect(jsonPath("$[0].question").value("氏名"));
        call("editor", "updateSurveyQuestion", Map.of("surveyQuestionId", questionId, "surveyQuestionTypeId", "ENUMERATION"))
                .andExpect(status().isBadRequest());
        call("readonly", "createSurveyQuestion", Map.of("surveyQuestionTypeId", "TEXT_SHORT"))
                .andExpect(status().isForbidden());
    }

    @Test
    void ac32CreateSurveyResponseValidatesAndStoresAnswers() throws Exception {
        String surveyId = body(call("editor", "createSurvey", Map.of("surveyName", "AC32", "isAnonymous", "N",
                "allowMultiple", "N", "allowUpdate", "N")).andExpect(status().isOk())).get("surveyId").asText();
        String emailQ = body(call("editor", "createSurveyQuestion", Map.of(
                "surveyQuestionTypeId", "EMAIL", "question", "メール", "surveyId", surveyId))).get("surveyQuestionId").asText();
        String numberQ = body(call("editor", "createSurveyQuestion", Map.of(
                "surveyQuestionTypeId", "NUMBER_LONG", "question", "人数", "surveyId", surveyId))).get("surveyQuestionId").asText();

        call("editor", "createSurveyResponse", Map.of("surveyId", surveyId, "answers", Map.of(emailQ, "a@example.com")))
                .andExpect(status().isBadRequest());
        call("editor", "createSurveyResponse", Map.of("surveyId", surveyId, "partyId", "AC32_PARTY",
                "answers", Map.of(emailQ, "not-an-email", numberQ, "3")))
                .andExpect(status().isBadRequest());
        String responseId = body(call("editor", "createSurveyResponse", Map.of("surveyId", surveyId, "partyId", "AC32_PARTY",
                "answers", Map.of(emailQ, "a@example.com", numberQ, "3")))
                .andExpect(status().isOk())).get("surveyResponseId").asText();
        JsonNode answers = body(entities("admin", "SurveyResponseAnswer", Map.of("surveyResponseId", responseId)));
        assertEquals(2, answers.size());
        call("editor", "createSurveyResponse", Map.of("surveyId", surveyId, "partyId", "AC32_PARTY",
                "answers", Map.of(emailQ, "b@example.com", numberQ, "4")))
                .andExpect(status().isConflict());
    }

    @Test
    void ac33WebSiteServices() throws Exception {
        call("admin", "createWebSite", Map.of("webSiteId", "AC33_SITE", "siteName", "AC33 サイト")).andExpect(status().isOk());
        call("admin", "createWebSiteContentType", Map.of("webSiteContentTypeId", "AC33_TYPE", "description", "トップ"))
                .andExpect(status().isOk());
        call("editor", "createWebSitePathAlias", Map.of("webSiteId", "AC33_SITE", "pathAlias", "/top",
                "fromDate", "2026-01-01 00:00:00", "aliasTo", "/control/main")).andExpect(status().isOk());
        call("readonly", "getWebSitePathAlias", Map.of("webSiteId", "AC33_SITE", "pathAlias", "/top",
                "fromDate", "2026-01-01 00:00:00"))
                .andExpect(jsonPath("$.pathTo").value("/control/main"));

        JsonNode created = body(call("editor", "autoCreateWebSiteContent", Map.of("webSiteId", "AC33_SITE",
                "webSiteContentTypeId", List.of("AC33_TYPE"))).andExpect(status().isOk()));
        String contentId = created.get("contentIds").get(0).asText();
        entities("readonly", "Content", Map.of("contentId", contentId))
                .andExpect(jsonPath("$[0].contentName").value("AC33 サイト トップ"))
                .andExpect(jsonPath("$[0].contentTypeId").value("WEB_SITE_PUB_PT"));
        entities("readonly", "WebSiteContent", Map.of("webSiteId", "AC33_SITE"))
                .andExpect(jsonPath("$[0].contentId").value(contentId));

        call("editor", "generateMissingSeoUrlForWebsite", Map.of("webSiteId", "AC33_SITE", "prodCatalogId", "AC33_CATALOG",
                "typeGenerate", List.of("category", "product", "content")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successMessageList[0]").value("Generate missing seo url's successfully"))
                .andExpect(jsonPath("$.successMessageList[1]").value(
                        "Categories already having seo url's: 0, Categories with url added: 0"));
    }

    @Test
    void ac34LuhnValidationForCreditCardAnswers() {
        assertEquals(true, ContentSimpleServices.isCreditCard("4111 1111 1111 1111"));
        assertEquals(false, ContentSimpleServices.isCreditCard("4111 1111 1111 1112"));
    }
}
