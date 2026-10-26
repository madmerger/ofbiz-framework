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
package org.apache.ofbiz.migration.product;

import java.util.Locale;
import java.util.Set;
import org.apache.ofbiz.migration.common.MigrationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ProductPermissions {
    public boolean hasPermission(String base, String action) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> authorities = authentication == null
                ? Set.of()
                : AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        String prefix = base.toUpperCase(Locale.ROOT);
        String normalizedAction = action.toUpperCase(Locale.ROOT);
        return authorities.contains(prefix + "_ADMIN")
                || authorities.contains(prefix + "_" + normalizedAction);
    }

    public void require(String base, String action) {
        if (!hasPermission(base, action)) {
            throw new MigrationException(HttpStatus.FORBIDDEN, "Service permission denied");
        }
    }

    public void requireAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> authorities = authentication == null
                ? Set.of()
                : AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        if (!authorities.contains(authority)) {
            throw new MigrationException(HttpStatus.FORBIDDEN, "Service permission denied");
        }
    }
}
