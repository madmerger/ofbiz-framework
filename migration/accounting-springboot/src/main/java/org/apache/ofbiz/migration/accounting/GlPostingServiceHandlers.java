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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GlPostingServiceHandlers {
    private static final String COMPONENT = "accounting";
    private static final List<String> SERVICES = List.of(
            "postFinAccountTransToGl",
            "createAcctgTransForSalesShipmentIssuance",
            "createAcctgTransForCanceledSalesShipmentIssuance",
            "createAcctgTransForInventoryItemCostChange",
            "createAcctgTransForShipmentReceipt",
            "createAcctgTransForWorkEffortIssuance",
            "createAcctgTransForPhysicalInventoryVariance",
            "createAcctgTransForWorkEffortInventoryProduced",
            "createAcctgTransForWorkEffortCost",
            "createAcctgTransForInventoryItemOwnerChange",
            "createAcctgTransForFixedAssetMaintIssuance",
            "createAcctgTransForCustomerReturnInvoice",
            "createAcctgTransForPurchaseInvoice",
            "createAcctgTransForSalesInvoice",
            "createAcctgTransAndEntriesForIncomingPayment",
            "createAcctgTransAndEntriesForOutgoingPayment",
            "createAcctgTransAndEntriesForPaymentApplication",
            "createAcctgTransAndEntriesForCustomerRefundPaymentApplication",
            "revertAcctgTransOnCancelInvoice",
            "revertAcctgTransOnRemovePaymentApplications");

    private final EntityManager entityManager;
    private final EntityRegistry entities;
    private final ServiceHandlerRegistry registry;
    private final GeneralLedgerServiceHandlers generalLedger;
    private final InventoryAccountingPort inventory;

    public GlPostingServiceHandlers(
            EntityManager entityManager,
            EntityRegistry entities,
            ServiceHandlerRegistry registry,
            GeneralLedgerServiceHandlers generalLedger,
            InventoryAccountingPort inventory) {
        this.entityManager = entityManager;
        this.entities = entities;
        this.registry = registry;
        this.generalLedger = generalLedger;
        this.inventory = inventory;
    }

    @PostConstruct
    void registerHandlers() {
        SERVICES.forEach(name -> registry.registerHandler(COMPONENT, name, "CREATE",
                input -> executeDirect(name, input)));
    }

    public Map<String, Object> executeDirect(String name, Map<String, Object> input) {
        return switch (name) {
            case "postFinAccountTransToGl" -> postFinAccountTransToGl(input);
            case "createAcctgTransForSalesInvoice" -> invoicePosting(input, "SALES_INVOICE");
            case "createAcctgTransForPurchaseInvoice" -> invoicePosting(input, "PURCHASE_INVOICE");
            case "createAcctgTransForCustomerReturnInvoice" -> invoicePosting(input, "CUST_RTN_INVOICE");
            case "createAcctgTransAndEntriesForIncomingPayment" -> paymentPosting(input, true);
            case "createAcctgTransAndEntriesForOutgoingPayment" -> paymentPosting(input, false);
            case "createAcctgTransAndEntriesForPaymentApplication" -> paymentApplicationPosting(input, false);
            case "createAcctgTransAndEntriesForCustomerRefundPaymentApplication" ->
                    paymentApplicationPosting(input, true);
            case "revertAcctgTransOnCancelInvoice" -> revertTransactions(input, false);
            case "revertAcctgTransOnRemovePaymentApplications" -> revertTransactions(input, true);
            default -> inventoryPosting(name, input);
        };
    }

    /** Migrated from applications/accounting/minilang/finaccount/FinAccountGlPostServices.xml:25. */
    private Map<String, Object> postFinAccountTransToGl(Map<String, Object> input) {
        String transId = required(input, "finAccountTransId");
        Object trans = one("FinAccountTrans", Map.of("finAccountTransId", transId));
        Object account = one("FinAccount", Map.of("finAccountId", value(trans, "finAccountId")));
        String organization = text(value(account, "organizationPartyId"));
        String accountType = text(value(account, "finAccountTypeId"));
        Object mapped = accountType == null ? null : first("FinAccountTypeGlAccount",
                Map.of("organizationPartyId", organization, "finAccountTypeId", accountType));
        String debitId = text(value(account, "postToGlAccountId"));
        if (debitId == null && mapped != null) {
            debitId = text(value(mapped, "glAccountId"));
        }
        String creditId = text(input.get("glAccountId"));
        if (debitId == null || creditId == null || organization == null) {
            throw badRequest("FinAccount transaction GL account or organization is missing");
        }
        BigDecimal amount = ledgerAmount(value(trans, "amount"));
        String type = text(value(trans, "finAccountTransTypeId"));
        boolean debit = "DEPOSIT".equals(type) || "ADJUSTMENT".equals(type) && amount.signum() >= 0;
        String transactionType;
        if ("WITHDRAWAL".equals(type)) {
            debit = false;
            transactionType = "PAYMENT_ACCTG_TRANS";
        } else if ("DEPOSIT".equals(type)) {
            transactionType = "RECEIPT";
        } else if ("ADJUSTMENT".equals(type)) {
            transactionType = amount.signum() < 0 ? "OUTGOING_PAYMENT" : "INCOMING_PAYMENT";
        } else if (!Set.of("DEPOSIT", "WITHDRAWAL", "ADJUSTMENT").contains(type)) {
            throw badRequest("Unsupported FinAccount transaction type: " + type);
        } else {
            transactionType = "INCOMING_PAYMENT";
        }
        if (amount.signum() < 0) {
            amount = amount.negate();
            debit = !debit;
        }
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("organizationPartyId", organization);
        request.put("acctgTransTypeId", transactionType);
        request.put("glFiscalTypeId", "ACTUAL");
        request.put("finAccountTransId", transId);
        request.put("transactionDate", Timestamp.from(Instant.now()));
        request.put("partyId", value(trans, "partyId"));
        request.put("isPosted", "N");
        request.put("acctgTransEntryTypeId", "_NA_");
        request.put("debitGlAccountId", debit ? debitId : creditId);
        request.put("creditGlAccountId", debit ? creditId : debitId);
        request.put("amount", amount);
        return generalLedger.quickCreate(request);
    }

    /** Migrated from applications/accounting/minilang/ledger/GeneralLedgerServices.xml:1970. */
    private Map<String, Object> invoicePosting(Map<String, Object> input, String expectedType) {
        String invoiceId = required(input, "invoiceId");
        Object invoice = one("Invoice", Map.of("invoiceId", invoiceId));
        boolean purchase = "PURCHASE_INVOICE".equals(expectedType);
        boolean customerReturn = "CUST_RTN_INVOICE".equals(expectedType);
        boolean matchingType = purchase ? isPurchaseInvoice(value(invoice, "invoiceTypeId"))
                : expectedType.equals(value(invoice, "invoiceTypeId"));
        if (!matchingType) {
            if (customerReturn) {
                throw badRequest("Invoice is not a customer return invoice");
            }
            return Map.of();
        }
        String organization = text(value(invoice,
                customerReturn || purchase ? "partyId" : "partyIdFrom"));
        String currency = text(value(invoice, "currencyUomId"));
        List<Map<String, Object>> entries = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Object item : find("InvoiceItem", Map.of("invoiceId", invoiceId))) {
            String itemType = text(value(item, "invoiceItemTypeId"));
            if (itemType == null || itemType.endsWith("SALES_TAX")) {
                continue;
            }
            BigDecimal quantity = value(item, "quantity") == null ? BigDecimal.ONE : money(value(item, "quantity"));
            BigDecimal amount = quantity.multiply(money(value(item, "amount"))).setScale(4, RoundingMode.HALF_UP);
            if (amount.signum() == 0) {
                continue;
            }
            String itemSide = purchase || customerReturn ? "D" : "C";
            String accountType = itemType;
            Map<String, Object> detail = entry(itemSide, organization, amount, currency, accountType);
            detail.put("productId", value(item, "productId"));
            detail.put("glAccountId", value(item, "overrideGlAccountId"));
            resolveAccount(detail, expectedType, invoiceId);
            entries.add(detail);
            total = total.add(amount);
        }
        if (total.signum() == 0) {
            return Map.of();
        }
        String balanceType = purchase ? "ACCOUNTS_PAYABLE" : "ACCOUNTS_RECEIVABLE";
        String balanceSide = purchase || customerReturn ? "C" : "D";
        String counterparty = purchase || customerReturn
                ? text(value(invoice, "partyIdFrom")) : text(value(invoice, "partyId"));
        Map<String, Object> balance = entry(balanceSide, organization, total, currency, balanceType);
        balance.put("partyId", counterparty);
        balance.put("roleTypeId", purchase ? "BILL_FROM_VENDOR" : "BILL_TO_CUSTOMER");
        resolveAccount(balance, expectedType, invoiceId);
        entries.add(balance);
        return createTransaction(expectedType, invoice, entries, Map.of("invoiceId", invoiceId));
    }

    /** Migrated from applications/accounting/minilang/ledger/GeneralLedgerServices.xml:2189. */
    private Map<String, Object> paymentPosting(Map<String, Object> input, boolean receipt) {
        Object payment = one("Payment", Map.of("paymentId", required(input, "paymentId")));
        Object paymentTypeId = value(payment, "paymentTypeId");
        if (receipt ? !isReceipt(paymentTypeId) : !isDisbursement(paymentTypeId)) {
            return Map.of();
        }
        String organization = text(value(payment, receipt ? "partyIdTo" : "partyIdFrom"));
        String type = text(value(payment, "paymentTypeId"));
        Object typeMap = first("PaymentGlAccountTypeMap",
                Map.of("organizationPartyId", organization, "paymentTypeId", type));
        String mappedType = text(value(typeMap, "glAccountTypeId"));
        BigDecimal amount = money(value(payment, "amount")).setScale(4, RoundingMode.HALF_UP);
        String currency = text(value(payment, "currencyUomId"));
        BigDecimal originalAmount = receipt ? money(value(payment, "actualCurrencyAmount")) : amount;
        String originalCurrency = receipt
                ? text(value(payment, "actualCurrencyUomId")) : currency;
        String partner = text(value(payment, receipt ? "partyIdFrom" : "partyIdTo"));
        String role = receipt ? "BILL_TO_CUSTOMER" : "BILL_FROM_VENDOR";
        Map<String, Object> debit = entry("D", organization, amount, currency, receipt ? null : mappedType);
        debit.put("origAmount", originalAmount);
        debit.put("origCurrencyUomId", originalCurrency);
        debit.put("paymentId", value(payment, "paymentId"));
        resolveAccount(debit, receipt ? "INCOMING_PAYMENT" : "OUTGOING_PAYMENT", null);
        Map<String, Object> credit = entry("C", organization, amount, currency, receipt ? mappedType : null);
        credit.put("origAmount", originalAmount);
        credit.put("origCurrencyUomId", originalCurrency);
        credit.put("paymentId", value(payment, "paymentId"));
        if (receipt) {
            credit.put("glAccountId", value(payment, "overrideGlAccountId"));
        }
        resolveAccount(credit, receipt ? "INCOMING_PAYMENT" : "OUTGOING_PAYMENT", null);
        if (!receipt) {
            debit.put("glAccountId", value(payment, "overrideGlAccountId"));
        }
        Map<String, Object> links = new LinkedHashMap<>();
        links.put("paymentId", value(payment, "paymentId"));
        links.put("partyId", partner);
        links.put("roleTypeId", role);
        Map<String, Object> result = createTransaction(receipt ? "INCOMING_PAYMENT" : "OUTGOING_PAYMENT",
                payment, List.of(debit, credit), links);
        for (Object application : find("PaymentApplication", Map.of("paymentId", value(payment, "paymentId")))) {
            if (!receipt) {
                Object invoiceId = value(application, "invoiceId");
                if (invoiceId == null) {
                    continue;
                }
                Object invoice = first("Invoice", Map.of("invoiceId", invoiceId));
                Object invoiceStatus = value(invoice, "statusId");
                if (invoiceStatus == null || !Set.of("INVOICE_READY", "INVOICE_PAID").contains(invoiceStatus)) {
                    continue;
                }
            }
            boolean refund = "CUSTOMER_REFUND".equals(type);
            paymentApplicationPosting(Map.of(
                    "paymentApplicationId", value(application, "paymentApplicationId")), refund);
        }
        return result;
    }

    /** Migrated from applications/accounting/minilang/ledger/GeneralLedgerServices.xml:2499. */
    private Map<String, Object> paymentApplicationPosting(Map<String, Object> input, boolean refund) {
        Object application = one("PaymentApplication",
                Map.of("paymentApplicationId", required(input, "paymentApplicationId")));
        Object payment = one("Payment", Map.of("paymentId", value(application, "paymentId")));
        if ("PMNT_NOT_PAID".equals(value(payment, "statusId"))
                || refund != "CUSTOMER_REFUND".equals(value(payment, "paymentTypeId"))) {
            return Map.of();
        }
        boolean receipt = isReceipt(value(payment, "paymentTypeId"));
        String organization = text(value(payment, receipt && !refund ? "partyIdTo" : "partyIdFrom"));
        Object mapping = first("PaymentGlAccountTypeMap", Map.of(
                "organizationPartyId", organization, "paymentTypeId", value(payment, "paymentTypeId")));
        String mappedType = text(value(mapping, "glAccountTypeId"));
        if ("ACCOUNTS_RECEIVABLE".equals(mappedType)
                || !refund && "ACCOUNTS_PAYABLE".equals(mappedType)) {
            return Map.of();
        }
        BigDecimal amount = money(value(application, "amountApplied")).setScale(4, RoundingMode.HALF_UP);
        String currency = text(value(payment, "currencyUomId"));
        String partner = text(value(payment, receipt && !refund ? "partyIdFrom" : "partyIdTo"));
        String role = receipt || refund ? "BILL_TO_CUSTOMER" : "BILL_FROM_VENDOR";
        String debitType = refund ? "ACCOUNTS_RECEIVABLE" : receipt ? mappedType : "ACCOUNTS_PAYABLE";
        String creditType = receipt ? "ACCOUNTS_RECEIVABLE" : mappedType;
        Map<String, Object> debit = entry("D", organization, amount, currency, debitType);
        Map<String, Object> credit = entry("C", organization, amount, currency, creditType);
        for (Map<String, Object> row : List.of(debit, credit)) {
            row.put("partyId", partner);
            row.put("roleTypeId", role);
            row.put("paymentId", value(payment, "paymentId"));
            resolveAccount(row, "PAYMENT_APPL", value(application, "invoiceId"));
        }
        putIfPresent(debit, "glAccountId", receipt && !refund
                ? value(payment, "overrideGlAccountId") : value(application, "overrideGlAccountId"));
        putIfPresent(credit, "glAccountId", receipt && !refund ? null : value(payment, "overrideGlAccountId"));
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("paymentId", value(payment, "paymentId"));
        header.put("invoiceId", value(application, "invoiceId"));
        header.put("partyId", partner);
        header.put("roleTypeId", role);
        return createTransaction("PAYMENT_APPL", payment, List.of(debit, credit), header);
    }

    /** Migrated from applications/accounting/minilang/ledger/AcctgTransServices.xml:441,457. */
    private Map<String, Object> revertTransactions(Map<String, Object> input, boolean paymentApplications) {
        List<Object> sourceRows;
        if (paymentApplications) {
            Object application = one("PaymentApplication",
                    Map.of("paymentApplicationId", required(input, "paymentApplicationId")));
            Map<String, Object> keys = new LinkedHashMap<>();
            keys.put("paymentId", value(application, "paymentId"));
            keys.put("invoiceId", value(application, "invoiceId"));
            sourceRows = find("AcctgTrans", keys);
        } else {
            sourceRows = find("AcctgTrans", Map.of("invoiceId", required(input, "invoiceId"))).stream()
                    .filter(row -> value(row, "paymentId") == null).toList();
        }
        Map<String, Object> result = new LinkedHashMap<>();
        for (Object source : sourceRows) {
            Map<String, Object> copy = generalLedger.copyTransaction(Map.of(
                    "fromAcctgTransId", value(source, "acctgTransId"), "revert", "Y"));
            if (copy.get("acctgTransId") != null) {
                result.put("acctgTransId", copy.get("acctgTransId"));
            }
        }
        return result;
    }

    private Map<String, Object> inventoryPosting(String name, Map<String, Object> input) {
        if (name.equals("createAcctgTransForWorkEffortIssuance")) {
            return workEffortIssuance(input);
        }
        if (name.equals("createAcctgTransForSalesShipmentIssuance")
                || name.equals("createAcctgTransForCanceledSalesShipmentIssuance")
                || name.equals("createAcctgTransForFixedAssetMaintIssuance")) {
            Object issuance = one("ItemIssuance", Map.of("itemIssuanceId", required(input, "itemIssuanceId")));
            Map<String, Object> item = inventory.findInventoryItem(text(value(issuance, "inventoryItemId")));
            if (item == null) {
                throw notFound("InventoryItem not found");
            }
            String organization = text(item.get("ownerPartyId"));
            Map<String, Object> preference = partyPreferences(organization);
            if (name.equals("createAcctgTransForSalesShipmentIssuance")
                    && "N".equals(preference.get("enableAccounting"))) {
                return Map.of();
            }
            BigDecimal quantity = name.equals("createAcctgTransForCanceledSalesShipmentIssuance")
                    ? money(input.get("canceledQuantity")) : money(value(issuance, "quantity"));
            BigDecimal unitCost = name.equals("createAcctgTransForCanceledSalesShipmentIssuance")
                    || name.equals("createAcctgTransForSalesShipmentIssuance")
                    && "COGS_AVG_COST".equals(preference.get("cogsMethodId"))
                    ? inventory.averageCost(item) : money(item.get("unitCost"));
            BigDecimal amount = quantity.multiply(unitCost).setScale(4, RoundingMode.HALF_UP);
            String debitType = "COGS_ACCOUNT";
            String creditType = "INVENTORY_ACCOUNT";
            String transactionType = "SALES_SHIPMENT";
            if (name.contains("Canceled")) {
                debitType = "INVENTORY_ACCOUNT";
                creditType = "COGS_ACCOUNT";
            } else if (name.contains("FixedAsset")) {
                debitType = "FIXED_ASSET_MAINT";
                creditType = "INVENTORY_ACCOUNT";
                transactionType = "INVENTORY";
            } else if (name.contains("WorkEffort")) {
                debitType = "WIP_INVENTORY";
                creditType = "RAWMAT_INVENTORY";
                transactionType = "INVENTORY";
            }
            Map<String, Object> debit = entry("D", organization, amount,
                    text(item.get("currencyUomId")), debitType);
            Map<String, Object> credit = entry("C", organization, amount,
                    text(item.get("currencyUomId")), creditType);
            Object billToCustomer = value(issuance, "orderId") == null ? null
                    : first("OrderRole", Map.of(
                            "orderId", value(issuance, "orderId"), "roleTypeId", "BILL_TO_CUSTOMER"));
            for (Map<String, Object> row : List.of(debit, credit)) {
                row.put("inventoryItemId", item.get("inventoryItemId"));
                row.put("productId", item.get("productId"));
                if (billToCustomer != null) {
                    row.put("partyId", value(billToCustomer, "partyId"));
                    row.put("roleTypeId", value(billToCustomer, "roleTypeId"));
                }
            }
            resolveAccount(debit, transactionType, null);
            resolveAccount(credit, transactionType, null);
            Map<String, Object> links = new LinkedHashMap<>();
            putIfPresent(links, "shipmentId", value(issuance, "shipmentId"));
            putIfPresent(links, "fixedAssetId", value(issuance, "fixedAssetId"));
            links.put("inventoryItemId", item.get("inventoryItemId"));
            return createTransaction(transactionType, issuance, List.of(debit, credit), links);
        }
        if (name.equals("createAcctgTransForInventoryItemCostChange")) {
            Object detail = one("InventoryItemDetail", Map.of(
                    "inventoryItemId", required(input, "inventoryItemId"),
                    "inventoryItemDetailSeqId", required(input, "inventoryItemDetailSeqId")));
            Map<String, Object> item = inventory.findInventoryItem(required(input, "inventoryItemId"));
            Object previousDetail = find("InventoryItemDetail",
                    Map.of("inventoryItemId", required(input, "inventoryItemId"))).stream()
                    .filter(candidate -> !required(input, "inventoryItemDetailSeqId")
                            .equals(value(candidate, "inventoryItemDetailSeqId")))
                    .filter(candidate -> value(candidate, "unitCost") != null)
                    .max(java.util.Comparator.comparing(candidate -> {
                        Object effectiveDate = value(candidate, "effectiveDate");
                        return effectiveDate instanceof java.util.Date date ? date : new java.util.Date(0);
                    })).orElse(null);
            if (previousDetail == null) {
                return Map.of();
            }
            BigDecimal amount = money(value(previousDetail, "unitCost"))
                    .subtract(money(value(detail, "unitCost")))
                    .multiply(money(item.get("quantityOnHandTotal")))
                    .setScale(4, RoundingMode.HALF_UP);
            if (amount.signum() == 0) {
                return Map.of();
            }
            List<Map<String, Object>> entries = List.of(
                    entry("C", text(item.get("ownerPartyId")), amount, text(item.get("currencyUomId")), "INVENTORY_ACCOUNT"),
                    entry("D", text(item.get("ownerPartyId")), amount, text(item.get("currencyUomId")), "INV_ADJ_VAL"));
            entries.forEach(row -> {
                row.put("inventoryItemId", input.get("inventoryItemId"));
                row.put("productId", item.get("productId"));
                resolveAccount(row, "INVENTORY", null);
            });
            return createTransaction("INVENTORY", detail, entries, Map.of("inventoryItemId", input.get("inventoryItemId")));
        }
        if (name.equals("createAcctgTransForInventoryItemOwnerChange")) {
            Map<String, Object> item = inventory.findInventoryItem(required(input, "inventoryItemId"));
            String oldOwner = required(input, "oldOwnerPartyId");
            String newOwner = text(item.get("ownerPartyId"));
            BigDecimal amount = money(item.get("unitCost"))
                    .multiply(money(item.get("quantityOnHandTotal"))).setScale(4, RoundingMode.HALF_UP);
            List<Map<String, Object>> entries = List.of(
                    entry("C", oldOwner, amount, text(item.get("currencyUomId")), "INVENTORY_XFER_IN"),
                    entry("D", oldOwner, amount, text(item.get("currencyUomId")), "INVENTORY_ACCOUNT"),
                    entry("C", newOwner, amount, text(item.get("currencyUomId")), "INVENTORY_XFER_IN"),
                    entry("D", newOwner, amount, text(item.get("currencyUomId")), "INVENTORY_ACCOUNT"));
            entries.forEach(row -> {
                row.put("productId", item.get("productId"));
                resolveAccount(row, "INVENTORY", null);
            });
            return createTransaction("INVENTORY", item, entries, Map.of("inventoryItemId", input.get("inventoryItemId")));
        }
        if (name.equals("createAcctgTransForWorkEffortCost")) {
            Object component = one("CostComponent", Map.of(
                    "workEffortId", required(input, "workEffortId"),
                    "costComponentId", required(input, "costComponentId")));
            Object calculation = value(component, "costComponentCalcId") == null ? null
                    : first("CostComponentCalc",
                            Map.of("costComponentCalcId", value(component, "costComponentCalcId")));
            Object workEffort = one("WorkEffort", Map.of("workEffortId", required(input, "workEffortId")));
            Object facility = value(workEffort, "facilityId") == null ? null
                    : one("Facility", Map.of("facilityId", value(workEffort, "facilityId")));
            String organization = text(value(facility, "ownerPartyId"));
            Object goodStandard = latestWorkEffortGoodStandard(workEffort);
            String creditType = text(value(calculation, "costGlAccountTypeId"));
            if (creditType == null && value(component, "fixedAssetId") != null) {
                creditType = "OPERATING_EXPENSE";
            }
            String debitType = text(value(calculation, "offsettingGlAccountTypeId"));
            if (debitType == null) {
                debitType = "WIP_INVENTORY";
            }
            BigDecimal amount = ledgerAmount(value(component, "cost"));
            Map<String, Object> credit = entry("C", organization, amount,
                    text(value(component, "costUomId")), creditType);
            Map<String, Object> debit = entry("D", organization, amount,
                    text(value(component, "costUomId")), debitType);
            if (goodStandard != null) {
                credit.put("productId", value(goodStandard, "productId"));
                debit.put("productId", value(goodStandard, "productId"));
            }
            resolveAccount(credit, "MANUFACTURING", null);
            resolveAccount(debit, "MANUFACTURING", null);
            return createTransaction("MANUFACTURING", component, List.of(debit, credit),
                    Map.of("workEffortId", input.get("workEffortId")));
        }
        return inventoryDocumentPosting(name, input);
    }

    private Map<String, Object> workEffortIssuance(Map<String, Object> input) {
        String workEffortId = required(input, "workEffortId");
        String inventoryItemId = required(input, "inventoryItemId");
        Object assignment = one("WorkEffortInventoryAssign", Map.of(
                "workEffortId", workEffortId, "inventoryItemId", inventoryItemId));
        Object workEffort = one("WorkEffort", Map.of("workEffortId", workEffortId));
        Map<String, Object> item = inventory.findInventoryItem(inventoryItemId);
        if (item == null) {
            throw notFound("InventoryItem not found");
        }
        String organization = text(item.get("ownerPartyId"));
        if ("N".equals(partyPreferences(organization).get("enableAccounting"))) {
            return Map.of();
        }
        BigDecimal amount = ledgerAmount(money(value(assignment, "quantity"))
                .multiply(money(item.get("unitCost"))));
        Map<String, Object> debit = entry("D", organization, amount,
                text(item.get("currencyUomId")), "WIP_INVENTORY");
        Map<String, Object> credit = entry("C", organization, amount,
                text(item.get("currencyUomId")), "RAWMAT_INVENTORY");
        debit.put("productId", productForWorkEffort(workEffort));
        credit.put("productId", item.get("productId"));
        BigDecimal quantityToDeduct = input.get("quantity") == null
                ? money(value(assignment, "quantity")) : money(input.get("quantity"));
        inventory.createInventoryItemDetail(Map.of(
                "inventoryItemId", inventoryItemId,
                "workEffortId", workEffortId,
                "accountingQuantityDiff", quantityToDeduct.negate()));
        resolveAccount(debit, "INVENTORY", null);
        resolveAccount(credit, "INVENTORY", null);
        return createTransaction("INVENTORY", workEffort, List.of(debit, credit),
                Map.of("workEffortId", workEffortId));
    }

    private Object latestWorkEffortGoodStandard(Object workEffort) {
        String workEffortType = text(value(workEffort, "workEffortTypeId"));
        Object workEffortId = "PROD_ORDER_TASK".equals(workEffortType)
                ? value(workEffort, "workEffortParentId")
                : "PROD_ORDER_HEADER".equals(workEffortType) ? value(workEffort, "workEffortId") : null;
        if (workEffortId == null) {
            return null;
        }
        return find("WorkEffortGoodStandard", Map.of(
                "workEffortId", workEffortId, "workEffortGoodStdTypeId", "PRUN_PROD_DELIV")).stream()
                .max(java.util.Comparator.comparing(standard -> {
                    Object fromDate = value(standard, "fromDate");
                    return fromDate instanceof java.util.Date date ? date : new java.util.Date(0);
                })).orElse(null);
    }

    private boolean isPurchaseInvoice(Object invoiceTypeId) {
        Set<String> visited = new java.util.HashSet<>();
        String current = text(invoiceTypeId);
        while (current != null && visited.add(current)) {
            if ("PURCHASE_INVOICE".equals(current)) {
                return true;
            }
            Object invoiceType = first("InvoiceType", Map.of("invoiceTypeId", current));
            current = text(value(invoiceType, "parentTypeId"));
        }
        return false;
    }

    private Object productForWorkEffort(Object workEffort) {
        Object standard = latestWorkEffortGoodStandard(workEffort);
        return value(standard, "productId");
    }

    private Map<String, Object> inventoryDocumentPosting(String name, Map<String, Object> input) {
        if (name.equals("createAcctgTransForPhysicalInventoryVariance")) {
            String physicalInventoryId = required(input, "physicalInventoryId");
            List<Object> details = find("InventoryItemDetail", Map.of("physicalInventoryId", physicalInventoryId));
            List<Map<String, Object>> entries = new ArrayList<>();
            for (Object detail : details) {
                Map<String, Object> item = inventory.findInventoryItem(text(value(detail, "inventoryItemId")));
                if (item == null) {
                    continue;
                }
                String organization = text(item.get("ownerPartyId"));
                BigDecimal amount = ledgerAmount(money(value(detail, "quantityOnHandDiff"))
                        .multiply(money(item.get("unitCost"))));
                Map<String, Object> credit = entry("C", organization, amount,
                        text(item.get("currencyUomId")), text(value(detail, "reasonEnumId")));
                Map<String, Object> debit = entry("D", organization, amount,
                        text(item.get("currencyUomId")), "INVENTORY_ACCOUNT");
                credit.put("productId", item.get("productId"));
                debit.put("productId", item.get("productId"));
                credit.put("inventoryItemId", item.get("inventoryItemId"));
                debit.put("inventoryItemId", item.get("inventoryItemId"));
                resolveAccount(credit, "ITEM_VARIANCE", null);
                resolveAccount(debit, "ITEM_VARIANCE", null);
                entries.add(credit);
                entries.add(debit);
            }
            return createTransaction("ITEM_VARIANCE", details.isEmpty() ? Map.of() : details.get(0),
                    entries, Map.of("physicalInventoryId", physicalInventoryId));
        }
        if (name.equals("createAcctgTransForWorkEffortInventoryProduced")) {
            String workEffortId = required(input, "workEffortId");
            String inventoryItemId = required(input, "inventoryItemId");
            Object produced = one("WorkEffortInventoryProduced", Map.of(
                    "workEffortId", workEffortId, "inventoryItemId", inventoryItemId));
            Map<String, Object> item = inventory.findInventoryItem(inventoryItemId);
            if (item == null) {
                throw notFound("InventoryItem not found");
            }
            BigDecimal amount = ledgerAmount(money(item.get("quantityOnHandTotal"))
                    .multiply(money(item.get("unitCost"))));
            inventory.createInventoryItemDetail(Map.of(
                    "inventoryItemId", inventoryItemId,
                    "workEffortId", workEffortId,
                    "accountingQuantityDiff", money(item.get("quantityOnHandTotal"))));
            String organization = text(item.get("ownerPartyId"));
            Map<String, Object> debit = entry("D", organization, amount,
                    text(item.get("currencyUomId")), "INVENTORY_ACCOUNT");
            Map<String, Object> credit = entry("C", organization, amount,
                    text(item.get("currencyUomId")), "WIP_INVENTORY");
            debit.put("productId", item.get("productId"));
            credit.put("productId", item.get("productId"));
            resolveAccount(debit, "INVENTORY", null);
            resolveAccount(credit, "INVENTORY", null);
            return createTransaction("INVENTORY", produced, List.of(credit, debit),
                    Map.of("workEffortId", workEffortId));
        }
        if (name.equals("createAcctgTransForShipmentReceipt")) {
            String receiptId = required(input, "receiptId");
            Object receipt = one("ShipmentReceipt", Map.of("receiptId", receiptId));
            if (money(value(receipt, "quantityAccepted")).signum() <= 0) {
                return Map.of();
            }
            Map<String, Object> item = inventory.findInventoryItem(text(value(receipt, "inventoryItemId")));
            if (item == null) {
                throw notFound("InventoryItem not found");
            }
            String organization = text(item.get("ownerPartyId"));
            Map<String, Object> preference = partyPreferences(organization);
            if ("N".equals(preference.get("enableAccounting"))) {
                return Map.of();
            }
            Object shipment = one("Shipment", Map.of("shipmentId", value(receipt, "shipmentId")));
            boolean returned = value(receipt, "returnId") != null;
            String creditType = returned ? "COGS_ACCOUNT" : "UNINVOICED_SHIP_RCPT";
            BigDecimal unitCost = money(item.get("unitCost"));
            if (returned && "COGS_AVG_COST".equals(preference.get("cogsMethodId"))) {
                unitCost = inventory.averageCost(item);
            }
            BigDecimal amount = ledgerAmount(money(value(receipt, "quantityAccepted")).multiply(unitCost));
            inventory.createInventoryItemDetail(Map.of(
                    "inventoryItemId", item.get("inventoryItemId"),
                    "receiptId", receiptId,
                    "accountingQuantityDiff", value(receipt, "quantityAccepted")));
            String partyId = text(value(shipment, "partyIdFrom"));
            Map<String, Object> debit = entry("D", organization, amount,
                    text(item.get("currencyUomId")), "INVENTORY_ACCOUNT");
            Map<String, Object> credit = entry("C", organization, amount,
                    text(item.get("currencyUomId")), creditType);
            for (Map<String, Object> row : List.of(debit, credit)) {
                row.put("partyId", partyId);
                row.put("roleTypeId", "BILL_FROM_VENDOR");
                row.put("productId", item.get("productId"));
                row.put("inventoryItemId", item.get("inventoryItemId"));
                resolveAccount(row, "SHIPMENT_RECEIPT", null);
            }
            Map<String, Object> links = new LinkedHashMap<>();
            putIfPresent(links, "shipmentId", value(receipt, "shipmentId"));
            putIfPresent(links, "partyId", partyId);
            return createTransaction("SHIPMENT_RECEIPT", receipt, List.of(credit, debit), links);
        }
        throw badRequest("Unsupported inventory posting service: " + name);
    }

    private Map<String, Object> createTransaction(
            String type, Object source, List<Map<String, Object>> entries, Map<String, Object> links) {
        if (entries.isEmpty()) {
            return Map.of();
        }
        Map<String, Object> input = new LinkedHashMap<>(links);
        Map<String, Object> sourceValues = entities.toMap(source);
        input.put("acctgTransTypeId", type);
        input.put("glFiscalTypeId", "ACTUAL");
        input.put("transactionDate", firstNonNull(sourceValues.get("invoiceDate"),
                sourceValues.get("effectiveDate"), sourceValues.get("transactionDate"),
                sourceValues.get("issuedDateTime"), Timestamp.from(Instant.now())));
        input.put("acctgTransEntries", entries);
        return generalLedger.createTransactionAndEntries(input);
    }

    private void resolveAccount(Map<String, Object> entry, String transactionType, Object invoiceId) {
        if (entry.get("glAccountId") != null || entry.get("glAccountTypeId") == null) {
            return;
        }
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("organizationPartyId", entry.get("organizationPartyId"));
        request.put("glAccountTypeId", entry.get("glAccountTypeId"));
        request.put("acctgTransTypeId", transactionType);
        request.put("debitCreditFlag", entry.get("debitCreditFlag"));
        request.put("productId", entry.get("productId"));
        request.put("partyId", entry.get("partyId"));
        request.put("roleTypeId", entry.get("roleTypeId"));
        request.put("paymentId", entry.get("paymentId"));
        if (invoiceId != null) {
            request.put("invoiceId", invoiceId);
        }
        Object accountId = generalLedger.getGlAccountFromType(request).get("glAccountId");
        if (accountId != null) {
            entry.put("glAccountId", accountId);
        }
    }

    private Map<String, Object> entry(String flag, String organization, BigDecimal amount, String currency, String type) {
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("debitCreditFlag", flag);
        entry.put("organizationPartyId", organization);
        entry.put("origAmount", amount);
        entry.put("origCurrencyUomId", currency);
        if (type != null) {
            entry.put("glAccountTypeId", type);
        }
        return entry;
    }

    private Map<String, Object> partyPreferences(String partyId) {
        Set<String> visited = new java.util.HashSet<>();
        String current = partyId;
        while (current != null && visited.add(current)) {
            Object preference = first("PartyAcctgPreference", Map.of("partyId", current));
            if (preference != null) {
                return entities.toMap(preference);
            }
            Object parent = find("PartyRelationship", Map.of(
                    "partyIdTo", current, "partyRelationshipTypeId", "GROUP_ROLLUP")).stream()
                    .filter(relationship -> "INTERNAL_ORGANIZATIO".equals(value(relationship, "roleTypeIdFrom")))
                    .map(relationship -> value(relationship, "partyIdFrom"))
                    .findFirst().orElse(null);
            current = text(parent);
        }
        return Map.of();
    }

    private boolean isReceipt(Object paymentTypeId) {
        return isPaymentTypeInHierarchy(paymentTypeId, "RECEIPT");
    }

    private boolean isDisbursement(Object paymentTypeId) {
        return isPaymentTypeInHierarchy(paymentTypeId, "DISBURSEMENT");
    }

    private boolean isPaymentTypeInHierarchy(Object paymentTypeId, String parentTypeId) {
        Set<String> visited = new java.util.HashSet<>();
        String current = text(paymentTypeId);
        while (current != null && visited.add(current)) {
            if (parentTypeId.equals(current)) {
                return true;
            }
            Object paymentType = first("PaymentType", Map.of("paymentTypeId", current));
            if (paymentType == null) {
                return false;
            }
            current = text(value(paymentType, "parentTypeId"));
        }
        return false;
    }

    private ArithmeticSettings getGlArithmeticSettingsInline() {
        return new ArithmeticSettings(4, RoundingMode.HALF_UP);
    }

    private BigDecimal ledgerAmount(Object value) {
        ArithmeticSettings settings = getGlArithmeticSettingsInline();
        return money(value).setScale(settings.scale(), settings.roundingMode());
    }

    private List<Object> find(String entityName, Map<String, ?> conditions) {
        return new ArrayList<>(AccountingEntityQueries.findByEquality(
                entityManager, entities.entityClass(entityName), conditions));
    }

    private Object first(String entityName, Map<String, ?> conditions) {
        List<Object> rows = find(entityName, conditions);
        return rows.isEmpty() ? null : rows.get(0);
    }

    private Object one(String entityName, Map<String, ?> conditions) {
        Object row = first(entityName, conditions);
        if (row == null) {
            throw notFound(entityName + " not found");
        }
        return row;
    }

    private Object value(Object entity, String field) {
        return entity == null ? null : entities.get(entity, field);
    }

    private static Object firstNonNull(Object... values) {
        for (Object value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private static void putIfPresent(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }

    private static BigDecimal money(Object value) {
        return value == null ? BigDecimal.ZERO : value instanceof BigDecimal amount
                ? amount : new BigDecimal(value.toString());
    }

    private static String required(Map<String, Object> input, String key) {
        String value = text(input.get(key));
        if (value == null) {
            throw badRequest("Missing " + key);
        }
        return value;
    }

    private static String text(Object value) {
        return value == null ? null : value.toString();
    }

    private static MigrationException badRequest(String message) {
        return new MigrationException(HttpStatus.BAD_REQUEST, message);
    }

    private static MigrationException notFound(String message) {
        return new MigrationException(HttpStatus.NOT_FOUND, message);
    }

    private record ArithmeticSettings(int scale, RoundingMode roundingMode) { }
}
