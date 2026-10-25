package org.apache.ofbiz.migration.humanres;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.ofbiz.migration.humanres.generated.ServiceCatalog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {HumanResMigrationApplication.class, TestUsersConfiguration.class})
@AutoConfigureMockMvc
class EntityAutoAcceptanceTest {
    private static final ObjectMapper JSON = new ObjectMapper();

    @Autowired
    private EntityAutoService entityAutoService;

    @Autowired
    private EntityRegistry entityRegistry;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void ac01_allInventoryEntitiesAreMapped() throws IOException {
        JsonNode entities = inventory().get("entities");
        assertEquals(41, entities.size());
        for (JsonNode entity : entities) {
            assertNotNull(entityRegistry.entityClass(entity.get("name").asText()));
        }
    }

    @Test
    void ac02_generatedEntityReceivesAllStampColumns() {
        Map<String, Object> created = entityAutoService.createEntity("EmplPositionType", Map.of(
                "emplPositionTypeId", "AC02_TYPE", "description", "AC-02"));
        Map<String, Object> fields = entityRegistry.toMap(
                entityAutoService.findEntity("EmplPositionType", created));
        for (String stamp : new String[] {
            "createdStamp", "createdTxStamp", "lastUpdatedStamp", "lastUpdatedTxStamp"
        }) {
            assertNotNull(fields.get(stamp), stamp);
        }
    }

    @ParameterizedTest(name = "ac10_{0}")
    @MethodSource("entityAutoServices")
    void ac10_generatedCatalogMatchesInventory(
            String name, String entity, String invoke, String permissionAction) throws IOException {
        ServiceCatalog.ServiceDefinition generated = ServiceCatalog.find(name).orElseThrow();
        assertEquals(entity, generated.entity());
        assertEquals(invoke.toLowerCase(), generated.invoke());
        assertEquals(permissionAction, generated.permissionMainAction());
    }

    @Test
    void ac11_sequenceIdsAndSubSequencesFollowOfbizShape() {
        Map<String, Object> position = entityAutoService.execute("createEmplPosition", Map.of(
                "emplPositionTypeId", "AC11_TYPE", "statusId", "EMPL_POS_ACTIVE"));
        String positionId = String.valueOf(position.get("emplPositionId"));
        org.junit.jupiter.api.Assertions.assertTrue(positionId.matches("\\d+"));
        org.junit.jupiter.api.Assertions.assertTrue(Long.parseLong(positionId) >= 10000);

        Map<String, Object> item = entityAutoService.execute("createPerfReviewItem", Map.of(
                "employeePartyId", "AC11_EMP", "employeeRoleTypeId", "EMPLOYEE", "perfReviewId", "AC11_REVIEW"));
        assertEquals("00001", item.get("perfReviewItemSeqId"));
    }

    @Test
    void ac12_missingFromDateDefaultsToNow() {
        Map<String, Object> created = entityAutoService.createEntity("EmplPositionReportingStruct", Map.of(
                "emplPositionIdManagedBy", "AC12_A", "emplPositionIdReportingTo", "AC12_B"));
        assertNotNull(created.get("fromDate"));
    }

    @Test
    void ac12_nonPrimaryFromDateIsNotDefaulted() {
        entityAutoService.execute("createUnemploymentClaim", Map.of(
                "unemploymentClaimId", "AC12_NO_FROM_DATE"));
        Object claim = entityAutoService.findEntity(
                "UnemploymentClaim", Map.of("unemploymentClaimId", "AC12_NO_FROM_DATE"));
        assertNull(entityRegistry.get(claim, "fromDate"));
    }

    @Test
    void ac11_missingRequiredInPrimaryKeyIsNotGenerated() {
        HumanResException error = assertThrows(
                HumanResException.class,
                () -> entityAutoService.execute("createTrainingTypes", Map.of("description", "AC11 training")));
        assertEquals(400, error.getStatus().value());
        assertEquals("Missing required IN parameter: trainingClassTypeId", error.getMessage());
    }

    @Test
    void ac11_inPrimaryKeyIsNotGeneratedAsSubSequence() {
        HumanResException error = assertThrows(
                HumanResException.class,
                () -> entityAutoService.execute("createSalaryStep", Map.of("payGradeId", "AC11_GRADE")));
        assertEquals(400, error.getStatus().value());
        assertEquals("Missing primary-key parameter: salaryStepSeqId", error.getMessage());
    }

    @Test
    void ac13_duplicateCreateReturnsConflict() {
        Map<String, Object> values = Map.of(
                "emplPositionId", "AC13_POSITION", "emplPositionTypeId", "AC13_TYPE");
        entityAutoService.createEntity("EmplPosition", values);
        HumanResException error = assertThrows(
                HumanResException.class, () -> entityAutoService.createEntity("EmplPosition", values));
        assertEquals(409, error.getStatus().value());
    }

    @Test
    void ac14_requiredAndMissingEntityErrorsAreDistinct() {
        HumanResException missing = assertThrows(
                HumanResException.class, () -> entityAutoService.execute("createPartyQual", Map.of()));
        assertEquals(400, missing.getStatus().value());
        HumanResException absent = assertThrows(
                HumanResException.class, () -> entityAutoService.updateEntity(
                        "EmplPositionType", Map.of("emplPositionTypeId", "ABSENT", "description", "updated")));
        assertEquals(404, absent.getStatus().value());
    }

    @Test
    void ac15_updatesOnlyProvidedFields() {
        entityAutoService.createEntity("EmplPositionType", Map.of(
                "emplPositionTypeId", "AC15_TYPE", "description", "Original"));
        entityAutoService.updateEntity("EmplPositionType", Map.of(
                "emplPositionTypeId", "AC15_TYPE", "hasTable", "N"));
        Map<String, Object> current = entityRegistry.toMap(
                entityAutoService.findEntity("EmplPositionType", Map.of("emplPositionTypeId", "AC15_TYPE")));
        assertEquals("Original", current.get("description"));
        assertEquals("N", current.get("hasTable"));
    }

    @Test
    void ac16_rejectsStatusTransitionWithoutStatusValidChange() {
        entityAutoService.createEntity("EmplPosition", Map.of(
                "emplPositionId", "AC16_POSITION",
                "emplPositionTypeId", "AC16_TYPE",
                "statusId", "EMPL_POS_ACTIVE"));
        HumanResException error = assertThrows(
                HumanResException.class, () -> entityAutoService.updateEntity("EmplPosition", Map.of(
                        "emplPositionId", "AC16_POSITION", "statusId", "EMPL_POS_PLANNEDFOR")));
        assertEquals(400, error.getStatus().value());
    }

    @Test
    void ac17_expireAndDeleteAreDistinctOperations() {
        Map<String, Object> created = entityAutoService.createEntity("EmplPositionReportingStruct", Map.of(
                "emplPositionIdManagedBy", "AC17_A", "emplPositionIdReportingTo", "AC17_B"));
        Map<String, Object> keys = new HashMap<>();
        keys.put("emplPositionIdManagedBy", created.get("emplPositionIdManagedBy"));
        keys.put("emplPositionIdReportingTo", created.get("emplPositionIdReportingTo"));
        keys.put("fromDate", created.get("fromDate"));
        Map<String, Object> expire = new HashMap<>(keys);
        expire.put("thruDate", EntityRegistry.now());
        entityAutoService.expireEntity("EmplPositionReportingStruct", expire);
        assertNotNull(entityRegistry.get(
                entityAutoService.findEntity("EmplPositionReportingStruct", keys),
                "thruDate"));
        entityAutoService.deleteEntity("EmplPositionReportingStruct", keys);
        assertThrows(HumanResException.class, () -> entityAutoService.findEntity(
                "EmplPositionReportingStruct", keys));
    }

    @Test
    void ac18_deniesDangerousText() {
        HumanResException error = assertThrows(
                HumanResException.class, () -> entityAutoService.createEntity("EmplPositionType", Map.of(
                        "emplPositionTypeId", "AC18_TYPE", "description", "<script>alert(1)</script>")));
        assertEquals(400, error.getStatus().value());
    }

    @Test
    void ac06_serviceAndEntityEndpointsAreAvailable() throws Exception {
        mockMvc.perform(post("/api/humanres/services/createEmplPositionType")
                        .with(httpBasic("admin", "admin"))
                        .contentType("application/json")
                        .content("{\"emplPositionTypeId\":\"AC06_HTTP\",\"description\":\"HTTP\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/humanres/entities/EmplPositionType")
                        .with(httpBasic("admin", "admin"))
                        .param("emplPositionTypeId", "AC06_HTTP"))
                .andExpect(status().isOk());
    }

    @Test
    void ac45_localProfileUsesEnvironmentSuppliedPassword() throws IOException {
        String localConfig = java.nio.file.Files.readString(Path.of(
                System.getProperty("ofbiz.root"),
                "migration/humanres-springboot/src/main/resources/application-local.yml"));
        assertTrue(localConfig.contains("${HUMANRES_DEMO_PASSWORD}"));
        assertTrue(!localConfig.contains("password: password"));
    }

    static Stream<Arguments> entityAutoServices() throws IOException {
        JsonNode services = inventory().get("services");
        var arguments = new java.util.ArrayList<Arguments>();
        for (JsonNode service : services) {
            if (!service.path("engine").asText().equals("entity-auto")) {
                continue;
            }
            String action = service.path("permission_service").isMissingNode()
                    || service.path("permission_service").isNull()
                    ? null : service.path("permission_service").path("main_action").asText(null);
            arguments.add(Arguments.of(
                    service.path("name").asText(),
                    service.path("default_entity_name").asText(),
                    service.path("invoke").asText(),
                    action));
        }
        return arguments.stream();
    }

    private static JsonNode inventory() throws IOException {
        return JSON.readTree(Path.of(System.getProperty("ofbiz.root"), "docs/migration/humanres/inventory/inventory.json")
                .toFile());
    }
}
