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

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class OrderItemStatusCheckService {
    private final OrderEntitySupport data;
    private final ObjectProvider<MigrationServiceDispatcher> dispatcher;

    public OrderItemStatusCheckService(OrderEntitySupport data, ObjectProvider<MigrationServiceDispatcher> dispatcher) {
        this.data = data;
        this.dispatcher = dispatcher;
    }

    public Map<String, Object> checkOrderItemStatus(Map<String, Object> input) {
        String orderId = (String) input.get("orderId");
        if (orderId == null || orderId.isEmpty()) throw new MigrationException(HttpStatus.BAD_REQUEST, "orderId is required");
        Object header = data.require("OrderHeader", Map.of("orderId", orderId));
        String current = (String) data.get(header, "statusId");
        boolean cancelled = true;
        boolean completed = true;
        boolean approved = true;
        for (Object item : data.find("OrderItem", Map.of("orderId", orderId))) {
            if (!"ITEM_CANCELLED".equals(data.get(item, "statusId"))) {
                cancelled = false;
                if (!"ITEM_COMPLETED".equals(data.get(item, "statusId"))) {
                    completed = false;
                    if (!"ITEM_APPROVED".equals(data.get(item, "statusId"))) {
                        approved = false;
                        break;
                    }
                }
            }
        }
        String next = null;
        if (cancelled) next = "ORDER_CANCELLED";
        else if (completed) next = "ORDER_COMPLETED";
        else if (approved && mayApprove(header, current)) next = "ORDER_APPROVED";
        if (next != null && !next.equals(current)) {
            Map<String, Object> result = dispatcher.getObject().execute("order", "changeOrderStatus", Map.of("orderId", orderId, "statusId", next));
            if ("error".equals(result.get("responseMessage"))) return result;
        }
        return Map.of();
    }

    private boolean mayApprove(Object header, String current) {
        if ("ORDER_SENT".equals(current) || "ORDER_CANCELLED".equals(current) || "ORDER_HOLD".equals(current)
                || "ORDER_COMPLETED".equals(current) && "SALES_ORDER".equals(data.get(header, "orderTypeId"))) return false;
        Object storeId = data.get(header, "productStoreId");
        if (storeId != null && !storeId.toString().isEmpty()) {
            Object store = data.find("ProductStore", Map.of("productStoreId", storeId)).stream().findFirst().orElse(null);
            Object approvedStatus = store == null ? null : data.get(store, "headerApprovedStatus");
            if (approvedStatus != null && !approvedStatus.toString().isEmpty() && approvedStatus.equals(current)) {
                Map<String, Object> filter = new LinkedHashMap<>();
                filter.put("orderId", data.get(header, "orderId"));
                filter.put("statusId", current);
                filter.put("orderItemSeqId", null);
                if (data.find("OrderStatus", filter).size() <= 1) return false;
            }
        }
        return true;
    }
}
