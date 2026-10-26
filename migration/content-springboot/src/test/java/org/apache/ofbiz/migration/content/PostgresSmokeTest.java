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
package org.apache.ofbiz.migration.content;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Map;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** PostgreSQL（Testcontainers）上で entity-auto と主要フローが動くことの smoke テスト。 */
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(classes = ContentMigrationApplication.class)
@ActiveProfiles({"postgres", "test"})
@WithMockUser(username = "admin", authorities = {"OFBTOOLS_VIEW", "CONTENTMGR_VIEW", "CONTENTMGR_ADMIN"})
class PostgresSmokeTest {
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private MigrationServiceDispatcher dispatcher;

    @Autowired
    private ContentEntityStore store;

    @Autowired
    private TransactionTemplate transactions;

    @Test
    void ac48PostgresCrudAndMainFlowSmoke() {
        dispatcher.execute("content", "createContentType", Map.of("contentTypeId", "PG_TYPE", "description", "PostgreSQL"));
        dispatcher.execute("content", "updateContentType", Map.of("contentTypeId", "PG_TYPE", "description", "Updated"));
        assertEquals("Updated", transactions.execute(status ->
                store.get(store.findOne("ContentType", Map.of("contentTypeId", "PG_TYPE")), "description")));
        dispatcher.execute("content", "removeContentType", Map.of("contentTypeId", "PG_TYPE"));
        assertNull(transactions.execute(status -> store.findOne("ContentType", Map.of("contentTypeId", "PG_TYPE"))));

        Map<String, Object> text = dispatcher.execute("content", "createTextContent", Map.of("contentName", "PG", "textData", "本文"));
        Map<String, Object> electronicText = dispatcher.execute("content", "getElectronicText",
                Map.of("contentId", text.get("contentId")));
        assertEquals("本文", electronicText.get("textData"));
    }
}
