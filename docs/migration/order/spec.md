# Order 既存仕様書

- 対象: `madmerger/ofbiz-framework` の `applications/order`。移行ベースは `origin/devin/1793006105-springboot-common` の `23f8c75e65` まで fast-forward した作業ブランチ。
- 位置づけ: `migration/order-springboot/` の仕様と受け入れ基準。情報源の優先順位は **現行 OFBiz コード > 本書の要約**。
- 網羅一覧: `docs/migration/order/unported.md` は `migration/order-springboot/scripts/gen_unported.py` で生成。entity-auto は生成された `ServiceCatalog` と対象 `services*.xml` の定義をテストで照合する。

## 1. コンポーネント構成

| 項目 | 内容 | 参照 |
| --- | --- | --- |
| サービス定義 | `applications/order/servicedef/services*.xml` | `applications/order/ofbiz-component.xml` |
| entity model | datamodel コンポーネントがロード | `applications/datamodel/entitydef/order-entitymodel.xml` |
| minilang | order、permission、request、requirement の各定義 | `applications/order/minilang/` |
| Web アプリ | `ordermgr`。OFBiz 側の base permission は `OFBTOOLS,ORDERMGR` | `applications/order/ofbiz-component.xml` |
| widget / template | 受注、注文管理、顧客依頼などの画面 | `applications/order/widget/`, `applications/order/template/`, `applications/order/webapp/` |
| 移行先 | Spring Boot 3.5.7、Java package `org.apache.ofbiz.migration.order` | `migration/order-springboot/build.gradle` |

本移行の対象は API と業務サービスであり、Widget/FTL の UI は再構築しない。画面・テンプレート・未移植サービス等の個別一覧は `unported.md` を参照。

## 2. 機能概要・ユースケース

| 領域 | 主なユースケース | 主エンティティ | 主サービス |
| --- | --- | --- | --- |
| 注文受付 | 商品・数量・価格を検証し、ヘッダ、明細、配送グループ、役割、初期履歴を作成 | `OrderHeader`, `OrderItem`, `OrderStatus` | `storeOrder` |
| 注文状態 | 有効な遷移のみ許可し、履歴を記録して必要時に明細状態を連鎖 | `StatusValidChange`, `OrderStatus` | `changeOrderStatus`, `changeOrderItemStatus` |
| キャンセル | 数量を累積し、全量キャンセル時に明細と注文の状態を更新 | `OrderItem`, `OrderHeader` | `cancelOrderItem`, `cancelOrderItemNoActions` |
| 出荷 | 承認済み注文から shipment/item/association を作成し、完了状態を記録 | `Shipment`, `ShipmentItem`, `OrderShipment` | `quickShipEntireOrder` |
| 請求 | 未請求の注文明細から invoice/item/billing を作成 | `Invoice`, `InvoiceItem`, `OrderItemBilling` | `createInvoiceForOrder` |
| 顧客依頼 | CRUD、状態遷移、メモ、明細、ショッピングリスト/カート/通信イベント連携 | `CustRequest`, `CustRequestItem`, `CustRequestNote` | minilang 由来 16 サービス |
| 要求 | 商品要求を登録し、必要な関連情報を記録 | `Requirement`, `RequirementRole` | `createRequirement` |

## 3. ドメインモデル

- 共通の `springboot-datamodel` が OFBiz entity model から生成した Entity を使う。Order は 112 entities、2 view-entities の棚卸し結果を含む。
- 注文の主なキーは `orderId`、明細は `(orderId, orderItemSeqId)`、配送グループは `(orderId, shipGroupSeqId)`。OFBiz の 5 桁サブシーケンスを保持する。
- 注文状態の初期値は `ORDER_CREATED` / `ITEM_CREATED`。遷移の許可は `StatusValidChange` に従い、変更ごとに `OrderStatus` を記録する。
- 顧客依頼は `CUSTREQ_STTS`、要求は `REQUIREMENT_STATUS` を用いる。状態の seed 値と遷移は `applications/datamodel/data/seed/OrderSeedData.xml:111-153,217-260` から取り込む。
- 他コンポーネントとのデータ連携は Port の境界を使う。詳細は §8 を参照する。

## 4. サービス仕様

### 4.1 entity-auto 220 本

対象は `applications/order/servicedef/services*.xml` の `engine="entity-auto"` 定義。共通 `EntityAutoService` が作成・更新・削除・失効、必須入力、重複・未存在時の status、サービス定義に宣言された更新パラメータのみの適用を担当する。空の INOUT 主キーは共通基盤の採番規則で扱い、`createOrderItemShipGroup` の `shipGroupSeqId` は入力がなければ 5 桁で採番される。OUT 主キーは入力値を無視し、共通基盤が採番する（`createCustRequestStatus` の `custRequestStatusId`）。

### 4.2 minilang simple 19 本

| 群 | サービス | 移植時の要点 |
| --- | --- | --- |
| 権限 | `orderAdjustmentPermissionCheck`, `returnPermissionCheck`, `custRequestPermissionCheck` | `mainAction` と `ORDERMGR_<action>` / `ORDERMGR_ADMIN` を評価 |
| 注文・受領 | `updateOrderStatusFromReceipt` | `ShipmentReceiptPort` 経由で受領結果を処理 |
| 顧客依頼 | `createCustRequest`, `updateCustRequest`, `deleteCustRequest`, `copyCustRequestItem`, `checkStatusCustRequest`, `updateCustRequestLastModifiedDate`, `createCustRequestNote`, `updateCustRequestNote`, `createCustRequestItemNote`, `createCustRequestFromCart`, `createCustRequestFromShoppingList`, `getCustRequestsByRole`, `setCustRequestStatus`, `createCustRequestFromCommEvent` | SequenceService による採番。連鎖処理は Spring Bean 呼び出しで同一 Tx に参加 |
| 要求 | `createRequirement` | 要求と関連行を同一 Tx で作成 |

権限チェック用サービスは `VIEW` 権限で呼べるようにし、入力 `mainAction` の権限判定を返す。OFBiz の `ORDERMGR_ROLE`、`altPermission`、`genericBasePermissionCheck` の細部は `review.md` に差分として記録する。

### 4.3 注文ライフサイクル

- `storeOrder`: `partyId`、`orderTypeId`、1 件以上の明細を検証。価格未指定時は `ProductPricePort` の価格を使用し、取得不能なら 400。注文・明細・配送グループ・役割・初期状態履歴を作成する。
- `changeOrderStatus`: `StatusValidChange` がなければ 409。同一状態なら成功として履歴を追加しない。`setItemStatus=Y` で承認/完了/キャンセルを明細へ連鎖する。
- `changeOrderItemStatus`: 指定状態を明細へ適用し、許可されない明細の遷移は OFBiz に合わせてスキップする。
- `cancelOrderItem` / `cancelOrderItemNoActions`: 数量を累積し、未残量超過は 400。全明細がキャンセルされるとヘッダもキャンセルする。
- `quickShipEntireOrder`: `ORDER_APPROVED` のみ許可し、Shipment Port で出荷関連行を作成、注文完了履歴を記録する。
- `createInvoiceForOrder`: 承認済み/完了注文で未請求明細がある場合だけ請求行を作成。重複請求は 409。

### 4.4 権限

OFBiz `ordermgr` の base permission は `OFBTOOLS,ORDERMGR`。Spring 側では両方に `_VIEW` または `_ADMIN` を要求し、業務操作ごとに `ORDERMGR_CREATE/UPDATE/DELETE/VIEW` または `ORDERMGR_ADMIN` を検査する。機密 Entity は `ORDERMGR_SENSITIVE_VIEW` または `ORDERMGR_ADMIN` を追加要求する。

## 5. Web エンドポイント仕様

| 種別 | Spring API | 動作 |
| --- | --- | --- |
| サービス | `POST /api/order/services/{serviceName}` | OFBiz サービス名を保持し dispatcher 経由で実行 |
| multi | `POST /api/order/services/{serviceName}/multi` | 1 Tx、1 件でも失敗すれば全件 rollback。409 を含む場合は 409 |
| Entity 照会 | `GET /api/order/entities/{entityName}` | Order カタログの参照 Entity と `extra-entities` に限定 |
| OpenAPI | `/v3/api-docs`, `/swagger-ui.html` | local profile のみ有効 |

Order 専用 multi route は common の `{component}` route より具体的な literal route として登録される。異なる URL component は common の必須設定 `migration.security.component=order` により 404。

## 6. 画面とテンプレート

OFBiz Widget の Screen/Form/Menu/Tree と FreeMarker は `unported.md` に全件列挙する。画面は REST API で代替し、UI と画面固有の検索・結合表示は別途再構築する。画面専用 Groovy action はサービス実装として扱わず、画面再構築時に移植する。

## 7. セキュリティ

- local demo users は `OFBTOOLS_VIEW` と Order の役割別権限を付与し、パスワードは `MIGRATION_DEMO_PASSWORD` から供給する。`run-local.sh` は変数未設定時に起動を拒否する。
- 17 の決済・個人情報関連 Entity を機密対象とし、API と entity-auto の双方で追加権限を要求する。
- `require-https` は既定 true。local/test だけ明示的に false、PostgreSQL profile は true。
- Tomcat の信頼 proxy は既定 `a^`（どの peer も信頼しない）。`X-Forwarded-Proto` 等の値は信頼 peer 以外から強制 HTTPS の回避に使用させない。
- DB 接続文字列/資格情報は PostgreSQL profile で環境変数から受け取る。

## 8. 依存とアダプタ化の境界

| Port | 依存 | local adapter |
| --- | --- | --- |
| `ProductPricePort` | Product の購入価格 | `ProductPrice` を照会 |
| `ShipmentPort` | Shipment 作成 | `Shipment`, `ShipmentItem`, `OrderShipment` を作成 |
| `InvoicePort` | 請求作成 | `Invoice`, `InvoiceItem`, `OrderItemBilling` を作成 |
| `ShipmentReceiptPort` | Product 側の受領 | `ShipmentReceipt` を照会 |
| `CommunicationEventPort` | Party/Communication 側イベント | `CommunicationEvent` と関連を照会・更新 |

OFBiz セッションの ShoppingCart は API 実行環境に存在しないため、`createCustRequestFromCart` は `cartItems: [{productId, quantity, ...}]` を入力として受ける。

## 9. コード根拠

- 注文作成: `applications/order/src/main/java/org/apache/ofbiz/order/order/OrderServices.java:190-`。
- 注文/明細状態: 同ファイルの `setOrderStatus` / `setItemStatus`（`changeOrderStatus`, `changeOrderItemStatus` の実装根拠）。
- キャンセル: 同ファイルの `cancelOrderItem`。
- simple サービス: `applications/order/minilang/order/OrderSimpleMethods.xml`、`applications/order/minilang/permission/PermissionServices.xml`、`applications/order/minilang/request/CustRequestServices.xml`、`applications/order/minilang/requirement/RequirementServices.xml`。
- 移行先: `migration/order-springboot/src/main/java/org/apache/ofbiz/migration/order/OrderFlowService.java`、`OrderSimpleServices.java`、`OrderServiceHandlers.java`、`OrderMultiController.java`。

## 10. テストが保証する振る舞い

`OrderApiAcceptanceTest` は AC 番号付きの H2/MockMvc テスト。生成カタログの 220 entity-auto 照合、全機密 Entity の存在、注文ライフサイクル、キャンセル、multi の 409/400、認可、機密 Entity、INOUT/OUT 主キー採番、simple service の正常経路を検査する。`OrderEntityAutoAcceptanceTest` は 81 create と、対応する auto-create fixture がある update/expire/delete の成功経路を実行する。entity-auto create が存在しない entity に対する update 4 件・delete 4 件の成功経路は未検証であり、全 CRUD 定義を実行したものではない。`OrderHttpsSecurityTest` は HTTPS 必須時に未信頼 `X-Forwarded-Proto` で迂回できないことを検査する。`PostgresSmokeTest` は Testcontainers で PostgreSQL の作成・承認を検査する。

テストの実測件数・結果・既知の制約は `review.md` に記録する。

## 11. 受け入れ基準

| AC | 基準 | テスト |
| --- | --- | --- |
| AC-01 | entity-auto 220 本が生成カタログに存在 | `ac01_generatedCatalogContainsAll220EntityAutoServices` |
| AC-02 | 機密対象 17 Entity がモデルに存在 | `ac02_everySensitiveEntityExistsInTheModel` |
| AC-03 | create/update/delete、重複・未存在・不正属性を検査 | `ac12_entityAutoCrudAndInputValidation` |
| AC-04 | 空 INOUT `shipGroupSeqId` が採番され、行を参照可能 | `ac11_entityAutoGeneratesMissingInOutSequenceKey` |
| AC-04b | OUT `custRequestStatusId` は入力値を無視し、採番値の行だけが存在 | `ac17_entityAutoIgnoresCallerSuppliedOutPrimaryKey` |
| AC-05 | 注文→承認→出荷→請求の履歴と二重請求拒否 | `ac03_storeApproveShipInvoiceAndExposeHistory` |
| AC-06 | 不正遷移と承認前出荷を 409 で拒否 | `ac04_invalidStatusAndUnapprovedShipmentReturnConflict` |
| AC-07 | 過剰キャンセルを 400 で拒否 | `ac05_cancelAccumulatesAndRejectsOverCancellation` |
| AC-08 | multi で 409 を保持し、それ以前の変更も rollback | `ac06_multiPreservesConflictAndRollsBackEarlierCalls` |
| AC-08b | multi の入力エラーを 400 で返す | `ac06b_multiReturnsBadRequestForInvalidInput` |
| AC-09 | 19 simple service の handler 登録と顧客依頼の正常経路 | `ac13_everySimpleServiceHasARegisteredHandler`, `ac18_simpleServicesSupportNormalCustomerRequestPaths` |
| AC-10 | 未認証は 401、Order 権限なし/読み取り専用書込みは 403 | `ac09_*`, `ac10_*`, `ac15_*` |
| AC-11 | 機密 Entity/API は追加権限なしに拒否 | `ac07_*`, `ac08_*`, `ac14_*` |
| AC-12 | component 違いは 404、HTTPS は既定 true、信頼されない Forwarded header は無効、test の Swagger は無効 | `ac09_*`, `ac16_httpsIsSecureByDefaultAndSwaggerIsDisabledInTestProfile`, `OrderHttpsSecurityTest.ac01_requiresHttpsAndIgnoresUntrustedForwardedProto` |
| AC-13 | PostgreSQL でも注文作成と承認が成功 | `PostgresSmokeTest.ac01_storeAndApproveOrderOnPostgres` |
