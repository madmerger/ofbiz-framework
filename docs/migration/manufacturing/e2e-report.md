# Manufacturing E2E 実施報告

## 実施条件

- 対象: PR #8 の HEAD `8f00f9e621`（ブランチ `devin/1793009070-manufacturing-springboot`）
- 起動したもの: `migration/manufacturing-springboot` だけ（`local` プロファイル、H2、`run-local.sh`）。旧 OFBiz は起動していない。
- 操作: Devin の E2E テスト機能（testing_agent）で、ブラウザの Swagger UI（`/swagger-ui/index.html`）の Try it out / Execute を使い、`cases.json` の末端 29 リクエストを id 順に実行した。
- 前のケースで返った ID は後続ケースで使った（製造指図 `10000`、タスク `10001`・`10002`）。
- 録画: 1 本、1600×1000、約 52 秒。各ケースの開始時に、ケース ID と名称を画面に表示した。
- 秘密情報: パスワードは環境変数 `MANUFACTURING_DEMO_PASSWORD` で渡した。Swagger の Authorize 欄では伏字で、録画には映っていない。ビルドログと依存のインストールは録画に含めていない。

## 結果（cases.json の id ごと）

判定は、期待 HTTP と期待する主要な値（cases.json の `expected`）を、Swagger UI に表示された実際の応答と照合したもの。

| ケース ID | セクション | 操作 | ユーザー | 期待値 | 実測値 | 判定 |
| --- | --- | --- | --- | --- | --- | --- |
| MANUFACTURING-E2E-01 | 未認証 API | `GET /api/manufacturing/entities/Product` | 未認証 | 401 | 401 | 合格 |
| MANUFACTURING-E2E-02 | 権限不足 | `GET /api/manufacturing/entities/Product` | ofbtoolsonly | 403 | 403 | 合格 |
| MANUFACTURING-E2E-03-01 | 機密 CostComponent | `GET /api/manufacturing/entities/CostComponent?costComponentId=MFG_COST_DEMO` | mfgviewer | 403 | 403 | 合格 |
| MANUFACTURING-E2E-03-02 | 機密 CostComponent | `GET /api/manufacturing/entities/CostComponent?costComponentId=MFG_COST_DEMO` | mfgwriter | 200 | 200 | 合格 |
| MANUFACTURING-E2E-04-01 | カレンダー CRUD | `POST /api/manufacturing/services/createCalendar` | mfgwriter | 200、`calendarId=E2E_CAL` | 200、`calendarId=E2E_CAL` | 合格 |
| MANUFACTURING-E2E-04-02 | カレンダー CRUD | `GET /api/manufacturing/entities/TechDataCalendar/one?calendarId=E2E_CAL` | mfgwriter | 200、`description=E2E` | 200、`description=E2E` | 合格 |
| MANUFACTURING-E2E-04-03 | カレンダー CRUD | `POST /api/manufacturing/services/updateCalendar` | mfgwriter | 200 | 200 | 合格 |
| MANUFACTURING-E2E-04-04 | カレンダー CRUD | `GET /api/manufacturing/entities/TechDataCalendar/one?calendarId=E2E_CAL` | mfgwriter | 200、`description=updated` | 200、`description=updated` | 合格 |
| MANUFACTURING-E2E-04-05 | カレンダー CRUD | `POST /api/manufacturing/services/createCalendarExceptionDay` | mfgwriter | 200 | 200 | 合格 |
| MANUFACTURING-E2E-04-06 | カレンダー CRUD | `POST /api/manufacturing/services/removeCalendarExceptionDay` | mfgwriter | 200 | 200 | 合格 |
| MANUFACTURING-E2E-04-07 | カレンダー CRUD | `GET /api/manufacturing/entities/TechDataCalendarExcDay?calendarId=E2E_CAL` | mfgwriter | 200、`recordCount=0` | 200、`recordCount=0` | 合格 |
| MANUFACTURING-E2E-05-01 | BOM 自己参照と線形式 | `POST /api/manufacturing/services/createBOMAssoc` | mfgwriter | 400 | 400 | 合格 |
| MANUFACTURING-E2E-05-02 | BOM 自己参照と線形式 | `POST /api/manufacturing/services/linearComponentFormula` | mfgwriter | 200、`quantity=4` | 200、`quantity=4` | 合格 |
| MANUFACTURING-E2E-06-01 | BOM から2タスクの生産完了 | `POST /api/manufacturing/services/createBOMAssoc` | mfgwriter | 200 | 200 | 合格 |
| MANUFACTURING-E2E-06-02 | BOM から2タスクの生産完了 | `POST /api/manufacturing/services/createBOMAssoc` | mfgwriter | 200 | 200 | 合格 |
| MANUFACTURING-E2E-06-03 | BOM から2タスクの生産完了 | `POST /api/manufacturing/services/createProductionRun` | mfgwriter | 200、`productionRunId=non-empty` | 200、`productionRunId=10000` | 合格 |
| MANUFACTURING-E2E-06-04 | BOM から2タスクの生産完了 | `GET /api/manufacturing/entities/WorkEffort?workEffortParentId=10000&workEffortTypeId=PROD_ORDER_TASK` | mfgwriter | 200、`taskCount=2` | 200、`taskCount=2` | 合格 |
| MANUFACTURING-E2E-06-05 | BOM から2タスクの生産完了 | `POST /api/manufacturing/services/changeProductionRunStatus` | mfgwriter | 200、`newStatusId=PRUN_DOC_PRINTED` | 200、`newStatusId=PRUN_DOC_PRINTED` | 合格 |
| MANUFACTURING-E2E-06-06 | BOM から2タスクの生産完了 | `POST /api/manufacturing/services/changeProductionRunTaskStatus` | mfgwriter | 200、`newStatusId=PRUN_RUNNING` | 200、`newStatusId=PRUN_RUNNING` | 合格 |
| MANUFACTURING-E2E-06-07 | BOM から2タスクの生産完了 | `POST /api/manufacturing/services/updateProductionRunTask` | mfgwriter | 200、`quantityProduced=2` | 200、`quantityProduced=2` | 合格 |
| MANUFACTURING-E2E-06-08 | BOM から2タスクの生産完了 | `POST /api/manufacturing/services/changeProductionRunTaskStatus` | mfgwriter | 200、`newStatusId=PRUN_COMPLETED` | 200、`newStatusId=PRUN_COMPLETED` | 合格 |
| MANUFACTURING-E2E-06-09 | BOM から2タスクの生産完了 | `POST /api/manufacturing/services/changeProductionRunTaskStatus` | mfgwriter | 200、`newStatusId=PRUN_RUNNING` | 200、`newStatusId=PRUN_RUNNING` | 合格 |
| MANUFACTURING-E2E-06-10 | BOM から2タスクの生産完了 | `POST /api/manufacturing/services/updateProductionRunTask` | mfgwriter | 200、`quantityProduced=2` | 200、`quantityProduced=2` | 合格 |
| MANUFACTURING-E2E-06-11 | BOM から2タスクの生産完了 | `POST /api/manufacturing/services/changeProductionRunTaskStatus` | mfgwriter | 200、`newStatusId=PRUN_COMPLETED` | 200、`newStatusId=PRUN_COMPLETED` | 合格 |
| MANUFACTURING-E2E-06-12 | BOM から2タスクの生産完了 | `GET /api/manufacturing/entities/WorkEffort/one?workEffortId=10000` | mfgwriter | 200、`currentStatusId=PRUN_COMPLETED` | 200、`currentStatusId=PRUN_COMPLETED` | 合格 |
| MANUFACTURING-E2E-06-13 | BOM から2タスクの生産完了 | `POST /api/manufacturing/services/changeProductionRunStatus` | mfgwriter | 200、`newStatusId=PRUN_CLOSED` | 200、`newStatusId=PRUN_CLOSED` | 合格 |
| MANUFACTURING-E2E-06-14 | BOM から2タスクの生産完了 | `GET /api/manufacturing/entities/WorkEffort/one?workEffortId=10000` | mfgwriter | 200、`currentStatusId=PRUN_CLOSED` | 200、`currentStatusId=PRUN_CLOSED` | 合格 |
| MANUFACTURING-E2E-07-01 | multi 重複時の全件 rollback | `POST /api/manufacturing/services/createCalendar/multi` | mfgwriter | 409 | 409 | 合格 |
| MANUFACTURING-E2E-07-02 | multi 重複時の全件 rollback | `GET /api/manufacturing/entities/TechDataCalendar/one?calendarId=E2E_MULTI` | mfgwriter | 404 | 404 | 合格 |

## セクションごとの集計

| 親セクションID | 名称 | 末端件数 | 合格件数 | 不一致件数 | 判定 |
| --- | --- | --- | --- | --- | --- |
| MANUFACTURING-E2E-01 | 未認証 API | 1 | 1 | 0 | 合格 |
| MANUFACTURING-E2E-02 | 権限不足 | 1 | 1 | 0 | 合格 |
| MANUFACTURING-E2E-03 | 機密 CostComponent | 2 | 2 | 0 | 合格 |
| MANUFACTURING-E2E-04 | カレンダー CRUD | 7 | 7 | 0 | 合格 |
| MANUFACTURING-E2E-05 | BOM 自己参照と線形式 | 2 | 2 | 0 | 合格 |
| MANUFACTURING-E2E-06 | BOM から2タスクの生産完了 | 14 | 14 | 0 | 合格 |
| MANUFACTURING-E2E-07 | multi 重複時の全件 rollback | 2 | 2 | 0 | 合格 |

**合計: 29 件中 29 件が合格**（親セクションは 7 件中 7 件が合格）

## 実施時の注意

- 01（未認証）では、ブラウザの Basic 認証ダイアログが開いた。ダイアログを閉じ、Swagger UI に表示された 401 を実測値とした。
- 実行の途中で、UI 操作の待機条件と応答を取得する箇所を調整して続行した。04-02 は実行済みの応答を取り直した。アプリの応答が期待値と違ったケースはない。

## 範囲外（この E2E では確認していない）

- cases.json 以外のサービス: JUnit の `EntityAutoAcceptanceTest` などで確認
- PostgreSQL プロファイル: `PostgresSmokeTest` で確認
- 既定プロファイルの HTTPS 強制: `ManufacturingHttpsSecurityTest` で確認
- 未移植の機能: `unported.md` を参照
