import unittest
from decimal import Decimal

from compare import Normalizer, bind_created_keys, delta, difference, mismatches, response_contract


class ComparisonTest(unittest.TestCase):
    def setUp(self):
        self.normalizer = Normalizer()

    def test_generated_identifiers_are_scoped_by_field_and_follow_foreign_keys(self):
        self.normalizer.bind('partyId', '100', '200', 'customer')
        self.assertEqual(self.normalizer.normalize({'partyIdFrom': '100'}, 'old'),
                         self.normalizer.normalize({'partyIdFrom': '200'}, 'new'))
        self.assertNotEqual(self.normalizer.normalize({'orderId': '100'}, 'old'),
                            self.normalizer.normalize({'orderId': '200'}, 'new'))

    def test_decimal_scale_and_row_order_are_not_business_differences(self):
        old = [{'amount': Decimal('12.00')}, {'amount': Decimal('5.0')}]
        new = [{'amount': Decimal('5')}, {'amount': Decimal('12')}]
        self.assertEqual(self.normalizer.normalize(old, 'old'), self.normalizer.normalize(new, 'new'))

    def test_timestamp_presence_does_not_hide_missing_values(self):
        old = self.normalizer.normalize({'createdStamp': '2025-01-01', 'fromDate': '2025-01-01'}, 'old')
        new = self.normalizer.normalize({'createdStamp': '2026-01-01', 'fromDate': '2026-01-01'}, 'new')
        self.assertEqual(old, new)
        self.assertNotEqual(old, self.normalizer.normalize({'createdStamp': None, 'fromDate': None}, 'new'))

    def test_supplied_shipment_dates_remain_observable(self):
        self.assertNotEqual(self.normalizer.normalize({'estimatedShipDate': '2025-01-01'}, 'old'),
                            self.normalizer.normalize({'estimatedShipDate': '2026-01-01'}, 'new'))

    def test_business_values_are_never_normalized(self):
        for field, old, new in [('statusId', 'ORDER_APPROVED', 'ORDER_COMPLETED'),
                                ('roleTypeId', '_NA_', 'CUSTOMER'), ('isPosted', 'N', 'Y'),
                                ('amount', Decimal('1'), Decimal('2'))]:
            with self.subTest(field=field):
                self.assertTrue(mismatches(self.normalizer.normalize({field: old}, 'old'),
                                           self.normalizer.normalize({field: new}, 'new')))

    def test_empty_snapshots_have_empty_deltas(self):
        self.assertEqual({'party': {'removed': [], 'added': []}},
                         delta({'party': []}, {'party': []}, self.normalizer, 'old'))

    def test_row_multiplicity_is_preserved(self):
        self.assertEqual({'removed': [], 'added': [{'id': '1'}]},
                         difference([{'id': '1'}], [{'id': '1'}, {'id': '1'}]))

    def test_error_response_details_and_missing_fields_remain_differences(self):
        self.assertTrue(mismatches({'responseMessage': 'error', 'errorMessage': 'permission'},
                                   {'responseMessage': 'error', 'errorMessage': 'duplicate'}))
        self.assertTrue(mismatches({}, {'missing': None}))

    def test_empty_and_matching_response_maps_match(self):
        self.assertEqual([], mismatches({}, {}))
        self.assertEqual([], mismatches({'responseMessage': 'success'}, {'responseMessage': 'success'}))

    def test_declared_outputs_are_compared_but_extensions_and_wording_are_information(self):
        compared, info = response_contract({'old': {'id': '1', 'successMessage': 'Done'},
                                           'new': {'id': '1', 'firstName': 'Extra', 'successMessage': 'OK'}},
                                          {'id': 'OUT', 'firstName': 'IN'}, {'old': True, 'new': True})
        self.assertEqual(compared['old'], compared['new'])
        self.assertEqual('Extra', info['new']['firstName'])
        compared, _ = response_contract({'old': {'id': '1'}, 'new': {}}, {'id': 'INOUT'},
                                        {'old': True, 'new': True})
        self.assertTrue(mismatches(compared['old'], compared['new']))

    def test_error_classification_and_shared_codes_remain_observable(self):
        for old, new, success, equal in [
                ({'errorMessage': 'Denied'}, {'errorMessage': 'Access refused'}, {'old': False, 'new': False}, True),
                ({'errorCode': 'DENIED'}, {'errorCode': 'INVALID'}, {'old': False, 'new': False}, False),
                ({}, {}, {'old': False, 'new': True}, False),
                ({}, {}, {'old': True, 'new': True}, True)]:
            compared, _ = response_contract({'old': old, 'new': new}, {}, success)
            self.assertEqual(equal, compared['old'] == compared['new'])

    def test_updates_never_treat_existing_primary_keys_as_generated(self):
        before = {side: {'party': [{'party_id': 'DemoCustomer', 'description': None}]} for side in ['old', 'new']}
        after = {side: {'party': [{'party_id': 'DemoCustomer', 'description': 'Changed'}]} for side in ['old', 'new']}
        bind_created_keys(self.normalizer, 'update', 'party', ['party_id'], before, after)
        self.assertEqual({'old': {}, 'new': {}}, self.normalizer.ids)

    def test_multiple_created_keys_only_align_unambiguous_business_rows(self):
        before = {side: {'history': []} for side in ['old', 'new']}
        after = {'old': {'history': [{'id': '1', 'status': 'A'}, {'id': '2', 'status': 'B'}]},
                 'new': {'history': [{'id': '4', 'status': 'B'}, {'id': '3', 'status': 'A'}]}}
        bind_created_keys(self.normalizer, 'create', 'history', ['id'], before, after)
        self.assertEqual(self.normalizer.normalize(after['old'], 'old'),
                         self.normalizer.normalize(after['new'], 'new'))


if __name__ == '__main__':
    unittest.main()
