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
package org.apache.ofbiz.migration.accounting.adapter.local;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ofbiz.migration.accounting.PartyAccountingPort;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class LocalPartyAccountingAdapter implements PartyAccountingPort {
    private final EntityRegistry entities;

    public LocalPartyAccountingAdapter(EntityRegistry entities) {
        this.entities = entities;
    }

    @Override
    public List<String> associatedPartyIdsByRelationshipType(String partyId, String relationshipTypeId) {
        Set<String> associated = new LinkedHashSet<>();
        ArrayDeque<String> pending = new ArrayDeque<>();
        pending.add(partyId);
        while (!pending.isEmpty()) {
            String current = pending.removeFirst();
            for (Object relationship : entities.findByEquality(entities.entityClass("PartyRelationship"),
                    Map.of("partyIdFrom", current, "partyRelationshipTypeId", relationshipTypeId))) {
                String next = entities.get(relationship, "partyIdTo").toString();
                if (associated.add(next)) {
                    pending.addLast(next);
                }
            }
        }
        return new ArrayList<>(associated);
    }

    @Override
    public String groupName(String partyId) {
        Object group = entities.findByPrimaryKey(
                entities.entityClass("PartyGroup"), Map.of("partyId", partyId));
        Object name = group == null ? null : entities.get(group, "groupName");
        return name == null ? partyId : name.toString();
    }
}
