# WorkEffort E2E レポート（Swagger UI）

- 対象: PR #10、HEAD `87db793f0c`、`migration/workeffort-springboot` を local プロファイル（H2、:8080）で単独起動。旧 OFBiz は起動していない。
- 実行: Devin の E2E テスト機能（testing_agent）が Swagger UI を操作し、`cases.json` のケースを id 順に実行。録画1本（1440×1000、約67秒）。秘密値・Authorization ヘッダー・ビルドログは記録・録画していない。
- 結果: **8ケースすべて期待値どおり**（E2E-06 は注記あり）。

| id | 名称 | 期待値（cases.json） | 実測値 | 判定 |
|---|---|---|---|---|
| WORKEFFORT-E2E-01 | 未認証 401 | 401 | 401 Unauthorized | 合格 |
| WORKEFFORT-E2E-02 | 権限不足 403（readonly が createWorkEffort） | 403 | 403 `Service permission denied` | 合格 |
| WORKEFFORT-E2E-03 | 機密 RateAmount: 閲覧専用ユーザーは 403 | 403 | 403 `Sensitive entity access denied`、値は返らない | 合格 |
| WORKEFFORT-E2E-04 | 機密 RateAmount: 専用権限（auditor）は 200 | 200、1行、rateAmount=125.0 | 200、1行、partyId=DemoEmployee、rateAmount=125.0 | 合格 |
| WORKEFFORT-E2E-05 | entity-auto 作成→取得→更新→削除（WorkEffortType） | 各 200、削除後の照会は [] | 200×5（E2E-TYPE-01 作成、description 更新を確認、削除後 []） | 合格 |
| WORKEFFORT-E2E-06 | simple 代表2本（createWorkEffortNote / createTimesheetForThisWeek） | 各 200 | 200×2（noteId=10000、timesheetId=10000、statusId=TIMESHEET_IN_PROCESS） | 合格（注記） |
| WORKEFFORT-E2E-07 | 主要フロー: タスク作成→担当割当→作業時間記録→完了 | 各 200、最終 currentStatusId=CAL_COMPLETED | 200×7（workEffortId=10000、timesheetId=10001、timeEntryId=10000、hours=2.5、採番 ID を後段に引き継ぎ）、最終 CAL_COMPLETED | 合格 |
| WORKEFFORT-E2E-08 | multi の 409 と全件ロールバック | 409、後続の照会は [] | 409（responseMessage=error、index=1、ServiceValueFound）、直後の照会 200 / [] | 合格 |

## 注記
- E2E-06: 初回はテストドライバーが UTC 時刻のプレースホルダーを置換せずに送信して 500 になり、正しい UTC 時刻で再入力して 200 を確認した（録画に残している）。不正な時刻文字列が 400 ではなく 500 になるのは入力検証の改善候補（残課題）。
- 対象外: PostgreSQL・非 local プロファイル・planner/noworkeffort ユーザーでの UI 操作（PostgreSQL は Testcontainers の smoke テストで確認済み）。
