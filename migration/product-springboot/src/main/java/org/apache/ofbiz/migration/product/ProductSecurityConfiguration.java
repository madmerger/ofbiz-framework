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
package org.apache.ofbiz.migration.product;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;
import org.apache.ofbiz.migration.common.MigrationSecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ProductSecurityConfiguration {
    @Bean
    @Order(1)
    SecurityFilterChain productSecurityFilterChain(
            HttpSecurity http,
            MigrationSecurityProperties properties,
            ProductEntityOwnership entityOwnership) throws Exception {
        http.securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize.anyRequest()
                        .access((authentication, context) ->
                                new AuthorizationDecision(hasApiAccess(
                                        authentication.get(), context.getRequest(), entityOwnership))));
        if (properties.isRequireHttps()) {
            http.requiresChannel(channel -> channel.anyRequest().requiresSecure());
        }
        return http.build();
    }

    private static boolean hasApiAccess(
            Authentication authentication, HttpServletRequest request, ProductEntityOwnership entityOwnership) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        Set<String> authorities = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        if (!hasViewOrAdmin(authorities, "OFBTOOLS")) {
            return false;
        }
        String entityName = entityName(request);
        if (entityName != null) {
            Set<String> owners = entityOwnership.ownerBases(entityName);
            if (owners != null) {
                return owners.stream().anyMatch(owner -> hasViewOrAdmin(authorities, owner));
            }
            return true;
        }
        return hasViewOrAdmin(authorities, "CATALOG") || hasViewOrAdmin(authorities, "FACILITY");
    }

    private static String entityName(HttpServletRequest request) {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return null;
        }
        String path = request.getRequestURI().substring(request.getContextPath().length());
        String prefix = "/api/product/entities/";
        if (!path.startsWith(prefix)) {
            return null;
        }
        String remainder = path.substring(prefix.length());
        int slash = remainder.indexOf('/');
        String name = slash < 0 ? remainder : remainder.substring(0, slash);
        return name.isBlank() ? null : name;
    }

    private static boolean hasViewOrAdmin(Set<String> authorities, String base) {
        return authorities.contains(base + "_VIEW") || authorities.contains(base + "_ADMIN");
    }
}
