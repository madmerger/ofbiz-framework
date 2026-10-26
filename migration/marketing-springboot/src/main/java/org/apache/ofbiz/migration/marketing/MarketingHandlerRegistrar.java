package org.apache.ofbiz.migration.marketing;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.apache.ofbiz.migration.marketing.service.ContactListServices;
import org.apache.ofbiz.migration.marketing.service.MarketingJavaServices;
import org.apache.ofbiz.migration.marketing.service.MarketingMiscServices;
import org.apache.ofbiz.migration.marketing.service.SfaServices;
import org.springframework.stereotype.Component;

/**
 * Registers the hand-ported simple (minilang) services and the main-flow Java services with their permissions.
 */
@Component
public class MarketingHandlerRegistrar {

    private final Map<String, String> simplePermissions = new LinkedHashMap<>();
    private final Map<String, String> javaPermissions = new LinkedHashMap<>();

    public MarketingHandlerRegistrar(ServiceHandlerRegistry registry, ContactListServices contactLists,
            MarketingMiscServices misc, SfaServices sfa, MarketingJavaServices java) {
        Registrar simple = (name, permission, handler) -> {
            simplePermissions.put(name, permission);
            registry.registerHandler(MarketingCatalogRegistrar.COMPONENT, name, permission, handler);
        };
        simple.add("createContactListParty", "CREATE", contactLists::createContactListParty);
        simple.add("updateContactListParty", "UPDATE", contactLists::updateContactListParty);
        simple.add("updateContactListPartyNoUserLogin", "UPDATE", contactLists::updateContactListPartyNoUserLogin);
        simple.add("unsubscribeContactListParty", "UPDATE", contactLists::unsubscribeContactListParty);
        simple.add("unsubscribeContactListPartyContachMech", "UPDATE",
                contactLists::unsubscribeContactListPartyContachMech);
        simple.add("updatePartyEmailContactListParty", "UPDATE", contactLists::updatePartyEmailContactListParty);
        simple.add("createContactListPartyStatus", "CREATE", contactLists::createContactListPartyStatus);
        simple.add("sendContactListPartyVerifyEmail", "UPDATE", contactLists::sendContactListPartyVerifyEmail);
        simple.add("sendContactListPartySubscribeEmail", "UPDATE", contactLists::sendContactListPartySubscribeEmail);
        simple.add("sendContactListPartyUnSubscribeVerifyEmail", "UPDATE",
                contactLists::sendContactListPartyUnSubscribeVerifyEmail);
        simple.add("sendContactListPartyUnSubscribeEmail", "UPDATE", contactLists::sendContactListPartyUnSubscribeEmail);
        simple.add("optOutOfListFromCommEvent", "UPDATE", misc::optOutOfListFromCommEvent);
        simple.add("updateContactListCommStatus", "UPDATE", misc::updateContactListCommStatus);
        simple.add("updateCommStatusFromCommEvent", "UPDATE", misc::updateCommStatusFromCommEvent);
        simple.add("createWebSiteContactList", "CREATE", misc::createWebSiteContactList);
        simple.add("updateWebSiteContactList", "UPDATE", misc::updateWebSiteContactList);
        simple.add("deleteWebSiteContactList", "DELETE", misc::deleteWebSiteContactList);
        simple.add("arePartiesRelatedThroughSegmentGroup", "VIEW", misc::arePartiesRelatedThroughSegmentGroup);
        simple.add("createTrackingCodeOrderReturns", "CREATE", misc::createTrackingCodeOrderReturns);
        simple.add("marketingPermissionService", "VIEW", misc::marketingPermissionService);
        simple.add("createContact", "CREATE", sfa::createContact);
        simple.add("mergeContacts", "UPDATE", sfa::mergeContacts);
        simple.add("createSalesForecast", "CREATE", sfa::createSalesForecast);
        simple.add("updateSalesForecast", "UPDATE", sfa::updateSalesForecast);
        simple.add("createSalesOpportunityAccountRole", "CREATE", sfa::createSalesOpportunityAccountRole);
        simple.add("createSalesOpportunityLeadRole", "CREATE", sfa::createSalesOpportunityLeadRole);
        simple.add("findPartyInSalesOpportunityRole", "VIEW", sfa::findPartyInSalesOpportunityRole);

        Registrar main = (name, permission, handler) -> {
            javaPermissions.put(name, permission);
            registry.registerHandler(MarketingCatalogRegistrar.COMPONENT, name, permission, handler);
        };
        main.add("signUpForContactList", "CREATE", java::signUpForContactList);
        main.add("deleteContactListParty", "DELETE", java::deleteContactListParty);
    }

    public Map<String, String> simplePermissions() {
        return Collections.unmodifiableMap(simplePermissions);
    }

    public Map<String, String> javaPermissions() {
        return Collections.unmodifiableMap(javaPermissions);
    }

    @FunctionalInterface
    private interface Registrar {
        void add(String name, String permission, ServiceHandlerRegistry.SimpleServiceHandler handler);
    }
}
