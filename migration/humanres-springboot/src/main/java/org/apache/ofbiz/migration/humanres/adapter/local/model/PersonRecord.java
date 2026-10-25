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
