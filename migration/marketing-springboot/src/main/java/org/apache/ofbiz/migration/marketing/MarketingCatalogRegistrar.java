package org.apache.ofbiz.migration.marketing;

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
        registry.registerCatalog(COMPONENT, ServiceCatalog.all());
    }
}
