# content 移行ログ（notes）

## 1. 変換判断

| # | 判断 | 根拠 |
| --- | --- | --- |
| N-01 | 新モジュール `migration/content-springboot` は共通 `springboot-common` / `springboot-datamodel` に依存し、`settings.gradle` は編集しない | `migration/*` は自動 include される |
| N-02 | entity-auto カタログは content の servicedef だけから `ofbiz2spring.py` でビルド時生成し（`generateContentCatalog`）、`ServiceCatalog.java` だけを sourceSet に入れる。Entity/Repository は共通 datamodel を使う | 他コンポーネントのサービスと名前が衝突しない。生成物はコミットしない |
| N-03 | 生成器が属性を解決できない定義を `ContentServiceCatalog` で補完した（`removeWebSiteRole` は default-entity-name なし、`addWebSiteRole` / `createWebSiteRole` / `updateWebSiteRole` は interface の implements のみ、mode なし override は IN 扱い） | 共通ツールを変更せずにコンポーネント内で解決するため |
| N-04 | servicedef の `default-value`（`createContent` の `DOCUMENT` / `CTNT_IN_PROGRESS`）は生成カタログに残らないため、`createContent` は handler で既定値と作成者を補ってから共通 `EntityAutoService` に渡す | entity-auto の 409 / 必須検証などは共通実装のまま使う |
| N-05 | `createSurveyMultiResp` / `createSurveyMultiRespColumn` は複合 PK の OUT 項目が `*SeqId` 名でないため共通実装では採番されない。handler でサブシーケンス採番して作成 | OFBiz の entity-auto は複合 PK の OUT 1 項目をサブシーケンスにする |
| N-06 | route / group / SECA（`createDataText`、`updateDataText`、`createTextContent`、`updateTextContent`、`createDataResource` → `createDataResourceRole` → `ensurePartyRole`、`createSurveyQuestion` → `createSurveyQuestionAppl`）は Java のメソッド呼び出しで再現 | 主要フローに必要。Spring 側にサービスエンジンの ECA はない |
| N-07 | 他コンポーネント依存は Port + local adapter に分離: `PartyRolePort`（party の `ensurePartyRole`）、`FinAccountPort`（accounting の FinAccount）、`CatalogSeoPort`（product / content の SEO URL 生成） | humanres の LocalPartyAdapter 方式 |
| N-08 | view-entity は JPA 化されていないため `ContentDataResourceView` と `SurveyQuestionAndAppl` を Java で結合して再現 | 共通 datamodel は通常 Entity のみ |
| N-09 | `/multi` で重複（`ServiceValueFound`）を含む失敗は 409 を返す `ContentMultiErrorAdvice` をコンポーネント側に置いた | 共通の例外ハンドラは 400 を返す。共通を変えずに要件（409）を満たす |
| N-10 | 機密エンティティに SurveyResponse / SurveyResponseAnswer を加えた | CREDIT_CARD・PASSWORD 型の回答を平文で保存する |
| N-11 | 主要フローの権限は OFBiz の permission-service の main-action を `CONTENTMGR_<action>` として handler に登録 | 共通 dispatcher の handler 権限方式 |

## 2. 自動化できた箇所 / 手作業の箇所

- 自動: 棚卸し（`ofbiz_inventory.py`）、entity-auto 176 本のカタログ生成、entity-auto 網羅テストのパラメータ（カタログから 176 ケースを生成）、未移植一覧（棚卸し JSON から生成）。
- 手作業: simple 7 本、主要フローの java 4 本・groovy 7 本、route 2 本・group 2 本、生成カタログの補完、Port/adapter、セキュリティ設定。

## 3. ハマりどころ

- Maven Central が 429。`~/.gradle/init.d` の mirror に加えて、ルート `build.gradle` の `mavenCentral()` / `repo1` が 429 で失敗するため、init script でそれらのリポジトリの content を除外した。プラグインポータルは Maven Central へリダイレクトするので、プラグイン用 mirror も追加した。
- 生成器に content の entitymodel だけを渡すと、`WebPage` / `PortalPage`（extend-entity）や party の `CommunicationEvent` を解決できない。全 entitymodel を渡す。
- 835 エンティティの JPA コンテキストを複数プロファイルで持つとテストが OOM になる。テスト JVM を `maxHeapSize = '3g'` にし、MockMvc の有無でコンテキストが分かれないようにした。
- 共通基盤の更新（`d6254e03b3`: `migration.security.component` 必須、`require-https` 既定 true、Entity API のコンポーネント範囲、update は宣言属性のみ。`3ff99279eb`: 空の INOUT 主キーの自動採番）を取り込み、設定とテストを合わせた。
- minilang の `getWebSitePathAlias` は `alias.pathTo` を返すが、Entity のフィールドは `aliasTo`。OFBiz では常に空になるため、移植では `aliasTo` を `pathTo` として返す（review.md の差分に記載）。
