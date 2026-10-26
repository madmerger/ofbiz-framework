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
package org.apache.ofbiz.migration.marketing.adapter;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.SequenceService;
import org.apache.ofbiz.migration.datamodel.entity.ContactMech;
import org.apache.ofbiz.migration.datamodel.entity.Party;
import org.apache.ofbiz.migration.datamodel.entity.PartyContactMech;
import org.apache.ofbiz.migration.datamodel.entity.PartyContactMechPurpose;
import org.apache.ofbiz.migration.datamodel.entity.PartyRelationship;
import org.apache.ofbiz.migration.datamodel.entity.PartyRole;
import org.apache.ofbiz.migration.datamodel.entity.Person;
import org.apache.ofbiz.migration.datamodel.entity.UserLogin;
import org.apache.ofbiz.migration.marketing.MarketingData;
import org.apache.ofbiz.migration.marketing.port.PartyPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Local adapter that reproduces the party services needed by marketing on the shared data model.
 */
@Component
public class LocalPartyAdapter implements PartyPort {

    /** Fields accepted by the party "person" map processor (PartyMapProcs.xml). */
    private static final Set<String> PERSON_FIELDS = Set.of("salutation", "firstName", "middleName", "lastName",
            "personalTitle", "suffix", "nickname", "firstNameLocal", "middleNameLocal", "lastNameLocal",
            "otherLocal", "memberId", "gender", "birthDate", "deceasedDate", "height", "weight",
            "mothersMaidenName", "maritalStatusTypeId", "socialSecurityNumber", "passportNumber",
            "passportExpireDate", "totalYearsWorkExperience", "comments", "employmentStatusEnumId",
            "residenceStatusEnumId", "occupation", "yearsWithEmployer", "monthsWithEmployer", "existingCustomer",
            "cardId");

    private static final String EMAIL = "EMAIL_ADDRESS";
    private final MarketingData data;
    private final SequenceService sequences;

    public LocalPartyAdapter(MarketingData data, SequenceService sequences) {
        this.data = data;
        this.sequences = sequences;
    }

    @Override
    public Optional<String> primaryEmailContactMechId(String partyId) {
        Timestamp now = MarketingData.now();
        return data.query(PartyContactMechPurpose.class,
                        "select p from PartyContactMechPurpose p where p.partyId = ?1"
                                + " and p.contactMechPurposeTypeId = 'PRIMARY_EMAIL' order by p.fromDate desc", partyId)
                .stream()
                .filter(purpose -> MarketingData.active(purpose.getFromDate(), purpose.getThruDate(), now))
                .map(PartyContactMechPurpose::getContactMechId)
                .filter(id -> EMAIL.equals(contactMechTypeId(id).orElse(null)))
                .findFirst();
    }

    @Override
    public List<EmailContact> findActiveEmailContacts(String partyId, String email, String purposeTypeId) {
        Timestamp now = MarketingData.now();
        return data.query(ContactMech.class,
                        "select c from ContactMech c where c.contactMechTypeId = ?1 and c.infoString = ?2", EMAIL, email)
                .stream()
                .flatMap(mech -> data.query(PartyContactMech.class,
                                "select p from PartyContactMech p where p.contactMechId = ?1 order by p.fromDate desc",
                                mech.getContactMechId()).stream()
                        .filter(link -> partyId == null || partyId.equals(link.getPartyId()))
                        .filter(link -> MarketingData.active(link.getFromDate(), link.getThruDate(), now))
                        .filter(link -> purposeTypeId == null || hasActivePurpose(link, purposeTypeId, now))
                        .map(link -> new EmailContact(link.getPartyId(), mech.getContactMechId(), mech.getInfoString())))
                .toList();
    }

    @Override
    public Optional<EmailContact> findActiveEmailContact(String partyId, String contactMechId) {
        Timestamp now = MarketingData.now();
        Optional<ContactMech> mech = data.one(ContactMech.class, Map.of("contactMechId", contactMechId))
                .filter(found -> EMAIL.equals(found.getContactMechTypeId()));
        if (mech.isEmpty()) {
            return Optional.empty();
        }
        return data.query(PartyContactMech.class,
                        "select p from PartyContactMech p where p.contactMechId = ?1 and p.partyId = ?2",
                        contactMechId, partyId).stream()
                .filter(link -> MarketingData.active(link.getFromDate(), link.getThruDate(), now))
                .findFirst()
                .map(link -> new EmailContact(partyId, contactMechId, mech.get().getInfoString()));
    }

    @Override
    public Optional<String> contactMechTypeId(String contactMechId) {
        return data.one(ContactMech.class, Map.of("contactMechId", contactMechId)).map(ContactMech::getContactMechTypeId);
    }

    @Override
    public Optional<String> infoString(String contactMechId) {
        return data.one(ContactMech.class, Map.of("contactMechId", contactMechId)).map(ContactMech::getInfoString);
    }

    @Override
    public String createPartyEmailAddress(String partyId, String email, String purposeTypeId, Timestamp fromDate) {
        ensureParty(partyId, "PERSON");
        ContactMech mech = new ContactMech();
        mech.setContactMechId(sequences.nextId("ContactMech"));
        mech.setContactMechTypeId(EMAIL);
        mech.setInfoString(email);
        data.persist(mech);
        PartyContactMech link = new PartyContactMech();
        link.setPartyId(partyId);
        link.setContactMechId(mech.getContactMechId());
        link.setFromDate(fromDate);
        data.persist(link);
        PartyContactMechPurpose purpose = new PartyContactMechPurpose();
        purpose.setPartyId(partyId);
        purpose.setContactMechId(mech.getContactMechId());
        purpose.setContactMechPurposeTypeId(purposeTypeId);
        purpose.setFromDate(fromDate);
        data.persist(purpose);
        return mech.getContactMechId();
    }

    @Override
    public String createPerson(Map<String, Object> input) {
        String partyId = MarketingData.text(input, "partyId");
        if (partyId == null) {
            partyId = sequences.nextId("Party");
        } else if (data.one(Party.class, Map.of("partyId", partyId)).isPresent()) {
            throw new MigrationException(HttpStatus.CONFLICT, "Party already exists: " + partyId);
        }
        ensureParty(partyId, "PERSON");
        Person person = new Person();
        Map<String, Object> personFields = new HashMap<>(input);
        personFields.keySet().retainAll(PERSON_FIELDS);
        data.copyFields(person, personFields, Set.of("partyId"));
        person.setPartyId(partyId);
        data.persist(person);
        return partyId;
    }

    @Override
    public Optional<String> loginPartyId(String userLoginId) {
        return data.one(UserLogin.class, Map.of("userLoginId", userLoginId)).map(UserLogin::getPartyId);
    }

    @Override
    public void ensurePartyRole(String partyId, String roleTypeId) {
        if (partyId == null || roleTypeId == null) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "partyId and roleTypeId are required");
        }
        if (data.one(PartyRole.class, Map.of("partyId", partyId, "roleTypeId", roleTypeId)).isEmpty()) {
            PartyRole role = new PartyRole();
            role.setPartyId(partyId);
            role.setRoleTypeId(roleTypeId);
            data.persist(role);
        }
    }

    @Override
    public void createPartyRelationship(
            String partyIdFrom, String partyIdTo, String roleTypeIdFrom, String roleTypeIdTo, String typeId) {
        PartyRelationship relationship = new PartyRelationship();
        relationship.setPartyIdFrom(partyIdFrom);
        relationship.setPartyIdTo(partyIdTo);
        relationship.setRoleTypeIdFrom(roleTypeIdFrom);
        relationship.setRoleTypeIdTo(roleTypeIdTo);
        relationship.setPartyRelationshipTypeId(typeId);
        relationship.setFromDate(MarketingData.now());
        data.persist(relationship);
    }

    @Override
    public void updatePartyContactMech(
            String partyId, String contactMechId, String newContactMechId, String purposeTypeId) {
        Timestamp now = MarketingData.now();
        for (PartyContactMech link : activeLinks(partyId, contactMechId, now)) {
            link.setThruDate(now);
        }
        PartyContactMech replacement = new PartyContactMech();
        replacement.setPartyId(partyId);
        replacement.setContactMechId(newContactMechId);
        replacement.setFromDate(now);
        data.persist(replacement);
        Set<String> purposeTypes = new LinkedHashSet<>();
        data.query(PartyContactMechPurpose.class,
                        "select p from PartyContactMechPurpose p where p.partyId = ?1 and p.contactMechId = ?2",
                        partyId, contactMechId).stream()
                .filter(purpose -> MarketingData.active(purpose.getFromDate(), purpose.getThruDate(), now))
                .forEach(purpose -> {
                    purpose.setThruDate(now);
                    purposeTypes.add(purpose.getContactMechPurposeTypeId());
                });
        if (purposeTypeId != null) {
            purposeTypes.add(purposeTypeId);
        }
        for (String type : purposeTypes) {
            if (data.query(PartyContactMechPurpose.class, "select p from PartyContactMechPurpose p where p.partyId = ?1"
                    + " and p.contactMechId = ?2 and p.contactMechPurposeTypeId = ?3", partyId, newContactMechId, type)
                    .isEmpty()) {
                PartyContactMechPurpose purpose = new PartyContactMechPurpose();
                purpose.setPartyId(partyId);
                purpose.setContactMechId(newContactMechId);
                purpose.setContactMechPurposeTypeId(type);
                purpose.setFromDate(now);
                data.persist(purpose);
            }
        }
    }

    @Override
    public void deletePartyContactMech(String partyId, String contactMechId) {
        Timestamp now = MarketingData.now();
        List<PartyContactMech> links = activeLinks(partyId, contactMechId, now);
        if (links.isEmpty()) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "PartyContactMech not found");
        }
        links.forEach(link -> link.setThruDate(now));
    }

    @Override
    public void setPartyStatus(String partyId, String statusId) {
        Party party = data.require(Party.class, Map.of("partyId", partyId));
        party.setStatusId(statusId);
    }

    private List<PartyContactMech> activeLinks(String partyId, String contactMechId, Timestamp now) {
        return data.query(PartyContactMech.class,
                        "select p from PartyContactMech p where p.partyId = ?1 and p.contactMechId = ?2",
                        partyId, contactMechId).stream()
                .filter(link -> MarketingData.active(link.getFromDate(), link.getThruDate(), now))
                .toList();
    }

    private boolean hasActivePurpose(PartyContactMech link, String purposeTypeId, Timestamp now) {
        return data.query(PartyContactMechPurpose.class,
                        "select p from PartyContactMechPurpose p where p.partyId = ?1 and p.contactMechId = ?2"
                                + " and p.contactMechPurposeTypeId = ?3",
                        link.getPartyId(), link.getContactMechId(), purposeTypeId).stream()
                .anyMatch(purpose -> MarketingData.active(purpose.getFromDate(), purpose.getThruDate(), now));
    }

    private void ensureParty(String partyId, String partyTypeId) {
        if (data.one(Party.class, Map.of("partyId", partyId)).isEmpty()) {
            Party party = new Party();
            party.setPartyId(partyId);
            party.setPartyTypeId(partyTypeId);
            party.setStatusId("PARTY_ENABLED");
            party.setCreatedDate(MarketingData.now());
            data.persist(party);
        }
    }
}
