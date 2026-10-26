# Entity inventory

## View entities

### WorkEffortAndChild

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:29`
- Member entities: WEP = WorkEffort, WEPH = WorkEffort

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WEP | WEPH | workEffortId → workEffortParentId |

### WorkEffortAndFulfillment

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:48`
- Member entities: WEF = WorkEffort, WRF = WorkRequirementFulfillment, REQ = Requirement

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WEF | WRF | workEffortId → — |
| WRF | REQ | requirementId → — |

### WorkEffortAndFixedAssetAssign

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:170`
- Member entities: WEFAA = WorkEffortFixedAssetAssign, WE = WorkEffort, FA = FixedAsset

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WEFAA | WE | workEffortId → — |
| WEFAA | FA | fixedAssetId → — |

### WorkEffortAndPartyAssign

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:197`
- Member entities: WE = WorkEffort, WEPA = WorkEffortPartyAssignment

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WE | WEPA | workEffortId → — |

### WorkEffortAndPartyAssignAndType

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:236`
- Member entities: WE = WorkEffort, WEPA = WorkEffortPartyAssignment, WETY = WorkEffortType

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WE | WEPA | workEffortId → — |
| WE | WETY | workEffortTypeId → — |

### WorkEffortPartyAssignAndRoleType

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:280`
- Member entities: WEPA = WorkEffortPartyAssignment, RT = RoleType

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WEPA | RT | roleTypeId → — |

### WorkEffortAssocView

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:294`
- Member entities: WA = WorkEffortAssoc, WETO = WorkEffort

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WA | WETO | workEffortIdTo → workEffortId |

### WorkEffortAssocFromView

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:317`
- Member entities: WEA = WorkEffortAssoc, WEFROM = WorkEffort

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WEA | WEFROM | workEffortIdFrom → workEffortId |

### WorkEffortAssocToView

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:330`
- Member entities: WEA = WorkEffortAssoc, WETO = WorkEffort

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WEA | WETO | workEffortIdTo → workEffortId |

### WorkEffortFindView

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:344`
- Member entities: WE = WorkEffort, WEA = WorkEffortAssoc, WEPA = WorkEffortPartyAssignment, WEFAA = WorkEffortFixedAssetAssign

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WE | WEA | workEffortId → workEffortIdTo |
| WE | WEPA | workEffortId → — |
| WE | WEFAA | workEffortId → — |

### WorkEffortNoteAndData

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:369`
- Member entities: WEN = WorkEffortNote, ND = NoteData

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WEN | ND | noteId → — |

### WorkEffortPartyAssignByGroup

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:397`
- Member entities: WE = WorkEffort, WEPA = WorkEffortPartyAssignment, PREL = PartyRelationship

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WE | WEPA | workEffortId → — |
| WEPA | PREL | partyId → partyIdFrom |

### WorkEffortPartyAssignByRole

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:452`
- Member entities: WE = WorkEffort, WEPA = WorkEffortPartyAssignment, PR = PartyRole

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WE | WEPA | workEffortId → — |
| WEPA | PR | roleTypeId → — |

### WorkEffortAndGoods

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:497`
- Member entities: WE = WorkEffort, WEGS = WorkEffortGoodStandard

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WE | WEGS | workEffortId → — |

### WorkEffortProductGoods

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:511`
- Member entities: WE = WorkEffort, WEGS = WorkEffortGoodStandard, PROD = Product

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WE | WEGS | workEffortId → — |
| WEGS | PROD | productId → — |

### WorkEffortAndContentDataResource

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:536`
- Member entities: WECO = WorkEffortContent, CO = Content, DR = DataResource

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WECO | CO | contentId → — |
| CO | DR | dataResourceId → — |

### WorkEffortAndInventoryAssign

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:582`
- Member entities: WEIA = WorkEffortInventoryAssign, II = InventoryItem

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WEIA | II | inventoryItemId → — |

### WorkEffortAndInventoryProduced

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:596`
- Member entities: WEIP = WorkEffortInventoryProduced, II = InventoryItem

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WEIP | II | inventoryItemId → — |

### WorkEffortInvResAndItem

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:611`
- Member entities: WEIR = WorkEffortInvRes, II = InventoryItem, WE = WorkEffort

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WEIR | II | inventoryItemId → — |
| WEIR | WE | workEffortId → — |

### WorkEffortInventoryAssignAndItem

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:631`
- Member entities: WEIA = WorkEffortInventoryAssign, II = InventoryItem

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WEIA | II | inventoryItemId → — |

### WorkEffortPartyAssignView

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:642`
- Member entities: WEPA = WorkEffortPartyAssignment, PNV = PartyNameView

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WEPA | PNV | partyId → — |

### WorkEffortCommunicationEventView

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:691`
- Member entities: CEWE = CommunicationEventWorkEff, CE = CommunicationEvent

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| CEWE | CE | communicationEventId → — |

### WorkEffortShoppingListView

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:709`
- Member entities: SLWE = ShoppingListWorkEffort, SL = ShoppingList, SLT = ShoppingListType

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| SLWE | SL | shoppingListId → — |
| SL | SLT | shoppingListTypeId → — |

### WorkEffortQuoteView

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:732`
- Member entities: QWE = QuoteWorkEffort, Q = Quote, SI = StatusItem

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| QWE | Q | quoteId → — |
| Q | SI | statusId → — |

### WorkEffortOrderHeaderView

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:755`
- Member entities: OHWE = OrderHeaderWorkEffort, OH = OrderHeader, OT = OrderType, SI = StatusItem

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| OHWE | OH | orderId → — |
| OH | OT | orderTypeId → — |
| OH | SI | statusId → — |

### WorkEffortCustRequestView

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:789`
- Member entities: CRWE = CustRequestWorkEffort, CR = CustRequest, CRT = CustRequestType, SI = StatusItem, WE = WorkEffort

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| CRWE | CR | custRequestId → — |
| CRWE | WE | workEffortId → — |
| CR | CRT | custRequestTypeId → — |
| CR | SI | statusId → — |

### WorkEffortCustRequestItemView

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:841`
- Member entities: CRIWE = CustRequestItemWorkEffort, CRI = CustRequestItem, SI = StatusItem

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| CRIWE | CRI | custRequestId → —, custRequestItemSeqId → — |
| CRI | SI | statusId → — |

### WorkEffortRequirementView

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:869`
- Member entities: WRF = WorkRequirementFulfillment, REQ = Requirement, SI = StatusItem, WRFT = WorkReqFulfType

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WRF | REQ | requirementId → — |
| REQ | SI | statusId → — |
| WRF | WRFT | workReqFulfTypeId → — |

### WorkEffortAndSalesOpportunity

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:904`
- Member entities: SOWE = SalesOpportunityWorkEffort, SO = SalesOpportunity, WE = WorkEffort

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| SOWE | SO | salesOpportunityId → — |
| SOWE | WE | workEffortId → — |

### WorkEffortContentAndInfo

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:931`
- Member entities: WC = WorkEffortContent, CO = Content, DR = DataResource

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WC | CO | contentId → — |
| CO | DR | dataResourceId → — |

### WorkEffortContactMechView

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:963`
- Member entities: WECM = WorkEffortContactMech, CM = ContactMech

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WECM | CM | contactMechId → — |

### WorkEffortAndContactMech

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:974`
- Member entities: WCM = WorkEffortContactMech, CMD = ContactMechDetail

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WCM | CMD | contactMechId → — |

### WorkEffortAndTimeEntry

- Package: org.apache.ofbiz.workeffort.workeffort
- Source: `applications/workeffort/entitydef/entitymodel_view.xml:1005`
- Member entities: WE = WorkEffort, TE = TimeEntry

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| WE | TE | workEffortId → — |
