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
import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.SequenceService;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentMethodServiceHandlers {
    private static final String COMPONENT = "accounting";
    private final EntityManager entityManager;
    private final EntityRegistry entities;
    private final SequenceService sequences;
    private final ServiceHandlerRegistry registry;

    public PaymentMethodServiceHandlers(
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
        registry.registerHandler(COMPONENT, "setPaymentMethodAddress", "UPDATE", "CreditCard",
                this::setPaymentMethodAddress);
        registry.registerHandler(COMPONENT, "updatePaymentMethodAddress", "UPDATE", "CreditCard",
                this::updatePaymentMethodAddress);
        registry.registerHandler(COMPONENT, "createPayPalPaymentMethod", "CREATE", "PaymentMethod",
                this::createPayPalPaymentMethod);
        registry.registerHandler(COMPONENT, "createPaymentGroupMember", "CREATE", this::createPaymentGroupMember);
        registry.registerHandler(COMPONENT, "expirePaymentGroupMember", "UPDATE", this::expirePaymentGroupMember);
    }

    /**
     * Migrated from applications/accounting/minilang/payment/PaymentMethodServices.xml:24.
     */
    private Map<String, Object> setPaymentMethodAddress(Map<String, Object> input) {
        String paymentMethodId = required(input, "paymentMethodId");
        String contactMechId = required(input, "contactMechId");
        Object paymentMethod = requireOne("PaymentMethod", Map.of("paymentMethodId", paymentMethodId));
        String typeId = text(entities.get(paymentMethod, "paymentMethodTypeId"));
        if ("CREDIT_CARD".equals(typeId) || "EFT_ACCOUNT".equals(typeId)) {
            Object creditCard = requireOne("CreditCard", Map.of("paymentMethodId", paymentMethodId));
            entities.set(creditCard, "contactMechId", contactMechId);
        }
        return Map.of();
    }

    /**
     * Migrated from applications/accounting/minilang/payment/PaymentMethodServices.xml:45.
     */
    private Map<String, Object> updatePaymentMethodAddress(Map<String, Object> input) {
        String oldContactMechId = required(input, "oldContactMechId");
        required(input, "contactMechId");
        AccountingEntityQueries.findByEquality(entityManager, entities.entityClass("CreditCard"), Map.of("contactMechId", oldContactMechId));
        AccountingEntityQueries.findByEquality(entityManager, entities.entityClass("EftAccount"), Map.of("contactMechId", oldContactMechId));
        return Map.of();
    }

    /**
     * Migrated from applications/accounting/minilang/payment/PaymentMethodServices.xml:84.
     */
    private Map<String, Object> createPayPalPaymentMethod(Map<String, Object> input) {
        Object paymentMethod = entities.newEntity(entities.entityClass("PaymentMethod"));
        String paymentMethodId = text(input.get("paymentMethodId"));
        if (paymentMethodId == null) {
            paymentMethodId = sequences.nextId("PaymentMethod");
        }
        entities.set(paymentMethod, "paymentMethodId", paymentMethodId);
        setNonPrimaryFields(paymentMethod, input);
        entities.set(paymentMethod, "paymentMethodTypeId", "EXT_PAYPAL");
        entityManager.persist(paymentMethod);

        Object paypal = entities.newEntity(entities.entityClass("PayPalPaymentMethod"));
        entities.set(paypal, "paymentMethodId", paymentMethodId);
        setNonPrimaryFields(paypal, input);
        entityManager.persist(paypal);
        entityManager.flush();
        return Map.of("paymentMethodId", paymentMethodId);
    }

    /**
     * Migrated from applications/accounting/minilang/payment/PaymentMethodServices.xml:101.
     */
    private Map<String, Object> createPaymentGroupMember(Map<String, Object> input) {
        String paymentGroupId = required(input, "paymentGroupId");
        String paymentId = required(input, "paymentId");
        Object paymentGroup = requireOne("PaymentGroup", Map.of("paymentGroupId", paymentGroupId));
        Object payment = requireOne("Payment", Map.of("paymentId", paymentId));
        String groupTypeId = text(entities.get(paymentGroup, "paymentGroupTypeId"));
        if ("CHECK_RUN".equals(groupTypeId) && !isPaymentType(payment, "DISBURSEMENT")) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Cannot create incoming payment");
        }
        if ("BATCH_PAYMENT".equals(groupTypeId) && !isPaymentType(payment, "RECEIPT")) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Cannot create outgoing payment");
        }
        if (!"CHECK_RUN".equals(groupTypeId) && !"BATCH_PAYMENT".equals(groupTypeId)) {
            return Map.of();
        }

        Object member = entities.newEntity(entities.entityClass("PaymentGroupMember"));
        Map<String, Object> values = new LinkedHashMap<>(input);
        values.putIfAbsent("fromDate", Timestamp.from(Instant.now()));
        setNonPrimaryFields(member, values);
        entities.set(member, "paymentGroupId", paymentGroupId);
        entities.set(member, "paymentId", paymentId);
        entities.set(member, "fromDate", values.get("fromDate"));
        entityManager.persist(member);
        entityManager.flush();
        return Map.of();
    }

    /**
     * Migrated from applications/accounting/minilang/payment/PaymentMethodServices.xml:77.
     */
    private Map<String, Object> expirePaymentGroupMember(Map<String, Object> input) {
        Object member = requireOne("PaymentGroupMember", primaryKey("PaymentGroupMember", input));
        entities.set(member, "thruDate", Timestamp.from(Instant.now()));
        entityManager.flush();
        return Map.of();
    }

    private boolean isPaymentType(Object payment, String wantedTypeId) {
        String paymentTypeId = text(entities.get(payment, "paymentTypeId"));
        while (paymentTypeId != null) {
            if (wantedTypeId.equals(paymentTypeId)) {
                return true;
            }
            Object paymentType = requireOne("PaymentType", Map.of("paymentTypeId", paymentTypeId));
            paymentTypeId = text(entities.get(paymentType, "parentTypeId"));
        }
        return false;
    }

    private Object requireOne(String entityName, Map<String, ?> filters) {
        Class<?> type = entities.entityClass(entityName);
        List<?> matches = AccountingEntityQueries.findByEquality(entityManager, type, filters);
        if (matches.isEmpty()) {
            throw new MigrationException(HttpStatus.NOT_FOUND, entityName + " not found");
        }
        return matches.get(0);
    }

    private Map<String, Object> primaryKey(String entityName, Map<String, Object> input) {
        Class<?> type = entities.entityClass(entityName);
        Map<String, Object> key = new LinkedHashMap<>();
        for (String name : entities.primaryKeys(type)) {
            key.put(name, required(input, name));
        }
        return key;
    }

    private void setNonPrimaryFields(Object entity, Map<String, Object> input) {
        Set<String> primaryKeys = Set.copyOf(entities.primaryKeys(entity.getClass()));
        Set<String> fields = entityManager.getMetamodel().entity(entity.getClass()).getAttributes().stream()
                .map(attribute -> attribute.getName()).collect(java.util.stream.Collectors.toSet());
        input.forEach((name, value) -> {
            if (value != null && fields.contains(name)
                    && !primaryKeys.contains(name) && !List.of("userLogin", "locale").contains(name)) {
                entities.set(entity, name, value);
            }
        });
    }

    private static String required(Map<String, Object> input, String name) {
        String value = text(input.get(name));
        if (value == null || value.isBlank()) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Missing required parameter: " + name);
        }
        return value;
    }

    private static String text(Object value) {
        if (value == null || value.toString().isBlank()) {
            return null;
        }
        return value.toString();
    }
}
