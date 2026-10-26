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
package org.apache.ofbiz.migration.product.adapter;

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
import org.apache.ofbiz.migration.product.ProductQueries;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class LocalOrderAdapter implements OrderPort {
    private final ProductUnportedPort unported;
    private final EntityRegistry entities;
    private final ProductQueries queries;
    private final EntityManager entityManager;
    private final SequenceService sequences;

    public LocalOrderAdapter(
            ProductUnportedPort unported,
            EntityRegistry entities,
            ProductQueries queries,
            EntityManager entityManager,
            SequenceService sequences) {
        this.unported = unported;
        this.entities = entities;
        this.queries = queries;
        this.entityManager = entityManager;
        this.sequences = sequences;
    }

    @Override
    public Map<String, Object> invoke(String serviceName, Map<String, Object> input) {
        if ("changeOrderItemStatus".equals(serviceName)) {
            return changeOrderItemStatus(input);
        }
        return unported.execute(serviceName);
    }

    private Map<String, Object> changeOrderItemStatus(Map<String, Object> input) {
        String orderId = required(input, "orderId");
        String orderItemSeqId = required(input, "orderItemSeqId");
        String statusId = required(input, "statusId");
        Object orderItem = entities.findByPrimaryKey(entities.entityClass("OrderItem"),
                Map.of("orderId", orderId, "orderItemSeqId", orderItemSeqId));
        if (orderItem == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "OrderItem not found");
        }

        String currentStatusId = stringValue(entities.get(orderItem, "statusId"));
        if (statusId.equals(currentStatusId)) {
            return Map.of();
        }
        boolean validTransition = currentStatusId != null
                && !queries.findAll(entities.entityClass("StatusValidChange"),
                        Map.of("statusId", currentStatusId, "statusIdTo", statusId)).isEmpty();
        if (!validTransition) {
            throw new MigrationException(HttpStatus.BAD_REQUEST,
                    "Invalid order item status transition from " + currentStatusId + " to " + statusId);
        }

        entities.set(orderItem, "statusId", statusId);
        Object orderStatus = entities.newEntity(entities.entityClass("OrderStatus"));
        entities.set(orderStatus, "orderStatusId", sequences.nextId("OrderStatus"));
        entities.set(orderStatus, "statusId", statusId);
        entities.set(orderStatus, "orderId", orderId);
        entities.set(orderStatus, "orderItemSeqId", orderItemSeqId);
        Object statusDateTime = input.get("statusDateTime");
        entities.set(orderStatus, "statusDatetime", statusDateTime instanceof Timestamp timestamp
                ? timestamp : Timestamp.from(Instant.now()));
        entities.set(orderStatus, "statusUserLogin",
                SecurityContextHolder.getContext().getAuthentication().getName());
        entities.set(orderStatus, "changeReason", input.get("changeReason"));
        entityManager.persist(orderStatus);
        return Map.of();
    }

    private static String required(Map<String, Object> input, String name) {
        String value = stringValue(input.get(name));
        if (value == null || value.isBlank()) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Missing required input: " + name);
        }
        return value;
    }

    private static String stringValue(Object value) {
        return value == null ? null : value.toString();
    }

    @Override
    public Map<String, Object> findOrderHeader(String orderId) {
        Object entity = entities.findByPrimaryKey(
                entities.entityClass("OrderHeader"), Map.of("orderId", orderId));
        return entity == null ? Map.of() : entities.toMap(entity);
    }

    @Override
    public Map<String, Object> findOrderItem(String orderId, String orderItemSeqId) {
        Object entity = entities.findByPrimaryKey(entities.entityClass("OrderItem"),
                Map.of("orderId", orderId, "orderItemSeqId", orderItemSeqId));
        return entity == null ? Map.of() : entities.toMap(entity);
    }

    @Override
    public List<Map<String, Object>> findOrderItemShipGroups(String orderId) {
        return rows("OrderItemShipGroup", Map.of("orderId", orderId));
    }

    @Override
    public List<Map<String, Object>> findApprovedOrderItemShipGroupAssocs(String orderId) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> association :
                rows("OrderItemShipGroupAssoc", Map.of("orderId", orderId))) {
            Map<String, Object> item = findOrderItem(
                    orderId, String.valueOf(association.get("orderItemSeqId")));
            if (!"ITEM_APPROVED".equals(item.get("statusId"))) {
                continue;
            }
            Map<String, Object> row = new LinkedHashMap<>(item);
            row.putAll(association);
            row.put("orderItemQuantity", item.get("quantity"));
            row.put("orderItemCancelQuantity", item.get("cancelQuantity"));
            row.put("statusId", item.get("statusId"));
            result.add(row);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> findApprovedReservations(String orderId) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> reservation :
                rows("OrderItemShipGrpInvRes", Map.of("orderId", orderId))) {
            Map<String, Object> item = findOrderItem(
                    orderId, String.valueOf(reservation.get("orderItemSeqId")));
            if (!"ITEM_APPROVED".equals(item.get("statusId"))) {
                continue;
            }
            Object inventory = entities.findByPrimaryKey(
                    entities.entityClass("InventoryItem"),
                    Map.of("inventoryItemId", reservation.get("inventoryItemId")));
            if (inventory == null) {
                continue;
            }
            Map<String, Object> row = new LinkedHashMap<>(reservation);
            Map<String, Object> inventoryValues = entities.toMap(inventory);
            inventoryValues.remove("productId");
            inventoryValues.remove("statusId");
            inventoryValues.remove("comments");
            row.putAll(inventoryValues);
            row.put("statusId", item.get("statusId"));
            result.add(row);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> findOrderRoles(String orderId) {
        return rows("OrderRole", Map.of("orderId", orderId));
    }

    private List<Map<String, Object>> rows(String entityName, Map<String, ?> filters) {
        return queries.findAll(entities.entityClass(entityName), filters).stream()
                .map(entities::toMap)
                .toList();
    }
}
