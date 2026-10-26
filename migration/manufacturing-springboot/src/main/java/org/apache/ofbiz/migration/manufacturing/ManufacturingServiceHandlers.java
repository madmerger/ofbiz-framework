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
package org.apache.ofbiz.migration.manufacturing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.SequenceService;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ManufacturingServiceHandlers {
    private static final String BOM_TYPE = "MANUF_COMPONENT";
    private static final Set<String> PRODUCT_ASSOC_FIELDS = Set.of(
            "productId", "productIdTo", "productAssocTypeId", "fromDate", "thruDate", "sequenceNum", "reason",
            "quantity", "scrapFactor", "instruction", "routingWorkEffortId", "estimateCalcMethod", "recurrenceInfoId");
    private static final Set<String> PRODUCT_RULE_FIELDS = Set.of(
            "ruleId", "productId", "productIdFor", "productIdIn", "ruleSeqId", "fromDate", "productIdInSubst",
            "productFeature", "ruleOperator", "quantity", "description", "thruDate");
    private static final Set<String> COPY_BOM_INPUT_FIELDS = Set.of(
            "productId", "copyToProductId", "productAssocTypeId", "productIdTo", "fromDate", "thruDate");
    private final ServiceHandlerRegistry registry;
    private final EntityRegistry entities;
    private final EntityManager entityManager;
    private final SequenceService sequences;
    private final ProductPort products;
    private final WorkEffortPort workEfforts;

    public ManufacturingServiceHandlers(
            ServiceHandlerRegistry registry,
            EntityRegistry entities,
            EntityManager entityManager,
            SequenceService sequences,
            ProductPort products,
            WorkEffortPort workEfforts) {
        this.registry = registry;
        this.entities = entities;
        this.entityManager = entityManager;
        this.sequences = sequences;
        this.products = products;
        this.workEfforts = workEfforts;
    }

    @PostConstruct
    void registerHandlers() {
        for (String service : List.of("createBOMAssoc", "copyBOMAssocs", "addProductManufacturingRule")) {
            registry.registerHandler("manufacturing", service, "CREATE", input -> execute(service, input));
        }
        for (String service : List.of("exampleComponentFormula", "linearComponentFormula", "exampleTaskFormula")) {
            registry.registerHandler("manufacturing", service, "VIEW", input -> execute(service, input));
        }
        registry.registerHandler("manufacturing", "updateProductManufacturingRule", "UPDATE",
                input -> execute("updateProductManufacturingRule", input));
        registry.registerHandler("manufacturing", "deleteProductManufacturingRule", "DELETE",
                input -> execute("deleteProductManufacturingRule", input));
        for (String service : List.of("searchDuplicatedAncestor", "getProductRouting", "getManufacturingComponents")) {
            registry.registerHandler("manufacturing", service, "VIEW", input -> execute(service, input));
        }
        registry.registerHandler("manufacturing", "createProductionRun", "CREATE",
                input -> execute("createProductionRun", input));
        for (String service : List.of("changeProductionRunStatus", "changeProductionRunTaskStatus",
                "updateProductionRunTask")) {
            registry.registerHandler("manufacturing", service, "UPDATE", input -> execute(service, input));
        }
    }

    private Map<String, Object> execute(String service, Map<String, Object> input) {
        return switch (service) {
            case "createBOMAssoc" -> createBomAssoc(input);
            case "copyBOMAssocs" -> copyBomAssocs(input);
            case "addProductManufacturingRule" -> addRule(input);
            case "updateProductManufacturingRule" -> updateRule(input);
            case "deleteProductManufacturingRule" -> deleteRule(input);
            case "exampleComponentFormula" -> Map.of("quantity", decimal(arg(input, "neededQuantity")).multiply(BigDecimal.TEN));
            case "linearComponentFormula" -> linearFormula(input);
            case "exampleTaskFormula" -> taskFormula(input);
            case "searchDuplicatedAncestor" -> searchDuplicatedAncestor(input);
            case "getProductRouting" -> getProductRouting(input);
            case "getManufacturingComponents" -> getManufacturingComponents(input);
            case "createProductionRun" -> createProductionRun(input);
            case "changeProductionRunStatus" -> changeProductionRunStatus(input);
            case "changeProductionRunTaskStatus" -> changeProductionRunTaskStatus(input);
            case "updateProductionRunTask" -> updateProductionRunTask(input);
            default -> throw new MigrationException(HttpStatus.NOT_FOUND, "Unknown service: " + service);
        };
    }

    private Map<String, Object> createBomAssoc(Map<String, Object> input) {
        validateFields(input, PRODUCT_ASSOC_FIELDS);
        String productId = required(input, "productId");
        String componentId = required(input, "productIdTo");
        String type = required(input, "productAssocTypeId");
        Timestamp fromDate = timestamp(input.get("fromDate"));
        if (fromDate == null) {
            fromDate = Timestamp.from(Instant.now());
        }
        if (productId.equals(componentId)) {
            throw badRequest("ManufacturingBomProductCannotBeComponentOfItself");
        }
        Map<String, Object> keys = assocKeys(productId, componentId, type, fromDate);
        if (products.findProductAssoc(keys) != null) {
            throw new MigrationException(HttpStatus.CONFLICT, "ServiceValueFound");
        }
        if (findDuplicatedAncestor(productId, componentId, type, fromDate) != null) {
            throw badRequest("ManufacturingDuplicatedComponentInBOM: " + productId + " " + componentId);
        }
        Map<String, Object> values = new LinkedHashMap<>();
        input.forEach(values::put);
        values.putAll(keys);
        return products.createProductAssoc(values);
    }

    private Map<String, Object> copyBomAssocs(Map<String, Object> input) {
        validateFields(input, COPY_BOM_INPUT_FIELDS);
        String source = required(input, "productId");
        String destination = required(input, "copyToProductId");
        String type = required(input, "productAssocTypeId");
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put("productId", source);
        filter.put("productAssocTypeId", type);
        copyIfPresent(input, filter, "productIdTo");
        Timestamp now = Timestamp.from(Instant.now());
        List<Map<String, Object>> assocs = products.findProductAssocs(filter);
        int copied = 0;
        for (Map<String, Object> assoc : assocs) {
            Timestamp from = timestamp(assoc.get("fromDate"));
            Timestamp thru = timestamp(assoc.get("thruDate"));
            if ((from != null && from.after(now)) || (thru != null && !thru.after(now))) {
                continue;
            }
            Map<String, Object> values = new LinkedHashMap<>();
            assoc.forEach((name, value) -> {
                if (PRODUCT_ASSOC_FIELDS.contains(name)) {
                    values.put(name, value);
                }
            });
            values.put("productId", destination);
            if (input.get("fromDate") != null) {
                values.put("fromDate", timestamp(input.get("fromDate")));
            }
            if (input.get("thruDate") != null) {
                values.put("thruDate", timestamp(input.get("thruDate")));
            }
            Timestamp fromDate = timestamp(values.get("fromDate"));
            Timestamp effectiveDate = fromDate == null ? now : fromDate;
            if (!destination.equals(values.get("productIdTo"))
                    && products.findProductAssoc(assocKeys(destination, text(values.get("productIdTo")),
                            text(values.get("productAssocTypeId")), timestamp(values.get("fromDate")))) == null
                    && findDuplicatedAncestor(destination, text(values.get("productIdTo")),
                            text(values.get("productAssocTypeId")), effectiveDate) == null) {
                products.createProductAssoc(values);
                copied++;
            }
        }
        return Map.of("copied", copied);
    }

    private Map<String, Object> addRule(Map<String, Object> input) {
        validateFields(input, PRODUCT_RULE_FIELDS);
        required(input, "productIdIn");
        Map<String, Object> values = new LinkedHashMap<>(input);
        String ruleId = text(values.get("ruleId"));
        if (ruleId.isBlank()) {
            ruleId = sequences.nextId("FinAccount");
            values.put("ruleId", ruleId);
        }
        if (find("ProductManufacturingRule", Map.of("ruleId", ruleId)) != null) {
            throw new MigrationException(HttpStatus.CONFLICT, "ServiceValueFound");
        }
        return create("ProductManufacturingRule", values);
    }

    private Map<String, Object> updateRule(Map<String, Object> input) {
        validateFields(input, PRODUCT_RULE_FIELDS);
        String ruleId = required(input, "ruleId");
        required(input, "productIdIn");
        Map<String, Object> values = find("ProductManufacturingRule", Map.of("ruleId", ruleId));
        if (values == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "ServiceValueNotFound");
        }
        Object entity = entityByPk("ProductManufacturingRule", Map.of("ruleId", ruleId));
        input.forEach((name, value) -> {
            if (!name.equals("ruleId")) {
                entities.set(entity, name, value);
            }
        });
        return find("ProductManufacturingRule", Map.of("ruleId", ruleId));
    }

    private Map<String, Object> deleteRule(Map<String, Object> input) {
        validateFields(input, Set.of("ruleId"));
        String ruleId = required(input, "ruleId");
        Object rule = entityByPk("ProductManufacturingRule", Map.of("ruleId", ruleId));
        if (rule != null) {
            entityManager.remove(rule);
        }
        return Map.of();
    }

    private Map<String, Object> linearFormula(Map<String, Object> input) {
        BigDecimal needed = decimal(arg(input, "neededQuantity"));
        BigDecimal amount = decimal(arg(input, "amount"));
        BigDecimal width = decimal(arg(input, "width"));
        if (width.signum() == 0) {
            throw badRequest("width must not be zero");
        }
        BigDecimal quotient = needed.multiply(amount).divide(width, 0, RoundingMode.CEILING);
        return Map.of("quantity", quotient);
    }

    private Map<String, Object> taskFormula(Map<String, Object> input) {
        Object workEffort = arg(input, "workEffort");
        if (!(workEffort instanceof Map<?, ?> map)) {
            throw badRequest("arguments.workEffort is required");
        }
        BigDecimal milliseconds = decimal(map.get("estimatedMilliSeconds"));
        return Map.of("totalTime", milliseconds.multiply(decimal(arg(input, "quantity"))).multiply(BigDecimal.TEN));
    }

    private Map<String, Object> searchDuplicatedAncestor(Map<String, Object> input) {
        String product = required(input, "productId");
        String component = required(input, "productIdTo");
        String type = text(input.getOrDefault("productAssocTypeId", BOM_TYPE));
        Timestamp date = timestamp(input.get("fromDate"));
        if (date == null) {
            date = Timestamp.from(Instant.now());
        }
        Map<String, Object> duplicated = findDuplicatedAncestor(product, component, type, date);
        return Map.of("duplicatedProductAssoc", duplicated == null ? Map.of() : duplicated);
    }

    private Map<String, Object> findDuplicatedAncestor(String productId, String productIdTo,
                                                       String type, Timestamp date) {
        Set<String> productIdKeys = new java.util.HashSet<>();
        Deque<String> productsToVisit = new ArrayDeque<>();
        productsToVisit.add(productIdTo);
        while (!productsToVisit.isEmpty()) {
            String currentProductId = productsToVisit.removeFirst();
            if (!productIdKeys.add(currentProductId)) {
                continue;
            }
            for (Map<String, Object> child : products.findProductAssocs(
                    Map.of("productId", currentProductId, "productAssocTypeId", type))) {
                if (activeOn(child, date)) {
                    productsToVisit.addLast(text(child.get("productIdTo")));
                }
            }
        }

        Set<String> visitedAncestors = new java.util.HashSet<>();
        Deque<String> ancestorsToVisit = new ArrayDeque<>();
        ancestorsToVisit.add(productId);
        while (!ancestorsToVisit.isEmpty()) {
            String currentProductId = ancestorsToVisit.removeFirst();
            if (!visitedAncestors.add(currentProductId)) {
                continue;
            }
            for (Map<String, Object> parent : products.findProductAssocs(
                    Map.of("productIdTo", currentProductId, "productAssocTypeId", type))) {
                if (!activeOn(parent, date)) {
                    continue;
                }
                String parentProductId = text(parent.get("productId"));
                if (productIdKeys.contains(parentProductId)) {
                    return parent;
                }
                ancestorsToVisit.addLast(parentProductId);
            }
        }
        return null;
    }

    private Map<String, Object> getProductRouting(Map<String, Object> input) {
        String productId = required(input, "productId");
        String requestedWorkEffortId = text(input.get("workEffortId"));
        Timestamp requestedDate = timestamp(input.get("applicableDate"));
        Timestamp applicableDate = requestedDate == null ? Timestamp.from(Instant.now()) : requestedDate;
        Map<String, Object> standardFilters = new LinkedHashMap<>();
        standardFilters.put("productId", productId);
        standardFilters.put("workEffortGoodStdTypeId", "ROU_PROD_TEMPLATE");
        if (!requestedWorkEffortId.isBlank()) {
            standardFilters.put("workEffortId", requestedWorkEffortId);
        }
        Map<String, Object> routingStandard = findRoutingStandard(standardFilters, applicableDate);
        if (routingStandard == null) {
            Map<String, Object> variantParent = products.findProductAssocs(Map.of(
                    "productIdTo", productId, "productAssocTypeId", "PRODUCT_VARIANT"))
                    .stream().filter(value -> activeOn(value, applicableDate)).findFirst().orElse(null);
            if (variantParent != null) {
                standardFilters.put("productId", text(variantParent.get("productId")));
                routingStandard = findRoutingStandard(standardFilters, applicableDate);
            }
        }
        Map<String, Object> routing;
        if (routingStandard != null) {
            routing = workEfforts.findWorkEffort(text(routingStandard.get("workEffortId")));
        } else {
            String ignoreDefaultRouting = text(input.get("ignoreDefaultRouting"));
            routing = "Y".equalsIgnoreCase(ignoreDefaultRouting)
                    ? null : workEfforts.findWorkEffort("DEFAULT_ROUTING");
        }
        List<Map<String, Object>> tasks = null;
        Map<String, Object> result = new LinkedHashMap<>();
        if (routing != null) {
            String workEffortId = text(routing.get("workEffortId"));
            tasks = workEfforts.findWorkEffortAssocs(Map.of(
                    "workEffortIdFrom", workEffortId, "workEffortAssocTypeId", "ROUTING_COMPONENT"));
            tasks = tasks.stream().filter(value -> activeOn(value, applicableDate))
                    .sorted((left, right) -> decimal(left.get("sequenceNum"))
                            .compareTo(decimal(right.get("sequenceNum"))))
                    .toList();
            result.put("workEffortId", workEffortId);
        }
        result.put("routing", routing);
        result.put("tasks", tasks);
        return result;
    }

    private Map<String, Object> findRoutingStandard(Map<String, ?> filters, Timestamp date) {
        return workEfforts.findWorkEffortGoodStandards(filters).stream()
                .filter(value -> activeOn(value, date)).findFirst().orElse(null);
    }

    private Map<String, Object> getManufacturingComponents(Map<String, Object> input) {
        String productId = required(input, "productId");
        BigDecimal quantity = input.get("quantity") == null ? BigDecimal.ONE : decimal(input.get("quantity"));
        Timestamp requestedDate = timestamp(input.get("applicableDate"));
        Timestamp applicableDate = requestedDate == null ? Timestamp.from(Instant.now()) : requestedDate;
        List<Map<String, Object>> components = new ArrayList<>();
        for (Map<String, Object> assoc : products.findProductAssocs(
                Map.of("productId", productId, "productAssocTypeId", BOM_TYPE))) {
            if (!activeOn(assoc, applicableDate)) {
                continue;
            }
            Map<String, Object> item = new LinkedHashMap<>(assoc);
            item.put("requiredQuantity", requiredComponentQuantity(assoc, quantity));
            components.add(item);
        }
        return Map.of("components", components);
    }

    private Map<String, Object> createProductionRun(Map<String, Object> input) {
        String productId = required(input, "productId");
        if (products.findProduct(productId) == null) {
            throw badRequest("Product not found: " + productId);
        }
        BigDecimal quantity = requiredDecimal(input, "pRQuantity");
        Timestamp start = requiredTimestamp(input, "startDate");
        String facilityId = required(input, "facilityId");
        Map<String, Object> routingInput = new LinkedHashMap<>();
        routingInput.put("productId", productId);
        routingInput.put("applicableDate", start);
        if (input.get("routingId") != null) {
            routingInput.put("workEffortId", input.get("routingId"));
        }
        Map<String, Object> routing;
        try {
            routing = getProductRouting(routingInput);
        } catch (MigrationException exception) {
            throw badRequest("Manufacturing routing not found for product " + productId);
        }
        if (routing.get("routing") == null) {
            throw badRequest("Manufacturing routing not found for product " + productId);
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> routingTasks = (List<Map<String, Object>>) routing.get("tasks");
        if (routingTasks == null || routingTasks.isEmpty()) {
            throw badRequest("Manufacturing routing has no tasks");
        }
        Map<String, Object> componentInput = Map.of("productId", productId, "quantity", quantity,
                "applicableDate", start);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> components = (List<Map<String, Object>>) getManufacturingComponents(componentInput)
                .get("components");

        String firstRoutingTaskId = text(routingTasks.get(0).get("workEffortIdTo"));
        Set<String> routingTaskIds = new java.util.HashSet<>();
        for (Map<String, Object> routingTask : routingTasks) {
            routingTaskIds.add(text(routingTask.get("workEffortIdTo")));
        }
        Set<String> componentAssignments = new java.util.HashSet<>();
        for (Map<String, Object> component : components) {
            String routingTaskId = text(component.get("routingWorkEffortId"));
            String targetRoutingTaskId = routingTaskIds.contains(routingTaskId) ? routingTaskId : firstRoutingTaskId;
            String componentId = text(component.get("productIdTo"));
            if (!componentAssignments.add(targetRoutingTaskId + "\u001f" + componentId)) {
                throw badRequest("Duplicate production component " + componentId
                        + " for routing task " + targetRoutingTaskId);
            }
        }

        Map<String, Object> routingWorkEffort = (Map<String, Object>) routing.get("routing");
        String workEffortName = text(input.get("workEffortName"));
        if (workEffortName.isBlank()) {
            String routingName = text(routingWorkEffort.get("workEffortName"));
            workEffortName = productId + "-" + (routingName.isBlank()
                    ? text(routingWorkEffort.get("workEffortId")) : routingName);
        }
        String productionRunId = sequences.nextId("WorkEffort");
        Map<String, Object> headerValues = new LinkedHashMap<>();
        headerValues.put("workEffortId", productionRunId);
        headerValues.put("workEffortTypeId", "PROD_ORDER_HEADER");
        headerValues.put("workEffortPurposeTypeId", "WEPT_PRODUCTION_RUN");
        headerValues.put("currentStatusId", "PRUN_CREATED");
        headerValues.put("quantityToProduce", quantity);
        headerValues.put("facilityId", facilityId);
        headerValues.put("estimatedStartDate", start);
        headerValues.put("workEffortName", workEffortName);
        headerValues.put("description", input.get("description"));
        Map<String, Object> header = workEfforts.createWorkEffort(headerValues);
        workEfforts.createWorkEffortGoodStandard(Map.of(
                "workEffortId", productionRunId, "productId", productId,
                "workEffortGoodStdTypeId", "PRUN_PROD_DELIV", "statusId", "WEGS_CREATED",
                "estimatedQuantity", quantity, "fromDate", start));

        Timestamp completion = start;
        Map<String, String> routingToProductionTask = new LinkedHashMap<>();
        List<Map<String, Object>> productionTasks = new ArrayList<>();
        for (Map<String, Object> routingTask : routingTasks) {
            String routingTaskId = text(routingTask.get("workEffortIdTo"));
            Map<String, Object> routingTaskWorkEffort = workEfforts.findWorkEffort(routingTaskId);
            if (routingTaskWorkEffort == null || !"ROU_TASK".equals(routingTaskWorkEffort.get("workEffortTypeId"))) {
                throw badRequest("Manufacturing routing task not found: " + routingTaskId);
            }
            String taskId = sequences.nextId("WorkEffort");
            BigDecimal setupMillis = decimal(routingTaskWorkEffort.get("estimatedSetupMillis"));
            BigDecimal estimatedMillis = decimal(routingTaskWorkEffort.get("estimatedMilliSeconds"));
            Timestamp taskStart = completion;
            BigDecimal duration = setupMillis.add(estimatedMillis.multiply(quantity));
            completion = new Timestamp(completion.getTime() + duration.longValue());
            Map<String, Object> task = new LinkedHashMap<>();
            task.put("workEffortId", taskId);
            task.put("workEffortTypeId", "PROD_ORDER_TASK");
            task.put("workEffortPurposeTypeId", "WEPT_PRODUCTION_RUN");
            task.put("workEffortParentId", productionRunId);
            task.put("currentStatusId", "PRUN_CREATED");
            task.put("priority", routingTask.get("sequenceNum"));
            task.put("workEffortName", routingTaskWorkEffort.get("workEffortName"));
            task.put("description", routingTaskWorkEffort.get("description"));
            task.put("facilityId", facilityId);
            task.put("estimatedSetupMillis", setupMillis);
            task.put("estimatedMilliSeconds", estimatedMillis);
            task.put("quantityToProduce", quantity);
            task.put("estimatedStartDate", taskStart);
            task.put("estimatedCompletionDate", completion);
            workEfforts.createWorkEffort(task);
            workEfforts.createWorkEffortAssoc(Map.of(
                    "workEffortIdFrom", routingTaskId, "workEffortIdTo", taskId,
                    "workEffortAssocTypeId", "WORK_EFF_TEMPLATE", "fromDate", start));
            routingToProductionTask.put(routingTaskId, taskId);
            productionTasks.add(task);
        }
        String firstTaskId = text(productionTasks.get(0).get("workEffortId"));
        for (Map<String, Object> component : components) {
            String routingTaskId = text(component.get("routingWorkEffortId"));
            String taskId = routingToProductionTask.getOrDefault(routingTaskId, firstTaskId);
            workEfforts.createWorkEffortGoodStandard(Map.of(
                    "workEffortId", taskId,
                    "productId", component.get("productIdTo"),
                    "workEffortGoodStdTypeId", "PRUNT_PROD_NEEDED",
                    "estimatedQuantity", component.get("requiredQuantity"),
                    "statusId", "WEGS_CREATED", "fromDate", start));
        }
        header = workEfforts.updateWorkEffort(productionRunId, Map.of("estimatedCompletionDate", completion));
        return Map.of("productionRunId", productionRunId, "estimatedCompletionDate", completion,
                "workEffort", header);
    }

    private Map<String, Object> changeProductionRunStatus(Map<String, Object> input) {
        String id = required(input, "productionRunId");
        Map<String, Object> header = workEfforts.findWorkEffort(id);
        if (header == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "ServiceValueNotFound");
        }
        String oldStatus = text(header.get("currentStatusId"));
        String newStatus = text(input.getOrDefault("statusId", input.get("newStatusId")));
        if (newStatus.isBlank()) {
            newStatus = switch (oldStatus) {
                case "PRUN_CREATED" -> "PRUN_SCHEDULED";
                case "PRUN_SCHEDULED" -> "PRUN_DOC_PRINTED";
                case "PRUN_DOC_PRINTED" -> "PRUN_RUNNING";
                case "PRUN_RUNNING" -> "PRUN_COMPLETED";
                case "PRUN_COMPLETED" -> "PRUN_CLOSED";
                default -> oldStatus;
            };
        }
        Set<String> allowed = Set.of("PRUN_CREATED:PRUN_SCHEDULED", "PRUN_CREATED:PRUN_DOC_PRINTED",
                "PRUN_SCHEDULED:PRUN_DOC_PRINTED", "PRUN_DOC_PRINTED:PRUN_RUNNING",
                "PRUN_RUNNING:PRUN_COMPLETED", "PRUN_COMPLETED:PRUN_CLOSED");
        if (allowed.contains(oldStatus + ":" + newStatus)) {
            if ("PRUN_DOC_PRINTED".equals(oldStatus) && "PRUN_RUNNING".equals(newStatus)) {
                Timestamp effectiveDate = Timestamp.from(Instant.now());
                List<Map<String, Object>> predecessors = workEfforts.findWorkEffortAssocs(Map.of(
                        "workEffortIdTo", id, "workEffortAssocTypeId", "WORK_EFF_PRECEDENCY"));
                boolean blocked = predecessors.stream().filter(value -> activeOn(value, effectiveDate))
                        .map(value -> workEfforts.findWorkEffort(text(value.get("workEffortIdFrom"))))
                        .anyMatch(value -> value == null || !Set.of("PRUN_COMPLETED", "PRUN_RUNNING", "PRUN_CLOSED")
                                .contains(text(value.get("currentStatusId"))));
                if (blocked) {
                    throw badRequest("Production run has incomplete precedence work efforts");
                }
            }
            String targetStatus = newStatus;
            Map<String, Object> changes = new LinkedHashMap<>();
            changes.put("currentStatusId", targetStatus);
            if (targetStatus.equals("PRUN_RUNNING")) {
                changes.put("actualStartDate", Timestamp.from(Instant.now()));
            }
            if (targetStatus.equals("PRUN_COMPLETED")) {
                changes.put("actualCompletionDate", Timestamp.from(Instant.now()));
            }
            workEfforts.updateWorkEffort(id, changes);
            if (Set.of("PRUN_SCHEDULED", "PRUN_DOC_PRINTED", "PRUN_CLOSED").contains(targetStatus)) {
                workEfforts.findChildTasks(id).forEach(task -> workEfforts.updateWorkEffort(
                        text(task.get("workEffortId")), Map.of("currentStatusId", targetStatus)));
            }
        } else {
            newStatus = oldStatus;
        }
        return Map.of("newStatusId", newStatus);
    }

    private Map<String, Object> changeProductionRunTaskStatus(Map<String, Object> input) {
        String runId = required(input, "productionRunId");
        String taskId = required(input, "productionRunTaskId");
        Map<String, Object> task = workEfforts.findWorkEffort(taskId);
        if (task == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "ServiceValueNotFound");
        }
        if (!runId.equals(text(task.get("workEffortParentId")))) {
            throw badRequest("Production run task does not belong to the requested production run");
        }
        String oldStatus = text(task.get("currentStatusId"));
        String newStatus = text(input.get("statusId"));
        if (newStatus.isBlank()) {
            newStatus = oldStatus.equals("PRUN_RUNNING") ? "PRUN_COMPLETED" : "PRUN_RUNNING";
        }
        if (newStatus.equals(oldStatus)) {
            return Map.of("oldStatusId", oldStatus, "newStatusId", oldStatus);
        }
        Map<String, Object> header = workEfforts.findWorkEffort(runId);
        if (header == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "ServiceValueNotFound");
        }
        Map<String, Object> changes = new LinkedHashMap<>();
        if ("PRUN_RUNNING".equals(newStatus)) {
            if (!Set.of("PRUN_CREATED", "PRUN_SCHEDULED", "PRUN_DOC_PRINTED").contains(oldStatus)) {
                throw badRequest("Production run task cannot be started from its current status");
            }
            if ("PRUN_CREATED".equals(header.get("currentStatusId"))) {
                throw badRequest("Production run must be printed before a task starts");
            }
            BigDecimal priority = decimal(task.get("priority"));
            boolean earlierIncomplete = workEfforts.findChildTasks(runId).stream()
                    .filter(value -> decimal(value.get("priority")).compareTo(priority) < 0)
                    .anyMatch(value -> !"PRUN_COMPLETED".equals(value.get("currentStatusId")));
            if (earlierIncomplete) {
                throw badRequest("An earlier production run task is not completed");
            }
            if ("PRUN_DOC_PRINTED".equals(header.get("currentStatusId"))) {
                changeProductionRunStatus(Map.of("productionRunId", runId, "statusId", "PRUN_RUNNING"));
            }
            changes.put("actualStartDate", Timestamp.from(Instant.now()));
        } else if ("PRUN_COMPLETED".equals(newStatus)) {
            if (!"PRUN_RUNNING".equals(oldStatus)) {
                throw badRequest("Only a running production run task can be completed");
            }
            BigDecimal targetQuantity = decimal(task.get("quantityToProduce"));
            if (targetQuantity.compareTo(BigDecimal.ZERO) == 0) {
                targetQuantity = decimal(header.get("quantityToProduce"));
            }
            BigDecimal produced = decimal(task.get("quantityProduced"));
            BigDecimal rejected = decimal(task.get("quantityRejected"));
            BigDecimal shortage = targetQuantity.subtract(produced.add(rejected));
            if (shortage.compareTo(BigDecimal.ZERO) > 0) {
                produced = produced.add(shortage);
            }
            changes.put("actualCompletionDate", Timestamp.from(Instant.now()));
            changes.put("quantityProduced", produced);
            if (task.get("actualSetupMillis") == null) {
                changes.put("actualSetupMillis", decimal(task.get("estimatedSetupMillis")));
            }
            if (task.get("actualMilliSeconds") == null) {
                changes.put("actualMilliSeconds", decimal(task.get("estimatedMilliSeconds")).multiply(produced));
            }
        } else {
            throw badRequest("Unsupported production run task status: " + newStatus);
        }
        changes.put("currentStatusId", newStatus);
        workEfforts.updateWorkEffort(taskId, changes);
        if (newStatus.equals("PRUN_COMPLETED")) {
            List<Map<String, Object>> tasks = workEfforts.findChildTasks(runId);
            if (tasks.stream().allMatch(value -> "PRUN_COMPLETED".equals(value.get("currentStatusId")))) {
                changeProductionRunStatus(Map.of("productionRunId", runId, "statusId", "PRUN_COMPLETED"));
            }
        }
        return Map.of("oldStatusId", oldStatus, "newStatusId", newStatus);
    }

    private Map<String, Object> updateProductionRunTask(Map<String, Object> input) {
        String runId = required(input, "productionRunId");
        String id = required(input, "productionRunTaskId");
        Map<String, Object> task = workEfforts.findWorkEffort(id);
        if (task == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "ServiceValueNotFound");
        }
        if (!runId.equals(text(task.get("workEffortParentId")))) {
            throw badRequest("Production run task does not belong to the requested production run");
        }
        if (!"PRUN_RUNNING".equals(task.get("currentStatusId"))) {
            throw badRequest("Production run task must be running");
        }
        Map<String, Object> changes = new LinkedHashMap<>();
        changes.put("quantityProduced", decimal(task.get("quantityProduced"))
                .add(decimal(input.get("addQuantityProduced"))));
        changes.put("quantityRejected", decimal(task.get("quantityRejected"))
                .add(decimal(input.get("addQuantityRejected"))));
        changes.put("actualSetupMillis", decimal(task.get("actualSetupMillis"))
                .add(decimal(input.get("addSetupTime"))));
        changes.put("actualMilliSeconds", decimal(task.get("actualMilliSeconds"))
                .add(decimal(input.get("addTaskTime"))));
        return workEfforts.updateWorkEffort(id, changes);
    }

    private Map<String, Object> create(String entity, Map<String, ?> values) {
        Object item = entities.newEntity(entities.entityClass(entity));
        values.forEach((name, value) -> entities.set(item, name, value));
        entityManager.persist(item);
        return entities.toMap(item);
    }

    private Map<String, Object> find(String name, Map<String, ?> keys) {
        Object item = entityByPk(name, keys);
        return item == null ? null : entities.toMap(item);
    }

    private Object entityByPk(String name, Map<String, ?> keys) {
        return entities.findByPrimaryKey(entities.entityClass(name), keys);
    }

    private static Map<String, Object> assocKeys(String parent, String component, String type, Timestamp from) {
        return Map.of("productId", parent, "productIdTo", component,
                "productAssocTypeId", type, "fromDate", from);
    }

    private static void copyIfPresent(Map<String, ?> input, Map<String, Object> output, String name) {
        if (input.containsKey(name)) {
            output.put(name, input.get(name));
        }
    }

    private static Object arg(Map<String, ?> input, String name) {
        Object arguments = input.get("arguments");
        if (!(arguments instanceof Map<?, ?> map) || !map.containsKey(name)) {
            throw badRequest("Missing arguments." + name);
        }
        return map.get(name);
    }

    private static BigDecimal decimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        try {
            return value instanceof BigDecimal decimal ? decimal : new BigDecimal(value.toString());
        } catch (NumberFormatException exception) {
            throw badRequest("Invalid decimal value");
        }
    }

    private static BigDecimal requiredDecimal(Map<String, ?> input, String name) {
        Object value = input.get(name);
        if (value == null || value.toString().isBlank()) {
            throw badRequest("Missing required input: " + name);
        }
        return decimal(value);
    }

    private static Timestamp requiredTimestamp(Map<String, ?> input, String name) {
        Object value = input.get(name);
        if (value == null || value.toString().isBlank()) {
            throw badRequest("Missing required input: " + name);
        }
        try {
            return timestamp(value);
        } catch (RuntimeException exception) {
            throw badRequest("Invalid timestamp value for " + name);
        }
    }

    private static BigDecimal requiredComponentQuantity(Map<String, ?> assoc, BigDecimal productionQuantity) {
        BigDecimal componentQuantity = assoc.get("quantity") == null
                ? BigDecimal.ONE : decimal(assoc.get("quantity"));
        return productionQuantity.multiply(componentQuantity).multiply(scrapMultiplier(assoc.get("scrapFactor")));
    }

    private static BigDecimal scrapMultiplier(Object scrapValue) {
        BigDecimal percent = scrapValue == null ? BigDecimal.ZERO : decimal(scrapValue);
        BigDecimal hundred = new BigDecimal("100");
        if (percent.compareTo(hundred.negate()) > 0 && percent.compareTo(hundred) < 0) {
            return BigDecimal.ONE.add(percent.movePointLeft(2));
        }
        return BigDecimal.ONE;
    }

    private static boolean activeOn(Map<String, ?> value, Timestamp date) {
        Timestamp from = timestamp(value.get("fromDate"));
        Timestamp thru = timestamp(value.get("thruDate"));
        return (from == null || !from.after(date)) && (thru == null || thru.after(date));
    }

    private static void validateFields(Map<String, ?> input, Set<String> allowed) {
        Set<String> unknown = new LinkedHashSet<>(input.keySet());
        unknown.removeAll(allowed);
        if (!unknown.isEmpty()) {
            throw badRequest("Undeclared input fields: " + String.join(", ", unknown));
        }
    }

    private static Timestamp timestamp(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Timestamp timestamp) {
            return timestamp;
        }
        if (value instanceof Date date) {
            return new Timestamp(date.getTime());
        }
        try {
            return Timestamp.from(Instant.parse(value.toString()));
        } catch (RuntimeException exception) {
            return Timestamp.valueOf(value.toString().replace('T', ' ').replace("Z", ""));
        }
    }

    private static String required(Map<String, ?> input, String name) {
        String value = text(input.get(name));
        if (value.isBlank()) {
            throw badRequest("Missing required input: " + name);
        }
        return value;
    }

    private static String text(Object value) {
        return value == null ? "" : value.toString();
    }

    private static MigrationException badRequest(String message) {
        return new MigrationException(HttpStatus.BAD_REQUEST, message);
    }
}
