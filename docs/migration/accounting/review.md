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
| `GlPostingServicesTest` | 5 pass / 0 fail / 0 skip |
| `PaymentGatewayConfigServicesTest` | 1 pass / 0 fail / 0 skip |
| `PaymentMethodServicesTest` | 7 pass / 0 fail / 0 skip |
| `PostgresSmokeTest` | 2 pass / 0 fail / 0 skip |
| `HttpsSecurityTest` / `SpringdocProfileTest` | 各1 pass / 0 fail / 0 skip |
| 合計 | **599 pass / 0 fail / 0 skip** |
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
- Accounting の simple service 93件にはGL自動仕訳20件を含み、catalog testで登録集合を検証する。commit ECA の内部 ledger entry 作成では、専用実行コンテキストに `ACCTG_ATX_CREATE` のみを付与し、完了後に元の SecurityContext を復元する。GL posting のテストは5件で、invoice READY、入金・消込、再READY抑止、失敗分離、認証コンテキスト復元を確認するが、GL自動仕訳20サービス個々の正常系・分岐は未網羅。
- `unported.md` の省略分岐節に、PaymentMethod住所更新の未移植Java呼出し、GL/AcctgTransの外貨換算・GROUP_ROLLUP・posting設定とerror journal、FinAccountの未移植Groovy／支払バッチ／GL呼出しの省略を記載。
- Widget、FTL、view-entity はSpring APIに移植しない。全一覧は `unported.md`。
- ECA/SECA engine は実装していない。FinAccount の残高を更新するコンポーネント内 ECA は移植したが、それ以外の暗黙のイベント連鎖は発生しない。

### GL自動仕訳 simple 20件の照合状況

これは minilang の入力・条件・伝票ヘッダー・各明細を確認するための途中記録であり、20件すべての照合完了や正常系テスト完了を示すものではない。未確認の項目はテストと実データによる確認が必要。

| service name | 照合結果 | 省略・未確認の分岐 |
| --- | --- | --- |
| `postFinAccountTransToGl` | branch と `ACTUAL`、FinAccountTypeGlAccount fallback を確認 | 不正なFinAccountTransTypeとGL未設定時の応答、entry account/amountを未検証 |
| `createAcctgTransForSalesShipmentIssuance` | `SALES_SHIPMENT`、COGS debit / inventory credit を反映 | FIFO/LIFO原価層、在庫不足エラー、serialized inventory |
| `createAcctgTransForCanceledSalesShipmentIssuance` | `SALES_SHIPMENT`、取消数量と逆方向のentryを反映 | BillTo partyと平均原価の正確な計算を未検証 |
| `createAcctgTransForInventoryItemCostChange` | 旧・新unitCostとon-hand数量、ゼロ時のskipを反映 | 有効日が同一のdetail選択、entry結果を未検証 |
| `createAcctgTransForShipmentReceipt` | 数量0以下skip、return時のCOGS、detail作成を反映 | receipt/return accountの全条件とentry結果を未検証 |
| `createAcctgTransForWorkEffortIssuance` | assignment quantity、WIP/RAWMAT、負の在庫detailを反映 | 親work effort・GoodStandard選択とenableAccounting分岐を未検証 |
| `createAcctgTransForPhysicalInventoryVariance` | InventoryItemDetailの数量差・reasonEnumId、`ITEM_VARIANCE`を反映 | 複数在庫品目での仕訳と負数明細を未検証 |
| `createAcctgTransForWorkEffortInventoryProduced` | on-hand数量・unitCost、Inventory/WIP、detail作成を反映 | 複数回生産時の数量と仕訳結果を未検証 |
| `createAcctgTransForWorkEffortCost` | CostComponentCalc、Facility、GoodStandardを参照し`MANUFACTURING`に修正 | fixed asset fallback、Calculation未設定時、entry結果を未検証 |
| `createAcctgTransForInventoryItemOwnerChange` | 旧・新ownerの4明細を反映 | 同一owner・数量ゼロ時を含む結果を未検証 |
| `createAcctgTransForFixedAssetMaintIssuance` | `INVENTORY`、fixed asset ID、固定資産保全費用/在庫の方向を反映 | source entity投入によるAPI正常系を未検証 |
| `createAcctgTransForCustomerReturnInvoice` | return header、明細方向、AR側組織を修正 | InvoiceWorker由来の税明細と税額を未移植 |
| `createAcctgTransForPurchaseInvoice` | invoice lineとAP balance entryを確認 | OrderItemBilling価格差異、InvoiceWorker税明細、子InvoiceTypeを未検証 |
| `createAcctgTransForSalesInvoice` | `SALES_INVOICE`、lineとAR balance entryを確認 | InvoiceWorker税明細・tax authority mappingを未移植 |
| `createAcctgTransAndEntriesForIncomingPayment` | receipt判定、actual currency amount、PaymentGlAccountTypeMapを確認 | 既存PaymentApplicationの反復作成と為替ケースを未検証 |
| `createAcctgTransAndEntriesForOutgoingPayment` | disbursement側の組織、entry方向、headerを確認 | 差額・override・ready/paid invoiceのapplication反復を未検証 |
| `createAcctgTransAndEntriesForPaymentApplication` | 未払・AR/AP map skip、通常receipt/refund分岐を確認 | 全PaymentType、override、外貨の組合せを未検証 |
| `createAcctgTransAndEntriesForCustomerRefundPaymentApplication` | refund用組織・AR/map分岐を確認 | status別、override別のentryを未検証 |
| `revertAcctgTransOnCancelInvoice` | paymentIdなしのinvoice仕訳を反転する経路を確認 | 複数取引と全entryの反転を未検証 |
| `revertAcctgTransOnRemovePaymentApplications` | PaymentApplicationのinvoice/payment対の仕訳を反転する経路を確認 | 複数取引と全entryの反転を未検証 |

## 3. セキュリティ選定

機密14 Entityは、カード・金融口座・金融取引・ゲートウェイ応答／設定情報を含むことを基準に選定した。`Payment` と `Invoice` は認証情報そのものを持たないため通常のAccounting権限とする。PaymentMethod/CreditCardの参照、カード番号redaction、機密entity-auto作成制限は `AccountingSecurityTest` で確認する。

`ACCOUNTING_ROLE_*` による組織／ロール単位の制限は未移植であり、`ACCOUNTING_*` 権限の保持者は全組織の伝票を操作できる。OFBiz の `acctgInvoicePermissionCheck` も組織所有者による追加制限を行わない。

Tomcat trusted proxy の既定値をlocalを含むAccounting profileで `a^` とし、明示設定がない限り転送ヘッダーを信頼しない。

## 4. 共通基盤への改善提案

- generated ServiceCatalog に `permission-service` 名と `main-action` を保持し、handler/EntityAutoを通じて共通permission dispatchで適用できるようにする。
- sensitive entity の read/create/update に対する統一契約と、呼出し元identityに応じた操作権限のテスト可能なAPIを整備する。
- `EntityRegistry.findByEquality` の100件上限により一覧・集計結果が欠落しうる。共通基盤に上限なし検索または明示的なページング契約を追加する（Accounting ではモジュール内の無制限 Criteria 検索で補完）。
- 上記提案のために共通基盤ソースは変更していない。
