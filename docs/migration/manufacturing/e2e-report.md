# Manufacturing E2E 実施報告

`local` profile / H2 で `cases.json` の全リクエストを実行した。パスワードは環境変数からのみ渡した。

| ケース ID | 名称 | 操作 | 期待値 | 実測値 | 判定 |
| --- | --- | --- | --- | --- | --- |
| MANUFACTURING-E2E-01 | 未認証 API | 認証情報なしで Product を GET | 401 | 401 | 成功 |
| MANUFACTURING-E2E-02 | 権限不足 | ofbtoolsonly で Manufacturing API を GET | 403 | 403 | 成功 |
| MANUFACTURING-E2E-03 | CostComponent 機密保護 | viewer / writer で GET | 403 / 200 | 403 / 200 | 成功 |
| MANUFACTURING-E2E-04 | カレンダー CRUD | 作成、取得、更新、例外日作成・削除 | 更新値反映、例外日 0 件 | `description=updated`、例外日 0 件 | 成功 |
| MANUFACTURING-E2E-05 | BOM 自己参照と線形式 | 自己参照 create、linearComponentFormula | 400、quantity=4 | 400、quantity=4 | 成功 |
| MANUFACTURING-E2E-06 | BOM から生産完了・close | BOM 2件、生産 run、2 task の開始・数量更新・完了・close | header と task が PRUN_CLOSED、各 quantityProduced=2 | header と task が PRUN_CLOSED、各 quantityProduced=2 | 成功 |
| MANUFACTURING-E2E-07 | multi rollback | 2行目重複の multi create、calendar を検索 | 409、作成行 0 件 | 409、0 件 | 成功 |

cases.json の 29 リクエストは 29/29 成功。Swagger UI `/swagger-ui/index.html` は HTTP 200。検証後に local サーバーを停止した。
