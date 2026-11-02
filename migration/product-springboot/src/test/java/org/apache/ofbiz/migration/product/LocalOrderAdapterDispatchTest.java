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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import java.util.Map;
import java.util.Optional;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.common.SequenceService;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.apache.ofbiz.migration.product.adapter.LocalOrderAdapter;
import org.apache.ofbiz.migration.product.adapter.ProductUnportedPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

class LocalOrderAdapterDispatchTest {
    private final EntityRegistry entities = mock(EntityRegistry.class);
    private final MigrationServiceDispatcher dispatcher = mock(MigrationServiceDispatcher.class);

    @SuppressWarnings("unchecked")
    private LocalOrderAdapter adapter() {
        var adapter = new LocalOrderAdapter(mock(ProductUnportedPort.class), entities, mock(ProductQueries.class),
                mock(EntityManager.class), mock(SequenceService.class));
        var registry = mock(ServiceHandlerRegistry.class);
        when(registry.componentFor("order", "changeOrderItemStatus")).thenReturn(Optional.of("order"));
        ObjectProvider<ServiceHandlerRegistry> registries = mock(ObjectProvider.class);
        ObjectProvider<MigrationServiceDispatcher> dispatchers = mock(ObjectProvider.class);
        when(registries.getObject()).thenReturn(registry);
        when(dispatchers.getObject()).thenReturn(dispatcher);
        ReflectionTestUtils.setField(adapter, "serviceRegistry", registries);
        ReflectionTestUtils.setField(adapter, "serviceDispatcher", dispatchers);
        return adapter;
    }

    @Test
    void coHostedOrderChangesUseTheDispatcherInsteadOfBypassingCommitRules() {
        Map<String, Object> input = Map.of("orderId", "ORDER", "orderItemSeqId", "00001", "statusId", "ITEM_COMPLETED");
        when(dispatcher.execute("order", "changeOrderItemStatus", input)).thenReturn(Map.of("responseMessage", "success"));
        assertEquals("success", adapter().invoke("changeOrderItemStatus", input).get("responseMessage"));
        verifyNoInteractions(entities);
    }

    @Test
    void coHostedValidationErrorsArePropagatedWithoutLocalWrites() {
        MigrationException error = new MigrationException(HttpStatus.BAD_REQUEST, "Missing orderId");
        when(dispatcher.execute("order", "changeOrderItemStatus", Map.of())).thenThrow(error);
        assertSame(error, assertThrows(MigrationException.class, () -> adapter().invoke("changeOrderItemStatus", Map.of())));
        verifyNoInteractions(entities);
    }
}
