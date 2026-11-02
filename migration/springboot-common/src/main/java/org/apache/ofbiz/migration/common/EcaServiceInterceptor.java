package org.apache.ofbiz.migration.common;

import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class EcaServiceInterceptor implements BeanPostProcessor, Ordered {
    private final ObjectProvider<EcaEngine> engines;
    private final ObjectProvider<ServiceHandlerRegistry> registries;
    private final ObjectProvider<PlatformTransactionManager> transactions;
    private final ObjectProvider<EcaCatalog> catalogs;

    public EcaServiceInterceptor(ObjectProvider<EcaEngine> engines, ObjectProvider<ServiceHandlerRegistry> registries,
            ObjectProvider<PlatformTransactionManager> transactions, ObjectProvider<EcaCatalog> catalogs) {
        this.engines = engines;
        this.registries = registries;
        this.transactions = transactions;
        this.catalogs = catalogs;
    }

    @Override
    public int getOrder() { return Ordered.LOWEST_PRECEDENCE; }

    @Override
    public Object postProcessAfterInitialization(Object bean, String name) {
        Class<?> type = AopUtils.getTargetClass(bean);
        if (!type.getName().startsWith("org.apache.ofbiz.migration.")
                || type.getName().startsWith("org.apache.ofbiz.migration.common.")
                || Modifier.isFinal(type.getModifiers())) return bean;
        boolean service = java.util.Arrays.stream(type.getDeclaredMethods()).anyMatch(method ->
                Map.class.isAssignableFrom(method.getReturnType()) && method.getParameterCount() == 1
                && Map.class.isAssignableFrom(method.getParameterTypes()[0])
                && !Modifier.isPrivate(method.getModifiers()) && !Modifier.isFinal(method.getModifiers())
                && (method.isAnnotationPresent(EcaService.class) || catalogs.getObject().hasServiceRules(method.getName())));
        if (!service) return bean;
        ProxyFactory factory = new ProxyFactory(bean);
        factory.setProxyTargetClass(true);
        factory.addAdvice((MethodInterceptor) invocation -> {
            var method = org.springframework.util.ClassUtils.getMostSpecificMethod(invocation.getMethod(), type);
            if (!Map.class.isAssignableFrom(method.getReturnType()) || invocation.getArguments().length != 1
                    || !(invocation.getArguments()[0] instanceof Map<?, ?> raw)) return invocation.proceed();
            EcaService annotation = method.getAnnotation(EcaService.class);
            String target = annotation == null ? method.getName() : annotation.value();
            var owner = registries.getObject().componentFor("", target);
            if (owner.isEmpty() || engines.getObject().dispatching(target)) return invocation.proceed();
            Map<String, Object> input = new LinkedHashMap<>();
            raw.forEach((key, value) -> input.put(key.toString(), value));
            invocation.getArguments()[0] = input;
            return new TransactionTemplate(transactions.getObject()).execute(status ->
                    engines.getObject().service(owner.get(), target, input, () -> {
                        try {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> result = (Map<String, Object>) invocation.proceed();
                            return result;
                        } catch (RuntimeException exception) {
                            throw exception;
                        } catch (Throwable exception) {
                            throw new IllegalStateException("Internal ECA service invocation failed", exception);
                        }
                    }));
        });
        return factory.getProxy();
    }
}
