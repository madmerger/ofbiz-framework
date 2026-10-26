# Accounting 移行判断ログ

## 判断

1. **multi の失敗は409**: module-local の `AccountingMultiExceptionAdvice` で `MultiServiceException` を409へ変換し、multi service が単一トランザクションで全件 rollback する。entity-auto / handler 共通処理の契約は共通基盤に従う。
2. **getInvoice* のAPI化**: `getInvoiceTotal`, `getInvoiceItemTotal`, `getInvoiceNotApplied`, `getInvoiceApplied` は OFBiz の service definition ではなく `InvoiceWorker` の static method。呼び出し可能なAPIとして移植し、移植元を handler Javadoc に記した。
3. **ECA/SECA**: Accounting の個々のサービス動作を移植するが、OFBiz ECA/SECA エンジン自体は実行しない。未移植サービスやGL仕訳を起動する暗黙の副作用は行わず、境界・差分を `unported.md` に明記する。
4. **GL 除外**: `createAcctgTransFor*`, `createAcctgTransAndEntriesFor*`, `revertAcctgTransOn*`, `postFinAccountTransToGl` の20 simple services は、GLの仕訳・転記と未移植呼び出し先を含むため未登録にする。
5. **entity-auto 範囲**: Accounting servicedef の386 entity-auto のみ登録し、生成済み `ServiceCatalog` を共通 registry に渡す。全コンポーネントのカタログは登録しない。

## 共通基盤の取り込み

Accounting branch は common base の次の3回の更新を merge している: `d6254e03b3`、`3ff99279eb`、`23f8c75e65`。その結果に合わせて generated catalog の直接登録、INOUT/OUT 主キー生成、entity-auto update の属性検証を利用する。Accounting 実装では共通基盤を変更しない。

## 外部依存

手書き handler が呼び出すサービスは Port 境界に置き、既定実装は local adapter とする。呼出し先がGL自動仕訳、未移植 Java/Groovy、または別コンポーネントで置換前のものは実行しない。具体的な未移植一覧を `unported.md` に記録する。
