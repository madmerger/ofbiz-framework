# Dependency inventory

## Called services and simple methods

| Kind | Name | Defined by | Definition | Source | Line |
| --- | --- | --- | --- | --- | --- |
| call-service | createContactListParty | marketing | applications/marketing/servicedef/services.xml | applications/marketing/minilang/marketing/contact/ContactListEvents.xml | 54 |
| call-simple-method | inlineCheckContactListMechType | unresolved | — | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 74 |
| call-simple-method | inlineCheckContactListStatusParameter | unresolved | — | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 75 |
| call-service | getPartyEmail | party | applications/party/servicedef/services.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 84 |
| call-service | createContactListPartyStatus | marketing | applications/marketing/servicedef/services.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 99 |
| call-simple-method | inlineCheckContactListMechType | unresolved | — | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 142 |
| call-simple-method | inlineCheckContactListStatusParameter | unresolved | — | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 143 |
| call-service | createContactListPartyStatus | marketing | applications/marketing/servicedef/services.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 150 |
| call-service | updateContactListParty | marketing | applications/marketing/servicedef/services.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 173 |
| call-service | updateContactListParty | marketing | applications/marketing/servicedef/services.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 214 |
| call-service | updateContactListParty | marketing | applications/marketing/servicedef/services.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 229 |
| call-service | unsubscribeContactListParty | marketing | applications/marketing/servicedef/services.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 250 |
| call-service | sendMailFromScreen | common | framework/common/servicedef/services_email.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 390 |
| call-service | updateContactListCommStatus | marketing | applications/marketing/servicedef/services.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 489 |
| call-service | sendMailFromScreen | common | framework/common/servicedef/services_email.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 541 |
| call-service | createCommunicationEvent | party | applications/party/servicedef/services.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 599 |
| call-service | sendMailFromScreen | common | framework/common/servicedef/services_email.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 620 |
| call-service | sendMailFromScreen | common | framework/common/servicedef/services_email.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 650 |
| call-service | createTrackingCodeOrderReturn | marketing | applications/marketing/servicedef/services.xml | applications/marketing/minilang/marketing/tracking/TrackingCodeServices.xml | 45 |
| call-simple-method | createPersonRoleAndContactMechs | party | component://party/minilang/party/PartySimpleMethods.xml | applications/marketing/minilang/sfa/contact/ContactServices.xml | 26 |
| call-service | createPerson | party | applications/party/servicedef/services.xml | applications/marketing/minilang/sfa/contact/ContactServices.xml | 30 |
| call-service | createPartyRole | party | applications/party/servicedef/services.xml | applications/marketing/minilang/sfa/contact/ContactServices.xml | 37 |
| call-service | createPartyEmailAddress | party | applications/party/servicedef/services.xml | applications/marketing/minilang/sfa/contact/ContactServices.xml | 50 |
| call-service | createPartyRelationship | party | applications/party/servicedef/services.xml | applications/marketing/minilang/sfa/contact/ContactServices.xml | 64 |
| call-service | updatePartyContactMech | party | applications/party/servicedef/services.xml | applications/marketing/minilang/sfa/contact/ContactServices.xml | 88 |
| call-service | deletePartyContactMech | party | applications/party/servicedef/services.xml | applications/marketing/minilang/sfa/contact/ContactServices.xml | 93 |
| call-service | updatePartyContactMech | party | applications/party/servicedef/services.xml | applications/marketing/minilang/sfa/contact/ContactServices.xml | 105 |
| call-service | deletePartyContactMech | party | applications/party/servicedef/services.xml | applications/marketing/minilang/sfa/contact/ContactServices.xml | 110 |
| call-service | updatePartyContactMech | party | applications/party/servicedef/services.xml | applications/marketing/minilang/sfa/contact/ContactServices.xml | 122 |
| call-service | deletePartyContactMech | party | applications/party/servicedef/services.xml | applications/marketing/minilang/sfa/contact/ContactServices.xml | 126 |
| call-service | setPartyStatus | party | applications/party/servicedef/services.xml | applications/marketing/minilang/sfa/contact/ContactServices.xml | 132 |
| runSync | createPartyEmailAddress | party | applications/party/servicedef/services.xml | applications/marketing/src/main/java/org/apache/ofbiz/marketing/marketing/MarketingServices.java | 98 |
| runSync | deleteContactListParty | marketing | applications/marketing/servicedef/services.xml | applications/marketing/src/main/java/org/apache/ofbiz/marketing/marketing/MarketingServices.java | 128 |
| runSync | createContactListParty | marketing | applications/marketing/servicedef/services.xml | applications/marketing/src/main/java/org/apache/ofbiz/marketing/marketing/MarketingServices.java | 142 |
| runSync | createPartyIdentification | party | applications/party/servicedef/services.xml | applications/marketing/src/main/java/org/apache/ofbiz/sfa/vcard/VCard.java | 220 |
| runSync | createContactList | marketing | applications/marketing/servicedef/services.xml | applications/marketing/src/test/groovy/org/apache/ofbiz/marketing/marketing/test/MarketingTests.groovy | 64 |
| runSync | updateContactList | marketing | applications/marketing/servicedef/services.xml | applications/marketing/src/test/groovy/org/apache/ofbiz/marketing/marketing/test/MarketingTests.groovy | 80 |

## External entity references

| Entity | Owning component | Owning entitymodel | Source | Line |
| --- | --- | --- | --- | --- |
| CommunicationEvent | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 435 |
| CommunicationEvent | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 479 |
| CommunicationEvent | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListScreens.xml | 252 |
| CommunicationEvent | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListScreens.xml | 425 |
| CommunicationEvent | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListScreens.xml | 502 |
| CommunicationEvent | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/TrackingCodeScreens.xml | 232 |
| CommunicationEvent | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/CommonScreens.xml | 710 |
| CommunicationEvent | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/OpportunityScreens.xml | 68 |
| CommunicationEventAndRole | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/OpportunityScreens.xml | 138 |
| CommunicationEventType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 328 |
| CommunicationEventType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 682 |
| CommunicationEventType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListScreens.xml | 249 |
| CommunicationEventType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ReportForms.xml | 153 |
| ContactMech | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 27 |
| ContactMech | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 521 |
| ContactMech | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 587 |
| ContactMech | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 636 |
| ContactMech | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 619 |
| ContactMech | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListScreens.xml | 427 |
| ContactMechDetail | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 226 |
| ContactMechType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 31 |
| ContactMechType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 34 |
| ContactMechType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 52 |
| ContactMechType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 98 |
| ContactMechType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 134 |
| ContactMechType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 356 |
| ContactMechType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 361 |
| ContactMechType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 366 |
| ContactMechType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 477 |
| ContactMechType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 499 |
| ContactMechType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 529 |
| ContactMechType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 547 |
| ContactMechType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 581 |
| ContactMechType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 599 |
| ContactMechType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/TrackingCodeForms.xml | 283 |
| ContactMechType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/forms/AccountForms.xml | 79 |
| ContactMechType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/forms/AccountForms.xml | 243 |
| DataSource | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/DataSourceForms.xml | 48 |
| DataSource | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/DataSourceScreens.xml | 30 |
| DataSource | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/DataSourceScreens.xml | 78 |
| DataSource | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/sfa/forms/LeadForms.xml | 63 |
| DataSource | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/sfa/forms/LeadForms.xml | 140 |
| DataSource | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/sfa/forms/LeadForms.xml | 152 |
| DataSource | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/sfa/forms/OpportunityForms.xml | 141 |
| DataSource | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/sfa/forms/OpportunityForms.xml | 149 |
| DataSourceType | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/DataSourceForms.xml | 33 |
| DataSourceType | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/DataSourceForms.xml | 56 |
| DataSourceType | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/DataSourceForms.xml | 115 |
| DataSourceType | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/DataSourceForms.xml | 136 |
| DataSourceType | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/DataSourceScreens.xml | 136 |
| Enumeration | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/TrackingCodeForms.xml | 163 |
| Enumeration | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/TrackingCodeForms.xml | 179 |
| Enumeration | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/TrackingCodeForms.xml | 204 |
| Enumeration | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/sfa/forms/OpportunityForms.xml | 38 |
| Enumeration | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/sfa/forms/OpportunityForms.xml | 133 |
| Geo | common | framework/common/entitydef/entitymodel.xml | applications/marketing/src/main/java/org/apache/ofbiz/sfa/vcard/VCard.java | 132 |
| Geo | common | framework/common/entitydef/entitymodel.xml | applications/marketing/src/main/java/org/apache/ofbiz/sfa/vcard/VCard.java | 139 |
| Geo | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 314 |
| Geo | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/SegmentForms.xml | 125 |
| Geo | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/sfa/AccountScreens.xml | 140 |
| Geo | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/sfa/AccountScreens.xml | 148 |
| Geo | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/sfa/forms/AccountForms.xml | 43 |
| Geo | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/sfa/forms/AccountForms.xml | 55 |
| Geo | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/sfa/forms/AccountForms.xml | 166 |
| Geo | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/sfa/forms/ContactForms.xml | 123 |
| Geo | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/sfa/forms/ContactForms.xml | 132 |
| Geo | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/sfa/forms/LeadForms.xml | 38 |
| Geo | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/sfa/forms/LeadForms.xml | 47 |
| MimeType | datamodel | applications/datamodel/entitydef/content-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 404 |
| OrderHeader | datamodel | applications/datamodel/entitydef/order-entitymodel.xml | applications/marketing/widget/TrackingCodeForms.xml | 149 |
| Party | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/TrackingCodeForms.xml | 278 |
| Party | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/CommonScreens.xml | 136 |
| Party | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/OpportunityScreens.xml | 134 |
| Party | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/forms/AccountForms.xml | 108 |
| Party | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/forms/CommonForms.xml | 26 |
| PartyAndContactMech | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 591 |
| PartyAndContactMech | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/forms/AccountForms.xml | 110 |
| PartyAndContactMech | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/forms/AccountForms.xml | 114 |
| PartyAndContactMech | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/forms/AccountForms.xml | 119 |
| PartyAndContactMech | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/forms/CommonForms.xml | 28 |
| PartyAndContactMech | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/forms/CommonForms.xml | 32 |
| PartyAndContactMech | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/forms/CommonForms.xml | 37 |
| PartyContactDetailByPurpose | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/src/main/java/org/apache/ofbiz/marketing/marketing/MarketingServices.java | 83 |
| PartyContactWithPurpose | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 198 |
| PartyContactWithPurpose | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 216 |
| PartyContactWithPurpose | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 241 |
| PartyDataSource | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/LeadScreens.xml | 207 |
| PartyGroup | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/CommonScreens.xml | 138 |
| PartyIdentification | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/src/main/java/org/apache/ofbiz/sfa/vcard/VCard.java | 99 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 216 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 300 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 613 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/MarketingCampaignForms.xml | 94 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/MarketingCampaignForms.xml | 102 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/SegmentForms.xml | 163 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/forms/EventForms.xml | 28 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/forms/EventForms.xml | 55 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/forms/LeadForms.xml | 81 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/forms/LeadForms.xml | 84 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/forms/OpportunityForms.xml | 77 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/forms/OpportunityForms.xml | 167 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/forms/OpportunityForms.xml | 168 |
| PartyRelationship | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/CommonScreens.xml | 795 |
| PartyRelationship | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/OpportunityScreens.xml | 142 |
| PartyRelationship | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/forms/AccountForms.xml | 124 |
| PartyRelationship | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/forms/AccountForms.xml | 131 |
| PartyRelationshipAndDetail | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/OpportunityScreens.xml | 69 |
| PartyRole | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/forms/LeadForms.xml | 88 |
| PartyType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 255 |
| Person | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/src/main/java/org/apache/ofbiz/sfa/vcard/VCard.java | 244 |
| Person | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 471 |
| Person | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 474 |
| Person | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 575 |
| Person | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 578 |
| Person | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/CommonScreens.xml | 137 |
| ProdCatalog | datamodel | applications/datamodel/entitydef/product-entitymodel.xml | applications/marketing/widget/TrackingCodeForms.xml | 49 |
| ProdCatalog | datamodel | applications/datamodel/entitydef/product-entitymodel.xml | applications/marketing/widget/TrackingCodeForms.xml | 83 |
| ProdCatalog | datamodel | applications/datamodel/entitydef/product-entitymodel.xml | applications/marketing/widget/TrackingCodeForms.xml | 222 |
| ProdCatalog | datamodel | applications/datamodel/entitydef/product-entitymodel.xml | applications/marketing/widget/TrackingCodeForms.xml | 251 |
| ProductStore | datamodel | applications/datamodel/entitydef/product-entitymodel.xml | applications/marketing/widget/SegmentForms.xml | 67 |
| ProductStoreEmailSetting | datamodel | applications/datamodel/entitydef/product-entitymodel.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 517 |
| ProductStoreEmailSetting | datamodel | applications/datamodel/entitydef/product-entitymodel.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 583 |
| ProductStoreEmailSetting | datamodel | applications/datamodel/entitydef/product-entitymodel.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 632 |
| ReturnHeader | datamodel | applications/datamodel/entitydef/order-entitymodel.xml | applications/marketing/minilang/marketing/tracking/TrackingCodeServices.xml | 26 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 263 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 372 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 379 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 478 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 479 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 552 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 582 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 583 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/MarketingCampaignForms.xml | 110 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/MarketingCampaignForms.xml | 136 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/SegmentForms.xml | 143 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/SegmentForms.xml | 169 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/TrackingCodeForms.xml | 284 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/marketing/widget/sfa/forms/OpportunityForms.xml | 84 |
| SegmentGroupViewRelatedPartiesClass | unresolved | unresolved | applications/marketing/minilang/marketing/segment/SegmentServices.xml | 45 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 158 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 185 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 224 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 339 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 444 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 476 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 540 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 580 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 621 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 683 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/ContactListScreens.xml | 254 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/MarketingCampaignForms.xml | 45 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/MarketingCampaignForms.xml | 78 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/MarketingCampaignForms.xml | 158 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/ReportForms.xml | 82 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/ReportForms.xml | 89 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/ReportForms.xml | 118 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/ReportForms.xml | 158 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/ReportForms.xml | 162 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/ReportForms.xml | 172 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/sfa/forms/LeadForms.xml | 201 |
| StatusValidChangeToDetail | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 347 |
| Uom | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/LookupForms.xml | 76 |
| Uom | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/MarketingCampaignForms.xml | 53 |
| Uom | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/MarketingCampaignForms.xml | 151 |
| Uom | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/sfa/forms/ForecastForms.xml | 71 |
| Uom | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/sfa/forms/OpportunityForms.xml | 116 |
| UomAndType | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/sfa/forms/ForecastForms.xml | 95 |
| UomAndType | common | framework/common/entitydef/entitymodel.xml | applications/marketing/widget/sfa/forms/ForecastForms.xml | 121 |
| UserLogin | security | framework/security/entitydef/entitymodel.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 168 |
| UserLogin | security | framework/security/entitydef/entitymodel.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 192 |
| UserLogin | security | framework/security/entitydef/entitymodel.xml | applications/marketing/src/main/java/org/apache/ofbiz/marketing/marketing/MarketingServices.java | 78 |
| UserLogin | security | framework/security/entitydef/entitymodel.xml | applications/marketing/widget/sfa/forms/EventForms.xml | 25 |
| Visitor | webapp | framework/webapp/entitydef/entitymodel.xml | applications/marketing/widget/TrackingCodeForms.xml | 281 |
| WebSite | webapp | framework/webapp/entitydef/entitymodel.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 512 |
| WebSite | webapp | framework/webapp/entitydef/entitymodel.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 578 |
| WebSite | webapp | framework/webapp/entitydef/entitymodel.xml | applications/marketing/minilang/marketing/contact/ContactListServices.xml | 627 |
| WebSite | webapp | framework/webapp/entitydef/entitymodel.xml | applications/marketing/src/main/java/org/apache/ofbiz/marketing/tracking/TrackingCodeEvents.java | 223 |
| WebSite | webapp | framework/webapp/entitydef/entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 72 |
| WebSite | webapp | framework/webapp/entitydef/entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 633 |
| WebSite | webapp | framework/webapp/entitydef/entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 641 |
| WebSite | webapp | framework/webapp/entitydef/entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 644 |
| WebSite | webapp | framework/webapp/entitydef/entitymodel.xml | applications/marketing/widget/ContactListForms.xml | 652 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/marketing/widget/sfa/EventScreens.xml | 63 |
| WorkEffortAndPartyAssign | workeffort | applications/workeffort/entitydef/entitymodel_view.xml | applications/marketing/widget/sfa/EventScreens.xml | 27 |
| WorkEffortAndPartyAssign | workeffort | applications/workeffort/entitydef/entitymodel_view.xml | applications/marketing/widget/sfa/EventScreens.xml | 34 |
| WorkEffortPurposeType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/marketing/widget/sfa/forms/EventForms.xml | 98 |

## Reverse component references

| Component | File | Line | References |
| --- | --- | --- | --- |
| order | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 2157 | 1 |
| party | applications/party/webapp/partymgr/WEB-INF/controller.xml | 1535 | 1 |
| party | applications/party/webapp/partymgr/WEB-INF/controller.xml | 1542 | 1 |
| party | applications/party/widget/partymgr/CommunicationEventScreens.xml | 457 | 1 |
| product | applications/product/webapp/catalog/WEB-INF/controller.xml | 3266 | 1 |
| product | applications/product/webapp/catalog/WEB-INF/controller.xml | 3267 | 1 |
| workeffort | applications/workeffort/webapp/workeffort/WEB-INF/controller.xml | 430 | 1 |