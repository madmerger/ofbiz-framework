package org.apache.ofbiz.migration.humanres.adapter.local.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "LocalPartyAcctgPreference")
@Table(name = "PARTY_ACCTG_PREFERENCE")
public class PartyAcctgPreferenceRecord {
    @Id
    @Column(name = "PARTY_ID", length = 20)
    String partyId;
    @Column(name = "BASE_CURRENCY_UOM_ID", length = 20)
    String baseCurrencyUomId;

    protected PartyAcctgPreferenceRecord() {}

    public PartyAcctgPreferenceRecord(String partyId, String currency) {
        this.partyId = partyId;
        baseCurrencyUomId = currency;
    }

    public String getBaseCurrencyUomId() {
        return baseCurrencyUomId;
    }
}
