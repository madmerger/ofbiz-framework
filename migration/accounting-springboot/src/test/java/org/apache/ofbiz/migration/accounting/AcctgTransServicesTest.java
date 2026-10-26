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
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.junit.jupiter.api.Test;
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

    private void createTransaction(String transactionId) {
        Object transaction = entities.newEntity(entities.entityClass("AcctgTrans"));
        entities.set(transaction, "acctgTransId", transactionId);
        entities.set(transaction, "isPosted", "N");
        entityManager.persist(transaction);
        entityManager.flush();
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
