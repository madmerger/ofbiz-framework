# content 未移植一覧

`docs/migration/content/inventory/inventory.json`（`migration/tools/ofbiz_inventory.py` で生成）を基に、移植しなかったサービス・画面・view-entity 等を全件列挙する。

## 件数集計

| 区分 | 総数 | 移植 | 未移植 |
| --- | --- | --- | --- |
| サービス（entity-auto） | 176 | 176 | 0 |
| サービス（simple） | 7 | 7 | 0 |
| サービス（java） | 57 | 4 | 53 |
| サービス（groovy） | 37 | 7 | 30 |
| サービス（group） | 6 | 2 | 4 |
| サービス（route） | 2 | 2 | 0 |
| サービス（interface） | 3 | 0 | 3 |
| **サービス合計** | **288** | **198** | **90** |
| controller request-map | 346 | 0 | 346 |
| controller view-map | 134 | 0 | 134 |
| Widget screen | 145 | 0 | 145 |
| Widget form/grid/menu/tree | 199 | 0 | 199 |
| FTL テンプレート | 27 | 0 | 27 |
| view-entity（content-entitymodel.xml） | 12 | 0 | 12 |

移植したサービスの内訳: entity-auto 176（全量）、simple 7（全量）、java 4（createDataResourceAndText, updateDataResourceAndText, getContent, deactivateContentAssoc）、groovy 7（createDataResource, getElectronicText, getDataResource, getContentAndDataResource, createContentFromDataResource, createSimpleTextContent, updateSimpleTextContent）、group 2（createTextContent, updateTextContent）、route 2（createDataText, updateDataText）。

## 未移植サービス

| サービス名 | engine | location | 理由 |
| --- | --- | --- | --- |
| assocContent | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy#assocContent` | deprecated（Release 24.09、genericContentPermission 推奨）。関連付けは entity-auto createContentAssoc で提供 |
| attachUploadToDataResource | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/data/DataServicesScript.groovy#attachUploadToDataResource` | ファイルアップロード / ファイル I/O（スコープ外: ファイルアップロードは未移植） |
| checkOwnership | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/permission/ContentPermissionServices.groovy#checkOwnership` | 権限サービス（Spring Security の authority と ContentPermissionService で代替） |
| contentManagerPermission | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/permission/ContentPermissionServices.groovy#contentManagerPermission` | 権限サービス（Spring Security の authority と ContentPermissionService で代替） |
| contentManagerRolePermission | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/permission/ContentPermissionServices.groovy#contentManagerRolePermission` | 権限サービス（Spring Security の authority と ContentPermissionService で代替） |
| copyContentAndElectronicTextandAssoc | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy#copyContentAndElectronicTextandAssoc` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| createArticleContent | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy#createArticleContent` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| createBlogEntry | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/BlogServices.groovy#createBlogEntry` | ブログ/フォーラム/コミュニケーション連携（主要フロー外） |
| createCommContentDataResource | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy#createCommContentDataResource` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| createContentAlternativeUrl | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy#createContentAlternativeUrl` | WebSite/CMS 公開系（主要フロー外） |
| createDataResourceAndAssocToContent | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/data/DataServicesScript.groovy#createDataResourceAndAssocToContent` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| createDownloadContent | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy#createDownloadContent` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| createEmailContent | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy#createEmailContent` | ブログ/フォーラム/コミュニケーション連携（主要フロー外） |
| createMissingContentAltUrls | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy#createMissingContentAltUrls` | WebSite/CMS 公開系（主要フロー外） |
| createTextAndUploadedContent | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy#createTextAndUploadedContent` | ファイルアップロード / ファイル I/O（スコープ外: ファイルアップロードは未移植） |
| deactivateAllContentRoles | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy#deactivateAllContentRoles` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| deleteContentKeywords | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy#deleteContentKeywords` | 全文検索 Lucene（スコープ外） |
| findAssocContent | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy#findAssocContent` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| forceIndexContentKeywords | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy#forceIndexContentKeywords` | 全文検索 Lucene（スコープ外） |
| genericContentPermission | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/permission/ContentPermissionServices.groovy#genericContentPermission` | 権限サービス（Spring Security の authority と ContentPermissionService で代替） |
| getBlogEntry | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/BlogServices.groovy#getBlogEntry` | ブログ/フォーラム/コミュニケーション連携（主要フロー外） |
| getOwnedOrPublishedBlogEntries | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/BlogServices.groovy#getOwnedOrPublishedBlogEntries` | ブログ/フォーラム/コミュニケーション連携（主要フロー外） |
| indexContentKeywords | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy#indexContentKeywords` | 全文検索 Lucene（スコープ外） |
| removeContentAndRelated | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy#removeContentAndRelated` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| setContentStatus | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy#setContentStatus` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| updateBlogEntry | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/BlogServices.groovy#updateBlogEntry` | ブログ/フォーラム/コミュニケーション連携（主要フロー外） |
| updateCommContentDataResource | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy#updateCommContentDataResource` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| updateDownloadContent | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy#updateDownloadContent` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| updateEmailContent | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy#updateEmailContent` | ブログ/フォーラム/コミュニケーション連携（主要フロー外） |
| updateSingleContentPurpose | groovy | `component://content/src/main/groovy/org/apache/ofbiz/content/content/ContentServicesScript.groovy#updateSingleContentPurpose` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| createContentFromUploadedFile | group | `applications/content/servicedef/services_content.xml` | ファイルアップロード / ファイル I/O（スコープ外: ファイルアップロードは未移植） |
| getPublicForumMessage | group | `applications/content/servicedef/services.xml` | ブログ/フォーラム/コミュニケーション連携（主要フロー外） |
| resequence | group | `applications/content/servicedef/services.xml` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| updateContentAndUploadedFile | group | `applications/content/servicedef/services_content.xml` | ファイルアップロード / ファイル I/O（スコープ外: ファイルアップロードは未移植） |
| surveyResponseProcessInterface | interface | `applications/content/servicedef/services_survey.xml` | interface（属性定義のみで実体なし。継承先の移植サービスに反映） |
| uploadFileInterface | interface | `applications/content/servicedef/services.xml` | interface（属性定義のみで実体なし。継承先の移植サービスに反映） |
| webSiteRoleInterface | interface | `applications/content/servicedef/services_website.xml` | interface（属性定義のみで実体なし。継承先の移植サービスに反映） |
| buildPdfFromSurveyResponse | java | `org.apache.ofbiz.content.survey.PdfSurveyServices#buildPdfFromSurveyResponse` | 描画・出力（FTL/FOP/画面系、Widget 未移植のため対象外） |
| buildSurveyFromPdf | java | `org.apache.ofbiz.content.survey.PdfSurveyServices#buildSurveyFromPdf` | 描画・出力（FTL/FOP/画面系、Widget 未移植のため対象外） |
| buildSurveyQuestionsAndAnswers | java | `org.apache.ofbiz.content.survey.PdfSurveyServices#buildSurveyQuestionsAndAnswers` | 描画・出力（FTL/FOP/画面系、Widget 未移植のため対象外） |
| buildSurveyResponseFromPdf | java | `org.apache.ofbiz.content.survey.PdfSurveyServices#buildSurveyResponseFromPdf` | 描画・出力（FTL/FOP/画面系、Widget 未移植のため対象外） |
| changeLeafToNode | java | `org.apache.ofbiz.content.ContentManagementServices#changeLeafToNode` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| checkAssocPermission | java | `org.apache.ofbiz.content.content.ContentPermissionServices#checkAssocPermission` | 権限サービス（Spring Security の authority と ContentPermissionService で代替） |
| checkContentPermission | java | `org.apache.ofbiz.content.content.ContentPermissionServices#checkContentPermission` | 権限サービス（Spring Security の authority と ContentPermissionService で代替） |
| clearAssociatedRenderCache | java | `org.apache.ofbiz.content.data.DataServices#clearAssociatedRenderCache` | 描画・出力（FTL/FOP/画面系、Widget 未移植のため対象外） |
| clearContentAssocDataResourceViewCache | java | `org.apache.ofbiz.content.ContentManagementServices#clearContentAssocDataResourceViewCache` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| clearContentAssocViewCache | java | `org.apache.ofbiz.content.ContentManagementServices#clearContentAssocViewCache` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| createAnonFile | java | `org.apache.ofbiz.content.data.DataServices#createFileNoPerm` | ファイルアップロード / ファイル I/O（スコープ外: ファイルアップロードは未移植） |
| createFile | java | `org.apache.ofbiz.content.data.DataServices#createFile` | ファイルアップロード / ファイル I/O（スコープ外: ファイルアップロードは未移植） |
| createFileFromScreen | java | `org.apache.ofbiz.content.output.OutputServices#createFileFromScreen` | ファイルアップロード / ファイル I/O（スコープ外: ファイルアップロードは未移植） |
| createImage | java | `org.apache.ofbiz.content.data.DataServices#createImage` | 画像変換（スコープ外） |
| createOrRemoveProductFeatureDataResource | java | `org.apache.ofbiz.content.ContentManagementServices#createOrRemoveProductFeatureDataResource` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| deactivateAssocs | java | `org.apache.ofbiz.content.content.ContentServices#deactivateAssocs` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| decrementContentChildStats | java | `org.apache.ofbiz.content.ContentManagementServices#decrementContentChildStats` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| findRelatedContent | java | `org.apache.ofbiz.content.content.ContentServices#findRelatedContent` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| findSubNodes | java | `org.apache.ofbiz.content.ContentManagementServices#findSubNodes` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| followNodeChildren | java | `org.apache.ofbiz.content.ContentManagementServices#followNodeChildren` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| generateBlogRssFeed | java | `org.apache.ofbiz.content.blog.BlogRssServices#generateBlogRssFeed` | ブログ/フォーラム/コミュニケーション連携（主要フロー外） |
| getAcroFieldsFromPdf | java | `org.apache.ofbiz.content.survey.PdfSurveyServices#getAcroFieldsFromPdf` | 描画・出力（FTL/FOP/画面系、Widget 未移植のため対象外） |
| getAssocAndContentAndDataResource | java | `org.apache.ofbiz.content.content.ContentServicesComplex#getAssocAndContentAndDataResource` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| getAssocAndContentAndDataResourceCache | java | `org.apache.ofbiz.content.content.ContentServicesComplex#getAssocAndContentAndDataResourceCache` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| getPrefixedMembers | java | `org.apache.ofbiz.content.content.ContentServices#getPrefixedMembers` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| getSubContent | java | `org.apache.ofbiz.content.ContentManagementServices#getSubContent` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| incrementContentChildStats | java | `org.apache.ofbiz.content.ContentManagementServices#incrementContentChildStats` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| initContentChildCounts | java | `org.apache.ofbiz.content.ContentManagementServices#initContentChildCounts` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| joinString | java | `org.apache.ofbiz.content.content.ContentServices#joinString` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| linkContentToPubPt | java | `org.apache.ofbiz.content.content.ContentServices#linkContentToPubPt` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| persistContentAndAssoc | java | `org.apache.ofbiz.content.ContentManagementServices#persistContentAndAssoc` | 画像バイナリ・アップロード・ロール・目的を一括処理する複合サービス。主要フローは createTextContent/createContentAssoc で代替 |
| persistDataResourceAndData | java | `org.apache.ofbiz.content.ContentManagementServices#persistDataResourceAndData` | 画像バイナリ・アップロード・ロール・目的を一括処理する複合サービス。主要フローは createTextContent/createContentAssoc で代替 |
| publishContent | java | `org.apache.ofbiz.content.content.ContentServices#publishContent` | WebSite/CMS 公開系（主要フロー外） |
| renderContentAsText | java | `org.apache.ofbiz.content.content.ContentServices#renderContentAsText` | 描画・出力（FTL/FOP/画面系、Widget 未移植のため対象外） |
| renderDataResourceAsText | java | `org.apache.ofbiz.content.data.DataServices#renderDataResourceAsText` | 描画・出力（FTL/FOP/画面系、Widget 未移植のため対象外） |
| renderSubContentAsText | java | `org.apache.ofbiz.content.content.ContentServices#renderSubContentAsText` | 描画・出力（FTL/FOP/画面系、Widget 未移植のため対象外） |
| resequenceContentAssocChildren | java | `org.apache.ofbiz.content.ContentManagementServices#resequence` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| resetToOutlineMode | java | `org.apache.ofbiz.content.ContentManagementServices#resetToOutlineMode` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| sendContentToFtp | java | `org.apache.ofbiz.content.ftp.FtpServices#sendContentToFtp` | ファイルアップロード / ファイル I/O（スコープ外: ファイルアップロードは未移植） |
| sendPrintFromScreen | java | `org.apache.ofbiz.content.output.OutputServices#sendPrintFromScreen` | 描画・出力（FTL/FOP/画面系、Widget 未移植のため対象外） |
| setAcroFields | java | `org.apache.ofbiz.content.survey.PdfSurveyServices#setAcroFields` | 描画・出力（FTL/FOP/画面系、Widget 未移植のため対象外） |
| setAcroFieldsFromSurveyResponse | java | `org.apache.ofbiz.content.survey.PdfSurveyServices#setAcroFieldsFromSurveyResponse` | 描画・出力（FTL/FOP/画面系、Widget 未移植のため対象外） |
| splitString | java | `org.apache.ofbiz.content.content.ContentServices#splitString` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| updateContentChildStats | java | `org.apache.ofbiz.content.ContentManagementServices#updateContentChildStats` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| updateContentSubscription | java | `org.apache.ofbiz.content.ContentManagementServices#updateContentSubscription` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| updateContentSubscriptionByOrder | java | `org.apache.ofbiz.content.ContentManagementServices#updateContentSubscriptionByOrder` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| updateContentSubscriptionByProduct | java | `org.apache.ofbiz.content.ContentManagementServices#updateContentSubscriptionByProduct` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| updateFile | java | `org.apache.ofbiz.content.data.DataServices#updateFile` | ファイルアップロード / ファイル I/O（スコープ外: ファイルアップロードは未移植） |
| updateImage | java | `org.apache.ofbiz.content.data.DataServices#updateImage` | 画像変換（スコープ外） |
| updatePageType | java | `org.apache.ofbiz.content.ContentManagementServices#updatePageType` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| updateSiteRoles | java | `org.apache.ofbiz.content.ContentManagementServices#updateSiteRoles` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| updateSiteRolesDyn | java | `org.apache.ofbiz.content.ContentManagementServices#updateSiteRolesDyn` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |
| urlEncodeArgs | java | `org.apache.ofbiz.content.content.ContentServices#urlEncodeArgs` | 主要フロー（テキスト DataResource→Content→Assoc→取得）外 |

## 未移植 view-entity

Spring Boot 側の共通 datamodel は通常 Entity のみを JPA 化しているため、view-entity は未移植。移植サービスで必要なもの（`ContentDataResourceView`、`SurveyQuestionAndAppl`）は Java 側で結合して同等の結果を返す。

| view-entity | 定義 | 備考 |
| --- | --- | --- |
| ContentAssocOptViewFrom | `applications/datamodel/entitydef/content-entitymodel.xml:42` | 未使用 |
| ContentAndRole | `applications/datamodel/entitydef/content-entitymodel.xml:161` | 未使用 |
| ContentAssocDataResourceViewFrom | `applications/datamodel/entitydef/content-entitymodel.xml:255` | 未使用 |
| ContentAssocDataResourceViewTo | `applications/datamodel/entitydef/content-entitymodel.xml:298` | 未使用 |
| ContentAssocViewFrom | `applications/datamodel/entitydef/content-entitymodel.xml:358` | 未使用 |
| ContentAssocViewTo | `applications/datamodel/entitydef/content-entitymodel.xml:372` | 未使用 |
| ContentDataResourceView | `applications/datamodel/entitydef/content-entitymodel.xml:402` | getContent の view を Content + dr 接頭辞の DataResource で再現 |
| SubContentDataResourceView | `applications/datamodel/entitydef/content-entitymodel.xml:611` | 未使用 |
| DataResourceContentView | `applications/datamodel/entitydef/content-entitymodel.xml:764` | 未使用 |
| SurveyQuestionAndAppl | `applications/datamodel/entitydef/content-entitymodel.xml:1187` | 未使用 |
| SurveyResponseAndAnswer | `applications/datamodel/entitydef/content-entitymodel.xml:1341` | 未使用 |
| WebSiteAndContent | `applications/datamodel/entitydef/content-entitymodel.xml:1461` | 未使用 |

## 未移植 画面（controller view-map）

画面（Widget/FTL）は REST API と Swagger UI で代替し、移植しない。

| view-map | type | page |
| --- | --- | --- |
| AddDataResource | screen | `component://content/widget/content/DataResourceScreens.xml#AddDataResource` |
| AddDataResourceFromContent | screen | `component://content/widget/content/DataResourceScreens.xml#AddDataResourceFromContent` |
| AddDataResourceText | screen | `component://content/widget/content/DataResourceScreens.xml#AddDataResourceText` |
| AddDataResourceUpload | screen | `component://content/widget/content/DataResourceScreens.xml#AddDataResourceUpload` |
| AddDataResourceUrl | screen | `component://content/widget/content/DataResourceScreens.xml#AddDataResourceUrl` |
| AddForumMessage | screen | `component://content/widget/forum/ForumScreens.xml#AddForumMessage` |
| AddForumThreadMessage | screen | `component://content/widget/forum/ForumScreens.xml#AddForumThreadMessage` |
| AddLayout | screen | `component://content/widget/layout/LayoutScreens.xml#AddLayout` |
| CMSContentEdit | screen | `component://content/widget/cms/CMSScreens.xml#CMSContentEdit` |
| CMSContentFind | screen | `component://content/widget/cms/CMSScreens.xml#CMSContentFind` |
| CMSSites | screen | `component://content/widget/cms/CMSScreens.xml#CMSSites` |
| ContentSearchOptions | screen | `component://content/widget/content/ContentScreens.xml#ContentSearchOptions` |
| ContentSearchResults | screen | `component://content/widget/content/ContentScreens.xml#ContentSearchResults` |
| EditAddContent | screen | `component://content/widget/cms/CMSScreens.xml#EditAddContent` |
| EditAddSubContent | screen | `component://content/widget/cms/CMSScreens.xml#EditAddSubContent` |
| EditBlog | screen | `component://content/widget/forum/BlogScreens.xml#EditBlog` |
| EditBlogArticle | screen | `component://content/widget/forum/BlogScreens.xml#EditArticle` |
| EditBlogImage | screen | `component://ecommerce/widget/blog/BlogScreens.xml#EditBlogResponse` |
| EditBlogResponse | screen | `component://ecommerce/widget/blog/BlogScreens.xml#EditBlogResponse` |
| EditBlogText | screen | `component://ecommerce/widget/blog/BlogScreens.xml#EditBlogResponse` |
| EditCharacterSet | screen | `component://content/widget/datasetup/DataResourceSetupScreens.xml#EditCharacterSet` |
| EditContent | screen | `component://content/widget/content/ContentScreens.xml#EditContent` |
| EditContentAssoc | screen | `component://content/widget/content/ContentScreens.xml#EditContentAssoc` |
| EditContentAssocPredicate | screen | `component://content/widget/contentsetup/ContentSetupScreens.xml#EditContentAssocPredicate` |
| EditContentAssocType | screen | `component://content/widget/contentsetup/ContentSetupScreens.xml#EditContentAssocType` |
| EditContentAttribute | screen | `component://content/widget/content/ContentScreens.xml#EditContentAttribute` |
| EditContentKeywords | screen | `component://content/widget/content/ContentScreens.xml#EditContentKeywords` |
| EditContentMetaData | screen | `component://content/widget/content/ContentScreens.xml#EditContentMetaData` |
| EditContentOperation | screen | `component://content/widget/contentsetup/ContentSetupScreens.xml#EditContentOperation` |
| EditContentPurpose | screen | `component://content/widget/content/ContentScreens.xml#EditContentPurpose` |
| EditContentPurposeOperation | screen | `component://content/widget/contentsetup/ContentSetupScreens.xml#EditContentPurposeOperation` |
| EditContentPurposeType | screen | `component://content/widget/contentsetup/ContentSetupScreens.xml#EditContentPurposeType` |
| EditContentRole | screen | `component://content/widget/content/ContentScreens.xml#EditContentRole` |
| EditContentType | screen | `component://content/widget/contentsetup/ContentSetupScreens.xml#EditContentType` |
| EditContentTypeAttr | screen | `component://content/widget/contentsetup/ContentSetupScreens.xml#EditContentTypeAttr` |
| EditContentWorkEfforts | screen | `component://content/widget/content/ContentScreens.xml#EditContentWorkEfforts` |
| EditDataCategory | screen | `component://content/widget/datasetup/DataResourceSetupScreens.xml#EditDataCategory` |
| EditDataResource | screen | `component://content/widget/content/DataResourceScreens.xml#EditDataResource` |
| EditDataResourceAttribute | screen | `component://content/widget/content/DataResourceScreens.xml#EditDataResourceAttribute` |
| EditDataResourceProductFeatures | screen | `component://content/widget/content/DataResourceScreens.xml#EditDataResourceProductFeatures` |
| EditDataResourceRole | screen | `component://content/widget/content/DataResourceScreens.xml#EditDataResourceRole` |
| EditDataResourceText | screen | `component://content/widget/content/DataResourceScreens.xml#EditDataResourceText` |
| EditDataResourceType | screen | `component://content/widget/datasetup/DataResourceSetupScreens.xml#EditDataResourceType` |
| EditDataResourceTypeAttr | screen | `component://content/widget/datasetup/DataResourceSetupScreens.xml#EditDataResourceTypeAttr` |
| EditDataResourceUpload | screen | `component://content/widget/content/DataResourceScreens.xml#EditDataResourceUpload` |
| EditDataResourceUrl | screen | `component://content/widget/content/DataResourceScreens.xml#EditDataResourceUrl` |
| EditDocument | screen | `component://content/widget/content/ContentScreens.xml#EditDocument` |
| EditDocumentTree | screen | `component://content/widget/content/ContentScreens.xml#EditDocumentTree` |
| EditElectronicText | screen | `component://content/widget/content/DataResourceScreens.xml#EditElectronicText` |
| EditFileExtension | screen | `component://content/widget/datasetup/DataResourceSetupScreens.xml#EditFileExtension` |
| EditForumMessage | screen | `component://content/widget/forum/ForumScreens.xml#EditForumMessage` |
| EditHtmlText | screen | `component://content/widget/content/DataResourceScreens.xml#EditHtmlText` |
| EditLayout | screen | `component://content/widget/layout/LayoutScreens.xml#EditLayout` |
| EditLayoutHtml | screen | `component://content/widget/layout/LayoutScreens.xml#EditLayoutHtml` |
| EditLayoutImage | screen | `component://content/widget/layout/LayoutScreens.xml#EditLayoutImage` |
| EditLayoutSubContent | screen | `component://content/widget/layout/LayoutScreens.xml#EditLayoutSubContent` |
| EditLayoutText | screen | `component://content/widget/layout/LayoutScreens.xml#EditLayoutText` |
| EditLayoutUrl | screen | `component://content/widget/layout/LayoutScreens.xml#EditLayoutUrl` |
| EditMetaDataPredicate | screen | `component://content/widget/datasetup/DataResourceSetupScreens.xml#EditMetaDataPredicate` |
| EditMimeType | screen | `component://content/widget/datasetup/DataResourceSetupScreens.xml#EditMimeType` |
| EditMimeTypeHtmlTemplate | screen | `component://content/widget/datasetup/DataResourceSetupScreens.xml#EditMimeTypeHtmlTemplate` |
| EditSurvey | screen | `component://content/widget/SurveyScreens.xml#EditSurvey` |
| EditSurveyMultiResps | screen | `component://content/widget/SurveyScreens.xml#EditSurveyMultiResps` |
| EditSurveyQuestions | screen | `component://content/widget/SurveyScreens.xml#EditSurveyQuestions` |
| EditSurveyResponse | screen | `component://content/widget/SurveyScreens.xml#EditSurveyResponse` |
| EditWebAnalyticsConfig | screen | `component://content/widget/WebAnalyticsScreens.xml#EditWebAnalyticsConfig` |
| EditWebSite | screen | `component://content/widget/WebSiteScreens.xml#EditWebSite` |
| EditWebSiteParties | screen | `component://content/widget/WebSiteScreens.xml#EditWebSiteParties` |
| EditWebSitePathAlias | screen | `component://content/widget/WebSiteScreens.xml#EditWebSitePathAlias` |
| FindContent | screen | `component://content/widget/content/ContentScreens.xml#FindContent` |
| FindDataResource | screen | `component://content/widget/content/DataResourceScreens.xml#FindDataResource` |
| FindForumGroups | screen | `component://content/widget/forum/ForumScreens.xml#FindForumGroups` |
| FindForumMessages | screen | `component://content/widget/forum/ForumScreens.xml#FindForumMessages` |
| FindForumThreads | screen | `component://content/widget/forum/ForumScreens.xml#FindForumThreads` |
| FindForums | screen | `component://content/widget/forum/ForumScreens.xml#FindForums` |
| FindLayout | screen | `component://content/widget/layout/LayoutScreens.xml#FindLayout` |
| FindSurvey | screen | `component://content/widget/SurveyScreens.xml#FindSurvey` |
| FindSurveyResponse | screen | `component://content/widget/SurveyScreens.xml#FindSurveyResponse` |
| FindWebAnalyticsConfigs | screen | `component://content/widget/WebAnalyticsScreens.xml#FindWebAnalyticsConfigs` |
| FindWebSite | screen | `component://content/widget/WebSiteScreens.xml#FindWebSite` |
| ForumGroupPurposes | screen | `component://content/widget/forum/ForumScreens.xml#ForumGroupPurposes` |
| ForumGroupRoles | screen | `component://content/widget/forum/ForumScreens.xml#ForumGroupRoles` |
| LatestResponses | screen | `component://ecommerce/widget/blog/BlogScreens.xml#LatestResponses` |
| ListContentTree | screen | `component://content/widget/content/ContentScreens.xml#ListContentTree` |
| ListDocument | screen | `component://content/widget/content/ContentScreens.xml#ListDocument` |
| ListFindSurveySearchResults | screen | `component://content/widget/SurveyScreens.xml#ListFindSurveySearchResults` |
| ListLayout | screen | `component://content/widget/layout/LayoutScreens.xml#ListLayout` |
| ListWebSite | screen | `component://content/widget/content/ContentScreens.xml#ListWebSite` |
| LookupContent | screen | `component://content/widget/content/ContentScreens.xml#LookupContent` |
| LookupDataResource | screen | `component://content/widget/content/DataResourceScreens.xml#LookupDataResource` |
| LookupDetailContentTree | screen | `component://content/widget/content/ContentScreens.xml#LookupDetailContentTree` |
| LookupListLayout | screen | `component://content/widget/LookupScreens.xml#LookupListLayout` |
| LookupPartyAndUserLoginAndPerson | screen | `component://party/widget/partymgr/LookupScreens.xml#LookupPartyAndUserLoginAndPerson` |
| LookupPartyName | screen | `component://party/widget/partymgr/LookupScreens.xml#LookupPartyName` |
| LookupPerson | screen | `component://party/widget/partymgr/LookupScreens.xml#LookupPerson` |
| LookupProductFeature | screen | `component://product/widget/catalog/LookupScreens.xml#LookupProductFeature` |
| LookupSubContent | screen | `component://content/widget/LookupScreens.xml#LookupSubContent` |
| LookupSurvey | screen | `component://content/widget/SurveyScreens.xml#LookupSurvey` |
| LookupSurveyResponse | screen | `component://content/widget/SurveyScreens.xml#LookupSurveyResponse` |
| LookupTreeContent | screen | `component://content/widget/content/ContentScreens.xml#LookupContentTree` |
| LookupUserLoginAndPartyDetails | screen | `component://party/widget/partymgr/LookupScreens.xml#LookupUserLoginAndPartyDetails` |
| LookupWorkEffort | screen | `component://workeffort/widget/LookupScreens.xml#LookupWorkEffort` |
| RespondBlog | screen | `component://ecommerce/widget/blog/BlogScreens.xml#AddBlogResponse` |
| UploadImage | screen | `component://content/widget/content/DataResourceScreens.xml#UploadImage` |
| UserPermissions | screen | `component://content/widget/contentsetup/ContentSetupScreens.xml#UserPermissions` |
| ViewBinaryDataResource | simplecontent | `` |
| ViewBlogArticle | screen | `component://content/widget/forum/BlogScreens.xml#ViewArticle` |
| ViewContentDetail | screen | `component://content/widget/content/ContentScreens.xml#ViewContentDetail` |
| ViewResponse | screen | `component://content/widget/forum/BlogScreens.xml#BlogMain` |
| ViewSimpleContent | simplecontent | `` |
| ViewSurveyResponses | screen | `component://content/widget/SurveyScreens.xml#ViewSurveyResponses` |
| WebSiteAliases | screen | `component://content/widget/WebSiteScreens.xml#WebSiteAliases` |
| WebSiteAliasesSearchResults | screen | `component://content/widget/WebSiteScreens.xml#WebSiteAliasesSearchResults` |
| WebSiteCMS | screen | `component://content/widget/WebSiteScreens.xml#WebSiteCMS` |
| WebSiteCMSContent | screen | `component://content/widget/WebSiteScreens.xml#WebSiteCMSContent` |
| WebSiteCMSEditor | screen | `component://content/widget/WebSiteScreens.xml#WebSiteCMSEditor` |
| WebSiteCMSMetaInfo | screen | `component://content/widget/WebSiteScreens.xml#WebSiteCMSMetaInfo` |
| WebSiteCMSNav | screen | `component://content/widget/WebSiteScreens.xml#WebSiteCMSNav` |
| WebSiteCMSPathAlias | screen | `component://content/widget/WebSiteScreens.xml#WebSiteCMSPathAlias` |
| WebSiteContactList | screen | `component://content/widget/WebSiteScreens.xml#WebSiteContactList` |
| WebSiteContent | screen | `component://content/widget/WebSiteScreens.xml#WebSiteContent` |
| WebSiteSEO | screen | `component://content/widget/WebSiteScreens.xml#WebSiteSEO` |
| addSubSite | screen | `component://content/widget/cms/CMSScreens.xml#addSubSite` |
| blogContent | screen | `component://content/widget/forum/BlogScreens.xml#BlogContent` |
| blogMain | screen | `component://content/widget/forum/BlogScreens.xml#BlogMain` |
| findContentSearchResults | screen | `component://content/widget/content/ContentScreens.xml#findContentSearchResults` |
| findDataResourceSearchResults | screen | `component://content/widget/content/DataResourceScreens.xml#findDataResourceSearchResults` |
| fonts.fo | screenfop | `component://content/widget/CommonScreens.xml#fonts.fo` |
| listDataResources | screen | `component://content/widget/content/DataResourceScreens.xml#listDataResources` |
| main | screen | `component://content/widget/CommonScreens.xml#main` |
| navigateContent | screen | `component://content/widget/content/ContentScreens.xml#navigateContent` |
| navigateDataResource | screen | `component://content/widget/content/DataResourceScreens.xml#navigateDataResource` |
| showContent | screen | `component://content/widget/content/ContentScreens.xml#ShowContent` |
| showContentPdf | screen | `component://content/widget/content/ContentScreens.xml#ShowContent` |

## 未移植 Widget screen

| screen | 定義ファイル |
| --- | --- |
| main-decorator | `applications/content/widget/CommonScreens.xml` |
| ShortcutApp | `applications/content/widget/CommonScreens.xml` |
| commonCmsDecorator | `applications/content/widget/CommonScreens.xml` |
| commonContentDecorator | `applications/content/widget/CommonScreens.xml` |
| contentDecorator | `applications/content/widget/CommonScreens.xml` |
| commonDataResourceDecorator | `applications/content/widget/CommonScreens.xml` |
| commonContentSetupDecorator | `applications/content/widget/CommonScreens.xml` |
| commonDataResourceSetupDecorator | `applications/content/widget/CommonScreens.xml` |
| CommonWebSiteDecorator | `applications/content/widget/CommonScreens.xml` |
| CommonWebSitePathAliasDecorator | `applications/content/widget/CommonScreens.xml` |
| commonLayoutDecorator | `applications/content/widget/CommonScreens.xml` |
| main | `applications/content/widget/CommonScreens.xml` |
| responseTreeLine | `applications/content/widget/CommonScreens.xml` |
| fonts.fo | `applications/content/widget/CommonScreens.xml` |
| CommonWebAnalyticsDecorator | `applications/content/widget/CommonScreens.xml` |
| LookupListLayout | `applications/content/widget/LookupScreens.xml` |
| LookupSubContent | `applications/content/widget/LookupScreens.xml` |
| FindSurvey | `applications/content/widget/SurveyScreens.xml` |
| CommonSurveyDecorator | `applications/content/widget/SurveyScreens.xml` |
| EditSurvey | `applications/content/widget/SurveyScreens.xml` |
| EditSurveyMultiResps | `applications/content/widget/SurveyScreens.xml` |
| EditSurveyQuestions | `applications/content/widget/SurveyScreens.xml` |
| FindSurveyResponse | `applications/content/widget/SurveyScreens.xml` |
| ViewSurveyResponses | `applications/content/widget/SurveyScreens.xml` |
| EditSurveyResponse | `applications/content/widget/SurveyScreens.xml` |
| LookupSurvey | `applications/content/widget/SurveyScreens.xml` |
| LookupSurveyResponse | `applications/content/widget/SurveyScreens.xml` |
| ListFindSurveySearchResults | `applications/content/widget/SurveyScreens.xml` |
| FindWebAnalyticsConfigs | `applications/content/widget/WebAnalyticsScreens.xml` |
| EditWebAnalyticsConfig | `applications/content/widget/WebAnalyticsScreens.xml` |
| FindWebSite | `applications/content/widget/WebSiteScreens.xml` |
| EditWebSite | `applications/content/widget/WebSiteScreens.xml` |
| WebSiteContent | `applications/content/widget/WebSiteScreens.xml` |
| EditWebSiteParties | `applications/content/widget/WebSiteScreens.xml` |
| WebSiteCMS | `applications/content/widget/WebSiteScreens.xml` |
| WebSiteCMSNav | `applications/content/widget/WebSiteScreens.xml` |
| WebSiteCMSContent | `applications/content/widget/WebSiteScreens.xml` |
| WebSiteCMSEditor | `applications/content/widget/WebSiteScreens.xml` |
| WebSiteCMSMetaInfo | `applications/content/widget/WebSiteScreens.xml` |
| WebSiteCMSPathAlias | `applications/content/widget/WebSiteScreens.xml` |
| EditWebSitePathAlias | `applications/content/widget/WebSiteScreens.xml` |
| WebSiteAliases | `applications/content/widget/WebSiteScreens.xml` |
| WebSiteAliasesSearchResults | `applications/content/widget/WebSiteScreens.xml` |
| WebSiteSEO | `applications/content/widget/WebSiteScreens.xml` |
| WebSiteContactList | `applications/content/widget/WebSiteScreens.xml` |
| CMSContentFind | `applications/content/widget/cms/CMSScreens.xml` |
| CMSContentEdit | `applications/content/widget/cms/CMSScreens.xml` |
| EditAddContent | `applications/content/widget/cms/CMSScreens.xml` |
| EditAddSubContent | `applications/content/widget/cms/CMSScreens.xml` |
| CMSSites | `applications/content/widget/cms/CMSScreens.xml` |
| addSubSite | `applications/content/widget/cms/CMSScreens.xml` |
| ContentOnly | `applications/content/widget/cms/CMSTemplates.xml` |
| FloatLeft | `applications/content/widget/cms/CMSTemplates.xml` |
| TopCenter | `applications/content/widget/cms/CMSTemplates.xml` |
| FindContent | `applications/content/widget/content/ContentScreens.xml` |
| findContentSearchResults | `applications/content/widget/content/ContentScreens.xml` |
| navigateContent | `applications/content/widget/content/ContentScreens.xml` |
| navigateMenu | `applications/content/widget/content/ContentScreens.xml` |
| EditDocument | `applications/content/widget/content/ContentScreens.xml` |
| ListDocument | `applications/content/widget/content/ContentScreens.xml` |
| ShowContent | `applications/content/widget/content/ContentScreens.xml` |
| ShowContentPortlet | `applications/content/widget/content/ContentScreens.xml` |
| EditDocumentTree | `applications/content/widget/content/ContentScreens.xml` |
| EditContent | `applications/content/widget/content/ContentScreens.xml` |
| EditContentAssoc | `applications/content/widget/content/ContentScreens.xml` |
| EditContentRole | `applications/content/widget/content/ContentScreens.xml` |
| EditContentPurpose | `applications/content/widget/content/ContentScreens.xml` |
| EditContentAttribute | `applications/content/widget/content/ContentScreens.xml` |
| ListWebSite | `applications/content/widget/content/ContentScreens.xml` |
| EditContentMetaData | `applications/content/widget/content/ContentScreens.xml` |
| EditContentWorkEfforts | `applications/content/widget/content/ContentScreens.xml` |
| LookupContent | `applications/content/widget/content/ContentScreens.xml` |
| ListContentTree | `applications/content/widget/content/ContentScreens.xml` |
| LookupContentTree | `applications/content/widget/content/ContentScreens.xml` |
| LookupDetailContentTree | `applications/content/widget/content/ContentScreens.xml` |
| ViewContentDetail | `applications/content/widget/content/ContentScreens.xml` |
| ContentSearchOptions | `applications/content/widget/content/ContentScreens.xml` |
| ContentSearchResults | `applications/content/widget/content/ContentScreens.xml` |
| EditContentKeywords | `applications/content/widget/content/ContentScreens.xml` |
| FindDataResource | `applications/content/widget/content/DataResourceScreens.xml` |
| findDataResourceSearchResults | `applications/content/widget/content/DataResourceScreens.xml` |
| navigateDataResource | `applications/content/widget/content/DataResourceScreens.xml` |
| navigateMenu | `applications/content/widget/content/DataResourceScreens.xml` |
| listDataResources | `applications/content/widget/content/DataResourceScreens.xml` |
| LookupDataResource | `applications/content/widget/content/DataResourceScreens.xml` |
| EditDataResource | `applications/content/widget/content/DataResourceScreens.xml` |
| BrowseDataResource | `applications/content/widget/content/DataResourceScreens.xml` |
| UploadImage | `applications/content/widget/content/DataResourceScreens.xml` |
| AddDataResource | `applications/content/widget/content/DataResourceScreens.xml` |
| AddDataResourceText | `applications/content/widget/content/DataResourceScreens.xml` |
| AddDataResourceUrl | `applications/content/widget/content/DataResourceScreens.xml` |
| AddDataResourceUpload | `applications/content/widget/content/DataResourceScreens.xml` |
| AddDataResourceFromContent | `applications/content/widget/content/DataResourceScreens.xml` |
| EditDataResourceText | `applications/content/widget/content/DataResourceScreens.xml` |
| EditDataResourceUrl | `applications/content/widget/content/DataResourceScreens.xml` |
| EditDataResourceUpload | `applications/content/widget/content/DataResourceScreens.xml` |
| EditElectronicText | `applications/content/widget/content/DataResourceScreens.xml` |
| EditDataResourceAttribute | `applications/content/widget/content/DataResourceScreens.xml` |
| EditDataResourceRole | `applications/content/widget/content/DataResourceScreens.xml` |
| EditDataResourceProductFeatures | `applications/content/widget/content/DataResourceScreens.xml` |
| EditHtmlText | `applications/content/widget/content/DataResourceScreens.xml` |
| DataResourceViewLink | `applications/content/widget/content/DataResourceScreens.xml` |
| EditContentType | `applications/content/widget/contentsetup/ContentSetupScreens.xml` |
| EditContentTypeAttr | `applications/content/widget/contentsetup/ContentSetupScreens.xml` |
| EditContentAssocType | `applications/content/widget/contentsetup/ContentSetupScreens.xml` |
| EditContentPurposeType | `applications/content/widget/contentsetup/ContentSetupScreens.xml` |
| EditContentAssocPredicate | `applications/content/widget/contentsetup/ContentSetupScreens.xml` |
| EditContentPurposeOperation | `applications/content/widget/contentsetup/ContentSetupScreens.xml` |
| UserPermissions | `applications/content/widget/contentsetup/ContentSetupScreens.xml` |
| EditContentOperation | `applications/content/widget/contentsetup/ContentSetupScreens.xml` |
| EditDataResourceType | `applications/content/widget/datasetup/DataResourceSetupScreens.xml` |
| EditCharacterSet | `applications/content/widget/datasetup/DataResourceSetupScreens.xml` |
| EditDataResourceTypeAttr | `applications/content/widget/datasetup/DataResourceSetupScreens.xml` |
| EditFileExtension | `applications/content/widget/datasetup/DataResourceSetupScreens.xml` |
| EditMetaDataPredicate | `applications/content/widget/datasetup/DataResourceSetupScreens.xml` |
| EditMimeType | `applications/content/widget/datasetup/DataResourceSetupScreens.xml` |
| EditMimeTypeHtmlTemplate | `applications/content/widget/datasetup/DataResourceSetupScreens.xml` |
| EditDataCategory | `applications/content/widget/datasetup/DataResourceSetupScreens.xml` |
| commonBlogDecorator | `applications/content/widget/forum/BlogScreens.xml` |
| blogDecorator | `applications/content/widget/forum/BlogScreens.xml` |
| blogArtDecorator | `applications/content/widget/forum/BlogScreens.xml` |
| BlogMain | `applications/content/widget/forum/BlogScreens.xml` |
| EditBlog | `applications/content/widget/forum/BlogScreens.xml` |
| BlogContent | `applications/content/widget/forum/BlogScreens.xml` |
| EditArticle | `applications/content/widget/forum/BlogScreens.xml` |
| ViewArticle | `applications/content/widget/forum/BlogScreens.xml` |
| commonForumDecorator | `applications/content/widget/forum/ForumScreens.xml` |
| FindForumGroups | `applications/content/widget/forum/ForumScreens.xml` |
| FindForums | `applications/content/widget/forum/ForumScreens.xml` |
| ForumGroupRoles | `applications/content/widget/forum/ForumScreens.xml` |
| ForumGroupPurposes | `applications/content/widget/forum/ForumScreens.xml` |
| FindForumMessages | `applications/content/widget/forum/ForumScreens.xml` |
| FindForumThreads | `applications/content/widget/forum/ForumScreens.xml` |
| EditForumMessage | `applications/content/widget/forum/ForumScreens.xml` |
| AddForumMessage | `applications/content/widget/forum/ForumScreens.xml` |
| AddForumThreadMessage | `applications/content/widget/forum/ForumScreens.xml` |
| FindLayout | `applications/content/widget/layout/LayoutScreens.xml` |
| ListLayout | `applications/content/widget/layout/LayoutScreens.xml` |
| EditLayout | `applications/content/widget/layout/LayoutScreens.xml` |
| EditLayoutSubContent | `applications/content/widget/layout/LayoutScreens.xml` |
| EditLayoutText | `applications/content/widget/layout/LayoutScreens.xml` |
| EditLayoutHtml | `applications/content/widget/layout/LayoutScreens.xml` |
| EditLayoutUrl | `applications/content/widget/layout/LayoutScreens.xml` |
| EditLayoutImage | `applications/content/widget/layout/LayoutScreens.xml` |
| AddLayout | `applications/content/widget/layout/LayoutScreens.xml` |

## 未移植 Widget form/grid/menu/tree

| 種別 | 名前 | 定義ファイル |
| --- | --- | --- |
| form | LookupListLayout | `applications/content/widget/LookupForms.xml` |
| form | LookupDataResourceContent | `applications/content/widget/LookupForms.xml` |
| form | ListLookupDataResourceContent | `applications/content/widget/LookupForms.xml` |
| menu | SurveyTabBar | `applications/content/widget/SurveyMenus.xml` |
| form | FindContent | `applications/content/widget/cms/CMSForms.xml` |
| form | ListFindContent | `applications/content/widget/cms/CMSForms.xml` |
| form | EditContent | `applications/content/widget/cms/CMSForms.xml` |
| form | UpdateContentPurposeOperation | `applications/content/widget/cms/CMSForms.xml` |
| form | AddContentPurposeOperation | `applications/content/widget/cms/CMSForms.xml` |
| form | EditAddContentMaster | `applications/content/widget/cms/CMSForms.xml` |
| form | EditAddContent | `applications/content/widget/cms/CMSForms.xml` |
| form | EditAddBioContent | `applications/content/widget/cms/CMSForms.xml` |
| form | AddResponse | `applications/content/widget/cms/CMSForms.xml` |
| form | EditAddImageMaster | `applications/content/widget/cms/CMSForms.xml` |
| form | EditAddImage | `applications/content/widget/cms/CMSForms.xml` |
| form | EditAddBioImage | `applications/content/widget/cms/CMSForms.xml` |
| form | EditAddContentStuff | `applications/content/widget/cms/CMSForms.xml` |
| form | EditAddSubContentStuff | `applications/content/widget/cms/CMSForms.xml` |
| menu | cms | `applications/content/widget/cms/CMSMenus.xml` |
| form | FindContent | `applications/content/widget/content/ContentForms.xml` |
| form | ListContent | `applications/content/widget/content/ContentForms.xml` |
| form | LookupContent | `applications/content/widget/content/ContentForms.xml` |
| form | ListLookupContent | `applications/content/widget/content/ContentForms.xml` |
| form | EditContent | `applications/content/widget/content/ContentForms.xml` |
| form | MruLookupContent | `applications/content/widget/content/ContentForms.xml` |
| form | EditContentAssoc | `applications/content/widget/content/ContentForms.xml` |
| form | AddContentAssoc | `applications/content/widget/content/ContentForms.xml` |
| form | ListContentAssocFrom | `applications/content/widget/content/ContentForms.xml` |
| form | ListContentAssocTo | `applications/content/widget/content/ContentForms.xml` |
| form | AddContentRole | `applications/content/widget/content/ContentForms.xml` |
| form | EditContentRole | `applications/content/widget/content/ContentForms.xml` |
| form | ListContentRole | `applications/content/widget/content/ContentForms.xml` |
| form | AddContentPurpose | `applications/content/widget/content/ContentForms.xml` |
| form | ListContentPurpose | `applications/content/widget/content/ContentForms.xml` |
| form | AddContentAttribute | `applications/content/widget/content/ContentForms.xml` |
| form | ListContentAttribute | `applications/content/widget/content/ContentForms.xml` |
| form | AddContentMetaData | `applications/content/widget/content/ContentForms.xml` |
| form | ListWebSites | `applications/content/widget/content/ContentForms.xml` |
| form | ListContentMetaData | `applications/content/widget/content/ContentForms.xml` |
| form | MruLookupPerson | `applications/content/widget/content/ContentForms.xml` |
| form | MruLookupPartyAndUserLoginAndPerson | `applications/content/widget/content/ContentForms.xml` |
| form | AddWorkEffortContent | `applications/content/widget/content/ContentForms.xml` |
| form | ListWorkEffortContents | `applications/content/widget/content/ContentForms.xml` |
| form | AddDocument | `applications/content/widget/content/ContentForms.xml` |
| form | ViewContentDetail | `applications/content/widget/content/ContentForms.xml` |
| form | EditShowContentPortlet | `applications/content/widget/content/ContentForms.xml` |
| form | AddContentKeyword | `applications/content/widget/content/ContentForms.xml` |
| form | ListContentKeywords | `applications/content/widget/content/ContentForms.xml` |
| menu | ContentAppBar | `applications/content/widget/content/ContentMenus.xml` |
| menu | ContentShortcutAppBar | `applications/content/widget/content/ContentMenus.xml` |
| menu | content | `applications/content/widget/content/ContentMenus.xml` |
| menu | contentSub | `applications/content/widget/content/ContentMenus.xml` |
| menu | website | `applications/content/widget/content/ContentMenus.xml` |
| menu | blog | `applications/content/widget/content/ContentMenus.xml` |
| menu | blogSub | `applications/content/widget/content/ContentMenus.xml` |
| menu | blogArt | `applications/content/widget/content/ContentMenus.xml` |
| menu | blogArtSub | `applications/content/widget/content/ContentMenus.xml` |
| menu | contentTopMenu | `applications/content/widget/content/ContentMenus.xml` |
| menu | lookupMenu | `applications/content/widget/content/ContentMenus.xml` |
| menu | contentMenu | `applications/content/widget/content/ContentMenus.xml` |
| menu | websiteMenu | `applications/content/widget/content/ContentMenus.xml` |
| menu | websitePathAliasMenu | `applications/content/widget/content/ContentMenus.xml` |
| menu | WebAnalyticsConfigButtonBar | `applications/content/widget/content/ContentMenus.xml` |
| form | FindDataResource | `applications/content/widget/content/DataResourceForms.xml` |
| form | ListDataResource | `applications/content/widget/content/DataResourceForms.xml` |
| form | LookupDataResource | `applications/content/widget/content/DataResourceForms.xml` |
| form | ListLookupDataResource | `applications/content/widget/content/DataResourceForms.xml` |
| form | MruLookupDataResource | `applications/content/widget/content/DataResourceForms.xml` |
| form | EditDataResource | `applications/content/widget/content/DataResourceForms.xml` |
| form | AddDataResourceFromContent | `applications/content/widget/content/DataResourceForms.xml` |
| form | ListContentsAssociatedToDataResource | `applications/content/widget/content/DataResourceForms.xml` |
| form | DataResourceMaster | `applications/content/widget/content/DataResourceForms.xml` |
| form | AddDataResource | `applications/content/widget/content/DataResourceForms.xml` |
| form | AddDataResourceText | `applications/content/widget/content/DataResourceForms.xml` |
| form | AddDataResourceUrl | `applications/content/widget/content/DataResourceForms.xml` |
| form | AddDataResourceUpload | `applications/content/widget/content/DataResourceForms.xml` |
| form | EditDataResourceText | `applications/content/widget/content/DataResourceForms.xml` |
| form | EditDataResourceUpload | `applications/content/widget/content/DataResourceForms.xml` |
| form | EditDataResourceUrl | `applications/content/widget/content/DataResourceForms.xml` |
| form | ImageUpload | `applications/content/widget/content/DataResourceForms.xml` |
| form | AddDataResourceAttribute | `applications/content/widget/content/DataResourceForms.xml` |
| form | ListDataResourceAttribute | `applications/content/widget/content/DataResourceForms.xml` |
| form | AddDataResourceRole | `applications/content/widget/content/DataResourceForms.xml` |
| form | ListDataResourceRole | `applications/content/widget/content/DataResourceForms.xml` |
| form | AddDataResourceProductFeature | `applications/content/widget/content/DataResourceForms.xml` |
| form | ListDataResourceProductFeature | `applications/content/widget/content/DataResourceForms.xml` |
| form | LookupProductFeature | `applications/content/widget/content/DataResourceForms.xml` |
| form | ListLookupProductFeature | `applications/content/widget/content/DataResourceForms.xml` |
| form | MruLookupProductFeature | `applications/content/widget/content/DataResourceForms.xml` |
| form | EditElectronicText | `applications/content/widget/content/DataResourceForms.xml` |
| form | AddHtmlText | `applications/content/widget/content/DataResourceForms.xml` |
| form | EditHtmlText | `applications/content/widget/content/DataResourceForms.xml` |
| menu | contentsetup | `applications/content/widget/content/DataResourceMenus.xml` |
| menu | dataresourceTopMenu | `applications/content/widget/content/DataResourceMenus.xml` |
| menu | dataresource | `applications/content/widget/content/DataResourceMenus.xml` |
| form | AddContentType | `applications/content/widget/contentsetup/ContentSetupForms.xml` |
| form | UpdateContentType | `applications/content/widget/contentsetup/ContentSetupForms.xml` |
| form | AddContentTypeAttr | `applications/content/widget/contentsetup/ContentSetupForms.xml` |
| form | UpdateContentTypeAttr | `applications/content/widget/contentsetup/ContentSetupForms.xml` |
| form | AddContentAssocType | `applications/content/widget/contentsetup/ContentSetupForms.xml` |
| form | UpdateContentAssocType | `applications/content/widget/contentsetup/ContentSetupForms.xml` |
| form | AddContentPurposeType | `applications/content/widget/contentsetup/ContentSetupForms.xml` |
| form | UpdateContentPurposeType | `applications/content/widget/contentsetup/ContentSetupForms.xml` |
| form | AddContentAssocPredicate | `applications/content/widget/contentsetup/ContentSetupForms.xml` |
| form | UpdateContentAssocPredicate | `applications/content/widget/contentsetup/ContentSetupForms.xml` |
| form | AddContentOperation | `applications/content/widget/contentsetup/ContentSetupForms.xml` |
| form | UpdateContentOperation | `applications/content/widget/contentsetup/ContentSetupForms.xml` |
| form | AddContentPurposeOperation | `applications/content/widget/contentsetup/ContentSetupForms.xml` |
| form | UpdateContentPurposeOperation | `applications/content/widget/contentsetup/ContentSetupForms.xml` |
| menu | contentsetup | `applications/content/widget/contentsetup/ContentSetupMenus.xml` |
| menu | dataresourcesetup | `applications/content/widget/datasetup/DataResourceSetupMenus.xml` |
| form | AddDataResourceType | `applications/content/widget/datasetup/DataSetupForms.xml` |
| form | UpdateDataResourceType | `applications/content/widget/datasetup/DataSetupForms.xml` |
| form | AddDataResourceTypeAttr | `applications/content/widget/datasetup/DataSetupForms.xml` |
| form | UpdateDataResourceTypeAttr | `applications/content/widget/datasetup/DataSetupForms.xml` |
| form | AddCharacterSet | `applications/content/widget/datasetup/DataSetupForms.xml` |
| form | UpdateCharacterSet | `applications/content/widget/datasetup/DataSetupForms.xml` |
| form | AddDataCategory | `applications/content/widget/datasetup/DataSetupForms.xml` |
| form | UpdateDataCategory | `applications/content/widget/datasetup/DataSetupForms.xml` |
| form | AddFileExtension | `applications/content/widget/datasetup/DataSetupForms.xml` |
| form | UpdateFileExtension | `applications/content/widget/datasetup/DataSetupForms.xml` |
| form | AddMetaDataPredicate | `applications/content/widget/datasetup/DataSetupForms.xml` |
| form | UpdateMetaDataPredicate | `applications/content/widget/datasetup/DataSetupForms.xml` |
| form | AddMimeType | `applications/content/widget/datasetup/DataSetupForms.xml` |
| form | UpdateMimeType | `applications/content/widget/datasetup/DataSetupForms.xml` |
| form | CreateMimeTypeHtmlTemplate | `applications/content/widget/datasetup/DataSetupForms.xml` |
| form | UpdateMimeTypeHtmlTemplate | `applications/content/widget/datasetup/DataSetupForms.xml` |
| form | ListBlogs | `applications/content/widget/forum/BlogForms.xml` |
| form | BlogContent | `applications/content/widget/forum/BlogForms.xml` |
| form | EditBlog | `applications/content/widget/forum/BlogForms.xml` |
| form | EditArticle | `applications/content/widget/forum/BlogForms.xml` |
| form | ListForumGroups | `applications/content/widget/forum/ForumForms.xml` |
| form | AddForumGroup | `applications/content/widget/forum/ForumForms.xml` |
| form | ListForums | `applications/content/widget/forum/ForumForms.xml` |
| form | AddForum | `applications/content/widget/forum/ForumForms.xml` |
| form | ForumGroupPurposes | `applications/content/widget/forum/ForumForms.xml` |
| form | AddForumGroupPurpose | `applications/content/widget/forum/ForumForms.xml` |
| form | ForumGroupRoles | `applications/content/widget/forum/ForumForms.xml` |
| form | AddForumGroupRole | `applications/content/widget/forum/ForumForms.xml` |
| form | ListForumMessages | `applications/content/widget/forum/ForumForms.xml` |
| form | EditForumMessage | `applications/content/widget/forum/ForumForms.xml` |
| form | AddForumMessage | `applications/content/widget/forum/ForumForms.xml` |
| form | AddForumThreadMessage | `applications/content/widget/forum/ForumForms.xml` |
| form | EditForumThreadMessage | `applications/content/widget/forum/ForumForms.xml` |
| menu | ForumGroupTabBar | `applications/content/widget/forum/ForumMenus.xml` |
| menu | ForumTabBar | `applications/content/widget/forum/ForumMenus.xml` |
| menu | ForumMessagesTabBar | `applications/content/widget/forum/ForumMenus.xml` |
| tree | MessageTree | `applications/content/widget/forum/ForumTrees.xml` |
| tree | MessageTree | `applications/content/widget/forum/ForumTrees.xml` |
| form | FindLayout | `applications/content/widget/layout/LayoutForms.xml` |
| form | EditLayout | `applications/content/widget/layout/LayoutForms.xml` |
| form | AddLayout | `applications/content/widget/layout/LayoutForms.xml` |
| form | ListFindLayout | `applications/content/widget/layout/LayoutForms.xml` |
| form | ListListLayout | `applications/content/widget/layout/LayoutForms.xml` |
| form | LayoutSubContentMaster | `applications/content/widget/layout/LayoutForms.xml` |
| form | EditLayoutHtml | `applications/content/widget/layout/LayoutForms.xml` |
| form | EditLayoutText | `applications/content/widget/layout/LayoutForms.xml` |
| form | EditLayoutImage | `applications/content/widget/layout/LayoutForms.xml` |
| form | AddLayoutImage | `applications/content/widget/layout/LayoutForms.xml` |
| form | EditLayoutUrl | `applications/content/widget/layout/LayoutForms.xml` |
| form | ListRelatedLayouts | `applications/content/widget/layout/LayoutForms.xml` |
| form | EditLayoutSubContent | `applications/content/widget/layout/LayoutForms.xml` |
| menu | layout | `applications/content/widget/layout/LayoutMenus.xml` |
| form | FindSurvey | `applications/content/widget/survey/SurveyForms.xml` |
| form | ListFindSurvey | `applications/content/widget/survey/SurveyForms.xml` |
| form | EditSurvey | `applications/content/widget/survey/SurveyForms.xml` |
| form | BuildSurveyFromPdf | `applications/content/widget/survey/SurveyForms.xml` |
| form | BuildSurveyResponseFromPdf | `applications/content/widget/survey/SurveyForms.xml` |
| form | EditSurveyMultiResp | `applications/content/widget/survey/SurveyForms.xml` |
| form | ListSurveyMultiRespColumns | `applications/content/widget/survey/SurveyForms.xml` |
| form | AddSurveyMultiRespColumn | `applications/content/widget/survey/SurveyForms.xml` |
| form | AddSurveyMultiResp | `applications/content/widget/survey/SurveyForms.xml` |
| form | ListSurveyPages | `applications/content/widget/survey/SurveyForms.xml` |
| form | AddSurveyPage | `applications/content/widget/survey/SurveyForms.xml` |
| form | ListSurveyQuestions | `applications/content/widget/survey/SurveyForms.xml` |
| form | CreateSurveyQuestion | `applications/content/widget/survey/SurveyForms.xml` |
| form | CreateSurveyQuestionCategory | `applications/content/widget/survey/SurveyForms.xml` |
| form | CreateSurveyQuestionOption | `applications/content/widget/survey/SurveyForms.xml` |
| form | FindSurveyResponse | `applications/content/widget/survey/SurveyForms.xml` |
| form | ListFindSurveyResponse | `applications/content/widget/survey/SurveyForms.xml` |
| form | LookupSurvey | `applications/content/widget/survey/SurveyForms.xml` |
| form | ListLookupSurvey | `applications/content/widget/survey/SurveyForms.xml` |
| form | LookupSurveyResponse | `applications/content/widget/survey/SurveyForms.xml` |
| form | ListLookupSurveyResponse | `applications/content/widget/survey/SurveyForms.xml` |
| form | ListWebAnalyticsConfig | `applications/content/widget/website/WebAnalyticsForms.xml` |
| form | EditWebAnalyticsConfig | `applications/content/widget/website/WebAnalyticsForms.xml` |
| form | EditWebSite | `applications/content/widget/website/WebSiteForms.xml` |
| form | ListWebSites | `applications/content/widget/website/WebSiteForms.xml` |
| form | FindWebSitePathAlias | `applications/content/widget/website/WebSiteForms.xml` |
| form | ListWebSitePathAlias | `applications/content/widget/website/WebSiteForms.xml` |
| form | EditWebSitePathAlias | `applications/content/widget/website/WebSiteForms.xml` |
| form | ListWebSiteContent | `applications/content/widget/website/WebSiteForms.xml` |
| form | CreateWebSiteContent | `applications/content/widget/website/WebSiteForms.xml` |
| form | CreateWebSiteRole | `applications/content/widget/website/WebSiteForms.xml` |
| form | UpdateWebSiteRole | `applications/content/widget/website/WebSiteForms.xml` |
| form | AutoCreateWebsiteContent | `applications/content/widget/website/WebSiteForms.xml` |
| form | CreateWebsiteSEO | `applications/content/widget/website/WebSiteForms.xml` |
| form | CreateWebSiteContactList | `applications/content/widget/website/WebSiteForms.xml` |
| form | ViewWebSiteContactList | `applications/content/widget/website/WebSiteForms.xml` |

## 未移植 FTL テンプレート

| FTL |
| --- |
| `applications/content/template/Fonts.fo.ftl` |
| `applications/content/template/HelpTemplate.ftl` |
| `applications/content/template/cms/AddSubSite.ftl` |
| `applications/content/template/cms/CMSContentEdit.ftl` |
| `applications/content/template/cms/CMSSites.ftl` |
| `applications/content/template/content/ContentNav.ftl` |
| `applications/content/template/content/ContentSearchOptions.ftl` |
| `applications/content/template/content/ContentSearchResults.ftl` |
| `applications/content/template/content/DisplayContentNav.ftl` |
| `applications/content/template/content/EditContentTree.ftl` |
| `applications/content/template/content/Nav.ftl` |
| `applications/content/template/contentsetup/UserPermissions.ftl` |
| `applications/content/template/layout/RenderSubContent.ftl` |
| `applications/content/template/lookup/ContentTreeLookupList.ftl` |
| `applications/content/template/mime-type/Flash-flv.ftl` |
| `applications/content/template/mime-type/Flash-swf.ftl` |
| `applications/content/template/mime-type/Image.ftl` |
| `applications/content/template/mime-type/QuickTime.ftl` |
| `applications/content/template/survey/EditSurveyQuestions.ftl` |
| `applications/content/template/survey/EditSurveyResponse.ftl` |
| `applications/content/template/survey/GenericSurvey.ftl` |
| `applications/content/template/survey/SurveyOptions.ftl` |
| `applications/content/template/survey/ViewSurveyResponses.ftl` |
| `applications/content/template/website/WebSiteCMSContent.ftl` |
| `applications/content/template/website/WebSiteCMSMeta.ftl` |
| `applications/content/template/website/WebSiteCMSNav.ftl` |
| `applications/content/template/website/WebSiteCMSPathAlias.ftl` |

## 未移植 controller request-map

| uri | event | 定義 |
| --- | --- | --- |
| `<@ofbizContentUrl>` | `groovy:component://content/webapp/content/WEB-INF/events/OfbizContentUrlEvent.groovy` | `applications/content/webapp/content/WEB-INF/controller.xml:45` |
| `AddDataResource` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:974` |
| `AddDataResourceFromContent` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:978` |
| `AddDataResourceText` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:975` |
| `AddDataResourceUpload` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:977` |
| `AddDataResourceUrl` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:976` |
| `AddLayout` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1435` |
| `CMSContentEdit` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1542` |
| `CMSContentFind` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1541` |
| `CMSSites` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1543` |
| `ContentSearchOptions` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1697` |
| `ContentSearchResults` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1701` |
| `ContentSetupMenu` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:919` |
| `CreateWebSiteContent` | `service:createWebSiteContent` | `applications/content/webapp/content/WEB-INF/controller.xml:296` |
| `DataSetupMenu` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:920` |
| `EditAddContent` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1582` |
| `EditAddSubContent` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1589` |
| `EditBlogArticle` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:120` |
| `EditBlogImage` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:124` |
| `EditBlogText` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:128` |
| `EditCharacterSet` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:600` |
| `EditContent` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:924` |
| `EditContentApproval` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1175` |
| `EditContentAssoc` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:946` |
| `EditContentAssocPredicate` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:576` |
| `EditContentAssocType` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:510` |
| `EditContentAttribute` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1060` |
| `EditContentKeywords` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1707` |
| `EditContentMetaData` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1082` |
| `EditContentOperation` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1039` |
| `EditContentPurpose` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1104` |
| `EditContentPurposeOperation` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1126` |
| `EditContentPurposeType` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:534` |
| `EditContentRevision` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1209` |
| `EditContentRevisionItem` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1231` |
| `EditContentRole` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:486` |
| `EditContentType` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:462` |
| `EditContentTypeAttr` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:558` |
| `EditContentWorkEfforts` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1147` |
| `EditDataCategory` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:624` |
| `EditDataResource` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:970` |
| `EditDataResourceAttribute` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:691` |
| `EditDataResourceProductFeatures` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:779` |
| `EditDataResourceRole` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:755` |
| `EditDataResourceText` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1019` |
| `EditDataResourceType` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:667` |
| `EditDataResourceTypeAttr` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:797` |
| `EditDataResourceUpload` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1021` |
| `EditDataResourceUrl` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1020` |
| `EditElectronicText` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:715` |
| `EditFileExtension` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:815` |
| `EditHtmlText` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:719` |
| `EditLayout` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1434` |
| `EditLayoutHtml` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1478` |
| `EditLayoutImage` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1480` |
| `EditLayoutSubContent` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1488` |
| `EditLayoutText` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1479` |
| `EditLayoutUrl` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1481` |
| `EditMetaDataPredicate` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:839` |
| `EditMimeType` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:863` |
| `EditMimeTypeHtmlTemplate` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:887` |
| `EditSurvey` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1257` |
| `EditSurveyMultiResps` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1290` |
| `EditSurveyQuestions` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1329` |
| `EditSurveyResponse` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1417` |
| `EditWebAnalyticsConfig` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1787` |
| `EditWebSite` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:277` |
| `EditWebSiteParties` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:322` |
| `EditWebSitePathAlias` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:354` |
| `FindLayout` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1433` |
| `FindSurvey` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1252` |
| `FindSurveyResponse` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1415` |
| `FindWebAnalyticsConfigs` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1782` |
| `FindWebSite` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:275` |
| `LatestResponses` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:116` |
| `LayoutMenu` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:921` |
| `ListContentApproval` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1173` |
| `ListContentRevisionItem` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1230` |
| `ListContentRevisions` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1208` |
| `ListFindSurveySearchResults` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1253` |
| `ListLayout` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1426` |
| `ListWaitingContentApproval` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1174` |
| `ListWebSite` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:276` |
| `ListWebSiteContent` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:292` |
| `LookupContent` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1617` |
| `LookupDataResource` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1625` |
| `LookupDetailContentTree` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1645` |
| `LookupListLayout` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1629` |
| `LookupPartyAndUserLoginAndPerson` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1653` |
| `LookupPartyName` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1655` |
| `LookupPerson` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1652` |
| `LookupProductFeature` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1654` |
| `LookupSubContent` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1621` |
| `LookupSurvey` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1633` |
| `LookupSurveyResponse` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1637` |
| `LookupTreeContent` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1641` |
| `LookupUserLoginAndPartyDetails` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1651` |
| `LookupWorkEffort` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1656` |
| `RemoveWebSiteContent` | `service:removeWebSiteContent` | `applications/content/webapp/content/WEB-INF/controller.xml:308` |
| `RespondBlog` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:132` |
| `UpdateWebSiteContent` | `service:updateWebSiteContent` | `applications/content/webapp/content/WEB-INF/controller.xml:302` |
| `UploadImage` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1031` |
| `UserPermissions` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1540` |
| `ViewBinaryDataResource` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:665` |
| `ViewBlogArticle` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:102` |
| `ViewBlogRss` | `rome:generateBlogRssFeed` | `applications/content/webapp/content/WEB-INF/controller.xml:106` |
| `ViewResponse` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:112` |
| `ViewSimpleContent` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1611` |
| `ViewSurveyResponses` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1416` |
| `WebAnalytics` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1772` |
| `WebAnalyticsConfigs` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1777` |
| `WebSiteAliases` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:346` |
| `WebSiteAliasesSearchResults` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:350` |
| `WebSiteCms` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:378` |
| `WebSiteCmsPreview` | `java:execute` | `applications/content/webapp/content/WEB-INF/controller.xml:1690` |
| `WebSiteContactList` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1748` |
| `WebSiteSeo` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1737` |
| `addCharacterSet` | `service:createCharacterSet` | `applications/content/webapp/content/WEB-INF/controller.xml:604` |
| `addContentAssocPredicate` | `service:createContentAssocPredicate` | `applications/content/webapp/content/WEB-INF/controller.xml:580` |
| `addContentAssocType` | `service:createContentAssocType` | `applications/content/webapp/content/WEB-INF/controller.xml:514` |
| `addContentAttribute` | `service:createContentAttribute` | `applications/content/webapp/content/WEB-INF/controller.xml:1061` |
| `addContentMetaData` | `service:createContentMetaData` | `applications/content/webapp/content/WEB-INF/controller.xml:1083` |
| `addContentOperation` | `service:createContentOperation` | `applications/content/webapp/content/WEB-INF/controller.xml:1040` |
| `addContentPurpose` | `service:createContentPurpose` | `applications/content/webapp/content/WEB-INF/controller.xml:1105` |
| `addContentPurposeOperation` | `service:createContentPurposeOperation` | `applications/content/webapp/content/WEB-INF/controller.xml:1127` |
| `addContentPurposeType` | `service:createContentPurposeType` | `applications/content/webapp/content/WEB-INF/controller.xml:538` |
| `addContentRole` | `service:createContentRole` | `applications/content/webapp/content/WEB-INF/controller.xml:490` |
| `addContentType` | `service:createContentType` | `applications/content/webapp/content/WEB-INF/controller.xml:466` |
| `addContentTypeAttr` | `service:createContentTypeAttr` | `applications/content/webapp/content/WEB-INF/controller.xml:562` |
| `addDataCategory` | `service:createDataCategory` | `applications/content/webapp/content/WEB-INF/controller.xml:628` |
| `addDataResourceAttribute` | `service:createDataResourceAttribute` | `applications/content/webapp/content/WEB-INF/controller.xml:695` |
| `addDataResourceRole` | `service:createDataResourceRole` | `applications/content/webapp/content/WEB-INF/controller.xml:759` |
| `addDataResourceType` | `service:createDataResourceType` | `applications/content/webapp/content/WEB-INF/controller.xml:671` |
| `addDataResourceTypeAttr` | `service:createDataResourceTypeAttr` | `applications/content/webapp/content/WEB-INF/controller.xml:801` |
| `addDocumentToTree` | `simple:createDocument` | `applications/content/webapp/content/WEB-INF/controller.xml:1675` |
| `addElectronicText` | `service:createElectronicText` | `applications/content/webapp/content/WEB-INF/controller.xml:723` |
| `addFileExtension` | `service:createFileExtension` | `applications/content/webapp/content/WEB-INF/controller.xml:819` |
| `addForumMessage` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:249` |
| `addForumThreadMessage` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:253` |
| `addHtmlText` | `service:createElectronicText` | `applications/content/webapp/content/WEB-INF/controller.xml:735` |
| `addMetaDataPredicate` | `service:createMetaDataPredicate` | `applications/content/webapp/content/WEB-INF/controller.xml:843` |
| `addMimeType` | `service:createMimeType` | `applications/content/webapp/content/WEB-INF/controller.xml:867` |
| `addSubSite` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1568` |
| `autoCreateWebSiteContent` | `service:autoCreateWebSiteContent` | `applications/content/webapp/content/WEB-INF/controller.xml:314` |
| `blogContent` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:86` |
| `blogMain` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:66` |
| `buildSurveyFromPdf` | `service:buildSurveyFromPdf` | `applications/content/webapp/content/WEB-INF/controller.xml:1270` |
| `buildSurveyResponseFromPdf` | `service:buildSurveyResponseFromPdf` | `applications/content/webapp/content/WEB-INF/controller.xml:1276` |
| `chain` | `java:test` | `applications/content/webapp/content/WEB-INF/controller.xml:51` |
| `clipFindLayout` | `java:copyToClip` | `applications/content/webapp/content/WEB-INF/controller.xml:1427` |
| `cloneLayout` | `java:cloneLayout` | `applications/content/webapp/content/WEB-INF/controller.xml:1531` |
| `createBlogArticle` | `service:createBlogEntry` | `applications/content/webapp/content/WEB-INF/controller.xml:96` |
| `createBlogResponse` | `service:createTextContent` | `applications/content/webapp/content/WEB-INF/controller.xml:154` |
| `createContent` | `service:createContent` | `applications/content/webapp/content/WEB-INF/controller.xml:934` |
| `createContentApproval` | `service:createContentApproval` | `applications/content/webapp/content/WEB-INF/controller.xml:1176` |
| `createContentAssoc` | `service:createContentAssoc` | `applications/content/webapp/content/WEB-INF/controller.xml:956` |
| `createContentCms` | `service:createContent` | `applications/content/webapp/content/WEB-INF/controller.xml:406` |
| `createContentKeyword` | `service:createContentKeyword` | `applications/content/webapp/content/WEB-INF/controller.xml:1711` |
| `createContentRevision` | `service:createContentRevision` | `applications/content/webapp/content/WEB-INF/controller.xml:1210` |
| `createContentRevisionItem` | `service:createContentRevisionItem` | `applications/content/webapp/content/WEB-INF/controller.xml:1232` |
| `createDataResource` | `java:persistDataResource` | `applications/content/webapp/content/WEB-INF/controller.xml:991` |
| `createDataResourceAndAssocToContent` | `service:createDataResourceAndAssocToContent` | `applications/content/webapp/content/WEB-INF/controller.xml:999` |
| `createDataResourceAndText` | `service:createDataResourceAndText` | `applications/content/webapp/content/WEB-INF/controller.xml:1013` |
| `createDataResourceProductFeature` | `service:createProductFeatureDataResource` | `applications/content/webapp/content/WEB-INF/controller.xml:783` |
| `createDataResourceUpload` | `service:createDataResource` | `applications/content/webapp/content/WEB-INF/controller.xml:1007` |
| `createForum` | `service:persistContentAndAssoc` | `applications/content/webapp/content/WEB-INF/controller.xml:228` |
| `createForumGroup` | `service:createContent` | `applications/content/webapp/content/WEB-INF/controller.xml:171` |
| `createForumGroupPurpose` | `service:createContentPurpose` | `applications/content/webapp/content/WEB-INF/controller.xml:211` |
| `createForumGroupRole` | `service:createContentRole` | `applications/content/webapp/content/WEB-INF/controller.xml:188` |
| `createLayout` | `simple:createLayout` | `applications/content/webapp/content/WEB-INF/controller.xml:1436` |
| `createLayoutHtml` | `simple:createLayoutText` | `applications/content/webapp/content/WEB-INF/controller.xml:1495` |
| `createLayoutImage` | `java:createLayoutImage` | `applications/content/webapp/content/WEB-INF/controller.xml:1519` |
| `createLayoutSubContent` | `java:createLayoutSubContent` | `applications/content/webapp/content/WEB-INF/controller.xml:1448` |
| `createLayoutText` | `simple:createLayoutText` | `applications/content/webapp/content/WEB-INF/controller.xml:1482` |
| `createLayoutUrl` | `simple:createLayoutUrl` | `applications/content/webapp/content/WEB-INF/controller.xml:1507` |
| `createMimeTypeHtmlTemplate` | `service:createMimeTypeHtmlTemplate` | `applications/content/webapp/content/WEB-INF/controller.xml:891` |
| `createObjectContentCms` | `service:createContentFromUploadedFile` | `applications/content/webapp/content/WEB-INF/controller.xml:394` |
| `createSurvey` | `service:createSurvey` | `applications/content/webapp/content/WEB-INF/controller.xml:1258` |
| `createSurveyMultiResp` | `service:createSurveyMultiResp` | `applications/content/webapp/content/WEB-INF/controller.xml:1291` |
| `createSurveyMultiRespColumn` | `service:createSurveyMultiRespColumn` | `applications/content/webapp/content/WEB-INF/controller.xml:1309` |
| `createSurveyPage` | `service:createSurveyPage` | `applications/content/webapp/content/WEB-INF/controller.xml:1395` |
| `createSurveyQuestion` | `service:createSurveyQuestion` | `applications/content/webapp/content/WEB-INF/controller.xml:1330` |
| `createSurveyQuestionAppl` | `service:createSurveyQuestionAppl` | `applications/content/webapp/content/WEB-INF/controller.xml:1376` |
| `createSurveyQuestionCategory` | `service:createSurveyQuestionCategory` | `applications/content/webapp/content/WEB-INF/controller.xml:1370` |
| `createSurveyQuestionOption` | `service:createSurveyQuestionOption` | `applications/content/webapp/content/WEB-INF/controller.xml:1350` |
| `createTextContentCms` | `service:createTextContent` | `applications/content/webapp/content/WEB-INF/controller.xml:382` |
| `createWebAnalyticsConfig` | `service:createWebAnalyticsConfig` | `applications/content/webapp/content/WEB-INF/controller.xml:1792` |
| `createWebSite` | `service:createWebSite` | `applications/content/webapp/content/WEB-INF/controller.xml:278` |
| `createWebSiteContactList` | `service:createWebSiteContactList` | `applications/content/webapp/content/WEB-INF/controller.xml:1752` |
| `createWebSiteMetaInfoJson` | `service:createTextContent` | `applications/content/webapp/content/WEB-INF/controller.xml:418` |
| `createWebSitePathAlias` | `service:createWebSitePathAlias` | `applications/content/webapp/content/WEB-INF/controller.xml:358` |
| `createWebSitePathAliasJson` | `service:createWebSitePathAlias` | `applications/content/webapp/content/WEB-INF/controller.xml:430` |
| `createWebSiteRole` | `service:createWebSiteRole` | `applications/content/webapp/content/WEB-INF/controller.xml:326` |
| `createWorkEffortContent` | `service:createWorkEffortContent` | `applications/content/webapp/content/WEB-INF/controller.xml:1151` |
| `deleteContentJson` | `java:deleteContent` | `applications/content/webapp/content/WEB-INF/controller.xml:455` |
| `deleteContentKeyword` | `service:deleteContentKeyword` | `applications/content/webapp/content/WEB-INF/controller.xml:1717` |
| `deleteContentKeywords` | `service:deleteContentKeywords` | `applications/content/webapp/content/WEB-INF/controller.xml:1723` |
| `deleteForumGroupPurpose` | `service:removeContentPurpose` | `applications/content/webapp/content/WEB-INF/controller.xml:217` |
| `deleteForumGroupRole` | `service:removeContentRole` | `applications/content/webapp/content/WEB-INF/controller.xml:200` |
| `deleteSurvey` | `service:deleteSurvey` | `applications/content/webapp/content/WEB-INF/controller.xml:1282` |
| `deleteSurveyMultiResp` | `service:deleteSurveyMultiResp` | `applications/content/webapp/content/WEB-INF/controller.xml:1303` |
| `deleteSurveyMultiRespColumn` | `service:deleteSurveyMultiRespColumn` | `applications/content/webapp/content/WEB-INF/controller.xml:1321` |
| `deleteSurveyQuestion` | `service:deleteSurveyQuestion` | `applications/content/webapp/content/WEB-INF/controller.xml:1342` |
| `deleteSurveyQuestionOption` | `service:deleteSurveyQuestionOption` | `applications/content/webapp/content/WEB-INF/controller.xml:1362` |
| `deleteWebAnalyticsConfig` | `service:deleteWebAnalyticsConfig` | `applications/content/webapp/content/WEB-INF/controller.xml:1808` |
| `deleteWebSiteContactList` | `service:deleteWebSiteContactList` | `applications/content/webapp/content/WEB-INF/controller.xml:1764` |
| `deleteWorkEffortContent` | `service:deleteWorkEffortContent` | `applications/content/webapp/content/WEB-INF/controller.xml:1165` |
| `editBlog` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:70` |
| `editContent` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:940` |
| `editForumMessage` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:257` |
| `findContent` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:911` |
| `findContentSearchResults` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:915` |
| `findDataResource` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:648` |
| `findDataResourceSearchResults` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:652` |
| `findForumGroups` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:167` |
| `findForumMessages` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:241` |
| `findForumThreads` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:245` |
| `findForums` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:224` |
| `fonts.pdf` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:60` |
| `forumGroupPurposes` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:207` |
| `forumGroupRoles` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:184` |
| `generateMissingSeoUrlForWebsite` | `service:generateMissingSeoUrlForWebsite` | `applications/content/webapp/content/WEB-INF/controller.xml:1741` |
| `getContentAssocsJson` | `java:getContentAssocs` | `applications/content/webapp/content/WEB-INF/controller.xml:443` |
| `img` | `java:serveImage` | `applications/content/webapp/content/WEB-INF/controller.xml:1032` |
| `linkContentToPubPt` | `service-multi:linkContentToPubPt` | `applications/content/webapp/content/WEB-INF/controller.xml:1550` |
| `listDataResources` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:661` |
| `main` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:58` |
| `moveContentJson` | `java:moveContent` | `applications/content/webapp/content/WEB-INF/controller.xml:449` |
| `navigateContent` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1659` |
| `navigateDataResource` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:656` |
| `newBlog` | `service:createContent` | `applications/content/webapp/content/WEB-INF/controller.xml:80` |
| `pasteSubContent` | `simple:pasteSubContent` | `applications/content/webapp/content/WEB-INF/controller.xml:1466` |
| `persistBlogImage` | `service:persistContentAndAssoc` | `applications/content/webapp/content/WEB-INF/controller.xml:148` |
| `persistBlogSummary` | `service:persistContentAndAssoc` | `applications/content/webapp/content/WEB-INF/controller.xml:136` |
| `persistBlogText` | `service:persistContentAndAssoc` | `applications/content/webapp/content/WEB-INF/controller.xml:142` |
| `persistContent` | `service:persistContentAndAssoc` | `applications/content/webapp/content/WEB-INF/controller.xml:1596` |
| `persistContentStuff` | `service:persistContentAndAssoc` | `applications/content/webapp/content/WEB-INF/controller.xml:1583` |
| `persistImage` | `java:uploadContentAndImage` | `applications/content/webapp/content/WEB-INF/controller.xml:1602` |
| `persistSubContentStuff` | `service:persistContentAndAssoc` | `applications/content/webapp/content/WEB-INF/controller.xml:1590` |
| `postNewSubSite` | `service:persistContentAndAssoc` | `applications/content/webapp/content/WEB-INF/controller.xml:1569` |
| `publishResponse` | `service-multi:updateContent` | `applications/content/webapp/content/WEB-INF/controller.xml:1562` |
| `removeCharacterSet` | `service:removeCharacterSet` | `applications/content/webapp/content/WEB-INF/controller.xml:616` |
| `removeContentApproval` | `service:removeContentApproval` | `applications/content/webapp/content/WEB-INF/controller.xml:1200` |
| `removeContentAssoc` | `service:removeContentAssoc` | `applications/content/webapp/content/WEB-INF/controller.xml:962` |
| `removeContentAssocPredicate` | `service:removeContentAssocPredicate` | `applications/content/webapp/content/WEB-INF/controller.xml:592` |
| `removeContentAssocType` | `service:removeContentAssocType` | `applications/content/webapp/content/WEB-INF/controller.xml:526` |
| `removeContentAttribute` | `service:removeContentAttribute` | `applications/content/webapp/content/WEB-INF/controller.xml:1073` |
| `removeContentMetaData` | `service:removeContentMetaData` | `applications/content/webapp/content/WEB-INF/controller.xml:1095` |
| `removeContentOperation` | `service:removeContentOperation` | `applications/content/webapp/content/WEB-INF/controller.xml:1052` |
| `removeContentPurpose` | `service:removeContentPurpose` | `applications/content/webapp/content/WEB-INF/controller.xml:1117` |
| `removeContentPurposeOperation` | `service:removeContentPurposeOperation` | `applications/content/webapp/content/WEB-INF/controller.xml:1139` |
| `removeContentPurposeType` | `service:removeContentPurposeType` | `applications/content/webapp/content/WEB-INF/controller.xml:550` |
| `removeContentRevision` | `service:removeContentRevision` | `applications/content/webapp/content/WEB-INF/controller.xml:1222` |
| `removeContentRevisionItem` | `service:removeContentRevisionItem` | `applications/content/webapp/content/WEB-INF/controller.xml:1244` |
| `removeContentRole` | `service:removeContentRole` | `applications/content/webapp/content/WEB-INF/controller.xml:502` |
| `removeContentType` | `service:removeContentType` | `applications/content/webapp/content/WEB-INF/controller.xml:478` |
| `removeContentTypeAttr` | `service:removeContentTypeAttr` | `applications/content/webapp/content/WEB-INF/controller.xml:568` |
| `removeDataCategory` | `service:removeDataCategory` | `applications/content/webapp/content/WEB-INF/controller.xml:640` |
| `removeDataResourceAttribute` | `service:removeDataResourceAttribute` | `applications/content/webapp/content/WEB-INF/controller.xml:707` |
| `removeDataResourceProductFeature` | `service:removeProductFeatureDataResource` | `applications/content/webapp/content/WEB-INF/controller.xml:789` |
| `removeDataResourceRole` | `service:removeDataResourceRole` | `applications/content/webapp/content/WEB-INF/controller.xml:771` |
| `removeDataResourceType` | `service:removeDataResourceType` | `applications/content/webapp/content/WEB-INF/controller.xml:683` |
| `removeDataResourceTypeAttr` | `service:removeDataResourceTypeAttr` | `applications/content/webapp/content/WEB-INF/controller.xml:807` |
| `removeDocumentFromTree` | `service:removeContentAssoc` | `applications/content/webapp/content/WEB-INF/controller.xml:1669` |
| `removeElectronicText` | `service:removeElectronicText` | `applications/content/webapp/content/WEB-INF/controller.xml:747` |
| `removeFileExtension` | `service:removeFileExtension` | `applications/content/webapp/content/WEB-INF/controller.xml:831` |
| `removeLayout` | `service:removeContentAssoc` | `applications/content/webapp/content/WEB-INF/controller.xml:1472` |
| `removeMetaDataPredicate` | `service:removeMetaDataPredicate` | `applications/content/webapp/content/WEB-INF/controller.xml:855` |
| `removeMimeType` | `service:removeMimeType` | `applications/content/webapp/content/WEB-INF/controller.xml:879` |
| `removeMimeTypeHtmlTemplate` | `service:removeMimeTypeHtmlTemplate` | `applications/content/webapp/content/WEB-INF/controller.xml:903` |
| `removeSite` | `service:deactivateAssocs` | `applications/content/webapp/content/WEB-INF/controller.xml:1576` |
| `removeSurveyPage` | `service:deleteSurveyPage` | `applications/content/webapp/content/WEB-INF/controller.xml:1407` |
| `removeSurveyQuestionAppl` | `service:deleteSurveyQuestionAppl` | `applications/content/webapp/content/WEB-INF/controller.xml:1388` |
| `removeWebSitePathAlias` | `service:removeWebSitePathAlias` | `applications/content/webapp/content/WEB-INF/controller.xml:370` |
| `removeWebSitePathAliasJson` | `service:removeWebSitePathAlias` | `applications/content/webapp/content/WEB-INF/controller.xml:436` |
| `removeWebSiteRole` | `service:removeWebSiteRole` | `applications/content/webapp/content/WEB-INF/controller.xml:338` |
| `replaceSubContent` | `java:replaceSubContent` | `applications/content/webapp/content/WEB-INF/controller.xml:1460` |
| `showContent` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1681` |
| `showContentPdf` | `` | `applications/content/webapp/content/WEB-INF/controller.xml:1685` |
| `updateBlog` | `service:updateContent` | `applications/content/webapp/content/WEB-INF/controller.xml:74` |
| `updateBlogArticle` | `service:updateBlogEntry` | `applications/content/webapp/content/WEB-INF/controller.xml:90` |
| `updateBlogResponse` | `service:updateTextContent` | `applications/content/webapp/content/WEB-INF/controller.xml:160` |
| `updateCharacterSet` | `service:updateCharacterSet` | `applications/content/webapp/content/WEB-INF/controller.xml:610` |
| `updateContent` | `service:updateContent` | `applications/content/webapp/content/WEB-INF/controller.xml:928` |
| `updateContentAllKeywords` | `java:updateAllContentKeywords` | `applications/content/webapp/content/WEB-INF/controller.xml:1729` |
| `updateContentApproval` | `service:updateContentApproval` | `applications/content/webapp/content/WEB-INF/controller.xml:1182` |
| `updateContentApprovalStatus` | `service:updateContentApproval` | `applications/content/webapp/content/WEB-INF/controller.xml:1188` |
| `updateContentAssoc` | `service:updateContentAssoc` | `applications/content/webapp/content/WEB-INF/controller.xml:950` |
| `updateContentAssocPredicate` | `service:updateContentAssocPredicate` | `applications/content/webapp/content/WEB-INF/controller.xml:586` |
| `updateContentAssocType` | `service:updateContentAssocType` | `applications/content/webapp/content/WEB-INF/controller.xml:520` |
| `updateContentAttribute` | `service:updateContentAttribute` | `applications/content/webapp/content/WEB-INF/controller.xml:1067` |
| `updateContentCms` | `service:updateContent` | `applications/content/webapp/content/WEB-INF/controller.xml:412` |
| `updateContentMetaData` | `service:updateContentMetaData` | `applications/content/webapp/content/WEB-INF/controller.xml:1089` |
| `updateContentOperation` | `service:updateContentOperation` | `applications/content/webapp/content/WEB-INF/controller.xml:1046` |
| `updateContentPurpose` | `service:updateContentPurpose` | `applications/content/webapp/content/WEB-INF/controller.xml:1111` |
| `updateContentPurposeOperation` | `service:updateContentPurposeOperation` | `applications/content/webapp/content/WEB-INF/controller.xml:1133` |
| `updateContentPurposeType` | `service:updateContentPurposeType` | `applications/content/webapp/content/WEB-INF/controller.xml:544` |
| `updateContentRevision` | `service:updateContentRevision` | `applications/content/webapp/content/WEB-INF/controller.xml:1216` |
| `updateContentRevisionItem` | `service:updateContentRevisionItem` | `applications/content/webapp/content/WEB-INF/controller.xml:1238` |
| `updateContentRole` | `service:updateContentRole` | `applications/content/webapp/content/WEB-INF/controller.xml:496` |
| `updateContentType` | `service:updateContentType` | `applications/content/webapp/content/WEB-INF/controller.xml:472` |
| `updateDataCategory` | `service:updateDataCategory` | `applications/content/webapp/content/WEB-INF/controller.xml:634` |
| `updateDataResource` | `service:updateDataResource` | `applications/content/webapp/content/WEB-INF/controller.xml:985` |
| `updateDataResourceAttribute` | `service:updateDataResourceAttribute` | `applications/content/webapp/content/WEB-INF/controller.xml:701` |
| `updateDataResourceRole` | `service:updateDataResourceRole` | `applications/content/webapp/content/WEB-INF/controller.xml:765` |
| `updateDataResourceText` | `service:updateDataResource` | `applications/content/webapp/content/WEB-INF/controller.xml:979` |
| `updateDataResourceType` | `service:updateDataResourceType` | `applications/content/webapp/content/WEB-INF/controller.xml:677` |
| `updateDocumentTree` | `service:updateContent` | `applications/content/webapp/content/WEB-INF/controller.xml:1663` |
| `updateElectronicText` | `service:updateElectronicText` | `applications/content/webapp/content/WEB-INF/controller.xml:729` |
| `updateFeatures` | `service-multi:createOrRemoveProductFeatureDataResource` | `applications/content/webapp/content/WEB-INF/controller.xml:1556` |
| `updateFileExtension` | `service:updateFileExtension` | `applications/content/webapp/content/WEB-INF/controller.xml:825` |
| `updateForum` | `service:persistContentAndAssoc` | `applications/content/webapp/content/WEB-INF/controller.xml:234` |
| `updateForumGroup` | `service:updateContent` | `applications/content/webapp/content/WEB-INF/controller.xml:177` |
| `updateForumGroupRole` | `service:updateContentRole` | `applications/content/webapp/content/WEB-INF/controller.xml:194` |
| `updateForumMessage` | `service:persistContentAndAssoc` | `applications/content/webapp/content/WEB-INF/controller.xml:261` |
| `updateForumThreadMessage` | `service:persistContentAndAssoc` | `applications/content/webapp/content/WEB-INF/controller.xml:267` |
| `updateHtmlText` | `service:updateElectronicText` | `applications/content/webapp/content/WEB-INF/controller.xml:741` |
| `updateLayout` | `simple:updateLayout` | `applications/content/webapp/content/WEB-INF/controller.xml:1442` |
| `updateLayoutHtml` | `simple:updateLayoutText` | `applications/content/webapp/content/WEB-INF/controller.xml:1501` |
| `updateLayoutImage` | `java:updateLayoutImage` | `applications/content/webapp/content/WEB-INF/controller.xml:1525` |
| `updateLayoutSubContent` | `java:updateLayoutSubContent` | `applications/content/webapp/content/WEB-INF/controller.xml:1454` |
| `updateLayoutText` | `simple:updateLayoutText` | `applications/content/webapp/content/WEB-INF/controller.xml:1489` |
| `updateLayoutUrl` | `simple:updateLayoutUrl` | `applications/content/webapp/content/WEB-INF/controller.xml:1513` |
| `updateMetaDataPredicate` | `service:updateMetaDataPredicate` | `applications/content/webapp/content/WEB-INF/controller.xml:849` |
| `updateMimeType` | `service:updateMimeType` | `applications/content/webapp/content/WEB-INF/controller.xml:873` |
| `updateMimeTypeHtmlTemplate` | `service:updateMimeTypeHtmlTemplate` | `applications/content/webapp/content/WEB-INF/controller.xml:897` |
| `updateObjectContentCms` | `service:updateContentAndUploadedFile` | `applications/content/webapp/content/WEB-INF/controller.xml:400` |
| `updateSiteRoles` | `service-multi:updateSiteRoles` | `applications/content/webapp/content/WEB-INF/controller.xml:1544` |
| `updateSurvey` | `service:updateSurvey` | `applications/content/webapp/content/WEB-INF/controller.xml:1264` |
| `updateSurveyMultiResp` | `service:updateSurveyMultiResp` | `applications/content/webapp/content/WEB-INF/controller.xml:1297` |
| `updateSurveyMultiRespColumn` | `service:updateSurveyMultiRespColumn` | `applications/content/webapp/content/WEB-INF/controller.xml:1315` |
| `updateSurveyPage` | `service:updateSurveyPage` | `applications/content/webapp/content/WEB-INF/controller.xml:1401` |
| `updateSurveyQuestion` | `service:updateSurveyQuestion` | `applications/content/webapp/content/WEB-INF/controller.xml:1336` |
| `updateSurveyQuestionAppl` | `service:updateSurveyQuestionAppl` | `applications/content/webapp/content/WEB-INF/controller.xml:1382` |
| `updateSurveyQuestionOption` | `service:updateSurveyQuestionOption` | `applications/content/webapp/content/WEB-INF/controller.xml:1356` |
| `updateSurveyResponse` | `service:createSurveyResponse` | `applications/content/webapp/content/WEB-INF/controller.xml:1418` |
| `updateTextContentCms` | `service:updateTextContent` | `applications/content/webapp/content/WEB-INF/controller.xml:388` |
| `updateWaitingContentApproval` | `service:updateContentApproval` | `applications/content/webapp/content/WEB-INF/controller.xml:1194` |
| `updateWebAnalyticsConfig` | `service:updateWebAnalyticsConfig` | `applications/content/webapp/content/WEB-INF/controller.xml:1800` |
| `updateWebSite` | `service:updateWebSite` | `applications/content/webapp/content/WEB-INF/controller.xml:284` |
| `updateWebSiteContactList` | `service:updateWebSiteContactList` | `applications/content/webapp/content/WEB-INF/controller.xml:1758` |
| `updateWebSiteMetaInfoJson` | `service:updateDataResource` | `applications/content/webapp/content/WEB-INF/controller.xml:424` |
| `updateWebSitePathAlias` | `service:updateWebSitePathAlias` | `applications/content/webapp/content/WEB-INF/controller.xml:364` |
| `updateWebSiteRole` | `service:updateWebSiteRole` | `applications/content/webapp/content/WEB-INF/controller.xml:332` |
| `updateWorkEffortContent` | `service:updateWorkEffortContent` | `applications/content/webapp/content/WEB-INF/controller.xml:1158` |
| `uploadImage` | `service:persistContentAndAssoc` | `applications/content/webapp/content/WEB-INF/controller.xml:1022` |

## その他の未移植要素

- entity ECA `applications/content/entitydef/eecas.xml`: 未移植（主要フローに関係する自動処理はなし）。
- service ECA `applications/content/servicedef/secas.xml`: 主要フローに必要な `createDataText`/`updateDataText` の振り分け、`createDataResource` 後の `createDataResourceRole`（+ `ensurePartyRole`）、`createSurveyQuestion` 後の `createSurveyQuestionAppl` のみ移植。それ以外は未移植。
- minilang イベント（`minilang/content/*.xml`、`minilang/layout/LayoutEvents.xml`）と `ContentManagementMapProcessors.xml`: 画面イベントのため未移植。
- `testdef/ContentTests.xml` の旧テスト: 新モジュールの JUnit で同等観点を再実装（review.md 参照）。
