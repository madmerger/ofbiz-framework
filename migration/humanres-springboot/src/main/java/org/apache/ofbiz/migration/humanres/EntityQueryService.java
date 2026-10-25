package org.apache.ofbiz.migration.humanres;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EntityQueryService {
    private final EntityRegistry entityRegistry;

    public EntityQueryService(EntityRegistry entityRegistry) {
        this.entityRegistry = entityRegistry;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> list(
            String entityName, Map<String, String> requestParams, int page, int size) {
        Map<String, String> filters = new LinkedHashMap<>(requestParams);
        filters.remove("page");
        filters.remove("size");
        var values = entityRegistry.findPage(entityName, filters, page, size);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("items", values);
        response.put("page", page);
        response.put("size", size);
        response.put("total", entityRegistry.count(entityName, filters));
        return response;
    }

    @Transactional(readOnly = true)
    public Object one(String entityName, Map<String, String> primaryKeys) {
        Object entity = entityRegistry.findByPrimaryKey(entityName, primaryKeys);
        if (entity == null) {
            throw HumanResException.notFound("ServiceValueNotFound");
        }
        return entity;
    }
}
