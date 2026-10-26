# CommonExt 移行ノート

## 1. 判断

| # | 判断 | 理由 |
| --- | --- | --- |
| N-01 | 独立した `migration/commonext-springboot` module に分離 | 共通 runtime / datamodel を変更せず、CommonExt 固有の service・Port・adapter だけを含める |
| N-02 | generator catalog を entity-auto のみとし、Groovy は手動登録 | `services.xml` の6件はすべて Groovy、entity-auto は0件。生成 catalog に Groovy を誤登録しない |
| N-03 | Current Party、Note 関連削除、status 検索を Port に分離 | UserLogin の party 解決と component 外 Entity / view 連携を adapter 境界に置く |
| N-04 | 既存の NoteData に専用 domain Entity を増やさない | OFBiz `NoteData` を共通 datamodel から利用し、`noteParty` / `moreInfo*` 拡張は共通 datamodel metadata に既に含まれる |
| N-05 | noteId / deletedCount をレスポンスに追加 | REST client から作成結果を参照し、削除件数を確認できるようにする。OFBiz の空 success map との差分 |
| N-06 | service 権限で VIEW / UPDATE を明示 | OFBiz の Webapp base permission `OFBTOOLS,SETUP` に加えて、読み取り・更新操作を個別に分離 |
| N-07 | HTTP forwarded header を Spring `forward-headers-strategy` で処理しない | 共通 runtime の trusted proxy 制御のみを利用し、Spring Boot 既定の別 RemoteIpValve による過剰な信頼を避ける |
| N-08 | PostgreSQL は `COMMONEXT_DB_*`、Swagger は local のみ | 認証情報を設定ファイルへ埋め込まず、運用 profile で docs を公開しない |
| N-09 | 画面 Widget / controller events は移植しない | Setup events は Party、Product、Accounting の広範なサービスに依存し、本タスクは CommonExt の6 service と読み取り API に限定 |
| N-10 | service-multi は HTTP 400 と全件 rollback を確認 | 共通 runtime のエラー契約に従う。該当 service は entity-auto でなく、409 の前提となるクライアント PK 衝突を使わない |
| N-11 | 共通 runtime の `migration.security.component` を `commonext` に固定 | 別 component 名でこの API を呼ぶと 404。共有 runtime が追加した scope 制御に従う |
| N-12 | generic Entity API は明示 `extra-entities` allowlist を使う | entity-auto catalog が0件でも NoteData と画面読み取り代替だけを公開し、他 Entity は404にする |
| N-13 | sensitive Entity の実アクセス試験は `UserLogin` で行う | Setup の `createUser` が使う Entity で、`currentPassword` がマスクされることと `SETUP_SENSITIVE_VIEW` を直接確認できる。`CreditCard` は allowlist 外のため 404 として扱う |

## 2. 実装境界

- `CurrentPartyPort`: SecurityContext の username を `UserLogin.userLoginId` として検索し、partyId を返す。
- `NoteAssociationPort`: 7種の関連 note relation を JPQL bulk delete する。
- `SystemInfoStatusPort`: CommunicationEvent と CommunicationEventRole、WorkEffort と WorkEffortPartyAssignment を join し、status / party / date / type を検索する。
- `SystemInfoNoteService`: authorization、入力検証、SYSTEMNOTE の固定値、一覧と legacy 条件を集約する。
- `LocalSeedData`: `local` profile 専用。UserLogin、NoteData、communication、task のスモーク用データを用意する。パスワードは seed せず `MIGRATION_DEMO_PASSWORD` を使う。

## 3. build / 開発環境

- `build.gradle` は Spring Boot 3.5.7 / dependency-management 1.1.7、Java 17、Testcontainers 1.21.4 を使用する。
- generator は `migration/tools/ofbiz2spring.py` を build-time に実行し、NoteData を含む base entity と全 component の `entitymodel*.xml` を渡して extension を解決する。CommonExt の service definition だけを catalog 入力にし、generated `ServiceCatalog` directory だけを sourceSet に加える。エンティティは datamodel と重複させず、生成物はコミットしない。
- Maven Central の HTTP 429 が発生した場合は、環境固有の mirror を `~/.gradle/init.d/mirror.gradle` に置く。リポジトリへ認証や VM 固有 URL を含めない。
- `run-local.sh` は `MIGRATION_DEMO_PASSWORD` を要求し、`local` profile で 8080 を起動する。Swagger UI / OpenAPI はこの profile のみ有効。
- `postgres` profile は `COMMONEXT_DB_URL`、`COMMONEXT_DB_USERNAME`、`COMMONEXT_DB_PASSWORD`、HTTPS 必須、Swagger 無効。`MIGRATION_SECURITY_TRUSTED_PROXIES` の未指定値 `a^` はプロキシを信頼しない。
- base config の HTTPS 既定値は true のまま保持し、`local` と H2 test profile は false を明示する。PostgreSQL deployment profile は HTTPS を true にする。HTTPS テストは profile default を上書きして検証する。

## 4. 調査上の不一致

ユーザー提供の事前調査では `SetupEvents.xml` の simple-method を11本としていたが、
現行 XML の `<simple-method>` 要素は9本（createOrganization、setupDefaultGeneralLedger、
createCustomer、createFacilityAndContactMech、createProductStoreWithDefaultSetting、
createProdCatalogAndProductStoreCatalog、createProductCategoryAndAddToProdCatalog、
createUpdateProductInCategory、createDefaultWebSite）だった。文書の一覧と集計はファイル上の要素数に基づく。
スコープ判断は変わらず、定義されている SetupEvents はいずれも未移植である。
