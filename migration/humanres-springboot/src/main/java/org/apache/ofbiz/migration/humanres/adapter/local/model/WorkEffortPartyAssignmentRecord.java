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
