# Manufacturing 移行レビュー

## 検証結果

| 検証 | 件数 / コマンド | 結果 |
| --- | --- | --- |
| Manufacturing acceptance | `ManufacturingAcceptanceTest` 7件 | 成功 |
| Manufacturing security | `ManufacturingSecurityTest` 1件 | 成功 |
| PostgreSQL smoke | `PostgresSmokeTest` 1件 | 成功 |
| 共通基盤 regression | `./gradlew :migration:springboot-common:test` 18件 | 成功 |
| 統合 Gradle 実行 | `MANUFACTURING_DEMO_PASSWORD=<任意値> MANUFACTURING_ADMIN_PASSWORD_HASH=<テスト用値> ./gradlew :migration:manufacturing-springboot:test :migration:springboot-common:test` | `BUILD SUCCESSFUL` |
| Checkstyle / Codenarc | モジュール build に適用設定なし | 該当タスクなし |
| local E2E curl | `cases.json` 27リクエスト | 27/27 成功 |
| Swagger UI | `/swagger-ui/index.html` | HTTP 200 |

OUT モードの主キーを持つ entity-auto create は、Manufacturing の生成 catalog 18件内に存在しなかったため、OUT 専用テストは追加していない。INOUT 主キー `createMrpEventType.mrpEventTypeId` の空値自動採番と明示値維持は acceptance test で確認した。共通基盤は upstream の `23f8c75e65` を merge したのみで、独自変更はない。

## OFBiz との差分

- Manufacturing の Java 6件 / Groovy 1件と simple 8件を対象とし、MRP、在庫引当、原価計算、画面検索・編集、帳票等は未移植一覧に記載した。
- getManufacturingComponents は1階層の BOM を返し、構成 tree / formula / configurator を再現しない。
- 生産 run のカレンダー調整、PartyAssignment、CostCalc、DOC_PRINTED 時の自動在庫引当は未再現。
- updateProductionRunTask の time entry / issueRequiredComponents、task status の issueAllComponents / indirect cost calculation は未再現。
- 生産 run の task precedence は未検証・未実装。
- common multi exception を Manufacturing advice で409へ写像する。details は `notes.md`。

## 既知の制約

- UI / screen / form / menu / FTL、MRP、在庫・注文連携、複雑な BOM 数量計算は対象外。
- local profile のデモ seed はテストと curl 検証専用。実データ移行ではない。
- 共通基盤・datamodel・tools と settings.gradle は変更していない。
