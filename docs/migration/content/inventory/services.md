# Service inventory

## addWebSiteRole

- Definition: `applications/content/servicedef/services_website.xml:112`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WebSiteRole; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: [{"service": "webSiteRoleInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | — | — | true |

## assocContent

- Definition: `applications/content/servicedef/services_content.xml:256`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy; invoke: assocContent
- Default entity: ContentAssoc; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentAssocTypeId | String | IN | false |
| contentIdFrom | String | IN | true |
| contentIdTo | String | IN | true |
| contentPurposeList | List | IN | true |
| entityOperation | String | IN | true |
| fromDate | String | IN | true |
| roleTypeList | List | INOUT | true |
| statusId | String | IN | true |
| targetOperationList | List | IN | true |
| thruDate | String | IN | true |
| userLogin | org.apache.ofbiz.entity.GenericValue | IN | true |

## attachUploadToDataResource

- Definition: `applications/content/servicedef/services_content.xml:87`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/data/DataServicesScript.groovy; invoke: attachUploadToDataResource
- Default entity: —; auth: —
- Use transaction: —; timeout: 300
- Permission service: —; main action: —
- Implements: [{"service": "uploadFileInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceId | String | INOUT | false |
| dataResourceTypeId | String | IN | true |
| mimeTypeId | String | INOUT | true |
| rootDir | String | IN | true |

## autoCreateWebSiteContent

- Definition: `applications/content/servicedef/services_website.xml:136`
- Engine: simple; location: component://content/minilang/website/WebSiteServices.xml; invoke: quickCreateWebSiteContent
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| webSiteContentTypeId | List | IN | false |
| webSiteId | String | IN | false |

## buildPdfFromSurveyResponse

- Definition: `applications/content/servicedef/services_survey.xml:335`
- Engine: java; location: org.apache.ofbiz.content.survey.PdfSurveyServices; invoke: buildPdfFromSurveyResponse
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| outByteBuffer | java.nio.ByteBuffer | OUT | false |
| surveyResponseId | String | IN | false |

## buildSurveyFromPdf

- Definition: `applications/content/servicedef/services_survey.xml:280`
- Engine: java; location: org.apache.ofbiz.content.survey.PdfSurveyServices; invoke: buildSurveyFromPdf
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | String | IN | true |
| inputByteBuffer | java.nio.ByteBuffer | IN | true |
| surveyId | String | INOUT | true |
| surveyName | String | IN | true |

## buildSurveyQuestionsAndAnswers

- Definition: `applications/content/servicedef/services_survey.xml:345`
- Engine: java; location: org.apache.ofbiz.content.survey.PdfSurveyServices; invoke: buildSurveyQuestionsAndAnswers
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| questionsAndAnswers | List | OUT | false |
| surveyResponseId | String | IN | false |

## buildSurveyResponseFromPdf

- Definition: `applications/content/servicedef/services_survey.xml:290`
- Engine: java; location: org.apache.ofbiz.content.survey.PdfSurveyServices; invoke: buildSurveyResponseFromPdf
- Default entity: —; auth: true
- Use transaction: —; timeout: 7200
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | String | IN | true |
| inputByteBuffer | java.nio.ByteBuffer | IN | true |
| partyId | String | IN | true |
| surveyId | String | IN | true |
| surveyResponseId | String | INOUT | true |

## changeLeafToNode

- Definition: `applications/content/servicedef/services.xml:532`
- Engine: java; location: org.apache.ofbiz.content.ContentManagementServices; invoke: changeLeafToNode
- Default entity: —; auth: true
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | String | IN | false |
| userLogin | org.apache.ofbiz.entity.GenericValue | IN | true |

## checkAssocPermission

- Definition: `applications/content/servicedef/services_content.xml:233`
- Engine: java; location: org.apache.ofbiz.content.content.ContentPermissionServices; invoke: checkAssocPermission
- Default entity: —; auth: true
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentAssocPredicateId | String | IN | true |
| contentAssocTypeId | String | IN | true |
| contentIdFrom | String | IN | true |
| contentIdTo | String | IN | true |
| contentPurposeList | List | IN | true |
| displayFailCond | Boolean | IN | true |
| entityOperation | String | IN | true |
| fromDate | String | IN | true |
| permissionRecorder | org.apache.ofbiz.content.content.PermissionRecorder | OUT | true |
| permissionRecorderTo | org.apache.ofbiz.content.content.PermissionRecorder | OUT | true |
| permissionStatus | String | OUT | true |
| privilegeEnumId | String | IN | true |
| roleTypeList | List | INOUT | true |
| statusId | String | IN | true |
| targetOperationList | List | IN | true |
| thruDate | String | IN | true |
| userLogin | GenericValue | IN | true |

## checkContentPermission

- Definition: `applications/content/servicedef/services_content.xml:183`
- Engine: java; location: org.apache.ofbiz.content.content.ContentPermissionServices; invoke: checkContentPermission
- Default entity: —; auth: false
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentPurposeList | List | IN | true |
| contentPurposeString | String | IN | true |
| currentContent | org.apache.ofbiz.entity.GenericValue | IN | true |
| displayFailCond | Boolean | IN | true |
| displayPassCond | Boolean | IN | true |
| entityOperation | String | IN | true |
| fromDate | String | IN | true |
| partyId | String | IN | true |
| permissionRecorder | org.apache.ofbiz.content.content.PermissionRecorder | OUT | true |
| permissionStatus | String | OUT | true |
| privilegeEnumId | String | IN | true |
| quickCheckContentId | String | IN | true |
| roleTypeList | List | INOUT | true |
| roleTypeString | String | IN | true |
| statusId | String | IN | true |
| targetOperationList | List | IN | true |
| targetOperationString | String | IN | true |
| thruDate | String | IN | true |
| userLoginId | String | IN | true |

## checkOwnership

- Definition: `applications/content/servicedef/services.xml:888`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/permission/ContentPermissionServices.groovy; invoke: checkOwnership
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| checkId | String | IN | true |
| roleEntity | String | IN | true |
| roleEntityField | String | IN | true |

## clearAssociatedRenderCache

- Definition: `applications/content/servicedef/services_data.xml:313`
- Engine: java; location: org.apache.ofbiz.content.data.DataServices; invoke: clearAssociatedRenderCache
- Default entity: DataResource; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceId | id | IN | — |

## clearContentAssocDataResourceViewCache

- Definition: `applications/content/servicedef/services.xml:570`
- Engine: java; location: org.apache.ofbiz.content.ContentManagementServices; invoke: clearContentAssocDataResourceViewCache
- Default entity: —; auth: true
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## clearContentAssocViewCache

- Definition: `applications/content/servicedef/services.xml:565`
- Engine: java; location: org.apache.ofbiz.content.ContentManagementServices; invoke: clearContentAssocViewCache
- Default entity: —; auth: true
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## contentManagerPermission

- Definition: `applications/content/servicedef/services.xml:871`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/permission/ContentPermissionServices.groovy; invoke: contentManagerPermission
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## contentManagerRolePermission

- Definition: `applications/content/servicedef/services.xml:867`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/permission/ContentPermissionServices.groovy; invoke: contentManagerRolePermission
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## copyContentAndElectronicTextandAssoc

- Definition: `applications/content/servicedef/services_content.xml:123`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy; invoke: copyContentAndElectronicTextandAssoc
- Default entity: Content; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | INOUT | false |

## createAnonFile

- Definition: `applications/content/servicedef/services_data.xml:287`
- Engine: java; location: org.apache.ofbiz.content.data.DataServices; invoke: createFileNoPerm
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "createFile"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceName | — | — | — |
| objectInfo | — | — | — |

## createArticleContent

- Definition: `applications/content/servicedef/services.xml:379`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy; invoke: createArticleContent
- Default entity: —; auth: true
- Use transaction: —; timeout: 300
- Permission service: —; main action: —
- Implements: [{"service": "createContentFromUploadedFile", "optional": "true"}, {"service": "createTextContent", "optional": "true"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentIdFrom | String | IN | false |
| pubPtContentId | String | IN | false |
| summaryData | String | IN | true |
| threadContentId | String | IN | true |

## createAudioDataResource

- Definition: `applications/content/servicedef/services_data.xml:200`
- Engine: entity-auto; location: —; invoke: create
- Default entity: AudioDataResource; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| audioData | byte-array | IN | true |
| dataResourceId | id | INOUT | true |

## createBlogEntry

- Definition: `applications/content/servicedef/services.xml:777`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/BlogServices.groovy; invoke: createBlogEntry
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "uploadFileInterface", "optional": "true"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| articleData | String | IN | true |
| blogContentId | String | INOUT | false |
| contentId | String | OUT | false |
| contentName | String | IN | true |
| description | String | IN | true |
| statusId | String | IN | true |
| summaryData | String | IN | true |
| templateDataResourceId | String | IN | true |

## createCharacterSet

- Definition: `applications/content/servicedef/services_contenttypes.xml:116`
- Engine: entity-auto; location: —; invoke: create
- Default entity: CharacterSet; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| characterSetId | id-long | INOUT | false |
| description | description | IN | true |

## createCommContentAssocType

- Definition: `applications/content/servicedef/services_commevent.xml:105`
- Engine: entity-auto; location: —; invoke: create
- Default entity: CommContentAssocType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| commContentAssocTypeId | id | INOUT | true |
| description | description | IN | true |

## createCommContentDataResource

- Definition: `applications/content/servicedef/services_commevent.xml:38`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy; invoke: createCommContentDataResource
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "persistContentAndAssoc"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | String | IN | false |
| contentId | String | IN | false |
| fromDate | java.sql.Timestamp | OUT | false |

## createCommEventContentAssoc

- Definition: `applications/content/servicedef/services_commevent.xml:72`
- Engine: entity-auto; location: —; invoke: create
- Default entity: CommEventContentAssoc; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| commContentAssocTypeId | id | IN | true |
| communicationEventId | id | INOUT | false |
| contentId | id | INOUT | false |
| fromDate | date-time | INOUT | true |
| sequenceNum | numeric | IN | true |
| thruDate | date-time | IN | true |

## createContent

- Definition: `applications/content/servicedef/services_content.xml:26`
- Engine: entity-auto; location: —; invoke: create
- Default entity: Content; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| characterSetId | id-long | INOUT | true |
| childBranchCount | numeric | INOUT | true |
| childLeafCount | numeric | INOUT | true |
| contentAssocTypeId | String | INOUT | true |
| contentId | id | INOUT | true |
| contentIdFrom | String | INOUT | true |
| contentIdTo | String | INOUT | true |
| contentName | value | INOUT | true |
| contentPurposeList | List | IN | true |
| contentPurposeString | String | IN | true |
| contentTypeId | id | INOUT | true |
| createdByUserLogin | id-vlong | INOUT | true |
| createdDate | date-time | INOUT | true |
| customMethodId | id | OUT | true |
| dataResourceId | id | INOUT | true |
| dataSourceId | id | INOUT | true |
| decoratorContentId | id | INOUT | true |
| description | description | INOUT | true |
| displayFailCond | Boolean | IN | true |
| instanceOfContentId | id | INOUT | true |
| lastModifiedByUserLogin | id-vlong | INOUT | true |
| lastModifiedDate | date-time | INOUT | true |
| localeString | very-short | INOUT | true |
| mimeTypeId | id-vlong | INOUT | true |
| ownerContentId | id | INOUT | true |
| privilegeEnumId | id | INOUT | true |
| roleTypeList | List | INOUT | true |
| serviceName | long-varchar | OUT | true |
| skipPermissionCheck | String | IN | true |
| statusId | id | INOUT | true |
| targetOperationList | List | IN | true |
| targetOperationString | String | IN | true |
| templateDataResourceId | id | INOUT | true |

## createContentAlternativeUrl

- Definition: `applications/content/servicedef/services.xml:905`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy; invoke: createContentAlternativeUrl
- Default entity: —; auth: true
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentCreated | String | OUT | true |
| contentId | String | IN | true |

## createContentApproval

- Definition: `applications/content/servicedef/services_content.xml:560`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ContentApproval; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| approvalDate | date-time | IN | true |
| approvalStatusId | id | IN | true |
| comments | comment | IN | true |
| contentApprovalId | id | INOUT | true |
| contentId | id | IN | true |
| contentRevisionSeqId | id | IN | true |
| partyId | id | IN | true |
| roleTypeId | id | IN | true |
| sequenceNum | numeric | IN | true |

## createContentAssoc

- Definition: `applications/content/servicedef/services_content.xml:278`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ContentAssoc; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentAssocPredicateId | id | IN | true |
| contentAssocTypeId | id | IN | false |
| contentId | id | IN | false |
| contentIdTo | id | IN | false |
| createdByUserLogin | id-vlong | IN | true |
| createdDate | date-time | IN | true |
| dataSourceId | id | IN | true |
| fromDate | date-time | INOUT | true |
| lastModifiedByUserLogin | id-vlong | IN | true |
| lastModifiedDate | date-time | IN | true |
| leftCoordinate | numeric | IN | true |
| mapKey | name | IN | true |
| sequenceNum | numeric | IN | true |
| thruDate | date-time | IN | true |
| upperCoordinate | numeric | IN | true |

## createContentAssocPredicate

- Definition: `applications/content/servicedef/services_contenttypes.xml:78`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ContentAssocPredicate; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentAssocPredicateId | id | INOUT | true |
| description | description | IN | true |

## createContentAssocType

- Definition: `applications/content/servicedef/services_contenttypes.xml:46`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ContentAssocType; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentAssocTypeId | id | INOUT | true |
| description | description | IN | true |

## createContentAttribute

- Definition: `applications/content/servicedef/services_content.xml:454`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ContentAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | false |
| attrValue | value | IN | true |
| contentId | id | IN | false |

## createContentFromDataResource

- Definition: `applications/content/servicedef/services_content.xml:73`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy; invoke: createContentFromDataResource
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "createContent", "optional": "true"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | String | OUT | false |
| dataResourceId | String | IN | false |
| uploadedFile | java.nio.ByteBuffer | IN | true |

## createContentFromUploadedFile

- Definition: `applications/content/servicedef/services_content.xml:103`
- Engine: group; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: 300
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## createContentKeyword

- Definition: `applications/content/servicedef/services_content.xml:473`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ContentKeyword; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| keyword | short-varchar | IN | false |
| relevancyWeight | numeric | IN | true |

## createContentMetaData

- Definition: `applications/content/servicedef/services_content.xml:371`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ContentMetaData; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| dataSourceId | id | IN | true |
| metaDataPredicateId | id | IN | false |
| metaDataValue | value | IN | true |

## createContentOperation

- Definition: `applications/content/servicedef/services_content.xml:390`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ContentOperation; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentOperationId | id | INOUT | true |
| description | description | IN | true |

## createContentPurpose

- Definition: `applications/content/servicedef/services_content.xml:409`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ContentPurpose; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| contentPurposeTypeId | id | IN | false |
| sequenceNum | numeric | IN | true |

## createContentPurposeOperation

- Definition: `applications/content/servicedef/services_content.xml:435`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ContentPurposeOperation; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentOperationId | id | IN | false |
| contentPurposeTypeId | id | IN | false |
| privilegeEnumId | id | IN | false |
| roleTypeId | id | IN | false |
| statusId | id | IN | false |

## createContentPurposeType

- Definition: `applications/content/servicedef/services_contenttypes.xml:97`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ContentPurposeType; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentPurposeTypeId | id | INOUT | true |
| description | description | IN | true |

## createContentRevision

- Definition: `applications/content/servicedef/services_content.xml:522`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ContentRevision; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| committedByPartyId | id | IN | true |
| contentId | id | IN | true |
| contentRevisionSeqId | id | IN | true |

## createContentRevisionItem

- Definition: `applications/content/servicedef/services_content.xml:541`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ContentRevisionItem; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| contentRevisionSeqId | id | IN | false |
| itemContentId | id | IN | false |
| newDataResourceId | id | IN | true |
| oldDataResourceId | id | IN | true |

## createContentRole

- Definition: `applications/content/servicedef/services_content.xml:321`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ContentRole; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| fromDate | date-time | IN | true |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |
| thruDate | date-time | IN | true |

## createContentType

- Definition: `applications/content/servicedef/services_contenttypes.xml:27`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ContentType; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentTypeId | id | INOUT | true |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## createContentTypeAttr

- Definition: `applications/content/servicedef/services_contenttypes.xml:65`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ContentTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | false |
| contentTypeId | id | IN | false |
| description | description | IN | true |

## createDataCategory

- Definition: `applications/content/servicedef/services.xml:60`
- Engine: entity-auto; location: —; invoke: create
- Default entity: DataCategory; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| categoryName | name | IN | true |
| dataCategoryId | id | INOUT | false |
| parentCategoryId | id | IN | true |

## createDataResource

- Definition: `applications/content/servicedef/services_data.xml:26`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/data/DataServicesScript.groovy; invoke: createDataResource
- Default entity: DataResource; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| characterSetId | id-long | IN | true |
| contentPurposeList | List | IN | true |
| createdByUserLogin | id-vlong | IN | true |
| createdDate | date-time | IN | true |
| dataCategoryId | id | IN | true |
| dataResource | org.apache.ofbiz.entity.GenericValue | OUT | true |
| dataResourceId | String | OUT | false |
| dataResourceName | value | IN | true |
| dataResourceTypeId | id | IN | true |
| dataSourceId | id | IN | true |
| dataTemplateTypeId | id | IN | true |
| isPublic | indicator | IN | true |
| lastModifiedByUserLogin | id-vlong | IN | true |
| lastModifiedDate | date-time | IN | true |
| localeString | very-short | IN | true |
| mimeTypeId | id-vlong | IN | true |
| objectInfo | long-varchar | IN | true |
| partyId | String | IN | true |
| relatedDetailId | id | IN | true |
| roleTypeId | String | IN | true |
| skipPermissionCheck | String | IN | true |
| statusId | id | IN | true |
| surveyId | id | IN | true |
| surveyResponseId | id | IN | true |
| targetOperationList | List | IN | true |
| uploadedFile | java.nio.ByteBuffer | IN | true |

## createDataResourceAndAssocToContent

- Definition: `applications/content/servicedef/services_data.xml:43`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/data/DataServicesScript.groovy; invoke: createDataResourceAndAssocToContent
- Default entity: DataResource; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: CREATE
- Implements: [{"service": "createDataResource", "optional": "true"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | String | INOUT | false |
| templateDataResource | String | IN | true |

## createDataResourceAndText

- Definition: `applications/content/servicedef/services_data.xml:89`
- Engine: java; location: org.apache.ofbiz.content.data.DataServices; invoke: createDataResourceAndText
- Default entity: DataResource; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| characterSetId | id-long | IN | true |
| contentPurposeList | List | IN | true |
| createdByUserLogin | id-vlong | IN | true |
| createdDate | date-time | IN | true |
| dataCategoryId | id | IN | true |
| dataResourceId | id | INOUT | true |
| dataResourceName | value | IN | true |
| dataResourceTypeId | id | IN | true |
| dataSourceId | id | IN | true |
| dataTemplateTypeId | id | IN | true |
| isPublic | indicator | IN | true |
| lastModifiedByUserLogin | id-vlong | IN | true |
| lastModifiedDate | date-time | IN | true |
| localeString | very-short | IN | true |
| mimeTypeId | id-vlong | IN | true |
| objectInfo | long-varchar | IN | true |
| relatedDetailId | id | IN | true |
| skipPermissionCheck | String | IN | true |
| statusId | id | IN | true |
| surveyId | id | IN | true |
| surveyResponseId | id | IN | true |
| targetOperationList | List | IN | true |
| textData | String | IN | true |

## createDataResourceAttribute

- Definition: `applications/content/servicedef/services.xml:79`
- Engine: entity-auto; location: —; invoke: create
- Default entity: DataResourceAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | false |
| attrValue | value | IN | true |
| dataResourceId | id | IN | false |

## createDataResourceMetaData

- Definition: `applications/content/servicedef/services_data.xml:238`
- Engine: entity-auto; location: —; invoke: create
- Default entity: DataResourceMetaData; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceId | id | INOUT | false |
| dataSourceId | id | IN | true |
| metaDataPredicateId | id | INOUT | false |
| metaDataValue | value | IN | true |

## createDataResourcePurpose

- Definition: `applications/content/servicedef/services_data.xml:257`
- Engine: entity-auto; location: —; invoke: create
- Default entity: DataResourcePurpose; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentPurposeTypeId | id | INOUT | true |
| dataResourceId | id | INOUT | true |

## createDataResourceRole

- Definition: `applications/content/servicedef/services.xml:98`
- Engine: entity-auto; location: —; invoke: create
- Default entity: DataResourceRole; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceId | String | IN | — |
| fromDate | Timestamp | INOUT | true |
| partyId | String | IN | — |
| roleTypeId | String | IN | — |
| thruDate | date-time | IN | true |

## createDataResourceType

- Definition: `applications/content/servicedef/services_contenttypes.xml:135`
- Engine: entity-auto; location: —; invoke: create
- Default entity: DataResourceType; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceTypeId | id | INOUT | false |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## createDataResourceTypeAttr

- Definition: `applications/content/servicedef/services_contenttypes.xml:154`
- Engine: entity-auto; location: —; invoke: create
- Default entity: DataResourceTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | false |
| dataResourceTypeId | id | IN | false |
| description | description | IN | true |

## createDataTemplateType

- Definition: `applications/content/servicedef/services_data.xml:321`
- Engine: entity-auto; location: —; invoke: create
- Default entity: DataTemplateType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataTemplateTypeId | id | INOUT | true |
| description | description | IN | true |
| extension | short-varchar | IN | true |

## createDataText

- Definition: `applications/content/servicedef/services_data.xml:77`
- Engine: route; location: —; invoke: —
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "createDataResource"}, {"service": "createElectronicText"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## createDocument

- Definition: `applications/content/servicedef/services_document.xml:43`
- Engine: entity-auto; location: —; invoke: create
- Default entity: Document; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| dateCreated | date-time | IN | true |
| documentId | id | INOUT | true |
| documentLocation | long-varchar | IN | true |
| documentText | long-varchar | IN | true |
| documentTypeId | id | IN | true |
| imageData | object | IN | true |

## createDocumentAttribute

- Definition: `applications/content/servicedef/services_document.xml:27`
- Engine: entity-auto; location: —; invoke: create
- Default entity: DocumentAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | — |
| attrValue | value | IN | true |
| documentId | id | IN | — |

## createDocumentType

- Definition: `applications/content/servicedef/services_document.xml:59`
- Engine: entity-auto; location: —; invoke: create
- Default entity: DocumentType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| documentTypeId | id | INOUT | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## createDocumentTypeAttr

- Definition: `applications/content/servicedef/services_document.xml:75`
- Engine: entity-auto; location: —; invoke: create
- Default entity: DocumentTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| description | description | IN | true |
| documentTypeId | id | IN | — |

## createDownloadContent

- Definition: `applications/content/servicedef/services.xml:146`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy; invoke: createDownloadContent
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | — | INOUT | — |
| file | String | IN | false |

## createElectronicText

- Definition: `applications/content/servicedef/services_data.xml:113`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ElectronicText; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: CREATE
- Implements: [{"service": "createDataResource"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceId | id | IN | false |
| dataResourceTypeId | — | — | — |
| textData | very-long | IN | true |

## createElectronicTextForm

- Definition: `applications/content/servicedef/services_data.xml:130`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ElectronicText; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: CREATE
- Implements: [{"service": "createDataResource"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceId | id | IN | false |
| dataResourceTypeId | — | — | — |
| textData | very-long | IN | true |

## createEmailContent

- Definition: `applications/content/servicedef/services.xml:120`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy; invoke: createEmailContent
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | — | INOUT | — |
| htmlBody | String | IN | true |
| plainBody | String | IN | false |
| subject | String | IN | false |

## createFile

- Definition: `applications/content/servicedef/services_data.xml:276`
- Engine: java; location: org.apache.ofbiz.content.data.DataServices; invoke: createFile
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "createDataResource"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| binData | java.nio.ByteBuffer | IN | true |
| dataResource | org.apache.ofbiz.entity.GenericValue | IN | true |
| dataResourceTypeId | String | IN | true |
| objectInfo | String | IN | true |
| rootDir | String | IN | true |
| textData | String | IN | true |

## createFileExtension

- Definition: `applications/content/servicedef/services_contenttypes.xml:173`
- Engine: entity-auto; location: —; invoke: create
- Default entity: FileExtension; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fileExtensionId | id-long | INOUT | false |
| mimeTypeId | id-vlong | IN | true |

## createFileFromScreen

- Definition: `applications/content/servicedef/services_output.xml:42`
- Engine: java; location: org.apache.ofbiz.content.output.OutputServices; invoke: createFileFromScreen
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentType | String | IN | true |
| fileName | String | IN | false |
| filePath | String | IN | true |
| screenContext | Map | IN | true |
| screenLocation | String | IN | false |

## createImage

- Definition: `applications/content/servicedef/services.xml:645`
- Engine: java; location: org.apache.ofbiz.content.data.DataServices; invoke: createImage
- Default entity: —; auth: —
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceId | String | IN | false |
| imageData | java.nio.ByteBuffer | IN | false |

## createImageDataResource

- Definition: `applications/content/servicedef/services_data.xml:162`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ImageDataResource; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceId | id | INOUT | true |
| imageData | byte-array | IN | true |

## createMetaDataPredicate

- Definition: `applications/content/servicedef/services_contenttypes.xml:192`
- Engine: entity-auto; location: —; invoke: create
- Default entity: MetaDataPredicate; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| metaDataPredicateId | id | INOUT | false |

## createMimeType

- Definition: `applications/content/servicedef/services_contenttypes.xml:211`
- Engine: entity-auto; location: —; invoke: create
- Default entity: MimeType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| mimeTypeId | id-vlong | INOUT | false |

## createMimeTypeHtmlTemplate

- Definition: `applications/content/servicedef/services_contenttypes.xml:227`
- Engine: entity-auto; location: —; invoke: create
- Default entity: MimeTypeHtmlTemplate; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| mimeTypeId | id-vlong | IN | false |
| templateLocation | long-varchar | IN | true |

## createMissingContentAltUrls

- Definition: `applications/content/servicedef/services_content.xml:361`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy; invoke: createMissingContentAltUrls
- Default entity: —; auth: true
- Use transaction: false; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentsNotUpdated | Integer | OUT | true |
| contentsUpdated | Integer | OUT | true |
| prodCatalogId | String | IN | true |
| webSiteId | String | IN | true |

## createOrRemoveProductFeatureDataResource

- Definition: `applications/content/servicedef/services.xml:503`
- Engine: java; location: org.apache.ofbiz.content.ContentManagementServices; invoke: createOrRemoveProductFeatureDataResource
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| action | String | IN | true |
| dataResourceId | String | IN | — |
| productFeatureId | String | IN | — |

## createOtherDataResource

- Definition: `applications/content/servicedef/services_data.xml:219`
- Engine: entity-auto; location: —; invoke: create
- Default entity: OtherDataResource; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceContent | byte-array | IN | true |
| dataResourceId | id | INOUT | true |

## createSimpleTextContent

- Definition: `applications/content/servicedef/services.xml:162`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy; invoke: createSimpleTextContent
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | — | INOUT | — |
| text | String | IN | false |

## createSurvey

- Definition: `applications/content/servicedef/services_survey.xml:27`
- Engine: entity-auto; location: —; invoke: create
- Default entity: Survey; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acroFormContentId | id | IN | true |
| allowMultiple | indicator | IN | true |
| allowUpdate | indicator | IN | true |
| comments | comment | IN | true |
| description | description | IN | true |
| isAnonymous | indicator | IN | true |
| responseService | long-varchar | IN | true |
| submitCaption | short-varchar | IN | true |
| surveyId | id | OUT | false |
| surveyName | name | IN | true |

## createSurveyApplType

- Definition: `applications/content/servicedef/services_survey.xml:117`
- Engine: entity-auto; location: —; invoke: create
- Default entity: SurveyApplType; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| surveyApplTypeId | id | IN | true |

## createSurveyMultiResp

- Definition: `applications/content/servicedef/services_survey.xml:56`
- Engine: entity-auto; location: —; invoke: create
- Default entity: SurveyMultiResp; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| multiRespTitle | name | IN | true |
| surveyId | String | IN | — |
| surveyMultiRespId | String | OUT | — |

## createSurveyMultiRespColumn

- Definition: `applications/content/servicedef/services_survey.xml:76`
- Engine: entity-auto; location: —; invoke: create
- Default entity: SurveyMultiRespColumn; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| columnTitle | name | IN | true |
| sequenceNum | numeric | IN | true |
| surveyId | String | IN | — |
| surveyMultiRespColId | String | OUT | — |
| surveyMultiRespId | String | IN | — |

## createSurveyPage

- Definition: `applications/content/servicedef/services_survey.xml:97`
- Engine: entity-auto; location: —; invoke: create
- Default entity: SurveyPage; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| pageName | name | IN | true |
| sequenceNum | numeric | IN | true |
| surveyId | String | IN | — |
| surveyPageSeqId | String | OUT | — |

## createSurveyQuestion

- Definition: `applications/content/servicedef/services_survey.xml:136`
- Engine: simple; location: component://content/minilang/survey/SurveyServices.xml; invoke: createSurveyQuestion
- Default entity: SurveyQuestion; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| enumTypeId | id | IN | true |
| formatString | value | IN | true |
| geoId | id | IN | true |
| hint | very-long | IN | true |
| question | very-long | IN | true |
| surveyId | String | IN | true |
| surveyQuestionCategoryId | id | IN | true |
| surveyQuestionId | id | OUT | false |
| surveyQuestionTypeId | id | IN | true |

## createSurveyQuestionAppl

- Definition: `applications/content/servicedef/services_survey.xml:178`
- Engine: entity-auto; location: —; invoke: create
- Default entity: SurveyQuestionAppl; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| externalFieldRef | long-varchar | IN | true |
| fromDate | date-time | IN | true |
| requiredField | indicator | IN | true |
| sequenceNum | numeric | IN | true |
| surveyId | id | IN | false |
| surveyMultiRespColId | id | IN | true |
| surveyMultiRespId | id | IN | true |
| surveyPageSeqId | id | IN | true |
| surveyQuestionId | id | IN | false |
| thruDate | date-time | IN | true |
| withSurveyOptionSeqId | id | IN | true |
| withSurveyQuestionId | id | IN | true |

## createSurveyQuestionCategory

- Definition: `applications/content/servicedef/services_survey.xml:198`
- Engine: entity-auto; location: —; invoke: create
- Default entity: SurveyQuestionCategory; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| parentCategoryId | id | IN | true |
| surveyQuestionCategoryId | id | OUT | false |

## createSurveyQuestionOption

- Definition: `applications/content/servicedef/services_survey.xml:158`
- Engine: entity-auto; location: —; invoke: create
- Default entity: SurveyQuestionOption; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amountBase | currency-amount | IN | true |
| amountBaseUomId | id | IN | true |
| description | description | IN | true |
| duration | numeric | IN | true |
| durationUomId | id | IN | true |
| sequenceNum | numeric | IN | true |
| surveyOptionSeqId | String | OUT | — |
| surveyQuestionId | String | IN | — |
| weightFactor | floating-point | IN | true |

## createSurveyQuestionType

- Definition: `applications/content/servicedef/services_survey.xml:217`
- Engine: entity-auto; location: —; invoke: create
- Default entity: SurveyQuestionType; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| surveyQuestionTypeId | id | IN | true |

## createSurveyResponse

- Definition: `applications/content/servicedef/services_survey.xml:256`
- Engine: simple; location: component://content/minilang/survey/SurveyServices.xml; invoke: createSurveyResponse
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| answers | Map | IN | false |
| dataResourceId | String | IN | true |
| productStoreSurveyId | String | INOUT | true |
| surveyId | — | INOUT | false |
| surveyResponseId | String | INOUT | true |

## createSurveyTrigger

- Definition: `applications/content/servicedef/services_survey.xml:236`
- Engine: entity-auto; location: —; invoke: create
- Default entity: SurveyTrigger; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | true |
| surveyApplTypeId | id | IN | false |
| surveyId | id | IN | false |
| thruDate | date-time | IN | true |

## createTextAndUploadedContent

- Definition: `applications/content/servicedef/services_content.xml:56`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy; invoke: createTextAndUploadedContent
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "createTextContent"}, {"service": "uploadFileInterface", "optional": "true"}, {"service": "createContentFromUploadedFile", "optional": "true"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## createTextContent

- Definition: `applications/content/servicedef/services_content.xml:64`
- Engine: group; location: —; invoke: —
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## createVideoDataResource

- Definition: `applications/content/servicedef/services_data.xml:181`
- Engine: entity-auto; location: —; invoke: create
- Default entity: VideoDataResource; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceId | id | INOUT | true |
| videoData | byte-array | IN | true |

## createWebPreferenceType

- Definition: `applications/content/servicedef/services.xml:912`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WebPreferenceType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| webPreferenceTypeId | id | INOUT | true |

## createWebSite

- Definition: `applications/content/servicedef/services_website.xml:26`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WebSite; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| cookieDomain | long-varchar | IN | true |
| enableHttps | indicator | IN | true |
| httpHost | long-varchar | IN | true |
| httpPort | very-short | IN | true |
| httpsHost | long-varchar | IN | true |
| httpsPort | very-short | IN | true |
| secureContentPrefix | url | IN | true |
| siteName | name | IN | false |
| standardContentPrefix | url | IN | true |
| visualThemeSetId | id | IN | true |
| webSiteId | id | IN | false |
| webappPath | long-varchar | IN | true |

## createWebSiteContent

- Definition: `applications/content/servicedef/services_website.xml:41`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WebSiteContent; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| fromDate | date-time | IN | true |
| thruDate | date-time | IN | true |
| webSiteContentTypeId | id | IN | false |
| webSiteId | id | IN | false |

## createWebSiteContentType

- Definition: `applications/content/servicedef/services_website.xml:61`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WebSiteContentType; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| webSiteContentTypeId | id | IN | false |

## createWebSitePathAlias

- Definition: `applications/content/servicedef/services_website.xml:80`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WebSitePathAlias; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| aliasTo | long-varchar | IN | true |
| contentId | id | IN | true |
| fromDate | date-time | IN | false |
| mapKey | name | IN | true |
| pathAlias | id-vlong | IN | false |
| thruDate | date-time | IN | true |
| webSiteId | id | IN | false |

## createWebSiteRole

- Definition: `applications/content/servicedef/services_website.xml:118`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WebSiteRole; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: CREATE
- Implements: [{"service": "webSiteRoleInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | — | — | true |

## deactivateAllContentRoles

- Definition: `applications/content/servicedef/services_content.xml:334`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy; invoke: deactivateAllContentRoles
- Default entity: ContentRole; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | String | IN | false |
| partyId | String | IN | false |
| roleTypeId | String | IN | false |

## deactivateAssocs

- Definition: `applications/content/servicedef/services.xml:349`
- Engine: java; location: org.apache.ofbiz.content.content.ContentServices; invoke: deactivateAssocs
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| activeContentId | String | IN | true |
| contentAssocTypeId | String | IN | false |
| contentId | String | IN | true |
| contentIdTo | String | IN | false |
| deactivatedList | List | OUT | false |
| fromDate | Timestamp | IN | true |
| mapKey | String | IN | true |

## deactivateContentAssoc

- Definition: `applications/content/servicedef/services.xml:369`
- Engine: java; location: org.apache.ofbiz.content.content.ContentServices; invoke: deactivateContentAssoc
- Default entity: —; auth: true
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentAssocTypeId | String | IN | true |
| contentId | String | IN | true |
| contentIdTo | String | IN | true |
| fromDate | Timestamp | IN | true |

## decrementContentChildStats

- Definition: `applications/content/servicedef/services.xml:608`
- Engine: java; location: org.apache.ofbiz.content.ContentManagementServices; invoke: decrementContentChildStats
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentAssocTypeId | String | IN | true |
| contentId | String | IN | false |

## deleteCommContentAssocType

- Definition: `applications/content/servicedef/services_commevent.xml:115`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: CommContentAssocType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| commContentAssocTypeId | id | IN | false |

## deleteContentKeyword

- Definition: `applications/content/servicedef/services_content.xml:485`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ContentKeyword; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| keyword | short-varchar | IN | false |

## deleteContentKeywords

- Definition: `applications/content/servicedef/services_content.xml:490`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy; invoke: deleteContentKeywords
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | String | IN | false |

## deleteDataTemplateType

- Definition: `applications/content/servicedef/services_data.xml:337`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: DataTemplateType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataTemplateTypeId | id | IN | — |

## deleteDocument

- Definition: `applications/content/servicedef/services_document.xml:53`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: Document; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| documentId | id | IN | — |

## deleteDocumentAttribute

- Definition: `applications/content/servicedef/services_document.xml:37`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: DocumentAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| documentId | id | IN | — |

## deleteDocumentType

- Definition: `applications/content/servicedef/services_document.xml:69`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: DocumentType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| documentTypeId | id | IN | — |

## deleteDocumentTypeAttr

- Definition: `applications/content/servicedef/services_document.xml:85`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: DocumentTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| documentTypeId | id | IN | — |

## deleteSurvey

- Definition: `applications/content/servicedef/services_survey.xml:39`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: Survey; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| surveyId | id | IN | false |

## deleteSurveyApplType

- Definition: `applications/content/servicedef/services_survey.xml:129`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: SurveyApplType; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| surveyApplTypeId | id | IN | false |

## deleteSurveyMultiResp

- Definition: `applications/content/servicedef/services_survey.xml:69`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: SurveyMultiResp; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| surveyId | id | IN | false |
| surveyMultiRespId | id | IN | false |

## deleteSurveyMultiRespColumn

- Definition: `applications/content/servicedef/services_survey.xml:90`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: SurveyMultiRespColumn; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| surveyId | id | IN | false |
| surveyMultiRespColId | id | IN | false |
| surveyMultiRespId | id | IN | false |

## deleteSurveyPage

- Definition: `applications/content/servicedef/services_survey.xml:110`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: SurveyPage; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| surveyId | id | IN | false |
| surveyPageSeqId | id | IN | false |

## deleteSurveyQuestion

- Definition: `applications/content/servicedef/services_survey.xml:151`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: SurveyQuestion; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| surveyQuestionId | id | IN | false |

## deleteSurveyQuestionAppl

- Definition: `applications/content/servicedef/services_survey.xml:191`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: SurveyQuestionAppl; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| surveyId | id | IN | false |
| surveyQuestionId | id | IN | false |

## deleteSurveyQuestionCategory

- Definition: `applications/content/servicedef/services_survey.xml:210`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: SurveyQuestionCategory; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| surveyQuestionCategoryId | id | IN | false |

## deleteSurveyQuestionOption

- Definition: `applications/content/servicedef/services_survey.xml:171`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: SurveyQuestionOption; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| surveyOptionSeqId | id | IN | false |
| surveyQuestionId | id | IN | false |

## deleteSurveyQuestionType

- Definition: `applications/content/servicedef/services_survey.xml:229`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: SurveyQuestionType; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| surveyQuestionTypeId | id | IN | false |

## deleteSurveyTrigger

- Definition: `applications/content/servicedef/services_survey.xml:249`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: SurveyTrigger; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| surveyApplTypeId | id | IN | false |
| surveyId | id | IN | false |

## deleteWebPreferenceType

- Definition: `applications/content/servicedef/services.xml:922`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WebPreferenceType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| webPreferenceTypeId | id | IN | — |

## expireCommEventContentAssoc

- Definition: `applications/content/servicedef/services_commevent.xml:100`
- Engine: entity-auto; location: —; invoke: expire
- Default entity: CommEventContentAssoc; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | id | IN | false |
| contentId | id | IN | false |
| fromDate | date-time | IN | false |

## expireContentAssoc

- Definition: `applications/content/servicedef/services_content.xml:291`
- Engine: entity-auto; location: —; invoke: expire
- Default entity: ContentAssoc; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentAssocTypeId | id | IN | false |
| contentId | id | IN | false |
| contentIdTo | id | IN | false |
| fromDate | date-time | IN | false |

## findAssocContent

- Definition: `applications/content/servicedef/services.xml:179`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy; invoke: findAssocContent
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentAssocs | List | OUT | true |
| contentId | String | IN | false |
| mapKeys | List | IN | false |

## findRelatedContent

- Definition: `applications/content/servicedef/services_content.xml:213`
- Engine: java; location: org.apache.ofbiz.content.content.ContentServices; invoke: findRelatedContent
- Default entity: Content; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentAssocTypeList | List | IN | true |
| contentList | List | OUT | false |
| contentTypeList | List | IN | true |
| currentContent | org.apache.ofbiz.entity.GenericValue | IN | false |
| entityOperation | String | IN | true |
| fromDate | String | IN | true |
| targetOperationList | List | IN | true |
| thruDate | String | IN | true |
| toFrom | String | IN | true |
| userLogin | org.apache.ofbiz.entity.GenericValue | IN | true |

## findSubNodes

- Definition: `applications/content/servicedef/services.xml:576`
- Engine: java; location: org.apache.ofbiz.content.ContentManagementServices; invoke: findSubNodes
- Default entity: —; auth: false
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| _LIST_ | List | OUT | true |
| contentId | String | IN | false |

## followNodeChildren

- Definition: `applications/content/servicedef/services.xml:710`
- Engine: java; location: org.apache.ofbiz.content.ContentManagementServices; invoke: followNodeChildren
- Default entity: —; auth: true
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentAssocTypeId | String | IN | true |
| contentId | String | IN | false |
| serviceName | String | IN | false |

## forceIndexContentKeywords

- Definition: `applications/content/servicedef/services_content.xml:510`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy; invoke: forceIndexContentKeywords
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | String | IN | false |

## generateBlogRssFeed

- Definition: `applications/content/servicedef/services.xml:855`
- Engine: java; location: org.apache.ofbiz.content.blog.BlogRssServices; invoke: generateBlogRssFeed
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "rssFeedInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| blogContentId | String | IN | false |

## generateMissingSeoUrlForWebsite

- Definition: `applications/content/servicedef/services_website.xml:151`
- Engine: simple; location: component://content/minilang/website/WebSiteServices.xml; invoke: generateMissingSeoUrlForWebsite
- Default entity: —; auth: true
- Use transaction: —; timeout: 36000000
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| prodCatalogId | String | IN | false |
| typeGenerate | List | IN | false |
| webSiteId | String | IN | false |

## genericContentPermission

- Definition: `applications/content/servicedef/services.xml:875`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/permission/ContentPermissionServices.groovy; invoke: genericContentPermission
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | String | IN | true |
| contentIdFrom | String | IN | true |
| contentIdTo | String | IN | true |
| contentOperationId | String | IN | true |
| contentPurposeTypeId | String | IN | true |
| ownerContentId | String | IN | true |
| roleTypeId | String | IN | true |
| statusId | String | IN | true |

## genericDataResourcePermission

- Definition: `applications/content/servicedef/services.xml:897`
- Engine: simple; location: component://content/minilang/permission/DataResourcePermissionServices.xml; invoke: genericDataResourcePermission
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceId | String | IN | true |
| dataTemplateTypeId | String | IN | true |

## getAcroFieldsFromPdf

- Definition: `applications/content/servicedef/services_survey.xml:301`
- Engine: java; location: org.apache.ofbiz.content.survey.PdfSurveyServices; invoke: getAcroFieldsFromPdf
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acroFieldMap | Map | OUT | false |
| contentId | String | IN | true |
| inputByteBuffer | java.nio.ByteBuffer | IN | true |

## getAssocAndContentAndDataResource

- Definition: `applications/content/servicedef/services.xml:232`
- Engine: java; location: org.apache.ofbiz.content.content.ContentServicesComplex; invoke: getAssocAndContentAndDataResource
- Default entity: ContentAssocDataResourceViewFrom; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| assocTypes | List | IN | true |
| contentId | String | IN | true |
| contentTypes | List | IN | true |
| direction | String | IN | true |
| entityList | List | OUT | true |
| fromDate | Timestamp | IN | true |
| fromDateStr | String | IN | true |
| mapKey | String | IN | true |
| nullThruDatesOnly | Boolean | IN | true |
| thruDate | Timestamp | IN | true |
| thruDateStr | String | IN | true |

## getAssocAndContentAndDataResourceCache

- Definition: `applications/content/servicedef/services.xml:206`
- Engine: java; location: org.apache.ofbiz.content.content.ContentServicesComplex; invoke: getAssocAndContentAndDataResourceCache
- Default entity: ContentAssocDataResourceViewFrom; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| assocTypes | List | IN | true |
| assocTypesString | String | IN | true |
| contentAssocPredicateId | String | IN | true |
| contentId | String | IN | true |
| contentIdFrom | String | IN | true |
| contentTypes | List | IN | true |
| contentTypesString | String | IN | true |
| direction | String | IN | true |
| entityList | List | OUT | true |
| fromDate | Timestamp | IN | true |
| fromDateStr | String | IN | true |
| mapKey | String | IN | true |
| nullThruDatesOnly | Boolean | IN | true |
| thruDate | Timestamp | IN | true |
| thruDateStr | String | IN | true |
| view | org.apache.ofbiz.entity.GenericValue | OUT | true |

## getBlogEntry

- Definition: `applications/content/servicedef/services.xml:821`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/BlogServices.groovy; invoke: getBlogEntry
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| articleContentId | String | OUT | true |
| articleData | String | OUT | true |
| articleDataResourceId | String | OUT | true |
| blogContentId | String | INOUT | false |
| contentId | String | INOUT | true |
| contentName | String | OUT | true |
| description | String | OUT | true |
| imageContentId | String | OUT | true |
| imageDataResourceId | String | IN | true |
| statusId | String | OUT | true |
| summaryContentId | String | OUT | true |
| summaryData | String | OUT | true |
| summaryDataResourceId | String | OUT | true |
| templateDataResourceId | String | OUT | true |

## getContent

- Definition: `applications/content/servicedef/services.xml:251`
- Engine: java; location: org.apache.ofbiz.content.ContentManagementServices; invoke: getContent
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | String | IN | true |
| view | org.apache.ofbiz.entity.GenericValue | OUT | true |

## getContentAndDataResource

- Definition: `applications/content/servicedef/services.xml:43`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy; invoke: getContentAndDataResource
- Default entity: Content; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| resultData | java.util.Map | OUT | true |

## getDataResource

- Definition: `applications/content/servicedef/services.xml:51`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy; invoke: getDataResource
- Default entity: DataResource; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceId | id | IN | false |
| resultData | java.util.Map | OUT | true |

## getElectronicText

- Definition: `applications/content/servicedef/services_data.xml:152`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/data/DataServicesScript.groovy; invoke: getElectronicText
- Default entity: ElectronicText; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| content | org.apache.ofbiz.entity.GenericValue | IN | true |
| contentId | String | IN | true |
| dataResourceId | String | OUT | true |
| textData | String | OUT | false |

## getOwnedOrPublishedBlogEntries

- Definition: `applications/content/servicedef/services.xml:843`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/BlogServices.groovy; invoke: getOwnedOrPublishedBlogEntries
- Default entity: —; auth: true
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| blogContentId | String | OUT | true |
| blogList | List | OUT | true |
| contentId | String | IN | — |

## getPrefixedMembers

- Definition: `applications/content/servicedef/services.xml:736`
- Engine: java; location: org.apache.ofbiz.content.content.ContentServices; invoke: getPrefixedMembers
- Default entity: —; auth: false
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| mapIn | java.util.Map | IN | true |
| mapOut | java.util.Map | OUT | true |
| prefix | String | IN | false |

## getPublicForumMessage

- Definition: `applications/content/servicedef/services.xml:35`
- Engine: group; location: —; invoke: —
- Default entity: Content; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## getSubContent

- Definition: `applications/content/servicedef/services.xml:257`
- Engine: java; location: org.apache.ofbiz.content.ContentManagementServices; invoke: getSubContent
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| assocTypes | List | IN | true |
| assocTypesString | String | IN | true |
| content | org.apache.ofbiz.entity.GenericValue | OUT | true |
| contentId | String | IN | true |
| contentTypes | List | IN | true |
| fromDate | Timestamp | IN | true |
| mapKey | String | IN | true |
| subContentId | String | IN | true |
| view | org.apache.ofbiz.entity.GenericValue | OUT | true |

## getWebSitePathAlias

- Definition: `applications/content/servicedef/services_website.xml:97`
- Engine: simple; location: component://content/minilang/website/WebSiteServices.xml; invoke: getWebSitePathAlias
- Default entity: WebSitePathAlias; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| aliasTo | long-varchar | OUT | false |
| contentId | id | OUT | false |
| fromDate | date-time | IN | false |
| mapKey | name | OUT | false |
| pathAlias | id-vlong | IN | false |
| thruDate | date-time | OUT | false |
| webSiteId | id | IN | false |

## incrementContentChildStats

- Definition: `applications/content/servicedef/services.xml:597`
- Engine: java; location: org.apache.ofbiz.content.ContentManagementServices; invoke: incrementContentChildStats
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentAssocTypeId | String | IN | true |
| contentId | String | IN | false |

## indexContentKeywords

- Definition: `applications/content/servicedef/services_content.xml:500`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy; invoke: indexContentKeywords
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | String | IN | false |
| contentInstance | org.apache.ofbiz.entity.GenericValue | IN | true |

## initContentChildCounts

- Definition: `applications/content/servicedef/services.xml:587`
- Engine: java; location: org.apache.ofbiz.content.ContentManagementServices; invoke: initContentChildCounts
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| content | org.apache.ofbiz.entity.GenericValue | IN | false |

## joinString

- Definition: `applications/content/servicedef/services.xml:754`
- Engine: java; location: org.apache.ofbiz.content.content.ContentServices; invoke: joinString
- Default entity: —; auth: false
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| delimiter | String | IN | true |
| inputList | java.util.List | IN | false |
| outputString | String | OUT | true |

## linkContentToPubPt

- Definition: `applications/content/servicedef/services.xml:476`
- Engine: java; location: org.apache.ofbiz.content.content.ContentServices; invoke: linkContentToPubPt
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentAssocTypeId | String | IN | true |
| contentId | String | IN | false |
| contentIdTo | String | IN | false |
| mapKey | String | IN | true |
| privilegeEnumId | String | IN | true |
| publish | String | IN | true |
| statusId | String | IN | true |

## persistContentAndAssoc

- Definition: `applications/content/servicedef/services.xml:270`
- Engine: java; location: org.apache.ofbiz.content.ContentManagementServices; invoke: persistContentAndAssoc
- Default entity: —; auth: true
- Use transaction: —; timeout: 7200
- Permission service: genericContentPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| _imageData_contentType | String | IN | true |
| _imageData_fileName | String | IN | true |
| caContentAssocTypeId | String | INOUT | true |
| caContentId | String | INOUT | true |
| caContentIdTo | String | INOUT | true |
| caFromDate | Timestamp | INOUT | true |
| caSequenceNum | Long | INOUT | true |
| contentId | String | INOUT | true |
| contentPurposeList | List | IN | true |
| contentPurposeString | String | IN | true |
| dataResourceId | String | INOUT | true |
| deactivateExisting | String | IN | true |
| displayFailCond | Boolean | IN | true |
| drDataResourceId | String | INOUT | true |
| forceElectronicText | String | IN | true |
| imageData | java.nio.ByteBuffer | IN | true |
| ownerContentId | String | IN | true |
| roleTypeList | List | INOUT | true |
| rootDir | String | IN | true |
| targetOperationList | List | IN | true |
| targetOperationString | String | IN | true |
| textData | — | — | — |
| userLogin | org.apache.ofbiz.entity.GenericValue | IN | true |

## persistDataResourceAndData

- Definition: `applications/content/servicedef/services.xml:319`
- Engine: java; location: org.apache.ofbiz.content.ContentManagementServices; invoke: persistDataResourceAndData
- Default entity: —; auth: true
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| _imageData_contentType | String | IN | true |
| _imageData_fileName | String | IN | true |
| contentId | String | INOUT | true |
| contentPurposeList | List | IN | true |
| contentPurposeString | String | IN | true |
| dataResourceId | String | INOUT | true |
| displayFailCond | Boolean | IN | true |
| drDataResourceId | String | INOUT | true |
| forceElectronicText | String | IN | true |
| imageData | java.nio.ByteBuffer | IN | true |
| rootDir | String | IN | true |
| targetOperationList | List | IN | true |
| targetOperationString | String | IN | true |
| textData | — | — | — |
| userLogin | org.apache.ofbiz.entity.GenericValue | IN | true |

## publishContent

- Definition: `applications/content/servicedef/services.xml:725`
- Engine: java; location: org.apache.ofbiz.content.content.ContentServices; invoke: publishContent
- Default entity: —; auth: true
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| content | org.apache.ofbiz.entity.GenericValue | IN | false |

## removeAudioDataResource

- Definition: `applications/content/servicedef/services_data.xml:212`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AudioDataResource; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceId | id | IN | false |

## removeCharacterSet

- Definition: `applications/content/servicedef/services_contenttypes.xml:128`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: CharacterSet; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| characterSetId | id-long | IN | false |

## removeContent

- Definition: `applications/content/servicedef/services_content.xml:162`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: Content; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| contentPurposeTypeId | String | IN | true |
| roleTypeList | List | INOUT | true |
| targetOperationList | List | IN | true |
| userLogin | org.apache.ofbiz.entity.GenericValue | IN | true |

## removeContentAndRelated

- Definition: `applications/content/servicedef/services_content.xml:172`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy; invoke: removeContentAndRelated
- Default entity: Content; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| contentPurposeTypeId | String | IN | true |
| roleTypeList | List | INOUT | true |
| targetOperationList | List | IN | true |
| userLogin | org.apache.ofbiz.entity.GenericValue | IN | true |

## removeContentApproval

- Definition: `applications/content/servicedef/services_content.xml:572`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ContentApproval; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentApprovalId | id | IN | false |

## removeContentAssoc

- Definition: `applications/content/servicedef/services_content.xml:296`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ContentAssoc; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentAssocTypeId | id | IN | false |
| contentId | id | IN | false |
| contentIdTo | id | IN | false |
| fromDate | date-time | IN | false |

## removeContentAssocPredicate

- Definition: `applications/content/servicedef/services_contenttypes.xml:90`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ContentAssocPredicate; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentAssocPredicateId | id | IN | false |

## removeContentAssocType

- Definition: `applications/content/servicedef/services_contenttypes.xml:58`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ContentAssocType; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentAssocTypeId | id | IN | false |

## removeContentAttribute

- Definition: `applications/content/servicedef/services_content.xml:466`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ContentAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | false |
| contentId | id | IN | false |

## removeContentMetaData

- Definition: `applications/content/servicedef/services_content.xml:383`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ContentMetaData; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| metaDataPredicateId | id | IN | false |

## removeContentOperation

- Definition: `applications/content/servicedef/services_content.xml:402`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ContentOperation; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentOperationId | id | IN | false |

## removeContentPurpose

- Definition: `applications/content/servicedef/services_content.xml:421`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ContentPurpose; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| contentPurposeTypeId | id | IN | false |

## removeContentPurposeOperation

- Definition: `applications/content/servicedef/services_content.xml:447`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ContentPurposeOperation; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentOperationId | id | IN | false |
| contentPurposeTypeId | id | IN | false |
| privilegeEnumId | id | IN | false |
| roleTypeId | id | IN | false |
| statusId | id | IN | false |

## removeContentPurposeType

- Definition: `applications/content/servicedef/services_contenttypes.xml:109`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ContentPurposeType; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentPurposeTypeId | id | IN | false |

## removeContentRevision

- Definition: `applications/content/servicedef/services_content.xml:534`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ContentRevision; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| contentRevisionSeqId | id | IN | false |

## removeContentRevisionItem

- Definition: `applications/content/servicedef/services_content.xml:553`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ContentRevisionItem; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| contentRevisionSeqId | id | IN | false |
| itemContentId | id | IN | false |

## removeContentRole

- Definition: `applications/content/servicedef/services_content.xml:354`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ContentRole; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| fromDate | date-time | IN | false |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |

## removeContentType

- Definition: `applications/content/servicedef/services_contenttypes.xml:39`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ContentType; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentTypeId | id | IN | false |

## removeContentTypeAttr

- Definition: `applications/content/servicedef/services_contenttypes.xml:71`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ContentTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | false |
| contentTypeId | id | IN | false |

## removeDataCategory

- Definition: `applications/content/servicedef/services.xml:72`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: DataCategory; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataCategoryId | id | IN | false |

## removeDataResource

- Definition: `applications/content/servicedef/services_data.xml:67`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: DataResource; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentPurposeList | List | IN | true |
| dataResourceId | id | IN | false |
| skipPermissionCheck | String | IN | true |
| targetOperationList | List | IN | true |

## removeDataResourceAttribute

- Definition: `applications/content/servicedef/services.xml:91`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: DataResourceAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | false |
| dataResourceId | id | IN | false |

## removeDataResourceMetaData

- Definition: `applications/content/servicedef/services_data.xml:250`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: DataResourceMetaData; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceId | id | IN | false |
| metaDataPredicateId | id | IN | false |

## removeDataResourcePurpose

- Definition: `applications/content/servicedef/services_data.xml:269`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: DataResourcePurpose; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentPurposeTypeId | id | IN | false |
| dataResourceId | id | IN | false |

## removeDataResourceRole

- Definition: `applications/content/servicedef/services.xml:113`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: DataResourceRole; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceId | id | IN | false |
| fromDate | date-time | IN | false |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |

## removeDataResourceType

- Definition: `applications/content/servicedef/services_contenttypes.xml:147`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: DataResourceType; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceTypeId | id | IN | false |

## removeDataResourceTypeAttr

- Definition: `applications/content/servicedef/services_contenttypes.xml:166`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: DataResourceTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | false |
| dataResourceTypeId | id | IN | false |

## removeElectronicText

- Definition: `applications/content/servicedef/services_data.xml:147`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ElectronicText; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceId | id | IN | false |

## removeFileExtension

- Definition: `applications/content/servicedef/services_contenttypes.xml:185`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: FileExtension; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fileExtensionId | id-long | IN | false |

## removeImageDataResource

- Definition: `applications/content/servicedef/services_data.xml:174`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ImageDataResource; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceId | id | IN | false |

## removeMetaDataPredicate

- Definition: `applications/content/servicedef/services_contenttypes.xml:204`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: MetaDataPredicate; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| metaDataPredicateId | id | IN | false |

## removeMimeType

- Definition: `applications/content/servicedef/services_contenttypes.xml:221`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: MimeType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| mimeTypeId | id-vlong | IN | false |

## removeMimeTypeHtmlTemplate

- Definition: `applications/content/servicedef/services_contenttypes.xml:237`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: MimeTypeHtmlTemplate; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| mimeTypeId | id-vlong | IN | false |

## removeOtherDataResource

- Definition: `applications/content/servicedef/services_data.xml:231`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: OtherDataResource; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceId | id | IN | false |

## removeVideoDataResource

- Definition: `applications/content/servicedef/services_data.xml:193`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: VideoDataResource; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceId | id | IN | false |

## removeWebSiteContent

- Definition: `applications/content/servicedef/services_website.xml:54`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WebSiteContent; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| fromDate | date-time | IN | false |
| webSiteContentTypeId | id | IN | false |
| webSiteId | id | IN | false |

## removeWebSiteContentType

- Definition: `applications/content/servicedef/services_website.xml:73`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WebSiteContentType; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| webSiteContentTypeId | id | IN | false |

## removeWebSitePathAlias

- Definition: `applications/content/servicedef/services_website.xml:92`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WebSitePathAlias; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| pathAlias | id-vlong | IN | false |
| webSiteId | id | IN | false |

## removeWebSiteRole

- Definition: `applications/content/servicedef/services_website.xml:129`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## renderContentAsText

- Definition: `applications/content/servicedef/services.xml:424`
- Engine: java; location: org.apache.ofbiz.content.content.ContentServices; invoke: renderContentAsText
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | String | IN | true |
| locale | String | IN | true |
| mimeTypeId | String | IN | true |
| outWriter | java.io.Writer | IN | true |
| subContentDataResourceView | org.apache.ofbiz.entity.GenericValue | IN | true |
| templateContext | Map | IN | true |
| textData | String | OUT | true |

## renderDataResourceAsText

- Definition: `applications/content/servicedef/services.xml:435`
- Engine: java; location: org.apache.ofbiz.content.data.DataServices; invoke: renderDataResourceAsText
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceId | String | IN | false |
| locale | String | IN | true |
| mimeTypeId | String | IN | true |
| outWriter | java.io.Writer | IN | false |
| subContentDataResourceView | org.apache.ofbiz.entity.GenericValue | IN | true |
| templateContext | Map | IN | false |
| textData | String | OUT | true |

## renderSubContentAsText

- Definition: `applications/content/servicedef/services.xml:401`
- Engine: java; location: org.apache.ofbiz.content.content.ContentServices; invoke: renderSubContentAsText
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | String | IN | true |
| fromDate | Timestamp | IN | true |
| locale | String | IN | true |
| mapKey | String | IN | true |
| mimeTypeId | String | IN | true |
| outWriter | java.io.Writer | IN | false |
| subContentDataResourceView | org.apache.ofbiz.entity.GenericValue | IN | true |
| subContentId | String | IN | true |
| templateContext | Map | IN | false |
| textData | String | OUT | true |
| view | org.apache.ofbiz.entity.GenericValue | OUT | true |

## resequence

- Definition: `applications/content/servicedef/services.xml:511`
- Engine: group; location: —; invoke: —
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## resequenceContentAssocChildren

- Definition: `applications/content/servicedef/services.xml:517`
- Engine: java; location: org.apache.ofbiz.content.ContentManagementServices; invoke: resequence
- Default entity: —; auth: true
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentAssocTypeId | String | IN | true |
| contentId | String | IN | false |
| contentIdTo | String | IN | true |
| dir | String | IN | true |
| seqInc | Integer | IN | true |
| typeList | List | IN | true |

## resetToOutlineMode

- Definition: `applications/content/servicedef/services.xml:554`
- Engine: java; location: org.apache.ofbiz.content.ContentManagementServices; invoke: resetToOutlineMode
- Default entity: —; auth: true
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | String | IN | false |
| pageMode | String | IN | true |

## sendContentToFtp

- Definition: `applications/content/servicedef/services_ftp.xml:26`
- Engine: java; location: org.apache.ofbiz.content.ftp.FtpServices; invoke: sendContentToFtp
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | String | INOUT | true |
| contactMechId | String | IN | — |
| contentId | String | IN | — |
| partyId | String | IN | true |

## sendPrintFromScreen

- Definition: `applications/content/servicedef/services_output.xml:26`
- Engine: java; location: org.apache.ofbiz.content.output.OutputServices; invoke: sendPrintFromScreen
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentType | String | IN | true |
| docAttributes | List | IN | true |
| printRequestAttributes | List | IN | true |
| printerContentType | String | IN | true |
| printerName | String | IN | true |
| screenContext | Map | IN | true |
| screenLocation | String | IN | false |

## setAcroFields

- Definition: `applications/content/servicedef/services_survey.xml:322`
- Engine: java; location: org.apache.ofbiz.content.survey.PdfSurveyServices; invoke: setAcroFields
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acroFieldMap | Map | IN | false |
| contentId | String | IN | true |
| inputByteBuffer | java.nio.ByteBuffer | IN | true |
| outByteBuffer | java.nio.ByteBuffer | OUT | true |

## setAcroFieldsFromSurveyResponse

- Definition: `applications/content/servicedef/services_survey.xml:309`
- Engine: java; location: org.apache.ofbiz.content.survey.PdfSurveyServices; invoke: setAcroFieldsFromSurveyResponse
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | String | IN | true |
| inputByteBuffer | java.nio.ByteBuffer | IN | true |
| outByteBuffer | java.nio.ByteBuffer | OUT | true |
| surveyResponseId | String | IN | false |

## setContentStatus

- Definition: `applications/content/servicedef/services_content.xml:303`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy; invoke: setContentStatus
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | String | IN | false |
| oldStatusId | String | OUT | true |
| statusId | String | IN | false |

## splitString

- Definition: `applications/content/servicedef/services.xml:747`
- Engine: java; location: org.apache.ofbiz.content.content.ContentServices; invoke: splitString
- Default entity: —; auth: false
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| delimiter | String | IN | true |
| inputString | String | IN | true |
| outputList | java.util.List | OUT | true |

## surveyResponseProcessInterface

- Definition: `applications/content/servicedef/services_survey.xml:271`
- Engine: interface; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| surveyResponseId | String | IN | false |

## updateAudioDataResource

- Definition: `applications/content/servicedef/services_data.xml:206`
- Engine: entity-auto; location: —; invoke: update
- Default entity: AudioDataResource; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| audioData | byte-array | IN | true |
| dataResourceId | id | IN | false |

## updateBlogEntry

- Definition: `applications/content/servicedef/services.xml:797`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/BlogServices.groovy; invoke: updateBlogEntry
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "uploadFileInterface", "optional": "true"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| articleData | String | IN | true |
| blogContentId | String | INOUT | false |
| contentId | String | INOUT | false |
| contentName | String | IN | true |
| description | String | IN | true |
| statusId | String | IN | true |
| summaryData | String | IN | true |
| templateDataResourceId | String | IN | true |

## updateCharacterSet

- Definition: `applications/content/servicedef/services_contenttypes.xml:122`
- Engine: entity-auto; location: —; invoke: update
- Default entity: CharacterSet; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| characterSetId | id-long | IN | false |
| description | description | IN | true |

## updateCommContentAssocType

- Definition: `applications/content/servicedef/services_commevent.xml:110`
- Engine: entity-auto; location: —; invoke: update
- Default entity: CommContentAssocType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| commContentAssocTypeId | id | IN | false |
| description | description | IN | true |

## updateCommContentDataResource

- Definition: `applications/content/servicedef/services_commevent.xml:55`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy; invoke: updateCommContentDataResource
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: UPDATE
- Implements: [{"service": "persistContentAndAssoc"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | String | IN | false |
| fromDate | java.sql.Timestamp | IN | false |

## updateCommEventContentAssoc

- Definition: `applications/content/servicedef/services_commevent.xml:79`
- Engine: entity-auto; location: —; invoke: update
- Default entity: CommEventContentAssoc; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| communicationEventId | String | IN | false |
| contentId | String | IN | false |
| fromDate | java.sql.Timestamp | IN | false |
| sequenceNum | Long | IN | true |
| thruDate | java.sql.Timestamp | IN | true |

## updateContent

- Definition: `applications/content/servicedef/services_content.xml:131`
- Engine: entity-auto; location: —; invoke: update
- Default entity: Content; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: UPDATE
- Implements: [{"service": "updateContentAssoc", "optional": "true"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| characterSetId | id-long | IN | true |
| childBranchCount | numeric | IN | true |
| childLeafCount | numeric | IN | true |
| contentId | id | INOUT | false |
| contentIdFrom | String | IN | true |
| contentName | value | IN | true |
| contentPurposeTypeId | String | IN | true |
| contentTypeId | id | IN | true |
| createdByUserLogin | id-vlong | IN | true |
| createdDate | date-time | IN | true |
| customMethodId | id | OUT | true |
| dataResourceId | id | IN | true |
| dataSourceId | id | IN | true |
| decoratorContentId | id | IN | true |
| description | description | IN | true |
| displayFailCond | Boolean | IN | true |
| instanceOfContentId | id | IN | true |
| lastModifiedByUserLogin | id-vlong | IN | true |
| lastModifiedDate | date-time | IN | true |
| localeString | very-short | IN | true |
| mimeTypeId | id-vlong | IN | true |
| ownerContentId | id | IN | true |
| privilegeEnumId | id | IN | true |
| roleTypeList | List | INOUT | true |
| serviceName | long-varchar | OUT | true |
| skipPermissionCheck | String | IN | true |
| statusId | id | IN | true |
| targetOperationList | List | IN | true |
| templateDataResourceId | id | IN | true |

## updateContentAndUploadedFile

- Definition: `applications/content/servicedef/services_content.xml:113`
- Engine: group; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: 300
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## updateContentApproval

- Definition: `applications/content/servicedef/services_content.xml:566`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ContentApproval; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| approvalDate | date-time | IN | true |
| approvalStatusId | id | IN | true |
| comments | comment | IN | true |
| contentApprovalId | id | IN | false |
| contentId | id | IN | true |
| contentRevisionSeqId | id | IN | true |
| partyId | id | IN | true |
| roleTypeId | id | IN | true |
| sequenceNum | numeric | IN | true |

## updateContentAssoc

- Definition: `applications/content/servicedef/services_content.xml:285`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ContentAssoc; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentAssocPredicateId | id | IN | true |
| contentAssocTypeId | id | IN | false |
| contentId | id | IN | false |
| contentIdTo | id | IN | false |
| createdByUserLogin | id-vlong | IN | true |
| createdDate | date-time | IN | true |
| dataSourceId | id | IN | true |
| fromDate | date-time | IN | false |
| lastModifiedByUserLogin | id-vlong | IN | true |
| lastModifiedDate | date-time | IN | true |
| leftCoordinate | numeric | IN | true |
| mapKey | name | IN | true |
| sequenceNum | numeric | IN | true |
| thruDate | date-time | IN | true |
| upperCoordinate | numeric | IN | true |

## updateContentAssocPredicate

- Definition: `applications/content/servicedef/services_contenttypes.xml:84`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ContentAssocPredicate; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentAssocPredicateId | id | IN | false |
| description | description | IN | true |

## updateContentAssocType

- Definition: `applications/content/servicedef/services_contenttypes.xml:52`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ContentAssocType; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentAssocTypeId | id | IN | false |
| description | description | IN | true |

## updateContentAttribute

- Definition: `applications/content/servicedef/services_content.xml:460`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ContentAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | false |
| attrValue | value | IN | true |
| contentId | id | IN | false |

## updateContentChildStats

- Definition: `applications/content/servicedef/services.xml:619`
- Engine: java; location: org.apache.ofbiz.content.ContentManagementServices; invoke: updateContentChildStats
- Default entity: —; auth: —
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentAssocTypeId | String | IN | true |
| contentId | String | IN | false |

## updateContentKeyword

- Definition: `applications/content/servicedef/services_content.xml:479`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ContentKeyword; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| keyword | short-varchar | IN | false |
| relevancyWeight | numeric | IN | true |

## updateContentMetaData

- Definition: `applications/content/servicedef/services_content.xml:377`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ContentMetaData; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| dataSourceId | id | IN | true |
| metaDataPredicateId | id | IN | false |
| metaDataValue | value | IN | true |

## updateContentOperation

- Definition: `applications/content/servicedef/services_content.xml:396`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ContentOperation; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentOperationId | id | IN | false |
| description | description | IN | true |

## updateContentPurpose

- Definition: `applications/content/servicedef/services_content.xml:415`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ContentPurpose; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| contentPurposeTypeId | id | IN | false |
| sequenceNum | numeric | IN | true |

## updateContentPurposeOperation

- Definition: `applications/content/servicedef/services_content.xml:441`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ContentPurposeOperation; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentOperationId | id | IN | false |
| contentPurposeTypeId | id | IN | false |
| privilegeEnumId | id | IN | false |
| roleTypeId | id | IN | false |
| statusId | id | IN | false |

## updateContentPurposeType

- Definition: `applications/content/servicedef/services_contenttypes.xml:103`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ContentPurposeType; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentPurposeTypeId | id | IN | false |
| description | description | IN | true |

## updateContentRevision

- Definition: `applications/content/servicedef/services_content.xml:528`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ContentRevision; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| committedByPartyId | id | IN | true |
| contentId | id | IN | false |
| contentRevisionSeqId | id | IN | false |

## updateContentRevisionItem

- Definition: `applications/content/servicedef/services_content.xml:547`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ContentRevisionItem; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| contentRevisionSeqId | id | IN | false |
| itemContentId | id | IN | false |
| newDataResourceId | id | IN | true |
| oldDataResourceId | id | IN | true |

## updateContentRole

- Definition: `applications/content/servicedef/services_content.xml:328`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ContentRole; auth: true
- Use transaction: —; timeout: —
- Permission service: genericContentPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| fromDate | date-time | IN | false |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |
| thruDate | date-time | IN | true |

## updateContentSubscription

- Definition: `applications/content/servicedef/services.xml:660`
- Engine: java; location: org.apache.ofbiz.content.ContentManagementServices; invoke: updateContentSubscription
- Default entity: —; auth: true
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | String | IN | true |
| partyId | String | IN | false |
| useRoleTypeId | String | IN | true |
| useTime | Integer | IN | false |
| useTimeUomId | String | IN | false |

## updateContentSubscriptionByOrder

- Definition: `applications/content/servicedef/services.xml:701`
- Engine: java; location: org.apache.ofbiz.content.ContentManagementServices; invoke: updateContentSubscriptionByOrder
- Default entity: —; auth: true
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| orderId | String | IN | false |

## updateContentSubscriptionByProduct

- Definition: `applications/content/servicedef/services.xml:681`
- Engine: java; location: org.apache.ofbiz.content.ContentManagementServices; invoke: updateContentSubscriptionByProduct
- Default entity: —; auth: true
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| orderCreatedDate | Timestamp | IN | true |
| partyId | String | IN | false |
| productId | String | IN | false |
| quantity | Integer | IN | false |

## updateContentType

- Definition: `applications/content/servicedef/services_contenttypes.xml:33`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ContentType; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentTypeId | id | IN | false |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## updateDataCategory

- Definition: `applications/content/servicedef/services.xml:66`
- Engine: entity-auto; location: —; invoke: update
- Default entity: DataCategory; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| categoryName | name | IN | true |
| dataCategoryId | id | IN | false |
| parentCategoryId | id | IN | true |

## updateDataResource

- Definition: `applications/content/servicedef/services_data.xml:55`
- Engine: entity-auto; location: —; invoke: update
- Default entity: DataResource; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| characterSetId | id-long | IN | true |
| createdByUserLogin | id-vlong | IN | true |
| createdDate | date-time | IN | true |
| dataCategoryId | id | IN | true |
| dataResourceId | String | IN | false |
| dataResourceName | value | IN | true |
| dataResourceTypeId | id | IN | true |
| dataSourceId | id | IN | true |
| dataTemplateTypeId | id | IN | true |
| isPublic | indicator | IN | true |
| lastModifiedByUserLogin | id-vlong | IN | true |
| lastModifiedDate | date-time | IN | true |
| localeString | very-short | IN | true |
| mimeTypeId | id-vlong | IN | true |
| objectInfo | long-varchar | IN | true |
| relatedDetailId | id | IN | true |
| statusId | id | IN | true |
| surveyId | id | IN | true |
| surveyResponseId | id | IN | true |

## updateDataResourceAndText

- Definition: `applications/content/servicedef/services_data.xml:100`
- Engine: java; location: org.apache.ofbiz.content.data.DataServices; invoke: updateDataResourceAndText
- Default entity: DataResource; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| characterSetId | id-long | IN | true |
| contentPurposeList | List | IN | true |
| createdByUserLogin | id-vlong | IN | true |
| createdDate | date-time | IN | true |
| dataCategoryId | id | IN | true |
| dataResourceId | id | IN | true |
| dataResourceName | value | IN | true |
| dataResourceTypeId | id | IN | true |
| dataSourceId | id | IN | true |
| dataTemplateTypeId | id | IN | true |
| isPublic | indicator | IN | true |
| lastModifiedByUserLogin | id-vlong | IN | true |
| lastModifiedDate | date-time | IN | true |
| localeString | very-short | IN | true |
| mimeTypeId | id-vlong | IN | true |
| objectInfo | long-varchar | IN | true |
| relatedDetailId | id | IN | true |
| skipPermissionCheck | String | IN | true |
| statusId | id | IN | true |
| surveyId | id | IN | true |
| surveyResponseId | id | IN | true |
| targetOperationList | List | IN | true |
| textData | String | IN | true |

## updateDataResourceAttribute

- Definition: `applications/content/servicedef/services.xml:85`
- Engine: entity-auto; location: —; invoke: update
- Default entity: DataResourceAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | false |
| attrValue | value | IN | true |
| dataResourceId | id | IN | false |

## updateDataResourceMetaData

- Definition: `applications/content/servicedef/services_data.xml:244`
- Engine: entity-auto; location: —; invoke: update
- Default entity: DataResourceMetaData; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceId | id | IN | false |
| dataSourceId | id | IN | true |
| metaDataPredicateId | id | IN | false |
| metaDataValue | value | IN | true |

## updateDataResourcePurpose

- Definition: `applications/content/servicedef/services_data.xml:263`
- Engine: entity-auto; location: —; invoke: update
- Default entity: DataResourcePurpose; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentPurposeTypeId | id | IN | false |
| dataResourceId | id | IN | false |

## updateDataResourceRole

- Definition: `applications/content/servicedef/services.xml:107`
- Engine: entity-auto; location: —; invoke: update
- Default entity: DataResourceRole; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceId | id | IN | false |
| fromDate | date-time | IN | false |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |
| thruDate | date-time | IN | true |

## updateDataResourceType

- Definition: `applications/content/servicedef/services_contenttypes.xml:141`
- Engine: entity-auto; location: —; invoke: update
- Default entity: DataResourceType; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceTypeId | id | IN | false |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## updateDataResourceTypeAttr

- Definition: `applications/content/servicedef/services_contenttypes.xml:160`
- Engine: entity-auto; location: —; invoke: update
- Default entity: DataResourceTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | false |
| dataResourceTypeId | id | IN | false |
| description | description | IN | true |

## updateDataTemplateType

- Definition: `applications/content/servicedef/services_data.xml:329`
- Engine: entity-auto; location: —; invoke: update
- Default entity: DataTemplateType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataTemplateTypeId | id | IN | — |
| description | description | IN | true |
| extension | short-varchar | IN | true |

## updateDataText

- Definition: `applications/content/servicedef/services_data.xml:82`
- Engine: route; location: —; invoke: —
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "updateDataResource"}, {"service": "updateElectronicText"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## updateDocument

- Definition: `applications/content/servicedef/services_document.xml:48`
- Engine: entity-auto; location: —; invoke: update
- Default entity: Document; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| dateCreated | date-time | IN | true |
| documentId | id | IN | — |
| documentLocation | long-varchar | IN | true |
| documentText | long-varchar | IN | true |
| documentTypeId | id | IN | true |
| imageData | object | IN | true |

## updateDocumentAttribute

- Definition: `applications/content/servicedef/services_document.xml:32`
- Engine: entity-auto; location: —; invoke: update
- Default entity: DocumentAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | — |
| attrValue | value | IN | true |
| documentId | id | IN | — |

## updateDocumentType

- Definition: `applications/content/servicedef/services_document.xml:64`
- Engine: entity-auto; location: —; invoke: update
- Default entity: DocumentType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| documentTypeId | id | IN | — |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## updateDocumentTypeAttr

- Definition: `applications/content/servicedef/services_document.xml:80`
- Engine: entity-auto; location: —; invoke: update
- Default entity: DocumentTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| description | description | IN | true |
| documentTypeId | id | IN | — |

## updateDownloadContent

- Definition: `applications/content/servicedef/services.xml:156`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy; invoke: updateDownloadContent
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| file | String | IN | true |
| fileDataResourceId | String | IN | true |

## updateElectronicText

- Definition: `applications/content/servicedef/services_data.xml:122`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ElectronicText; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | String | INOUT | true |
| dataResourceId | id | INOUT | false |
| textData | very-long | IN | true |

## updateElectronicTextForm

- Definition: `applications/content/servicedef/services_data.xml:139`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ElectronicText; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | String | INOUT | true |
| dataResourceId | id | INOUT | false |
| textData | very-long | IN | true |

## updateEmailContent

- Definition: `applications/content/servicedef/services.xml:136`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy; invoke: updateEmailContent
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| htmlBody | String | IN | true |
| htmlBodyDataResourceId | String | IN | true |
| plainBody | String | IN | true |
| plainBodyDataResourceId | String | IN | true |
| subject | String | IN | true |
| subjectDataResourceId | String | IN | true |

## updateFile

- Definition: `applications/content/servicedef/services_data.xml:302`
- Engine: java; location: org.apache.ofbiz.content.data.DataServices; invoke: updateFile
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| binData | java.nio.ByteBuffer | IN | true |
| dataResource | org.apache.ofbiz.entity.GenericValue | IN | true |
| dataResourceTypeId | String | IN | true |
| objectInfo | String | IN | true |
| rootDir | String | IN | true |
| textData | String | IN | true |

## updateFileExtension

- Definition: `applications/content/servicedef/services_contenttypes.xml:179`
- Engine: entity-auto; location: —; invoke: update
- Default entity: FileExtension; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fileExtensionId | id-long | IN | false |
| mimeTypeId | id-vlong | IN | true |

## updateImage

- Definition: `applications/content/servicedef/services.xml:630`
- Engine: java; location: org.apache.ofbiz.content.data.DataServices; invoke: updateImage
- Default entity: —; auth: —
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceId | String | IN | false |
| imageData | java.nio.ByteBuffer | IN | false |

## updateImageDataResource

- Definition: `applications/content/servicedef/services_data.xml:168`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ImageDataResource; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceId | id | IN | false |
| imageData | byte-array | IN | true |

## updateMetaDataPredicate

- Definition: `applications/content/servicedef/services_contenttypes.xml:198`
- Engine: entity-auto; location: —; invoke: update
- Default entity: MetaDataPredicate; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| metaDataPredicateId | id | IN | false |

## updateMimeType

- Definition: `applications/content/servicedef/services_contenttypes.xml:216`
- Engine: entity-auto; location: —; invoke: update
- Default entity: MimeType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| mimeTypeId | id-vlong | IN | false |

## updateMimeTypeHtmlTemplate

- Definition: `applications/content/servicedef/services_contenttypes.xml:232`
- Engine: entity-auto; location: —; invoke: update
- Default entity: MimeTypeHtmlTemplate; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| mimeTypeId | id-vlong | IN | false |
| templateLocation | long-varchar | IN | true |

## updateOtherDataResource

- Definition: `applications/content/servicedef/services_data.xml:225`
- Engine: entity-auto; location: —; invoke: update
- Default entity: OtherDataResource; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceContent | byte-array | IN | true |
| dataResourceId | id | IN | false |

## updatePageType

- Definition: `applications/content/servicedef/services.xml:543`
- Engine: java; location: org.apache.ofbiz.content.ContentManagementServices; invoke: updatePageType
- Default entity: —; auth: true
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | String | IN | false |
| pageMode | String | IN | true |

## updateSimpleTextContent

- Definition: `applications/content/servicedef/services.xml:172`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy; invoke: updateSimpleTextContent
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| text | String | IN | true |
| textDataResourceId | String | IN | true |

## updateSingleContentPurpose

- Definition: `applications/content/servicedef/services_content.xml:426`
- Engine: groovy; location: component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy; invoke: updateSingleContentPurpose
- Default entity: ContentPurpose; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| contentPurposeTypeId | id | IN | false |
| sequenceNum | numeric | IN | true |

## updateSiteRoles

- Definition: `applications/content/servicedef/services.xml:459`
- Engine: java; location: org.apache.ofbiz.content.ContentManagementServices; invoke: updateSiteRoles
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| blogAdmin | String | IN | true |
| blogAdminFromDate | Timestamp | IN | true |
| blogAuthor | String | IN | true |
| blogAuthorFromDate | Timestamp | IN | true |
| blogEditor | String | IN | true |
| blogEditorFromDate | Timestamp | IN | true |
| blogPublisher | String | IN | true |
| blogPublisherFromDate | Timestamp | IN | true |
| blogUser | String | IN | true |
| blogUserFromDate | Timestamp | IN | true |
| contentId | String | IN | true |
| partyId | String | IN | true |

## updateSiteRolesDyn

- Definition: `applications/content/servicedef/services.xml:496`
- Engine: java; location: org.apache.ofbiz.content.ContentManagementServices; invoke: updateSiteRolesDyn
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | String | IN | true |
| partyId | String | IN | true |

## updateSurvey

- Definition: `applications/content/servicedef/services_survey.xml:33`
- Engine: entity-auto; location: —; invoke: update
- Default entity: Survey; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acroFormContentId | id | IN | true |
| allowMultiple | indicator | IN | true |
| allowUpdate | indicator | IN | true |
| comments | comment | IN | true |
| description | description | IN | true |
| isAnonymous | indicator | IN | true |
| responseService | long-varchar | IN | true |
| submitCaption | short-varchar | IN | true |
| surveyId | id | IN | false |
| surveyName | name | IN | true |

## updateSurveyApplType

- Definition: `applications/content/servicedef/services_survey.xml:123`
- Engine: entity-auto; location: —; invoke: update
- Default entity: SurveyApplType; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| surveyApplTypeId | id | IN | false |

## updateSurveyMultiResp

- Definition: `applications/content/servicedef/services_survey.xml:63`
- Engine: entity-auto; location: —; invoke: update
- Default entity: SurveyMultiResp; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| multiRespTitle | name | IN | true |
| surveyId | id | IN | false |
| surveyMultiRespId | id | IN | false |

## updateSurveyMultiRespColumn

- Definition: `applications/content/servicedef/services_survey.xml:84`
- Engine: entity-auto; location: —; invoke: update
- Default entity: SurveyMultiRespColumn; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| columnTitle | name | IN | true |
| sequenceNum | numeric | IN | true |
| surveyId | id | IN | false |
| surveyMultiRespColId | id | IN | false |
| surveyMultiRespId | id | IN | false |

## updateSurveyPage

- Definition: `applications/content/servicedef/services_survey.xml:104`
- Engine: entity-auto; location: —; invoke: update
- Default entity: SurveyPage; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| pageName | name | IN | true |
| sequenceNum | numeric | IN | true |
| surveyId | id | IN | false |
| surveyPageSeqId | id | IN | false |

## updateSurveyQuestion

- Definition: `applications/content/servicedef/services_survey.xml:144`
- Engine: simple; location: component://content/minilang/survey/SurveyServices.xml; invoke: updateSurveyQuestion
- Default entity: SurveyQuestion; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| enumTypeId | id | IN | true |
| formatString | value | IN | true |
| geoId | id | IN | true |
| hint | very-long | IN | true |
| question | very-long | IN | true |
| surveyQuestionCategoryId | id | IN | true |
| surveyQuestionId | id | IN | false |
| surveyQuestionTypeId | id | IN | true |

## updateSurveyQuestionAppl

- Definition: `applications/content/servicedef/services_survey.xml:185`
- Engine: entity-auto; location: —; invoke: update
- Default entity: SurveyQuestionAppl; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| externalFieldRef | long-varchar | IN | true |
| fromDate | date-time | IN | false |
| requiredField | indicator | IN | true |
| sequenceNum | numeric | IN | true |
| surveyId | id | IN | false |
| surveyMultiRespColId | id | IN | true |
| surveyMultiRespId | id | IN | true |
| surveyPageSeqId | id | IN | true |
| surveyQuestionId | id | IN | false |
| thruDate | date-time | IN | true |
| withSurveyOptionSeqId | id | IN | true |
| withSurveyQuestionId | id | IN | true |

## updateSurveyQuestionCategory

- Definition: `applications/content/servicedef/services_survey.xml:204`
- Engine: entity-auto; location: —; invoke: update
- Default entity: SurveyQuestionCategory; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| parentCategoryId | id | IN | true |
| surveyQuestionCategoryId | id | IN | false |

## updateSurveyQuestionOption

- Definition: `applications/content/servicedef/services_survey.xml:165`
- Engine: entity-auto; location: —; invoke: update
- Default entity: SurveyQuestionOption; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amountBase | currency-amount | IN | true |
| amountBaseUomId | id | IN | true |
| description | description | IN | true |
| duration | numeric | IN | true |
| durationUomId | id | IN | true |
| sequenceNum | numeric | IN | true |
| surveyOptionSeqId | id | IN | false |
| surveyQuestionId | id | IN | false |
| weightFactor | floating-point | IN | true |

## updateSurveyQuestionType

- Definition: `applications/content/servicedef/services_survey.xml:223`
- Engine: entity-auto; location: —; invoke: update
- Default entity: SurveyQuestionType; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| surveyQuestionTypeId | id | IN | false |

## updateSurveyTrigger

- Definition: `applications/content/servicedef/services_survey.xml:243`
- Engine: entity-auto; location: —; invoke: update
- Default entity: SurveyTrigger; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| surveyApplTypeId | id | IN | false |
| surveyId | id | IN | false |
| thruDate | date-time | IN | true |

## updateTextContent

- Definition: `applications/content/servicedef/services_content.xml:153`
- Engine: group; location: —; invoke: —
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## updateVideoDataResource

- Definition: `applications/content/servicedef/services_data.xml:187`
- Engine: entity-auto; location: —; invoke: update
- Default entity: VideoDataResource; auth: true
- Use transaction: —; timeout: —
- Permission service: genericDataResourcePermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| dataResourceId | id | IN | false |
| videoData | byte-array | IN | true |

## updateWebPreferenceType

- Definition: `applications/content/servicedef/services.xml:917`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WebPreferenceType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| webPreferenceTypeId | id | IN | — |

## updateWebSite

- Definition: `applications/content/servicedef/services_website.xml:33`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WebSite; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| cookieDomain | long-varchar | IN | true |
| enableHttps | indicator | IN | true |
| httpHost | long-varchar | IN | true |
| httpPort | very-short | IN | true |
| httpsHost | long-varchar | IN | true |
| httpsPort | very-short | IN | true |
| secureContentPrefix | url | IN | true |
| siteName | name | IN | true |
| standardContentPrefix | url | IN | true |
| visualThemeSetId | id | IN | true |
| webSiteId | id | IN | false |
| webappPath | long-varchar | IN | true |

## updateWebSiteContent

- Definition: `applications/content/servicedef/services_website.xml:48`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WebSiteContent; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| fromDate | date-time | IN | false |
| thruDate | date-time | IN | true |
| webSiteContentTypeId | id | IN | false |
| webSiteId | id | IN | false |

## updateWebSiteContentType

- Definition: `applications/content/servicedef/services_website.xml:67`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WebSiteContentType; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| webSiteContentTypeId | id | IN | false |

## updateWebSitePathAlias

- Definition: `applications/content/servicedef/services_website.xml:86`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WebSitePathAlias; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| aliasTo | long-varchar | IN | true |
| contentId | id | IN | true |
| fromDate | date-time | IN | false |
| mapKey | name | IN | true |
| pathAlias | id-vlong | IN | false |
| thruDate | date-time | IN | true |
| webSiteId | id | IN | false |

## updateWebSiteRole

- Definition: `applications/content/servicedef/services_website.xml:124`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WebSiteRole; auth: true
- Use transaction: —; timeout: —
- Permission service: contentManagerPermission; main action: UPDATE
- Implements: [{"service": "webSiteRoleInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## uploadFileInterface

- Definition: `applications/content/servicedef/services.xml:27`
- Engine: interface; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| _uploadedFile_contentType | String | IN | true |
| _uploadedFile_fileName | String | IN | true |
| uploadedFile | java.nio.ByteBuffer | IN | true |

## urlEncodeArgs

- Definition: `applications/content/servicedef/services.xml:765`
- Engine: java; location: org.apache.ofbiz.content.content.ContentServices; invoke: urlEncodeArgs
- Default entity: —; auth: false
- Use transaction: —; timeout: 7200
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| mapIn | java.util.Map | IN | false |
| outputString | String | OUT | true |

## webSiteRoleInterface

- Definition: `applications/content/servicedef/services_website.xml:105`
- Engine: interface; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| sequenceNum | — | — | true |
| thruDate | — | — | true |
