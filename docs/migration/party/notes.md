# Party 移行時の判断と注意点

## 実装判断

- 現行 `ofbiz2spring.py` は service-def XML のみでは Party カタログを作れないため、承認された方式に従い Gradle で inventory を生成してから `--inventory` で catalog を生成する。`migration/tools` は変更しない。レビュー用 inventory は `inventory/` に保持する。
- 共通側に `registerCatalog(String, Map<String, ?>)` が追加されたため、Party は生成 catalog を変換せず登録する。共通 ServiceDefinition への独自変換処理は置かない。
- Common の dispatcher が `executeMulti` にトランザクションを持つため、Party の重複 Advice だけを追加し、二重の multi transaction aspect は置かない。duplicate 判定は `ServiceValueFound` と `PARTY_DUPLICATE` を使う。
- `migration.security.component` を `party` に固定する。Entity API の公開対象は entity-auto catalog の Entity と必要な `extra-entities` に限定する。ローカル・テストは `require-https: false` を明示し、PostgreSQL 設定は `true` のままにする。
- 共通基盤の最新更新を取り込み、空の INOUT 主キーは呼び出し側の値を優先し、未指定の場合はサービス側で採番する。Party の `createAddendum` を受け入れテストで確認する。
- Party の直接 handler は共通 Entity Auto の型変換を通らないため、JSON の日付文字列を JDBC `Timestamp` / `Date` / `Time` に変換する。
- 全 84 entity の JPA mapping を読み込むテスト用に、Party の Gradle test worker heap を 2 GiB に設定する。

## OFBiz との差分

- `partyIdFrom` 未指定時、OFBiz はログインユーザーの partyId を使う。移行モジュールにはその属性がないため、明示的な必須入力エラー 400 にする。
- OFBiz は SHA 系のパスワード処理を用いるが、Spring Boot adapter は Spring Security の BCrypt `PasswordEncoder` を使用する。currentPassword は hash のみ保存し、レスポンスでは common EntityRegistry が `[REDACTED]` にする。
- UserLogin の作成は `UserLoginPort` と local adapter に分離し、確認値一致、最小長 5、重複 ID を検査する。
- PartyRelationship の複合キーには from/to party、from/to role、fromDate が含まれる。削除条件に role と partyRelationshipTypeId を含め、異なる role / relationship type の行を誤って削除しない。
- 連絡先更新時は既存 ContactMech を上書きせず新規 ContactMech を作り、PartyContactMech を期限切れにして付け替える。state / province の検査とメール形式の検査も行う。
- local seed は PartyType、RoleType、PartyRelationshipType、ContactMechType、ContactMechPurposeType、StatusType、StatusItem のうち、主要フローが必要とする最小集合を投入する。

## 機密 Entity

`application.yml` の機密 Entity は Party datamodel の UserLogin 系と、PaymentMethod / 金融口座・カード、PartyIdentification、税情報、給与情報等を対象にする。値は EntityRegistry のマスキングに委ね、読み書き API は `PARTYMGR_SENSITIVE_VIEW` を別途要求する。データモデルに存在しない候補は設定対象から外し、理由を `review.md` に記録する。

`UserLoginSecurityQuestion` は datamodel の入力 entity model に存在しないため、機密 Entity の設定対象から除外した。`CommunicationEventWorkEffort` ではなく、実在する join entity 名 `CommunicationEventWorkEff` と `WorkEffort` を `extra-entities` に設定する。

## 既知の境界

- Party 画面・widget、request-map、ECA、メールテンプレートは UI 実行環境や外部副作用を伴うため、今回の REST 移行対象外。全件を [unported.md](unported.md) に記録する。
- group / interface サービスは、指定された `createPartyRelationshipAndRole` 以外は移植しない。
- E2E レポートは別途実行するための雛形のみを置き、ここでは E2E 結果を記載しない。
