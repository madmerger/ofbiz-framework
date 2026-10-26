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
    private final MigrationServiceDispatcher dispatcher;
    private final ServiceHandlerRegistry registry;

    public GeneralLedgerServiceHandlers(
            EntityManager entityManager,
            EntityRegistry entities,
            InventoryAccountingPort inventory,
            MigrationServiceDispatcher dispatcher,
            ServiceHandlerRegistry registry) {
        this.entityManager = entityManager;
        this.entities = entities;
        this.inventory = inventory;
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
        Map<String, Object> transactionInput = entityValues("AcctgTrans", input);
        transactionInput.putIfAbsent("transactionDate", Timestamp.from(Instant.now()));
        Map<String, Object> transaction = dispatcher.execute(COMPONENT, "createAcctgTrans", transactionInput);
        String transactionId = text(transaction.get("acctgTransId"));
        for (String[] debitCredit : List.of(
                new String[] {required(input, "debitGlAccountId"), "D"},
                new String[] {required(input, "creditGlAccountId"), "C"})) {
            Map<String, Object> entry = entityValues("AcctgTransEntry", input);
            entry.put("acctgTransId", transactionId);
            entry.put("glAccountId", debitCredit[0]);
            entry.put("debitCreditFlag", debitCredit[1]);
            entry.put("acctgTransEntryTypeId", "_NA_");
            dispatcher.execute(COMPONENT, "createAcctgTransEntry", entry);
        }
        return Map.of("acctgTransId", transactionId);
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
        Map<String, Object> transactionInput = entityValues("AcctgTrans", input);
        transactionInput.putIfAbsent("transactionDate", Timestamp.from(Instant.now()));
        String transactionId = text(dispatcher.execute(COMPONENT, "createAcctgTrans", transactionInput)
                .get("acctgTransId"));
        for (Map<String, Object> entry : normalized) {
            entry.put("acctgTransId", transactionId);
            dispatcher.execute(COMPONENT, "createAcctgTransEntry", entry);
        }
        return Map.of("acctgTransId", transactionId);
    }

    /** Migrated from applications/accounting/minilang/ledger/GeneralLedgerServices.xml:543. */
    private Map<String, Object> closePeriod(Map<String, Object> input) {
        Object period = requireOne("CustomTimePeriod",
                Map.of("customTimePeriodId", required(input, "customTimePeriodId")));
        List<?> children = entities.findByEquality(entities.entityClass("CustomTimePeriod"),
                Map.of("parentPeriodId", input.get("customTimePeriodId"), "isClosed", "N"));
        if (!children.isEmpty()) {
            throw badRequest("Cannot close a financial period with open child periods");
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
        boolean debitAccount = Set.of("ASSET", "EXPENSE", "CONTRA_LIABILITY", "CONTRA_EQUITY")
                .contains(entities.get(account, "glAccountClassId"));
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
        Map<String, BigDecimal> totals = new LinkedHashMap<>();
        for (Object entry : entities.findByEquality(entities.entityClass("AcctgTransEntry"),
                Map.of("organizationPartyId", organization))) {
            Object transaction = findOne("AcctgTrans", Map.of("acctgTransId", entities.get(entry, "acctgTransId")));
            if (transaction == null || !"Y".equals(entities.get(transaction, "isPosted"))
                    || !equal(input.get("glFiscalTypeId"), entities.get(transaction, "glFiscalTypeId"))) {
                continue;
            }
            Date date = asDate(entities.get(transaction, "transactionDate"));
            if (date == null || from != null && date.before(from) || thru != null && !date.before(thru)) {
                continue;
            }
            String accountId = text(entities.get(entry, "glAccountId"));
            Object account = findOne("GlAccount", Map.of("glAccountId", accountId));
            if (account == null || !Set.of("EXPENSE", "REVENUE", "INCOME")
                    .contains(text(entities.get(account, "glAccountClassId")))) {
                continue;
            }
            BigDecimal amount = decimal(entities.get(entry, "amount"));
            BigDecimal signed = "C".equals(entities.get(entry, "debitCreditFlag")) ? amount : amount.negate();
            totals.merge(accountId, signed, BigDecimal::add);
        }
        BigDecimal net = totals.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return Map.of("totalNetIncome", net, "glAccountTotalsMap", totals);
    }

    /** Migrated from applications/accounting/minilang/ledger/GeneralLedgerServices.xml:333. */
    private Map<String, Object> getGlAccountFromType(Map<String, Object> input) {
        String organization = required(input, "organizationPartyId");
        String type = required(input, "glAccountTypeId");
        Object mapping = findOne("GlAccountTypeDefault", Map.of(
                "organizationPartyId", organization, "glAccountTypeId", type));
        Object accountId = mapping == null ? null : entities.get(mapping, "glAccountId");
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
        Object source = requireOne("AcctgTrans", Map.of("acctgTransId", sourceId));
        Map<String, Object> copy = entityValues("AcctgTrans", entities.toMap(source));
        copy.remove("acctgTransId");
        copy.put("transactionDate", Timestamp.from(Instant.now()));
        String targetId = text(dispatcher.execute(COMPONENT, "createAcctgTrans", copy).get("acctgTransId"));
        for (Object entry : entities.findByEquality(entities.entityClass("AcctgTransEntry"),
                Map.of("acctgTransId", sourceId))) {
            Map<String, Object> clone = entityValues("AcctgTransEntry", entities.toMap(entry));
            clone.remove("acctgTransId");
            clone.remove("acctgTransEntrySeqId");
            clone.put("acctgTransId", targetId);
            if ("Y".equals(input.get("revert"))) {
                clone.put("debitCreditFlag", "D".equals(entities.get(entry, "debitCreditFlag")) ? "C" : "D");
            }
            dispatcher.execute(COMPONENT, "createAcctgTransEntry", clone);
        }
        return Map.of("acctgTransId", targetId);
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
        List<?> found = entities.findByEquality(entities.entityClass("GlAccountCategoryMember"), filters);
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
        } else if (total.compareTo(BigDecimal.ZERO) == 0 && !found.isEmpty()) {
            entities.set(found.get(0), "thruDate", Timestamp.from(Instant.now()));
        } else {
            throw badRequest("Total amount percentage must equal 100 or 0");
        }
        entityManager.flush();
        return Map.of();
    }

    /** Migrated from applications/accounting/minilang/ledger/GeneralLedgerServices.xml:2795. */
    private Map<String, Object> getCostCenterAssignments(Map<String, Object> input) {
        String organization = required(input, "organizationPartyId");
        List<?> organizations = entities.findByEquality(entities.entityClass("GlAccountOrganization"),
                Map.of("organizationPartyId", organization));
        List<?> categories = entities.findByEquality(entities.entityClass("GlAccountCategory"),
                Map.of("glAccountCategoryTypeId", "COST_CENTER"));
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Object assignment : organizations) {
            Object account = findOne("GlAccount", Map.of("glAccountId", entities.get(assignment, "glAccountId")));
            if (account == null) {
                continue;
            }
            Map<String, Object> row = new LinkedHashMap<>(entities.toMap(account));
            for (Object category : categories) {
                List<?> memberships = entities.findByEquality(entities.entityClass("GlAccountCategoryMember"),
                        Map.of("glAccountId", entities.get(assignment, "glAccountId"),
                                "glAccountCategoryId", entities.get(category, "glAccountCategoryId")));
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
