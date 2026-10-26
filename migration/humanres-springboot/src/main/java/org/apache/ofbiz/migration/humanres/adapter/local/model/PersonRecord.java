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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "LocalPerson")
@Table(name = "PERSON")
public class PersonRecord {
    @Id
    @Column(name = "PARTY_ID", length = 20)
    String partyId;
    @Column(name = "FIRST_NAME", length = 100)
    String firstName;
    @Column(name = "MIDDLE_NAME", length = 100)
    String middleName;
    @Column(name = "LAST_NAME", length = 100)
    String lastName;
    @Column(name = "GENDER", length = 1)
    String gender;

    protected PersonRecord() {}

    public PersonRecord(String partyId, String firstName, String middleName, String lastName, String gender) {
        this.partyId = partyId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
