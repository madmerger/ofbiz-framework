# Accounting 未移植一覧

この一覧は `applications/accounting/servicedef/*.xml`、Accounting の entitymodel、Widget XML、FreeMarker/Groovy ファイルから生成した。
現在のコードに存在する実装だけを移植済みとして数え、未実装のサービスを移植済みとみなさない。

## 件数集計

| 区分 | 件数 | 状態 |
| --- | ---: | --- |
| entity-auto | 386 | AccountingCatalogRegistrar に登録 |
| groovy | 88 | 主要フロー 6 本を移植、82 本未移植 |
| java | 109 | 主要フロー 2 本を移植、107 本未移植 |
| simple 合計 | 93 | 73本を移植、GL自動仕訳20本を意図的に除外 |
| simple: 移植済み | 73 | FinAccount 27、GL 19、Permission 13、AcctgTrans 8、PaymentMethod 5、GatewayConfig 1 |
| simple: GL 自動仕訳除外 | 20 | 意図的に未登録・404 |
| group | 10 | 未移植 |
| interface | 19 | 未移植 |
| view-entity | 40 | 未移植（JPA view 化なし） |
| Widget screen / form / menu | 341 / 261 / 52 | 画面未移植 |
| FreeMarker template | 37 | 画面未移植 |
| Groovy files | 74 | 画面／業務スクリプト未移植 |

## 権限スコープの制約

`ACCOUNTING_ROLE_*` による組織／ロール単位の権限は未移植。`ACCOUNTING_*` 権限の保持者は全組織の伝票を操作できる。OFBiz の `acctgInvoicePermissionCheck` と同等の範囲であり、組織所有者による追加制限はない。

## 移植済みの Java / Groovy サービス（主要フロー 8 本）

| service name | engine | location | 状態 |
| --- | --- | --- | --- |
| `createInvoice` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy` | 主要フローとして移植（`AccountingServiceHandlers`） |
| `setInvoiceStatus` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy` | 主要フローとして移植（`AccountingServiceHandlers`） |
| `createInvoiceItem` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy` | 主要フローとして移植（`AccountingServiceHandlers`） |
| `createInvoiceForOrder` | java | `org.apache.ofbiz.accounting.invoice.InvoiceServices` | 主要フローとして移植（`AccountingServiceHandlers`） |
| `checkInvoicePaymentApplications` | java | `org.apache.ofbiz.accounting.invoice.InvoiceServices` | 主要フローとして移植（`AccountingServiceHandlers`） |
| `createPayment` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy` | 主要フローとして移植（`AccountingServiceHandlers`） |
| `setPaymentStatus` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy` | 主要フローとして移植（`AccountingServiceHandlers`） |
| `createPaymentApplication` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy` | 主要フローとして移植（`AccountingServiceHandlers`） |

このほか、OFBiz では servicedef に定義のない `InvoiceWorker` のユーティリティ（`getInvoiceTotal`、`getInvoiceItemTotal`、`getInvoiceNotApplied`、`getInvoiceApplied`）を、照会用サービスとして `AccountingServiceHandlers` に追加した。

## Java / Groovy サービス

| service name | engine | location | 理由 |
| --- | --- | --- | --- |
| `createPartyAcctgPreference` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/admin/AcctgAdminServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `getPartyAccountingPreferences` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/admin/AcctgAdminServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `setAcctgCompany` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/admin/AcctgAdminServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `updateFXConversion` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/admin/AcctgAdminServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `getFXConversion` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/admin/AcctgAdminServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `createFuturePeriod` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/admin/AcctgAdminServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `copyAgreement` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/agreement/AgreementServicesScript.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `getCommissionForProduct` | java | `org.apache.ofbiz.accounting.agreement.AgreementServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `calcBillingAccountBalance` | java | `org.apache.ofbiz.accounting.payment.BillingAccountWorker` | このタスクでの Java/Groovy サービス移植対象外 |
| `updateBudgetStatus` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/budget/BudgetServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `updateProductAverageCostOnReceiveInventory` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/cost/CostServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `getProductAverageCost` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/cost/CostServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `createFinAccountForStore` | java | `org.apache.ofbiz.accounting.finaccount.FinAccountServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `finAccountDeposit` | java | `org.apache.ofbiz.accounting.finaccount.FinAccountPaymentServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `finAccountWithdraw` | java | `org.apache.ofbiz.accounting.finaccount.FinAccountPaymentServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `finAccountRefund` | java | `org.apache.ofbiz.accounting.finaccount.FinAccountServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `finAccountReplenish` | java | `org.apache.ofbiz.accounting.finaccount.FinAccountPaymentServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `checkFinAccountBalance` | java | `org.apache.ofbiz.accounting.finaccount.FinAccountServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `checkFinAccountStatus` | java | `org.apache.ofbiz.accounting.finaccount.FinAccountServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `createServiceCredit` | java | `org.apache.ofbiz.accounting.finaccount.FinAccountServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `createFinAccountAndCredit` | java | `org.apache.ofbiz.accounting.finaccount.FinAccountServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `createPartyFinAccountFromPurchase` | java | `org.apache.ofbiz.accounting.finaccount.FinAccountProductServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `ofbFaAuthorize` | java | `org.apache.ofbiz.accounting.finaccount.FinAccountPaymentServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `ofbFaCapture` | java | `org.apache.ofbiz.accounting.finaccount.FinAccountPaymentServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `ofbFaRelease` | java | `org.apache.ofbiz.accounting.finaccount.FinAccountPaymentServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `ofbFaRefund` | java | `org.apache.ofbiz.accounting.finaccount.FinAccountPaymentServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `createGiftCertificate` | java | `org.apache.ofbiz.accounting.payment.GiftCertificateServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `addFundsToGiftCertificate` | java | `org.apache.ofbiz.accounting.payment.GiftCertificateServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `redeemGiftCertificate` | java | `org.apache.ofbiz.accounting.payment.GiftCertificateServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `checkGiftCertificateBalance` | java | `org.apache.ofbiz.accounting.payment.GiftCertificateServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `createGcFulFillmentRecord` | java | `org.apache.ofbiz.accounting.payment.GiftCertificateServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `refundGcPurchase` | java | `org.apache.ofbiz.accounting.payment.GiftCertificateServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `ofbGcProcessor` | java | `org.apache.ofbiz.accounting.payment.GiftCertificateServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `ofbGcAuthorize` | java | `org.apache.ofbiz.accounting.payment.GiftCertificateServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `ofbGcRelease` | java | `org.apache.ofbiz.accounting.payment.GiftCertificateServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `ofbGcRefund` | java | `org.apache.ofbiz.accounting.payment.GiftCertificateServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `ofbGcPurchase` | java | `org.apache.ofbiz.accounting.payment.GiftCertificateServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `ofbGcReload` | java | `org.apache.ofbiz.accounting.payment.GiftCertificateServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `decryptFinAccountCodes` | java | `org.apache.ofbiz.accounting.finaccount.FinAccountServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `createFixedAssetMaint` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/fixedasset/FixedAssetServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `updateFixedAssetMaint` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/fixedasset/FixedAssetServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `createMaintsFromTimeInterval` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/fixedasset/FixedAssetServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `autoAssignFixedAssetPartiesToMaintenance` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/fixedasset/FixedAssetServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `createMaintsFromMeterReading` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/fixedasset/FixedAssetServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `createFixedAssetMaintOrder` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/fixedasset/FixedAssetServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `checkUpdateFixedAssetDepreciation` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/fixedasset/FixedAssetServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `straightLineDepreciation` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/fixedasset/FixedAssetServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `doubleDecliningBalanceDepreciation` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/fixedasset/FixedAssetServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `calculateFixedAssetDepreciation` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/fixedasset/FixedAssetServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `getNextInvoiceId` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `invoiceSequenceEnforced` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `invoiceSequenceRestart` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `copyInvoice` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `getInvoice` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `updateInvoice` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `copyInvoiceToTemplate` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `checkInvoiceStatusInProgress` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `updateInvoiceItem` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `removeInvoiceItem` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `createInvoiceForOrderAllItems` | java | `org.apache.ofbiz.accounting.invoice.InvoiceServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `createInvoiceFromReturn` | java | `org.apache.ofbiz.accounting.invoice.InvoiceServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `createCommissionInvoices` | java | `org.apache.ofbiz.accounting.invoice.InvoiceServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `readyInvoices` | java | `org.apache.ofbiz.accounting.invoice.InvoiceServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `createInvoicesFromShipment` | java | `org.apache.ofbiz.accounting.invoice.InvoiceServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `setInvoicesToReadyFromShipment` | java | `org.apache.ofbiz.accounting.invoice.InvoiceServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `createSalesInvoicesFromDropShipment` | java | `org.apache.ofbiz.accounting.invoice.InvoiceServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `createInvoicesFromShipments` | java | `org.apache.ofbiz.accounting.invoice.InvoiceServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `createInvoicesFromReturnShipment` | java | `org.apache.ofbiz.accounting.invoice.InvoiceServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `sendInvoicePerEmail` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `calculateInvoicedAdjustmentTotal` | java | `org.apache.ofbiz.accounting.invoice.InvoiceServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `autoGenerateInvoiceFromExistingInvoice` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `cancelInvoice` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `getInvoiceRunningTotal` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `addTaxOnInvoice` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `getInvoicesFilterByAssocType` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `removeInvoiceItemAssocOnCancelInvoice` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `resetOrderItemBillingAndOrderAdjustmentBillingOnCancelInvoice` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `massChangeInvoiceStatus` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `createInvoiceFromOrder` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `isInvoiceInForeignCurrency` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `importInvoice` | java | `org.apache.ofbiz.accounting.invoice.InvoiceServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `addInvoiceNote` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `createAcctgTrans` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/ledger/GeneralLedgerServicesScript.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `findCustomTimePeriods` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/period/GPeriodServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `findLastClosedDate` | java | `org.apache.ofbiz.accounting.period.PeriodServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `getPreviousTimePeriod` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/period/GPeriodServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `createUpdateCostCenter` | java | `org.apache.ofbiz.accounting.ledger.GeneralLedgerServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `updatePayment` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `checkPaymentInvoices` | java | `org.apache.ofbiz.accounting.invoice.InvoiceServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `quickSendPayment` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `updatePaymentApplication` | java | `org.apache.ofbiz.accounting.invoice.InvoiceServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `updatePaymentApplicationDef` | java | `org.apache.ofbiz.accounting.invoice.InvoiceServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `removePaymentApplication` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `createPaymentAndApplication` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `getInvoicePaymentInfoList` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `getInvoicePaymentInfoListByDueDateOffset` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `voidPayment` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `getPaymentRunningTotal` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `cancelPaymentBatch` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `createPaymentAndPaymentGroupForInvoices` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `createPaymentAndApplicationForParty` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `createPaymentGroupAndMember` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `cancelCheckRunPayments` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `createFinAccoutnTransFromPayment` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `getPayments` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `getPaymentGroupReconciliationId` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `checkAndCreateBatchForValidPayments` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `massChangePaymentStatus` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `createPaymentFromOrder` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `createMatchingPaymentApplication` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `createPaymentContent` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `updatePaymentContent` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `deletePaymentMethod` | java | `org.apache.ofbiz.accounting.payment.PaymentMethodServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `createCreditCard` | java | `org.apache.ofbiz.accounting.payment.PaymentMethodServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `updateCreditCard` | java | `org.apache.ofbiz.accounting.payment.PaymentMethodServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `clearCreditCardDataAndExpire` | java | `org.apache.ofbiz.accounting.payment.PaymentMethodServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `buildCcExpireDate` | java | `org.apache.ofbiz.accounting.payment.PaymentMethodServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `createGiftCard` | java | `org.apache.ofbiz.accounting.payment.PaymentMethodServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `updateGiftCard` | java | `org.apache.ofbiz.accounting.payment.PaymentMethodServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `createEftAccount` | java | `org.apache.ofbiz.accounting.payment.PaymentMethodServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `createCheckAccount` | java | `org.apache.ofbiz.accounting.payment.PaymentMethodServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `updateCheckAccount` | java | `org.apache.ofbiz.accounting.payment.PaymentMethodServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `updateEftAccount` | java | `org.apache.ofbiz.accounting.payment.PaymentMethodServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `manualForcedCcAuthTransaction` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `manualForcedCcTransaction` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `retryFailedAuths` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `retryFailedOrderAuth` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `retryFailedAuthNsfs` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `authOrderPaymentPreference` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `authOrderPayments` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `releaseOrderPayments` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `releaseOrderPaymentPreference` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `creditOrderPaymentPreference` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `capturePaymentsByInvoice` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `captureOrderPayments` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `captureBillingAccountPayments` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `processCaptureSplitPayment` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `refundOrderPaymentPreference` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `refundPayment` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `processAuthResult` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `processCaptureResult` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `processReleaseResult` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `processCreditResult` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `processRefundResult` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `processPaymentServiceError` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `savePaymentGatewayResponse` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `savePaymentGatewayResponseAndMessages` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `testCCProcessor` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `testCCProcessorWithCapture` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `testRandomAuthorize` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `alwaysApproveCCProcessor` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `alwaysApproveWithCaptureCCProcessor` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `alwaysDeclineCCProcessor` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `alwaysNsfCCProcessor` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `alwaysBadExpireCCProcessor` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `badExpireEvenCCProcessor` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `alwaysBadCardNumberCCProcessor` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `alwaysFailCCProcessor` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `testCCCapture` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `testCCCaptureWithReAuth` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `testCCProcessorCaptureAlwaysDecline` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `testCCRelease` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `testCCRefund` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `testCCRefundFailure` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `alwaysApproveGCProcessor` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `alwaysDeclineGCProcessor` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `testGCRelease` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `testEFTProcessor` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `alwaysApproveEFTProcessor` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `alwaysDeclineEFTProcessor` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `testEFTRelease` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `verifyCreditCard` | java | `org.apache.ofbiz.accounting.payment.PaymentGatewayServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `updateRateAmount` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/rate/RateServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `expireRateAmount` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/rate/RateServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `deleteRateAmount` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/rate/RateServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `getRateAmount` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/rate/RateServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `getRatesAmountsFromWorkEffortId` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/rate/RateServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `getRatesAmountsFromPartyId` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/rate/RateServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `getRatesAmountsFromEmplPositionTypeId` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/rate/RateServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `filterRateAmountList` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/rate/RateServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `updatePartyRate` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/rate/RateServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `deletePartyRate` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/rate/RateServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `expirePartyRate` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/rate/RateServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `sampleInvoiceAffiliateCommission` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/SampleServices.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `calcTax` | java | `org.apache.ofbiz.accounting.tax.TaxAuthorityServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `calcTaxForDisplay` | java | `org.apache.ofbiz.accounting.tax.TaxAuthorityServices` | このタスクでの Java/Groovy サービス移植対象外 |
| `createPartyTaxAuthInfo` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/tax/TaxAuthorityServicesScript.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `updatePartyTaxAuthInfo` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/tax/TaxAuthorityServicesScript.groovy` | このタスクでの Java/Groovy サービス移植対象外 |
| `createCustomerTaxAuthInfo` | groovy | `component://accounting/src/main/groovy/org/apache/ofbiz/accounting/tax/TaxAuthorityServicesScript.groovy` | このタスクでの Java/Groovy サービス移植対象外 |

## simple サービス（未移植の GL 自動仕訳 20 本のみ）

その他の simple サービス73本は Java handler として移植済み。servicedef 由来の期待集合で登録を検証し、以下20本は未登録・404とする。

### GL 自動仕訳 simple 20 本（意図的除外）

GL 自動仕訳・転記は未移植 Java/Groovy 呼び出しや会計ポスティング副作用を含むため、handler を登録せず API は404とする。

| service name | engine | location | 理由 |
| --- | --- | --- | --- |
| `postFinAccountTransToGl` | simple | `component://accounting/minilang/finaccount/FinAccountGlPostServices.xml` | GL 自動仕訳を対象外とする明示的判断 |
| `createAcctgTransForSalesShipmentIssuance` | simple | `component://accounting/minilang/ledger/GeneralLedgerServices.xml` | GL 自動仕訳を対象外とする明示的判断 |
| `createAcctgTransForCanceledSalesShipmentIssuance` | simple | `component://accounting/minilang/ledger/GeneralLedgerServices.xml` | GL 自動仕訳を対象外とする明示的判断 |
| `createAcctgTransForInventoryItemCostChange` | simple | `component://accounting/minilang/ledger/GeneralLedgerServices.xml` | GL 自動仕訳を対象外とする明示的判断 |
| `createAcctgTransForShipmentReceipt` | simple | `component://accounting/minilang/ledger/GeneralLedgerServices.xml` | GL 自動仕訳を対象外とする明示的判断 |
| `createAcctgTransForWorkEffortIssuance` | simple | `component://accounting/minilang/ledger/GeneralLedgerServices.xml` | GL 自動仕訳を対象外とする明示的判断 |
| `createAcctgTransForPhysicalInventoryVariance` | simple | `component://accounting/minilang/ledger/GeneralLedgerServices.xml` | GL 自動仕訳を対象外とする明示的判断 |
| `createAcctgTransForWorkEffortInventoryProduced` | simple | `component://accounting/minilang/ledger/GeneralLedgerServices.xml` | GL 自動仕訳を対象外とする明示的判断 |
| `createAcctgTransForWorkEffortCost` | simple | `component://accounting/minilang/ledger/GeneralLedgerServices.xml` | GL 自動仕訳を対象外とする明示的判断 |
| `createAcctgTransForInventoryItemOwnerChange` | simple | `component://accounting/minilang/ledger/GeneralLedgerServices.xml` | GL 自動仕訳を対象外とする明示的判断 |
| `createAcctgTransAndEntriesForIncomingPayment` | simple | `component://accounting/minilang/ledger/GeneralLedgerServices.xml` | GL 自動仕訳を対象外とする明示的判断 |
| `createAcctgTransForFixedAssetMaintIssuance` | simple | `component://accounting/minilang/ledger/GeneralLedgerServices.xml` | GL 自動仕訳を対象外とする明示的判断 |
| `createAcctgTransForCustomerReturnInvoice` | simple | `component://accounting/minilang/ledger/GeneralLedgerServices.xml` | GL 自動仕訳を対象外とする明示的判断 |
| `createAcctgTransForPurchaseInvoice` | simple | `component://accounting/minilang/ledger/GeneralLedgerServices.xml` | GL 自動仕訳を対象外とする明示的判断 |
| `createAcctgTransForSalesInvoice` | simple | `component://accounting/minilang/ledger/GeneralLedgerServices.xml` | GL 自動仕訳を対象外とする明示的判断 |
| `createAcctgTransAndEntriesForOutgoingPayment` | simple | `component://accounting/minilang/ledger/GeneralLedgerServices.xml` | GL 自動仕訳を対象外とする明示的判断 |
| `createAcctgTransAndEntriesForPaymentApplication` | simple | `component://accounting/minilang/ledger/GeneralLedgerServices.xml` | GL 自動仕訳を対象外とする明示的判断 |
| `createAcctgTransAndEntriesForCustomerRefundPaymentApplication` | simple | `component://accounting/minilang/ledger/GeneralLedgerServices.xml` | GL 自動仕訳を対象外とする明示的判断 |
| `revertAcctgTransOnCancelInvoice` | simple | `component://accounting/minilang/ledger/AcctgTransServices.xml` | GL 自動仕訳を対象外とする明示的判断 |
| `revertAcctgTransOnRemovePaymentApplications` | simple | `component://accounting/minilang/ledger/AcctgTransServices.xml` | GL 自動仕訳を対象外とする明示的判断 |

## group / interface

| service name | engine | location | 理由 |
| --- | --- | --- | --- |
| `uploadAgreementContentFile` | group | `—` | グループ／インターフェースの合成サービス動作は未移植 |
| `createBillingAccountAndRole` | group | `—` | グループ／インターフェースの合成サービス動作は未移植 |
| `createInvoiceContentAndUpdateContent` | group | `—` | グループ／インターフェースの合成サービス動作は未移植 |
| `updateInvoiceContentAndContent` | group | `—` | グループ／インターフェースの合成サービス動作は未移植 |
| `createSimpleTextContentForInvoice` | group | `—` | グループ／インターフェースの合成サービス動作は未移植 |
| `updateSimpleTextContentForInvoice` | group | `—` | グループ／インターフェースの合成サービス動作は未移植 |
| `createCreditCardAndAddress` | group | `createCreditCardAndAddress` | グループ／インターフェースの合成サービス動作は未移植 |
| `updateCreditCardAndAddress` | group | `updateCreditCardAndAddress` | グループ／インターフェースの合成サービス動作は未移植 |
| `createEftAccountAndAddress` | group | `createEftAccountAndAddress` | グループ／インターフェースの合成サービス動作は未移植 |
| `updateEftAccountAndAddress` | group | `updateEftAccountAndAddress` | グループ／インターフェースの合成サービス動作は未移植 |
| `fixedAssetDepCalcInterface` | interface | `—` | グループ／インターフェースの合成サービス動作は未移植 |
| `interfaceAcctgTrans` | interface | `—` | グループ／インターフェースの合成サービス動作は未移植 |
| `interfaceAcctgTransEntry` | interface | `—` | グループ／インターフェースの合成サービス動作は未移植 |
| `paymentProcessInterface` | interface | `—` | グループ／インターフェースの合成サービス動作は未移植 |
| `paymentReleaseInterface` | interface | `—` | グループ／インターフェースの合成サービス動作は未移植 |
| `paymentCreditInterface` | interface | `—` | グループ／インターフェースの合成サービス動作は未移植 |
| `paymentRefundInterface` | interface | `—` | グループ／インターフェースの合成サービス動作は未移植 |
| `ccAuthInterface` | interface | `—` | グループ／インターフェースの合成サービス動作は未移植 |
| `ccCaptureInterface` | interface | `—` | グループ／インターフェースの合成サービス動作は未移植 |
| `ccCreditInterface` | interface | `—` | グループ／インターフェースの合成サービス動作は未移植 |
| `eftProcessInterface` | interface | `—` | グループ／インターフェースの合成サービス動作は未移植 |
| `payPalProcessInterface` | interface | `—` | グループ／インターフェースの合成サービス動作は未移植 |
| `payPalCaptureInterface` | interface | `—` | グループ／インターフェースの合成サービス動作は未移植 |
| `payPalSetExpressCheckoutInterface` | interface | `—` | グループ／インターフェースの合成サービス動作は未移植 |
| `payPalGetExpressCheckoutInterface` | interface | `—` | グループ／インターフェースの合成サービス動作は未移植 |
| `payPalDoExpressCheckoutInterface` | interface | `—` | グループ／インターフェースの合成サービス動作は未移植 |
| `giftCardProcessInterface` | interface | `—` | グループ／インターフェースの合成サービス動作は未移植 |
| `calcTaxInterface` | interface | `—` | グループ／インターフェースの合成サービス動作は未移植 |
| `calcTaxTotalForDisplayInterface` | interface | `—` | グループ／インターフェースの合成サービス動作は未移植 |

## view-entity

- `FinAccountAndRole` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `FinAccountTransSum` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `FinAccountAuthSum` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `FixedAssetAndGeoPoint` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `PartyFixedAssetAssignAndRole` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `FixedAssetMaintWorkEffort` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `InvoiceAndApplAndPayment` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `InvoiceAndType` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `InvoiceAndRole` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `InvoiceItemAndAssocProduct` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `InvItemAndOrdItem` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `InvoiceItemAndShipmentView` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `InvoiceContentAndInfo` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `AcctgTransAndEntries` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `AcctgTransEntrySums` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `GlAccountAndHistory` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `GlAccountAndHistoryTotals` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `GlAccountOrganizationAndClass` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `PartyPrefDocTypeTplAndCustomScreen` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `BillingAccountAndRole` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `BillingAccountRoleAndAddress` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `PaymentAndTypePartyNameView` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `PaymentAndType` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `PaymentAndTypeAndCreditCard` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `PaymentAndApplication` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `PaymentContentAndInfo` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `PaymentMethodAndCreditCard` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `PaymentMethodAndEftAccount` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `PaymentMethodAndGiftCard` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `PartyTaxAuthInfoAndDetail` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `TaxAuthorityAndDetail` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `TaxAuthorityCategoryView` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `TaxAuthorityGlAccountBalance` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `GlAccOrgAndAcctgTransAndEntry` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `RateAmountAndRelations` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `PmtGrpMembrPaymentAndFinAcctTrans` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `PaymentMethodAndFinAccount` — `applications/datamodel/entitydef/accounting-entitymodel.xml`
- `InvoiceItemProductSummary` — `applications/accounting/entitydef/entitymodel_reports.xml`
- `InvoiceItemCategorySummary` — `applications/accounting/entitydef/entitymodel_reports.xml`
- `InvoiceExport` — `applications/accounting/entitydef/entitymodel_reports.xml`

## Widget screens / forms / menus

| kind | file | name |
| --- | --- | --- |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `AccountingAppBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `MainActionMenu` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `AccountingShortcutAppBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `InvoiceTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `InvoiceSubTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `PaymentTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `PaymentSubTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `PaymentGroupTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `PaymentGroupSubTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `FixedAssetMaintTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `FixedAssetTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `BudgetTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `BudgetSubTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `PartyGlShortCuts` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `EditGlAcctgTransSubTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `PartyTopTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `OrganizationAccountingTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `OrganizationAccountingReportsTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `PartyAdminTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `PartyAdminAssignTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `PartyAccountingChecksTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `GlobalGLSettingMenus` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `AgreementTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `AgreementItemTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `BillingAccountTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `GlAccountTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `GlSettingTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `TransactionsTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `PaymentGatewayConfigTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `GlAccountCategoryTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `FinAccountTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `FinAccountSubTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `FinAccountMainTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `FinAccountReconciliationsTabBar` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `NewAccountOrganization` |
| menu | `applications/accounting/widget/AccountingMenus.xml` | `TaxAuthorityTabBar` |
| screen | `applications/accounting/widget/AccountingPrintScreens.xml` | `InvoicePDF` |
| screen | `applications/accounting/widget/AccountingPrintScreens.xml` | `DefaultInvoicePDF` |
| screen | `applications/accounting/widget/AccountingPrintScreens.xml` | `PrintCheckPDF` |
| screen | `applications/accounting/widget/AccountingPrintScreens.xml` | `PrintInvoices` |
| form | `applications/accounting/widget/AgreementForms.xml` | `FindAgreements` |
| form | `applications/accounting/widget/AgreementForms.xml` | `EditAgreement` |
| form | `applications/accounting/widget/AgreementForms.xml` | `Agreement` |
| form | `applications/accounting/widget/AgreementForms.xml` | `EditAgreementItem` |
| form | `applications/accounting/widget/AgreementForms.xml` | `AddAgreementTerm` |
| form | `applications/accounting/widget/AgreementForms.xml` | `AddAgreementContent` |
| form | `applications/accounting/widget/AgreementForms.xml` | `EditAgreementItemTerm` |
| form | `applications/accounting/widget/AgreementForms.xml` | `ViewAgreementInfoForReport` |
| form | `applications/accounting/widget/AgreementForms.xml` | `ViewAgreementItemInfoForReport` |
| form | `applications/accounting/widget/AgreementForms.xml` | `EditAgreementItemProduct` |
| form | `applications/accounting/widget/AgreementForms.xml` | `EditAgreementItemFacility` |
| form | `applications/accounting/widget/AgreementForms.xml` | `EditAgreementItemSupplierProduct` |
| form | `applications/accounting/widget/AgreementForms.xml` | `EditAgreementPromoAppl` |
| form | `applications/accounting/widget/AgreementForms.xml` | `EditAgreementItemParty` |
| form | `applications/accounting/widget/AgreementForms.xml` | `EditAgreementGeographicalApplic` |
| form | `applications/accounting/widget/AgreementForms.xml` | `AddAgreementWorkEffortApplic` |
| form | `applications/accounting/widget/AgreementForms.xml` | `AddAgreementRole` |
| screen | `applications/accounting/widget/AgreementScreens.xml` | `CommonAgreementDecorator` |
| screen | `applications/accounting/widget/AgreementScreens.xml` | `FindAgreement` |
| screen | `applications/accounting/widget/AgreementScreens.xml` | `EditAgreement` |
| screen | `applications/accounting/widget/AgreementScreens.xml` | `ListAgreementItems` |
| screen | `applications/accounting/widget/AgreementScreens.xml` | `EditAgreementItem` |
| screen | `applications/accounting/widget/AgreementScreens.xml` | `EditAgreementTerms` |
| screen | `applications/accounting/widget/AgreementScreens.xml` | `EditAgreementContents` |
| screen | `applications/accounting/widget/AgreementScreens.xml` | `ListAgreementPromoAppls` |
| screen | `applications/accounting/widget/AgreementScreens.xml` | `EditAgreementPromoAppl` |
| screen | `applications/accounting/widget/AgreementScreens.xml` | `ListAgreementItemTerms` |
| screen | `applications/accounting/widget/AgreementScreens.xml` | `EditAgreementItemTerm` |
| screen | `applications/accounting/widget/AgreementScreens.xml` | `ListAgreementItemProducts` |
| screen | `applications/accounting/widget/AgreementScreens.xml` | `ListAgreementItemFacilities` |
| screen | `applications/accounting/widget/AgreementScreens.xml` | `ListAgreementItemProductsReport` |
| screen | `applications/accounting/widget/AgreementScreens.xml` | `ListAgreementItemFacilitiesReport` |
| screen | `applications/accounting/widget/AgreementScreens.xml` | `EditAgreementItemProduct` |
| screen | `applications/accounting/widget/AgreementScreens.xml` | `EditAgreementItemFacility` |
| screen | `applications/accounting/widget/AgreementScreens.xml` | `ListAgreementItemSupplierProducts` |
| screen | `applications/accounting/widget/AgreementScreens.xml` | `ListAgreementItemSupplierProductsReport` |
| screen | `applications/accounting/widget/AgreementScreens.xml` | `EditAgreementItemSupplierProduct` |
| screen | `applications/accounting/widget/AgreementScreens.xml` | `ListAgreementItemParties` |
| screen | `applications/accounting/widget/AgreementScreens.xml` | `EditAgreementItemParty` |
| screen | `applications/accounting/widget/AgreementScreens.xml` | `ListAgreementGeographicalApplic` |
| screen | `applications/accounting/widget/AgreementScreens.xml` | `EditAgreementGeographicalApplic` |
| screen | `applications/accounting/widget/AgreementScreens.xml` | `EditAgreementWorkEffortApplics` |
| screen | `applications/accounting/widget/AgreementScreens.xml` | `EditAgreementRoles` |
| form | `applications/accounting/widget/BillingAccountForms.xml` | `BillingAccount` |
| form | `applications/accounting/widget/BillingAccountForms.xml` | `EditBillingAccount` |
| form | `applications/accounting/widget/BillingAccountForms.xml` | `AddBillingAccountRole` |
| form | `applications/accounting/widget/BillingAccountForms.xml` | `EditBillingAccountTerms` |
| form | `applications/accounting/widget/BillingAccountForms.xml` | `CreateIncomingBillingAccountPayment` |
| form | `applications/accounting/widget/BillingAccountForms.xml` | `FindBillingAccounts` |
| screen | `applications/accounting/widget/BillingAccountScreens.xml` | `CommonBillingAccountDecorator` |
| screen | `applications/accounting/widget/BillingAccountScreens.xml` | `FindBillingAccount` |
| screen | `applications/accounting/widget/BillingAccountScreens.xml` | `EditBillingAccount` |
| screen | `applications/accounting/widget/BillingAccountScreens.xml` | `EditBillingAccountRoles` |
| screen | `applications/accounting/widget/BillingAccountScreens.xml` | `EditBillingAccountTerms` |
| screen | `applications/accounting/widget/BillingAccountScreens.xml` | `BillingAccountInvoices` |
| screen | `applications/accounting/widget/BillingAccountScreens.xml` | `BillingAccountOrders` |
| screen | `applications/accounting/widget/BillingAccountScreens.xml` | `BillingAccountPayments` |
| form | `applications/accounting/widget/BudgetForms.xml` | `FindBudgetOptions` |
| form | `applications/accounting/widget/BudgetForms.xml` | `EditBudget` |
| form | `applications/accounting/widget/BudgetForms.xml` | `BudgetHeader` |
| form | `applications/accounting/widget/BudgetForms.xml` | `EditBudgetItems` |
| form | `applications/accounting/widget/BudgetForms.xml` | `EditBudgetItem` |
| form | `applications/accounting/widget/BudgetForms.xml` | `EditBudgetRole` |
| form | `applications/accounting/widget/BudgetForms.xml` | `EditBudgetReview` |
| screen | `applications/accounting/widget/BudgetScreens.xml` | `ListBudgets` |
| screen | `applications/accounting/widget/BudgetScreens.xml` | `BudgetSearchResults` |
| screen | `applications/accounting/widget/BudgetScreens.xml` | `CommonBudgetDecorator` |
| screen | `applications/accounting/widget/BudgetScreens.xml` | `EditBudget` |
| screen | `applications/accounting/widget/BudgetScreens.xml` | `BudgetOverview` |
| screen | `applications/accounting/widget/BudgetScreens.xml` | `EditBudgetItems` |
| screen | `applications/accounting/widget/BudgetScreens.xml` | `BudgetRoles` |
| screen | `applications/accounting/widget/BudgetScreens.xml` | `BudgetReviews` |
| screen | `applications/accounting/widget/CommonScreens.xml` | `main-decorator` |
| screen | `applications/accounting/widget/CommonScreens.xml` | `ShortcutApp` |
| screen | `applications/accounting/widget/CommonScreens.xml` | `CommonFinAccountDecorator` |
| screen | `applications/accounting/widget/CommonScreens.xml` | `CommonPaymentDecorator` |
| screen | `applications/accounting/widget/CommonScreens.xml` | `CommonPaymentGroupDecorator` |
| screen | `applications/accounting/widget/CommonScreens.xml` | `CommonPartyDecorator` |
| screen | `applications/accounting/widget/CommonScreens.xml` | `CommonOrganizationAccountingReportsDecorator` |
| screen | `applications/accounting/widget/CommonScreens.xml` | `CommonAdminChecksDecorator` |
| screen | `applications/accounting/widget/CommonScreens.xml` | `main` |
| screen | `applications/accounting/widget/CommonScreens.xml` | `creditCardFields` |
| screen | `applications/accounting/widget/CommonScreens.xml` | `GlobalGLSettingsDecorator` |
| screen | `applications/accounting/widget/CommonScreens.xml` | `CommonInvoiceDecorator` |
| screen | `applications/accounting/widget/CommonScreens.xml` | `CommonEmbeddedDecorator` |
| form | `applications/accounting/widget/CostForms.xml` | `AddCostComponentCalc` |
| screen | `applications/accounting/widget/CostScreens.xml` | `EditCostCalcs` |
| form | `applications/accounting/widget/FieldLookupForms.xml` | `LookupFixedAsset` |
| form | `applications/accounting/widget/FieldLookupForms.xml` | `LookupBudget` |
| form | `applications/accounting/widget/FieldLookupForms.xml` | `ListLookupBudget` |
| form | `applications/accounting/widget/FieldLookupForms.xml` | `lookupBillingAccount` |
| form | `applications/accounting/widget/FieldLookupForms.xml` | `LookupGlAccount` |
| form | `applications/accounting/widget/FieldLookupForms.xml` | `ListLookupGlAccount` |
| form | `applications/accounting/widget/FieldLookupForms.xml` | `LookupPayment` |
| form | `applications/accounting/widget/FieldLookupForms.xml` | `ListPayment` |
| form | `applications/accounting/widget/FieldLookupForms.xml` | `LookupInvoice` |
| form | `applications/accounting/widget/FieldLookupForms.xml` | `ListInvoice` |
| form | `applications/accounting/widget/FieldLookupForms.xml` | `LookupAgreement` |
| form | `applications/accounting/widget/FieldLookupForms.xml` | `LookupAgreementItem` |
| form | `applications/accounting/widget/FieldLookupForms.xml` | `LookupPaymentGroupMember` |
| form | `applications/accounting/widget/FieldLookupForms.xml` | `LookupGlReconciliation` |
| form | `applications/accounting/widget/FieldLookupForms.xml` | `LookupCustomTimePeriod` |
| form | `applications/accounting/widget/FinAccountForms.xml` | `FindFinAccounts` |
| form | `applications/accounting/widget/FinAccountForms.xml` | `FinAccount` |
| form | `applications/accounting/widget/FinAccountForms.xml` | `EditFinAccount` |
| form | `applications/accounting/widget/FinAccountForms.xml` | `AddFinAccountRole` |
| form | `applications/accounting/widget/FinAccountForms.xml` | `AddFinAccountTrans` |
| form | `applications/accounting/widget/FinAccountForms.xml` | `AddFinAccountAuth` |
| form | `applications/accounting/widget/FinAccountForms.xml` | `PaymentsDepositWithdraw` |
| form | `applications/accounting/widget/FinAccountForms.xml` | `FindDepositSlips` |
| form | `applications/accounting/widget/FinAccountForms.xml` | `EditDepositSlip` |
| form | `applications/accounting/widget/FinAccountForms.xml` | `AddDepositSlipMember` |
| form | `applications/accounting/widget/FinAccountForms.xml` | `QuickFindFinAccounts` |
| form | `applications/accounting/widget/FinAccountForms.xml` | `FindFinAccountTransactions` |
| form | `applications/accounting/widget/FinAccountForms.xml` | `FindBankReconciliationFinAcctTrans` |
| form | `applications/accounting/widget/FinAccountForms.xml` | `EditDepositPayment` |
| form | `applications/accounting/widget/FinAccountForms.xml` | `EditWithdrawalPayment` |
| form | `applications/accounting/widget/FinAccountForms.xml` | `EditFinAccountReconciliation` |
| form | `applications/accounting/widget/FinAccountForms.xml` | `FindBankReconciliation` |
| screen | `applications/accounting/widget/FinAccountScreens.xml` | `CommonFinAccountDecorator` |
| screen | `applications/accounting/widget/FinAccountScreens.xml` | `FindFinAccount` |
| screen | `applications/accounting/widget/FinAccountScreens.xml` | `EditFinAccount` |
| screen | `applications/accounting/widget/FinAccountScreens.xml` | `EditFinAccountRoles` |
| screen | `applications/accounting/widget/FinAccountScreens.xml` | `EditFinAccountTrans` |
| screen | `applications/accounting/widget/FinAccountScreens.xml` | `EditFinAccountAuths` |
| screen | `applications/accounting/widget/FinAccountScreens.xml` | `PaymentsDepositWithdraw` |
| screen | `applications/accounting/widget/FinAccountScreens.xml` | `FinAccountMain` |
| screen | `applications/accounting/widget/FinAccountScreens.xml` | `ListBankAccount` |
| screen | `applications/accounting/widget/FinAccountScreens.xml` | `FindDepositSlips` |
| screen | `applications/accounting/widget/FinAccountScreens.xml` | `EditDepositSlipAndMembers` |
| screen | `applications/accounting/widget/FinAccountScreens.xml` | `NewDepositSlip` |
| screen | `applications/accounting/widget/FinAccountScreens.xml` | `FindFinAccountTrans` |
| screen | `applications/accounting/widget/FinAccountScreens.xml` | `BankReconciliation` |
| screen | `applications/accounting/widget/FinAccountScreens.xml` | `FinAccountPortlets` |
| screen | `applications/accounting/widget/FinAccountScreens.xml` | `NewDepositPayment` |
| screen | `applications/accounting/widget/FinAccountScreens.xml` | `NewWithdrawalPayment` |
| screen | `applications/accounting/widget/FinAccountScreens.xml` | `EditFinAccountReconciliations` |
| screen | `applications/accounting/widget/FinAccountScreens.xml` | `ViewGlReconciliationWithTransaction` |
| screen | `applications/accounting/widget/FinAccountScreens.xml` | `FindFinAccountReconciliations` |
| form | `applications/accounting/widget/FixedAssetForms.xml` | `FindFixedAssetOptions` |
| form | `applications/accounting/widget/FixedAssetForms.xml` | `EditFixedAsset` |
| form | `applications/accounting/widget/FixedAssetForms.xml` | `AddFixedAssetProduct` |
| form | `applications/accounting/widget/FixedAssetForms.xml` | `WorkEffortSummary` |
| form | `applications/accounting/widget/FixedAssetForms.xml` | `EditFixedAssetStdCost` |
| form | `applications/accounting/widget/FixedAssetForms.xml` | `AddFixedAssetIdent` |
| form | `applications/accounting/widget/FixedAssetForms.xml` | `AddFixedAssetRegistration` |
| form | `applications/accounting/widget/FixedAssetForms.xml` | `EditFixedAssetMaint` |
| form | `applications/accounting/widget/FixedAssetForms.xml` | `AddFixedAssetMeter` |
| form | `applications/accounting/widget/FixedAssetForms.xml` | `AddFixedAssetMaintOrder` |
| form | `applications/accounting/widget/FixedAssetForms.xml` | `AddPartyFixedAssetAssignment` |
| form | `applications/accounting/widget/FixedAssetForms.xml` | `AddFixedAssetDepMethod` |
| form | `applications/accounting/widget/FixedAssetForms.xml` | `AddFixedAssetTypeGlAccount` |
| screen | `applications/accounting/widget/FixedAssetScreens.xml` | `ListFixedAssets` |
| screen | `applications/accounting/widget/FixedAssetScreens.xml` | `FixedAssetSearchResults` |
| screen | `applications/accounting/widget/FixedAssetScreens.xml` | `CommonFixedAssetDecorator` |
| screen | `applications/accounting/widget/FixedAssetScreens.xml` | `EditFixedAsset` |
| screen | `applications/accounting/widget/FixedAssetScreens.xml` | `ListFixedAssetProducts` |
| screen | `applications/accounting/widget/FixedAssetScreens.xml` | `WorkEffortSummary` |
| screen | `applications/accounting/widget/FixedAssetScreens.xml` | `EditFixedAssetStdCosts` |
| screen | `applications/accounting/widget/FixedAssetScreens.xml` | `EditFixedAssetIdents` |
| screen | `applications/accounting/widget/FixedAssetScreens.xml` | `Calendar` |
| screen | `applications/accounting/widget/FixedAssetScreens.xml` | `EditFixedAssetRegistrations` |
| screen | `applications/accounting/widget/FixedAssetScreens.xml` | `CommonFixedAssetMaintDecorator` |
| screen | `applications/accounting/widget/FixedAssetScreens.xml` | `ListFixedAssetMaints` |
| screen | `applications/accounting/widget/FixedAssetScreens.xml` | `EditFixedAssetMaint` |
| screen | `applications/accounting/widget/FixedAssetScreens.xml` | `EditFixedAssetMeters` |
| screen | `applications/accounting/widget/FixedAssetScreens.xml` | `FixedAssetChildren` |
| screen | `applications/accounting/widget/FixedAssetScreens.xml` | `EditFixedAssetMaintOrders` |
| screen | `applications/accounting/widget/FixedAssetScreens.xml` | `EditPartyFixedAssetAssignments` |
| screen | `applications/accounting/widget/FixedAssetScreens.xml` | `ShowFixedAssetDepreciation` |
| screen | `applications/accounting/widget/FixedAssetScreens.xml` | `FixedAssetGeoLocation` |
| form | `applications/accounting/widget/GlForms.xml` | `FindAcctgTrans` |
| form | `applications/accounting/widget/GlForms.xml` | `FindAcctgTransEntries` |
| form | `applications/accounting/widget/GlForms.xml` | `CreateAcctgTransAndEntries` |
| form | `applications/accounting/widget/GlForms.xml` | `EditAcctgTrans` |
| form | `applications/accounting/widget/GlForms.xml` | `EditAcctgTransEntry` |
| form | `applications/accounting/widget/GlForms.xml` | `CreateAcctgTrans` |
| form | `applications/accounting/widget/GlForms.xml` | `FindGlAccountReconciliation` |
| form | `applications/accounting/widget/GlForms.xml` | `ListGlAccountReconciliation` |
| form | `applications/accounting/widget/GlForms.xml` | `EditGlReconciliation` |
| form | `applications/accounting/widget/GlForms.xml` | `FindGlAccountReconciliations` |
| form | `applications/accounting/widget/GlForms.xml` | `ViewAcctgTrans` |
| form | `applications/accounting/widget/GlForms.xml` | `AcctgTransDetailReportPdf` |
| screen | `applications/accounting/widget/GlScreens.xml` | `CommonPartyGlDecorator` |
| screen | `applications/accounting/widget/GlScreens.xml` | `PartyAccountsSummary` |
| screen | `applications/accounting/widget/GlScreens.xml` | `FindAcctgTrans` |
| screen | `applications/accounting/widget/GlScreens.xml` | `FindAcctgTransEntries` |
| screen | `applications/accounting/widget/GlScreens.xml` | `CreateAcctgTransAndEntries` |
| screen | `applications/accounting/widget/GlScreens.xml` | `EditAcctgTrans` |
| screen | `applications/accounting/widget/GlScreens.xml` | `ListUnpostedAcctgTrans` |
| screen | `applications/accounting/widget/GlScreens.xml` | `ListChecksToPrint` |
| screen | `applications/accounting/widget/GlScreens.xml` | `ListChecksToSend` |
| screen | `applications/accounting/widget/GlScreens.xml` | `NewAcctgTrans` |
| screen | `applications/accounting/widget/GlScreens.xml` | `FindGlAccountReconciliation` |
| screen | `applications/accounting/widget/GlScreens.xml` | `EditGlReconciliation` |
| screen | `applications/accounting/widget/GlScreens.xml` | `FindGlAccountReconciliations` |
| screen | `applications/accounting/widget/GlScreens.xml` | `AcctgTransSearchResultsCsv` |
| screen | `applications/accounting/widget/GlScreens.xml` | `AcctgTransEntriesSearchResultsCsv` |
| screen | `applications/accounting/widget/GlScreens.xml` | `AcctgTransEntriesSearchResultsPdf` |
| screen | `applications/accounting/widget/GlScreens.xml` | `AcctgTransSearchResultPdf` |
| screen | `applications/accounting/widget/GlScreens.xml` | `AcctgTransDetailReportPdf` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `ListCompanies` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `ListGlAccountOrganization` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `AddCompany` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `ExportInvoice` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `ExportInvoiceCsv` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `ImportInvoice` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `AssignGlAccount` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `PartyAcctgPreference` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `ListPartyPrefDocTypeTpl` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `SelectPartyPrefDocTypeTpl` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `AddPartyPrefDocTypeTpl` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `AddPartyPrefInvoiceTypeTpl` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `AddPartyPrefQuoteTypeTpl` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `AddPartyPrefOrderTypeTpl` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `ListConversions` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `UpdateFXConversion` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `ListGlAccountTypeDefaults` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `EditGlAccountTypeDefault` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `ListSalInvoiceItemTypeGlAssignments` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `AddSalInvoiceItemTypeGlAssignment` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `ListPurInvoiceItemTypeGlAssignments` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `AddPurInvoiceItemTypeGlAssignment` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `ListPaymentTypeGlAssignments` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `AddPaymentTypeGlAssignment` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `ListPaymentMethodTypeGlAssignments` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `AddPaymentMethodTypeGlAssignment` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `ListClosedTimePeriods` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `ListOpenTimePeriods` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `CreateTimePeriod` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `EditGlJournal` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `ListGlJournals` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `ListProductGlAccounts` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `AddProductGlAccount` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `ListFinAccountTypeGlAccounts` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `AddFinAccountTypeGlAccount` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `ListProductCategoryGlAccounts` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `AddProductCategoryGlAccount` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `ListVarianceReasonGlAccounts` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `AddVarianceReasonGlAccount` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `ListCreditCardTypeGlAccounts` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `AddCreditCardTypeGlAccount` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `ListTaxAuthorityGlAccounts` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `AddTaxAuthorityGlAccount` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `ListPartyGlAccounts` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `AddPartyGlAccount` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `ListGlAccountOrgCsv` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `FindGlAccountCategory` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `ListGlAccountCategory` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `EditGlAccountCategory` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `ListGlAccountCategoryMember` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `AddGlAccountCategoryMember` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `AddFixedAssetTypeGlAccount` |
| form | `applications/accounting/widget/GlSetupForms.xml` | `FixedAssetTypeGlAccounts` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `CommonGlSetupDecorator` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `ListCompanies` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `AddCompany` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `ImportExport` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `ExportInvoiceCsv` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `ListGlAccountOrganization` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `TimePeriods` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `PartyAcctgPreference` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `ListPartyPrefDocTypeTpl` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `SetupGlJournals` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `GlAccountTypeDefaults` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `GlAccountSalInvoice` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `GlAccountPurInvoice` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `GlAccountTypePaymentType` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `GlAccountNrPaymentMethod` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `EditProductGlAccounts` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `EditFinAccountTypeGlAccounts` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `EditProductCategoryGlAccounts` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `EditVarianceReasonGlAccounts` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `EditCreditCardTypeGlAccounts` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `EditOrganizationTaxAuthorityGlAccounts` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `EditPartyGlAccount` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `FixedAssetTypeGlAccounts` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `ListGlAccountOrgPdf` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `ListGlAccountOrgCsv` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `FindGlAccountCategory` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `EditGlAccountCategory` |
| screen | `applications/accounting/widget/GlSetupScreens.xml` | `EditGlAccountCategoryMember` |
| form | `applications/accounting/widget/GlobalGlAccountsForms.xml` | `ListGlAccountOrganization` |
| form | `applications/accounting/widget/GlobalGlAccountsForms.xml` | `AssignGlAccount` |
| form | `applications/accounting/widget/GlobalGlAccountsForms.xml` | `ListGlAccountPdf` |
| form | `applications/accounting/widget/GlobalGlAccountsForms.xml` | `EditGlAccount` |
| form | `applications/accounting/widget/GlobalGlAccountsForms.xml` | `GlAccountsNavForm` |
| form | `applications/accounting/widget/GlobalGlAccountsForms.xml` | `ListGlReconciliations` |
| form | `applications/accounting/widget/GlobalGlAccountsForms.xml` | `ListGlReconciliationEntries` |
| form | `applications/accounting/widget/GlobalGlAccountsForms.xml` | `UpdateRateAmount` |
| screen | `applications/accounting/widget/GlobalGlAccountsScreens.xml` | `GenericDecorator` |
| screen | `applications/accounting/widget/GlobalGlAccountsScreens.xml` | `CommonAccountDecorator` |
| screen | `applications/accounting/widget/GlobalGlAccountsScreens.xml` | `AssignGlAccount` |
| screen | `applications/accounting/widget/GlobalGlAccountsScreens.xml` | `GlAccountNavigate` |
| screen | `applications/accounting/widget/GlobalGlAccountsScreens.xml` | `ListGlAccountEntries` |
| screen | `applications/accounting/widget/GlobalGlAccountsScreens.xml` | `ListAcctgTransEntries` |
| screen | `applications/accounting/widget/GlobalGlAccountsScreens.xml` | `AddGlAccount` |
| screen | `applications/accounting/widget/GlobalGlAccountsScreens.xml` | `ListGlAccounts` |
| screen | `applications/accounting/widget/GlobalGlAccountsScreens.xml` | `ListGlAccountsReport` |
| screen | `applications/accounting/widget/GlobalGlAccountsScreens.xml` | `GlAccountDetail` |
| screen | `applications/accounting/widget/GlobalGlAccountsScreens.xml` | `EditGlobalGlAccount` |
| screen | `applications/accounting/widget/GlobalGlAccountsScreens.xml` | `ViewRateAmounts` |
| screen | `applications/accounting/widget/GlobalGlAccountsScreens.xml` | `ViewFXConversions` |
| screen | `applications/accounting/widget/GlobalGlAccountsScreens.xml` | `CostCenters` |
| form | `applications/accounting/widget/InvoiceForms.xml` | `FindInvoices` |
| form | `applications/accounting/widget/InvoiceForms.xml` | `InvoiceHeader` |
| form | `applications/accounting/widget/InvoiceForms.xml` | `NewSalesInvoice` |
| form | `applications/accounting/widget/InvoiceForms.xml` | `NewPurchaseInvoice` |
| form | `applications/accounting/widget/InvoiceForms.xml` | `EditInvoice` |
| form | `applications/accounting/widget/InvoiceForms.xml` | `EditInvoiceItems` |
| form | `applications/accounting/widget/InvoiceForms.xml` | `EditInvoiceItem` |
| form | `applications/accounting/widget/InvoiceForms.xml` | `EditInvoiceTerm` |
| form | `applications/accounting/widget/InvoiceForms.xml` | `AddPayment` |
| form | `applications/accounting/widget/InvoiceForms.xml` | `EditInvoiceRole` |
| form | `applications/accounting/widget/InvoiceForms.xml` | `SendPerEmail` |
| form | `applications/accounting/widget/InvoiceForms.xml` | `LookupInvoicesStatus` |
| form | `applications/accounting/widget/InvoiceItemTypeForms.xml` | `ListInvoiceItemType` |
| screen | `applications/accounting/widget/InvoiceItemTypeScreens.xml` | `EditInvoiceItemType` |
| screen | `applications/accounting/widget/InvoiceScreens.xml` | `CommonInvoiceDecorator` |
| screen | `applications/accounting/widget/InvoiceScreens.xml` | `FindInvoices` |
| screen | `applications/accounting/widget/InvoiceScreens.xml` | `NewInvoice` |
| screen | `applications/accounting/widget/InvoiceScreens.xml` | `EditInvoice` |
| screen | `applications/accounting/widget/InvoiceScreens.xml` | `ViewInvoice` |
| screen | `applications/accounting/widget/InvoiceScreens.xml` | `InvoiceStatus` |
| screen | `applications/accounting/widget/InvoiceScreens.xml` | `EditInvoiceApplications` |
| screen | `applications/accounting/widget/InvoiceScreens.xml` | `EditInvoiceItems` |
| screen | `applications/accounting/widget/InvoiceScreens.xml` | `EditInvoiceTimeEntries` |
| screen | `applications/accounting/widget/InvoiceScreens.xml` | `InvoiceAttributes` |
| screen | `applications/accounting/widget/InvoiceScreens.xml` | `InvoiceContactMech` |
| screen | `applications/accounting/widget/InvoiceScreens.xml` | `InvoiceContent` |
| screen | `applications/accounting/widget/InvoiceScreens.xml` | `InvoiceNotes` |
| screen | `applications/accounting/widget/InvoiceScreens.xml` | `InvoiceRoles` |
| screen | `applications/accounting/widget/InvoiceScreens.xml` | `InvoiceTerms` |
| screen | `applications/accounting/widget/InvoiceScreens.xml` | `SendPerEmail` |
| screen | `applications/accounting/widget/InvoiceScreens.xml` | `sendPerEmailBody` |
| screen | `applications/accounting/widget/InvoiceScreens.xml` | `ListCustomerInvoices` |
| screen | `applications/accounting/widget/InvoiceScreens.xml` | `ListSupplierInvoices` |
| screen | `applications/accounting/widget/InvoiceScreens.xml` | `InvoicesArPastDue` |
| screen | `applications/accounting/widget/InvoiceScreens.xml` | `InvoicesArDueSoon` |
| screen | `applications/accounting/widget/InvoiceScreens.xml` | `InvoicesApPastDue` |
| screen | `applications/accounting/widget/InvoiceScreens.xml` | `InvoicesApDueSoon` |
| screen | `applications/accounting/widget/LookupScreens.xml` | `LookupPayment` |
| screen | `applications/accounting/widget/LookupScreens.xml` | `LookupInvoice` |
| screen | `applications/accounting/widget/LookupScreens.xml` | `LookupFixedAsset` |
| screen | `applications/accounting/widget/LookupScreens.xml` | `LookupBillingAccount` |
| screen | `applications/accounting/widget/LookupScreens.xml` | `LookupGlAccount` |
| screen | `applications/accounting/widget/LookupScreens.xml` | `LookupAgreement` |
| screen | `applications/accounting/widget/LookupScreens.xml` | `LookupAgreementItem` |
| screen | `applications/accounting/widget/LookupScreens.xml` | `LookupPaymentGroupMember` |
| screen | `applications/accounting/widget/LookupScreens.xml` | `LookupGlReconciliation` |
| screen | `applications/accounting/widget/LookupScreens.xml` | `LookupCustomTimePeriod` |
| form | `applications/accounting/widget/PaymentForms.xml` | `FindPayments` |
| form | `applications/accounting/widget/PaymentForms.xml` | `EditPaymentAttributes` |
| form | `applications/accounting/widget/PaymentForms.xml` | `NewPaymentOut` |
| form | `applications/accounting/widget/PaymentForms.xml` | `NewPaymentIn` |
| form | `applications/accounting/widget/PaymentForms.xml` | `EditPayment` |
| form | `applications/accounting/widget/PaymentForms.xml` | `ListInvoicesNotAppliedOtherCurrency` |
| form | `applications/accounting/widget/PaymentForms.xml` | `AddPaymentApplication` |
| form | `applications/accounting/widget/PaymentForms.xml` | `PaymentHeader` |
| form | `applications/accounting/widget/PaymentForms.xml` | `AcctgTransAndEntries` |
| form | `applications/accounting/widget/PaymentForms.xml` | `ListChecksToPrint` |
| form | `applications/accounting/widget/PaymentForms.xml` | `ListChecksToSend` |
| form | `applications/accounting/widget/PaymentForms.xml` | `FindSalesInvoicesByDueDate` |
| form | `applications/accounting/widget/PaymentForms.xml` | `FindPurchaseInvoicesByDueDate` |
| form | `applications/accounting/widget/PaymentForms.xml` | `FinAccountTransAssociatedWithPayment` |
| form | `applications/accounting/widget/PaymentGatewayConfigForms.xml` | `FindPaymentGatewayConfig` |
| form | `applications/accounting/widget/PaymentGatewayConfigForms.xml` | `ListPaymentGatewayConfig` |
| form | `applications/accounting/widget/PaymentGatewayConfigForms.xml` | `EditPaymentGatewayConfig` |
| form | `applications/accounting/widget/PaymentGatewayConfigForms.xml` | `FindPaymentGatewayConfigTypes` |
| form | `applications/accounting/widget/PaymentGatewayConfigForms.xml` | `ListPaymentGatewayConfigTypes` |
| form | `applications/accounting/widget/PaymentGatewayConfigForms.xml` | `EditPaymentGatewayConfigType` |
| screen | `applications/accounting/widget/PaymentGatewayConfigScreens.xml` | `GenericPaymentGatewayConfigDecorator` |
| screen | `applications/accounting/widget/PaymentGatewayConfigScreens.xml` | `FindPaymentGatewayConfig` |
| screen | `applications/accounting/widget/PaymentGatewayConfigScreens.xml` | `EditPaymentGatewayConfig` |
| screen | `applications/accounting/widget/PaymentGatewayConfigScreens.xml` | `FindPaymentGatewayConfigTypes` |
| screen | `applications/accounting/widget/PaymentGatewayConfigScreens.xml` | `EditPaymentGatewayConfigType` |
| form | `applications/accounting/widget/PaymentGroupForms.xml` | `FindPaymentGroup` |
| form | `applications/accounting/widget/PaymentGroupForms.xml` | `ListPaymentGroup` |
| form | `applications/accounting/widget/PaymentGroupForms.xml` | `EditPaymentGroup` |
| form | `applications/accounting/widget/PaymentGroupForms.xml` | `ListPaymentGroupMember` |
| form | `applications/accounting/widget/PaymentGroupForms.xml` | `AddPaymentGroupMember` |
| form | `applications/accounting/widget/PaymentGroupForms.xml` | `PaymentGroupMembers` |
| screen | `applications/accounting/widget/PaymentGroupScreens.xml` | `FindPaymentGroup` |
| screen | `applications/accounting/widget/PaymentGroupScreens.xml` | `EditPaymentGroup` |
| screen | `applications/accounting/widget/PaymentGroupScreens.xml` | `EditPaymentGroupMember` |
| screen | `applications/accounting/widget/PaymentGroupScreens.xml` | `PaymentGroupOverview` |
| screen | `applications/accounting/widget/PaymentGroupScreens.xml` | `DepositSlipPdf` |
| form | `applications/accounting/widget/PaymentMethodTypeForms.xml` | `ListPaymentMethodType` |
| screen | `applications/accounting/widget/PaymentMethodTypeScreens.xml` | `EditPaymentMethodType` |
| screen | `applications/accounting/widget/PaymentScreens.xml` | `CommonPaymentDecorator` |
| screen | `applications/accounting/widget/PaymentScreens.xml` | `FindPayments` |
| screen | `applications/accounting/widget/PaymentScreens.xml` | `ListPayments` |
| screen | `applications/accounting/widget/PaymentScreens.xml` | `NewPayment` |
| screen | `applications/accounting/widget/PaymentScreens.xml` | `EditPayment` |
| screen | `applications/accounting/widget/PaymentScreens.xml` | `EditPaymentApplications` |
| screen | `applications/accounting/widget/PaymentScreens.xml` | `PaymentOverview` |
| screen | `applications/accounting/widget/PaymentScreens.xml` | `PaymentAttributes` |
| screen | `applications/accounting/widget/PaymentScreens.xml` | `ManualTransaction` |
| screen | `applications/accounting/widget/PaymentScreens.xml` | `manualCCTx` |
| screen | `applications/accounting/widget/PaymentScreens.xml` | `manualGCTx` |
| screen | `applications/accounting/widget/PaymentScreens.xml` | `PrintChecks` |
| screen | `applications/accounting/widget/PaymentScreens.xml` | `InvoiceAcctgTransEntry` |
| screen | `applications/accounting/widget/PaymentScreens.xml` | `FindSalesInvoicesByDueDate` |
| screen | `applications/accounting/widget/PaymentScreens.xml` | `FindPurchaseInvoicesByDueDate` |
| screen | `applications/accounting/widget/PeriodScreens.xml` | `EditCustomTimePeriod` |
| form | `applications/accounting/widget/ReportFinancialSummaryForms.xml` | `BaseSummaryOptions` |
| form | `applications/accounting/widget/ReportFinancialSummaryForms.xml` | `SalesInvoiceByProductCategorySummaryOptions` |
| form | `applications/accounting/widget/ReportFinancialSummaryForms.xml` | `SalesInvoiceByProductGlAccountSummaryOptions` |
| form | `applications/accounting/widget/ReportFinancialSummaryForms.xml` | `PaymentByMethodSummaryOptions` |
| form | `applications/accounting/widget/ReportFinancialSummaryForms.xml` | `InventoryIssueSummaryOptions` |
| form | `applications/accounting/widget/ReportFinancialSummaryForms.xml` | `FinancialAccountSummaryOptions` |
| form | `applications/accounting/widget/ReportFinancialSummaryForms.xml` | `TransactionSelectionForm` |
| form | `applications/accounting/widget/ReportFinancialSummaryForms.xml` | `ComparativeIncomeStatementParameters` |
| form | `applications/accounting/widget/ReportFinancialSummaryForms.xml` | `ComparativeIncomeStatementParametersOneColumn` |
| form | `applications/accounting/widget/ReportFinancialSummaryForms.xml` | `BalanceSheetParameters` |
| form | `applications/accounting/widget/ReportFinancialSummaryForms.xml` | `ComparativeBalanceSheetParameters` |
| form | `applications/accounting/widget/ReportFinancialSummaryForms.xml` | `ComparativeBalanceSheetParametersOneColumn` |
| form | `applications/accounting/widget/ReportFinancialSummaryForms.xml` | `SelectAcctReportPeriod` |
| form | `applications/accounting/widget/ReportFinancialSummaryForms.xml` | `FindTransactionTotals` |
| form | `applications/accounting/widget/ReportFinancialSummaryForms.xml` | `IncomeStatementListCsv` |
| form | `applications/accounting/widget/ReportFinancialSummaryForms.xml` | `ExpenseStatementListCsv` |
| form | `applications/accounting/widget/ReportFinancialSummaryForms.xml` | `BalanceSheetAssetListCsv` |
| form | `applications/accounting/widget/ReportFinancialSummaryForms.xml` | `BalanceSheetLiabilityListCsv` |
| form | `applications/accounting/widget/ReportFinancialSummaryForms.xml` | `BalanceSheetEquityListCsv` |
| form | `applications/accounting/widget/ReportFinancialSummaryForms.xml` | `GlAccountTrialBalance` |
| form | `applications/accounting/widget/ReportFinancialSummaryForms.xml` | `InventoryValuation` |
| form | `applications/accounting/widget/ReportFinancialSummaryForms.xml` | `TrialBalanceFinancialTimePeriodSelection` |
| form | `applications/accounting/widget/ReportFinancialSummaryForms.xml` | `CashFlowStatementParameters` |
| form | `applications/accounting/widget/ReportFinancialSummaryForms.xml` | `FindCashFlowTotals` |
| form | `applications/accounting/widget/ReportFinancialSummaryForms.xml` | `ComparativeCashFlowStatementParameters` |
| form | `applications/accounting/widget/ReportFinancialSummaryForms.xml` | `ComparativeCashFlowStatementParametersOneColumn` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `FinancialSummaryReportOptions` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `FinancialSummaryDataPrep` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `SalesInvoiceByProductCategorySummary` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `TrialBalance` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `TrialBalanceSearchResultsCsv` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `TrialBalanceSearchResultsPdf` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `BalanceSheet` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `BalanceSheetPdf` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `BalanceSheetCsv` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `ComparativeBalanceSheet` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `ComparativeBalanceSheetPdf` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `ComparativeBalanceSheetCsv` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `TransactionTotals` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `TransactionTotalsPdf` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `TransactionTotalsCsv` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `IncomeStatement` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `IncomeStatementListPdf` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `IncomeStatementListCsv` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `ComparativeIncomeStatement` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `ComparativeIncomeStatementsPdf` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `ComparativeIncomeStatementsCsv` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `GlAccountTrialBalance` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `GlAccountTrialBalanceReportPdf` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `GlAccountBalanceByCostCenter` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `GlAccountBalanceByCostCenterPdf` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `InventoryValuation` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `InventoryValuationPdf` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `InventoryValuationCsv` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `CashFlowStatement` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `CashFlowStatementListPdf` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `CashFlowStatementListCsv` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `ComparativeCashFlowStatement` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `ComparativeCashFlowStatementPdf` |
| screen | `applications/accounting/widget/ReportFinancialSummaryScreens.xml` | `ComparativeCashFlowStatementCsv` |
| form | `applications/accounting/widget/TaxAuthorityForms.xml` | `EditTaxAuthority` |
| form | `applications/accounting/widget/TaxAuthorityForms.xml` | `AddTaxAuthorityCategory` |
| form | `applications/accounting/widget/TaxAuthorityForms.xml` | `AddTaxAuthorityAssoc` |
| form | `applications/accounting/widget/TaxAuthorityForms.xml` | `AddTaxAuthorityGlAccount` |
| form | `applications/accounting/widget/TaxAuthorityForms.xml` | `AddTaxAuthorityRateProduct` |
| form | `applications/accounting/widget/TaxAuthorityForms.xml` | `FindTaxAuthorityParties` |
| form | `applications/accounting/widget/TaxAuthorityForms.xml` | `EditTaxAuthorityPartyInfo` |
| form | `applications/accounting/widget/TaxAuthorityForms.xml` | `FindTaxAuthorityReport` |
| screen | `applications/accounting/widget/TaxAuthorityScreens.xml` | `CommonTaxAuthorityDecorator` |
| screen | `applications/accounting/widget/TaxAuthorityScreens.xml` | `FindTaxAuthority` |
| screen | `applications/accounting/widget/TaxAuthorityScreens.xml` | `EditTaxAuthority` |
| screen | `applications/accounting/widget/TaxAuthorityScreens.xml` | `EditTaxAuthorityCategories` |
| screen | `applications/accounting/widget/TaxAuthorityScreens.xml` | `EditTaxAuthorityAssocs` |
| screen | `applications/accounting/widget/TaxAuthorityScreens.xml` | `EditTaxAuthorityGlAccounts` |
| screen | `applications/accounting/widget/TaxAuthorityScreens.xml` | `EditTaxAuthorityRateProducts` |
| screen | `applications/accounting/widget/TaxAuthorityScreens.xml` | `ListTaxAuthorityParties` |
| screen | `applications/accounting/widget/TaxAuthorityScreens.xml` | `EditTaxAuthorityPartyInfo` |
| screen | `applications/accounting/widget/TaxAuthorityScreens.xml` | `FindTaxAuthorityReport` |
| form | `applications/accounting/widget/TransactionForms.xml` | `FindGatewayResponses` |
| form | `applications/accounting/widget/TransactionForms.xml` | `ViewGatewayResponseRelations` |
| form | `applications/accounting/widget/TransactionForms.xml` | `ViewGatewayResponse` |
| form | `applications/accounting/widget/TransactionForms.xml` | `AuthorizeTransaction` |
| form | `applications/accounting/widget/TransactionForms.xml` | `CaptureTransaction` |
| screen | `applications/accounting/widget/TransactionScreens.xml` | `GenericTransactionDecorator` |
| screen | `applications/accounting/widget/TransactionScreens.xml` | `FindGatewayResponses` |
| screen | `applications/accounting/widget/TransactionScreens.xml` | `ViewGatewayResponse` |
| screen | `applications/accounting/widget/TransactionScreens.xml` | `AuthorizeTransaction` |
| screen | `applications/accounting/widget/TransactionScreens.xml` | `CaptureTransaction` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceApplicationCpd.xml` | `InvoiceApplications` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceApplicationCpd.xml` | `EditInvoiceApplications` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceApplicationCpd.xml` | `EditPayment` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceApplicationCpd.xml` | `PaymentApplications` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceApplicationCpd.xml` | `ViewInvoiceApplications` |
| menu | `applications/accounting/widget/accounting/AccountingInvoiceApplicationCpd.xml` | `InvoiceApplicationsTabBar` |
| menu | `applications/accounting/widget/accounting/AccountingInvoiceApplicationCpd.xml` | `EditInvoiceApplicationsInLineBar` |
| form | `applications/accounting/widget/accounting/AccountingInvoiceApplicationCpd.xml` | `EditPayment` |
| form | `applications/accounting/widget/accounting/AccountingInvoiceApplicationCpd.xml` | `EditInvoiceApplications` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceAttributeCpd.xml` | `InvoiceAttributes` |
| form | `applications/accounting/widget/accounting/AccountingInvoiceAttributeCpd.xml` | `AddInvoiceAttribute` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceContactMechCpd.xml` | `InvoiceContactMechs` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceContentCpd.xml` | `InvoiceContents` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceCpd.xml` | `FindInvoices` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceCpd.xml` | `ListInvoices` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceCpd.xml` | `NewInvoice` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceCpd.xml` | `ViewInvoice` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceCpd.xml` | `EditInvoice` |
| menu | `applications/accounting/widget/accounting/AccountingInvoiceCpd.xml` | `FindInvoicesMenuBar` |
| form | `applications/accounting/widget/accounting/AccountingInvoiceCpd.xml` | `FindInvoices` |
| form | `applications/accounting/widget/accounting/AccountingInvoiceCpd.xml` | `NewSalesInvoice` |
| form | `applications/accounting/widget/accounting/AccountingInvoiceCpd.xml` | `NewPurchaseInvoice` |
| form | `applications/accounting/widget/accounting/AccountingInvoiceCpd.xml` | `InvoiceHeader` |
| form | `applications/accounting/widget/accounting/AccountingInvoiceCpd.xml` | `EditInvoice` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceItemCpd.xml` | `InvoiceItems` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceItemCpd.xml` | `AddInvoiceItem` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceItemCpd.xml` | `EditInvoiceItem` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceItemCpd.xml` | `InvoiceItem` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceItemCpd.xml` | `InvoiceItemsPayrol` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceItemCpd.xml` | `ListInvoiceItems` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceItemCpd.xml` | `EditInvoiceItems` |
| menu | `applications/accounting/widget/accounting/AccountingInvoiceItemCpd.xml` | `InvoiceItemsTabBar` |
| menu | `applications/accounting/widget/accounting/AccountingInvoiceItemCpd.xml` | `ListInvoiceItemsInLineBar` |
| form | `applications/accounting/widget/accounting/AccountingInvoiceItemCpd.xml` | `EditInvoiceItem` |
| form | `applications/accounting/widget/accounting/AccountingInvoiceItemCpd.xml` | `EditInvoiceItems` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceNoteCpd.xml` | `InvoiceNotes` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceRoleCpd.xml` | `InvoiceRoles` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceRoleCpd.xml` | `EditInvoiceRole` |
| menu | `applications/accounting/widget/accounting/AccountingInvoiceRoleCpd.xml` | `InvoiceRolesTabBar` |
| form | `applications/accounting/widget/accounting/AccountingInvoiceRoleCpd.xml` | `EditInvoiceRole` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceStatusCpd.xml` | `InvoiceStatus` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceTermCpd.xml` | `InvoiceTerms` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceTermCpd.xml` | `EditInvoiceTerm` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceTermCpd.xml` | `EditInvoiceTerms` |
| menu | `applications/accounting/widget/accounting/AccountingInvoiceTermCpd.xml` | `InvoiceTermsTabBar` |
| menu | `applications/accounting/widget/accounting/AccountingInvoiceTermCpd.xml` | `ListInvoiceTermsInLineBar` |
| form | `applications/accounting/widget/accounting/AccountingInvoiceTermCpd.xml` | `EditInvoiceTerm` |
| form | `applications/accounting/widget/accounting/AccountingInvoiceTermCpd.xml` | `EditInvoiceTerms` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceTimeEntryCpd.xml` | `InvoiceTimeEntries` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceTimeEntryCpd.xml` | `ListInvoiceTimeEntries` |
| screen | `applications/accounting/widget/accounting/AccountingInvoiceTimeEntryCpd.xml` | `ViewInvoiceTimeEntries` |
| menu | `applications/accounting/widget/accounting/AccountingInvoiceTimeEntryCpd.xml` | `ListInvoiceTimeEntriesInLineBar` |
| menu | `applications/accounting/widget/ap/ApMenus.xml` | `ApAppBar` |
| menu | `applications/accounting/widget/ap/ApMenus.xml` | `ApShortcutAppBar` |
| menu | `applications/accounting/widget/ap/ApMenus.xml` | `ApInvoiceTabBar` |
| menu | `applications/accounting/widget/ap/ApMenus.xml` | `ApReportTabBar` |
| screen | `applications/accounting/widget/ap/ApPrintScreens.xml` | `CommissionReportPdf` |
| screen | `applications/accounting/widget/ap/ApScreens.xml` | `FindApPayments` |
| screen | `applications/accounting/widget/ap/ApScreens.xml` | `NewOutgoingPayment` |
| screen | `applications/accounting/widget/ap/ApScreens.xml` | `FindApPaymentGroups` |
| screen | `applications/accounting/widget/ap/CommonScreens.xml` | `main-decorator` |
| screen | `applications/accounting/widget/ap/CommonScreens.xml` | `ShortcutApp` |
| screen | `applications/accounting/widget/ap/CommonScreens.xml` | `main` |
| screen | `applications/accounting/widget/ap/CommonScreens.xml` | `ApPastDueInvoices` |
| screen | `applications/accounting/widget/ap/CommonScreens.xml` | `ApInvoicesDueSoon` |
| screen | `applications/accounting/widget/ap/CommonScreens.xml` | `EditVendor` |
| screen | `applications/accounting/widget/ap/CommonScreens.xml` | `FindVendors` |
| screen | `applications/accounting/widget/ap/CommonScreens.xml` | `CommonInvoiceDecorator` |
| screen | `applications/accounting/widget/ap/CommonScreens.xml` | `CommonPaymentDecorator` |
| screen | `applications/accounting/widget/ap/CommonScreens.xml` | `CommonPaymentGroupDecorator` |
| screen | `applications/accounting/widget/ap/CommonScreens.xml` | `CommonApPaymentDecorator` |
| screen | `applications/accounting/widget/ap/CommonScreens.xml` | `CommonApReportDecorator` |
| screen | `applications/accounting/widget/ap/InvoiceScreens.xml` | `ListReports` |
| screen | `applications/accounting/widget/ap/InvoiceScreens.xml` | `FindApInvoices` |
| screen | `applications/accounting/widget/ap/InvoiceScreens.xml` | `FindPurchaseInvoices` |
| screen | `applications/accounting/widget/ap/InvoiceScreens.xml` | `NewPurchaseInvoice` |
| screen | `applications/accounting/widget/ap/InvoiceScreens.xml` | `CommissionRun` |
| screen | `applications/accounting/widget/ap/InvoiceScreens.xml` | `CommissionReport` |
| screen | `applications/accounting/widget/ap/LookupScreens.xml` | `LookupInvoice` |
| screen | `applications/accounting/widget/ap/LookupScreens.xml` | `LookupPayment` |
| form | `applications/accounting/widget/ap/forms/InvoiceForms.xml` | `ListReports` |
| form | `applications/accounting/widget/ap/forms/InvoiceForms.xml` | `FindApInvoices` |
| form | `applications/accounting/widget/ap/forms/InvoiceForms.xml` | `FindPurchaseInvoices` |
| form | `applications/accounting/widget/ap/forms/InvoiceForms.xml` | `CommissionRun` |
| form | `applications/accounting/widget/ap/forms/InvoiceForms.xml` | `CommissionReport` |
| form | `applications/accounting/widget/ap/forms/LookupForms.xml` | `LookupInvoice` |
| form | `applications/accounting/widget/ap/forms/LookupForms.xml` | `LookupPayment` |
| form | `applications/accounting/widget/ap/forms/VendorForms.xml` | `ListVendors` |
| form | `applications/accounting/widget/ap/forms/VendorForms.xml` | `EditVendor` |
| form | `applications/accounting/widget/ap/forms/VendorForms.xml` | `FindVendors` |
| form | `applications/accounting/widget/ap/forms/VendorForms.xml` | `FindApPayments` |
| form | `applications/accounting/widget/ap/forms/VendorForms.xml` | `FindApPaymentGroups` |
| menu | `applications/accounting/widget/ar/ArMenus.xml` | `ArAppBar` |
| menu | `applications/accounting/widget/ar/ArMenus.xml` | `ArShortcutAppBar` |
| menu | `applications/accounting/widget/ar/ArMenus.xml` | `ArPaymentTabBar` |
| screen | `applications/accounting/widget/ar/ArPaymentScreens.xml` | `FindPayments` |
| screen | `applications/accounting/widget/ar/ArPaymentScreens.xml` | `BatchPayments` |
| screen | `applications/accounting/widget/ar/ArPaymentScreens.xml` | `NewIncomingPayment` |
| screen | `applications/accounting/widget/ar/ArPaymentScreens.xml` | `FindArPaymentGroups` |
| screen | `applications/accounting/widget/ar/CommonScreens.xml` | `main-decorator` |
| screen | `applications/accounting/widget/ar/CommonScreens.xml` | `ShortcutApp` |
| screen | `applications/accounting/widget/ar/CommonScreens.xml` | `main` |
| screen | `applications/accounting/widget/ar/CommonScreens.xml` | `ArPastDueInvoices` |
| screen | `applications/accounting/widget/ar/CommonScreens.xml` | `ArInvoicesDueSoon` |
| screen | `applications/accounting/widget/ar/CommonScreens.xml` | `CommonPaymentDecorator` |
| screen | `applications/accounting/widget/ar/CommonScreens.xml` | `CommonArPaymentDecorator` |
| screen | `applications/accounting/widget/ar/CommonScreens.xml` | `CommonPaymentGroupDecorator` |
| screen | `applications/accounting/widget/ar/CommonScreens.xml` | `CommonInvoiceDecorator` |
| screen | `applications/accounting/widget/ar/InvoiceScreens.xml` | `ListReports` |
| screen | `applications/accounting/widget/ar/InvoiceScreens.xml` | `FindArInvoices` |
| screen | `applications/accounting/widget/ar/InvoiceScreens.xml` | `NewSalesInvoice` |
| screen | `applications/accounting/widget/ar/LookupScreens.xml` | `LookupInvoice` |
| screen | `applications/accounting/widget/ar/LookupScreens.xml` | `LookupPayment` |
| form | `applications/accounting/widget/ar/forms/ArPaymentForms.xml` | `FindBatchPayments` |
| form | `applications/accounting/widget/ar/forms/ArPaymentForms.xml` | `FindBatchPaymentsForDepositSlip` |
| form | `applications/accounting/widget/ar/forms/ArPaymentForms.xml` | `FindArPayments` |
| form | `applications/accounting/widget/ar/forms/ArPaymentForms.xml` | `FindArPaymentGroups` |
| form | `applications/accounting/widget/ar/forms/InvoiceForms.xml` | `ListReports` |
| form | `applications/accounting/widget/ar/forms/InvoiceForms.xml` | `FindArInvoices` |
| form | `applications/accounting/widget/ar/forms/LookupForms.xml` | `LookupInvoice` |
| form | `applications/accounting/widget/ar/forms/LookupForms.xml` | `LookupPayment` |

## FreeMarker templates

- `CopyAgreement.ftl` — `applications/accounting/template/agreement/CopyAgreement.ftl`
- `CommissionReport.ftl` — `applications/accounting/template/ap/invoices/CommissionReport.ftl`
- `CommissionRun.ftl` — `applications/accounting/template/ap/invoices/CommissionRun.ftl`
- `PurchaseInvoices.ftl` — `applications/accounting/template/ap/invoices/PurchaseInvoices.ftl`
- `CommissionReport.fo.ftl` — `applications/accounting/template/ap/reports/CommissionReport.fo.ftl`
- `ListInvoices.ftl` — `applications/accounting/template/ar/invoice/ListInvoices.ftl`
- `BatchPayments.ftl` — `applications/accounting/template/ar/payment/BatchPayments.ftl`
- `CreditCardFields.ftl` — `applications/accounting/template/common/CreditCardFields.ftl`
- `FinAccountTrans.ftl` — `applications/accounting/template/finaccount/FinAccountTrans.ftl`
- `FinAccountTransTotals.ftl` — `applications/accounting/template/finaccount/FinAccountTransTotals.ftl`
- `Mutations.ftl` — `applications/accounting/template/finaccount/Mutations.ftl`
- `FinAccountTrans.ftl` — `applications/accounting/template/finaccounttrans/FinAccountTrans.ftl`
- `GlReconciledFinAccountTrans.ftl` — `applications/accounting/template/finaccounttrans/GlReconciledFinAccountTrans.ftl`
- `ShowGlTransactions.ftl` — `applications/accounting/template/finaccounttrans/ShowGlTransactions.ftl`
- `CommissionRunSearchResult.ftl` — `applications/accounting/template/invoice/CommissionRunSearchResult.ftl`
- `InvoiceItemsPayrol.ftl` — `applications/accounting/template/invoice/InvoiceItemsPayrol.ftl`
- `InvoiceReportContactMechs.fo.ftl` — `applications/accounting/template/invoice/InvoiceReportContactMechs.fo.ftl`
- `InvoiceReportHeaderInfo.fo.ftl` — `applications/accounting/template/invoice/InvoiceReportHeaderInfo.fo.ftl`
- `InvoiceReportItems.fo.ftl` — `applications/accounting/template/invoice/InvoiceReportItems.fo.ftl`
- `NoAccountingView.fo.ftl` — `applications/accounting/template/invoice/NoAccountingView.fo.ftl`
- `PrintInvoices.fo.ftl` — `applications/accounting/template/invoice/PrintInvoices.fo.ftl`
- `CostCenters.ftl` — `applications/accounting/template/ledger/CostCenters.ftl`
- `DepositWithdrawPayments.ftl` — `applications/accounting/template/payment/DepositWithdrawPayments.ftl`
- `ManualCCTx.ftl` — `applications/accounting/template/payment/ManualCCTx.ftl`
- `ManualTx.ftl` — `applications/accounting/template/payment/ManualTx.ftl`
- `PrintChecks.fo.ftl` — `applications/accounting/template/payment/PrintChecks.fo.ftl`
- `EditCustomTimePeriod.ftl` — `applications/accounting/template/period/EditCustomTimePeriod.ftl`
- `AcctgTransEntriesSearchResult.fo.ftl` — `applications/accounting/template/reports/AcctgTransEntriesSearchResult.fo.ftl`
- `AcctgTransSearchResult.fo.ftl` — `applications/accounting/template/reports/AcctgTransSearchResult.fo.ftl`
- `ChartOfAccount.fo.ftl` — `applications/accounting/template/reports/ChartOfAccount.fo.ftl`
- `CostCentersReport.fo.ftl` — `applications/accounting/template/reports/CostCentersReport.fo.ftl`
- `CostCentersReport.ftl` — `applications/accounting/template/reports/CostCentersReport.ftl`
- `DepositSlip.fo.ftl` — `applications/accounting/template/reports/DepositSlip.fo.ftl`
- `GlAccountTrialBalanceReport.fo.ftl` — `applications/accounting/template/reports/GlAccountTrialBalanceReport.fo.ftl`
- `GlAccountTrialBalanceReport.ftl` — `applications/accounting/template/reports/GlAccountTrialBalanceReport.ftl`
- `InvoiceAcctgTransEntry.fo.ftl` — `applications/accounting/template/reports/InvoiceAcctgTransEntry.fo.ftl`
- `SalesInvoiceByProductCategorySummary.ftl` — `applications/accounting/template/reports/SalesInvoiceByProductCategorySummary.ftl`

## Groovy scripts

- `AcctgAdminServices.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/admin/AcctgAdminServices.groovy`
- `FilterOutReceipts.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/admin/FilterOutReceipts.groovy`
- `ListInvoiceItemTypesGlAccount.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/admin/ListInvoiceItemTypesGlAccount.groovy`
- `AgreementServicesScript.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/agreement/AgreementServicesScript.groovy`
- `GetPartyNameForDate.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/agreement/GetPartyNameForDate.groovy`
- `CommissionReport.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/ap/invoices/CommissionReport.groovy`
- `CommissionRun.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/ap/invoices/CommissionRun.groovy`
- `BatchPayments.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/ar/BatchPayments.groovy`
- `BudgetServices.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/budget/BudgetServices.groovy`
- `TaxAuthorityGlAccounts.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/chartofaccounts/TaxAuthorityGlAccounts.groovy`
- `FixedAssetGeoLocation.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/fixedasset/FixedAssetGeoLocation.groovy`
- `FixedAssetServices.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/fixedasset/FixedAssetServices.groovy`
- `CreateApplicationList.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/CreateApplicationList.groovy`
- `DisplayInvoiceAmounts.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/DisplayInvoiceAmounts.groovy`
- `EditInvoice.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/EditInvoice.groovy`
- `GetAccountOrganizationAndClass.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/GetAccountOrganizationAndClass.groovy`
- `InvoiceEvents.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceEvents.groovy`
- `InvoiceReport.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceReport.groovy`
- `InvoiceServicesScript.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy`
- `ListNotAppliedPayments.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/ListNotAppliedPayments.groovy`
- `PrintInvoices.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/PrintInvoices.groovy`
- `SampleServices.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/SampleServices.groovy`
- `GeneralLedgerServicesScript.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/ledger/GeneralLedgerServicesScript.groovy`
- `PartyPreferenceTooltipLabels.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/ledger/PartyPreferenceTooltipLabels.groovy`
- `BillingAccountOrders.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/order/BillingAccountOrders.groovy`
- `BillingAccounts.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/BillingAccounts.groovy`
- `DepositWithdrawPayments.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/DepositWithdrawPayments.groovy`
- `FindInvoicesByDueDate.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/FindInvoicesByDueDate.groovy`
- `ListNotAppliedInvoices.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/ListNotAppliedInvoices.groovy`
- `ListNotAppliedPayments.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/ListNotAppliedPayments.groovy`
- `ManualTx.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/ManualTx.groovy`
- `PaymentServices.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy`
- `PrintChecks.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PrintChecks.groovy`
- `EditCustomTimePeriod.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/period/EditCustomTimePeriod.groovy`
- `GPeriodServices.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/period/GPeriodServices.groovy`
- `RateServices.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/rate/RateServices.groovy`
- `BalanceSheet.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/reports/BalanceSheet.groovy`
- `CashFlowStatement.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/reports/CashFlowStatement.groovy`
- `ComparativeBalanceSheet.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/reports/ComparativeBalanceSheet.groovy`
- `ComparativeCashFlowStatement.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/reports/ComparativeCashFlowStatement.groovy`
- `ComparativeIncomeStatement.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/reports/ComparativeIncomeStatement.groovy`
- `CostCenters.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/reports/CostCenters.groovy`
- `GlAccountTrialBalance.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/reports/GlAccountTrialBalance.groovy`
- `IncomeStatement.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/reports/IncomeStatement.groovy`
- `InvoiceAcctgTransEntryParameters.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/reports/InvoiceAcctgTransEntryParameters.groovy`
- `MonthSelection.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/reports/MonthSelection.groovy`
- `SalesInvoiceByProductCategorySummary.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/reports/SalesInvoiceByProductCategorySummary.groovy`
- `TransactionTotals.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/reports/TransactionTotals.groovy`
- `TrialBalance.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/reports/TrialBalance.groovy`
- `TaxAuthorityServicesScript.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/tax/TaxAuthorityServicesScript.groovy`
- `AuthorizeTransaction.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/transaction/AuthorizeTransaction.groovy`
- `CaptureTransaction.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/transaction/CaptureTransaction.groovy`
- `ViewGatewayResponse.groovy` — `applications/accounting/src/main/groovy/org/apache/ofbiz/accounting/transaction/ViewGatewayResponse.groovy`
- `AutoAcctgAdminTests.groovy` — `applications/accounting/src/test/groovy/org/apache/ofbiz/accounting/accounting/AutoAcctgAdminTests.groovy`
- `AutoAcctgAgreementTests.groovy` — `applications/accounting/src/test/groovy/org/apache/ofbiz/accounting/accounting/AutoAcctgAgreementTests.groovy`
- `AutoAcctgBudgetTests.groovy` — `applications/accounting/src/test/groovy/org/apache/ofbiz/accounting/accounting/AutoAcctgBudgetTests.groovy`
- `AutoAcctgCostTests.groovy` — `applications/accounting/src/test/groovy/org/apache/ofbiz/accounting/accounting/AutoAcctgCostTests.groovy`
- `AutoAcctgFinAccountTests.groovy` — `applications/accounting/src/test/groovy/org/apache/ofbiz/accounting/accounting/AutoAcctgFinAccountTests.groovy`
- `AutoAcctgFixedAssetTests.groovy` — `applications/accounting/src/test/groovy/org/apache/ofbiz/accounting/accounting/AutoAcctgFixedAssetTests.groovy`
- `AutoAcctgInvoiceTests.groovy` — `applications/accounting/src/test/groovy/org/apache/ofbiz/accounting/accounting/AutoAcctgInvoiceTests.groovy`
- `AutoAcctgLedgerTests.groovy` — `applications/accounting/src/test/groovy/org/apache/ofbiz/accounting/accounting/AutoAcctgLedgerTests.groovy`
- `AutoAcctgPaymentGatewayTests.groovy` — `applications/accounting/src/test/groovy/org/apache/ofbiz/accounting/accounting/AutoAcctgPaymentGatewayTests.groovy`
- `AutoAcctgPaymentTests.groovy` — `applications/accounting/src/test/groovy/org/apache/ofbiz/accounting/accounting/AutoAcctgPaymentTests.groovy`
- `AutoAcctgTransPurchaseTests.groovy` — `applications/accounting/src/test/groovy/org/apache/ofbiz/accounting/accounting/AutoAcctgTransPurchaseTests.groovy`
- `AutoAcctgTransSalesTests.groovy` — `applications/accounting/src/test/groovy/org/apache/ofbiz/accounting/accounting/AutoAcctgTransSalesTests.groovy`
- `AutoInvoiceTests.groovy` — `applications/accounting/src/test/groovy/org/apache/ofbiz/accounting/accounting/AutoInvoiceTests.groovy`
- `AutoPaymentTests.groovy` — `applications/accounting/src/test/groovy/org/apache/ofbiz/accounting/accounting/AutoPaymentTests.groovy`
- `FixedAssetTests.groovy` — `applications/accounting/src/test/groovy/org/apache/ofbiz/accounting/accounting/FixedAssetTests.groovy`
- `InvoicePerShipmentTests.groovy` — `applications/accounting/src/test/groovy/org/apache/ofbiz/accounting/accounting/InvoicePerShipmentTests.groovy`
- `PaymentApplicationTests.groovy` — `applications/accounting/src/test/groovy/org/apache/ofbiz/accounting/accounting/PaymentApplicationTests.groovy`
- `PaymentContentTests.groovy` — `applications/accounting/src/test/groovy/org/apache/ofbiz/accounting/accounting/PaymentContentTests.groovy`
- `RateTests.groovy` — `applications/accounting/src/test/groovy/org/apache/ofbiz/accounting/accounting/RateTests.groovy`
- `SendInvoicePerEmailPermissionTests.groovy` — `applications/accounting/src/test/groovy/org/apache/ofbiz/accounting/invoice/SendInvoicePerEmailPermissionTests.groovy`
- `TaxAuthorityTests.groovy` — `applications/accounting/src/test/groovy/org/apache/ofbiz/accounting/tax/test/TaxAuthorityTests.groovy`

## 主要フロー／simple サービス内で省略した OFBiz 分岐

- 主要 invoice/payment flow: party invoice prefix と個別採番、承認時 payment application matching/capture、order adjustments/billing account/contact-mech/shipment/payment-preference/fees、GL posting/reversal、purchase/return order。
- 未移植 simple services: 下流の GL 自動仕訳・未移植 Java/Groovy・他コンポーネント service 呼出しは実行していない。
- 再請求: 未請求数量のない受注は追加請求を生成しない。
- `updatePaymentMethodAddress`: 住所履歴更新を委譲する未移植の `createUpdatePaymentMethodAndCreditCard` Java service 呼び出しのみを省略。PaymentMethod address の他の各 simple handler は実装済み。
- `GeneralLedgerServices.xml`: 為替レート取得と通貨換算、`GROUP_ROLLUP` 親組織への集計、未移植 GL 自動仕訳サービス呼び出しは省略。期間締めに伴う自動仕訳・一部の account posting configuration／error journal 分岐も未移植。
- `AcctgTransServices.xml`: 外貨換算、関連組織の `GROUP_ROLLUP` 集計、posting configuration に基づく自動仕訳と error journal の生成は省略。
- `FinAccountServices.xml`: `getFinAccountTransRunningTotalAndBalances` が呼ぶ未移植 Groovy `getPartyAccountingPreferences` は実行せず、`PartyAcctgPreference` の直接参照で基準通貨を選ぶ（locale による OFBiz 通貨書式は未移植）。`depositWithdrawPayments` の `checkAndCreateBatchForValidPayments` 呼び出しは未移植のため、`groupInOneTransaction=Y` でも PaymentGroup の作成は行わない。`updatePayment` Groovy service 呼び出しは Payment の FinAccountTrans 関連付けのみを直接更新し、service 固有の処理/ECA は実行しない。`postFinAccountTransToGl` の GL 自動仕訳と外貨換算は省略した。
