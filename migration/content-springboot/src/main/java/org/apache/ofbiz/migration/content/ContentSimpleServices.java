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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** content の simple（minilang）サービス 7 本の Java 移植。 */
@Component
public final class ContentSimpleServices {
    private static final String C = ContentServiceCatalog.COMPONENT;
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern URL = Pattern.compile("^[a-zA-Z][a-zA-Z0-9+.-]*://\\S+$");
    private final ContentEntityStore store;
    private final ContentPermissionService permissions;
    private final FinAccountPort finAccountPort;
    private final CatalogSeoPort catalogSeoPort;
    private final ContentFlowServices flowServices;

    public ContentSimpleServices(ServiceHandlerRegistry registry, ContentEntityStore store, ContentPermissionService permissions,
            FinAccountPort finAccountPort, CatalogSeoPort catalogSeoPort, ContentFlowServices flowServices) {
        this.store = store;
        this.permissions = permissions;
        this.finAccountPort = finAccountPort;
        this.catalogSeoPort = catalogSeoPort;
        this.flowServices = flowServices;
        registry.registerHandler(C, "genericDataResourcePermission", "VIEW", this::genericDataResourcePermission);
        registry.registerHandler(C, "createSurveyQuestion", "CREATE", this::createSurveyQuestion);
        registry.registerHandler(C, "updateSurveyQuestion", "UPDATE", this::updateSurveyQuestion);
        registry.registerHandler(C, "createSurveyResponse", "CREATE", this::createSurveyResponse);
        registry.registerHandler(C, "getWebSitePathAlias", "VIEW", this::getWebSitePathAlias);
        registry.registerHandler(C, "autoCreateWebSiteContent", "CREATE", this::autoCreateWebSiteContent);
        registry.registerHandler(C, "generateMissingSeoUrlForWebsite", "UPDATE", this::generateMissingSeoUrlForWebsite);
    }

    /** DataResourcePermissionServices.xml#genericDataResourcePermission。 */
    public Map<String, Object> genericDataResourcePermission(Map<String, Object> input) {
        boolean allowed = permissions.hasDataResourcePermission(
                (String) input.get("mainAction"), (String) input.get("dataResourceId"), (String) input.get("dataTemplateTypeId"));
        return result("hasPermission", allowed);
    }

    /** SurveyServices.xml#createSurveyQuestion（+ SECA createSurveyQuestionAppl）。 */
    public Map<String, Object> createSurveyQuestion(Map<String, Object> input) {
        validateQuestionType(input);
        Map<String, Object> values = new LinkedHashMap<>(input);
        values.put("surveyQuestionId", store.nextId("SurveyQuestion"));
        store.create("SurveyQuestion", values);
        if (!ContentEntityStore.isEmpty(input.get("surveyId"))) {
            Map<String, Object> appl = new LinkedHashMap<>(input);
            appl.put("surveyQuestionId", values.get("surveyQuestionId"));
            appl.putIfAbsent("fromDate", ContentEntityStore.now());
            store.create("SurveyQuestionAppl", appl);
        }
        return result("surveyQuestionId", values.get("surveyQuestionId"));
    }

    /** SurveyServices.xml#updateSurveyQuestion。 */
    public Map<String, Object> updateSurveyQuestion(Map<String, Object> input) {
        validateQuestionType(input);
        Object question = store.requireOne("SurveyQuestion", Map.of("surveyQuestionId", required(input, "surveyQuestionId")),
                "ServiceValueNotFound");
        return store.store(question, input);
    }

    /** SurveyServices.xml#createSurveyResponse（validate/process インライン含む）。 */
    @SuppressWarnings("unchecked")
    public Map<String, Object> createSurveyResponse(Map<String, Object> input) {
        String surveyId = required(input, "surveyId");
        Object survey = store.requireOne("Survey", Map.of("surveyId", surveyId), "ContentNoSurveyFound");
        List<Map<String, Object>> questions = appliedQuestions(surveyId);
        if (questions.isEmpty()) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "ContentNoQuestionsSurveyFound");
        }
        String partyId = (String) input.get("partyId");
        if (!"Y".equals(store.get(survey, "isAnonymous")) && ContentEntityStore.isEmpty(partyId)) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "ContentSurveyAnonymousResponse");
        }
        if (!"Y".equals(store.get(survey, "allowMultiple")) && !ContentEntityStore.isEmpty(partyId)
                && !store.findList("SurveyResponse", Map.of("partyId", partyId, "surveyId", surveyId)).isEmpty()
                && !"Y".equals(store.get(survey, "allowUpdate"))) {
            throw new MigrationException(HttpStatus.CONFLICT, "ContentSurveyAlreadyResponded");
        }
        Object rawAnswers = input.get("answers");
        if (!(rawAnswers instanceof Map<?, ?> answersMap) || answersMap.isEmpty()) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "ContentSurveyAnswersNotFound");
        }
        Map<String, Object> answers = (Map<String, Object>) answersMap;
        List<String> errors = new ArrayList<>();
        for (Map<String, Object> question : questions) {
            for (String[] field : answerFields(question)) {
                validateAnswer(question, answers.get(field[0]), errors);
            }
        }
        if (!errors.isEmpty()) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, String.join("; ", errors));
        }
        Object response = null;
        if ("Y".equals(store.get(survey, "allowUpdate")) && !ContentEntityStore.isEmpty(input.get("surveyResponseId"))) {
            response = store.findOne("SurveyResponse", Map.of("surveyResponseId", input.get("surveyResponseId")));
        }
        if (response == null) {
            Map<String, Object> values = new LinkedHashMap<>(input);
            values.put("surveyResponseId", store.nextId("SurveyResponse"));
            store.create("SurveyResponse", values);
            response = store.findOne("SurveyResponse", Map.of("surveyResponseId", values.get("surveyResponseId")));
        }
        Map<String, Object> stamp = new LinkedHashMap<>();
        if (store.get(response, "responseDate") == null) {
            stamp.put("responseDate", ContentEntityStore.now());
        }
        stamp.put("lastModifiedDate", ContentEntityStore.now());
        store.store(response, stamp);
        String surveyResponseId = (String) store.get(response, "surveyResponseId");
        if (!ContentEntityStore.isEmpty(input.get("dataResourceId"))) {
            Object dataResource = store.requireOne("DataResource", Map.of("dataResourceId", input.get("dataResourceId")),
                    "ContentDataResourceNotFound");
            store.store(dataResource, Map.of("relatedDetailId", surveyResponseId));
        }
        for (Map<String, Object> question : questions) {
            for (String[] field : answerFields(question)) {
                processAnswer(surveyResponseId, question, field, answers.get(field[0]));
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("surveyResponseId", surveyResponseId);
        result.put("productStoreSurveyId", input.get("productStoreSurveyId"));
        result.put("surveyId", surveyId);
        return result;
    }

    /** WebSiteServices.xml#getWebSitePathAlias。 */
    public Map<String, Object> getWebSitePathAlias(Map<String, Object> input) {
        Object alias = store.findOne("WebSitePathAlias", Map.of("webSiteId", required(input, "webSiteId"),
                "pathAlias", required(input, "pathAlias"), "fromDate", required(input, "fromDate")));
        // minilang は alias.pathTo を返すが Entity のフィールドは aliasTo（OFBiz では常に null になる）
        return result("pathTo", alias == null ? null : store.get(alias, "aliasTo"));
    }

    /** WebSiteServices.xml#quickCreateWebSiteContent（サービス名 autoCreateWebSiteContent）。 */
    public Map<String, Object> autoCreateWebSiteContent(Map<String, Object> input) {
        String webSiteId = required(input, "webSiteId");
        Object webSite = store.requireOne("WebSite", Map.of("webSiteId", webSiteId), "ServiceValueNotFound");
        Object types = input.get("webSiteContentTypeId");
        List<String> typeIds = types instanceof List<?> list
                ? list.stream().map(String::valueOf).toList() : List.of(String.valueOf(required(input, "webSiteContentTypeId")));
        List<String> contentIds = new ArrayList<>();
        for (String typeId : typeIds) {
            Object type = store.findOne("WebSiteContentType", Map.of("webSiteContentTypeId", typeId));
            Map<String, Object> content = new LinkedHashMap<>();
            content.put("contentName", store.get(webSite, "siteName") + " " + (type == null ? "" : store.get(type, "description")));
            content.put("contentTypeId", "WEB_SITE_PUB_PT");
            String contentId = (String) flowServices.createContent(content).get("contentId");
            store.create("WebSiteContent", Map.of("webSiteId", webSiteId, "contentId", contentId,
                    "webSiteContentTypeId", typeId, "fromDate", ContentEntityStore.now()));
            contentIds.add(contentId);
        }
        return result("contentIds", contentIds);
    }

    /** WebSiteServices.xml#generateMissingSeoUrlForWebsite（product 依存は CatalogSeoPort 経由）。 */
    public Map<String, Object> generateMissingSeoUrlForWebsite(Map<String, Object> input) {
        String webSiteId = required(input, "webSiteId");
        String prodCatalogId = required(input, "prodCatalogId");
        Object rawTypes = input.get("typeGenerate");
        if (!(rawTypes instanceof List<?> rawList) || rawList.isEmpty()) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Missing required input: typeGenerate");
        }
        List<String> typeGenerate = rawList.stream().map(String::valueOf).toList();
        Object webSite = store.requireOne("WebSite", Map.of("webSiteId", webSiteId), "ServiceValueNotFound");
        List<String> catalogs = "all".equals(prodCatalogId)
                ? catalogSeoPort.productStoreCatalogIds((String) store.get(webSite, "productStoreId")) : List.of(prodCatalogId);
        int[] totals = new int[6];
        if ("all".equals(prodCatalogId) && catalogs.isEmpty()) {
            if (typeGenerate.contains("content")) {
                add(totals, catalogSeoPort.createMissingContentAltUrls(webSiteId, null));
            }
        }
        for (String catalogId : catalogs) {
            for (String type : typeGenerate) {
                if ("category".equals(type)) {
                    add(totals, catalogSeoPort.createMissingCategoryAndProductAltUrls(catalogId, true, false));
                } else if ("product".equals(type)) {
                    add(totals, catalogSeoPort.createMissingCategoryAndProductAltUrls(catalogId, false, true));
                } else if ("content".equals(type)) {
                    add(totals, catalogSeoPort.createMissingContentAltUrls(webSiteId, catalogId));
                }
            }
        }
        List<String> messages = new ArrayList<>();
        messages.add("Generate missing seo url's successfully");
        for (String type : typeGenerate) {
            if ("category".equals(type)) {
                messages.add("Categories already having seo url's: " + totals[0] + ", Categories with url added: " + totals[1]);
            } else if ("product".equals(type)) {
                messages.add("Products already having seo url's: " + totals[2] + ", Products with url added: " + totals[3]);
            } else if ("content".equals(type)) {
                messages.add("Contents already having seo url's: " + totals[4] + ", Contents with url added: " + totals[5]);
            }
        }
        return result("successMessageList", messages);
    }

    private static void add(int[] totals, CatalogSeoPort.SeoResult result) {
        totals[0] += result.categoriesNotUpdated();
        totals[1] += result.categoriesUpdated();
        totals[2] += result.productsNotUpdated();
        totals[3] += result.productsUpdated();
        totals[4] += result.contentsNotUpdated();
        totals[5] += result.contentsUpdated();
    }

    private void validateQuestionType(Map<String, Object> input) {
        Object type = input.get("surveyQuestionTypeId");
        if ("ENUMERATION".equals(type) && ContentEntityStore.isEmpty(input.get("enumTypeId"))) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "ContentQuestionTypeRequiredField: enumTypeId");
        }
        if ("GEO".equals(type) && ContentEntityStore.isEmpty(input.get("geoId"))) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "ContentQuestionTypeRequiredField: geoId");
        }
    }

    /** SurveyQuestionAndAppl（view-entity）相当を SurveyQuestionAppl + SurveyQuestion から組み立てる。 */
    private List<Map<String, Object>> appliedQuestions(String surveyId) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Object appl : store.findList("SurveyQuestionAppl", Map.of("surveyId", surveyId))) {
            Map<String, Object> row = new LinkedHashMap<>(store.toMap(appl));
            Map<String, Object> question = store.findOneMap("SurveyQuestion", Map.of("surveyQuestionId", row.get("surveyQuestionId")));
            if (question != null) {
                row.put("surveyQuestionTypeId", question.get("surveyQuestionTypeId"));
            }
            rows.add(row);
        }
        rows.sort(Comparator.comparing(row -> row.get("sequenceNum") == null ? Long.MAX_VALUE
                : Long.parseLong(row.get("sequenceNum").toString())));
        return rows;
    }

    /** 回答フィールド名（[answers のキー, surveyMultiRespId, surveyMultiRespColId]）。 */
    private List<String[]> answerFields(Map<String, Object> question) {
        String questionId = (String) question.get("surveyQuestionId");
        String multiRespId = (String) question.get("surveyMultiRespId");
        if (ContentEntityStore.isEmpty(multiRespId) || store.findOne("SurveyMultiResp",
                Map.of("surveyId", question.get("surveyId"), "surveyMultiRespId", multiRespId)) == null) {
            return List.<String[]>of(new String[] {questionId, null, "_NA_"});
        }
        String colId = (String) question.get("surveyMultiRespColId");
        if (!ContentEntityStore.isEmpty(colId)) {
            return List.<String[]>of(new String[] {questionId + "_" + colId, multiRespId, colId});
        }
        return store.findList("SurveyMultiRespColumn", Map.of("surveyId", question.get("surveyId"), "surveyMultiRespId", multiRespId))
                .stream().map(column -> new String[] {questionId + "_" + store.get(column, "surveyMultiRespColId"), multiRespId,
                        (String) store.get(column, "surveyMultiRespColId")}).toList();
    }

    private void validateAnswer(Map<String, Object> question, Object answer, List<String> errors) {
        String type = (String) question.get("surveyQuestionTypeId");
        if ("Y".equals(question.get("requiredField")) && ContentEntityStore.isEmpty(answer)) {
            errors.add("ContentSurveyQuestionRequiresAResponse");
        }
        if (ContentEntityStore.isEmpty(answer) || type == null) {
            return;
        }
        String text = answer.toString();
        boolean valid = switch (type) {
        case "CREDIT_CARD" -> isCreditCard(text);
        case "GIFT_CARD" -> finAccountPort.isActiveGiftCertificateCode(text) || isGiftCard(text);
        case "DATE" -> isDate(text);
        case "EMAIL" -> EMAIL.matcher(text).matches();
        case "NUMBER_CURRENCY", "NUMBER_FLOAT" -> isDouble(text);
        case "NUMBER_LONG" -> text.matches("[+-]?\\d+");
        case "URL" -> URL.matcher(text).matches();
        default -> true;
        };
        if (!valid) {
            errors.add("ContentSurveyQuestionRequiresAValid" + type);
        }
    }

    private void processAnswer(String surveyResponseId, Map<String, Object> question, String[] field, Object answer) {
        Map<String, Object> key = Map.of("surveyResponseId", surveyResponseId,
                "surveyQuestionId", question.get("surveyQuestionId"), "surveyMultiRespColId", field[2]);
        Object existing = store.findOne("SurveyResponseAnswer", key);
        if (existing == null) {
            Map<String, Object> values = new LinkedHashMap<>(key);
            if (field[1] != null) {
                values.put("surveyMultiRespId", field[1]);
            }
            store.create("SurveyResponseAnswer", values);
            existing = store.findOne("SurveyResponseAnswer", key);
        }
        if (ContentEntityStore.isEmpty(answer)) {
            return;
        }
        Map<String, Object> values = new LinkedHashMap<>();
        String text = answer.toString();
        switch (String.valueOf(question.get("surveyQuestionTypeId"))) {
        case "BOOLEAN" -> values.put("booleanResponse", text);
        case "NUMBER_CURRENCY" -> values.put("currencyResponse", new BigDecimal(text));
        case "NUMBER_FLOAT" -> values.put("floatResponse", Double.valueOf(text));
        case "NUMBER_LONG" -> values.put("numericResponse", Long.valueOf(text));
        case "OPTION" -> values.put("surveyOptionSeqId", text);
        case "CONTENT" -> values.put("contentId", text);
        default -> values.put("textResponse", text);
        }
        if (store.get(existing, "sequenceNum") == null && question.get("sequenceNum") != null) {
            values.put("sequenceNum", question.get("sequenceNum"));
        }
        values.put("answeredDate", ContentEntityStore.now());
        store.store(existing, values);
    }

    static boolean isCreditCard(String text) {
        String digits = text.replaceAll("[\\s-]", "");
        if (!digits.matches("\\d{13,19}")) {
            return false;
        }
        int sum = 0;
        for (int i = 0; i < digits.length(); i++) {
            int digit = digits.charAt(digits.length() - 1 - i) - '0';
            if (i % 2 == 1) {
                digit = digit * 2 > 9 ? digit * 2 - 9 : digit * 2;
            }
            sum += digit;
        }
        return sum % 10 == 0;
    }

    private static boolean isGiftCard(String text) {
        return text.replaceAll("\\s", "").matches("\\d{16,19}") && isCreditCard(text);
    }

    private static boolean isDate(String text) {
        try {
            LocalDate.parse(text.length() > 10 ? text.substring(0, 10) : text);
            return true;
        } catch (RuntimeException exception) {
            return false;
        }
    }

    private static boolean isDouble(String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    static String required(Map<String, ?> input, String name) {
        Object value = input.get(name);
        if (ContentEntityStore.isEmpty(value)) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Missing required input: " + name);
        }
        return value.toString();
    }

    static Map<String, Object> result(String key, Object value) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(key, value);
        return result;
    }
}
