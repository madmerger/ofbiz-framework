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
package org.apache.ofbiz.migration.marketing;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Small JPA helper shared by the hand-ported marketing services.
 */
@Component
public class MarketingData {

    private final EntityManager entityManager;
    private final EntityRegistry entityRegistry;

    public MarketingData(EntityManager entityManager, EntityRegistry entityRegistry) {
        this.entityManager = entityManager;
        this.entityRegistry = entityRegistry;
    }

    public EntityManager em() {
        return entityManager;
    }

    public EntityRegistry registry() {
        return entityRegistry;
    }

    public <T> Optional<T> one(Class<T> type, Map<String, ?> keys) {
        return Optional.ofNullable(entityRegistry.findByPrimaryKey(type, keys));
    }

    public <T> T require(Class<T> type, Map<String, ?> keys) {
        return one(type, keys).orElseThrow(() -> new MigrationException(
                HttpStatus.NOT_FOUND, type.getSimpleName() + " not found"));
    }

    public <T> List<T> query(Class<T> type, String jpql, Object... parameters) {
        TypedQuery<T> query = entityManager.createQuery(jpql, type);
        for (int index = 0; index < parameters.length; index++) {
            query.setParameter(index + 1, parameters[index]);
        }
        return query.getResultList();
    }

    public <T> Optional<T> first(Class<T> type, String jpql, Object... parameters) {
        List<T> found = query(type, jpql, parameters);
        return found.isEmpty() ? Optional.empty() : Optional.of(found.get(0));
    }

    public void persist(Object entity) {
        entityManager.persist(entity);
    }

    public void remove(Object entity) {
        entityManager.remove(entity);
    }

    public void removeAll(Collection<?> entities) {
        entities.forEach(entityManager::remove);
    }

    public Map<String, Object> toMap(Object entity) {
        return entityRegistry.toMap(entity);
    }

    /** Copies the writable fields of {@code input} except {@code excluded} onto {@code entity}. */
    public void copyFields(Object entity, Map<String, ?> input, Collection<String> excluded) {
        BeanWrapper wrapper = new BeanWrapperImpl(entity);
        for (var entry : input.entrySet()) {
            String name = entry.getKey();
            if (excluded.contains(name) || !wrapper.isWritableProperty(name) || isStamp(name)) {
                continue;
            }
            entityRegistry.set(entity, name, entry.getValue());
        }
    }

    public List<String> primaryKeys(Class<?> type) {
        return entityRegistry.primaryKeys(type);
    }

    public static boolean isStamp(String name) {
        return name.endsWith("Stamp") && (name.startsWith("created") || name.startsWith("lastUpdated"));
    }

    public static Timestamp now() {
        return Timestamp.from(Instant.now().truncatedTo(ChronoUnit.MILLIS));
    }

    public static String text(Map<String, ?> input, String name) {
        Object value = input.get(name);
        if (value == null) {
            return null;
        }
        String text = value.toString();
        return text.isBlank() ? null : text;
    }

    public static String requireText(Map<String, ?> input, String name) {
        String value = text(input, name);
        if (value == null) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Missing required input: " + name);
        }
        return value;
    }

    public static Timestamp timestamp(Map<String, ?> input, String name) {
        Object value = input.get(name);
        if (value == null || value.toString().isBlank()) {
            return null;
        }
        if (value instanceof Timestamp stamp) {
            return stamp;
        }
        String text = value.toString();
        try {
            return Timestamp.valueOf(text);
        } catch (IllegalArgumentException notJdbc) {
            try {
                return Timestamp.from(OffsetDateTime.parse(text).toInstant());
            } catch (RuntimeException notOffset) {
                try {
                    return Timestamp.from(Instant.parse(text));
                } catch (RuntimeException invalid) {
                    throw new MigrationException(HttpStatus.BAD_REQUEST, "Invalid timestamp: " + name);
                }
            }
        }
    }

    public static String currentUserLoginId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? "system" : authentication.getName();
    }

    public static boolean isEmail(String value) {
        return value != null && value.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }

    public static boolean active(Timestamp fromDate, Timestamp thruDate, Timestamp moment) {
        return (fromDate == null || !fromDate.after(moment)) && (thruDate == null || thruDate.after(moment));
    }
}
