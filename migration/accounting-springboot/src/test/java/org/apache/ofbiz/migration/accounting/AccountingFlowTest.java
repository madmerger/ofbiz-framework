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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(classes = AccountingMigrationApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountingFlowTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void orderInvoicePaymentAndApplicationFlow() throws Exception {
        MvcResult create = postService("createInvoiceForOrder", "{\"orderId\":\"MIG_DEMO_ORDER\"}")
                .andExpect(status().isOk()).andReturn();
        String invoiceId = json(create, "$.invoiceId");
        postService("setInvoiceStatus", "{\"invoiceId\":\"" + invoiceId
                + "\",\"statusId\":\"INVOICE_APPROVED\"}").andExpect(status().isOk());
        MvcResult payment = postService("createPayment", "{\"paymentTypeId\":\"CUSTOMER_PAYMENT\","
                + "\"paymentMethodTypeId\":\"CREDIT_CARD\",\"paymentMethodId\":\"MIG_DEMO_PM\","
                + "\"partyIdFrom\":\"MIG_DEMO_CUSTOMER\",\"partyIdTo\":\"MIG_DEMO_VENDOR\","
                + "\"currencyUomId\":\"USD\",\"amount\":\"30.00\",\"statusId\":\"PMNT_RECEIVED\"}")
                .andExpect(status().isOk()).andReturn();
        String paymentId = json(payment, "$.paymentId");
        postService("createPaymentApplication", "{\"invoiceId\":\"" + invoiceId
                + "\",\"paymentId\":\"" + paymentId + "\"}").andExpect(status().isOk());
        postService("getInvoiceNotApplied", "{\"invoiceId\":\"" + invoiceId + "\"}")
                .andExpect(status().isOk()).andExpect(jsonPath("$.notApplied").value(0.00));
        postService("setInvoiceStatus", "{\"invoiceId\":\"" + invoiceId
                + "\",\"statusId\":\"INVOICE_READY\"}").andExpect(status().isOk());
        mockMvc.perform(get("/api/accounting/entities/Invoice/one?invoiceId=" + invoiceId)
                        .with(httpBasic("acct-clerk", "test")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.statusId").value("INVOICE_PAID"));
        postService("createInvoiceForOrder", "{\"orderId\":\"MIG_DEMO_ORDER\"}")
                .andExpect(status().isOk()).andExpect(jsonPath("$.invoiceId").doesNotExist());
    }

    @Test
    void invalidStatusOverapplicationAndMultiRollbackAreReported() throws Exception {
        MvcResult created = postService("createInvoice", "{\"invoiceTypeId\":\"SALES_INVOICE\","
                + "\"partyId\":\"MIG_DEMO_CUSTOMER\",\"partyIdFrom\":\"MIG_DEMO_VENDOR\"}")
                .andExpect(status().isOk()).andReturn();
        String invoiceId = json(created, "$.invoiceId");
        postService("setInvoiceStatus", "{\"invoiceId\":\"" + invoiceId
                + "\",\"statusId\":\"INVOICE_PAID\"}").andExpect(status().isBadRequest());
        postService("createInvoiceItem", "{\"invoiceId\":\"" + invoiceId
                + "\",\"amount\":\"1.00\"}").andExpect(status().isOk());
        MvcResult payment = postService("createPayment", "{\"paymentTypeId\":\"CUSTOMER_PAYMENT\","
                + "\"paymentMethodTypeId\":\"CREDIT_CARD\",\"paymentMethodId\":\"MIG_DEMO_PM\","
                + "\"partyIdFrom\":\"MIG_DEMO_CUSTOMER\",\"partyIdTo\":\"MIG_DEMO_VENDOR\","
                + "\"currencyUomId\":\"USD\",\"amount\":\"1.50\",\"statusId\":\"PMNT_RECEIVED\"}")
                .andExpect(status().isOk()).andReturn();
        postService("createPaymentApplication", "{\"invoiceId\":\"" + invoiceId
                + "\",\"paymentId\":\"" + json(payment, "$.paymentId")
                + "\",\"amountApplied\":\"1.01\"}").andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/accounting/services/createInvoice/multi")
                        .with(httpBasic("acct-clerk", "test"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"invoiceId\":\"ROLLBACK_A\",\"invoiceTypeId\":\"SALES_INVOICE\"},"
                                + "{\"invoiceId\":\"ROLLBACK_B\",\"invoiceTypeId\":\"SALES_INVOICE\"},"
                                + "{\"invoiceId\":\"" + invoiceId + "\",\"invoiceTypeId\":\"SALES_INVOICE\"}]"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.errors").isArray());
        mockMvc.perform(get("/api/accounting/entities/Invoice/one?invoiceId=ROLLBACK_A")
                        .with(httpBasic("acct-clerk", "test")))
                .andExpect(status().isNotFound());
    }

    private org.springframework.test.web.servlet.ResultActions postService(String name, String body)
            throws Exception {
        return mockMvc.perform(post("/api/accounting/services/" + name)
                .with(httpBasic("acct-clerk", "test"))
                .contentType(MediaType.APPLICATION_JSON).content(body));
    }

    private static String json(MvcResult result, String expression) throws Exception {
        return com.jayway.jsonpath.JsonPath.read(result.getResponse().getContentAsString(), expression);
    }
}
