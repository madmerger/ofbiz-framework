# Entity 棚卸し

## ProductManufacturingRule

- Package: org.apache.ofbiz.manufacturing.bom
- Table: `PRODUCT_MANUFACTURING_RULE`
- Source: `applications/datamodel/entitydef/manufacturing-entitymodel.xml:43`

### 属性

| 名前 | 型 | DB 列 | 主キー |
| --- | --- | --- | --- |
| ruleId | id | — | Y |
| productId | id | — | N |
| productIdFor | id | — | N |
| productIdIn | id | — | N |
| ruleSeqId | id | — | N |
| fromDate | date-time | — | N |
| productIdInSubst | id | — | N |
| productFeature | id | — | N |
| ruleOperator | id | — | N |
| quantity | floating-point | — | N |
| description | description | — | N |
| thruDate | date-time | — | N |

### 関連

| 種別 | 関連 Entity | タイトル | キー対応 | 外部所有者 |
| --- | --- | --- | --- | --- |
| one | Product (external) | — | productId → — | applications/datamodel/entitydef/product-entitymodel.xml |
| one | Product (external) | ProductFor | productIdFor → productId | applications/datamodel/entitydef/product-entitymodel.xml |
| one | Product (external) | ProductIn | productIdIn → productId | applications/datamodel/entitydef/product-entitymodel.xml |
| one | Product (external) | ProductSubst | productIdInSubst → productId | applications/datamodel/entitydef/product-entitymodel.xml |
| one | ProductFeature (external) | — | productFeature → productFeatureId | applications/datamodel/entitydef/product-entitymodel.xml |

## TechDataCalendar

- Package: org.apache.ofbiz.manufacturing.techdata
- Table: `TECH_DATA_CALENDAR`
- Source: `applications/datamodel/entitydef/manufacturing-entitymodel.xml:80`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| calendarId | id | — | Y |
| description | description | — | N |
| calendarWeekId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | TechDataCalendarWeek | — | calendarWeekId → — | — |

## TechDataCalendarExcDay

- Package: org.apache.ofbiz.manufacturing.techdata
- Table: `TECH_DATA_CALENDAR_EXC_DAY`
- Source: `applications/datamodel/entitydef/manufacturing-entitymodel.xml:95`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| calendarId | id | — | Y |
| exceptionDateStartTime | date-time | — | Y |
| exceptionCapacity | fixed-point | — | N |
| usedCapacity | fixed-point | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | TechDataCalendar | — | calendarId → — | — |

## TechDataCalendarExcWeek

- Package: org.apache.ofbiz.manufacturing.techdata
- Table: `TECH_DATA_CALENDAR_EXC_WEEK`
- Source: `applications/datamodel/entitydef/manufacturing-entitymodel.xml:111`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| calendarId | id | — | Y |
| exceptionDateStart | date | — | Y |
| calendarWeekId | id | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | TechDataCalendar | — | calendarId → — | — |
| one | TechDataCalendarWeek | — | calendarWeekId → — | — |

## TechDataCalendarWeek

- Package: org.apache.ofbiz.manufacturing.techdata
- Table: `TECH_DATA_CALENDAR_WEEK`
- Source: `applications/datamodel/entitydef/manufacturing-entitymodel.xml:128`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| calendarWeekId | id | — | Y |
| description | description | — | N |
| mondayStartTime | time | — | N |
| mondayCapacity | floating-point | — | N |
| tuesdayStartTime | time | — | N |
| tuesdayCapacity | floating-point | — | N |
| wednesdayStartTime | time | — | N |
| wednesdayCapacity | floating-point | — | N |
| thursdayStartTime | time | — | N |
| thursdayCapacity | floating-point | — | N |
| fridayStartTime | time | — | N |
| fridayCapacity | floating-point | — | N |
| saturdayStartTime | time | — | N |
| saturdayCapacity | floating-point | — | N |
| sundayStartTime | time | — | N |
| sundayCapacity | floating-point | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## MrpEventType

- Package: org.apache.ofbiz.manufacturing.mrp
- Table: `MRP_EVENT_TYPE`
- Source: `applications/datamodel/entitydef/manufacturing-entitymodel.xml:156`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| mrpEventTypeId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## MrpRunLog

- Package: org.apache.ofbiz.manufacturing.mrp
- Table: `MRP_RUN_LOG`
- Source: `applications/datamodel/entitydef/manufacturing-entitymodel.xml:166`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| mrpRunLogId | id | — | Y |
| mrpId | id | — | N |
| jobId | id | — | N |
| mrpName | name | — | N |
| facilityId | id | — | N |
| facilityGroupId | id | — | N |
| defaultYearsOffset | numeric | — | N |
| runByUserLoginId | id-vlong | — | N |
| runStatusId | id | — | N |
| outcomeMessage | very-long | — | N |
| mrpEventCount | numeric | — | N |
| proposedRequirementCount | numeric | — | N |
| failureReason | name | — | N |
| failureMessage | very-long | — | N |
| startedAt | date-time | — | N |
| finishedAt | date-time | — | N |
| durationMillis | numeric | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Facility (external) | — | facilityId → — | applications/datamodel/entitydef/product-entitymodel.xml |
| one | StatusItem (external) | — | runStatusId → statusId | framework/common/entitydef/entitymodel.xml |
| one | UserLogin (external) | — | runByUserLoginId → userLoginId | framework/security/entitydef/entitymodel.xml |

## MrpEvent

- Package: org.apache.ofbiz.manufacturing.mrp
- Table: `MRP_EVENT`
- Source: `applications/datamodel/entitydef/manufacturing-entitymodel.xml:199`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| mrpId | id | — | Y |
| productId | id | — | Y |
| eventDate | date-time | — | Y |
| mrpEventTypeId | id | — | Y |
| facilityId | id | — | N |
| quantity | floating-point | — | N |
| eventName | very-long | — | N |
| isLate | indicator | — | N |
| facilityIdTo | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Product (external) | — | productId → — | applications/datamodel/entitydef/product-entitymodel.xml |
| one | MrpEventType | — | mrpEventTypeId → — | — |
| one | Facility (external) | — | facilityId → — | applications/datamodel/entitydef/product-entitymodel.xml |

## View entities

### MrpEventView

- Package: org.apache.ofbiz.manufacturing.mrp
- Source: `applications/datamodel/entitydef/manufacturing-entitymodel.xml:227`
- Member entities: MEV = MrpEvent, PR = Product

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| MEV | PR | productId → — |
