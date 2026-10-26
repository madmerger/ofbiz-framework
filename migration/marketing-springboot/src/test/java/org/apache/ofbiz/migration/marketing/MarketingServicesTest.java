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
package org.apache.ofbiz.migration.marketing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.EntityManager;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.datamodel.entity.CommunicationEvent;
import org.apache.ofbiz.migration.datamodel.entity.ContactListCommStatus;
import org.apache.ofbiz.migration.datamodel.entity.ContactListParty;
import org.apache.ofbiz.migration.datamodel.entity.ContactListPartyStatus;
import org.apache.ofbiz.migration.datamodel.entity.Party;
import org.apache.ofbiz.migration.datamodel.entity.PartyContactMechPurpose;
import org.apache.ofbiz.migration.datamodel.entity.PartyRelationship;
import org.apache.ofbiz.migration.datamodel.entity.Person;
import org.apache.ofbiz.migration.datamodel.entity.ProductStoreEmailSetting;
import org.apache.ofbiz.migration.datamodel.entity.ReturnHeader;
import org.apache.ofbiz.migration.datamodel.entity.ReturnItem;
import org.apache.ofbiz.migration.datamodel.entity.SalesForecastHistory;
import org.apache.ofbiz.migration.datamodel.entity.SegmentGroupRole;
import org.apache.ofbiz.migration.datamodel.entity.TrackingCodeOrder;
import org.apache.ofbiz.migration.datamodel.entity.TrackingCodeOrderReturn;
import org.apache.ofbiz.migration.datamodel.entity.UserLogin;
import org.apache.ofbiz.migration.datamodel.entity.WebSite;
import org.apache.ofbiz.migration.marketing.adapter.LocalMailStubAdapter;
import org.apache.ofbiz.migration.marketing.port.MailPort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(classes = MarketingMigrationApplication.class,
        properties = MarketingTestSupport.TEST_PROPERTIES)
class MarketingServicesTest {

    @Autowired
    private MigrationServiceDispatcher dispatcher;
    @Autowired
    private MarketingHandlerRegistrar handlers;
    @Autowired
    private LocalMailStubAdapter mail;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private TransactionTemplate transactions;

    @BeforeEach
    void login() {
        MarketingTestSupport.loginAdmin();
        mail.clear();
    }

    @AfterEach
    void logout() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void svc01_everySimpleServiceIsRegisteredWithPermission() throws IOException {
        Set<String> simple = new TreeSet<>();
        Set<String> java = new TreeSet<>();
        for (JsonNode service : MarketingTestSupport.inventory().get("services")) {
            String engine = service.path("engine").asText();
            if ("simple".equals(engine)) {
                simple.add(service.path("name").asText());
            } else if ("java".equals(engine) || "groovy".equals(engine)) {
                java.add(service.path("name").asText());
            }
        }
        assertEquals(27, simple.size());
        assertEquals(simple, new TreeSet<>(handlers.simplePermissions().keySet()));
        assertTrue(java.containsAll(handlers.javaPermissions().keySet()));
        assertEquals(2, handlers.javaPermissions().size());
        handlers.simplePermissions().values().forEach(permission ->
                assertTrue(Set.of("VIEW", "CREATE", "UPDATE", "DELETE").contains(permission)));
    }

    @Test
    void flow01_campaignContactListSubscribeAndUnsubscribe() {
        storeEmailSettings("FLOW_STORE", "FLOW_WS");
        String campaignId = (String) run("createMarketingCampaign", Map.of("campaignName", "新製品"))
                .get("marketingCampaignId");
        assertNotNull(campaignId);
        run("createContactList", Map.of("contactListId", "FLOW_CL", "contactListName", "ニュースレター",
                "contactMechTypeId", "EMAIL_ADDRESS", "marketingCampaignId", campaignId,
                "verifyEmailFrom", "noreply@example.com", "verifyEmailScreen", "component://marketing/verify",
                "verifyEmailSubject", "確認", "verifyEmailWebSiteId", "FLOW_WS"));

        Map<String, Object> signUp = run("signUpForContactList",
                Map.of("contactListId", "FLOW_CL", "email", "flow@example.com"));
        assertEquals("CLPT_PENDING", signUp.get("statusId"));
        assertEquals("_NA_", signUp.get("partyId"));
        assertEquals("VERIFY_CONT_LIST", mail.sentMails().get(0).emailType());
        Timestamp fromDate = (Timestamp) signUp.get("fromDate");
        String code = statuses("FLOW_CL").get(0).getOptInVerifyCode();
        assertNotNull(code);

        Map<String, Object> wrong = new HashMap<>(Map.of("contactListId", "FLOW_CL", "partyId", "_NA_",
                "fromDate", fromDate, "statusId", "CLPT_ACCEPTED", "optInVerifyCode", "0000000000x"));
        assertEquals(400, assertThrows(MigrationException.class,
                () -> run("updateContactListPartyNoUserLogin", wrong)).getStatus().value());

        Map<String, Object> accept = new HashMap<>(wrong);
        accept.put("optInVerifyCode", code);
        assertEquals("CLPT_ACCEPTED", run("updateContactListPartyNoUserLogin", accept).get("statusId"));
        assertTrue(mail.sentMails().stream().anyMatch(m -> "SUB_CONT_LIST_NOTI".equals(m.emailType())));

        Map<String, Object> unsubscribe = run("unsubscribeContactListParty",
                Map.of("contactListId", "FLOW_CL", "email", "flow@example.com"));
        assertEquals("CLPT_UNSUBS_PENDING", unsubscribe.get("statusId"));
        assertTrue(mail.sentMails().stream().anyMatch(m -> "UNSUB_CONT_LIST_VERI".equals(m.emailType())));

        Map<String, Object> done = run("updateContactListParty", Map.of("contactListId", "FLOW_CL",
                "partyId", "_NA_", "fromDate", fromDate, "statusId", "CLPT_UNSUBSCRIBED"));
        assertEquals(400, assertThrows(MigrationException.class,
                () -> run("updateContactListPartyNoUserLogin", accept)).getStatus().value());
        assertEquals("CLPT_UNSUBSCRIBED", done.get("statusId"));
        assertTrue(mail.sentMails().stream().anyMatch(m -> "UNSUB_CONT_LIST_NOTI".equals(m.emailType())));
        assertEquals(List.of("CLPT_PENDING", "CLPT_ACCEPTED", "CLPT_UNSUBS_PENDING", "CLPT_UNSUBSCRIBED"),
                statuses("FLOW_CL").stream().map(ContactListPartyStatus::getStatusId).toList());
        // OFBiz と同じく再登録ごとに新しい ContactMech を作るため、再登録で新しい PENDING が作られる
        assertEquals("CLPT_PENDING", run("signUpForContactList",
                Map.of("contactListId", "FLOW_CL", "email", "flow@example.com")).get("statusId"));
    }

    @Test
    void flow02_invalidEmailAndUnknownListAreRejected() {
        assertEquals(400, assertThrows(MigrationException.class, () -> run("signUpForContactList",
                Map.of("contactListId", "NONE", "email", "not-an-email"))).getStatus().value());
        assertEquals(404, assertThrows(MigrationException.class, () -> run("signUpForContactList",
                Map.of("contactListId", "NONE", "email", "ok@example.com"))).getStatus().value());
    }

    @Test
    void flow03_resignUpReplacesPendingAndDeleteRemovesStatuses() {
        run("createContactList", Map.of("contactListId", "RE_CL", "contactMechTypeId", "EMAIL_ADDRESS"));
        Map<String, Object> first = run("signUpForContactList",
                Map.of("contactListId", "RE_CL", "email", "re@example.com", "partyId", "RE_PARTY"));
        Map<String, Object> deleted = run("deleteContactListParty", Map.of("contactListId", "RE_CL",
                "partyId", "RE_PARTY", "fromDate", first.get("fromDate")));
        assertEquals(1, deleted.get("removedCount"));
        assertTrue(statuses("RE_CL").isEmpty());
    }

    @Test
    void simple01_contactListPartyCreateAndEmailChange() {
        run("createContactList", Map.of("contactListId", "S1_CL", "contactMechTypeId", "EMAIL_ADDRESS"));
        run("createContactList", Map.of("contactListId", "S1_TEL", "contactMechTypeId", "TELECOM_NUMBER"));
        Map<String, Object> contact = run("createContact",
                Map.of("firstName", "花子", "lastName", "山田", "emailAddress", "hanako@example.com"));
        String partyId = (String) contact.get("partyId");
        Map<String, Object> created = run("createContactListParty", Map.of("contactListId", "S1_CL",
                "partyId", partyId, "preferredContactMechId", contact.get("contactMechId"), "statusId", "CLPT_PENDING"));
        assertNotNull(created.get("fromDate"));
        MigrationException wrongType = assertThrows(MigrationException.class, () -> run("createContactListParty",
                Map.of("contactListId", "S1_TEL", "partyId", partyId,
                        "preferredContactMechId", contact.get("contactMechId"))));
        assertEquals(400, wrongType.getStatus().value());
        Map<String, Object> moved = run("updatePartyEmailContactListParty",
                Map.of("contactMechId", "NEW_CM", "oldContactMechId", contact.get("contactMechId")));
        assertEquals(1, moved.get("updatedCount"));
    }

    @Test
    void simple02_websiteContactListCommStatusAndOptOut() {
        Map<String, Object> link = run("createWebSiteContactList", Map.of("webSiteId", "WS2", "contactListId", "CL2"));
        assertEquals(409, assertThrows(MigrationException.class, () -> run("createWebSiteContactList",
                Map.of("webSiteId", "WS2", "contactListId", "CL2", "fromDate", link.get("fromDate"))))
                .getStatus().value());
        Map<String, Object> keys = Map.of("webSiteId", "WS2", "contactListId", "CL2", "fromDate", link.get("fromDate"));
        Map<String, Object> update = new HashMap<>(keys);
        update.put("thruDate", Timestamp.valueOf("2030-01-01 00:00:00"));
        assertNotNull(run("updateWebSiteContactList", update).get("thruDate"));
        run("deleteWebSiteContactList", keys);

        run("createContactList", Map.of("contactListId", "CL2", "contactMechTypeId", "EMAIL_ADDRESS"));
        Map<String, Object> signUp = run("signUpForContactList",
                Map.of("contactListId", "CL2", "email", "cl2@example.com", "partyId", "CL2_PARTY"));
        persist(event("CE_PARENT", null, "CL2", "CL2_PARTY", (String) signUp.get("preferredContactMechId")),
                event("CE_CHILD", "CE_PARENT", "CL2", "CL2_PARTY", (String) signUp.get("preferredContactMechId")));
        run("updateContactListCommStatus", Map.of("contactListId", "CL2", "communicationEventId", "CE_PARENT",
                "contactMechId", signUp.get("preferredContactMechId"), "partyId", "CL2_PARTY", "statusId", "COM_PENDING"));
        assertEquals(1, run("updateCommStatusFromCommEvent",
                Map.of("communicationEventId", "CE_CHILD", "statusId", "COM_COMPLETE")).get("updatedCount"));
        ContactListCommStatus status = entityManager.createQuery(
                "select s from ContactListCommStatus s where s.communicationEventId = 'CE_PARENT'",
                ContactListCommStatus.class).getSingleResult();
        assertEquals("COM_COMPLETE", status.getStatusId());
        Map<String, Object> optOut = run("optOutOfListFromCommEvent", Map.of("communicationEventId", "CE_PARENT"));
        assertEquals("CL2", optOut.get("contactListId"));
        assertEquals(1, optOut.get("optedOutCount"));
        ContactListParty row = entityManager.createQuery(
                "select c from ContactListParty c where c.contactListId = 'CL2'", ContactListParty.class)
                .getSingleResult();
        assertNotNull(row.getThruDate());
    }

    @Test
    void simple03_segmentTrackingAndPermission() {
        persist(segmentRole("SEG1", "P_A", "CUSTOMER"), segmentRole("SEG1", "P_B", "CUSTOMER"),
                segmentRole("SEG2", "P_C", "CUSTOMER"));
        assertEquals("Y", run("arePartiesRelatedThroughSegmentGroup",
                Map.of("partyId", "P_A", "toPartyId", "P_B")).get("areRelated"));
        assertEquals("N", run("arePartiesRelatedThroughSegmentGroup",
                Map.of("partyId", "P_A", "toPartyId", "P_C")).get("areRelated"));

        ReturnHeader header = new ReturnHeader();
        header.setReturnId("RET1");
        ReturnItem item = new ReturnItem();
        item.setReturnId("RET1");
        item.setReturnItemSeqId("00001");
        item.setOrderId("ORD1");
        item.setOrderItemSeqId("00001");
        TrackingCodeOrder order = new TrackingCodeOrder();
        order.setOrderId("ORD1");
        order.setTrackingCodeTypeId("PARTNER_MGD");
        order.setTrackingCodeId("TC1");
        persist(header, item, order);
        assertEquals(1, run("createTrackingCodeOrderReturns", Map.of("returnId", "RET1")).get("createdCount"));
        TrackingCodeOrderReturn created = entityManager.createQuery(
                "select t from TrackingCodeOrderReturn t where t.returnId = 'RET1'", TrackingCodeOrderReturn.class)
                .getSingleResult();
        assertEquals("TC1", created.getTrackingCodeId());
        assertEquals("PARTNER_MGD", created.getTrackingCodeTypeId());
        assertEquals("N", created.getHasExported());
        assertEquals(404, assertThrows(MigrationException.class,
                () -> run("createTrackingCodeOrderReturns", Map.of("returnId", "NONE"))).getStatus().value());

        assertEquals(true, run("marketingPermissionService", Map.of("mainAction", "UPDATE")).get("hasPermission"));
        MarketingTestSupport.loginAs("readonly", "OFBTOOLS_VIEW", "MARKETING_VIEW");
        assertEquals(false, run("marketingPermissionService", Map.of("mainAction", "UPDATE")).get("hasPermission"));
        assertEquals(403, assertThrows(MigrationException.class, () -> run("createTrackingCodeOrderReturns",
                Map.of("returnId", "RET1"))).getStatus().value());
    }

    @Test
    void simple04_sfaContactsForecastAndOpportunityRoles() {
        Map<String, Object> a = run("createContact", Map.of("firstName", "A", "lastName", "Merge",
                "emailAddress", "a@example.com", "partyIdFrom", "OWNER"));
        Map<String, Object> b = run("createContact", Map.of("firstName", "B", "lastName", "Merge",
                "emailAddress", "b@example.com"));
        assertEquals("CONTACT", a.get("roleTypeId"));
        transactions.executeWithoutResult(status -> {
            PartyContactMechPurpose billing = new PartyContactMechPurpose();
            billing.setPartyId((String) a.get("partyId"));
            billing.setContactMechId((String) a.get("contactMechId"));
            billing.setContactMechPurposeTypeId("BILLING_EMAIL");
            billing.setFromDate(Timestamp.from(Instant.now().minusSeconds(60)));
            entityManager.persist(billing);
        });
        Map<String, Object> merged = run("mergeContacts", Map.of("partyIdTo", a.get("partyId"),
                "partyId", b.get("partyId"), "useEmail2", "Y", "emailContactMechId", b.get("contactMechId"),
                "emailContactMechIdTo", a.get("contactMechId")));
        assertEquals(a.get("partyId"), merged.get("partyId"));
        assertEquals("PARTY_DISABLED", entityManager.find(Party.class, b.get("partyId")).getStatusId());
        List<PartyContactMechPurpose> purposes = entityManager.createQuery(
                "select p from PartyContactMechPurpose p where p.partyId = ?1", PartyContactMechPurpose.class)
                .setParameter(1, a.get("partyId")).getResultList();
        assertTrue(purposes.stream().anyMatch(p -> p.getContactMechId().equals(b.get("contactMechId"))
                && "PRIMARY_EMAIL".equals(p.getContactMechPurposeTypeId()) && p.getThruDate() == null));
        assertTrue(purposes.stream().anyMatch(p -> p.getContactMechId().equals(b.get("contactMechId"))
                && "BILLING_EMAIL".equals(p.getContactMechPurposeTypeId()) && p.getThruDate() == null));
        assertTrue(purposes.stream().filter(p -> p.getContactMechId().equals(a.get("contactMechId")))
                .allMatch(p -> p.getThruDate() != null));

        String forecastId = (String) run("createSalesForecast",
                Map.of("organizationPartyId", "ORG", "quotaAmount", "1000")).get("salesForecastId");
        run("updateSalesForecast", Map.of("salesForecastId", forecastId, "forecastAmount", "800",
                "changeNote", "下方修正"));
        List<SalesForecastHistory> history = entityManager.createQuery(
                "select h from SalesForecastHistory h where h.salesForecastId = ?1 order by h.salesForecastHistoryId",
                SalesForecastHistory.class).setParameter(1, forecastId).getResultList();
        assertEquals(2, history.size());
        assertEquals("下方修正", history.get(1).getChangeNote());

        run("createSalesOpportunityAccountRole", Map.of("salesOpportunityId", "OPP1", "accountPartyId", "ACC1"));
        run("createSalesOpportunityAccountRole", Map.of("salesOpportunityId", "OPP1", "accountPartyId", "ACC2"));
        run("createSalesOpportunityLeadRole", Map.of("salesOpportunityId", "OPP1", "leadPartyId", "LEAD1"));
        assertEquals("ACC2", run("findPartyInSalesOpportunityRole",
                Map.of("salesOpportunityId", "OPP1", "roleTypeId", "ACCOUNT")).get("partyId"));
        assertEquals("LEAD1", run("findPartyInSalesOpportunityRole",
                Map.of("salesOpportunityId", "OPP1", "roleTypeId", "LEAD")).get("partyId"));
        assertNull(run("findPartyInSalesOpportunityRole",
                Map.of("salesOpportunityId", "OPP1", "roleTypeId", "OTHER")).get("partyId"));
    }

    @Test
    void simple05_createContactRelatesToLoginPartyAndIgnoresUndeclaredPersonFields() {
        transactions.executeWithoutResult(status -> {
            UserLogin login = new UserLogin();
            login.setUserLoginId("contactowner");
            login.setPartyId("OWNER_PARTY");
            entityManager.persist(login);
        });
        MarketingTestSupport.loginAs("contactowner", "OFBTOOLS_VIEW", "MARKETING_VIEW", "MARKETING_ADMIN");
        String partyId = (String) run("createContact", Map.of("firstName", "C", "lastName", "Owner",
                "oldMaritalStatusEnumId", "IGNORED")).get("partyId");
        assertNull(entityManager.find(Person.class, partyId).getOldMaritalStatusEnumId());
        assertEquals(1, entityManager.createQuery("select r from PartyRelationship r where r.partyIdFrom = ?1"
                        + " and r.partyIdTo = ?2 and r.partyRelationshipTypeId = 'CONTACT_REL'", PartyRelationship.class)
                .setParameter(1, "OWNER_PARTY").setParameter(2, partyId).getResultList().size());
    }

    @Test
    void simple06_mailStubKeepsOnlyLatestRequests() {
        for (int i = 0; i < 150; i++) {
            mail.sendMailFromScreen(new MailPort.MailRequest("T" + i, "to@example.com", "from@example.com",
                    "s", "screen", null, null, Map.of()));
        }
        assertEquals(100, mail.sentMails().size());
        assertEquals("T149", mail.sentMails().get(99).emailType());
    }

    private Map<String, Object> run(String service, Map<String, ?> input) {
        return MarketingTestSupport.run(dispatcher, service, input);
    }

    private List<ContactListPartyStatus> statuses(String contactListId) {
        return entityManager.createQuery("select s from ContactListPartyStatus s where s.contactListId = ?1"
                        + " order by s.statusDate", ContactListPartyStatus.class)
                .setParameter(1, contactListId).getResultList();
    }

    private void persist(Object... rows) {
        transactions.executeWithoutResult(status -> {
            for (Object row : rows) {
                entityManager.persist(row);
            }
        });
    }

    private void storeEmailSettings(String storeId, String webSiteId) {
        WebSite site = new WebSite();
        site.setWebSiteId(webSiteId);
        site.setProductStoreId(storeId);
        persist(site, setting(storeId, "SUB_CONT_LIST_NOTI"), setting(storeId, "UNSUB_CONT_LIST_VERI"),
                setting(storeId, "UNSUB_CONT_LIST_NOTI"));
    }

    private static ProductStoreEmailSetting setting(String storeId, String type) {
        ProductStoreEmailSetting setting = new ProductStoreEmailSetting();
        setting.setProductStoreId(storeId);
        setting.setEmailType(type);
        setting.setBodyScreenLocation("component://marketing/" + type);
        setting.setFromAddress("noreply@example.com");
        setting.setSubject(type);
        return setting;
    }

    private static CommunicationEvent event(String id, String parent, String list, String party, String mech) {
        CommunicationEvent event = new CommunicationEvent();
        event.setCommunicationEventId(id);
        event.setParentCommEventId(parent);
        event.setContactListId(list);
        event.setPartyIdTo(party);
        event.setContactMechIdTo(mech);
        return event;
    }

    private static SegmentGroupRole segmentRole(String group, String party, String role) {
        SegmentGroupRole row = new SegmentGroupRole();
        row.setSegmentGroupId(group);
        row.setPartyId(party);
        row.setRoleTypeId(role);
        return row;
    }
}
