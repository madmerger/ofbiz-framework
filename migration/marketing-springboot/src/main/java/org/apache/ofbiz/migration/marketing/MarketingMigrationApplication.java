package org.apache.ofbiz.migration.marketing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
    "org.apache.ofbiz.migration.common",
    "org.apache.ofbiz.migration.marketing"
})
@EntityScan("org.apache.ofbiz.migration.datamodel.entity")
@EnableJpaRepositories("org.apache.ofbiz.migration.datamodel.repository")
public class MarketingMigrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketingMigrationApplication.class, args);
    }
}
