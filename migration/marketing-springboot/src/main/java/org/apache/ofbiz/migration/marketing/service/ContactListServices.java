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
package org.apache.ofbiz.migration.marketing.service;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.datamodel.entity.ContactList;
import org.apache.ofbiz.migration.datamodel.entity.ContactListParty;
import org.apache.ofbiz.migration.datamodel.entity.ContactListPartyStatus;
import org.apache.ofbiz.migration.marketing.MarketingData;
import org.apache.ofbiz.migration.marketing.port.MailPort;
import org.apache.ofbiz.migration.marketing.port.PartyPort;
import org.apache.ofbiz.migration.marketing.port.ProductStorePort;
import org.apache.ofbiz.migration.marketing.port.CommunicationEventPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Hand-ported minilang services of ContactListServices.xml (subscription state transitions).
 * Service ECAs of secas.xml that send mail are executed synchronously after the status change.
 */
@Service
public class ContactListServices {

    public static final String PENDING = "CLPT_PENDING";
    public static final String ACCEPTED = "CLPT_ACCEPTED";
    public static final String REJECTED = "CLPT_REJECTED";
    public static final String UNSUBSCRIBED = "CLPT_UNSUBSCRIBED";
    public static final String UNSUBS_PENDING = "CLPT_UNSUBS_PENDING";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final MarketingData data;
    private final PartyPort party;
    private final MailPort mail;
    private final ProductStorePort stores;
    private final CommunicationEventPort communicationEvents;

    public ContactListServices(MarketingData data, PartyPort party, MailPort mail, ProductStorePort stores,
            CommunicationEventPort communicationEvents) {
        this.data = data;
        this.party = party;
        this.mail = mail;
        this.stores = stores;
        this.communicationEvents = communicationEvents;
    }

    public Map<String, Object> createContactListParty(Map<String, Object> input) {
        String contactListId = MarketingData.requireText(input, "contactListId");
        String partyId = MarketingData.requireText(input, "partyId");
        ContactList contactList = data.require(ContactList.class, Map.of("contactListId", contactListId));
        Map<String, Object> parameters = new HashMap<>(input);
        checkContactListMechType(contactList, parameters);
        checkContactListStatusParameter(contactList, parameters);
        if (MarketingData.text(parameters, "preferredContactMechId") == null
                && MarketingData.text(parameters, "contactMechId") == null) {
            party.primaryEmailContactMechId(partyId)
                    .ifPresent(id -> parameters.put("preferredContactMechId", id));
        }
        if (MarketingData.text(parameters, "preferredContactMechId") == null) {
            return Map.of("errorMessage", "MarketingContactMechNotExists");
        }
        ContactListParty row = new ContactListParty();
        data.copyFields(row, parameters, Set.of("fromDate"));
        Timestamp fromDate = MarketingData.timestamp(parameters, "fromDate");
        row.setFromDate(fromDate == null ? MarketingData.now() : fromDate);
        if (data.one(ContactListParty.class, keys(contactListId, partyId, row.getFromDate())).isPresent()) {
            throw new MigrationException(HttpStatus.CONFLICT, "ServiceValueFound");
        }
        data.persist(row);
        Map<String, Object> status = new HashMap<>(data.toMap(row));
        status.put("baseLocation", parameters.get("baseLocation"));
        status.put("optInVerifyCode", parameters.get("optInVerifyCode"));
        if (row.getStatusId() != null) {
            createContactListPartyStatus(status);
        }
        return new LinkedHashMap<>(data.toMap(row));
    }

    public Map<String, Object> updateContactListParty(Map<String, Object> input) {
        String contactListId = MarketingData.requireText(input, "contactListId");
        String partyId = MarketingData.requireText(input, "partyId");
        Map<String, Object> parameters = new HashMap<>(input);
        if (MarketingData.timestamp(parameters, "fromDate") == null) {
            data.first(ContactListPartyStatus.class,
                            "select s from ContactListPartyStatus s where s.contactListId = ?1 and s.partyId = ?2"
                                    + " order by s.statusDate desc", contactListId, partyId)
                    .ifPresent(last -> parameters.put("fromDate", last.getFromDate()));
        }
        Timestamp fromDate = MarketingData.timestamp(parameters, "fromDate");
        if (fromDate == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "ServiceValueNotFound");
        }
        ContactList contactList = data.require(ContactList.class, Map.of("contactListId", contactListId));
        checkContactListMechType(contactList, parameters);
        checkContactListStatusParameter(contactList, parameters);
        ContactListParty row = data.require(ContactListParty.class, keys(contactListId, partyId, fromDate));
        String statusId = MarketingData.text(parameters, "statusId");
        if (statusId != null && !statusId.equals(row.getStatusId())) {
            Map<String, Object> status = new HashMap<>(parameters);
            status.put("fromDate", fromDate);
            if (MarketingData.text(status, "preferredContactMechId") == null) {
                status.put("preferredContactMechId", row.getPreferredContactMechId());
            }
            createContactListPartyStatus(status);
        }
        data.copyFields(row, parameters, data.primaryKeys(ContactListParty.class));
        if (REJECTED.equals(statusId)) {
            Map<String, Object> mailInput = new HashMap<>(data.toMap(row));
            mailInput.put("productStoreId", parameters.get("productStoreId"));
            sendContactListPartyUnSubscribeEmail(mailInput);
        }
        Map<String, Object> result = new LinkedHashMap<>(data.toMap(row));
        result.put("productStoreId", parameters.get("productStoreId"));
        result.put("contactListId", contactListId);
        return result;
    }

    public Map<String, Object> updateContactListPartyNoUserLogin(Map<String, Object> input) {
        String contactListId = MarketingData.requireText(input, "contactListId");
        String partyId = MarketingData.requireText(input, "partyId");
        Timestamp fromDate = MarketingData.timestamp(input, "fromDate");
        String code = MarketingData.text(input, "optInVerifyCode");
        data.require(ContactList.class, Map.of("contactListId", contactListId));
        boolean valid = code != null && fromDate != null
                && data.one(ContactListParty.class,
                                Map.of("contactListId", contactListId, "partyId", partyId, "fromDate", fromDate))
                        .map(current -> allowedVerifiedTransitions(current.getStatusId())
                                .contains(MarketingData.text(input, "statusId")))
                        .orElse(false)
                && data.first(ContactListPartyStatus.class,
                                "select s from ContactListPartyStatus s where s.contactListId = ?1 and s.partyId = ?2"
                                        + " and s.fromDate = ?3 and s.statusId = ?4 order by s.statusDate desc",
                                contactListId, partyId, fromDate, PENDING)
                        .map(ContactListPartyStatus::getOptInVerifyCode)
                        .filter(code::equals)
                        .isPresent();
        if (!valid) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "MarketingContactListInvalidVerifyCode");
        }
        return updateContactListParty(input);
    }

    private static Set<String> allowedVerifiedTransitions(String currentStatusId) {
        if (PENDING.equals(currentStatusId)) {
            return Set.of(ACCEPTED, REJECTED);
        }
        if (UNSUBS_PENDING.equals(currentStatusId)) {
            return Set.of(UNSUBSCRIBED);
        }
        return Set.of();
    }

    public Map<String, Object> unsubscribeContactListParty(Map<String, Object> input) {
        String email = MarketingData.text(input, "email");
        if (!MarketingData.isEmail(email)) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "MarketingCampaignInvalidEmailInput");
        }
        String contactListId = MarketingData.requireText(input, "contactListId");
        String partyId = MarketingData.text(input, "partyId");
        if (partyId == null) {
            partyId = "_NA_";
        }
        List<PartyPort.EmailContact> contacts = party.findActiveEmailContacts(partyId, email, "OTHER_EMAIL");
        if (contacts.isEmpty()) {
            contacts = party.findActiveEmailContacts(null, email, null);
        }
        if (contacts.isEmpty()) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "MarketingContactListAnotherEmailNotExist");
        }
        PartyPort.EmailContact contact = contacts.get(0);
        Map<String, Object> update = new HashMap<>();
        update.put("contactListId", contactListId);
        update.put("partyId", contact.partyId());
        update.put("preferredContactMechId", contact.contactMechId());
        update.put("statusId", UNSUBS_PENDING);
        update.put("baseLocation", input.get("baseLocation"));
        return updateContactListParty(update);
    }

    public Map<String, Object> unsubscribeContactListPartyContachMech(Map<String, Object> input) {
        String contactMechId = MarketingData.requireText(input, "preferredContactMechId");
        String partyId = MarketingData.requireText(input, "partyId");
        PartyPort.EmailContact contact = party.findActiveEmailContact(partyId, contactMechId)
                .orElseThrow(() -> new MigrationException(HttpStatus.NOT_FOUND, "PartyContactMech not found"));
        Map<String, Object> unsubscribe = new HashMap<>(input);
        unsubscribe.put("email", contact.infoString());
        return unsubscribeContactListParty(unsubscribe);
    }

    public Map<String, Object> updatePartyEmailContactListParty(Map<String, Object> input) {
        String contactMechId = MarketingData.requireText(input, "contactMechId");
        String oldContactMechId = MarketingData.requireText(input, "oldContactMechId");
        List<ContactListParty> rows = data.query(ContactListParty.class,
                "select c from ContactListParty c where c.preferredContactMechId = ?1", oldContactMechId);
        rows.forEach(row -> row.setPreferredContactMechId(contactMechId));
        return Map.of("updatedCount", rows.size());
    }

    public Map<String, Object> createContactListPartyStatus(Map<String, Object> input) {
        String contactListId = MarketingData.requireText(input, "contactListId");
        String partyId = MarketingData.requireText(input, "partyId");
        Timestamp fromDate = MarketingData.timestamp(input, "fromDate");
        MarketingData.requireText(input, "preferredContactMechId");
        if (fromDate == null) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Missing required input: fromDate");
        }
        ContactListPartyStatus status = new ContactListPartyStatus();
        data.copyFields(status, input, Set.of("fromDate", "statusDate", "optInVerifyCode", "setByUserLoginId"));
        status.setContactListId(contactListId);
        status.setPartyId(partyId);
        status.setFromDate(fromDate);
        status.setStatusDate(java.sql.Timestamp.from(java.time.Instant.now()));
        status.setSetByUserLoginId(MarketingData.currentUserLoginId());
        if (PENDING.equals(status.getStatusId())) {
            status.setOptInVerifyCode(String.format("%010d", RANDOM.nextLong(10_000_000_000L)));
        } else if (ACCEPTED.equals(status.getStatusId())) {
            checkVerifyCode(contactListId, partyId, fromDate, MarketingData.text(input, "optInVerifyCode"));
        }
        data.persist(status);
        runStatusEcas(status, input);
        return new LinkedHashMap<>(data.toMap(status));
    }

    public Map<String, Object> sendContactListPartyVerifyEmail(Map<String, Object> input) {
        ContactList contactList = contactList(input);
        ContactListParty row = contactListParty(input);
        if (contactList.getVerifyEmailFrom() == null || contactList.getVerifyEmailScreen() == null
                || contactList.getVerifyEmailSubject() == null || contactList.getVerifyEmailWebSiteId() == null) {
            return Map.of("mailSent", false);
        }
        ContactListPartyStatus pending = latestStatus(row, PENDING);
        Map<String, Object> body = body(input, contactList, row, pending);
        mail.sendMailFromScreen(new MailPort.MailRequest("VERIFY_CONT_LIST",
                party.infoString(row.getPreferredContactMechId()).orElse(null),
                contactList.getVerifyEmailFrom(), contactList.getVerifyEmailSubject(),
                contactList.getVerifyEmailScreen(), contactList.getVerifyEmailWebSiteId(), null, body));
        return Map.of("mailSent", true);
    }

    public Map<String, Object> sendContactListPartySubscribeEmail(Map<String, Object> input) {
        return sendStoreMail(input, "SUB_CONT_LIST_NOTI", ACCEPTED, false);
    }

    public Map<String, Object> sendContactListPartyUnSubscribeVerifyEmail(Map<String, Object> input) {
        return sendStoreMail(input, "UNSUB_CONT_LIST_VERI", ACCEPTED, true);
    }

    public Map<String, Object> sendContactListPartyUnSubscribeEmail(Map<String, Object> input) {
        return sendStoreMail(input, "UNSUB_CONT_LIST_NOTI", null, false);
    }

    private Map<String, Object> sendStoreMail(
            Map<String, Object> input, String emailType, String statusForBody, boolean createCommEvent) {
        ContactList contactList = contactList(input);
        String productStoreId = MarketingData.text(input, "productStoreId");
        if (productStoreId == null) {
            productStoreId = stores.productStoreIdOfWebSite(contactList.getVerifyEmailWebSiteId()).orElse(null);
        }
        var setting = stores.storeEmail(productStoreId, emailType);
        if (setting.isEmpty() || setting.get().bodyScreenLocation() == null) {
            return Map.of("mailSent", false);
        }
        String preferredContactMechId = MarketingData.text(input, "preferredContactMechId");
        String sendTo = preferredContactMechId == null ? null : party.infoString(preferredContactMechId).orElse(null);
        String subject = setting.get().subject() + " " + contactList.getContactListName();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("contactListId", contactList.getContactListId());
        body.put("partyId", input.get("partyId"));
        String communicationEventId = null;
        if (statusForBody != null) {
            ContactListParty row = contactListParty(input);
            body.putAll(body(input, contactList, row, latestStatus(row, statusForBody)));
            if (createCommEvent) {
                Map<String, Object> event = new HashMap<>();
                event.put("contactListId", contactList.getContactListId());
                event.put("partyIdTo", row.getPartyId());
                event.put("contactMechIdTo", row.getPreferredContactMechId());
                event.put("fromString", setting.get().fromAddress());
                event.put("toString", sendTo);
                event.put("subject", subject);
                communicationEventId = communicationEvents.createCommunicationEvent(event);
            }
        }
        mail.sendMailFromScreen(new MailPort.MailRequest(emailType, sendTo, setting.get().fromAddress(), subject,
                setting.get().bodyScreenLocation(), contactList.getVerifyEmailWebSiteId(), communicationEventId, body));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("mailSent", true);
        result.put("communicationEventId", communicationEventId);
        return result;
    }

    private void runStatusEcas(ContactListPartyStatus status, Map<String, Object> input) {
        Map<String, Object> mailInput = new HashMap<>(data.toMap(status));
        mailInput.put("preferredContactMechId", input.get("preferredContactMechId"));
        mailInput.put("productStoreId", input.get("productStoreId"));
        mailInput.put("baseLocation", input.get("baseLocation"));
        switch (status.getStatusId() == null ? "" : status.getStatusId()) {
            case PENDING -> sendContactListPartyVerifyEmail(mailInput);
            case ACCEPTED -> sendContactListPartySubscribeEmail(mailInput);
            case UNSUBSCRIBED -> sendContactListPartyUnSubscribeEmail(mailInput);
            case UNSUBS_PENDING -> sendContactListPartyUnSubscribeVerifyEmail(mailInput);
            default -> { }
        }
    }

    private void checkVerifyCode(String contactListId, String partyId, Timestamp fromDate, String code) {
        data.first(ContactListPartyStatus.class,
                        "select s from ContactListPartyStatus s where s.contactListId = ?1 and s.partyId = ?2"
                                + " and s.fromDate = ?3 and s.statusId = ?4 order by s.statusDate desc",
                        contactListId, partyId, fromDate, PENDING)
                .map(ContactListPartyStatus::getOptInVerifyCode)
                .filter(expected -> !expected.isBlank())
                .ifPresent(expected -> {
                    if (code == null) {
                        throw new MigrationException(HttpStatus.BAD_REQUEST, "MarketingContactListOptInVerifyCodeIsMandatory");
                    }
                    if (!expected.equals(code)) {
                        throw new MigrationException(HttpStatus.BAD_REQUEST, "MarketingContactListOptInVerifyCodeNotCorrect");
                    }
                });
    }

    private void checkContactListMechType(ContactList contactList, Map<String, Object> parameters) {
        String preferred = MarketingData.text(parameters, "preferredContactMechId");
        if (preferred == null) {
            return;
        }
        String type = party.contactMechTypeId(preferred).orElse(null);
        if (contactList.getContactMechTypeId() != null && !contactList.getContactMechTypeId().equals(type)) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "MarketingContactMechNotRightForContactList");
        }
    }

    private static void checkContactListStatusParameter(ContactList contactList, Map<String, Object> parameters) {
        if (!"EMAIL_ADDRESS".equals(contactList.getContactMechTypeId())
                && PENDING.equals(MarketingData.text(parameters, "statusId"))) {
            parameters.put("statusId", ACCEPTED);
        }
    }

    private ContactList contactList(Map<String, Object> input) {
        return data.require(ContactList.class, Map.of("contactListId", MarketingData.requireText(input, "contactListId")));
    }

    private ContactListParty contactListParty(Map<String, Object> input) {
        Timestamp fromDate = MarketingData.timestamp(input, "fromDate");
        if (fromDate == null) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Missing required input: fromDate");
        }
        return data.require(ContactListParty.class, keys(
                MarketingData.requireText(input, "contactListId"), MarketingData.requireText(input, "partyId"), fromDate));
    }

    private ContactListPartyStatus latestStatus(ContactListParty row, String statusId) {
        return data.first(ContactListPartyStatus.class,
                "select s from ContactListPartyStatus s where s.contactListId = ?1 and s.partyId = ?2"
                        + " and s.fromDate = ?3 and s.statusId = ?4 order by s.statusDate desc",
                row.getContactListId(), row.getPartyId(), row.getFromDate(), statusId).orElse(null);
    }

    private Map<String, Object> body(Map<String, Object> input, ContactList contactList, ContactListParty row,
            ContactListPartyStatus status) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("contactListId", contactList.getContactListId());
        body.put("partyId", row.getPartyId());
        body.put("contactListName", contactList.getContactListName());
        body.put("statusId", status == null ? null : status.getStatusId());
        body.put("optInVerifyCode", status == null ? null : status.getOptInVerifyCode());
        body.put("baseLocation", input.get("baseLocation"));
        return body;
    }

    static Map<String, Object> keys(String contactListId, String partyId, Timestamp fromDate) {
        return Map.of("contactListId", contactListId, "partyId", partyId, "fromDate", fromDate);
    }
}
