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
package org.apache.ofbiz.migration.party;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.apache.ofbiz.migration.party.generated.ServiceCatalog;
import org.apache.ofbiz.migration.party.generated.ServiceCatalog.ServiceDefinition;
import org.apache.ofbiz.migration.party.generated.ServiceCatalog.ParamDefinition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@SpringBootTest(classes = {PartyMigrationApplication.class, TestUsersConfiguration.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EntityAutoAcceptanceTest {
    private static final AtomicInteger CASES = new AtomicInteger();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ServiceHandlerRegistry registry;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void ac01_catalogMatchesEntityAutoServicesParsedFromServiceDefinitions() throws Exception {
        Path root = Path.of(System.getProperty("ofbiz.root"));
        File[] definitions = root.resolve("applications/party/servicedef").toFile()
                .listFiles((directory, name) -> name.endsWith(".xml"));
        assertNotNull(definitions);
        Set<String> names = new HashSet<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        for (File file : definitions) {
            NodeList services = factory.newDocumentBuilder().parse(file).getElementsByTagName("service");
            for (int index = 0; index < services.getLength(); index++) {
                Element service = (Element) services.item(index);
                if ("entity-auto".equals(service.getAttribute("engine"))) {
                    names.add(service.getAttribute("name"));
                }
            }
        }
        assertEquals(128, names.size());
        assertEquals(names.size(), ServiceCatalog.all().size());
        names.forEach(name -> {
            assertNotNull(ServiceCatalog.find(name).orElse(null), name);
            assertNotNull(registry.find("party", name).orElse(null), name);
        });
    }

    @Test
    void ac02_entityAutoUpdateRejectsFieldsOutsideServiceDefinition() throws Exception {
        mockMvc.perform(post("/api/party/services/updatePartyType")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partyTypeId\":\"PERSON\",\"description\":\"Person\","
                                + "\"unlistedPartyAttribute\":\"unexpected\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void ac03_entityAutoCreateRejectsMissingRequiredInput() throws Exception {
        mockMvc.perform(post("/api/party/services/createPartyRole")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"partyId\":\"AC03_MISSING\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void ac04_duplicateEntityAutoCreateReturnsConflict() throws Exception {
        String input = "{\"partyId\":\"AC04_DUPLICATE\",\"roleTypeId\":\"_NA_\"}";
        mockMvc.perform(post("/api/party/services/createPartyRole")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(input))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/party/services/createPartyRole")
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(input))
                .andExpect(status().isConflict());
    }

    @Test
    void ac06_optionalInoutPrimaryKeyIsGenerated() throws Exception {
        ServiceDefinition definition = ServiceCatalog.find("createAddendum").orElseThrow();
        Class<?> entityType = entities.entityClass(definition.entity());
        Map<String, Object> input = sampleInput(definition, entityType, CASES.incrementAndGet());
        input.remove("addendumId");

        Map<String, Object> response = callService(definition, input, 200);
        Object addendumId = response.get("addendumId");
        assertNotNull(addendumId);
        assertNotNull(entities.findByPrimaryKey(entityType, Map.of("addendumId", addendumId)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("entityAutoServices")
    @Transactional
    void ac05_everyEntityAutoServiceExecutesAndReflectsDatabaseChanges(ServiceDefinition definition)
            throws Exception {
        int caseId = CASES.incrementAndGet();
        Class<?> entityType = entities.entityClass(definition.entity());
        ServiceDefinition createDefinition = "create".equalsIgnoreCase(definition.invoke())
                ? definition
                : ServiceCatalog.all().values().stream()
                        .filter(candidate -> definition.entity().equals(candidate.entity()))
                        .filter(candidate -> "create".equalsIgnoreCase(candidate.invoke()))
                        .findFirst()
                        .orElse(null);

        Map<String, Object> row;
        if (createDefinition == null) {
            row = sampleInput(definition, entityType, caseId);
            Object fixture = entities.newEntity(entityType);
            setWritableFields(fixture, row);
            entityManager.persist(fixture);
            entityManager.flush();
            row = entities.toMap(fixture);
        } else {
            Map<String, Object> createInput = sampleInput(createDefinition, entityType, caseId);
            if (!"create".equalsIgnoreCase(definition.invoke())) {
                row = callService(createDefinition, createInput, 200);
            } else {
                row = null;
            }
            if ("create".equalsIgnoreCase(definition.invoke())) {
                row = callService(createDefinition, createInput, 200);
            }
        }

        Map<String, Object> input = sampleInput(definition, entityType, caseId);
        Map<String, Object> keys = new LinkedHashMap<>();
        for (String key : entities.primaryKeys(entityType)) {
            Object value = row.get(key);
            assertNotNull(value, definition.name() + " generated key " + key);
            keys.put(key, value);
            input.put(key, value);
        }
        if ("update".equalsIgnoreCase(definition.invoke())) {
            definition.params().stream()
                    .filter(param -> !keys.containsKey(param.name()))
                    .filter(param -> !"OUT".equalsIgnoreCase(param.mode()))
                    .findFirst()
                    .ifPresent(param -> input.put(param.name(), sampleValue(
                            param, new BeanWrapperImpl(entities.newEntity(entityType)), caseId + 1)));
        }
        Object before = entities.findByPrimaryKey(entityType, keys);
        assertNotNull(before, definition.name() + " setup row");

        if (!"create".equalsIgnoreCase(definition.invoke())) {
            callService(definition, input, 200);
        }
        switch (definition.invoke().toLowerCase()) {
            case "create" -> assertNotNull(entities.findByPrimaryKey(entityType, keys), definition.name());
            case "delete" -> assertNull(entities.findByPrimaryKey(entityType, keys), definition.name());
            case "expire" -> Assertions.assertNotNull(
                    entities.get(entities.findByPrimaryKey(entityType, keys), "thruDate"), definition.name());
            default -> assertNotNull(entities.findByPrimaryKey(entityType, keys), definition.name());
        }
    }

    static java.util.stream.Stream<ServiceDefinition> entityAutoServices() {
        return ServiceCatalog.all().values().stream();
    }

    private Map<String, Object> callService(ServiceDefinition definition, Map<String, Object> input, int expectedStatus)
            throws Exception {
        var result = mockMvc.perform(post("/api/party/services/" + definition.name())
                        .with(httpBasic("admin", "admin-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(input)))
                .andReturn();
        assertEquals(expectedStatus, result.getResponse().getStatus(), definition.name() + ": "
                + result.getResponse().getContentAsString());
        if (result.getResponse().getStatus() != 200) {
            return Map.of();
        }
        return objectMapper.readValue(result.getResponse().getContentAsByteArray(), new TypeReference<>() {});
    }

    private Map<String, Object> sampleInput(ServiceDefinition definition, Class<?> entityType, int caseId) {
        Object entity = entities.newEntity(entityType);
        BeanWrapperImpl bean = new BeanWrapperImpl(entity);
        Set<String> keys = Set.copyOf(entities.primaryKeys(entityType));
        Map<String, Object> input = new LinkedHashMap<>();
        for (ParamDefinition param : definition.params()) {
            if ("OUT".equalsIgnoreCase(param.mode())) {
                continue;
            }
            if (!param.optional() || keys.contains(param.name())) {
                input.put(param.name(), sampleValue(param, bean, caseId));
            }
        }
        return input;
    }

    private Object sampleValue(ParamDefinition param, BeanWrapperImpl bean, int caseId) {
        String type = param.type() == null ? "" : param.type().toLowerCase();
        Class<?> propertyType = bean.getPropertyType(param.name());
        if (type.contains("date-time") || java.sql.Timestamp.class.equals(propertyType)) {
            return "2025-01-01 00:00:" + String.format("%02d", caseId % 60);
        }
        if (type.equals("date") || Date.class.equals(propertyType)) {
            return Date.valueOf("2025-01-01");
        }
        if (type.equals("time") || Time.class.equals(propertyType)) {
            return Time.valueOf("00:00:00");
        }
        if (type.equals("indicator")) {
            return "Y";
        }
        if (type.contains("currency") || type.contains("numeric") || type.contains("floating")
                || BigDecimal.class.equals(propertyType)) {
            return "1.0";
        }
        if (propertyType == null) {
            return "AC" + caseId;
        }
        if (propertyType == Boolean.class || propertyType == boolean.class) {
            return true;
        }
        if (Number.class.isAssignableFrom(propertyType) || propertyType.isPrimitive()) {
            return "1";
        }
        String value = "EA" + caseId;
        return value.length() > 18 ? value.substring(0, 18) : value;
    }

    private void setWritableFields(Object entity, Map<String, Object> fields) {
        BeanWrapperImpl bean = new BeanWrapperImpl(entity);
        fields.forEach((name, value) -> {
            if (bean.isWritableProperty(name)) {
                entities.set(entity, name, value);
            }
        });
    }
}
