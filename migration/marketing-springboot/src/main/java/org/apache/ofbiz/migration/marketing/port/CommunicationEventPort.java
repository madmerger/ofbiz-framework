package org.apache.ofbiz.migration.marketing.port;

import java.util.Map;
import java.util.Optional;

/**
 * Party component CommunicationEvent services (findByPrimaryKey, createCommunicationEvent).
 */
public interface CommunicationEventPort {

    Optional<CommunicationEventView> find(String communicationEventId);

    String createCommunicationEvent(Map<String, Object> fields);

    record CommunicationEventView(
            String communicationEventId,
            String parentCommEventId,
            String contactListId,
            String partyIdTo,
            String contactMechIdTo) { }
}
