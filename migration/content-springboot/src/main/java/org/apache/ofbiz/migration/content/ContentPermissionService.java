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

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.ofbiz.migration.common.MigrationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * DataResourcePermissionServices.xml#genericDataResourcePermission の Java 移植。
 * テンプレート付き DataResource は CONTENTMGR_SUPER、通常は CONTENTMGR、不足時は CONTENTMGR_ROLE と所有者ロールで判定する。
 */
@Component
public final class ContentPermissionService {
    private final ContentEntityStore store;

    public ContentPermissionService(ContentEntityStore store) {
        this.store = store;
    }

    public boolean hasDataResourcePermission(String mainAction, String dataResourceId, String dataTemplateTypeId) {
        String action = mainAction == null ? "VIEW" : mainAction.toUpperCase(Locale.ROOT);
        Object dataResource = ContentEntityStore.isEmpty(dataResourceId)
                ? null : store.findOne("DataResource", Map.of("dataResourceId", dataResourceId));
        boolean template = isTemplate(dataTemplateTypeId)
                || (dataResource != null && isTemplate((String) store.get(dataResource, "dataTemplateTypeId")));
        if (template) {
            return basePermission("CONTENTMGR_SUPER", action);
        }
        if (basePermission("CONTENTMGR", action)) {
            return true;
        }
        if (!basePermission("CONTENTMGR_ROLE", action)) {
            return false;
        }
        if (!"UPDATE".equals(action)) {
            return true;
        }
        if (ContentEntityStore.isEmpty(dataResourceId)) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "ContentSecurityUpdatePermission");
        }
        if (dataResource == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "ContentDataResourceNotFound");
        }
        return !store.findList("DataResourceRole", Map.of(
                "dataResourceId", dataResourceId, "partyId", ContentEntityStore.currentUserLoginId())).isEmpty();
    }

    public void requireDataResourcePermission(String mainAction, String dataResourceId, String dataTemplateTypeId) {
        if (!hasDataResourcePermission(mainAction, dataResourceId, dataTemplateTypeId)) {
            throw new MigrationException(HttpStatus.FORBIDDEN, "Service permission denied");
        }
    }

    /** CommonPermissionServices#genericBasePermissionCheck 相当（_ADMIN は全アクションを許可）。 */
    public static boolean basePermission(String primaryPermission, String action) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> authorities = authentication == null
                ? Set.of() : AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        return authorities.contains(primaryPermission + "_ADMIN") || authorities.contains(primaryPermission + "_" + action);
    }

    private static boolean isTemplate(String dataTemplateTypeId) {
        return !ContentEntityStore.isEmpty(dataTemplateTypeId) && !"NONE".equals(dataTemplateTypeId);
    }
}
