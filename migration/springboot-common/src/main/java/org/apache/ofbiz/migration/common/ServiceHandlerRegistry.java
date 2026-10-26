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
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class ServiceHandlerRegistry {
    private final Map<String, Map<String, ServiceDefinition>> catalogs = new ConcurrentHashMap<>();
    private final Map<String, Map<String, HandlerRegistration>> handlers = new ConcurrentHashMap<>();

    public void registerCatalog(String component, Map<String, ServiceDefinition> services) {
        catalogs.put(component, Map.copyOf(services));
    }

    public void registerHandler(String component, String service, SimpleServiceHandler handler) {
        registerHandler(component, service, null, null, handler);
    }

    public void registerHandler(
            String component, String service, String requiredPermission, SimpleServiceHandler handler) {
        registerHandler(component, service, requiredPermission, null, handler);
    }

    public void registerHandler(
            String component,
            String service,
            String requiredPermission,
            String sensitiveResultEntity,
            SimpleServiceHandler handler) {
        HandlerRegistration registration = new HandlerRegistration(requiredPermission, sensitiveResultEntity, handler);
        handlers.computeIfAbsent(component, ignored -> new ConcurrentHashMap<>()).put(service, registration);
    }

    public Optional<ServiceDefinition> find(String component, String name) {
        return Optional.ofNullable(catalogs.getOrDefault(component, Map.of()).get(name));
    }

    public Optional<HandlerRegistration> handler(String component, String name) {
        return Optional.ofNullable(handlers.getOrDefault(component, Map.of()).get(name));
    }

    public record ParamDefinition(String name, String mode, boolean optional, String type) { }
    public record ServiceDefinition(
            String name, String entity, String invoke, String permissionMainAction, List<ParamDefinition> params) { }
    public record HandlerRegistration(
            String requiredPermission, String sensitiveResultEntity, SimpleServiceHandler handler) { }

    @FunctionalInterface
    public interface SimpleServiceHandler {
        Map<String, Object> execute(Map<String, Object> input);
    }
}
