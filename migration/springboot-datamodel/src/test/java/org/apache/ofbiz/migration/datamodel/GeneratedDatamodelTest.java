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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.apache.ofbiz.migration.datamodel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootTest(
        classes = GeneratedDatamodelTest.TestApplication.class,
        properties = {
            "spring.datasource.url=jdbc:h2:mem:datamodel;DB_CLOSE_DELAY=-1",
            "spring.datasource.username=sa",
            "spring.datasource.password=",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.open-in-view=false"
        })
class GeneratedDatamodelTest {
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    void allGeneratedEntitiesBootAndMatchGeneratorReport() throws IOException {
        JsonNode report = new ObjectMapper().readTree(new File(System.getProperty("datamodel.report")));
        int entityCount = entityManagerFactory.getMetamodel().getEntities().size();
        int viewCount = report.path("view_entity_count").asInt();
        assertEquals(report.path("entity_count").asInt(), entityCount);
        System.out.printf("Generated datamodel: %d entities, %d views%n", entityCount, viewCount);
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EntityScan("org.apache.ofbiz.migration.datamodel.entity")
    @EnableJpaRepositories("org.apache.ofbiz.migration.datamodel.repository")
    static class TestApplication {
    }
}
