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
package org.apache.ofbiz.migration.accounting.adapter.local;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.accounting.OrderPort;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class LocalOrderAdapter implements OrderPort {
    private final EntityRegistry entities;
    private final EntityManager entityManager;

    public LocalOrderAdapter(EntityRegistry entities, EntityManager entityManager) {
        this.entities = entities;
        this.entityManager = entityManager;
    }

    @Override
    public Map<String, Object> findOrder(String orderId) {
        Object order = entities.findByPrimaryKey(entities.entityClass("OrderHeader"), Map.of("orderId", orderId));
        return order == null ? null : entities.toMap(order);
    }

    @Override
    public List<Map<String, Object>> findOrderRoles(String orderId) {
        return find("OrderRole", Map.of("orderId", orderId));
    }

    @Override
    public List<Map<String, Object>> findOrderItems(String orderId) {
        return find("OrderItem", Map.of("orderId", orderId));
    }

    @Override
    public BigDecimal billedQuantity(String orderId, String orderItemSeqId) {
        return find("OrderItemBilling", Map.of("orderId", orderId, "orderItemSeqId", orderItemSeqId)).stream()
                .map(row -> decimal(row.get("quantity")))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void createOrderItemBilling(Map<String, ?> values) {
        Class<?> type = entities.entityClass("OrderItemBilling");
        Object billing = entities.newEntity(type);
        values.forEach((name, value) -> entities.set(billing, name, value));
        entities.primaryKeys(type).forEach(key -> {
            if (entities.get(billing, key) == null) {
                throw new IllegalArgumentException("Missing OrderItemBilling primary key: " + key);
            }
        });
        entityManager.persist(billing);
    }

    @Override
    public String productStorePartyId(String productStoreId) {
        if (productStoreId == null || productStoreId.isBlank()) {
            return null;
        }
        Object store = entities.findByPrimaryKey(
                entities.entityClass("ProductStore"), Map.of("productStoreId", productStoreId));
        if (store == null) {
            return null;
        }
        for (String property : List.of("payToPartyId", "companyPartyId", "partyId")) {
            Object partyId = read(store, property);
            if (partyId != null && !partyId.toString().isBlank()) {
                return partyId.toString();
            }
        }
        return null;
    }

    private List<Map<String, Object>> find(String entityName, Map<String, ?> filters) {
        return entities.findByEquality(entities.entityClass(entityName), filters).stream()
                .map(entities::toMap)
                .toList();
    }

    private static BigDecimal decimal(Object value) {
        return value == null ? BigDecimal.ZERO : new BigDecimal(value.toString());
    }

    private Object read(Object entity, String property) {
        try {
            return entities.get(entity, property);
        } catch (RuntimeException ignored) {
            return null;
        }
    }
}
