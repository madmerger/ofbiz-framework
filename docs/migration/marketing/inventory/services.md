# Service inventory

## addPriceRuleToMarketingCampaign

- Definition: `applications/marketing/servicedef/services.xml:47`
- Engine: entity-auto; location: —; invoke: create
- Default entity: MarketingCampaignPrice; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "createMarketingCampaignPrice"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## addPromoToMarketingCampaign

- Definition: `applications/marketing/servicedef/services.xml:80`
- Engine: entity-auto; location: —; invoke: create
- Default entity: MarketingCampaignPromo; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "createMarketingCampaignPromo"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## arePartiesRelatedThroughSegmentGroup

- Definition: `applications/marketing/servicedef/services.xml:429`
- Engine: simple; location: component://marketing/minilang/marketing/segment/SegmentServices.xml; invoke: arePartiesRelatedThroughSegmentGroup
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| areRelated | String | OUT | false |
| partyId | String | IN | false |
| roleTypeId | String | IN | true |
| toPartyId | String | IN | false |
| toRoleTypeId | String | IN | true |

## convertLeadToContact

- Definition: `applications/marketing/servicedef/services.xml:574`
- Engine: groovy; location: component://marketing/src/main/groovy/org/apache/ofbiz/marketing/sfa/lead/LeadServices.groovy; invoke: convertLeadToContact
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyGroupId | String | INOUT | false |
| partyId | String | INOUT | false |

## createAccount

- Definition: `applications/marketing/servicedef/services.xml:558`
- Engine: groovy; location: component://marketing/src/main/groovy/org/apache/ofbiz/marketing/sfa/account/AccountServices.groovy; invoke: createAccount
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emailAddress | String | IN | true |
| extension | String | IN | true |
| partyId | String | OUT | false |

## createContact

- Definition: `applications/marketing/servicedef/services.xml:521`
- Engine: simple; location: component://marketing/minilang/sfa/contact/ContactServices.xml; invoke: createContact
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactListId | String | IN | true |
| contactMechId | String | OUT | true |
| emailAddress | String | IN | true |
| extension | String | IN | true |
| partyId | String | OUT | — |
| partyIdFrom | String | IN | true |
| quickAdd | String | IN | true |
| roleTypeId | String | OUT | true |

## createContactList

- Definition: `applications/marketing/servicedef/services.xml:164`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ContactList; auth: true
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| contactListId | id | INOUT | true |
| contactListName | name | IN | true |
| contactListTypeId | id | IN | true |
| contactMechTypeId | id | IN | true |
| createdByUserLogin | id-vlong | IN | true |
| description | description | IN | true |
| isPublic | indicator | IN | true |
| lastModifiedByUserLogin | id-vlong | IN | true |
| marketingCampaignId | id | IN | true |
| optOutScreen | long-varchar | IN | true |
| ownerPartyId | id | IN | true |
| singleUse | indicator | IN | true |
| verifyEmailFrom | long-varchar | IN | true |
| verifyEmailScreen | long-varchar | IN | true |
| verifyEmailSubject | long-varchar | IN | true |
| verifyEmailWebSiteId | id | IN | true |

## createContactListParty

- Definition: `applications/marketing/servicedef/services.xml:182`
- Engine: simple; location: component://marketing/minilang/marketing/contact/ContactListServices.xml; invoke: createContactListParty
- Default entity: ContactListParty; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| baseLocation | String | IN | true |
| contactListId | id | IN | false |
| fromDate | date-time | IN | true |
| partyId | id | IN | false |
| preferredContactMechId | id | IN | true |
| statusId | id | IN | false |
| thruDate | date-time | IN | true |

## createContactListPartyStatus

- Definition: `applications/marketing/servicedef/services.xml:226`
- Engine: simple; location: component://marketing/minilang/marketing/contact/ContactListServices.xml; invoke: createContactListPartyStatus
- Default entity: ContactListPartyStatus; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| baseLocation | String | IN | true |
| contactListId | id | IN | false |
| fromDate | date-time | IN | true |
| optInVerifyCode | short-varchar | IN | true |
| partyId | id | IN | false |
| preferredContactMechId | String | IN | false |
| setByUserLoginId | id-vlong | IN | true |
| statusDate | date-time | IN | false |
| statusId | id | IN | false |

## createContactListType

- Definition: `applications/marketing/servicedef/services_contact.xml:26`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ContactListType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactListTypeId | id | INOUT | true |
| description | description | IN | true |

## createLead

- Definition: `applications/marketing/servicedef/services.xml:489`
- Engine: groovy; location: component://marketing/src/main/groovy/org/apache/ofbiz/marketing/sfa/lead/LeadServices.groovy; invoke: createLead
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactListId | String | IN | true |
| contactMechId | String | OUT | true |
| dataSourceId | String | IN | true |
| emailAddress | String | IN | true |
| extension | String | IN | true |
| groupName | String | IN | true |
| numEmployees | String | IN | true |
| officeSiteName | String | IN | true |
| partyGroupPartyId | String | OUT | true |
| partyId | String | OUT | true |
| partyIdFrom | String | IN | true |
| roleTypeId | String | OUT | true |
| title | String | IN | true |

## createMarketingCampaign

- Definition: `applications/marketing/servicedef/services.xml:28`
- Engine: entity-auto; location: —; invoke: create
- Default entity: MarketingCampaign; auth: true
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| actualCost | currency-amount | IN | true |
| budgetedCost | currency-amount | IN | true |
| campaignName | name | IN | true |
| campaignSummary | very-long | IN | true |
| convertedLeads | id | IN | true |
| createdByUserLogin | id-vlong | IN | true |
| currencyUomId | id | IN | true |
| estimatedCost | currency-amount | IN | true |
| expectedResponsePercent | floating-point | IN | true |
| expectedRevenue | currency-amount | IN | true |
| fromDate | date-time | IN | true |
| isActive | indicator | IN | true |
| lastModifiedByUserLogin | id-vlong | IN | true |
| marketingCampaignId | id | OUT | false |
| numSent | numeric | IN | true |
| parentCampaignId | id | IN | true |
| startDate | date-time | IN | true |
| statusId | id | IN | true |
| thruDate | date-time | IN | true |

## createMarketingCampaignNote

- Definition: `applications/marketing/servicedef/services.xml:608`
- Engine: entity-auto; location: —; invoke: create
- Default entity: MarketingCampaignNote; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| marketingCampaignId | id | INOUT | false |
| noteId | id | INOUT | false |

## createMarketingCampaignPrice

- Definition: `applications/marketing/servicedef/services.xml:53`
- Engine: entity-auto; location: —; invoke: create
- Default entity: MarketingCampaignPrice; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| marketingCampaignId | id | IN | false |
| productPriceRuleId | id | IN | false |
| thruDate | date-time | IN | true |

## createMarketingCampaignPromo

- Definition: `applications/marketing/servicedef/services.xml:86`
- Engine: entity-auto; location: —; invoke: create
- Default entity: MarketingCampaignPromo; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| marketingCampaignId | id | IN | false |
| productPromoId | id | IN | false |
| thruDate | date-time | IN | true |

## createMarketingCampaignRole

- Definition: `applications/marketing/servicedef/services.xml:140`
- Engine: entity-auto; location: —; invoke: create
- Default entity: MarketingCampaignRole; auth: true
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | — |
| marketingCampaignId | id | IN | — |
| partyId | id | IN | — |
| roleTypeId | id | IN | — |
| thruDate | date-time | IN | true |

## createPartyContactFromVCard

- Definition: `applications/marketing/servicedef/services.xml:598`
- Engine: groovy; location: component://marketing/src/main/groovy/org/apache/ofbiz/marketing/sfa/lead/LeadServices.groovy; invoke: createPartyContactFromVCard
- Default entity: —; auth: true
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: [{"service": "importVCard"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## createPartyFromVCard

- Definition: `applications/marketing/servicedef/services.xml:587`
- Engine: java; location: org.apache.ofbiz.sfa.vcard.VCard; invoke: importVCard
- Default entity: —; auth: true
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: [{"service": "importVCard"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| serviceName | String | IN | — |

## createPartyLeadFromVCard

- Definition: `applications/marketing/servicedef/services.xml:593`
- Engine: groovy; location: component://marketing/src/main/groovy/org/apache/ofbiz/marketing/sfa/lead/LeadServices.groovy; invoke: createPartyLeadFromVCard
- Default entity: —; auth: true
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: [{"service": "importVCard"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## createSalesForecast

- Definition: `applications/marketing/servicedef/services_opportunity.xml:26`
- Engine: simple; location: component://order/minilang/opportunity/OpportunityServices.xml; invoke: createSalesForecast
- Default entity: SalesForecast; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| bestCaseAmount | currency-amount | IN | true |
| closedAmount | currency-amount | IN | true |
| createdByUserLoginId | id-vlong | IN | true |
| currencyUomId | id | IN | true |
| customTimePeriodId | id | IN | true |
| forecastAmount | currency-amount | IN | true |
| internalPartyId | id | IN | true |
| modifiedByUserLoginId | id-vlong | IN | true |
| organizationPartyId | id | IN | true |
| parentSalesForecastId | id | IN | true |
| percentOfQuotaClosed | fixed-point | IN | true |
| percentOfQuotaForecast | fixed-point | IN | true |
| pipelineAmount | currency-amount | IN | true |
| quotaAmount | currency-amount | IN | true |
| salesForecastId | id | OUT | false |

## createSalesForecastDetail

- Definition: `applications/marketing/servicedef/services_opportunity.xml:52`
- Engine: entity-auto; location: —; invoke: create
- Default entity: SalesForecastDetail; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | currency-amount | IN | true |
| productCategoryId | id | IN | true |
| productId | id | IN | true |
| quantity | fixed-point | IN | true |
| quantityUomId | id | IN | true |
| salesForecastDetailId | id | OUT | false |
| salesForecastId | id | IN | false |

## createSalesOpportunity

- Definition: `applications/marketing/servicedef/services_opportunity.xml:69`
- Engine: entity-auto; location: —; invoke: create
- Default entity: SalesOpportunity; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accountPartyId | String | IN | true |
| createdByUserLogin | id-vlong | IN | true |
| currencyUomId | id | IN | true |
| dataSourceId | id | IN | true |
| description | very-long | IN | true |
| estimatedAmount | currency-amount | IN | true |
| estimatedCloseDate | date-time | IN | true |
| estimatedProbability | fixed-point | IN | true |
| leadPartyId | String | IN | true |
| marketingCampaignId | id | IN | true |
| nextStep | very-long | IN | true |
| nextStepDate | date-time | IN | true |
| opportunityName | name | IN | true |
| opportunityStageId | id | IN | true |
| salesOpportunityId | id | INOUT | true |
| typeEnumId | id | IN | true |

## createSalesOpportunityAccountRole

- Definition: `applications/marketing/servicedef/services_opportunity.xml:93`
- Engine: simple; location: component://order/minilang/opportunity/OpportunityServices.xml; invoke: createSalesOpportunityAccountRole
- Default entity: SalesOpportunityRole; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accountPartyId | String | IN | — |
| salesOpportunityId | String | IN | — |

## createSalesOpportunityCompetitor

- Definition: `applications/marketing/servicedef/services_opportunity.xml:114`
- Engine: entity-auto; location: —; invoke: create
- Default entity: SalesOpportunityCompetitor; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| competitorPartyId | id | INOUT | false |
| positionEnumId | id | IN | true |
| salesOpportunityId | id | INOUT | false |
| strengths | very-long | IN | true |
| weaknesses | very-long | IN | true |

## createSalesOpportunityLeadRole

- Definition: `applications/marketing/servicedef/services_opportunity.xml:99`
- Engine: simple; location: component://order/minilang/opportunity/OpportunityServices.xml; invoke: createSalesOpportunityLeadRole
- Default entity: SalesOpportunityRole; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| leadPartyId | String | IN | — |
| salesOpportunityId | String | IN | — |

## createSalesOpportunityQuote

- Definition: `applications/marketing/servicedef/services_opportunity.xml:178`
- Engine: entity-auto; location: —; invoke: create
- Default entity: SalesOpportunityQuote; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| quoteId | id | INOUT | false |
| salesOpportunityId | id | INOUT | false |

## createSalesOpportunityRole

- Definition: `applications/marketing/servicedef/services_opportunity.xml:89`
- Engine: entity-auto; location: —; invoke: create
- Default entity: SalesOpportunityRole; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |
| salesOpportunityId | id | IN | false |

## createSalesOpportunityStage

- Definition: `applications/marketing/servicedef/services_opportunity.xml:135`
- Engine: entity-auto; location: —; invoke: create
- Default entity: SalesOpportunityStage; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| defaultProbability | fixed-point | IN | true |
| description | description | IN | true |
| opportunityStageId | id | INOUT | true |
| sequenceNum | numeric | IN | true |

## createSalesOpportunityTrckCode

- Definition: `applications/marketing/servicedef/services_opportunity.xml:151`
- Engine: entity-auto; location: —; invoke: create
- Default entity: SalesOpportunityTrckCode; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| receivedDate | date-time | IN | true |
| salesOpportunityId | id | INOUT | false |
| trackingCodeId | id | INOUT | false |

## createSalesOpportunityWorkEffort

- Definition: `applications/marketing/servicedef/services_opportunity.xml:167`
- Engine: entity-auto; location: —; invoke: create
- Default entity: SalesOpportunityWorkEffort; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| salesOpportunityId | id | INOUT | false |
| workEffortId | id | INOUT | false |

## createSegmentGroup

- Definition: `applications/marketing/servicedef/services.xml:353`
- Engine: entity-auto; location: —; invoke: create
- Default entity: SegmentGroup; auth: true
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| productStoreId | id | IN | true |
| segmentGroupId | id | OUT | false |
| segmentGroupTypeId | id | IN | true |

## createSegmentGroupClassification

- Definition: `applications/marketing/servicedef/services.xml:372`
- Engine: entity-auto; location: —; invoke: create
- Default entity: SegmentGroupClassification; auth: true
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyClassificationGroupId | id | IN | false |
| segmentGroupId | id | IN | false |

## createSegmentGroupGeo

- Definition: `applications/marketing/servicedef/services.xml:391`
- Engine: entity-auto; location: —; invoke: create
- Default entity: SegmentGroupGeo; auth: true
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| geoId | id | IN | false |
| segmentGroupId | id | IN | false |

## createSegmentGroupRole

- Definition: `applications/marketing/servicedef/services.xml:410`
- Engine: entity-auto; location: —; invoke: create
- Default entity: SegmentGroupRole; auth: true
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |
| segmentGroupId | id | IN | false |

## createSegmentGroupType

- Definition: `applications/marketing/servicedef/services.xml:619`
- Engine: entity-auto; location: —; invoke: create
- Default entity: SegmentGroupType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| segmentGroupTypeId | id | INOUT | true |

## createTrackingCode

- Definition: `applications/marketing/servicedef/services.xml:317`
- Engine: entity-auto; location: —; invoke: create
- Default entity: TrackingCode; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| billableLifetime | numeric | IN | true |
| comments | comment | IN | true |
| createdByUserLogin | id-vlong | IN | true |
| createdDate | date-time | IN | true |
| description | description | IN | true |
| fromDate | date-time | IN | true |
| groupId | id | IN | true |
| lastModifiedByUserLogin | id-vlong | IN | true |
| lastModifiedDate | date-time | IN | true |
| marketingCampaignId | id | IN | true |
| overrideCss | url | IN | true |
| overrideLogo | url | IN | true |
| prodCatalogId | id | IN | true |
| redirectUrl | url | IN | true |
| subgroupId | id | IN | true |
| thruDate | date-time | IN | true |
| trackableLifetime | numeric | IN | true |
| trackingCodeId | id | IN | false |
| trackingCodeTypeId | id | IN | false |

## createTrackingCodeOrder

- Definition: `applications/marketing/servicedef/services.xml:441`
- Engine: entity-auto; location: —; invoke: create
- Default entity: TrackingCodeOrder; auth: —
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| affiliateReferredTimeStamp | date-time | IN | true |
| hasExported | indicator | IN | true |
| isBillable | indicator | IN | true |
| orderId | id | IN | false |
| siteId | long-varchar | IN | true |
| trackingCodeId | id | IN | true |
| trackingCodeTypeId | id | IN | false |

## createTrackingCodeOrderReturn

- Definition: `applications/marketing/servicedef/services.xml:455`
- Engine: entity-auto; location: —; invoke: create
- Default entity: TrackingCodeOrderReturn; auth: —
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| affiliateReferredTimeStamp | date-time | IN | true |
| hasExported | indicator | IN | true |
| isBillable | indicator | IN | true |
| orderId | id | IN | false |
| orderItemSeqId | id | IN | true |
| returnId | id | IN | false |
| siteId | long-varchar | IN | true |
| trackingCodeId | id | IN | true |
| trackingCodeTypeId | id | IN | false |

## createTrackingCodeOrderReturns

- Definition: `applications/marketing/servicedef/services.xml:473`
- Engine: simple; location: component://marketing/minilang/marketing/tracking/TrackingCodeServices.xml; invoke: createTrackingCodeOrderReturns
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| returnId | String | IN | false |

## createTrackingCodeType

- Definition: `applications/marketing/servicedef/services.xml:334`
- Engine: entity-auto; location: —; invoke: create
- Default entity: TrackingCodeType; auth: true
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| trackingCodeTypeId | id | IN | false |

## createWebSiteContactList

- Definition: `applications/marketing/servicedef/services.xml:298`
- Engine: simple; location: component://marketing/minilang/marketing/contact/ContactListServices.xml; invoke: createWebSiteContactList
- Default entity: WebSiteContactList; auth: —
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactListId | id | IN | false |
| fromDate | date-time | IN | false |
| thruDate | date-time | IN | true |
| webSiteId | id | IN | false |

## deleteContactListParty

- Definition: `applications/marketing/servicedef/services.xml:221`
- Engine: java; location: org.apache.ofbiz.marketing.marketing.MarketingServices; invoke: deleteContactListParty
- Default entity: ContactListParty; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactListId | id | IN | true |
| fromDate | date-time | IN | true |
| partyId | id | IN | true |

## deleteContactListType

- Definition: `applications/marketing/servicedef/services_contact.xml:36`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ContactListType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactListTypeId | id | IN | — |
| description | description | IN | true |

## deleteMarketingCampaign

- Definition: `applications/marketing/servicedef/services.xml:40`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: MarketingCampaign; auth: true
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| marketingCampaignId | id | IN | false |

## deleteMarketingCampaignNote

- Definition: `applications/marketing/servicedef/services.xml:613`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: MarketingCampaignNote; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| marketingCampaignId | id | IN | false |
| noteId | id | IN | false |

## deleteMarketingCampaignPrice

- Definition: `applications/marketing/servicedef/services.xml:69`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: MarketingCampaignPrice; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| marketingCampaignId | id | IN | false |
| productPriceRuleId | id | IN | false |

## deleteMarketingCampaignPromo

- Definition: `applications/marketing/servicedef/services.xml:102`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: MarketingCampaignPromo; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| marketingCampaignId | id | IN | false |
| productPromoId | id | IN | false |

## deleteMarketingCampaignRole

- Definition: `applications/marketing/servicedef/services.xml:157`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: MarketingCampaignRole; auth: true
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| marketingCampaignId | id | IN | false |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |

## deleteSalesForecastDetail

- Definition: `applications/marketing/servicedef/services_opportunity.xml:63`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: SalesForecastDetail; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| salesForecastDetailId | id | IN | false |
| salesForecastId | id | IN | false |

## deleteSalesOpportunityCompetitor

- Definition: `applications/marketing/servicedef/services_opportunity.xml:124`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: SalesOpportunityCompetitor; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| competitorPartyId | id | IN | false |
| salesOpportunityId | id | IN | false |

## deleteSalesOpportunityQuote

- Definition: `applications/marketing/servicedef/services_opportunity.xml:183`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: SalesOpportunityQuote; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| quoteId | id | IN | false |
| salesOpportunityId | id | IN | false |

## deleteSalesOpportunityRole

- Definition: `applications/marketing/servicedef/services_opportunity.xml:129`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: SalesOpportunityRole; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |
| salesOpportunityId | id | IN | false |

## deleteSalesOpportunityStage

- Definition: `applications/marketing/servicedef/services_opportunity.xml:145`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: SalesOpportunityStage; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| opportunityStageId | id | IN | false |

## deleteSalesOpportunityTrckCode

- Definition: `applications/marketing/servicedef/services_opportunity.xml:161`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: SalesOpportunityTrckCode; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| salesOpportunityId | id | IN | false |
| trackingCodeId | id | IN | false |

## deleteSalesOpportunityWorkEffort

- Definition: `applications/marketing/servicedef/services_opportunity.xml:172`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: SalesOpportunityWorkEffort; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| salesOpportunityId | id | IN | false |
| workEffortId | id | IN | false |

## deleteSegmentGroup

- Definition: `applications/marketing/servicedef/services.xml:365`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: SegmentGroup; auth: true
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| segmentGroupId | id | IN | false |

## deleteSegmentGroupClassification

- Definition: `applications/marketing/servicedef/services.xml:384`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: SegmentGroupClassification; auth: true
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyClassificationGroupId | id | IN | false |
| segmentGroupId | id | IN | false |

## deleteSegmentGroupGeo

- Definition: `applications/marketing/servicedef/services.xml:403`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: SegmentGroupGeo; auth: true
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| geoId | id | IN | false |
| segmentGroupId | id | IN | false |

## deleteSegmentGroupRole

- Definition: `applications/marketing/servicedef/services.xml:422`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: SegmentGroupRole; auth: true
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |
| segmentGroupId | id | IN | false |

## deleteSegmentGroupType

- Definition: `applications/marketing/servicedef/services.xml:629`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: SegmentGroupType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| segmentGroupTypeId | id | IN | false |

## deleteTrackingCode

- Definition: `applications/marketing/servicedef/services.xml:328`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: TrackingCode; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| trackingCodeId | id | IN | false |

## deleteTrackingCodeOrderReturn

- Definition: `applications/marketing/servicedef/services.xml:467`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: TrackingCodeOrderReturn; auth: —
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| orderId | id | IN | false |
| returnId | id | IN | false |
| trackingCodeTypeId | id | IN | false |

## deleteTrackingCodeType

- Definition: `applications/marketing/servicedef/services.xml:346`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: TrackingCodeType; auth: true
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| trackingCodeTypeId | id | IN | false |

## deleteWebSiteContactList

- Definition: `applications/marketing/servicedef/services.xml:310`
- Engine: simple; location: component://marketing/minilang/marketing/contact/ContactListServices.xml; invoke: deleteWebSiteContactList
- Default entity: WebSiteContactList; auth: —
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactListId | id | IN | false |
| fromDate | date-time | IN | false |
| webSiteId | id | IN | false |

## expireMarketingCampaignPrice

- Definition: `applications/marketing/servicedef/services.xml:73`
- Engine: entity-auto; location: —; invoke: expire
- Default entity: MarketingCampaignPrice; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| marketingCampaignId | id | IN | false |
| productPriceRuleId | id | IN | false |
| thruDate | date-time | IN | true |

## expireMarketingCampaignPromo

- Definition: `applications/marketing/servicedef/services.xml:106`
- Engine: entity-auto; location: —; invoke: expire
- Default entity: MarketingCampaignPromo; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| marketingCampaignId | id | IN | false |
| productPromoId | id | IN | false |
| thruDate | date-time | IN | true |

## expireMarketingCampaignRole

- Definition: `applications/marketing/servicedef/services.xml:152`
- Engine: entity-auto; location: —; invoke: expire
- Default entity: MarketingCampaignRole; auth: true
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| marketingCampaignId | id | IN | false |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |

## exportVCard

- Definition: `applications/marketing/servicedef/services.xml:603`
- Engine: java; location: org.apache.ofbiz.sfa.vcard.VCard; invoke: exportVCard
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | String | IN | false |

## findPartyInSalesOpportunityRole

- Definition: `applications/marketing/servicedef/services_opportunity.xml:105`
- Engine: simple; location: component://order/minilang/opportunity/OpportunityServices.xml; invoke: findPartyInSalesOpportunityRole
- Default entity: SalesOpportunityRole; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | String | OUT | true |
| roleTypeId | String | IN | — |
| salesOpportunityId | String | IN | true |

## importVCard

- Definition: `applications/marketing/servicedef/services.xml:580`
- Engine: interface; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| infile | java.nio.ByteBuffer | IN | — |
| partiesCreated | List | OUT | — |
| partiesExist | List | OUT | true |
| partyType | String | IN | true |
| serviceContext | Map | IN | true |

## marketingPermissionService

- Definition: `applications/marketing/servicedef/services.xml:481`
- Engine: simple; location: component://common/minilang/permission/CommonPermissionServices.xml; invoke: genericBasePermissionCheck
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| altPermission | String | IN | true |
| primaryPermission | String | IN | true |

## mergeContacts

- Definition: `applications/marketing/servicedef/services.xml:542`
- Engine: simple; location: component://marketing/minilang/sfa/contact/ContactServices.xml; invoke: mergeContacts
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| addrContactMechId | String | IN | true |
| addrContactMechIdTo | String | IN | true |
| emailContactMechId | String | IN | true |
| emailContactMechIdTo | String | IN | true |
| partyId | String | INOUT | false |
| partyIdTo | String | IN | false |
| phoneContactMechId | String | IN | true |
| phoneContactMechIdTo | String | IN | true |
| useAddress2 | String | IN | true |
| useContactNum2 | String | IN | true |
| useEmail2 | String | IN | true |

## optOutOfListFromCommEvent

- Definition: `applications/marketing/servicedef/services.xml:246`
- Engine: simple; location: component://marketing/minilang/marketing/contact/ContactListServices.xml; invoke: optOutOfListFromCommEvent
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | String | IN | false |
| contactListId | String | OUT | true |

## removeContactList

- Definition: `applications/marketing/servicedef/services.xml:176`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ContactList; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactListId | id | IN | false |

## removePriceRuleFromMarketingCampaign

- Definition: `applications/marketing/servicedef/services.xml:58`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: MarketingCampaignPrice; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "deleteMarketingCampaignPrice"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## removePromoFromMarketingCampaign

- Definition: `applications/marketing/servicedef/services.xml:91`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: MarketingCampaignPromo; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "deleteMarketingCampaignPromo"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## sendContactListPartySubscribeEmail

- Definition: `applications/marketing/servicedef/services.xml:252`
- Engine: simple; location: component://marketing/minilang/marketing/contact/ContactListServices.xml; invoke: sendContactListPartySubscribeEmail
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| baseLocation | String | IN | true |
| contactListId | String | IN | true |
| fromDate | Timestamp | IN | true |
| partyId | String | IN | true |
| preferredContactMechId | String | IN | false |
| productStoreId | String | IN | true |
| statusId | String | IN | true |

## sendContactListPartyUnSubscribeEmail

- Definition: `applications/marketing/servicedef/services.xml:272`
- Engine: simple; location: component://marketing/minilang/marketing/contact/ContactListServices.xml; invoke: sendContactListPartyUnSubscribeEmail
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactListId | String | IN | true |
| fromDate | Timestamp | IN | true |
| partyId | String | IN | true |
| preferredContactMechId | String | IN | false |
| productStoreId | String | IN | true |
| statusId | String | IN | true |

## sendContactListPartyUnSubscribeVerifyEmail

- Definition: `applications/marketing/servicedef/services.xml:262`
- Engine: simple; location: component://marketing/minilang/marketing/contact/ContactListServices.xml; invoke: sendContactListPartyUnSubscribeVerifyEmail
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| baseLocation | String | IN | true |
| contactListId | String | IN | true |
| fromDate | Timestamp | IN | true |
| partyId | String | IN | true |
| preferredContactMechId | String | IN | false |
| productStoreId | String | IN | true |
| statusId | String | IN | true |

## sendContactListPartyVerifyEmail

- Definition: `applications/marketing/servicedef/services.xml:240`
- Engine: simple; location: component://marketing/minilang/marketing/contact/ContactListServices.xml; invoke: sendContactListPartyVerifyEmail
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| baseLocation | String | IN | true |

## signUpForContactList

- Definition: `applications/marketing/servicedef/services.xml:112`
- Engine: java; location: org.apache.ofbiz.marketing.marketing.MarketingServices; invoke: signUpForContactList
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| baseLocation | String | IN | true |
| contactListId | String | IN | false |
| email | String | IN | false |
| partyId | String | IN | true |

## unsubscribeContactListParty

- Definition: `applications/marketing/servicedef/services.xml:121`
- Engine: simple; location: component://marketing/minilang/marketing/contact/ContactListServices.xml; invoke: unsubscribeContactListParty
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| baseLocation | String | IN | true |
| contactListId | String | IN | false |
| email | String | IN | false |
| partyId | String | IN | true |

## unsubscribeContactListPartyContachMech

- Definition: `applications/marketing/servicedef/services.xml:130`
- Engine: simple; location: component://marketing/minilang/marketing/contact/ContactListServices.xml; invoke: unsubscribeContactListPartyContachMech
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| baseLocation | String | IN | true |
| contactListId | String | IN | false |
| partyId | String | IN | true |
| preferredContactMechId | String | IN | false |

## updateCommStatusFromCommEvent

- Definition: `applications/marketing/servicedef/services.xml:291`
- Engine: simple; location: component://marketing/minilang/marketing/contact/ContactListServices.xml; invoke: updateCommStatusFromCommEvent
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | String | IN | — |
| statusId | String | IN | — |

## updateContactList

- Definition: `applications/marketing/servicedef/services.xml:170`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ContactList; auth: true
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| contactListId | id | IN | false |
| contactListName | name | IN | true |
| contactListTypeId | id | IN | true |
| contactMechTypeId | id | IN | true |
| createdByUserLogin | id-vlong | IN | true |
| description | description | IN | true |
| isPublic | indicator | IN | true |
| lastModifiedByUserLogin | id-vlong | IN | true |
| marketingCampaignId | id | IN | true |
| optOutScreen | long-varchar | IN | true |
| ownerPartyId | id | IN | true |
| singleUse | indicator | IN | true |
| verifyEmailFrom | long-varchar | IN | true |
| verifyEmailScreen | long-varchar | IN | true |
| verifyEmailSubject | long-varchar | IN | true |
| verifyEmailWebSiteId | id | IN | true |

## updateContactListCommStatus

- Definition: `applications/marketing/servicedef/services.xml:283`
- Engine: simple; location: component://marketing/minilang/marketing/contact/ContactListServices.xml; invoke: updateContactListCommStatus
- Default entity: ContactListCommStatus; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| changeByUserLoginId | id-vlong | IN | false |
| communicationEventId | id | IN | false |
| contactListId | id | IN | false |
| contactMechId | id | IN | false |
| messageId | value | IN | true |
| partyId | id | IN | true |
| statusId | id | IN | false |

## updateContactListParty

- Definition: `applications/marketing/servicedef/services.xml:191`
- Engine: simple; location: component://marketing/minilang/marketing/contact/ContactListServices.xml; invoke: updateContactListParty
- Default entity: ContactListParty; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| baseLocation | String | IN | true |
| contactListId | String | OUT | true |
| fromDate | date-time | IN | true |
| optInVerifyCode | String | IN | true |
| partyId | id | IN | false |
| preferredContactMechId | id | IN | true |
| productStoreId | String | INOUT | true |
| statusId | id | IN | true |
| thruDate | date-time | IN | true |

## updateContactListPartyNoUserLogin

- Definition: `applications/marketing/servicedef/services.xml:202`
- Engine: simple; location: component://marketing/minilang/marketing/contact/ContactListServices.xml; invoke: updateContactListPartyNoUserLogin
- Default entity: ContactListParty; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| baseLocation | String | IN | true |
| contactListId | id | IN | false |
| email | String | IN | true |
| fromDate | date-time | IN | true |
| optInVerifyCode | String | IN | true |
| partyId | id | IN | true |
| preferredContactMechId | id | IN | true |
| productStoreId | String | INOUT | true |
| statusId | id | IN | true |
| thruDate | date-time | IN | true |
| webSiteId | String | IN | true |

## updateContactListType

- Definition: `applications/marketing/servicedef/services_contact.xml:31`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ContactListType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactListTypeId | id | IN | — |
| description | description | IN | true |

## updateMarketingCampaign

- Definition: `applications/marketing/servicedef/services.xml:34`
- Engine: entity-auto; location: —; invoke: update
- Default entity: MarketingCampaign; auth: true
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| actualCost | currency-amount | IN | true |
| budgetedCost | currency-amount | IN | true |
| campaignName | name | IN | true |
| campaignSummary | very-long | IN | true |
| convertedLeads | id | IN | true |
| createdByUserLogin | id-vlong | IN | true |
| currencyUomId | id | IN | true |
| estimatedCost | currency-amount | IN | true |
| expectedResponsePercent | floating-point | IN | true |
| expectedRevenue | currency-amount | IN | true |
| fromDate | date-time | IN | true |
| isActive | indicator | IN | true |
| lastModifiedByUserLogin | id-vlong | IN | true |
| marketingCampaignId | id | IN | false |
| numSent | numeric | IN | true |
| parentCampaignId | id | IN | true |
| startDate | date-time | IN | true |
| statusId | id | IN | true |
| thruDate | date-time | IN | true |

## updateMarketingCampaignPrice

- Definition: `applications/marketing/servicedef/services.xml:64`
- Engine: entity-auto; location: —; invoke: update
- Default entity: MarketingCampaignPrice; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| marketingCampaignId | id | IN | false |
| productPriceRuleId | id | IN | false |
| thruDate | date-time | IN | true |

## updateMarketingCampaignPromo

- Definition: `applications/marketing/servicedef/services.xml:97`
- Engine: entity-auto; location: —; invoke: update
- Default entity: MarketingCampaignPromo; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| marketingCampaignId | id | IN | false |
| productPromoId | id | IN | false |
| thruDate | date-time | IN | true |

## updateMarketingCampaignRole

- Definition: `applications/marketing/servicedef/services.xml:146`
- Engine: entity-auto; location: —; invoke: update
- Default entity: MarketingCampaignRole; auth: true
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| marketingCampaignId | id | IN | false |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |
| thruDate | date-time | IN | true |

## updatePartyEmailContactListParty

- Definition: `applications/marketing/servicedef/services.xml:215`
- Engine: simple; location: component://marketing/minilang/marketing/contact/ContactListServices.xml; invoke: updatePartyEmailContactListParty
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | IN | false |
| oldContactMechId | String | IN | false |

## updateSalesForecast

- Definition: `applications/marketing/servicedef/services_opportunity.xml:38`
- Engine: simple; location: component://order/minilang/opportunity/OpportunityServices.xml; invoke: updateSalesForecast
- Default entity: SalesForecast; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| bestCaseAmount | currency-amount | IN | true |
| changeNote | String | IN | true |
| closedAmount | currency-amount | IN | true |
| createdByUserLoginId | id-vlong | IN | true |
| currencyUomId | id | IN | true |
| customTimePeriodId | id | IN | true |
| forecastAmount | currency-amount | IN | true |
| internalPartyId | id | IN | true |
| modifiedByUserLoginId | id-vlong | IN | true |
| organizationPartyId | id | IN | true |
| parentSalesForecastId | id | IN | true |
| percentOfQuotaClosed | fixed-point | IN | true |
| percentOfQuotaForecast | fixed-point | IN | true |
| pipelineAmount | currency-amount | IN | true |
| quotaAmount | currency-amount | IN | true |
| salesForecastId | id | IN | false |

## updateSalesForecastDetail

- Definition: `applications/marketing/servicedef/services_opportunity.xml:58`
- Engine: entity-auto; location: —; invoke: update
- Default entity: SalesForecastDetail; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | currency-amount | IN | true |
| productCategoryId | id | IN | true |
| productId | id | IN | true |
| quantity | fixed-point | IN | true |
| quantityUomId | id | IN | true |
| salesForecastDetailId | id | IN | false |
| salesForecastId | id | IN | false |

## updateSalesOpportunity

- Definition: `applications/marketing/servicedef/services_opportunity.xml:79`
- Engine: entity-auto; location: —; invoke: update
- Default entity: SalesOpportunity; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accountPartyId | String | IN | true |
| createdByUserLogin | id-vlong | IN | true |
| currencyUomId | id | IN | true |
| dataSourceId | id | IN | true |
| description | very-long | IN | true |
| estimatedAmount | currency-amount | IN | true |
| estimatedCloseDate | date-time | IN | true |
| estimatedProbability | fixed-point | IN | true |
| leadPartyId | String | IN | true |
| marketingCampaignId | id | IN | true |
| nextStep | very-long | IN | true |
| nextStepDate | date-time | IN | true |
| opportunityName | name | IN | true |
| opportunityStageId | id | IN | true |
| salesOpportunityId | id | IN | false |
| typeEnumId | id | IN | true |

## updateSalesOpportunityCompetitor

- Definition: `applications/marketing/servicedef/services_opportunity.xml:119`
- Engine: entity-auto; location: —; invoke: update
- Default entity: SalesOpportunityCompetitor; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| competitorPartyId | id | IN | false |
| positionEnumId | id | IN | true |
| salesOpportunityId | id | IN | false |
| strengths | very-long | IN | true |
| weaknesses | very-long | IN | true |

## updateSalesOpportunityStage

- Definition: `applications/marketing/servicedef/services_opportunity.xml:140`
- Engine: entity-auto; location: —; invoke: update
- Default entity: SalesOpportunityStage; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| defaultProbability | fixed-point | IN | true |
| description | description | IN | true |
| opportunityStageId | id | IN | false |
| sequenceNum | numeric | IN | true |

## updateSalesOpportunityTrckCode

- Definition: `applications/marketing/servicedef/services_opportunity.xml:156`
- Engine: entity-auto; location: —; invoke: update
- Default entity: SalesOpportunityTrckCode; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| receivedDate | date-time | IN | true |
| salesOpportunityId | id | IN | false |
| trackingCodeId | id | IN | false |

## updateSegmentGroup

- Definition: `applications/marketing/servicedef/services.xml:359`
- Engine: entity-auto; location: —; invoke: update
- Default entity: SegmentGroup; auth: true
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| productStoreId | id | IN | true |
| segmentGroupId | id | IN | false |
| segmentGroupTypeId | id | IN | true |

## updateSegmentGroupClassification

- Definition: `applications/marketing/servicedef/services.xml:378`
- Engine: entity-auto; location: —; invoke: update
- Default entity: SegmentGroupClassification; auth: true
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyClassificationGroupId | id | IN | false |
| segmentGroupId | id | IN | false |

## updateSegmentGroupGeo

- Definition: `applications/marketing/servicedef/services.xml:397`
- Engine: entity-auto; location: —; invoke: update
- Default entity: SegmentGroupGeo; auth: true
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| geoId | id | IN | false |
| segmentGroupId | id | IN | false |

## updateSegmentGroupRole

- Definition: `applications/marketing/servicedef/services.xml:416`
- Engine: entity-auto; location: —; invoke: update
- Default entity: SegmentGroupRole; auth: true
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |
| segmentGroupId | id | IN | false |

## updateSegmentGroupType

- Definition: `applications/marketing/servicedef/services.xml:624`
- Engine: entity-auto; location: —; invoke: update
- Default entity: SegmentGroupType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| segmentGroupTypeId | id | IN | false |

## updateTrackingCode

- Definition: `applications/marketing/servicedef/services.xml:323`
- Engine: entity-auto; location: —; invoke: update
- Default entity: TrackingCode; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| billableLifetime | numeric | IN | true |
| comments | comment | IN | true |
| createdByUserLogin | id-vlong | IN | true |
| createdDate | date-time | IN | true |
| description | description | IN | true |
| fromDate | date-time | IN | true |
| groupId | id | IN | true |
| lastModifiedByUserLogin | id-vlong | IN | true |
| lastModifiedDate | date-time | IN | true |
| marketingCampaignId | id | IN | true |
| overrideCss | url | IN | true |
| overrideLogo | url | IN | true |
| prodCatalogId | id | IN | true |
| redirectUrl | url | IN | true |
| subgroupId | id | IN | true |
| thruDate | date-time | IN | true |
| trackableLifetime | numeric | IN | true |
| trackingCodeId | id | IN | false |
| trackingCodeTypeId | id | IN | true |

## updateTrackingCodeOrder

- Definition: `applications/marketing/servicedef/services.xml:447`
- Engine: entity-auto; location: —; invoke: update
- Default entity: TrackingCodeOrder; auth: —
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| affiliateReferredTimeStamp | date-time | IN | true |
| hasExported | indicator | IN | true |
| isBillable | indicator | IN | true |
| orderId | id | IN | false |
| siteId | long-varchar | IN | true |
| trackingCodeId | id | IN | true |
| trackingCodeTypeId | id | IN | false |

## updateTrackingCodeOrderReturn

- Definition: `applications/marketing/servicedef/services.xml:461`
- Engine: entity-auto; location: —; invoke: update
- Default entity: TrackingCodeOrderReturn; auth: —
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| affiliateReferredTimeStamp | date-time | IN | true |
| hasExported | indicator | IN | true |
| isBillable | indicator | IN | true |
| orderId | id | IN | false |
| orderItemSeqId | id | IN | true |
| returnId | id | IN | false |
| siteId | long-varchar | IN | true |
| trackingCodeId | id | IN | true |
| trackingCodeTypeId | id | IN | false |

## updateTrackingCodeType

- Definition: `applications/marketing/servicedef/services.xml:340`
- Engine: entity-auto; location: —; invoke: update
- Default entity: TrackingCodeType; auth: true
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| trackingCodeTypeId | id | IN | false |

## updateWebSiteContactList

- Definition: `applications/marketing/servicedef/services.xml:304`
- Engine: simple; location: component://marketing/minilang/marketing/contact/ContactListServices.xml; invoke: updateWebSiteContactList
- Default entity: WebSiteContactList; auth: —
- Use transaction: —; timeout: —
- Permission service: marketingPermissionService; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactListId | id | IN | false |
| fromDate | date-time | IN | false |
| thruDate | date-time | IN | true |
| webSiteId | id | IN | false |
