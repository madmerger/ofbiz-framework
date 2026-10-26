# content 既存仕様書と受け入れ基準

- 対象: `madmerger/ofbiz-framework` の `applications/content`（ベース: `devin/1793006105-springboot-common` @ `3ff99279eb`）
- 位置づけ: Spring Boot 移行（`migration/content-springboot/`）の仕様の正。網羅的な一覧（全エンティティ、全サービスの展開済み入出力、全リクエスト）は機械生成の棚卸し `docs/migration/content/inventory/` を正とする。
  - 生成コマンド: `python3 migration/tools/ofbiz_inventory.py --component applications/content --entitymodel applications/datamodel/entitydef/content-entitymodel.xml --out docs/migration/content/inventory`
- スコープ（ユーザー決定）: entity-auto と simple は全量、java/groovy は主要フロー（テキスト DataResource/ElectronicText の作成・更新 → Content 作成 → ContentAssoc → 取得）のみ。ファイルアップロード、画像変換、全文検索（Lucene）は移植しない。未移植は `unported.md` に全件記録する。

## 1. コンポーネント構成

| 項目 | 内容 | 参照 |
| --- | --- | --- |
| entity-resource (model) | なし。エンティティは datamodel が `content-entitymodel.xml`（entity 64 / view-entity 12）をロード | `applications/datamodel/entitydef/content-entitymodel.xml` |
| entity ECA | `eecas.xml` | `applications/content/entitydef/eecas.xml` |
| service-resource | `services.xml`, `services_content.xml`, `services_contenttypes.xml`, `services_data.xml`, `services_document.xml`, `services_ftp.xml`, `services_output.xml`, `services_survey.xml`, `services_commevent.xml`, `services_website.xml`、SECA `secas.xml` | `applications/content/ofbiz-component.xml` |
| webapp | `content`、`base-permission="OFBTOOLS,CONTENTMGR"` | `applications/content/ofbiz-component.xml` |
| test-suite | `ContentTests.xml` | `applications/content/testdef/ContentTests.xml` |
| minilang（サービス） | `permission/DataResourcePermissionServices.xml`、`survey/SurveyServices.xml`、`website/WebSiteServices.xml` | `applications/content/minilang/` |
| Java / Groovy | `ContentManagementServices`、`DataServices`、`ContentServices`、`ContentServicesScript.groovy`、`DataServicesScript.groovy` ほか | `applications/content/src/main/` |
| 画面 | controller request-map 346 / view-map 134、Widget screen 145、FTL 27 | `applications/content/webapp/content/WEB-INF/controller.xml`、`widget/`、`template/` |

サービス 288 本の engine 内訳: entity-auto 176 / java 57 / groovy 37 / simple 7 / group 6 / interface 3 / route 2。

## 2. 主要フローの現行仕様（コード参照）

| # | 処理 | 現行実装 | 仕様 |
| --- | --- | --- | --- |
| F-01 | DataResource 作成 | `DataServicesScript.groovy#createDataResource`（`services_data.xml` の `createDataResource`、権限 `genericDataResourcePermission` CREATE） | `dataResourceId` 未指定なら `getNextSeqId('DataResource')`。作成者・更新者 = userLogin、作成・更新日時 = 現在。`dataTemplateTypeId` 未指定は `NONE`。`statusId` 未指定は `CONTENT_STATUS` の先頭 StatusItem。SECA: `partyId` があれば `createDataResourceRole`（`roleTypeId` 未指定は `OWNER`、`ensurePartyRole` 付き） |
| F-02 | テキスト DataResource 作成（route） | `createDataText`（route）+ `secas.xml` の振り分け | `dataResourceTypeId` が `ELECTRONIC_TEXT`、または未指定で `textData` あり → DataResource + ElectronicText。未指定かつ `textData` なし → `SHORT_TEXT` の DataResource のみ。その他 → DataResource のみ |
| F-03 | テキスト DataResource 更新（route） | `updateDataText` + SECA | `updateDataResource`、`ELECTRONIC_TEXT` なら `updateElectronicText` も |
| F-04 | DataResource + テキスト（Java） | `DataServices#createDataResourceAndText` / `#updateDataResourceAndText` | `ELECTRONIC_TEXT` のときだけ ElectronicText を作成・更新 |
| F-05 | Content 作成 | `services_content.xml` の entity-auto `createContent`（権限 `genericContentPermission` CREATE） | `contentTypeId` 既定 `DOCUMENT`、`statusId` 既定 `CTNT_IN_PROGRESS`、`contentId` は採番 |
| F-06 | テキスト Content 作成・更新（group） | `createTextContent` = `createDataText` → `createContent`、`updateTextContent` = `updateDataText` → `updateContent` | result-to-context で `dataResourceId` を引き継ぐ |
| F-07 | 関連付け | entity-auto `createContentAssoc` / `updateContentAssoc`（UPDATE 権限）、`ContentServices#deactivateContentAssoc` | `fromDate` 未指定は現在。無効化は `thruDate` = 現在 |
| F-08 | 取得 | `ContentManagementServices#getContent`（`ContentWorker.getContentCache` → `ContentDataResourceView`）、`ContentServicesScript.groovy#getContentAndDataResource` / `#getDataResource`、`DataServicesScript.groovy#getElectronicText` | `getContent` は `view`（Content + `dr` 接頭辞の DataResource）。`getContentAndDataResource` は `resultData.content/dataResource/electronicText/imageDataResource` |
| F-09 | その他テキスト系 | `ContentServicesScript.groovy#createContentFromDataResource` / `#createSimpleTextContent` / `#updateSimpleTextContent` | `createSimpleTextContent` は `ELECTRONIC_TEXT` + `dataTemplateTypeId=FTL` の DataResource と `DOCUMENT` の Content を作る |

## 3. simple（minilang）サービス 7 本

| サービス | 実装 | 仕様 |
| --- | --- | --- |
| `genericDataResourcePermission` | `minilang/permission/DataResourcePermissionServices.xml#genericDataResourcePermission` | テンプレート付き（`dataTemplateTypeId` が NONE 以外）は `CONTENTMGR_SUPER`。それ以外は `CONTENTMGR`、不足時は `CONTENTMGR_ROLE`（UPDATE は `dataResourceId` 必須 + 所有ロール確認）。`hasPermission` を返す |
| `createSurveyQuestion` | `minilang/survey/SurveyServices.xml#createSurveyQuestion` | `ENUMERATION` は `enumTypeId`、`GEO` は `geoId` 必須。`surveyQuestionId` を採番。SECA: `surveyId` があれば `createSurveyQuestionAppl` |
| `updateSurveyQuestion` | 同 `#updateSurveyQuestion` | 同じ型チェック後に nonpk を更新 |
| `createSurveyResponse` | 同 `#createSurveyResponse`（+ `validateSurveyResponseInline` / `processSurveyResponseInline`） | Survey・適用質問の存在、匿名可否、複数回答可否、必須回答と型（CREDIT_CARD / GIFT_CARD / DATE / EMAIL / 数値 / URL）を検証し、SurveyResponse と SurveyResponseAnswer を作成・更新 |
| `getWebSitePathAlias` | `minilang/website/WebSiteServices.xml#getWebSitePathAlias` | PK で WebSitePathAlias を取得し `pathTo` を返す |
| `autoCreateWebSiteContent` | 同 `#quickCreateWebSiteContent` | `webSiteContentTypeId` ごとに `WEB_SITE_PUB_PT` の Content（名前 = サイト名 + 種別説明）と WebSiteContent を作る |
| `generateMissingSeoUrlForWebsite` | 同 `#generateMissingSeoUrlForWebsite` | product の `createMissingCategoryAndProductAltUrls`、content の `createMissingContentAltUrls` を呼び、件数メッセージを返す |

## 4. セキュリティ仕様

- base-permission: `OFBTOOLS` と `CONTENTMGR`（webapp 定義）。両方の `_VIEW`（または `_ADMIN`）が `/api/**` に必要。
- サービス権限: `CONTENTMGR_<mainAction>`（`_ADMIN` は全 action を包含）。テンプレート DataResource は `CONTENTMGR_SUPER_<action>`。
- 機密エンティティ（`migration.security.sensitive-entities`）: 認証情報（UserLogin 系）、決済（CreditCard、GiftCard、BankAccount、EftAccount、PaymentMethod、Payment、PaymentGatewayResponse）、口座（FinAccount、FinAccountTrans）、給与（PayHistory、PayrollPreference）、Survey 回答（SurveyResponse、SurveyResponseAnswer。CREDIT_CARD・PASSWORD 型の回答を平文で保持するため）。`CONTENTMGR_SENSITIVE_VIEW` か `CONTENTMGR_ADMIN` が必要。
- Entity API の公開範囲: content カタログが参照する Entity + `extra-entities`（SurveyResponse、SurveyResponseAnswer）。
- HTTPS: 既定 `require-https: true`、postgres プロファイルも true。local とテストのみ false。信頼プロキシ既定は `a^`（なし）。
- Swagger/OpenAPI: local プロファイルのみ。
- デモユーザーのパスワード: `CONTENT_DEMO_PASSWORD` 環境変数。

## 5. 受け入れ基準（AC）

| AC | 内容 |
| --- | --- |
| AC-01 | content servicedef の entity-auto 176 本がすべてカタログに登録され、棚卸しと一致する |
| AC-02 | entity-auto 176 本すべてが create / update / expire / delete として動作する |
| AC-03 | 重複作成は 409、存在しない更新は 404 |
| AC-04 | `createContent` は `DOCUMENT` / `CTNT_IN_PROGRESS` の既定値と作成者を設定する。`createContentAssoc` は `fromDate` を補う |
| AC-20 | 主要フロー: DataResource+ElectronicText → Content → ContentAssoc → getContent / getContentAndDataResource / getElectronicText → 更新 → 無効化 |
| AC-21 | group（createTextContent / updateTextContent）と route（createDataText / updateDataText）の振り分け |
| AC-22 | createDataResource の SECA（DataResourceRole OWNER）、createContentFromDataResource、createSimpleTextContent / updateSimpleTextContent |
| AC-23 | テンプレート DataResource は CONTENTMGR_SUPER が必要 |
| AC-30〜34 | simple 7 本の移植（権限判定、Survey 質問の型チェックと Appl 作成、Survey 回答の検証と保存、WebSite パス別名、WebSite Content 自動作成、SEO URL 件数） |
| AC-40 | 未認証は 401 |
| AC-41 | base-permission またはサービス権限が不足すると 403 |
| AC-42 | 機密エンティティは閲覧専用ユーザーで 403、`CONTENTMGR_SENSITIVE_VIEW` で 200。content 範囲外の Entity は 404 |
| AC-43 | `/multi` の重複は 409 で全件ロールバック |
| AC-44〜47 | Swagger は local のみ。秘密情報は環境変数。postgres は HTTPS 必須。HTTPS 必須時は平文をリダイレクト |
| AC-48 | PostgreSQL（Testcontainers）で CRUD と主要フローが動く |
| AC-49 | 他コンポーネントの URL は 404 |
| AC-90 | `applications/` と `framework/` を変更しない |
