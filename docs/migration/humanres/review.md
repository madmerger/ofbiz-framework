# HumanRes Spring Boot pilot acceptance review

Acceptance criteria are taken from §11 of `spec.md`. The spec defines only the IDs listed below; gaps in numbering are intentional.

Test command:

```sh
/home/ubuntu/.gradle/gradle-9.8.2/bin/gradle --no-daemon :migration:humanres-springboot:test
```

Result: **151 tests, 0 failures, 1 skipped**. The skipped test is the PostgreSQL Testcontainers smoke test; see AC-04.

| AC | Result | Evidence |
| --- | --- | --- |
| AC-01 | PASS | `EntityAutoAcceptanceTest.ac01_allInventoryEntitiesAreMapped` |
| AC-02 | PASS | `EntityAutoAcceptanceTest.ac02_generatedEntityReceivesAllStampColumns` |
| AC-03 | PASS | Final diff review found no changes under `applications/`, `framework/`, or `plugins/`; root `settings.gradle` contains only the D1-allowed module include. |
| AC-04 | PARTIAL | H2 startup and CRUD are covered by the module tests and local-profile smoke. `PostgresSmokeTest.ac04_postgresSchemaAndCrudSmoke` covers PostgreSQL create/read/update/delete but is skipped: Docker daemon minimum API is 1.40, while Testcontainers 1.21.3's client requests API 1.32 (`client version 1.32 is too old. Minimum supported API version is 1.40`). PostgreSQL behavior is not verified. |
| AC-10 | PASS | Parameterized `EntityAutoAcceptanceTest.ac10_generatedCatalogMatchesInventory` checks all 121 entity-auto service catalog entries against the inventory. |
| AC-11 | PASS | `EntityAutoAcceptanceTest.ac11_sequenceIdsAndSubSequencesFollowOfbizShape` |
| AC-12 | PASS | `EntityAutoAcceptanceTest.ac12_missingFromDateDefaultsToNow` |
| AC-13 | PASS | `EntityAutoAcceptanceTest.ac13_duplicateCreateReturnsConflict` |
| AC-14 | PASS | `EntityAutoAcceptanceTest.ac14_requiredAndMissingEntityErrorsAreDistinct` |
| AC-15 | PASS | `EntityAutoAcceptanceTest.ac15_updatesOnlyProvidedFields` |
| AC-16 | PASS | `EntityAutoAcceptanceTest.ac16_rejectsStatusTransitionWithoutStatusValidChange` |
| AC-17 | PASS | `EntityAutoAcceptanceTest.ac17_expireAndDeleteAreDistinctOperations` |
| AC-18 | PASS | `EntityAutoAcceptanceTest.ac18_deniesDangerousText` |
| AC-20 | PASS | `HumanResBusinessServicesTest.ac20_createEmploymentCreatesEmploymentAndPayHistoryAtomically` checks successful creation and a duplicate PayHistory failure rolling back Employment. |
| AC-21 | PASS | `HumanResBusinessServicesTest.ac21_updatePayHistoryChangesExistingPayRecord` covers comments-only update and changed-amount expire/recreate. |
| AC-22 | PASS | `HumanResBusinessServicesTest.ac22_reportingStructCannotReportToItself` |
| AC-23 | PASS | `HumanResBusinessServicesTest.ac23_createEmployeeUsesPartyAdapter` checks returned party ID and employment relationship creation. |
| AC-24 | PASS | `HumanResBusinessServicesTest.ac24_positionTypeRateUpdateAndDeleteExpireRowsAndCallAccountingPort` |
| AC-25 | PASS | `HumanResBusinessServicesTest.ac25_currentEmploymentDataReturnsExpectedShape` seeds employment, benefit, position, and fulfillment data and checks returned data and accounting rates. |
| AC-26 | PASS | `HumanResBusinessServicesTest.ac26_applyTrainingCreatesRequestAndPersonTraining` checks both apply and assign paths create `PersonTraining` rows. |
| AC-27 | PASS | `HumanResBusinessServicesTest.ac27_internalOrganizationCanBeCreatedAndRemoved` |
| AC-28 | PASS | `HumanResBusinessServicesTest.ac28_publicHolidayRejectsDuplicateStartDate` checks duplicate rejection and the `WorkEffortPort` call. |
| AC-29 | PASS | `HumanResBusinessServicesTest.ac29_treeIncludesChildrenAndActivePositionsButExcludesInactiveAndExpired` checks organization/position nodes, inactive/expired filtering, and occupant nodes. |
| AC-30 | PASS | `HumanResBusinessServicesTest.ac30_multiServiceFailureRollsBackEarlierRows` |
| AC-40 | PASS | `HrTreeSecurityTest.ac40_anonymousIsRejectedBeforeTreeLookup` returns 401 before tree lookup. |
| AC-41 | PASS | `HrTreeSecurityTest.ac41_missingHumanResAuthorityIsRejectedBeforeTreeLookup` returns 403 before tree lookup. |
| AC-42 | PASS | `HrTreeSecurityTest.ac42_authorizedHumanResViewReachesTreeService` |
| AC-43 | PASS | `HrTreeSecurityTest.ac43_humanResAuthorityWithoutOfbtoolsBaseGateIsRejected` |
| AC-44 | PASS | `HrTreeSecurityTest.ac44_missingPerServiceActionIsRejectedBeforeEntityAutoDispatch` |
| AC-45 | PASS | `EntityAutoAcceptanceTest.ac45_localProfileUsesEnvironmentSuppliedPassword`; final source review found no committed plaintext password or database credential. |

The three local curl cases reproducing the legacy tree security behavior also passed: anonymous 401, authenticated user without HumanRes permission 403, and authorized admin 200.
