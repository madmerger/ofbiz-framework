# Order Spring Boot 移行レビュー

## 1. 検証結果

| 検証 | コマンド/テスト | 結果 |
| --- | --- | --- |
| Order API / H2 | `./gradlew :migration:order-springboot:test` | 成功。全 51 件、失敗・エラー・JUnit skip は各 0 件 |
| PostgreSQL | `PostgresSmokeTest` (Testcontainers 1.21.4) | 成功。1 件 pass。終了時に PostgreSQL 接続切断と Hibernate の schema-drop 失敗ログがあるが、テストと Gradle は成功 |
| 静的解析 | root の `checkstyleMain checkstyleTest codenarcMain codenarcTest`、Order Java への Checkstyle CLI | `-x nodeSetup -x npmInstall` 付きで root の 4 タスク成功。Order サブプロジェクトには同名 task がなく、root の `activeComponents()` にも migration は含まれない。root 設定を Order の Java 20 ファイルへ直接適用した結果は 53 件: `MethodName` 22 件（指定された `acNN_...` テスト名）、`DesignForExtension` 31 件（Spring transaction proxy を使う bean）。switch case の indentation 4 件は修正済み。Order 配下に Groovy source はなく CodeNarc 対象なし |
| local 起動/curl | `migration/order-springboot/run-local.sh` と HTTP Basic | 成功。未認証 `GET /api/order/entities/OrderHeader` は 401、`admin` 認証では 200、local の `/v3/api-docs` は 200 |
| unported generator | `migration/order-springboot/scripts/gen_unported.py` の生成物 | `unported.md` を確認。ECA 111、画面 action 75、controller 137、minilang event 10、view-entity 53、service 221、template 126、screen/form/menu/tree 367 |

テストクラス別の内訳は `OrderApiAcceptanceTest` 48、`OrderEntityAutoAcceptanceTest` 1、`OrderHttpsSecurityTest` 1、`PostgresSmokeTest` 1。テスト結果 XML 上の skip は 0 件。root の静的解析は Node 24.16.0 の Maven Central 配布物取得が HTTP 429 となったため Node 関連タスクを除外して実施した。Google Maven mirror は同 artifact に 404 を返した。root の静的解析は migration source を含まず、Order への手動 Checkstyle 結果には指定 AC 命名と Spring proxy に由来する上記の警告が残る。

entity-auto 網羅テストはカタログ 220 件と内訳を照合し、create 81/81 件、update 62/62 件、expire 4/4 件、delete 73/73 件の成功経路を実行する。対応する entity-auto create がない8件は、次の fixture で前提行を用意する。

| invoke | サービス | fixture |
| --- | --- | --- |
| update | `updateOrderNote` | `storeOrder` で OrderHeader を作成し、`OrderEntitySupport` で NoteData と OrderHeaderNote を作成 |
| update | `updateOrderHeader` | `storeOrder` で OrderHeader を作成 |
| update | `updateRequirement` | `OrderEntitySupport` で Requirement を作成 |
| update | `updateQuoteAdjustment` | `OrderEntitySupport` で QuoteAdjustment を作成 |
| delete | `deleteOrderItemShipGroupAssoc` | `storeOrder` で OrderItemShipGroupAssoc を作成 |
| delete | `removeReturnAdjustment` | `OrderEntitySupport` で ReturnAdjustment を作成 |
| delete | `removeShoppingList` | `OrderEntitySupport` で ShoppingList を作成 |
| delete | `removeQuoteAdjustment` | `OrderEntitySupport` で QuoteAdjustment を作成 |

## 2. OFBiz 版との差分

| ID | 差分 | OFBiz | Spring Boot 版 | 方針/影響 |
| --- | --- | --- | --- | --- |
| B-01 | UI と request-map | Widget/FTL、OFBiz controller event | JSON API、画面は再構築対象外 | `unported.md` に画面と request-map を棚卸し |
| B-02 | ShoppingCart | セッション内の ShoppingCart を参照 | `createCustRequestFromCart` は `cartItems` JSON を受ける | session state を API body に置換 |
| B-03 | 外部 Entity/サービス | Product, Shipment, Accounting, Party 等を各 component で処理 | `ProductPricePort`, `ShipmentPort`, `InvoicePort`, `ShipmentReceiptPort`, `CommunicationEventPort` と local JPA adapter | adapter はローカル DB 上の最小実装 |
| B-04 | 価格・請求額 | 価格設定、promo、税、送料、調整などを含む OFBiz の価格計算 | 商品価格の `DEFAULT_PRICE/PURCHASE` を使用する簡略計算 | promo/税/送料/調整は未実装。会計の本番移行前に要設計 |
| B-05 | ECA / SECA | Entity/Service Engine による暗黙の副作用 | 状態遷移・注文完了はフロー Bean が明示呼び出し | `unported.md` に ECA 定義を列挙。対象外の暗黙イベントは再現しない |
| B-06 | 権限モデル | `ORDERMGR_ROLE`, `altPermission`, `genericBasePermissionCheck` の組合せ | base permission と action authority を common dispatcher で確認 | 細部の role fallback/代替 permission は未再現。顧客依頼・ショッピングリストの所有者・担当者による範囲制限も未実装。本番では `ORDERMGR_ROLE` 相当の設計が必要 |
| B-07 | multi エラー | OFBiz `service-multi` の Tx とエラー構造 | Order 専用 endpoint が 409 を維持し、いずれか失敗なら全件 rollback | common multi の 400 正規化を避ける |
| B-08 | HTTPS | デプロイ環境に依存 | 既定で HTTPS 必須、信頼 proxy なしを既定とする | local/test は意図的に HTTPS を無効化 |
| B-09 | entity-auto update / 主キー | サービス engine が入力を解釈 | undeclared update は 400、OUT 主キーは caller input を無視して採番 | 宣言された属性だけを適用し、OUT/INOUT の方向性を保持 |
| B-10 | proxy の信頼境界 | deployment setting に依存 | trusted proxy の既定は空。未信頼 `X-Forwarded-Proto` は HTTPS を迂回できない | `OrderHttpsSecurityTest` で MockMvc の HTTP と偽装 header を確認 |
| B-11 | entity-auto CRUD 網羅の fixture | EntityAuto は各 entity の既存データを前提に操作可能 | entity-auto create がない8サービスは別のサービスまたは `OrderEntitySupport` で fixture を作成 | カタログの create/update/expire/delete 全220本で成功経路を確認 |

## 3. 既知の制約・未対応項目

- `createCustRequestFromCart` は `cartItems` 入力での代替であり、OFBiz ShoppingCart の session/checkout state は再現しない。
- Product price は商品・通貨・価格目的に一致する行を1件参照する最小実装。価格ルール、プロモーション、税、送料、割引、調整は対象外。
- `ShipmentPort` / `InvoicePort` は注文の代表経路を成立させる最小の同名 Entity 書き込みであり、在庫予約、配送パッケージ、税会計、支払・GL 連携等を再現しない。
- `CommunicationEventPort` は指定された Event の参照・完了状態更新に限定。Party/Communication の完全な workflow は別途移行が必要。
- ECA/SECA engine は移植していない。Order の quick-ship/status/cancel 経路で必要な変化だけを明示的に呼ぶ。
- Widget/FTL、画面用検索、view-entity の API 化、他コンポーネントとの完全なトランザクション連携は未実装。
- HTTP Basic の local demo users は開発専用。本番運用では認証基盤、TLS 終端、PostgreSQL schema migration、OFBiz との共有採番を別途設計する。
- entity-auto 網羅テストの fixture は対象操作に必要な最小行を用意する。特に create 定義がない entity は対応する create service の存在を示すものではなく、EntityRegistry経由の直接 fixture も使用する。
- HTTPS/proxy spoof は MockMvc で確認し、local profile の curl も実施した。TLS 終端を含む実ネットワーク経路、実データ DB との並行稼働は未検証。
- Gradle の `Plugins directory not found.` は root `build.gradle` の任意プラグイン文書化処理が `plugins/` 不在を通知するもの。Order のテスト失敗ではない。

## 4. ベースブランチ取り込み

`origin/devin/1793006105-springboot-common` の `d6254e03b3`、`3ff99279eb`、`23f8c75e65` までを `git fetch` 後に fast-forward で取り込んだ。merge commit/rebase は作成していない。common 側の INOUT/OUT 主キー採番、update parameter validation、component scope、HTTPS default を Order 設定・テストに合わせた。共通基盤・OFBiz 本体の追加編集は行っていない。
