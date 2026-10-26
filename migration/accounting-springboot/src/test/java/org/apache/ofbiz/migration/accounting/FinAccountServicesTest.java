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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
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
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = AccountingMigrationApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FinAccountServicesTest {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createFinAccountPersistsAccountAndReturnsItsId() throws Exception {
        postService("createFinAccount", "{\"finAccountId\":\"MIG_FA_CREATE\",\"statusId\":\"FNACT_ACTIVE\","
                + "\"currencyUomId\":\"USD\",\"ownerPartyId\":\"MIG_DEMO_CUSTOMER\"}")
                .andExpect(status().isOk()).andExpect(jsonPath("$.finAccountId").value("MIG_FA_CREATE"));
        assertEquals(1, entities.findByEquality(entities.entityClass("FinAccount"),
                Map.of("finAccountId", "MIG_FA_CREATE")).size());
    }

    @Test
    void updateFinAccountPersistsChangedFields() throws Exception {
        persistAccount("MIG_FA_UPDATE");
        postService("updateFinAccount", "{\"finAccountId\":\"MIG_FA_UPDATE\",\"finAccountName\":\"Updated\"}")
                .andExpect(status().isOk());
        Object account = entities.findByEquality(entities.entityClass("FinAccount"),
                Map.of("finAccountId", "MIG_FA_UPDATE")).get(0);
        assertEquals("Updated", entities.get(account, "finAccountName"));
    }

    @Test
    void createFinAccountTransDefaultsToApprovedAndUpdatesBalances() throws Exception {
        persistAccount("MIG_FA_TRANS");
        postService("createFinAccountTrans", "{\"finAccountId\":\"MIG_FA_TRANS\","
                + "\"finAccountTransTypeId\":\"DEPOSIT\",\"amount\":12.50}")
                .andExpect(status().isOk()).andExpect(jsonPath("$.finAccountTransId").exists());
        Object trans = entities.findByEquality(entities.entityClass("FinAccountTrans"),
                Map.of("finAccountId", "MIG_FA_TRANS")).get(0);
        assertEquals("FINACT_TRNS_APPROVED", entities.get(trans, "statusId"));
        String id = entities.get(trans, "finAccountTransId").toString();
        postService("updateFinAccountBalancesFromTrans", "{\"finAccountTransId\":\"" + id + "\"}")
                .andExpect(status().isOk());
        Object account = entities.findByEquality(entities.entityClass("FinAccount"),
                Map.of("finAccountId", "MIG_FA_TRANS")).get(0);
        assertEquals(new java.math.BigDecimal("12.50"), entities.get(account, "actualBalance"));
    }

    @Test
    void withdrawalReducesBalanceAndListTotalsUseSignedAmounts() throws Exception {
        persistAccount("MIG_FA_WITHDRAW");
        persistTransaction("MIG_FA_WITHDRAW", "MIG_FA_WITHDRAW_TX", "WITHDRAWAL", "FINACT_TRNS_APPROVED", "4.00");
        postService("updateFinAccountBalancesFromTrans", "{\"finAccountTransId\":\"MIG_FA_WITHDRAW_TX\"}")
                .andExpect(status().isOk());
        postService("getFinAccountTransListAndTotals", "{\"finAccountId\":\"MIG_FA_WITHDRAW\"}")
                .andExpect(status().isOk()).andExpect(jsonPath("$.grandTotal").value(-4.0))
                .andExpect(jsonPath("$.approvedGrandTotal").value(-4.0));
    }

    @Test
    void updateFinAccountBalancesFromAuthIncludesUnexpiredAuthorizations() throws Exception {
        persistAccount("MIG_FA_AUTH");
        Object auth = entities.newEntity(entities.entityClass("FinAccountAuth"));
        entities.set(auth, "finAccountAuthId", "MIG_FA_AUTH_ROW");
        entities.set(auth, "finAccountId", "MIG_FA_AUTH");
        entities.set(auth, "amount", new java.math.BigDecimal("3.00"));
        entityManager.persist(auth);
        entityManager.flush();
        postService("updateFinAccountBalancesFromAuth", "{\"finAccountAuthId\":\"MIG_FA_AUTH_ROW\"}")
                .andExpect(status().isOk());
        Object account = entities.findByEquality(entities.entityClass("FinAccount"),
                Map.of("finAccountId", "MIG_FA_AUTH")).get(0);
        assertEquals(new java.math.BigDecimal("-3.00"), entities.get(account, "availableBalance"));
    }

    @Test
    void depositWithdrawPaymentsRejectsAnInactiveAccount() throws Exception {
        persistAccount("MIG_FA_FROZEN");
        Object account = entities.findByEquality(entities.entityClass("FinAccount"),
                Map.of("finAccountId", "MIG_FA_FROZEN")).get(0);
        entities.set(account, "statusId", "FNACT_MANFROZEN");
        entityManager.flush();
        postService("depositWithdrawPayments", "{\"finAccountId\":\"MIG_FA_FROZEN\",\"paymentIds\":[]}")
                .andExpect(status().isBadRequest());
    }

    @Test
    void depositWithdrawPaymentsRejectsDisbursementsInAGroupedReceiptBatch() throws Exception {
        persistAccount("MIG_FA_BATCH");
        persistPayment("MIG_FA_BATCH_PAY", null);
        Object payment = entities.findByEquality(entities.entityClass("Payment"),
                Map.of("paymentId", "MIG_FA_BATCH_PAY")).get(0);
        entities.set(payment, "paymentTypeId", "SUPPLIER_PAYMENT");
        entities.set(payment, "statusId", "PMNT_SENT");
        entityManager.flush();
        postService("depositWithdrawPayments", "{\"finAccountId\":\"MIG_FA_BATCH\","
                + "\"paymentIds\":[\"MIG_FA_BATCH_PAY\"],\"groupInOneTransaction\":\"Y\"}")
                .andExpect(status().isBadRequest());
    }

    @Test
    void finAccountServicesRequireBothActionAndSensitiveAuthorities() throws Exception {
        mockMvc.perform(post("/api/accounting/services/createFinAccount")
                        .with(httpBasic("acct-clerk", "test"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"finAccountId\":\"MIG_FA_NO_SENSITIVE\"}"))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest(name = "{0} requires sensitive authority")
    @MethodSource("reconciliationReadServices")
    void reconciliationReadServicesRequireSensitiveAuthority(String serviceName) throws Exception {
        persistReconciliation("MIG_FA_SENSITIVE_REC");
        String body = "{\"glReconciliationId\":\"MIG_FA_SENSITIVE_REC\"}";
        mockMvc.perform(post("/api/accounting/services/" + serviceName)
                        .with(httpBasic("acct-clerk", "test"))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/accounting/services/" + serviceName)
                        .with(httpBasic("acct-sensitive", "test"))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("additionalFinAccountServices")
    void additionalFinAccountServicesExecuteTheirNormalPath(String serviceName) throws Exception {
        persistAccount("MIG_FA_SERVICE");
        persistTransaction("MIG_FA_SERVICE", "MIG_FA_SERVICE_TX", "DEPOSIT", "FINACT_TRNS_CREATED", "5.00");
        persistReconciliation("MIG_FA_SERVICE_REC");
        Object trans = entities.findByEquality(entities.entityClass("FinAccountTrans"),
                Map.of("finAccountTransId", "MIG_FA_SERVICE_TX")).get(0);
        entities.set(trans, "glReconciliationId", "MIG_FA_SERVICE_REC");
        persistPayment("MIG_FA_SVC_PAY", "MIG_FA_SERVICE_TX");
        persistValidStatusChange("FINACT_TRNS_CREATED", "FINACT_TRNS_APPROVED");
        entityManager.flush();

        switch (serviceName) {
            case "createFinAccountRole" -> postService(serviceName,
                    "{\"finAccountId\":\"MIG_FA_SERVICE\",\"partyId\":\"MIG_DEMO_CUSTOMER\","
                            + "\"roleTypeId\":\"OWNER\"}").andExpect(status().isOk());
            case "createFinAccountAuth" -> postService(serviceName,
                    "{\"finAccountId\":\"MIG_FA_SERVICE\",\"amount\":2.00}")
                    .andExpect(status().isOk()).andExpect(jsonPath("$.finAccountAuthId").exists());
            case "expireFinAccountAuth" -> {
                persistAuthorization("MIG_FA_SERVICE_AUTH", "MIG_FA_SERVICE");
                postService(serviceName, "{\"finAccountAuthId\":\"MIG_FA_SERVICE_AUTH\"}")
                        .andExpect(status().isOk());
            }
            case "setFinAccountTransStatus" -> postService(serviceName,
                    "{\"finAccountTransId\":\"MIG_FA_SERVICE_TX\",\"statusId\":\"FINACT_TRNS_APPROVED\"}")
                    .andExpect(status().isOk()).andExpect(jsonPath("$.oldStatusId").value("FINACT_TRNS_CREATED"));
            case "updatePaymentOnFinAccTransStatusSetToCancel" -> postService(serviceName,
                    "{\"finAccountTransId\":\"MIG_FA_SERVICE_TX\"}").andExpect(status().isOk());
            case "getFinAccountTransRunningTotalAndBalances" -> postService(serviceName,
                    "{\"finAccountTransId\":\"MIG_FA_SERVICE_TX\",\"organizationPartyId\":\"MIG_DEMO_ORG\","
                            + "\"openingBalance\":2.00,\"reconciledBalance\":3.00}")
                    .andExpect(status().isOk()).andExpect(jsonPath("$.numberOfTransactions").value(1));
            case "reconcileFinAccountTrans" -> postService(serviceName,
                    "{\"finAccountTransId\":\"MIG_FA_SERVICE_TX\",\"organizationPartyId\":\"MIG_DEMO_ORG\"}")
                    .andExpect(status().isOk());
            case "reconcileAdjustmentFinAcctgTrans", "reconcileDepositFinAcctgTrans",
                    "reconcileWithdrawalFinAcctgTrans" -> {
                String type = serviceName.equals("reconcileAdjustmentFinAcctgTrans") ? "ADJUSTMENT"
                        : serviceName.equals("reconcileDepositFinAcctgTrans") ? "DEPOSIT" : "WITHDRAWAL";
                entities.set(trans, "finAccountTransTypeId", type);
                postService(serviceName, "{\"finAccountTransId\":\"MIG_FA_SERVICE_TX\","
                        + "\"organizationPartyId\":\"MIG_DEMO_ORG\"}").andExpect(status().isOk());
            }
            case "getReconciliationClosingBalance" -> postService(serviceName,
                    "{\"glReconciliationId\":\"MIG_FA_SERVICE_REC\"}")
                    .andExpect(status().isOk()).andExpect(jsonPath("$.closingBalance").value(6.0));
            case "expirePaymentAssociationsOnFinAccountTransCancel" -> {
                persistPaymentGroupMember("MIG_FA_SVC_PAY");
                postService(serviceName, "{\"finAccountTransId\":\"MIG_FA_SERVICE_TX\"}")
                        .andExpect(status().isOk());
            }
            case "depositWithdrawPayments" -> {
                Object payment = entities.findByEquality(entities.entityClass("Payment"),
                        Map.of("paymentId", "MIG_FA_SVC_PAY")).get(0);
                entities.set(payment, "finAccountTransId", null);
                postService(serviceName, "{\"finAccountId\":\"MIG_FA_SERVICE\","
                        + "\"paymentIds\":[\"MIG_FA_SVC_PAY\"]}")
                        .andExpect(status().isOk()).andExpect(jsonPath("$.finAccountTransId").exists());
            }
            case "createPaymentAndFinAccountTrans" -> {
                Object paymentMethod = entities.findByEquality(entities.entityClass("PaymentMethod"),
                        Map.of("paymentMethodId", "MIG_DEMO_PM")).get(0);
                entities.set(paymentMethod, "finAccountId", "MIG_FA_SERVICE");
                postService(serviceName, "{\"paymentTypeId\":\"CUSTOMER_PAYMENT\","
                        + "\"paymentMethodTypeId\":\"CREDIT_CARD\",\"paymentMethodId\":\"MIG_DEMO_PM\","
                        + "\"partyIdFrom\":\"MIG_DEMO_CUSTOMER\",\"partyIdTo\":\"MIG_DEMO_VENDOR\","
                        + "\"currencyUomId\":\"USD\",\"amount\":5.00,\"isDepositWithDrawPayment\":\"Y\","
                        + "\"finAccountTransTypeId\":\"DEPOSIT\"}")
                        .andExpect(status().isOk()).andExpect(jsonPath("$.paymentId").exists());
            }
            case "getTransactionTotalByGlReconcileId" -> postService(serviceName,
                    "{\"glReconciliationId\":\"MIG_FA_SERVICE_REC\"}")
                    .andExpect(status().isOk()).andExpect(jsonPath("$.grandTotal").value(9.0));
            case "assignGlRecToFinAccTrans" -> postService(serviceName,
                    "{\"finAccountTransId\":\"MIG_FA_SERVICE_TX\","
                            + "\"glReconciliationId\":\"MIG_FA_SERVICE_REC\"}").andExpect(status().isOk());
            case "removeFinAccountTransFromReconciliation" -> postService(serviceName,
                    "{\"finAccountTransId\":\"MIG_FA_SERVICE_TX\"}").andExpect(status().isOk());
            case "isGlReconciliationReconciled" -> postService(serviceName,
                    "{\"glReconciliationId\":\"MIG_FA_SERVICE_REC\"}")
                    .andExpect(status().isOk()).andExpect(jsonPath("$.isReconciled").value(false));
            case "cancelBankReconciliation" -> postService(serviceName,
                    "{\"glReconciliationId\":\"MIG_FA_SERVICE_REC\"}").andExpect(status().isOk());
            case "getAssociatedAcctgTransEntriesWithFinAccountTrans" -> postService(serviceName,
                    "{\"finAccountTransId\":\"MIG_FA_SERVICE_TX\"}")
                    .andExpect(status().isOk()).andExpect(jsonPath("$.acctgTransAndEntries").isArray());
            case "autoFinAccountReconciliation" -> {
                Object reconciliation = entities.findByEquality(entities.entityClass("GlReconciliation"),
                        Map.of("glReconciliationId", "MIG_FA_SERVICE_REC")).get(0);
                entities.set(reconciliation, "reconciledDate", Timestamp.from(Instant.now()));
                postService(serviceName, "{}").andExpect(status().isOk());
            }
            default -> throw new IllegalArgumentException("Unhandled FinAccount service: " + serviceName);
        }
    }

    private void persistAccount(String id) {
        Object account = entities.newEntity(entities.entityClass("FinAccount"));
        entities.set(account, "finAccountId", id);
        entities.set(account, "statusId", "FNACT_ACTIVE");
        entities.set(account, "currencyUomId", "USD");
        entities.set(account, "actualBalance", new java.math.BigDecimal("0.00"));
        entities.set(account, "availableBalance", new java.math.BigDecimal("0.00"));
        entityManager.persist(account);
        entityManager.flush();
    }

    private void persistTransaction(String accountId, String id, String type, String statusId, String amount) {
        Object trans = entities.newEntity(entities.entityClass("FinAccountTrans"));
        entities.set(trans, "finAccountTransId", id);
        entities.set(trans, "finAccountId", accountId);
        entities.set(trans, "finAccountTransTypeId", type);
        entities.set(trans, "statusId", statusId);
        entities.set(trans, "amount", new java.math.BigDecimal(amount));
        entityManager.persist(trans);
        entityManager.flush();
    }

    private void persistReconciliation(String id) {
        Object reconciliation = entities.newEntity(entities.entityClass("GlReconciliation"));
        entities.set(reconciliation, "glReconciliationId", id);
        entities.set(reconciliation, "statusId", "GLREC_CREATED");
        entities.set(reconciliation, "openingBalance", new BigDecimal("2.00"));
        entities.set(reconciliation, "reconciledBalance", new BigDecimal("4.00"));
        entityManager.persist(reconciliation);
    }

    private void persistPayment(String id, String transactionId) {
        Object payment = entities.newEntity(entities.entityClass("Payment"));
        entities.set(payment, "paymentId", id);
        entities.set(payment, "paymentTypeId", "CUSTOMER_PAYMENT");
        entities.set(payment, "paymentMethodTypeId", "CREDIT_CARD");
        entities.set(payment, "partyIdFrom", "MIG_DEMO_CUSTOMER");
        entities.set(payment, "partyIdTo", "MIG_DEMO_VENDOR");
        entities.set(payment, "statusId", "PMNT_RECEIVED");
        entities.set(payment, "effectiveDate", Timestamp.from(Instant.now()));
        entities.set(payment, "amount", new BigDecimal("5.00"));
        entities.set(payment, "currencyUomId", "USD");
        entities.set(payment, "finAccountTransId", transactionId);
        entityManager.persist(payment);
    }

    private void persistAuthorization(String id, String accountId) {
        Object authorization = entities.newEntity(entities.entityClass("FinAccountAuth"));
        entities.set(authorization, "finAccountAuthId", id);
        entities.set(authorization, "finAccountId", accountId);
        entities.set(authorization, "amount", new BigDecimal("2.00"));
        entities.set(authorization, "authorizationDate", Timestamp.from(Instant.now()));
        entities.set(authorization, "fromDate", Timestamp.from(Instant.now()));
        entityManager.persist(authorization);
    }

    private void persistValidStatusChange(String from, String to) {
        if (!entities.findByEquality(entities.entityClass("StatusValidChange"),
                Map.of("statusId", from, "statusIdTo", to)).isEmpty()) {
            return;
        }
        Object change = entities.newEntity(entities.entityClass("StatusValidChange"));
        entities.set(change, "statusId", from);
        entities.set(change, "statusIdTo", to);
        entityManager.persist(change);
    }

    private void persistPaymentGroupMember(String paymentId) {
        Object group = entities.newEntity(entities.entityClass("PaymentGroup"));
        entities.set(group, "paymentGroupId", "MIG_FA_SERVICE_GROUP");
        entityManager.persist(group);
        Object member = entities.newEntity(entities.entityClass("PaymentGroupMember"));
        entities.set(member, "paymentGroupId", "MIG_FA_SERVICE_GROUP");
        entities.set(member, "paymentId", paymentId);
        entities.set(member, "fromDate", Timestamp.from(Instant.now().minusSeconds(3600)));
        entityManager.persist(member);
    }

    private static Stream<String> additionalFinAccountServices() {
        return Stream.of("createFinAccountRole", "createFinAccountAuth", "expireFinAccountAuth",
                "setFinAccountTransStatus", "updatePaymentOnFinAccTransStatusSetToCancel",
                "getFinAccountTransRunningTotalAndBalances", "reconcileFinAccountTrans",
                "reconcileAdjustmentFinAcctgTrans", "reconcileDepositFinAcctgTrans",
                "reconcileWithdrawalFinAcctgTrans", "getReconciliationClosingBalance", "depositWithdrawPayments",
                "expirePaymentAssociationsOnFinAccountTransCancel", "createPaymentAndFinAccountTrans",
                "getTransactionTotalByGlReconcileId", "assignGlRecToFinAccTrans",
                "removeFinAccountTransFromReconciliation", "isGlReconciliationReconciled",
                "cancelBankReconciliation", "getAssociatedAcctgTransEntriesWithFinAccountTrans",
                "autoFinAccountReconciliation");
    }

    private static Stream<String> reconciliationReadServices() {
        return Stream.of("getReconciliationClosingBalance", "getTransactionTotalByGlReconcileId");
    }

    private org.springframework.test.web.servlet.ResultActions postService(String name, String body)
            throws Exception {
        return mockMvc.perform(post("/api/accounting/services/" + name)
                .with(httpBasic("acct-admin", "test"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }
}
