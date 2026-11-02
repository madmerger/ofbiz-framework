# PostgreSQL equivalence harness

**Work in progress:** the 22-step manifest covers party/content/marketing/manufacturing/
HR/WorkEffort and negative cases, plus order creation, approval, receipt, reservation,
and shipping probes. Explicit invoice READY/payment/application/PAID cases and complete
business-flow equivalence have not yet been implemented or verified.

## Prerequisites and safety

- Java 17, the repository Gradle wrapper, Python 3 (standard library only), Docker.
- Ports 15432 (PostgreSQL), 18443/18080 (original), 18090 (co-hosted migration),
  and 18091 (HR) must be available on loopback.
- All state/logs are in `~/.local/state/ofbiz-equivalence`, or `$EQUIV_STATE`.
- The setup only operates on Docker container `ofbiz-equivalence-harness`, labelled
  `org.ofbiz.equivalence=true`. It **destroys and recreates** its `equiv_template`,
  `equiv_old`, and `equiv_new` databases. Never point it at shared or production data.
- The database uses trust authentication on loopback for disposable demo data. Application
  credentials use a per-run random token, never the original demo password. The original
  dispatcher bridge requires that token and a loopback caller before impersonating a user.

## Run

```sh
bash migration/equivalence/run.sh
```

The default run loads the original OFBiz seed/demo data into `equiv_template`, then clones
it into `equiv_old` and `equiv_new`. The SQL clone commands are equivalent to:

```sh
createdb -h 127.0.0.1 -p 15432 -U ofbiz -T equiv_template equiv_old
createdb -h 127.0.0.1 -p 15432 -U ofbiz -T equiv_template equiv_new
```

For a debug rerun after a successful template load:

```sh
bash migration/equivalence/run.sh --reuse-template
```

`--skip-build` reuses built JARs and is only valid when Java/resources have not changed.
`--until N` executes a prefix and writes partial results in the state directory, never
overwriting the final report. All child JVMs are stopped on success or failure.

The generated Gradle init script remains outside the repository. It overlays original
datasource/component-loading configuration without editing `applications/` or `framework/`.
The out-of-tree original plugin is loaded only through that component-loading overlay.
The migration's equivalence-only profile uses the original PostgreSQL schema with
`ddl-auto=none`; normal module profiles remain H2.

## Boundaries

Original calls go through `LocalDispatcher.runSync` from the loopback JSON bridge. The
Groovy event transaction commits before HTTP completion, so service commit rules fire.
The new runner uses authenticated HTTP and the existing `MigrationServiceDispatcher`.
There are no direct migration-handler calls or replacement service implementations.

Party/order/product/accounting/WorkEffort/marketing/content/manufacturing are co-hosted
so registered cross-component dispatch is local. HR runs separately: it uses its own
generated entity model and security implementation and cannot share the common entity
scan without duplicate entity mappings. Both migration processes use `equiv_new`.

## Comparison contract

Each step declares service, equivalent run-as user, identical logical input, captured IDs,
snapshot tables, and optional independent business-row assertions. The harness takes
complete table snapshots before/after the call and compares **row deltas**, preserving
deletions, modifications, duplicate rows and counts. No database writes are performed by
the comparator. PostgreSQL query errors are fatal rather than silently omitting tables.

- Captured IDs are mapped by step and field domain, including declared foreign-key aliases.
- Additional declared generated keys are mapped only when each side creates exactly one row.
  Ambiguous multi-row mappings are not guessed.
- Generated timestamp fields explicitly listed in `compare.GENERATED_TIMES` compare presence,
  not their clock values. Null and present remain different. Explicit shipment dates are
  not in that list.
- Decimal scale and list/row ordering are normalized.
- Statuses, amounts, role types, posted flags and row counts are never suppressed.
- Responses compare only original declared OUT/INOUT parameters (including inherited
  interfaces), success/error classification, and shared error codes/categories. Localized
  message wording and migration extensions are retained as `responseInfo`, not mismatches.
  HTTP status and exact response bodies are also retained as evidence. Row deltas remain strict.
- Every positive case must actually succeed. Every negative case must actually fail and
  change no declared business rows. Both sides returning errors is not a positive match.
- Accepted differences, when introduced, require an investigation reason and an exact
  difference fingerprint. A changed mismatch cannot inherit a blanket exception.

Raw/normalized evidence is emitted in `results.json`; report generation is in `harness.py`.
Comparator regression tests:

```sh
PYTHONDONTWRITEBYTECODE=1 python3 -m unittest discover -s migration/equivalence -p 'test_*.py' -v
```
