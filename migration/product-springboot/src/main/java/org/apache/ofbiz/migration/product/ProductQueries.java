/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.ofbiz.migration.product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.MigrationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ProductQueries {
    private final EntityManager entityManager;

    public ProductQueries(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public <T> List<T> findAll(Class<T> type, Map<String, ?> filters) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(type);
        Root<T> root = query.from(type);
        List<Predicate> predicates = new ArrayList<>();
        for (var filter : filters.entrySet()) {
            Class<?> fieldType;
            try {
                fieldType = entityManager.getMetamodel().entity(type).getAttribute(filter.getKey()).getJavaType();
            } catch (IllegalArgumentException exception) {
                throw new MigrationException(HttpStatus.BAD_REQUEST, "Unknown field: " + filter.getKey());
            }
            predicates.add(builder.equal(root.get(filter.getKey()), convert(filter.getValue(), fieldType)));
        }
        query.select(root).where(predicates.toArray(Predicate[]::new));
        return new ArrayList<>(entityManager.createQuery(query).getResultList());
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
            try {
                return Timestamp.valueOf(text);
            } catch (IllegalArgumentException ignored) {
                return Timestamp.from(Instant.parse(text));
            }
        }
        return value;
    }
}
