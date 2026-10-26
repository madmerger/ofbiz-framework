package org.apache.ofbiz.migration.marketing.port;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Party component services used by marketing (getPartyEmail, createPartyEmailAddress, createPerson, ...).
 */
public interface PartyPort {

    Optional<String> primaryEmailContactMechId(String partyId);

    List<EmailContact> findActiveEmailContacts(String partyId, String email, String contactMechPurposeTypeId);

    Optional<EmailContact> findActiveEmailContact(String partyId, String contactMechId);

    Optional<String> contactMechTypeId(String contactMechId);

    Optional<String> infoString(String contactMechId);

    String createPartyEmailAddress(String partyId, String email, String contactMechPurposeTypeId, Timestamp fromDate);

    String createPerson(Map<String, Object> person);

    void ensurePartyRole(String partyId, String roleTypeId);

    void createPartyRelationship(
            String partyIdFrom, String partyIdTo, String roleTypeIdFrom, String roleTypeIdTo, String typeId);

    void updatePartyContactMech(
            String partyId, String contactMechId, String newContactMechId, String contactMechPurposeTypeId);

    void deletePartyContactMech(String partyId, String contactMechId);

    void setPartyStatus(String partyId, String statusId);

    record EmailContact(String partyId, String contactMechId, String infoString) { }
}
