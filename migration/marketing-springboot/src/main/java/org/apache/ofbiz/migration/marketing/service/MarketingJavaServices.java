package org.apache.ofbiz.migration.marketing.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.datamodel.entity.ContactList;
import org.apache.ofbiz.migration.datamodel.entity.ContactListParty;
import org.apache.ofbiz.migration.datamodel.entity.ContactListPartyStatus;
import org.apache.ofbiz.migration.marketing.MarketingData;
import org.apache.ofbiz.migration.marketing.port.PartyPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Main-flow Java services of MarketingServices.java (newsletter sign-up and pending request removal).
 */
@Service
public class MarketingJavaServices {

    private final MarketingData data;
    private final PartyPort party;
    private final ContactListServices contactLists;

    public MarketingJavaServices(MarketingData data, PartyPort party, ContactListServices contactLists) {
        this.data = data;
        this.party = party;
        this.contactLists = contactLists;
    }

    public Map<String, Object> signUpForContactList(Map<String, Object> input) {
        String email = MarketingData.text(input, "email");
        if (!MarketingData.isEmail(email)) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "MarketingCampaignInvalidEmailInput");
        }
        String contactListId = MarketingData.requireText(input, "contactListId");
        if (data.one(ContactList.class, Map.of("contactListId", contactListId)).isEmpty()) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "MarketingContactListNotFound");
        }
        String partyId = MarketingData.text(input, "partyId");
        if (partyId == null) {
            partyId = party.findActiveEmailContacts(null, email, "PRIMARY_EMAIL").stream()
                    .map(PartyPort.EmailContact::partyId).findFirst().orElse("_NA_");
        }
        Timestamp fromDate = MarketingData.now();
        String contactMechId = party.createPartyEmailAddress(partyId, email, "OTHER_EMAIL", fromDate);
        Timestamp now = MarketingData.now();
        List<ContactListParty> existing = data.query(ContactListParty.class,
                        "select c from ContactListParty c where c.contactListId = ?1 and c.partyId = ?2"
                                + " and c.preferredContactMechId = ?3", contactListId, partyId, contactMechId)
                .stream().filter(row -> MarketingData.active(row.getFromDate(), row.getThruDate(), now)).toList();
        if (existing.stream().anyMatch(row -> ContactListServices.ACCEPTED.equals(row.getStatusId()))) {
            throw new MigrationException(HttpStatus.CONFLICT, "MarketingNewsletterSubscriptionAlreadyExistsMsg");
        }
        String message = "MarketingNewsletterSubscriptionRequestSuccessMessage";
        for (ContactListParty pending : existing) {
            if (ContactListServices.PENDING.equals(pending.getStatusId())) {
                message = "MarketingNewsletterSubscriptionReqstAlreadyExistsMsg";
                deleteContactListParty(data.toMap(pending));
            }
        }
        Map<String, Object> create = new HashMap<>();
        create.put("contactListId", contactListId);
        create.put("partyId", partyId);
        create.put("fromDate", fromDate);
        create.put("statusId", ContactListServices.PENDING);
        create.put("preferredContactMechId", contactMechId);
        create.put("baseLocation", input.get("baseLocation"));
        Map<String, Object> created = contactLists.createContactListParty(create);
        Map<String, Object> result = new LinkedHashMap<>(created);
        result.put("successMessage", message);
        return result;
    }

    public Map<String, Object> deleteContactListParty(Map<String, Object> input) {
        String contactListId = MarketingData.requireText(input, "contactListId");
        String partyId = MarketingData.requireText(input, "partyId");
        Timestamp fromDate = MarketingData.timestamp(input, "fromDate");
        if (fromDate == null) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Missing required input: fromDate");
        }
        int removed = 0;
        var row = data.one(ContactListParty.class, ContactListServices.keys(contactListId, partyId, fromDate))
                .filter(found -> MarketingData.active(found.getFromDate(), found.getThruDate(), MarketingData.now()));
        if (row.isPresent()) {
            List<ContactListPartyStatus> statuses = data.query(ContactListPartyStatus.class,
                    "select s from ContactListPartyStatus s where s.contactListId = ?1 and s.partyId = ?2"
                            + " and s.fromDate = ?3", contactListId, partyId, fromDate);
            data.removeAll(statuses);
            if (!statuses.isEmpty()) {
                data.remove(row.get());
                removed = 1;
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("successMessage", "MarketingNewsletterSubscriptionPendingRequestDeletedMessage");
        result.put("removedCount", removed);
        return result;
    }
}
