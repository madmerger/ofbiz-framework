# Manufacturing 移行レビュー

## 検証結果

| 検証 | 件数 / コマンド | 結果 |
| --- | --- | --- |
| Manufacturing acceptance | `ManufacturingAcceptanceTest` 5件 | 成功 |
| Entity-auto lifecycle | `EntityAutoAcceptanceTest` 9件（7 parameterized cases + catalog / INOUT checks） | 成功 |
| Simple services | `SimpleServicesAcceptanceTest` 11件 | 成功 |
| Production flow | `ProductionRunFlowTest` 12件 | 成功 |
| Security / HTTPS / multi rollback | `ManufacturingSecurityTest` 7件、`ManufacturingHttpsSecurityTest` 3件、`MultiRollbackTest` 2件 | 成功 |
| PostgreSQL smoke | `PostgresSmokeTest` 1件（Testcontainers） | 成功 |
| Manufacturing Gradle tests | `MANUFACTURING_DEMO_PASSWORD=<任意値> ./gradlew :migration:manufacturing-springboot:test`（全8クラス、49件） | `BUILD SUCCESSFUL` |
| 共通基盤 regression | `./gradlew :migration:springboot-common:test` 18件（共通基盤は今回変更なし。前回検証結果） | 成功 |
| Checkstyle / Codenarc | リポジトリ root の `./gradlew checkstyleMain checkstyleTest codenarcMain codenarcTest`（local-only で Node/npm asset tasks を除外） | 成功（17 tasks）。初回の除外なし実行は Node 24.16.0 の artifact 解決が Maven Central HTTP 429 で失敗したため、`nodeSetup`、`npmInstall`、Swagger/Redoc asset tasks を除外して再実行。Manufacturing サブプロジェクト自体には該当 task なし |
| local E2E curl | `python3 /home/ubuntu/work/manufacturing-e2e.py`、`cases.json` 全リクエスト | 29/29 成功 |
| Swagger UI | `curl http://127.0.0.1:18787/swagger-ui/index.html` | HTTP 200。検証後にサーバー停止 |

OUT モードの主キーを持つ entity-auto create は、Manufacturing の生成 catalog 18件内に存在しなかったため、OUT 専用テストは追加していない。INOUT 主キー `createMrpEventType.mrpEventTypeId` の空値自動採番と明示値維持は acceptance test で確認した。共通基盤は upstream の `23f8c75e65` を merge したのみで、独自変更はない。

## OFBiz との差分

- Manufacturing の Java 6件 / Groovy 1件と simple 8件を対象とし、MRP、在庫引当、原価計算、画面検索・編集、帳票等は未移植一覧に記載した。
- getManufacturingComponents は1階層の BOM を返し、構成 tree / formula / configurator を再現しない。
- 生産 run のカレンダー調整、PartyAssignment、CostCalc、DOC_PRINTED 時の自動在庫引当は未再現。
- updateProductionRunTask の time entry / issueRequiredComponents、task status の issueAllComponents / indirect cost calculation は未再現。
- task start は有効な先行タスクの COMPLETED を要求する。`issueAllComponents` は無視する。
- 有効期間が重なる同一部品の BOM 行は、OFBiz と同じく production run の作成を失敗させる。WorkEffortGoodStandard の重複を永続化前に検出し、データ不整合として部品 ID を含む400を返す。
- common multi exception を Manufacturing advice で409へ写像する。details は `notes.md`。

## 既知の制約

- UI / screen / form / menu / FTL、MRP、在庫・注文連携、複雑な BOM 数量計算は対象外。
- local profile のデモ seed はテストと curl 検証専用。実データ移行ではない。
- Manufacturing サブプロジェクトには Checkstyle / Codenarc タスクがない。root の style task は Node/npm asset tasks をローカルで除外して実行した。
- 共通基盤・datamodel・tools と settings.gradle は変更していない。
