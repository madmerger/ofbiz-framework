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
package org.apache.ofbiz.migration.party.adapter.local;

import java.util.LinkedHashMap;
import java.util.Map;
import jakarta.persistence.EntityManager;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.party.UserLoginPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class LocalUserLoginAdapter implements UserLoginPort {
    private final EntityManager entityManager;
    private final EntityRegistry entities;
    private final PasswordEncoder passwordEncoder;

    public LocalUserLoginAdapter(EntityManager entityManager, EntityRegistry entities, PasswordEncoder passwordEncoder) {
        this.entityManager = entityManager;
        this.entities = entities;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Map<String, Object> createUserLogin(Map<String, ?> input, String partyId) {
        String userLoginId = text(input, "userLoginId");
        String password = text(input, "currentPassword");
        String verify = text(input, "currentPasswordVerify");
        if (userLoginId == null || userLoginId.isBlank()) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Missing required input: userLoginId");
        }
        if (password == null || !password.equals(verify)) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "currentPassword and currentPasswordVerify must match");
        }
        if (password.length() < 5) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Password must be at least 5 characters");
        }
        if (entities.findByPrimaryKey(entities.entityClass("UserLogin"), Map.of("userLoginId", userLoginId)) != null) {
            throw new MigrationException(HttpStatus.CONFLICT, "PARTY_DUPLICATE: userLoginId");
        }
        Object login = entities.newEntity(entities.entityClass("UserLogin"));
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("userLoginId", userLoginId);
        fields.put("currentPassword", passwordEncoder.encode(password));
        fields.put("passwordHint", input.get("passwordHint"));
        fields.put("requirePasswordChange", input.get("requirePasswordChange"));
        fields.put("partyId", partyId == null ? input.get("partyId") : partyId);
        for (var field : fields.entrySet()) {
            if (field.getValue() != null) {
                entities.set(login, field.getKey(), field.getValue());
            }
        }
        entityManager.persist(login);
        entityManager.flush();
        return entities.toMap(login);
    }

    private static String text(Map<String, ?> input, String name) {
        Object value = input.get(name);
        return value == null ? null : value.toString();
    }
}
