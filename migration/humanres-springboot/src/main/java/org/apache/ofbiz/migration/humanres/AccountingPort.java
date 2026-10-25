package org.apache.ofbiz.migration.humanres;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Map;
import java.util.List;

public interface AccountingPort {
    void updateRateAmount(Map<String, ?> values);

    void expireRateAmount(Map<String, ?> values);

    String baseCurrency(String partyId);

    List<Rate> currentRates(String positionTypeId, String currencyUomId);

    record Rate(String rateTypeId, String currencyUomId, Timestamp fromDate, BigDecimal amount) {}
}
