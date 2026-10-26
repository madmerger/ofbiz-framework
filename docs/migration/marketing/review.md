# marketing Spring Boot 移行 レビュー / テスト結果

## 1. 実行結果

| 検証 | コマンド | 結果 |
| --- | --- | --- |
| モジュールの全テスト | `./gradlew :migration:marketing-springboot:test` | **170 件 pass / 0 fail / 0 skip**（EntityAutoAcceptanceTest 151、MarketingServicesTest 10、MarketingSecurityTest 6、HttpsAndDocsProfileTest 2、PostgresSmokeTest 1） |
| PostgreSQL（Testcontainers 1.21.4） | 上に含まれる | 1 件 pass |
| 静的解析 | `./gradlew checkstyleMain checkstyleTest codenarcMain codenarcTest` | BUILD SUCCESSFUL（ルートプロジェクトが対象。移行モジュールは humanres と同じく対象外） |
| E2E（Swagger UI） | `docs/migration/marketing/cases.json` を testing agent で実行 | `e2e-report.md` を参照 |

## 2. 件数

| 区分 | 件数 |
| --- | ---: |
| entity-auto（全量登録・全件実行テスト） | 73 |
| simple（Java に手作業で移植） | 27 |
| 移植した java / groovy（主要フロー） | 2（`signUpForContactList`, `deleteContactListParty`） |
| 未移植の java / groovy サービス | 8（interface 1 を含む） |
| 未移植のその他（画面スクリプト、イベント、ECA、view-entity、画面） | `unported.md` §1 |

## 3. 受け入れ基準の判定（spec §4）

AC-01〜AC-15 はすべて PASS（対応テストは spec §4）。

## 4. OFBiz との差分

| # | 差分 | 理由 / 影響 |
| --- | --- | --- |
| D-01 | ECA のメール送信（async, persist）を同一トランザクション内の同期呼び出しにした | MailPort スタブで失敗しない。実 SMTP の adapter を作るときはトランザクション確定後の非同期送信にする |
| D-02 | `updateContactListPartyNoUserLogin` も認証と `MARKETING_UPDATE` が必要 | OFBiz はメールのリンクから未ログインで呼べる。公開エンドポイントは確認コード検証付きで別途設計する |
| D-03 | `sendContactListParty*Email` を直接呼ぶには `MARKETING_UPDATE` が必要 | 閲覧専用ユーザーによるメール送信を防ぐ |
| D-04 | `createContact` は `contactListId` を受け取らず、ECA による `createContactListParty` を行わない | 購読は `signUpForContactList` / `createContactListParty` を直接呼ぶ |
| D-05 | `createSalesForecastDetail` の `salesForecastDetailId` を marketing 側でサブシーケンス採番 | 共通基盤の採番条件（`*SeqId`）に合わないため |
| D-06 | 非推奨エイリアス 8 件は実装先のパラメータを継承 | OFBiz の `implements` と同じ |
| D-07 | ContactListPartyStatus を機密エンティティにした | 確認コードの漏えい防止 |
| D-08 | `updateContactListPartyNoUserLogin` は、購読確認（PENDING→ACCEPTED/REJECTED）だけを受け付け、最新の PENDING の確認コードと照合する。解除の確定は認証付きの `updateContactListParty` で行う | OFBiz は履歴に一致するコードがあれば任意の statusId を受け付けるため、古いコードで解除済みの購読を再承認したり、購読時のコードで解除を確定したりできた（Devin Review の指摘で強化） |
| D-09 | `createContact` で Person に書き込めるのは、party の `person` マップ処理（PartyMapProcs.xml）が許可する属性だけ | OFBiz の map-processor と同じ制限 |
| D-10 | メールスタブは直近 100 件だけを保持する | 長時間起動したときのメモリ増加を防ぐ |

## 5. 既知の制約

- 同じメールで再度 `signUpForContactList` すると、OFBiz と同様に新しい ContactMech が作られ、購読済み判定に掛からず新しい PENDING ができる（OFBiz の既存挙動を維持）。
- ECA の `updateContactListCommStatus`（COM_BOUNCED）→ `updateContactListParty` は未移植（`unported.md` §4）。
- party / communication event / order return / product store は local adapter（同名テーブルを直接操作）。全体移行時に差し替えが必要。
- Widget / FTL / レポートは未移植。UI は別途再構築。
