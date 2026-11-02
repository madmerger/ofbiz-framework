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
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.SequenceService;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AcctgTransServiceHandlers {
    private static final String COMPONENT = "accounting";
    private final EntityManager entityManager;
    private final EntityRegistry entities;
    private final SequenceService sequences;
    private final ServiceHandlerRegistry registry;

    public AcctgTransServiceHandlers(
            EntityManager entityManager,
            EntityRegistry entities,
            SequenceService sequences,
            ServiceHandlerRegistry registry) {
        this.entityManager = entityManager;
        this.entities = entities;
        this.sequences = sequences;
        this.registry = registry;
    }

    @PostConstruct
    void registerHandlers() {
        registry.registerHandler(COMPONENT, "checkAcctgTransPosted", "VIEW", this::checkAcctgTransPosted);
        registry.registerHandler(COMPONENT, "createAcctgTransEntry", "ACCTG_ATX_CREATE", this::createAcctgTransEntry);
        registry.registerHandler(COMPONENT, "updateAcctgTransEntry", "ACCTG_ATX_UPDATE", this::updateAcctgTransEntry);
        registry.registerHandler(COMPONENT, "deleteAcctgTransEntry", "ACCTG_ATX_DELETE", this::deleteAcctgTransEntry);
        registry.registerHandler(COMPONENT, "calculateAcctgTransTrialBalance", "ACCTG_ATX_CREATE",
                this::calculateAcctgTransTrialBalance);
        registry.registerHandler(COMPONENT, "postAcctgTrans", "ACCTG_ATX_CREATE", this::postAcctgTrans);
        registry.registerHandler(COMPONENT, "getAcctgTransEntriesAndTransTotal", "VIEW",
                this::getAcctgTransEntriesAndTransTotal);
        registry.registerHandler(COMPONENT, "calculateGlAccountTrialBalance", "VIEW",
                this::calculateGlAccountTrialBalance);
    }

    /**
     * Migrated from applications/accounting/minilang/ledger/AcctgTransServices.xml:24.
     */
    private Map<String, Object> checkAcctgTransPosted(Map<String, Object> input) {
        Object transaction = requireOne("AcctgTrans", Map.of("acctgTransId", key(input, "acctgTransId")));
        if ("Y".equals(entities.get(transaction, "isPosted"))) {
            throw badRequest("Accounting transaction has already been posted");
        }
        return Map.of();
    }

    /**
     * Migrated from applications/accounting/minilang/ledger/AcctgTransServices.xml:39.
     */
    private Map<String, Object> createAcctgTransEntry(Map<String, Object> input) {
        String transactionId = key(input, "acctgTransId");
        Object transaction = requireOne("AcctgTrans", Map.of("acctgTransId", transactionId));
        if ("Y".equals(entities.get(transaction, "isPosted"))) {
            throw badRequest("Accounting transaction has already been posted");
        }
        Class<?> type = entities.entityClass("AcctgTransEntry");
        Object entry = entities.newEntity(type);
        Map<String, Object> values = new LinkedHashMap<>(input);
        String seqId = sequences.nextSubSequence("AcctgTransEntry",
                Map.of("acctgTransId", transactionId), "acctgTransEntrySeqId");
        entities.set(entry, "acctgTransId", transactionId);
        entities.set(entry, "acctgTransEntrySeqId", seqId);
        setNonPrimaryFields(entry, values);
        if (blank(entities.get(entry, "currencyUomId"))) {
            String organizationPartyId = text(input.get("organizationPartyId"));
            Object preference = organizationPartyId == null ? null : findOne(
                    "PartyAcctgPreference", Map.of("partyId", organizationPartyId));
            Object baseCurrency = preference == null ? null : entities.get(preference, "baseCurrencyUomId");
            if (baseCurrency != null) {
                entities.set(entry, "currencyUomId", baseCurrency);
            }
        }
        if (blank(entities.get(entry, "amount")) && entities.get(entry, "origAmount") != null) {
            String originalCurrency = text(entities.get(entry, "origCurrencyUomId"));
            String currency = text(entities.get(entry, "currencyUomId"));
            if (blank(originalCurrency)) {
                entities.set(entry, "origCurrencyUomId", currency);
                originalCurrency = currency;
            }
            if (originalCurrency != null && originalCurrency.equals(currency)) {
                entities.set(entry, "amount", entities.get(entry, "origAmount"));
            } else if (!blank(originalCurrency) && !blank(currency)) {
                throw badRequest("Currency conversion for AcctgTransEntry is not migrated");
            }
        }
        if (blank(entities.get(entry, "acctgTransEntryTypeId"))) {
            entities.set(entry, "acctgTransEntryTypeId", "_NA_");
        }
        entities.set(entry, "reconcileStatusId", "AES_NOT_RECONCILED");
        entityManager.persist(entry);
        entityManager.flush();
        return Map.of("acctgTransEntrySeqId", seqId);
    }

    /**
     * Migrated from applications/accounting/minilang/ledger/AcctgTransServices.xml:95.
     */
    private Map<String, Object> updateAcctgTransEntry(Map<String, Object> input) {
        Map<String, Object> primaryKey = entryKey(input);
        Object entry = requireOne("AcctgTransEntry", primaryKey);
        Object transaction = requireOne("AcctgTrans", Map.of("acctgTransId", primaryKey.get("acctgTransId")));
        if ("Y".equals(entities.get(transaction, "isPosted"))) {
            for (Map.Entry<String, Object> field : input.entrySet()) {
                String name = field.getKey();
                if (!List.of("acctgTransId", "acctgTransEntrySeqId", "reconcileStatusId", "userLogin", "locale")
                        .contains(name)
                        && !equal(field.getValue(), entities.get(entry, name))) {
                    throw badRequest("Posted accounting transaction entries can only change reconciliation status");
                }
            }
        }
        setNonPrimaryFields(entry, input);
        updateTransactionLastModified(transaction);
        entityManager.flush();
        return Map.of();
    }

    /**
     * Migrated from applications/accounting/minilang/ledger/AcctgTransServices.xml:115.
     */
    private Map<String, Object> deleteAcctgTransEntry(Map<String, Object> input) {
        Map<String, Object> primaryKey = entryKey(input);
        Object transaction = requireOne("AcctgTrans", Map.of("acctgTransId", primaryKey.get("acctgTransId")));
        if ("Y".equals(entities.get(transaction, "isPosted"))) {
            throw badRequest("Accounting transaction has already been posted");
        }
        Object entry = requireOne("AcctgTransEntry", primaryKey);
        entityManager.remove(entityManager.contains(entry) ? entry : entityManager.merge(entry));
        updateTransactionLastModified(transaction);
        entityManager.flush();
        return Map.of();
    }

    /**
     * Migrated from applications/accounting/minilang/ledger/AcctgTransServices.xml:129.
     */
    private Map<String, Object> calculateAcctgTransTrialBalance(Map<String, Object> input) {
        String transactionId = key(input, "acctgTransId");
        List<?> entries = AccountingEntityQueries.findByEquality(entityManager, entities.entityClass("AcctgTransEntry"),
                Map.of("acctgTransId", transactionId));
        BigDecimal debit = BigDecimal.ZERO;
        BigDecimal credit = BigDecimal.ZERO;
        for (Object entry : entries) {
            String flag = text(entities.get(entry, "debitCreditFlag"));
            BigDecimal amount = money(entities.get(entry, "amount"));
            if ("D".equals(flag)) {
                debit = debit.add(amount).setScale(4, java.math.RoundingMode.HALF_UP);
            } else if ("C".equals(flag)) {
                credit = credit.add(amount).setScale(4, java.math.RoundingMode.HALF_UP);
            } else {
                throw badRequest("Invalid debitCreditFlag on accounting transaction entry");
            }
        }
        BigDecimal difference = debit.subtract(credit).setScale(4, java.math.RoundingMode.HALF_UP);
        return Map.of("debitTotal", debit, "creditTotal", credit, "debitCreditDifference", difference);
    }

    /**
     * Migrated from applications/accounting/minilang/ledger/AcctgTransServices.xml:181.
     */
    private Map<String, Object> postAcctgTrans(Map<String, Object> input) {
        String transactionId = key(input, "acctgTransId");
        Object transaction = requireOne("AcctgTrans", Map.of("acctgTransId", transactionId));
        if ("Y".equals(entities.get(transaction, "isPosted"))) {
            throw badRequest("Accounting transaction has already been posted");
        }
        Map<String, Object> totals = calculateAcctgTransTrialBalance(input);
        BigDecimal debit = money(totals.get("debitTotal"));
        BigDecimal credit = money(totals.get("creditTotal"));
        BigDecimal difference = debit.subtract(credit);
        List<String> errors = new ArrayList<>();
        if (difference.compareTo(new BigDecimal("0.01")) >= 0
                || difference.compareTo(new BigDecimal("-0.01")) <= 0
                || (debit.signum() == 0 && credit.signum() != 0)
                || (credit.signum() == 0 && debit.signum() != 0)) {
            errors.add("Accounting transaction trial balance failed");
        }
        List<?> entries = new ArrayList<>(AccountingEntityQueries.findByEquality(entityManager, entities.entityClass("AcctgTransEntry"),
                Map.of("acctgTransId", transactionId)));
        entries = entries.stream().sorted(Comparator.comparing(
                entry -> text(entities.get(entry, "acctgTransEntrySeqId")))).toList();
        Date scheduled = asDate(entities.get(transaction, "scheduledPostingDate"));
        if (scheduled != null && scheduled.after(new Date())) {
            errors.add("Accounting transaction is not scheduled to be posted yet");
        }
        for (Object entry : entries) {
            if (blank(entities.get(entry, "glAccountId"))) {
                errors.add("GL account is required for every accounting transaction entry");
            }
            if (entities.get(entry, "amount") == null) {
                errors.add("Amount is required for every accounting transaction entry");
            }
            String organizationPartyId = text(entities.get(entry, "organizationPartyId"));
            List<?> periods = financialPeriods(organizationPartyId, asDate(entities.get(transaction, "transactionDate")));
            if (periods.isEmpty()) {
                errors.add("No open financial period found for transaction date");
            }
            if (periods.stream().anyMatch(period -> "Y".equals(entities.get(period, "isClosed")))) {
                errors.add("Financial period is closed");
            }
        }
        if ("Y".equals(input.get("verifyOnly"))) {
            return errors.isEmpty() ? Map.of() : Map.of("successMessageList", errors);
        }
        if (!errors.isEmpty()) {
            for (Object entry : entries) {
                Map<String, Object> preferences = accountingPreferences(text(entities.get(entry, "organizationPartyId")));
                if ("N".equals(preferences.get("enableAccounting"))) return Map.of();
                Object errorJournal = preferences.get("errorGlJournalId");
                if (!blank(errorJournal)) {
                    entities.set(transaction, "glJournalId", errorJournal);
                    return Map.of("successMessageList", List.of("The accounting transaction [" + transactionId
                            + "] has been posted to the Error Journal [" + errorJournal + "]."));
                }
                throw badRequest(String.join("; ", errors));
            }
            throw badRequest(String.join("; ", errors));
        }
        entities.set(transaction, "isPosted", "Y");
        entities.set(transaction, "postedDate", Timestamp.from(Instant.now()));
        updateTransactionLastModified(transaction);
        entityManager.flush();
        return Map.of();
    }

    /**
     * Migrated from applications/accounting/minilang/ledger/AcctgTransServices.xml:567.
     */
    private Map<String, Object> getAcctgTransEntriesAndTransTotal(Map<String, Object> input) {
        String organizationPartyId = key(input, "organizationPartyId");
        String glAccountId = key(input, "glAccountId");
        List<?> entries = AccountingEntityQueries.findByEquality(entityManager, entities.entityClass("AcctgTransEntry"),
                Map.of("organizationPartyId", organizationPartyId, "glAccountId", glAccountId));
        Date start = asDate(input.get("customTimePeriodStartDate"));
        Date end = asDate(input.get("customTimePeriodEndDate"));
        String posted = text(input.get("isPosted"));
        List<Map<String, Object>> outputRows = new ArrayList<>();
        BigDecimal debit = BigDecimal.ZERO;
        BigDecimal credit = BigDecimal.ZERO;
        for (Object entry : entries) {
            Object transaction = findOne("AcctgTrans", Map.of("acctgTransId", entities.get(entry, "acctgTransId")));
            if (transaction == null) {
                continue;
            }
            Date transactionDate = asDate(entities.get(transaction, "transactionDate"));
            if (posted != null && !posted.equals(entities.get(transaction, "isPosted"))) {
                continue;
            }
            if (start != null && (transactionDate == null || transactionDate.before(start))) {
                continue;
            }
            if (end != null && (transactionDate == null || !transactionDate.before(end))) {
                continue;
            }
            BigDecimal amount = money(entities.get(entry, "amount"));
            if ("D".equals(entities.get(entry, "debitCreditFlag"))) {
                debit = debit.add(amount);
            } else if ("C".equals(entities.get(entry, "debitCreditFlag"))) {
                credit = credit.add(amount);
            }
            Map<String, Object> row = new LinkedHashMap<>();
            for (String field : List.of("acctgTransId", "acctgTransEntrySeqId", "glAccountId",
                    "organizationPartyId", "debitCreditFlag", "amount", "currencyUomId",
                    "origAmount", "origCurrencyUomId", "reconcileStatusId")) {
                Object value = entities.get(entry, field);
                if (value != null) {
                    row.put(field, value);
                }
            }
            row.put("isPosted", entities.get(transaction, "isPosted"));
            row.put("transactionDate", entities.get(transaction, "transactionDate"));
            row.put("invoiceId", entities.get(transaction, "invoiceId"));
            row.put("paymentId", entities.get(transaction, "paymentId"));
            outputRows.add(row);
        }
        debit = debit.setScale(4, java.math.RoundingMode.HALF_UP);
        credit = credit.setScale(4, java.math.RoundingMode.HALF_UP);
        return Map.of("acctgTransAndEntries", outputRows, "debitTotal", debit, "creditTotal", credit,
                "debitCreditDifference", debit.subtract(credit));
    }

    /**
     * Migrated from applications/accounting/minilang/ledger/AcctgTransServices.xml:580.
     */
    private Map<String, Object> calculateGlAccountTrialBalance(Map<String, Object> input) {
        String glAccountId = key(input, "glAccountId");
        List<?> entries = AccountingEntityQueries.findByEquality(entityManager, entities.entityClass("AcctgTransEntry"),
                Map.of("glAccountId", glAccountId));
        Date start = asDate(input.get("fromDate"));
        Date end = asDate(input.get("thruDate"));
        String posted = text(input.get("isPosted"));
        BigDecimal debit = BigDecimal.ZERO;
        BigDecimal credit = BigDecimal.ZERO;
        for (Object entry : entries) {
            Object transaction = findOne("AcctgTrans", Map.of("acctgTransId", entities.get(entry, "acctgTransId")));
            if (transaction == null) {
                continue;
            }
            Date transactionDate = asDate(entities.get(transaction, "transactionDate"));
            if (posted != null && !posted.equals(entities.get(transaction, "isPosted"))) {
                continue;
            }
            if (start != null && (transactionDate == null || transactionDate.before(start))) {
                continue;
            }
            if (end != null && (transactionDate == null || !transactionDate.before(end))) {
                continue;
            }
            BigDecimal amount = money(entities.get(entry, "amount"));
            if ("D".equals(entities.get(entry, "debitCreditFlag"))) {
                debit = debit.add(amount);
            } else {
                credit = credit.add(amount);
            }
        }
        debit = debit.setScale(4, java.math.RoundingMode.HALF_UP);
        credit = credit.setScale(4, java.math.RoundingMode.HALF_UP);
        return Map.of("openingBalanceDebit", debit, "openingBalanceCredit", credit,
                "debitCreditDifference", debit.subtract(credit));
    }

    private List<?> financialPeriods(String organizationPartyId, Date transactionDate) {
        if (transactionDate == null || organizationPartyId == null) return List.of();
        java.util.Set<String> organizations = new java.util.LinkedHashSet<>();
        collectParents(organizationPartyId, organizations);
        List<Object> matches = new ArrayList<>();
        for (String organization : organizations) {
            for (Object period : AccountingEntityQueries.findByEquality(entityManager, entities.entityClass("CustomTimePeriod"),
                    Map.of("organizationPartyId", organization))) {
                String type = text(entities.get(period, "periodTypeId"));
                Date from = asDate(entities.get(period, "fromDate"));
                Date thru = asDate(entities.get(period, "thruDate"));
                if (List.of("FISCAL_YEAR", "FISCAL_QUARTER", "FISCAL_MONTH", "FISCAL_WEEK", "FISCAL_BIWEEK")
                        .contains(type) && from != null && !transactionDate.before(from)
                        && (thru == null || !transactionDate.after(thru))) matches.add(period);
            }
        }
        return matches;
    }

    private void collectParents(String organization, java.util.Set<String> visited) {
        if (!visited.add(organization)) return;
        for (String side : List.of("From", "To")) {
            String other = "From".equals(side) ? "To" : "From";
            for (Object relationship : AccountingEntityQueries.findByEquality(entityManager, entities.entityClass("PartyRelationship"),
                    Map.of("partyId" + side, organization, "partyRelationshipTypeId", "GROUP_ROLLUP",
                            "roleTypeId" + other, "PARENT_ORGANIZATION"))) {
                String role = text(entities.get(relationship, "roleTypeId" + side));
                java.util.Set<String> seen = new java.util.HashSet<>();
                while (role != null && seen.add(role) && !"ORGANIZATION_UNIT".equals(role)) {
                    Object type = findOne("RoleType", Map.of("roleTypeId", role));
                    role = type == null ? null : text(entities.get(type, "parentTypeId"));
                }
                if ("ORGANIZATION_UNIT".equals(role) && activeRelationship(relationship)) {
                    collectParents(text(entities.get(relationship, "partyId" + other)), visited);
                }
            }
        }
    }

    private boolean activeRelationship(Object relationship) {
        Date now = new Date();
        Date from = asDate(entities.get(relationship, "fromDate"));
        Date thru = asDate(entities.get(relationship, "thruDate"));
        return (from == null || !from.after(now)) && (thru == null || thru.after(now));
    }

    private Map<String, Object> accountingPreferences(String organization) {
        Map<String, Object> result = new LinkedHashMap<>();
        java.util.Set<String> visited = new java.util.HashSet<>();
        while (organization != null && visited.add(organization)) {
            Object preference = findOne("PartyAcctgPreference", Map.of("partyId", organization));
            boolean missing = preference == null;
            if (preference != null) {
                for (var field : entities.toMap(preference).entrySet()) {
                    if (blank(field.getValue())) missing = true;
                    else result.put(field.getKey(), field.getValue());
                }
            }
            if (!missing) break;
            List<?> parents = AccountingEntityQueries.findByEquality(entityManager, entities.entityClass("PartyRelationship"),
                    Map.of("partyIdTo", organization, "partyRelationshipTypeId", "GROUP_ROLLUP", "roleTypeIdFrom", "_NA_", "roleTypeIdTo", "_NA_"));
            organization = parents.stream().filter(this::activeRelationship).findFirst()
                    .map(parent -> text(entities.get(parent, "partyIdFrom"))).orElse(null);
        }
        return result;
    }

    private void updateTransactionLastModified(Object transaction) {
        String userLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        entities.set(transaction, "lastModifiedByUserLogin", userLoginId);
        entities.set(transaction, "lastModifiedDate", Timestamp.from(Instant.now()));
    }

    private void setNonPrimaryFields(Object entity, Map<String, ?> values) {
        List<String> keys = entities.primaryKeys(entity.getClass());
        values.forEach((name, value) -> {
            if (value != null && !keys.contains(name)
                    && !List.of("userLogin", "locale", "purposeEnumId").contains(name)) {
                entities.set(entity, name, value);
            }
        });
    }

    private Object requireOne(String entityName, Map<String, ?> key) {
        Object value = findOne(entityName, key);
        if (value == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, entityName + " not found");
        }
        return value;
    }

    private Object findOne(String entityName, Map<String, ?> key) {
        List<?> matches = AccountingEntityQueries.findByEquality(entityManager, entities.entityClass(entityName), key);
        return matches.isEmpty() ? null : matches.get(0);
    }

    private Map<String, Object> entryKey(Map<String, ?> input) {
        return Map.of("acctgTransId", key(input, "acctgTransId"),
                "acctgTransEntrySeqId", key(input, "acctgTransEntrySeqId"));
    }

    private static String key(Map<String, ?> input, String name) {
        String value = text(input.get(name));
        if (value == null || value.isBlank()) {
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

    private static boolean equal(Object left, Object right) {
        return left == null ? right == null : left.toString().equals(text(right));
    }

    private static BigDecimal money(Object value) {
        return value == null ? BigDecimal.ZERO
                : value instanceof BigDecimal amount ? amount : new BigDecimal(value.toString());
    }

    private static Date asDate(Object value) {
        if (value instanceof Date date) {
            return date;
        }
        if (value == null) {
            return null;
        }
        return Timestamp.valueOf(value.toString());
    }

    private static MigrationException badRequest(String message) {
        return new MigrationException(HttpStatus.BAD_REQUEST, message);
    }
}
