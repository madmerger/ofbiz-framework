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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.product.ProductEntityAutoRegistration;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateShipmentFlow {
    private static final Set<String> EXCLUDED_FIELDS = Set.of(
            "createdDate", "createdByUserLogin", "lastModifiedDate", "lastModifiedByUserLogin");

    private final EntityRegistry entities;
    private final MigrationServiceDispatcher dispatcher;
    private final ShipmentSideEffectPort sideEffects;
    private final ProductEntityAutoRegistration entityAutoRegistration;

    public UpdateShipmentFlow(
            EntityRegistry entities,
            MigrationServiceDispatcher dispatcher,
            ShipmentSideEffectPort sideEffects,
            ProductEntityAutoRegistration entityAutoRegistration) {
        this.entities = entities;
        this.dispatcher = dispatcher;
        this.sideEffects = sideEffects;
        this.entityAutoRegistration = entityAutoRegistration;
    }

    /**
     * Port of ShipmentServices.groovy:35-80.
     */
    @Transactional
    public Map<String, Object> execute(Map<String, Object> input) {
        entityAutoRegistration.checkShipmentStatus("checkCanChangeShipmentStatusDelivered", input);
        String shipmentId = requiredString(input, "shipmentId");
        Class<?> shipmentType = entities.entityClass("Shipment");
        Object shipment = entities.findByPrimaryKey(shipmentType, Map.of("shipmentId", shipmentId));
        if (shipment == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "ServiceValueNotFound");
        }

        String oldStatusId = stringValue(entities.get(shipment, "statusId"));
        String nextStatusId = stringValue(input.get("statusId"));
        String shipmentTypeId = stringValue(entities.get(shipment, "shipmentTypeId"));
        List<String> notes = new ArrayList<>(sideEffects.checkAndUpdateWorkEffort(input));
        if (nextStatusId != null && !nextStatusId.equals(oldStatusId)) {
            Map<String, Object> transitionFilter = new LinkedHashMap<>();
            transitionFilter.put("statusId", oldStatusId);
            transitionFilter.put("statusIdTo", nextStatusId);
            boolean valid = !entities.findByEquality(entities.entityClass("StatusValidChange"),
                    transitionFilter).isEmpty();
            if (!valid) {
                throw new MigrationException(HttpStatus.CONFLICT,
                        "ERROR: Changing the status from " + oldStatusId + " to " + nextStatusId + " is not allowed.");
            }
            Map<String, Object> statusInput = new LinkedHashMap<>();
            statusInput.put("shipmentId", shipmentId);
            statusInput.put("statusId", nextStatusId);
            if (input.get("eventDate") != null) {
                statusInput.put("statusDate", input.get("eventDate"));
            }
            dispatcher.execute("product", "createShipmentStatus", statusInput);
            notes.addAll(sideEffects.onStatusChange(shipmentTypeId, oldStatusId, nextStatusId));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("shipmentTypeId", shipmentTypeId);
        result.put("oldStatusId", oldStatusId);
        result.put("oldPrimaryOrderId", entities.get(shipment, "primaryOrderId"));
        result.put("oldOriginFacilityId", entities.get(shipment, "originFacilityId"));
        result.put("oldDestinationFacilityId", entities.get(shipment, "destinationFacilityId"));

        BeanWrapperImpl properties = new BeanWrapperImpl(shipment);
        input.forEach((name, value) -> {
            if (!"shipmentId".equals(name) && !"eventDate".equals(name)
                    && !EXCLUDED_FIELDS.contains(name) && properties.isWritableProperty(name)) {
                entities.set(shipment, name, value);
            }
        });
        entities.set(shipment, "lastModifiedDate", new Timestamp(System.currentTimeMillis()));
        entities.set(shipment, "lastModifiedByUserLogin",
                SecurityContextHolder.getContext().getAuthentication().getName());
        result.put("shipmentId", shipmentId);
        result.put("sideEffectNotes", notes);
        return result;
    }

    private static String requiredString(Map<String, Object> input, String name) {
        String value = stringValue(input.get(name));
        if (value == null || value.isBlank()) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Missing required input: " + name);
        }
        return value;
    }

    private static String stringValue(Object value) {
        return value == null ? null : value.toString();
    }
}
