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
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GeneralLedgerServiceHandlers {
    private static final String COMPONENT = "accounting";
    private final EntityManager entityManager;
    private final EntityRegistry entities;
    private final InventoryAccountingPort inventory;
    private final PartyAccountingPort parties;
    private final ProductPort products;
    private final MigrationServiceDispatcher dispatcher;
    private final ServiceHandlerRegistry registry;

    public GeneralLedgerServiceHandlers(
            EntityManager entityManager,
            EntityRegistry entities,
            InventoryAccountingPort inventory,
            PartyAccountingPort parties,
            ProductPort products,
            MigrationServiceDispatcher dispatcher,
            ServiceHandlerRegistry registry) {
        this.entityManager = entityManager;
        this.entities = entities;
        this.inventory = inventory;
        this.parties = parties;
        this.products = products;
        this.dispatcher = dispatcher;
        this.registry = registry;
    }

    @PostConstruct
    void registerHandlers() {
        register("quickCreateAcctgTransAndEntries", "CREATE", this::quickCreate);
        register("calculateGlJournalTrialBalance", "ACCOUNTING_VIEW", this::calculateJournal);
        register("postGlJournal", "UPDATE", this::postJournal);
        register("updateGlReconciliationLastModified", "UPDATE", this::updateReconciliationLastModified);
        register("createGlReconciliationEntry", "ACCOUNTING_CREATE", this::createReconciliationEntry);
        register("completeAcctgTransEntries", "ACCTG_ATX_UPDATE", this::completeEntries);
        register("createAcctgTransAndEntries", "ACCTG_ATX_CREATE", this::createTransactionAndEntries);
        register("closeFinancialTimePeriod", "UPDATE", this::closePeriod);
        register("computeGlAccountBalanceForTimePeriod", "VIEW", this::computeBalance);
        register("computeAndStoreGlAccountHistoryBalance", "UPDATE", this::computeAndStoreHistory);
        register("prepareIncomeStatement", "VIEW", this::prepareIncomeStatement);
        register("getGlAccountFromAccountType", "VIEW", this::getGlAccountFromType);
        register("getInventoryItemOwner", "VIEW", this::getInventoryItemOwner);
        register("copyAcctgTransAndEntries", "CREATE", this::copyTransaction);
        register("updateGlAccountCategoryMember", "UPDATE", this::updateCategoryMember);
        register("createGlAcctCatMemFromCostCenters", "UPDATE", this::createCategoryMemberFromCostCenter);
        register("getGlAcctgAndAmountPercentage", "VIEW", this::getCostCenterAssignments);
        register("getInventoryValuationList", "VIEW", this::getInventoryValuation);
        register("setGlReconciliationStatus", "UPDATE", this::setReconciliationStatus);
    }

    private void register(String name, String permission, ServiceHandlerRegistry.SimpleServiceHandler handler) {
        registry.registerHandler(COMPONENT, name, permission, handler);
    }

    /** Migrated from applications/accounting/minilang/ledger/GeneralLedgerServices.xml:25. */
    private Map<String, Object> quickCreate(Map<String, Object> input) {
        return Map.of();
    }

    /** Migrated from applications/accounting/minilang/ledger/GeneralLedgerServices.xml:59. */
    private Map<String, Object> calculateJournal(Map<String, Object> input) {
        String journalId = required(input, "glJournalId");
        List<?> transactions = entities.findByEquality(
                entities.entityClass("AcctgTrans"), Map.of("glJournalId", journalId));
        BigDecimal debit = BigDecimal.ZERO;
        BigDecimal credit = BigDecimal.ZERO;
        for (Object transaction : transactions) {
            if ("Y".equals(entities.get(transaction, "isPosted"))) {
                continue;
            }
            Map<String, Object> trial = dispatcher.execute(COMPONENT, "calculateAcctgTransTrialBalance",
                    Map.of("acctgTransId", entities.get(transaction, "acctgTransId")));
            debit = debit.add(decimal(trial.get("debitTotal")));
            credit = credit.add(decimal(trial.get("creditTotal")));
        }
        debit = debit.setScale(4, RoundingMode.HALF_UP);
        credit = credit.setScale(4, RoundingMode.HALF_UP);
        return Map.of("debitTotal", debit, "creditTotal", credit, "debitCreditDifference", debit.subtract(credit));
    }

    /** Migrated from applications/accounting/minilang/ledger/GeneralLedgerServices.xml:94. */
    private Map<String, Object> postJournal(Map<String, Object> input) {
        String journalId = required(input, "glJournalId");
        Map<String, Object> totals = calculateJournal(input);
        if (decimal(totals.get("debitCreditDifference")).compareTo(BigDecimal.ZERO) != 0) {
            throw badRequest("GL journal trial balance failed");
        }
        List<?> transactions = entities.findByEquality(
                entities.entityClass("AcctgTrans"), Map.of("glJournalId", journalId));
        for (Object transaction : transactions) {
            if (!"Y".equals(entities.get(transaction, "isPosted"))) {
                dispatcher.execute(COMPONENT, "postAcctgTrans",
                        Map.of("acctgTransId", entities.get(transaction, "acctgTransId")));
            }
        }
        return Map.of();
    }

    /** Migrated from applications/accounting/minilang/ledger/GeneralLedgerServices.xml:121. */
    private Map<String, Object> updateReconciliationLastModified(Map<String, Object> input) {
        Object reconciliation = requireOne("GlReconciliation",
                Map.of("glReconciliationId", required(input, "glReconciliationId")));
        entities.set(reconciliation, "lastModifiedByUserLogin", currentUser());
        entityManager.flush();
        return Map.of();
    }

    /** Migrated from applications/accounting/minilang/ledger/GeneralLedgerServices.xml:128. */
    private Map<String, Object> createReconciliationEntry(Map<String, Object> input) {
        Map<String, Object> entryKey = Map.of(
                "acctgTransId", required(input, "acctgTransId"),
                "acctgTransEntrySeqId", required(input, "acctgTransEntrySeqId"));
        Object transEntry = requireOne("AcctgTransEntry", entryKey);
        if ("AES_RECONCILED".equals(entities.get(transEntry, "reconcileStatusId"))) {
            throw badRequest("Accounting transaction entry has already been reconciled");
        }
        Object record = entities.newEntity(entities.entityClass("GlReconciliationEntry"));
        setKnown(record, input, true);
        entityManager.persist(record);
        entities.set(transEntry, "reconcileStatusId", "AES_RECONCILED");
        if (input.get("reconciledAmount") != null) {
            entities.set(transEntry, "amount", input.get("reconciledAmount"));
        }
        Object reconciliation = requireOne("GlReconciliation",
                Map.of("glReconciliationId", required(input, "glReconciliationId")));
        Map<String, Object> result = new LinkedHashMap<>();
        if ("GLREC_CREATED".equals(entities.get(reconciliation, "statusId"))) {
            result.put("statusId", "GLREC_RECONCILED");
        }
        entities.set(reconciliation, "lastModifiedByUserLogin", currentUser());
        entityManager.flush();
        return result;
    }

    /** Migrated from applications/accounting/minilang/ledger/GeneralLedgerServices.xml:160. */
    private Map<String, Object> completeEntries(Map<String, Object> input) {
        Object transaction = requireOne("AcctgTrans", Map.of("acctgTransId", required(input, "acctgTransId")));
        if ("Y".equals(entities.get(transaction, "isPosted"))) {
            throw badRequest("Accounting transaction has already been posted");
        }
        List<?> entries = entities.findByEquality(entities.entityClass("AcctgTransEntry"),
                Map.of("acctgTransId", input.get("acctgTransId")));
        int completed = 0;
        for (Object entry : entries) {
            if (blank(entities.get(entry, "glAccountId"))) {
                String organization = text(entities.get(entry, "organizationPartyId"));
                String accountType = text(entities.get(entry, "glAccountTypeId"));
                if (organization == null || accountType == null) {
                    continue;
                }
                Map<String, Object> lookup = new LinkedHashMap<>();
                lookup.put("organizationPartyId", organization);
                lookup.put("acctgTransTypeId", entities.get(transaction, "acctgTransTypeId"));
                lookup.put("glAccountTypeId", accountType);
                lookup.put("debitCreditFlag", entities.get(entry, "debitCreditFlag"));
                Object glAccountId = dispatcher.execute(COMPONENT, "getGlAccountFromAccountType", lookup)
                        .get("glAccountId");
                if (glAccountId != null) {
                    entities.set(entry, "glAccountId", glAccountId);
                    completed++;
                }
            }
            if (entities.get(entry, "origAmount") == null && entities.get(entry, "amount") != null) {
                entities.set(entry, "origAmount", entities.get(entry, "amount"));
            }
        }
        entityManager.flush();
        return Map.of("completedEntries", completed);
    }

    /** Migrated from applications/accounting/minilang/ledger/GeneralLedgerServices.xml:199. */
    private Map<String, Object> createTransactionAndEntries(Map<String, Object> input) {
        Object rawEntries = input.get("acctgTransEntries");
        if (!(rawEntries instanceof List<?> entryValues) || entryValues.isEmpty()) {
            return Map.of();
        }
        List<Map<String, Object>> normalized = new ArrayList<>();
        for (Object raw : entryValues) {
            if (!(raw instanceof Map<?, ?> map)) {
                throw badRequest("acctgTransEntries must contain objects");
            }
            Map<String, Object> entry = stringMap(map);
            String organization = required(entry, "organizationPartyId");
            List<?> roles = entities.findByEquality(entities.entityClass("PartyRole"),
                    Map.of("partyId", organization, "roleTypeId", "INTERNAL_ORGANIZATIO"));
            if (roles.isEmpty()) {
                continue;
            }
            Object preference = findOne("PartyAcctgPreference", Map.of("organizationPartyId", organization));
            if (preference != null && "N".equals(entities.get(preference, "enableAccounting"))) {
                return Map.of();
            }
            Object amount = entry.get("amount");
            if (amount == null && entry.get("origAmount") != null) {
                String originalCurrency = text(entry.get("origCurrencyUomId"));
                Object baseCurrency = preference == null ? null : entities.get(preference, "baseCurrencyUomId");
                if (originalCurrency != null && baseCurrency != null && !originalCurrency.equals(baseCurrency)) {
                    return Map.of();
                }
                entry.put("amount", entry.get("origAmount"));
            }
            BigDecimal original = decimal(entry.get("origAmount"));
            if (original.signum() < 0) {
                entry.put("origAmount", original.abs());
                entry.put("amount", decimal(entry.get("amount")).abs());
                entry.put("debitCreditFlag", "D".equals(entry.get("debitCreditFlag")) ? "C" : "D");
            }
            normalized.add(entry);
        }
        if (normalized.isEmpty()) {
            return Map.of();
        }
        return Map.of();
    }

    /** Migrated from applications/accounting/minilang/ledger/GeneralLedgerServices.xml:543. */
    private Map<String, Object> closePeriod(Map<String, Object> input) {
        Object period = requireOne("CustomTimePeriod",
                Map.of("customTimePeriodId", required(input, "customTimePeriodId")));
        Map<String, Object> periodValues = entities.toMap(period);
        String organization = text(periodValues.get("organizationPartyId"));
        Date from = asDate(periodValues.get("fromDate"));
        Date thru = asDate(periodValues.get("thruDate"));
        List<?> children = entities.findByEquality(entities.entityClass("CustomTimePeriod"),
                Map.of("parentPeriodId", input.get("customTimePeriodId")));
        if (children.stream().anyMatch(child -> !"Y".equals(entities.get(child, "isClosed")))) {
            throw badRequest("Cannot close a financial period with open child periods");
        }

        Date lastClosedDate = from;
        if (organization != null) {
            String periodType = text(periodValues.get("periodTypeId"));
            Date latestClosedEnd = null;
            for (Object candidate : entities.findByEquality(entities.entityClass("CustomTimePeriod"),
                    Map.of("organizationPartyId", organization))) {
                Date candidateEnd = asDate(entities.get(candidate, "thruDate"));
                if ("Y".equals(entities.get(candidate, "isClosed"))
                        && equal(periodType, entities.get(candidate, "periodTypeId"))
                        && candidateEnd != null && (thru == null || candidateEnd.before(thru))
                        && (latestClosedEnd == null || candidateEnd.after(latestClosedEnd))) {
                    latestClosedEnd = candidateEnd;
                }
            }
            if (latestClosedEnd != null) {
                lastClosedDate = latestClosedEnd;
            }

            BigDecimal debits = BigDecimal.ZERO;
            BigDecimal credits = BigDecimal.ZERO;
            for (Object entry : entities.findByEquality(entities.entityClass("AcctgTransEntry"),
                    Map.of("organizationPartyId", organization))) {
                Object transaction = findOne("AcctgTrans",
                        Map.of("acctgTransId", entities.get(entry, "acctgTransId")));
                if (transaction == null || !"Y".equals(entities.get(transaction, "isPosted"))
                        || !"ACTUAL".equals(entities.get(transaction, "glFiscalTypeId"))
                        || "PERIOD_CLOSING".equals(entities.get(transaction, "acctgTransTypeId"))) {
                    continue;
                }
                Date transactionDate = asDate(entities.get(transaction, "transactionDate"));
                if (transactionDate == null || lastClosedDate != null && transactionDate.before(lastClosedDate)
                        || thru != null && !transactionDate.before(thru)) {
                    continue;
                }
                Object entryAccountId = entities.get(entry, "glAccountId");
                if (entryAccountId == null) {
                    continue;
                }
                Object account = findOne("GlAccount", Map.of("glAccountId", entryAccountId));
                String accountClass = account == null ? null : text(entities.get(account, "glAccountClassId"));
                if (!hasClassAncestor(accountClass, "EXPENSE")
                        && !hasClassAncestor(accountClass, "REVENUE")
                        && !hasClassAncestor(accountClass, "INCOME")) {
                    continue;
                }
                BigDecimal amount = decimal(entities.get(entry, "amount"));
                if ("D".equals(entities.get(entry, "debitCreditFlag"))) {
                    debits = debits.add(amount);
                } else {
                    credits = credits.add(amount);
                }
            }
            BigDecimal periodProfitLoss = credits.subtract(debits);
            Object profitLossDefault = findOne("GlAccountTypeDefault", Map.of(
                    "organizationPartyId", organization, "glAccountTypeId", "PROFIT_LOSS_ACCOUNT"));
            Object profitLossAccountId = profitLossDefault == null ? null
                    : entities.get(profitLossDefault, "glAccountId");
            if (profitLossAccountId != null) {
                Object history = findOne("GlAccountHistory", Map.of(
                        "organizationPartyId", organization,
                        "customTimePeriodId", periodValues.get("customTimePeriodId"),
                        "glAccountId", profitLossAccountId));
                if (history != null && decimal(entities.get(history, "endingBalance")).compareTo(periodProfitLoss) != 0) {
                    throw badRequest("Posted balance does not match the period profit/loss");
                }
            }

            for (Object assignment : entities.findByEquality(entities.entityClass("GlAccountOrganization"),
                    Map.of("organizationPartyId", organization))) {
                Date assignmentFrom = asDate(entities.get(assignment, "fromDate"));
                Date assignmentThru = asDate(entities.get(assignment, "thruDate"));
                if (thru != null && assignmentFrom != null && !assignmentFrom.before(thru)
                        || from != null && assignmentThru != null && assignmentThru.before(from)) {
                    continue;
                }
                String accountId = text(entities.get(assignment, "glAccountId"));
                Map<String, Object> historyKey = Map.of("organizationPartyId", organization,
                        "customTimePeriodId", periodValues.get("customTimePeriodId"), "glAccountId", accountId);
                Object history = findOne("GlAccountHistory", historyKey);
                if (history == null) {
                    history = entities.newEntity(entities.entityClass("GlAccountHistory"));
                    for (Map.Entry<String, Object> key : historyKey.entrySet()) {
                        entities.set(history, key.getKey(), key.getValue());
                    }
                    entityManager.persist(history);
                }
                computeAndStoreHistory(historyKey);
            }
        }
        entities.set(period, "isClosed", "Y");
        entityManager.flush();
        return Map.of();
    }

    /** Migrated from applications/accounting/minilang/ledger/GeneralLedgerServices.xml:747. */
    private Map<String, Object> computeBalance(Map<String, Object> input) {
        Object period = requireOne("CustomTimePeriod",
                Map.of("customTimePeriodId", required(input, "customTimePeriodId")));
        Object account = requireOne("GlAccount", Map.of("glAccountId", required(input, "glAccountId")));
        Date from = asDate(entities.get(period, "fromDate"));
        Date thru = asDate(entities.get(period, "thruDate"));
        BigDecimal debitBefore = BigDecimal.ZERO;
        BigDecimal creditBefore = BigDecimal.ZERO;
        BigDecimal debitEnd = BigDecimal.ZERO;
        BigDecimal creditEnd = BigDecimal.ZERO;
        for (Object entry : entities.findByEquality(entities.entityClass("AcctgTransEntry"),
                Map.of("organizationPartyId", required(input, "organizationPartyId"),
                        "glAccountId", required(input, "glAccountId")))) {
            Object transaction = findOne("AcctgTrans", Map.of("acctgTransId", entities.get(entry, "acctgTransId")));
            if (transaction == null || !"Y".equals(entities.get(transaction, "isPosted"))
                    || !"ACTUAL".equals(entities.get(transaction, "glFiscalTypeId"))) {
                continue;
            }
            Date date = asDate(entities.get(transaction, "transactionDate"));
            if (date == null || !date.before(thru)) {
                continue;
            }
            BigDecimal amount = decimal(entities.get(entry, "amount"));
            boolean debitEntry = "D".equals(entities.get(entry, "debitCreditFlag"));
            if (from != null && date.before(from)) {
                if (debitEntry) {
                    debitBefore = debitBefore.add(amount);
                } else {
                    creditBefore = creditBefore.add(amount);
                }
            }
            if (debitEntry) {
                debitEnd = debitEnd.add(amount);
            } else {
                creditEnd = creditEnd.add(amount);
            }
        }
        boolean debitAccount = hasClassAncestor(text(entities.get(account, "glAccountClassId")), "DEBIT");
        BigDecimal opening = debitAccount ? debitBefore.subtract(creditBefore) : creditBefore.subtract(debitBefore);
        BigDecimal ending = debitAccount ? debitEnd.subtract(creditEnd) : creditEnd.subtract(debitEnd);
        return Map.of("openingBalance", opening, "endingBalance", ending,
                "postedDebits", debitEnd.subtract(debitBefore), "postedCredits", creditEnd.subtract(creditBefore));
    }

    /** Migrated from applications/accounting/minilang/ledger/GeneralLedgerServices.xml:827. */
    private Map<String, Object> computeAndStoreHistory(Map<String, Object> input) {
        Map<String, Object> balances = computeBalance(input);
        Map<String, Object> key = Map.of("organizationPartyId", required(input, "organizationPartyId"),
                "customTimePeriodId", required(input, "customTimePeriodId"),
                "glAccountId", required(input, "glAccountId"));
        Object existing = findOne("GlAccountHistory", key);
        Object history = existing == null
                ? entities.newEntity(entities.entityClass("GlAccountHistory")) : existing;
        if (existing == null) {
            key.forEach((name, value) -> entities.set(history, name, value));
            entityManager.persist(history);
        }
        balances.forEach((name, value) -> entities.set(history, name, value));
        entityManager.flush();
        return Map.of();
    }

    /** Migrated from applications/accounting/minilang/ledger/GeneralLedgerServices.xml:846. */
    private Map<String, Object> prepareIncomeStatement(Map<String, Object> input) {
        String organization = required(input, "organizationPartyId");
        Date from = asDate(input.get("fromDate"));
        Date thru = asDate(input.get("thruDate"));
        Set<String> organizationIds = new java.util.LinkedHashSet<>(
                parties.associatedPartyIdsByRelationshipType(organization, "GROUP_ROLLUP"));
        organizationIds.add(organization);
        Map<String, BigDecimal> incomeTotals = new LinkedHashMap<>();
        Map<String, BigDecimal> expenseTotals = new LinkedHashMap<>();
        Map<String, BigDecimal> currentPeriodTotals = new LinkedHashMap<>();
        Object fiscalPeriod = null;
        if (from != null) {
            for (Object period : entities.findByEquality(entities.entityClass("CustomTimePeriod"),
                    Map.of("organizationPartyId", organization))) {
                Date periodFrom = asDate(entities.get(period, "fromDate"));
                Date periodThru = asDate(entities.get(period, "thruDate"));
                if (periodFrom != null && !from.before(periodFrom)
                        && (periodThru == null || !from.after(periodThru))) {
                    fiscalPeriod = period;
                    break;
                }
            }
        }
        for (Object entry : entityManager.createQuery("select entry from AcctgTransEntry entry", Object.class)
                .setMaxResults(100000).getResultList()) {
            if (!organizationIds.contains(text(entities.get(entry, "organizationPartyId")))) {
                continue;
            }
            Object transaction = findOne("AcctgTrans", Map.of("acctgTransId", entities.get(entry, "acctgTransId")));
            if (transaction == null || !"Y".equals(entities.get(transaction, "isPosted"))
                    || !equal(input.get("glFiscalTypeId"), entities.get(transaction, "glFiscalTypeId"))
                    || "PERIOD_CLOSING".equals(entities.get(transaction, "acctgTransTypeId"))) {
                continue;
            }
            Date date = asDate(entities.get(transaction, "transactionDate"));
            if (date == null || from != null && date.before(from) || thru != null && !date.before(thru)) {
                continue;
            }
            String accountId = text(entities.get(entry, "glAccountId"));
            if (accountId == null) {
                continue;
            }
            Object account = findOne("GlAccount", Map.of("glAccountId", accountId));
            String accountClass = account == null ? null : text(entities.get(account, "glAccountClassId"));
            boolean expense = hasClassAncestor(accountClass, "EXPENSE");
            if (!expense && !hasClassAncestor(accountClass, "REVENUE")
                    && !hasClassAncestor(accountClass, "INCOME")) {
                continue;
            }
            BigDecimal amount = decimal(entities.get(entry, "amount"));
            boolean creditAccount = hasClassAncestor(accountClass, "CREDIT");
            boolean debitAccount = hasClassAncestor(accountClass, "DEBIT");
            if ("D".equals(entities.get(entry, "debitCreditFlag")) && creditAccount
                    || "C".equals(entities.get(entry, "debitCreditFlag")) && debitAccount) {
                amount = amount.negate();
            }
            if (expense) {
                amount = amount.negate();
            }
            (expense ? expenseTotals : incomeTotals).merge(accountId, amount, BigDecimal::add);

            Date periodFrom = fiscalPeriod == null ? from : asDate(entities.get(fiscalPeriod, "fromDate"));
            String entryOrganization = text(entities.get(entry, "organizationPartyId"));
            if (entryOrganization != null && entryOrganization.equals(organization)
                    && periodFrom != null && !date.before(periodFrom) && thru != null && date.before(thru)) {
                BigDecimal entryAmount = decimal(entities.get(entry, "amount"));
                if ("C".equals(entities.get(entry, "debitCreditFlag"))) {
                    entryAmount = entryAmount.negate();
                }
                currentPeriodTotals.merge(accountId, entryAmount, BigDecimal::add);
            }
        }
        BigDecimal net = incomeTotals.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(expenseTotals.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add))
                .setScale(4, RoundingMode.HALF_UP);
        Map<String, Object> totals = new LinkedHashMap<>();
        totals.put("income", statementLines(incomeTotals, currentPeriodTotals));
        totals.put("expenses", statementLines(expenseTotals, currentPeriodTotals));
        return Map.of("totalNetIncome", net, "glAccountTotalsMap", totals);
    }

    private List<Map<String, Object>> statementLines(
            Map<String, BigDecimal> totals, Map<String, BigDecimal> currentPeriodTotals) {
        List<Map<String, Object>> rows = new ArrayList<>();
        totals.forEach((accountId, total) -> rows.add(Map.of(
                "glAccountId", accountId,
                "totalAmount", total.setScale(4, RoundingMode.HALF_UP),
                "totalOfCurrentFiscalPeriod", currentPeriodTotals.getOrDefault(accountId, BigDecimal.ZERO)
                        .setScale(4, RoundingMode.HALF_UP))));
        return rows;
    }

    /** Migrated from applications/accounting/minilang/ledger/GeneralLedgerServices.xml:333. */
    private Map<String, Object> getGlAccountFromType(Map<String, Object> input) {
        String organization = required(input, "organizationPartyId");
        String type = text(input.get("glAccountTypeId"));
        Object accountId = null;
        if ("ITEM_VARIANCE".equals(input.get("acctgTransTypeId")) && type != null) {
            Object mapping = findOne("VarianceReasonGlAccount", Map.of(
                    "organizationPartyId", organization, "varianceReasonId", type));
            accountId = mapping == null ? null : entities.get(mapping, "glAccountId");
        }
        if (accountId == null && "DEPRECIATION".equals(input.get("acctgTransTypeId"))
                && text(input.get("fixedAssetId")) != null) {
            List<?> mappings = entities.findByEquality(entities.entityClass("FixedAssetTypeGlAccount"),
                    Map.of("fixedAssetId", input.get("fixedAssetId"), "organizationPartyId", organization));
            Object mapping = mappings.isEmpty() ? null : mappings.get(0);
            if (mapping == null) {
                Object fixedAsset = findOne("FixedAsset", Map.of("fixedAssetId", input.get("fixedAssetId")));
                String fixedAssetTypeId = fixedAsset == null ? null
                        : text(entities.get(fixedAsset, "fixedAssetTypeId"));
                if (fixedAssetTypeId != null) {
                    mappings = entities.findByEquality(entities.entityClass("FixedAssetTypeGlAccount"),
                                    Map.of("fixedAssetId", "_NA_", "organizationPartyId", organization)).stream()
                            .filter(candidate -> fixedAssetTypeId.equals(entities.get(candidate, "fixedAssetTypeId"))
                                    || "_NA_".equals(entities.get(candidate, "fixedAssetTypeId")))
                            .toList();
                    mapping = mappings.isEmpty() ? null : mappings.get(0);
                }
            }
            String mappedAccount = mapping == null ? null : text(entities.get(mapping,
                    "C".equals(input.get("debitCreditFlag")) ? "accDepGlAccountId" : "depGlAccountId"));
            if (mappedAccount != null) {
                return Map.of("glAccountId", mappedAccount);
            }
        }
        if (accountId == null && type != null && input.get("partyId") != null && input.get("roleTypeId") != null) {
            Object mapping = findOne("PartyGlAccount", Map.of("organizationPartyId", organization,
                    "partyId", input.get("partyId"), "roleTypeId", input.get("roleTypeId"),
                    "glAccountTypeId", type));
            accountId = mapping == null ? null : entities.get(mapping, "glAccountId");
        }
        if (accountId == null && input.get("paymentId") != null
                && (("OUTGOING_PAYMENT".equals(input.get("acctgTransTypeId"))
                        && "C".equals(input.get("debitCreditFlag")))
                    || ("INCOMING_PAYMENT".equals(input.get("acctgTransTypeId"))
                        && "D".equals(input.get("debitCreditFlag"))))) {
            Object payment = findOne("Payment", Map.of("paymentId", input.get("paymentId")));
            if (payment != null) {
                Object paymentMethod = findOne("PaymentMethod",
                        Map.of("paymentMethodId", entities.get(payment, "paymentMethodId")));
                if (paymentMethod != null) {
                    accountId = entities.get(paymentMethod, "glAccountId");
                }
                if (accountId == null) {
                    if ("CREDIT_CARD".equals(entities.get(payment, "paymentMethodTypeId")) && paymentMethod != null) {
                        Object creditCard = findOne("CreditCard",
                                Map.of("paymentMethodId", entities.get(paymentMethod, "paymentMethodId")));
                        if (creditCard != null && entities.get(creditCard, "cardType") != null) {
                            Object cardMapping = findOne("CreditCardTypeGlAccount", Map.of(
                                    "organizationPartyId", organization, "cardType", entities.get(creditCard, "cardType")));
                            accountId = cardMapping == null ? null : entities.get(cardMapping, "glAccountId");
                        }
                    }
                }
                if (accountId == null) {
                    Object mapping = findOne("PaymentMethodTypeGlAccount", Map.of("organizationPartyId", organization,
                            "paymentMethodTypeId", entities.get(payment, "paymentMethodTypeId")));
                    accountId = mapping == null ? null : entities.get(mapping, "glAccountId");
                }
            }
            return accountId == null ? Map.of() : Map.of("glAccountId", accountId);
        }
        if (accountId == null && text(input.get("productId")) != null && type != null) {
            Map<String, Object> mapping = products.productGlAccount(text(input.get("productId")),
                    organization, type);
            accountId = mapping == null ? null : mapping.get("glAccountId");
            if (accountId == null) {
                for (Map<String, Object> membership : products.productCategoryMemberships(
                        text(input.get("productId")))) {
                    mapping = products.productCategoryGlAccount(text(membership.get("productCategoryId")),
                            organization, type);
                    if (mapping != null && mapping.get("glAccountId") != null) {
                        accountId = mapping.get("glAccountId");
                        break;
                    }
                }
            }
        }
        String transactionType = text(input.get("acctgTransTypeId"));
        String debitCredit = text(input.get("debitCreditFlag"));
        boolean invoiceMappingBranch = text(input.get("invoiceId")) != null && type != null
                && ("PURCHASE_INVOICE".equals(transactionType) && "D".equals(debitCredit)
                    || "CUST_RTN_INVOICE".equals(transactionType) && "D".equals(debitCredit)
                    || "SALES_INVOICE".equals(transactionType) && "C".equals(debitCredit));
        if (invoiceMappingBranch) {
            Object mapping = findOne("InvoiceItemTypeGlAccount", Map.of(
                    "invoiceItemTypeId", type, "organizationPartyId", organization));
            accountId = mapping == null ? null : entities.get(mapping, "glAccountId");
            if (accountId == null) {
                Object itemType = findOne("InvoiceItemType", Map.of("invoiceItemTypeId", type));
                accountId = itemType == null ? null : entities.get(itemType, "defaultGlAccountId");
            }
            if (accountId == null && text(input.get("productId")) != null) {
                String defaultType = switch (transactionType) {
                    case "PURCHASE_INVOICE" -> "UNINVOICED_SHIP_RCPT";
                    case "CUST_RTN_INVOICE" -> "SALES_RETURNS";
                    default -> "SALES_ACCOUNT";
                };
                Object defaultMapping = findOne("GlAccountTypeDefault", Map.of(
                        "organizationPartyId", organization, "glAccountTypeId", defaultType));
                accountId = defaultMapping == null ? null : entities.get(defaultMapping, "glAccountId");
            }
            return accountId == null ? Map.of() : Map.of("glAccountId", accountId);
        }
        if (accountId == null && type != null) {
            Object mapping = findOne("GlAccountTypeDefault", Map.of(
                    "organizationPartyId", organization, "glAccountTypeId", type));
            accountId = mapping == null ? null : entities.get(mapping, "glAccountId");
        }
        return accountId == null ? Map.of() : Map.of("glAccountId", accountId);
    }

    /** Migrated from applications/accounting/minilang/ledger/GeneralLedgerServices.xml:532. */
    private Map<String, Object> getInventoryItemOwner(Map<String, Object> input) {
        Map<String, Object> item = inventory.findInventoryItem(required(input, "inventoryItemId"));
        if (item == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "InventoryItem not found");
        }
        Object owner = item.get("ownerPartyId");
        return owner == null ? Map.of() : Map.of("ownerPartyId", owner);
    }

    /** Migrated from applications/accounting/minilang/ledger/GeneralLedgerServices.xml:2459. */
    private Map<String, Object> copyTransaction(Map<String, Object> input) {
        String sourceId = required(input, "fromAcctgTransId");
        requireOne("AcctgTrans", Map.of("acctgTransId", sourceId));
        return Map.of();
    }

    /** Migrated from applications/accounting/minilang/ledger/GeneralLedgerServices.xml:2779. */
    private Map<String, Object> updateCategoryMember(Map<String, Object> input) {
        if (input.get("amountPercentage") == null) {
            return Map.of();
        }
        Map<String, Object> key = Map.of("glAccountId", required(input, "glAccountId"),
                "glAccountCategoryId", required(input, "glAccountCategoryId"),
                "fromDate", required(input, "fromDate"));
        Object current = requireOne("GlAccountCategoryMember", key);
        if (decimal(entities.get(current, "amountPercentage")).compareTo(decimal(input.get("amountPercentage"))) != 0) {
            Object clone = entities.newEntity(entities.entityClass("GlAccountCategoryMember"));
            entities.toMap(current).forEach((name, value) -> {
                if (value != null) {
                    entities.set(clone, name, value);
                }
            });
            entities.set(current, "thruDate", Timestamp.from(Instant.now()));
            entities.set(clone, "fromDate", Timestamp.from(Instant.now()));
            entities.set(clone, "amountPercentage", input.get("amountPercentage"));
            entityManager.persist(clone);
        }
        entityManager.flush();
        return Map.of();
    }

    /** Migrated from applications/accounting/minilang/ledger/GeneralLedgerServices.xml:2737. */
    private Map<String, Object> createCategoryMemberFromCostCenter(Map<String, Object> input) {
        BigDecimal total = decimal(input.get("totalAmountPercentage"));
        BigDecimal amount = decimal(input.get("amountPercentage"));
        Map<String, Object> filters = Map.of("glAccountId", required(input, "glAccountId"),
                "glAccountCategoryId", required(input, "glAccountCategoryId"));
        List<?> found = entities.findByEquality(entities.entityClass("GlAccountCategoryMember"), filters).stream()
                .filter(member -> isActiveAt(member, new Date()))
                .toList();
        if (total.compareTo(new BigDecimal("100")) == 0) {
            if (found.isEmpty()) {
                Map<String, Object> values = Map.of("glAccountId", filters.get("glAccountId"),
                        "glAccountCategoryId", filters.get("glAccountCategoryId"),
                        "amountPercentage", amount, "fromDate", Timestamp.from(Instant.now()));
                dispatcher.execute(COMPONENT, "createGlAccountCategoryMember", values);
            } else {
                Map<String, Object> update = new LinkedHashMap<>(filters);
                update.put("fromDate", entities.get(found.get(0), "fromDate"));
                update.put("amountPercentage", amount);
                updateCategoryMember(update);
            }
        } else if (total.compareTo(BigDecimal.ZERO) == 0) {
            if (!found.isEmpty()) {
                entities.set(found.get(0), "thruDate", Timestamp.from(Instant.now()));
            }
        } else {
            throw badRequest("Total amount percentage must equal 100 or 0");
        }
        entityManager.flush();
        return Map.of();
    }

    /** Migrated from applications/accounting/minilang/ledger/GeneralLedgerServices.xml:2795. */
    private Map<String, Object> getCostCenterAssignments(Map<String, Object> input) {
        String organization = required(input, "organizationPartyId");
        Set<String> organizationIds = new java.util.LinkedHashSet<>(
                parties.associatedPartyIdsByRelationshipType(organization, "GROUP_ROLLUP"));
        organizationIds.add(organization);
        List<Object> organizations = new ArrayList<>();
        for (String organizationId : organizationIds) {
            organizations.addAll(entities.findByEquality(entities.entityClass("GlAccountOrganization"),
                    Map.of("organizationPartyId", organizationId)));
        }
        List<?> categories = entities.findByEquality(entities.entityClass("GlAccountCategory"),
                Map.of("glAccountCategoryTypeId", "COST_CENTER"));
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Object assignment : organizations) {
            Object account = findOne("GlAccount", Map.of("glAccountId", entities.get(assignment, "glAccountId")));
            if (account == null) {
                continue;
            }
            Map<String, Object> row = new LinkedHashMap<>(entities.toMap(account));
            String assignedOrganization = text(entities.get(assignment, "organizationPartyId"));
            row.put("organizationPartyId", parties.groupName(assignedOrganization) + " [" + assignedOrganization + "]");
            for (Object category : categories) {
                List<?> memberships = entities.findByEquality(entities.entityClass("GlAccountCategoryMember"),
                        Map.of("glAccountId", entities.get(assignment, "glAccountId"),
                                "glAccountCategoryId", entities.get(category, "glAccountCategoryId"))).stream()
                        .filter(member -> isActiveAt(member, new Date()))
                        .toList();
                if (!memberships.isEmpty()) {
                    row.put(text(entities.get(category, "glAccountCategoryId")),
                            entities.get(memberships.get(0), "amountPercentage"));
                }
            }
            rows.add(row);
        }
        return Map.of("glAcctgAndAmountPercentageList", rows, "glAccountCategories", categories);
    }

    /** Migrated from applications/accounting/minilang/ledger/GeneralLedgerServices.xml:2838. */
    private Map<String, Object> getInventoryValuation(Map<String, Object> input) {
        Date from = asDate(input.get("fromDate"));
        Date thru = asDate(input.get("thruDate"));
        Map<String, List<Map<String, Object>>> byProduct = new LinkedHashMap<>();
        for (Map<String, Object> item : inventory.findProductInventoryItems()) {
            if (input.get("productId") != null && !equal(input.get("productId"), item.get("productId"))
                    || input.get("facilityId") != null && !equal(input.get("facilityId"), item.get("facilityId"))
                    || input.get("productCategoryId") != null
                    && !equal(input.get("productCategoryId"), item.get("primaryProductCategoryId"))) {
                continue;
            }
            Date received = asDate(item.get("datetimeReceived"));
            if (from != null && (received == null || received.before(from))
                    || thru != null && (received == null || received.after(thru))) {
                continue;
            }
            byProduct.computeIfAbsent(text(item.get("productId")), ignored -> new ArrayList<>()).add(item);
        }
        List<Map<String, Object>> valuations = new ArrayList<>();
        for (Map.Entry<String, List<Map<String, Object>>> product : byProduct.entrySet()) {
            BigDecimal quantity = BigDecimal.ZERO;
            BigDecimal cost = BigDecimal.ZERO;
            BigDecimal averageCost = BigDecimal.ZERO;
            String currency = null;
            for (Map<String, Object> item : product.getValue()) {
                BigDecimal itemQuantity = decimal(item.get("quantityOnHandTotal"));
                if ("COGS_AVG_COST".equals(input.get("cogsMethodId"))) {
                    averageCost = inventory.averageCost(item);
                }
                quantity = quantity.add(itemQuantity);
                cost = cost.add(itemQuantity.multiply(averageCost));
                currency = text(item.get("currencyUomId"));
            }
            Map<String, Object> valuation = new LinkedHashMap<>();
            valuation.put("productId", product.getKey());
            valuation.put("totalQuantityOnHand", quantity);
            valuation.put("totalInventoryCost", cost);
            valuation.put("productAverageCost", averageCost);
            if (currency != null) {
                valuation.put("currencyUomId", currency);
            }
            valuations.add(valuation);
        }
        return valuations.isEmpty() ? Map.of() : Map.of("inventoryValuationList", valuations);
    }

    /** Migrated from applications/accounting/minilang/ledger/GeneralLedgerServices.xml:2889. */
    private Map<String, Object> setReconciliationStatus(Map<String, Object> input) {
        Object reconciliation = requireOne("GlReconciliation",
                Map.of("glReconciliationId", required(input, "glReconciliationId")));
        String oldStatus = text(entities.get(reconciliation, "statusId"));
        String newStatus = required(input, "statusId");
        if (!newStatus.equals(oldStatus)) {
            List<?> changes = entities.findByEquality(entities.entityClass("StatusValidChange"),
                    Map.of("statusId", oldStatus, "statusIdTo", newStatus));
            if (changes.isEmpty()) {
                throw badRequest("Invalid status change from " + oldStatus + " to " + newStatus);
            }
            entities.set(reconciliation, "statusId", newStatus);
            entityManager.flush();
        }
        return Map.of("oldStatusId", oldStatus);
    }

    private Map<String, Object> entityValues(String entity, Map<String, ?> values) {
        Set<String> valid = Set.copyOf(
                entities.toMap(entities.newEntity(entities.entityClass(entity))).keySet());
        Map<String, Object> result = new LinkedHashMap<>();
        values.forEach((name, value) -> {
            if (valid.contains(name) && value != null) {
                result.put(name, value);
            }
        });
        return result;
    }

    private void setKnown(Object entity, Map<String, ?> values, boolean includePrimary) {
        Set<String> keys = Set.copyOf(entities.primaryKeys(entity.getClass()));
        Set<String> valid = Set.copyOf(entities.toMap(entity).keySet());
        values.forEach((name, value) -> {
            if (valid.contains(name) && (includePrimary || !keys.contains(name)) && value != null) {
                entities.set(entity, name, value);
            }
        });
    }

    private Object requireOne(String name, Map<String, ?> key) {
        Object value = findOne(name, key);
        if (value == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, name + " not found");
        }
        return value;
    }

    private Object findOne(String name, Map<String, ?> key) {
        List<?> values = entities.findByEquality(entities.entityClass(name), key);
        return values.isEmpty() ? null : values.get(0);
    }

    private boolean hasClassAncestor(String classId, String targetId) {
        String current = classId;
        while (current != null) {
            if (targetId.equals(current)) {
                return true;
            }
            Object accountClass = findOne("GlAccountClass", Map.of("glAccountClassId", current));
            current = accountClass == null ? null : text(entities.get(accountClass, "parentClassId"));
        }
        return false;
    }

    private boolean isActiveAt(Object value, Date date) {
        Date from = asDate(entities.get(value, "fromDate"));
        Date thru = asDate(entities.get(value, "thruDate"));
        return (from == null || !date.before(from)) && (thru == null || !date.after(thru));
    }

    private static Map<String, Object> stringMap(Map<?, ?> values) {
        Map<String, Object> result = new LinkedHashMap<>();
        values.forEach((key, value) -> result.put(key.toString(), value));
        return result;
    }

    private static String required(Map<String, ?> input, String name) {
        String value = text(input.get(name));
        if (blank(value)) {
            throw badRequest("Missing required parameter: " + name);
        }
        return value;
    }

    private static boolean blank(Object value) {
        return value == null || value.toString().isBlank();
    }

    private static String text(Object value) {
        return value == null ? null : value.toString();
    }

    private static BigDecimal decimal(Object value) {
        return value == null ? BigDecimal.ZERO : value instanceof BigDecimal amount
                ? amount : new BigDecimal(value.toString());
    }

    private static Date asDate(Object value) {
        if (value instanceof Date date) {
            return date;
        }
        return value == null ? null : Timestamp.valueOf(value.toString());
    }

    private static boolean equal(Object left, Object right) {
        return left == null ? right == null : left.toString().equals(text(right));
    }

    private static String currentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private static MigrationException badRequest(String message) {
        return new MigrationException(HttpStatus.BAD_REQUEST, message);
    }
}
