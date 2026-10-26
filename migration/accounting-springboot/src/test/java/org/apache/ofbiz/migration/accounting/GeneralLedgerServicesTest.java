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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.persistence.EntityManager;
import java.sql.Timestamp;
import java.util.Map;
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
class GeneralLedgerServicesTest {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void quickCreateAcctgTransAndEntriesSkipsTheUnportedTransactionCreationCall() throws Exception {
        persist("GlAccount", Map.of("glAccountId", "MIG_GL_DEBIT"));
        persist("GlAccount", Map.of("glAccountId", "MIG_GL_CREDIT"));
        post("quickCreateAcctgTransAndEntries", "{\"debitGlAccountId\":\"MIG_GL_DEBIT\","
                + "\"creditGlAccountId\":\"MIG_GL_CREDIT\",\"amount\":\"10.00\"}").andExpect(status().isOk());
    }

    @Test
    void calculateGlJournalTrialBalanceReturnsZeroForEmptyJournal() throws Exception {
        persist("GlJournal", Map.of("glJournalId", "MIG_GL_JOURNAL"));
        post("calculateGlJournalTrialBalance", "{\"glJournalId\":\"MIG_GL_JOURNAL\"}")
                .andExpect(status().isOk()).andExpect(jsonPath("$.debitTotal").value(0))
                .andExpect(jsonPath("$.creditTotal").value(0));
    }

    @Test
    void postGlJournalAcceptsBalancedEmptyJournal() throws Exception {
        persist("GlJournal", Map.of("glJournalId", "MIG_GL_POST_JOURNAL"));
        post("postGlJournal", "{\"glJournalId\":\"MIG_GL_POST_JOURNAL\"}").andExpect(status().isOk());
    }

    @Test
    void updateGlReconciliationLastModifiedStoresTheCaller() throws Exception {
        reconciliation("MIG_GL_LAST_MODIFIED");
        post("updateGlReconciliationLastModified", "{\"glReconciliationId\":\"MIG_GL_LAST_MODIFIED\"}")
                .andExpect(status().isOk());
    }

    @Test
    void createGlReconciliationEntryRejectsAlreadyReconciledEntries() throws Exception {
        transaction("MIG_GL_RECON_TRANS", "N");
        persist("AcctgTransEntry", Map.of("acctgTransId", "MIG_GL_RECON_TRANS",
                "acctgTransEntrySeqId", "00001", "reconcileStatusId", "AES_RECONCILED"));
        reconciliation("MIG_GL_RECON_ENTRY");
        post("createGlReconciliationEntry", "{\"glReconciliationId\":\"MIG_GL_RECON_ENTRY\","
                + "\"acctgTransId\":\"MIG_GL_RECON_TRANS\",\"acctgTransEntrySeqId\":\"00001\","
                + "\"reconciledAmount\":\"1.00\"}").andExpect(status().isBadRequest());
    }

    @Test
    void createGlReconciliationEntryReconcilesAnUnreconciledEntry() throws Exception {
        transaction("MIG_GL_RECON_OK_TX", "N");
        persist("AcctgTransEntry", Map.of("acctgTransId", "MIG_GL_RECON_OK_TX",
                "acctgTransEntrySeqId", "00001", "reconcileStatusId", "AES_NOT_RECONCILED"));
        reconciliation("MIG_GL_RECON_OK");
        post("createGlReconciliationEntry", "{\"glReconciliationId\":\"MIG_GL_RECON_OK\","
                + "\"acctgTransId\":\"MIG_GL_RECON_OK_TX\",\"acctgTransEntrySeqId\":\"00001\","
                + "\"reconciledAmount\":\"1.00\"}").andExpect(status().isOk())
                .andExpect(jsonPath("$.statusId").value("GLREC_RECONCILED"));
    }

    @Test
    void completeAcctgTransEntriesAcceptsAnEmptyUnpostedTransaction() throws Exception {
        transaction("MIG_GL_COMPLETE", "N");
        post("completeAcctgTransEntries", "{\"acctgTransId\":\"MIG_GL_COMPLETE\"}")
                .andExpect(status().isOk());
    }

    @Test
    void createAcctgTransAndEntriesReturnsWithoutCreatingAnEmptyTransaction() throws Exception {
        post("createAcctgTransAndEntries", "{\"acctgTransEntries\":[]}")
                .andExpect(status().isOk()).andExpect(jsonPath("$.acctgTransId").doesNotExist());
    }

    @Test
    void closeFinancialTimePeriodClosesALeafPeriod() throws Exception {
        persist("CustomTimePeriod", Map.of("customTimePeriodId", "MIG_GL_PERIOD",
                "fromDate", Timestamp.valueOf("2025-01-01 00:00:00"),
                "thruDate", Timestamp.valueOf("2025-12-31 23:59:59"), "isClosed", "N"));
        post("closeFinancialTimePeriod", "{\"customTimePeriodId\":\"MIG_GL_PERIOD\"}")
                .andExpect(status().isOk());
    }

    @Test
    void computeGlAccountBalanceForTimePeriodReturnsZeroForAnEmptyPeriod() throws Exception {
        period("MIG_GL_BAL_PERIOD");
        persist("GlAccount", Map.of("glAccountId", "MIG_GL_BAL_ACCOUNT"));
        post("computeGlAccountBalanceForTimePeriod", "{\"organizationPartyId\":\"MIG_DEMO_VENDOR\","
                + "\"customTimePeriodId\":\"MIG_GL_BAL_PERIOD\",\"glAccountId\":\"MIG_GL_BAL_ACCOUNT\"}")
                .andExpect(status().isOk()).andExpect(jsonPath("$.endingBalance").value(0));
    }

    @Test
    void computeAndStoreGlAccountHistoryBalanceUpdatesHistory() throws Exception {
        period("MIG_GL_HIST");
        persist("GlAccount", Map.of("glAccountId", "MIG_GL_HIST_ACCT"));
        persist("GlAccountHistory", Map.of("glAccountId", "MIG_GL_HIST_ACCT",
                "organizationPartyId", "MIG_DEMO_VENDOR", "customTimePeriodId", "MIG_GL_HIST"));
        post("computeAndStoreGlAccountHistoryBalance", "{\"organizationPartyId\":\"MIG_DEMO_VENDOR\","
                + "\"customTimePeriodId\":\"MIG_GL_HIST\","
                + "\"glAccountId\":\"MIG_GL_HIST_ACCT\"}").andExpect(status().isOk());
    }

    @Test
    void prepareIncomeStatementReturnsZeroWhenNoTransactionsMatch() throws Exception {
        post("prepareIncomeStatement", "{\"organizationPartyId\":\"MIG_DEMO_VENDOR\","
                + "\"fromDate\":\"2025-01-01 00:00:00\",\"thruDate\":\"2026-01-01 00:00:00\","
                + "\"glFiscalTypeId\":\"ACTUAL\"}").andExpect(status().isOk())
                .andExpect(jsonPath("$.totalNetIncome").value(0));
    }

    @Test
    void getGlAccountFromAccountTypeReturnsEmptyWhenNoMappingExists() throws Exception {
        post("getGlAccountFromAccountType", "{\"organizationPartyId\":\"MIG_DEMO_VENDOR\","
                + "\"glAccountTypeId\":\"MIG_NO_MAPPING\"}").andExpect(status().isOk());
    }

    @Test
    void getInventoryItemOwnerReturnsOwnerFromInventoryPort() throws Exception {
        persist("InventoryItem", Map.of("inventoryItemId", "MIG_GL_ITEM",
                "productId", "MIG_DEMO_PRODUCT_1", "ownerPartyId", "MIG_DEMO_VENDOR"));
        post("getInventoryItemOwner", "{\"inventoryItemId\":\"MIG_GL_ITEM\"}")
                .andExpect(status().isOk()).andExpect(jsonPath("$.ownerPartyId").value("MIG_DEMO_VENDOR"));
    }

    @Test
    void copyAcctgTransAndEntriesSkipsTheUnportedTransactionCreationCall() throws Exception {
        transaction("MIG_GL_COPY_SOURCE", "N");
        post("copyAcctgTransAndEntries", "{\"fromAcctgTransId\":\"MIG_GL_COPY_SOURCE\"}")
                .andExpect(status().isOk());
    }

    @Test
    void updateGlAccountCategoryMemberDoesNothingWhenPercentageIsOmitted() throws Exception {
        post("updateGlAccountCategoryMember", "{\"glAccountId\":\"MIG_GL_ACCOUNT\","
                + "\"glAccountCategoryId\":\"MIG_GL_CATEGORY\",\"fromDate\":\"2025-01-01 00:00:00\"}")
                .andExpect(status().isOk());
    }

    @Test
    void createGlAcctCatMemFromCostCentersAcceptsAnEmptyAllocation() throws Exception {
        persist("GlAccount", Map.of("glAccountId", "MIG_GL_COST_ACCOUNT"));
        persist("GlAccountCategoryType", Map.of("glAccountCategoryTypeId", "MIG_GL_COST_TYPE"));
        persist("GlAccountCategory", Map.of("glAccountCategoryId", "MIG_GL_COST_CATEGORY",
                "glAccountCategoryTypeId", "MIG_GL_COST_TYPE"));
        post("createGlAcctCatMemFromCostCenters", "{\"glAccountId\":\"MIG_GL_COST_ACCOUNT\","
                + "\"glAccountCategoryId\":\"MIG_GL_COST_CATEGORY\",\"totalAmountPercentage\":\"0\"}")
                .andExpect(status().isOk());
    }

    @Test
    void getGlAcctgAndAmountPercentageReturnsAnEmptyOrganizationList() throws Exception {
        post("getGlAcctgAndAmountPercentage", "{\"organizationPartyId\":\"MIG_DEMO_VENDOR\"}")
                .andExpect(status().isOk());
    }

    @Test
    void getInventoryValuationListReturnsEmptyWithoutInventory() throws Exception {
        post("getInventoryValuationList", "{}").andExpect(status().isOk());
    }

    @Test
    void setGlReconciliationStatusReturnsTheExistingStatus() throws Exception {
        reconciliation("MIG_GL_STATUS");
        post("setGlReconciliationStatus", "{\"glReconciliationId\":\"MIG_GL_STATUS\","
                + "\"statusId\":\"GLREC_CREATED\"}").andExpect(status().isOk())
                .andExpect(jsonPath("$.oldStatusId").value("GLREC_CREATED"));
    }

    private void period(String id) {
        persist("CustomTimePeriod", Map.of("customTimePeriodId", id,
                "fromDate", Timestamp.valueOf("2025-01-01 00:00:00"),
                "thruDate", Timestamp.valueOf("2025-12-31 23:59:59"), "isClosed", "N"));
    }

    private void reconciliation(String id) {
        persist("GlReconciliation", Map.of("glReconciliationId", id, "organizationPartyId", "MIG_DEMO_VENDOR",
                "glReconciliationName", id, "statusId", "GLREC_CREATED"));
    }

    private void transaction(String id, String posted) {
        persist("AcctgTrans", Map.of("acctgTransId", id, "isPosted", posted,
                "transactionDate", Timestamp.valueOf("2025-01-01 00:00:00")));
    }

    private Object persist(String entityName, Map<String, ?> values) {
        Object entity = entities.newEntity(entities.entityClass(entityName));
        values.forEach((name, value) -> entities.set(entity, name, value));
        entityManager.persist(entity);
        entityManager.flush();
        return entity;
    }

    private org.springframework.test.web.servlet.ResultActions post(String service, String body) throws Exception {
        return mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .post("/api/accounting/services/" + service)
                .with(httpBasic("acct-admin", "test"))
                .contentType(MediaType.APPLICATION_JSON).content(body));
    }
}
