# Accounting Spring Boot 移行レビュー

## 1. 検証結果

| 検証 | 結果 |
| --- | --- |
| Task B `./gradlew :migration:accounting-springboot:check --console=plain` | **BUILD SUCCESSFUL** |
| `AccountingCatalogTest` | 500 pass / 0 fail / 0 skip |
| `AccountingFlowTest` | 3 pass / 0 fail / 0 skip |
| `AccountingSecurityTest` | 3 pass / 0 fail / 0 skip |
| `AcctgTransServicesTest` | 10 pass / 0 fail / 0 skip |
| `FinAccountServicesTest` | 38 pass / 0 fail / 0 skip |
| `GeneralLedgerServicesTest` | 28 pass / 0 fail / 0 skip |
| `PaymentGatewayConfigServicesTest` | 1 pass / 0 fail / 0 skip |
| `PaymentMethodServicesTest` | 7 pass / 0 fail / 0 skip |
| `HttpsSecurityTest` / `SpringdocProfileTest` | 各1 pass / 0 fail / 0 skip |
| `PostgresSmokeTest` | 2 pass / 0 fail / 0 skip |
| 合計 | **594 pass / 0 fail / 0 skip** |
| local bootRun と `cases.json` の E2E 予行 | ID順に21/21ケースが期待どおり。予行後にサーバ停止を確認 |
| inventory 再生成 | service 705、entity-auto 386、request 551、entity 147、指定 accounting entitymodel のview-entity 37 |

entity-auto CRUD acceptance は77件実行、54件スキップ（CRUD三つ組不足23、型・桁・integrity制約22、validation／required依存9）。simple-handlerの登録・404検証とサービス別テストはこの集計とは別に実施した。

## 2. OFBiz との差分と既知の制約

- `ACCOUNTING_ROLE_*` による組織／ロール単位の権限は未移植。`ACCOUNTING_*` 権限の保持者は全組織の伝票を操作できる。これは OFBiz の `acctgInvoicePermissionCheck` と同等の挙動。
- `createInvoice`: PartyAcctgPreference の `invoiceIdPrefix` と per-party invoice sequence enforcement は未移植。
- `setInvoiceStatus`: 承認時の自動PaymentApplication照合・gateway captureは未移植。
- `createInvoiceForOrder`: adjustments、billing account、contact-mech、shipment grouping、payment-preference auto-application、fees は未移植。
- 請求／支払: GL transaction creation、posting、reversal は未移植。
- 注文請求: `SALES_ORDER` のみ。purchase order / return order は未移植。未請求数量のない再請求は新しい invoice を作らない。
- Accounting の simple service は73件を移植し、全件にサービス別の正常系テストを用意した。GL自動仕訳20件は意図的に未登録とし、servicedef由来の期待集合テストで登録対象73件と未登録・404対象20件を確認する。
- `unported.md` の省略分岐節に、PaymentMethod住所更新の未移植Java呼出し、GL/AcctgTransの外貨換算・GROUP_ROLLUP・posting設定とerror journal、FinAccountの未移植Groovy／支払バッチ／GL呼出しの省略を記載。
- Widget、FTL、view-entity はSpring APIに移植しない。全一覧は `unported.md`。
- ECA/SECA engine は実装していない。FinAccount の残高を更新するコンポーネント内 ECA は移植したが、それ以外の暗黙のイベント連鎖は発生しない。

## 3. セキュリティ選定

機密14 Entityは、カード・金融口座・金融取引・ゲートウェイ応答／設定情報を含むことを基準に選定した。`Payment` と `Invoice` は認証情報そのものを持たないため通常のAccounting権限とする。PaymentMethod/CreditCardの参照、カード番号redaction、機密entity-auto作成制限は `AccountingSecurityTest` で確認する。

`ACCOUNTING_ROLE_*` による組織／ロール単位の制限は未移植であり、`ACCOUNTING_*` 権限の保持者は全組織の伝票を操作できる。OFBiz の `acctgInvoicePermissionCheck` も組織所有者による追加制限を行わない。

Tomcat trusted proxy の既定値をlocalを含むAccounting profileで `a^` とし、明示設定がない限り転送ヘッダーを信頼しない。

## 4. 共通基盤への改善提案

- generated ServiceCatalog に `permission-service` 名と `main-action` を保持し、handler/EntityAutoを通じて共通permission dispatchで適用できるようにする。
- sensitive entity の read/create/update に対する統一契約と、呼出し元identityに応じた操作権限のテスト可能なAPIを整備する。
- `EntityRegistry.findByEquality` の100件上限により一覧・集計結果が欠落しうる。共通基盤に上限なし検索または明示的なページング契約を追加する（Accounting ではモジュール内の無制限 Criteria 検索で補完）。
- 上記提案のために共通基盤ソースは変更していない。
