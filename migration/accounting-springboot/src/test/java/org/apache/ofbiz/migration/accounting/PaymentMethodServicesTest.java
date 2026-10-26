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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = AccountingMigrationApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PaymentMethodServicesTest {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest(name = "PaymentMethod simple service {0}")
    @MethodSource("simpleServices")
    void eachPaymentMethodServiceHasANamedNormalPath(String serviceName) throws Exception {
        switch (serviceName) {
            case "setPaymentMethodAddress" -> {
                Object contactMech = entities.newEntity(entities.entityClass("ContactMech"));
                entities.set(contactMech, "contactMechId", "MIG_PM_CONTACT");
                entities.set(contactMech, "contactMechTypeId", "POSTAL_ADDRESS");
                entityManager.persist(contactMech);
                entityManager.flush();
                postService(serviceName, "{\"paymentMethodId\":\"MIG_DEMO_PM\","
                        + "\"contactMechId\":\"MIG_PM_CONTACT\"}").andExpect(status().isOk());
            }
            case "updatePaymentMethodAddress" ->
                    postService(serviceName, "{\"oldContactMechId\":\"NO_MATCHING_CONTACT\","
                            + "\"contactMechId\":\"MIG_PM_CONTACT\"}").andExpect(status().isOk());
            case "createPayPalPaymentMethod" ->
                    postService(serviceName, "{\"paymentMethodId\":\"MIG_PM_PAYPAL\","
                            + "\"partyId\":\"MIG_DEMO_CUSTOMER\",\"description\":\"PayPal test\"}")
                            .andExpect(status().isOk()).andExpect(jsonPath("$.paymentMethodId").exists());
            case "createPaymentGroupMember" -> {
                createPaymentGroupFixture();
                postService(serviceName, "{\"paymentGroupId\":\"MIG_PM_GROUP\","
                        + "\"paymentId\":\"MIG_PM_PAYMENT\"}").andExpect(status().isOk());
            }
            case "expirePaymentGroupMember" -> {
                createPaymentGroupFixture();
                postService("createPaymentGroupMember", "{\"paymentGroupId\":\"MIG_PM_GROUP\","
                        + "\"paymentId\":\"MIG_PM_PAYMENT\"}").andExpect(status().isOk());
                Object member = entities.findByEquality(entities.entityClass("PaymentGroupMember"),
                        Map.of("paymentGroupId", "MIG_PM_GROUP", "paymentId", "MIG_PM_PAYMENT")).get(0);
                postService(serviceName, "{\"paymentGroupId\":\"MIG_PM_GROUP\","
                        + "\"paymentId\":\"MIG_PM_PAYMENT\",\"fromDate\":\""
                        + entities.get(member, "fromDate") + "\"}").andExpect(status().isOk());
            }
            default -> throw new IllegalArgumentException(serviceName);
        }
    }

    private static Stream<String> simpleServices() {
        return Stream.of("setPaymentMethodAddress", "updatePaymentMethodAddress", "createPayPalPaymentMethod",
                "createPaymentGroupMember", "expirePaymentGroupMember");
    }

    private void createPaymentGroupFixture() {
        Object group = entities.newEntity(entities.entityClass("PaymentGroup"));
        entities.set(group, "paymentGroupId", "MIG_PM_GROUP");
        entities.set(group, "paymentGroupTypeId", "BATCH_PAYMENT");
        entityManager.persist(group);

        Object payment = entities.newEntity(entities.entityClass("Payment"));
        entities.set(payment, "paymentId", "MIG_PM_PAYMENT");
        entities.set(payment, "paymentTypeId", "CUSTOMER_PAYMENT");
        entities.set(payment, "paymentMethodTypeId", "CREDIT_CARD");
        entities.set(payment, "paymentMethodId", "MIG_DEMO_PM");
        entities.set(payment, "partyIdFrom", "MIG_DEMO_CUSTOMER");
        entities.set(payment, "partyIdTo", "MIG_DEMO_VENDOR");
        entities.set(payment, "statusId", "PMNT_RECEIVED");
        entities.set(payment, "currencyUomId", "USD");
        entities.set(payment, "amount", "1.00");
        entityManager.persist(payment);
        entityManager.flush();
    }

    @Test
    void handlesAddressPaypalAndPaymentGroupMemberServices() throws Exception {
        Object contactMech = entities.newEntity(entities.entityClass("ContactMech"));
        entities.set(contactMech, "contactMechId", "MIG_PM_CONTACT");
        entities.set(contactMech, "contactMechTypeId", "POSTAL_ADDRESS");
        entityManager.persist(contactMech);
        entityManager.flush();

        postService("setPaymentMethodAddress", "{\"paymentMethodId\":\"MIG_DEMO_PM\","
                + "\"contactMechId\":\"MIG_PM_CONTACT\"}").andExpect(status().isOk());
        Object creditCard = entities.findByPrimaryKey(
                entities.entityClass("CreditCard"), Map.of("paymentMethodId", "MIG_DEMO_PM"));
        assertEquals("MIG_PM_CONTACT", entities.get(creditCard, "contactMechId"));

        postService("updatePaymentMethodAddress", "{\"oldContactMechId\":\"NO_MATCHING_CONTACT\","
                + "\"contactMechId\":\"MIG_PM_CONTACT\"}").andExpect(status().isOk());

        postService("createPayPalPaymentMethod", "{\"paymentMethodId\":\"MIG_PAYPAL_TEST\","
                + "\"partyId\":\"MIG_DEMO_CUSTOMER\",\"description\":\"PayPal test\"}")
                .andExpect(status().isOk()).andExpect(jsonPath("$.paymentMethodId").value("MIG_PAYPAL_TEST"));
        assertFalse(entities.findByEquality(
                entities.entityClass("PayPalPaymentMethod"), Map.of("paymentMethodId", "MIG_PAYPAL_TEST")).isEmpty());

        Object group = entities.newEntity(entities.entityClass("PaymentGroup"));
        entities.set(group, "paymentGroupId", "MIG_PM_GROUP");
        entities.set(group, "paymentGroupTypeId", "BATCH_PAYMENT");
        entityManager.persist(group);

        Object payment = entities.newEntity(entities.entityClass("Payment"));
        entities.set(payment, "paymentId", "MIG_PM_PAYMENT");
        entities.set(payment, "paymentTypeId", "CUSTOMER_PAYMENT");
        entities.set(payment, "paymentMethodTypeId", "CREDIT_CARD");
        entities.set(payment, "paymentMethodId", "MIG_DEMO_PM");
        entities.set(payment, "partyIdFrom", "MIG_DEMO_CUSTOMER");
        entities.set(payment, "partyIdTo", "MIG_DEMO_VENDOR");
        entities.set(payment, "statusId", "PMNT_RECEIVED");
        entities.set(payment, "currencyUomId", "USD");
        entities.set(payment, "amount", "1.00");
        entityManager.persist(payment);
        entityManager.flush();

        postService("createPaymentGroupMember", "{\"paymentGroupId\":\"MIG_PM_GROUP\","
                + "\"paymentId\":\"MIG_PM_PAYMENT\"}").andExpect(status().isOk());
        List<?> members = entities.findByEquality(entities.entityClass("PaymentGroupMember"),
                Map.of("paymentGroupId", "MIG_PM_GROUP", "paymentId", "MIG_PM_PAYMENT"));
        assertEquals(1, members.size());
        String fromDate = entities.get(members.get(0), "fromDate").toString();
        postService("expirePaymentGroupMember", "{\"paymentGroupId\":\"MIG_PM_GROUP\","
                + "\"paymentId\":\"MIG_PM_PAYMENT\",\"fromDate\":\"" + fromDate + "\"}")
                .andExpect(status().isOk());
        Object expired = entities.findByEquality(entities.entityClass("PaymentGroupMember"),
                Map.of("paymentGroupId", "MIG_PM_GROUP", "paymentId", "MIG_PM_PAYMENT")).get(0);
        org.junit.jupiter.api.Assertions.assertNotNull(entities.get(expired, "thruDate"));
    }

    @Test
    void createPaymentApplicationDefaultsToTheRemainingPaymentBalance() throws Exception {
        Object billingAccount = entities.newEntity(entities.entityClass("BillingAccount"));
        entities.set(billingAccount, "billingAccountId", "MIG_PM_BILLING");
        entities.set(billingAccount, "accountCurrencyUomId", "USD");
        entityManager.persist(billingAccount);

        Object payment = entities.newEntity(entities.entityClass("Payment"));
        entities.set(payment, "paymentId", "MIG_PM_DEFAULT_APP");
        entities.set(payment, "paymentTypeId", "CUSTOMER_PAYMENT");
        entities.set(payment, "paymentMethodTypeId", "CREDIT_CARD");
        entities.set(payment, "paymentMethodId", "MIG_DEMO_PM");
        entities.set(payment, "partyIdFrom", "MIG_DEMO_CUSTOMER");
        entities.set(payment, "partyIdTo", "MIG_DEMO_VENDOR");
        entities.set(payment, "statusId", "PMNT_RECEIVED");
        entities.set(payment, "currencyUomId", "USD");
        entities.set(payment, "amount", "12.34");
        entityManager.persist(payment);
        entityManager.flush();

        postService("createPaymentApplication", "{\"paymentId\":\"MIG_PM_DEFAULT_APP\","
                + "\"billingAccountId\":\"MIG_PM_BILLING\",\"amountApplied\":\"2.34\"}")
                .andExpect(status().isOk());
        postService("createPaymentApplication", "{\"paymentId\":\"MIG_PM_DEFAULT_APP\","
                + "\"billingAccountId\":\"MIG_PM_BILLING\"}")
                .andExpect(status().isOk()).andExpect(jsonPath("$.amountApplied").value(10.00));
    }

    private org.springframework.test.web.servlet.ResultActions postService(String service, String body)
            throws Exception {
        return mockMvc.perform(post("/api/accounting/services/" + service)
                .with(httpBasic("acct-admin", "test"))
                .contentType(MediaType.APPLICATION_JSON).content(body));
    }
}
