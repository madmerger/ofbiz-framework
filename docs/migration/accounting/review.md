# Accounting Spring Boot 移行レビュー

## 1. 検証結果

| 検証 | 結果 |
| --- | --- |
| `./gradlew :migration:accounting-springboot:check --console=plain` | **BUILD SUCCESSFUL** |
| `AccountingCatalogTest` | 500 pass / 0 fail / 0 skip |
| `AccountingFlowTest` | 3 pass / 0 fail / 0 skip |
| `AccountingSecurityTest` | 3 pass / 0 fail / 0 skip |
| `AcctgTransServicesTest` | 10 pass / 0 fail / 0 skip |
| `FinAccountServicesTest` | 38 pass / 0 fail / 0 skip |
| `GeneralLedgerServicesTest` | 28 pass / 0 fail / 0 skip |
| `GlPostingServicesTest` | 27 pass / 0 fail / 0 skip |
| `PaymentGatewayConfigServicesTest` | 1 pass / 0 fail / 0 skip |
| `PaymentMethodServicesTest` | 7 pass / 0 fail / 0 skip |
| `PostgresSmokeTest` | 2 pass / 0 fail / 0 skip |
| `HttpsSecurityTest` / `SpringdocProfileTest` | 各1 pass / 0 fail / 0 skip |
| 合計 | **621 pass / 0 fail / 0 skip** |
| local bootRun と `cases.json` の E2E 予行 | 以前のE2E-01〜21は21/21成功。GL posting 追加後の01〜23再予行は未実施 |
| inventory 再生成 | service 705、entity-auto 386、request 551、entity 147、指定 accounting entitymodel のview-entity 37 |

entity-auto CRUD acceptance は77件実行、54件スキップ（CRUD三つ組不足23、型・桁・integrity制約22、validation／required依存9）。simple-handlerの登録・404検証とサービス別テストはこの集計とは別に実施した。

## 2. OFBiz との差分と既知の制約

- `ACCOUNTING_ROLE_*` による組織／ロール単位の権限は未移植。`ACCOUNTING_*` 権限の保持者は全組織の伝票を操作できる。これは OFBiz の `acctgInvoicePermissionCheck` と同等の挙動。
- `createInvoice`: PartyAcctgPreference の `invoiceIdPrefix` と per-party invoice sequence enforcement は未移植。
- `setInvoiceStatus`: 承認時の自動PaymentApplication照合・gateway captureは未移植。
- `createInvoiceForOrder`: adjustments、billing account、contact-mech、shipment grouping、payment-preference auto-application、fees は未移植。
- ECA起動元は Accounting 内の invoice READY、received/sent payment、invoice payment application、FinAccountTrans のGL account指定に限定して移植。出荷・入荷・在庫・作業・固定資産、および未移植 Java `cancelInvoice` / `removePaymentApplication` からの起動は未配線。
- 注文請求: `SALES_ORDER` のみ。purchase order / return order は未移植。未請求数量のない再請求は新しい invoice を作らない。
- Accounting の simple service 93件にはGL自動仕訳20件を含み、catalog testで登録集合を検証する。commit ECA の内部 ledger entry 作成では、専用実行コンテキストに `ACCTG_ATX_CREATE` のみを付与し、完了後に元の SecurityContext を復元する。GL posting のテスト27件で20サービスすべての正常系、請求書READY、入金・消込、再READY抑止、CUSTOMER_REFUND分岐、逆仕訳、失敗分離、認証コンテキスト復元を確認する。
- `unported.md` の省略分岐節に、PaymentMethod住所更新の未移植Java呼出し、GL/AcctgTransの外貨換算・GROUP_ROLLUP・posting設定とerror journal、FinAccountの未移植Groovy／支払バッチ／GL呼出しの省略を記載。
- Widget、FTL、view-entity はSpring APIに移植しない。全一覧は `unported.md`。
- ECA/SECA engine は実装していない。FinAccount の残高を更新するコンポーネント内 ECA は移植したが、それ以外の暗黙のイベント連鎖は発生しない。

### GL自動仕訳 simple 20件の照合状況

minilang本文とhandlerの入力・条件・仕訳ヘッダー（伝票type/fiscal type）・明細（勘定科目type、借貸、金額、組織）を20サービスすべて照合した。正常系テストは全サービスで実行し、仕訳type、借貸合計および勘定科目typeを検証している。説明文と本文に差がある場合はminilang本文のentry定義を基準にした。

| サービス名 | 照合結果 | 省略した分岐 |
| --- | --- | --- |
| `postFinAccountTransToGl` | `RECEIPT`、`ACTUAL`、入出金側GL勘定と金額を確認。`glAccountId`ありのみ転記 | DEPOSIT以外の取引typeとFinAccountType fallbackの組合せ |
| `createAcctgTransForSalesShipmentIssuance` | `SALES_SHIPMENT`、COGS debit / inventory credit、平均原価と数量を確認 | FIFO/LIFO原価層、在庫不足、serialized inventory |
| `createAcctgTransForCanceledSalesShipmentIssuance` | `SALES_SHIPMENT`、取消数量によるinventory debit / COGS creditを確認 | BillTo party・平均原価の組合せ |
| `createAcctgTransForInventoryItemCostChange` | `INVENTORY`、旧・新unitCostとon-hand数量、INV_ADJ_VAL / inventoryを確認 | 新旧detailの同一有効日、ゼロ差額 |
| `createAcctgTransForShipmentReceipt` | `SHIPMENT_RECEIPT`、inventory debit / uninvoiced receipt credit、受入数量を確認 | return receipt、数量0以下、enableAccounting無効 |
| `createAcctgTransForWorkEffortIssuance` | `INVENTORY`、assignment合計、WIP debit / RAWMAT credit、親GoodStandard productを確認 | 明示quantity override、enableAccounting無効、serialized inventory |
| `createAcctgTransForPhysicalInventoryVariance` | `ITEM_VARIANCE`、数量差×unitCost、reasonEnumId credit / inventory debitを確認 | 複数明細と負の数量差 |
| `createAcctgTransForWorkEffortInventoryProduced` | `INVENTORY`、on-hand×unitCost、inventory debit / WIP creditを確認 | 同一work effortでの複数回生産 |
| `createAcctgTransForWorkEffortCost` | `MANUFACTURING`、CostComponentCalcのcost/offset type、Facility組織、GoodStandard productを確認 | 固定資産fallback、cost calculation未設定 |
| `createAcctgTransForInventoryItemOwnerChange` | `INVENTORY`、旧・新ownerごとの4明細、組織と金額を確認。XML本文は両ownerのcreditを`INVENTORY_XFER_IN`と定義（short-descriptionと差異） | 同一owner、数量ゼロ、serialized inventory |
| `createAcctgTransForFixedAssetMaintIssuance` | `INVENTORY`、fixedAssetId、fixed asset maintenance debit / inventory creditを確認 | 明細の異なる固定資産保全type |
| `createAcctgTransForCustomerReturnInvoice` | `CUST_RTN_INVOICE`、return line debit / AR credit、組織を確認 | InvoiceWorker由来の税明細と税額 |
| `createAcctgTransForPurchaseInvoice` | `PURCHASE_INVOICE`、invoice line debit / AP credit、仕入先組織を確認 | OrderItemBilling価格差異、InvoiceWorker税明細、子InvoiceType |
| `createAcctgTransForSalesInvoice` | `SALES_INVOICE`、invoice line credit / AR debit、発行組織を確認 | InvoiceWorker税明細・tax authority mapping |
| `createAcctgTransAndEntriesForIncomingPayment` | `INCOMING_PAYMENT`、receipt判定、actual currency amount、PaymentGlAccountTypeMapを確認 | 既存PaymentApplicationの反復作成、外貨換算 |
| `createAcctgTransAndEntriesForOutgoingPayment` | `OUTGOING_PAYMENT`、支払組織、AP/現金勘定の方向を確認 | 差額・override・ready/paid invoiceのapplication反復 |
| `createAcctgTransAndEntriesForPaymentApplication` | `PAYMENT_APPL`、未払invoiceのskip、通常receiptのunapplied/AR entryを確認 | 全PaymentType、override、外貨の組合せ |
| `createAcctgTransAndEntriesForCustomerRefundPaymentApplication` | `PAYMENT_APPL`、CUSTOMER_REFUNDのAR/unapplied entryとECA出し分けを確認 | status別・override別のentry |
| `revertAcctgTransOnCancelInvoice` | 元仕訳のAcctgTransType・FiscalTypeを保持し、全entryの借貸を反転することを確認 | 同一invoiceの複数仕訳 |
| `revertAcctgTransOnRemovePaymentApplications` | PaymentApplicationのinvoice/payment対の元仕訳と全entryを反転することを確認 | 複数application・複数元仕訳 |

## 3. セキュリティ選定

機密14 Entityは、カード・金融口座・金融取引・ゲートウェイ応答／設定情報を含むことを基準に選定した。`Payment` と `Invoice` は認証情報そのものを持たないため通常のAccounting権限とする。PaymentMethod/CreditCardの参照、カード番号redaction、機密entity-auto作成制限は `AccountingSecurityTest` で確認する。

`ACCOUNTING_ROLE_*` による組織／ロール単位の制限は未移植であり、`ACCOUNTING_*` 権限の保持者は全組織の伝票を操作できる。OFBiz の `acctgInvoicePermissionCheck` も組織所有者による追加制限を行わない。

Tomcat trusted proxy の既定値をlocalを含むAccounting profileで `a^` とし、明示設定がない限り転送ヘッダーを信頼しない。

## 4. 共通基盤への改善提案

- generated ServiceCatalog に `permission-service` 名と `main-action` を保持し、handler/EntityAutoを通じて共通permission dispatchで適用できるようにする。
- sensitive entity の read/create/update に対する統一契約と、呼出し元identityに応じた操作権限のテスト可能なAPIを整備する。
- `EntityRegistry.findByEquality` の100件上限により一覧・集計結果が欠落しうる。共通基盤に上限なし検索または明示的なページング契約を追加する（Accounting ではモジュール内の無制限 Criteria 検索で補完）。
- 上記提案のために共通基盤ソースは変更していない。
