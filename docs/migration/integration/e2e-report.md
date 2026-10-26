# Order・Product・Accounting 横断E2E検証結果

## 概要

共有PostgreSQLに接続する3つのSpring Bootアプリを再利用し、実ブラウザーのSwagger UIから `cases.json` の15ケースを記載順に実行した。必須15ケースの期待値はすべて一致した。追加診断として実行した試算表計算APIは `acct-clerk` で403となり、当該計算サービスの正常応答は未確認。貸借一致は、正常に取得できた実際のGL明細2行の金額・借貸区分から確認した。

Orderの出荷・請求スタブは呼び出していない。Order:8081が返した新しい受注ID `10003` をProduct:8082の引当・出荷とAccounting:8083の請求書作成に引き継いだ。最終状態もOrder:8081から再取得した。

## 環境・リビジョン

| 項目 | 実測・使用値 |
|---|---|
| 実行日 | 2026-10-26 UTC |
| 作業ツリー | `/home/ubuntu/repos/ofbiz-framework-integration-1793026391` |
| ブランチ | `devin/1793026391-springboot-integration` |
| 指定・開始時revision | `ee5feec68b` |
| 終了時観測HEAD | `9203e552db583f2d31a9bbd2b31f9799b319dddb` |
| revision差分 | `ee5feec68b..9203e552db` は `docs/migration/PLAYBOOK.md` のみ。実行コード差分なし |
| Java | Java 17 |
| PostgreSQL | 起動済みDockerコンテナ `ofbiz-integration-postgres`、5432 |
| アプリ | Order:8081 / Product:8082 / Accounting:8083、別々のJavaプロセス |
| プロファイル | 3アプリともコマンド引数 `--spring.profiles.active=integration` |
| PID | Order:319220 / Product:319615 / Accounting:323944 |
| health | 実行前・終了後とも3アプリすべてHTTP 200、`{"status":"UP"}` |
| UI | 各ポートの `/swagger-ui/index.html` |
| 認証 | 未認証、Order:`readonly` / `orderclerk`、Product:`admin`、Accounting:`acct-clerk` |
| パスワード | 起動環境の `MIGRATION_DEMO_PASSWORD` / `PRODUCT_DEMO_PASSWORD` を使用。値は記載しない |
| 起動・終了 | 稼働中の環境を再利用し、再起動・追加アプリ起動・停止は行っていない。旧OFBizは起動していない |
| テスト方式 | 実ChromeをPlaywright/CDPで操作。SwaggerのAuthorize・Try it out・入力欄・Executeを使用 |

業務APIをcurlやPlaywrightのHTTPクライアントで直接呼び出していない。shellからのHTTP確認はhealthのみ。ローカルダミーのBasic認証文字列がSwagger生成Curl欄に一時的に映る場面があるため、録画を外部共有する場合は留意する。下記主要スクリーンショットには認証ヘッダーを含めていない。

## 今回採番されたID

| 対象 | 応答から取得した値 |
|---|---|
| orderId | `10003` |
| inventoryItemId / receiptId | `10002` / `10002` |
| shipmentId | `10002` |
| invoiceId | `10002` |
| acctgTransId | `10003` |
| paymentId / paymentApplicationId | `10001` / `10001` |

同じ数値でもエンティティごとに別のIDである。既存dry-run受注 `10000`〜`10002` は使用せず、今回のstoreOrder応答を起点に後続入力へ引き継いだ。

## ケース別結果

表中、`O` は `http://localhost:8081/api/order`、`P` は `http://localhost:8082/api/product`、`A` は `http://localhost:8083/api/accounting`。POST本文は実際にSwagger UIへ入力したJSON。`pass` は当該ケースの指定期待値との一致を表す。

| ケースID | 名称 | リクエスト（認証・本文） | 期待値 | 実測値 | 合否 |
|---|---|---|---|---|---|
| INTEGRATION-01 | 未認証のOrder API | 未認証、GET `O/entities/OrderHeader` | HTTP 401 | HTTP 401、`error="Unauthorized"` | pass |
| INTEGRATION-02 | 閲覧専用ユーザーの作成拒否 | readonly、POST `O/services/storeOrder`、`{"partyId":"DemoCustomer","orderTypeId":"SALES_ORDER","productStoreId":"DEMO_ORDER_STORE","orderItems":[{"productId":"DEMO_ORDER_PRODUCT_1","quantity":2}]}` | HTTP 403 | HTTP 403、`errorMessage="Service permission denied"` | pass |
| INTEGRATION-03 | 受注作成 | orderclerk、POST `O/services/storeOrder`、INTEGRATION-02と同じ本文 | HTTP 200、非空orderId、grandTotal=50、ORDER_CREATED | HTTP 200、orderId=`10003`、grandTotal=50.0、ORDER_CREATED | pass |
| INTEGRATION-04 | 受注承認 | orderclerk、POST `O/services/changeOrderStatus`、`{"orderId":"10003","statusId":"ORDER_APPROVED","setItemStatus":"Y"}` | HTTP 200、old=ORDER_CREATED、new=ORDER_APPROVED | HTTP 200、oldStatusId=ORDER_CREATED、statusId=ORDER_APPROVED、grandTotal=50.0 | pass |
| INTEGRATION-05 | 在庫入荷・fromDate省略 | admin、POST `P/services/receiveInventoryProduct`、`{"productId":"DEMO_ORDER_PRODUCT_1","facilityId":"E2E_WH","inventoryItemTypeId":"NON_SERIAL_INV_ITEM","quantityAccepted":10,"quantityRejected":0,"unitCost":1.25}` | HTTP 200、非空inventoryItemId・receiptId | HTTP 200、inventoryItemId=`10002`、receiptId=`10002`。入荷会計・callbackのno-op通知あり | pass |
| INTEGRATION-06 | 受注分引当 | admin、POST `P/services/reserveProductInventoryByFacility`、`{"productId":"DEMO_ORDER_PRODUCT_1","facilityId":"E2E_WH","orderId":"10003","orderItemSeqId":"00001","shipGroupSeqId":"00001","quantity":2,"reserveOrderEnumId":"INVRO_FIFO_REC","requireInventory":"N"}` | HTTP 200、quantityNotReserved=0 | HTTP 200、quantityNotReserved=0.0 | pass |
| INTEGRATION-07 | 受注出荷 | admin、POST `P/services/quickShipEntireOrder`、`{"orderId":"10003"}` | HTTP 200、shipmentIdsが1件 | HTTP 200、shipmentIds=`["10002"]`。既知のWorkEffort未移行501通知2件 | pass |
| INTEGRATION-08 | Shipmentの状態 | admin、GET `P/entities/Shipment/one?shipmentId=10002` | HTTP 200、primaryOrderId=10003、SHIPMENT_SHIPPED | HTTP 200、primaryOrderId=`10003`、statusId=SHIPMENT_SHIPPED | pass |
| INTEGRATION-09 | 受注から請求書作成 | acct-clerk、POST `A/services/createInvoiceForOrder`、`{"orderId":"10003"}` | HTTP 200、非空invoiceId | HTTP 200、invoiceId=`10002` | pass |
| INTEGRATION-10 | READY・売上GL生成 | acct-clerk、POST `A/services/setInvoiceStatus`、`{"invoiceId":"10002","statusId":"INVOICE_READY"}`。続けてGET `A/entities/AcctgTrans?invoiceId=10002` | HTTP 200、old=INVOICE_IN_PROCESS、invoiceTypeId=SALES_INVOICE。同invoiceのSALES_INVOICE GLが1件 | HTTP 200、oldStatusId=INVOICE_IN_PROCESS、invoiceTypeId=SALES_INVOICE。GL一覧1件：acctgTransId=`10003`、invoiceId=`10002`、acctgTransTypeId=SALES_INVOICE、isPosted=N | pass |
| INTEGRATION-11 | 入金作成 | acct-clerk、POST `A/services/createPayment`、`{"paymentTypeId":"CUSTOMER_PAYMENT","paymentMethodTypeId":"CREDIT_CARD","paymentMethodId":"MIG_DEMO_PM","partyIdFrom":"DemoCustomer","partyIdTo":"Company","currencyUomId":"USD","amount":"50.00","statusId":"PMNT_RECEIVED"}` | HTTP 200、非空paymentId | HTTP 200、paymentId=`10001` | pass |
| INTEGRATION-12 | 入金適用 | acct-clerk、POST `A/services/createPaymentApplication`、`{"invoiceId":"10002","paymentId":"10001"}` | HTTP 200、amountApplied=50、非空paymentApplicationId、CUSTOMER_PAYMENT | HTTP 200、amountApplied=50.0、paymentApplicationId=`10001`、paymentTypeId=CUSTOMER_PAYMENT | pass |
| INTEGRATION-13 | PAID・残高0 | acct-clerk、GET `A/entities/Invoice/one?invoiceId=10002`。続けてPOST `A/services/getInvoiceNotApplied`、`{"invoiceId":"10002"}` | HTTP 200、INVOICE_PAID、notApplied=0.00 | 両方HTTP 200、invoiceId=`10002`、statusId=INVOICE_PAID、notApplied=0.0 | pass |
| INTEGRATION-14 | 最終Orderヘッダー | orderclerk、GET `O/entities/OrderHeader/one?orderId=10003` | HTTP 200、ORDER_APPROVED、grandTotal=50 | HTTP 200、orderId=`10003`、statusId=ORDER_APPROVED、grandTotal=50.0 | pass |
| INTEGRATION-15 | 最終Order明細 | orderclerk、GET `O/entities/OrderItem?orderId=10003` | HTTP 200、orderItemSeqId=00001、ITEM_COMPLETED | HTTP 200、1件、orderId=`10003`、orderItemSeqId=`00001`、statusId=ITEM_COMPLETED、quantity=2.0、unitPrice=25.0 | pass |

## GL貸借の追加検証

INTEGRATION-10の直後に、同じ `acct-clerk` で以下をSwagger UIから実行した。

| 追加確認 | リクエスト | 期待値 | 実測値 | 判定 |
|---|---|---|---|---|
| GL明細 | GET `A/entities/AcctgTransEntry?acctgTransId=10003` | 同仕訳の借方合計=貸方合計 | HTTP 200、2行。C=50.0 USD（00001、GL 400000）、D=50.0 USD（00002、GL 120000）。50.0−50.0=0 | pass |
| 試算表計算サービス（任意診断） | POST `A/services/calculateAcctgTransTrialBalance`、`{"acctgTransId":"10003"}` | 200、debitTotal=50、creditTotal=50、差額0 | HTTP 403、`{"responseMessage":"error","errorMessage":"Service permission denied"}` | fail（このユーザーでは正常計算未確認） |

試算表計算の403を成功と扱っていない。代わりに、取得できた明細そのものの金額とD/C区分で必須の貸借一致を確認した。権限変更・別ユーザーへの昇格は行っていない。

売上仕訳の `isPosted` は実際には `N`、`postedDate` は `null`。本検証が確認したのは、指定されたSALES_INVOICE取引・明細の生成と貸借一致であり、GLへの最終転記済み状態ではない。

## 正確な通知・制限

必須15ケースの指定期待値との不一致はなし。ただし、次の既知の未移行処理・追加診断エラーを観測した。

### 入荷応答（INTEGRATION-05、HTTP 200）

```json
{
  "inventoryItemId": "10002",
  "receiptId": "10002",
  "successMessageList": ["Received 10 of DEMO_ORDER_PRODUCT_1 in inventory item 10002."],
  "sideEffectNotes": "Receipt accounting and receipt SECA/ECA callbacks are no-ops.",
  "responseMessage": "success"
}
```

入荷の会計連携およびSECA/ECA callbackはno-opであるため、その副作用を実装済みとは判断しない。

### 出荷応答（INTEGRATION-07、HTTP 200）

```json
{
  "shipmentShipGroupFacilityList": [
    {"shipmentId": "10002", "facilityId": "E2E_WH", "shipGroupSeqId": "00001"}
  ],
  "successMessageList": ["FacilityShipmentIdCreated 10002 00001 E2E_WH"],
  "shipmentIds": ["10002"],
  "sideEffectNotes": [
    "501: checkAndUpdateWorkEffort (work-effort integration is not ported)",
    "501: checkAndUpdateWorkEffort (work-effort integration is not ported)"
  ],
  "responseMessage": "success"
}
```

上記のキー名を含め、実レスポンスは `shipmentShipGroupFacilityList`。通知内の501はHTTP応答コードではない。WorkEffort連携は未検証・未移行だが、同shipmentを再取得して `SHIPMENT_SHIPPED` を確認した。

### 追加の試算表計算応答（HTTP 403）

```json
{
  "responseMessage": "error",
  "errorMessage": "Service permission denied"
}
```

### 検証補助環境での調整

- 最初の参照先checkoutにはintegration資料がなく、指定ブランチが存在する上記worktreeに参照先を合わせた。別checkoutのブランチは変更していない。
- ChromeのCDPはIPv6ループバック `[::1]:29229` で待受しており、IPv4接続失敗後に接続先を修正した。
- ウィンドウマネージャーがないため、CDPでブラウザーを画面全体へ配置した。
- 初回401の応答読み取りで補助スクリプトがテーブル見出しを読んで停止した。Swagger画面には401が表示されており、tbody指定へ修正して再実行・記録した。状態変更リクエストは重複実行していない。
- 長いJSONはレスポンス枠内をスクロールし、ID・状態・借貸区分を目視確認した。

## 録画・主要画面証跡

[全ケースのSwagger UI録画（日本語注釈付き・約48秒）](https://devin-gtm.devinenterprise.com/attachments/a5012912-9029-4333-8b78-3fb153d1b3ad/integration-cross-component-e2e-edited.mp4)

### 別プロセスで確認した出荷と売上GL

| Product:8082 出荷完了・受注ID一致 | Accounting:8083 売上GL・請求書ID一致 |
|---|---|
| ![Productの出荷10002は受注10003に紐づきSHIPMENT_SHIPPED](https://devin-gtm.devinenterprise.com/attachments/05f7c759-63c9-4757-9378-912f7b35c106/shipment-shipped.png) | ![Accountingの請求10002にSALES_INVOICE仕訳10003が存在](https://devin-gtm.devinenterprise.com/attachments/a5ccf6a3-5016-4102-8f3b-9b87702b4c3c/GL-TRANS.png) |

### 同一GL仕訳の貸借一致

| 貸方明細：C=50 USD | 借方明細：D=50 USD |
|---|---|
| ![仕訳10003の貸方50](https://devin-gtm.devinenterprise.com/attachments/2048c877-a26f-42c5-81d0-dbc9b1cf2eae/GL-ENTRIES.png) | ![仕訳10003の借方50](https://devin-gtm.devinenterprise.com/attachments/d3a69462-ba12-411e-bdcc-b41fa3725ce8/gl-debit.png) |

### 請求PAIDと最終Orderヘッダー

| Accounting:8083 請求書PAID | Order:8081 最終ヘッダー |
|---|---|
| ![請求10002はINVOICE_PAID](https://devin-gtm.devinenterprise.com/attachments/25e91961-4b1b-42d6-97f0-dc9ae05ad3d6/invoice-paid.png) | ![受注10003はORDER_APPROVED、合計50](https://devin-gtm.devinenterprise.com/attachments/ed9990e5-8ddf-4a8c-a9e8-332b03674253/order-final-header.png) |

### Orderのヘッダーと明細の実状態

| ヘッダー：ORDER_APPROVED | 明細00001：ITEM_COMPLETED |
|---|---|
| ![受注ヘッダーの実状態](https://devin-gtm.devinenterprise.com/attachments/ed9990e5-8ddf-4a8c-a9e8-332b03674253/order-final-header.png) | ![受注明細の実状態](https://devin-gtm.devinenterprise.com/attachments/188ba06a-894e-435e-ac87-2f39b04562c6/order-final-item.png) |

## 保存場所・対象外

- 録画：`/home/ubuntu/screencasts/integration-cross-component-e2e/integration-cross-component-e2e-edited.mp4`
- 主要スクリーンショット：`/tmp/integration-e2e/` の `shipment-shipped.png`、`GL-TRANS.png`、`GL-ENTRIES.png`、`gl-debit.png`、`invoice-paid.png`、`order-final-header.png`、`order-final-item.png`
- ケースごとの実リクエスト・完全応答JSON：`/tmp/integration-e2e/INTEGRATION-01.json`〜`INTEGRATION-15.json`。追加確認は `GL-TRANS.json`、`GL-ENTRIES.json`、`GL-BALANCE.json`、`INVOICE-NOT-APPLIED.json`
- このレポート：`docs/migration/integration/e2e-report.md`。コミットしていない。
- 本検証は指定の単一受注フローを対象とする。全移行サービスの網羅、旧OFBizとの実行比較、WorkEffort、入荷会計callback、最終GL転記、並行処理・障害復旧・本番環境は対象外。
- 新規テストデータは共有DBに残している。既存アプリ・PostgreSQLは稼働したまま。追加のユーザー操作は必須ではないが、試算表APIも必要なら別途権限確認が必要。
