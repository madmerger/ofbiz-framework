package org.apache.ofbiz.migration.common;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

final class EcaConditionEvaluator {
    private EcaConditionEvaluator() { }

    static boolean evaluate(JsonNode node, Map<String, Object> context,
            BiFunction<String, Map<String, Object>, Map<String, Object>> dispatch, String trigger) {
        String tag = node.path("tag").asText();
        if (tag.equals("condition-service")) {
            Map<String, Object> input = new java.util.LinkedHashMap<>();
            input.put("serviceContext", context);
            input.put("serviceName", trigger);
            input.put("userLogin", context.get("userLogin"));
            return Boolean.TRUE.equals(dispatch.apply(attr(node, "service-name"), input).get("conditionReply"));
        }
        if (java.util.Set.of("and", "or", "xor", "not").contains(tag)) {
            int matches = 0;
            for (JsonNode child : node.path("children")) {
                if (evaluate(child, context, dispatch, trigger)) matches++;
            }
            return switch (tag) {
                case "and" -> matches == node.path("children").size();
                case "or" -> matches > 0;
                case "xor" -> matches == 1;
                default -> matches == 0;
            };
        }
        String lhsPath = path(attr(node, "map-name"), attr(node, "field-name"));
        Object left = lookup(context, lhsPath);
        Object right = tag.equals("condition-field")
                ? lookup(context, path(attr(node, "to-map-name"), attr(node, "to-field-name")))
                : expand(attr(node, "value"), context);
        return compare(left, right, attr(node, "operator"), attr(node, "type"));
    }

    static boolean compare(Object left, Object right, String operator, String type) {
        if (operator.equals("is-empty")) return empty(left);
        if (operator.equals("is-not-empty")) return !empty(left);
        if (!type.isBlank()) {
            left = convert(left, type);
            right = convert(right, type);
        }
        boolean equal = left instanceof Number && right instanceof Number
                ? new BigDecimal(left.toString()).compareTo(new BigDecimal(right.toString())) == 0
                : Objects.equals(left, right);
        if (operator.equals("equals")) return equal;
        if (operator.equals("not-equals")) return !equal;
        if (left == null || right == null) return false;
        int comparison = left instanceof Number || right instanceof Number
                ? new BigDecimal(left.toString()).compareTo(new BigDecimal(right.toString()))
                : left.toString().compareTo(right.toString());
        return switch (operator) {
            case "less" -> comparison < 0;
            case "less-equals" -> comparison <= 0;
            case "greater" -> comparison > 0;
            case "greater-equals" -> comparison >= 0;
            case "contains" -> left.toString().contains(right.toString());
            default -> throw new IllegalArgumentException("Unsupported ECA operator: " + operator);
        };
    }

    private static Object convert(Object value, String type) {
        if (value == null) return null;
        return switch (type) {
            case "BigDecimal", "Double", "Float", "Long", "Integer", "Number" -> new BigDecimal(value.toString());
            case "Boolean" -> Boolean.valueOf(value.toString());
            case "String" -> value.toString();
            case "Timestamp", "java.sql.Timestamp" -> java.sql.Timestamp.valueOf(value.toString());
            default -> throw new IllegalArgumentException("Unsupported ECA type: " + type);
        };
    }

    static boolean empty(Object value) {
        return value == null || value instanceof String s && s.isEmpty()
                || value instanceof Collection<?> c && c.isEmpty() || value instanceof Map<?, ?> m && m.isEmpty();
    }

    static Object lookup(Map<String, Object> context, String path) {
        Object value = context;
        for (String key : path.split("\\.")) {
            value = value instanceof Map<?, ?> map ? map.get(key) : null;
        }
        return value;
    }

    static String expand(String value, Map<String, Object> context) {
        var matcher = java.util.regex.Pattern.compile("\\$\\{([^}]+)}").matcher(value);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            Object replacement = lookup(context, matcher.group(1));
            matcher.appendReplacement(result, java.util.regex.Matcher.quoteReplacement(
                    replacement == null ? "" : replacement.toString()));
        }
        return matcher.appendTail(result).toString();
    }

    private static String path(String map, String field) { return map.isBlank() ? field : map + "." + field; }
    private static String attr(JsonNode node, String name) { return EcaCatalog.attribute(node, name); }
}
