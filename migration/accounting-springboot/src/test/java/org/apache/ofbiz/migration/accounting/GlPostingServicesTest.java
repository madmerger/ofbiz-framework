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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(classes = AccountingMigrationApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GlPostingServicesTest {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommitGlPostingEca postingEca;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void invoiceReadyCreatesBalancedSalesInvoiceTransactionAfterCommit() throws Exception {
        String invoiceId = createInvoice();
        post("createInvoiceItem", "{\"invoiceId\":\"" + invoiceId
                + "\",\"invoiceItemTypeId\":\"INV_PROD_ITEM\",\"productId\":\"MIG_DEMO_PRODUCT_1\","
                + "\"quantity\":\"1\",\"amount\":\"30.00\"}").andExpect(status().isOk());
        post("setInvoiceStatus", "{\"invoiceId\":\"" + invoiceId + "\",\"statusId\":\"INVOICE_READY\"}")
                .andExpect(status().isOk());

        var transactions = AccountingEntityQueries.findByEquality(entityManager,
                entities.entityClass("AcctgTrans"), Map.of("invoiceId", invoiceId));
        assertEquals(1, transactions.size());
        Object transaction = transactions.get(0);
        assertEquals("SALES_INVOICE", entities.get(transaction, "acctgTransTypeId"));
        String transactionId = entities.get(transaction, "acctgTransId").toString();
        var entries = AccountingEntityQueries.findByEquality(entityManager,
                entities.entityClass("AcctgTransEntry"), Map.of("acctgTransId", transactionId));
        BigDecimal debit = entries.stream().filter(entry -> "D".equals(entities.get(entry, "debitCreditFlag")))
                .map(entry -> (BigDecimal) entities.get(entry, "amount")).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal credit = entries.stream().filter(entry -> "C".equals(entities.get(entry, "debitCreditFlag")))
                .map(entry -> (BigDecimal) entities.get(entry, "amount")).reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(0, new BigDecimal("30.00").compareTo(debit));
        assertEquals(0, debit.compareTo(credit));
    }

    @Test
    void anAlreadyReadyInvoiceDoesNotCreateAnotherPosting() throws Exception {
        String invoiceId = createInvoice();
        post("createInvoiceItem", "{\"invoiceId\":\"" + invoiceId
                + "\",\"invoiceItemTypeId\":\"INV_PROD_ITEM\",\"productId\":\"MIG_DEMO_PRODUCT_1\","
                + "\"quantity\":\"1\",\"amount\":\"30.00\"}").andExpect(status().isOk());
        post("setInvoiceStatus", "{\"invoiceId\":\"" + invoiceId + "\",\"statusId\":\"INVOICE_READY\"}")
                .andExpect(status().isOk());
        post("setInvoiceStatus", "{\"invoiceId\":\"" + invoiceId + "\",\"statusId\":\"INVOICE_READY\"}")
                .andExpect(status().isOk());
        assertEquals(1, AccountingEntityQueries.findByEquality(entityManager,
                entities.entityClass("AcctgTrans"), Map.of("invoiceId", invoiceId)).size());
    }

    @Test
    void receivedPaymentAndApplicationCreateTheirAccountingTransactions() throws Exception {
        String invoiceId = createInvoice();
        post("createInvoiceItem", "{\"invoiceId\":\"" + invoiceId
                + "\",\"invoiceItemTypeId\":\"INV_PROD_ITEM\",\"productId\":\"MIG_DEMO_PRODUCT_1\","
                + "\"quantity\":\"1\",\"amount\":\"30.00\"}").andExpect(status().isOk());
        MvcResult payment = post("createPayment", "{\"paymentTypeId\":\"CUSTOMER_PAYMENT\","
                + "\"paymentMethodTypeId\":\"CREDIT_CARD\",\"paymentMethodId\":\"MIG_DEMO_PM\","
                + "\"partyIdFrom\":\"MIG_DEMO_CUSTOMER\",\"partyIdTo\":\"MIG_DEMO_VENDOR\","
                + "\"currencyUomId\":\"USD\",\"amount\":\"30.00\",\"statusId\":\"PMNT_RECEIVED\"}")
                .andExpect(status().isOk()).andReturn();
        String paymentId = objectMapper.readTree(payment.getResponse().getContentAsString())
                .get("paymentId").asText();
        post("createPaymentApplication", "{\"invoiceId\":\"" + invoiceId
                + "\",\"paymentId\":\"" + paymentId + "\"}").andExpect(status().isOk());

        var transactions = AccountingEntityQueries.findByEquality(entityManager,
                entities.entityClass("AcctgTrans"), Map.of("paymentId", paymentId));
        assertEquals(2, transactions.size());
        assertEquals(1, transactions.stream()
                .filter(transaction -> "INCOMING_PAYMENT".equals(entities.get(transaction, "acctgTransTypeId")))
                .count());
        assertEquals(1, transactions.stream()
                .filter(transaction -> "PAYMENT_APPL".equals(entities.get(transaction, "acctgTransTypeId")))
                .count());
    }

    @Test
    void createAcctgTransForSalesInvoiceCreatesBalancedSalesPosting() throws Exception {
        String invoiceId = createInvoice("SALES_INVOICE", "MIG_DEMO_CUSTOMER", "MIG_DEMO_VENDOR");
        createInvoiceItem(invoiceId);

        postGl("createAcctgTransForSalesInvoice", "{\"invoiceId\":\"" + invoiceId + "\"}")
                .andExpect(status().isOk());

        assertPosting("SALES_INVOICE", "invoiceId", invoiceId, new BigDecimal("30.00"), List.of(
                new EntryExpectation("INV_PROD_ITEM", "C"),
                new EntryExpectation("ACCOUNTS_RECEIVABLE", "D")));
    }

    @Test
    void createAcctgTransForPurchaseInvoiceCreatesBalancedPurchasePosting() throws Exception {
        String invoiceId = createInvoice("PURCHASE_INVOICE", "MIG_DEMO_VENDOR", "MIG_DEMO_CUSTOMER");
        createInvoiceItem(invoiceId);

        postGl("createAcctgTransForPurchaseInvoice", "{\"invoiceId\":\"" + invoiceId + "\"}")
                .andExpect(status().isOk());

        assertPosting("PURCHASE_INVOICE", "invoiceId", invoiceId, new BigDecimal("30.00"), List.of(
                new EntryExpectation("INV_PROD_ITEM", "D"),
                new EntryExpectation("ACCOUNTS_PAYABLE", "C")));
    }

    @Test
    void createAcctgTransForCustomerReturnInvoiceCreatesBalancedReturnPosting() throws Exception {
        String invoiceId = createInvoice("CUST_RTN_INVOICE", "MIG_DEMO_VENDOR", "MIG_DEMO_CUSTOMER");
        createInvoiceItem(invoiceId);

        postGl("createAcctgTransForCustomerReturnInvoice", "{\"invoiceId\":\"" + invoiceId + "\"}")
                .andExpect(status().isOk());

        assertPosting("CUST_RTN_INVOICE", "invoiceId", invoiceId, new BigDecimal("30.00"), List.of(
                new EntryExpectation("INV_PROD_ITEM", "D"),
                new EntryExpectation("ACCOUNTS_RECEIVABLE", "C")));
    }

    @Test
    void createAcctgTransAndEntriesForIncomingPaymentCreatesBalancedReceiptPosting() throws Exception {
        String paymentId = id("MIG_GL_INCOMING");
        persistPayment(paymentId, "CUSTOMER_PAYMENT", "MIG_DEMO_CUSTOMER",
                "MIG_DEMO_VENDOR", "PMNT_RECEIVED", "30.00");

        postGl("createAcctgTransAndEntriesForIncomingPayment", "{\"paymentId\":\"" + paymentId + "\"}")
                .andExpect(status().isOk());

        assertPosting("INCOMING_PAYMENT", "paymentId", paymentId, new BigDecimal("30.00"), List.of(
                new EntryExpectation(null, "D"),
                new EntryExpectation("ACCREC_UNAPPLIED", "C")));
    }

    @Test
    void createAcctgTransAndEntriesForOutgoingPaymentCreatesBalancedDisbursementPosting() throws Exception {
        String paymentId = id("MIG_GL_OUTGOING");
        ensurePaymentMap("SUPPLIER_PAYMENT", "ACCOUNTS_PAYABLE");
        persistPayment(paymentId, "SUPPLIER_PAYMENT", "MIG_DEMO_VENDOR",
                "MIG_DEMO_CUSTOMER", "PMNT_SENT", "30.00");

        postGl("createAcctgTransAndEntriesForOutgoingPayment", "{\"paymentId\":\"" + paymentId + "\"}")
                .andExpect(status().isOk());

        assertPosting("OUTGOING_PAYMENT", "paymentId", paymentId, new BigDecimal("30.00"), List.of(
                new EntryExpectation("ACCOUNTS_PAYABLE", "D"),
                new EntryExpectation(null, "C")));
    }

    @Test
    void createAcctgTransAndEntriesForPaymentApplicationCreatesBalancedApplicationPosting() throws Exception {
        String invoiceId = createInvoice();
        String paymentId = id("MIG_GL_APP");
        persistPayment(paymentId, "CUSTOMER_PAYMENT", "MIG_DEMO_CUSTOMER",
                "MIG_DEMO_VENDOR", "PMNT_RECEIVED", "30.00");
        String applicationId = persistPaymentApplication(paymentId, invoiceId, "30.00");

        postGl("createAcctgTransAndEntriesForPaymentApplication",
                "{\"paymentApplicationId\":\"" + applicationId + "\"}").andExpect(status().isOk());

        assertPosting("PAYMENT_APPL", "paymentId", paymentId, new BigDecimal("30.00"), List.of(
                new EntryExpectation("ACCREC_UNAPPLIED", "D"),
                new EntryExpectation("ACCOUNTS_RECEIVABLE", "C")));
    }

    @Test
    void createAcctgTransAndEntriesForCustomerRefundPaymentApplicationCreatesBalancedRefundPosting()
            throws Exception {
        String invoiceId = createInvoice();
        String paymentId = id("MIG_GL_REFUND");
        ensurePaymentMap("CUSTOMER_REFUND", "ACCREC_UNAPPLIED");
        persistPayment(paymentId, "CUSTOMER_REFUND", "MIG_DEMO_VENDOR",
                "MIG_DEMO_CUSTOMER", "PMNT_RECEIVED", "30.00");
        String applicationId = persistPaymentApplication(paymentId, invoiceId, "30.00");

        postGl("createAcctgTransAndEntriesForCustomerRefundPaymentApplication",
                "{\"paymentApplicationId\":\"" + applicationId + "\"}").andExpect(status().isOk());

        assertPosting("PAYMENT_APPL", "paymentId", paymentId, new BigDecimal("30.00"), List.of(
                new EntryExpectation("ACCOUNTS_RECEIVABLE", "D"),
                new EntryExpectation("ACCREC_UNAPPLIED", "C")));
    }

    @Test
    void paymentApplicationEcaSelectsTheCustomerRefundService() throws Exception {
        String invoiceId = createInvoice();
        ensurePaymentMap("CUSTOMER_REFUND", "ACCREC_UNAPPLIED");
        String refundPaymentId = id("MIG_GL_REFUND_ECA");
        persistPayment(refundPaymentId, "CUSTOMER_REFUND", "MIG_DEMO_VENDOR",
                "MIG_DEMO_CUSTOMER", "PMNT_RECEIVED", "30.00");
        String refundApplicationId = persistPaymentApplication(refundPaymentId, invoiceId, "30.00");

        postingEca.paymentApplicationCreated(refundApplicationId, invoiceId, "CUSTOMER_REFUND");

        assertPosting("PAYMENT_APPL", "paymentId", refundPaymentId, new BigDecimal("30.00"), List.of(
                new EntryExpectation("ACCOUNTS_RECEIVABLE", "D"),
                new EntryExpectation("ACCREC_UNAPPLIED", "C")));

        String paymentId = id("MIG_GL_STANDARD_ECA");
        persistPayment(paymentId, "CUSTOMER_PAYMENT", "MIG_DEMO_CUSTOMER",
                "MIG_DEMO_VENDOR", "PMNT_RECEIVED", "30.00");
        String applicationId = persistPaymentApplication(paymentId, invoiceId, "30.00");
        postingEca.paymentApplicationCreated(applicationId, invoiceId, "CUSTOMER_PAYMENT");

        assertPosting("PAYMENT_APPL", "paymentId", paymentId, new BigDecimal("30.00"), List.of(
                new EntryExpectation("ACCREC_UNAPPLIED", "D"),
                new EntryExpectation("ACCOUNTS_RECEIVABLE", "C")));
    }

    @Test
    void finAccountTransactionPostsOnlyWhenAnOffsetGlAccountIsProvided() throws Exception {
        String accountWithGl = persistFinAccount();
        String postedId = persistFinAccountTransaction(accountWithGl);
        postingEca.finAccountTransactionCreated(postedId, "126000");
        assertPosting("RECEIPT", "finAccountTransId", postedId, new BigDecimal("30.00"), List.of(
                new EntryExpectation(null, "D"),
                new EntryExpectation(null, "C")));

        String accountWithoutGl = persistFinAccount();
        String notPostedId = persistFinAccountTransaction(accountWithoutGl);
        postingEca.finAccountTransactionCreated(notPostedId, null);
        assertEquals(0, AccountingEntityQueries.findByEquality(entityManager,
                entities.entityClass("AcctgTrans"), Map.of("finAccountTransId", notPostedId)).size());
    }

    @Test
    void revertAcctgTransOnCancelInvoiceCreatesAnExactReversal() throws Exception {
        String invoiceId = createInvoice();
        String sourceId = persistSourceTransaction(invoiceId, null, "SALES_INVOICE");

        postGl("revertAcctgTransOnCancelInvoice", "{\"invoiceId\":\"" + invoiceId + "\"}")
                .andExpect(status().isOk());

        assertReversal(sourceId, invoiceId);
    }

    @Test
    void revertAcctgTransOnRemovePaymentApplicationsCreatesAnExactReversal() throws Exception {
        String invoiceId = createInvoice();
        String paymentId = id("MIG_GL_REVERT_APP");
        persistPayment(paymentId, "CUSTOMER_PAYMENT", "MIG_DEMO_CUSTOMER",
                "MIG_DEMO_VENDOR", "PMNT_RECEIVED", "30.00");
        String applicationId = persistPaymentApplication(paymentId, invoiceId, "30.00");
        String sourceId = persistSourceTransaction(invoiceId, paymentId, "PAYMENT_APPL");

        postGl("revertAcctgTransOnRemovePaymentApplications",
                "{\"paymentApplicationId\":\"" + applicationId + "\"}").andExpect(status().isOk());

        assertReversal(sourceId, invoiceId);
    }

    @Test
    void nonInternalOrganizationDoesNotCreateAnAccountingTransaction() throws Exception {
        String invoiceId = createInvoice("SALES_INVOICE", "MIG_DEMO_CUSTOMER", "MIG_DEMO_CUSTOMER");
        createInvoiceItem(invoiceId);

        postGl("createAcctgTransForSalesInvoice", "{\"invoiceId\":\"" + invoiceId + "\"}")
                .andExpect(status().isOk());

        assertEquals(0, AccountingEntityQueries.findByEquality(entityManager,
                entities.entityClass("AcctgTrans"), Map.of("invoiceId", invoiceId)).size());
    }

    @Test
    void commitPostingFailuresAreIsolatedFromTheCallingOperation() {
        assertDoesNotThrow(() -> postingEca.paymentCreated("MISSING_PAYMENT", "PMNT_RECEIVED"));
    }

    @Test
    void commitPostingRestoresTheCallingSecurityContext() {
        var caller = new UsernamePasswordAuthenticationToken(
                "acct-clerk", null, List.of(new SimpleGrantedAuthority("ACCOUNTING_CREATE")));
        SecurityContextHolder.getContext().setAuthentication(caller);
        try {
            postingEca.paymentCreated("MISSING_PAYMENT", "PMNT_RECEIVED");
            assertSame(caller, SecurityContextHolder.getContext().getAuthentication());
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private String createInvoice() throws Exception {
        return createInvoice("SALES_INVOICE", "MIG_DEMO_CUSTOMER", "MIG_DEMO_VENDOR");
    }

    private String createInvoice(String type, String partyId, String partyIdFrom) throws Exception {
        MvcResult result = post("createInvoice", "{\"invoiceTypeId\":\"" + type
                + "\",\"partyId\":\"" + partyId + "\",\"partyIdFrom\":\"" + partyIdFrom
                + "\",\"currencyUomId\":\"USD\"}")
                .andExpect(status().isOk()).andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("invoiceId").asText();
    }

    private void createInvoiceItem(String invoiceId) throws Exception {
        post("createInvoiceItem", "{\"invoiceId\":\"" + invoiceId
                + "\",\"invoiceItemTypeId\":\"INV_PROD_ITEM\",\"productId\":\"MIG_DEMO_PRODUCT_1\","
                + "\"quantity\":\"1\",\"amount\":\"30.00\"}").andExpect(status().isOk());
    }

    private void persistPayment(String paymentId, String paymentTypeId, String partyIdFrom,
            String partyIdTo, String statusId, String amount) {
        inTransaction(() -> {
            Object payment = entities.newEntity(entities.entityClass("Payment"));
            entities.set(payment, "paymentId", paymentId);
            entities.set(payment, "paymentTypeId", paymentTypeId);
            entities.set(payment, "paymentMethodTypeId", "CREDIT_CARD");
            entities.set(payment, "paymentMethodId", "MIG_DEMO_PM");
            entities.set(payment, "partyIdFrom", partyIdFrom);
            entities.set(payment, "partyIdTo", partyIdTo);
            entities.set(payment, "statusId", statusId);
            entities.set(payment, "effectiveDate", Timestamp.from(Instant.now()));
            entities.set(payment, "amount", new BigDecimal(amount));
            entities.set(payment, "currencyUomId", "USD");
            entities.set(payment, "actualCurrencyAmount", new BigDecimal(amount));
            entities.set(payment, "actualCurrencyUomId", "USD");
            entityManager.persist(payment);
            entityManager.flush();
        });
    }

    private void ensurePaymentMap(String paymentTypeId, String glAccountTypeId) {
        Map<String, Object> key = Map.of(
                "organizationPartyId", "MIG_DEMO_VENDOR", "paymentTypeId", paymentTypeId);
        List<?> matches = AccountingEntityQueries.findByEquality(entityManager,
                entities.entityClass("PaymentGlAccountTypeMap"), key);
        if (matches.isEmpty()) {
            inTransaction(() -> {
                Object mapping = entities.newEntity(entities.entityClass("PaymentGlAccountTypeMap"));
                entities.set(mapping, "organizationPartyId", "MIG_DEMO_VENDOR");
                entities.set(mapping, "paymentTypeId", paymentTypeId);
                entities.set(mapping, "glAccountTypeId", glAccountTypeId);
                entityManager.persist(mapping);
                entityManager.flush();
            });
        } else {
            assertEquals(glAccountTypeId, entities.get(matches.get(0), "glAccountTypeId"));
        }
    }

    private String persistPaymentApplication(String paymentId, String invoiceId, String amount) {
        String applicationId = id("MIG_GL_APP");
        inTransaction(() -> {
            Object application = entities.newEntity(entities.entityClass("PaymentApplication"));
            entities.set(application, "paymentApplicationId", applicationId);
            entities.set(application, "paymentId", paymentId);
            entities.set(application, "invoiceId", invoiceId);
            entities.set(application, "amountApplied", new BigDecimal(amount));
            entityManager.persist(application);
            entityManager.flush();
        });
        return applicationId;
    }

    private String persistFinAccount() {
        String accountId = id("MIG_GL_FA");
        inTransaction(() -> {
            Object account = entities.newEntity(entities.entityClass("FinAccount"));
            entities.set(account, "finAccountId", accountId);
            entities.set(account, "statusId", "FNACT_ACTIVE");
            entities.set(account, "organizationPartyId", "MIG_DEMO_VENDOR");
            entities.set(account, "currencyUomId", "USD");
            entities.set(account, "actualBalance", BigDecimal.ZERO);
            entities.set(account, "availableBalance", BigDecimal.ZERO);
            entities.set(account, "postToGlAccountId", "120000");
            entityManager.persist(account);
            entityManager.flush();
        });
        return accountId;
    }

    private String persistFinAccountTransaction(String accountId) {
        String transactionId = id("MIG");
        inTransaction(() -> {
            Object transaction = entities.newEntity(entities.entityClass("FinAccountTrans"));
            entities.set(transaction, "finAccountTransId", transactionId);
            entities.set(transaction, "finAccountId", accountId);
            entities.set(transaction, "finAccountTransTypeId", "DEPOSIT");
            entities.set(transaction, "statusId", "FINACT_TRNS_APPROVED");
            entities.set(transaction, "amount", new BigDecimal("30.00"));
            entities.set(transaction, "entryDate", Timestamp.from(Instant.now()));
            entities.set(transaction, "transactionDate", Timestamp.from(Instant.now()));
            entityManager.persist(transaction);
            entityManager.flush();
        });
        return transactionId;
    }

    private String persistSourceTransaction(String invoiceId, String paymentId, String type) {
        String transactionId = id("MIG_GL_SOURCE");
        inTransaction(() -> {
            Object transaction = entities.newEntity(entities.entityClass("AcctgTrans"));
            entities.set(transaction, "acctgTransId", transactionId);
            entities.set(transaction, "acctgTransTypeId", type);
            entities.set(transaction, "glFiscalTypeId", "ACTUAL");
            entities.set(transaction, "transactionDate", Timestamp.from(Instant.now()));
            entities.set(transaction, "invoiceId", invoiceId);
            entities.set(transaction, "paymentId", paymentId);
            entities.set(transaction, "isPosted", "N");
            entityManager.persist(transaction);
            persistSourceEntry(transactionId, "00001", "D", "400000");
            persistSourceEntry(transactionId, "00002", "C", "120000");
            entityManager.flush();
        });
        return transactionId;
    }

    private void persistSourceEntry(String transactionId, String sequenceId, String flag, String accountId) {
        Object entry = entities.newEntity(entities.entityClass("AcctgTransEntry"));
        entities.set(entry, "acctgTransId", transactionId);
        entities.set(entry, "acctgTransEntrySeqId", sequenceId);
        entities.set(entry, "debitCreditFlag", flag);
        entities.set(entry, "glAccountId", accountId);
        entities.set(entry, "glAccountTypeId", "SALES_ACCOUNT");
        entities.set(entry, "organizationPartyId", "MIG_DEMO_VENDOR");
        entities.set(entry, "currencyUomId", "USD");
        entities.set(entry, "origCurrencyUomId", "USD");
        entities.set(entry, "amount", new BigDecimal("30.00"));
        entities.set(entry, "origAmount", new BigDecimal("30.00"));
        entityManager.persist(entry);
    }

    private void assertPosting(String type, String sourceField, String sourceId, BigDecimal total,
            List<EntryExpectation> expectedEntries) {
        List<?> transactions = AccountingEntityQueries.findByEquality(entityManager,
                entities.entityClass("AcctgTrans"), Map.of(sourceField, sourceId));
        assertEquals(1, transactions.size());
        Object transaction = transactions.get(0);
        assertEquals(type, entities.get(transaction, "acctgTransTypeId"));
        assertEquals("ACTUAL", entities.get(transaction, "glFiscalTypeId"));
        String transactionId = entities.get(transaction, "acctgTransId").toString();
        List<?> entries = AccountingEntityQueries.findByEquality(entityManager,
                entities.entityClass("AcctgTransEntry"), Map.of("acctgTransId", transactionId));
        assertEquals(expectedEntries.size(), entries.size());

        BigDecimal debit = BigDecimal.ZERO;
        BigDecimal credit = BigDecimal.ZERO;
        for (Object entry : entries) {
            BigDecimal amount = new BigDecimal(entities.get(entry, "amount").toString());
            if ("D".equals(entities.get(entry, "debitCreditFlag"))) {
                debit = debit.add(amount);
            } else if ("C".equals(entities.get(entry, "debitCreditFlag"))) {
                credit = credit.add(amount);
            }
        }
        assertEquals(0, total.compareTo(debit));
        assertEquals(0, debit.compareTo(credit));
        for (EntryExpectation expected : expectedEntries) {
            long matches = entries.stream().filter(entry ->
                    Objects.equals(expected.glAccountTypeId(), entities.get(entry, "glAccountTypeId"))
                            && expected.debitCreditFlag().equals(entities.get(entry, "debitCreditFlag"))
                            && total.compareTo(new BigDecimal(entities.get(entry, "amount").toString())) == 0)
                    .count();
            assertEquals(1, matches, "Expected " + expected + " entry in " + type);
        }
    }

    private void assertReversal(String sourceId, String invoiceId) {
        List<?> transactions = AccountingEntityQueries.findByEquality(entityManager,
                entities.entityClass("AcctgTrans"), Map.of("invoiceId", invoiceId));
        Object source = AccountingEntityQueries.findByEquality(entityManager,
                entities.entityClass("AcctgTrans"), Map.of("acctgTransId", sourceId)).get(0);
        Object reversal = transactions.stream()
                .filter(transaction -> !sourceId.equals(entities.get(transaction, "acctgTransId")))
                .findFirst().orElse(null);
        assertNotNull(reversal);
        assertEquals(entities.get(source, "acctgTransTypeId"), entities.get(reversal, "acctgTransTypeId"));
        assertEquals(entities.get(source, "glFiscalTypeId"), entities.get(reversal, "glFiscalTypeId"));
        List<?> originalEntries = AccountingEntityQueries.findByEquality(entityManager,
                entities.entityClass("AcctgTransEntry"), Map.of("acctgTransId", sourceId));
        List<?> reversalEntries = AccountingEntityQueries.findByEquality(entityManager,
                entities.entityClass("AcctgTransEntry"),
                Map.of("acctgTransId", entities.get(reversal, "acctgTransId")));
        assertEquals(originalEntries.size(), reversalEntries.size());
        for (Object original : originalEntries) {
            String opposite = "D".equals(entities.get(original, "debitCreditFlag")) ? "C" : "D";
            assertEquals(1, reversalEntries.stream().filter(entry ->
                    opposite.equals(entities.get(entry, "debitCreditFlag"))
                            && Objects.equals(entities.get(original, "glAccountId"),
                                    entities.get(entry, "glAccountId"))
                            && Objects.equals(entities.get(original, "amount"), entities.get(entry, "amount")))
                    .count());
        }
    }

    private static String id(String prefix) {
        return "MIG" + UUID.randomUUID().toString().replace("-", "").substring(0, 17);
    }

    private void inTransaction(Runnable work) {
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> work.run());
    }

    private org.springframework.test.web.servlet.ResultActions postGl(String service, String body) throws Exception {
        return mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .post("/api/accounting/services/" + service)
                .with(httpBasic("acct-admin", "test"))
                .contentType(MediaType.APPLICATION_JSON).content(body));
    }

    private org.springframework.test.web.servlet.ResultActions post(String service, String body) throws Exception {
        return mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .post("/api/accounting/services/" + service)
                .with(httpBasic("acct-clerk", "test"))
                .contentType(MediaType.APPLICATION_JSON).content(body));
    }

    private record EntryExpectation(String glAccountTypeId, String debitCreditFlag) { }
}
