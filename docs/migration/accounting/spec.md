# Accounting Spring Boot 移行仕様

## 1. 対象と構成

- 対象コンポーネント: `applications/accounting`
- 対象ソース基準コミット: `4da645b23a`（Task A 実装時点。Task B の変更は現在の未コミット差分）
- common base: `origin/devin/1793006105-springboot-common` の `23f8c75e65` までを取り込み済み。
- 実装先: `migration/accounting-springboot`
- 参照する共通基盤: `migration/springboot-common`。この移行では共通基盤を変更しない。
- カタログの基準: Accounting の service definition XML。entity-auto は生成 `ServiceCatalog` からAccounting由来の386件だけを登録する。
- 機械生成の全量棚卸し: `docs/migration/accounting/inventory/`。生成コマンドは本書末尾。

## 2. 機能とドメインモデル

| 領域 | 主な機能 | 主なエンティティ |
| --- | --- | --- |
| Entity API / entity-auto | entity-auto の登録と CRUD、Entity API | entity-auto 対象エンティティ |
| Invoice | 請求作成・明細・受注請求・状態履歴・合計・消込残高 | Invoice, InvoiceItem, InvoiceStatus |
| Payment | 入金・支払、状態遷移、PaymentApplication | Payment, PaymentApplication |
| Financial account / ledger | Financial Account、勘定科目、取引、期間締め、照合 | FinAccount, AcctgTrans 等 |

主要な手書きサービス12件: `createInvoice`, `setInvoiceStatus`, `createInvoiceItem`, `createInvoiceForOrder`, `checkInvoicePaymentApplications`, `getInvoiceTotal`, `getInvoiceItemTotal`, `getInvoiceNotApplied`, `getInvoiceApplied`, `createPayment`, `createPaymentApplication`, `setPaymentStatus`。`getInvoice*` のうち合計・残高は OFBiz ではサービスではなく `InvoiceWorker` のメソッドを API handler として公開する。

simple-method の手書き移植73件:

| クラス | 件数 | サービス |
| --- | ---: | --- |
| `FinAccountServiceHandlers` | 27 | `createFinAccount`, `updateFinAccount`, `updateFinAccountBalancesFromTrans`, `updateFinAccountBalancesFromAuth`, `createFinAccountTrans`, `createFinAccountRole`, `createFinAccountAuth`, `expireFinAccountAuth`, `setFinAccountTransStatus`, `updatePaymentOnFinAccTransStatusSetToCancel`, `getFinAccountTransListAndTotals`, `getFinAccountTransRunningTotalAndBalances`, `reconcileFinAccountTrans`, `reconcileAdjustmentFinAcctgTrans`, `reconcileDepositFinAcctgTrans`, `reconcileWithdrawalFinAcctgTrans`, `getReconciliationClosingBalance`, `depositWithdrawPayments`, `expirePaymentAssociationsOnFinAccountTransCancel`, `createPaymentAndFinAccountTrans`, `getTransactionTotalByGlReconcileId`, `assignGlRecToFinAccTrans`, `removeFinAccountTransFromReconciliation`, `isGlReconciliationReconciled`, `cancelBankReconciliation`, `getAssociatedAcctgTransEntriesWithFinAccountTrans`, `autoFinAccountReconciliation` |
| `GeneralLedgerServiceHandlers` | 19 | `quickCreateAcctgTransAndEntries`, `calculateGlJournalTrialBalance`, `postGlJournal`, `updateGlReconciliationLastModified`, `createGlReconciliationEntry`, `completeAcctgTransEntries`, `createAcctgTransAndEntries`, `closeFinancialTimePeriod`, `computeGlAccountBalanceForTimePeriod`, `computeAndStoreGlAccountHistoryBalance`, `prepareIncomeStatement`, `getGlAccountFromAccountType`, `getInventoryItemOwner`, `copyAcctgTransAndEntries`, `updateGlAccountCategoryMember`, `createGlAcctCatMemFromCostCenters`, `getGlAcctgAndAmountPercentage`, `getInventoryValuationList`, `setGlReconciliationStatus` |
| `AccountingPermissionHandlers` | 13 | `acctgAgreementPermissionCheck`, `acctgBasePermissionCheck`, `acctgBillingAcctCheck`, `acctgCommissionPermissionCheck`, `acctgCostPermissionCheck`, `acctgFinAcctPermissionCheck`, `acctgFxPermissionCheck`, `acctgInvoicePermissionCheck`, `acctgPrefPermissionCheck`, `acctgTransactionPermissionCheck`, `basicGeneralLedgerPermissionCheck`, `fixedAssetPermissionCheck`, `acctgPaymentPermissionCheck` |
| `AcctgTransServiceHandlers` | 8 | `checkAcctgTransPosted`, `createAcctgTransEntry`, `updateAcctgTransEntry`, `deleteAcctgTransEntry`, `getAcctgTransEntriesAndTransTotal`, `calculateAcctgTransTrialBalance`, `postAcctgTrans`, `calculateGlAccountTrialBalance` |
| `PaymentMethodServiceHandlers` | 5 | `setPaymentMethodAddress`, `updatePaymentMethodAddress`, `createPayPalPaymentMethod`, `createPaymentGroupMember`, `expirePaymentGroupMember` |
| `PaymentGatewayConfigServiceHandlers` | 1 | `updatePaymentGatewayConfigType` |

GL自動仕訳のsimple 20件は意図的に未登録として404を返す。権限は、FinAccountの明示的な`acctgFinAcctPermissionCheck`でCREATE/UPDATEを照合し、その他はservicedefのpermission-service/main-actionまたはinvoke既定アクションに従う。FinAccount handlerは機密result entityを指定する。共通の割当表と除外理由は `unported.md` を参照。

### 手書きサービスの権限割当

| サービス群 | 権限 |
| --- | --- |
| FinAccount 27件 | 明示的な `acctgFinAcctPermissionCheck` は servicedef の `main-action` を適用: CREATE は `createFinAccount`, `createFinAccountTrans`, `createFinAccountRole`, `createFinAccountAuth`, `depositWithdrawPayments`、UPDATE は `updateFinAccount`, `setFinAccountTransStatus`, `reconcileFinAccountTrans`, `assignGlRecToFinAccTrans`, `removeFinAccountTransFromReconciliation`, `cancelBankReconciliation`。他の mutation は `ACCOUNTING_CREATE` / `ACCOUNTING_UPDATE`、読取6件は `ACCOUNTING_VIEW`。`createPaymentAndFinAccountTrans` は `ACCOUNTING_CREATE`。全27 handlerに機密な `FinAccount` / `FinAccountTrans` / `FinAccountAuth` / `FinAccountRole` を `sensitiveResultEntity` として設定し、`ACCOUNTING_SENSITIVE_VIEW` または `ACCOUNTING_ADMIN` も必須とする。 |
| GeneralLedger 19件 | `basicGeneralLedgerPermissionCheck` は `ACCOUNTING_{CREATE,UPDATE,DELETE,VIEW}`。`acctgTransactionPermissionCheck` を指定する `createAcctgTransAndEntries` / `completeAcctgTransEntries` は `ACCTG_ATX_CREATE` / `ACCTG_ATX_UPDATE`。 |
| AcctgTrans 8件 | `acctgTransactionPermissionCheck` の `main-action` に従い `ACCTG_ATX_CREATE` / `ACCTG_ATX_UPDATE` / `ACCTG_ATX_DELETE`。permission-service のない読取系は `ACCOUNTING_VIEW`。 |
| PermissionServices 13件 | `mainAction` を受け、`ACCOUNTING_*`、`ACCOUNTING_ROLE_*`、`ACCOUNTING_COMM_*`、`ACCOUNTING_FX_*`、`ACCTG_PREF_*`、`ACCTG_ATX_*` authorityから `hasPermission` / `failMessage` を返す。`ADMIN` は該当actionを包含する。 |
| PaymentMethod 5件 / GatewayConfig 1件 | servicedefに個別permission-serviceがないため、handlerのCREATE/UPDATE/VIEW actionで `ACCOUNTING_*` を要求。機密entityのhandlerには `sensitiveResultEntity` も適用。 |

## 3. API と画面方針

- サービス: `POST /api/accounting/services/{serviceName}`
- 複数実行: `POST /api/accounting/services/{serviceName}/multi`。単一トランザクションで、1件でも失敗すれば全件を rollback。
- エンティティ: `/api/accounting/entities/{EntityName}` および `/one`。Entity API の範囲は登録カタログ参照エンティティと `extra-entities` に限定する。
- Widget、画面、FTL は移植せず、JSON API を提供する。全画面一覧は `unported.md`。

## 4. セキュリティ

- `migration.security.component: accounting`。base permission は `OFBTOOLS` と `ACCOUNTING`、action prefix は `ACCOUNTING`。
- `extra-entities` は手書き handler、デモデータ、テストに必要な次の32件に限定する: `StatusItem`, `StatusValidChange`, `Party`, `PartyRole`, `OrderHeader`, `OrderRole`, `OrderItem`, `OrderItemBilling`, `Product`, `ProductStore`, `Invoice`, `InvoiceItem`, `InvoiceStatus`, `InvoiceItemTypeMap`, `Payment`, `PaymentApplication`, `PaymentMethod`, `PaymentMethodType`, `CreditCard`, `PartyRelationship`, `PartyGroup`, `ProductGlAccount`, `ProductCategoryGlAccount`, `ProductCategoryMember`, `FinAccount`, `FinAccountStatus`, `FinAccountTrans`, `FinAccountAuth`, `FinAccountRole`, `GlReconciliation`, `GlReconciliationEntry`, `PaymentGroupMember`。
- 機密 Entity は14件: `PaymentMethod`, `CreditCard`, `EftAccount`, `CheckAccount`, `GiftCard`, `PayPalPaymentMethod`, `FinAccount`, `FinAccountTrans`, `FinAccountAuth`, `FinAccountRole`, `PaymentGatewayResponse`, `PaymentGatewayRespMsg`, `PaymentGatewayConfig`, `PaymentGatewayConfigType`。資格情報・カード番号・金融口座・ゲートウェイ応答／設定を含む範囲として選定した。
- `Payment` と `Invoice` は金額・会計記録であり、カード番号や認証情報を持たないため、機密 Entity ではなく通常の `ACCOUNTING` 権限で保護する。`CreditCardTypeGlAccount` は機密値を含まないため除外する。Accounting の entity/service 定義には `ValueLinkKey` および payroll 名の Entity はない。
- CreditCard の `cardNumber` は `[REDACTED]` で返す。機密 Entity の参照・サービス処理には sensitive 権限を要求する。
- HTTPS は local/test で明示的に無効化し、postgres/prod は有効化する。Tomcat trusted proxy の既定値は全プロファイルで信頼なし (`a^`)。実際のTLS終端プロキシだけを `MIGRATION_SECURITY_TRUSTED_PROXIES` で設定する。
- Swagger UI は local のみ有効。デモパスワードは `MIGRATION_DEMO_PASSWORD` 環境変数から読み、値をファイルやログに保存しない。

## 5. 依存と Port 境界

- 受注・請求関連の呼出し境界: `OrderPort`、`ProductPort` と `adapter/local`。
- Accounting 外の service は Port を介して接続する。未移植 Java/Groovy、GL 自動仕訳は未呼出し。Port 実装・境界の現況は `migration/accounting-springboot/src/main/java/org/apache/ofbiz/migration/accounting/` を参照。
- 共通基盤の entity-auto カタログ・採番・セキュリティ・multi API は利用するが、共通基盤ソースは変更しない。

## 6. テストと受け入れ基準

既存テスト: `AccountingCatalogTest`, `AccountingFlowTest`, `AccountingSecurityTest`, `HttpsSecurityTest`, `PostgresSmokeTest`, `SpringdocProfileTest`。Testcontainers PostgreSQL は `postgres:16-alpine` を使用する。

simpleサービスは `GeneralLedgerServicesTest`, `FinAccountServicesTest`, `AcctgTransServicesTest`, `PaymentMethodServicesTest`, `PaymentGatewayConfigServicesTest` でサービス名を表示するケースとして検証する。PermissionServices 13件は `AccountingSecurityTest.permissionServicesAreRegisteredAndReflectTheCallersAuthorities` で各サービスの応答を確認する。

| AC | 基準 | 対応テスト |
| --- | --- | --- |
| AC-01 | servicedef 由来の entity-auto 386件が一致・登録される | `AccountingCatalogTest` |
| AC-02 | CRUD 受入れ対象は作成・取得・更新・削除され、未実行理由が記録される | `AccountingCatalogTest.executesEntityAutoCrudWhereCatalogAndSchemaAllowIt` |
| AC-03 | 匿名401、base permission・閲覧者の更新拒否、機密Entity制御と redaction | `AccountingSecurityTest` |
| AC-04 | 注文請求、承認、入金、消込、合計、READY→PAID | `AccountingFlowTest.orderInvoicePaymentAndApplicationFlow` |
| AC-05 | 不正遷移・過剰消込・再請求と multi rollback | `AccountingFlowTest` |
| AC-06 | local/test HTTP、postgres HTTPS、Swagger local-only | `HttpsSecurityTest`, `SpringdocProfileTest` |
| AC-07 | PostgreSQL で schema と CRUD が動作する | `PostgresSmokeTest` |
| AC-08 | simple-method 73件が登録され、除外GL 20件は未登録 | `AccountingCatalogTest.simpleServicesMatchTheServicedefRegistrationScope` |

主要な受入れ基準の実行状況は `review.md` に記録する。

## 7. コード参照

- 請求・支払 handler と OFBiz file:line Javadoc: `AccountingServiceHandlers.java`
- entity-auto カタログ: `AccountingCatalogRegistrar.java`
- permission handler: `AccountingPermissionHandlers.java`; 移植元 `applications/accounting/minilang/permissions/PermissionServices.xml`
- security profiles: `migration/accounting-springboot/src/main/resources/application*.yml`
- inventory 再生成:
  `python3 migration/tools/ofbiz_inventory.py --component applications/accounting --entitymodel applications/datamodel/entitydef/accounting-entitymodel.xml --out docs/migration/accounting/inventory`
