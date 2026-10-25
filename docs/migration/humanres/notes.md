# humanres 移行ログ（notes）

プレイブックの一次情報。OFBiz 固有の仕組みを Spring Boot に置き換える際の判断・ハマりどころ・自動化できた箇所/手作業の箇所を時系列で記録する。

## 1. 変換判断（根拠付き）

| # | 判断 | 根拠 |
| --- | --- | --- |
| N-01 | 別モジュール `migration/humanres-springboot` を **ルート Gradle のサブプロジェクト** にした（`settings.gradle` に `include 'migration:humanres-springboot'` を1行追加しただけ） | Gradle 9.8.2 での `help` 実行と既存 `HumanResEventsTest` が通り、ルートの `subprojects {}` 設定とも Spring Boot 3.5.7 プラグインが共存できたため。ルートの他ファイルは無変更。 |
| N-02 | JPA のテーブル名・カラム名は OFBiz 命名（`EMPL_POSITION` / `PARTY_ID_FROM`）。stamp 列4本は `@MappedSuperclass` で付与 | 既存 OFBiz DB をそのまま参照できるため（段階的に切り替え可能）。 |
| N-03 | JPA の関連（`@ManyToOne`）は張らず、FK は ID 列のまま | 外部エンティティ（party など）を持たずに済み、アダプタで境界を切れる。entity-engine の `GenericValue` が関連を遅延取得するモデルとも近い。 |
| N-04 | 複合 PK は `@IdClass`（ネストした static クラス）にした | 41 エンティティ中、ほとんどが期間付き複合 PK（`fromDate` を含む）。`@EmbeddedId` だと JSON 入出力がネストしてしまうため。 |
| N-05 | entity-auto の 121 本はサービスごとにクラスを作らず、**汎用の `EntityAutoService` と生成した `ServiceCatalog`** で実現 | OFBiz の `EntityAutoEngine` 自体が汎用エンジン。サービス名・入出力・権限をカタログで 1:1 に保持すれば、仕様との突き合わせ（AC-10）が機械的にできる。 |
| N-06 | REST は `POST /api/humanres/services/{serviceName}` で OFBiz のサービス名を保持し、読み取り専用の `GET /entities/...` を追加 | controller.xml・サービス・新 API の対応を名前で追えるため。リソース指向の API への再設計は UI 再構築時に行う。 |
| N-07 | service-multi は `/multi` の1トランザクション。1行でも失敗すれば全行ロールバック | `ServiceMultiEventHandler` の `global-transaction` は既定 true。 |
| N-08 | 権限は OFBiz の permissionId をそのまま Spring Security の authority に使う | 既存の `SecurityGroupPermission` をそのまま移行でき、`_ADMIN` は全 action を包含するという規則も保てる。 |
| N-09 | party / workeffort / accounting は `PartyPort` / `WorkEffortPort` / `AccountingPort` に切り出した。既定実装は同名テーブルを読み書きする `adapter.local` | 全体移行時は、party の Spring 版や REST クライアントに差し替えるだけで済む。 |
| N-10 | Widget/FTL は移植しない | OFBiz 固有の DSL で 1:1 の変換先がない。業務操作は API で全部カバー。 |

## 2. 自動化できた箇所 / 手作業の箇所

| 区分 | 内容 | 量 |
| --- | --- | --- |
| 自動 | `migration/tools/ofbiz_inventory.py`: entitydef / servicedef / controller.xml / 依存関係の棚卸し（auto-attributes と override を解決した後の入出力まで） | 全量 |
| 自動 | `migration/tools/ofbiz2spring.py`: JPA エンティティ 41、Repository 41、stamp 用スーパークラス、ServiceCatalog 121 | 全量 |
| 自動 | AC-10 のパラメタライズドテスト（カタログ ⇔ inventory.json） | 121 ケース |
| 手作業 | `EntityAutoService`（create/update/delete/expire の挙動を EntityAutoEngine から移植） | 約 340 行 |
| 手作業 | simple サービス 9 本 + イベント 3 本 + `getHRChild` ツリー | 約 390 行 |
| 手作業 | アダプタ（Port 3 種 + local 実装 + 最小 party/workeffort/accounting テーブル） | 約 700 行 |
| 手作業 | Security、例外ハンドラ、ローカル用シード、受け入れテスト | — |

所要時間（目安）: 棚卸しツール 0.5h、生成器 0.3h、ランタイム 1.2h、デバッグと受け入れ検証 0.8h。

## 3. ハマりどころと対処

| # | 事象 | 原因 | 対処 |
| --- | --- | --- | --- |
| P-01 | Maven Central が HTTP 429 | VM の IP にレート制限がかかっていた | ユーザーの `~/.gradle/init.d/mirror.gradle` で Google のミラーを優先（リポジトリには入れない） |
| P-02 | `./gradlew` が `Unable to access jarfile gradle-wrapper.jar` で失敗 | OFBiz は wrapper jar を `.gitignore` 対象にしている | Gradle 9.8.2 で `gradle wrapper --gradle-version 9.8.2` をローカル実行（tracked なファイルは無変更）。pre-push フックもこれで通る |
| P-03 | Testcontainers が `client version 1.32 is too old` | Docker 29 は最小 API 1.40。Boot 3.5.7 が管理する Testcontainers 1.21.3 では古い | `ext['testcontainers.version']='1.21.4'` |
| P-04 | 認証済みの MockMvc 呼び出しが 401 になる | 独自の `UserDetailsService` に `DaoAuthenticationProvider` が紐付いていなかった | provider を明示的に登録 |
| P-05 | 複合 PK を検索すると null になる | `EntityType.getIdType()` が `@IdClass` だと null を返す | 生成コードが持つ `@IdClass` のメタデータから PK を組み立てる |
| P-06 | 複合 PK の timestamp が一致しない | DB 往復で精度が落ちる | ミリ秒に正規化（OFBiz の Timestamp もミリ秒） |
| P-07 | fromDate を「あれば何でも」既定値で埋めてしまった | EntityAutoEngine が既定値を入れるのは PK の fromDate だけ（`:316-320`） | PK のときだけに修正し、回帰テストを追加 |
| P-08 | PK の採番を推測で行い、必須 IN の PK まで採番してしまった | OFBiz は param の mode（OUT / INOUT / N IN + 1 OUT）で決める | カタログの mode に従うよう修正。`applyTraining` は minilang 側の `<make-next-seq-id>` を明示的に移植 |
| P-09 | `isValidText` の deny-list が普通の HR テキスト（system, include, import 等）を拒否する | OFBiz の `deniedWebShellTokens`（`framework/security/config/security.properties:367`）を部分一致でそのまま移植したため | 挙動差を出さないためそのまま。全体移行時の改善候補として review.md に記載 |
| P-10 | `StatusValidChange` がシードに無い | humanres のシードは StatusItem だけ | 遷移が定義されていない限り拒否（OFBiz と同じ）。ローカル用シードで必要な遷移を明示する |
| P-11 | DeepWiki に実在しないエンティティやクラスの役割が書かれていた | Wiki の要約誤り | コードを優先（spec §9） |
| P-12 | `run-local.sh` が Permission denied | 新規ファイルの mode が 100644 | 実行ビットを commit |
