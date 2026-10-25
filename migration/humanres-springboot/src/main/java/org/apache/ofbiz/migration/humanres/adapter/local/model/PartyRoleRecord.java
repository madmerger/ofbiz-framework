package org.apache.ofbiz.migration.humanres.adapter.local.model;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity(name = "LocalPartyRole")
@Table(name = "PARTY_ROLE")
@IdClass(PartyRoleRecord.Key.class)
public class PartyRoleRecord {
    @Id
    @Column(name = "PARTY_ID", length = 20)
    String partyId;
    @Id
    @Column(name = "ROLE_TYPE_ID", length = 20)
    String roleTypeId;

    protected PartyRoleRecord() {}

    public PartyRoleRecord(String partyId, String roleTypeId) {
        this.partyId = partyId;
        this.roleTypeId = roleTypeId;
    }

    public static class Key implements Serializable {
        private static final long serialVersionUID = 1L;
        public String partyId;
        public String roleTypeId;

        public Key() {}

        @Override
        public boolean equals(Object other) {
            return other instanceof Key key
                    && Objects.equals(partyId, key.partyId)
                    && Objects.equals(roleTypeId, key.roleTypeId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(partyId, roleTypeId);
        }
    }
}
