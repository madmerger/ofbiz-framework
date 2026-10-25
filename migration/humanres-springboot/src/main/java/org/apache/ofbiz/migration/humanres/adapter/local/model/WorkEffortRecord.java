package org.apache.ofbiz.migration.humanres.adapter.local.model;

import java.sql.Timestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "LocalWorkEffort")
@Table(name = "WORK_EFFORT")
public class WorkEffortRecord {
    @Id
    @Column(name = "WORK_EFFORT_ID", length = 20)
    String workEffortId;
    @Column(name = "WORK_EFFORT_TYPE_ID", length = 20)
    String workEffortTypeId;
    @Column(name = "WORK_EFFORT_NAME", length = 100)
    String workEffortName;
    @Column(name = "ESTIMATED_START_DATE")
    Timestamp estimatedStartDate;
    @Column(name = "ESTIMATED_COMPLETION_DATE")
    Timestamp estimatedCompletionDate;

    protected WorkEffortRecord() {}

    public WorkEffortRecord(String id, String type, String name, Timestamp start, Timestamp end) {
        workEffortId = id;
        workEffortTypeId = type;
        workEffortName = name;
        estimatedStartDate = start;
        estimatedCompletionDate = end;
    }
}
