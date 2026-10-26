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

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.common.SequenceService;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FinAccountServiceHandlers {
    private static final String COMPONENT = "accounting";
    private static final String SOURCE = "applications/accounting/minilang/finaccount/FinAccountServices.xml";
    private final EntityManager entityManager;
    private final EntityRegistry entities;
    private final SequenceService sequences;
    private final MigrationServiceDispatcher dispatcher;
    private final ServiceHandlerRegistry registry;

    public FinAccountServiceHandlers(
            EntityManager entityManager,
            EntityRegistry entities,
            SequenceService sequences,
            MigrationServiceDispatcher dispatcher,
            ServiceHandlerRegistry registry) {
        this.entityManager = entityManager;
        this.entities = entities;
        this.sequences = sequences;
        this.dispatcher = dispatcher;
        this.registry = registry;
    }

    @PostConstruct
    void registerHandlers() {
        register("createFinAccount", "CREATE", "FinAccount", this::createFinAccount);
        register("updateFinAccount", "UPDATE", "FinAccount", this::updateFinAccount);
        register("updateFinAccountBalancesFromTrans", "UPDATE", "FinAccount", this::updateBalancesFromTrans);
        register("updateFinAccountBalancesFromAuth", "UPDATE", "FinAccount", this::updateBalancesFromAuth);
        register("createFinAccountTrans", "CREATE", "FinAccountTrans", this::createFinAccountTrans);
        register("createFinAccountRole", "CREATE", "FinAccountRole", this::createFinAccountRole);
        register("createFinAccountAuth", "CREATE", "FinAccountAuth", this::createFinAccountAuth);
        register("expireFinAccountAuth", "UPDATE", "FinAccountAuth", this::expireFinAccountAuth);
        register("setFinAccountTransStatus", "UPDATE", "FinAccountTrans", this::setFinAccountTransStatus);
        register("updatePaymentOnFinAccTransStatusSetToCancel", "UPDATE", "FinAccountTrans",
                this::updatePaymentOnFinAccTransStatusSetToCancel);
        register("getFinAccountTransListAndTotals", "VIEW", "FinAccountTrans", this::getFinAccountTransListAndTotals);
        register("getFinAccountTransRunningTotalAndBalances", "VIEW", "FinAccountTrans",
                this::getFinAccountTransRunningTotalAndBalances);
        register("reconcileFinAccountTrans", "UPDATE", "FinAccountTrans", this::reconcileFinAccountTrans);
        register("reconcileAdjustmentFinAcctgTrans", "UPDATE", "FinAccountTrans",
                this::reconcileAdjustmentFinAcctgTrans);
        register("reconcileDepositFinAcctgTrans", "UPDATE", "FinAccountTrans", this::reconcileDepositFinAcctgTrans);
        register("reconcileWithdrawalFinAcctgTrans", "UPDATE", "FinAccountTrans",
                this::reconcileWithdrawalFinAcctgTrans);
        register("getReconciliationClosingBalance", "VIEW", "FinAccountTrans", this::getReconciliationClosingBalance);
        register("depositWithdrawPayments", "CREATE", "FinAccount", this::depositWithdrawPayments);
        register("expirePaymentAssociationsOnFinAccountTransCancel", "UPDATE", "FinAccountTrans",
                this::expirePaymentAssociationsOnFinAccountTransCancel);
        register("createPaymentAndFinAccountTrans", "CREATE", "FinAccountTrans", this::createPaymentAndFinAccountTrans);
        register("getTransactionTotalByGlReconcileId", "VIEW", "FinAccountTrans",
                this::getTransactionTotalByGlReconcileId);
        register("assignGlRecToFinAccTrans", "UPDATE", "FinAccountTrans", this::assignGlRecToFinAccTrans);
        register("removeFinAccountTransFromReconciliation", "UPDATE", "FinAccountTrans",
                this::removeFinAccountTransFromReconciliation);
        register("isGlReconciliationReconciled", "VIEW", "FinAccountTrans", this::isGlReconciliationReconciled);
        register("cancelBankReconciliation", "UPDATE", "FinAccountTrans", this::cancelBankReconciliation);
        register("getAssociatedAcctgTransEntriesWithFinAccountTrans", "VIEW", "FinAccountTrans",
                this::getAssociatedAcctgTransEntriesWithFinAccountTrans);
        register("autoFinAccountReconciliation", "UPDATE", "FinAccountTrans", this::autoFinAccountReconciliation);
    }

    private void register(
            String name, String permission, String sensitiveEntity, ServiceHandlerRegistry.SimpleServiceHandler handler) {
        registry.registerHandler(COMPONENT, name, permission, sensitiveEntity, handler);
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountServices.xml:30. */
    private Map<String, Object> createFinAccount(Map<String, Object> input) {
        String id = text(input.get("finAccountId"));
        if (id == null) {
            id = sequences.nextId("FinAccount");
        }
        if (one("FinAccount", Map.of("finAccountId", id)) != null) {
            throw new MigrationException(HttpStatus.CONFLICT, "FinAccount already exists");
        }
        Object account = newEntity("FinAccount");
        copy(account, input, Set.of("finAccountId", "actualBalance", "availableBalance"));
        entities.set(account, "finAccountId", id);
        defaultValue(account, "statusId", "FNACT_ACTIVE");
        defaultValue(account, "actualBalance", BigDecimal.ZERO);
        defaultValue(account, "availableBalance", BigDecimal.ZERO);
        if (blank(entities.get(account, "currencyUomId"))) {
            entities.set(account, "currencyUomId", "USD");
        }
        if (blank(entities.get(account, "isRefundable")) && !blank(input.get("finAccountTypeId"))) {
            Object accountType = one("FinAccountType", Map.of(
                    "finAccountTypeId", text(input.get("finAccountTypeId"))));
            if (accountType != null && "Y".equals(entities.get(accountType, "isRefundable"))) {
                entities.set(account, "isRefundable", "Y");
            }
        }
        roundField(account, "replenishLevel");
        entityManager.persist(account);
        createStatus(id, text(entities.get(account, "statusId")), null);
        return Map.of("finAccountId", id);
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountServices.xml:86. */
    private Map<String, Object> updateFinAccount(Map<String, Object> input) {
        Object account = requiredOne("FinAccount", "finAccountId", input);
        String oldStatus = text(entities.get(account, "statusId"));
        Object statusValue = input.get("statusId");
        if (statusValue != null && !statusValue.toString().equals(oldStatus)) {
            requireStatusChange(oldStatus, statusValue.toString());
            entities.set(account, "statusId", statusValue);
            createStatus(text(input.get("finAccountId")), statusValue.toString(), oldStatus);
        } else if ("FNACT_MANFROZEN".equals(oldStatus) || "FNACT_CANCELLED".equals(oldStatus)) {
            throw badRequest("FinAccount status does not allow updates");
        }
        String oldReplenishPayment = text(entities.get(account, "replenishPaymentId"));
        Object oldReplenishLevel = entities.get(account, "replenishLevel");
        copy(account, input, Set.of("actualBalance", "availableBalance"));
        roundField(account, "replenishLevel");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("finAccountId", entities.get(account, "finAccountId"));
        result.put("oldStatusId", oldStatus);
        result.put("oldReplenishPaymentId", oldReplenishPayment);
        result.put("oldReplenishLevel", oldReplenishLevel);
        result.put("replenishPaymentId", entities.get(account, "replenishPaymentId"));
        result.put("replenishLevel", entities.get(account, "replenishLevel"));
        return result;
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountServices.xml:151. */
    private Map<String, Object> createFinAccountTrans(Map<String, Object> input) {
        String id = sequences.nextId("FinAccountTrans");
        BigDecimal amount = new BigDecimal(required(input, "amount"));
        Object account = requiredOne("FinAccount", "finAccountId", input);
        requireActiveForTransaction(account);
        Object trans = newEntity("FinAccountTrans");
        copy(trans, input, Set.of("finAccountTransId", "performedByPartyId"));
        entities.set(trans, "finAccountTransId", id);
        entities.set(trans, "finAccountId", input.get("finAccountId"));
        entities.set(trans, "statusId", input.getOrDefault("statusId", "FINACT_TRNS_APPROVED"));
        entities.set(trans, "entryDate", input.getOrDefault("entryDate", now()));
        entities.set(trans, "transactionDate", input.getOrDefault("transactionDate", now()));
        entities.set(trans, "performedByPartyId", currentUserPartyId());
        entities.set(trans, "amount", round(amount));
        entityManager.persist(trans);
        return Map.of("finAccountTransId", id);
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountServices.xml:196. */
    private Map<String, Object> createFinAccountRole(Map<String, Object> input) {
        Object account = requiredOne("FinAccount", "finAccountId", input);
        requireActiveForTransaction(account);
        Object role = newEntity("FinAccountRole");
        copy(role, input, Set.of());
        entities.set(role, "fromDate", input.getOrDefault("fromDate", now()));
        entityManager.persist(role);
        return Map.of();
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountServices.xml:221. */
    private Map<String, Object> createFinAccountAuth(Map<String, Object> input) {
        String id = sequences.nextId("FinAccountAuth");
        requiredOne("FinAccount", "finAccountId", input);
        BigDecimal amount = new BigDecimal(required(input, "amount"));
        Object auth = newEntity("FinAccountAuth");
        copy(auth, input, Set.of("finAccountAuthId"));
        entities.set(auth, "finAccountAuthId", id);
        entities.set(auth, "authorizationDate", input.getOrDefault("authorizationDate", now()));
        entities.set(auth, "fromDate", input.getOrDefault("fromDate", now()));
        entities.set(auth, "amount", round(amount));
        entityManager.persist(auth);
        return Map.of("finAccountAuthId", id);
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountServices.xml:249. */
    private Map<String, Object> expireFinAccountAuth(Map<String, Object> input) {
        Object auth = requiredOne("FinAccountAuth", "finAccountAuthId", input);
        entities.set(auth, "thruDate", input.getOrDefault("expireDateTime", now()));
        return Map.of();
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountServices.xml:261. */
    private Map<String, Object> updateBalancesFromTrans(Map<String, Object> input) {
        String accountId = text(input.get("finAccountId"));
        if (accountId == null) {
            Object trans = requiredOne("FinAccountTrans", "finAccountTransId", input);
            accountId = text(entities.get(trans, "finAccountId"));
        }
        recalculateBalances(accountId);
        return Map.of();
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountServices.xml:272. */
    private Map<String, Object> updateBalancesFromAuth(Map<String, Object> input) {
        String accountId = text(input.get("finAccountId"));
        if (accountId == null) {
            Object auth = requiredOne("FinAccountAuth", "finAccountAuthId", input);
            accountId = text(entities.get(auth, "finAccountId"));
        }
        recalculateBalances(accountId);
        return Map.of();
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountServices.xml:436. */
    private Map<String, Object> setFinAccountTransStatus(Map<String, Object> input) {
        Object trans = requiredOne("FinAccountTrans", "finAccountTransId", input);
        String oldStatus = text(entities.get(trans, "statusId"));
        String newStatus = required(input, "statusId");
        if (!same(oldStatus, newStatus)) {
            Object change = one("StatusValidChange", Map.of("statusId", oldStatus, "statusIdTo", newStatus));
            if (change == null) {
                throw badRequest("Invalid FinAccount transaction status change");
            }
            entities.set(trans, "statusId", newStatus);
        }
        return Map.of("oldStatusId", oldStatus);
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountServices.xml:458. */
    private Map<String, Object> updatePaymentOnFinAccTransStatusSetToCancel(Map<String, Object> input) {
        Object trans = requiredOne("FinAccountTrans", "finAccountTransId", input);
        for (Object payment : paymentsFor(trans)) {
            entities.set(payment, "finAccountTransId", null);
        }
        return Map.of();
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountServices.xml:500. */
    private Map<String, Object> getFinAccountTransListAndTotals(Map<String, Object> input) {
        String accountId = required(input, "finAccountId");
        List<?> all = entities.findByEquality(entities.entityClass("FinAccountTrans"), Map.of("finAccountId", accountId));
        List<?> matched = all.stream().filter(value -> matchesTransaction(value, input)).sorted(
                Comparator.comparing(value -> date(entities.get(value, "transactionDate")),
                        Comparator.nullsFirst(Comparator.naturalOrder()))).toList();
        BigDecimal grand = BigDecimal.ZERO;
        BigDecimal created = BigDecimal.ZERO;
        BigDecimal approved = BigDecimal.ZERO;
        BigDecimal reconciled = decimal(input.get("openingBalance"));
        long createdCount = 0;
        long approvedCount = 0;
        BigDecimal reconciliationTotal = BigDecimal.ZERO;
        for (Object trans : all) {
            BigDecimal amount = signedAmount(trans);
            String status = text(entities.get(trans, "statusId"));
            if ("FINACT_TRNS_CREATED".equals(status)) {
                created = created.add(amount);
                createdCount++;
            } else if ("FINACT_TRNS_APPROVED".equals(status)) {
                approved = approved.add(amount);
                approvedCount++;
                if (same(input.get("glReconciliationId"), entities.get(trans, "glReconciliationId"))) {
                    reconciliationTotal = reconciliationTotal.add(amount);
                }
            }
        }
        for (Object trans : matched) {
            grand = grand.add(signedAmount(trans));
        }
        reconciled = reconciled.add(reconciliationTotal);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("finAccountTransList", matched);
        result.put("searchedNumberOfRecords", matched.size());
        result.put("grandTotal", grand);
        result.put("createdGrandTotal", created);
        result.put("totalCreatedTransactions", createdCount);
        result.put("approvedGrandTotal", approved);
        result.put("totalApprovedTransactions", approvedCount);
        result.put("createdApprovedGrandTotal", created.add(approved));
        result.put("totalCreatedApprovedTransactions", createdCount + approvedCount);
        result.put("glReconciliationApprovedGrandTotal", reconciled);
        return result;
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountServices.xml:602. */
    private Map<String, Object> getFinAccountTransRunningTotalAndBalances(Map<String, Object> input) {
        Object trans = requiredOne("FinAccountTrans", "finAccountTransId", input);
        BigDecimal running = decimal(input.get("runningTotal")).add(signedAmount(trans));
        long count = ((Number) (input.get("numberOfTransactions") instanceof Number n ? n : 0L)).longValue() + 1;
        BigDecimal ending = decimal(input.get("openingBalance")).add(decimal(input.get("reconciledBalance"))).add(running);
        String currency = "USD";
        Object pref = one("PartyAcctgPreference", Map.of("partyId", required(input, "organizationPartyId")));
        if (pref != null && !blank(entities.get(pref, "baseCurrencyUomId"))) {
            currency = text(entities.get(pref, "baseCurrencyUomId"));
        }
        return Map.of("runningTotal", running, "numberOfTransactions", count,
                "finAccountTransRunningTotal", moneyString(running, currency), "endingBalance", moneyString(ending, currency));
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountServices.xml:632. */
    private Map<String, Object> reconcileFinAccountTrans(Map<String, Object> input) {
        Object trans = requiredOne("FinAccountTrans", "finAccountTransId", input);
        String reconciliationId = text(entities.get(trans, "glReconciliationId"));
        if (reconciliationId == null) {
            throw badRequest("FinAccount transaction is not assigned to a reconciliation");
        }
        String type = text(entities.get(trans, "finAccountTransTypeId"));
        Map<String, Object> branchInput = new LinkedHashMap<>();
        branchInput.put("finAccountTrans", trans);
        branchInput.put("organizationPartyId", input.get("organizationPartyId"));
        String branchService;
        if ("ADJUSTMENT".equals(type)) {
            branchService = "reconcileAdjustmentFinAcctgTrans";
        } else if ("DEPOSIT".equals(type)) {
            branchService = "reconcileDepositFinAcctgTrans";
        } else if ("WITHDRAWAL".equals(type)) {
            branchService = "reconcileWithdrawalFinAcctgTrans";
        } else {
            throw badRequest("Unsupported financial account transaction type");
        }
        dispatcher.execute(COMPONENT, branchService, branchInput);
        dispatcher.execute(COMPONENT, "setFinAccountTransStatus",
                Map.of("finAccountTransId", entities.get(trans, "finAccountTransId"), "statusId", "FINACT_TRNS_APPROVED"));
        Object reconciliation = requiredOne("GlReconciliation", "glReconciliationId",
                Map.of("glReconciliationId", reconciliationId));
        BigDecimal value = decimal(entities.get(reconciliation, "reconciledBalance"));
        BigDecimal amount = decimal(entities.get(trans, "amount"));
        entities.set(reconciliation, "reconciledBalance", Set.of("ADJUSTMENT", "DEPOSIT").contains(type)
                ? value.add(amount) : value.subtract(amount));
        if (Boolean.TRUE.equals(dispatcher.execute(COMPONENT, "isGlReconciliationReconciled",
                Map.of("glReconciliationId", reconciliationId)).get("isReconciled"))
                && entities.get(reconciliation, "reconciledDate") == null) {
            entities.set(reconciliation, "reconciledDate", now());
        }
        return Map.of();
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountServices.xml:688. */
    private Map<String, Object> reconcileAdjustmentFinAcctgTrans(Map<String, Object> input) {
        Object trans = resolveFinAccountTrans(input);
        if (!"ADJUSTMENT".equals(entities.get(trans, "finAccountTransTypeId"))) {
            throw badRequest("Not an adjustment transaction");
        }
        List<?> transactions = accountingTransactionsFor(trans);
        if (!transactions.isEmpty()) {
            Object accountingTransaction = transactions.get(0);
            postIfNeeded(accountingTransaction);
            markEntriesReconciled(accountingTransaction, text(entities.get(trans, "glReconciliationId")));
        }
        return Map.of();
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountServices.xml:718. */
    private Map<String, Object> reconcileDepositFinAcctgTrans(Map<String, Object> input) {
        Object trans = resolveFinAccountTrans(input);
        if (!"DEPOSIT".equals(entities.get(trans, "finAccountTransTypeId"))) {
            throw badRequest("Not a deposit transaction");
        }
        reconcilePaymentEntries(trans, true);
        return Map.of();
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountServices.xml:801. */
    private Map<String, Object> reconcileWithdrawalFinAcctgTrans(Map<String, Object> input) {
        Object trans = resolveFinAccountTrans(input);
        if (!"WITHDRAWAL".equals(entities.get(trans, "finAccountTransTypeId"))) {
            throw badRequest("Not a withdrawal transaction");
        }
        reconcilePaymentEntries(trans, false);
        return Map.of();
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountServices.xml:1060. */
    private Map<String, Object> getReconciliationClosingBalance(Map<String, Object> input) {
        Object reconciliation = requiredOne("GlReconciliation", "glReconciliationId", input);
        return Map.of("closingBalance", decimal(entities.get(reconciliation, "reconciledBalance"))
                .add(decimal(entities.get(reconciliation, "openingBalance"))));
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountServices.xml:340. */
    private Map<String, Object> depositWithdrawPayments(Map<String, Object> input) {
        return depositWithdrawPaymentsInternal(input);
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountServices.xml:883. */
    private Map<String, Object> createPaymentAndFinAccountTrans(Map<String, Object> input) {
        Map<String, Object> paymentInput = new LinkedHashMap<>(input);
        paymentInput.remove("isDepositWithDrawPayment");
        paymentInput.remove("finAccountTransTypeId");
        paymentInput.remove("paymentGroupTypeId");
        Map<String, Object> paymentResult = dispatcher.execute(COMPONENT, "createPayment", paymentInput);
        String paymentId = text(paymentResult.get("paymentId"));
        if (paymentId == null) {
            throw badRequest("createPayment did not return paymentId");
        }
        String methodId = text(input.get("paymentMethodId"));
        Object method = methodId == null ? null : one("PaymentMethod", Map.of("paymentMethodId", methodId));
        String accountId = method == null ? null : text(entities.get(method, "finAccountId"));
        if (accountId != null && "Y".equals(input.get("isDepositWithDrawPayment"))) {
            Map<String, Object> transInput = new LinkedHashMap<>();
            transInput.put("finAccountId", accountId);
            transInput.put("finAccountTransTypeId", input.get("finAccountTransTypeId"));
            transInput.put("paymentId", paymentId);
            transInput.put("partyId", input.get("partyIdFrom"));
            transInput.put("amount", input.get("amount"));
            transInput.put("statusId", "FINACT_TRNS_CREATED");
            String transId = text(dispatcher.execute(COMPONENT, "createFinAccountTrans", transInput).get("finAccountTransId"));
            Object payment = requiredOne("Payment", "paymentId", Map.of("paymentId", paymentId));
            entities.set(payment, "finAccountTransId", transId);
        }
        return Map.of("paymentId", paymentId);
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountServices.xml:924. */
    private Map<String, Object> getTransactionTotalByGlReconcileId(Map<String, Object> input) {
        String id = required(input, "glReconciliationId");
        Object rec = requiredOne("GlReconciliation", "glReconciliationId", input);
        BigDecimal total = decimal(entities.get(rec, "reconciledBalance"));
        for (Object trans : entities.findByEquality(
                entities.entityClass("FinAccountTrans"), Map.of("glReconciliationId", id))) {
            total = total.add(signedAmount(trans));
        }
        return Map.of("grandTotal", total);
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountServices.xml:946. */
    private Map<String, Object> assignGlRecToFinAccTrans(Map<String, Object> input) {
        Object trans = requiredOne("FinAccountTrans", "finAccountTransId", input);
        Object rec = requiredOne("GlReconciliation", "glReconciliationId", input);
        if (!"GLREC_CREATED".equals(entities.get(rec, "statusId"))) {
            throw badRequest("Invalid GL reconciliation");
        }
        if (!"FINACT_TRNS_CREATED".equals(entities.get(trans, "statusId"))) {
            throw badRequest("Only created financial transactions can be reconciled");
        }
        entities.set(trans, "glReconciliationId", input.get("glReconciliationId"));
        for (Object payment : paymentsFor(trans)) {
            if (!Set.of("PMNT_SENT", "PMNT_RECEIVED", "PMNT_CONFIRMED").contains(text(entities.get(payment, "statusId")))) {
                throw badRequest("Payment has an invalid status for reconciliation");
            }
        }
        return Map.of();
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountServices.xml:989. */
    private Map<String, Object> removeFinAccountTransFromReconciliation(Map<String, Object> input) {
        Object trans = requiredOne("FinAccountTrans", "finAccountTransId", input);
        if (!"FINACT_TRNS_CREATED".equals(entities.get(trans, "statusId"))) {
            throw badRequest("Only created transactions can be removed from reconciliation");
        }
        entities.set(trans, "glReconciliationId", null);
        return Map.of();
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountServices.xml:1003. */
    private Map<String, Object> isGlReconciliationReconciled(Map<String, Object> input) {
        String id = required(input, "glReconciliationId");
        List<?> transactions = entities.findByEquality(
                entities.entityClass("FinAccountTrans"), Map.of("glReconciliationId", id));
        boolean reconciled = transactions.stream()
                .noneMatch(trans -> "FINACT_TRNS_CREATED".equals(entities.get(trans, "statusId")));
        return Map.of("isReconciled", reconciled);
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountServices.xml:1024. */
    private Map<String, Object> cancelBankReconciliation(Map<String, Object> input) {
        String id = required(input, "glReconciliationId");
        for (Object trans : entities.findByEquality(entities.entityClass("FinAccountTrans"),
                Map.of("glReconciliationId", id, "statusId", "FINACT_TRNS_CREATED"))) {
            entities.set(trans, "glReconciliationId", null);
        }
        return Map.of();
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountServices.xml:1037. */
    private Map<String, Object> getAssociatedAcctgTransEntriesWithFinAccountTrans(Map<String, Object> input) {
        Object trans = requiredOne("FinAccountTrans", "finAccountTransId", input);
        List<String> paymentIds = paymentsFor(trans).stream()
                .map(payment -> text(entities.get(payment, "paymentId"))).toList();
        List<Object> rows = new ArrayList<>();
        for (String paymentId : paymentIds) {
            for (Object accountingTransaction : entities.findByEquality(
                    entities.entityClass("AcctgTrans"), Map.of("paymentId", paymentId))) {
                for (Object entry : entities.findByEquality(entities.entityClass("AcctgTransEntry"),
                        Map.of("acctgTransId", entities.get(accountingTransaction, "acctgTransId")))) {
                    Map<String, Object> row = new LinkedHashMap<>(entities.toMap(accountingTransaction));
                    row.putAll(entities.toMap(entry));
                    rows.add(row);
                }
            }
        }
        return Map.of("acctgTransAndEntries", rows);
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountServices.xml:1068. */
    private Map<String, Object> autoFinAccountReconciliation(Map<String, Object> input) {
        LocalDate today = LocalDate.now(ZoneOffset.systemDefault());
        Timestamp start = Timestamp.from(today.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Timestamp end = Timestamp.from(today.plusDays(1).atStartOfDay()
                .minusNanos(1).atZone(ZoneId.systemDefault()).toInstant());
        List<?> reconciliations = entities.findByEquality(
                entities.entityClass("GlReconciliation"), Map.of("statusId", "GLREC_CREATED"));
        for (Object reconciliation : reconciliations) {
            Date date = date(entities.get(reconciliation, "reconciledDate"));
            if (date == null || date.before(start) || date.after(end)) {
                continue;
            }
            String recId = text(entities.get(reconciliation, "glReconciliationId"));
            for (Object trans : entities.findByEquality(
                    entities.entityClass("FinAccountTrans"), Map.of("glReconciliationId", recId))) {
                Map<String, Object> reconcileInput = new LinkedHashMap<>();
                reconcileInput.put("finAccountTransId", entities.get(trans, "finAccountTransId"));
                if (entities.get(trans, "partyId") != null) {
                    reconcileInput.put("organizationPartyId", entities.get(trans, "partyId"));
                }
                dispatcher.execute(COMPONENT, "reconcileFinAccountTrans", reconcileInput);
            }
        }
        return Map.of();
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountServices.xml:476. */
    private Map<String, Object> expirePaymentAssociationsOnFinAccountTransCancel(Map<String, Object> input) {
        Object trans = requiredOne("FinAccountTrans", "finAccountTransId", input);
        Timestamp now = now();
        for (Object payment : paymentsFor(trans)) {
            String id = text(entities.get(payment, "paymentId"));
            Date today = new Date();
            Object member = entities.findByEquality(entities.entityClass("PaymentGroupMember"),
                    Map.of("paymentId", id)).stream()
                    .filter(candidate -> {
                        Date from = date(entities.get(candidate, "fromDate"));
                        Date thru = date(entities.get(candidate, "thruDate"));
                        return (from == null || !from.after(today)) && (thru == null || !thru.before(today));
                    }).findFirst().orElse(null);
            if (member != null) {
                entities.set(member, "thruDate", now);
            }
        }
        return Map.of();
    }

    private Map<String, Object> depositWithdrawPaymentsInternal(Map<String, Object> input) {
        Object account = requiredOne("FinAccount", "finAccountId", input);
        requireActiveForTransaction(account);
        List<String> ids = stringList(input.get("paymentIds"));
        if (ids.isEmpty()) {
            throw badRequest("paymentIds is required");
        }
        BigDecimal total = BigDecimal.ZERO;
        List<Object> payments = new ArrayList<>();
        for (String id : ids) {
            Object payment = requiredOne("Payment", "paymentId", Map.of("paymentId", id));
            if (!blank(entities.get(payment, "finAccountTransId"))) {
                throw badRequest("Payment already associated with a financial account transaction");
            }
            String status = text(entities.get(payment, "statusId"));
            if (!Set.of("PMNT_SENT", "PMNT_RECEIVED").contains(status)) {
                throw badRequest("Payment status is not received or sent");
            }
            boolean receipt = isPaymentType(payment, "RECEIPT");
            boolean disbursement = isPaymentType(payment, "DISBURSEMENT");
            if (!receipt && !disbursement) {
                throw badRequest("Payment type is not a receipt or disbursement");
            }
            if ("Y".equals(input.get("groupInOneTransaction")) && !receipt) {
                throw badRequest("Disbursements cannot be grouped into one financial transaction");
            }
            payments.add(payment);
            total = total.add(decimal(entities.get(payment, "amount")));
        }
        if ("Y".equals(input.get("groupInOneTransaction"))) {
            Map<String, Object> trans = new LinkedHashMap<>();
            trans.put("finAccountId", input.get("finAccountId"));
            trans.put("finAccountTransTypeId", "DEPOSIT");
            trans.put("partyId", entities.get(account, "ownerPartyId"));
            trans.put("amount", total);
            trans.put("statusId", "FINACT_TRNS_CREATED");
            String transId = text(dispatcher.execute(COMPONENT, "createFinAccountTrans", trans).get("finAccountTransId"));
            payments.forEach(payment -> entities.set(payment, "finAccountTransId", transId));
            return Map.of("finAccountTransId", transId);
        }
        String first = null;
        for (Object payment : payments) {
            String type = isPaymentType(payment, "RECEIPT") ? "DEPOSIT" : "WITHDRAWAL";
            Map<String, Object> trans = new LinkedHashMap<>();
            trans.put("finAccountId", input.get("finAccountId"));
            trans.put("finAccountTransTypeId", type);
            trans.put("partyId", entities.get(account, "ownerPartyId"));
            trans.put("paymentId", entities.get(payment, "paymentId"));
            trans.put("amount", entities.get(payment, "amount"));
            trans.put("statusId", "FINACT_TRNS_CREATED");
            String transId = text(dispatcher.execute(COMPONENT, "createFinAccountTrans", trans).get("finAccountTransId"));
            entities.set(payment, "finAccountTransId", transId);
            if (first == null) {
                first = transId;
            }
        }
        return first == null ? Map.of() : Map.of("finAccountTransId", first);
    }

    private boolean isPaymentType(Object payment, String typeId) {
        String paymentTypeId = text(entities.get(payment, "paymentTypeId"));
        while (paymentTypeId != null) {
            if (typeId.equals(paymentTypeId)) {
                return true;
            }
            Object paymentType = one("PaymentType", Map.of("paymentTypeId", paymentTypeId));
            paymentTypeId = paymentType == null ? null : text(entities.get(paymentType, "parentTypeId"));
        }
        return false;
    }

    private void recalculateBalances(String accountId) {
        Object account = requiredOne("FinAccount", "finAccountId", Map.of("finAccountId", accountId));
        BigDecimal actual = BigDecimal.ZERO;
        List<?> transactions = entities.findByEquality(
                entities.entityClass("FinAccountTrans"), Map.of("finAccountId", accountId));
        for (Object trans : transactions) {
            if (!"FINACT_TRNS_APPROVED".equals(entities.get(trans, "statusId"))) {
                continue;
            }
            BigDecimal amount = balanceAmount(trans);
            if (amount != null) {
                actual = actual.add(amount);
            }
        }
        actual = round(actual);
        BigDecimal available = actual;
        for (Object auth : entities.findByEquality(
                entities.entityClass("FinAccountAuth"), Map.of("finAccountId", accountId))) {
            Date from = date(entities.get(auth, "fromDate"));
            Date thru = date(entities.get(auth, "thruDate"));
            Date now = new Date();
            if ((from == null || !from.after(now)) && (thru == null || !thru.before(now))) {
                available = round(available.subtract(decimal(entities.get(auth, "amount"))));
            }
        }
        entities.set(account, "actualBalance", actual);
        entities.set(account, "availableBalance", available);
    }

    private void createStatus(String id, String status, String oldStatus) {
        Object statusRecord = newEntity("FinAccountStatus");
        entities.set(statusRecord, "finAccountId", id);
        entities.set(statusRecord, "statusId", status);
        entities.set(statusRecord, "statusDate", now());
        entities.set(statusRecord, "changeByUserLoginId", currentUser());
        entityManager.persist(statusRecord);
    }

    private void requireStatusChange(String from, String to) {
        if (from == null || one("StatusValidChange", Map.of("statusId", from, "statusIdTo", to)) == null) {
            throw badRequest("Invalid status change from " + from + " to " + to);
        }
    }

    private void requireActiveForTransaction(Object account) {
        String status = text(entities.get(account, "statusId"));
        if ("FNACT_MANFROZEN".equals(status) || "FNACT_CANCELLED".equals(status)) {
            throw badRequest("Financial account is inactive");
        }
    }

    private boolean matchesTransaction(Object trans, Map<String, Object> input) {
        Object reconciliationFilter = input.get("glReconciliationId");
        String status = text(input.get("statusId"));
        if ("_NA_".equals(reconciliationFilter)) {
            boolean excludeCanceled = status == null || !"FINACT_TRNS_CANCELED".equals(status);
            if (entities.get(trans, "glReconciliationId") != null
                    || excludeCanceled && "FINACT_TRNS_CANCELED".equals(entities.get(trans, "statusId"))) {
                return false;
            }
        } else if (reconciliationFilter != null
                && !same(entities.get(trans, "glReconciliationId"), reconciliationFilter)) {
            return false;
        }
        return fieldMatches(trans, "finAccountTransTypeId", input.get("finAccountTransTypeId"))
                && fieldMatches(trans, "statusId", input.get("statusId"))
                && after(trans, "transactionDate", input.get("fromTransactionDate"))
                && before(trans, "transactionDate", input.get("thruTransactionDate"))
                && after(trans, "entryDate", input.get("fromEntryDate"))
                && before(trans, "entryDate", input.get("thruEntryDate"));
    }

    private boolean fieldMatches(Object entity, String field, Object wanted) {
        return wanted == null || blank(wanted) || same(entities.get(entity, field), wanted);
    }

    private boolean after(Object entity, String field, Object boundary) {
        Date actual = date(entities.get(entity, field));
        Date from = date(boundary);
        return from == null || actual != null && !actual.before(from);
    }

    private boolean before(Object entity, String field, Object boundary) {
        Date actual = date(entities.get(entity, field));
        Date thru = date(boundary);
        return thru == null || actual != null && !actual.after(thru);
    }

    private BigDecimal signedAmount(Object trans) {
        BigDecimal amount = decimal(entities.get(trans, "amount"));
        return "WITHDRAWAL".equals(entities.get(trans, "finAccountTransTypeId")) ? amount.negate() : amount;
    }

    private BigDecimal balanceAmount(Object trans) {
        String type = text(entities.get(trans, "finAccountTransTypeId"));
        if ("WITHDRAWAL".equals(type)) {
            return decimal(entities.get(trans, "amount")).negate();
        }
        if ("DEPOSIT".equals(type) || "ADJUSTMENT".equals(type)) {
            return decimal(entities.get(trans, "amount"));
        }
        return null;
    }

    private List<Object> paymentsFor(Object trans) {
        String paymentId = text(entities.get(trans, "paymentId"));
        if (paymentId != null) {
            Object payment = one("Payment", Map.of("paymentId", paymentId));
            return payment == null ? List.of() : List.of(payment);
        }
        return new ArrayList<>(entities.findByEquality(
                entities.entityClass("Payment"), Map.of("finAccountTransId", entities.get(trans, "finAccountTransId"))));
    }

    private Object resolveFinAccountTrans(Map<String, Object> input) {
        Object value = input.get("finAccountTrans");
        if (value != null && entities.entityClass("FinAccountTrans").isInstance(value)) {
            return value;
        }
        String id = value instanceof Map<?, ?> map
                ? text(map.get("finAccountTransId")) : text(input.get("finAccountTransId"));
        if (id == null) {
            throw badRequest("Missing required parameter: finAccountTrans");
        }
        return requiredOne("FinAccountTrans", "finAccountTransId", Map.of("finAccountTransId", id));
    }

    private List<?> accountingTransactionsFor(Object trans) {
        return entities.findByEquality(entities.entityClass("AcctgTrans"),
                Map.of("finAccountTransId", entities.get(trans, "finAccountTransId")));
    }

    private void postIfNeeded(Object transaction) {
        if ("N".equals(entities.get(transaction, "isPosted"))) {
            dispatcher.execute(COMPONENT, "postAcctgTrans",
                    Map.of("acctgTransId", entities.get(transaction, "acctgTransId")));
        }
    }

    private void markEntriesReconciled(Object transaction, String reconciliationId) {
        String transactionId = text(entities.get(transaction, "acctgTransId"));
        for (Object entry : entities.findByEquality(entities.entityClass("AcctgTransEntry"),
                Map.of("acctgTransId", transactionId))) {
            Map<String, Object> input = new LinkedHashMap<>();
            input.put("acctgTransId", transactionId);
            input.put("acctgTransEntrySeqId", entities.get(entry, "acctgTransEntrySeqId"));
            input.put("glReconciliationId", reconciliationId);
            input.put("reconciledAmount", entities.get(entry, "amount"));
            dispatcher.execute(COMPONENT, "createGlReconciliationEntry", input);
        }
    }

    private void reconcilePaymentEntries(Object finAccountTrans, boolean deposit) {
        List<?> payments = paymentsFor(finAccountTrans);
        if (payments.isEmpty()) {
            List<?> transactions = accountingTransactionsFor(finAccountTrans);
            if (!transactions.isEmpty()) {
                postIfNeeded(transactions.get(0));
            }
            return;
        }
        Object account = requiredOne("FinAccount", "finAccountId",
                Map.of("finAccountId", entities.get(finAccountTrans, "finAccountId")));
        for (Object payment : payments) {
            String paymentId = text(entities.get(payment, "paymentId"));
            List<?> paymentTransactions = entities.findByEquality(entities.entityClass("AcctgTrans"),
                    Map.of("paymentId", paymentId));
            for (Object source : paymentTransactions) {
                if ("PAYMENT_APPL".equals(entities.get(source, "acctgTransTypeId"))) {
                    continue;
                }
                postIfNeeded(source);
                List<Map<String, Object>> entries = new ArrayList<>();
                BigDecimal accountAmount = BigDecimal.ZERO;
                Object partyId = null;
                Object organizationId = null;
                for (Object entry : entities.findByEquality(entities.entityClass("AcctgTransEntry"),
                        Map.of("acctgTransId", entities.get(source, "acctgTransId")))) {
                    String flag = text(entities.get(entry, "debitCreditFlag"));
                    if (deposit && "D".equals(flag) || !deposit && "C".equals(flag)) {
                        Map<String, Object> reverseEntry = new LinkedHashMap<>();
                        reverseEntry.put("glAccountId", entities.get(entry, "glAccountId"));
                        reverseEntry.put("organizationPartyId", entities.get(entry, "organizationPartyId"));
                        reverseEntry.put("partyId", entities.get(entry, "partyId"));
                        reverseEntry.put("amount", entities.get(entry, "amount"));
                        reverseEntry.put("acctgTransEntryTypeId", entities.get(entry, "acctgTransEntryTypeId"));
                        reverseEntry.put("debitCreditFlag", deposit ? "C" : "D");
                        entries.add(reverseEntry);
                        accountAmount = accountAmount.add(decimal(entities.get(entry, "amount")));
                        partyId = entities.get(entry, "partyId");
                        organizationId = entities.get(entry, "organizationPartyId");
                    }
                    Map<String, Object> update = new LinkedHashMap<>();
                    update.put("acctgTransId", entities.get(entry, "acctgTransId"));
                    update.put("acctgTransEntrySeqId", entities.get(entry, "acctgTransEntrySeqId"));
                    update.put("reconcileStatusId", "AES_RECONCILED");
                    dispatcher.execute(COMPONENT, "updateAcctgTransEntry", update);
                }
                if (accountAmount.signum() == 0 || entries.isEmpty()) {
                    continue;
                }
                Map<String, Object> accountEntry = new LinkedHashMap<>();
                accountEntry.put("glAccountId", entities.get(account, "postToGlAccountId"));
                accountEntry.put("organizationPartyId", organizationId);
                accountEntry.put("partyId", partyId);
                accountEntry.put("amount", accountAmount);
                accountEntry.put("acctgTransEntryTypeId", "_NA_");
                accountEntry.put("debitCreditFlag", deposit ? "D" : "C");
                entries.add(accountEntry);

                Map<String, Object> create = new LinkedHashMap<>(entities.toMap(source));
                create.remove("acctgTransId");
                create.remove("isPosted");
                create.put("transactionDate", now());
                create.put("postedDate", now());
                create.put("acctgTransEntries", entries);
                Map<String, Object> created = dispatcher.execute(COMPONENT, "createAcctgTransAndEntries", create);
                String createdId = text(created.get("acctgTransId"));
                if (createdId == null) {
                    continue;
                }
                for (Object entry : entities.findByEquality(entities.entityClass("AcctgTransEntry"),
                        Map.of("acctgTransId", createdId))) {
                    Map<String, Object> reconciliationEntry = new LinkedHashMap<>();
                    reconciliationEntry.put("acctgTransId", createdId);
                    reconciliationEntry.put("acctgTransEntrySeqId", entities.get(entry, "acctgTransEntrySeqId"));
                    reconciliationEntry.put("glReconciliationId", entities.get(finAccountTrans, "glReconciliationId"));
                    reconciliationEntry.put("reconciledAmount", entities.get(entry, "amount"));
                    dispatcher.execute(COMPONENT, "createGlReconciliationEntry", reconciliationEntry);
                }
            }
        }
    }

    private void roundField(Object entity, String field) {
        Object value = entities.get(entity, field);
        if (value != null) {
            entities.set(entity, field, round(decimal(value)));
        }
    }

    private BigDecimal round(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private void defaultValue(Object entity, String field, Object value) {
        if (entities.get(entity, field) == null) {
            entities.set(entity, field, value);
        }
    }

    private Object newEntity(String name) {
        return entities.newEntity(entities.entityClass(name));
    }

    private Object one(String name, Map<String, ?> key) {
        return entities.findByPrimaryKey(entities.entityClass(name), key);
    }

    private Object requiredOne(String name, String key, Map<String, ?> input) {
        String id = required(input, key);
        Object value = one(name, Map.of(key, id));
        if (value == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, name + " not found");
        }
        return value;
    }

    private void copy(Object entity, Map<String, ?> input, Set<String> excluded) {
        Set<String> keys = Set.copyOf(entities.primaryKeys(entity.getClass()));
        input.forEach((name, value) -> {
            if (value != null && !excluded.contains(name) && !keys.contains(name)
                    && !"userLogin".equals(name) && !"locale".equals(name)) {
                entities.set(entity, name, value);
            }
        });
    }

    private static String required(Map<String, ?> input, String key) {
        String value = text(input.get(key));
        if (value == null) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Missing required parameter: " + key);
        }
        return value;
    }

    private static String text(Object value) {
        return value == null || value.toString().isBlank() ? null : value.toString();
    }

    private static boolean blank(Object value) {
        return value == null || value.toString().isBlank();
    }

    private static BigDecimal decimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value instanceof BigDecimal decimal ? decimal : new BigDecimal(value.toString());
    }

    private static Timestamp now() {
        return Timestamp.from(Instant.now());
    }

    private static boolean same(Object first, Object second) {
        return first != null && second != null && first.toString().equals(second.toString());
    }

    private static Date date(Object value) {
        if (value instanceof Date date) {
            return date;
        }
        if (value instanceof Instant instant) {
            return Date.from(instant);
        }
        try {
            return value == null ? null : Timestamp.valueOf(value.toString());
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private static List<String> stringList(Object input) {
        if (!(input instanceof List<?> list)) {
            return List.of();
        }
        return list.stream().map(FinAccountServiceHandlers::text).filter(value -> value != null).toList();
    }

    private static String currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? null : authentication.getName();
    }

    private String currentUserPartyId() {
        String userLoginId = currentUser();
        if (userLoginId == null) {
            return null;
        }
        Object userLogin = one("UserLogin", Map.of("userLoginId", userLoginId));
        return userLogin == null ? null : text(entities.get(userLogin, "partyId"));
    }

    private static String moneyString(BigDecimal amount, String currency) {
        java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance(Locale.US);
        format.setRoundingMode(RoundingMode.HALF_UP);
        return format.format(amount) + " " + currency;
    }

    private static MigrationException badRequest(String message) {
        return new MigrationException(HttpStatus.BAD_REQUEST, message);
    }
}
