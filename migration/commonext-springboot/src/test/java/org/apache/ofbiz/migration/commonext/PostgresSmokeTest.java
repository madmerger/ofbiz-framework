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
package org.apache.ofbiz.migration.commonext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Map;
import org.apache.ofbiz.migration.commonext.service.SystemInfoNoteService;
import org.apache.ofbiz.migration.datamodel.entity.NoteData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.test.context.support.WithMockUser;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import jakarta.persistence.EntityManager;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = {
        "migration.security.require-https=false",
        "migration.security.users[0].username=postgres-admin",
        "migration.security.users[0].password=test-password",
        "migration.security.users[0].password-hash=",
        "migration.security.users[0].authorities[0]=OFBTOOLS_VIEW",
        "migration.security.users[0].authorities[1]=SETUP_ADMIN",
        "migration.security.users[0].authorities[2]=COMMONEXT_PUBLMSG"
})
@ActiveProfiles("postgres")
@Transactional
class PostgresSmokeTest {
    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
    }

    @Autowired
    private SystemInfoNoteService noteService;
    @Autowired
    private EntityManager entityManager;

    @Test
    @WithMockUser(username = "postgres-user", authorities = {"SETUP_ADMIN"})
    void ac01postgresCreatesListsAndDeletesSystemNotes() {
        CommonextTestSupport.seedUserLogin(entityManager, "postgres-user", "POSTGRES-PARTY");
        Map<String, Object> created = noteService.createSystemInfoNote(Map.of("noteInfo", "PostgreSQL smoke"));
        Map<String, Object> list = noteService.getSystemInfoNotes(Map.of());

        assertEquals(1, ((java.util.List<?>) list.get("systemInfoNotes")).size());
        noteService.deleteSystemInfoNote(Map.of("noteId", created.get("noteId")));

        assertNull(entityManager.find(NoteData.class, String.valueOf(created.get("noteId"))));
    }
}
