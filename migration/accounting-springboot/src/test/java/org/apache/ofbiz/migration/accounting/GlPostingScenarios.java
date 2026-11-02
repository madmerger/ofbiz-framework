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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

abstract class GlPostingScenarios {
    @Autowired private EntityManager manager;
    @Autowired private EntityRegistry entities;
    @Autowired private MigrationServiceDispatcher dispatcher;

    @Test
    @Transactional
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ACCOUNTING_ADMIN", "ACCTG_ATX_CREATE"})
    void transactionAndEntriesCommitRulePostsBalancedGlAndRejectsReposting() {
        fixture("N");
        Map<String, Object> result = dispatcher.execute("accounting", "createAcctgTransAndEntries", transaction());
        Object row = find("AcctgTrans", Map.of("acctgTransId", result.get("acctgTransId")));
        assertEquals("Y", entities.get(row, "isPosted"));
        assertNotNull(entities.get(row, "postedDate"));
        assertEquals(2, entities.findByEquality(entities.entityClass("AcctgTransEntry"), Map.of("acctgTransId", result.get("acctgTransId"))).size());
        assertThrows(MigrationException.class, () -> dispatcher.execute("accounting", "postAcctgTrans",
                Map.of("acctgTransId", result.get("acctgTransId"))));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ACCOUNTING_ADMIN", "ACCTG_ATX_CREATE"})
    void closedPeriodVerificationLeavesGlUnpostedAndReportsErrors() {
        fixture("Y");
        Map<String, Object> result = dispatcher.execute("accounting", "createAcctgTransAndEntries", transaction());
        Object id = result.get("acctgTransId");
        assertEquals("N", entities.get(find("AcctgTrans", Map.of("acctgTransId", id)), "isPosted"));
        Map<String, Object> verification = dispatcher.execute("accounting", "postAcctgTrans", Map.of("acctgTransId", id, "verifyOnly", "Y"));
        assertTrue(verification.get("successMessageList").toString().contains("closed"));
        assertThrows(MigrationException.class,
                () -> dispatcher.execute("accounting", "postAcctgTrans", Map.of("acctgTransId", id)));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ACCOUNTING_ADMIN", "ACCTG_ATX_CREATE"})
    void emptyEntriesDoNotCreateATransaction() {
        Map<String, Object> result = dispatcher.execute("accounting", "createAcctgTransAndEntries", Map.of("acctgTransEntries", List.of()));
        assertEquals(Map.of("responseMessage", "success"), result);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ACCOUNTING_ADMIN", "ACCTG_ATX_CREATE"})
    void invalidGlIsRoutedToConfiguredErrorJournalWithoutPosting() {
        fixture("Y");
        persist("PartyAcctgPreference", Map.of("partyId", "GAP_ORG", "errorGlJournalId", "GAP_ERRORS"));
        Map<String, Object> result = dispatcher.execute("accounting", "createAcctgTransAndEntries", transaction());
        Object row = find("AcctgTrans", Map.of("acctgTransId", result.get("acctgTransId")));
        assertEquals("N", entities.get(row, "isPosted"));
        assertEquals("GAP_ERRORS", entities.get(row, "glJournalId"));
    }

    private void fixture(String closed) {
        persist("Party", Map.of("partyId", "GAP_ORG", "partyTypeId", "PARTY_GROUP"));
        persist("PartyRole", Map.of("partyId", "GAP_ORG", "roleTypeId", "INTERNAL_ORGANIZATIO"));
        persist("GlAccount", Map.of("glAccountId", "GAP_GL"));
        persist("CustomTimePeriod", Map.of("customTimePeriodId", "GAP_PERIOD", "organizationPartyId", "GAP_ORG",
                "periodTypeId", "FISCAL_YEAR", "fromDate", "2020-01-01T00:00:00Z", "thruDate", "2099-12-31T00:00:00Z", "isClosed", closed));
    }

    private Map<String, Object> transaction() {
        return Map.of("transactionDate", "2026-01-15 12:00:00", "acctgTransEntries", List.of(entry("D"), entry("C")));
    }

    private Map<String, Object> entry(String flag) {
        return Map.of("organizationPartyId", "GAP_ORG", "glAccountId", "GAP_GL",
                "debitCreditFlag", flag, "amount", "10.00", "currencyUomId", "USD");
    }

    private Object find(String name, Map<String, ?> key) {
        return entities.findByPrimaryKey(entities.entityClass(name), key);
    }

    private void persist(String name, Map<String, ?> values) {
        Object row = entities.newEntity(entities.entityClass(name));
        values.forEach((field, value) -> entities.set(row, field, value));
        manager.persist(row);
        manager.flush();
    }
}
