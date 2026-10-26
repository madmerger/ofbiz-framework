# Entity inventory

## MarketingCampaign

- Package: org.apache.ofbiz.marketing.campaign
- Table: `MARKETING_CAMPAIGN`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:44`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| marketingCampaignId | id | — | Y |
| parentCampaignId | id | — | N |
| statusId | id | — | N |
| campaignName | name | — | N |
| campaignSummary | very-long | — | N |
| budgetedCost | currency-amount | — | N |
| actualCost | currency-amount | — | N |
| estimatedCost | currency-amount | — | N |
| currencyUomId | id | — | N |
| fromDate | date-time | — | N |
| thruDate | date-time | — | N |
| isActive | indicator | — | N |
| convertedLeads | id | — | N |
| expectedResponsePercent | floating-point | — | N |
| expectedRevenue | currency-amount | — | N |
| numSent | numeric | — | N |
| startDate | date-time | — | N |
| createdByUserLogin | id-vlong | — | N |
| lastModifiedByUserLogin | id-vlong | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | MarketingCampaign | Parent | parentCampaignId → marketingCampaignId | — |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |
| one | Uom (external) | — | currencyUomId → uomId | framework/common/entitydef/entitymodel.xml |

## MarketingCampaignNote

- Package: org.apache.ofbiz.marketing.campaign
- Table: `MARKETING_CAMPAIGN_NOTE`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:77`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| marketingCampaignId | id | — | Y |
| noteId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | MarketingCampaign | — | marketingCampaignId → — | — |
| one | NoteData (external) | — | noteId → — | framework/common/entitydef/entitymodel.xml |

## MarketingCampaignPrice

- Package: org.apache.ofbiz.marketing.campaign
- Table: `MARKETING_CAMPAIGN_PRICE`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:91`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| marketingCampaignId | id | — | Y |
| productPriceRuleId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | MarketingCampaign | — | marketingCampaignId → — | — |
| one | ProductPriceRule (external) | — | productPriceRuleId → — | applications/datamodel/entitydef/product-entitymodel.xml |

## MarketingCampaignPromo

- Package: org.apache.ofbiz.marketing.campaign
- Table: `MARKETING_CAMPAIGN_PROMO`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:108`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| marketingCampaignId | id | — | Y |
| productPromoId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | MarketingCampaign | — | marketingCampaignId → — | — |
| one | ProductPromo (external) | — | productPromoId → — | applications/datamodel/entitydef/product-entitymodel.xml |

## MarketingCampaignRole

- Package: org.apache.ofbiz.marketing.campaign
- Table: `MARKETING_CAMPAIGN_ROLE`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:125`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| marketingCampaignId | id | — | Y |
| partyId | id | — | Y |
| roleTypeId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | MarketingCampaign | — | marketingCampaignId → — | — |
| one | PartyRole (external) | — | partyId → —, roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | RoleType (external) | — | roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |

## ContactList

- Package: org.apache.ofbiz.marketing.contact
- Table: `CONTACT_LIST`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:156`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contactListId | id | — | Y |
| contactListTypeId | id | — | N |
| contactMechTypeId | id | — | N |
| marketingCampaignId | id | — | N |
| contactListName | name | — | N |
| description | description | — | N |
| comments | comment | — | N |
| isPublic | indicator | — | N |
| singleUse | indicator | — | N |
| ownerPartyId | id | — | N |
| verifyEmailFrom | long-varchar | — | N |
| verifyEmailScreen | long-varchar | — | N |
| verifyEmailSubject | long-varchar | — | N |
| verifyEmailWebSiteId | id | — | N |
| optOutScreen | long-varchar | — | N |
| createdByUserLogin | id-vlong | — | N |
| lastModifiedByUserLogin | id-vlong | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | MarketingCampaign | — | marketingCampaignId → — | — |
| one | ContactListType | — | contactListTypeId → — | — |
| one | ContactMechType (external) | — | contactMechTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | UserLogin (external) | CreatedBy | createdByUserLogin → userLoginId | framework/security/entitydef/entitymodel.xml |
| one | UserLogin (external) | LastModifiedBy | lastModifiedByUserLogin → userLoginId | framework/security/entitydef/entitymodel.xml |
| one | Party (external) | Owner | ownerPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |

## WebSiteContactList

- Package: org.apache.ofbiz.marketing.contact
- Table: `WEB_SITE_CONTACT_LIST`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:196`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| webSiteId | id | — | Y |
| contactListId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WebSite (external) | — | webSiteId → — | framework/webapp/entitydef/entitymodel.xml |
| one | ContactList | — | contactListId → — | — |

## ContactListCommStatus

- Package: org.apache.ofbiz.marketing.contact
- Table: `CONTACT_LIST_COMM_STATUS`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:213`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contactListId | id | — | Y |
| communicationEventId | id | — | Y |
| contactMechId | id | — | Y |
| partyId | id | — | N |
| messageId | value | — | N |
| statusId | id | — | N |
| changeByUserLoginId | id-vlong | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | ContactList | — | contactListId → — | — |
| one | CommunicationEvent (external) | — | communicationEventId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | ContactMech (external) | — | contactMechId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |
| one | UserLogin (external) | ChangeBy | changeByUserLoginId → userLoginId | framework/security/entitydef/entitymodel.xml |

## ContactListParty

- Package: org.apache.ofbiz.marketing.contact
- Table: `CONTACT_LIST_PARTY`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:248`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contactListId | id | — | Y |
| partyId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| statusId | id | — | N |
| preferredContactMechId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | ContactList | — | contactListId → — | — |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |
| one | ContactMech (external) | Preferred | preferredContactMechId → contactMechId | applications/datamodel/entitydef/party-entitymodel.xml |

## ContactListPartyStatus

- Package: org.apache.ofbiz.marketing.contact
- Table: `CONTACT_LIST_PARTY_STATUS`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:283`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contactListId | id | — | Y |
| partyId | id | — | Y |
| fromDate | date-time | — | Y |
| statusDate | date-time | — | Y |
| statusId | id | — | N |
| setByUserLoginId | id-vlong | — | N |
| optInVerifyCode | short-varchar | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | ContactListParty | — | contactListId → —, partyId → —, fromDate → — | — |

## ContactListType

- Package: org.apache.ofbiz.marketing.contact
- Table: `CONTACT_LIST_TYPE`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:303`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contactListTypeId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## SegmentGroup

- Package: org.apache.ofbiz.marketing.segment
- Table: `SEGMENT_GROUP`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:316`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| segmentGroupId | id | — | Y |
| segmentGroupTypeId | id | — | N |
| description | description | — | N |
| productStoreId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | SegmentGroupType | — | segmentGroupTypeId → — | — |
| one | ProductStore (external) | — | productStoreId → — | applications/datamodel/entitydef/product-entitymodel.xml |

## SegmentGroupClassification

- Package: org.apache.ofbiz.marketing.segment
- Table: `SEGMENT_GROUP_CLASSIFICATION`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:331`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| segmentGroupId | id | — | Y |
| partyClassificationGroupId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | SegmentGroup | — | segmentGroupId → — | — |
| one | PartyClassificationGroup (external) | — | partyClassificationGroupId → — | applications/datamodel/entitydef/party-entitymodel.xml |

## SegmentGroupGeo

- Package: org.apache.ofbiz.marketing.segment
- Table: `SEGMENT_GROUP_GEO`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:345`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| segmentGroupId | id | — | Y |
| geoId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | SegmentGroup | — | segmentGroupId → — | — |
| one | Geo (external) | — | geoId → — | framework/common/entitydef/entitymodel.xml |

## SegmentGroupRole

- Package: org.apache.ofbiz.marketing.segment
- Table: `SEGMENT_GROUP_ROLE`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:359`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| segmentGroupId | id | — | Y |
| partyId | id | — | Y |
| roleTypeId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | SegmentGroup | — | segmentGroupId → — | — |
| one | PartyRole (external) | — | partyId → —, roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | RoleType (external) | — | roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |

## SegmentGroupType

- Package: org.apache.ofbiz.marketing.segment
- Table: `SEGMENT_GROUP_TYPE`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:382`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| segmentGroupTypeId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## TrackingCode

- Package: org.apache.ofbiz.marketing.tracking
- Table: `TRACKING_CODE`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:446`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| trackingCodeId | id | — | Y |
| trackingCodeTypeId | id | — | N |
| marketingCampaignId | id | — | N |
| redirectUrl | url | — | N |
| overrideLogo | url | — | N |
| overrideCss | url | — | N |
| prodCatalogId | id | — | N |
| comments | comment | — | N |
| description | description | — | N |
| trackableLifetime | numeric | — | N |
| billableLifetime | numeric | — | N |
| fromDate | date-time | — | N |
| thruDate | date-time | — | N |
| groupId | id | — | N |
| subgroupId | id | — | N |
| createdDate | date-time | — | N |
| createdByUserLogin | id-vlong | — | N |
| lastModifiedDate | date-time | — | N |
| lastModifiedByUserLogin | id-vlong | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | MarketingCampaign | — | marketingCampaignId → — | — |
| one | TrackingCodeType | — | trackingCodeTypeId → — | — |

## TrackingCodeOrder

- Package: org.apache.ofbiz.marketing.tracking
- Table: `TRACKING_CODE_ORDER`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:476`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| orderId | id | — | Y |
| trackingCodeTypeId | id | — | Y |
| trackingCodeId | id | — | N |
| isBillable | indicator | — | N |
| siteId | long-varchar | — | N |
| hasExported | indicator | — | N |
| affiliateReferredTimeStamp | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | OrderHeader (external) | — | orderId → — | applications/datamodel/entitydef/order-entitymodel.xml |
| one | TrackingCode | — | trackingCodeId → — | — |
| one | TrackingCodeType | — | trackingCodeTypeId → — | — |

## TrackingCodeOrderReturn

- Package: org.apache.ofbiz.marketing.tracking
- Table: `TRACKING_CODE_ORDER_RETURN`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:498`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| returnId | id | — | Y |
| orderId | id | — | Y |
| orderItemSeqId | id | — | N |
| trackingCodeTypeId | id | — | Y |
| trackingCodeId | id | — | N |
| isBillable | indicator | — | N |
| siteId | long-varchar | — | N |
| hasExported | indicator | — | N |
| affiliateReferredTimeStamp | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | ReturnHeader (external) | — | returnId → — | applications/datamodel/entitydef/order-entitymodel.xml |
| one | OrderHeader (external) | — | orderId → — | applications/datamodel/entitydef/order-entitymodel.xml |
| one | TrackingCode | — | trackingCodeId → — | — |
| one | TrackingCodeType | — | trackingCodeTypeId → — | — |

## TrackingCodeType

- Package: org.apache.ofbiz.marketing.tracking
- Table: `TRACKING_CODE_TYPE`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:526`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| trackingCodeTypeId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## TrackingCodeVisit

- Package: org.apache.ofbiz.marketing.tracking
- Table: `TRACKING_CODE_VISIT`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:534`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| trackingCodeId | id | — | Y |
| visitId | id | — | Y |
| fromDate | date-time | — | Y |
| sourceEnumId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | TrackingCode | — | trackingCodeId → — | — |
| one | Enumeration (external) | — | sourceEnumId → enumId | framework/common/entitydef/entitymodel.xml |

## SalesOpportunity

- Package: org.apache.ofbiz.marketing.opportunity
- Table: `SALES_OPPORTUNITY`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:661`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| salesOpportunityId | id | — | Y |
| opportunityName | name | — | N |
| description | very-long | — | N |
| nextStep | very-long | — | N |
| nextStepDate | date-time | — | N |
| estimatedAmount | currency-amount | — | N |
| estimatedProbability | fixed-point | — | N |
| currencyUomId | id | — | N |
| marketingCampaignId | id | — | N |
| dataSourceId | id | — | N |
| estimatedCloseDate | date-time | — | N |
| opportunityStageId | id | — | N |
| typeEnumId | id | — | N |
| createdByUserLogin | id-vlong | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Uom (external) | — | currencyUomId → uomId | framework/common/entitydef/entitymodel.xml |
| one | SalesOpportunityStage | — | opportunityStageId → — | — |
| one | Enumeration (external) | Type | typeEnumId → enumId | framework/common/entitydef/entitymodel.xml |
| one | MarketingCampaign | — | marketingCampaignId → — | — |
| one | UserLogin (external) | — | createdByUserLogin → userLoginId | framework/security/entitydef/entitymodel.xml |
| one | DataSource (external) | — | dataSourceId → — | framework/common/entitydef/entitymodel.xml |

## SalesOpportunityHistory

- Package: org.apache.ofbiz.marketing.opportunity
- Table: `SALES_OPPORTUNITY_HISTORY`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:699`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| salesOpportunityHistoryId | id | — | Y |
| salesOpportunityId | id | — | N |
| description | very-long | — | N |
| nextStep | very-long | — | N |
| estimatedAmount | currency-amount | — | N |
| estimatedProbability | fixed-point | — | N |
| currencyUomId | id | — | N |
| estimatedCloseDate | date-time | — | N |
| opportunityStageId | id | — | N |
| changeNote | very-long | — | N |
| modifiedByUserLogin | id-vlong | — | N |
| modifiedTimestamp | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Uom (external) | — | currencyUomId → uomId | framework/common/entitydef/entitymodel.xml |
| one | SalesOpportunityStage | — | opportunityStageId → — | — |
| one | SalesOpportunity | — | salesOpportunityId → — | — |
| one | UserLogin (external) | — | modifiedByUserLogin → userLoginId | framework/security/entitydef/entitymodel.xml |

## SalesOpportunityRole

- Package: org.apache.ofbiz.marketing.opportunity
- Table: `SALES_OPPORTUNITY_ROLE`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:731`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| salesOpportunityId | id | — | Y |
| partyId | id | — | Y |
| roleTypeId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | SalesOpportunity | — | salesOpportunityId → — | — |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | RoleType (external) | — | roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PartyRole (external) | — | partyId → —, roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |

## SalesOpportunityStage

- Package: org.apache.ofbiz.marketing.opportunity
- Table: `SALES_OPPORTUNITY_STAGE`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:767`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| opportunityStageId | id | — | Y |
| description | description | — | N |
| defaultProbability | fixed-point | — | N |
| sequenceNum | numeric | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## SalesOpportunityWorkEffort

- Package: org.apache.ofbiz.marketing.opportunity
- Table: `SALES_OPPORTUNITY_WORK_EFFORT`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:777`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| salesOpportunityId | id | — | Y |
| workEffortId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | SalesOpportunity | — | salesOpportunityId → — | — |
| one | WorkEffort (external) | — | workEffortId → — | applications/datamodel/entitydef/workeffort-entitymodel.xml |

## SalesOpportunityQuote

- Package: org.apache.ofbiz.marketing.opportunity
- Table: `SALES_OPPORTUNITY_QUOTE`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:792`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| salesOpportunityId | id | — | Y |
| quoteId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | SalesOpportunity | — | salesOpportunityId → — | — |
| one | Quote (external) | — | quoteId → — | applications/datamodel/entitydef/order-entitymodel.xml |

## SalesForecast

- Package: org.apache.ofbiz.marketing.opportunity
- Table: `SALES_FORECAST`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:807`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| salesForecastId | id | — | Y |
| parentSalesForecastId | id | — | N |
| organizationPartyId | id | — | N |
| internalPartyId | id | — | N |
| customTimePeriodId | id | — | N |
| currencyUomId | id | — | N |
| quotaAmount | currency-amount | — | N |
| forecastAmount | currency-amount | — | N |
| bestCaseAmount | currency-amount | — | N |
| closedAmount | currency-amount | — | N |
| percentOfQuotaForecast | fixed-point | — | N |
| percentOfQuotaClosed | fixed-point | — | N |
| pipelineAmount | currency-amount | — | N |
| createdByUserLoginId | id-vlong | — | N |
| modifiedByUserLoginId | id-vlong | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | SalesForecast | Parent | parentSalesForecastId → salesForecastId | — |
| one | Party (external) | Organization | organizationPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | Party (external) | Internal | internalPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | CustomTimePeriod (external) | — | customTimePeriodId → — | framework/common/entitydef/entitymodel.xml |
| one | Uom (external) | — | currencyUomId → uomId | framework/common/entitydef/entitymodel.xml |
| one | UserLogin (external) | CreatedBy | createdByUserLoginId → userLoginId | framework/security/entitydef/entitymodel.xml |
| one | UserLogin (external) | ModifiedBy | modifiedByUserLoginId → userLoginId | framework/security/entitydef/entitymodel.xml |

## SalesForecastDetail

- Package: org.apache.ofbiz.marketing.opportunity
- Table: `SALES_FORECAST_DETAIL`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:849`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| salesForecastId | id | — | Y |
| salesForecastDetailId | id | — | Y |
| amount | currency-amount | — | N |
| quantityUomId | id | — | N |
| quantity | fixed-point | — | N |
| productId | id | — | N |
| productCategoryId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | SalesForecast | — | salesForecastId → — | — |
| one | Uom (external) | Quantity | quantityUomId → uomId | framework/common/entitydef/entitymodel.xml |
| one | Product (external) | — | productId → — | applications/datamodel/entitydef/product-entitymodel.xml |
| one | ProductCategory (external) | — | productCategoryId → — | applications/datamodel/entitydef/product-entitymodel.xml |

## SalesForecastHistory

- Package: org.apache.ofbiz.marketing.opportunity
- Table: `SALES_FORECAST_HISTORY`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:875`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| salesForecastHistoryId | id | — | Y |
| salesForecastId | id | — | N |
| parentSalesForecastId | id | — | N |
| organizationPartyId | id | — | N |
| internalPartyId | id | — | N |
| customTimePeriodId | id | — | N |
| currencyUomId | id | — | N |
| quotaAmount | currency-amount | — | N |
| forecastAmount | currency-amount | — | N |
| bestCaseAmount | currency-amount | — | N |
| closedAmount | currency-amount | — | N |
| percentOfQuotaForecast | fixed-point | — | N |
| percentOfQuotaClosed | fixed-point | — | N |
| changeNote | very-long | — | N |
| modifiedByUserLoginId | id-vlong | — | N |
| modifiedTimestamp | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | SalesForecast | — | salesForecastId → — | — |
| one | Party (external) | Organization | organizationPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | Party (external) | Internal | internalPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | CustomTimePeriod (external) | — | customTimePeriodId → — | framework/common/entitydef/entitymodel.xml |
| one | Uom (external) | — | currencyUomId → uomId | framework/common/entitydef/entitymodel.xml |
| one | UserLogin (external) | ModifiedBy | modifiedByUserLoginId → userLoginId | framework/security/entitydef/entitymodel.xml |

## SalesOpportunityCompetitor

- Package: org.apache.ofbiz.marketing.opportunity
- Table: `SALES_OPPORTUNITY_COMPETITOR`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:916`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| salesOpportunityId | id | — | Y |
| competitorPartyId | id | — | Y |
| positionEnumId | id | — | N |
| strengths | very-long | — | N |
| weaknesses | very-long | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | SalesOpportunity | — | salesOpportunityId → — | — |

## SalesOpportunityTrckCode

- Package: org.apache.ofbiz.marketing.opportunity
- Table: `SALES_OPPORTUNITY_TRCK_CODE`
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:930`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| salesOpportunityId | id | — | Y |
| trackingCodeId | id | — | Y |
| receivedDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | SalesOpportunity | — | salesOpportunityId → — | — |

## View entities

### ContactListPartyAndStatus

- Package: org.apache.ofbiz.marketing.contact
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:273`
- Member entities: CLPS = ContactListPartyStatus, SI = StatusItem

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| CLPS | SI | statusId → — |

### SegmentGroupViewRelatedParties

- Package: org.apache.ofbiz.marketing.segment
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:390`
- Member entities: SGR = SegmentGroupRole, SGRTO = SegmentGroupRole, PRSGR = PartyRelationship, SGC = SegmentGroupClassification, PC = PartyClassification, PRPC = PartyRelationship

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| SGR | SGRTO | segmentGroupId → — |
| SGRTO | PRSGR | partyId → partyIdFrom |
| SGR | SGC | segmentGroupId → — |
| SGC | PC | partyClassificationGroupId → — |
| PC | PRPC | partyId → partyIdFrom |

### TrackingCodeAndVisit

- Package: org.apache.ofbiz.marketing.reports
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:556`
- Member entities: TC = TrackingCode, TCV = TrackingCodeVisit

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| TC | TCV | trackingCodeId → — |

### TrackingCodeAndOrderHeader

- Package: org.apache.ofbiz.marketing.reports
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:572`
- Member entities: TCO = TrackingCodeOrder, OH = OrderHeader

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| TCO | OH | orderId → — |

### TrackingCodeOrderAndOrderHeader

- Package: org.apache.ofbiz.marketing.reports
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:585`
- Member entities: TCO = TrackingCodeOrder, OH = OrderHeader

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| TCO | OH | orderId → — |

### TrackingCodeOrderReturnAndReturnHeader

- Package: org.apache.ofbiz.marketing.reports
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:604`
- Member entities: TCO = TrackingCodeOrderReturn, RH = ReturnHeader

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| TCO | RH | returnId → — |

### MarketingCampaignAndVisit

- Package: org.apache.ofbiz.marketing.reports
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:625`
- Member entities: TC = TrackingCode, TCV = TrackingCodeVisit

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| TC | TCV | trackingCodeId → — |

### MarketingCampaignAndOrderHeader

- Package: org.apache.ofbiz.marketing.reports
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:639`
- Member entities: TC = TrackingCode, TCO = TrackingCodeOrder, OH = OrderHeader

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| TC | TCO | trackingCodeId → — |
| TCO | OH | orderId → — |

### SalesOpportunityAndRole

- Package: org.apache.ofbiz.marketing.opportunity
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:756`
- Member entities: SO = SalesOpportunity, SR = SalesOpportunityRole

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| SO | SR | salesOpportunityId → — |

### SalesForecastAndCustomTimePeriod

- Package: org.apache.ofbiz.marketing.opportunity
- Source: `applications/datamodel/entitydef/marketing-entitymodel.xml:942`
- Member entities: SF = SalesForecast, CTP = CustomTimePeriod

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| SF | CTP | customTimePeriodId → — |
