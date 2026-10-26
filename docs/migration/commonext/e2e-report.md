# commonext Spring Boot 移行 E2E 実行結果

## 実行環境・方法

- 対象 PR: https://github.com/madmerger/ofbiz-framework/pull/7
- HEAD: `6d2109a6448265da35c41e99f0eb9efb1a0ab5b5`
- ブランチ: `devin/1793009436-commonext-springboot`
- 実行日: 2026-10-26、リクエスト実行時間帯 11:26〜11:36 UTC（20:26〜20:36 JST）。
- 起動先: `http://localhost:8080`、`commonext-springboot` 単独、`local` プロファイル、Java 17、H2インメモリDB・local adapter/seed。
- 既存プロセス PID `61402` の `/actuator/health` が HTTP 200 / `{"status":"UP"}` であることを先に確認し、再起動・ビルド・依存インストールは行っていない。旧 OFBiz の起動は行っていない。
- 操作画面: `http://localhost:8080/swagger-ui/index.html`。Chrome 137、画面録画1440×1000、ブラウザ内表示1439×912。
- `cases.json` の01〜16を順番に実行。Swagger UIの展開、Try it out、パラメーター・JSON入力、Executeを利用。ユーザーは同UIのAuthorize／Logoutで切替。HTTP APIの直接呼出しでケースを代替していない。
- ブラウザ操作にはPlaywright/CDPも利用。実応答のstatus/bodyを証跡JSONとして採取し、同じ応答をSwagger UI上で確認した。
- ケースIDと名称は画面上部に固定バナー表示。秘密漏えい防止のため、ブラウザ表示のみCSSでSwagger生成curl欄を非表示にした。API・認証処理・応答値は改変していない。
- パスワードは稼働プロセス環境からメモリ上で取得し、マスク入力欄にのみ入力。平文およびAuthorizationヘッダーは録画・スクリーンショット・本レポートに記載しない。

## ケース照合

判定基準は `docs/migration/commonext/cases.json`。08の実採番値 `10000` を09/15/16で照合した。

| ID | 名称 | 期待値 | 実測値（status・主要値） | 結果 |
|---|---|---|---|---|
| COMMONEXT-E2E-01 | 未認証アクセス | 401 | 401、`error=Unauthorized`、対象path一致 | 合格 |
| COMMONEXT-E2E-02 | base-permission(SETUP)なし | 403 | nosetupで403、`error=Forbidden` | 合格 |
| COMMONEXT-E2E-03 | 閲覧専用ユーザーの更新禁止 | 403 | readonlyで403、`errorMessage=Service permission denied` | 合格 |
| COMMONEXT-E2E-04 | 閲覧専用ユーザーの機密UserLogin参照禁止 | 403、Sensitive entity access denied | 403、`errorMessage=Sensitive entity access denied` | 合格 |
| COMMONEXT-E2E-05 | 専用権限による機密UserLogin参照・マスク | 200、userLoginId/partyId=admin、currentPassword=[REDACTED] | editorで200、`userLoginId=admin`、`partyId=admin`、`currentPassword=[REDACTED]` | 合格 |
| COMMONEXT-E2E-06 | entity-auto 0件・未登録CRUD | 404、Unknown service: createNoteData | 404、`errorMessage=Unknown service: createNoteData` | 合格 |
| COMMONEXT-E2E-07 | simple 0件・他コンポーネントURL | humanres/NoteDataが404 | 404、`errorMessage=Unknown component: humanres` | 合格 |
| COMMONEXT-E2E-08 | createSystemInfoNote | 200、success、noteId採番 | 200、`responseMessage=success`、`noteId=10000` | 合格 |
| COMMONEXT-E2E-09 | 一覧先頭・日時降順 | 200、先頭noteId=08、E2E system note、SYSTEMNOTE、admin | 200、先頭`10000 / E2E system note / SYSTEMNOTE / admin`。続くIDは100/101、noteDateTime降順 | 合格 |
| COMMONEXT-E2E-10 | 最新ノート取得 | 200、lastSystemInfoNote1.noteInfo=E2E system note | 200、`lastSystemInfoNote1.noteId=10000`、`noteInfo=E2E system note` | 合格 |
| COMMONEXT-E2E-11 | 通信イベント・タスク件数 | 200、Open communication events: 1、Assigned and not completed tasks: 2 | 200、`systemInfoStatus[0].noteInfo=Open communication events: 1`、`[1].noteInfo=Assigned and not completed tasks: 2` | 合格 |
| COMMONEXT-E2E-12 | スクリプト入力拒否 | 400、Unsafe text in noteInfo | 400、`errorMessage=Unsafe text in noteInfo` | 合格 |
| COMMONEXT-E2E-13 | multi 2件目失敗 | 400、errors[0].index=1、Unsafe text in noteInfo | 400、`errors[0].index=1`、`errors[0].errorMessage=Unsafe text in noteInfo` | 合格（依頼文409との差分あり） |
| COMMONEXT-E2E-14 | multi全件ロールバック | 200、E2E multi 1が存在しない | 200、全3件のnoteInfoに`E2E multi 1`なし。IDは10000/100/101 | 合格 |
| COMMONEXT-E2E-15 | 作成ノート削除 | 200、success | `noteId=10000`で実行、200、`responseMessage=success` | 合格 |
| COMMONEXT-E2E-16 | 削除後一覧確認 | 200、08のnoteIdが存在しない | 200、全2件のIDは100/101。`10000`は存在しない | 合格 |

## 409→400 の既知差分

依頼文のmulti失敗時期待値は409だが、共通基盤の `MultiServiceException` は400を返す。ケース定義も400である。実測も400かつエラーindex=1であり、14で1件目の `E2E multi 1` が保存されていないことを確認した。**409を検証できた、または409仕様を満たしたという意味ではない。** 本実行ではアプリ修正を行っていない。

## 主要スクリーンショット

### 認証・権限

| 01：未認証401 | 02：SETUPなし403 |
|---|---|
| ![01 未認証401](https://devin-gtm.devinenterprise.com/attachments/bf0ab7e3-d566-45ea-987a-79b631881314/01.png) | ![02 SETUPなし403](https://devin-gtm.devinenterprise.com/attachments/c2da70a1-f136-4035-b15d-3260150f8347/02.png) |

### 機密エンティティの権限差

| 04：readonly拒否403 | 05：editor許可200・機密値マスク |
|---|---|
| ![04 機密403](https://devin-gtm.devinenterprise.com/attachments/a6f5a97f-cb4c-4f3e-be73-023d81d1d94c/04.png) | ![05 機密200 REDACTED](https://devin-gtm.devinenterprise.com/attachments/57237b15-7c83-45c8-82b7-8bd8afbddf5b/05.png) |

### 主要フロー

| 08：作成・noteId採番 | 11：通信イベント・タスク件数 |
|---|---|
| ![08 作成](https://devin-gtm.devinenterprise.com/attachments/85135a6f-7b83-4091-a21c-7cafe367bfa5/08.png) | ![11 status](https://devin-gtm.devinenterprise.com/attachments/6a298798-1775-4c9d-b079-aeed6a791423/11.png) |

| 13：multi失敗400 | 16：削除後一覧 |
|---|---|
| ![13 multi400](https://devin-gtm.devinenterprise.com/attachments/5f2a35e5-e257-4946-a290-6bb7052241c7/13.png) | ![16 削除後](https://devin-gtm.devinenterprise.com/attachments/e89ad701-e4e0-4f11-8b98-a024fba840d4/16.png) |

## 録画・証跡ファイル

- 録画1本: `/home/ubuntu/screencasts/commonext-swagger-e2e/commonext-swagger-e2e-edited.mp4`（1440×1000、約64秒の処理済み動画）
- 全16件のUIスクリーンショット: `/tmp/commonext-e2e-artifacts/01.png` ～ `16.png`
- 実応答status/bodyの証跡: `/tmp/commonext-e2e-artifacts/01.json` ～ `16.json`

## 所見・制限

- 定義済み16件はいずれも期待値と一致した。セキュリティの拒否系と許可系の差、ノートの作成→一覧先頭→最新取得→削除→不在、およびmulti失敗後の全件ロールバックを実応答で確認した。
- 01ではHTTP Basicのブラウザ標準認証要求によりUI応答が保留されたため、OS側Escapeで取消し、Swaggerに表示された401を確認した。02/03/04/07では操作スクリプトのセレクタ／描画待ちを調整して続行した。07は初回Swagger既定クエリ付きで404を確認後、クエリを空にして定義どおりのURLで再実行し404を確認した。これらはアプリの変更ではない。
- 録画外の初期セットアップでブラウザ初期化に失敗したため、存在しなかったX画面を `Xvfb :0 -screen 0 1440x1000x24 -ac` で準備した。ウィンドウマネージャーがなく `wmctrl` の最大化は使えず、CDPで画面内最大サイズに設定した。
- 本結果はlocal/H2/local adapterでのE2Eに限定する。PostgreSQL、本番HTTPS・プロキシ、旧OFBizとの実接続、他コンポーネント機能、ケース外のdeleteAllSystemNotesや追加入力パターンは未検証。全実装・全環境の保証ではない。
- テストで作成したnoteId=10000は15で削除済み。アプリコード修正・コミットは行っていない。
