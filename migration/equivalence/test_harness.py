import unittest

from harness import assertions, resolve, capture_created_rows


class HarnessTest(unittest.TestCase):
    def test_captures_a_unique_created_row_and_refuses_empty_or_ambiguous_deltas(self):
        specification = {'invoice': {'invoice_id': 'invoice'}}
        before = {'invoice': [{'invoice_id': 'existing'}]}
        after = {'invoice': [{'invoice_id': 'existing'}, {'invoice_id': 'CI1'}]}
        self.assertEqual({'invoice': 'CI1'}, capture_created_rows(before, after, specification))
        self.assertEqual({}, capture_created_rows(before, before, specification))
        after['invoice'].append({'invoice_id': 'CI2'})
        self.assertEqual({}, capture_created_rows(before, after, specification))

    def test_nested_references_use_each_runners_captured_identifiers(self):
        values = {'orderId': '$order', 'items': [{'partyId': '$person'}]}
        self.assertEqual({'orderId': 'WSCO10000', 'items': [{'partyId': '10000'}]},
                         resolve(values, {'order': 'WSCO10000', 'person': '10000'}))
        self.assertEqual({'orderId': '10001', 'items': [{'partyId': '10002'}]},
                         resolve(values, {'order': '10001', 'person': '10002'}))

    def test_empty_and_literal_values_are_preserved(self):
        self.assertEqual({}, resolve({}, {}))
        self.assertEqual(['', None, 'ORDER_COMPLETED'], resolve(['', None, 'ORDER_COMPLETED'], {}))

    def test_missing_references_fail_instead_of_sending_literal_placeholders(self):
        with self.assertRaises(KeyError):
            resolve({'orderId': '$unknown'}, {})

    def test_business_assertions_resolve_references_before_matching_rows(self):
        step = {'assertRows': [{'table': 'order_header', 'where': {'order_id': '$order',
                                                                  'status_id': 'ORDER_COMPLETED'}}]}
        result = {'httpStatus': 200, 'response': {'responseMessage': 'success'}}
        self.assertEqual([], assertions(step, {'order_header': [{'order_id': 'WSCO10000',
                            'status_id': 'ORDER_COMPLETED'}]}, {'order': 'WSCO10000'}, result))
        self.assertTrue(assertions(step, {'order_header': []}, {'order': 'WSCO10000'}, result))

    def test_failed_positive_service_is_not_considered_a_valid_equivalence_case(self):
        result = {'httpStatus': 200, 'response': {'responseMessage': 'error'}}
        self.assertEqual(['Expected success'], assertions({}, {}, {}, result))
        self.assertEqual([], assertions({'expect': 'error'}, {}, {}, result))


if __name__ == '__main__':
    unittest.main()
