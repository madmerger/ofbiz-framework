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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.apache.ofbiz.migration.order;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.SequenceService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderItemStatusService {
    private final OrderEntitySupport data;
    private final SequenceService sequences;

    public OrderItemStatusService(OrderEntitySupport data, SequenceService sequences) {
        this.data = data;
        this.sequences = sequences;
    }

    @Transactional
    public Map<String, Object> changeItemStatus(
            String orderId, String itemSeqId, String fromStatusId, String targetStatus, String changeReason) {
        data.require("OrderHeader", Map.of("orderId", orderId));
        List<?> items = data.find("OrderItem", Map.of("orderId", orderId));
        if (itemSeqId != null) {
            items = items.stream()
                    .filter(item -> itemSeqId.equals(text(data.get(item, "orderItemSeqId"))))
                    .toList();
            if (items.isEmpty()) {
                throw new MigrationException(HttpStatus.NOT_FOUND, "注文明細が見つかりません");
            }
        }
        List<String> changed = new ArrayList<>();
        for (Object item : items) {
            String currentStatus = text(data.get(item, "statusId"));
            if (fromStatusId != null && !fromStatusId.equals(currentStatus)) {
                continue;
            }
            if (fromStatusId == null
                    && ("ITEM_COMPLETED".equals(currentStatus) || "ITEM_CANCELLED".equals(currentStatus))) {
                continue;
            }
            if (!hasValidChange(currentStatus, targetStatus)) {
                continue;
            }
            data.set(item, "statusId", targetStatus);
            recordStatus(orderId, text(data.get(item, "orderItemSeqId")), targetStatus, changeReason);
            changed.add(text(data.get(item, "orderItemSeqId")));
        }
        return Map.of("orderId", orderId, "statusId", targetStatus, "changedOrderItemSeqIds", changed);
    }

    public boolean hasValidChange(String fromStatus, String toStatus) {
        return !data.find("StatusValidChange", Map.of("statusId", fromStatus, "statusIdTo", toStatus)).isEmpty();
    }

    @Transactional
    public void forceItemStatus(String orderId, String itemSeqId, String targetStatus, String changeReason) {
        Object item = data.require("OrderItem", Map.of(
                "orderId", orderId, "orderItemSeqId", itemSeqId));
        if (targetStatus.equals(data.get(item, "statusId"))) {
            return;
        }
        data.set(item, "statusId", targetStatus);
        recordStatus(orderId, itemSeqId, targetStatus, changeReason);
    }

    private void recordStatus(String orderId, String itemSeqId, String statusId, String changeReason) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("orderStatusId", sequences.nextId("OrderStatus"));
        values.put("orderId", orderId);
        values.put("orderItemSeqId", itemSeqId);
        values.put("statusId", statusId);
        values.put("statusUserLogin", currentUser());
        values.put("statusDatetime", Timestamp.from(Instant.now()));
        values.put("changeReason", changeReason);
        data.create("OrderStatus", values);
    }

    private static String currentUser() {
        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication();
        return authentication == null ? "system" : authentication.getName();
    }

    private static String text(Object value) {
        return value == null ? null : value.toString();
    }
}
