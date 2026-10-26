# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

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
