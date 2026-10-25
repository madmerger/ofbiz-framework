# Entity inventory

## PartyQual

- Package: org.apache.ofbiz.humanres.ability
- Table: `PARTY_QUAL`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:43`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| partyQualTypeId | id | — | Y |
| qualificationDesc | id-long | — | N |
| title | id-long | — | N |
| statusId | id | — | N |
| verifStatusId | id | — | N |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PartyQualType | — | partyQualTypeId → — | — |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |
| one | StatusItem (external) | Verification | verifStatusId → statusId | framework/common/entitydef/entitymodel.xml |

## PartyQualType

- Package: org.apache.ofbiz.humanres.ability
- Table: `PARTY_QUAL_TYPE`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:71`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyQualTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PartyQualType | Parent | parentTypeId → partyQualTypeId | — |

## PartyResume

- Package: org.apache.ofbiz.humanres.ability
- Table: `PARTY_RESUME`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:83`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| resumeId | id | — | Y |
| partyId | id | — | N |
| contentId | id | — | N |
| resumeDate | date-time | — | N |
| resumeText | long-varchar | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | Content (external) | — | contentId → — | applications/datamodel/entitydef/content-entitymodel.xml |

## PartySkill

- Package: org.apache.ofbiz.humanres.ability
- Table: `PARTY_SKILL`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:99`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| skillTypeId | id | — | Y |
| yearsExperience | numeric | — | N |
| rating | numeric | — | N |
| skillLevel | numeric | — | N |
| startedUsingDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | SkillType | — | skillTypeId → — | — |

## PerfRatingType

- Package: org.apache.ofbiz.humanres.ability
- Table: `PERF_RATING_TYPE`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:117`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| perfRatingTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PerfRatingType | Parent | parentTypeId → perfRatingTypeId | — |

## PerfReview

- Package: org.apache.ofbiz.humanres.ability
- Table: `PERF_REVIEW`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:129`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| employeePartyId | id | — | Y |
| employeeRoleTypeId | id | — | Y |
| perfReviewId | id | — | Y |
| managerPartyId | id | — | N |
| managerRoleTypeId | id | — | N |
| paymentId | id | — | N |
| emplPositionId | id | — | N |
| fromDate | date-time | — | N |
| thruDate | date-time | — | N |
| comments | comment | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party (external) | Employee | employeePartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PartyRole (external) | Employee | employeePartyId → partyId, employeeRoleTypeId → roleTypeId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | Party (external) | Manager | managerPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | PartyRole (external) | Manager | managerPartyId → partyId, managerRoleTypeId → roleTypeId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | Payment (external) | — | paymentId → — | applications/datamodel/entitydef/accounting-entitymodel.xml |
| one-nofk | EmplPosition | — | emplPositionId → — | — |

## PerfReviewItem

- Package: org.apache.ofbiz.humanres.ability
- Table: `PERF_REVIEW_ITEM`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:166`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| employeePartyId | id | — | Y |
| employeeRoleTypeId | id | — | Y |
| perfReviewId | id | — | Y |
| perfReviewItemSeqId | id | — | Y |
| perfReviewItemTypeId | id | — | N |
| perfRatingTypeId | id | — | N |
| comments | comment | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PerfReview | — | employeePartyId → —, employeeRoleTypeId → —, perfReviewId → — | — |
| one | Party (external) | Employee | employeePartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PartyRole (external) | Employee | employeePartyId → partyId, employeeRoleTypeId → roleTypeId | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | PerfRatingType | — | perfRatingTypeId → — | — |
| one-nofk | PerfReviewItemType | — | perfReviewItemTypeId → — | — |

## PerfReviewItemType

- Package: org.apache.ofbiz.humanres.ability
- Table: `PERF_REVIEW_ITEM_TYPE`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:199`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| perfReviewItemTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PerfReviewItemType | Parent | parentTypeId → perfReviewItemTypeId | — |

## PerformanceNote

- Package: org.apache.ofbiz.humanres.ability
- Table: `PERFORMANCE_NOTE`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:211`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| roleTypeId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| communicationDate | date-time | — | N |
| comments | comment | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PartyRole (external) | — | partyId → —, roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |

## PersonTraining

- Package: org.apache.ofbiz.humanres.ability
- Table: `PERSON_TRAINING`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:231`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| trainingRequestId | id | — | N |
| trainingClassTypeId | id | — | Y |
| workEffortId | id | — | N |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| approverId | id | — | N |
| approvalStatus | short-varchar | — | N |
| reason | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | Person (external) | Approver | approverId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | TrainingClassType | — | trainingClassTypeId → — | — |
| one | WorkEffort (external) | — | workEffortId → — | applications/datamodel/entitydef/workeffort-entitymodel.xml |
| one | TrainingRequest | — | trainingRequestId → — | — |

## ResponsibilityType

- Package: org.apache.ofbiz.humanres.ability
- Table: `RESPONSIBILITY_TYPE`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:262`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| responsibilityTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | ResponsibilityType | Parent | parentTypeId → responsibilityTypeId | — |

## SkillType

- Package: org.apache.ofbiz.humanres.ability
- Table: `SKILL_TYPE`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:274`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| skillTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | SkillType | Parent | parentTypeId → skillTypeId | — |

## TrainingClassType

- Package: org.apache.ofbiz.humanres.ability
- Table: `TRAINING_CLASS_TYPE`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:286`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| trainingClassTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | TrainingClassType | Parent | parentTypeId → trainingClassTypeId | — |

## BenefitType

- Package: org.apache.ofbiz.humanres.employment
- Table: `BENEFIT_TYPE`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:303`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| benefitTypeId | id | — | Y |
| benefitName | name | — | N |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |
| employerPaidPercentage | floating-point | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | BenefitType | Parent | parentTypeId → benefitTypeId | — |

## Employment

- Package: org.apache.ofbiz.humanres.employment
- Table: `EMPLOYMENT`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:330`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| roleTypeIdFrom | id | — | Y |
| roleTypeIdTo | id | — | Y |
| partyIdFrom | id | — | Y |
| partyIdTo | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| terminationReasonId | id | — | N |
| terminationTypeId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party (external) | To | partyIdTo → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PartyRole (external) | To | partyIdTo → partyId, roleTypeIdTo → roleTypeId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | Party (external) | From | partyIdFrom → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PartyRole (external) | From | partyIdFrom → partyId, roleTypeIdFrom → roleTypeId | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | TerminationReason | — | terminationReasonId → — | — |
| one-nofk | TerminationType | — | terminationTypeId → — | — |
| many | Agreement (external) | — | partyIdTo → agreementId, partyIdFrom → agreementId | applications/datamodel/entitydef/party-entitymodel.xml |

## EmploymentApp

- Package: org.apache.ofbiz.humanres.employment
- Table: `EMPLOYMENT_APP`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:383`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| applicationId | id | — | Y |
| emplPositionId | id | — | N |
| statusId | id | — | N |
| employmentAppSourceTypeId | id | — | N |
| applyingPartyId | id | — | N |
| referredByPartyId | id | — | N |
| applicationDate | date-time | — | N |
| approverPartyId | id | — | N |
| jobRequisitionId | id | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one-nofk | EmplPosition | — | emplPositionId → — | — |
| one-nofk | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |
| one-nofk | EmploymentAppSourceType | — | employmentAppSourceTypeId → — | — |
| one-nofk | Party (external) | Applying | applyingPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | Party (external) | ReferredBy | referredByPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | Party (external) | Approver | approverPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | JobRequisition | — | jobRequisitionId → — | — |

## EmploymentAppSourceType

- Package: org.apache.ofbiz.humanres.employment
- Table: `EMPLOYMENT_APP_SOURCE_TYPE`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:418`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| employmentAppSourceTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | EmploymentAppSourceType | Parent | parentTypeId → employmentAppSourceTypeId | — |

## EmplLeave

- Package: org.apache.ofbiz.humanres.employment
- Table: `EMPL_LEAVE`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:430`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| leaveTypeId | id | — | Y |
| emplLeaveReasonTypeId | id | — | N |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| approverPartyId | id | — | N |
| leaveStatus | id | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | EmplLeaveType | — | leaveTypeId → — | — |
| one | EmplLeaveReasonType | — | emplLeaveReasonTypeId → — | — |
| one | Party (external) | Approver | approverPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | StatusItem (external) | — | leaveStatus → statusId | framework/common/entitydef/entitymodel.xml |

## EmplLeaveType

- Package: org.apache.ofbiz.humanres.employment
- Table: `EMPL_LEAVE_TYPE`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:460`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| leaveTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | EmplLeaveType | Parent | parentTypeId → leaveTypeId | — |

## PartyBenefit

- Package: org.apache.ofbiz.humanres.employment
- Table: `PARTY_BENEFIT`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:473`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| roleTypeIdFrom | id | — | Y |
| roleTypeIdTo | id | — | Y |
| partyIdFrom | id | — | Y |
| partyIdTo | id | — | Y |
| benefitTypeId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| periodTypeId | id | — | N |
| cost | currency-amount | — | N |
| actualEmployerPaidPercent | floating-point | — | N |
| availableTime | numeric | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party (external) | To | partyIdTo → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PartyRole (external) | To | partyIdTo → partyId, roleTypeIdTo → roleTypeId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | Party (external) | From | partyIdFrom → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PartyRole (external) | From | partyIdFrom → partyId, roleTypeIdFrom → roleTypeId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | BenefitType | — | benefitTypeId → — | — |
| one-nofk | PeriodType (external) | — | periodTypeId → — | framework/common/entitydef/entitymodel.xml |

## PayGrade

- Package: org.apache.ofbiz.humanres.employment
- Table: `PAY_GRADE`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:514`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| payGradeId | id | — | Y |
| payGradeName | name | — | N |
| comments | comment | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## PayHistory

- Package: org.apache.ofbiz.humanres.employment
- Table: `PAY_HISTORY`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:522`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| roleTypeIdFrom | id | — | Y |
| roleTypeIdTo | id | — | Y |
| partyIdFrom | id | — | Y |
| partyIdTo | id | — | Y |
| emplFromDate | date-time | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| salaryStepSeqId | id | — | N |
| payGradeId | id | — | N |
| periodTypeId | id | — | N |
| amount | currency-amount | — | N |
| comments | comment | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Employment | — | roleTypeIdFrom → —, roleTypeIdTo → —, partyIdFrom → —, partyIdTo → —, emplFromDate → fromDate | — |
| one | PayGrade | — | payGradeId → — | — |
| one-nofk | SalaryStep | — | salaryStepSeqId → —, payGradeId → —, fromDate → — | — |
| one | PeriodType (external) | — | periodTypeId → — | framework/common/entitydef/entitymodel.xml |

## PayrollPreference

- Package: org.apache.ofbiz.humanres.employment
- Table: `PAYROLL_PREFERENCE`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:562`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| partyId | id | — | Y |
| roleTypeId | id | — | Y |
| payrollPreferenceSeqId | id | — | Y |
| deductionTypeId | id | — | N |
| paymentMethodTypeId | id | — | N |
| periodTypeId | id | — | N |
| fromDate | date-time | — | N |
| thruDate | date-time | — | N |
| percentage | floating-point | — | N |
| flatAmount | currency-amount | — | N |
| routingNumber | short-varchar | — | N |
| accountNumber | short-varchar | — | N |
| bankName | name | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one | PartyRole (external) | — | partyId → —, roleTypeId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | DeductionType (external) | — | deductionTypeId → — | applications/datamodel/entitydef/accounting-entitymodel.xml |
| one-nofk | PaymentMethodType (external) | — | paymentMethodTypeId → — | applications/datamodel/entitydef/accounting-entitymodel.xml |
| one-nofk | PeriodType (external) | — | periodTypeId → — | framework/common/entitydef/entitymodel.xml |

## SalaryStep

- Package: org.apache.ofbiz.humanres.employment
- Table: `SALARY_STEP`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:598`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| salaryStepSeqId | id | — | Y |
| payGradeId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| dateModified | date-time | — | N |
| amount | currency-amount | — | N |
| createdByUserLogin | id-vlong | — | N |
| lastModifiedByUserLogin | id-vlong | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | PayGrade | — | payGradeId → — | — |

## TerminationReason

- Package: org.apache.ofbiz.humanres.employment
- Table: `TERMINATION_REASON`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:616`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| terminationReasonId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## TerminationType

- Package: org.apache.ofbiz.humanres.employment
- Table: `TERMINATION_TYPE`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:623`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| terminationTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | TerminationType | Parent | parentTypeId → terminationTypeId | — |

## UnemploymentClaim

- Package: org.apache.ofbiz.humanres.employment
- Table: `UNEMPLOYMENT_CLAIM`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:635`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| unemploymentClaimId | id | — | Y |
| unemploymentClaimDate | date-time | — | N |
| description | description | — | N |
| statusId | id | — | N |
| partyIdFrom | id | — | N |
| partyIdTo | id | — | N |
| roleTypeIdFrom | id | — | N |
| roleTypeIdTo | id | — | N |
| fromDate | date-time | — | N |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one-nofk | Employment | — | roleTypeIdFrom → —, roleTypeIdTo → —, partyIdFrom → —, partyIdTo → —, fromDate → — | — |
| one-nofk | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |

## EmplPosition

- Package: org.apache.ofbiz.humanres.position
- Table: `EMPL_POSITION`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:665`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| emplPositionId | id | — | Y |
| statusId | id | — | N |
| partyId | id | — | N |
| budgetId | id | — | N |
| budgetItemSeqId | id | — | N |
| emplPositionTypeId | id | — | N |
| estimatedFromDate | date-time | — | N |
| estimatedThruDate | date-time | — | N |
| salaryFlag | indicator | — | N |
| exemptFlag | indicator | — | N |
| fulltimeFlag | indicator | — | N |
| temporaryFlag | indicator | — | N |
| actualFromDate | date-time | — | N |
| actualThruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | StatusItem (external) | — | statusId → — | framework/common/entitydef/entitymodel.xml |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |
| one-nofk | BudgetItem (external) | — | budgetId → —, budgetItemSeqId → — | applications/datamodel/entitydef/accounting-entitymodel.xml |
| one-nofk | EmplPositionType | — | emplPositionTypeId → — | — |

## EmplPositionClassType

- Package: org.apache.ofbiz.humanres.position
- Table: `EMPL_POSITION_CLASS_TYPE`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:697`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| emplPositionClassTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | EmplPositionClassType | Parent | parentTypeId → emplPositionClassTypeId | — |

## EmplPositionFulfillment

- Package: org.apache.ofbiz.humanres.position
- Table: `EMPL_POSITION_FULFILLMENT`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:709`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| emplPositionId | id | — | Y |
| partyId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| comments | comment | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | EmplPosition | — | emplPositionId → — | — |
| one | Party (external) | — | partyId → — | applications/datamodel/entitydef/party-entitymodel.xml |

## EmplPositionReportingStruct

- Package: org.apache.ofbiz.humanres.position
- Table: `EMPL_POSITION_REPORTING_STRUCT`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:743`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| emplPositionIdReportingTo | id | — | Y |
| emplPositionIdManagedBy | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| comments | comment | — | N |
| primaryFlag | indicator | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | EmplPosition | ReportingTo | emplPositionIdReportingTo → emplPositionId | — |
| one | EmplPosition | ManagedBy | emplPositionIdManagedBy → emplPositionId | — |

## EmplPositionResponsibility

- Package: org.apache.ofbiz.humanres.position
- Table: `EMPL_POSITION_RESPONSIBILITY`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:762`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| emplPositionId | id | — | Y |
| responsibilityTypeId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| comments | comment | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | EmplPosition | — | emplPositionId → — | — |
| one | ResponsibilityType | — | responsibilityTypeId → — | — |

## EmplPositionType

- Package: org.apache.ofbiz.humanres.position
- Table: `EMPL_POSITION_TYPE`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:780`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| emplPositionTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | EmplPositionType | Parent | parentTypeId → emplPositionTypeId | — |

## EmplPositionTypeClass

- Package: org.apache.ofbiz.humanres.position
- Table: `EMPL_POSITION_TYPE_CLASS`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:792`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| emplPositionTypeId | id | — | Y |
| emplPositionClassTypeId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| standardHoursPerWeek | floating-point | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | EmplPositionType | — | emplPositionTypeId → — | — |
| one | EmplPositionClassType | — | emplPositionClassTypeId → — | — |

## ValidResponsibility

- Package: org.apache.ofbiz.humanres.position
- Table: `VALID_RESPONSIBILITY`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:810`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| emplPositionTypeId | id | — | Y |
| responsibilityTypeId | id | — | Y |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |
| comments | comment | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | EmplPositionType | — | emplPositionTypeId → — | — |
| one | ResponsibilityType | — | responsibilityTypeId → — | — |

## EmplPositionTypeRate

- Package: org.apache.ofbiz.humanres.position
- Table: `EMPL_POSITION_TYPE_RATE`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:828`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| emplPositionTypeId | id | — | Y |
| rateTypeId | id | — | Y |
| payGradeId | id | — | N |
| salaryStepSeqId | id | — | N |
| fromDate | date-time | — | Y |
| thruDate | date-time | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | EmplPositionType | — | emplPositionTypeId → — | — |
| one-nofk | SalaryStep | — | salaryStepSeqId → —, payGradeId → —, fromDate → — | — |
| one-nofk | RateType (external) | — | rateTypeId → — | applications/datamodel/entitydef/accounting-entitymodel.xml |

## JobRequisition

- Package: org.apache.ofbiz.humanres.recruitment
- Table: `JOB_REQUISITION`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:872`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| jobRequisitionId | id | — | Y |
| durationMonths | numeric | — | N |
| age | numeric | — | N |
| gender | indicator | — | N |
| experienceMonths | numeric | — | N |
| experienceYears | numeric | — | N |
| qualification | id-long | — | N |
| jobLocation | id | — | N |
| skillTypeId | id | — | N |
| noOfResources | numeric | — | N |
| jobPostingTypeEnumId | id | — | N |
| jobRequisitionDate | date | — | N |
| examTypeEnumId | id | — | N |
| requiredOnDate | date | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | SkillType | — | skillTypeId → — | — |
| one | Enumeration (external) | ExamType | examTypeEnumId → enumId | framework/common/entitydef/entitymodel.xml |
| one | Enumeration (external) | JobPostingType | jobPostingTypeEnumId → enumId | framework/common/entitydef/entitymodel.xml |

## JobInterview

- Package: org.apache.ofbiz.humanres.recruitment
- Table: `JOB_INTERVIEW`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:900`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| jobInterviewId | id | — | Y |
| jobIntervieweePartyId | id | — | N |
| jobRequisitionId | id | — | N |
| jobInterviewerPartyId | id | — | N |
| jobInterviewTypeId | id | — | N |
| gradeSecuredEnumId | id | — | N |
| jobInterviewResult | id | — | N |
| jobInterviewDate | date | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | Party (external) | Interviewee | jobIntervieweePartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | Party (external) | Interviewer | jobInterviewerPartyId → partyId | applications/datamodel/entitydef/party-entitymodel.xml |
| one | JobInterviewType | — | jobInterviewTypeId → — | — |
| one | JobRequisition | — | jobRequisitionId → — | — |
| one | Enumeration (external) | — | gradeSecuredEnumId → enumId | framework/common/entitydef/entitymodel.xml |

## JobInterviewType

- Package: org.apache.ofbiz.humanres.recruitment
- Table: `JOB_INTERVIEW_TYPE`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:928`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| jobInterviewTypeId | id | — | Y |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## TrainingRequest

- Package: org.apache.ofbiz.humanres.trainings
- Table: `TRAINING_REQUEST`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:953`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| trainingRequestId | id | — | Y |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |

## EmplLeaveReasonType

- Package: org.apache.ofbiz.humanres.employment
- Table: `EMPL_LEAVE_REASON_TYPE`
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:959`

### Fields

| Name | Type | Column | PK |
| --- | --- | --- | --- |
| emplLeaveReasonTypeId | id | — | Y |
| parentTypeId | id | — | N |
| hasTable | indicator | — | N |
| description | description | — | N |

### Relations

| Type | Related entity | Title | Key maps | External owner |
| --- | --- | --- | --- | --- |
| one | EmplLeaveReasonType | Parent | parentTypeId → emplLeaveReasonTypeId | — |

## View entities

### BenefitTypeAndParty

- Package: org.apache.ofbiz.humanres.employment
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:317`
- Member entities: BT = BenefitType, PB = PartyBenefit

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| BT | PB | benefitTypeId → — |

### EmploymentAndPerson

- Package: org.apache.ofbiz.humanres.employment
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:371`
- Member entities: EMPLMNT = Employment, PERS = Person

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| EMPLMNT | PERS | partyIdTo → partyId |

### EmplPositionAndFulfillment

- Package: org.apache.ofbiz.humanres.position
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:727`
- Member entities: EMPPOS = EmplPosition, EPF = EmplPositionFulfillment

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| EMPPOS | EPF | emplPositionId → — |

### EmplPositionTypeRateAndAmount

- Package: org.apache.ofbiz.humanres.position
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:852`
- Member entities: EPTR = EmplPositionTypeRate, RA = RateAmount

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| EPTR | RA | emplPositionTypeId → —, rateTypeId → — |

### EmplPositionFulfillmentAndReportingStruct

- Package: org.apache.ofbiz.humanres.recruitment
- Source: `applications/datamodel/entitydef/humanres-entitymodel.xml:935`
- Member entities: EMPPOS = EmplPosition, EMPPOSFUL = EmplPositionFulfillment, EMPPOSREPST = EmplPositionReportingStruct

| Entity alias | Related alias | Key maps |
| --- | --- | --- |
| EMPPOS | EMPPOSFUL | emplPositionId → — |
| EMPPOSFUL | EMPPOSREPST | emplPositionId → emplPositionIdManagedBy |
