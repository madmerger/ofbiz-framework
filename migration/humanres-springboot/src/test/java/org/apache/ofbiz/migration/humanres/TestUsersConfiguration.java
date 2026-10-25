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
                        .build());
    }
}
