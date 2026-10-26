# accounting Spring Boot 移行: Swagger UI E2E 再実施結果

## 実施情報

- 実施日: 2026-10-26。APIケース実行は14:28:41〜14:33:03 UTC（23:28:41〜23:33:03 JST）、録画終了は14:35 UTC。
- 対象 PR: https://github.com/madmerger/ofbiz-framework/pull/11
- 対象コミット: `daff83e60120b223bd3b8158a6e4572b19128d0d`
- ブランチ: `devin/1793009070-accounting-springboot`
- ケース原本: [`cases.json`](cases.json)（ACCOUNTING-E2E-01〜23）。
- 今回は前回の21ケースの証跡を再利用せず、新コード・新規H2で01〜23を最初から順に実行し直した。
- 実行環境: Ubuntu、Java 17.0.19、Spring Boot 3.5.7、`local`、H2 2.3.232（`jdbc:h2:mem:accounting-local`）、Chrome 137、Swagger UI `/swagger-ui/index.html`。
- 起動前に8080とJavaプロセスを確認し、旧accountingアプリが停止済みであることを確認。起動シェルで `MIGRATION_DEMO_PASSWORD` をランダム生成し、環境変数のみで `migration/accounting-springboot/run-local.sh` へ渡した。旧 OFBiz は起動していない。
- 本実施中のAPI操作はすべて表示中のSwagger UIのAuthorize、Try it out、入力欄、ExecuteをPlaywrightで操作した。curlや直接API呼出による事前実行・代替検証は行っていない。
- ケース完了までサーバ再起動なし。10は起動直後の未請求 `MIG_DEMO_ORDER` から請求書を作成した。
- 実応答から引き継いだ値: `invoiceId=10000`、`paymentId=10000`、`paymentGroupId=10000`、`acctgTransId=10002`。

## 集計

|項目|結果|
|---|---|
|定義ケース数|23|
|期待値一致|23|
|不一致|0|
|未実施ケース|0|
|setup|2リクエスト（19の支払グループ作成、23の仕訳再取得。いずれも200）|
|followUp|5リクエスト（08、09、16、19、21。すべて期待値一致）|
|Swagger Executeによるリクエスト数|30（本操作23 + setup2 + followUp5）|

金額は数値で照合した。JSONの `30`、`0` は期待値の `30.00`、`0.00` と一致する。

## cases.json IDごとの期待値・実測値

01〜21は新コードでの再実施、22〜23は今回追加されたGL自動仕訳の確認。

|ID|名称|期待値|実測値（ステータス・主要値）|判定|
|---|---|---|---|---|
|ACCOUNTING-E2E-01|未認証は 401|401|401、`error=Unauthorized`|一致|
|ACCOUNTING-E2E-02|base-permission 不足（OFBTOOLS のみ）は 403|403|acct-nobase、403、`error=Forbidden`|一致|
|ACCOUNTING-E2E-03|閲覧専用ユーザーの請求書作成は 403|403|acct-viewer、403、`errorMessage=Service permission denied`|一致|
|ACCOUNTING-E2E-04|機密エンティティ CreditCard: 閲覧専用ユーザーは 403|403|acct-viewer、403、`errorMessage=Sensitive entity access denied`|一致|
|ACCOUNTING-E2E-05|機密エンティティ CreditCard: 専用権限ありは 200（カード番号はマスク）|200、paymentMethodId=MIG_DEMO_PM、cardNumber=[REDACTED]|acct-sensitive、200、`paymentMethodId=MIG_DEMO_PM`、`cardNumber=[REDACTED]`|一致|
|ACCOUNTING-E2E-06|entity-auto 作成 createPaymentGroupType|200、paymentGroupTypeId=E2E_PG_TYPE|200、`paymentGroupTypeId=E2E_PG_TYPE`、`description=E2E 作成`|一致|
|ACCOUNTING-E2E-07|entity-auto 取得 PaymentGroupType|200、description=E2E 作成|200、`description=E2E 作成`|一致|
|ACCOUNTING-E2E-08|entity-auto 更新 updatePaymentGroupType|200、followUp GET 200、description=E2E 更新|本操作200、追跡GET200、`description=E2E 更新`|一致|
|ACCOUNTING-E2E-09|entity-auto 削除 deletePaymentGroupType|200、followUp GET 404|本操作200、追跡GET404、`errorMessage=Entity not found`|一致|
|ACCOUNTING-E2E-10|主要フロー1 受注から請求書作成 createInvoiceForOrder|200、invoiceId採番|200、`invoiceId=10000`、`responseMessage=success`|一致|
|ACCOUNTING-E2E-11|主要フロー2 請求書合計 getInvoiceTotal|200、invoiceTotal=30.00|200、`invoiceTotal=30`|一致|
|ACCOUNTING-E2E-12|主要フロー3 請求書承認 setInvoiceStatus → INVOICE_APPROVED|200、oldStatusId=INVOICE_IN_PROCESS|200、`oldStatusId=INVOICE_IN_PROCESS`、`invoiceTypeId=SALES_INVOICE`|一致|
|ACCOUNTING-E2E-13|主要フロー4 入金作成 createPayment（30.00, PMNT_RECEIVED）|200、paymentId採番|200、`paymentId=10000`、`responseMessage=success`|一致|
|ACCOUNTING-E2E-14|主要フロー5 消込 createPaymentApplication|200、amountApplied=30.00|200、`amountApplied=30`、`paymentApplicationId=10000`|一致|
|ACCOUNTING-E2E-15|主要フロー6 未消込額 getInvoiceNotApplied = 0|200、notApplied=0.00|200、`notApplied=0`|一致|
|ACCOUNTING-E2E-16|主要フロー7 READY 遷移で INVOICE_PAID へ自動遷移|200、followUp GET 200、statusId=INVOICE_PAID|本操作200、`oldStatusId=INVOICE_APPROVED`。追跡GET200、`invoiceId=10000`、`statusId=INVOICE_PAID`、paidDateあり|一致|
|ACCOUNTING-E2E-17|simple 代表1 acctgInvoicePermissionCheck（clerk は UPDATE 可）|200、hasPermission=true|acct-clerk、200、`hasPermission=true`|一致|
|ACCOUNTING-E2E-18|simple 代表1 acctgInvoicePermissionCheck（viewer は UPDATE 不可）|200、hasPermission=false|acct-viewer、200、`hasPermission=false`、`failMessage=Permission denied`|一致|
|ACCOUNTING-E2E-19|simple 代表2 createPaymentGroupMember（入金を支払グループに追加）|setup採番、本操作200、followUp GET 200、対象paymentId行1件|setup200、`paymentGroupId=10000`。本操作200。追跡GET200、配列1件、`paymentGroupId=10000`、`paymentId=10000`|一致|
|ACCOUNTING-E2E-20|multi: 重複を含む一括作成は 409|409、responseMessage=error、errorsにServiceValueFound|409、`responseMessage=error`、`errors=[{"index":2,"errorMessage":"ServiceValueFound"}]`|一致|
|ACCOUNTING-E2E-21|multi: 全件ロールバック（先行 2 件も残らない）|AのGET404、followUpでBも404|A／Bとも404、`{"responseMessage":"error","errorMessage":"Entity not found"}`|一致|
|ACCOUNTING-E2E-22|請求書承認で売上仕訳が自動作成|200、対象invoiceIdのSALES_INVOICE行が1件|200、配列1件、`invoiceId=10000`、`acctgTransTypeId=SALES_INVOICE`、`acctgTransId=10002`|一致|
|ACCOUNTING-E2E-23|売上仕訳の借方・貸方が一致|200、Dのamount合計=Cの合計=30|setup再取得200、22と同じ`acctgTransId=10002`。明細GET200、3件すべて同ID。貸方10+20=30、借方30、差額0|一致|

## GL自動仕訳の実測詳細

22の取得結果から `acctgTransId=10002` を保存。23のcases.jsonにあるsetupで同じ仕訳を再取得し、同IDであることを確認してから、そのIDを指定して `AcctgTransEntry` を取得した。

|明細番号|debitCreditFlag|amount|glAccountId|organizationPartyId|
|---|---|---|---|---|
|00001|C|10|400000|MIG_DEMO_VENDOR|
|00002|C|20|400000|MIG_DEMO_VENDOR|
|00003|D|30|120000|MIG_DEMO_VENDOR|

- 借方合計: 30、貸方合計: 30、差額: 0（USD）。
- 仕訳の `createdStamp=2026-10-26T14:30:52.073+00:00` は16のREADY遷移直後。今回は「INVOICE_APPROVEDへの遷移時点」単独ではなく、cases.jsonの手順どおりREADY遷移まで完了した後の保存結果を確認した。
- 仕訳の `isPosted=N`。今回確認したのは仕訳と明細の自動作成・保存・借貸一致であり、元帳へのpost済み状態ではない。

## 録画・実行上の注意と未検証範囲

- 録画は今回1回だけ開始・停止した。ビルドと起動完了後に開始し、01〜23の順序・ユーザー切替・全setup／followUpを収録した。
- 画面上部にケースID・日本語名称・ユーザー・操作段階を固定表示。23では実応答に基づく借貸合計と明細件数も表示した。
- パスワードは起動シェルでランダム生成し、ファイル・レポートに保存せず、入力欄はpassword型の伏せ字。Basic認証値を含み得るSwagger生成curlはテスト用CSSで非表示にした。APIの実リクエスト・レスポンス本文は変更していない。
- 23の最初のスクロールは外側要素を対象としており表示が動かなかったため、実際のスクロール領域（内側のPRE）へ操作先を調整した。その後、貸方10・貸方20・借方30を順に可視確認し、スクリーンショットを保存した。APIの再実行・サーバ再起動・実装修正は行っていない。
- 保存した全ケース実応答の照合とスクリーンショットの目視確認を実施。録画レビューでも主要応答・GL3明細が読め、平文パスワード・認証文字列・ビルドログが映らないことを確認した。
- 対象はcases.jsonの23ケース。GL20サービスの全分岐、postgres/prod、HTTPS終端プロキシ、全移行サービス・エンティティ網羅は未実施であり、検証済みとはしない。
- 不一致・未完了ケース: なし。実装修正・commit・push・PRコメント投稿: なし。作業ツリーの本レポートのみ更新した。

## 成果物

- MP4（1600×1000、約78.33秒、4,627,542 bytes、1本）: `/home/ubuntu/screencasts/rec-521ee67e-934f-40a2-af08-af305a3ab9a8/rec-521ee67e-934f-40a2-af08-af305a3ab9a8-edited.mp4`
- Animated WebP（1280×800、3fps、235フレーム、6,362,368 bytes）: `/tmp/accounting-e2e-daff83/accounting-swagger-e2e.webp`
- [PR埋め込み用WebP共有リンク](https://devin-gtm.devinenterprise.com/attachments/875516e3-5364-4cfb-9118-77846e2c1674/accounting-swagger-e2e.webp)
- 各ケースの実応答JSON: `/tmp/accounting-e2e-daff83/ACCOUNTING-E2E-01.json`〜`ACCOUNTING-E2E-23.json`
- 各操作の原寸スクリーンショット: `/tmp/accounting-e2e-daff83/ACCOUNTING-E2E-*.png`

## 主要スクリーンショット

以下はすべて今回のHEADで取得した証跡。

### 追加GLケース

|22: 売上仕訳1件・ID=10002|23: 貸方10・明細3件・借貸合計各30|
|---|---|
|![22 売上仕訳](https://devin-gtm.devinenterprise.com/attachments/98a86fed-3343-47a6-93ef-1205c25cf81e/ACCOUNTING-E2E-22-%E6%9C%AC%E6%93%8D%E4%BD%9C.png)|![23 貸方10](https://devin-gtm.devinenterprise.com/attachments/9a65e0d7-b4ef-4ce7-bdd2-0a98fddc6ad6/ACCOUNTING-E2E-23-%E6%9C%AC%E6%93%8D%E4%BD%9C.png)|

|23: 貸方20|23: 借方30|
|---|---|
|![23 貸方20](https://devin-gtm.devinenterprise.com/attachments/0367faec-4b5d-442d-9734-37d1d63e70c1/ACCOUNTING-E2E-23-%E8%B2%B8%E6%96%B920.png)|![23 借方30](https://devin-gtm.devinenterprise.com/attachments/738b21e7-77c6-4e8c-b3b0-cfc1495c65a8/ACCOUNTING-E2E-23-%E5%80%9F%E6%96%B930.png)|

### 再実施: 認証・機密保護

|01: 未認証401|04: 機密403|
|---|---|
|![01 未認証401](https://devin-gtm.devinenterprise.com/attachments/d04efa24-ee39-4e93-bff4-c0ee7824ae22/ACCOUNTING-E2E-01-%E6%9C%AC%E6%93%8D%E4%BD%9C.png)|![04 機密403](https://devin-gtm.devinenterprise.com/attachments/364277d3-cc50-4658-842a-439cbc17448a/ACCOUNTING-E2E-04-%E6%9C%AC%E6%93%8D%E4%BD%9C.png)|

|05: 専用権限200・マスク|15: 未消込額0|
|---|---|
|![05 機密200とマスク](https://devin-gtm.devinenterprise.com/attachments/16d22d62-94af-4fa3-b1bf-09cd59f366a8/ACCOUNTING-E2E-05-%E6%9C%AC%E6%93%8D%E4%BD%9C.png)|![15 未消込額0](https://devin-gtm.devinenterprise.com/attachments/790e00a4-1266-4f33-aa1e-03b2bdcc592a/ACCOUNTING-E2E-15-%E6%9C%AC%E6%93%8D%E4%BD%9C.png)|

### 再実施: 支払済み・multi

|16: INVOICE_PAID|20: multi409・ServiceValueFound|
|---|---|
|![16 INVOICE_PAID](https://devin-gtm.devinenterprise.com/attachments/940a4d96-df7e-4f07-813f-0efbb2c354b5/ACCOUNTING-E2E-16-%E8%BF%BD%E8%B7%A1%E7%A2%BA%E8%AA%8D.png)|![20 multi409](https://devin-gtm.devinenterprise.com/attachments/f632375e-7a5d-44b5-8e0c-99773fbf0dfc/ACCOUNTING-E2E-20-%E6%9C%AC%E6%93%8D%E4%BD%9C.png)|

|21: Aは404|21追跡: Bも404|
|---|---|
|![21 A404](https://devin-gtm.devinenterprise.com/attachments/61add3f6-10ad-489b-895a-3f5beecd42cc/ACCOUNTING-E2E-21-%E6%9C%AC%E6%93%8D%E4%BD%9C.png)|![21 B404](https://devin-gtm.devinenterprise.com/attachments/df95af99-8ed9-480f-b607-8d7d470dd71f/ACCOUNTING-E2E-21-%E8%BF%BD%E8%B7%A1%E7%A2%BA%E8%AA%8D.png)|
