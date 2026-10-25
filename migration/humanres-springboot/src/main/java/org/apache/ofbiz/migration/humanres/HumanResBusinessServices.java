package org.apache.ofbiz.migration.humanres;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HumanResBusinessServices {
    private static final Set<String> SERVICES = Set.of(
            "createEmployment", "updatePayHistory", "createEmplPositionReportingStruct",
            "createEmployee", "updateEmplPositionTypeRate", "deleteEmplPositionTypeRate",
            "getCurrentPartyEmploymentData", "applyTraining", "assignTraining",
            "createInternalOrg", "removeInternalOrg", "createPublicHoliday");
    private final EntityAutoService entityAutoService;
    private final EntityRegistry entityRegistry;
    private final SequenceService sequenceService;
    private final PartyPort partyPort;
    private final WorkEffortPort workEffortPort;
    private final AccountingPort accountingPort;
    private final HumanResTreeService treeService;

    public HumanResBusinessServices(
            EntityAutoService entityAutoService,
            EntityRegistry entityRegistry,
            SequenceService sequenceService,
            PartyPort partyPort,
            WorkEffortPort workEffortPort,
            AccountingPort accountingPort,
            HumanResTreeService treeService) {
        this.entityAutoService = entityAutoService;
        this.entityRegistry = entityRegistry;
        this.sequenceService = sequenceService;
        this.partyPort = partyPort;
        this.workEffortPort = workEffortPort;
        this.accountingPort = accountingPort;
        this.treeService = treeService;
    }

    public boolean supports(String serviceName) {
        return SERVICES.contains(serviceName);
    }

    @Transactional
    public Map<String, Object> execute(String serviceName, Map<String, ?> input) {
        return switch (serviceName) {
            case "createEmployment" -> createEmployment(input);
            case "updatePayHistory" -> updatePayHistory(input);
            case "createEmplPositionReportingStruct" -> createReportingStruct(input);
            case "createEmployee" -> createEmployee(input);
            case "updateEmplPositionTypeRate" -> updatePositionTypeRate(input);
            case "deleteEmplPositionTypeRate" -> deletePositionTypeRate(input);
            case "getCurrentPartyEmploymentData" -> currentEmploymentData(required(input, "partyId"));
            case "applyTraining" -> createTraining(input, true);
            case "assignTraining" -> createTraining(input, false);
            case "createInternalOrg" -> createInternalOrg(input);
            case "removeInternalOrg" -> removeInternalOrg(input);
            case "createPublicHoliday" -> createPublicHoliday(input);
            default -> throw HumanResException.notFound("Unknown service: " + serviceName);
        };
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getHrTree(String partyId) {
        return treeService.getHrTree(partyId);
    }

    private Map<String, Object> createEmployment(Map<String, ?> input) {
        partyPort.ensureRole(required(input, "partyIdTo"), required(input, "roleTypeIdTo"));
        Map<String, Object> employment = entityAutoService.createEntity("Employment", input);
        Map<String, Object> payHistory = new HashMap<>(input);
        payHistory.put("emplFromDate", employment.get("fromDate"));
        entityAutoService.createEntity("PayHistory", payHistory);
        return employment;
    }

    private Map<String, Object> updatePayHistory(Map<String, ?> input) {
        Object existing = entityAutoService.findEntity("PayHistory", input);
        Map<String, Object> old = entityRegistry.toMap(existing);
        boolean same = sameValue(old.get("amount"), input.get("amount"))
                && Objects.equals(old.get("payGradeId"), input.get("payGradeId"))
                && Objects.equals(old.get("salaryStepSeqId"), input.get("salaryStepSeqId"));
        if (same) {
            Map<String, Object> changes = new HashMap<>(primaryKeys("PayHistory", input));
            if (input.containsKey("comments")) {
                changes.put("comments", input.get("comments"));
            }
            return entityAutoService.updateEntity("PayHistory", changes);
        }
        Map<String, Object> expire = primaryKeys("PayHistory", input);
        expire.put("thruDate", EntityRegistry.now());
        entityAutoService.expireEntity("PayHistory", expire);
        Map<String, Object> create = new HashMap<>(input);
        create.remove("changeDate");
        create.remove("fromDate");
        create.put("fromDate", input.get("changeDate") == null
                ? EntityRegistry.now()
                : entityRegistry.convertForClass(input.get("changeDate"), Timestamp.class));
        create.put("emplFromDate", old.get("emplFromDate"));
        return entityAutoService.createEntity("PayHistory", create);
    }

    private Map<String, Object> createReportingStruct(Map<String, ?> input) {
        if (Objects.equals(input.get("emplPositionIdManagedBy"), input.get("emplPositionIdReportingTo"))) {
            throw HumanResException.badRequest(
                    "emplPositionIdManagedBy and emplPositionIdReportingTo must differ");
        }
        return entityAutoService.createEntity("EmplPositionReportingStruct", input);
    }

    private Map<String, Object> createEmployee(Map<String, ?> input) {
        return Map.of("partyId", partyPort.createEmployee(input));
    }

    private Map<String, Object> updatePositionTypeRate(Map<String, ?> input) {
        String typeId = required(input, "emplPositionTypeId");
        String rateTypeId = required(input, "rateTypeId");
        List<Object> existing = entityAutoService.findEntities("EmplPositionTypeRate",
                Map.of("emplPositionTypeId", typeId, "rateTypeId", rateTypeId));
        Timestamp now = EntityRegistry.now();
        for (Object rate : existing) {
            Object start = entityRegistry.get(rate, "fromDate");
            Object end = entityRegistry.get(rate, "thruDate");
            if ((start == null || !((Timestamp) start).after(now))
                    && (end == null || ((Timestamp) end).after(now))) {
                Map<String, Object> expire = primaryKeys("EmplPositionTypeRate", entityRegistry.toMap(rate));
                expire.put("thruDate", now);
                entityAutoService.expireEntity("EmplPositionTypeRate", expire);
            }
        }
        Map<String, Object> create = new HashMap<>(input);
        create.putIfAbsent("fromDate", now);
        Map<String, Object> result = entityAutoService.createEntity("EmplPositionTypeRate", create);
        if (input.get("rateAmount") != null) {
            accountingPort.updateRateAmount(input);
        }
        return result;
    }

    private Map<String, Object> deletePositionTypeRate(Map<String, ?> input) {
        Map<String, Object> keys = primaryKeys("EmplPositionTypeRate", input);
        Map<String, Object> expire = new HashMap<>(keys);
        expire.put("thruDate", EntityRegistry.now());
        Map<String, Object> result = entityAutoService.expireEntity("EmplPositionTypeRate", expire);
        accountingPort.expireRateAmount(input);
        return result;
    }

    private Map<String, Object> currentEmploymentData(String partyId) {
        Map<String, Object> result = new LinkedHashMap<>();
        List<Object> benefits = entityAutoService.findEntities("PartyBenefit", Map.of("partyIdTo", partyId));
        result.put("partyBenefitTypes", benefits.stream()
                .filter(benefit -> active(benefit, "fromDate", "thruDate"))
                .toList());
        List<Object> employment = entityAutoService.findEntities("Employment", Map.of("partyIdTo", partyId))
                .stream()
                .filter(value -> active(value, "fromDate", "thruDate"))
                .toList();
        Object currentEmployment = employment.isEmpty() ? null : employment.get(0);
        result.put("employment", currentEmployment);
        String employerId = currentEmployment == null
                ? null : String.valueOf(entityRegistry.get(currentEmployment, "partyIdFrom"));
        List<Object> positions = employerId == null ? List.of()
                : entityAutoService.findEntities("EmplPositionFulfillment", Map.of("partyId", partyId))
                        .stream().filter(value -> active(value, "fromDate", "thruDate")).toList();
        Object fulfillment = positions.isEmpty() ? null : positions.get(0);
        Object position = fulfillment == null ? null
                : entityAutoService.findEntity("EmplPosition", Map.of(
                        "emplPositionId", entityRegistry.get(fulfillment, "emplPositionId")));
        result.put("emplPosition", position);
        Object positionTypeId = position == null ? null : entityRegistry.get(position, "emplPositionTypeId");
        Object positionType = positionTypeId == null ? null
                : entityAutoService.findEntity("EmplPositionType", Map.of("emplPositionTypeId", positionTypeId));
        result.put("emplPositionType", positionType);
        String currency = employerId == null ? null : accountingPort.baseCurrency(employerId);
        result.put("emplPositionRateAmount", positionTypeId == null || currency == null
                ? List.of() : accountingPort.currentRates(positionTypeId.toString(), currency));
        return result;
    }

    private Map<String, Object> createTraining(Map<String, ?> input, boolean apply) {
        Map<String, Object> requestInput = new HashMap<>(input);
        requestInput.put("trainingRequestId", sequenceService.nextId("TrainingRequest"));
        Map<String, Object> request = entityAutoService.createEntity("TrainingRequest", requestInput);
        Map<String, Object> training = new HashMap<>(input);
        training.put("trainingRequestId", request.get("trainingRequestId"));
        if (apply && input.get("workEffortName") != null) {
            training.put("trainingClassTypeId", input.get("workEffortName"));
        }
        training.putIfAbsent("fromDate", EntityRegistry.now());
        entityAutoService.createEntity("PersonTraining", training);
        return request;
    }

    private Map<String, Object> createInternalOrg(Map<String, ?> input) {
        String parentId = required(input, "headpartyId");
        String childId = required(input, "partyId");
        partyPort.ensureRole(parentId, "INTERNAL_ORGANIZATIO");
        partyPort.ensureRole(childId, "INTERNAL_ORGANIZATIO");
        partyPort.createRelationship(parentId, childId, "GROUP_ROLLUP",
                "INTERNAL_ORGANIZATIO", "INTERNAL_ORGANIZATIO", null, EntityRegistry.now());
        return Map.of("partyId", childId, "headpartyId", parentId);
    }

    private Map<String, Object> removeInternalOrg(Map<String, ?> input) {
        partyPort.removeRelationship(required(input, "parentpartyId"), required(input, "partyId"));
        return Map.of();
    }

    private Map<String, Object> createPublicHoliday(Map<String, ?> input) {
        String name = required(input, "workEffortName");
        Timestamp start = input.get("estimatedStartDate") == null
                ? null : (Timestamp) entityRegistry.convertForClass(input.get("estimatedStartDate"), Timestamp.class);
        if (start == null) {
            throw HumanResException.badRequest("estimatedStartDate is required");
        }
        if (workEffortPort.publicHolidayExists(start)) {
            throw HumanResException.badRequest("Public holiday already exists for the start date");
        }
        LocalDate date = start.toInstant().atOffset(ZoneOffset.UTC).toLocalDate();
        Timestamp end = Timestamp.from(date.atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC));
        String id = workEffortPort.createPublicHoliday(
                name, start, end, input.get("partyId") == null ? null : input.get("partyId").toString());
        return Map.of("workEffortId", id);
    }

    private boolean active(Object entity, String fromField, String thruField) {
        Timestamp now = EntityRegistry.now();
        Object from = entityRegistry.get(entity, fromField);
        Object thru = entityRegistry.get(entity, thruField);
        return (from == null || !((Timestamp) from).after(now))
                && (thru == null || ((Timestamp) thru).after(now));
    }

    private Map<String, Object> primaryKeys(String entityName, Map<String, ?> values) {
        Class<?> type = entityRegistry.entityClass(entityName);
        Map<String, Object> keys = new HashMap<>();
        for (String key : entityRegistry.primaryKeyNames(type)) {
            if (values.containsKey(key)) {
                keys.put(key, values.get(key));
            }
        }
        return keys;
    }

    private static String required(Map<String, ?> values, String name) {
        Object value = values.get(name);
        if (value == null || value.toString().isBlank()) {
            throw HumanResException.badRequest("Missing required IN parameter: " + name);
        }
        return value.toString();
    }

    private static boolean sameValue(Object left, Object right) {
        if (left instanceof BigDecimal leftAmount && right != null) {
            try {
                return leftAmount.compareTo(new BigDecimal(right.toString())) == 0;
            } catch (NumberFormatException ignored) {
                return false;
            }
        }
        return Objects.equals(left == null ? null : left.toString(), right == null ? null : right.toString());
    }
}
