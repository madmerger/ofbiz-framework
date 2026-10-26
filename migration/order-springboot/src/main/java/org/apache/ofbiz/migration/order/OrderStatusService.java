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
package org.apache.ofbiz.migration.order;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.SequenceService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderStatusService {
    private final OrderEntitySupport data;
    private final SequenceService sequences;
    private final OrderItemStatusService itemStatuses;

    public OrderStatusService(
            OrderEntitySupport data, SequenceService sequences, OrderItemStatusService itemStatuses) {
        this.data = data;
        this.sequences = sequences;
        this.itemStatuses = itemStatuses;
    }

    public void recordInitialStatus(String orderId, String itemSeqId, String statusId) {
        recordStatus(orderId, itemSeqId, statusId, null);
    }

    @Transactional
    public Map<String, Object> changeHeaderStatus(
            String orderId, String targetStatus, boolean setItemStatus, String changeReason) {
        Object order = data.require("OrderHeader", Map.of("orderId", orderId));
        String oldStatus = text(data.get(order, "statusId"));
        String orderType = text(data.get(order, "orderTypeId"));
        BigDecimal grandTotal = decimal(data.get(order, "grandTotal"));
        String orderStatusId = null;
        if (!oldStatus.equals(targetStatus)) {
            requireValidChange(oldStatus, targetStatus);
            data.set(order, "statusId", targetStatus);
            orderStatusId = recordStatus(orderId, null, targetStatus, changeReason);
            String itemStatus = switch (targetStatus) {
            case "ORDER_APPROVED" -> "ITEM_APPROVED";
            case "ORDER_COMPLETED" -> "ITEM_COMPLETED";
            case "ORDER_CANCELLED" -> "ITEM_CANCELLED";
            default -> null;
            };
            if (setItemStatus && itemStatus != null) {
                itemStatuses.changeItemStatus(orderId, null, null, itemStatus, changeReason);
            }
        }
        Map<String, Object> output = new LinkedHashMap<>();
        output.put("oldStatusId", oldStatus);
        output.put("orderStatusId", orderStatusId);
        output.put("orderTypeId", orderType);
        output.put("grandTotal", grandTotal);
        output.put("statusId", targetStatus);
        return output;
    }

    private void requireValidChange(String fromStatus, String toStatus) {
        if (!itemStatuses.hasValidChange(fromStatus, toStatus)) {
            throw new MigrationException(
                    HttpStatus.CONFLICT, "許可されていない注文状態遷移です: " + fromStatus + " → " + toStatus);
        }
    }

    private String recordStatus(String orderId, String itemSeqId, String statusId, String changeReason) {
        String statusIdValue = sequences.nextId("OrderStatus");
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("orderStatusId", statusIdValue);
        values.put("orderId", orderId);
        values.put("orderItemSeqId", itemSeqId);
        values.put("statusId", statusId);
        values.put("statusUserLogin", currentUser());
        values.put("statusDatetime", Timestamp.from(Instant.now()));
        values.put("changeReason", changeReason);
        data.create("OrderStatus", values);
        return statusIdValue;
    }

    private static String currentUser() {
        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication();
        return authentication == null ? "system" : authentication.getName();
    }

    private static String text(Object value) {
        return value == null ? null : value.toString();
    }

    private static BigDecimal decimal(Object value) {
        return value == null ? BigDecimal.ZERO : new BigDecimal(value.toString());
    }
}
