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

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class CommitGlPostingEca {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommitGlPostingEca.class);
    private final GlPostingServiceHandlers posting;
    private final TransactionTemplate transaction;

    public CommitGlPostingEca(GlPostingServiceHandlers posting, PlatformTransactionManager manager) {
        this.posting = posting;
        this.transaction = new TransactionTemplate(manager);
        this.transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    public void invoiceReady(String invoiceId, String invoiceTypeId, String oldStatusId) {
        if (invoiceId == null
                || "INVOICE_READY".equals(oldStatusId) || "INVOICE_PAID".equals(oldStatusId)) {
            return;
        }
        if ("CUST_RTN_INVOICE".equals(invoiceTypeId)) {
            schedule(action("createAcctgTransForCustomerReturnInvoice", "invoiceId", invoiceId));
        } else {
            schedule(
                    action("createAcctgTransForPurchaseInvoice", "invoiceId", invoiceId),
                    action("createAcctgTransForSalesInvoice", "invoiceId", invoiceId));
        }
    }

    public void paymentCreated(String paymentId, String statusId) {
        if ("PMNT_RECEIVED".equals(statusId)) {
            schedule(action("createAcctgTransAndEntriesForIncomingPayment", "paymentId", paymentId));
        } else if ("PMNT_SENT".equals(statusId)) {
            schedule(action("createAcctgTransAndEntriesForOutgoingPayment", "paymentId", paymentId));
        }
    }

    public void paymentStatusChanged(String paymentId, String oldStatusId, String statusId) {
        if ("PMNT_RECEIVED".equals(statusId) && !"PMNT_RECEIVED".equals(oldStatusId)
                && !"PMNT_CONFIRMED".equals(oldStatusId)) {
            schedule(action("createAcctgTransAndEntriesForIncomingPayment", "paymentId", paymentId));
        } else if ("PMNT_SENT".equals(statusId) && !"PMNT_SENT".equals(oldStatusId)
                && !"PMNT_CONFIRMED".equals(oldStatusId)) {
            schedule(action("createAcctgTransAndEntriesForOutgoingPayment", "paymentId", paymentId));
        }
    }

    public void paymentApplicationCreated(String applicationId, String invoiceId, String paymentTypeId) {
        if (invoiceId == null || invoiceId.isBlank()) {
            return;
        }
        String service = "CUSTOMER_REFUND".equals(paymentTypeId)
                ? "createAcctgTransAndEntriesForCustomerRefundPaymentApplication"
                : "createAcctgTransAndEntriesForPaymentApplication";
        schedule(action(service, "paymentApplicationId", applicationId));
    }

    public void finAccountTransactionCreated(String transactionId, String glAccountId) {
        if (glAccountId != null && !glAccountId.isBlank()) {
            schedule(action("postFinAccountTransToGl", "finAccountTransId", transactionId,
                    "glAccountId", glAccountId));
        }
    }

    private static Action action(String service, String... keyValues) {
        java.util.LinkedHashMap<String, Object> input = new java.util.LinkedHashMap<>();
        for (int index = 0; index < keyValues.length; index += 2) {
            input.put(keyValues[index], keyValues[index + 1]);
        }
        return new Action(service, Map.copyOf(input));
    }

    private void schedule(Action... actions) {
        List<Action> work = List.of(actions);
        if (TransactionSynchronizationManager.isSynchronizationActive()
                && TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    run(work);
                }
            });
        } else {
            run(work);
        }
    }

    private void run(List<Action> actions) {
        SecurityContext previousContext = SecurityContextHolder.getContext();
        Authentication previousAuthentication = previousContext.getAuthentication();
        SecurityContext ecaContext = SecurityContextHolder.createEmptyContext();
        ecaContext.setAuthentication(new UsernamePasswordAuthenticationToken(
                "accounting-commit-eca", null, List.of(new SimpleGrantedAuthority("ACCTG_ATX_CREATE"))));
        SecurityContextHolder.setContext(ecaContext);
        try {
            for (Action action : actions) {
                try {
                    transaction.executeWithoutResult(status ->
                            posting.executeDirect(action.service(), new java.util.LinkedHashMap<>(action.input())));
                } catch (RuntimeException exception) {
                    LOGGER.error("Accounting commit ECA {} failed", action.service(), exception);
                }
            }
        } finally {
            if (previousAuthentication == null) {
                SecurityContextHolder.clearContext();
            } else {
                SecurityContext restoredContext = SecurityContextHolder.createEmptyContext();
                restoredContext.setAuthentication(previousAuthentication);
                SecurityContextHolder.setContext(restoredContext);
            }
        }
    }

    private record Action(String service, Map<String, Object> input) { }
}
