# content E2E レポート（Swagger UI）

- 対象: PR #3 `devin/1793009056-content-springboot` @ `f351689965`
- 方法: `migration/content-springboot` を local プロファイル（H2 インメモリ）で単体起動し（旧 OFBiz は起動しない）、testing agent がブラウザの Swagger UI（`/swagger-ui/index.html`）から `cases.json` の CONTENT-E2E-01〜26 をファイル順に実行して録画（1600×1000、1 本、各画面にケース id と名称を表示）
- 秘密情報: デモパスワードは環境変数 `CONTENT_DEMO_PASSWORD`。録画で認証入力欄と Authorization ヘッダーは隠している
- 結果: **26/26 件 合格**（HTTP ステータスと指定値がすべて一致）

| id | 区分 | ケース | ユーザー | リクエスト | 期待値 | 実測値 | 判定 |
| --- | --- | --- | --- | --- | --- | --- | --- |
| CONTENT-E2E-01 | 未認証 401 | 未認証で entity-auto を呼ぶと 401 | （未認証） | `POST /api/content/services/createContentType` | HTTP status=401 | HTTP status=401 | 合格 |
| CONTENT-E2E-02 | 権限不足 403 | 閲覧専用ユーザーで entity-auto 作成は 403 | readonly | `POST /api/content/services/createContentType` | HTTP status=403 | HTTP status=403 | 合格 |
| CONTENT-E2E-03 | 権限不足 403 | CONTENTMGR 権限なしユーザーの Entity 取得は 403 | nocontent | `GET /api/content/entities/Content` | HTTP status=403 | HTTP status=403 | 合格 |
| CONTENT-E2E-04 | 機密エンティティ | 機密エンティティ SurveyResponseAnswer: 閲覧専用ユーザーは 403 | readonly | `GET /api/content/entities/SurveyResponseAnswer` | HTTP status=403 | HTTP status=403 | 合格 |
| CONTENT-E2E-05 | 機密エンティティ | 機密エンティティ SurveyResponseAnswer: CONTENTMGR_SENSITIVE_VIEW ありは 200 | auditor | `GET /api/content/entities/SurveyResponseAnswer` | HTTP status=200; $=配列（JSON array） | HTTP status=200; $=[] | 合格 |
| CONTENT-E2E-06 | entity-auto CRUD | entity-auto 作成 createContentType | editor | `POST /api/content/services/createContentType` | HTTP status=200; $.contentTypeId=E2E_TYPE; $.responseMessage=success | HTTP status=200; $.contentTypeId=E2E_TYPE; $.responseMessage=success | 合格 |
| CONTENT-E2E-07 | entity-auto CRUD | entity-auto 取得（作成後） | readonly | `GET /api/content/entities/ContentType?contentTypeId=E2E_TYPE` | HTTP status=200; $[0].description=E2E 種別 | HTTP status=200; $[0].description=E2E 種別 | 合格 |
| CONTENT-E2E-08 | entity-auto CRUD | entity-auto 更新 updateContentType | editor | `POST /api/content/services/updateContentType` | HTTP status=200; $.description=E2E 種別 更新 | HTTP status=200; $.description=E2E 種別 更新 | 合格 |
| CONTENT-E2E-09 | entity-auto CRUD | entity-auto 取得（更新後） | readonly | `GET /api/content/entities/ContentType?contentTypeId=E2E_TYPE` | HTTP status=200; $[0].description=E2E 種別 更新 | HTTP status=200; $[0].description=E2E 種別 更新 | 合格 |
| CONTENT-E2E-10 | entity-auto CRUD | entity-auto 削除 removeContentType | editor | `POST /api/content/services/removeContentType` | HTTP status=200; $.responseMessage=success | HTTP status=200; $.responseMessage=success | 合格 |
| CONTENT-E2E-11 | entity-auto CRUD | entity-auto 取得（削除後は 0 件） | readonly | `GET /api/content/entities/ContentType?contentTypeId=E2E_TYPE` | HTTP status=200; $.length()=0 | HTTP status=200; $.length()=0 | 合格 |
| CONTENT-E2E-12 | simple サービス | simple createSurveyQuestion: ENUMERATION で enumTypeId なしは 400 | editor | `POST /api/content/services/createSurveyQuestion` | HTTP status=400; $.errorMessage=ContentQuestionTypeRequiredField: enumTypeId | HTTP status=400; $.errorMessage=ContentQuestionTypeRequiredField: enumTypeId | 合格 |
| CONTENT-E2E-13 | simple サービス | simple createSurveyQuestion: TEXT_SHORT は採番して作成 | editor | `POST /api/content/services/createSurveyQuestion` | HTTP status=200; $.surveyQuestionId=採番された数値 ID | HTTP status=200; $.surveyQuestionId=10000 | 合格 |
| CONTENT-E2E-14 | simple サービス | simple genericDataResourcePermission: テンプレート（FTL）は CONTENTMGR_SUPER がない editor では false | editor | `POST /api/content/services/genericDataResourcePermission` | HTTP status=200; $.hasPermission=false | HTTP status=200; $.hasPermission=false | 合格 |
| CONTENT-E2E-15 | 主要フロー | 主要フロー: DataResource + ElectronicText 作成 createDataResourceAndText | editor | `POST /api/content/services/createDataResourceAndText` | HTTP status=200; $.dataResourceId=E2E_DR_1 | HTTP status=200; $.dataResourceId=E2E_DR_1 | 合格 |
| CONTENT-E2E-16 | 主要フロー | 主要フロー: 子 Content 作成 createContent（DataResource を参照） | editor | `POST /api/content/services/createContent` | HTTP status=200; $.contentId=E2E_CNT_CHILD; $.contentTypeId=DOCUMENT; $.statusId=CTNT_IN_PROGRESS | HTTP status=200; $.contentId=E2E_CNT_CHILD; $.contentTypeId=DOCUMENT; $.statusId=CTNT_IN_PROGRESS | 合格 |
| CONTENT-E2E-17 | 主要フロー | 主要フロー: 親 Content 作成 createContent | editor | `POST /api/content/services/createContent` | HTTP status=200; $.contentId=E2E_CNT_PARENT | HTTP status=200; $.contentId=E2E_CNT_PARENT | 合格 |
| CONTENT-E2E-18 | 主要フロー | 主要フロー: 関連付け createContentAssoc（SUB_CONTENT） | editor | `POST /api/content/services/createContentAssoc` | HTTP status=200; $.contentIdTo=E2E_CNT_CHILD | HTTP status=200; $.contentIdTo=E2E_CNT_CHILD | 合格 |
| CONTENT-E2E-19 | 主要フロー | 主要フロー: 取得 getContent（ContentDataResourceView 相当） | readonly | `POST /api/content/services/getContent` | HTTP status=200; $.view.contentName=E2E 子コンテンツ; $.view.drDataResourceTypeId=ELECTRONIC_TEXT | HTTP status=200; $.view.contentName=E2E 子コンテンツ; $.view.drDataResourceTypeId=ELECTRONIC_TEXT | 合格 |
| CONTENT-E2E-20 | 主要フロー | 主要フロー: 取得 getContentAndDataResource（本文込み） | readonly | `POST /api/content/services/getContentAndDataResource` | HTTP status=200; $.resultData.electronicText.textData=E2E 本文テキスト | HTTP status=200; $.resultData.electronicText.textData=E2E 本文テキスト | 合格 |
| CONTENT-E2E-21 | 主要フロー | 主要フロー: 関連の取得（ContentAssoc） | readonly | `GET /api/content/entities/ContentAssoc?contentId=E2E_CNT_PARENT` | HTTP status=200; $[0].contentIdTo=E2E_CNT_CHILD | HTTP status=200; $[0].contentIdTo=E2E_CNT_CHILD | 合格 |
| CONTENT-E2E-22 | 主要フロー | 主要フロー: 本文更新 updateDataResourceAndText | editor | `POST /api/content/services/updateDataResourceAndText` | HTTP status=200; $.dataResourceId=E2E_DR_1 | HTTP status=200; $.dataResourceId=E2E_DR_1 | 合格 |
| CONTENT-E2E-23 | 主要フロー | 主要フロー: 更新後の本文取得 getElectronicText | readonly | `POST /api/content/services/getElectronicText` | HTTP status=200; $.textData=E2E 本文テキスト 更新 | HTTP status=200; $.textData=E2E 本文テキスト 更新 | 合格 |
| CONTENT-E2E-24 | multi 409 とロールバック | multi: 3 行中 3 行目が重複で 409 | editor | `POST /api/content/services/createContentType/multi` | HTTP status=409; $.errors[0].index=2; $.errors[0].errorMessage=ServiceValueFound | HTTP status=409; $.errors[0].index=2; $.errors[0].errorMessage=ServiceValueFound | 合格 |
| CONTENT-E2E-25 | multi 409 とロールバック | multi: 1 行目（E2E_M_A）もロールバックされ 0 件 | readonly | `GET /api/content/entities/ContentType?contentTypeId=E2E_M_A` | HTTP status=200; $.length()=0 | HTTP status=200; $.length()=0 | 合格 |
| CONTENT-E2E-26 | multi 409 とロールバック | multi: 2 行目（E2E_M_B）もロールバックされ 0 件 | readonly | `GET /api/content/entities/ContentType?contentTypeId=E2E_M_B` | HTTP status=200; $.length()=0 | HTTP status=200; $.length()=0 | 合格 |

## 区分ごとの結果

| 区分 | ケース | 結果 |
| --- | --- | --- |
| 未認証 401 | 01〜01 | 1/1 合格 |
| 権限不足 403 | 02〜03 | 2/2 合格 |
| 機密エンティティ | 04〜05 | 2/2 合格 |
| entity-auto CRUD | 06〜11 | 6/6 合格 |
| simple サービス | 12〜14 | 3/3 合格 |
| 主要フロー | 15〜23 | 9/9 合格 |
| multi 409 とロールバック | 24〜26 | 3/3 合格 |

## 範囲外

- 旧 OFBiz との同時起動・比較、本番 PostgreSQL での UI 操作（PostgreSQL は JUnit の Testcontainers smoke で確認）、cases.json 以外のサービス。
