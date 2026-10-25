package org.apache.ofbiz.migration.humanres;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jakarta.persistence.IdClass;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import org.apache.ofbiz.migration.humanres.generated.ServiceCatalog;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class EntityRegistry {
    private static final String GENERATED_PACKAGE = "org.apache.ofbiz.migration.humanres.generated";
    private final EntityManager entityManager;

    public EntityRegistry(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public EntityManager entityManager() {
        return entityManager;
    }

    public Class<?> entityClass(String name) {
        return entityManager.getMetamodel().getEntities().stream()
                .map(EntityType::getJavaType)
                .filter(type -> GENERATED_PACKAGE.equals(type.getPackageName()))
                .filter(type -> type.getSimpleName().equals(name))
                .findFirst()
                .orElseThrow(() -> HumanResException.notFound("Unknown entity: " + name));
    }

    public List<String> primaryKeyNames(Class<?> entityClass) {
        EntityType<?> entityType = entityManager.getMetamodel().entity(entityClass);
        return entityType.getSingularAttributes().stream()
                .filter(SingularAttribute::isId)
                .map(Attribute::getName)
                .sorted()
                .toList();
    }

    public boolean hasProperty(Class<?> entityClass, String propertyName) {
        return entityManager.getMetamodel().entity(entityClass).getAttributes().stream()
                .anyMatch(attribute -> attribute.getName().equals(propertyName));
    }

    public Class<?> propertyType(Class<?> entityClass, String propertyName) {
        try {
            return entityManager.getMetamodel().entity(entityClass)
                    .getAttribute(propertyName).getJavaType();
        } catch (IllegalArgumentException exception) {
            throw new HumanResException(HttpStatus.BAD_REQUEST,
                    "Unknown field " + propertyName + " on " + entityClass.getSimpleName());
        }
    }

    public Object get(Object bean, String propertyName) {
        return new BeanWrapperImpl(bean).getPropertyValue(propertyName);
    }

    public void set(Object bean, String propertyName, Object value) {
        if (hasProperty(bean.getClass(), propertyName)) {
            new BeanWrapperImpl(bean).setPropertyValue(propertyName, value);
        }
    }

    public Object newEntity(Class<?> entityClass) {
        try {
            return entityClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Cannot instantiate " + entityClass.getName(), exception);
        }
    }

    public Object findByPrimaryKey(String entityName, Map<String, ?> values) {
        Class<?> entityClass = entityClass(entityName);
        List<String> primaryKeys = primaryKeyNames(entityClass);
        if (primaryKeys.isEmpty()) {
            throw new IllegalStateException(entityName + " has no primary key");
        }
        BeanWrapper keyValues = new BeanWrapperImpl(newEntity(entityClass));
        for (String key : primaryKeys) {
            if (!values.containsKey(key) || values.get(key) == null || values.get(key).toString().isBlank()) {
                throw HumanResException.badRequest("Missing primary-key parameter: " + key);
            }
            keyValues.setPropertyValue(key, convertForClass(values.get(key), propertyType(entityClass, key)));
        }
        Object identifier;
        EntityType<?> entityType = entityManager.getMetamodel().entity(entityClass);
        if (entityType.hasSingleIdAttribute()) {
            identifier = keyValues.getPropertyValue(primaryKeys.get(0));
        } else {
            IdClass idClassAnnotation = entityClass.getAnnotation(IdClass.class);
            Class<?> idClass = idClassAnnotation == null
                    ? entityType.getIdType().getJavaType()
                    : idClassAnnotation.value();
            try {
                Object id = idClass.getDeclaredConstructor().newInstance();
                BeanWrapper idValues = new BeanWrapperImpl(id);
                for (String key : primaryKeys) {
                    idValues.setPropertyValue(key, keyValues.getPropertyValue(key));
                }
                identifier = id;
            } catch (ReflectiveOperationException exception) {
                throw new IllegalStateException("Cannot instantiate composite ID for " + entityName, exception);
            }
        }
        return entityManager.find(entityClass, identifier);
    }

    public List<Object> findByEquality(String entityName, Map<String, ?> filters) {
        Class<?> entityClass = entityClass(entityName);
        var builder = entityManager.getCriteriaBuilder();
        @SuppressWarnings("rawtypes")
        jakarta.persistence.criteria.CriteriaQuery query = builder.createQuery(entityClass);
        @SuppressWarnings("rawtypes")
        jakarta.persistence.criteria.Root root = query.from(entityClass);
        List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
        filters.forEach((name, value) -> {
            if (value != null && !value.toString().isBlank()) {
                Class<?> targetType = propertyType(entityClass, name);
                predicates.add(builder.equal(root.get(name), convertForClass(value, targetType)));
            }
        });
        query.select(root).where(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
        List<jakarta.persistence.criteria.Order> orders = primaryKeyNames(entityClass).stream()
                .map(name -> builder.asc(root.get(name)))
                .toList();
        if (!orders.isEmpty()) {
            query.orderBy(orders);
        }
        return new ArrayList<>((List<?>) entityManager.createQuery(query).getResultList());
    }

    public List<Object> findPage(String entityName, Map<String, ?> filters, int page, int size) {
        Class<?> entityClass = entityClass(entityName);
        var builder = entityManager.getCriteriaBuilder();
        @SuppressWarnings("rawtypes")
        jakarta.persistence.criteria.CriteriaQuery query = builder.createQuery(entityClass);
        @SuppressWarnings("rawtypes")
        jakarta.persistence.criteria.Root root = query.from(entityClass);
        List<jakarta.persistence.criteria.Predicate> predicates = predicates(entityClass, filters, root, builder);
        query.select(root).where(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
        List<jakarta.persistence.criteria.Order> orders = primaryKeyNames(entityClass).stream()
                .map(name -> builder.asc(root.get(name)))
                .toList();
        if (!orders.isEmpty()) {
            query.orderBy(orders);
        }
        return new ArrayList<>((List<?>) entityManager.createQuery(query)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList());
    }

    public long count(String entityName, Map<String, ?> filters) {
        Class<?> entityClass = entityClass(entityName);
        var builder = entityManager.getCriteriaBuilder();
        var query = builder.createQuery(Long.class);
        var root = query.from(entityClass);
        List<jakarta.persistence.criteria.Predicate> predicates = predicates(entityClass, filters, root, builder);
        query.select(builder.count(root)).where(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
        return entityManager.createQuery(query).getSingleResult();
    }

    public Map<String, Object> toMap(Object entity) {
        Map<String, Object> result = new LinkedHashMap<>();
        EntityType<?> entityType = entityManager.getMetamodel().entity(entity.getClass());
        entityType.getAttributes().stream()
                .sorted(Comparator.comparing((Attribute<?, ?> attribute) -> attribute.getName()))
                .forEach(attribute -> result.put(attribute.getName(), get(entity, attribute.getName())));
        return result;
    }

    public Object convertForType(Object value, String ofbizType) {
        if (value == null) {
            return null;
        }
        return switch (ofbizType == null ? "" : ofbizType) {
            case "date-time", "Timestamp", "java.sql.Timestamp" ->
                    convertForClass(value, Timestamp.class);
            case "date", "LocalDate" -> convertForClass(value, LocalDate.class);
            case "time", "LocalTime" -> convertForClass(value, LocalTime.class);
            case "currency-amount", "currency-precise", "fixed-point", "BigDecimal" ->
                    convertForClass(value, BigDecimal.class);
            case "floating-point", "Double", "double" -> convertForClass(value, Double.class);
            case "integer", "numeric", "Long", "long" -> convertForClass(value, Long.class);
            default -> value.toString();
        };
    }

    public Object convertForClass(Object value, Class<?> targetType) {
        if (value == null) {
            return value;
        }
        if (targetType == Timestamp.class && value instanceof Timestamp timestamp) {
            return Timestamp.from(timestamp.toInstant().truncatedTo(ChronoUnit.MILLIS));
        }
        if (targetType.isInstance(value)) {
            return value;
        }
        String text = value.toString();
        if (targetType == String.class) {
            return text;
        }
        if (targetType == Timestamp.class) {
            if (value instanceof Number number) {
                return new Timestamp(number.longValue());
            }
            try {
                return Timestamp.from(Instant.parse(text));
            } catch (RuntimeException ignored) {
                try {
                    return Timestamp.from(OffsetDateTime.parse(text).toInstant());
                } catch (RuntimeException ignoredOffset) {
                    return Timestamp.valueOf(LocalDateTime.parse(text.replace(' ', 'T')));
                }
            }
        }
        if (targetType == LocalDate.class) {
            return LocalDate.parse(text);
        }
        if (targetType == LocalTime.class) {
            return LocalTime.parse(text);
        }
        if (targetType == BigDecimal.class) {
            return new BigDecimal(text);
        }
        if (targetType == Long.class || targetType == long.class) {
            return Long.valueOf(text);
        }
        if (targetType == Integer.class || targetType == int.class) {
            return Integer.valueOf(text);
        }
        if (targetType == Double.class || targetType == double.class) {
            return Double.valueOf(text);
        }
        if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.valueOf(text);
        }
        return value;
    }

    public static Timestamp now() {
        return Timestamp.from(Instant.now().truncatedTo(ChronoUnit.MILLIS));
    }

    private List<jakarta.persistence.criteria.Predicate> predicates(
            Class<?> entityClass, Map<String, ?> filters, jakarta.persistence.criteria.Root<?> root,
            jakarta.persistence.criteria.CriteriaBuilder builder) {
        return filters.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().toString().isBlank())
                .map(entry -> builder.equal(root.get(entry.getKey()),
                        convertForClass(entry.getValue(), propertyType(entityClass, entry.getKey()))))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}
