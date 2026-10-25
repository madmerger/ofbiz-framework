package org.apache.ofbiz.migration.humanres.adapter.local.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "LocalRoleType")
@Table(name = "ROLE_TYPE")
public class RoleTypeRecord {
    @Id
    @Column(name = "ROLE_TYPE_ID", length = 20)
    String roleTypeId;
    @Column(name = "DESCRIPTION", length = 255)
    String description;

    protected RoleTypeRecord() {}

    public RoleTypeRecord(String roleTypeId, String description) {
        this.roleTypeId = roleTypeId;
        this.description = description;
    }
}
