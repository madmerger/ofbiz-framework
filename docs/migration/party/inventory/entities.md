# Entity inventory

## Addendum

- Package: org.apache.ofbiz.party.agreement
- Table: `ADDENDUM`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:46`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| addendumId | id | — | Y |
| agreementId | id | — | N |
| agreementItemSeqId | id | — | N |
| addendumCreationDate | date-time | — | N |
| addendumEffectiveDate | date-time | — | N |
| addendumText | long-varchar | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Agreement | — | agreementId → — | — |
| one | AgreementItem | — | agreementId → —, agreementItemSeqId → — | — |

## Agreement

- Package: org.apache.ofbiz.party.agreement
- Table: `AGREEMENT`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:64`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| agreementId | id | — | Y |
| productId | id | — | N |
| partyIdFrom | id | — | N |
| partyIdTo | id | — | N |
| roleTypeIdFrom | id | — | N |
| roleTypeIdTo | id | — | N |
| agreementTypeId | id | — | N |
| agreementDate | date-time | — | N |
| fromDate | date-time | — | N |
| thruDate | date-time | — | N |
| description | description | — | N |
| textData | very-long | — | N |
| statusId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Product (external) | — | productId → — | applications/datamodel/entitydef/product-entitymodel.xml |
| one-nofk | Party | From | partyIdFrom → partyId | — |
| one-nofk | RoleType | From | roleTypeIdFrom → roleTypeId | — |
| one | PartyRole | From | partyIdFrom → partyId, roleTypeIdFrom → roleTypeId | — |
| one-nofk | Party | To | partyIdTo → partyId | — |
| one-nofk | RoleType | To | roleTypeIdTo → roleTypeId | — |
| one | PartyRole | To | partyIdTo → partyId, roleTypeIdTo → roleTypeId | — |
| many | PartyRelationship | — | roleTypeIdFrom → —, roleTypeIdTo → —, partyIdFrom → —, partyIdTo → — | — |
| one | AgreementType | — | agreementTypeId → — | — |
| many | AgreementTypeAttr | — | agreementTypeId → — | — |

## AgreementAttribute

- Package: org.apache.ofbiz.party.agreement
- Table: `AGREEMENT_ATTRIBUTE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:117`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| agreementId | id | — | Y |
| attrName | id-long | — | Y |
| attrValue | value | — | N |
| attrDescription | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Agreement | — | agreementId → — | — |
| many | AgreementTypeAttr | — | attrName → — | — |

## AgreementGeographicalApplic

- Package: org.apache.ofbiz.party.agreement
- Table: `AGREEMENT_GEOGRAPHICAL_APPLIC`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:133`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| agreementId | id | — | Y |
| agreementItemSeqId | id | — | Y |
| geoId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Agreement | — | agreementId → — | — |
| one | AgreementItem | — | agreementId → —, agreementItemSeqId → — | — |
| one | Geo (external) | — | geoId → — | framework/common/entitydef/entitymodel.xml |

## AgreementItem

- Package: org.apache.ofbiz.party.agreement
- Table: `AGREEMENT_ITEM`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:153`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| agreementId | id | — | Y |
| agreementItemSeqId | id | — | Y |
| agreementItemTypeId | id | — | N |
| currencyUomId | id | — | N |
| agreementText | very-long | — | N |
| agreementImage | object | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Agreement | — | agreementId → — | — |
| one | AgreementItemType | — | agreementItemTypeId → — | — |
| many | AgreementItemTypeAttr | — | agreementItemTypeId → — | — |

## AgreementItemAttribute

- Package: org.apache.ofbiz.party.agreement
- Table: `AGREEMENT_ITEM_ATTRIBUTE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:174`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| agreementId | id | — | Y |
| agreementItemSeqId | id | — | Y |
| attrName | id-long | — | Y |
| attrValue | value | — | N |
| attrDescription | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | AgreementItem | — | agreementId → —, agreementItemSeqId → — | — |
| many | AgreementItemTypeAttr | — | attrName → — | — |

## AgreementItemType

- Package: org.apache.ofbiz.party.agreement
- Table: `AGREEMENT_ITEM_TYPE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:193`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| agreementItemTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | AgreementItemType | Parent | parentTypeId → agreementItemTypeId | — |

## AgreementItemTypeAttr

- Package: org.apache.ofbiz.party.agreement
- Table: `AGREEMENT_ITEM_TYPE_ATTR`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:206`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| agreementItemTypeId | id | — | Y |
| attrName | id-long | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | AgreementItemType | — | agreementItemTypeId → — | — |
| many | AgreementItemAttribute | — | attrName → — | — |
| many | AgreementItem | — | agreementItemTypeId → — | — |

## AgreementContent

- Package: org.apache.ofbiz.party.agreement
- Table: `AGREEMENT_CONTENT`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:224`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| agreementId | id | — | Y |
| agreementItemSeqId | id | — | Y |
| agreementContentTypeId | id | — | Y |
| contentId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Agreement | — | agreementId → — | — |
| one | Content (external) | — | contentId → — | applications/datamodel/entitydef/content-entitymodel.xml |
| one | AgreementContentType | — | agreementContentTypeId → — | — |

## AgreementContentType

- Package: org.apache.ofbiz.party.agreement
- Table: `AGREEMENT_CONTENT_TYPE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:248`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| agreementContentTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | AgreementContentType | Parent | parentTypeId → agreementContentTypeId | — |

## AgreementPartyApplic

- Package: org.apache.ofbiz.party.agreement
- Table: `AGREEMENT_PARTY_APPLIC`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:260`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| agreementId | id | — | Y |
| agreementItemSeqId | id | — | Y |
| partyId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Agreement | — | agreementId → — | — |
| one-nofk | AgreementItem | — | agreementId → —, agreementItemSeqId → — | — |
| one | Party | — | partyId → — | — |

## AgreementProductAppl

- Package: org.apache.ofbiz.party.agreement
- Table: `AGREEMENT_PRODUCT_APPL`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:280`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| agreementId | id | — | Y |
| agreementItemSeqId | id | — | Y |
| productId | id | — | Y |
| price | currency-precise | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one-nofk | Agreement | — | agreementId → — | — |
| one | AgreementItem | — | agreementId → —, agreementItemSeqId → — | — |
| one | Product (external) | — | productId → — | applications/datamodel/entitydef/product-entitymodel.xml |

## AgreementPromoAppl

- Package: org.apache.ofbiz.party.agreement
- Table: `AGREEMENT_PROMO_APPL`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:301`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| agreementId | id | — | Y |
| agreementItemSeqId | id | — | Y |
| productPromoId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| sequenceNum | numeric | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | ProductPromo (external) | — | productPromoId → — | applications/datamodel/entitydef/product-entitymodel.xml |
| one-nofk | Agreement | — | agreementId → — | — |
| one | AgreementItem | — | agreementId → —, agreementItemSeqId → — | — |

## AgreementFacilityAppl

- Package: org.apache.ofbiz.party.agreement
- Table: `AGREEMENT_FACILITY_APPL`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:325`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| agreementId | id | — | Y |
| agreementItemSeqId | id | — | Y |
| facilityId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one-nofk | Agreement | — | agreementId → — | — |
| one | AgreementItem | — | agreementId → —, agreementItemSeqId → — | — |
| one | Facility (external) | — | facilityId → — | applications/datamodel/entitydef/product-entitymodel.xml |

## AgreementRole

- Package: org.apache.ofbiz.party.agreement
- Table: `AGREEMENT_ROLE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:345`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| agreementId | id | — | Y |
| partyId | id | — | Y |
| roleTypeId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Agreement | — | agreementId → — | — |
| one | Party | — | partyId → — | — |
| one-nofk | RoleType | — | roleTypeId → — | — |
| one | PartyRole | — | partyId → —, roleTypeId → — | — |

## AgreementStatus

- Package: org.apache.ofbiz.party.agreement
- Table: `AGREEMENT_STATUS`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:368`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| agreementId | id | — | Y |
| statusId | id | — | Y |
| statusDate | date-time | — | Y |
| comments | comment | — | N |
| changeByUserLoginId | id-vlong | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Agreement | — | agreementId → — | — |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |
| one | UserLogin (external) | ChangeBy | changeByUserLoginId → userLoginId | framework/security/entitydef/entitymodel.xml |

## AgreementTerm

- Package: org.apache.ofbiz.party.agreement
- Table: `AGREEMENT_TERM`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:389`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| agreementTermId | id | — | Y |
| termTypeId | id | — | N |
| agreementId | id | — | N |
| agreementItemSeqId | id | — | N |
| invoiceItemTypeId | id | — | N |
| fromDate | date-time | — | N |
| thruDate | date-time | — | N |
| termValue | currency-precise | — | N |
| termDays | numeric | — | N |
| textValue | description | — | N |
| minQuantity | floating-point | — | N |
| maxQuantity | floating-point | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | TermType | — | termTypeId → — | — |
| one | Agreement | — | agreementId → — | — |
| one | AgreementItem | — | agreementId → —, agreementItemSeqId → — | — |
| one | InvoiceItemType (external) | — | invoiceItemTypeId → — | applications/datamodel/entitydef/accounting-entitymodel.xml |

## AgreementTermAttribute

- Package: org.apache.ofbiz.party.agreement
- Table: `AGREEMENT_TERM_ATTRIBUTE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:420`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| agreementTermId | id | — | Y |
| attrName | id-long | — | Y |
| attrValue | value | — | N |
| attrDescription | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | AgreementTerm | — | agreementTermId → — | — |

## AgreementType

- Package: org.apache.ofbiz.party.agreement
- Table: `AGREEMENT_TYPE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:433`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| agreementTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | AgreementType | Parent | parentTypeId → agreementTypeId | — |

## AgreementTypeAttr

- Package: org.apache.ofbiz.party.agreement
- Table: `AGREEMENT_TYPE_ATTR`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:446`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| agreementTypeId | id | — | Y |
| attrName | id-long | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | AgreementType | — | agreementTypeId → — | — |
| many | AgreementAttribute | — | attrName → — | — |
| many | Agreement | — | agreementTypeId → — | — |

## AgreementWorkEffortApplic

- Package: org.apache.ofbiz.party.agreement
- Table: `AGREEMENT_WORK_EFFORT_APPLIC`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:464`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| agreementId | id | — | Y |
| agreementItemSeqId | id | — | Y |
| workEffortId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Agreement | — | agreementId → — | — |
| one-nofk | AgreementItem | — | agreementId → —, agreementItemSeqId → — | — |
| one | WorkEffort (external) | — | workEffortId → — | applications/datamodel/entitydef/workeffort-entitymodel.xml |

## TermType

- Package: org.apache.ofbiz.party.agreement
- Table: `TERM_TYPE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:484`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| termTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | TermType | Parent | parentTypeId → termTypeId | — |

## TermTypeAttr

- Package: org.apache.ofbiz.party.agreement
- Table: `TERM_TYPE_ATTR`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:496`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| termTypeId | id | — | Y |
| attrName | id-long | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | TermType | — | termTypeId → — | — |
| many | AgreementTermAttribute | — | attrName → — | — |
| many | AgreementTerm | — | termTypeId → — | — |
| many | OrderTermAttribute (external) | — | attrName → — | applications/datamodel/entitydef/order-entitymodel.xml |
| many | OrderTerm (external) | — | termTypeId → — | applications/datamodel/entitydef/order-entitymodel.xml |
| many | QuoteTermAttribute (external) | — | attrName → — | applications/datamodel/entitydef/order-entitymodel.xml |
| many | QuoteTerm (external) | — | termTypeId → — | applications/datamodel/entitydef/order-entitymodel.xml |
| many | InvoiceTermAttribute (external) | — | attrName → — | applications/datamodel/entitydef/accounting-entitymodel.xml |
| many | InvoiceTerm (external) | — | termTypeId → — | applications/datamodel/entitydef/accounting-entitymodel.xml |

## AgreementEmploymentAppl

- Package: org.apache.ofbiz.party.agreement
- Table: `AGREEMENT_EMPLOYMENT_APPL`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:532`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| agreementId | id | — | Y |
| agreementItemSeqId | id | — | Y |
| partyIdFrom | id | — | Y |
| partyIdTo | id | — | Y |
| roleTypeIdFrom | id | — | Y |
| roleTypeIdTo | id | — | Y |
| fromDate | date-time | — | Y |
| agreementDate | date-time | — | N |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Employment (external) | — | roleTypeIdFrom → —, roleTypeIdTo → —, partyIdFrom → —, partyIdTo → —, fromDate → — | applications/datamodel/entitydef/humanres-entitymodel.xml |
| many | Agreement | — | agreementId → partyIdFrom, agreementId → partyIdTo | — |
| one | AgreementItem | — | agreementId → —, agreementItemSeqId → — | — |

## CommContentAssocType

- Package: org.apache.ofbiz.party.communication
- Table: `COMM_CONTENT_ASSOC_TYPE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:664`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| commContentAssocTypeId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## CommEventContentAssoc

- Package: org.apache.ofbiz.party.communication
- Table: `COMM_EVENT_CONTENT_ASSOC`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:669`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contentId | id | — | Y |
| communicationEventId | id | — | Y |
| commContentAssocTypeId | id | — | N |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| sequenceNum | numeric | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Content (external) | From | contentId → — | applications/datamodel/entitydef/content-entitymodel.xml |
| one | CommunicationEvent | — | communicationEventId → — | — |
| one | CommContentAssocType | — | commContentAssocTypeId → — | — |

## CommunicationEvent

- Package: org.apache.ofbiz.party.communication
- Table: `COMMUNICATION_EVENT`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:733`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| communicationEventId | id | — | Y |
| communicationEventTypeId | id | — | N |
| origCommEventId | id | — | N |
| parentCommEventId | id | — | N |
| statusId | id | — | N |
| contactMechTypeId | id | — | N |
| contactMechIdFrom | id | — | N |
| contactMechIdTo | id | — | N |
| roleTypeIdFrom | id | — | N |
| roleTypeIdTo | id | — | N |
| partyIdFrom | id | — | N |
| partyIdTo | id | — | N |
| entryDate | date-time | — | N |
| datetimeStarted | date-time | — | N |
| datetimeEnded | date-time | — | N |
| subject | long-varchar | — | N |
| contentMimeTypeId | id-vlong | — | N |
| content | very-long | — | N |
| note | comment | — | N |
| reasonEnumId | id | — | N |
| contactListId | id | — | N |
| headerString | very-long | — | N |
| fromString | very-long | — | N |
| toString | very-long | — | N |
| ccString | very-long | — | N |
| bccString | very-long | — | N |
| messageId | value | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | CommunicationEventType | — | communicationEventTypeId → — | — |
| one | Party | To | partyIdTo → partyId | — |
| one | RoleType | To | roleTypeIdTo → roleTypeId | — |
| one-nofk | PartyRole | To | partyIdTo → partyId, roleTypeIdTo → roleTypeId | — |
| one | Party | From | partyIdFrom → partyId | — |
| one | RoleType | From | roleTypeIdFrom → roleTypeId | — |
| one-nofk | PartyRole | From | partyIdFrom → partyId, roleTypeIdFrom → roleTypeId | — |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |
| one | ContactMechType | — | contactMechTypeId → — | — |
| one | ContactMech | From | contactMechIdFrom → contactMechId | — |
| one | ContactMech | To | contactMechIdTo → contactMechId | — |
| one | ContactList (external) | — | contactListId → — | applications/datamodel/entitydef/marketing-entitymodel.xml |
| one | MimeType (external) | — | contentMimeTypeId → mimeTypeId | applications/datamodel/entitydef/content-entitymodel.xml |
| one | Enumeration (external) | — | reasonEnumId → enumId | framework/common/entitydef/entitymodel.xml |

## CommunicationEventProduct

- Package: org.apache.ofbiz.party.communication
- Table: `COMMUNICATION_EVENT_PRODUCT`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:893`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| productId | id | — | Y |
| communicationEventId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Product (external) | — | productId → — | applications/datamodel/entitydef/product-entitymodel.xml |
| one | CommunicationEvent | — | communicationEventId → — | — |

## CommunicationEventPrpTyp

- Package: org.apache.ofbiz.party.communication
- Table: `COMMUNICATION_EVENT_PRP_TYP`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:905`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| communicationEventPrpTypId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | CommunicationEventPrpTyp | Parent | parentTypeId → communicationEventPrpTypId | — |

## CommunicationEventPurpose

- Package: org.apache.ofbiz.party.communication
- Table: `COMMUNICATION_EVENT_PURPOSE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:918`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| communicationEventPrpTypId | id | — | Y |
| communicationEventId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | CommunicationEvent | — | communicationEventId → — | — |
| one | CommunicationEventPrpTyp | — | communicationEventPrpTypId → — | — |

## CommunicationEventRole

- Package: org.apache.ofbiz.party.communication
- Table: `COMMUNICATION_EVENT_ROLE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:933`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| communicationEventId | id | — | Y |
| partyId | id | — | Y |
| roleTypeId | id | — | Y |
| contactMechId | id | — | N |
| statusId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | CommunicationEvent | — | communicationEventId → — | — |
| one | Party | — | partyId → — | — |
| one-nofk | RoleType | — | roleTypeId → — | — |
| one | PartyRole | — | partyId → —, roleTypeId → — | — |
| one | ContactMech | — | contactMechId → — | — |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |

## CommunicationEventType

- Package: org.apache.ofbiz.party.communication
- Table: `COMMUNICATION_EVENT_TYPE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:964`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| communicationEventTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |
| contactMechTypeId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | CommunicationEventType | Parent | parentTypeId → communicationEventTypeId | — |
| one | ContactMechType | ContacMechType | contactMechTypeId → — | — |

## ContactMech

- Package: org.apache.ofbiz.party.contact
- Table: `CONTACT_MECH`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:993`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contactMechId | id | — | Y |
| contactMechTypeId | id | — | N |
| infoString | long-varchar | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | ContactMechType | — | contactMechTypeId → — | — |
| many | ContactMechTypeAttr | — | contactMechTypeId → — | — |

## ContactMechAttribute

- Package: org.apache.ofbiz.party.contact
- Table: `CONTACT_MECH_ATTRIBUTE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1010`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contactMechId | id | — | Y |
| attrName | id-long | — | Y |
| attrValue | value | — | N |
| attrDescription | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | ContactMech | — | contactMechId → — | — |
| many | ContactMechTypeAttr | — | attrName → — | — |

## ContactMechLink

- Package: org.apache.ofbiz.party.contact
- Table: `CONTACT_MECH_LINK`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1026`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contactMechIdFrom | id | — | Y |
| contactMechIdTo | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | ContactMech | From | contactMechIdFrom → contactMechId | — |
| one | ContactMech | To | contactMechIdTo → contactMechId | — |

## ContactMechPurposeType

- Package: org.apache.ofbiz.party.contact
- Table: `CONTACT_MECH_PURPOSE_TYPE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1040`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contactMechPurposeTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## ContactMechType

- Package: org.apache.ofbiz.party.contact
- Table: `CONTACT_MECH_TYPE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1050`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contactMechTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | ContactMechType | Parent | parentTypeId → contactMechTypeId | — |

## ContactMechTypeAttr

- Package: org.apache.ofbiz.party.contact
- Table: `CONTACT_MECH_TYPE_ATTR`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1062`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contactMechTypeId | id | — | Y |
| attrName | id-long | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | ContactMechType | — | contactMechTypeId → — | — |
| many | ContactMechAttribute | — | attrName → — | — |
| many | ContactMech | — | contactMechTypeId → — | — |

## ContactMechTypePurpose

- Package: org.apache.ofbiz.party.contact
- Table: `CONTACT_MECH_TYPE_PURPOSE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1080`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contactMechTypeId | id | — | Y |
| contactMechPurposeTypeId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | ContactMechType | — | contactMechTypeId → — | — |
| one | ContactMechPurposeType | — | contactMechPurposeTypeId → — | — |

## EmailAddressVerification

- Package: org.apache.ofbiz.party.contact
- Table: `EMAIL_ADDRESS_VERIFICATION`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1095`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| emailAddress | id-vlong | — | Y |
| verifyHash | value | — | N |
| expireDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## PartyContactMech

- Package: org.apache.ofbiz.party.contact
- Table: `PARTY_CONTACT_MECH`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1152`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| contactMechId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| roleTypeId | id | — | N |
| allowSolicitation | indicator | — | N |
| extension | long-varchar | — | N |
| verified | indicator | — | N |
| comments | comment | — | N |
| yearsWithContactMech | numeric | — | N |
| monthsWithContactMech | numeric | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party | — | partyId → — | — |
| one-nofk | Person | — | partyId → — | — |
| one-nofk | PartyGroup | — | partyId → — | — |
| one | PartyRole | — | partyId → —, roleTypeId → — | — |
| one | RoleType | — | roleTypeId → — | — |
| one | ContactMech | — | contactMechId → — | — |
| one-nofk | TelecomNumber | — | contactMechId → — | — |
| one-nofk | PostalAddress | — | contactMechId → — | — |
| many | PartyContactMechPurpose | — | partyId → —, contactMechId → — | — |

## PartyContactMechPurpose

- Package: org.apache.ofbiz.party.contact
- Table: `PARTY_CONTACT_MECH_PURPOSE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1199`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| contactMechId | id | — | Y |
| contactMechPurposeTypeId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| many | PartyContactMech | — | partyId → —, contactMechId → — | — |
| one | ContactMechPurposeType | — | contactMechPurposeTypeId → — | — |
| one | Party | — | partyId → — | — |
| one-nofk | Person | — | partyId → — | — |
| one-nofk | PartyGroup | — | partyId → — | — |
| one | ContactMech | — | contactMechId → — | — |
| one-nofk | PostalAddress | — | contactMechId → — | — |
| one-nofk | TelecomNumber | — | contactMechId → — | — |

## PostalAddress

- Package: org.apache.ofbiz.party.contact
- Table: `POSTAL_ADDRESS`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1237`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contactMechId | id | — | Y |
| toName | name | — | N |
| attnName | name | — | N |
| address1 | long-varchar | — | N |
| address2 | long-varchar | — | N |
| houseNumber | numeric | — | N |
| houseNumberExt | short-varchar | — | N |
| directions | long-varchar | — | N |
| city | name | — | N |
| cityGeoId | id | — | N |
| postalCode | short-varchar | — | N |
| postalCodeExt | short-varchar | — | N |
| countryGeoId | id | — | N |
| stateProvinceGeoId | id | — | N |
| countyGeoId | id | — | N |
| municipalityGeoId | id | — | N |
| postalCodeGeoId | id | — | N |
| geoPointId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | ContactMech | — | contactMechId → — | — |
| one | Geo (external) | Country | countryGeoId → geoId | framework/common/entitydef/entitymodel.xml |
| one | Geo (external) | StateProvince | stateProvinceGeoId → geoId | framework/common/entitydef/entitymodel.xml |
| one | Geo (external) | County | countyGeoId → geoId | framework/common/entitydef/entitymodel.xml |
| one | Geo (external) | Municipality | municipalityGeoId → geoId | framework/common/entitydef/entitymodel.xml |
| one | Geo (external) | City | cityGeoId → geoId | framework/common/entitydef/entitymodel.xml |
| one | Geo (external) | PostalCode | postalCodeGeoId → geoId | framework/common/entitydef/entitymodel.xml |
| one | GeoPoint (external) | — | geoPointId → — | framework/common/entitydef/entitymodel.xml |

## PostalAddressBoundary

- Package: org.apache.ofbiz.party.contact
- Table: `POSTAL_ADDRESS_BOUNDARY`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1296`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contactMechId | id | — | Y |
| geoId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PostalAddress | — | contactMechId → — | — |
| one | Geo (external) | — | geoId → — | framework/common/entitydef/entitymodel.xml |

## TelecomNumber

- Package: org.apache.ofbiz.party.contact
- Table: `TELECOM_NUMBER`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1310`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contactMechId | id | — | Y |
| countryCode | very-short | — | N |
| areaCode | very-short | — | N |
| contactNumber | short-varchar | — | N |
| askForName | name | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | ContactMech | — | contactMechId → — | — |

## FtpAddress

- Package: org.apache.ofbiz.party.contact
- Table: `FTP_ADDRESS`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1332`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contactMechId | id | — | Y |
| hostname | long-varchar | — | N |
| port | numeric | — | N |
| username | long-varchar | — | N |
| ftpPassword | long-varchar | — | N |
| binaryTransfer | indicator | — | N |
| filePath | long-varchar | — | N |
| zipFile | indicator | — | N |
| passiveMode | indicator | — | N |
| defaultTimeout | numeric | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | ContactMech | — | contactMechId → — | — |

## ValidContactMechRole

- Package: org.apache.ofbiz.party.contact
- Table: `VALID_CONTACT_MECH_ROLE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1350`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| roleTypeId | id | — | Y |
| contactMechTypeId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | RoleType | — | roleTypeId → — | — |
| one | ContactMechType | — | contactMechTypeId → — | — |

## NeedType

- Package: org.apache.ofbiz.party.need
- Table: `NEED_TYPE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1510`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| needTypeId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## PartyNeed

- Package: org.apache.ofbiz.party.need
- Table: `PARTY_NEED`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1517`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyNeedId | id | — | Y |
| partyId | id | — | Y |
| roleTypeId | id | — | Y |
| partyTypeId | id | — | N |
| needTypeId | id | — | N |
| communicationEventId | id | — | N |
| productId | id | — | N |
| productCategoryId | id | — | N |
| visitId | id | — | N |
| datetimeRecorded | date-time | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | NeedType | — | needTypeId → — | — |
| one | Party | — | partyId → — | — |
| one | RoleType | — | roleTypeId → — | — |
| one-nofk | PartyRole | — | partyId → —, roleTypeId → — | — |
| one | PartyType | — | partyTypeId → — | — |
| one | CommunicationEvent | — | communicationEventId → — | — |
| one | Product (external) | — | productId → — | applications/datamodel/entitydef/product-entitymodel.xml |
| one | ProductCategory (external) | — | productCategoryId → — | applications/datamodel/entitydef/product-entitymodel.xml |

## AddressMatchMap

- Package: org.apache.ofbiz.party.party
- Table: `ADDRESS_MATCH_MAP`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1565`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| mapKey | id-vlong | — | Y |
| mapValue | id-vlong | — | Y |
| sequenceNum | numeric | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## Affiliate

- Package: org.apache.ofbiz.party.party
- Table: `AFFILIATE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1574`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| affiliateName | name | — | N |
| affiliateDescription | description | — | N |
| yearEstablished | very-short | — | N |
| siteType | comment | — | N |
| sitePageViews | comment | — | N |
| siteVisitors | comment | — | N |
| dateTimeCreated | date-time | — | N |
| dateTimeApproved | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party | — | partyId → — | — |
| one | PartyGroup | — | partyId → — | — |

## Party

- Package: org.apache.ofbiz.party.party
- Table: `PARTY`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1594`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| partyTypeId | id | — | N |
| externalId | id | — | N |
| preferredCurrencyUomId | id | — | N |
| description | very-long | — | N |
| statusId | id | — | N |
| createdDate | date-time | — | N |
| createdByUserLogin | id-vlong | — | N |
| lastModifiedDate | date-time | — | N |
| lastModifiedByUserLogin | id-vlong | — | N |
| dataSourceId | id | — | N |
| isUnread | indicator | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PartyType | — | partyTypeId → — | — |
| one | UserLogin (external) | CreatedBy | createdByUserLogin → userLoginId | framework/security/entitydef/entitymodel.xml |
| one | UserLogin (external) | LastModifiedBy | lastModifiedByUserLogin → userLoginId | framework/security/entitydef/entitymodel.xml |
| one | Uom (external) | — | preferredCurrencyUomId → uomId | framework/common/entitydef/entitymodel.xml |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |
| many | PartyTypeAttr | — | partyTypeId → — | — |
| one | DataSource (external) | — | dataSourceId → — | framework/common/entitydef/entitymodel.xml |

## PartyIdentification

- Package: org.apache.ofbiz.party.party
- Table: `PARTY_IDENTIFICATION`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1766`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| partyIdentificationTypeId | id | — | Y |
| idValue | id-long | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PartyIdentificationType | — | partyIdentificationTypeId → — | — |
| one | Party | — | partyId → — | — |

## PartyIdentificationType

- Package: org.apache.ofbiz.party.party
- Table: `PARTY_IDENTIFICATION_TYPE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1800`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyIdentificationTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PartyIdentificationType | Parent | parentTypeId → partyIdentificationTypeId | — |

## PartyGeoPoint

- Package: org.apache.ofbiz.party.party
- Table: `PARTY_GEO_POINT`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1814`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| geoPointId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party | — | partyId → — | — |
| one | GeoPoint (external) | — | geoPointId → — | framework/common/entitydef/entitymodel.xml |

## PartyAttribute

- Package: org.apache.ofbiz.party.party
- Table: `PARTY_ATTRIBUTE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1895`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| attrName | id-long | — | Y |
| attrValue | value | — | N |
| attrDescription | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party | — | partyId → — | — |
| many | PartyTypeAttr | — | attrName → — | — |

## PartyCarrierAccount

- Package: org.apache.ofbiz.party.party
- Table: `PARTY_CARRIER_ACCOUNT`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1911`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| carrierPartyId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| accountNumber | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party | — | partyId → — | — |
| one | Party | Carrier | carrierPartyId → partyId | — |

## PartyClassification

- Package: org.apache.ofbiz.party.party
- Table: `PARTY_CLASSIFICATION`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1929`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| partyClassificationGroupId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party | — | partyId → — | — |
| one | PartyClassificationGroup | — | partyClassificationGroupId → — | — |

## PartyClassificationGroup

- Package: org.apache.ofbiz.party.party
- Table: `PARTY_CLASSIFICATION_GROUP`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1946`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyClassificationGroupId | id | — | Y |
| partyClassificationTypeId | id | — | N |
| parentGroupId | id | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PartyClassificationGroup | Parent | parentGroupId → partyClassificationGroupId | — |
| one | PartyClassificationType | — | partyClassificationTypeId → — | — |

## PartyClassificationType

- Package: org.apache.ofbiz.party.party
- Table: `PARTY_CLASSIFICATION_TYPE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1961`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyClassificationTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PartyClassificationType | Parent | parentTypeId → partyClassificationTypeId | — |

## PartyContent

- Package: org.apache.ofbiz.party.party
- Table: `PARTY_CONTENT`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2021`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| contentId | id | — | Y |
| partyContentTypeId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party | — | partyId → — | — |
| one | Content (external) | — | contentId → — | applications/datamodel/entitydef/content-entitymodel.xml |
| one | PartyContentType | — | partyContentTypeId → — | — |

## PartyContentType

- Package: org.apache.ofbiz.party.party
- Table: `PARTY_CONTENT_TYPE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2058`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyContentTypeId | id | — | Y |
| parentTypeId | id | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PartyContentType | Parent | parentTypeId → partyContentTypeId | — |

## PartyDataSource

- Package: org.apache.ofbiz.party.party
- Table: `PARTY_DATA_SOURCE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2070`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| dataSourceId | id | — | Y |
| fromDate | date-time | — | Y |
| visitId | id | — | N |
| comments | comment | — | N |
| isCreate | indicator | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party | — | partyId → — | — |
| one | DataSource (external) | — | dataSourceId → — | framework/common/entitydef/entitymodel.xml |

## PartyGroup

- Package: org.apache.ofbiz.party.party
- Table: `PARTY_GROUP`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2089`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| groupName | name | — | N |
| groupNameLocal | name | — | N |
| officeSiteName | name | — | N |
| annualRevenue | currency-amount | — | N |
| numEmployees | numeric | — | N |
| tickerSymbol | very-short | — | N |
| comments | comment | — | N |
| logoImageUrl | url | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party | — | partyId → — | — |

## PartyIcsAvsOverride

- Package: org.apache.ofbiz.party.party
- Table: `PARTY_ICS_AVS_OVERRIDE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2109`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| avsDeclineString | long-varchar | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party | — | partyId → — | — |

## PartyInvitation

- Package: org.apache.ofbiz.party.party
- Table: `PARTY_INVITATION`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2119`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyInvitationId | id | — | Y |
| partyIdFrom | id | — | N |
| partyId | id | — | N |
| toName | name | — | N |
| emailAddress | long-varchar | — | N |
| statusId | id | — | N |
| lastInviteDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party | — | partyIdFrom → partyId | — |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |

## PartyInvitationGroupAssoc

- Package: org.apache.ofbiz.party.party
- Table: `PARTY_INVITATION_GROUP_ASSOC`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2137`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyInvitationId | id | — | Y |
| partyIdTo | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PartyGroup | To | partyIdTo → partyId | — |
| one | Party | To | partyIdTo → partyId | — |
| one | PartyInvitation | — | partyInvitationId → — | — |

## PartyInvitationRoleAssoc

- Package: org.apache.ofbiz.party.party
- Table: `PARTY_INVITATION_ROLE_ASSOC`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2154`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyInvitationId | id | — | Y |
| roleTypeId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | RoleType | — | roleTypeId → — | — |
| one | PartyInvitation | — | partyInvitationId → — | — |

## PartyNameHistory

- Package: org.apache.ofbiz.party.party
- Table: `PARTY_NAME_HISTORY`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2209`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| changeDate | date-time | — | Y |
| groupName | name | — | N |
| firstName | name | — | N |
| middleName | name | — | N |
| lastName | name | — | N |
| personalTitle | name | — | N |
| suffix | name | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party | — | partyId → — | — |

## PartyNote

- Package: org.apache.ofbiz.party.party
- Table: `PARTY_NOTE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2255`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| noteId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party | — | partyId → — | — |
| one | NoteData (external) | — | noteId → — | framework/common/entitydef/entitymodel.xml |

## PartyProfileDefault

- Package: org.apache.ofbiz.party.party
- Table: `PARTY_PROFILE_DEFAULT`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2284`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| productStoreId | id | — | Y |
| defaultShipAddr | id | — | N |
| defaultBillAddr | id | — | N |
| defaultPayMeth | id | — | N |
| defaultShipMeth | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party | — | partyId → — | — |
| one | ProductStore (external) | — | productStoreId → — | applications/datamodel/entitydef/product-entitymodel.xml |

## PartyRelationship

- Package: org.apache.ofbiz.party.party
- Table: `PARTY_RELATIONSHIP`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2302`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyIdFrom | id | — | Y |
| partyIdTo | id | — | Y |
| roleTypeIdFrom | id | — | Y |
| roleTypeIdTo | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| statusId | id | — | N |
| relationshipName | name | — | N |
| securityGroupId | id | — | N |
| priorityTypeId | id | — | N |
| partyRelationshipTypeId | id | — | N |
| permissionsEnumId | id | — | N |
| positionTitle | name | — | N |
| comments | comment | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one-nofk | Party | From | partyIdFrom → partyId | — |
| one-nofk | Party | To | partyIdTo → partyId | — |
| one-nofk | RoleType | From | roleTypeIdFrom → roleTypeId | — |
| one-nofk | RoleType | To | roleTypeIdTo → roleTypeId | — |
| one | PartyRole | From | partyIdFrom → partyId, roleTypeIdFrom → roleTypeId | — |
| one | PartyRole | To | partyIdTo → partyId, roleTypeIdTo → roleTypeId | — |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |
| one | PriorityType | — | priorityTypeId → — | — |
| one | PartyRelationshipType | — | partyRelationshipTypeId → — | — |
| one | SecurityGroup (external) | — | securityGroupId → groupId | framework/security/entitydef/entitymodel.xml |

## PartyRelationshipType

- Package: org.apache.ofbiz.party.party
- Table: `PARTY_RELATIONSHIP_TYPE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2372`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyRelationshipTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| partyRelationshipName | name | — | N |
| description | description | — | N |
| roleTypeIdValidFrom | id | — | N |
| roleTypeIdValidTo | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PartyRelationshipType | Parent | parentTypeId → partyRelationshipTypeId | — |
| one | RoleType | ValidFrom | roleTypeIdValidFrom → roleTypeId | — |
| one | RoleType | ValidTo | roleTypeIdValidTo → roleTypeId | — |

## PartyRole

- Package: org.apache.ofbiz.party.party
- Table: `PARTY_ROLE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2561`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| roleTypeId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party | — | partyId → — | — |
| one | RoleType | — | roleTypeId → — | — |
| many | RoleTypeAttr | — | roleTypeId → — | — |
| many | PartyAttribute | — | partyId → — | — |

## PartyStatus

- Package: org.apache.ofbiz.party.party
- Table: `PARTY_STATUS`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2746`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| statusId | id | — | Y |
| partyId | id | — | Y |
| statusDate | date-time | — | Y |
| changeByUserLoginId | id-vlong | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |
| one | Party | — | partyId → — | — |
| one | UserLogin (external) | ChangeBy | changeByUserLoginId → userLoginId | framework/security/entitydef/entitymodel.xml |

## PartyType

- Package: org.apache.ofbiz.party.party
- Table: `PARTY_TYPE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2766`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PartyType | Parent | parentTypeId → partyTypeId | — |
| many | PartyType | Sibling | parentTypeId → — | — |

## PartyTypeAttr

- Package: org.apache.ofbiz.party.party
- Table: `PARTY_TYPE_ATTR`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2782`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyTypeId | id | — | Y |
| attrName | id-long | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PartyType | — | partyTypeId → — | — |
| many | PartyAttribute | — | attrName → — | — |
| many | Party | — | partyTypeId → — | — |

## Person

- Package: org.apache.ofbiz.party.party
- Table: `PERSON`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2800`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| salutation | name | — | N |
| firstName | name | — | N |
| middleName | name | — | N |
| lastName | name | — | N |
| personalTitle | name | — | N |
| suffix | name | — | N |
| nickname | name | — | N |
| firstNameLocal | name | — | N |
| middleNameLocal | name | — | N |
| lastNameLocal | name | — | N |
| otherLocal | name | — | N |
| memberId | id | — | N |
| gender | indicator | — | N |
| birthDate | date | — | N |
| deceasedDate | date | — | N |
| height | floating-point | — | N |
| weight | floating-point | — | N |
| mothersMaidenName | long-varchar | — | N |
| oldMaritalStatusEnumId | id | MARITAL_STATUS_ENUM_ID | N |
| maritalStatusTypeId | id | — | N |
| socialSecurityNumber | long-varchar | — | N |
| passportNumber | long-varchar | — | N |
| passportExpireDate | date | — | N |
| totalYearsWorkExperience | floating-point | — | N |
| comments | comment | — | N |
| employmentStatusEnumId | id | — | N |
| residenceStatusEnumId | id | — | N |
| occupation | name | — | N |
| yearsWithEmployer | numeric | — | N |
| monthsWithEmployer | numeric | — | N |
| existingCustomer | indicator | — | N |
| cardId | id-long | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party | — | partyId → — | — |
| one | Enumeration (external) | EmploymentStatus | employmentStatusEnumId → enumId | framework/common/entitydef/entitymodel.xml |
| one | Enumeration (external) | ResidenceStatus | residenceStatusEnumId → enumId | framework/common/entitydef/entitymodel.xml |
| one | MaritalStatusType | — | maritalStatusTypeId → — | — |

## PriorityType

- Package: org.apache.ofbiz.party.party
- Table: `PRIORITY_TYPE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2861`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| priorityTypeId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## RoleType

- Package: org.apache.ofbiz.party.party
- Table: `ROLE_TYPE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2868`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| roleTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | RoleType | Parent | parentTypeId → roleTypeId | — |

## RoleTypeAttr

- Package: org.apache.ofbiz.party.party
- Table: `ROLE_TYPE_ATTR`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2912`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| roleTypeId | id | — | Y |
| attrName | id-long | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | RoleType | — | roleTypeId → — | — |
| many | PartyAttribute | — | attrName → — | — |
| many | PartyRole | — | roleTypeId → — | — |
| many | PartyRelationshipType | ValidFrom | roleTypeId → roleTypeIdValidFrom | — |
| many | PartyRelationshipType | ValidTo | roleTypeId → roleTypeIdValidTo | — |

## Vendor

- Package: org.apache.ofbiz.party.party
- Table: `VENDOR`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2936`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| manifestCompanyName | name | — | N |
| manifestCompanyTitle | name | — | N |
| manifestLogoUrl | url | — | N |
| manifestPolicies | very-long | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party | — | partyId → — | — |

## MaritalStatus

- Package: org.apache.ofbiz.party.person
- Table: `MARITAL_STATUS`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:3088`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| maritalStatusTypeId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party | — | partyId → — | — |
| one | MaritalStatusType | — | maritalStatusTypeId → — | — |

## MaritalStatusType

- Package: org.apache.ofbiz.party.person
- Table: `MARITAL_STATUS_TYPE`
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:3106`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| maritalStatusTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | MaritalStatusType | Parent | parentTypeId → maritalStatusTypeId | — |

## View entities

### AgreementItemAndProductAppl

- Package: org.apache.ofbiz.party.agreement
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:567`
- Member entities: AGI = AgreementItem, AGPA = AgreementProductAppl, AG = Agreement

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| AGI | AGPA | agreementId → —, agreementItemSeqId → — |
| AGI | AG | agreementId → — |

### AgreementItemAndFacilityAppl

- Package: org.apache.ofbiz.party.agreement
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:589`
- Member entities: AGI = AgreementItem, AGFA = AgreementFacilityAppl, AG = Agreement

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| AGI | AGFA | agreementId → —, agreementItemSeqId → — |
| AGI | AG | agreementId → — |

### AgreementItemAndPartyAppl

- Package: org.apache.ofbiz.party.agreement
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:609`
- Member entities: AGI = AgreementItem, AGPA = AgreementPartyApplic, AG = Agreement

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| AGI | AGPA | agreementId → —, agreementItemSeqId → — |
| AGI | AG | agreementId → — |

### AgreementContentAndInfo

- Package: org.apache.ofbiz.accounting.invoice
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:628`
- Member entities: AGC = AgreementContent, CO = Content, DR = DataResource

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| AGC | CO | contentId → — |
| CO | DR | dataResourceId → — |

### CommEventContentDataResource

- Package: org.apache.ofbiz.party.communication
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:689`
- Member entities: CECA = CommEventContentAssoc, CO = Content, DR = DataResource

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| CECA | CO | contentId → — |
| CO | DR | dataResourceId → — |

### CommunicationEventAndSubscr

- Package: org.apache.ofbiz.party.communication
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:810`
- Member entities: SC = SubscriptionCommEvent, SU = Subscription

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| SC | SU | subscriptionId → — |

### CommunicationEventAndProduct

- Package: org.apache.ofbiz.party.communication
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:821`
- Member entities: CP = CommunicationEventProduct, CE = CommunicationEvent

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| CP | CE | communicationEventId → — |

### CommunicationEventAndRole

- Package: org.apache.ofbiz.party.communication
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:830`
- Member entities: CE = CommunicationEvent, CR = CommunicationEventRole

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| CE | CR | communicationEventId → — |

### CommunicationEventSum

- Package: org.apache.ofbiz.party.communication
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:980`
- Member entities: CE = CommunicationEvent

| Entity alias | Related alias | Key maps |
| --- | --- | --- |

### PartyContactDetailByPurpose

- Package: org.apache.ofbiz.party.contact
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1107`
- Member entities: PCM = PartyContactMech, PCMP = PartyContactMechPurpose, CM = ContactMech, PA = PostalAddress, TN = TelecomNumber, STTG = Geo, CTYG = Geo, CTRYG = Geo

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PCM | CM | contactMechId → — |
| PCM | PCMP | partyId → —, contactMechId → — |
| CM | PA | contactMechId → — |
| CM | TN | contactMechId → — |
| PA | STTG | stateProvinceGeoId → geoId |
| PA | CTYG | countyGeoId → geoId |
| PA | CTRYG | countryGeoId → geoId |

### ContactMechDetail

- Package: org.apache.ofbiz.party.contact
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1364`
- Member entities: CM = ContactMech, PA = PostalAddress, TN = TelecomNumber, FA = FtpAddress

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| CM | PA | contactMechId → — |
| CM | TN | contactMechId → — |
| CM | FA | contactMechId → — |

### PartyAndContactMech

- Package: org.apache.ofbiz.party.contact
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1391`
- Member entities: PTY = Party, PCM = PartyContactMech, CMD = ContactMechDetail

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PTY | PCM | partyId → — |
| PCM | CMD | contactMechId → — |

### PartyAndPostalAddress

- Package: org.apache.ofbiz.party.contact
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1426`
- Member entities: PTY = Party, PCM = PartyContactMech, CM = ContactMech, PA = PostalAddress

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PTY | PCM | partyId → — |
| PCM | CM | contactMechId → — |
| CM | PA | contactMechId → — |

### PartyAndTelecomNumber

- Package: org.apache.ofbiz.party.contact
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1468`
- Member entities: PTY = Party, PCM = PartyContactMech, TN = TelecomNumber

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PTY | PCM | partyId → — |
| PCM | TN | contactMechId → — |

### ContactListPartyAndContactMech

- Package: org.apache.ofbiz.party.contact
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1484`
- Member entities: CLP = ContactListParty, CM = ContactMech, PCM = PartyContactMech

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| CLP | CM | preferredContactMechId → contactMechId |
| CLP | PCM | preferredContactMechId → contactMechId |

### PartyAcctgPrefAndGroup

- Package: org.apache.ofbiz.party.party
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1635`
- Member entities: PTYACCPREF = PartyAcctgPreference, PTYGROUP = PartyGroup, PTYROLE = PartyRole

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PTYACCPREF | PTYGROUP | partyId → — |
| PTYACCPREF | PTYROLE | partyId → — |

### PartyAndGroup

- Package: org.apache.ofbiz.party.party
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1655`
- Member entities: PTY = Party, PGRP = PartyGroup

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PTY | PGRP | partyId → — |

### PartyAndPerson

- Package: org.apache.ofbiz.party.party
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1666`
- Member entities: PTY = Party, PERS = Person

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PTY | PERS | partyId → — |

### PartyAndUserLogin

- Package: org.apache.ofbiz.party.party
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1677`
- Member entities: PTY = Party, ULN = UserLogin

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PTY | ULN | partyId → — |

### PartyAndUserLoginAndPerson

- Package: org.apache.ofbiz.party.party
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1700`
- Member entities: PTY = Party, ULN = UserLogin, PER = Person

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PTY | ULN | partyId → — |
| PTY | PER | partyId → — |

### PartyDetailAndWorkEffortAssign

- Package: org.apache.ofbiz.party.party
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1731`
- Member entities: PTY = Party, WEF = WorkEffortPartyAssignment, PER = Person, PGR = PartyGroup, WE = WorkEffort

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WEF | PTY | partyId → — |
| WEF | WE | workEffortId → — |
| PTY | PER | partyId → — |
| PTY | PGR | partyId → — |

### PartyIdentificationAndParty

- Package: org.apache.ofbiz.party.party
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1784`
- Member entities: PIN = PartyIdentification, PIT = PartyIdentificationType, PA = Party

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PIN | PA | partyId → — |
| PIN | PIT | partyIdentificationTypeId → — |

### PartyAndGeoPoint

- Package: org.apache.ofbiz.party.party
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1831`
- Member entities: PTY = Party, PGPT = PartyGeoPoint, GPT = GeoPoint

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PTY | PGPT | partyId → — |
| PGPT | GPT | geoPointId → — |

### UserLoginAndPartyDetails

- Package: org.apache.ofbiz.party.party
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1858`
- Member entities: ULN = UserLogin, PTY = Party, PER = Person, PTYGRP = PartyGroup

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| ULN | PTY | partyId → — |
| ULN | PER | partyId → — |
| ULN | PTYGRP | partyId → — |

### PartyContactWithPurpose

- Package: org.apache.ofbiz.party.party
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:1974`
- Member entities: PCMP = PartyContactMechPurpose, CM = ContactMech, PCM = PartyContactMech, PT = ContactMechPurposeType

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PCMP | CM | contactMechId → — |
| PCMP | PCM | partyId → —, contactMechId → — |
| PCMP | PT | contactMechPurposeTypeId → — |

### PartyContentDetail

- Package: org.apache.ofbiz.content.content
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2043`
- Member entities: PCT = PartyContent, CNT = Content

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PCT | CNT | contentId → — |

### PartyNameContactMechView

- Package: org.apache.ofbiz.party.party
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2168`
- Member entities: PTY = Party, PER = Person, PTYGRP = PartyGroup, PTYPCM = PartyContactMech, PTYCM = ContactMech

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PTY | PER | partyId → — |
| PTY | PTYGRP | partyId → — |
| PTY | PTYPCM | partyId → — |
| PTYPCM | PTYCM | contactMechId → — |

### PartyNameView

- Package: org.apache.ofbiz.party.party
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2226`
- Member entities: PTY = Party, PER = Person, PTYGRP = PartyGroup

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PTY | PER | partyId → — |
| PTY | PTYGRP | partyId → — |

### PartyNoteView

- Package: org.apache.ofbiz.party.party
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2269`
- Member entities: PN = PartyNote, ND = NoteData

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PN | ND | noteId → — |

### PartyRelationshipToFrom

- Package: org.apache.ofbiz.party.party
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2359`
- Member entities: PR1 = PartyRelationship, PR2 = PartyRelationship

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PR1 | PR2 | partyIdTo → partyIdFrom |

### PartyRelationshipAndDetail

- Package: org.apache.ofbiz.party.party
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2394`
- Member entities: PTY = Party, PTYREL = PartyRelationship, PER = Person, PTYGRP = PartyGroup

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PTY | PTYREL | partyId → partyIdTo |
| PTY | PER | partyId → — |
| PTY | PTYGRP | partyId → — |

### PartyRelationshipAndContactMechDetail

- Package: org.apache.ofbiz.party.party
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2426`
- Member entities: PTY = Party, PTYREL = PartyRelationship, PER = Person, PTYGRP = PartyGroup, PCM = PartyContactMech, CM = ContactMech, PA = PostalAddress, TN = TelecomNumber

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PTY | PTYREL | partyId → partyIdTo |
| PTY | PER | partyId → — |
| PTY | PTYGRP | partyId → — |
| PTY | PCM | partyId → — |
| PCM | CM | contactMechId → — |
| PCM | PA | contactMechId → — |
| PCM | TN | contactMechId → — |

### PartyRelationshipAndPartyDetail

- Package: org.apache.ofbiz.party.party
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2503`
- Member entities: TO_PTY = Party, PTYREL = PartyRelationship, PTYRELTP = PartyRelationshipType, TO_PER = Person, TO_PTYGRP = PartyGroup, FROM_PER = Person, FROM_PTYGRP = PartyGroup

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| TO_PTY | PTYREL | partyId → partyIdTo |
| TO_PTY | TO_PER | partyId → — |
| TO_PTY | TO_PTYGRP | partyId → — |
| PTYREL | FROM_PER | partyIdFrom → partyId |
| PTYREL | PTYRELTP | partyRelationshipTypeId → — |
| PTYREL | FROM_PTYGRP | partyIdFrom → partyId |

### PartyRoleAndPartyDetail

- Package: org.apache.ofbiz.party.party
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2581`
- Member entities: PTY = Party, PR = PartyRole, PERSON = Person, PTYGRP = PartyGroup

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PTY | PR | partyId → — |
| PTY | PERSON | partyId → — |
| PTY | PTYGRP | partyId → — |

### PartyRoleDetailAndPartyDetail

- Package: org.apache.ofbiz.party.party
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2608`
- Member entities: PTY = Party, PR = PartyRole, RT = RoleType, PERSON = Person, PTYGRP = PartyGroup

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PTY | PR | partyId → — |
| PTY | PERSON | partyId → — |
| PTY | PTYGRP | partyId → — |
| PR | RT | roleTypeId → — |

### PartyRoleNameDetail

- Package: org.apache.ofbiz.party.party
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2643`
- Member entities: PTY = Party, PR = PartyRole, PERSON = Person, PTYGRP = PartyGroup

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PTY | PR | partyId → — |
| PTY | PERSON | partyId → — |
| PTY | PTYGRP | partyId → — |

### PartyRoleAndContactMechDetail

- Package: org.apache.ofbiz.party.party
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2668`
- Member entities: PTY = Party, PR = PartyRole, RT = RoleType, PERSON = Person, PTYGRP = PartyGroup, PCM = PartyContactMech, CM = ContactMech, PA = PostalAddress, TN = TelecomNumber

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PTY | PR | partyId → — |
| PTY | PERSON | partyId → — |
| PTY | PTYGRP | partyId → — |
| PR | RT | roleTypeId → — |
| PTY | PCM | partyId → — |
| PCM | CM | contactMechId → — |
| PCM | PA | contactMechId → — |
| PCM | TN | contactMechId → — |

### RoleTypeIn3Levels

- Package: org.apache.ofbiz.party.party
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2881`
- Member entities: RT1 = RoleType, RT2 = RoleType, RT3 = RoleType

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| RT1 | RT2 | roleTypeId → parentTypeId |
| RT2 | RT3 | roleTypeId → parentTypeId |

### RoleTypeAndParty

- Package: org.apache.ofbiz.party.party
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2899`
- Member entities: PR = PartyRole, RT = RoleType

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| RT | PR | roleTypeId → — |

### PartyExport

- Package: org.apache.ofbiz.accounting.reports
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:2996`
- Member entities: PRT = Party, GRP = PartyGroup, PER = Person, PR = PartyRelationship, CGRP = PartyGroup, PRL = PartyRole, PCM = PartyContactMech, PCP = PartyContactMechPurpose, CM = ContactMech, TN = TelecomNumber, PA = PostalAddress

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PRT | GRP | partyId → — |
| PRT | PER | partyId → — |
| PRT | PR | partyId → partyIdTo |
| PR | CGRP | partyIdFrom → partyId |
| PRT | PRL | partyId → — |
| PRT | PCM | partyId → — |
| PCM | PA | contactMechId → — |
| PCM | CM | contactMechId → — |
| PCM | TN | contactMechId → — |
| PCM | PCP | contactMechId → —, partyId → — |

### PartyClassificationGroupAndType

- Package: org.apache.ofbiz.party.party
- Source: `applications/datamodel/entitydef/party-entitymodel.xml:3073`
- Member entities: PCG = PartyClassificationGroup, PCT = PartyClassificationType

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| PCG | PCT | partyClassificationTypeId → — |
