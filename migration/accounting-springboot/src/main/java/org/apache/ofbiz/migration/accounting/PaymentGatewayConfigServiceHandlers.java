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
package org.apache.ofbiz.migration.accounting;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentGatewayConfigServiceHandlers {
    private static final String COMPONENT = "accounting";
    private static final String ENTITY = "PaymentGatewayConfigType";
    private final EntityManager entityManager;
    private final EntityRegistry entities;
    private final ServiceHandlerRegistry registry;

    public PaymentGatewayConfigServiceHandlers(
            EntityManager entityManager, EntityRegistry entities, ServiceHandlerRegistry registry) {
        this.entityManager = entityManager;
        this.entities = entities;
        this.registry = registry;
    }

    @PostConstruct
    void registerHandlers() {
        registry.registerHandler(
                COMPONENT, "updatePaymentGatewayConfigType", "UPDATE", ENTITY, this::updatePaymentGatewayConfigType);
    }

    /**
     * Migrated from applications/accounting/minilang/payment/PaymentGatewayConfigServices.xml:24.
     */
    private Map<String, Object> updatePaymentGatewayConfigType(Map<String, Object> input) {
        Class<?> entityType = entities.entityClass(ENTITY);
        Map<String, Object> primaryKey = new LinkedHashMap<>();
        Set<String> keys = Set.copyOf(entities.primaryKeys(entityType));
        for (String key : keys) {
            Object value = input.get(key);
            if (value == null || value.toString().isBlank()) {
                throw new MigrationException(HttpStatus.BAD_REQUEST, "Missing required parameter: " + key);
            }
            primaryKey.put(key, value);
        }
        List<?> matches = entities.findByEquality(entityType, primaryKey);
        if (matches.isEmpty()) {
            throw new MigrationException(HttpStatus.NOT_FOUND, ENTITY + " not found");
        }
        Object lookedUpValue = matches.get(0);
        input.forEach((name, value) -> {
            if (value != null && !keys.contains(name) && !List.of("userLogin", "locale").contains(name)) {
                entities.set(lookedUpValue, name, value);
            }
        });
        entityManager.flush();
        return Map.of();
    }
}
