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
package org.apache.ofbiz.migration.order;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public final class OrderEntitySupport {
    private final EntityRegistry entities;
    private final EntityManager entityManager;

    public OrderEntitySupport(EntityRegistry entities, EntityManager entityManager) {
        this.entities = entities;
        this.entityManager = entityManager;
    }

    public Object require(String entityName, Map<String, ?> keys) {
        Class<?> type = entities.entityClass(entityName);
        Object value = entities.findByPrimaryKey(type, keys);
        if (value == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, entityName + " が見つかりません");
        }
        return value;
    }

    public List<?> find(String entityName, Map<String, ?> filters) {
        return entities.findByEquality(entities.entityClass(entityName), filters);
    }

    public Object create(String entityName, Map<String, ?> values) {
        Class<?> type = entities.entityClass(entityName);
        Object value = entities.newEntity(type);
        values.forEach((name, fieldValue) -> setIfWritable(value, name, fieldValue));
        entityManager.persist(value);
        return value;
    }

    public Object get(Object entity, String field) {
        return entities.get(entity, field);
    }

    public void set(Object entity, String field, Object value) {
        setIfWritable(entity, field, value);
    }

    public Map<String, Object> toMap(Object entity) {
        return entities.toMap(entity);
    }

    public void remove(Object entity) {
        entityManager.remove(entity);
    }

    public boolean hasField(Object entity, String field) {
        return new BeanWrapperImpl(entity).isWritableProperty(field);
    }

    public void setIfWritable(Object entity, String field, Object value) {
        if (value != null && hasField(entity, field)) {
            entities.set(entity, field, value);
        }
    }

}
