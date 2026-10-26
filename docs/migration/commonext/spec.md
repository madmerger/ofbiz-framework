# CommonExt 移行仕様

- 対象: `applications/commonext` のサービス、権限、Entity 拡張、および `ofbizsetup` の API 境界
- 移行先: `migration/commonext-springboot/`
- 一次情報: 現行 XML / Groovy。画面 DSL は移植せず、API と未移植範囲を明記する。
- 網羅対象: `services.xml` の6サービス、controller request-map 36件、CommonExt の Entity 拡張・権限シード。

## 1. コンポーネント構成

| 項目 | OFBiz 現行 | 移行時の扱い |
| --- | --- | --- |
| コンポーネント | `applications/commonext/ofbiz-component.xml:27-35` | Spring Boot 独立サブプロジェクト |
| Entity model | `entitydef/entitymodel.xml:28-35` | 共通 datamodel の `NoteData` に統合 |
| Service model / ECA | `servicedef/services.xml:27-57` / `secas.xml` | 6 handler を登録 / ECA は空 |
| Web アプリ | `ofbiz-component.xml:37-56`、`/ofbizsetup`、base permission `OFBTOOLS,SETUP` | REST API。OFBiz widget UI は対象外 |
| Security permission | `data/CommonExtSecurityPermissionSeedData.xml:23-26`、`data/OfbizSetupSecurityPermissionSeedData.xml:21-24` | `COMMONEXT_PUBLMSG`、`SETUP_*` を Spring authority で評価 |
| 生成 catalog | 6サービスはすべて Groovy、entity-auto 0 | generator が選別した entity-auto catalog は空。Groovy handler は明示登録 |

OFBiz の datamodel 側に `NoteData.noteParty` と Party relation が追加されている
(`applications/datamodel/entitydef/party-entitymodel.xml:2955-2960`)。CommonExt 側では
`moreInfoUrl`、`moreInfoItemId`、`moreInfoItemName` と `noteName` index を拡張する
(`applications/commonext/entitydef/entitymodel.xml:28-35`)。

## 2. サービス仕様

定義の所在は `applications/commonext/servicedef/services.xml:27-57`、実装は
`applications/commonext/src/main/groovy/org/apache/ofbiz/commonext/ofbizsetup/SystemInfoServices.groovy`。
API は `POST /api/commonext/services/{serviceName}`。entity-auto の追加サービスはない。

| サービス | 現行入力 | 現行出力 / 振る舞い | 認証 / Spring Boot 対応 |
| --- | --- | --- | --- |
| `createSystemInfoNote` | `NoteData` の全 non-PK (`auto-attributes`, 任意) | `noteParty` 既定はログイン party。`noteDateTime=now`、`noteName=SYSTEMNOTE`、採番して保存。OFBiz は空の success map。Groovy: 26-34行 | 認証必須、`SETUP_UPDATE`。入力を検証して `noteId` を返す |
| `deleteSystemInfoNote` | PK `noteId` 必須 | `noteName=SYSTEMNOTE` の NoteData と関連7表を削除。通常ノートは権限に関係なく 404。Groovy: 36-47行 | 認証必須、`SETUP_UPDATE`。note の存在・種類・所有権を検査 |
| `deleteAllSystemNotes` | なし | ログイン party の `SYSTEMNOTE` を全削除。OFBiz は空 map。Groovy: 49-52行 | `auth=true`、`SETUP_UPDATE`。`deletedCount` を返す |
| `getSystemInfoNotes` | なし | 自 party の `SYSTEMNOTE` を `noteDateTime DESC` で返す。空なら空 map。Groovy: 54-64行 | `auth=true`、`SETUP_VIEW` |
| `getLastSystemInfoNote` | なし | `lastSystemInfoNote1..3`。添字は[0]、size>2 で[1]、size>3 で[2]。Groovy: 66-83行 | OFBiz は `auth=false`。Spring Boot は API 全体を認証必須にする |
| `getSystemInfoStatus` | なし | open communication と割当中 task の件数・先頭日時。Groovy: 85-109行 | `auth=true`、`SETUP_VIEW` |

### ノート作成・削除・一覧

- 作成時に `noteName`、`noteDateTime`、`noteId` をサーバーで決める。未知キー・PK・stamp は無視する。
- 文字列は `(?i)<[a-z!/]|javascript:|on[a-z]+\\s*=` を拒否し、`moreInfoUrl` は `/` で始まる相対 path（`//` は不可）か HTTP(S) URL のみ許可する。
- 明示 `noteParty` が現在の party と異なる場合は `COMMONEXT_PUBLMSG` または `SETUP_ADMIN` を要求する。現在のユーザーに party が無ければ作成・一覧・削除一括・status は 403。`getLastSystemInfoNote` はレガシーの `_NA_` 検索を維持する。
- 単件削除は `CustRequestItemNote`、`CustRequestNote`、`MarketingCampaignNote`、`OrderHeaderNote`、`PartyNote`、`QuoteNote`、`WorkEffortNote` を先に削除する。
- 削除対象の `noteName` が `SYSTEMNOTE` でなければ、owner/admin 権限に関係なく 404 (`System note not found`) を返す。SYSTEMNOTE は owner または admin に限って削除し、関連7表も削除する。OFBiz にない対象種別・アクセス制御を追加した。
- `getLastSystemInfoNote` の off-by-one 条件（2件で2番目を返さず、3件で3番目を返さない）を互換性のため再現する。

### Status 検索

- `CommunicationEventAndRole` は CE と CR の communicationEventId を結合し、現在 party の CR 行を検索する。OFBiz view の `statusId` は CE の status であり、`COM_ROLE_COMPLETED` と比較するレガシー挙動を保持する。`NULL <> value` は SQL 上 false となり除外される。日時は CE `entryDate DESC`。
- `WorkEffortAndPartyAssign` は WE と WEPA を workEffortId で結合する。現在 party、`PAS_ASSIGNED`、`TASK` のみ、`fromDate <= now` / `thruDate > now` の有効期間を対象とする。日時は assignment `fromDate DESC`。
- 対応 view の定義は `applications/datamodel/entitydef/party-entitymodel.xml:830-840` と
  `applications/workeffort/entitydef/entitymodel_view.xml:197-209`。

## 3. 権限

| 権限 | OFBiz 定義 | Spring Boot |
| --- | --- | --- |
| `OFBTOOLS,SETUP` | `ofbiz-component.xml:37-42` | `/api` の base permission |
| `SETUP_ADMIN` | `OfbizSetupSecurityPermissionSeedData.xml:21-24` | 全 Setup action を許可 |
| `SETUP_VIEW` | 同上 | 読み取りサービス |
| `SETUP_UPDATE` | 同上 | 作成・削除サービス |
| `COMMONEXT_PUBLMSG` | `CommonExtSecurityPermissionSeedData.xml:23-26` | 他 party 宛て公開ノートを作成 |
| 機微 Entity | datamodel の機密フィールド | 許可対象の `UserLogin` は entity endpoint で追加認可し、`currentPassword` をマスク |

PostgreSQL 運用では `COMMONEXT_DB_URL`、`COMMONEXT_DB_USERNAME`、
`COMMONEXT_DB_PASSWORD` と `COMMONEXT_ADMIN_PASSWORD_HASH` を環境変数に設定する。
admin の hash は `{bcrypt}...` 形式で、権限は local profile の admin と同じ。
hash 値を設定ファイルやリポジトリに保存しない。

## 4. REST 面

- Service: `POST /api/commonext/services/{serviceName}`。multi: `POST .../{serviceName}/multi`。
- Generic read-only Entity API: `GET /api/commonext/entities/{Entity}` と `GET .../{Entity}/one`。component は `commonext` に固定し、entity-auto catalog に存在する Entity と `extra-entities` のみを公開する。
- `extra-entities`: `NoteData`、request-map の読み取り代替用 `ProductStore` / `Facility` / `WebSite` / `Party` / `PartyRole` / `PartyGroup` / `ProdCatalog` / `ProductCategory` / `Product`、Setup の `createUser` が扱う機密 `UserLogin`。機密 Entity の sensitivity 設定は別途維持する。
- CommonExt の6 service handler は明示登録し、read-only 3件を `VIEW`、変更3件を `UPDATE` とする。
- entity-auto catalog 件数は0。生成器の実装は `migration/tools/ofbiz2spring.py` の `generate()` で `engine == "entity-auto"` のみ catalog に含める。
- multi 失敗は共通 runtime の `MultiServiceException` により HTTP 400 となり、全 item を rollback する。entity-auto / client-supplied PK を持たないため 409 は適用しない。

## 5. Controller 36 request-map 対応表

画面遷移を含む request-map は移植対象外。generic Entity API は読み取り代替であり、OFBiz の widget / controller 画面の等価実装を意味しない。

### `applications/commonext/webapp/ofbizsetup/WEB-INF/controller.xml` (32)

| URI | Event / invoke | 移行先 API または理由 |
| --- | --- | --- |
| `main` | view `success` | Setup 初期画面。未移植 |
| `initialsetup` | view `success` | 初期設定 screen。未移植 |
| `showMessage` | view `success` | message screen。未移植 |
| `updatePartyGroup` | service `updatePartyGroup` | 更新は Party component service のため未移植。既存値の参照は `GET .../PartyGroup/one` |
| `EntityExportAll` | view `success/error` | Entity export screen。未移植 |
| `entityExportAll` | service `entityExportAll` | Webtools export service。未移植 |
| `FindProductStore` | view `success` | read-only は `GET /api/commonext/entities/ProductStore?payToPartyId=...` |
| `EditProductStore` | view `success` | 必要なら PK 指定 `GET .../ProductStore/one`。編集画面は未移植 |
| `createProductStore` | simple `createProductStoreWithDefaultSetting` | SetupEvents 依存があるため未移植 |
| `updateProductStore` | service `updateProductStore` | Product Store service。未移植 |
| `createOrganization` | simple `createOrganization` | Party / ContactMech / UserLogin 等に依存。未移植 |
| `OrganizationToComplete` | simple `setupDefaultGeneralLedger` | Accounting の初期化を行う。未移植 |
| `ViewFacility` | view `success` | read-only は `GET .../Facility/one` |
| `EditFacility` | view `success` | 必要なら PK 指定 `GET .../Facility/one`。編集画面は未移植 |
| `CreateFacility` | simple `createFacilityAndContactMech` | facility と ContactMech 作成。未移植 |
| `UpdateFacility` | service `updateFacility` | Facility service。未移植 |
| `FindWebSite` | view `success` | read-only は `GET .../WebSite` |
| `EditWebSite` | view `success` | 必要なら PK 指定 `GET .../WebSite/one`。編集画面は未移植 |
| `createWebSite` | simple `createDefaultWebSite` | Site / ProductStore 初期化。未移植 |
| `updateWebSite` | service `updateWebSite` | Website service。未移植 |
| `firstproduct` | view `success` | 初回 Product 設定画面は未移植。データ参照は `GET .../ProdCatalog` |
| `createProdCatalog` | simple `createProdCatalogAndProductStoreCatalog` | Product service 群に依存。未移植 |
| `updateProdCatalog` | service `updateProdCatalog` | Product service。未移植 |
| `EditCategory` | view `success` | read-only は `GET .../ProductCategory/one`。screen は未移植 |
| `createProductCategory` | simple `createProductCategoryAndAddToProdCatalog` | Product catalog との連携。未移植 |
| `updateProductCategory` | service `updateProductCategory` | Product service。未移植 |
| `EditProduct` | view `success` | read-only は `GET .../Product/one`。screen は未移植 |
| `createUpdateProduct` | simple `createUpdateProductInCategory` | Product / Price / Category service に依存。未移植 |
| `firstcustomer` | view `success` | 初回 Customer 設定画面は未移植。既存値は `GET .../Party` / `PartyRole` |
| `createCustomer` | simple `createCustomer` | Party、連絡先、UserLogin 作成。処理は未移植。UserLogin read endpoint は sensitive view 権限必須 |
| `LookupFacility` | view `success` | 読み取り候補は `GET .../Facility`。lookup screen は未移植 |
| `LookupPartyName` | view `success` | Party lookup screen は未移植 |

参照行: 同 controller の request-map は `:27-195`、view-map は `:198-219`。

### `applications/commonext/webapp/WEB-INF/controller.xml` (4)

| URI | Event / invoke | 移行先 API または理由 |
| --- | --- | --- |
| `deleteSystemInfoNote` | service `deleteSystemInfoNote` | `POST /api/commonext/services/deleteSystemInfoNote` |
| `deleteAllSystemNotes` | service `deleteAllSystemNotes` | `POST /api/commonext/services/deleteAllSystemNotes` |
| `createSystemInfoNote` | service `createSystemInfoNote` | `POST /api/commonext/services/createSystemInfoNote` |
| `stream` | Java `DataEvents.serveObjectData` | Content streaming endpoint。未移植 |

参照行: `:27-51`。`webapp/common-js/WEB-INF/controller.xml` request-map 0件で、
preprocessor の `LoginWorker.securedUserLoginByJWTCookie` のみ。

## 6. 受け入れ基準

| AC | 受け入れ内容 | テスト |
| --- | --- | --- |
| AC-01 | entity-auto XML / report / 登録 catalog の件数一致（0） | `EntityAutoAcceptanceTest.ac01entityAutoCountMatchesSourceReportAndRegisteredCatalog` |
| AC-02 | Groovy 6 service handler 登録 | `EntityAutoAcceptanceTest.ac02allGroovyServicesHaveRegisteredHandlers` |
| AC-03 | entity-auto catalog を汎用に実行 | `EntityAutoAcceptanceTest.ac03registeredEntityAutoCatalogCanBeExercisedGenerically` |
| AC-04〜08 | 作成既定値・検証・他 party 権限 | `SystemInfoNoteServiceTest.ac01`〜`ac06` |
| AC-09〜12 | 関連削除、owner/admin、404、必須 PK | 同 `ac07`〜`ac10` |
| AC-13〜17 | 一括削除、一覧、legacy off-by-one、party なし、status 条件 | 同 `ac11`〜`ac17` |
| AC-18〜24 | 401/403、機微 Entity 認可/マスク、Swagger、HTTPS/proxy | `CommonextSecurityTest.ac01`〜`ac07`、`CommonextHttpsTest.ac08` |
| AC-25〜26 | multi 400 と transaction rollback | `MultiRollbackTest.ac01`〜`ac02` |
| AC-27 | PostgreSQL で作成・一覧・削除 | `PostgresSmokeTest.ac01` |
| AC-28 | admin でも非 SYSTEMNOTE を削除できず行が残る | `SystemInfoNoteServiceTest.ac28adminCannotDeleteNonSystemNoteAndRowRemains` |
