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
package org.apache.ofbiz.migration.content;

/** product/content の SEO 代替 URL 生成サービス呼び出しを分離する Port。 */
public interface CatalogSeoPort {
    /** createMissingCategoryAndProductAltUrls 相当。 */
    SeoResult createMissingCategoryAndProductAltUrls(String prodCatalogId, boolean category, boolean product);

    /** createMissingContentAltUrls 相当。 */
    SeoResult createMissingContentAltUrls(String webSiteId, String prodCatalogId);

    /** 対象 webSite の ProductStore に紐づく有効な prodCatalogId 一覧。 */
    java.util.List<String> productStoreCatalogIds(String productStoreId);

    record SeoResult(int categoriesNotUpdated, int categoriesUpdated, int productsNotUpdated, int productsUpdated,
            int contentsNotUpdated, int contentsUpdated) {
        public static SeoResult empty() {
            return new SeoResult(0, 0, 0, 0, 0, 0);
        }
    }
}
