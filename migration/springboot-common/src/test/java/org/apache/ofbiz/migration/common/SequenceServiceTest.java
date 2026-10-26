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
package org.apache.ofbiz.migration.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.persistence.EntityManager;
import java.util.Locale;
import java.util.Map;
import org.apache.ofbiz.migration.datamodel.entity.PerfReviewItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
        classes = MigrationCommonApiTest.TestApplication.class,
        properties = {
            "spring.datasource.url=jdbc:h2:mem:common;DB_CLOSE_DELAY=-1",
            "spring.datasource.username=sa",
            "spring.datasource.password=",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.open-in-view=false",
            "migration.security.component=test",
            "migration.security.require-https=false",
            "migration.security.service-permission-prefix=TEST",
            "migration.security.users[0].username=writer",
            "migration.security.users[0].password=writer-password",
            "migration.security.users[0].authorities[0]=TEST_VIEW",
            "migration.security.users[0].authorities[1]=TEST_CREATE",
            "migration.security.users[0].authorities[2]=TEST_SENSITIVE_VIEW",
            "migration.security.users[0].authorities[3]=OFBTOOLS_VIEW",
            "migration.security.users[1].username=readonly",
            "migration.security.users[1].password=readonly-password",
            "migration.security.users[1].authorities[0]=TEST_VIEW",
            "migration.security.users[1].authorities[1]=OFBTOOLS_VIEW",
            "migration.security.users[2].username=ofbtools-only",
            "migration.security.users[2].password=ofbtools-password",
            "migration.security.users[2].authorities[0]=OFBTOOLS_VIEW",
            "migration.security.users[3].username=both",
            "migration.security.users[3].password=both-password",
            "migration.security.users[3].authorities[0]=OFBTOOLS_VIEW",
            "migration.security.users[3].authorities[1]=TEST_VIEW",
            "migration.security.users[4].username=admin",
            "migration.security.users[4].password=admin-password",
            "migration.security.users[4].authorities[0]=OFBTOOLS_VIEW",
            "migration.security.users[4].authorities[1]=TEST_ADMIN",
            "migration.security.sensitive-entities[0]=Enumeration",
            "migration.security.sensitive-entities[1]=PayHistory",
            "migration.security.extra-entities[0]=GeoType"
        })
@AutoConfigureMockMvc
class SequenceServiceTest {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SequenceService sequenceService;

    @Test
    @Transactional
    void subsequenceUsesOnlyParentKeysAndIncludesRowsBeyondOneHundred() {
        for (int sequence = 1; sequence <= 105; sequence++) {
            PerfReviewItem item = new PerfReviewItem();
            item.setEmployeePartyId("EMPLOYEE");
            item.setEmployeeRoleTypeId("EMPLOYEE");
            item.setPerfReviewId("REVIEW");
            item.setPerfReviewItemSeqId(String.format(Locale.ROOT, "%05d", sequence));
            item.setComments("row-" + sequence);
            entityManager.persist(item);
        }
        entityManager.flush();

        assertEquals(
                "00106",
                sequenceService.nextSubSequence(
                        "PerfReviewItem",
                        Map.of(
                                "employeePartyId", "EMPLOYEE",
                                "employeeRoleTypeId", "EMPLOYEE",
                                "perfReviewId", "REVIEW",
                                "comments", "not-a-pk-filter"),
                        "perfReviewItemSeqId"));
    }
}
