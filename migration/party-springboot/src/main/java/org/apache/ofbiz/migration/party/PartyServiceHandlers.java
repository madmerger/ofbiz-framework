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

import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.SequenceService;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.stereotype.Component;

@Component
public class PartyServiceHandlers {
    private static final String NOT_FOUND = "ServiceValueNotFound";
    private final ServiceHandlerRegistry registry;
    private final EntityRegistry entities;
    private final EntityManager entityManager;
    private final SequenceService sequences;
    private final UserLoginPort userLoginPort;
    private final TransactionTemplate transactions;

    public PartyServiceHandlers(
            ServiceHandlerRegistry registry,
            EntityRegistry entities,
            EntityManager entityManager,
            SequenceService sequences,
            UserLoginPort userLoginPort,
            PlatformTransactionManager transactionManager) {
        this.registry = registry;
        this.entities = entities;
        this.entityManager = entityManager;
        this.sequences = sequences;
        this.userLoginPort = userLoginPort;
        this.transactions = new TransactionTemplate(transactionManager);
    }

    @PostConstruct
    void register() {
        handler("createPerson", "CREATE", this::createPerson);
        handler("updatePerson", "UPDATE", this::updatePerson);
        handler("createPartyGroup", "CREATE", this::createPartyGroup);
        handler("updatePartyGroup", "UPDATE", this::updatePartyGroup);
        handler("createPartyContactMechPurpose", "CREATE", this::createPurpose);
        handler("createPartyRelationship", "CREATE", this::createRelationship);
        handler("updatePartyRelationship", "UPDATE", this::updateRelationship);
        handler("deletePartyRelationship", "DELETE", this::deleteRelationship);
        handler("createPartyGroupRoleAndContactMechs", "CREATE", this::createPartyGroupRoleAndContactMechs);
        handler("createPostalAddress", "CREATE", input -> createContactValue("PostalAddress", "POSTAL_ADDRESS", input));
        handler("updatePostalAddress", "UPDATE", input -> updateContactValue("PostalAddress", input));
        handler("createTelecomNumber", "CREATE", input -> createContactValue("TelecomNumber", "TELECOM_NUMBER", input));
        handler("updateTelecomNumber", "UPDATE", input -> updateContactValue("TelecomNumber", input));
        handler("createEmailAddress", "CREATE", this::createEmail);
        handler("updateEmailAddress", "UPDATE", this::updateEmail);
        handler("updateContactMech", "UPDATE", this::updateContactMech);
        handler("getPartyEmail", "VIEW", input -> getPartyContact(input, "EMAIL_ADDRESS"));
        handler("getPartyTelephone", "VIEW", input -> getPartyContact(input, "TELECOM_NUMBER"));
        handler("getPartyPostalAddress", "VIEW", input -> getPartyContact(input, "POSTAL_ADDRESS"));
        handler("createPartyRelationshipAndRole", "CREATE", this::createRelationshipAndRoles);
        handler("createPersonAndUserLogin", "CREATE", "UserLogin", this::createPersonAndUserLogin);
        handler("createPartyContactMechs", "CREATE", this::createPartyContactMechs);
        handler("createPersonRoleAndContactMechs", "CREATE", this::createPersonRoleAndContactMechs);
        handler("ensurePartyRole", "CREATE", this::ensurePartyRole);
        handler("ensureNaPartyRole", "CREATE", this::ensureNaPartyRole);
        handler("ensurePartyRoleFrom", "CREATE", this::ensurePartyRoleFrom);
        handler("ensurePartyRoleTo", "CREATE", this::ensurePartyRoleTo);
        handler("createPartyContactMech", "CREATE", this::createPartyContactMech);
        handler("updatePartyContactMech", "UPDATE", this::updatePartyContactMech);
        handler("deletePartyContactMech", "DELETE", this::deletePartyContactMech);
        handler("findPartyFromEmailAddress", "VIEW",
                input -> findPartyByContact(input, "EMAIL_ADDRESS", false));
        handler("findPartyFromTelephone", "VIEW",
                input -> findPartyByContact(input, "TELECOM_NUMBER", false));
        handler("findPartyFromTelephoneComplete", "VIEW",
                input -> findPartyByContact(input, "TELECOM_NUMBER", true));
        handler("createPartyPostalAddress", "CREATE", this::createPartyPostalAddress);
        handler("updatePartyPostalAddress", "UPDATE", this::updatePartyPostalAddress);
        handler("createPartyTelecomNumber", "CREATE", input -> linkContact(input, "TELECOM_NUMBER", "createTelecomNumber"));
        handler("updatePartyTelecomNumber", "UPDATE", input -> updatePartyContact(input, "TELECOM_NUMBER", "updateTelecomNumber"));
        handler("createPartyEmailAddress", "CREATE", this::createPartyEmailAddress);
        handler("updatePartyEmailAddress", "UPDATE", input -> updatePartyContact(input, "EMAIL_ADDRESS", "updateEmailAddress"));
        handler("createCommEventWorkEffort", "CREATE", this::createCommEventWorkEffort);
        handler("createPostalAddressAndPurposes", "CREATE", this::createPostalAddressAndPurposes);
        handler("updatePostalAddressAndPurposes", "UPDATE", this::updatePostalAddressAndPurposes);
        handler("updateContactMechAndPurposes", "UPDATE", this::updateContactMechAndPurposes);
        handler("createUpdatePartyTelecomNumber", "UPDATE", input -> createOrUpdatePartyContact(input, "TELECOM_NUMBER"));
        handler("createUpdatePartyEmailAddress", "UPDATE", input -> createOrUpdatePartyContact(input, "EMAIL_ADDRESS"));
        handler("createUpdatePartyPostalAddress", "UPDATE", input -> createOrUpdatePartyContact(input, "POSTAL_ADDRESS"));
    }

    private void handler(String name, String action, ServiceHandlerRegistry.SimpleServiceHandler handler) {
        registry.registerHandler("party", name, action,
                input -> transactions.execute(status -> handler.execute(input)));
    }

    private void handler(
            String name, String action, String sensitiveEntity, ServiceHandlerRegistry.SimpleServiceHandler handler) {
        registry.registerHandler("party", name, action, sensitiveEntity,
                input -> transactions.execute(status -> handler.execute(input)));
    }

    public Map<String, Object> createPerson(Map<String, Object> input) {
        String partyId = text(input, "partyId");
        if (partyId == null || partyId.isBlank()) {
            partyId = sequences.nextId("Party");
        }
        Object party = find("Party", Map.of("partyId", partyId));
        if (party != null) {
            if (!"PERSON".equals(entities.get(party, "partyTypeId"))) {
                throw badRequest("partyId already belongs to a non-person Party");
            }
            if (find("Person", Map.of("partyId", partyId)) != null) {
                throw conflict("PARTY_DUPLICATE: Person");
            }
        } else {
            Map<String, Object> partyFields = new LinkedHashMap<>();
            partyFields.put("partyId", partyId);
            partyFields.put("partyTypeId", "PERSON");
            partyFields.put("statusId", value(input, "statusId", "PARTY_ENABLED"));
            copy(input, partyFields, "description", "preferredCurrencyUomId", "externalId");
            create("Party", partyFields);
        }
        Map<String, Object> personFields = new LinkedHashMap<>();
        personFields.put("partyId", partyId);
        copy(input, personFields, "firstName", "middleName", "lastName", "personalTitle", "suffix", "gender",
                "birthDate", "deceasedDate", "maritalStatusEnumId", "employmentStatusEnumId", "occupation",
                "socialSecurityNumber", "passportNumber", "comments");
        create("Person", personFields);
        return result("partyId", partyId);
    }

    private Map<String, Object> updatePerson(Map<String, Object> input) {
        return update("Person", input);
    }

    private Map<String, Object> createPartyGroup(Map<String, Object> input) {
        String partyId = text(input, "partyId");
        if (partyId == null || partyId.isBlank()) {
            partyId = sequences.nextId("Party");
        }
        if (find("Party", Map.of("partyId", partyId)) != null) {
            throw conflict("PARTY_DUPLICATE: Party");
        }
        Map<String, Object> partyFields = new LinkedHashMap<>();
        partyFields.put("partyId", partyId);
        partyFields.put("partyTypeId", "PARTY_GROUP");
        partyFields.put("statusId", value(input, "statusId", "PARTY_ENABLED"));
        copy(input, partyFields, "description", "preferredCurrencyUomId", "externalId");
        create("Party", partyFields);
        Map<String, Object> group = new LinkedHashMap<>();
        group.put("partyId", partyId);
        copy(input, group, "groupName", "officeSiteName", "annualRevenue", "numEmployees", "tickerSymbol",
                "comments", "logoImageUrl");
        create("PartyGroup", group);
        return result("partyId", partyId);
    }

    private Map<String, Object> updatePartyGroup(Map<String, Object> input) {
        return update("PartyGroup", input);
    }

    private Map<String, Object> createPurpose(Map<String, Object> input) {
        return create("PartyContactMechPurpose", input);
    }

    private Map<String, Object> createRelationship(Map<String, Object> input) {
        required(input, "partyIdFrom", "partyIdTo", "partyRelationshipTypeId");
        Map<String, Object> fields = new LinkedHashMap<>(input);
        fields.putIfAbsent("roleTypeIdFrom", "_NA_");
        fields.putIfAbsent("roleTypeIdTo", "_NA_");
        fields.putIfAbsent("fromDate", now());
        fields.putIfAbsent("statusId", "PARTYREL_CREATED");
        return create("PartyRelationship", fields);
    }

    private Map<String, Object> updateRelationship(Map<String, Object> input) {
        return update("PartyRelationship", input);
    }

    private Map<String, Object> deleteRelationship(Map<String, Object> input) {
        required(input, "partyIdFrom", "partyIdTo", "partyRelationshipTypeId");
        Map<String, Object> filters = new LinkedHashMap<>();
        copy(input, filters, "partyIdFrom", "partyIdTo", "partyRelationshipTypeId", "roleTypeIdFrom", "roleTypeIdTo");
        filters.putIfAbsent("roleTypeIdFrom", "_NA_");
        filters.putIfAbsent("roleTypeIdTo", "_NA_");
        copy(input, filters, "fromDate");
        List<?> matches = entities.findByEquality(entities.entityClass("PartyRelationship"), filters);
        if (matches.isEmpty()) {
            throw notFound(NOT_FOUND);
        }
        Object match = matches.get(0);
        Map<String, Object> response = entities.toMap(match);
        entityManager.remove(match);
        return response;
    }

    private Map<String, Object> createRelationshipAndRoles(Map<String, Object> input) {
        String from = text(input, "partyIdFrom");
        String to = text(input, "partyIdTo");
        ensureRole(from, value(input, "roleTypeIdFrom", "_NA_"));
        ensureRole(to, value(input, "roleTypeIdTo", "_NA_"));
        return createRelationship(input);
    }

    private Map<String, Object> createPartyGroupRoleAndContactMechs(Map<String, Object> input) {
        Map<String, Object> group = createPartyGroup(input);
        String partyId = text(group, "partyId");
        ensureRole(partyId, value(input, "roleTypeId", "CUSTOMER"));
        createPartyContactMechs(input, partyId);
        return group;
    }

    private Map<String, Object> createPersonAndUserLogin(Map<String, Object> input) {
        Map<String, Object> person = createPerson(input);
        return userLoginPort.createUserLogin(input, text(person, "partyId"));
    }

    private Map<String, Object> createPersonRoleAndContactMechs(Map<String, Object> input) {
        Map<String, Object> person = createPerson(input);
        String partyId = text(person, "partyId");
        ensureRole(partyId, value(input, "roleTypeId", "CONTACT"));
        createPartyContactMechs(input, partyId);
        return person;
    }

    private Map<String, Object> ensurePartyRole(Map<String, Object> input) {
        ensureRole(text(input, "partyId"), value(input, "roleTypeId", "_NA_"));
        return result("partyId", text(input, "partyId"));
    }

    private Map<String, Object> ensureNaPartyRole(Map<String, Object> input) {
        String partyId = value(input, "partyId", value(input, "partyIdFrom", text(input, "partyIdTo")));
        String role = value(input, "roleTypeId", value(input, "roleTypeIdFrom",
                value(input, "roleTypeIdTo", "_NA_")));
        ensureRole(partyId, role);
        return result("partyId", partyId);
    }

    private Map<String, Object> ensurePartyRoleFrom(Map<String, Object> input) {
        ensureRole(text(input, "partyIdFrom"), value(input, "roleTypeIdFrom", "_NA_"));
        return result("partyId", text(input, "partyIdFrom"));
    }

    private Map<String, Object> ensurePartyRoleTo(Map<String, Object> input) {
        ensureRole(text(input, "partyIdTo"), value(input, "roleTypeIdTo", "_NA_"));
        return result("partyId", text(input, "partyIdTo"));
    }

    private Map<String, Object> createPartyContactMech(Map<String, Object> input) {
        required(input, "partyId", "contactMechTypeId");
        String partyId = text(input, "partyId");
        String type = text(input, "contactMechTypeId");
        String infoString = text(input, "infoString");
        String contactMechId = text(input, "contactMechId");

        if (infoString != null) {
            for (Object link : entities.findByEquality(entities.entityClass("PartyContactMech"),
                    Map.of("partyId", partyId))) {
                if (!isCurrent(link, now())) {
                    continue;
                }
                String linkedId = text(entities.toMap(link), "contactMechId");
                Object contact = find("ContactMech", Map.of("contactMechId", linkedId));
                if (contact != null && type.equals(entities.get(contact, "contactMechTypeId"))
                        && infoString.equals(entities.get(contact, "infoString"))) {
                    return result("contactMechId", linkedId);
                }
            }
        }

        if (contactMechId == null || contactMechId.isBlank()) {
            contactMechId = sequences.nextId("ContactMech");
            Map<String, Object> contact = new LinkedHashMap<>();
            contact.put("contactMechId", contactMechId);
            contact.put("contactMechTypeId", type);
            copy(input, contact, "infoString", "comments");
            create("ContactMech", contact);
        }
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("partyId", partyId);
        fields.put("contactMechId", contactMechId);
        copy(input, fields, "fromDate", "comments", "extension", "allowSolicitation");
        fields.putIfAbsent("fromDate", now());
        fields.putIfAbsent("allowSolicitation", "Y");
        create("PartyContactMech", fields);
        createPurposeIfPresent(partyId, contactMechId, text(input, "contactMechPurposeTypeId"));
        return result("contactMechId", contactMechId);
    }

    private Map<String, Object> updatePartyContactMech(Map<String, Object> input) {
        required(input, "partyId", "contactMechId");
        String partyId = text(input, "partyId");
        String oldId = text(input, "contactMechId");
        String newId = text(input, "newContactMechId");
        Map<String, Object> filters = Map.of("partyId", partyId, "contactMechId", oldId);
        List<?> relationships = entities.findByEquality(entities.entityClass("PartyContactMech"), filters);
        Object relationship = relationships.stream()
                .filter(candidate -> isCurrent(candidate, now()))
                .findFirst()
                .orElse(null);
        if (relationship == null) {
            throw notFound(NOT_FOUND);
        }
        if (newId == null && input.containsKey("infoString")) {
            Map<String, Object> contactInput = new LinkedHashMap<>(input);
            contactInput.put("contactMechId", oldId);
            newId = text(updateContactMech(contactInput), "contactMechId");
        }
        if (newId != null && !newId.equals(oldId)) {
            entities.set(relationship, "thruDate", now());
            Map<String, Object> newLink = new LinkedHashMap<>(entities.toMap(relationship));
            newLink.put("partyId", partyId);
            newLink.put("contactMechId", newId);
            newLink.put("fromDate", now());
            newLink.put("thruDate", null);
            copy(input, newLink, "comments", "extension", "allowSolicitation");
            create("PartyContactMech", newLink);
            transferCurrentPurposes(partyId, oldId, newId);
            return result("contactMechId", newId);
        }
        copyWritable(input, relationship, "partyId", "contactMechId", "newContactMechId",
                "contactMechTypeId", "fromDate", "thruDate");
        return result("contactMechId", oldId);
    }

    private Map<String, Object> deletePartyContactMech(Map<String, Object> input) {
        required(input, "partyId", "contactMechId");
        String partyId = text(input, "partyId");
        String contactMechId = text(input, "contactMechId");
        Map<String, Object> filters = Map.of("partyId", partyId, "contactMechId", contactMechId);
        List<?> matches = entities.findByEquality(entities.entityClass("PartyContactMech"), filters);
        Object link = matches.stream().filter(match -> isCurrent(match, now())).findFirst().orElse(null);
        if (link == null) {
            throw notFound(NOT_FOUND);
        }
        entities.set(link, "thruDate", now());
        return entities.toMap(link);
    }

    private void transferCurrentPurposes(String partyId, String oldId, String newId) {
        List<?> purposes = entities.findByEquality(entities.entityClass("PartyContactMechPurpose"),
                Map.of("partyId", partyId, "contactMechId", oldId));
        Timestamp changedAt = now();
        for (Object purpose : purposes) {
            if (!isCurrent(purpose, changedAt)) {
                continue;
            }
            String purposeTypeId = text(entities.toMap(purpose), "contactMechPurposeTypeId");
            entities.set(purpose, "thruDate", changedAt);
            createPurposeIfPresent(partyId, newId, purposeTypeId);
        }
    }

    private Map<String, Object> createPartyPostalAddress(Map<String, Object> input) {
        Map<String, Object> address = createContactValue("PostalAddress", "POSTAL_ADDRESS", input);
        Map<String, Object> linked = new LinkedHashMap<>(input);
        linked.put("contactMechId", address.get("contactMechId"));
        linked.put("contactMechTypeId", "POSTAL_ADDRESS");
        createPartyContactMech(linked);
        return address;
    }

    private Map<String, Object> updatePartyPostalAddress(Map<String, Object> input) {
        Map<String, Object> updated = updateContactValue("PostalAddress", input);
        Map<String, Object> link = new LinkedHashMap<>(input);
        link.put("newContactMechId", updated.get("contactMechId"));
        link.put("contactMechTypeId", "POSTAL_ADDRESS");
        updatePartyContactMech(link);
        return updated;
    }

    private Map<String, Object> createContactValue(String entityName, String type, Map<String, Object> input) {
        String contactMechId = text(input, "contactMechId");
        if (contactMechId == null || contactMechId.isBlank()) {
            contactMechId = sequences.nextId("ContactMech");
        }
        Map<String, Object> mech = new LinkedHashMap<>();
        mech.put("contactMechId", contactMechId);
        mech.put("contactMechTypeId", type);
        String info = value(input, "infoString", text(input, "emailAddress"));
        if (info != null) {
            mech.put("infoString", info);
        }
        create("ContactMech", mech);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("contactMechId", contactMechId);
        copy(input, data, "toName", "attnName", "address1", "address2", "houseNumber", "houseNumberExt",
                "directions", "city", "cityGeoId", "postalCode", "postalCodeExt", "countryGeoId",
                "stateProvinceGeoId", "countyGeoId", "postalCodeGeoId", "geoPointId", "areaCode",
                "countryCode", "contactNumber", "askForName");
        if ("PostalAddress".equals(entityName)) {
            validatePostalState(input);
        }
        create(entityName, data);
        return result("contactMechId", contactMechId);
    }

    private Map<String, Object> updateContactValue(String entityName, Map<String, Object> input) {
        required(input, "contactMechId");
        String oldId = text(input, "contactMechId");
        Object oldValue = find(entityName, Map.of("contactMechId", oldId));
        if (oldValue == null) {
            throw notFound(NOT_FOUND);
        }
        Map<String, Object> existing = entities.toMap(oldValue);
        boolean changed = false;
        for (String key : input.keySet()) {
            if (existing.containsKey(key) && !key.equals("contactMechId")
                    && input.get(key) != null && !input.get(key).toString().equals(String.valueOf(existing.get(key)))) {
                changed = true;
                break;
            }
        }
        if (!changed) {
            return result("contactMechId", oldId, "oldContactMechId", oldId);
        }
        String type = "PostalAddress".equals(entityName) ? "POSTAL_ADDRESS" : "TelecomNumber".equals(entityName)
                ? "TELECOM_NUMBER" : "EMAIL_ADDRESS";
        Map<String, Object> clone = new LinkedHashMap<>(existing);
        clone.putAll(input);
        clone.remove("createdStamp");
        clone.remove("createdTxStamp");
        clone.remove("lastUpdatedStamp");
        clone.remove("lastUpdatedTxStamp");
        clone.put("contactMechId", null);
        Map<String, Object> created = createContactValue(entityName, type, clone);
        return result("contactMechId", created.get("contactMechId"), "oldContactMechId", oldId);
    }

    private Map<String, Object> createEmail(Map<String, Object> input) {
        String email = text(input, "emailAddress");
        required(input, "emailAddress");
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw badRequest("PartyEmailAddressNotFormattedCorrectly");
        }
        String contactMechId = text(input, "contactMechId");
        if (contactMechId == null || contactMechId.isBlank()) {
            contactMechId = sequences.nextId("ContactMech");
        }
        return create("ContactMech", Map.of(
                "contactMechId", contactMechId,
                "contactMechTypeId", "EMAIL_ADDRESS",
                "infoString", email));
    }

    private Map<String, Object> updateEmail(Map<String, Object> input) {
        required(input, "contactMechId", "emailAddress");
        if (!text(input, "emailAddress").matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw badRequest("PartyEmailAddressNotFormattedCorrectly");
        }
        Object old = find("ContactMech", Map.of("contactMechId", input.get("contactMechId")));
        if (old == null) {
            throw notFound(NOT_FOUND);
        }
        String previous = text(entities.toMap(old), "infoString");
        if (previous.equals(text(input, "emailAddress"))) {
            return result("contactMechId", input.get("contactMechId"));
        }
        Map<String, Object> updated = new LinkedHashMap<>(input);
        updated.remove("contactMechId");
        return createEmail(updated);
    }

    private Map<String, Object> updateContactMech(Map<String, Object> input) {
        required(input, "contactMechId");
        Object old = find("ContactMech", Map.of("contactMechId", input.get("contactMechId")));
        if (old == null) {
            throw notFound(NOT_FOUND);
        }
        String oldInfo = text(entities.toMap(old), "infoString");
        String newInfo = text(input, "infoString");
        if (oldInfo == null ? newInfo == null : oldInfo.equals(newInfo)) {
            return result("contactMechId", input.get("contactMechId"));
        }
        Map<String, Object> copy = new LinkedHashMap<>(entities.toMap(old));
        copy.putAll(input);
        copy.put("contactMechId", null);
        String id = sequences.nextId("ContactMech");
        copy.put("contactMechId", id);
        return create("ContactMech", copy);
    }

    private Map<String, Object> getPartyContact(Map<String, Object> input, String type) {
        required(input, "partyId");
        List<?> links = entities.findByEquality(entities.entityClass("PartyContactMech"),
                Map.of("partyId", input.get("partyId")));
        Timestamp current = now();
        for (Object link : links) {
            if (!isCurrent(link, current)) {
                continue;
            }
            String id = text(entities.toMap(link), "contactMechId");
            Object mech = find("ContactMech", Map.of("contactMechId", id));
            if (mech == null || !type.equals(entities.get(mech, "contactMechTypeId"))) {
                continue;
            }
            String entity = switch (type) {
                case "POSTAL_ADDRESS" -> "PostalAddress";
                case "TELECOM_NUMBER" -> "TelecomNumber";
                default -> "ContactMech";
            };
            Object value = find(entity, Map.of("contactMechId", id));
            return value == null ? entities.toMap(mech) : entities.toMap(value);
        }
        throw notFound(NOT_FOUND);
    }

    private Map<String, Object> createPartyContactMechs(Map<String, Object> input) {
        String partyId = text(input, "partyId");
        if (partyId == null || partyId.isBlank()) {
            throw badRequest("partyId is required");
        }
        Map<String, Object> postal = nestedInput(input, "postalAddressContext");
        Map<String, Object> telephone = nestedInput(input, "telecomNumberContext");
        Map<String, Object> email = nestedInput(input, "emailAddressContext");
        if (postal.isEmpty() && input.get("address1") != null) {
            postal = new LinkedHashMap<>(input);
        }
        if (telephone.isEmpty() && input.get("contactNumber") != null) {
            telephone = new LinkedHashMap<>(input);
        }
        if (email.isEmpty() && input.get("emailAddress") != null) {
            email = new LinkedHashMap<>(input);
        }
        if (!postal.isEmpty()) {
            postal.put("partyId", partyId);
            postal.putIfAbsent("contactMechPurposeTypeId", "GENERAL_LOCATION");
            String contactId = text(createPartyPostalAddress(postal), "contactMechId");
            createPurposeIfPresent(partyId, contactId, text(input, "postalAddContactMechPurpTypeId"));
        }
        if (!telephone.isEmpty()) {
            telephone.put("partyId", partyId);
            telephone.putIfAbsent("contactMechPurposeTypeId",
                    value(input, "phoneContactMechPurpTypeId", "PRIMARY_PHONE"));
            String contactId = text(linkContact(telephone, "TELECOM_NUMBER", "createTelecomNumber"),
                    "contactMechId");
            createPurposeIfPresent(partyId, contactId, text(input, "phoneContactMechPurpTypeId"));
        }
        if (!email.isEmpty()) {
            email.put("partyId", partyId);
            email.putIfAbsent("contactMechPurposeTypeId",
                    value(input, "emailContactMechPurpTypeId", "PRIMARY_EMAIL"));
            String contactId = text(createPartyEmailAddress(email), "contactMechId");
            createPurposeIfPresent(partyId, contactId, text(input, "emailContactMechPurpTypeId"));
        }
        return result("partyId", partyId);
    }

    private Map<String, Object> createPartyEmailAddress(Map<String, Object> input) {
        required(input, "partyId", "emailAddress");
        String email = text(input, "emailAddress");
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw badRequest("PartyEmailAddressNotFormattedCorrectly");
        }
        Map<String, Object> link = new LinkedHashMap<>(input);
        link.put("contactMechTypeId", "EMAIL_ADDRESS");
        link.put("infoString", email);
        return createPartyContactMech(link);
    }

    private Map<String, Object> linkContact(Map<String, Object> input, String type, String createService) {
        Map<String, Object> contact = switch (createService) {
            case "createTelecomNumber" -> createContactValue("TelecomNumber", type, input);
            case "createEmailAddress" -> createEmail(input);
            default -> createContactValue("PostalAddress", type, input);
        };
        Map<String, Object> link = new LinkedHashMap<>(input);
        link.put("contactMechId", contact.get("contactMechId"));
        link.put("contactMechTypeId", type);
        return createPartyContactMech(link);
    }

    private Map<String, Object> updatePartyContact(Map<String, Object> input, String type, String operation) {
        Map<String, Object> updated = type.equals("EMAIL_ADDRESS") ? updateEmail(input)
                : updateContactValue("TelecomNumber", input);
        Map<String, Object> link = new LinkedHashMap<>(input);
        link.put("newContactMechId", updated.get("contactMechId"));
        link.put("contactMechTypeId", type);
        updatePartyContactMech(link);
        return updated;
    }

    private Map<String, Object> createPostalAddressAndPurposes(Map<String, Object> input) {
        Map<String, Object> created = createPartyPostalAddress(input);
        addPostalPurposes(input, text(created, "contactMechId"));
        return created;
    }

    private Map<String, Object> updatePostalAddressAndPurposes(Map<String, Object> input) {
        Map<String, Object> updated = updatePartyPostalAddress(input);
        addPostalPurposes(input, text(updated, "contactMechId"));
        return updated;
    }

    private Map<String, Object> updateContactMechAndPurposes(Map<String, Object> input) {
        Map<String, Object> updatedAddress = updatePostalAddressAndPurposes(input);
        String phoneContactMechId = text(input, "phoneContactMechId");
        if (phoneContactMechId != null && !phoneContactMechId.isBlank()) {
            Map<String, Object> phoneInput = new LinkedHashMap<>(input);
            phoneInput.put("contactMechId", phoneContactMechId);
            updatePartyContact(phoneInput, "TELECOM_NUMBER", "updateTelecomNumber");
        }
        return updatedAddress;
    }

    private Map<String, Object> createOrUpdatePartyContact(Map<String, Object> input, String type) {
        String contactId = text(input, "contactMechId");
        List<?> existing = contactId == null ? List.of() : entities.findByEquality(
                entities.entityClass("PartyContactMech"),
                Map.of("partyId", input.get("partyId"), "contactMechId", contactId));
        if (existing.isEmpty()) {
            return type.equals("POSTAL_ADDRESS") ? createPartyPostalAddress(input)
                    : linkContact(input, type, type.equals("EMAIL_ADDRESS") ? "createEmailAddress" : "createTelecomNumber");
        }
        return type.equals("POSTAL_ADDRESS") ? updatePartyPostalAddress(input)
                : updatePartyContact(input, type, type.equals("EMAIL_ADDRESS") ? "updateEmailAddress" : "updateTelecomNumber");
    }

    private Map<String, Object> findPartyByContact(Map<String, Object> input, String type, boolean completeTelephone) {
        String address = type.equals("EMAIL_ADDRESS")
                ? value(input, "address", value(input, "emailAddress", text(input, "infoString")))
                : value(input, "telno", text(input, "contactNumber"));
        if (address == null || address.isBlank()) {
            throw badRequest("address or telno is required");
        }
        if (type.equals("EMAIL_ADDRESS")) {
            boolean caseInsensitive = "Y".equalsIgnoreCase(text(input, "caseInsensitive"));
            for (Object mechanism : entities.findByEquality(
                    entities.entityClass("ContactMech"), new LinkedHashMap<>())) {
                Map<String, Object> values = entities.toMap(mechanism);
                String info = text(values, "infoString");
                if (info == null || !type.equals(entities.get(mechanism, "contactMechTypeId"))
                        || (caseInsensitive ? !info.equalsIgnoreCase(address) : !info.equals(address))) {
                    continue;
                }
                Map<String, Object> match = findCurrentPartyLink(text(values, "contactMechId"));
                if (match != null) {
                    return match;
                }
            }
        } else {
            String target = telephoneValue(address, completeTelephone);
            for (Object telephone : entities.findByEquality(
                    entities.entityClass("TelecomNumber"), new LinkedHashMap<>())) {
                Map<String, Object> values = entities.toMap(telephone);
                String number = text(values, "contactNumber");
                if (number == null) {
                    continue;
                }
                String area = value(values, "areaCode", "");
                String country = value(values, "countryCode", "");
                List<String> candidates = new java.util.ArrayList<>();
                candidates.add(telephoneValue(number, completeTelephone));
                candidates.add(telephoneValue(area + number, completeTelephone));
                candidates.add(telephoneValue(country + area + number, completeTelephone));
                candidates.add(telephoneValue("+" + country + area + number, completeTelephone));
                if (!candidates.contains(target)) {
                    continue;
                }
                Map<String, Object> match = findCurrentPartyLink(text(values, "contactMechId"));
                if (match != null) {
                    return match;
                }
            }
        }
        return Map.of();
    }

    private void addPurposes(Map<String, Object> input, String contactMechId) {
        Object purposeValues = input.get("contactMechPurposeTypeIds");
        if (!(purposeValues instanceof Iterable<?> iterable)) {
            return;
        }
        for (Object value : iterable) {
            createPurposeIfPresent(text(input, "partyId"), contactMechId, String.valueOf(value));
        }
    }

    private void addPostalPurposes(Map<String, Object> input, String contactMechId) {
        addPurposes(input, contactMechId);
        String partyId = text(input, "partyId");
        if ("Y".equalsIgnoreCase(text(input, "setShippingPurpose"))) {
            createPurposeIfPresent(partyId, contactMechId, "SHIPPING_LOCATION");
        }
        if ("Y".equalsIgnoreCase(text(input, "setBillingPurpose"))) {
            createPurposeIfPresent(partyId, contactMechId, "BILLING_LOCATION");
        }
    }

    private Map<String, Object> findCurrentPartyLink(String contactMechId) {
        if (contactMechId == null) {
            return null;
        }
        for (Object link : entities.findByEquality(entities.entityClass("PartyContactMech"),
                Map.of("contactMechId", contactMechId))) {
            if (isCurrent(link, now())) {
                return result("partyId", entities.get(link, "partyId"), "contactMechId", contactMechId);
            }
        }
        return null;
    }

    private void createPurposeIfPresent(String partyId, String contactMechId, String purposeTypeId) {
        if (partyId == null || contactMechId == null || purposeTypeId == null || purposeTypeId.isBlank()) {
            return;
        }
        Map<String, Object> filters = Map.of(
                "partyId", partyId,
                "contactMechId", contactMechId,
                "contactMechPurposeTypeId", purposeTypeId);
        if (entities.findByEquality(entities.entityClass("PartyContactMechPurpose"), filters).stream()
                .anyMatch(purpose -> isCurrent(purpose, now()))) {
            return;
        }
        create("PartyContactMechPurpose", Map.of(
                "partyId", partyId,
                "contactMechId", contactMechId,
                "contactMechPurposeTypeId", purposeTypeId,
                "fromDate", now()));
    }

    private static String telephoneValue(String value, boolean complete) {
        return complete ? value : value.replace("-", "");
    }

    private Map<String, Object> createCommEventWorkEffort(Map<String, Object> input) {
        required(input, "communicationEventId", "currentStatusId", "workEffortName", "workEffortTypeId");
        String workEffortId = text(input, "workEffortId");
        if (workEffortId == null || workEffortId.isBlank()) {
            workEffortId = sequences.nextId("WorkEffort");
        }
        Map<String, Object> workEffort = new LinkedHashMap<>();
        workEffort.put("workEffortId", workEffortId);
        copy(input, workEffort, "workEffortTypeId", "currentStatusId", "lastStatusUpdate",
                "workEffortPurposeTypeId", "priority", "percentComplete", "workEffortName",
                "description", "locationDesc", "estimatedStartDate", "estimatedCompletionDate");
        create("WorkEffort", workEffort);
        Map<String, Object> association = new LinkedHashMap<>();
        association.put("workEffortId", workEffortId);
        association.put("communicationEventId", input.get("communicationEventId"));
        copy(input, association, "description");
        create("CommunicationEventWorkEff", association);
        return result("workEffortId", workEffortId, "communicationEventId", input.get("communicationEventId"));
    }

    private void createPartyContactMechs(Map<String, Object> input, String partyId) {
        Map<String, Object> values = new LinkedHashMap<>(input);
        values.put("partyId", partyId);
        createPartyContactMechs(values);
    }

    private static Map<String, Object> nestedInput(Map<String, Object> input, String name) {
        Object nested = input.get(name);
        if (!(nested instanceof Map<?, ?> values)) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> result = new LinkedHashMap<>();
        values.forEach((key, value) -> result.put(String.valueOf(key), value));
        return result;
    }

    private void ensureRole(String partyId, String roleTypeId) {
        if (partyId == null || partyId.isBlank()) {
            throw badRequest("partyId is required");
        }
        Map<String, Object> key = Map.of("partyId", partyId, "roleTypeId", roleTypeId);
        if (find("PartyRole", key) == null) {
            create("PartyRole", key);
        }
    }

    private Map<String, Object> create(String entityName, Map<String, ?> input) {
        Class<?> type = entities.entityClass(entityName);
        Object entity = entities.newEntity(type);
        copyWritable(input, entity);
        if (hasProperty(entity, "fromDate") && entities.get(entity, "fromDate") == null) {
            entities.set(entity, "fromDate", now());
        }
        Map<String, Object> keys = new LinkedHashMap<>();
        for (String key : entities.primaryKeys(type)) {
            Object value = entities.get(entity, key);
            if (value == null) {
                throw badRequest("Missing primary key: " + key);
            }
            keys.put(key, value);
        }
        if (entities.findByPrimaryKey(type, keys) != null) {
            throw conflict("ServiceValueFound");
        }
        entityManager.persist(entity);
        return entities.toMap(entity);
    }

    private Map<String, Object> update(String entityName, Map<String, ?> input) {
        Class<?> type = entities.entityClass(entityName);
        Object entity = entities.findByPrimaryKey(type, input);
        if (entity == null) {
            throw notFound(NOT_FOUND);
        }
        copyWritable(input, entity, entities.primaryKeys(type).toArray(String[]::new));
        return entities.toMap(entity);
    }

    private Object find(String entityName, Map<String, ?> keys) {
        return entities.findByPrimaryKey(entities.entityClass(entityName), keys);
    }

    private static void copyWritable(Map<String, ?> input, Object target, String... excluded) {
        List<String> skip = List.of(excluded);
        BeanWrapperImpl wrapper = new BeanWrapperImpl(target);
        for (var field : input.entrySet()) {
            if (field.getValue() != null && !skip.contains(field.getKey()) && wrapper.isWritableProperty(field.getKey())) {
                wrapper.setPropertyValue(field.getKey(), convertValue(wrapper, field.getKey(), field.getValue()));
            }
        }
    }

    private static Object convertValue(BeanWrapperImpl wrapper, String property, Object value) {
        if (value instanceof String text) {
            Class<?> propertyType = wrapper.getPropertyType(property);
            if (Timestamp.class.equals(propertyType)) {
                return Timestamp.valueOf(text.replace('T', ' '));
            }
            if (java.sql.Date.class.equals(propertyType)) {
                return java.sql.Date.valueOf(text);
            }
            if (java.sql.Time.class.equals(propertyType)) {
                return java.sql.Time.valueOf(text);
            }
        }
        return value;
    }

    private static boolean hasProperty(Object target, String name) {
        return new BeanWrapperImpl(target).isReadableProperty(name);
    }

    private static void required(Map<String, ?> input, String... names) {
        for (String name : names) {
            if (input.get(name) == null || input.get(name).toString().isBlank()) {
                throw badRequest("Missing required input: " + name);
            }
        }
    }

    private static void validatePostalState(Map<String, ?> input) {
        String country = text(input, "countryGeoId");
        String state = text(input, "stateProvinceGeoId");
        if ("USA".equals(country) && (state == null || state.isBlank())) {
            throw badRequest("PartyStateInUsMissing");
        }
        if ("CAN".equals(country) && (state == null || state.isBlank())) {
            throw badRequest("PartyProvinceInCanadaMissing");
        }
    }

    private static void copy(Map<String, ?> source, Map<String, Object> destination, String... names) {
        for (String name : names) {
            if (source.containsKey(name)) {
                destination.put(name, source.get(name));
            }
        }
    }

    private static Map<String, Object> merge(Map<String, Object> input, Map<String, Object> contact, String type) {
        Map<String, Object> result = new LinkedHashMap<>(input);
        result.put("contactMechId", contact.get("contactMechId"));
        result.put("contactMechTypeId", type);
        return result;
    }

    private static Map<String, Object> result(Object... pairs) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            result.put(String.valueOf(pairs[i]), pairs[i + 1]);
        }
        result.put("responseMessage", "success");
        return result;
    }

    private static String text(Map<String, ?> map, String key) {
        Object value = map.get(key);
        return value == null ? null : value.toString();
    }

    private static String value(Map<String, ?> map, String key, String fallback) {
        String value = text(map, key);
        return value == null || value.isBlank() ? fallback : value;
    }

    private static Timestamp now() {
        return Timestamp.from(Instant.now());
    }

    private static boolean isCurrent(Object entity, Timestamp current) {
        Object from = new BeanWrapperImpl(entity).getPropertyValue("fromDate");
        Object thru = new BeanWrapperImpl(entity).getPropertyValue("thruDate");
        return (from == null || !((Timestamp) from).after(current))
                && (thru == null || ((Timestamp) thru).after(current));
    }

    private static MigrationException badRequest(String message) {
        return new MigrationException(HttpStatus.BAD_REQUEST, message);
    }

    private static MigrationException conflict(String message) {
        return new MigrationException(HttpStatus.CONFLICT, message);
    }

    private static MigrationException notFound(String message) {
        return new MigrationException(HttpStatus.NOT_FOUND, message);
    }
}
