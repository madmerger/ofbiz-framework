# Service 棚卸し

## addProductManufacturingRule

- Definition: `applications/manufacturing/servicedef/services_bom.xml:57`
- Engine: simple; location: component://manufacturing/minilang/bom/BomSimpleMethods.xml; invoke: addProductManufacturingRule
- Default entity: ProductManufacturingRule; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| 名前 | 型 | モード | 任意 |
| --- | --- | --- | --- |
| description | description | IN | true |
| fromDate | date-time | IN | true |
| productFeature | id | IN | true |
| productId | id | IN | true |
| productIdFor | id | IN | true |
| productIdIn | id | IN | false |
| productIdInSubst | id | IN | true |
| quantity | floating-point | IN | true |
| ruleId | id | IN | true |
| ruleOperator | id | IN | true |
| ruleSeqId | id | IN | true |
| thruDate | date-time | IN | true |

## addProductionRunComponent

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:228`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: addProductionRunComponent
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| estimatedQuantity | BigDecimal | IN | — |
| productId | String | IN | false |
| productionRunId | String | IN | false |
| workEffortId | String | IN | true |

## addProductionRunRoutingTask

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:198`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: addProductionRunRoutingTask
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | String | IN | true |
| estimatedCompletionDate | Timestamp | INOUT | true |
| estimatedMilliSeconds | BigDecimal | IN | true |
| estimatedSetupMillis | BigDecimal | IN | true |
| estimatedStartDate | Timestamp | INOUT | true |
| priority | Long | IN | false |
| productionRunId | String | IN | false |
| routingTaskId | String | INOUT | false |
| workEffortName | String | IN | true |

## autoReserveWorkEffortInventory

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:510`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunResServices.groovy; invoke: autoReserveWorkEffortInventory
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| productionRunId | String | IN | true |
| reserveOrderEnumId | String | IN | true |
| workEffortId | String | IN | true |

## cancelProductionRun

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:451`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: cancelProductionRun
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| productionRunId | String | IN | false |

## changeProductionRunStatus

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:181`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: changeProductionRunStatus
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| newStatusId | String | OUT | false |
| productionRunId | String | IN | false |
| statusId | String | IN | true |

## changeProductionRunTaskStatus

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:188`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: changeProductionRunTaskStatus
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| issueAllComponents | Boolean | IN | true |
| newStatusId | String | OUT | false |
| oldStatusId | String | OUT | false |
| productionRunId | String | IN | false |
| statusId | String | IN | true |
| workEffortId | String | IN | false |

## checkDecomposeInventoryItem

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:391`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: checkDecomposeInventoryItem
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| inventoryItemId | String | IN | true |

## checkProductionRunFacilityAutoReserve

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:517`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunResServices.groovy; invoke: checkProductionRunFacilityAutoReserve
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| conditionReply | Boolean | OUT | false |
| productionRunId | String | IN | true |
| serviceContext | Map | IN | true |
| workEffortId | String | IN | true |

## checkProductionRunFacilityRecon

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:596`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunResServices.groovy; invoke: checkProductionRunFacilityRecon
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| conditionReply | Boolean | OUT | true |
| facilityId | String | OUT | true |
| inventoryItemId | String | IN | true |
| productId | String | OUT | true |
| serviceContext | Map | IN | true |

## checkRoutingTaskAssoc

- Definition: `applications/manufacturing/servicedef/services_routing.xml:35`
- Engine: java; location: org.apache.ofbiz.manufacturing.techdata.TechDataServices; invoke: checkRoutingTaskAssoc
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| create | String | IN | true |
| sequenceNum | Long | IN | true |
| sequenceNumNotOk | String | OUT | false |
| thruDate | java.sql.Timestamp | IN | true |

## checkUpdatePrunRoutingTask

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:211`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: checkUpdatePrunRoutingTask
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| estimatedMilliSeconds | BigDecimal | IN | — |
| estimatedSetupMillis | BigDecimal | IN | — |
| estimatedStartDate | Timestamp | IN | — |
| priority | Long | IN | — |
| productionRunId | String | IN | false |
| routingTaskId | String | IN | false |

## copyBOMAssocs

- Definition: `applications/manufacturing/servicedef/services_bom.xml:36`
- Engine: simple; location: component://manufacturing/minilang/bom/BomSimpleMethods.xml; invoke: copyBOMAssocs
- Default entity: ProductAssoc; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| copyToProductId | String | IN | false |
| fromDate | date-time | IN | true |
| productAssocTypeId | id | IN | false |
| productId | id | IN | false |
| productIdTo | id | IN | true |

## createBOMAssoc

- Definition: `applications/manufacturing/servicedef/services_bom.xml:28`
- Engine: simple; location: component://manufacturing/minilang/bom/BomSimpleMethods.xml; invoke: createBOMAssoc
- Default entity: ProductAssoc; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| estimateCalcMethod | id | IN | true |
| fromDate | date-time | IN | true |
| instruction | long-varchar | IN | true |
| productAssocTypeId | id | IN | false |
| productId | id | IN | false |
| productIdTo | id | IN | false |
| quantity | fixed-point | IN | true |
| reason | long-varchar | IN | true |
| recurrenceInfoId | id | IN | true |
| routingWorkEffortId | id | IN | true |
| scrapFactor | fixed-point | IN | true |
| sequenceNum | numeric | IN | true |
| thruDate | date-time | IN | true |

## createCalendar

- Definition: `applications/manufacturing/servicedef/services_calendar.xml:27`
- Engine: entity-auto; location: —; invoke: create
- Default entity: TechDataCalendar; auth: —
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| calendarId | id | IN | — |
| calendarWeekId | id | IN | true |
| description | description | IN | true |

## createCalendarExceptionDay

- Definition: `applications/manufacturing/servicedef/services_calendar.xml:72`
- Engine: entity-auto; location: —; invoke: create
- Default entity: TechDataCalendarExcDay; auth: —
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| calendarId | id | IN | — |
| description | description | IN | true |
| exceptionCapacity | fixed-point | IN | true |
| exceptionDateStartTime | date-time | IN | — |
| usedCapacity | fixed-point | IN | true |

## createCalendarExceptionWeek

- Definition: `applications/manufacturing/servicedef/services_calendar.xml:90`
- Engine: entity-auto; location: —; invoke: create
- Default entity: TechDataCalendarExcWeek; auth: —
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| calendarId | id | IN | — |
| calendarWeekId | id | IN | true |
| description | description | IN | true |
| exceptionDateStart | date | IN | — |

## createCalendarWeek

- Definition: `applications/manufacturing/servicedef/services_calendar.xml:46`
- Engine: entity-auto; location: —; invoke: create
- Default entity: TechDataCalendarWeek; auth: —
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| calendarWeekId | id | IN | — |
| description | description | IN | true |
| fridayCapacity | floating-point | IN | true |
| fridayStartTime | time | IN | true |
| mondayCapacity | floating-point | IN | true |
| mondayStartTime | time | IN | true |
| saturdayCapacity | floating-point | IN | true |
| saturdayStartTime | time | IN | true |
| sundayCapacity | floating-point | IN | true |
| sundayStartTime | time | IN | true |
| thursdayCapacity | floating-point | IN | true |
| thursdayStartTime | time | IN | true |
| tuesdayCapacity | floating-point | IN | true |
| tuesdayStartTime | time | IN | true |
| wednesdayCapacity | floating-point | IN | true |
| wednesdayStartTime | time | IN | true |

## createMrpEvent

- Definition: `applications/manufacturing/servicedef/services_mrp.xml:108`
- Engine: java; location: org.apache.ofbiz.manufacturing.mrp.InventoryEventPlannedServices; invoke: createMrpEvent
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| eventDate | Timestamp | IN | false |
| eventName | String | IN | true |
| facilityId | String | IN | true |
| mrpEventTypeId | String | IN | false |
| mrpId | String | IN | false |
| productId | String | IN | false |
| quantity | BigDecimal | IN | false |

## createMrpEventType

- Definition: `applications/manufacturing/servicedef/services_mrp.xml:124`
- Engine: entity-auto; location: —; invoke: create
- Default entity: MrpEventType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| mrpEventTypeId | id | INOUT | true |

## createMrpRunLog

- Definition: `applications/manufacturing/servicedef/services_mrp.xml:41`
- Engine: entity-auto; location: —; invoke: create
- Default entity: MrpRunLog; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| defaultYearsOffset | numeric | IN | true |
| durationMillis | numeric | IN | true |
| facilityGroupId | id | IN | true |
| facilityId | id | IN | true |
| failureMessage | very-long | IN | true |
| failureReason | name | IN | true |
| finishedAt | date-time | IN | true |
| jobId | id | IN | true |
| mrpEventCount | numeric | IN | true |
| mrpId | id | IN | true |
| mrpName | name | IN | true |
| mrpRunLogId | id | INOUT | false |
| outcomeMessage | very-long | IN | true |
| proposedRequirementCount | numeric | IN | true |
| runByUserLoginId | id-vlong | IN | true |
| runStatusId | id | IN | true |
| startedAt | date-time | IN | true |

## createMrpRunLogAndTrackerBeforeQueueingMrp

- Definition: `applications/manufacturing/servicedef/services_mrp.xml:51`
- Engine: java; location: org.apache.ofbiz.manufacturing.mrp.MrpServices; invoke: createMrpRunLogAndTrackerBeforeQueueingMrp
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| defaultYearsOffset | Integer | IN | true |
| facilityGroupId | String | IN | true |
| facilityId | String | IN | true |
| jobTrackerId | String | IN | true |
| mrpName | String | IN | true |
| mrpRunLogId | String | INOUT | true |
| runStatusId | String | IN | true |
| startedAt | Timestamp | IN | true |

## createProductionRun

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:82`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: createProductionRun
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | String | IN | true |
| estimatedCompletionDate | java.sql.Timestamp | OUT | true |
| facilityId | String | IN | false |
| pRQuantity | BigDecimal | IN | false |
| productId | String | IN | false |
| productionRunId | String | OUT | false |
| routingId | String | IN | true |
| startDate | java.sql.Timestamp | IN | false |
| workEffortName | String | IN | true |

## createProductionRunAssoc

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:108`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunServicesScript.groovy; invoke: createProductionRunAssoc
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| productionRunId | String | IN | false |
| productionRunIdTo | String | IN | false |
| workFlowSequenceTypeId | String | IN | false |

## createProductionRunForMktgPkg

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:159`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: createProductionRunForMktgPkg
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| facilityId | String | IN | false |
| orderId | String | IN | false |
| orderItemSeqId | String | IN | false |
| productionRunId | String | OUT | true |

## createProductionRunFromConfiguration

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:148`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: createProductionRunFromConfiguration
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| config | org.apache.ofbiz.product.config.ProductConfigWrapper | IN | true |
| configId | String | IN | true |
| facilityId | String | IN | false |
| orderId | String | IN | true |
| orderItemSeqId | String | IN | true |
| productionRunId | String | OUT | false |
| quantity | BigDecimal | IN | true |

## createProductionRunFromRequirement

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:141`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: createProductionRunFromRequirement
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| productionRunId | String | OUT | true |
| quantity | BigDecimal | IN | true |
| requirementId | String | IN | false |

## createProductionRunPartyAssign

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:96`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunServicesScript.groovy; invoke: createProductionRunPartyAssign
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | java.sql.Timestamp | INOUT | true |
| partyId | String | IN | false |
| productionRunId | String | INOUT | false |
| roleTypeId | String | IN | false |
| thruDate | Timestamp | IN | true |
| workEffortId | String | IN | true |

## createProductionRunTaskCosts

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:477`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: createProductionRunTaskCosts
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| productionRunTaskId | String | IN | false |

## createProductionRunsForOrder

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:130`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: createProductionRunsForOrder
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | String | IN | true |
| orderId | String | IN | false |
| orderItemSeqId | String | IN | true |
| productionRuns | java.util.List | OUT | — |
| quantity | BigDecimal | IN | true |
| shipGroupSeqId | String | IN | true |
| shipmentId | String | IN | true |

## createProductionRunsForProductBom

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:117`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: createProductionRunsForProductBom
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | String | IN | true |
| facilityId | String | IN | false |
| productId | String | IN | false |
| productionRunId | String | OUT | false |
| productionRuns | java.util.List | OUT | true |
| quantity | BigDecimal | IN | true |
| routingId | String | IN | true |
| startDate | java.sql.Timestamp | IN | false |
| workEffortName | String | IN | true |

## createShipmentPackages

- Definition: `applications/manufacturing/servicedef/services.xml:27`
- Engine: java; location: org.apache.ofbiz.manufacturing.bom.BOMServices; invoke: createShipmentPackages
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| shipmentId | String | IN | false |

## createWorkEffortInvRes

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:559`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WorkEffortInvRes; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| createdDatetime | date-time | IN | true |
| currentPromisedDate | date-time | IN | true |
| inventoryItemId | id | IN | false |
| productId | id | IN | false |
| promisedDatetime | date-time | IN | true |
| quantity | fixed-point | IN | true |
| quantityNotAvailable | fixed-point | IN | true |
| reserveOrderEnumId | id | IN | true |
| reservedDatetime | date-time | IN | true |
| sequenceId | id | IN | true |
| workEffortId | id | IN | false |

## decomposeInventoryItem

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:400`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: decomposeInventoryItem
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| inventoryItemId | String | IN | false |
| inventoryItemIds | List | OUT | false |
| quantity | BigDecimal | IN | true |

## deleteMrpEventType

- Definition: `applications/manufacturing/servicedef/services_mrp.xml:134`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: MrpEventType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| mrpEventTypeId | id | IN | — |

## deleteProductManufacturingRule

- Definition: `applications/manufacturing/servicedef/services_bom.xml:68`
- Engine: simple; location: component://manufacturing/minilang/bom/BomSimpleMethods.xml; invoke: deleteProductManufacturingRule
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| ruleId | String | IN | false |

## deleteProductionRunComponent

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:245`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: deleteProductionRunComponent
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| productId | String | IN | false |
| productionRunId | String | IN | false |

## deleteProductionRunRoutingTask

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:222`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: deleteProductionRunRoutingTask
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| productionRunId | String | IN | false |
| routingTaskId | String | IN | false |

## deleteWorkEffortInvRes

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:567`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WorkEffortInvRes; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| inventoryItemId | String | IN | false |
| productId | String | IN | false |
| workEffortId | String | IN | false |

## exampleComponentFormula

- Definition: `applications/manufacturing/servicedef/services_formula.xml:32`
- Engine: simple; location: component://manufacturing/minilang/bom/BomFormulas.xml; invoke: exampleComponentFormula
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "interfaceBomFormula"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## exampleTaskFormula

- Definition: `applications/manufacturing/servicedef/services_formula.xml:47`
- Engine: simple; location: component://manufacturing/minilang/techdata/TaskFormulae.xml; invoke: exampleTaskFormula
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "interfaceTaskFormula"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## executeMrp

- Definition: `applications/manufacturing/servicedef/services_mrp.xml:28`
- Engine: java; location: org.apache.ofbiz.manufacturing.mrp.MrpServices; invoke: executeMrp
- Default entity: —; auth: true
- Use transaction: —; timeout: 7200
- Permission service: manufacturingPermissionService; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| defaultYearsOffset | Integer | IN | true |
| facilityGroupId | String | IN | true |
| facilityId | String | IN | true |
| jobTrackerId | String | IN | true |
| mrpName | String | IN | true |
| mrpRunLogId | String | IN | true |
| msgResult | List | OUT | false |

## executeProductionRunTaskComponentAtomic

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:291`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunServicesScript.groovy; invoke: executeProductionRunTaskComponentAtomic
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | String | IN | true |
| failIfItemsAreNotAvailable | String | IN | true |
| failIfItemsAreNotOnHand | String | IN | true |
| inventoryItemId | String | IN | true |
| productId | String | IN | false |
| quantity | BigDecimal | IN | true |
| reasonEnumId | String | IN | true |
| reserveOrderEnumId | String | IN | true |
| workEffortId | String | IN | false |

## expireRoutingGoodStandard

- Definition: `applications/manufacturing/servicedef/services_routing.xml:147`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/RoutingCostManagementServices.groovy; invoke: expireRoutingGoodStandard
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | Timestamp | OUT | false |
| productId | String | OUT | false |
| removed | Boolean | OUT | false |
| thruDate | Timestamp | OUT | false |
| workEffortGoodStdTypeId | String | OUT | false |
| workEffortId | String | OUT | false |

## findBomProducts

- Definition: `applications/manufacturing/servicedef/services_bom.xml:150`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/BomManagementServices.groovy; invoke: findBomProducts
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| componentProductId | String | IN | true |
| hasNext | Boolean | OUT | false |
| links | Map | OUT | true |
| nextPageCount | Long | OUT | true |
| pageIndex | Integer | OUT | false |
| pageSize | Integer | OUT | false |
| previousPageCount | Long | OUT | true |
| productAssocTypeId | String | IN | true |
| productId | String | IN | true |
| productIdTo | String | IN | true |
| products | java.util.List | OUT | false |
| query | String | IN | true |
| sort | String | IN | true |
| totalCount | Long | OUT | false |
| totalPages | Long | OUT | false |

## findBomTypes

- Definition: `applications/manufacturing/servicedef/services_bom.xml:291`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/BomManagementServices.groovy; invoke: findBomTypes
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| bomTypes | java.util.List | OUT | false |
| hasNext | Boolean | OUT | false |
| links | Map | OUT | true |
| nextPageCount | Long | OUT | true |
| pageIndex | Integer | OUT | false |
| pageSize | Integer | OUT | false |
| previousPageCount | Long | OUT | true |
| sort | String | IN | true |
| totalCount | Long | OUT | false |
| totalPages | Long | OUT | false |

## findCostComponentOptions

- Definition: `applications/manufacturing/servicedef/services_lookup.xml:25`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/ManufacturingLookupServices.groovy; invoke: findCostComponentOptions
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| costComponentCalcs | java.util.List | OUT | false |
| costComponentTypes | java.util.List | OUT | false |

## findFixedAssets

- Definition: `applications/manufacturing/servicedef/services_lookup.xml:66`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/ManufacturingLookupServices.groovy; invoke: findFixedAssets
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetTypeId | String | IN | true |
| fixedAssets | java.util.List | OUT | false |
| hasNext | Boolean | OUT | false |
| links | Map | OUT | true |
| nextPageCount | Long | OUT | true |
| pageIndex | Integer | OUT | false |
| pageSize | Integer | OUT | false |
| previousPageCount | Long | OUT | true |
| sort | String | IN | true |
| totalCount | Long | OUT | false |
| totalPages | Long | OUT | false |

## findManufacturingFacilities

- Definition: `applications/manufacturing/servicedef/services_lookup.xml:32`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/ManufacturingLookupServices.groovy; invoke: findManufacturingFacilities
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| facilities | java.util.List | OUT | false |
| hasNext | Boolean | OUT | false |
| links | Map | OUT | true |
| nextPageCount | Long | OUT | true |
| pageIndex | Integer | OUT | false |
| pageSize | Integer | OUT | false |
| previousPageCount | Long | OUT | true |
| sort | String | IN | true |
| totalCount | Long | OUT | false |
| totalPages | Long | OUT | false |

## findManufacturingPlants

- Definition: `applications/manufacturing/servicedef/services_lookup.xml:49`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/ManufacturingLookupServices.groovy; invoke: findManufacturingPlants
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| facilities | java.util.List | OUT | false |
| hasNext | Boolean | OUT | false |
| links | Map | OUT | true |
| nextPageCount | Long | OUT | true |
| pageIndex | Integer | OUT | false |
| pageSize | Integer | OUT | false |
| previousPageCount | Long | OUT | true |
| sort | String | IN | true |
| totalCount | Long | OUT | false |
| totalPages | Long | OUT | false |

## findMrpEventPlans

- Definition: `applications/manufacturing/servicedef/services_mrp.xml:139`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/mrp/MrpEventPlanServices.groovy; invoke: findMrpEventPlans
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attentionOnly | String | IN | true |
| attentionType | String | IN | true |
| dateFrom | String | IN | true |
| dateTo | String | IN | true |
| eventDate | String | IN | true |
| eventPlans | java.util.List | OUT | false |
| facilityGroupId | String | IN | true |
| facilityId | String | IN | true |
| hasNext | Boolean | OUT | false |
| links | Map | OUT | true |
| mrpEventTypeId | String | IN | true |
| mrpId | String | IN | true |
| nextPageCount | Long | OUT | true |
| orderBy | String | IN | true |
| pageIndex | Integer | OUT | false |
| pageSize | Integer | OUT | false |
| previousPageCount | Long | OUT | true |
| productId | String | IN | true |
| query | String | IN | true |
| search | String | IN | true |
| sort | String | IN | true |
| totalCount | Long | OUT | false |
| totalPages | Long | OUT | false |

## findMrpRuns

- Definition: `applications/manufacturing/servicedef/services_mrp.xml:169`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/mrp/MrpRunLogServices.groovy; invoke: findMrpRuns
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| daysBack | Integer | IN | true |
| fromDate | Timestamp | IN | true |
| hasNext | Boolean | OUT | false |
| jobId | String | IN | true |
| links | Map | OUT | true |
| mrpId | String | IN | true |
| nextPageCount | Long | OUT | true |
| orderBy | String | IN | true |
| pageIndex | Integer | OUT | false |
| pageSize | Integer | OUT | false |
| previousPageCount | Long | OUT | true |
| runs | java.util.List | OUT | false |
| sort | String | IN | true |
| statusId | String | IN | true |
| totalCount | Long | OUT | false |
| totalPages | Long | OUT | false |

## findPlanningFacilities

- Definition: `applications/manufacturing/servicedef/services_mrp.xml:192`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/ManufacturingLookupServices.groovy; invoke: findManufacturingFacilities
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| facilities | java.util.List | OUT | false |
| hasNext | Boolean | OUT | false |
| links | Map | OUT | true |
| nextPageCount | Long | OUT | true |
| orderBy | String | IN | true |
| pageIndex | Integer | OUT | false |
| pageSize | Integer | OUT | false |
| previousPageCount | Long | OUT | true |
| sort | String | IN | true |
| totalCount | Long | OUT | false |
| totalPages | Long | OUT | false |

## findProductLookupOptions

- Definition: `applications/manufacturing/servicedef/services_bom.xml:172`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/ManufacturingLookupServices.groovy; invoke: findProductLookupOptions
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| hasNext | Boolean | OUT | false |
| links | Map | OUT | true |
| nextPageCount | Long | OUT | true |
| pageIndex | Integer | OUT | false |
| pageSize | Integer | OUT | false |
| previousPageCount | Long | OUT | true |
| products | java.util.List | OUT | false |
| query | String | IN | true |
| sort | String | IN | true |
| totalCount | Long | OUT | false |
| totalPages | Long | OUT | false |

## findProductMrpQoh

- Definition: `applications/manufacturing/servicedef/services_mrp.xml:102`
- Engine: java; location: org.apache.ofbiz.manufacturing.mrp.MrpServices; invoke: findProductMrpQoh
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| productId | String | IN | false |
| quantityOnHandTotal | BigDecimal | OUT | false |

## findProductRoutings

- Definition: `applications/manufacturing/servicedef/services_routing.xml:72`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/RoutingCostManagementServices.groovy; invoke: findProductRoutings
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| currentStatusId | String | IN | true |
| hasNext | Boolean | OUT | false |
| links | Map | OUT | true |
| nextPageCount | Long | OUT | true |
| pageIndex | Integer | OUT | false |
| pageSize | Integer | OUT | false |
| previousPageCount | Long | OUT | true |
| productId | String | IN | true |
| query | String | IN | true |
| routingId | String | IN | true |
| routings | java.util.List | OUT | false |
| sort | String | IN | true |
| totalCount | Long | OUT | false |
| totalPages | Long | OUT | false |
| workEffortId | String | IN | true |
| workEffortName | String | IN | true |

## findProductionRuns

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:27`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunQueryServices.groovy; invoke: findProductionRuns
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| currentStatusId | String | IN | true |
| estimatedStartDateFrom | Timestamp | IN | true |
| estimatedStartDateThru | Timestamp | IN | true |
| facilityId | String | IN | true |
| hasNext | Boolean | OUT | false |
| links | Map | OUT | true |
| nextPageCount | Long | OUT | true |
| pageIndex | Integer | INOUT | true |
| pageSize | Integer | INOUT | true |
| previousPageCount | Long | OUT | true |
| productId | String | IN | true |
| productName | String | IN | true |
| productionRunId | String | IN | true |
| productionRuns | List | OUT | false |
| query | String | IN | true |
| sort | String | IN | true |
| statusId | String | IN | true |
| totalCount | Long | OUT | false |
| totalPages | Long | OUT | false |
| workEffortId | String | IN | true |
| workEffortName | String | IN | true |

## findRoutingTaskPurposeTypeOptions

- Definition: `applications/manufacturing/servicedef/services_routing.xml:95`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/RoutingCostManagementServices.groovy; invoke: findRoutingTaskPurposeTypeOptions
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| hasNext | Boolean | OUT | false |
| links | Map | OUT | true |
| nextPageCount | Long | OUT | true |
| pageIndex | Integer | OUT | false |
| pageSize | Integer | OUT | false |
| previousPageCount | Long | OUT | true |
| purposeTypes | java.util.List | OUT | false |
| sort | String | IN | true |
| totalCount | Long | OUT | false |
| totalPages | Long | OUT | false |

## findRoutingTasks

- Definition: `applications/manufacturing/servicedef/services_routing.xml:162`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/RoutingCostManagementServices.groovy; invoke: findRoutingTasks
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetId | String | IN | true |
| hasNext | Boolean | OUT | false |
| links | Map | OUT | true |
| nextPageCount | Long | OUT | true |
| pageIndex | Integer | OUT | false |
| pageSize | Integer | OUT | false |
| previousPageCount | Long | OUT | true |
| query | String | IN | true |
| routingTaskId | String | IN | true |
| sort | String | IN | true |
| tasks | java.util.List | OUT | false |
| totalCount | Long | OUT | false |
| totalPages | Long | OUT | false |
| workEffortId | String | IN | true |
| workEffortName | String | IN | true |

## findWarehouses

- Definition: `applications/manufacturing/servicedef/services_bom.xml:274`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/ManufacturingLookupServices.groovy; invoke: findWarehouses
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| facilities | java.util.List | OUT | false |
| hasNext | Boolean | OUT | false |
| links | Map | OUT | true |
| nextPageCount | Long | OUT | true |
| pageIndex | Integer | OUT | false |
| pageSize | Integer | OUT | false |
| previousPageCount | Long | OUT | true |
| sort | String | IN | true |
| totalCount | Long | OUT | false |
| totalPages | Long | OUT | false |

## getBOMTree

- Definition: `applications/manufacturing/servicedef/services_bom.xml:107`
- Engine: java; location: org.apache.ofbiz.manufacturing.bom.BOMServices; invoke: getBOMTree
- Default entity: ProductAssoc; auth: —
- Use transaction: false; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | BigDecimal | IN | true |
| bomType | String | IN | false |
| fromDate | String | IN | true |
| productId | String | IN | false |
| quantity | BigDecimal | IN | true |
| tree | org.apache.ofbiz.manufacturing.bom.BOMTree | OUT | true |
| type | Integer | IN | true |

## getBomSnapshot

- Definition: `applications/manufacturing/servicedef/services_bom.xml:190`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/BomManagementServices.groovy; invoke: getBomSnapshot
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | BigDecimal | OUT | true |
| asOfDate | Timestamp | IN | true |
| bomLevels | java.util.List | OUT | false |
| bomType | String | OUT | false |
| componentCount | Integer | OUT | false |
| components | java.util.List | OUT | false |
| currencyUomId | String | OUT | true |
| facilityId | String | OUT | true |
| fromDate | Timestamp | OUT | true |
| internalName | String | OUT | true |
| productAssocTypeId | String | OUT | false |
| productId | String | OUT | false |
| productName | String | OUT | true |
| quantity | BigDecimal | OUT | true |
| type | Integer | OUT | true |
| usedInCount | Integer | OUT | false |
| usedInParents | java.util.List | OUT | false |

## getEstimatedTaskTime

- Definition: `applications/manufacturing/servicedef/services_routing.xml:60`
- Engine: java; location: org.apache.ofbiz.manufacturing.routing.RoutingServices; invoke: getEstimatedTaskTime
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| estimatedTaskTime | Long | OUT | false |
| productId | String | IN | true |
| quantity | BigDecimal | IN | true |
| routingId | String | IN | true |
| setupTime | BigDecimal | OUT | true |
| taskId | String | IN | false |
| taskUnitTime | BigDecimal | OUT | true |

## getManufacturingComponents

- Definition: `applications/manufacturing/servicedef/services_bom.xml:119`
- Engine: java; location: org.apache.ofbiz.manufacturing.bom.BOMServices; invoke: getManufacturingComponents
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | BigDecimal | IN | true |
| components | java.util.List | OUT | true |
| componentsMap | java.util.List | OUT | true |
| excludeWIPs | Boolean | IN | true |
| fromDate | String | IN | true |
| productId | String | IN | false |
| quantity | BigDecimal | IN | true |
| workEffortId | String | OUT | true |

## getMaxDepth

- Definition: `applications/manufacturing/servicedef/services_bom.xml:74`
- Engine: java; location: org.apache.ofbiz.manufacturing.bom.BOMServices; invoke: getMaxDepth
- Default entity: ProductAssoc; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| bomType | String | IN | true |
| depth | Long | OUT | false |
| fromDate | String | IN | true |
| productId | String | IN | false |

## getNotAssembledComponents

- Definition: `applications/manufacturing/servicedef/services_bom.xml:140`
- Engine: java; location: org.apache.ofbiz.manufacturing.bom.BOMServices; invoke: getNotAssembledComponents
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | BigDecimal | IN | true |
| fromDate | String | IN | true |
| notAssembledComponents | java.util.List | OUT | — |
| productId | String | IN | false |
| quantity | BigDecimal | IN | true |

## getProductRouting

- Definition: `applications/manufacturing/servicedef/services_routing.xml:44`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/routing/RoutingServicesScript.groovy; invoke: getProductRouting
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| applicableDate | java.sql.Timestamp | IN | true |
| ignoreDefaultRouting | String | IN | true |
| productId | String | IN | false |
| routing | org.apache.ofbiz.entity.GenericValue | OUT | true |
| tasks | java.util.List | OUT | true |
| workEffortId | String | IN | true |

## getProductionRunCost

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:471`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: getProductionRunCost
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| totalCost | BigDecimal | OUT | false |
| workEffortId | String | IN | false |

## getProductionRunDetails

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:53`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunQueryServices.groovy; invoke: getProductionRunDetails
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| actualCompletionDate | Timestamp | OUT | true |
| actualStartDate | Timestamp | OUT | true |
| components | List | OUT | false |
| description | String | OUT | true |
| estimatedCompletionDate | Timestamp | OUT | true |
| estimatedStartDate | Timestamp | OUT | true |
| facilityId | String | OUT | true |
| facilityName | String | OUT | true |
| notes | List | OUT | false |
| parties | List | OUT | false |
| productId | String | OUT | true |
| productName | String | OUT | true |
| productionRunId | String | INOUT | true |
| quantity | BigDecimal | OUT | true |
| quantityProduced | BigDecimal | OUT | true |
| quantityRejected | BigDecimal | OUT | true |
| quantityUomDescription | String | OUT | true |
| quantityUomId | String | OUT | true |
| statusDescription | String | OUT | true |
| statusId | String | OUT | true |
| tasks | List | OUT | false |
| workEffortId | String | INOUT | true |
| workEffortName | String | OUT | true |

## getProductionRunTaskForceIssueImpact

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:525`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunResServices.groovy; invoke: getProductionRunTaskForceIssueImpact
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| facilityId | String | OUT | true |
| impactList | List | OUT | true |
| inventoryIssuePlan | List | OUT | true |
| policyViolation | String | OUT | true |
| productId | String | IN | true |
| reserveOrderEnumId | String | IN | true |
| totalNeededQuantity | BigDecimal | OUT | true |
| workEffortId | String | IN | — |

## getProductionRunTotResQty

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:456`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: getProductionRunTotResQty
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| productId | String | IN | false |
| reservedQuantity | BigDecimal | OUT | false |
| startDate | Timestamp | IN | true |

## getProductsInPackages

- Definition: `applications/manufacturing/servicedef/services_bom.xml:132`
- Engine: java; location: org.apache.ofbiz.manufacturing.bom.BOMServices; invoke: getProductsInPackages
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | String | IN | true |
| productId | String | IN | false |
| productsInPackages | java.util.List | OUT | false |
| quantity | BigDecimal | IN | true |

## getRoutingDetails

- Definition: `applications/manufacturing/servicedef/services_routing.xml:130`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/RoutingCostManagementServices.groovy; invoke: getRoutingDetails
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| costs | java.util.List | OUT | false |
| currentStatusId | String | OUT | true |
| description | String | OUT | true |
| operations | java.util.List | OUT | false |
| products | java.util.List | OUT | false |
| quantityToProduce | BigDecimal | OUT | true |
| routingId | String | OUT | false |
| tasks | java.util.List | OUT | false |
| workEffortId | String | OUT | false |
| workEffortName | String | OUT | true |

## getRoutingTaskAssocs

- Definition: `applications/manufacturing/servicedef/services_routing.xml:54`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/routing/RoutingServicesScript.groovy; invoke: getRoutingTaskAssocs
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| routingTaskAssocs | java.util.List | OUT | true |
| workEffortId | String | IN | false |

## getRoutingTaskDetails

- Definition: `applications/manufacturing/servicedef/services_routing.xml:184`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/RoutingCostManagementServices.groovy; invoke: getRoutingTaskDetails
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| costRates | java.util.List | OUT | false |
| costs | java.util.List | OUT | false |
| description | String | OUT | true |
| estimateCalcMethod | String | OUT | true |
| estimatedMilliSeconds | Double | OUT | true |
| estimatedSetupMillis | Double | OUT | true |
| fixedAssetId | String | OUT | true |
| fixedAssetName | String | OUT | true |
| fixedAssets | java.util.List | OUT | false |
| operators | java.util.List | OUT | false |
| outputs | java.util.List | OUT | false |
| reservPersons | BigDecimal | OUT | true |
| routingTaskId | String | OUT | false |
| routings | java.util.List | OUT | false |
| taskId | String | IN | true |
| tools | java.util.List | OUT | false |
| workCenterName | String | OUT | true |
| workEffortId | String | OUT | false |
| workEffortName | String | OUT | true |
| workEffortPurposeTypeDescription | String | OUT | true |
| workEffortPurposeTypeId | String | OUT | true |

## getTaskQuantities

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:545`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunResServices.groovy; invoke: getTaskQuantities
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | java.sql.Timestamp | IN | true |
| issuedQuantity | BigDecimal | OUT | false |
| netReservedQuantity | BigDecimal | OUT | false |
| productId | String | IN | false |
| remainingQuantity | BigDecimal | OUT | — |
| requiredQuantity | BigDecimal | OUT | false |
| reservedQuantity | BigDecimal | OUT | false |
| shortfallQuantity | BigDecimal | OUT | false |
| unaccountedQuantity | BigDecimal | OUT | — |
| workEffortId | String | IN | false |

## getWorkEffortCosts

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:463`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: getWorkEffortCosts
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| costComponents | List | OUT | false |
| totalCost | BigDecimal | OUT | false |
| totalCostNoMaterials | BigDecimal | OUT | false |
| workEffortId | String | IN | false |

## initLowLevelCode

- Definition: `applications/manufacturing/servicedef/services_bom.xml:92`
- Engine: java; location: org.apache.ofbiz.manufacturing.bom.BOMServices; invoke: initLowLevelCode
- Default entity: —; auth: —
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## initMrpEvents

- Definition: `applications/manufacturing/servicedef/services_mrp.xml:93`
- Engine: java; location: org.apache.ofbiz.manufacturing.mrp.MrpServices; invoke: initMrpEvents
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| defaultYearsOffset | Integer | IN | true |
| facilityId | String | IN | false |
| manufacturingFacilityId | String | IN | false |
| mrpId | String | IN | false |
| reInitialize | Boolean | IN | false |

## interfaceBomFormula

- Definition: `applications/manufacturing/servicedef/services_formula.xml:28`
- Engine: interface; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| arguments | java.util.Map | IN | false |
| quantity | BigDecimal | OUT | false |

## interfaceTaskFormula

- Definition: `applications/manufacturing/servicedef/services_formula.xml:43`
- Engine: interface; location: —; invoke: —
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| arguments | java.util.Map | IN | false |
| totalTime | BigDecimal | OUT | false |

## issueInventoryItemToWorkEffort

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:320`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunServicesScript.groovy; invoke: issueInventoryItemToWorkEffort
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finishedProductId | String | OUT | false |
| inventoryItem | org.apache.ofbiz.entity.GenericValue | IN | false |
| quantity | BigDecimal | IN | true |
| quantityIssued | BigDecimal | OUT | false |
| workEffortId | String | IN | false |

## issueProductionRunTask

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:251`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunServicesScript.groovy; invoke: issueProductionRunTask
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| failIfItemsAreNotAvailable | String | IN | true |
| failIfItemsAreNotOnHand | String | IN | true |
| reserveOrderEnumId | String | IN | true |
| workEffortId | String | IN | false |

## issueProductionRunTaskComponent

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:262`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunServicesScript.groovy; invoke: issueProductionRunTaskComponent
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | String | IN | true |
| failIfItemsAreNotAvailable | String | IN | true |
| failIfItemsAreNotOnHand | String | IN | true |
| fromDate | Timestamp | IN | true |
| inventoryItemId | String | IN | true |
| locationSeqId | String | IN | true |
| lotId | String | IN | true |
| productId | String | IN | false |
| quantity | BigDecimal | IN | true |
| reasonEnumId | String | IN | true |
| reserveOrderEnumId | String | IN | true |
| secondaryLocationSeqId | String | IN | true |
| workEffortId | String | IN | false |

## launchMrpRun

- Definition: `applications/manufacturing/servicedef/services_mrp.xml:66`
- Engine: java; location: org.apache.ofbiz.manufacturing.mrp.MrpServices; invoke: launchMrpRun
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| defaultYearsOffset | Integer | OUT | true |
| durationMillis | Long | OUT | true |
| facilityGroupId | String | OUT | true |
| facilityId | String | OUT | true |
| failureMessage | String | OUT | true |
| failureReason | String | OUT | true |
| finishDateTime | Timestamp | OUT | true |
| jobId | String | OUT | true |
| mrpId | String | OUT | true |
| mrpName | String | OUT | true |
| mrpRunLogId | String | OUT | false |
| runAsUser | String | OUT | true |
| runId | String | OUT | false |
| runTime | String | OUT | true |
| startDateTime | Timestamp | OUT | true |
| statusDescription | String | OUT | true |
| statusId | String | OUT | true |

## linearComponentFormula

- Definition: `applications/manufacturing/servicedef/services_formula.xml:37`
- Engine: simple; location: component://manufacturing/minilang/bom/BomFormulas.xml; invoke: linearComponentConsumptionFormula
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "interfaceBomFormula"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## lookupRoutingTask

- Definition: `applications/manufacturing/servicedef/services_routing.xml:28`
- Engine: java; location: org.apache.ofbiz.manufacturing.techdata.TechDataServices; invoke: lookupRoutingTask
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetId | String | IN | true |
| lookupResult | List | OUT | false |
| workEffortName | String | IN | true |

## manufacturingPermissionService

- Definition: `applications/manufacturing/servicedef/services_calendar.xml:65`
- Engine: groovy; location: component://common/src/main/groovy/org/apache/ofbiz/common/permission/CommonPermissionServices.groovy; invoke: genericBasePermissionCheck
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| altPermission | String | IN | true |
| primaryPermission | String | IN | true |

## productionRunDeclareAndProduce

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:347`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: productionRunDeclareAndProduce
- Default entity: —; auth: true
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| autoCreateLot | Boolean | IN | true |
| componentsLocationMap | Map | IN | true |
| createLotIfNeeded | Boolean | IN | true |
| inventoryItemIds | List | OUT | false |
| inventoryItemTypeId | String | IN | true |
| locationSeqId | String | IN | true |
| lotId | String | IN | true |
| quantity | BigDecimal | INOUT | false |
| quantityUomId | String | IN | true |
| workEffortId | String | IN | false |

## productionRunProduce

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:332`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: productionRunProduce
- Default entity: —; auth: true
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| autoCreateLot | Boolean | IN | true |
| createLotIfNeeded | Boolean | IN | true |
| inventoryItemIds | List | OUT | false |
| inventoryItemTypeId | String | IN | true |
| locationSeqId | String | IN | true |
| lotId | String | IN | true |
| quantity | BigDecimal | INOUT | true |
| quantityUomId | String | IN | true |
| workEffortId | String | IN | false |

## productionRunTaskProduce

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:363`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: productionRunTaskProduce
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| currencyUomId | String | IN | true |
| facilityId | String | IN | true |
| inventoryItemIds | List | OUT | false |
| inventoryItemTypeId | String | IN | true |
| isReturned | String | IN | true |
| locationSeqId | String | IN | true |
| lotId | String | IN | true |
| productId | String | IN | false |
| quantity | BigDecimal | IN | false |
| unitCost | BigDecimal | IN | true |
| workEffortId | String | IN | false |

## productionRunTaskReturnMaterial

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:380`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: productionRunTaskReturnMaterial
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| inventoryItemTypeId | String | IN | true |
| lotId | String | IN | true |
| productId | String | IN | false |
| quantity | BigDecimal | IN | true |
| workEffortId | String | IN | false |

## quickChangeProductionRunStatus

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:445`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: quickChangeProductionRunStatus
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| productionRunId | String | IN | false |
| statusId | String | IN | false |

## quickRunAllProductionRunTasks

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:435`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: quickRunAllProductionRunTasks
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| productionRunId | String | IN | false |

## quickRunProductionRunTask

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:429`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: quickRunProductionRunTask
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| productionRunId | String | IN | false |
| taskId | String | IN | false |

## quickStartAllProductionRunTasks

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:440`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: quickStartAllProductionRunTasks
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| productionRunId | String | IN | false |

## reallocateAndIssueInventory

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:572`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunResServices.groovy; invoke: reallocateAndIssueInventory
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| failIfItemsAreNotAvailable | String | IN | true |
| failIfItemsAreNotOnHand | String | IN | true |
| impactList | List | INOUT | true |
| inventoryIssuePlan | List | INOUT | true |
| productId | String | IN | true |
| productionRunId | String | INOUT | true |
| reserveOrderEnumId | String | IN | true |
| workEffortId | String | IN | false |

## reassignWorkEffortInventoryReservations

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:584`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunResServices.groovy; invoke: reassignWorkEffortInventoryReservations
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| facilityId | String | IN | true |
| inventoryItemId | String | IN | true |
| productId | String | IN | true |
| satisfiedCount | Integer | OUT | true |
| workEffortIds | List | IN | true |

## reconcileGlobalReservations

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:307`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunServicesScript.groovy; invoke: reconcileGlobalReservationsService
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| allowInventoryReallocation | String | IN | true |
| amountToIssue | BigDecimal | IN | true |
| currentWorkEffortId | String | IN | true |
| failAvailable | String | IN | true |
| impactedTaskIds | List | OUT | true |
| inventoryItemId | String | IN | true |
| strategy | String | IN | true |

## reconcileInventoryForProductionJobs

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:605`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunResServices.groovy; invoke: reconcileInventoryForProductionJobs
- Default entity: —; auth: true
- Use transaction: —; timeout: 3600
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## releaseProductionRunTaskComponent

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:536`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunResServices.groovy; invoke: releaseProductionRunTaskComponent
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| appendInventoryItemDetail | Boolean | IN | true |
| inventoryItemId | String | IN | false |
| productId | String | IN | false |
| quantity | BigDecimal | IN | true |
| workEffortId | String | IN | false |

## removeBomAssociation

- Definition: `applications/manufacturing/servicedef/services_bom.xml:220`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/BomManagementServices.groovy; invoke: removeBomAssociation
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| componentProductId | String | OUT | true |
| fromDate | Timestamp | OUT | true |
| parentProductId | String | OUT | true |
| productAssocTypeId | String | OUT | true |
| productId | String | OUT | false |
| productIdTo | String | OUT | true |
| removed | Boolean | OUT | false |
| thruDate | Timestamp | OUT | true |

## removeBomParentAssociation

- Definition: `applications/manufacturing/servicedef/services_bom.xml:237`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/BomManagementServices.groovy; invoke: removeBomParentAssociation
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| componentProductId | String | OUT | true |
| fromDate | Timestamp | OUT | true |
| parentProductId | String | OUT | true |
| productAssocTypeId | String | OUT | true |
| productId | String | OUT | false |
| productIdTo | String | OUT | true |
| removed | Boolean | OUT | false |
| thruDate | Timestamp | OUT | true |

## removeCalendar

- Definition: `applications/manufacturing/servicedef/services_calendar.xml:39`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/routing/RoutingServicesScript.groovy; invoke: removeCalendar
- Default entity: TechDataCalendar; auth: —
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| calendarId | id | IN | — |

## removeCalendarExceptionDay

- Definition: `applications/manufacturing/servicedef/services_calendar.xml:84`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: TechDataCalendarExcDay; auth: —
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| calendarId | id | IN | — |
| exceptionDateStartTime | date-time | IN | — |

## removeCalendarExceptionWeek

- Definition: `applications/manufacturing/servicedef/services_calendar.xml:102`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: TechDataCalendarExcWeek; auth: —
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| calendarId | id | IN | — |
| exceptionDateStart | date | IN | — |

## removeCalendarWeek

- Definition: `applications/manufacturing/servicedef/services_calendar.xml:58`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/routing/RoutingServicesScript.groovy; invoke: removeCalendarWeek
- Default entity: TechDataCalendarWeek; auth: —
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| calendarWeekId | id | IN | — |

## reserveWorkEffortInventory

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:484`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunResServices.groovy; invoke: reserveWorkEffortInventory
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| inventoryItemId | String | IN | false |
| productId | String | IN | true |
| promisedDatetime | java.sql.Timestamp | IN | true |
| quantity | BigDecimal | IN | false |
| quantityNotAvailable | BigDecimal | IN | true |
| reserveOrderEnumId | String | IN | true |
| reservedDatetime | java.sql.Timestamp | IN | true |
| sequenceId | Long | IN | true |
| workEffortId | String | IN | false |

## reserveWorkEffortInventoryItem

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:497`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunResServices.groovy; invoke: reserveWorkEffortInventoryItem
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| facilityId | String | IN | false |
| inventoryItemId | String | IN | true |
| lotId | String | IN | true |
| productId | String | IN | false |
| quantity | BigDecimal | IN | false |
| quantityNotAvailable | BigDecimal | IN | true |
| requireInventory | String | IN | true |
| reserveOrderEnumId | String | IN | true |
| workEffortId | String | IN | false |

## runBomSimulation

- Definition: `applications/manufacturing/servicedef/services_bom.xml:254`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/BomManagementServices.groovy; invoke: runBomSimulation
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| asOfDate | Timestamp | IN | true |
| availabilityRows | java.util.List | OUT | false |
| costRows | java.util.List | OUT | false |
| currencyUomId | String | IN | true |
| effectiveDate | Timestamp | IN | true |
| facilityId | String | IN | true |
| fromDate | Timestamp | IN | true |
| parameters | Map | OUT | false |
| productAssocTypeId | String | IN | true |
| productId | String | IN | false |
| quantity | BigDecimal | IN | true |
| simulationMode | String | IN | true |
| summary | Map | OUT | false |
| type | Integer | IN | true |
| warnings | java.util.List | OUT | false |

## searchDuplicatedAncestor

- Definition: `applications/manufacturing/servicedef/services_bom.xml:97`
- Engine: java; location: org.apache.ofbiz.manufacturing.bom.BOMServices; invoke: searchDuplicatedAncestor
- Default entity: ProductAssoc; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| duplicatedProductAssoc | org.apache.ofbiz.entity.GenericValue | OUT | true |
| fromDate | Timestamp | IN | true |
| productAssocTypeId | String | IN | false |
| productId | String | IN | false |
| productIdTo | String | IN | false |

## searchParties

- Definition: `applications/manufacturing/servicedef/services_lookup.xml:102`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/ManufacturingLookupServices.groovy; invoke: searchParties
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| hasNext | Boolean | OUT | false |
| links | Map | OUT | true |
| nextPageCount | Long | OUT | true |
| pageIndex | Integer | OUT | false |
| pageSize | Integer | OUT | false |
| parties | java.util.List | OUT | false |
| previousPageCount | Long | OUT | true |
| query | String | IN | true |
| sort | String | IN | true |
| totalCount | Long | OUT | false |
| totalPages | Long | OUT | false |

## searchProducts

- Definition: `applications/manufacturing/servicedef/services_lookup.xml:84`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/ManufacturingLookupServices.groovy; invoke: searchProducts
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| hasNext | Boolean | OUT | false |
| links | Map | OUT | true |
| nextPageCount | Long | OUT | true |
| pageIndex | Integer | OUT | false |
| pageSize | Integer | OUT | false |
| previousPageCount | Long | OUT | true |
| products | java.util.List | OUT | false |
| query | String | IN | true |
| sort | String | IN | true |
| totalCount | Long | OUT | false |
| totalPages | Long | OUT | false |

## searchRoutingTasksForRouting

- Definition: `applications/manufacturing/servicedef/services_routing.xml:112`
- Engine: groovy; location: component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/RoutingCostManagementServices.groovy; invoke: searchRoutingTasksForRouting
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| hasNext | Boolean | OUT | false |
| links | Map | OUT | true |
| nextPageCount | Long | OUT | true |
| pageIndex | Integer | OUT | false |
| pageSize | Integer | OUT | false |
| previousPageCount | Long | OUT | true |
| query | String | IN | true |
| sort | String | IN | true |
| tasks | java.util.List | OUT | false |
| totalCount | Long | OUT | false |
| totalPages | Long | OUT | false |

## setEstimatedDeliveryDates

- Definition: `applications/manufacturing/servicedef/services_mrp.xml:119`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: setEstimatedDeliveryDates
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## updateCalendar

- Definition: `applications/manufacturing/servicedef/services_calendar.xml:33`
- Engine: entity-auto; location: —; invoke: update
- Default entity: TechDataCalendar; auth: —
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| calendarId | id | IN | — |
| calendarWeekId | id | IN | true |
| description | description | IN | true |

## updateCalendarExceptionDay

- Definition: `applications/manufacturing/servicedef/services_calendar.xml:78`
- Engine: entity-auto; location: —; invoke: update
- Default entity: TechDataCalendarExcDay; auth: —
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| calendarId | id | IN | — |
| description | description | IN | true |
| exceptionCapacity | fixed-point | IN | true |
| exceptionDateStartTime | date-time | IN | — |
| usedCapacity | fixed-point | IN | true |

## updateCalendarExceptionWeek

- Definition: `applications/manufacturing/servicedef/services_calendar.xml:96`
- Engine: entity-auto; location: —; invoke: update
- Default entity: TechDataCalendarExcWeek; auth: —
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| calendarId | id | IN | — |
| calendarWeekId | id | IN | true |
| description | description | IN | true |
| exceptionDateStart | date | IN | — |

## updateCalendarWeek

- Definition: `applications/manufacturing/servicedef/services_calendar.xml:52`
- Engine: entity-auto; location: —; invoke: update
- Default entity: TechDataCalendarWeek; auth: —
- Use transaction: —; timeout: —
- Permission service: manufacturingPermissionService; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| calendarWeekId | id | IN | — |
| description | description | IN | true |
| fridayCapacity | floating-point | IN | true |
| fridayStartTime | time | IN | true |
| mondayCapacity | floating-point | IN | true |
| mondayStartTime | time | IN | true |
| saturdayCapacity | floating-point | IN | true |
| saturdayStartTime | time | IN | true |
| sundayCapacity | floating-point | IN | true |
| sundayStartTime | time | IN | true |
| thursdayCapacity | floating-point | IN | true |
| thursdayStartTime | time | IN | true |
| tuesdayCapacity | floating-point | IN | true |
| tuesdayStartTime | time | IN | true |
| wednesdayCapacity | floating-point | IN | true |
| wednesdayStartTime | time | IN | true |

## updateLowLevelCode

- Definition: `applications/manufacturing/servicedef/services_bom.xml:83`
- Engine: java; location: org.apache.ofbiz.manufacturing.bom.BOMServices; invoke: updateLowLevelCode
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| alsoComponents | Boolean | IN | true |
| alsoVariants | Boolean | IN | true |
| lowLevelCode | Long | OUT | false |
| productIdTo | String | IN | false |

## updateMrpEventType

- Definition: `applications/manufacturing/servicedef/services_mrp.xml:129`
- Engine: entity-auto; location: —; invoke: update
- Default entity: MrpEventType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| mrpEventTypeId | id | IN | — |

## updateMrpRunLog

- Definition: `applications/manufacturing/servicedef/services_mrp.xml:46`
- Engine: entity-auto; location: —; invoke: update
- Default entity: MrpRunLog; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| defaultYearsOffset | numeric | IN | true |
| durationMillis | numeric | IN | true |
| facilityGroupId | id | IN | true |
| facilityId | id | IN | true |
| failureMessage | very-long | IN | true |
| failureReason | name | IN | true |
| finishedAt | date-time | IN | true |
| jobId | id | IN | true |
| mrpEventCount | numeric | IN | true |
| mrpId | id | IN | true |
| mrpName | name | IN | true |
| mrpRunLogId | id | IN | false |
| outcomeMessage | very-long | IN | true |
| proposedRequirementCount | numeric | IN | true |
| runByUserLoginId | id-vlong | IN | true |
| runStatusId | id | IN | true |
| startedAt | date-time | IN | true |

## updateProductManufacturingRule

- Definition: `applications/manufacturing/servicedef/services_bom.xml:46`
- Engine: simple; location: component://manufacturing/minilang/bom/BomSimpleMethods.xml; invoke: updateProductManufacturingRule
- Default entity: ProductManufacturingRule; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| fromDate | date-time | IN | true |
| productFeature | id | IN | true |
| productId | id | IN | true |
| productIdFor | id | IN | true |
| productIdIn | id | IN | false |
| productIdInSubst | id | IN | true |
| quantity | floating-point | IN | true |
| ruleId | id | IN | false |
| ruleOperator | id | IN | true |
| ruleSeqId | id | IN | true |
| thruDate | date-time | IN | true |

## updateProductionRun

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:171`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: updateProductionRun
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | String | IN | true |
| estimatedStartDate | Timestamp | IN | true |
| facilityId | String | IN | true |
| productionRunId | String | IN | false |
| quantity | BigDecimal | IN | true |
| workEffortName | String | IN | true |

## updateProductionRunComponent

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:236`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: updateProductionRunComponent
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| estimatedQuantity | BigDecimal | IN | true |
| productId | String | IN | false |
| productionRunId | String | IN | false |
| workEffortId | String | IN | true |

## updateProductionRunTask

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:411`
- Engine: java; location: org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices; invoke: updateProductionRunTask
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| addQuantityProduced | BigDecimal | IN | true |
| addQuantityRejected | BigDecimal | IN | true |
| addSetupTime | BigDecimal | IN | true |
| addTaskTime | BigDecimal | IN | true |
| comments | String | IN | true |
| componentsLocationMap | Map | IN | true |
| fromDate | Timestamp | IN | true |
| issueRequiredComponents | Boolean | IN | true |
| partyId | String | IN | true |
| productionRunId | String | IN | false |
| productionRunTaskId | String | IN | false |
| thruDate | Timestamp | IN | true |

## updateWorkEffortInvRes

- Definition: `applications/manufacturing/servicedef/services_production_run.xml:563`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WorkEffortInvRes; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| createdDatetime | date-time | IN | true |
| currentPromisedDate | date-time | IN | true |
| inventoryItemId | id | IN | false |
| productId | id | IN | false |
| promisedDatetime | date-time | IN | true |
| quantity | fixed-point | IN | true |
| quantityNotAvailable | fixed-point | IN | true |
| reserveOrderEnumId | id | IN | true |
| reservedDatetime | date-time | IN | true |
| sequenceId | id | IN | true |
| workEffortId | id | IN | false |
