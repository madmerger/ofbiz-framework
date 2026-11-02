"""Regression checks for generated original-rule coverage."""

import json
import unittest

from eca_catalog import ROOT, generate, report


class EcaCatalogTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.disabled = json.loads((ROOT / 'migration/springboot-common/src/main/resources/eca-disabled-rules.json').read_text())
        cls.catalog = generate(cls.disabled)

    def test_complete_original_rule_inventory(self):
        rules = self.catalog['rules']
        self.assertEqual(348, len(rules))
        self.assertEqual(314, sum(r['kind'] == 'service' for r in rules))
        self.assertEqual(34, sum(r['kind'] == 'entity' for r in rules))
        self.assertEqual(len(rules), len({r['id'] for r in rules}))

    def test_explicit_exclusions_and_unsupported_condition_services(self):
        for rule in self.catalog['rules']:
            if rule['id'] in self.disabled:
                self.assertEqual('disabled-hand-wired', rule['status'])
                self.assertTrue(rule['disabledReason'])
        self.assertTrue(any(r['status'] == 'unsupported-condition' for r in self.catalog['rules']))

    def test_generation_is_deterministic_and_report_covers_every_rule(self):
        self.assertEqual(self.catalog, generate(self.disabled))
        markdown = report(self.catalog)
        for rule in self.catalog['rules']:
            self.assertIn('`' + rule['id'] + '`', markdown)

    def test_invalid_exclusions_fail_instead_of_silently_expiring(self):
        with self.assertRaisesRegex(ValueError, 'Unknown disabled rule IDs'):
            generate({'party/servicedef/secas.xml#99999': 'Invalid rule'})

    def test_inherited_and_entity_auto_action_inputs(self):
        params = self.catalog['parameters']
        self.assertEqual('IN', params['postAcctgTrans']['acctgTransId'])
        self.assertIn('partyId', params['ensureNaPartyRole'])
        self.assertIn('budgetId', params['createBudgetStatus'])


if __name__ == '__main__':
    unittest.main()
