package org.apache.ofbiz.migration.humanres;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class HumanResMigrationApplication {
    public static void main(String[] args) {
        SpringApplication.run(HumanResMigrationApplication.class, args);
    }
}
