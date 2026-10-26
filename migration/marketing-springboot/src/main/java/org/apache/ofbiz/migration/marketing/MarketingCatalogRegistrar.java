package org.apache.ofbiz.migration.marketing;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.apache.ofbiz.migration.marketing.generated.ServiceCatalog;
import org.springframework.stereotype.Component;

/**
 * Registers every marketing entity-auto service generated from the OFBiz service definitions.
 */
@Component
public class MarketingCatalogRegistrar {

    public static final String COMPONENT = "marketing";

    public MarketingCatalogRegistrar(ServiceHandlerRegistry registry) {
        registry.registerCatalog(COMPONENT, definitions());
    }

    public static Map<String, ServiceHandlerRegistry.ServiceDefinition> definitions() {
        Map<String, ServiceHandlerRegistry.ServiceDefinition> services = new LinkedHashMap<>();
        for (ServiceCatalog.ServiceDefinition source : ServiceCatalog.all().values()) {
            services.put(source.name(), new ServiceHandlerRegistry.ServiceDefinition(
                    source.name(),
                    source.entity(),
                    source.invoke(),
                    source.permissionMainAction(),
                    source.params().stream()
                            .map(param -> new ServiceHandlerRegistry.ParamDefinition(
                                    param.name(), param.mode(), param.optional(), param.type()))
                            .toList()));
        }
        return services;
    }
}
