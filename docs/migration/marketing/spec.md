# marketing 既存仕様書

- 対象: `madmerger/ofbiz-framework` `applications/marketing`（ベース `devin/1793006105-springboot-common`）
- 位置づけ: Spring Boot 移行（`migration/marketing-springboot/`）の正。網羅的な一覧（全エンティティ・全サービスの展開済み入出力・全リクエスト）は機械生成の棚卸し `docs/migration/marketing/inventory/` を正とする（生成: `python3 migration/tools/ofbiz_inventory.py --component applications/marketing --entitymodel applications/datamodel/entitydef/marketing-entitymodel.xml --out docs/migration/marketing/inventory`）。
- 情報源の優先順位: 現行コード > ドキュメント。

## 1. コンポーネント構成

| 項目 | 内容 | 参照 |
| --- | --- | --- |
| entity-resource (model) | なし。エンティティは datamodel の `marketing-entitymodel.xml` | `applications/datamodel/entitydef/marketing-entitymodel.xml` |
| service-resource | `services.xml`, `services_contact.xml`, `services_opportunity.xml`, ECA `secas.xml` | `applications/marketing/ofbiz-component.xml`、`applications/marketing/servicedef/` |
| webapp | `marketing`（`base-permission="OFBTOOLS,MARKETING"`）、`SalesForceAutomation`（`/sfa`、`base-permission="OFBTOOLS,SFA"`） | `applications/marketing/ofbiz-component.xml:39-52` |
| Java | `MarketingServices`（signUpForContactList / deleteContactListParty）、`VCard`、`TrackingCodeEvents`（Web イベント）、`ReportHelper` | `applications/marketing/src/main/java/` |
| Groovy | SFA サービス（LeadServices / AccountServices）、レポート・画面 action | `applications/marketing/src/main/groovy/` |
| minilang | ContactList / Segment / Tracking / SFA（contact, opportunity）/ 権限 | `applications/marketing/minilang/` |
| Widget / FTL | Widget 33 ファイル、FTL 3 | `applications/marketing/widget/`、`template/` |

規模: エンティティ 32 + view-entity 10、サービス 110（entity-auto 73 / simple 27 / java 4 / groovy 5 / interface 1）、request-map 146、view-map 84、ECA 11。

## 2. 機能概要

| 領域 | ユースケース | 主エンティティ | 主サービス |
| --- | --- | --- | --- |
| キャンペーン | キャンペーンの CRUD、ロール・価格ルール・プロモ・ノートの紐付け | MarketingCampaign, MarketingCampaignRole/Price/Promo/Note | `createMarketingCampaign` ほか entity-auto |
| 連絡先リスト | リスト CRUD、Web サイトとの紐付け、配信状況 | ContactList, ContactListType, WebSiteContactList, ContactListCommStatus | `createContactList`, `createWebSiteContactList`, `updateContactListCommStatus` |
| 購読 | 申込→確認（opt-in）→購読→解除申請→解除。状態ごとに確認・通知メール | ContactListParty, ContactListPartyStatus | `signUpForContactList`, `createContactListParty`, `updateContactListParty(NoUserLogin)`, `unsubscribeContactListParty`, `deleteContactListParty` |
| セグメント | セグメントグループと分類・地域・ロール。2 者が同一セグメントか判定 | SegmentGroup, SegmentGroupRole など | `arePartiesRelatedThroughSegmentGroup` |
| トラッキングコード | コード・種別・訪問・受注/返品との紐付け | TrackingCode, TrackingCodeVisit, TrackingCodeOrder(Return) | `createTrackingCodeOrderReturns` ほか entity-auto |
| SFA | コンタクト作成・統合、商談、商談ロール、売上予測 | SalesOpportunity*, SalesForecast* | `createContact`, `mergeContacts`, `createSalesForecast`, `createSalesOpportunityAccountRole` |

## 3. 購読状態の遷移（主要フロー）

`ContactListParty.statusId` と履歴 `ContactListPartyStatus`（状態ごとに 1 行、`optInVerifyCode` を保持）。

| 遷移 | トリガ | 振る舞い | OFBiz 参照 | 移行先 |
| --- | --- | --- | --- | --- |
| （なし）→ CLPT_PENDING | `signUpForContactList` / `createContactListParty`（`ContactList.optInVerify=Y` など） | メール形式検証、リスト存在確認、メールの ContactMech を作成、既存の PENDING を削除して新規作成。ステータス行に 10 桁の確認コードを採番し、確認メール（VERIFY_CONT_LIST）を送る | `MarketingServices.java:52`、`ContactListServices.xml:57,261,330`、`secas.xml` | `MarketingJavaServices.java:34`、`ContactListServices.java:52,185,210` |
| PENDING → CLPT_ACCEPTED | `updateContactListPartyNoUserLogin`（確認コード一致が必須） | コード不一致は拒否。購読通知メール（SUB_CONT_LIST_NOTI） | `ContactListServices.xml:158,494` | `ContactListServices.java:123,226` |
| ACCEPTED → CLPT_UNSUBS_PENDING | `unsubscribeContactListParty`（email 指定） | 解除確認メール（UNSUBS_CONT_LIST_VERI）と CommunicationEvent | `ContactListServices.xml:182,545` | `ContactListServices.java:139,230` |
| → CLPT_UNSUBSCRIBED / CLPT_REJECTED | `updateContactListParty` | 解除通知メール（UNSUB_CONT_LIST_NOTI） | `ContactListServices.xml:111,623` | `ContactListServices.java:84,234` |
| 削除 | `deleteContactListParty` | ステータス履歴と本体を削除 | `MarketingServices.java:158` | `MarketingJavaServices.java:78` |

メール送信は `MailPort`（`port/MailPort.java`）に分離し、local 実装 `LocalMailStubAdapter` は送信要求を記録するだけ（SMTP には送らない）。

## 4. 受け入れ基準（AC）

| AC | 内容 | テスト |
| --- | --- | --- |
| AC-01 | inventory の通常エンティティ 32 件がすべて JPA にマップされている | `EntityAutoAcceptanceTest.ac01_allInventoryEntitiesAreMapped` |
| AC-02 | entity-auto 73 件が生成カタログに登録され、inventory と一致する | `ac02_*`, `ac03_generatedCatalogMatchesServiceDefinition` |
| AC-03 | entity-auto 73 件がすべて実行できる（create→update/expire/delete） | `ac04_everyEntityAutoServiceExecutes`（73 パラメタライズ） |
| AC-04 | HTTP で作成→取得→更新→削除ができ、OUT の主キーは採番される | `ac05_crudOverHttpForMarketingCampaign` |
| AC-05 | update は宣言外の属性を 400 で拒否 | `ac06_updateRejectsUndeclaredField` |
| AC-06 | simple 27 件が必要権限付きで登録されている | `MarketingServicesTest.svc01_everySimpleServiceIsRegisteredWithPermission` |
| AC-07 | キャンペーン→連絡先リスト→購読申込→確認→解除申請→解除が通り、状態ごとのメールが MailPort に渡る | `flow01_campaignContactListSubscribeAndUnsubscribe` |
| AC-08 | 不正メール・存在しないリストは拒否。再申込で PENDING が置き換わり、削除で履歴も消える | `flow02_*`, `flow03_*` |
| AC-09 | simple サービス（ContactListParty、WebSite、CommStatus、opt-out、セグメント、トラッキング、権限、SFA）が OFBiz と同じ結果を返す | `simple01_*`〜`simple04_*` |
| AC-10 | 未認証 401、base-permission 不足 403、閲覧専用の書き込み 403 | `MarketingSecurityTest.sec01_*`〜`sec03_*` |
| AC-11 | 機密エンティティは `MARKETING_SENSITIVE_VIEW` が必要。Entity API は自コンポーネントの範囲のみ | `sec04_*`, `sec05_*` |
| AC-12 | multi は 1 件失敗で 409、全件ロールバック | `sec06_multiFailureIs409AndRollsBackEveryItem` |
| AC-13 | 既定プロファイルは HTTPS 必須で、信頼しないプロキシの X-Forwarded-Proto は無視。Swagger は local 以外で無効 | `HttpsAndDocsProfileTest` |
| AC-14 | local のパスワードは環境変数のみ。平文の認証情報をコミットしない | `ac07_localProfileTakesPasswordFromEnvironmentOnly` |
| AC-15 | PostgreSQL で作成・購読・削除が動く | `PostgresSmokeTest` |

## 5. セキュリティ

- base-permission: `OFBTOOLS` + `MARKETING`（marketing webapp に合わせる。SFA 由来のサービスも同じ MARKETING 権限で保護）。
- サービス権限: `MARKETING_<ACTION>`（`_ADMIN` は全 action を含む）。entity-auto はカタログの `permissionMainAction`、simple/Java はハンドラ登録時の action（`MarketingHandlerRegistrar.java`）。
- 機密エンティティ（`migration.security.sensitive-entities`）: ContactListPartyStatus（購読確認コード）、UserLogin 系 3、決済・カード・口座系 9（PaymentMethod, CreditCard, EftAccount, GiftCard, BillingAccount, FinAccount, FinAccountTrans, Payment, PaymentGatewayResponse）、給与系 4（PayHistory, PayrollPreference, PartyBenefit, SalaryStep, PayGrade を含む）。
- `require-https`: 既定 true（postgres も true）。local / test のみ false。信頼プロキシの既定値は一致しない正規表現 `a^`（＝なし）。
- Swagger: local プロファイルのみ。
