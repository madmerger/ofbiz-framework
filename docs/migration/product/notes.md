# Product 移行ノート

## 1. 境界とアダプター

| 境界 | 実装 | 判断 |
| --- | --- | --- |
| 注文 | `OrderPort`、`LocalOrderAdapter` | 読み取りはローカル DB の OrderHeader/OrderItem/ship group/予約を読む。`changeOrderItemStatus` は `StatusValidChange` を検証し OrderItem と OrderStatus を更新する。ヘッダー完了判定・order ECA は別途未実装。 |
| 出荷副作用 | `ShipmentSideEffectPort` | invoice/通知などの ECA 呼出しを明示し、未対応は no-op 注記または 501 として結果に残す。 |
| 会計 | `AccountingPort` | 所有コンポーネント境界を維持。未移植の会計呼び出しを暗黙に成功扱いしない。 |
| 未移植 simple 呼出し | `ProductUnportedPort` | 未対応サービスは 501 を返し、機能状態を明示する。 |

登録規則は `migration/product-springboot/src/main/java/org/apache/ofbiz/migration/product/flow/ProductFlowServiceRegistration.java:56`、注文アダプターは `migration/product-springboot/src/main/java/org/apache/ofbiz/migration/product/adapter/LocalOrderAdapter.java:60`、Port は `adapter/` 以下を参照する（`ProductUnportedPort.java:25`、`AccountingPort.java:23`）。

## 2. ECA と entity/query の再現

OFBiz の ECA/SECA は単なる DB CRUD ではなくタイミングや呼び出し条件を含むため、対象フローで明示的に順序を再現し、副作用が未移植の場合は応答注記または 501 にする。全 ECA を黙って省略しない。view-entity 相当の集計/結合は手書き JPQL として表し、group-by、集計、join をレビュー可能にする。例: OFBiz `InventoryItemDetailForSum` (`applications/datamodel/entitydef/product-entitymodel.xml:2205`) の集計は `migration/product-springboot/src/main/java/org/apache/ofbiz/migration/product/simple/InventoryServices.java:364`、在庫詳細からの合計は同 `:219`。

Minilang の `not-equals` は通常の SQL `<>` と異なり、右辺が非 NULL のとき `(field <> value OR field IS NULL)` を意味する。根拠は `framework/entity/src/main/java/org/apache/ofbiz/entity/util/EntityFinderUtil.java:247`。該当 simple query と Criteria predicate はこの NULL 包含を維持する。

共通 `EntityRegistry.findByEquality` は最大100件に制限するため、Product の集計・反復業務には使用しない。Product 固有 `ProductQueries.findAll(Class, Map)` は同じ Criteria equality 条件を適用し、最大件数を設定せず全結果を返す（`migration/product-springboot/src/main/java/org/apache/ofbiz/migration/product/ProductQueries.java:48`）。101件の InventoryItem を使う集計テストは `migration/product-springboot/src/test/java/org/apache/ofbiz/migration/product/InventoryServicesTest.java:84`。

Product main code に残る `setMaxResults` は次のとおり。`InventoryServices.java:375` と `:397` は Minilang の `first-from-list` 相当で、group-by view entity の先頭結果だけを読む。`PicklistServices.java:1061` と `:1168` は明示的な limit pagination。いずれも equality query の暗黙100件上限とは異なる意図的制限である。

## 3. ローカル seed・heap・実行

`migration/product-springboot/src/main/java/org/apache/ofbiz/migration/product/ProductLocalSeed.java:31` は local プロファイルの H2 専用デモデータを投入する。E2E の Facility/Store/Order/Status 等を用意するが、E2E Product や在庫は API が作成する。実データや秘密情報を seed しない。

テスト worker heap は `migration/product-springboot/build.gradle:364` の `4g`。ローカル実行:

```sh
PRODUCT_DEMO_PASSWORD=local-smoke-only migration/product-springboot/run-local.sh
```

同等に Gradle を直接実行する場合は `PRODUCT_DEMO_PASSWORD=local-smoke-only ./gradlew :migration:product-springboot:bootRun --args='--spring.profiles.active=local'`。デモパスワードは環境変数でのみ渡し、出力・コミットしない。PostgreSQL 本番相当では `application-postgres.yml` の HTTPS 必須設定と DB 環境変数を使用し、プロキシ許可リストは運用側が明示設定する。
