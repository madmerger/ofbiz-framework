# marketing Spring Boot 移行 E2E 結果

## 対象と実行方法

- PR: https://github.com/madmerger/ofbiz-framework/pull/4
- 対象 HEAD: `c4b58338e27d2b5b829c60fc5f63bf42bba931c8`
- 実行日: 2026-10-26（実行環境の応答日時、UTC）
- `migration/marketing-springboot` の `local` プロファイルのみを起動。Java 17、Spring Boot 3.5.7、新規インメモリ H2 を使用。旧 OFBiz は起動していない。
- http://localhost:8080/swagger-ui/index.html で、`cases.json` の20件を記載順に実行した。ユーザー切替はSwaggerのAuthorizeを使用し、01は未認証。すべてのAPI操作はSwaggerのTry it out／Executeから実行した。
- 画面操作の自動化は入力・クリック・実応答の採取に使用した。APIの直接呼出し、応答のモック、実装変更はしていない。
- 各ケースの番号・名称・リクエスト・ユーザーを画面上部に表示。パスワード欄と認証情報を含むcurl表示だけを非表示にした。未認証時のChrome標準認証ダイアログは認証チャレンジをキャンセルして回避し、実際の401を表示した。
- データ引継ぎ: `{campaignId}=10000`、`{flowCampaignId}=10001`、`{fromDate}=2026-10-26T11:07:31.633+00:00`。確認コードは15のPENDING行から16へ引き継いだが、値は画面確認のみとし本文には記載しない。

## 結果

**20件実行、20件PASS、0件FAIL、0件未検証。** 指定ケースの期待値との不一致はなかった。

| ID | ケース名 | 期待HTTP・主要値 | 実測HTTP・主要値 | 判定 |
|---|---|---|---|---|
| MARKETING-E2E-01 | 未認証は401 | 401 | 401、`Unauthorized` | PASS |
| MARKETING-E2E-02 | MARKETING権限なしユーザー | 403 | nomarketingで403、`Forbidden` | PASS |
| MARKETING-E2E-03 | 閲覧専用ユーザーの作成 | 403 | readonlyで403、`Service permission denied` | PASS |
| MARKETING-E2E-04 | 閲覧専用ユーザーの機密Entity閲覧 | 403 | readonlyで403、`Sensitive entity access denied` | PASS |
| MARKETING-E2E-05 | 機密Entityの許可された閲覧 | 200、JSON配列 | sensitiveで200、`[]` | PASS |
| MARKETING-E2E-06 | entity-auto 作成 createMarketingCampaign | 200、`responseMessage=success`、採番ID | 200、`responseMessage=success`、`marketingCampaignId=10000` | PASS |
| MARKETING-E2E-07 | entity-auto 取得 MarketingCampaign | 200、`campaignName=E2E 秋キャンペーン` | 200、ID=10000、`campaignName=E2E 秋キャンペーン` | PASS |
| MARKETING-E2E-08 | entity-auto 更新 updateMarketingCampaign | 200、再取得で`campaignName=E2E 冬キャンペーン` | 更新200、one再取得も200・`campaignName=E2E 冬キャンペーン` | PASS |
| MARKETING-E2E-09 | entity-auto 削除 deleteMarketingCampaign | 200、同IDの一覧再取得で`[]` | 削除200、ID=10000で一覧再取得200・`[]` | PASS |
| MARKETING-E2E-10 | simple createWebSiteContactList | 200、fromDate自動設定 | 200、`fromDate=2026-10-26T11:06:48.830+00:00` | PASS |
| MARKETING-E2E-11 | simple arePartiesRelatedThroughSegmentGroup | 200、`areRelated=N` | 200、`areRelated=N` | PASS |
| MARKETING-E2E-12 | 主要フロー1 キャンペーン作成 | 200、採番ID | 200、`marketingCampaignId=10001` | PASS |
| MARKETING-E2E-13 | 主要フロー2 連絡先リスト作成 | 200、`contactListId=E2E_CL` | 200、`contactListId=E2E_CL`、`marketingCampaignId=10001` | PASS |
| MARKETING-E2E-14 | 主要フロー3 購読申込 signUpForContactList | 200、`statusId=CLPT_PENDING`、`partyId=_NA_`、fromDate発行 | 200、`statusId=CLPT_PENDING`、`partyId=_NA_`、`fromDate=2026-10-26T11:07:31.633+00:00` | PASS |
| MARKETING-E2E-15 | 主要フロー4 確認コード取得（機密Entity） | 200、PENDING行に確認コード | sensitiveで200、`statusId=CLPT_PENDING`、空でないコードを画面上で確認 | PASS |
| MARKETING-E2E-16 | 主要フロー5 購読確定 updateContactListPartyNoUserLogin | 200、`statusId=CLPT_ACCEPTED` | 200、`statusId=CLPT_ACCEPTED` | PASS |
| MARKETING-E2E-17 | 主要フロー6 解除申請 unsubscribeContactListParty | 200、`statusId=CLPT_UNSUBS_PENDING` | 200、`statusId=CLPT_UNSUBS_PENDING` | PASS |
| MARKETING-E2E-18 | 主要フロー7 解除確定 updateContactListParty | 200、`statusId=CLPT_UNSUBSCRIBED`、状態履歴4件 | 更新200・`statusId=CLPT_UNSUBSCRIBED`。sensitiveで履歴再取得200、`CLPT_PENDING → CLPT_ACCEPTED → CLPT_UNSUBS_PENDING → CLPT_UNSUBSCRIBED`の4件 | PASS |
| MARKETING-E2E-19 | multiで1件重複すると409 | 409、`rolledBack=true`、`errors[0].index=2` | 409、`rolledBack=true`、`errors[0].index=2`、`errorMessage=ServiceValueFound` | PASS |
| MARKETING-E2E-20 | multi失敗後の全件ロールバック | 200、A/B両方`[]` | readonlyで`E2E_MULTI_A`を取得200・`[]`、続けて`E2E_MULTI_B`も200・`[]` | PASS |

## 証跡

正式な録画は標準録画機能による1本、1600×900、約50.33秒。ビルドログ・依存インストール・パスワード・Authorization値は含まれていない。WebPは同じ録画を5fpsで変換したもの。

- [正式録画（MP4）](https://devin-gtm.devinenterprise.com/attachments/90ee6eb6-1de5-4320-a582-88aa6b57e6e7/rec-cc4d20e3-37aa-4bc2-ad5b-b42b6d0216b3-edited.mp4)
- [同録画のWebP版](https://devin-gtm.devinenterprise.com/attachments/0253e27e-ecf3-44dc-9960-8ec44466c043/marketing-e2e.webp)

| 未認証401（01） | 機密Entity拒否403（04） |
|---|---|
| ![01 未認証401](https://devin-gtm.devinenterprise.com/attachments/c3e43ea8-15b6-414f-9684-b4a92e78e0e8/MARKETING-E2E-01.png) | ![04 機密Entity拒否403](https://devin-gtm.devinenterprise.com/attachments/d9f2eef5-dbc4-48bc-9674-fbffa4284f32/MARKETING-E2E-04.png) |

| 機密Entity許可200（05） | 主要フロー・購読申込（14） |
|---|---|
| ![05 機密Entity許可200](https://devin-gtm.devinenterprise.com/attachments/331ae131-0ade-4ec5-9d2c-38f55b851162/MARKETING-E2E-05.png) | ![14 購読申込](https://devin-gtm.devinenterprise.com/attachments/b9d1747e-6fba-4a5f-862a-582321424683/MARKETING-E2E-14.png) |

| 主要フロー・解除完了（18） | multi重複409（19） |
|---|---|
| ![18 解除完了](https://devin-gtm.devinenterprise.com/attachments/0c738663-29bd-4ba1-84f9-a7a8e7874868/MARKETING-E2E-18-service.png) | ![19 multi重複409](https://devin-gtm.devinenterprise.com/attachments/85ba0ccc-7526-4721-9223-a1de600cf61e/MARKETING-E2E-19.png) |

実行セッション: https://devin-gtm.devinenterprise.com/sessions/98a99ec8a80b4e9cb04cba9cce1d2625

## 制約・実行上の問題

- 初回の操作準備中にChrome標準認証ダイアログとTry it outの有効化待ちが発生した。認証チャレンジのキャンセルとパネル有効化後、正式な記録では01から順番に全20件を完了した。準備時の試行録画は正式成果物に含めない。
- この結果が対象とするのは指定20ケースとlocal/H2上の挙動。旧OFBiz比較、本番DB、外部メール配信、全サービスの網羅テストは対象外。メールはlocalアダプターのスタブであり、実配信を検証したものではない。
- 実装には変更を加えていない。本ファイルは未コミット。
