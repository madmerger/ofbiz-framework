# PostgreSQL old-vs-new equivalence

MATCH: 4/22; accepted differences: 0; unresolved: 18.

| Case | Flow | Service | Result |
|---|---|---|---|
| party-person | party | `createPerson` | MATCH |
| party-group | party | `createPartyGroup` | MATCH |
| party-name-history | party | `updatePerson` | MISMATCH |
| party-customer-role | party | `createPartyRole` | MISMATCH |
| party-contact | party | `createPartyEmailAddress` | MISMATCH |
| content-electronic-text | content | `createElectronicText` | MISMATCH |
| marketing-campaign | marketing | `createMarketingCampaign` | MISMATCH |
| manufacturing-calendar | manufacturing | `createCalendar` | MISMATCH |
| humanres-position-type | humanres | `createEmplPositionType` | MISMATCH |
| workeffort-create | workeffort | `createWorkEffort` | MISMATCH |
| workeffort-update | workeffort | `updateWorkEffort` | MISMATCH |
| workeffort-shipment-fixture | workeffort | `createShipment` | MISMATCH |
| workeffort-shipment-sync | workeffort | `checkAndUpdateWorkEffort` | MATCH |
| negative-invalid-input | negative | `createPartyRole` | MISMATCH |
| negative-permission | negative | `createCalendar` | MISMATCH |
| negative-duplicate-fixture | negative | `createPerson` | MATCH |
| negative-duplicate | negative | `createPerson` | MISMATCH |
| sales-order-create | order-product-accounting | `storeOrder` | MISMATCH |
| sales-order-approve | order-product-accounting | `changeOrderStatus` | MISMATCH |
| inventory-receive | order-product-accounting | `receiveInventoryProduct` | MISMATCH |
| inventory-reserve | order-product-accounting | `reserveProductInventoryByFacility` | MISMATCH |
| order-ship | order-product-accounting | `quickShipEntireOrder` | MISMATCH |

## Accepted differences


## Mismatch → cause → action history

See [history.md](history.md) for investigations and fixes. Raw responses, row changes,
assertion failures and exact differences are retained in [results.json](results.json).

## Rerun

```sh
bash migration/equivalence/run.sh
```

See [the harness README](../../../migration/equivalence/README.md) for prerequisites,
schema/transaction boundaries and the precise normalization policy.
