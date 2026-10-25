package org.apache.ofbiz.migration.humanres;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface PartyPort {
    void ensureRole(String partyId, String roleTypeId);

    String createEmployee(Map<String, ?> input);

    void createRelationship(
            String partyIdFrom,
            String partyIdTo,
            String relationshipTypeId,
            String roleTypeIdFrom,
            String roleTypeIdTo,
            String relationshipName,
            Timestamp fromDate);

    void removeRelationship(String partyIdFrom, String partyIdTo);

    boolean isPartyGroup(String partyId);

    String partyName(String partyId);

    List<Relationship> childOrganizations(String parentPartyId);

    record Relationship(String partyIdFrom, String partyIdTo, Timestamp fromDate) {}
}
