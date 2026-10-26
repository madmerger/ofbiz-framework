# Party Spring Boot 移行仕様

## 目的と対象

`applications/party` のパーティー、人物、所属、連絡先に関する主要 API を `migration/party-springboot` に切り出す。データモデルは `migration:springboot-datamodel`、HTTP/API・Entity Auto 実行基盤は `migration:springboot-common` を利用する。共通基盤・OFBiz 本体・生成ツールは変更しない。

ビルド時に `generatePartyInventory` が `applications/party` と `applications/datamodel/entitydef/party-entitymodel.xml` から inventory を作り、`generatePartyCatalog` が inventory を ServiceCatalog に変換する。両タスクの入力・出力を宣言し、`compileJava` はカタログ生成に依存する。生成 Java は `build/generated` のみに置く。

API component 名は `party`。サービスは `/api/party/services/{name}` と `/api/party/services/{name}/multi`、Entity API は `/api/party/entities/{entity}` と `/api/party/entities/{entity}/one`。component が `party` と異なる URL は 404 とする。

## 移植範囲

| 種別 | 件数 | 方針 |
|---|---:|---|
| entity-auto | 128 | ServiceCatalog を `ServiceHandlerRegistry` に登録し、共通の EntityAutoService で実行 |
| simple | 26 | `PartyServiceHandlers` に手動実装 |
| Java / Groovy | 19 | Party・Person・PartyGroup・PartyRelationship・連絡先の主要フローを実装 |
| group | 1 | `createPartyRelationshipAndRole` を単一トランザクションで実装 |
| 未移植サービス | 92 | 個別一覧・理由は [unported.md](unported.md) |

entity-auto 件数の受け入れ確認は inventory を参照せず `applications/party/servicedef/*.xml` を直接 XML parse する。entity-auto の `update` はサービス定義の IN / INOUT 属性以外を受け付けず、未知属性は 400 とする。

## データと認可

- `PartyMigrationApplication` は Party module、common component、datamodel の Entity / Repository を構成する。
- `PartyCatalogRegistrar` は生成 ServiceCatalog を `registerCatalog("party", ...)` に直接渡す。
- `migration.security.component: party`、base permissions `OFBTOOLS` / `PARTYMGR`、service prefix `PARTYMGR` を使用する。
- Entity API は component カタログが参照する Entity と `extra-entities` のみ許可する。認可対象外の Entity と他 component の API は 404。
- 機密 Entity の読み取りは `PARTYMGR_SENSITIVE_VIEW` または `PARTYMGR_ADMIN` を要求する。応答の機密値は `[REDACTED]` にする。
- local のデモユーザーは `PARTY_DEMO_PASSWORD` から設定し、`run-local.sh` は未設定なら起動しない。local / test は HTTPS を無効にし、postgres は HTTPS 必須、DDL 検証を行う。
- UserLogin のローカル adapter は共通 `PasswordEncoder` で BCrypt hash を保存する。平文保存をせず、確認用パスワード一致、最小長 5、重複 ID 409 を検証する。

## 主なサービス仕様

- 既定値のない `partyId` は `SequenceService.nextId("Party")` で採番する。パーティーから人物・グループ、役割へ同一トランザクションで作成する。
- PartyRelationship の role 未指定値は `_NA_`。削除は party ID、両 role、関係種別、必要に応じ fromDate を使い、別 role の関係を削除しない。
- 連絡先の作成では ContactMech と具体的な連絡先 entity、PartyContactMech を同じトランザクションで作る。住所・電話・メールの取得サービスは現行の有効な連絡先を返す。
- 連絡先の内容変更では新 ContactMech を作り、PartyContactMech を付け替える。USA / CAN の住所では state / province を検証し、メール形式を検証する。
- `partyIdFrom` を省略した PartyRelationship は 400 とする。このモジュールの認証ユーザーに OFBiz の partyId はない。
- multi のいずれかに重複がある場合は 409、それ以外の複数サービス失敗は 400。バッチ全体を rollback する。

## 受け入れ基準

| ID | 基準 | 主な確認箇所 |
|---|---|---|
| AC-01 | XML 直接 parse 件数と生成・登録カタログが 128 で一致し、全サービスを登録する | `EntityAutoAcceptanceTest.ac01_catalogMatchesEntityAutoServicesParsedFromServiceDefinitions` |
| AC-02 | Entity Auto update に定義外属性を指定すると 400 | `EntityAutoAcceptanceTest.ac02_entityAutoUpdateRejectsFieldsOutsideServiceDefinition` |
| AC-03 – AC-05 | 必須欠落 400、重複 create 409、全 entity-auto の実行・DB 反映 | `EntityAutoAcceptanceTest` |
| AC-06 | 空の INOUT 主キーを省略した create で主キーを採番する | `EntityAutoAcceptanceTest.ac06_optionalInoutPrimaryKeyIsGenerated` |
| AC-20 – AC-21 | 26 simple services を登録し、各サービスを正常実行する | `SimpleServicesTest` |
| AC-30 | Party/Person/role/relationship/住所/電話/メールを連携し、CONTACT_REL の削除後に EMPLOYMENT のみ残す | `MainFlowTest.ac30_partyGroupRelationshipAndContactFlow` |
| AC-31 | UserLogin 応答のパスワードを伏せ、DB 保存値が平文ではなく BCrypt 検証に成功する | `MainFlowTest.ac31_userLoginIsHashedAndMasked` |
| AC-32 | 重複を含む multi は 409 かつ先行行も rollback | `MultiRollbackTest` |
| AC-33 | UserLogin 作成で許可リスト外属性を無視し、enabled は null のままにする | `MainFlowTest.ac33_userLoginIgnoresUnlistedFields` |
| AC-40 – AC-42 | 認証・権限・機密 Entity・component scope を適用する | `PartySecurityTest` |
| AC-43 | HTTPS 必須設定で HTTP は redirect される | `HttpsSecurityTest` |
| AC-45 | local 以外で OpenAPI と Swagger を無効にする | `SpringdocProfileTest` |
| AC-46 | PostgreSQL 上でスキーマ・seed・CRUD を確認する | `PostgresSmokeTest` |
| AC-47 | 全 service が entity-auto、登録 handler、未移植表のいずれかに分類される | `UnportedInventoryTest` |

実行結果は [review.md](review.md)、差分判断は [notes.md](notes.md)、未移植機能の一覧は [unported.md](unported.md) を参照。
