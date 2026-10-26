/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
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
