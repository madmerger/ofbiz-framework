# Accounting migration review notes

- `Payment` and `Invoice` records are business documents, not credential stores; they remain nonsensitive. Payment-method and gateway configuration entities are sensitive.
- `CreditCardTypeGlAccount` contains accounting configuration but no payment credential and is intentionally not treated as sensitive.
- `getInvoiceTotal`, `getInvoiceItemTotal`, `getInvoiceNotApplied`, and `getInvoiceApplied` are worker-style calculations exposed as migration handlers; OFBiz does not define these as services.
- The common multi-service layer currently discards individual HTTP statuses. The Accounting advice restores HTTP 409 only when an error contains `ServiceValueFound`; preserving status codes in the common multi-service exception is a follow-up improvement candidate.
