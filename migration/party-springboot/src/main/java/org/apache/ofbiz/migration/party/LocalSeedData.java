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
package org.apache.ofbiz.migration.party;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jakarta.persistence.EntityManager;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile({"local", "test"})
public class LocalSeedData implements CommandLineRunner {
    private final EntityManager entityManager;
    private final EntityRegistry entities;

    public LocalSeedData(EntityManager entityManager, EntityRegistry entities) {
        this.entityManager = entityManager;
        this.entities = entities;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seed("PartyType", "partyTypeId", List.of("PERSON", "PARTY_GROUP"));
        seed("RoleType", "roleTypeId", List.of(
                "_NA_", "CUSTOMER", "SUPPLIER", "EMPLOYEE", "CONTACT", "ACCOUNT", "INTERNAL_ORGANIZATIO"));
        seed("PartyRelationshipType", "partyRelationshipTypeId", List.of(
                "EMPLOYMENT", "CUSTOMER_REL", "CONTACT_REL", "GROUP_ROLLUP"));
        seed("ContactMechType", "contactMechTypeId", List.of(
                "POSTAL_ADDRESS", "TELECOM_NUMBER", "EMAIL_ADDRESS"));
        seed("ContactMechPurposeType", "contactMechPurposeTypeId", List.of(
                "GENERAL_LOCATION", "PRIMARY_PHONE", "PRIMARY_EMAIL", "SHIPPING_LOCATION", "BILLING_LOCATION"));
        seed("StatusType", "statusTypeId", List.of("PARTY_STATUS", "PARTY_REL_STATUS"));
        seedStatuses();
    }

    private void seed(String entityName, String keyName, List<String> identifiers) {
        Class<?> type = entities.entityClass(entityName);
        for (String identifier : identifiers) {
            Map<String, Object> key = Map.of(keyName, identifier);
            if (entities.findByPrimaryKey(type, key) != null) {
                continue;
            }
            Object entity = entities.newEntity(type);
            entities.set(entity, keyName, identifier);
            BeanWrapperImpl wrapper = new BeanWrapperImpl(entity);
            if (wrapper.isWritableProperty("description")) {
                entities.set(entity, "description", identifier.replace('_', ' '));
            }
            entityManager.persist(entity);
        }
    }

    private void seedStatuses() {
        Class<?> type = entities.entityClass("StatusItem");
        for (Status status : List.of(
                new Status("PARTY_ENABLED", "PARTY_STATUS", "Enabled", "01"),
                new Status("PARTY_DISABLED", "PARTY_STATUS", "Disabled", "99"),
                new Status("PARTYREL_CREATED", "PARTY_REL_STATUS", "Created", "01"))) {
            Map<String, Object> key = Map.of("statusId", status.id());
            if (entities.findByPrimaryKey(type, key) != null) {
                continue;
            }
            Object entity = entities.newEntity(type);
            Map<String, Object> fields = new LinkedHashMap<>();
            fields.put("statusId", status.id());
            fields.put("statusTypeId", status.type());
            fields.put("description", status.description());
            fields.put("statusCode", status.id().replace("PARTY_", "").replace("PARTYREL_", ""));
            fields.put("sequenceId", status.sequence());
            for (var field : fields.entrySet()) {
                if (new BeanWrapperImpl(entity).isWritableProperty(field.getKey())) {
                    entities.set(entity, field.getKey(), field.getValue());
                }
            }
            entityManager.persist(entity);
        }
    }

    private record Status(String id, String type, String description, String sequence) {}
}
