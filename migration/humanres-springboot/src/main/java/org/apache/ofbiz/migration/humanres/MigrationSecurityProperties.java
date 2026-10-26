package org.apache.ofbiz.migration.humanres;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "migration.security")
public class MigrationSecurityProperties {
    private List<String> sensitiveEntities = new ArrayList<>();
    private boolean requireHttps;

    public List<String> getSensitiveEntities() {
        return sensitiveEntities;
    }

    public void setSensitiveEntities(List<String> sensitiveEntities) {
        this.sensitiveEntities = sensitiveEntities;
    }

    public boolean isRequireHttps() {
        return requireHttps;
    }

    public void setRequireHttps(boolean requireHttps) {
        this.requireHttps = requireHttps;
    }

    public boolean isSensitiveEntity(String entityName) {
        return sensitiveEntities.stream().anyMatch(name -> name.equalsIgnoreCase(entityName));
    }
}
