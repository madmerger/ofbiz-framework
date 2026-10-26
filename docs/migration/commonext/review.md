# CommonExt 移行レビュー

## 1. 検証結果

| 検証 | 結果 |
| --- | --- |
| CommonExt Spring Boot テスト | 成功: 34件（`CommonextHttpsTest` 1、`CommonextSecurityTest` 9、`EntityAutoAcceptanceTest` 3、`MultiRollbackTest` 2、`PostgresSmokeTest` 1、`SystemInfoNoteServiceTest` 18）。失敗・skip なし |
| module Checkstyle (`checkstyleMain`, `checkstyleTest`) | 成功。生成 `ServiceCatalog` はコンパイル対象のまま Checkstyle 対象外 |
| root `checkstyleMain codenarcMain` | 実行不能。root `runtimeClasspath` の `ical4j:1.0-rc4-atlassian-12` と `activiti-juel-jakarta:8.1.0` が Maven Central の HTTP 429 で取得不可。Google Maven Central mirror も両 artifact が HTTP 404。root build は `No hooks found` でも失敗 |
| pre-commit | `Checkstyle` / `RuboCop` hook ID の小文字修正を含む。初回の Docker Hub HTTP 429 は承認済み手順で `mirror.gcr.io` から pull/tag して解消し、hook は迂回せず実行 |
| local 起動 / HTTP 401・200 / Swagger UI / password redaction | 成功: port 8080。未認証 Entity API は 401、認証後の Entity API と `/swagger-ui/index.html` は 200。`editor` で `UserLogin/admin` を取得し `currentPassword` が `[REDACTED]` になることを curl で確認。root Gradle による `run-local.sh` は依存取得失敗のため、外部 Gradle bootstrap から同じ `bootRun` を起動 |
| PostgreSQL Testcontainers | 成功: `PostgresSmokeTest` 1件 |

## 2. 既知の OFBiz との差分

| 項目 | OFBiz | Spring Boot 移行 |
| --- | --- | --- |
| service permission | Webapp base permission `OFBTOOLS,SETUP` が中心 | base permission に加えて各 service に VIEW / UPDATE を適用 |
| 公開ノート | `COMMONEXT_PUBLMSG` は公開情報の作成権限 | 別 party 宛て作成時に `COMMONEXT_PUBLMSG` または `SETUP_ADMIN` を要求 |
| 単件削除 | owner check を持たない | `SYSTEMNOTE` のみ削除可能。非 `SYSTEMNOTE` は権限に関係なく 404 とし、owner または admin のみ削除できる |
| 作成レスポンス | 空 success map | `noteId` を返す |
| delete-all レスポンス | 空 success map | `deletedCount` を返す |
| `getLastSystemInfoNote` | service `auth=false` | REST API は認証必須 |
| multi 失敗 | 共通 runtime は HTTP 400 | HTTP 400 と全件 rollback。409 は適用しない |
| 入力検証 | service に HTML / URL 制限なし | 指定 deny-list と URL scheme 制限を追加 |
| status communication | view の CE.statusId に `COM_ROLE_COMPLETED` を比較 | レガシー条件を意図的に再現 |
| latest note | 2件目・3件目の off-by-one | 互換性のため条件を再現 |
| 認証 provider | OFBiz user login / permission data | Basic 認証と profile 定義の in-memory user |
| API component | `/api/{component}` | `migration.security.component=commonext` 固定。異なる component 名は404 |
| 汎用 Entity API | 画面単位のアクセス | entity-auto catalog と `extra-entities` allowlist のみを read API で公開。機密 Entity は別権限。NoteData は他 party の行も読み取り可能 |

## 3. セキュリティ・既知の制約

- Entity API は read-only だが、一覧・単件検索の範囲を業務 party に自動限定しない。アクセス可能な Entity の分割が必要な運用では個別 API に置き換える。
- `NoteData` は generic Entity API 経由で他 party の行も読める。本移行では legacy service ACL と汎用 runtime の API 認可を混同しないよう明記した。
- allowlist 内の機密 Entity `UserLogin` は `SETUP_SENSITIVE_VIEW` または `SETUP_ADMIN` を要求し、`currentPassword` を `[REDACTED]` にする。`CreditCard` / `PayHistory` は allowlist 外のため 404。
- PostgreSQL HTTPS 必須時は信頼済み proxy CIDR を環境側で明示する。未指定の `a^` は信頼対象なし。
- HTML/URL deny-list は移行仕様の保護措置であり、ブラウザ出力時の適切な escaping の代替ではない。

## 4. 実装差分 / 残作業

- `SetupEvents.xml` の定義数は事前情報11本に対して XML 実測9本だった。9本すべて未移植。
- original view/entity screen や画面アクションは対象外。read-only generic Entity API はデータ参照面の代替であり、フォーム・遷移・業務イベントの同等性を保証しない。
- `e2e-report.md` は依頼により作成しない。
