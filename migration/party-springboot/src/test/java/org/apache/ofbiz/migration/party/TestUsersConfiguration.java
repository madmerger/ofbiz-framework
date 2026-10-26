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
package org.apache.ofbiz.migration.party;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@TestConfiguration
public class TestUsersConfiguration {
    @Bean
    @Primary
    AuthenticationProvider testAuthenticationProvider(UserDetailsService testUserDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(testUserDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        return provider;
    }

    @Bean
    @Primary
    UserDetailsService testUserDetailsService() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return new InMemoryUserDetailsManager(
                User.withUsername("admin").password(encoder.encode("admin-password"))
                        .authorities("OFBTOOLS_VIEW", "OFBTOOLS_ADMIN", "PARTYMGR_ADMIN").build(),
                User.withUsername("readonly").password(encoder.encode("readonly-password"))
                        .authorities("OFBTOOLS_VIEW", "PARTYMGR_VIEW").build(),
                User.withUsername("nopartymgr").password(encoder.encode("nopartymgr-password"))
                        .authorities("OFBTOOLS_VIEW").build(),
                User.withUsername("sensitive").password(encoder.encode("sensitive-password"))
                        .authorities("OFBTOOLS_VIEW", "PARTYMGR_VIEW", "PARTYMGR_SENSITIVE_VIEW").build(),
                User.withUsername("writer").password(encoder.encode("writer-password"))
                        .authorities("OFBTOOLS_VIEW", "PARTYMGR_VIEW", "PARTYMGR_CREATE",
                                "PARTYMGR_UPDATE", "PARTYMGR_DELETE").build());
    }
}
