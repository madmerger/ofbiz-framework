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

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.product.adapter.OrderPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
        classes = ProductMigrationApplication.class,
        properties = "migration.security.require-https=false")
class LocalOrderAdapterTest {
    @Autowired
    private OrderPort orders;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void invalidItemStatusTransitionReturnsBadRequest() {
        persist("OrderHeader", Map.of("orderId", "BADSTAT_ORDER", "orderTypeId", "SALES_ORDER"));
        persist("Product", Map.of("productId", "BADSTAT_PROD", "productTypeId", "FINISHED_GOOD"));
        persist("OrderItem", Map.of(
                "orderId", "BADSTAT_ORDER",
                "orderItemSeqId", "00001",
                "productId", "BADSTAT_PROD",
                "statusId", "ITEM_CANCELLED",
                "quantity", BigDecimal.ONE));
        entityManager.flush();

        MigrationException exception = assertThrows(MigrationException.class,
                () -> orders.invoke("changeOrderItemStatus", Map.of(
                        "orderId", "BADSTAT_ORDER",
                        "orderItemSeqId", "00001",
                        "statusId", "ITEM_COMPLETED")));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Object item = entities.findByPrimaryKey(entities.entityClass("OrderItem"),
                Map.of("orderId", "BADSTAT_ORDER", "orderItemSeqId", "00001"));
        assertEquals("ITEM_CANCELLED", entities.get(item, "statusId"));
    }

    private void persist(String entityName, Map<String, Object> fields) {
        Object entity = entities.newEntity(entities.entityClass(entityName));
        fields.forEach((name, value) -> entities.set(entity, name, value));
        entityManager.persist(entity);
    }
}
