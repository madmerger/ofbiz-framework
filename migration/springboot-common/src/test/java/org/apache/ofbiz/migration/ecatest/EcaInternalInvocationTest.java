package org.apache.ofbiz.migration.ecatest;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ofbiz.migration.common.EcaCatalog;
import org.apache.ofbiz.migration.common.EcaEngine;
import org.apache.ofbiz.migration.common.EcaService;
import org.apache.ofbiz.migration.common.EcaServiceInterceptor;
import org.apache.ofbiz.migration.common.EntityAutoService;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationSecurityProperties;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class EcaInternalInvocationTest {
    @Test
    void internalAliasRunsOriginalRuleAndDispatcherDoesNotDoubleWrapIt() throws Exception {
        var beans = new DefaultListableBeanFactory();
        var registry = new ServiceHandlerRegistry();
        var security = new MigrationSecurityProperties();
        security.setServicePermissionPrefix("TEST");
        var transactions = new TestTransactions();
        var catalog = new EcaCatalog(new ObjectMapper());
        var engine = new EcaEngine(catalog, registry,
                beans.getBeanProvider(MigrationServiceDispatcher.class), beans.getBeanProvider(UserDetailsService.class),
                security, transactions, beans.getBeanProvider(EntityRegistry.class));
        var dispatcher = new MigrationServiceDispatcher(registry, mock(EntityAutoService.class),
                mock(EntityRegistry.class), security, engine);
        beans.registerSingleton("dispatcher", dispatcher);
        beans.registerSingleton("engine", engine);
        beans.registerSingleton("registry", registry);
        beans.registerSingleton("transactions", transactions);
        beans.registerSingleton("catalog", catalog);
        var interceptor = new EcaServiceInterceptor(beans.getBeanProvider(EcaEngine.class),
                beans.getBeanProvider(ServiceHandlerRegistry.class), beans.getBeanProvider(PlatformTransactionManager.class),
                beans.getBeanProvider(EcaCatalog.class));
        PersonServices original = new PersonServices();
        PersonServices proxy = (PersonServices) interceptor.postProcessAfterInitialization(original, "personServices");
        AtomicInteger roles = new AtomicInteger();
        registry.registerHandler("party", "createPerson", proxy::createNewPerson);
        registry.registerHandler("party", "ensureNaPartyRole", input -> {
            assertEquals("INTERNAL_PERSON", input.get("partyId"));
            roles.incrementAndGet();
            return Map.of();
        });
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("test", null,
                List.of(new SimpleGrantedAuthority("TEST_ADMIN"))));
        try {
            proxy.createNewPerson(Map.of("partyId", "INTERNAL_PERSON"));
            dispatcher.execute("party", "createPerson", Map.of("partyId", "INTERNAL_PERSON"));
            assertEquals(2, original.calls);
            assertEquals(2, roles.get());
            assertEquals(2L, engine.counters().get("party/servicedef/secas.xml#1:executed"));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    public static class PersonServices {
        int calls;
        @EcaService("createPerson")
        public Map<String, Object> createNewPerson(Map<String, Object> input) {
            calls++;
            return Map.of("partyId", input.get("partyId"));
        }
    }

    static class TestTransactions extends AbstractPlatformTransactionManager {
        @Override protected Object doGetTransaction() { return new Object(); }
        @Override protected void doBegin(Object tx, TransactionDefinition definition) { }
        @Override protected void doCommit(DefaultTransactionStatus status) { }
        @Override protected void doRollback(DefaultTransactionStatus status) { }
    }
}
