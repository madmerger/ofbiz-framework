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

import jakarta.persistence.EntityManager;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.SequenceService;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountingServiceHandlers {
    private static final String COMPONENT = "accounting";
    private final EntityManager entityManager;
    private final EntityRegistry entities;
    private final SequenceService sequences;
    private final ServiceHandlerRegistry registry;
    private final OrderPort orders;
    private final ProductPort products;
    private final CommitGlPostingEca glPostingEca;
    private final ObjectProvider<MigrationServiceDispatcher> dispatcher;
    @Value("${migration.accounting.payment.salesorder-autocreate:Y}") private String salesPaymentAutoCreate = "Y";
    @Value("${migration.accounting.payment.purchaseorder-autocreate:Y}") private String purchasePaymentAutoCreate = "Y";

    public AccountingServiceHandlers(
            EntityManager entityManager,
            EntityRegistry entities,
            SequenceService sequences,
            ServiceHandlerRegistry registry,
            OrderPort orders,
            ProductPort products,
            CommitGlPostingEca glPostingEca, ObjectProvider<MigrationServiceDispatcher> dispatcher) {
        this.entityManager = entityManager;
        this.entities = entities;
        this.sequences = sequences;
        this.registry = registry;
        this.orders = orders;
        this.products = products;
        this.glPostingEca = glPostingEca;
        this.dispatcher = dispatcher;
    }

    @PostConstruct
    void registerHandlers() {
        registry.registerHandler(COMPONENT, "createInvoice", "CREATE", this::createInvoice);
        registry.registerHandler(COMPONENT, "setInvoiceStatus", "UPDATE", this::setInvoiceStatus);
        registry.registerHandler(COMPONENT, "createInvoiceItem", "CREATE", this::createInvoiceItem);
        registry.registerHandler(COMPONENT, "createInvoiceForOrder", "CREATE", this::createInvoiceForOrder);
        registry.registerHandler(COMPONENT, "checkInvoicePaymentApplications", "UPDATE",
                this::checkInvoicePaymentApplications);
        registry.registerHandler(COMPONENT, "getInvoiceTotal", "VIEW", this::getInvoiceTotal);
        registry.registerHandler(COMPONENT, "getInvoiceItemTotal", "VIEW", this::getInvoiceItemTotal);
        registry.registerHandler(COMPONENT, "getInvoiceNotApplied", "VIEW", this::getInvoiceNotApplied);
        registry.registerHandler(COMPONENT, "getInvoiceApplied", "VIEW", this::getInvoiceApplied);
        registry.registerHandler(COMPONENT, "createPayment", "CREATE", this::createPayment);
        registry.registerHandler(COMPONENT, "updatePayment", "UPDATE", this::updatePayment);
        registry.registerHandler(COMPONENT, "createPaymentFromOrder", "OFBTOOLS_VIEW", this::createPaymentFromOrder);
        registry.registerHandler(COMPONENT, "createMatchingPaymentApplication", "OFBTOOLS_VIEW", this::createMatchingPaymentApplication);
        registry.registerHandler(COMPONENT, "removePaymentApplication", "UPDATE", this::removePaymentApplication);
        registry.registerHandler(COMPONENT, "createPaymentApplication", "CREATE", this::createPaymentApplication);
        registry.registerHandler(COMPONENT, "setPaymentStatus", "UPDATE", this::setPaymentStatus);
    }

    /**
     * Migrated from applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy:137.
     */
    private Map<String, Object> createInvoice(Map<String, Object> input) {
        String invoiceId = text(input.get("invoiceId"));
        if (invoiceId == null) {
            invoiceId = sequences.nextId("Invoice");
        }
        if (findOne("Invoice", Map.of("invoiceId", invoiceId)) != null) {
            throw new MigrationException(HttpStatus.CONFLICT, "ServiceValueFound");
        }
        Object invoice = newEntity("Invoice");
        assign(invoice, input);
        set(invoice, "invoiceId", invoiceId);
        setDefault(invoice, "statusId", "INVOICE_IN_PROCESS");
        setDefault(invoice, "invoiceDate", now());
        if (value(invoice, "invoiceTypeId") == null) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Missing invoiceTypeId");
        }
        entityManager.persist(invoice);
        addInvoiceStatus(invoice, text(value(invoice, "statusId")), null);
        return Map.of("invoiceId", invoiceId);
    }

    private Map<String, Object> createPaymentFromOrder(Map<String, Object> input) {
        String orderId = required(input, "orderId");
        Map<String, Object> order = orders.findOrder(orderId);
        if (order == null) return Map.of();
        boolean purchase = "PURCHASE_ORDER".equals(order.get("orderTypeId"));
        if (!"Y".equals(purchase ? purchasePaymentAutoCreate : salesPaymentAutoCreate)) return Map.of();
        for (Object preference : find("OrderPaymentPreference", Map.of("orderId", orderId))) {
            if (!"PAYMENT_CANCELLED".equals(value(preference, "statusId"))
                    && !find("Payment", Map.of("paymentPreferenceId", value(preference, "orderPaymentPreferenceId"))).isEmpty()) return Map.of();
        }
        List<Map<String, Object>> roles = orders.findOrderRoles(orderId);
        String from = roleParty(roles, "BILL_TO_CUSTOMER");
        String to = roleParty(roles, "BILL_FROM_VENDOR");
        require(from != null && to != null, "Order billing parties are required");
        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("paymentTypeId", purchase ? "VENDOR_PAYMENT" : "CUSTOMER_PAYMENT");
        parameters.put("partyIdFrom", from);
        parameters.put("partyIdTo", to);
        parameters.put("paymentMethodTypeId", "COMPANY_ACCOUNT");
        parameters.put("statusId", "PMNT_NOT_PAID");
        parameters.put("amount", order.get("grandTotal"));
        parameters.put("currencyUomId", order.get("currencyUom"));
        parameters.put("effectiveDate", input.getOrDefault("effectiveDate", now()));
        Object accountingPreference = findOne("PartyAcctgPreference", Map.of("partyId", purchase ? from : to));
        if (accountingPreference != null && java.util.Objects.equals(value(accountingPreference, "baseCurrencyUomId"), order.get("currencyUom"))) {
            parameters.put("actualCurrencyAmount", order.get("grandTotal"));
            parameters.put("actualCurrencyUomId", order.get("currencyUom"));
        }
        Map<String, Object> payment = dispatcher.getObject().execute("accounting", "createPayment", parameters);
        parameters.put("orderId", orderId);
        parameters.put("maxAmount", order.get("grandTotal"));
        Map<String, Object> preference = dispatcher.getObject().execute("order", "createOrderPaymentPreference", parameters);
        parameters.put("paymentId", payment.get("paymentId"));
        parameters.put("paymentPreferenceId", preference.get("orderPaymentPreferenceId"));
        dispatcher.getObject().execute("accounting", "updatePayment", parameters);
        return Map.of("paymentId", payment.get("paymentId"));
    }

    private Map<String, Object> updatePayment(Map<String, Object> input) {
        Object payment = requireEntity("Payment", "paymentId", input);
        if (!"PMNT_NOT_PAID".equals(value(payment, "statusId"))) {
            throw new MigrationException(HttpStatus.NOT_IMPLEMENTED, "Only updates to unpaid payments are ported");
        }
        String status = text(value(payment, "statusId"));
        Map<String, Object> fields = new LinkedHashMap<>();
        for (String field : entities.toMap(payment).keySet()) {
            if (input.containsKey(field)) fields.put(field, input.get(field));
        }
        assign(payment, fields);
        set(payment, "statusId", status);
        setDefault(payment, "effectiveDate", now());
        if (input.get("statusId") != null && !input.get("statusId").equals(status)) setPaymentStatus(input);
        return Map.of();
    }

    /**
     * Migrated from applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy:234.
     */
    private Map<String, Object> setInvoiceStatus(Map<String, Object> input) {
        Object invoice = requireEntity("Invoice", "invoiceId", input);
        String oldStatusId = text(value(invoice, "statusId"));
        String newStatusId = required(input, "statusId");
        if (oldStatusId.equals(newStatusId)) {
            return Map.of("oldStatusId", oldStatusId, "invoiceTypeId", text(value(invoice, "invoiceTypeId")));
        }
        requireValidChange(oldStatusId, newStatusId);
        if ("INVOICE_PAID".equals(newStatusId)) {
            require(money(invoiceNotApplied(invoice)).signum() == 0,
                    "Invoice cannot change status to paid while an amount remains outstanding");
            set(invoice, "paidDate", input.getOrDefault("paidDate", now()));
        } else if ("INVOICE_READY".equals(newStatusId) && value(invoice, "paidDate") != null) {
            set(invoice, "paidDate", null);
        }
        set(invoice, "statusId", newStatusId);
        addInvoiceStatus(invoice, newStatusId, input.get("statusDate"));
        if ("INVOICE_READY".equals(newStatusId) && !"INVOICE_PAID".equals(oldStatusId)
                && invoiceTotal(invoice).subtract(invoiceApplied(text(value(invoice, "invoiceId")))).signum() == 0) {
            requireValidChange(newStatusId, "INVOICE_PAID");
            set(invoice, "statusId", "INVOICE_PAID");
            set(invoice, "paidDate", now());
            addInvoiceStatus(invoice, "INVOICE_PAID", null);
        }
        if ("INVOICE_READY".equals(newStatusId)) {
            glPostingEca.invoiceReady(text(value(invoice, "invoiceId")),
                    text(value(invoice, "invoiceTypeId")), oldStatusId);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("oldStatusId", oldStatusId);
        result.put("invoiceTypeId", value(invoice, "invoiceTypeId"));
        return result;
    }

    /**
     * Migrated from applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy:349.
     */
    private Map<String, Object> createInvoiceItem(Map<String, Object> input) {
        Object invoice = requireEntity("Invoice", "invoiceId", input);
        Object item = newEntity("InvoiceItem");
        assign(item, input);
        String invoiceId = required(input, "invoiceId");
        set(item, "invoiceId", invoiceId);
        String seqId = text(input.get("invoiceItemSeqId"));
        if (seqId == null) {
            seqId = sequences.nextSubSequence("InvoiceItem", input, "invoiceItemSeqId");
        }
        set(item, "invoiceItemSeqId", seqId);
        if (input.get("productId") != null && value(item, "quantity") == null) {
            set(item, "quantity", BigDecimal.ONE);
        }
        if (value(item, "amount") == null) {
            throw badRequest("Invoice amount is mandatory");
        }
        if (value(item, "description") == null && input.get("productId") != null) {
            Map<String, Object> product = products.findProduct(text(input.get("productId")));
            if (product != null) {
                setIfWritable(item, "description", product.get("description"));
            }
        }
        entityManager.persist(item);
        return Map.of("invoiceId", invoiceId, "invoiceItemSeqId", seqId);
    }

    /**
     * Migrated from applications/accounting/src/main/java/org/apache/ofbiz/accounting/invoice/InvoiceServices.java:153.
     */
    private Map<String, Object> createInvoiceForOrder(Map<String, Object> input) {
        String orderId = required(input, "orderId");
        Map<String, Object> order = orders.findOrder(orderId);
        if (order == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "No order header for " + orderId);
        }
        if (!"SALES_ORDER".equals(order.get("orderTypeId"))) {
            throw badRequest("Only SALES_ORDER invoices are supported");
        }
        List<Map<String, Object>> billable = new ArrayList<>();
        for (Map<String, Object> item : orders.findOrderItems(orderId)) {
            BigDecimal quantity = money(item.get("quantity"));
            BigDecimal remaining = quantity.subtract(orders.billedQuantity(orderId, text(item.get("orderItemSeqId"))));
            if (remaining.signum() > 0) {
                if (input.get("billItems") instanceof List<?> issuedItems) {
                    for (Object raw : issuedItems) {
                        if (!(raw instanceof Map<?, ?> issue)) throw badRequest("billItems must contain objects");
                        if (!java.util.Objects.equals(item.get("orderItemSeqId"), issue.get("orderItemSeqId"))) continue;
                        BigDecimal issuedQuantity = money(issue.get("quantity")).subtract(money(issue.get("cancelQuantity")));
                        BigDecimal quantityToBill = issuedQuantity.min(remaining);
                        if (quantityToBill.signum() <= 0) continue;
                        Map<String, Object> row = new LinkedHashMap<>(item);
                        row.put("remainingQuantity", quantityToBill);
                        row.put("itemIssuanceId", issue.get("itemIssuanceId"));
                        row.put("inventoryItemId", issue.get("inventoryItemId"));
                        billable.add(row);
                        remaining = remaining.subtract(quantityToBill);
                    }
                } else {
                    Map<String, Object> row = new LinkedHashMap<>(item);
                    row.put("remainingQuantity", remaining);
                    billable.add(row);
                }
            }
        }
        if (billable.isEmpty()) {
            return Map.of("message", "No order items to invoice");
        }
        List<Map<String, Object>> roles = orders.findOrderRoles(orderId);
        String customerPartyId = roleParty(roles, "BILL_TO_CUSTOMER");
        String vendorPartyId = roleParty(roles, "BILL_FROM_VENDOR");
        if (customerPartyId == null) {
            customerPartyId = text(order.get("partyId"));
        }
        if (vendorPartyId == null) {
            vendorPartyId = orders.productStorePartyId(text(order.get("productStoreId")));
        }
        require(customerPartyId != null && vendorPartyId != null,
                "Unable to resolve bill-to and bill-from parties for order " + orderId);
        Map<String, Object> invoiceInput = new LinkedHashMap<>();
        invoiceInput.put("partyId", customerPartyId);
        invoiceInput.put("partyIdFrom", vendorPartyId);
        invoiceInput.put("invoiceTypeId", "SALES_INVOICE");
        invoiceInput.put("statusId", "INVOICE_IN_PROCESS");
        invoiceInput.put("currencyUomId", order.get("currencyUom"));
        invoiceInput.put("invoiceDate", input.getOrDefault("eventDate", now()));
        Map<String, Object> created = dispatcher.getObject().execute("accounting", "createInvoice", invoiceInput);
        String invoiceId = text(created.get("invoiceId"));
        for (Map<String, Object> role : roles) {
            dispatcher.getObject().execute("accounting", "createInvoiceRole", Map.of("invoiceId", invoiceId,
                    "partyId", role.get("partyId"), "roleTypeId", role.get("roleTypeId"), "datetimePerformed", now()));
        }
        for (Object contact : find("OrderContactMech", Map.of("orderId", orderId, "contactMechPurposeTypeId", "BILLING_LOCATION"))) {
            dispatcher.getObject().execute("accounting", "createInvoiceContactMech", Map.of("invoiceId", invoiceId,
                    "contactMechId", value(contact, "contactMechId"), "contactMechPurposeTypeId", "BILLING_LOCATION"));
        }
        Object store = order.get("productStoreId") == null ? null : findOne("ProductStore", Map.of("productStoreId", order.get("productStoreId")));
        if (store != null && value(store, "payToPartyId") != null) {
            List<Object> purposes = new ArrayList<>(find("PartyContactMechPurpose", Map.of("partyId", value(store, "payToPartyId"), "contactMechPurposeTypeId", "PAYMENT_LOCATION")));
            purposes.sort(java.util.Comparator.comparing(row -> (Timestamp) value(row, "fromDate"), java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder())));
            for (Object purpose : purposes) {
                if (!currentContact(purpose)) continue;
                boolean active = find("PartyContactMech", Map.of("partyId", value(store, "payToPartyId"), "contactMechId", value(purpose, "contactMechId")))
                        .stream().anyMatch(this::currentContact);
                if (!active) continue;
                if (findOne("PostalAddress", Map.of("contactMechId", value(purpose, "contactMechId"))) != null) {
                    dispatcher.getObject().execute("accounting", "createInvoiceContactMech", Map.of("invoiceId", invoiceId,
                            "contactMechId", value(purpose, "contactMechId"), "contactMechPurposeTypeId", "PAYMENT_LOCATION"));
                }
                break;
            }
        }
        for (Map<String, Object> orderItem : billable) {
            BigDecimal quantity = money(orderItem.get("remainingQuantity"));
            BigDecimal amount = money(orderItem.get("unitPrice"));
            String itemType = products.invoiceItemType(
                    "SALES_INVOICE", text(orderItem.get("orderItemTypeId")));
            Map<String, Object> itemInput = new LinkedHashMap<>();
            itemInput.put("invoiceId", invoiceId);
            itemInput.put("invoiceItemTypeId", itemType);
            itemInput.put("productId", orderItem.get("productId"));
            itemInput.put("quantity", quantity);
            itemInput.put("amount", amount);
            itemInput.put("description", orderItem.get("itemDescription"));
            itemInput.put("inventoryItemId", orderItem.get("inventoryItemId"));
            itemInput.put("productFeatureId", orderItem.get("productFeatureId"));
            itemInput.put("overrideGlAccountId", orderItem.get("overrideGlAccountId"));
            Map<String, Object> product = products.findProduct(text(orderItem.get("productId")));
            if (product != null) itemInput.put("taxableFlag", product.get("taxable"));
            Map<String, Object> invoiceItem = dispatcher.getObject().execute("accounting", "createInvoiceItem", itemInput);
            Map<String, Object> billing = new LinkedHashMap<>();
            billing.put("invoiceId", invoiceId);
            billing.put("invoiceItemSeqId", invoiceItem.get("invoiceItemSeqId"));
            billing.put("orderId", orderId);
            billing.put("orderItemSeqId", orderItem.get("orderItemSeqId"));
            billing.put("quantity", quantity);
            billing.put("amount", amount);
            if (orderItem.get("itemIssuanceId") != null) billing.put("itemIssuanceId", orderItem.get("itemIssuanceId"));
            if (registry.componentFor("order", "createOrderItemBilling").isPresent()) {
                dispatcher.getObject().execute("order", "createOrderItemBilling", billing);
            } else {
                orders.createOrderItemBilling(billing);
            }
            if (orderItem.get("itemIssuanceId") != null) {
                Object issuance = findOne("ItemIssuance", Map.of("itemIssuanceId", orderItem.get("itemIssuanceId")));
                if (issuance != null && value(issuance, "shipmentId") != null && value(issuance, "shipmentItemSeqId") != null) {
                    dispatcher.getObject().execute("product", "createShipmentItemBilling", Map.of("invoiceId", invoiceId,
                            "invoiceItemSeqId", invoiceItem.get("invoiceItemSeqId"), "shipmentId", value(issuance, "shipmentId"),
                            "shipmentItemSeqId", value(issuance, "shipmentItemSeqId")));
                }
            }
        }
        return Map.of("invoiceId", invoiceId);
    }

    /**
     * Migrated from applications/accounting/src/main/java/org/apache/ofbiz/accounting/invoice/InvoiceServices.java:2391.
     */
    private Map<String, Object> checkInvoicePaymentApplications(Map<String, Object> input) {
        Object invoice = requireEntity("Invoice", "invoiceId", input);
        if ("INVOICE_READY".equals(value(invoice, "statusId"))
                && invoiceTotal(invoice).subtract(invoiceApplied(text(value(invoice, "invoiceId")))).signum() == 0) {
            requireValidChange("INVOICE_READY", "INVOICE_PAID");
            set(invoice, "statusId", "INVOICE_PAID");
            set(invoice, "paidDate", now());
            addInvoiceStatus(invoice, "INVOICE_PAID", null);
        }
        return Map.of();
    }

    /**
     * Migrated from applications/accounting/src/main/java/org/apache/ofbiz/accounting/invoice/InvoiceWorker.java:72,200,212.
     */
    private Map<String, Object> getInvoiceTotal(Map<String, Object> input) {
        Object invoice = requireEntity("Invoice", "invoiceId", input);
        return Map.of("invoiceTotal", invoiceTotal(invoice));
    }

    /**
     * Migrated from applications/accounting/src/main/java/org/apache/ofbiz/accounting/invoice/InvoiceWorker.java:109.
     */
    private Map<String, Object> getInvoiceItemTotal(Map<String, Object> input) {
        String invoiceId = required(input, "invoiceId");
        String invoiceItemSeqId = required(input, "invoiceItemSeqId");
        Object invoiceItem = findOne("InvoiceItem", Map.of(
                "invoiceId", invoiceId, "invoiceItemSeqId", invoiceItemSeqId));
        if (invoiceItem == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "Invoice item not found");
        }
        return Map.of("invoiceItemTotal", invoiceItemTotal(invoiceItem));
    }

    /**
     * Migrated from applications/accounting/src/main/java/org/apache/ofbiz/accounting/invoice/InvoiceWorker.java:444-463.
     */
    private Map<String, Object> getInvoiceNotApplied(Map<String, Object> input) {
        Object invoice = requireEntity("Invoice", "invoiceId", input);
        return Map.of("notApplied", invoiceNotApplied(invoice));
    }

    /**
     * Migrated from applications/accounting/src/main/java/org/apache/ofbiz/accounting/invoice/InvoiceWorker.java:474-536.
     */
    private Map<String, Object> getInvoiceApplied(Map<String, Object> input) {
        Object invoice = requireEntity("Invoice", "invoiceId", input);
        return Map.of("applied", invoiceApplied(text(value(invoice, "invoiceId"))));
    }

    /**
     * Migrated from applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy:38.
     */
    private Map<String, Object> createPayment(Map<String, Object> input) {
        require(text(input.get("paymentMethodTypeId")) != null, "Payment method type is required");
        Object payment = newEntity("Payment");
        assign(payment, input);
        String paymentId = text(input.get("paymentId"));
        if (paymentId == null) {
            paymentId = sequences.nextId("Payment");
        }
        set(payment, "paymentId", paymentId);
        setDefault(payment, "statusId", "PMNT_NOT_PAID");
        setDefault(payment, "effectiveDate", now());
        entityManager.persist(payment);
        glPostingEca.paymentCreated(paymentId, text(value(payment, "statusId")));
        return Map.of("paymentId", paymentId);
    }

    /**
     * Migrated from applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy:726.
     */
    private Map<String, Object> createPaymentApplication(Map<String, Object> input) {
        String invoiceId = nonBlank(input.get("invoiceId"));
        require(invoiceId != null || nonBlank(input.get("billingAccountId")) != null
                        || nonBlank(input.get("taxAuthGeoId")) != null || nonBlank(input.get("toPaymentId")) != null,
                "Payment application target is required");
        Object payment = requireEntity("Payment", "paymentId", input);
        Object invoice = invoiceId == null ? null : requireEntity("Invoice", "invoiceId", input);
        BigDecimal paymentAvailable = paymentNotApplied(text(value(payment, "paymentId")));
        BigDecimal invoiceAvailable = BigDecimal.ZERO;
        if (invoice != null) {
            Object currency = value(invoice, "currencyUomId");
            Object paymentCurrency = value(payment, "currencyUomId");
            Object actualCurrency = value(payment, "actualCurrencyUomId");
            require(currency != null && (currency.equals(paymentCurrency) || currency.equals(actualCurrency)),
                    "Currencies of invoice and payment are not compatible");
            invoiceAvailable = invoiceNotApplied(invoice);
        }
        String toPaymentId = nonBlank(input.get("toPaymentId"));
        Object toPayment = toPaymentId == null ? null : requireEntity("Payment", "paymentId", Map.of(
                "paymentId", toPaymentId));
        BigDecimal amount;
        if (input.get("amountApplied") == null || money(input.get("amountApplied")).signum() == 0) {
            if (invoice != null) {
                amount = paymentAvailable.min(invoiceAvailable);
            } else if (toPayment != null) {
                amount = paymentAvailable.min(paymentNotApplied(text(value(toPayment, "paymentId"))));
            } else {
                amount = paymentAvailable;
            }
        } else {
            amount = money(input.get("amountApplied"));
        }
        if (invoice != null) {
            require(amount.signum() >= 0 && amount.compareTo(invoiceAvailable) <= 0
                            && amount.compareTo(paymentAvailable) <= 0,
                    "Payment application exceeds invoice or payment balance");
        } else {
            require(amount.signum() >= 0 && amount.compareTo(paymentAvailable) <= 0,
                    "Payment application exceeds payment balance");
        }
        Object application = newEntity("PaymentApplication");
        assign(application, input);
        set(application, "paymentApplicationId", sequences.nextId("PaymentApplication"));
        set(application, "paymentId", value(payment, "paymentId"));
        setIfWritable(application, "invoiceId", input.get("invoiceId"));
        set(application, "amountApplied", amount);
        entityManager.persist(application);
        glPostingEca.paymentApplicationCreated(text(value(application, "paymentApplicationId")),
                invoiceId, text(value(payment, "paymentTypeId")));
        if (invoice != null && "INVOICE_READY".equals(value(invoice, "statusId"))
                && invoiceTotal(invoice).subtract(invoiceApplied(invoiceId)).signum() == 0) {
            requireValidChange("INVOICE_READY", "INVOICE_PAID");
            set(invoice, "statusId", "INVOICE_PAID");
            set(invoice, "paidDate", now());
            addInvoiceStatus(invoice, "INVOICE_PAID", null);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("amountApplied", amount);
        result.put("paymentApplicationId", value(application, "paymentApplicationId"));
        result.put("paymentTypeId", value(payment, "paymentTypeId"));
        return result;
    }

    /**
     * Migrated from applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy:784.
     */
    private Map<String, Object> setPaymentStatus(Map<String, Object> input) {
        Object payment = requireEntity("Payment", "paymentId", input);
        String oldStatusId = text(value(payment, "statusId"));
        String statusId = required(input, "statusId");
        if (!oldStatusId.equals(statusId)) {
            requireValidChange(oldStatusId, statusId);
            if ("PMNT_RECEIVED".equals(statusId) || "PMNT_SENT".equals(statusId)) {
                require(value(payment, "paymentMethodId") != null, "Payment method is required");
            }
            if ("PMNT_CONFIRMED".equals(statusId)) {
                require(paymentNotApplied(text(value(payment, "paymentId"))).signum() == 0,
                        "Payment must be fully applied before confirmation");
            }
        }
        set(payment, "statusId", statusId);
        glPostingEca.paymentStatusChanged(text(value(payment, "paymentId")), oldStatusId, statusId);
        return Map.of("oldStatusId", oldStatusId);
    }

    private BigDecimal invoiceTotal(Object invoice) {
        String invoiceId = text(value(invoice, "invoiceId"));
        return find("InvoiceItem", Map.of("invoiceId", invoiceId)).stream()
                .map(item -> money(value(item, "amount")).multiply(value(item, "quantity") == null
                        ? BigDecimal.ONE : money(value(item, "quantity"))))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal invoiceItemTotal(Object invoiceItem) {
        BigDecimal quantity = value(invoiceItem, "quantity") == null
                ? BigDecimal.ONE : money(value(invoiceItem, "quantity"));
        return quantity.multiply(money(value(invoiceItem, "amount"))).setScale(2, RoundingMode.HALF_UP);
    }

    private Map<String, Object> createMatchingPaymentApplication(Map<String, Object> input) {
        Object invoice = input.get("invoiceId") == null ? null : findOne("Invoice", Map.of("invoiceId", input.get("invoiceId")));
        Object payment = input.get("paymentId") == null ? null : findOne("Payment", Map.of("paymentId", input.get("paymentId")));
        if (invoice != null) {
            BigDecimal total = invoiceTotal(invoice);
            Object invoiceCurrency = value(invoice, "currencyUomId");
            payment = find("Payment", Map.of("partyIdFrom", value(invoice, "partyId"), "partyIdTo", value(invoice, "partyIdFrom"))).stream()
                    .filter(row -> !"PMNT_CONFIRMED".equals(value(row, "statusId"))
                            && money(value(row, "amount")).compareTo(total) == 0
                            && java.util.Objects.equals(value(row, "currencyUomId"), invoiceCurrency))
                    .min(java.util.Comparator.comparing(row -> (Timestamp) value(row, "effectiveDate"), java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())))
                    .orElse(null);
            if (payment != null && !find("PaymentApplication", Map.of("paymentId", value(payment, "paymentId"))).isEmpty()) payment = null;
        } else if (payment != null) {
            List<Object> invoices = new ArrayList<>(find("Invoice", Map.of("partyId", value(payment, "partyIdFrom"), "partyIdFrom", value(payment, "partyIdTo"))));
            invoices.sort(java.util.Comparator.comparing(row -> (Timestamp) value(row, "invoiceDate"), java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())));
            for (Object candidate : invoices) {
                if (!List.of("INVOICE_READY", "INVOICE_PAID", "INVOICE_CANCELLED", "INVOICE_WRITEOFF").contains(value(candidate, "statusId"))
                        && List.of("SALES_INVOICE", "PURCHASE_INVOICE").contains(value(candidate, "invoiceTypeId"))
                        && invoiceTotal(candidate).compareTo(money(value(payment, "amount"))) == 0
                        && java.util.Objects.equals(value(candidate, "currencyUomId"), value(payment, "currencyUomId"))) invoice = candidate;
            }
            if (invoice != null && !find("PaymentApplication", Map.of("invoiceId", value(invoice, "invoiceId"))).isEmpty()) invoice = null;
        }
        if (invoice != null && payment != null) {
            dispatcher.getObject().execute("accounting", "createPaymentApplication", Map.of("invoiceId", value(invoice, "invoiceId"),
                    "paymentId", value(payment, "paymentId"), "amountApplied", value(payment, "amount")));
        }
        return Map.of();
    }

    private Map<String, Object> removePaymentApplication(Map<String, Object> input) {
        Object application = requireEntity("PaymentApplication", "paymentApplicationId", input);
        for (String field : List.of("paymentId", "toPaymentId")) {
            if (value(application, field) != null) {
                Object payment = findOne("Payment", Map.of("paymentId", value(application, field)));
                require(payment == null || !"PMNT_CONFIRMED".equals(value(payment, "statusId")), "Confirmed payment applications cannot be removed");
            }
        }
        if (value(application, "invoiceId") != null) {
            Object invoice = findOne("Invoice", Map.of("invoiceId", value(application, "invoiceId")));
            if (invoice != null && "INVOICE_PAID".equals(value(invoice, "statusId"))) {
                dispatcher.getObject().execute("accounting", "setInvoiceStatus", Map.of("invoiceId", value(invoice, "invoiceId"), "statusId", "INVOICE_READY"));
            }
        }
        Map<String, Object> fields = entities.toMap(application);
        entityManager.remove(application);
        return fields;
    }

    private BigDecimal invoiceApplied(String invoiceId) {
        return find("PaymentApplication", Map.of("invoiceId", invoiceId)).stream()
                .filter(application -> validInvoiceApplication(application))
                .map(application -> money(value(application, "amountApplied")))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private boolean validInvoiceApplication(Object application) {
        Object paymentId = value(application, "paymentId");
        Object payment = findOne("Payment", Map.of("paymentId", paymentId));
        if (payment == null) {
            return false;
        }
        String status = text(value(payment, "statusId"));
        String paymentType = text(value(payment, "paymentTypeId"));
        return ("PMNT_RECEIVED".equals(status)
                    && ("CUSTOMER_PAYMENT".equals(paymentType) || "CUSTOMER_REFUND".equals(paymentType)))
                || ("PMNT_SENT".equals(status)
                    && ("VENDOR_PAYMENT".equals(paymentType) || "CUSTOMER_REFUND".equals(paymentType)));
    }

    private boolean currentContact(Object contact) {
        Timestamp from = (Timestamp) value(contact, "fromDate");
        Timestamp thru = (Timestamp) value(contact, "thruDate");
        return (from == null || !from.after(now())) && (thru == null || thru.after(now()));
    }

    private BigDecimal invoiceNotApplied(Object invoice) {
        BigDecimal allocated = BigDecimal.ZERO;
        for (Object application : find("PaymentApplication", Map.of("invoiceId", value(invoice, "invoiceId")))) {
            Object payment = findOne("Payment", Map.of("paymentId", value(application, "paymentId")));
            if (payment != null && (value(payment, "effectiveDate") == null || !((Timestamp) value(payment, "effectiveDate")).after(now()))) {
                allocated = allocated.add(money(value(application, "amountApplied")));
            }
        }
        return invoiceTotal(invoice).subtract(allocated)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal paymentNotApplied(String paymentId) {
        Object payment = findOne("Payment", Map.of("paymentId", paymentId));
        BigDecimal total = money(value(payment, "amount"));
        BigDecimal applied = find("PaymentApplication", Map.of("paymentId", paymentId)).stream()
                .map(application -> money(value(application, "amountApplied")))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.subtract(applied).setScale(2, RoundingMode.HALF_UP);
    }

    private void requireValidChange(String oldStatusId, String newStatusId) {
        boolean valid = !find("StatusValidChange", Map.of(
                "statusId", oldStatusId, "statusIdTo", newStatusId)).isEmpty();
        require(valid, "No valid status change from " + oldStatusId + " to " + newStatusId);
    }

    private void addInvoiceStatus(Object invoice, String statusId, Object statusDate) {
        Object history = newEntity("InvoiceStatus");
        set(history, "invoiceId", value(invoice, "invoiceId"));
        set(history, "statusId", statusId);
        setIfWritable(history, "statusDate", statusDate == null ? now() : statusDate);
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) setIfWritable(history, "changeByUserLoginId", authentication.getName());
        entities.primaryKeys(history.getClass()).forEach(key -> {
            if (entities.get(history, key) == null) {
                throw badRequest("Missing InvoiceStatus primary key: " + key);
            }
        });
        entityManager.persist(history);
    }

    private Object requireEntity(String entityName, String idName, Map<String, ?> input) {
        String id = required(input, idName);
        Object entity = findOne(entityName, Map.of(idName, id));
        if (entity == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, entityName + " not found");
        }
        return entity;
    }

    private Object findOne(String entityName, Map<String, ?> filters) {
        List<?> matches = AccountingEntityQueries.findByEquality(entityManager, entities.entityClass(entityName), filters);
        return matches.isEmpty() ? null : matches.get(0);
    }

    private List<?> find(String entityName, Map<String, ?> filters) {
        return AccountingEntityQueries.findByEquality(entityManager, entities.entityClass(entityName), filters);
    }

    private Object newEntity(String entityName) {
        return entities.newEntity(entities.entityClass(entityName));
    }

    private void assign(Object entity, Map<String, ?> values) {
        values.forEach((name, value) -> {
            if (!List.of("userLogin", "locale").contains(name) && value != null) {
                entities.set(entity, name, value);
            }
        });
    }

    private void set(Object entity, String name, Object value) {
        entities.set(entity, name, value);
    }

    private void setIfWritable(Object entity, String name, Object value) {
        if (value == null) {
            return;
        }
        try {
            entities.set(entity, name, value);
        } catch (MigrationException ignored) {
            return;
        }
    }

    private void setDefault(Object entity, String name, Object defaultValue) {
        if (value(entity, name) == null) {
            set(entity, name, defaultValue);
        }
    }

    private Object value(Object entity, String name) {
        return entities.get(entity, name);
    }

    private static String roleParty(List<Map<String, Object>> roles, String roleTypeId) {
        return roles.stream()
                .filter(role -> roleTypeId.equals(role.get("roleTypeId")))
                .map(role -> text(role.get("partyId")))
                .filter(partyId -> partyId != null)
                .findFirst()
                .orElse(null);
    }

    private static String required(Map<String, ?> values, String name) {
        String result = text(values.get(name));
        if (result == null || result.isBlank()) {
            throw badRequest("Missing required parameter: " + name);
        }
        return result;
    }

    private static String text(Object value) {
        return value == null ? null : value.toString();
    }

    private static String nonBlank(Object value) {
        String result = text(value);
        return result == null || result.isBlank() ? null : result;
    }

    private static BigDecimal money(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value instanceof BigDecimal amount ? amount : new BigDecimal(value.toString());
    }

    private static Timestamp now() {
        return Timestamp.from(Instant.now());
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw badRequest(message);
        }
    }

    private static MigrationException badRequest(String message) {
        return new MigrationException(HttpStatus.BAD_REQUEST, message);
    }
}
