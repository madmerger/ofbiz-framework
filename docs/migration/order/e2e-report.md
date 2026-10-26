# order E2E テストレポート

## 実行条件

| 項目 | 内容 |
| --- | --- |
| 対象コミット | `0eeb968cd9`（PR #5） |
| 起動 | `migration/order-springboot` だけを local プロファイルで起動した。旧 OFBiz は起動していない |
| 操作 | ブラウザの Swagger UI（`/swagger-ui/index.html`）から操作した。Devin の E2E テスト機能（testing agent）で実行した |
| ケース定義 | [`cases.json`](cases.json) の 20 ケースを、定義順に実行した |
| 認証 | HTTP Basic。パスワードは環境変数 `MIGRATION_DEMO_PASSWORD` から渡し、画面や録画には表示していない |
| 録画 | 1 本、1600×1000、約 123 秒。各ケースの番号と名称を、画面上部のバナーに表示した |
| サーバーログ | `ERROR`・`WARN`・例外の記録はなかった |

## 結果

20 ケースすべてが合格した（20/20）。期待値と実測値の不一致はなかった。

| ID | ケース | 期待値 | 実測値 | 判定 |
| --- | --- | --- | --- | --- |
| ORDER-E2E-01 | 未認証 401 | 401 | 401。リクエストに Authorization ヘッダーなし | 合格 |
| ORDER-E2E-02a | 権限不足 403（ORDERMGR なし） | 403 | 403 | 合格 |
| ORDER-E2E-02b | 権限不足 403（readonly で create） | 403。その後 admin で取得すると 404 | 403 `Service permission denied`。取得は 404 | 合格 |
| ORDER-E2E-03a | 機密エンティティ: 閲覧専用ユーザー | 403 | 403 `Sensitive entity access denied` | 合格 |
| ORDER-E2E-03b | 機密エンティティ: 専用権限あり | 200。`ORDER-DEMO-PAYMENT` を含む | 200。`orderPaymentPreferenceId=ORDER-DEMO-PAYMENT` | 合格 |
| ORDER-E2E-04a | entity-auto 作成 createOrderType | 200 | 200 | 合格 |
| ORDER-E2E-04b | entity-auto 取得 | 200。`description=E2E 作成` | 200。`orderTypeId=E2E_TYPE`、`description=E2E 作成` | 合格 |
| ORDER-E2E-04c | entity-auto 更新 updateOrderType | 200。再取得で `E2E 更新` | 200。再取得で `description=E2E 更新` | 合格 |
| ORDER-E2E-04d | entity-auto 削除 deleteOrderType | 200。再取得で 404 | 200。再取得で 404 | 合格 |
| ORDER-E2E-05a | simple: createCustRequest | 200。採番され、状態は `CRQ_DRAFT` | 200。`custRequestId=10000`、状態は `CRQ_DRAFT` | 合格 |
| ORDER-E2E-05b | simple: setCustRequestStatus | 200。状態は `CRQ_SUBMITTED` | 200。再取得で `CRQ_SUBMITTED` | 合格 |
| ORDER-E2E-06a | 主要フロー: 受注 storeOrder | 200。`grandTotal=90.00`、`ORDER_CREATED` | 200。`orderId=10000`、`grandTotal=90`、`ORDER_CREATED` | 合格 |
| ORDER-E2E-06b | 主要フロー: 承認 changeOrderStatus | 200。`ORDER_CREATED` から `ORDER_APPROVED` へ | 200。`oldStatusId=ORDER_CREATED`、`statusId=ORDER_APPROVED` | 合格 |
| ORDER-E2E-06c | 主要フロー: 出荷要求 quickShipEntireOrder | 200。`ORDER_COMPLETED`、出荷 1 件 | 200。`ORDER_COMPLETED`、`shipmentIds=["10000"]` | 合格 |
| ORDER-E2E-06d | 主要フロー: 請求要求 createInvoiceForOrder | 200。invoiceId が採番される | 200。`invoiceId=10000` | 合格 |
| ORDER-E2E-06e | 主要フロー: 状態確認 | ヘッダは `ORDER_COMPLETED`・90。明細 2 件とも完了。状態履歴に 3 つの状態。請求行 2 件 | すべて 200。`ORDER_COMPLETED`・90。明細 2 件とも `ITEM_COMPLETED`。履歴に `ORDER_CREATED`・`ORDER_APPROVED`・`ORDER_COMPLETED`。請求行 2 件 | 合格 |
| ORDER-E2E-06f | 主要フロー: 二重請求 | 409 | 409 `未請求の注文明細がありません` | 合格 |
| ORDER-E2E-06g | 主要フロー: キャンセル cancelOrderItem | 200。ヘッダは `ORDER_CANCELLED` | 前提の受注 `orderId=10001`（25）を作成。キャンセルは 200（`["00001"]`）。再取得で `ORDER_CANCELLED` | 合格 |
| ORDER-E2E-07a | multi: 重複で 409 | 409。`errors[0].index=1`、`status=409` | 409。`errors[0].index=1`、`status=409` | 合格 |
| ORDER-E2E-07b | multi: 全件ロールバック | 1 件目も 404 | `E2E_MULTI_1` は 404 | 合格 |

## 補足と未検証の範囲

- 実行中に、未認証リクエストで Basic 認証のダイアログが保留される問題と、Swagger の操作待ちのタイムアウトが起きた。操作手順を直して続行した。リクエストの内容は変えていない。
- 録画の自動注釈の一部では、日本語が文字化けしている。画面上部のバナーに表示したケース番号と名称は正しく表示されている。
- 次の項目は、この E2E では確認していない。PostgreSQL プロファイル（JUnit の Testcontainers smoke テストで確認済み）。local 以外のプロファイルで Swagger が無効になること。外部の Shipment/Invoice との実連携（local adapter で代替）。未移植の機能（[`unported.md`](unported.md) を参照）。
