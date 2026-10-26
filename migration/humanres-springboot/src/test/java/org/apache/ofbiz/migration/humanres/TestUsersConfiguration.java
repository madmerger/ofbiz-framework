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

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@TestConfiguration
public class TestUsersConfiguration {
    @Bean
    @Primary
    UserDetailsService testUserDetailsService() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return new InMemoryUserDetailsManager(
                User.withUsername("admin")
                        .password(encoder.encode("admin"))
                        .authorities("OFBTOOLS_ADMIN", "HUMANRES_ADMIN", "HUMANRES_VIEW")
                        .build(),
                User.withUsername("nohr")
                        .password(encoder.encode("nohr"))
                        .authorities("OFBTOOLS_VIEW")
                        .build(),
                User.withUsername("noofb")
                        .password(encoder.encode("noofb"))
                        .authorities("HUMANRES_VIEW")
                        .build(),
                User.withUsername("view")
                        .password(encoder.encode("view"))
                        .authorities("OFBTOOLS_VIEW", "HUMANRES_VIEW")
                        .build(),
                User.withUsername("no-create")
                        .password(encoder.encode("no-create"))
                        .authorities("OFBTOOLS_VIEW", "HUMANRES_VIEW")
                        .build(),
                User.withUsername("sensitive")
                        .password(encoder.encode("sensitive"))
                        .authorities("OFBTOOLS_VIEW", "HUMANRES_VIEW", "HUMANRES_SENSITIVE_VIEW")
                        .build(),
                User.withUsername("writer")
                        .password(encoder.encode("writer"))
                        .authorities(
                                "OFBTOOLS_VIEW",
                                "HUMANRES_VIEW",
                                "HUMANRES_CREATE",
                                "HUMANRES_UPDATE",
                                "HUMANRES_DELETE")
                        .build());
    }
}
