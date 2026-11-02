package org.apache.ofbiz.migration.equivalence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "org.apache.ofbiz.migration",
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX,
                pattern = ".*\\.(?:\\w*MigrationApplication|ProductSecurityConfiguration)"))
@ConfigurationPropertiesScan("org.apache.ofbiz.migration")
@EntityScan("org.apache.ofbiz.migration.datamodel.entity")
@EnableJpaRepositories("org.apache.ofbiz.migration.datamodel.repository")
public class EquivalenceApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(EquivalenceApplication.class);
        application.setAdditionalProfiles("equivalence");
        application.run(args);
    }
}
