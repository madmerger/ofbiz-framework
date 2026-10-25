package org.apache.ofbiz.migration.humanres;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SequenceService {
    private static final long FIRST_ID = 10_000L;
    private final SequenceValueItemRepository repository;
    private final EntityManager entityManager;
    private final EntityRegistry entityRegistry;

    public SequenceService(
            SequenceValueItemRepository repository, EntityManager entityManager, EntityRegistry entityRegistry) {
        this.repository = repository;
        this.entityManager = entityManager;
        this.entityRegistry = entityRegistry;
    }

    @Transactional
    public String nextId(String entityName) {
        SequenceValueItem item = repository.findForUpdate(entityName).orElse(null);
        if (item == null) {
            repository.save(new SequenceValueItem(entityName, FIRST_ID));
            return Long.toString(FIRST_ID);
        }
        long next = item.getSeqId() + 1;
        item.setSeqId(next);
        return Long.toString(next);
    }

    @Transactional(readOnly = true)
    public String nextSubSequence(String entityName, String sequenceField, Map<String, ?> parentKeys) {
        Class<?> entityClass = entityRegistry.entityClass(entityName);
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = builder.createQuery(Object.class);
        Root<?> root = query.from(entityClass);
        var predicates = entityRegistry.primaryKeyNames(entityClass).stream()
                .filter(name -> !name.equals(sequenceField))
                .filter(parentKeys::containsKey)
                .map(name -> builder.equal(root.get(name),
                        entityRegistry.convertForClass(parentKeys.get(name), entityRegistry.propertyType(entityClass, name))))
                .toList();
        query.select(root.get(sequenceField))
                .where(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new))
                .orderBy(builder.desc(root.get(sequenceField)));
        List<Object> values = entityManager.createQuery(query).setMaxResults(1).getResultList();
        long next = values.isEmpty() || values.get(0) == null
                ? 1L
                : Long.parseLong(values.get(0).toString()) + 1;
        return String.format(Locale.ROOT, "%05d", next);
    }
}
