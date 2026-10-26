# content Spring Boot 移行 レビュー / テスト結果

## 1. 実行結果

| 検証 | コマンド | 結果 |
| --- | --- | --- |
| Spring Boot モジュールの全テスト | `./gradlew :migration:content-springboot:test` | **201 件 pass / 0 fail / 0 skip**（EntityAutoAcceptanceTest 180、ContentMainFlowTest 5、ContentSimpleServicesTest 6、ContentSecurityTest 7、ContentProfileTest 2、PostgresSmokeTest 1） |
| PostgreSQL（Testcontainers 1.21.4、postgres:16-alpine） | 上に含まれる | 1 件 pass（skip なし） |
| 静的解析 | `./gradlew :migration:content-springboot:checkstyleMain :migration:content-springboot:checkstyleTest` | BUILD SUCCESSFUL（ルートの `config/checkstyle/checkstyle.xml` を適用。Groovy ソースはないため CodeNarc の対象なし） |
| 起動と疎通 | `CONTENT_DEMO_PASSWORD=... ./migration/content-springboot/run-local.sh` + `cases.json` を curl で実行 | health UP、CONTENT-E2E-01〜26 すべて期待どおり |
| E2E（Swagger UI、testing agent） | `cases.json` の順に実行 | `e2e-report.md` を参照 |

件数: entity-auto 176（全量）、simple 7（全量）、移植した java 4 / groovy 7（+ route 2 / group 2）、未移植サービス 90（java 53 / groovy 30 / group 4 / interface 3）。詳細は `unported.md`。

## 2. 受け入れ基準の判定（spec §5）

| AC | 判定 | テスト |
| --- | --- | --- |
| AC-01 | PASS | `EntityAutoAcceptanceTest.ac01CatalogMatchesInventoryEntityAutoServices` |
| AC-02 | PASS | `ac02_<サービス名>`（176 パラメタライズ。create は永続化、update は値の反映、expire は thruDate、delete は削除を確認） |
| AC-03 | PASS | `ac03DuplicateCreateReturnsConflictAndMissingUpdateReturnsNotFound` |
| AC-04 | PASS | `ac04CreateContentAppliesServicedefDefaultsAndSequence`、`ac05CreateContentAssocDefaultsFromDate` |
| AC-20 | PASS | `ContentMainFlowTest.ac20DataResourceContentAssocAndRetrieval` |
| AC-21 | PASS | `ac21GroupAndRouteServices` |
| AC-22 | PASS | `ac22ContentFromDataResourceAndSimpleText` |
| AC-23 | PASS | `ac23TemplateDataResourceRequiresSuperPermission` |
| AC-30〜34 | PASS | `ContentSimpleServicesTest.ac30`〜`ac34` |
| AC-40 | PASS | `ContentSecurityTest.ac40UnauthenticatedRequestsAreRejected` |
| AC-41 | PASS | `ac41MissingPermissionsAreForbidden` |
| AC-42 | PASS | `ac42SensitiveEntitiesRequireDedicatedPermission` |
| AC-43 | PASS | `ac43MultiConflictRollsBackEveryRow` |
| AC-44〜47 | PASS | `ac44SwaggerIsDisabledOutsideLocalProfile`、`ac45ProfilesKeepSecretsOutAndRequireHttpsInPostgres`、`ContentProfileTest.ac46`・`ac47` |
| AC-48 | PASS | `PostgresSmokeTest.ac48PostgresCrudAndMainFlowSmoke` |
| AC-49 | PASS | `ac49OtherComponentPathIsNotFound` |
| AC-90 | PASS | `git diff origin/devin/1793006105-springboot-common -- applications framework` が空 |

## 3. OFBiz との差分

| # | 差分 | 理由 |
| --- | --- | --- |
| D-01 | 画面（Widget/FTL、controller）は移植せず REST API + Swagger UI（local のみ）で代替 | スコープ外 |
| D-02 | `CONTENTMGR_ROLE` による所有者権限は `genericDataResourcePermission` の判定結果としてのみ提供。サービス呼び出しは `CONTENTMGR_<action>` が必要（共通 dispatcher が handler 実行前に検査するため） | 共通基盤の権限モデル |
| D-03 | 所有者判定の partyId は Spring Security のユーザー名（UserLogin.partyId の代替） | UserLogin を移行していない |
| D-04 | `createDataResource` の SECA のうち「partyId も roleTypeId もない場合に userLogin.partyId を OWNER にする」は未再現（D-03 と同じ理由） | |
| D-05 | `getWebSitePathAlias` は Entity の `aliasTo` を `pathTo` として返す（OFBiz の minilang は存在しない `pathTo` を参照し常に空） | 不具合の修正 |
| D-06 | `updateSimpleTextContent` は `dataResourceId` に `textDataResourceId` を返す（OFBiz は `parameters.textdataResourceId` の綴り誤りで常に空） | 不具合の修正 |
| D-07 | `generateMissingSeoUrlForWebsite` の URL 生成は `CatalogSeoPort` 経由。local adapter は件数 0 を返す（生成処理は product 移行後に実装） | 他コンポーネント（product）依存 |
| D-08 | `createSurveyResponse` の CONTENT 型でのファイル（ByteBuffer）アップロード、`survey.responseService` の非同期呼び出しは未移植 | ファイルアップロードはスコープ外。サービスエンジンがない |
| D-09 | 検証メソッドは OFBiz `UtilValidate` の簡易版（Luhn、日付 yyyy-MM-dd、メール、URL、数値） | framework に依存しない |
| D-10 | `/multi` の失敗は重複を含めば 409、それ以外は 400（OFBiz は画面へエラー表示） | API 化 |
| D-11 | `getContent` の `view` は `ContentDataResourceView` を Java で結合して再現（キャッシュなし） | view-entity は未 JPA 化 |
| D-13 | `createSurveyResponse` の更新（allowUpdate）は同じ Survey・同じ partyId の回答だけを許可（OFBiz は所属を確認しない）。`createContent` / `updateTextContent` は servicedef で OUT の `serviceName` / `customMethodId` を入力から受け付けない | Devin Review のセキュリティ指摘への対応 |
| D-12 | `createSurveyMultiResp` / `createSurveyMultiRespColumn` は handler でサブシーケンス採番（共通の entity-auto は `*SeqId` 名のみ対象） | OFBiz と同じ採番結果にするため |

## 4. 既知の制約

- 共通基盤への変更なし。生成ツールの不足（default-entity-name なし、interface 継承、mode なし override）は `ContentServiceCatalog` で補完した。共通ツール側で直せば補完は不要になる。
- `/multi` の 409 は `ContentMultiErrorAdvice`（コンポーネント内）で実装。共通の例外ハンドラは 400 のまま。
- view-entity 12 本、画面、Lucene、ファイル/画像、FTP、出力（FOP）、ブログ/フォーラム系は未移植（`unported.md`）。
- 本番 PostgreSQL は `ddl-auto: validate`。スキーマは既存 OFBiz DB（または別途 DDL）を前提とする。
