# 未移植項目と画面・サービス集計

この文書は `python3 docs/migration/manufacturing/render_unported.py` で再生成する。

## 件数集計

| 項目 | 件数 |
| --- | ---: |
| servicedef（XML） | 8 |
| entity-auto | 18 |
| simple | 8 |
| Java | 50 |
| Groovy | 49 |
| interface | 2 |
| 全サービス | 127 |
| 移植 Java | 6 |
| 移植 Groovy | 1 |
| 未移植 Java/Groovy | 92 |
| controller request-map | 155 |
| controller view-map | 69 |
| FTL | 21 |
| widget screen | 80 |
| view-entity | 1 |
| 未再現 SECA | 6 |

## 未移植 Java / Groovy サービス

| サービス名 | engine | servicedef | location | 理由 |
| --- | --- | --- | --- | --- |
| `createShipmentPackages` | java | `applications/manufacturing/servicedef/services.xml` | `org.apache.ofbiz.manufacturing.bom.BOMServices` | 設計で指定された主要 Java フロー以外は段階移行対象。 |
| `getMaxDepth` | java | `applications/manufacturing/servicedef/services_bom.xml` | `org.apache.ofbiz.manufacturing.bom.BOMServices` | BOM 編集・シミュレーション・帳票などの周辺機能は本移行対象外。 |
| `updateLowLevelCode` | java | `applications/manufacturing/servicedef/services_bom.xml` | `org.apache.ofbiz.manufacturing.bom.BOMServices` | BOM 編集・シミュレーション・帳票などの周辺機能は本移行対象外。 |
| `initLowLevelCode` | java | `applications/manufacturing/servicedef/services_bom.xml` | `org.apache.ofbiz.manufacturing.bom.BOMServices` | BOM 編集・シミュレーション・帳票などの周辺機能は本移行対象外。 |
| `getBOMTree` | java | `applications/manufacturing/servicedef/services_bom.xml` | `org.apache.ofbiz.manufacturing.bom.BOMServices` | BOM 編集・シミュレーション・帳票などの周辺機能は本移行対象外。 |
| `getProductsInPackages` | java | `applications/manufacturing/servicedef/services_bom.xml` | `org.apache.ofbiz.manufacturing.bom.BOMServices` | BOM 編集・シミュレーション・帳票などの周辺機能は本移行対象外。 |
| `getNotAssembledComponents` | java | `applications/manufacturing/servicedef/services_bom.xml` | `org.apache.ofbiz.manufacturing.bom.BOMServices` | BOM 編集・シミュレーション・帳票などの周辺機能は本移行対象外。 |
| `findBomProducts` | groovy | `applications/manufacturing/servicedef/services_bom.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/BomManagementServices.groovy` | 画面用検索・候補表示サービスは本移行対象外。 |
| `findProductLookupOptions` | groovy | `applications/manufacturing/servicedef/services_bom.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/ManufacturingLookupServices.groovy` | 画面用検索・候補表示サービスは本移行対象外。 |
| `getBomSnapshot` | groovy | `applications/manufacturing/servicedef/services_bom.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/BomManagementServices.groovy` | BOM 編集・シミュレーション・帳票などの周辺機能は本移行対象外。 |
| `removeBomAssociation` | groovy | `applications/manufacturing/servicedef/services_bom.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/BomManagementServices.groovy` | BOM 編集・シミュレーション・帳票などの周辺機能は本移行対象外。 |
| `removeBomParentAssociation` | groovy | `applications/manufacturing/servicedef/services_bom.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/BomManagementServices.groovy` | BOM 編集・シミュレーション・帳票などの周辺機能は本移行対象外。 |
| `runBomSimulation` | groovy | `applications/manufacturing/servicedef/services_bom.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/BomManagementServices.groovy` | BOM 編集・シミュレーション・帳票などの周辺機能は本移行対象外。 |
| `findWarehouses` | groovy | `applications/manufacturing/servicedef/services_bom.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/ManufacturingLookupServices.groovy` | 画面用検索・候補表示サービスは本移行対象外。 |
| `findBomTypes` | groovy | `applications/manufacturing/servicedef/services_bom.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/BomManagementServices.groovy` | 画面用検索・候補表示サービスは本移行対象外。 |
| `removeCalendar` | groovy | `applications/manufacturing/servicedef/services_calendar.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/routing/RoutingServicesScript.groovy` | 設計で指定された主要フロー以外は段階移行対象。 |
| `removeCalendarWeek` | groovy | `applications/manufacturing/servicedef/services_calendar.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/routing/RoutingServicesScript.groovy` | 設計で指定された主要フロー以外は段階移行対象。 |
| `manufacturingPermissionService` | groovy | `applications/manufacturing/servicedef/services_calendar.xml` | `component://common/src/main/groovy/org/apache/ofbiz/common/permission/CommonPermissionServices.groovy` | 権限判定は common の MANUFACTURING main-action マッピングで担保する。 |
| `findCostComponentOptions` | groovy | `applications/manufacturing/servicedef/services_lookup.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/ManufacturingLookupServices.groovy` | 画面用検索・候補表示サービスは本移行対象外。 |
| `findManufacturingFacilities` | groovy | `applications/manufacturing/servicedef/services_lookup.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/ManufacturingLookupServices.groovy` | 画面用検索・候補表示サービスは本移行対象外。 |
| `findManufacturingPlants` | groovy | `applications/manufacturing/servicedef/services_lookup.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/ManufacturingLookupServices.groovy` | 画面用検索・候補表示サービスは本移行対象外。 |
| `findFixedAssets` | groovy | `applications/manufacturing/servicedef/services_lookup.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/ManufacturingLookupServices.groovy` | 画面用検索・候補表示サービスは本移行対象外。 |
| `searchProducts` | groovy | `applications/manufacturing/servicedef/services_lookup.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/ManufacturingLookupServices.groovy` | 画面用検索・候補表示サービスは本移行対象外。 |
| `searchParties` | groovy | `applications/manufacturing/servicedef/services_lookup.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/ManufacturingLookupServices.groovy` | 画面用検索・候補表示サービスは本移行対象外。 |
| `executeMrp` | java | `applications/manufacturing/servicedef/services_mrp.xml` | `org.apache.ofbiz.manufacturing.mrp.MrpServices` | MRP 計画・イベント・実行ログは本移行の対象外。 |
| `createMrpRunLogAndTrackerBeforeQueueingMrp` | java | `applications/manufacturing/servicedef/services_mrp.xml` | `org.apache.ofbiz.manufacturing.mrp.MrpServices` | MRP 計画・イベント・実行ログは本移行の対象外。 |
| `launchMrpRun` | java | `applications/manufacturing/servicedef/services_mrp.xml` | `org.apache.ofbiz.manufacturing.mrp.MrpServices` | MRP 計画・イベント・実行ログは本移行の対象外。 |
| `initMrpEvents` | java | `applications/manufacturing/servicedef/services_mrp.xml` | `org.apache.ofbiz.manufacturing.mrp.MrpServices` | MRP 計画・イベント・実行ログは本移行の対象外。 |
| `findProductMrpQoh` | java | `applications/manufacturing/servicedef/services_mrp.xml` | `org.apache.ofbiz.manufacturing.mrp.MrpServices` | MRP 計画・イベント・実行ログは本移行の対象外。 |
| `createMrpEvent` | java | `applications/manufacturing/servicedef/services_mrp.xml` | `org.apache.ofbiz.manufacturing.mrp.InventoryEventPlannedServices` | MRP 計画・イベント・実行ログは本移行の対象外。 |
| `setEstimatedDeliveryDates` | java | `applications/manufacturing/servicedef/services_mrp.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | MRP 計画・イベント・実行ログは本移行の対象外。 |
| `findMrpEventPlans` | groovy | `applications/manufacturing/servicedef/services_mrp.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/mrp/MrpEventPlanServices.groovy` | MRP 計画・イベント・実行ログは本移行の対象外。 |
| `findMrpRuns` | groovy | `applications/manufacturing/servicedef/services_mrp.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/mrp/MrpRunLogServices.groovy` | MRP 計画・イベント・実行ログは本移行の対象外。 |
| `findPlanningFacilities` | groovy | `applications/manufacturing/servicedef/services_mrp.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/ManufacturingLookupServices.groovy` | MRP 計画・イベント・実行ログは本移行の対象外。 |
| `findProductionRuns` | groovy | `applications/manufacturing/servicedef/services_production_run.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunQueryServices.groovy` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `getProductionRunDetails` | groovy | `applications/manufacturing/servicedef/services_production_run.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunQueryServices.groovy` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `createProductionRunPartyAssign` | groovy | `applications/manufacturing/servicedef/services_production_run.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunServicesScript.groovy` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `createProductionRunAssoc` | groovy | `applications/manufacturing/servicedef/services_production_run.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunServicesScript.groovy` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `createProductionRunsForProductBom` | java | `applications/manufacturing/servicedef/services_production_run.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `createProductionRunsForOrder` | java | `applications/manufacturing/servicedef/services_production_run.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `createProductionRunFromRequirement` | java | `applications/manufacturing/servicedef/services_production_run.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `createProductionRunFromConfiguration` | java | `applications/manufacturing/servicedef/services_production_run.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `createProductionRunForMktgPkg` | java | `applications/manufacturing/servicedef/services_production_run.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `updateProductionRun` | java | `applications/manufacturing/servicedef/services_production_run.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `addProductionRunRoutingTask` | java | `applications/manufacturing/servicedef/services_production_run.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `checkUpdatePrunRoutingTask` | java | `applications/manufacturing/servicedef/services_production_run.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `deleteProductionRunRoutingTask` | java | `applications/manufacturing/servicedef/services_production_run.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `addProductionRunComponent` | java | `applications/manufacturing/servicedef/services_production_run.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `updateProductionRunComponent` | java | `applications/manufacturing/servicedef/services_production_run.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `deleteProductionRunComponent` | java | `applications/manufacturing/servicedef/services_production_run.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `issueProductionRunTask` | groovy | `applications/manufacturing/servicedef/services_production_run.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunServicesScript.groovy` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `issueProductionRunTaskComponent` | groovy | `applications/manufacturing/servicedef/services_production_run.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunServicesScript.groovy` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `executeProductionRunTaskComponentAtomic` | groovy | `applications/manufacturing/servicedef/services_production_run.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunServicesScript.groovy` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `reconcileGlobalReservations` | groovy | `applications/manufacturing/servicedef/services_production_run.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunServicesScript.groovy` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `issueInventoryItemToWorkEffort` | groovy | `applications/manufacturing/servicedef/services_production_run.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunServicesScript.groovy` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `productionRunProduce` | java | `applications/manufacturing/servicedef/services_production_run.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `productionRunDeclareAndProduce` | java | `applications/manufacturing/servicedef/services_production_run.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `productionRunTaskProduce` | java | `applications/manufacturing/servicedef/services_production_run.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `productionRunTaskReturnMaterial` | java | `applications/manufacturing/servicedef/services_production_run.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `checkDecomposeInventoryItem` | java | `applications/manufacturing/servicedef/services_production_run.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `decomposeInventoryItem` | java | `applications/manufacturing/servicedef/services_production_run.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `quickRunProductionRunTask` | java | `applications/manufacturing/servicedef/services_production_run.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `quickRunAllProductionRunTasks` | java | `applications/manufacturing/servicedef/services_production_run.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `quickStartAllProductionRunTasks` | java | `applications/manufacturing/servicedef/services_production_run.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `quickChangeProductionRunStatus` | java | `applications/manufacturing/servicedef/services_production_run.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `cancelProductionRun` | java | `applications/manufacturing/servicedef/services_production_run.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `getProductionRunTotResQty` | java | `applications/manufacturing/servicedef/services_production_run.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `getWorkEffortCosts` | java | `applications/manufacturing/servicedef/services_production_run.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `getProductionRunCost` | java | `applications/manufacturing/servicedef/services_production_run.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `createProductionRunTaskCosts` | java | `applications/manufacturing/servicedef/services_production_run.xml` | `org.apache.ofbiz.manufacturing.jobshopmgt.ProductionRunServices` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `reserveWorkEffortInventory` | groovy | `applications/manufacturing/servicedef/services_production_run.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunResServices.groovy` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `reserveWorkEffortInventoryItem` | groovy | `applications/manufacturing/servicedef/services_production_run.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunResServices.groovy` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `autoReserveWorkEffortInventory` | groovy | `applications/manufacturing/servicedef/services_production_run.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunResServices.groovy` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `checkProductionRunFacilityAutoReserve` | groovy | `applications/manufacturing/servicedef/services_production_run.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunResServices.groovy` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `getProductionRunTaskForceIssueImpact` | groovy | `applications/manufacturing/servicedef/services_production_run.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunResServices.groovy` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `releaseProductionRunTaskComponent` | groovy | `applications/manufacturing/servicedef/services_production_run.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunResServices.groovy` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `getTaskQuantities` | groovy | `applications/manufacturing/servicedef/services_production_run.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunResServices.groovy` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `reallocateAndIssueInventory` | groovy | `applications/manufacturing/servicedef/services_production_run.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunResServices.groovy` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `reassignWorkEffortInventoryReservations` | groovy | `applications/manufacturing/servicedef/services_production_run.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunResServices.groovy` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `checkProductionRunFacilityRecon` | groovy | `applications/manufacturing/servicedef/services_production_run.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunResServices.groovy` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `reconcileInventoryForProductionJobs` | groovy | `applications/manufacturing/servicedef/services_production_run.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/jobshopmgt/ProductionRunResServices.groovy` | 在庫引当、原価、注文連携、帳票などの周辺処理は本移行の主要フロー対象外。 |
| `lookupRoutingTask` | java | `applications/manufacturing/servicedef/services_routing.xml` | `org.apache.ofbiz.manufacturing.techdata.TechDataServices` | 画面検索・ルーティング編集・コスト計算などの周辺機能は本移行対象外。 |
| `checkRoutingTaskAssoc` | java | `applications/manufacturing/servicedef/services_routing.xml` | `org.apache.ofbiz.manufacturing.techdata.TechDataServices` | 画面検索・ルーティング編集・コスト計算などの周辺機能は本移行対象外。 |
| `getRoutingTaskAssocs` | groovy | `applications/manufacturing/servicedef/services_routing.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/routing/RoutingServicesScript.groovy` | 画面検索・ルーティング編集・コスト計算などの周辺機能は本移行対象外。 |
| `getEstimatedTaskTime` | java | `applications/manufacturing/servicedef/services_routing.xml` | `org.apache.ofbiz.manufacturing.routing.RoutingServices` | 画面検索・ルーティング編集・コスト計算などの周辺機能は本移行対象外。 |
| `findProductRoutings` | groovy | `applications/manufacturing/servicedef/services_routing.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/RoutingCostManagementServices.groovy` | 画面検索・ルーティング編集・コスト計算などの周辺機能は本移行対象外。 |
| `findRoutingTaskPurposeTypeOptions` | groovy | `applications/manufacturing/servicedef/services_routing.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/RoutingCostManagementServices.groovy` | 画面検索・ルーティング編集・コスト計算などの周辺機能は本移行対象外。 |
| `searchRoutingTasksForRouting` | groovy | `applications/manufacturing/servicedef/services_routing.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/RoutingCostManagementServices.groovy` | 画面検索・ルーティング編集・コスト計算などの周辺機能は本移行対象外。 |
| `getRoutingDetails` | groovy | `applications/manufacturing/servicedef/services_routing.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/RoutingCostManagementServices.groovy` | 画面検索・ルーティング編集・コスト計算などの周辺機能は本移行対象外。 |
| `expireRoutingGoodStandard` | groovy | `applications/manufacturing/servicedef/services_routing.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/RoutingCostManagementServices.groovy` | 画面検索・ルーティング編集・コスト計算などの周辺機能は本移行対象外。 |
| `findRoutingTasks` | groovy | `applications/manufacturing/servicedef/services_routing.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/RoutingCostManagementServices.groovy` | 画面検索・ルーティング編集・コスト計算などの周辺機能は本移行対象外。 |
| `getRoutingTaskDetails` | groovy | `applications/manufacturing/servicedef/services_routing.xml` | `component://manufacturing/src/main/groovy/org/apache/ofbiz/manufacturing/api/RoutingCostManagementServices.groovy` | 画面検索・ルーティング編集・コスト計算などの周辺機能は本移行対象外。 |

## interface サービス

| サービス名 | location |
| --- | --- |
| `interfaceBomFormula` | `` |
| `interfaceTaskFormula` | `` |

## Widget 定義ファイル

| ファイル | 種別 | screen 数 |
| --- | --- | ---: |
| `applications/manufacturing/widget/manufacturing/BomForms.xml` | form | 0 |
| `applications/manufacturing/widget/manufacturing/BomScreens.xml` | screen | 5 |
| `applications/manufacturing/widget/manufacturing/CalendarForms.xml` | form | 0 |
| `applications/manufacturing/widget/manufacturing/CalendarScreens.xml` | screen | 13 |
| `applications/manufacturing/widget/manufacturing/CommonScreens.xml` | screen | 3 |
| `applications/manufacturing/widget/manufacturing/CostForms.xml` | form | 0 |
| `applications/manufacturing/widget/manufacturing/CostScreens.xml` | screen | 1 |
| `applications/manufacturing/widget/manufacturing/FieldLookupForms.xml` | form | 0 |
| `applications/manufacturing/widget/manufacturing/JobshopScreens.xml` | screen | 23 |
| `applications/manufacturing/widget/manufacturing/LookupScreens.xml` | screen | 3 |
| `applications/manufacturing/widget/manufacturing/ManufacturingMenus.xml` | menu | 0 |
| `applications/manufacturing/widget/manufacturing/MrpForms.xml` | form | 0 |
| `applications/manufacturing/widget/manufacturing/MrpScreens.xml` | screen | 4 |
| `applications/manufacturing/widget/manufacturing/ProductionRunForms.xml` | form | 0 |
| `applications/manufacturing/widget/manufacturing/ReportScreens.xml` | screen | 13 |
| `applications/manufacturing/widget/manufacturing/RoutingScreens.xml` | screen | 15 |
| `applications/manufacturing/widget/manufacturing/RoutingTaskForms.xml` | form | 0 |

## view-entity

| entity-name |
| --- |
| `MrpEventView` |

## 未再現 SECA

| service | event | action |
| --- | --- | --- |
| `updateRequirement` | commit | `createProductionRunFromRequirement` |
| `createRequirement` | commit | `createProductionRunFromRequirement` |
| `createBOMAssoc` | commit | `updateLowLevelCode` |
| `deleteProductAssoc` | commit | `updateLowLevelCode` |
| `changeProductionRunStatus` | return | `autoReserveWorkEffortInventory` |
| `balanceInventoryItems` | commit | `reassignWorkEffortInventoryReservations` |
