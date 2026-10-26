# CommonExt 未移植範囲

このファイルは `applications/commonext` の画面・イベント・静的リソースを列挙する。
サービス6本（`SystemInfoServices.groovy`）と CommonExt の NoteData 拡張は移行対象。
OFBiz Widget / controller event は UI 再設計と他コンポーネント service 依存のため未移植。

## 1. Groovy 画面アクション (6)

| ファイル | 内容 / 理由 |
| --- | --- |
| `src/main/groovy/org/apache/ofbiz/commonext/ofbizsetup/FindFacility.groovy` | Facility / PartyAndPostalAddress 検索、画面用 context |
| `src/main/groovy/org/apache/ofbiz/commonext/ofbizsetup/ChangeOrgPartyId.groovy` | 組織 party 変更用の画面 action |
| `src/main/groovy/org/apache/ofbiz/commonext/ofbizsetup/GetProdCatalog.groovy` | ProductStore / Facility / Catalog / Category / Product 情報を構築 |
| `src/main/groovy/org/apache/ofbiz/commonext/ofbizsetup/GetProductStoreAndWebSite.groovy` | Store / Website 情報を構築 |
| `src/main/groovy/org/apache/ofbiz/commonext/ofbizsetup/SystemInfoNote.groovy` | SystemInfo widget の画面用 context |
| `src/main/groovy/org/apache/ofbiz/commonext/template/DocTypeTemplate.groovy` | `PartyPrefDocTypeTplAndCustomScreen` による画面 template |

`SystemInfoServices.groovy` はサービス実装として移行済みであり、この表の画面 Groovy 数には含めない。

## 2. SetupEvents simple-method (9定義)

所在: `minilang/setup/SetupEvents.xml`。事前調査記載の11本と現行 XML の9 element に不一致がある（notes §4）。
Party、Product、Accounting、ContactMech、UserLogin 等の他 component service を呼び出すため未移植。

| simple-method | controller URI |
| --- | --- |
| `createOrganization` | `createOrganization` |
| `setupDefaultGeneralLedger` | `OrganizationToComplete` |
| `createCustomer` | `createCustomer` |
| `createFacilityAndContactMech` | `CreateFacility` |
| `createProductStoreWithDefaultSetting` | `createProductStore` |
| `createProdCatalogAndProductStoreCatalog` | `createProdCatalog` |
| `createProductCategoryAndAddToProdCatalog` | `createProductCategory` |
| `createUpdateProductInCategory` | `createUpdateProduct` |
| `createDefaultWebSite` | `createWebSite` |

## 3. Controller request-map (未移植33 / 全36)

`ofbizsetup/WEB-INF/controller.xml` の32件と `webapp/WEB-INF/controller.xml` の4件。
CommonExt service に対応する次の3 request は API handler として移行済みのため未移植数から除く:
`createSystemInfoNote`、`deleteSystemInfoNote`、`deleteAllSystemNotes`。

| 未移植 URI | 種別 / invoke | 理由 |
| --- | --- | --- |
| `main` | view | Setup 初期画面 |
| `initialsetup` | view | 初期設定 screen |
| `showMessage` | view | message screen |
| `updatePartyGroup` | service | Party component service |
| `EntityExportAll` | view | Webtools entity export screen |
| `entityExportAll` | service | Webtools entity export service |
| `FindProductStore` | view | Setup search flow。ProductStore の generic read endpoint のみ利用可能 |
| `EditProductStore` | view | フォーム・screen action 未移植 |
| `createProductStore` | simple `createProductStoreWithDefaultSetting` | SetupEvents / Product service 群 |
| `updateProductStore` | service | 他 component の service |
| `createOrganization` | simple `createOrganization` | Party、連絡先、UserLogin 作成 |
| `OrganizationToComplete` | simple `setupDefaultGeneralLedger` | Accounting 初期化 |
| `ViewFacility` | view | generic read-only Facility endpoint のみ |
| `EditFacility` | view | フォーム・screen action 未移植 |
| `CreateFacility` | simple `createFacilityAndContactMech` | Facility / ContactMech 作成 |
| `UpdateFacility` | service | Facility service |
| `FindWebSite` | view | generic Website read endpoint のみ |
| `EditWebSite` | view | フォーム・screen action 未移植 |
| `createWebSite` | simple `createDefaultWebSite` | Website / ProductStore 初期化 |
| `updateWebSite` | service | Website service |
| `firstproduct` | view | 初回 Product 設定 screen |
| `createProdCatalog` | simple `createProdCatalogAndProductStoreCatalog` | Product catalog service 群 |
| `updateProdCatalog` | service | Product service |
| `EditCategory` | view | generic ProductCategory read endpoint のみ |
| `createProductCategory` | simple `createProductCategoryAndAddToProdCatalog` | Catalog / Category link 作成 |
| `updateProductCategory` | service | Product service |
| `EditProduct` | view | generic Product read endpoint のみ |
| `createUpdateProduct` | simple `createUpdateProductInCategory` | Product / Price / Category service 群 |
| `firstcustomer` | view | 初回 Customer 設定 screen |
| `createCustomer` | simple `createCustomer` | Party / ContactMech / UserLogin 作成 |
| `LookupFacility` | view | generic Facility read は可能、lookup widget 未移植 |
| `LookupPartyName` | view | Party lookup widget 未移植 |
| `stream` | Java `DataEvents.serveObjectData` | Content streaming |

## 4. Widget / Template / View

### Widget 定義

| ファイル | 定義 |
| --- | --- |
| `widget/CommonScreens.xml` | screen: `ApplicationDecorator` |
| `widget/SystemInfoScreens.xml` | screen: `SystemInfoNotes`, `SystemInfoStatus` |
| `widget/SystemInfoForms.xml` | form: `SystemInfoNotes`, `EditSysInfoPortletParams`, `SystemInfoStatus`, `CreateSystemInfoNote` |
| `widget/SystemInfoMenus.xml` | menu: `SystemInfoNotes` |
| `widget/ofbizsetup/CommonScreens.xml` | screen: `main-decorator`, `CommonPartyDecorator`, `CommonSetupDecorator`, `EntityExportAll` |
| `widget/ofbizsetup/ProfileScreens.xml` | screen: `FirstCustomer`, `viewprofile` |
| `widget/ofbizsetup/SetupScreens.xml` | screen: `InitialSetup`, `EditFacility`, `EditProductStore`, `EditWebSite`, `EditProdCatalog`, `EditCategory`, `EditProduct`, `CommonFirstProductDecorator`, `nopartyAcctgPreference` |
| `widget/ofbizsetup/SetupForms.xml` | form: `NewOrganization`, `NewCustomer`, `ViewOrganization`, `EditFacility`, `EditProductStore`, `EditWebSite`, `EditProdCatalog`, `EditProductCategory`, `EditProduct`, `ListProduct`, `ListOrganizations`, `EditCustomer` |
| `widget/ofbizsetup/Menus.xml` | menu: `MainActionMenu`, `SetupAppBar`, `SetupSubTabBar`, `FirstProductTabBar`, `PersonUpdate`, `GroupUpdate` |

### FTL / JavaScript

- CommonExt 内の `.ftl`: **0件**。
- `webapp/common-js` 内の JavaScript: **0件**。この webapp の controller は preprocessor のみ。
- `webapp/ordermgr-js`: `ConvertUom.js`、`OrderShippingInfo.js`、`geoAutoCompleter.js`、`order.js`、`return.js`。他 application 画面の静的資産のため未移植。

### view-entity

| view-entity | 利用箇所 / 扱い |
| --- | --- |
| `CommunicationEventAndRole` | `SystemInfoServices.groovy` status で使用。JPQL で代替 |
| `WorkEffortAndPartyAssign` | 同 status で使用。JPQL で代替 |
| `PartyAcctgPrefAndGroup` | setup CommonScreens の会計設定。Accounting / widget 依存で未移植 |
| `PartyAndPostalAddress` | `FindFacility.groovy`。Party / ContactMech 表示で未移植 |
| `PartyPrefDocTypeTplAndCustomScreen` | `DocTypeTemplate.groovy`。Accounting / Content screen 依存で未移植 |
| `PartyNameView` | `LookupPartyName` / party lookup。lookup screen 未移植 |

### help / asciidoc

未移植: `data/helpdata/HELP_OFBizDocumentationSystem.xml`、`HELP_OFBizDocumentationSystem_FR.xml`、
`src/docs/asciidoc/ofbiz-setup.adoc` と `_include/` の
`HELP_initialsetup.adoc`、`HELP_setup_editFacility.adoc`、`HELP_setup_editProductStore.adoc`、
`HELP_setup_editWebSite.adoc`、`HELP_setup_firstProdCatalog.adoc`、
`HELP_setup_firstProductCategory.adoc`、`HELP_setup_firstcustomer.adoc`、
`HELP_setup_firstproduct.adoc`。

## 5. Data file

component data seed は Spring Boot module へは持ち込まず未移植。権限シードは API authority 定義へ反映し、local 開発用 data は `LocalSeedData` で別途用意する。

- `CommonExtSecurityPermissionSeedData.xml`
- `OfbizSetupGlAccountData.xml`
- `OfbizSetupProductStoreData.xml`
- `OfbizSetupSecurityPermissionSeedData.xml`
- `OfbizSetupShippingData.xml`
- `SetupData.xml`
- `SystemInfoPortletData.xml`

## 6. 種別別集計

| 種別 | 数 | 移行状況 |
| --- | ---: | --- |
| CommonExt service | 6 | 移行済み。全件 Groovy |
| entity-auto service | 0 | catalog 空であることを検証 |
| secas | 0 | `secas.xml` 空 |
| 画面 Groovy | 6 | 未移植 |
| SetupEvents simple-method | 9 | 未移植（現行 XML 実測） |
| controller request-map | 36 | API 化 3、未移植 33 |
| Screen / Form / Menu | 18 / 16 / 7 | 未移植 |
| FTL / common-js JS / ordermgr-js JS | 0 / 0 / 5 | 未移植対象 JS は5 |
| CommonExt data seed | 7 | 未移植（権限・開発データは別表現） |
| Help XML / asciidoc | 2 / 9 | 未移植 |
| view-entity | 6 | 2は JPQL 代替、4は未移植 |
