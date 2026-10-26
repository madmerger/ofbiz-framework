# Order Spring Boot 未移植棚卸し

本ファイルは `migration/order-springboot/scripts/gen_unported.py` で生成。

## 区分別件数

| 区分 | 件数 |
| --- | ---: |
| ECA | 111 |
| Groovy screen action | 75 |
| controller | 137 |
| minilang event | 10 |
| view-entity | 53 |
| サービス | 221 |
| テンプレート | 126 |
| 画面 | 367 |

## 一覧

| 区分 | 名称 | 種別 | location | invoke | 定義ファイル | 理由 |
| --- | --- | --- | --- | --- | --- | --- |
| ECA | OrderHeader / create-store | eca |  |  | applications/order/entitydef/eecas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | OrderItem / create-store | eca |  |  | applications/order/entitydef/eecas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | OrderPaymentPreference / create-store | eca |  |  | applications/order/entitydef/eecas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | appendOrderItem | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | appendOrderItem | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | cancelOrderItem | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | changeAllocationPlanStatus | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | changeAllocationPlanStatus | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | changeOrderItemStatus | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | changeOrderItemStatus | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | changeOrderItemStatus | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | changeOrderItemStatus | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | changeOrderItemStatus | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | changeOrderItemStatus | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | changeOrderItemStatus | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | changeOrderStatus | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | changeOrderStatus | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | changeOrderStatus | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | changeOrderStatus | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | changeOrderStatus | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | changeOrderStatus | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | changeOrderStatus | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | createCustRequest | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | createCustRequestAttribute | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | createCustRequestContent | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | createCustRequestContent | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | createCustRequestItem | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | createCustRequestItem | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | createCustRequestItemNote | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | createCustRequestItemNote | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | createCustRequestParty | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | createCustRequestParty | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | createItemIssuance | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | createOrderAdjustment | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | createOrderDeliverySchedule | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | createPaymentFromPreference | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | createQuoteRole | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | createRequirement | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | createRequirement | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | createRequirement | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | createRequirementRole | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | createReturnHeader | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | createReturnItem | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | createSalesOpportunity | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | createSalesOpportunity | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | createShoppingList | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | createShoppingList | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | createShoppingListItem | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | createUpdateCustomerAndShippingAddress | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | deleteCustRequestContent | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | deleteCustRequestContent | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | deleteCustRequestParty | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | deleteCustRequestParty | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | deleteOrderAdjustment | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | deleteOrderAdjustment | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | expireCustRequestContent | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | expireCustRequestContent | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | expireCustRequestParty | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | expireCustRequestParty | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | processCreditReturn | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | processRefundReturn | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | processReplacementReturn | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | receiveInventoryProduct | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | receiveInventoryProduct | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | reserveOrderItemInventory | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | sendOrderBackorderNotification | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | sendOrderChangeNotification | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | sendOrderCompleteNotification | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | sendOrderConfirmation | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | sendOrderPayRetryNotification | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | setCustRequestStatus | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | setCustRequestStatus | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | storeOrder | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | storeOrder | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | storeOrder | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | storeOrder | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | storeOrder | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | storeOrder | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | storeOrder | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | storeOrder | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | storeOrder | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateCustRequest | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateCustRequestAttribute | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateCustRequestItem | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateCustRequestItem | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateCustRequestParty | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateCustRequestParty | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateItemIssuance | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateOrderAdjustment | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateOrderDeliverySchedule | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateOrderItems | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateOrderItems | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateOrderItems | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateOrderItems | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateRequirement | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateReturnHeader | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateReturnHeader | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateReturnHeader | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateReturnHeader | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateReturnHeader | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateReturnHeader | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateReturnHeader | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateReturnHeader | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateReturnItem | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateReturnItem | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateReturnStatusFromReceipt | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateSalesOpportunity | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateSalesOpportunity | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateShipGroupShipInfo | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateShoppingList | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| ECA | updateShoppingList | eca |  |  | applications/order/servicedef/secas.xml | ECA は実行せず、必要な副作用は移植サービスで明示的に処理 |
| Groovy screen action | AddGiftCertificates | groovy | src/main/groovy/org/apache/ofbiz/order/entry/AddGiftCertificates.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/AddGiftCertificates.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | AddOrderAttachments | groovy | src/main/groovy/org/apache/ofbiz/order/order/AddOrderAttachments.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/order/AddOrderAttachments.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | AdditionalPartyListing | groovy | src/main/groovy/org/apache/ofbiz/order/entry/AdditionalPartyListing.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/AdditionalPartyListing.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | AdvancedSearchOptions | groovy | src/main/groovy/org/apache/ofbiz/order/entry/catalog/AdvancedSearchOptions.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/catalog/AdvancedSearchOptions.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | ApprovedProductRequirements | groovy | src/main/groovy/org/apache/ofbiz/order/requirement/ApprovedProductRequirements.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/requirement/ApprovedProductRequirements.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | ApprovedProductRequirementsByVendor | groovy | src/main/groovy/org/apache/ofbiz/order/requirement/ApprovedProductRequirementsByVendor.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/requirement/ApprovedProductRequirementsByVendor.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | BillSettings | groovy | src/main/groovy/org/apache/ofbiz/order/entry/BillSettings.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/BillSettings.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | Category | groovy | src/main/groovy/org/apache/ofbiz/order/entry/catalog/Category.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/catalog/Category.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | CategoryDetail | groovy | src/main/groovy/org/apache/ofbiz/order/entry/catalog/CategoryDetail.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/catalog/CategoryDetail.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | CheckInits | groovy | src/main/groovy/org/apache/ofbiz/order/entry/CheckInits.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/CheckInits.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | CheckoutMapProcs | groovy | src/main/groovy/org/apache/ofbiz/order/customer/CheckoutMapProcs.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/customer/CheckoutMapProcs.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | CheckoutOptions | groovy | src/main/groovy/org/apache/ofbiz/order/entry/CheckoutOptions.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/CheckoutOptions.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | CheckoutPayment | groovy | src/main/groovy/org/apache/ofbiz/order/entry/CheckoutPayment.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/CheckoutPayment.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | CheckoutReview | groovy | src/main/groovy/org/apache/ofbiz/order/entry/CheckoutReview.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/CheckoutReview.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | CheckoutShippingAddress | groovy | src/main/groovy/org/apache/ofbiz/order/entry/CheckoutShippingAddress.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/CheckoutShippingAddress.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | ChooseCatalog | groovy | src/main/groovy/org/apache/ofbiz/order/entry/catalog/ChooseCatalog.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/catalog/ChooseCatalog.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | CompanyHeader | groovy | src/main/groovy/org/apache/ofbiz/order/order/CompanyHeader.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/order/CompanyHeader.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | CompareProducts | groovy | src/main/groovy/org/apache/ofbiz/order/entry/catalog/CompareProducts.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/catalog/CompareProducts.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | CreateAllocationPlan | groovy | src/main/groovy/org/apache/ofbiz/order/allocationplan/CreateAllocationPlan.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/allocationplan/CreateAllocationPlan.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | FilterOrderList | groovy | src/main/groovy/org/apache/ofbiz/order/order/FilterOrderList.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/order/FilterOrderList.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | FindOrders | groovy | src/main/groovy/org/apache/ofbiz/order/order/FindOrders.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/order/FindOrders.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | GetNextSequenceNum | groovy | src/main/groovy/org/apache/ofbiz/order/request/GetNextSequenceNum.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/request/GetNextSequenceNum.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | GetPartyAddress | groovy | src/main/groovy/org/apache/ofbiz/order/quote/GetPartyAddress.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/quote/GetPartyAddress.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | GetPartyEmailAddress | groovy | src/main/groovy/org/apache/ofbiz/order/quote/GetPartyEmailAddress.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/quote/GetPartyEmailAddress.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | InlineProductDetail | groovy | src/main/groovy/org/apache/ofbiz/order/entry/catalog/InlineProductDetail.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/catalog/InlineProductDetail.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | KeywordSearch | groovy | src/main/groovy/org/apache/ofbiz/order/entry/catalog/KeywordSearch.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/catalog/KeywordSearch.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | KeywordSearchOptions | groovy | src/main/groovy/org/apache/ofbiz/order/entry/catalog/KeywordSearchOptions.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/catalog/KeywordSearchOptions.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | ListAllocationPlan | groovy | src/main/groovy/org/apache/ofbiz/order/allocationplan/ListAllocationPlan.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/allocationplan/ListAllocationPlan.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | LookupAssociatedProducts | groovy | src/main/groovy/org/apache/ofbiz/order/lookup/LookupAssociatedProducts.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/lookup/LookupAssociatedProducts.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | LookupBulkAddProducts | groovy | src/main/groovy/org/apache/ofbiz/order/entry/cart/LookupBulkAddProducts.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/cart/LookupBulkAddProducts.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | LookupBulkAddSupplierProducts | groovy | src/main/groovy/org/apache/ofbiz/order/entry/cart/LookupBulkAddSupplierProducts.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/cart/LookupBulkAddSupplierProducts.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | ManageQuotePrices | groovy | src/main/groovy/org/apache/ofbiz/order/quote/ManageQuotePrices.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/quote/ManageQuotePrices.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | NewNote | groovy | src/main/groovy/org/apache/ofbiz/order/order/NewNote.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/order/NewNote.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | OpenOrderItemsReport | groovy | src/main/groovy/org/apache/ofbiz/order/reports/OpenOrderItemsReport.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/reports/OpenOrderItemsReport.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | OptionSettings | groovy | src/main/groovy/org/apache/ofbiz/order/entry/OptionSettings.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/OptionSettings.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | OrderAgreements | groovy | src/main/groovy/org/apache/ofbiz/order/entry/OrderAgreements.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/OrderAgreements.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | OrderDeliveryScheduleInfo | groovy | src/main/groovy/org/apache/ofbiz/order/order/OrderDeliveryScheduleInfo.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/order/OrderDeliveryScheduleInfo.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | OrderHistory | groovy | src/main/groovy/org/apache/ofbiz/order/order/OrderHistory.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/order/OrderHistory.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | OrderList | groovy | src/main/groovy/org/apache/ofbiz/order/order/OrderList.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/order/OrderList.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | OrderStats | groovy | src/main/groovy/org/apache/ofbiz/order/order/OrderStats.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/order/OrderStats.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | OrderTaskList | groovy | src/main/groovy/org/apache/ofbiz/order/task/OrderTaskList.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/task/OrderTaskList.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | OrderTerms | groovy | src/main/groovy/org/apache/ofbiz/order/entry/OrderTerms.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/OrderTerms.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | OrderView | groovy | src/main/groovy/org/apache/ofbiz/order/order/OrderView.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/order/OrderView.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | OrderViewWebSecure | groovy | src/main/groovy/org/apache/ofbiz/order/order/OrderViewWebSecure.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/order/OrderViewWebSecure.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | PaymentSetup | groovy | src/main/groovy/org/apache/ofbiz/order/setup/PaymentSetup.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/setup/PaymentSetup.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | PrepareConfigForm | groovy | src/main/groovy/org/apache/ofbiz/order/entry/catalog/PrepareConfigForm.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/catalog/PrepareConfigForm.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | Product | groovy | src/main/groovy/org/apache/ofbiz/order/entry/catalog/Product.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/catalog/Product.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | ProductDetail | groovy | src/main/groovy/org/apache/ofbiz/order/entry/catalog/ProductDetail.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/catalog/ProductDetail.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | ProductSummary | groovy | src/main/groovy/org/apache/ofbiz/order/entry/catalog/ProductSummary.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/catalog/ProductSummary.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | ProductUomDropDownOnly | groovy | src/main/groovy/org/apache/ofbiz/order/entry/catalog/ProductUomDropDownOnly.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/catalog/ProductUomDropDownOnly.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | QuickAdd | groovy | src/main/groovy/org/apache/ofbiz/order/entry/catalog/QuickAdd.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/catalog/QuickAdd.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | QuickReturn | groovy | src/main/groovy/org/apache/ofbiz/order/orderreturn/QuickReturn.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/orderreturn/QuickReturn.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | ReceivePayment | groovy | src/main/groovy/org/apache/ofbiz/order/order/ReceivePayment.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/order/ReceivePayment.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | RequestItemNotes | groovy | src/main/groovy/org/apache/ofbiz/order/request/RequestItemNotes.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/request/RequestItemNotes.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | ReturnHeader | groovy | src/main/groovy/org/apache/ofbiz/order/orderreturn/ReturnHeader.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/orderreturn/ReturnHeader.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | ReturnHistory | groovy | src/main/groovy/org/apache/ofbiz/order/orderreturn/ReturnHistory.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/orderreturn/ReturnHistory.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | ReturnItems | groovy | src/main/groovy/org/apache/ofbiz/order/orderreturn/ReturnItems.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/orderreturn/ReturnItems.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | SelectCreatedProposed | groovy | src/main/groovy/org/apache/ofbiz/order/requirement/SelectCreatedProposed.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/requirement/SelectCreatedProposed.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | SendConfirmationEmail | groovy | src/main/groovy/org/apache/ofbiz/order/order/SendConfirmationEmail.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/order/SendConfirmationEmail.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | SetAdditionalParty | groovy | src/main/groovy/org/apache/ofbiz/order/entry/SetAdditionalParty.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/SetAdditionalParty.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | SetCheckOutTabBar | groovy | src/main/groovy/org/apache/ofbiz/order/entry/SetCheckOutTabBar.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/SetCheckOutTabBar.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | SetRequestQuote | groovy | src/main/groovy/org/apache/ofbiz/order/request/SetRequestQuote.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/request/SetRequestQuote.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | SetShoppingCart | groovy | src/main/groovy/org/apache/ofbiz/order/entry/SetShoppingCart.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/SetShoppingCart.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | ShipGroups | groovy | src/main/groovy/org/apache/ofbiz/order/order/ShipGroups.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/order/ShipGroups.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | ShipSettings | groovy | src/main/groovy/org/apache/ofbiz/order/entry/ShipSettings.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/ShipSettings.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | ShoppingList | groovy | src/main/groovy/org/apache/ofbiz/order/entry/ShoppingList.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/ShoppingList.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | ShowCart | groovy | src/main/groovy/org/apache/ofbiz/order/entry/ShowCart.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/ShowCart.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | ShowPromoText | groovy | src/main/groovy/org/apache/ofbiz/order/entry/ShowPromoText.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/ShowPromoText.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | ShowPromotionDetails | groovy | src/main/groovy/org/apache/ofbiz/order/entry/cart/ShowPromotionDetails.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/cart/ShowPromotionDetails.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | SideDeepCategory | groovy | src/main/groovy/org/apache/ofbiz/order/entry/catalog/SideDeepCategory.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/catalog/SideDeepCategory.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | SplitShip | groovy | src/main/groovy/org/apache/ofbiz/order/entry/SplitShip.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/SplitShip.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | StorePaymentOptions | groovy | src/main/groovy/org/apache/ofbiz/order/entry/StorePaymentOptions.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/entry/StorePaymentOptions.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | ViewAllocationPlan | groovy | src/main/groovy/org/apache/ofbiz/order/allocationplan/ViewAllocationPlan.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/allocationplan/ViewAllocationPlan.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | ViewImage | groovy | src/main/groovy/org/apache/ofbiz/order/order/ViewImage.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/order/ViewImage.groovy | 画面専用処理は UI 再構築時に移植 |
| Groovy screen action | ViewQuoteProfit | groovy | src/main/groovy/org/apache/ofbiz/order/quote/ViewQuoteProfit.groovy |  | applications/order/src/main/groovy/org/apache/ofbiz/order/quote/ViewQuoteProfit.groovy | 画面専用処理は UI 再構築時に移植 |
| controller | AddOrderItemShipGroup | service |  | addOrderItemShipGroup | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | AddOrderItemShipGroupAssoc | service |  | addOrderItemShipGroupAssoc | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | DeleteOrderItemShipGroup | service |  | deleteOrderItemShipGroup | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | DeleteOrderItemShipGroupAssoc | service |  | deleteOrderItemShipGroupAssoc | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | addItemToShoppingList | service |  | createShoppingListItem | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | addOrderAttachments | service |  | uploadOrderContentFile | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | addPaymentMethodToOrder | service |  | addPaymentMethodToOrder | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | addShippingAddress | service |  | createUpdateShippingAddress | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | allowordersplit | service |  | setAllowOrderSplit | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | appendItemToOrder | service |  | appendOrderItem | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | authOrderPayment | service |  | authOrderPaymentPreference | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | autoAssignRequirementToSupplier | service |  | autoAssignRequirementToSupplier | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | autoCreateQuoteAdjustments | service |  | autoCreateQuoteAdjustments | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | balanceInventoryItems | service |  | balanceInventoryItems | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | changeAllocationPlanStatus | service |  | changeAllocationPlanStatus | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | completePurchaseOrder | service |  | completePurchaseOrder | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | copyQuote | service |  | copyQuote | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createAllocationPlanAndItems | service |  | createAllocationPlanAndItems | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createCreditCardAndPostalAddress | service |  | createCreditCardAndAddress | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createCreditCardOrderEntry | service |  | createCreditCard | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createCustRequestItemWorkEffort | service |  | createWorkEffortRequestItem | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createCustRequestParty | service |  | createCustRequestParty | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createEftAccount | service |  | createEftAccount | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createEftAndPostalAddress | service |  | createEftAccountAndAddress | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createOrderAdjustment | service |  | createOrderAdjustment | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createOrderConversation | service |  | createOrderConversation | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createOrderDeliverySchedule | service |  | createOrderDeliverySchedule | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createOrderHeader | service |  | createOrderHeader | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createOrderItemShipGroup | service |  | createOrderItemShipGroup | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createOrderTerm | service |  | createOrderTerm | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createPartyContactMechPurpose | service |  | createPartyContactMechPurpose | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createPartyGroup | service |  | createPartyGroup | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createPostalAddress | service |  | createPartyPostalAddress | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createPostalAddressAndPurpose | service |  | createPartyPostalAddress | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createQuote | service |  | createQuote | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createQuoteAdjustment | service |  | createQuoteAdjustment | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createQuoteAndQuoteItemForRequest | service |  | createQuoteAndQuoteItemForRequest | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createQuoteAttribute | service |  | createQuoteAttribute | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createQuoteCoefficient | service |  | createQuoteCoefficient | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createQuoteFromCustRequest | service |  | createQuoteFromCustRequest | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createQuoteFromShoppingList | service |  | createQuoteFromShoppingList | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createQuoteItem | service |  | createQuoteItem | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createQuoteItemForRequest | service |  | createQuoteItem | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createQuoteRole | service |  | createQuoteRole | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createQuoteTerm | service |  | createQuoteTerm | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createQuoteTermFromItem | service |  | createQuoteTerm | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createRequirementRole | service |  | createRequirementRole | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createReturn | service |  | createReturnHeader | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createordernote | service |  | createOrderNote | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createquotenote | service |  | createQuoteNote | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | createrequestitem | service |  | createCustRequestItem | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | deleteAllocationPlanItem | service |  | deleteAllocationPlanItem | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | deleteCustRequestItemWorkEffort | service |  | deleteWorkEffortRequestItem | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | deleteCustRequestParty | service |  | deleteCustRequestParty | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | deleteCustomerTaxAuthInfo | service |  | deletePartyTaxAuthInfo | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | deleteOrderAdjustment | service |  | deleteOrderAdjustment | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | deleteQuoteTerm | service |  | deleteQuoteTerm | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | deleteQuoteTermFromItem | service |  | deleteQuoteTerm | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | deleteQuoteWorkEffort | service |  | deleteQuoteWorkEffort | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | deleteRequirement | service |  | deleteRequirementAndRelated | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | emailorder | service |  | sendOrderConfirmation | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | ensureWorkEffortAndCreateQuoteWorkEffort | service |  | ensureWorkEffortAndCreateQuoteWorkEffort | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | expireCustRequestContent | service |  | expireCustRequestContent | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | expireCustRequestParty | service |  | expireCustRequestParty | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | expirePartyContactMechPurpose | service |  | expirePartyContactMechPurpose | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | getProductInventoryAvailable | service |  | getInventoryAvailableByFacility | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | getStatusItemsForReturn | service |  | getStatusItemsForReturn | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | markOrderViewed | service |  | updateOrderHeader | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | massApproveOrders | service |  | massChangeOrderApproved | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | massCancelOrders | service |  | massCancelOrders | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | massCancelRemainingPurchaseOrderItems | service |  | massCancelRemainingPurchaseOrderItems | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | massCreateFileForOrders | service |  | massCreateFileForOrders | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | massHoldOrders | service |  | massHoldOrders | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | massPickOrders | service |  | massPickOrders | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | massPrintOrders | service |  | massPrintOrders | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | massProcessOrders | service |  | massProcessOrders | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | massQuickShipOrders | service |  | massQuickShipOrders | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | massRejectOrders | service |  | massRejectOrders | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | orderPickSheet.pdf | service |  | printPickSheets | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | productAvailabilityByFacility | service |  | productAvailabilityByFacility | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | quickDropShipOrder | service |  | quickDropShipOrder | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | quickRefundOrder | service |  | quickReturnOrder | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | quickShipOrder | service |  | quickShipEntireOrder | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | recalcTax | service |  | recalcTaxTotal | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | removeOrderTerm | service |  | removeOrderTerm | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | removeQuoteAdjustment | service |  | removeQuoteAdjustment | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | removeQuoteAttribute | service |  | removeQuoteAttribute | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | removeQuoteCoefficient | service |  | removeQuoteCoefficient | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | removeQuoteItem | service |  | removeQuoteItem | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | removeQuoteRole | service |  | removeQuoteRole | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | removeRequirementRole | service |  | removeRequirementRole | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | removeReturnAdjustment | service |  | removeReturnAdjustment | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | removeReturnItem | service |  | removeReturnItem | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | searchorders | service |  | findOrders | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | sendPOEmail | service |  | sendPOEmail | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | sendQuoteReportMail | service |  | sendQuoteReportMail | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | sendconfirmationmail | service |  | sendMail | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | setGiftMessage | service |  | setGiftMessage | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | setInvoicePerShipment | service |  | updateOrderHeader | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | setOrderReservationPriority | service |  | setOrderReservationPriority | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | setShippingInstructions | service |  | setShippingInstructions | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateAllocationPlanItems | service |  | updateAllocationPlanItems | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateCreditCardAndPostalAddress | service |  | updateCreditCardAndAddress | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateCustRequestParty | service |  | updateCustRequestParty | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateEftAndPostalAddress | service |  | updateEftAccountAndAddress | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateOrderAdjustment | service |  | updateOrderAdjustment | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateOrderContactMech | service |  | updateOrderContactMech | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateOrderDeliverySchedule | service |  | updateOrderDeliverySchedule | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateOrderHeader | service |  | updateOrderHeader | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateOrderItemShipGroup | service |  | updateOrderItemShipGroup | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateOrderItems | service |  | updateOrderItems | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateOrderNote | service |  | updateOrderNote | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateOrderPaymentPreference | service |  | updateOrderPaymentPreference | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateOrderTerm | service |  | updateOrderTerm | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updatePostalAddress | service |  | updatePartyPostalAddress | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updatePostalAddressOrderEntry | service |  | updatePartyPostalAddress | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateQuote | service |  | updateQuote | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateQuoteAdjustment | service |  | updateQuoteAdjustment | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateQuoteAttribute | service |  | updateQuoteAttribute | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateQuoteCoefficient | service |  | updateQuoteCoefficient | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateQuoteItem | service |  | updateQuoteItem | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateQuoteItemForRequest | service |  | updateQuoteItem | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateQuoteNote | service |  | updateNote | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateQuoteRole | service |  | updateQuoteRole | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateQuoteTerm | service |  | updateQuoteTerm | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateQuoteTermFromItem | service |  | updateQuoteTerm | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateQuoteWorkEffort | service |  | updateWorkEffort | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateRequirement | service |  | updateRequirement | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateRequirementRole | service |  | updateRequirementRole | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateReturn | service |  | updateReturnHeader | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateShipGroupShipInfo | service |  | updateShipGroupShipInfo | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateShippingAddress | service |  | setCartShippingAddress | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateShippingMethodAndCharges | service |  | updateShippingMethodAndCharges | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateShippingOptions | service |  | setCartShippingOptions | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updateTrackingNumber | service |  | updateTrackingNumber | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updaterequestitem | service |  | updateCustRequestItem | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| controller | updatesplit | service |  | assignItemShipGroup | applications/order/webapp/ordermgr/WEB-INF/controller.xml | 画面イベントは API で代替、UI 再構築は別途 |
| minilang event | createCustRequestContent | simple-method |  |  | applications/order/minilang/request/CustRequestEvents.xml | 画面イベントは API で代替、UI 再構築は別途 |
| minilang event | createCustomer | simple-method |  |  | applications/order/minilang/customer/CustomerEvents.xml | 画面イベントは API で代替、UI 再構築は別途 |
| minilang event | createUpdateCustomerInfo | simple-method |  |  | applications/order/minilang/customer/CustomerEvents.xml | 画面イベントは API で代替、UI 再構築は別途 |
| minilang event | getOrder | simple-method |  |  | applications/order/minilang/reports/NetBeforeOverheadDateEvent.xml | 画面イベントは API で代替、UI 再構築は別途 |
| minilang event | getOrder | simple-method |  |  | applications/order/minilang/reports/NetBeforeOverheadEvent.xml | 画面イベントは API で代替、UI 再構築は別途 |
| minilang event | getOrder | simple-method |  |  | applications/order/minilang/reports/NetBeforeOverheadMonthlyEvent.xml | 画面イベントは API で代替、UI 再構築は別途 |
| minilang event | getOrder | simple-method |  |  | applications/order/minilang/reports/ProductDemandReportEvent.xml | 画面イベントは API で代替、UI 再構築は別途 |
| minilang event | getOrder | simple-method |  |  | applications/order/minilang/reports/SalesReportEvent.xml | 画面イベントは API で代替、UI 再構築は別途 |
| minilang event | updateCustomer | simple-method |  |  | applications/order/minilang/customer/CustomerEvents.xml | 画面イベントは API で代替、UI 再構築は別途 |
| minilang event | validateCustomerInfo | simple-method |  |  | applications/order/minilang/customer/CustomerEvents.xml | 画面イベントは API で代替、UI 再構築は別途 |
| view-entity | CommunicationEventAndCustRequest |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | CommunicationEventAndOrder |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | CommunicationEventAndReturn |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | CustReqAndTypeAndPartyRel |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | CustRequestAndCommEvent |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | CustRequestAndContent |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | CustRequestAndNote |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | CustRequestAndRole |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | CustRequestAndWorkEffort |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | CustRequestInfoAndWorkEffortAndPartyRel |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | CustRequestItemNoteView |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | CustRequestNoteView |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | ItemIssuanceQuantitySum |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderAndPartyContactMech |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderHeaderAndItemFacilityLocation |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderHeaderAndItems |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderHeaderAndPaymentPref |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderHeaderAndRoleSummary |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderHeaderAndRoles |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderHeaderAndShipGroups |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderHeaderAndShipGroupsByProduct |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderHeaderAndWorkEffort |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderHeaderItemAndInv |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderHeaderItemAndInvRoles |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderHeaderItemAndRoles |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderHeaderItemAndShipGroup |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderHeaderNoteView |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderItemAndProduct |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderItemAndProductContentInfo |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderItemAndShipGroupAssoc |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderItemAndShipGrpInvResAndItem |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderItemAndShipGrpInvResAndItemSum |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderItemBillingAndInvoiceAndItem |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderItemQuantityReportGroupByItem |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderItemQuantityReportGroupByProduct |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderItemShipGrpInvResAndItem |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderItemShipGrpInvResAndItemLocation |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderPaymentPrefAndPayment |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderPurchasePaymentSummary |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderPurchaseProductSummary |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderReportGroupByProduct |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderReportPurchasesGroupByProduct |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderReportSalesGroupByProduct |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderReportView |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderRoleAndProductContentInfo |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | OrderTaskList |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | QuoteNoteView |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | QuoteWorkEffortView |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | RequirementAndRole |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | RequirementByProductFacility |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | RequirementCustRequestView |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | RequirementPartyProductCount |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| view-entity | WorkAndOrderItemFulfillment |  |  |  | applications/order/entitydef/entitymodel_view.xml | 照会用 view-entity は別途設計 |
| サービス | addDistinctShoppingListItem | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/shoppinglist/ShoppingListServicesScript.groovy | addDistinctShoppingListItem | applications/order/servicedef/services_shoppinglist.xml | 主要フロー外（スコープ外） |
| サービス | addOrderItemShipGroup | java | org.apache.ofbiz.order.order.OrderServices | addOrderItemShipGroup | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | addOrderItemShipGroupAssoc | java | org.apache.ofbiz.order.order.OrderServices | addOrderItemShipGroupAssoc | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | addOrderRole | java | org.apache.ofbiz.order.order.OrderServices | addRoleType | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | addPaymentMethodToOrder | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderServicesScript.groovy | addPaymentMethodToOrder | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | addProductsBackToCategory | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderReturnServicesScript.groovy | addProductsBackToCategory | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | addSuggestionsToShoppingList | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/shoppinglist/ShoppingListServicesScript.groovy | addSuggestionsToShoppingList | applications/order/servicedef/services_shoppinglist.xml | 主要フロー外（スコープ外） |
| サービス | appendOrderItem | java | org.apache.ofbiz.order.order.OrderServices | addItemToApprovedOrder | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | approveAllocationPlanItems | java | org.apache.ofbiz.order.order.OrderServices | approveAllocationPlanItems | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | approveRequirement | java | org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices | approveRequirement | applications/order/servicedef/services_requirement.xml | 主要フロー外（スコープ外） |
| サービス | assignItemShipGroup | java | org.apache.ofbiz.order.shoppingcart.ShoppingCartServices | assignItemShipGroup | applications/order/servicedef/services_cart.xml | 主要フロー外（スコープ外） |
| サービス | associateOrderWithAllocationPlans | java | org.apache.ofbiz.order.order.OrderServices | associateOrderWithAllocationPlans | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | autoAssignRequirementToSupplier | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/requirement/RequirementServicesScript.groovy | autoAssignRequirementToSupplier | applications/order/servicedef/services_requirement.xml | 主要フロー外（スコープ外） |
| サービス | autoCancelOrderItems | java | org.apache.ofbiz.order.order.OrderServices | cancelFlaggedSalesOrders | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | autoCancelReplacementOrders | java | org.apache.ofbiz.order.order.OrderReturnServices | autoCancelReplacementOrders | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | autoCreateQuoteAdjustments | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/quote/QuoteServicesScript.groovy | autoCreateQuoteAdjustments | applications/order/servicedef/services_quote.xml | 主要フロー外（スコープ外） |
| サービス | autoDeleteAutoSaveShoppingList | java | org.apache.ofbiz.order.shoppinglist.ShoppingListServices | autoDeleteAutoSaveShoppingList | applications/order/servicedef/services_shoppinglist.xml | 主要フロー外（スコープ外） |
| サービス | autoUpdateQuotePrice | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/quote/QuoteServicesScript.groovy | autoUpdateQuotePrice | applications/order/servicedef/services_quote.xml | 主要フロー外（スコープ外） |
| サービス | calculateShoppingListDeepTotalPrice | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/shoppinglist/ShoppingListServicesScript.groovy | calculateShoppingListDeepTotalPrice | applications/order/servicedef/services_shoppinglist.xml | 主要フロー外（スコープ外） |
| サービス | callProcessOrderPayments | java | org.apache.ofbiz.order.order.OrderServices | callProcessOrderPayments | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | cancelAllBackOrders | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderServicesScript.groovy | cancelAllBackOrders | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | cancelAllocationPlanItemByOrderItem | java | org.apache.ofbiz.order.order.OrderServices | cancelAllocationPlanItemByOrderItem | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | cancelAllocationPlanItems | java | org.apache.ofbiz.order.order.OrderServices | cancelAllocationPlanItems | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | cancelRemainingPurchaseOrderItems | java | org.apache.ofbiz.order.order.OrderServices | cancelRemainingPurchaseOrderItems | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | cancelReplacementOrderItems | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderReturnServicesScript.groovy | cancelReplacementOrderItems | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | cancelReturnItems | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderReturnServicesScript.groovy | cancelReturnItems | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | changeAllocationPlanItemStatus | java | org.apache.ofbiz.order.order.OrderServices | changeAllocationPlanItemStatus | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | changeAllocationPlanStatus | java | org.apache.ofbiz.order.order.OrderServices | changeAllocationPlanStatus | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | changeOrderPaymentStatus | java | org.apache.ofbiz.order.order.OrderServices | setOrderPaymentStatus | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | checkCreateDropShipPurchaseOrders | java | org.apache.ofbiz.order.order.OrderServices | checkCreateDropShipPurchaseOrders | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | checkCreateOrderRequirement | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderRequirementServicesScript.groovy | checkCreateOrderRequirement | applications/order/servicedef/services_requirement.xml | 主要フロー外（スコープ外） |
| サービス | checkCreateProductRequirementForFacility | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderRequirementServicesScript.groovy | checkCreateProductRequirementForFacility | applications/order/servicedef/services_requirement.xml | 主要フロー外（スコープ外） |
| サービス | checkCreateStockRequirementAtp | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderRequirementServicesScript.groovy | checkCreateStockRequirementAtp | applications/order/servicedef/services_requirement.xml | 主要フロー外（スコープ外） |
| サービス | checkCreateStockRequirementQoh | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderRequirementServicesScript.groovy | checkCreateStockRequirementQoh | applications/order/servicedef/services_requirement.xml | 主要フロー外（スコープ外） |
| サービス | checkDigitalItemFulfillment | java | org.apache.ofbiz.order.order.OrderServices | checkDigitalItemFulfillment | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | checkOrderIsOnBackOrder | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderServicesScript.groovy | checkOrderIsOnBackOrder | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | checkOrderItemStatus | java | org.apache.ofbiz.order.order.OrderServices | checkItemStatus | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | checkPaymentAmountForRefund | java | org.apache.ofbiz.order.order.OrderReturnServices | checkPaymentAmountForRefund | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | checkReturnComplete | java | org.apache.ofbiz.order.order.OrderReturnServices | checkReturnComplete | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | checkShoppingListItemSecurity | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/shoppinglist/ShoppingListServicesScript.groovy | checkShoppingListItemSecurity | applications/order/servicedef/services_shoppinglist.xml | 主要フロー外（スコープ外） |
| サービス | checkShoppingListSecurity | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/shoppinglist/ShoppingListServicesScript.groovy | checkShoppingListSecurity | applications/order/servicedef/services_shoppinglist.xml | 主要フロー外（スコープ外） |
| サービス | checkSupplierRelatedOrderPermission | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderDeliveryServices.groovy | checkSupplierRelatedOrderPermissionService | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | checkUpdateQuoteStatus | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/quote/QuoteServicesScript.groovy | checkUpdateQuoteStatus | applications/order/servicedef/services_quote.xml | 主要フロー外（スコープ外） |
| サービス | completeAllocationPlanItemByOrderItem | java | org.apache.ofbiz.order.order.OrderServices | completeAllocationPlanItemByOrderItem | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | completePurchaseOrder | group |  |  | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | copyQuote | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/quote/QuoteServicesScript.groovy | copyQuote | applications/order/servicedef/services_quote.xml | 主要フロー外（スコープ外） |
| サービス | copyQuoteItem | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/quote/QuoteServicesScript.groovy | copyQuoteItem | applications/order/servicedef/services_quote.xml | 主要フロー外（スコープ外） |
| サービス | countProductQuantityOrdered | java | org.apache.ofbiz.order.order.OrderServices | countProductQuantityOrdered | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | createATPRequirementsForOrder | java | org.apache.ofbiz.order.requirement.RequirementServices | createATPRequirementsForOrder | applications/order/servicedef/services_requirement.xml | 主要フロー外（スコープ外） |
| サービス | createAllocationPlanAndItems | java | org.apache.ofbiz.order.order.OrderServices | createAllocationPlanAndItems | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | createAlsoBoughtProductAssocs | java | org.apache.ofbiz.order.order.OrderServices | createAlsoBoughtProductAssocs | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | createAlsoBoughtProductAssocsForOrder | java | org.apache.ofbiz.order.order.OrderServices | createAlsoBoughtProductAssocsForOrder | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | createAutoRequirementsForOrder | java | org.apache.ofbiz.order.requirement.RequirementServices | createAutoRequirementsForOrder | applications/order/servicedef/services_requirement.xml | 主要フロー外（スコープ外） |
| サービス | createExchangeOrderAssoc | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderReturnServicesScript.groovy | createExchangeOrderAssoc | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | createOrderConversation | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/communications/CommunicationServices.groovy | createOrderConversation | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | createOrderDeliverySchedule | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderDeliveryServices.groovy | createOrderDeliverySchedule | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | createOrderFromShoppingCart | java | org.apache.ofbiz.order.order.OrderServices | createOrderFromShoppingCart | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | createOrderHeader | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderServicesScript.groovy | createOrderHeader | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | createOrderItemChange | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderServicesScript.groovy | createOrderItemChange | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | createOrderNote | java | org.apache.ofbiz.order.order.OrderServices | createOrderNote | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | createOrderPaymentApplication | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderServicesScript.groovy | createOrderPaymentApplication | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | createPaymentApplicationsFromReturnItemResponse | java | org.apache.ofbiz.order.order.OrderReturnServices | createPaymentApplicationsFromReturnItemResponse | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | createPaymentFromPreference | java | org.apache.ofbiz.order.order.OrderServices | createPaymentFromPreference | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | createQuote | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/quote/QuoteServicesScript.groovy | createQuote | applications/order/servicedef/services_quote.xml | 主要フロー外（スコープ外） |
| サービス | createQuoteAdjustment | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/quote/QuoteServicesScript.groovy | createQuoteAdjustment | applications/order/servicedef/services_quote.xml | 主要フロー外（スコープ外） |
| サービス | createQuoteAndQuoteItemForRequest | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/quote/QuoteServicesScript.groovy | createQuoteAndQuoteItemForRequest | applications/order/servicedef/services_quote.xml | 主要フロー外（スコープ外） |
| サービス | createQuoteFromCart | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/quote/QuoteServicesScript.groovy | createQuoteFromCart | applications/order/servicedef/services_quote.xml | 主要フロー外（スコープ外） |
| サービス | createQuoteFromCustRequest | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/quote/QuoteServicesScript.groovy | createQuoteFromCustRequest | applications/order/servicedef/services_quote.xml | 主要フロー外（スコープ外） |
| サービス | createQuoteFromShoppingList | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/quote/QuoteServicesScript.groovy | createQuoteFromShoppingList | applications/order/servicedef/services_quote.xml | 主要フロー外（スコープ外） |
| サービス | createQuoteItem | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/quote/QuoteServicesScript.groovy | createQuoteItem | applications/order/servicedef/services_quote.xml | 主要フロー外（スコープ外） |
| サービス | createQuoteNote | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/quote/QuoteServicesScript.groovy | createQuoteNote | applications/order/servicedef/services_quote.xml | 主要フロー外（スコープ外） |
| サービス | createReturnAdjustment | java | org.apache.ofbiz.order.order.OrderReturnServices | createReturnAdjustment | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | createReturnAndItemOrAdjustment | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderReturnServicesScript.groovy | createReturnAndItemOrAdjustment | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | createReturnHeader | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderReturnServicesScript.groovy | createReturnHeader | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | createReturnItem | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderReturnServicesScript.groovy | createReturnItem | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | createReturnItemForRental | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderReturnServicesScript.groovy | createReturnItemForRental | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | createReturnItemOrAdjustment | java | org.apache.ofbiz.order.order.OrderReturnServices | createReturnItemOrAdjustment | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | createReturnStatus | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderReturnServicesScript.groovy | createReturnStatus | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | createShoppingList | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/shoppinglist/ShoppingListServicesScript.groovy | createShoppingList | applications/order/servicedef/services_shoppinglist.xml | 主要フロー外（スコープ外） |
| サービス | createShoppingListItem | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/shoppinglist/ShoppingListServicesScript.groovy | createShoppingListItem | applications/order/servicedef/services_shoppinglist.xml | 主要フロー外（スコープ外） |
| サービス | createShoppingListRecurrence | java | org.apache.ofbiz.order.shoppinglist.ShoppingListServices | setShoppingListRecurrence | applications/order/servicedef/services_shoppinglist.xml | 主要フロー外（スコープ外） |
| サービス | createSimpleNonProductSalesOrder | java | org.apache.ofbiz.order.order.OrderServices | createSimpleNonProductSalesOrder | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | createTestSalesOrderSingle | java | org.apache.ofbiz.order.test.OrderTestServices | createTestSalesOrderSingle | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | createTestSalesOrders | java | org.apache.ofbiz.order.test.OrderTestServices | createTestSalesOrders | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | createTransferFromRequirement | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/requirement/RequirementServicesScript.groovy | createTransferFromRequirement | applications/order/servicedef/services_requirement.xml | 主要フロー外（スコープ外） |
| サービス | createUpdateBillingAddress | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderServicesScript.groovy | createUpdateBillingAddress | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | createUpdateBillingAddressAndPaymentMethod | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/CheckoutServices.groovy | createUpdateBillingAddressAndPaymentMethod | applications/order/servicedef/services_checkout.xml | 主要フロー外（スコープ外） |
| サービス | createUpdateCreditCard | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderServicesScript.groovy | createUpdateCreditCard | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | createUpdateCustomerAndShippingAddress | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/CheckoutServices.groovy | createUpdateCustomerAndShippingAddress | applications/order/servicedef/services_checkout.xml | 主要フロー外（スコープ外） |
| サービス | createUpdateShippingAddress | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderServicesScript.groovy | createUpdateShippingAddress | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | deleteOrderItemShipGroup | java | org.apache.ofbiz.order.order.OrderServices | deleteOrderItemShipGroup | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | deleteRequirementAndRelated | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/requirement/RequirementServicesScript.groovy | deleteRequirementAndRelated | applications/order/servicedef/services_requirement.xml | 主要フロー外（スコープ外） |
| サービス | ensureWorkEffortAndCreateQuoteWorkEffort | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/quote/QuoteServicesScript.groovy | ensureWorkEffortAndCreateQuoteWorkEffort | applications/order/servicedef/services_quote.xml | 主要フロー外（スコープ外） |
| サービス | findOrders | java | org.apache.ofbiz.order.order.OrderLookupServices | findOrders | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | fulfillDigitalItems | java | org.apache.ofbiz.order.order.OrderServices | fulfillDigitalItems | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | generateReqsFromCancelledPOItems | java | org.apache.ofbiz.order.order.OrderServices | generateReqsFromCancelledPOItems | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | getNextOrderId | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderServicesScript.groovy | getNextOrderId | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | getNextQuoteId | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/quote/QuoteServicesScript.groovy | getNextQuoteId | applications/order/servicedef/services_quote.xml | 主要フロー外（スコープ外） |
| サービス | getOrderAvailableReturnedTotal | java | org.apache.ofbiz.order.order.OrderReturnServices | getOrderAvailableReturnedTotal | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | getOrderHeaderInformation | java | org.apache.ofbiz.order.order.OrderServices | getOrderHeaderInformation | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | getOrderItemInvoicedAmountAndQuantity | java | org.apache.ofbiz.order.order.OrderServices | getOrderItemInvoicedAmountAndQuantity | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | getOrderItemShipGroupEstimatedShipDate | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderServicesScript.groovy | getOrderItemShipGroupEstimatedShipDate | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | getOrderShippingAmount | java | org.apache.ofbiz.order.order.OrderServices | getOrderShippingAmount | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | getOrderStatus | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderServicesScript.groovy | getOrderStatus | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | getOrderedSummaryInformation | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderServicesScript.groovy | getOrderedSummaryInformation | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | getRequirementsForSupplier | java | org.apache.ofbiz.order.requirement.RequirementServices | getRequirementsForSupplier | applications/order/servicedef/services_requirement.xml | 主要フロー外（スコープ外） |
| サービス | getReturnAmountByOrder | java | org.apache.ofbiz.order.order.OrderReturnServices | getReturnAmountByOrder | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | getReturnItemInitialCost | java | org.apache.ofbiz.order.order.OrderReturnServices | getReturnItemInitialCost | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | getReturnableItems | java | org.apache.ofbiz.order.order.OrderReturnServices | getReturnableItems | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | getReturnableQuantity | java | org.apache.ofbiz.order.order.OrderReturnServices | getReturnableQuantity | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | getShoppingCartData | java | org.apache.ofbiz.order.shoppingcart.ShoppingCartServices | getShoppingCartData | applications/order/servicedef/services_cart.xml | 主要フロー外（スコープ外） |
| サービス | getShoppingCartItemIndex | java | org.apache.ofbiz.order.shoppingcart.ShoppingCartServices | getShoppingCartItemIndex | applications/order/servicedef/services_cart.xml | 主要フロー外（スコープ外） |
| サービス | getStatusItemsForReturn | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderReturnServicesScript.groovy | getStatusItemsForReturn | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | invoiceServiceItems | java | org.apache.ofbiz.order.order.OrderServices | invoiceServiceItems | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | isInventoryAllocationRequired | java | org.apache.ofbiz.order.order.OrderServices | isInventoryAllocationRequired | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | itemFulfillmentInterface | interface |  |  | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | loadCartForUpdate | java | org.apache.ofbiz.order.order.OrderServices | loadCartForUpdate | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | loadCartFromOrder | java | org.apache.ofbiz.order.shoppingcart.ShoppingCartServices | loadCartFromOrder | applications/order/servicedef/services_cart.xml | 主要フロー外（スコープ外） |
| サービス | loadCartFromQuote | java | org.apache.ofbiz.order.shoppingcart.ShoppingCartServices | loadCartFromQuote | applications/order/servicedef/services_cart.xml | 主要フロー外（スコープ外） |
| サービス | loadCartFromShoppingList | java | org.apache.ofbiz.order.shoppingcart.ShoppingCartServices | loadCartFromShoppingList | applications/order/servicedef/services_cart.xml | 主要フロー外（スコープ外） |
| サービス | makeShoppingListFromOrder | java | org.apache.ofbiz.order.shoppinglist.ShoppingListServices | makeListFromOrder | applications/order/servicedef/services_shoppinglist.xml | 主要フロー外（スコープ外） |
| サービス | massCancelOrders | java | org.apache.ofbiz.order.order.OrderServices | massCancelOrders | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | massCancelRemainingPurchaseOrderItems | java | org.apache.ofbiz.order.order.OrderServices | massCancelRemainingPurchaseOrderItems | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | massChangeOrderApproved | java | org.apache.ofbiz.order.order.OrderServices | massChangeApproved | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | massCreateFileForOrders | java | org.apache.ofbiz.order.order.OrderServices | massCreateFileForOrders | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | massHoldOrders | java | org.apache.ofbiz.order.order.OrderServices | massHoldOrders | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | massOrderChangeInterface | interface |  |  | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | massPickOrders | java | org.apache.ofbiz.order.order.OrderServices | massPickOrders | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | massPrintOrders | java | org.apache.ofbiz.order.order.OrderServices | massPrintOrders | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | massProcessOrders | java | org.apache.ofbiz.order.order.OrderServices | massProcessOrders | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | massQuickShipOrders | java | org.apache.ofbiz.order.order.OrderServices | massQuickShipOrders | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | massRejectOrders | java | org.apache.ofbiz.order.order.OrderServices | massRejectOrders | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | migrateOldOrderBlacklistAndOldOrderBlacklistType | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/OrderBlacklistServices.groovy | migrateOldOrderBlacklistAndOldOrderBlacklistType | applications/order/servicedef/services_upgrade.xml | 主要フロー外（スコープ外） |
| サービス | moveItemBetweenShipGroups | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderServicesScript.groovy | moveItemBetweenShipGroups | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | orderNotificationInterface | interface |  |  | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | orderSequence_enforced | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderServicesScript.groovy | orderSequence_enforced | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | prepareVendorShipGroups | java | org.apache.ofbiz.order.shoppingcart.ShoppingCartServices | prepareVendorShipGroups | applications/order/servicedef/services_cart.xml | 主要フロー外（スコープ外） |
| サービス | processCreditReturn | java | org.apache.ofbiz.order.order.OrderReturnServices | processCreditReturn | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | processCrossShipReplacementReturn | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderReturnServicesScript.groovy | processCrossShipReplacementReturn | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | processOrderPayments | java | org.apache.ofbiz.order.order.OrderServices | processOrderPayments | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | processRefundImmediatelyReturn | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderReturnServicesScript.groovy | processRefundImmediatelyReturn | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | processRefundOnlyReturn | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderReturnServicesScript.groovy | processRefundOnlyReturn | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | processRefundReturn | java | org.apache.ofbiz.order.order.OrderReturnServices | processRefundReturn | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | processRefundReturnForReplacement | java | org.apache.ofbiz.order.order.OrderReturnServices | processRefundReturnForReplacement | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | processRepairReplacementReturn | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderReturnServicesScript.groovy | processRepairReplacementReturn | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | processReplaceImmediatelyReturn | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderReturnServicesScript.groovy | processReplaceImmediatelyReturn | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | processReplacementReturn | java | org.apache.ofbiz.order.order.OrderReturnServices | processReplacementReturn | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | processSubscriptionReturn | java | org.apache.ofbiz.order.order.OrderReturnServices | processSubscriptionReturn | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | processWaitReplacementReservedReturn | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderReturnServicesScript.groovy | processWaitReplacementReservedReturn | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | processWaitReplacementReturn | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderReturnServicesScript.groovy | processWaitReplacementReservedReturn | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | productAvailabilityByFacility | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderServicesScript.groovy | productAvailabilityByFacility | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | quickReturnOrder | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderReturnServicesScript.groovy | quickReturnFromOrder | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | quoteSequenceEnforced | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/quote/QuoteServicesScript.groovy | quoteSequenceEnforced | applications/order/servicedef/services_quote.xml | 主要フロー外（スコープ外） |
| サービス | recalcShippingTotal | java | org.apache.ofbiz.order.order.OrderServices | recalcOrderShipping | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | recalcTaxTotal | java | org.apache.ofbiz.order.order.OrderServices | recalcOrderTax | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | recreateOrderAdjustments | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderServicesScript.groovy | recreateOrderAdjustments | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | refundBillingAccountPayment | java | org.apache.ofbiz.order.order.OrderReturnServices | refundBillingAccountPayment | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | removeOrderRole | java | org.apache.ofbiz.order.order.OrderServices | removeRoleType | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | removeQuoteItem | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/quote/QuoteServicesScript.groovy | removeQuoteItem | applications/order/servicedef/services_quote.xml | 主要フロー外（スコープ外） |
| サービス | removeReturnItem | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderReturnServicesScript.groovy | removeReturnItem | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | removeShoppingListItem | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/shoppinglist/ShoppingListServicesScript.groovy | removeShoppingListItem | applications/order/servicedef/services_shoppinglist.xml | 主要フロー外（スコープ外） |
| サービス | requirementInterface | interface |  |  | applications/order/servicedef/services_requirement.xml | 主要フロー外（スコープ外） |
| サービス | resetGrandTotal | java | org.apache.ofbiz.order.order.OrderServices | resetGrandTotal | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | resetShipGroupItems | java | org.apache.ofbiz.order.shoppingcart.ShoppingCartServices | resetShipGroupItems | applications/order/servicedef/services_cart.xml | 主要フロー外（スコープ外） |
| サービス | returnAdjustmentInterface | interface |  |  | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | runShoppingListAutoReorder | java | org.apache.ofbiz.order.shoppinglist.ShoppingListServices | createListReorders | applications/order/servicedef/services_shoppinglist.xml | 主要フロー外（スコープ外） |
| サービス | runSubscriptionAutoReorders | java | org.apache.ofbiz.order.order.OrderServices | runSubscriptionAutoReorders | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | saveUpdatedCartToOrder | java | org.apache.ofbiz.order.order.OrderServices | saveUpdatedCartToOrder | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | sendOrderBackorderNotification | java | org.apache.ofbiz.order.order.OrderServices | sendOrderBackorderNotification | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | sendOrderChangeNotification | java | org.apache.ofbiz.order.order.OrderServices | sendOrderChangeNotification | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | sendOrderCompleteNotification | java | org.apache.ofbiz.order.order.OrderServices | sendOrderCompleteNotification | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | sendOrderConfirmation | java | org.apache.ofbiz.order.order.OrderServices | sendOrderConfirmNotification | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | sendOrderDeliveryScheduleNotification | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderDeliveryServices.groovy | sendOrderDeliveryScheduleNotification | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | sendOrderPayRetryNotification | java | org.apache.ofbiz.order.order.OrderServices | sendOrderPayRetryNotification | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | sendPOEmail | java | org.apache.ofbiz.order.order.OrderServices | sendPOEmail | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | sendProcessNotification | java | org.apache.ofbiz.order.order.OrderServices | sendProcessNotification | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | sendQuoteReportMail | java | org.apache.ofbiz.order.quote.QuoteServices | sendQuoteReportMail | applications/order/servicedef/services_quote.xml | 主要フロー外（スコープ外） |
| サービス | sendReturnAcceptNotification | java | org.apache.ofbiz.order.order.OrderReturnServices | sendReturnAcceptNotification | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | sendReturnCancelNotification | java | org.apache.ofbiz.order.order.OrderReturnServices | sendReturnCancelNotification | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | sendReturnCompleteNotification | java | org.apache.ofbiz.order.order.OrderReturnServices | sendReturnCompleteNotification | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | setAllowOrderSplit | java | org.apache.ofbiz.order.order.OrderServices | allowOrderSplit | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | setAnonUserLogin | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/CheckoutServices.groovy | setAnonUserLogin | applications/order/servicedef/services_checkout.xml | 主要フロー外（スコープ外） |
| サービス | setCartOtherOptions | java | org.apache.ofbiz.order.shoppingcart.ShoppingCartServices | setOtherOptions | applications/order/servicedef/services_cart.xml | 主要フロー外（スコープ外） |
| サービス | setCartPaymentOptions | java | org.apache.ofbiz.order.shoppingcart.ShoppingCartServices | setPaymentOptions | applications/order/servicedef/services_cart.xml | 主要フロー外（スコープ外） |
| サービス | setCartShippingAddress | java | org.apache.ofbiz.order.shoppingcart.ShoppingCartServices | setShippingOptions | applications/order/servicedef/services_cart.xml | 主要フロー外（スコープ外） |
| サービス | setCartShippingOptions | java | org.apache.ofbiz.order.shoppingcart.ShoppingCartServices | setShippingOptions | applications/order/servicedef/services_cart.xml | 主要フロー外（スコープ外） |
| サービス | setEmptyGrandTotals | java | org.apache.ofbiz.order.order.OrderServices | setEmptyGrandTotals | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | setGiftMessage | java | org.apache.ofbiz.order.order.OrderServices | setGiftMessage | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | setShippingInstructions | java | org.apache.ofbiz.order.order.OrderServices | setShippingInstructions | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | setUnitPriceAsLastPrice | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderServicesScript.groovy | setUnitPriceAsLastPrice | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | shoppingCartRemoteTest | java | org.apache.ofbiz.order.order.OrderServices | shoppingCartRemoteTest | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | shoppingCartTest | java | org.apache.ofbiz.order.order.OrderServices | shoppingCartTest | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | shoppingListInterface | interface |  |  | applications/order/servicedef/services_shoppinglist.xml | 主要フロー外（スコープ外） |
| サービス | shoppingListItemInterface | interface |  |  | applications/order/servicedef/services_shoppinglist.xml | 主要フロー外（スコープ外） |
| サービス | splitShipmentMethodString | java | org.apache.ofbiz.order.shoppinglist.ShoppingListServices | splitShipmentMethodString | applications/order/servicedef/services_shoppinglist.xml | 主要フロー外（スコープ外） |
| サービス | storeQuote | java | org.apache.ofbiz.order.quote.QuoteServices | storeQuote | applications/order/servicedef/services_quote.xml | 主要フロー外（スコープ外） |
| サービス | updateAllocatedQuantityOnOrderItemChange | java | org.apache.ofbiz.order.order.OrderServices | updateAllocatedQuantityOnOrderItemChange | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | updateAllocationPlanItems | java | org.apache.ofbiz.order.order.OrderServices | updateAllocationPlanItems | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | updateOrderContactMech | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderServicesScript.groovy | updateOrderContactMech | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | updateOrderDeliverySchedule | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderDeliveryServices.groovy | updateOrderDeliverySchedule | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | updateOrderItemShipGroup | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderServicesScript.groovy | updateOrderItemShipGroup | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | updateOrderItemShipGroupAssoc | java | org.apache.ofbiz.order.order.OrderServices | updateOrderItemShipGroupAssoc | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | updateOrderItems | java | org.apache.ofbiz.order.order.OrderServices | updateApprovedOrderItems | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | updateOrderPaymentPreference | java | org.apache.ofbiz.order.order.OrderServices | updateOrderPaymentPreference | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | updateQuote | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/quote/QuoteServicesScript.groovy | updateQuote | applications/order/servicedef/services_quote.xml | 主要フロー外（スコープ外） |
| サービス | updateQuoteItem | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/quote/QuoteServicesScript.groovy | updateQuoteItem | applications/order/servicedef/services_quote.xml | 主要フロー外（スコープ外） |
| サービス | updateRequirementsToOrdered | java | org.apache.ofbiz.order.requirement.RequirementServices | updateRequirementsToOrdered | applications/order/servicedef/services_requirement.xml | 主要フロー外（スコープ外） |
| サービス | updateReturnAdjustment | java | org.apache.ofbiz.order.order.OrderReturnServices | updateReturnAdjustment | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | updateReturnContactMech | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderReturnServicesScript.groovy | updateReturnContactMech | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | updateReturnHeader | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderReturnServicesScript.groovy | updateReturnHeader | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | updateReturnItem | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderReturnServicesScript.groovy | updateReturnItem | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | updateReturnItemOrAdjustment | java | org.apache.ofbiz.order.order.OrderReturnServices | updateReturnItemOrAdjustment | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | updateReturnItemsStatus | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderReturnServicesScript.groovy | updateReturnItemsStatus | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | updateReturnStatusFromReceipt | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderReturnServicesScript.groovy | updateReturnStatusFromReceipt | applications/order/servicedef/services_return.xml | 主要フロー外（スコープ外） |
| サービス | updateShipGroupShipInfo | java | org.apache.ofbiz.order.order.OrderServices | updateShipGroupShipInfo | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | updateShippingMethodAndCharges | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/order/OrderServicesScript.groovy | updateShippingMethodAndCharges | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | updateShoppingList | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/shoppinglist/ShoppingListServicesScript.groovy | updateShoppingList | applications/order/servicedef/services_shoppinglist.xml | 主要フロー外（スコープ外） |
| サービス | updateShoppingListItem | groovy | component://order/src/main/groovy/org/apache/ofbiz/order/shoppinglist/ShoppingListServicesScript.groovy | updateShoppingListItem | applications/order/servicedef/services_shoppinglist.xml | 主要フロー外（スコープ外） |
| サービス | updateShoppingListQuantitiesFromOrder | java | org.apache.ofbiz.order.shoppinglist.ShoppingListServices | updateShoppingListQuantitiesFromOrder | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | updateTrackingNumber | java | org.apache.ofbiz.order.order.OrderServices | updateTrackingNumber | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| サービス | uploadOrderContentFile | group |  |  | applications/order/servicedef/services.xml | 主要フロー外（スコープ外） |
| テンプレート | AddItemsToShoppingList.ftl | ftl |  |  | applications/order/template/entry/cart/AddItemsToShoppingList.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | AddOrderAttachments.ftl | ftl |  |  | applications/order/template/order/AddOrderAttachments.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | AddedNoteCustRequestNotification.ftl | ftl |  |  | applications/order/template/request/AddedNoteCustRequestNotification.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | AdditionalPartyListing.ftl | ftl |  |  | applications/order/template/entry/AdditionalPartyListing.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | AdvancedSearch.ftl | ftl |  |  | applications/order/template/entry/catalog/AdvancedSearch.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | AppendOrderItem.ftl | ftl |  |  | applications/order/template/order/AppendOrderItem.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | AssociatedProducts.ftl | ftl |  |  | applications/order/template/entry/cart/AssociatedProducts.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | BillSettings.ftl | ftl |  |  | applications/order/template/entry/BillSettings.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | BreadCrumbs.ftl | ftl |  |  | applications/order/template/entry/catalog/BreadCrumbs.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | CategoryDetail.ftl | ftl |  |  | applications/order/template/entry/catalog/CategoryDetail.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | CheckInits.ftl | ftl |  |  | applications/order/template/entry/CheckInits.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | CheckoutOptions.ftl | ftl |  |  | applications/order/template/entry/CheckoutOptions.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | CheckoutPayment.ftl | ftl |  |  | applications/order/template/entry/CheckoutPayment.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | CheckoutShippingAddress.ftl | ftl |  |  | applications/order/template/entry/CheckoutShippingAddress.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ChooseCatalog.ftl | ftl |  |  | applications/order/template/entry/catalog/ChooseCatalog.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | CompanyHeader.fo.ftl | ftl |  |  | applications/order/template/order/CompanyHeader.fo.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | CompareProducts.ftl | ftl |  |  | applications/order/template/entry/catalog/CompareProducts.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | CompareProductsList.ftl | ftl |  |  | applications/order/template/entry/catalog/CompareProductsList.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | CompletedCustRequestNotification.ftl | ftl |  |  | applications/order/template/request/CompletedCustRequestNotification.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ConfigProductDetail.ftl | ftl |  |  | applications/order/template/entry/catalog/ConfigProductDetail.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | CopyQuote.ftl | ftl |  |  | applications/order/template/quote/CopyQuote.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | CopyRequestItem.ftl | ftl |  |  | applications/order/template/request/CopyRequestItem.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | CreateAllocationPlan.ftl | ftl |  |  | applications/order/template/order/CreateAllocationPlan.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | CreateCustRequestNotification.ftl | ftl |  |  | applications/order/template/request/CreateCustRequestNotification.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | CustSettings.ftl | ftl |  |  | applications/order/template/entry/CustSettings.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | CustomerTaxInfo.ftl | ftl |  |  | applications/order/template/entry/CustomerTaxInfo.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | EditOrderItems.ftl | ftl |  |  | applications/order/template/order/EditOrderItems.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | EditProduct.ftl | ftl |  |  | applications/order/template/entry/catalog/EditProduct.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | EditShipAddress.ftl | ftl |  |  | applications/order/template/entry/EditShipAddress.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | EmailProcessNotify.ftl | ftl |  |  | applications/order/template/email/EmailProcessNotify.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | FindOrders.ftl | ftl |  |  | applications/order/template/order/FindOrders.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | GiftCertificates.ftl | ftl |  |  | applications/order/template/order/GiftCertificates.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | InlineProductDetail.ftl | ftl |  |  | applications/order/template/entry/catalog/InlineProductDetail.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | Javascript.ftl | ftl |  |  | applications/order/template/entry/cart/Javascript.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | KeywordSearch.ftl | ftl |  |  | applications/order/template/entry/catalog/KeywordSearch.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | KeywordSearchBox.ftl | ftl |  |  | applications/order/template/entry/catalog/KeywordSearchBox.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ManageQuotePrices.ftl | ftl |  |  | applications/order/template/quote/ManageQuotePrices.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ManageQuotePricesTotals.ftl | ftl |  |  | applications/order/template/quote/ManageQuotePricesTotals.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ManualPromotions.ftl | ftl |  |  | applications/order/template/entry/cart/ManualPromotions.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | MiniCart.ftl | ftl |  |  | applications/order/template/entry/cart/MiniCart.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OptionSettings.ftl | ftl |  |  | applications/order/template/entry/OptionSettings.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderAgreements.ftl | ftl |  |  | applications/order/template/entry/OrderAgreements.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderContactInfo.ftl | ftl |  |  | applications/order/template/order/OrderContactInfo.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderDeliveryUpdatedNotice.ftl | ftl |  |  | applications/order/template/email/OrderDeliveryUpdatedNotice.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderEntryCatalogTabBar.ftl | ftl |  |  | applications/order/template/entry/OrderEntryCatalogTabBar.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderEntryCheckOutTabBar.ftl | ftl |  |  | applications/order/template/entry/OrderEntryCheckOutTabBar.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderEntryTabBar.ftl | ftl |  |  | applications/order/template/entry/OrderEntryTabBar.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderHeaderInfo.ftl | ftl |  |  | applications/order/template/entry/OrderHeaderInfo.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderHeaderInfo.ftl | ftl |  |  | applications/order/template/entry/order/OrderHeaderInfo.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderInfo.ftl | ftl |  |  | applications/order/template/order/OrderInfo.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderItems.ftl | ftl |  |  | applications/order/template/entry/order/OrderItems.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderItems.ftl | ftl |  |  | applications/order/template/order/OrderItems.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderList.ftl | ftl |  |  | applications/order/template/order/OrderList.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderListByDate.ftl | ftl |  |  | applications/order/template/order/OrderListByDate.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderNotes.ftl | ftl |  |  | applications/order/template/order/OrderNotes.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderPaymentInfo.ftl | ftl |  |  | applications/order/template/entry/order/OrderPaymentInfo.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderPaymentInfo.ftl | ftl |  |  | applications/order/template/order/OrderPaymentInfo.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderPurchaseReportPayment.fo.ftl | ftl |  |  | applications/order/template/reports/OrderPurchaseReportPayment.fo.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderPurchaseReportProduct.fo.ftl | ftl |  |  | applications/order/template/reports/OrderPurchaseReportProduct.fo.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderReportBody.fo.ftl | ftl |  |  | applications/order/template/order/OrderReportBody.fo.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderReportConditions.fo.ftl | ftl |  |  | applications/order/template/order/OrderReportConditions.fo.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderReportContactMechs.fo.ftl | ftl |  |  | applications/order/template/order/OrderReportContactMechs.fo.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderReportHeaderInfo.fo.ftl | ftl |  |  | applications/order/template/order/OrderReportHeaderInfo.fo.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderSalesReps.ftl | ftl |  |  | applications/order/template/order/OrderSalesReps.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderShippingInfo.ftl | ftl |  |  | applications/order/template/order/OrderShippingInfo.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderShortcuts.ftl | ftl |  |  | applications/order/template/entry/OrderShortcuts.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderStats.ftl | ftl |  |  | applications/order/template/order/OrderStats.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderTaskList.ftl | ftl |  |  | applications/order/template/task/OrderTaskList.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderTerms.ftl | ftl |  |  | applications/order/template/entry/OrderTerms.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | OrderTerms.ftl | ftl |  |  | applications/order/template/order/OrderTerms.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | PaymentSetup.ftl | ftl |  |  | applications/order/template/setup/PaymentSetup.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ProductDetail.ftl | ftl |  |  | applications/order/template/entry/catalog/ProductDetail.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ProductSummary.ftl | ftl |  |  | applications/order/template/entry/catalog/ProductSummary.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ProductUomDropDownOnly.ftl | ftl |  |  | applications/order/template/entry/catalog/ProductUomDropDownOnly.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ProductVariantJS.ftl | ftl |  |  | applications/order/template/entry/catalog/ProductVariantJS.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | PromoCodes.ftl | ftl |  |  | applications/order/template/entry/cart/PromoCodes.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | PromoText.ftl | ftl |  |  | applications/order/template/entry/cart/PromoText.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | PromoUseDetailsInline.ftl | ftl |  |  | applications/order/template/entry/cart/PromoUseDetailsInline.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | PromotionCategories.ftl | ftl |  |  | applications/order/template/entry/cart/PromotionCategories.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | PromotionDetails.ftl | ftl |  |  | applications/order/template/entry/cart/PromotionDetails.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | PromotionProducts.ftl | ftl |  |  | applications/order/template/entry/cart/PromotionProducts.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | PromotionsApplied.ftl | ftl |  |  | applications/order/template/entry/cart/PromotionsApplied.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | PurchasesByOrganizationReport.fo.ftl | ftl |  |  | applications/order/template/reports/PurchasesByOrganizationReport.fo.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | QuickAdd.ftl | ftl |  |  | applications/order/template/entry/catalog/QuickAdd.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | QuickAddSummary.ftl | ftl |  |  | applications/order/template/entry/catalog/QuickAddSummary.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | QuickReturn.ftl | ftl |  |  | applications/order/template/return/QuickReturn.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | QuoteDate.ftl | ftl |  |  | applications/order/template/quote/QuoteDate.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | QuoteInfo.ftl | ftl |  |  | applications/order/template/quote/QuoteInfo.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | QuoteLinks.ftl | ftl |  |  | applications/order/template/request/QuoteLinks.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | QuoteReportBody.fo.ftl | ftl |  |  | applications/order/template/quote/QuoteReportBody.fo.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | QuoteReportContactMechs.fo.ftl | ftl |  |  | applications/order/template/quote/QuoteReportContactMechs.fo.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | QuoteReportHeaderInfo.fo.ftl | ftl |  |  | applications/order/template/quote/QuoteReportHeaderInfo.fo.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | QuoteRoles.ftl | ftl |  |  | applications/order/template/quote/QuoteRoles.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ReceivePayment.ftl | ftl |  |  | applications/order/template/order/ReceivePayment.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | RequestContactMech.ftl | ftl |  |  | applications/order/template/request/RequestContactMech.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | RequestDate.ftl | ftl |  |  | applications/order/template/request/RequestDate.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | RequestItemNotes.ftl | ftl |  |  | applications/order/template/request/RequestItemNotes.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ReturnItemInc.ftl | ftl |  |  | applications/order/template/return/ReturnItemInc.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ReturnItems.ftl | ftl |  |  | applications/order/template/return/ReturnItems.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ReturnLinks.ftl | ftl |  |  | applications/order/template/return/ReturnLinks.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ReturnList.ftl | ftl |  |  | applications/order/template/return/ReturnList.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ReturnReportBody.fo.ftl | ftl |  |  | applications/order/template/return/ReturnReportBody.fo.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ReturnReportConditions.fo.ftl | ftl |  |  | applications/order/template/return/ReturnReportConditions.fo.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ReturnReportContactMechs.fo.ftl | ftl |  |  | applications/order/template/return/ReturnReportContactMechs.fo.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ReturnReportHeaderInfo.fo.ftl | ftl |  |  | applications/order/template/return/ReturnReportHeaderInfo.fo.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | SalesByStoreReport.fo.ftl | ftl |  |  | applications/order/template/reports/SalesByStoreReport.fo.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | SendConfirmationEmail.ftl | ftl |  |  | applications/order/template/order/SendConfirmationEmail.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | SendPurchaseOrderEmail.ftl | ftl |  |  | applications/order/template/order/SendPurchaseOrderEmail.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | SetAdditionalParty.ftl | ftl |  |  | applications/order/template/entry/SetAdditionalParty.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | SetItemShipGroups.ftl | ftl |  |  | applications/order/template/entry/SetItemShipGroups.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ShipGroupConfirmSummary.ftl | ftl |  |  | applications/order/template/entry/order/ShipGroupConfirmSummary.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ShipGroups.fo.ftl | ftl |  |  | applications/order/template/order/ShipGroups.fo.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ShipSettings.ftl | ftl |  |  | applications/order/template/entry/ShipSettings.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ShowAllPromotions.ftl | ftl |  |  | applications/order/template/entry/cart/ShowAllPromotions.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ShowCart.ftl | ftl |  |  | applications/order/template/entry/cart/ShowCart.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ShowCartItems.ftl | ftl |  |  | applications/order/template/entry/cart/ShowCartItems.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | SideDeepCategory.ftl | ftl |  |  | applications/order/template/entry/catalog/SideDeepCategory.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | SplitShip.ftl | ftl |  |  | applications/order/template/entry/SplitShip.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | Survey.ftl | ftl |  |  | applications/order/template/entry/Survey.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | TagSearch.ftl | ftl |  |  | applications/order/template/entry/catalog/TagSearch.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | Transitions.ftl | ftl |  |  | applications/order/template/order/Transitions.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ViewAllocationPlan.ftl | ftl |  |  | applications/order/template/order/ViewAllocationPlan.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ViewImage.ftl | ftl |  |  | applications/order/template/order/ViewImage.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ViewQuoteItemInfo.ftl | ftl |  |  | applications/order/template/quote/ViewQuoteItemInfo.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ViewQuoteProfit.ftl | ftl |  |  | applications/order/template/quote/ViewQuoteProfit.ftl | 画面は API で代替、UI 再構築は別途 |
| テンプレート | ViewRequestItemInfo.ftl | ftl |  |  | applications/order/template/request/ViewRequestItemInfo.ftl | 画面は API で代替、UI 再構築は別途 |
| 画面 | AddCustRequestContent | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | AddCustRequestItemWorkEffort | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | AddFromShoppingList | form |  |  | applications/order/widget/ordermgr/OrderEntryForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | AddFromShoppingList | screen |  |  | applications/order/widget/ordermgr/OrderEntryScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | AddFromShoppingListAll | form |  |  | applications/order/widget/ordermgr/OrderEntryForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | AddGiftCertificate | screen |  |  | applications/order/widget/ordermgr/OrderEntryCartScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | AddNoteCustRequestNotification | screen |  |  | applications/order/widget/ordermgr/CustRequestScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | AddOrEditQuoteNote | form |  |  | applications/order/widget/ordermgr/QuoteForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | AddOrderAttachments | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | AddOrderTerm | form |  |  | applications/order/widget/ordermgr/OrderForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | AddQuoteWorkEffort | form |  |  | applications/order/widget/ordermgr/QuoteWorkEffortForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | AddQuoteWorkEffort | screen |  |  | applications/order/widget/ordermgr/QuoteWorkEffortScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ApproveRequirements | form |  |  | applications/order/widget/ordermgr/RequirementForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ApproveRequirements | screen |  |  | applications/order/widget/ordermgr/RequirementScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ApprovedProductRequirements | form |  |  | applications/order/widget/ordermgr/RequirementForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ApprovedProductRequirements | screen |  |  | applications/order/widget/ordermgr/RequirementScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ApprovedProductRequirementsByVendor | form |  |  | applications/order/widget/ordermgr/RequirementForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ApprovedProductRequirementsByVendor | screen |  |  | applications/order/widget/ordermgr/RequirementScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ApprovedProductRequirementsByVendorReport | screen |  |  | applications/order/widget/ordermgr/RequirementScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ApprovedProductRequirementsList | form |  |  | applications/order/widget/ordermgr/RequirementForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ApprovedProductRequirementsReport | screen |  |  | applications/order/widget/ordermgr/RequirementScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ApprovedProductRequirementsSubmit | form |  |  | applications/order/widget/ordermgr/RequirementForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ApprovedProductRequirementsSummary | form |  |  | applications/order/widget/ordermgr/RequirementForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | BillSettings | screen |  |  | applications/order/widget/ordermgr/OrderEntryOrderScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | CheckInits | screen |  |  | applications/order/widget/ordermgr/OrderEntryScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | CommonAllocationPlanDecorator | screen |  |  | applications/order/widget/ordermgr/AllocationPlanScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | CommonOrderCatalogDecorator | screen |  |  | applications/order/widget/ordermgr/OrderEntryCommonScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | CommonOrderCheckoutDecorator | screen |  |  | applications/order/widget/ordermgr/OrderEntryOrderScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | CommonOrderEntryDecorator | screen |  |  | applications/order/widget/ordermgr/OrderEntryCommonScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | CommonOrderHeaderDecorator | screen |  |  | applications/order/widget/ordermgr/OrderHeaderScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | CommonOrderReturnDecorator | screen |  |  | applications/order/widget/ordermgr/OrderReturnScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | CommonOrderSetupDecorator | screen |  |  | applications/order/widget/ordermgr/OrderSetupScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | CommonOrderTaskDecorator | screen |  |  | applications/order/widget/ordermgr/OrderTaskScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | CommonOrderViewDecorator | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | CommonPartyDecorator | screen |  |  | applications/order/widget/ordermgr/CommonScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | CommonPopUpDecorator | screen |  |  | applications/order/widget/ordermgr/CommonScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | CommonQuoteDecorator | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | CommonQuoteDecorator | screen |  |  | applications/order/widget/ordermgr/QuoteWorkEffortScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | CommonRequestDecorator | screen |  |  | applications/order/widget/ordermgr/CommonScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | CommonRequirementDecorator | screen |  |  | applications/order/widget/ordermgr/RequirementScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | CommonRequirementsDecorator | screen |  |  | applications/order/widget/ordermgr/RequirementScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | CompanyLogo | screen |  |  | applications/order/widget/ordermgr/OrderPrintScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | CompletedCustRequestNotification | screen |  |  | applications/order/widget/ordermgr/CustRequestScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ConfirmOrder | screen |  |  | applications/order/widget/ordermgr/OrderEntryOrderScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | CreateAllocationPlan | screen |  |  | applications/order/widget/ordermgr/AllocationPlanScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | CreateCustRequestNotification | screen |  |  | applications/order/widget/ordermgr/CustRequestScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | CreateNewOrderMessage | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | CreateQuoteAndQuoteItemForRequest | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | CreateQuoteAndQuoteItemForRequest | screen |  |  | applications/order/widget/ordermgr/CustRequestScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | CustSettings | screen |  |  | applications/order/widget/ordermgr/OrderEntryOrderScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | DefaultOrderPDF | screen |  |  | applications/order/widget/ordermgr/OrderPrintScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | DefaultQuotePDF | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditAllocationPlan | screen |  |  | applications/order/widget/ordermgr/AllocationPlanScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditCustReqStatusId | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditCustRequest | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditCustRequestContent | screen |  |  | applications/order/widget/ordermgr/CustRequestScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditCustRequestItem | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditCustRetStatusId | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditOrderByCustomer | form |  |  | applications/order/widget/ordermgr/OrderForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditOrderHeader | form |  |  | applications/order/widget/ordermgr/OrderForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditOrderHeader | screen |  |  | applications/order/widget/ordermgr/OrderHeaderScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditQuote | form |  |  | applications/order/widget/ordermgr/QuoteForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditQuote | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditQuoteAdjustment | form |  |  | applications/order/widget/ordermgr/QuoteForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditQuoteAdjustment | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditQuoteAttribute | form |  |  | applications/order/widget/ordermgr/QuoteForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditQuoteAttribute | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditQuoteCoefficient | form |  |  | applications/order/widget/ordermgr/QuoteForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditQuoteCoefficient | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditQuoteItem | form |  |  | applications/order/widget/ordermgr/QuoteForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditQuoteItem | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditQuoteItemForRequest | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditQuoteItemForRequest | screen |  |  | applications/order/widget/ordermgr/CustRequestScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditQuoteNote | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditQuoteReportMail | form |  |  | applications/order/widget/ordermgr/QuoteForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditQuoteReportMail | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditQuoteRole | form |  |  | applications/order/widget/ordermgr/QuoteForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditQuoteRole | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditQuoteTerm | form |  |  | applications/order/widget/ordermgr/QuoteForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditQuoteTerm | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditQuoteTermItem | form |  |  | applications/order/widget/ordermgr/QuoteForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditQuoteTermItem | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditQuoteWorkEffort | form |  |  | applications/order/widget/ordermgr/QuoteWorkEffortForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditQuoteWorkEffort | screen |  |  | applications/order/widget/ordermgr/QuoteWorkEffortScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditRequest | screen |  |  | applications/order/widget/ordermgr/CustRequestScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditRequestCustomer | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditRequestCustomer | screen |  |  | applications/order/widget/ordermgr/CustRequestScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditRequestItem | screen |  |  | applications/order/widget/ordermgr/CustRequestScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditRequestItemMenu | menu |  |  | applications/order/widget/ordermgr/OrderMenus.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditRequestItemNote | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditRequestItemWorkEfforts | screen |  |  | applications/order/widget/ordermgr/CustRequestScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditRequestRole | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditRequirement | form |  |  | applications/order/widget/ordermgr/RequirementForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditRequirement | screen |  |  | applications/order/widget/ordermgr/RequirementScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditRequirementRole | form |  |  | applications/order/widget/ordermgr/RequirementForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditRequirementRole | screen |  |  | applications/order/widget/ordermgr/RequirementScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditReturn | form |  |  | applications/order/widget/ordermgr/ReturnForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditShipAddress | screen |  |  | applications/order/widget/ordermgr/OrderEntryOrderScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditShopList | form |  |  | applications/order/widget/ordermgr/OrderShopListForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | EditSmallCustRequest | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | FindAllocationPlan | form |  |  | applications/order/widget/ordermgr/AllocationPlanForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | FindAllocationPlan | screen |  |  | applications/order/widget/ordermgr/AllocationPlanScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | FindApprovedProductRequirements | form |  |  | applications/order/widget/ordermgr/RequirementForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | FindNotApprovedRequirements | form |  |  | applications/order/widget/ordermgr/RequirementForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | FindQuote | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | FindQuoteForCart | screen |  |  | applications/order/widget/ordermgr/OrderEntryScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | FindQuotes | form |  |  | applications/order/widget/ordermgr/OrderEntryForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | FindQuotes | form |  |  | applications/order/widget/ordermgr/QuoteForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | FindRequest | screen |  |  | applications/order/widget/ordermgr/CustRequestScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | FindRequests | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | FindRequirements | form |  |  | applications/order/widget/ordermgr/OrderEntryForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | FindRequirements | form |  |  | applications/order/widget/ordermgr/RequirementForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | FindRequirements | screen |  |  | applications/order/widget/ordermgr/RequirementScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | FindReturns | form |  |  | applications/order/widget/ordermgr/ReturnForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | IncomingCustRequests | screen |  |  | applications/order/widget/ordermgr/CustRequestScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListCustRequestContent | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListCustRequestItemWorkEfforts | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListCustRequests | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListCustRequests | screen |  |  | applications/order/widget/ordermgr/CustRequestScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListCustReturns | screen |  |  | applications/order/widget/ordermgr/CustRequestScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListCustomerOrders | form |  |  | applications/order/widget/ordermgr/OrderForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListCustomerOrders | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListLookupCustRequest | form |  |  | applications/order/widget/ordermgr/FieldLookupForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListLookupCustRequestItem | form |  |  | applications/order/widget/ordermgr/FieldLookupForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListLookupOrderHeader | form |  |  | applications/order/widget/ordermgr/FieldLookupForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListLookupOrderHeaderAndShipInfo | form |  |  | applications/order/widget/ordermgr/FieldLookupForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListLookupQuote | form |  |  | applications/order/widget/ordermgr/FieldLookupForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListLookupQuoteItem | form |  |  | applications/order/widget/ordermgr/FieldLookupForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListLookupRequirement | form |  |  | applications/order/widget/ordermgr/FieldLookupForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListLookupReturnHeader | form |  |  | applications/order/widget/ordermgr/FieldLookupForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListLookupShoppingList | form |  |  | applications/order/widget/ordermgr/FieldLookupForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListMyCustRequests | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListOrderHeaders | form |  |  | applications/order/widget/ordermgr/OrderForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListOrderHeaders | screen |  |  | applications/order/widget/ordermgr/OrderHeaderScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListOrderTerms | form |  |  | applications/order/widget/ordermgr/OrderForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListOrderTerms | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListPurchaseOrders | form |  |  | applications/order/widget/ordermgr/OrderForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListPurchaseOrders | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListQuoteAdjustments | form |  |  | applications/order/widget/ordermgr/QuoteForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListQuoteAdjustments | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListQuoteAttributes | form |  |  | applications/order/widget/ordermgr/QuoteForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListQuoteAttributes | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListQuoteCoefficients | form |  |  | applications/order/widget/ordermgr/QuoteForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListQuoteCoefficients | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListQuoteInfo | form |  |  | applications/order/widget/ordermgr/QuoteForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListQuoteInfo | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListQuoteItems | form |  |  | applications/order/widget/ordermgr/QuoteForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListQuoteItems | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListQuoteNoteInfo | form |  |  | applications/order/widget/ordermgr/QuoteForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListQuoteNoteInfo | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListQuoteNotes | form |  |  | applications/order/widget/ordermgr/QuoteForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListQuoteNotes | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListQuoteRoles | form |  |  | applications/order/widget/ordermgr/QuoteForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListQuoteRoles | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListQuoteTermItem | form |  |  | applications/order/widget/ordermgr/QuoteForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListQuoteTerms | form |  |  | applications/order/widget/ordermgr/QuoteForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListQuoteTerms | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListQuoteWorkEfforts | form |  |  | applications/order/widget/ordermgr/QuoteWorkEffortForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListQuoteWorkEfforts | screen |  |  | applications/order/widget/ordermgr/QuoteWorkEffortScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListQuotes | form |  |  | applications/order/widget/ordermgr/OrderEntryForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListQuotes | form |  |  | applications/order/widget/ordermgr/QuoteForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListRequestItemNotes | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListRequestItemRequirements | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListRequestItemWorkEffortReq | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListRequestItems | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListRequestList | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListRequestQuoteItems | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListRequestRoles | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListRequests | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListRequirementCustRequests | form |  |  | applications/order/widget/ordermgr/RequirementForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListRequirementCustRequests | screen |  |  | applications/order/widget/ordermgr/RequirementScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListRequirementOrders | form |  |  | applications/order/widget/ordermgr/RequirementForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListRequirementOrders | screen |  |  | applications/order/widget/ordermgr/RequirementScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListRequirementRoles | form |  |  | applications/order/widget/ordermgr/RequirementForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListRequirementRoles | screen |  |  | applications/order/widget/ordermgr/RequirementScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListRequirements | form |  |  | applications/order/widget/ordermgr/RequirementForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListReturns | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListSalesOrders | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ListShopLists | form |  |  | applications/order/widget/ordermgr/OrderShopListForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupAssociatedProducts | screen |  |  | applications/order/widget/ordermgr/OrderEntryCartScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupAssociatedProducts | form |  |  | applications/order/widget/ordermgr/OrderEntryForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupBulkAddProducts | screen |  |  | applications/order/widget/ordermgr/OrderEntryCartScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupBulkAddProducts | form |  |  | applications/order/widget/ordermgr/OrderEntryForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupBulkAddSupplierProducts | screen |  |  | applications/order/widget/ordermgr/OrderEntryCartScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupBulkAddSupplierProducts | form |  |  | applications/order/widget/ordermgr/OrderEntryForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupBulkAddSupplierProductsInApprovedOrder | screen |  |  | applications/order/widget/ordermgr/OrderEntryOrderScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupBulkAddSupplierProductsInApprovedOrder | form |  |  | applications/order/widget/ordermgr/OrderForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupCustRequest | form |  |  | applications/order/widget/ordermgr/FieldLookupForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupCustRequest | screen |  |  | applications/order/widget/ordermgr/LookupScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupCustRequestItem | form |  |  | applications/order/widget/ordermgr/FieldLookupForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupCustRequestItem | screen |  |  | applications/order/widget/ordermgr/LookupScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupOrderHeader | form |  |  | applications/order/widget/ordermgr/FieldLookupForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupOrderHeader | screen |  |  | applications/order/widget/ordermgr/LookupScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupOrderHeaderAndShipInfo | form |  |  | applications/order/widget/ordermgr/FieldLookupForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupOrderHeaderAndShipInfo | screen |  |  | applications/order/widget/ordermgr/LookupScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupPurchaseOrderHeaderAndShipInfo | form |  |  | applications/order/widget/ordermgr/FieldLookupForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupPurchaseOrderHeaderAndShipInfo | screen |  |  | applications/order/widget/ordermgr/LookupScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupQuote | form |  |  | applications/order/widget/ordermgr/FieldLookupForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupQuote | screen |  |  | applications/order/widget/ordermgr/LookupScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupQuoteItem | form |  |  | applications/order/widget/ordermgr/FieldLookupForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupQuoteItem | screen |  |  | applications/order/widget/ordermgr/LookupScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupRequirement | form |  |  | applications/order/widget/ordermgr/FieldLookupForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupRequirement | screen |  |  | applications/order/widget/ordermgr/LookupScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupReturnHeader | form |  |  | applications/order/widget/ordermgr/FieldLookupForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupReturnHeader | screen |  |  | applications/order/widget/ordermgr/LookupScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupShoppingList | form |  |  | applications/order/widget/ordermgr/FieldLookupForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | LookupShoppingList | screen |  |  | applications/order/widget/ordermgr/LookupScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | Main | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | MainActionMenu | menu |  |  | applications/order/widget/ordermgr/OrderMenus.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ManageQuotePrices | form |  |  | applications/order/widget/ordermgr/QuoteForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ManageQuotePrices | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OpenOrderItemsList | form |  |  | applications/order/widget/ordermgr/ReportForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OpenOrderItemsReport | form |  |  | applications/order/widget/ordermgr/ReportForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OpenOrderItemsReport | screen |  |  | applications/order/widget/ordermgr/ReportScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OpenOrderItemsTotal | form |  |  | applications/order/widget/ordermgr/ReportForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OptionSettings | screen |  |  | applications/order/widget/ordermgr/OrderEntryOrderScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderAgreements | screen |  |  | applications/order/widget/ordermgr/OrderEntryScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderAppBar | menu |  |  | applications/order/widget/ordermgr/OrderMenus.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderConversations | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderDeliveryScheduleInfo | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderFindOrder | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderFindReturn | screen |  |  | applications/order/widget/ordermgr/OrderReturnScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderHeaderListView | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderHeaderView | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderHistory | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderItemEdit | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderNewNote | form |  |  | applications/order/widget/ordermgr/OrderForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderNewNote | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderPDF | screen |  |  | applications/order/widget/ordermgr/OrderPrintScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderPaymentSetup | screen |  |  | applications/order/widget/ordermgr/OrderSetupScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderPurchasePaymentOptions | form |  |  | applications/order/widget/ordermgr/ReportForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderPurchaseProductOptions | form |  |  | applications/order/widget/ordermgr/ReportForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderPurchaseReportOptions | form |  |  | applications/order/widget/ordermgr/ReportForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderPurchaseReportOptions | screen |  |  | applications/order/widget/ordermgr/ReportScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderPurchaseReportPayment | screen |  |  | applications/order/widget/ordermgr/ReportScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderPurchaseReportProduct | screen |  |  | applications/order/widget/ordermgr/ReportScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderQuantityHistory | form |  |  | applications/order/widget/ordermgr/OrderForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderQuantityHistory | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderQuickReturn | screen |  |  | applications/order/widget/ordermgr/OrderReturnScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderReceivePayment | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderReportOpenOrderItems | screen |  |  | applications/order/widget/ordermgr/ReportScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderReportPurchasesByOrganization | screen |  |  | applications/order/widget/ordermgr/ReportScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderReportPurchasesByPaymentMethod | screen |  |  | applications/order/widget/ordermgr/ReportScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderReportPurchasesByProduct | screen |  |  | applications/order/widget/ordermgr/ReportScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderReportSalesByStore | screen |  |  | applications/order/widget/ordermgr/ReportScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderReturnHeader | screen |  |  | applications/order/widget/ordermgr/OrderReturnScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderReturnHistory | screen |  |  | applications/order/widget/ordermgr/OrderReturnScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderReturnItems | screen |  |  | applications/order/widget/ordermgr/OrderReturnScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderReturnList | screen |  |  | applications/order/widget/ordermgr/OrderReturnScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderRoleInfo | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderSalesReps | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderShipmentMethodHistory | form |  |  | applications/order/widget/ordermgr/OrderForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderShipmentMethodHistory | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderShortcutAppBar | menu |  |  | applications/order/widget/ordermgr/OrderMenus.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderStats | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderTaskList | screen |  |  | applications/order/widget/ordermgr/OrderTaskScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderTerms | screen |  |  | applications/order/widget/ordermgr/OrderEntryOrderScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderUnitPriceHistory | form |  |  | applications/order/widget/ordermgr/OrderForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OrderUnitPriceHistory | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | OverviewRequestItems | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ProductUomDropDownOnly | screen |  |  | applications/order/widget/ordermgr/OrderEntryCatalogScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | PurchasesByOrganizationReport | form |  |  | applications/order/widget/ordermgr/ReportForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | PurchasesByOrganizationReport | screen |  |  | applications/order/widget/ordermgr/ReportScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | QuickAddOptionalParty | form |  |  | applications/order/widget/ordermgr/PartySettingsForm.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | QuoteItemSubTabBar | menu |  |  | applications/order/widget/ordermgr/OrderMenus.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | QuoteNewNote | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | QuoteReport | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | QuoteSubTabBar | menu |  |  | applications/order/widget/ordermgr/OrderMenus.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | QuoteTabBar | menu |  |  | applications/order/widget/ordermgr/OrderMenus.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | QuoteTermSubTabBar | menu |  |  | applications/order/widget/ordermgr/OrderMenus.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ReceivedQuantityHistory | screen |  |  | applications/order/widget/ordermgr/OrderReturnScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | RequestInfo | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | RequestItemNotes | screen |  |  | applications/order/widget/ordermgr/CustRequestScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | RequestItemQuotes | screen |  |  | applications/order/widget/ordermgr/CustRequestScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | RequestItemRequirements | screen |  |  | applications/order/widget/ordermgr/CustRequestScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | RequestItems | screen |  |  | applications/order/widget/ordermgr/CustRequestScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | RequestRoles | screen |  |  | applications/order/widget/ordermgr/CustRequestScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | RequestScreenletMenu | menu |  |  | applications/order/widget/ordermgr/OrderMenus.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | RequestSubTabBar | menu |  |  | applications/order/widget/ordermgr/OrderMenus.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | RequestTabBar | menu |  |  | applications/order/widget/ordermgr/OrderMenus.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | RequirementTabBar | menu |  |  | applications/order/widget/ordermgr/OrderMenus.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | RequirementsForSupplier | screen |  |  | applications/order/widget/ordermgr/OrderEntryScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | RequirementsList | form |  |  | applications/order/widget/ordermgr/OrderEntryForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | RequirementsTabBar | menu |  |  | applications/order/widget/ordermgr/OrderMenus.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ReturnPDF | screen |  |  | applications/order/widget/ordermgr/OrderPrintScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ReturnPriceHistory | screen |  |  | applications/order/widget/ordermgr/OrderReturnScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ReturnQuantityHistory | screen |  |  | applications/order/widget/ordermgr/OrderReturnScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ReturnReasonHistory | screen |  |  | applications/order/widget/ordermgr/OrderReturnScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ReturnStatusHistory | screen |  |  | applications/order/widget/ordermgr/OrderReturnScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ReturnTypeHistory | screen |  |  | applications/order/widget/ordermgr/OrderReturnScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | SalesByStoreReport | form |  |  | applications/order/widget/ordermgr/ReportForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | SalesByStoreReport | screen |  |  | applications/order/widget/ordermgr/ReportScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | SendOrderCompletion | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | SendOrderConfirmation | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | SendPOEmail | screen |  |  | applications/order/widget/ordermgr/OrderHeaderScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | SetAdditionalParty | screen |  |  | applications/order/widget/ordermgr/OrderEntryOrderScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | SetItemShipGroups | screen |  |  | applications/order/widget/ordermgr/OrderEntryOrderScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ShipGroupsPDF | screen |  |  | applications/order/widget/ordermgr/OrderPrintScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ShipSettings | screen |  |  | applications/order/widget/ordermgr/OrderEntryOrderScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ShortcutApp | screen |  |  | applications/order/widget/ordermgr/CommonScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ShowCart | screen |  |  | applications/order/widget/ordermgr/OrderEntryCartScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | UpdateDeliveryScheduleInformation | form |  |  | applications/order/widget/ordermgr/OrderDeliveryScheduleForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ViewAllocationPlan | screen |  |  | applications/order/widget/ordermgr/AllocationPlanScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ViewCustRequest | screen |  |  | applications/order/widget/ordermgr/CustRequestScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ViewImage | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ViewQuote | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ViewQuoteInfo | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ViewQuoteItemInfo | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ViewQuoteProfit | form |  |  | applications/order/widget/ordermgr/QuoteForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ViewQuoteProfit | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ViewQuoteSimple | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ViewQuoteTemplate | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ViewRequest | screen |  |  | applications/order/widget/ordermgr/CustRequestScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ViewRequestCommunicationEvents | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ViewRequestItemInfo | screen |  |  | applications/order/widget/ordermgr/CustRequestScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ViewRequestRoles | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ViewRequestStatus | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ViewRequestWorkEfforts | form |  |  | applications/order/widget/ordermgr/CustRequestForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ViewShoppingLists | form |  |  | applications/order/widget/ordermgr/OrderEntryForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ViewShoppingLists | screen |  |  | applications/order/widget/ordermgr/OrderEntryScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | advancedsearch | screen |  |  | applications/order/widget/ordermgr/OrderEntryCatalogScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | breadcrumbs | screen |  |  | applications/order/widget/ordermgr/OrderEntryCatalogScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | category | screen |  |  | applications/order/widget/ordermgr/OrderEntryCatalogScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | category-include | screen |  |  | applications/order/widget/ordermgr/OrderEntryCatalogScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | categorydetail | screen |  |  | applications/order/widget/ordermgr/OrderEntryCatalogScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | checkoutpayment | screen |  |  | applications/order/widget/ordermgr/OrderEntryOrderScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | checkoutshippingaddress | screen |  |  | applications/order/widget/ordermgr/OrderEntryOrderScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | choosecatalog | screen |  |  | applications/order/widget/ordermgr/OrderEntryCatalogScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | compareProducts | screen |  |  | applications/order/widget/ordermgr/OrderEntryCatalogScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | compareproductslist | screen |  |  | applications/order/widget/ordermgr/OrderEntryCatalogScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | configproductdetail | screen |  |  | applications/order/widget/ordermgr/OrderEntryCatalogScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | createNewOrderConversation | form |  |  | applications/order/widget/ordermgr/OrderForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | createNewOrderMessage | form |  |  | applications/order/widget/ordermgr/OrderForms.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | customertaxinfo | screen |  |  | applications/order/widget/ordermgr/OrderEntryOrderScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | inlineProductDetail | screen |  |  | applications/order/widget/ordermgr/OrderEntryCatalogScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | keywordsearch | screen |  |  | applications/order/widget/ordermgr/OrderEntryCatalogScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | keywordsearchbox | screen |  |  | applications/order/widget/ordermgr/OrderEntryCatalogScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | leftbar | screen |  |  | applications/order/widget/ordermgr/OrderEntryCommonScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | leftbarCatalog | screen |  |  | applications/order/widget/ordermgr/OrderEntryCommonScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | main-decorator | screen |  |  | applications/order/widget/ordermgr/CommonScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | minicart | screen |  |  | applications/order/widget/ordermgr/OrderEntryCartScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | orderHeader | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | orderHeaderInfo | screen |  |  | applications/order/widget/ordermgr/OrderEntryCartScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | orderShortcuts | screen |  |  | applications/order/widget/ordermgr/OrderEntryCartScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ordercontactinfo | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | orderinfo | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | orderpaymentinfo | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | ordershippinginfo | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | orderterms | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | product | screen |  |  | applications/order/widget/ordermgr/OrderEntryCatalogScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | productdetail | screen |  |  | applications/order/widget/ordermgr/OrderEntryCatalogScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | productsummary | screen |  |  | applications/order/widget/ordermgr/OrderEntryCatalogScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | productvariantjs | screen |  |  | applications/order/widget/ordermgr/OrderEntryCatalogScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | projectAssoOrder | screen |  |  | applications/order/widget/ordermgr/OrderViewScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | promoUseDetailsInline | screen |  |  | applications/order/widget/ordermgr/OrderEntryCartScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | promotion | screen |  |  | applications/order/widget/ordermgr/OrderEntryCartScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | quickFinalizeOrder | screen |  |  | applications/order/widget/ordermgr/OrderEntryOrderScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | quickadd | screen |  |  | applications/order/widget/ordermgr/OrderEntryCatalogScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | quickaddsummary | screen |  |  | applications/order/widget/ordermgr/OrderEntryCatalogScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | quoteDate | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | quoteInfo | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | quoteRoles | screen |  |  | applications/order/widget/ordermgr/QuoteScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | showAllPromotions | screen |  |  | applications/order/widget/ordermgr/OrderEntryCartScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | showPromotionDetails | screen |  |  | applications/order/widget/ordermgr/OrderEntryCartScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | sidedeepcategory | screen |  |  | applications/order/widget/ordermgr/OrderEntryCatalogScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | splitship | screen |  |  | applications/order/widget/ordermgr/OrderEntryOrderScreens.xml | 画面は API で代替、UI 再構築は別途 |
| 画面 | survey | screen |  |  | applications/order/widget/ordermgr/OrderEntryCartScreens.xml | 画面は API で代替、UI 再構築は別途 |
