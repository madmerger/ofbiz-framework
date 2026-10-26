# Entity inventory

## Content

- Package: org.apache.ofbiz.content.content
- Table: `CONTENT`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:75`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contentId | id | — | Y |
| contentTypeId | id | — | N |
| ownerContentId | id | — | N |
| decoratorContentId | id | — | N |
| instanceOfContentId | id | — | N |
| dataResourceId | id | — | N |
| templateDataResourceId | id | — | N |
| dataSourceId | id | — | N |
| statusId | id | — | N |
| privilegeEnumId | id | — | N |
| serviceName | long-varchar | — | N |
| customMethodId | id | — | N |
| contentName | value | — | N |
| description | description | — | N |
| localeString | very-short | — | N |
| mimeTypeId | id-vlong | — | N |
| characterSetId | id-long | — | N |
| childLeafCount | numeric | — | N |
| childBranchCount | numeric | — | N |
| createdDate | date-time | — | N |
| createdByUserLogin | id-vlong | — | N |
| lastModifiedDate | date-time | — | N |
| lastModifiedByUserLogin | id-vlong | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | ContentType | — | contentTypeId → — | — |
| one | DataResource | — | dataResourceId → — | — |
| one | DataResource | Template | templateDataResourceId → dataResourceId | — |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |
| one | Enumeration (external) | Privilege | privilegeEnumId → enumId | framework/common/entitydef/entitymodel.xml |
| one | CustomMethod (external) | — | customMethodId → — | framework/common/entitydef/entitymodel.xml |
| one-nofk | MimeType | — | mimeTypeId → — | — |
| one | CharacterSet | — | characterSetId → — | — |
| many | ContentTypeAttr | — | contentTypeId → — | — |
| one | UserLogin (external) | CreatedBy | createdByUserLogin → userLoginId | framework/security/entitydef/entitymodel.xml |
| one | UserLogin (external) | LastModifiedBy | lastModifiedByUserLogin → userLoginId | framework/security/entitydef/entitymodel.xml |
| many | ProductFeatureDataResource (external) | — | dataResourceId → — | applications/datamodel/entitydef/product-entitymodel.xml |
| one | DataSource (external) | — | dataSourceId → — | framework/common/entitydef/entitymodel.xml |
| one | Content | Decorator | decoratorContentId → contentId | — |
| one | Content | Owner | ownerContentId → contentId | — |
| one | Content | InstanceOf | instanceOfContentId → contentId | — |
| many | ContentAssocDataResourceViewFrom | — | contentId → contentIdStart | — |
| many | ContentAssocDataResourceViewTo | — | contentId → contentIdStart | — |

## ContentApproval

- Package: org.apache.ofbiz.content.content
- Table: `CONTENT_APPROVAL`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:176`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contentApprovalId | id | — | Y |
| contentId | id | — | N |
| contentRevisionSeqId | id | — | N |
| partyId | id | — | N |
| roleTypeId | id | — | N |
| approvalStatusId | id | — | N |
| approvalDate | date-time | — | N |
| sequenceNum | numeric | — | N |
| comments | comment | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Content | — | contentId → — | — |
| one-nofk | ContentRevision | — | contentId → —, contentRevisionSeqId → — | — |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | RoleType (external) | — | roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | StatusItem (external) | Approval | approvalStatusId → statusId | framework/common/entitydef/entitymodel.xml |

## ContentAssoc

- Package: org.apache.ofbiz.content.content
- Table: `CONTENT_ASSOC`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:206`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contentId | id | — | Y |
| contentIdTo | id | — | Y |
| contentAssocTypeId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| contentAssocPredicateId | id | — | N |
| dataSourceId | id | — | N |
| sequenceNum | numeric | — | N |
| mapKey | name | — | N |
| upperCoordinate | numeric | — | N |
| leftCoordinate | numeric | — | N |
| createdDate | date-time | — | N |
| createdByUserLogin | id-vlong | — | N |
| lastModifiedDate | date-time | — | N |
| lastModifiedByUserLogin | id-vlong | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Content | From | contentId → — | — |
| one | Content | To | contentIdTo → contentId | — |
| one | ContentAssocType | — | contentAssocTypeId → — | — |
| one | UserLogin (external) | CreatedBy | createdByUserLogin → userLoginId | framework/security/entitydef/entitymodel.xml |
| one | UserLogin (external) | LastModifiedBy | lastModifiedByUserLogin → userLoginId | framework/security/entitydef/entitymodel.xml |
| one | ContentAssocPredicate | — | contentAssocPredicateId → — | — |
| one | DataSource (external) | — | dataSourceId → — | framework/common/entitydef/entitymodel.xml |

## ContentAssocPredicate

- Package: org.apache.ofbiz.content.content
- Table: `CONTENT_ASSOC_PREDICATE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:342`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contentAssocPredicateId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## ContentAssocType

- Package: org.apache.ofbiz.content.content
- Table: `CONTENT_ASSOC_TYPE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:350`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contentAssocTypeId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## ContentAttribute

- Package: org.apache.ofbiz.content.content
- Table: `CONTENT_ATTRIBUTE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:386`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contentId | id | — | Y |
| attrName | id-long | — | Y |
| attrValue | value | — | N |
| attrDescription | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Content | — | contentId → — | — |
| many | ContentTypeAttr | — | attrName → — | — |

## ContentMetaData

- Package: org.apache.ofbiz.content.content
- Table: `CONTENT_META_DATA`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:440`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contentId | id | — | Y |
| metaDataPredicateId | id | — | Y |
| metaDataValue | value | — | N |
| dataSourceId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Content | — | contentId → — | — |
| one | MetaDataPredicate | — | metaDataPredicateId → — | — |
| one | DataSource (external) | — | dataSourceId → — | framework/common/entitydef/entitymodel.xml |

## ContentOperation

- Package: org.apache.ofbiz.content.content
- Table: `CONTENT_OPERATION`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:459`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contentOperationId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## ContentPurpose

- Package: org.apache.ofbiz.content.content
- Table: `CONTENT_PURPOSE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:467`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contentId | id | — | Y |
| contentPurposeTypeId | id | — | Y |
| sequenceNum | numeric | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Content | — | contentId → — | — |
| one | ContentPurposeType | — | contentPurposeTypeId → — | — |

## ContentPurposeOperation

- Package: org.apache.ofbiz.content.content
- Table: `CONTENT_PURPOSE_OPERATION`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:482`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contentPurposeTypeId | id | — | Y |
| contentOperationId | id | — | Y |
| roleTypeId | id | — | Y |
| statusId | id | — | Y |
| privilegeEnumId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | ContentPurposeType | — | contentPurposeTypeId → — | — |
| one | ContentOperation | — | contentOperationId → — | — |
| one | RoleType (external) | — | roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |
| one | Enumeration (external) | — | privilegeEnumId → enumId | framework/common/entitydef/entitymodel.xml |

## ContentPurposeType

- Package: org.apache.ofbiz.content.content
- Table: `CONTENT_PURPOSE_TYPE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:511`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contentPurposeTypeId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## ContentRevision

- Package: org.apache.ofbiz.content.content
- Table: `CONTENT_REVISION`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:519`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contentId | id | — | Y |
| contentRevisionSeqId | id | — | Y |
| committedByPartyId | id | — | N |
| comments | comment | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Content | — | contentId → — | — |
| one | Party (external) | CommittedBy | committedByPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |

## ContentRevisionItem

- Package: org.apache.ofbiz.content.content
- Table: `CONTENT_REVISION_ITEM`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:535`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contentId | id | — | Y |
| contentRevisionSeqId | id | — | Y |
| itemContentId | id | — | Y |
| oldDataResourceId | id | — | N |
| newDataResourceId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | ContentRevision | — | contentId → —, contentRevisionSeqId → — | — |
| one | DataResource | Old | oldDataResourceId → dataResourceId | — |
| one | DataResource | New | newDataResourceId → dataResourceId | — |

## ContentRole

- Package: org.apache.ofbiz.content.content
- Table: `CONTENT_ROLE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:557`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contentId | id | — | Y |
| partyId | id | — | Y |
| roleTypeId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Content | — | contentId → — | — |
| one-nofk | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PartyRole (external) | — | partyId → —, roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |

## ContentType

- Package: org.apache.ofbiz.content.content
- Table: `CONTENT_TYPE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:580`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contentTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | ContentType | Parent | parentTypeId → contentTypeId | — |

## ContentTypeAttr

- Package: org.apache.ofbiz.content.content
- Table: `CONTENT_TYPE_ATTR`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:593`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contentTypeId | id | — | Y |
| attrName | id-long | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | ContentType | — | contentTypeId → — | — |
| many | ContentAttribute | — | attrName → — | — |
| many | Content | — | contentTypeId → — | — |

## AudioDataResource

- Package: org.apache.ofbiz.content.data
- Table: `AUDIO_DATA_RESOURCE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:654`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| dataResourceId | id | — | Y |
| audioData | byte-array | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | DataResource | — | dataResourceId → — | — |

## CharacterSet

- Package: org.apache.ofbiz.content.data
- Table: `CHARACTER_SET`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:664`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| characterSetId | id-long | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## DataCategory

- Package: org.apache.ofbiz.content.data
- Table: `DATA_CATEGORY`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:672`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| dataCategoryId | id | — | Y |
| parentCategoryId | id | — | N |
| categoryName | name | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | DataCategory | Parent | parentCategoryId → dataCategoryId | — |
| many | DataCategory | Sibling | parentCategoryId → — | — |

## DataResource

- Package: org.apache.ofbiz.content.data
- Table: `DATA_RESOURCE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:687`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| dataResourceId | id | — | Y |
| dataResourceTypeId | id | — | N |
| dataTemplateTypeId | id | — | N |
| dataCategoryId | id | — | N |
| dataSourceId | id | — | N |
| statusId | id | — | N |
| dataResourceName | value | — | N |
| localeString | very-short | — | N |
| mimeTypeId | id-vlong | — | N |
| characterSetId | id-long | — | N |
| objectInfo | long-varchar | — | N |
| surveyId | id | — | N |
| surveyResponseId | id | — | N |
| relatedDetailId | id | — | N |
| isPublic | indicator | — | N |
| createdDate | date-time | — | N |
| createdByUserLogin | id-vlong | — | N |
| lastModifiedDate | date-time | — | N |
| lastModifiedByUserLogin | id-vlong | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |
| one | DataResourceType | — | dataResourceTypeId → — | — |
| one | DataTemplateType | — | dataTemplateTypeId → — | — |
| one | DataCategory | — | dataCategoryId → — | — |
| one | DataSource (external) | — | dataSourceId → — | framework/common/entitydef/entitymodel.xml |
| one-nofk | MimeType | — | mimeTypeId → — | — |
| one | CharacterSet | — | characterSetId → — | — |
| many | DataResourceTypeAttr | — | dataResourceTypeId → — | — |
| one | UserLogin (external) | CreatedBy | createdByUserLogin → userLoginId | framework/security/entitydef/entitymodel.xml |
| one | UserLogin (external) | LastModifiedBy | lastModifiedByUserLogin → userLoginId | framework/security/entitydef/entitymodel.xml |
| one | Survey | — | surveyId → — | — |
| one | SurveyResponse | — | surveyResponseId → — | — |

## DataResourceAttribute

- Package: org.apache.ofbiz.content.data
- Table: `DATA_RESOURCE_ATTRIBUTE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:748`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| dataResourceId | id | — | Y |
| attrName | id-long | — | Y |
| attrValue | value | — | N |
| attrDescription | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | DataResource | — | dataResourceId → — | — |
| many | DataResourceTypeAttr | — | attrName → — | — |

## DataResourceMetaData

- Package: org.apache.ofbiz.content.data
- Table: `DATA_RESOURCE_META_DATA`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:814`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| dataResourceId | id | — | Y |
| metaDataPredicateId | id | — | Y |
| metaDataValue | value | — | N |
| dataSourceId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | DataResource | — | dataResourceId → — | — |
| one | MetaDataPredicate | — | metaDataPredicateId → — | — |
| one | DataSource (external) | — | dataSourceId → — | framework/common/entitydef/entitymodel.xml |

## DataResourcePurpose

- Package: org.apache.ofbiz.content.data
- Table: `DATA_RESOURCE_PURPOSE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:833`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| dataResourceId | id | — | Y |
| contentPurposeTypeId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | DataResource | — | dataResourceId → — | — |
| one | ContentPurposeType | — | contentPurposeTypeId → — | — |

## DataResourceRole

- Package: org.apache.ofbiz.content.data
- Table: `DATA_RESOURCE_ROLE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:847`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| dataResourceId | id | — | Y |
| partyId | id | — | Y |
| roleTypeId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | DataResource | — | dataResourceId → — | — |
| one-nofk | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PartyRole (external) | — | partyId → —, roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |

## DataResourceType

- Package: org.apache.ofbiz.content.data
- Table: `DATA_RESOURCE_TYPE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:870`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| dataResourceTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | DataResourceType | Parent | parentTypeId → dataResourceTypeId | — |

## DataResourceTypeAttr

- Package: org.apache.ofbiz.content.data
- Table: `DATA_RESOURCE_TYPE_ATTR`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:883`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| dataResourceTypeId | id | — | Y |
| attrName | id-long | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | DataResourceType | — | dataResourceTypeId → — | — |
| many | DataResourceAttribute | — | attrName → — | — |
| many | DataResource | — | dataResourceTypeId → — | — |

## DataTemplateType

- Package: org.apache.ofbiz.content.data
- Table: `DATA_TEMPLATE_TYPE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:901`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| dataTemplateTypeId | id | — | Y |
| description | description | — | N |
| extension | short-varchar | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## ElectronicText

- Package: org.apache.ofbiz.content.data
- Table: `ELECTRONIC_TEXT`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:910`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| dataResourceId | id | — | Y |
| textData | very-long | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | DataResource | — | dataResourceId → — | — |

## FileExtension

- Package: org.apache.ofbiz.content.data
- Table: `FILE_EXTENSION`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:920`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| fileExtensionId | id-long | — | Y |
| mimeTypeId | id-vlong | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | MimeType | — | mimeTypeId → — | — |

## ImageDataResource

- Package: org.apache.ofbiz.content.data
- Table: `IMAGE_DATA_RESOURCE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:930`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| dataResourceId | id | — | Y |
| imageData | byte-array | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | DataResource | — | dataResourceId → — | — |

## MetaDataPredicate

- Package: org.apache.ofbiz.content.data
- Table: `META_DATA_PREDICATE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:940`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| metaDataPredicateId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## MimeType

- Package: org.apache.ofbiz.content.data
- Table: `MIME_TYPE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:948`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| mimeTypeId | id-vlong | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## MimeTypeHtmlTemplate

- Package: org.apache.ofbiz.content.data
- Table: `MIME_TYPE_HTML_TEMPLATE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:956`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| mimeTypeId | id-vlong | — | Y |
| templateLocation | long-varchar | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | MimeType | — | mimeTypeId → — | — |

## OtherDataResource

- Package: org.apache.ofbiz.content.data
- Table: `OTHER_DATA_RESOURCE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:967`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| dataResourceId | id | — | Y |
| dataResourceContent | byte-array | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | DataResource | — | dataResourceId → — | — |

## VideoDataResource

- Package: org.apache.ofbiz.content.data
- Table: `VIDEO_DATA_RESOURCE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:977`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| dataResourceId | id | — | Y |
| videoData | byte-array | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | DataResource | — | dataResourceId → — | — |

## Document

- Package: org.apache.ofbiz.content.document
- Table: `DOCUMENT`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:992`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| documentId | id | — | Y |
| documentTypeId | id | — | N |
| dateCreated | date-time | — | N |
| comments | comment | — | N |
| documentLocation | long-varchar | — | N |
| documentText | long-varchar | — | N |
| imageData | object | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | DocumentType | — | documentTypeId → — | — |
| many | DocumentTypeAttr | — | documentTypeId → — | — |

## DocumentAttribute

- Package: org.apache.ofbiz.content.document
- Table: `DOCUMENT_ATTRIBUTE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1010`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| documentId | id | — | Y |
| attrName | id-long | — | Y |
| attrValue | value | — | N |
| attrDescription | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Document | — | documentId → — | — |
| many | DocumentTypeAttr | — | attrName → — | — |

## DocumentType

- Package: org.apache.ofbiz.content.document
- Table: `DOCUMENT_TYPE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1026`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| documentTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | DocumentType | Parent | parentTypeId → documentTypeId | — |

## DocumentTypeAttr

- Package: org.apache.ofbiz.content.document
- Table: `DOCUMENT_TYPE_ATTR`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1039`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| documentTypeId | id | — | Y |
| attrName | id-long | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | DocumentType | — | documentTypeId → — | — |
| many | DocumentAttribute | — | attrName → — | — |
| many | Document | — | documentTypeId → — | — |

## WebPreferenceType

- Package: org.apache.ofbiz.content.preference
- Table: `WEB_PREFERENCE_TYPE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1062`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| webPreferenceTypeId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## WebUserPreference

- Package: org.apache.ofbiz.content.preference
- Table: `WEB_USER_PREFERENCE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1069`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| userLoginId | id-vlong | — | Y |
| partyId | id | — | Y |
| visitId | id | — | Y |
| webPreferenceTypeId | id | — | Y |
| webPreferenceValue | long-varchar | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WebPreferenceType | — | webPreferenceTypeId → — | — |
| one | UserLogin (external) | — | userLoginId → — | framework/security/entitydef/entitymodel.xml |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |

## Survey

- Package: org.apache.ofbiz.content.survey
- Table: `SURVEY`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1096`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| surveyId | id | — | Y |
| surveyName | name | — | N |
| description | description | — | N |
| comments | comment | — | N |
| submitCaption | short-varchar | — | N |
| responseService | long-varchar | — | N |
| isAnonymous | indicator | — | N |
| allowMultiple | indicator | — | N |
| allowUpdate | indicator | — | N |
| acroFormContentId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## SurveyApplType

- Package: org.apache.ofbiz.content.survey
- Table: `SURVEY_APPL_TYPE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1111`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| surveyApplTypeId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## SurveyMultiResp

- Package: org.apache.ofbiz.content.survey
- Table: `SURVEY_MULTI_RESP`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1119`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| surveyId | id | — | Y |
| surveyMultiRespId | id | — | Y |
| multiRespTitle | name | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Survey | — | surveyId → — | — |

## SurveyMultiRespColumn

- Package: org.apache.ofbiz.content.survey
- Table: `SURVEY_MULTI_RESP_COLUMN`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1131`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| surveyId | id | — | Y |
| surveyMultiRespId | id | — | Y |
| surveyMultiRespColId | id | — | Y |
| columnTitle | name | — | N |
| sequenceNum | numeric | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | SurveyMultiResp | — | surveyId → —, surveyMultiRespId → — | — |

## SurveyPage

- Package: org.apache.ofbiz.content.survey
- Table: `SURVEY_PAGE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1147`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| surveyId | id | — | Y |
| surveyPageSeqId | id | — | Y |
| pageName | name | — | N |
| sequenceNum | numeric | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Survey | — | surveyId → — | — |

## SurveyQuestion

- Package: org.apache.ofbiz.content.survey
- Table: `SURVEY_QUESTION`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1160`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| surveyQuestionId | id | — | Y |
| surveyQuestionCategoryId | id | — | N |
| surveyQuestionTypeId | id | — | N |
| description | description | — | N |
| question | very-long | — | N |
| hint | very-long | — | N |
| enumTypeId | id | — | N |
| geoId | id | — | N |
| formatString | value | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | SurveyQuestionType | — | surveyQuestionTypeId → — | — |
| one | SurveyQuestionCategory | — | surveyQuestionCategoryId → — | — |
| one | Geo (external) | — | geoId → — | framework/common/entitydef/entitymodel.xml |
| many | Enumeration (external) | — | enumTypeId → — | framework/common/entitydef/entitymodel.xml |

## SurveyQuestionAppl

- Package: org.apache.ofbiz.content.survey
- Table: `SURVEY_QUESTION_APPL`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1229`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| surveyId | id | — | Y |
| surveyQuestionId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| surveyPageSeqId | id | — | N |
| surveyMultiRespId | id | — | N |
| surveyMultiRespColId | id | — | N |
| requiredField | indicator | — | N |
| sequenceNum | numeric | — | N |
| externalFieldRef | long-varchar | — | N |
| withSurveyQuestionId | id | — | N |
| withSurveyOptionSeqId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Survey | — | surveyId → — | — |
| one | SurveyQuestion | — | surveyQuestionId → — | — |
| one | SurveyQuestionOption | With | withSurveyQuestionId → surveyQuestionId, withSurveyOptionSeqId → surveyOptionSeqId | — |
| one-nofk | SurveyPage | — | surveyId → —, surveyPageSeqId → — | — |
| one-nofk | SurveyMultiResp | — | surveyId → —, surveyMultiRespId → — | — |
| one-nofk | SurveyMultiRespColumn | — | surveyId → —, surveyMultiRespId → —, surveyMultiRespColId → — | — |

## SurveyQuestionCategory

- Package: org.apache.ofbiz.content.survey
- Table: `SURVEY_QUESTION_CATEGORY`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1273`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| surveyQuestionCategoryId | id | — | Y |
| parentCategoryId | id | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | SurveyQuestionCategory | Parent | parentCategoryId → surveyQuestionCategoryId | — |

## SurveyQuestionOption

- Package: org.apache.ofbiz.content.survey
- Table: `SURVEY_QUESTION_OPTION`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1284`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| surveyQuestionId | id | — | Y |
| surveyOptionSeqId | id | — | Y |
| description | description | — | N |
| sequenceNum | numeric | — | N |
| amountBase | currency-amount | — | N |
| amountBaseUomId | id | — | N |
| weightFactor | floating-point | — | N |
| duration | numeric | — | N |
| durationUomId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | SurveyQuestion | — | surveyQuestionId → — | — |

## SurveyQuestionType

- Package: org.apache.ofbiz.content.survey
- Table: `SURVEY_QUESTION_TYPE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1302`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| surveyQuestionTypeId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## SurveyResponse

- Package: org.apache.ofbiz.content.survey
- Table: `SURVEY_RESPONSE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1310`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| surveyResponseId | id | — | Y |
| surveyId | id | — | N |
| partyId | id | — | N |
| responseDate | date-time | — | N |
| lastModifiedDate | date-time | — | N |
| referenceId | id-vlong | — | N |
| generalFeedback | very-long | — | N |
| orderId | id | — | N |
| orderItemSeqId | id | — | N |
| statusId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one-nofk | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | OrderItem (external) | — | orderId → —, orderItemSeqId → — | applications/datamodel/entitydef/order-entitymodel.xml |
| one-nofk | OrderHeader (external) | — | orderId → — | applications/datamodel/entitydef/order-entitymodel.xml |
| one | Survey | — | surveyId → — | — |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |

## SurveyResponseAnswer

- Package: org.apache.ofbiz.content.survey
- Table: `SURVEY_RESPONSE_ANSWER`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1373`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| surveyResponseId | id | — | Y |
| surveyQuestionId | id | — | Y |
| surveyMultiRespColId | id | — | Y |
| surveyMultiRespId | id | — | N |
| booleanResponse | indicator | — | N |
| currencyResponse | currency-amount | — | N |
| floatResponse | floating-point | — | N |
| numericResponse | numeric | — | N |
| textResponse | very-long | — | N |
| surveyOptionSeqId | id | — | N |
| contentId | id | — | N |
| answeredDate | date-time | — | N |
| amountBase | currency-amount | — | N |
| amountBaseUomId | id | — | N |
| weightFactor | floating-point | — | N |
| duration | numeric | — | N |
| durationUomId | id | — | N |
| sequenceNum | numeric | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | SurveyResponse | — | surveyResponseId → — | — |
| one | SurveyQuestion | — | surveyQuestionId → — | — |
| one | SurveyQuestionOption | — | surveyQuestionId → —, surveyOptionSeqId → — | — |
| one | Content | — | contentId → — | — |

## SurveyTrigger

- Package: org.apache.ofbiz.content.survey
- Table: `SURVEY_TRIGGER`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1411`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| surveyId | id | — | Y |
| surveyApplTypeId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Survey | — | surveyId → — | — |
| one | SurveyApplType | — | surveyApplTypeId → — | — |

## WebSiteContent

- Package: org.apache.ofbiz.content.website
- Table: `WEB_SITE_CONTENT`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1439`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| webSiteId | id | — | Y |
| contentId | id | — | Y |
| webSiteContentTypeId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WebSite (external) | — | webSiteId → — | framework/webapp/entitydef/entitymodel.xml |
| one | Content | — | contentId → — | — |
| one | WebSiteContentType | — | webSiteContentTypeId → — | — |

## WebSiteContentType

- Package: org.apache.ofbiz.content.website
- Table: `WEB_SITE_CONTENT_TYPE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1474`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| webSiteContentTypeId | id | — | Y |
| description | description | — | N |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WebSiteContentType | Parent | parentTypeId → webSiteContentTypeId | — |

## WebSitePathAlias

- Package: org.apache.ofbiz.content.website
- Table: `WEB_SITE_PATH_ALIAS`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1487`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| webSiteId | id | — | Y |
| pathAlias | id-vlong | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| aliasTo | long-varchar | — | N |
| contentId | id | — | N |
| mapKey | name | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WebSite (external) | — | webSiteId → — | framework/webapp/entitydef/entitymodel.xml |
| one | Content | — | contentId → — | — |

## WebSitePublishPoint

- Package: org.apache.ofbiz.content.website
- Table: `WEB_SITE_PUBLISH_POINT`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1507`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contentId | id | — | Y |
| templateTitle | long-varchar | — | N |
| styleSheetFile | long-varchar | — | N |
| logo | long-varchar | — | N |
| medallionLogo | long-varchar | — | N |
| lineLogo | long-varchar | — | N |
| leftBarId | id | — | N |
| rightBarId | id | — | N |
| contentDept | id | — | N |
| aboutContentId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Content | — | contentId → — | — |

## WebSiteRole

- Package: org.apache.ofbiz.party.party
- Table: `WEB_SITE_ROLE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1525`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| roleTypeId | id | — | Y |
| webSiteId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| sequenceNum | numeric | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one-nofk | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | RoleType (external) | — | roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | Person (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | PartyGroup (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PartyRole (external) | — | partyId → —, roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | WebSite (external) | — | webSiteId → — | framework/webapp/entitydef/entitymodel.xml |

## ContentKeyword

- Package: org.apache.ofbiz.content.content
- Table: `CONTENT_KEYWORD`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1565`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contentId | id | — | Y |
| keyword | short-varchar | — | Y |
| relevancyWeight | numeric | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Content | — | contentId → — | — |

## ContentSearchConstraint

- Package: org.apache.ofbiz.content.content
- Table: `CONTENT_SEARCH_CONSTRAINT`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1578`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contentSearchResultId | id | — | Y |
| constraintSeqId | id | — | Y |
| constraintName | long-varchar | — | N |
| infoString | long-varchar | — | N |
| includeSubCategories | indicator | — | N |
| isAnd | indicator | — | N |
| anyPrefix | indicator | — | N |
| anySuffix | indicator | — | N |
| removeStems | indicator | — | N |
| lowValue | short-varchar | — | N |
| highValue | short-varchar | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | ContentSearchResult | — | contentSearchResultId → — | — |

## ContentSearchResult

- Package: org.apache.ofbiz.content.content
- Table: `CONTENT_SEARCH_RESULT`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1596`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| contentSearchResultId | id | — | Y |
| visitId | id | — | N |
| orderByName | long-varchar | — | N |
| isAscending | indicator | — | N |
| numResults | numeric | — | N |
| secondsTotal | floating-point | — | N |
| searchDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## WebAnalyticsConfig

- Package: org.apache.ofbiz.content.website
- Table: `WEB_ANALYTICS_CONFIG`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1610`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| webSiteId | id | — | Y |
| webAnalyticsTypeId | id | — | Y |
| webAnalyticsCode | very-long | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one-nofk | WebAnalyticsType | — | webAnalyticsTypeId → — | — |
| one-nofk | WebSite (external) | — | webSiteId → — | framework/webapp/entitydef/entitymodel.xml |

## WebAnalyticsType

- Package: org.apache.ofbiz.content.website
- Table: `WEB_ANALYTICS_TYPE`
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1626`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| webAnalyticsTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | WebAnalyticsType | Parent | parentTypeId → webAnalyticsTypeId | — |

## View entities

### ContentAssocOptViewFrom

- Package: org.apache.ofbiz.content.content
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:42`
- Member entities: CO = Content, CA = ContentAssoc

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| CA | CO | contentIdTo → contentId |

### ContentAndRole

- Package: org.apache.ofbiz.content.content
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:161`
- Member entities: CNT = Content, CRLE = ContentRole

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| CNT | CRLE | contentId → — |

### ContentAssocDataResourceViewFrom

- Package: org.apache.ofbiz.content.content
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:255`
- Member entities: CO = Content, CA = ContentAssoc, DR = DataResource

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| CA | CO | contentId → contentId |
| CO | DR | dataResourceId → dataResourceId |

### ContentAssocDataResourceViewTo

- Package: org.apache.ofbiz.content.content
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:298`
- Member entities: CO = Content, CA = ContentAssoc, DR = DataResource

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| CA | CO | contentIdTo → contentId |
| CO | DR | dataResourceId → dataResourceId |

### ContentAssocViewFrom

- Package: org.apache.ofbiz.content.content
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:358`
- Member entities: CO = Content, CA = ContentAssoc

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| CA | CO | contentId → — |

### ContentAssocViewTo

- Package: org.apache.ofbiz.content.content
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:372`
- Member entities: CO = Content, CA = ContentAssoc

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| CA | CO | contentIdTo → contentId |

### ContentDataResourceView

- Package: org.apache.ofbiz.content.content
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:402`
- Member entities: CO = Content, DR = DataResource

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| CO | DR | dataResourceId → — |

### SubContentDataResourceView

- Package: org.apache.ofbiz.content.content
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:611`
- Member entities: CO = Content, DR = DataResource

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| CO | DR | dataResourceId → — |

### DataResourceContentView

- Package: org.apache.ofbiz.content.content
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:764`
- Member entities: DR = DataResource, CO = Content

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| DR | CO | dataResourceId → — |

### SurveyQuestionAndAppl

- Package: org.apache.ofbiz.content.survey
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1187`
- Member entities: SQ = SurveyQuestion, SA = SurveyQuestionAppl

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| SQ | SA | surveyQuestionId → — |

### SurveyResponseAndAnswer

- Package: org.apache.ofbiz.content.survey
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1341`
- Member entities: SR = SurveyResponse, SRA = SurveyResponseAnswer

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| SR | SRA | surveyResponseId → — |

### WebSiteAndContent

- Package: org.apache.ofbiz.content.website
- Source: `applications/datamodel/entitydef/content-entitymodel.xml:1461`
- Member entities: WS = WebSite, WSCT = WebSiteContent

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WS | WSCT | webSiteId → — |
