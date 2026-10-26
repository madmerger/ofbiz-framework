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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import org.apache.ofbiz.migration.datamodel.entity.NoteData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import jakarta.persistence.EntityManager;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MultiRollbackTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @AfterEach
    void cleanUpSeededNote() {
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            NoteData note = entityManager.find(NoteData.class, "MULTI-DELETE");
            if (note != null) {
                entityManager.remove(note);
            }
        });
    }

    @BeforeEach
    void seedUsers() {
        new TransactionTemplate(transactionManager).executeWithoutResult(
                status -> CommonextTestSupport.seedUserLogins(entityManager));
    }

    @Test
    void ac01createMultiReturns400AndRollsBackSuccessfulEarlierItem() throws Exception {
        long before = countNotes();
        mockMvc.perform(post("/api/commonext/services/createSystemInfoNote/multi")
                        .with(httpBasic("admin", "commonext-test-password"))
                        .contentType("application/json")
                        .content("[{\"noteInfo\":\"valid\"},{\"noteInfo\":\"<script>invalid</script>\"}]"))
                .andExpect(status().isBadRequest());

        assertEquals(before, countNotes());
    }

    @Test
    void ac02deleteMultiReturns404AndRollsBackEarlierDelete() throws Exception {
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            NoteData note = new NoteData();
            note.setNoteId("MULTI-DELETE");
            note.setNoteParty("admin");
            note.setNoteName("SYSTEMNOTE");
            note.setNoteInfo("keep after rollback");
            note.setNoteDateTime(java.sql.Timestamp.from(Instant.now()));
            entityManager.persist(note);
        });

        mockMvc.perform(post("/api/commonext/services/deleteSystemInfoNote/multi")
                        .with(httpBasic("admin", "commonext-test-password"))
                        .contentType("application/json")
                        .content("[{\"noteId\":\"MULTI-DELETE\"},{\"noteId\":\"DOES-NOT-EXIST\"}]"))
                .andExpect(status().isNotFound());

        NoteData remaining = new TransactionTemplate(transactionManager)
                .execute(status -> entityManager.find(NoteData.class, "MULTI-DELETE"));
        assertNotNull(remaining);
    }

    private long countNotes() {
        return new TransactionTemplate(transactionManager)
                .execute(status -> jdbcTemplate.queryForObject("select count(*) from NOTE_DATA", Long.class));
    }
}
