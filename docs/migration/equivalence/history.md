# Equivalence investigation history (work in progress)

The harness has **not** established complete equivalence. No remaining mismatch is
accepted implicitly. In particular, missing ledger rows, role rows, reservations,
invoices, status transitions, and differing amounts must not be normalized away.

## Implemented fixes

| Observed mismatch | Original behavior / cause | Action |
|---|---|---|
| Party creation omitted audit fields and initial status history | Original person/group creation writes both | `df1d681247`: preserve creation metadata and initial PartyStatus; regression tests |
| ElectronicText contained a PostgreSQL large-object identifier instead of its text | JPA `@Lob` mapped OFBiz TEXT to an OID | `200ae40e45`: generate native TEXT mappings; generator and PostgreSQL regression tests |
| Omitted contact solicitation preference became Y | Original leaves it null | `cbe82dd258`: preserve unspecified preference; omitted/Y/N tests |
| WorkEffort revisions and last modifier did not change | Original increments revision only for actual changes | `af017290ae`: preserve revision/modifier semantics; empty, identical, changed and missing-record tests |
| Shipment entity-auto creation lacked audit fields and status actor | Original EntityAutoEngine sets creation/modifier metadata and changeByUserLoginId | `d7dff2788e`: generic entity-auto audit support; authenticated REST and missing-actor tests |
| Shipment-linked WorkEffort status history omitted its actor | Original updateWorkEffort records setByUserLogin | `211beb995c`: preserve actor; shipment synchronization regression assertion |
| Sales order defaults and supplied ship groups differed | Original uses store sales channel, accounting invoice-per-shipment setting, explicit ship-group data, and nullable item cancellation/audit fields | `a23e6e507e`: preserve those values, optional END_USER_CUSTOMER role, and null-safe cancellation; order regression tests |
| Shipping completed items without rolling up the order header | Product's local order adapter wrote directly instead of dispatching the original order service | `2318990c70`: dispatch to the co-hosted order handler, retaining standalone fallback; delegation/error tests and PostgreSQL shipping probe now confirm ORDER_COMPLETED |
| Approval returned a generated history ID as orderStatusId | Original OrderServices.java:2610 returns the business status, not the OrderStatus primary key | `292aca760f`: return the original status and needsInventoryIssuance output; H2/PostgreSQL normal, repeated and missing-input tests |

## Latest commercial-flow probe

The first expanded run executed 22 steps: **4 MATCH, 18 MISMATCH, 0 accepted**.
The raw evidence is in `results.json`. This is a diagnostic checkpoint, not a
completed delivery or an assertion that all requested flows are covered.

| Observation | Evidence / remaining work |
|---|---|
| Original storeOrder reserves inventory; migration does not | Added InventoryItemDetail and OrderItemShipGrpInvRes rows only on original; ATP decreases only on original. Port store-order reservation integration before interpreting downstream quantity differences. |
| Explicit reservation then adds to the original automatic reservation | The probe requests another quantity 2: original reservation becomes 4, migration 2. Correct the missing automatic integration, then design the explicit reserve step around the original service's additive semantics. Do not normalize the quantities. |
| Receipt accounting is missing | Original creates one posted SHIPMENT_RECEIPT transaction and two entries; migration creates none. ProductInventoryFlow explicitly labels receipt accounting callbacks as no-ops. |
| Receipt entity details differ | Accounting quantity total, inventory detail count, and receipt/item received-date handling differ. Check original receipt and inventory-detail callbacks before changing the migration. |
| Shipment header completion is fixed | The latest PostgreSQL probe satisfies both original and migration ORDER_COMPLETED/item-completed assertions. Invoice/ledger differences remain. |
| Shipment invoice and ledger side effects are absent | Original creates invoice, items, status history, order billing and ledger rows; migration creates none. These are unresolved production gaps, not API-envelope differences. |
| Response maps differ across components | Original often returns declared OUT values and localized messages; migration often returns full entity maps. A response-contract policy is still required. The comparator currently preserves all differences. |

## Incomplete coverage

The agreed response-contract policy now compares original declared OUT/INOUT
parameters (including implemented interfaces), success/error classification, and
shared error codes/categories. Migration extensions and localized message wording
are retained as information. With strict row comparison unchanged, the rerun has
**18 MATCH, 4 MISMATCH, 0 accepted**. All remaining mismatches are commercial-flow
row effects (plus the missing original storeOrder orderTypeId output).

An explicit invoice creation/READY/payment/application/PAID sequence has not yet
been added or executed. The shipping probe does exercise original invoice
creation as a side effect, but it does not replace those requested cases.
Final accepted-difference decisions, final documentation, and the final WP3
harness commit/push remain unfinished. No response difference has been accepted.

Current code verification: all-module Gradle run completed with `BUILD SUCCESSFUL
in 5m 27s`, `83 actionable tasks: 10 executed, 73 up-to-date`; JUnit totals are
3328 tests, zero failures/errors/skips. Comparator/reference-resolution tests:
16 tests, `OK`. Captured references are correctly resolved per runner; `$order`
in normalized row evidence denotes the intended generated-ID mapping, not a
literal identifier sent to either service.
