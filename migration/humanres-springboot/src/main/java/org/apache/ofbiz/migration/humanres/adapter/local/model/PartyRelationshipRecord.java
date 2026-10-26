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
    @Column(name = "PARTY_RELATIONSHIP_TYPE_ID", length = 20)
    String partyRelationshipTypeId;
    @Id
    @Column(name = "ROLE_TYPE_ID_FROM", length = 20)
    String roleTypeIdFrom;
    @Id
    @Column(name = "ROLE_TYPE_ID_TO", length = 20)
    String roleTypeIdTo;
    @Id
    @Column(name = "FROM_DATE")
    Timestamp fromDate;
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
        public String roleTypeIdFrom;
        public String roleTypeIdTo;
        public Timestamp fromDate;

        public Key() {}

        @Override
        public boolean equals(Object other) {
            return other instanceof Key key
                    && Objects.equals(partyIdFrom, key.partyIdFrom)
                    && Objects.equals(partyIdTo, key.partyIdTo)
                    && Objects.equals(roleTypeIdFrom, key.roleTypeIdFrom)
                    && Objects.equals(roleTypeIdTo, key.roleTypeIdTo)
                    && Objects.equals(fromDate, key.fromDate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(partyIdFrom, partyIdTo, roleTypeIdFrom, roleTypeIdTo, fromDate);
        }
    }
}
