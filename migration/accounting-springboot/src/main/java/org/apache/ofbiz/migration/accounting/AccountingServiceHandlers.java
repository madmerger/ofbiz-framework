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

    public AccountingServiceHandlers(
            EntityManager entityManager,
            EntityRegistry entities,
            SequenceService sequences,
            ServiceHandlerRegistry registry,
            OrderPort orders,
            ProductPort products) {
        this.entityManager = entityManager;
        this.entities = entities;
        this.sequences = sequences;
        this.registry = registry;
        this.orders = orders;
        this.products = products;
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
        if ("INVOICE_READY".equals(newStatusId) && money(invoiceNotApplied(invoice)).signum() == 0) {
            requireValidChange(newStatusId, "INVOICE_PAID");
            set(invoice, "statusId", "INVOICE_PAID");
            set(invoice, "paidDate", now());
            addInvoiceStatus(invoice, "INVOICE_PAID", null);
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
                Map<String, Object> row = new LinkedHashMap<>(item);
                row.put("remainingQuantity", remaining);
                billable.add(row);
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
        Map<String, Object> created = createInvoice(invoiceInput);
        String invoiceId = text(created.get("invoiceId"));
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
            Map<String, Object> invoiceItem = createInvoiceItem(itemInput);
            Map<String, Object> billing = new LinkedHashMap<>();
            billing.put("invoiceId", invoiceId);
            billing.put("invoiceItemSeqId", invoiceItem.get("invoiceItemSeqId"));
            billing.put("orderId", orderId);
            billing.put("orderItemSeqId", orderItem.get("orderItemSeqId"));
            billing.put("quantity", quantity);
            billing.put("amount", amount);
            orders.createOrderItemBilling(billing);
        }
        return Map.of("invoiceId", invoiceId);
    }

    /**
     * Migrated from applications/accounting/src/main/java/org/apache/ofbiz/accounting/invoice/InvoiceServices.java:2391.
     */
    private Map<String, Object> checkInvoicePaymentApplications(Map<String, Object> input) {
        Object invoice = requireEntity("Invoice", "invoiceId", input);
        if ("INVOICE_READY".equals(value(invoice, "statusId"))
                && money(invoiceNotApplied(invoice)).signum() == 0) {
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
        if (invoice != null && "INVOICE_READY".equals(value(invoice, "statusId"))
                && money(invoiceNotApplied(invoice)).signum() == 0) {
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

    private BigDecimal invoiceNotApplied(Object invoice) {
        return invoiceTotal(invoice).subtract(invoiceApplied(text(value(invoice, "invoiceId"))))
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
        List<?> matches = entities.findByEquality(entities.entityClass(entityName), filters);
        return matches.isEmpty() ? null : matches.get(0);
    }

    private List<?> find(String entityName, Map<String, ?> filters) {
        return entities.findByEquality(entities.entityClass(entityName), filters);
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
