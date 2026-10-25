package org.apache.ofbiz.migration.humanres;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HumanResTreeService {
    private final PartyPort partyPort;
    private final EntityAutoService entityAutoService;
    private final EntityRegistry entityRegistry;

    public HumanResTreeService(
            PartyPort partyPort, EntityAutoService entityAutoService, EntityRegistry entityRegistry) {
        this.partyPort = partyPort;
        this.entityAutoService = entityAutoService;
        this.entityRegistry = entityRegistry;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getHrTree(String partyId) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Object> requestedPosition = entityAutoService.findEntities(
                "EmplPosition", Map.of("emplPositionId", partyId));
        if (!requestedPosition.isEmpty()) {
            return positionMembers(partyId);
        }
        List<Object> positions = entityAutoService.findEntities("EmplPosition", Map.of("partyId", partyId));
        if (!positions.isEmpty()) {
            for (Object position : currentPositions(positions)) {
                result.addAll(positionMembers(String.valueOf(entityRegistry.get(position, "emplPositionId"))));
            }
        }
        if (!partyPort.isPartyGroup(partyId)) {
            return result;
        }
        for (PartyPort.Relationship relationship : partyPort.childOrganizations(partyId)) {
            String childId = relationship.partyIdTo();
            List<Object> childPositions = entityAutoService.findEntities("EmplPosition", Map.of("partyId", childId));
            List<PartyPort.Relationship> grandchildren = partyPort.childOrganizations(childId);
            Map<String, Object> node = node(
                    childId, partyPort.partyName(childId), "Y", childId, "#", "");
            if (!grandchildren.isEmpty() || !currentPositions(childPositions).isEmpty()) {
                node.put("state", "closed");
            }
            result.add(node);
        }
        for (Object position : currentPositions(positions)) {
            result.add(positionNode(position));
        }
        return result;
    }

    private List<Object> currentPositions(List<Object> positions) {
        Timestamp now = EntityRegistry.now();
        return positions.stream()
                .filter(position -> !"EMPL_POS_INACTIVE".equals(entityRegistry.get(position, "statusId")))
                .filter(position -> isActive(
                        entityRegistry.get(position, "actualFromDate"),
                        entityRegistry.get(position, "actualThruDate"), now))
                .toList();
    }

    private List<Map<String, Object>> positionMembers(String positionId) {
        List<Object> assignments = entityAutoService.findEntities(
                "EmplPositionFulfillment", Map.of("emplPositionId", positionId));
        Timestamp now = EntityRegistry.now();
        return assignments.stream()
                .filter(assignment -> isActive(
                        entityRegistry.get(assignment, "fromDate"),
                        entityRegistry.get(assignment, "thruDate"), now))
                .map(assignment -> {
                    String partyId = String.valueOf(entityRegistry.get(assignment, "partyId"));
                    return node(partyId, partyPort.partyName(partyId), "P", partyId, "#", "");
                })
                .toList();
    }

    private Map<String, Object> positionNode(Object position) {
        String positionId = String.valueOf(entityRegistry.get(position, "emplPositionId"));
        Object typeId = entityRegistry.get(position, "emplPositionTypeId");
        String title = typeId == null ? positionId : String.valueOf(typeId) + " [" + positionId + "]";
        Map<String, Object> node = node(positionId, title, "N", positionId,
                "emplPositionView?emplPositionId=" + positionId, "callEmplDocument('" + positionId + "')");
        if (!positionMembers(positionId).isEmpty()) {
            node.put("state", "closed");
        }
        return node;
    }

    private static Map<String, Object> node(
            String id, String title, String relation, String hrefId, String href, String onClick) {
        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("rel", relation);
        attributes.put("id", id);
        Map<String, Object> dataAttributes = new LinkedHashMap<>();
        dataAttributes.put("href", href);
        dataAttributes.put("onClick", onClick);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("title", title);
        data.put("attr", dataAttributes);
        Map<String, Object> node = new LinkedHashMap<>();
        node.put("attr", attributes);
        node.put("data", data);
        return node;
    }

    private static boolean isActive(Object from, Object thru, Timestamp now) {
        boolean started = from == null || !((Timestamp) from).after(now);
        boolean notExpired = thru == null || ((Timestamp) thru).after(now);
        return started && notExpired;
    }
}
