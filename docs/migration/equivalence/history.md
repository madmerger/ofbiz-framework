# Equivalence investigation history

The final PostgreSQL run establishes **28 MATCH / 0 MISMATCH / 0 accepted
differences** for the ordered manifest. This is case-level equivalence, not a
claim that all original OFBiz branches have been ported; see `unported.md`.

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

## Commercial-flow closure

The diagnostic checkpoint had **4 MATCH / 18 MISMATCH** across 22 cases. Applying
the agreed response contract yielded **18 MATCH / 4 MISMATCH**; expanded payment
coverage later yielded **23 MATCH / 4 MISMATCH**, then **26 MATCH / 2 MISMATCH**.
The final expanded manifest has **28 MATCH / 0 MISMATCH**. The extra release case
allows a positive cash application rather than hiding an original preallocation.

| Mismatch | Verified cause / original reference | Action |
|---|---|---|
| Response maps and localized messages differed | Original declared outputs differ from migration full-entity REST responses | `e42a8c6529`: apply the user-approved OUT/INOUT/interface contract; keep extensions as `responseInfo`; keep strict rows |
| Automatic order reservation was absent | Original `OrderServices.createOrder` calls reservation services | `893baf4175`: dispatch original reservation flow; explicitly unreserve before the manual reserve case to avoid double reservation |
| Generated order prefix differed | Original combines store and accounting-preference order prefixes | `893baf4175`: honor both prefixes instead of accepting an ID-format difference |
| Receipt accounting/detail/date rows differed | Receipt persistence bypassed the `createShipmentReceipt` service and its accounting SECA | `e60d6adb94`: dispatch receipt and inventory-detail services; preserve supplied received dates and original linkage |
| Shipping omitted invoice/billing/ledger rows | Direct shipment updates bypassed the invoice SECAs; invoice action targets were absent | Dispatch issuance and shipment status changes; port sales-shipment invoice/READY targets and invoice, role, contact, order-billing and shipment-billing services |
| Shipment cost layers differed | Original `GeneralLedgerServices.xml` selects FIFO/LIFO by owner/facility/product/accounting quantity/received date | Select and consume cost layers; preserve issued inventory on the header and cost inventory on entries; add a two-layer FIFO regression |
| Invoice GL failed a foreign key and lacked counterparty fields | An invoice-item type was written as a nonexistent GL account type; original sales-invoice header carries bill-to party/role | Remove the invalid entry account type and restore header party/role; preserve system posting actor |
| Invoice roles/contact rows differed | Original `createInvoiceRole` defaults `datetimePerformed`; `InvoiceServices.createInvoiceForOrder` copies the store payment address | Restore default timestamp and active payment address; compare all role/contact and shipment-billing rows; normalize only the generated timestamp |
| Incoming payment had zero amounts/error-journal state | Actual-currency amount was absent; a typeless debit skipped payment-method-type GL lookup | Preserve payment amount, allow missing actual-currency fields, resolve the payment-type account, guard an absent payment-method ID, and preserve original system audit actor |
| Explicit application had a different amount and PAID timing | Original `createMatchingPaymentApplication` preallocates the pending order payment at invoice readiness | Port matching/removal; distinguish all allocations from qualifying received/sent payments; release the pending allocation before applying the received cash payment |
| Order payment status history was missing | Original `OrderPaymentPreference` creation EECA invokes `changeOrderPaymentStatus` | Register the target and use the existing entity-auto preference service; H2/PostgreSQL scenario verifies one history row and idempotence |

The final evidence in `results.json` includes strict posted ledger rows for
receipt, sales shipment, sales invoice, incoming payment and application. The
cash application is positive (10) and transitions the invoice to PAID. The
explicit READY and PAID calls also verify repeat/idempotent behavior after their
original automatic transitions. Captured `$order`/`$invoice` values in normalized
evidence are ID bindings, not literal service inputs.

## Regression expectations and limitations

- The sales-invoice GL regression now expects a null credit `glAccountTypeId`,
  matching original `GeneralLedgerServices.xml`, rather than the invalid invoice
  item type. Amount and debit/credit assertions are retained.
- The existing shipment-cost fixture now provides the original required COGS
  preference. The new FIFO test asserts separate 1.25 and 2.25 credit layers and
  a balanced 3.50 debit; unsupported costing remains an error.
- New coverage includes matching/application release (normal, empty, repeated,
  missing-row), payment without actual-currency/payment-method fields, shipment
  invoice target errors/empty/duplicate paths, shipment defaults, and payment
  preference history on H2/PostgreSQL.
- Known Testcontainers/Hikari shutdown warnings remain. PostgreSQL equivalence
  uses real signed-in services and real storage; no UI surface is changed.
- No accepted business differences remain in the manifest. Excluded original
  branches are explicit in `unported.md`, not normalized away.

## Final verification

All twelve migration test tasks completed together:

```text
BUILD SUCCESSFUL in 19m 54s
71 actionable tasks: 10 executed, 61 up-to-date
```

| Module | Tests | Failures | Errors | Skipped |
|---|---:|---:|---:|---:|
| accounting-springboot | 642 | 0 | 0 | 0 |
| commonext-springboot | 39 | 0 | 0 | 0 |
| content-springboot | 208 | 0 | 0 | 0 |
| humanres-springboot | 162 | 0 | 0 | 0 |
| manufacturing-springboot | 50 | 0 | 0 | 0 |
| marketing-springboot | 170 | 0 | 0 | 0 |
| order-springboot | 69 | 0 | 0 | 0 |
| party-springboot | 157 | 0 | 0 | 0 |
| product-springboot | 1619 | 0 | 0 | 0 |
| springboot-common | 53 | 0 | 0 | 0 |
| springboot-datamodel | 1 | 0 | 0 | 0 |
| workeffort-springboot | 177 | 0 | 0 | 0 |
| **Total** | **3347** | **0** | **0** | **0** |

Harness/comparator unit tests: `Ran 20 tests in 0.003s`, `OK`.
The equivalence boot JAR compiled successfully before the final PostgreSQL run.
The final run exited zero and printed:

```text
{"MATCH": 28, "ACCEPTED_DIFFERENCE": 0, "MISMATCH": 0}
```
