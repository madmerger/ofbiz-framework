# humanres Spring Boot パイロット レビュー / テスト結果

## 1. 実行結果

| 検証 | コマンド | 結果 |
| --- | --- | --- |
| Spring Boot モジュールの全テスト | `./gradlew :migration:humanres-springboot:test` | **157 件 pass / 0 fail / 0 skip**（EntityAutoAcceptanceTest 136、HrTreeSecurityTest 7、HumanResBusinessServicesTest 13、PostgresSmokeTest 1） |
| PostgreSQL（Testcontainers） | 上に含まれる | 1 件 pass（skip なし） |
| 棚卸しツール・生成器 | `python3 -m unittest discover -s migration/tools -p 'test_*.py'` | 6 件 pass |
| 既存 OFBiz テスト（回帰） | `./gradlew test --tests 'org.apache.ofbiz.humanres.HumanResEventsTest'` | 3 件 pass |
| 静的解析 | `./gradlew checkstyleMain checkstyleTest codenarcMain codenarcTest` | BUILD SUCCESSFUL |
| 起動と疎通 | `HUMANRES_DEMO_PASSWORD=... ./migration/humanres-springboot/run-local.sh` + curl | health 200 / hr-tree（admin）200 / HUMANRES 権限なし 403 / 未認証 401 / create・update・expire 200 / multi の失敗時 400 → ロールバック確認（GET 404） |

操作確認用に Swagger UI（/swagger-ui.html、local プロファイル）を追加。

## 2. 受け入れ基準の判定（spec §11）

| AC | 判定 | テスト |
| --- | --- | --- |
| AC-01 | PASS | `EntityAutoAcceptanceTest.ac01_allInventoryEntitiesAreMapped` |
| AC-02 | PASS | `ac02_generatedEntityReceivesAllStampColumns` |
| AC-03 | PASS | `git diff trunk -- applications framework plugins` が空。ルートの変更は `settings.gradle` の include 1 行のみ |
| AC-04 | PASS | H2: 全テスト。PostgreSQL: `PostgresSmokeTest.ac04_postgresSchemaAndCrudSmoke` |
| AC-10 | PASS | `ac10_generatedCatalogMatchesInventory`（121 パラメタライズ） |
| AC-11 | PASS | `ac11_sequenceIdsAndSubSequencesFollowOfbizShape`（PerfReviewItem のサブシーケンスを含む） |
| AC-12 | PASS | `ac12_missingFromDateDefaultsToNow` + PK でない fromDate の回帰テスト |
| AC-13 | PASS | `ac13_duplicateCreateReturnsConflict` |
| AC-14 | PASS | `ac14_requiredAndMissingEntityErrorsAreDistinct` + 必須 IN の PK（TrainingClassType / SalaryStep） |
| AC-15 | PASS | `ac15_updatesOnlyProvidedFields` |
| AC-16 | PASS | `ac16_rejectsStatusTransitionWithoutStatusValidChange` |
| AC-17 | PASS | `ac17_expireAndDeleteAreDistinctOperations` |
| AC-18 | PASS | `ac18_deniesDangerousText` |
| AC-20〜30 | PASS | `HumanResBusinessServicesTest.ac20_*` 〜 `ac30_*`（各 1 件。AC-20 と AC-30 はロールバックも検証） |
| AC-40 | PASS | `HrTreeSecurityTest.ac40_anonymousIsRejectedBeforeTreeLookup`（既存テスト 1 の再現。ツリーサービスが呼ばれないことを verify） |
| AC-41 | PASS | `ac41_missingHumanResAuthorityIsRejectedBeforeTreeLookup`（既存テスト 2 の再現） |
| AC-42 | PASS | `ac42_authorizedHumanResViewReachesTreeService`（既存テスト 3 の再現） |
| AC-43 | PASS | `ac43_humanResAuthorityWithoutOfbtoolsBaseGateIsRejected` |
| AC-44 | PASS | `ac44_missingPerServiceActionIsRejectedBeforeEntityAutoDispatch` + permissionMainAction 未定義の create/delete 拒否と未 dispatch の検証 |
| AC-45 | PASS | `ac45_localProfileUsesEnvironmentSuppliedPassword`。リポジトリに平文の認証情報なし |

## 3. 旧 OFBiz 版との挙動差分

| # | 差分 | OFBiz | Spring Boot 版 | 影響と方針 |
| --- | --- | --- | --- | --- |
| B-01 | 権限が無いときの応答 | `getHRChild`: 403 + `error`。未ログインはログイン画面へリダイレクト | 未認証は 401、権限不足は 403（JSON） | API 化に伴う意図的な差分 |
| B-02 | エラーの表現 | `_ERROR_MESSAGE_` + 同じ画面を再描画 | HTTP ステータス（400/404/409）+ `{"responseMessage":"error","errorMessage":...}` | 同上 |
| B-03 | 入出力の形式 | フォーム POST、画面（HTML） | JSON。日付は ISO-8601 | 同上 |
| B-04 | 画面 | Widget / FTL の 121 screens | 無し（API のみ） | UI は別途再構築（PLAYBOOK §4.4） |
| B-05 | 認証 | OFBiz の UserLogin / セッション / 外部ログインキー | HTTP Basic（stateless）、インメモリユーザ | 全体移行時に OIDC 等へ置き換え |
| B-06 | エラーメッセージの文言 | UiLabels でローカライズ | 英語固定のキー（`ServiceValueFound` 等） | i18n は未対応 |
| B-07 | ECA / SECA | humanres に独自の ECA は無いが、呼び出し先の party サービスなどの SECA は動く | アダプタは SECA を実行しない | 全体移行時に party 側の移行で吸収 |
| B-08 | permission-service 未定義の entity-auto サービス | 認証＋base-permission(VIEW) のみで実行可能 | invoke 種別に応じた HUMANRES_CREATE/UPDATE/DELETE を要求 | セキュリティ強化のための意図的差分 |

## 4. 既知の制約・未対応項目

- 本番運用では TLS 終端（HTTPS）必須。HTTP Basic は平文 HTTP では資格情報が露出する。
- HUMANRES_VIEW で給与履歴等の全エンティティを参照可能（OFBiz 画面と同等）。フィールド/エンティティ単位の閲覧制御は全体移行時に設計。
- Widget / FTL / メニュー / Lookup 画面（UI）は未移植。
- 105 本ある「画面のみ」のリクエストは `GET /entities/{EntityName}` に一般化した。画面固有の結合（view-entity の一部や `FindEmployee.ftl` の検索条件）は未実装。view-entity 5 本は JPA 化していない。
- アダプタの local 実装は最小限のカラムだけ（PARTY / PERSON / PARTY_ROLE / PARTY_RELATIONSHIP / WORK_EFFORT / RATE_AMOUNT など）。party の `createPersonRoleAndContactMechs` のうち連絡先（ContactMech）作成は未実装。
- `isValidText` 相当の deny-list は OFBiz と同じ部分一致のため、普通の文に含まれる語（system / include 等）も拒否してしまう（OFBiz と同じ挙動）。
- スキーマ生成は Hibernate `ddl-auto`。本番では Flyway 等で既存 OFBiz スキーマとの差分を管理する必要がある。
- 実データの入った OFBiz DB に対する並行稼働の検証（同じ DB を新旧両方から読み書きする）は未実施。
- `SEQUENCE_VALUE_ITEM` を OFBiz と共有して採番すると、OFBiz 側の bank（キャッシュ採番）と衝突する恐れがある。並行稼働時は採番を片方に寄せる必要がある。
