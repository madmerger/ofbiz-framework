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

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class StoreInventoryReservationService {
    private final EntityRegistry entities;
    private final ServiceHandlerRegistry registry;
    private final ObjectProvider<MigrationServiceDispatcher> dispatcher;

    public StoreInventoryReservationService(EntityRegistry entities, ServiceHandlerRegistry registry,
            ObjectProvider<MigrationServiceDispatcher> dispatcher) {
        this.entities = entities;
        this.registry = registry;
        this.dispatcher = dispatcher;
    }

    @PostConstruct
    void register() {
        registry.registerHandler("product", "reserveStoreInventory", "OFBTOOLS_VIEW", this::reserve);
    }

    public Map<String, Object> reserve(Map<String, Object> input) {
        Object store = require("ProductStore", "productStoreId", input.get("productStoreId"));
        Object product = require("Product", "productId", input.get("productId"));
        BigDecimal quantity;
        try {
            quantity = new BigDecimal(input.get("quantity").toString());
        } catch (RuntimeException exception) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "quantity is required and must be a decimal");
        }
        if ("N".equals(entities.get(store, "reserveInventory"))) return Map.of("quantityNotReserved", quantity);
        String requireInventory = text(entities.get(product, "requireInventory"));
        if (requireInventory == null) requireInventory = text(entities.get(store, "requireInventory"));
        Map<String, Object> parameters = new LinkedHashMap<>(input);
        parameters.put("requireInventory", requireInventory == null ? "Y" : requireInventory);
        parameters.put("reserveOrderEnumId", entities.get(store, "reserveOrderEnumId"));
        Object order = input.get("orderId") == null ? null : entities.findByPrimaryKey(entities.entityClass("OrderHeader"),
                Map.of("orderId", input.get("orderId")));
        parameters.put("priority", order == null ? null : entities.get(order, "priority"));
        List<?> facilities = entities.findByEquality(entities.entityClass("ProductStoreFacility"),
                Map.of("productStoreId", input.get("productStoreId"))).stream().filter(this::active)
                .sorted(Comparator.comparingLong(row -> entities.get(row, "sequenceNum") instanceof Number number
                        ? number.longValue() : Long.MAX_VALUE)).toList();
        String facility = text(input.get("facilityId"));
        if (facility != null) {
            String supplied = facility;
            if (facilities.stream().noneMatch(row -> supplied.equals(entities.get(row, "facilityId")))) {
                throw new MigrationException(HttpStatus.BAD_REQUEST, "Facility is not associated with the product store");
            }
        } else if ("Y".equals(entities.get(store, "oneInventoryFacility"))) {
            facility = text(entities.get(store, "inventoryFacilityId"));
        } else {
            for (Object candidate : facilities) {
                Map<String, Object> availability = dispatcher.getObject().execute("product", "getInventoryAvailableByFacility",
                        Map.of("productId", input.get("productId"), "facilityId", entities.get(candidate, "facilityId")));
                Object available = availability.get("availableToPromiseTotal");
                if (available != null && new BigDecimal(available.toString()).compareTo(quantity) >= 0) {
                    facility = text(entities.get(candidate, "facilityId"));
                    break;
                }
            }
            if (facility == null) facility = text(entities.get(store, "inventoryFacilityId"));
            if (facility == null && !facilities.isEmpty()) facility = text(entities.get(facilities.get(0), "facilityId"));
        }
        if (facility == null) throw new MigrationException(HttpStatus.BAD_REQUEST, "No inventory facility is configured");
        parameters.put("facilityId", facility);
        Map<String, Object> result = dispatcher.getObject().execute("product", "reserveProductInventoryByFacility", parameters);
        return Map.of("quantityNotReserved", result.get("quantityNotReserved"));
    }

    private boolean active(Object row) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp from = (Timestamp) entities.get(row, "fromDate");
        Timestamp thru = (Timestamp) entities.get(row, "thruDate");
        return (from == null || !from.after(now)) && (thru == null || thru.after(now));
    }

    private Object require(String entity, String field, Object id) {
        if (text(id) == null) throw new MigrationException(HttpStatus.BAD_REQUEST, field + " is required");
        Object row = entities.findByPrimaryKey(entities.entityClass(entity), Map.of(field, id));
        if (row == null) throw new MigrationException(HttpStatus.NOT_FOUND, entity + " not found");
        return row;
    }

    private static String text(Object value) {
        return value == null || value.toString().isBlank() ? null : value.toString();
    }
}
