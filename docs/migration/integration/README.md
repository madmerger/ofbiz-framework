# Spring Boot コンポーネント統合 E2E（ローカル PostgreSQL）

この手順は、order（8081）、product（8082）、accounting（8083）を別々の Spring Boot プロセスとして起動し、1つの PostgreSQL データベースを共有して確認するためのものです。各操作はそれぞれのコンポーネント API に送信します。Order の出荷・請求スタブは使いません。

## 前提と環境変数

- Docker とリポジトリの Gradle wrapper を使用します。
- `INTEGRATION_DB_USERNAME`、`INTEGRATION_DB_PASSWORD` は必須です。`INTEGRATION_DB_URL` の既定値は `jdbc:postgresql://localhost:5432/ofbiz_integration` です。
- `MIGRATION_DEMO_PASSWORD` は Order と Accounting のデモユーザー用、`PRODUCT_DEMO_PASSWORD` は Product の `admin`、`readonly`、`sensitive` ユーザー用です。
- パスワードはローカル専用の値をシェル環境に設定してください。ソース、コマンド履歴、録画、ログへ記録しないでください。
- `integration` プロファイルはローカル統合テスト専用です。HTTPS を無効にし、Swagger UI/API docs を有効にし、Hibernate の `ddl-auto` を `update` にします。スキーマ更新の競合を避けるため、PostgreSQL、Order、Product、Accounting の順で起動してください。

```bash
export INTEGRATION_DB_URL="${INTEGRATION_DB_URL:-jdbc:postgresql://localhost:5432/ofbiz_integration}"
export INTEGRATION_DB_USERNAME=ofbiz
read -rsp 'Local integration DB password: ' INTEGRATION_DB_PASSWORD; printf '\n'
export INTEGRATION_DB_PASSWORD
read -rsp 'Order/Accounting demo password: ' MIGRATION_DEMO_PASSWORD; printf '\n'
export MIGRATION_DEMO_PASSWORD
read -rsp 'Product demo password: ' PRODUCT_DEMO_PASSWORD; printf '\n'
export PRODUCT_DEMO_PASSWORD
```

この環境変数を、以下の PostgreSQL と各アプリを起動するシェルに渡してください。パスワード値はこの README に書き込まないでください。

## PostgreSQL の起動

```bash
docker run -d --name ofbiz-integration-postgres \
  -e POSTGRES_DB=ofbiz_integration \
  -e POSTGRES_USER="$INTEGRATION_DB_USERNAME" \
  -e POSTGRES_PASSWORD="$INTEGRATION_DB_PASSWORD" \
  -p 5432:5432 postgres:16
docker exec ofbiz-integration-postgres pg_isready -U "$INTEGRATION_DB_USERNAME" -d ofbiz_integration
```

既にコンテナが存在する場合は再作成せず、そのコンテナを利用してください。停止する場合は `docker stop ofbiz-integration-postgres` を実行します。

## アプリの起動

リポジトリのルートで、次のコマンドをそれぞれ別のターミナルで実行します。各アプリが DB 初期化を終えて health endpoint に応答してから次のアプリを起動してください。

```bash
./gradlew --no-daemon :migration:order-springboot:bootRun --args='--spring.profiles.active=integration'
```

```bash
./gradlew --no-daemon :migration:product-springboot:bootRun --args='--spring.profiles.active=integration'
```

```bash
./gradlew --no-daemon :migration:accounting-springboot:bootRun --args='--spring.profiles.active=integration'
```

各アプリの health endpoint は `http://localhost:{8081|8082|8083}/actuator/health` です。Order / Accounting のユーザーは `MIGRATION_DEMO_PASSWORD`、Product のユーザーは `PRODUCT_DEMO_PASSWORD` で Basic 認証します。主な API ユーザーは Order の `orderclerk`、Product の `admin`、Accounting の `acct-clerk` です。

## シードデータ

各コンポーネントの integration 対応シーダーが、共有 DB のデモデータを主キー確認付きで登録します。Accounting の統合専用 `AccountingIntegrationData.xml` は一度だけ Accounting アプリから読み込まれ、Order の ProductStore が参照する `Company` に内部組織ロール、会計設定、売掛金・売上・入金用 GL デフォルトを設定します。既存のローカル／PostgreSQL／本番プロファイルは変更しません。

## E2E ケース

`cases.json` のケースは記載順に実行します。採番値は後続ケースの `{{orderId}}`、`{{shipmentId}}`、`{{invoiceId}}`、`{{paymentId}}` に引き継ぎます。

1. 認証確認: Order の未認証 401、read-only ユーザーの受注作成 403。
2. Order API で受注作成・承認。
3. Product API で在庫入荷（`fromDate` を省略）、受注分引当、`quickShipEntireOrder`。Shipment の `SHIPMENT_SHIPPED` を確認。
4. Accounting API で受注から請求書を作成し `INVOICE_READY` に変更。`SALES_INVOICE` GL transaction が自動作成されたことを確認。
5. 入金を `PMNT_RECEIVED` で作成し請求書に適用。請求書 `INVOICE_PAID`、未適用額 `0.00` を確認。
6. Order API で最終状態を確認。Order header は `ORDER_APPROVED` のまま、shipment によって Order item は `ITEM_COMPLETED` になります。

Product の出荷レスポンスには WorkEffort 連携未移行の非致命的な `501` side-effect note が含まれることがあります。出荷・Shipment 状態確認は成功します。
