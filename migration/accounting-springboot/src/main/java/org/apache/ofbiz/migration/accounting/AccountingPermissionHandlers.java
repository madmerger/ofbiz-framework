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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.apache.ofbiz.migration.accounting;

import jakarta.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AccountingPermissionHandlers {
    private static final String COMPONENT = "accounting";
    private static final String BASE = "ACCOUNTING";
    private final ServiceHandlerRegistry registry;

    public AccountingPermissionHandlers(ServiceHandlerRegistry registry) {
        this.registry = registry;
    }

    @PostConstruct
    void registerHandlers() {
        register("acctgAgreementPermissionCheck", "basePlusRole");
        register("acctgBasePermissionCheck", "base");
        register("acctgBillingAcctCheck", "base");
        register("acctgCommissionPermissionCheck", "commission");
        register("acctgCostPermissionCheck", "base");
        register("acctgFinAcctPermissionCheck", "base");
        register("acctgFxPermissionCheck", "foreignExchange");
        register("acctgInvoicePermissionCheck", "basePlusRole");
        register("acctgPrefPermissionCheck", "preference");
        register("acctgTransactionPermissionCheck", "transaction");
        register("basicGeneralLedgerPermissionCheck", "base");
        register("fixedAssetPermissionCheck", "base");
        register("acctgPaymentPermissionCheck", "basePlusRole");
    }

    private void register(String service, String permissionCheck) {
        registry.registerHandler(COMPONENT, service, "VIEW", input -> check(permissionCheck, input));
    }

    /**
     * Migrated from applications/accounting/minilang/permissions/PermissionServices.xml:25.
     */
    private Map<String, Object> check(String permissionCheck, Map<String, Object> input) {
        String mainAction = input.get("mainAction") == null ? null : input.get("mainAction").toString();
        if (mainAction == null || mainAction.isBlank()) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Missing required parameter: mainAction");
        }
        Set<String> authorities = authorities();
        String primary = switch (permissionCheck) {
            case "commission" -> "ACCOUNTING_COMM";
            case "foreignExchange" -> "ACCOUNTING_FX";
            case "preference" -> "ACCTG_PREF";
            case "transaction" -> "ACCTG_ATX";
            default -> BASE;
        };
        String alternate = "basePlusRole".equals(permissionCheck) ? "ACCOUNTING_ROLE" : null;
        boolean allowed = hasPermission(authorities, primary, mainAction)
                || alternate != null && hasPermission(authorities, alternate, mainAction);
        if (!allowed && "foreignExchange".equals(permissionCheck)) {
            allowed = authorities.contains("ACCOUNTING_FX_ENTRY")
                    || hasPermission(authorities, "ACCOUNTING_FX_ENTRY", mainAction);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("hasPermission", allowed);
        if (!allowed) {
            result.put("failMessage", "Permission denied");
        }
        return result;
    }

    private static boolean hasPermission(Set<String> authorities, String permission, String action) {
        return authorities.contains(permission + "_" + action.toUpperCase(java.util.Locale.ROOT))
                || authorities.contains(permission + "_ADMIN");
    }

    private static Set<String> authorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null
                ? Set.of()
                : AuthorityUtils.authorityListToSet(authentication.getAuthorities());
    }
}
