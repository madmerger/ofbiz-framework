# Entity inventory

## TimeEntry

- Package: org.apache.ofbiz.workeffort.timesheet
- Table: `TIME_ENTRY`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:42`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| timeEntryId | id | — | Y |
| partyId | id | — | N |
| fromDate | date-time | — | N |
| thruDate | date-time | — | N |
| rateTypeId | id | — | N |
| workEffortId | id | — | N |
| timesheetId | id | — | N |
| invoiceId | id | — | N |
| invoiceItemSeqId | id | — | N |
| hours | floating-point | — | N |
| comments | comment | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | RateType (external) | — | rateTypeId → — | applications/datamodel/entitydef/accounting-entitymodel.xml |
| one | WorkEffort | — | workEffortId → — | — |
| one | Timesheet | — | timesheetId → — | — |
| one-nofk | Invoice (external) | — | invoiceId → — | applications/datamodel/entitydef/accounting-entitymodel.xml |
| one | InvoiceItem (external) | — | invoiceId → —, invoiceItemSeqId → — | applications/datamodel/entitydef/accounting-entitymodel.xml |

## Timesheet

- Package: org.apache.ofbiz.workeffort.timesheet
- Table: `TIMESHEET`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:77`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| timesheetId | id | — | Y |
| partyId | id | — | N |
| clientPartyId | id | — | N |
| fromDate | date-time | — | N |
| thruDate | date-time | — | N |
| statusId | id | — | N |
| approvedByUserLoginId | id-vlong | — | N |
| comments | comment | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | Party (external) | Client | clientPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |
| one | UserLogin (external) | ApprovedBy | approvedByUserLoginId → userLoginId | framework/security/entitydef/entitymodel.xml |

## TimesheetRole

- Package: org.apache.ofbiz.workeffort.timesheet
- Table: `TIMESHEET_ROLE`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:102`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| timesheetId | id | — | Y |
| partyId | id | — | Y |
| roleTypeId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Timesheet | — | timesheetId → — | — |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | RoleType (external) | — | roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PartyRole (external) | — | partyId → —, roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |

## ApplicationSandbox

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `APPLICATION_SANDBOX`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:130`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| applicationId | id | — | Y |
| workEffortId | id | — | N |
| partyId | id | — | N |
| roleTypeId | id | — | N |
| fromDate | date-time | — | N |
| runtimeDataId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffortPartyAssignment | — | workEffortId → —, partyId → —, roleTypeId → —, fromDate → — | — |
| one | RuntimeData (external) | — | runtimeDataId → — | framework/service/entitydef/entitymodel.xml |

## CommunicationEventWorkEff

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `COMMUNICATION_EVENT_WORK_EFF`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:150`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortId | id | — | Y |
| communicationEventId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffort | — | workEffortId → — | — |
| one | CommunicationEvent (external) | — | communicationEventId → — | applications/datamodel/entitydef/party-entitymodel.xml |

## Deliverable

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `DELIVERABLE`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:165`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| deliverableId | id | — | Y |
| deliverableTypeId | id | — | N |
| deliverableName | name | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | DeliverableType | — | deliverableTypeId → — | — |

## DeliverableType

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `DELIVERABLE_TYPE`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:177`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| deliverableTypeId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## WorkEffort

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:184`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortId | id | — | Y |
| workEffortTypeId | id | — | N |
| currentStatusId | id | — | N |
| lastStatusUpdate | date-time | — | N |
| workEffortPurposeTypeId | id | — | N |
| workEffortParentId | id | — | N |
| scopeEnumId | id | — | N |
| priority | numeric | — | N |
| percentComplete | numeric | — | N |
| workEffortName | name | — | N |
| showAsEnumId | id | — | N |
| sendNotificationEmail | indicator | — | N |
| description | description | — | N |
| locationDesc | description | — | N |
| estimatedStartDate | date-time | — | N |
| estimatedCompletionDate | date-time | — | N |
| actualStartDate | date-time | — | N |
| actualCompletionDate | date-time | — | N |
| estimatedMilliSeconds | floating-point | — | N |
| estimatedSetupMillis | floating-point | — | N |
| estimateCalcMethod | id | — | N |
| actualMilliSeconds | floating-point | — | N |
| actualSetupMillis | floating-point | — | N |
| totalMilliSecondsAllowed | floating-point | — | N |
| totalMoneyAllowed | currency-amount | — | N |
| moneyUomId | id | — | N |
| specialTerms | long-varchar | — | N |
| timeTransparency | numeric | — | N |
| universalId | short-varchar | — | N |
| sourceReferenceId | id-long | — | N |
| fixedAssetId | id | — | N |
| facilityId | id | — | N |
| infoUrl | long-varchar | — | N |
| recurrenceInfoId | id | — | N |
| tempExprId | id | — | N |
| runtimeDataId | id | — | N |
| noteId | id | — | N |
| serviceLoaderName | name | — | N |
| quantityToProduce | fixed-point | — | N |
| quantityProduced | fixed-point | — | N |
| quantityRejected | fixed-point | — | N |
| reservPersons | fixed-point | — | N |
| reserv2ndPPPerc | fixed-point | — | N |
| reservNthPPPerc | fixed-point | — | N |
| accommodationMapId | id | — | N |
| accommodationSpotId | id | — | N |
| revisionNumber | numeric | — | N |
| createdDate | date-time | — | N |
| createdByUserLogin | id-vlong | — | N |
| lastModifiedDate | date-time | — | N |
| lastModifiedByUserLogin | id-vlong | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffortType | — | workEffortTypeId → — | — |
| one | WorkEffortPurposeType | — | workEffortPurposeTypeId → — | — |
| one | WorkEffort | Parent | workEffortParentId → workEffortId | — |
| many | WorkEffortTypeAttr | — | workEffortTypeId → — | — |
| one | StatusItem (external) | Current | currentStatusId → statusId | framework/common/entitydef/entitymodel.xml |
| one | Enumeration (external) | Scope | scopeEnumId → enumId | framework/common/entitydef/entitymodel.xml |
| one | FixedAsset (external) | — | fixedAssetId → — | applications/datamodel/entitydef/accounting-entitymodel.xml |
| one | Facility (external) | — | facilityId → — | applications/datamodel/entitydef/product-entitymodel.xml |
| one | Uom (external) | Money | moneyUomId → uomId | framework/common/entitydef/entitymodel.xml |
| one | RecurrenceInfo (external) | — | recurrenceInfoId → — | framework/service/entitydef/entitymodel.xml |
| one | TemporalExpression (external) | — | tempExprId → — | framework/service/entitydef/entitymodel.xml |
| one | RuntimeData (external) | — | runtimeDataId → — | framework/service/entitydef/entitymodel.xml |
| one | NoteData (external) | — | noteId → — | framework/common/entitydef/entitymodel.xml |
| one | CustomMethod (external) | — | estimateCalcMethod → customMethodId | framework/common/entitydef/entitymodel.xml |
| one | AccommodationMap (external) | — | accommodationMapId → — | applications/datamodel/entitydef/accounting-entitymodel.xml |
| one | AccommodationSpot (external) | — | accommodationSpotId → — | applications/datamodel/entitydef/accounting-entitymodel.xml |

## WorkEffortAssoc

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_ASSOC`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:288`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortIdFrom | id | — | Y |
| workEffortIdTo | id | — | Y |
| workEffortAssocTypeId | id | — | Y |
| sequenceNum | numeric | — | N |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffortAssocType | — | workEffortAssocTypeId → — | — |
| many | WorkEffortAssocTypeAttr | — | workEffortAssocTypeId → — | — |
| one | WorkEffort | From | workEffortIdFrom → workEffortId | — |
| one | WorkEffort | To | workEffortIdTo → workEffortId | — |

## WorkEffortAssocAttribute

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_ASSOC_ATTRIBUTE`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:314`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortIdFrom | id | — | Y |
| workEffortIdTo | id | — | Y |
| workEffortAssocTypeId | id | — | Y |
| fromDate | date-time | — | N |
| attrName | id-long | — | Y |
| attrValue | value | — | N |
| attrDescription | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffortAssoc | — | workEffortIdFrom → —, workEffortIdTo → —, workEffortAssocTypeId → —, fromDate → — | — |
| many | WorkEffortAssocTypeAttr | — | attrName → — | — |

## WorkEffortAssocType

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_ASSOC_TYPE`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:338`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortAssocTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffortAssocType | Parent | parentTypeId → workEffortAssocTypeId | — |

## WorkEffortAssocTypeAttr

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_ASSOC_TYPE_ATTR`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:351`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortAssocTypeId | id | — | Y |
| attrName | id-long | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffortAssocType | — | workEffortAssocTypeId → — | — |
| many | WorkEffortAssocAttribute | — | attrName → — | — |
| many | WorkEffortAssoc | — | workEffortAssocTypeId → — | — |

## WorkEffortAttribute

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_ATTRIBUTE`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:370`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortId | id | — | Y |
| attrName | id-long | — | Y |
| attrValue | value | — | N |
| attrDescription | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffort | — | workEffortId → — | — |
| many | WorkEffortTypeAttr | — | attrName → — | — |

## WorkEffortBilling

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_BILLING`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:386`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortId | id | — | Y |
| invoiceId | id | — | Y |
| invoiceItemSeqId | id | — | Y |
| percentage | floating-point | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffort | — | workEffortId → — | — |
| one-nofk | Invoice (external) | — | invoiceId → — | applications/datamodel/entitydef/accounting-entitymodel.xml |
| one | InvoiceItem (external) | — | invoiceId → —, invoiceItemSeqId → — | applications/datamodel/entitydef/accounting-entitymodel.xml |

## WorkEffortContactMech

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_CONTACT_MECH`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:407`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortId | id | — | Y |
| contactMechId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| comments | comment | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffort | — | workEffortId → — | — |
| one | ContactMech (external) | — | contactMechId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | TelecomNumber (external) | — | contactMechId → — | applications/datamodel/entitydef/party-entitymodel.xml |

## WorkEffortContent

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_CONTENT`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:428`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortId | id | — | Y |
| contentId | id | — | Y |
| workEffortContentTypeId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffort | — | workEffortId → — | — |
| one | Content (external) | — | contentId → — | applications/datamodel/entitydef/content-entitymodel.xml |
| one | WorkEffortContentType | — | workEffortContentTypeId → — | — |

## WorkEffortContentType

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_CONTENT_TYPE`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:448`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortContentTypeId | id | — | Y |
| parentTypeId | id | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffortContentType | Parent | parentTypeId → workEffortContentTypeId | — |

## WorkEffortDeliverableProd

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_DELIVERABLE_PROD`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:457`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortId | id | — | Y |
| deliverableId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffort | — | workEffortId → — | — |
| one | Deliverable | — | deliverableId → — | — |

## WorkEffortEventReminder

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_EVENT_REMINDER`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:471`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortId | id | — | Y |
| sequenceId | id | — | Y |
| contactMechId | id | — | N |
| partyId | id | — | N |
| reminderDateTime | date-time | — | N |
| repeatCount | numeric | — | N |
| repeatInterval | numeric | — | N |
| currentCount | numeric | — | N |
| reminderOffset | numeric | — | N |
| localeId | id | — | N |
| timeZoneId | id-long | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffort | — | workEffortId → — | — |
| one | ContactMech (external) | — | contactMechId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |

## WorkEffortFixedAssetAssign

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_FIXED_ASSET_ASSIGN`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:503`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortId | id | — | Y |
| fixedAssetId | id | — | Y |
| statusId | id | — | N |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| availabilityStatusId | id | — | N |
| allocatedCost | currency-amount | — | N |
| comments | comment | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffort | — | workEffortId → — | — |
| one | FixedAsset (external) | — | fixedAssetId → — | applications/datamodel/entitydef/accounting-entitymodel.xml |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |
| one | StatusItem (external) | Availability | availabilityStatusId → statusId | framework/common/entitydef/entitymodel.xml |

## WorkEffortFixedAssetStd

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_FIXED_ASSET_STD`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:530`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortId | id | — | Y |
| fixedAssetTypeId | id | — | Y |
| estimatedQuantity | floating-point | — | N |
| estimatedDuration | floating-point | — | N |
| estimatedCost | currency-amount | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffort | — | workEffortId → — | — |
| one | FixedAssetType (external) | — | fixedAssetTypeId → — | applications/datamodel/entitydef/accounting-entitymodel.xml |

## WorkEffortGoodStandard

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_GOOD_STANDARD`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:547`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortId | id | — | Y |
| productId | id | — | Y |
| workEffortGoodStdTypeId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| statusId | id | — | N |
| estimatedQuantity | floating-point | — | N |
| estimatedCost | currency-amount | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffort | — | workEffortId → — | — |
| one | WorkEffortGoodStandardType | — | workEffortGoodStdTypeId → — | — |
| one | Product (external) | — | productId → — | applications/datamodel/entitydef/product-entitymodel.xml |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |

## WorkEffortGoodStandardType

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_GOOD_STANDARD_TYPE`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:573`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortGoodStdTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffortGoodStandardType | Parent | parentTypeId → workEffortGoodStdTypeId | — |

## WorkEffortIcalData

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_ICAL_DATA`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:585`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortId | id | — | Y |
| icalData | very-long | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffort | — | workEffortId → — | — |

## WorkEffortInvRes

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_INV_RES`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:597`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortId | id | — | Y |
| inventoryItemId | id | — | Y |
| productId | id | WEGS_PRODUCT_ID | Y |
| reserveOrderEnumId | id | — | N |
| quantity | fixed-point | — | N |
| quantityNotAvailable | fixed-point | — | N |
| reservedDatetime | date-time | — | N |
| createdDatetime | date-time | — | N |
| promisedDatetime | date-time | — | N |
| currentPromisedDate | date-time | — | N |
| sequenceId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffort | — | workEffortId → — | — |
| one | InventoryItem (external) | — | inventoryItemId → — | applications/datamodel/entitydef/product-entitymodel.xml |
| one | Product (external) | — | productId → — | applications/datamodel/entitydef/product-entitymodel.xml |

## WorkEffortInventoryAssign

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_INVENTORY_ASSIGN`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:624`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortId | id | — | Y |
| inventoryItemId | id | — | Y |
| statusId | id | — | N |
| quantity | floating-point | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffort | — | workEffortId → — | — |
| one | InventoryItem (external) | — | inventoryItemId → — | applications/datamodel/entitydef/product-entitymodel.xml |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |

## WorkEffortInventoryProduced

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_INVENTORY_PRODUCED`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:643`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortId | id | — | Y |
| inventoryItemId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffort | — | workEffortId → — | — |
| one | InventoryItem (external) | — | inventoryItemId → — | applications/datamodel/entitydef/product-entitymodel.xml |

## WorkEffortCostCalc

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_COST_CALC`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:658`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortId | id | — | Y |
| costComponentTypeId | id | — | Y |
| costComponentCalcId | id | — | N |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffort | — | workEffortId → — | — |
| one | CostComponentType (external) | — | costComponentTypeId → — | applications/datamodel/entitydef/product-entitymodel.xml |
| one | CostComponentCalc (external) | — | costComponentCalcId → — | applications/datamodel/entitydef/product-entitymodel.xml |

## WorkEffortKeyword

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_KEYWORD`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:680`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortId | id | — | Y |
| keyword | short-varchar | — | Y |
| relevancyWeight | numeric | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffort | — | workEffortId → — | — |

## WorkEffortNote

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_NOTE`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:693`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortId | id | — | Y |
| noteId | id | — | Y |
| internalNote | indicator | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffort | — | workEffortId → — | — |
| one | NoteData (external) | — | noteId → — | framework/common/entitydef/entitymodel.xml |

## WorkEffortPartyAssignment

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_PARTY_ASSIGNMENT`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:708`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortId | id | — | Y |
| partyId | id | — | Y |
| roleTypeId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| assignedByUserLoginId | id-vlong | — | N |
| statusId | id | — | N |
| statusDateTime | date-time | — | N |
| expectationEnumId | id | — | N |
| delegateReasonEnumId | id | — | N |
| facilityId | id | — | N |
| comments | comment | — | N |
| mustRsvp | indicator | — | N |
| availabilityStatusId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffort | — | workEffortId → — | — |
| one-nofk | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PartyRole (external) | — | partyId → —, roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | RoleType (external) | — | roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | UserLogin (external) | AssignedBy | assignedByUserLoginId → userLoginId | framework/security/entitydef/entitymodel.xml |
| one | StatusItem (external) | Assignment | statusId → — | framework/common/entitydef/entitymodel.xml |
| one | Enumeration (external) | Expectation | expectationEnumId → enumId | framework/common/entitydef/entitymodel.xml |
| one | Enumeration (external) | DelegateReason | delegateReasonEnumId → enumId | framework/common/entitydef/entitymodel.xml |
| one | Facility (external) | — | facilityId → — | applications/datamodel/entitydef/product-entitymodel.xml |
| one | StatusItem (external) | Availability | availabilityStatusId → statusId | framework/common/entitydef/entitymodel.xml |

## WorkEffortPurposeType

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_PURPOSE_TYPE`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:759`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortPurposeTypeId | id | — | Y |
| parentTypeId | id | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffortPurposeType | Parent | parentTypeId → workEffortPurposeTypeId | — |

## WorkEffortReview

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_REVIEW`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:769`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortId | id | — | Y |
| userLoginId | id-vlong | — | Y |
| reviewDate | date-time | — | Y |
| statusId | id | — | N |
| postedAnonymous | indicator | — | N |
| rating | floating-point | — | N |
| reviewText | very-long | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffort | — | workEffortId → — | — |
| one | UserLogin (external) | — | userLoginId → — | framework/security/entitydef/entitymodel.xml |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |

## WorkEffortSearchConstraint

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_SEARCH_CONSTRAINT`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:790`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortSearchResultId | id | — | Y |
| constraintSeqId | id | — | Y |
| constraintName | long-varchar | — | N |
| infoString | long-varchar | — | N |
| includeSubWorkEfforts | indicator | — | N |
| isAnd | indicator | — | N |
| anyPrefix | indicator | — | N |
| anySuffix | indicator | — | N |
| removeStems | indicator | — | N |
| lowValue | short-varchar | — | N |
| highValue | short-varchar | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffortSearchResult | — | workEffortSearchResultId → — | — |

## WorkEffortSearchResult

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_SEARCH_RESULT`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:808`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortSearchResultId | id | — | Y |
| visitId | id | — | N |
| orderByName | long-varchar | — | N |
| isAscending | indicator | — | N |
| numResults | numeric | — | N |
| secondsTotal | floating-point | — | N |
| searchDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## WorkEffortSkillStandard

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_SKILL_STANDARD`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:818`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortId | id | — | Y |
| skillTypeId | id | — | Y |
| estimatedNumPeople | floating-point | — | N |
| estimatedDuration | floating-point | — | N |
| estimatedCost | currency-amount | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffort | — | workEffortId → — | — |
| one | SkillType (external) | — | skillTypeId → — | applications/datamodel/entitydef/humanres-entitymodel.xml |

## WorkEffortStatus

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_STATUS`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:835`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortId | id | — | Y |
| statusId | id | — | Y |
| statusDatetime | date-time | — | Y |
| setByUserLogin | id-vlong | — | N |
| reason | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffort | — | workEffortId → — | — |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |
| one | UserLogin (external) | SetBy | setByUserLogin → userLoginId | framework/security/entitydef/entitymodel.xml |

## WorkEffortTransBox

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_TRANS_BOX`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:856`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| processWorkEffortId | id | — | Y |
| toActivityId | id-long | — | Y |
| transitionId | id-long | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffort | — | processWorkEffortId → workEffortId | — |

## WorkEffortType

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_TYPE`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:869`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffortType | Parent | parentTypeId → workEffortTypeId | — |

## WorkEffortTypeAttr

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_TYPE_ATTR`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:882`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortTypeId | id | — | Y |
| attrName | id-long | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WorkEffortType | — | workEffortTypeId → — | — |
| many | WorkEffortAttribute | — | attrName → — | — |
| many | WorkEffort | — | workEffortTypeId → — | — |

## WorkEffortSurveyAppl

- Package: org.apache.ofbiz.workeffort.workeffort
- Table: `WORK_EFFORT_SURVEY_APPL`
- Source: `applications/datamodel/entitydef/workeffort-entitymodel.xml:900`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| workEffortId | id | — | Y |
| surveyId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Survey (external) | — | surveyId → — | applications/datamodel/entitydef/content-entitymodel.xml |
| one | WorkEffort | — | workEffortId → — | — |
| one | ProductStoreSurveyAppl (external) | — | surveyId → productStoreSurveyId | applications/datamodel/entitydef/product-entitymodel.xml |

## View entities
