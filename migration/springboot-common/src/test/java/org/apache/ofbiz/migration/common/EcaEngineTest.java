package org.apache.ofbiz.migration.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EcaEngineTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private final ServiceHandlerRegistry registry = new ServiceHandlerRegistry();
    private final MigrationServiceDispatcher dispatcher = mock(MigrationServiceDispatcher.class);
    private final TestTransactions transactions = new TestTransactions();

    @AfterEach
    void clearAuthentication() { SecurityContextHolder.clearContext(); }

    @SuppressWarnings("unchecked")
    private EcaEngine engine(JsonNode catalog) {
        ObjectProvider<MigrationServiceDispatcher> dispatchers = mock(ObjectProvider.class);
        ObjectProvider<UserDetailsService> users = mock(ObjectProvider.class);
        when(dispatchers.getObject()).thenReturn(dispatcher);
        return new EcaEngine(new EcaCatalog(catalog), registry, dispatchers, users,
                new MigrationSecurityProperties(), transactions);
    }

    private EcaEngine engine(String... rules) throws Exception {
        return engine(mapper.readTree("{\"rules\":[" + String.join(",", rules) + "],\"parameters\":{}}"));
    }

    private String rule(String id, String event, String extra, String children) {
        return "{\"id\":\"" + id + "\",\"kind\":\"service\",\"attributes\":{\"service\":\"root\","
                + "\"event\":\"" + event + "\"" + extra + "},\"children\":[" + children + "]}";
    }

    private String action(String name, String extra) {
        return "{\"tag\":\"action\",\"attributes\":{\"service\":\"" + name + "\",\"mode\":\"sync\"" + extra + "}}";
    }

    private void register(String name) {
        registry.registerHandler("test", name, input -> Map.of());
    }

    @Test
    void generatedInheritedOutputSurvivesElectronicTextActionContextFiltering() throws Exception {
        EcaCatalog original = new EcaCatalog(mapper);
        JsonNode electronic = original.rules("service", "createDataText", "invoke").get(0).deepCopy();
        ((com.fasterxml.jackson.databind.node.ObjectNode) electronic).put("disabledReason", "");
        register("createElectronicText");
        when(dispatcher.executeEca(eq("test"), eq("createElectronicText"), anyMap()))
                .thenReturn(Map.of("dataResourceId", "CREATED_ID", "undeclared", "not an output"));
        JsonNode generated;
        try (var stream = new org.springframework.core.io.ClassPathResource("eca-catalog.json").getInputStream()) {
            generated = mapper.valueToTree(Map.of("rules", List.of(electronic), "parameters", mapper.readTree(stream).path("parameters")));
        }
        EcaEngine engine = engine(generated);
        Map<String, Object> input = new LinkedHashMap<>(Map.of("dataResourceTypeId", "ELECTRONIC_TEXT", "textData", "body"));
        engine.service("test", "createDataText", input, () -> {
            assertEquals("CREATED_ID", input.get("dataResourceId"));
            assertFalse(input.containsKey("undeclared"));
            return Map.of();
        });
        verify(dispatcher).executeEca(eq("test"), eq("createElectronicText"), argThat(values -> !values.containsKey("dataResourceId")));
    }

    @Test
    void eventsHaveDefinedOrderAndCommitBoundaries() throws Exception {
        List<String> events = new ArrayList<>();
        List<String> rules = new ArrayList<>();
        for (String event : List.of("auth", "in-validate", "invoke", "out-validate", "commit", "return", "global-commit",
                "global-commit-post-run", "global-rollback")) {
            register(event);
            rules.add(rule(event, event, "", action(event, "")));
        }
        when(dispatcher.executeEca(eq("test"), anyString(), anyMap())).thenAnswer(call -> {
            events.add(call.getArgument(1));
            assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            return Map.of("responseMessage", "success");
        });
        engine(rules.toArray(String[]::new)).service("test", "root", new LinkedHashMap<>(), () -> {
            events.add("handler");
            return Map.of();
        });
        assertEquals(List.of("auth", "in-validate", "invoke", "handler", "out-validate", "commit", "return", "global-commit",
                "global-commit-post-run"), events);
        assertEquals(3, transactions.commits);
    }

    @Test
    void rollbackCallbacksRunButCommitAndAsyncCallbacksDoNot() throws Exception {
        for (String target : List.of("rollback", "commit", "async")) register(target);
        when(dispatcher.executeEca(anyString(), anyString(), anyMap())).thenReturn(Map.of());
        EcaEngine engine = engine(rule("r", "global-rollback", "", action("rollback", "")),
                rule("c", "global-commit", "", action("commit", "")),
                rule("a", "invoke", "", action("async", ",\"mode\":\"async\"")));
        assertThrows(IllegalStateException.class, () -> engine.service("test", "root", new LinkedHashMap<>(), () -> {
            throw new IllegalStateException("rollback");
        }));
        verify(dispatcher).executeEca(eq("test"), eq("rollback"), anyMap());
        verify(dispatcher, never()).executeEca(eq("test"), eq("commit"), anyMap());
        verify(dispatcher, never()).executeEca(eq("test"), eq("async"), anyMap());
        assertEquals(1, transactions.rollbacks);
    }

    @Test
    void asyncIsDeferredAndRunsOnceAfterCommit() throws Exception {
        register("later");
        when(dispatcher.executeEca(anyString(), anyString(), anyMap())).thenReturn(Map.of());
        EcaEngine engine = engine(rule("a", "commit", "", action("later", ",\"mode\":\"async\"")));
        engine.service("test", "root", new LinkedHashMap<>(), () -> {
            verifyNoInteractions(dispatcher);
            return Map.of();
        });
        verify(dispatcher).executeEca(eq("test"), eq("later"), anyMap());
    }

    @Test
    void ignoredAndPropagatedActionResultsAndRunOnErrorFlags() throws Exception {
        register("error");
        register("observe");
        when(dispatcher.executeEca(eq("test"), eq("error"), anyMap())).thenReturn(Map.of("responseMessage", "error"));
        when(dispatcher.executeEca(eq("test"), eq("observe"), anyMap())).thenReturn(Map.of());
        EcaEngine ignored = engine(rule("i", "commit", "", action("error", "")));
        assertFalse(ignored.service("test", "root", new LinkedHashMap<>(), Map::of).containsKey("responseMessage"));
        EcaEngine strict = engine(rule("s", "commit", "", action("error", ",\"ignore-error\":\"false\"")),
                rule("on-error", "return", ",\"run-on-error\":\"true\"", action("observe", "")));
        assertEquals("error", strict.service("test", "root", new LinkedHashMap<>(), Map::of).get("responseMessage"));
        verify(dispatcher).executeEca(eq("test"), eq("observe"), anyMap());
        assertEquals(1, transactions.rollbacks);
    }

    @Test
    void failureFlagsAreIndependentOfErrorFlags() throws Exception {
        register("observe");
        when(dispatcher.executeEca(anyString(), anyString(), anyMap())).thenReturn(Map.of());
        EcaEngine engine = engine(rule("no", "commit", "", action("observe", "")),
                rule("yes", "return", ",\"run-on-failure\":\"true\"", action("observe", "")));
        assertEquals("fail", engine.service("test", "root", new LinkedHashMap<>(),
                () -> Map.of("responseMessage", "fail")).get("responseMessage"));
        verify(dispatcher, times(1)).executeEca(eq("test"), eq("observe"), anyMap());
    }

    @Test
    void recursionGuardAndPerPhaseDeduplication() throws Exception {
        register("child");
        EcaEngine engine = engine(rule("cycle", "commit", "", action("child", "")),
                rule("duplicate", "commit", "", action("child", "")));
        AtomicInteger calls = new AtomicInteger();
        when(dispatcher.executeEca(anyString(), anyString(), anyMap())).thenAnswer(call -> {
            calls.incrementAndGet();
            return engine.service("test", "root", new LinkedHashMap<>(), Map::of);
        });
        engine.service("test", "root", new LinkedHashMap<>(), Map::of);
        assertEquals(2, calls.get());
        assertTrue(engine.counters().entrySet().stream().anyMatch(e -> e.getKey().endsWith(":recursion-guard") && e.getValue() > 0));
    }

    @Test
    void setResultMappingAndRunAsRestoreCaller() throws Exception {
        register("child");
        var caller = new UsernamePasswordAuthenticationToken("caller", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(caller);
        when(dispatcher.executeEca(anyString(), anyString(), anyMap())).thenAnswer(call -> {
            assertEquals("system", SecurityContextHolder.getContext().getAuthentication().getName());
            assertEquals("VALUE", ((Map<?, ?>) call.getArgument(2)).get("field"));
            return Map.of("answer", 42);
        });
        String set = "{\"tag\":\"set\",\"attributes\":{\"field-name\":\"field\",\"value\":\"value\",\"format\":\"to-upper\"}}";
        EcaEngine engine = engine(rule("set", "invoke", "", set + "," + action("child",
                ",\"run-as-user\":\"system\",\"result-to-result\":\"true\"")));
        Map<String, Object> result = engine.service("test", "root", new LinkedHashMap<>(), Map::of);
        assertEquals(42, result.get("answer"));
        assertSame(caller, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void unportedTargetsAndConditionServicesAreCounted() throws Exception {
        EcaEngine engine = engine(rule("target", "commit", "", action("missing", "")),
                rule("condition", "commit", "", "{\"tag\":\"condition-service\",\"attributes\":{\"service-name\":\"missing\"}},"
                        + action("missing", "")));
        engine.service("test", "root", new LinkedHashMap<>(), Map::of);
        assertEquals(1L, engine.counters().get("target:target-unported"));
        assertEquals(1L, engine.counters().get("condition:unsupported-condition"));
        verifyNoInteractions(dispatcher);
    }

    @Test
    void ignoredNestedValidationExceptionDoesNotMarkTheParentRollbackOnly() throws Exception {
        register("child");
        EcaEngine engine = engine(rule("ignore", "commit", "", action("child", ",\"ignore-error\":\"true\"")));
        when(dispatcher.executeEca(anyString(), anyString(), anyMap())).thenAnswer(call ->
                engine.service("test", "child", new LinkedHashMap<>(), () -> {
                    throw new IllegalArgumentException("invalid action input");
                }));
        assertEquals(Map.of("value", "retained"), engine.service("test", "root", new LinkedHashMap<>(),
                () -> Map.of("value", "retained")));
        assertEquals(0, transactions.rollbacks);
        assertEquals(1, transactions.commits);
        assertEquals(1L, engine.counters().get("ignore:action-error"));
    }

    @Test
    void nonIgnoredNestedExceptionRollsBackTheInvocation() throws Exception {
        register("child");
        EcaEngine engine = engine(rule("strict", "commit", "", action("child", ",\"ignore-error\":\"false\"")));
        when(dispatcher.executeEca(anyString(), anyString(), anyMap())).thenAnswer(call ->
                engine.service("test", "child", new LinkedHashMap<>(), () -> {
                    throw new IllegalArgumentException("invalid action input");
                }));
        assertThrows(IllegalArgumentException.class, () -> engine.service("test", "root", new LinkedHashMap<>(), Map::of));
        assertEquals(1, transactions.rollbacks);
    }

    @Test
    void supportedConditionServiceReceivesTheOriginalEnvelope() throws Exception {
        register("condition");
        register("action");
        when(dispatcher.executeEca(eq("test"), eq("condition"), anyMap())).thenAnswer(call -> {
            Map<?, ?> input = call.getArgument(2);
            assertEquals("root", input.get("serviceName"));
            assertEquals("value", ((Map<?, ?>) input.get("serviceContext")).get("field"));
            return Map.of("conditionReply", true);
        });
        when(dispatcher.executeEca(eq("test"), eq("action"), anyMap())).thenReturn(Map.of());
        EcaEngine engine = engine(rule("condition", "commit", "",
                "{\"tag\":\"condition-service\",\"attributes\":{\"service-name\":\"condition\"}}," + action("action", "")));
        engine.service("test", "root", new LinkedHashMap<>(Map.of("field", "value")), Map::of);
        verify(dispatcher).executeEca(eq("test"), eq("action"), anyMap());
    }

    @Test
    void actionFailureCanBeIgnoredOrPropagatedWithoutAnAutomaticRollback() throws Exception {
        register("failure");
        when(dispatcher.executeEca(anyString(), anyString(), anyMap())).thenReturn(Map.of("responseMessage", "fail"));
        EcaEngine ignored = engine(rule("ignored", "commit", "", action("failure", "")));
        assertEquals(Map.of(), ignored.service("test", "root", new LinkedHashMap<>(), Map::of));
        EcaEngine strict = engine(rule("strict", "commit", "", action("failure", ",\"ignore-failure\":\"false\"")));
        assertEquals("fail", strict.service("test", "root", new LinkedHashMap<>(), Map::of).get("responseMessage"));
        assertEquals(0, transactions.rollbacks);
    }

    @ParameterizedTest
    @CsvSource({"equals,2,2,true", "not-equals,2,3,true", "less,2,3,true", "greater,3,2,true",
            "less-equals,2,2,true", "greater-equals,2,2,true", "greater,2,3,false"})
    void numericOperators(String operator, String left, String right, boolean expected) {
        assertEquals(expected, EcaConditionEvaluator.compare(left, right, operator, "BigDecimal"));
    }

    @Test
    void emptyBooleanFieldAndLogicalConditions() throws Exception {
        assertTrue(EcaConditionEvaluator.compare(null, null, "is-empty", ""));
        assertFalse(EcaConditionEvaluator.compare(List.of(), null, "is-not-empty", ""));
        assertTrue(EcaConditionEvaluator.compare(true, "true", "equals", "Boolean"));
        JsonNode condition = mapper.readTree("{\"tag\":\"condition-field\",\"attributes\":{\"field-name\":\"a\","
                + "\"to-field-name\":\"b\",\"operator\":\"equals\"}}");
        assertTrue(EcaConditionEvaluator.evaluate(condition, Map.of("a", "x", "b", "x"), null, "root"));
        for (String tag : List.of("and", "or", "xor", "not")) {
            var logical = mapper.createObjectNode().put("tag", tag);
            logical.putArray("children").add(condition);
            assertEquals(!tag.equals("not"), EcaConditionEvaluator.evaluate(logical,
                    Map.of("a", "x", "b", "x"), null, "root"));
        }
    }

    @ParameterizedTest
    @CsvSource({"accounting/servicedef/secas.xml#1", "content/servicedef/secas.xml#17",
            "marketing/servicedef/secas.xml#3", "order/servicedef/secas.xml#3",
            "party/servicedef/secas.xml#1", "product/entitydef/eecas.xml#13", "workeffort/entitydef/eecas.xml#1"})
    void originalRuleForEveryComponentWithActiveRules(String id) throws Exception {
        JsonNode original;
        try (var stream = getClass().getResourceAsStream("/eca-catalog.json")) {
            original = mapper.readTree(stream);
        }
        JsonNode rule = java.util.stream.StreamSupport.stream(original.path("rules").spliterator(), false)
                .filter(r -> id.equals(r.path("id").asText())).findFirst().orElseThrow();
        var selected = mapper.createObjectNode();
        selected.putArray("rules").add(rule);
        selected.set("parameters", original.path("parameters"));
        String component = rule.path("component").asText();
        String target = EcaCatalog.attribute(rule.path("children").get(rule.path("children").size() - 1), "service");
        registry.registerHandler(component, target, input -> Map.of());
        when(dispatcher.executeEca(eq(component), eq(target), anyMap())).thenReturn(Map.of());
        EcaEngine engine = engine(selected);
        Map<String, Object> context = new LinkedHashMap<>(Map.of("partyId", "TEST_PARTY", "budgetId", "TEST_BUDGET",
                "surveyId", "TEST_SURVEY", "surveyQuestionId", "TEST_QUESTION", "statusId", "PICKLIST_CANCELLED",
                "picklistId", "TEST_PICKLIST", "orderId", "TEST_ORDER", "workEffortId", "TEST_WORK"));
        String kind = rule.path("kind").asText();
        engine.event(kind, component, EcaCatalog.attribute(rule, kind.equals("entity") ? "entity" : "service"),
                "store", EcaCatalog.attribute(rule, "event"), context, new LinkedHashMap<>(), context);
        verify(dispatcher).executeEca(eq(component), eq(target), anyMap());
        assertEquals(1L, engine.counters().get(id + ":executed"));
    }

    static class TestTransactions extends AbstractPlatformTransactionManager {
        int commits;
        int rollbacks;
        @Override protected Object doGetTransaction() { return new Object(); }
        @Override protected void doBegin(Object tx, TransactionDefinition definition) { }
        @Override protected void doCommit(DefaultTransactionStatus status) { commits++; }
        @Override protected void doRollback(DefaultTransactionStatus status) { rollbacks++; }
        @Override protected Object doSuspend(Object tx) { return tx; }
        @Override protected void doResume(Object tx, Object suspended) { }
    }
}
