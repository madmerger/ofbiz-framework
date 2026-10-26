# Manufacturing 既存仕様書

## 1. コンポーネント構成

対象は `applications/manufacturing/ofbiz-component.xml`（31–44行）である。seed は `data/ManufacturingSecurityPermissionSeedData.xml`、サービスモデルは `services.xml`、`services_lookup.xml`、`services_calendar.xml`、`services_routing.xml`、`services_production_run.xml`、`services_bom.xml`、`services_mrp.xml`、`services_formula.xml`、SECA は `servicedef/secas.xml` を登録する。コンポーネント依存は OFBiz の共通・商品・作業管理等にまたがる。棚卸しは `inventory/` を参照。

servicedef 8 ファイルを XML から再集計した結果は entity-auto 18、simple 8、Java 50、Groovy 49、interface 2、計127サービス。`inventory/` の機械棚卸しもサービス127、entity-auto 18、エンティティ8、view-entity 1 と一致する。SECA は6件。

## 2. 機能概要・ユースケース

| 領域 | ユースケース | 主エンティティ | 主サービス |
| --- | --- | --- | --- |
| BOM | 製品構成、構成部品、数量計算 | ProductAssoc、ProductManufacturingRule | createBOMAssoc、copyBOMAssocs、searchDuplicatedAncestor |
| カレンダー | 製造稼働日・例外日の登録 | TechDataCalendar、TechDataCalendarWeek、TechDataCalendarExcDay、TechDataCalendarExcWeek | create/update/removeCalendar* |
| ルーティング | 生産ルーティングとタスク | WorkEffort、WorkEffortAssoc、WorkEffortGoodStandard | getProductRouting |
| 生産実行 | 生産指示、タスク、進捗 | WorkEffort、WorkEffortGoodStandard | createProductionRun、changeProductionRunStatus、changeProductionRunTaskStatus、updateProductionRunTask |
| MRP・原価 | 所要量計画、原価 | MrpRunLog、MrpEvent、CostComponent | executeMrp、findMrpRuns、findCostComponentOptions |

## 3. ドメインモデル

Manufacturing の主キーは単一 ID と複合キーの両方を含む。`ProductAssoc` は productId / productIdTo / productAssocTypeId / fromDate、カレンダー例外日は calendarId と開始時刻等をキーにする。`WorkEffort` は workEffortId、`ProductManufacturingRule` は ruleId を主キーとする。statusId は Product/WorkEffort の業務状態に使われる。製造エンティティ定義は `applications/datamodel/entitydef/manufacturing-entitymodel.xml` にあり、唯一の view-entity は `MrpEventView`。他コンポーネントの Product、WorkEffort、Facility 等を参照する。

## 4. サービス仕様

### 4.1 entity-auto の共通仕様

18件すべてを生成 catalog に登録する。カレンダー4エンティティ、MrpRunLog / MrpEventType、WorkEffortInvRes などの create/update/delete を含む。生成 catalog の `permissionMainAction` を共通 dispatcher に渡し、MANUFACTURING の main-action で認可する。サービス定義は `services_calendar.xml`、`services_mrp.xml`、`services_production_run.xml`。

### 4.2 手書きサービスの副作用とトランザクション境界

`BomSimpleMethods.xml` 24行以降は BOM 作成・複製・製造ルール、`BomFormulas.xml` 24行以降と `TaskFormulae.xml` 24行以降は数量・タスク時間式を実装する。`BOMServices.java` 261行の searchDuplicatedAncestor は親方向に BOM を再帰探索し循環構成を検出する。349行の getManufacturingComponents は構成を展開する。

主要生産フローは `ProductionRunServices.java` の createProductionRun（185行）、changeProductionRunStatus（605行）、changeProductionRunTaskStatus（820行）、updateProductionRunTask（2384行）。ルーティング選択は `RoutingServicesScript.groovy` 29行の getProductRouting。移行版は各サービスを common dispatcher のトランザクション内で実行する。詳細な移行範囲と差分は `notes.md` / `review.md` を参照。

### 4.3 権限

権限 seed `ManufacturingSecurityPermissionSeedData.xml` 23–30行は MANUFACTURING_VIEW / CREATE / UPDATE / DELETE / ADMIN を定義し、SUPER に ADMIN を割り当てる。画面 base-permission は controller.xml で OFBTOOLS と MANUFACTURING。移行版は `migration.security.component: manufacturing` で API の component path を限定し、`/api/**` に OFBTOOLS_VIEW|ADMIN と MANUFACTURING_VIEW|ADMIN の双方を要求する。Entity API は entity-auto catalog が参照する Entity と `extra-entities`（Product、ProductAssoc、ProductManufacturingRule、WorkEffort 系、CostComponent）のみを公開し、機密 Entity は MANUFACTURING_SENSITIVE_VIEW を追加要求する。

### 4.4 外部呼び出し

旧サービスは delegator / dispatcher 経由で Product、WorkEffort、Facility、在庫、注文、原価、MRP を参照する。移行版の主要 Product / WorkEffort 参照は `ProductPort`、`WorkEffortPort` の local adapter に限定する。

## 5. Web エンドポイント

`controller.xml` は request-map 155件、view-map 69件。request-map の多くは画面イベントまたは帳票であり、サービス API とは別に段階移行する。全件の機械棚卸しは `inventory/requests.md`。

## 6. 画面とテンプレート

`widget/manufacturing/` は screen / form / menu 定義17ファイル、screen 80件。テンプレートは FTL 21件。画面、帳票、Widget/FTL は Spring Boot API の移行範囲外であり、`unported.md` にファイル単位の一覧を記録する。

## 7. セキュリティ / 権限

既定の proxy 信頼範囲は空（common の `a^`）で、`MIGRATION_SECURITY_TRUSTED_PROXIES` のみで上書きする。default および postgres は HTTPS 必須、Swagger は local profile のみ有効。ローカル利用者パスワードは `MANUFACTURING_DEMO_PASSWORD` 環境変数から読み込み、ソースには保存しない。機密 Entity の列挙結果は `application.yml` と `notes.md` を参照。

## 8. 依存とアダプタ化の境界

モジュールは `springboot-common` と `springboot-datamodel` に依存する。製品構成・ルーティング・作業実績の永続化は local adapter に閉じ、OFBiz の画面・サービスエンジンを実行時依存にしない。共通基盤、datamodel、tools、settings.gradle は変更しない。

## 9. DeepWiki との差分

外部 DeepWiki 資料との比較は実施していない。本書の現行仕様はこのリポジトリの servicedef、entitymodel、controller、widget、Java/Groovy 実装を一次情報として採録した。

## 10. 既存テストが保証する振る舞い

現行 `applications/manufacturing/src/test/groovy/` には8テストファイル、`testdef/` には3定義ファイルがある。BOM、MRP、routing cost、production run 等の旧実装テストである。新モジュールでは AC 対応を Java acceptance test と Testcontainers smoke test で行う。

## 11. 受け入れ基準

| AC | 受け入れ条件 | 対応テスト |
| --- | --- | --- |
| AC-01 | モジュール構成、catalog が entity-auto 18件を認識する | `ac01_catalogContainsAllEntityAutoServices` |
| AC-02 | calendar entity-auto の作成・更新・例外日の削除ができる | `ac02_calendarEntityAutoSupportsCreateUpdateAndDelete` |
| AC-03 | simple formula、BOM 自己参照拒否等を検証する | `ac03_simpleFormulaAndBomValidationMatchTheContract` |
| AC-04 | BOM 登録から生産 run / task 完了まで進み header が PRUN_COMPLETED になる | `ac04_productionRunCompletesAfterItsTasks` |
| AC-05 | 未認証・権限不足・機密 Entity・Swagger profile を検証する | `ac05_apiAuthorizationSensitiveEntityAndSwagger` |
| AC-06 | multi の重複 409 と全件 rollback を検証する | `ac06_multiDuplicateReturnsConflictAndRollsBackEarlierRows` |
