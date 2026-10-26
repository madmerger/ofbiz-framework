# Dependency inventory

## Called services and simple methods

| Kind | Name | Defined by | Definition | Source | Line |
| --- | --- | --- | --- | --- | --- |
| call-simple-method | genericBasePermissionCheck | common | component://common/minilang/permission/CommonPermissionServices.xml | applications/workeffort/minilang/permission/WorkEffortPermissionServices.xml | 26 |
| call-simple-method | genericBasePermissionCheck | common | component://common/minilang/permission/CommonPermissionServices.xml | applications/workeffort/minilang/permission/WorkEffortPermissionServices.xml | 31 |
| call-simple-method | genericBasePermissionCheck | common | component://common/minilang/permission/CommonPermissionServices.xml | applications/workeffort/minilang/permission/WorkEffortPermissionServices.xml | 42 |
| call-simple-method | workEffortPartyAnyRolePermission | unresolved | — | applications/workeffort/minilang/permission/WorkEffortPermissionServices.xml | 61 |
| call-simple-method | workEffortPartyOwnerRolePermission | unresolved | — | applications/workeffort/minilang/permission/WorkEffortPermissionServices.xml | 71 |
| call-simple-method | workEffortPartyOwnerRolePermission | unresolved | — | applications/workeffort/minilang/permission/WorkEffortPermissionServices.xml | 88 |
| call-simple-method | workEffortPartyGroupRolePermission | unresolved | — | applications/workeffort/minilang/permission/WorkEffortPermissionServices.xml | 101 |
| call-simple-method | workEffortPartyGroupRolePermission | unresolved | — | applications/workeffort/minilang/permission/WorkEffortPermissionServices.xml | 113 |
| call-simple-method | workEffortGenericPermission | unresolved | — | applications/workeffort/minilang/permission/WorkEffortPermissionServices.xml | 222 |
| call-simple-method | workEffortManagerPermission | unresolved | — | applications/workeffort/minilang/permission/WorkEffortPermissionServices.xml | 311 |
| call-service | createTimesheet | workeffort | applications/workeffort/servicedef/services_timesheet.xml | applications/workeffort/minilang/timesheet/TimesheetServices.xml | 45 |
| call-service | createTimesheet | workeffort | applications/workeffort/servicedef/services_timesheet.xml | applications/workeffort/minilang/timesheet/TimesheetServices.xml | 60 |
| call-service | createInvoice | accounting | applications/accounting/servicedef/services_invoice.xml | applications/workeffort/minilang/timesheet/TimesheetServices.xml | 71 |
| call-simple-method | createTimeEntryInvoiceItemsInline | unresolved | — | applications/workeffort/minilang/timesheet/TimesheetServices.xml | 88 |
| call-service | createInvoice | accounting | applications/accounting/servicedef/services_invoice.xml | applications/workeffort/minilang/timesheet/TimesheetServices.xml | 96 |
| call-service | updateInvoice | accounting | applications/accounting/servicedef/services_invoice.xml | applications/workeffort/minilang/timesheet/TimesheetServices.xml | 122 |
| call-simple-method | createTimeEntryInvoiceItemsInline | unresolved | — | applications/workeffort/minilang/timesheet/TimesheetServices.xml | 124 |
| call-service | getTimeEntryRate | workeffort | applications/workeffort/servicedef/services_timesheet.xml | applications/workeffort/minilang/timesheet/TimesheetServices.xml | 196 |
| call-service | createInvoiceItem | accounting | applications/accounting/servicedef/services_invoice.xml | applications/workeffort/minilang/timesheet/TimesheetServices.xml | 221 |
| call-service | updateInvoiceItem | accounting | applications/accounting/servicedef/services_invoice.xml | applications/workeffort/minilang/timesheet/TimesheetServices.xml | 234 |
| call-service | createInvoiceItem | accounting | applications/accounting/servicedef/services_invoice.xml | applications/workeffort/minilang/timesheet/TimesheetServices.xml | 248 |
| call-service | updateTimeEntry | workeffort | applications/workeffort/servicedef/services_timesheet.xml | applications/workeffort/minilang/timesheet/TimesheetServices.xml | 256 |
| call-simple-method | checkTimesheetStatus | unresolved | — | applications/workeffort/minilang/timesheet/TimesheetServices.xml | 278 |
| call-service | getRateAmount | accounting | applications/accounting/servicedef/services_rate.xml | applications/workeffort/minilang/timesheet/TimesheetServices.xml | 331 |
| call-service | createWorkEffort | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 117 |
| call-service | createWorkEffortAssoc | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 124 |
| call-service | copyWorkEffort | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 173 |
| call-service | createWorkEffort | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 358 |
| call-simple-method | testUpdateEventService | unresolved | — | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 361 |
| call-service | updateWorkEffort | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 383 |
| call-service | createWorkEffort | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 395 |
| call-simple-method | testUpdateProjectService | unresolved | — | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 398 |
| call-simple-method | testCreateWorkEffortNoteService | unresolved | — | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 399 |
| call-service | updateWorkEffort | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 429 |
| call-service | createWorkEffortNote | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 440 |
| call-service | wfAcceptAssignment | unresolved | — | applications/workeffort/minilang/workeffort/WorkflowSimpleEvents.xml | 27 |
| call-service | wfAcceptRoleAssignment | unresolved | — | applications/workeffort/minilang/workeffort/WorkflowSimpleEvents.xml | 36 |
| call-service | createWorkEffort | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/minilang/workeffort/WorkflowSimpleEvents.xml | 44 |
| call-service | createWorkEffortAndAssoc | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/minilang/workeffort/WorkflowSimpleEvents.xml | 56 |
| call-service | createWorkEffortNote | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/minilang/workeffort/WorkflowSimpleEvents.xml | 65 |
| runSync | userLogin | common | framework/common/servicedef/services.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/ICalWorker.java | 267 |
| runSync | wfAcceptAssignment | unresolved | — | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortPartyAssignmentServices.java | 63 |
| runSync | wfCompleteAssignment | unresolved | — | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortPartyAssignmentServices.java | 76 |
| runSync | wfDeclineAssignment | unresolved | — | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortPartyAssignmentServices.java | 89 |
| runSync | processWorkEffortEventReminder | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortServices.java | 996 |
| runSync | processWorkEffortEventReminder | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortServices.java | 1040 |
| runAsync | sendMailFromTemplateSetting | common | framework/common/servicedef/services_email.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortServices.java | 1091 |
| runSync | createWorkEffortAndPartyAssign | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 54 |
| runSync | createWorkEffort | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 87 |
| runSync | deleteWorkEffort | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 93 |
| runSync | copyWorkEffort | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 108 |
| runSync | duplicateWorkEffort | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 125 |
| runSync | makeCommunicationEventWorkEffort | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 144 |
| runSync | assignPartyToWorkEffort | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 171 |
| runSync | updatePartyToWorkEffortAssignment | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 201 |
| runSync | expireWorkEffortPartyAssignment | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 229 |
| runSync | quickAssignPartyToWorkEffort | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 254 |
| runSync | quickAssignPartyToWorkEffortWithRole | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 277 |
| runSync | createWorkEffortNote | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 300 |
| runSync | updateWorkEffortNote | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 325 |
| runSync | getWorkEffort | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 343 |
| runSync | createWorkEffortAssoc | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 361 |
| runSync | copyWorkEffortAssocs | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 385 |
| runSync | createWorkEffortKeyword | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 403 |
| runSync | deleteWorkEffortKeyword | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 423 |
| runSync | deleteWorkEffortKeywords | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 438 |
| runSync | createTimesheet | workeffort | applications/workeffort/servicedef/services_timesheet.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 457 |
| runSync | updateTimesheet | workeffort | applications/workeffort/servicedef/services_timesheet.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 480 |
| runSync | deleteTimesheet | workeffort | applications/workeffort/servicedef/services_timesheet.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 497 |
| runSync | createTimesheets | workeffort | applications/workeffort/servicedef/services_timesheet.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 514 |
| runSync | createTimesheetForThisWeek | workeffort | applications/workeffort/servicedef/services_timesheet.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 536 |
| runSync | addTimesheetToNewInvoice | workeffort | applications/workeffort/servicedef/services_timesheet.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 558 |
| runSync | createTimeEntry | workeffort | applications/workeffort/servicedef/services_timesheet.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 577 |
| runSync | updateTimeEntry | workeffort | applications/workeffort/servicedef/services_timesheet.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 597 |
| runSync | deleteTimeEntry | workeffort | applications/workeffort/servicedef/services_timesheet.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 613 |
| runSync | createWorkEffort | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 637 |
| runSync | updateWorkEffort | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 649 |
| runSync | createWorkEffort | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 678 |
| runSync | updateWorkEffort | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 690 |
| runSync | createWorkEffortNote | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 699 |
| runSync | getTimeEntryRate | workeffort | applications/workeffort/servicedef/services_timesheet.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 724 |
| runSync | duplicateWorkEffort | workeffort | applications/workeffort/servicedef/services.xml | applications/workeffort/src/test/groovy/org/apache/ofbiz/workeffort/workeffort/test/WorkEffortTests.groovy | 753 |

## External entity references

| Entity | Owning component | Owning entitymodel | Source | Line |
| --- | --- | --- | --- | --- |
| Agreement | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortAgreementCpd.xml | 117 |
| AgreementWorkEffortApplic | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortAgreementCpd.xml | 111 |
| CommunicationEvent | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortCommEventCpd.xml | 77 |
| CommunicationEventType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortCommEventCpd.xml | 170 |
| CommunicationEventWorkEff | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 26 |
| CommunicationEventWorkEff | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 28 |
| CommunicationEventWorkEff | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 45 |
| CommunicationEventWorkEff | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 47 |
| CommunicationEventWorkEff | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 60 |
| CommunicationEventWorkEff | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 62 |
| ContactMech | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortContactMechCpd.xml | 116 |
| ContactMechType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/LookupForms.xml | 152 |
| ContactMechType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/WorkEffortCommEventForms.xml | 39 |
| ContactMechType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 286 |
| ContactMechType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 513 |
| ContactMechType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 529 |
| ContactMechType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 570 |
| ContactMechType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortCommEventCpd.xml | 147 |
| ContactMechType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortCommEventCpd.xml | 196 |
| ContactMechType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortContactMechCpd.xml | 122 |
| Content | datamodel | applications/datamodel/entitydef/content-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortContentsCpd.xml | 125 |
| CustRequest | datamodel | applications/datamodel/entitydef/order-entitymodel.xml | applications/workeffort/widget/CustRequestScreens.xml | 34 |
| CustRequest | datamodel | applications/datamodel/entitydef/order-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortCommEventCpd.xml | 155 |
| CustRequestItem | datamodel | applications/datamodel/entitydef/order-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 206 |
| CustRequestItem | datamodel | applications/datamodel/entitydef/order-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 208 |
| EmailTemplateSetting | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortServices.java | 1082 |
| Enumeration | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/CalendarForms.xml | 27 |
| Enumeration | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/CalendarForms.xml | 117 |
| Enumeration | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/CalendarForms.xml | 147 |
| Enumeration | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/LookupForms.xml | 66 |
| Enumeration | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 109 |
| Enumeration | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 244 |
| Enumeration | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 500 |
| Enumeration | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 596 |
| Enumeration | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortCpd.xml | 258 |
| Enumeration | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortPartyAssignCpd.xml | 169 |
| Enumeration | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortPartyAssignCpd.xml | 170 |
| Enumeration | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortPartyAssignCpd.xml | 211 |
| Enumeration | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortPartyAssignCpd.xml | 219 |
| Facility | datamodel | applications/datamodel/entitydef/product-entitymodel.xml | applications/workeffort/widget/CalendarForms.xml | 45 |
| Facility | datamodel | applications/datamodel/entitydef/product-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortPartyAssignCpd.xml | 171 |
| FixedAsset | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/workeffort/widget/CalendarForms.xml | 52 |
| FixedAsset | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortFixedAssetCpd.xml | 130 |
| InventoryItemDetail | datamodel | applications/datamodel/entitydef/product-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortServices.java | 815 |
| Invoice | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/workeffort/minilang/timesheet/TimesheetServices.xml | 80 |
| Invoice | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/workeffort/minilang/timesheet/TimesheetServices.xml | 101 |
| Invoice | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/workeffort/minilang/timesheet/TimesheetServices.xml | 123 |
| InvoiceItem | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/workeffort/minilang/timesheet/TimesheetServices.xml | 200 |
| JobSandbox | service | framework/service/entitydef/entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 37 |
| JobSandbox | service | framework/service/entitydef/entitymodel.xml | applications/workeffort/widget/WorkEffortScreens.xml | 29 |
| MarketingCampaign | datamodel | applications/datamodel/entitydef/marketing-entitymodel.xml | applications/workeffort/widget/LookupForms.xml | 153 |
| MarketingCampaign | datamodel | applications/datamodel/entitydef/marketing-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 287 |
| NoteData | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 73 |
| NoteData | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 96 |
| NoteData | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 403 |
| Party | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/minilang/timesheet/TimesheetServices.xml | 110 |
| PartyAndContactMech | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 506 |
| PartyContactWithPurpose | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 627 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortSearch.java | 807 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/CalendarForms.xml | 165 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/LookupForms.xml | 182 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/LookupForms.xml | 183 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/TimesheetForms.xml | 37 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/TimesheetForms.xml | 96 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/TimesheetForms.xml | 179 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 490 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 561 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 564 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortCommEventCpd.xml | 138 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortCommEventCpd.xml | 141 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortPartyAssignCpd.xml | 157 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortReviewsCpd.xml | 131 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortShopListCpd.xml | 127 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortTimeEntriesCpd.xml | 204 |
| PartyRate | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/workeffort/minilang/timesheet/TimesheetServices.xml | 174 |
| PartyRate | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 478 |
| PartyRate | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/workeffort/widget/TimesheetForms.xml | 43 |
| PartyRate | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/workeffort/widget/TimesheetForms.xml | 48 |
| PartyRelationship | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/minilang/permission/WorkEffortPermissionServices.xml | 278 |
| PeriodType | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortRateAssignCpd.xml | 129 |
| PeriodType | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortRateAssignCpd.xml | 151 |
| PostalAddress | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortContactMechCpd.xml | 118 |
| Product | datamodel | applications/datamodel/entitydef/product-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortSearch.java | 937 |
| Product | datamodel | applications/datamodel/entitydef/product-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortGoodStandardsCpd.xml | 140 |
| Product | datamodel | applications/datamodel/entitydef/product-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortGoodStandardsCpd.xml | 181 |
| QuoteItem | datamodel | applications/datamodel/entitydef/order-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortQuotesCpd.xml | 212 |
| RateAmount | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortRateAssignCpd.xml | 70 |
| RateAmount | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortRateAssignCpd.xml | 117 |
| RateType | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/workeffort/widget/TimesheetForms.xml | 49 |
| RateType | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/workeffort/widget/TimesheetForms.xml | 59 |
| RateType | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/workeffort/widget/TimesheetForms.xml | 138 |
| RateType | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/workeffort/widget/TimesheetForms.xml | 225 |
| RateType | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/workeffort/widget/TimesheetForms.xml | 264 |
| RateType | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/workeffort/widget/TimesheetScreens.xml | 79 |
| RateType | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortRateAssignCpd.xml | 127 |
| RateType | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortRateAssignCpd.xml | 144 |
| RateType | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortTimeEntriesCpd.xml | 205 |
| RateType | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortTimeEntriesCpd.xml | 226 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortSearch.java | 808 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/CalendarForms.xml | 167 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/CalendarForms.xml | 181 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/CustRequestForms.xml | 38 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/TimesheetForms.xml | 178 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/TimesheetForms.xml | 198 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 496 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 536 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 573 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 576 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 622 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortCommEventCpd.xml | 150 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortCommEventCpd.xml | 153 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortCommEventCpd.xml | 204 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortCommEventCpd.xml | 211 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortPartyAssignCpd.xml | 136 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortPartyAssignCpd.xml | 163 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortPartyAssignCpd.xml | 186 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortServices.java | 331 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortServices.java | 355 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/CalendarForms.xml | 110 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/CalendarForms.xml | 144 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/CalendarForms.xml | 169 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/CustRequestForms.xml | 35 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/LookupForms.xml | 47 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/LookupForms.xml | 51 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/LookupForms.xml | 55 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/LookupForms.xml | 115 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/LookupForms.xml | 119 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/LookupForms.xml | 123 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/TimesheetForms.xml | 113 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/WorkEffortCommEventForms.xml | 38 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 27 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 39 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 78 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 82 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 86 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 211 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 215 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 219 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 427 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 499 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 522 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 567 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 698 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 707 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortCommEventCpd.xml | 144 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortCommEventCpd.xml | 180 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortCpd.xml | 225 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortCpd.xml | 229 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortCpd.xml | 233 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortFixedAssetCpd.xml | 138 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortFixedAssetCpd.xml | 139 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortFixedAssetCpd.xml | 156 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortFixedAssetCpd.xml | 164 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortGoodStandardsCpd.xml | 144 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortGoodStandardsCpd.xml | 168 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortGoodStandardsCpd.xml | 186 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortPartyAssignCpd.xml | 128 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortPartyAssignCpd.xml | 167 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortPartyAssignCpd.xml | 168 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortPartyAssignCpd.xml | 195 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortPartyAssignCpd.xml | 203 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortReviewsCpd.xml | 138 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortReviewsCpd.xml | 156 |
| StatusValidChangeToDetail | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortCommEventCpd.xml | 187 |
| TelecomNumber | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortContactMechCpd.xml | 119 |
| TemporalExpression | service | framework/service/entitydef/entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 122 |
| TimeEntry | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/timesheet/TimesheetServices.xml | 277 |
| TimeEntry | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/timesheet/TimesheetServices.xml | 317 |
| TimeEntry | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/TimesheetForms.xml | 125 |
| TimeEntry | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/TimesheetForms.xml | 132 |
| TimeEntry | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/TimesheetForms.xml | 210 |
| TimeEntry | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/TimesheetScreens.xml | 62 |
| TimeEntry | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortTimeEntriesCpd.xml | 97 |
| TimeEntry | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortTimeEntriesCpd.xml | 187 |
| Timesheet | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/timesheet/TimesheetServices.xml | 39 |
| Timesheet | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/timesheet/TimesheetServices.xml | 64 |
| Timesheet | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/timesheet/TimesheetServices.xml | 167 |
| Timesheet | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/timesheet/TimesheetServices.xml | 352 |
| Timesheet | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/CommonScreens.xml | 129 |
| Timesheet | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/LookupForms.xml | 159 |
| Timesheet | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/TimesheetForms.xml | 25 |
| Timesheet | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/TimesheetForms.xml | 30 |
| Timesheet | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/TimesheetForms.xml | 72 |
| Timesheet | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/TimesheetForms.xml | 90 |
| Timesheet | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/TimesheetScreens.xml | 33 |
| Timesheet | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/TimesheetScreens.xml | 176 |
| Timesheet | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/TimesheetScreens.xml | 219 |
| Timesheet | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/TimesheetScreens.xml | 243 |
| TimesheetRole | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/TimesheetForms.xml | 171 |
| Uom | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/LookupForms.xml | 74 |
| Uom | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 132 |
| Uom | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 252 |
| Uom | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortCpd.xml | 266 |
| Uom | common | framework/common/entitydef/entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortRateAssignCpd.xml | 160 |
| UserLogin | security | framework/security/entitydef/entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 355 |
| UserLogin | security | framework/security/entitydef/entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 380 |
| UserLogin | security | framework/security/entitydef/entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 392 |
| UserLogin | security | framework/security/entitydef/entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 426 |
| UserLogin | security | framework/security/entitydef/entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 437 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/permission/WorkEffortPermissionServices.xml | 73 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/permission/WorkEffortPermissionServices.xml | 167 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/permission/WorkEffortPermissionServices.xml | 208 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/permission/WorkEffortPermissionServices.xml | 295 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/permission/WorkEffortPermissionServices.xml | 322 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/timesheet/TimesheetServices.xml | 91 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 220 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 250 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 295 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 332 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 341 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 362 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 400 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/groovy/org/apache/ofbiz/workeffort/workeffort/workeffort/WorkEffortServicesScript.groovy | 305 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/content/WorkEffortContentWrapper.java | 355 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/content/WorkEffortContentWrapper.java | 358 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/ICalConverter.java | 394 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/ICalConverter.java | 730 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/ICalConverter.java | 758 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/ICalConverter.java | 845 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/ICalWorker.java | 132 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortPartyAssignmentServices.java | 48 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortSearch.java | 120 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortSearch.java | 618 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortSearch.java | 1293 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortServices.java | 313 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortServices.java | 645 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/CalendarScreens.xml | 149 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/CalendarScreens.xml | 238 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/CommonScreens.xml | 68 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/LookupForms.xml | 26 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/TimesheetForms.xml | 140 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 297 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 327 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 346 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 353 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 373 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 380 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortRelatedSummaryScreens.xml | 44 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortScreens.xml | 99 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortScreens.xml | 133 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortScreens.xml | 236 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortScreens.xml | 652 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortScreens.xml | 754 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortScreens.xml | 798 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortScreens.xml | 822 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortScreens.xml | 844 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortScreens.xml | 867 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortScreens.xml | 891 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortTrees.xml | 23 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortTrees.xml | 25 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortTrees.xml | 26 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortTrees.xml | 34 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortTrees.xml | 53 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortTrees.xml | 55 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortTrees.xml | 56 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortTrees.xml | 64 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortAgreementCpd.xml | 55 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortAttributeCpd.xml | 61 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortAttributeCpd.xml | 80 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortCommEventCpd.xml | 57 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortCommEventCpd.xml | 76 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortContactMechCpd.xml | 55 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortContentsCpd.xml | 61 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortContentsCpd.xml | 80 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortCpd.xml | 113 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortCpd.xml | 128 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortEventRemindersCpd.xml | 61 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortFixedAssetCpd.xml | 57 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortGoodStandardsCpd.xml | 61 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortKeywordsCpd.xml | 55 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortNotesCpd.xml | 55 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortNotesCpd.xml | 141 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortOrdersCpd.xml | 55 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortPartyAssignCpd.xml | 57 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortQuotesCpd.xml | 73 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortQuotesCpd.xml | 106 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortRateAssignCpd.xml | 51 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortRequestAndItemCpd.xml | 77 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortRequestAndItemCpd.xml | 96 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortRequestAndItemCpd.xml | 114 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortRequestAndItemCpd.xml | 133 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortRequirementsCpd.xml | 65 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortReviewsCpd.xml | 61 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortTimeEntriesCpd.xml | 78 |
| WorkEffortAssoc | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 106 |
| WorkEffortAssoc | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 135 |
| WorkEffortAssoc | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 145 |
| WorkEffortAssoc | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 159 |
| WorkEffortAssoc | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortSearch.java | 104 |
| WorkEffortAssoc | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortWorker.java | 57 |
| WorkEffortAssoc | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortWorker.java | 60 |
| WorkEffortAssoc | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortWorker.java | 65 |
| WorkEffortAssoc | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortScreens.xml | 237 |
| WorkEffortAssoc | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortScreens.xml | 256 |
| WorkEffortAssoc | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortScreens.xml | 281 |
| WorkEffortAssoc | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortTrees.xml | 28 |
| WorkEffortAssoc | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortTrees.xml | 33 |
| WorkEffortAssoc | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortTrees.xml | 44 |
| WorkEffortAssoc | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortTrees.xml | 58 |
| WorkEffortAssoc | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortTrees.xml | 63 |
| WorkEffortAssoc | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortTrees.xml | 69 |
| WorkEffortAssocType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortSearch.java | 619 |
| WorkEffortAssocType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 301 |
| WorkEffortAssocType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 331 |
| WorkEffortAssocType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 360 |
| WorkEffortAssocType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 391 |
| WorkEffortAttribute | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortKeywordIndex.java | 85 |
| WorkEffortAttribute | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortAttributeCpd.xml | 118 |
| WorkEffortContactMech | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortContactMechCpd.xml | 111 |
| WorkEffortContent | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/content/WorkEffortContentWrapper.java | 339 |
| WorkEffortContent | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/content/WorkEffortContentWrapper.java | 371 |
| WorkEffortContent | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/content/WorkEffortContentWrapper.java | 410 |
| WorkEffortContent | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortContentsCpd.xml | 118 |
| WorkEffortContentType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/content/WorkEffortContentWrapper.java | 207 |
| WorkEffortContentType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortContentsCpd.xml | 131 |
| WorkEffortContentType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortContentsCpd.xml | 152 |
| WorkEffortEventReminder | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/ICalConverter.java | 331 |
| WorkEffortEventReminder | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortServices.java | 928 |
| WorkEffortEventReminder | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortEventRemindersCpd.xml | 80 |
| WorkEffortEventReminder | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortEventRemindersCpd.xml | 128 |
| WorkEffortFixedAssetAssign | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 223 |
| WorkEffortFixedAssetAssign | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 228 |
| WorkEffortFixedAssetAssign | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 238 |
| WorkEffortFixedAssetAssign | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 244 |
| WorkEffortFixedAssetAssign | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 599 |
| WorkEffortFixedAssetAssign | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortFixedAssetCpd.xml | 76 |
| WorkEffortFixedAssetAssign | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortFixedAssetCpd.xml | 103 |
| WorkEffortFixedAssetAssign | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortFixedAssetCpd.xml | 123 |
| WorkEffortGoodStandard | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortGoodStandardsCpd.xml | 80 |
| WorkEffortGoodStandard | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortGoodStandardsCpd.xml | 134 |
| WorkEffortGoodStandardType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortGoodStandardsCpd.xml | 141 |
| WorkEffortGoodStandardType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortGoodStandardsCpd.xml | 160 |
| WorkEffortGoodStandardType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortGoodStandardsCpd.xml | 182 |
| WorkEffortInventoryAssign | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 188 |
| WorkEffortInventoryAssign | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 196 |
| WorkEffortKeyword | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 305 |
| WorkEffortKeyword | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 311 |
| WorkEffortKeyword | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortKeywordIndex.java | 122 |
| WorkEffortKeyword | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortKeywordsCpd.xml | 111 |
| WorkEffortNote | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 88 |
| WorkEffortNote | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 95 |
| WorkEffortPartyAssignment | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/permission/WorkEffortPermissionServices.xml | 141 |
| WorkEffortPartyAssignment | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/permission/WorkEffortPermissionServices.xml | 148 |
| WorkEffortPartyAssignment | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/permission/WorkEffortPermissionServices.xml | 190 |
| WorkEffortPartyAssignment | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/permission/WorkEffortPermissionServices.xml | 324 |
| WorkEffortPartyAssignment | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 582 |
| WorkEffortPartyAssignment | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/ICalConverter.java | 826 |
| WorkEffortPartyAssignment | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortServices.java | 340 |
| WorkEffortPartyAssignment | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/CalendarForms.xml | 158 |
| WorkEffortPartyAssignment | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortPartyAssignCpd.xml | 79 |
| WorkEffortPartyAssignment | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortPartyAssignCpd.xml | 146 |
| WorkEffortPurposeType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/LookupForms.xml | 39 |
| WorkEffortPurposeType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/LookupForms.xml | 107 |
| WorkEffortPurposeType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 71 |
| WorkEffortPurposeType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 204 |
| WorkEffortPurposeType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 428 |
| WorkEffortPurposeType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 474 |
| WorkEffortPurposeType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortCpd.xml | 218 |
| WorkEffortReview | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 265 |
| WorkEffortReview | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/minilang/workeffort/WorkEffortSimpleServices.xml | 272 |
| WorkEffortReview | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortReviewsCpd.xml | 80 |
| WorkEffortReview | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortReviewsCpd.xml | 124 |
| WorkEffortSearchConstraint | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortSearch.java | 606 |
| WorkEffortSearchConstraint | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortSearch.java | 716 |
| WorkEffortSearchConstraint | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortSearch.java | 798 |
| WorkEffortSearchConstraint | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortSearch.java | 926 |
| WorkEffortSearchConstraint | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortSearch.java | 1069 |
| WorkEffortSearchConstraint | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortSearch.java | 1176 |
| WorkEffortSearchResult | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortSearch.java | 468 |
| WorkEffortType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/ICalConverter.java | 865 |
| WorkEffortType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/CalendarForms.xml | 38 |
| WorkEffortType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/CalendarForms.xml | 95 |
| WorkEffortType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/CalendarForms.xml | 103 |
| WorkEffortType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/CalendarForms.xml | 141 |
| WorkEffortType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/LookupForms.xml | 32 |
| WorkEffortType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/LookupForms.xml | 100 |
| WorkEffortType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/LookupForms.xml | 151 |
| WorkEffortType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 64 |
| WorkEffortType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 197 |
| WorkEffortType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 285 |
| WorkEffortType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 426 |
| WorkEffortType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/WorkEffortForms.xml | 473 |
| WorkEffortType | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortCpd.xml | 211 |
| WorkReqFulfType | datamodel | applications/datamodel/entitydef/order-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortRequirementsCpd.xml | 179 |
| WorkReqFulfType | datamodel | applications/datamodel/entitydef/order-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortRequirementsCpd.xml | 194 |
| WorkReqFulfType | datamodel | applications/datamodel/entitydef/order-entitymodel.xml | applications/workeffort/widget/workeffort/WorkEffortRequirementsCpd.xml | 200 |

## Reverse component references

| Component | File | Line | References |
| --- | --- | --- | --- |
| accounting | applications/accounting/webapp/accounting/WEB-INF/controller.xml | 2826 | 1 |
| accounting | applications/accounting/widget/FixedAssetScreens.xml | 292 | 1 |
| content | applications/content/webapp/content/WEB-INF/controller.xml | 1953 | 1 |
| humanres | applications/humanres/webapp/humanres/WEB-INF/web.xml | 49 | 1 |
| humanres | applications/humanres/widget/PersonTrainingScreens.xml | 37 | 1 |
| humanres | applications/humanres/widget/PersonTrainingScreens.xml | 44 | 1 |
| humanres | applications/humanres/widget/PersonTrainingScreens.xml | 57 | 1 |
| humanres | applications/humanres/widget/PersonTrainingScreens.xml | 64 | 1 |
| humanres | applications/humanres/widget/PersonTrainingScreens.xml | 74 | 1 |
| humanres | applications/humanres/widget/PersonTrainingScreens.xml | 81 | 1 |
| humanres | applications/humanres/widget/PersonTrainingScreens.xml | 91 | 1 |
| humanres | applications/humanres/widget/PersonTrainingScreens.xml | 97 | 1 |
| marketing | applications/marketing/webapp/sfa/WEB-INF/controller.xml | 26 | 1 |
| marketing | applications/marketing/webapp/sfa/WEB-INF/web.xml | 54 | 1 |
| marketing | applications/marketing/widget/sfa/forms/EventForms.xml | 23 | 1 |
| marketing | applications/marketing/widget/sfa/forms/EventForms.xml | 74 | 1 |
| order | applications/order/minilang/request/CustRequestServices.xml | 141 | 1 |
| order | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 2171 | 1 |
| order | applications/order/widget/ordermgr/QuoteWorkEffortForms.xml | 50 | 1 |
| order | applications/order/widget/ordermgr/QuoteWorkEffortForms.xml | 68 | 1 |
| party | applications/party/servicedef/services.xml | 932 | 1 |
| party | applications/party/webapp/partymgr/WEB-INF/controller.xml | 1533 | 1 |
| party | applications/party/widget/partymgr/CommunicationEventForms.xml | 632 | 1 |
| product | applications/product/webapp/catalog/WEB-INF/controller.xml | 3262 | 1 |
| product | applications/product/webapp/facility/WEB-INF/controller.xml | 1348 | 1 |