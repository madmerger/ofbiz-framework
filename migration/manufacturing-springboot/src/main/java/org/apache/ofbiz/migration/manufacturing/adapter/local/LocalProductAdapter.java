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
package org.apache.ofbiz.migration.manufacturing.adapter.local;

import java.util.List;
import java.util.Map;
import jakarta.persistence.EntityManager;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.manufacturing.ProductPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LocalProductAdapter implements ProductPort {
    private final EntityRegistry entities;
    private final EntityManager entityManager;

    public LocalProductAdapter(EntityRegistry entities, EntityManager entityManager) {
        this.entities = entities;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> findProduct(String productId) {
        Object value = entities.findByPrimaryKey(entities.entityClass("Product"), Map.of("productId", productId));
        return value == null ? null : entities.toMap(value);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> findProductAssoc(Map<String, ?> keys) {
        Object value = entities.findByPrimaryKey(entities.entityClass("ProductAssoc"), keys);
        return value == null ? null : entities.toMap(value);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> findProductAssocs(Map<String, ?> filters) {
        return entities.findByEquality(entities.entityClass("ProductAssoc"), filters).stream()
                .map(entities::toMap).toList();
    }

    @Override
    public Map<String, Object> createProductAssoc(Map<String, ?> values) {
        Class<?> type = entities.entityClass("ProductAssoc");
        Object entity = entities.newEntity(type);
        values.forEach((name, value) -> entities.set(entity, name, value));
        entityManager.persist(entity);
        return entities.toMap(entity);
    }
}
