/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.apache.ofbiz.migration.manufacturing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
@WithMockUser(authorities = {"OFBTOOLS_VIEW", "MANUFACTURING_ADMIN", "MANUFACTURING_SENSITIVE_VIEW"})
class SimpleServicesAcceptanceTest {
    @Autowired
    private MigrationServiceDispatcher dispatcher;
    @Autowired
    private EntityRegistry entities;

    @Test
    void ac03_createBomAssocValidatesRequiredFieldsAndDuplicates() {
        Timestamp from = new Timestamp(System.currentTimeMillis());
        Map<String, Object> input = Map.of(
                "productId", "MFG_FG", "productIdTo", "MFG_RAW1",
                "productAssocTypeId", "MANUF_COMPONENT", "fromDate", from);
        call("createBOMAssoc", input);
        MigrationException duplicate = assertThrows(MigrationException.class, () -> call("createBOMAssoc", input));
        assertEquals(409, duplicate.getStatus().value());

        MigrationException selfReference = assertThrows(MigrationException.class, () -> call("createBOMAssoc", Map.of(
                "productId", "MFG_FG", "productIdTo", "MFG_FG", "productAssocTypeId", "MANUF_COMPONENT")));
        assertEquals(400, selfReference.getStatus().value());
        MigrationException missingType = assertThrows(MigrationException.class, () -> call("createBOMAssoc", Map.of(
                "productId", "MFG_FG", "productIdTo", "MFG_RAW2")));
        assertEquals(400, missingType.getStatus().value());
        MigrationException undeclared = assertThrows(MigrationException.class, () -> call("createBOMAssoc", Map.of(
                "productId", "MFG_FG", "productIdTo", "MFG_RAW2",
                "productAssocTypeId", "MANUF_COMPONENT", "createdStamp", from)));
        assertEquals(400, undeclared.getStatus().value());
        assertFalse(productsContains("MFG_FG", "MFG_RAW2", from));
    }

    @Test
    void ac03_createBomAssocDetectsCyclesAndAllowsDiamondsAndDateVersions() {
        Timestamp firstDate = Timestamp.from(Instant.parse("2020-01-01T00:00:00Z"));
        String type = "MANUF_COMPONENT";
        call("createBOMAssoc", Map.of(
                "productId", "AC03_CYCLE_A", "productIdTo", "AC03_CYCLE_B",
                "productAssocTypeId", type, "fromDate", firstDate));
        MigrationException reverseCycle = assertThrows(MigrationException.class, () -> call("createBOMAssoc", Map.of(
                "productId", "AC03_CYCLE_B", "productIdTo", "AC03_CYCLE_A",
                "productAssocTypeId", type, "fromDate", firstDate)));
        assertEquals(400, reverseCycle.getStatus().value());

        call("createBOMAssoc", Map.of(
                "productId", "AC03_CHAIN_A", "productIdTo", "AC03_CHAIN_B",
                "productAssocTypeId", type, "fromDate", firstDate));
        call("createBOMAssoc", Map.of(
                "productId", "AC03_CHAIN_B", "productIdTo", "AC03_CHAIN_C",
                "productAssocTypeId", type, "fromDate", firstDate));
        MigrationException multiHopCycle = assertThrows(MigrationException.class, () -> call("createBOMAssoc", Map.of(
                "productId", "AC03_CHAIN_C", "productIdTo", "AC03_CHAIN_A",
                "productAssocTypeId", type, "fromDate", firstDate)));
        assertEquals(400, multiHopCycle.getStatus().value());

        call("createBOMAssoc", Map.of(
                "productId", "AC03_DIAMOND_A", "productIdTo", "AC03_DIAMOND_C",
                "productAssocTypeId", type, "fromDate", firstDate));
        call("createBOMAssoc", Map.of(
                "productId", "AC03_DIAMOND_C", "productIdTo", "AC03_DIAMOND_B",
                "productAssocTypeId", type, "fromDate", firstDate));
        call("createBOMAssoc", Map.of(
                "productId", "AC03_DIAMOND_A", "productIdTo", "AC03_DIAMOND_B",
                "productAssocTypeId", type, "fromDate", firstDate));
        assertTrue(productsContains("AC03_DIAMOND_A", "AC03_DIAMOND_B", firstDate));

        call("createBOMAssoc", Map.of(
                "productId", "AC03_VERSION_A", "productIdTo", "AC03_VERSION_B",
                "productAssocTypeId", type, "fromDate", firstDate));
        Timestamp secondDate = Timestamp.from(Instant.parse("2020-01-02T00:00:00Z"));
        call("createBOMAssoc", Map.of(
                "productId", "AC03_VERSION_A", "productIdTo", "AC03_VERSION_B",
                "productAssocTypeId", type, "fromDate", secondDate));
        assertEquals(2, entities.findByEquality(entities.entityClass("ProductAssoc"), Map.of(
                "productId", "AC03_VERSION_A", "productIdTo", "AC03_VERSION_B",
                "productAssocTypeId", type)).size());
    }

    @Test
    void ac03_searchDuplicatedAncestorReturnsTheAssociationOrEmpty() {
        Timestamp fromDate = Timestamp.from(Instant.parse("2020-01-01T00:00:00Z"));
        Map<String, Object> association = call("createBOMAssoc", Map.of(
                "productId", "AC03_SEARCH_A", "productIdTo", "AC03_SEARCH_B",
                "productAssocTypeId", "MANUF_COMPONENT", "fromDate", fromDate));

        Map<String, Object> duplicate = call("searchDuplicatedAncestor", Map.of(
                "productId", "AC03_SEARCH_B", "productIdTo", "AC03_SEARCH_A",
                "productAssocTypeId", "MANUF_COMPONENT", "fromDate", fromDate));
        @SuppressWarnings("unchecked")
        Map<String, Object> duplicatedAssociation = (Map<String, Object>) duplicate.get("duplicatedProductAssoc");
        assertEquals(association.get("productId"), duplicatedAssociation.get("productId"));
        assertEquals(association.get("productIdTo"), duplicatedAssociation.get("productIdTo"));
        assertEquals(association.get("productAssocTypeId"), duplicatedAssociation.get("productAssocTypeId"));
        assertEquals(association.get("fromDate"), duplicatedAssociation.get("fromDate"));

        Map<String, Object> notFound = call("searchDuplicatedAncestor", Map.of(
                "productId", "AC03_SEARCH_ISOLATED", "productIdTo", "AC03_SEARCH_A",
                "productAssocTypeId", "MANUF_COMPONENT", "fromDate", fromDate));
        assertEquals(Map.of(), notFound.get("duplicatedProductAssoc"));
    }

    @Test
    void ac03_copyBomAssocsCopiesOnlyActiveNonduplicateRows() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp old = new Timestamp(now.getTime() - 10_000);
        Timestamp expired = new Timestamp(now.getTime() - 1_000);
        call("createBOMAssoc", Map.of(
                "productId", "MFG_FG", "productIdTo", "MFG_RAW1",
                "productAssocTypeId", "MANUF_COMPONENT", "fromDate", now));
        call("createBOMAssoc", Map.of(
                "productId", "MFG_FG", "productIdTo", "MFG_RAW2",
                "productAssocTypeId", "MANUF_COMPONENT", "fromDate", now));
        call("createBOMAssoc", Map.of(
                "productId", "MFG_FG", "productIdTo", "MFG_RAW2",
                "productAssocTypeId", "MANUF_COMPONENT", "fromDate", old, "thruDate", expired));

        Map<String, Object> result = call("copyBOMAssocs", Map.of(
                "productId", "MFG_FG", "copyToProductId", "MFG_RAW1",
                "productAssocTypeId", "MANUF_COMPONENT"));
        assertEquals(1, result.get("copied"));
        Map<String, Object> secondCopy = call("copyBOMAssocs", Map.of(
                "productId", "MFG_FG", "copyToProductId", "MFG_RAW1",
                "productAssocTypeId", "MANUF_COMPONENT"));
        assertEquals(0, secondCopy.get("copied"));
        assertFalse(entities.findByEquality(entities.entityClass("ProductAssoc"),
                Map.of("productId", "MFG_RAW1", "productIdTo", "MFG_RAW2")).isEmpty());
        assertFalse(productsContains("MFG_RAW1", "MFG_RAW1", now));

        Map<String, Object> cyclicCopy = call("copyBOMAssocs", Map.of(
                "productId", "MFG_FG", "copyToProductId", "MFG_RAW2",
                "productAssocTypeId", "MANUF_COMPONENT"));
        assertEquals(0, cyclicCopy.get("copied"));
        assertFalse(productsContains("MFG_RAW2", "MFG_RAW1", now));
    }

    @Test
    void ac03_addProductManufacturingRuleGeneratesIdAndRejectsDuplicates() {
        Map<String, Object> created = call("addProductManufacturingRule", Map.of(
                "productId", "MFG_FG", "productIdIn", "MFG_RAW1"));
        String ruleId = String.valueOf(created.get("ruleId"));
        assertFalse(ruleId.isBlank());
        assertTrue(entities.findByPrimaryKey(entities.entityClass("ProductManufacturingRule"),
                Map.of("ruleId", ruleId)) != null);
        MigrationException duplicate = assertThrows(MigrationException.class, () -> call(
                "addProductManufacturingRule", Map.of(
                        "ruleId", ruleId, "productId", "MFG_FG", "productIdIn", "MFG_RAW1")));
        assertEquals(409, duplicate.getStatus().value());
    }

    @Test
    void ac03_updateProductManufacturingRuleUpdatesAndReturnsNotFound() {
        Map<String, Object> created = call("addProductManufacturingRule", Map.of(
                "productId", "MFG_FG", "productIdIn", "MFG_RAW1"));
        String ruleId = String.valueOf(created.get("ruleId"));
        Map<String, Object> updated = call("updateProductManufacturingRule", Map.of(
                "ruleId", ruleId, "productIdIn", "MFG_RAW2", "quantity", BigDecimal.TEN));
        assertEquals(0, new BigDecimal("10").compareTo(new BigDecimal(updated.get("quantity").toString())));

        MigrationException notFound = assertThrows(MigrationException.class, () -> call(
                "updateProductManufacturingRule", Map.of(
                        "ruleId", "AC03_MISSING_RULE", "productIdIn", "MFG_RAW1")));
        assertEquals(404, notFound.getStatus().value());
    }

    @Test
    void ac03_deleteProductManufacturingRuleSucceedsWhenAbsent() {
        call("deleteProductManufacturingRule", Map.of("ruleId", "AC03_NONEXISTENT_RULE"));
        Map<String, Object> created = call("addProductManufacturingRule", Map.of(
                "productId", "MFG_FG", "productIdIn", "MFG_RAW1"));
        String ruleId = String.valueOf(created.get("ruleId"));
        call("deleteProductManufacturingRule", Map.of("ruleId", ruleId));
        assertTrue(entities.findByPrimaryKey(entities.entityClass("ProductManufacturingRule"),
                Map.of("ruleId", ruleId)) == null);
    }

    @Test
    void ac03_exampleComponentFormulaCalculatesQuantity() {
        Map<String, Object> result = call("exampleComponentFormula",
                Map.of("arguments", Map.of("neededQuantity", new BigDecimal("3"))));
        assertEquals(new BigDecimal("30"), result.get("quantity"));
    }

    @Test
    void ac03_linearComponentFormulaRoundsUpAndRejectsZeroWidth() {
        Map<String, Object> divisible = call("linearComponentFormula",
                Map.of("arguments", Map.of("neededQuantity", BigDecimal.TEN,
                        "amount", new BigDecimal("2"), "width", new BigDecimal("5"))));
        assertEquals(new BigDecimal("4"), divisible.get("quantity"));
        Map<String, Object> rounded = call("linearComponentFormula",
                Map.of("arguments", Map.of("neededQuantity", new BigDecimal("2"),
                        "amount", new BigDecimal("5"), "width", new BigDecimal("3"))));
        assertEquals(new BigDecimal("4"), rounded.get("quantity"));
        MigrationException zeroWidth = assertThrows(MigrationException.class, () -> call(
                "linearComponentFormula", Map.of("arguments", Map.of(
                        "neededQuantity", BigDecimal.ONE, "amount", BigDecimal.ONE, "width", BigDecimal.ZERO))));
        assertEquals(400, zeroWidth.getStatus().value());
    }

    @Test
    void ac03_exampleTaskFormulaCalculatesTime() {
        Map<String, Object> result = call("exampleTaskFormula", Map.of(
                "arguments", Map.of("workEffort", Map.of("estimatedMilliSeconds", new BigDecimal("1000")),
                        "quantity", new BigDecimal("2"))));
        assertEquals(new BigDecimal("20000"), result.get("totalTime"));
    }

    private boolean productsContains(String productId, String productIdTo, Timestamp fromDate) {
        return entities.findByPrimaryKey(entities.entityClass("ProductAssoc"), Map.of(
                "productId", productId, "productIdTo", productIdTo,
                "productAssocTypeId", "MANUF_COMPONENT", "fromDate", fromDate)) != null;
    }

    private Map<String, Object> call(String service, Map<String, Object> input) {
        return dispatcher.execute("manufacturing", service, input);
    }
}
