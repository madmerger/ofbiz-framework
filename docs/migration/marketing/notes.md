# marketing 移行ログ（notes）

## 1. 変換判断

| # | 判断 | 根拠 |
| --- | --- | --- |
| N-01 | 新モジュール `migration/marketing-springboot`。`settings.gradle` は `migration/*` を自動 include するので無変更 | 共通基盤の規約 |
| N-02 | entity-auto 73 件は共通の `EntityAutoService` と、marketing の servicedef だけから生成した `ServiceCatalog` で動かす（`build.gradle` の生成タスク） | humanres と同じ方式。カタログと inventory を機械的に突き合わせられる |
| N-03 | 生成カタログの非推奨エイリアス（`implements` だけを持ち、パラメータが空の 8 件。例 `addPriceRuleToMarketingCampaign`）は、実装先サービスのパラメータを継承して登録 | OFBiz の `implements` は実装先の属性を取り込む。共通基盤を変えずに `MarketingCatalogRegistrar` で解決 |
| N-04 | `createSalesForecastDetail` の主キー `salesForecastDetailId` は OUT だが `*SeqId` 名ではないため、共通の採番の対象外。marketing 側でサブシーケンス採番するハンドラを登録 | OFBiz の entity-auto は複合主キーの欠けた 1 列をサブシーケンス採番する。共通基盤の変更を避けた |
| N-05 | 他コンポーネント（party / communication event / order return / product store / メール）は Port に分離し、local adapter で同名テーブルを読み書き | humanres の `LocalPartyAdapter` 方式。全体移行時に差し替え |
| N-06 | メール送信は `MailPort` スタブ。ECA（状態別メール）は非同期 persist ではなく、同一トランザクション内の同期呼び出し | スタブなので送信失敗がなく、テストで送信内容を検証できる |
| N-07 | `ContactListPartyStatus` を機密エンティティにした | 購読確認コード `optInVerifyCode` を持ち、漏れると第三者が購読を確定できる |
| N-08 | `sendContactListParty*Email` は UPDATE 権限が必要 | 閲覧専用ユーザーがメールを送信できないようにする（OFBiz は内部呼び出し前提） |
| N-09 | Entity API に必要な、カタログ外のエンティティ 8 件を `migration.security.extra-entities` に列挙（ContactListParty / ContactListPartyStatus / ContactListCommStatus / WebSiteContactList / TrackingCodeVisit / SalesForecast / SalesForecastHistory / SalesOpportunityHistory） | 共通基盤の更新で Entity API がカタログの参照範囲に限定されたため |
| N-10 | Widget / FTL / レポートは移植しない（`unported.md`） | humanres と同じ |

## 2. 共通基盤の更新への追従

| ベース | 対応 |
| --- | --- |
| `d6254e03b3` | `MarketingCatalogRegistrar` を `registerCatalog` の直接登録に変更。`migration.security.component: marketing`、`extra-entities`、local / test で `require-https: false` |
| `3ff99279eb` | 空の INOUT 主キーの自動採番を ContactList などで確認 |
| `23f8c75e65` | OUT の主キー（`createMarketingCampaign` の `marketingCampaignId`、`createSegmentGroup` の `segmentGroupId`）は入力値を無視して採番。テストは応答の ID を使うよう変更。multi のロールバックテストは呼び出し側が主キーを決める `createContactListType` に変更 |

## 3. ハマりどころ

- テスト用ユーザーを独自の `UserDetailsService` で定義すると、共通の SecurityFilterChain が使わず全部 401 になる。`src/test/resources/application-test.yml` の `migration.security.users` で定義する。
- Testcontainers の PostgreSQL コンテキストで、既定ヒープだとコンテキスト読み込み中に OOM。test タスクの `maxHeapSize = "3g"`。
- `UserLoginSecurityQuestion` は datamodel に存在しないため機密一覧に入れると起動に失敗する。
- OFBiz の `signUpForContactList` は申込のたびに新しい ContactMech を作るため、同じメールで再申込しても「既に購読済み」の判定に掛からない（既存の挙動を維持し、`review.md` に記録）。
- 既定のチェックスタイル（ルートの `checkstyleMain` など）はルートプロジェクトのソースだけが対象。移行モジュールは humanres と同じく対象外。
