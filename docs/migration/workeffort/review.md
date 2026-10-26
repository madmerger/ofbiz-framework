# 実装レビューと検証記録

## 移行件数

| 区分 | 件数 | 根拠 |
|---|---:|---|
| Entity-auto | 95 | 3 service definition XML の登録対象 |
| Simple | 33 | 3 service definition XML の登録対象 |
| 移植 Java | 3 | getWorkEffort / getWorkEffortAssignedTasks / getWorkEffortEventsByPeriod |
| 移植 Groovy | 8 | create/update/delete・作成割当・割当更新/quick assign |
| 未移植 Java | 9 | `unported.md` |
| 未移植 Groovy | 13 | `unported.md` |
| 未移植 view-entity | 33 | `unported.md` |
| 未移植 FTL | 11 | `unported.md` |
| 未移植 Widget XML | 35 | `unported.md` |

Inventory JSON の `services` は service definition files 上のユニーク数であり、engine別の移植件数とは集計基準が異なる。

## テスト結果

最終実行コマンド:

```sh
./gradlew --console=plain :migration:workeffort-springboot:test :migration:workeffort-springboot:checkstyleMain :migration:workeffort-springboot:checkstyleTest
```

結果: `BUILD SUCCESSFUL in 1m 33s`、21 actionable tasks（3 executed / 18 up-to-date）。Test XML 合計は164 passed / 0 failed / 0 errors / 0 skipped。`checkstyleMain` / `checkstyleTest` とも成功。

| Test class | Cases | Passed | Failed | Skipped |
|---|---:|---:|---:|---:|
| `EntityAutoExecutionAcceptanceTest` | 95 | 95 | 0 | 0 |
| `WorkEffortAcceptanceTest` | 31 | 31 | 0 | 0 |
| `WorkEffortSimpleServicesTest` | 33 | 33 | 0 | 0 |
| `HttpsSecurityTest` | 1 | 1 | 0 | 0 |
| `LocalSpringdocProfileTest` | 1 | 1 | 0 | 0 |
| `SpringdocProfileTest` | 1 | 1 | 0 | 0 |
| `PostgresSmokeTest` | 1 | 1 | 0 | 0 |
| `WorkEffortServiceRegistrationTest` | 1 | 1 | 0 | 0 |
| **合計** | **164** | **164** | **0** | **0** |

## local profile curl 実測

`bash migration/workeffort-springboot/run-local.sh` で local profile を単独起動し、`MIGRATION_DEMO_PASSWORD` は一時生成して子プロセスの環境変数としてだけ渡した。curl認証設定は標準入力経由とし、秘密値はログ・文書へ保存していない。

| Case | 実測 |
|---|---|
| WORKEFFORT-E2E-01 | 未認証 Entity GET: 401 |
| WORKEFFORT-E2E-02 | readonly create service: 403 |
| WORKEFFORT-E2E-03 | readonly RateAmount: 403 |
| WORKEFFORT-E2E-04 | auditor RateAmount: 200、DemoEmployeeの1行、125.0 |
| WORKEFFORT-E2E-05 | WorkEffortType entity-auto CRUD: 各200、削除後のreadは空配列 |
| WORKEFFORT-E2E-06 | createWorkEffortNote / createTimesheetForThisWeek: 各200 |
| WORKEFFORT-E2E-07 | create → assign → timesheet → time entry → CAL_IN_PROGRESS → CAL_COMPLETED → get: 全件200、最終status CAL_COMPLETED |
| WORKEFFORT-E2E-08 | duplicate multi: 409、失敗後のWorkEffortStatus照会: 空配列 |

RateAmount seed値はLocalSeedData由来。E2Eの詳細なmethod/path/user/body/ID由来は `/home/ubuntu/work/workeffort/e2e-requests.md`、status記録は `/home/ubuntu/work/workeffort/curl-check.txt`。終了時にサーバーを停止し、port 8080が閉じたことを確認した。

## 実行したinventoryコマンド

| コマンド | 結果 |
|---|---|
| `python3 migration/tools/ofbiz_inventory.py --component applications/workeffort --entitymodel applications/datamodel/entitydef/workeffort-entitymodel.xml --out docs/migration/workeffort/inventory/entitymodel` | entities=41, entity-auto=95, requests=96, services=167, view_entities=0, view_maps=69 |
| `python3 migration/tools/ofbiz_inventory.py --component applications/workeffort --entitymodel applications/workeffort/entitydef/entitymodel_view.xml --out docs/migration/workeffort/inventory/view-model` | entities=0, entity-auto=95, requests=96, services=167, view_entities=33, view_maps=69; duplicate output was removed after comparing matching root files |

`inventory/dependencies.md` は `inventory/` 直下と `entitymodel/` の入力元XMLを説明する。`inventory/entitymodel/` は保持し、重複する `inventory/view-model/` は削除済み。

## OFBizとの差分・既知の制約

- Generated catalog record 自体は定義ファイル provenance を保持しないため、generatorの入力をWorkEffortの3 servicedefに限定し、生成recordをregistryへ直接登録する。
- `LocalWorkEffortAdapter` のRateAmount候補順はOFBizの全条件・優先順位と完全一致しない。
- `indexWorkEffortKeywords` は WorkEffort/name/description、attribute、note等からkeywordを再作成し、create/store ECAから呼び出す。OFBiz `WorkEffortKeywordIndex` のstop-word・stemming・content/data-resource抽出・backend index連携は未移植。
- `getWorkEffortEventsByPeriod` はUTCのCalendar period buckets、期間overlap、party/facility/fixed-asset/type/canceled filterを扱う。OFBizの`TemporalExpression` recurrence、TimeZone/Locale依存境界、`entityExprList`、`calendarEntriesByDateRange` groupingは未移植。
- `getICalWorkEfforts` はassignment、fixed-asset、association候補をJPA queryで集約する。OFBiz view-entityの全filter意味論、ICS serialization、recurrence展開とは差がある。`getPartyICalUrl` は日付有効なICAL_URL / PRIMARY_EMAIL contact purposeを参照する。
- `addTimesheetToInvoice` / `addWorkEffortTimeToInvoice` はinvoice item orchestrationを簡略化し、期間overlap・PartyRate percentage・combineInvoiceItem・amount/description groupingを完全再現しない。
- `copyWorkEffortAssocs` は `deepCopy=Y` の場合でも、環境内にcopyWorkEffortがないとshallow copyに落ちる。
- SECA/EECA・画面Widget/FTL・view-entityの未移植一覧と件数は `unported.md`、permission/action・seed・ECA対応表は `notes.md` を参照。

## 実行時の注意

Gradle出力に `Plugins directory not found.` とGradle 10互換性に関するdeprecation noticeがあったが、指定テスト/Checkstyleコマンドは成功した。`codenarcMain` は今回の検証対象ではなく、Java-only moduleにGroovy source setはない。
