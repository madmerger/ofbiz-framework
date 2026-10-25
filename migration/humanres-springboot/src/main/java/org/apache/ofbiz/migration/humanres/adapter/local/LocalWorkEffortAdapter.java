package org.apache.ofbiz.migration.humanres.adapter.local;

import java.sql.Timestamp;
import jakarta.persistence.EntityManager;
import org.apache.ofbiz.migration.humanres.SequenceService;
import org.apache.ofbiz.migration.humanres.WorkEffortPort;
import org.apache.ofbiz.migration.humanres.adapter.local.model.WorkEffortPartyAssignmentRecord;
import org.apache.ofbiz.migration.humanres.adapter.local.model.WorkEffortRecord;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class LocalWorkEffortAdapter implements WorkEffortPort {
    private final EntityManager entityManager;
    private final SequenceService sequenceService;

    public LocalWorkEffortAdapter(EntityManager entityManager, SequenceService sequenceService) {
        this.entityManager = entityManager;
        this.sequenceService = sequenceService;
    }

    @Override
    public boolean publicHolidayExists(Timestamp startDate) {
        Long count = entityManager.createQuery(
                        "select count(effort) from LocalWorkEffort effort "
                                + "where effort.workEffortTypeId = 'PUBLIC_HOLIDAY' "
                                + "and effort.estimatedStartDate = :startDate",
                        Long.class)
                .setParameter("startDate", startDate)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public String createPublicHoliday(
            String name, Timestamp startDate, Timestamp endDate, String assignedPartyId) {
        String workEffortId = sequenceService.nextId("WorkEffort");
        entityManager.persist(new WorkEffortRecord(
                workEffortId, "PUBLIC_HOLIDAY", name, startDate, endDate));
        if (assignedPartyId != null && !assignedPartyId.isBlank()) {
            entityManager.persist(new WorkEffortPartyAssignmentRecord(
                    workEffortId, assignedPartyId, "INTERNAL_ORGANIZATIO", startDate));
        }
        return workEffortId;
    }
}
