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
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.apache.ofbiz.migration.accounting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.persistence.EntityManager;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = AccountingMigrationApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AcctgTransServicesTest {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest(name = "AcctgTrans simple service {0}")
    @MethodSource("simpleServices")
    void eachAcctgTransServiceHasANamedNormalPath(String serviceName) throws Exception {
        createTransaction("MIG_ATX_TEST");
        switch (serviceName) {
            case "checkAcctgTransPosted" ->
                    postService(serviceName, "{\"acctgTransId\":\"MIG_ATX_TEST\"}").andExpect(status().isOk());
            case "createAcctgTransEntry" ->
                    postService(serviceName, entryBody("D")).andExpect(status().isOk());
            case "updateAcctgTransEntry", "deleteAcctgTransEntry" -> {
                MvcResult result = postService("createAcctgTransEntry", entryBody("D"))
                        .andExpect(status().isOk()).andReturn();
                String sequence = com.jayway.jsonpath.JsonPath.parse(result.getResponse().getContentAsString())
                        .read("$.acctgTransEntrySeqId");
                String body = "{\"acctgTransId\":\"MIG_ATX_TEST\",\"acctgTransEntrySeqId\":\""
                        + sequence + "\",\"description\":\"normal path\"}";
                postService(serviceName, body).andExpect(status().isOk());
            }
            case "getAcctgTransEntriesAndTransTotal" ->
                    postService(serviceName, "{\"organizationPartyId\":\"MIG_DEMO_VENDOR\","
                            + "\"glAccountId\":\"MIG_GL_ACCOUNT\","
                            + "\"customTimePeriodStartDate\":\"2020-01-01 00:00:00\","
                            + "\"customTimePeriodEndDate\":\"2030-01-01 00:00:00\"}")
                            .andExpect(status().isOk());
            case "calculateAcctgTransTrialBalance" ->
                    postService(serviceName, "{\"acctgTransId\":\"MIG_ATX_TEST\"}").andExpect(status().isOk());
            case "postAcctgTrans" ->
                    postService(serviceName, "{\"acctgTransId\":\"MIG_ATX_TEST\"}").andExpect(status().isOk());
            case "calculateGlAccountTrialBalance" ->
                    postService(serviceName, "{\"glAccountId\":\"MIG_GL_ACCOUNT\","
                            + "\"fromDate\":\"2020-01-01 00:00:00\",\"thruDate\":\"2030-01-01 00:00:00\"}")
                            .andExpect(status().isOk());
            default -> throw new IllegalArgumentException(serviceName);
        }
    }

    private static Stream<String> simpleServices() {
        return Stream.of("checkAcctgTransPosted", "createAcctgTransEntry", "updateAcctgTransEntry",
                "deleteAcctgTransEntry", "getAcctgTransEntriesAndTransTotal", "calculateAcctgTransTrialBalance",
                "postAcctgTrans", "calculateGlAccountTrialBalance");
    }

    @Test
    void calculatesMaintainsAndDeletesEntriesAndPostsAnEmptyTransaction() throws Exception {
        String glAccountId = "MIG_GL_ACCOUNT";
        createTransaction("MIG_ATX_TEST");

        postService("checkAcctgTransPosted", "{\"acctgTransId\":\"MIG_ATX_TEST\"}")
                .andExpect(status().isOk());
        MvcResult debitResult = postService("createAcctgTransEntry", entryBody("D"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.acctgTransEntrySeqId").exists()).andReturn();
        String debitEntrySeq = com.jayway.jsonpath.JsonPath.parse(debitResult.getResponse().getContentAsString())
                .read("$.acctgTransEntrySeqId");
        postService("postAcctgTrans", "{\"acctgTransId\":\"MIG_ATX_TEST\"}")
                .andExpect(status().isBadRequest());

        postService("createAcctgTransEntry", entryBody("C"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.acctgTransEntrySeqId").exists());
        postService("calculateAcctgTransTrialBalance", "{\"acctgTransId\":\"MIG_ATX_TEST\"}")
                .andExpect(status().isOk()).andExpect(jsonPath("$.debitTotal").value(10.0000))
                .andExpect(jsonPath("$.creditTotal").value(10.0000))
                .andExpect(jsonPath("$.debitCreditDifference").value(0.0000));

        postService("updateAcctgTransEntry", "{\"acctgTransId\":\"MIG_ATX_TEST\","
                + "\"acctgTransEntrySeqId\":\"" + debitEntrySeq + "\",\"description\":\"updated\"}")
                .andExpect(status().isOk());
        postService("getAcctgTransEntriesAndTransTotal", "{\"organizationPartyId\":\"MIG_DEMO_VENDOR\","
                + "\"glAccountId\":\"" + glAccountId + "\",\"customTimePeriodStartDate\":\"2020-01-01 00:00:00\","
                + "\"customTimePeriodEndDate\":\"2030-01-01 00:00:00\"}")
                .andExpect(status().isOk()).andExpect(jsonPath("$.debitTotal").value(0.0000));
        postService("calculateGlAccountTrialBalance", "{\"glAccountId\":\"" + glAccountId
                + "\",\"fromDate\":\"2020-01-01 00:00:00\",\"thruDate\":\"2030-01-01 00:00:00\"}")
                .andExpect(status().isOk()).andExpect(jsonPath("$.openingBalanceDebit").exists());
        postService("deleteAcctgTransEntry", "{\"acctgTransId\":\"MIG_ATX_TEST\","
                + "\"acctgTransEntrySeqId\":\"" + debitEntrySeq + "\"}")
                .andExpect(status().isOk());
        assertTrue(entities.findByEquality(entities.entityClass("AcctgTransEntry"),
                Map.of("acctgTransId", "MIG_ATX_TEST", "acctgTransEntrySeqId", debitEntrySeq)).isEmpty());

        createTransaction("MIG_ATX_POST");
        postService("postAcctgTrans", "{\"acctgTransId\":\"MIG_ATX_POST\"}").andExpect(status().isOk());
        Object posted = entities.findByEquality(entities.entityClass("AcctgTrans"),
                Map.of("acctgTransId", "MIG_ATX_POST")).get(0);
        assertEquals("Y", entities.get(posted, "isPosted"));
    }

    @Test
    void postAcctgTransUsesOFBizLedgerRoundingForTotalsAndDifference() throws Exception {
        createTransaction("MIG_ATX_ROUND");
        persist("GlAccount", Map.of("glAccountId", "MIG_ATX_ROUND_DEBIT"));
        persist("GlAccount", Map.of("glAccountId", "MIG_ATX_ROUND_CREDIT"));
        persistTrialBalanceEntry("00001", "D", "1.00004", "MIG_ATX_ROUND_DEBIT");
        persistTrialBalanceEntry("00002", "C", "1.00000", "MIG_ATX_ROUND_CREDIT");

        postService("calculateAcctgTransTrialBalance", "{\"acctgTransId\":\"MIG_ATX_ROUND\"}")
                .andExpect(status().isOk()).andExpect(jsonPath("$.debitTotal").value(1.0000))
                .andExpect(jsonPath("$.creditTotal").value(1.0000))
                .andExpect(jsonPath("$.debitCreditDifference").value(0.0000));
        postService("postAcctgTrans", "{\"acctgTransId\":\"MIG_ATX_ROUND\"}").andExpect(status().isOk());
    }

    private void createTransaction(String transactionId) {
        Object transaction = entities.newEntity(entities.entityClass("AcctgTrans"));
        entities.set(transaction, "acctgTransId", transactionId);
        entities.set(transaction, "isPosted", "N");
        entityManager.persist(transaction);
        entityManager.flush();
    }

    private void persistTrialBalanceEntry(String sequence, String flag, String amount, String accountId) {
        Object entry = entities.newEntity(entities.entityClass("AcctgTransEntry"));
        entities.set(entry, "acctgTransId", "MIG_ATX_ROUND");
        entities.set(entry, "acctgTransEntrySeqId", sequence);
        entities.set(entry, "debitCreditFlag", flag);
        entities.set(entry, "amount", amount);
        entities.set(entry, "glAccountId", accountId);
        entityManager.persist(entry);
        entityManager.flush();
    }

    private Object persist(String entityName, Map<String, ?> values) {
        Object entity = entities.newEntity(entities.entityClass(entityName));
        values.forEach((name, value) -> entities.set(entity, name, value));
        entityManager.persist(entity);
        entityManager.flush();
        return entity;
    }

    private static String entryBody(String flag) {
        return "{\"acctgTransId\":\"MIG_ATX_TEST\",\"organizationPartyId\":\"MIG_DEMO_VENDOR\","
                + "\"debitCreditFlag\":\"" + flag
                + "\",\"amount\":\"10.00\",\"currencyUomId\":\"USD\"}";
    }

    private org.springframework.test.web.servlet.ResultActions postService(String service, String body)
            throws Exception {
        return mockMvc.perform(post("/api/accounting/services/" + service)
                .with(httpBasic("acct-admin", "test"))
                .contentType(MediaType.APPLICATION_JSON).content(body));
    }
}
