# marketing 未移植一覧（unported）

移行方針「全コンポーネントで自動CRUD・エンティティ・simpleサービスは全量、java/groovyは主要業務フロー（受注→出荷→請求など）に絞り、残りは未移植一覧に記録」に基づき、`migration/marketing-springboot` に移植しなかった要素を全件列挙する。
一覧は `docs/migration/marketing/inventory/inventory.json`（`migration/tools/ofbiz_inventory.py` で生成）とソースツリーから作成した。

## 1. 件数の集計

| 区分 | 総数 | 移植済み | 未移植 |
| --- | ---: | ---: | ---: |
| entity-auto サービス | 73 | 73 | 0 |
| simple（minilang）サービス | 27 | 27 | 0 |
| java / groovy サービス（interface 1 を含む） | 10 | 2 | 8 |
| Groovy 画面スクリプト（サービス以外） | 7 | 0 | 7 |
| Java イベント / ヘルパー（サービス以外） | 2 クラス（メソッド 7） | 0 | 2 クラス（メソッド 7） |
| minilang イベント（サービス以外） | 1 | 0 | 1 |
| ECA（secas.xml） | 11 | 5（購読状態に応じたメール送信を同期呼び出しで再現） | 6 |
| view-entity | 10 | 0 | 10 |
| request-map（controller.xml） | 146 | 0（同名サービスは API で呼べる） | 146 |
| view-map（画面） | 84 | 0 | 84 |
| Widget 定義ファイル | 33 | 0 | 33 |
| FreeMarker テンプレート | 3 | 0 | 3 |
| 既存 Groovy テスト（MarketingTests.groovy） | 1 | 0（同等の観点を JUnit で再実装） | 1 |

## 2. java / groovy サービス

| # | サービス名 | engine | location | invoke | 状態 | 理由 |
| ---: | --- | --- | --- | --- | --- | --- |
| 1 | `convertLeadToContact` | groovy | `component://marketing/src/main/groovy/org/apache/ofbiz/marketing/sfa/lead/LeadServices.groovy` | `convertLeadToContact` | 未移植 | SFA のリード→コンタクト変換。party（PartyRelationship/PartyRole）を大きく書き換える処理で、主要フロー（キャンペーン→連絡先リスト→購読→解除）の外 |
| 2 | `createAccount` | groovy | `component://marketing/src/main/groovy/org/apache/ofbiz/marketing/sfa/account/AccountServices.groovy` | `createAccount` | 未移植 | SFA のアカウント（PartyGroup）作成。party コンポーネントの createPartyGroup に依存し、主要フロー外 |
| 3 | `createLead` | groovy | `component://marketing/src/main/groovy/org/apache/ofbiz/marketing/sfa/lead/LeadServices.groovy` | `createLead` | 未移植 | SFA のリード作成。party・contactmech・ContactList 連携を含むが主要フロー外（購読は signUpForContactList で代替） |
| 4 | `createPartyContactFromVCard` | groovy | `component://marketing/src/main/groovy/org/apache/ofbiz/marketing/sfa/lead/LeadServices.groovy` | `createPartyContactFromVCard` | 未移植 | vCard 取り込み。ezvcard ライブラリと party/contactmech に依存し、主要フロー外 |
| 5 | `createPartyFromVCard` | java | `org.apache.ofbiz.sfa.vcard.VCard` | `importVCard` | 未移植 | vCard 取り込み（Java、VCard.importVCard）。ファイルアップロードと party 作成。主要フロー外 |
| 6 | `createPartyLeadFromVCard` | groovy | `component://marketing/src/main/groovy/org/apache/ofbiz/marketing/sfa/lead/LeadServices.groovy` | `createPartyLeadFromVCard` | 未移植 | vCard からのリード作成。主要フロー外 |
| 7 | `deleteContactListParty` | java | `org.apache.ofbiz.marketing.marketing.MarketingServices` | `deleteContactListParty` | 移植済み | 主要フロー（購読・解除）の中核。`MarketingJavaServices` に移植 |
| 8 | `exportVCard` | java | `org.apache.ofbiz.sfa.vcard.VCard` | `exportVCard` | 未移植 | vCard 出力（ファイルダウンロード）。UI 連携の機能で主要フロー外 |
| 9 | `importVCard` | interface | `None` | `None` | 未移植 | interface サービス（実装を持たない入出力定義のみ）。実装サービス側が未移植のため対象外 |
| 10 | `signUpForContactList` | java | `org.apache.ofbiz.marketing.marketing.MarketingServices` | `signUpForContactList` | 移植済み | 主要フロー（購読・解除）の中核。`MarketingJavaServices` に移植 |

## 3. サービス以外の Groovy / Java / minilang

| # | 名前 | 種別 | location | 理由 |
| ---: | --- | --- | --- | --- |
| 1 | GetContactListMarketingEmail | groovy（画面 action） | `applications/marketing/src/main/groovy/org/apache/ofbiz/marketing/marketing/contact/GetContactListMarketingEmail.groovy` | メール本文プレビュー用の画面データ準備。画面は移植しない |
| 2 | EmailStatusReport | groovy（レポート画面） | `applications/marketing/src/main/groovy/org/apache/ofbiz/marketing/marketing/reports/EmailStatusReport.groovy` | レポート画面。集計 API は対象外 |
| 3 | MarketingCampaignReport | groovy（レポート画面） | `applications/marketing/src/main/groovy/org/apache/ofbiz/marketing/marketing/reports/MarketingCampaignReport.groovy` | レポート画面（view-entity MarketingCampaignAndVisit/OrderHeader に依存） |
| 4 | PartyStatusReport | groovy（レポート画面） | `applications/marketing/src/main/groovy/org/apache/ofbiz/marketing/marketing/reports/PartyStatusReport.groovy` | レポート画面 |
| 5 | TrackingCodeReport | groovy（レポート画面） | `applications/marketing/src/main/groovy/org/apache/ofbiz/marketing/marketing/reports/TrackingCodeReport.groovy` | レポート画面（TrackingCodeAndVisit/OrderHeader に依存） |
| 6 | CloneLead | groovy（画面 action） | `applications/marketing/src/main/groovy/org/apache/ofbiz/marketing/sfa/CloneLead.groovy` | SFA 画面のデータ準備 |
| 7 | MergeContacts | groovy（画面 action） | `applications/marketing/src/main/groovy/org/apache/ofbiz/marketing/sfa/MergeContacts.groovy` | コンタクト統合画面のデータ準備（統合処理自体は simple サービス mergeContacts として移植済み） |
| 8 | TrackingCodeEvents（checkTrackingCodeUrlParam / checkPartnerTrackingCodeUrlParam / processTrackingCode / checkTrackingCodeCookies / checkAccessTrackingCode / removeAccesTrackingCodeCookie） | java（Web イベント、6 メソッド） | `applications/marketing/src/main/java/org/apache/ofbiz/marketing/tracking/TrackingCodeEvents.java` | ecommerce 等の firstvisit/preprocessor で URL パラメータ・Cookie からトラッキングコードを記録するサーブレット層の処理。Web フロント（ecommerce）移行時に Filter として実装する。TrackingCode / TrackingCodeVisit の CRUD は entity-auto と Entity API で利用可能 |
| 9 | ReportHelper.calcConversionRates | java（レポート用ヘルパー） | `applications/marketing/src/main/java/org/apache/ofbiz/marketing/report/ReportHelper.java` | レポート画面専用の集計 |
| 10 | importContactListParties | minilang イベント | `applications/marketing/minilang/marketing/contact/ContactListEvents.xml` | 画面からの一括取り込みイベント。個別の createContactListParty は移植済み（API の /multi で代替可能） |

## 4. ECA（servicedef/secas.xml）

| # | トリガ | 条件 | アクション | 状態 |
| ---: | --- | --- | --- | --- |
| 1 | createContactListPartyStatus commit | statusId=CLPT_PENDING | sendContactListPartyVerifyEmail（async） | 再現（同一トランザクション内で同期呼び出し。MailPort スタブ） |
| 2 | createContactListPartyStatus commit | statusId=CLPT_ACCEPTED | sendContactListPartySubscribeEmail（async） | 再現（同上） |
| 3 | updatePartyEmailAddress return | — | updatePartyEmailContactListParty | 未移植（トリガ側は party コンポーネント。サービス自体は移植済みで API から呼べる） |
| 4 | updateContactListParty commit | statusId=CLPT_REJECTED | sendContactListPartyUnSubscribeEmail | 再現（同上） |
| 5 | createContactListPartyStatus commit | statusId=CLPT_UNSUBSCRIBED | sendContactListPartyUnSubscribeEmail（async） | 再現（同上） |
| 6 | createContactListPartyStatus commit | statusId=CLPT_UNSUBS_PENDING | sendContactListPartyUnSubscribeVerifyEmail（async） | 再現（同上） |
| 7 | updateCommunicationEvent commit | statusId≠oldStatusId | updateCommStatusFromCommEvent | 未移植（トリガ側は party の CommunicationEvent。サービス自体は移植済み） |
| 8 | updateContactListCommStatus commit | statusId=COM_BOUNCED かつ contactMechId・partyId あり | updateContactListParty | 未移植（ECA のコンテキストに ContactListParty の主キー fromDate が含まれず、OFBiz でも COM_BOUNCED は CLPT_* 状態ではないため、更新内容が定まらない。バウンス処理を再設計するときに扱う） |
| 9 | createLead commit | contactListId・contactMechId あり | createContactListParty | 未移植（createLead が未移植） |
| 10 | createContact commit | contactListId・contactMechId あり | createContactListParty | 未移植（Port 化した createContact は contactListId を受け取らない。購読は signUpForContactList / createContactListParty を直接呼ぶ） |
| 11 | createAccount commit | contactListId・contactMechId あり | createContactListParty | 未移植（createAccount が未移植） |

## 5. view-entity

照会専用。JPA エンティティは `springboot-datamodel` で生成済みだが、marketing の API（Entity API）には公開していない。照会画面・レポートの移行時に Repository/クエリとして実装する。

| # | view-entity | 理由 |
| ---: | --- | --- |
| 1 | `ContactListPartyAndStatus` | 購読者一覧画面用。購読状態は ContactListParty / ContactListPartyStatus を Entity API で取得可能 |
| 2 | `SegmentGroupViewRelatedParties` | arePartiesRelatedThroughSegmentGroup は SegmentGroupRole を直接引く実装で代替 |
| 3 | `TrackingCodeAndVisit` | レポート・照会画面専用（画面は移植しない） |
| 4 | `TrackingCodeAndOrderHeader` | レポート・照会画面専用（画面は移植しない） |
| 5 | `TrackingCodeOrderAndOrderHeader` | レポート・照会画面専用（画面は移植しない） |
| 6 | `TrackingCodeOrderReturnAndReturnHeader` | レポート・照会画面専用（画面は移植しない） |
| 7 | `MarketingCampaignAndVisit` | レポート・照会画面専用（画面は移植しない） |
| 8 | `MarketingCampaignAndOrderHeader` | レポート・照会画面専用（画面は移植しない） |
| 9 | `SalesOpportunityAndRole` | レポート・照会画面専用（画面は移植しない） |
| 10 | `SalesForecastAndCustomTimePeriod` | レポート・照会画面専用（画面は移植しない） |

## 6. 画面（Widget / FreeMarker / request-map / view-map）

OFBiz 固有の Widget DSL と FreeMarker は 1:1 の移行先がないため、humanres と同じく移植しない（業務操作は `/api/marketing/services/{name}` と Entity API で提供）。UI は別途再構築する。

### 6.1 Widget 定義ファイル

| # | ファイル | 定義数（screen/form/menu/grid） |
| ---: | --- | ---: |
| 1 | `applications/marketing/widget/CommonScreens.xml` | 4 |
| 2 | `applications/marketing/widget/ContactListForms.xml` | 20 |
| 3 | `applications/marketing/widget/ContactListScreens.xml` | 17 |
| 4 | `applications/marketing/widget/DataSourceForms.xml` | 9 |
| 5 | `applications/marketing/widget/DataSourceScreens.xml` | 5 |
| 6 | `applications/marketing/widget/LookupForms.xml` | 5 |
| 7 | `applications/marketing/widget/LookupScreens.xml` | 2 |
| 8 | `applications/marketing/widget/MarketingCampaignForms.xml` | 5 |
| 9 | `applications/marketing/widget/MarketingCampaignScreens.xml` | 6 |
| 10 | `applications/marketing/widget/MarketingMenus.xml` | 9 |
| 11 | `applications/marketing/widget/MarketingReportScreens.xml` | 6 |
| 12 | `applications/marketing/widget/ReportForms.xml` | 8 |
| 13 | `applications/marketing/widget/SegmentForms.xml` | 8 |
| 14 | `applications/marketing/widget/SegmentScreens.xml` | 6 |
| 15 | `applications/marketing/widget/TrackingCodeForms.xml` | 17 |
| 16 | `applications/marketing/widget/TrackingCodeScreens.xml` | 15 |
| 17 | `applications/marketing/widget/sfa/AccountScreens.xml` | 3 |
| 18 | `applications/marketing/widget/sfa/CommonScreens.xml` | 19 |
| 19 | `applications/marketing/widget/sfa/ContactScreens.xml` | 4 |
| 20 | `applications/marketing/widget/sfa/EventScreens.xml` | 2 |
| 21 | `applications/marketing/widget/sfa/ForecastScreens.xml` | 4 |
| 22 | `applications/marketing/widget/sfa/LeadScreens.xml` | 8 |
| 23 | `applications/marketing/widget/sfa/LookupScreens.xml` | 3 |
| 24 | `applications/marketing/widget/sfa/OpportunityScreens.xml` | 4 |
| 25 | `applications/marketing/widget/sfa/SfaMenus.xml` | 17 |
| 26 | `applications/marketing/widget/sfa/forms/AccountForms.xml` | 9 |
| 27 | `applications/marketing/widget/sfa/forms/CommonForms.xml` | 2 |
| 28 | `applications/marketing/widget/sfa/forms/ContactForms.xml` | 7 |
| 29 | `applications/marketing/widget/sfa/forms/EventForms.xml` | 3 |
| 30 | `applications/marketing/widget/sfa/forms/ForecastForms.xml` | 5 |
| 31 | `applications/marketing/widget/sfa/forms/LeadForms.xml` | 12 |
| 32 | `applications/marketing/widget/sfa/forms/LookupForms.xml` | 3 |
| 33 | `applications/marketing/widget/sfa/forms/OpportunityForms.xml` | 4 |

### 6.2 FreeMarker テンプレート

| # | ファイル |
| ---: | --- |
| 1 | `applications/marketing/template/contact/ContactCommunicationPreview.ftl` |
| 2 | `applications/marketing/template/contact/MergeContacts.ftl` |
| 3 | `applications/marketing/template/lead/MergeLeads.ftl` |

### 6.3 view-map（画面）

| # | view-map | webapp | page |
| ---: | --- | --- | --- |
| 1 | `AddRelatedCompany` | sfa | `component://marketing/widget/sfa/LeadScreens.xml#AddRelatedCompany` |
| 2 | `CloneLead` | sfa | `component://marketing/widget/sfa/LeadScreens.xml#CloneLead` |
| 3 | `ContactListOptOut` | marketing | `component://marketing/widget/ContactListScreens.xml#OptOutResponse` |
| 4 | `ContactMechTypeOnly` | marketing | `component://marketing/widget/sfa/AccountScreens.xml#ContactMechTypeOnly` |
| 5 | `ContactMechTypeOnly` | sfa | `component://marketing/widget/sfa/AccountScreens.xml#ContactMechTypeOnly` |
| 6 | `ConvertLead` | sfa | `component://marketing/widget/sfa/LeadScreens.xml#ConvertLead` |
| 7 | `EditContactList` | marketing | `component://marketing/widget/ContactListScreens.xml#EditContactList` |
| 8 | `EditContactListCommEvent` | marketing | `component://marketing/widget/ContactListScreens.xml#EditContactListCommEvent` |
| 9 | `EditContactListParty` | marketing | `component://marketing/widget/ContactListScreens.xml#EditContactListParty` |
| 10 | `EditDataSource` | marketing | `component://marketing/widget/DataSourceScreens.xml#EditDataSource` |
| 11 | `EditDataSourceType` | marketing | `component://marketing/widget/DataSourceScreens.xml#EditDataSourceType` |
| 12 | `EditEvent` | sfa | `component://marketing/widget/sfa/EventScreens.xml#EditEvent` |
| 13 | `EditMarketingCampaign` | marketing | `component://marketing/widget/MarketingCampaignScreens.xml#EditMarketingCampaign` |
| 14 | `EditMarketingCampaignRoles` | marketing | `component://marketing/widget/MarketingCampaignScreens.xml#EditMarketingCampaignRoles` |
| 15 | `EditSalesForecast` | sfa | `component://marketing/widget/sfa/ForecastScreens.xml#EditSalesForecast` |
| 16 | `EditSalesForecastDetail` | sfa | `component://marketing/widget/sfa/ForecastScreens.xml#EditSalesForecastDetail` |
| 17 | `EditSalesOpportunity` | sfa | `component://marketing/widget/sfa/OpportunityScreens.xml#EditSalesOpportunity` |
| 18 | `EditTrackingCode` | marketing | `component://marketing/widget/TrackingCodeScreens.xml#EditTrackingCode` |
| 19 | `EditTrackingCodeType` | marketing | `component://marketing/widget/TrackingCodeScreens.xml#EditTrackingCodeType` |
| 20 | `EmailStatusReport` | marketing | `component://marketing/widget/MarketingReportScreens.xml#EmailStatusReport` |
| 21 | `Events` | sfa | `component://marketing/widget/sfa/EventScreens.xml#main` |
| 22 | `FindAccounts` | sfa | `component://marketing/widget/sfa/AccountScreens.xml#FindAccounts` |
| 23 | `FindContactListCommEvents` | marketing | `component://marketing/widget/ContactListScreens.xml#FindContactListCommEvents` |
| 24 | `FindContactListParties` | marketing | `component://marketing/widget/ContactListScreens.xml#FindContactListParties` |
| 25 | `FindContactLists` | marketing | `component://marketing/widget/ContactListScreens.xml#FindContactLists` |
| 26 | `FindContacts` | sfa | `component://marketing/widget/sfa/ContactScreens.xml#FindContacts` |
| 27 | `FindDataSource` | marketing | `component://marketing/widget/DataSourceScreens.xml#ListDataSource` |
| 28 | `FindDataSourceType` | marketing | `component://marketing/widget/DataSourceScreens.xml#ListDataSourceType` |
| 29 | `FindImportContactListParties` | marketing | `component://marketing/widget/ContactListScreens.xml#FindImportContactListParties` |
| 30 | `FindLeads` | sfa | `component://marketing/widget/sfa/LeadScreens.xml#FindLeads` |
| 31 | `FindMarketingCampaign` | marketing | `component://marketing/widget/MarketingCampaignScreens.xml#ListMarketingCampaign` |
| 32 | `FindSalesForecast` | sfa | `component://marketing/widget/sfa/ForecastScreens.xml#FindSalesForecast` |
| 33 | `FindSalesOpportunity` | sfa | `component://marketing/widget/sfa/OpportunityScreens.xml#FindSalesOpportunity` |
| 34 | `FindSegmentGroup` | marketing | `component://marketing/widget/SegmentScreens.xml#FindSegmentGroup` |
| 35 | `FindTrackingCode` | marketing | `component://marketing/widget/TrackingCodeScreens.xml#ListTrackingCode` |
| 36 | `FindTrackingCodeOrders` | marketing | `component://marketing/widget/TrackingCodeScreens.xml#ListTrackingCodeOrders` |
| 37 | `FindTrackingCodeType` | marketing | `component://marketing/widget/TrackingCodeScreens.xml#ListTrackingCodeType` |
| 38 | `FindTrackingCodeVisits` | marketing | `component://marketing/widget/TrackingCodeScreens.xml#ListTrackingCodeVisits` |
| 39 | `ListContactListCommEvents` | marketing | `component://marketing/widget/ContactListScreens.xml#ListContactListCommEvents` |
| 40 | `ListContactListParties` | marketing | `component://marketing/widget/ContactListScreens.xml#ListContactListParties` |
| 41 | `ListContactListParty` | marketing | `component://marketing/widget/ContactListScreens.xml#ListContactListParties` |
| 42 | `ListContactLists` | marketing | `component://marketing/widget/ContactListScreens.xml#ListContactLists` |
| 43 | `ListMarketingCampaignRoles` | marketing | `component://marketing/widget/MarketingCampaignScreens.xml#ListMarketingCampaignRoles` |
| 44 | `ListPartyCommEvents` | sfa | `component://marketing/widget/sfa/OpportunityScreens.xml#OpportunityCommEvent` |
| 45 | `LookupAccountLeads` | sfa | `component://marketing/widget/sfa/LookupScreens.xml#LookupAccountLeads` |
| 46 | `LookupAccounts` | sfa | `component://marketing/widget/sfa/LookupScreens.xml#LookupAccounts` |
| 47 | `LookupCommEvent` | marketing | `component://party/widget/partymgr/LookupScreens.xml#LookupCommEvent` |
| 48 | `LookupContactList` | marketing | `component://marketing/widget/ContactListScreens.xml#LookupContactList` |
| 49 | `LookupContactMech` | marketing | `component://party/widget/partymgr/LookupScreens.xml#LookupContactMech` |
| 50 | `LookupLeads` | sfa | `component://marketing/widget/sfa/LookupScreens.xml#LookupLeads` |
| 51 | `LookupPartyClassificationGroup` | marketing | `component://party/widget/partymgr/LookupScreens.xml#LookupPartyClassificationGroup` |
| 52 | `LookupPartyName` | marketing | `component://party/widget/partymgr/LookupScreens.xml#LookupPartyName` |
| 53 | `LookupPreferredContactMech` | marketing | `component://marketing/widget/ContactListScreens.xml#LookupPreferredContactMech` |
| 54 | `LookupProduct` | sfa | `component://product/widget/catalog/LookupScreens.xml#LookupProduct` |
| 55 | `LookupProductCategory` | sfa | `component://product/widget/catalog/LookupScreens.xml#LookupProductCategory` |
| 56 | `LookupProductStore` | marketing | `component://product/widget/catalog/LookupScreens.xml#LookupProductStore` |
| 57 | `LookupSalesForecast` | sfa | `component://marketing/widget/LookupScreens.xml#LookupSalesForecast` |
| 58 | `LookupSegmentGroup` | marketing | `component://marketing/widget/LookupScreens.xml#LookupSegmentGroup` |
| 59 | `MarketingCampaignReport` | marketing | `component://marketing/widget/MarketingReportScreens.xml#MarketingCampaignReport` |
| 60 | `MarketingReport` | marketing | `component://marketing/widget/MarketingReportScreens.xml#MarketingReportList` |
| 61 | `MergeContacts` | sfa | `component://marketing/widget/sfa/ContactScreens.xml#MergeContacts` |
| 62 | `MergeLeads` | sfa | `component://marketing/widget/sfa/LeadScreens.xml#MergeLeads` |
| 63 | `NewAccount` | sfa | `component://marketing/widget/sfa/AccountScreens.xml#NewAccount` |
| 64 | `NewContact` | sfa | `component://marketing/widget/sfa/ContactScreens.xml#NewContact` |
| 65 | `NewContactFromVCard` | sfa | `component://marketing/widget/sfa/ContactScreens.xml#NewContactFromVCard` |
| 66 | `NewLead` | sfa | `component://marketing/widget/sfa/LeadScreens.xml#NewLead` |
| 67 | `NewLeadFromVCard` | sfa | `component://marketing/widget/sfa/LeadScreens.xml#NewLeadFromVCard` |
| 68 | `PartyStatusReport` | marketing | `component://marketing/widget/MarketingReportScreens.xml#PartyStatusReport` |
| 69 | `PreviewContactListCommEvent` | marketing | `component://marketing/widget/ContactListScreens.xml#PreviewContactListCommEvent` |
| 70 | `TrackingCodeReport` | marketing | `component://marketing/widget/MarketingReportScreens.xml#TrackingCodeReport` |
| 71 | `ViewAccountLeadProfile` | sfa | `component://marketing/widget/sfa/CommonScreens.xml#ViewAccountLeadProfile` |
| 72 | `ViewAccountProfile` | sfa | `component://marketing/widget/sfa/CommonScreens.xml#ViewAccountProfile` |
| 73 | `ViewContactProfile` | sfa | `component://marketing/widget/sfa/CommonScreens.xml#ViewContactProfile` |
| 74 | `ViewLeadProfile` | sfa | `component://marketing/widget/sfa/CommonScreens.xml#ViewLeadProfile` |
| 75 | `ViewPartiesCreatedByVCard` | sfa | `component://marketing/widget/sfa/CommonScreens.xml#ViewPartiesCreatedByVCard` |
| 76 | `ViewSalesOpportunity` | sfa | `component://marketing/widget/sfa/OpportunityScreens.xml#ViewSalesOpportunity` |
| 77 | `WebSiteContactList` | marketing | `component://marketing/widget/ContactListScreens.xml#WebSiteContactList` |
| 78 | `listSegmentGroupClass` | marketing | `component://marketing/widget/SegmentScreens.xml#listSegmentGroupClass` |
| 79 | `listSegmentGroupGeo` | marketing | `component://marketing/widget/SegmentScreens.xml#listSegmentGroupGeo` |
| 80 | `listSegmentGroupRole` | marketing | `component://marketing/widget/SegmentScreens.xml#listSegmentGroupRole` |
| 81 | `main` | marketing | `component://marketing/widget/CommonScreens.xml#Main` |
| 82 | `main` | sfa | `component://marketing/widget/sfa/CommonScreens.xml#main` |
| 83 | `viewSegmentGroup` | marketing | `component://marketing/widget/SegmentScreens.xml#EditSegmentGroup` |
| 84 | `viewprofile` | sfa | `component://marketing/widget/sfa/CommonScreens.xml#ViewProfile` |

### 6.4 request-map

サービスを呼ぶ request-map は、同名サービスが移植済み（entity-auto / simple / 主要 Java）であれば API から同じ操作ができる。

| # | request-map | webapp | event | API での代替 |
| ---: | --- | --- | --- | --- |
| 1 | `AddRelatedCompany` | sfa | —（画面表示のみ） | — |
| 2 | `CloneLead` | sfa | —（画面表示のみ） | — |
| 3 | `ContactMechTypeOnly` | marketing | —（画面表示のみ） | — |
| 4 | `ContactMechTypeOnly` | sfa | —（画面表示のみ） | — |
| 5 | `ConvertLead` | sfa | —（画面表示のみ） | — |
| 6 | `EditContactList` | marketing | —（画面表示のみ） | — |
| 7 | `EditContactListCommEvent` | marketing | —（画面表示のみ） | — |
| 8 | `EditContactListParty` | marketing | —（画面表示のみ） | — |
| 9 | `EditDataSource` | marketing | —（画面表示のみ） | — |
| 10 | `EditDataSourceType` | marketing | —（画面表示のみ） | — |
| 11 | `EditEvent` | sfa | —（画面表示のみ） | — |
| 12 | `EditMarketingCampaign` | marketing | —（画面表示のみ） | — |
| 13 | `EditMarketingCampaignRoles` | marketing | —（画面表示のみ） | — |
| 14 | `EditSalesForecast` | sfa | —（画面表示のみ） | — |
| 15 | `EditSalesForecastDetail` | sfa | —（画面表示のみ） | — |
| 16 | `EditSalesOpportunity` | sfa | —（画面表示のみ） | — |
| 17 | `EditTrackingCode` | marketing | —（画面表示のみ） | — |
| 18 | `EditTrackingCodeType` | marketing | —（画面表示のみ） | — |
| 19 | `EmailStatusReport` | marketing | —（画面表示のみ） | — |
| 20 | `Events` | sfa | —（画面表示のみ） | — |
| 21 | `FindAccounts` | sfa | —（画面表示のみ） | — |
| 22 | `FindContactListCommEvents` | marketing | —（画面表示のみ） | — |
| 23 | `FindContactListParties` | marketing | —（画面表示のみ） | — |
| 24 | `FindContactLists` | marketing | —（画面表示のみ） | — |
| 25 | `FindContacts` | sfa | —（画面表示のみ） | — |
| 26 | `FindDataSource` | marketing | —（画面表示のみ） | — |
| 27 | `FindDataSourceType` | marketing | —（画面表示のみ） | — |
| 28 | `FindImportContactListParties` | marketing | —（画面表示のみ） | — |
| 29 | `FindLeads` | sfa | —（画面表示のみ） | — |
| 30 | `FindMarketingCampaign` | marketing | —（画面表示のみ） | — |
| 31 | `FindSalesForecast` | sfa | —（画面表示のみ） | — |
| 32 | `FindSalesOpportunity` | sfa | —（画面表示のみ） | — |
| 33 | `FindSegmentGroup` | marketing | —（画面表示のみ） | — |
| 34 | `FindTrackingCode` | marketing | —（画面表示のみ） | — |
| 35 | `FindTrackingCodeOrders` | marketing | —（画面表示のみ） | — |
| 36 | `FindTrackingCodeType` | marketing | —（画面表示のみ） | — |
| 37 | `FindTrackingCodeVisits` | marketing | —（画面表示のみ） | — |
| 38 | `ListContactListCommEvents` | marketing | —（画面表示のみ） | — |
| 39 | `ListContactListParties` | marketing | —（画面表示のみ） | — |
| 40 | `ListContactLists` | marketing | —（画面表示のみ） | — |
| 41 | `ListMarketingCampaignRoles` | marketing | —（画面表示のみ） | — |
| 42 | `LookupAccountLeads` | sfa | —（画面表示のみ） | — |
| 43 | `LookupAccounts` | sfa | —（画面表示のみ） | — |
| 44 | `LookupCommEvent` | marketing | —（画面表示のみ） | — |
| 45 | `LookupContactList` | marketing | —（画面表示のみ） | — |
| 46 | `LookupContactMech` | marketing | —（画面表示のみ） | — |
| 47 | `LookupLeads` | sfa | —（画面表示のみ） | — |
| 48 | `LookupPartyClassificationGroup` | marketing | —（画面表示のみ） | — |
| 49 | `LookupPartyName` | marketing | —（画面表示のみ） | — |
| 50 | `LookupPreferredContactMech` | marketing | —（画面表示のみ） | — |
| 51 | `LookupProduct` | sfa | —（画面表示のみ） | — |
| 52 | `LookupProductCategory` | sfa | —（画面表示のみ） | — |
| 53 | `LookupProductStore` | marketing | —（画面表示のみ） | — |
| 54 | `LookupSalesForecast` | sfa | —（画面表示のみ） | — |
| 55 | `LookupSegmentGroup` | marketing | —（画面表示のみ） | — |
| 56 | `MarketingCampaignReport` | marketing | —（画面表示のみ） | — |
| 57 | `MarketingReport` | marketing | —（画面表示のみ） | — |
| 58 | `MergeContacts` | sfa | —（画面表示のみ） | — |
| 59 | `MergeLeads` | sfa | —（画面表示のみ） | — |
| 60 | `NewAccount` | sfa | —（画面表示のみ） | — |
| 61 | `NewContact` | sfa | —（画面表示のみ） | — |
| 62 | `NewContactFromVCard` | sfa | —（画面表示のみ） | — |
| 63 | `NewLead` | sfa | —（画面表示のみ） | — |
| 64 | `NewLeadFromVCard` | sfa | —（画面表示のみ） | — |
| 65 | `PartyStatusReport` | marketing | —（画面表示のみ） | — |
| 66 | `PreviewContactListCommEvent` | marketing | —（画面表示のみ） | — |
| 67 | `TrackingCodeReport` | marketing | —（画面表示のみ） | — |
| 68 | `ViewAccountLeadProfile` | sfa | —（画面表示のみ） | — |
| 69 | `ViewAccountProfile` | sfa | —（画面表示のみ） | — |
| 70 | `ViewContactProfile` | sfa | —（画面表示のみ） | — |
| 71 | `ViewLeadProfile` | sfa | —（画面表示のみ） | — |
| 72 | `ViewSalesOpportunity` | sfa | —（画面表示のみ） | — |
| 73 | `closeSalesOpportunity` | sfa | service:updateSalesOpportunity | `POST /api/marketing/services/updateSalesOpportunity` |
| 74 | `contactListOptOut` | marketing | service:updateContactListPartyNoUserLogin | `POST /api/marketing/services/updateContactListPartyNoUserLogin` |
| 75 | `convertLead` | sfa | service:convertLeadToContact | 未移植サービス |
| 76 | `createAccount` | sfa | service:createAccount | 未移植サービス |
| 77 | `createCommunicationEvent` | sfa | service:createCommunicationEvent | 他コンポーネントのサービス |
| 78 | `createContact` | sfa | service:createContact | `POST /api/marketing/services/createContact` |
| 79 | `createContactFromVCard` | sfa | service:createPartyContactFromVCard | 未移植サービス |
| 80 | `createContactList` | marketing | service:createContactList | `POST /api/marketing/services/createContactList` |
| 81 | `createContactListCommEvent` | marketing | service:createCommunicationEvent | 他コンポーネントのサービス |
| 82 | `createContactListParty` | marketing | service:createContactListParty | `POST /api/marketing/services/createContactListParty` |
| 83 | `createDataSource` | marketing | service:createDataSource | 他コンポーネントのサービス |
| 84 | `createDataSourceType` | marketing | service:createDataSourceType | 他コンポーネントのサービス |
| 85 | `createLead` | sfa | service:createLead | 未移植サービス |
| 86 | `createLeadFromVCard` | sfa | service:createPartyLeadFromVCard | 未移植サービス |
| 87 | `createLeadPartyDataSource` | sfa | service:createPartyDataSource | 他コンポーネントのサービス |
| 88 | `createMarketingCampaign` | marketing | service:createMarketingCampaign | `POST /api/marketing/services/createMarketingCampaign` |
| 89 | `createMarketingCampaignRole` | marketing | service:createMarketingCampaignRole | `POST /api/marketing/services/createMarketingCampaignRole` |
| 90 | `createSalesForecast` | sfa | service:createSalesForecast | `POST /api/marketing/services/createSalesForecast` |
| 91 | `createSalesForecastDetail` | sfa | service:createSalesForecastDetail | `POST /api/marketing/services/createSalesForecastDetail` |
| 92 | `createSalesOpportunity` | sfa | service:createSalesOpportunity | `POST /api/marketing/services/createSalesOpportunity` |
| 93 | `createSegmentGroup` | marketing | service:createSegmentGroup | `POST /api/marketing/services/createSegmentGroup` |
| 94 | `createSegmentGroupClassification` | marketing | service:createSegmentGroupClassification | `POST /api/marketing/services/createSegmentGroupClassification` |
| 95 | `createSegmentGroupGeo` | marketing | service:createSegmentGroupGeo | `POST /api/marketing/services/createSegmentGroupGeo` |
| 96 | `createSegmentGroupRole` | marketing | service:createSegmentGroupRole | `POST /api/marketing/services/createSegmentGroupRole` |
| 97 | `createTrackingCode` | marketing | service:createTrackingCode | `POST /api/marketing/services/createTrackingCode` |
| 98 | `createTrackingCodeType` | marketing | service:createTrackingCodeType | `POST /api/marketing/services/createTrackingCodeType` |
| 99 | `createVCardFromContact` | sfa | service:exportVCard | 未移植サービス |
| 100 | `createWebSiteContactList` | marketing | service:createWebSiteContactList | `POST /api/marketing/services/createWebSiteContactList` |
| 101 | `deleteDataSource` | marketing | service:deleteDataSource | 他コンポーネントのサービス |
| 102 | `deleteDataSourceType` | marketing | service:deleteDataSourceType | 他コンポーネントのサービス |
| 103 | `deleteSalesForecastDetail` | sfa | service:deleteSalesForecastDetail | `POST /api/marketing/services/deleteSalesForecastDetail` |
| 104 | `deleteSegmentGroup` | marketing | service:deleteSegmentGroup | `POST /api/marketing/services/deleteSegmentGroup` |
| 105 | `deleteSegmentGroupClassification` | marketing | service:deleteSegmentGroupClassification | `POST /api/marketing/services/deleteSegmentGroupClassification` |
| 106 | `deleteSegmentGroupGeo` | marketing | service:deleteSegmentGroupGeo | `POST /api/marketing/services/deleteSegmentGroupGeo` |
| 107 | `deleteSegmentGroupRole` | marketing | service:deleteSegmentGroupRole | `POST /api/marketing/services/deleteSegmentGroupRole` |
| 108 | `deleteTrackingCode` | marketing | service:deleteTrackingCode | `POST /api/marketing/services/deleteTrackingCode` |
| 109 | `deleteTrackingCodeType` | marketing | service:deleteTrackingCodeType | `POST /api/marketing/services/deleteTrackingCodeType` |
| 110 | `deleteWebSiteContactList` | marketing | service:deleteWebSiteContactList | `POST /api/marketing/services/deleteWebSiteContactList` |
| 111 | `expireContactListParty` | marketing | service:updateContactListParty | `POST /api/marketing/services/updateContactListParty` |
| 112 | `importContactListParties` | marketing | simple:importContactListParties | —（イベント未移植） |
| 113 | `listSegmentGroupClass` | marketing | —（画面表示のみ） | — |
| 114 | `listSegmentGroupGeo` | marketing | —（画面表示のみ） | — |
| 115 | `listSegmentGroupRole` | marketing | —（画面表示のみ） | — |
| 116 | `main` | marketing | —（画面表示のみ） | — |
| 117 | `main` | sfa | —（画面表示のみ） | — |
| 118 | `mergeContacts` | sfa | service:mergeContacts | `POST /api/marketing/services/mergeContacts` |
| 119 | `mergeLeads` | sfa | —（画面表示のみ） | — |
| 120 | `quickAddContact` | sfa | service:createContact | `POST /api/marketing/services/createContact` |
| 121 | `quickAddLead` | sfa | service:createLead | 未移植サービス |
| 122 | `removeContactList` | marketing | service:removeContactList | `POST /api/marketing/services/removeContactList` |
| 123 | `removeContactListCommEvent` | marketing | service:deleteCommunicationEvent | 他コンポーネントのサービス |
| 124 | `removeContactListParty` | marketing | service:deleteContactListParty | `POST /api/marketing/services/deleteContactListParty` |
| 125 | `removeMarketingCampaign` | marketing | service:deleteMarketingCampaign | `POST /api/marketing/services/deleteMarketingCampaign` |
| 126 | `removeMarketingCampaignRole` | marketing | service:deleteMarketingCampaignRole | `POST /api/marketing/services/deleteMarketingCampaignRole` |
| 127 | `updateContactList` | marketing | service:updateContactList | `POST /api/marketing/services/updateContactList` |
| 128 | `updateContactListCommEvent` | marketing | service:updateCommunicationEvent | 他コンポーネントのサービス |
| 129 | `updateContactListParty` | marketing | service:updateContactListParty | `POST /api/marketing/services/updateContactListParty` |
| 130 | `updateDataSource` | marketing | service:updateDataSource | 他コンポーネントのサービス |
| 131 | `updateDataSourceType` | marketing | service:updateDataSourceType | 他コンポーネントのサービス |
| 132 | `updateMarketingCampaign` | marketing | service:updateMarketingCampaign | `POST /api/marketing/services/updateMarketingCampaign` |
| 133 | `updateSalesForecast` | sfa | service:updateSalesForecast | `POST /api/marketing/services/updateSalesForecast` |
| 134 | `updateSalesForecastDetail` | sfa | service:updateSalesForecastDetail | `POST /api/marketing/services/updateSalesForecastDetail` |
| 135 | `updateSalesOpportunity` | sfa | service:updateSalesOpportunity | `POST /api/marketing/services/updateSalesOpportunity` |
| 136 | `updateSegmentGroup` | marketing | service:updateSegmentGroup | `POST /api/marketing/services/updateSegmentGroup` |
| 137 | `updateSegmentGroupClassification` | marketing | service:updateSegmentGroupClassification | `POST /api/marketing/services/updateSegmentGroupClassification` |
| 138 | `updateSegmentGroupGeo` | marketing | service:updateSegmentGroupGeo | `POST /api/marketing/services/updateSegmentGroupGeo` |
| 139 | `updateSegmentGroupRole` | marketing | service:updateSegmentGroupRole | `POST /api/marketing/services/updateSegmentGroupRole` |
| 140 | `updateTaskAssigment` | sfa | service:updateTaskAssigment | 他コンポーネントのサービス |
| 141 | `updateTrackingCode` | marketing | service:updateTrackingCode | `POST /api/marketing/services/updateTrackingCode` |
| 142 | `updateTrackingCodeType` | marketing | service:updateTrackingCodeType | `POST /api/marketing/services/updateTrackingCodeType` |
| 143 | `updateWebSiteContactList` | marketing | service:updateWebSiteContactList | `POST /api/marketing/services/updateWebSiteContactList` |
| 144 | `view` | marketing | —（画面表示のみ） | — |
| 145 | `viewSegmentGroup` | marketing | —（画面表示のみ） | — |
| 146 | `webSiteContactList` | marketing | —（画面表示のみ） | — |
