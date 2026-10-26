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

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.datamodel.entity.ContactListCommStatus;
import org.apache.ofbiz.migration.datamodel.entity.ContactListParty;
import org.apache.ofbiz.migration.datamodel.entity.SegmentGroupRole;
import org.apache.ofbiz.migration.datamodel.entity.TrackingCodeOrder;
import org.apache.ofbiz.migration.datamodel.entity.TrackingCodeOrderReturn;
import org.apache.ofbiz.migration.datamodel.entity.WebSiteContactList;
import org.apache.ofbiz.migration.marketing.MarketingData;
import org.apache.ofbiz.migration.marketing.port.CommunicationEventPort;
import org.apache.ofbiz.migration.marketing.port.OrderReturnPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Hand-ported minilang services: WebSiteContactList, communication status, segment, tracking code, permission.
 */
@Service
public class MarketingMiscServices {

    private static final String PARTNER_MANAGED = "PARTNER_MGD";

    private final MarketingData data;
    private final CommunicationEventPort communicationEvents;
    private final OrderReturnPort orderReturns;

    public MarketingMiscServices(
            MarketingData data, CommunicationEventPort communicationEvents, OrderReturnPort orderReturns) {
        this.data = data;
        this.communicationEvents = communicationEvents;
        this.orderReturns = orderReturns;
    }

    public Map<String, Object> createWebSiteContactList(Map<String, Object> input) {
        String webSiteId = MarketingData.requireText(input, "webSiteId");
        String contactListId = MarketingData.requireText(input, "contactListId");
        Timestamp fromDate = MarketingData.timestamp(input, "fromDate");
        if (fromDate == null) {
            fromDate = MarketingData.now();
        }
        Map<String, Object> keys = Map.of("webSiteId", webSiteId, "contactListId", contactListId, "fromDate", fromDate);
        if (data.one(WebSiteContactList.class, keys).isPresent()) {
            throw new MigrationException(HttpStatus.CONFLICT, "This webSiteContactList (webSiteId[" + webSiteId
                    + "], contactListId[" + contactListId + "]) already exists.");
        }
        WebSiteContactList row = new WebSiteContactList();
        data.copyFields(row, input, Set.of("fromDate"));
        row.setFromDate(fromDate);
        data.persist(row);
        return new LinkedHashMap<>(data.toMap(row));
    }

    public Map<String, Object> updateWebSiteContactList(Map<String, Object> input) {
        WebSiteContactList row = data.require(WebSiteContactList.class, webSiteKeys(input));
        data.copyFields(row, input, data.primaryKeys(WebSiteContactList.class));
        return new LinkedHashMap<>(data.toMap(row));
    }

    public Map<String, Object> deleteWebSiteContactList(Map<String, Object> input) {
        data.remove(data.require(WebSiteContactList.class, webSiteKeys(input)));
        return Map.of();
    }

    public Map<String, Object> optOutOfListFromCommEvent(Map<String, Object> input) {
        String communicationEventId = MarketingData.requireText(input, "communicationEventId");
        Optional<CommunicationEventPort.CommunicationEventView> event = communicationEvents.find(communicationEventId);
        Map<String, Object> result = new LinkedHashMap<>();
        int optedOut = 0;
        if (event.isPresent() && event.get().contactListId() != null && event.get().partyIdTo() != null
                && event.get().contactMechIdTo() != null) {
            Timestamp now = MarketingData.now();
            List<ContactListParty> rows = data.query(ContactListParty.class,
                    "select c from ContactListParty c where c.contactListId = ?1 and c.preferredContactMechId = ?2"
                            + " and c.partyId = ?3",
                    event.get().contactListId(), event.get().contactMechIdTo(), event.get().partyIdTo());
            for (ContactListParty row : rows) {
                if (MarketingData.active(row.getFromDate(), row.getThruDate(), now)) {
                    row.setThruDate(now);
                    optedOut++;
                }
            }
        }
        result.put("contactListId", event.map(CommunicationEventPort.CommunicationEventView::contactListId).orElse(null));
        result.put("optedOutCount", optedOut);
        return result;
    }

    public Map<String, Object> updateContactListCommStatus(Map<String, Object> input) {
        Map<String, Object> keys = Map.of(
                "contactListId", MarketingData.requireText(input, "contactListId"),
                "communicationEventId", MarketingData.requireText(input, "communicationEventId"),
                "contactMechId", MarketingData.requireText(input, "contactMechId"));
        Optional<ContactListCommStatus> existing = data.one(ContactListCommStatus.class, keys);
        ContactListCommStatus row = existing.orElseGet(ContactListCommStatus::new);
        if (existing.isEmpty()) {
            data.copyFields(row, keys, Set.of());
        }
        data.copyFields(row, input, data.primaryKeys(ContactListCommStatus.class));
        row.setChangeByUserLoginId(MarketingData.currentUserLoginId());
        if (existing.isEmpty()) {
            data.persist(row);
        }
        return new LinkedHashMap<>(data.toMap(row));
    }

    public Map<String, Object> updateCommStatusFromCommEvent(Map<String, Object> input) {
        String communicationEventId = MarketingData.requireText(input, "communicationEventId");
        int updated = 0;
        Optional<CommunicationEventPort.CommunicationEventView> event = communicationEvents.find(communicationEventId);
        if (event.isPresent()) {
            List<ContactListCommStatus> statuses = data.query(ContactListCommStatus.class,
                    "select s from ContactListCommStatus s where s.communicationEventId = ?1 and s.partyId = ?2"
                            + " and s.contactMechId = ?3",
                    event.get().parentCommEventId(), event.get().partyIdTo(), event.get().contactMechIdTo());
            for (ContactListCommStatus status : statuses) {
                Map<String, Object> update = new HashMap<>(data.toMap(status));
                update.put("statusId", input.get("statusId"));
                updateContactListCommStatus(update);
                updated++;
            }
        }
        return Map.of("updatedCount", updated);
    }

    public Map<String, Object> arePartiesRelatedThroughSegmentGroup(Map<String, Object> input) {
        String partyId = MarketingData.requireText(input, "partyId");
        String toPartyId = MarketingData.requireText(input, "toPartyId");
        String roleTypeId = MarketingData.text(input, "roleTypeId");
        String toRoleTypeId = MarketingData.text(input, "toRoleTypeId");
        List<SegmentGroupRole> from = data.query(SegmentGroupRole.class,
                "select r from SegmentGroupRole r where r.partyId = ?1", partyId);
        List<SegmentGroupRole> to = data.query(SegmentGroupRole.class,
                "select r from SegmentGroupRole r where r.partyId = ?1", toPartyId);
        boolean related = from.stream()
                .filter(role -> roleTypeId == null || roleTypeId.equals(role.getRoleTypeId()))
                .anyMatch(role -> to.stream()
                        .filter(other -> toRoleTypeId == null || toRoleTypeId.equals(other.getRoleTypeId()))
                        .anyMatch(other -> other.getSegmentGroupId().equals(role.getSegmentGroupId())));
        return Map.of("areRelated", related ? "Y" : "N");
    }

    public Map<String, Object> createTrackingCodeOrderReturns(Map<String, Object> input) {
        String returnId = MarketingData.requireText(input, "returnId");
        if (!orderReturns.returnExists(returnId)) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "ReturnHeader not found");
        }
        int created = 0;
        for (OrderReturnPort.ReturnOrderLine line : orderReturns.returnOrderLines(returnId)) {
            Optional<TrackingCodeOrder> trackingCodeOrder = data.first(TrackingCodeOrder.class,
                    "select t from TrackingCodeOrder t where t.orderId = ?1", line.orderId());
            if (trackingCodeOrder.isEmpty()) {
                continue;
            }
            TrackingCodeOrder source = trackingCodeOrder.get();
            Map<String, Object> keys = Map.of("returnId", returnId, "orderId", line.orderId(),
                    "trackingCodeTypeId", PARTNER_MANAGED);
            if (data.one(TrackingCodeOrderReturn.class, keys).isPresent()) {
                continue;
            }
            TrackingCodeOrderReturn row = new TrackingCodeOrderReturn();
            row.setReturnId(returnId);
            row.setOrderId(line.orderId());
            row.setOrderItemSeqId(line.orderItemSeqId());
            row.setTrackingCodeTypeId(PARTNER_MANAGED);
            row.setTrackingCodeId(source.getTrackingCodeId());
            row.setIsBillable(source.getIsBillable());
            row.setSiteId(source.getSiteId());
            row.setHasExported("N");
            row.setAffiliateReferredTimeStamp(MarketingData.now());
            data.persist(row);
            created++;
        }
        return Map.of("createdCount", created);
    }

    /** Port of genericBasePermissionCheck with primaryPermission defaulting to MARKETING. */
    public Map<String, Object> marketingPermissionService(Map<String, Object> input) {
        String primary = Optional.ofNullable(MarketingData.text(input, "primaryPermission")).orElse("MARKETING");
        String alt = MarketingData.text(input, "altPermission");
        String action = Optional.ofNullable(MarketingData.text(input, "mainAction")).orElse("VIEW")
                .toUpperCase(Locale.ROOT);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> authorities = authentication == null ? Set.of()
                : AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        boolean allowed = granted(authorities, primary, action) || alt != null && granted(authorities, alt, action);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("hasPermission", allowed);
        if (!allowed) {
            result.put("failMessage", "You do not have permission to " + action + " " + primary);
        }
        return result;
    }

    private static boolean granted(Set<String> authorities, String permission, String action) {
        String base = permission.toUpperCase(Locale.ROOT);
        return authorities.contains(base + "_ADMIN") || authorities.contains(base + "_" + action);
    }

    private static Map<String, Object> webSiteKeys(Map<String, Object> input) {
        Timestamp fromDate = MarketingData.timestamp(input, "fromDate");
        if (fromDate == null) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Missing required input: fromDate");
        }
        return Map.of("webSiteId", MarketingData.requireText(input, "webSiteId"),
                "contactListId", MarketingData.requireText(input, "contactListId"), "fromDate", fromDate);
    }
}
