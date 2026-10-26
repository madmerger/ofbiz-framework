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
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.apache.ofbiz.migration.product.simple;

import jakarta.persistence.EntityManager;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.SequenceService;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** Party service ports used by FacilityContactMechServices. */
@Component
final class FacilityContactMechServicesPartyContact {
    private static final Set<String> STAMP_FIELDS = Set.of(
            "lastUpdatedStamp", "lastUpdatedTxStamp", "createdStamp", "createdTxStamp");
    private static final Set<String> CONTACT_MECH_FIELDS =
            Set.of("contactMechId", "contactMechTypeId", "infoString");
    private final EntityManager entityManager;
    private final EntityRegistry entities;
    private final SequenceService sequences;

    FacilityContactMechServicesPartyContact(
            EntityManager entityManager, EntityRegistry entities, SequenceService sequences) {
        this.entityManager = entityManager;
        this.entities = entities;
        this.sequences = sequences;
    }

    Map<String, Object> execute(String serviceName, Map<String, ?> input) {
        Map<String, Object> parameters = serviceFields(serviceName, input);
        return switch (serviceName) {
            case "createContactMech" -> createContactMech(parameters);
            case "updateContactMech" -> updateContactMech(parameters);
            case "createPostalAddress" -> createPostalAddress(parameters);
            case "updatePostalAddress" -> updatePostalAddress(parameters);
            case "createTelecomNumber" -> createTelecomNumber(parameters);
            case "updateTelecomNumber" -> updateTelecomNumber(parameters);
            case "createGeoPoint" -> createGeoPoint(parameters);
            default -> throw new MigrationException(HttpStatus.NOT_FOUND, "Unknown party service: " + serviceName);
        };
    }

    Map<String, Object> serviceFields(String serviceName, Map<String, ?> source) {
        Set<String> allowed = switch (serviceName) {
            case "createContactMech", "updateContactMech" -> CONTACT_MECH_FIELDS;
            case "createPostalAddress" -> entityFields("PostalAddress", true);
            case "updatePostalAddress" -> union(
                    entityFields("PostalAddress", false),
                    Set.of("contactMechId", "partyId", "latitude", "longitude"));
            case "createTelecomNumber" -> entityFields("TelecomNumber", true);
            case "updateTelecomNumber" -> union(
                    entityFields("TelecomNumber", false), Set.of("contactMechId"));
            case "createGeoPoint" -> entityFields("GeoPoint", false);
            default -> throw new MigrationException(HttpStatus.NOT_FOUND, "Unknown party service: " + serviceName);
        };
        Map<String, Object> result = new LinkedHashMap<>();
        source.forEach((name, value) -> {
            if (allowed.contains(name)) {
                result.put(name, value);
            }
        });
        return result;
    }

    /** Port of applications/party/servicedef/services_contact.xml:28-33. */
    private Map<String, Object> createContactMech(Map<String, Object> parameters) {
        requireInputs(parameters, "contactMechTypeId");
        Object contactMech = entities.newEntity(entities.entityClass("ContactMech"));
        setEntityFields(contactMech, parameters);
        Object contactMechId = entities.get(contactMech, "contactMechId");
        if (isEmpty(contactMechId)) {
            contactMechId = sequences.nextId("ContactMech");
            entities.set(contactMech, "contactMechId", contactMechId);
        }
        requirePrimaryKeys(contactMech);
        if (entities.findByPrimaryKey(entities.entityClass("ContactMech"), Map.of("contactMechId", contactMechId))
                != null) {
            throw badRequest("ServiceValueFound");
        }
        entityManager.persist(contactMech);
        return result("contactMechId", contactMechId);
    }

    /** Port of applications/party/servicedef/services_contact.xml:34-39 and ContactMechServicesScript.groovy:29-55. */
    private Map<String, Object> updateContactMech(Map<String, Object> parameters) {
        requireInputs(parameters, "contactMechId");
        Class<?> type = entities.entityClass("ContactMech");
        Object lookedValue = entities.findByPrimaryKey(type, parameters);
        if (lookedValue == null) {
            throw badRequest("Value not found, cannot update");
        }

        Object contactMechId = parameters.get("contactMechId");
        if (!Objects.equals(entities.get(lookedValue, "infoString"), parameters.get("infoString"))) {
            Object newValue = cloneValue(lookedValue);
            setNonPKFields(newValue, parameters);
            entities.set(newValue, "contactMechId", null);
            Map<String, Object> createParameters =
                    serviceFields("createContactMech", entities.toMap(newValue));
            contactMechId = execute("createContactMech", createParameters).get("contactMechId");
        }
        return result("contactMechId", contactMechId);
    }

    /** Port of applications/party/servicedef/services_contact.xml:41-49 and ContactMechServicesScript.groovy:76-88. */
    private Map<String, Object> createPostalAddress(Map<String, Object> parameters) {
        requireInputs(parameters, "address1", "city", "postalCode");
        validateStateProvince(parameters);

        Object postalAddress = entities.newEntity(entities.entityClass("PostalAddress"));
        setEntityFields(postalAddress, parameters);
        Map<String, Object> createContactMechParameters = new LinkedHashMap<>();
        createContactMechParameters.put("contactMechTypeId", "POSTAL_ADDRESS");
        createContactMechParameters.put("contactMechId", parameters.get("contactMechId"));
        Object contactMechId = execute("createContactMech", createContactMechParameters).get("contactMechId");
        entities.set(postalAddress, "contactMechId", contactMechId);
        requirePrimaryKeys(postalAddress);
        entityManager.persist(postalAddress);
        return result("contactMechId", contactMechId);
    }

    /** Port of applications/party/servicedef/services_contact.xml:50-63 and ContactMechServicesScript.groovy:93-126. */
    private Map<String, Object> updatePostalAddress(Map<String, Object> parameters) {
        requireInputs(parameters, "contactMechId", "address1", "city", "postalCode");
        validateStateProvince(parameters);
        Class<?> type = entities.entityClass("PostalAddress");
        Object lookedValue = entities.findByPrimaryKey(type, parameters);
        if (lookedValue == null) {
            throw badRequest("Value not found, cannot update");
        }

        Object newValue = cloneValue(lookedValue);
        setNonPKFields(newValue, parameters);
        Object oldContactMechId = entities.get(lookedValue, "contactMechId");
        Object contactMechId;
        if (hasEntityChanges(lookedValue, newValue)) {
            Map<String, Object> createParameters = new LinkedHashMap<>(parameters);
            createParameters.put("contactMechId", null);
            contactMechId = execute(
                            "createPostalAddress",
                            serviceFields("createPostalAddress", createParameters))
                    .get("contactMechId");
        } else {
            contactMechId = execute(
                            "updateContactMech",
                            serviceFields("updateContactMech", parameters))
                    .get("contactMechId");
            if (!Objects.equals(contactMechId, oldContactMechId)) {
                entities.set(newValue, "contactMechId", contactMechId);
                requirePrimaryKeys(newValue);
                entityManager.persist(newValue);
            }
        }
        Map<String, Object> result = result("contactMechId", contactMechId);
        result.put("oldContactMechId", oldContactMechId);
        return result;
    }

    /** Port of applications/party/servicedef/services_contact.xml:65-70 and ContactMechServicesScript.groovy:131-140. */
    private Map<String, Object> createTelecomNumber(Map<String, Object> parameters) {
        Object telecomNumber = entities.newEntity(entities.entityClass("TelecomNumber"));
        setEntityFields(telecomNumber, parameters);
        Map<String, Object> createContactMechParameters = new LinkedHashMap<>();
        createContactMechParameters.put("contactMechTypeId", "TELECOM_NUMBER");
        createContactMechParameters.put("contactMechId", parameters.get("contactMechId"));
        Object contactMechId = execute("createContactMech", createContactMechParameters).get("contactMechId");
        entities.set(telecomNumber, "contactMechId", contactMechId);
        requirePrimaryKeys(telecomNumber);
        entityManager.persist(telecomNumber);
        return result("contactMechId", contactMechId);
    }

    /** Port of applications/party/servicedef/services_contact.xml:71-77 and ContactMechServicesScript.groovy:145-175. */
    private Map<String, Object> updateTelecomNumber(Map<String, Object> parameters) {
        requireInputs(parameters, "contactMechId");
        Class<?> type = entities.entityClass("TelecomNumber");
        Object lookedValue = entities.findByPrimaryKey(type, parameters);
        if (lookedValue == null) {
            throw badRequest("Value not found, cannot update");
        }

        Object oldContactMechId = entities.get(lookedValue, "contactMechId");
        Object newValue = cloneValue(lookedValue);
        setNonPKFields(newValue, parameters);
        Object contactMechId;
        if (hasEntityChanges(lookedValue, newValue)) {
            Map<String, Object> createParameters = new LinkedHashMap<>(parameters);
            createParameters.put("contactMechId", null);
            contactMechId = execute(
                            "createTelecomNumber",
                            serviceFields("createTelecomNumber", createParameters))
                    .get("contactMechId");
        } else {
            contactMechId = execute(
                            "updateContactMech",
                            serviceFields("updateContactMech", parameters))
                    .get("contactMechId");
            if (!Objects.equals(contactMechId, oldContactMechId)) {
                entities.set(newValue, "contactMechId", contactMechId);
                requirePrimaryKeys(newValue);
                entityManager.persist(newValue);
            }
        }
        Map<String, Object> result = result("contactMechId", contactMechId);
        result.put("oldContactMechId", oldContactMechId);
        return result;
    }

    /** Port of framework/common/servicedef/services.xml:831-839. */
    private Map<String, Object> createGeoPoint(Map<String, Object> parameters) {
        requireInputs(parameters, "dataSourceId", "latitude", "longitude");
        Object geoPoint = entities.newEntity(entities.entityClass("GeoPoint"));
        setEntityFields(geoPoint, parameters);
        entities.set(geoPoint, "geoPointId", sequences.nextId("GeoPoint"));
        requirePrimaryKeys(geoPoint);
        entityManager.persist(geoPoint);
        return result("geoPointId", entities.get(geoPoint, "geoPointId"));
    }

    /** Port of ContactMechServicesScript.groovy:60-71. */
    private void validateStateProvince(Map<String, Object> parameters) {
        Object stateProvinceGeoId = parameters.get("stateProvinceGeoId");
        if (!isEmpty(stateProvinceGeoId)) {
            return;
        }
        Object countryGeoId = parameters.get("countryGeoId");
        if ("USA".equals(countryGeoId)) {
            throw badRequest("State is missing, and is required for an address in the United States.");
        }
        if ("CAN".equals(countryGeoId)) {
            throw badRequest("Province is missing, and is required for an address in Canada.");
        }
    }

    private Set<String> entityFields(String entityName, boolean includePrimaryKeys) {
        Class<?> type = entities.entityClass(entityName);
        Set<String> primaryKeys = Set.copyOf(entities.primaryKeys(type));
        BeanWrapper wrapper = new BeanWrapperImpl(entities.newEntity(type));
        Set<String> result = new LinkedHashSet<>();
        for (var descriptor : wrapper.getPropertyDescriptors()) {
            String name = descriptor.getName();
            if (!name.equals("class") && !STAMP_FIELDS.contains(name)
                    && descriptor.getWriteMethod() != null
                    && (includePrimaryKeys || !primaryKeys.contains(name))) {
                result.add(name);
            }
        }
        return result;
    }

    private static Set<String> union(Set<String> first, Set<String> second) {
        Set<String> result = new LinkedHashSet<>(first);
        result.addAll(second);
        return result;
    }

    private void setEntityFields(Object entity, Map<String, ?> values) {
        BeanWrapperImpl wrapper = new BeanWrapperImpl(entity);
        for (var descriptor : wrapper.getPropertyDescriptors()) {
            String name = descriptor.getName();
            if (!name.equals("class") && !STAMP_FIELDS.contains(name)
                    && descriptor.getWriteMethod() != null && values.containsKey(name)) {
                entities.set(entity, name, values.get(name));
            }
        }
    }

    private void setNonPKFields(Object entity, Map<String, ?> values) {
        Set<String> primaryKeys = Set.copyOf(entities.primaryKeys(entity.getClass()));
        BeanWrapperImpl wrapper = new BeanWrapperImpl(entity);
        for (var descriptor : wrapper.getPropertyDescriptors()) {
            String name = descriptor.getName();
            if (!name.equals("class") && !primaryKeys.contains(name) && !STAMP_FIELDS.contains(name)
                    && descriptor.getWriteMethod() != null && values.containsKey(name)) {
                entities.set(entity, name, values.get(name));
            }
        }
    }

    private Object cloneValue(Object source) {
        Object clone = entities.newEntity(source.getClass());
        BeanWrapper sourceWrapper = new BeanWrapperImpl(source);
        BeanWrapper cloneWrapper = new BeanWrapperImpl(clone);
        for (var descriptor : sourceWrapper.getPropertyDescriptors()) {
            String name = descriptor.getName();
            if (!name.equals("class") && descriptor.getReadMethod() != null
                    && cloneWrapper.isWritableProperty(name)) {
                cloneWrapper.setPropertyValue(name, sourceWrapper.getPropertyValue(name));
            }
        }
        return clone;
    }

    private boolean hasEntityChanges(Object original, Object updated) {
        BeanWrapper originalWrapper = new BeanWrapperImpl(original);
        BeanWrapper updatedWrapper = new BeanWrapperImpl(updated);
        for (var descriptor : originalWrapper.getPropertyDescriptors()) {
            String name = descriptor.getName();
            if (!name.equals("class") && !STAMP_FIELDS.contains(name) && descriptor.getReadMethod() != null
                    && !Objects.equals(
                            originalWrapper.getPropertyValue(name), updatedWrapper.getPropertyValue(name))) {
                return true;
            }
        }
        return false;
    }

    private void requirePrimaryKeys(Object entity) {
        for (String name : entities.primaryKeys(entity.getClass())) {
            Object value = entities.get(entity, name);
            if (isEmpty(value)) {
                throw badRequest("Missing primary key: " + name);
            }
        }
    }

    private static void requireInputs(Map<String, ?> parameters, String... names) {
        for (String name : names) {
            if (isEmpty(parameters.get(name))) {
                throw badRequest("Missing required service input parameter: " + name);
            }
        }
    }

    private static boolean isEmpty(Object value) {
        return value == null || value instanceof String text && text.isBlank();
    }

    private static Map<String, Object> result(String name, Object value) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(name, value);
        return result;
    }

    private static MigrationException badRequest(String message) {
        return new MigrationException(HttpStatus.BAD_REQUEST, message);
    }
}
