# 未移植機能の台帳

## Java / Groovy service definitions

| Service | Engine | Location | 理由 |
|---|---|---|---|
| `getWorkEffortAssignedEventsForRole` | java | `applications/workeffort/src/main/java/org/apache/ofbiz/workeffort/workeffort/WorkEffortServices.java` | role calendar query未移植 |
| `getWorkEffortAssignedEventsForRoleOfAllParties` | java | 同上 | 全party calendar query未移植 |
| `getWorkEffortAssignedActivities` | java | 同上 | activity検索のOFBiz dispatcher依存 |
| `getWorkEffortAssignedActivitiesByRole` | java | 同上 | role view/query未移植 |
| `getWorkEffortAssignedActivitiesByGroup` | java | 同上 | PartyGroup検索未移植 |
| `removeDuplicateWorkEfforts` | java | 同上 | 重複統合ロジック未移植 |
| `getProductManufacturingSummaryByFacility` | java | 同上 | manufacturing component依存 |
| `processWorkEffortEventReminders` | java | 同上 | async job/notification未移植 |
| `processWorkEffortEventReminder` | java | 同上 | reminder template/email未移植 |
| `copyWorkEffort` | groovy | `applications/workeffort/src/main/groovy/org/apache/ofbiz/workeffort/workeffort/workeffort/WorkEffortServicesScript.groovy` | association/content copy未移植 |
| `duplicateWorkEffort` | groovy | 同上 | duplicate workflow未移植 |
| `checkAndCreateWorkEffort` | groovy | 同上 | lookup/create orchestration未移植 |
| `checkAndUpdateWorkEffort` | groovy | 同上 | lookup/update orchestration未移植 |
| `assocAcceptedCustRequestToWorkEffort` | groovy | 同上 | CustRequest SECA dependency未移植 |
| `createWorkEffortContactMech` | groovy | 同上 | contact-mech workflow未移植 |
| `updateWorkEffortContactMech` | groovy | 同上 | contact-mech workflow未移植 |
| `createWorkEffortPostalAddress` | groovy | 同上 | party address services未移植 |
| `updateWorkEffortPostalAddress` | groovy | 同上 | party address services未移植 |
| `createWorkEffortTelecomNumber` | groovy | 同上 | telecom services未移植 |
| `updateWorkEffortTelecomNumber` | groovy | 同上 | telecom services未移植 |
| `createWorkEffortEmailAddress` | groovy | 同上 | email contact-mech services未移植 |
| `updateWorkEffortEmailAddress` | groovy | 同上 | email contact-mech services未移植 |

## Widget / FTL / screens / forms / menus

全件未移植。完全な request/controller 一覧は `inventory/requests.md` を参照する。

- FTL: `template/{Main.ftl,calendar/{CalendarEventContent,Day,Month,Upcoming,Week}.ftl,find/{WorkEffortSearchOptions,WorkEffortSearchResults}.ftl,task/MyTasks.ftl,workeffort/{EventReminderEmail,ICalendarHelp}.ftl}`。
- Widget definitions: `widget/{CalendarForms,CalendarScreens,CommonScreens,CustRequestForms,CustRequestScreens,LookupForms,LookupScreens,TimesheetForms,TimesheetScreens,WorkEffortCommEventForms,WorkEffortForms,WorkEffortMenus,WorkEffortRelatedSummaryScreens,WorkEffortScreens,WorkEffortTrees}.xml` および `widget/workeffort/{WorkEffortAgreementCpd,WorkEffortAttributeCpd,WorkEffortCommEventCpd,WorkEffortContactMechCpd,WorkEffortContentsCpd,WorkEffortCpd,WorkEffortEventRemindersCpd,WorkEffortFixedAssetCpd,WorkEffortGoodStandardsCpd,WorkEffortKeywordsCpd,WorkEffortNotesCpd,WorkEffortOrdersCpd,WorkEffortPartyAssignCpd,WorkEffortQuotesCpd,WorkEffortRateAssignCpd,WorkEffortRequestAndItemCpd,WorkEffortRequirementsCpd,WorkEffortReviewsCpd,WorkEffortShopListCpd,WorkEffortTimeEntriesCpd}.xml`。
- Web screens/routes: `webapp/workeffort/WEB-INF/{controller.xml,icalendar-controller.xml,web.xml,workeffort-controller.xml}`, `webapp/ical/WEB-INF/web.xml`, `webapp/workeffort/index.jsp`。

## view-entity

33件すべて未移植: `WorkEffortAndChild`, `WorkEffortAndFulfillment`, `WorkEffortAndFixedAssetAssign`, `WorkEffortAndPartyAssign`, `WorkEffortAndPartyAssignAndType`, `WorkEffortPartyAssignAndRoleType`, `WorkEffortAssocView`, `WorkEffortAssocFromView`, `WorkEffortAssocToView`, `WorkEffortFindView`, `WorkEffortNoteAndData`, `WorkEffortPartyAssignByGroup`, `WorkEffortPartyAssignByRole`, `WorkEffortAndGoods`, `WorkEffortProductGoods`, `WorkEffortAndContentDataResource`, `WorkEffortAndInventoryAssign`, `WorkEffortAndInventoryProduced`, `WorkEffortInvResAndItem`, `WorkEffortInventoryAssignAndItem`, `WorkEffortPartyAssignView`, `WorkEffortCommunicationEventView`, `WorkEffortShoppingListView`, `WorkEffortQuoteView`, `WorkEffortOrderHeaderView`, `WorkEffortCustRequestView`, `WorkEffortCustRequestItemView`, `WorkEffortRequirementView`, `WorkEffortAndSalesOpportunity`, `WorkEffortContentAndInfo`, `WorkEffortContactMechView`, `WorkEffortAndContactMech`, `WorkEffortAndTimeEntry`.

## SECA / EECA / ICS / CalDAV

- `secas.xml` の全 trigger/action と移植状態、`eecas.xml` 4件の状態は `notes.md` の「原典 ECA 対応表」に列挙した。SECA 16件のうち8件は未移植、1件は部分移植、7件は移植済み。EECA 4件は keyword reindex handler に接続しているが、OFBiz の stemming / stop-word / content resource backend は未移植。
- CustRequest / Quote / Requirement / ShoppingList / OrderHeader 関連のECA連鎖と `assocAcceptedCustRequestToWorkEffort` は未移植。ICS feed serialization/recurrence、CalDAV endpoints・sync semantics は未移植。`getICalWorkEfforts` はJSON assignment一覧でICS互換ではない。

`getWorkEffortEventsByPeriod` は UTC の Java Calendar period bucket を返し、期間overlap・facility/type/canceled・personal assignment/public filterを扱う。OFBiz TemporalExpression recurrence、timezone/localeのUtilDateTime調整、`entityExprList` 条件、calendarEntriesByDateRange grouping の完全互換は未実装。

## 件数集計

| 区分 | 件数 |
|---|---:|
| Entity-auto | 95 |
| Simple | 33 |
| Java移植 / 未移植 | 3 / 9 |
| Groovy移植 / 未移植 | 8 / 13 |
| view-entity未移植 | 33 |
| FTL未移植 | 11 |
| Widget XML未移植 | 35 |
