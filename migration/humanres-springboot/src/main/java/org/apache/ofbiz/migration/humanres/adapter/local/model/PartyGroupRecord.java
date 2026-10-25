package org.apache.ofbiz.migration.humanres.adapter.local.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "LocalPartyGroup")
@Table(name = "PARTY_GROUP")
public class PartyGroupRecord {
    @Id
    @Column(name = "PARTY_ID", length = 20)
    String partyId;
    @Column(name = "GROUP_NAME", length = 100)
    String groupName;
    @Column(name = "GROUP_NAME_LOCAL", length = 100)
    String groupNameLocal;

    protected PartyGroupRecord() {}

    public PartyGroupRecord(String partyId, String groupName) {
        this.partyId = partyId;
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }
}
