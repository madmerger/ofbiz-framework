# WorkEffort E2E 検証予定

実測値はテスト実行時に記入する。秘密値・Authorization headerは記録しない。

| Case | シナリオ | 期待値 | 実測値 |
|---|---|---|---|
| WORKEFFORT-E2E-01 | 未認証 request | 401 | |
| WORKEFFORT-E2E-02 | base/action permission 不足 | 403 | |
| WORKEFFORT-E2E-03 | RateAmount readonly | 403 | |
| WORKEFFORT-E2E-04 | RateAmount auditor | 200 | |
| WORKEFFORT-E2E-05 | Entity-auto CRUD | create/read/update/delete が成功し、最後に対象なし | |
| WORKEFFORT-E2E-06 | Simple service 代表 | 成功response、Side effectを確認 | |
| WORKEFFORT-E2E-07 | 主要業務 flow: 作成→担当割当→作業時間記録→完了 | 各APIが成功し、最後の状態が完了 | |
| WORKEFFORT-E2E-08 | multi conflict と rollback | HTTP 409、先行行も永続化されない | |
