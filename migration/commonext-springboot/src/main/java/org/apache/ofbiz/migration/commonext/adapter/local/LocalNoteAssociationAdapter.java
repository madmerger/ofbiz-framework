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
package org.apache.ofbiz.migration.commonext.adapter.local;

import java.util.List;
import org.apache.ofbiz.migration.commonext.port.NoteAssociationPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;

@Component
public class LocalNoteAssociationAdapter implements NoteAssociationPort {
    private static final List<String> ASSOCIATION_ENTITIES = List.of(
            "CustRequestItemNote",
            "CustRequestNote",
            "MarketingCampaignNote",
            "OrderHeaderNote",
            "PartyNote",
            "QuoteNote",
            "WorkEffortNote");
    private final EntityManager entityManager;

    public LocalNoteAssociationAdapter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void removeNoteAssociations(String noteId) {
        for (String entityName : ASSOCIATION_ENTITIES) {
            entityManager.createQuery("delete from " + entityName + " association where association.noteId = :noteId")
                    .setParameter("noteId", noteId)
                    .executeUpdate();
        }
    }
}
