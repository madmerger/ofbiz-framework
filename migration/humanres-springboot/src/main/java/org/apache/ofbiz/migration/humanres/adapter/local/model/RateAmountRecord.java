/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.apache.ofbiz.migration.humanres.adapter.local.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity(name = "LocalRateAmount")
@Table(name = "RATE_AMOUNT")
@IdClass(RateAmountRecord.Key.class)
public class RateAmountRecord {
    @Id
    @Column(name = "RATE_TYPE_ID", length = 20)
    String rateTypeId;
    @Id
    @Column(name = "RATE_CURRENCY_UOM_ID", length = 20)
    String rateCurrencyUomId;
    @Id
    @Column(name = "FROM_DATE")
    Timestamp fromDate;
    @Column(name = "RATE_AMOUNT", precision = 18, scale = 2)
    BigDecimal rateAmount;
    @Column(name = "THRU_DATE")
    Timestamp thruDate;

    protected RateAmountRecord() {}

    public RateAmountRecord(String rateTypeId, String currency, Timestamp start, BigDecimal amount) {
        this.rateTypeId = rateTypeId;
        rateCurrencyUomId = currency;
        fromDate = start;
        rateAmount = amount;
    }

    public void expireAt(Timestamp timestamp) {
        thruDate = timestamp;
    }

    public void updateAmount(BigDecimal amount) {
        rateAmount = amount;
    }

    public String getRateTypeId() {
        return rateTypeId;
    }

    public String getRateCurrencyUomId() {
        return rateCurrencyUomId;
    }

    public Timestamp getFromDate() {
        return fromDate;
    }

    public BigDecimal getRateAmount() {
        return rateAmount;
    }

    public static class Key implements Serializable {
        private static final long serialVersionUID = 1L;
        public String rateTypeId;
        public String rateCurrencyUomId;
        public Timestamp fromDate;

        public Key() {}

        @Override
        public boolean equals(Object other) {
            return other instanceof Key key
                    && Objects.equals(rateTypeId, key.rateTypeId)
                    && Objects.equals(rateCurrencyUomId, key.rateCurrencyUomId)
                    && Objects.equals(fromDate, key.fromDate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(rateTypeId, rateCurrencyUomId, fromDate);
        }
    }
}
