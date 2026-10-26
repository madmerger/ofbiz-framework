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
package org.apache.ofbiz.migration.common;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class EntityRegistry {
    private static final Set<String> SECRET_FIELDS = Set.of(
            "currentpassword", "passwordhint", "cardnumber", "accountnumber", "pinnumber");
    private final EntityManager entityManager;
    private final MigrationSecurityProperties properties;

    public EntityRegistry(EntityManager entityManager, MigrationSecurityProperties properties) {
        this.entityManager = entityManager;
        this.properties = properties;
    }

    public Class<?> entityClass(String name) {
        return entityManager.getMetamodel().getEntities().stream()
                .map(EntityType::getJavaType)
                .filter(type -> type.getSimpleName().equals(name))
                .findFirst().orElseThrow(() -> new MigrationException(HttpStatus.NOT_FOUND, "Unknown entity: " + name));
    }

    public List<String> primaryKeys(Class<?> type) {
        return entityManager.getMetamodel().entity(type).getSingularAttributes().stream()
                .filter(SingularAttribute::isId).map(SingularAttribute::getName).toList();
    }

    public Object newEntity(Class<?> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Cannot construct " + type.getSimpleName());
        }
    }

    public <T> T findByPrimaryKey(Class<T> type, Map<String, ?> values) {
        List<String> keys = primaryKeys(type);
        if (keys.isEmpty() || keys.stream().anyMatch(key -> values.get(key) == null)) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Missing primary key for " + type.getSimpleName());
        }
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(type);
        Root<T> root = query.from(type);
        List<Predicate> predicates = new ArrayList<>();
        for (String key : keys) {
            predicates.add(builder.equal(root.get(key), convert(values.get(key), propertyType(type, key))));
        }
        query.select(root).where(predicates.toArray(Predicate[]::new));
        List<T> found = entityManager.createQuery(query).setMaxResults(1).getResultList();
        return found.isEmpty() ? null : found.get(0);
    }

    public <T> List<T> findByEquality(Class<T> type, Map<String, ?> filters) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(type);
        Root<T> root = query.from(type);
        List<Predicate> predicates = new ArrayList<>();
        for (var filter : filters.entrySet()) {
            if (entityManager.getMetamodel().entity(type).getAttribute(filter.getKey()) == null) {
                throw new MigrationException(HttpStatus.BAD_REQUEST, "Unknown field: " + filter.getKey());
            }
            predicates.add(builder.equal(
                    root.get(filter.getKey()), convert(filter.getValue(), propertyType(type, filter.getKey()))));
        }
        query.select(root).where(predicates.toArray(Predicate[]::new));
        return new ArrayList<>(entityManager.createQuery(query).setMaxResults(100).getResultList());
    }

    public void requireSensitiveView(Class<?> type) {
        if (!properties.isSensitiveEntity(type.getSimpleName())) {
            return;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Set<String> authorities = auth == null ? Set.of() : AuthorityUtils.authorityListToSet(auth.getAuthorities());
        String prefix = properties.getServicePermissionPrefix();
        if (!authorities.contains(prefix + "_SENSITIVE_VIEW") && !authorities.contains(prefix + "_ADMIN")) {
            throw new MigrationException(HttpStatus.FORBIDDEN, "Sensitive entity access denied");
        }
    }

    public Object get(Object entity, String name) {
        return new BeanWrapperImpl(entity).getPropertyValue(name);
    }

    public void set(Object entity, String name, Object value) {
        BeanWrapper wrapper = new BeanWrapperImpl(entity);
        if (!wrapper.isWritableProperty(name)) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Unknown field: " + name);
        }
        wrapper.setPropertyValue(name, convert(value, wrapper.getPropertyType(name)));
    }

    public Map<String, Object> toMap(Object entity) {
        BeanWrapper wrapper = new BeanWrapperImpl(entity);
        Map<String, Object> result = new LinkedHashMap<>();
        for (var descriptor : wrapper.getPropertyDescriptors()) {
            String name = descriptor.getName();
            if (descriptor.getReadMethod() == null || name.equals("class")) {
                continue;
            }
            Object value = wrapper.getPropertyValue(name);
            result.put(name, isSecret(name) ? (value == null ? null : "[REDACTED]") : value);
        }
        return result;
    }

    private static boolean isSecret(String name) {
        String lower = name.toLowerCase();
        return SECRET_FIELDS.contains(lower) || lower.contains("securitycode")
                || lower.endsWith("password") || lower.endsWith("passwordhint");
    }

    private Class<?> propertyType(Class<?> entityClass, String property) {
        return new BeanWrapperImpl(newEntity(entityClass)).getPropertyType(property);
    }

    private static Object convert(Object value, Class<?> target) {
        if (value == null || target.isInstance(value)) {
            return value;
        }
        String text = value.toString();
        if (target == String.class) return text;
        if (target == Long.class || target == long.class) return Long.valueOf(text);
        if (target == Integer.class || target == int.class) return Integer.valueOf(text);
        if (target == Double.class || target == double.class) return Double.valueOf(text);
        if (target == BigDecimal.class) return new BigDecimal(text);
        if (target == Date.class) return Date.valueOf(LocalDate.parse(text));
        if (target == Time.class) return Time.valueOf(LocalTime.parse(text));
        if (target == Timestamp.class) {
            try { return Timestamp.valueOf(text); }
            catch (IllegalArgumentException ignored) { return Timestamp.from(Instant.parse(text)); }
        }
        return value;
    }
}
