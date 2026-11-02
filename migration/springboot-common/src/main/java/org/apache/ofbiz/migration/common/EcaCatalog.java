package org.apache.ofbiz.migration.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class EcaCatalog {
    private final List<JsonNode> rules;
    private final JsonNode parameters;

    @org.springframework.beans.factory.annotation.Autowired
    public EcaCatalog(ObjectMapper mapper) throws IOException {
        this(load(mapper));
    }

    private static JsonNode load(ObjectMapper mapper) throws IOException {
        try (var stream = new ClassPathResource("eca-catalog.json").getInputStream()) {
            return mapper.readTree(stream);
        }
    }

    EcaCatalog(JsonNode catalog) {
        List<JsonNode> loaded = new ArrayList<>();
        catalog.path("rules").forEach(loaded::add);
        rules = List.copyOf(loaded);
        parameters = catalog.path("parameters");
    }

    public List<JsonNode> rules(String kind, String trigger, String event) {
        return rules.stream().filter(rule -> kind.equals(rule.path("kind").asText()))
                .filter(rule -> trigger.equals(attribute(rule, kind.equals("service") ? "service" : "entity")))
                .filter(rule -> event.equals(attribute(rule, "event"))).toList();
    }

    boolean hasServiceRules(String service) {
        return rules.stream().anyMatch(rule -> "service".equals(rule.path("kind").asText())
                && service.equals(attribute(rule, "service")));
    }

    Map<String, Object> inputs(String service, Map<String, Object> context) {
        Map<String, Object> input = new java.util.LinkedHashMap<>();
        JsonNode definition = parameters.path(service);
        definition.fields().forEachRemaining(field -> {
            if (!"OUT".equals(field.getValue().asText()) && context.containsKey(field.getKey())) {
                input.put(field.getKey(), context.get(field.getKey()));
            }
        });
        if (definition.isMissingNode()) input.putAll(context);
        for (String key : List.of("userLogin", "locale", "timeZone")) {
            if (context.containsKey(key)) input.put(key, context.get(key));
        }
        return input;
    }

    Map<String, Object> outputs(String service, Map<String, Object> output) {
        Map<String, Object> filtered = new java.util.LinkedHashMap<>();
        output.forEach((key, value) -> {
            if (!"responseMessage".equals(key) && !key.startsWith("error") && !"failMessage".equals(key)
                    && (!"IN".equals(parameters.path(service).path(key).asText())
                    && !parameters.path(service).path(key).isMissingNode()
                    || parameters.path(service).isMissingNode())) filtered.put(key, value);
        });
        return filtered;
    }

    static String attribute(JsonNode node, String name) {
        return node.path("attributes").path(name).asText("");
    }
}
