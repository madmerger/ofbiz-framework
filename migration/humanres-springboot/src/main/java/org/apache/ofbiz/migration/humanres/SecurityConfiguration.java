package org.apache.ofbiz.migration.humanres;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.apache.ofbiz.migration.humanres.generated.ServiceCatalog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class SecurityConfiguration {
    private static final String HUMANRES_ADMIN = "HUMANRES_ADMIN";
    private final ObjectMapper objectMapper;

    public SecurityConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService humanResUserDetailsService(
            HumanResSecurityProperties properties, PasswordEncoder passwordEncoder) {
        List<UserDetails> users = new ArrayList<>();
        for (HumanResSecurityProperties.User configured : properties.getUsers()) {
            String password = configured.getPasswordHash();
            if (password == null || password.isBlank()) {
                if (configured.getPassword() == null || configured.getPassword().isBlank()) {
                    throw new IllegalStateException("User " + configured.getUsername() + " has no password or password hash");
                }
                password = passwordEncoder.encode(configured.getPassword());
            }
            users.add(User.withUsername(configured.getUsername())
                    .password(password)
                    .authorities(configured.getAuthorities().toArray(String[]::new))
                    .build());
        }
        return new InMemoryUserDetailsManager(users);
    }

    @Bean
    AuthenticationProvider humanResAuthenticationProvider(
            UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider)
            throws Exception {
        http.authenticationProvider(authenticationProvider)
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/humanres/**"))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/humanres/**").access(this::authorizeHumanResRequest)
                        .anyRequest().permitAll())
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler()));
        return http.build();
    }

    private AuthorizationDecision authorizeHumanResRequest(
            Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
        Authentication authentication = authenticationSupplier.get();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }
        Set<String> authorities = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        if (!hasAny(authorities, "OFBTOOLS_VIEW", "OFBTOOLS_ADMIN")
                || !hasAny(authorities, "HUMANRES_VIEW", HUMANRES_ADMIN)) {
            return new AuthorizationDecision(false);
        }

        String path = context.getRequest().getRequestURI();
        String method = context.getRequest().getMethod();
        if (path.startsWith("/api/humanres/entities/") || path.equals("/api/humanres/hr-tree")) {
            return new AuthorizationDecision(hasAny(authorities, "HUMANRES_VIEW", HUMANRES_ADMIN));
        }
        if (path.startsWith("/api/humanres/services/")) {
            String[] parts = path.split("/");
            if (parts.length > 4) {
                String serviceName = parts[4];
                String permission = ServiceCatalog.find(serviceName)
                        .map(ServiceCatalog.ServiceDefinition::permissionMainAction)
                        .orElseGet(() -> simpleServiceAction(serviceName));
                return new AuthorizationDecision(permission == null
                        || hasAny(authorities, "HUMANRES_" + permission, HUMANRES_ADMIN));
            }
        }
        if (path.equals("/api/humanres/internal-orgs")) {
            return new AuthorizationDecision(hasAny(
                    authorities, "HUMANRES_" + ("DELETE".equals(method) ? "DELETE" : "CREATE"), HUMANRES_ADMIN));
        }
        if (path.equals("/api/humanres/public-holidays")) {
            return new AuthorizationDecision(hasAny(authorities, "HUMANRES_CREATE", HUMANRES_ADMIN));
        }
        return new AuthorizationDecision(true);
    }

    private static String simpleServiceAction(String serviceName) {
        return switch (serviceName) {
            case "createInternalOrg", "createPublicHoliday" -> "CREATE";
            case "removeInternalOrg", "deleteEmplPositionTypeRate" -> "DELETE";
            case "createEmployee", "createEmployment", "applyTraining", "assignTraining" -> "CREATE";
            case "updatePayHistory", "createEmplPositionReportingStruct", "updateEmplPositionTypeRate" -> "UPDATE";
            case "getCurrentPartyEmploymentData", "getHRChild" -> "VIEW";
            default -> null;
        };
    }

    private static boolean hasAny(Collection<String> authorities, String... expected) {
        for (String authority : expected) {
            if (authorities.contains(authority)) {
                return true;
            }
        }
        return false;
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, exception) -> {
            response.setStatus(401);
            response.setHeader("WWW-Authenticate", "Basic realm=\"HumanRes\"");
            writeError(response, "Authentication required");
        };
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return (request, response, exception) -> {
            response.setStatus(403);
            writeError(response, "Forbidden");
        };
    }

    private void writeError(jakarta.servlet.http.HttpServletResponse response, String message) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(),
                Map.of("responseMessage", "error", "errorMessage", message));
    }
}
