# Human Resources (humanres) 既存仕様書

- 対象: `madmerger/ofbiz-framework` trunk @ `b570d7ef66`
- 位置づけ: Spring Boot 移行（`migration/humanres-springboot/`）の **単一の正（Single Source of Truth）**。
- 情報源の優先順位: **現行コード > DeepWiki**。DeepWiki（4.2 Party and Human Resources / 2.2 Entity Engine / 2.3 Service Engine / 2.4 Web Application Layer）は初期仮説として利用し、すべてコードで裏取りした。食い違いは §9 に記録。
- 網羅的な一覧（全エンティティ・全サービスの展開済み入出力・全リクエスト）は機械生成の棚卸し `docs/migration/humanres/inventory/` を正とする（生成: `python3 migration/tools/ofbiz_inventory.py --component applications/humanres --entitymodel applications/datamodel/entitydef/humanres-entitymodel.xml --out docs/migration/humanres/inventory`）。本書は振る舞い・判断・受け入れ基準を記述する。

## 1. コンポーネント構成（Phase 0 棚卸し結果）

| 項目 | 内容 | 参照 |
| --- | --- | --- |
| リソースローダ | `main` (component) | `applications/humanres/ofbiz-component.xml:24` |
| entity-resource (model) | **なし**。エンティティは datamodel コンポーネントが `entitydef/humanres-entitymodel.xml` をロード | `applications/datamodel/ofbiz-component.xml`、`applications/datamodel/entitydef/humanres-entitymodel.xml` |
| entity-resource (seed data) | `HumanResSecurityPermissionSeedData.xml`, `HumanResPortletData.xml` | `ofbiz-component.xml:27-28` |
| service-resource | `services.xml`, `services_ability.xml`, `services_employment.xml`, `services_position.xml` | `ofbiz-component.xml:29-32` |
| webapp | `humanres`, mount `/humanres`, `base-permission="OFBTOOLS,HUMANRES"` | `ofbiz-component.xml:34-40` |
| test-suite | **なし**。`HumanResEventsTest.groovy` は Gradle `test` で走る JUnit5+Mockito 単体テスト | `applications/humanres/src/test/groovy/org/apache/ofbiz/humanres/HumanResEventsTest.groovy` |
| Java 実装 | `HumanResEvents.getChildHRCategoryTree`（イベント 1 本のみ。Java サービス実装はなし） | `applications/humanres/src/main/java/org/apache/ofbiz/humanres/HumanResEvents.java:48` |
| Groovy 実装 | `CategoryTree.groovy`（画面 action 用のツリー初期データ） | `applications/humanres/src/main/groovy/org/apache/ofbiz/humanres/category/CategoryTree.groovy` |
| minilang | `HumanResServices.xml`（サービス 9）、`HumanResEvents.xml`（イベント 4）、`permission/HumanResPermissionServices.xml`（権限 1） | `applications/humanres/minilang/` |
| Widget | Screen 18 ファイル（121 screens）、Form 15 ファイル、Menu、Tree | `applications/humanres/widget/` |
| FreeMarker | `FindEmployee.ftl`, `category/CategoryTree.ftl`, `internalorg/EditInternalOrg.ftl`, `internalorg/RemoveInternalOrg.ftl` | `applications/humanres/template/` |

規模: エンティティ 41 + view-entity 5、サービス 131（entity-auto 121 / simple 10）、request-map 232、view-map 98（すべて `screen`）。

### パイロット妥当性の判断
- **継続（humanres で確定）**。根拠: (1) 全レイヤ（entity / service / controller / widget / test）が揃う、(2) Java 実装が 1 本で手作業移植量が小さい、(3) サービスの 92%（121/131）が entity-auto で自動変換の効果を検証できる、(4) 外部依存はサービス呼び出し 7 種（party 4、workeffort 1、accounting 2）とエンティティ参照に限定される。
- 前提との差分: entitydef が datamodel 側にある、test-suite が無い、party controller との **相互依存**（party 側 screen/menu が humanres widget を参照）。いずれもブロッカーではなく、アダプタ化と「画面は移行しない」判断で吸収する（§8）。
- 代替案（不採用）: marketing は Java サービス・ECA が多く第一弾には重い。rest-api は Entity/Service Engine を持たず「レイヤ代表性」が低い。

## 2. 機能概要・ユースケース

| 領域 | ユースケース | 主エンティティ | 主サービス |
| --- | --- | --- | --- |
| 従業員 | 従業員（Person+EMPLOYEE ロール）作成、検索、内部組織との雇用関係作成 | Party/Person(party), PartyRole, PartyRelationship | `createEmployee` |
| 雇用 | 雇用作成（給与履歴同時作成）、更新、終了理由/種別 | Employment, PayHistory, TerminationReason, TerminationType | `createEmployment`, `updateEmployment`, `updatePayHistory` |
| 職位 | 職位の CRUD、職位充足（人の割当）、報告構造、職責、職位タイプ別レート | EmplPosition, EmplPositionFulfillment, EmplPositionReportingStruct, EmplPositionResponsibility, EmplPositionTypeRate | `createEmplPosition`, `createEmplPositionReportingStruct`, `updateEmplPositionTypeRate` |
| 組織ツリー | 内部組織 → 子組織/職位/職位充足のツリー表示、内部組織の追加/削除 | PartyGroup, PartyRelationship(GROUP_ROLLUP), EmplPosition | `getHRChild`, `createInternalOrg`, `removeInternalOrg` |
| スキル・資格 | スキル、資格、履歴書、能力 | PartySkill, PartyQual, PartyResume | `createPartySkill`, `createPartyQual`, … |
| 評価 | 人事評価、評価項目、パフォーマンスノート | PerfReview, PerfReviewItem, PerformanceNote | `createPerfReview`, … |
| 研修 | 研修申請・割当・承認 | TrainingRequest, PersonTraining, TrainingClassType | `applyTraining`, `assignTraining`, `updatePersonTraining` |
| 採用 | 求人、面接、応募 | JobRequisition, JobInterview, EmploymentApp | `createJobRequisition`, `createJobInterview`, `createEmploymentApp` |
| 休暇 | 休暇申請/承認、祝日 | EmplLeave, WorkEffort(PUBLIC_HOLIDAY) | `createEmplLeave`, `createPublicHoliday` |
| 給与・福利 | 給与等級、昇給ステップ、給与設定、福利厚生、失業請求 | PayGrade, SalaryStep, PayrollPreference, PartyBenefit, UnemploymentClaim | 各 CRUD |
| マスタ設定 | 各種 *Type マスタ | SkillType, EmplLeaveType, ResponsibilityType など | 各 CRUD |
| 照会 | ある party の現在の雇用情報一式 | BenefitTypeAndParty, Employment, EmplPositionAndFulfillment, PartyAcctgPreference, RateAmount | `getCurrentPartyEmploymentData` |

## 3. ドメインモデル

全 41 エンティティのフィールド・PK・リレーション・テーブル名は `inventory/entities.md` を参照。主要な構造:

- **PK パターン**（移行の難所）
  - 単一 ID（シーケンス採番）: `EmplPosition`, `EmploymentApp`, `PartyResume`, `UnemploymentClaim`, `JobRequisition`, `JobInterview`, `TrainingRequest`, 各 *Type。
  - 期間付き複合 PK（`fromDate` を PK に含む）: `Employment`(5 列), `PayHistory`(6 列), `PartyBenefit`(6 列), `EmplPositionFulfillment`, `EmplPositionReportingStruct`, `EmplPositionTypeRate`, `PartyQual`, `PersonTraining`, `EmplLeave`, `SalaryStep`, `PerformanceNote` など。`thruDate` で論理失効（expire）させる。
  - サブシーケンス: `PerfReviewItem.perfReviewItemSeqId`, `PayrollPreference.payrollPreferenceSeqId`。
- **statusId を持つエンティティ**（更新時に `StatusValidChange` 検査）: `PartyQual`, `EmploymentApp`, `UnemploymentClaim`, `EmplPosition`。
- **party への依存**: ほぼ全エンティティが `partyId`/`partyIdFrom`/`partyIdTo`/`roleTypeId*` で party コンポーネントの `Party`/`PartyRole` を FK 参照（`inventory/entities.md` で external と表示）。他に `Uom`, `StatusItem`, `PeriodType`, `RateType`, `WorkEffort`, `Budget` 等を参照。
- **テーブル命名**: OFBiz `ModelUtil.javaNameToDbName`（`EmplPosition` → `EMPL_POSITION`、`partyIdFrom` → `PARTY_ID_FROM`）。全テーブルに `LAST_UPDATED_STAMP`, `LAST_UPDATED_TX_STAMP`, `CREATED_STAMP`, `CREATED_TX_STAMP` が自動付与される。
- **view-entity 5 本**: `BenefitTypeAndParty`, `EmploymentAndPerson`, `EmplPositionAndFulfillment`, `EmplPositionTypeRateAndAmount`, `EmplPositionFulfillmentAndReportingStruct`（照会専用）。

## 4. サービス仕様

全 131 サービスの展開済み入出力（auto-attributes / override 解決後）は `inventory/services.md` を参照。

### 4.1 entity-auto（121 本）の共通仕様
参照: `framework/service/src/main/java/org/apache/ofbiz/service/engine/EntityAutoEngine.java`

| 操作 | 仕様 | 参照行 |
| --- | --- | --- |
| 共通 | 全文字列入力を `SecuredUpload.isValidText` で検査（危険な内容は拒否）。サービス 1 本 = 1 トランザクション（`use-transaction` 既定 true）。`auth="true"` は認証必須。 | `:77`, `:624-636` |
| create | 単一 PK が OUT → `getNextSeqId`。単一 PK INOUT → 未指定なら採番、指定時は重複検査。2 PK のうち 1 つが OUT → サブシーケンス（5 桁）。全 PK IN → 既存なら `ServiceValueFound` エラー。PK に `fromDate` があり未指定なら現在時刻。非 PK は入力にあるものだけ設定。 | `:208-320` |
| update | 全 PK 必須。対象なし → `ServiceValueNotFound`。`statusId` 変更時は `StatusValidChange(statusId, statusIdTo)` が存在しなければ `CommonErrorNoStatusValidChange`。入力にある非 PK のみ更新。 | `:407`, `:433-461` |
| delete | 全 PK 必須。対象なし → `ServiceValueNotFoundForRemove`。 | `:538` |
| expire | 対象なし → `ServiceValueNotFound`。`thruDate` 未指定なら現在時刻を設定して更新。 | `:571` |

### 4.2 simple サービス（手作業移植対象）
参照: `applications/humanres/minilang/HumanResServices.xml`

| サービス | 行 | 処理・副作用 | トランザクション境界 |
| --- | --- | --- | --- |
| `createEmployment` | 25 | party `ensurePartyRoleTo`（partyIdTo に roleTypeIdTo を保証）→ `Employment` 作成（fromDate 既定 now）→ `createPayHistory`（emplFromDate=雇用 fromDate）。 | 1 Tx（呼出先サービスも同一 Tx に参加） |
| `updatePayHistory` | 43 | amount / payGradeId / salaryStepSeqId が現行と同じなら comments のみ更新。異なれば現行を `expirePayHistory` し、新規作成（fromDate = 入力 changeDate または now）。 | 1 Tx |
| `createEmplPositionReportingStruct` | 77 | `emplPositionIdManagedBy == emplPositionIdReportingTo` ならエラー。fromDate 既定 now で作成。 | 1 Tx |
| `createEmployee` | 101 | party `createPersonRoleAndContactMechs`（roleTypeId=EMPLOYEE）。`partyIdFrom` 指定時は `createPartyRelationship`（INTERNAL_ORGANIZATIO → EMPLOYEE、EMPLOYMENT）。OUT `partyId`。 | 1 Tx |
| `updateEmplPositionTypeRate` | 121 | 現行有効レート（日付フィルタ）を thruDate=now で失効 → 新規作成。`rateAmount` 指定時は accounting `updateRateAmount`。 | 1 Tx |
| `deleteEmplPositionTypeRate` | 146 | thruDate=now で失効（物理削除しない）。accounting `expireRateAmount`。 | 1 Tx |
| `getCurrentPartyEmploymentData` | 155 | 読み取りのみ。福利・雇用・職位・職位タイプ・会計設定・レートを返す。 | 読取 |
| `applyTraining` | 191 | `TrainingRequest`（採番）+ `PersonTraining` 作成（trainingClassTypeId=研修名）。 | 1 Tx |
| `assignTraining` | 206 | 同上（割当）。 | 1 Tx |

### 4.3 権限サービス
`humanResManagerPermission`（`minilang/permission/HumanResPermissionServices.xml:23`）→ `genericBasePermissionCheck`（primaryPermission=`HUMANRES`）。`HUMANRES_<mainAction>` または `HUMANRES_ADMIN` を持てば許可。main-action 分布: CREATE 30 / UPDATE 37 / DELETE 35 / VIEW 1。

### 4.4 外部サービス呼び出し（アダプタ化対象）
`inventory/dependencies.md` 参照。party: `ensurePartyRoleTo`, `createPersonRoleAndContactMechs`, `createPartyRelationship`, `deletePartyRelationship`。workeffort: `createWorkEffortAndPartyAssign`。accounting: `updateRateAmount`, `expireRateAmount`。common: `genericBasePermissionCheck`。

## 5. Web エンドポイント仕様

全 request-map / view-map の対応は `inventory/requests.md` を参照。`controller.xml` は `common-controller.xml` と party の `controller.xml` を include（`controller.xml:24-25`）。

| 種別 | 件数 | 仕様 |
| --- | --- | --- |
| `service` イベント | 106 | 同名（多くは同名）サービスを呼び、success → 編集/一覧画面、error → 同画面へ。 |
| `service-multi` | 17 | 一覧フォームの複数行を一括更新。`global-transaction` 既定 true → **全行 1 Tx、1 行でもエラーなら全行ロールバック**（`ServiceMultiEventHandler.java:164`, `:375-394`）。 |
| `simple` | 3 | `createInternalOrg`(1060), `removeInternalOrg`(1066), `createPublicHoliday`(1085)（`minilang/HumanResEvents.xml:23,45,57`）。 |
| `java` | 1 | `getHRChild`(1054) → `HumanResEvents.getChildHRCategoryTree`。 |
| 画面のみ | 105 | view-map → screen widget を描画。 |

イベントの詳細:
- `getHRChild`: (1) `HUMANRES_VIEW`（または ADMIN）が無ければ 403・`_ERROR_MESSAGE_`・`"error"`、delegator に触れない（`HumanResEvents.java:50-56`）。(2) partyId が職位 ID なら充足者ノード、組織なら GROUP_ROLLUP の子組織（子を持てば closed）と、`EMPL_POS_INACTIVE` 以外かつ日付有効な職位を jsTree 形式で `hrTree` に設定（`:93`, `:158`, `:176`, `:213`）。
- `createInternalOrg`: `createPartyRelationship`（GROUP_ROLLUP、headpartyId → partyId、両ロール INTERNAL_ORGANIZATIO）。
- `removeInternalOrg`: 該当 PartyRelationship を検索して `deletePartyRelationship`。
- `createPublicHoliday`: workEffortName・estimatedStartDate 必須。終了日=開始日の日末。同開始日の PUBLIC_HOLIDAY が既存ならエラー。`createWorkEffortAndPartyAssign`。
- `removeEmlpPosition`（`HumanResEvents.xml:31`）は controller から参照されない **デッドコード**。

## 6. 画面（Widget）とテンプレート
- Screen: 一覧（Find*）、編集（Edit*）、ルックアップ（Lookup*）、メインのツリー（`EmplPositionScreens`/`CommonScreens`）。`CommonScreens.xml` がアプリ共通デコレータ（`main-decorator`）とショートカットを定義。
- Form: 検索条件フォーム、一覧（list / multi）フォーム、編集フォーム。multi フォームが `service-multi` と対になる。
- Menu: `HumanresMenus.xml`（アプリバー・タブ）。Tree: `PartyTrees.xml`。
- FTL: `FindEmployee.ftl`（従業員検索）、`CategoryTree.ftl`（jsTree 呼出し → `getHRChild`）、内部組織追加/削除ダイアログ。
- 画面内権限: 多くの screen が `<if-has-permission permission="HUMANRES" action="_CREATE|_UPDATE|...">` でボタン表示を制御。

**移行方針（確定）**: パイロットでは Widget/FTL を移植しない。画面が呼ぶ業務操作をすべて REST API として提供し、UI は将来の SPA 等で別途再構築する（理由: Widget は OFBiz 固有 DSL で 1:1 変換先がなく、画面のデータ取得は API 化で代替可能）。

## 7. セキュリティ / 権限
- Seed 権限: `HUMANRES_ADMIN/VIEW/CREATE/UPDATE/DELETE/APPROVE`、`SUPER` グループに `HUMANRES_ADMIN`（`data/HumanResSecurityPermissionSeedData.xml:22-31`）。
- アプリ入場（base-permission）: 各 base permission について `<P>_VIEW` か `<P>_ADMIN` が必要 → `OFBTOOLS` と `HUMANRES` の **両方**（`LoginWorker.java:1456-1462`、`SecurityFactory.java:119`）。
- サービス: `auth="true"` は認証必須。`humanResManagerPermission` で `HUMANRES_<action>` or `HUMANRES_ADMIN`。
- イベント: `getHRChild` は独自に `HUMANRES_VIEW` を検査（防御的多層化）。

**Spring 側方針（確定）**: Spring Security（HTTP Basic、パイロットはインメモリユーザ）。権限は OFBiz の permissionId をそのまま `GrantedAuthority` に使う。`/api/humanres/**` 全体に base-permission 相当のゲート、サービスごとに main-action 相当のゲートを適用。`_ADMIN` は全 action を包含。

## 8. 依存とアダプタ化の境界
| 依存 | 内容 | Spring 側 |
| --- | --- | --- |
| party | Party/Person/PartyGroup/PartyRole/PartyRelationship、4 サービス、controller include、相互 widget 参照 | `PartyPort`（既定実装: OFBiz と同名テーブルへの JPA アダプタ） |
| workeffort | WorkEffort(PUBLIC_HOLIDAY)、`createWorkEffortAndPartyAssign` | `WorkEffortPort` |
| accounting | RateAmount、PartyAcctgPreference、`updateRateAmount`/`expireRateAmount` | `AccountingPort` |
| common | StatusValidChange、権限チェック、シーケンス採番 | 共通部品（`StatusValidChangeRepository`, `SequenceService`, Security） |

## 9. DeepWiki との差分（コード優先）
- DeepWiki 4.2 は `PartyPosition`, `PersonSkill` などに言及するが実在しない（実体は `EmplPosition`, `PartySkill`）。
- DeepWiki は `HumanResEvents.java` が採用・休暇等を扱うと記述するが、実際は `getChildHRCategoryTree` のみ。
- DeepWiki は humanres が entitydef を持つ前提で説明するが、実際は datamodel コンポーネント配下。

## 10. 既存テストが保証する振る舞い
`HumanResEventsTest.groovy`（3 ケース、ローカル実行で 3/3 green）:
1. `deniesAnonymousCallerWithNoSecurityAttribute`（:65）: security 無し → `error`、403、`hrTree` 未設定、delegator 未取得。
2. `deniesAuthenticatedCallerWithoutHumanresViewPermission`（:77）: HUMANRES_VIEW 無し → 同上。
3. `allowsCallerWithHumanresViewPermissionPastTheGate`（:90）: 権限あり → `success`、403 なし、`hrTree` 設定。

## 11. 受け入れ基準（移行後も満たすべき振る舞い）
各項目は Spring Boot 側テストで検証し、`review.md` で ID ごとに結果を記録する。

**構成・データ**
- AC-01: 41 エンティティすべてが JPA エンティティとして存在し、テーブル名・カラム名・PK が OFBiz 命名（`inventory/entities.md`）と一致する。
- AC-02: 4 つの stamp カラムが作成・更新時に自動設定される。
- AC-03: OFBiz 既存コードは一切変更されない（`git diff trunk -- applications framework` が空）。
- AC-04: H2 で起動し、PostgreSQL（Testcontainers）でもスキーマ生成と CRUD が成功する。

**entity-auto サービス（121 本）**
- AC-10: 121 本すべてが同名で呼び出し可能で、操作種別・対象エンティティ・権限 action が servicedef と一致する。
- AC-11: create で単一 PK 未指定なら採番、2 PK のうち 1 つが採番対象ならサブシーケンス（5 桁ゼロ埋め）が採番され OUT で返る。
- AC-12: create で PK に fromDate を持つエンティティは fromDate 未指定時に現在時刻が入る。
- AC-13: 全 PK 指定の create で既存行があればエラー（409）。
- AC-14: 必須 IN パラメータ欠落はエラー（400）、update/delete/expire の対象なしはエラー（404）。
- AC-15: update は入力に含まれる非 PK フィールドのみ更新する。
- AC-16: statusId を持つ 4 エンティティで、`StatusValidChange` に無い遷移の update はエラー。
- AC-17: expire は thruDate 未指定時に現在時刻を設定する。delete は物理削除する。
- AC-18: 危険な文字列入力（`isValidText` 相当: `<script` 等）は拒否される。

**simple サービス・イベント**
- AC-20: `createEmployment` は PartyRole 保証 → Employment 作成 → PayHistory 作成を 1 Tx で行い、途中失敗で全ロールバック。
- AC-21: `updatePayHistory` は金額等が同じなら comments のみ更新、異なれば旧行失効 + 新行作成。
- AC-22: `createEmplPositionReportingStruct` は同一職位指定でエラー。
- AC-23: `createEmployee` は Person+EMPLOYEE ロール作成、partyIdFrom 指定時に EMPLOYMENT 関係を作成し partyId を返す。
- AC-24: `updateEmplPositionTypeRate` は現行レートを失効して新規作成、`deleteEmplPositionTypeRate` は失効のみ。rateAmount 指定時に AccountingPort が呼ばれる。
- AC-25: `getCurrentPartyEmploymentData` が雇用・職位・福利を返す。
- AC-26: `applyTraining`/`assignTraining` が TrainingRequest と PersonTraining を作成する。
- AC-27: `createInternalOrg`/`removeInternalOrg` が GROUP_ROLLUP 関係を作成/削除する。
- AC-28: `createPublicHoliday` は必須チェック、同日重複でエラー、成功時 WorkEffortPort を呼ぶ。
- AC-29: `getHRChild` 相当は組織 → 子組織・有効職位、職位 → 充足者を jsTree 形式で返し、`EMPL_POS_INACTIVE` と期限切れ職位を除外する。
- AC-30: service-multi 相当は全行 1 Tx で、1 行でも失敗すれば全行ロールバックする。

**セキュリティ（既存テストの再現を含む）**
- AC-40: 未認証の呼び出しは 401、`getHRChild` 相当は業務処理（リポジトリ）に到達しない。（既存テスト 1）
- AC-41: 認証済みでも `HUMANRES_VIEW`/`ADMIN` が無ければ 403、リポジトリに到達しない。（既存テスト 2）
- AC-42: `HUMANRES_VIEW` があれば `getHRChild` 相当は 200 でツリーを返す。（既存テスト 3）
- AC-43: base-permission 相当: `OFBTOOLS_VIEW` が無いユーザは HUMANRES 権限があっても 403。
- AC-44: 各サービスは `HUMANRES_<main-action>` または `HUMANRES_ADMIN` が無ければ 403。
- AC-45: 認証情報・DB パスワードを平文でリポジトリに含めない（環境変数またはプロファイル外部化）。
