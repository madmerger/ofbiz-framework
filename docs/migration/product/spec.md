# Product コンポーネント移行仕様

- 対象: `applications/product/` の現行 OFBiz 実装と `migration/product-springboot/`
- 目的: サービス名、入出力、権限、トランザクション、Minilang/Groovy/Java の業務意味を保ち、段階的に Spring Boot へ移行する。
- 一次情報: 現行 OFBiz ソース。棚卸しの全件一覧は `inventory/` と `unported.md` を参照する。
- 優先事項: **Faithfulness to the minilang is the priority.**

## 1. 範囲と構成

Product のサービスカタログは entity-auto 460 件、simple 77 件を対象とする。entity-auto は生成カタログと共通エンジンで登録し、simple と主要な Groovy/Java フローは専用ハンドラーを登録する。画面、FreeMarker、イベント、全 Java/Groovy 実装の完全移植を意味するものではない。現時点の部分移植と no-op/501 は `unported.md` に列挙する。

## 2. 受け入れ基準

| ID | 基準 | OFBiz 根拠 | 検証 |
| --- | --- | --- | --- |
| AC-01 | Product の entity-auto 460 件をカタログ化し、HTTP から create/update/delete/expire を実行できる。複合キー、OUT キー、以前の値、エラーを保持する。 | `applications/product/servicedef/services.xml:40`、`applications/product/servicedef/services_shipment.xml:166` | `migration/product-springboot/src/test/java/org/apache/ofbiz/migration/product/EntityAutoCatalogTest.java:73`、`EntityAutoAcceptanceTest.java:85` |
| AC-02 | simple 77 件すべてを登録し、サービス定義から得た権限を登録ハンドラーに適用する。 | `applications/product/minilang/` の各サービスファイル。定義・権限の変換は `migration/product-springboot/build.gradle:127` | `migration/product-springboot/src/test/java/org/apache/ofbiz/migration/product/SimpleServiceCatalogTest.java:41` |
| AC-03 | `createProduct` は指定 ID または採番 ID、必須値検証、`isVirtual`/`isVariant` の既定値、作成日時・ユーザーを保持する。商品キーワードと Product の ECA は明示的な未移植として扱う。 | `applications/product/servicedef/services.xml:40`、`applications/product/src/main/groovy/org/apache/ofbiz/product/product/product/ProductServicesScript.groovy:36` | `migration/product-springboot/src/test/java/org/apache/ofbiz/migration/product/ProductInventoryFlowTest.java:58` |
| AC-04 | `createInventoryItem` は在庫 ID、施設所有者、受領日時、通貨等を作成する。`receiveInventoryProduct` は InventoryItem、ShipmentReceipt、在庫詳細を作り、QOH/ATP を記録する。 | `applications/product/servicedef/services_facility.xml:38`、`applications/product/src/main/groovy/org/apache/ofbiz/product/product/inventory/InventoryServicesScript.groovy:92`、`applications/product/servicedef/services_shipment.xml:868`、`applications/product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentReceiptServices.groovy:65` | `migration/product-springboot/src/test/java/org/apache/ofbiz/migration/product/ProductInventoryFlowTest.java:93`、`MainFlowE2eSequenceTest.java:64` |
| AC-05 | 施設別予約は ProductStore の予約設定、注文・明細・出荷グループを参照し、予約数量に従って ATP を更新する。NULL に対する Minilang `not-equals` は NULL 行も含める。 | `applications/product/servicedef/services_facility.xml:542`、`applications/product/minilang/product/inventory/InventoryReserveServices.xml:98`、`:122`、`:147` | `migration/product-springboot/src/test/java/org/apache/ofbiz/migration/product/InventoryReserveServicesTest.java:107`、`MainFlowE2eSequenceTest.java:84` |
| AC-06 | `createShipment` と `createShipmentItem` は entity-auto の入出力・権限を保ち、出荷と品目を永続化する。 | `applications/product/servicedef/services_shipment.xml:166`、`:281` | `migration/product-springboot/src/test/java/org/apache/ofbiz/migration/product/EntityAutoAcceptanceTest.java:93`、`QuickShipEntireOrderFlowTest.java:92` |
| AC-07 | `issueOrderItemShipGrpInvResToShipment` は予約を出荷に発行し、ShipmentItem、ItemIssuance を作成し、在庫台帳を減算する。 | `applications/product/servicedef/services_shipment.xml:498`、`applications/product/minilang/shipment/issuance/IssuanceServices.xml:91` | `migration/product-springboot/src/test/java/org/apache/ofbiz/migration/product/MainFlowE2eSequenceTest.java:104`、`QuickShipEntireOrderFlowTest.java:348` |
| AC-08 | `updateShipment` は StatusValidChange を検証し、ShipmentStatus を記録して、service 定義の IN/OUT/INOUT 項目を更新する。SHIPPED 等の外部 ECA は明示的に報告する。 | `applications/product/servicedef/services_shipment.xml:177`、`:189`、`applications/product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy:35`、`:45` | `migration/product-springboot/src/test/java/org/apache/ofbiz/migration/product/UpdateShipmentFlowTest.java:55` |
| AC-09 | `quickShipEntireOrder` は OrderPort の注文読み取り、ship group ごとの出荷作成、発行サービス呼出し、PACKED→SHIPPED 遷移を実行する。OrderItem 完了遷移はローカル adapter が StatusValidChange を検証して OrderStatus を記録する。 | `applications/product/servicedef/services_shipment.xml:68`、`applications/product/src/main/groovy/org/apache/ofbiz/product/shipment/ShipmentServices.groovy:760`、`applications/order/src/main/java/org/apache/ofbiz/order/order/OrderServices.java:5027` | `migration/product-springboot/src/test/java/org/apache/ofbiz/migration/product/QuickShipEntireOrderFlowTest.java:348`、`LocalOrderAdapterTest.java:51` |
| AC-10 | `calculateProductPrice` は指定時点の有効 ProductPrice、価格種別の優先順位、仮想商品からの基本価格フォールバックを扱う。価格ルールと販促は範囲外である。 | `applications/product/servicedef/services_pricepromo.xml:28`、`applications/product/src/main/java/org/apache/ofbiz/product/price/PriceServices.java:84`、`:1073` | `migration/product-springboot/src/test/java/org/apache/ofbiz/migration/product/CalculateProductPriceFlowTest.java:58`、`MainFlowE2eSequenceTest.java:138` |
| AC-11 | API は基本権限 OFBTOOLS に加え、サービスに応じ CATALOG/FACILITY 権限を検査する。機微エンティティは別権限で保護し、既定/本番 PostgreSQL は HTTPS を要求する。信頼プロキシは明示設定がない限り無効、Swagger は local プロファイルのみ有効、デモ資格情報は `PRODUCT_DEMO_PASSWORD` から読み込む。 | `migration/product-springboot/src/main/resources/application.yml:24`、`:35`、`:40`、`:42`、`application-postgres.yml:10`、`:13`、`application-local.yml:4`、`:13` | `migration/product-springboot/src/test/java/org/apache/ofbiz/migration/product/SecurityTest.java:121`、`HttpsTest.java:45`、`SpringdocLocalProfileTest.java:43`、`SpringdocProfileTest.java:39` |
| AC-12 | `/multi` は一つのトランザクションで実行し、後続失敗時は先行する entity-auto 作成もロールバックする。 | `framework/webapp/src/main/java/org/apache/ofbiz/webapp/event/ServiceMultiEventHandler.java:54`、`:177`、`:377` | `migration/product-springboot/src/test/java/org/apache/ofbiz/migration/product/MultiRollbackTest.java:67`、`:91` |
| AC-13 | Product 内部の equality query は100件で切り捨てず、全件を集計対象にする。共通 EntityRegistry は変更しない。 | `applications/product/minilang/product/inventory/InventoryServices.xml:400`、`migration/springboot-common/src/main/java/org/apache/ofbiz/migration/common/EntityRegistry.java:108` | `migration/product-springboot/src/test/java/org/apache/ofbiz/migration/product/InventoryServicesTest.java:84` |

## 3. 認証・実行

ローカル実行は `PRODUCT_DEMO_PASSWORD=local-smoke-only migration/product-springboot/run-local.sh`。local プロファイルだけが H2、デモユーザー、Swagger UI、`ProductLocalSeed` を有効にする。本番相当の PostgreSQL は `PRODUCT_DB_URL`、`PRODUCT_DB_USERNAME`、`PRODUCT_DB_PASSWORD` と HTTPS を設定し、デモ資格情報を使用しない。

## 4. 既知の差分

未移植 ECA、サービス依存、完全移植対象外の画面/イベント等は `unported.md` を参照する。注文品目ステータス変更はローカル DB adapter で実装するが、注文ヘッダー完了判定と order ECA は未実装である。`OrderItem` は order コンポーネント所有で Product の entity API には含まれないため、状態確認はテストから行う。
