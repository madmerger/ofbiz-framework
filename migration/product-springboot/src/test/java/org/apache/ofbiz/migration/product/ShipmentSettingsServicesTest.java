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
package org.apache.ofbiz.migration.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.apache.ofbiz.migration.product.flow.ShipmentSettingsServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShipmentSettingsServicesTest {
    private final EntityRegistry entities = mock(EntityRegistry.class);
    private final Map<String, Object> shipment = new LinkedHashMap<>(Map.of("shipmentId", "S"));
    private final ShipmentSettingsServices service = new ShipmentSettingsServices(entities, mock(ServiceHandlerRegistry.class));

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setup() {
        doReturn(Map.class).when(entities).entityClass(anyString());
        when(entities.findByPrimaryKey(Map.class, Map.of("shipmentId", "S"))).thenReturn(shipment);
        when(entities.get(any(), anyString())).thenAnswer(call -> ((Map<?, ?>) call.getArgument(0)).get(call.getArgument(1)));
        doAnswer(call -> ((Map<String, Object>) call.getArgument(0)).put(call.getArgument(1), call.getArgument(2)))
                .when(entities).set(any(), anyString(), any());
    }

    @Test
    void facilitiesUseActivePreferredPurposesWithoutOverwritingExplicitFields() {
        shipment.put("originFacilityId", "F");
        shipment.put("originTelecomNumberId", "KEEP");
        when(entities.findByEquality(eq(Map.class), anyMap())).thenReturn(List.of(
                Map.of("contactMechId", "EXPIRED", "thruDate", Timestamp.valueOf("2000-01-01 00:00:00")),
                Map.of("contactMechId", "ADDRESS")));
        service.fromFacilities(Map.of("shipmentId", "S"));
        assertEquals("ADDRESS", shipment.get("originContactMechId"));
        assertEquals("KEEP", shipment.get("originTelecomNumberId"));
    }

    @Test
    void primaryOrderCopiesCustomerAndDefaultsZeroShippingCost() {
        shipment.put("primaryOrderId", "O");
        when(entities.findByPrimaryKey(Map.class, Map.of("orderId", "O"))).thenReturn(Map.of("orderTypeId", "SALES_ORDER"));
        when(entities.findByEquality(Map.class, Map.of("orderId", "O", "roleTypeId", "CUSTOMER"))).thenReturn(List.of(Map.of("partyId", "CUSTOMER")));
        service.fromOrder(Map.of("shipmentId", "S"));
        assertEquals("CUSTOMER", shipment.get("partyIdTo"));
        assertEquals(BigDecimal.ZERO, shipment.get("estimatedShipCost"));
    }

    @Test
    void missingFacilitiesAndPrimaryOrderLeaveShipmentUnchanged() {
        assertEquals(Map.of(), service.fromFacilities(Map.of("shipmentId", "S")));
        assertEquals(Map.of(), service.fromOrder(Map.of("shipmentId", "S")));
        assertEquals(Map.of("shipmentId", "S"), shipment);
    }

    @Test
    void missingIdentifierAndUnknownShipmentAreErrors() {
        assertThrows(MigrationException.class, () -> service.fromFacilities(Map.of()));
        assertThrows(MigrationException.class, () -> service.fromOrder(Map.of("shipmentId", "MISSING")));
    }
}
