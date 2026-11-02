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
package org.apache.ofbiz.migration.workeffort;

import jakarta.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ShipmentWorkEffortService {
    private final EntityRegistry entities;
    private final ServiceHandlerRegistry registry;
    private final ObjectProvider<MigrationServiceDispatcher> dispatcher;

    public ShipmentWorkEffortService(EntityRegistry entities, ServiceHandlerRegistry registry,
            ObjectProvider<MigrationServiceDispatcher> dispatcher) {
        this.entities = entities;
        this.registry = registry;
        this.dispatcher = dispatcher;
    }

    @PostConstruct
    void register() {
        registry.registerHandler("workeffort", "checkAndUpdateWorkEffort", "OFBTOOLS_VIEW", this::checkAndUpdateWorkEffort);
    }

    public Map<String, Object> checkAndUpdateWorkEffort(Map<String, Object> input) {
        if (input.get("shipmentId") == null) throw new MigrationException(HttpStatus.BAD_REQUEST, "shipmentId is required");
        Object shipment = entities.findByPrimaryKey(entities.entityClass("Shipment"), Map.of("shipmentId", input.get("shipmentId")));
        if (shipment == null) throw new MigrationException(HttpStatus.NOT_FOUND, "Shipment not found");
        Object status = input.get("statusId");
        boolean statusChanged = changed(input, shipment, "statusId");
        if (changed(input, shipment, "estimatedShipDate") || changed(input, shipment, "originFacilityId")
                || statusChanged && List.of("SHIPMENT_CANCELLED", "SHIPMENT_PACKED", "SHIPMENT_SHIPPED").contains(status)) {
            update(shipment, "estimatedShipWorkEffId", input, "estimatedShipDate", "originFacilityId", statusChanged ? status : null);
        }
        if (changed(input, shipment, "estimatedArrivalDate") || changed(input, shipment, "destinationFacilityId")) {
            update(shipment, "estimatedArrivalWorkEffId", input, "estimatedArrivalDate", "destinationFacilityId", null);
        }
        if (changed(input, shipment, "partyIdFrom")) assign(shipment, "estimatedShipWorkEffId", input.get("partyIdFrom"));
        if (changed(input, shipment, "partyIdTo")) assign(shipment, "estimatedArrivalWorkEffId", input.get("partyIdTo"));
        return Map.of();
    }

    private boolean changed(Map<String, Object> input, Object shipment, String field) {
        Object value = input.get(field);
        if (value == null || value.toString().isEmpty()) return false;
        Object previous = entities.get(shipment, field);
        if (field.endsWith("Date") && value instanceof String text) {
            try { value = Timestamp.valueOf(text); }
            catch (IllegalArgumentException ignored) { value = Timestamp.from(Instant.parse(text)); }
        }
        return !Objects.equals(value, previous);
    }

    private void update(Object shipment, String link, Map<String, Object> input, String date, String facility, Object status) {
        Object id = entities.get(shipment, link);
        if (id == null) return;
        Object workEffort = entities.findByPrimaryKey(entities.entityClass("WorkEffort"), Map.of("workEffortId", id));
        if (workEffort == null) return;
        Map<String, Object> values = new LinkedHashMap<>(entities.toMap(workEffort));
        if (input.get(date) != null) {
            values.put("estimatedStartDate", input.get(date));
            values.put("estimatedCompletionDate", input.get(date));
        }
        if (input.get(facility) != null) values.put("facilityId", input.get(facility));
        String next = status == null ? null : switch (status.toString()) {
            case "SHIPMENT_CANCELLED" -> "CAL_CANCELLED";
            case "SHIPMENT_PACKED" -> "CAL_CONFIRMED";
            case "SHIPMENT_SHIPPED" -> "CAL_COMPLETED";
            default -> null;
        };
        if (next != null) values.put("currentStatusId", next);
        invoke("updateWorkEffort", values);
    }

    private void assign(Object shipment, String link, Object party) {
        Object id = entities.get(shipment, link);
        if (id == null || id.toString().isEmpty()) return;
        Timestamp now = Timestamp.from(Instant.now());
        boolean active = entities.findByEquality(entities.entityClass("WorkEffortPartyAssignment"),
                Map.of("workEffortId", id, "partyId", party)).stream().anyMatch(row -> {
                    Timestamp from = (Timestamp) entities.get(row, "fromDate");
                    Timestamp thru = (Timestamp) entities.get(row, "thruDate");
                    return (from == null || !from.after(now)) && (thru == null || thru.after(now));
                });
        if (!active) invoke("assignPartyToWorkEffort",
                Map.of("workEffortId", id, "partyId", party, "roleTypeId", "CAL_ATTENDEE", "statusId", "CAL_SENT"));
    }

    private void invoke(String service, Map<String, Object> input) {
        Map<String, Object> result = dispatcher.getObject().execute("workeffort", service, input);
        if ("error".equals(result.get("responseMessage")) || "fail".equals(result.get("responseMessage"))) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, Objects.toString(result.get("errorMessage"), service + " failed"));
        }
    }
}
