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
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.product.ProductQueries;
import org.apache.ofbiz.migration.product.generated.ProductServiceMetadata;
import org.apache.ofbiz.migration.product.adapter.ProductUnportedPort;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Port of applications/product/minilang/product/storage/FacilityContactMechServices.xml:23-327.
 * FacilityContactMechMapProcs.xml is not referenced, nothing to port; log, field-to-request, and
 * default-message are no-ops.
 */
@Component
public class FacilityContactMechServices extends UnportedSimpleMethodFile {
    private static final Set<String> STAMP_FIELDS = Set.of(
            "lastUpdatedStamp", "lastUpdatedTxStamp", "createdStamp", "createdTxStamp");
    private static final Pattern EMAIL_LOCAL_PART = Pattern.compile(
            "[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*");
    private final EntityManager entityManager;
    private final EntityRegistry entities;
    private final ProductQueries queries;
    private final MigrationServiceDispatcher dispatcher;
    private final FacilityContactMechServicesPartyContact partyContact;

    public FacilityContactMechServices(
            ProductUnportedPort unportedPort,
            EntityManager entityManager,
            EntityRegistry entities,
            ProductQueries queries,
            @Lazy MigrationServiceDispatcher dispatcher,
            FacilityContactMechServicesPartyContact partyContact) {
        super(unportedPort);
        this.entityManager = entityManager;
        this.entities = entities;
        this.queries = queries;
        this.dispatcher = dispatcher;
        this.partyContact = partyContact;
    }

    @Override
    public Map<String, Object> execute(String serviceName, String methodName, Map<String, Object> input) {
        return switch (serviceName) {
            case "createFacilityContactMech" ->
                createFacilityContactMech(inParams(serviceName, input));
            case "updateFacilityContactMech" ->
                updateFacilityContactMech(inParams(serviceName, input));
            case "deleteFacilityContactMech" ->
                deleteFacilityContactMech(inParams(serviceName, input));
            case "createFacilityPostalAddress" ->
                createFacilityPostalAddress(inParams(serviceName, input));
            case "updateFacilityPostalAddress" ->
                updateFacilityPostalAddress(inParams(serviceName, input));
            case "createFacilityTelecomNumber" ->
                createFacilityTelecomNumber(inParams(serviceName, input));
            case "updateFacilityTelecomNumber" ->
                updateFacilityTelecomNumber(inParams(serviceName, input));
            case "createFacilityEmailAddress" ->
                createFacilityEmailAddress(inParams(serviceName, input));
            case "updateFacilityEmailAddress" ->
                updateFacilityEmailAddress(inParams(serviceName, input));
            case "createFacilityContactMechPurpose" ->
                createFacilityContactMechPurpose(inParams(serviceName, input));
            case "deleteFacilityContactMechPurpose" ->
                deleteFacilityContactMechPurpose(inParams(serviceName, input));
            default -> super.execute(serviceName, methodName, input);
        };
    }

    /** Port of applications/product/minilang/product/storage/FacilityContactMechServices.xml:23-56. */
    private Map<String, Object> createFacilityContactMech(Map<String, Object> parameters) {
        Object contactMechId = parameters.get("contactMechId");
        if (isEmpty(contactMechId)) {
            Map<String, Object> contactMechParameters = partyContact.serviceFields("createContactMech", parameters);
            contactMechParameters.put("contactMechTypeId", parameters.get("contactMechTypeId"));
            contactMechId = partyContact.execute("createContactMech", contactMechParameters).get("contactMechId");
        }

        Timestamp now = now();
        Object facilityContactMech = newEntity("FacilityContactMech");
        if (parameters.containsKey("facilityId")) {
            entities.set(facilityContactMech, "facilityId", parameters.get("facilityId"));
        }
        entities.set(facilityContactMech, "contactMechId", contactMechId);
        setNonPKFields(facilityContactMech, parameters);
        entities.set(facilityContactMech, "fromDate", now);
        requirePrimaryKeys(facilityContactMech);
        entityManager.persist(facilityContactMech);

        Object purposeTypeId = parameters.get("contactMechPurposeTypeId");
        if (!isEmpty(purposeTypeId)) {
            Object purpose = newEntity("FacilityContactMechPurpose");
            copyPrimaryKeys(facilityContactMech, purpose);
            entities.set(purpose, "contactMechPurposeTypeId", purposeTypeId);
            requirePrimaryKeys(purpose);
            entityManager.persist(purpose);
        }
        return result("contactMechId", contactMechId);
    }

    /** Port of applications/product/minilang/product/storage/FacilityContactMechServices.xml:58-124. */
    private Map<String, Object> updateFacilityContactMech(Map<String, Object> parameters) {
        requireInputs(parameters, "facilityId", "contactMechId");
        Timestamp now = now();
        Class<?> type = entities.entityClass("FacilityContactMech");
        List<?> activeRows = findByEquality(type, Map.of(
                "facilityId", parameters.get("facilityId"),
                "contactMechId", parameters.get("contactMechId"))).stream()
                .filter(row -> isActive(row, now))
                .toList();
        if (activeRows.isEmpty()) {
            throw badRequest("ERROR: Cannot update specified contact info because it does not correspond to the specified party.");
        }

        Object oldValue = activeRows.get(0);
        Object newValue = cloneValue(oldValue);
        Object oldContactMechId = entities.get(oldValue, "contactMechId");
        Object newContactMechId = parameters.get("newContactMechId");
        if (isEmpty(newContactMechId)) {
            Map<String, Object> updateParameters = partyContact.serviceFields("updateContactMech", parameters);
            updateParameters.put("contactMechTypeId", parameters.get("contactMechTypeId"));
            newContactMechId = partyContact.execute("updateContactMech", updateParameters).get("contactMechId");
        }
        entities.set(newValue, "contactMechId", newContactMechId);
        setNonPKFields(newValue, parameters);
        entities.set(newValue, "fromDate", now);
        entities.set(oldValue, "thruDate", now);
        entityManager.flush();
        requirePrimaryKeys(newValue);
        entityManager.persist(newValue);

        if (!Objects.equals(oldContactMechId, newContactMechId)) {
            movePurposes(
                    entities.get(oldValue, "facilityId"),
                    oldContactMechId,
                    newContactMechId,
                    now);
        }
        return result("contactMechId", newContactMechId);
    }

    /** Port of applications/product/minilang/product/storage/FacilityContactMechServices.xml:126-143. */
    private Map<String, Object> deleteFacilityContactMech(Map<String, Object> parameters) {
        Timestamp now = now();
        Class<?> type = entities.entityClass("FacilityContactMech");
        Object activeValue = findByEquality(type, Map.of(
                        "facilityId", parameters.get("facilityId"),
                        "contactMechId", parameters.get("contactMechId")))
                .stream()
                .filter(value -> isActive(value, now))
                .findFirst()
                .orElse(null);
        if (activeValue == null) {
            return Map.of("errorMessageList",
                    List.of("ERROR: Could not delete contact info (party contact mech not found)"));
        }
        entities.set(activeValue, "thruDate", now);
        entityManager.flush();
        return Map.of();
    }

    /** Port of applications/product/minilang/product/storage/FacilityContactMechServices.xml:145-171. */
    private Map<String, Object> createFacilityPostalAddress(Map<String, Object> parameters) {
        if (!isEmpty(parameters.get("latitude")) && !isEmpty(parameters.get("longitude"))) {
            Map<String, Object> geoPoint = partyContact.execute(
                    "createGeoPoint", partyContact.serviceFields("createGeoPoint", parameters));
            parameters.put("geoPointId", geoPoint.get("geoPointId"));
        }

        Map<String, Object> postalAddress = partyContact.execute(
                "createPostalAddress", partyContact.serviceFields("createPostalAddress", parameters));
        Object contactMechId = postalAddress.get("contactMechId");
        Map<String, Object> createParameters = new LinkedHashMap<>();
        createParameters.put("contactMechId", contactMechId);
        createParameters.putAll(serviceFields("createFacilityContactMech", parameters));
        dispatchProduct("createFacilityContactMech", createParameters);
        return result("contactMechId", contactMechId);
    }

    /** Port of applications/product/minilang/product/storage/FacilityContactMechServices.xml:173-200. */
    private Map<String, Object> updateFacilityPostalAddress(Map<String, Object> parameters) {
        if (!isEmpty(parameters.get("latitude")) && !isEmpty(parameters.get("longitude"))) {
            Map<String, Object> geoPoint = partyContact.execute(
                    "createGeoPoint", partyContact.serviceFields("createGeoPoint", parameters));
            parameters.put("geoPointId", geoPoint.get("geoPointId"));
        }

        Map<String, Object> postalAddress = partyContact.execute(
                "updatePostalAddress", partyContact.serviceFields("updatePostalAddress", parameters));
        Object contactMechId = postalAddress.get("contactMechId");
        Map<String, Object> updateParameters = serviceFields("updateFacilityContactMech", parameters);
        updateParameters.put("newContactMechId", contactMechId);
        updateParameters.put("contactMechTypeId", "POSTAL_ADDRESS");
        dispatchProduct("updateFacilityContactMech", updateParameters);
        return result("contactMechId", contactMechId);
    }

    /** Port of applications/product/minilang/product/storage/FacilityContactMechServices.xml:202-219. */
    private Map<String, Object> createFacilityTelecomNumber(Map<String, Object> parameters) {
        Map<String, Object> telecomNumber = partyContact.execute(
                "createTelecomNumber", partyContact.serviceFields("createTelecomNumber", parameters));
        Object contactMechId = telecomNumber.get("contactMechId");
        Map<String, Object> createParameters = serviceFields("createFacilityContactMech", parameters);
        createParameters.put("contactMechId", contactMechId);
        dispatchProduct("createFacilityContactMech", createParameters);
        return result("contactMechId", contactMechId);
    }

    /** Port of applications/product/minilang/product/storage/FacilityContactMechServices.xml:221-241. */
    private Map<String, Object> updateFacilityTelecomNumber(Map<String, Object> parameters) {
        Map<String, Object> telecomNumber = partyContact.execute(
                "updateTelecomNumber", partyContact.serviceFields("updateTelecomNumber", parameters));
        Object contactMechId = telecomNumber.get("contactMechId");
        Map<String, Object> updateParameters = serviceFields("updateFacilityContactMech", parameters);
        updateParameters.put("newContactMechId", contactMechId);
        updateParameters.put("contactMechTypeId", "TELECOM_NUMBER");
        dispatchProduct("updateFacilityContactMech", updateParameters);
        return result("contactMechId", contactMechId);
    }

    /** Port of applications/product/minilang/product/storage/FacilityContactMechServices.xml:243-261. */
    private Map<String, Object> createFacilityEmailAddress(Map<String, Object> parameters) {
        Object emailAddress = parameters.get("emailAddress");
        if (!isEmail(emailAddress == null ? null : emailAddress.toString())) {
            throw badRequest("PartyEmailAddressNotFormattedCorrectly");
        }
        Map<String, Object> createParameters = serviceFields("createFacilityContactMech", parameters);
        createParameters.put("infoString", emailAddress);
        createParameters.put("contactMechTypeId", "EMAIL_ADDRESS");
        Map<String, Object> result = dispatchProduct("createFacilityContactMech", createParameters);
        return result("contactMechId", result.get("contactMechId"));
    }

    /** Port of applications/product/minilang/product/storage/FacilityContactMechServices.xml:263-281. */
    private Map<String, Object> updateFacilityEmailAddress(Map<String, Object> parameters) {
        Object emailAddress = parameters.get("emailAddress");
        if (!isEmail(emailAddress == null ? null : emailAddress.toString())) {
            throw badRequest("PartyEmailAddressNotFormattedCorrectly");
        }
        Map<String, Object> updateParameters = serviceFields("updateFacilityContactMech", parameters);
        updateParameters.put("infoString", emailAddress);
        updateParameters.put("contactMechTypeId", "EMAIL_ADDRESS");
        Map<String, Object> result = dispatchProduct("updateFacilityContactMech", updateParameters);
        return result("contactMechId", result.get("contactMechId"));
    }

    /** Port of applications/product/minilang/product/storage/FacilityContactMechServices.xml:283-308. */
    private Map<String, Object> createFacilityContactMechPurpose(Map<String, Object> parameters) {
        Timestamp now = now();
        Class<?> type = entities.entityClass("FacilityContactMechPurpose");
        List<?> purposes = findByEquality(type, Map.of(
                "facilityId", parameters.get("facilityId"),
                "contactMechId", parameters.get("contactMechId"),
                "contactMechPurposeTypeId", parameters.get("contactMechPurposeTypeId")));
        if (purposes.stream().anyMatch(value -> isActive(value, now))) {
            throw badRequest("Could not create new purpose, a purpose with that type already exists");
        }
        Object purpose = newEntity("FacilityContactMechPurpose");
        setEntityFields(purpose, parameters);
        entities.set(purpose, "fromDate", now);
        requirePrimaryKeys(purpose);
        entityManager.persist(purpose);
        return result("fromDate", now);
    }

    /** Port of applications/product/minilang/product/storage/FacilityContactMechServices.xml:310-327. */
    private Map<String, Object> deleteFacilityContactMechPurpose(Map<String, Object> parameters) {
        Class<?> type = entities.entityClass("FacilityContactMechPurpose");
        Object purpose = entities.findByPrimaryKey(type, parameters);
        if (purpose == null) {
            throw badRequest("Unable to locate purpose; cannot delete");
        }
        entities.set(purpose, "thruDate", now());
        entityManager.flush();
        return Map.of();
    }

    private void movePurposes(Object facilityId, Object oldContactMechId, Object newContactMechId, Timestamp now) {
        Class<?> type = entities.entityClass("FacilityContactMechPurpose");
        List<?> purposes = findByEquality(type, Map.of(
                "facilityId", facilityId,
                "contactMechId", oldContactMechId));
        for (Object oldPurpose : purposes) {
            Object newPurpose = cloneValue(oldPurpose);
            entities.set(oldPurpose, "thruDate", now);
            entityManager.flush();
            Map<String, Object> target = Map.of(
                    "facilityId", facilityId,
                    "contactMechId", newContactMechId,
                    "contactMechPurposeTypeId", entities.get(oldPurpose, "contactMechPurposeTypeId"));
            if (findByEquality(type, target).isEmpty()) {
                entities.set(newPurpose, "contactMechId", newContactMechId);
                requirePrimaryKeys(newPurpose);
                entityManager.persist(newPurpose);
            }
        }
    }

    private Map<String, Object> dispatchProduct(String serviceName, Map<String, ?> parameters) {
        return dispatcher.execute("product", serviceName, serviceFields(serviceName, parameters));
    }

    private static Map<String, Object> serviceFields(String serviceName, Map<String, ?> source) {
        return inParams(serviceName, source);
    }

    private static Map<String, Object> inParams(String serviceName, Map<String, ?> source) {
        ProductServiceMetadata.SimpleServiceMetadata metadata =
                ProductServiceMetadata.simpleServices().get(serviceName);
        if (metadata == null) {
            throw new IllegalArgumentException("Unknown product simple service: " + serviceName);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        for (ProductServiceMetadata.SimpleParam parameter : metadata.params()) {
            String mode = parameter.mode().toUpperCase(Locale.ROOT);
            if ((mode.equals("IN") || mode.equals("INOUT")) && source.containsKey(parameter.name())) {
                result.put(parameter.name(), source.get(parameter.name()));
            }
        }
        return result;
    }

    private <T> List<T> findByEquality(Class<T> type, Map<String, ?> fields) {
        return queries.findAll(type, fields);
    }

    private Object newEntity(String entityName) {
        return entities.newEntity(entities.entityClass(entityName));
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

    private void copyPrimaryKeys(Object source, Object target) {
        for (String name : entities.primaryKeys(source.getClass())) {
            entities.set(target, name, entities.get(source, name));
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

    private void requirePrimaryKeys(Object entity) {
        for (String name : entities.primaryKeys(entity.getClass())) {
            Object value = entities.get(entity, name);
            if (value == null || value instanceof String text && text.isBlank()) {
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

    private boolean isActive(Object entity, Timestamp now) {
        Object fromDate = entities.get(entity, "fromDate");
        Object thruDate = entities.get(entity, "thruDate");
        return (!(fromDate instanceof Timestamp start) || !start.after(now))
                && (!(thruDate instanceof Timestamp end) || end.after(now));
    }

    private static boolean isEmpty(Object value) {
        return value == null || value instanceof String text && text.isBlank();
    }

    private static Timestamp now() {
        return Timestamp.from(Instant.now().truncatedTo(ChronoUnit.MILLIS));
    }

    private static Map<String, Object> result(String name, Object value) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(name, value);
        return result;
    }

    private static MigrationException badRequest(String message) {
        return new MigrationException(HttpStatus.BAD_REQUEST, message);
    }

    private static boolean isEmail(String email) {
        if (email == null || email.isEmpty()) {
            return true;
        }
        int separator = email.lastIndexOf('@');
        if (separator < 1 || separator != email.indexOf('@')
                || email.length() > 254 || separator > 64
                || !EMAIL_LOCAL_PART.matcher(email.substring(0, separator)).matches()) {
            return false;
        }
        String domain = email.substring(separator + 1);
        if (!domain.contains(".") || domain.length() > 253 || domain.startsWith(".") || domain.endsWith(".")) {
            return false;
        }
        String[] labels = domain.split("\\.", -1);
        for (String label : labels) {
            if (label.isEmpty() || label.length() > 63
                    || !label.matches("[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?")) {
                return false;
            }
        }
        String topLevelDomain = labels[labels.length - 1];
        return topLevelDomain.length() >= 2 && topLevelDomain.matches("[A-Za-z]+");
    }
}
