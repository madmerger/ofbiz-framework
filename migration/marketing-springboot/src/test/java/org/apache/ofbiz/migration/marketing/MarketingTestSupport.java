package org.apache.ofbiz.migration.marketing;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

final class MarketingTestSupport {

    static final String TEST_PROPERTIES = "migration.security.require-https=false";
    private static final ObjectMapper JSON = new ObjectMapper();

    private MarketingTestSupport() {
    }

    static JsonNode inventory() throws IOException {
        return JSON.readTree(Path.of(System.getProperty("ofbiz.root"),
                "docs/migration/marketing/inventory/inventory.json").toFile());
    }

    static void loginAs(String user, String... authorities) {
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(user, "n/a", authorities));
    }

    static void loginAdmin() {
        loginAs("admin", "OFBTOOLS_VIEW", "MARKETING_VIEW", "MARKETING_ADMIN");
    }

    static Map<String, Object> run(MigrationServiceDispatcher dispatcher, String service, Map<String, ?> input) {
        return dispatcher.execute(MarketingCatalogRegistrar.COMPONENT, service, input);
    }
}
