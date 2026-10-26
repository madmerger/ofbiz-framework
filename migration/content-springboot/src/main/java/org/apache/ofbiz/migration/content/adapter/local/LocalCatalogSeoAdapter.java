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
package org.apache.ofbiz.migration.content.adapter.local;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.content.CatalogSeoPort;
import org.apache.ofbiz.migration.content.ContentEntityStore;
import org.springframework.stereotype.Component;

/**
 * SEO 代替 URL 生成の local adapter。
 * 実際の URL 生成（product の createMissingCategoryAndProductAltUrls、content の createMissingContentAltUrls）は
 * 未移植のため、ここでは件数 0 を返す。product コンポーネント移行後に remote adapter へ差し替える。
 */
@Component
public final class LocalCatalogSeoAdapter implements CatalogSeoPort {
    private final ContentEntityStore store;

    public LocalCatalogSeoAdapter(ContentEntityStore store) {
        this.store = store;
    }

    @Override
    public SeoResult createMissingCategoryAndProductAltUrls(String prodCatalogId, boolean category, boolean product) {
        return SeoResult.empty();
    }

    @Override
    public SeoResult createMissingContentAltUrls(String webSiteId, String prodCatalogId) {
        return SeoResult.empty();
    }

    @Override
    public List<String> productStoreCatalogIds(String productStoreId) {
        if (productStoreId == null) {
            return List.of();
        }
        Timestamp now = ContentEntityStore.now();
        return store.findList("ProductStoreCatalog", Map.of("productStoreId", productStoreId)).stream()
                .filter(row -> {
                    Timestamp from = (Timestamp) store.get(row, "fromDate");
                    Timestamp thru = (Timestamp) store.get(row, "thruDate");
                    return (from == null || !from.after(now)) && (thru == null || thru.after(now));
                })
                .map(row -> (String) store.get(row, "prodCatalogId"))
                .toList();
    }
}
