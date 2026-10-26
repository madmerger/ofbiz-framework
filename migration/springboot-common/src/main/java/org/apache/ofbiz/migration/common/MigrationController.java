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
package org.apache.ofbiz.migration.common;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/{component}")
public class MigrationController {
    private final MigrationServiceDispatcher dispatcher;
    private final EntityRegistry entities;
    private final MigrationSecurityProperties securityProperties;

    @Autowired
    public MigrationController(
            MigrationServiceDispatcher dispatcher,
            EntityRegistry entities,
            MigrationSecurityProperties securityProperties) {
        this.dispatcher = dispatcher;
        this.entities = entities;
        this.securityProperties = securityProperties;
    }

    public MigrationController(MigrationServiceDispatcher dispatcher, EntityRegistry entities) {
        this(dispatcher, entities, new MigrationSecurityProperties());
    }

    @PostMapping("/services/{serviceName}")
    public Map<String, Object> service(
            @PathVariable("component") String component,
            @PathVariable("serviceName") String serviceName,
            @RequestBody Map<String, Object> input) {
        requireComponent(component);
        return dispatcher.execute(component, serviceName, input);
    }

    @PostMapping("/services/{serviceName}/multi")
    public List<Map<String, Object>> multi(
            @PathVariable("component") String component,
            @PathVariable("serviceName") String serviceName,
            @RequestBody List<Map<String, Object>> input) {
        requireComponent(component);
        return dispatcher.executeMulti(component, serviceName, input);
    }

    @GetMapping("/entities/{entityName}")
    public List<Map<String, Object>> list(
            @PathVariable("component") String component,
            @PathVariable("entityName") String entityName,
            @RequestParam Map<String, String> filters) {
        requireComponent(component);
        Class<?> type = entities.entityClass(entityName);
        entities.requireComponentEntity(component, type);
        entities.requireSensitiveView(type);
        filters.remove("page");
        filters.remove("size");
        return entities.findByEquality(type, filters, 100).stream().map(entities::toMap).toList();
    }

    @GetMapping("/entities/{entityName}/one")
    public Map<String, Object> one(
            @PathVariable("component") String component,
            @PathVariable("entityName") String entityName,
            @RequestParam Map<String, String> keys) {
        requireComponent(component);
        Class<?> type = entities.entityClass(entityName);
        entities.requireComponentEntity(component, type);
        entities.requireSensitiveView(type);
        Object entity = entities.findByPrimaryKey(type, keys);
        if (entity == null) throw new MigrationException(HttpStatus.NOT_FOUND, "Entity not found");
        return entities.toMap(entity);
    }

    private void requireComponent(String component) {
        if (securityProperties.getComponent() == null
                || securityProperties.getComponent().isBlank()
                || !securityProperties.getComponent().equals(component)) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "Unknown component: " + component);
        }
    }
}
