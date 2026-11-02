/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.ofbiz.migration.product.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.common.MigrationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class LocalShipmentSideEffectAdapter implements ShipmentSideEffectPort {
    private final ServiceHandlerRegistry registry;
    private final MigrationServiceDispatcher dispatcher;

    public LocalShipmentSideEffectAdapter(ServiceHandlerRegistry registry, MigrationServiceDispatcher dispatcher) {
        this.registry = registry;
        this.dispatcher = dispatcher;
    }

    @Override
    public List<String> checkAndUpdateWorkEffort(Map<String, Object> input) {
        var component = registry.componentFor("workeffort", "checkAndUpdateWorkEffort");
        if (component.isEmpty()) {
            return List.of("501: checkAndUpdateWorkEffort (workeffort component is not registered; no remote adapter configured)");
        }
        Map<String, Object> result = dispatcher.execute(component.get(), "checkAndUpdateWorkEffort", input);
        if ("error".equals(result.get("responseMessage")) || "fail".equals(result.get("responseMessage"))) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, result.getOrDefault("errorMessage", "WorkEffort update failed").toString());
        }
        return List.of();
    }

    @Override
    public List<String> onStatusChange(String shipmentTypeId, String oldStatusId, String statusId) {
        List<String> notes = new ArrayList<>();
        if ("SALES_SHIPMENT".equals(shipmentTypeId) && "SHIPMENT_PACKED".equals(statusId)) {
            notes.add("No-op: createInvoicesFromShipment");
            notes.add("No-op: setInvoicesToReadyFromShipment");
        }
        if ("SALES_SHIPMENT".equals(shipmentTypeId) && "SHIPMENT_SHIPPED".equals(statusId)) {
            notes.add("No-op: sendShipmentCompleteNotification");
        }
        if ("PURCHASE_RETURN".equals(shipmentTypeId) && "SHIPMENT_SHIPPED".equals(statusId)) {
            notes.add("No-op: createInvoicesFromReturnShipment");
        }
        if ("DROP_SHIPMENT".equals(shipmentTypeId) && "PURCH_SHIP_SHIPPED".equals(statusId)) {
            notes.add("No-op: createInvoicesFromShipment");
        }
        return notes;
    }
}
