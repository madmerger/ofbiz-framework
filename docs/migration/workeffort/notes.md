# 移行判断メモ

## 権限・主体

| OFBiz action / handler | Spring authority | 用途 |
|---|---|---|
| base permission `OFBTOOLS` | `OFBTOOLS_VIEW` | 共通のcomponent入口 |
| base permission `WORKEFFORTMGR` | `WORKEFFORTMGR_VIEW` | component利用 |
| create/update/delete | `WORKEFFORTMGR_CREATE/UPDATE/DELETE` | service action |
| administrator | `WORKEFFORTMGR_ADMIN` | 管理者バイパス |
| sensitive read | `WORKEFFORTMGR_SENSITIVE_VIEW` | RateAmount等 |
| role branch | `WORKEFFORTMGR_ROLE_<ACTION>` | WorkEffortPartyAssignment / 親 WorkEffort / CAL_OWNER・CAL_DELEGATE |

local profile は `admin`, `planner`, `readonly`, `auditor`, `noworkeffort` を用意する。Password は `MIGRATION_DEMO_PASSWORD` から共通 security configuration が受け取り、平文・固定値を設定ファイルに置かない。`workeffort.security.user-party-ids` は `admin→admin`, `planner→DemoEmployee`, `readonly→DemoEmployee`, `auditor→DemoAuditor`, `noworkeffort→DemoEmployee`。partyId はローカル認可の主体であり、HTTP入力値を優先しない。

次の明示表を `actionFor` のフォールバックとして用いる（catalog の main-action がある場合はそちらを優先）。表にも無い登録対象サービスは `IllegalStateException` として起動を止める。コードの `DEFAULT_ACTIONS` とこの表は同一。

| service | action |
|---|---|
| `makeCommunicationEventWorkEffort` | UPDATE |
| `createWorkEffortNote` | CREATE |
| `updateWorkEffortNote` | UPDATE |
| `createWorkEffortAssoc` | CREATE |
| `copyWorkEffortAssocs` | CREATE |
| `createWorkEffortAndAssoc` | CREATE |
| `assignInventoryToWorkEffort` | UPDATE |
| `createCommunicationEventWorkEff` | CREATE |
| `updateCommunicationEventWorkEff` | UPDATE |
| `deleteCommunicationEventWorkEff` | DELETE |
| `checkCustRequestItemExists` | VIEW |
| `setWorkEffortFixedAssetAssign` | UPDATE |
| `createWorkEffortFixedAssetAssign` | CREATE |
| `createWorkEffortReview` | CREATE |
| `indexWorkEffortKeywords` | UPDATE |
| `createWorkEffortKeyword` | CREATE |
| `createWorkEffortKeywords` | CREATE |
| `deleteWorkEffortKeywords` | DELETE |
| `workEffortManagerPermission` | VIEW |
| `workEffortGenericPermission` | VIEW |
| `timesheetUpdatePermission` | UPDATE |
| `workEffortICalendarPermission` | VIEW |
| `getICalWorkEfforts` | VIEW |
| `getPartyICalUrl` | VIEW |
| `createTimesheets` | CREATE |
| `createTimesheetForThisWeek` | CREATE |
| `addTimesheetToInvoice` | UPDATE |
| `addTimesheetToNewInvoice` | CREATE |
| `addWorkEffortTimeToInvoice` | UPDATE |
| `addWorkEffortTimeToNewInvoice` | CREATE |
| `checkTimesheetStatus` | VIEW |
| `updateTimeEntry` | UPDATE |
| `getTimeEntryRate` | VIEW |
| `deleteTimesheet` | DELETE |
| `createWorkEffortAttribute` | CREATE |
| `updateWorkEffortAttribute` | UPDATE |
| `deleteWorkEffortAttribute` | DELETE |
| `createWorkEffortContent` | CREATE |
| `updateWorkEffortContent` | UPDATE |
| `deleteWorkEffortContent` | DELETE |
| `createTimesheetRole` | CREATE |
| `createWorkEffort` | CREATE |
| `updateWorkEffort` | UPDATE |
| `deleteWorkEffort` | DELETE |
| `createWorkEffortRequest` | CREATE |
| `deleteWorkEffortRequest` | DELETE |
| `createWorkEffortRequestItem` | CREATE |
| `deleteWorkEffortRequestItem` | DELETE |
| `createWorkEffortQuote` | CREATE |
| `deleteWorkEffortQuote` | DELETE |
| `createWorkRequirementFulfillment` | CREATE |
| `deleteWorkRequirementFulfillment` | DELETE |
| `createShoppingListWorkEffort` | CREATE |
| `deleteShoppingListWorkEffort` | DELETE |
| `createOrderHeaderWorkEffort` | CREATE |
| `deleteOrderHeaderWorkEffort` | DELETE |
| `createWorkEffortAndPartyAssign` | CREATE |
| `assignPartyToWorkEffort` | UPDATE |
| `updatePartyToWorkEffortAssignment` | UPDATE |
| `quickAssignPartyToWorkEffort` | UPDATE |
| `quickAssignPartyToWorkEffortWithRole` | UPDATE |
| `getWorkEffort` | VIEW |
| `getWorkEffortAssignedTasks` | VIEW |
| `getWorkEffortEventsByPeriod` | VIEW |
| `createTimeEntry` | CREATE |
| `updateTimesheet` | UPDATE |

## multi 409

共通 `executeMulti` は失敗行を集めて `MultiServiceException` をthrowし、d6254e03b3 の `MigrationApiExceptionHandler.multiError` が `ResponseEntity.badRequest()` を返すためHTTP 400。brief §7のworkeffort固有controllerを維持し、409競合が一件でもあればHTTP 409、その他は先頭失敗status（default 400）を返す。成功時はcommon同様result list、失敗bodyは `responseMessage/errorMessage/errors` とする。

## Ports

| Port | local adapter |
|---|---|
| `InvoicePort` | `LocalWorkEffortAdapter` (Invoice / InvoiceItem JPA entities) |
| `RatePort` | `LocalWorkEffortAdapter` (RateAmount, specificity候補順で選択) |
| `CommunicationEventPort` | `LocalWorkEffortAdapter` |
| `PartyPort` | `LocalWorkEffortAdapter` |

Rate候補の優先順は WorkEffort・Party・Position・Rate type・Currency の一致度とし、OFBizの完全な期間/position優先順位とは一致しない可能性がある。実装・確認が必要な差分は `review.md` に記録する。

## SECA / EECA / seed

- 同一 component の entity-auto 呼出しは dispatcher または EntityAutoService に渡し、同一 transaction 内で実行する。
- WorkEffort create/update と WorkEffortNote create/update は `indexWorkEffortKeywords` handler を呼ぶ。
- createWorkEffort に quickAssignPartyId / communicationEventId がある場合、担当割当 / CommunicationEventWorkEff link を同一transactionで処理する。
- timesheet の entity-auto createTimeEntry / updateTimesheet は status check wrapper を経由する。
- local seed は OFBiz `applications/workeffort/data/WorkEffortTypeData.xml` と `applications/workeffort/testdef/data/WorkEffortTestData.xml` を参照し、デモ専用 party/rate/status はコード側で最小限作る。Postgres profile では seed を行わない。
- 未移植SECA/EECA actionおよびICS/CalDAV実装範囲は `unported.md` に記載する。

### 原典 ECA 対応表

| ECA trigger | action | 状態 |
|---|---|---|
| `createWorkEffort` commit | `quickAssignPartyToWorkEffort` | 移植。`quickAssignPartyId` / role を同一transactionで処理 |
| `createWorkEffort` commit | `makeCommunicationEventWorkEffort` | 移植。link handler実装 |
| `createWorkEffort` commit | `assocAcceptedCustRequestToWorkEffort` | 未移植 |
| `createWorkEffort` commit | `createWorkRequirementFulfillment` | 未移植 |
| `updateWorkEffort` commit | `makeCommunicationEventWorkEffort` | 移植。link handler実装 |
| `createCommunicationEventWorkEff` invoke | `createCommunicationEvent` | 部分移植。event/linkを作るがOFBiz service/ECA全条件は未再現 |
| `createWorkEffortRequest` invoke | `createCustRequest` | 未移植 |
| `createWorkEffortRequestItem` invoke | `createCustRequestItem` | 未移植 |
| `createWorkEffortQuote` invoke | `createQuote` | 未移植 |
| `createWorkRequirementFulfillment` invoke | `createRequirement` | 未移植 |
| `createShoppingListWorkEffort` invoke | `createShoppingList` | 未移植 |
| `createOrderHeaderWorkEffort` invoke | `createOrderHeader` | 未移植 |
| `createTimeEntry` invoke | `checkTimesheetStatus` | 移植。status wrapperで実行 |
| `updateTimesheet` invoke | `checkTimesheetStatus` | 移植。status wrapperで実行 |
| `deleteTimesheet` invoke | `checkTimesheetStatus` | 移植。status wrapperで実行 |
| `createTimesheetRole` invoke | `ensurePartyRole` | 移植。catalog handler後に実行 |
| WorkEffort create-store | `indexWorkEffortKeywords` | 移植。作成後に再index |
| WorkEffortAttribute create-store | `indexWorkEffortKeywords` | 移植。entity-auto handler後に再index |
| WorkEffortContent create-store | `indexWorkEffortKeywords` | 移植。entity-auto handler後に再index |
| WorkEffortNote create-store | `indexWorkEffortKeywords` | 部分移植。WorkEffortNote service handlerで呼び出し |

## ベース更新 d6254e03b3 / 3ff99279eb / 23f8c75e65 の取り込み対応

- generated recordを `ServiceHandlerRegistry.registerCatalog("workeffort", ServiceCatalog.all())` に直接渡し、型変換コードを置かない。Generatorには対象3 servicedefのみを渡す。
- `migration.security.component=workeffort` を設定し、catalog entityと `extra-entities` だけをEntity APIで公開する。RateAmount / PartyRate / StatusItem / StatusValidChange等の handler/E2E利用分を追加し、Paymentは非公開とする。
- EntityAutoServiceのINOUT primary key優先、必須INOUT検証、sub-sequence採番、update未宣言属性400をテストする。
- HTTPS既定trueのためlocal/testでは明示false、postgresでは明示true。
- common multiは400と確認したのでworkeffort固有409 controllerを維持する。
- 3ff99279ebで空のINOUT主キー（単一PK・sub-sequence PK）の自動採番もEntityAutoServiceで扱う。workeffort catalogの `createWorkEffortType`（`workEffortTypeId` INOUT optional）で、空文字入力時の採番と永続化を検証する。component側に採番回避策は置かない。
- 23f8c75e65ではEntityAutoServiceのcreate時にOUT主キーの入力値を無視して採番する。`createTimesheet` の `timesheetId`（OUT）へcaller値を送っても採番IDが返ることをテストする。移植済みhandlerでは `createTimesheets` が行mapをcreateTimesheetへ渡すが、OFBiz原典の `TimesheetServices.xml` は `set-service-fields` でサービス入力項目だけを作るため、OUT主キーを使う差異はない。`createTimesheetForThisWeek` もOFBizの呼出し先が同じentity-auto serviceであり、callerのOUT値を採用しない。
