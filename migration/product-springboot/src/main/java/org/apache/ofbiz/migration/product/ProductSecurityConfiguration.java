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

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
public class ProductSecurityConfiguration {
    @Bean
    @Order(1)
    SecurityFilterChain productSecurityFilterChain(
            HttpSecurity http, MigrationSecurityProperties properties) throws Exception {
        http.securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize.anyRequest()
                        .access((authentication, context) ->
                                new AuthorizationDecision(hasApiAccess(authentication.get()))));
        if (properties.isRequireHttps()) {
            http.addFilterBefore(new HttpsRedirectFilter(), ChannelProcessingFilter.class);
            http.requiresChannel(channel -> channel.anyRequest().requiresSecure());
        }
        return http.build();
    }

    private static final class HttpsRedirectFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(
                HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws ServletException, java.io.IOException {
            if (request.isSecure()) {
                chain.doFilter(request, response);
                return;
            }
            StringBuilder location = new StringBuilder("https://").append(request.getServerName());
            if (request.getServerPort() == 8080) {
                location.append(":8443");
            }
            location.append(request.getRequestURI());
            if (request.getQueryString() != null) {
                location.append('?').append(request.getQueryString());
            }
            response.setStatus(HttpServletResponse.SC_FOUND);
            response.setHeader("Location", location.toString());
        }
    }

    private static boolean hasApiAccess(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        Set<String> authorities = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        return hasViewOrAdmin(authorities, "OFBTOOLS")
                && (hasViewOrAdmin(authorities, "CATALOG") || hasViewOrAdmin(authorities, "FACILITY"));
    }

    private static boolean hasViewOrAdmin(Set<String> authorities, String base) {
        return authorities.contains(base + "_VIEW") || authorities.contains(base + "_ADMIN");
    }
}
