# Service inventory

## applyTraining

- Definition: `applications/humanres/servicedef/services.xml:828`
- Engine: simple; location: component://humanres/minilang/HumanResServices.xml; invoke: applyTraining
- Default entity: PersonTraining; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| approvalStatus | short-varchar | IN | true |
| approverId | id | IN | false |
| fromDate | date-time | IN | true |
| partyId | id | IN | false |
| reason | description | IN | true |
| thruDate | date-time | IN | true |
| trainingClassTypeId | id | IN | false |
| trainingRequestId | id | IN | true |
| workEffortId | id | IN | false |

## assignTraining

- Definition: `applications/humanres/servicedef/services.xml:843`
- Engine: simple; location: component://humanres/minilang/HumanResServices.xml; invoke: assignTraining
- Default entity: PersonTraining; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| approvalStatus | short-varchar | IN | true |
| approverId | id | IN | false |
| fromDate | date-time | IN | false |
| partyId | id | IN | false |
| reason | description | IN | true |
| thruDate | date-time | IN | true |
| trainingClassTypeId | id | IN | false |
| trainingRequestId | id | IN | true |
| workEffortId | id | IN | true |

## createAgreementEmploymentAppl

- Definition: `applications/humanres/servicedef/services.xml:632`
- Engine: entity-auto; location: —; invoke: create
- Default entity: AgreementEmploymentAppl; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementDate | date-time | IN | true |
| agreementId | id | IN | false |
| agreementItemSeqId | id | IN | false |
| fromDate | date-time | IN | false |
| partyIdFrom | id | IN | false |
| partyIdTo | id | IN | false |
| roleTypeIdFrom | id | IN | false |
| roleTypeIdTo | id | IN | false |
| thruDate | date-time | IN | true |

## createBenefitType

- Definition: `applications/humanres/servicedef/services_employment.xml:27`
- Engine: entity-auto; location: —; invoke: create
- Default entity: BenefitType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| benefitName | name | IN | true |
| benefitTypeId | id | INOUT | true |
| description | description | IN | true |
| employerPaidPercentage | floating-point | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## createEmplLeave

- Definition: `applications/humanres/servicedef/services.xml:651`
- Engine: entity-auto; location: —; invoke: create
- Default entity: EmplLeave; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| approverPartyId | id | IN | false |
| description | description | IN | true |
| emplLeaveReasonTypeId | id | IN | true |
| fromDate | date-time | IN | false |
| leaveStatus | id | IN | true |
| leaveTypeId | id | IN | false |
| partyId | id | IN | false |
| thruDate | date-time | IN | false |

## createEmplLeaveReasonType

- Definition: `applications/humanres/servicedef/services.xml:886`
- Engine: entity-auto; location: —; invoke: create
- Default entity: EmplLeaveReasonType; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | false |
| emplLeaveReasonTypeId | id | INOUT | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## createEmplLeaveType

- Definition: `applications/humanres/servicedef/services.xml:674`
- Engine: entity-auto; location: —; invoke: create
- Default entity: EmplLeaveType; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | false |
| hasTable | indicator | IN | true |
| leaveTypeId | id | INOUT | true |
| parentTypeId | id | IN | true |

## createEmplPosition

- Definition: `applications/humanres/servicedef/services.xml:392`
- Engine: entity-auto; location: —; invoke: create
- Default entity: EmplPosition; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| actualFromDate | date-time | IN | true |
| actualThruDate | date-time | IN | true |
| budgetId | id | IN | true |
| budgetItemSeqId | id | IN | true |
| emplPositionId | id | INOUT | true |
| emplPositionTypeId | id | IN | true |
| estimatedFromDate | date-time | IN | true |
| estimatedThruDate | date-time | IN | true |
| exemptFlag | indicator | IN | true |
| fulltimeFlag | indicator | IN | true |
| partyId | id | IN | true |
| salaryFlag | indicator | IN | true |
| statusId | id | IN | true |
| temporaryFlag | indicator | IN | true |

## createEmplPositionClassType

- Definition: `applications/humanres/servicedef/services_position.xml:42`
- Engine: entity-auto; location: —; invoke: create
- Default entity: EmplPositionClassType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| emplPositionClassTypeId | id | INOUT | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## createEmplPositionFulfillment

- Definition: `applications/humanres/servicedef/services.xml:412`
- Engine: entity-auto; location: —; invoke: create
- Default entity: EmplPositionFulfillment; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| emplPositionId | id | IN | false |
| fromDate | Timestamp | IN | true |
| partyId | id | IN | false |
| thruDate | date-time | IN | true |

## createEmplPositionReportingStruct

- Definition: `applications/humanres/servicedef/services.xml:433`
- Engine: simple; location: component://humanres/minilang/HumanResServices.xml; invoke: createEmplPositionReportingStruct
- Default entity: EmplPositionReportingStruct; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| emplPositionIdManagedBy | id | IN | false |
| emplPositionIdReportingTo | id | IN | false |
| fromDate | Timestamp | IN | true |
| primaryFlag | indicator | IN | true |
| thruDate | date-time | IN | true |

## createEmplPositionResponsibility

- Definition: `applications/humanres/servicedef/services.xml:455`
- Engine: entity-auto; location: —; invoke: create
- Default entity: EmplPositionResponsibility; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| emplPositionId | id | IN | false |
| fromDate | Timestamp | IN | true |
| responsibilityTypeId | id | IN | false |
| thruDate | date-time | IN | true |

## createEmplPositionType

- Definition: `applications/humanres/servicedef/services.xml:586`
- Engine: entity-auto; location: —; invoke: create
- Default entity: EmplPositionType; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | false |
| emplPositionTypeId | id | INOUT | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## createEmplPositionTypeClass

- Definition: `applications/humanres/servicedef/services_position.xml:27`
- Engine: entity-auto; location: —; invoke: create
- Default entity: EmplPositionTypeClass; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emplPositionClassTypeId | id | IN | — |
| emplPositionTypeId | id | IN | — |
| fromDate | date-time | IN | — |
| standardHoursPerWeek | floating-point | IN | true |
| thruDate | date-time | IN | true |

## createEmployee

- Definition: `applications/humanres/servicedef/services.xml:522`
- Engine: simple; location: component://humanres/minilang/HumanResServices.xml; invoke: createEmployee
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emailAddress | String | IN | true |
| fromDate | String | IN | true |
| partyId | String | OUT | — |
| postalAddContactMechPurpTypeId | String | IN | false |

## createEmployment

- Definition: `applications/humanres/servicedef/services.xml:189`
- Engine: simple; location: component://humanres/minilang/HumanResServices.xml; invoke: createEmployment
- Default entity: Employment; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | BigDecimal | IN | true |
| comments | String | IN | true |
| fromDate | Timestamp | IN | true |
| partyIdFrom | id | IN | false |
| partyIdTo | id | IN | false |
| payGradeId | String | IN | true |
| periodTypeId | String | IN | true |
| roleTypeIdFrom | id | IN | false |
| roleTypeIdTo | id | IN | false |
| salaryStepSeqId | String | IN | true |
| terminationReasonId | id | IN | true |
| terminationTypeId | id | IN | true |
| thruDate | date-time | IN | true |

## createEmploymentApp

- Definition: `applications/humanres/servicedef/services.xml:217`
- Engine: entity-auto; location: —; invoke: create
- Default entity: EmploymentApp; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| applicationDate | date-time | IN | true |
| applicationId | id | INOUT | true |
| applyingPartyId | id | IN | true |
| approverPartyId | id | IN | true |
| emplPositionId | id | IN | true |
| employmentAppSourceTypeId | id | IN | true |
| jobRequisitionId | id | IN | true |
| referredByPartyId | id | IN | true |
| statusId | id | IN | true |

## createEmploymentAppSourceType

- Definition: `applications/humanres/servicedef/services_employment.xml:43`
- Engine: entity-auto; location: —; invoke: create
- Default entity: EmploymentAppSourceType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| employmentAppSourceTypeId | id | INOUT | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## createInternalJobPosting

- Definition: `applications/humanres/servicedef/services.xml:742`
- Engine: entity-auto; location: —; invoke: create
- Default entity: EmploymentApp; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| applicationDate | date-time | IN | true |
| applicationId | id | INOUT | true |
| applyingPartyId | id | IN | false |
| approverPartyId | id | IN | false |
| emplPositionId | id | IN | true |
| employmentAppSourceTypeId | id | IN | true |
| jobRequisitionId | id | IN | false |
| referredByPartyId | id | IN | true |
| statusId | id | IN | true |

## createJobInterview

- Definition: `applications/humanres/servicedef/services.xml:768`
- Engine: entity-auto; location: —; invoke: create
- Default entity: JobInterview; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| gradeSecuredEnumId | id | IN | true |
| jobInterviewDate | date | IN | true |
| jobInterviewId | id | INOUT | true |
| jobInterviewResult | id | IN | true |
| jobInterviewTypeId | id | IN | true |
| jobIntervieweePartyId | id | IN | false |
| jobInterviewerPartyId | id | IN | false |
| jobRequisitionId | id | IN | false |

## createJobInterviewType

- Definition: `applications/humanres/servicedef/services.xml:793`
- Engine: entity-auto; location: —; invoke: create
- Default entity: JobInterviewType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | false |
| jobInterviewTypeId | id | INOUT | true |

## createJobRequisition

- Definition: `applications/humanres/servicedef/services.xml:711`
- Engine: entity-auto; location: —; invoke: create
- Default entity: JobRequisition; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| age | numeric | IN | true |
| durationMonths | numeric | IN | true |
| examTypeEnumId | id | IN | true |
| experienceMonths | numeric | IN | false |
| experienceYears | numeric | IN | false |
| gender | indicator | IN | true |
| jobLocation | id | IN | false |
| jobPostingTypeEnumId | id | IN | true |
| jobRequisitionDate | date | IN | true |
| jobRequisitionId | id | INOUT | true |
| noOfResources | numeric | IN | false |
| qualification | id-long | IN | false |
| requiredOnDate | date | IN | true |
| skillTypeId | id | IN | false |

## createPartyBenefit

- Definition: `applications/humanres/servicedef/services.xml:237`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PartyBenefit; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| actualEmployerPaidPercent | floating-point | IN | true |
| availableTime | numeric | IN | true |
| benefitTypeId | id | IN | false |
| cost | currency-amount | IN | true |
| fromDate | Timestamp | IN | true |
| partyIdFrom | id | IN | false |
| partyIdTo | id | IN | false |
| periodTypeId | id | IN | true |
| roleTypeIdFrom | id | IN | false |
| roleTypeIdTo | id | IN | false |
| thruDate | date-time | IN | true |

## createPartyQual

- Definition: `applications/humanres/servicedef/services.xml:27`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PartyQual; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | Timestamp | INOUT | true |
| partyId | String | IN | — |
| partyQualTypeId | String | IN | — |
| qualificationDesc | id-long | IN | true |
| statusId | id | IN | true |
| thruDate | date-time | IN | true |
| title | id-long | IN | true |
| verifStatusId | id | IN | true |

## createPartyQualType

- Definition: `applications/humanres/servicedef/services.xml:916`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PartyQualType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| partyQualTypeId | id | INOUT | true |

## createPartyResume

- Definition: `applications/humanres/servicedef/services.xml:57`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PartyResume; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | true |
| partyId | id | IN | true |
| resumeDate | date-time | IN | true |
| resumeId | id | IN | true |
| resumeText | long-varchar | IN | true |

## createPartySkill

- Definition: `applications/humanres/servicedef/services.xml:77`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PartySkill; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | id | IN | false |
| rating | numeric | IN | true |
| skillLevel | numeric | IN | true |
| skillTypeId | id | IN | false |
| startedUsingDate | date-time | IN | true |
| yearsExperience | numeric | IN | true |

## createPayGrade

- Definition: `applications/humanres/servicedef/services.xml:258`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PayGrade; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| payGradeId | id | INOUT | true |
| payGradeName | name | IN | false |

## createPayHistory

- Definition: `applications/humanres/servicedef/services.xml:280`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PayHistory; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | currency-amount | IN | true |
| comments | comment | IN | true |
| emplFromDate | date-time | IN | false |
| fromDate | Timestamp | IN | true |
| partyIdFrom | id | IN | false |
| partyIdTo | id | IN | false |
| payGradeId | id | IN | true |
| periodTypeId | id | IN | true |
| roleTypeIdFrom | id | IN | false |
| roleTypeIdTo | id | IN | false |
| salaryStepSeqId | id | IN | true |
| thruDate | date-time | IN | true |

## createPayrollPreference

- Definition: `applications/humanres/servicedef/services.xml:309`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PayrollPreference; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accountNumber | short-varchar | IN | true |
| bankName | name | IN | true |
| deductionTypeId | id | IN | true |
| flatAmount | currency-amount | IN | true |
| fromDate | date-time | IN | true |
| partyId | id | IN | false |
| paymentMethodTypeId | id | IN | true |
| payrollPreferenceSeqId | String | IN | true |
| percentage | floating-point | IN | true |
| periodTypeId | id | IN | true |
| roleTypeId | id | IN | false |
| routingNumber | short-varchar | IN | true |
| thruDate | date-time | IN | true |

## createPerfRatingType

- Definition: `applications/humanres/servicedef/services_ability.xml:28`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PerfRatingType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| perfRatingTypeId | id | INOUT | true |

## createPerfReview

- Definition: `applications/humanres/servicedef/services.xml:97`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PerfReview; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| emplPositionId | id | IN | true |
| employeePartyId | String | INOUT | false |
| employeeRoleTypeId | String | INOUT | false |
| fromDate | date-time | IN | true |
| managerPartyId | id | IN | true |
| managerRoleTypeId | id | IN | true |
| paymentId | id | IN | true |
| perfReviewId | String | OUT | false |
| thruDate | date-time | IN | true |

## createPerfReviewItem

- Definition: `applications/humanres/servicedef/services.xml:132`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PerfReviewItem; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| employeePartyId | String | IN | false |
| employeeRoleTypeId | String | IN | false |
| perfRatingTypeId | id | IN | true |
| perfReviewId | String | IN | false |
| perfReviewItemSeqId | String | OUT | false |
| perfReviewItemTypeId | id | IN | true |

## createPerfReviewItemType

- Definition: `applications/humanres/servicedef/services_ability.xml:44`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PerfReviewItemType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| perfReviewItemTypeId | id | INOUT | true |

## createPerformanceNote

- Definition: `applications/humanres/servicedef/services.xml:167`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PerformanceNote; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| communicationDate | date-time | IN | true |
| fromDate | Timestamp | IN | true |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |
| thruDate | date-time | IN | true |

## createResponsibilityType

- Definition: `applications/humanres/servicedef/services.xml:544`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ResponsibilityType; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | false |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| responsibilityTypeId | id | INOUT | true |

## createSalaryStep

- Definition: `applications/humanres/servicedef/services.xml:332`
- Engine: entity-auto; location: —; invoke: create
- Default entity: SalaryStep; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | currency-amount | IN | true |
| createdByUserLogin | id-vlong | IN | true |
| dateModified | date-time | IN | true |
| fromDate | date-time | IN | false |
| lastModifiedByUserLogin | id-vlong | IN | true |
| payGradeId | id | IN | false |
| salaryStepSeqId | String | IN | true |
| thruDate | date-time | IN | true |

## createSkillType

- Definition: `applications/humanres/servicedef/services.xml:502`
- Engine: entity-auto; location: —; invoke: create
- Default entity: SkillType; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | false |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| skillTypeId | id | INOUT | true |

## createTerminationReason

- Definition: `applications/humanres/servicedef/services.xml:352`
- Engine: entity-auto; location: —; invoke: create
- Default entity: TerminationReason; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | false |
| terminationReasonId | id | INOUT | true |

## createTerminationType

- Definition: `applications/humanres/servicedef/services.xml:564`
- Engine: entity-auto; location: —; invoke: create
- Default entity: TerminationType; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | false |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| terminationTypeId | id | INOUT | true |

## createTrainingTypes

- Definition: `applications/humanres/servicedef/services.xml:856`
- Engine: entity-auto; location: —; invoke: create
- Default entity: TrainingClassType; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | String | IN | false |
| parentTypeId | String | IN | true |
| trainingClassTypeId | String | IN | false |

## createUnemploymentClaim

- Definition: `applications/humanres/servicedef/services.xml:373`
- Engine: entity-auto; location: —; invoke: create
- Default entity: UnemploymentClaim; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| fromDate | date-time | IN | true |
| partyIdFrom | id | IN | true |
| partyIdTo | id | IN | true |
| roleTypeIdFrom | id | IN | true |
| roleTypeIdTo | id | IN | true |
| statusId | id | IN | true |
| thruDate | date-time | IN | true |
| unemploymentClaimDate | date-time | IN | true |
| unemploymentClaimId | id | INOUT | true |

## createValidResponsibility

- Definition: `applications/humanres/servicedef/services.xml:476`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ValidResponsibility; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| emplPositionTypeId | id | IN | false |
| fromDate | Timestamp | IN | true |
| responsibilityTypeId | id | IN | false |
| thruDate | date-time | IN | true |

## deleteAgreementEmploymentAppl

- Definition: `applications/humanres/servicedef/services.xml:644`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AgreementEmploymentAppl; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| agreementItemSeqId | id | IN | false |
| fromDate | date-time | IN | false |
| partyIdFrom | id | IN | false |
| partyIdTo | id | IN | false |
| roleTypeIdFrom | id | IN | false |
| roleTypeIdTo | id | IN | false |

## deleteBenefitType

- Definition: `applications/humanres/servicedef/services_employment.xml:37`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: BenefitType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| benefitTypeId | id | IN | — |

## deleteEmplLeave

- Definition: `applications/humanres/servicedef/services.xml:667`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: EmplLeave; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| leaveTypeId | id | IN | false |
| partyId | id | IN | false |

## deleteEmplLeaveReasonType

- Definition: `applications/humanres/servicedef/services.xml:902`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: EmplLeaveReasonType; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emplLeaveReasonTypeId | id | IN | false |

## deleteEmplLeaveType

- Definition: `applications/humanres/servicedef/services.xml:688`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: EmplLeaveType; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| leaveTypeId | id | IN | false |

## deleteEmplPosition

- Definition: `applications/humanres/servicedef/services.xml:406`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: EmplPosition; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emplPositionId | id | IN | false |

## deleteEmplPositionClassType

- Definition: `applications/humanres/servicedef/services_position.xml:52`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: EmplPositionClassType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emplPositionClassTypeId | id | IN | — |

## deleteEmplPositionFulfillment

- Definition: `applications/humanres/servicedef/services.xml:427`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: EmplPositionFulfillment; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emplPositionId | id | IN | false |
| fromDate | date-time | IN | false |
| partyId | id | IN | false |

## deleteEmplPositionReportingStruct

- Definition: `applications/humanres/servicedef/services.xml:449`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: EmplPositionReportingStruct; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emplPositionIdManagedBy | id | IN | false |
| emplPositionIdReportingTo | id | IN | false |
| fromDate | date-time | IN | false |

## deleteEmplPositionResponsibility

- Definition: `applications/humanres/servicedef/services.xml:470`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: EmplPositionResponsibility; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emplPositionId | id | IN | false |
| fromDate | date-time | IN | false |
| responsibilityTypeId | id | IN | false |

## deleteEmplPositionType

- Definition: `applications/humanres/servicedef/services.xml:600`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: EmplPositionType; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emplPositionTypeId | id | IN | false |

## deleteEmplPositionTypeRate

- Definition: `applications/humanres/servicedef/services.xml:622`
- Engine: simple; location: component://humanres/minilang/HumanResServices.xml; invoke: deleteEmplPositionTypeRate
- Default entity: EmplPositionTypeRate; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emplPositionTypeId | id | IN | false |
| fromDate | date-time | IN | false |
| periodTypeId | String | IN | true |
| rateAmountFromDate | Timestamp | IN | true |
| rateTypeId | id | IN | false |

## deleteEmployment

- Definition: `applications/humanres/servicedef/services.xml:211`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: Employment; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| partyIdFrom | id | IN | false |
| partyIdTo | id | IN | false |
| roleTypeIdFrom | id | IN | false |
| roleTypeIdTo | id | IN | false |

## deleteEmploymentApp

- Definition: `applications/humanres/servicedef/services.xml:231`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: EmploymentApp; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| applicationId | id | IN | false |

## deleteEmploymentAppSourceType

- Definition: `applications/humanres/servicedef/services_employment.xml:53`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: EmploymentAppSourceType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| employmentAppSourceTypeId | id | IN | — |

## deleteInternalJobPosting

- Definition: `applications/humanres/servicedef/services.xml:762`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: EmploymentApp; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| applicationId | id | IN | false |

## deleteJobInterview

- Definition: `applications/humanres/servicedef/services.xml:787`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: JobInterview; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| jobInterviewId | id | IN | false |

## deleteJobInterviewType

- Definition: `applications/humanres/servicedef/services.xml:808`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: JobInterviewType; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| jobInterviewTypeId | id | IN | false |

## deleteJobRequisition

- Definition: `applications/humanres/servicedef/services.xml:736`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: JobRequisition; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| jobRequisitionId | id | IN | false |

## deletePartyBenefit

- Definition: `applications/humanres/servicedef/services.xml:252`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PartyBenefit; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| benefitTypeId | id | IN | false |
| fromDate | date-time | IN | false |
| partyIdFrom | id | IN | false |
| partyIdTo | id | IN | false |
| roleTypeIdFrom | id | IN | false |
| roleTypeIdTo | id | IN | false |

## deletePartyQual

- Definition: `applications/humanres/servicedef/services.xml:51`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PartyQual; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| partyId | id | IN | false |
| partyQualTypeId | id | IN | false |

## deletePartyQualType

- Definition: `applications/humanres/servicedef/services.xml:926`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PartyQualType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyQualTypeId | id | IN | — |

## deletePartyResume

- Definition: `applications/humanres/servicedef/services.xml:71`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PartyResume; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| resumeId | id | IN | false |

## deletePartySkill

- Definition: `applications/humanres/servicedef/services.xml:91`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PartySkill; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | id | IN | false |
| skillTypeId | id | IN | false |

## deletePayGrade

- Definition: `applications/humanres/servicedef/services.xml:274`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PayGrade; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| payGradeId | id | IN | false |

## deletePayHistory

- Definition: `applications/humanres/servicedef/services.xml:298`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PayHistory; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emplFromDate | date-time | IN | false |
| fromDate | date-time | IN | false |
| partyIdFrom | id | IN | false |
| partyIdTo | id | IN | false |
| roleTypeIdFrom | id | IN | false |
| roleTypeIdTo | id | IN | false |

## deletePayrollPreference

- Definition: `applications/humanres/servicedef/services.xml:325`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PayrollPreference; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | id | IN | false |
| payrollPreferenceSeqId | id | IN | false |
| roleTypeId | id | IN | false |

## deletePerfRatingType

- Definition: `applications/humanres/servicedef/services_ability.xml:38`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PerfRatingType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| perfRatingTypeId | id | IN | — |

## deletePerfReview

- Definition: `applications/humanres/servicedef/services.xml:126`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PerfReview; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| employeePartyId | id | IN | false |
| employeeRoleTypeId | id | IN | false |
| perfReviewId | id | IN | false |

## deletePerfReviewItem

- Definition: `applications/humanres/servicedef/services.xml:161`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PerfReviewItem; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| employeePartyId | id | IN | false |
| employeeRoleTypeId | id | IN | false |
| perfReviewId | id | IN | false |
| perfReviewItemSeqId | id | IN | false |

## deletePerfReviewItemType

- Definition: `applications/humanres/servicedef/services_ability.xml:54`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PerfReviewItemType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| perfReviewItemTypeId | id | IN | — |

## deletePerformanceNote

- Definition: `applications/humanres/servicedef/services.xml:182`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PerformanceNote; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |

## deleteResponsibilityType

- Definition: `applications/humanres/servicedef/services.xml:558`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ResponsibilityType; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| responsibilityTypeId | id | IN | false |

## deleteSalaryStep

- Definition: `applications/humanres/servicedef/services.xml:345`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: SalaryStep; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| payGradeId | id | IN | false |
| salaryStepSeqId | id | IN | false |

## deleteSkillType

- Definition: `applications/humanres/servicedef/services.xml:517`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: SkillType; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| skillTypeId | id | IN | false |

## deleteTerminationReason

- Definition: `applications/humanres/servicedef/services.xml:366`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: TerminationReason; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| terminationReasonId | id | IN | false |

## deleteTerminationType

- Definition: `applications/humanres/servicedef/services.xml:579`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: TerminationType; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| terminationTypeId | id | IN | false |

## deleteTrainingTypes

- Definition: `applications/humanres/servicedef/services.xml:880`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: TrainingClassType; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| trainingClassTypeId | id | IN | false |

## deleteUnemploymentClaim

- Definition: `applications/humanres/servicedef/services.xml:385`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: UnemploymentClaim; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| unemploymentClaimId | id | IN | false |

## deleteValidResponsibility

- Definition: `applications/humanres/servicedef/services.xml:491`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ValidResponsibility; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emplPositionTypeId | id | IN | false |
| fromDate | date-time | IN | false |
| responsibilityTypeId | id | IN | false |

## expireEmplPositionTypeClass

- Definition: `applications/humanres/servicedef/services_position.xml:37`
- Engine: entity-auto; location: —; invoke: expire
- Default entity: EmplPositionTypeClass; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emplPositionClassTypeId | id | IN | — |
| emplPositionTypeId | id | IN | — |
| fromDate | date-time | IN | — |

## expirePayHistory

- Definition: `applications/humanres/servicedef/services.xml:303`
- Engine: entity-auto; location: —; invoke: expire
- Default entity: PayHistory; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emplFromDate | date-time | IN | false |
| fromDate | date-time | IN | false |
| partyIdFrom | id | IN | false |
| partyIdTo | id | IN | false |
| roleTypeIdFrom | id | IN | false |
| roleTypeIdTo | id | IN | false |

## getCurrentPartyEmploymentData

- Definition: `applications/humanres/servicedef/services.xml:694`
- Engine: simple; location: component://humanres/minilang/HumanResServices.xml; invoke: getCurrentPartyEmploymentData
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emplPosition | org.apache.ofbiz.entity.GenericValue | OUT | true |
| emplPositionRateAmount | org.apache.ofbiz.entity.GenericValue | OUT | true |
| emplPositionRateType | org.apache.ofbiz.entity.GenericValue | OUT | true |
| emplPositionType | org.apache.ofbiz.entity.GenericValue | OUT | true |
| employment | org.apache.ofbiz.entity.GenericValue | OUT | true |
| partyBenefitTypes | java.util.List | OUT | true |
| partyId | String | IN | — |

## humanResManagerPermission

- Definition: `applications/humanres/servicedef/services.xml:497`
- Engine: simple; location: component://humanres/minilang/permission/HumanResPermissionServices.xml; invoke: humanResManagerPermission
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## updateAgreementEmploymentAppl

- Definition: `applications/humanres/servicedef/services.xml:638`
- Engine: entity-auto; location: —; invoke: update
- Default entity: AgreementEmploymentAppl; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementDate | date-time | IN | true |
| agreementId | id | IN | false |
| agreementItemSeqId | id | IN | false |
| fromDate | date-time | IN | false |
| partyIdFrom | id | IN | false |
| partyIdTo | id | IN | false |
| roleTypeIdFrom | id | IN | false |
| roleTypeIdTo | id | IN | false |
| thruDate | date-time | IN | true |

## updateApprovalStatus

- Definition: `applications/humanres/servicedef/services.xml:814`
- Engine: entity-auto; location: —; invoke: update
- Default entity: EmploymentApp; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| applicationDate | date-time | IN | true |
| applicationId | id | IN | false |
| applyingPartyId | id | IN | true |
| approverPartyId | id | IN | true |
| emplPositionId | id | IN | true |
| employmentAppSourceTypeId | id | IN | true |
| jobRequisitionId | id | IN | true |
| referredByPartyId | id | IN | true |
| statusId | id | IN | true |

## updateBenefitType

- Definition: `applications/humanres/servicedef/services_employment.xml:32`
- Engine: entity-auto; location: —; invoke: update
- Default entity: BenefitType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| benefitName | name | IN | true |
| benefitTypeId | id | IN | — |
| description | description | IN | true |
| employerPaidPercentage | floating-point | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## updateEmplLeave

- Definition: `applications/humanres/servicedef/services.xml:659`
- Engine: entity-auto; location: —; invoke: update
- Default entity: EmplLeave; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| approverPartyId | id | IN | false |
| description | description | IN | true |
| emplLeaveReasonTypeId | id | IN | true |
| fromDate | date-time | IN | false |
| leaveStatus | id | IN | true |
| leaveTypeId | id | IN | false |
| partyId | id | IN | false |
| thruDate | date-time | IN | false |

## updateEmplLeaveReasonType

- Definition: `applications/humanres/servicedef/services.xml:894`
- Engine: entity-auto; location: —; invoke: update
- Default entity: EmplLeaveReasonType; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | false |
| emplLeaveReasonTypeId | id | IN | false |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## updateEmplLeaveStatus

- Definition: `applications/humanres/servicedef/services.xml:908`
- Engine: entity-auto; location: —; invoke: update
- Default entity: EmplLeave; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| approverPartyId | id | IN | true |
| description | description | IN | true |
| emplLeaveReasonTypeId | id | IN | true |
| fromDate | date-time | IN | false |
| leaveStatus | id | IN | true |
| leaveTypeId | id | IN | false |
| partyId | id | IN | false |
| thruDate | date-time | IN | true |

## updateEmplLeaveType

- Definition: `applications/humanres/servicedef/services.xml:681`
- Engine: entity-auto; location: —; invoke: update
- Default entity: EmplLeaveType; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | false |
| hasTable | indicator | IN | true |
| leaveTypeId | id | IN | false |
| parentTypeId | id | IN | true |

## updateEmplPosition

- Definition: `applications/humanres/servicedef/services.xml:399`
- Engine: entity-auto; location: —; invoke: update
- Default entity: EmplPosition; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| actualFromDate | date-time | IN | true |
| actualThruDate | date-time | IN | true |
| budgetId | id | IN | true |
| budgetItemSeqId | id | IN | true |
| emplPositionId | id | IN | false |
| emplPositionTypeId | id | IN | true |
| estimatedFromDate | date-time | IN | true |
| estimatedThruDate | date-time | IN | true |
| exemptFlag | indicator | IN | true |
| fulltimeFlag | indicator | IN | true |
| partyId | id | IN | true |
| salaryFlag | indicator | IN | true |
| statusId | id | IN | true |
| temporaryFlag | indicator | IN | true |

## updateEmplPositionClassType

- Definition: `applications/humanres/servicedef/services_position.xml:47`
- Engine: entity-auto; location: —; invoke: update
- Default entity: EmplPositionClassType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| emplPositionClassTypeId | id | IN | — |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## updateEmplPositionFulfillment

- Definition: `applications/humanres/servicedef/services.xml:420`
- Engine: entity-auto; location: —; invoke: update
- Default entity: EmplPositionFulfillment; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| emplPositionId | id | IN | false |
| fromDate | date-time | IN | false |
| partyId | id | IN | false |
| thruDate | date-time | IN | true |

## updateEmplPositionReportingStruct

- Definition: `applications/humanres/servicedef/services.xml:442`
- Engine: entity-auto; location: —; invoke: update
- Default entity: EmplPositionReportingStruct; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| emplPositionIdManagedBy | id | IN | false |
| emplPositionIdReportingTo | id | IN | false |
| fromDate | date-time | IN | false |
| primaryFlag | indicator | IN | true |
| thruDate | date-time | IN | true |

## updateEmplPositionResponsibility

- Definition: `applications/humanres/servicedef/services.xml:463`
- Engine: entity-auto; location: —; invoke: update
- Default entity: EmplPositionResponsibility; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| emplPositionId | id | IN | false |
| fromDate | date-time | IN | false |
| responsibilityTypeId | id | IN | false |
| thruDate | date-time | IN | true |

## updateEmplPositionType

- Definition: `applications/humanres/servicedef/services.xml:593`
- Engine: entity-auto; location: —; invoke: update
- Default entity: EmplPositionType; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | false |
| emplPositionTypeId | id | IN | false |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## updateEmplPositionTypeClass

- Definition: `applications/humanres/servicedef/services_position.xml:32`
- Engine: entity-auto; location: —; invoke: update
- Default entity: EmplPositionTypeClass; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emplPositionClassTypeId | id | IN | — |
| emplPositionTypeId | id | IN | — |
| fromDate | date-time | IN | — |
| standardHoursPerWeek | floating-point | IN | true |
| thruDate | date-time | IN | true |

## updateEmplPositionTypeRate

- Definition: `applications/humanres/servicedef/services.xml:606`
- Engine: simple; location: component://humanres/minilang/HumanResServices.xml; invoke: updateEmplPositionTypeRate
- Default entity: EmplPositionTypeRate; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emplPositionTypeId | id | IN | false |
| fromDate | date-time | IN | true |
| payGradeId | id | IN | true |
| periodTypeId | String | IN | — |
| rateAmount | BigDecimal | IN | true |
| rateCurrencyUomId | String | IN | true |
| rateTypeId | id | IN | false |
| salaryStepSeqId | id | IN | true |
| thruDate | date-time | IN | true |

## updateEmployment

- Definition: `applications/humanres/servicedef/services.xml:204`
- Engine: entity-auto; location: —; invoke: update
- Default entity: Employment; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| partyIdFrom | id | IN | false |
| partyIdTo | id | IN | false |
| roleTypeIdFrom | id | IN | false |
| roleTypeIdTo | id | IN | false |
| terminationReasonId | id | IN | true |
| terminationTypeId | id | IN | true |
| thruDate | date-time | IN | true |

## updateEmploymentApp

- Definition: `applications/humanres/servicedef/services.xml:224`
- Engine: entity-auto; location: —; invoke: update
- Default entity: EmploymentApp; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| applicationDate | date-time | IN | true |
| applicationId | id | IN | false |
| applyingPartyId | id | IN | true |
| approverPartyId | id | IN | true |
| emplPositionId | id | IN | true |
| employmentAppSourceTypeId | id | IN | true |
| jobRequisitionId | id | IN | true |
| referredByPartyId | id | IN | true |
| statusId | id | IN | true |

## updateEmploymentAppSourceType

- Definition: `applications/humanres/servicedef/services_employment.xml:48`
- Engine: entity-auto; location: —; invoke: update
- Default entity: EmploymentAppSourceType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| employmentAppSourceTypeId | id | IN | — |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## updateInternalJobPosting

- Definition: `applications/humanres/servicedef/services.xml:752`
- Engine: entity-auto; location: —; invoke: update
- Default entity: EmploymentApp; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| applicationDate | date-time | IN | true |
| applicationId | id | IN | false |
| applyingPartyId | id | IN | false |
| approverPartyId | id | IN | false |
| emplPositionId | id | IN | true |
| employmentAppSourceTypeId | id | IN | true |
| jobRequisitionId | id | IN | false |
| referredByPartyId | id | IN | true |
| statusId | id | IN | true |

## updateJobInterview

- Definition: `applications/humanres/servicedef/services.xml:777`
- Engine: entity-auto; location: —; invoke: update
- Default entity: JobInterview; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| gradeSecuredEnumId | id | IN | true |
| jobInterviewDate | date | IN | true |
| jobInterviewId | id | IN | false |
| jobInterviewResult | id | IN | true |
| jobInterviewTypeId | id | IN | false |
| jobIntervieweePartyId | id | IN | false |
| jobInterviewerPartyId | id | IN | true |
| jobRequisitionId | id | IN | false |

## updateJobInterviewType

- Definition: `applications/humanres/servicedef/services.xml:800`
- Engine: entity-auto; location: —; invoke: update
- Default entity: JobInterviewType; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | false |
| jobInterviewTypeId | id | IN | false |

## updateJobRequisition

- Definition: `applications/humanres/servicedef/services.xml:724`
- Engine: entity-auto; location: —; invoke: update
- Default entity: JobRequisition; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| age | numeric | IN | true |
| durationMonths | numeric | IN | true |
| examTypeEnumId | id | IN | true |
| experienceMonths | numeric | IN | false |
| experienceYears | numeric | IN | false |
| gender | indicator | IN | true |
| jobLocation | id | IN | false |
| jobPostingTypeEnumId | id | IN | true |
| jobRequisitionDate | date | IN | true |
| jobRequisitionId | id | IN | false |
| noOfResources | numeric | IN | false |
| qualification | id-long | IN | true |
| requiredOnDate | date | IN | true |
| skillTypeId | id | IN | false |

## updatePartyBenefit

- Definition: `applications/humanres/servicedef/services.xml:245`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PartyBenefit; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| actualEmployerPaidPercent | floating-point | IN | true |
| availableTime | numeric | IN | true |
| benefitTypeId | id | IN | false |
| cost | currency-amount | IN | true |
| fromDate | date-time | IN | false |
| partyIdFrom | id | IN | false |
| partyIdTo | id | IN | false |
| periodTypeId | id | IN | true |
| roleTypeIdFrom | id | IN | false |
| roleTypeIdTo | id | IN | false |
| thruDate | date-time | IN | true |

## updatePartyQual

- Definition: `applications/humanres/servicedef/services.xml:44`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PartyQual; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| partyId | id | IN | false |
| partyQualTypeId | id | IN | false |
| qualificationDesc | id-long | IN | true |
| statusId | id | IN | true |
| thruDate | date-time | IN | true |
| title | id-long | IN | true |
| verifStatusId | id | IN | true |

## updatePartyQualType

- Definition: `applications/humanres/servicedef/services.xml:921`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PartyQualType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| partyQualTypeId | id | IN | — |

## updatePartyResume

- Definition: `applications/humanres/servicedef/services.xml:64`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PartyResume; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | true |
| partyId | id | IN | true |
| resumeDate | date-time | IN | true |
| resumeId | id | IN | false |
| resumeText | long-varchar | IN | true |

## updatePartySkill

- Definition: `applications/humanres/servicedef/services.xml:84`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PartySkill; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | id | IN | false |
| rating | numeric | IN | true |
| skillLevel | numeric | IN | true |
| skillTypeId | id | IN | false |
| startedUsingDate | date-time | IN | true |
| yearsExperience | numeric | IN | true |

## updatePayGrade

- Definition: `applications/humanres/servicedef/services.xml:266`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PayGrade; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| payGradeId | id | IN | false |
| payGradeName | name | IN | false |

## updatePayHistory

- Definition: `applications/humanres/servicedef/services.xml:288`
- Engine: simple; location: component://humanres/minilang/HumanResServices.xml; invoke: updatePayHistory
- Default entity: PayHistory; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | currency-amount | IN | true |
| changeDate | Timestamp | IN | true |
| comments | comment | IN | true |
| emplFromDate | date-time | IN | false |
| fromDate | Timestamp | OUT | — |
| partyIdFrom | id | IN | false |
| partyIdTo | id | IN | false |
| payGradeId | id | IN | true |
| periodTypeId | id | IN | true |
| roleTypeIdFrom | id | IN | false |
| roleTypeIdTo | id | IN | false |
| salaryStepSeqId | id | IN | true |
| thruDate | date-time | IN | true |

## updatePayrollPreference

- Definition: `applications/humanres/servicedef/services.xml:318`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PayrollPreference; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accountNumber | short-varchar | IN | true |
| bankName | name | IN | true |
| deductionTypeId | id | IN | true |
| flatAmount | currency-amount | IN | true |
| fromDate | date-time | IN | true |
| partyId | id | IN | false |
| paymentMethodTypeId | id | IN | true |
| payrollPreferenceSeqId | id | IN | false |
| percentage | floating-point | IN | true |
| periodTypeId | id | IN | true |
| roleTypeId | id | IN | false |
| routingNumber | short-varchar | IN | true |
| thruDate | date-time | IN | true |

## updatePerfRatingType

- Definition: `applications/humanres/servicedef/services_ability.xml:33`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PerfRatingType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| perfRatingTypeId | id | IN | — |

## updatePerfReview

- Definition: `applications/humanres/servicedef/services.xml:119`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PerfReview; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| emplPositionId | id | IN | true |
| employeePartyId | id | INOUT | false |
| employeeRoleTypeId | id | INOUT | false |
| fromDate | date-time | IN | true |
| managerPartyId | id | IN | true |
| managerRoleTypeId | id | IN | true |
| paymentId | id | IN | true |
| perfReviewId | id | INOUT | false |
| thruDate | date-time | IN | true |

## updatePerfReviewItem

- Definition: `applications/humanres/servicedef/services.xml:154`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PerfReviewItem; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| employeePartyId | id | IN | false |
| employeeRoleTypeId | id | IN | false |
| perfRatingTypeId | id | IN | true |
| perfReviewId | id | IN | false |
| perfReviewItemSeqId | id | IN | false |
| perfReviewItemTypeId | id | IN | true |

## updatePerfReviewItemType

- Definition: `applications/humanres/servicedef/services_ability.xml:49`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PerfReviewItemType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| perfReviewItemTypeId | id | IN | — |

## updatePerformanceNote

- Definition: `applications/humanres/servicedef/services.xml:175`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PerformanceNote; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| communicationDate | date-time | IN | true |
| fromDate | date-time | IN | false |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |
| thruDate | date-time | IN | true |

## updateResponsibilityType

- Definition: `applications/humanres/servicedef/services.xml:551`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ResponsibilityType; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | false |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| responsibilityTypeId | id | IN | false |

## updateSalaryStep

- Definition: `applications/humanres/servicedef/services.xml:339`
- Engine: entity-auto; location: —; invoke: update
- Default entity: SalaryStep; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | currency-amount | IN | true |
| createdByUserLogin | id-vlong | IN | true |
| dateModified | date-time | IN | true |
| fromDate | date-time | IN | false |
| lastModifiedByUserLogin | id-vlong | IN | true |
| payGradeId | id | IN | false |
| salaryStepSeqId | id | IN | false |
| thruDate | date-time | IN | true |

## updateSkillType

- Definition: `applications/humanres/servicedef/services.xml:510`
- Engine: entity-auto; location: —; invoke: update
- Default entity: SkillType; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | false |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| skillTypeId | id | IN | false |

## updateTerminationReason

- Definition: `applications/humanres/servicedef/services.xml:359`
- Engine: entity-auto; location: —; invoke: update
- Default entity: TerminationReason; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | false |
| terminationReasonId | id | IN | false |

## updateTerminationType

- Definition: `applications/humanres/servicedef/services.xml:572`
- Engine: entity-auto; location: —; invoke: update
- Default entity: TerminationType; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | false |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| terminationTypeId | id | IN | false |

## updateTrainingStatus

- Definition: `applications/humanres/servicedef/services.xml:821`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PersonTraining; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| approvalStatus | short-varchar | IN | true |
| approverId | id | IN | true |
| fromDate | date-time | IN | false |
| partyId | id | IN | false |
| reason | description | IN | false |
| thruDate | date-time | IN | true |
| trainingClassTypeId | id | IN | false |
| trainingRequestId | id | IN | true |
| workEffortId | id | IN | true |

## updateTrainingTypes

- Definition: `applications/humanres/servicedef/services.xml:872`
- Engine: entity-auto; location: —; invoke: update
- Default entity: TrainingClassType; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | false |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| trainingClassTypeId | id | IN | false |

## updateUnemploymentClaim

- Definition: `applications/humanres/servicedef/services.xml:379`
- Engine: entity-auto; location: —; invoke: update
- Default entity: UnemploymentClaim; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| fromDate | date-time | IN | true |
| partyIdFrom | id | IN | true |
| partyIdTo | id | IN | true |
| roleTypeIdFrom | id | IN | true |
| roleTypeIdTo | id | IN | true |
| statusId | id | IN | true |
| thruDate | date-time | IN | true |
| unemploymentClaimDate | date-time | IN | true |
| unemploymentClaimId | id | IN | false |

## updateValidResponsibility

- Definition: `applications/humanres/servicedef/services.xml:484`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ValidResponsibility; auth: true
- Use transaction: —; timeout: —
- Permission service: humanResManagerPermission; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| emplPositionTypeId | id | IN | false |
| fromDate | date-time | IN | false |
| responsibilityTypeId | id | IN | false |
| thruDate | date-time | IN | true |
