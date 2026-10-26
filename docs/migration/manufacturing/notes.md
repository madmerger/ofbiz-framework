# Manufacturing 移行メモ

- `addProductManufacturingRule` の ruleId が空の場合、旧 minilang の採番先を確認し `SequenceService.nextId("FinAccount")` を維持した。自然な `ProductManufacturingRule` 系列に置き換えていない。
- common の `MigrationServiceDispatcher.executeMulti` は行ごとのエラー詳細に HTTP status を持たず、multi 失敗時は通常400となる。共通基盤は変更せず、Manufacturing の高優先度 advice で `ServiceValueFound` を含む行を status 409 にし、レスポンスも409とする。
- 生産 run の推定時間は task の setupMillis + taskMillis × 生産数量で積算する。稼働カレンダー・休業日は考慮しない。
- `searchDuplicatedAncestor` は候補部品以下の BOM ツリーをキーにし、候補製品の祖先を有効期間内の ProductAssoc で探索する。訪問済み集合で既存の循環も防ぎ、同じ関連の別 `fromDate` や有効なダイヤモンド構成は拒否しない。BOM の多階層展開、formula / configurator は本移行では未実装。
- sensitive Entity は設計指定の22名称をデータモデル XML と照合し、`UserLoginSecurityQuestion` は定義が無いため `application.yml` から除外した。他の21名称は entitymodel に存在する。
- `copyBOMAssocs` は有効期間中の行のみ複製し、自己構成・重複・循環を事前に除外する。行ごとの失敗で transaction を rollback-only にしない。
- local seed は MFG_FG / MFG_RAW1 / MFG_RAW2、MFG_PLANT、MFG_ROUTING と2タスク、機密確認用 CostComponent を作る。seed は local profile のみで冪等。
- `updateProductManufacturingRule` の対象が無い場合は移行版で404、旧版は null 参照に伴うエラーとなる。削除対象が無い場合は旧版に合わせ成功扱い。
- Entity API は component を `manufacturing` に固定し、catalog 参照 Entity と `extra-entities` の Product / ProductAssoc / ProductManufacturingRule / WorkEffort 系 / CostComponent に公開範囲を限定する。CostComponent は extra に含めても sensitive 判定を維持する。PaymentMethod は sensitive の多層防御対象として設定するが extra には含めず、API からは404となる。
- `createProductionRun` は OFBiz 名の `pRQuantity`、`startDate`、`facilityId` を必須とする。routing task は有効期間内の association を sequenceNum の数値順に処理し、setup + unit duration × 数量を直列積算する。Task のカレンダー日程は計算しない。
- 有効期間が重なる同一部品の BOM 行は、同じ production task と部品の `WorkEffortGoodStandard` を重複作成するため、OFBiz と同じく製造指図の作成を失敗させる。永続化前にデータ不整合として部品 ID を含む400を返す。
- BOM scrapFactor は百分率（-100% 超〜100% 未満のみ適用）として扱い、負数は salvage とする。null quantity / scrapFactor は読み込み時に 1 / 0% とみなす。
- `changeProductionRunTaskStatus` は同一ランの直前タスク完了を必須とする。`issueAllComponents`、在庫引当、実際原価・ECA は移行していない。
