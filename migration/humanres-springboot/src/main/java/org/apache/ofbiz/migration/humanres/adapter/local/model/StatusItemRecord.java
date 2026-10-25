package org.apache.ofbiz.migration.humanres.adapter.local.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "LocalStatusItem")
@Table(name = "STATUS_ITEM")
public class StatusItemRecord {
    @Id
    @Column(name = "STATUS_ID", length = 20)
    String statusId;
    @Column(name = "STATUS_TYPE_ID", length = 20)
    String statusTypeId;
    @Column(name = "DESCRIPTION", length = 255)
    String description;
    @Column(name = "SEQUENCE_ID", length = 20)
    String sequenceId;

    protected StatusItemRecord() {}

    public StatusItemRecord(String statusId, String type, String description, String sequenceId) {
        this.statusId = statusId;
        statusTypeId = type;
        this.description = description;
        this.sequenceId = sequenceId;
    }
}
