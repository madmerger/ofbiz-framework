package org.apache.ofbiz.migration.humanres;

import java.sql.Timestamp;

public interface WorkEffortPort {
    boolean publicHolidayExists(Timestamp startDate);

    String createPublicHoliday(String name, Timestamp startDate, Timestamp endDate, String assignedPartyId);
}
