# OFBiz → Spring Boot 移行プレイブック

humanres のパイロット（`docs/migration/humanres/` の spec / notes / review）から一般化した、全コンポーネントで共通に使う移行手順書。

## 0. 次に着手するコンポーネントと推奨順序

規模の指標（trunk @ b570d7ef66 で計測。svc = servicedef のサービス数、EA = entity-auto の本数、code = src/main 配下の Java/Groovy ファイル数、req = request-map 数）

| 順 | コンポーネント | svc / EA | code | テスト | req | 推奨理由 |
| --- | --- | --- | --- | --- | --- | --- |
| 0 | **共通基盤**（framework/common の一部） | 229 / 99 | 55 | 9 | — | humanres で自作した `SequenceService`・`StatusValidChange`・権限・`EntityAutoService`・生成器を **共有ライブラリ `migration/ofbiz-spring-common`** に切り出す。以降のすべての前提 |
| 1 | **workeffort** | 167 / 95 | 23 | 3 | 96 | EA 比率が 57% と高くコードが少ない。humanres の `WorkEffortPort` を本物に差し替えられる。依存は主に party |
| 2 | **marketing** | 110 / 73 | 13 | 3 | 146 | 小規模で EA が 66%。party・content への依存を Port で切りやすい |
| 3 | **party** | 266 / 128 | 48 | 13 | 275 | ほぼ全コンポーネントが依存する中核。1〜2 で Port の形が固まってから着手すると、humanres / workeffort / marketing の local アダプタを一斉に置き換えられる |
| 4 | content | 289 / 176 | 79 | 5 | 346 | EA は多いが、ファイル・アップロード系の処理が重い |
| 5 | manufacturing | 127 / 18 | 54 | 13 | 155 | EA が少なく、Java の業務ロジックが中心（手作業が多い） |
| 6 | accounting / order / product | 705〜835 | 70〜180 | 22〜33 | 379〜764 | 最大規模で相互依存も強い。Port と共通基盤が十分に固まってから、さらにサブドメイン単位に分割して進める |
| — | webtools / widget / rest-api / entity / service など | — | — | — | — | フレームワーク自体は「移行対象」ではなく「置き換えられる側」。業務コンポーネントがすべて抜けた後に廃止する |

## 1. コンポーネントの選定基準

| 観点 | 指標 | 優先度を上げる条件 |
| --- | --- | --- |
| 依存度 | `ofbiz_inventory.py` の dependencies.md（呼び出す外部サービス・外部エンティティ・逆依存） | 外部サービスの呼び出しが少ない。逆依存（他コンポーネントからの `component://` 参照）が少ない |
| 規模 | svc / EA / code / req | EA の比率が高い（自動変換が効く）。手書きの Java/Groovy が少ない |
| テスト | src/test・testdef の有無 | 既存テストがある（受け入れ基準の種にできる） |
| 業務影響 | 利用頻度・障害時の影響 | パイロット直後は影響が小さいものから |
| 基盤への貢献 | 他コンポーネントの Port を本物に置き換えられるか | 置き換え効果が大きいもの（party など）は、Port の形が固まった段階で前倒しする |

## 2. 標準作業フロー（チェックリスト）

1コンポーネントあたりの目安: 調査と仕様化 0.5〜1日、生成と手作業の移植 1〜3日、テストとレビュー 1日（humanres 規模の場合）。

全体は次の順で進める。humanres のパイロット後、共通基盤を先に安定させ、その基盤を共通の土台として残り9コンポーネントを並列セッションで移行する。各コンポーネント PR は共通基盤を base にし、統合ブランチで全 migration モジュールのテストを確認してから、共有 PostgreSQL を使うコンポーネント横断 E2E を実施する。

- [ ] **P0 棚卸し**: `python3 migration/tools/ofbiz_inventory.py --component applications/<c> --entitymodel <entitymodel...> --out docs/migration/<c>/inventory` を実行
  - [ ] entitydef の所在を確認する（humanres のように datamodel 側にある場合がある）
  - [ ] test-suite と既存テストの有無を確認する
  - [ ] ECA の有無を確認する（`ofbiz-component.xml` の `service-resource type="eca"` と `entity-resource type="eca"`）
  - [ ] 既存テストを実行してベースラインを記録する
- [ ] **P1 仕様化**: §5 のテンプレートで `docs/migration/<c>/spec.md` を作成
  - [ ] DeepWiki で全体像の仮説を立て、ファイルと行番号で裏取りする。食い違いは「DeepWiki との差分」節に記録
  - [ ] 受け入れ基準（AC-xx）を書く
  - [ ] `spec.md`、判断・調査メモの `notes.md`、レビュー結果の `review.md`、対象外サービスの `unported.md` をそろえる
- [ ] **P2 実装**
  - [ ] `migration/<c>-springboot` を作成し、ルートの `settings.gradle` に include を1行追加（それ以外の既存ファイルは触らない）
  - [ ] `ofbiz2spring.py` で JPA エンティティ・Repository・ServiceCatalog を生成（手で編集しない）
  - [ ] 共通の `EntityAutoService` で entity-auto を提供
  - [ ] simple / Java / Groovy のサービスを `@Service @Transactional` に手で移植（呼び出しの連鎖は同一トランザクション）
  - [ ] 外部コンポーネントへの依存を Port + `adapter.local` に分離
  - [ ] controller.xml のイベントを REST に置き換え、service-multi は `/multi` の1トランザクションにする
  - [ ] Security（base-permission と main-action）を設定
  - [ ] 判断・ハマりどころを `notes.md` に記録
- [ ] **P3 検証**
  - [ ] AC ごとのテスト（メソッド名に AC 番号を付ける）、既存テストの再現、カタログと棚卸しの突き合わせ
  - [ ] Testcontainers で PostgreSQL のスモークテスト
  - [ ] bootRun と curl による疎通
  - [ ] `cases.json` に API E2E を記録し、実行結果を `e2e-report.md` に残す
  - [ ] Swagger UI を使った主要フローの E2E 録画を成果物として保存する
  - [ ] 既存 OFBiz 側の回帰テスト
  - [ ] `review.md` に AC の判定・挙動差分・制約を記録
- [ ] **P4 PR**: 共通基盤を base に PR → セキュリティ自己点検 → Devin Review → 指摘を解消 → 統合ブランチで全モジュールを検証 → 共有 PostgreSQL E2E

## 3. レイヤ別の変換ルール

### 3.1 entitydef → JPA（自動）
- テーブル名とカラム名は `ModelUtil.javaNameToDbName` に揃える。stamp 列4本は共通の `@MappedSuperclass` で付与。
- 型の対応は fieldtype（`framework/entity/fieldtype/fieldtypepostgres.xml`）に従う。例: id → VARCHAR(20)、currency-amount → NUMERIC(18,2)、indicator → CHAR(1)、date-time → Timestamp（ミリ秒）。
- 複合 PK は `@IdClass`。関連は張らず、FK は ID 列のまま持つ（境界を Port で切るため）。
- view-entity は、必要になった時点で JPQL または Repository のクエリとして手で書く。

### 3.2 servicedef → @Service
| engine | 変換 | 自動化 |
| --- | --- | --- |
| entity-auto | 共通の `EntityAutoService` + 生成した `ServiceCatalog` | ◎ 全自動 |
| simple（minilang） | `@Service @Transactional` へ手で移植。`<make-next-seq-id>`、`<set-service-fields>`、`<call-service>` の連鎖、日付フィルタなどを1行ずつ読む | △ |
| java / groovy | ロジックはほぼそのまま。`Delegator` は Repository に、`dispatcher.runSync` は Bean の呼び出しか Port に置き換える | ○ |
| permission-service | `HUMANRES_<action>` の規則で Security 側に寄せる | ◎ |
| ECA / SECA | 該当サービス内での明示的な呼び出しか、Spring の `@TransactionalEventListener` に置き換える | △ |

移行範囲は engine 種別で明記する。entity と entity-auto、simple は全量を対象とし、Java / Groovy は主要業務フローを優先する。移植しないサービスは engine 種別とともに `unported.md` に列挙する。「〜は未移植」とだけ指示すると simple まで除外されることがある。Accounting の GL 自動仕訳では、simple の約20本まで対象外と誤解された事例があるため、「除外するのは Java / Groovy の残り。simple は全量」のように指定する。

entity-auto で必ず再現する挙動: 採番（OUT / 空の INOUT / N IN + 1 OUT のサブシーケンス 5桁）、PK の fromDate の既定値 now、重複時 409、対象なし 404、必須項目欠落 400、更新は入力に含まれる項目だけ、`StatusValidChange`、expire の thruDate、`isValidText`。

### 3.3 controller.xml → Spring MVC
- `service` イベント → `POST /api/<c>/services/{name}`（OFBiz のサービス名を維持し、追跡できるようにする）
- `service-multi` → `/multi`（global-transaction=true なら全行1トランザクション）
- `simple` / `java` イベント → 専用のエンドポイント
- 画面のみのリクエスト → `GET /api/<c>/entities/{Entity}`（一覧・1件取得）
- include された他コンポーネントの controller は移行対象外。参照元のコンポーネントを移行するときに扱う。

### 3.4 Widget / FTL → 別途方針
- パイロット期間は移植しない（API で業務操作を全部提供）。
- UI 再構築の方針: Spring 側の API の上に SPA（React 等）を作る。Screen・Form の定義は画面要件の一次資料として使う（項目・必須・Lookup・権限による表示制御）。
- 新旧並行期間は、OFBiz の画面を残したまま、裏側のデータを API 経由に寄せていく。

### 3.5 permission → Spring Security
- authority = OFBiz の permissionId。`<P>_ADMIN` は全 action を包含。
- webapp の base-permission → `/api/<c>/**` の全体ゲート（各 base permission に `_VIEW` または `_ADMIN` が必要）。
- サービスの permission-service の main-action → サービス単位のゲート。
- 拒否したときは業務処理（Repository）に到達しないことをテストで verify する。
- センシティブ Entity は URI 文字列ではなく EntityRegistry が解決した正規名で認可し、EntityAuto と行を返す simple service の双方で検査する。
- 機密エンティティは通常の VIEW 権限から分離し、`<BASE>_SENSITIVE_VIEW` などの専用権限を要求する。readonly で 403、専用権限で 200 になることを確認する。
- `/api/{component}/...` の component 名がアプリ設定と一致すること、および Entity API の対象がそのコンポーネントの登録カタログまたは明示的な追加許可リストに含まれることを確認する。
- Entity update はサービス定義に宣言された IN / INOUT フィールドだけを適用し、未宣言フィールドは 400 で拒否する。
- 本番では HTTPS を必須にする。trusted proxy は既定で設定せず、`X-Forwarded-Proto` を使う場合だけ TLS 終端プロキシを明示的に許可する。Tomcat native `RemoteIpValve` と信頼プロキシ regex を設定し、公開 IP の偽装ヘッダーは拒否、信頼済みプロキシ経由だけ HTTPS と扱うテストを置く。
- Swagger UI / API docs は local 専用（integration を含むローカル検証プロファイルのみ）で有効にし、本番・共有環境では無効にする。
- 認証はパイロットでは HTTP Basic + インメモリ。全体移行では OIDC 等にし、`SecurityGroupPermission` から authority をロードする。
- Basic 認証のユーザー名・パスワード、Authorization ヘッダーはテスト記録、ログ、`cases.json`、画面録画で伏せ字化し、環境変数名だけを記載する。

## 4. 依存のアダプタ化と段階的な切り替え

### 4.1 パターン
```
<c> の業務サービス ──▶ XxxPort（interface、<c> 側で定義）
                          ├─ adapter.local    : OFBiz と同名のテーブルを JPA で直接読み書き（初期）
                          ├─ adapter.ofbiz    : OFBiz の REST（rest-api プラグイン）を呼ぶ（並行稼働期）
                          └─ adapter.spring   : 移行済みの Xxx-springboot の Bean か API を呼ぶ（最終）
```
- Port のメソッドは、OFBiz のサービス名と粒度をそのまま使う（`ensurePartyRoleTo`、`createPartyRelationship` など）。
- 依存先が移行されたら、`@Profile` か `@ConditionalOnProperty` で実装を切り替え、local アダプタは削除する。

### 4.2 段階的な切り替え
1. **並存**: Spring 版を別プロセスで起動し、OFBiz と同じ DB スキーマを使う。読み取り API から利用を始める。
2. **書き込みの移管**: コンポーネント単位で書き込みを Spring 版に寄せる。採番（`SEQUENCE_VALUE_ITEM`）は OFBiz の bank と衝突するため、片方に寄せるか、ID の範囲を分ける。
3. **画面の移管**: SPA 化した画面から順に、OFBiz 画面へのリンクを差し替える。
4. **廃止**: OFBiz の該当コンポーネントを `component-load.xml` から外す。

### 4.3 共有 PostgreSQL による統合 E2E
- 統合方式は各コンポーネントを別々の Spring Boot プロセスとして動かし、同一の PostgreSQL データベース（同一スキーマ）を共有する。ユーザー操作は各コンポーネント自身の API に送る。
- `integration` プロファイルはローカル専用とし、DB 接続情報は環境変数から受け取る。アプリを順番に起動し、Hibernate のスキーマ更新が同時に走らないようにする。
- Seed は主キーを確認してから登録し、複数アプリの起動・再起動で重複キーにならないよう冪等にする。コンポーネント別の Seed を同じ共有 DB に入れる場合も、同じデータを複数の Seed 経路から重複登録しない。
- Port と `adapter.local` は共有テーブルへの読み書きに使える。統合 E2E は Order の local stub に集約せず、出荷は Product、請求・入金は Accounting の各 API を呼ぶ。
- Accounting の GL 自動仕訳は、仕訳組織に `INTERNAL_ORGANIZATIO` の PartyRole と必要な GL account default / organization 設定がないと処理がスキップされる。状態だけでなく `AcctgTrans` と仕訳種別も確認する。

## 5. 仕様書テンプレート（spec.md の雛形）

```markdown
# <component> 既存仕様書
- 対象コミット / 情報源の優先順位（コード > DeepWiki）/ 棚卸しのパス
## 1. コンポーネント構成（ofbiz-component.xml の各リソースと行番号、規模、パイロット妥当性）
## 2. 機能概要・ユースケース（領域 | ユースケース | 主エンティティ | 主サービス）
## 3. ドメインモデル（PK パターン、statusId を持つエンティティ、外部依存、view-entity）
## 4. サービス仕様（4.1 entity-auto の共通仕様 / 4.2 手書きサービスの副作用とトランザクション境界 / 4.3 権限 / 4.4 外部呼び出し）
## 5. Web エンドポイント（イベント種別ごとの件数、特殊なイベントの詳細、デッドコード）
## 6. 画面とテンプレート（役割と移行方針）
## 7. セキュリティ / 権限（seed、base-permission、サービス、イベント → Spring 方針）
## 8. 依存とアダプタ化の境界
## 9. DeepWiki との差分
## 10. 既存テストが保証する振る舞い
## 11. 受け入れ基準（AC-xx。構成・データ / entity-auto / 手書きサービス / セキュリティ）
```

DeepWiki の使い方: 「<component> の主要ユースケース、エンティティ、サービス、画面、他コンポーネントへの依存を、ファイル名付きで」と質問して仮説を得る。回答に出てきたファイル・クラス・エンティティは **すべて実在を確認** する（humanres では実在しないエンティティ名が2つ出た）。

## 6. テスト移行と受け入れ基準の作り方
- 受け入れ基準は「入力・操作 → 観測できる結果」の形で書き、1 AC に1つ以上のテストを付ける（メソッド名は `acNN_…`）。
- 種にするもの: (1) 既存テストのケース（ほぼそのまま再現）、(2) entity-auto の共通挙動（共通テストを再利用）、(3) 手書きサービスの分岐ごとの副作用、(4) 権限のマトリクス（未認証 / base 権限なし / action 権限なし / 許可）、(5) トランザクション（途中で失敗したら全ロールバック）。
- `cases.json` の各ケースに `id`、`name`、`request`（`method`、コンポーネント名とポートを含む `path`、`user`、`body`）、`expected`（HTTP status と確認するキー値）を記録する。前ケースで採番した ID の受け渡し方法も明記する。
- 標準ケースは未認証 401、権限不足 403、機密 entity の readonly 403 / 専用権限 200、entity-auto の create/read/update/delete、simple service 1〜2本、主要業務フロー、multi の失敗と先行行 rollback を含める。
- Swagger UI から主要 E2E を実行・録画し、認証情報を伏せた動画と `e2e-report.md` をレビュー可能な成果物として残す。
- 網羅性の機械チェック: カタログ ⇔ inventory.json のパラメタライズドテスト。
- DB: 普段は H2、AC として PostgreSQL（Testcontainers 1.21.4 以上）を必ず1本は通す。
- 既存 OFBiz 側の回帰テストも毎回流す（AC: 既存コードに変更がないこと）。

## 7. 繰り返し発生する落とし穴と回避策
| 落とし穴 | 回避策 |
| --- | --- |
| Maven Central のレート制限（429） | ユーザーの `~/.gradle/init.d` でミラーを優先する（リポジトリには入れない） |
| `gradle-wrapper.jar` が無く `./gradlew` も pre-push フックも動かない | `gradle wrapper --gradle-version <properties の版>` をローカルで実行（jar は gitignore 対象） |
| Docker 29 と Testcontainers の API 版が合わない | `ext['testcontainers.version']='1.21.4'` 以上 |
| H2 では通るが PostgreSQL で nullable JPQL bind が失敗する | `:p is null` / `:p is not null` に null を bind すると PostgreSQL が `42P18`（parameter type を決定できない）を返す。null のときは条件を JPQL から除き、値があるときだけ bind する。PostgreSQL Testcontainers smoke test を必ず置く |
| Gradle の `FileTree` input が task output と重なる | 広い root ディレクトリを入力にせず、実際に処理する source file の一覧に入力を絞る |
| サブシーケンス採番で同じ親に同時登録する | 同じ親の最大値を読んで次値を作る方式では並行要求が同じ値になり、一方が重複キーで 409 になる。テストは直列化し、同時登録が要件なら DB 側で採番を原子的にする |
| 複合 PK や検索条件の Timestamp 精度が一致しない | PostgreSQL のマイクロ秒値を Entity key / 比較条件に使う前にミリ秒へ正規化する |
| EntityRegistry の暗黙の100件上限で内部処理が欠落する | PR #12 で内部 `findByEquality` の既定上限を解消。HTTP の一覧 API だけは互換性のため明示的に100件を指定する |
| `/multi` の失敗 status が常に 400 になる | PR #12 以降は失敗行の `MigrationException` status を伝播する（重複 409、対象なし 404、その他の入力エラー 400）。全行 rollback も確認する |
| Headless 環境で Swagger UI の E2E 録画が起動しない | Linux CI / 開発環境では Xvfb と `DISPLAY` を用意し、画面を実際に操作・記録する |
| entitydef がコンポーネント外（datamodel）にある | 棚卸しの段階で `--entitymodel` を明示する |
| fromDate の既定値・採番を「推測」で実装する | EntityAutoEngine の規則（PK の fromDate だけ、param の mode で採番を判断）を厳守し、回帰テストを置く |
| minilang の `<make-next-seq-id>` などの暗黙処理を見落とす | minilang は要素を1つずつ移植表に起こす |
| 複合 PK の Timestamp の精度 | ミリ秒に正規化する |
| `@IdClass` だと `EntityType.getIdType()` が null | 生成メタデータから PK を組み立てる |
| deny-list（`deniedWebShellTokens`）の誤検知 | OFBiz と同じ挙動にとどめ、改善は全体で一括して判断する |
| StatusValidChange のシードが別コンポーネントにある | 必要な遷移をシードに明示する |
| センシティブな Entity を汎用 API から公開する | `migration.security.sensitive-entities` に列挙し、`<BASE>_SENSITIVE_VIEW` と HTTPS を設定してテストする |
| URI だけでセンシティブ Entity を認可する | 正規 Entity 名へ解決した時点で検査し、EntityAuto と simple service の戻り値にも同じゲートを適用する |
| `X-Forwarded-Proto` を任意クライアントやプライベートネットワーク全体から信頼する | `server.forward-headers-strategy=native` とし、既定ではプロキシを信頼しない。`MIGRATION_SECURITY_TRUSTED_PROXIES` に TLS 終端の許可範囲だけを設定する |
| DeepWiki の誤り | コードで裏取りし、spec の「DeepWiki との差分」に記録 |
| 相互依存（party ⇔ humanres の画面） | 画面を移植しないので API 層では切れる。UI 再構築時に対応する |

## 8. 自動化できる手順 / 手作業が必要な手順
| 手順 | 自動化 | ツール・メモ |
| --- | --- | --- |
| エンティティ・サービス・リクエスト・依存の棚卸し | ◎ | `migration/tools/ofbiz_inventory.py` |
| JPA エンティティと Repository、stamp | ◎ | `migration/tools/ofbiz2spring.py` |
| entity-auto サービス | ◎ | `ServiceCatalog` + 共通の `EntityAutoService` |
| 権限ゲート | ◎ | カタログの main-action |
| カタログ網羅性のテスト | ◎ | パラメタライズドテスト |
| 仕様書の骨格 | ○ | テンプレート + 棚卸し + DeepWiki |
| minilang / Java / Groovy のサービス | ✕ 手作業 | 1要素ずつ移植し、分岐ごとにテスト |
| Port の設計と local アダプタ | ✕ 手作業 | 依存先の移行状況に合わせて設計する |
| 挙動差分の判断（エラー形式・認証方式） | ✕ 手作業 | review.md に記録 |
| UI の再構築 | ✕ 手作業 | Widget を要件の一次資料として使う |

## 9. 残る横断課題
- permission の `*_ROLE_*`（例: `ORDERMGR_ROLE_*`、`ACCOUNTING_ROLE_*`、`WORKEFFORTMGR_ROLE_*`）を既存の role / permission に照合し、共通の認可モデルへ反映する。
- `UserLogin` に基づくデータ所有者確認を、Entity API と手書きサービスの双方で一貫して実装・テストする。
- ECA によるコンポーネント間状態遷移を整理する。例として、出荷後に全 Order item が `ITEM_COMPLETED` でも Order header が `ORDER_APPROVED` のままの場合がある。どのコンポーネントがヘッダー状態を更新するかを明示する。
- Widget / FTL の画面移行と view-entity の共通的な扱いは未完了。画面要件を Widget 定義から抽出し、必要な view-entity は API 用 query / DTO として設計する。
