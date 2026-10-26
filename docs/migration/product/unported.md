# Product migration: unported behavior

The following callbacks are deliberately explicit no-ops until their owning flows are
ported. `updateShipment` returns the no-op names in `sideEffectNotes`.

| Trigger | OFBiz callback | Current behavior |
|---|---|---|
| `SHIPMENT_PACKED`, `SALES_SHIPMENT` | `createInvoicesFromShipment` | No-op note |
| `SHIPMENT_PACKED`, `SALES_SHIPMENT` | `setInvoicesToReadyFromShipment` | No-op note |
| `SHIPMENT_SHIPPED`, `SALES_SHIPMENT` | `sendShipmentCompleteNotification` | No-op note |
| `SHIPMENT_SHIPPED`, `PURCHASE_RETURN` | `createInvoicesFromReturnShipment` | No-op note |
| `PURCH_SHIP_SHIPPED`, `DROP_SHIPMENT` | `createInvoicesFromShipment` | No-op note |

`checkAndUpdateWorkEffort` is invoked through the shipment side-effect port and returns
a `501` note because work-effort integration is outside this flow's port.

Other shipment SECA callbacks (scheduled/received shipment notifications, receipt
invoicing, and facility-setting synchronization) are not yet implemented.

`quickShipEntireOrder` delegates issuance to `IssuanceServices.execute`. Until the
parallel `IssuanceServices` port is merged, issuance returns `501` and the shipment
transaction is rolled back. Order-service invocations remain routed through `OrderPort`;
non-CRUD order services return `501`.

`calculateProductPrice` ports only effective basic ProductPrice values. Product price
rules, promotions, VAT display adjustments, agreements, and currency conversion are not
ported by this flow.
