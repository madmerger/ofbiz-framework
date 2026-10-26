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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.product.generated.ServiceCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

    private static GrantedAuthority[] authorities() {
        return java.util.Arrays.stream(new String[] {
            "OFBTOOLS_ADMIN", "CATALOG_ADMIN", "FACILITY_ADMIN", "PRODUCT_ADMIN",
            "PRODUCT_SENSITIVE_VIEW", "CATALOG_PRICE_MAINT", "CATALOG_CREATE"
        }).map(SimpleGrantedAuthority::new).toArray(GrantedAuthority[]::new);
    }
}
