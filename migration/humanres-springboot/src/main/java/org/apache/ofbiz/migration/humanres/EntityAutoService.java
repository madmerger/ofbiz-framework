package org.apache.ofbiz.migration.humanres;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.persistence.EntityManager;
import org.apache.ofbiz.migration.humanres.generated.ServiceCatalog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EntityAutoService {
    private static final Set<String> DENIED_TEXT_TOKENS = Set.of(
            "java.", "beans", "freemarker", "<script", "javascript", "<body", "body ",
            "<form", "<jsp:", "<c:out", "taglib", "<prefix", "<?php", "exec(", "alert(",
            "%eval", "@eval", "eval(", "runtime", "import", "passthru", "shell_exec",
            "assert", "str_rot13", "system", "decode", "include", "iframe", "object",
            "embed", "onload", "onerror", "onclick", "onmouseover", "vbscript:",
            "data:text/html", "process ", "wget ", "curl", "bash", "powershell");

    private final EntityRegistry entityRegistry;
    private final SequenceService sequenceService;
    private final EntityManager entityManager;
    private final JdbcTemplate jdbcTemplate;

    public EntityAutoService(
            EntityRegistry entityRegistry,
            SequenceService sequenceService,
            EntityManager entityManager,
            JdbcTemplate jdbcTemplate) {
        this.entityRegistry = entityRegistry;
        this.sequenceService = sequenceService;
        this.entityManager = entityManager;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public Map<String, Object> execute(String serviceName, Map<String, ?> input) {
        ServiceCatalog.ServiceDefinition definition = ServiceCatalog.find(serviceName)
                .orElseThrow(() -> HumanResException.notFound("Unknown service: " + serviceName));
        validateText(input);
        Class<?> entityClass = entityRegistry.entityClass(definition.entity());
        return switch (definition.invoke().toLowerCase(Locale.ROOT)) {
            case "create" -> create(definition, entityClass, input);
            case "update" -> update(definition, entityClass, input);
            case "expire" -> expire(definition, entityClass, input);
            case "delete" -> delete(definition, entityClass, input);
            default -> throw HumanResException.notFound("Unsupported entity-auto operation: " + definition.invoke());
        };
    }

    @Transactional
    public Map<String, Object> createEntity(String entityName, Map<String, ?> input) {
        validateText(input);
        Class<?> entityClass = entityRegistry.entityClass(entityName);
        Object entity = entityRegistry.newEntity(entityClass);
        setProvidedFields(entity, entityClass, input, Set.of());
        setDefaultFromDate(entity, entityClass);
        ensureCompletePrimaryKey(entityName, entity, entityClass);
        if (entityRegistry.findByPrimaryKey(entityName, keyValues(entity, entityClass)) != null) {
            throw HumanResException.conflict("ServiceValueFound");
        }
        entityManager.persist(entity);
        return response(entity, entityClass, null);
    }

    @Transactional
    public Map<String, Object> updateEntity(String entityName, Map<String, ?> input) {
        validateText(input);
        Class<?> entityClass = entityRegistry.entityClass(entityName);
        Object entity = requireEntity(entityName, entityClass, input);
        checkStatusTransition(entity, entityClass, input);
        Set<String> primaryKeys = Set.copyOf(entityRegistry.primaryKeyNames(entityClass));
        setProvidedFields(entity, entityClass, input, primaryKeys);
        return response(entity, entityClass, null);
    }

    @Transactional
    public Map<String, Object> expireEntity(String entityName, Map<String, ?> input) {
        validateText(input);
        Class<?> entityClass = entityRegistry.entityClass(entityName);
        Object entity = requireEntity(entityName, entityClass, input);
        if (!entityRegistry.hasProperty(entityClass, "thruDate")) {
            throw HumanResException.badRequest(entityName + " has no thruDate field");
        }
        entityRegistry.set(entity, "thruDate", input.containsKey("thruDate") && input.get("thruDate") != null
                ? entityRegistry.convertForClass(input.get("thruDate"),
                        entityRegistry.propertyType(entityClass, "thruDate"))
                : EntityRegistry.now());
        return response(entity, entityClass, null);
    }

    @Transactional
    public Map<String, Object> deleteEntity(String entityName, Map<String, ?> input) {
        Class<?> entityClass = entityRegistry.entityClass(entityName);
        Object entity = requireEntity(entityName, entityClass, input);
        Map<String, Object> result = response(entity, entityClass, null);
        entityManager.remove(entity);
        return result;
    }

    @Transactional(readOnly = true)
    public Object findEntity(String entityName, Map<String, ?> primaryKeys) {
        Object entity = entityRegistry.findByPrimaryKey(entityName, primaryKeys);
        if (entity == null) {
            throw HumanResException.notFound("ServiceValueNotFound");
        }
        return entity;
    }

    @Transactional(readOnly = true)
    public List<Object> findEntities(String entityName, Map<String, ?> filters) {
        return entityRegistry.findByEquality(entityName, filters);
    }

    private Map<String, Object> create(
            ServiceCatalog.ServiceDefinition definition, Class<?> entityClass, Map<String, ?> input) {
        Object entity = entityRegistry.newEntity(entityClass);
        Map<String, ServiceCatalog.ParamDefinition> params = paramsByName(definition);
        setProvidedFields(entity, entityClass, input, Set.of(), params);
        setDefaultFromDate(entity, entityClass);
        assignGeneratedKeys(definition, entity, entityClass);
        validateRequiredInputs(definition, input, entityClass);
        ensureCompletePrimaryKey(definition.entity(), entity, entityClass);
        if (entityRegistry.findByPrimaryKey(definition.entity(), keyValues(entity, entityClass)) != null) {
            throw HumanResException.conflict("ServiceValueFound");
        }
        entityManager.persist(entity);
        return response(entity, entityClass, definition);
    }

    private Map<String, Object> update(
            ServiceCatalog.ServiceDefinition definition, Class<?> entityClass, Map<String, ?> input) {
        validateRequiredInputs(definition, input, entityClass);
        Object entity = requireEntity(definition.entity(), entityClass, input);
        checkStatusTransition(entity, entityClass, input);
        Set<String> primaryKeys = Set.copyOf(entityRegistry.primaryKeyNames(entityClass));
        setProvidedFields(entity, entityClass, input, primaryKeys, paramsByName(definition));
        return response(entity, entityClass, definition);
    }

    private Map<String, Object> expire(
            ServiceCatalog.ServiceDefinition definition, Class<?> entityClass, Map<String, ?> input) {
        validateRequiredInputs(definition, input, entityClass);
        Object entity = requireEntity(definition.entity(), entityClass, input);
        if (!entityRegistry.hasProperty(entityClass, "thruDate")) {
            throw HumanResException.badRequest(definition.entity() + " has no thruDate field");
        }
        Object value = input.get("thruDate");
        entityRegistry.set(entity, "thruDate", value == null
                ? EntityRegistry.now()
                : convertInput(entityClass, "thruDate", value, paramsByName(definition).get("thruDate")));
        return response(entity, entityClass, definition);
    }

    private Map<String, Object> delete(
            ServiceCatalog.ServiceDefinition definition, Class<?> entityClass, Map<String, ?> input) {
        validateRequiredInputs(definition, input, entityClass);
        Object entity = requireEntity(definition.entity(), entityClass, input);
        Map<String, Object> result = response(entity, entityClass, definition);
        entityManager.remove(entity);
        return result;
    }

    private Object requireEntity(String entityName, Class<?> entityClass, Map<String, ?> keys) {
        Object entity = entityRegistry.findByPrimaryKey(entityName, keys);
        if (entity == null) {
            throw HumanResException.notFound("ServiceValueNotFound");
        }
        return entity;
    }

    private void validateRequiredInputs(
            ServiceCatalog.ServiceDefinition definition, Map<String, ?> input, Class<?> entityClass) {
        Set<String> primaryKeys = Set.copyOf(entityRegistry.primaryKeyNames(entityClass));
        for (ServiceCatalog.ParamDefinition param : definition.params()) {
            boolean isIn = param.mode().equalsIgnoreCase("IN") || param.mode().equalsIgnoreCase("INOUT");
            if (!isIn || param.optional() || present(input.get(param.name()))) {
                continue;
            }
            if (definition.invoke().equalsIgnoreCase("create")
                    && primaryKeys.contains(param.name())
                    && isGeneratedOnCreate(definition, param.name(), primaryKeys, input)) {
                continue;
            }
            if (definition.invoke().equalsIgnoreCase("create")
                    && param.name().equals("fromDate")
                    && primaryKeys.contains(param.name())) {
                continue;
            }
            throw HumanResException.badRequest("Missing required IN parameter: " + param.name());
        }
    }

    private void setProvidedFields(
            Object entity,
            Class<?> entityClass,
            Map<String, ?> input,
            Set<String> ignoredFields) {
        setProvidedFields(entity, entityClass, input, ignoredFields, Map.of());
    }

    private void setProvidedFields(
            Object entity,
            Class<?> entityClass,
            Map<String, ?> input,
            Set<String> ignoredFields,
            Map<String, ServiceCatalog.ParamDefinition> params) {
        input.forEach((name, value) -> {
            if (value == null || ignoredFields.contains(name) || !entityRegistry.hasProperty(entityClass, name)) {
                return;
            }
            entityRegistry.set(entity, name, convertInput(entityClass, name, value, params.get(name)));
        });
    }

    private Object convertInput(
            Class<?> entityClass, String propertyName, Object value, ServiceCatalog.ParamDefinition param) {
        return param == null
                ? entityRegistry.convertForClass(value, entityRegistry.propertyType(entityClass, propertyName))
                : entityRegistry.convertForType(value, param.type());
    }

    private void setDefaultFromDate(Object entity, Class<?> entityClass) {
        if (entityRegistry.primaryKeyNames(entityClass).contains("fromDate")
                && entityRegistry.hasProperty(entityClass, "fromDate")
                && entityRegistry.get(entity, "fromDate") == null) {
            entityRegistry.set(entity, "fromDate", EntityRegistry.now());
        }
    }

    private void assignGeneratedKeys(
            ServiceCatalog.ServiceDefinition definition, Object entity, Class<?> entityClass) {
        List<String> primaryKeys = entityRegistry.primaryKeyNames(entityClass);
        Map<String, ServiceCatalog.ParamDefinition> params = paramsByName(definition);
        if (primaryKeys.size() == 1) {
            String name = primaryKeys.get(0);
            ServiceCatalog.ParamDefinition param = params.get(name);
            if (param != null
                    && (param.mode().equalsIgnoreCase("OUT")
                            || param.mode().equalsIgnoreCase("INOUT")
                                    && !present(entityRegistry.get(entity, name)))
                    && name.endsWith("Id")
                    && entityRegistry.propertyType(entityClass, name) == String.class) {
                entityRegistry.set(entity, name, sequenceService.nextId(definition.entity()));
            }
            return;
        }
        String sequenceField = subSequenceField(entity, entityClass, primaryKeys, params);
        if (sequenceField != null) {
            entityRegistry.set(entity, sequenceField, sequenceService.nextSubSequence(
                    definition.entity(), sequenceField, keyValues(entity, entityClass)));
        }
    }

    private void ensureCompletePrimaryKey(String entityName, Object entity, Class<?> entityClass) {
        for (String name : entityRegistry.primaryKeyNames(entityClass)) {
            if (entityRegistry.get(entity, name) == null) {
                throw HumanResException.badRequest("Missing primary-key parameter: " + name);
            }
        }
    }

    private void checkStatusTransition(Object entity, Class<?> entityClass, Map<String, ?> input) {
        if (!input.containsKey("statusId") || input.get("statusId") == null
                || !entityRegistry.hasProperty(entityClass, "statusId")) {
            return;
        }
        String current = String.valueOf(entityRegistry.get(entity, "statusId"));
        String next = String.valueOf(input.get("statusId"));
        if (current.equals(next)) {
            return;
        }
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from STATUS_VALID_CHANGE where STATUS_ID = ? and STATUS_ID_TO = ?",
                Integer.class, current, next);
        if (count == null || count == 0) {
            throw HumanResException.badRequest(
                    "CommonErrorNoStatusValidChange: " + current + " -> " + next);
        }
    }

    private Map<String, Object> response(
            Object entity, Class<?> entityClass, ServiceCatalog.ServiceDefinition definition) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (String key : entityRegistry.primaryKeyNames(entityClass)) {
            result.put(key, entityRegistry.get(entity, key));
        }
        if (definition != null) {
            for (ServiceCatalog.ParamDefinition param : definition.params()) {
                if ((param.mode().equalsIgnoreCase("OUT") || param.mode().equalsIgnoreCase("INOUT"))
                        && entityRegistry.hasProperty(entityClass, param.name())) {
                    result.put(param.name(), entityRegistry.get(entity, param.name()));
                }
            }
        }
        return result;
    }

    private Map<String, ServiceCatalog.ParamDefinition> paramsByName(
            ServiceCatalog.ServiceDefinition definition) {
        return definition.params().stream()
                .collect(Collectors.toMap(ServiceCatalog.ParamDefinition::name, param -> param));
    }

    private Map<String, Object> keyValues(Object entity, Class<?> entityClass) {
        Map<String, Object> keys = new LinkedHashMap<>();
        for (String key : entityRegistry.primaryKeyNames(entityClass)) {
            keys.put(key, entityRegistry.get(entity, key));
        }
        return keys;
    }

    private void validateText(Map<String, ?> values) {
        values.values().forEach(this::validateTextValue);
    }

    private void validateTextValue(Object value) {
        if (value instanceof String text) {
            String lowercase = text.toLowerCase(Locale.ROOT);
            if (DENIED_TEXT_TOKENS.stream().anyMatch(lowercase::contains)) {
                throw HumanResException.badRequest("Invalid text input");
            }
        } else if (value instanceof Map<?, ?> nested) {
            nested.values().forEach(this::validateTextValue);
        } else if (value instanceof Iterable<?> iterable) {
            iterable.forEach(this::validateTextValue);
        }
    }

    private static boolean present(Object value) {
        return value != null && !value.toString().isBlank();
    }

    private boolean isGeneratedOnCreate(
            ServiceCatalog.ServiceDefinition definition,
            String name,
            Set<String> primaryKeys,
            Map<String, ?> input) {
        if (!primaryKeys.contains(name)) {
            return false;
        }
        ServiceCatalog.ParamDefinition param = paramsByName(definition).get(name);
        if (param == null) {
            return false;
        }
        if (primaryKeys.size() != 1) {
            return false;
        }
        return param.mode().equalsIgnoreCase("OUT")
                || param.mode().equalsIgnoreCase("INOUT") && !present(input.get(name));
    }

    private String subSequenceField(
            Object entity,
            Class<?> entityClass,
            List<String> primaryKeys,
            Map<String, ServiceCatalog.ParamDefinition> params) {
        if (primaryKeys.size() < 2) {
            return null;
        }
        String sequenceField = null;
        for (String name : primaryKeys) {
            ServiceCatalog.ParamDefinition param = params.get(name);
            if (param == null) {
                return null;
            }
            if (param.mode().equalsIgnoreCase("OUT")) {
                if (sequenceField != null || !name.endsWith("SeqId")) {
                    return null;
                }
                sequenceField = name;
            } else if (!param.mode().equalsIgnoreCase("IN")) {
                return null;
            }
            if (entity != null && entityClass != null && !name.equals(sequenceField)
                    && !present(entityRegistry.get(entity, name))) {
                return null;
            }
        }
        return sequenceField;
    }
}
