# Party E2E 再実行レポート

## 実行条件

- 対象: [PR #6](https://github.com/madmerger/ofbiz-framework/pull/6)、ブランチ `devin/1793009059-party-springboot`、HEAD `da504ff140`。
- 実行日: 2026-10-26、11:45〜11:54 UTC（実行環境・HTTP応答の時計）。実行者: Devin testing agent。
- Ubuntu / OpenJDK 17.0.19 / Gradle wrapper / Chrome / Xvfb。
- ランダムな `PARTY_DEMO_PASSWORD` をプロセス環境へ設定し、`migration/party-springboot/run-local.sh` で再ビルド・再起動。Gradleの依存・生成・コンパイルタスクを経た増分ビルド（変更のないタスクはUP-TO-DATE）で、`local` プロファイルのParty Spring Bootだけをポート8080で起動した。旧OFBizは起動していない。既存のMaven mirror設定は変更していない。
- 再起動により新規H2インメモリDBから開始した。接続先: `jdbc:h2:mem:party-local;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`。ケース実行中の再起動なし。
- UI: `http://localhost:8080/swagger-ui/index.html`。Playwrightによるブラウザ操作でSwaggerのAuthorize・入力欄・Try it out・Executeを使用した。認証付き要求を直接APIクライアントで代替していない。
- 更新済み `cases.json` のPARTY-E2E-01〜19を順番どおりに実行。12/13の `expected.verify` GETはそれぞれ作成直後、同一ケース内で実行。19ケース後に05の補足GETを実行した。
- `{ORG}=10000`、`{PERSON}=10001`。18の `fromDate` は15の2件目（CONTACT_REL）の応答文字列 `2026-10-26T11:51:39.410+00:00` を無加工で使用した。
- 新規証跡ディレクトリ: `/tmp/party-e2e-da504ff140/`。前回証跡とは混在させていない。
- 録画は連続1回のキャプチャから生成した1本。配布MP4は1440×1000・15fps・82.2秒（操作区間を中心に録画ツールが編集）、WebPは同じ内容・1440×1000・5fps。ケースIDと名称を固定バナー表示し、日本語の判定アノテーションを付けた。ビルドと準備は録画前に完了した。
- 認証入力は伏せ字。資格情報を含むSwaggerのcurlブロックと、10のパスワード付き入力本文は表示層だけで非表示にした。保存した要求本文でもパスワード項目は `[非表示]` とした。
- アプリケーションコード・`cases.json` は変更していない。このレポートのみ今回の結果で全面置換し、未コミット。

## 結果の概要

**19ケースを最初から実行し、更新済みcases.jsonのHTTPステータス・主要値・verify条件は19件とも一致した。** 05の伏せ字は実在行の補足GETで確認した。18は削除HTTP 200、直後の再取得HTTP 200、残件数1・EMPLOYMENTのみを確認した。19は409後に空配列となり、1件目もロールバックされた。

これは指定されたlocal/H2・Swaggerの19ケースに対する結果であり、全サービス・全入力・本番DBの網羅検証を意味しない。**10の保存ハッシュ実体はSwaggerでは検証不能**であり、本E2Eの確認対象は作成応答と再取得での伏せ字まで。

## cases.json IDごとの期待値と実測値

| ID | 期待値 | 実測値 | 判定 |
|---|---|---|---|
| PARTY-E2E-01 | 未認証GET Party →401 | 401 Unauthorized。Chromeのネイティブ認証ダイアログ取消後、Swaggerの実応答を確認 | 一致 |
| PARTY-E2E-02 | nopartymgrのGET Party →403 | 403 Forbidden | 一致 |
| PARTY-E2E-03 | readonlyのcreatePartyGroup →403 | 403、`Service permission denied` | 一致 |
| PARTY-E2E-04 | readonlyのGET UserLogin →403 | 403、`Sensitive entity access denied` | 一致 |
| PARTY-E2E-05 | sensitiveのGET UserLogin →200、全行パスワード伏せ字またはnull | 初回200 `[]`。19ケース後の補足GETは200・e2e-hanakoの1行、`currentPassword="[REDACTED]"` | 一致（補足で実在行を確認） |
| PARTY-E2E-06 | 作成200、E2E_CLASS、responseMessage=success | 200、`partyClassificationTypeId=E2E_CLASS`、`responseMessage=success` | 一致 |
| PARTY-E2E-07 | 単件取得200、description=E2E 分類 | 200、`description="E2E 分類"` | 一致 |
| PARTY-E2E-08 | 更新200、再取得description=E2E 分類（更新） | 更新200、再取得200、`description="E2E 分類（更新）"` | 一致 |
| PARTY-E2E-09 | 削除200、再取得404 | 削除200、単件再取得404 `Entity not found` | 一致 |
| PARTY-E2E-10 | 作成200、partyId=E2E_LOGIN、currentPassword=[REDACTED] | 200、`partyId=E2E_LOGIN`、`userLoginId=e2e-hanako`、`currentPassword="[REDACTED]"` | 一致（Swagger範囲。保存ハッシュ実体は対象外） |
| PARTY-E2E-11 | 作成・検索200、contactMechId採番、検索にE2E_LOGIN | 両方200、contactMechId=10000、検索結果partyId=E2E_LOGIN | 一致 |
| PARTY-E2E-12 | 作成200・partyId採番。expected.verifyのGETでstatusId=PARTY_ENABLED | 作成200・partyId=10000。直後のGET Party?partyId=10000が200・1行、partyTypeId=PARTY_GROUP、statusId=PARTY_ENABLED | 一致 |
| PARTY-E2E-13 | 作成200・partyId採番。expected.verifyのGETでstatusId=PARTY_ENABLED | 作成200・partyId=10001。直後のGET Party?partyId=10001が200・1行、partyTypeId=PERSON、statusId=PARTY_ENABLED | 一致 |
| PARTY-E2E-14 | 組織・人物の役割作成2回とも200 | 両方200。10000/INTERNAL_ORGANIZATIO、10001/EMPLOYEE | 一致 |
| PARTY-E2E-15 | 関係作成2回とも200、2件目両役割_NA_ | 両方200。EMPLOYMENT、CONTACT_REL。CONTACT_RELのroleTypeIdFrom/Toとも_NA_ | 一致 |
| PARTY-E2E-16 | 住所・電話・メール作成200、各contactMechId採番 | 3回とも200、IDは順に10001・10002・10003 | 一致 |
| PARTY-E2E-17 | 3取得200、住所1-2-3 Marunouchi、電話12345678、メールtaro@example.test | 3回とも200、address1・contactNumber・infoStringが期待値と一致 | 一致 |
| PARTY-E2E-18 | CONTACT_REL応答のfromDateで削除200、再取得200、EMPLOYMENTのみ1件 | 無加工のfromDateで削除200。直後の一覧取得200、配列長1・partyRelationshipTypeId=EMPLOYMENT | 一致 |
| PARTY-E2E-19 | multi409、errors[0].errorMessage=ServiceValueFound、再取得[] | 409、errors[0]={index:1,errorMessage:ServiceValueFound}。再取得200 `[]` | 一致 |

HTTPステータスはSwaggerの実レスポンス欄の表示値と照合した。各ケースの保存済み要求・応答を更新済みcases.jsonと突き合わせ、12/13のverify、18のfromDate同一性・配列長・関係種別、19の空配列を再確認した。照合結果は `comparison.json` に保存した。

## 重点確認の要求・応答

以下はすべてSwagger UIから実行した。認証ヘッダーは記載しない。抜粋にはその旨を明示した。今回、期待値との不一致はない。

### PARTY-E2E-05 補足

ケース順を維持した初回GETは200 `[]`。19ケース後、E2E-10で作成したUserLoginをsensitiveで取得した。

`GET /api/party/entities/UserLogin?userLoginId=e2e-hanako` →200、配列長1。主要値の抜粋:

```json
[{"userLoginId":"e2e-hanako","partyId":"E2E_LOGIN","currentPassword":"[REDACTED]"}]
```

### PARTY-E2E-12 / PARTY-E2E-13

12: adminで `POST /api/party/services/createPartyGroup`、入力 `{"groupName":"E2E 商事"}`。

HTTP 200、応答全文:

```json
{"partyId":"10000","responseMessage":"success"}
```

**同ケース内ですぐに** `GET /api/party/entities/Party?partyId=10000` →200・1行。主要値は `partyId=10000`、`partyTypeId=PARTY_GROUP`、`statusId=PARTY_ENABLED`。

13: adminで `POST /api/party/services/createPerson`、入力 `{"firstName":"Taro","lastName":"Suzuki"}`。

HTTP 200、応答全文:

```json
{"partyId":"10001","responseMessage":"success"}
```

**同ケース内ですぐに** `GET /api/party/entities/Party?partyId=10001` →200・1行。主要値は `partyId=10001`、`partyTypeId=PERSON`、`statusId=PARTY_ENABLED`。

作成応答自体にはstatusIdはなく、更新済みcases.jsonどおりGETの保存値を判定対象とした。

### PARTY-E2E-18

15の2件目CONTACT_REL作成応答の `fromDate` は `2026-10-26T11:51:39.410+00:00`。文字列を再構成・丸め・タイムゾーン変換せず使用した。

adminで `POST /api/party/services/deletePartyRelationship`、要求全文:

```json
{
  "partyIdFrom": "10000",
  "partyIdTo": "10001",
  "partyRelationshipTypeId": "CONTACT_REL",
  "fromDate": "2026-10-26T11:51:39.410+00:00"
}
```

HTTP 200。応答は削除されたCONTACT_RELであり、主要値の抜粋:

```json
{
  "partyIdFrom": "10000",
  "partyIdTo": "10001",
  "partyRelationshipTypeId": "CONTACT_REL",
  "fromDate": "2026-10-26T11:51:39.410+00:00",
  "roleTypeIdFrom": "_NA_",
  "roleTypeIdTo": "_NA_",
  "responseMessage": "success"
}
```

直後に `GET /api/party/entities/PartyRelationship?partyIdFrom=10000&partyIdTo=10001` →200、配列長1。主要値の抜粋:

```json
[
  {
    "partyIdFrom": "10000",
    "partyIdTo": "10001",
    "partyRelationshipTypeId": "EMPLOYMENT",
    "fromDate": "2026-10-26T11:51:38.709+00:00",
    "roleTypeIdFrom": "INTERNAL_ORGANIZATIO",
    "roleTypeIdTo": "EMPLOYEE"
  }
]
```

### PARTY-E2E-19

adminで `POST /api/party/services/createPartyRole/multi`、要求全文:

```json
[{"partyId":"10000","roleTypeId":"CUSTOMER"},{"partyId":"10000","roleTypeId":"CUSTOMER"}]
```

HTTP 409、応答全文:

```json
{"responseMessage":"error","errorMessage":"One or more service calls failed","errors":[{"index":1,"errorMessage":"ServiceValueFound"}]}
```

直後に `GET /api/party/entities/PartyRole?partyId=10000&roleTypeId=CUSTOMER` →200、応答全文 `[]`。1件目も保存されていない。

## 問題・備考

- 前回runではPARTY-E2E-18が削除404・2件残存で不一致だったが、修正コミット `c173f5aa6a` を含むHEAD `da504ff140` で今回は削除200・EMPLOYMENTのみ1件を確認した。
- **E2E-10の保存ハッシュ実体はSwaggerから検証不能。** 作成応答・実在行の取得では伏せ字を確認した。保存ハッシュは別途JUnit `MainFlowTest` で検証済み（依頼者の確認に基づく。本Swagger再実行で保存層を直接参照したものではない）。
- 05の初回は空配列なので、それだけで伏せ字を証明したとは扱わず、10の作成後の補足取得を含めて一致とした。
- 01ではブラウザのネイティブBasic認証ダイアログを取消して401を観測した。今回は要求待機のタイムアウトはなく、同一要求の実応答を保存した。
- 録画前の画面サイズ設定でCDP対象をページへ修正し、1440×1000の全体表示を確認してから録画した。ケース実行への影響はない。
- 録画ツールの初期字幕出力に日本語の欠字を検出したため、同一原記録・同一編集タイムラインのまま、字幕フォントをNoto Sans CJK JPへ変更してMP4/WebPを再出力した。最終版では日本語字幕・ケースバナー・重点ケースの実応答を再確認した。追加の録画やケースの再実行は行っていない。
- 今回の範囲で未実行ケース・期待値不一致はない。保存ハッシュ実体、本番PostgreSQL、未移植サービス、ケース定義外の入力・権限組合せは本Swagger E2Eでは検証していない。

## 画面証跡（2列）

### 未認証・権限

| 未認証401 | PARTYMGR権限なし403 |
|---|---|
| ![PARTY-E2E-01 未認証401](https://devin-gtm.devinenterprise.com/attachments/87bf3dcb-b80e-4521-a9b4-4feb62ebdfc1/PARTY-E2E-01-01.png) | ![PARTY-E2E-02 権限なし403](https://devin-gtm.devinenterprise.com/attachments/f80c02a2-f246-4dd0-bbf5-cff15834edd9/PARTY-E2E-02-01.png) |

### 機密閲覧

| readonlyの403 | sensitiveの200・実在行伏せ字 |
|---|---|
| ![PARTY-E2E-04 機密閲覧403](https://devin-gtm.devinenterprise.com/attachments/9966f243-6fe4-424c-bd1e-74d3e1597310/PARTY-E2E-04-01.png) | ![PARTY-E2E-05 補足取得](https://devin-gtm.devinenterprise.com/attachments/9e72657b-a95a-478e-9efc-c2e105c48302/PARTY-E2E-05-SUP-01.png) |

### 組織作成と同ケース内のverify

| 組織作成応答 | 直後のGET・PARTY_ENABLED |
|---|---|
| ![PARTY-E2E-12 作成](https://devin-gtm.devinenterprise.com/attachments/12f06684-86be-4463-a7b9-19c45dd7bb93/PARTY-E2E-12-01.png) | ![PARTY-E2E-12 verify](https://devin-gtm.devinenterprise.com/attachments/0ef11e8e-89e8-48c2-8a1c-5617a3ba38e1/PARTY-E2E-12-02.png) |

### 人物作成と同ケース内のverify

| 人物作成応答 | 直後のGET・PARTY_ENABLED |
|---|---|
| ![PARTY-E2E-13 作成](https://devin-gtm.devinenterprise.com/attachments/b49a85b0-67b4-4f73-9110-76b3881b04e4/PARTY-E2E-13-01.png) | ![PARTY-E2E-13 verify](https://devin-gtm.devinenterprise.com/attachments/4b47ef13-d798-4906-9dab-88de3e6b348d/PARTY-E2E-13-02.png) |

### 主要フローの取得

| 住所取得 | メール取得 |
|---|---|
| ![PARTY-E2E-17 住所](https://devin-gtm.devinenterprise.com/attachments/f3ce833d-647d-4f80-b430-9ba0112a0475/PARTY-E2E-17-01.png) | ![PARTY-E2E-17 メール](https://devin-gtm.devinenterprise.com/attachments/80f1b014-7e34-4773-b270-61288d608ce1/PARTY-E2E-17-03.png) |

### 関係削除の修正確認

| CONTACT_RELの削除応答 | 削除直後の取得・EMPLOYMENTのみ1件 |
|---|---|
| ![PARTY-E2E-18 削除](https://devin-gtm.devinenterprise.com/attachments/aa7edfb7-5a14-49b6-b504-9e7e43acd15e/PARTY-E2E-18-01.png) | ![PARTY-E2E-18 残件1](https://devin-gtm.devinenterprise.com/attachments/1fb8ef84-8681-49e3-ad80-a8a2abf4243f/PARTY-E2E-18-02.png) |

### multiのロールバック

| multi409・重複エラー | 直後の再取得・空配列 |
|---|---|
| ![PARTY-E2E-19 multi409](https://devin-gtm.devinenterprise.com/attachments/61ffb324-87e0-4f3a-8b1f-d1fc1a6ac5b0/PARTY-E2E-19-01.png) | ![PARTY-E2E-19 空配列](https://devin-gtm.devinenterprise.com/attachments/95e9379a-a7b6-4d6f-8ffd-15088546a354/PARTY-E2E-19-02.png) |

## 成果物

- MP4: `/home/ubuntu/screencasts/party-e2e-da504ff140/party-e2e-da504ff140-edited.mp4`
- WebP: `/home/ubuntu/screencasts/party-e2e-da504ff140/party-e2e-da504ff140.webp`
- 全33枚のケーススクリーンショット: `/tmp/party-e2e-da504ff140/PARTY-E2E-*.png`
- 伏せ字処理済み要求・実応答・HTTPステータス: `/tmp/party-e2e-da504ff140/results.json`
- ケース照合結果: `/tmp/party-e2e-da504ff140/comparison.json`
- 実行計画: `/tmp/party-e2e-plan.md`
- [Devinセッション](https://devin-gtm.devinenterprise.com/sessions/661bad600cad4320babbb60258157a83)

録画を確認し、ケースバナー、12/13のverify、18の200とEMPLOYMENTのみ、19の409と空配列、最後のUserLogin伏せ字が見えることを確認した。パスワードやAuthorizationヘッダーの露出は確認されなかった。上記ローカルパスの成果物は配布時に添付すること。
