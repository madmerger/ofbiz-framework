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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.apache.ofbiz.migration.workeffort.adapter.local;

import jakarta.persistence.EntityManager;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.SequenceService;
import org.apache.ofbiz.migration.workeffort.port.CommunicationEventPort;
import org.apache.ofbiz.migration.workeffort.port.InvoicePort;
import org.apache.ofbiz.migration.workeffort.port.PartyPort;
import org.apache.ofbiz.migration.workeffort.port.RatePort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LocalWorkEffortAdapter implements RatePort, InvoicePort, CommunicationEventPort, PartyPort {
    private static final List<String> RATE_FIELDS = List.of(
            "workEffortId", "partyId", "emplPositionTypeId", "rateTypeId", "rateCurrencyUomId");
    private final EntityManager entityManager;
    private final EntityRegistry entities;
    private final SequenceService sequences;

    public LocalWorkEffortAdapter(EntityManager entityManager, EntityRegistry entities, SequenceService sequences) {
        this.entityManager = entityManager;
        this.entities = entities;
        this.sequences = sequences;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getRateAmount(Map<String, ?> criteria) {
        Class<?> type = entities.entityClass("RateAmount");
        List<List<String>> priorities = new ArrayList<>();
        priorities.add(RATE_FIELDS);
        priorities.add(List.of("partyId", "emplPositionTypeId", "rateTypeId", "rateCurrencyUomId"));
        priorities.add(List.of("workEffortId", "partyId", "rateTypeId", "rateCurrencyUomId"));
        priorities.add(List.of("partyId", "rateTypeId", "rateCurrencyUomId"));
        priorities.add(List.of("emplPositionTypeId", "rateTypeId", "rateCurrencyUomId"));
        priorities.add(List.of("rateTypeId", "rateCurrencyUomId"));
        for (List<String> priority : priorities) {
            Map<String, Object> filters = new LinkedHashMap<>();
            for (String field : priority) {
                Object value = criteria.get(field);
                if (value != null && hasAttribute(type, field)) filters.put(field, value);
            }
            if (filters.isEmpty()) continue;
            List<?> rates = entities.findByEquality(type, filters);
            Object match = rates.stream().filter(this::isCurrent).findFirst().orElse(null);
            if (match != null) return entities.toMap(match);
        }
        throw new MigrationException(HttpStatus.NOT_FOUND, "Rate amount not found");
    }

    @Override
    @Transactional
    public Map<String, Object> createInvoice(Map<String, ?> values) {
        Map<String, Object> input = new LinkedHashMap<>(values);
        input.putIfAbsent("invoiceId", sequences.nextId("Invoice"));
        input.putIfAbsent("invoiceDate", Timestamp.from(Instant.now()));
        input.putIfAbsent("invoiceTypeId", "SALES_INVOICE");
        return create("Invoice", input);
    }

    @Override
    @Transactional
    public Map<String, Object> updateInvoice(Map<String, ?> values) {
        return update("Invoice", values);
    }

    @Override
    @Transactional
    public Map<String, Object> createInvoiceItem(Map<String, ?> values) {
        Map<String, Object> input = new LinkedHashMap<>(values);
        input.putIfAbsent("invoiceItemSeqId",
                sequences.nextSubSequence("InvoiceItem", Map.of("invoiceId", input.get("invoiceId")), "invoiceItemSeqId"));
        return create("InvoiceItem", input);
    }

    @Override
    @Transactional
    public Map<String, Object> updateInvoiceItem(Map<String, ?> values) {
        return update("InvoiceItem", values);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> findInvoice(String invoiceId) {
        Object invoice = entities.findByPrimaryKey(
                entities.entityClass("Invoice"), Map.of("invoiceId", invoiceId));
        if (invoice == null) return Map.of();
        return entities.toMap(invoice);
    }

    @Override
    @Transactional
    public Map<String, Object> createCommunicationEvent(Map<String, ?> values) {
        Map<String, Object> input = new LinkedHashMap<>(values);
        input.putIfAbsent("communicationEventId", sequences.nextId("CommunicationEvent"));
        input.putIfAbsent("entryDate", Timestamp.from(Instant.now()));
        return create("CommunicationEvent", input);
    }

    @Override
    @Transactional
    public Map<String, Object> ensurePartyRole(String partyId, String roleTypeId) {
        Class<?> type = entities.entityClass("PartyRole");
        Map<String, Object> key = Map.of("partyId", partyId, "roleTypeId", roleTypeId);
        if (entities.findByPrimaryKey(type, key) == null) {
            Object role = entities.newEntity(type);
            entities.set(role, "partyId", partyId);
            entities.set(role, "roleTypeId", roleTypeId);
            entityManager.persist(role);
        }
        return key;
    }

    private Map<String, Object> create(String entityName, Map<String, ?> values) {
        Class<?> type = entities.entityClass(entityName);
        Object entity = entities.newEntity(type);
        values.forEach((name, value) -> setIfPresent(entityName, type, entity, name, value));
        entityManager.persist(entity);
        return entities.toMap(entity);
    }

    private Map<String, Object> update(String entityName, Map<String, ?> values) {
        Class<?> type = entities.entityClass(entityName);
        Object entity = entities.findByPrimaryKey(type, values);
        if (entity == null) throw new MigrationException(HttpStatus.NOT_FOUND, entityName + " not found");
        values.forEach((name, value) -> setIfPresent(entityName, type, entity, name, value));
        return entities.toMap(entity);
    }

    private void setIfPresent(String entityName, Class<?> type, Object entity, String name, Object value) {
        if (hasAttribute(type, name)) entities.set(entity, name, value);
    }

    private boolean hasAttribute(Class<?> type, String name) {
        try {
            entityManager.getMetamodel().entity(type).getAttribute(name);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private boolean isCurrent(Object rate) {
        Timestamp now = Timestamp.from(Instant.now());
        Object from = hasAttribute(rate.getClass(), "fromDate") ? entities.get(rate, "fromDate") : null;
        Object thru = hasAttribute(rate.getClass(), "thruDate") ? entities.get(rate, "thruDate") : null;
        return (from == null || !((Timestamp) from).after(now))
                && (thru == null || ((Timestamp) thru).after(now));
    }
}
