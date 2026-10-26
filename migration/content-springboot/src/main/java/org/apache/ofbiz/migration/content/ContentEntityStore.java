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
package org.apache.ofbiz.migration.content;

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
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** OFBiz の Delegator 相当の最小操作（Map ベース）を共通 EntityRegistry 上に提供する。 */
@Component
public final class ContentEntityStore {
    private final EntityManager entityManager;
    private final EntityRegistry entityRegistry;
    private final SequenceService sequenceService;

    public ContentEntityStore(EntityManager entityManager, EntityRegistry entityRegistry, SequenceService sequenceService) {
        this.entityManager = entityManager;
        this.entityRegistry = entityRegistry;
        this.sequenceService = sequenceService;
    }

    public String nextId(String sequenceName) {
        return sequenceService.nextId(sequenceName);
    }

    /** OFBiz の make-next-seq-id 相当（親キーごとの 5 桁連番）。 */
    public String nextSubSequence(String entityName, Map<String, ?> parentKeys, String sequenceField) {
        return sequenceService.nextSubSequence(entityName, parentKeys, sequenceField);
    }

    public Object findOne(String entityName, Map<String, ?> primaryKey) {
        return entityRegistry.findByPrimaryKey(entityRegistry.entityClass(entityName), primaryKey);
    }

    public Object requireOne(String entityName, Map<String, ?> primaryKey, String message) {
        Object entity = findOne(entityName, primaryKey);
        if (entity == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, message);
        }
        return entity;
    }

    public Map<String, Object> findOneMap(String entityName, Map<String, ?> primaryKey) {
        Object entity = findOne(entityName, primaryKey);
        return entity == null ? null : entityRegistry.toMap(entity);
    }

    public List<Object> findList(String entityName, Map<String, ?> filters) {
        return new ArrayList<Object>(entityRegistry.findByEquality(entityRegistry.entityClass(entityName), filters));
    }

    public Map<String, Object> create(String entityName, Map<String, ?> values) {
        Object entity = entityRegistry.newEntity(entityRegistry.entityClass(entityName));
        applyFields(entity, values);
        if (entityRegistry.findByPrimaryKey(entity.getClass(), entityRegistry.toMap(entity)) != null) {
            throw new MigrationException(HttpStatus.CONFLICT, "ServiceValueFound");
        }
        entityManager.persist(entity);
        return entityRegistry.toMap(entity);
    }

    public Map<String, Object> store(Object entity, Map<String, ?> values) {
        List<String> keys = entityRegistry.primaryKeys(entity.getClass());
        Map<String, Object> nonPk = new LinkedHashMap<>(values);
        keys.forEach(nonPk::remove);
        applyFields(entity, nonPk);
        return entityRegistry.toMap(entity);
    }

    public Object get(Object entity, String field) {
        return entityRegistry.get(entity, field);
    }

    public Map<String, Object> toMap(Object entity) {
        return entityRegistry.toMap(entity);
    }

    public void requireSensitiveView(String entityName) {
        entityRegistry.requireSensitiveView(entityRegistry.entityClass(entityName));
    }

    /** 対象 Entity に存在するフィールドだけを設定する（OFBiz の setNonPKFields / setPKFields 相当）。 */
    public void applyFields(Object entity, Map<String, ?> values) {
        BeanWrapperImpl wrapper = new BeanWrapperImpl(entity);
        for (var entry : values.entrySet()) {
            if (entry.getValue() != null && wrapper.isWritableProperty(entry.getKey())) {
                entityRegistry.set(entity, entry.getKey(), entry.getValue());
            }
        }
    }

    /** 指定 Entity に存在するフィールドだけを残した Map を返す。 */
    public Map<String, Object> filterFields(String entityName, Map<String, ?> values) {
        BeanWrapperImpl wrapper = new BeanWrapperImpl(entityRegistry.newEntity(entityRegistry.entityClass(entityName)));
        Map<String, Object> filtered = new LinkedHashMap<>();
        values.forEach((key, value) -> {
            if (value != null && wrapper.isWritableProperty(key)) {
                filtered.put(key, value);
            }
        });
        return filtered;
    }

    public static Timestamp now() {
        return Timestamp.from(Instant.now());
    }

    public static String currentUserLoginId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? "system" : authentication.getName();
    }

    public static boolean isEmpty(Object value) {
        return value == null || value.toString().isBlank();
    }
}
