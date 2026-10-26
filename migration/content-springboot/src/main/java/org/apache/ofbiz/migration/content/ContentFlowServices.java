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

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityAutoService;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * 主要フロー（テキスト系 DataResource/ElectronicText の作成・更新、Content 作成、ContentAssoc、取得）の Java/Groovy 移植。
 * group/route サービス（createTextContent など）と SECA（createDataText など）の連鎖もここで再現する。
 */
@Component
public final class ContentFlowServices {
    private static final String C = ContentServiceCatalog.COMPONENT;
    private static final String ELECTRONIC_TEXT = "ELECTRONIC_TEXT";
    private final ContentEntityStore store;
    private final ContentPermissionService permissions;
    private final PartyRolePort partyRolePort;
    private final EntityAutoService entityAutoService;
    private final ContentServiceCatalog catalog;

    public ContentFlowServices(ServiceHandlerRegistry registry, ContentEntityStore store, ContentPermissionService permissions,
            PartyRolePort partyRolePort, EntityAutoService entityAutoService, ContentServiceCatalog catalog) {
        this.store = store;
        this.permissions = permissions;
        this.partyRolePort = partyRolePort;
        this.entityAutoService = entityAutoService;
        this.catalog = catalog;
        // java / groovy
        registry.registerHandler(C, "createDataResource", "CREATE", this::createDataResource);
        registry.registerHandler(C, "createDataResourceAndText", "CREATE", this::createDataResourceAndText);
        registry.registerHandler(C, "updateDataResourceAndText", "UPDATE", this::updateDataResourceAndText);
        registry.registerHandler(C, "getElectronicText", "VIEW", this::getElectronicText);
        registry.registerHandler(C, "getContent", "VIEW", this::getContent);
        registry.registerHandler(C, "getDataResource", "VIEW", this::getDataResource);
        registry.registerHandler(C, "getContentAndDataResource", "VIEW", this::getContentAndDataResource);
        registry.registerHandler(C, "createContentFromDataResource", "CREATE", this::createContentFromDataResource);
        registry.registerHandler(C, "createSimpleTextContent", "CREATE", this::createSimpleTextContent);
        registry.registerHandler(C, "updateSimpleTextContent", "UPDATE", this::updateSimpleTextContent);
        registry.registerHandler(C, "deactivateContentAssoc", "UPDATE", this::deactivateContentAssoc);
        // route / group
        registry.registerHandler(C, "createDataText", "CREATE", this::createDataText);
        registry.registerHandler(C, "updateDataText", "UPDATE", this::updateDataText);
        registry.registerHandler(C, "createTextContent", "CREATE", this::createTextContent);
        registry.registerHandler(C, "updateTextContent", "UPDATE", this::updateTextContent);
        // entity-auto に servicedef の override default-value と監査項目を加えるラッパー
        registry.registerHandler(C, "createContent", "CREATE", this::createContent);
        // 複合 PK の OUT 項目（名前が *SeqId でない）を OFBiz と同じくサブシーケンスで採番するラッパー
        registry.registerHandler(C, "createSurveyMultiResp", "CREATE",
                input -> createWithSubSequence("SurveyMultiResp", "surveyMultiRespId", input, "surveyId"));
        registry.registerHandler(C, "createSurveyMultiRespColumn", "CREATE",
                input -> createWithSubSequence("SurveyMultiRespColumn", "surveyMultiRespColId", input, "surveyId", "surveyMultiRespId"));
    }

    /** DataServicesScript.groovy#createDataResource（+ SECA createDataResourceRole / ensurePartyRole）。 */
    public Map<String, Object> createDataResource(Map<String, Object> input) {
        permissions.requireDataResourcePermission("CREATE", null, (String) input.get("dataTemplateTypeId"));
        Map<String, Object> values = new LinkedHashMap<>(input);
        if (ContentEntityStore.isEmpty(values.get("dataResourceId"))) {
            values.put("dataResourceId", store.nextId("DataResource"));
        }
        String user = ContentEntityStore.currentUserLoginId();
        values.put("createdByUserLogin", user);
        values.put("lastModifiedByUserLogin", user);
        values.put("createdDate", ContentEntityStore.now());
        values.put("lastModifiedDate", ContentEntityStore.now());
        if (ContentEntityStore.isEmpty(values.get("dataTemplateTypeId"))) {
            values.put("dataTemplateTypeId", "NONE");
        }
        if (ContentEntityStore.isEmpty(values.get("statusId"))) {
            values.put("statusId", firstContentStatus());
        }
        Map<String, Object> dataResource = store.create("DataResource", values);
        String partyId = (String) input.get("partyId");
        if (!ContentEntityStore.isEmpty(partyId)) {
            String roleTypeId = ContentEntityStore.isEmpty(input.get("roleTypeId")) ? "OWNER" : (String) input.get("roleTypeId");
            partyRolePort.ensurePartyRole(partyId, roleTypeId);
            store.create("DataResourceRole", Map.of("dataResourceId", dataResource.get("dataResourceId"), "partyId", partyId,
                    "roleTypeId", roleTypeId, "fromDate", ContentEntityStore.now()));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("dataResourceId", dataResource.get("dataResourceId"));
        result.put("dataResource", dataResource);
        return result;
    }

    /** route createDataText（SECA による ELECTRONIC_TEXT / SHORT_TEXT の振り分け）。 */
    public Map<String, Object> createDataText(Map<String, Object> input) {
        Map<String, Object> values = new LinkedHashMap<>(input);
        Object type = values.get("dataResourceTypeId");
        boolean hasText = !ContentEntityStore.isEmpty(values.get("textData"));
        if (ContentEntityStore.isEmpty(type)) {
            values.put("dataResourceTypeId", hasText ? ELECTRONIC_TEXT : "SHORT_TEXT");
        }
        Map<String, Object> result = createDataResource(values);
        if (ELECTRONIC_TEXT.equals(values.get("dataResourceTypeId"))) {
            createElectronicText((String) result.get("dataResourceId"), values.get("textData"));
        }
        result.put("dataResourceTypeId", values.get("dataResourceTypeId"));
        return result;
    }

    /** route updateDataText（updateDataResource + ELECTRONIC_TEXT なら updateElectronicText）。 */
    public Map<String, Object> updateDataText(Map<String, Object> input) {
        String dataResourceId = ContentSimpleServices.required(input, "dataResourceId");
        Object dataResource = updateDataResourceRecord(dataResourceId, input);
        Object type = input.containsKey("dataResourceTypeId") ? input.get("dataResourceTypeId")
                : store.get(dataResource, "dataResourceTypeId");
        if (ELECTRONIC_TEXT.equals(type)) {
            updateElectronicText(dataResourceId, input.get("textData"));
        }
        return ContentSimpleServices.result("dataResourceId", dataResourceId);
    }

    /** DataServices#createDataResourceAndText。 */
    public Map<String, Object> createDataResourceAndText(Map<String, Object> input) {
        Map<String, Object> created = createDataResource(input);
        if (ELECTRONIC_TEXT.equals(input.get("dataResourceTypeId"))) {
            createElectronicText((String) created.get("dataResourceId"), input.get("textData"));
        }
        return ContentSimpleServices.result("dataResourceId", created.get("dataResourceId"));
    }

    /** DataServices#updateDataResourceAndText。 */
    public Map<String, Object> updateDataResourceAndText(Map<String, Object> input) {
        String dataResourceId = ContentSimpleServices.required(input, "dataResourceId");
        updateDataResourceRecord(dataResourceId, input);
        if (ELECTRONIC_TEXT.equals(input.get("dataResourceTypeId"))) {
            updateElectronicText(dataResourceId, input.get("textData"));
        }
        return ContentSimpleServices.result("dataResourceId", dataResourceId);
    }

    /** entity-auto createContent + override default-value（DOCUMENT / CTNT_IN_PROGRESS）と作成者・作成日時。 */
    public Map<String, Object> createContent(Map<String, Object> input) {
        Map<String, Object> values = store.filterFields("Content", input);
        values.putIfAbsent("contentTypeId", "DOCUMENT");
        values.putIfAbsent("statusId", "CTNT_IN_PROGRESS");
        String user = ContentEntityStore.currentUserLoginId();
        values.put("createdByUserLogin", user);
        values.put("lastModifiedByUserLogin", user);
        values.put("createdDate", ContentEntityStore.now());
        values.put("lastModifiedDate", ContentEntityStore.now());
        Map<String, Object> result = new LinkedHashMap<>(entityAutoService.execute(catalog.require("createContent"), values));
        result.putIfAbsent("contentId", values.get("contentId"));
        return result;
    }

    /** group createTextContent（createDataText → createContent、result-to-context）。 */
    public Map<String, Object> createTextContent(Map<String, Object> input) {
        Map<String, Object> context = new LinkedHashMap<>(input);
        Map<String, Object> dataText = createDataText(context);
        context.put("dataResourceId", dataText.get("dataResourceId"));
        context.remove("dataResource");
        Map<String, Object> content = createContent(context);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("contentId", content.get("contentId"));
        result.put("dataResourceId", dataText.get("dataResourceId"));
        return result;
    }

    /** group updateTextContent（updateDataText → updateContent）。 */
    public Map<String, Object> updateTextContent(Map<String, Object> input) {
        String contentId = ContentSimpleServices.required(input, "contentId");
        Object content = store.requireOne("Content", Map.of("contentId", contentId), "ServiceValueNotFound");
        Map<String, Object> context = new LinkedHashMap<>(input);
        context.putIfAbsent("dataResourceId", store.get(content, "dataResourceId"));
        updateDataText(context);
        Map<String, Object> contentValues = new LinkedHashMap<>(input);
        contentValues.remove("dataResourceId");
        contentValues.remove("dataResourceTypeId");
        contentValues.put("lastModifiedByUserLogin", ContentEntityStore.currentUserLoginId());
        contentValues.put("lastModifiedDate", ContentEntityStore.now());
        Map<String, Object> result = new LinkedHashMap<>(store.store(content, contentValues));
        result.put("dataResourceId", context.get("dataResourceId"));
        return result;
    }

    /** ContentManagementServices#getContent（ContentDataResourceView 相当を view として返す）。 */
    public Map<String, Object> getContent(Map<String, Object> input) {
        Map<String, Object> content = ContentEntityStore.isEmpty(input.get("contentId"))
                ? null : store.findOneMap("Content", Map.of("contentId", input.get("contentId")));
        if (content == null) {
            return ContentSimpleServices.result("view", null);
        }
        Map<String, Object> view = new LinkedHashMap<>(content);
        Object dataResourceId = content.get("dataResourceId");
        Map<String, Object> dataResource = ContentEntityStore.isEmpty(dataResourceId)
                ? null : store.findOneMap("DataResource", Map.of("dataResourceId", dataResourceId));
        if (dataResource != null) {
            dataResource.forEach((key, value) -> view.put("dr" + Character.toUpperCase(key.charAt(0)) + key.substring(1), value));
        }
        return ContentSimpleServices.result("view", view);
    }

    /** ContentServicesScript.groovy#getDataResource。 */
    public Map<String, Object> getDataResource(Map<String, Object> input) {
        return ContentSimpleServices.result("resultData", dataResourceData(ContentSimpleServices.required(input, "dataResourceId")));
    }

    /** ContentServicesScript.groovy#getContentAndDataResource。 */
    public Map<String, Object> getContentAndDataResource(Map<String, Object> input) {
        Map<String, Object> content = store.findOneMap("Content", Map.of("contentId", ContentSimpleServices.required(input, "contentId")));
        Map<String, Object> resultData = new LinkedHashMap<>();
        resultData.put("content", content);
        if (content != null && !ContentEntityStore.isEmpty(content.get("dataResourceId"))) {
            Map<String, Object> data = dataResourceData(content.get("dataResourceId").toString());
            resultData.put("dataResource", data.get("dataResource"));
            resultData.put("electronicText", data.get("electronicText"));
            resultData.put("imageDataResource", data.get("imageDataResource"));
        }
        return ContentSimpleServices.result("resultData", resultData);
    }

    /** DataServicesScript.groovy#getElectronicText。 */
    public Map<String, Object> getElectronicText(Map<String, Object> input) {
        if (ContentEntityStore.isEmpty(input.get("contentId"))) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "ContentNeitherContentSupplied");
        }
        Object content = store.requireOne("Content", Map.of("contentId", input.get("contentId")), "ContentNeitherContentSupplied");
        Object dataResourceId = store.get(content, "dataResourceId");
        if (ContentEntityStore.isEmpty(dataResourceId)) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "ContentDataResourceNotFound");
        }
        Object text = store.requireOne("ElectronicText", Map.of("dataResourceId", dataResourceId), "ContentElectronicTextNotFound");
        Map<String, Object> result = ContentSimpleServices.result("dataResourceId", dataResourceId);
        result.put("textData", store.get(text, "textData"));
        return result;
    }

    /** ContentServicesScript.groovy#createContentFromDataResource（uploadedFile は未移植）。 */
    public Map<String, Object> createContentFromDataResource(Map<String, Object> input) {
        String dataResourceId = ContentSimpleServices.required(input, "dataResourceId");
        Object dataResource = store.requireOne("DataResource", Map.of("dataResourceId", dataResourceId), "ContentDataResourceNotFound");
        Map<String, Object> values = new LinkedHashMap<>(input);
        if (ContentEntityStore.isEmpty(values.get("contentName"))) {
            values.put("contentName", store.get(dataResource, "dataResourceName"));
        }
        return ContentSimpleServices.result("contentId", createContent(values).get("contentId"));
    }

    /** ContentServicesScript.groovy#createSimpleTextContent（FTL テンプレートの ElectronicText + DOCUMENT Content）。 */
    public Map<String, Object> createSimpleTextContent(Map<String, Object> input) {
        String text = ContentSimpleServices.required(input, "text");
        Map<String, Object> dataValues = new LinkedHashMap<>(input);
        dataValues.remove("contentId");
        dataValues.put("dataResourceTypeId", ELECTRONIC_TEXT);
        dataValues.put("dataTemplateTypeId", "FTL");
        String dataResourceId = (String) createDataResource(dataValues).get("dataResourceId");
        createElectronicText(dataResourceId, text);
        Map<String, Object> contentValues = new LinkedHashMap<>(input);
        contentValues.put("contentTypeId", "DOCUMENT");
        contentValues.put("dataResourceId", dataResourceId);
        Map<String, Object> content = createContent(contentValues);
        Map<String, Object> result = ContentSimpleServices.result("contentId", content.get("contentId"));
        result.put("dataResourceId", dataResourceId);
        return result;
    }

    /** ContentServicesScript.groovy#updateSimpleTextContent。 */
    public Map<String, Object> updateSimpleTextContent(Map<String, Object> input) {
        Map<String, Object> result = new LinkedHashMap<>();
        Object textDataResourceId = input.get("textDataResourceId");
        if (!ContentEntityStore.isEmpty(textDataResourceId)) {
            updateElectronicText(textDataResourceId.toString(), input.get("text"));
            result.put("dataResourceId", textDataResourceId);
            result.put("textData", input.get("text"));
        }
        return result;
    }

    /** ContentServices#deactivateContentAssoc（thruDate を現在時刻に設定）。 */
    public Map<String, Object> deactivateContentAssoc(Map<String, Object> input) {
        Map<String, Object> key = new LinkedHashMap<>();
        for (String field : List.of("contentId", "contentIdTo", "contentAssocTypeId", "fromDate")) {
            key.put(field, ContentSimpleServices.required(input, field));
        }
        Object assoc = store.requireOne("ContentAssoc", key, "ServiceValueNotFound");
        return ContentSimpleServices.result("contentAssoc", store.store(assoc, Map.of("thruDate", ContentEntityStore.now())));
    }

    /** entity-auto create（pk の 1 項目が OUT のサブシーケンス）。 */
    public Map<String, Object> createWithSubSequence(String entityName, String sequenceField, Map<String, Object> input,
            String... parentKeys) {
        Map<String, Object> values = new LinkedHashMap<>(input);
        Map<String, Object> parent = new LinkedHashMap<>();
        for (String key : parentKeys) {
            parent.put(key, ContentSimpleServices.required(input, key));
        }
        values.put(sequenceField, store.nextSubSequence(entityName, parent, sequenceField));
        return store.create(entityName, values);
    }

    private Map<String, Object> dataResourceData(String dataResourceId) {
        Map<String, Object> data = new LinkedHashMap<>();
        Map<String, Object> dataResource = store.findOneMap("DataResource", Map.of("dataResourceId", dataResourceId));
        if (dataResource != null) {
            data.put("dataResource", dataResource);
            if (ELECTRONIC_TEXT.equals(dataResource.get("dataResourceTypeId"))) {
                data.put("electronicText", store.findOneMap("ElectronicText", Map.of("dataResourceId", dataResourceId)));
            }
            if ("IMAGE_OBJECT".equals(dataResource.get("dataResourceTypeId"))) {
                data.put("imageDataResource", store.findOneMap("ImageDataResource", Map.of("dataResourceId", dataResourceId)));
            }
        }
        return data;
    }

    private Object updateDataResourceRecord(String dataResourceId, Map<String, Object> input) {
        Object dataResource = store.requireOne("DataResource", Map.of("dataResourceId", dataResourceId), "ContentDataResourceNotFound");
        Object templateType = input.containsKey("dataTemplateTypeId") ? input.get("dataTemplateTypeId")
                : store.get(dataResource, "dataTemplateTypeId");
        permissions.requireDataResourcePermission("UPDATE", dataResourceId, templateType == null ? null : templateType.toString());
        Map<String, Object> values = new LinkedHashMap<>(input);
        values.put("lastModifiedByUserLogin", ContentEntityStore.currentUserLoginId());
        values.put("lastModifiedDate", ContentEntityStore.now());
        store.store(dataResource, values);
        return dataResource;
    }

    private void createElectronicText(String dataResourceId, Object textData) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("dataResourceId", dataResourceId);
        values.put("textData", textData);
        store.create("ElectronicText", values);
    }

    private void updateElectronicText(String dataResourceId, Object textData) {
        Object text = store.findOne("ElectronicText", Map.of("dataResourceId", dataResourceId));
        if (text == null) {
            createElectronicText(dataResourceId, textData);
        } else {
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("textData", textData);
            store.store(text, values);
        }
    }

    private String firstContentStatus() {
        return store.findList("StatusItem", Map.of("statusTypeId", "CONTENT_STATUS")).stream()
                .min(Comparator.comparing(row -> String.valueOf(store.get(row, "sequenceId"))))
                .map(row -> (String) store.get(row, "statusId"))
                .orElse("CTNT_IN_PROGRESS");
    }
}
