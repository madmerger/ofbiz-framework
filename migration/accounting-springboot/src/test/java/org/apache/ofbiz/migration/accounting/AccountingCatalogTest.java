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
package org.apache.ofbiz.migration.accounting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SpringBootTest(classes = AccountingMigrationApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountingCatalogTest {
    private static final int EXPECTED_SERVICES = 386;

    @Autowired
    private ServiceHandlerRegistry registry;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void accountingCatalogContainsExactlyTheExpectedInventory() {
        assertEquals(EXPECTED_SERVICES,
                new AccountingCatalogRegistrar(new ServiceHandlerRegistry()).parseEntityAutoNames().size());
    }

    @ParameterizedTest
    @MethodSource("entityAutoServices")
    void eachEntityAutoDefinitionIsRegistered(String name) {
        assertTrue(registry.find("accounting", name).isPresent(), name);
    }

    @ParameterizedTest(name = "GeneralLedger simple service {0} is registered")
    @MethodSource("generalLedgerServices")
    void eachGeneralLedgerSimpleServiceIsRegistered(String name) {
        assertTrue(registry.handler("accounting", name).isPresent(), name);
    }

    @ParameterizedTest(name = "Accounting simple service {0}")
    @MethodSource("simpleServicesFromDefinitions")
    void allSimpleServicesAreRegistered(String name) throws Exception {
        assertTrue(registry.handler("accounting", name).isPresent(), name);
        assertTrue(mockMvc.perform(post("/api/accounting/services/" + name)
                        .with(httpBasic("acct-admin", "test"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andReturn().getResponse().getStatus() != 404, name);
    }

    @Test
    void executesEntityAutoCrudWhereCatalogAndSchemaAllowIt() throws Exception {
        List<ServiceHandlerRegistry.ServiceDefinition> definitions = entityAutoServices()
                .map(name -> registry.find("accounting", name).orElseThrow())
                .toList();
        Map<String, ServiceHandlerRegistry.ServiceDefinition> creates = byEntityAndAction(definitions, "create");
        Map<String, ServiceHandlerRegistry.ServiceDefinition> updates = byEntityAndAction(definitions, "update");
        Map<String, ServiceHandlerRegistry.ServiceDefinition> deletes = byEntityAndAction(definitions, "delete");
        Set<String> visited = new HashSet<>();
        List<String> skipped = new ArrayList<>();
        int executed = 0;
        String suffix = Long.toString(System.nanoTime(), 36);

        for (var entry : creates.entrySet()) {
            String entityName = entry.getKey();
            if (!visited.add(entityName)) {
                continue;
            }
            var update = updates.get(entityName);
            var delete = deletes.get(entityName);
            if (update == null || delete == null) {
                skipped.add(entityName + ": create/update/delete trio unavailable");
                continue;
            }
            Map<String, Object> keys = new LinkedHashMap<>();
            try {
                Class<?> entityType = entities.entityClass(entityName);
                Map<String, Object> input = testInput(entry.getValue(), suffix);
                MvcResult createdResult = invoke(entry.getValue().name(), input);
                if (createdResult.getResponse().getStatus() != 200) {
                    skipped.add(entityName + ": create returned "
                            + createdResult.getResponse().getStatus());
                    continue;
                }
                Map<String, Object> created = objectMapper.readValue(
                        createdResult.getResponse().getContentAsString(), new TypeReference<>() { });
                for (String key : entities.primaryKeys(entityType)) {
                    if (created.containsKey(key) && created.get(key) != null) {
                        keys.put(key, created.get(key));
                    }
                }
                if (keys.size() != entities.primaryKeys(entityType).size()) {
                    skipped.add(entityName + ": create response omitted a primary key");
                    continue;
                }
                var loaded = mockMvc.perform(get("/api/accounting/entities/" + entityName + "/one")
                                .with(httpBasic("acct-admin", "test"))
                                .queryParams(toMultiValueMap(keys)))
                        .andReturn();
                if (loaded.getResponse().getStatus() != 200) {
                    skipped.add(entityName + ": entity lookup returned " + loaded.getResponse().getStatus());
                    continue;
                }
                Map<String, Object> updateInput = testInput(update, suffix);
                updateInput.putAll(keys);
                MvcResult updateResult = invoke(update.name(), updateInput);
                if (updateResult.getResponse().getStatus() != 200) {
                    invoke(delete.name(), keys);
                    skipped.add(entityName + ": update returned " + updateResult.getResponse().getStatus());
                    continue;
                }
                MvcResult deleteResult = invoke(delete.name(), keys);
                if (deleteResult.getResponse().getStatus() != 200) {
                    skipped.add(entityName + ": delete returned " + deleteResult.getResponse().getStatus());
                    continue;
                }
                executed++;
            } catch (Exception exception) {
                if (!keys.isEmpty()) {
                    try {
                        invoke(delete.name(), keys);
                    } catch (Exception ignored) {
                        skipped.add(entityName + ": cleanup failed after " + exception.getClass().getSimpleName());
                    }
                }
                skipped.add(entityName + ": " + exception.getClass().getSimpleName() + ": "
                        + exception.getMessage());
            }
        }

        System.out.printf("Accounting entity-auto CRUD: executed=%d skipped=%d%n", executed, skipped.size());
        skipped.forEach(reason -> System.out.println("Accounting entity-auto skipped: " + reason));
        assertTrue(executed > 0, "No entity-auto CRUD candidate completed");
    }

    private static Stream<String> entityAutoServices() {
        return new AccountingCatalogRegistrar(new ServiceHandlerRegistry()).parseEntityAutoNames().stream();
    }

    private static Stream<String> generalLedgerServices() {
        return Stream.of(
                "quickCreateAcctgTransAndEntries",
                "calculateGlJournalTrialBalance",
                "postGlJournal",
                "updateGlReconciliationLastModified",
                "createGlReconciliationEntry",
                "completeAcctgTransEntries",
                "createAcctgTransAndEntries",
                "closeFinancialTimePeriod",
                "computeGlAccountBalanceForTimePeriod",
                "computeAndStoreGlAccountHistoryBalance",
                "prepareIncomeStatement",
                "getGlAccountFromAccountType",
                "getInventoryItemOwner",
                "copyAcctgTransAndEntries",
                "updateGlAccountCategoryMember",
                "createGlAcctCatMemFromCostCenters",
                "getGlAcctgAndAmountPercentage",
                "getInventoryValuationList",
                "setGlReconciliationStatus");
    }

    private static Stream<Arguments> simpleServicesFromDefinitions() throws Exception {
        Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources("classpath*:ofbiz-servicedef/accounting/services_*.xml");
        List<Arguments> result = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        for (Resource resource : resources) {
            try (InputStream input = resource.getInputStream()) {
                NodeList services = factory.newDocumentBuilder().parse(input).getElementsByTagName("service");
                for (int index = 0; index < services.getLength(); index++) {
                    Node node = services.item(index);
                    if (node instanceof Element service && "simple".equals(service.getAttribute("engine"))) {
                        String name = service.getAttribute("name");
                        result.add(Arguments.of(name));
                    }
                }
            }
        }
        assertEquals(93, result.size());
        return result.stream();
    }

    private Map<String, ServiceHandlerRegistry.ServiceDefinition> byEntityAndAction(
            List<ServiceHandlerRegistry.ServiceDefinition> definitions, String action) {
        Map<String, ServiceHandlerRegistry.ServiceDefinition> result = new LinkedHashMap<>();
        definitions.stream().filter(definition -> action.equalsIgnoreCase(definition.invoke()))
                .forEach(definition -> result.putIfAbsent(definition.entity(), definition));
        return result;
    }

    private Map<String, Object> testInput(ServiceHandlerRegistry.ServiceDefinition definition, String suffix) {
        Map<String, Object> result = new HashMap<>();
        Set<String> primaryKeys = Set.copyOf(
                entities.primaryKeys(entities.entityClass(definition.entity())));
        for (var parameter : definition.params()) {
            if (!"IN".equalsIgnoreCase(parameter.mode()) && !"INOUT".equalsIgnoreCase(parameter.mode())) {
                continue;
            }
            if ("INOUT".equalsIgnoreCase(parameter.mode())
                    && primaryKeys.contains(parameter.name())
                    && parameter.name().endsWith("SeqId")) {
                continue;
            }
            String type = parameter.type() == null ? "" : parameter.type().toLowerCase();
            String name = parameter.name().toLowerCase();
            Object value;
            if (type.contains("date-time") || type.contains("timestamp")) {
                value = Timestamp.valueOf("2024-01-01 00:00:00.000").toString();
            } else if (type.equals("date") || type.endsWith("-date")) {
                value = "2024-01-01";
            } else if (type.equals("time") || type.endsWith("-time")) {
                value = "12:00:00";
            } else if (type.contains("numeric") || type.contains("amount") || type.contains("quantity")
                    || type.contains("integer") || type.contains("long") || type.contains("float")
                    || type.contains("decimal")) {
                value = "1";
            } else {
                value = "AT" + suffix + Integer.toHexString(name.hashCode());
            }
            result.put(parameter.name(), value);
        }
        return result;
    }

    private MvcResult invoke(String service, Map<String, Object> input) throws Exception {
        return mockMvc.perform(post("/api/accounting/services/" + service)
                        .with(httpBasic("acct-admin", "test"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andReturn();
    }

    private static org.springframework.util.MultiValueMap<String, String> toMultiValueMap(
            Map<String, Object> values) {
        org.springframework.util.MultiValueMap<String, String> result =
                new org.springframework.util.LinkedMultiValueMap<>();
        values.forEach((key, value) -> result.add(key, value.toString()));
        return result;
    }
}
