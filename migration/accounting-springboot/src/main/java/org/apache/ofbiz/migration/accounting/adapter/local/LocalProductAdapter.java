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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.apache.ofbiz.migration.accounting.adapter.local;

import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.accounting.ProductPort;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class LocalProductAdapter implements ProductPort {
    private final EntityRegistry entities;

    public LocalProductAdapter(EntityRegistry entities) {
        this.entities = entities;
    }

    @Override
    public Map<String, Object> findProduct(String productId) {
        Object product = entities.findByPrimaryKey(entities.entityClass("Product"), Map.of("productId", productId));
        return product == null ? null : entities.toMap(product);
    }

    @Override
    public String invoiceItemType(String invoiceTypeId, String orderItemTypeId) {
        List<?> mappings = entities.findByEquality(entities.entityClass("InvoiceItemTypeMap"), Map.of(
                "invoiceTypeId", invoiceTypeId, "invoiceItemMapKey", orderItemTypeId));
        if (!mappings.isEmpty()) {
            Object mapping = mappings.get(0);
            Object result = entities.get(mapping, "invoiceItemTypeId");
            if (result != null) {
                return result.toString();
            }
        }
        return "INV_FPROD_ITEM";
    }
}
