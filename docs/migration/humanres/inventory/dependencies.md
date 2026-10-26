# Dependency inventory

## Called services and simple methods

| Kind | Name | Defined by | Definition | Source | Line |
| --- | --- | --- | --- | --- | --- |
| call-service | createPartyRelationship | party | applications/party/servicedef/services.xml | applications/humanres/minilang/HumanResEvents.xml | 29 |
| call-service | deleteEmplPositionFulfillment | humanres | applications/humanres/servicedef/services.xml | applications/humanres/minilang/HumanResEvents.xml | 39 |
| call-service | deleteEmplPosition | humanres | applications/humanres/servicedef/services.xml | applications/humanres/minilang/HumanResEvents.xml | 43 |
| call-service | deletePartyRelationship | party | applications/party/servicedef/services.xml | applications/humanres/minilang/HumanResEvents.xml | 53 |
| call-service | createWorkEffortAndPartyAssign | workeffort | applications/workeffort/servicedef/services.xml | applications/humanres/minilang/HumanResEvents.xml | 80 |
| call-service | ensurePartyRoleTo | party | applications/party/servicedef/services.xml | applications/humanres/minilang/HumanResServices.xml | 27 |
| call-service | createPayHistory | humanres | applications/humanres/servicedef/services.xml | applications/humanres/minilang/HumanResServices.xml | 40 |
| call-service | expirePayHistory | humanres | applications/humanres/servicedef/services.xml | applications/humanres/minilang/HumanResServices.xml | 62 |
| call-service | createPayHistory | humanres | applications/humanres/servicedef/services.xml | applications/humanres/minilang/HumanResServices.xml | 71 |
| call-simple-method | createPersonRoleAndContactMechs | party | component://party/minilang/party/PartySimpleMethods.xml | applications/humanres/minilang/HumanResServices.xml | 103 |
| call-service | createPartyRelationship | party | applications/party/servicedef/services.xml | applications/humanres/minilang/HumanResServices.xml | 116 |
| call-service | updateRateAmount | accounting | applications/accounting/servicedef/services_rate.xml | applications/humanres/minilang/HumanResServices.xml | 142 |
| call-service | expireRateAmount | accounting | applications/accounting/servicedef/services_rate.xml | applications/humanres/minilang/HumanResServices.xml | 153 |
| call-simple-method | genericBasePermissionCheck | common | component://common/minilang/permission/CommonPermissionServices.xml | applications/humanres/minilang/permission/HumanResPermissionServices.xml | 25 |

## External entity references

| Entity | Owning component | Owning entitymodel | Source | Line |
| --- | --- | --- | --- | --- |
| AgreementEmploymentAppl | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/EmploymentForms.xml | 434 |
| AgreementEmploymentAppl | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/LookupForms.xml | 234 |
| Budget | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/humanres/widget/forms/LookupForms.xml | 25 |
| Budget | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/humanres/widget/forms/LookupForms.xml | 47 |
| BudgetItem | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/humanres/widget/forms/LookupForms.xml | 54 |
| BudgetItem | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/humanres/widget/forms/LookupForms.xml | 75 |
| BudgetItemType | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/humanres/widget/forms/LookupForms.xml | 58 |
| BudgetType | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/humanres/widget/forms/LookupForms.xml | 29 |
| DeductionType | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/humanres/widget/forms/EmploymentForms.xml | 322 |
| Enumeration | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/LookupForms.xml | 333 |
| Enumeration | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/RecruitmentForms.xml | 38 |
| Enumeration | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/RecruitmentForms.xml | 47 |
| Enumeration | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/RecruitmentForms.xml | 123 |
| Enumeration | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/RecruitmentForms.xml | 246 |
| Enumeration | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/RecruitmentForms.xml | 304 |
| Geo | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/EmployeeForms.xml | 45 |
| Geo | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/EmployeeForms.xml | 54 |
| InvoiceAndType | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/humanres/widget/EmployeeScreens.xml | 344 |
| Party | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/CommonScreens.xml | 271 |
| Party | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/CommonScreens.xml | 327 |
| PartyAcctgPrefAndGroup | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/EmploymentForms.xml | 29 |
| PartyAcctgPreference | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/humanres/minilang/HumanResServices.xml | 180 |
| PartyAndGroup | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/PartyTrees.xml | 23 |
| PartyAndGroup | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/PartyTrees.xml | 24 |
| PartyAndGroup | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/PartyTrees.xml | 25 |
| PartyAndGroup | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/PartyTrees.xml | 39 |
| PartyAndPerson | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/PartyTrees.xml | 55 |
| PartyGroup | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/src/main/java/org/apache/ofbiz/humanres/HumanResEvents.java | 80 |
| PartyGroup | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/CommonScreens.xml | 329 |
| PartyGroup | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/EmplPositionForms.xml | 307 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/CommonScreens.xml | 192 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/CommonScreens.xml | 195 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/CommonScreens.xml | 245 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/EmplLeaveForms.xml | 63 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/EmplLeaveForms.xml | 72 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/EmplLeaveForms.xml | 144 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/EmplLeaveForms.xml | 153 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/EmplPositionForms.xml | 41 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/EmplPositionForms.xml | 75 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/EmplPositionForms.xml | 111 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/EmployeeForms.xml | 225 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/EmploymentForms.xml | 350 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/GlobalHRSettingForms.xml | 345 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/PartyQualForms.xml | 67 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/PartyResumeForms.xml | 50 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/PerfReviewForms.xml | 48 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/PerfReviewForms.xml | 54 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/PersonTrainingForms.xml | 78 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/PersonTrainingForms.xml | 115 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/PersonTrainingForms.xml | 122 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/PersonTrainingForms.xml | 186 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/PersonTrainingForms.xml | 193 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/PersonTrainingForms.xml | 221 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/PersonTrainingForms.xml | 234 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/PersonTrainingForms.xml | 237 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/RecruitmentForms.xml | 182 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/RecruitmentForms.xml | 189 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/RecruitmentForms.xml | 272 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/RecruitmentForms.xml | 279 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/RecruitmentForms.xml | 358 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/RecruitmentForms.xml | 365 |
| PartyNameView | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/RecruitmentForms.xml | 429 |
| PartyRelationship | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/minilang/HumanResEvents.xml | 46 |
| PartyRelationship | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/src/main/java/org/apache/ofbiz/humanres/HumanResEvents.java | 156 |
| PartyRelationship | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/src/main/java/org/apache/ofbiz/humanres/HumanResEvents.java | 174 |
| PartyRelationship | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/PartyTrees.xml | 30 |
| PartyRelationship | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/PartyTrees.xml | 38 |
| PartyRole | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/EmplPositionScreens.xml | 328 |
| PartyRole | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/EmplPositionForms.xml | 343 |
| PartyRole | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/EmploymentForms.xml | 120 |
| PartyRole | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/RecruitmentForms.xml | 408 |
| PartyRoleAndPartyDetail | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/EmplPositionForms.xml | 68 |
| PartyRoleNameDetail | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/EmployeeForms.xml | 31 |
| PaymentMethodType | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/humanres/widget/forms/EmploymentForms.xml | 299 |
| PaymentMethodType | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/humanres/widget/forms/EmploymentForms.xml | 329 |
| PeriodType | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/EmploymentForms.xml | 138 |
| PeriodType | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/EmploymentForms.xml | 189 |
| PeriodType | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/EmploymentForms.xml | 216 |
| PeriodType | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/EmploymentForms.xml | 240 |
| PeriodType | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/EmploymentForms.xml | 277 |
| PeriodType | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/EmploymentForms.xml | 300 |
| PeriodType | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/EmploymentForms.xml | 336 |
| PeriodType | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/GlobalHRSettingForms.xml | 159 |
| PeriodType | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/GlobalHRSettingForms.xml | 181 |
| Person | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/CommonScreens.xml | 272 |
| Person | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/CommonScreens.xml | 328 |
| RateAmount | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/humanres/minilang/HumanResServices.xml | 183 |
| RateType | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/humanres/widget/forms/GlobalHRSettingForms.xml | 158 |
| RateType | datamodel | applications/datamodel/entitydef/accounting-entitymodel.xml | applications/humanres/widget/forms/GlobalHRSettingForms.xml | 173 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/EmploymentForms.xml | 297 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/EmploymentForms.xml | 363 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/EmploymentForms.xml | 468 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/EmploymentForms.xml | 475 |
| RoleType | datamodel | applications/datamodel/entitydef/party-entitymodel.xml | applications/humanres/widget/forms/PerfReviewForms.xml | 81 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/EmplLeaveForms.xml | 39 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/EmplLeaveForms.xml | 78 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/EmplLeaveForms.xml | 124 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/EmplLeaveForms.xml | 159 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/EmplLeaveForms.xml | 179 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/EmplPositionForms.xml | 47 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/EmplPositionForms.xml | 83 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/EmployeeForms.xml | 118 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/EmployeeForms.xml | 126 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/EmployeeForms.xml | 150 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/EmployeeForms.xml | 158 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/EmployeeForms.xml | 217 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/EmploymentAppForms.xml | 44 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/EmploymentAppForms.xml | 82 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/EmploymentAppForms.xml | 117 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/EmploymentForms.xml | 393 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/EmploymentForms.xml | 418 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/LookupForms.xml | 86 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/PartyQualForms.xml | 36 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/PartyQualForms.xml | 44 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/PartyQualForms.xml | 79 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/PartyQualForms.xml | 87 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/PartyQualForms.xml | 118 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/PartyQualForms.xml | 126 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/PersonTrainingForms.xml | 84 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/PersonTrainingForms.xml | 138 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/PersonTrainingForms.xml | 147 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/PersonTrainingForms.xml | 201 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/PersonTrainingForms.xml | 218 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/PersonTrainingForms.xml | 241 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/RecruitmentForms.xml | 150 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/RecruitmentForms.xml | 337 |
| StatusItem | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/RecruitmentForms.xml | 389 |
| Uom | common | framework/common/entitydef/entitymodel.xml | applications/humanres/widget/forms/GlobalHRSettingForms.xml | 190 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/humanres/minilang/HumanResEvents.xml | 75 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/humanres/widget/GlobalHRSettingScreens.xml | 279 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/humanres/widget/PersonTrainingScreens.xml | 123 |
| WorkEffort | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/humanres/widget/forms/GlobalHRSettingForms.xml | 327 |
| WorkEffortPartyAssignment | datamodel | applications/datamodel/entitydef/workeffort-entitymodel.xml | applications/humanres/widget/forms/GlobalHRSettingForms.xml | 333 |

## Reverse component references

| Component | File | Line | References |
| --- | --- | --- | --- |
| party | applications/party/webapp/partymgr/WEB-INF/controller.xml | 1521 | 1 |
| party | applications/party/webapp/partymgr/WEB-INF/controller.xml | 1539 | 1 |
| party | applications/party/webapp/partymgr/WEB-INF/controller.xml | 1540 | 1 |
| party | applications/party/webapp/partymgr/WEB-INF/controller.xml | 1562 | 1 |
| party | applications/party/webapp/partymgr/WEB-INF/controller.xml | 1564 | 1 |
| party | applications/party/widget/partymgr/PartyScreens.xml | 992 | 1 |
| party | applications/party/widget/partymgr/PartyScreens.xml | 995 | 1 |