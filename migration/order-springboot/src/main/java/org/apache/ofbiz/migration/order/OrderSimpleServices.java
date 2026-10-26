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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.apache.ofbiz.migration.order;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.SequenceService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderSimpleServices {
    private final OrderEntitySupport data;
    private final SequenceService sequences;
    private final OrderItemStatusService itemStatuses;
    private final OrderStatusService orderStatuses;
    private final ShipmentReceiptPort shipmentReceipts;
    private final CommunicationEventPort communicationEvents;

    public OrderSimpleServices(
            OrderEntitySupport data,
            SequenceService sequences,
            OrderItemStatusService itemStatuses,
            OrderStatusService orderStatuses,
            ShipmentReceiptPort shipmentReceipts,
            CommunicationEventPort communicationEvents) {
        this.data = data;
        this.sequences = sequences;
        this.itemStatuses = itemStatuses;
        this.orderStatuses = orderStatuses;
        this.shipmentReceipts = shipmentReceipts;
        this.communicationEvents = communicationEvents;
    }

    public Map<String, Object> orderAdjustmentPermissionCheck(Map<String, Object> input) {
        return permissionResult(text(input.get("mainAction")), "ORDERMGR");
    }

    public Map<String, Object> returnPermissionCheck(Map<String, Object> input) {
        return permissionResult(text(input.get("mainAction")), "ORDERMGR");
    }

    public Map<String, Object> custRequestPermissionCheck(Map<String, Object> input) {
        String requestedParty = text(input.get("fromPartyId"));
        String currentPartyId = currentPartyId();
        boolean ownRequest = currentPartyId != null
                && (requestedParty == null || requestedParty.equals(currentPartyId));
        Map<String, Object> result = permissionResult(
                text(input.getOrDefault("mainAction", "CREATE")), "ORDERMGR_CRQ");
        boolean allowed = ownRequest || Boolean.TRUE.equals(result.get("hasPermission"));
        return Map.of("hasPermission", allowed);
    }

    public Map<String, Object> updateOrderStatusFromReceipt(Map<String, Object> input) {
        String orderId = requiredText(input, "orderId");
        data.require("OrderHeader", Map.of("orderId", orderId));
        Map<String, BigDecimal> received = new LinkedHashMap<>();
        for (Map<String, Object> receipt : shipmentReceipts.findForOrder(orderId)) {
            String itemSeqId = text(receipt.get("orderItemSeqId"));
            if (itemSeqId != null) {
                BigDecimal quantity = decimal(receipt.get("quantityAccepted"))
                        .add(decimal(receipt.get("quantityRejected")));
                received.merge(itemSeqId, quantity, BigDecimal::add);
            }
        }
        List<?> items = data.find("OrderItem", Map.of("orderId", orderId));
        for (Object item : items) {
            String itemSeqId = text(data.get(item, "orderItemSeqId"));
            String statusId = text(data.get(item, "statusId"));
            if (!"ITEM_COMPLETED".equals(statusId)
                    && decimal(data.get(item, "quantity")).compareTo(received.getOrDefault(itemSeqId, BigDecimal.ZERO))
                            <= 0) {
                itemStatuses.forceItemStatus(orderId, itemSeqId, "ITEM_COMPLETED", null);
            }
        }
        boolean allCompleted = !items.isEmpty()
                && data.find("OrderItem", Map.of("orderId", orderId)).stream()
                        .allMatch(item -> "ITEM_COMPLETED".equals(data.get(item, "statusId")));
        if (allCompleted) {
            orderStatuses.changeHeaderStatus(orderId, "ORDER_COMPLETED", false, null);
        }
        return Map.of("orderId", orderId, "statusId", allCompleted ? "ORDER_COMPLETED" : "IN_PROGRESS");
    }

    public Map<String, Object> createCustRequest(Map<String, Object> input) {
        requiredText(input, "fromPartyId");
        String custRequestId = text(input.get("custRequestId"));
        if (custRequestId == null) {
            custRequestId = sequences.nextId("CustRequest");
        } else if (!data.find("CustRequest", Map.of("custRequestId", custRequestId)).isEmpty()) {
            throw conflict("顧客依頼が既に存在します");
        }
        Timestamp now = Timestamp.from(Instant.now());
        String userLoginId = currentUser();
        Map<String, Object> values = copyWritable("CustRequest", input);
        values.put("custRequestId", custRequestId);
        values.put("statusId", "CRQ_DRAFT");
        values.putIfAbsent("custRequestDate", now);
        values.put("createdDate", now);
        values.put("lastModifiedDate", now);
        values.put("createdByUserLogin", userLoginId);
        values.put("lastModifiedByUserLogin", userLoginId);
        data.create("CustRequest", values);

        String requestedStatus = text(input.get("statusId"));
        if (requestedStatus != null && !"CRQ_DRAFT".equals(requestedStatus)) {
            Map<String, Object> statusInput = new LinkedHashMap<>();
            statusInput.put("custRequestId", custRequestId);
            statusInput.put("statusId", requestedStatus);
            statusInput.put("webSiteId", input.get("webSiteId"));
            setCustRequestStatus(statusInput);
        }
        if (hasItemFields(input)) {
            createCustRequestItem(custRequestId, input);
        }
        return Map.of("custRequestId", custRequestId);
    }

    public Map<String, Object> updateCustRequest(Map<String, Object> input) {
        String custRequestId = requiredText(input, "custRequestId");
        Object request = data.require("CustRequest", Map.of("custRequestId", custRequestId));
        checkStatus(request);
        String oldStatus = text(data.get(request, "statusId"));
        data.set(request, "lastModifiedDate", Timestamp.from(Instant.now()));
        data.set(request, "lastModifiedByUserLogin", currentUser());
        copyWritable("CustRequest", input).forEach((field, value) -> {
            if (!"statusId".equals(field)) {
                data.set(request, field, value);
            }
        });
        data.set(request, "statusId", oldStatus);
        String requestedStatus = text(input.get("statusId"));
        if (requestedStatus != null && !requestedStatus.equals(oldStatus)) {
            Map<String, Object> statusInput = new LinkedHashMap<>();
            statusInput.put("custRequestId", custRequestId);
            statusInput.put("statusId", requestedStatus);
            statusInput.put("reason", input.get("reason"));
            setCustRequestStatus(statusInput);
        }
        String story = text(input.get("story"));
        if (story != null) {
            List<?> items = data.find("CustRequestItem", Map.of("custRequestId", custRequestId));
            if (items.isEmpty()) {
                createCustRequestItem(custRequestId, Map.of("story", story));
            } else {
                data.set(items.get(0), "story", story);
            }
        }
        return Map.of("oldStatusId", oldStatus);
    }

    public Map<String, Object> deleteCustRequest(Map<String, Object> input) {
        String custRequestId = requiredText(input, "custRequestId");
        Object request = data.require("CustRequest", Map.of("custRequestId", custRequestId));
        if (!"CRQ_DRAFT".equals(data.get(request, "statusId"))) {
            throw conflict("下書き状態の顧客依頼のみ削除できます");
        }
        for (String related : List.of("CustRequestContent", "CustRequestParty", "CustRequestStatus")) {
            for (Object entity : data.find(related, Map.of("custRequestId", custRequestId))) {
                data.remove(entity);
            }
        }
        data.remove(request);
        try {
            data.flush();
        } catch (DataIntegrityViolationException exception) {
            throw conflict("顧客依頼に削除できない関連データがあります");
        }
        return Map.of("custRequestId", custRequestId);
    }

    public Map<String, Object> copyCustRequestItem(Map<String, Object> input) {
        String sourceRequestId = requiredText(input, "custRequestId");
        String sourceItemSeqId = requiredText(input, "custRequestItemSeqId");
        Object source = data.require("CustRequestItem", Map.of(
                "custRequestId", sourceRequestId,
                "custRequestItemSeqId", sourceItemSeqId));
        String targetRequestId = text(input.get("custRequestIdTo"));
        if (targetRequestId == null) {
            targetRequestId = sourceRequestId;
        }
        data.require("CustRequest", Map.of("custRequestId", targetRequestId));
        Map<String, Object> values = data.toMap(source);
        values.put("custRequestId", targetRequestId);
        String targetItemSeqId = text(input.get("custRequestItemSeqIdTo"));
        if (targetItemSeqId == null) {
            targetItemSeqId = sequences.nextSubSequence(
                    "CustRequestItem", Map.of("custRequestId", targetRequestId), "custRequestItemSeqId");
        }
        values.put("custRequestItemSeqId", targetItemSeqId);
        values.remove("class");
        data.create("CustRequestItem", values);
        return Map.of("custRequestIdTo", targetRequestId, "custRequestItemSeqId", targetItemSeqId);
    }

    public Map<String, Object> checkStatusCustRequest(Map<String, Object> input) {
        Object request = data.require("CustRequest", Map.of("custRequestId", requiredText(input, "custRequestId")));
        checkStatus(request);
        return Map.of("custRequestId", input.get("custRequestId"), "statusId", data.get(request, "statusId"));
    }

    public Map<String, Object> updateCustRequestLastModifiedDate(Map<String, Object> input) {
        Object request = data.require("CustRequest", Map.of("custRequestId", requiredText(input, "custRequestId")));
        data.set(request, "lastModifiedDate", Timestamp.from(Instant.now()));
        return Map.of("custRequestId", input.get("custRequestId"));
    }

    public Map<String, Object> createCustRequestNote(Map<String, Object> input) {
        String custRequestId = requiredText(input, "custRequestId");
        Object request = data.require("CustRequest", Map.of("custRequestId", custRequestId));
        String noteId = createNote(text(input.get("noteInfo")), text(input.get("partyId")));
        data.create("CustRequestNote", Map.of("custRequestId", custRequestId, "noteId", noteId));
        updateLastModified(request);
        return Map.of("noteId", noteId);
    }

    public Map<String, Object> updateCustRequestNote(Map<String, Object> input) {
        String custRequestId = requiredText(input, "custRequestId");
        String noteId = requiredText(input, "noteId");
        data.require("CustRequestNote", Map.of("custRequestId", custRequestId, "noteId", noteId));
        Object note = data.require("NoteData", Map.of("noteId", noteId));
        setProvidedFields(note, input, Set.of("noteInfo", "noteName", "noteDate"));
        Object request = data.require("CustRequest", Map.of("custRequestId", custRequestId));
        updateLastModified(request);
        return Map.of("custRequestId", custRequestId, "noteId", noteId);
    }

    public Map<String, Object> createCustRequestItemNote(Map<String, Object> input) {
        String custRequestId = requiredText(input, "custRequestId");
        Object request = data.require("CustRequest", Map.of("custRequestId", custRequestId));
        checkStatus(request);
        String itemSeqId = requiredText(input, "custRequestItemSeqId");
        data.require("CustRequestItem", Map.of(
                "custRequestId", custRequestId, "custRequestItemSeqId", itemSeqId));
        String noteId = createNote(text(input.get("note")), text(input.get("partyId")));
        data.create("CustRequestItemNote", Map.of(
                "custRequestId", custRequestId,
                "custRequestItemSeqId", itemSeqId,
                "noteId", noteId));
        updateLastModified(request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("noteId", noteId);
        String currentPartyId = currentPartyId();
        if (currentPartyId != null) {
            result.put("partyId", currentPartyId);
        }
        return result;
    }

    public Map<String, Object> createCustRequestFromCart(Map<String, Object> input) {
        Object cartItems = input.get("cartItems");
        if (!(cartItems instanceof List<?> items) || items.stream().noneMatch(this::requestableCartItem)) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "カートに依頼可能な商品がありません");
        }
        String custRequestName = text(input.get("custRequestName"));
        if (custRequestName == null) {
            custRequestName = "ショッピングカートからの顧客依頼";
        }
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("fromPartyId", text(input.getOrDefault("fromPartyId", currentPartyId())));
        header.put("custRequestTypeId", "RF_QUOTE");
        header.put("statusId", "CRQ_SUBMITTED");
        header.put("custRequestName", custRequestName);
        header.put("maximumAmountUomId", input.getOrDefault("currencyUomId", "USD"));
        header.put("productStoreId", input.get("productStoreId"));
        Map<String, Object> created = createCustRequest(header);
        String custRequestId = text(created.get("custRequestId"));
        for (Object value : items) {
            if (requestableCartItem(value)) {
                createCustRequestItem(custRequestId, stringMap(value));
            }
        }
        return Map.of("custRequestId", custRequestId);
    }

    public Map<String, Object> createCustRequestFromShoppingList(Map<String, Object> input) {
        String shoppingListId = requiredText(input, "shoppingListId");
        Object shoppingList = data.require("ShoppingList", Map.of("shoppingListId", shoppingListId));
        List<?> sourceItems = data.find("ShoppingListItem", Map.of("shoppingListId", shoppingListId));
        List<Map<String, Object>> cartItems = sourceItems.stream()
                .map(data::toMap)
                .toList();
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("cartItems", cartItems);
        request.put("custRequestName", data.get(shoppingList, "listName"));
        request.put("fromPartyId", data.get(shoppingList, "partyId"));
        return createCustRequestFromCart(request);
    }

    public Map<String, Object> getCustRequestsByRole(Map<String, Object> input) {
        String partyId = currentPartyId();
        if (partyId == null) {
            return Map.of("custRequestAndRoles", List.of());
        }
        String roleTypeId = text(input.get("roleTypeId"));
        Map<String, Object> filters = new LinkedHashMap<>();
        filters.put("partyId", partyId);
        if (roleTypeId != null) {
            filters.put("roleTypeId", roleTypeId);
        }
        List<Map<String, Object>> requests = new ArrayList<>();
        for (Object role : data.find("CustRequestParty", filters)) {
            String custRequestId = text(data.get(role, "custRequestId"));
            data.find("CustRequest", Map.of("custRequestId", custRequestId)).stream()
                    .findFirst()
                    .map(data::toMap)
                    .ifPresent(request -> {
                        request.put("roleTypeId", data.get(role, "roleTypeId"));
                        requests.add(request);
                    });
        }
        return Map.of("custRequestAndRoles", requests);
    }

    public Map<String, Object> setCustRequestStatus(Map<String, Object> input) {
        String custRequestId = requiredText(input, "custRequestId");
        String targetStatus = requiredText(input, "statusId");
        Object request = data.require("CustRequest", Map.of("custRequestId", custRequestId));
        String oldStatus = text(data.get(request, "statusId"));
        if (!targetStatus.equals(oldStatus)) {
            if (data.find("StatusValidChange", Map.of(
                    "statusId", oldStatus, "statusIdTo", targetStatus)).isEmpty()) {
                throw conflict("許可されていない顧客依頼状態遷移です");
            }
            if (!"CRQ_DRAFT".equals(targetStatus)
                    && (text(data.get(request, "fromPartyId")) == null
                            || text(data.get(request, "custRequestName")) == null)) {
                throw new MigrationException(HttpStatus.BAD_REQUEST, "送信済み依頼には依頼元と名称が必要です");
            }
            data.set(request, "statusId", targetStatus);
            String reason = text(input.get("reason"));
            if (reason != null) {
                data.set(request, "reason", reason);
            }
            updateLastModified(request);
            Map<String, Object> status = new LinkedHashMap<>();
            status.put("custRequestStatusId", sequences.nextId("CustRequestStatus"));
            status.put("custRequestId", custRequestId);
            status.put("statusId", targetStatus);
            status.put("statusDate", Timestamp.from(Instant.now()));
            status.put("changeByUserLoginId", currentUser());
            status.put("webSiteId", input.get("webSiteId"));
            data.create("CustRequestStatus", status);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("custRequestId", custRequestId);
        result.put("oldStatusId", oldStatus);
        result.put("fromPartyId", data.get(request, "fromPartyId"));
        result.put("custRequestName", data.get(request, "custRequestName"));
        return result;
    }

    public Map<String, Object> createCustRequestFromCommEvent(Map<String, Object> input) {
        String eventId = requiredText(input, "communicationEventId");
        Map<String, Object> event = communicationEvents.findById(eventId)
                .orElseThrow(() -> new MigrationException(HttpStatus.NOT_FOUND, "通信イベントが見つかりません"));
        List<Map<String, Object>> associations = communicationEvents.contentAssociations(eventId);
        if ("COM_COMPLETE".equals(event.get("statusId"))) {
            List<?> existing = data.find("CustRequestCommEvent", Map.of("communicationEventId", eventId));
            if (!existing.isEmpty()) {
                return Map.of("custRequestId", data.get(existing.get(0), "custRequestId"));
            }
        }
        String name = text(input.get("custRequestName"));
        if (name == null) {
            name = text(event.get("subject"));
        }
        if (name != null && name.length() >= 100) {
            name = name.substring(0, 95) + ".....";
        }
        Map<String, Object> request = new LinkedHashMap<>(input);
        request.remove("communicationEventId");
        request.put("custRequestTypeId", input.getOrDefault("custRequestTypeId", "RF_SUPPORT"));
        request.put("custRequestName", name);
        request.put("fromPartyId", input.getOrDefault("fromPartyId", event.get("partyIdFrom")));
        request.put("custRequestDate", event.get("entryDate"));
        request.put("statusId", "CRQ_ACCEPTED");
        request.put("story", input.getOrDefault("story", event.get("content")));
        Map<String, Object> created = createCustRequest(request);
        String custRequestId = text(created.get("custRequestId"));
        data.create("CustRequestCommEvent", Map.of(
                "custRequestId", custRequestId, "communicationEventId", eventId));
        for (Map<String, Object> association : associations) {
            Object contentId = association.get("contentId");
            if (contentId != null) {
                Map<String, Object> content = new LinkedHashMap<>();
                content.put("custRequestId", custRequestId);
                content.put("contentId", contentId);
                content.put("fromDate", Timestamp.from(Instant.now()));
                data.create("CustRequestContent", content);
            }
        }
        communicationEvents.markCompleted(eventId, currentUser());
        return Map.of("custRequestId", custRequestId);
    }

    public Map<String, Object> createRequirement(Map<String, Object> input) {
        requiredText(input, "requirementTypeId");
        String requirementId = text(input.get("requirementId"));
        if (requirementId == null) {
            requirementId = sequences.nextId("Requirement");
        }
        Timestamp now = Timestamp.from(Instant.now());
        Map<String, Object> requirement = copyWritable("Requirement", input);
        requirement.put("requirementId", requirementId);
        requirement.putIfAbsent("statusId", "REQ_CREATED");
        requirement.put("createdDate", now);
        requirement.put("lastModifiedDate", now);
        requirement.put("createdByUserLogin", currentUser());
        requirement.put("lastModifiedByUserLogin", currentUser());
        data.create("Requirement", requirement);
        return Map.of("requirementId", requirementId);
    }

    private void createCustRequestItem(String custRequestId, Map<String, Object> input) {
        String itemSeqId = text(input.get("custRequestItemSeqId"));
        if (itemSeqId == null) {
            itemSeqId = sequences.nextSubSequence(
                    "CustRequestItem", Map.of("custRequestId", custRequestId), "custRequestItemSeqId");
        }
        Map<String, Object> item = copyWritable("CustRequestItem", input);
        item.put("custRequestId", custRequestId);
        item.put("custRequestItemSeqId", itemSeqId);
        item.putIfAbsent("statusId", "CRQ_SUBMITTED");
        data.create("CustRequestItem", item);
    }

    private String createNote(String noteInfo, String partyId) {
        String noteId = sequences.nextId("NoteData");
        Map<String, Object> note = new LinkedHashMap<>();
        note.put("noteId", noteId);
        note.put("noteInfo", noteInfo);
        note.put("partyId", partyId);
        note.put("noteDate", Timestamp.from(Instant.now()));
        data.create("NoteData", note);
        return noteId;
    }

    private void updateLastModified(Object request) {
        data.set(request, "lastModifiedDate", Timestamp.from(Instant.now()));
    }

    private void setProvidedFields(Object entity, Map<String, Object> input, Set<String> fields) {
        for (String field : fields) {
            if (input.containsKey(field)) {
                data.set(entity, field, input.get(field));
            }
        }
    }

    private void checkStatus(Object request) {
        String status = text(data.get(request, "statusId"));
        if ("CRQ_CANCELLED".equals(status) || "CRQ_COMPLETED".equals(status)) {
            throw conflict("処理中の顧客依頼のみ更新できます");
        }
    }

    private boolean hasItemFields(Map<String, Object> input) {
        return input.containsKey("productId")
                || input.containsKey("reservStart")
                || input.containsKey("story")
                || input.containsKey("description");
    }

    private boolean requestableCartItem(Object value) {
        if (!(value instanceof Map<?, ?> item) || Boolean.TRUE.equals(item.get("isPromo"))) {
            return false;
        }
        return text(item.get("productId")) != null
                && decimal(item.get("quantity")).compareTo(BigDecimal.ZERO) > 0;
    }

    private Map<String, Object> stringMap(Object value) {
        if (!(value instanceof Map<?, ?> map)) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "商品行はオブジェクトである必要があります");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        map.forEach((key, fieldValue) -> result.put(key.toString(), fieldValue));
        return result;
    }

    private Map<String, Object> copyWritable(String entityName, Map<String, ?> input) {
        Map<String, Object> values = new LinkedHashMap<>();
        input.forEach((key, value) -> {
            if (!"userLogin".equals(key) && !"cart".equals(key) && !"cartItems".equals(key)) {
                values.put(key, value);
            }
        });
        return values;
    }

    private Map<String, Object> permissionResult(String action, String primaryPermission) {
        String normalizedAction = action == null ? "VIEW" : action.replaceFirst("^_", "").toUpperCase();
        boolean allowed = hasAuthority(primaryPermission + "_ADMIN")
                || hasAuthority(primaryPermission + "_" + normalizedAction)
                || hasAuthority("ORDERMGR_ADMIN")
                || hasAuthority("ORDERMGR_" + normalizedAction);
        return Map.of("hasPermission", allowed);
    }

    private boolean hasAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && AuthorityUtils.authorityListToSet(authentication.getAuthorities()).contains(authority);
    }

    private String currentPartyId() {
        String currentUser = currentUser();
        List<?> logins = data.find("UserLogin", Map.of("userLoginId", currentUser));
        return logins.isEmpty() ? null : text(data.get(logins.get(0), "partyId"));
    }

    private static String currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? "system" : authentication.getName();
    }

    private static String requiredText(Map<String, ?> input, String field) {
        String value = text(input.get(field));
        if (value == null) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "必須項目がありません: " + field);
        }
        return value;
    }

    private static String text(Object value) {
        return value == null || value.toString().isBlank() ? null : value.toString();
    }

    private static BigDecimal decimal(Object value) {
        if (value == null || value.toString().isBlank()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException exception) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "数値が不正です");
        }
    }

    private static MigrationException conflict(String message) {
        return new MigrationException(HttpStatus.CONFLICT, message);
    }
}
