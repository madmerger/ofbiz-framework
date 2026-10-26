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
package org.apache.ofbiz.migration.marketing.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.SequenceService;
import org.apache.ofbiz.migration.datamodel.entity.SalesForecast;
import org.apache.ofbiz.migration.datamodel.entity.SalesForecastHistory;
import org.apache.ofbiz.migration.datamodel.entity.SalesOpportunityRole;
import org.apache.ofbiz.migration.marketing.MarketingData;
import org.apache.ofbiz.migration.marketing.port.PartyPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Hand-ported SFA minilang services: ContactServices.xml and order OpportunityServices.xml.
 */
@Service
public class SfaServices {

    private final MarketingData data;
    private final PartyPort party;
    private final SequenceService sequences;

    public SfaServices(MarketingData data, PartyPort party, SequenceService sequences) {
        this.data = data;
        this.party = party;
        this.sequences = sequences;
    }

    public Map<String, Object> createContact(Map<String, Object> input) {
        String roleTypeId = "CONTACT";
        String partyId = party.createPerson(input);
        party.ensurePartyRole(partyId, roleTypeId);
        String contactMechId = null;
        String email = MarketingData.text(input, "emailAddress");
        if (email != null) {
            if (!MarketingData.isEmail(email)) {
                throw new MigrationException(HttpStatus.BAD_REQUEST, "PartyEmailAddressNotFormattedCorrectly");
            }
            contactMechId = party.createPartyEmailAddress(partyId, email, "PRIMARY_EMAIL", MarketingData.now());
        }
        String partyIdFrom = party.loginPartyId(MarketingData.currentUserLoginId())
                .orElse(MarketingData.text(input, "partyIdFrom"));
        if (partyIdFrom != null) {
            party.ensurePartyRole(partyIdFrom, "_NA_");
            party.createPartyRelationship(partyIdFrom, partyId, "_NA_", roleTypeId, "CONTACT_REL");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("partyId", partyId);
        result.put("roleTypeId", roleTypeId);
        result.put("contactMechId", contactMechId);
        return result;
    }

    public Map<String, Object> mergeContacts(Map<String, Object> input) {
        String partyIdTo = MarketingData.requireText(input, "partyIdTo");
        String partyId = MarketingData.requireText(input, "partyId");
        if (!partyIdTo.equals(partyId)) {
            merge(input, partyIdTo, partyId, "useAddress2", "addrContactMechId", "addrContactMechIdTo",
                    "GENERAL_LOCATION");
            merge(input, partyIdTo, partyId, "useContactNum2", "phoneContactMechId", "phoneContactMechIdTo",
                    "PRIMARY_PHONE");
            merge(input, partyIdTo, partyId, "useEmail2", "emailContactMechId", "emailContactMechIdTo",
                    "PRIMARY_EMAIL");
            party.setPartyStatus(partyId, "PARTY_DISABLED");
        }
        return Map.of("partyId", partyIdTo);
    }

    private void merge(Map<String, Object> input, String partyIdTo, String partyId,
            String flag, String mechField, String mechToField, String purposeTypeId) {
        String mech = MarketingData.text(input, mechField);
        if (!"Y".equals(MarketingData.text(input, flag)) || mech == null) {
            return;
        }
        String mechTo = MarketingData.text(input, mechToField);
        if (mechTo != null) {
            party.updatePartyContactMech(partyIdTo, mechTo, mech, purposeTypeId);
        }
        party.deletePartyContactMech(partyId, mech);
    }

    public Map<String, Object> createSalesForecast(Map<String, Object> input) {
        SalesForecast forecast = new SalesForecast();
        data.copyFields(forecast, input, Set.of("salesForecastId"));
        forecast.setSalesForecastId(sequences.nextId("SalesForecast"));
        forecast.setCreatedByUserLoginId(MarketingData.currentUserLoginId());
        forecast.setModifiedByUserLoginId(MarketingData.currentUserLoginId());
        data.persist(forecast);
        saveSalesForecastHistory(forecast, MarketingData.text(input, "changeNote"));
        return Map.of("salesForecastId", forecast.getSalesForecastId());
    }

    public Map<String, Object> updateSalesForecast(Map<String, Object> input) {
        SalesForecast forecast = data.require(SalesForecast.class,
                Map.of("salesForecastId", MarketingData.requireText(input, "salesForecastId")));
        data.copyFields(forecast, input, Set.of("salesForecastId", "changeNote"));
        saveSalesForecastHistory(forecast, MarketingData.text(input, "changeNote"));
        return Map.of("salesForecastId", forecast.getSalesForecastId());
    }

    private void saveSalesForecastHistory(SalesForecast forecast, String changeNote) {
        SalesForecastHistory history = new SalesForecastHistory();
        Map<String, Object> values = new HashMap<>(data.toMap(forecast));
        data.copyFields(history, values, Set.of("salesForecastHistoryId", "modifiedByUserLoginId"));
        history.setSalesForecastHistoryId(sequences.nextId("SalesForecastHistory"));
        history.setChangeNote(changeNote);
        history.setModifiedTimestamp(MarketingData.now());
        history.setModifiedByUserLoginId(MarketingData.currentUserLoginId());
        data.persist(history);
    }

    public Map<String, Object> createSalesOpportunityAccountRole(Map<String, Object> input) {
        return replaceRole(input, "ACCOUNT", MarketingData.text(input, "accountPartyId"), false);
    }

    public Map<String, Object> createSalesOpportunityLeadRole(Map<String, Object> input) {
        return replaceRole(input, "LEAD", MarketingData.text(input, "leadPartyId"), true);
    }

    private Map<String, Object> replaceRole(
            Map<String, Object> input, String roleTypeId, String newPartyId, boolean alwaysEnsure) {
        String salesOpportunityId = MarketingData.requireText(input, "salesOpportunityId");
        Optional<SalesOpportunityRole> current = currentRole(salesOpportunityId, roleTypeId);
        String currentPartyId = current.map(SalesOpportunityRole::getPartyId).orElse(null);
        if (currentPartyId != null && currentPartyId.equals(newPartyId)) {
            return Map.of("changed", false);
        }
        current.ifPresent(data::remove);
        if (newPartyId == null) {
            if (alwaysEnsure) {
                throw new MigrationException(HttpStatus.BAD_REQUEST, "Missing required input: leadPartyId");
            }
            return Map.of("changed", current.isPresent());
        }
        party.ensurePartyRole(newPartyId, roleTypeId);
        SalesOpportunityRole role = new SalesOpportunityRole();
        role.setSalesOpportunityId(salesOpportunityId);
        role.setPartyId(newPartyId);
        role.setRoleTypeId(roleTypeId);
        data.persist(role);
        return Map.of("changed", true);
    }

    public Map<String, Object> findPartyInSalesOpportunityRole(Map<String, Object> input) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("partyId", currentRole(MarketingData.requireText(input, "salesOpportunityId"),
                MarketingData.requireText(input, "roleTypeId")).map(SalesOpportunityRole::getPartyId).orElse(null));
        return result;
    }

    private Optional<SalesOpportunityRole> currentRole(String salesOpportunityId, String roleTypeId) {
        return data.first(SalesOpportunityRole.class,
                "select r from SalesOpportunityRole r where r.salesOpportunityId = ?1 and r.roleTypeId = ?2",
                salesOpportunityId, roleTypeId);
    }
}
