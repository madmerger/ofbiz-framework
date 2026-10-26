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
package org.apache.ofbiz.migration.accounting.adapter.local;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.accounting.InventoryAccountingPort;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class LocalInventoryAccountingAdapter implements InventoryAccountingPort {
    private final EntityRegistry entities;
    private final EntityManager entityManager;

    public LocalInventoryAccountingAdapter(EntityRegistry entities, EntityManager entityManager) {
        this.entities = entities;
        this.entityManager = entityManager;
    }

    @Override
    public Map<String, Object> findInventoryItem(String inventoryItemId) {
        Object item = entities.findByPrimaryKey(
                entities.entityClass("InventoryItem"), Map.of("inventoryItemId", inventoryItemId));
        if (item == null) {
            return null;
        }
        Map<String, Object> values = entities.toMap(item);
        if (values.get("ownerPartyId") == null && values.get("facilityId") != null) {
            Object facility = entities.findByPrimaryKey(
                    entities.entityClass("Facility"), Map.of("facilityId", values.get("facilityId")));
            if (facility != null) {
                values.put("ownerPartyId", entities.get(facility, "ownerPartyId"));
            }
        }
        return values;
    }

    @Override
    public List<Map<String, Object>> findProductInventoryItems() {
        return entityManager.createQuery("select item from InventoryItem item", Object.class)
                .setMaxResults(10000).getResultList().stream().map(item -> {
                    Map<String, Object> values = entities.toMap(item);
                    Object productId = values.get("productId");
                    Object product = productId == null ? null : entities.findByPrimaryKey(
                            entities.entityClass("Product"), Map.of("productId", productId));
                    if (product != null) {
                        values.put("primaryProductCategoryId", entities.get(product, "primaryProductCategoryId"));
                    }
                    return values;
                }).toList();
    }

    @Override
    public BigDecimal averageCost(Map<String, Object> inventoryItem) {
        Object ownerPartyId = inventoryItem.get("ownerPartyId");
        if (ownerPartyId != null) {
            List<?> costs = entities.findByEquality(entities.entityClass("ProductAverageCost"), Map.of(
                    "productAverageCostTypeId", "SIMPLE_AVG_COST",
                    "organizationPartyId", ownerPartyId,
                    "productId", inventoryItem.get("productId"),
                    "facilityId", inventoryItem.get("facilityId")));
            Date now = new Date();
            for (Object cost : costs) {
                Date fromDate = date(entities.get(cost, "fromDate"));
                Date thruDate = date(entities.get(cost, "thruDate"));
                if ((fromDate == null || !now.before(fromDate)) && (thruDate == null || now.before(thruDate))) {
                    return decimal(entities.get(cost, "averageCost"));
                }
            }
        }
        return decimal(inventoryItem.get("unitCost"));
    }

    private static Date date(Object value) {
        if (value instanceof Date date) {
            return date;
        }
        return value == null ? null : java.sql.Timestamp.valueOf(value.toString());
    }

    private static BigDecimal decimal(Object value) {
        return value == null ? BigDecimal.ZERO
                : value instanceof BigDecimal amount ? amount : new BigDecimal(value.toString());
    }
}
