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
        String productId = required(input, "productId");
        String componentId = required(input, "productIdTo");
        String type = text(input.getOrDefault("productAssocTypeId", BOM_TYPE));
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
        if (isAncestor(componentId, productId, type, fromDate)) {
            throw badRequest("ManufacturingDuplicatedComponentInBOM: " + productId + " " + componentId);
        }
        Map<String, Object> values = new LinkedHashMap<>(input);
        values.putAll(keys);
        values.putIfAbsent("quantity", BigDecimal.ONE);
        values.putIfAbsent("scrapFactor", BigDecimal.ONE);
        return products.createProductAssoc(values);
    }

    private Map<String, Object> copyBomAssocs(Map<String, Object> input) {
        String source = required(input, "productId");
        String destination = required(input, "copyToProductId");
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put("productId", source);
        copyIfPresent(input, filter, "productAssocTypeId");
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
            Map<String, Object> values = new LinkedHashMap<>(assoc);
            values.put("productId", destination);
            if (input.get("fromDate") != null) {
                values.put("fromDate", timestamp(input.get("fromDate")));
            }
            if (input.get("thruDate") != null) {
                values.put("thruDate", timestamp(input.get("thruDate")));
            }
            if (!destination.equals(values.get("productIdTo"))
                    && products.findProductAssoc(assocKeys(destination, text(values.get("productIdTo")),
                            text(values.get("productAssocTypeId")), timestamp(values.get("fromDate"))) ) == null
                    && !isAncestor(text(values.get("productIdTo")), destination,
                            text(values.get("productAssocTypeId")), timestamp(values.get("fromDate")))) {
                products.createProductAssoc(values);
                copied++;
            }
        }
        return Map.of("copied", copied);
    }

    private Map<String, Object> addRule(Map<String, Object> input) {
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
        Map<String, Object> duplicated = isAncestor(component, product, type, date)
                ? Map.of("productId", product, "productIdTo", component) : Map.of();
        return Map.of("duplicatedProductAssoc", duplicated);
    }

    private boolean isAncestor(String start, String target, String type, Timestamp date) {
        Deque<String> pending = new ArrayDeque<>();
        Set<String> visited = new java.util.HashSet<>();
        pending.add(start);
        while (!pending.isEmpty()) {
            String current = pending.removeFirst();
            if (!visited.add(current)) {
                continue;
            }
            for (Map<String, Object> parent : products.findProductAssocs(
                    Map.of("productIdTo", current, "productAssocTypeId", type))) {
                Timestamp from = timestamp(parent.get("fromDate"));
                Timestamp thru = timestamp(parent.get("thruDate"));
                if ((from == null || !from.after(date)) && (thru == null || thru.after(date))) {
                    String parentId = text(parent.get("productId"));
                    if (target.equals(parentId)) {
                        return true;
                    }
                    pending.addLast(parentId);
                }
            }
        }
        return false;
    }

    private Map<String, Object> getProductRouting(Map<String, Object> input) {
        String id = text(input.get("workEffortId"));
        if (id.isBlank()) {
            String productId = required(input, "productId");
            List<Map<String, Object>> standards = workEfforts.findWorkEffortGoodStandards(
                    Map.of("productId", productId, "workEffortGoodStdTypeId", "ROU_PROD_TEMPLATE"));
            if (!standards.isEmpty()) {
                id = text(standards.get(0).get("workEffortId"));
            } else if (!Boolean.TRUE.equals(input.get("ignoreDefaultRouting"))) {
                id = "DEFAULT_ROUTING";
            }
        }
        Map<String, Object> routing = id.isBlank() ? null : workEfforts.findWorkEffort(id);
        if (routing == null) {
            throw badRequest("Manufacturing product routing not found");
        }
        List<Map<String, Object>> tasks = workEfforts.findWorkEffortAssocs(Map.of(
                "workEffortIdFrom", id, "workEffortAssocTypeId", "ROUTING_COMPONENT"));
        tasks = tasks.stream().sorted((left, right) -> text(left.get("sequenceNum"))
                .compareTo(text(right.get("sequenceNum")))).toList();
        return Map.of("workEffortId", id, "routing", routing, "tasks", tasks);
    }

    private Map<String, Object> getManufacturingComponents(Map<String, Object> input) {
        String productId = required(input, "productId");
        BigDecimal quantity = input.get("quantity") == null ? BigDecimal.ONE : decimal(input.get("quantity"));
        List<Map<String, Object>> components = new ArrayList<>();
        for (Map<String, Object> assoc : products.findProductAssocs(
                Map.of("productId", productId, "productAssocTypeId", BOM_TYPE))) {
            BigDecimal factor = assoc.get("quantity") == null ? BigDecimal.ONE : decimal(assoc.get("quantity"));
            BigDecimal scrap = assoc.get("scrapFactor") == null ? BigDecimal.ONE : decimal(assoc.get("scrapFactor"));
            Map<String, Object> item = new LinkedHashMap<>(assoc);
            item.put("requiredQuantity", quantity.multiply(factor).multiply(scrap));
            components.add(item);
        }
        return Map.of("components", components);
    }

    private Map<String, Object> createProductionRun(Map<String, Object> input) {
        String productId = required(input, "productId");
        if (products.findProduct(productId) == null) {
            throw badRequest("Product not found: " + productId);
        }
        BigDecimal quantity = input.get("quantityToProduce") == null
                ? BigDecimal.ONE : decimal(input.get("quantityToProduce"));
        Map<String, Object> routing = getProductRouting(input);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> routingTasks = (List<Map<String, Object>>) routing.get("tasks");
        if (routingTasks.isEmpty()) {
            throw badRequest("Manufacturing routing has no tasks");
        }
        String productionRunId = sequences.nextId("WorkEffort");
        Timestamp start = input.get("estimatedStartDate") == null
                ? Timestamp.from(Instant.now()) : timestamp(input.get("estimatedStartDate"));
        Map<String, Object> header = workEfforts.createWorkEffort(Map.of(
                "workEffortId", productionRunId,
                "workEffortTypeId", "PROD_ORDER_HEADER",
                "currentStatusId", "PRUN_CREATED",
                "quantityToProduce", quantity,
                "facilityId", input.getOrDefault("facilityId", "MFG_PLANT"),
                "estimatedStartDate", start,
                "workEffortName", input.getOrDefault("workEffortName", "Manufacturing run")));
        workEfforts.createWorkEffortGoodStandard(Map.of(
                "workEffortId", productionRunId, "productId", productId,
                "workEffortGoodStdTypeId", "PRUN_PROD_DELIV", "statusId", "WEGS_CREATED",
                "estimatedQuantity", quantity, "fromDate", start));
        Timestamp completion = start;
        for (Map<String, Object> routingTask : routingTasks) {
            String taskId = sequences.nextId("WorkEffort");
            BigDecimal duration = decimal(routingTask.getOrDefault("estimatedSetupMillis", 0))
                    .add(decimal(routingTask.getOrDefault("estimatedMilliSeconds", 0)).multiply(quantity));
            completion = new Timestamp(completion.getTime() + duration.longValue());
            Map<String, Object> task = new LinkedHashMap<>();
            task.put("workEffortId", taskId);
            task.put("workEffortTypeId", "PROD_ORDER_TASK");
            task.put("workEffortParentId", productionRunId);
            task.put("currentStatusId", "PRUN_CREATED");
            task.put("priority", routingTask.getOrDefault("sequenceNum", "10"));
            task.put("estimatedStartDate", start);
            task.put("estimatedCompletionDate", completion);
            workEfforts.createWorkEffort(task);
            workEfforts.createWorkEffortAssoc(Map.of(
                    "workEffortIdFrom", productionRunId, "workEffortIdTo", taskId,
                    "workEffortAssocTypeId", "WORK_EFF_TEMPLATE", "fromDate", start));
        }
        List<Map<String, Object>> tasks = workEfforts.findChildTasks(productionRunId);
        String taskId = text(tasks.get(0).get("workEffortId"));
        for (Map<String, Object> component : products.findProductAssocs(
                Map.of("productId", productId, "productAssocTypeId", BOM_TYPE))) {
            BigDecimal componentQuantity = decimal(component.getOrDefault("quantity", BigDecimal.ONE))
                    .multiply(quantity).multiply(decimal(component.getOrDefault("scrapFactor", BigDecimal.ONE)));
            workEfforts.createWorkEffortGoodStandard(Map.of(
                    "workEffortId", taskId,
                    "productId", component.get("productIdTo"),
                    "workEffortGoodStdTypeId", "PRUNT_PROD_NEEDED",
                    "estimatedQuantity", componentQuantity,
                    "statusId", "WEGS_CREATED", "fromDate", start));
        }
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
        }
        return Map.of("newStatusId", newStatus);
    }

    private Map<String, Object> changeProductionRunTaskStatus(Map<String, Object> input) {
        String runId = required(input, "productionRunId");
        String taskId = required(input, "workEffortId");
        Map<String, Object> task = workEfforts.findWorkEffort(taskId);
        if (task == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "ServiceValueNotFound");
        }
        String oldStatus = text(task.get("currentStatusId"));
        String newStatus = text(input.get("statusId"));
        if (newStatus.isBlank()) {
            newStatus = oldStatus.equals("PRUN_RUNNING") ? "PRUN_COMPLETED" : "PRUN_RUNNING";
        }
        Map<String, Object> header = workEfforts.findWorkEffort(runId);
        if ("PRUN_RUNNING".equals(newStatus) && header != null
                && "PRUN_CREATED".equals(header.get("currentStatusId"))) {
            throw badRequest("Production run must be printed before a task starts");
        }
        if (newStatus.equals("PRUN_RUNNING") && header != null
                && !"PRUN_RUNNING".equals(header.get("currentStatusId"))) {
            changeProductionRunStatus(Map.of("productionRunId", runId, "statusId", "PRUN_RUNNING"));
        }
        workEfforts.updateWorkEffort(taskId, Map.of("currentStatusId", newStatus));
        if (newStatus.equals("PRUN_COMPLETED")) {
            List<Map<String, Object>> tasks = workEfforts.findChildTasks(runId);
            if (tasks.stream().allMatch(value -> "PRUN_COMPLETED".equals(value.get("currentStatusId")))) {
                changeProductionRunStatus(Map.of("productionRunId", runId, "statusId", "PRUN_COMPLETED"));
            }
        }
        return Map.of("oldStatusId", oldStatus, "newStatusId", newStatus);
    }

    private Map<String, Object> updateProductionRunTask(Map<String, Object> input) {
        String id = text(input.getOrDefault("productionRunTaskId", input.get("workEffortId")));
        if (id.isBlank()) {
            throw badRequest("Missing required input: productionRunTaskId");
        }
        Map<String, Object> task = workEfforts.findWorkEffort(id);
        if (task == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "ServiceValueNotFound");
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
