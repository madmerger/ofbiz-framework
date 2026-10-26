package org.apache.ofbiz.migration.marketing;

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
                user(encoder, "admin", "OFBTOOLS_VIEW", "MARKETING_VIEW", "MARKETING_ADMIN"),
                user(encoder, "marketer", "OFBTOOLS_VIEW", "MARKETING_VIEW", "MARKETING_CREATE",
                        "MARKETING_UPDATE", "MARKETING_DELETE"),
                user(encoder, "readonly", "OFBTOOLS_VIEW", "MARKETING_VIEW"),
                user(encoder, "sensitive", "OFBTOOLS_VIEW", "MARKETING_VIEW", "MARKETING_SENSITIVE_VIEW"),
                user(encoder, "nomarketing", "OFBTOOLS_VIEW"));
    }

    private static org.springframework.security.core.userdetails.UserDetails user(
            BCryptPasswordEncoder encoder, String name, String... authorities) {
        return User.withUsername(name).password(encoder.encode(name)).authorities(authorities).build();
    }
}
