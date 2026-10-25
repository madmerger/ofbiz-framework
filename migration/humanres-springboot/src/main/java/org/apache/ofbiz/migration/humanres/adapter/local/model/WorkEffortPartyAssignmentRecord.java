package org.apache.ofbiz.migration.humanres.adapter.local.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity(name = "LocalWorkEffortPartyAssignment")
@Table(name = "WORK_EFFORT_PARTY_ASSIGNMENT")
@IdClass(WorkEffortPartyAssignmentRecord.Key.class)
public class WorkEffortPartyAssignmentRecord {
    @Id
    @Column(name = "WORK_EFFORT_ID", length = 20)
    String workEffortId;
    @Id
    @Column(name = "PARTY_ID", length = 20)
    String partyId;
    @Id
    @Column(name = "ROLE_TYPE_ID", length = 20)
    String roleTypeId;
    @Id
    @Column(name = "FROM_DATE")
    Timestamp fromDate;

    protected WorkEffortPartyAssignmentRecord() {}

    public WorkEffortPartyAssignmentRecord(String effortId, String party, String role, Timestamp start) {
        workEffortId = effortId;
        partyId = party;
        roleTypeId = role;
        fromDate = start;
    }

    public static class Key implements Serializable {
        private static final long serialVersionUID = 1L;
        public String workEffortId;
        public String partyId;
        public String roleTypeId;
        public Timestamp fromDate;

        public Key() {}

        @Override
        public boolean equals(Object other) {
            return other instanceof Key key
                    && Objects.equals(workEffortId, key.workEffortId)
                    && Objects.equals(partyId, key.partyId)
                    && Objects.equals(roleTypeId, key.roleTypeId)
                    && Objects.equals(fromDate, key.fromDate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(workEffortId, partyId, roleTypeId, fromDate);
        }
    }
}
