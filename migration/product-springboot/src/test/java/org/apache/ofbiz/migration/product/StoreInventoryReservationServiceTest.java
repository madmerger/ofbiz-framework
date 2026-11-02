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
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.apache.ofbiz.migration.product.flow.StoreInventoryReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

class StoreInventoryReservationServiceTest {
    private final EntityRegistry entities = mock(EntityRegistry.class);
    private final MigrationServiceDispatcher dispatcher = mock(MigrationServiceDispatcher.class);
    private final Object store = new Object();
    private StoreInventoryReservationService service;
    private final Map<String, Object> input = Map.of("productStoreId", "store", "productId", "product", "quantity", 2);

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        ObjectProvider<MigrationServiceDispatcher> provider = mock(ObjectProvider.class);
        when(provider.getObject()).thenReturn(dispatcher);
        service = new StoreInventoryReservationService(entities, mock(ServiceHandlerRegistry.class), provider);
        doReturn(Object.class).when(entities).entityClass(anyString());
        doReturn(store).when(entities).findByPrimaryKey(Object.class, Map.of("productStoreId", "store"));
        doReturn(new Object()).when(entities).findByPrimaryKey(Object.class, Map.of("productId", "product"));
        doReturn(List.of()).when(entities).findByEquality(eq(Object.class), anyMap());
        when(entities.get(store, "oneInventoryFacility")).thenReturn("Y");
        when(entities.get(store, "inventoryFacilityId")).thenReturn("warehouse");
        when(entities.get(store, "reserveOrderEnumId")).thenReturn("INVRO_FIFO_REC");
    }

    @Test
    void reservationUsesTheStoreFacilityAndTheRealServiceBoundary() {
        when(dispatcher.execute(eq("product"), eq("reserveProductInventoryByFacility"), anyMap()))
                .thenReturn(Map.of("quantityNotReserved", BigDecimal.ZERO));
        assertEquals(BigDecimal.ZERO, service.reserve(input).get("quantityNotReserved"));
        verify(dispatcher).execute(eq("product"), eq("reserveProductInventoryByFacility"), argThat(parameters ->
                "warehouse".equals(parameters.get("facilityId")) && "Y".equals(parameters.get("requireInventory"))
                        && "INVRO_FIFO_REC".equals(parameters.get("reserveOrderEnumId"))));
    }

    @Test
    void disabledReservationReturnsTheQuantityWithoutInventoryWrites() {
        when(entities.get(store, "reserveInventory")).thenReturn("N");
        assertEquals(new BigDecimal("2"), service.reserve(input).get("quantityNotReserved"));
        verifyNoInteractions(dispatcher);
    }

    @Test
    void emptyAndInvalidInputsCannotDispatchInventoryWrites() {
        assertThrows(MigrationException.class, () -> service.reserve(Map.of()));
        assertThrows(MigrationException.class, () -> service.reserve(Map.of("productStoreId", "store", "productId", "product")));
        assertThrows(MigrationException.class, () -> service.reserve(Map.of("productStoreId", "store", "productId", "product",
                "quantity", 2, "facilityId", "unrelated")));
        verifyNoInteractions(dispatcher);
    }
}
