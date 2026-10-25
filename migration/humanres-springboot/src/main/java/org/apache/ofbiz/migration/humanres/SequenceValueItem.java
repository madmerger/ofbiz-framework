package org.apache.ofbiz.migration.humanres;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "SEQUENCE_VALUE_ITEM")
public class SequenceValueItem {
    @Id
    @Column(name = "SEQ_NAME", length = 60, nullable = false)
    private String seqName;

    @Column(name = "SEQ_ID")
    private Long seqId;

    protected SequenceValueItem() {
    }

    public SequenceValueItem(String seqName, Long seqId) {
        this.seqName = seqName;
        this.seqId = seqId;
    }

    public String getSeqName() {
        return seqName;
    }

    public Long getSeqId() {
        return seqId;
    }

    public void setSeqId(Long seqId) {
        this.seqId = seqId;
    }
}
