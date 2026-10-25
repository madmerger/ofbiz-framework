package org.apache.ofbiz.migration.humanres.adapter.local.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity(name = "LocalPartyRelationship")
@Table(name = "PARTY_RELATIONSHIP")
@IdClass(PartyRelationshipRecord.Key.class)
public class PartyRelationshipRecord {
    @Id
    @Column(name = "PARTY_ID_FROM", length = 20)
    String partyIdFrom;
    @Id
    @Column(name = "PARTY_ID_TO", length = 20)
    String partyIdTo;
    @Id
    @Column(name = "PARTY_RELATIONSHIP_TYPE_ID", length = 20)
    String partyRelationshipTypeId;
    @Id
    @Column(name = "FROM_DATE")
    Timestamp fromDate;
    @Column(name = "ROLE_TYPE_ID_FROM", length = 20)
    String roleTypeIdFrom;
    @Column(name = "ROLE_TYPE_ID_TO", length = 20)
    String roleTypeIdTo;
    @Column(name = "RELATIONSHIP_NAME", length = 100)
    String relationshipName;
    @Column(name = "THRU_DATE")
    Timestamp thruDate;

    protected PartyRelationshipRecord() {}

    public PartyRelationshipRecord(
            String from, String to, String type, String roleFrom, String roleTo, String name, Timestamp start) {
        partyIdFrom = from;
        partyIdTo = to;
        partyRelationshipTypeId = type;
        roleTypeIdFrom = roleFrom;
        roleTypeIdTo = roleTo;
        relationshipName = name;
        fromDate = start;
    }

    public String getPartyIdFrom() {
        return partyIdFrom;
    }

    public String getPartyIdTo() {
        return partyIdTo;
    }

    public Timestamp getFromDate() {
        return fromDate;
    }

    public static class Key implements Serializable {
        private static final long serialVersionUID = 1L;
        public String partyIdFrom;
        public String partyIdTo;
        public String partyRelationshipTypeId;
        public Timestamp fromDate;

        public Key() {}

        @Override
        public boolean equals(Object other) {
            return other instanceof Key key
                    && Objects.equals(partyIdFrom, key.partyIdFrom)
                    && Objects.equals(partyIdTo, key.partyIdTo)
                    && Objects.equals(partyRelationshipTypeId, key.partyRelationshipTypeId)
                    && Objects.equals(fromDate, key.fromDate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(partyIdFrom, partyIdTo, partyRelationshipTypeId, fromDate);
        }
    }
}
