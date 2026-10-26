# Service inventory

## addTimesheetToInvoice

- Definition: `applications/workeffort/servicedef/services_timesheet.xml:63`
- Engine: simple; location: component://workeffort/minilang/timesheet/TimesheetServices.xml; invoke: addTimesheetToInvoice
- Default entity: Timesheet; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | String | IN | false |
| timesheetId | String | IN | false |

## addTimesheetToNewInvoice

- Definition: `applications/workeffort/servicedef/services_timesheet.xml:70`
- Engine: simple; location: component://workeffort/minilang/timesheet/TimesheetServices.xml; invoke: addTimesheetToInvoice
- Default entity: Timesheet; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | String | OUT | false |
| partyId | String | IN | false |
| partyIdFrom | String | IN | false |
| timesheetId | String | IN | false |

## addWorkEffortTimeToInvoice

- Definition: `applications/workeffort/servicedef/services_timesheet.xml:80`
- Engine: simple; location: component://workeffort/minilang/timesheet/TimesheetServices.xml; invoke: addWorkEffortTimeToInvoice
- Default entity: Timesheet; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| combineInvoiceItem | String | IN | true |
| invoiceId | String | IN | false |
| thruDate | Timestamp | IN | true |
| workEffortId | String | IN | false |

## addWorkEffortTimeToNewInvoice

- Definition: `applications/workeffort/servicedef/services_timesheet.xml:89`
- Engine: simple; location: component://workeffort/minilang/timesheet/TimesheetServices.xml; invoke: addWorkEffortTimeToInvoice
- Default entity: Timesheet; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| combineInvoiceItem | String | IN | true |
| invoiceId | String | OUT | false |
| partyId | String | IN | false |
| partyIdFrom | String | IN | false |
| thruDate | Timestamp | IN | true |
| workEffortId | String | IN | false |

## assignInventoryToWorkEffort

- Definition: `applications/workeffort/servicedef/services.xml:684`
- Engine: simple; location: component://workeffort/minilang/workeffort/WorkEffortSimpleServices.xml; invoke: assignInventoryToWorkEffort
- Default entity: WorkEffortInventoryAssign; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| inventoryItemId | id | IN | false |
| quantity | floating-point | IN | true |
| statusId | id | IN | true |
| workEffortId | id | IN | false |

## assignPartyToWorkEffort

- Definition: `applications/workeffort/servicedef/services.xml:205`
- Engine: groovy; location: component://workeffort/src/main/groovy/org/apache/ofbiz/workeffort/workeffort/workeffort/WorkEffortServicesScript.groovy; invoke: assignPartyToWorkEffort
- Default entity: WorkEffortPartyAssignment; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "createWorkEffortPartyAssignment"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## assocAcceptedCustRequestToWorkEffort

- Definition: `applications/workeffort/servicedef/services.xml:836`
- Engine: groovy; location: component://workeffort/src/main/groovy/org/apache/ofbiz/workeffort/workeffort/workeffort/WorkEffortServicesScript.groovy; invoke: assocAcceptedCustRequestToWorkEffort
- Default entity: CustRequestWorkEffort; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| custRequestId | id | IN | — |
| workEffortId | id | IN | — |

## checkAndCreateWorkEffort

- Definition: `applications/workeffort/servicedef/services.xml:421`
- Engine: groovy; location: component://workeffort/src/main/groovy/org/apache/ofbiz/workeffort/workeffort/workeffort/WorkEffortServicesScript.groovy; invoke: checkAndCreateWorkEffort
- Default entity: Shipment; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| additionalShippingCharge | currency-amount | IN | true |
| addtlShippingChargeDesc | long-varchar | IN | true |
| createdByUserLogin | id-vlong | IN | true |
| createdDate | date-time | IN | true |
| currencyUomId | id | IN | true |
| destinationContactMechId | id | IN | true |
| destinationFacilityId | id | IN | true |
| destinationTelecomNumberId | id | IN | true |
| estimatedArrivalDate | date-time | IN | true |
| estimatedArrivalWorkEffId | id | IN | true |
| estimatedReadyDate | date-time | IN | true |
| estimatedShipCost | currency-amount | IN | true |
| estimatedShipDate | date-time | IN | true |
| estimatedShipWorkEffId | id | IN | true |
| handlingInstructions | long-varchar | IN | true |
| lastModifiedByUserLogin | id-vlong | IN | true |
| lastModifiedDate | date-time | IN | true |
| latestCancelDate | date-time | IN | true |
| originContactMechId | id | IN | true |
| originFacilityId | id | IN | true |
| originTelecomNumberId | id | IN | true |
| partyIdFrom | id | IN | true |
| partyIdTo | id | IN | true |
| picklistBinId | id | IN | true |
| primaryOrderId | id | IN | true |
| primaryReturnId | id | IN | true |
| primaryShipGroupSeqId | id | IN | true |
| shipmentId | id | IN | false |
| shipmentTypeId | id | IN | true |
| statusId | id | IN | true |

## checkAndUpdateWorkEffort

- Definition: `applications/workeffort/servicedef/services.xml:427`
- Engine: groovy; location: component://workeffort/src/main/groovy/org/apache/ofbiz/workeffort/workeffort/workeffort/WorkEffortServicesScript.groovy; invoke: checkAndUpdateWorkEffort
- Default entity: Shipment; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| additionalShippingCharge | currency-amount | IN | true |
| addtlShippingChargeDesc | long-varchar | IN | true |
| createdByUserLogin | id-vlong | IN | true |
| createdDate | date-time | IN | true |
| currencyUomId | id | IN | true |
| destinationContactMechId | id | IN | true |
| destinationFacilityId | id | IN | true |
| destinationTelecomNumberId | id | IN | true |
| estimatedArrivalDate | date-time | IN | true |
| estimatedArrivalWorkEffId | id | IN | true |
| estimatedReadyDate | date-time | IN | true |
| estimatedShipCost | currency-amount | IN | true |
| estimatedShipDate | date-time | IN | true |
| estimatedShipWorkEffId | id | IN | true |
| handlingInstructions | long-varchar | IN | true |
| lastModifiedByUserLogin | id-vlong | IN | true |
| lastModifiedDate | date-time | IN | true |
| latestCancelDate | date-time | IN | true |
| originContactMechId | id | IN | true |
| originFacilityId | id | IN | true |
| originTelecomNumberId | id | IN | true |
| partyIdFrom | id | IN | true |
| partyIdTo | id | IN | true |
| picklistBinId | id | IN | true |
| primaryOrderId | id | IN | true |
| primaryReturnId | id | IN | true |
| primaryShipGroupSeqId | id | IN | true |
| shipmentId | id | IN | false |
| shipmentTypeId | id | IN | true |
| statusId | id | IN | true |

## checkCustRequestItemExists

- Definition: `applications/workeffort/servicedef/services.xml:831`
- Engine: simple; location: component://workeffort/minilang/workeffort/WorkEffortSimpleServices.xml; invoke: checkCustRequestItemExists
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| custRequestItemExists | java.lang.String | OUT | true |

## checkTimesheetStatus

- Definition: `applications/workeffort/servicedef/services_timesheet.xml:114`
- Engine: simple; location: component://workeffort/minilang/timesheet/TimesheetServices.xml; invoke: checkTimesheetStatus
- Default entity: TimeEntry; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| timeEntryId | id | IN | true |

## copyWorkEffort

- Definition: `applications/workeffort/servicedef/services.xml:116`
- Engine: groovy; location: component://workeffort/src/main/groovy/org/apache/ofbiz/workeffort/workeffort/workeffort/WorkEffortServicesScript.groovy; invoke: copyWorkEffort
- Default entity: —; auth: true
- Use transaction: —; timeout: 300
- Permission service: workEffortGenericPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| copyRelatedValues | String | IN | true |
| copyWorkEffortAssocs | String | IN | true |
| deepCopy | String | IN | true |
| excludeExpiredAssocs | String | IN | true |
| excludeExpiredRelations | String | IN | true |
| sourceWorkEffortId | String | IN | false |
| targetWorkEffortId | String | IN | true |
| workEffortId | String | OUT | false |

## copyWorkEffortAssocs

- Definition: `applications/workeffort/servicedef/services.xml:534`
- Engine: simple; location: component://workeffort/minilang/workeffort/WorkEffortSimpleServices.xml; invoke: copyWorkEffortAssocs
- Default entity: —; auth: true
- Use transaction: —; timeout: 300
- Permission service: workEffortGenericPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| copyRelatedValues | String | IN | true |
| deepCopy | String | IN | true |
| excludeExpiredAssocs | String | IN | true |
| excludeExpiredRelations | String | IN | true |
| sourceWorkEffortId | String | IN | false |
| targetWorkEffortId | String | IN | true |

## createApplicationSandbox

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:200`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ApplicationSandbox; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| applicationId | id | INOUT | true |
| fromDate | date-time | IN | true |
| partyId | id | IN | true |
| roleTypeId | id | IN | true |
| runtimeDataId | id | IN | true |
| workEffortId | id | IN | true |

## createCommunicationEventWorkEff

- Definition: `applications/workeffort/servicedef/services.xml:701`
- Engine: simple; location: component://workeffort/minilang/workeffort/WorkEffortSimpleServices.xml; invoke: makeCommunicationEventWorkEffort
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## createDeliverable

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:28`
- Engine: entity-auto; location: —; invoke: create
- Default entity: Deliverable; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| deliverableId | id | INOUT | true |
| deliverableName | name | IN | true |
| deliverableTypeId | id | IN | true |
| description | description | IN | true |

## createDeliverableType

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:44`
- Engine: entity-auto; location: —; invoke: create
- Default entity: DeliverableType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| deliverableTypeId | id | INOUT | true |
| description | description | IN | true |

## createOrderHeaderWorkEffort

- Definition: `applications/workeffort/servicedef/services.xml:925`
- Engine: entity-auto; location: —; invoke: create
- Default entity: OrderHeaderWorkEffort; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| orderId | id | INOUT | true |
| workEffortId | id | IN | false |

## createShoppingListWorkEffort

- Definition: `applications/workeffort/servicedef/services.xml:898`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ShoppingListWorkEffort; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| shoppingListId | id | INOUT | true |
| workEffortId | id | IN | false |

## createTimeEntry

- Definition: `applications/workeffort/servicedef/services_timesheet.xml:120`
- Engine: entity-auto; location: —; invoke: create
- Default entity: TimeEntry; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| fromDate | date-time | IN | true |
| hours | floating-point | IN | true |
| invoiceId | id | IN | true |
| invoiceItemSeqId | id | IN | true |
| partyId | id | IN | true |
| rateTypeId | id | IN | true |
| thruDate | date-time | IN | true |
| timeEntryId | id | OUT | false |
| timesheetId | id | IN | true |
| workEffortId | id | IN | true |

## createTimesheet

- Definition: `applications/workeffort/servicedef/services_timesheet.xml:28`
- Engine: entity-auto; location: —; invoke: create
- Default entity: Timesheet; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| approvedByUserLoginId | id-vlong | IN | true |
| clientPartyId | id | IN | true |
| comments | comment | IN | true |
| fromDate | date-time | IN | true |
| partyId | id | IN | true |
| statusId | id | IN | true |
| thruDate | date-time | IN | true |
| timesheetId | id | OUT | false |

## createTimesheetForThisWeek

- Definition: `applications/workeffort/servicedef/services_timesheet.xml:53`
- Engine: simple; location: component://workeffort/minilang/timesheet/TimesheetServices.xml; invoke: createTimesheetForThisWeek
- Default entity: Timesheet; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| approvedByUserLoginId | id-vlong | IN | true |
| clientPartyId | id | IN | true |
| comments | comment | IN | true |
| fromDate | date-time | IN | true |
| partyId | id | IN | true |
| requiredDate | Timestamp | IN | true |
| statusId | id | IN | true |
| thruDate | date-time | IN | true |
| timesheetId | id | OUT | false |

## createTimesheetRole

- Definition: `applications/workeffort/servicedef/services_timesheet.xml:102`
- Engine: entity-auto; location: —; invoke: create
- Default entity: TimesheetRole; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |
| timesheetId | id | IN | false |

## createTimesheets

- Definition: `applications/workeffort/servicedef/services_timesheet.xml:44`
- Engine: simple; location: component://workeffort/minilang/timesheet/TimesheetServices.xml; invoke: createTimesheets
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| clientPartyId | String | IN | true |
| comments | String | IN | true |
| fromDate | String | IN | true |
| partyIdList | List | IN | true |
| thruDate | String | IN | true |

## createWorkEffort

- Definition: `applications/workeffort/servicedef/services.xml:41`
- Engine: groovy; location: component://workeffort/src/main/groovy/org/apache/ofbiz/workeffort/workeffort/workeffort/WorkEffortServicesScript.groovy; invoke: createWorkEffort
- Default entity: WorkEffort; auth: —
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: CREATE
- Implements: [{"service": "interfaceWorkEffort"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | String | IN | true |
| currentStatusId | — | — | false |
| custRequestId | String | IN | true |
| description | — | — | — |
| quickAssignPartyId | String | IN | true |
| requirementId | String | IN | true |
| webSiteId | String | IN | true |
| workEffortId | id | INOUT | true |
| workEffortName | — | — | false |
| workEffortTypeId | — | — | false |

## createWorkEffortAndAssoc

- Definition: `applications/workeffort/servicedef/services.xml:561`
- Engine: simple; location: component://workeffort/minilang/workeffort/WorkEffortSimpleServices.xml; invoke: createWorkEffortAndAssoc
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| currentStatusId | — | — | false |
| fromDate | — | — | true |
| quickAssignPartyId | String | IN | true |
| workEffortAssocTypeId | — | — | false |
| workEffortIdFrom | — | — | false |
| workEffortIdTo | — | — | true |
| workEffortName | — | — | false |
| workEffortTypeId | — | — | false |

## createWorkEffortAndPartyAssign

- Definition: `applications/workeffort/servicedef/services.xml:57`
- Engine: groovy; location: component://workeffort/src/main/groovy/org/apache/ofbiz/workeffort/workeffort/workeffort/WorkEffortServicesScript.groovy; invoke: createWorkEffortAndPartyAssign
- Default entity: WorkEffort; auth: —
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: CREATE
- Implements: [{"service": "interfaceWorkEffort"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | String | IN | true |
| currentStatusId | — | — | false |
| partyId | String | IN | — |
| quickAssignPartyId | String | IN | true |
| requirementId | String | IN | true |
| roleTypeId | String | IN | — |
| statusId | String | IN | — |
| workEffortId | id | INOUT | true |
| workEffortName | — | — | false |
| workEffortTypeId | — | — | false |

## createWorkEffortAssoc

- Definition: `applications/workeffort/servicedef/services.xml:450`
- Engine: simple; location: component://workeffort/minilang/workeffort/WorkEffortSimpleServices.xml; invoke: createWorkEffortAssoc
- Default entity: WorkEffortAssoc; auth: —
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | true |
| sequenceNum | numeric | IN | true |
| thruDate | date-time | IN | true |
| workEffortAssocTypeId | id | IN | false |
| workEffortIdFrom | id | IN | false |
| workEffortIdTo | id | IN | false |

## createWorkEffortAssocAttribute

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:60`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WorkEffortAssocAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | — |
| attrValue | value | IN | true |
| fromDate | date-time | IN | true |
| workEffortAssocTypeId | id | IN | — |
| workEffortIdFrom | id | IN | — |
| workEffortIdTo | id | IN | — |

## createWorkEffortAssocType

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:76`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WorkEffortAssocType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| workEffortAssocTypeId | id | INOUT | true |

## createWorkEffortAssocTypeAttr

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:92`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WorkEffortAssocTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| description | description | IN | true |
| workEffortAssocTypeId | id | IN | — |

## createWorkEffortAttribute

- Definition: `applications/workeffort/servicedef/services.xml:1373`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WorkEffortAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | false |
| attrValue | value | IN | true |
| workEffortId | id | IN | false |

## createWorkEffortBilling

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:108`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WorkEffortBilling; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | id | IN | — |
| invoiceItemSeqId | id | IN | — |
| percentage | floating-point | IN | true |
| workEffortId | id | IN | — |

## createWorkEffortContactMech

- Definition: `applications/workeffort/servicedef/services.xml:1422`
- Engine: groovy; location: component://workeffort/src/main/groovy/org/apache/ofbiz/workeffort/workeffort/workeffort/WorkEffortServicesScript.groovy; invoke: createWorkEffortContactMech
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | INOUT | true |
| contactMechPurposeTypeId | String | IN | true |
| contactMechTypeId | String | IN | true |
| infoString | String | IN | true |
| workEffortId | String | IN | — |

## createWorkEffortContent

- Definition: `applications/workeffort/servicedef/services.xml:1083`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WorkEffortContent; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| fromDate | date-time | IN | true |
| thruDate | date-time | IN | true |
| workEffortContentTypeId | id | IN | false |
| workEffortId | id | IN | false |

## createWorkEffortContentType

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:124`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WorkEffortContentType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| parentTypeId | id | IN | true |
| workEffortContentTypeId | id | INOUT | true |

## createWorkEffortEmailAddress

- Definition: `applications/workeffort/servicedef/services.xml:1494`
- Engine: groovy; location: component://workeffort/src/main/groovy/org/apache/ofbiz/workeffort/workeffort/workeffort/WorkEffortServicesScript.groovy; invoke: createWorkEffortContactMech
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: CREATE
- Implements: [{"service": "createWorkEffortContactMech"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechTypeId | — | — | — |

## createWorkEffortEventReminder

- Definition: `applications/workeffort/servicedef/services.xml:1615`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WorkEffortEventReminder; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | id | IN | true |
| currentCount | numeric | IN | true |
| localeId | id | IN | true |
| partyId | id | IN | true |
| reminderDateTime | date-time | IN | true |
| reminderOffset | numeric | IN | true |
| repeatCount | numeric | IN | true |
| repeatInterval | numeric | IN | true |
| sequenceId | id | OUT | false |
| timeZoneId | id-long | IN | true |
| workEffortId | id | IN | false |

## createWorkEffortFixedAssetAssign

- Definition: `applications/workeffort/servicedef/services.xml:1015`
- Engine: simple; location: component://workeffort/minilang/workeffort/WorkEffortSimpleServices.xml; invoke: createWorkEffortFixedAssetAssign
- Default entity: WorkEffortFixedAssetAssign; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| allocatedCost | currency-amount | IN | true |
| availabilityStatusId | id | IN | true |
| comments | comment | IN | true |
| fixedAssetId | id | IN | false |
| fromDate | date-time | IN | true |
| statusId | id | IN | true |
| thruDate | date-time | IN | true |
| workEffortId | id | IN | false |

## createWorkEffortFixedAssetStd

- Definition: `applications/workeffort/servicedef/services.xml:961`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WorkEffortFixedAssetStd; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| estimatedCost | currency-amount | IN | true |
| estimatedDuration | floating-point | IN | true |
| estimatedQuantity | floating-point | IN | true |
| fixedAssetTypeId | id | IN | false |
| workEffortId | id | IN | false |

## createWorkEffortGoodStandard

- Definition: `applications/workeffort/servicedef/services.xml:604`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WorkEffortGoodStandard; auth: —
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| estimatedCost | currency-amount | IN | true |
| estimatedQuantity | floating-point | IN | true |
| fromDate | date-time | IN | true |
| productId | id | IN | false |
| statusId | id | IN | true |
| thruDate | date-time | IN | true |
| workEffortGoodStdTypeId | id | IN | false |
| workEffortId | id | IN | false |

## createWorkEffortGoodStandardType

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:139`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WorkEffortGoodStandardType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| workEffortGoodStdTypeId | id | INOUT | true |

## createWorkEffortICalData

- Definition: `applications/workeffort/servicedef/services.xml:1552`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WorkEffortIcalData; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortICalendarPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| icalData | very-long | IN | true |
| workEffortId | id | IN | false |

## createWorkEffortInventoryProduced

- Definition: `applications/workeffort/servicedef/services.xml:1510`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WorkEffortInventoryProduced; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| inventoryItemId | id | IN | false |
| workEffortId | id | IN | false |

## createWorkEffortKeyword

- Definition: `applications/workeffort/servicedef/services.xml:1248`
- Engine: simple; location: component://workeffort/minilang/workeffort/WorkEffortSimpleServices.xml; invoke: createWorkEffortKeyword
- Default entity: WorkEffortKeyword; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| keyword | short-varchar | IN | false |
| relevancyWeight | numeric | IN | true |
| workEffortId | id | IN | false |

## createWorkEffortKeywords

- Definition: `applications/workeffort/servicedef/services.xml:1280`
- Engine: simple; location: component://workeffort/minilang/workeffort/WorkEffortSimpleServices.xml; invoke: createWorkEffortKeywords
- Default entity: WorkEffortKeyword; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| keyword | short-varchar | IN | false |
| relevancyWeight | numeric | IN | true |
| workEffortId | id | IN | false |

## createWorkEffortNote

- Definition: `applications/workeffort/servicedef/services.xml:280`
- Engine: simple; location: component://workeffort/minilang/workeffort/WorkEffortSimpleServices.xml; invoke: createWorkEffortNote
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| internalNote | String | IN | true |
| noteId | String | OUT | false |
| noteInfo | String | IN | false |
| noteName | String | IN | true |
| noteParty | String | IN | true |
| workEffortId | String | IN | false |

## createWorkEffortPartyAssignment

- Definition: `applications/workeffort/servicedef/services.xml:186`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WorkEffortPartyAssignment; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| assignedByUserLoginId | id-vlong | IN | true |
| availabilityStatusId | id | IN | true |
| comments | comment | IN | true |
| delegateReasonEnumId | id | IN | true |
| expectationEnumId | id | IN | true |
| facilityId | id | IN | true |
| fromDate | date-time | INOUT | true |
| mustRsvp | indicator | IN | true |
| partyId | id | IN | — |
| roleTypeId | id | IN | — |
| statusDateTime | date-time | IN | true |
| statusId | id | IN | true |
| thruDate | date-time | IN | true |
| workEffortId | id | IN | — |

## createWorkEffortPostalAddress

- Definition: `applications/workeffort/servicedef/services.xml:1456`
- Engine: groovy; location: component://workeffort/src/main/groovy/org/apache/ofbiz/workeffort/workeffort/workeffort/WorkEffortServicesScript.groovy; invoke: createWorkEffortContactMech
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: CREATE
- Implements: [{"service": "createPostalAddress"}, {"service": "createPartyPostalAddress"}, {"service": "createWorkEffortContactMech"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechTypeId | — | — | — |
| relationEntityName | String | IN | — |

## createWorkEffortPurposeType

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:154`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WorkEffortPurposeType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| parentTypeId | id | IN | true |
| workEffortPurposeTypeId | id | INOUT | true |

## createWorkEffortQuote

- Definition: `applications/workeffort/servicedef/services.xml:842`
- Engine: entity-auto; location: —; invoke: create
- Default entity: QuoteWorkEffort; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| quoteId | id | INOUT | false |
| workEffortId | id | IN | false |

## createWorkEffortRequest

- Definition: `applications/workeffort/servicedef/services.xml:740`
- Engine: entity-auto; location: —; invoke: create
- Default entity: CustRequestWorkEffort; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| custRequestId | id | INOUT | false |
| description | — | — | — |
| workEffortId | id | IN | false |

## createWorkEffortRequestItem

- Definition: `applications/workeffort/servicedef/services.xml:792`
- Engine: entity-auto; location: —; invoke: create
- Default entity: CustRequestItemWorkEffort; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| custRequestId | id | IN | false |
| custRequestItemSeqId | id | IN | false |
| workEffortId | id | IN | false |

## createWorkEffortRequestItemAndRequestItem

- Definition: `applications/workeffort/servicedef/services.xml:770`
- Engine: group; location: createWorkEffortRequestItemAndRequestItem; invoke: —
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| custRequestId | — | — | false |
| custRequestItemExists | java.lang.String | OUT | true |
| custRequestItemSeqId | — | — | false |
| workEffortId | — | — | false |

## createWorkEffortReview

- Definition: `applications/workeffort/servicedef/services.xml:1183`
- Engine: simple; location: component://workeffort/minilang/workeffort/WorkEffortSimpleServices.xml; invoke: createWorkEffortReview
- Default entity: WorkEffortReview; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| postedAnonymous | indicator | IN | true |
| rating | floating-point | IN | true |
| reviewDate | date-time | IN | true |
| reviewText | very-long | IN | true |
| statusId | id | IN | true |
| userLoginId | id-vlong | IN | true |
| workEffortId | id | IN | false |

## createWorkEffortSkillStandard

- Definition: `applications/workeffort/servicedef/services.xml:1327`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WorkEffortSkillStandard; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| estimatedCost | currency-amount | IN | true |
| estimatedDuration | floating-point | IN | true |
| estimatedNumPeople | floating-point | IN | true |
| skillTypeId | id | IN | false |
| workEffortId | id | IN | false |

## createWorkEffortStatus

- Definition: `applications/workeffort/servicedef/services.xml:110`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WorkEffortStatus; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| reason | description | IN | true |
| setByUserLogin | id-vlong | IN | true |
| statusDatetime | date-time | IN | — |
| statusId | id | IN | — |
| workEffortId | id | IN | — |

## createWorkEffortSurveyAppl

- Definition: `applications/workeffort/servicedef/services.xml:1670`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WorkEffortSurveyAppl; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| surveyId | id | IN | false |
| thruDate | date-time | IN | true |
| workEffortId | id | IN | false |

## createWorkEffortTelecomNumber

- Definition: `applications/workeffort/servicedef/services.xml:1475`
- Engine: groovy; location: component://workeffort/src/main/groovy/org/apache/ofbiz/workeffort/workeffort/workeffort/WorkEffortServicesScript.groovy; invoke: createWorkEffortContactMech
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: CREATE
- Implements: [{"service": "createTelecomNumber"}, {"service": "createPartyTelecomNumber"}, {"service": "createWorkEffortContactMech"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechTypeId | — | — | — |
| relationEntityName | String | IN | — |

## createWorkEffortTextContent

- Definition: `applications/workeffort/servicedef/services.xml:1159`
- Engine: group; location: —; invoke: —
- Default entity: —; auth: true
- Use transaction: —; timeout: 300
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## createWorkEffortType

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:169`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WorkEffortType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| workEffortTypeId | id | INOUT | true |

## createWorkEffortTypeAttr

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:184`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WorkEffortTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| description | description | IN | true |
| workEffortTypeId | id | IN | — |

## createWorkRequirementFulfillment

- Definition: `applications/workeffort/servicedef/services.xml:872`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WorkRequirementFulfillment; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| requirementId | id | IN | true |
| workEffortId | id | IN | false |
| workReqFulfTypeId | id | IN | true |

## deleteApplicationSandbox

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:210`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ApplicationSandbox; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| applicationId | id | IN | — |

## deleteCommunicationEventWorkEff

- Definition: `applications/workeffort/servicedef/services.xml:725`
- Engine: simple; location: component://workeffort/minilang/workeffort/WorkEffortSimpleServices.xml; invoke: deleteCommunicationEventWorkEff
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | — | — | false |
| workEffortId | — | — | false |

## deleteDeliverable

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:38`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: Deliverable; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| deliverableId | id | IN | — |

## deleteDeliverableType

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:54`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: DeliverableType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| deliverableTypeId | id | IN | — |

## deleteOrderHeaderWorkEffort

- Definition: `applications/workeffort/servicedef/services.xml:937`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: OrderHeaderWorkEffort; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| orderId | id | IN | false |
| workEffortId | id | IN | false |

## deletePartyToWorkEffortAssignment

- Definition: `applications/workeffort/servicedef/services.xml:215`
- Engine: entity-auto; location: —; invoke: expire
- Default entity: WorkEffortPartyAssignment; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "expireWorkEffortPartyAssignment"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## deleteShoppingListWorkEffort

- Definition: `applications/workeffort/servicedef/services.xml:910`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ShoppingListWorkEffort; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| shoppingListId | id | IN | false |
| workEffortId | id | IN | false |

## deleteTimeEntry

- Definition: `applications/workeffort/servicedef/services_timesheet.xml:132`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: TimeEntry; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| timeEntryId | id | IN | false |

## deleteTimesheet

- Definition: `applications/workeffort/servicedef/services_timesheet.xml:40`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: Timesheet; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| timesheetId | id | IN | false |

## deleteTimesheetRole

- Definition: `applications/workeffort/servicedef/services_timesheet.xml:108`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: TimesheetRole; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |
| timesheetId | id | IN | false |

## deleteWorkEffort

- Definition: `applications/workeffort/servicedef/services.xml:100`
- Engine: groovy; location: component://workeffort/src/main/groovy/org/apache/ofbiz/workeffort/workeffort/workeffort/WorkEffortServicesScript.groovy; invoke: deleteWorkEffort
- Default entity: WorkEffort; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| workEffortId | id | IN | false |

## deleteWorkEffortAssocAttribute

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:70`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WorkEffortAssocAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| workEffortAssocTypeId | id | IN | — |
| workEffortIdFrom | id | IN | — |
| workEffortIdTo | id | IN | — |

## deleteWorkEffortAssocType

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:86`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WorkEffortAssocType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| workEffortAssocTypeId | id | IN | — |

## deleteWorkEffortAssocTypeAttr

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:102`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WorkEffortAssocTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| workEffortAssocTypeId | id | IN | — |

## deleteWorkEffortAttribute

- Definition: `applications/workeffort/servicedef/services.xml:1405`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WorkEffortAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | false |
| workEffortId | id | IN | false |

## deleteWorkEffortBilling

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:118`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WorkEffortBilling; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | id | IN | — |
| invoiceItemSeqId | id | IN | — |
| workEffortId | id | IN | — |

## deleteWorkEffortContactMech

- Definition: `applications/workeffort/servicedef/services.xml:1451`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WorkEffortContactMech; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | id | IN | false |
| fromDate | date-time | IN | false |
| workEffortId | id | IN | false |

## deleteWorkEffortContent

- Definition: `applications/workeffort/servicedef/services.xml:1131`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WorkEffortContent; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| fromDate | date-time | IN | false |
| thruDate | date-time | IN | true |
| workEffortContentTypeId | id | IN | false |
| workEffortId | id | IN | false |

## deleteWorkEffortContentType

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:134`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WorkEffortContentType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| workEffortContentTypeId | id | IN | — |

## deleteWorkEffortEventReminder

- Definition: `applications/workeffort/servicedef/services.xml:1643`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WorkEffortEventReminder; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| sequenceId | id | IN | false |
| workEffortId | id | IN | false |

## deleteWorkEffortGoodStandardType

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:149`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WorkEffortGoodStandardType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| workEffortGoodStdTypeId | id | IN | — |

## deleteWorkEffortICalData

- Definition: `applications/workeffort/servicedef/services.xml:1574`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WorkEffortIcalData; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortICalendarPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| workEffortId | id | IN | false |

## deleteWorkEffortInventoryProduced

- Definition: `applications/workeffort/servicedef/services.xml:1525`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WorkEffortInventoryProduced; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | — | — | false |
| fromDate | — | — | false |
| inventoryItemId | id | IN | false |
| workEffortId | id | IN | false |

## deleteWorkEffortKeyword

- Definition: `applications/workeffort/servicedef/services.xml:1265`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WorkEffortKeyword; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| keyword | short-varchar | IN | false |
| workEffortId | id | IN | false |

## deleteWorkEffortKeywords

- Definition: `applications/workeffort/servicedef/services.xml:1297`
- Engine: simple; location: component://workeffort/minilang/workeffort/WorkEffortSimpleServices.xml; invoke: deleteWorkEffortKeywords
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| workEffortId | — | — | false |

## deleteWorkEffortPurposeType

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:164`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WorkEffortPurposeType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| workEffortPurposeTypeId | id | IN | — |

## deleteWorkEffortQuote

- Definition: `applications/workeffort/servicedef/services.xml:857`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: QuoteWorkEffort; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| quoteId | id | IN | false |
| workEffortId | id | IN | false |

## deleteWorkEffortRequest

- Definition: `applications/workeffort/servicedef/services.xml:756`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: CustRequestWorkEffort; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| custRequestId | id | IN | false |
| workEffortId | id | IN | false |

## deleteWorkEffortRequestItem

- Definition: `applications/workeffort/servicedef/services.xml:812`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: CustRequestItemWorkEffort; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| custRequestId | id | IN | false |
| custRequestItemSeqId | id | IN | false |
| workEffortId | id | IN | false |

## deleteWorkEffortReview

- Definition: `applications/workeffort/servicedef/services.xml:1216`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WorkEffortReview; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| reviewDate | date-time | IN | false |
| userLoginId | id-vlong | IN | false |
| workEffortId | id | IN | false |

## deleteWorkEffortSkillStandard

- Definition: `applications/workeffort/servicedef/services.xml:1357`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WorkEffortSkillStandard; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| skillTypeId | id | IN | false |
| workEffortId | id | IN | false |

## deleteWorkEffortSurveyAppl

- Definition: `applications/workeffort/servicedef/services.xml:1713`
- Engine: entity-auto; location: —; invoke: expire
- Default entity: WorkEffortSurveyAppl; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| surveyId | id | IN | false |
| workEffortId | id | IN | false |

## deleteWorkEffortType

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:179`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WorkEffortType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| workEffortTypeId | id | IN | — |

## deleteWorkEffortTypeAttr

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:194`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WorkEffortTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| workEffortTypeId | id | IN | — |

## deleteWorkRequirementFulfillment

- Definition: `applications/workeffort/servicedef/services.xml:883`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WorkRequirementFulfillment; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| requirementId | id | IN | false |
| workEffortId | id | IN | false |

## duplicateWorkEffort

- Definition: `applications/workeffort/servicedef/services.xml:151`
- Engine: groovy; location: component://workeffort/src/main/groovy/org/apache/ofbiz/workeffort/workeffort/workeffort/WorkEffortServicesScript.groovy; invoke: duplicateWorkEffort
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| duplicateWorkEffortAssignmentRates | String | IN | true |
| duplicateWorkEffortAssocs | String | IN | true |
| duplicateWorkEffortContents | String | IN | true |
| duplicateWorkEffortNotes | String | IN | true |
| oldWorkEffortId | String | IN | — |
| removeWorkEffortAssignmentRates | String | IN | true |
| removeWorkEffortAssocs | String | IN | true |
| removeWorkEffortContents | String | IN | true |
| removeWorkEffortNotes | String | IN | true |
| statusId | String | IN | true |
| workEffortId | String | OUT | — |

## expireWorkEffortPartyAssignment

- Definition: `applications/workeffort/servicedef/services.xml:199`
- Engine: entity-auto; location: —; invoke: expire
- Default entity: WorkEffortPartyAssignment; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | — |
| partyId | id | IN | — |
| roleTypeId | id | IN | — |
| thruDate | Timestamp | IN | true |
| workEffortId | id | IN | — |

## getICalWorkEfforts

- Definition: `applications/workeffort/servicedef/services.xml:1590`
- Engine: simple; location: component://workeffort/minilang/workeffort/WorkEffortSimpleServices.xml; invoke: getICalWorkEfforts
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| workEffortId | String | IN | false |
| workEffortTypeId | String | IN | true |
| workEfforts | List | OUT | — |

## getPartyICalUrl

- Definition: `applications/workeffort/servicedef/services.xml:1602`
- Engine: simple; location: component://workeffort/minilang/workeffort/WorkEffortSimpleServices.xml; invoke: getPartyICalUrl
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| iCalUrl | String | OUT | true |
| partyId | String | IN | false |

## getProductManufacturingSummaryByFacility

- Definition: `applications/workeffort/servicedef/services.xml:433`
- Engine: java; location: org.apache.ofbiz.workeffort.workeffort.WorkEffortServices; invoke: getProductManufacturingSummaryByFacility
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| facilityId | String | IN | true |
| productId | String | IN | false |
| summaryInByFacility | Map | OUT | false |
| summaryOutByFacility | Map | OUT | false |

## getTimeEntryRate

- Definition: `applications/workeffort/servicedef/services_timesheet.xml:136`
- Engine: simple; location: component://workeffort/minilang/timesheet/TimesheetServices.xml; invoke: getTimeEntryRate
- Default entity: TimeEntry; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortManagerPermission; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| currencyUomId | String | IN | true |
| rateAmount | BigDecimal | OUT | true |
| timeEntryId | id | IN | false |

## getWorkEffort

- Definition: `applications/workeffort/servicedef/services.xml:368`
- Engine: java; location: org.apache.ofbiz.workeffort.workeffort.WorkEffortServices; invoke: getWorkEffort
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| canView | java.lang.Boolean | OUT | true |
| currentStatusId | java.lang.String | IN | true |
| currentStatusItem | org.apache.ofbiz.entity.GenericValue | OUT | true |
| partyAssigns | java.util.Collection | OUT | true |
| tryEntity | java.lang.Boolean | OUT | true |
| workEffort | org.apache.ofbiz.entity.GenericValue | OUT | true |
| workEffortId | java.lang.String | INOUT | true |

## getWorkEffortAssignedActivities

- Definition: `applications/workeffort/servicedef/services.xml:347`
- Engine: java; location: org.apache.ofbiz.workeffort.workeffort.WorkEffortServices; invoke: getWorkEffortAssignedActivities
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| activities | java.util.List | OUT | false |

## getWorkEffortAssignedActivitiesByGroup

- Definition: `applications/workeffort/servicedef/services.xml:360`
- Engine: java; location: org.apache.ofbiz.workeffort.workeffort.WorkEffortServices; invoke: getWorkEffortAssignedActivitiesByGroup
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| groupActivities | java.util.List | OUT | false |

## getWorkEffortAssignedActivitiesByRole

- Definition: `applications/workeffort/servicedef/services.xml:354`
- Engine: java; location: org.apache.ofbiz.workeffort.workeffort.WorkEffortServices; invoke: getWorkEffortAssignedActivitiesByRole
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| roleActivities | java.util.List | OUT | false |

## getWorkEffortAssignedEventsForRole

- Definition: `applications/workeffort/servicedef/services.xml:320`
- Engine: java; location: org.apache.ofbiz.workeffort.workeffort.WorkEffortServices; invoke: getWorkEffortAssignedEventsForRole
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| events | java.util.List | OUT | false |
| roleTypeId | String | IN | false |

## getWorkEffortAssignedEventsForRoleOfAllParties

- Definition: `applications/workeffort/servicedef/services.xml:330`
- Engine: java; location: org.apache.ofbiz.workeffort.workeffort.WorkEffortServices; invoke: getWorkEffortAssignedEventsForRoleOfAllParties
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| events | java.util.List | OUT | false |
| roleTypeId | String | IN | false |

## getWorkEffortAssignedTasks

- Definition: `applications/workeffort/servicedef/services.xml:340`
- Engine: java; location: org.apache.ofbiz.workeffort.workeffort.WorkEffortServices; invoke: getWorkEffortAssignedTasks
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| tasks | java.util.List | OUT | false |

## getWorkEffortEventsByPeriod

- Definition: `applications/workeffort/servicedef/services.xml:379`
- Engine: java; location: org.apache.ofbiz.workeffort.workeffort.WorkEffortServices; invoke: getWorkEffortEventsByPeriod
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| calendarType | String | IN | true |
| entityExprList | java.util.List | IN | true |
| facilityId | String | IN | true |
| filterOutCanceledEvents | java.lang.Boolean | IN | true |
| fixedAssetId | String | IN | true |
| maxConcurrentEntries | java.lang.Integer | OUT | false |
| numPeriods | java.lang.Integer | IN | false |
| partyId | String | IN | true |
| partyIds | java.util.Collection | IN | true |
| periodType | java.lang.Integer | IN | false |
| periods | java.util.List | OUT | false |
| start | java.sql.Timestamp | IN | false |
| workEffortTypeId | String | IN | true |

## indexWorkEffortKeywords

- Definition: `applications/workeffort/servicedef/services.xml:1237`
- Engine: simple; location: component://workeffort/minilang/workeffort/WorkEffortSimpleServices.xml; invoke: indexWorkEffortKeywords
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| workEffortId | String | IN | false |
| workEffortInstance | org.apache.ofbiz.entity.GenericValue | IN | true |

## interfaceWorkEffort

- Definition: `applications/workeffort/servicedef/services.xml:28`
- Engine: interface; location: —; invoke: —
- Default entity: WorkEffort; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accommodationMapId | id | IN | true |
| accommodationSpotId | id | IN | true |
| actualCompletionDate | date-time | IN | true |
| actualMilliSeconds | floating-point | IN | true |
| actualSetupMillis | floating-point | IN | true |
| actualStartDate | date-time | IN | true |
| createdByUserLogin | id-vlong | IN | true |
| createdDate | date-time | IN | true |
| currentStatusId | id | IN | true |
| description | description | IN | true |
| estimateCalcMethod | id | IN | true |
| estimatedCompletionDate | date-time | IN | true |
| estimatedMilliSeconds | floating-point | IN | true |
| estimatedSetupMillis | floating-point | IN | true |
| estimatedStartDate | date-time | IN | true |
| facilityId | id | IN | true |
| fixedAssetId | id | IN | true |
| infoUrl | long-varchar | IN | true |
| lastModifiedByUserLogin | id-vlong | IN | true |
| lastModifiedDate | date-time | IN | true |
| lastStatusUpdate | date-time | IN | true |
| locationDesc | description | IN | true |
| moneyUomId | id | IN | true |
| noteId | id | IN | true |
| percentComplete | numeric | IN | true |
| priority | numeric | IN | true |
| quantityProduced | fixed-point | IN | true |
| quantityRejected | fixed-point | IN | true |
| quantityToProduce | fixed-point | IN | true |
| recurrenceInfoId | id | IN | true |
| reserv2ndPPPerc | fixed-point | IN | true |
| reservNthPPPerc | fixed-point | IN | true |
| reservPersons | fixed-point | IN | true |
| revisionNumber | numeric | IN | true |
| runtimeDataId | id | IN | true |
| scopeEnumId | id | IN | true |
| sendNotificationEmail | indicator | IN | true |
| serviceLoaderName | name | IN | true |
| showAsEnumId | id | IN | true |
| sourceReferenceId | id-long | IN | true |
| specialTerms | long-varchar | IN | true |
| tempExprId | id | IN | true |
| timeTransparency | numeric | IN | true |
| totalMilliSecondsAllowed | floating-point | IN | true |
| totalMoneyAllowed | currency-amount | IN | true |
| universalId | short-varchar | IN | true |
| workEffortName | name | IN | true |
| workEffortParentId | id | IN | true |
| workEffortPurposeTypeId | id | IN | true |
| workEffortTypeId | id | IN | true |

## makeCommunicationEventWorkEffort

- Definition: `applications/workeffort/servicedef/services.xml:176`
- Engine: simple; location: component://workeffort/minilang/workeffort/WorkEffortSimpleServices.xml; invoke: makeCommunicationEventWorkEffort
- Default entity: WorkEffort; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accommodationMapId | id | IN | true |
| accommodationSpotId | id | IN | true |
| actualCompletionDate | date-time | IN | true |
| actualMilliSeconds | floating-point | IN | true |
| actualSetupMillis | floating-point | IN | true |
| actualStartDate | date-time | IN | true |
| communicationEventId | String | INOUT | — |
| createdByUserLogin | id-vlong | IN | true |
| createdDate | date-time | IN | true |
| currentStatusId | id | IN | true |
| description | description | IN | true |
| estimateCalcMethod | id | IN | true |
| estimatedCompletionDate | date-time | IN | true |
| estimatedMilliSeconds | floating-point | IN | true |
| estimatedSetupMillis | floating-point | IN | true |
| estimatedStartDate | date-time | IN | true |
| facilityId | id | IN | true |
| fixedAssetId | id | IN | true |
| infoUrl | long-varchar | IN | true |
| lastModifiedByUserLogin | id-vlong | IN | true |
| lastModifiedDate | date-time | IN | true |
| lastStatusUpdate | date-time | IN | true |
| locationDesc | description | IN | true |
| moneyUomId | id | IN | true |
| noteId | id | IN | true |
| percentComplete | numeric | IN | true |
| priority | numeric | IN | true |
| quantityProduced | fixed-point | IN | true |
| quantityRejected | fixed-point | IN | true |
| quantityToProduce | fixed-point | IN | true |
| recurrenceInfoId | id | IN | true |
| relationDescription | String | IN | true |
| reserv2ndPPPerc | fixed-point | IN | true |
| reservNthPPPerc | fixed-point | IN | true |
| reservPersons | fixed-point | IN | true |
| revisionNumber | numeric | IN | true |
| runtimeDataId | id | IN | true |
| scopeEnumId | id | IN | true |
| sendNotificationEmail | indicator | IN | true |
| serviceLoaderName | name | IN | true |
| showAsEnumId | id | IN | true |
| sourceReferenceId | id-long | IN | true |
| specialTerms | long-varchar | IN | true |
| tempExprId | id | IN | true |
| timeTransparency | numeric | IN | true |
| totalMilliSecondsAllowed | floating-point | IN | true |
| totalMoneyAllowed | currency-amount | IN | true |
| universalId | short-varchar | IN | true |
| workEffortId | id | INOUT | false |
| workEffortName | name | IN | true |
| workEffortParentId | id | IN | true |
| workEffortPurposeTypeId | id | IN | true |
| workEffortTypeId | id | IN | true |

## processWorkEffortEventReminder

- Definition: `applications/workeffort/servicedef/services.xml:1662`
- Engine: java; location: org.apache.ofbiz.workeffort.workeffort.WorkEffortServices; invoke: processWorkEffortEventReminder
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| bodyParameters | Map | IN | — |
| reminder | GenericValue | IN | — |

## processWorkEffortEventReminders

- Definition: `applications/workeffort/servicedef/services.xml:1658`
- Engine: java; location: org.apache.ofbiz.workeffort.workeffort.WorkEffortServices; invoke: processWorkEffortEventReminders
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## quickAssignPartyToWorkEffort

- Definition: `applications/workeffort/servicedef/services.xml:246`
- Engine: groovy; location: component://workeffort/src/main/groovy/org/apache/ofbiz/workeffort/workeffort/workeffort/WorkEffortServicesScript.groovy; invoke: quickAssignPartyToWorkEffort
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| quickAssignPartyId | String | IN | — |
| roleTypeId | String | IN | — |
| workEffortId | String | IN | — |

## quickAssignPartyToWorkEffortWithRole

- Definition: `applications/workeffort/servicedef/services.xml:261`
- Engine: groovy; location: component://workeffort/src/main/groovy/org/apache/ofbiz/workeffort/workeffort/workeffort/WorkEffortServicesScript.groovy; invoke: quickAssignPartyToWorkEffortWithRole
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| quickAssignPartyId | String | IN | — |
| roleTypeId | String | IN | — |
| workEffortId | String | IN | — |

## removeDuplicateWorkEfforts

- Definition: `applications/workeffort/servicedef/services.xml:415`
- Engine: java; location: org.apache.ofbiz.workeffort.workeffort.WorkEffortServices; invoke: removeDuplicateWorkEfforts
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| workEffortIterator | java.util.ListIterator | IN | true |
| workEfforts | java.util.List | INOUT | true |

## removeWorkEffortAssoc

- Definition: `applications/workeffort/servicedef/services.xml:505`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WorkEffortAssoc; auth: —
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| workEffortAssocTypeId | id | IN | false |
| workEffortIdFrom | id | IN | false |
| workEffortIdTo | id | IN | false |

## removeWorkEffortFixedAssetAssign

- Definition: `applications/workeffort/servicedef/services.xml:1058`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WorkEffortFixedAssetAssign; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetId | id | IN | false |
| fromDate | date-time | IN | false |
| workEffortId | id | IN | false |

## removeWorkEffortFixedAssetStd

- Definition: `applications/workeffort/servicedef/services.xml:997`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WorkEffortFixedAssetStd; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetTypeId | id | IN | false |
| workEffortId | id | IN | false |

## removeWorkEffortGoodStandard

- Definition: `applications/workeffort/servicedef/services.xml:658`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WorkEffortGoodStandard; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| productId | id | IN | false |
| workEffortGoodStdTypeId | id | IN | false |
| workEffortId | id | IN | false |

## setWorkEffortFixedAssetAssign

- Definition: `applications/workeffort/servicedef/services.xml:951`
- Engine: simple; location: component://workeffort/minilang/workeffort/WorkEffortSimpleServices.xml; invoke: setWorkEffortFixedAssetAssign
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| workEffortId | String | IN | false |

## timesheetUpdatePermission

- Definition: `applications/workeffort/servicedef/services.xml:1320`
- Engine: simple; location: component://workeffort/minilang/permission/WorkEffortPermissionServices.xml; invoke: timesheetUpdatePermission
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| workEffortId | String | IN | true |

## unassignPartyFromWorkEffort

- Definition: `applications/workeffort/servicedef/services.xml:220`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WorkEffortPartyAssignment; auth: —
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |
| workEffortId | id | IN | false |

## updateApplicationSandbox

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:205`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ApplicationSandbox; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| applicationId | id | IN | — |
| fromDate | date-time | IN | true |
| partyId | id | IN | true |
| roleTypeId | id | IN | true |
| runtimeDataId | id | IN | true |
| workEffortId | id | IN | true |

## updateCommunicationEventWorkEff

- Definition: `applications/workeffort/servicedef/services.xml:708`
- Engine: simple; location: component://workeffort/minilang/workeffort/WorkEffortSimpleServices.xml; invoke: updateCommunicationEventWorkEff
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | — | — | false |
| workEffortId | — | — | false |

## updateDeliverable

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:33`
- Engine: entity-auto; location: —; invoke: update
- Default entity: Deliverable; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| deliverableId | id | IN | — |
| deliverableName | name | IN | true |
| deliverableTypeId | id | IN | true |
| description | description | IN | true |

## updateDeliverableType

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:49`
- Engine: entity-auto; location: —; invoke: update
- Default entity: DeliverableType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| deliverableTypeId | id | IN | — |
| description | description | IN | true |

## updatePartyToWorkEffortAssignment

- Definition: `applications/workeffort/servicedef/services.xml:210`
- Engine: groovy; location: component://workeffort/src/main/groovy/org/apache/ofbiz/workeffort/workeffort/workeffort/WorkEffortServicesScript.groovy; invoke: updatePartyToWorkEffortAssignment
- Default entity: WorkEffortPartyAssignment; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "updateWorkEffortPartyAssignment"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## updateTimeEntry

- Definition: `applications/workeffort/servicedef/services_timesheet.xml:126`
- Engine: simple; location: component://workeffort/minilang/timesheet/TimesheetServices.xml; invoke: updateTimeEntry
- Default entity: TimeEntry; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| fromDate | date-time | IN | true |
| hours | floating-point | IN | true |
| invoiceId | id | IN | true |
| invoiceItemSeqId | id | IN | true |
| partyId | id | IN | true |
| rateTypeId | id | IN | true |
| thruDate | date-time | IN | true |
| timeEntryId | id | IN | false |
| timesheetId | id | IN | true |
| workEffortId | id | IN | true |

## updateTimesheet

- Definition: `applications/workeffort/servicedef/services_timesheet.xml:35`
- Engine: entity-auto; location: —; invoke: update
- Default entity: Timesheet; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| approvedByUserLoginId | id-vlong | IN | true |
| clientPartyId | id | IN | true |
| comments | comment | IN | true |
| fromDate | date-time | IN | true |
| partyId | id | IN | true |
| statusId | id | IN | true |
| thruDate | date-time | IN | true |
| timesheetId | id | IN | false |

## updateWorkEffort

- Definition: `applications/workeffort/servicedef/services.xml:85`
- Engine: groovy; location: component://workeffort/src/main/groovy/org/apache/ofbiz/workeffort/workeffort/workeffort/WorkEffortServicesScript.groovy; invoke: updateWorkEffort
- Default entity: WorkEffort; auth: —
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: UPDATE
- Implements: [{"service": "interfaceWorkEffort"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | String | IN | true |
| reason | String | IN | true |
| webSiteId | String | IN | true |
| workEffortId | id | IN | false |

## updateWorkEffortAndAssoc

- Definition: `applications/workeffort/servicedef/services.xml:596`
- Engine: group; location: updateWorkEffortAndAssoc; invoke: —
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## updateWorkEffortAssoc

- Definition: `applications/workeffort/servicedef/services.xml:476`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WorkEffortAssoc; auth: —
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| sequenceNum | numeric | IN | true |
| thruDate | date-time | IN | true |
| workEffortAssocTypeId | id | IN | false |
| workEffortIdFrom | id | IN | false |
| workEffortIdTo | id | IN | false |

## updateWorkEffortAssocAttribute

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:65`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WorkEffortAssocAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | — |
| attrValue | value | IN | true |
| fromDate | date-time | IN | true |
| workEffortAssocTypeId | id | IN | — |
| workEffortIdFrom | id | IN | — |
| workEffortIdTo | id | IN | — |

## updateWorkEffortAssocType

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:81`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WorkEffortAssocType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| workEffortAssocTypeId | id | IN | — |

## updateWorkEffortAssocTypeAttr

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:97`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WorkEffortAssocTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| description | description | IN | true |
| workEffortAssocTypeId | id | IN | — |

## updateWorkEffortAttribute

- Definition: `applications/workeffort/servicedef/services.xml:1389`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WorkEffortAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | false |
| attrValue | value | IN | true |
| workEffortId | id | IN | false |

## updateWorkEffortBilling

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:113`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WorkEffortBilling; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | id | IN | — |
| invoiceItemSeqId | id | IN | — |
| percentage | floating-point | IN | true |
| workEffortId | id | IN | — |

## updateWorkEffortContactMech

- Definition: `applications/workeffort/servicedef/services.xml:1438`
- Engine: groovy; location: component://workeffort/src/main/groovy/org/apache/ofbiz/workeffort/workeffort/workeffort/WorkEffortServicesScript.groovy; invoke: updateWorkEffortContactMech
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | INOUT | true |
| contactMechPurposeTypeId | String | IN | true |
| contactMechTypeId | String | IN | true |
| infoString | String | IN | true |
| newContactMechId | String | IN | true |
| workEffortId | String | IN | — |

## updateWorkEffortContent

- Definition: `applications/workeffort/servicedef/services.xml:1105`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WorkEffortContent; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| fromDate | date-time | IN | false |
| thruDate | date-time | IN | true |
| workEffortContentTypeId | id | IN | false |
| workEffortId | id | IN | false |

## updateWorkEffortContentType

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:129`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WorkEffortContentType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| parentTypeId | id | IN | true |
| workEffortContentTypeId | id | IN | — |

## updateWorkEffortEmailAddress

- Definition: `applications/workeffort/servicedef/services.xml:1501`
- Engine: groovy; location: component://workeffort/src/main/groovy/org/apache/ofbiz/workeffort/workeffort/workeffort/WorkEffortServicesScript.groovy; invoke: updateWorkEffortContactMech
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: UPDATE
- Implements: [{"service": "updateWorkEffortContactMech"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechTypeId | — | — | — |

## updateWorkEffortEventReminder

- Definition: `applications/workeffort/servicedef/services.xml:1627`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WorkEffortEventReminder; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | id | IN | true |
| currentCount | numeric | IN | true |
| localeId | id | IN | true |
| partyId | id | IN | true |
| reminderDateTime | date-time | IN | true |
| reminderOffset | numeric | IN | true |
| repeatCount | numeric | IN | true |
| repeatInterval | numeric | IN | true |
| sequenceId | id | IN | false |
| timeZoneId | id-long | IN | true |
| workEffortId | id | IN | false |

## updateWorkEffortFixedAssetAssign

- Definition: `applications/workeffort/servicedef/services.xml:1036`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WorkEffortFixedAssetAssign; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| allocatedCost | currency-amount | IN | true |
| availabilityStatusId | id | IN | true |
| comments | comment | IN | true |
| fixedAssetId | id | IN | false |
| fromDate | date-time | IN | false |
| statusId | id | IN | true |
| thruDate | date-time | IN | true |
| workEffortId | id | IN | false |

## updateWorkEffortFixedAssetStd

- Definition: `applications/workeffort/servicedef/services.xml:980`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WorkEffortFixedAssetStd; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| estimatedCost | currency-amount | IN | true |
| estimatedDuration | floating-point | IN | true |
| estimatedQuantity | floating-point | IN | true |
| fixedAssetTypeId | id | IN | false |
| workEffortId | id | IN | false |

## updateWorkEffortGoodStandard

- Definition: `applications/workeffort/servicedef/services.xml:629`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WorkEffortGoodStandard; auth: —
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| estimatedCost | currency-amount | IN | true |
| estimatedQuantity | floating-point | IN | true |
| fromDate | date-time | IN | false |
| productId | id | IN | false |
| statusId | id | IN | true |
| thruDate | date-time | IN | true |
| workEffortGoodStdTypeId | id | IN | false |
| workEffortId | id | IN | false |

## updateWorkEffortGoodStandardType

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:144`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WorkEffortGoodStandardType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| workEffortGoodStdTypeId | id | IN | — |

## updateWorkEffortICalData

- Definition: `applications/workeffort/servicedef/services.xml:1563`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WorkEffortIcalData; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortICalendarPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| icalData | very-long | IN | true |
| workEffortId | id | IN | false |

## updateWorkEffortNote

- Definition: `applications/workeffort/servicedef/services.xml:299`
- Engine: simple; location: component://workeffort/minilang/workeffort/WorkEffortSimpleServices.xml; invoke: updateWorkEffortNote
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| internalNote | String | IN | false |
| noteId | String | IN | false |
| noteInfo | String | IN | true |
| workEffortId | String | IN | false |

## updateWorkEffortPartyAssignment

- Definition: `applications/workeffort/servicedef/services.xml:193`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WorkEffortPartyAssignment; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| assignedByUserLoginId | id-vlong | IN | true |
| availabilityStatusId | id | IN | true |
| comments | comment | IN | true |
| delegateReasonEnumId | id | IN | true |
| expectationEnumId | id | IN | true |
| facilityId | id | IN | true |
| fromDate | date-time | IN | — |
| mustRsvp | indicator | IN | true |
| partyId | id | IN | — |
| roleTypeId | id | IN | — |
| statusDateTime | date-time | IN | true |
| statusId | id | IN | true |
| thruDate | date-time | IN | true |
| workEffortId | id | IN | — |

## updateWorkEffortPostalAddress

- Definition: `applications/workeffort/servicedef/services.xml:1466`
- Engine: groovy; location: component://workeffort/src/main/groovy/org/apache/ofbiz/workeffort/workeffort/workeffort/WorkEffortServicesScript.groovy; invoke: updateWorkEffortContactMech
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: UPDATE
- Implements: [{"service": "updatePostalAddress"}, {"service": "updateWorkEffortContactMech"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechTypeId | — | — | — |
| relationEntityName | String | IN | — |

## updateWorkEffortPurposeType

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:159`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WorkEffortPurposeType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| parentTypeId | id | IN | true |
| workEffortPurposeTypeId | id | IN | — |

## updateWorkEffortReview

- Definition: `applications/workeffort/servicedef/services.xml:1195`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WorkEffortReview; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| postedAnonymous | indicator | IN | true |
| rating | floating-point | IN | true |
| reviewDate | date-time | IN | false |
| reviewText | very-long | IN | true |
| statusId | id | IN | true |
| userLoginId | id-vlong | IN | false |
| workEffortId | id | IN | false |

## updateWorkEffortSkillStandard

- Definition: `applications/workeffort/servicedef/services.xml:1342`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WorkEffortSkillStandard; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| estimatedCost | currency-amount | IN | true |
| estimatedDuration | floating-point | IN | true |
| estimatedNumPeople | floating-point | IN | true |
| skillTypeId | id | IN | false |
| workEffortId | id | IN | false |

## updateWorkEffortSurveyAppl

- Definition: `applications/workeffort/servicedef/services.xml:1691`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WorkEffortSurveyAppl; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| surveyId | id | IN | false |
| thruDate | date-time | IN | true |
| workEffortId | id | IN | false |

## updateWorkEffortTelecomNumber

- Definition: `applications/workeffort/servicedef/services.xml:1485`
- Engine: groovy; location: component://workeffort/src/main/groovy/org/apache/ofbiz/workeffort/workeffort/workeffort/WorkEffortServicesScript.groovy; invoke: updateWorkEffortContactMech
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: workEffortGenericPermission; main action: UPDATE
- Implements: [{"service": "updateTelecomNumber"}, {"service": "updateWorkEffortContactMech"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechTypeId | — | — | — |
| relationEntityName | String | IN | — |

## updateWorkEffortTextContent

- Definition: `applications/workeffort/servicedef/services.xml:1166`
- Engine: group; location: —; invoke: —
- Default entity: —; auth: true
- Use transaction: —; timeout: 300
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## updateWorkEffortType

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:174`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WorkEffortType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| workEffortTypeId | id | IN | — |

## updateWorkEffortTypeAttr

- Definition: `applications/workeffort/servicedef/services_workeffort.xml:189`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WorkEffortTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| description | description | IN | true |
| workEffortTypeId | id | IN | — |

## uploadWorkEffortContentFile

- Definition: `applications/workeffort/servicedef/services.xml:1174`
- Engine: group; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: 300
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## workEffortGenericPermission

- Definition: `applications/workeffort/servicedef/services.xml:1314`
- Engine: simple; location: component://workeffort/minilang/permission/WorkEffortPermissionServices.xml; invoke: workEffortGenericPermission
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| workEffortId | String | IN | true |
| workEffortParentId | String | IN | true |

## workEffortICalendarPermission

- Definition: `applications/workeffort/servicedef/services.xml:1584`
- Engine: simple; location: component://workeffort/minilang/permission/WorkEffortPermissionServices.xml; invoke: workEffortICalendarPermission
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| workEffortId | String | IN | true |

## workEffortManagerPermission

- Definition: `applications/workeffort/servicedef/services.xml:1310`
- Engine: simple; location: component://workeffort/minilang/permission/WorkEffortPermissionServices.xml; invoke: workEffortManagerPermission
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
