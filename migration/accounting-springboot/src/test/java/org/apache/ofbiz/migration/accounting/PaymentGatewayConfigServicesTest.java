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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.persistence.EntityManager;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = AccountingMigrationApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PaymentGatewayConfigServicesTest {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void updatesOnlyExistingGatewayConfigTypeAndRequiresSensitiveAuthority() throws Exception {
        Class<?> type = entities.entityClass("PaymentGatewayConfigType");
        Object fixture = entities.newEntity(type);
        entities.set(fixture, "paymentGatewayConfigTypeId", "MIG_PG_TYPE");
        entities.set(fixture, "description", "Before update");
        entityManager.persist(fixture);
        entityManager.flush();

        String payload = "{\"paymentGatewayConfigTypeId\":\"MIG_PG_TYPE\","
                + "\"description\":\"After update\"}";
        mockMvc.perform(post("/api/accounting/services/updatePaymentGatewayConfigType")
                        .with(httpBasic("acct-clerk", "test"))
                        .contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/accounting/services/updatePaymentGatewayConfigType")
                        .with(httpBasic("acct-admin", "test"))
                        .contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isOk());
        entityManager.clear();
        Object updated = entityManager.find(type, "MIG_PG_TYPE");
        assertEquals("After update", entities.get(updated, "description"));
    }
}
