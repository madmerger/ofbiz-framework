# accounting Spring Boot 移行: Swagger UI E2E 実施結果

## 実施情報

- 実施日時: 2026-10-26 12:56:59〜13:05:14 UTC（2026-10-26 21:56:59〜22:05:14 JST）。
- 対象 PR: https://github.com/madmerger/ofbiz-framework/pull/11
- 対象コミット: `026fdf0676304c2561c1c1bcf83149212af1fa92`
- ブランチ: `devin/1793009070-accounting-springboot`
- ケース原本: [`cases.json`](cases.json)（実行前にコミット済みの ACCOUNTING-E2E-01〜21）。
- 実行環境: Ubuntu、Java 17.0.19、Spring Boot 3.5.7、`local` プロファイル、組み込み H2 `accounting-local`、Chrome 137、Swagger UI `/swagger-ui/index.html`。
- 起動: `MIGRATION_DEMO_PASSWORD` を起動プロセスの環境変数にのみ設定し、リポジトリルートで `migration/accounting-springboot/run-local.sh`。旧 OFBiz は起動していない。
- 操作: Playwright で表示中の Chrome に接続し、実際の Swagger UI の Authorize / Try it out / 入力欄 / Execute を操作。API の直接呼び出しや curl によるテストは本実施では使用していない。ステータスと本文は Execute による実応答とスクリーンショットで照合した。
- ストレージ: 起動直後の H2 を使用し、ケース完了までサーバ再起動なし。01〜21を順番に完了。10の注文 `MIG_DEMO_ORDER` は未請求状態で開始した。
- 採番値: `invoiceId=10000`、`paymentId=10000`、`paymentGroupId=10000`。後続ケースでは実応答の値を使用した。

## 集計

|項目|結果|
|---|---|
|定義ケース数|21|
|期待値一致|21|
|不一致|0|
|未実施ケース|0|
|setup|1件（19の支払グループ作成、200）|
|followUp|5件（08、09、16、19、21、すべて期待値一致）|
|最終確認対象の Swagger リクエスト数|27（本操作21 + setup1 + followUp5）|

JSON の金額は実応答で `30`、`0` と表示されるため、期待値 `30.00`、`0.00` と数値として比較した。

## ケース別照合

|ID|名称|期待値|実測値（ステータス・主要値）|判定|
|---|---|---|---|---|
|ACCOUNTING-E2E-01|未認証は 401|401|401、`error=Unauthorized`、対象 `/api/accounting/entities/Invoice`|一致|
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
|ACCOUNTING-E2E-19|simple 代表2 createPaymentGroupMember（入金を支払グループに追加）|setupでpaymentGroupId採番、本操作200、followUp GET 200、対象paymentIdの行1件|setup200、`paymentGroupId=10000`。本操作200、`responseMessage=success`。追跡GET200、配列1件、`paymentGroupId=10000`、`paymentId=10000`|一致|
|ACCOUNTING-E2E-20|multi: 重複を含む一括作成は 409|409、responseMessage=error、errorsにServiceValueFound|409、`responseMessage=error`、`errors=[{"errorMessage":"ServiceValueFound","index":2}]`|一致|
|ACCOUNTING-E2E-21|multi: 全件ロールバック（先行 2 件も残らない）|E2E_ROLLBACK_AのGET404、followUpでBも404|AのGET404、Bの追跡GET404。両方 `{"errorMessage":"Entity not found","responseMessage":"error"}`|一致|

## 実行上の注意と範囲

- 初回のブラウザ起動時に X server がなく、Xvfb `:0`（1600×1000）を起動して解消した。ビルドと起動は録画開始前に完了している。
- 01でブラウザの HTTP Basic 認証チャレンジが待機になったため、その未認証GETを中断し、ブラウザ側のチャレンジを `CancelAuth` として再実行した。サーバの401レスポンスを変更していない。これに伴い Swagger ページを再読込したが、サーバとH2は再起動していない。
- 03開始前に Swagger Logout ボタンのアクセシブル名（`Remove authorization`）に合わせて自動操作を調整した。この調整中に03の本操作は送信されていない。以降のユーザー切替は Authorize ダイアログから行った。
- ケース名・ID・ユーザー・操作段階を画面上部の固定バナーに表示した。パスワードは password 型入力欄で伏せ字、Basic認証文字列を含む Swagger の生成 curl エリアはテスト用CSSで非表示。リクエストとレスポンス本文は改変していない。
- 録画は1回だけ開始・停止した。配布用MP4は録画ツールによる待機時間圧縮後で約73秒。ケース順、認証切替、レスポンスが表示され、ビルドログや秘密値は含まれない。
- 本報告は cases.json の21ケースに限る。postgres/prod、HTTPS終端プロキシ、移行対象の全サービス・全エンティティ網羅は本E2Eの対象外で未実施。これらまで検証済みとはしない。
- 不一致応答・実装修正: なし。

## 成果物

- 録画（1600×1000、約73秒、1本）: PR #11 の E2E 結果コメントに WebP で埋め込み
- WebP共有リンク: https://devin-gtm.devinenterprise.com/attachments/ec242d89-92a9-4aba-8f69-bf7e9d32e08d/accounting-swagger-e2e.webp
- ケース別スクリーンショット（主要分）: PR #11 の E2E 結果コメントを参照

## 主要スクリーンショット

### 未認証と機密アクセス制御

|01: 未認証401|04: 機密閲覧403|
|---|---|
|![ACCOUNTING-E2E-01 未認証401](https://devin-gtm.devinenterprise.com/attachments/789450d5-e714-41ed-b35f-0fdd55e0348f/ACCOUNTING-E2E-01-%E6%9C%AC%E6%93%8D%E4%BD%9C.png)|![ACCOUNTING-E2E-04 機密403](https://devin-gtm.devinenterprise.com/attachments/a8df5da5-d298-4753-a6bb-9aa4aaaf36db/ACCOUNTING-E2E-04-%E6%9C%AC%E6%93%8D%E4%BD%9C.png)|

### 機密マスクと未消込額

|05: 専用権限200・cardNumber=[REDACTED]|15: 未消込額0|
|---|---|
|![ACCOUNTING-E2E-05 マスク200](https://devin-gtm.devinenterprise.com/attachments/1a0f3c98-60c8-4d33-aa59-467022f1b94a/ACCOUNTING-E2E-05-%E6%9C%AC%E6%93%8D%E4%BD%9C.png)|![ACCOUNTING-E2E-15 未消込0](https://devin-gtm.devinenterprise.com/attachments/305664c6-e84e-43ab-84d6-7d40bac1d076/ACCOUNTING-E2E-15-%E6%9C%AC%E6%93%8D%E4%BD%9C.png)|

### 支払済み状態とmulti競合

|16: 再取得でINVOICE_PAID|20: multi 409・ServiceValueFound|
|---|---|
|![ACCOUNTING-E2E-16 INVOICE_PAID](https://devin-gtm.devinenterprise.com/attachments/c8a5bad5-018c-4f5f-abbe-bc27fe6d7998/ACCOUNTING-E2E-16-%E8%BF%BD%E8%B7%A1%E7%A2%BA%E8%AA%8D.png)|![ACCOUNTING-E2E-20 multi409](https://devin-gtm.devinenterprise.com/attachments/3afdd516-d44f-4f27-9a8f-402c53d418f2/ACCOUNTING-E2E-20-%E6%9C%AC%E6%93%8D%E4%BD%9C.png)|

### ロールバック後の先行2件

|21: E2E_ROLLBACK_Aは404|21追跡: E2E_ROLLBACK_Bも404|
|---|---|
|![ACCOUNTING-E2E-21 A404](https://devin-gtm.devinenterprise.com/attachments/00fe7d87-b356-40f4-99f6-386eb5208666/ACCOUNTING-E2E-21-%E6%9C%AC%E6%93%8D%E4%BD%9C.png)|![ACCOUNTING-E2E-21 B404](https://devin-gtm.devinenterprise.com/attachments/6b6e28b2-fee0-4708-8d21-995ebef4f0ec/ACCOUNTING-E2E-21-%E8%BF%BD%E8%B7%A1%E7%A2%BA%E8%AA%8D.png)|
