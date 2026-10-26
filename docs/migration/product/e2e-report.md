# Product Spring Boot 移行 E2E 実測レポート

## 実行環境・方法

- 対象 PR: https://github.com/madmerger/ofbiz-framework/pull/9
- 対象 revision: `8d42c61066ca1aae2a9a4e3ea0627a1a58f84c91`（短縮 `8d42c61066`）。
- 実行日: 2026-10-26 UTC。
- Java: OpenJDK 17.0.19。Spring Boot の `local` プロファイル、H2 インメモリー DB (`product-local`)。
- 起動: `./gradlew :migration:product-springboot:bootRun --args='--spring.profiles.active=local'`。デモパスワードは環境変数から渡し、ここには記載しない。
- **Product モジュールのみを新規プロセスで起動。旧 OFBiz は起動していない。** `ProductLocalSeed` の倉庫・受注・ステータスを起点とし、商品・在庫はケース実行時に作成した。
- 操作面: `http://localhost:8080/swagger-ui/index.html`。Playwright で実際の Swagger UI の展開・Try it out・パラメーター入力・Execute を操作。追跡 GET を含む **全24リクエストは Swagger UI の Execute 経由**。curl や別クライアントによる API 実行は行っていない。
- 認証: Swagger Authorize の HTTP Basic。未認証 → readonly → sensitive → admin → readonly → admin と切り替えた。パスワードはマスクされた入力欄だけに入力。Authorization 値を含む curl 表示ブロックを非表示にし、資格情報を録画・画像に露出させていない。
- 表示: Xvfb 1440×900、Chrome ウィンドウを画面全体へ配置。録画 1440×900、スクリーンショット 1439×812。ケース番号・名称・実行ユーザーを固定バナーに表示した。ビルド・インストールは録画外。
- 定義: `cases.json` は変更せず、ファイル順の18ケースを実行。期待値は同ファイルの各 `id` と突合した。下表の追跡 GET はすべて主リクエストと同じユーザー。
- 動的変数: 06b の実応答から `inventoryItemId=10000`、06d の実応答から `shipmentId=10000` を取得し、後続のリクエストへ代入した。

## ケース別の期待値と実測値

パスは省略せず記載。`ATP` は `availableToPromiseTotal`、`QOH` は `quantityOnHandTotal` を表す。

|ケース ID|名称|リクエスト（method / path / user）|期待 HTTP・主要値|実測 HTTP・主要値|判定|
|---|---|---|---|---|---|
|PRODUCT-E2E-01|未認証アクセスは 401|GET `/api/product/entities/ProductType` / 未認証|401|401、`error=Unauthorized`|合格|
|PRODUCT-E2E-02|権限不足（閲覧専用ユーザーの作成）は 403|POST `/api/product/services/createProductType` / readonly。`productTypeId=E2E_TYPE_RO`|403|403、`errorMessage=Service permission denied`|合格|
|PRODUCT-E2E-03a|機密エンティティ: 閲覧専用ユーザーは 403|GET `/api/product/entities/ShipmentGatewayConfig` / readonly|403|403、`errorMessage=Sensitive entity access denied`|合格|
|PRODUCT-E2E-03b|機密エンティティ: 専用権限ユーザーは 200|GET `/api/product/entities/ShipmentGatewayConfig` / sensitive|200、JSON 配列|200、`[]`（JSON 配列）|合格|
|PRODUCT-E2E-04a|entity-auto 作成 createProductType|POST `/api/product/services/createProductType` / admin。`productTypeId=E2E_TYPE`、`description=E2E 作成`、`isPhysical=Y`、`isDigital=N`|200、`productTypeId=E2E_TYPE`|200、`productTypeId=E2E_TYPE`、`description=E2E 作成`、`responseMessage=success`|合格|
|PRODUCT-E2E-04b|entity-auto 取得|GET `/api/product/entities/ProductType/one?productTypeId=E2E_TYPE` / admin|200、`description=E2E 作成`|200、`description=E2E 作成`|合格|
|PRODUCT-E2E-04c|entity-auto 更新 updateProductType|POST `/api/product/services/updateProductType` / admin。`productTypeId=E2E_TYPE`、`description=E2E 更新`。追跡 GET `/api/product/entities/ProductType/one?productTypeId=E2E_TYPE`|更新200、追跡 GET の `description=E2E 更新`|更新200、追跡 GET 200、`description=E2E 更新`|合格|
|PRODUCT-E2E-04d|entity-auto 削除 deleteProductType|POST `/api/product/services/deleteProductType` / admin。`productTypeId=E2E_TYPE`。追跡 GET `/api/product/entities/ProductType/one?productTypeId=E2E_TYPE`|削除200、追跡 GET 404|削除200、追跡 GET 404、`errorMessage=Entity not found`|合格|
|PRODUCT-E2E-05|simple: facilityPermissionCheck|POST `/api/product/services/facilityPermissionCheck` / readonly。`mainAction=UPDATE`|200、`hasPermission=false`|200、`hasPermission=false`、FACILITY_UPDATE / FACILITY_ADMIN が必要というメッセージ|合格|
|PRODUCT-E2E-06a|主要フロー: 商品登録 createProduct|POST `/api/product/services/createProduct` / admin。`productId=E2E_PROD`、`productTypeId=FINISHED_GOOD`、`internalName=E2E Demo Product`|200、`productId=E2E_PROD`|200、`productId=E2E_PROD`。ProductKeyword / SECA / ECA の no-op 注記あり|合格|
|PRODUCT-E2E-06b|主要フロー: 在庫入荷 receiveInventoryProduct（10個）|POST `/api/product/services/receiveInventoryProduct` / admin。商品 E2E_PROD、倉庫 E2E_WH、NON_SERIAL_INV_ITEM、受入10・拒否0、単価1.25|200、`inventoryItemId` / `receiptId` 非空|200、`inventoryItemId=10000`、`receiptId=10000`、10個受入メッセージ。会計・receipt SECA/ECA の no-op 注記あり|合格|
|PRODUCT-E2E-05b|simple: countProductInventoryOnHand（入荷直後の手持在庫）|POST `/api/product/services/countProductInventoryOnHand` / admin。商品 E2E_PROD、倉庫 E2E_WH、`inventoryCountDate=2099-12-31 23:59:59`|200、`quantityOnHandTotal=10`|200、`quantityOnHandTotal=10`|合格|
|PRODUCT-E2E-06c|主要フロー: 在庫引当 reserveProductInventoryByFacility（2個）|POST `/api/product/services/reserveProductInventoryByFacility` / admin。商品 E2E_PROD、倉庫 E2E_WH、受注 E2E_ORDER、明細/ship group 00001、数量2、INVRO_FIFO_REC、requireInventory=N。追跡 GET `/api/product/entities/InventoryItem/one?inventoryItemId=10000`|200、`quantityNotReserved=0`、追跡 GET ATP=8 / QOH=10|200、`quantityNotReserved=0`。追跡 GET 200、ATP=8 / QOH=10|合格|
|PRODUCT-E2E-06d|主要フロー: 出荷作成 createShipment|POST `/api/product/services/createShipment` / admin。SALES_SHIPMENT、SHIPMENT_INPUT、E2E_WH、E2E_ORDER、ship group 00001|200、`shipmentId` 非空|200、`shipmentId=10000`、`statusId=SHIPMENT_INPUT`|合格|
|PRODUCT-E2E-06e|主要フロー: 出庫 issueOrderItemShipGrpInvResToShipment（2個）|POST `/api/product/services/issueOrderItemShipGrpInvResToShipment` / admin。shipmentId=10000、inventoryItemId=10000、E2E_ORDER、明細/ship group 00001、数量2。追跡 GET `/api/product/entities/InventoryItem/one?inventoryItemId=10000`|200、`shipmentItemSeqId=00001`、`itemIssuanceId` 非空、追跡 GET ATP=8 / QOH=8|200、`shipmentItemSeqId=00001`、`itemIssuanceId=10000`。追跡 GET 200、ATP=8 / QOH=8|合格|
|PRODUCT-E2E-06f|主要フロー: 梱包済み updateShipment → SHIPMENT_PACKED|POST `/api/product/services/updateShipment` / admin。shipmentId=10000、`statusId=SHIPMENT_PACKED`|200、`oldStatusId=SHIPMENT_INPUT`|200、`oldStatusId=SHIPMENT_INPUT`。work-effort 未移植（501）の注記と請求連携 no-op 注記あり|合格|
|PRODUCT-E2E-06g|主要フロー: 出荷済み updateShipment → SHIPMENT_SHIPPED|POST `/api/product/services/updateShipment` / admin。shipmentId=10000、`statusId=SHIPMENT_SHIPPED`。追跡 GET `/api/product/entities/Shipment/one?shipmentId=10000`|200、`oldStatusId=SHIPMENT_PACKED`、追跡 GET `statusId=SHIPMENT_SHIPPED`|200、`oldStatusId=SHIPMENT_PACKED`。追跡 GET 200、`shipmentId=10000`、`statusId=SHIPMENT_SHIPPED`。work-effort 未移植（501）・通知 no-op の注記あり|合格|
|PRODUCT-E2E-07|multi: 2件目の重複で 409、1件目もロールバック|POST `/api/product/services/createProductType/multi` / admin。`[{"productTypeId":"E2E_MULTI","description":"1件目"},{"productTypeId":"E2E_MULTI","description":"2件目（重複）"}]`。追跡 GET `/api/product/entities/ProductType/one?productTypeId=E2E_MULTI`|409、追跡 GET 404（1件目も残らない）|409、`errorMessage=ServiceValueFound`、`errors[0].index=1`。追跡 GET 404、`errorMessage=Entity not found`|合格|

## 実行中の注意点と対象外

- ケース01ではブラウザー標準の HTTP Basic 認証チャレンジが応答表示を待たせた。資格情報なしでチャレンジをキャンセルしたうえで、Swagger UI から再実行し401を確認した。
- 前半の自動操作で「Try it out」の描画待ちと認証ボタンのアクセシブル名に対するセレクター調整・再操作が発生した。アプリの応答不一致ではなく、ケース02・03b等の API 実行前に起きた操作待ちである。最終的に全18ケースと追跡 GET を完了した。ケース定義・アプリコードは修正していない。
- 主要フローの HTTP 200 は外部副作用が完全に移植されたことを意味しない。応答に残る no-op / 501 注記を上表にも明記した。請求・通知・work-effort 等の外部連携を今回の合格に含めない。
- 対象は指定18ケースとlocal/H2。PostgreSQL、本番HTTPS・信頼プロキシ、実データ負荷、未移植サービス、旧 OFBiz の画面、他モジュールとの本番統合は未検証。nofacility ユーザーは cases.json の18件で指定されていないため今回は使用していない。

## 集計と証跡

**指定18ケース: 合格18、失敗0、未実行0。主リクエスト18件＋追跡GET6件＝24件のHTTP状態・主要値を確認。**

録画は1本（1440×900、約85秒の自動編集版。操作待ちを短縮）で全18ケースを指定順に収録。MP4 と同内容のアニメーション WebP を別途成果物として提供する。

主要スクリーンショットを下記のファイル名から開ける。左・右は比較用の2列であり、すべて今回の同一revisionの実測結果。

|401 未認証|403 権限不足|
|---|---|
|![PRODUCT-E2E-01.png：401 未認証](https://devin-gtm.devinenterprise.com/attachments/7b564c62-fc94-4605-aa2e-6e1c38556a52/PRODUCT-E2E-01.png)|![PRODUCT-E2E-02.png：403 閲覧専用の作成拒否](https://devin-gtm.devinenterprise.com/attachments/84a6c092-4e42-4670-b790-6f670c09268b/PRODUCT-E2E-02.png)|

|機密403：readonly|機密200：sensitive|
|---|---|
|![PRODUCT-E2E-03a.png：機密403](https://devin-gtm.devinenterprise.com/attachments/6d4e83cb-e06b-45a9-ba86-c566f82950a6/PRODUCT-E2E-03a.png)|![PRODUCT-E2E-03b.png：機密200](https://devin-gtm.devinenterprise.com/attachments/5ab9257b-9408-4ca0-bf2f-5e42922156b2/PRODUCT-E2E-03b.png)|

|CRUD 作成|CRUD 取得|
|---|---|
|![PRODUCT-E2E-04a.png：作成200](https://devin-gtm.devinenterprise.com/attachments/3d7676f2-7a6c-4588-98f3-40fbb1ad2456/PRODUCT-E2E-04a.png)|![PRODUCT-E2E-04b.png：取得200](https://devin-gtm.devinenterprise.com/attachments/7dc79d9b-9be1-4b9d-b0c5-c7c43f5d96e4/PRODUCT-E2E-04b.png)|

|CRUD 更新後の追跡GET|CRUD 削除後の追跡GET|
|---|---|
|![PRODUCT-E2E-04c-after.png：更新を保存](https://devin-gtm.devinenterprise.com/attachments/0792f9a0-f01d-4c0a-9b4c-4432772c7f6e/PRODUCT-E2E-04c-after.png)|![PRODUCT-E2E-04d-after.png：削除後404](https://devin-gtm.devinenterprise.com/attachments/46895059-c91b-46da-b294-71e7632665aa/PRODUCT-E2E-04d-after.png)|

|simple 権限判定|simple 手持在庫集計|
|---|---|
|![PRODUCT-E2E-05.png：権限false](https://devin-gtm.devinenterprise.com/attachments/3a7d821c-3fb6-40d5-9900-acb3f804a702/PRODUCT-E2E-05.png)|![PRODUCT-E2E-05b.png：手持10](https://devin-gtm.devinenterprise.com/attachments/9756722d-3cdd-4f9f-ad5e-67dd16f2ad70/PRODUCT-E2E-05b.png)|

|主要フロー 商品登録|主要フロー 在庫入荷|
|---|---|
|![PRODUCT-E2E-06a.png：商品E2E_PROD](https://devin-gtm.devinenterprise.com/attachments/19601559-dc01-431d-9731-0c684cba8799/PRODUCT-E2E-06a.png)|![PRODUCT-E2E-06b.png：在庫10000](https://devin-gtm.devinenterprise.com/attachments/3a799524-5c51-4be7-a944-9dfb5ab95a70/PRODUCT-E2E-06b.png)|

|引当後 ATP8 / QOH10|出庫後 ATP8 / QOH8|
|---|---|
|![PRODUCT-E2E-06c-after.png：引当後在庫](https://devin-gtm.devinenterprise.com/attachments/ea413428-6dd3-4b8d-a039-003041e44f34/PRODUCT-E2E-06c-after.png)|![PRODUCT-E2E-06e-after.png：出庫後在庫](https://devin-gtm.devinenterprise.com/attachments/e3d4780c-882f-4d55-8796-4e2e5e498572/PRODUCT-E2E-06e-after.png)|

|出荷作成|梱包済み更新|
|---|---|
|![PRODUCT-E2E-06d.png：出荷ID10000](https://devin-gtm.devinenterprise.com/attachments/62831e84-d8be-414e-8320-550893d26d51/PRODUCT-E2E-06d.png)|![PRODUCT-E2E-06f.png：梱包済み更新200](https://devin-gtm.devinenterprise.com/attachments/f470885b-fee7-421f-ab16-723d520bb24a/PRODUCT-E2E-06f.png)|

|出荷済み更新200|追跡GET：SHIPMENT_SHIPPED|
|---|---|
|![PRODUCT-E2E-06g.png：旧PACKED](https://devin-gtm.devinenterprise.com/attachments/250eafac-89a4-42de-8f66-04315a295e64/PRODUCT-E2E-06g.png)|![PRODUCT-E2E-06g-after.png：SHIPPED保存](https://devin-gtm.devinenterprise.com/attachments/a8f10e66-4046-4989-8ad7-f088b4e1e79e/PRODUCT-E2E-06g-after.png)|

|multi 重複409|追跡GET 404：全件rollback|
|---|---|
|![PRODUCT-E2E-07.png：409とindex=1](https://devin-gtm.devinenterprise.com/attachments/62eeafe3-999f-4b42-babe-0e74e7058d3b/PRODUCT-E2E-07.png)|![PRODUCT-E2E-07-after.png：1件目も存在しない](https://devin-gtm.devinenterprise.com/attachments/f3b82cd7-5895-4881-9d84-1056e25a7b46/PRODUCT-E2E-07-after.png)|
