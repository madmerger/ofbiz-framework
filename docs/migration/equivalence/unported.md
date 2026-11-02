# Equivalence scope and unported branches

The PostgreSQL report is evidence for the ordered cases in `cases.json`, not a
claim that the complete original applications have been migrated. No business
value, missing row, or unsupported branch is accepted implicitly.

## Sales shipment invoice subset

Original reference: `applications/accounting/src/main/java/org/apache/ofbiz/accounting/invoice/InvoiceServices.java`,
`createInvoicesFromShipment`, `createInvoicesFromShipments`, `createInvoiceForOrder`,
and `setInvoicesToReadyFromShipment`.

The exercised subset supports sales shipments, issued quantities, unit prices,
duplicate-billing avoidance, invoice roles, order billing contacts, the store's
active payment address, invoice items, order-item and shipment-item billing,
invoice status audit rows, and posted sales-invoice transactions. Calls cross
the dispatcher/entity-auto boundary. The original shipment-billing service is
authenticated without a facility permission-service; the accounting co-host
registers that contract through the common entity-auto engine rather than the
product facade's blanket facility-create permission.

The original invoice implementation is substantially larger than 1,500 lines.
Excluded branches are purchase/return/drop shipments, taxes, order adjustments,
order and billing-account terms, specialized invoice-item derivations,
multi-order invoice consolidation policies, foreign-currency conversions, and
specialized accounting for those branches. They are not exercised or certified
by this report. Unsupported non-sales shipments return an explicit error.

## Payment subset

Original reference: `PaymentServices.groovy`, `createPaymentFromOrder`,
`createMatchingPaymentApplication`, and `removePaymentApplication`; and
`GeneralLedgerServices.xml`, incoming-payment accounting.

The flow exercises the automatically generated pending company-account payment,
its order-payment preference/history, automatic same-currency application,
release of that application, a separate received cash payment without an
explicit payment-method record or actual-currency fields, a positive manual
application, and invoice PAID. The release step is intentional: without it,
original OFBiz has already allocated the invoice to the pending order payment,
so an explicit new application is clamped to zero. Business amounts are never
normalized to hide this behavior.

Matching currently covers same-currency party/amount matching, payment ordering,
terminal invoice exclusions, and duplicate-application guards. Foreign-currency
matching and account-specific automatic-creation policy overrides are outside
this subset. Specialized billing-account, tax-authority, refund, and
payment-to-payment application accounting is not certified by these cases.

## Shipment defaults and inventory costing

Original references: `ShipmentServices.groovy` facility/order defaults and
`GeneralLedgerServices.xml` sales-shipment costing.

The cases compare destination/origin parties, active contact and telecom
defaults, direct shipping charges, FIFO/LIFO inventory cost layers and detail
links, issued inventory on the accounting header, cost inventory on entries,
and posting. Complex percentage/discount shipping adjustments and allocation
across multiple facilities are excluded. Average-cost computation remains
legacy migration behavior and is not certified by this run.

## Runtime boundaries

The original runner uses a disposable, loopback-only plugin and real dispatcher
transactions. The new runner uses signed-in REST calls on PostgreSQL with
`ddl-auto=none`. The co-host is equivalence-only; normal module defaults remain
H2. HR runs in a separate process against the same database because its entity
model cannot safely share the common scan. Standalone modules without a
registered cross-component target retain their documented fallback behavior.

Generated timestamps compare presence only, but values supplied in inputs are
preserved. Generated IDs, decimal scale, and row ordering are normalized. Status,
amount, role type, posted flag, and row multiplicity remain strict. Extra
migration response fields are informational; only original declared OUT/INOUT
parameters and success/error classification participate in response comparison.
