# Product Simple Service 依存関係

この一覧は product の service definition と Minilang の `<simple-method>` を照合したものです。行番号は移植対象メソッドの開始位置です。

`LocalProductUnportedAdapter` を経由するサービスは、未移植の処理を実行せず HTTP 501 (`"<service> is not ported"`) を返します。表のうち `DB-only` とした2サービス、および権限確認サービス以外はこの状態です。通知専用の no-op はありません。

| Service | Minilang source | Owner | 主な service / entity 依存 | Local adapter |
|---|---|---|---|---|
| `createCustomerDigitalDownloadProduct` | `CustomerDigitalDownloadServices.xml:23` | CATALOG | Product, ProductPrice, SupplierProduct, ProductStore, ProductCategoryMember | 501 |
| `updateCustomerDigitalDownloadProduct` | `CustomerDigitalDownloadServices.xml:68` | CATALOG | Product, ProductPrice, SupplierProduct | 501 |
| `deleteCustomerDigitalDownloadProduct` | `CustomerDigitalDownloadServices.xml:112` | CATALOG | SupplierProduct, ProductCategoryMember | 501 |
| `addCustomerDigitalDownloadProductFile` | `CustomerDigitalDownloadServices.xml:143` | CATALOG | SupplierProduct, ContentRole, ProductContent | 501 |
| `removeCustomerDigitalDownloadProductFile` | `CustomerDigitalDownloadServices.xml:178` | CATALOG | SupplierProduct, ProductContent | 501 |
| `createInventoryItemCheckSetAtpQoh` | `InventoryServices.xml:25` | FACILITY | `createInventoryItemDetail` | 501 |
| `updateInventoryItem` | `InventoryServices.xml:42` | FACILITY | InventoryItem, Lot, `createInventoryItemDetail` | 501 |
| `createInventoryItemStatus` | `InventoryServices.xml:86` | FACILITY | InventoryItem, InventoryItemStatus | 501 |
| `createInventoryItemDetail` | `InventoryServices.xml:115` | FACILITY | InventoryItemDetail, ItemIssuance | 501 |
| `updateInventoryItemFromDetail` | `InventoryServices.xml:141` | FACILITY | InventoryItem, InventoryItemDetailSummary | 501 |
| `updateSerializedInventoryTotals` | `InventoryServices.xml:151` | FACILITY | InventoryItem | 501 |
| `checkProductInventoryDiscontinuation` | `InventoryServices.xml:214` | FACILITY | `discontinueProductSales`, `getProductInventoryAvailable` | 501 |
| `getProductInventoryAvailable` | `InventoryServices.xml:325` | FACILITY | InventoryItem, Facility, OrderHeader, Product | 501 |
| `getInventoryAvailableByFacility` | `InventoryServices.xml:325` | FACILITY | `getProductInventoryAvailable` | 501 |
| `getInventoryAvailableByLocation` | `InventoryServices.xml:325` | FACILITY | `getProductInventoryAvailable` | 501 |
| `getInventoryAvailableByContainer` | `InventoryServices.xml:325` | FACILITY | `getProductInventoryAvailable` | 501 |
| `getInventoryAvailableByItem` | `InventoryServices.xml:325` | FACILITY | `getProductInventoryAvailable` | 501 |
| `getProductInventoryAvailableBySupplier` | `InventoryServices.xml:325` | FACILITY | `getProductInventoryAvailable` | 501 |
| `createInventoryItemVariance` | `InventoryServices.xml:280` | FACILITY | InventoryItem, InventoryItemVariance, `createInventoryItemDetail` | 501 |
| `createPhysicalInventoryAndVariance` | `InventoryServices.xml:311` | FACILITY | `createPhysicalInventory`, `createInventoryItemVariance` | 501 |
| `countProductInventoryOnHand` | `InventoryServices.xml:400` | FACILITY | InventoryItem | 501 |
| `countProductInventoryShippedForSales` | `InventoryServices.xml:414` | FACILITY | InventoryItem | 501 |
| `getMktgPackagesAvailable` | `InventoryServices.xml:434` | FACILITY | Product, `getAssociatedProducts`, `getProductInventoryAvailableFromAssocProducts` | 501 |
| `balanceInventoryItems` | `InventoryServices.xml:476` | FACILITY | InventoryItem, `reassignInventoryReservations`, `reassignInventoryReservationsByAllocationPlan` | 501 |
| `reassignInventoryReservations` | `InventoryServices.xml:498` | FACILITY | OrderHeader, `cancelOrderItemShipGrpInvRes`, `checkOrderIsOnBackOrder`, `reserveProductInventoryByFacility` | 501 |
| `reassignInventoryReservationsByAllocationPlan` | `InventoryServices.xml:614` | FACILITY | OrderHeader, OrderItem, OrderItemShipGroup, `checkOrderIsOnBackOrder`, `reserveProductInventoryByFacility` | 501 |
| `balanceOrderItemsWithNegativeReservations` | `InventoryServices.xml:706` | FACILITY | OrderHeader, `reassignInventoryReservations` | 501 |
| `createInventoryTransfer` | `InventoryServices.xml:740` | FACILITY | InventoryTransfer | 501 |
| `updateInventoryTransfer` | `InventoryServices.xml:749` | FACILITY | StatusValidChange | 501 |
| `createInventoryTransfersForProduct` | `InventoryServices.xml:770` | FACILITY | Product, Facility, `createInventoryTransfer` | 501 |
| `changeOwnerUponIssuance` | `InventoryServices.xml:879` | FACILITY | ItemIssuance, ProductStore, `updateInventoryItem` | 501 |
| `setOrderReservationPriority` | `InventoryServices.xml:914` | FACILITY | OrderHeader, OrderItemShipGrpInvRes, `reassignInventoryReservations` | 501 |
| `setLastInventoryCount` | `InventoryServices.xml:953` | FACILITY | InventoryItem, ProductFacility, `getInventoryAvailableByFacility`, `updateProductFacility` | 501 |
| `reserveProductInventory` | `InventoryReserveServices.xml:23` | FACILITY | Product, Facility, InventoryItem, OrderHeader, UserLogin, WorkEffort; inventory/order services | 501 |
| `reserveProductInventoryByFacility` | `InventoryReserveServices.xml:23` | FACILITY | Product, Facility, InventoryItem, OrderHeader, UserLogin, WorkEffort; inventory/order services | 501 |
| `reserveProductInventoryByContainer` | `InventoryReserveServices.xml:23` | FACILITY | Product, Facility, InventoryItem, OrderHeader, UserLogin, WorkEffort; inventory/order services | 501 |
| `reserveAnInventoryItem` | `InventoryReserveServices.xml:350` | FACILITY | InventoryItem; order reservation and inventory-detail services | 501 |
| `reserveOrderItemInventory` | `InventoryReserveServices.xml:603` | FACILITY | OrderItem, OrderItemShipGrpInvRes | 501 |
| `cancelOrderInventoryReservation` | `InventoryReserveServices.xml:636` | FACILITY | `cancelOrderItemShipGrpInvRes`, `checkDecomposeInventoryItem` | 501 |
| `cancelOrderItemInvResQty` | `InventoryReserveServices.xml:665` | FACILITY | `cancelOrderInventoryReservation`, `cancelOrderItemShipGrpInvRes`, `checkDecomposeInventoryItem` | 501 |
| `cancelOrderItemShipGrpInvRes` | `InventoryReserveServices.xml:710` | FACILITY | OrderItemShipGrpInvRes, `createInventoryItemDetail` | 501 |
| `createFacilityContactMech` | `FacilityContactMechServices.xml:23` | FACILITY | FacilityContactMech, FacilityContactMechPurpose, `createContactMech` | 501 |
| `updateFacilityContactMech` | `FacilityContactMechServices.xml:58` | FACILITY | FacilityContactMech, `updateContactMech` | 501 |
| `deleteFacilityContactMech` | `FacilityContactMechServices.xml:126` | FACILITY | FacilityContactMech | 501 |
| `createFacilityPostalAddress` | `FacilityContactMechServices.xml:145` | FACILITY | `createFacilityContactMech`, `createGeoPoint`, `createPostalAddress` | 501 |
| `updateFacilityPostalAddress` | `FacilityContactMechServices.xml:173` | FACILITY | FacilityContactMech, `createGeoPoint`, `updateFacilityContactMech`, `updatePostalAddress` | 501 |
| `createFacilityTelecomNumber` | `FacilityContactMechServices.xml:202` | FACILITY | `createFacilityContactMech`, `createTelecomNumber` | 501 |
| `updateFacilityTelecomNumber` | `FacilityContactMechServices.xml:221` | FACILITY | FacilityContactMech, `updateFacilityContactMech`, `updateTelecomNumber` | 501 |
| `createFacilityEmailAddress` | `FacilityContactMechServices.xml:243` | FACILITY | `createFacilityContactMech` | 501 |
| `updateFacilityEmailAddress` | `FacilityContactMechServices.xml:263` | FACILITY | `updateFacilityContactMech` | 501 |
| `createFacilityContactMechPurpose` | `FacilityContactMechServices.xml:283` | FACILITY | FacilityContactMechPurpose | 501 |
| `deleteFacilityContactMechPurpose` | `FacilityContactMechServices.xml:310` | FACILITY | FacilityContactMechPurpose | 501 |
| `findStockMovesNeeded` | `StockMoveServices.xml:28` | FACILITY | FacilityLocation, InventoryItem, OrderItemShipGrpInvResAndItemLocation, Product, ProductFacilityLocationView | 501 |
| `findStockMovesRecommended` | `StockMoveServices.xml:192` | FACILITY | InventoryItemAndLocation, ProductFacilityLocationQuantityTest | 501 |
| `processPhysicalStockMove` | `StockMoveServices.xml:343` | FACILITY | InventoryItem, `createInventoryItem`, `createInventoryItemDetail`, `processOisgirMoveByStatusInline` | 501 |
| `findProductInventorylocations` | `StockMoveServices.xml:625` | FACILITY | Product inventory-location entities | 501 |
| `createInventoryItemLabelAppl` | `InventoryServices.xml:976` | FACILITY | InventoryItemLabel, InventoryItemLabelAppl | 501 |
| `facilityPermissionCheck` | `PermissionServices.xml:24` | FACILITY | `genericBasePermissionCheck` | Product authority check |
| `findOrdersToPickMove` | `PicklistServices.xml:23` | FACILITY | OrderHeader, OrderItemAndShipGroupAssoc, OrderItemShipGroup, OrderItemShipGrpInvRes | 501 |
| `createPicklistFromOrders` | `PicklistServices.xml:615` | FACILITY | PicklistBin, `createPicklist`, `createPicklistBin`, `createPicklistItem`, `findOrdersToPickMove`, `assembleOrderHeaderInfoInline` | 501 |
| `printPickSheets` | `PicklistServices.xml:716` | FACILITY | `findOrdersToPickMove`, `updateOrderHeader` | 501 |
| `getPicklistDisplayInfo` | `PicklistServices.xml:973` | FACILITY | `getPicklistSingleInfoInline` | 501 |
| `getPickAndPackReportInfo` | `PicklistServices.xml:1015` | FACILITY | FacilityLocation, Picklist, Product, `getPicklistSingleInfoInline` | 501 |
| `checkPicklistBinItemStatuses` | `PicklistServices.xml:1255` | FACILITY | Picklist, PicklistBin, PicklistItemAndBin | 501 |
| `editPicklistItem` | `PicklistServices.xml:768` | FACILITY | OrderItemShipGrpInvRes, PicklistItem, reservation and picklist services | 501 |
| `setPicklistItemToComplete` | `PicklistServices.xml:964` | FACILITY | `updatePicklistItem` | 501 |
| `cancelPicklistAndItems` | `PicklistServices.xml:1304` | FACILITY | PicklistBin, PicklistItem | 501 |
| `createFixedAssetAndLinkToProduct` | `RentalServices.xml:24` | FACILITY | `createFixedAsset`, `addFixedAssetProduct` | 501 |
| `getProductFirstRelatedFixedAsset` | `RentalServices.xml:52` | FACILITY | Product, FixedAssetProduct | DB-only |
| `createItemIssuance` | `IssuanceServices.xml:24` | FACILITY | ItemIssuance, Product, `updateInventoryItem` | 501 |
| `issueOrderItemToShipment` | `IssuanceServices.xml:65` | FACILITY | OrderHeader, OrderItem, Shipment; issuance and shipment services | 501 |
| `issueOrderItemShipGrpInvResToShipment` | `IssuanceServices.xml:91` | FACILITY | InventoryItem, Order*, Shipment*, reservation and issuance services | 501 |
| `issueInventoryItemToFixedAssetMaint` | `IssuanceServices.xml:505` | FACILITY | FixedAssetMaint, InventoryItem, `createInventoryItemDetail`, `createItemIssuance` | 501 |
| `returnInventoryItemIssuedToFixedAssetMaint` | `IssuanceServices.xml:550` | FACILITY | ItemIssuance, `createInventoryItemDetail`, `updateItemIssuance` | 501 |
| `cancelOrderItemIssuanceFromSalesShipment` | `IssuanceServices.xml:572` | FACILITY | ItemIssuance, ProductStore, shipment status and reservation services | 501 |
| `issueInventoryItemToShipment` | `IssuanceServices.xml:669` | FACILITY | InventoryItem, ItemIssuance, ReturnItemShipment, ShipmentItem | 501 |
| `convertUomProduct` | `ProductUomFormulas.xml:39` | FACILITY | Product, Uom | DB-only |

`convertUomProduct` ports the product-based calculation and reads Product/Uom locally. `getProductFirstRelatedFixedAsset` reads Product and FixedAssetProduct locally. No other service is treated as DB-only. Notification-only asynchronous calls were not identified among these methods, so no notification no-op adapter is registered.
