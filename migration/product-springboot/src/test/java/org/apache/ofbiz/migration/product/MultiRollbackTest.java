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
package org.apache.ofbiz.migration.product;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.SequenceService;
import org.apache.ofbiz.migration.product.generated.ServiceCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
        classes = ProductMigrationApplication.class,
        properties = "migration.security.require-https=false")
@AutoConfigureMockMvc
class MultiRollbackTest {
    private static final ObjectMapper JSON = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityRegistry entities;

    @SpyBean
    private SequenceService sequences;

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void ac01_duplicateInMultiReturns409AndRollsBackAllRows() throws Exception {
        ServiceCatalog.ServiceDefinition create =
                ProductTestSupport.matching("ProductType", "create", "catalog");
        Map<String, Object> first = ProductTestSupport.input(create, entities, "multi-first", false);
        Map<String, Object> second = ProductTestSupport.input(create, entities, "multi-second", false);
        first.put("productTypeId", "multi-first");
        second.put("productTypeId", "multi-second");
        List<Map<String, Object>> inputs = List.of(first, second, first);

        mockMvc.perform(post("/api/product/services/" + create.name() + "/multi")
                        .with(user("admin").authorities(authorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(inputs)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage").value("ServiceValueFound"));

        assertNull(entities.findByPrimaryKey(entities.entityClass("ProductType"),
                ProductTestSupport.keys(create, entities, first)));
        assertNull(entities.findByPrimaryKey(entities.entityClass("ProductType"),
                ProductTestSupport.keys(create, entities, second)));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void ac02_compositeOutKeyCreateInMultiIsRolledBackWhenNextCallFails() throws Exception {
        ServiceCatalog.ServiceDefinition parentCreate =
                ProductTestSupport.matching("ProductConfigItem", "create", "catalog");
        Map<String, Object> parentInput = ProductTestSupport.input(
                parentCreate, entities, "multi-config-parent", false);
        String parentBody = mockMvc.perform(post("/api/product/services/" + parentCreate.name())
                        .with(user("admin").authorities(authorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(parentInput)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Map<String, Object> parentOutput = JSON.readValue(parentBody, new TypeReference<>() { });

        ServiceCatalog.ServiceDefinition optionCreate =
                ServiceCatalog.all().get("createProductConfigOption");
        Map<String, Object> optionInput = ProductTestSupport.input(
                optionCreate, entities, "multi-config-option", false);
        String configItemId = parentOutput.get("configItemId").toString();
        optionInput.put("configItemId", configItemId);
        doReturn("00001").when(sequences)
                .nextSubSequence(eq("ProductConfigOption"), anyMap(), eq("configOptionId"));

        mockMvc.perform(post("/api/product/services/" + optionCreate.name() + "/multi")
                        .with(user("admin").authorities(authorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(List.of(optionInput, optionInput))))
                .andExpect(status().isConflict());

        assertNull(entities.findByPrimaryKey(entities.entityClass("ProductConfigOption"), Map.of(
                "configItemId", configItemId, "configOptionId", "00001")));
    }

    private static GrantedAuthority[] authorities() {
        return java.util.Arrays.stream(new String[] {
            "OFBTOOLS_ADMIN", "CATALOG_ADMIN", "FACILITY_ADMIN", "PRODUCT_ADMIN",
            "PRODUCT_SENSITIVE_VIEW", "CATALOG_PRICE_MAINT", "CATALOG_CREATE"
        }).map(SimpleGrantedAuthority::new).toArray(GrantedAuthority[]::new);
    }
}
