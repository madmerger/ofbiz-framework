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
package org.apache.ofbiz.migration.accounting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import java.util.Map;
import java.util.Optional;
import org.apache.ofbiz.migration.accounting.adapter.local.LocalInventoryAccountingAdapter;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.common.SequenceService;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;

class LocalInventoryAccountingDispatchTest {
    @Test
    @SuppressWarnings("unchecked")
    void cohostedDetailsUseTheProductServiceAndPropagateValidationFailuresWithoutRepositoryWrites() {
        ServiceHandlerRegistry registry = mock(ServiceHandlerRegistry.class);
        MigrationServiceDispatcher dispatcher = mock(MigrationServiceDispatcher.class);
        ObjectProvider<MigrationServiceDispatcher> provider = mock(ObjectProvider.class);
        EntityManager manager = mock(EntityManager.class);
        EntityRegistry entities = mock(EntityRegistry.class);
        SequenceService sequences = mock(SequenceService.class);
        when(registry.componentFor("product", "createInventoryItemDetail")).thenReturn(Optional.of("product"));
        when(provider.getObject()).thenReturn(dispatcher);
        var adapter = new LocalInventoryAccountingAdapter(entities, manager, sequences, registry, provider);
        Map<String, Object> input = Map.of("inventoryItemId", "ITEM", "accountingQuantityDiff", "2");
        when(dispatcher.execute("product", "createInventoryItemDetail", input)).thenReturn(Map.of("inventoryItemDetailSeqId", "1"));
        assertEquals(Map.of("inventoryItemDetailSeqId", "1"), adapter.createInventoryItemDetail(input));
        when(dispatcher.execute("product", "createInventoryItemDetail", Map.of()))
                .thenThrow(new MigrationException(HttpStatus.BAD_REQUEST, "Missing inventoryItemId"));
        assertThrows(MigrationException.class, () -> adapter.createInventoryItemDetail(Map.of()));
        verifyNoInteractions(manager, entities, sequences);
    }
}
