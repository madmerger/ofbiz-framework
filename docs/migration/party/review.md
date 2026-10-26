# Party 移行レビュー

## 検証結果

| 項目 | 結果 |
|---|---|
| Party inventory 生成 | 成功。84 entities / 128 entity-auto / 274 requests / 266 services / 41 view entities / 111 view maps |
| Catalog 生成 | Gradle `generatePartyCatalog` 成功。84 entity/repository、128 entity-auto service |
| Party テスト | 最終ゲート `./gradlew :migration:party-springboot:test checkstyleMain checkstyleTest codenarcMain codenarcTest` 成功。147 件成功、失敗 0、エラー 0、スキップ 0 |
| Checkstyle / CodeNarc | `./gradlew checkstyleMain checkstyleTest codenarcMain codenarcTest` 成功。Party サブプロジェクト固有の解析タスクは未定義 |
| PostgreSQL / Testcontainers | `PostgresSmokeTest` 1 件成功。コンテナ停止後の context shutdown で Hibernate / Hikari が接続拒否を記録したが、テストは成功 |
| local curl | anonymous 401、`nopartymgr` 403、`admin` 200。確認後にローカルサーバーを停止 |
| commit / push | 未実施。この作業ハンドオフではワークツリーをレビュー用に保持 |

### テストクラス別

| クラス | 成功 | 失敗 | スキップ |
|---|---:|---:|---:|
| EntityAutoAcceptanceTest | 133 | 0 | 0 |
| HttpsSecurityTest | 1 | 0 | 0 |
| MainFlowTest | 3 | 0 | 0 |
| MultiRollbackTest | 1 | 0 | 0 |
| PartySecurityTest | 3 | 0 | 0 |
| PostgresSmokeTest | 1 | 0 | 0 |
| SimpleServicesTest | 3 | 0 | 0 |
| SpringdocProfileTest | 1 | 0 | 0 |
| UnportedInventoryTest | 1 | 0 | 0 |
| 合計 | 147 | 0 | 0 |

## 受け入れ基準

| ID | 判定 | 根拠 |
|---|---|---|
| AC-01 – AC-06 | 成功 | `EntityAutoAcceptanceTest` |
| AC-20 – AC-22 | 成功 | `SimpleServicesTest` |
| AC-30 – AC-31、AC-33 | 成功 | `MainFlowTest` |
| AC-32 | 成功 | `MultiRollbackTest` |
| AC-40 – AC-42 | 成功 | `PartySecurityTest` |
| AC-43 | 成功 | `HttpsSecurityTest` |
| AC-45 | 成功 | `SpringdocProfileTest` |
| AC-46 | 成功 | `PostgresSmokeTest` |
| AC-47 | 成功 | `UnportedInventoryTest` |

## 移植件数

| 種別 | 件数 |
|---|---:|
| entity-auto | 128 |
| simple | 26 |
| Java / Groovy | 19 |
| group | 1 |
| 未移植 service | 92 |

### OFBiz との差分と制約

- `partyIdFrom` が未指定の場合は 400 とし、notes に記載した。
- UserLogin は BCrypt hash で保存する。OFBiz の SHA 系処理とは互換ではない。
- Entity API は catalog entity と `extra-entities` の範囲に限定する。画面 / controller request / ECA は移植対象外。
- 共通基盤は指定 base branch の更新を取り込んだだけで、本変更による共通基盤のソース変更はない。
- テストでは全 84 entity の JPA mapping を読み込むため、Party の Gradle test worker heap を 2 GiB に設定している。
- Checkstyle / CodeNarc のルートタスクは成功したが、`migration:party-springboot` には専用タスクがないため、同モジュール単独の静的解析は実施していない。
