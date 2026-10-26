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

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.List;
import java.util.stream.Stream;
import org.apache.catalina.valves.RemoteIpValve;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(MigrationSecurityProperties.class)
public class MigrationSecurityConfiguration {
    @Bean
    OpenAPI migrationOpenApi() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes(
                        "basicAuth", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"));
    }

    @Bean
    PasswordEncoder migrationPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService migrationUserDetailsService(
            MigrationSecurityProperties properties, PasswordEncoder migrationPasswordEncoder) {
        List<UserDetails> users = properties.getUsers().stream().map(configured -> {
            String password = configured.getPasswordHash();
            if (password == null || password.isBlank()) {
                password = configured.getPassword();
            }
            if (password == null || password.isBlank()) {
                String envPassword = System.getenv("MIGRATION_DEMO_PASSWORD");
                if (envPassword == null || envPassword.isBlank()) {
                    throw new IllegalStateException("Set MIGRATION_DEMO_PASSWORD for configured demo users");
                }
                password = migrationPasswordEncoder.encode(envPassword);
            } else if (!password.startsWith("{")) {
                password = migrationPasswordEncoder.encode(password);
            }
            return User.withUsername(configured.getUsername())
                    .password(password)
                    .authorities(configured.getAuthorities().toArray(String[]::new))
                    .build();
        }).toList();
        return new InMemoryUserDetailsManager(users);
    }

    @Bean
    SecurityFilterChain migrationSecurityFilterChain(
            HttpSecurity http, MigrationSecurityProperties properties) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/**").hasAnyAuthority(Stream.concat(
                                        Stream.of(
                                                properties.getServicePermissionPrefix() + "_VIEW",
                                                properties.getServicePermissionPrefix() + "_ADMIN"),
                                        properties.getBasePermissions().stream().map(permission -> permission + "_VIEW"))
                                .toArray(String[]::new))
                        .anyRequest().permitAll());
        if (properties.isRequireHttps()) {
            http.requiresChannel(channel -> channel.anyRequest().requiresSecure());
        }
        return http.build();
    }

    @Bean
    WebServerFactoryCustomizer<TomcatServletWebServerFactory> migrationRemoteIpValve(
            MigrationSecurityProperties properties) {
        return factory -> {
            RemoteIpValve valve = new RemoteIpValve();
            valve.setInternalProxies(properties.getTrustedProxies());
            valve.setRemoteIpHeader("X-Forwarded-For");
            valve.setProtocolHeader("X-Forwarded-Proto");
            factory.addEngineValves(valve);
        };
    }
}
