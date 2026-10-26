# Product 移行レビュー記録

## 1. 検証結果

最終 plain `./gradlew :migration:product-springboot:check`: **BUILD SUCCESSFUL**。XML レポート集計は 1605 tests、failures/errors/skipped 0。

| テストクラス | 件数 |
| --- | ---: |
| `CalculateProductPriceFlowTest` | 6 |
| `CustomerDigitalDownloadServicesTest` | 26 |
| `EntityAutoAcceptanceTest` | 465 |
| `EntityAutoCatalogTest` | 921 |
| `FacilityContactMechServicesTest` | 29 |
| `HttpsTest` | 3 |
| `InventoryReserveServicesTest` | 13 |
| `InventoryServicesTest` | 35 |
| `IssuanceServicesTest` | 9 |
| `LocalOrderAdapterTest` | 1 |
| `MainFlowE2eSequenceTest` | 2 |
| `MultiRollbackTest` | 2 |
| `PermissionServicesTest` | 3 |
| `PicklistServicesTest` | 17 |
| `PostgresSmokeTest` | 1 |
| `ProductInventoryFlowTest` | 14 |
| `ProductSimpleServiceExecutorTest` | 4 |
| `ProductUomFormulasTest` | 2 |
| `QuickShipEntireOrderFlowTest` | 3 |
| `RentalServicesAutoCreateDisabledTest` | 1 |
| `RentalServicesTest` | 12 |
| `SecurityTest` | 11 |
| `ShipmentStatusPermissionTest` | 2 |
| `SimpleServiceCatalogTest` | 1 |
| `SpringdocLocalProfileTest` | 1 |
| `SpringdocProfileTest` | 1 |
| `StockMoveServicesTest` | 17 |
| `UpdateShipmentFlowTest` | 3 |
| **合計** | **1605** |
## 2. OFBiz 互換の判断・差分

- 同一親の複合 OUT-key 作成で parent-scoped max+1 が競合した場合、DB の一意制約により 409 を返す。これは OFBiz の make-next-seq-id の競合特性に合わせた明示判断であり、再試行しない。`REQUIRES_NEW` 再試行は `/multi` の原子性を壊したため revert `6f24478024` で撤回し、rollback 回帰は `b012a905` の `MultiRollbackTest` にある（`migration/product-springboot/src/test/java/org/apache/ofbiz/migration/product/MultiRollbackTest.java:91`）。
- LIST_PRICE だけ存在する場合も `price = defaultPrice`（通常 0）となる。OFBiz `PriceServices.java:1073-1075` は rule action が無い場合に price を defaultPrice に戻すためである。
- `updateShipment` が `shipmentTypeId` を受け付けるのは意図した仕様。servicedef は INOUT とし、Groovy 側は `setNonPKFields(parameters)` で非PK項目を更新する（`ShipmentServices.groovy:333` 以降）。
- `reserveAnInventoryItem` は特定在庫品目を予約するが OrderHeader を取得/設定しないため、order reservation を作らない。Minilang の予約作成分岐は `applications/product/minilang/product/inventory/InventoryReserveServices.xml:350-460`。
- 複合 OUT-key の競合を自動リトライする挙動は追加しない。`/multi` rollback と互換性を優先する。
- 受領数量のシリアル品目では accepted が正の場合のみ 1 件分を処理し、accepted=0/rejected>0 は accepted=0 の ShipmentReceipt のみを作る（`ShipmentReceiptServices.groovy:124-135`）。
- シリアル InventoryItem の作成・状態更新後は `updateSerializedInventoryTotals` を呼び、INV_AVAILABLE の ATP/QOH を 1 に同期する（`eecas.xml:52-55`）。
- accepted>0 の受領後に `balanceInventoryItems` を呼び、shortfall reservation の再割当てを行う（`ShipmentReceiptServices.groovy:181-188`）。
- INV_AVAILABLE への更新は returnId がなく accepted>0 の場合だけ行い、既存の INV_PROMISED / INV_ON_HOLD は保持する（`ShipmentReceiptServices.groovy:153-166`）。
- ShipmentReceipt.datetimeReceived は入力値を保持し、省略時のみ現在時刻を設定する（`ShipmentReceiptServices.groovy:38-54`）。
- currentInventoryItemId の既存品目更新は `updateInventoryItem` に委譲し、Catalog または Facility の UPDATE 権限を別途要求する（`services_facility.xml:67-79`）。
- `createProduct` は handler 内で CATALOG または CATALOG_ROLE の CREATE/ADMIN を確認し、OFBTOOLS の base permission も維持する（`ProductServicesScript.groovy:36-40`）。

## 3. 部分移植・外部副作用

H3b 以前に「部分移植」として扱った6サービスと callee。現在の 5 件は引き続き `partial`。`issueOrderItemShipGrpInvResToShipment` は `LocalOrderAdapter` の実装後に `ported` となったため、その状態差も表に明記する。

| サービス | 未移植呼び出し |
| --- | --- |
| `cancelOrderInventoryReservation` | `decomposeInventoryItem` |
| `cancelOrderItemInvResQty` | `decomposeInventoryItem` |
| `cancelOrderItemIssuanceFromSalesShipment` | `reserveStoreInventory` |
| `getMktgPackagesAvailable` | `getProductInventoryAvailableFromAssocProducts` |
| `processPhysicalStockMove` | `createInventoryItem` |
| `issueOrderItemShipGrpInvResToShipment`（現在は `ported`） | `OrderPort.changeOrderItemStatus`（`LocalOrderAdapter` で実装済み） |

現在の状態別件数と全77件の callee は `inventory/simple-dependencies.md` を参照してください。

出荷の `checkAndUpdateWorkEffort` は 501 note。出荷・受領時の請求書生成、通知、ProductKeyword/ECA、会計連携は明示的 no-op または Port の 501 であり、`unported.md` に列挙する。`quickShipEntireOrder` の在庫発行自体は実装済み。

## 4. 注文境界と検証上の注意

OrderItem のステータスを変更するローカル adapter は OrderItem と OrderStatus を更新するが、注文ヘッダー完了判定と order-status ECA は no-op。`OrderItem` は order コンポーネント所有のため Product entity API に公開されず、`ITEM_COMPLETED` と OrderStatus の確認は `MainFlowE2eSequenceTest` / `QuickShipEntireOrderFlowTest` で行う。

基本価格フローは有効期間・価格種別の選択と基本値の fallback を実装する。価格ルール、販促、VAT 表示調整、契約価格、通貨換算は未移植。詳細は `unported.md` を参照。
