# Service inventory

## accAndDecPartyInvitationPermissionCheck

- Definition: `applications/party/servicedef/services.xml:1340`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyPermissionServices.groovy; invoke: accAndDecPartyInvitationPermissionCheck
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyInvitationId | String | IN | false |

## acceptPartyInvitation

- Definition: `applications/party/servicedef/services.xml:1230`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyInvitationServices.groovy; invoke: acceptPartyInvitation
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: accAndDecPartyInvitationPermissionCheck; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | String | IN | false |
| partyInvitationId | String | IN | false |

## basePermissionCheck

- Definition: `applications/party/servicedef/services.xml:1270`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyPermissionServices.groovy; invoke: basePermissionCheck
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## cancelPartyInvitation

- Definition: `applications/party/servicedef/services.xml:1254`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyInvitationServices.groovy; invoke: updatePartyInvitation
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: cancelPartyInvitationPermissionCheck; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyInvitationId | String | IN | — |
| statusId | String | IN | true |

## cancelPartyInvitationPermissionCheck

- Definition: `applications/party/servicedef/services.xml:1354`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyPermissionServices.groovy; invoke: cancelPartyInvitationPermissionCheck
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyInvitationId | String | IN | false |

## clearAddressMatchMap

- Definition: `applications/party/servicedef/services.xml:48`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyServicesScript.groovy; invoke: clearAddressMatchMap
- Default entity: AddressMatchMap; auth: true
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## copyPartyContactMechs

- Definition: `applications/party/servicedef/services.xml:621`
- Engine: java; location: org.apache.ofbiz.party.contact.ContactMechServices; invoke: copyPartyContactMechs
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyIdFrom | String | IN | false |
| partyIdTo | String | IN | false |

## createAddendum

- Definition: `applications/party/servicedef/services_agreement.xml:87`
- Engine: entity-auto; location: —; invoke: create
- Default entity: Addendum; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| addendumCreationDate | date-time | IN | true |
| addendumEffectiveDate | date-time | IN | true |
| addendumId | id | INOUT | true |
| addendumText | long-varchar | IN | true |
| agreementId | id | IN | true |
| agreementItemSeqId | id | IN | true |

## createAddressMatchMap

- Definition: `applications/party/servicedef/services.xml:28`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyServicesScript.groovy; invoke: createAddressMatchMap
- Default entity: AddressMatchMap; auth: true
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| mapKey | id-vlong | IN | false |
| mapValue | id-vlong | IN | false |
| sequenceNum | numeric | IN | true |

## createAffiliate

- Definition: `applications/party/servicedef/services.xml:216`
- Engine: java; location: org.apache.ofbiz.party.party.PartyServices; invoke: createAffiliate
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| affiliateDescription | String | IN | true |
| affiliateName | String | IN | — |
| partyId | String | INOUT | — |
| sitePageViews | String | IN | true |
| siteType | String | IN | true |
| siteVisitors | String | IN | true |
| yearEstablished | String | IN | true |

## createAgreementAttribute

- Definition: `applications/party/servicedef/services.xml:1535`
- Engine: entity-auto; location: —; invoke: create
- Default entity: AgreementAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| attrDescription | description | IN | true |
| attrName | id-long | IN | false |
| attrValue | value | IN | true |

## createAgreementContentType

- Definition: `applications/party/servicedef/services_agreement.xml:103`
- Engine: entity-auto; location: —; invoke: create
- Default entity: AgreementContentType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementContentTypeId | id | INOUT | true |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## createAgreementItemType

- Definition: `applications/party/servicedef/services_agreement.xml:39`
- Engine: entity-auto; location: —; invoke: create
- Default entity: AgreementItemType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementItemTypeId | id | INOUT | true |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## createAgreementItemTypeAttr

- Definition: `applications/party/servicedef/services.xml:1556`
- Engine: entity-auto; location: —; invoke: create
- Default entity: AgreementItemTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementItemTypeId | id | IN | false |
| attrName | id-long | IN | false |
| description | description | IN | true |

## createAgreementTermAttribute

- Definition: `applications/party/servicedef/services_agreement.xml:26`
- Engine: entity-auto; location: —; invoke: create
- Default entity: AgreementTermAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementTermId | id | INOUT | false |
| attrDescription | description | IN | true |
| attrName | id-long | INOUT | false |
| attrValue | value | IN | true |

## createCommEventFromEmail

- Definition: `applications/party/servicedef/services.xml:990`
- Engine: java; location: org.apache.ofbiz.party.communication.CommunicationEventServices; invoke: createCommEventFromEmail
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | String | OUT | — |
| contentType | String | IN | true |
| orderId | String | IN | true |
| partyId | String | IN | true |
| returnId | String | IN | true |
| sendFrom | String | IN | false |
| sendTo | String | IN | false |
| statusId | String | IN | true |
| subject | String | IN | false |

## createCommEventFromFtpTransfer

- Definition: `applications/party/servicedef/services.xml:977`
- Engine: java; location: org.apache.ofbiz.party.communication.CommunicationEventServices; invoke: createCommEventFromFtpTransfer
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | String | OUT | — |
| contactMechId | String | IN | — |
| contentId | String | IN | — |
| partyId | String | IN | true |

## createCommEventWorkEffort

- Definition: `applications/party/servicedef/services.xml:932`
- Engine: simple; location: component://workeffort/minilang/workeffort/WorkEffortSimpleServices.xml; invoke: makeCommunicationEventWorkEffort
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| currentStatusId | — | — | false |
| workEffortId | — | — | true |
| workEffortName | — | — | false |
| workEffortTypeId | — | — | false |

## createCommunicationEvent

- Definition: `applications/party/servicedef/services.xml:829`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/communication/CommunicationEventServicesScript.groovy; invoke: createCommunicationEvent
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: partyCommunicationEventPermissionCheck; main action: CREATE
- Implements: [{"service": "createCommunicationEventInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## createCommunicationEventInterface

- Definition: `applications/party/servicedef/services.xml:815`
- Engine: interface; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| action | String | IN | true |
| content | — | — | — |
| custRequestId | String | IN | true |
| headerString | — | — | — |
| messageId | — | — | — |
| orderId | String | IN | true |
| productId | String | IN | true |
| returnId | String | IN | true |
| subject | — | — | — |

## createCommunicationEventPrpTyp

- Definition: `applications/party/servicedef/services.xml:1612`
- Engine: entity-auto; location: —; invoke: create
- Default entity: CommunicationEventPrpTyp; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventPrpTypId | id | INOUT | true |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## createCommunicationEventPurpose

- Definition: `applications/party/servicedef/services.xml:872`
- Engine: entity-auto; location: —; invoke: create
- Default entity: CommunicationEventPurpose; auth: true
- Use transaction: —; timeout: —
- Permission service: partyCommunicationEventPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | id | IN | false |
| communicationEventPrpTypId | id | IN | false |
| description | description | IN | true |

## createCommunicationEventRole

- Definition: `applications/party/servicedef/services.xml:904`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/communication/CommunicationEventServicesScript.groovy; invoke: createCommunicationEventRole
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: partyCommunicationEventPermissionCheck; main action: CREATE
- Implements: [{"service": "createCommunicationEventRoleInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## createCommunicationEventRoleInterface

- Definition: `applications/party/servicedef/services.xml:898`
- Engine: interface; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## createCommunicationEventRoleWithoutPermission

- Definition: `applications/party/servicedef/services.xml:911`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/communication/CommunicationEventServicesScript.groovy; invoke: createCommunicationEventRole
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "createCommunicationEventRoleInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## createCommunicationEventType

- Definition: `applications/party/servicedef/services_communication.xml:28`
- Engine: entity-auto; location: —; invoke: create
- Default entity: CommunicationEventType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventTypeId | id | INOUT | true |
| contactMechTypeId | id | IN | true |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## createCommunicationEventWithoutPermission

- Definition: `applications/party/servicedef/services.xml:835`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/communication/CommunicationEventServicesScript.groovy; invoke: createCommunicationEventWithoutPermission
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "createCommunicationEventInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## createContactMech

- Definition: `applications/party/servicedef/services_contact.xml:28`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ContactMech; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | id | INOUT | true |
| contactMechTypeId | id | IN | false |
| infoString | long-varchar | IN | true |

## createContactMechAttribute

- Definition: `applications/party/servicedef/services.xml:646`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ContactMechAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | false |
| attrValue | value | IN | true |
| contactMechId | id | IN | false |

## createContactMechLink

- Definition: `applications/party/servicedef/services.xml:685`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ContactMechLink; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechIdFrom | id | IN | false |
| contactMechIdTo | id | IN | false |

## createContactMechPurposeType

- Definition: `applications/party/servicedef/services_contact.xml:126`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ContactMechPurposeType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechPurposeTypeId | id | INOUT | true |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## createContactMechType

- Definition: `applications/party/servicedef/services_contact.xml:110`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ContactMechType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechTypeId | id | INOUT | true |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## createContactMechTypeAttr

- Definition: `applications/party/servicedef/services_contact.xml:141`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ContactMechTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| contactMechTypeId | id | IN | — |
| description | description | IN | true |

## createContactMechTypePurpose

- Definition: `applications/party/servicedef/services_contact.xml:156`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ContactMechTypePurpose; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechPurposeTypeId | id | IN | — |
| contactMechTypeId | id | IN | — |

## createCustRequestCommEvent

- Definition: `applications/party/servicedef/services.xml:888`
- Engine: entity-auto; location: —; invoke: create
- Default entity: CustRequestCommEvent; auth: true
- Use transaction: —; timeout: —
- Permission service: partyCommunicationEventPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | id | IN | false |
| custRequestId | id | IN | false |

## createEmailAddress

- Definition: `applications/party/servicedef/services_contact.xml:79`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/contact/ContactMechServicesScript.groovy; invoke: createEmailAddress
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emailAddress | String | IN | false |
| infoString | — | — | true |

## createEmailAddressVerification

- Definition: `applications/party/servicedef/services.xml:1168`
- Engine: java; location: org.apache.ofbiz.party.contact.ContactMechServices; invoke: createEmailAddressVerification
- Default entity: EmailAddressVerification; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emailAddress | String | IN | false |
| expireDate | String | IN | true |
| verifyHash | String | OUT | false |

## createFtpAddress

- Definition: `applications/party/servicedef/services_contact.xml:93`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/contact/ContactMechServicesScript.groovy; invoke: createFtpAddress
- Default entity: FtpAddress; auth: —
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| binaryTransfer | indicator | IN | true |
| contactMechId | id | OUT | — |
| defaultTimeout | numeric | IN | true |
| filePath | long-varchar | IN | true |
| ftpPassword | long-varchar | IN | true |
| hostname | long-varchar | IN | true |
| passiveMode | indicator | IN | true |
| port | numeric | IN | true |
| username | long-varchar | IN | true |
| zipFile | indicator | IN | true |

## createMaritalStatus

- Definition: `applications/party/servicedef/services.xml:1628`
- Engine: entity-auto; location: —; invoke: create
- Default entity: MaritalStatus; auth: true
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | true |
| maritalStatusTypeId | id | IN | — |
| partyId | id | IN | — |
| thruDate | date-time | IN | true |

## createMaritalStatusType

- Definition: `applications/party/servicedef/services.xml:1662`
- Engine: entity-auto; location: —; invoke: create
- Default entity: MaritalStatusType; auth: —
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| maritalStatusTypeId | id | IN | — |
| parentTypeId | id | IN | true |

## createNeedType

- Definition: `applications/party/servicedef/services.xml:1596`
- Engine: entity-auto; location: —; invoke: create
- Default entity: NeedType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| needTypeId | id | INOUT | true |

## createPartyAttribute

- Definition: `applications/party/servicedef/services.xml:265`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PartyAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | false |
| attrValue | value | IN | true |
| partyId | id | IN | false |

## createPartyCarrierAccount

- Definition: `applications/party/servicedef/services.xml:1128`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PartyCarrierAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accountNumber | String | IN | false |
| carrierPartyId | id | IN | false |
| fromDate | Timestamp | IN | true |
| partyId | id | IN | false |
| thruDate | date-time | IN | true |

## createPartyClassification

- Definition: `applications/party/servicedef/services.xml:705`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PartyClassification; auth: —
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | true |
| partyClassificationGroupId | id | IN | false |
| partyId | id | IN | false |
| thruDate | date-time | IN | true |

## createPartyClassificationGroup

- Definition: `applications/party/servicedef/services.xml:723`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PartyClassificationGroup; auth: —
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| parentGroupId | id | IN | true |
| partyClassificationGroupId | id | OUT | false |
| partyClassificationTypeId | id | IN | true |

## createPartyClassificationType

- Definition: `applications/party/servicedef/services_party.xml:28`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PartyClassificationType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| partyClassificationTypeId | id | INOUT | true |

## createPartyContactMech

- Definition: `applications/party/servicedef/services.xml:457`
- Engine: simple; location: component://party/minilang/contact/PartyContactMechServices.xml; invoke: createPartyContactMech
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: partyContactMechPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | INOUT | true |
| contactMechPurposeTypeId | String | IN | true |
| contactMechTypeId | String | IN | false |

## createPartyContactMechPurpose

- Definition: `applications/party/servicedef/services.xml:665`
- Engine: java; location: org.apache.ofbiz.party.contact.ContactMechServices; invoke: createPartyContactMechPurpose
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | IN | false |
| contactMechPurposeTypeId | String | IN | false |
| fromDate | Timestamp | INOUT | true |
| partyId | String | IN | true |

## createPartyContactMechs

- Definition: `applications/party/servicedef/services.xml:119`
- Engine: simple; location: component://party/minilang/party/PartySimpleMethods.xml; invoke: createPartyContactMechs
- Default entity: createPartyContactMechs; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactNumber | String | IN | true |
| emailAddressContext | Map | IN | true |
| emailContactMechPurpTypeId | String | IN | true |
| partyGroupContext | Map | IN | true |
| phoneContactMechPurpTypeId | String | IN | true |
| postalAddContactMechPurpTypeId | String | IN | true |
| postalAddressContext | Map | IN | true |
| telecomNumberContext | Map | IN | true |

## createPartyContent

- Definition: `applications/party/servicedef/services.xml:1054`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PartyContent; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| fromDate | date-time | IN | true |
| partyContentTypeId | id | IN | false |
| partyId | id | IN | false |
| thruDate | date-time | IN | true |

## createPartyContentType

- Definition: `applications/party/servicedef/services_party.xml:44`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PartyContentType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| parentTypeId | id | IN | true |
| partyContentTypeId | id | INOUT | true |

## createPartyDataSource

- Definition: `applications/party/servicedef/services.xml:787`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PartyDataSource; auth: true
- Use transaction: —; timeout: —
- Permission service: partyDatasourcePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| dataSourceId | id | IN | false |
| fromDate | Timestamp | IN | true |
| isCreate | indicator | IN | true |
| partyId | id | IN | false |
| visitId | id | IN | true |

## createPartyEmailAddress

- Definition: `applications/party/servicedef/services.xml:590`
- Engine: simple; location: component://party/minilang/contact/PartyContactMechServices.xml; invoke: createPartyEmailAddress
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: partyContactMechPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | INOUT | true |
| contactMechPurposeTypeId | String | IN | true |
| emailAddress | String | IN | false |

## createPartyFtpAddress

- Definition: `applications/party/servicedef/services.xml:627`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/contact/ContactMechServicesScript.groovy; invoke: createPartyFtpAddress
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: partyContactMechPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | INOUT | true |
| contactMechPurposeTypeId | String | IN | true |

## createPartyGeoPoint

- Definition: `applications/party/servicedef/services_party.xml:60`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PartyGeoPoint; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | — |
| geoPointId | id | IN | — |
| partyId | id | IN | — |
| thruDate | date-time | IN | true |

## createPartyGroup

- Definition: `applications/party/servicedef/services.xml:158`
- Engine: java; location: org.apache.ofbiz.party.party.PartyServices; invoke: createPartyGroup
- Default entity: PartyGroup; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| annualRevenue | currency-amount | IN | true |
| comments | comment | IN | true |
| description | String | IN | true |
| externalId | String | IN | true |
| groupName | name | IN | false |
| groupNameLocal | name | IN | true |
| logoImageUrl | url | IN | true |
| numEmployees | numeric | IN | true |
| officeSiteName | name | IN | true |
| partyId | id | INOUT | true |
| partyTypeId | String | IN | true |
| preferredCurrencyUomId | String | IN | true |
| statusId | String | IN | true |
| tickerSymbol | very-short | IN | true |

## createPartyGroupRoleAndContactMechs

- Definition: `applications/party/servicedef/services.xml:95`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartySimpleMethods.groovy; invoke: createPartyGroupRoleAndContactMechs
- Default entity: PartyGroupRoleAndContactMechs; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| address1 | String | IN | true |
| contactNumber | String | IN | true |
| emailAddress | String | IN | true |
| emailContactMechPurpTypeId | String | IN | true |
| extension | String | IN | true |
| groupName | String | IN | false |
| partyId | String | OUT | false |
| phoneContactMechPurpTypeId | String | IN | true |
| postalAddContactMechPurpTypeId | String | IN | true |
| roleTypeId | String | IN | true |

## createPartyIcsAvsOverride

- Definition: `applications/party/servicedef/services_party.xml:76`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PartyIcsAvsOverride; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| avsDeclineString | long-varchar | IN | true |
| partyId | id | IN | — |

## createPartyIdentification

- Definition: `applications/party/servicedef/services.xml:742`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PartyIdentification; auth: —
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| idValue | id-long | IN | true |
| partyId | id | IN | — |
| partyIdentificationTypeId | id | IN | — |

## createPartyIdentificationType

- Definition: `applications/party/servicedef/services_party.xml:92`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PartyIdentificationType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| partyIdentificationTypeId | id | INOUT | true |

## createPartyIdentifications

- Definition: `applications/party/servicedef/services.xml:759`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyServicesScript.groovy; invoke: createPartyIdentifications
- Default entity: PartyIdentification; auth: —
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| identifications | Map | IN | — |
| partyId | String | IN | — |

## createPartyInvitation

- Definition: `applications/party/servicedef/services.xml:1192`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyInvitationServices.groovy; invoke: createPartyInvitation
- Default entity: PartyInvitation; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emailAddress | long-varchar | IN | true |
| lastInviteDate | date-time | IN | true |
| partyId | id | IN | true |
| partyIdFrom | id | IN | true |
| partyInvitationId | id | OUT | false |
| statusId | id | IN | true |
| toName | name | IN | true |

## createPartyInvitationGroupAssoc

- Definition: `applications/party/servicedef/services.xml:1210`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PartyInvitationGroupAssoc; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyIdTo | id | IN | — |
| partyInvitationId | id | IN | — |

## createPartyInvitationRoleAssoc

- Definition: `applications/party/servicedef/services.xml:1220`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PartyInvitationRoleAssoc; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyInvitationId | id | IN | — |
| roleTypeId | id | IN | — |

## createPartyNote

- Definition: `applications/party/servicedef/services.xml:241`
- Engine: java; location: org.apache.ofbiz.party.party.PartyServices; invoke: createPartyNote
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| note | String | IN | true |
| noteId | String | INOUT | true |
| noteName | String | IN | true |
| partyId | String | IN | false |

## createPartyPostalAddress

- Definition: `applications/party/servicedef/services.xml:540`
- Engine: simple; location: component://party/minilang/contact/PartyContactMechServices.xml; invoke: createPartyPostalAddress
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: partyContactMechPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| address1 | — | — | false |
| city | — | — | false |
| contactMechId | String | OUT | false |
| contactMechPurposeTypeId | String | IN | true |
| latitude | String | IN | true |
| longitude | String | IN | true |
| paymentMethodId | String | IN | true |
| postalCode | — | — | false |

## createPartyRelationship

- Definition: `applications/party/servicedef/services.xml:375`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyServicesScript.groovy; invoke: createPartyRelationship
- Default entity: PartyRelationship; auth: true
- Use transaction: —; timeout: —
- Permission service: partyRelationshipPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| fromDate | date-time | IN | true |
| partyIdFrom | id | IN | true |
| partyIdTo | id | IN | false |
| partyRelationshipTypeId | id | IN | true |
| permissionsEnumId | id | IN | true |
| positionTitle | name | IN | true |
| priorityTypeId | id | IN | true |
| relationshipName | name | IN | true |
| roleTypeIdFrom | id | IN | true |
| roleTypeIdTo | id | IN | true |
| securityGroupId | id | IN | true |
| statusId | id | IN | true |
| thruDate | date-time | IN | true |

## createPartyRelationshipAndRole

- Definition: `applications/party/servicedef/services.xml:417`
- Engine: group; location: —; invoke: —
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## createPartyRelationshipContactAccount

- Definition: `applications/party/servicedef/services.xml:448`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyServicesScript.groovy; invoke: createPartyRelationshipContactAccount
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accountPartyId | String | IN | — |
| comments | String | IN | true |
| contactPartyId | String | IN | — |

## createPartyRelationshipType

- Definition: `applications/party/servicedef/services.xml:426`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PartyRelationshipType; auth: true
- Use transaction: —; timeout: —
- Permission service: partyIdPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| partyRelationshipName | name | IN | false |
| partyRelationshipTypeId | id | IN | false |
| roleTypeIdValidFrom | id | IN | true |
| roleTypeIdValidTo | id | IN | true |

## createPartyRole

- Definition: `applications/party/servicedef/services.xml:318`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PartyRole; auth: true
- Use transaction: —; timeout: —
- Permission service: partyRolePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |

## createPartyTelecomNumber

- Definition: `applications/party/servicedef/services.xml:571`
- Engine: simple; location: component://party/minilang/contact/PartyContactMechServices.xml; invoke: createPartyTelecomNumber
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: partyContactMechPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | OUT | false |
| contactMechPurposeTypeId | String | IN | true |
| contactNumber | — | — | false |

## createPartyTextContent

- Definition: `applications/party/servicedef/services.xml:1070`
- Engine: group; location: —; invoke: —
- Default entity: —; auth: true
- Use transaction: —; timeout: 300
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## createPartyType

- Definition: `applications/party/servicedef/services_party.xml:108`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PartyType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| partyTypeId | id | INOUT | true |

## createPartyTypeAttr

- Definition: `applications/party/servicedef/services_party.xml:124`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PartyTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| description | description | IN | true |
| partyTypeId | id | IN | — |

## createPerson

- Definition: `applications/party/servicedef/services.xml:66`
- Engine: java; location: org.apache.ofbiz.party.party.PartyServices; invoke: createPerson
- Default entity: Person; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| birthDate | date | IN | true |
| cardId | id-long | IN | true |
| comments | comment | IN | true |
| deceasedDate | date | IN | true |
| description | String | IN | true |
| employmentStatusEnumId | id | IN | true |
| existingCustomer | indicator | IN | true |
| externalId | String | IN | true |
| firstName | name | IN | true |
| firstNameLocal | name | IN | true |
| gender | indicator | IN | true |
| height | floating-point | IN | true |
| lastName | name | IN | true |
| lastNameLocal | name | IN | true |
| maritalStatusTypeId | id | IN | true |
| memberId | id | IN | true |
| middleName | name | IN | true |
| middleNameLocal | name | IN | true |
| monthsWithEmployer | numeric | IN | true |
| mothersMaidenName | long-varchar | IN | true |
| nickname | name | IN | true |
| occupation | name | IN | true |
| oldMaritalStatusEnumId | id | IN | true |
| otherLocal | name | IN | true |
| partyId | id | OUT | false |
| passportExpireDate | date | IN | true |
| passportNumber | long-varchar | IN | true |
| personalTitle | name | IN | true |
| preferredCurrencyUomId | String | IN | true |
| residenceStatusEnumId | id | IN | true |
| salutation | name | IN | true |
| socialSecurityNumber | long-varchar | IN | true |
| statusId | String | IN | true |
| suffix | name | IN | true |
| totalYearsWorkExperience | floating-point | IN | true |
| weight | floating-point | IN | true |
| yearsWithEmployer | numeric | IN | true |

## createPersonAndUserLogin

- Definition: `applications/party/servicedef/services.xml:81`
- Engine: simple; location: component://party/minilang/party/PartySimpleMethods.xml; invoke: createPersonAndUserLogin
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "createUserLogin"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| newUserLogin | Map | OUT | false |
| partyId | String | INOUT | true |

## createPersonRoleAndContactMechs

- Definition: `applications/party/servicedef/services.xml:131`
- Engine: simple; location: component://party/minilang/party/PartySimpleMethods.xml; invoke: createPersonRoleAndContactMechs
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emailAddress | String | IN | true |
| partyId | String | OUT | false |
| roleTypeId | String | IN | — |

## createPostalAddress

- Definition: `applications/party/servicedef/services_contact.xml:41`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/contact/ContactMechServicesScript.groovy; invoke: createPostalAddress
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| address1 | — | — | false |
| city | — | — | false |
| contactMechId | String | OUT | false |
| postalCode | — | — | false |

## createPostalAddressAndPurposes

- Definition: `applications/party/servicedef/services.xml:1375`
- Engine: simple; location: component://party/minilang/contact/PartyContactMechServices.xml; invoke: createPostalAddressAndPurposes
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "createPartyPostalAddress"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| productStoreId | String | IN | true |
| setBillingPurpose | String | IN | true |
| setShippingPurpose | String | IN | true |

## createPostalAddressBoundary

- Definition: `applications/party/servicedef/services.xml:693`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PostalAddressBoundary; auth: —
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | id | IN | false |
| geoId | id | IN | false |

## createPriorityType

- Definition: `applications/party/servicedef/services_party.xml:140`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PriorityType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| priorityTypeId | id | INOUT | true |

## createRoleType

- Definition: `applications/party/servicedef/services.xml:250`
- Engine: entity-auto; location: —; invoke: create
- Default entity: RoleType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | false |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| roleTypeId | id | INOUT | false |

## createRoleTypeAttr

- Definition: `applications/party/servicedef/services.xml:1581`
- Engine: entity-auto; location: —; invoke: create
- Default entity: RoleTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| description | description | IN | true |
| roleTypeId | id | IN | — |

## createTelecomNumber

- Definition: `applications/party/servicedef/services_contact.xml:65`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/contact/ContactMechServicesScript.groovy; invoke: createTelecomNumber
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | OUT | false |

## createTermType

- Definition: `applications/party/servicedef/services_agreement.xml:55`
- Engine: entity-auto; location: —; invoke: create
- Default entity: TermType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| termTypeId | id | INOUT | true |

## createTermTypeAttr

- Definition: `applications/party/servicedef/services_agreement.xml:71`
- Engine: entity-auto; location: —; invoke: create
- Default entity: TermTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| description | description | IN | true |
| termTypeId | id | IN | — |

## createUpdatePartyEmailAddress

- Definition: `applications/party/servicedef/services.xml:1416`
- Engine: simple; location: component://party/minilang/contact/PartyContactMechServices.xml; invoke: createUpdatePartyEmailAddress
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | OUT | false |
| contactMechPurposeTypeId | String | IN | true |
| emailAddress | String | INOUT | false |
| partyId | String | IN | true |
| userLogin | org.apache.ofbiz.entity.GenericValue | IN | true |

## createUpdatePartyPostalAddress

- Definition: `applications/party/servicedef/services.xml:1426`
- Engine: simple; location: component://party/minilang/contact/PartyContactMechServices.xml; invoke: createUpdatePartyPostalAddress
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| address1 | — | — | false |
| city | — | — | false |
| contactMechId | String | INOUT | true |
| contactMechPurposeTypeId | String | IN | true |
| partyId | String | IN | false |
| postalCode | — | — | false |
| userLogin | org.apache.ofbiz.entity.GenericValue | IN | true |

## createUpdatePartyRelationshipAndRoles

- Definition: `applications/party/servicedef/services.xml:434`
- Engine: java; location: org.apache.ofbiz.party.party.PartyRelationshipServices; invoke: createUpdatePartyRelationshipAndRoles
- Default entity: PartyRelationship; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| fromDate | date-time | IN | true |
| partyId | String | IN | false |
| partyIdFrom | id | IN | false |
| partyIdTo | id | IN | false |
| partyRelationshipTypeId | id | IN | true |
| permissionsEnumId | id | IN | true |
| positionTitle | name | IN | true |
| priorityTypeId | id | IN | true |
| relationshipName | name | IN | true |
| roleTypeIdFrom | id | IN | false |
| roleTypeIdTo | id | IN | false |
| securityGroupId | id | IN | true |
| statusId | id | IN | true |
| thruDate | date-time | IN | true |

## createUpdatePartyTelecomNumber

- Definition: `applications/party/servicedef/services.xml:1405`
- Engine: simple; location: component://party/minilang/contact/PartyContactMechServices.xml; invoke: createUpdatePartyTelecomNumber
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | INOUT | true |
| contactMechPurposeTypeId | String | IN | true |
| partyId | String | IN | false |
| roleTypeId | String | IN | true |
| userLogin | org.apache.ofbiz.entity.GenericValue | IN | true |

## createUpdatePerson

- Definition: `applications/party/servicedef/services.xml:1396`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyServicesScript.groovy; invoke: createUpdatePerson
- Default entity: Person; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| birthDate | date | IN | true |
| cardId | id-long | IN | true |
| comments | comment | IN | true |
| deceasedDate | date | IN | true |
| employmentStatusEnumId | id | IN | true |
| existingCustomer | indicator | IN | true |
| firstName | name | IN | false |
| firstNameLocal | name | IN | true |
| gender | indicator | IN | true |
| height | floating-point | IN | true |
| lastName | name | IN | false |
| lastNameLocal | name | IN | true |
| maritalStatusTypeId | id | IN | true |
| memberId | id | IN | true |
| middleName | name | IN | true |
| middleNameLocal | name | IN | true |
| monthsWithEmployer | numeric | IN | true |
| mothersMaidenName | long-varchar | IN | true |
| nickname | name | IN | true |
| occupation | name | IN | true |
| oldMaritalStatusEnumId | id | IN | true |
| otherLocal | name | IN | true |
| partyId | id | INOUT | true |
| passportExpireDate | date | IN | true |
| passportNumber | long-varchar | IN | true |
| personalTitle | name | IN | true |
| residenceStatusEnumId | id | IN | true |
| salutation | name | IN | true |
| socialSecurityNumber | long-varchar | IN | true |
| suffix | name | IN | true |
| totalYearsWorkExperience | floating-point | IN | true |
| userLogin | org.apache.ofbiz.entity.GenericValue | IN | true |
| weight | floating-point | IN | true |
| yearsWithEmployer | numeric | IN | true |

## createVendor

- Definition: `applications/party/servicedef/services.xml:768`
- Engine: entity-auto; location: —; invoke: create
- Default entity: Vendor; auth: true
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| manifestCompanyName | name | IN | true |
| manifestCompanyTitle | name | IN | true |
| manifestLogoUrl | url | IN | true |
| manifestPolicies | very-long | IN | true |
| partyId | id | IN | false |

## declinePartyInvitation

- Definition: `applications/party/servicedef/services.xml:1244`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyInvitationServices.groovy; invoke: updatePartyInvitation
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: accAndDecPartyInvitationPermissionCheck; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyInvitationId | String | IN | false |
| statusId | String | IN | true |

## deleteAddendum

- Definition: `applications/party/servicedef/services_agreement.xml:97`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: Addendum; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| addendumId | id | IN | — |

## deleteAgreementAttribute

- Definition: `applications/party/servicedef/services.xml:1545`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AgreementAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| attrName | id-long | IN | false |

## deleteAgreementContentType

- Definition: `applications/party/servicedef/services_agreement.xml:113`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AgreementContentType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementContentTypeId | id | IN | — |

## deleteAgreementItemType

- Definition: `applications/party/servicedef/services_agreement.xml:49`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AgreementItemType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementItemTypeId | id | IN | — |

## deleteAgreementItemTypeAttr

- Definition: `applications/party/servicedef/services.xml:1566`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AgreementItemTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementItemTypeId | id | IN | false |
| attrName | id-long | IN | false |

## deleteAgreementTermAttribute

- Definition: `applications/party/servicedef/services_agreement.xml:34`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AgreementTermAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementTermId | id | IN | false |
| attrName | id-long | IN | false |

## deleteCommunicationEvent

- Definition: `applications/party/servicedef/services.xml:855`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/communication/CommunicationEventServicesScript.groovy; invoke: deleteCommunicationEvent
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: partyCommunicationEventPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| delContentDataResource | String | IN | true |

## deleteCommunicationEventPrpTyp

- Definition: `applications/party/servicedef/services.xml:1622`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: CommunicationEventPrpTyp; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventPrpTypId | id | IN | false |

## deleteCommunicationEventType

- Definition: `applications/party/servicedef/services_communication.xml:38`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: CommunicationEventType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventTypeId | id | IN | — |
| contactMechTypeId | id | IN | true |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## deleteCommunicationEventWorkEffort

- Definition: `applications/party/servicedef/services.xml:863`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/communication/CommunicationEventServicesScript.groovy; invoke: deleteCommunicationEventWorkEffort
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: partyCommunicationEventPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| delContentDataResource | String | IN | true |

## deleteContactMechLink

- Definition: `applications/party/servicedef/services.xml:688`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ContactMechLink; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechIdFrom | id | IN | false |
| contactMechIdTo | id | IN | false |

## deleteContactMechPurposeType

- Definition: `applications/party/servicedef/services_contact.xml:136`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ContactMechPurposeType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechPurposeTypeId | id | IN | — |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## deleteContactMechType

- Definition: `applications/party/servicedef/services_contact.xml:120`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ContactMechType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechTypeId | id | IN | — |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## deleteContactMechTypeAttr

- Definition: `applications/party/servicedef/services_contact.xml:151`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ContactMechTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| contactMechTypeId | id | IN | — |

## deleteContactMechTypePurpose

- Definition: `applications/party/servicedef/services_contact.xml:166`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ContactMechTypePurpose; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechPurposeTypeId | id | IN | — |
| contactMechTypeId | id | IN | — |

## deleteCustRequestCommEvent

- Definition: `applications/party/servicedef/services.xml:893`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: CustRequestCommEvent; auth: true
- Use transaction: —; timeout: —
- Permission service: partyCommunicationEventPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | id | IN | — |
| custRequestId | id | IN | — |

## deleteMaritalStatus

- Definition: `applications/party/servicedef/services.xml:1646`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: MaritalStatus; auth: true
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | — |
| maritalStatusTypeId | id | IN | — |
| partyId | id | IN | — |

## deleteMaritalStatusType

- Definition: `applications/party/servicedef/services.xml:1674`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: MaritalStatusType; auth: —
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| maritalStatusTypeId | id | IN | — |

## deleteNeedType

- Definition: `applications/party/servicedef/services.xml:1606`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: NeedType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| needTypeId | id | IN | false |

## deletePartyClassification

- Definition: `applications/party/servicedef/services.xml:718`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PartyClassification; auth: —
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| partyClassificationGroupId | id | IN | false |
| partyId | id | IN | false |

## deletePartyClassificationGroup

- Definition: `applications/party/servicedef/services.xml:735`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PartyClassificationGroup; auth: —
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyClassificationGroupId | id | IN | false |

## deletePartyClassificationType

- Definition: `applications/party/servicedef/services_party.xml:38`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PartyClassificationType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyClassificationTypeId | id | IN | — |

## deletePartyContactMech

- Definition: `applications/party/servicedef/services.xml:489`
- Engine: simple; location: component://party/minilang/contact/PartyContactMechServices.xml; invoke: deletePartyContactMech
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: partyContactMechPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | IN | false |
| partyId | String | IN | true |

## deletePartyContactMechPurpose

- Definition: `applications/party/servicedef/services.xml:673`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PartyContactMechPurpose; auth: —
- Use transaction: —; timeout: —
- Permission service: partyIdPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | id | IN | false |
| contactMechPurposeTypeId | id | IN | false |
| fromDate | date-time | IN | false |
| partyId | id | IN | false |

## deletePartyContentType

- Definition: `applications/party/servicedef/services_party.xml:54`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PartyContentType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyContentTypeId | id | IN | — |

## deletePartyIcsAvsOverride

- Definition: `applications/party/servicedef/services_party.xml:86`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PartyIcsAvsOverride; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | id | IN | — |

## deletePartyIdentification

- Definition: `applications/party/servicedef/services.xml:754`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PartyIdentification; auth: —
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | id | IN | — |
| partyIdentificationTypeId | id | IN | — |

## deletePartyIdentificationType

- Definition: `applications/party/servicedef/services_party.xml:102`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PartyIdentificationType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyIdentificationTypeId | id | IN | — |

## deletePartyInvitation

- Definition: `applications/party/servicedef/services.xml:1205`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PartyInvitation; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyInvitationId | id | IN | — |

## deletePartyInvitationGroupAssoc

- Definition: `applications/party/servicedef/services.xml:1215`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PartyInvitationGroupAssoc; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyIdTo | id | IN | — |
| partyInvitationId | id | IN | — |

## deletePartyInvitationRoleAssoc

- Definition: `applications/party/servicedef/services.xml:1225`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PartyInvitationRoleAssoc; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyInvitationId | id | IN | — |
| roleTypeId | id | IN | — |

## deletePartyRelationship

- Definition: `applications/party/servicedef/services.xml:403`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyServicesScript.groovy; invoke: deletePartyRelationship
- Default entity: PartyRelationship; auth: true
- Use transaction: —; timeout: —
- Permission service: partyRelationshipPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| partyIdFrom | id | IN | true |
| partyIdTo | id | IN | false |
| roleTypeIdFrom | id | IN | true |
| roleTypeIdTo | id | IN | true |

## deletePartyRole

- Definition: `applications/party/servicedef/services.xml:324`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PartyRole; auth: true
- Use transaction: —; timeout: —
- Permission service: partyRolePermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |

## deletePartyType

- Definition: `applications/party/servicedef/services_party.xml:118`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PartyType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyTypeId | id | IN | — |

## deletePartyTypeAttr

- Definition: `applications/party/servicedef/services_party.xml:134`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PartyTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| partyTypeId | id | IN | — |

## deletePostalAddressBoundary

- Definition: `applications/party/servicedef/services.xml:698`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PostalAddressBoundary; auth: —
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | id | IN | false |
| geoId | id | IN | false |

## deletePriorityType

- Definition: `applications/party/servicedef/services_party.xml:150`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PriorityType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| priorityTypeId | id | IN | — |

## deleteRoleType

- Definition: `applications/party/servicedef/services.xml:1577`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: RoleType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| roleTypeId | id | IN | — |

## deleteRoleTypeAttr

- Definition: `applications/party/servicedef/services.xml:1591`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: RoleTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| roleTypeId | id | IN | — |

## deleteTermType

- Definition: `applications/party/servicedef/services_agreement.xml:65`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: TermType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| termTypeId | id | IN | — |

## deleteTermTypeAttr

- Definition: `applications/party/servicedef/services_agreement.xml:81`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: TermTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| termTypeId | id | IN | — |

## deleteVendor

- Definition: `applications/party/servicedef/services.xml:780`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: Vendor; auth: true
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | id | IN | false |

## ensureNaPartyRole

- Definition: `applications/party/servicedef/services.xml:344`
- Engine: simple; location: component://party/minilang/party/PartySimpleMethods.xml; invoke: ensureNaPartyRole
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | String | IN | false |

## ensurePartyRole

- Definition: `applications/party/servicedef/services.xml:330`
- Engine: simple; location: component://party/minilang/party/PartySimpleMethods.xml; invoke: ensureNaPartyRole
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | String | IN | false |
| roleTypeId | String | IN | false |

## ensurePartyRoleFrom

- Definition: `applications/party/servicedef/services.xml:353`
- Engine: simple; location: component://party/minilang/party/PartySimpleMethods.xml; invoke: ensureNaPartyRole
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyIdFrom | String | IN | false |
| roleTypeIdFrom | String | IN | true |

## ensurePartyRoleTo

- Definition: `applications/party/servicedef/services.xml:363`
- Engine: simple; location: component://party/minilang/party/PartySimpleMethods.xml; invoke: ensureNaPartyRole
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyIdTo | String | IN | false |
| roleTypeIdTo | String | IN | true |

## expireMaritalStatus

- Definition: `applications/party/servicedef/services.xml:1641`
- Engine: entity-auto; location: —; invoke: expire
- Default entity: MaritalStatus; auth: true
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | — |
| maritalStatusTypeId | id | IN | — |
| partyId | id | IN | — |

## expirePartyContactMechPurpose

- Definition: `applications/party/servicedef/services.xml:678`
- Engine: entity-auto; location: —; invoke: expire
- Default entity: PartyContactMechPurpose; auth: —
- Use transaction: —; timeout: —
- Permission service: partyContactMechPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | id | IN | false |
| contactMechPurposeTypeId | id | IN | false |
| fromDate | date-time | IN | false |
| partyId | id | IN | false |

## expirePartyGeoPoint

- Definition: `applications/party/servicedef/services_party.xml:70`
- Engine: entity-auto; location: —; invoke: expire
- Default entity: PartyGeoPoint; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | — |
| geoPointId | id | IN | — |
| partyId | id | IN | — |

## findPartiesById

- Definition: `applications/party/servicedef/services.xml:306`
- Engine: java; location: org.apache.ofbiz.party.party.PartyServices; invoke: findPartyById
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| idToFind | String | IN | false |
| partiesFound | List | OUT | true |
| party | org.apache.ofbiz.entity.GenericValue | OUT | true |
| partyIdentificationTypeId | String | IN | true |
| searchAllId | String | IN | true |
| searchPartyFirst | String | IN | true |

## findParty

- Definition: `applications/party/servicedef/services_view.xml:28`
- Engine: java; location: org.apache.ofbiz.party.party.PartyServices; invoke: findParty
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| VIEW_INDEX | String | IN | true |
| VIEW_SIZE | String | IN | true |
| address1 | String | IN | true |
| address2 | String | IN | true |
| areaCode | String | IN | true |
| city | String | IN | true |
| contactNumber | String | IN | true |
| countryCode | String | IN | true |
| currentPartyType | org.apache.ofbiz.entity.GenericValue | OUT | true |
| currentRole | org.apache.ofbiz.entity.GenericValue | OUT | true |
| currentStateGeo | org.apache.ofbiz.entity.GenericValue | OUT | true |
| extInfo | String | IN | true |
| firstName | String | IN | true |
| groupName | String | IN | true |
| highIndex | Integer | OUT | false |
| infoString | String | IN | true |
| inventoryItemId | String | IN | true |
| lastName | String | IN | true |
| lookupFlag | String | IN | true |
| lowIndex | Integer | OUT | false |
| ownerPartyIds | List | IN | true |
| paramList | String | OUT | false |
| partyId | String | IN | true |
| partyList | List | OUT | false |
| partyListSize | Integer | OUT | false |
| partyRelationshipTypeId | String | IN | true |
| partyTypeId | String | IN | true |
| partyTypes | List | OUT | false |
| postalCode | String | IN | true |
| roleTypeId | String | IN | true |
| roleTypes | List | OUT | false |
| serialNumber | String | IN | true |
| showAll | String | IN | true |
| softIdentifier | String | IN | true |
| sortField | String | OUT | true |
| stateProvinceGeoId | String | IN | true |
| userLoginId | String | IN | true |
| viewIndex | Integer | OUT | false |
| viewSize | Integer | OUT | false |

## findPartyFromEmailAddress

- Definition: `applications/party/servicedef/services.xml:501`
- Engine: simple; location: component://party/minilang/contact/PartyContactMechServices.xml; invoke: findPartyFromEmailAddress
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| address | String | IN | false |
| caseInsensitive | String | IN | true |
| contactMechId | String | OUT | true |
| fromDate | Timestamp | IN | true |
| partyId | String | OUT | true |
| personal | String | IN | true |

## findPartyFromTelephone

- Definition: `applications/party/servicedef/services.xml:515`
- Engine: simple; location: component://party/minilang/contact/PartyContactMechServices.xml; invoke: findPartyFromTelephone
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | OUT | true |
| partyId | String | OUT | true |
| telno | String | IN | false |

## findPartyFromTelephoneComplete

- Definition: `applications/party/servicedef/services.xml:526`
- Engine: simple; location: component://party/minilang/contact/PartyContactMechServices.xml; invoke: findPartyFromTelephoneComplete
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | OUT | true |
| partyId | String | OUT | true |
| telno | String | IN | false |

## getChildRoleTypes

- Definition: `applications/party/servicedef/services_view.xml:203`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyServicesScript.groovy; invoke: getChildRoleTypes
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| childRoleTypeIdList | List | OUT | false |
| roleTypeId | String | IN | false |

## getParentOrganizations

- Definition: `applications/party/servicedef/services_view.xml:180`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyServicesScript.groovy; invoke: getParentOrganizations
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| getParentsOfParents | String | IN | true |
| organizationPartyId | String | IN | false |
| parentOrganizationPartyIdList | List | OUT | false |

## getPartiesByRelationship

- Definition: `applications/party/servicedef/services_view.xml:172`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyServicesScript.groovy; invoke: getPartiesByRelationship
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| parties | java.util.Collection | OUT | true |
| partyIdFrom | — | — | false |

## getPartiesFromExactEmail

- Definition: `applications/party/servicedef/services_view.xml:129`
- Engine: java; location: org.apache.ofbiz.party.party.PartyServices; invoke: getPartiesFromExactEmail
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| email | String | IN | — |
| parties | java.util.Collection | OUT | true |

## getPartiesFromExternalId

- Definition: `applications/party/servicedef/services_view.xml:165`
- Engine: java; location: org.apache.ofbiz.party.party.PartyServices; invoke: getPartiesFromExternalId
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| externalId | String | IN | false |
| parties | java.util.List | OUT | true |

## getPartiesFromGroupName

- Definition: `applications/party/servicedef/services_view.xml:158`
- Engine: java; location: org.apache.ofbiz.party.party.PartyServices; invoke: getPartiesFromPartyGroup
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| groupName | String | IN | false |
| parties | java.util.Collection | OUT | true |

## getPartiesFromName

- Definition: `applications/party/servicedef/services_view.xml:150`
- Engine: java; location: org.apache.ofbiz.party.party.PartyServices; invoke: getPartiesFromPerson
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| firstName | String | IN | true |
| lastName | String | IN | true |
| parties | java.util.Collection | OUT | true |

## getPartiesFromPartOfEmail

- Definition: `applications/party/servicedef/services_view.xml:136`
- Engine: java; location: org.apache.ofbiz.party.party.PartyServices; invoke: getPartiesFromPartOfEmail
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| email | String | IN | — |
| parties | java.util.Collection | OUT | true |

## getPartiesFromPartOfUserloginId

- Definition: `applications/party/servicedef/services_view.xml:143`
- Engine: java; location: org.apache.ofbiz.party.party.PartyServices; invoke: getPartiesFromPartOfUserloginId
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| parties | java.util.Collection | OUT | true |
| userLoginId | String | IN | — |

## getPartyContactMechValueMaps

- Definition: `applications/party/servicedef/services_view.xml:112`
- Engine: java; location: org.apache.ofbiz.party.contact.ContactMechServices; invoke: getPartyContactMechValueMaps
- Default entity: —; auth: true
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechTypeId | String | IN | true |
| partyId | String | IN | true |
| showOld | Boolean | IN | true |
| userLogin | org.apache.ofbiz.entity.GenericValue | IN | true |
| valueMaps | java.util.List | OUT | true |

## getPartyEmail

- Definition: `applications/party/servicedef/services.xml:1086`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyServicesScript.groovy; invoke: getPartyEmail
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | OUT | true |
| contactMechPurposeTypeId | String | IN | false |
| emailAddress | String | OUT | true |
| partyId | String | IN | false |

## getPartyMainRole

- Definition: `applications/party/servicedef/services.xml:1491`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyServicesScript.groovy; invoke: getPartyMainRole
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | String | OUT | true |
| partyId | String | IN | false |
| roleTypeId | String | OUT | true |

## getPartyNameForDate

- Definition: `applications/party/servicedef/services.xml:196`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyServicesScript.groovy; invoke: getPartyNameForDate
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| compareDate | Timestamp | IN | true |
| firstName | String | OUT | true |
| fullName | String | OUT | true |
| gender | String | OUT | true |
| groupName | String | OUT | true |
| lastName | String | OUT | true |
| lastNameFirst | String | IN | true |
| middleName | String | OUT | true |
| partyId | String | IN | false |
| personalTitle | String | OUT | true |
| suffix | String | OUT | true |

## getPartyPostalAddress

- Definition: `applications/party/servicedef/services.xml:1115`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyServicesScript.groovy; invoke: getPartyPostalAddress
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | OUT | true |
| contactMechPurposeTypeId | String | INOUT | true |
| partyId | String | IN | false |

## getPartyTelephone

- Definition: `applications/party/servicedef/services.xml:1103`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyServicesScript.groovy; invoke: getPartyTelephone
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| areaCode | String | OUT | true |
| contactMechId | String | OUT | true |
| contactMechPurposeTypeId | String | INOUT | true |
| contactNumber | String | OUT | true |
| countryCode | String | OUT | true |
| extension | String | OUT | true |
| partyId | String | IN | false |

## getPerson

- Definition: `applications/party/servicedef/services_view.xml:122`
- Engine: java; location: org.apache.ofbiz.party.party.PartyServices; invoke: getPerson
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| lookupPerson | org.apache.ofbiz.entity.GenericValue | OUT | — |
| partyId | String | IN | — |

## getPostalAddressBoundary

- Definition: `applications/party/servicedef/services_view.xml:222`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyServicesScript.groovy; invoke: getPostalAddressBoundary
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | IN | false |
| geos | java.util.List | OUT | true |

## getRelatedParties

- Definition: `applications/party/servicedef/services_view.xml:234`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyServicesScript.groovy; invoke: getRelatedParties
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| includeFromToSwitched | String | IN | true |
| partyIdFrom | String | IN | false |
| partyRelationshipTypeId | String | IN | true |
| recurse | String | IN | true |
| relatedPartyIdList | List | OUT | false |
| roleTypeIdFrom | String | IN | true |
| roleTypeIdFromInclueAllChildTypes | String | IN | true |
| roleTypeIdTo | String | IN | true |
| roleTypeIdToIncludeAllChildTypes | String | IN | true |
| useCache | String | IN | true |

## importAddressMatchMapCsv

- Definition: `applications/party/servicedef/services.xml:35`
- Engine: java; location: org.apache.ofbiz.party.party.PartyServices; invoke: importAddressMatchMapCsv
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| _uploadedFile_contentType | String | IN | false |
| _uploadedFile_fileName | String | IN | false |
| uploadedFile | java.nio.ByteBuffer | IN | false |

## importParty

- Definition: `applications/party/servicedef/services.xml:1550`
- Engine: java; location: org.apache.ofbiz.party.party.PartyServices; invoke: importParty
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: partyGroupPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| uploadedFile | java.nio.ByteBuffer | IN | true |

## linkPartyRecord

- Definition: `applications/party/servicedef/services.xml:283`
- Engine: java; location: org.apache.ofbiz.party.party.PartyServices; invoke: linkParty
- Default entity: —; auth: true
- Use transaction: false; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | String | INOUT | false |
| partyIdTo | String | IN | false |

## logIncomingMessage

- Definition: `applications/party/servicedef/services.xml:1459`
- Engine: java; location: org.apache.ofbiz.party.communication.CommunicationEventServices; invoke: logIncomingMessage
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "mailProcessInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## lookupParty

- Definition: `applications/party/servicedef/services.xml:290`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/LookupServices.groovy; invoke: lookupParty
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| firstName | String | IN | true |
| lastName | String | IN | true |
| lookupResult | List | OUT | false |

## migratePersonMaritalStatusType

- Definition: `applications/party/servicedef/services.xml:1657`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyServicesScript.groovy; invoke: migratePersonMaritalStatusType
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## partyBasePermissionCheck

- Definition: `applications/party/servicedef/services.xml:1262`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyPermissionServices.groovy; invoke: basePermissionCheck
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## partyCommunicationEventPermissionCheck

- Definition: `applications/party/servicedef/services.xml:1368`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyPermissionServices.groovy; invoke: partyCommunicationEventPermissionCheck
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyIdFrom | String | IN | true |
| partyIdTo | String | IN | true |

## partyContactMechPermissionCheck

- Definition: `applications/party/servicedef/services.xml:1331`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyPermissionServices.groovy; invoke: partyContactMechPermissionCheck
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | String | IN | true |

## partyDatasourcePermissionCheck

- Definition: `applications/party/servicedef/services.xml:1305`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyPermissionServices.groovy; invoke: partyDatasourcePermissionCheck
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## partyGroupPermissionCheck

- Definition: `applications/party/servicedef/services.xml:1296`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyPermissionServices.groovy; invoke: partyGroupPermissionCheck
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | String | INOUT | true |

## partyIdPermissionCheck

- Definition: `applications/party/servicedef/services.xml:1277`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyPermissionServices.groovy; invoke: basePlusPartyIdPermissionCheck
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | String | INOUT | true |

## partyRelationshipPermissionCheck

- Definition: `applications/party/servicedef/services.xml:1322`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyPermissionServices.groovy; invoke: partyRelationshipPermissionCheck
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyIdFrom | String | IN | true |

## partyRolePermissionCheck

- Definition: `applications/party/servicedef/services.xml:1313`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyPermissionServices.groovy; invoke: partyRolePermissionCheck
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | String | INOUT | true |

## partyStatusPermissionCheck

- Definition: `applications/party/servicedef/services.xml:1287`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyPermissionServices.groovy; invoke: partyStatusPermissionCheck
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | String | IN | true |

## performFindParty

- Definition: `applications/party/servicedef/services_view.xml:74`
- Engine: java; location: org.apache.ofbiz.party.party.PartyServices; invoke: performFindParty
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| address1 | String | IN | true |
| address2 | String | IN | true |
| areaCode | String | IN | true |
| city | String | IN | true |
| contactNumber | String | IN | true |
| countryCode | String | IN | true |
| extCond | org.apache.ofbiz.entity.condition.EntityCondition | IN | true |
| extInfo | String | IN | true |
| externalId | String | IN | true |
| firstName | String | IN | true |
| groupName | String | IN | true |
| idValue | String | IN | true |
| infoString | String | IN | true |
| inventoryItemId | String | IN | true |
| lastName | String | IN | true |
| listIt | org.apache.ofbiz.entity.util.EntityListIterator | OUT | true |
| noConditionFind | String | IN | true |
| ownerPartyIds | List | IN | true |
| partyClassificationGroupId | List | IN | true |
| partyId | String | IN | true |
| partyIdentificationTypeId | String | IN | true |
| partyRelationshipTypeId | String | IN | true |
| partyTypeId | String | IN | true |
| postalCode | String | IN | true |
| roleTypeId | String | IN | true |
| serialNumber | String | IN | true |
| softIdentifier | String | IN | true |
| sortField | String | IN | true |
| stateProvinceGeoId | String | IN | true |
| userLoginId | String | IN | true |

## processBouncedMessage

- Definition: `applications/party/servicedef/services.xml:1453`
- Engine: java; location: org.apache.ofbiz.party.communication.CommunicationEventServices; invoke: processBouncedMessage
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "mailProcessInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## quickCreateCustomer

- Definition: `applications/party/servicedef/services.xml:1464`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyServicesScript.groovy; invoke: quickCreateCustomer
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactListId | String | IN | true |
| emailAddress | String | IN | false |
| firstName | String | IN | false |
| lastName | String | IN | false |
| partyId | String | OUT | false |
| subscribeContactList | String | IN | true |

## removeAddressMatchMap

- Definition: `applications/party/servicedef/services.xml:43`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AddressMatchMap; auth: true
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| mapKey | id-vlong | IN | false |
| mapValue | id-vlong | IN | false |

## removeCommunicationEventPurpose

- Definition: `applications/party/servicedef/services.xml:883`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: CommunicationEventPurpose; auth: true
- Use transaction: —; timeout: —
- Permission service: partyCommunicationEventPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | id | IN | false |
| communicationEventPrpTypId | id | IN | false |

## removeCommunicationEventRole

- Definition: `applications/party/servicedef/services.xml:924`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/communication/CommunicationEventServicesScript.groovy; invoke: removeCommunicationEventRole
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: partyCommunicationEventPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| delContentDataResource | String | IN | true |
| deleteCommEventIfLast | String | IN | true |

## removeContactMechAttribute

- Definition: `applications/party/servicedef/services.xml:658`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ContactMechAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | false |
| contactMechId | id | IN | false |

## removePartyAttribute

- Definition: `applications/party/servicedef/services.xml:277`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PartyAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | false |
| partyId | id | IN | false |

## removePartyContent

- Definition: `applications/party/servicedef/services.xml:1065`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PartyContent; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| fromDate | date-time | IN | false |
| partyContentTypeId | id | IN | false |
| partyId | id | IN | false |

## saveMaritalStatusChange

- Definition: `applications/party/servicedef/services.xml:1651`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyServicesScript.groovy; invoke: saveMaritalStatusChange
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| maritalStatusTypeId | String | IN | — |
| partyId | String | IN | — |

## savePartyNameChange

- Definition: `applications/party/servicedef/services.xml:185`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyServicesScript.groovy; invoke: savePartyNameChange
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| firstName | String | IN | true |
| groupName | String | IN | true |
| lastName | String | IN | true |
| middleName | String | IN | true |
| partyId | String | IN | true |
| personalTitle | String | IN | true |
| suffix | String | IN | true |

## sendAccountActivatedEmailNotification

- Definition: `applications/party/servicedef/services.xml:1522`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyServicesScript.groovy; invoke: sendAccountActivatedEmailNotification
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| productStoreId | String | IN | false |
| userLoginId | String | IN | false |

## sendCommEventAsEmail

- Definition: `applications/party/servicedef/services.xml:962`
- Engine: java; location: org.apache.ofbiz.party.communication.CommunicationEventServices; invoke: sendCommEventAsEmail
- Default entity: —; auth: true
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | String | IN | false |

## sendCommEventAsFtp

- Definition: `applications/party/servicedef/services.xml:970`
- Engine: java; location: org.apache.ofbiz.party.communication.CommunicationEventServices; invoke: sendCommEventAsFtp
- Default entity: —; auth: true
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | String | IN | — |

## sendContactUsEmailToCompany

- Definition: `applications/party/servicedef/services.xml:1503`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/communication/CommunicationEventServicesScript.groovy; invoke: sendContactUsEmailToCompany
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "createCommunicationEventWithoutPermission"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| countryCode | String | IN | true |
| emailAddress | String | IN | true |
| emailType | String | IN | true |
| firstName | String | IN | true |
| lastName | String | IN | true |
| partyIdTo | — | — | false |
| postalCode | String | IN | true |
| productStoreId | String | IN | true |

## sendCreatePartyEmailNotification

- Definition: `applications/party/servicedef/services.xml:1148`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyServicesScript.groovy; invoke: sendCreatePartyEmailNotification
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emailAddress | String | IN | false |
| partyId | String | IN | false |
| productStoreId | String | IN | false |

## sendEmailDated

- Definition: `applications/party/servicedef/services.xml:1047`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/communication/CommunicationEventServicesScript.groovy; invoke: sendEmailDated
- Default entity: —; auth: true
- Use transaction: false; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## sendEmailToContactList

- Definition: `applications/party/servicedef/services.xml:1029`
- Engine: java; location: org.apache.ofbiz.party.communication.CommunicationEventServices; invoke: sendEmailToContactList
- Default entity: —; auth: true
- Use transaction: true; timeout: 300
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | String | IN | false |
| contactListId | String | IN | false |

## sendUpdatePersonalInfoEmailNotification

- Definition: `applications/party/servicedef/services.xml:1142`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyServicesScript.groovy; invoke: sendUpdatePersonalInfoEmailNotification
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | String | IN | true |
| productStoreId | String | IN | true |
| updatedUserLogin | org.apache.ofbiz.entity.GenericValue | IN | true |

## sendVerifyEmailAddressNotification

- Definition: `applications/party/servicedef/services.xml:1174`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/contact/ContactMechServicesScript.groovy; invoke: sendVerifyEmailAddressNotification
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emailAddress | String | IN | false |

## setCommEventComplete

- Definition: `applications/party/servicedef/services.xml:1039`
- Engine: java; location: org.apache.ofbiz.party.communication.CommunicationEventServices; invoke: setCommEventComplete
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | String | IN | false |
| partyIdFrom | String | IN | true |

## setCommEventRoleToRead

- Definition: `applications/party/servicedef/services.xml:956`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/communication/CommunicationEventServicesScript.groovy; invoke: setCommEventRoleToRead
- Default entity: CommunicationEventRole; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | id | IN | false |
| partyId | id | IN | true |
| roleTypeId | id | IN | true |

## setCommunicationEventRoleStatus

- Definition: `applications/party/servicedef/services.xml:806`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/communication/CommunicationEventServicesScript.groovy; invoke: setCommunicationEventRoleStatus
- Default entity: CommunicationEventRole; auth: true
- Use transaction: —; timeout: —
- Permission service: partyCommunicationEventPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | id | IN | false |
| oldStatusId | String | OUT | true |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |
| statusId | String | IN | false |

## setCommunicationEventStatus

- Definition: `applications/party/servicedef/services.xml:795`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/communication/CommunicationEventServicesScript.groovy; invoke: setCommunicationEventStatus
- Default entity: CommunicationEvent; auth: true
- Use transaction: —; timeout: —
- Permission service: partyCommunicationEventPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | id | IN | false |
| oldStatusId | String | OUT | true |
| partyIdFrom | String | IN | true |
| setRoleStatusToComplete | String | IN | — |
| statusId | String | IN | false |

## setPartyProfileDefaults

- Definition: `applications/party/servicedef/services.xml:257`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyServicesScript.groovy; invoke: setPartyProfileDefaults
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: partyIdPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| productStoreId | — | — | false |

## setPartyStatus

- Definition: `applications/party/servicedef/services.xml:55`
- Engine: java; location: org.apache.ofbiz.party.party.PartyServices; invoke: setPartyStatus
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: partyStatusPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| oldStatusId | String | OUT | false |
| partyId | String | IN | false |
| statusDate | Timestamp | IN | true |
| statusId | String | IN | false |

## storeIncomingEmail

- Definition: `applications/party/servicedef/services.xml:1014`
- Engine: java; location: org.apache.ofbiz.party.communication.CommunicationEventServices; invoke: storeIncomingEmail
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | String | OUT | true |
| messageWrapper | org.apache.ofbiz.service.mail.MimeMessageWrapper | IN | — |
| statusId | String | OUT | true |

## updateAddendum

- Definition: `applications/party/servicedef/services_agreement.xml:92`
- Engine: entity-auto; location: —; invoke: update
- Default entity: Addendum; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| addendumCreationDate | date-time | IN | true |
| addendumEffectiveDate | date-time | IN | true |
| addendumId | id | IN | — |
| addendumText | long-varchar | IN | true |
| agreementId | id | IN | true |
| agreementItemSeqId | id | IN | true |

## updateAffiliate

- Definition: `applications/party/servicedef/services.xml:228`
- Engine: java; location: org.apache.ofbiz.party.party.PartyServices; invoke: updateAffiliate
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| affiliateDescription | String | IN | true |
| affiliateName | String | IN | — |
| partyId | String | IN | true |
| sitePageViews | String | IN | true |
| siteType | String | IN | true |
| siteVisitors | String | IN | true |
| yearEstablished | String | IN | true |

## updateAgreementAttribute

- Definition: `applications/party/servicedef/services.xml:1540`
- Engine: entity-auto; location: —; invoke: update
- Default entity: AgreementAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| attrDescription | description | IN | true |
| attrName | id-long | IN | false |
| attrValue | value | IN | true |

## updateAgreementContentType

- Definition: `applications/party/servicedef/services_agreement.xml:108`
- Engine: entity-auto; location: —; invoke: update
- Default entity: AgreementContentType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementContentTypeId | id | IN | — |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## updateAgreementItemType

- Definition: `applications/party/servicedef/services_agreement.xml:44`
- Engine: entity-auto; location: —; invoke: update
- Default entity: AgreementItemType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementItemTypeId | id | IN | — |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## updateAgreementItemTypeAttr

- Definition: `applications/party/servicedef/services.xml:1561`
- Engine: entity-auto; location: —; invoke: update
- Default entity: AgreementItemTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementItemTypeId | id | IN | false |
| attrName | id-long | IN | false |
| description | description | IN | true |

## updateAgreementTermAttribute

- Definition: `applications/party/servicedef/services_agreement.xml:30`
- Engine: entity-auto; location: —; invoke: update
- Default entity: AgreementTermAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementTermId | id | IN | false |
| attrDescription | description | IN | true |
| attrName | id-long | IN | false |
| attrValue | value | IN | true |

## updateCommEventAfterEmail

- Definition: `applications/party/servicedef/services.xml:1005`
- Engine: java; location: org.apache.ofbiz.party.communication.CommunicationEventServices; invoke: updateCommEventAfterEmail
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | String | IN | false |
| messageWrapper | org.apache.ofbiz.service.mail.MimeMessageWrapper | IN | false |

## updateCommunicationEvent

- Definition: `applications/party/servicedef/services.xml:841`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/communication/CommunicationEventServicesScript.groovy; invoke: updateCommunicationEvent
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: partyCommunicationEventPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechPurposeTypeIdFrom | String | IN | true |
| content | — | — | — |
| messageId | — | — | — |
| oldStatusId | String | OUT | true |
| subject | — | — | — |

## updateCommunicationEventPrpTyp

- Definition: `applications/party/servicedef/services.xml:1617`
- Engine: entity-auto; location: —; invoke: update
- Default entity: CommunicationEventPrpTyp; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventPrpTypId | id | IN | false |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## updateCommunicationEventPurpose

- Definition: `applications/party/servicedef/services.xml:878`
- Engine: entity-auto; location: —; invoke: update
- Default entity: CommunicationEventPurpose; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | id | IN | false |
| communicationEventPrpTypId | id | IN | false |
| description | description | IN | true |

## updateCommunicationEventRole

- Definition: `applications/party/servicedef/services.xml:917`
- Engine: entity-auto; location: —; invoke: update
- Default entity: CommunicationEventRole; auth: true
- Use transaction: —; timeout: —
- Permission service: partyCommunicationEventPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | id | IN | false |
| contactMechId | id | IN | true |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |
| statusId | id | IN | true |

## updateCommunicationEventType

- Definition: `applications/party/servicedef/services_communication.xml:33`
- Engine: entity-auto; location: —; invoke: update
- Default entity: CommunicationEventType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventTypeId | id | IN | — |
| contactMechTypeId | id | IN | true |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## updateContactMech

- Definition: `applications/party/servicedef/services_contact.xml:34`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/contact/ContactMechServicesScript.groovy; invoke: updateContactMech
- Default entity: ContactMech; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | id | INOUT | false |
| contactMechTypeId | id | IN | true |
| infoString | long-varchar | IN | true |

## updateContactMechAndPurposes

- Definition: `applications/party/servicedef/services.xml:1390`
- Engine: simple; location: component://party/minilang/contact/PartyContactMechServices.xml; invoke: updateContactMechAndPurposes
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "updatePostalAddressAndPurposes"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| phoneContactMechId | String | IN | true |

## updateContactMechAttribute

- Definition: `applications/party/servicedef/services.xml:652`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ContactMechAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | false |
| attrValue | value | IN | true |
| contactMechId | id | IN | false |

## updateContactMechPurposeType

- Definition: `applications/party/servicedef/services_contact.xml:131`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ContactMechPurposeType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechPurposeTypeId | id | IN | — |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## updateContactMechType

- Definition: `applications/party/servicedef/services_contact.xml:115`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ContactMechType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechTypeId | id | IN | — |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## updateContactMechTypeAttr

- Definition: `applications/party/servicedef/services_contact.xml:146`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ContactMechTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| contactMechTypeId | id | IN | — |
| description | description | IN | true |

## updateContactMechTypePurpose

- Definition: `applications/party/servicedef/services_contact.xml:161`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ContactMechTypePurpose; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechPurposeTypeId | id | IN | — |
| contactMechTypeId | id | IN | — |

## updateEmailAddress

- Definition: `applications/party/servicedef/services_contact.xml:87`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/contact/ContactMechServicesScript.groovy; invoke: updateEmailAddress
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | INOUT | false |
| emailAddress | String | IN | false |

## updateFtpAddressWithHistory

- Definition: `applications/party/servicedef/services_contact.xml:100`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/contact/ContactMechServicesScript.groovy; invoke: updateFtpAddressWithHistory
- Default entity: FtpAddress; auth: —
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| binaryTransfer | indicator | IN | true |
| contactMechId | String | INOUT | — |
| defaultTimeout | numeric | IN | true |
| filePath | long-varchar | IN | true |
| ftpPassword | long-varchar | IN | true |
| hostname | long-varchar | IN | true |
| oldContactMechId | String | OUT | — |
| passiveMode | indicator | IN | true |
| port | numeric | IN | true |
| username | long-varchar | IN | true |
| zipFile | indicator | IN | true |

## updateMaritalStatus

- Definition: `applications/party/servicedef/services.xml:1635`
- Engine: entity-auto; location: —; invoke: update
- Default entity: MaritalStatus; auth: true
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | — |
| maritalStatusTypeId | id | IN | — |
| partyId | id | IN | — |
| thruDate | date-time | IN | true |

## updateMaritalStatusType

- Definition: `applications/party/servicedef/services.xml:1668`
- Engine: entity-auto; location: —; invoke: update
- Default entity: MaritalStatusType; auth: —
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| maritalStatusTypeId | id | IN | — |
| parentTypeId | id | IN | true |

## updateNeedType

- Definition: `applications/party/servicedef/services.xml:1601`
- Engine: entity-auto; location: —; invoke: update
- Default entity: NeedType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| needTypeId | id | IN | false |

## updatePartyAttribute

- Definition: `applications/party/servicedef/services.xml:271`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PartyAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | false |
| attrValue | value | IN | true |
| partyId | id | IN | false |

## updatePartyCarrierAccount

- Definition: `applications/party/servicedef/services.xml:1136`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PartyCarrierAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accountNumber | id | IN | true |
| carrierPartyId | id | IN | false |
| fromDate | date-time | IN | false |
| partyId | id | IN | false |
| thruDate | date-time | IN | true |

## updatePartyClassification

- Definition: `applications/party/servicedef/services.xml:712`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PartyClassification; auth: —
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| partyClassificationGroupId | id | IN | false |
| partyId | id | IN | false |
| thruDate | date-time | IN | true |

## updatePartyClassificationGroup

- Definition: `applications/party/servicedef/services.xml:729`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PartyClassificationGroup; auth: —
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| parentGroupId | id | IN | true |
| partyClassificationGroupId | id | IN | false |
| partyClassificationTypeId | id | IN | true |

## updatePartyClassificationType

- Definition: `applications/party/servicedef/services_party.xml:33`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PartyClassificationType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| partyClassificationTypeId | id | IN | — |

## updatePartyContactMech

- Definition: `applications/party/servicedef/services.xml:471`
- Engine: simple; location: component://party/minilang/contact/PartyContactMechServices.xml; invoke: updatePartyContactMech
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: partyContactMechPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | INOUT | false |
| contactMechTypeId | String | IN | false |
| infoString | String | IN | true |
| newContactMechId | String | IN | true |

## updatePartyContent

- Definition: `applications/party/servicedef/services.xml:1060`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PartyContent; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| fromDate | date-time | IN | false |
| partyContentTypeId | id | IN | false |
| partyId | id | IN | false |
| thruDate | date-time | IN | true |

## updatePartyContentType

- Definition: `applications/party/servicedef/services_party.xml:49`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PartyContentType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| parentTypeId | id | IN | true |
| partyContentTypeId | id | IN | — |

## updatePartyEmailAddress

- Definition: `applications/party/servicedef/services.xml:600`
- Engine: simple; location: component://party/minilang/contact/PartyContactMechServices.xml; invoke: updatePartyEmailAddress
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: partyContactMechPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | INOUT | false |
| emailAddress | String | IN | false |
| oldContactMechId | String | OUT | false |

## updatePartyFtpAddress

- Definition: `applications/party/servicedef/services.xml:637`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/contact/ContactMechServicesScript.groovy; invoke: updatePartyFtpAddress
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: partyContactMechPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | INOUT | — |

## updatePartyGeoPoint

- Definition: `applications/party/servicedef/services_party.xml:65`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PartyGeoPoint; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | — |
| geoPointId | id | IN | — |
| partyId | id | IN | — |
| thruDate | date-time | IN | true |

## updatePartyGroup

- Definition: `applications/party/servicedef/services.xml:171`
- Engine: java; location: org.apache.ofbiz.party.party.PartyServices; invoke: updatePartyGroup
- Default entity: PartyGroup; auth: true
- Use transaction: —; timeout: —
- Permission service: partyGroupPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| annualRevenue | currency-amount | IN | true |
| comments | comment | IN | true |
| description | String | IN | true |
| externalId | String | IN | true |
| groupName | name | IN | true |
| groupNameLocal | name | IN | true |
| logoImageUrl | url | IN | true |
| numEmployees | numeric | IN | true |
| officeSiteName | name | IN | true |
| partyId | id | IN | true |
| partyTypeId | String | IN | true |
| preferredCurrencyUomId | String | IN | true |
| statusId | String | IN | true |
| tickerSymbol | very-short | IN | true |

## updatePartyIcsAvsOverride

- Definition: `applications/party/servicedef/services_party.xml:81`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PartyIcsAvsOverride; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| avsDeclineString | long-varchar | IN | true |
| partyId | id | IN | — |

## updatePartyIdentification

- Definition: `applications/party/servicedef/services.xml:748`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PartyIdentification; auth: —
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| idValue | id-long | IN | true |
| partyId | id | IN | — |
| partyIdentificationTypeId | id | IN | — |

## updatePartyIdentificationType

- Definition: `applications/party/servicedef/services_party.xml:97`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PartyIdentificationType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| partyIdentificationTypeId | id | IN | — |

## updatePartyInvitation

- Definition: `applications/party/servicedef/services.xml:1199`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyInvitationServices.groovy; invoke: updatePartyInvitation
- Default entity: PartyInvitation; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emailAddress | long-varchar | IN | true |
| lastInviteDate | date-time | IN | true |
| partyId | id | IN | true |
| partyIdFrom | id | IN | true |
| partyInvitationId | id | IN | false |
| statusId | id | IN | true |
| toName | name | IN | true |

## updatePartyPostalAddress

- Definition: `applications/party/servicedef/services.xml:559`
- Engine: simple; location: component://party/minilang/contact/PartyContactMechServices.xml; invoke: updatePartyPostalAddress
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: partyContactMechPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| address1 | — | — | false |
| city | — | — | false |
| contactMechId | String | INOUT | false |
| directions | String | IN | true |
| postalCode | — | — | false |

## updatePartyRelationship

- Definition: `applications/party/servicedef/services.xml:388`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/party/PartyServicesScript.groovy; invoke: updatePartyRelationship
- Default entity: PartyRelationship; auth: true
- Use transaction: —; timeout: —
- Permission service: partyRelationshipPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| fromDate | date-time | IN | false |
| partyIdFrom | id | IN | true |
| partyIdTo | id | IN | false |
| partyRelationshipTypeId | id | IN | true |
| permissionsEnumId | id | IN | true |
| positionTitle | name | IN | true |
| priorityTypeId | id | IN | true |
| relationshipName | name | IN | true |
| roleTypeIdFrom | id | IN | true |
| roleTypeIdTo | id | IN | true |
| securityGroupId | id | IN | true |
| statusId | id | IN | true |
| thruDate | date-time | IN | true |

## updatePartyTelecomNumber

- Definition: `applications/party/servicedef/services.xml:581`
- Engine: simple; location: component://party/minilang/contact/PartyContactMechServices.xml; invoke: updatePartyTelecomNumber
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: partyContactMechPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | INOUT | false |
| contactNumber | — | — | false |

## updatePartyType

- Definition: `applications/party/servicedef/services_party.xml:113`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PartyType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| partyTypeId | id | IN | — |

## updatePartyTypeAttr

- Definition: `applications/party/servicedef/services_party.xml:129`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PartyTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| description | description | IN | true |
| partyTypeId | id | IN | — |

## updatePerson

- Definition: `applications/party/servicedef/services.xml:142`
- Engine: java; location: org.apache.ofbiz.party.party.PartyServices; invoke: updatePerson
- Default entity: Person; auth: true
- Use transaction: —; timeout: —
- Permission service: partyGroupPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| birthDate | date | IN | true |
| cardId | id-long | IN | true |
| comments | comment | IN | true |
| deceasedDate | date | IN | true |
| description | String | IN | true |
| employmentStatusEnumId | id | IN | true |
| existingCustomer | indicator | IN | true |
| externalId | String | IN | true |
| firstName | name | IN | false |
| firstNameLocal | name | IN | true |
| gender | indicator | IN | true |
| height | floating-point | IN | true |
| lastName | name | IN | false |
| lastNameLocal | name | IN | true |
| maritalStatusTypeId | id | IN | true |
| memberId | id | IN | true |
| middleName | name | IN | true |
| middleNameLocal | name | IN | true |
| monthsWithEmployer | numeric | IN | true |
| mothersMaidenName | long-varchar | IN | true |
| nickname | name | IN | true |
| occupation | name | IN | true |
| oldMaritalStatusEnumId | id | IN | true |
| otherLocal | name | IN | true |
| partyId | id | IN | true |
| passportExpireDate | date | IN | true |
| passportNumber | long-varchar | IN | true |
| personalTitle | name | IN | true |
| preferredCurrencyUomId | String | IN | true |
| residenceStatusEnumId | id | IN | true |
| salutation | name | IN | true |
| socialSecurityNumber | long-varchar | IN | true |
| statusId | String | IN | true |
| suffix | name | IN | true |
| totalYearsWorkExperience | floating-point | IN | true |
| weight | floating-point | IN | true |
| yearsWithEmployer | numeric | IN | true |

## updatePostalAddress

- Definition: `applications/party/servicedef/services_contact.xml:50`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/contact/ContactMechServicesScript.groovy; invoke: updatePostalAddress
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| address1 | — | — | false |
| city | — | — | false |
| contactMechId | String | INOUT | false |
| directions | String | IN | true |
| latitude | String | IN | true |
| longitude | String | IN | true |
| oldContactMechId | String | OUT | false |
| partyId | String | IN | true |
| postalCode | — | — | false |

## updatePostalAddressAndPurposes

- Definition: `applications/party/servicedef/services.xml:1382`
- Engine: simple; location: component://party/minilang/contact/PartyContactMechServices.xml; invoke: updatePostalAddressAndPurposes
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "updatePartyPostalAddress"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | — | — | false |
| productStoreId | String | IN | true |
| setBillingPurpose | String | IN | true |
| setShippingPurpose | String | IN | true |

## updatePriorityType

- Definition: `applications/party/servicedef/services_party.xml:145`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PriorityType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| priorityTypeId | id | IN | — |

## updateRoleType

- Definition: `applications/party/servicedef/services.xml:1572`
- Engine: entity-auto; location: —; invoke: update
- Default entity: RoleType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| roleTypeId | id | IN | — |

## updateRoleTypeAttr

- Definition: `applications/party/servicedef/services.xml:1586`
- Engine: entity-auto; location: —; invoke: update
- Default entity: RoleTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| description | description | IN | true |
| roleTypeId | id | IN | — |

## updateTelecomNumber

- Definition: `applications/party/servicedef/services_contact.xml:71`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/contact/ContactMechServicesScript.groovy; invoke: updateTelecomNumber
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | INOUT | false |
| oldContactMechId | String | OUT | false |

## updateTermType

- Definition: `applications/party/servicedef/services_agreement.xml:60`
- Engine: entity-auto; location: —; invoke: update
- Default entity: TermType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| termTypeId | id | IN | — |

## updateTermTypeAttr

- Definition: `applications/party/servicedef/services_agreement.xml:76`
- Engine: entity-auto; location: —; invoke: update
- Default entity: TermTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| description | description | IN | true |
| termTypeId | id | IN | — |

## updateVendor

- Definition: `applications/party/servicedef/services.xml:774`
- Engine: entity-auto; location: —; invoke: update
- Default entity: Vendor; auth: true
- Use transaction: —; timeout: —
- Permission service: partyBasePermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| manifestCompanyName | name | IN | true |
| manifestCompanyTitle | name | IN | true |
| manifestLogoUrl | url | IN | true |
| manifestPolicies | very-long | IN | true |
| partyId | id | IN | false |

## uploadPartyContentFile

- Definition: `applications/party/servicedef/services.xml:1078`
- Engine: group; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: 300
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## verifyEmailAddress

- Definition: `applications/party/servicedef/services.xml:1182`
- Engine: groovy; location: component://party/src/main/groovy/org/apache/ofbiz/party/contact/ContactMechServicesScript.groovy; invoke: verifyEmailAddress
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| verifyHash | String | IN | false |
