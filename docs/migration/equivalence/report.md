# PostgreSQL old-vs-new equivalence

MATCH: 28/28; accepted differences: 0; unresolved: 0.

| Case | Flow | Service | Result |
|---|---|---|---|
| party-person | party | `createPerson` | MATCH |
| party-group | party | `createPartyGroup` | MATCH |
| party-name-history | party | `updatePerson` | MATCH |
| party-customer-role | party | `createPartyRole` | MATCH |
| party-contact | party | `createPartyEmailAddress` | MATCH |
| content-electronic-text | content | `createElectronicText` | MATCH |
| marketing-campaign | marketing | `createMarketingCampaign` | MATCH |
| manufacturing-calendar | manufacturing | `createCalendar` | MATCH |
| humanres-position-type | humanres | `createEmplPositionType` | MATCH |
| workeffort-create | workeffort | `createWorkEffort` | MATCH |
| workeffort-update | workeffort | `updateWorkEffort` | MATCH |
| workeffort-shipment-fixture | workeffort | `createShipment` | MATCH |
| workeffort-shipment-sync | workeffort | `checkAndUpdateWorkEffort` | MATCH |
| negative-invalid-input | negative | `createPartyRole` | MATCH |
| negative-permission | negative | `createCalendar` | MATCH |
| negative-duplicate-fixture | negative | `createPerson` | MATCH |
| negative-duplicate | negative | `createPerson` | MATCH |
| sales-order-create | order-product-accounting | `storeOrder` | MATCH |
| sales-order-approve | order-product-accounting | `changeOrderStatus` | MATCH |
| inventory-receive | order-product-accounting | `receiveInventoryProduct` | MATCH |
| inventory-unreserve | order-product-accounting | `cancelOrderInventoryReservation` | MATCH |
| inventory-reserve | order-product-accounting | `reserveProductInventoryByFacility` | MATCH |
| order-ship | order-product-accounting | `quickShipEntireOrder` | MATCH |
| invoice-ready | order-product-accounting | `setInvoiceStatus` | MATCH |
| invoice-release-preapplication | order-product-accounting | `removePaymentApplication` | MATCH |
| payment-create | order-product-accounting | `createPayment` | MATCH |
| payment-apply | order-product-accounting | `createPaymentApplication` | MATCH |
| invoice-paid | order-product-accounting | `setInvoiceStatus` | MATCH |

## Accepted differences

None. No business-value or row-count difference is accepted for these cases.

## Mismatch → cause → action history

See [history.md](history.md) for investigations and fixes. Raw responses, row changes,
assertion failures and exact differences are retained in [results.json](results.json).

## Rerun

```sh
bash migration/equivalence/run.sh
```

See [the harness README](../../../migration/equivalence/README.md) for prerequisites,
schema/transaction boundaries and the precise normalization policy.
See [unported.md](unported.md) for excluded original branches. Matching these cases
does not certify the complete original applications.
