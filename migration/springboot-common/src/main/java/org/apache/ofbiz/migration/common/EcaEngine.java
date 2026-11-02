package org.apache.ofbiz.migration.common;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class EcaEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(EcaEngine.class);
    private final EcaCatalog catalog;
    private final ServiceHandlerRegistry registry;
    private final ObjectProvider<MigrationServiceDispatcher> dispatcher;
    private final ObjectProvider<UserDetailsService> users;
    private final ObjectProvider<EntityRegistry> entities;
    private final MigrationSecurityProperties security;
    private final TransactionTemplate independent;
    private final TransactionTemplate required;
    private final ThreadLocal<Deque<TransactionStatus>> transactions = ThreadLocal.withInitial(ArrayDeque::new);
    private final ThreadLocal<Deque<String>> stack = ThreadLocal.withInitial(ArrayDeque::new);
    private final ThreadLocal<Deque<String>> services = ThreadLocal.withInitial(ArrayDeque::new);
    private final ThreadLocal<Deque<Boolean>> ignoredErrors = ThreadLocal.withInitial(ArrayDeque::new);
    private final ThreadLocal<Map<String, Map<String, Object>>> logins = ThreadLocal.withInitial(LinkedHashMap::new);
    private final Map<String, LongAdder> counters = new ConcurrentHashMap<>();

    public EcaEngine(EcaCatalog catalog, ServiceHandlerRegistry registry,
            ObjectProvider<MigrationServiceDispatcher> dispatcher, ObjectProvider<UserDetailsService> users,
            MigrationSecurityProperties security, PlatformTransactionManager transactions, ObjectProvider<EntityRegistry> entities) {
        this.catalog = catalog;
        this.registry = registry;
        this.dispatcher = dispatcher;
        this.users = users;
        this.entities = entities;
        this.security = security;
        independent = new TransactionTemplate(transactions);
        independent.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        required = new TransactionTemplate(transactions);
    }

    public Map<String, Long> counters() {
        Map<String, Long> snapshot = new java.util.TreeMap<>();
        counters.forEach((name, count) -> snapshot.put(name, count.sum()));
        return Map.copyOf(snapshot);
    }

    Map<String, Object> service(String component, String service, Map<String, Object> input,
            Supplier<Map<String, Object>> invocation) {
        boolean nested = !services.get().isEmpty();
        services.get().push(service);
        try {
            if (nested) return executeService(component, service, input, invocation);
            return required.execute(status -> {
                transactions.get().push(status);
                try { return executeService(component, service, input, invocation); }
                finally {
                    transactions.get().pop();
                    if (transactions.get().isEmpty()) transactions.remove();
                }
            });
        } finally {
            services.get().pop();
            if (services.get().isEmpty()) {
                services.remove();
                logins.remove();
            }
            if (stack.get().isEmpty()) stack.remove();
        }
    }

    boolean dispatching(String service) { return services.get().contains(service); }

    private Map<String, Object> executeService(String component, String service, Map<String, Object> input,
            Supplier<Map<String, Object>> invocation) {
        Map<String, Object> result = new LinkedHashMap<>();
        Map<String, Object> context = new LinkedHashMap<>(input);
        context.remove("userLogin");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            context.put("userLogin", logins.get().computeIfAbsent(authentication.getName(), name -> userLogin(authentication)));
        }
        event("service", component, service, "", "global-rollback", context, result, null);
        event("service", component, service, "", "global-commit", context, result, null);
        event("service", component, service, "", "auth", context, result, null);
        event("service", component, service, "", "in-validate", context, result, null);
        event("service", component, service, "", "invoke", context, result, null);
        if (successful(result)) result.putAll(invocationWithInput(input, context, invocation));
        context.putAll(result);
        event("service", component, service, "", "out-validate", context, result, null);
        event("service", component, service, "", "commit", context, result, null);
        event("service", component, service, "", "global-commit-post-run", context, result, null);
        event("service", component, service, "", "return", context, result, null);
        if ("error".equals(result.get("responseMessage"))
                && (ignoredErrors.get().isEmpty() || !ignoredErrors.get().peek())) rollbackOnly();
        return result;
    }

    private Map<String, Object> userLogin(Authentication authentication) {
        Map<String, Object> login = new LinkedHashMap<>();
        login.put("userLoginId", authentication.getName());
        Object partyId = null;
        var principal = new org.springframework.beans.BeanWrapperImpl(authentication.getPrincipal());
        if (principal.isReadableProperty("partyId")) partyId = principal.getPropertyValue("partyId");
        EntityRegistry registry = entities.getIfAvailable();
        if (partyId == null && registry != null) {
            Object persisted = registry.findByPrimaryKey(registry.entityClass("UserLogin"), Map.of("userLoginId", authentication.getName()));
            if (persisted != null) partyId = registry.get(persisted, "partyId");
        }
        if (partyId != null) login.put("partyId", partyId);
        return java.util.Collections.unmodifiableMap(login);
    }

    private Map<String, Object> invocationWithInput(Map<String, Object> input, Map<String, Object> context,
            Supplier<Map<String, Object>> invocation) {
        input.clear();
        input.putAll(context);
        return invocation.get();
    }

    void entity(String component, String entity, String operation, String event,
            Map<String, Object> value, Object instance) {
        event("entity", component, entity, operation, event, value, new LinkedHashMap<>(), instance);
    }

    void event(String kind, String component, String trigger, String operation, String event,
            Map<String, Object> context, Map<String, Object> result, Object instance) {
        Set<String> actionsRun = new HashSet<>();
        for (JsonNode rule : catalog.rules(kind, trigger, event)) {
            String id = rule.path("id").asText();
            if (!rule.path("disabledReason").asText().isBlank() || "false".equals(attr(rule, "enabled"))) {
                count(id, "disabled-hand-wired");
                continue;
            }
            if ("entity".equals(kind) && !"any".equals(attr(rule, "operation"))
                    && !List.of(attr(rule, "operation").split("-")).contains(operation)) continue;
            if ("error".equals(result.get("responseMessage")) && !flag(rule, "run-on-error", false)
                    || "fail".equals(result.get("responseMessage")) && !flag(rule, "run-on-failure", false)) continue;
            if (stack.get().contains(id) || stack.get().size() >= 32) {
                count(id, "recursion-guard");
                continue;
            }
            if (!supported(rule, component)) {
                count(id, "unsupported-condition");
                continue;
            }
            stack.get().push(id);
            try {
                boolean matches = true;
                for (JsonNode child : rule.path("children")) {
                    if (!Set.of("action", "set").contains(child.path("tag").asText())
                            && !EcaConditionEvaluator.evaluate(child, context,
                            (name, input) -> dispatch(component, name, input), trigger)) {
                        matches = false;
                        break;
                    }
                }
                if (!matches) continue;
                count(id, "matched");
                for (JsonNode child : rule.path("children")) {
                    if ("set".equals(child.path("tag").asText())) set(child, context);
                    if ("action".equals(child.path("tag").asText())) {
                        String target = attr(child, "service");
                        if (actionsRun.contains(target)) continue;
                        if (registry.componentFor(component, target).isEmpty()) {
                            count(id, "target-unported");
                            LOGGER.debug("Skipping ECA {}: target {} is not registered", id, target);
                            continue;
                        }
                        if (action(rule, child, kind, component, context, result, instance)) actionsRun.add(target);
                    }
                }
            } finally {
                stack.get().pop();
                if (stack.get().isEmpty()) stack.remove();
            }
        }
    }

    private boolean supported(JsonNode node, String component) {
        if ("condition-service".equals(node.path("tag").asText())
                && registry.componentFor(component, attr(node, "service-name")).isEmpty()) return false;
        for (JsonNode child : node.path("children")) {
            String tag = child.path("tag").asText();
            if (!Set.of("condition", "condition-field", "condition-service", "and", "or", "xor", "not", "set", "action")
                    .contains(tag) || !supported(child, component)) return false;
        }
        return true;
    }

    private boolean action(JsonNode rule, JsonNode action, String kind, String component,
            Map<String, Object> context, Map<String, Object> result, Object instance) {
        String event = attr(rule, "event");
        String id = rule.path("id").asText();
        String target = attr(action, "service");
        Map<String, Object> input = catalog.inputs(target, context);
        if (!attr(action, "value-attr").isBlank()) input.put(attr(action, "value-attr"), instance);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Supplier<Map<String, Object>> call = () -> {
            ignoredErrors.get().push(kind.equals("entity") ? !flag(action, "rollback-on-error", false)
                    : flag(action, "ignore-error", true));
            try {
                return asUser(action, authentication, () -> dispatch(component, target, new LinkedHashMap<>(input)));
            } finally {
                ignoredErrors.get().pop();
                if (ignoredErrors.get().isEmpty()) ignoredErrors.remove();
            }
        };
        if (event.startsWith("global-") || "async".equals(attr(action, "mode"))) {
            List<String> ancestry = List.copyOf(stack.get());
            Runnable deferred = () -> {
                Deque<String> previous = stack.get();
                stack.set(new ArrayDeque<>(ancestry));
                try {
                    independent.execute(status -> call.get());
                    count(id, "executed");
                } catch (RuntimeException exception) {
                    count(id, "action-error");
                    LOGGER.warn("Deferred ECA {} failed: {}", id, exception.getClass().getSimpleName());
                } finally {
                    if (previous.isEmpty()) stack.remove(); else stack.set(previous);
                }
            };
            boolean rollback = event.equals("global-rollback");
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        if (rollback ? status == STATUS_ROLLED_BACK : status == STATUS_COMMITTED) deferred.run();
                    }
                });
            } else if (!rollback) deferred.run();
            return true;
        }
        try {
            Map<String, Object> output = flag(action, "new-transaction", false)
                    ? independent.execute(status -> call.get()) : call.get();
            count(id, "executed");
            boolean error = "error".equals(output.get("responseMessage"));
            boolean failure = "fail".equals(output.get("responseMessage"));
            if (kind.equals("entity")) {
                if (error) throw new IllegalStateException("Entity ECA target returned error: " + target);
                if (flag(action, "result-to-value", true)) context.putAll(catalog.outputs(target, output));
            } else {
                Map<String, Object> values = catalog.outputs(target, output);
                if (flag(action, "result-to-context", true)) context.putAll(values);
                if (!attr(action, "result-map-name").isBlank()) {
                    String mapName = attr(action, "result-map-name");
                    Map<String, Object> merged = new LinkedHashMap<>();
                    if (context.get(mapName) instanceof Map<?, ?> old) old.forEach((key, value) -> merged.put(key.toString(), value));
                    merged.putAll(values);
                    context.put(mapName, merged);
                }
                if (flag(action, "result-to-result", false)) result.putAll(values);
                if (error && !flag(action, "ignore-error", true) || failure && !flag(action, "ignore-failure", true)) {
                    result.putAll(output);
                    return false;
                }
            }
            return true;
        } catch (RuntimeException exception) {
            count(id, "action-error");
            if (kind.equals("entity")) {
                if (flag(action, "rollback-on-error", false)) rollbackOnly();
                if (flag(action, "abort-on-error", false)) throw exception;
            } else if (!flag(action, "ignore-error", true)) throw exception;
            LOGGER.debug("Ignored ECA action error for {}: {}", id, exception.getClass().getSimpleName());
            return false;
        }
    }

    private Map<String, Object> asUser(JsonNode action, Authentication original, Supplier<Map<String, Object>> call) {
        var previous = SecurityContextHolder.getContext();
        var context = SecurityContextHolder.createEmptyContext();
        String runAs = attr(action, "run-as-user");
        if (runAs.isBlank()) runAs = attr(action, "runAsUser");
        Authentication authentication = original;
        if (runAs.equals("system")) {
            authentication = new UsernamePasswordAuthenticationToken("system", null,
                    List.of(new SimpleGrantedAuthority(security.getServicePermissionPrefix() + "_ADMIN")));
        } else if (!runAs.isBlank()) {
            var user = users.getObject().loadUserByUsername(runAs);
            authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        }
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        try { return call.get(); } finally { SecurityContextHolder.setContext(previous); }
    }

    private Map<String, Object> dispatch(String component, String target, Map<String, Object> input) {
        String owner = registry.componentFor(component, target).orElseThrow();
        return dispatcher.getObject().executeEca(owner, target, input);
    }

    @SuppressWarnings("unchecked")
    private static void set(JsonNode set, Map<String, Object> context) {
        String field = attr(set, "field-name");
        String source = attr(set, "env-name");
        Object value = set.path("attributes").has("value") ? EcaConditionEvaluator.expand(attr(set, "value"), context)
                : source.startsWith("${") ? EcaConditionEvaluator.expand(source, context)
                : EcaConditionEvaluator.lookup(context, source);
        if (value == null) return;
        String format = attr(set, "format");
        value = switch (format) {
            case "to-upper" -> value.toString().toUpperCase(Locale.ROOT);
            case "to-lower" -> value.toString().toLowerCase(Locale.ROOT);
            case "long" -> Long.valueOf(value.toString());
            case "double" -> Double.valueOf(value.toString());
            case "hash-code" -> value.toString().hashCode();
            case "append" -> java.util.Objects.toString(context.get(source), "") + value;
            default -> value;
        };
        String[] path = field.split("\\.");
        Map<String, Object> destination = context;
        for (int i = 0; i < path.length - 1; i++) {
            Object old = destination.get(path[i]);
            Map<String, Object> nested = old instanceof Map<?, ?> map
                    ? new LinkedHashMap<>((Map<String, Object>) map) : new LinkedHashMap<>();
            destination.put(path[i], nested);
            destination = nested;
        }
        destination.put(path[path.length - 1], value);
    }

    private static boolean successful(Map<String, Object> result) {
        return !Set.of("error", "fail").contains(java.util.Objects.toString(result.get("responseMessage"), ""));
    }

    private void rollbackOnly() {
        if (!transactions.get().isEmpty()) transactions.get().peek().setRollbackOnly();
    }

    private void count(String id, String status) { counters.computeIfAbsent(id + ":" + status, ignored -> new LongAdder()).increment(); }
    private static String attr(JsonNode node, String key) { return EcaCatalog.attribute(node, key); }
    private static boolean flag(JsonNode node, String key, boolean fallback) {
        return attr(node, key).isBlank() ? fallback : Boolean.parseBoolean(attr(node, key));
    }
}
