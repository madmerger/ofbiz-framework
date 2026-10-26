# Accounting migration scope notes

The current service set covers the entity-auto catalog and the invoice/payment handlers named in `DESIGN.md`. The simple/minilang service set remains outside this handoff.

The following OFBiz branches are intentionally not ported:

- `createInvoice`: enforcing `PartyAcctgPreference.invoiceIdPrefix` and per-party invoice sequence settings.
- `setInvoiceStatus`: automatic matching/capture of payment applications when approving, including gateway capture.
- `createInvoiceForOrder`: adjustments, billing accounts, contact-mech selection, shipment grouping, payment-preference auto-application, and fees.
- Payment and invoice services: GL transaction creation, posting, and reversal.
- Order invoicing: purchase-order and return-order paths; only `SALES_ORDER` is supported.

Re-billing an order with no unbilled quantity creates no additional invoice.
