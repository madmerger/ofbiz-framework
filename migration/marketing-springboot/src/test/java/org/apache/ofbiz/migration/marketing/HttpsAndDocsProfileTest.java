package org.apache.ofbiz.migration.marketing;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/** Uses the default profile as-is: HTTPS required and springdoc disabled. */
@SpringBootTest(classes = {MarketingMigrationApplication.class, TestUsersConfiguration.class})
@AutoConfigureMockMvc
class HttpsAndDocsProfileTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void defaultProfileRedirectsPlainHttpAndIgnoresUntrustedForwardedProto() throws Exception {
        mockMvc.perform(get("/actuator/health")).andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/actuator/health")
                        .with(request -> {
                            request.setRemoteAddr("10.0.0.5");
                            return request;
                        })
                        .header("X-Forwarded-Proto", "https"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void docsAndSwaggerUiAreNotServedWithoutLocalProfile() throws Exception {
        mockMvc.perform(get("/v3/api-docs").secure(true)).andExpect(status().isNotFound());
        mockMvc.perform(get("/swagger-ui/index.html").secure(true)).andExpect(status().isNotFound());
    }
}
