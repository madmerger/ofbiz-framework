package org.apache.ofbiz.migration.marketing.adapter;

import java.util.Map;
import java.util.Optional;

import org.apache.ofbiz.migration.datamodel.entity.ProductStoreEmailSetting;
import org.apache.ofbiz.migration.datamodel.entity.WebSite;
import org.apache.ofbiz.migration.marketing.MarketingData;
import org.apache.ofbiz.migration.marketing.port.ProductStorePort;
import org.springframework.stereotype.Component;

/**
 * Local adapter for WebSite / ProductStoreEmailSetting lookups.
 */
@Component
public class LocalProductStoreAdapter implements ProductStorePort {

    private final MarketingData data;

    public LocalProductStoreAdapter(MarketingData data) {
        this.data = data;
    }

    @Override
    public Optional<String> productStoreIdOfWebSite(String webSiteId) {
        if (webSiteId == null) {
            return Optional.empty();
        }
        return data.one(WebSite.class, Map.of("webSiteId", webSiteId)).map(WebSite::getProductStoreId);
    }

    @Override
    public Optional<StoreEmail> storeEmail(String productStoreId, String emailType) {
        if (productStoreId == null) {
            return Optional.empty();
        }
        return data.one(ProductStoreEmailSetting.class, Map.of("productStoreId", productStoreId, "emailType", emailType))
                .map(setting -> new StoreEmail(
                        setting.getBodyScreenLocation(), setting.getFromAddress(), setting.getSubject()));
    }
}
