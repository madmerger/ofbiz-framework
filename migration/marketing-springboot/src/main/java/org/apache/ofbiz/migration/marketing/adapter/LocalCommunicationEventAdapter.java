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
package org.apache.ofbiz.migration.marketing.adapter;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.ofbiz.migration.common.SequenceService;
import org.apache.ofbiz.migration.datamodel.entity.CommunicationEvent;
import org.apache.ofbiz.migration.marketing.MarketingData;
import org.apache.ofbiz.migration.marketing.port.CommunicationEventPort;
import org.springframework.stereotype.Component;

/**
 * Local adapter for the party component CommunicationEvent services.
 */
@Component
public class LocalCommunicationEventAdapter implements CommunicationEventPort {

    private final MarketingData data;
    private final SequenceService sequences;

    public LocalCommunicationEventAdapter(MarketingData data, SequenceService sequences) {
        this.data = data;
        this.sequences = sequences;
    }

    @Override
    public Optional<CommunicationEventView> find(String communicationEventId) {
        return data.one(CommunicationEvent.class, Map.of("communicationEventId", communicationEventId))
                .map(event -> new CommunicationEventView(
                        event.getCommunicationEventId(),
                        event.getParentCommEventId(),
                        event.getContactListId(),
                        event.getPartyIdTo(),
                        event.getContactMechIdTo()));
    }

    @Override
    public String createCommunicationEvent(Map<String, Object> fields) {
        CommunicationEvent event = new CommunicationEvent();
        data.copyFields(event, fields, Set.of("communicationEventId"));
        event.setCommunicationEventId(sequences.nextId("CommunicationEvent"));
        if (event.getEntryDate() == null) {
            event.setEntryDate(MarketingData.now());
        }
        data.persist(event);
        return event.getCommunicationEventId();
    }
}
