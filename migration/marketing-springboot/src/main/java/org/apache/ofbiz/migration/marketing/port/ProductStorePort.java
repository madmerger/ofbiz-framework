package org.apache.ofbiz.migration.marketing.port;

import java.util.Optional;

/**
 * Product/content component data used to choose contact list notification e-mail settings.
 */
public interface ProductStorePort {

    Optional<String> productStoreIdOfWebSite(String webSiteId);

    Optional<StoreEmail> storeEmail(String productStoreId, String emailType);

    record StoreEmail(String bodyScreenLocation, String fromAddress, String subject) { }
}
