package org.apache.ofbiz.migration.humanres.adapter.local;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Map;
import java.util.List;
import jakarta.persistence.EntityManager;
import org.apache.ofbiz.migration.humanres.AccountingPort;
import org.apache.ofbiz.migration.humanres.EntityRegistry;
import org.apache.ofbiz.migration.humanres.adapter.local.model.PartyAcctgPreferenceRecord;
import org.apache.ofbiz.migration.humanres.adapter.local.model.RateAmountRecord;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class LocalAccountingAdapter implements AccountingPort {
    private final EntityManager entityManager;

    public LocalAccountingAdapter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void updateRateAmount(Map<String, ?> values) {
        String rateTypeId = required(values, "rateTypeId");
        String currency = required(values, "rateCurrencyUomId");
        Timestamp fromDate = values.get("fromDate") == null
                ? EntityRegistry.now()
                : (Timestamp) new EntityRegistryConverter().timestamp(values.get("fromDate"));
        RateAmountRecord.Key key = new RateAmountRecord.Key();
        key.rateTypeId = rateTypeId;
        key.rateCurrencyUomId = currency;
        key.fromDate = fromDate;
        RateAmountRecord existing = entityManager.find(RateAmountRecord.class, key);
        BigDecimal amount = values.get("rateAmount") == null
                ? null : new BigDecimal(values.get("rateAmount").toString());
        if (existing == null) {
            entityManager.persist(new RateAmountRecord(rateTypeId, currency, fromDate, amount));
        } else {
            existing.updateAmount(amount);
        }
    }

    @Override
    public void expireRateAmount(Map<String, ?> values) {
        String rateTypeId = stringValue(values, "rateTypeId");
        String currency = stringValue(values, "rateCurrencyUomId");
        Object rawDate = values.containsKey("fromDate") ? values.get("fromDate") : values.get("rateAmountFromDate");
        if (rateTypeId == null || currency == null || rawDate == null) {
            return;
        }
        Timestamp fromDate = (Timestamp) new EntityRegistryConverter().timestamp(rawDate);
        RateAmountRecord.Key key = new RateAmountRecord.Key();
        key.rateTypeId = rateTypeId;
        key.rateCurrencyUomId = currency;
        key.fromDate = fromDate;
        RateAmountRecord existing = entityManager.find(RateAmountRecord.class, key);
        if (existing != null) {
            existing.expireAt(EntityRegistry.now());
        }
    }

    @Override
    public String baseCurrency(String partyId) {
        PartyAcctgPreferenceRecord preferences = entityManager.find(PartyAcctgPreferenceRecord.class, partyId);
        return preferences == null ? null : preferences.getBaseCurrencyUomId();
    }

    @Override
    public List<Rate> currentRates(String positionTypeId, String currencyUomId) {
        Timestamp now = EntityRegistry.now();
        return entityManager.createQuery(
                        "select rate from LocalRateAmount rate "
                                + "where rate.rateTypeId = :positionTypeId "
                                + "and rate.rateCurrencyUomId = :currency "
                                + "and rate.fromDate <= :now "
                                + "and (rate.thruDate is null or rate.thruDate > :now)",
                        RateAmountRecord.class)
                .setParameter("positionTypeId", positionTypeId)
                .setParameter("currency", currencyUomId)
                .setParameter("now", now)
                .getResultList()
                .stream()
                .map(rate -> new Rate(
                        rate.getRateTypeId(), rate.getRateCurrencyUomId(),
                        rate.getFromDate(), rate.getRateAmount()))
                .toList();
    }

    private static String required(Map<String, ?> values, String key) {
        String value = stringValue(values, key);
        if (value == null || value.isBlank()) {
            throw org.apache.ofbiz.migration.humanres.HumanResException.badRequest(
                    "Missing required IN parameter: " + key);
        }
        return value;
    }

    private static String stringValue(Map<String, ?> values, String key) {
        Object value = values.get(key);
        return value == null ? null : value.toString();
    }

    private static class EntityRegistryConverter {
        Object timestamp(Object value) {
            return new EntityRegistryConverterHelper().toTimestamp(value);
        }
    }

    private static class EntityRegistryConverterHelper {
        Timestamp toTimestamp(Object value) {
            if (value instanceof Timestamp timestamp) {
                return timestamp;
            }
            try {
                return Timestamp.from(java.time.Instant.parse(value.toString()));
            } catch (RuntimeException ignored) {
                return Timestamp.valueOf(value.toString().replace('T', ' ').replace("Z", ""));
            }
        }
    }
}
