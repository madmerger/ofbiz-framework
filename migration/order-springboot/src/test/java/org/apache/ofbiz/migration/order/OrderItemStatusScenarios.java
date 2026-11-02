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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

abstract class OrderItemStatusScenarios {
    @Autowired private OrderEntitySupport data;
    @Autowired private MigrationServiceDispatcher dispatcher;

    @Test
    @Transactional
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_ADMIN"})
    void itemCommitRulesCompleteCancelAndApproveHeadersWithOneHistoryRow() {
        for (String next : new String[] {"COMPLETED", "CANCELLED", "APPROVED"}) {
            String id = "GAP_" + next;
            seed(id, next.equals("APPROVED") ? "ORDER_CREATED" : "ORDER_APPROVED");
            for (String seq : new String[] {"00001", "00002"}) {
                data.create("OrderItem", Map.of("orderId", id, "orderItemSeqId", seq,
                        "statusId", next.equals("APPROVED") ? "ITEM_CREATED" : "ITEM_APPROVED"));
            }
            dispatcher.execute("order", "changeOrderItemStatus", Map.of("orderId", id, "statusId", "ITEM_" + next));
            assertEquals("ORDER_" + next, data.get(data.require("OrderHeader", Map.of("orderId", id)), "statusId"));
            Map<String, Object> headerStatus = new java.util.LinkedHashMap<>(Map.of("orderId", id, "statusId", "ORDER_" + next));
            headerStatus.put("orderItemSeqId", null);
            assertEquals(1, data.find("OrderStatus", headerStatus).size());
            assertEquals(3, data.find("OrderStatus", Map.of("orderId", id)).size());
            dispatcher.execute("order", "checkOrderItemStatus", Map.of("orderId", id));
            assertEquals(3, data.find("OrderStatus", Map.of("orderId", id)).size());
        }
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_ADMIN"})
    void emptyOrderCancelsButHeldApprovedItemsDoNotAutoApprove() {
        seed("GAP_EMPTY", "ORDER_APPROVED");
        dispatcher.execute("order", "checkOrderItemStatus", Map.of("orderId", "GAP_EMPTY"));
        assertEquals("ORDER_CANCELLED", data.get(data.require("OrderHeader", Map.of("orderId", "GAP_EMPTY")), "statusId"));
        seed("GAP_HOLD", "ORDER_HOLD");
        data.create("OrderItem", Map.of("orderId", "GAP_HOLD", "orderItemSeqId", "00001", "statusId", "ITEM_APPROVED"));
        dispatcher.execute("order", "checkOrderItemStatus", Map.of("orderId", "GAP_HOLD"));
        assertEquals("ORDER_HOLD", data.get(data.require("OrderHeader", Map.of("orderId", "GAP_HOLD")), "statusId"));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_ADMIN"})
    void itemCancellationReasonDoesNotPropagateToTheRolledUpHeader() {
        seed("GAP_REASON", "ORDER_APPROVED");
        data.create("OrderItem", Map.of("orderId", "GAP_REASON", "orderItemSeqId", "00001", "statusId", "ITEM_APPROVED"));
        dispatcher.execute("order", "changeOrderItemStatus", Map.of("orderId", "GAP_REASON",
                "orderItemSeqId", "00001", "statusId", "ITEM_CANCELLED", "changeReason", "Customer request"));
        Object itemHistory = data.find("OrderStatus", Map.of("orderId", "GAP_REASON", "statusId", "ITEM_CANCELLED")).get(0);
        Object headerHistory = data.find("OrderStatus", Map.of("orderId", "GAP_REASON", "statusId", "ORDER_CANCELLED")).get(0);
        assertEquals("Customer request", data.get(itemHistory, "changeReason"));
        assertEquals(null, data.get(headerHistory, "changeReason"));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_ADMIN"})
    void missingOrderIsRejected() {
        assertThrows(MigrationException.class,
                () -> dispatcher.execute("order", "checkOrderItemStatus", Map.of()));
        assertThrows(MigrationException.class,
                () -> dispatcher.execute("order", "checkOrderItemStatus", Map.of("orderId", "GAP_MISSING")));
    }

    private void seed(String id, String status) {
        data.create("OrderHeader", Map.of("orderId", id, "orderTypeId", "SALES_ORDER", "statusId", status));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_ADMIN"})
    void headerStatusResponseContainsBusinessStatusRatherThanHistoryPrimaryKey() {
        seed("GAP_STATUS_OUTPUT", "ORDER_CREATED");
        Object header = data.require("OrderHeader", Map.of("orderId", "GAP_STATUS_OUTPUT"));
        data.set(header, "needsInventoryIssuance", "Y");
        Map<String, Object> result = dispatcher.execute("order", "changeOrderStatus",
                Map.of("orderId", "GAP_STATUS_OUTPUT", "statusId", "ORDER_APPROVED"));
        assertEquals("ORDER_APPROVED", result.get("orderStatusId"));
        assertEquals("Y", result.get("needsInventoryIssuance"));
        assertEquals("ORDER_CREATED", result.get("oldStatusId"));
        Map<String, Object> repeated = dispatcher.execute("order", "changeOrderStatus",
                Map.of("orderId", "GAP_STATUS_OUTPUT", "statusId", "ORDER_APPROVED"));
        assertEquals(false, repeated.containsKey("orderStatusId"));
        assertEquals(1, data.find("OrderStatus", Map.of("orderId", "GAP_STATUS_OUTPUT")).size());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_ADMIN"})
    void missingHeaderStatusInputsDoNotCreateHistory() {
        assertThrows(MigrationException.class, () -> dispatcher.execute("order", "changeOrderStatus", Map.of()));
        assertThrows(MigrationException.class, () -> dispatcher.execute("order", "changeOrderStatus",
                Map.of("orderId", "GAP_STATUS_MISSING", "statusId", "ORDER_APPROVED")));
        assertEquals(0, data.find("OrderStatus", Map.of("orderId", "GAP_STATUS_MISSING")).size());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {"OFBTOOLS_VIEW", "ORDERMGR_ADMIN"})
    void nextOrderIdHonorsBothOriginalPrefixesAndRejectsMissingOrganization() {
        data.create("Party", Map.of("partyId", "GAP_PREFIX_ORG", "partyTypeId", "PARTY_GROUP"));
        data.create("PartyAcctgPreference", Map.of("partyId", "GAP_PREFIX_ORG", "orderIdPrefix", "CO"));
        data.create("ProductStore", Map.of("productStoreId", "GAP_PREFIX_STORE", "orderNumberPrefix", "WS"));
        assertEquals("WSCO123", dispatcher.execute("order", "getNextOrderId", Map.of("partyId", "GAP_PREFIX_ORG",
                "productStoreId", "GAP_PREFIX_STORE", "orderId", "123")).get("orderId"));
        assertEquals("456", dispatcher.execute("order", "getNextOrderId",
                Map.of("partyId", "GAP_NO_PREFERENCES", "orderId", "456")).get("orderId"));
        assertThrows(MigrationException.class, () -> dispatcher.execute("order", "getNextOrderId", Map.of()));
        assertEquals(0, data.find("OrderHeader", Map.of("orderId", "WSCO123")).size());
    }
}
