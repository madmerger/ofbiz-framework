# 実装レビューと検証記録

## 件数

| 区分 | 件数 | 根拠 |
|---|---:|---|
| Entity-auto | 95 | 3 service definition XML を namespace-aware parse |
| Simple | 33 | 3 service definition XML を namespace-aware parse |
| 移植 Java | 3 | getWorkEffort / getWorkEffortAssignedTasks / getWorkEffortEventsByPeriod |
| 移植 Groovy | 8 | create/update/delete・作成割当・割当更新/quick assign |
| 未移植 Java | 9 | `unported.md` |
| 未移植 Groovy | 13 | `unported.md` |

inventory tool が生成する `inventory.json` の `services` 数は service definition files のユニーク登録数であり、engine別件数とは集計基準が異なる。

## コマンドと結果

| コマンド | 結果 |
|---|---|
| `python3 migration/tools/ofbiz_inventory.py --component applications/workeffort --entitymodel applications/datamodel/entitydef/workeffort-entitymodel.xml --out docs/migration/workeffort/inventory/entitymodel` | 実行済み: entities=41, entity-auto=95, requests=96, services=167, view_entities=0, view_maps=69 |
| `python3 migration/tools/ofbiz_inventory.py --component applications/workeffort --entitymodel applications/workeffort/entitydef/entitymodel_view.xml --out docs/migration/workeffort/inventory/view-model` | 実行済み: entities=0, entity-auto=95, requests=96, services=167, view_entities=33, view_maps=69 |
| `./gradlew --console=plain :migration:workeffort-springboot:tasks --all` | `test` / `checkstyleMain`を確認。Groovy source setがなく `codenarcMain` taskなし。 |
| `./gradlew --console=plain --continue :migration:workeffort-springboot:test :migration:workeffort-springboot:checkstyleMain :migration:springboot-common:test :migration:humanres-springboot:test` | `BUILD SUCCESSFUL`。WorkEffort 18 passed / 0 failed / 0 skipped、Checkstyle main passed、common 18 passed / 0 failed / 0 skipped、humanres test UP-TO-DATE (XML: 162 passed / 0 failed / 0 skipped)。 |
| `bash migration/workeffort-springboot/run-local.sh` + local-profile curl | 実行済み。WORKEFFORT-E2E-01〜08 の認証/権限、RateAmount、Entity-auto CRUD、simple、主要flow、multi 409/rollbackの各status期待値を確認。multi失敗後のWorkEffortStatus照会は `[]`。サーバー停止を確認。詳細ログ: `/home/ubuntu/work/workeffort/curl-check.txt`。 |
| `:migration:workeffort-springboot:codenarcMain` | 未実行。現行moduleはJava source setのみでtaskが存在しない。 |

## OFBiz との差分・制約

- 生成 catalog record に definition file provenance がなく、build-time generatorへ対象3 servicedefを明示した。
- `LocalWorkEffortAdapter` RateAmount候補順はOFBizの全条件/優先順位を再現していない。
- `indexWorkEffortKeywords` は WorkEffort / attribute / note text の token weight を更新するが、`WorkEffortKeywordIndex` の stop-word・stemming・content/data-resource 抽出・backend index連携は未移植。
- `getWorkEffortEventsByPeriod` は UTC の Calendar period buckets と date-overlap、party/facility/fixed-asset/type/canceled filter を扱う。OFBizの `TemporalExpression` recurrence、TimeZone/Locale依存の境界、`entityExprList`、`calendarEntriesByDateRange` grouping は未移植。
- `getICalWorkEfforts` は assignment・fixed-asset・association candidateを集約するが、ICS serialization、recurrence展開、完全な view-entity filter semantics は未移植。`getPartyICalUrl` は `PartyContactWithPurpose` の日付有効行を対象とする。
- `addTimesheetToInvoice` / `addWorkEffortTimeToInvoice` は invoice item orchestration を簡略化しており、OFBizの期間overlap・PartyRate percentage・combineInvoiceItem・amount/description groupingと完全互換でない。
- `copyWorkEffortAssocs` は `deepCopy=Y` の場合も `copyWorkEffort` がWorkEffort catalogに無い環境ではshallow copyに落ちる。OFBizのcopy service dependencyは未移植。
- simple 33サービスについてカタログ登録と一部重点正常/拒否分岐を検証したが、各サービスの成功結果を個別にassertする33ケースは未完成。
- 共通基盤を `d6254e03b3`、`3ff99279eb`、`23f8c75e65` までfast-forward mergeし、直接カタログ登録、component/entity scope、INOUTおよびOUT主キー挙動を反映した。
- test acceptance は RateAmount 行を永続化して auditor応答に非空の `125.00` を確認する。ローカルcurl実行結果は `/home/ubuntu/work/workeffort/curl-check.txt` に追記する。
- Calendar period query / ICS、CalDAV、UI Widgets、非移植サービスは `unported.md` を参照。
- 実装はworktreeにあり、commit/pushは行っていない。PR・CI対応はリード側の作業。
