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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.apache.ofbiz.migration.humanres;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import jakarta.persistence.EntityManager;
import org.apache.ofbiz.migration.humanres.adapter.local.model.PartyRelationshipRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest(classes = {HumanResMigrationApplication.class, TestUsersConfiguration.class})
class HumanResBusinessServicesTest {
    @Autowired
    private HumanResBusinessServices businessServices;

    @Autowired
    private HumanResServiceDispatcher serviceDispatcher;

    @Autowired
    private EntityAutoService entityAutoService;

    @Autowired
    private EntityRegistry entityRegistry;

    @Autowired
    private EntityManager entityManager;

    @MockitoSpyBean
    private PartyPort partyPort;

    @MockitoSpyBean
    private AccountingPort accountingPort;

    @MockitoSpyBean
    private WorkEffortPort workEffortPort;

    @Test
    void ac20_createEmploymentCreatesEmploymentAndPayHistoryAtomically() {
        Map<String, Object> input = Map.of(
                "partyIdFrom", "AC20_COMPANY",
                "partyIdTo", "AC20_EMPLOYEE",
                "roleTypeIdFrom", "INTERNAL_ORGANIZATIO",
                "roleTypeIdTo", "EMPLOYEE",
                "amount", new BigDecimal("50000.00"),
                "payGradeId", "AC20_GRADE",
                "salaryStepSeqId", "01");
        Map<String, Object> result = businessServices.execute("createEmployment", input);
        assertEquals("AC20_EMPLOYEE", result.get("partyIdTo"));
        assertEquals(1, entityAutoService.findEntities(
                "Employment", Map.of("partyIdTo", "AC20_EMPLOYEE")).size());
        assertEquals(1, entityAutoService.findEntities(
                "PayHistory", Map.of("partyIdTo", "AC20_EMPLOYEE")).size());
        verify(partyPort).ensureRole("AC20_EMPLOYEE", "EMPLOYEE");

        Timestamp rollbackStart = Timestamp.valueOf("2026-02-01 00:00:00");
        Map<String, Object> existingPayHistory = Map.of(
                "partyIdFrom", "AC20_COMPANY",
                "partyIdTo", "AC20_ROLLBACK",
                "roleTypeIdFrom", "INTERNAL_ORGANIZATIO",
                "roleTypeIdTo", "EMPLOYEE",
                "emplFromDate", rollbackStart,
                "fromDate", rollbackStart,
                "amount", new BigDecimal("25000.00"));
        entityAutoService.createEntity("PayHistory", existingPayHistory);
        Map<String, Object> failedEmployment = new HashMap<>(existingPayHistory);
        failedEmployment.put("payGradeId", "AC20_GRADE");
        HumanResException error = assertThrows(HumanResException.class,
                () -> businessServices.execute("createEmployment", failedEmployment));
        assertEquals(409, error.getStatus().value());
        assertEquals(0, entityAutoService.findEntities(
                "Employment", Map.of("partyIdTo", "AC20_ROLLBACK")).size());
    }

    @Test
    void ac22_reportingStructCannotReportToItself() {
        HumanResException error = assertThrows(HumanResException.class,
                () -> businessServices.execute("createEmplPositionReportingStruct", Map.of(
                        "emplPositionIdManagedBy", "SAME", "emplPositionIdReportingTo", "SAME")));
        assertEquals(400, error.getStatus().value());
    }

    @Test
    void ac23_createEmployeeUsesPartyAdapter() {
        Map<String, Object> result = businessServices.execute("createEmployee", Map.of(
                "partyId", "AC23_EMPLOYEE",
                "partyIdFrom", "AC23_COMPANY",
                "fromDate", EntityRegistry.now(),
                "firstName", "AC23",
                "lastName", "Employee"));
        assertEquals("AC23_EMPLOYEE", result.get("partyId"));
        verify(partyPort).createRelationship(
                eq("AC23_COMPANY"), eq("AC23_EMPLOYEE"), eq("EMPLOYMENT"),
                eq("INTERNAL_ORGANIZATIO"), eq("EMPLOYEE"), eq("EMPLOYMENT"), any(Timestamp.class));
    }

    @Test
    void ac21_updatePayHistoryChangesExistingPayRecord() {
        Timestamp employmentStart = Timestamp.valueOf("2024-01-01 00:00:00");
        Timestamp historyStart = Timestamp.valueOf("2024-01-01 00:00:00");
        Map<String, Object> original = new java.util.HashMap<>(Map.of(
                "partyIdFrom", "AC21_COMPANY",
                "partyIdTo", "AC21_EMPLOYEE",
                "roleTypeIdFrom", "INTERNAL_ORGANIZATIO",
                "roleTypeIdTo", "EMPLOYEE",
                "fromDate", historyStart,
                "emplFromDate", employmentStart,
                "amount", new BigDecimal("40000.00")));
        entityAutoService.createEntity("PayHistory", original);
        original.put("comments", "Updated by AC-21");
        businessServices.execute("updatePayHistory", original);
        Object current = entityAutoService.findEntity("PayHistory", original);
        assertEquals("Updated by AC-21", new EntityRegistryAccessor().read(current, "comments"));

        original.put("amount", new BigDecimal("45000.00"));
        original.put("changeDate", Timestamp.valueOf("2025-01-01 00:00:00"));
        Map<String, Object> replacement = businessServices.execute("updatePayHistory", original);
        Object expired = entityAutoService.findEntity("PayHistory", original);
        assertNotNull(entityRegistry.get(expired, "thruDate"));
        Object newHistory = entityAutoService.findEntity("PayHistory", replacement);
        assertEquals(new BigDecimal("45000.00"), entityRegistry.get(newHistory, "amount"));
    }

    @Test
    void ac25_currentEmploymentDataReturnsExpectedShape() {
        Timestamp now = EntityRegistry.now();
        AccountingPort.Rate rate = new AccountingPort.Rate(
                "AC25_RATE", "USD", now, new BigDecimal("70.00"));
        doReturn("USD").when(accountingPort).baseCurrency("AC25_COMPANY");
        doReturn(List.of(rate)).when(accountingPort).currentRates("AC25_TYPE", "USD");

        entityAutoService.createEntity("Employment", Map.of(
                "roleTypeIdFrom", "INTERNAL_ORGANIZATIO",
                "roleTypeIdTo", "EMPLOYEE",
                "partyIdFrom", "AC25_COMPANY",
                "partyIdTo", "AC25_EMPLOYEE",
                "fromDate", now));
        entityAutoService.createEntity("PartyBenefit", Map.of(
                "roleTypeIdFrom", "INTERNAL_ORGANIZATIO",
                "roleTypeIdTo", "EMPLOYEE",
                "partyIdFrom", "AC25_COMPANY",
                "partyIdTo", "AC25_EMPLOYEE",
                "benefitTypeId", "AC25_MEDICAL",
                "fromDate", now));
        entityAutoService.createEntity("EmplPositionType", Map.of(
                "emplPositionTypeId", "AC25_TYPE",
                "description", "AC25 position type"));
        entityAutoService.createEntity("EmplPosition", Map.of(
                "emplPositionId", "AC25_POSITION",
                "partyId", "AC25_COMPANY",
                "emplPositionTypeId", "AC25_TYPE",
                "statusId", "EMPL_POS_ACTIVE"));
        entityAutoService.createEntity("EmplPositionFulfillment", Map.of(
                "emplPositionId", "AC25_POSITION",
                "partyId", "AC25_EMPLOYEE",
                "fromDate", now));

        Map<String, Object> result = businessServices.execute(
                "getCurrentPartyEmploymentData", Map.of("partyId", "AC25_EMPLOYEE"));
        assertNotNull(result.get("employment"));
        assertEquals(1, ((List<?>) result.get("partyBenefitTypes")).size());
        assertNotNull(result.get("emplPosition"));
        assertNotNull(result.get("emplPositionType"));
        assertEquals(List.of(rate), result.get("emplPositionRateAmount"));
    }

    @Test
    void ac26_applyTrainingCreatesRequestAndPersonTraining() {
        Map<String, Object> result = businessServices.execute("applyTraining", Map.of(
                "partyId", "AC26_EMPLOYEE",
                "workEffortId", "AC26_EFFORT",
                "workEffortName", "AC26_TRAINING",
                "fromDate", EntityRegistry.now()));
        org.junit.jupiter.api.Assertions.assertNotNull(result.get("trainingRequestId"));
        assertEquals(1, entityAutoService.findEntities(
                "PersonTraining", Map.of("partyId", "AC26_EMPLOYEE")).size());
        Map<String, Object> assigned = businessServices.execute("assignTraining", Map.of(
                "partyId", "AC26_ASSIGNED",
                "workEffortId", "AC26_ASSIGNED_EFFORT",
                "trainingClassTypeId", "AC26_ASSIGNED_CLASS",
                "fromDate", EntityRegistry.now()));
        assertNotNull(assigned.get("trainingRequestId"));
        assertEquals(1, entityAutoService.findEntities(
                "PersonTraining", Map.of("partyId", "AC26_ASSIGNED")).size());
    }

    @Test
    void ac29_treeForUnknownPartyIsEmpty() {
        assertEquals(java.util.List.of(), businessServices.getHrTree("AC29_UNKNOWN"));
    }

    @Test
    void ac29_treeIncludesChildrenAndActivePositionsButExcludesInactiveAndExpired() {
        Timestamp past = Timestamp.valueOf("2020-01-01 00:00:00");
        Timestamp now = EntityRegistry.now();
        doReturn(true).when(partyPort).isPartyGroup("AC29_PARENT");
        doReturn(List.of(new PartyPort.Relationship("AC29_PARENT", "AC29_CHILD", past)))
                .when(partyPort).childOrganizations("AC29_PARENT");
        doReturn("Child organization").when(partyPort).partyName("AC29_CHILD");

        entityAutoService.createEntity("EmplPosition", Map.of(
                "emplPositionId", "AC29_ACTIVE",
                "partyId", "AC29_PARENT",
                "statusId", "EMPL_POS_ACTIVE",
                "actualFromDate", past));
        entityAutoService.createEntity("EmplPosition", Map.of(
                "emplPositionId", "AC29_INACTIVE",
                "partyId", "AC29_PARENT",
                "statusId", "EMPL_POS_INACTIVE",
                "actualFromDate", past));
        entityAutoService.createEntity("EmplPosition", Map.of(
                "emplPositionId", "AC29_EXPIRED",
                "partyId", "AC29_PARENT",
                "statusId", "EMPL_POS_ACTIVE",
                "actualFromDate", past,
                "actualThruDate", Timestamp.valueOf("2020-02-01 00:00:00")));

        Map<String, Object> employee = businessServices.execute("createEmployee", Map.of(
                "firstName", "Tree", "lastName", "Member"));
        String employeeId = employee.get("partyId").toString();
        entityAutoService.createEntity("EmplPositionFulfillment", Map.of(
                "emplPositionId", "AC29_ACTIVE",
                "partyId", employeeId,
                "fromDate", now));

        List<Map<String, Object>> organizationTree = businessServices.getHrTree("AC29_PARENT");
        assertTrue(organizationTree.stream().anyMatch(node -> nodeId(node).equals("AC29_CHILD")));
        assertTrue(organizationTree.stream().anyMatch(node -> nodeId(node).equals("AC29_ACTIVE")));
        assertFalse(organizationTree.stream().anyMatch(node -> nodeId(node).equals("AC29_INACTIVE")));
        assertFalse(organizationTree.stream().anyMatch(node -> nodeId(node).equals("AC29_EXPIRED")));

        List<Map<String, Object>> positionTree = businessServices.getHrTree("AC29_ACTIVE");
        assertTrue(positionTree.stream().anyMatch(node -> nodeId(node).equals(employeeId)));
    }

    @Test
    void ac27_internalOrganizationCanBeCreatedAndRemoved() {
        Map<String, Object> created = businessServices.execute("createInternalOrg", Map.of(
                "headpartyId", "AC27_PARENT", "partyId", "AC27_CHILD"));
        assertEquals("AC27_CHILD", created.get("partyId"));
        businessServices.execute("removeInternalOrg", Map.of(
                "parentpartyId", "AC27_PARENT", "partyId", "AC27_CHILD"));
    }

    @Test
    void ac27_internalOrganizationRemovalPreservesOtherRelationshipTypes() {
        Timestamp organizationFromDate = Timestamp.valueOf("2026-03-02 00:00:00");
        Timestamp otherFromDate = Timestamp.valueOf("2026-03-01 00:00:00");
        partyPort.createRelationship(
                "AC27_REL_PARENT", "AC27_REL_CHILD", "GROUP_ROLLUP",
                "INTERNAL_ORGANIZATIO", "INTERNAL_ORGANIZATIO", null, organizationFromDate);
        partyPort.createRelationship(
                "AC27_REL_PARENT", "AC27_REL_CHILD", "OTHER_RELATIONSHIP",
                "OTHER_ROLE_FROM", "OTHER_ROLE_TO", null, otherFromDate);

        businessServices.execute("removeInternalOrg", Map.of(
                "parentpartyId", "AC27_REL_PARENT", "partyId", "AC27_REL_CHILD"));

        PartyRelationshipRecord.Key organization = new PartyRelationshipRecord.Key();
        organization.partyIdFrom = "AC27_REL_PARENT";
        organization.partyIdTo = "AC27_REL_CHILD";
        organization.roleTypeIdFrom = "INTERNAL_ORGANIZATIO";
        organization.roleTypeIdTo = "INTERNAL_ORGANIZATIO";
        organization.fromDate = organizationFromDate;

        PartyRelationshipRecord.Key other = new PartyRelationshipRecord.Key();
        other.partyIdFrom = "AC27_REL_PARENT";
        other.partyIdTo = "AC27_REL_CHILD";
        other.roleTypeIdFrom = "OTHER_ROLE_FROM";
        other.roleTypeIdTo = "OTHER_ROLE_TO";
        other.fromDate = otherFromDate;

        assertNull(entityManager.find(PartyRelationshipRecord.class, organization));
        assertNotNull(entityManager.find(PartyRelationshipRecord.class, other));
    }

    @Test
    void ac28_publicHolidayRejectsDuplicateStartDate() {
        Timestamp start = Timestamp.valueOf("2026-01-01 00:00:00");
        Map<String, Object> input = Map.of("workEffortName", "AC28 Holiday", "estimatedStartDate", start);
        businessServices.execute("createPublicHoliday", input);
        HumanResException error = assertThrows(
                HumanResException.class, () -> businessServices.execute("createPublicHoliday", input));
        assertEquals(400, error.getStatus().value());
        verify(workEffortPort).createPublicHoliday(
                eq("AC28 Holiday"), eq(start), any(Timestamp.class), isNull());
    }

    @Test
    void ac30_multiServiceFailureRollsBackEarlierRows() {
        assertThrows(MultiServiceException.class, () -> serviceDispatcher.executeMulti(
                "createEmplPositionType",
                java.util.List.of(
                        Map.of("emplPositionTypeId", "AC30_TYPE", "description", "first"),
                        Map.of("emplPositionTypeId", "AC30_TYPE", "description", "duplicate"))));
        assertThrows(HumanResException.class, () -> entityAutoService.findEntity(
                "EmplPositionType", Map.of("emplPositionTypeId", "AC30_TYPE")));
    }

    @Test
    void ac24_positionTypeRateUpdateAndDeleteExpireRowsAndCallAccountingPort() {
        Timestamp originalFromDate = Timestamp.valueOf("2024-01-01 00:00:00");
        entityAutoService.createEntity("EmplPositionTypeRate", Map.of(
                "emplPositionTypeId", "AC24_TYPE",
                "rateTypeId", "AC24_RATE",
                "fromDate", originalFromDate));

        Timestamp replacementFromDate = EntityRegistry.now();
        Map<String, Object> input = new HashMap<>();
        input.put("emplPositionTypeId", "AC24_TYPE");
        input.put("rateTypeId", "AC24_RATE");
        input.put("fromDate", replacementFromDate);
        input.put("rateAmount", new BigDecimal("72500.00"));
        input.put("rateCurrencyUomId", "USD");
        businessServices.execute("updateEmplPositionTypeRate", input);

        Object original = entityAutoService.findEntity("EmplPositionTypeRate", Map.of(
                "emplPositionTypeId", "AC24_TYPE",
                "rateTypeId", "AC24_RATE",
                "fromDate", originalFromDate));
        assertNotNull(entityRegistry.get(original, "thruDate"));
        List<Object> rates = entityAutoService.findEntities("EmplPositionTypeRate", Map.of(
                "emplPositionTypeId", "AC24_TYPE", "rateTypeId", "AC24_RATE"));
        assertEquals(2, rates.size());
        verify(accountingPort).updateRateAmount(input);

        businessServices.execute("deleteEmplPositionTypeRate", input);
        Object replacement = entityAutoService.findEntity("EmplPositionTypeRate", Map.of(
                "emplPositionTypeId", "AC24_TYPE",
                "rateTypeId", "AC24_RATE",
                "fromDate", replacementFromDate));
        assertNotNull(entityRegistry.get(replacement, "thruDate"));
        assertEquals(2, entityAutoService.findEntities("EmplPositionTypeRate", Map.of(
                "emplPositionTypeId", "AC24_TYPE", "rateTypeId", "AC24_RATE")).size());
        verify(accountingPort).expireRateAmount(input);
    }

    @SuppressWarnings("unchecked")
    private static String nodeId(Map<String, Object> node) {
        return ((Map<String, Object>) node.get("attr")).get("id").toString();
    }

    private class EntityRegistryAccessor {
        Object read(Object entity, String name) {
            try {
                var field = entity.getClass().getDeclaredField(name);
                field.setAccessible(true);
                return field.get(entity);
            } catch (ReflectiveOperationException exception) {
                throw new AssertionError(exception);
            }
        }
    }
}
