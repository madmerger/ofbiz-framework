import unittest
from pathlib import Path

from ofbiz_inventory import build_inventory


REPOSITORY = Path(__file__).resolve().parents[2]


class HumanResInventoryTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.inventory = build_inventory(
            REPOSITORY,
            "applications/humanres",
            "applications/datamodel/entitydef/humanres-entitymodel.xml",
        )

    def test_entity_and_view_entity_counts(self):
        self.assertEqual(self.inventory["counts"]["entities"], 41)
        self.assertEqual(self.inventory["counts"]["view_entities"], 5)

    def test_service_counts_and_expanded_party_qual_params(self):
        self.assertEqual(self.inventory["counts"]["services"], 131)
        self.assertEqual(self.inventory["counts"]["entity_auto_services"], 121)
        service = next(item for item in self.inventory["services"] if item["name"] == "createPartyQual")
        names = {param["name"] for param in service["params"]}
        self.assertTrue({"partyId", "partyQualTypeId", "fromDate"}.issubset(names))

    def test_controller_counts_and_table_name(self):
        self.assertEqual(self.inventory["counts"]["requests"], 232)
        self.assertEqual(self.inventory["counts"]["view_maps"], 98)
        entity = next(item for item in self.inventory["entities"] if item["name"] == "EmplPosition")
        self.assertEqual(entity["table"], "EMPL_POSITION")


if __name__ == "__main__":
    unittest.main()
