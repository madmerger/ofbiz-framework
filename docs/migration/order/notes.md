# Order 移行ログ（notes）

## 1. 変換判断

| ID | 判断 | 根拠 |
| --- | --- | --- |
| N-01 | `migration/order-springboot` を独立した root Gradle subproject とする | 既存の root 設定が `migration/*` の build script を自動検出する。保護対象の `settings.gradle` は編集しない |
| N-02 | entity-auto 220 本は共通 `ServiceCatalog` と `EntityAutoService` に委譲 | OFBiz entity-auto は CRUD の汎用処理であり、ServiceCatalog が宣言済みの入力・権限・対象 Entity を保持する |
| N-03 | 生成カタログは `registry.registerCatalog("order", ServiceCatalog.all())` で直接登録 | 共通基盤更新で生成レコードを直接登録できるようになった |
| N-04 | component 名を全 profile 共通で `order` に固定 | 共通 API の URL component ゲートと Entity API の component-scoped entity 許可リストを有効化する |
| N-05 | extra Entity は共通カタログ外で、状態・履歴参照に必要なものだけを列挙 | `OrderItem`, `OrderStatus`, Shipment/Invoice の状態確認で使う。機密 Entity を不用意に公開しない |
| N-06 | 空の INOUT 主キーを共通基盤の採番に委譲 | ベース `3ff99279eb` の変更で空の INOUT `SeqId` も generate 可能になった。Order 側に採番ロジックを重複実装しない |
| N-07 | simple サービスの Entity/外部連携を Order Bean/Port に切り出す | 同一 Spring transaction のまま依存先コンポーネントと交換可能にする |
| N-08 | `ShipmentReceiptPort` と `CommunicationEventPort` を設ける | Product/Party の Entity を直接 Order の業務サービスに混在させない |
| N-09 | `createCustRequestFromCart` は `cartItems` を受ける | API には OFBiz の session ShoppingCart が存在しないため |
| N-10 | ECA/SECA の状態連鎖は対象フローで明示呼び出し | common の dispatcher は OFBiz ECA engine を再現しない。状態遷移をサービスの Tx 内で明示する |
| N-11 | local/test で seed XML の必要行をロード | H2 の空 DB でも Order 状態・遷移の代表フローを再現する |
| N-12 | local demo password は環境変数からのみ取得 | リポジトリ/YAML に平文秘密情報を置かない |
| N-13 | Order multi endpoint を明示して 409 を保持 | 共通 multi の失敗 status が 400 に正規化されるため、Order API では MigrationException の status を保持する |
| N-14 | OUT 主キーは入力値を無視し共通採番を使う | ベース `23f8c75e65` の変更に合わせ、`createCustRequestStatus` の回帰テストで caller key と返却 key を区別する |

## 2. 作業中に確認した点・注意点

| ID | 内容 | 対処 |
| --- | --- | --- |
| P-01 | base branch に common の変更が追加された | `git fetch origin && git merge origin/devin/1793006105-springboot-common` で fast-forward を取り込み、rebase は行わなかった |
| P-02 | 生成 catalog 登録 API が base 上で変更された | `OrderCatalogRegistrar` は ServiceDefinition 変換をせず `ServiceCatalog.all()` を直接登録 |
| P-03 | entity-auto update が未宣言 field を拒否する | 網羅テストは service params に限定し、OrderItemType の未宣言 field で 400 を確認 |
| P-04 | Entity API は component-scoped allowlist になった | 実際に照会する非 catalog Entity を `extra-entities` に追加し、機密確認には catalog Entity `OrderPaymentPreference` を使用 |
| P-05 | HTTPS が common の既定で必須になった | local/test で `migration.security.require-https: false` を明示し、Postgres は true を維持 |
| P-06 | 初期 Order shipment group は既に存在する | entity-auto の空 INOUT 採番回帰では別の `createOrderItemShipGroup` を呼び、返却キーで照会する |
| P-07 | cart session は REST 実行時に利用できない | `createCustRequestFromCart` の入力代替を spec/review に明記 |
| P-08 | OUT 主キーに caller value が含まれても採番結果を優先する | `createCustRequestStatus` で返却 ID の行だけを照会するテストを追加 |
| P-09 | 一部 entity-auto update/delete に entity-auto create fixture がない | 4 update・4 delete の成功経路は未検証。実行済み件数とサービス名は `review.md` に列挙し、全 220 CRUD を実行したとは扱わない |
| P-10 | Order サブプロジェクトには Checkstyle/CodeNarc task がない | root の静的解析は migration source を対象にしない。AC テスト名 `acNN_...` は指定形式を維持し、Spring transaction proxy が必要な非 final bean も Checkstyle の DesignForExtension 警告に合わせて変更しない |
