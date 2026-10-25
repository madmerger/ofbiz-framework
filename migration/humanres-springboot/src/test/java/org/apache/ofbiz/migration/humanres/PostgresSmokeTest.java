package org.apache.ofbiz.migration.humanres;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(
        classes = {HumanResMigrationApplication.class, TestUsersConfiguration.class},
        properties = "spring.profiles.active=postgres")
class PostgresSmokeTest {
    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private EntityAutoService entityAutoService;

    @Autowired
    private EntityRegistry entityRegistry;

    @Test
    void ac04_postgresSchemaAndCrudSmoke() {
        Map<String, Object> created = entityAutoService.createEntity("EmplPositionType", Map.of(
                "emplPositionTypeId", "PG_AC04", "description", "PostgreSQL smoke"));
        assertEquals("PG_AC04", created.get("emplPositionTypeId"));
        Object loaded = entityAutoService.findEntity(
                "EmplPositionType", Map.of("emplPositionTypeId", "PG_AC04"));
        assertEquals("PostgreSQL smoke", entityRegistry.get(loaded, "description"));

        entityAutoService.updateEntity("EmplPositionType", Map.of(
                "emplPositionTypeId", "PG_AC04",
                "description", "Updated PostgreSQL smoke"));
        Object updated = entityAutoService.findEntity(
                "EmplPositionType", Map.of("emplPositionTypeId", "PG_AC04"));
        assertEquals("Updated PostgreSQL smoke", entityRegistry.get(updated, "description"));

        entityAutoService.deleteEntity(
                "EmplPositionType", Map.of("emplPositionTypeId", "PG_AC04"));
        assertThrows(HumanResException.class, () -> entityAutoService.findEntity(
                "EmplPositionType", Map.of("emplPositionTypeId", "PG_AC04")));
    }
}
