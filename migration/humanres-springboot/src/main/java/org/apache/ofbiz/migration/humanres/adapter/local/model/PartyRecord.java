package org.apache.ofbiz.migration.humanres.adapter.local.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "LocalParty")
@Table(name = "PARTY")
public class PartyRecord {
    @Id
    @Column(name = "PARTY_ID", length = 20)
    String partyId;
    @Column(name = "PARTY_TYPE_ID", length = 20)
    String partyTypeId;

    protected PartyRecord() {}

    public PartyRecord(String partyId, String partyTypeId) {
        this.partyId = partyId;
        this.partyTypeId = partyTypeId;
    }

    public String getPartyId() {
        return partyId;
    }
}
