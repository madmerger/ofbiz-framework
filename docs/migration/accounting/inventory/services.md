# Service inventory

## acctgAgreementPermissionCheck

- Definition: `applications/accounting/servicedef/services_perm.xml:26`
- Engine: simple; location: component://accounting/minilang/permissions/PermissionServices.xml; invoke: acctgAgreementPermissionCheck
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## acctgBasePermissionCheck

- Definition: `applications/accounting/servicedef/services_perm.xml:32`
- Engine: simple; location: component://accounting/minilang/permissions/PermissionServices.xml; invoke: basePermissionCheck
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## acctgBillingAcctCheck

- Definition: `applications/accounting/servicedef/services_perm.xml:38`
- Engine: simple; location: component://accounting/minilang/permissions/PermissionServices.xml; invoke: acctgBillingAcctCheck
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## acctgCommissionPermissionCheck

- Definition: `applications/accounting/servicedef/services_perm.xml:44`
- Engine: simple; location: component://accounting/minilang/permissions/PermissionServices.xml; invoke: commissionPermissionCheck
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## acctgCostPermissionCheck

- Definition: `applications/accounting/servicedef/services_perm.xml:50`
- Engine: simple; location: component://accounting/minilang/permissions/PermissionServices.xml; invoke: acctgCostPermissionCheck
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## acctgFinAcctPermissionCheck

- Definition: `applications/accounting/servicedef/services_perm.xml:56`
- Engine: simple; location: component://accounting/minilang/permissions/PermissionServices.xml; invoke: acctgFinAcctPermissionCheck
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## acctgFxPermissionCheck

- Definition: `applications/accounting/servicedef/services_perm.xml:62`
- Engine: simple; location: component://accounting/minilang/permissions/PermissionServices.xml; invoke: acctgFxPermissionCheck
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## acctgInvoicePermissionCheck

- Definition: `applications/accounting/servicedef/services_perm.xml:68`
- Engine: simple; location: component://accounting/minilang/permissions/PermissionServices.xml; invoke: acctgInvoicePermissionCheck
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## acctgPaymentPermissionCheck

- Definition: `applications/accounting/servicedef/services_perm.xml:97`
- Engine: simple; location: component://accounting/minilang/permissions/PermissionServices.xml; invoke: acctgPaymentPermissionCheck
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## acctgPrefPermissionCheck

- Definition: `applications/accounting/servicedef/services_perm.xml:74`
- Engine: simple; location: component://accounting/minilang/permissions/PermissionServices.xml; invoke: preferencePermissionCheck
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## acctgTransactionPermissionCheck

- Definition: `applications/accounting/servicedef/services_perm.xml:80`
- Engine: simple; location: component://accounting/minilang/permissions/PermissionServices.xml; invoke: acctgTransactionPermissionCheck
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## addFixedAssetProduct

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:44`
- Engine: entity-auto; location: —; invoke: create
- Default entity: FixedAssetProduct; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| fixedAssetId | id | IN | false |
| fixedAssetProductTypeId | id | IN | false |
| fromDate | date-time | IN | true |
| productId | id | IN | false |
| quantity | fixed-point | IN | true |
| quantityUomId | id | IN | true |
| sequenceNum | numeric | IN | true |
| thruDate | date-time | IN | true |

## addFundsToGiftCertificate

- Definition: `applications/accounting/servicedef/services_finaccount.xml:360`
- Engine: java; location: org.apache.ofbiz.accounting.payment.GiftCertificateServices; invoke: addFundsToGiftCertificate
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | BigDecimal | INOUT | false |
| balance | BigDecimal | OUT | false |
| cardNumber | String | IN | false |
| currency | String | IN | true |
| partyId | String | IN | true |
| pinNumber | String | IN | true |
| previousBalance | BigDecimal | OUT | true |
| processResult | Boolean | OUT | false |
| productStoreId | String | IN | false |
| referenceNum | String | OUT | false |
| responseCode | String | OUT | false |

## addInvoiceItemTypeGlAssignment

- Definition: `applications/accounting/servicedef/services_admin.xml:111`
- Engine: entity-auto; location: —; invoke: create
- Default entity: InvoiceItemTypeGlAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgPrefPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glAccountId | id | IN | false |
| invoiceItemTypeId | id | IN | false |
| organizationPartyId | id | IN | false |

## addInvoiceNote

- Definition: `applications/accounting/servicedef/services_invoice.xml:541`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy; invoke: addInvoiceNote
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | String | IN | — |
| noteId | String | OUT | — |
| noteInfo | String | IN | — |
| noteName | String | IN | true |

## addPaymentMethodTypeGlAssignment

- Definition: `applications/accounting/servicedef/services_admin.xml:133`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PaymentMethodTypeGlAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgPrefPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glAccountId | id | IN | false |
| organizationPartyId | id | IN | false |
| paymentMethodTypeId | id | IN | false |

## addPaymentTypeGlAssignment

- Definition: `applications/accounting/servicedef/services_admin.xml:122`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PaymentGlAccountTypeMap; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgPrefPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glAccountTypeId | id | IN | false |
| organizationPartyId | id | IN | false |
| paymentTypeId | id | IN | false |

## addTaxOnInvoice

- Definition: `applications/accounting/servicedef/services_invoice.xml:346`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy; invoke: addTaxOnInvoice
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | String | IN | — |

## alwaysApproveCCProcessor

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:685`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: alwaysApproveProcessor
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "ccAuthInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## alwaysApproveEFTProcessor

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:780`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: alwaysApproveProcessor
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "eftProcessInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## alwaysApproveGCProcessor

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:758`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: alwaysApproveWithCapture
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "giftCardProcessInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## alwaysApproveWithCaptureCCProcessor

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:690`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: alwaysApproveWithCapture
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "ccAuthInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## alwaysBadCardNumberCCProcessor

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:715`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: alwaysBadCardNumberProcessor
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "ccAuthInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## alwaysBadExpireCCProcessor

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:705`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: alwaysBadExpireProcessor
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "ccAuthInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## alwaysDeclineCCProcessor

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:695`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: alwaysDeclineProcessor
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "ccAuthInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## alwaysDeclineEFTProcessor

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:785`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: alwaysDeclineProcessor
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "eftProcessInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## alwaysDeclineGCProcessor

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:763`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: alwaysDeclineProcessor
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "giftCardProcessInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## alwaysFailCCProcessor

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:720`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: alwaysFailProcessor
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "ccAuthInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## alwaysNsfCCProcessor

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:700`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: alwaysNsfProcessor
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "ccAuthInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## assignGlRecToFinAccTrans

- Definition: `applications/accounting/servicedef/services_finaccount.xml:493`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountServices.xml; invoke: assignGlRecToFinAccTrans
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgFinAcctPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountTransId | String | IN | false |
| glReconciliationId | String | IN | false |

## authOrderPaymentPreference

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:295`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: authOrderPaymentPreference
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| authCode | String | OUT | true |
| errors | Boolean | OUT | false |
| finished | Boolean | OUT | false |
| messages | List | OUT | true |
| orderPaymentPreferenceId | String | IN | false |
| overrideAmount | BigDecimal | IN | true |
| processAmount | BigDecimal | OUT | true |

## authOrderPayments

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:307`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: authOrderPayments
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| authCode | String | OUT | true |
| authResultMsgs | List | OUT | true |
| orderId | String | IN | false |
| processResult | String | OUT | false |
| reAuth | Boolean | IN | true |

## autoAssignFixedAssetPartiesToMaintenance

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:155`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/fixedasset/FixedAssetServices.groovy; invoke: autoAssignFixedAssetPartiesToMaintenance
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetId | String | IN | — |
| workEffortId | String | IN | — |

## autoFinAccountReconciliation

- Definition: `applications/accounting/servicedef/services_finaccount.xml:529`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountServices.xml; invoke: autoFinAccountReconciliation
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## autoGenerateInvoiceFromExistingInvoice

- Definition: `applications/accounting/servicedef/services_invoice.xml:325`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy; invoke: autoGenerateInvoiceFromExistingInvoice
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| recurrenceInfoId | String | IN | — |

## badExpireEvenCCProcessor

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:710`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: badExpireEvenProcessor
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "ccAuthInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## basicGeneralLedgerPermissionCheck

- Definition: `applications/accounting/servicedef/services_perm.xml:86`
- Engine: simple; location: component://accounting/minilang/permissions/PermissionServices.xml; invoke: basePermissionCheck
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## buildCcExpireDate

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:87`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentMethodServices; invoke: makeExpireDate
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| expMonth | String | IN | false |
| expYear | String | IN | false |
| expireDate | String | OUT | — |

## calcBillingAccountBalance

- Definition: `applications/accounting/servicedef/services_billing.xml:86`
- Engine: java; location: org.apache.ofbiz.accounting.payment.BillingAccountWorker; invoke: calcBillingAccountBalance
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accountBalance | BigDecimal | OUT | false |
| availableBalance | BigDecimal | OUT | false |
| availableToCapture | BigDecimal | OUT | false |
| billingAccount | org.apache.ofbiz.entity.GenericValue | OUT | false |
| billingAccountId | String | IN | false |
| netAccountBalance | BigDecimal | OUT | false |

## calcTax

- Definition: `applications/accounting/servicedef/services_tax.xml:59`
- Engine: java; location: org.apache.ofbiz.accounting.tax.TaxAuthorityServices; invoke: rateProductTaxCalc
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "calcTaxInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## calcTaxForDisplay

- Definition: `applications/accounting/servicedef/services_tax.xml:64`
- Engine: java; location: org.apache.ofbiz.accounting.tax.TaxAuthorityServices; invoke: rateProductTaxCalcForDisplay
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "calcTaxTotalForDisplayInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## calcTaxInterface

- Definition: `applications/accounting/servicedef/services_tax.xml:28`
- Engine: interface; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| billToPartyId | String | IN | true |
| facilityId | String | IN | true |
| itemAdjustments | java.util.List | OUT | false |
| itemAmountList | java.util.List | IN | false |
| itemPriceList | java.util.List | IN | false |
| itemProductList | java.util.List | IN | false |
| itemQuantityList | java.util.List | IN | true |
| itemShippingList | java.util.List | IN | true |
| orderAdjustments | java.util.List | OUT | false |
| orderPromotionsAmount | BigDecimal | IN | true |
| orderShippingAmount | BigDecimal | IN | true |
| payToPartyId | String | IN | true |
| productStoreId | String | IN | true |
| shippingAddress | org.apache.ofbiz.entity.GenericValue | IN | true |

## calcTaxTotalForDisplayInterface

- Definition: `applications/accounting/servicedef/services_tax.xml:45`
- Engine: interface; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| basePrice | BigDecimal | IN | false |
| billToPartyId | String | IN | true |
| priceWithTax | BigDecimal | OUT | false |
| productId | String | IN | false |
| productStoreId | String | IN | false |
| quantity | BigDecimal | IN | true |
| shippingPrice | BigDecimal | IN | true |
| taxPercentage | BigDecimal | OUT | false |
| taxTotal | BigDecimal | OUT | false |

## calculateAcctgTransTrialBalance

- Definition: `applications/accounting/servicedef/services_ledger.xml:269`
- Engine: simple; location: component://accounting/minilang/ledger/AcctgTransServices.xml; invoke: calculateAcctgTransTrialBalance
- Default entity: AcctgTrans; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgTransactionPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | id | IN | false |
| creditTotal | BigDecimal | OUT | false |
| debitCreditDifference | BigDecimal | OUT | false |
| debitTotal | BigDecimal | OUT | false |

## calculateFixedAssetDepreciation

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:270`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/fixedasset/FixedAssetServices.groovy; invoke: calculateFixedAssetDepreciation
- Default entity: FixedAssetDepMethod; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| assetDepreciationInfoList | List | OUT | true |
| assetDepreciationTillDate | List | OUT | true |
| assetNBVAfterDepreciation | List | OUT | true |
| fixedAssetId | String | IN | — |
| nextDepreciationAmount | BigDecimal | OUT | true |
| plannedPastDepreciationTotal | BigDecimal | OUT | true |

## calculateGlAccountTrialBalance

- Definition: `applications/accounting/servicedef/services_ledger.xml:580`
- Engine: simple; location: component://accounting/minilang/ledger/AcctgTransServices.xml; invoke: calculateGlAccountTrialBalance
- Default entity: GlAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| debitCreditDifference | BigDecimal | OUT | — |
| fromDate | Timestamp | IN | — |
| glAccountId | String | IN | — |
| isPosted | String | IN | true |
| openingBalanceCredit | BigDecimal | OUT | — |
| openingBalanceDebit | BigDecimal | OUT | — |
| thruDate | Timestamp | IN | — |

## calculateGlJournalTrialBalance

- Definition: `applications/accounting/servicedef/services_ledger.xml:85`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: calculateGlJournalTrialBalance
- Default entity: GlJournal; auth: true
- Use transaction: —; timeout: —
- Permission service: basicGeneralLedgerPermissionCheck; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| creditTotal | BigDecimal | OUT | false |
| debitCreditDifference | BigDecimal | OUT | false |
| debitTotal | BigDecimal | OUT | false |
| glJournalId | id | IN | false |

## calculateInvoicedAdjustmentTotal

- Definition: `applications/accounting/servicedef/services_invoice.xml:312`
- Engine: java; location: org.apache.ofbiz.accounting.invoice.InvoiceServices; invoke: calculateInvoicedAdjustmentTotal
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoicedTotal | BigDecimal | OUT | — |
| orderAdjustment | org.apache.ofbiz.entity.GenericValue | IN | — |

## cancelBankReconciliation

- Definition: `applications/accounting/servicedef/services_finaccount.xml:515`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountServices.xml; invoke: cancelBankReconciliation
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgFinAcctPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glReconciliationId | String | IN | false |

## cancelCheckRunPayments

- Definition: `applications/accounting/servicedef/services_payment.xml:198`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy; invoke: cancelCheckRunPayments
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| paymentGroupId | String | IN | false |

## cancelFixedAssetStdCost

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:78`
- Engine: entity-auto; location: —; invoke: expire
- Default entity: FixedAssetStdCost; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | currency-amount | IN | true |
| amountUomId | id | IN | true |
| fixedAssetId | id | IN | false |
| fixedAssetStdCostTypeId | id | IN | false |
| fromDate | date-time | IN | false |
| thruDate | date-time | IN | true |

## cancelInvoice

- Definition: `applications/accounting/servicedef/services_invoice.xml:331`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy; invoke: cancelInvoice
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | String | IN | — |
| invoiceTypeId | String | OUT | — |

## cancelPaymentBatch

- Definition: `applications/accounting/servicedef/services_payment.xml:161`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy; invoke: cancelPaymentBatch
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| paymentGroupId | String | IN | false |

## captureBillingAccountPayments

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:354`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: captureBillingAccountPayments
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| billingAccountId | String | IN | false |
| captureAmount | BigDecimal | INOUT | false |
| invoiceId | String | IN | false |
| orderId | String | IN | true |

## captureOrderPayments

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:344`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: captureOrderPayments
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| billingAccountId | String | IN | true |
| captureAmount | BigDecimal | IN | false |
| invoiceId | String | IN | true |
| orderId | String | IN | false |
| processResult | String | OUT | false |

## capturePaymentsByInvoice

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:337`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: capturePaymentsByInvoice
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | String | IN | false |
| processResult | String | OUT | false |

## ccAuthInterface

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:588`
- Engine: interface; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "paymentProcessInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| cardSecurityCode | String | IN | true |
| creditCard | org.apache.ofbiz.entity.GenericValue | IN | false |

## ccCaptureInterface

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:595`
- Engine: interface; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| authTrans | org.apache.ofbiz.entity.GenericValue | IN | true |
| captureAltRefNum | String | OUT | true |
| captureAmount | BigDecimal | INOUT | false |
| captureCode | String | OUT | true |
| captureFlag | String | OUT | true |
| captureMessage | String | OUT | true |
| captureRefNum | String | OUT | false |
| captureResult | Boolean | OUT | true |
| currency | String | IN | true |
| internalRespMsgs | List | OUT | true |
| orderPaymentPreference | org.apache.ofbiz.entity.GenericValue | IN | false |
| paymentConfig | String | IN | true |
| paymentGatewayConfigId | String | IN | true |

## ccCreditInterface

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:612`
- Engine: interface; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "paymentCreditInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| cardSecurityCode | String | IN | true |
| creditCard | org.apache.ofbiz.entity.GenericValue | IN | false |

## checkAcctgTransPosted

- Definition: `applications/accounting/servicedef/services_ledger.xml:215`
- Engine: simple; location: component://accounting/minilang/ledger/AcctgTransServices.xml; invoke: checkAcctgTransPosted
- Default entity: AcctgTrans; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | id | IN | false |

## checkAndCreateBatchForValidPayments

- Definition: `applications/accounting/servicedef/services_payment.xml:223`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy; invoke: checkAndCreateBatchForValidPayments
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "createPaymentGroupAndMember"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## checkFinAccountBalance

- Definition: `applications/accounting/servicedef/services_finaccount.xml:201`
- Engine: java; location: org.apache.ofbiz.accounting.finaccount.FinAccountServices; invoke: checkFinAccountBalance
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| availableBalance | BigDecimal | OUT | false |
| balance | BigDecimal | OUT | false |
| finAccountId | String | IN | false |
| statusId | String | OUT | false |

## checkFinAccountStatus

- Definition: `applications/accounting/servicedef/services_finaccount.xml:209`
- Engine: java; location: org.apache.ofbiz.accounting.finaccount.FinAccountServices; invoke: checkFinAccountStatus
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountAuthId | String | IN | true |
| finAccountId | String | IN | true |

## checkGiftCertificateBalance

- Definition: `applications/accounting/servicedef/services_finaccount.xml:391`
- Engine: java; location: org.apache.ofbiz.accounting.payment.GiftCertificateServices; invoke: checkGiftCertificateBalance
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| balance | BigDecimal | OUT | false |
| cardNumber | String | IN | false |
| currency | String | IN | true |
| pinNumber | String | IN | false |

## checkInvoicePaymentApplications

- Definition: `applications/accounting/servicedef/services_invoice.xml:297`
- Engine: java; location: org.apache.ofbiz.accounting.invoice.InvoiceServices; invoke: checkInvoicePaymentApplications
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | String | IN | — |

## checkInvoiceStatusInProgress

- Definition: `applications/accounting/servicedef/services_invoice.xml:106`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy; invoke: checkInvoiceStatusInProgress
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: acctgInvoicePermissionCheck; main action: UPDATE
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | String | IN | — |

## checkPaymentInvoices

- Definition: `applications/accounting/servicedef/services_payment.xml:56`
- Engine: java; location: org.apache.ofbiz.accounting.invoice.InvoiceServices; invoke: checkPaymentInvoices
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| paymentId | String | IN | false |

## checkUpdateFixedAssetDepreciation

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:239`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/fixedasset/FixedAssetServices.groovy; invoke: checkUpdateFixedAssetDepreciation
- Default entity: AcctgTrans; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | id | IN | false |

## clearCreditCardDataAndExpire

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:82`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentMethodServices; invoke: clearCreditCardData
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| paymentMethodId | String | IN | false |

## closeFinancialTimePeriod

- Definition: `applications/accounting/servicedef/services_ledger.xml:289`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: closeFinancialTimePeriod
- Default entity: CustomTimePeriod; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| customTimePeriodId | id | IN | false |

## completeAcctgTransEntries

- Definition: `applications/accounting/servicedef/services_ledger.xml:178`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: completeAcctgTransEntries
- Default entity: AcctgTrans; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgTransactionPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | id | IN | false |

## computeAndStoreGlAccountHistoryBalance

- Definition: `applications/accounting/servicedef/services_ledger.xml:306`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: computeAndStoreGlAccountHistoryBalance
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## computeGlAccountBalanceForTimePeriod

- Definition: `applications/accounting/servicedef/services_ledger.xml:295`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: computeGlAccountBalanceForTimePeriod
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| customTimePeriodId | String | IN | false |
| endingBalance | BigDecimal | OUT | false |
| glAccountId | String | IN | false |
| openingBalance | BigDecimal | OUT | false |
| organizationPartyId | String | IN | false |
| postedCredits | BigDecimal | OUT | false |
| postedDebits | BigDecimal | OUT | false |

## copyAcctgTransAndEntries

- Definition: `applications/accounting/servicedef/services_ledger.xml:498`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: copyAcctgTransAndEntries
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | String | OUT | true |
| fromAcctgTransId | String | IN | false |
| revert | String | IN | true |

## copyAgreement

- Definition: `applications/accounting/servicedef/services_agreement.xml:46`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/agreement/AgreementServicesScript.groovy; invoke: copyAgreement
- Default entity: Agreement; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | INOUT | false |
| copyAgreementFacilities | String | IN | true |
| copyAgreementParties | String | IN | true |
| copyAgreementProducts | String | IN | true |
| copyAgreementTerms | String | IN | true |

## copyInvoice

- Definition: `applications/accounting/servicedef/services_invoice.xml:64`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy; invoke: copyInvoice
- Default entity: Invoice; auth: —
- Use transaction: —; timeout: —
- Permission service: acctgInvoicePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | id | INOUT | true |
| invoiceIdToCopyFrom | String | IN | — |

## copyInvoiceToTemplate

- Definition: `applications/accounting/servicedef/services_invoice.xml:99`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy; invoke: copyInvoiceToTemplate
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: acctgInvoicePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | String | INOUT | — |
| invoiceTypeId | String | IN | — |

## createAccommodationClass

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:321`
- Engine: entity-auto; location: —; invoke: create
- Default entity: AccommodationClass; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accommodationClassId | id | INOUT | true |
| description | description | IN | true |
| parentClassId | id | IN | true |

## createAccommodationMap

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:351`
- Engine: entity-auto; location: —; invoke: create
- Default entity: AccommodationMap; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accommodationClassId | id | IN | true |
| accommodationMapId | id | INOUT | true |
| accommodationMapTypeId | id | IN | true |
| fixedAssetId | id | IN | true |
| numberOfSpaces | numeric | IN | true |

## createAccommodationMapType

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:335`
- Engine: entity-auto; location: —; invoke: create
- Default entity: AccommodationMapType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accommodationMapTypeId | id | INOUT | true |
| description | description | IN | true |

## createAcctgTrans

- Definition: `applications/accounting/servicedef/services_ledger.xml:196`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/ledger/GeneralLedgerServicesScript.groovy; invoke: createAcctgTrans
- Default entity: AcctgTrans; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgTransactionPermissionCheck; main action: CREATE
- Implements: [{"service": "interfaceAcctgTrans"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | id | OUT | false |

## createAcctgTransAndEntries

- Definition: `applications/accounting/servicedef/services_ledger.xml:255`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: createAcctgTransAndEntries
- Default entity: AcctgTrans; auth: —
- Use transaction: —; timeout: —
- Permission service: acctgTransactionPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransEntries | java.util.List | IN | false |
| acctgTransId | String | OUT | true |
| acctgTransTypeId | id | IN | true |
| createdByUserLogin | id-vlong | IN | true |
| createdDate | date-time | IN | true |
| description | description | IN | true |
| finAccountTransId | id | IN | true |
| fixedAssetId | id | IN | true |
| glFiscalTypeId | id | IN | true |
| glJournalId | id | IN | true |
| groupStatusId | id | IN | true |
| inventoryItemId | id | IN | true |
| invoiceId | id | IN | true |
| isPosted | indicator | IN | true |
| lastModifiedByUserLogin | id-vlong | IN | true |
| lastModifiedDate | date-time | IN | true |
| partyId | id | IN | true |
| paymentId | id | IN | true |
| physicalInventoryId | id | IN | true |
| postedDate | date-time | IN | true |
| receiptId | id | IN | true |
| roleTypeId | id | IN | true |
| scheduledPostingDate | date-time | IN | true |
| shipmentId | id | IN | true |
| theirAcctgTransId | id-long | IN | true |
| transactionDate | date-time | IN | true |
| voucherDate | date-time | IN | true |
| voucherRef | short-varchar | IN | true |
| workEffortId | id | IN | true |

## createAcctgTransAndEntriesForCustomerRefundPaymentApplication

- Definition: `applications/accounting/servicedef/services_ledger.xml:513`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: createAcctgTransAndEntriesForCustomerRefundPaymentApplication
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | String | OUT | true |
| paymentApplicationId | String | IN | false |

## createAcctgTransAndEntriesForIncomingPayment

- Definition: `applications/accounting/servicedef/services_ledger.xml:451`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: createAcctgTransAndEntriesForIncomingPayment
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | String | OUT | true |
| paymentId | String | IN | false |

## createAcctgTransAndEntriesForOutgoingPayment

- Definition: `applications/accounting/servicedef/services_ledger.xml:490`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: createAcctgTransAndEntriesForOutgoingPayment
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | String | OUT | true |
| paymentId | String | IN | false |

## createAcctgTransAndEntriesForPaymentApplication

- Definition: `applications/accounting/servicedef/services_ledger.xml:506`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: createAcctgTransAndEntriesForPaymentApplication
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | String | OUT | true |
| paymentApplicationId | String | IN | false |

## createAcctgTransAttribute

- Definition: `applications/accounting/servicedef/services_ledger.xml:675`
- Engine: entity-auto; location: —; invoke: create
- Default entity: AcctgTransAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | id | IN | false |
| attrDescription | description | IN | true |
| attrName | id-long | IN | false |
| attrValue | value | IN | true |

## createAcctgTransEntry

- Definition: `applications/accounting/servicedef/services_ledger.xml:231`
- Engine: simple; location: component://accounting/minilang/ledger/AcctgTransServices.xml; invoke: createAcctgTransEntry
- Default entity: AcctgTransEntry; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgTransactionPermissionCheck; main action: CREATE
- Implements: [{"service": "interfaceAcctgTransEntry"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransEntrySeqId | id | OUT | false |
| acctgTransId | id | IN | false |
| purposeEnumId | String | IN | true |

## createAcctgTransEntryType

- Definition: `applications/accounting/servicedef/services_ledger.xml:706`
- Engine: entity-auto; location: —; invoke: create
- Default entity: AcctgTransEntryType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransEntryTypeId | id | INOUT | true |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## createAcctgTransForCanceledSalesShipmentIssuance

- Definition: `applications/accounting/servicedef/services_ledger.xml:356`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: createAcctgTransForCanceledSalesShipmentIssuance
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | String | OUT | true |
| canceledQuantity | BigDecimal | IN | false |
| itemIssuanceId | String | IN | false |

## createAcctgTransForCustomerReturnInvoice

- Definition: `applications/accounting/servicedef/services_ledger.xml:466`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: createAcctgTransForCustomerReturnInvoice
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | String | OUT | true |
| invoiceId | String | IN | false |

## createAcctgTransForFixedAssetMaintIssuance

- Definition: `applications/accounting/servicedef/services_ledger.xml:458`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: createAcctgTransForFixedAssetMaintIssuance
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | String | OUT | true |
| itemIssuanceId | String | IN | false |

## createAcctgTransForInventoryItemCostChange

- Definition: `applications/accounting/servicedef/services_ledger.xml:363`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: createAcctgTransForInventoryItemCostChange
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | String | OUT | true |
| inventoryItemDetailSeqId | String | IN | false |
| inventoryItemId | String | IN | false |

## createAcctgTransForInventoryItemOwnerChange

- Definition: `applications/accounting/servicedef/services_ledger.xml:443`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: createAcctgTransForInventoryItemOwnerChange
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | String | OUT | true |
| inventoryItemId | String | IN | false |
| oldOwnerPartyId | String | IN | false |

## createAcctgTransForPhysicalInventoryVariance

- Definition: `applications/accounting/servicedef/services_ledger.xml:418`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: createAcctgTransForPhysicalInventoryVariance
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | String | OUT | true |
| physicalInventoryId | String | IN | false |

## createAcctgTransForPurchaseInvoice

- Definition: `applications/accounting/servicedef/services_ledger.xml:474`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: createAcctgTransForPurchaseInvoice
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | String | OUT | true |
| invoiceId | String | IN | false |

## createAcctgTransForSalesInvoice

- Definition: `applications/accounting/servicedef/services_ledger.xml:482`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: createAcctgTransForSalesInvoice
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | String | OUT | true |
| invoiceId | String | IN | false |

## createAcctgTransForSalesShipmentIssuance

- Definition: `applications/accounting/servicedef/services_ledger.xml:350`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: createAcctgTransForSalesShipmentIssuance
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | String | OUT | true |
| itemIssuanceId | String | IN | false |

## createAcctgTransForShipmentReceipt

- Definition: `applications/accounting/servicedef/services_ledger.xml:370`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: createAcctgTransForShipmentReceipt
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | String | OUT | true |
| receiptId | String | IN | false |

## createAcctgTransForWorkEffortCost

- Definition: `applications/accounting/servicedef/services_ledger.xml:434`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: createAcctgTransForWorkEffortCost
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | String | OUT | true |
| costComponentId | String | IN | false |
| workEffortId | String | IN | false |

## createAcctgTransForWorkEffortInventoryProduced

- Definition: `applications/accounting/servicedef/services_ledger.xml:425`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: createAcctgTransForWorkEffortInventoryProduced
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | String | OUT | true |
| inventoryItemId | String | IN | false |
| workEffortId | String | IN | false |

## createAcctgTransForWorkEffortIssuance

- Definition: `applications/accounting/servicedef/services_ledger.xml:409`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: createAcctgTransForWorkEffortIssuance
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | String | OUT | true |
| inventoryItemId | String | IN | false |
| quantity | BigDecimal | IN | true |
| workEffortId | String | IN | false |

## createAcctgTransType

- Definition: `applications/accounting/servicedef/services_ledger.xml:721`
- Engine: entity-auto; location: —; invoke: create
- Default entity: AcctgTransType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransTypeId | id | INOUT | true |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## createAcctgTransTypeAttr

- Definition: `applications/accounting/servicedef/services_ledger.xml:691`
- Engine: entity-auto; location: —; invoke: create
- Default entity: AcctgTransTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransTypeId | id | IN | false |
| attrName | id-long | IN | false |
| description | description | IN | true |

## createAgreement

- Definition: `applications/accounting/servicedef/services_agreement.xml:27`
- Engine: entity-auto; location: —; invoke: create
- Default entity: Agreement; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementDate | date-time | IN | true |
| agreementId | id | OUT | true |
| agreementTypeId | id | IN | true |
| description | description | IN | true |
| fromDate | date-time | IN | true |
| partyIdFrom | id | IN | true |
| partyIdTo | id | IN | true |
| productId | id | IN | true |
| roleTypeIdFrom | id | IN | true |
| roleTypeIdTo | id | IN | true |
| statusId | id | IN | true |
| textData | very-long | IN | true |
| thruDate | date-time | IN | true |

## createAgreementContent

- Definition: `applications/accounting/servicedef/services_agreement.xml:253`
- Engine: entity-auto; location: —; invoke: create
- Default entity: AgreementContent; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementContentTypeId | id | IN | false |
| agreementId | id | IN | false |
| agreementItemSeqId | id | IN | false |
| contentId | id | IN | false |
| fromDate | date-time | IN | true |
| thruDate | date-time | IN | true |

## createAgreementFacilityAppl

- Definition: `applications/accounting/servicedef/services_agreement.xml:161`
- Engine: entity-auto; location: —; invoke: create
- Default entity: AgreementFacilityAppl; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| agreementItemSeqId | id | IN | false |
| facilityId | id | IN | false |

## createAgreementGeographicalApplic

- Definition: `applications/accounting/servicedef/services_agreement.xml:201`
- Engine: entity-auto; location: —; invoke: create
- Default entity: AgreementGeographicalApplic; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| agreementItemSeqId | id | IN | false |
| geoId | id | IN | false |

## createAgreementItem

- Definition: `applications/accounting/servicedef/services_agreement.xml:58`
- Engine: entity-auto; location: —; invoke: create
- Default entity: AgreementItem; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | INOUT | false |
| agreementImage | object | IN | true |
| agreementItemSeqId | id | INOUT | true |
| agreementItemTypeId | id | IN | true |
| agreementText | very-long | IN | true |
| currencyUomId | id | IN | true |

## createAgreementItemAttribute

- Definition: `applications/accounting/servicedef/services_agreement.xml:80`
- Engine: entity-auto; location: —; invoke: create
- Default entity: AgreementItemAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| agreementItemSeqId | id | IN | false |
| attrDescription | description | IN | true |
| attrName | id-long | IN | false |
| attrValue | value | IN | true |

## createAgreementPartyApplic

- Definition: `applications/accounting/servicedef/services_agreement.xml:181`
- Engine: entity-auto; location: —; invoke: create
- Default entity: AgreementPartyApplic; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| agreementItemSeqId | id | IN | false |
| partyId | id | IN | false |

## createAgreementProductAppl

- Definition: `applications/accounting/servicedef/services_agreement.xml:141`
- Engine: entity-auto; location: —; invoke: create
- Default entity: AgreementProductAppl; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| agreementItemSeqId | id | IN | false |
| price | currency-precise | IN | true |
| productId | id | IN | false |

## createAgreementPromoAppl

- Definition: `applications/accounting/servicedef/services_agreement.xml:121`
- Engine: entity-auto; location: —; invoke: create
- Default entity: AgreementPromoAppl; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| agreementItemSeqId | id | IN | false |
| fromDate | date-time | IN | false |
| productPromoId | id | IN | false |
| sequenceNum | numeric | IN | true |
| thruDate | date-time | IN | true |

## createAgreementRole

- Definition: `applications/accounting/servicedef/services_agreement.xml:221`
- Engine: entity-auto; location: —; invoke: create
- Default entity: AgreementRole; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |

## createAgreementTerm

- Definition: `applications/accounting/servicedef/services_agreement.xml:99`
- Engine: entity-auto; location: —; invoke: create
- Default entity: AgreementTerm; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | true |
| agreementItemSeqId | id | IN | true |
| agreementTermId | id | INOUT | true |
| description | description | IN | true |
| fromDate | date-time | IN | true |
| invoiceItemTypeId | id | IN | true |
| maxQuantity | floating-point | IN | true |
| minQuantity | floating-point | IN | true |
| termDays | numeric | IN | true |
| termTypeId | id | IN | true |
| termValue | currency-precise | IN | true |
| textValue | description | IN | true |
| thruDate | date-time | IN | true |

## createAgreementType

- Definition: `applications/accounting/servicedef/services_agreement.xml:237`
- Engine: entity-auto; location: —; invoke: create
- Default entity: AgreementType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementTypeId | id | INOUT | true |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## createAgreementTypeAttr

- Definition: `applications/accounting/servicedef/services_agreement.xml:313`
- Engine: entity-auto; location: —; invoke: create
- Default entity: AgreementTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementTypeId | id | IN | false |
| attrName | id-long | IN | false |
| description | description | IN | true |

## createAgreementWorkEffortApplic

- Definition: `applications/accounting/servicedef/services_agreement.xml:301`
- Engine: entity-auto; location: —; invoke: create
- Default entity: AgreementWorkEffortApplic; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| agreementItemSeqId | id | IN | false |
| workEffortId | id | IN | false |

## createBillingAccount

- Definition: `applications/accounting/servicedef/services_billing.xml:28`
- Engine: entity-auto; location: —; invoke: create
- Default entity: BillingAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBillingAcctCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accountCurrencyUomId | id | IN | true |
| accountLimit | currency-amount | IN | true |
| billingAccountId | id | OUT | false |
| contactMechId | id | IN | true |
| description | description | IN | true |
| externalAccountId | id | IN | true |
| fromDate | date-time | IN | true |
| thruDate | date-time | IN | true |

## createBillingAccountAndRole

- Definition: `applications/accounting/servicedef/services_billing.xml:58`
- Engine: group; location: —; invoke: —
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| billingAccountId | — | — | true |

## createBillingAccountRole

- Definition: `applications/accounting/servicedef/services_billing.xml:40`
- Engine: entity-auto; location: —; invoke: create
- Default entity: BillingAccountRole; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBillingAcctCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| billingAccountId | id | IN | false |
| fromDate | date-time | IN | true |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |
| thruDate | date-time | IN | true |

## createBillingAccountTerm

- Definition: `applications/accounting/servicedef/services_billing.xml:65`
- Engine: entity-auto; location: —; invoke: create
- Default entity: BillingAccountTerm; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBillingAcctCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| billingAccountId | id | IN | false |
| billingAccountTermId | id | OUT | false |
| termDays | numeric | IN | true |
| termTypeId | id | IN | false |
| termValue | currency-amount | IN | true |
| uomId | id | IN | true |

## createBillingAccountTermAttr

- Definition: `applications/accounting/servicedef/services_payment.xml:275`
- Engine: entity-auto; location: —; invoke: create
- Default entity: BillingAccountTermAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | false |
| attrValue | value | IN | true |
| billingAccountTermId | id | IN | false |

## createBudget

- Definition: `applications/accounting/servicedef/services_budget.xml:28`
- Engine: entity-auto; location: —; invoke: create
- Default entity: Budget; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetId | id | INOUT | true |
| budgetTypeId | id | IN | true |
| comments | comment | IN | true |
| customTimePeriodId | id | IN | true |

## createBudgetAttribute

- Definition: `applications/accounting/servicedef/services_budget.xml:224`
- Engine: entity-auto; location: —; invoke: create
- Default entity: BudgetAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | false |
| attrValue | value | IN | true |
| budgetId | id | IN | false |

## createBudgetItem

- Definition: `applications/accounting/servicedef/services_budget.xml:52`
- Engine: entity-auto; location: —; invoke: create
- Default entity: BudgetItem; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | currency-amount | IN | true |
| budgetId | String | IN | false |
| budgetItemSeqId | String | OUT | false |
| budgetItemTypeId | id | IN | true |
| justification | long-varchar | IN | true |
| purpose | long-varchar | IN | true |

## createBudgetItemAttribute

- Definition: `applications/accounting/servicedef/services_budget.xml:104`
- Engine: entity-auto; location: —; invoke: create
- Default entity: BudgetItemAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | — |
| attrValue | value | IN | true |
| budgetId | id | IN | — |
| budgetItemSeqId | id | IN | — |

## createBudgetItemType

- Definition: `applications/accounting/servicedef/services_budget.xml:118`
- Engine: entity-auto; location: —; invoke: create
- Default entity: BudgetItemType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetItemTypeId | id | INOUT | true |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## createBudgetItemTypeAttr

- Definition: `applications/accounting/servicedef/services_budget.xml:132`
- Engine: entity-auto; location: —; invoke: create
- Default entity: BudgetItemTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| budgetItemTypeId | id | IN | — |
| description | description | IN | true |

## createBudgetReview

- Definition: `applications/accounting/servicedef/services_budget.xml:78`
- Engine: entity-auto; location: —; invoke: create
- Default entity: BudgetReview; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetId | id | IN | false |
| budgetReviewId | String | OUT | false |
| budgetReviewResultTypeId | id | IN | false |
| partyId | id | IN | false |
| reviewDate | date-time | IN | true |

## createBudgetReviewResultType

- Definition: `applications/accounting/servicedef/services_budget.xml:146`
- Engine: entity-auto; location: —; invoke: create
- Default entity: BudgetReviewResultType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetReviewResultTypeId | id | INOUT | true |
| comments | comment | IN | true |
| description | description | IN | true |

## createBudgetRevision

- Definition: `applications/accounting/servicedef/services_budget.xml:160`
- Engine: entity-auto; location: —; invoke: create
- Default entity: BudgetRevision; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetId | id | IN | — |
| dateRevised | date-time | IN | true |
| revisionSeqId | id | IN | — |

## createBudgetRevisionImpact

- Definition: `applications/accounting/servicedef/services_budget.xml:191`
- Engine: entity-auto; location: —; invoke: create
- Default entity: BudgetRevisionImpact; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| addDeleteFlag | indicator | IN | true |
| budgetId | id | IN | false |
| budgetItemSeqId | id | IN | false |
| revisedAmount | currency-amount | IN | true |
| revisionReason | long-varchar | IN | true |
| revisionSeqId | id | IN | false |

## createBudgetRole

- Definition: `applications/accounting/servicedef/services_budget.xml:68`
- Engine: entity-auto; location: —; invoke: create
- Default entity: BudgetRole; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetId | id | IN | false |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |

## createBudgetScenario

- Definition: `applications/accounting/servicedef/services_budget.xml:174`
- Engine: entity-auto; location: —; invoke: create
- Default entity: BudgetScenario; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetScenarioId | id | INOUT | true |
| description | description | IN | true |

## createBudgetScenarioApplication

- Definition: `applications/accounting/servicedef/services_budget.xml:252`
- Engine: entity-auto; location: —; invoke: create
- Default entity: BudgetScenarioApplication; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amountChange | currency-amount | IN | true |
| budgetId | id | IN | true |
| budgetItemSeqId | id | IN | true |
| budgetScenarioApplicId | id | IN | — |
| budgetScenarioId | id | IN | — |
| percentageChange | fixed-point | IN | true |

## createBudgetScenarioRule

- Definition: `applications/accounting/servicedef/services_budget.xml:206`
- Engine: entity-auto; location: —; invoke: create
- Default entity: BudgetScenarioRule; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amountChange | currency-amount | IN | true |
| budgetItemTypeId | id | IN | false |
| budgetScenarioId | id | IN | false |
| percentageChange | fixed-point | IN | true |

## createBudgetStatus

- Definition: `applications/accounting/servicedef/services_budget.xml:40`
- Engine: entity-auto; location: —; invoke: create
- Default entity: BudgetStatus; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetId | id | IN | false |
| changeByUserLoginId | id-vlong | IN | true |
| comments | comment | IN | true |
| statusDate | date-time | IN | true |
| statusId | id | IN | false |

## createBudgetType

- Definition: `applications/accounting/servicedef/services_budget.xml:88`
- Engine: entity-auto; location: —; invoke: create
- Default entity: BudgetType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetTypeId | id | INOUT | true |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## createBudgetTypeAttr

- Definition: `applications/accounting/servicedef/services_budget.xml:238`
- Engine: entity-auto; location: —; invoke: create
- Default entity: BudgetTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| budgetTypeId | id | IN | — |
| description | description | IN | true |

## createCheckAccount

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:142`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentMethodServices; invoke: createCheckAccount
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accountNumber | String | IN | true |
| accountType | String | IN | true |
| bankName | String | IN | false |
| companyNameOnAccount | String | IN | true |
| contactMechId | String | IN | true |
| description | String | IN | true |
| nameOnAccount | String | IN | false |
| partyId | String | IN | true |
| paymentMethodId | String | INOUT | true |
| paymentMethodTypeId | String | IN | false |
| routingNumber | String | IN | true |

## createCommissionInvoices

- Definition: `applications/accounting/servicedef/services_invoice.xml:214`
- Engine: java; location: org.apache.ofbiz.accounting.invoice.InvoiceServices; invoke: createCommissionInvoices
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceIds | List | IN | — |
| invoicesCreated | List | OUT | true |
| partyIds | List | IN | true |

## createCostComponentCalc

- Definition: `applications/accounting/servicedef/services_cost.xml:27`
- Engine: entity-auto; location: —; invoke: create
- Default entity: CostComponentCalc; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgCostPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| costComponentCalcId | id | OUT | true |
| costCustomMethodId | id | IN | true |
| costGlAccountTypeId | id | IN | true |
| currencyUomId | id | IN | true |
| description | description | IN | true |
| fixedCost | currency-amount | IN | true |
| offsettingGlAccountTypeId | id | IN | true |
| perMilliSecond | numeric | IN | true |
| variableCost | currency-amount | IN | true |

## createCreditCard

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:35`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentMethodServices; invoke: createCreditCard
- Default entity: CreditCard; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| cardNumber | credit-card-number | IN | false |
| cardType | short-varchar | IN | false |
| companyNameOnCard | name | IN | true |
| consecutiveFailedAuths | numeric | IN | true |
| consecutiveFailedNsf | numeric | IN | true |
| contactMechId | id | IN | true |
| description | String | IN | true |
| expMonth | String | IN | true |
| expYear | String | IN | true |
| expireDate | credit-card-date | IN | false |
| firstNameOnCard | name | IN | false |
| fromDate | Timestamp | IN | true |
| issueNumber | credit-card-date | IN | true |
| lastFailedAuthDate | date-time | IN | true |
| lastFailedNsfDate | date-time | IN | true |
| lastNameOnCard | name | IN | false |
| middleNameOnCard | name | IN | true |
| partyId | String | IN | true |
| paymentMethodId | id | INOUT | true |
| productStoreId | String | IN | true |
| suffixOnCard | name | IN | true |
| thruDate | Timestamp | IN | true |
| titleOnCard | name | IN | true |
| validFromDate | credit-card-date | IN | true |

## createCreditCardAndAddress

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:55`
- Engine: group; location: createCreditCardAndAddress; invoke: —
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | INOUT | true |
| paymentMethodId | String | INOUT | true |

## createCreditCardTypeGlAccount

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:806`
- Engine: entity-auto; location: —; invoke: create
- Default entity: CreditCardTypeGlAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| cardType | short-varchar | IN | false |
| glAccountId | id | IN | false |
| organizationPartyId | id | IN | false |

## createCustomerTaxAuthInfo

- Definition: `applications/accounting/servicedef/services_tax.xml:188`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/tax/TaxAuthorityServicesScript.groovy; invoke: createCustomerTaxAuthInfo
- Default entity: PartyTaxAuthInfo; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | String | IN | true |
| isExempt | indicator | IN | true |
| isNexus | indicator | IN | true |
| partyId | String | IN | — |
| partyTaxId | id-long | IN | true |
| taxAuthPartyGeoIds | String | IN | — |
| thruDate | date-time | IN | true |

## createDeduction

- Definition: `applications/accounting/servicedef/services_payment.xml:286`
- Engine: entity-auto; location: —; invoke: create
- Default entity: Deduction; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | currency-amount | IN | true |
| deductionId | id | INOUT | true |
| deductionTypeId | id | IN | true |
| paymentId | id | IN | true |

## createDeductionType

- Definition: `applications/accounting/servicedef/services_payment.xml:300`
- Engine: entity-auto; location: —; invoke: create
- Default entity: DeductionType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| deductionTypeId | id | INOUT | true |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## createEftAccount

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:125`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentMethodServices; invoke: createEftAccount
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accountNumber | String | IN | false |
| accountType | String | IN | false |
| bankName | String | IN | false |
| companyNameOnAccount | String | IN | true |
| contactMechId | String | IN | true |
| description | String | IN | true |
| fromDate | Timestamp | IN | true |
| nameOnAccount | String | IN | false |
| partyId | String | IN | true |
| paymentMethodId | String | INOUT | true |
| routingNumber | String | IN | false |
| thruDate | Timestamp | IN | true |

## createEftAccountAndAddress

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:176`
- Engine: group; location: createEftAccountAndAddress; invoke: —
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | INOUT | true |
| paymentMethodId | String | INOUT | true |

## createFinAccount

- Definition: `applications/accounting/servicedef/services_finaccount.xml:28`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountServices.xml; invoke: createFinAccount
- Default entity: FinAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgFinAcctPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| actualBalance | currency-amount | IN | true |
| availableBalance | currency-amount | IN | true |
| currencyUomId | id | IN | true |
| finAccountCode | long-varchar | IN | true |
| finAccountId | id | INOUT | true |
| finAccountName | name | IN | true |
| finAccountPin | long-varchar | IN | true |
| finAccountTypeId | id | IN | true |
| fromDate | date-time | IN | true |
| isRefundable | indicator | IN | true |
| organizationPartyId | id | IN | true |
| ownerPartyId | id | IN | true |
| postToGlAccountId | id | IN | true |
| replenishLevel | currency-amount | IN | true |
| replenishPaymentId | id | IN | true |
| statusId | id | IN | true |
| thruDate | date-time | IN | true |

## createFinAccountAndCredit

- Definition: `applications/accounting/servicedef/services_finaccount.xml:297`
- Engine: java; location: org.apache.ofbiz.accounting.finaccount.FinAccountServices; invoke: createAccountAndCredit
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | BigDecimal | IN | false |
| comments | String | IN | true |
| currencyUomId | String | IN | true |
| finAccountId | String | INOUT | true |
| finAccountName | String | IN | true |
| finAccountTypeId | String | IN | false |
| partyId | String | IN | false |
| productStoreId | String | IN | true |
| reasonEnumId | String | IN | true |
| replenishLevel | BigDecimal | IN | true |
| replenishPaymentId | String | IN | true |

## createFinAccountAttribute

- Definition: `applications/accounting/servicedef/services_finaccount.xml:535`
- Engine: entity-auto; location: —; invoke: create
- Default entity: FinAccountAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | INOUT | false |
| attrValue | value | IN | true |
| finAccountId | id | INOUT | false |

## createFinAccountAuth

- Definition: `applications/accounting/servicedef/services_finaccount.xml:113`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountServices.xml; invoke: createFinAccountAuth
- Default entity: FinAccountAuth; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgFinAcctPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | currency-amount | IN | true |
| authorizationDate | date-time | IN | true |
| currencyUomId | id | IN | true |
| finAccountAuthId | id | OUT | false |
| finAccountId | id | IN | true |
| fromDate | date-time | IN | true |
| thruDate | date-time | IN | true |

## createFinAccountForStore

- Definition: `applications/accounting/servicedef/services_finaccount.xml:142`
- Engine: java; location: org.apache.ofbiz.accounting.finaccount.FinAccountServices; invoke: createFinAccountForStore
- Default entity: FinAccount; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| actualBalance | currency-amount | IN | true |
| availableBalance | currency-amount | IN | true |
| currencyUomId | id | IN | true |
| finAccountCode | String | OUT | true |
| finAccountId | String | OUT | true |
| finAccountName | name | IN | true |
| finAccountPin | String | OUT | true |
| finAccountTypeId | id | IN | false |
| fromDate | date-time | IN | true |
| isRefundable | indicator | IN | true |
| organizationPartyId | id | IN | true |
| ownerPartyId | id | IN | true |
| postToGlAccountId | id | IN | true |
| productStoreId | String | IN | false |
| replenishLevel | currency-amount | IN | true |
| replenishPaymentId | id | IN | true |
| statusId | id | IN | true |
| thruDate | date-time | IN | true |

## createFinAccountRole

- Definition: `applications/accounting/servicedef/services_finaccount.xml:95`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountServices.xml; invoke: createFinAccountRole
- Default entity: FinAccountRole; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgFinAcctPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountId | id | IN | false |
| fromDate | date-time | IN | true |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |
| thruDate | date-time | IN | true |

## createFinAccountStatus

- Definition: `applications/accounting/servicedef/services_finaccount.xml:68`
- Engine: entity-auto; location: —; invoke: create
- Default entity: FinAccountStatus; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| changeByUserLoginId | id-vlong | IN | false |
| finAccountId | id | IN | false |
| statusDate | date-time | IN | true |
| statusEndDate | date-time | IN | false |
| statusId | id | IN | false |

## createFinAccountTrans

- Definition: `applications/accounting/servicedef/services_finaccount.xml:78`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountServices.xml; invoke: createFinAccountTrans
- Default entity: FinAccountTrans; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgFinAcctPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | currency-amount | IN | true |
| comments | comment | IN | true |
| entryDate | date-time | IN | true |
| finAccountId | id | IN | true |
| finAccountTransId | id | OUT | false |
| finAccountTransTypeId | id | IN | true |
| glAccountId | String | IN | true |
| glReconciliationId | id | IN | true |
| orderId | id | IN | true |
| orderItemSeqId | id | IN | true |
| partyId | id | IN | true |
| paymentId | id | IN | true |
| performedByPartyId | id | IN | true |
| reasonEnumId | id | IN | true |
| statusId | id | IN | true |
| transactionDate | date-time | IN | true |

## createFinAccountTransAttribute

- Definition: `applications/accounting/servicedef/services_finaccount.xml:551`
- Engine: entity-auto; location: —; invoke: create
- Default entity: FinAccountTransAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | INOUT | false |
| attrValue | value | IN | true |
| finAccountTransId | id | INOUT | false |

## createFinAccountTransType

- Definition: `applications/accounting/servicedef/services_finaccount.xml:567`
- Engine: entity-auto; location: —; invoke: create
- Default entity: FinAccountTransType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| finAccountTransTypeId | id | INOUT | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## createFinAccountTransTypeAttr

- Definition: `applications/accounting/servicedef/services_finaccount.xml:583`
- Engine: entity-auto; location: —; invoke: create
- Default entity: FinAccountTransTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | INOUT | false |
| description | description | IN | true |
| finAccountTransTypeId | id | INOUT | false |

## createFinAccountType

- Definition: `applications/accounting/servicedef/services_finaccount.xml:599`
- Engine: entity-auto; location: —; invoke: create
- Default entity: FinAccountType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| finAccountTypeId | id | INOUT | true |
| hasTable | indicator | IN | true |
| isRefundable | indicator | IN | true |
| parentTypeId | id | IN | true |
| replenishEnumId | id | IN | true |

## createFinAccountTypeAttr

- Definition: `applications/accounting/servicedef/services_finaccount.xml:615`
- Engine: entity-auto; location: —; invoke: create
- Default entity: FinAccountTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | INOUT | false |
| attrValue | long-varchar | IN | true |
| description | description | IN | true |
| finAccountTypeId | id | INOUT | false |

## createFinAccountTypeGlAccount

- Definition: `applications/accounting/servicedef/services_ledger.xml:378`
- Engine: entity-auto; location: —; invoke: create
- Default entity: FinAccountTypeGlAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountTypeId | id | IN | false |
| glAccountId | id | IN | false |
| organizationPartyId | id | IN | false |

## createFinAccoutnTransFromPayment

- Definition: `applications/accounting/servicedef/services_payment.xml:203`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy; invoke: createFinAccoutnTransFromPayment
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceIds | List | IN | true |

## createFixedAsset

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:28`
- Engine: entity-auto; location: —; invoke: create
- Default entity: FixedAsset; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acquireOrderId | id | IN | true |
| acquireOrderItemSeqId | id | IN | true |
| actualEndOfLife | date | IN | true |
| calendarId | id | IN | true |
| classEnumId | id | IN | true |
| dateAcquired | date-time | IN | true |
| dateLastServiced | date-time | IN | true |
| dateNextService | date-time | IN | true |
| depreciation | currency-amount | IN | true |
| expectedEndOfLife | date | IN | true |
| fixedAssetId | id | INOUT | true |
| fixedAssetName | name | IN | true |
| fixedAssetTypeId | id | IN | false |
| instanceOfProductId | id | IN | true |
| locatedAtFacilityId | id | IN | true |
| locatedAtLocationSeqId | id | IN | true |
| parentFixedAssetId | id | IN | true |
| partyId | id | IN | true |
| productionCapacity | fixed-point | IN | true |
| purchaseCost | currency-amount | IN | true |
| purchaseCostUomId | id | IN | true |
| roleTypeId | id | IN | true |
| salvageValue | currency-amount | IN | true |
| serialNumber | long-varchar | IN | true |
| uomId | id | IN | true |

## createFixedAssetAttribute

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:367`
- Engine: entity-auto; location: —; invoke: create
- Default entity: FixedAssetAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | INOUT | false |
| attrValue | value | IN | true |
| fixedAssetId | id | INOUT | false |

## createFixedAssetDepMethod

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:222`
- Engine: entity-auto; location: —; invoke: create
- Default entity: FixedAssetDepMethod; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| depreciationCustomMethodId | id | IN | false |
| fixedAssetId | id | IN | false |
| fromDate | date-time | IN | true |
| thruDate | date-time | IN | true |

## createFixedAssetGeoPoint

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:301`
- Engine: entity-auto; location: —; invoke: create
- Default entity: FixedAssetGeoPoint; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetId | id | IN | false |
| fromDate | date-time | IN | true |
| geoPointId | id | IN | false |
| thruDate | date-time | IN | true |

## createFixedAssetIdent

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:86`
- Engine: entity-auto; location: —; invoke: create
- Default entity: FixedAssetIdent; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetId | id | IN | false |
| fixedAssetIdentTypeId | id | IN | false |
| idValue | long-varchar | IN | true |

## createFixedAssetIdentType

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:383`
- Engine: entity-auto; location: —; invoke: create
- Default entity: FixedAssetIdentType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| fixedAssetIdentTypeId | id | INOUT | true |

## createFixedAssetMaint

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:123`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/fixedasset/FixedAssetServices.groovy; invoke: createFixedAssetMaint
- Default entity: FixedAssetMaint; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| estimatedCompletionDate | Timestamp | IN | true |
| estimatedStartDate | Timestamp | IN | true |
| fixedAssetId | id | IN | false |
| intervalMeterTypeId | id | IN | true |
| intervalQuantity | fixed-point | IN | true |
| intervalUomId | id | IN | true |
| maintHistSeqId | id | OUT | false |
| maintTemplateWorkEffortId | String | IN | true |
| productMaintSeqId | id | IN | true |
| productMaintTypeId | id | IN | true |
| purchaseOrderId | id | IN | true |
| scheduleWorkEffortId | id | IN | true |
| statusId | id | IN | true |

## createFixedAssetMaintOrder

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:185`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/fixedasset/FixedAssetServices.groovy; invoke: createFixedAssetMaintOrder
- Default entity: FixedAssetMaintOrder; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetId | String | IN | false |
| maintHistSeqId | String | IN | false |
| orderId | String | IN | false |
| orderItemSeqId | String | IN | true |

## createFixedAssetMeter

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:161`
- Engine: entity-auto; location: —; invoke: create
- Default entity: FixedAssetMeter; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetId | id | IN | false |
| maintHistSeqId | id | IN | true |
| meterValue | fixed-point | IN | true |
| productMeterTypeId | id | IN | false |
| readingDate | date-time | IN | false |
| readingReasonEnumId | id | IN | true |
| workEffortId | id | IN | true |

## createFixedAssetProductType

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:399`
- Engine: entity-auto; location: —; invoke: create
- Default entity: FixedAssetProductType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| fixedAssetProductTypeId | id | INOUT | true |

## createFixedAssetRegistration

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:104`
- Engine: entity-auto; location: —; invoke: create
- Default entity: FixedAssetRegistration; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetId | id | IN | false |
| fromDate | date-time | IN | true |
| govAgencyPartyId | id | IN | true |
| licenseNumber | long-varchar | IN | true |
| registrationDate | date-time | IN | true |
| registrationNumber | long-varchar | IN | true |
| thruDate | date-time | IN | true |

## createFixedAssetStdCost

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:65`
- Engine: entity-auto; location: —; invoke: create
- Default entity: FixedAssetStdCost; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | currency-amount | IN | true |
| amountUomId | id | IN | true |
| fixedAssetId | id | IN | false |
| fixedAssetStdCostTypeId | id | IN | false |
| fromDate | date-time | IN | false |
| thruDate | date-time | IN | true |

## createFixedAssetStdCostType

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:415`
- Engine: entity-auto; location: —; invoke: create
- Default entity: FixedAssetStdCostType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| fixedAssetStdCostTypeId | id | INOUT | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## createFixedAssetType

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:431`
- Engine: entity-auto; location: —; invoke: create
- Default entity: FixedAssetType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| fixedAssetTypeId | id | INOUT | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## createFixedAssetTypeAttr

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:447`
- Engine: entity-auto; location: —; invoke: create
- Default entity: FixedAssetTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | INOUT | false |
| description | description | IN | true |
| fixedAssetTypeId | id | INOUT | false |

## createFixedAssetTypeGlAccount

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:281`
- Engine: entity-auto; location: —; invoke: create
- Default entity: FixedAssetTypeGlAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accDepGlAccountId | id | IN | true |
| assetGlAccountId | id | IN | true |
| depGlAccountId | id | IN | true |
| fixedAssetId | String | IN | true |
| fixedAssetTypeId | String | IN | true |
| lossGlAccountId | id | IN | true |
| organizationPartyId | String | IN | false |
| profitGlAccountId | id | IN | true |

## createFuturePeriod

- Definition: `applications/accounting/servicedef/services_admin.xml:152`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/admin/AcctgAdminServices.groovy; invoke: createFuturePeriod
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## createGcFulFillmentRecord

- Definition: `applications/accounting/servicedef/services_finaccount.xml:400`
- Engine: java; location: org.apache.ofbiz.accounting.payment.GiftCertificateServices; invoke: createFulfillmentRecord
- Default entity: —; auth: true
- Use transaction: true; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | BigDecimal | IN | true |
| authCode | String | IN | true |
| cardNumber | String | IN | true |
| merchantId | String | IN | true |
| orderId | String | IN | true |
| orderItemSeqId | String | IN | true |
| partyId | String | IN | true |
| pinNumber | String | IN | true |
| referenceNum | String | IN | true |
| responseCode | String | IN | true |
| surveyResponseId | String | IN | true |
| typeEnumId | String | IN | false |

## createGiftCard

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:95`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentMethodServices; invoke: createGiftCard
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| cardNumber | String | IN | false |
| description | String | IN | true |
| expMonth | String | IN | true |
| expYear | String | IN | true |
| expireDate | String | IN | true |
| fromDate | Timestamp | IN | true |
| partyId | String | IN | true |
| paymentMethodId | String | INOUT | true |
| pinNumber | String | IN | true |
| thruDate | Timestamp | IN | true |

## createGiftCertificate

- Definition: `applications/accounting/servicedef/services_finaccount.xml:346`
- Engine: java; location: org.apache.ofbiz.accounting.payment.GiftCertificateServices; invoke: createGiftCertificate
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| cardNumber | String | OUT | false |
| currency | String | IN | true |
| initialAmount | BigDecimal | INOUT | false |
| orderId | String | IN | false |
| partyId | String | IN | true |
| pinNumber | String | OUT | true |
| processResult | Boolean | OUT | false |
| productStoreId | String | IN | false |
| referenceNum | String | OUT | false |
| responseCode | String | OUT | false |

## createGlAccount

- Definition: `applications/accounting/servicedef/services_ledger.xml:30`
- Engine: entity-auto; location: —; invoke: create
- Default entity: GlAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: basicGeneralLedgerPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accountCode | name | IN | true |
| accountName | name | IN | false |
| description | description | IN | true |
| externalId | id | IN | true |
| glAccountClassId | id | IN | false |
| glAccountId | id | OUT | false |
| glAccountTypeId | id | IN | false |
| glResourceTypeId | id | IN | false |
| glXbrlClassId | id | IN | true |
| parentGlAccountId | id | IN | true |
| productId | id | IN | true |

## createGlAccountCategory

- Definition: `applications/accounting/servicedef/services_ledger.xml:603`
- Engine: entity-auto; location: —; invoke: create
- Default entity: GlAccountCategory; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| glAccountCategoryId | id | OUT | false |
| glAccountCategoryTypeId | id | IN | true |

## createGlAccountCategoryMember

- Definition: `applications/accounting/servicedef/services_ledger.xml:613`
- Engine: entity-auto; location: —; invoke: create
- Default entity: GlAccountCategoryMember; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amountPercentage | fixed-point | IN | true |
| fromDate | date-time | IN | true |
| glAccountCategoryId | id | IN | false |
| glAccountId | id | IN | false |
| thruDate | date-time | IN | true |

## createGlAccountCategoryType

- Definition: `applications/accounting/servicedef/services_ledger.xml:736`
- Engine: entity-auto; location: —; invoke: create
- Default entity: GlAccountCategoryType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| glAccountCategoryTypeId | id | INOUT | true |

## createGlAccountClass

- Definition: `applications/accounting/servicedef/services_ledger.xml:754`
- Engine: entity-auto; location: —; invoke: create
- Default entity: GlAccountClass; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| glAccountClassId | id | INOUT | true |
| isAssetClass | indicator | IN | true |
| parentClassId | id | IN | true |
| sequenceNum | numeric | IN | true |

## createGlAccountGroup

- Definition: `applications/accounting/servicedef/services_ledger.xml:770`
- Engine: entity-auto; location: —; invoke: create
- Default entity: GlAccountGroup; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| glAccountGroupId | id | INOUT | true |
| glAccountGroupTypeId | id | IN | true |

## createGlAccountGroupMember

- Definition: `applications/accounting/servicedef/services_ledger.xml:786`
- Engine: entity-auto; location: —; invoke: create
- Default entity: GlAccountGroupMember; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glAccountGroupId | id | IN | true |
| glAccountGroupTypeId | id | INOUT | false |
| glAccountId | id | INOUT | false |

## createGlAccountGroupType

- Definition: `applications/accounting/servicedef/services_ledger.xml:802`
- Engine: entity-auto; location: —; invoke: create
- Default entity: GlAccountGroupType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| glAccountGroupTypeId | id | INOUT | true |

## createGlAccountOrganization

- Definition: `applications/accounting/servicedef/services_ledger.xml:52`
- Engine: entity-auto; location: —; invoke: create
- Default entity: GlAccountOrganization; auth: true
- Use transaction: —; timeout: —
- Permission service: basicGeneralLedgerPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | true |
| glAccountId | id | IN | false |
| organizationPartyId | id | IN | false |
| roleTypeId | id | IN | true |
| thruDate | date-time | IN | true |

## createGlAccountRole

- Definition: `applications/accounting/servicedef/services_ledger.xml:818`
- Engine: entity-auto; location: —; invoke: create
- Default entity: GlAccountRole; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | INOUT | false |
| glAccountId | id | INOUT | false |
| partyId | id | INOUT | false |
| roleTypeId | id | INOUT | false |
| thruDate | date-time | IN | true |

## createGlAccountType

- Definition: `applications/accounting/servicedef/services_ledger.xml:830`
- Engine: entity-auto; location: —; invoke: create
- Default entity: GlAccountType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| glAccountTypeId | id | INOUT | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## createGlAccountTypeDefault

- Definition: `applications/accounting/servicedef/services_admin.xml:101`
- Engine: entity-auto; location: —; invoke: create
- Default entity: GlAccountTypeDefault; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgPrefPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glAccountId | id | IN | false |
| glAccountTypeId | id | IN | false |
| organizationPartyId | id | IN | false |

## createGlAcctCatMemFromCostCenters

- Definition: `applications/accounting/servicedef/services_ledger.xml:630`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: createGlAcctCatMemFromCostCenters
- Default entity: GlAccountCategoryMember; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amountPercentage | BigDecimal | IN | true |
| glAccountCategoryId | String | IN | — |
| glAccountId | String | IN | — |
| totalAmountPercentage | BigDecimal | IN | true |

## createGlBudgetXref

- Definition: `applications/accounting/servicedef/services_ledger.xml:846`
- Engine: entity-auto; location: —; invoke: create
- Default entity: GlBudgetXref; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| allocationPercentage | fixed-point | IN | true |
| budgetItemTypeId | id | INOUT | false |
| fromDate | date-time | INOUT | false |
| glAccountId | id | INOUT | false |
| thruDate | date-time | IN | true |

## createGlFiscalType

- Definition: `applications/accounting/servicedef/services_ledger.xml:862`
- Engine: entity-auto; location: —; invoke: create
- Default entity: GlFiscalType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| glFiscalTypeId | id | INOUT | true |

## createGlJournal

- Definition: `applications/accounting/servicedef/services_ledger.xml:99`
- Engine: entity-auto; location: —; invoke: create
- Default entity: GlJournal; auth: true
- Use transaction: —; timeout: —
- Permission service: basicGeneralLedgerPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glJournalId | id | OUT | false |
| glJournalName | name | IN | true |
| isPosted | indicator | IN | true |
| organizationPartyId | id | IN | false |
| postedDate | date-time | IN | true |

## createGlReconciliation

- Definition: `applications/accounting/servicedef/services_ledger.xml:125`
- Engine: entity-auto; location: —; invoke: create
- Default entity: GlReconciliation; auth: true
- Use transaction: —; timeout: —
- Permission service: basicGeneralLedgerPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| createdByUserLogin | id-vlong | IN | true |
| createdDate | date-time | IN | true |
| description | description | IN | true |
| glAccountId | id | IN | true |
| glReconciliationId | id | OUT | false |
| glReconciliationName | name | IN | false |
| lastModifiedByUserLogin | id-vlong | IN | true |
| lastModifiedDate | date-time | IN | true |
| openingBalance | currency-amount | IN | true |
| organizationPartyId | id | IN | true |
| reconciledBalance | currency-amount | IN | true |
| reconciledDate | date-time | IN | true |
| statusId | id | IN | true |

## createGlReconciliationEntry

- Definition: `applications/accounting/servicedef/services_ledger.xml:158`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: createGlReconciliationEntry
- Default entity: GlReconciliationEntry; auth: true
- Use transaction: —; timeout: —
- Permission service: basicGeneralLedgerPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransEntrySeqId | id | IN | false |
| acctgTransId | id | IN | false |
| glReconciliationId | id | IN | false |
| reconciledAmount | currency-amount | IN | false |
| statusId | String | OUT | true |

## createGlResourceType

- Definition: `applications/accounting/servicedef/services_ledger.xml:878`
- Engine: entity-auto; location: —; invoke: create
- Default entity: GlResourceType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| glResourceTypeId | id | INOUT | true |

## createGlXbrlClass

- Definition: `applications/accounting/servicedef/services_ledger.xml:894`
- Engine: entity-auto; location: —; invoke: create
- Default entity: GlXbrlClass; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| glXbrlClassId | id | INOUT | true |
| parentGlXbrlClassId | id | IN | true |

## createInvoice

- Definition: `applications/accounting/servicedef/services_invoice.xml:50`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy; invoke: createInvoice
- Default entity: Invoice; auth: —
- Use transaction: —; timeout: —
- Permission service: acctgInvoicePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| billingAccountId | id | IN | true |
| contactMechId | id | IN | true |
| currencyUomId | id | IN | true |
| description | description | IN | true |
| dueDate | date-time | IN | true |
| invoiceDate | date-time | IN | true |
| invoiceId | id | INOUT | true |
| invoiceMessage | long-varchar | IN | true |
| invoiceTypeId | id | IN | true |
| paidDate | date-time | IN | true |
| partyId | id | IN | true |
| partyIdFrom | id | IN | true |
| recurrenceInfoId | id | IN | true |
| referenceNumber | short-varchar | IN | true |
| roleTypeId | id | IN | true |
| statusId | id | IN | true |

## createInvoiceAttribute

- Definition: `applications/accounting/servicedef/services_invoice.xml:504`
- Engine: entity-auto; location: —; invoke: create
- Default entity: InvoiceAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | INOUT | — |
| attrValue | value | IN | true |
| invoiceId | id | INOUT | — |

## createInvoiceContactMech

- Definition: `applications/accounting/servicedef/services_invoice.xml:302`
- Engine: entity-auto; location: —; invoke: create
- Default entity: InvoiceContactMech; auth: —
- Use transaction: —; timeout: —
- Permission service: acctgInvoicePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | id | IN | — |
| contactMechPurposeTypeId | id | IN | — |
| invoiceId | id | IN | — |

## createInvoiceContent

- Definition: `applications/accounting/servicedef/services_invoice.xml:404`
- Engine: entity-auto; location: —; invoke: create
- Default entity: InvoiceContent; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | INOUT | — |
| fromDate | date-time | INOUT | true |
| invoiceContentTypeId | id | INOUT | — |
| invoiceId | id | INOUT | — |
| thruDate | date-time | IN | true |

## createInvoiceContentAndUpdateContent

- Definition: `applications/accounting/servicedef/services_invoice.xml:419`
- Engine: group; location: —; invoke: —
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## createInvoiceContentType

- Definition: `applications/accounting/servicedef/services_invoice.xml:555`
- Engine: entity-auto; location: —; invoke: create
- Default entity: InvoiceContentType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| invoiceContentTypeId | id | INOUT | true |
| parentTypeId | id | IN | true |

## createInvoiceForOrder

- Definition: `applications/accounting/servicedef/services_invoice.xml:190`
- Engine: java; location: org.apache.ofbiz.accounting.invoice.InvoiceServices; invoke: createInvoiceForOrder
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| billItems | List | IN | — |
| eventDate | Timestamp | IN | true |
| invoiceId | String | INOUT | true |
| invoiceTypeId | String | OUT | true |
| orderId | String | IN | — |

## createInvoiceForOrderAllItems

- Definition: `applications/accounting/servicedef/services_invoice.xml:181`
- Engine: java; location: org.apache.ofbiz.accounting.invoice.InvoiceServices; invoke: createInvoiceForOrderAllItems
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | String | OUT | true |
| orderId | String | IN | — |

## createInvoiceFromOrder

- Definition: `applications/accounting/servicedef/services_invoice.xml:396`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy; invoke: createInvoiceFromOrder
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | String | OUT | true |
| orderId | String | IN | — |

## createInvoiceFromReturn

- Definition: `applications/accounting/servicedef/services_invoice.xml:203`
- Engine: java; location: org.apache.ofbiz.accounting.invoice.InvoiceServices; invoke: createInvoiceFromReturn
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| billItems | List | IN | true |
| invoiceId | String | OUT | true |
| returnId | String | IN | — |

## createInvoiceItem

- Definition: `applications/accounting/servicedef/services_invoice.xml:122`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy; invoke: createInvoiceItem
- Default entity: InvoiceItem; auth: —
- Use transaction: —; timeout: —
- Permission service: checkInvoiceStatusInProgress; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | currency-precise | IN | true |
| description | description | IN | true |
| inventoryItemId | id | IN | true |
| invoiceId | id | IN | — |
| invoiceItemSeqId | id | INOUT | true |
| invoiceItemTypeId | id | IN | true |
| overrideGlAccountId | id | IN | true |
| overrideOrgPartyId | id | IN | true |
| parentInvoiceId | id | IN | true |
| parentInvoiceItemSeqId | id | IN | true |
| productFeatureId | id | IN | true |
| productId | id | IN | true |
| quantity | fixed-point | IN | true |
| salesOpportunityId | id | IN | true |
| taxAuthGeoId | id | IN | true |
| taxAuthPartyId | id | IN | true |
| taxAuthorityRateSeqId | id | IN | true |
| taxableFlag | indicator | IN | true |
| uomId | id | IN | true |

## createInvoiceItemAssoc

- Definition: `applications/accounting/servicedef/services_invoice.xml:361`
- Engine: entity-auto; location: —; invoke: create
- Default entity: InvoiceItemAssoc; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | currency-amount | IN | true |
| fromDate | date-time | IN | true |
| invoiceIdFrom | id | IN | — |
| invoiceIdTo | id | IN | — |
| invoiceItemAssocTypeId | id | IN | — |
| invoiceItemSeqIdFrom | id | IN | — |
| invoiceItemSeqIdTo | id | IN | — |
| partyIdFrom | id | IN | true |
| partyIdTo | id | IN | true |
| quantity | fixed-point | IN | true |
| thruDate | date-time | IN | true |

## createInvoiceItemAssocType

- Definition: `applications/accounting/servicedef/services_invoice.xml:520`
- Engine: entity-auto; location: —; invoke: create
- Default entity: InvoiceItemAssocType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| invoiceItemAssocTypeId | id | INOUT | true |
| parentTypeId | id | IN | true |

## createInvoiceItemAttribute

- Definition: `applications/accounting/servicedef/services_invoice.xml:459`
- Engine: entity-auto; location: —; invoke: create
- Default entity: InvoiceItemAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | — |
| attrValue | value | IN | true |
| invoiceId | id | IN | — |
| invoiceItemSeqId | id | IN | — |

## createInvoiceItemTypeAttr

- Definition: `applications/accounting/servicedef/services_invoice.xml:571`
- Engine: entity-auto; location: —; invoke: create
- Default entity: InvoiceItemTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| description | description | IN | true |
| invoiceItemTypeId | id | IN | — |

## createInvoiceItemTypeMap

- Definition: `applications/accounting/servicedef/services_invoice.xml:586`
- Engine: entity-auto; location: —; invoke: create
- Default entity: InvoiceItemTypeMap; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceItemMapKey | id | IN | — |
| invoiceItemTypeId | id | IN | true |
| invoiceTypeId | id | IN | — |

## createInvoiceNote

- Definition: `applications/accounting/servicedef/services_invoice.xml:536`
- Engine: entity-auto; location: —; invoke: create
- Default entity: InvoiceNote; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | id | INOUT | — |
| noteId | id | INOUT | — |

## createInvoiceRole

- Definition: `applications/accounting/servicedef/services_invoice.xml:152`
- Engine: entity-auto; location: —; invoke: create
- Default entity: InvoiceRole; auth: —
- Use transaction: —; timeout: —
- Permission service: checkInvoiceStatusInProgress; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| datetimePerformed | date-time | IN | true |
| invoiceId | id | IN | — |
| partyId | id | IN | — |
| percentage | fixed-point | IN | true |
| roleTypeId | id | IN | — |

## createInvoiceStatus

- Definition: `applications/accounting/servicedef/services_invoice.xml:146`
- Engine: entity-auto; location: —; invoke: create
- Default entity: InvoiceStatus; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | id | IN | — |
| statusDate | date-time | IN | — |
| statusId | id | IN | — |

## createInvoiceTerm

- Definition: `applications/accounting/servicedef/services_invoice.xml:164`
- Engine: entity-auto; location: —; invoke: create
- Default entity: InvoiceTerm; auth: —
- Use transaction: —; timeout: —
- Permission service: checkInvoiceStatusInProgress; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| invoiceId | id | IN | true |
| invoiceItemSeqId | id | IN | true |
| invoiceTermId | id | OUT | — |
| termDays | numeric | IN | true |
| termTypeId | id | IN | true |
| termValue | currency-amount | IN | true |
| textValue | description | IN | true |
| uomId | id | IN | true |

## createInvoiceTermAttribute

- Definition: `applications/accounting/servicedef/services_invoice.xml:473`
- Engine: entity-auto; location: —; invoke: create
- Default entity: InvoiceTermAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | — |
| attrValue | value | IN | true |
| invoiceTermId | id | IN | — |

## createInvoiceType

- Definition: `applications/accounting/servicedef/services_invoice.xml:601`
- Engine: entity-auto; location: —; invoke: create
- Default entity: InvoiceType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| invoiceTypeId | id | INOUT | true |
| parentTypeId | id | IN | true |

## createInvoiceTypeAttr

- Definition: `applications/accounting/servicedef/services_invoice.xml:488`
- Engine: entity-auto; location: —; invoke: create
- Default entity: InvoiceTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| description | description | IN | true |
| invoiceTypeId | id | IN | — |

## createInvoicesFromReturnShipment

- Definition: `applications/accounting/servicedef/services_invoice.xml:276`
- Engine: java; location: org.apache.ofbiz.accounting.invoice.InvoiceServices; invoke: createInvoicesFromReturnShipment
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoicesCreated | List | OUT | true |
| shipmentId | String | IN | — |

## createInvoicesFromShipment

- Definition: `applications/accounting/servicedef/services_invoice.xml:233`
- Engine: java; location: org.apache.ofbiz.accounting.invoice.InvoiceServices; invoke: createInvoicesFromShipment
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| eventDate | Timestamp | IN | true |
| invoicesCreated | List | OUT | true |
| shipmentId | String | IN | — |

## createInvoicesFromShipments

- Definition: `applications/accounting/servicedef/services_invoice.xml:260`
- Engine: java; location: org.apache.ofbiz.accounting.invoice.InvoiceServices; invoke: createInvoicesFromShipments
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| createSalesInvoicesForDropShipments | Boolean | IN | true |
| eventDate | Timestamp | IN | true |
| invoicesCreated | List | OUT | true |
| shipmentIds | List | IN | — |

## createMaintsFromMeterReading

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:173`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/fixedasset/FixedAssetServices.groovy; invoke: createMaintsFromMeterReading
- Default entity: FixedAssetMeter; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetId | id | IN | false |
| maintHistSeqId | id | IN | true |
| meterValue | fixed-point | IN | true |
| productMeterTypeId | id | IN | false |
| readingDate | date-time | IN | false |
| readingReasonEnumId | id | IN | true |
| workEffortId | id | IN | true |

## createMaintsFromTimeInterval

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:147`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/fixedasset/FixedAssetServices.groovy; invoke: createMaintsFromTimeInterval
- Default entity: —; auth: true
- Use transaction: false; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## createMatchingPaymentApplication

- Definition: `applications/accounting/servicedef/services_payment.xml:247`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy; invoke: createMatchingPaymentApplication
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | String | IN | true |
| paymentId | String | IN | true |

## createPartyAcctgPreference

- Definition: `applications/accounting/servicedef/services_admin.xml:27`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/admin/AcctgAdminServices.groovy; invoke: createPartyAcctgPreference
- Default entity: PartyAcctgPreference; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgPrefPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| baseCurrencyUomId | id | IN | true |
| cogsMethodId | id | IN | true |
| enableAccounting | indicator | IN | true |
| errorGlJournalId | id | IN | true |
| fiscalYearStartDay | numeric | IN | true |
| fiscalYearStartMonth | numeric | IN | true |
| invoiceIdPrefix | very-short | IN | true |
| invoiceSeqCustMethId | id | IN | true |
| lastInvoiceNumber | numeric | IN | true |
| lastInvoiceRestartDate | date-time | IN | true |
| lastOrderNumber | numeric | IN | true |
| lastQuoteNumber | numeric | IN | true |
| orderIdPrefix | very-short | IN | true |
| orderSeqCustMethId | id | IN | true |
| partyId | id | IN | false |
| quoteIdPrefix | very-short | IN | true |
| quoteSeqCustMethId | id | IN | true |
| refundPaymentMethodId | id | IN | true |
| taxFormId | id | IN | true |
| useInvoiceIdForReturns | indicator | IN | true |

## createPartyFinAccountFromPurchase

- Definition: `applications/accounting/servicedef/services_finaccount.xml:313`
- Engine: java; location: org.apache.ofbiz.accounting.finaccount.FinAccountProductServices; invoke: createPartyFinAccountFromPurchase
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "itemFulfillmentInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountId | String | OUT | false |

## createPartyFixedAssetAssignment

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:202`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PartyFixedAssetAssignment; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| allocatedDate | date-time | IN | true |
| comments | comment | IN | true |
| fixedAssetId | id | IN | false |
| fromDate | date-time | IN | true |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |
| statusId | id | IN | true |
| thruDate | date-time | IN | true |

## createPartyGlAccount

- Definition: `applications/accounting/servicedef/services_ledger.xml:521`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PartyGlAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glAccountId | String | IN | false |
| glAccountTypeId | id | IN | false |
| organizationPartyId | id | IN | false |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |

## createPartyPrefDocTypeTpl

- Definition: `applications/accounting/servicedef/services_admin.xml:60`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PartyPrefDocTypeTpl; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgPrefPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| customScreenId | id | IN | true |
| fromDate | date-time | IN | true |
| invoiceTypeId | id | IN | true |
| orderTypeId | id | IN | true |
| partyId | id | IN | false |
| partyPrefDocTypeTplId | String | OUT | — |
| quoteTypeId | id | IN | true |
| thruDate | date-time | IN | true |

## createPartyTaxAuthInfo

- Definition: `applications/accounting/servicedef/services_tax.xml:167`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/tax/TaxAuthorityServicesScript.groovy; invoke: createPartyTaxAuthInfo
- Default entity: PartyTaxAuthInfo; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | true |
| isExempt | indicator | IN | true |
| isNexus | indicator | IN | true |
| partyId | id | IN | — |
| partyTaxId | id-long | IN | true |
| taxAuthGeoId | id | IN | — |
| taxAuthPartyId | id | IN | — |
| thruDate | date-time | IN | true |

## createPayPalPaymentMethod

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:217`
- Engine: simple; location: component://accounting/minilang/payment/PaymentMethodServices.xml; invoke: createPayPalPaymentMethod
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | String | IN | true |
| fromDate | Timestamp | IN | true |
| partyId | String | IN | true |
| thruDate | Timestamp | IN | true |

## createPayment

- Definition: `applications/accounting/servicedef/services_payment.xml:28`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy; invoke: createPayment
- Default entity: Payment; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| actualCurrencyAmount | currency-amount | IN | true |
| actualCurrencyUomId | id | IN | true |
| amount | currency-amount | IN | false |
| comments | comment | IN | true |
| currencyUomId | id | IN | true |
| effectiveDate | date-time | IN | true |
| finAccountTransId | id | IN | true |
| overrideGlAccountId | id | IN | true |
| partyIdFrom | id | IN | false |
| partyIdTo | id | IN | false |
| paymentGatewayResponseId | id | IN | true |
| paymentId | id | INOUT | true |
| paymentMethodId | id | IN | true |
| paymentMethodTypeId | id | IN | true |
| paymentPreferenceId | id | IN | true |
| paymentRefNum | short-varchar | IN | true |
| paymentTypeId | id | IN | false |
| roleTypeIdTo | id | IN | true |
| statusId | id | IN | true |

## createPaymentAndApplication

- Definition: `applications/accounting/servicedef/services_payment.xml:111`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy; invoke: createPaymentAndApplication
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | — | — | false |
| billingAccountId | String | IN | true |
| invoiceId | String | IN | true |
| invoiceItemSeqId | String | IN | true |
| overrideGlAccountId | String | IN | true |
| partyIdFrom | — | — | false |
| partyIdTo | — | — | false |
| paymentApplicationId | String | OUT | false |
| paymentTypeId | — | — | false |
| statusId | — | — | false |
| taxAuthGeoId | String | IN | true |

## createPaymentAndApplicationForParty

- Definition: `applications/accounting/servicedef/services_payment.xml:176`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy; invoke: createPaymentAndApplicationForParty
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | BigDecimal | OUT | true |
| checkStartNumber | Long | IN | true |
| finAccountId | String | INOUT | true |
| invoiceIds | List | OUT | true |
| invoices | List | IN | false |
| organizationPartyId | String | INOUT | true |
| partyId | String | IN | false |
| paymentId | String | OUT | true |
| paymentMethodId | String | IN | false |
| paymentMethodTypeId | String | IN | true |

## createPaymentAndFinAccountTrans

- Definition: `applications/accounting/servicedef/services_finaccount.xml:475`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountServices.xml; invoke: createPaymentAndFinAccountTrans
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "createPayment"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountTransTypeId | String | IN | false |
| isDepositWithDrawPayment | String | IN | true |
| paymentGroupTypeId | String | IN | true |
| paymentId | String | OUT | true |
| paymentMethodId | String | IN | true |

## createPaymentAndPaymentGroupForInvoices

- Definition: `applications/accounting/servicedef/services_payment.xml:166`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy; invoke: createPaymentAndPaymentGroupForInvoices
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| checkStartNumber | Long | IN | true |
| invoiceIds | List | IN | false |
| organizationPartyId | String | IN | false |
| paymentGroupId | String | OUT | true |
| paymentMethodId | String | IN | false |
| paymentMethodTypeId | String | IN | true |

## createPaymentApplication

- Definition: `applications/accounting/servicedef/services_payment.xml:69`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy; invoke: createPaymentApplication
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amountApplied | BigDecimal | INOUT | true |
| billingAccountId | String | IN | true |
| invoiceId | String | IN | true |
| paymentApplicationId | String | OUT | false |
| paymentId | String | IN | false |
| paymentTypeId | String | OUT | false |
| taxAuthGeoId | String | IN | true |
| toPaymentId | String | IN | true |

## createPaymentAttribute

- Definition: `applications/accounting/servicedef/services_payment.xml:315`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PaymentAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | — |
| attrValue | value | IN | true |
| paymentId | id | IN | — |

## createPaymentBudgetAllocation

- Definition: `applications/accounting/servicedef/services_payment.xml:330`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PaymentBudgetAllocation; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | currency-amount | IN | true |
| budgetId | id | IN | — |
| budgetItemSeqId | id | IN | — |
| paymentId | id | IN | — |

## createPaymentContent

- Definition: `applications/accounting/servicedef/services_payment.xml:255`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy; invoke: createPaymentContent
- Default entity: PaymentContent; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | INOUT | false |
| fromDate | date-time | INOUT | true |
| paymentContentTypeId | id | INOUT | false |
| paymentId | id | INOUT | false |
| thruDate | date-time | IN | true |

## createPaymentContentType

- Definition: `applications/accounting/servicedef/services_payment.xml:345`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PaymentContentType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| paymentContentTypeId | id | INOUT | true |

## createPaymentFromOrder

- Definition: `applications/accounting/servicedef/services_payment.xml:237`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy; invoke: createPaymentFromOrder
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | String | IN | true |
| orderId | String | IN | false |
| paymentId | String | OUT | true |
| paymentMethodId | String | IN | true |
| paymentRefNum | String | IN | true |

## createPaymentGatewayConfig

- Definition: `applications/accounting/servicedef/services_paymentgateway.xml:28`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PaymentGatewayConfig; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| paymentGatewayConfigId | id | INOUT | true |
| paymentGatewayConfigTypeId | id | IN | true |

## createPaymentGroup

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:838`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PaymentGroup; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| paymentGroupId | id | OUT | false |
| paymentGroupName | name | IN | false |
| paymentGroupTypeId | id | IN | false |

## createPaymentGroupAndMember

- Definition: `applications/accounting/servicedef/services_payment.xml:190`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy; invoke: createPaymentGroupAndMember
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | Timestamp | IN | true |
| paymentGroupId | String | OUT | false |
| paymentGroupName | String | IN | true |
| paymentGroupTypeId | String | IN | false |
| paymentIds | List | IN | false |

## createPaymentGroupMember

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:856`
- Engine: simple; location: component://accounting/minilang/payment/PaymentMethodServices.xml; invoke: createPaymentGroupMember
- Default entity: PaymentGroupMember; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | true |
| paymentGroupId | id | IN | false |
| paymentId | id | IN | false |
| sequenceNum | numeric | IN | true |
| thruDate | date-time | IN | true |

## createPaymentGroupType

- Definition: `applications/accounting/servicedef/services_payment.xml:360`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PaymentGroupType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| paymentGroupTypeId | id | INOUT | true |

## createPaymentMethodType

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:822`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PaymentMethodType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| defaultGlAccountId | id | IN | true |
| description | description | IN | true |
| paymentMethodTypeId | id | INOUT | true |

## createPaymentType

- Definition: `applications/accounting/servicedef/services_payment.xml:375`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PaymentType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| paymentTypeId | id | INOUT | true |

## createPaymentTypeAttr

- Definition: `applications/accounting/servicedef/services_payment.xml:390`
- Engine: entity-auto; location: —; invoke: create
- Default entity: PaymentTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| description | description | IN | true |
| paymentTypeId | id | IN | — |

## createProductAverageCost

- Definition: `applications/accounting/servicedef/services_cost.xml:69`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ProductAverageCost; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgCostPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| averageCost | fixed-point | IN | false |
| facilityId | id | IN | false |
| fromDate | date-time | IN | true |
| organizationPartyId | id | IN | false |
| productAverageCostTypeId | id | IN | false |
| productId | id | IN | false |
| thruDate | date-time | IN | true |

## createProductAverageCostType

- Definition: `applications/accounting/servicedef/services_ledger.xml:926`
- Engine: entity-auto; location: —; invoke: create
- Default entity: ProductAverageCostType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| productAverageCostTypeId | id | INOUT | true |

## createRateType

- Definition: `applications/accounting/servicedef/services_rate.xml:140`
- Engine: entity-auto; location: —; invoke: create
- Default entity: RateType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| rateTypeId | id | INOUT | true |

## createSalesInvoicesFromDropShipment

- Definition: `applications/accounting/servicedef/services_invoice.xml:251`
- Engine: java; location: org.apache.ofbiz.accounting.invoice.InvoiceServices; invoke: createSalesInvoicesFromDropShipment
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoicesCreated | List | OUT | true |
| shipmentId | String | IN | — |

## createServiceCredit

- Definition: `applications/accounting/servicedef/services_finaccount.xml:283`
- Engine: java; location: org.apache.ofbiz.accounting.finaccount.FinAccountServices; invoke: createAccountAndCredit
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | BigDecimal | IN | false |
| comments | String | IN | true |
| currencyUomId | String | IN | true |
| finAccountId | String | INOUT | true |
| finAccountName | String | IN | true |
| finAccountTypeId | String | IN | — |
| partyId | String | IN | false |
| productStoreId | String | IN | true |
| reasonEnumId | String | IN | true |

## createSettlementTerm

- Definition: `applications/accounting/servicedef/services_ledger.xml:910`
- Engine: entity-auto; location: —; invoke: create
- Default entity: SettlementTerm; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| settlementTermId | id | INOUT | true |
| termName | name | IN | true |
| termValue | numeric | IN | true |
| uomId | id | IN | true |

## createSimpleTextContentForInvoice

- Definition: `applications/accounting/servicedef/services_invoice.xml:433`
- Engine: group; location: —; invoke: —
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## createTaxAuthority

- Definition: `applications/accounting/servicedef/services_tax.xml:71`
- Engine: entity-auto; location: —; invoke: create
- Default entity: TaxAuthority; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| includeTaxInPrice | indicator | IN | true |
| requireTaxIdForExemption | indicator | IN | true |
| taxAuthGeoId | id | IN | false |
| taxAuthPartyId | id | IN | false |
| taxIdFormatPattern | long-varchar | IN | true |

## createTaxAuthorityAssoc

- Definition: `applications/accounting/servicedef/services_tax.xml:90`
- Engine: entity-auto; location: —; invoke: create
- Default entity: TaxAuthorityAssoc; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | true |
| taxAuthGeoId | id | IN | false |
| taxAuthPartyId | id | IN | false |
| taxAuthorityAssocTypeId | id | IN | true |
| thruDate | date-time | IN | true |
| toTaxAuthGeoId | id | IN | false |
| toTaxAuthPartyId | id | IN | false |

## createTaxAuthorityAssocType

- Definition: `applications/accounting/servicedef/services_tax.xml:198`
- Engine: entity-auto; location: —; invoke: create
- Default entity: TaxAuthorityAssocType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| taxAuthorityAssocTypeId | id | INOUT | true |

## createTaxAuthorityCategory

- Definition: `applications/accounting/servicedef/services_tax.xml:110`
- Engine: entity-auto; location: —; invoke: create
- Default entity: TaxAuthorityCategory; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| productCategoryId | id | IN | false |
| taxAuthGeoId | id | IN | false |
| taxAuthPartyId | id | IN | false |

## createTaxAuthorityGlAccount

- Definition: `applications/accounting/servicedef/services_tax.xml:129`
- Engine: entity-auto; location: —; invoke: create
- Default entity: TaxAuthorityGlAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glAccountId | id | IN | true |
| organizationPartyId | id | IN | false |
| taxAuthGeoId | id | IN | false |
| taxAuthPartyId | id | IN | false |

## createTaxAuthorityRateProduct

- Definition: `applications/accounting/servicedef/services_tax.xml:148`
- Engine: entity-auto; location: —; invoke: create
- Default entity: TaxAuthorityRateProduct; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| fromDate | date-time | IN | true |
| isTaxInShippingPrice | indicator | IN | true |
| minItemPrice | currency-amount | IN | true |
| minPurchase | currency-amount | IN | true |
| productCategoryId | id | IN | true |
| productStoreId | id | IN | true |
| taxAuthGeoId | id | IN | true |
| taxAuthPartyId | id | IN | true |
| taxAuthorityRateSeqId | id | OUT | false |
| taxAuthorityRateTypeId | id | IN | true |
| taxPercentage | fixed-point | IN | true |
| taxPromotions | indicator | IN | true |
| taxShipping | indicator | IN | true |
| thruDate | date-time | IN | true |
| titleTransferEnumId | id | IN | true |

## createTaxAuthorityRateType

- Definition: `applications/accounting/servicedef/services_tax.xml:214`
- Engine: entity-auto; location: —; invoke: create
- Default entity: TaxAuthorityRateType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| taxAuthorityRateTypeId | id | INOUT | true |

## createUpdateCostCenter

- Definition: `applications/accounting/servicedef/services_ledger.xml:667`
- Engine: java; location: org.apache.ofbiz.accounting.ledger.GeneralLedgerServices; invoke: createUpdateCostCenter
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amountPercentageMap | Map | IN | true |
| glAccountId | String | IN | — |

## createVarianceReasonGlAccount

- Definition: `applications/accounting/servicedef/services_ledger.xml:394`
- Engine: entity-auto; location: —; invoke: create
- Default entity: VarianceReasonGlAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glAccountId | id | IN | false |
| organizationPartyId | id | IN | false |
| varianceReasonId | id | IN | false |

## createWorkEffortCostCalc

- Definition: `applications/accounting/servicedef/services_cost.xml:48`
- Engine: entity-auto; location: —; invoke: create
- Default entity: WorkEffortCostCalc; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgCostPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| costComponentCalcId | id | IN | true |
| costComponentTypeId | id | IN | false |
| fromDate | date-time | IN | true |
| thruDate | date-time | IN | true |
| workEffortId | id | IN | false |

## creditOrderPaymentPreference

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:331`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: creditOrderPaymentPreference
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| orderPaymentPreferenceId | String | IN | false |

## decryptFinAccountCodes

- Definition: `applications/accounting/servicedef/services_finaccount.xml:630`
- Engine: java; location: org.apache.ofbiz.accounting.finaccount.FinAccountServices; invoke: decryptFinAccountCodes
- Default entity: —; auth: true
- Use transaction: —; timeout: 14400
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## deleteAccommodationClass

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:331`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AccommodationClass; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accommodationClassId | id | IN | false |

## deleteAccommodationMap

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:361`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AccommodationMap; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accommodationMapId | id | IN | — |

## deleteAccommodationMapType

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:345`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AccommodationMapType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accommodationMapTypeId | id | IN | false |

## deleteAcctgTrans

- Definition: `applications/accounting/servicedef/services_ledger.xml:209`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AcctgTrans; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgTransactionPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | id | IN | false |

## deleteAcctgTransAttribute

- Definition: `applications/accounting/servicedef/services_ledger.xml:687`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AcctgTransAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | id | IN | false |
| attrName | id-long | IN | false |

## deleteAcctgTransEntry

- Definition: `applications/accounting/servicedef/services_ledger.xml:247`
- Engine: simple; location: component://accounting/minilang/ledger/AcctgTransServices.xml; invoke: deleteAcctgTransEntry
- Default entity: AcctgTransEntry; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgTransactionPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransEntrySeqId | id | IN | false |
| acctgTransId | id | IN | false |

## deleteAcctgTransEntryType

- Definition: `applications/accounting/servicedef/services_ledger.xml:716`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AcctgTransEntryType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransEntryTypeId | id | IN | false |

## deleteAcctgTransTypeAttr

- Definition: `applications/accounting/servicedef/services_ledger.xml:701`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AcctgTransTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransTypeId | id | IN | false |
| attrName | id-long | IN | false |

## deleteAgreementItemAttribute

- Definition: `applications/accounting/servicedef/services_agreement.xml:92`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AgreementItemAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| agreementItemSeqId | id | IN | false |
| attrName | id-long | IN | false |

## deleteAgreementRole

- Definition: `applications/accounting/servicedef/services_agreement.xml:231`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AgreementRole; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |

## deleteAgreementTerm

- Definition: `applications/accounting/servicedef/services_agreement.xml:113`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AgreementTerm; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | true |
| agreementItemSeqId | id | IN | true |
| agreementTermId | id | IN | false |
| description | description | IN | true |
| fromDate | date-time | IN | true |
| invoiceItemTypeId | id | IN | true |
| maxQuantity | floating-point | IN | true |
| minQuantity | floating-point | IN | true |
| termDays | numeric | IN | true |
| termTypeId | id | IN | true |
| termValue | currency-precise | IN | true |
| textValue | description | IN | true |
| thruDate | date-time | IN | true |

## deleteAgreementWorkEffortApplic

- Definition: `applications/accounting/servicedef/services_agreement.xml:306`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AgreementWorkEffortApplic; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| agreementItemSeqId | id | IN | false |
| workEffortId | id | IN | false |

## deleteBillingAccountTermAttr

- Definition: `applications/accounting/servicedef/services_payment.xml:283`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: BillingAccountTermAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | false |
| billingAccountTermId | id | IN | false |

## deleteBudgetItemAttribute

- Definition: `applications/accounting/servicedef/services_budget.xml:114`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: BudgetItemAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| budgetId | id | IN | — |
| budgetItemSeqId | id | IN | — |

## deleteBudgetItemTypeAttr

- Definition: `applications/accounting/servicedef/services_budget.xml:142`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: BudgetItemTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| budgetItemTypeId | id | IN | — |

## deleteBudgetReviewResultType

- Definition: `applications/accounting/servicedef/services_budget.xml:156`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: BudgetReviewResultType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetReviewResultTypeId | id | IN | — |

## deleteBudgetRevision

- Definition: `applications/accounting/servicedef/services_budget.xml:170`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: BudgetRevision; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetId | id | IN | — |
| revisionSeqId | id | IN | — |

## deleteBudgetScenario

- Definition: `applications/accounting/servicedef/services_budget.xml:184`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: BudgetScenario; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetScenarioId | id | IN | — |
| description | description | IN | true |

## deleteBudgetScenarioApplication

- Definition: `applications/accounting/servicedef/services_budget.xml:262`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: BudgetScenarioApplication; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetScenarioApplicId | id | IN | — |
| budgetScenarioId | id | IN | — |

## deleteBudgetScenarioRule

- Definition: `applications/accounting/servicedef/services_budget.xml:218`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: BudgetScenarioRule; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetItemTypeId | id | IN | false |
| budgetScenarioId | id | IN | false |

## deleteBudgetType

- Definition: `applications/accounting/servicedef/services_budget.xml:98`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: BudgetType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetTypeId | id | IN | — |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## deleteBudgetTypeAttr

- Definition: `applications/accounting/servicedef/services_budget.xml:248`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: BudgetTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| budgetTypeId | id | IN | — |

## deleteCreditCardTypeGlAccount

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:816`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: CreditCardTypeGlAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| cardType | short-varchar | IN | false |
| organizationPartyId | id | IN | false |

## deleteDeduction

- Definition: `applications/accounting/servicedef/services_payment.xml:296`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: Deduction; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| deductionId | id | IN | — |

## deleteDeductionType

- Definition: `applications/accounting/servicedef/services_payment.xml:310`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: DeductionType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| deductionTypeId | id | IN | — |

## deleteFinAccount

- Definition: `applications/accounting/servicedef/services_finaccount.xml:53`
- Engine: entity-auto; location: —; invoke: expire
- Default entity: FinAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgFinAcctPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountId | id | IN | false |

## deleteFinAccountAttribute

- Definition: `applications/accounting/servicedef/services_finaccount.xml:545`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: FinAccountAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | false |
| finAccountId | id | IN | false |

## deleteFinAccountRole

- Definition: `applications/accounting/servicedef/services_finaccount.xml:108`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: FinAccountRole; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountId | id | IN | false |
| fromDate | date-time | IN | false |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |

## deleteFinAccountTransAttribute

- Definition: `applications/accounting/servicedef/services_finaccount.xml:561`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: FinAccountTransAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | false |
| finAccountTransId | id | IN | false |

## deleteFinAccountTransType

- Definition: `applications/accounting/servicedef/services_finaccount.xml:577`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: FinAccountTransType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountTransTypeId | id | IN | false |

## deleteFinAccountTransTypeAttr

- Definition: `applications/accounting/servicedef/services_finaccount.xml:593`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: FinAccountTransTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | false |
| finAccountTransTypeId | id | IN | false |

## deleteFinAccountType

- Definition: `applications/accounting/servicedef/services_finaccount.xml:609`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: FinAccountType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountTypeId | id | IN | false |

## deleteFinAccountTypeAttr

- Definition: `applications/accounting/servicedef/services_finaccount.xml:625`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: FinAccountTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | false |
| finAccountTypeId | id | IN | false |

## deleteFinAccountTypeGlAccount

- Definition: `applications/accounting/servicedef/services_ledger.xml:388`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: FinAccountTypeGlAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountTypeId | id | IN | false |
| organizationPartyId | id | IN | false |

## deleteFixedAssetAttribute

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:377`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: FixedAssetAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | false |
| fixedAssetId | id | IN | false |

## deleteFixedAssetDepMethod

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:234`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: FixedAssetDepMethod; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| depreciationCustomMethodId | id | IN | false |
| fixedAssetId | id | IN | false |

## deleteFixedAssetGeoPoint

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:314`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: FixedAssetGeoPoint; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetId | id | IN | false |
| fromDate | date-time | IN | false |
| geoPointId | id | IN | false |

## deleteFixedAssetIdentType

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:393`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: FixedAssetIdentType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetIdentTypeId | id | IN | false |

## deleteFixedAssetMaint

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:142`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: FixedAssetMaint; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetId | id | IN | false |
| maintHistSeqId | id | IN | false |

## deleteFixedAssetMaintOrder

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:195`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: FixedAssetMaintOrder; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetId | id | IN | false |
| maintHistSeqId | id | IN | false |
| orderId | id | IN | false |
| orderItemSeqId | id | IN | false |

## deleteFixedAssetMeter

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:179`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: FixedAssetMeter; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetId | id | IN | false |
| productMeterTypeId | id | IN | false |
| readingDate | date-time | IN | false |

## deleteFixedAssetProductType

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:409`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: FixedAssetProductType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetProductTypeId | id | IN | false |

## deleteFixedAssetRegistration

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:117`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: FixedAssetRegistration; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetId | id | IN | false |
| fromDate | date-time | IN | false |

## deleteFixedAssetStdCostType

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:425`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: FixedAssetStdCostType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetStdCostTypeId | id | IN | false |

## deleteFixedAssetType

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:441`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: FixedAssetType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetTypeId | id | IN | false |

## deleteFixedAssetTypeAttr

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:457`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: FixedAssetTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | false |
| fixedAssetTypeId | id | IN | false |

## deleteFixedAssetTypeGlAccount

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:294`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: FixedAssetTypeGlAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetId | id | IN | false |
| fixedAssetTypeId | id | IN | false |
| organizationPartyId | id | IN | false |

## deleteGlAccount

- Definition: `applications/accounting/servicedef/services_ledger.xml:46`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: GlAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: basicGeneralLedgerPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glAccountId | id | IN | false |

## deleteGlAccountCategoryMember

- Definition: `applications/accounting/servicedef/services_ledger.xml:619`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: GlAccountCategoryMember; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| glAccountCategoryId | id | IN | false |
| glAccountId | id | IN | false |

## deleteGlAccountCategoryType

- Definition: `applications/accounting/servicedef/services_ledger.xml:748`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: GlAccountCategoryType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glAccountCategoryTypeId | id | IN | false |

## deleteGlAccountClass

- Definition: `applications/accounting/servicedef/services_ledger.xml:764`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: GlAccountClass; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glAccountClassId | id | IN | false |

## deleteGlAccountGroup

- Definition: `applications/accounting/servicedef/services_ledger.xml:780`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: GlAccountGroup; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glAccountGroupId | id | IN | false |

## deleteGlAccountGroupMember

- Definition: `applications/accounting/servicedef/services_ledger.xml:796`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: GlAccountGroupMember; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glAccountGroupTypeId | id | IN | false |
| glAccountId | id | IN | false |

## deleteGlAccountGroupType

- Definition: `applications/accounting/servicedef/services_ledger.xml:812`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: GlAccountGroupType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glAccountGroupTypeId | id | IN | false |

## deleteGlAccountOrganization

- Definition: `applications/accounting/servicedef/services_ledger.xml:64`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: GlAccountOrganization; auth: true
- Use transaction: —; timeout: —
- Permission service: basicGeneralLedgerPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glAccountId | id | IN | false |
| organizationPartyId | id | IN | false |

## deleteGlAccountType

- Definition: `applications/accounting/servicedef/services_ledger.xml:840`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: GlAccountType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glAccountTypeId | id | IN | false |

## deleteGlFiscalType

- Definition: `applications/accounting/servicedef/services_ledger.xml:872`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: GlFiscalType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glFiscalTypeId | id | IN | false |

## deleteGlJournal

- Definition: `applications/accounting/servicedef/services_ledger.xml:118`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: GlJournal; auth: true
- Use transaction: —; timeout: —
- Permission service: basicGeneralLedgerPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glJournalId | id | IN | false |

## deleteGlReconciliation

- Definition: `applications/accounting/servicedef/services_ledger.xml:145`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: GlReconciliation; auth: true
- Use transaction: —; timeout: —
- Permission service: basicGeneralLedgerPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glReconciliationId | id | IN | false |

## deleteGlReconciliationEntry

- Definition: `applications/accounting/servicedef/services_ledger.xml:172`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: GlReconciliationEntry; auth: true
- Use transaction: —; timeout: —
- Permission service: basicGeneralLedgerPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransEntrySeqId | id | IN | false |
| acctgTransId | id | IN | false |
| glReconciliationId | id | IN | false |

## deleteGlResourceType

- Definition: `applications/accounting/servicedef/services_ledger.xml:888`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: GlResourceType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glResourceTypeId | id | IN | false |

## deleteGlXbrlClass

- Definition: `applications/accounting/servicedef/services_ledger.xml:904`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: GlXbrlClass; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glXbrlClassId | id | IN | false |

## deleteInvoiceAttribute

- Definition: `applications/accounting/servicedef/services_invoice.xml:514`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: InvoiceAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| invoiceId | id | IN | — |

## deleteInvoiceContactMech

- Definition: `applications/accounting/servicedef/services_invoice.xml:307`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: InvoiceContactMech; auth: —
- Use transaction: —; timeout: —
- Permission service: acctgInvoicePermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | id | IN | — |
| contactMechPurposeTypeId | id | IN | — |
| invoiceId | id | IN | — |

## deleteInvoiceContentType

- Definition: `applications/accounting/servicedef/services_invoice.xml:565`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: InvoiceContentType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceContentTypeId | id | IN | — |

## deleteInvoiceItemAssoc

- Definition: `applications/accounting/servicedef/services_invoice.xml:372`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: InvoiceItemAssoc; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | — |
| invoiceIdFrom | id | IN | — |
| invoiceIdTo | id | IN | — |
| invoiceItemAssocTypeId | id | IN | — |
| invoiceItemSeqIdFrom | id | IN | — |
| invoiceItemSeqIdTo | id | IN | — |

## deleteInvoiceItemAssocType

- Definition: `applications/accounting/servicedef/services_invoice.xml:530`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: InvoiceItemAssocType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceItemAssocTypeId | id | IN | — |

## deleteInvoiceItemAttribute

- Definition: `applications/accounting/servicedef/services_invoice.xml:469`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: InvoiceItemAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| invoiceId | id | IN | — |
| invoiceItemSeqId | id | IN | — |

## deleteInvoiceItemTypeAttr

- Definition: `applications/accounting/servicedef/services_invoice.xml:581`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: InvoiceItemTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| invoiceItemTypeId | id | IN | — |

## deleteInvoiceItemTypeMap

- Definition: `applications/accounting/servicedef/services_invoice.xml:596`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: InvoiceItemTypeMap; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceItemMapKey | id | IN | — |
| invoiceTypeId | id | IN | — |

## deleteInvoiceNote

- Definition: `applications/accounting/servicedef/services_invoice.xml:550`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: InvoiceNote; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | id | IN | — |
| noteId | id | IN | — |

## deleteInvoiceTerm

- Definition: `applications/accounting/servicedef/services_invoice.xml:176`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: InvoiceTerm; auth: —
- Use transaction: —; timeout: —
- Permission service: acctgInvoicePermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceTermId | id | IN | — |

## deleteInvoiceTermAttribute

- Definition: `applications/accounting/servicedef/services_invoice.xml:483`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: InvoiceTermAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| invoiceTermId | id | IN | — |

## deleteInvoiceType

- Definition: `applications/accounting/servicedef/services_invoice.xml:611`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: InvoiceType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceTypeId | id | IN | — |

## deleteInvoiceTypeAttr

- Definition: `applications/accounting/servicedef/services_invoice.xml:498`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: InvoiceTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| invoiceTypeId | id | IN | — |

## deletePartyFixedAssetAssignment

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:215`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PartyFixedAssetAssignment; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetId | id | IN | false |
| fromDate | date-time | IN | false |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |

## deletePartyGlAccount

- Definition: `applications/accounting/servicedef/services_ledger.xml:533`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PartyGlAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glAccountTypeId | id | IN | false |
| organizationPartyId | id | IN | false |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |

## deletePartyRate

- Definition: `applications/accounting/servicedef/services_rate.xml:124`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/rate/RateServices.groovy; invoke: deletePartyRate
- Default entity: PartyRate; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| partyId | id | IN | false |
| rateAmountFromDate | Timestamp | IN | true |
| rateTypeId | id | IN | false |

## deletePartyTaxAuthInfo

- Definition: `applications/accounting/servicedef/services_tax.xml:182`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PartyTaxAuthInfo; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| partyId | id | IN | false |
| taxAuthGeoId | id | IN | false |
| taxAuthPartyId | id | IN | false |

## deletePaymentAttribute

- Definition: `applications/accounting/servicedef/services_payment.xml:325`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PaymentAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| paymentId | id | IN | — |

## deletePaymentBudgetAllocation

- Definition: `applications/accounting/servicedef/services_payment.xml:340`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PaymentBudgetAllocation; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetId | id | IN | — |
| budgetItemSeqId | id | IN | — |
| paymentId | id | IN | — |

## deletePaymentContentType

- Definition: `applications/accounting/servicedef/services_payment.xml:355`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PaymentContentType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| paymentContentTypeId | id | IN | — |

## deletePaymentGatewayConfig

- Definition: `applications/accounting/servicedef/services_paymentgateway.xml:38`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PaymentGatewayConfig; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| paymentGatewayConfigId | id | IN | false |

## deletePaymentGroup

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:850`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PaymentGroup; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| paymentGroupId | id | IN | false |

## deletePaymentGroupMember

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:868`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PaymentGroupMember; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| paymentGroupId | id | IN | false |
| paymentId | id | IN | false |

## deletePaymentGroupType

- Definition: `applications/accounting/servicedef/services_payment.xml:370`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PaymentGroupType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| paymentGroupTypeId | id | IN | — |

## deletePaymentMethod

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:28`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentMethodServices; invoke: deletePaymentMethod
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyId | String | IN | true |
| paymentMethodId | String | IN | false |

## deletePaymentMethodType

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:832`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PaymentMethodType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| paymentMethodTypeId | id | IN | — |

## deletePaymentType

- Definition: `applications/accounting/servicedef/services_payment.xml:385`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PaymentType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| paymentTypeId | id | IN | — |

## deletePaymentTypeAttr

- Definition: `applications/accounting/servicedef/services_payment.xml:400`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PaymentTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| paymentTypeId | id | IN | — |

## deleteProductAverageCost

- Definition: `applications/accounting/servicedef/services_cost.xml:85`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ProductAverageCost; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgCostPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| facilityId | id | IN | false |
| fromDate | date-time | IN | false |
| organizationPartyId | id | IN | false |
| productAverageCostTypeId | id | IN | false |
| productId | id | IN | false |

## deleteProductAverageCostType

- Definition: `applications/accounting/servicedef/services_ledger.xml:936`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: ProductAverageCostType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| productAverageCostTypeId | id | IN | false |

## deleteRateAmount

- Definition: `applications/accounting/servicedef/services_rate.xml:51`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/rate/RateServices.groovy; invoke: deleteRateAmount
- Default entity: RateAmount; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emplPositionTypeId | id | IN | true |
| fromDate | date-time | IN | false |
| partyId | id | IN | true |
| periodTypeId | id | IN | true |
| rateCurrencyUomId | id | IN | true |
| rateTypeId | id | IN | false |
| workEffortId | id | IN | true |

## deleteRateType

- Definition: `applications/accounting/servicedef/services_rate.xml:150`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: RateType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| rateTypeId | id | IN | false |

## deleteSettlementTerm

- Definition: `applications/accounting/servicedef/services_ledger.xml:920`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: SettlementTerm; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| settlementTermId | id | IN | false |

## deleteTaxAuthority

- Definition: `applications/accounting/servicedef/services_tax.xml:83`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: TaxAuthority; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| taxAuthGeoId | id | IN | false |
| taxAuthPartyId | id | IN | false |

## deleteTaxAuthorityAssoc

- Definition: `applications/accounting/servicedef/services_tax.xml:103`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: TaxAuthorityAssoc; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| taxAuthGeoId | id | IN | false |
| taxAuthPartyId | id | IN | false |
| toTaxAuthGeoId | id | IN | false |
| toTaxAuthPartyId | id | IN | false |

## deleteTaxAuthorityAssocType

- Definition: `applications/accounting/servicedef/services_tax.xml:208`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: TaxAuthorityAssocType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| taxAuthorityAssocTypeId | id | IN | false |

## deleteTaxAuthorityCategory

- Definition: `applications/accounting/servicedef/services_tax.xml:122`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: TaxAuthorityCategory; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| productCategoryId | id | IN | — |
| taxAuthGeoId | id | IN | — |
| taxAuthPartyId | id | IN | — |

## deleteTaxAuthorityGlAccount

- Definition: `applications/accounting/servicedef/services_tax.xml:141`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: TaxAuthorityGlAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| organizationPartyId | id | IN | false |
| taxAuthGeoId | id | IN | false |
| taxAuthPartyId | id | IN | false |

## deleteTaxAuthorityRateProduct

- Definition: `applications/accounting/servicedef/services_tax.xml:160`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: TaxAuthorityRateProduct; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| taxAuthorityRateSeqId | id | IN | false |

## deleteTaxAuthorityRateType

- Definition: `applications/accounting/servicedef/services_tax.xml:224`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: TaxAuthorityRateType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| taxAuthorityRateTypeId | id | IN | false |

## deleteVarianceReasonGlAccount

- Definition: `applications/accounting/servicedef/services_ledger.xml:404`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: VarianceReasonGlAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| organizationPartyId | id | IN | false |
| varianceReasonId | id | IN | false |

## depositWithdrawPayments

- Definition: `applications/accounting/servicedef/services_finaccount.xml:458`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountServices.xml; invoke: depositWithdrawPayments
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgFinAcctPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountId | String | IN | false |
| finAccountTransId | String | OUT | true |
| groupInOneTransaction | String | IN | true |
| paymentGroupId | String | OUT | true |
| paymentGroupName | String | IN | true |
| paymentGroupTypeId | String | IN | true |
| paymentIds | List | IN | false |

## doubleDecliningBalanceDepreciation

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:264`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/fixedasset/FixedAssetServices.groovy; invoke: doubleDecliningBalanceDepreciation
- Default entity: FixedAsset; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "fixedAssetDepCalcInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetId | id | IN | false |

## eftProcessInterface

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:620`
- Engine: interface; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "paymentProcessInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| eftAccount | org.apache.ofbiz.entity.GenericValue | IN | false |

## expireAgreement

- Definition: `applications/accounting/servicedef/services_agreement.xml:41`
- Engine: entity-auto; location: —; invoke: expire
- Default entity: Agreement; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |

## expireFinAccountAuth

- Definition: `applications/accounting/servicedef/services_finaccount.xml:120`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountServices.xml; invoke: expireFinAccountAuth
- Default entity: FinAccountAuth; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| expireDateTime | Timestamp | IN | true |
| finAccountAuthId | String | IN | false |

## expireGlAccountRole

- Definition: `applications/accounting/servicedef/services_ledger.xml:823`
- Engine: entity-auto; location: —; invoke: expire
- Default entity: GlAccountRole; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| glAccountId | id | IN | false |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |

## expireGlBudgetXref

- Definition: `applications/accounting/servicedef/services_ledger.xml:856`
- Engine: entity-auto; location: —; invoke: expire
- Default entity: GlBudgetXref; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetItemTypeId | id | IN | false |
| fromDate | date-time | IN | false |
| glAccountId | id | IN | false |

## expirePartyPrefDocTypeTpl

- Definition: `applications/accounting/servicedef/services_admin.xml:67`
- Engine: entity-auto; location: —; invoke: expire
- Default entity: PartyPrefDocTypeTpl; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgPrefPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyPrefDocTypeTplId | id | IN | false |

## expirePartyRate

- Definition: `applications/accounting/servicedef/services_rate.xml:131`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/rate/RateServices.groovy; invoke: expirePartyRate
- Default entity: PartyRate; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| partyId | id | IN | false |
| rateAmountFromDate | Timestamp | IN | true |
| rateTypeId | id | IN | false |

## expirePaymentAssociationsOnFinAccountTransCancel

- Definition: `applications/accounting/servicedef/services_finaccount.xml:470`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountServices.xml; invoke: expirePaymentAssociationsOnFinAccountTransCancel
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountTransId | String | IN | true |

## expirePaymentGroupMember

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:872`
- Engine: simple; location: component://accounting/minilang/payment/PaymentMethodServices.xml; invoke: expirePaymentGroupMember
- Default entity: PaymentGroupMember; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| paymentGroupId | id | IN | false |
| paymentId | id | IN | false |

## expireRateAmount

- Definition: `applications/accounting/servicedef/services_rate.xml:39`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/rate/RateServices.groovy; invoke: expireRateAmount
- Default entity: RateAmount; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emplPositionTypeId | id | IN | true |
| fromDate | date-time | IN | false |
| partyId | id | IN | true |
| periodTypeId | id | IN | true |
| rateCurrencyUomId | id | IN | true |
| rateTypeId | id | IN | false |
| workEffortId | id | IN | true |

## filterRateAmountList

- Definition: `applications/accounting/servicedef/services_rate.xml:101`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/rate/RateServices.groovy; invoke: filterRateAmountList
- Default entity: RateAmount; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emplPositionTypeId | id | IN | true |
| filteredRatesList | List | OUT | true |
| fromDate | date-time | IN | true |
| partyId | id | IN | true |
| periodTypeId | id | IN | true |
| rateAmount | currency-amount | IN | true |
| rateCurrencyUomId | id | IN | true |
| rateTypeId | id | IN | true |
| ratesList | List | IN | true |
| thruDate | date-time | IN | true |
| workEffortId | id | IN | true |

## finAccountDeposit

- Definition: `applications/accounting/servicedef/services_finaccount.xml:155`
- Engine: java; location: org.apache.ofbiz.accounting.finaccount.FinAccountPaymentServices; invoke: finAccountDeposit
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | BigDecimal | INOUT | false |
| balance | BigDecimal | OUT | false |
| currency | String | IN | true |
| finAccountId | String | IN | false |
| isRefund | Boolean | IN | true |
| orderId | String | IN | true |
| orderItemSeqId | String | IN | true |
| partyId | String | IN | true |
| previousBalance | BigDecimal | OUT | true |
| processResult | Boolean | OUT | false |
| productStoreId | String | IN | true |
| reasonEnumId | String | IN | true |
| referenceNum | String | OUT | false |

## finAccountRefund

- Definition: `applications/accounting/servicedef/services_finaccount.xml:190`
- Engine: java; location: org.apache.ofbiz.accounting.finaccount.FinAccountServices; invoke: refundFinAccount
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountId | String | IN | false |

## finAccountReplenish

- Definition: `applications/accounting/servicedef/services_finaccount.xml:195`
- Engine: java; location: org.apache.ofbiz.accounting.finaccount.FinAccountPaymentServices; invoke: finAccountReplenish
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountId | String | IN | false |
| productStoreId | String | IN | true |

## finAccountWithdraw

- Definition: `applications/accounting/servicedef/services_finaccount.xml:172`
- Engine: java; location: org.apache.ofbiz.accounting.finaccount.FinAccountPaymentServices; invoke: finAccountWithdraw
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | BigDecimal | INOUT | false |
| balance | BigDecimal | OUT | false |
| currency | String | IN | true |
| finAccountId | String | IN | false |
| orderId | String | IN | true |
| orderItemSeqId | String | IN | true |
| partyId | String | IN | true |
| previousBalance | BigDecimal | OUT | true |
| processResult | Boolean | OUT | false |
| productStoreId | String | IN | true |
| reasonEnumId | String | IN | true |
| referenceNum | String | OUT | false |
| requireBalance | Boolean | IN | true |

## findCustomTimePeriods

- Definition: `applications/accounting/servicedef/services_ledger.xml:539`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/period/GPeriodServices.groovy; invoke: findCustomTimePeriods
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| customTimePeriodList | List | OUT | true |
| excludeNoOrganizationPeriods | String | IN | true |
| findDate | Timestamp | IN | — |
| onlyIncludePeriodTypeIdList | List | IN | true |
| organizationPartyId | String | IN | true |

## findLastClosedDate

- Definition: `applications/accounting/servicedef/services_ledger.xml:548`
- Engine: java; location: org.apache.ofbiz.accounting.period.PeriodServices; invoke: findLastClosedDate
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| findDate | Timestamp | IN | true |
| lastClosedDate | Timestamp | OUT | true |
| lastClosedTimePeriod | org.apache.ofbiz.entity.GenericValue | OUT | true |
| organizationPartyId | String | IN | true |
| periodTypeId | String | IN | true |

## fixedAssetDepCalcInterface

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:245`
- Engine: interface; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| assetAcquiredYear | Integer | IN | false |
| assetDepreciationInfoList | List | OUT | false |
| assetDepreciationTillDate | List | OUT | false |
| assetNBVAfterDepreciation | List | OUT | false |
| expEndOfLifeYear | Integer | IN | false |
| nextDepreciationAmount | BigDecimal | OUT | false |
| plannedPastDepreciationTotal | BigDecimal | OUT | false |
| purchaseCost | BigDecimal | IN | false |
| salvageValue | BigDecimal | IN | false |
| usageYears | Integer | IN | false |

## fixedAssetPermissionCheck

- Definition: `applications/accounting/servicedef/services_perm.xml:92`
- Engine: simple; location: component://accounting/minilang/permissions/PermissionServices.xml; invoke: basePermissionCheck
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "permissionInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## getAcctgTransEntriesAndTransTotal

- Definition: `applications/accounting/servicedef/services_ledger.xml:567`
- Engine: simple; location: component://accounting/minilang/ledger/AcctgTransServices.xml; invoke: getAcctgTransEntriesAndTransTotal
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransAndEntries | List | OUT | — |
| creditTotal | BigDecimal | OUT | — |
| customTimePeriodEndDate | Timestamp | IN | — |
| customTimePeriodStartDate | Timestamp | IN | — |
| debitCreditDifference | BigDecimal | OUT | — |
| debitTotal | BigDecimal | OUT | — |
| glAccountId | String | IN | — |
| isPosted | String | IN | true |
| organizationPartyId | String | IN | — |

## getAssociatedAcctgTransEntriesWithFinAccountTrans

- Definition: `applications/accounting/servicedef/services_finaccount.xml:522`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountServices.xml; invoke: getAssociatedAcctgTransEntriesWithFinAccountTrans
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransAndEntries | List | OUT | true |
| finAccountTransId | String | IN | false |

## getCommissionForProduct

- Definition: `applications/accounting/servicedef/services_agreement.xml:277`
- Engine: java; location: org.apache.ofbiz.accounting.agreement.AgreementServices; invoke: getCommissionForProduct
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgCommissionPermissionCheck; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | BigDecimal | IN | false |
| commissions | List | OUT | false |
| invoiceId | String | IN | true |
| invoiceItemSeqId | String | IN | true |
| invoiceItemTypeId | String | IN | false |
| productId | String | IN | false |
| quantity | BigDecimal | IN | true |

## getFXConversion

- Definition: `applications/accounting/servicedef/services_admin.xml:144`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/admin/AcctgAdminServices.groovy; invoke: getFXConversion
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| asOfTimestamp | Timestamp | IN | true |
| conversionRate | BigDecimal | OUT | false |
| uomId | String | IN | false |
| uomIdTo | String | IN | false |

## getFinAccountTransListAndTotals

- Definition: `applications/accounting/servicedef/services_finaccount.xml:215`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountServices.xml; invoke: getFinAccountTransListAndTotals
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| approvedGrandTotal | BigDecimal | OUT | true |
| createdApprovedGrandTotal | BigDecimal | OUT | true |
| createdGrandTotal | BigDecimal | OUT | true |
| finAccountId | String | IN | false |
| finAccountTransList | List | OUT | true |
| finAccountTransTypeId | String | IN | true |
| fromEntryDate | Timestamp | IN | true |
| fromTransactionDate | Timestamp | IN | true |
| glReconciliationApprovedGrandTotal | BigDecimal | OUT | true |
| glReconciliationId | String | IN | true |
| grandTotal | BigDecimal | OUT | true |
| openingBalance | BigDecimal | IN | true |
| searchedNumberOfRecords | Integer | OUT | true |
| statusId | String | IN | true |
| thruEntryDate | Timestamp | IN | true |
| thruTransactionDate | Timestamp | IN | true |
| totalApprovedTransactions | Long | OUT | true |
| totalCreatedApprovedTransactions | Long | OUT | true |
| totalCreatedTransactions | Long | OUT | true |

## getFinAccountTransRunningTotalAndBalances

- Definition: `applications/accounting/servicedef/services_finaccount.xml:238`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountServices.xml; invoke: getFinAccountTransRunningTotalAndBalances
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| endingBalance | String | OUT | true |
| finAccountTransId | String | IN | false |
| finAccountTransRunningTotal | String | OUT | true |
| numberOfTransactions | Long | INOUT | true |
| openingBalance | BigDecimal | IN | true |
| organizationPartyId | String | IN | false |
| reconciledBalance | BigDecimal | IN | true |
| runningTotal | BigDecimal | INOUT | true |

## getGlAccountFromAccountType

- Definition: `applications/accounting/servicedef/services_ledger.xml:325`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: getGlAccountFromAccountType
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransTypeId | String | IN | true |
| debitCreditFlag | String | IN | true |
| fixedAssetId | String | IN | true |
| glAccountId | String | OUT | true |
| glAccountTypeId | String | IN | true |
| invoiceId | String | IN | true |
| organizationPartyId | String | IN | false |
| partyId | String | IN | true |
| paymentId | String | IN | true |
| productId | String | IN | true |
| roleTypeId | String | IN | true |

## getGlAcctgAndAmountPercentage

- Definition: `applications/accounting/servicedef/services_ledger.xml:639`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: getGlAcctgAndAmountPercentage
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glAccountCategories | List | OUT | true |
| glAcctgAndAmountPercentageList | List | OUT | true |
| organizationPartyId | String | IN | — |

## getInventoryItemOwner

- Definition: `applications/accounting/servicedef/services_ledger.xml:341`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: getInventoryItemOwner
- Default entity: InventoryItem; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| inventoryItemId | id | IN | false |
| ownerPartyId | String | OUT | false |

## getInventoryValuationList

- Definition: `applications/accounting/servicedef/services_ledger.xml:647`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: getInventoryValuationList
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| cogsMethodId | String | IN | true |
| facilityId | String | IN | true |
| fromDate | Timestamp | IN | true |
| inventoryValuationList | List | OUT | true |
| productCategoryId | String | IN | true |
| productId | String | IN | true |
| thruDate | Timestamp | IN | true |

## getInvoice

- Definition: `applications/accounting/servicedef/services_invoice.xml:71`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy; invoke: getInvoice
- Default entity: Invoice; auth: —
- Use transaction: —; timeout: —
- Permission service: acctgInvoicePermissionCheck; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoice | org.apache.ofbiz.entity.GenericValue | OUT | — |
| invoiceId | id | IN | — |
| invoiceItems | List | OUT | — |

## getInvoicePaymentInfoList

- Definition: `applications/accounting/servicedef/services_payment.xml:129`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy; invoke: getInvoicePaymentInfoList
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoice | org.apache.ofbiz.entity.GenericValue | IN | true |
| invoiceId | String | IN | true |
| invoicePaymentInfoList | List | OUT | false |

## getInvoicePaymentInfoListByDueDateOffset

- Definition: `applications/accounting/servicedef/services_payment.xml:136`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy; invoke: getInvoicePaymentInfoListByDueDateOffset
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| daysOffset | Long | IN | false |
| invoicePaymentInfoList | List | OUT | true |
| invoiceTypeId | String | IN | false |
| partyId | String | IN | true |
| partyIdFrom | String | IN | true |

## getInvoiceRunningTotal

- Definition: `applications/accounting/servicedef/services_invoice.xml:338`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy; invoke: getInvoiceRunningTotal
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceIds | List | IN | — |
| invoiceRunningTotal | String | OUT | — |
| organizationPartyId | String | IN | true |

## getInvoicesFilterByAssocType

- Definition: `applications/accounting/servicedef/services_invoice.xml:352`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy; invoke: getInvoicesFilterByAssocType
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| filteredInvoiceList | List | OUT | true |
| invoiceItemAssocTypeId | String | IN | — |
| invoiceList | List | IN | — |

## getNextInvoiceId

- Definition: `applications/accounting/servicedef/services_invoice.xml:28`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy; invoke: getNextInvoiceId
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "createInvoice"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | String | OUT | — |
| partyId | String | IN | — |

## getPartyAccountingPreferences

- Definition: `applications/accounting/servicedef/services_admin.xml:73`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/admin/AcctgAdminServices.groovy; invoke: getPartyAccountingPreferences
- Default entity: PartyAcctgPreference; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgPrefPermissionCheck; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| organizationPartyId | String | IN | false |
| partyAccountingPreference | org.apache.ofbiz.entity.GenericValue | OUT | true |

## getPaymentGroupReconciliationId

- Definition: `applications/accounting/servicedef/services_payment.xml:216`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy; invoke: getPaymentGroupReconciliationId
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glReconciliationId | String | OUT | true |
| paymentGroupId | String | IN | false |

## getPaymentRunningTotal

- Definition: `applications/accounting/servicedef/services_payment.xml:154`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy; invoke: getPaymentRunningTotal
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| organizationPartyId | String | IN | true |
| paymentIds | List | IN | false |
| paymentRunningTotal | String | OUT | false |

## getPayments

- Definition: `applications/accounting/servicedef/services_payment.xml:209`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy; invoke: getPayments
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountTransId | String | IN | true |
| paymentGroupId | String | IN | true |
| payments | List | OUT | true |

## getPreviousTimePeriod

- Definition: `applications/accounting/servicedef/services_ledger.xml:561`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/period/GPeriodServices.groovy; invoke: getPreviousTimePeriod
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| customTimePeriodId | String | IN | — |
| previousTimePeriod | Map | OUT | true |

## getProductAverageCost

- Definition: `applications/accounting/servicedef/services_cost.xml:101`
- Engine: groovy; location: component://product/src/main/groovy/org/apache/ofbiz/product/product/cost/CostServices.groovy; invoke: getProductAverageCost
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| inventoryItem | org.apache.ofbiz.entity.GenericValue | IN | true |
| unitCost | BigDecimal | OUT | false |

## getRateAmount

- Definition: `applications/accounting/servicedef/services_rate.xml:59`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/rate/RateServices.groovy; invoke: getRateAmount
- Default entity: RateAmount; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emplPositionTypeId | id | IN | true |
| fromDate | Timestamp | OUT | true |
| level | String | OUT | true |
| partyId | id | IN | true |
| periodTypeId | String | INOUT | true |
| rateAmount | BigDecimal | OUT | true |
| rateCurrencyUomId | String | INOUT | true |
| rateTypeId | id | IN | false |
| workEffortId | id | IN | true |

## getRatesAmountsFromEmplPositionTypeId

- Definition: `applications/accounting/servicedef/services_rate.xml:91`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/rate/RateServices.groovy; invoke: getRatesAmountsFromEmplPositionTypeId
- Default entity: RateAmount; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emplPositionTypeId | id | IN | false |
| fromDate | date-time | IN | true |
| partyId | id | IN | true |
| periodTypeId | String | INOUT | true |
| rateCurrencyUomId | String | INOUT | true |
| rateTypeId | id | IN | true |
| ratesList | List | OUT | true |
| workEffortId | id | IN | true |

## getRatesAmountsFromPartyId

- Definition: `applications/accounting/servicedef/services_rate.xml:81`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/rate/RateServices.groovy; invoke: getRatesAmountsFromPartyId
- Default entity: RateAmount; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emplPositionTypeId | id | IN | true |
| fromDate | date-time | IN | true |
| partyId | id | IN | false |
| periodTypeId | String | INOUT | true |
| rateCurrencyUomId | String | INOUT | true |
| rateTypeId | id | IN | true |
| ratesList | List | OUT | true |
| workEffortId | id | IN | true |

## getRatesAmountsFromWorkEffortId

- Definition: `applications/accounting/servicedef/services_rate.xml:71`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/rate/RateServices.groovy; invoke: getRatesAmountsFromWorkEffortId
- Default entity: RateAmount; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emplPositionTypeId | id | IN | true |
| fromDate | date-time | IN | true |
| partyId | id | IN | true |
| periodTypeId | String | INOUT | true |
| rateCurrencyUomId | String | INOUT | true |
| rateTypeId | id | IN | true |
| ratesList | List | OUT | true |
| workEffortId | id | IN | false |

## getReconciliationClosingBalance

- Definition: `applications/accounting/servicedef/services_finaccount.xml:275`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountServices.xml; invoke: getReconciliationClosingBalance
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| closingBalance | BigDecimal | OUT | false |
| glReconciliationId | String | IN | false |

## getTransactionTotalByGlReconcileId

- Definition: `applications/accounting/servicedef/services_finaccount.xml:486`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountServices.xml; invoke: getTransactionTotalByGlReconcileId
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glReconciliationId | String | IN | false |
| grandTotal | BigDecimal | OUT | true |

## giftCardProcessInterface

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:662`
- Engine: interface; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "paymentProcessInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| giftCard | org.apache.ofbiz.entity.GenericValue | IN | false |

## importInvoice

- Definition: `applications/accounting/servicedef/services_invoice.xml:452`
- Engine: java; location: org.apache.ofbiz.accounting.invoice.InvoiceServices; invoke: importInvoice
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgInvoicePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| organizationPartyId | String | INOUT | — |
| uploadedFile | java.nio.ByteBuffer | IN | true |

## interfaceAcctgTrans

- Definition: `applications/accounting/servicedef/services_ledger.xml:184`
- Engine: interface; location: —; invoke: —
- Default entity: AcctgTrans; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransTypeId | id | IN | false |
| createdByUserLogin | id-vlong | IN | true |
| createdDate | date-time | IN | true |
| description | description | IN | true |
| finAccountTransId | id | IN | true |
| fixedAssetId | id | IN | true |
| glFiscalTypeId | id | IN | false |
| glJournalId | id | IN | true |
| groupStatusId | id | IN | true |
| inventoryItemId | id | IN | true |
| invoiceId | id | IN | true |
| isPosted | indicator | IN | true |
| lastModifiedByUserLogin | id-vlong | IN | true |
| lastModifiedDate | date-time | IN | true |
| partyId | id | IN | true |
| paymentId | id | IN | true |
| physicalInventoryId | id | IN | true |
| postedDate | date-time | IN | true |
| receiptId | id | IN | true |
| roleTypeId | id | IN | true |
| scheduledPostingDate | date-time | IN | true |
| shipmentId | id | IN | true |
| theirAcctgTransId | id-long | IN | true |
| transactionDate | date-time | IN | false |
| voucherDate | date-time | IN | true |
| voucherRef | short-varchar | IN | true |
| workEffortId | id | IN | true |

## interfaceAcctgTransEntry

- Definition: `applications/accounting/servicedef/services_ledger.xml:220`
- Engine: interface; location: —; invoke: —
- Default entity: AcctgTransEntry; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransEntryTypeId | id | IN | true |
| amount | currency-amount | IN | true |
| currencyUomId | id | IN | true |
| debitCreditFlag | indicator | IN | false |
| description | description | IN | true |
| dueDate | date | IN | true |
| glAccountId | id | IN | true |
| glAccountTypeId | id | IN | true |
| groupId | id | IN | true |
| inventoryItemId | id | IN | true |
| isSummary | indicator | IN | true |
| organizationPartyId | id | IN | false |
| origAmount | currency-amount | IN | true |
| origCurrencyUomId | id | IN | true |
| partyId | id | IN | true |
| productId | id | IN | true |
| reconcileStatusId | id | IN | true |
| roleTypeId | id | IN | true |
| settlementTermId | id | IN | true |
| taxId | id | IN | true |
| theirPartyId | id | IN | true |
| theirProductId | id | IN | true |
| voucherRef | short-varchar | IN | true |

## invoiceSequenceEnforced

- Definition: `applications/accounting/servicedef/services_invoice.xml:36`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy; invoke: invoiceSequenceEnforced
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "getNextInvoiceId"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | Long | OUT | — |
| partyAcctgPreference | org.apache.ofbiz.entity.GenericValue | IN | — |

## invoiceSequenceRestart

- Definition: `applications/accounting/servicedef/services_invoice.xml:43`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy; invoke: invoiceSequenceRestart
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "getNextInvoiceId"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| partyAcctgPreference | org.apache.ofbiz.entity.GenericValue | IN | — |

## isGlReconciliationReconciled

- Definition: `applications/accounting/servicedef/services_finaccount.xml:508`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountServices.xml; invoke: isGlReconciliationReconciled
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glReconciliationId | String | IN | false |
| isReconciled | Boolean | OUT | false |

## isInvoiceInForeignCurrency

- Definition: `applications/accounting/servicedef/services_invoice.xml:446`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy; invoke: isInvoiceInForeignCurrency
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | String | IN | — |
| isForeign | Boolean | OUT | — |

## manualForcedCcAuthTransaction

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:234`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: processManualCcAuth
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | BigDecimal | IN | false |
| paymentMethodId | String | IN | false |
| productStoreId | String | IN | false |
| securityCode | String | IN | true |

## manualForcedCcTransaction

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:242`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: processManualCcTx
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| address1 | String | IN | false |
| address2 | String | IN | true |
| amount | BigDecimal | IN | false |
| cardNumber | String | IN | false |
| cardSecurityCode | String | IN | true |
| cardType | String | IN | false |
| city | String | IN | false |
| companyNameOnCard | String | IN | true |
| countryGeoId | String | IN | false |
| expMonth | String | IN | false |
| expYear | String | IN | false |
| firstNameOnCard | String | IN | false |
| infoString | String | IN | false |
| lastNameOnCard | String | IN | false |
| middleNameOnCard | String | IN | true |
| orderPaymentPreferenceId | String | IN | false |
| paymentMethodTypeId | String | IN | false |
| postalCode | String | IN | false |
| productStoreId | String | IN | false |
| referenceCode | String | IN | true |
| referenceNum | String | OUT | false |
| stateProvinceGeoId | String | IN | true |
| suffixOnCard | String | IN | true |
| titleOnCard | String | IN | true |
| tranRespMsgs | List | OUT | true |
| transactionType | String | IN | false |

## massChangeInvoiceStatus

- Definition: `applications/accounting/servicedef/services_invoice.xml:389`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy; invoke: massChangeInvoiceStatus
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceIds | List | IN | — |
| statusId | String | IN | — |

## massChangePaymentStatus

- Definition: `applications/accounting/servicedef/services_payment.xml:229`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy; invoke: massChangePaymentStatus
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| errorMessage | String | OUT | true |
| paymentIds | List | IN | false |
| statusId | String | IN | false |

## ofbFaAuthorize

- Definition: `applications/accounting/servicedef/services_finaccount.xml:320`
- Engine: java; location: org.apache.ofbiz.accounting.finaccount.FinAccountPaymentServices; invoke: finAccountPreAuth
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "paymentProcessInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountCode | String | IN | true |
| finAccountId | String | IN | true |
| finAccountPin | String | IN | true |

## ofbFaCapture

- Definition: `applications/accounting/servicedef/services_finaccount.xml:328`
- Engine: java; location: org.apache.ofbiz.accounting.finaccount.FinAccountPaymentServices; invoke: finAccountCapture
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "ccCaptureInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## ofbFaRefund

- Definition: `applications/accounting/servicedef/services_finaccount.xml:338`
- Engine: java; location: org.apache.ofbiz.accounting.finaccount.FinAccountPaymentServices; invoke: finAccountRefund
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "paymentRefundInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountId | String | IN | true |

## ofbFaRelease

- Definition: `applications/accounting/servicedef/services_finaccount.xml:333`
- Engine: java; location: org.apache.ofbiz.accounting.finaccount.FinAccountPaymentServices; invoke: finAccountReleaseAuth
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "paymentReleaseInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## ofbGcAuthorize

- Definition: `applications/accounting/servicedef/services_finaccount.xml:430`
- Engine: java; location: org.apache.ofbiz.accounting.payment.GiftCertificateServices; invoke: giftCertificateAuthorize
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "giftCardProcessInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## ofbGcProcessor

- Definition: `applications/accounting/servicedef/services_finaccount.xml:425`
- Engine: java; location: org.apache.ofbiz.accounting.payment.GiftCertificateServices; invoke: giftCertificateProcessor
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "giftCardProcessInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## ofbGcPurchase

- Definition: `applications/accounting/servicedef/services_finaccount.xml:447`
- Engine: java; location: org.apache.ofbiz.accounting.payment.GiftCertificateServices; invoke: giftCertificatePurchase
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "itemFulfillmentInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## ofbGcRefund

- Definition: `applications/accounting/servicedef/services_finaccount.xml:440`
- Engine: java; location: org.apache.ofbiz.accounting.payment.GiftCertificateServices; invoke: giftCertificateRefund
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "paymentRefundInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## ofbGcRelease

- Definition: `applications/accounting/servicedef/services_finaccount.xml:435`
- Engine: java; location: org.apache.ofbiz.accounting.payment.GiftCertificateServices; invoke: giftCertificateRelease
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "paymentReleaseInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## ofbGcReload

- Definition: `applications/accounting/servicedef/services_finaccount.xml:452`
- Engine: java; location: org.apache.ofbiz.accounting.payment.GiftCertificateServices; invoke: giftCertificateReload
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "itemFulfillmentInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## payPalCaptureInterface

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:632`
- Engine: interface; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| authTrans | org.apache.ofbiz.entity.GenericValue | IN | true |
| captureAltRefNum | String | OUT | true |
| captureAmount | BigDecimal | INOUT | false |
| captureCode | String | OUT | true |
| captureFlag | String | OUT | true |
| captureMessage | String | OUT | true |
| captureRefNum | String | OUT | false |
| captureResult | Boolean | OUT | true |
| currency | String | IN | true |
| internalRespMsgs | List | OUT | true |
| orderPaymentPreference | org.apache.ofbiz.entity.GenericValue | IN | false |
| paymentConfig | String | IN | true |
| paymentGatewayConfigId | String | IN | true |

## payPalDoExpressCheckoutInterface

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:656`
- Engine: interface; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| orderPaymentPreference | GenericValue | IN | false |

## payPalGetExpressCheckoutInterface

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:652`
- Engine: interface; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| cart | org.apache.ofbiz.order.shoppingcart.ShoppingCart | IN | false |

## payPalProcessInterface

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:627`
- Engine: interface; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "paymentProcessInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| payPalPaymentMethod | org.apache.ofbiz.entity.GenericValue | IN | false |

## payPalSetExpressCheckoutInterface

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:648`
- Engine: interface; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| cart | org.apache.ofbiz.order.shoppingcart.ShoppingCart | IN | false |

## paymentCreditInterface

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:551`
- Engine: interface; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| billToEmail | org.apache.ofbiz.entity.GenericValue | IN | true |
| billToParty | org.apache.ofbiz.entity.GenericValue | IN | false |
| billingAddress | org.apache.ofbiz.entity.GenericValue | IN | true |
| creditAltRefNum | String | OUT | true |
| creditAmount | BigDecimal | INOUT | false |
| creditCode | String | OUT | true |
| creditFlag | String | OUT | true |
| creditMessage | String | OUT | true |
| creditRefNum | String | OUT | false |
| creditResult | Boolean | OUT | false |
| currency | String | IN | true |
| internalRespMsgs | List | OUT | true |
| orderItems | List | IN | true |
| paymentConfig | String | IN | true |
| paymentGatewayConfigId | String | IN | true |
| referenceCode | String | IN | false |

## paymentProcessInterface

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:497`
- Engine: interface; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| authAltRefNum | String | OUT | true |
| authCode | String | OUT | true |
| authFlag | String | OUT | true |
| authMessage | String | OUT | true |
| authRefNum | String | OUT | false |
| authResult | Boolean | OUT | true |
| avsCode | String | OUT | true |
| billToEmail | org.apache.ofbiz.entity.GenericValue | IN | true |
| billToParty | org.apache.ofbiz.entity.GenericValue | IN | false |
| billingAddress | org.apache.ofbiz.entity.GenericValue | IN | true |
| captureAltRefNum | String | OUT | true |
| captureCode | String | OUT | true |
| captureFlag | String | OUT | true |
| captureMessage | String | OUT | true |
| captureRefNum | String | OUT | true |
| captureResult | Boolean | OUT | true |
| currency | String | IN | true |
| customerIpAddress | String | IN | true |
| customerRespMsgs | List | OUT | true |
| cvCode | String | OUT | true |
| internalRespMsgs | List | OUT | true |
| orderId | String | IN | false |
| orderItems | List | IN | false |
| orderPaymentPreference | org.apache.ofbiz.entity.GenericValue | IN | false |
| paymentConfig | String | IN | true |
| paymentGatewayConfigId | String | IN | true |
| processAmount | BigDecimal | INOUT | false |
| resultBadCardNumber | Boolean | OUT | true |
| resultBadExpire | Boolean | OUT | true |
| resultDeclined | Boolean | OUT | true |
| resultNsf | Boolean | OUT | true |
| scoreCode | String | OUT | true |
| shippingAddress | org.apache.ofbiz.entity.GenericValue | IN | true |

## paymentRefundInterface

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:571`
- Engine: interface; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| currency | String | IN | true |
| internalRespMsgs | List | OUT | true |
| orderPaymentPreference | org.apache.ofbiz.entity.GenericValue | IN | false |
| paymentConfig | String | IN | true |
| paymentGatewayConfigId | String | IN | true |
| refundAltRefNum | String | OUT | true |
| refundAmount | BigDecimal | INOUT | false |
| refundCode | String | OUT | true |
| refundFlag | String | OUT | true |
| refundMessage | String | OUT | true |
| refundRefNum | String | OUT | false |
| refundResult | Boolean | OUT | false |

## paymentReleaseInterface

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:534`
- Engine: interface; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| authTrans | org.apache.ofbiz.entity.GenericValue | IN | true |
| currency | String | IN | true |
| internalRespMsgs | List | OUT | true |
| orderPaymentPreference | org.apache.ofbiz.entity.GenericValue | IN | false |
| paymentConfig | String | IN | true |
| paymentGatewayConfigId | String | IN | true |
| releaseAltRefNum | String | OUT | true |
| releaseAmount | BigDecimal | INOUT | false |
| releaseCode | String | OUT | true |
| releaseFlag | String | OUT | true |
| releaseMessage | String | OUT | true |
| releaseRefNum | String | OUT | false |
| releaseResult | Boolean | OUT | true |

## postAcctgTrans

- Definition: `applications/accounting/servicedef/services_ledger.xml:278`
- Engine: simple; location: component://accounting/minilang/ledger/AcctgTransServices.xml; invoke: postAcctgTrans
- Default entity: AcctgTrans; auth: true
- Use transaction: —; timeout: 600
- Permission service: acctgTransactionPermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | id | IN | false |
| verifyOnly | String | IN | true |

## postFinAccountTransToGl

- Definition: `applications/accounting/servicedef/services_finaccount.xml:88`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountGlPostServices.xml; invoke: postFinAccountTransToGl
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountTransId | String | IN | false |
| glAccountId | String | IN | false |

## postGlJournal

- Definition: `applications/accounting/servicedef/services_ledger.xml:94`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: postGlJournal
- Default entity: GlJournal; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glJournalId | id | IN | false |

## prepareIncomeStatement

- Definition: `applications/accounting/servicedef/services_ledger.xml:313`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: prepareIncomeStatement
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | Timestamp | IN | false |
| glAccountTotalsMap | Map | OUT | true |
| glFiscalTypeId | String | IN | false |
| organizationPartyId | String | IN | false |
| thruDate | Timestamp | IN | false |
| totalNetIncome | BigDecimal | OUT | false |

## processAuthResult

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:387`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: processAuthResult
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| authAltRefNum | String | IN | true |
| authCode | String | IN | true |
| authFlag | String | IN | true |
| authMessage | String | IN | true |
| authRefNum | String | IN | false |
| authResult | Boolean | IN | false |
| avsCode | String | IN | true |
| currencyUomId | String | IN | true |
| cvCode | String | IN | true |
| internalRespMsgs | List | IN | true |
| orderPaymentPreference | org.apache.ofbiz.entity.GenericValue | IN | false |
| processAmount | BigDecimal | IN | false |
| scoreCode | String | IN | true |
| serviceTypeEnum | String | IN | true |

## processCaptureResult

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:406`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: processCaptureResult
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| captureAltRefNum | String | IN | true |
| captureAmount | BigDecimal | IN | false |
| captureCode | String | IN | true |
| captureFlag | String | IN | true |
| captureMessage | String | IN | true |
| captureRefNum | String | IN | false |
| captureResult | Boolean | IN | false |
| currencyUomId | String | IN | true |
| internalRespMsgs | List | IN | true |
| invoiceId | String | IN | true |
| orderPaymentPreference | org.apache.ofbiz.entity.GenericValue | IN | false |
| payToPartyId | String | IN | true |
| serviceTypeEnum | String | IN | true |

## processCaptureSplitPayment

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:363`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: processCaptureSplitPayment
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| orderPaymentPreference | org.apache.ofbiz.entity.GenericValue | IN | false |
| splitAmount | BigDecimal | IN | false |

## processCreditResult

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:439`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: processCreditResult
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| creditAltRefNum | String | IN | true |
| creditAmount | BigDecimal | IN | false |
| creditCode | String | IN | true |
| creditFlag | String | IN | true |
| creditMessage | String | IN | true |
| creditRefNum | String | IN | false |
| creditResult | Boolean | IN | false |
| currencyUomId | String | IN | true |
| internalRespMsgs | List | IN | true |
| orderPaymentPreference | org.apache.ofbiz.entity.GenericValue | IN | false |

## processPaymentServiceError

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:474`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: storePaymentErrorMessage
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| orderPaymentPreference | org.apache.ofbiz.entity.GenericValue | IN | false |
| paymentServiceTypeEnumId | String | IN | false |
| serviceResultMap | Map | IN | false |
| transCodeEnumId | String | IN | false |

## processRefundResult

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:454`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: processRefundResult
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| currencyUomId | String | IN | true |
| internalRespMsgs | List | IN | true |
| invoiceId | String | IN | true |
| orderPaymentPreference | org.apache.ofbiz.entity.GenericValue | IN | false |
| payFromPartyId | String | IN | true |
| payToPartyId | String | IN | true |
| paymentId | String | OUT | true |
| refundAltRefNum | String | IN | true |
| refundAmount | BigDecimal | INOUT | false |
| refundCode | String | IN | true |
| refundFlag | String | IN | true |
| refundMessage | String | IN | true |
| refundRefNum | String | IN | false |
| refundResult | Boolean | IN | false |
| serviceTypeEnum | String | IN | true |

## processReleaseResult

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:424`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: processReleaseResult
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| currencyUomId | String | IN | true |
| internalRespMsgs | List | IN | true |
| orderPaymentPreference | org.apache.ofbiz.entity.GenericValue | IN | false |
| releaseAltRefNum | String | IN | true |
| releaseAmount | BigDecimal | IN | false |
| releaseCode | String | IN | true |
| releaseFlag | String | IN | true |
| releaseMessage | String | IN | true |
| releaseRefNum | String | IN | false |
| releaseResult | Boolean | IN | false |

## quickCreateAcctgTransAndEntries

- Definition: `applications/accounting/servicedef/services_ledger.xml:71`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: quickCreateAcctgTransAndEntries
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | String | OUT | false |
| amount | — | — | false |
| creditGlAccountId | String | IN | false |
| debitGlAccountId | String | IN | false |

## quickSendPayment

- Definition: `applications/accounting/servicedef/services_payment.xml:62`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy; invoke: quickSendPayment
- Default entity: Payment; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| actualCurrencyAmount | currency-amount | IN | true |
| actualCurrencyUomId | id | IN | true |
| amount | currency-amount | IN | true |
| comments | comment | IN | true |
| currencyUomId | id | IN | true |
| effectiveDate | date-time | IN | true |
| finAccountTransId | id | IN | true |
| overrideGlAccountId | id | IN | true |
| partyIdFrom | id | IN | true |
| partyIdTo | id | IN | true |
| paymentGatewayResponseId | id | IN | true |
| paymentId | id | IN | false |
| paymentMethodId | id | IN | true |
| paymentMethodTypeId | id | IN | true |
| paymentPreferenceId | id | IN | true |
| paymentRefNum | short-varchar | IN | true |
| paymentTypeId | id | IN | true |
| roleTypeIdTo | id | IN | true |
| statusId | id | IN | true |

## readyInvoices

- Definition: `applications/accounting/servicedef/services_invoice.xml:226`
- Engine: java; location: org.apache.ofbiz.accounting.invoice.InvoiceServices; invoke: readyInvoices
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoicesCreated | List | IN | — |

## reconcileAdjustmentFinAcctgTrans

- Definition: `applications/accounting/servicedef/services_finaccount.xml:259`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountServices.xml; invoke: reconcileAdjustmentFinAcctgTrans
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountTrans | org.apache.ofbiz.entity.GenericValue | IN | false |
| organizationPartyId | String | IN | false |

## reconcileDepositFinAcctgTrans

- Definition: `applications/accounting/servicedef/services_finaccount.xml:265`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountServices.xml; invoke: reconcileDepositFinAcctgTrans
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountTrans | org.apache.ofbiz.entity.GenericValue | IN | false |

## reconcileFinAccountTrans

- Definition: `applications/accounting/servicedef/services_finaccount.xml:250`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountServices.xml; invoke: reconcileFinAccountTrans
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgFinAcctPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| debitCreditFlag | String | IN | true |
| finAccountTransId | String | IN | false |
| glAccountId | String | IN | true |
| organizationPartyId | String | IN | false |

## reconcileWithdrawalFinAcctgTrans

- Definition: `applications/accounting/servicedef/services_finaccount.xml:270`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountServices.xml; invoke: reconcileWithdrawalFinAcctgTrans
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountTrans | org.apache.ofbiz.entity.GenericValue | IN | false |

## redeemGiftCertificate

- Definition: `applications/accounting/servicedef/services_finaccount.xml:375`
- Engine: java; location: org.apache.ofbiz.accounting.payment.GiftCertificateServices; invoke: redeemGiftCertificate
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | BigDecimal | INOUT | false |
| balance | BigDecimal | OUT | false |
| cardNumber | String | IN | false |
| currency | String | IN | true |
| orderId | String | IN | true |
| partyId | String | IN | true |
| pinNumber | String | IN | false |
| previousBalance | BigDecimal | OUT | true |
| processResult | Boolean | OUT | false |
| productStoreId | String | IN | false |
| referenceNum | String | OUT | false |
| responseCode | String | OUT | false |

## refundGcPurchase

- Definition: `applications/accounting/servicedef/services_finaccount.xml:417`
- Engine: java; location: org.apache.ofbiz.accounting.payment.GiftCertificateServices; invoke: refundGcPurchase
- Default entity: —; auth: true
- Use transaction: true; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| orderItem | org.apache.ofbiz.entity.GenericValue | IN | false |
| partyId | String | IN | false |

## refundOrderPaymentPreference

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:370`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: refundOrderPaymentPreference
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | BigDecimal | IN | false |
| orderPaymentPreferenceId | String | IN | false |
| paymentId | String | OUT | false |
| refundAmount | BigDecimal | OUT | false |

## refundPayment

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:379`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: refundPayment
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| orderPaymentPreference | org.apache.ofbiz.entity.GenericValue | IN | false |
| paymentId | String | OUT | false |
| refundAmount | BigDecimal | INOUT | false |

## releaseOrderPaymentPreference

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:325`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: releaseOrderPaymentPreference
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| orderPaymentPreferenceId | String | IN | false |

## releaseOrderPayments

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:317`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: releaseOrderPayments
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| orderId | String | IN | false |
| orderPaymentPreferenceId | String | IN | true |
| processResult | String | OUT | false |

## removeAcctgTransType

- Definition: `applications/accounting/servicedef/services_ledger.xml:731`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AcctgTransType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransTypeId | id | IN | false |

## removeAgreementContent

- Definition: `applications/accounting/servicedef/services_agreement.xml:264`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AgreementContent; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementContentTypeId | id | IN | false |
| agreementId | id | IN | false |
| agreementItemSeqId | id | IN | false |
| contentId | id | IN | false |
| fromDate | date-time | IN | false |

## removeAgreementFacilityAppl

- Definition: `applications/accounting/servicedef/services_agreement.xml:173`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AgreementFacilityAppl; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| agreementItemSeqId | id | IN | false |
| facilityId | id | IN | false |

## removeAgreementGeographicalApplic

- Definition: `applications/accounting/servicedef/services_agreement.xml:213`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AgreementGeographicalApplic; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| agreementItemSeqId | id | IN | false |
| geoId | id | IN | false |

## removeAgreementItem

- Definition: `applications/accounting/servicedef/services_agreement.xml:73`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AgreementItem; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| agreementItemSeqId | id | IN | false |

## removeAgreementPartyApplic

- Definition: `applications/accounting/servicedef/services_agreement.xml:193`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AgreementPartyApplic; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| agreementItemSeqId | id | IN | false |
| partyId | id | IN | false |

## removeAgreementProductAppl

- Definition: `applications/accounting/servicedef/services_agreement.xml:153`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AgreementProductAppl; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| agreementItemSeqId | id | IN | false |
| price | currency-precise | IN | true |
| productId | id | IN | false |

## removeAgreementPromoAppl

- Definition: `applications/accounting/servicedef/services_agreement.xml:133`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AgreementPromoAppl; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| agreementItemSeqId | id | IN | false |
| fromDate | date-time | IN | false |
| productPromoId | id | IN | false |
| sequenceNum | numeric | IN | true |
| thruDate | date-time | IN | true |

## removeAgreementType

- Definition: `applications/accounting/servicedef/services_agreement.xml:247`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AgreementType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementTypeId | id | IN | — |

## removeAgreementTypeAttr

- Definition: `applications/accounting/servicedef/services_agreement.xml:323`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: AgreementTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementTypeId | id | IN | false |
| attrName | id-long | IN | false |

## removeBillingAccountRole

- Definition: `applications/accounting/servicedef/services_billing.xml:53`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: BillingAccountRole; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBillingAcctCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| billingAccountId | id | IN | false |
| fromDate | date-time | IN | false |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |

## removeBillingAccountTerm

- Definition: `applications/accounting/servicedef/services_billing.xml:81`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: BillingAccountTerm; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBillingAcctCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| billingAccountTermId | id | IN | false |

## removeBudgetAttribute

- Definition: `applications/accounting/servicedef/services_budget.xml:234`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: BudgetAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| budgetId | id | IN | — |

## removeBudgetItem

- Definition: `applications/accounting/servicedef/services_budget.xml:63`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: BudgetItem; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetId | id | IN | false |
| budgetItemSeqId | id | IN | false |

## removeBudgetItemType

- Definition: `applications/accounting/servicedef/services_budget.xml:128`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: BudgetItemType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetItemTypeId | id | IN | — |

## removeBudgetReview

- Definition: `applications/accounting/servicedef/services_budget.xml:84`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: BudgetReview; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetId | id | IN | false |
| budgetReviewId | id | IN | false |
| budgetReviewResultTypeId | id | IN | false |
| partyId | id | IN | false |

## removeBudgetRevisionImpact

- Definition: `applications/accounting/servicedef/services_budget.xml:201`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: BudgetRevisionImpact; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetId | id | IN | false |
| budgetItemSeqId | id | IN | false |
| revisionSeqId | id | IN | false |

## removeBudgetRole

- Definition: `applications/accounting/servicedef/services_budget.xml:73`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: BudgetRole; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetId | id | IN | false |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |

## removeCostComponentCalc

- Definition: `applications/accounting/servicedef/services_cost.xml:41`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: CostComponentCalc; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgCostPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| costComponentCalcId | id | IN | false |
| costCustomMethodId | id | IN | true |
| costGlAccountTypeId | id | IN | true |
| currencyUomId | id | IN | true |
| description | description | IN | true |
| fixedCost | currency-amount | IN | true |
| offsettingGlAccountTypeId | id | IN | true |
| perMilliSecond | numeric | IN | true |
| variableCost | currency-amount | IN | true |

## removeFinAccountTransFromReconciliation

- Definition: `applications/accounting/servicedef/services_finaccount.xml:501`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountServices.xml; invoke: removeFinAccountTransFromReconciliation
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgFinAcctPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountTransId | String | IN | false |

## removeFixedAssetIdent

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:98`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: FixedAssetIdent; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetId | id | IN | false |
| fixedAssetIdentTypeId | id | IN | false |

## removeFixedAssetProduct

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:57`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: FixedAssetProduct; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetId | id | IN | false |
| fixedAssetProductTypeId | id | IN | false |
| fromDate | date-time | IN | false |
| productId | id | IN | false |

## removeGlAccountTypeDefault

- Definition: `applications/accounting/servicedef/services_admin.xml:106`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: GlAccountTypeDefault; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgPrefPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glAccountTypeId | id | IN | false |
| organizationPartyId | id | IN | false |

## removeInvoiceContent

- Definition: `applications/accounting/servicedef/services_invoice.xml:415`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: InvoiceContent; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | — |
| fromDate | date-time | IN | — |
| invoiceContentTypeId | id | IN | — |
| invoiceId | id | IN | — |

## removeInvoiceItem

- Definition: `applications/accounting/servicedef/services_invoice.xml:139`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy; invoke: removeInvoiceItem
- Default entity: InvoiceItem; auth: —
- Use transaction: —; timeout: —
- Permission service: checkInvoiceStatusInProgress; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | id | IN | — |
| invoiceItemSeqId | id | IN | — |

## removeInvoiceItemAssocOnCancelInvoice

- Definition: `applications/accounting/servicedef/services_invoice.xml:377`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy; invoke: removeInvoiceItemAssocOnCancelInvoice
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | String | IN | — |

## removeInvoiceItemTypeGlAssignment

- Definition: `applications/accounting/servicedef/services_admin.xml:116`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: InvoiceItemTypeGlAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgPrefPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceItemTypeId | id | IN | false |
| organizationPartyId | id | IN | false |

## removeInvoiceRole

- Definition: `applications/accounting/servicedef/services_invoice.xml:159`
- Engine: entity-auto; location: —; invoke: remove
- Default entity: InvoiceRole; auth: —
- Use transaction: —; timeout: —
- Permission service: checkInvoiceStatusInProgress; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | id | IN | — |
| partyId | id | IN | — |
| roleTypeId | id | IN | — |

## removePaymentApplication

- Definition: `applications/accounting/servicedef/services_payment.xml:103`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy; invoke: removePaymentApplication
- Default entity: PaymentApplication; auth: —
- Use transaction: —; timeout: —
- Permission service: acctgInvoicePermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amountApplied | currency-amount | OUT | true |
| billingAccountId | id | OUT | true |
| invoiceId | id | OUT | true |
| invoiceItemSeqId | id | OUT | true |
| overrideGlAccountId | id | OUT | true |
| paymentApplicationId | id | IN | false |
| paymentId | id | OUT | true |
| taxAuthGeoId | id | OUT | true |
| toPaymentId | id | OUT | true |

## removePaymentContent

- Definition: `applications/accounting/servicedef/services_payment.xml:270`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PaymentContent; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| fromDate | date-time | IN | false |
| paymentContentTypeId | id | IN | false |
| paymentId | id | IN | false |

## removePaymentMethodTypeGlAssignment

- Definition: `applications/accounting/servicedef/services_admin.xml:138`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PaymentMethodTypeGlAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgPrefPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| organizationPartyId | id | IN | false |
| paymentMethodTypeId | id | IN | false |

## removePaymentTypeGlAssignment

- Definition: `applications/accounting/servicedef/services_admin.xml:127`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: PaymentGlAccountTypeMap; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgPrefPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| organizationPartyId | id | IN | false |
| paymentTypeId | id | IN | false |

## removeWorkEffortCostCalc

- Definition: `applications/accounting/servicedef/services_cost.xml:63`
- Engine: entity-auto; location: —; invoke: delete
- Default entity: WorkEffortCostCalc; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgCostPermissionCheck; main action: DELETE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| costComponentTypeId | id | IN | false |
| fromDate | date-time | IN | false |
| workEffortId | id | IN | false |

## resetOrderItemBillingAndOrderAdjustmentBillingOnCancelInvoice

- Definition: `applications/accounting/servicedef/services_invoice.xml:383`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy; invoke: resetOrderItemBillingAndOrderAdjustmentBillingOnCancelInvoice
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | String | IN | — |

## retryFailedAuthNsfs

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:289`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: retryFailedAuthNsfs
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## retryFailedAuths

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:276`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: retryFailedAuths
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## retryFailedOrderAuth

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:281`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: retryFailedOrderAuth
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| authResultMsgs | List | OUT | true |
| orderId | String | IN | false |
| processResult | String | OUT | true |

## revertAcctgTransOnCancelInvoice

- Definition: `applications/accounting/servicedef/services_ledger.xml:592`
- Engine: simple; location: component://accounting/minilang/ledger/AcctgTransServices.xml; invoke: revertAcctgTransOnCancelInvoice
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | String | IN | false |

## revertAcctgTransOnRemovePaymentApplications

- Definition: `applications/accounting/servicedef/services_ledger.xml:597`
- Engine: simple; location: component://accounting/minilang/ledger/AcctgTransServices.xml; invoke: revertAcctgTransOnRemovePaymentApplications
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| paymentApplicationId | String | IN | false |

## sampleInvoiceAffiliateCommission

- Definition: `applications/accounting/servicedef/services_sample.xml:27`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/SampleServices.groovy; invoke: sampleCalculateAffiliateCommission
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceIds | List | OUT | — |
| paymentId | String | IN | — |

## savePaymentGatewayResponse

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:483`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: savePaymentGatewayResponse
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| paymentGatewayResponse | org.apache.ofbiz.entity.GenericValue | IN | false |

## savePaymentGatewayResponseAndMessages

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:489`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: savePaymentGatewayResponseAndMessages
- Default entity: —; auth: false
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| messages | List | IN | false |
| paymentGatewayResponse | org.apache.ofbiz.entity.GenericValue | IN | false |

## sendInvoicePerEmail

- Definition: `applications/accounting/servicedef/services_invoice.xml:285`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy; invoke: sendInvoicePerEmail
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: acctgInvoicePermissionCheck; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| bodyText | String | IN | true |
| invoiceId | String | IN | — |
| other | String | IN | true |
| sendCc | String | IN | true |
| sendFrom | String | IN | — |
| sendTo | String | IN | — |
| subject | String | IN | true |

## setAcctgCompany

- Definition: `applications/accounting/servicedef/services_admin.xml:81`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/admin/AcctgAdminServices.groovy; invoke: setAcctgCompany
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgPrefPermissionCheck; main action: VIEW
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| organizationPartyId | String | INOUT | true |

## setFinAccountTransStatus

- Definition: `applications/accounting/servicedef/services_finaccount.xml:128`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountServices.xml; invoke: setFinAccountTransStatus
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgFinAcctPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountTransId | String | IN | true |
| oldStatusId | String | OUT | true |
| statusId | String | IN | true |

## setGlReconciliationStatus

- Definition: `applications/accounting/servicedef/services_ledger.xml:659`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: setGlReconciliationStatus
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glReconciliationId | String | IN | false |
| oldStatusId | String | OUT | true |
| statusId | String | IN | false |

## setInvoiceStatus

- Definition: `applications/accounting/servicedef/services_invoice.xml:88`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy; invoke: setInvoiceStatus
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: acctgInvoicePermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceId | String | IN | — |
| invoiceTypeId | String | OUT | true |
| oldStatusId | String | OUT | true |
| paidDate | Timestamp | IN | true |
| statusDate | Timestamp | IN | — |
| statusId | String | IN | — |

## setInvoicesToReadyFromShipment

- Definition: `applications/accounting/servicedef/services_invoice.xml:245`
- Engine: java; location: org.apache.ofbiz.accounting.invoice.InvoiceServices; invoke: setInvoicesToReadyFromShipment
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| shipmentId | String | IN | — |

## setPaymentMethodAddress

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:204`
- Engine: simple; location: component://accounting/minilang/payment/PaymentMethodServices.xml; invoke: setPaymentMethodAddress
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | IN | false |
| paymentMethodId | String | IN | false |

## setPaymentStatus

- Definition: `applications/accounting/servicedef/services_payment.xml:47`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy; invoke: setPaymentStatus
- Default entity: Payment; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgPaymentPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| oldStatusId | String | OUT | true |
| paymentId | id | IN | false |
| statusId | String | IN | false |

## straightLineDepreciation

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:258`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/fixedasset/FixedAssetServices.groovy; invoke: straightLineDepreciation
- Default entity: FixedAsset; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "fixedAssetDepCalcInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetId | id | IN | false |

## testCCCapture

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:726`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: testCapture
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "ccCaptureInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## testCCCaptureWithReAuth

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:731`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: testCaptureWithReAuth
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "ccCaptureInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## testCCProcessor

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:670`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: testProcessor
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "ccAuthInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## testCCProcessorCaptureAlwaysDecline

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:736`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: testCCProcessorCaptureAlwaysDecline
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "ccCaptureInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## testCCProcessorWithCapture

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:675`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: testProcessorWithCapture
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "ccAuthInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## testCCRefund

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:746`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: testRefund
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "paymentRefundInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## testCCRefundFailure

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:751`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: testRefundFailure
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "paymentRefundInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## testCCRelease

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:741`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: testRelease
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "paymentReleaseInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## testEFTProcessor

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:775`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: testProcessor
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "eftProcessInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## testEFTRelease

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:790`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: testRelease
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "paymentReleaseInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## testGCRelease

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:768`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: testRelease
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "paymentReleaseInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## testRandomAuthorize

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:680`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: testRandomAuthorize
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: [{"service": "ccAuthInterface"}]

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## updateAccommodationClass

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:326`
- Engine: entity-auto; location: —; invoke: update
- Default entity: AccommodationClass; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accommodationClassId | id | IN | false |
| description | description | IN | true |
| parentClassId | id | IN | true |

## updateAccommodationMap

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:356`
- Engine: entity-auto; location: —; invoke: update
- Default entity: AccommodationMap; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accommodationClassId | id | IN | true |
| accommodationMapId | id | IN | — |
| accommodationMapTypeId | id | IN | true |
| fixedAssetId | id | IN | true |
| numberOfSpaces | numeric | IN | true |

## updateAccommodationMapType

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:340`
- Engine: entity-auto; location: —; invoke: update
- Default entity: AccommodationMapType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accommodationMapTypeId | id | IN | false |
| description | description | IN | true |

## updateAcctgTrans

- Definition: `applications/accounting/servicedef/services_ledger.xml:203`
- Engine: entity-auto; location: —; invoke: update
- Default entity: AcctgTrans; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgTransactionPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | id | IN | false |
| acctgTransTypeId | id | IN | true |
| createdByUserLogin | id-vlong | IN | true |
| createdDate | date-time | IN | true |
| description | description | IN | true |
| finAccountTransId | id | IN | true |
| fixedAssetId | id | IN | true |
| glFiscalTypeId | id | IN | true |
| glJournalId | id | IN | true |
| groupStatusId | id | IN | true |
| inventoryItemId | id | IN | true |
| invoiceId | id | IN | true |
| isPosted | indicator | IN | true |
| lastModifiedByUserLogin | id-vlong | IN | true |
| lastModifiedDate | date-time | IN | true |
| partyId | id | IN | true |
| paymentId | id | IN | true |
| physicalInventoryId | id | IN | true |
| postedDate | date-time | IN | true |
| receiptId | id | IN | true |
| roleTypeId | id | IN | true |
| scheduledPostingDate | date-time | IN | true |
| shipmentId | id | IN | true |
| theirAcctgTransId | id-long | IN | true |
| transactionDate | date-time | IN | true |
| voucherDate | date-time | IN | true |
| voucherRef | short-varchar | IN | true |
| workEffortId | id | IN | true |

## updateAcctgTransAttribute

- Definition: `applications/accounting/servicedef/services_ledger.xml:681`
- Engine: entity-auto; location: —; invoke: update
- Default entity: AcctgTransAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransId | id | IN | false |
| attrDescription | description | IN | true |
| attrName | id-long | IN | false |
| attrValue | value | IN | true |

## updateAcctgTransEntry

- Definition: `applications/accounting/servicedef/services_ledger.xml:240`
- Engine: simple; location: component://accounting/minilang/ledger/AcctgTransServices.xml; invoke: updateAcctgTransEntry
- Default entity: AcctgTransEntry; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgTransactionPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransEntrySeqId | id | IN | false |
| acctgTransEntryTypeId | id | IN | true |
| acctgTransId | id | IN | false |
| amount | currency-amount | IN | true |
| currencyUomId | id | IN | true |
| debitCreditFlag | indicator | IN | true |
| description | description | IN | true |
| dueDate | date | IN | true |
| glAccountId | id | IN | true |
| glAccountTypeId | id | IN | true |
| groupId | id | IN | true |
| inventoryItemId | id | IN | true |
| isSummary | indicator | IN | true |
| organizationPartyId | id | IN | true |
| origAmount | currency-amount | IN | true |
| origCurrencyUomId | id | IN | true |
| partyId | id | IN | true |
| productId | id | IN | true |
| reconcileStatusId | id | IN | true |
| roleTypeId | id | IN | true |
| settlementTermId | id | IN | true |
| taxId | id | IN | true |
| theirPartyId | id | IN | true |
| theirProductId | id | IN | true |
| voucherRef | short-varchar | IN | true |

## updateAcctgTransEntryType

- Definition: `applications/accounting/servicedef/services_ledger.xml:711`
- Engine: entity-auto; location: —; invoke: update
- Default entity: AcctgTransEntryType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransEntryTypeId | id | IN | false |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## updateAcctgTransType

- Definition: `applications/accounting/servicedef/services_ledger.xml:726`
- Engine: entity-auto; location: —; invoke: update
- Default entity: AcctgTransType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransTypeId | id | IN | false |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## updateAcctgTransTypeAttr

- Definition: `applications/accounting/servicedef/services_ledger.xml:696`
- Engine: entity-auto; location: —; invoke: update
- Default entity: AcctgTransTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransTypeId | id | IN | false |
| attrName | id-long | IN | false |
| description | description | IN | true |

## updateAgreement

- Definition: `applications/accounting/servicedef/services_agreement.xml:34`
- Engine: entity-auto; location: —; invoke: update
- Default entity: Agreement; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementDate | date-time | IN | true |
| agreementId | id | IN | false |
| agreementTypeId | id | IN | true |
| description | description | IN | true |
| fromDate | date-time | IN | true |
| partyIdFrom | id | IN | true |
| partyIdTo | id | IN | true |
| productId | id | IN | true |
| roleTypeIdFrom | id | IN | true |
| roleTypeIdTo | id | IN | true |
| statusId | id | IN | true |
| textData | very-long | IN | true |
| thruDate | date-time | IN | true |

## updateAgreementContent

- Definition: `applications/accounting/servicedef/services_agreement.xml:259`
- Engine: entity-auto; location: —; invoke: update
- Default entity: AgreementContent; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementContentTypeId | id | IN | false |
| agreementId | id | IN | false |
| agreementItemSeqId | id | IN | false |
| contentId | id | IN | false |
| fromDate | date-time | IN | false |
| thruDate | date-time | IN | true |

## updateAgreementFacilityAppl

- Definition: `applications/accounting/servicedef/services_agreement.xml:167`
- Engine: entity-auto; location: —; invoke: update
- Default entity: AgreementFacilityAppl; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| agreementItemSeqId | id | IN | false |
| facilityId | id | IN | false |

## updateAgreementGeographicalApplic

- Definition: `applications/accounting/servicedef/services_agreement.xml:207`
- Engine: entity-auto; location: —; invoke: update
- Default entity: AgreementGeographicalApplic; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| agreementItemSeqId | id | IN | false |
| geoId | id | IN | false |

## updateAgreementItem

- Definition: `applications/accounting/servicedef/services_agreement.xml:66`
- Engine: entity-auto; location: —; invoke: update
- Default entity: AgreementItem; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| agreementImage | object | IN | true |
| agreementItemSeqId | id | IN | false |
| agreementItemTypeId | id | IN | true |
| agreementText | very-long | IN | true |
| currencyUomId | id | IN | true |

## updateAgreementItemAttribute

- Definition: `applications/accounting/servicedef/services_agreement.xml:86`
- Engine: entity-auto; location: —; invoke: update
- Default entity: AgreementItemAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| agreementItemSeqId | id | IN | false |
| attrDescription | description | IN | true |
| attrName | id-long | IN | false |
| attrValue | value | IN | true |

## updateAgreementPartyApplic

- Definition: `applications/accounting/servicedef/services_agreement.xml:187`
- Engine: entity-auto; location: —; invoke: update
- Default entity: AgreementPartyApplic; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| agreementItemSeqId | id | IN | false |
| partyId | id | IN | false |

## updateAgreementProductAppl

- Definition: `applications/accounting/servicedef/services_agreement.xml:147`
- Engine: entity-auto; location: —; invoke: update
- Default entity: AgreementProductAppl; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| agreementItemSeqId | id | IN | false |
| price | currency-precise | IN | true |
| productId | id | IN | false |

## updateAgreementPromoAppl

- Definition: `applications/accounting/servicedef/services_agreement.xml:127`
- Engine: entity-auto; location: —; invoke: update
- Default entity: AgreementPromoAppl; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| agreementItemSeqId | id | IN | false |
| fromDate | date-time | IN | false |
| productPromoId | id | IN | false |
| sequenceNum | numeric | IN | true |
| thruDate | date-time | IN | true |

## updateAgreementRole

- Definition: `applications/accounting/servicedef/services_agreement.xml:226`
- Engine: entity-auto; location: —; invoke: update
- Default entity: AgreementRole; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | false |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |

## updateAgreementTerm

- Definition: `applications/accounting/servicedef/services_agreement.xml:106`
- Engine: entity-auto; location: —; invoke: update
- Default entity: AgreementTerm; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgAgreementPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementId | id | IN | true |
| agreementItemSeqId | id | IN | true |
| agreementTermId | id | IN | false |
| description | description | IN | true |
| fromDate | date-time | IN | true |
| invoiceItemTypeId | id | IN | true |
| maxQuantity | floating-point | IN | true |
| minQuantity | floating-point | IN | true |
| termDays | numeric | IN | true |
| termTypeId | id | IN | true |
| termValue | currency-precise | IN | true |
| textValue | description | IN | true |
| thruDate | date-time | IN | true |

## updateAgreementType

- Definition: `applications/accounting/servicedef/services_agreement.xml:242`
- Engine: entity-auto; location: —; invoke: update
- Default entity: AgreementType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementTypeId | id | IN | — |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## updateAgreementTypeAttr

- Definition: `applications/accounting/servicedef/services_agreement.xml:318`
- Engine: entity-auto; location: —; invoke: update
- Default entity: AgreementTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| agreementTypeId | id | IN | false |
| attrName | id-long | IN | false |
| description | description | IN | true |

## updateBillingAccount

- Definition: `applications/accounting/servicedef/services_billing.xml:34`
- Engine: entity-auto; location: —; invoke: update
- Default entity: BillingAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBillingAcctCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accountCurrencyUomId | id | IN | true |
| accountLimit | currency-amount | IN | true |
| billingAccountId | id | IN | false |
| contactMechId | id | IN | true |
| description | description | IN | true |
| externalAccountId | id | IN | true |
| fromDate | date-time | IN | true |
| thruDate | date-time | IN | true |

## updateBillingAccountRole

- Definition: `applications/accounting/servicedef/services_billing.xml:47`
- Engine: entity-auto; location: —; invoke: update
- Default entity: BillingAccountRole; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBillingAcctCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| billingAccountId | id | IN | false |
| fromDate | date-time | IN | false |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |
| thruDate | date-time | IN | true |

## updateBillingAccountTerm

- Definition: `applications/accounting/servicedef/services_billing.xml:73`
- Engine: entity-auto; location: —; invoke: update
- Default entity: BillingAccountTerm; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBillingAcctCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| billingAccountId | id | IN | false |
| billingAccountTermId | id | IN | false |
| termDays | numeric | IN | true |
| termTypeId | id | IN | false |
| termValue | currency-amount | IN | true |
| uomId | id | IN | true |

## updateBillingAccountTermAttr

- Definition: `applications/accounting/servicedef/services_payment.xml:279`
- Engine: entity-auto; location: —; invoke: update
- Default entity: BillingAccountTermAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | false |
| attrValue | value | IN | true |
| billingAccountTermId | id | IN | false |

## updateBudget

- Definition: `applications/accounting/servicedef/services_budget.xml:34`
- Engine: entity-auto; location: —; invoke: update
- Default entity: Budget; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetId | id | IN | false |
| budgetTypeId | id | IN | true |
| comments | comment | IN | true |
| customTimePeriodId | id | IN | true |

## updateBudgetAttribute

- Definition: `applications/accounting/servicedef/services_budget.xml:229`
- Engine: entity-auto; location: —; invoke: update
- Default entity: BudgetAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | — |
| attrValue | value | IN | true |
| budgetId | id | IN | — |

## updateBudgetItem

- Definition: `applications/accounting/servicedef/services_budget.xml:58`
- Engine: entity-auto; location: —; invoke: update
- Default entity: BudgetItem; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | currency-amount | IN | true |
| budgetId | id | IN | false |
| budgetItemSeqId | id | IN | false |
| budgetItemTypeId | id | IN | true |
| justification | long-varchar | IN | true |
| purpose | long-varchar | IN | true |

## updateBudgetItemAttribute

- Definition: `applications/accounting/servicedef/services_budget.xml:109`
- Engine: entity-auto; location: —; invoke: update
- Default entity: BudgetItemAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | — |
| attrValue | value | IN | true |
| budgetId | id | IN | — |
| budgetItemSeqId | id | IN | — |

## updateBudgetItemType

- Definition: `applications/accounting/servicedef/services_budget.xml:123`
- Engine: entity-auto; location: —; invoke: update
- Default entity: BudgetItemType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetItemTypeId | id | IN | — |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## updateBudgetItemTypeAttr

- Definition: `applications/accounting/servicedef/services_budget.xml:137`
- Engine: entity-auto; location: —; invoke: update
- Default entity: BudgetItemTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| budgetItemTypeId | id | IN | — |
| description | description | IN | true |

## updateBudgetReviewResultType

- Definition: `applications/accounting/servicedef/services_budget.xml:151`
- Engine: entity-auto; location: —; invoke: update
- Default entity: BudgetReviewResultType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetReviewResultTypeId | id | IN | — |
| comments | comment | IN | true |
| description | description | IN | true |

## updateBudgetRevision

- Definition: `applications/accounting/servicedef/services_budget.xml:165`
- Engine: entity-auto; location: —; invoke: update
- Default entity: BudgetRevision; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetId | id | IN | — |
| dateRevised | date-time | IN | true |
| revisionSeqId | id | IN | — |

## updateBudgetRevisionImpact

- Definition: `applications/accounting/servicedef/services_budget.xml:196`
- Engine: entity-auto; location: —; invoke: update
- Default entity: BudgetRevisionImpact; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| addDeleteFlag | indicator | IN | true |
| budgetId | id | IN | false |
| budgetItemSeqId | id | IN | false |
| revisedAmount | currency-amount | IN | true |
| revisionReason | long-varchar | IN | true |
| revisionSeqId | id | IN | false |

## updateBudgetScenario

- Definition: `applications/accounting/servicedef/services_budget.xml:179`
- Engine: entity-auto; location: —; invoke: update
- Default entity: BudgetScenario; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetScenarioId | id | IN | — |
| description | description | IN | true |

## updateBudgetScenarioApplication

- Definition: `applications/accounting/servicedef/services_budget.xml:257`
- Engine: entity-auto; location: —; invoke: update
- Default entity: BudgetScenarioApplication; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amountChange | currency-amount | IN | true |
| budgetId | id | IN | true |
| budgetItemSeqId | id | IN | true |
| budgetScenarioApplicId | id | IN | — |
| budgetScenarioId | id | IN | — |
| percentageChange | fixed-point | IN | true |

## updateBudgetScenarioRule

- Definition: `applications/accounting/servicedef/services_budget.xml:212`
- Engine: entity-auto; location: —; invoke: update
- Default entity: BudgetScenarioRule; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amountChange | currency-amount | IN | true |
| budgetItemTypeId | id | IN | false |
| budgetScenarioId | id | IN | false |
| percentageChange | fixed-point | IN | true |

## updateBudgetStatus

- Definition: `applications/accounting/servicedef/services_budget.xml:45`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/budget/BudgetServices.groovy; invoke: updateBudgetStatus
- Default entity: BudgetStatus; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetId | id | IN | false |
| changeByUserLoginId | id-vlong | IN | true |
| comments | comment | IN | true |
| statusDate | date-time | IN | true |
| statusId | id | IN | false |

## updateBudgetType

- Definition: `applications/accounting/servicedef/services_budget.xml:93`
- Engine: entity-auto; location: —; invoke: update
- Default entity: BudgetType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| budgetTypeId | id | IN | — |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## updateBudgetTypeAttr

- Definition: `applications/accounting/servicedef/services_budget.xml:243`
- Engine: entity-auto; location: —; invoke: update
- Default entity: BudgetTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| budgetTypeId | id | IN | — |
| description | description | IN | true |

## updateCheckAccount

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:158`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentMethodServices; invoke: updateCheckAccount
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accountNumber | String | IN | true |
| accountType | String | IN | true |
| bankName | String | IN | true |
| companyNameOnAccount | String | IN | true |
| contactMechId | String | IN | true |
| description | String | IN | true |
| fromDate | Timestamp | IN | true |
| nameOnAccount | String | IN | false |
| oldPaymentMethodId | String | OUT | true |
| partyId | String | IN | true |
| paymentMethodId | String | INOUT | false |
| paymentMethodTypeId | String | IN | false |
| routingNumber | String | IN | true |

## updateCostComponentCalc

- Definition: `applications/accounting/servicedef/services_cost.xml:34`
- Engine: entity-auto; location: —; invoke: update
- Default entity: CostComponentCalc; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgCostPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| costComponentCalcId | id | IN | false |
| costCustomMethodId | id | IN | true |
| costGlAccountTypeId | id | IN | true |
| currencyUomId | id | IN | true |
| description | description | IN | true |
| fixedCost | currency-amount | IN | true |
| offsettingGlAccountTypeId | id | IN | true |
| perMilliSecond | numeric | IN | true |
| variableCost | currency-amount | IN | true |

## updateCreditCard

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:65`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentMethodServices; invoke: updateCreditCard
- Default entity: CreditCard; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| cardNumber | credit-card-number | IN | true |
| cardType | short-varchar | IN | true |
| companyNameOnCard | name | IN | true |
| consecutiveFailedAuths | numeric | IN | true |
| consecutiveFailedNsf | numeric | IN | true |
| contactMechId | id | IN | true |
| description | String | IN | true |
| expMonth | String | IN | true |
| expYear | String | IN | true |
| expireDate | credit-card-date | IN | true |
| firstNameOnCard | name | IN | true |
| fromDate | Timestamp | IN | true |
| issueNumber | credit-card-date | IN | true |
| lastFailedAuthDate | date-time | IN | true |
| lastFailedNsfDate | date-time | IN | true |
| lastNameOnCard | name | IN | true |
| middleNameOnCard | name | IN | true |
| oldPaymentMethodId | String | OUT | false |
| partyId | String | IN | true |
| paymentMethodId | String | OUT | false |
| productStoreId | String | IN | true |
| suffixOnCard | name | IN | true |
| thruDate | Timestamp | IN | true |
| titleOnCard | name | IN | true |
| validFromDate | credit-card-date | IN | true |

## updateCreditCardAndAddress

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:60`
- Engine: group; location: updateCreditCardAndAddress; invoke: —
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | INOUT | true |
| paymentMethodId | String | INOUT | true |

## updateCreditCardTypeGlAccount

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:811`
- Engine: entity-auto; location: —; invoke: update
- Default entity: CreditCardTypeGlAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| cardType | short-varchar | IN | false |
| glAccountId | id | IN | false |
| organizationPartyId | id | IN | false |

## updateDeduction

- Definition: `applications/accounting/servicedef/services_payment.xml:291`
- Engine: entity-auto; location: —; invoke: update
- Default entity: Deduction; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | currency-amount | IN | true |
| deductionId | id | IN | — |
| deductionTypeId | id | IN | true |
| paymentId | id | IN | true |

## updateDeductionType

- Definition: `applications/accounting/servicedef/services_payment.xml:305`
- Engine: entity-auto; location: —; invoke: update
- Default entity: DeductionType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| deductionTypeId | id | IN | — |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## updateEftAccount

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:186`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentMethodServices; invoke: updateEftAccount
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accountNumber | String | IN | false |
| accountType | String | IN | false |
| bankName | String | IN | false |
| companyNameOnAccount | String | IN | true |
| contactMechId | String | IN | true |
| description | String | IN | true |
| fromDate | Timestamp | IN | true |
| nameOnAccount | String | IN | false |
| oldPaymentMethodId | String | OUT | true |
| partyId | String | IN | true |
| paymentMethodId | String | INOUT | false |
| routingNumber | String | IN | false |
| thruDate | Timestamp | IN | true |

## updateEftAccountAndAddress

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:181`
- Engine: group; location: updateEftAccountAndAddress; invoke: —
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | INOUT | true |
| paymentMethodId | String | INOUT | true |

## updateFXConversion

- Definition: `applications/accounting/servicedef/services_admin.xml:88`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/admin/AcctgAdminServices.groovy; invoke: updateFXConversion
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: acctgFxPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| asOfTimestamp | Timestamp | IN | true |
| conversionFactor | BigDecimal | IN | false |
| fromDate | Timestamp | IN | true |
| purposeEnumId | String | IN | true |
| thruDate | Timestamp | IN | true |
| uomId | String | IN | false |
| uomIdTo | String | IN | false |

## updateFinAccount

- Definition: `applications/accounting/servicedef/services_finaccount.xml:38`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountServices.xml; invoke: updateFinAccount
- Default entity: FinAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgFinAcctPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| actualBalance | currency-amount | IN | true |
| availableBalance | currency-amount | IN | true |
| currencyUomId | id | IN | true |
| finAccountCode | long-varchar | IN | true |
| finAccountId | id | INOUT | false |
| finAccountName | name | IN | true |
| finAccountPin | long-varchar | IN | true |
| finAccountTypeId | id | IN | true |
| fromDate | date-time | IN | true |
| isRefundable | indicator | IN | true |
| oldReplenishLevel | BigDecimal | OUT | true |
| oldReplenishPaymentId | String | OUT | true |
| oldStatusId | String | OUT | true |
| organizationPartyId | id | IN | true |
| ownerPartyId | id | IN | true |
| postToGlAccountId | id | IN | true |
| replenishLevel | BigDecimal | OUT | true |
| replenishPaymentId | String | OUT | true |
| statusId | id | IN | true |
| thruDate | date-time | IN | true |

## updateFinAccountAttribute

- Definition: `applications/accounting/servicedef/services_finaccount.xml:540`
- Engine: entity-auto; location: —; invoke: update
- Default entity: FinAccountAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | false |
| attrValue | value | IN | true |
| finAccountId | id | IN | false |

## updateFinAccountBalancesFromAuth

- Definition: `applications/accounting/servicedef/services_finaccount.xml:63`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountServices.xml; invoke: updateFinAccountBalancesFromAuth
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountAuthId | String | IN | false |

## updateFinAccountBalancesFromTrans

- Definition: `applications/accounting/servicedef/services_finaccount.xml:58`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountServices.xml; invoke: updateFinAccountBalancesFromTrans
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountTransId | String | IN | false |

## updateFinAccountRole

- Definition: `applications/accounting/servicedef/services_finaccount.xml:103`
- Engine: entity-auto; location: —; invoke: update
- Default entity: FinAccountRole; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountId | id | IN | false |
| fromDate | date-time | IN | false |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |
| thruDate | date-time | IN | true |

## updateFinAccountTransAttribute

- Definition: `applications/accounting/servicedef/services_finaccount.xml:556`
- Engine: entity-auto; location: —; invoke: update
- Default entity: FinAccountTransAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | false |
| attrValue | value | IN | true |
| finAccountTransId | id | IN | false |

## updateFinAccountTransType

- Definition: `applications/accounting/servicedef/services_finaccount.xml:572`
- Engine: entity-auto; location: —; invoke: update
- Default entity: FinAccountTransType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| finAccountTransTypeId | id | IN | false |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## updateFinAccountTransTypeAttr

- Definition: `applications/accounting/servicedef/services_finaccount.xml:588`
- Engine: entity-auto; location: —; invoke: update
- Default entity: FinAccountTransTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | false |
| description | description | IN | true |
| finAccountTransTypeId | id | IN | false |

## updateFinAccountType

- Definition: `applications/accounting/servicedef/services_finaccount.xml:604`
- Engine: entity-auto; location: —; invoke: update
- Default entity: FinAccountType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| finAccountTypeId | id | IN | false |
| hasTable | indicator | IN | true |
| isRefundable | indicator | IN | true |
| parentTypeId | id | IN | true |
| replenishEnumId | id | IN | true |

## updateFinAccountTypeAttr

- Definition: `applications/accounting/servicedef/services_finaccount.xml:620`
- Engine: entity-auto; location: —; invoke: update
- Default entity: FinAccountTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | false |
| attrValue | long-varchar | IN | true |
| description | description | IN | true |
| finAccountTypeId | id | IN | false |

## updateFinAccountTypeGlAccount

- Definition: `applications/accounting/servicedef/services_ledger.xml:383`
- Engine: entity-auto; location: —; invoke: update
- Default entity: FinAccountTypeGlAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountTypeId | id | IN | false |
| glAccountId | id | IN | false |
| organizationPartyId | id | IN | false |

## updateFixedAsset

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:35`
- Engine: entity-auto; location: —; invoke: update
- Default entity: FixedAsset; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acquireOrderId | id | IN | true |
| acquireOrderItemSeqId | id | IN | true |
| actualEndOfLife | date | IN | true |
| calendarId | id | IN | true |
| classEnumId | id | IN | true |
| dateAcquired | date-time | IN | true |
| dateLastServiced | date-time | IN | true |
| dateNextService | date-time | IN | true |
| depreciation | currency-amount | IN | true |
| expectedEndOfLife | date | IN | true |
| fixedAssetId | id | IN | false |
| fixedAssetName | name | IN | true |
| fixedAssetTypeId | id | IN | false |
| instanceOfProductId | id | IN | true |
| locatedAtFacilityId | id | IN | true |
| locatedAtLocationSeqId | id | IN | true |
| parentFixedAssetId | id | IN | true |
| partyId | id | IN | true |
| productionCapacity | fixed-point | IN | true |
| purchaseCost | currency-amount | IN | true |
| purchaseCostUomId | id | IN | true |
| roleTypeId | id | IN | true |
| salvageValue | currency-amount | IN | true |
| serialNumber | long-varchar | IN | true |
| uomId | id | IN | true |

## updateFixedAssetAttribute

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:372`
- Engine: entity-auto; location: —; invoke: update
- Default entity: FixedAssetAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | false |
| attrValue | value | IN | true |
| fixedAssetId | id | IN | false |

## updateFixedAssetDepMethod

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:228`
- Engine: entity-auto; location: —; invoke: update
- Default entity: FixedAssetDepMethod; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| depreciationCustomMethodId | id | IN | false |
| fixedAssetId | id | IN | false |
| fromDate | date-time | IN | true |
| thruDate | date-time | IN | true |

## updateFixedAssetGeoPoint

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:308`
- Engine: entity-auto; location: —; invoke: update
- Default entity: FixedAssetGeoPoint; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetId | id | IN | false |
| fromDate | date-time | IN | false |
| geoPointId | id | IN | false |
| thruDate | date-time | IN | true |

## updateFixedAssetIdent

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:92`
- Engine: entity-auto; location: —; invoke: update
- Default entity: FixedAssetIdent; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetId | id | IN | false |
| fixedAssetIdentTypeId | id | IN | false |
| idValue | long-varchar | IN | true |

## updateFixedAssetIdentType

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:388`
- Engine: entity-auto; location: —; invoke: update
- Default entity: FixedAssetIdentType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| fixedAssetIdentTypeId | id | IN | false |

## updateFixedAssetMaint

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:134`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/fixedasset/FixedAssetServices.groovy; invoke: updateFixedAssetMaint
- Default entity: FixedAssetMaint; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetId | id | IN | false |
| intervalMeterTypeId | id | IN | true |
| intervalQuantity | fixed-point | IN | true |
| intervalUomId | id | IN | true |
| maintHistSeqId | id | IN | false |
| oldStatusId | String | OUT | false |
| productMaintSeqId | id | IN | true |
| productMaintTypeId | id | IN | true |
| purchaseOrderId | id | IN | true |
| scheduleWorkEffortId | id | IN | true |
| statusId | id | IN | true |

## updateFixedAssetMeter

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:167`
- Engine: entity-auto; location: —; invoke: update
- Default entity: FixedAssetMeter; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetId | id | IN | false |
| maintHistSeqId | id | IN | true |
| meterValue | fixed-point | IN | true |
| productMeterTypeId | id | IN | false |
| readingDate | date-time | IN | false |
| readingReasonEnumId | id | IN | true |
| workEffortId | id | IN | true |

## updateFixedAssetProduct

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:51`
- Engine: entity-auto; location: —; invoke: update
- Default entity: FixedAssetProduct; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| comments | comment | IN | true |
| fixedAssetId | id | IN | false |
| fixedAssetProductTypeId | id | IN | false |
| fromDate | date-time | IN | false |
| productId | id | IN | false |
| quantity | fixed-point | IN | true |
| quantityUomId | id | IN | true |
| sequenceNum | numeric | IN | true |
| thruDate | date-time | IN | true |

## updateFixedAssetProductType

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:404`
- Engine: entity-auto; location: —; invoke: update
- Default entity: FixedAssetProductType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| fixedAssetProductTypeId | id | IN | false |

## updateFixedAssetRegistration

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:111`
- Engine: entity-auto; location: —; invoke: update
- Default entity: FixedAssetRegistration; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetId | id | IN | false |
| fromDate | date-time | IN | false |
| govAgencyPartyId | id | IN | true |
| licenseNumber | long-varchar | IN | true |
| registrationDate | date-time | IN | true |
| registrationNumber | long-varchar | IN | true |
| thruDate | date-time | IN | true |

## updateFixedAssetStdCost

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:71`
- Engine: entity-auto; location: —; invoke: update
- Default entity: FixedAssetStdCost; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | currency-amount | IN | true |
| amountUomId | id | IN | true |
| fixedAssetId | id | IN | false |
| fixedAssetStdCostTypeId | id | IN | false |
| fromDate | date-time | IN | false |
| thruDate | date-time | IN | true |

## updateFixedAssetStdCostType

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:420`
- Engine: entity-auto; location: —; invoke: update
- Default entity: FixedAssetStdCostType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| fixedAssetStdCostTypeId | id | IN | false |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## updateFixedAssetType

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:436`
- Engine: entity-auto; location: —; invoke: update
- Default entity: FixedAssetType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| fixedAssetTypeId | id | IN | false |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## updateFixedAssetTypeAttr

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:452`
- Engine: entity-auto; location: —; invoke: update
- Default entity: FixedAssetTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | false |
| description | description | IN | true |
| fixedAssetTypeId | id | IN | false |

## updateFixedAssetTypeGlAccount

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:289`
- Engine: entity-auto; location: —; invoke: update
- Default entity: FixedAssetTypeGlAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fixedAssetId | id | IN | false |
| fixedAssetTypeId | id | IN | false |
| organizationPartyId | id | IN | false |

## updateGiftCard

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:109`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentMethodServices; invoke: updateGiftCard
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| cardNumber | String | IN | false |
| description | String | IN | true |
| expMonth | String | IN | true |
| expYear | String | IN | true |
| expireDate | String | IN | true |
| fromDate | Timestamp | IN | true |
| oldPaymentMethodId | String | OUT | true |
| partyId | String | IN | true |
| paymentMethodId | String | INOUT | false |
| pinNumber | String | IN | true |
| thruDate | Timestamp | IN | true |

## updateGlAccount

- Definition: `applications/accounting/servicedef/services_ledger.xml:40`
- Engine: entity-auto; location: —; invoke: update
- Default entity: GlAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: basicGeneralLedgerPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| accountCode | name | IN | true |
| accountName | name | IN | true |
| description | description | IN | true |
| externalId | id | IN | true |
| glAccountClassId | id | IN | true |
| glAccountId | id | IN | false |
| glAccountTypeId | id | IN | true |
| glResourceTypeId | id | IN | true |
| glXbrlClassId | id | IN | true |
| parentGlAccountId | id | IN | true |
| productId | id | IN | true |

## updateGlAccountCategory

- Definition: `applications/accounting/servicedef/services_ledger.xml:608`
- Engine: entity-auto; location: —; invoke: update
- Default entity: GlAccountCategory; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| glAccountCategoryId | id | IN | false |
| glAccountCategoryTypeId | id | IN | true |

## updateGlAccountCategoryMember

- Definition: `applications/accounting/servicedef/services_ledger.xml:623`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: updateGlAccountCategoryMember
- Default entity: GlAccountCategoryMember; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amountPercentage | fixed-point | IN | true |
| fromDate | date-time | IN | false |
| glAccountCategoryId | id | IN | false |
| glAccountId | id | IN | false |
| thruDate | date-time | IN | true |

## updateGlAccountCategoryType

- Definition: `applications/accounting/servicedef/services_ledger.xml:742`
- Engine: entity-auto; location: —; invoke: update
- Default entity: GlAccountCategoryType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| glAccountCategoryTypeId | id | IN | false |

## updateGlAccountClass

- Definition: `applications/accounting/servicedef/services_ledger.xml:759`
- Engine: entity-auto; location: —; invoke: update
- Default entity: GlAccountClass; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| glAccountClassId | id | IN | false |
| isAssetClass | indicator | IN | true |
| parentClassId | id | IN | true |
| sequenceNum | numeric | IN | true |

## updateGlAccountGroup

- Definition: `applications/accounting/servicedef/services_ledger.xml:775`
- Engine: entity-auto; location: —; invoke: update
- Default entity: GlAccountGroup; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| glAccountGroupId | id | IN | false |
| glAccountGroupTypeId | id | IN | true |

## updateGlAccountGroupMember

- Definition: `applications/accounting/servicedef/services_ledger.xml:791`
- Engine: entity-auto; location: —; invoke: update
- Default entity: GlAccountGroupMember; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glAccountGroupId | id | IN | true |
| glAccountGroupTypeId | id | IN | false |
| glAccountId | id | IN | false |

## updateGlAccountGroupType

- Definition: `applications/accounting/servicedef/services_ledger.xml:807`
- Engine: entity-auto; location: —; invoke: update
- Default entity: GlAccountGroupType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| glAccountGroupTypeId | id | IN | false |

## updateGlAccountOrganization

- Definition: `applications/accounting/servicedef/services_ledger.xml:58`
- Engine: entity-auto; location: —; invoke: update
- Default entity: GlAccountOrganization; auth: true
- Use transaction: —; timeout: —
- Permission service: basicGeneralLedgerPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | true |
| glAccountId | id | IN | false |
| organizationPartyId | id | IN | false |
| roleTypeId | id | IN | true |
| thruDate | date-time | IN | true |

## updateGlAccountType

- Definition: `applications/accounting/servicedef/services_ledger.xml:835`
- Engine: entity-auto; location: —; invoke: update
- Default entity: GlAccountType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| glAccountTypeId | id | IN | false |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |

## updateGlBudgetXref

- Definition: `applications/accounting/servicedef/services_ledger.xml:851`
- Engine: entity-auto; location: —; invoke: update
- Default entity: GlBudgetXref; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| allocationPercentage | fixed-point | IN | true |
| budgetItemTypeId | id | IN | false |
| fromDate | date-time | IN | false |
| glAccountId | id | IN | false |
| thruDate | date-time | IN | true |

## updateGlFiscalType

- Definition: `applications/accounting/servicedef/services_ledger.xml:867`
- Engine: entity-auto; location: —; invoke: update
- Default entity: GlFiscalType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| glFiscalTypeId | id | IN | false |

## updateGlJournal

- Definition: `applications/accounting/servicedef/services_ledger.xml:109`
- Engine: entity-auto; location: —; invoke: update
- Default entity: GlJournal; auth: true
- Use transaction: —; timeout: —
- Permission service: basicGeneralLedgerPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glJournalId | id | IN | false |
| glJournalName | name | IN | true |
| isPosted | indicator | IN | true |
| organizationPartyId | id | IN | true |
| postedDate | date-time | IN | true |

## updateGlReconciliation

- Definition: `applications/accounting/servicedef/services_ledger.xml:136`
- Engine: entity-auto; location: —; invoke: update
- Default entity: GlReconciliation; auth: true
- Use transaction: —; timeout: —
- Permission service: basicGeneralLedgerPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| createdByUserLogin | id-vlong | IN | true |
| createdDate | date-time | IN | true |
| description | description | IN | true |
| glAccountId | id | IN | true |
| glReconciliationId | id | IN | false |
| glReconciliationName | name | IN | true |
| lastModifiedByUserLogin | id-vlong | IN | true |
| lastModifiedDate | date-time | IN | true |
| openingBalance | currency-amount | IN | true |
| organizationPartyId | id | IN | true |
| reconciledBalance | currency-amount | IN | true |
| reconciledDate | date-time | IN | true |
| statusId | id | IN | true |

## updateGlReconciliationEntry

- Definition: `applications/accounting/servicedef/services_ledger.xml:166`
- Engine: entity-auto; location: —; invoke: update
- Default entity: GlReconciliationEntry; auth: true
- Use transaction: —; timeout: —
- Permission service: basicGeneralLedgerPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| acctgTransEntrySeqId | id | IN | false |
| acctgTransId | id | IN | false |
| glReconciliationId | id | IN | false |
| reconciledAmount | currency-amount | IN | false |

## updateGlReconciliationLastModified

- Definition: `applications/accounting/servicedef/services_ledger.xml:150`
- Engine: simple; location: component://accounting/minilang/ledger/GeneralLedgerServices.xml; invoke: updateGlReconciliationLastModified
- Default entity: GlReconciliation; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| createdByUserLogin | id-vlong | IN | true |
| createdDate | date-time | IN | true |
| description | description | IN | true |
| glAccountId | id | IN | true |
| glReconciliationId | id | IN | false |
| glReconciliationName | name | IN | true |
| lastModifiedByUserLogin | id-vlong | IN | true |
| lastModifiedDate | date-time | IN | true |
| openingBalance | currency-amount | IN | true |
| organizationPartyId | id | IN | true |
| reconciledBalance | currency-amount | IN | true |
| reconciledDate | date-time | IN | true |
| statusId | id | IN | true |

## updateGlResourceType

- Definition: `applications/accounting/servicedef/services_ledger.xml:883`
- Engine: entity-auto; location: —; invoke: update
- Default entity: GlResourceType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| glResourceTypeId | id | IN | false |

## updateGlXbrlClass

- Definition: `applications/accounting/servicedef/services_ledger.xml:899`
- Engine: entity-auto; location: —; invoke: update
- Default entity: GlXbrlClass; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| glXbrlClassId | id | IN | false |
| parentGlXbrlClassId | id | IN | true |

## updateInvoice

- Definition: `applications/accounting/servicedef/services_invoice.xml:79`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy; invoke: updateInvoice
- Default entity: Invoice; auth: —
- Use transaction: —; timeout: —
- Permission service: checkInvoiceStatusInProgress; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| billingAccountId | id | IN | true |
| contactMechId | id | IN | true |
| currencyUomId | id | IN | true |
| description | description | IN | true |
| dueDate | date-time | IN | true |
| invoiceDate | date-time | IN | true |
| invoiceId | id | IN | — |
| invoiceMessage | long-varchar | IN | true |
| invoiceTypeId | id | IN | true |
| paidDate | date-time | IN | true |
| partyId | id | IN | true |
| partyIdFrom | id | IN | true |
| recurrenceInfoId | id | IN | true |
| referenceNumber | short-varchar | IN | true |
| roleTypeId | id | IN | true |
| statusId | id | IN | true |

## updateInvoiceAttribute

- Definition: `applications/accounting/servicedef/services_invoice.xml:509`
- Engine: entity-auto; location: —; invoke: update
- Default entity: InvoiceAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | — |
| attrValue | value | IN | true |
| invoiceId | id | IN | — |

## updateInvoiceContent

- Definition: `applications/accounting/servicedef/services_invoice.xml:410`
- Engine: entity-auto; location: —; invoke: update
- Default entity: InvoiceContent; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | — |
| fromDate | date-time | IN | — |
| invoiceContentTypeId | id | IN | — |
| invoiceId | id | IN | — |
| thruDate | date-time | IN | true |

## updateInvoiceContentAndContent

- Definition: `applications/accounting/servicedef/services_invoice.xml:426`
- Engine: group; location: —; invoke: —
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## updateInvoiceContentType

- Definition: `applications/accounting/servicedef/services_invoice.xml:560`
- Engine: entity-auto; location: —; invoke: update
- Default entity: InvoiceContentType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| invoiceContentTypeId | id | IN | — |
| parentTypeId | id | IN | true |

## updateInvoiceItem

- Definition: `applications/accounting/servicedef/services_invoice.xml:131`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/invoice/InvoiceServicesScript.groovy; invoke: updateInvoiceItem
- Default entity: InvoiceItem; auth: —
- Use transaction: —; timeout: —
- Permission service: checkInvoiceStatusInProgress; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | currency-precise | IN | true |
| description | description | IN | true |
| inventoryItemId | id | IN | true |
| invoiceId | id | INOUT | — |
| invoiceItemSeqId | id | INOUT | — |
| invoiceItemTypeId | id | IN | true |
| overrideGlAccountId | id | IN | true |
| overrideOrgPartyId | id | IN | true |
| parentInvoiceId | id | IN | true |
| parentInvoiceItemSeqId | id | IN | true |
| productFeatureId | id | IN | true |
| productId | id | IN | true |
| quantity | fixed-point | IN | true |
| salesOpportunityId | id | IN | true |
| taxAuthGeoId | id | IN | true |
| taxAuthPartyId | id | IN | true |
| taxAuthorityRateSeqId | id | IN | true |
| taxableFlag | indicator | IN | true |
| uomId | id | IN | true |

## updateInvoiceItemAssoc

- Definition: `applications/accounting/servicedef/services_invoice.xml:367`
- Engine: entity-auto; location: —; invoke: update
- Default entity: InvoiceItemAssoc; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | currency-amount | IN | true |
| fromDate | date-time | IN | — |
| invoiceIdFrom | id | IN | — |
| invoiceIdTo | id | IN | — |
| invoiceItemAssocTypeId | id | IN | — |
| invoiceItemSeqIdFrom | id | IN | — |
| invoiceItemSeqIdTo | id | IN | — |
| partyIdFrom | id | IN | true |
| partyIdTo | id | IN | true |
| quantity | fixed-point | IN | true |
| thruDate | date-time | IN | true |

## updateInvoiceItemAssocType

- Definition: `applications/accounting/servicedef/services_invoice.xml:525`
- Engine: entity-auto; location: —; invoke: update
- Default entity: InvoiceItemAssocType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| invoiceItemAssocTypeId | id | IN | — |
| parentTypeId | id | IN | true |

## updateInvoiceItemAttribute

- Definition: `applications/accounting/servicedef/services_invoice.xml:464`
- Engine: entity-auto; location: —; invoke: update
- Default entity: InvoiceItemAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | — |
| attrValue | value | IN | true |
| invoiceId | id | IN | — |
| invoiceItemSeqId | id | IN | — |

## updateInvoiceItemType

- Definition: `applications/accounting/servicedef/services_invoice.xml:319`
- Engine: entity-auto; location: —; invoke: update
- Default entity: InvoiceItemType; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| defaultGlAccountId | id | IN | true |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| invoiceItemTypeId | id | IN | — |
| parentTypeId | id | IN | true |

## updateInvoiceItemTypeAttr

- Definition: `applications/accounting/servicedef/services_invoice.xml:576`
- Engine: entity-auto; location: —; invoke: update
- Default entity: InvoiceItemTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| description | description | IN | true |
| invoiceItemTypeId | id | IN | — |

## updateInvoiceItemTypeMap

- Definition: `applications/accounting/servicedef/services_invoice.xml:591`
- Engine: entity-auto; location: —; invoke: update
- Default entity: InvoiceItemTypeMap; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| invoiceItemMapKey | id | IN | — |
| invoiceItemTypeId | id | IN | true |
| invoiceTypeId | id | IN | — |

## updateInvoiceTerm

- Definition: `applications/accounting/servicedef/services_invoice.xml:170`
- Engine: entity-auto; location: —; invoke: update
- Default entity: InvoiceTerm; auth: —
- Use transaction: —; timeout: —
- Permission service: acctgInvoicePermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| invoiceId | id | IN | true |
| invoiceItemSeqId | id | IN | true |
| invoiceTermId | id | IN | — |
| termDays | numeric | IN | true |
| termTypeId | id | IN | true |
| termValue | currency-amount | IN | true |
| textValue | description | IN | true |
| uomId | id | IN | true |

## updateInvoiceTermAttribute

- Definition: `applications/accounting/servicedef/services_invoice.xml:478`
- Engine: entity-auto; location: —; invoke: update
- Default entity: InvoiceTermAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | — |
| attrValue | value | IN | true |
| invoiceTermId | id | IN | — |

## updateInvoiceType

- Definition: `applications/accounting/servicedef/services_invoice.xml:606`
- Engine: entity-auto; location: —; invoke: update
- Default entity: InvoiceType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| invoiceTypeId | id | IN | — |
| parentTypeId | id | IN | true |

## updateInvoiceTypeAttr

- Definition: `applications/accounting/servicedef/services_invoice.xml:493`
- Engine: entity-auto; location: —; invoke: update
- Default entity: InvoiceTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| description | description | IN | true |
| invoiceTypeId | id | IN | — |

## updatePartyAcctgPreference

- Definition: `applications/accounting/servicedef/services_admin.xml:34`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PartyAcctgPreference; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgPrefPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| baseCurrencyUomId | id | IN | true |
| cogsMethodId | id | IN | true |
| enableAccounting | indicator | IN | true |
| errorGlJournalId | id | IN | true |
| fiscalYearStartDay | numeric | IN | true |
| fiscalYearStartMonth | numeric | IN | true |
| invoiceIdPrefix | very-short | IN | true |
| invoiceSeqCustMethId | id | IN | true |
| lastInvoiceNumber | numeric | IN | true |
| lastInvoiceRestartDate | date-time | IN | true |
| lastOrderNumber | numeric | IN | true |
| lastQuoteNumber | numeric | IN | true |
| orderIdPrefix | very-short | IN | true |
| orderSeqCustMethId | id | IN | true |
| partyId | id | IN | false |
| quoteIdPrefix | very-short | IN | true |
| quoteSeqCustMethId | id | IN | true |
| refundPaymentMethodId | id | IN | true |
| taxFormId | id | IN | true |
| useInvoiceIdForReturns | indicator | IN | true |

## updatePartyFixedAssetAssignment

- Definition: `applications/accounting/servicedef/services_fixedasset.xml:209`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PartyFixedAssetAssignment; auth: true
- Use transaction: —; timeout: —
- Permission service: fixedAssetPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| allocatedDate | date-time | IN | true |
| comments | comment | IN | true |
| fixedAssetId | id | IN | false |
| fromDate | date-time | IN | false |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |
| statusId | id | IN | true |
| thruDate | date-time | IN | true |

## updatePartyGlAccount

- Definition: `applications/accounting/servicedef/services_ledger.xml:527`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PartyGlAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glAccountId | String | IN | false |
| glAccountTypeId | id | IN | false |
| organizationPartyId | id | IN | false |
| partyId | id | IN | false |
| roleTypeId | id | IN | false |

## updatePartyRate

- Definition: `applications/accounting/servicedef/services_rate.xml:112`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/rate/RateServices.groovy; invoke: updatePartyRate
- Default entity: PartyRate; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| defaultRate | indicator | IN | true |
| fromDate | date-time | IN | true |
| organizationPartyId | String | IN | true |
| partyId | id | IN | false |
| percentageUsed | floating-point | IN | true |
| periodTypeId | String | IN | — |
| rateAmount | BigDecimal | IN | true |
| rateCurrencyUomId | String | IN | true |
| rateTypeId | id | IN | false |
| thruDate | date-time | IN | true |

## updatePartyTaxAuthInfo

- Definition: `applications/accounting/servicedef/services_tax.xml:175`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/tax/TaxAuthorityServicesScript.groovy; invoke: updatePartyTaxAuthInfo
- Default entity: PartyTaxAuthInfo; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | — |
| isExempt | indicator | IN | true |
| isNexus | indicator | IN | true |
| partyId | id | IN | — |
| partyTaxId | id-long | IN | true |
| taxAuthGeoId | id | IN | — |
| taxAuthPartyId | id | IN | — |
| thruDate | date-time | IN | true |

## updatePayPalPaymentMethod

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:227`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PayPalPaymentMethod; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| avsAddr | indicator | IN | true |
| avsZip | indicator | IN | true |
| contactMechId | id | IN | true |
| correlationId | id | IN | true |
| payerId | id | IN | true |
| payerStatus | short-varchar | IN | true |
| paymentMethodId | String | INOUT | false |
| transactionId | short-varchar | IN | true |

## updatePayment

- Definition: `applications/accounting/servicedef/services_payment.xml:40`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy; invoke: updatePayment
- Default entity: Payment; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| actualCurrencyAmount | currency-amount | IN | true |
| actualCurrencyUomId | id | IN | true |
| amount | currency-amount | IN | true |
| comments | comment | IN | true |
| currencyUomId | id | IN | true |
| effectiveDate | date-time | IN | true |
| finAccountTransId | id | IN | true |
| overrideGlAccountId | id | IN | true |
| partyIdFrom | id | IN | true |
| partyIdTo | id | IN | true |
| paymentGatewayResponseId | id | IN | true |
| paymentId | id | IN | false |
| paymentMethodId | id | IN | true |
| paymentMethodTypeId | id | IN | true |
| paymentPreferenceId | id | IN | true |
| paymentRefNum | short-varchar | IN | true |
| paymentTypeId | id | IN | true |
| roleTypeIdTo | id | IN | true |
| statusId | id | IN | true |

## updatePaymentApplication

- Definition: `applications/accounting/servicedef/services_payment.xml:81`
- Engine: java; location: org.apache.ofbiz.accounting.invoice.InvoiceServices; invoke: updatePaymentApplication
- Default entity: PaymentApplication; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amountApplied | currency-amount | IN | true |
| billingAccountId | id | IN | true |
| invoiceId | id | IN | true |
| invoiceItemSeqId | id | IN | true |
| invoiceProcessing | String | IN | true |
| overrideGlAccountId | id | IN | true |
| paymentApplicationId | id | IN | true |
| paymentId | id | IN | false |
| taxAuthGeoId | id | IN | true |
| toPaymentId | id | IN | true |

## updatePaymentApplicationDef

- Definition: `applications/accounting/servicedef/services_payment.xml:92`
- Engine: java; location: org.apache.ofbiz.accounting.invoice.InvoiceServices; invoke: updatePaymentApplicationDef
- Default entity: PaymentApplication; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amountApplied | currency-amount | IN | true |
| billingAccountId | id | IN | true |
| invoiceId | id | IN | true |
| invoiceItemSeqId | id | IN | true |
| invoiceProcessing | String | IN | true |
| overrideGlAccountId | id | IN | true |
| paymentApplicationId | id | IN | true |
| paymentId | id | IN | false |
| taxAuthGeoId | id | IN | true |
| toPaymentId | id | IN | true |

## updatePaymentAttribute

- Definition: `applications/accounting/servicedef/services_payment.xml:320`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PaymentAttribute; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrDescription | description | IN | true |
| attrName | id-long | IN | — |
| attrValue | value | IN | true |
| paymentId | id | IN | — |

## updatePaymentBudgetAllocation

- Definition: `applications/accounting/servicedef/services_payment.xml:335`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PaymentBudgetAllocation; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| amount | currency-amount | IN | true |
| budgetId | id | IN | — |
| budgetItemSeqId | id | IN | — |
| paymentId | id | IN | — |

## updatePaymentContent

- Definition: `applications/accounting/servicedef/services_payment.xml:263`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy; invoke: updatePaymentContent
- Default entity: PaymentContent; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contentId | id | IN | false |
| fromDate | date-time | IN | false |
| paymentContentTypeId | id | IN | false |
| paymentId | id | IN | false |
| thruDate | date-time | IN | true |

## updatePaymentContentType

- Definition: `applications/accounting/servicedef/services_payment.xml:350`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PaymentContentType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| paymentContentTypeId | id | IN | — |

## updatePaymentGatewayConfig

- Definition: `applications/accounting/servicedef/services_paymentgateway.xml:33`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PaymentGatewayConfig; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| paymentGatewayConfigId | id | IN | false |
| paymentGatewayConfigTypeId | id | IN | true |

## updatePaymentGatewayConfigType

- Definition: `applications/accounting/servicedef/services_paymentgateway.xml:43`
- Engine: simple; location: component://accounting/minilang/payment/PaymentGatewayConfigServices.xml; invoke: updatePaymentGatewayConfigType
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## updatePaymentGroup

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:845`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PaymentGroup; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| paymentGroupId | id | IN | false |
| paymentGroupName | name | IN | true |
| paymentGroupTypeId | id | IN | true |

## updatePaymentGroupMember

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:863`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PaymentGroupMember; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| paymentGroupId | id | IN | false |
| paymentId | id | IN | false |
| sequenceNum | numeric | IN | true |
| thruDate | date-time | IN | true |

## updatePaymentGroupType

- Definition: `applications/accounting/servicedef/services_payment.xml:365`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PaymentGroupType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| paymentGroupTypeId | id | IN | — |

## updatePaymentMethodAddress

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:210`
- Engine: simple; location: component://accounting/minilang/payment/PaymentMethodServices.xml; invoke: updatePaymentMethodAddress
- Default entity: —; auth: true
- Use transaction: false; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| contactMechId | String | IN | false |
| oldContactMechId | String | IN | false |
| partyId | String | IN | true |

## updatePaymentMethodType

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:827`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PaymentMethodType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| defaultGlAccountId | id | IN | true |
| description | description | IN | true |
| paymentMethodTypeId | id | IN | false |

## updatePaymentOnFinAccTransStatusSetToCancel

- Definition: `applications/accounting/servicedef/services_finaccount.xml:137`
- Engine: simple; location: component://accounting/minilang/finaccount/FinAccountServices.xml; invoke: updatePaymentOnFinAccTransStatusSetToCancel
- Default entity: —; auth: —
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountTransId | String | IN | true |

## updatePaymentType

- Definition: `applications/accounting/servicedef/services_payment.xml:380`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PaymentType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| paymentTypeId | id | IN | — |

## updatePaymentTypeAttr

- Definition: `applications/accounting/servicedef/services_payment.xml:395`
- Engine: entity-auto; location: —; invoke: update
- Default entity: PaymentTypeAttr; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| attrName | id-long | IN | — |
| description | description | IN | true |
| paymentTypeId | id | IN | — |

## updateProductAverageCost

- Definition: `applications/accounting/servicedef/services_cost.xml:78`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ProductAverageCost; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgCostPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| averageCost | fixed-point | IN | true |
| facilityId | id | IN | false |
| fromDate | date-time | IN | false |
| organizationPartyId | id | IN | false |
| productAverageCostTypeId | id | IN | false |
| productId | id | IN | false |
| thruDate | date-time | IN | true |

## updateProductAverageCostOnReceiveInventory

- Definition: `applications/accounting/servicedef/services_cost.xml:91`
- Engine: groovy; location: component://product/src/main/groovy/org/apache/ofbiz/product/product/cost/CostServices.groovy; invoke: updateProductAverageCostOnReceiveInventory
- Default entity: ProductAverageCost; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgCostPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| facilityId | String | IN | false |
| inventoryItemId | String | IN | false |
| productId | String | IN | false |
| quantityAccepted | BigDecimal | IN | false |

## updateProductAverageCostType

- Definition: `applications/accounting/servicedef/services_ledger.xml:931`
- Engine: entity-auto; location: —; invoke: update
- Default entity: ProductAverageCostType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| hasTable | indicator | IN | true |
| parentTypeId | id | IN | true |
| productAverageCostTypeId | id | IN | false |

## updateRateAmount

- Definition: `applications/accounting/servicedef/services_rate.xml:27`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/rate/RateServices.groovy; invoke: updateRateAmount
- Default entity: RateAmount; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: CREATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| emplPositionTypeId | id | IN | true |
| fromDate | date-time | IN | true |
| partyId | id | IN | true |
| periodTypeId | id | IN | true |
| rateAmount | currency-amount | IN | false |
| rateCurrencyUomId | id | IN | true |
| rateTypeId | id | IN | false |
| thruDate | date-time | IN | true |
| workEffortId | id | IN | true |

## updateRateType

- Definition: `applications/accounting/servicedef/services_rate.xml:145`
- Engine: entity-auto; location: —; invoke: update
- Default entity: RateType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| rateTypeId | id | IN | false |

## updateSettlementTerm

- Definition: `applications/accounting/servicedef/services_ledger.xml:915`
- Engine: entity-auto; location: —; invoke: update
- Default entity: SettlementTerm; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| settlementTermId | id | IN | false |
| termName | name | IN | true |
| termValue | numeric | IN | true |
| uomId | id | IN | true |

## updateSimpleTextContentForInvoice

- Definition: `applications/accounting/servicedef/services_invoice.xml:439`
- Engine: group; location: —; invoke: —
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## updateTaxAuthority

- Definition: `applications/accounting/servicedef/services_tax.xml:77`
- Engine: entity-auto; location: —; invoke: update
- Default entity: TaxAuthority; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| includeTaxInPrice | indicator | IN | true |
| requireTaxIdForExemption | indicator | IN | true |
| taxAuthGeoId | id | IN | false |
| taxAuthPartyId | id | IN | false |
| taxIdFormatPattern | long-varchar | IN | true |

## updateTaxAuthorityAssoc

- Definition: `applications/accounting/servicedef/services_tax.xml:97`
- Engine: entity-auto; location: —; invoke: update
- Default entity: TaxAuthorityAssoc; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| fromDate | date-time | IN | false |
| taxAuthGeoId | id | IN | false |
| taxAuthPartyId | id | IN | false |
| taxAuthorityAssocTypeId | id | IN | true |
| thruDate | date-time | IN | true |
| toTaxAuthGeoId | id | IN | false |
| toTaxAuthPartyId | id | IN | false |

## updateTaxAuthorityAssocType

- Definition: `applications/accounting/servicedef/services_tax.xml:203`
- Engine: entity-auto; location: —; invoke: update
- Default entity: TaxAuthorityAssocType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| taxAuthorityAssocTypeId | id | IN | false |

## updateTaxAuthorityCategory

- Definition: `applications/accounting/servicedef/services_tax.xml:116`
- Engine: entity-auto; location: —; invoke: update
- Default entity: TaxAuthorityCategory; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| productCategoryId | id | IN | false |
| taxAuthGeoId | id | IN | false |
| taxAuthPartyId | id | IN | false |

## updateTaxAuthorityGlAccount

- Definition: `applications/accounting/servicedef/services_tax.xml:135`
- Engine: entity-auto; location: —; invoke: update
- Default entity: TaxAuthorityGlAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glAccountId | id | IN | true |
| organizationPartyId | id | IN | false |
| taxAuthGeoId | id | IN | false |
| taxAuthPartyId | id | IN | false |

## updateTaxAuthorityRateProduct

- Definition: `applications/accounting/servicedef/services_tax.xml:154`
- Engine: entity-auto; location: —; invoke: update
- Default entity: TaxAuthorityRateProduct; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgBasePermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| fromDate | date-time | IN | true |
| isTaxInShippingPrice | indicator | IN | true |
| minItemPrice | currency-amount | IN | true |
| minPurchase | currency-amount | IN | true |
| productCategoryId | id | IN | true |
| productStoreId | id | IN | true |
| taxAuthGeoId | id | IN | true |
| taxAuthPartyId | id | IN | true |
| taxAuthorityRateSeqId | id | IN | false |
| taxAuthorityRateTypeId | id | IN | true |
| taxPercentage | fixed-point | IN | true |
| taxPromotions | indicator | IN | true |
| taxShipping | indicator | IN | true |
| thruDate | date-time | IN | true |
| titleTransferEnumId | id | IN | true |

## updateTaxAuthorityRateType

- Definition: `applications/accounting/servicedef/services_tax.xml:219`
- Engine: entity-auto; location: —; invoke: update
- Default entity: TaxAuthorityRateType; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| description | description | IN | true |
| taxAuthorityRateTypeId | id | IN | false |

## updateVarianceReasonGlAccount

- Definition: `applications/accounting/servicedef/services_ledger.xml:399`
- Engine: entity-auto; location: —; invoke: update
- Default entity: VarianceReasonGlAccount; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| glAccountId | id | IN | false |
| organizationPartyId | id | IN | false |
| varianceReasonId | id | IN | false |

## updateWorkEffortCostCalc

- Definition: `applications/accounting/servicedef/services_cost.xml:56`
- Engine: entity-auto; location: —; invoke: update
- Default entity: WorkEffortCostCalc; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgCostPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| costComponentCalcId | id | IN | true |
| costComponentTypeId | id | IN | false |
| fromDate | date-time | IN | false |
| thruDate | date-time | IN | true |
| workEffortId | id | IN | false |

## uploadAgreementContentFile

- Definition: `applications/accounting/servicedef/services_agreement.xml:269`
- Engine: group; location: —; invoke: —
- Default entity: —; auth: —
- Use transaction: —; timeout: 300
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |

## verifyCreditCard

- Definition: `applications/accounting/servicedef/services_paymentmethod.xml:797`
- Engine: java; location: org.apache.ofbiz.accounting.payment.PaymentGatewayServices; invoke: verifyCreditCard
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: —; main action: —
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| mode | String | IN | false |
| oldPaymentMethodId | String | IN | true |
| paymentMethodId | String | IN | false |
| productStoreId | String | IN | true |

## voidPayment

- Definition: `applications/accounting/servicedef/services_payment.xml:145`
- Engine: groovy; location: component://accounting/src/main/groovy/org/apache/ofbiz/accounting/payment/PaymentServices.groovy; invoke: voidPayment
- Default entity: —; auth: true
- Use transaction: —; timeout: —
- Permission service: acctgPaymentPermissionCheck; main action: UPDATE
- Implements: —

| Name | Type | Mode | Optional |
| --- | --- | --- | --- |
| finAccountTransId | String | OUT | true |
| paymentId | String | IN | false |
| statusId | String | OUT | true |
