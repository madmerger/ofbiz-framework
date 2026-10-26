# OFBiz → Spring Boot 全体移行レポート

## 1. 概要

- **方針（ユーザーの決定事項）**
  - 全コンポーネントで、エンティティ・自動 CRUD（entity-auto）・simple サービスは全量を移植した。
  - java/groovy サービスは主要な業務フローに絞って移植し、残りは未移植一覧に記録した。
- **セキュリティ強化**
  - 機密エンティティは、読むのに専用の権限が必要。
  - 本番プロファイルでは HTTPS が必須。
  - 組織の削除は、関係の種類（GROUP_ROLLUP）で絞る。
- **既存の `applications/` と `framework/` は変更していない。** 新しい実装は `migration/<component>-springboot` に並べて置いている。
- **共通基盤**（[PR #2](https://github.com/madmerger/ofbiz-framework/pull/2)、[PR #12](https://github.com/madmerger/ofbiz-framework/pull/12)）
  - JPA エンティティ 835本をビルド時に自動生成する。view-entity の 270本は対象外とした。
  - entity-auto サービス 1,859本を共通の仕組みで実行する。
  - 採番、権限、HTTPS、機密データの伏せ字、コンポーネント境界の制御も共通基盤で行う。

## 2. コンポーネント別の結果

| コンポーネント | PR | entity-auto | simple | 移植した java/groovy | JUnit | E2E |
|---|---|---:|---:|---:|---:|---:|
| humanres（パイロット） | [#1](https://github.com/madmerger/ofbiz-framework/pull/1) | 全量 | 9/9 | - | 162 | 合格 |
| content | [#3](https://github.com/madmerger/ofbiz-framework/pull/3) | 176/176 | 7/7 | 11 | 201 | 26/26 |
| marketing | [#4](https://github.com/madmerger/ofbiz-framework/pull/4) | 73/73 | 27/27 | 2 | 170 | 20/20 |
| order | [#5](https://github.com/madmerger/ofbiz-framework/pull/5) | 220/220 | 19/19 | 5 | 52 | 20/20 |
| party | [#6](https://github.com/madmerger/ofbiz-framework/pull/6) | 128/128 | 26/26 | 19 + group 1 | 148 | 19/19 |
| commonext | [#7](https://github.com/madmerger/ofbiz-framework/pull/7) | 0/0 | 0/0 | 6/6 | 39 | 16/16 |
| manufacturing | [#8](https://github.com/madmerger/ofbiz-framework/pull/8) | 18/18 | 8/8 | 7 | 49 | 29/29 |
| product | [#9](https://github.com/madmerger/ofbiz-framework/pull/9) | 460/460 | 77/77（うち一部移植 5） | 6 | 1,609 | 18/18 |
| workeffort | [#10](https://github.com/madmerger/ofbiz-framework/pull/10) | 95/95 | 33/33 | 11 | 171 | 8/8 |
| accounting | [#11](https://github.com/madmerger/ofbiz-framework/pull/11) | 386/386 | 93/93 | 8 | 624 | 23/23 |

- 全 PR で、最新の commit に対する Devin Review の指摘は 0 件である。
  - workeffort だけは、指摘 8 件のうち 1 件が判断待ちとして残っている（§4）。
- 各コンポーネントの E2E は、Swagger UI から local プロファイル（H2）で実行し、録画した。
  - 詳細は各コンポーネントの `docs/migration/<component>/e2e-report.md` と `cases.json` にある。

## 3. コンポーネントをまたぐ E2E

- **方式**
  - order（8081）、product（8082）、accounting（8083）を別々のプロセスで起動した。
  - 3つとも `integration` プロファイルで、1つの PostgreSQL を共有している。
  - 各コンポーネントの本物の実装を、それぞれの Swagger UI から呼んだ。order 内の簡易な代替実装は使っていない。
- **結果:** `docs/migration/integration/cases.json` の 15/15 件が期待値と一致した。詳細は `docs/migration/integration/e2e-report.md` にある。
  - 01〜02: 未認証は 401、readonly ユーザーの作成は 403
  - 03〜04: order で受注を作成し（合計 50）、承認した（ORDER_APPROVED）
  - 05〜08: product で入荷 → 引当 → 一括出荷を行い、同じ受注の出荷が SHIPMENT_SHIPPED になった
  - 09〜10: accounting で同じ受注から請求書を作成し、READY にした。売上仕訳（SALES_INVOICE）が自動作成され、借方 50 と貸方 50 が一致した
  - 11〜13: 入金 50 を作成して消し込み、請求書が INVOICE_PAID、未消込額が 0 になった
  - 14〜15: order で再取得すると、明細は ITEM_COMPLETED、ヘッダーは ORDER_APPROVED のままだった（ECA が未移植のため。§4 の 10）
- **統合のときに見つけて直した不具合**
  - product の入荷が、PostgreSQL だけで 500 になっていた。原因は nullable な日付を JPQL にそのまま渡していたこと（SQLState 42P18）。H2 では起きないため、各コンポーネントの E2E では見つからなかった。[PR #9](https://github.com/madmerger/ofbiz-framework/pull/9) で修正し、PostgreSQL での回帰テストを追加した。
  - 共通基盤の Gradle タスクの入力の宣言を修正した（[PR #12](https://github.com/madmerger/ofbiz-framework/pull/12)）。
- **統合ブランチの全モジュールテスト:** すべて成功した（1,637 件。product 統合後の product は 1,609 件）。

## 4. 既知の制約・判断待ち

| # | コンポーネント | 内容 | 現在の扱い |
|---|---|---|---|
| 1 | content | アンケート回答の partyId を、ログイン中のユーザーと突き合わせていない | party の取り込み後に対応する |
| 2 | order | 所有者・担当者による制限（ORDERMGR_ROLE）が未移植 | party の取り込み後に対応する |
| 3 | accounting | 組織・ロール単位の権限（ACCOUNTING_ROLE_*）が未移植 | 後で対応する |
| 4 | 共通基盤 | `*_ROLE_*` の権限だけを持つユーザーが 403 になる（workeffort） | 後で対応する |
| 5 | workeffort | 既存の請求書に作業時間を足すと、請求書の通貨が上書きされる | OFBiz と同じ挙動のまま |
| 6 | commonext | 削除できるノートを SYSTEMNOTE に限定した | OFBiz より厳しくしたまま |
| 7 | product | 同じ親の下に同時に登録すると枝番が衝突する | OFBiz と同じ挙動（409）のまま |
| 8 | 全体 | PostgreSQL の実データでの新旧比較、本番 HTTPS／プロキシ配下での確認 | 未実施 |
| 9 | 全体 | 画面（Widget/FTL）、ECA、view-entity | 未移植（各 `unported.md` に記載） |
| 10 | order | 明細がすべて完了しても、受注ヘッダーが ORDER_COMPLETED にならない（ECA が未移植） | 未対応 |
| 11 | product | 出荷すると、未移植の WorkEffort への通知が 501 として応答に含まれる（出荷そのものは成功する） | 未対応 |
| 12 | accounting | 自動作成した仕訳は未転記（isPosted=N）。試算表 API は acct-clerk の権限では 403 | 転記と試算表は未確認 |
