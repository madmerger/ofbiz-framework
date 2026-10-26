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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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
        MvcResult result = post("createInvoice", "{\"invoiceTypeId\":\"SALES_INVOICE\","
                + "\"partyId\":\"MIG_DEMO_CUSTOMER\",\"partyIdFrom\":\"MIG_DEMO_VENDOR\","
                + "\"currencyUomId\":\"USD\"}").andExpect(status().isOk()).andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("invoiceId").asText();
    }

    private org.springframework.test.web.servlet.ResultActions post(String service, String body) throws Exception {
        return mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .post("/api/accounting/services/" + service)
                .with(httpBasic("acct-clerk", "test"))
                .contentType(MediaType.APPLICATION_JSON).content(body));
    }
}
