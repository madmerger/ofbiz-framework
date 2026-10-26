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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.apache.ofbiz.migration.humanres;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EntityQueryService {
    private final EntityRegistry entityRegistry;

    public EntityQueryService(EntityRegistry entityRegistry) {
        this.entityRegistry = entityRegistry;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> list(
            String entityName, Map<String, String> requestParams, int page, int size) {
        Map<String, String> filters = new LinkedHashMap<>(requestParams);
        filters.remove("page");
        filters.remove("size");
        var values = entityRegistry.findPage(entityName, filters, page, size);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("items", values);
        response.put("page", page);
        response.put("size", size);
        response.put("total", entityRegistry.count(entityName, filters));
        return response;
    }

    @Transactional(readOnly = true)
    public Object one(String entityName, Map<String, String> primaryKeys) {
        Object entity = entityRegistry.findByPrimaryKey(entityName, primaryKeys);
        if (entity == null) {
            throw HumanResException.notFound("ServiceValueNotFound");
        }
        return entity;
    }
}
