# OFBiz Spring Boot migration — English guide

This is the English entry point for the migration alongside the original OFBiz
applications. It describes the implemented architecture and the evidence obtained,
not a claim that every original business branch has been ported. The Japanese
[migration report](MIGRATION_REPORT.md) and [playbook](PLAYBOOK.md) remain available
as historical implementation records.

## Status and evidence

| Measure | Recorded result | Source / qualification |
|---|---|---|
| Original ECA rules catalogued | 348: 163 active, 31 disabled hand-wired, 151 target-unported, 3 unsupported-condition | [Generated ECA coverage](eca-coverage.md) |
| WP3 all-module JUnit baseline | **3,347 tests**, 0 failures, 0 errors, 0 skipped | [Verification history](equivalence/history.md); a later PR #18 fix added one accounting regression, so this is the recorded WP3 baseline, not a frozen current test count |
| PostgreSQL old-vs-new equivalence | **28/28 MATCH**, 0 accepted differences, 0 unresolved mismatches | [Report](equivalence/report.md) and [machine-readable evidence](equivalence/results.json) |

An active ECA rule has at least one statically ported action; it does **not** mean
every action in that rule is implemented. Runtime registration is authoritative.
Unregistered targets are skipped and counted. Hand-wired rules are disabled in
the generic engine to avoid duplicating existing inline side effects. Entity ECAs
cover entity-auto operations; arbitrary direct JPA writes are not intercepted.
See the coverage document for each rule and its source location.

## Architecture

- [springboot-datamodel](../../migration/springboot-datamodel/) generates the shared
  JPA entity/repository model from original entity XML at build time. Its
  [Gradle configuration](../../migration/springboot-datamodel/build.gradle) defines
  the input model files and generation task. Original view-entities are outside
  the generated shared entity model.
- [springboot-common](../../migration/springboot-common/) supplies the REST service
  boundary, authentication/authorization, entity-auto execution, entity lookup,
  sequence allocation using OFBiz's `SequenceValueItem`, and shared errors.
  [MigrationServiceDispatcher](../../migration/springboot-common/src/main/java/org/apache/ofbiz/migration/common/MigrationServiceDispatcher.java)
  routes requests to registered component handlers or generated entity-auto
  services. Authenticated identity, rather than caller-supplied identity, is
  propagated to service/ECA context.
- The ten business modules are [accounting](../../migration/accounting-springboot/),
  [commonext](../../migration/commonext-springboot/), [content](../../migration/content-springboot/),
  [humanres](../../migration/humanres-springboot/), [manufacturing](../../migration/manufacturing-springboot/),
  [marketing](../../migration/marketing-springboot/), [order](../../migration/order-springboot/),
  [party](../../migration/party-springboot/), [product](../../migration/product-springboot/),
  and [workeffort](../../migration/workeffort-springboot/). They combine generated
  service catalogs with hand-ported business handlers and component ports/adapters.
  HR retains its own generated entity model/security implementation.
- [eca_catalog.py](../../migration/tools/eca_catalog.py) reads original SECA/EECA
  XML and generates the catalog and coverage report through the common module's
  `generateEcaCatalog` task.
  [EcaEngine](../../migration/springboot-common/src/main/java/org/apache/ofbiz/migration/common/EcaEngine.java)
  evaluates conditions/actions around service phases and transaction callbacks,
  propagates results, guards recursion, and records unsupported actions. Business
  calls must cross dispatcher/entity-auto boundaries to receive those effects.

The strangler model preserves OFBiz table/column names instead of introducing a
replacement business schema. Normal module defaults remain H2. The
[equivalence-only PostgreSQL profile](../../migration/equivalence/src/main/resources/application-equivalence.yml)
uses the original OFBiz-created schema with `ddl-auto=none`, not Hibernate schema
creation. The harness compares two database clones of the **same schema/data**:
original OFBiz writes `equiv_old`, while migration processes write `equiv_new`.
It does not run competing old/new writers against one comparison database.

The [equivalence application](../../migration/equivalence/) co-hosts
party/order/product/accounting/WorkEffort/marketing/content/manufacturing so that
registered cross-component calls are local. HR runs separately against the same
`equiv_new` database because its entity scan cannot safely be combined with the
shared model. Standalone modules do not automatically gain every co-hosted target;
their adapter fallback behavior still applies. This harness is not a production
deployment recipe.

## Build and test

Run commands from the repository root. Requirements: Java 17, Python 3, network
access for Gradle/Maven dependencies, and Docker for PostgreSQL tests and the
equivalence harness. The [wrapper configuration](../../gradle/wrapper/gradle-wrapper.properties)
pins Gradle; do not substitute an arbitrary system Gradle version.

The wrapper JAR is intentionally gitignored. On a fresh checkout, bootstrap it
using the checked-in [initialization script](../../gradle/init-gradle-wrapper.sh):

```sh
sh gradle/init-gradle-wrapper.sh
./gradlew :migration:order-springboot:build
```

The Windows counterpart is [init-gradle-wrapper.ps1](../../gradle/init-gradle-wrapper.ps1).
Keep downloaded wrapper binaries out of commits. Generation tasks run as build
dependencies; do not hand-edit their generated Java/catalog outputs.

Run all twelve migration module test tasks:

```sh
./gradlew --max-workers=4 \
  :migration:springboot-datamodel:test \
  :migration:springboot-common:test \
  :migration:accounting-springboot:test \
  :migration:commonext-springboot:test \
  :migration:content-springboot:test \
  :migration:humanres-springboot:test \
  :migration:manufacturing-springboot:test \
  :migration:marketing-springboot:test \
  :migration:order-springboot:test \
  :migration:party-springboot:test \
  :migration:product-springboot:test \
  :migration:workeffort-springboot:test
```

H2 exercises the normal module configuration. PostgreSQL Testcontainers tests are
part of module `test` tasks, not the separate old-vs-new harness. For example,
[order PostgresSmokeTest](../../migration/order-springboot/src/test/java/org/apache/ofbiz/migration/order/PostgresSmokeTest.java)
and [accounting PostgresSmokeTest](../../migration/accounting-springboot/src/test/java/org/apache/ofbiz/migration/accounting/PostgresSmokeTest.java)
exercise order completion and GL posting. They use `disabledWithoutDocker=true`:
a green build with skipped tests is **not** PostgreSQL verification. Ensure Docker
is available and inspect the JUnit skipped counts.

For a standalone local application, for example:

```sh
./gradlew :migration:order-springboot:bootRun --args='--spring.profiles.active=local'
```

Use the component's own configuration/documentation for users, ports, and adapters;
do not assume that standalone mode reproduces the co-hosted equivalence topology.

## Run and interpret PostgreSQL equivalence

Read the [harness operating guide](../../migration/equivalence/README.md) first.
It requires Java 17, Python 3, Docker, the Gradle wrapper, and free loopback ports
15432, 18443/18080, 18090, and 18091. The setup is destructive **only for its
disposable labelled Docker container/databases**; never point it at shared or
production data. Application HTTP endpoints bind to loopback and use per-run
credentials; this is intentionally a local test profile, not public HTTP Basic auth.

```sh
# First run: load original OFBiz seed/demo data and create both comparison clones.
bash migration/equivalence/run.sh
# Subsequent run: reuse the loaded template, but recreate both comparison clones.
bash migration/equivalence/run.sh --reuse-template
```

[setup-db.sh](../../migration/equivalence/setup-db.sh) documents/recreates the
PostgreSQL template setup. Generated init configuration stays outside the repo;
original application/framework files are not rewritten. The original runner
invokes the real dispatcher and commits transactions so ECAs fire; the new runner
uses authenticated REST through the migration dispatcher.

- [cases.json](../../migration/equivalence/cases.json) is the ordered manifest:
  logical inputs, equivalent run-as users, captured IDs, snapshot tables, and
  business assertions. It covers party, content, marketing, manufacturing, HR,
  WorkEffort, negative/rollback cases, and sales order → inventory → shipment →
  invoice → payment → posted GL.
- [report.md](equivalence/report.md) is the human-readable per-case verdict table.
  MATCH certifies only the exercised cases and declared row deltas/assertions.
- [results.json](equivalence/results.json) retains machine-readable totals,
  responses/wire bodies, row changes, assertions, and exact differences.
- [history.md](equivalence/history.md) explains mismatch → cause → action and the
  recorded verification baseline. It is an investigation history, not a list of
  differences silently accepted today.

Rows are compared strictly for statuses, amounts, role types, posted flags, and
multiplicity. Only generated IDs/timestamps, decimal scale, and ordering are
normalized; explicitly supplied timestamp values remain observable. Responses
compare the original declared OUT/INOUT parameters, including implemented
interfaces, plus success/error and available shared error codes/categories.
Localized wording and migration-only response extensions are informational, not
mismatches. A positive case must succeed; a negative case must fail without
changing declared business rows. The current report accepts **no differences**.

The comparator's own tests are independent of the Java suites:

```sh
PYTHONDONTWRITEBYTECODE=1 python3 -m unittest discover -s migration/equivalence -p 'test_*.py' -v
```

## Faithful-to-original behavior retained deliberately

These reviewed behaviors were not changed merely because they looked surprising:

| Behavior | Original evidence and migration consequence |
|---|---|
| `checkAndUpdateWorkEffort` has no shipment permission check | [WorkEffort services](../../applications/workeffort/servicedef/services.xml#L427), lines 427–432: the service is authenticated, but has no shipment permission-service. Do not describe it as unauthenticated. |
| Shipment-driven updates still require WorkEffort update permission | [WorkEffort services](../../applications/workeffort/servicedef/services.xml#L85), lines 85–88: nested `updateWorkEffort` checks `workEffortGenericPermission` UPDATE. A caller with only shipment permission can therefore fail when linked events need updating. |
| Item cancellation reason is not rolled up to the header | [OrderServices](../../applications/order/src/main/java/org/apache/ofbiz/order/order/OrderServices.java#L2091), lines 2091–2095: the header roll-up context contains order ID/status/user, not `changeReason`. |
| Re-shipment readiness considers only the first billing invoice per order | [InvoiceServices](../../applications/accounting/src/main/java/org/apache/ofbiz/accounting/invoice/InvoiceServices.java#L1321), lines 1321–1343: `queryFirst()` precedes the invoice status check. This inherited limitation is not a promise to ready every invoice. |
| Caller-supplied order IDs are allowed | [OrderServices](../../applications/order/src/main/java/org/apache/ofbiz/order/order/OrderServices.java#L448), lines 448–465: generate only if empty. Lines 583–589 insert the header and reject insertion failures; accepting an ID does not authorize overwriting an existing order. |

These are compatibility decisions, not recommendations for designing a new API.

## Explicitly not certified

The authoritative detailed boundary is [unported.md](equivalence/unported.md).
In particular, the 28-case result does **not** certify:

- Purchase, return, and drop-shipment invoicing; taxes/order adjustments;
  order/billing-account terms; specialized invoice-item derivations; multi-order
  consolidation; foreign-currency conversions or their specialized accounting.
- Foreign-currency payment matching, account-specific automatic-creation policy
  overrides, or specialized billing-account, tax-authority, refund, and
  payment-to-payment application accounting.
- Complex percentage/discount shipping adjustments or allocations across multiple
  facilities. Average-cost computation remains legacy migration behavior and is
  not certified; the exercised costing comparison concerns FIFO/LIFO layers.
- Every standalone adapter topology, every original Java/Groovy branch, or all
  catalogued ECA actions. Unported targets and unsupported conditions remain
  visible in the coverage report; they are not implicitly accepted differences.

Keep H2 defaults, the equivalence-only PostgreSQL profile, and these certification
boundaries separate when interpreting a passing build or planning deployment.
