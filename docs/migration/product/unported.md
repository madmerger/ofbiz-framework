# Product の部分移植・no-op の一覧

## 1. 明示された部分移植と副作用

`ProductUnportedPort` に委譲するサービス呼び出しは 501 とし、成功したように見せない。現在の部分移植 simple services と各未移植先は `inventory/simple-dependencies.md` に記録する。

### 出荷・受領

| 条件 | OFBiz 副作用/サービス | 移行版の扱い |
| --- | --- | --- |
| `SHIPMENT_PACKED`、`SALES_SHIPMENT` | `createInvoicesFromShipment` | no-op として response note を返す |
| `SHIPMENT_PACKED`、`SALES_SHIPMENT` | `setInvoicesToReadyFromShipment` | no-op note |
| `SHIPMENT_SHIPPED`、`SALES_SHIPMENT` | `sendShipmentCompleteNotification` | no-op note |
| `SHIPMENT_SHIPPED`、`PURCHASE_RETURN` | `createInvoicesFromReturnShipment` | no-op note |
| `PURCH_SHIP_SHIPPED`、`DROP_SHIPMENT` | `createInvoicesFromShipment` | no-op note |
| 出荷状態変更時 | `checkAndUpdateWorkEffort` | Port 経由で 501 note |
| その他の shipment SECA | 予定/受領通知、受領請求書、施設設定同期 | 未実装 |

### 商品・在庫・注文・価格

- `createProduct` は ProductKeyword の生成/検索索引および product-create ECA を実行しない。
- `receiveInventoryProduct` は ShipmentReceipt と在庫詳細を記録する。会計処理と receipt ECA は no-op note。
- `createInventoryItem` の標準 unit-cost lookup に必要な会計連携は `AccountingPort` の境界に置く。
- 注文品目ステータス変更は `LocalOrderAdapter` が StatusValidChange を検証し、OrderItem/OrderStatus を更新する。order header completion check と order service ECA は no-op。その他の非 CRUD order service は `OrderPort` 経由で 501。
- `reserveAnInventoryItem` は `InventoryReserveServices.xml:350-460` のとおり OrderHeader を設定せず、注文予約は作成しない。
- `calculateProductPrice` は有効な基本 ProductPrice のみを扱う。価格ルール、販促、VAT 表示調整、契約価格、通貨換算は未移植。
- `OrderItem` は order コンポーネントの entity であり Product entity API に公開しない。状態検証はテストから行う。
- entity-auto の親スコープ max+1 競合は 409 とし、OFBiz と同じくリトライしない。`/multi` の rollback と両立するため、独立 transaction retry は採用しない。

## 2. 棚卸しスクリプトによる対象外/未移植一覧

以下は `~/work/product/unported_tables.md` を変更せず、そのまま埋め込む。集計値を含め、生成元の内容を保持する。

<!-- BEGIN verbatim: /home/ubuntu/work/product/unported_tables.md -->
## 集計（サービス）

| 区分 | 件数 |
|---|---:|
| servicedef 定義総数（21 ファイル） | 835 |
| うち entity-auto | 460 |
| うち simple | 77 |
| うち java | 88 |
| うち groovy | 195 |
| うち interface | 9 |
| うち group | 6 |
| 移植済み: entity-auto（全量） | 460 |
| 移植済み: simple（全量、Java へ手作業移植） | 77 |
| 移植済み: java/groovy（主要フロー） | 6 |
| 未移植: java/groovy/group | 283 |
| 対象外: interface（実体を持たない定義） | 9 |

### 移植済みの java/groovy サービス

| サービス | engine | location |
|---|---|---|
| `calculateProductPrice` | java | `org.apache.ofbiz.product.price.PriceServices#calculateProductPrice` |
| `createInventoryItem` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/inventory/InventoryServicesScript.groovy#createInventoryItem` |
| `createProduct` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#createProduct` |
| `quickShipEntireOrder` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#quickShipEntireOrder` |
| `receiveInventoryProduct` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentReceiptServices.groovy#receiveInventoryProduct` |
| `updateShipment` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#updateShipment` |

### 未移植の java/groovy/group サービス（全件）

| 理由 | 件数 |
|---|---:|
| 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） | 233 |
| スコープ外（プロモーションは移植しない） | 39 |
| 主要フロー外（サービスグループ） | 6 |
| 主要フロー外（基本価格以外の価格計算・価格ルール） | 4 |
| 主要フロー外（データ移行・アップグレード用） | 1 |

| # | サービス | engine | location | servicedef | 理由 |
|---:|---|---|---|---|---|
| 1 | `addAdditionalViewForProduct` | java | `org.apache.ofbiz.product.product.ProductServices#addAdditionalViewForProduct` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 2 | `addImageForProductPromo` | java | `org.apache.ofbiz.product.product.ProductServices#addImageForProductPromo` | servicedef/services.xml | スコープ外（プロモーションは移植しない） |
| 3 | `addImageFrame` | java | `org.apache.ofbiz.product.imagemanagement.FrameImage#addImageFrame` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 4 | `addMultipleuploadForProduct` | java | `org.apache.ofbiz.product.imagemanagement.ImageManagementServices#addMultipleuploadForProduct` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 5 | `addPartyToCategory` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryServicesScript.groovy#addPartyToCategory` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 6 | `addPartyToProduct` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#addPartyToProduct` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 7 | `addProductCategoryToCategories` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryServicesScript.groovy#addProductCategoryToCategories` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 8 | `addProductCategoryToCategory` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryServicesScript.groovy#addProductCategoryToCategory` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 9 | `addProductToCategories` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryServicesScript.groovy#addProductToCategories` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 10 | `addRejectedReasonImageManagement` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/imagemanagement/ImageManagementServicesScript.groovy#addRejectedReasonImageManagement` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 11 | `cancleOrderItemGroupOrder` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#cancleOrderItemGroupOrder` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 12 | `catalogPermissionCheck` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/catalog/CatalogServices.groovy#catalogPermissionCheck` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 13 | `checkCategoryPermissionWithViewPurchaseAllow` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryServicesScript.groovy#checkCategoryPermissionWithViewPurchaseAllow` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 14 | `checkOrderItemForProductGroupOrder` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#checkOrderItemForProductGroupOrder` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 15 | `checkProductGroupOrderExpired` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#checkProductGroupOrderExpired` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 16 | `checkProductRelatedPermission` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#checkProductRelatedPermissionService` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 17 | `copyToProductVariants` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#copyToProductVariants` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 18 | `countProductView` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#countProductView` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 19 | `createCategoryContent` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryContentServicesScript.groovy#createCategoryContent` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 20 | `createDownloadContentForCategory` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryContentServicesScript.groovy#createDownloadContentForCategory` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 21 | `createDownloadContentForProduct` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductContentServicesScript.groovy#createDownloadContentForProduct` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 22 | `createEmailContentForProduct` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductContentServicesScript.groovy#createEmailContentForProduct` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 23 | `createImageContentApproval` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/imagemanagement/ImageManagementServicesScript.groovy#createImageContentApproval` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 24 | `createJobForProductGroupOrder` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#createJobForProductGroupOrder` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 25 | `createMissingCategoryAndProductAltUrls` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/catalog/CatalogServices.groovy#createMissingCategoryAndProductAltUrls` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 26 | `createNewImageThumbnail` | java | `org.apache.ofbiz.product.imagemanagement.ImageManagementServices#createNewImageThumbnail` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 27 | `createProductCategory` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryServicesScript.groovy#createProductCategory` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 28 | `createProductCategoryAttribute` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryServicesScript.groovy#createProductCategoryAttribute` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 29 | `createProductCategoryGlAccount` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#createProductCategoryGlAccount` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 30 | `createProductCategoryLink` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryServicesScript.groovy#createProductCategoryLink` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 31 | `createProductConfigItemContent` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/config/ProductConfigItemContentServicesScript.groovy#createProductConfigItemContent` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 32 | `createProductContent` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductContentServicesScript.groovy#createProductContent` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 33 | `createProductGroupOrder` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#createProductGroupOrder` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 34 | `createProductInCategory` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryServicesScript.groovy#createProductInCategory` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 35 | `createProductPrice` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/price/PriceServicesScript.groovy#createProductPrice` | servicedef/services.xml | 主要フロー外（基本価格以外の価格計算・価格ルール） |
| 36 | `createProductReview` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#createProductReview` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 37 | `createRelatedUrlContentForCategory` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryContentServicesScript.groovy#createRelatedUrlContentForCategory` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 38 | `createSalesAgreement` | group | `createSalesAgreement#` | servicedef/services.xml | 主要フロー外（サービスグループ） |
| 39 | `createSimpleTextContentForAlternateLocale` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductContentServicesScript.groovy#createSimpleTextContentForAlternateLocale` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 40 | `createSimpleTextContentForCategory` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryContentServicesScript.groovy#createSimpleTextContentForCategory` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 41 | `createSimpleTextContentForProduct` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductContentServicesScript.groovy#createSimpleTextContentForProduct` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 42 | `createSimpleTextContentForProductConfigItem` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/config/ProductConfigItemContentServicesScript.groovy#createSimpleTextContentForProductConfigItem` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 43 | `deleteProductCategoryAttribute` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryServicesScript.groovy#deleteProductCategoryAttribute` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 44 | `deleteProductCategoryGlAccount` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#deleteProductCategoryGlAccount` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 45 | `deleteProductGroupOrder` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#deleteProductGroupOrder` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 46 | `deleteProductKeywords` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#deleteProductKeywords` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 47 | `deleteProductPrice` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/price/PriceServicesScript.groovy#deleteProductPrice` | servicedef/services.xml | 主要フロー外（基本価格以外の価格計算・価格ルール） |
| 48 | `discontinueProductSales` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#discontinueProductSales` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 49 | `duplicateCategoryEntities` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryServicesScript.groovy#duplicateCategoryEntities` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 50 | `duplicateProduct` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#duplicateProduct` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 51 | `duplicateProductCategory` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryServicesScript.groovy#duplicateProductCategory` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 52 | `findProductById` | java | `org.apache.ofbiz.product.product.ProductServices#findProductById` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 53 | `forceIndexProductKeywords` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#forceIndexProductKeywords` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 54 | `getAllExistingVariants` | java | `org.apache.ofbiz.product.feature.ProductFeatureServices#getAllExistingVariants` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 55 | `getCategoryVariantProducts` | java | `org.apache.ofbiz.product.feature.ProductFeatureServices#getCategoryVariantProducts` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 56 | `getProductFeaturesByType` | java | `org.apache.ofbiz.product.feature.ProductFeatureServices#getProductFeaturesByType` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 57 | `getSupplierProductFeatures` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/supplier/SupplierProductServicesScript.groovy#getSupplierProductFeatures` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 58 | `getVariantCombinations` | java | `org.apache.ofbiz.product.feature.ProductFeatureServices#getVariantCombinations` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 59 | `imageCrop` | java | `org.apache.ofbiz.product.imagemanagement.CropImage#imageCrop` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 60 | `imageRotate` | java | `org.apache.ofbiz.product.imagemanagement.RotateImage#imageRotate` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 61 | `indexProductKeywords` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#indexProductKeywords` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 62 | `multipleUploadProductImages` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/imagemanagement/ImageManagementServicesScript.groovy#uploadProductImages` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 63 | `prodCatalogToPartyPermissionCheck` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/catalog/CatalogServices.groovy#prodCatalogToPartyPermissionCheck` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 64 | `productCategoryGenericPermission` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryServicesScript.groovy#productCategoryGenericPermission` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 65 | `productGenericPermission` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#productGenericPermission` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 66 | `productImportFromSpreadsheet` | java | `org.apache.ofbiz.product.spreadsheetimport.ImportProductServices#productImportFromSpreadsheet` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 67 | `productPriceGenericPermission` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#productPriceGenericPermission` | servicedef/services.xml | 主要フロー外（基本価格以外の価格計算・価格ルール） |
| 68 | `quickAddVariant` | java | `org.apache.ofbiz.product.product.ProductServices#quickAddVariant` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 69 | `quickCreateVirtualWithVariants` | java | `org.apache.ofbiz.product.product.ProductServices#quickCreateVirtualWithVariants` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 70 | `removeImageBySize` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/imagemanagement/ImageManagementServicesScript.groovy#removeImageBySize` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 71 | `removeImageContentApproval` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/imagemanagement/ImageManagementServicesScript.groovy#removeImageContentApproval` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 72 | `removeImageFileForImageManagement` | java | `org.apache.ofbiz.product.imagemanagement.ImageManagementServices#removeImageFileForImageManagement` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 73 | `removePartyFromCategory` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryServicesScript.groovy#removePartyFromCategory` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 74 | `removePartyFromProduct` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#removePartyFromProduct` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 75 | `removeProductCategoryFromCategory` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryServicesScript.groovy#removeProductCategoryFromCategory` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 76 | `removeProductContentAndImageFile` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/imagemanagement/ImageManagementServicesScript.groovy#removeProductContentAndImageFile` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 77 | `removeProductContentForImageManagement` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/imagemanagement/ImageManagementServicesScript.groovy#removeProductContentForImageManagement` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 78 | `removeProductFromCategory` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryServicesScript.groovy#removeProductFromCategory` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 79 | `renameImage` | java | `org.apache.ofbiz.product.imagemanagement.ImageManagementServices#renameImage` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 80 | `replaceImageToExistImage` | java | `org.apache.ofbiz.product.imagemanagement.ReplaceImage#replaceImageToExistImage` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 81 | `resizeImageOfProduct` | java | `org.apache.ofbiz.product.imagemanagement.ImageManagementServices#resizeImageOfProduct` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 82 | `resizeImages` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/imagemanagement/ImageManagementServicesScript.groovy#resizeImages` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 83 | `safeAddProductCategoryToCategory` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryServicesScript.groovy#addProductCategoryToCategory` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 84 | `setImageDetail` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/imagemanagement/ImageManagementServicesScript.groovy#setImageDetail` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 85 | `setProductReviewStatus` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#setProductReviewStatus` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 86 | `updateCategoryContent` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryContentServicesScript.groovy#updateCategoryContent` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 87 | `updateContentSEOForCategory` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryContentServicesScript.groovy#updateContentSEOForCategory` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 88 | `updateContentSEOForProduct` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductContentServicesScript.groovy#updateContentSEOForProduct` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 89 | `updateDownloadContentForCategory` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryContentServicesScript.groovy#updateDownloadContentForCategory` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 90 | `updateDownloadContentForProduct` | group | `#` | servicedef/services.xml | 主要フロー外（サービスグループ） |
| 91 | `updateEmailContentForProduct` | group | `#` | servicedef/services.xml | 主要フロー外（サービスグループ） |
| 92 | `updatePartyToCategory` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryServicesScript.groovy#updatePartyToCategory` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 93 | `updatePartyToProduct` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#updatePartyToProduct` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 94 | `updateProduct` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#updateProduct` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 95 | `updateProductCategory` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryServicesScript.groovy#updateProductCategory` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 96 | `updateProductCategoryAttribute` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryServicesScript.groovy#updateProductCategoryAttribute` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 97 | `updateProductCategoryGlAccount` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#updateProductCategoryGlAccount` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 98 | `updateProductCategoryToCategory` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryServicesScript.groovy#updateProductCategoryToCategory` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 99 | `updateProductConfigItemContent` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/config/ProductConfigItemContentServicesScript.groovy#updateProductConfigItemContent` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 100 | `updateProductContent` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductContentServicesScript.groovy#updateProductContent` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 101 | `updateProductGroupOrder` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#updateProductGroupOrder` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 102 | `updateProductIfAvailableFromShipment` | java | `org.apache.ofbiz.product.product.ProductServices#updateProductIfAvailableFromShipment` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 103 | `updateProductPrice` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/price/PriceServicesScript.groovy#updateProductPrice` | servicedef/services.xml | 主要フロー外（基本価格以外の価格計算・価格ルール） |
| 104 | `updateProductQuickAdminName` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#updateProductQuickAdminName` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 105 | `updateProductQuickAdminShipping` | java | `org.apache.ofbiz.product.product.ProductServices#updateProductQuickAdminShipping` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 106 | `updateProductReview` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy#updateProductReview` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 107 | `updateRelatedUrlContentForCategory` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryContentServicesScript.groovy#updateRelatedUrlContentForCategory` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 108 | `updateSimpleTextContentForCategory` | group | `#` | servicedef/services.xml | 主要フロー外（サービスグループ） |
| 109 | `updateSimpleTextContentForProduct` | group | `#` | servicedef/services.xml | 主要フロー外（サービスグループ） |
| 110 | `updateSimpleTextContentForProductConfigItem` | group | `#` | servicedef/services.xml | 主要フロー外（サービスグループ） |
| 111 | `updateStatusImageManagement` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/imagemanagement/ImageManagementServicesScript.groovy#updateStatusImageManagement` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 112 | `uploadFrame` | java | `org.apache.ofbiz.product.imagemanagement.FrameImage#uploadFrame` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 113 | `uploadProductAdditionalViewImages` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductContentServicesScript.groovy#uploadProductAdditionalViewImages` | servicedef/services.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 114 | `calculateAllProductsCosts` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/cost/CostServices.groovy#calculateAllProductsCosts` | servicedef/services_cost.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 115 | `calculateProductAverageCost` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/cost/CostServices.groovy#calculateProductAverageCost` | servicedef/services_cost.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 116 | `calculateProductCosts` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/cost/CostServices.groovy#calculateProductCosts` | servicedef/services_cost.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 117 | `cancelCostComponents` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/cost/CostServices.groovy#cancelCostComponents` | servicedef/services_cost.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 118 | `getProductCost` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/cost/CostServices.groovy#getProductCost` | servicedef/services_cost.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 119 | `getTaskCost` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/cost/CostServices.groovy#getTaskCost` | servicedef/services_cost.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 120 | `productCostPercentageFormula` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/cost/CostServices.groovy#productCostPercentageFormula` | servicedef/services_cost.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 121 | `recreateCostComponent` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/cost/CostServices.groovy#recreateCostComponent` | servicedef/services_cost.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 122 | `cancelInventoryTransfer` | java | `org.apache.ofbiz.product.inventory.InventoryServices#cancelInventoryTransfer` | servicedef/services_facility.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 123 | `checkInventoryAvailability` | java | `org.apache.ofbiz.product.inventory.InventoryServices#checkInventoryAvailability` | servicedef/services_facility.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 124 | `checkProductFacilityRelatedPermission` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/inventory/InventoryServicesScript.groovy#checkProductFacilityRelatedPermission` | servicedef/services_facility.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 125 | `completeInventoryTransfer` | java | `org.apache.ofbiz.product.inventory.InventoryServices#completeInventoryTransfer` | servicedef/services_facility.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 126 | `createFacilityLocation` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/facility/storage/StorageServices.groovy#createFacilityLocation` | servicedef/services_facility.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 127 | `createUpdateFacilityGeoPoint` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/facility/facility/FacilityGeoServices.groovy#createUpdateFacilityGeoPoint` | servicedef/services_facility.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 128 | `facilityGenericPermission` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/inventory/InventoryServicesScript.groovy#facilityGenericPermission` | servicedef/services_facility.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 129 | `getProductInventoryAndFacilitySummary` | java | `org.apache.ofbiz.product.inventory.InventoryServices#getProductInventoryAndFacilitySummary` | servicedef/services_facility.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 130 | `getProductInventoryAvailableFromAssocProducts` | java | `org.apache.ofbiz.product.inventory.InventoryServices#getProductInventoryAvailableFromAssocProducts` | servicedef/services_facility.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 131 | `getProductInventorySummaryForItems` | java | `org.apache.ofbiz.product.inventory.InventoryServices#getProductInventorySummaryForItems` | servicedef/services_facility.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 132 | `issueImmediatelyFulfilledOrder` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/inventory/InventoryIssueServices.groovy#issueImmediatelyFulfilledOrder` | servicedef/services_facility.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 133 | `issueImmediatelyFulfilledOrderItem` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/inventory/InventoryIssueServices.groovy#issueImmediatelyFulfilledOrderItem` | servicedef/services_facility.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 134 | `prepareInventoryTransfer` | java | `org.apache.ofbiz.product.inventory.InventoryServices#prepareInventoryTransfer` | servicedef/services_facility.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 135 | `applyFeatureToProductFromTypeAndCode` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/feature/ProductFeatureServicesScript.groovy#applyFeatureToProductFromTypeAndCode` | servicedef/services_feature.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 136 | `createProductFeatureApplAttr` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/feature/ProductFeatureServicesScript.groovy#createProductFeatureApplAttr` | servicedef/services_feature.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 137 | `createProductFeatureType` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/feature/ProductFeatureServicesScript.groovy#createProductFeatureType` | servicedef/services_feature.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 138 | `attachProductFeaturesToCategory` | java | `org.apache.ofbiz.product.product.ProductUtilServices#attachProductFeaturesToCategory` | servicedef/services_maint.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 139 | `checkImageUrl` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/catalog/CatalogServices.groovy#checkImageUrl` | servicedef/services_maint.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 140 | `checkImageUrlForAllCategories` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/catalog/CatalogServices.groovy#checkImageUrlForAllCategories` | servicedef/services_maint.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 141 | `checkImageUrlForCategory` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/catalog/CatalogServices.groovy#checkImageUrlForCategory` | servicedef/services_maint.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 142 | `checkImageUrlForCategoryAndProduct` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/catalog/CatalogServices.groovy#checkImageUrlForCategoryAndProduct` | servicedef/services_maint.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 143 | `checkImageUrlForProduct` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/catalog/CatalogServices.groovy#checkImageUrlForProduct` | servicedef/services_maint.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 144 | `clearAllVirtualProductImageNames` | java | `org.apache.ofbiz.product.product.ProductUtilServices#clearAllVirtualProductImageNames` | servicedef/services_maint.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 145 | `copyCategoryProductMembers` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryServicesScript.groovy#copyCategoryProductMembers` | servicedef/services_maint.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 146 | `discVirtualsWithDiscVariants` | java | `org.apache.ofbiz.product.product.ProductUtilServices#discVirtualsWithDiscVariants` | servicedef/services_maint.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 147 | `expireAllCategoryProductMembers` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryServicesScript.groovy#expireAllCategoryProductMembers` | servicedef/services_maint.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 148 | `getAllCategories` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/catalog/CatalogServices.groovy#getAllCategories` | servicedef/services_maint.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 149 | `getCategoryTrail` | java | `org.apache.ofbiz.product.category.CategoryWorker#getCategoryTrail` | servicedef/services_maint.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 150 | `getRelatedCategories` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/catalog/CatalogServices.groovy#getRelatedCategories` | servicedef/services_maint.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 151 | `makeStandAloneFromSingleVariantVirtuals` | java | `org.apache.ofbiz.product.product.ProductUtilServices#makeStandAloneFromSingleVariantVirtuals` | servicedef/services_maint.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 152 | `mergeVirtualWithSingleVariant` | java | `org.apache.ofbiz.product.product.ProductUtilServices#mergeVirtualWithSingleVariant` | servicedef/services_maint.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 153 | `purgeOldStoreAutoPromos` | java | `org.apache.ofbiz.product.promo.PromoServices#purgeOldStoreAutoPromos` | servicedef/services_maint.xml | スコープ外（プロモーションは移植しない） |
| 154 | `removeCategoryMembersOfDiscProducts` | java | `org.apache.ofbiz.product.product.ProductUtilServices#removeCategoryMembersOfDiscProducts` | servicedef/services_maint.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 155 | `removeDuplicateOpenEndedCategoryMembers` | java | `org.apache.ofbiz.product.product.ProductUtilServices#removeDuplicateOpenEndedCategoryMembers` | servicedef/services_maint.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 156 | `removeExpiredCategoryProductMembers` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryServicesScript.groovy#removeExpiredCategoryProductMembers` | servicedef/services_maint.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 157 | `setAllProductImageNames` | java | `org.apache.ofbiz.product.product.ProductUtilServices#setAllProductImageNames` | servicedef/services_maint.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 158 | `migrateProductPromoCodeEmail` | java | `org.apache.ofbiz.product.migrate.MigrationServices#migrateProductPromoCodeEmail` | servicedef/services_migration.xml | スコープ外（プロモーションは移植しない） |
| 159 | `convertPickOrderIdListToHeaders` | java | `org.apache.ofbiz.shipment.picklist.PickListServices#convertOrderIdListToHeaders` | servicedef/services_picklist.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 160 | `calculatePurchasePrice` | java | `org.apache.ofbiz.product.price.PriceServices#calculatePurchasePrice` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 161 | `createBulkProductPromoCode` | java | `org.apache.ofbiz.product.promo.PromoServices#importPromoCodesFromFile` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 162 | `createBulkProductPromoCodeEmail` | java | `org.apache.ofbiz.product.promo.PromoServices#importPromoCodeEmailsFromFile` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 163 | `createProductPriceCond` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/price/PriceServicesScript.groovy#createProductPriceCond` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 164 | `createProductPromoCodeSet` | java | `org.apache.ofbiz.product.promo.PromoServices#createProductPromoCodeSet` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 165 | `createProductPromoCond` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/PromoServicesScript.groovy#createProductPromoCond` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 166 | `getAssociatedPriceRulesConds` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/price/PriceServicesScript.groovy#getAssociatedPriceRulesConds` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 167 | `productPromoActFreeShip` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/ProductPromoActionServices.groovy#productActFreeShip` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 168 | `productPromoActGiftGWP` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/ProductPromoActionServices.groovy#productGWP` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 169 | `productPromoActOrderAmount` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/ProductPromoActionServices.groovy#productOrderAmount` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 170 | `productPromoActOrderPercent` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/ProductPromoActionServices.groovy#productOrderPercent` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 171 | `productPromoActProdAMDISC` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/ProductPromoActionServices.groovy#productAMDISC` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 172 | `productPromoActProdDISC` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/ProductPromoActionServices.groovy#productDISC` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 173 | `productPromoActProdPrice` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/ProductPromoActionServices.groovy#productPrice` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 174 | `productPromoActProdSpecialPrice` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/ProductPromoActionServices.groovy#productSpecialPrice` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 175 | `productPromoActShipCharge` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/ProductPromoActionServices.groovy#productShipCharge` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 176 | `productPromoActTaxPercent` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/ProductPromoActionServices.groovy#productTaxPercent` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 177 | `productPromoCondGeoID` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/ProductPromoCondServices.groovy#productGeoID` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 178 | `productPromoCondListPriceMinAmount` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/ProductPromoCondServices.groovy#productListPriceMinAmount` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 179 | `productPromoCondListPriceMinPercent` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/ProductPromoCondServices.groovy#productListPriceMinPercent` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 180 | `productPromoCondNewACCT` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/ProductPromoCondServices.groovy#productNewACCT` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 181 | `productPromoCondOrderHist` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/ProductPromoCondServices.groovy#productOrderHist` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 182 | `productPromoCondOrderLastYear` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/ProductPromoCondServices.groovy#productOrderLastYear` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 183 | `productPromoCondOrderShipTotal` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/ProductPromoCondServices.groovy#productShipTotal` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 184 | `productPromoCondOrderTotal` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/ProductPromoCondServices.groovy#productOrderTotal` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 185 | `productPromoCondOrderYear` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/ProductPromoCondServices.groovy#productOrderYear` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 186 | `productPromoCondPartyClass` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/ProductPromoCondServices.groovy#productPartyClass` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 187 | `productPromoCondPartyGM` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/ProductPromoCondServices.groovy#productPartyGM` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 188 | `productPromoCondPartyID` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/ProductPromoCondServices.groovy#productPartyID` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 189 | `productPromoCondProductAmount` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/ProductPromoCondServices.groovy#productAmount` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 190 | `productPromoCondProductQuant` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/ProductPromoCondServices.groovy#productQuant` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 191 | `productPromoCondProductTotal` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/ProductPromoCondServices.groovy#productTotal` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 192 | `productPromoCondPromoRecurrence` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/ProductPromoCondServices.groovy#productPromoRecurrence` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 193 | `productPromoCondRoleType` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/ProductPromoCondServices.groovy#productRoleType` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 194 | `updateProductPriceCond` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/price/PriceServicesScript.groovy#updateProductPriceCond` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 195 | `updateProductPromoCond` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/promo/PromoServicesScript.groovy#updateProductPromoCond` | servicedef/services_pricepromo.xml | スコープ外（プロモーションは移植しない） |
| 196 | `addOrderShipmentToShipment` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#addOrderShipmentToShipment` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 197 | `addShipmentContentToPackage` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#addShipmentContentToPackage` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 198 | `balanceItemIssuancesForShipment` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#balanceItemIssuancesForShipment` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 199 | `calcPackSessionAdditionalShippingCharge` | java | `org.apache.ofbiz.shipment.packing.PackingServices#calcPackSessionAdditionalShippingCharge` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 200 | `calcShipmentCostEstimate` | java | `org.apache.ofbiz.shipment.shipment.ShipmentServices#calcShipmentCostEstimate` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 201 | `cancelAllRows` | java | `org.apache.ofbiz.shipment.verify.VerifyPickServices#cancelAllRows` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 202 | `cancelReceivedItems` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentReceiptServices.groovy#cancelReceivedItems` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 203 | `checkCanChangeShipmentStatusDelivered` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#checkCanChangeShipmentStatusDelivered` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 204 | `checkCanChangeShipmentStatusPacked` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#checkCanChangeShipmentStatusPacked` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 205 | `checkCanChangeShipmentStatusShipped` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#checkCanChangeShipmentStatusShipped` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 206 | `checkCancelItemIssuanceAndOrderShipmentFromShipment` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#checkCancelItemIssuanceAndOrderShipmentFromShipment` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 207 | `clearLastPackage` | java | `org.apache.ofbiz.shipment.packing.PackingServices#clearLastPackage` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 208 | `clearPackAll` | java | `org.apache.ofbiz.shipment.packing.PackingServices#clearPackAll` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 209 | `clearPackLine` | java | `org.apache.ofbiz.shipment.packing.PackingServices#clearPackLine` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 210 | `completePack` | java | `org.apache.ofbiz.shipment.packing.PackingServices#completePack` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 211 | `completePackage` | java | `org.apache.ofbiz.shipment.weightPackage.WeightPackageServices#completePackage` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 212 | `completeShipment` | java | `org.apache.ofbiz.shipment.weightPackage.WeightPackageServices#completeShipment` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 213 | `completeVerifiedPick` | java | `org.apache.ofbiz.shipment.verify.VerifyPickServices#completeVerifiedPick` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 214 | `createOrderShipmentPlan` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#createOrderShipmentPlan` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 215 | `createShipmentAndItemsForReturn` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#createShipmentAndItemsForReturn` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 216 | `createShipmentAndItemsForVendorReturn` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#createShipmentAndItemsForVendorReturn` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 217 | `createShipmentEstimate` | java | `org.apache.ofbiz.shipment.shipment.ShipmentServices#createShipmentEstimate` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 218 | `createShipmentForReturn` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#createShipmentForReturn` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 219 | `createShipmentPackage` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#createShipmentPackage` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 220 | `createShipmentReceipt` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentReceiptServices.groovy#createShipmentReceipt` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 221 | `deletePackedLine` | java | `org.apache.ofbiz.shipment.weightPackage.WeightPackageServices#deletePackedLine` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 222 | `deleteShipmentPackage` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#deleteShipmentPackage` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 223 | `duplicateShipmentRouteSegment` | java | `org.apache.ofbiz.shipment.shipment.ShipmentServices#duplicateShipmentRouteSegment` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 224 | `ensureRouteSegPackage` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#ensureRouteSegPackage` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 225 | `getQuantityForShipment` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#getQuantityForShipment` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 226 | `getShipmentPackageValueFromOrders` | java | `org.apache.ofbiz.shipment.shipment.ShipmentServices#getShipmentPackageValueFromOrders` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 227 | `getTotalIssuedQuantityForOrderItem` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/IssuanceServices.groovy#getTotalIssuedQuantityForOrderItem` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 228 | `issueSerializedInvToShipmentPackageAndSetTracking` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#issueSerializedInvToShipmentPackageAndSetTracking` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 229 | `packBulkItems` | java | `org.apache.ofbiz.shipment.packing.PackingServices#packBulk` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 230 | `packSingleItem` | java | `org.apache.ofbiz.shipment.packing.PackingServices#addPackLine` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 231 | `quickDropShipOrder` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#quickDropShipOrder` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 232 | `quickReceivePurchaseOrder` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#quickReceivePurchaseOrder` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 233 | `quickReceiveReturn` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentReceiptServices.groovy#quickReceiveReturn` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 234 | `quickShipOrderByItem` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#quickShipOrderByItem` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 235 | `removeOrderShipmentFromShipment` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#removeOrderShipmentFromShipment` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 236 | `removeShipmentEstimate` | java | `org.apache.ofbiz.shipment.shipment.ShipmentServices#removeShipmentEstimate` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 237 | `savePackagesInfo` | java | `org.apache.ofbiz.shipment.weightPackage.WeightPackageServices#savePackagesInfo` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 238 | `sendShipmentCompleteNotification` | java | `org.apache.ofbiz.shipment.shipment.ShipmentServices#sendShipmentCompleteNotification` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 239 | `sendShipmentScheduledNotification` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#sendShipmentScheduledNotification` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 240 | `setNextPackageSeq` | java | `org.apache.ofbiz.shipment.packing.PackingServices#incrementPackageSeq` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 241 | `setPackageInfo` | java | `org.apache.ofbiz.shipment.weightPackage.WeightPackageServices#setPackageInfo` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 242 | `setShipmentSettingsFromFacilities` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#setShipmentSettingsFromFacilities` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 243 | `setShipmentSettingsFromPrimaryOrder` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#setShipmentSettingsFromPrimaryOrder` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 244 | `setShipmentStatusPackedAndShipped` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#setShipmentStatusPackedAndShipped` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 245 | `splitShipmentItemByQuantity` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#splitShipmentItemByQuantity` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 246 | `updateIssuanceShipmentAndPoOnReceiveInventory` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentReceiptServices.groovy#updateIssuanceShipmentAndPoOnReceiveInventory` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 247 | `updatePackedLine` | java | `org.apache.ofbiz.shipment.weightPackage.WeightPackageServices#updatePackedLine` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 248 | `updatePurchaseShipmentFromReceipt` | java | `org.apache.ofbiz.shipment.shipment.ShipmentServices#updatePurchaseShipmentFromReceipt` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 249 | `updateShipmentPackage` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy#updateShipmentPackage` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 250 | `verifyBulkItem` | java | `org.apache.ofbiz.shipment.verify.VerifyPickServices#verifyBulkItem` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 251 | `verifySingleItem` | java | `org.apache.ofbiz.shipment.verify.VerifyPickServices#verifySingleItem` | servicedef/services_shipment.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 252 | `checkProductStoreGroupRollup` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/store/ProductStoreServices.groovy#checkProductStoreGroupRollup` | servicedef/services_store.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 253 | `createProductStore` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/store/ProductStoreServices.groovy#createProductStore` | servicedef/services_store.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 254 | `isStoreInventoryAvailable` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/store/ProductStoreServices.groovy#isStoreInventoryAvailable` | servicedef/services_store.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 255 | `isStoreInventoryAvailableOrNotRequired` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/store/ProductStoreServices.groovy#isStoreInventoryAvailableOrNotRequired` | servicedef/services_store.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 256 | `isStoreInventoryRequired` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/store/ProductStoreServices.groovy#isStoreInventoryRequired` | servicedef/services_store.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 257 | `productStoreGenericPermission` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/store/ProductStoreServices.groovy#productStoreGenericPermission` | servicedef/services_store.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 258 | `reserveStoreInventory` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/store/ProductStoreServices.groovy#reserveStoreInventory` | servicedef/services_store.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 259 | `updateProductStore` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/store/ProductStoreServices.groovy#updateProductStore` | servicedef/services_store.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 260 | `createSubscription` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/subscription/SubscriptionServicesScript.groovy#createSubscription` | servicedef/services_subscription.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 261 | `getSubscriptionEnt` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/subscription/SubscriptionServicesScript.groovy#getSubscription` | servicedef/services_subscription.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 262 | `isSubscribed` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/subscription/SubscriptionServicesScript.groovy#isSubscribed` | servicedef/services_subscription.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 263 | `processExtendSubscription` | java | `org.apache.ofbiz.product.subscription.SubscriptionServices#processExtendSubscription` | servicedef/services_subscription.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 264 | `processExtendSubscriptionByOrder` | java | `org.apache.ofbiz.product.subscription.SubscriptionServices#processExtendSubscriptionByOrder` | servicedef/services_subscription.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 265 | `processExtendSubscriptionByProduct` | java | `org.apache.ofbiz.product.subscription.SubscriptionServices#processExtendSubscriptionByProduct` | servicedef/services_subscription.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 266 | `runServiceOnSubscriptionExpiry` | java | `org.apache.ofbiz.product.subscription.SubscriptionServices#runServiceOnSubscriptionExpiry` | servicedef/services_subscription.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 267 | `runSubscriptionExpired` | java | `org.apache.ofbiz.product.subscription.SubscriptionServices#runSubscriptionExpired` | servicedef/services_subscription.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 268 | `subscriptionPermissionCheck` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/subscription/SubscriptionServicesScript.groovy#subscriptionPermissionCheck` | servicedef/services_subscription.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 269 | `updateSubscriptionAttribute` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/subscription/SubscriptionServicesScript.groovy#updateSubscriptionAttribute` | servicedef/services_subscription.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 270 | `migrateOldPicklistStatusHistoryToPickListStatus` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/shipment/PicklistServices.groovy#migrateOldPicklistStatusHistoryToPickListStatus` | servicedef/services_upgrade.xml | 主要フロー外（データ移行・アップグレード用） |
| 271 | `convertFeaturesForSupplier` | java | `org.apache.ofbiz.product.supplier.SupplierProductServices#convertFeaturesForSupplier` | servicedef/services_view.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 272 | `getAllProductVariants` | java | `org.apache.ofbiz.product.product.ProductServices#prodFindAllVariants` | servicedef/services_view.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 273 | `getAssociatedProducts` | java | `org.apache.ofbiz.product.product.ProductServices#prodFindAssociatedByType` | servicedef/services_view.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 274 | `getAssociatedProductsList` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/product/category/CategoryServicesScript.groovy#getAssociatedProductsList` | servicedef/services_view.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 275 | `getPreviousNextProducts` | java | `org.apache.ofbiz.product.category.CategoryServices#getPreviousNextProducts` | servicedef/services_view.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 276 | `getProduct` | java | `org.apache.ofbiz.product.product.ProductServices#prodFindProduct` | servicedef/services_view.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 277 | `getProductCategoryAndLimitedMembers` | java | `org.apache.ofbiz.product.category.CategoryServices#getProductCategoryAndLimitedMembers` | servicedef/services_view.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 278 | `getProductCategoryMembers` | java | `org.apache.ofbiz.product.category.CategoryServices#getCategoryMembers` | servicedef/services_view.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 279 | `getProductFeatureSet` | java | `org.apache.ofbiz.product.product.ProductServices#prodFindFeatureTypes` | servicedef/services_view.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 280 | `getProductFeatures` | java | `org.apache.ofbiz.product.product.ProductServices#prodGetFeatures` | servicedef/services_view.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 281 | `getProductVariant` | java | `org.apache.ofbiz.product.product.ProductServices#prodFindSelectedVariant` | servicedef/services_view.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 282 | `getProductVariantTree` | java | `org.apache.ofbiz.product.product.ProductServices#prodMakeFeatureTree` | servicedef/services_view.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |
| 283 | `getSuppliersForProduct` | java | `org.apache.ofbiz.product.supplier.SupplierProductServices#getSuppliersForProduct` | servicedef/services_view.xml | 主要フロー外（スコープ決定により java/groovy は主要フローのみ移植） |

### interface サービス（対象外）

| サービス | servicedef |
|---|---|
| `calcShipmentEstimateInterface` | servicedef/services_shipment.xml |
| `interfaceInventoryTransfer` | servicedef/services_facility.xml |
| `interfaceProduct` | servicedef/services.xml |
| `interfaceProductPromoAction` | servicedef/services_pricepromo.xml |
| `interfaceProductPromoCond` | servicedef/services_pricepromo.xml |
| `interfaceShipmentReceipt` | servicedef/services_shipment.xml |
| `interfaceShipmentReceiptRole` | servicedef/services_shipment.xml |
| `interfaceUomFormula` | servicedef/services_uom.xml |
| `productCostCalcInterface` | servicedef/services_cost.xml |

## 集計（画面・その他）

| 区分 | 件数 | 状態 |
|---|---:|---|
| Widget 定義ファイル | 47 | 未移植（UI は対象外。REST API のみ提供） |
| うち screen | 284 | 未移植 |
| うち form | 194 | 未移植 |
| うち menu | 32 | 未移植 |
| うち tree | 1 | 未移植 |
| うち grid | 138 | 未移植 |
| FTL テンプレート | 98 | 未移植 |
| 画面用 Groovy スクリプト（サービス以外） | 85 | 未移植 |
| controller.xml の request-map（イベントあり） | 487 | 未移植（service/service-multi イベントは呼び出し先サービスの状態に依存） |
| うち event type=service | 433 | 未移植 |
| うち event type=java | 25 | 未移植 |
| うち event type=service-multi | 23 | 未移植 |
| うち event type=groovy | 4 | 未移植 |
| うち event type=simple | 2 | 未移植 |
| view-entity（product 関連） | 55 | 未移植（共通 datamodel は view を生成しない。必要箇所は JPQL で個別実装） |

<details><summary>Widget ファイル別の件数と screen 名（全件）</summary>

| ファイル | screen | form | menu | tree | grid | screen 名 |
|---|---:|---:|---:|---:|---:|---|
| `applications/product/widget/catalog/CatalogMenus.xml` | 0 | 0 | 21 | 0 | 0 |  |
| `applications/product/widget/catalog/CatalogScreens.xml` | 7 | 0 | 0 | 0 | 0 | FindCatalog, EditProdCatalog, EditProdCatalogSection, EditProdCatalogCategories, EditProdCatalogParties, EditProdCatalogStores, ShortcutApp |
| `applications/product/widget/catalog/CategoryForms.xml` | 0 | 10 | 0 | 0 | 6 |  |
| `applications/product/widget/catalog/CategoryScreens.xml` | 15 | 0 | 0 | 0 | 0 | CommonCategoryDecorator, FindCategory, EditCategory, EditCategorySection, EditCategoryContent, EditCategoryContentContent, EditCategoryRollup, EditCategoryProducts, EditCategoryProdCatalogs, EditCategoryFeatureCats, EditCategoryParties, EditCategoryAttributes, createProductInCategoryStart, CreateProductInCategoryCheckExisting, EditProductCategoryLinks |
| `applications/product/widget/catalog/CommonScreens.xml` | 16 | 0 | 0 | 0 | 0 | main-decorator, CommonCatalogDecorator, CommonProductStoreDecorator, CommonProductStoreGroupDecorator, CommonShippingDecorator, leftbar, keywordsearchbox, miniproductlist, ChooseTopCategory, FastLoadCache, categorytree, ProductStoreGroupTree, main, ImageManagementDecorator, listMiniproduct, ProductStoreFacilities |
| `applications/product/widget/catalog/ConfigForms.xml` | 0 | 10 | 0 | 0 | 4 |  |
| `applications/product/widget/catalog/ConfigScreens.xml` | 12 | 0 | 0 | 0 | 0 | CommonConfigDecorator, FindProductConfigItems, EditProductConfigItem, EditProductConfigOptions, configOptions, configComponent, EditProductConfigItemContent, PrepareAddProductConfigItemContentAssoc, AddProductConfigItemContentAssoc, AddProductConfigItemContentAssoc, EditProductConfigItemContentContent, ProductConfigItemArticle |
| `applications/product/widget/catalog/FeatureForms.xml` | 0 | 13 | 0 | 0 | 5 |  |
| `applications/product/widget/catalog/FeatureScreens.xml` | 15 | 0 | 0 | 0 | 0 | CommonFeatureDecorator, EditFeature, EditFeatureTypes, EditFeatureType, EditFeatureInterActions, EditFeatureInterAction, CreateProductFeature, EditFeatureCategories, EditFeatureCategoryFeatures, EditFeatureGroups, EditFeatureGroupAppls, QuickAddProductFeatures, ListFeaturePrice, CreateFeaturePrice, CreateFeature |
| `applications/product/widget/catalog/FieldLookupForms.xml` | 0 | 16 | 0 | 0 | 0 |  |
| `applications/product/widget/catalog/FindScreens.xml` | 5 | 0 | 0 | 0 | 0 | CommonFindDecorator, advancedsearch, keywordsearch, exportproducts, FindProductById |
| `applications/product/widget/catalog/ImageManagementForms.xml` | 0 | 14 | 0 | 0 | 9 |  |
| `applications/product/widget/catalog/ImageManagementScreens.xml` | 23 | 0 | 0 | 0 | 0 | Imagemanagement, ImageGallery, ListImageGallery, ImageUpload, ImageApprove, ListPeopleApproved, ImageRejected, ListPeopleRejected, ImageManage, ListImageManage, ImageFrames, ImageCropping, ImageRotating, ImageShare, ImageReplace, ListImageReplace, ViewImage, ViewPreviewImage, LookupImage, ImageRecentlyApproved, ListImageRecentlyApproved, LookupImageFrame, ImageResize |
| `applications/product/widget/catalog/LookupScreens.xml` | 9 | 0 | 0 | 0 | 0 | LookupProduct, LookupVirtualProduct, LookupVariantProduct, LookupProductAndPrice, LookupProductCategory, LookupProductFeature, LookupProductStore, LookupSupplierProduct, LookupCostComponentCalc |
| `applications/product/widget/catalog/PriceForms.xml` | 0 | 1 | 0 | 0 | 6 |  |
| `applications/product/widget/catalog/PriceScreens.xml` | 3 | 0 | 0 | 0 | 0 | CommonPriceDecorator, FindProductPriceRule, EditProductPriceRules |
| `applications/product/widget/catalog/ProdCatalogForms.xml` | 0 | 6 | 0 | 0 | 5 |  |
| `applications/product/widget/catalog/ProductForms.xml` | 0 | 47 | 0 | 0 | 40 |  |
| `applications/product/widget/catalog/ProductScreens.xml` | 47 | 0 | 0 | 0 | 0 | CommonProductDecorator, FindProduct, EditProduct, EditProductPrices, ViewProductAgreements, CreateSalesAgreement, EditProductCategories, EditProductConfigs, EditProductAssetUsage, showFixedAssetProduct, newFixedAssetProduct, ViewProductManufacturing, EditProductCosts, QuickAddVariants, EditProductQuickAdmin, EditProductFacilities, EditProductFacilityLocations, EditProductKeyword, EditProductInventoryItems, EditProductGoodIdentifications, EditProductGlAccounts, EditProductPaymentMethodTypes, EditProductFeatures, EditSupplierProduct, EditProductContent, EditProductContentContent, EditProductAttributes, EditProductAssoc, ApplyFeaturesFromCategory, CreateVirtualWithVariantsForm, CreateVirtualWithVariantsFormInclude, EditProductMaints, EditProductMeters, EditProductGeos, EditProductSubscriptionResources, EditProductWorkEfforts, ProductBarCode.fo, EditProductParties, EditVendorProduct, BestSellingProducts, ViewProductOrder, EditProductCommunicationEvents, EditCommunicationEvent, ProductPriceHistory, ViewProductGroupOrder, EditProductGroupOrder, EditProductTag |
| `applications/product/widget/catalog/PromoForms.xml` | 0 | 4 | 0 | 0 | 3 |  |
| `applications/product/widget/catalog/PromoScreens.xml` | 8 | 0 | 0 | 0 | 0 | CommonPromoDecorator, FindProductPromo, EditProductPromo, EditProductPromoRules, EditProductPromoStores, FindProductPromoCode, EditProductPromoCode, EditProductPromoContent |
| `applications/product/widget/catalog/ReviewForms.xml` | 0 | 2 | 0 | 0 | 0 |  |
| `applications/product/widget/catalog/ReviewScreens.xml` | 2 | 0 | 0 | 0 | 0 | CommonReviewDecorator, FindReviews |
| `applications/product/widget/catalog/ShippingForms.xml` | 0 | 4 | 0 | 0 | 4 |  |
| `applications/product/widget/catalog/ShippingScreens.xml` | 4 | 0 | 0 | 0 | 0 | ListQuantityBreaks, ListShipmentMethodTypes, ListCarrierShipmentMethods, ListShipmentTimeEstimates |
| `applications/product/widget/catalog/StoreForms.xml` | 0 | 18 | 0 | 0 | 17 |  |
| `applications/product/widget/catalog/StoreScreens.xml` | 21 | 0 | 0 | 0 | 0 | FindProductStore, EditProductStore, FindProductStoreRoles, EditProductStorePromos, EditProductStoreCatalogs, EditProductStoreWebSites, EditProductStoreShipSetup, EditProductStoreShipmentCostEstimates, EditProductStorePaySetup, EditProductStoreEmails, EditProductStoreSurveys, EditProductStoreKeywordOvrd, ViewProductStoreSegments, EditProductStoreFinAccountSettings, EditProductStoreVendorPayments, EditProductStoreVendorShipments, ListProductStoreFacility, EditProductStoreFacility, ListParentProductStoreGroup, EditProductStoreGroup, EditProductStoreGroupAndAssoc |
| `applications/product/widget/catalog/SubscriptionForms.xml` | 0 | 8 | 0 | 0 | 3 |  |
| `applications/product/widget/catalog/SubscriptionScreens.xml` | 9 | 0 | 0 | 0 | 0 | FindSubscription, CommonSubscriptionDecorator, EditSubscription, EditSubscriptionAttributes, FindSubscriptionResource, CommonSubscriptionResourceDecorator, EditSubscriptionResource, EditSubscriptionResourceProducts, EditSubscriptionCommEvent |
| `applications/product/widget/catalog/ThesaurusScreens.xml` | 2 | 0 | 0 | 0 | 0 | CommonThesaurusDecorator, EditKeywordThesaurus |
| `applications/product/widget/facility/CommonScreens.xml` | 4 | 0 | 0 | 0 | 0 | main-decorator, CommonFacilityDecorator, CommonInventoryItemLabelsDecorator, CommonInventoryItemLabelDecorator |
| `applications/product/widget/facility/FacilityForms.xml` | 0 | 15 | 0 | 0 | 18 |  |
| `applications/product/widget/facility/FacilityGroupForms.xml` | 0 | 5 | 0 | 0 | 5 |  |
| `applications/product/widget/facility/FacilityGroupScreens.xml` | 6 | 0 | 0 | 0 | 0 | FindFacilityGroup, CommonFacilityGroupDecorator, EditFacilityGroup, EditFacilityGroupMembers, EditFacilityGroupRoles, EditFacilityGroupRollup |
| `applications/product/widget/facility/FacilityMenus.xml` | 0 | 0 | 11 | 0 | 0 |  |
| `applications/product/widget/facility/FacilityScreens.xml` | 41 | 0 | 0 | 0 | 0 | FindFacility, FacilitySearchResults, EditFacility, FacilityChildren, FindFacilityTransfers, FindFacilityLocation, EditFacilityLocation, EditFacilityInventoryItems, SearchInventoryItemsByLabels, ViewFacilityInventoryByProduct, ViewFacilityInventoryByProductSimple, ViewFacilityInventoryByProductReport, ViewFacilityInventoryItemsDetails, InventoryItemTotals, InventoryItemGrandTotals, InventoryItemTotalsExport, InventoryAverageCosts, EditFacilityGroups, EditFacilityParties, ViewContactMechs, EditContactMech, EditInventoryItem, ViewInventoryItemDetail, EditInventoryItemLabels, FindFacilityPhysicalInventory, TransferInventoryItem, ReceiveInventory, ReceiveReturn, UpdatedInventoryItemStatus, PicklistOptions, PrintPickSheets.fo, ReviewOrdersNotPickedOrPacked, PicklistManage, PickMoveStock, PickMoveStockSimple.fo, PicklistReport.fo, FacilityLocationGeoLocation, EditFacilityContent, EditFacilityGeoPoint, EditFacilityAgreements, ShortcutApp |
| `applications/product/widget/facility/FacilityTrees.xml` | 0 | 0 | 0 | 1 | 0 |  |
| `applications/product/widget/facility/FieldLookupForms.xml` | 0 | 7 | 0 | 0 | 0 |  |
| `applications/product/widget/facility/InventoryForms.xml` | 0 | 3 | 0 | 0 | 5 |  |
| `applications/product/widget/facility/InventoryItemLabelForms.xml` | 0 | 3 | 0 | 0 | 3 |  |
| `applications/product/widget/facility/InventoryItemLabelScreens.xml` | 4 | 0 | 0 | 0 | 0 | FindInventoryItemLabels, EditInventoryItemLabelTypes, EditInventoryItemLabel, EditInventoryItemLabelAppls |
| `applications/product/widget/facility/LookupScreens.xml` | 5 | 0 | 0 | 0 | 0 | LookupFacility, LookupFacilityLocation, LookupShipment, LookupInventoryItem, LookupProductInventoryLocation |
| `applications/product/widget/facility/ReportScreens.xml` | 1 | 0 | 0 | 0 | 0 | InventoryReports |
| `applications/product/widget/facility/ShipmentForms.xml` | 0 | 4 | 0 | 0 | 3 |  |
| `applications/product/widget/facility/ShipmentGatewayConfigForms.xml` | 0 | 4 | 0 | 0 | 2 |  |
| `applications/product/widget/facility/ShipmentGatewayConfigScreens.xml` | 5 | 0 | 0 | 0 | 0 | GenericShipmentGatewayConfigDecorator, FindShipmentGatewayConfig, EditShipmentGatewayConfig, FindShipmentGatewayConfigTypes, EditShipmentGatewayConfigType |
| `applications/product/widget/facility/ShipmentScreens.xml` | 20 | 0 | 0 | 0 | 0 | CommonShipmentMainDecorator, FindShipment, CommonShipmentDecorator, ViewShipment, EditShipment, EditShipmentItems, EditShipmentPlan, EditShipmentPackages, EditShipmentRouteSegments, AddItemsFromOrder, ViewShipmentReceipts, QuickShipOrder, VerifyPick, PackOrder, WeightPackageOnly, PackingSlip.fo, ShipmentBarCode.fo, ShipmentManifest.fo, ReceiveInventoryAgainstPurchaseOrder, AddItemsFromInventory |

</details>

<details><summary>FTL テンプレート（全件）</summary>

- `applications/product/template/FastLoadCache.ftl`
- `applications/product/template/Main.ftl`
- `applications/product/template/category/CategoryTree.ftl`
- `applications/product/template/category/CreateProductInCategoryCheckExisting.ftl`
- `applications/product/template/category/CreateProductInCategoryStart.ftl`
- `applications/product/template/category/EditCategory.ftl`
- `applications/product/template/category/EditCategoryContent.ftl`
- `applications/product/template/category/EditCategoryFeatureCats.ftl`
- `applications/product/template/category/EditCategoryParties.ftl`
- `applications/product/template/category/EditCategoryProdCatalogs.ftl`
- `applications/product/template/category/EditCategoryProducts.ftl`
- `applications/product/template/category/EditCategoryRollup.ftl`
- `applications/product/template/config/EditProductConfigItemContent.ftl`
- `applications/product/template/facility/EditContactMech.ftl`
- `applications/product/template/facility/EditFacility.ftl`
- `applications/product/template/facility/EditFacilityLocation.ftl`
- `applications/product/template/facility/FindFacilityLocation.ftl`
- `applications/product/template/facility/NotReadyToPick.fo.ftl`
- `applications/product/template/facility/PickMoveStock.ftl`
- `applications/product/template/facility/PickMoveStockSimple.fo.ftl`
- `applications/product/template/facility/Picklist.fo.ftl`
- `applications/product/template/facility/PicklistManage.ftl`
- `applications/product/template/facility/PicklistOptions.ftl`
- `applications/product/template/facility/PrintPickSheets.fo.ftl`
- `applications/product/template/facility/ReviewOrdersNotPickedOrPacked.ftl`
- `applications/product/template/facility/SearchInventoryItemsByLabelsForm.ftl`
- `applications/product/template/facility/ViewContactMechs.ftl`
- `applications/product/template/feature/BulkAddFeature.ftl`
- `applications/product/template/feature/EditFeatureCategoryFeatures.ftl`
- `applications/product/template/feature/EditFeatureGroups.ftl`
- `applications/product/template/find/AdvancedSearch.ftl`
- `applications/product/template/find/ExportProducts.ftl`
- `applications/product/template/find/FindProductById.ftl`
- `applications/product/template/find/KeywordSearch.ftl`
- `applications/product/template/find/KeywordSearchActions.ftl`
- `applications/product/template/find/KeywordSearchBox.ftl`
- `applications/product/template/find/MiniProductList.ftl`
- `applications/product/template/find/SideCatalogs.ftl`
- `applications/product/template/find/SideDeepCategory.ftl`
- `applications/product/template/imagemanagement/AddMultipleImages.ftl`
- `applications/product/template/imagemanagement/ImageCrop.ftl`
- `applications/product/template/imagemanagement/ImageFrame.ftl`
- `applications/product/template/imagemanagement/ImageGallery.ftl`
- `applications/product/template/imagemanagement/ImageRecentlyApproved.ftl`
- `applications/product/template/imagemanagement/ImageRotateButton.ftl`
- `applications/product/template/imagemanagement/ImageRotating.ftl`
- `applications/product/template/imagemanagement/ImageShare.ftl`
- `applications/product/template/imagemanagement/ResizeImage.ftl`
- `applications/product/template/imagemanagement/ShowPeopleApprove.ftl`
- `applications/product/template/inventory/ReceiveInventory.ftl`
- `applications/product/template/inventory/TransferInventoryItem.ftl`
- `applications/product/template/lookup/LookupInventoryItems.ftl`
- `applications/product/template/lookup/LookupVariantProduct.ftl`
- `applications/product/template/price/EditProductPriceRules.ftl`
- `applications/product/template/price/SetPriceRulesCondEventJs.ftl`
- `applications/product/template/product/AddAdditionalImages.ftl`
- `applications/product/template/product/ApplyFeaturesFromCategory.ftl`
- `applications/product/template/product/CreateVirtualWithVariantsForm.ftl`
- `applications/product/template/product/EditProductContent.ftl`
- `applications/product/template/product/EditProductFeatures.ftl`
- `applications/product/template/product/EditProductInventoryItems.ftl`
- `applications/product/template/product/EditProductQuickAdmin.ftl`
- `applications/product/template/product/EditVirtualProductInventory.ftl`
- `applications/product/template/product/ProductBarCode.fo.ftl`
- `applications/product/template/product/ProductInventorySummary.ftl`
- `applications/product/template/product/QuickAddVariants.ftl`
- `applications/product/template/product/ViewProductOrders.ftl`
- `applications/product/template/promo/EditProductPromoCode.ftl`
- `applications/product/template/promo/EditProductPromoRules.ftl`
- `applications/product/template/promo/EditProductPromoStores.ftl`
- `applications/product/template/promo/FindProductPromoCode.ftl`
- `applications/product/template/returns/ReceiveReturn.ftl`
- `applications/product/template/returns/UpdatedInventoryItemStatus.ftl`
- `applications/product/template/shipment/AddItemsFromInventory.ftl`
- `applications/product/template/shipment/AddItemsFromOrder.ftl`
- `applications/product/template/shipment/EditShipmentItems.ftl`
- `applications/product/template/shipment/EditShipmentPackages.ftl`
- `applications/product/template/shipment/EditShipmentRouteSegments.ftl`
- `applications/product/template/shipment/PackOrder.ftl`
- `applications/product/template/shipment/PackingSlip.fo.ftl`
- `applications/product/template/shipment/PackingSlipShipmentBarCode.fo.ftl`
- `applications/product/template/shipment/QuickShipOrder.ftl`
- `applications/product/template/shipment/ReceiveInventoryAgainstPurchaseOrder.ftl`
- `applications/product/template/shipment/SelectShipmentMethod.ftl`
- `applications/product/template/shipment/ShipmentBarCode.fo.ftl`
- `applications/product/template/shipment/ShipmentManifest.fo.ftl`
- `applications/product/template/shipment/ShipmentScheduledNotice.ftl`
- `applications/product/template/shipment/VerifyPick.ftl`
- `applications/product/template/shipment/ViewShipmentInfo.ftl`
- `applications/product/template/shipment/ViewShipmentItemInfo.ftl`
- `applications/product/template/shipment/ViewShipmentPackageInfo.ftl`
- `applications/product/template/shipment/ViewShipmentRouteInfo.ftl`
- `applications/product/template/shipment/WeightPackage.ftl`
- `applications/product/template/store/EditProductStoreShipSetup.ftl`
- `applications/product/template/store/EditProductStoreSurveys.ftl`
- `applications/product/template/store/EditProductStoreWebSites.ftl`
- `applications/product/template/store/ProductStoreGroupTree.ftl`
- `applications/product/template/thesaurus/EditKeywordThesaurus.ftl`

</details>

<details><summary>画面用 Groovy スクリプト（全件）</summary>

- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/ChooseTopCategory.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/FastLoadCache.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/GetSupplierInventories.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/PrepareCreateShipMeth.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/PrepareCreateShipmentTimeEstimate.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/category/CategoryTree.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/category/CreateProductInCategoryCheckExisting.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/category/EditCategory.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/category/EditCategoryContentContent.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/category/EditCategoryProducts.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/category/EditCategorySEO.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/config/EditProductConfigItemContent.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/config/EditProductConfigItemContentContent.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/feature/EditFeatureCategoryFeatures.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/feature/EditFeatureGroups.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/feature/QuickAddProductFeatures.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/find/AdvancedSearchOptions.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/find/KeywordSearch.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/find/KeywordSearchBox.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/find/MiniProductList.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/find/SideCatalogs.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/find/SideDeepCategory.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/imagemanagement/CheckAction.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/imagemanagement/CheckRejected.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/imagemanagement/ImageFrame.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/imagemanagement/ImageGallery.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/imagemanagement/ImageRecentlyApproved.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/imagemanagement/ImageUpload.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/imagemanagement/SeoLocales.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/imagemanagement/SetDefaultImage.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/imagemanagement/SortSequenceNum.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/lookup/LookupVariantProduct.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/price/EditProductPriceRules.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/product/ApplyFeaturesFromCategory.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/product/ApplyFeaturesFromGroup.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/product/BestProducts.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/product/EditProductAssoc.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/product/EditProductContent.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/product/EditProductContentContent.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/product/EditProductFeatures.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/product/EditProductInventoryItems.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/product/EditProductQuickAdmin.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/product/EditProductSEO.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/product/QuickAddVariants.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/promo/EditProductPromoCode.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/store/EditProductStorePaySetup.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/store/EditProductStoreSurveys.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/catalog/thesaurus/EditKeywordThesaurus.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/facility/ComputeProductSellThroughData.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/facility/CountFacilityInventoryByProduct.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/facility/EditContactMech.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/facility/EditFacility.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/facility/EditFacilityLocation.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/facility/FacilityLocationGeoLocation.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/facility/FindFacility.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/facility/FindFacilityLocation.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/facility/FindFacilityTransfers.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/facility/FindInventoryItemsByLabels.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/facility/ViewContactMechs.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/facility/ViewFacilityInventoryByProduct.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/inventory/FindFacilityPhysicalInventory.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/inventory/InventoryAverageCosts.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/inventory/InventoryItemTotals.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/inventory/LookupInventoryItems.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/inventory/PhysicalInventoryVariance.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/inventory/ReceiveInventory.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/inventory/TransferInventoryItem.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/returns/ReceiveReturn.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/shipment/AddItemsFromInventory.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/shipment/AddItemsFromOrder.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/shipment/EditShipment.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/shipment/EditShipmentItems.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/shipment/EditShipmentPackages.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/shipment/EditShipmentPlan.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/shipment/EditShipmentRouteSegments.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/shipment/PackOrder.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/shipment/PackingSlip.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/shipment/PrintPickSheets.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/shipment/QuickShipOrder.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/shipment/ReceiveInventoryAgainstPurchaseOrder.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/shipment/ReviewOrdersNotPickedOrPacked.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/shipment/ShipmentManifest.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/shipment/VerifyPick.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/shipment/ViewShipment.groovy`
- `applications/product/src/main/groovy/org/apache/ofbiz/product/facility/shipment/WeightPackage.groovy`

</details>

<details><summary>controller.xml の java/groovy/simple イベント（全件）</summary>

| webapp | uri | type | path | invoke |
|---|---|---|---|---|
| catalog | `chain` | java | `org.apache.ofbiz.webapp.event.TestEvent` | `test` |
| catalog | `searchRemoveFromCategory` | java | `org.apache.ofbiz.product.product.ProductSearchEvents` | `searchRemoveFromCategory` |
| catalog | `searchExpireFromCategory` | java | `org.apache.ofbiz.product.product.ProductSearchEvents` | `searchExpireFromCategory` |
| catalog | `searchAddToCategory` | java | `org.apache.ofbiz.product.product.ProductSearchEvents` | `searchAddToCategory` |
| catalog | `searchAddFeature` | java | `org.apache.ofbiz.product.product.ProductSearchEvents` | `searchAddFeature` |
| catalog | `searchRemoveFeature` | java | `org.apache.ofbiz.product.product.ProductSearchEvents` | `searchRemoveFeature` |
| catalog | `searchExportProductList` | java | `org.apache.ofbiz.product.product.ProductSearchEvents` | `searchExportProductList` |
| catalog | `UpdateAllKeywords` | java | `org.apache.ofbiz.product.product.ProductEvents` | `updateAllKeywords` |
| catalog | `updateProductQuickAdminShipping` | java | `org.apache.ofbiz.product.product.ProductEvents` | `updateProductQuickAdminShipping` |
| catalog | `updateProductQuickAdminSelFeat` | java | `org.apache.ofbiz.product.product.ProductEvents` | `updateProductQuickAdminSelFeat` |
| catalog | `updateProductQuickAdminDelFeatureTypes` | java | `org.apache.ofbiz.product.product.ProductEvents` | `removeFeatureApplsByFeatureTypeId` |
| catalog | `quickAdminUpdateProductAssoc` | java | `org.apache.ofbiz.product.product.ProductEvents` | `updateProductAssoc` |
| catalog | `quickAdminRemoveProductFeature` | java | `org.apache.ofbiz.product.product.ProductEvents` | `removeProductFeatureAppl` |
| catalog | `quickAdminAddCategories` | java | `org.apache.ofbiz.product.product.ProductEvents` | `addProductToCategories` |
| catalog | `quickAdminUnPublish` | java | `org.apache.ofbiz.product.product.ProductEvents` | `updateProductCategoryMember` |
| catalog | `quickAdminApplyFeatureToProduct` | java | `org.apache.ofbiz.product.product.ProductEvents` | `addProductFeatures` |
| catalog | `QuickAddChosenVariant` | java | `org.apache.ofbiz.product.product.VariantEvents` | `quickAddChosenVariant` |
| catalog | `getProductStoreGroupRollupHierarchy` | java | `org.apache.ofbiz.product.store.ProductStoreEvents` | `getChildProductStoreGroupTree` |
| catalog | `prepareCreateShipMeth` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/catalog/PrepareCreateShipMeth.groovy` | `` |
| catalog | `prepareCreateShipmentTimeEstimate` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/catalog/PrepareCreateShipmentTimeEstimate.groovy` | `` |
| catalog | `setDefaultImage` | simple | `component://product/minilang/product/imagemanagement/ImageManagementEvents.xml` | `setDefaultImage` |
| catalog | `uploadFrame` | java | `org.apache.ofbiz.product.imagemanagement.FrameImage` | `uploadFrame` |
| catalog | `previewFrameImage` | java | `org.apache.ofbiz.product.imagemanagement.FrameImage` | `previewFrameImage` |
| catalog | `checkAction` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/catalog/imagemanagement/CheckAction.groovy` | `` |
| catalog | `checkRejected` | groovy | `component://product/src/main/groovy/org/apache/ofbiz/product/catalog/imagemanagement/CheckRejected.groovy` | `` |
| catalog | `chooseFrameImage` | java | `org.apache.ofbiz.product.imagemanagement.FrameImage` | `chooseFrameImage` |
| catalog | `getChild` | java | `org.apache.ofbiz.product.category.CategoryServices` | `getChildCategoryTree` |
| facility | `checkForceShipmentReceived` | java | `org.apache.ofbiz.shipment.shipment.ShipmentEvents` | `checkForceShipmentReceived` |
| facility | `createFacilityContent` | simple | `component://product/minilang/product/storage/FacilityEvents.xml` | `createFacilityContent` |
| facility | `viewShipmentPackageRouteSegLabelImage` | java | `org.apache.ofbiz.shipment.shipment.ShipmentEvents` | `viewShipmentPackageRouteSegLabelImage` |
| facility | `viewShipmentLabel` | java | `org.apache.ofbiz.shipment.shipment.ShipmentEvents` | `viewShipmentPackageRouteSegLabelImage` |

</details>

<details><summary>view-entity（全件）</summary>

| view-entity | 定義ファイル |
|---|---|
| `ContentApprovalProductContentAndInfo` | `applications/product/entitydef/entitymodel_view.xml` |
| `ProductCategoryMemberAndPrice` | `applications/product/entitydef/entitymodel_view.xml` |
| `ProductStoreGroupAndMember` | `applications/product/entitydef/entitymodel_view.xml` |
| `ProductStoreGroupRollupAndChild` | `applications/product/entitydef/entitymodel_view.xml` |
| `ProductAndCategoryMember` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `ProductCategoryAndMember` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `ProductCategoryContentAndInfo` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `ProductCategoryMemberAndRole` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `ProductCategoryRollupAndChild` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `ProductCategoryRollupAndRole` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `ProductConfigAndProduct` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `ContainerAndGeoPoint` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `FacilityAndContactMech` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `FacilityLocationAndGeoPoint` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `FacilityContentDetail` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `ProductFacilityAndPostalAddress` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `ProductFacilityLocationQuantityTest` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `ProductFacilityLocationView` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `ProductFeatureAndAppl` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `ProductFeatureGroupAndAppl` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `ProdFeaGrpAppAndProdFeaApp` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `BalanceInventoryItemsView` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `InventoryItemAndLocation` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `InventoryItemAndDetail` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `InventoryItemDetailSummary` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `InventoryItemDetailForSum` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `InventoryItemStatusForCount` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `PhysicalInventoryAndVariance` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `GoodIdentificationAndProduct` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `ProductAndAssoc` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `ProductAndAssocTo` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `ProductContentAndInfo` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `ProductInventoryItem` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `ProductVirtualAndAssocPrices` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `ProductVirtualAndVariantInfo` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `ProductAndPriceView` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `ProductPromoCodeEmailParty` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `ProductPromoUseCheck` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `ProductStoreFacilityByOrder` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `ProductStorePromoAndAppl` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `ProductStoreShipmentMethView` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `SubscriptionAndCommEvent` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `SupplierProductAndProduct` | `applications/datamodel/entitydef/product-entitymodel.xml` |
| `ItemIssuanceAndInventoryItem` | `applications/datamodel/entitydef/shipment-entitymodel.xml` |
| `PicklistAndBinAndItem` | `applications/datamodel/entitydef/shipment-entitymodel.xml` |
| `PicklistAndBin` | `applications/datamodel/entitydef/shipment-entitymodel.xml` |
| `PicklistItemAndBin` | `applications/datamodel/entitydef/shipment-entitymodel.xml` |
| `ShipmentReceiptAndItem` | `applications/datamodel/entitydef/shipment-entitymodel.xml` |
| `CarrierAndShipmentMethod` | `applications/datamodel/entitydef/shipment-entitymodel.xml` |
| `OrderShipmentInfoSummary` | `applications/datamodel/entitydef/shipment-entitymodel.xml` |
| `ShipmentAndItem` | `applications/datamodel/entitydef/shipment-entitymodel.xml` |
| `ShipmentManifestView` | `applications/datamodel/entitydef/shipment-entitymodel.xml` |
| `ShipmentPackageRouteDetail` | `applications/datamodel/entitydef/shipment-entitymodel.xml` |
| `ShipmentRouteSegmentDetail` | `applications/datamodel/entitydef/shipment-entitymodel.xml` |
| `PackedQtyVsOrderItemQuantity` | `applications/datamodel/entitydef/shipment-entitymodel.xml` |

</details>
<!-- END verbatim: /home/ubuntu/work/product/unported_tables.md -->
