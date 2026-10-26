/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.apache.ofbiz.migration.manufacturing;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import jakarta.persistence.EntityManager;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ActiveProfiles("postgres")
@Testcontainers(disabledWithoutDocker = true)
@Transactional
@WithMockUser(authorities = {"OFBTOOLS_VIEW", "MANUFACTURING_ADMIN"})
class PostgresSmokeTest {
    @Container
    static final PostgreSQLContainer<?> DATABASE = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private EntityRegistry entities;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private WorkEffortPort workEfforts;
    @Autowired
    private MigrationServiceDispatcher dispatcher;
    @Autowired
    private ProductPort products;

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry properties) {
        properties.add("spring.datasource.url", DATABASE::getJdbcUrl);
        properties.add("spring.datasource.username", DATABASE::getUsername);
        properties.add("spring.datasource.password", DATABASE::getPassword);
        properties.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Test
    void ac04_postgresPersistsCalendarAndProductionRun() {
        dispatcher.execute("manufacturing", "createCalendar", Map.of("calendarId", "PG_CAL"));
        assertFalse(entities.findByPrimaryKey(entities.entityClass("TechDataCalendar"),
                Map.of("calendarId", "PG_CAL")) == null);
        create("Product", Map.of("productId", "PG_FG", "productTypeId", "FINISHED_GOOD",
                "internalName", "Postgres finished good", "isVirtual", "N"));
        workEfforts.createWorkEffort(Map.of("workEffortId", "PG_ROUTING", "workEffortTypeId", "ROUTING"));
        workEfforts.createWorkEffort(Map.of("workEffortId", "PG_TASK", "workEffortTypeId", "ROU_TASK",
                "estimatedSetupMillis", BigDecimal.ONE, "estimatedMilliSeconds", BigDecimal.ONE));
        Timestamp date = Timestamp.from(Instant.now());
        workEfforts.createWorkEffortAssoc(Map.of("workEffortIdFrom", "PG_ROUTING", "workEffortIdTo", "PG_TASK",
                "workEffortAssocTypeId", "ROUTING_COMPONENT", "sequenceNum", BigDecimal.TEN, "fromDate", date));
        workEfforts.createWorkEffortGoodStandard(Map.of("workEffortId", "PG_ROUTING", "productId", "PG_FG",
                "workEffortGoodStdTypeId", "ROU_PROD_TEMPLATE", "fromDate", date));
        Map<String, Object> result = dispatcher.execute("manufacturing", "createProductionRun",
                Map.of("productId", "PG_FG", "quantityToProduce", BigDecimal.ONE));
        assertFalse(String.valueOf(result.get("productionRunId")).isBlank());
    }

    private void create(String name, Map<String, ?> values) {
        Object entity = entities.newEntity(entities.entityClass(name));
        values.forEach((field, value) -> entities.set(entity, field, value));
        entityManager.persist(entity);
    }
}
