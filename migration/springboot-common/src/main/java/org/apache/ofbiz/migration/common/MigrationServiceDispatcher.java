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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MigrationServiceDispatcher {
    private final ServiceHandlerRegistry registry;
    private final EntityAutoService entityAutoService;
    private final EntityRegistry entityRegistry;
    private final MigrationSecurityProperties securityProperties;

    public MigrationServiceDispatcher(
            ServiceHandlerRegistry registry,
            EntityAutoService entityAutoService,
            EntityRegistry entityRegistry,
            MigrationSecurityProperties securityProperties) {
        this.registry = registry;
        this.entityAutoService = entityAutoService;
        this.entityRegistry = entityRegistry;
        this.securityProperties = securityProperties;
    }

    @Transactional
    public Map<String, Object> execute(String component, String serviceName, Map<String, ?> input) {
        ServiceHandlerRegistry.ServiceDefinition definition = registry.find(component, serviceName)
                .orElse(null);
        ServiceHandlerRegistry.HandlerRegistration handler = registry.handler(component, serviceName)
                .orElse(null);
        if (definition == null && handler == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "Unknown service: " + serviceName);
        }
        if (definition != null) {
            requireServicePermission(definition);
            if (!definition.entity().isBlank()) {
                entityRegistry.requireSensitiveView(entityRegistry.entityClass(definition.entity()));
            }
        }
        if (handler != null) {
            requireHandlerPermission(handler.requiredPermission());
            if (handler.sensitiveResultEntity() != null && !handler.sensitiveResultEntity().isBlank()) {
                entityRegistry.requireSensitiveView(entityRegistry.entityClass(handler.sensitiveResultEntity()));
            }
        }
        Map<String, Object> output = handler == null
                ? entityAutoService.execute(definition, input)
                : handler.handler().execute(new LinkedHashMap<>(input));
        Map<String, Object> response = new LinkedHashMap<>(output);
        response.put("responseMessage", "success");
        return response;
    }

    private void requireHandlerPermission(String requiredPermission) {
        String prefix = securityProperties.getServicePermissionPrefix();
        String requiredAuthority = requiredPermission == null || requiredPermission.isBlank()
                ? prefix + "_ADMIN"
                : requiredPermission.contains("_")
                        ? requiredPermission.toUpperCase(Locale.ROOT)
                        : prefix + "_" + requiredPermission.toUpperCase(Locale.ROOT);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> authorities = authentication == null
                ? Set.of()
                : AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        if (!authorities.contains(prefix + "_ADMIN") && !authorities.contains(requiredAuthority)) {
            throw new MigrationException(HttpStatus.FORBIDDEN, "Service permission denied");
        }
    }

    private void requireServicePermission(ServiceHandlerRegistry.ServiceDefinition definition) {
        if (definition == null) {
            return;
        }
        String action = definition.permissionMainAction();
        if (action == null || action.isBlank()) {
            action = switch (definition.invoke() == null ? "" : definition.invoke().toLowerCase(Locale.ROOT)) {
                case "create" -> "CREATE";
                case "update", "expire" -> "UPDATE";
                case "delete" -> "DELETE";
                default -> "VIEW";
            };
        } else {
            action = action.toUpperCase(Locale.ROOT);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> authorities = authentication == null
                ? Set.of()
                : AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        String prefix = securityProperties.getServicePermissionPrefix();
        if (!authorities.contains(prefix + "_ADMIN") && !authorities.contains(prefix + "_" + action)) {
            throw new MigrationException(HttpStatus.FORBIDDEN, "Service permission denied");
        }
    }

    @Transactional
    public List<Map<String, Object>> executeMulti(
            String component, String serviceName, List<Map<String, Object>> inputs) {
        if (registry.find(component, serviceName).isEmpty() && registry.handler(component, serviceName).isEmpty()) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "Unknown service: " + serviceName);
        }
        List<Map<String, Object>> results = new ArrayList<>();
        List<Map<String, Object>> errors = new ArrayList<>();
        for (int index = 0; index < inputs.size(); index++) {
            try {
                results.add(execute(component, serviceName, inputs.get(index)));
            } catch (RuntimeException exception) {
                errors.add(Map.of("index", index, "errorMessage",
                        exception.getMessage() == null ? "Service failed" : exception.getMessage()));
            }
        }
        if (!errors.isEmpty()) {
            throw new MultiServiceException(errors);
        }
        return results;
    }

    public static class MultiServiceException extends RuntimeException {
        private final List<Map<String, Object>> errors;

        public MultiServiceException(List<Map<String, Object>> errors) {
            super("One or more service calls failed");
            this.errors = List.copyOf(errors);
        }

        public List<Map<String, Object>> getErrors() {
            return errors;
        }
    }
}
