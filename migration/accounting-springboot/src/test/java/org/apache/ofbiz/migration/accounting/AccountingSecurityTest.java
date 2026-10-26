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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = AccountingMigrationApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountingSecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void authenticationAndBasePermissionsAreEnforced() throws Exception {
        mockMvc.perform(get("/api/accounting/entities/Invoice"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/accounting/entities/Invoice")
                        .with(httpBasic("acct-nobase", "test")))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/accounting/services/createPaymentGatewayConfig")
                        .with(httpBasic("acct-viewer", "test"))
                        .contentType("application/json")
                        .content("{\"paymentGatewayConfigId\":\"MIG_VIEWER_CONFIG\"}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/accounting/entities/PaymentGatewayConfig/one"
                        + "?paymentGatewayConfigId=MIG_VIEWER_CONFIG")
                        .with(httpBasic("acct-admin", "test")))
                .andExpect(status().isNotFound());
    }

    @Test
    void permissionServicesAreRegisteredAndReflectTheCallersAuthorities() throws Exception {
        List<String> permissionServices = List.of(
                "acctgAgreementPermissionCheck", "acctgBasePermissionCheck", "acctgBillingAcctCheck",
                "acctgCommissionPermissionCheck", "acctgCostPermissionCheck", "acctgFinAcctPermissionCheck",
                "acctgFxPermissionCheck", "acctgInvoicePermissionCheck", "acctgPrefPermissionCheck",
                "acctgTransactionPermissionCheck", "basicGeneralLedgerPermissionCheck",
                "fixedAssetPermissionCheck", "acctgPaymentPermissionCheck");
        for (String service : permissionServices) {
            mockMvc.perform(post("/api/accounting/services/" + service)
                            .with(httpBasic("acct-viewer", "test"))
                            .contentType("application/json")
                            .content("{\"mainAction\":\"VIEW\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.hasPermission").exists());
        }
        mockMvc.perform(post("/api/accounting/services/acctgBasePermissionCheck")
                        .with(httpBasic("acct-viewer", "test"))
                        .contentType("application/json").content("{\"mainAction\":\"VIEW\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.hasPermission").value(true));
        mockMvc.perform(post("/api/accounting/services/acctgCommissionPermissionCheck")
                        .with(httpBasic("acct-viewer", "test"))
                        .contentType("application/json").content("{\"mainAction\":\"VIEW\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.hasPermission").value(false))
                .andExpect(jsonPath("$.failMessage").exists());
    }

    @Test
    void sensitiveEntitiesRequireSensitiveAuthorityAndMaskCardNumber() throws Exception {
        String paymentMethod = "/api/accounting/entities/PaymentMethod/one?paymentMethodId=MIG_DEMO_PM";
        String creditCard = "/api/accounting/entities/CreditCard/one?paymentMethodId=MIG_DEMO_PM";
        mockMvc.perform(get(paymentMethod).with(httpBasic("acct-viewer", "test")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/accounting/entities/PaymentGatewayConfig/one"
                        + "?paymentGatewayConfigId=MIG_DENIED_CONFIG")
                        .with(httpBasic("acct-admin", "test")))
                .andExpect(status().isNotFound());
        mockMvc.perform(get(creditCard).with(httpBasic("acct-viewer", "test")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get(paymentMethod).with(httpBasic("acct-sensitive", "test")))
                .andExpect(status().isOk());
        mockMvc.perform(get(creditCard).with(httpBasic("acct-admin", "test")))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .jsonPath("$.cardNumber").value("[REDACTED]"));
        mockMvc.perform(post("/api/accounting/services/createPaymentGatewayConfig")
                        .with(httpBasic("acct-clerk", "test"))
                        .contentType("application/json")
                        .content("{\"paymentGatewayConfigId\":\"MIG_DENIED_CONFIG\","
                                + "\"paymentGatewayConfigTypeId\":\"PAYMENT_GATEWAY_CONFIG\"}"))
                .andExpect(status().isForbidden());
    }
}
