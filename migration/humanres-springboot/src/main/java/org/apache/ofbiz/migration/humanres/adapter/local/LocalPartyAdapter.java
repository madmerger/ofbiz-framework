package org.apache.ofbiz.migration.humanres.adapter.local;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import jakarta.persistence.EntityManager;
import org.apache.ofbiz.migration.humanres.EntityRegistry;
import org.apache.ofbiz.migration.humanres.PartyPort;
import org.apache.ofbiz.migration.humanres.SequenceService;
import org.apache.ofbiz.migration.humanres.adapter.local.model.PartyGroupRecord;
import org.apache.ofbiz.migration.humanres.adapter.local.model.PartyRecord;
import org.apache.ofbiz.migration.humanres.adapter.local.model.PartyRelationshipRecord;
import org.apache.ofbiz.migration.humanres.adapter.local.model.PartyRoleRecord;
import org.apache.ofbiz.migration.humanres.adapter.local.model.PersonRecord;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class LocalPartyAdapter implements PartyPort {
    private final EntityManager entityManager;
    private final SequenceService sequenceService;

    public LocalPartyAdapter(EntityManager entityManager, SequenceService sequenceService) {
        this.entityManager = entityManager;
        this.sequenceService = sequenceService;
    }

    @Override
    public void ensureRole(String partyId, String roleTypeId) {
        PartyRoleRecord.Key key = new PartyRoleRecord.Key();
        key.partyId = partyId;
        key.roleTypeId = roleTypeId;
        if (entityManager.find(PartyRoleRecord.class, key) == null) {
            entityManager.persist(new PartyRoleRecord(partyId, roleTypeId));
        }
    }

    @Override
    public String createEmployee(Map<String, ?> input) {
        String partyId = input.get("partyId") == null
                ? sequenceService.nextId("Party")
                : input.get("partyId").toString();
        if (entityManager.find(PartyRecord.class, partyId) != null) {
            throw org.apache.ofbiz.migration.humanres.HumanResException.conflict("ServiceValueFound");
        }
        entityManager.persist(new PartyRecord(partyId, "PERSON"));
        entityManager.persist(new PersonRecord(
                partyId,
                stringValue(input, "firstName"),
                stringValue(input, "middleName"),
                stringValue(input, "lastName"),
                stringValue(input, "gender")));
        ensureRole(partyId, "EMPLOYEE");
        Object partyIdFrom = input.get("partyIdFrom");
        if (partyIdFrom != null && !partyIdFrom.toString().isBlank()) {
            createRelationship(
                    partyIdFrom.toString(), partyId, "EMPLOYMENT", "INTERNAL_ORGANIZATIO", "EMPLOYEE",
                    "EMPLOYMENT", input.get("fromDate") instanceof Timestamp timestamp ? timestamp : null);
        }
        return partyId;
    }

    @Override
    public void createRelationship(
            String partyIdFrom, String partyIdTo, String relationshipTypeId,
            String roleTypeIdFrom, String roleTypeIdTo, String relationshipName, Timestamp fromDate) {
        entityManager.persist(new PartyRelationshipRecord(
                partyIdFrom, partyIdTo, relationshipTypeId, roleTypeIdFrom, roleTypeIdTo,
                relationshipName, fromDate == null ? EntityRegistry.now() : fromDate));
    }

    @Override
    public void removeRelationship(String partyIdFrom, String partyIdTo) {
        List<PartyRelationshipRecord> matches = entityManager.createQuery(
                        "select relation from LocalPartyRelationship relation "
                                + "where relation.partyIdFrom = :from and relation.partyIdTo = :to "
                                + "and relation.partyRelationshipTypeId = 'GROUP_ROLLUP' "
                                + "and relation.roleTypeIdFrom = 'INTERNAL_ORGANIZATIO' "
                                + "and relation.roleTypeIdTo = 'INTERNAL_ORGANIZATIO' "
                                + "order by relation.fromDate desc",
                        PartyRelationshipRecord.class)
                .setParameter("from", partyIdFrom)
                .setParameter("to", partyIdTo)
                .setMaxResults(1)
                .getResultList();
        if (matches.isEmpty()) {
            throw org.apache.ofbiz.migration.humanres.HumanResException.notFound("PartyRelationship not found");
        }
        entityManager.remove(matches.get(0));
    }

    @Override
    public boolean isPartyGroup(String partyId) {
        return entityManager.find(PartyGroupRecord.class, partyId) != null;
    }

    @Override
    public String partyName(String partyId) {
        PartyGroupRecord group = entityManager.find(PartyGroupRecord.class, partyId);
        if (group != null) {
            return group.getGroupName();
        }
        PersonRecord person = entityManager.find(PersonRecord.class, partyId);
        if (person != null) {
            return (person.getFirstName() == null ? "" : person.getFirstName() + " ")
                    + (person.getLastName() == null ? "" : person.getLastName());
        }
        return partyId;
    }

    @Override
    public List<Relationship> childOrganizations(String parentPartyId) {
        Timestamp now = EntityRegistry.now();
        return entityManager.createQuery(
                        "select relation from LocalPartyRelationship relation "
                                + "where relation.partyIdFrom = :parent "
                                + "and relation.partyRelationshipTypeId = 'GROUP_ROLLUP' "
                                + "and (relation.thruDate is null or relation.thruDate > :now)",
                        PartyRelationshipRecord.class)
                .setParameter("parent", parentPartyId)
                .setParameter("now", now)
                .getResultList()
                .stream()
                .map(relation -> new Relationship(
                        relation.getPartyIdFrom(), relation.getPartyIdTo(), relation.getFromDate()))
                .toList();
    }

    private static String stringValue(Map<String, ?> values, String key) {
        Object value = values.get(key);
        return value == null ? null : value.toString();
    }
}
