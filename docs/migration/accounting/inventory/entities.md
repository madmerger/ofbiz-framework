# Entity inventory

## Budget

- Package: org.apache.ofbiz.accounting.budget
- Table: `BUDGET`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:48`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| budgetId | id | — | Y |
| budgetTypeId | id | — | N |
| customTimePeriodId | id | — | N |
| comments | comment | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | BudgetType | — | budgetTypeId → — | — |
| one | CustomTimePeriod (external) | — | customTimePeriodId → — | framework/common/entitydef/entitymodel.xml |
| many | BudgetTypeAttr | — | budgetTypeId → — | — |

## BudgetAttribute

- Package: org.apache.ofbiz.accounting.budget
- Table: `BUDGET_ATTRIBUTE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:66`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| budgetId | id | — | Y |
| attrName | id-long | — | Y |
| attrValue | value | — | N |
| attrDescription | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Budget | — | budgetId → — | — |
| many | BudgetTypeAttr | — | attrName → — | — |

## BudgetItem

- Package: org.apache.ofbiz.accounting.budget
- Table: `BUDGET_ITEM`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:82`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| budgetId | id | — | Y |
| budgetItemSeqId | id | — | Y |
| budgetItemTypeId | id | — | N |
| amount | currency-amount | — | N |
| purpose | long-varchar | — | N |
| justification | long-varchar | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Budget | — | budgetId → — | — |
| one | BudgetItemType | — | budgetItemTypeId → — | — |
| many | BudgetItemTypeAttr | — | budgetItemTypeId → — | — |

## BudgetItemAttribute

- Package: org.apache.ofbiz.accounting.budget
- Table: `BUDGET_ITEM_ATTRIBUTE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:103`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| budgetId | id | — | Y |
| budgetItemSeqId | id | — | Y |
| attrName | id-long | — | Y |
| attrValue | value | — | N |
| attrDescription | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | BudgetItem | — | budgetId → —, budgetItemSeqId → — | — |
| many | BudgetItemTypeAttr | — | attrName → — | — |

## BudgetItemType

- Package: org.apache.ofbiz.accounting.budget
- Table: `BUDGET_ITEM_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:122`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| budgetItemTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | BudgetItemType | Parent | parentTypeId → budgetItemTypeId | — |

## BudgetItemTypeAttr

- Package: org.apache.ofbiz.accounting.budget
- Table: `BUDGET_ITEM_TYPE_ATTR`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:134`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| budgetItemTypeId | id | — | Y |
| attrName | id-long | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | BudgetItemType | — | budgetItemTypeId → — | — |
| many | BudgetItemAttribute | — | attrName → — | — |
| many | BudgetItem | — | budgetItemTypeId → — | — |

## BudgetReview

- Package: org.apache.ofbiz.accounting.budget
- Table: `BUDGET_REVIEW`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:152`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| budgetId | id | — | Y |
| budgetReviewId | id | — | Y |
| partyId | id | — | Y |
| budgetReviewResultTypeId | id | — | Y |
| reviewDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Budget | — | budgetId → — | — |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | BudgetReviewResultType | — | budgetReviewResultTypeId → — | — |

## BudgetReviewResultType

- Package: org.apache.ofbiz.accounting.budget
- Table: `BUDGET_REVIEW_RESULT_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:174`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| budgetReviewResultTypeId | id | — | Y |
| description | description | — | N |
| comments | comment | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## BudgetRevision

- Package: org.apache.ofbiz.accounting.budget
- Table: `BUDGET_REVISION`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:182`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| budgetId | id | — | Y |
| revisionSeqId | id | — | Y |
| dateRevised | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Budget | — | budgetId → — | — |

## BudgetRevisionImpact

- Package: org.apache.ofbiz.accounting.budget
- Table: `BUDGET_REVISION_IMPACT`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:194`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| budgetId | id | — | Y |
| budgetItemSeqId | id | — | Y |
| revisionSeqId | id | — | Y |
| revisedAmount | currency-amount | — | N |
| addDeleteFlag | indicator | — | N |
| revisionReason | long-varchar | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Budget | — | budgetId → — | — |
| one | BudgetItem | — | budgetId → —, budgetItemSeqId → — | — |
| one | BudgetRevision | — | budgetId → —, revisionSeqId → — | — |

## BudgetRole

- Package: org.apache.ofbiz.accounting.budget
- Table: `BUDGET_ROLE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:218`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| budgetId | id | — | Y |
| partyId | id | — | Y |
| roleTypeId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Budget | — | budgetId → — | — |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PartyRole (external) | — | partyId → —, roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |

## BudgetScenario

- Package: org.apache.ofbiz.accounting.budget
- Table: `BUDGET_SCENARIO`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:238`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| budgetScenarioId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## BudgetScenarioApplication

- Package: org.apache.ofbiz.accounting.budget
- Table: `BUDGET_SCENARIO_APPLICATION`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:245`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| budgetScenarioApplicId | id | — | Y |
| budgetScenarioId | id | — | Y |
| budgetId | id | — | N |
| budgetItemSeqId | id | — | N |
| amountChange | currency-amount | — | N |
| percentageChange | fixed-point | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | BudgetScenario | — | budgetScenarioId → — | — |
| one | Budget | — | budgetId → — | — |
| one | BudgetItem | — | budgetId → —, budgetItemSeqId → — | — |

## BudgetScenarioRule

- Package: org.apache.ofbiz.accounting.budget
- Table: `BUDGET_SCENARIO_RULE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:267`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| budgetScenarioId | id | — | Y |
| budgetItemTypeId | id | — | Y |
| amountChange | currency-amount | — | N |
| percentageChange | fixed-point | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | BudgetScenario | — | budgetScenarioId → — | — |
| one | BudgetItemType | — | budgetItemTypeId → — | — |

## BudgetStatus

- Package: org.apache.ofbiz.accounting.budget
- Table: `BUDGET_STATUS`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:283`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| budgetId | id | — | Y |
| statusId | id | — | Y |
| statusDate | date-time | — | N |
| comments | comment | — | N |
| changeByUserLoginId | id-vlong | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Budget | — | budgetId → — | — |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |
| one | UserLogin (external) | ChangeBy | changeByUserLoginId → userLoginId | framework/security/entitydef/entitymodel.xml |

## BudgetType

- Package: org.apache.ofbiz.accounting.budget
- Table: `BUDGET_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:303`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| budgetTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | BudgetType | Parent | parentTypeId → budgetTypeId | — |

## BudgetTypeAttr

- Package: org.apache.ofbiz.accounting.budget
- Table: `BUDGET_TYPE_ATTR`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:315`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| budgetTypeId | id | — | Y |
| attrName | id-long | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | BudgetType | — | budgetTypeId → — | — |
| many | BudgetAttribute | — | attrName → — | — |
| many | Budget | — | budgetTypeId → — | — |

## FinAccount

- Package: org.apache.ofbiz.accounting.finaccount
- Table: `FIN_ACCOUNT`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:339`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| finAccountId | id | — | Y |
| finAccountTypeId | id | — | N |
| statusId | id | — | N |
| finAccountName | name | — | N |
| finAccountCode | long-varchar | — | N |
| finAccountPin | long-varchar | — | N |
| currencyUomId | id | — | N |
| organizationPartyId | id | — | N |
| ownerPartyId | id | — | N |
| postToGlAccountId | id | — | N |
| fromDate | date-time | — | N |
| thruDate | date-time | — | N |
| isRefundable | indicator | — | N |
| replenishPaymentId | id | — | N |
| replenishLevel | currency-amount | — | N |
| actualBalance | currency-amount | — | N |
| availableBalance | currency-amount | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | FinAccountType | — | finAccountTypeId → — | — |
| one | Uom (external) | Currency | currencyUomId → uomId | framework/common/entitydef/entitymodel.xml |
| one | Party (external) | Organization | organizationPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | Party (external) | Owner | ownerPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | GlAccount | PostTo | postToGlAccountId → glAccountId | — |
| one | PaymentMethod | Replenish | replenishPaymentId → paymentMethodId | — |
| many | FinAccountTypeAttr | — | finAccountTypeId → — | — |

## FinAccountAttribute

- Package: org.apache.ofbiz.accounting.finaccount
- Table: `FIN_ACCOUNT_ATTRIBUTE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:392`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| finAccountId | id | — | Y |
| attrName | id-long | — | Y |
| attrValue | value | — | N |
| attrDescription | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | FinAccount | — | finAccountId → — | — |
| many | FinAccountTypeAttr | — | attrName → — | — |

## FinAccountAuth

- Package: org.apache.ofbiz.accounting.finaccount
- Table: `FIN_ACCOUNT_AUTH`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:406`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| finAccountAuthId | id | — | Y |
| finAccountId | id | — | N |
| amount | currency-amount | — | N |
| currencyUomId | id | — | N |
| authorizationDate | date-time | — | N |
| fromDate | date-time | — | N |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | FinAccount | — | finAccountId → — | — |

## FinAccountRole

- Package: org.apache.ofbiz.accounting.finaccount
- Table: `FIN_ACCOUNT_ROLE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:419`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| finAccountId | id | — | Y |
| partyId | id | — | Y |
| roleTypeId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | FinAccount | — | finAccountId → — | — |
| one-nofk | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | RoleType (external) | — | roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PartyRole (external) | — | partyId → —, roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |

## FinAccountStatus

- Package: org.apache.ofbiz.accounting.finaccount
- Table: `FIN_ACCOUNT_STATUS`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:443`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| finAccountId | id | — | Y |
| statusId | id | — | Y |
| statusDate | date-time | — | Y |
| statusEndDate | date-time | — | N |
| changeByUserLoginId | id-vlong | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | FinAccount | — | finAccountId → — | — |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |
| one | UserLogin (external) | — | changeByUserLoginId → userLoginId | framework/security/entitydef/entitymodel.xml |

## FinAccountTrans

- Package: org.apache.ofbiz.accounting.finaccount
- Table: `FIN_ACCOUNT_TRANS`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:462`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| finAccountTransId | id | — | Y |
| finAccountTransTypeId | id | — | N |
| finAccountId | id | — | N |
| partyId | id | — | N |
| glReconciliationId | id | — | N |
| transactionDate | date-time | — | N |
| entryDate | date-time | — | N |
| amount | currency-amount | — | N |
| paymentId | id | — | N |
| orderId | id | — | N |
| orderItemSeqId | id | — | N |
| performedByPartyId | id | — | N |
| reasonEnumId | id | — | N |
| comments | comment | — | N |
| statusId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | FinAccountTransType | — | finAccountTransTypeId → — | — |
| many | FinAccountTransTypeAttr | — | finAccountTransTypeId → — | — |
| one | FinAccount | — | finAccountId → — | — |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | Payment | — | paymentId → — | — |
| one | OrderItem (external) | — | orderId → —, orderItemSeqId → — | applications/datamodel/entitydef/order-entitymodel.xml |
| one | Party (external) | PerformedBy | performedByPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | Enumeration (external) | Reason | reasonEnumId → enumId | framework/common/entitydef/entitymodel.xml |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |
| one | GlReconciliation | — | glReconciliationId → — | — |

## FinAccountTransAttribute

- Package: org.apache.ofbiz.accounting.finaccount
- Table: `FIN_ACCOUNT_TRANS_ATTRIBUTE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:511`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| finAccountTransId | id | — | Y |
| attrName | id-long | — | Y |
| attrValue | value | — | N |
| attrDescription | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | FinAccountTrans | — | finAccountTransId → — | — |
| many | FinAccountTransTypeAttr | — | attrName → — | — |

## FinAccountTransType

- Package: org.apache.ofbiz.accounting.finaccount
- Table: `FIN_ACCOUNT_TRANS_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:525`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| finAccountTransTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | FinAccountTransType | Parent | parentTypeId → finAccountTransTypeId | — |

## FinAccountTransTypeAttr

- Package: org.apache.ofbiz.accounting.finaccount
- Table: `FIN_ACCOUNT_TRANS_TYPE_ATTR`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:538`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| finAccountTransTypeId | id | — | Y |
| attrName | id-long | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | FinAccountTransType | — | finAccountTransTypeId → — | — |
| many | FinAccountTransAttribute | — | attrName → — | — |
| many | FinAccountTrans | — | finAccountTransTypeId → — | — |

## FinAccountType

- Package: org.apache.ofbiz.accounting.finaccount
- Table: `FIN_ACCOUNT_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:554`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| finAccountTypeId | id | — | Y |
| parentTypeId | id | — | N |
| replenishEnumId | id | — | N |
| isRefundable | indicator | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | FinAccountType | Parent | parentTypeId → finAccountTypeId | — |
| one | Enumeration (external) | Replenish | replenishEnumId → enumId | framework/common/entitydef/entitymodel.xml |

## FinAccountTypeAttr

- Package: org.apache.ofbiz.accounting.finaccount
- Table: `FIN_ACCOUNT_TYPE_ATTR`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:572`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| finAccountTypeId | id | — | Y |
| attrName | id-long | — | Y |
| attrValue | long-varchar | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | FinAccountType | — | finAccountTypeId → — | — |
| many | FinAccountAttribute | — | attrName → — | — |
| many | FinAccount | — | finAccountTypeId → — | — |

## FinAccountTypeGlAccount

- Package: org.apache.ofbiz.accounting.finaccount
- Table: `FIN_ACCOUNT_TYPE_GL_ACCOUNT`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:589`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| finAccountTypeId | id | — | Y |
| organizationPartyId | id | — | Y |
| glAccountId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | FinAccountType | — | finAccountTypeId → — | — |
| one | Party (external) | Organization | organizationPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | GlAccount | — | glAccountId → — | — |

## FixedAsset

- Package: org.apache.ofbiz.accounting.fixedasset
- Table: `FIXED_ASSET`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:630`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| fixedAssetId | id | — | Y |
| fixedAssetTypeId | id | — | N |
| parentFixedAssetId | id | — | N |
| instanceOfProductId | id | — | N |
| classEnumId | id | — | N |
| partyId | id | — | N |
| roleTypeId | id | — | N |
| fixedAssetName | name | — | N |
| acquireOrderId | id | — | N |
| acquireOrderItemSeqId | id | — | N |
| dateAcquired | date-time | — | N |
| dateLastServiced | date-time | — | N |
| dateNextService | date-time | — | N |
| expectedEndOfLife | date | — | N |
| actualEndOfLife | date | — | N |
| productionCapacity | fixed-point | — | N |
| uomId | id | — | N |
| calendarId | id | — | N |
| serialNumber | long-varchar | — | N |
| locatedAtFacilityId | id | — | N |
| locatedAtLocationSeqId | id | — | N |
| salvageValue | currency-amount | — | N |
| depreciation | currency-amount | — | N |
| purchaseCost | currency-amount | — | N |
| purchaseCostUomId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | FixedAssetType | — | fixedAssetTypeId → — | — |
| many | FixedAssetTypeAttr | — | fixedAssetTypeId → — | — |
| one | FixedAsset | Parent | parentFixedAssetId → fixedAssetId | — |
| one | Product (external) | InstanceOf | instanceOfProductId → productId | applications/datamodel/entitydef/product-entitymodel.xml |
| one | Enumeration (external) | Class | classEnumId → enumId | framework/common/entitydef/entitymodel.xml |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | RoleType (external) | — | roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | PartyRole (external) | — | partyId → —, roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | OrderHeader (external) | Acquire | acquireOrderId → orderId | applications/datamodel/entitydef/order-entitymodel.xml |
| one | OrderItem (external) | Acquire | acquireOrderId → orderId, acquireOrderItemSeqId → orderItemSeqId | applications/datamodel/entitydef/order-entitymodel.xml |
| one | Uom (external) | — | uomId → — | framework/common/entitydef/entitymodel.xml |
| one | TechDataCalendar (external) | — | calendarId → — | applications/datamodel/entitydef/manufacturing-entitymodel.xml |
| one | Facility (external) | LocatedAt | locatedAtFacilityId → facilityId | applications/datamodel/entitydef/product-entitymodel.xml |
| one-nofk | FacilityLocation (external) | LocatedAt | locatedAtFacilityId → facilityId, locatedAtLocationSeqId → locationSeqId | applications/datamodel/entitydef/product-entitymodel.xml |

## FixedAssetAttribute

- Package: org.apache.ofbiz.accounting.fixedasset
- Table: `FIXED_ASSET_ATTRIBUTE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:703`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| fixedAssetId | id | — | Y |
| attrName | id-long | — | Y |
| attrValue | value | — | N |
| attrDescription | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | FixedAsset | — | fixedAssetId → — | — |
| many | FixedAssetTypeAttr | — | attrName → — | — |

## FixedAssetDepMethod

- Package: org.apache.ofbiz.accounting.fixedasset
- Table: `FIXED_ASSET_DEP_METHOD`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:719`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| depreciationCustomMethodId | id | — | Y |
| fixedAssetId | id | — | Y |
| fromDate | date-time | — | N |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | CustomMethod (external) | — | depreciationCustomMethodId → customMethodId | framework/common/entitydef/entitymodel.xml |
| one | FixedAsset | — | fixedAssetId → — | — |

## FixedAssetGeoPoint

- Package: org.apache.ofbiz.accounting.fixedasset
- Table: `FIXED_ASSET_GEO_POINT`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:735`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| fixedAssetId | id | — | Y |
| geoPointId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | FixedAsset | — | fixedAssetId → — | — |
| one | GeoPoint (external) | — | geoPointId → — | framework/common/entitydef/entitymodel.xml |

## FixedAssetIdent

- Package: org.apache.ofbiz.accounting.fixedasset
- Table: `FIXED_ASSET_IDENT`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:777`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| fixedAssetId | id | — | Y |
| fixedAssetIdentTypeId | id | — | Y |
| idValue | long-varchar | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | FixedAsset | — | fixedAssetId → — | — |
| one | FixedAssetIdentType | — | fixedAssetIdentTypeId → — | — |

## FixedAssetIdentType

- Package: org.apache.ofbiz.accounting.fixedasset
- Table: `FIXED_ASSET_IDENT_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:792`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| fixedAssetIdentTypeId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## FixedAssetMaint

- Package: org.apache.ofbiz.accounting.fixedasset
- Table: `FIXED_ASSET_MAINT`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:800`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| fixedAssetId | id | — | Y |
| maintHistSeqId | id | — | Y |
| statusId | id | — | N |
| productMaintTypeId | id | — | N |
| productMaintSeqId | id | — | N |
| scheduleWorkEffortId | id | — | N |
| intervalQuantity | fixed-point | — | N |
| intervalUomId | id | — | N |
| intervalMeterTypeId | id | — | N |
| purchaseOrderId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | FixedAsset | — | fixedAssetId → — | — |
| one | ProductMaintType (external) | — | productMaintTypeId → — | applications/datamodel/entitydef/product-entitymodel.xml |
| one | WorkEffort (external) | Schedule | scheduleWorkEffortId → workEffortId | applications/datamodel/entitydef/workeffort-entitymodel.xml |
| one | Uom (external) | Interval | intervalUomId → uomId | framework/common/entitydef/entitymodel.xml |
| one | ProductMeterType (external) | Interval | intervalMeterTypeId → productMeterTypeId | applications/datamodel/entitydef/product-entitymodel.xml |
| one | OrderHeader (external) | Purchase | purchaseOrderId → orderId | applications/datamodel/entitydef/order-entitymodel.xml |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |

## FixedAssetMeter

- Package: org.apache.ofbiz.accounting.fixedasset
- Table: `FIXED_ASSET_METER`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:838`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| fixedAssetId | id | — | Y |
| productMeterTypeId | id | — | Y |
| readingDate | date-time | — | Y |
| meterValue | fixed-point | — | N |
| readingReasonEnumId | id | — | N |
| maintHistSeqId | id | — | N |
| workEffortId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | FixedAssetMaint | — | fixedAssetId → —, maintHistSeqId → — | — |
| one | ProductMeterType (external) | — | productMeterTypeId → — | applications/datamodel/entitydef/product-entitymodel.xml |

## FixedAssetProduct

- Package: org.apache.ofbiz.accounting.fixedasset
- Table: `FIXED_ASSET_PRODUCT`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:859`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| fixedAssetId | id | — | Y |
| productId | id | — | Y |
| fixedAssetProductTypeId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| comments | comment | — | N |
| sequenceNum | numeric | — | N |
| quantity | fixed-point | — | N |
| quantityUomId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Product (external) | — | productId → — | applications/datamodel/entitydef/product-entitymodel.xml |
| one | FixedAsset | — | fixedAssetId → — | — |
| one | FixedAssetProductType | — | fixedAssetProductTypeId → — | — |
| one | Uom (external) | — | quantityUomId → uomId | framework/common/entitydef/entitymodel.xml |

## FixedAssetProductType

- Package: org.apache.ofbiz.accounting.fixedasset
- Table: `FIXED_ASSET_PRODUCT_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:888`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| fixedAssetProductTypeId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## FixedAssetTypeGlAccount

- Package: org.apache.ofbiz.accounting.fixedasset
- Table: `FIXED_ASSET_TYPE_GL_ACCOUNT`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:896`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| fixedAssetTypeId | id | — | Y |
| fixedAssetId | id | — | Y |
| organizationPartyId | id | — | Y |
| assetGlAccountId | id | — | N |
| accDepGlAccountId | id | — | N |
| depGlAccountId | id | — | N |
| profitGlAccountId | id | — | N |
| lossGlAccountId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one-nofk | FixedAssetType | — | fixedAssetTypeId → — | — |
| one-nofk | FixedAsset | — | fixedAssetId → — | — |
| one | Party (external) | — | organizationPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | GlAccount | Asset | assetGlAccountId → glAccountId | — |
| one | GlAccount | AccumulatedDepreciation | accDepGlAccountId → glAccountId | — |
| one | GlAccount | Depreciation | depGlAccountId → glAccountId | — |
| one | GlAccount | Profit | profitGlAccountId → glAccountId | — |
| one | GlAccount | Loss | lossGlAccountId → glAccountId | — |

## FixedAssetRegistration

- Package: org.apache.ofbiz.accounting.fixedasset
- Table: `FIXED_ASSET_REGISTRATION`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:936`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| fixedAssetId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| registrationDate | date-time | — | N |
| govAgencyPartyId | id | — | N |
| registrationNumber | long-varchar | — | N |
| licenseNumber | long-varchar | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | FixedAsset | — | fixedAssetId → — | — |
| one | Party (external) | GovAgency | govAgencyPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |

## FixedAssetStdCost

- Package: org.apache.ofbiz.accounting.fixedasset
- Table: `FIXED_ASSET_STD_COST`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:953`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| fixedAssetId | id | — | Y |
| fixedAssetStdCostTypeId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| amountUomId | id | — | N |
| amount | currency-amount | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | FixedAsset | — | fixedAssetId → — | — |
| one | FixedAssetStdCostType | — | fixedAssetStdCostTypeId → — | — |
| one | Uom (external) | — | amountUomId → uomId | framework/common/entitydef/entitymodel.xml |

## FixedAssetStdCostType

- Package: org.apache.ofbiz.accounting.fixedasset
- Table: `FIXED_ASSET_STD_COST_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:973`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| fixedAssetStdCostTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | FixedAssetStdCostType | Parent | parentTypeId → fixedAssetStdCostTypeId | — |

## FixedAssetType

- Package: org.apache.ofbiz.accounting.fixedasset
- Table: `FIXED_ASSET_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:986`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| fixedAssetTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | FixedAssetType | Parent | parentTypeId → fixedAssetTypeId | — |

## FixedAssetTypeAttr

- Package: org.apache.ofbiz.accounting.fixedasset
- Table: `FIXED_ASSET_TYPE_ATTR`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:999`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| fixedAssetTypeId | id | — | Y |
| attrName | id-long | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | FixedAssetType | — | fixedAssetTypeId → — | — |
| many | FixedAssetAttribute | — | attrName → — | — |
| many | FixedAsset | — | fixedAssetTypeId → — | — |

## PartyFixedAssetAssignment

- Package: org.apache.ofbiz.accounting.fixedasset
- Table: `PARTY_FIXED_ASSET_ASSIGNMENT`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1017`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| roleTypeId | id | — | Y |
| fixedAssetId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| allocatedDate | date-time | — | N |
| statusId | id | — | N |
| comments | comment | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one-nofk | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | RoleType (external) | — | roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PartyRole (external) | — | partyId → —, roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | FixedAsset | — | fixedAssetId → — | — |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |

## FixedAssetMaintOrder

- Package: org.apache.ofbiz.accounting.fixedasset
- Table: `FIXED_ASSET_MAINT_ORDER`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1061`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| fixedAssetId | id | — | Y |
| maintHistSeqId | id | — | Y |
| orderId | id | — | Y |
| orderItemSeqId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | FixedAsset | — | fixedAssetId → — | — |
| one | OrderHeader (external) | — | orderId → — | applications/datamodel/entitydef/order-entitymodel.xml |
| one-nofk | OrderItem (external) | — | orderId → —, orderItemSeqId → — | applications/datamodel/entitydef/order-entitymodel.xml |

## AccommodationClass

- Package: org.apache.ofbiz.accounting.fixedasset
- Table: `ACCOMMODATION_CLASS`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1082`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| accommodationClassId | id | — | Y |
| parentClassId | id | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | AccommodationClass | Parent | parentClassId → accommodationClassId | — |

## AccommodationSpot

- Package: org.apache.ofbiz.order.reservations
- Table: `ACCOMMODATION_SPOT`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1093`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| accommodationSpotId | id | — | Y |
| accommodationClassId | id | — | N |
| fixedAssetId | id | — | N |
| numberOfSpaces | numeric | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | AccommodationClass | — | accommodationClassId → — | — |
| one | FixedAsset | — | fixedAssetId → — | — |

## AccommodationMap

- Package: org.apache.ofbiz.accounting.fixedasset
- Table: `ACCOMMODATION_MAP`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1110`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| accommodationMapId | id | — | Y |
| accommodationClassId | id | — | N |
| fixedAssetId | id | — | N |
| accommodationMapTypeId | id | — | N |
| numberOfSpaces | numeric | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | AccommodationClass | — | accommodationClassId → — | — |
| one | FixedAsset | — | fixedAssetId → — | — |
| one | AccommodationMapType | — | accommodationMapTypeId → — | — |

## AccommodationMapType

- Package: org.apache.ofbiz.accounting.fixedasset
- Table: `ACCOMMODATION_MAP_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1130`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| accommodationMapTypeId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## Invoice

- Package: org.apache.ofbiz.accounting.invoice
- Table: `INVOICE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1159`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| invoiceId | id | — | Y |
| invoiceTypeId | id | — | N |
| partyIdFrom | id | — | N |
| partyId | id | — | N |
| roleTypeId | id | — | N |
| statusId | id | — | N |
| billingAccountId | id | — | N |
| contactMechId | id | — | N |
| invoiceDate | date-time | — | N |
| dueDate | date-time | — | N |
| paidDate | date-time | — | N |
| invoiceMessage | long-varchar | — | N |
| referenceNumber | short-varchar | — | N |
| description | description | — | N |
| currencyUomId | id | — | N |
| recurrenceInfoId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | InvoiceType | — | invoiceTypeId → — | — |
| many | InvoiceTypeAttr | — | invoiceTypeId → — | — |
| one | Party (external) | From | partyIdFrom → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | RoleType (external) | — | roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | PartyRole (external) | — | partyId → —, roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |
| one | BillingAccount | — | billingAccountId → — | — |
| one | ContactMech (external) | — | contactMechId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | Uom (external) | Currency | currencyUomId → uomId | framework/common/entitydef/entitymodel.xml |
| one | RecurrenceInfo (external) | — | recurrenceInfoId → — | framework/service/entitydef/entitymodel.xml |

## InvoiceAttribute

- Package: org.apache.ofbiz.accounting.invoice
- Table: `INVOICE_ATTRIBUTE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1214`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| invoiceId | id | — | Y |
| attrName | id-long | — | Y |
| attrValue | value | — | N |
| attrDescription | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Invoice | — | invoiceId → — | — |
| many | InvoiceTypeAttr | — | attrName → — | — |

## InvoiceContent

- Package: org.apache.ofbiz.accounting.invoice
- Table: `INVOICE_CONTENT`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1230`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| invoiceId | id | — | Y |
| invoiceContentTypeId | id | — | Y |
| contentId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Invoice | — | invoiceId → — | — |
| one | Content (external) | — | contentId → — | applications/datamodel/entitydef/content-entitymodel.xml |
| one | InvoiceContentType | — | invoiceContentTypeId → — | — |

## InvoiceContentType

- Package: org.apache.ofbiz.accounting.invoice
- Table: `INVOICE_CONTENT_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1252`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| invoiceContentTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | InvoiceContentType | Parent | parentTypeId → invoiceContentTypeId | — |

## InvoiceContactMech

- Package: org.apache.ofbiz.accounting.invoice
- Table: `INVOICE_CONTACT_MECH`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1264`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| invoiceId | id | — | Y |
| contactMechPurposeTypeId | id | — | Y |
| contactMechId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Invoice | — | invoiceId → — | — |
| one | ContactMech (external) | — | contactMechId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | ContactMechPurposeType (external) | — | contactMechPurposeTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |

## InvoiceItem

- Package: org.apache.ofbiz.accounting.invoice
- Table: `INVOICE_ITEM`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1284`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| invoiceId | id | — | Y |
| invoiceItemSeqId | id | — | Y |
| invoiceItemTypeId | id | — | N |
| overrideGlAccountId | id | — | N |
| overrideOrgPartyId | id | — | N |
| inventoryItemId | id | — | N |
| productId | id | — | N |
| productFeatureId | id | — | N |
| parentInvoiceId | id | — | N |
| parentInvoiceItemSeqId | id | — | N |
| uomId | id | — | N |
| taxableFlag | indicator | — | N |
| quantity | fixed-point | — | N |
| amount | currency-precise | — | N |
| description | description | — | N |
| taxAuthPartyId | id | — | N |
| taxAuthGeoId | id | — | N |
| taxAuthorityRateSeqId | id | — | N |
| salesOpportunityId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | InvoiceItemType | — | invoiceItemTypeId → — | — |
| many | InvoiceItemTypeAttr | — | invoiceItemTypeId → — | — |
| one | Invoice | — | invoiceId → — | — |
| one | InventoryItem (external) | — | inventoryItemId → — | applications/datamodel/entitydef/product-entitymodel.xml |
| one | Product (external) | — | productId → — | applications/datamodel/entitydef/product-entitymodel.xml |
| one | ProductFeature (external) | — | productFeatureId → — | applications/datamodel/entitydef/product-entitymodel.xml |
| one | InvoiceItem | — | parentInvoiceId → invoiceId, parentInvoiceItemSeqId → invoiceItemSeqId | — |
| many | InvoiceItem | Children | invoiceId → parentInvoiceId, invoiceItemSeqId → parentInvoiceItemSeqId | — |
| one | Uom (external) | — | uomId → — | framework/common/entitydef/entitymodel.xml |
| one | GlAccount | Override | overrideGlAccountId → glAccountId | — |
| one | Party (external) | TaxAuthority | taxAuthPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | Geo (external) | Tax | taxAuthGeoId → geoId | framework/common/entitydef/entitymodel.xml |
| one | TaxAuthorityRateProduct | — | taxAuthorityRateSeqId → — | — |
| one | Party (external) | OverrideOrg | overrideOrgPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | SalesOpportunity (external) | — | salesOpportunityId → — | applications/datamodel/entitydef/marketing-entitymodel.xml |

## InvoiceItemAssoc

- Package: org.apache.ofbiz.accounting.invoice
- Table: `INVOICE_ITEM_ASSOC`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1357`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| invoiceIdFrom | id | — | Y |
| invoiceItemSeqIdFrom | id | — | Y |
| invoiceIdTo | id | — | Y |
| invoiceItemSeqIdTo | id | — | Y |
| invoiceItemAssocTypeId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| partyIdFrom | id | — | N |
| partyIdTo | id | — | N |
| quantity | fixed-point | — | N |
| amount | currency-amount | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | InvoiceItemAssocType | — | invoiceItemAssocTypeId → — | — |
| one | InvoiceItem | From | invoiceIdFrom → invoiceId, invoiceItemSeqIdFrom → invoiceItemSeqId | — |
| one | InvoiceItem | To | invoiceIdTo → invoiceId, invoiceItemSeqIdTo → invoiceItemSeqId | — |
| one-nofk | Party (external) | From | partyIdFrom → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | Party (external) | To | partyIdTo → partyId | applications/datamodel/entitydef/party-entitymodel.xml |

## InvoiceItemAssocType

- Package: org.apache.ofbiz.accounting.invoice
- Table: `INVOICE_ITEM_ASSOC_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1396`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| invoiceItemAssocTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | InvoiceItemAssocType | Parent | parentTypeId → invoiceItemAssocTypeId | — |

## InvoiceItemAttribute

- Package: org.apache.ofbiz.accounting.invoice
- Table: `INVOICE_ITEM_ATTRIBUTE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1409`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| invoiceId | id | — | Y |
| invoiceItemSeqId | id | — | Y |
| attrName | id-long | — | Y |
| attrValue | value | — | N |
| attrDescription | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | InvoiceItem | — | invoiceId → —, invoiceItemSeqId → — | — |
| many | InvoiceItemTypeAttr | — | attrName → — | — |

## InvoiceItemType

- Package: org.apache.ofbiz.accounting.invoice
- Table: `INVOICE_ITEM_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1428`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| invoiceItemTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |
| defaultGlAccountId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | InvoiceItemType | Parent | parentTypeId → invoiceItemTypeId | — |
| one | GlAccount | Default | defaultGlAccountId → glAccountId | — |

## InvoiceItemTypeAttr

- Package: org.apache.ofbiz.accounting.invoice
- Table: `INVOICE_ITEM_TYPE_ATTR`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1444`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| invoiceItemTypeId | id | — | Y |
| attrName | id-long | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | InvoiceItemType | — | invoiceItemTypeId → — | — |
| many | InvoiceItemAttribute | — | attrName → — | — |
| many | InvoiceItem | — | invoiceItemTypeId → — | — |

## InvoiceItemTypeGlAccount

- Package: org.apache.ofbiz.accounting.invoice
- Table: `INVOICE_ITEM_TYPE_GL_ACCOUNT`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1462`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| invoiceItemTypeId | id | — | Y |
| organizationPartyId | id | — | Y |
| glAccountId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | InvoiceItemType | — | invoiceItemTypeId → — | — |
| one | Party (external) | Organization | organizationPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | GlAccount | — | glAccountId → — | — |

## InvoiceItemTypeMap

- Package: org.apache.ofbiz.accounting.invoice
- Table: `INVOICE_ITEM_TYPE_MAP`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1480`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| invoiceItemMapKey | id | — | Y |
| invoiceTypeId | id | — | Y |
| invoiceItemTypeId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | InvoiceItemType | — | invoiceItemTypeId → — | — |
| one | InvoiceType | — | invoiceTypeId → — | — |
| many | InvoiceItem | — | invoiceItemTypeId → — | — |

## InvoiceRole

- Package: org.apache.ofbiz.accounting.invoice
- Table: `INVOICE_ROLE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1498`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| invoiceId | id | — | Y |
| partyId | id | — | Y |
| roleTypeId | id | — | Y |
| datetimePerformed | date-time | — | N |
| percentage | fixed-point | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Invoice | — | invoiceId → — | — |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | RoleType (external) | — | roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PartyRole (external) | — | partyId → —, roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |

## InvoiceStatus

- Package: org.apache.ofbiz.accounting.invoice
- Table: `INVOICE_STATUS`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1523`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| statusId | id | — | Y |
| invoiceId | id | — | Y |
| statusDate | date-time | — | Y |
| changeByUserLoginId | id-vlong | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |
| one | Invoice | — | invoiceId → — | — |
| one | UserLogin (external) | ChangeBy | changeByUserLoginId → userLoginId | framework/security/entitydef/entitymodel.xml |

## InvoiceTerm

- Package: org.apache.ofbiz.accounting.invoice
- Table: `INVOICE_TERM`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1543`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| invoiceTermId | id | — | Y |
| termTypeId | id | — | N |
| invoiceId | id | — | N |
| invoiceItemSeqId | id | — | N |
| termValue | currency-amount | — | N |
| termDays | numeric | — | N |
| textValue | description | — | N |
| description | description | — | N |
| uomId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | TermType (external) | — | termTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | Invoice | — | invoiceId → — | — |
| one-nofk | InvoiceItem | — | invoiceId → —, invoiceItemSeqId → — | — |

## InvoiceTermAttribute

- Package: org.apache.ofbiz.accounting.invoice
- Table: `INVOICE_TERM_ATTRIBUTE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1567`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| invoiceTermId | id | — | Y |
| attrName | id-long | — | Y |
| attrValue | value | — | N |
| attrDescription | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | InvoiceTerm | — | invoiceTermId → — | — |
| many | TermTypeAttr (external) | — | attrName → — | applications/datamodel/entitydef/party-entitymodel.xml |

## InvoiceType

- Package: org.apache.ofbiz.accounting.invoice
- Table: `INVOICE_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1583`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| invoiceTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | InvoiceType | Parent | parentTypeId → invoiceTypeId | — |

## InvoiceTypeAttr

- Package: org.apache.ofbiz.accounting.invoice
- Table: `INVOICE_TYPE_ATTR`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1595`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| invoiceTypeId | id | — | Y |
| attrName | id-long | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | InvoiceType | — | invoiceTypeId → — | — |
| many | InvoiceAttribute | — | attrName → — | — |
| many | Invoice | — | invoiceTypeId → — | — |

## InvoiceNote

- Package: org.apache.ofbiz.accounting.invoice
- Table: `INVOICE_NOTE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1613`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| invoiceId | id | — | Y |
| noteId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Invoice | — | invoiceId → — | — |
| one | NoteData (external) | — | noteId → — | framework/common/entitydef/entitymodel.xml |

## AcctgTrans

- Package: org.apache.ofbiz.accounting.ledger
- Table: `ACCTG_TRANS`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1764`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| acctgTransId | id | — | Y |
| acctgTransTypeId | id | — | N |
| description | description | — | N |
| transactionDate | date-time | — | N |
| isPosted | indicator | — | N |
| postedDate | date-time | — | N |
| scheduledPostingDate | date-time | — | N |
| glJournalId | id | — | N |
| glFiscalTypeId | id | — | N |
| voucherRef | short-varchar | — | N |
| voucherDate | date-time | — | N |
| groupStatusId | id | — | N |
| fixedAssetId | id | — | N |
| inventoryItemId | id | — | N |
| physicalInventoryId | id | — | N |
| partyId | id | — | N |
| roleTypeId | id | — | N |
| invoiceId | id | — | N |
| paymentId | id | — | N |
| finAccountTransId | id | — | N |
| shipmentId | id | — | N |
| receiptId | id | — | N |
| workEffortId | id | — | N |
| theirAcctgTransId | id-long | — | N |
| createdDate | date-time | — | N |
| createdByUserLogin | id-vlong | — | N |
| lastModifiedDate | date-time | — | N |
| lastModifiedByUserLogin | id-vlong | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | AcctgTransType | — | acctgTransTypeId → — | — |
| one | GlJournal | — | glJournalId → — | — |
| one | GlFiscalType | — | glFiscalTypeId → — | — |
| one | StatusItem (external) | — | groupStatusId → statusId | framework/common/entitydef/entitymodel.xml |
| one | FixedAsset | — | fixedAssetId → — | — |
| one | PhysicalInventory (external) | — | physicalInventoryId → — | applications/datamodel/entitydef/product-entitymodel.xml |
| one | InventoryItem (external) | — | inventoryItemId → — | applications/datamodel/entitydef/product-entitymodel.xml |
| one | InventoryItemVariance (external) | — | inventoryItemId → —, physicalInventoryId → — | applications/datamodel/entitydef/product-entitymodel.xml |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | RoleType (external) | — | roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | PartyRole (external) | — | partyId → —, roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | Invoice | — | invoiceId → — | — |
| one | Payment | — | paymentId → — | — |
| one | FinAccountTrans | — | finAccountTransId → — | — |
| one | Shipment (external) | — | shipmentId → — | applications/datamodel/entitydef/shipment-entitymodel.xml |
| one | ShipmentReceipt (external) | — | receiptId → — | applications/datamodel/entitydef/shipment-entitymodel.xml |
| one | WorkEffort (external) | — | workEffortId → — | applications/datamodel/entitydef/workeffort-entitymodel.xml |
| many | AcctgTransTypeAttr | — | acctgTransTypeId → — | — |

## AcctgTransAttribute

- Package: org.apache.ofbiz.accounting.ledger
- Table: `ACCTG_TRANS_ATTRIBUTE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1853`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| acctgTransId | id | — | Y |
| attrName | id-long | — | Y |
| attrValue | value | — | N |
| attrDescription | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | AcctgTrans | — | acctgTransId → — | — |
| many | AcctgTransTypeAttr | — | attrName → — | — |

## AcctgTransEntry

- Package: org.apache.ofbiz.accounting.ledger
- Table: `ACCTG_TRANS_ENTRY`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1869`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| acctgTransId | id | — | Y |
| acctgTransEntrySeqId | id | — | Y |
| acctgTransEntryTypeId | id | — | N |
| description | description | — | N |
| voucherRef | short-varchar | — | N |
| partyId | id | — | N |
| roleTypeId | id | — | N |
| theirPartyId | id | — | N |
| productId | id | — | N |
| theirProductId | id | — | N |
| inventoryItemId | id | — | N |
| glAccountTypeId | id | — | N |
| glAccountId | id | — | N |
| organizationPartyId | id | — | N |
| amount | currency-amount | — | N |
| currencyUomId | id | — | N |
| origAmount | currency-amount | — | N |
| origCurrencyUomId | id | — | N |
| debitCreditFlag | indicator | — | N |
| dueDate | date | — | N |
| groupId | id | — | N |
| taxId | id | — | N |
| reconcileStatusId | id | — | N |
| settlementTermId | id | — | N |
| isSummary | indicator | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | AcctgTransEntryType | — | acctgTransEntryTypeId → — | — |
| one | Uom (external) | Currency | currencyUomId → uomId | framework/common/entitydef/entitymodel.xml |
| one | Uom (external) | OrigCurrency | origCurrencyUomId → uomId | framework/common/entitydef/entitymodel.xml |
| one | AcctgTrans | — | acctgTransId → — | — |
| one | InventoryItem (external) | — | inventoryItemId → — | applications/datamodel/entitydef/product-entitymodel.xml |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | RoleType (external) | — | roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | PartyRole (external) | — | partyId → —, roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | GlAccountType | — | glAccountTypeId → — | — |
| one | GlAccount | — | glAccountId → — | — |
| one | GlAccountOrganization | — | glAccountId → —, organizationPartyId → — | — |
| one | StatusItem (external) | — | reconcileStatusId → statusId | framework/common/entitydef/entitymodel.xml |
| one | SettlementTerm | — | settlementTermId → — | — |

## AcctgTransEntryType

- Package: org.apache.ofbiz.accounting.ledger
- Table: `ACCTG_TRANS_ENTRY_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2039`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| acctgTransEntryTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | AcctgTransEntryType | Parent | parentTypeId → acctgTransEntryTypeId | — |

## AcctgTransType

- Package: org.apache.ofbiz.accounting.ledger
- Table: `ACCTG_TRANS_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2051`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| acctgTransTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | AcctgTransType | Parent | parentTypeId → acctgTransTypeId | — |

## AcctgTransTypeAttr

- Package: org.apache.ofbiz.accounting.ledger
- Table: `ACCTG_TRANS_TYPE_ATTR`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2064`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| acctgTransTypeId | id | — | Y |
| attrName | id-long | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | AcctgTransType | — | acctgTransTypeId → — | — |
| many | AcctgTransAttribute | — | attrName → — | — |
| many | AcctgTrans | — | acctgTransTypeId → — | — |

## GlAccount

- Package: org.apache.ofbiz.accounting.ledger
- Table: `GL_ACCOUNT`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2082`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| glAccountId | id | — | Y |
| glAccountTypeId | id | — | N |
| glAccountClassId | id | — | N |
| glResourceTypeId | id | — | N |
| glXbrlClassId | id | — | N |
| parentGlAccountId | id | — | N |
| accountCode | name | — | N |
| accountName | name | — | N |
| description | description | — | N |
| productId | id | — | N |
| externalId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | GlAccountType | — | glAccountTypeId → — | — |
| one | GlAccountClass | — | glAccountClassId → — | — |
| one | GlResourceType | — | glResourceTypeId → — | — |
| one | GlXbrlClass | — | glXbrlClassId → — | — |
| one | GlAccount | Parent | parentGlAccountId → glAccountId | — |

## GlAccountClass

- Package: org.apache.ofbiz.accounting.ledger
- Table: `GL_ACCOUNT_CLASS`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2117`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| glAccountClassId | id | — | Y |
| parentClassId | id | — | N |
| description | description | — | N |
| isAssetClass | indicator | — | N |
| sequenceNum | numeric | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | GlAccountClass | Parent | parentClassId → glAccountClassId | — |

## GlAccountGroup

- Package: org.apache.ofbiz.accounting.ledger
- Table: `GL_ACCOUNT_GROUP`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2131`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| glAccountGroupId | id | — | Y |
| glAccountGroupTypeId | id | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | GlAccountGroupType | — | glAccountGroupTypeId → — | — |

## GlAccountGroupMember

- Package: org.apache.ofbiz.accounting.ledger
- Table: `GL_ACCOUNT_GROUP_MEMBER`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2142`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| glAccountId | id | — | Y |
| glAccountGroupTypeId | id | — | Y |
| glAccountGroupId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | GlAccount | — | glAccountId → — | — |
| one | GlAccountGroup | — | glAccountGroupId → — | — |
| one | GlAccountGroupType | — | glAccountGroupTypeId → — | — |

## GlAccountGroupType

- Package: org.apache.ofbiz.accounting.ledger
- Table: `GL_ACCOUNT_GROUP_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2160`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| glAccountGroupTypeId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## GlAccountHistory

- Package: org.apache.ofbiz.accounting.ledger
- Table: `GL_ACCOUNT_HISTORY`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2167`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| glAccountId | id | — | Y |
| organizationPartyId | id | — | Y |
| customTimePeriodId | id | — | Y |
| openingBalance | currency-amount | — | N |
| postedDebits | currency-amount | — | N |
| postedCredits | currency-amount | — | N |
| endingBalance | currency-amount | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | GlAccount | — | glAccountId → — | — |
| one | Party (external) | — | organizationPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | CustomTimePeriod (external) | — | customTimePeriodId → — | framework/common/entitydef/entitymodel.xml |

## GlAccountOrganization

- Package: org.apache.ofbiz.accounting.ledger
- Table: `GL_ACCOUNT_ORGANIZATION`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2239`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| glAccountId | id | — | Y |
| organizationPartyId | id | — | Y |
| roleTypeId | id | — | N |
| fromDate | date-time | — | N |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | GlAccount | — | glAccountId → — | — |
| one | Party (external) | — | organizationPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | RoleType (external) | — | roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | PartyRole (external) | — | organizationPartyId → partyId, roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |

## GlAccountRole

- Package: org.apache.ofbiz.accounting.ledger
- Table: `GL_ACCOUNT_ROLE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2281`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| glAccountId | id | — | Y |
| partyId | id | — | Y |
| roleTypeId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | GlAccount | — | glAccountId → — | — |
| one-nofk | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | RoleType (external) | — | roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PartyRole (external) | — | partyId → —, roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |

## GlAccountType

- Package: org.apache.ofbiz.accounting.ledger
- Table: `GL_ACCOUNT_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2307`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| glAccountTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | GlAccountType | Parent | parentTypeId → glAccountTypeId | — |

## GlAccountTypeDefault

- Package: org.apache.ofbiz.accounting.ledger
- Table: `GL_ACCOUNT_TYPE_DEFAULT`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2320`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| glAccountTypeId | id | — | Y |
| organizationPartyId | id | — | Y |
| glAccountId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | GlAccountType | — | glAccountTypeId → — | — |
| one | Party (external) | Organization | organizationPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | GlAccount | — | glAccountId → — | — |

## GlBudgetXref

- Package: org.apache.ofbiz.accounting.ledger
- Table: `GL_BUDGET_XREF`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2338`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| glAccountId | id | — | Y |
| budgetItemTypeId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| allocationPercentage | fixed-point | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | GlAccount | — | glAccountId → — | — |
| one | BudgetItemType | — | budgetItemTypeId → — | — |

## GlFiscalType

- Package: org.apache.ofbiz.accounting.ledger
- Table: `GL_FISCAL_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2356`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| glFiscalTypeId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## GlJournal

- Package: org.apache.ofbiz.accounting.ledger
- Table: `GL_JOURNAL`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2364`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| glJournalId | id | — | Y |
| glJournalName | name | — | N |
| organizationPartyId | id | — | N |
| isPosted | indicator | — | N |
| postedDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party (external) | — | organizationPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |

## GlReconciliation

- Package: org.apache.ofbiz.accounting.ledger
- Table: `GL_RECONCILIATION`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2377`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| glReconciliationId | id | — | Y |
| glReconciliationName | name | — | N |
| description | description | — | N |
| createdDate | date-time | — | N |
| createdByUserLogin | id-vlong | — | N |
| lastModifiedDate | date-time | — | N |
| lastModifiedByUserLogin | id-vlong | — | N |
| glAccountId | id | — | N |
| statusId | id | — | N |
| organizationPartyId | id | — | N |
| reconciledBalance | currency-amount | — | N |
| openingBalance | currency-amount | — | N |
| reconciledDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | GlAccount | — | glAccountId → — | — |
| one | Party (external) | — | organizationPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |

## GlReconciliationEntry

- Package: org.apache.ofbiz.accounting.ledger
- Table: `GL_RECONCILIATION_ENTRY`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2404`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| glReconciliationId | id | — | Y |
| acctgTransId | id | — | Y |
| acctgTransEntrySeqId | id | — | Y |
| reconciledAmount | currency-amount | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | GlReconciliation | — | glReconciliationId → — | — |
| one | AcctgTransEntry | — | acctgTransId → —, acctgTransEntrySeqId → — | — |
| one-nofk | AcctgTrans | — | acctgTransId → — | — |

## GlResourceType

- Package: org.apache.ofbiz.accounting.ledger
- Table: `GL_RESOURCE_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2425`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| glResourceTypeId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## GlXbrlClass

- Package: org.apache.ofbiz.accounting.ledger
- Table: `GL_XBRL_CLASS`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2433`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| glXbrlClassId | id | — | Y |
| parentGlXbrlClassId | id | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | GlXbrlClass | Parent | parentGlXbrlClassId → glXbrlClassId | — |

## PartyAcctgPreference

- Package: org.apache.ofbiz.accounting.ledger
- Table: `PARTY_ACCTG_PREFERENCE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2444`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| fiscalYearStartMonth | numeric | — | N |
| fiscalYearStartDay | numeric | — | N |
| taxFormId | id | — | N |
| cogsMethodId | id | — | N |
| baseCurrencyUomId | id | — | N |
| invoiceSeqCustMethId | id | — | N |
| invoiceIdPrefix | very-short | — | N |
| lastInvoiceNumber | numeric | — | N |
| lastInvoiceRestartDate | date-time | — | N |
| useInvoiceIdForReturns | indicator | — | N |
| quoteSeqCustMethId | id | — | N |
| quoteIdPrefix | very-short | — | N |
| lastQuoteNumber | numeric | — | N |
| orderSeqCustMethId | id | — | N |
| orderIdPrefix | very-short | — | N |
| lastOrderNumber | numeric | — | N |
| refundPaymentMethodId | id | — | N |
| errorGlJournalId | id | — | N |
| enableAccounting | indicator | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | Enumeration (external) | TaxForm | taxFormId → enumId | framework/common/entitydef/entitymodel.xml |
| one | Enumeration (external) | Cogs | cogsMethodId → enumId | framework/common/entitydef/entitymodel.xml |
| one | Uom (external) | — | baseCurrencyUomId → uomId | framework/common/entitydef/entitymodel.xml |
| one | CustomMethod (external) | Invoice | invoiceSeqCustMethId → customMethodId | framework/common/entitydef/entitymodel.xml |
| one | CustomMethod (external) | Quote | quoteSeqCustMethId → customMethodId | framework/common/entitydef/entitymodel.xml |
| one | CustomMethod (external) | Order | orderSeqCustMethId → customMethodId | framework/common/entitydef/entitymodel.xml |
| one | PaymentMethod | — | refundPaymentMethodId → paymentMethodId | — |
| one | GlJournal | — | errorGlJournalId → glJournalId | — |

## PartyPrefDocTypeTpl

- Package: org.apache.ofbiz.accounting.ledger
- Table: `PARTY_PREF_DOC_TYPE_TPL`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2501`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyPrefDocTypeTplId | id | — | Y |
| partyId | id | — | N |
| fromDate | date-time | — | N |
| thruDate | date-time | — | N |
| invoiceTypeId | id | — | N |
| orderTypeId | id | — | N |
| quoteTypeId | id | — | N |
| customScreenId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PartyAcctgPreference | — | partyId → — | — |
| one | InvoiceType | — | invoiceTypeId → — | — |
| one | QuoteType (external) | — | quoteTypeId → — | applications/datamodel/entitydef/order-entitymodel.xml |
| one | OrderType (external) | — | orderTypeId → — | applications/datamodel/entitydef/order-entitymodel.xml |

## ProductAverageCost

- Package: org.apache.ofbiz.accounting.ledger
- Table: `PRODUCT_AVERAGE_COST`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2540`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| productAverageCostTypeId | id | — | Y |
| organizationPartyId | id | — | Y |
| productId | id | — | Y |
| facilityId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| averageCost | fixed-point | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | ProductAverageCostType | — | productAverageCostTypeId → — | — |
| one | Party (external) | — | organizationPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | Product (external) | — | productId → — | applications/datamodel/entitydef/product-entitymodel.xml |
| one | Facility (external) | — | facilityId → — | applications/datamodel/entitydef/product-entitymodel.xml |

## ProductAverageCostType

- Package: org.apache.ofbiz.accounting.ledger
- Table: `PRODUCT_AVERAGE_COST_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2569`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| productAverageCostTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | ProductAverageCostType | Parent | parentTypeId → productAverageCostTypeId | — |

## SettlementTerm

- Package: org.apache.ofbiz.accounting.ledger
- Table: `SETTLEMENT_TERM`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2581`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| settlementTermId | id | — | Y |
| termName | name | — | N |
| termValue | numeric | — | N |
| uomId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## VarianceReasonGlAccount

- Package: org.apache.ofbiz.accounting.ledger
- Table: `VARIANCE_REASON_GL_ACCOUNT`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2590`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| varianceReasonId | id | — | Y |
| organizationPartyId | id | — | Y |
| glAccountId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | VarianceReason (external) | — | varianceReasonId → — | applications/datamodel/entitydef/product-entitymodel.xml |
| one | Party (external) | Organization | organizationPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | GlAccount | — | glAccountId → — | — |

## BillingAccount

- Package: org.apache.ofbiz.accounting.payment
- Table: `BILLING_ACCOUNT`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2613`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| billingAccountId | id | — | Y |
| accountLimit | currency-amount | — | N |
| accountCurrencyUomId | id | — | N |
| contactMechId | id | — | N |
| fromDate | date-time | — | N |
| thruDate | date-time | — | N |
| description | description | — | N |
| externalAccountId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | ContactMech (external) | — | contactMechId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | Uom (external) | — | accountCurrencyUomId → uomId | framework/common/entitydef/entitymodel.xml |
| one | PostalAddress (external) | — | contactMechId → — | applications/datamodel/entitydef/party-entitymodel.xml |

## BillingAccountRole

- Package: org.apache.ofbiz.accounting.payment
- Table: `BILLING_ACCOUNT_ROLE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2682`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| billingAccountId | id | — | Y |
| partyId | id | — | Y |
| roleTypeId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | BillingAccount | — | billingAccountId → — | — |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | RoleType (external) | — | roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PartyRole (external) | — | partyId → —, roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |

## BillingAccountTerm

- Package: org.apache.ofbiz.accounting.payment
- Table: `BILLING_ACCOUNT_TERM`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2726`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| billingAccountTermId | id | — | Y |
| billingAccountId | id | — | N |
| termTypeId | id | — | N |
| termValue | currency-amount | — | N |
| termDays | numeric | — | N |
| uomId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Uom (external) | — | uomId → — | framework/common/entitydef/entitymodel.xml |
| one | TermType (external) | — | termTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | BillingAccount | — | billingAccountId → — | — |

## BillingAccountTermAttr

- Package: org.apache.ofbiz.accounting.payment
- Table: `BILLING_ACCOUNT_TERM_ATTR`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2746`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| billingAccountTermId | id | — | Y |
| attrName | id-long | — | Y |
| attrValue | value | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | BillingAccountTerm | — | billingAccountTermId → — | — |
| many | TermTypeAttr (external) | — | attrName → — | applications/datamodel/entitydef/party-entitymodel.xml |

## CreditCard

- Package: org.apache.ofbiz.accounting.payment
- Table: `CREDIT_CARD`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2761`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| paymentMethodId | id | — | Y |
| cardType | short-varchar | — | N |
| cardNumber | credit-card-number | — | N |
| validFromDate | credit-card-date | — | N |
| expireDate | credit-card-date | — | N |
| issueNumber | credit-card-date | — | N |
| companyNameOnCard | name | — | N |
| titleOnCard | name | — | N |
| firstNameOnCard | name | — | N |
| middleNameOnCard | name | — | N |
| lastNameOnCard | name | — | N |
| suffixOnCard | name | — | N |
| contactMechId | id | — | N |
| consecutiveFailedAuths | numeric | — | N |
| lastFailedAuthDate | date-time | — | N |
| consecutiveFailedNsf | numeric | — | N |
| lastFailedNsfDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PaymentMethod | — | paymentMethodId → — | — |
| one | ContactMech (external) | — | contactMechId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PostalAddress (external) | — | contactMechId → — | applications/datamodel/entitydef/party-entitymodel.xml |

## CreditCardTypeGlAccount

- Package: org.apache.ofbiz.accounting.payment
- Table: `CREDIT_CARD_TYPE_GL_ACCOUNT`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2792`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| cardType | short-varchar | — | Y |
| organizationPartyId | id | — | Y |
| glAccountId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## Deduction

- Package: org.apache.ofbiz.accounting.payment
- Table: `DEDUCTION`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2801`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| deductionId | id | — | Y |
| deductionTypeId | id | — | N |
| paymentId | id | — | N |
| amount | currency-amount | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | DeductionType | — | deductionTypeId → — | — |
| one | Payment | — | paymentId → — | — |

## DeductionType

- Package: org.apache.ofbiz.accounting.payment
- Table: `DEDUCTION_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2816`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| deductionTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | DeductionType | Parent | parentTypeId → deductionTypeId | — |

## EftAccount

- Package: org.apache.ofbiz.accounting.payment
- Table: `EFT_ACCOUNT`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2828`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| paymentMethodId | id | — | Y |
| bankName | name | — | N |
| routingNumber | short-varchar | — | N |
| accountType | short-varchar | — | N |
| accountNumber | long-varchar | — | N |
| nameOnAccount | name | — | N |
| companyNameOnAccount | name | — | N |
| contactMechId | id | — | N |
| yearsAtBank | numeric | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PaymentMethod | — | paymentMethodId → — | — |
| one | ContactMech (external) | — | contactMechId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PostalAddress (external) | — | contactMechId → — | applications/datamodel/entitydef/party-entitymodel.xml |

## CheckAccount

- Package: org.apache.ofbiz.accounting.payment
- Table: `CHECK_ACCOUNT`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2852`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| paymentMethodId | id | — | Y |
| bankName | name | — | N |
| routingNumber | short-varchar | — | N |
| accountType | short-varchar | — | N |
| accountNumber | long-varchar | — | N |
| nameOnAccount | name | — | N |
| companyNameOnAccount | name | — | N |
| contactMechId | id | — | N |
| branchCode | short-varchar | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PaymentMethod | — | paymentMethodId → — | — |
| one | ContactMech (external) | — | contactMechId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PostalAddress (external) | — | contactMechId → — | applications/datamodel/entitydef/party-entitymodel.xml |

## GiftCard

- Package: org.apache.ofbiz.accounting.payment
- Table: `GIFT_CARD`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2876`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| paymentMethodId | id | — | Y |
| cardNumber | long-varchar | — | N |
| pinNumber | long-varchar | — | N |
| expireDate | credit-card-date | — | N |
| contactMechId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PaymentMethod | — | paymentMethodId → — | — |
| one | ContactMech (external) | — | contactMechId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PostalAddress (external) | — | contactMechId → — | applications/datamodel/entitydef/party-entitymodel.xml |

## GiftCardFulfillment

- Package: org.apache.ofbiz.accounting.payment
- Table: `GIFT_CARD_FULFILLMENT`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2895`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| fulfillmentId | id | — | Y |
| typeEnumId | id | — | N |
| merchantId | id-vlong | — | N |
| partyId | id | — | N |
| orderId | id | — | N |
| orderItemSeqId | id | — | N |
| surveyResponseId | id | — | N |
| cardNumber | long-varchar | — | N |
| pinNumber | long-varchar | — | N |
| amount | currency-amount | — | N |
| responseCode | short-varchar | — | N |
| referenceNum | short-varchar | — | N |
| authCode | short-varchar | — | N |
| fulfillmentDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Enumeration (external) | — | typeEnumId → enumId | framework/common/entitydef/entitymodel.xml |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | OrderHeader (external) | — | orderId → — | applications/datamodel/entitydef/order-entitymodel.xml |
| one | OrderItem (external) | — | orderId → —, orderItemSeqId → — | applications/datamodel/entitydef/order-entitymodel.xml |
| one | SurveyResponse (external) | — | surveyResponseId → — | applications/datamodel/entitydef/content-entitymodel.xml |

## Payment

- Package: org.apache.ofbiz.accounting.payment
- Table: `PAYMENT`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2931`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| paymentId | id | — | Y |
| paymentTypeId | id | — | N |
| paymentMethodTypeId | id | — | N |
| paymentMethodId | id | — | N |
| paymentGatewayResponseId | id | — | N |
| paymentPreferenceId | id | — | N |
| partyIdFrom | id | — | N |
| partyIdTo | id | — | N |
| roleTypeIdTo | id | — | N |
| statusId | id | — | N |
| effectiveDate | date-time | — | N |
| paymentRefNum | short-varchar | — | N |
| amount | currency-amount | — | N |
| currencyUomId | id | — | N |
| comments | comment | — | N |
| finAccountTransId | id | — | N |
| overrideGlAccountId | id | — | N |
| actualCurrencyAmount | currency-amount | — | N |
| actualCurrencyUomId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PaymentType | — | paymentTypeId → — | — |
| many | PaymentTypeAttr | — | paymentTypeId → — | — |
| one | PaymentMethodType | — | paymentMethodTypeId → — | — |
| one | PaymentMethod | — | paymentMethodId → — | — |
| one | Uom (external) | Currency | currencyUomId → uomId | framework/common/entitydef/entitymodel.xml |
| one | Uom (external) | ActualCurrency | actualCurrencyUomId → uomId | framework/common/entitydef/entitymodel.xml |
| one-nofk | CreditCard | — | paymentMethodId → — | — |
| one-nofk | EftAccount | — | paymentMethodId → — | — |
| one-nofk | GiftCard | — | paymentMethodId → — | — |
| one | OrderPaymentPreference (external) | — | paymentPreferenceId → orderPaymentPreferenceId | applications/datamodel/entitydef/order-entitymodel.xml |
| one | PaymentGatewayResponse | — | paymentGatewayResponseId → — | — |
| one | Party (external) | From | partyIdFrom → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | Party (external) | To | partyIdTo → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | RoleType (external) | To | roleTypeIdTo → roleTypeId | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | PartyRole (external) | To | partyIdTo → partyId, roleTypeIdTo → roleTypeId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |
| one | FinAccountTrans | — | finAccountTransId → — | — |
| one | GlAccount | — | overrideGlAccountId → glAccountId | — |

## PaymentApplication

- Package: org.apache.ofbiz.accounting.payment
- Table: `PAYMENT_APPLICATION`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3119`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| paymentApplicationId | id | — | Y |
| paymentId | id | — | N |
| invoiceId | id | — | N |
| invoiceItemSeqId | id | — | N |
| billingAccountId | id | — | N |
| overrideGlAccountId | id | — | N |
| toPaymentId | id | — | N |
| taxAuthGeoId | id | — | N |
| amountApplied | currency-amount | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Payment | — | paymentId → — | — |
| one | Invoice | — | invoiceId → — | — |
| one-nofk | InvoiceItem | — | invoiceId → —, invoiceItemSeqId → — | — |
| one | BillingAccount | — | billingAccountId → — | — |
| one | Payment | To | toPaymentId → paymentId | — |
| one | Geo (external) | — | taxAuthGeoId → geoId | framework/common/entitydef/entitymodel.xml |
| one | GlAccount | — | overrideGlAccountId → glAccountId | — |

## PaymentAttribute

- Package: org.apache.ofbiz.accounting.payment
- Table: `PAYMENT_ATTRIBUTE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3155`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| paymentId | id | — | Y |
| attrName | id-long | — | Y |
| attrValue | value | — | N |
| attrDescription | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Payment | — | paymentId → — | — |
| many | PaymentTypeAttr | — | attrName → — | — |

## PaymentBudgetAllocation

- Package: org.apache.ofbiz.accounting.payment
- Table: `PAYMENT_BUDGET_ALLOCATION`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3171`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| budgetId | id | — | Y |
| budgetItemSeqId | id | — | Y |
| paymentId | id | — | Y |
| amount | currency-amount | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Budget | — | budgetId → — | — |
| one-nofk | BudgetItem | — | budgetId → —, budgetItemSeqId → — | — |
| one | Payment | — | paymentId → — | — |

## PaymentContent

- Package: org.apache.ofbiz.accounting.payment
- Table: `PAYMENT_CONTENT`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3192`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| paymentId | id | — | Y |
| paymentContentTypeId | id | — | Y |
| contentId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Payment | — | paymentId → — | — |
| one | Content (external) | — | contentId → — | applications/datamodel/entitydef/content-entitymodel.xml |
| one | PaymentContentType | — | paymentContentTypeId → — | — |

## PaymentContentType

- Package: org.apache.ofbiz.accounting.payment
- Table: `PAYMENT_CONTENT_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3214`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| paymentContentTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PaymentContentType | Parent | parentTypeId → paymentContentTypeId | — |

## PaymentMethod

- Package: org.apache.ofbiz.accounting.payment
- Table: `PAYMENT_METHOD`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3258`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| paymentMethodId | id | — | Y |
| paymentMethodTypeId | id | — | N |
| partyId | id | — | N |
| glAccountId | id | — | N |
| finAccountId | id | — | N |
| description | description | — | N |
| fromDate | date-time | — | N |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PaymentMethodType | — | paymentMethodTypeId → — | — |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | GlAccount | — | glAccountId → — | — |
| one | FinAccount | — | finAccountId → — | — |

## PaymentMethodType

- Package: org.apache.ofbiz.accounting.payment
- Table: `PAYMENT_METHOD_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3390`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| paymentMethodTypeId | id | — | Y |
| description | description | — | N |
| defaultGlAccountId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | GlAccount | Default | defaultGlAccountId → glAccountId | — |

## PaymentMethodTypeGlAccount

- Package: org.apache.ofbiz.accounting.payment
- Table: `PAYMENT_METHOD_TYPE_GL_ACCOUNT`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3401`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| paymentMethodTypeId | id | — | Y |
| organizationPartyId | id | — | Y |
| glAccountId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PaymentMethodType | — | paymentMethodTypeId → — | — |
| one | Party (external) | Organization | organizationPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | GlAccount | — | glAccountId → — | — |

## PaymentType

- Package: org.apache.ofbiz.accounting.payment
- Table: `PAYMENT_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3419`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| paymentTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PaymentType | Parent | parentTypeId → paymentTypeId | — |

## PaymentTypeAttr

- Package: org.apache.ofbiz.accounting.payment
- Table: `PAYMENT_TYPE_ATTR`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3431`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| paymentTypeId | id | — | Y |
| attrName | id-long | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PaymentType | — | paymentTypeId → — | — |
| many | PaymentAttribute | — | attrName → — | — |
| many | Payment | — | paymentTypeId → — | — |

## PaymentGlAccountTypeMap

- Package: org.apache.ofbiz.accounting.payment
- Table: `PAYMENT_GL_ACCOUNT_TYPE_MAP`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3449`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| paymentTypeId | id | — | Y |
| organizationPartyId | id | — | Y |
| glAccountTypeId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PaymentType | — | paymentTypeId → — | — |
| one | Party (external) | — | organizationPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | GlAccountType | — | glAccountTypeId → — | — |

## PaymentGatewayConfigType

- Package: org.apache.ofbiz.accounting.payment
- Table: `PAYMENT_GATEWAY_CONFIG_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3467`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| paymentGatewayConfigTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PaymentGatewayConfigType | Parent | parentTypeId → paymentGatewayConfigTypeId | — |
| many | PaymentGatewayConfigType | Sibling | parentTypeId → — | — |

## PaymentGatewayConfig

- Package: org.apache.ofbiz.accounting.payment
- Table: `PAYMENT_GATEWAY_CONFIG`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3482`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| paymentGatewayConfigId | id | — | Y |
| paymentGatewayConfigTypeId | id | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PaymentGatewayConfigType | — | paymentGatewayConfigTypeId → paymentGatewayConfigTypeId | — |

## PaymentGatewayRespMsg

- Package: org.apache.ofbiz.accounting.payment
- Table: `PAYMENT_GATEWAY_RESP_MSG`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3493`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| paymentGatewayRespMsgId | id | — | Y |
| paymentGatewayResponseId | id | — | N |
| pgrMessage | very-long | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PaymentGatewayResponse | — | paymentGatewayResponseId → — | — |

## PaymentGatewayResponse

- Package: org.apache.ofbiz.accounting.payment
- Table: `PAYMENT_GATEWAY_RESPONSE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3504`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| paymentGatewayResponseId | id | — | Y |
| paymentServiceTypeEnumId | id | — | N |
| orderPaymentPreferenceId | id | — | N |
| paymentMethodTypeId | id | — | N |
| paymentMethodId | id | — | N |
| transCodeEnumId | id | — | N |
| amount | currency-amount | — | N |
| currencyUomId | id | — | N |
| referenceNum | short-varchar | — | N |
| altReference | short-varchar | — | N |
| subReference | short-varchar | — | N |
| gatewayCode | short-varchar | — | N |
| gatewayFlag | short-varchar | — | N |
| gatewayAvsResult | short-varchar | — | N |
| gatewayCvResult | short-varchar | — | N |
| gatewayScoreResult | short-varchar | — | N |
| gatewayMessage | long-varchar | — | N |
| transactionDate | date-time | — | N |
| resultDeclined | indicator | — | N |
| resultNsf | indicator | — | N |
| resultBadExpire | indicator | — | N |
| resultBadCardNumber | indicator | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Enumeration (external) | ServiceType | paymentServiceTypeEnumId → enumId | framework/common/entitydef/entitymodel.xml |
| one | Enumeration (external) | TranCode | transCodeEnumId → enumId | framework/common/entitydef/entitymodel.xml |
| one | Uom (external) | — | currencyUomId → uomId | framework/common/entitydef/entitymodel.xml |
| one | OrderPaymentPreference (external) | — | orderPaymentPreferenceId → — | applications/datamodel/entitydef/order-entitymodel.xml |
| one | PaymentMethodType | — | paymentMethodTypeId → — | — |
| one | PaymentMethod | — | paymentMethodId → — | — |

## PaymentGroup

- Package: org.apache.ofbiz.accounting.payment
- Table: `PAYMENT_GROUP`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3550`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| paymentGroupId | id | — | Y |
| paymentGroupTypeId | id | — | N |
| paymentGroupName | name | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PaymentGroupType | — | paymentGroupTypeId → — | — |

## PaymentGroupType

- Package: org.apache.ofbiz.accounting.payment
- Table: `PAYMENT_GROUP_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3562`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| paymentGroupTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PaymentGroupType | Parent | parentTypeId → paymentGroupTypeId | — |

## PaymentGroupMember

- Package: org.apache.ofbiz.accounting.payment
- Table: `PAYMENT_GROUP_MEMBER`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3575`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| paymentGroupId | id | — | Y |
| paymentId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| sequenceNum | numeric | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PaymentGroup | — | paymentGroupId → — | — |
| one | Payment | — | paymentId → — | — |

## PayPalPaymentMethod

- Package: org.apache.ofbiz.accounting.payment
- Table: `PAY_PAL_PAYMENT_METHOD`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3595`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| paymentMethodId | id | — | Y |
| payerId | id | — | N |
| payerStatus | short-varchar | — | N |
| avsAddr | indicator | — | N |
| avsZip | indicator | — | N |
| correlationId | id | — | N |
| contactMechId | id | — | N |
| transactionId | short-varchar | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PaymentMethod | — | paymentMethodId → — | — |
| one | ContactMech (external) | — | contactMechId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PostalAddress (external) | — | contactMechId → — | applications/datamodel/entitydef/party-entitymodel.xml |

## PartyTaxAuthInfo

- Package: org.apache.ofbiz.accounting.tax
- Table: `PARTY_TAX_AUTH_INFO`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3622`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| taxAuthGeoId | id | — | Y |
| taxAuthPartyId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| partyTaxId | id-long | — | N |
| isExempt | indicator | — | N |
| isNexus | indicator | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | TaxAuthority | — | taxAuthGeoId → —, taxAuthPartyId → — | — |

## TaxAuthority

- Package: org.apache.ofbiz.accounting.tax
- Table: `TAX_AUTHORITY`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3658`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| taxAuthGeoId | id | — | Y |
| taxAuthPartyId | id | — | Y |
| requireTaxIdForExemption | indicator | — | N |
| taxIdFormatPattern | long-varchar | — | N |
| includeTaxInPrice | indicator | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Geo (external) | TaxAuth | taxAuthGeoId → geoId | framework/common/entitydef/entitymodel.xml |
| one | Party (external) | TaxAuth | taxAuthPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |

## TaxAuthorityAssoc

- Package: org.apache.ofbiz.accounting.tax
- Table: `TAX_AUTHORITY_ASSOC`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3687`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| taxAuthGeoId | id | — | Y |
| taxAuthPartyId | id | — | Y |
| toTaxAuthGeoId | id | — | Y |
| toTaxAuthPartyId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| taxAuthorityAssocTypeId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | TaxAuthority | — | taxAuthGeoId → —, taxAuthPartyId → — | — |
| one | TaxAuthority | To | toTaxAuthGeoId → taxAuthGeoId, toTaxAuthPartyId → taxAuthPartyId | — |
| one | TaxAuthorityAssocType | — | taxAuthorityAssocTypeId → — | — |

## TaxAuthorityAssocType

- Package: org.apache.ofbiz.accounting.tax
- Table: `TAX_AUTHORITY_ASSOC_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3712`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| taxAuthorityAssocTypeId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## TaxAuthorityCategory

- Package: org.apache.ofbiz.accounting.tax
- Table: `TAX_AUTHORITY_CATEGORY`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3720`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| taxAuthGeoId | id | — | Y |
| taxAuthPartyId | id | — | Y |
| productCategoryId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | TaxAuthority | — | taxAuthGeoId → —, taxAuthPartyId → — | — |
| one | ProductCategory (external) | — | productCategoryId → — | applications/datamodel/entitydef/product-entitymodel.xml |

## TaxAuthorityGlAccount

- Package: org.apache.ofbiz.accounting.tax
- Table: `TAX_AUTHORITY_GL_ACCOUNT`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3744`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| taxAuthGeoId | id | — | Y |
| taxAuthPartyId | id | — | Y |
| organizationPartyId | id | — | Y |
| glAccountId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | TaxAuthority | — | taxAuthGeoId → —, taxAuthPartyId → — | — |
| one | Party (external) | Organization | organizationPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | GlAccount | — | glAccountId → — | — |

## TaxAuthorityRateProduct

- Package: org.apache.ofbiz.accounting.tax
- Table: `TAX_AUTHORITY_RATE_PRODUCT`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3780`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| taxAuthorityRateSeqId | id | — | Y |
| taxAuthGeoId | id | — | N |
| taxAuthPartyId | id | — | N |
| taxAuthorityRateTypeId | id | — | N |
| productStoreId | id | — | N |
| productCategoryId | id | — | N |
| titleTransferEnumId | id | — | N |
| minItemPrice | currency-amount | — | N |
| minPurchase | currency-amount | — | N |
| taxShipping | indicator | — | N |
| taxPercentage | fixed-point | — | N |
| taxPromotions | indicator | — | N |
| fromDate | date-time | — | N |
| thruDate | date-time | — | N |
| description | description | — | N |
| isTaxInShippingPrice | indicator | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | TaxAuthority | — | taxAuthGeoId → —, taxAuthPartyId → — | — |
| one | TaxAuthorityRateType | — | taxAuthorityRateTypeId → — | — |
| one | ProductStore (external) | — | productStoreId → — | applications/datamodel/entitydef/product-entitymodel.xml |
| one | ProductCategory (external) | — | productCategoryId → — | applications/datamodel/entitydef/product-entitymodel.xml |

## TaxAuthorityRateType

- Package: org.apache.ofbiz.accounting.tax
- Table: `TAX_AUTHORITY_RATE_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3812`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| taxAuthorityRateTypeId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## PartyGlAccount

- Package: org.apache.ofbiz.accounting.ledger
- Table: `PARTY_GL_ACCOUNT`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3820`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| organizationPartyId | id | — | Y |
| partyId | id | — | Y |
| roleTypeId | id | — | Y |
| glAccountTypeId | id | — | Y |
| glAccountId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party (external) | Organization | organizationPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | RoleType (external) | — | roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PartyRole (external) | — | partyId → —, roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | GlAccountType | — | glAccountTypeId → — | — |
| one | GlAccount | — | glAccountId → — | — |

## RateType

- Package: org.apache.ofbiz.accounting.rate
- Table: `RATE_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3875`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| rateTypeId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## RateAmount

- Package: org.apache.ofbiz.accounting.rate
- Table: `RATE_AMOUNT`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3882`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| rateTypeId | id | — | Y |
| rateCurrencyUomId | id | — | Y |
| periodTypeId | id | — | Y |
| workEffortId | id | — | Y |
| partyId | id | — | Y |
| emplPositionTypeId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| rateAmount | currency-amount | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | RateType | — | rateTypeId → — | — |
| one | Uom (external) | — | rateCurrencyUomId → uomId | framework/common/entitydef/entitymodel.xml |
| one | WorkEffort (external) | — | workEffortId → — | applications/datamodel/entitydef/workeffort-entitymodel.xml |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | EmplPositionType (external) | — | emplPositionTypeId → — | applications/datamodel/entitydef/humanres-entitymodel.xml |
| one | PeriodType (external) | — | periodTypeId → — | framework/common/entitydef/entitymodel.xml |

## PartyRate

- Package: org.apache.ofbiz.accounting.rate
- Table: `PARTY_RATE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3952`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| rateTypeId | id | — | Y |
| defaultRate | indicator | — | N |
| percentageUsed | floating-point | — | N |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | RateType | — | rateTypeId → — | — |

## GlAccountCategory

- Package: org.apache.ofbiz.accounting.ledger
- Table: `GL_ACCOUNT_CATEGORY`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3971`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| glAccountCategoryId | id | — | Y |
| glAccountCategoryTypeId | id | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | GlAccountCategoryType | — | glAccountCategoryTypeId → — | — |

## GlAccountCategoryMember

- Package: org.apache.ofbiz.accounting.ledger
- Table: `GL_ACCOUNT_CATEGORY_MEMBER`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3983`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| glAccountId | id | — | Y |
| glAccountCategoryId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| amountPercentage | fixed-point | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | GlAccount | — | glAccountId → — | — |
| one | GlAccountCategory | — | glAccountCategoryId → — | — |

## GlAccountCategoryType

- Package: org.apache.ofbiz.accounting.ledger
- Table: `GL_ACCOUNT_CATEGORY_TYPE`
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:4002`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| glAccountCategoryTypeId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## View entities

### FinAccountAndRole

- Package: org.apache.ofbiz.accounting.finaccount
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:380`
- Member entities: FA = FinAccount, FR = FinAccountRole

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| FA | FR | finAccountId → — |

### FinAccountTransSum

- Package: org.apache.ofbiz.accounting.finaccount
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:605`
- Member entities: FAT = FinAccountTrans

| Entity alias | Related alias | Key maps |
| --- | --- | --- |

### FinAccountAuthSum

- Package: org.apache.ofbiz.accounting.finaccount
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:614`
- Member entities: FAA = FinAccountAuth

| Entity alias | Related alias | Key maps |
| --- | --- | --- |

### FixedAssetAndGeoPoint

- Package: org.apache.ofbiz.accounting.fixedasset
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:751`
- Member entities: FA = FixedAsset, FAGPT = FixedAssetGeoPoint, GPT = GeoPoint

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| FA | FAGPT | fixedAssetId → — |
| FAGPT | GPT | geoPointId → — |

### PartyFixedAssetAssignAndRole

- Package: org.apache.ofbiz.accounting.fixedasset
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1050`
- Member entities: PFA = PartyFixedAssetAssignment, RT = RoleType

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PFA | RT | roleTypeId → — |

### FixedAssetMaintWorkEffort

- Package: org.apache.ofbiz.accounting.fixedasset
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1137`
- Member entities: FA = FixedAsset, FAM = FixedAssetMaint, WE = WorkEffort

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| FAM | FA | fixedAssetId → — |
| FAM | WE | scheduleWorkEffortId → workEffortId |

### InvoiceAndApplAndPayment

- Package: org.apache.ofbiz.accounting.invoice
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1628`
- Member entities: INV = Invoice, PAP = PaymentApplication, PAM = Payment

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| INV | PAP | invoiceId → — |
| PAP | PAM | paymentId → — |

### InvoiceAndType

- Package: org.apache.ofbiz.accounting.invoice
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1645`
- Member entities: INV = Invoice, IVT = InvoiceType

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| INV | IVT | invoiceTypeId → — |

### InvoiceAndRole

- Package: org.apache.ofbiz.accounting.invoice
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1668`
- Member entities: INV = Invoice, INR = InvoiceRole

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| INV | INR | invoiceId → — |

### InvoiceItemAndAssocProduct

- Package: org.apache.ofbiz.accounting.invoice
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1679`
- Member entities: INTM = InvoiceItem, IIA = InvoiceItemAssoc, PROD = Product

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| INTM | IIA | invoiceId → invoiceIdFrom, invoiceItemSeqId → invoiceItemSeqIdFrom |
| INTM | PROD | productId → — |

### InvItemAndOrdItem

- Package: org.apache.ofbiz.accounting.invoice
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1699`
- Member entities: INVITM = InvoiceItem, ORDBIL = OrderItemBilling

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| INVITM | ORDBIL | invoiceId → —, invoiceItemSeqId → — |

### InvoiceItemAndShipmentView

- Package: org.apache.ofbiz.accounting.invoice
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1710`
- Member entities: INVITM = InvoiceItem, ORDBIL = OrderItemBilling, ITMISS = ItemIssuance, SHIP = Shipment

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| INVITM | ORDBIL | invoiceId → —, invoiceItemSeqId → — |
| ORDBIL | ITMISS | itemIssuanceId → — |
| ITMISS | SHIP | shipmentId → — |

### InvoiceContentAndInfo

- Package: org.apache.ofbiz.accounting.invoice
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1729`
- Member entities: INVC = InvoiceContent, CO = Content, DR = DataResource

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| INVC | CO | contentId → — |
| CO | DR | dataResourceId → — |

### AcctgTransAndEntries

- Package: org.apache.ofbiz.accounting.ledger
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:1941`
- Member entities: ATR = AcctgTrans, ATT = AcctgTransType, ATE = AcctgTransEntry, GLA = GlAccount, GLAC = GlAccountClass

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| ATR | ATE | acctgTransId → — |
| ATR | ATT | acctgTransTypeId → — |
| ATE | GLA | glAccountId → — |
| GLA | GLAC | glAccountClassId → — |

### AcctgTransEntrySums

- Package: org.apache.ofbiz.accounting.ledger
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2014`
- Member entities: ATE = AcctgTransEntry, ACT = AcctgTrans, GLA = GlAccount

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| ATE | ACT | acctgTransId → — |
| ATE | GLA | glAccountId → — |

### GlAccountAndHistory

- Package: org.apache.ofbiz.accounting.ledger
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2190`
- Member entities: GLA = GlAccount, GLAH = GlAccountHistory, GLAC = GlAccountClass

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| GLA | GLAH | glAccountId → — |
| GLA | GLAC | glAccountClassId → — |

### GlAccountAndHistoryTotals

- Package: org.apache.ofbiz.accounting.ledger
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2217`
- Member entities: GLA = GlAccount, GLAH = GlAccountHistory

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| GLA | GLAH | glAccountId → — |

### GlAccountOrganizationAndClass

- Package: org.apache.ofbiz.accounting.ledger
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2263`
- Member entities: GLAO = GlAccountOrganization, GLA = GlAccount

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| GLAO | GLA | glAccountId → — |

### PartyPrefDocTypeTplAndCustomScreen

- Package: org.apache.ofbiz.accounting.ledger
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2529`
- Member entities: PAP = PartyPrefDocTypeTpl, CS = CustomScreen

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PAP | CS | customScreenId → — |

### BillingAccountAndRole

- Package: org.apache.ofbiz.accounting.payment
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2636`
- Member entities: BA = BillingAccount, BR = BillingAccountRole

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| BA | BR | billingAccountId → — |

### BillingAccountRoleAndAddress

- Package: org.apache.ofbiz.accounting.payment
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:2708`
- Member entities: BAR = BillingAccountRole, PCM = PartyContactMech, PADDR = PostalAddress

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| BAR | PCM | partyId → — |
| PCM | PADDR | contactMechId → — |

### PaymentAndTypePartyNameView

- Package: org.apache.ofbiz.accounting.payment
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3010`
- Member entities: PY = Payment, FPNV = PartyNameView, TPNV = PartyNameView, TY = PaymentType, PMT = PaymentMethodType, SI = StatusItem

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PY | FPNV | partyIdFrom → partyId |
| PY | TPNV | partyIdTo → partyId |
| PY | TY | paymentTypeId → — |
| PY | PMT | paymentMethodTypeId → — |
| PY | SI | statusId → — |

### PaymentAndType

- Package: org.apache.ofbiz.accounting.payment
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3052`
- Member entities: PY = Payment, TY = PaymentType

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PY | TY | paymentTypeId → — |

### PaymentAndTypeAndCreditCard

- Package: org.apache.ofbiz.accounting.payment
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3072`
- Member entities: PAT = PaymentAndType, CC = CreditCard

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PAT | CC | paymentMethodId → paymentMethodId |

### PaymentAndApplication

- Package: org.apache.ofbiz.accounting.payment
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3084`
- Member entities: PY = Payment, PA = PaymentApplication

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PY | PA | paymentId → — |

### PaymentContentAndInfo

- Package: org.apache.ofbiz.accounting.payment
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3226`
- Member entities: PAYC = PaymentContent, CO = Content, DR = DataResource

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PAYC | CO | contentId → — |
| CO | DR | dataResourceId → — |

### PaymentMethodAndCreditCard

- Package: org.apache.ofbiz.accounting.payment
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3283`
- Member entities: PM = PaymentMethod, CC = CreditCard

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PM | CC | paymentMethodId → — |

### PaymentMethodAndEftAccount

- Package: org.apache.ofbiz.accounting.payment
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3321`
- Member entities: PM = PaymentMethod, EA = EftAccount

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PM | EA | paymentMethodId → — |

### PaymentMethodAndGiftCard

- Package: org.apache.ofbiz.accounting.payment
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3358`
- Member entities: PM = PaymentMethod, GC = GiftCard

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PM | GC | paymentMethodId → — |

### PartyTaxAuthInfoAndDetail

- Package: org.apache.ofbiz.accounting.tax
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3644`
- Member entities: PTAI = PartyTaxAuthInfo, PG = PartyGroup, GEO = Geo

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PTAI | PG | taxAuthPartyId → partyId |
| PTAI | GEO | taxAuthGeoId → geoId |

### TaxAuthorityAndDetail

- Package: org.apache.ofbiz.accounting.tax
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3673`
- Member entities: TA = TaxAuthority, PG = PartyGroup, GEO = Geo

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| TA | PG | taxAuthPartyId → partyId |
| TA | GEO | taxAuthGeoId → geoId |

### TaxAuthorityCategoryView

- Package: org.apache.ofbiz.accounting.tax
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3735`
- Member entities: TAC = TaxAuthorityCategory, PC = ProductCategory

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| TAC | PC | productCategoryId → — |

### TaxAuthorityGlAccountBalance

- Package: org.apache.ofbiz.accounting.tax
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3763`
- Member entities: TAGA = TaxAuthorityGlAccount, GLAO = GlAccountOrganization, PAP = PartyAcctgPreference

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| TAGA | GLAO | glAccountId → —, organizationPartyId → — |
| TAGA | PAP | organizationPartyId → partyId |

### GlAccOrgAndAcctgTransAndEntry

- Package: org.apache.ofbiz.accounting.ledger
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3851`
- Member entities: GAO = GlAccountOrganization, ATE = AcctgTransEntry, ATR = AcctgTrans

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| GAO | ATE | glAccountId → —, organizationPartyId → — |
| ATE | ATR | acctgTransId → — |

### RateAmountAndRelations

- Package: org.apache.ofbiz.accounting.rate
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:3919`
- Member entities: RA = RateAmount, RT = RateType, PT = PeriodType, PN = PartyNameView, WE = WorkEffort, EPT = EmplPositionType

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| RA | RT | rateTypeId → — |
| RA | PT | periodTypeId → — |
| RA | PN | partyId → — |
| RA | WE | workEffortId → — |
| RA | EPT | emplPositionTypeId → — |

### PmtGrpMembrPaymentAndFinAcctTrans

- Package: org.apache.ofbiz.accounting.finaccount
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:4010`
- Member entities: PGM = PaymentGroupMember, PY = Payment, FAT = FinAccountTrans

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PGM | PY | paymentId → — |
| PY | FAT | finAccountTransId → — |

### PaymentMethodAndFinAccount

- Package: org.apache.ofbiz.accounting.finaccount
- Source: `applications/datamodel/entitydef/accounting-entitymodel.xml:4028`
- Member entities: PM = PaymentMethod, FA = FinAccount

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| FA | PM | finAccountId → — |
