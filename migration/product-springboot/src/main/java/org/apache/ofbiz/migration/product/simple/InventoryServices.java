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
package org.apache.ofbiz.migration.product.simple;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.common.SequenceService;
import org.apache.ofbiz.migration.product.adapter.ProductUnportedPort;
import org.apache.ofbiz.migration.product.generated.ProductServiceMetadata;
import org.apache.ofbiz.migration.product.generated.ServiceCatalog;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** Minilang source: applications/product/minilang/product/inventory/InventoryServices.xml. */
@Component
public class InventoryServices extends UnportedSimpleMethodFile {
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private final ProductUnportedPort unportedPort;
    private final EntityRegistry entities;
    private final SequenceService sequences;
    private final EntityManager entityManager;
    private final MigrationServiceDispatcher dispatcher;
    private final InventoryReserveServices inventoryReserveServices;

    public InventoryServices(
            ProductUnportedPort unportedPort,
            EntityRegistry entities,
            SequenceService sequences,
            EntityManager entityManager,
            @Lazy MigrationServiceDispatcher dispatcher,
            @Lazy InventoryReserveServices inventoryReserveServices) {
        super(unportedPort);
        this.unportedPort = unportedPort;
        this.entities = entities;
        this.sequences = sequences;
        this.entityManager = entityManager;
        this.dispatcher = dispatcher;
        this.inventoryReserveServices = inventoryReserveServices;
    }

    @Override
    public Map<String, Object> execute(String serviceName, String methodName, Map<String, Object> input) {
        return switch (methodName) {
            case "createInventoryItemCheckSetAtpQoh" -> createInventoryItemCheckSetAtpQoh(input);
            case "updateInventoryItem" -> updateInventoryItem(serviceName, input);
            case "createInventoryItemStatus" -> createInventoryItemStatus(input);
            case "createInventoryItemDetail" -> createInventoryItemDetail(input);
            case "updateInventoryItemFromDetail" -> updateInventoryItemFromDetail(input);
            case "updateSerializedInventoryTotals" -> updateSerializedInventoryTotals(input);
            case "checkProductInventoryDiscontinuation" -> checkProductInventoryDiscontinuation(input);
            case "createInventoryItemVariance" -> createInventoryItemVariance(input);
            case "createPhysicalInventoryAndVariance" -> createPhysicalInventoryAndVariance(input);
            case "getProductInventoryAvailable" -> getProductInventoryAvailable(input);
            case "countProductInventoryOnHand" -> countProductInventoryOnHand(input);
            case "countProductInventoryShippedForSales" -> countProductInventoryShippedForSales(input);
            case "getMktgPackagesAvailable" -> getMktgPackagesAvailable(input);
            case "balanceInventoryItems" -> balanceInventoryItems(input);
            case "reassignInventoryReservations" -> reassignInventoryReservations(input);
            case "reassignInventoryReservationsByAllocationPlan" -> reassignInventoryReservationsByAllocationPlan(input);
            case "balanceOrderItemsWithNegativeReservations" -> balanceOrderItemsWithNegativeReservations(input);
            case "createInventoryTransfer" -> createInventoryTransfer(input);
            case "updateInventoryTransfer" -> updateInventoryTransfer(input);
            case "createInventoryTransfersForProduct" -> createInventoryTransfersForProduct(input);
            case "changeOwnerUponIssuance" -> changeOwnerUponIssuance(input);
            case "setOrderReservationPriority" -> setOrderReservationPriority(input);
            case "setLastInventoryCount" -> setLastInventoryCount(input);
            case "createInventoryItemLabelAppl" -> createInventoryItemLabelAppl(input);
            default -> super.execute(serviceName, methodName, input);
        };
    }

    /** applications/product/minilang/product/inventory/InventoryServices.xml:25-41; calls createInventoryItemDetail, including detail ECAs. */
    private Map<String, Object> createInventoryItemCheckSetAtpQoh(Map<String, Object> input) {
        if (!empty(input.get("availableToPromiseTotal")) || !empty(input.get("quantityOnHandTotal"))) {
            Map<String, Object> detail = values("inventoryItemId", input.get("inventoryItemId"),
                    "availableToPromiseDiff", input.get("availableToPromiseTotal"),
                    "quantityOnHandDiff", input.get("quantityOnHandTotal"));
            call("createInventoryItemDetail", detail);
        }
        return new LinkedHashMap<>();
    }

    /** applications/product/minilang/product/inventory/InventoryServices.xml:42-84; reproduces the serialized-inventory EECA; async discontinuation is a no-op. */
    private Map<String, Object> updateInventoryItem(String service, Map<String, Object> input) {
        Object item = findByPk("InventoryItem", serviceInput(service, input));
        require(item, "InventoryItem");
        Map<String, Object> result = values("oldOwnerPartyId", get(item, "ownerPartyId"),
                "oldProductId", get(item, "productId"), "oldStatusId", get(item, "statusId"));
        if (empty(result.get("oldOwnerPartyId"))) {
            Object facility = findByPk("Facility", values("facilityId", get(item, "facilityId")));
            result.put("oldOwnerPartyId", get(facility, "ownerPartyId"));
            if (facility != null && get(facility, "ownerPartyId") != null) {
                set(item, "ownerPartyId", get(facility, "ownerPartyId"));
            }
        }
        Object cost = input.get("unitCost");
        if (!empty(cost) && decimal(cost).signum() < 0) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Unit cost for inventory item "
                    + get(item, "inventoryItemId") + " cannot be negative.");
        }
        Object previousCost = get(item, "unitCost");
        Object lotId = input.get("lotId");
        if (!empty(lotId) && findByPk("Lot", Map.of("lotId", lotId)) == null) {
            Object lot = newEntity("Lot");
            set(lot, "lotId", lotId);
            entityManager.persist(lot);
        }
        setFields(item, service, input, false);
        entityManager.flush();
        if (!empty(cost) && !same(cost, previousCost)) {
            call("createInventoryItemDetail",
                    values("inventoryItemId", get(item, "inventoryItemId"), "unitCost", cost));
        }
        call("updateSerializedInventoryTotals",
                values("inventoryItemId", get(item, "inventoryItemId")));
        return result;
    }

    /** applications/product/minilang/product/inventory/InventoryServices.xml:86-113; sources userLogin.userLoginId from Spring Security authentication. */
    private Map<String, Object> createInventoryItemStatus(Map<String, Object> input) {
        String inventoryItemId = String.valueOf(input.get("inventoryItemId"));
        List<?> previous = find("InventoryItemStatus", Map.of("inventoryItemId", inventoryItemId));
        Object latest = previous.stream().max(Comparator.comparing(
                row -> timestamp(get(row, "statusDatetime")),
                Comparator.nullsFirst(Comparator.naturalOrder()))).orElse(null);
        Timestamp current = now();
        if (latest != null) {
            set(latest, "statusEndDatetime", current);
        }
        Object status = newEntity("InventoryItemStatus");
        setFields(status, "createInventoryItemStatus", input, true);
        setFields(status, "createInventoryItemStatus", input, false);
        set(status, "statusDatetime", current);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            set(status, "changeByUserLoginId", authentication.getName());
        }
        if (empty(get(status, "productId"))) {
            Object item = require(findByPk("InventoryItem", Map.of("inventoryItemId", inventoryItemId)),
                    "InventoryItem");
            set(status, "productId", get(item, "productId"));
        }
        entityManager.persist(status);
        return new LinkedHashMap<>();
    }

    /** applications/product/minilang/product/inventory/InventoryServices.xml:115-140; reproduces updateInventoryItemFromDetail and setLastInventoryCount EECAs. */
    private Map<String, Object> createInventoryItemDetail(Map<String, Object> input) {
        Object detail = newEntity("InventoryItemDetail");
        setFields(detail, "createInventoryItemDetail", input, true);
        setFields(detail, "createInventoryItemDetail", input, false);
        String sequenceId = sequences.nextId("InventoryItemDetail");
        set(detail, "inventoryItemDetailSeqId", sequenceId);
        Timestamp effectiveDate = now();
        if (!empty(input.get("itemIssuanceId"))) {
            Object issuance = findByPk("ItemIssuance",
                    Map.of("itemIssuanceId", input.get("itemIssuanceId")));
            if (issuance != null) {
                effectiveDate = timestamp(get(issuance, "issuedDateTime"));
            }
        }
        set(detail, "effectiveDate", effectiveDate);
        for (String field : List.of("availableToPromiseDiff", "quantityOnHandDiff", "accountingQuantityDiff")) {
            if (get(detail, field) == null) {
                set(detail, field, ZERO);
            }
        }
        entityManager.persist(detail);
        entityManager.flush();
        call("updateInventoryItemFromDetail",
                values("inventoryItemId", get(detail, "inventoryItemId")));
        if (decimal(get(detail, "availableToPromiseDiff")).signum() != 0) {
            call("setLastInventoryCount", values("inventoryItemId", get(detail, "inventoryItemId")));
        }
        return values("inventoryItemDetailSeqId", sequenceId);
    }

    /** applications/product/minilang/product/inventory/InventoryServices.xml:141-149; reproduces the serialized-inventory EECA on store. */
    private Map<String, Object> updateInventoryItemFromDetail(Map<String, Object> input) {
        Object id = input.get("inventoryItemId");
        Object item = require(findByPk("InventoryItem", Map.of("inventoryItemId", id)), "InventoryItem");
        Object[] totals = (Object[]) entityManager.createQuery(
                        "select sum(d.availableToPromiseDiff), sum(d.quantityOnHandDiff), "
                                + "sum(d.accountingQuantityDiff) from InventoryItemDetail d "
                                + "where d.inventoryItemId = :id")
                .setParameter("id", id).getSingleResult();
        if (totals[0] != null) {
            set(item, "availableToPromiseTotal", totals[0]);
            set(item, "quantityOnHandTotal", totals[1]);
            set(item, "accountingQuantityTotal", totals[2]);
        }
        call("updateSerializedInventoryTotals", values("inventoryItemId", id));
        return new LinkedHashMap<>();
    }

    /** applications/product/minilang/product/inventory/InventoryServices.xml:151-212; does not trigger a second serialized-inventory EECA pass. */
    private Map<String, Object> updateSerializedInventoryTotals(Map<String, Object> input) {
        Object item = require(findByPk("InventoryItem",
                Map.of("inventoryItemId", input.get("inventoryItemId"))), "InventoryItem");
        if (!"SERIALIZED_INV_ITEM".equals(get(item, "inventoryItemTypeId"))) {
            return new LinkedHashMap<>();
        }
        String status = String.valueOf(get(item, "statusId"));
        Object atpValue = get(item, "availableToPromiseTotal");
        Object qohValue = get(item, "quantityOnHandTotal");
        BigDecimal atp = decimal(atpValue);
        BigDecimal qoh = decimal(qohValue);
        BigDecimal desiredAtp;
        BigDecimal desiredQoh;
        if ("INV_AVAILABLE".equals(status)) {
            desiredAtp = BigDecimal.ONE;
            desiredQoh = BigDecimal.ONE;
        } else if ("INV_DELIVERED".equals(status)) {
            desiredAtp = ZERO;
            desiredQoh = ZERO;
        } else {
            desiredAtp = ZERO;
            desiredQoh = BigDecimal.ONE;
        }
        if (atpValue == null || qohValue == null
                || atp.compareTo(desiredAtp) != 0 || qoh.compareTo(desiredQoh) != 0) {
            set(item, "availableToPromiseTotal", desiredAtp);
            set(item, "quantityOnHandTotal", desiredQoh);
        }
        return new LinkedHashMap<>();
    }

    /** applications/product/minilang/product/inventory/InventoryServices.xml:214-277; inlines discontinueProductSales; async discontinuation EECA is a no-op. */
    private Map<String, Object> checkProductInventoryDiscontinuation(Map<String, Object> input) {
        Object product = findByPk("Product", Map.of("productId", input.get("productId")));
        if (product == null) {
            return new LinkedHashMap<>();
        }
        Object virtual = null;
        if ("Y".equals(get(product, "isVariant"))) {
            List<?> associations = find("ProductAssoc", values("productIdTo", get(product, "productId"),
                    "productAssocTypeId", "PRODUCT_VARIANT"));
            Object association = associations.stream().filter(this::active).findFirst().orElse(null);
            if (association != null) {
                virtual = findByPk("Product", Map.of("productId", get(association, "productId")));
                if (empty(get(product, "salesDiscWhenNotAvail")) && virtual != null) {
                    set(product, "salesDiscWhenNotAvail", get(virtual, "salesDiscWhenNotAvail"));
                }
            }
        }
        Timestamp discontinuation = timestamp(get(product, "salesDiscontinuationDate"));
        if ("Y".equals(get(product, "salesDiscWhenNotAvail"))
                && (discontinuation == null || discontinuation.after(now()))) {
            Map<String, Object> availability = call("getProductInventoryAvailable",
                    values("productId", input.get("productId")));
            if (decimal(availability.get("availableToPromiseTotal")).signum() <= 0) {
                discontinueProductSales(String.valueOf(input.get("productId")));
            }
            if (virtual != null && "Y".equals(get(virtual, "salesDiscWhenNotAvail"))) {
                List<?> variants = find("ProductAssoc", values("productId", get(virtual, "productId"),
                        "productAssocTypeId", "PRODUCT_VARIANT"));
                if (variants.stream().noneMatch(this::active)) {
                    discontinueProductSales(String.valueOf(get(virtual, "productId")));
                }
            }
        }
        return new LinkedHashMap<>();
    }

    /** applications/product/minilang/product/inventory/InventoryServices.xml:280-309; calls createInventoryItemDetail, including detail ECAs. */
    private Map<String, Object> createInventoryItemVariance(Map<String, Object> input) {
        Object item = require(findByPk("InventoryItem",
                serviceInput("createInventoryItemVariance", input)), "InventoryItem");
        if (!"NON_SERIAL_INV_ITEM".equals(get(item, "inventoryItemTypeId"))) {
            throw new MigrationException(HttpStatus.BAD_REQUEST,
                    "Can only create an InventoryItemVariance for a Non-Serialized Inventory Item");
        }
        call("createInventoryItemDetail", values("inventoryItemId", input.get("inventoryItemId"),
                "physicalInventoryId", input.get("physicalInventoryId"),
                "availableToPromiseDiff", input.get("availableToPromiseVar"),
                "quantityOnHandDiff", input.get("quantityOnHandVar"),
                "accountingQuantityDiff", input.get("quantityOnHandVar"),
                "reasonEnumId", input.get("varianceReasonId"), "description", input.get("comments")));
        Object variance = newEntity("InventoryItemVariance");
        setFields(variance, "createInventoryItemVariance", input, true);
        setFields(variance, "createInventoryItemVariance", input, false);
        entityManager.persist(variance);
        return new LinkedHashMap<>();
    }

    /** applications/product/minilang/product/inventory/InventoryServices.xml:311-319; dispatches createPhysicalInventory and createInventoryItemVariance. */
    private Map<String, Object> createPhysicalInventoryAndVariance(Map<String, Object> input) {
        Map<String, Object> physical = call("createPhysicalInventory", input);
        Map<String, Object> variance = new LinkedHashMap<>(input);
        variance.put("physicalInventoryId", physical.get("physicalInventoryId"));
        call("createInventoryItemVariance", variance);
        return values("physicalInventoryId", physical.get("physicalInventoryId"));
    }

    /** applications/product/minilang/product/inventory/InventoryServices.xml:325-398 */
    private Map<String, Object> getProductInventoryAvailable(Map<String, Object> input) {
        Map<String, Object> filters = new LinkedHashMap<>();
        for (String field : List.of("inventoryItemId", "productId", "facilityId", "partyId",
                "locationSeqId", "containerId", "lotId")) {
            if (input.get(field) != null) {
                filters.put(field, input.get(field));
            }
        }
        BigDecimal atp = ZERO;
        BigDecimal qoh = ZERO;
        BigDecimal accounting = ZERO;
        for (Object item : find("InventoryItem", filters)) {
            Object status = get(item, "statusId");
            Object requested = input.get("statusId");
            boolean allowed = !empty(requested) ? same(requested, status)
                    : empty(status) || "INV_AVAILABLE".equals(status) || "INV_NS_RETURNED".equals(status)
                            || "SERIALIZED_INV_ITEM".equals(get(item, "inventoryItemTypeId"));
            if (allowed) {
                atp = atp.add(decimal(get(item, "availableToPromiseTotal")));
                qoh = qoh.add(decimal(get(item, "quantityOnHandTotal")));
                accounting = accounting.add(decimal(get(item, "accountingQuantityTotal")));
            }
        }
        return values("availableToPromiseTotal", atp, "quantityOnHandTotal", qoh,
                "accountingQuantityTotal", accounting);
    }

    /** applications/product/minilang/product/inventory/InventoryServices.xml:400-412 */
    private Map<String, Object> countProductInventoryOnHand(Map<String, Object> input) {
        String dateCondition = input.get("inventoryCountDate") == null
                ? "d.effectiveDate is null" : "d.effectiveDate <= :date";
        String query = "select sum(d.quantityOnHandDiff) from InventoryItemDetail d "
                + "join InventoryItem i on i.inventoryItemId = d.inventoryItemId "
                + "where i.facilityId = :facility and i.productId = :product "
                + "and " + dateCondition + " group by i.inventoryItemTypeId, i.facilityId, "
                + "i.productId, i.unitCost, i.currencyUomId";
        var queryBuilder = entityManager.createQuery(query)
                .setParameter("facility", input.get("facilityId"))
                .setParameter("product", input.get("productId"));
        if (input.get("inventoryCountDate") != null) {
            queryBuilder.setParameter("date", input.get("inventoryCountDate"));
        }
        List<?> rows = queryBuilder.setMaxResults(1).getResultList();
        return values("quantityOnHandTotal", rows.isEmpty() ? ZERO : decimal(rows.get(0)));
    }

    /** applications/product/minilang/product/inventory/InventoryServices.xml:414-432 */
    private Map<String, Object> countProductInventoryShippedForSales(Map<String, Object> input) {
        Timestamp thru = input.get("thruDate") == null ? now() : timestamp(input.get("thruDate"));
        String fromCondition = input.get("fromDate") == null
                ? "d.effectiveDate is null" : "d.effectiveDate >= :from";
        String query = "select sum(d.quantityOnHandDiff) from InventoryItemDetail d "
                + "join InventoryItem i on i.inventoryItemId = d.inventoryItemId "
                + "where i.facilityId = :facility and i.productId = :product and " + fromCondition
                + " and d.effectiveDate < :thru "
                + "and d.orderId is not null and d.quantityOnHandDiff < 0 "
                + "group by i.inventoryItemTypeId, i.facilityId, i.productId, i.unitCost, i.currencyUomId";
        var queryBuilder = entityManager.createQuery(query)
                .setParameter("facility", input.get("facilityId"))
                .setParameter("product", input.get("productId"))
                .setParameter("thru", thru);
        if (input.get("fromDate") != null) {
            queryBuilder.setParameter("from", input.get("fromDate"));
        }
        List<?> rows = queryBuilder.setMaxResults(1).getResultList();
        return values("quantityOnHandTotal", rows.isEmpty() ? ZERO : decimal(rows.get(0)).negate());
    }

    /** applications/product/minilang/product/inventory/InventoryServices.xml:434-465; inlines getAssociatedProducts (ProductServices.java:427-534), 501 for nonempty components. */
    private Map<String, Object> getMktgPackagesAvailable(Map<String, Object> input) {
        Object product = require(findByPk("Product", Map.of("productId", input.get("productId"))), "Product");
        String type = hasProductType(get(product, "productTypeId"), "MARKETING_PKG_AUTO")
                ? "MANUF_COMPONENT" : "PRODUCT_COMPONENT";
        List<?> associations = entityManager.createQuery("select a from ProductAssoc a "
                        + "where a.productId = :productId and a.productAssocTypeId = :type "
                        + "order by a.sequenceNum", entities.entityClass("ProductAssoc"))
                .setParameter("productId", input.get("productId"))
                .setParameter("type", type).getResultList();
        if (associations.stream().anyMatch(this::active)) {
            return unportedPort.execute("getProductInventoryAvailableFromAssocProducts");
        }
        return values("availableToPromiseTotal", ZERO, "quantityOnHandTotal", ZERO);
    }

    /** applications/product/minilang/product/inventory/InventoryServices.xml:476-496; fromDate is omitted because nowTimestamp is undefined in this method. */
    private Map<String, Object> balanceInventoryItems(Map<String, Object> input) {
        Object item = require(findByPk("InventoryItem",
                Map.of("inventoryItemId", input.get("inventoryItemId"))), "InventoryItem");
        Map<String, Object> request = values("productId", get(item, "productId"),
                "facilityId", get(item, "facilityId"));
        String query = "select h.planId, h.productId, i.orderId, i.orderItemSeqId, "
                + "i.allocatedQuantity, i.prioritySeqId, i.statusId, i.planMethodEnumId "
                + "from AllocationPlanHeader h "
                + "join AllocationPlanItem i on i.planId = h.planId and i.productId = h.productId "
                + "where h.productId = :product and i.statusId = 'ALLOC_PLAN_ITEM_APRV' "
                + "and i.planMethodEnumId = 'MANUAL' order by i.prioritySeqId";
        List<?> planRows = entityManager.createQuery(query)
                .setParameter("product", get(item, "productId")).getResultList();
        List<Map<String, Object>> plans = new ArrayList<>();
        for (Object planRow : planRows) {
            Object[] fields = (Object[]) planRow;
            plans.add(values("planId", fields[0], "productId", fields[1], "orderId", fields[2],
                    "orderItemSeqId", fields[3], "allocatedQuantity", fields[4],
                    "prioritySeqId", fields[5], "planItemStatusId", fields[6],
                    "planMethodEnumId", fields[7]));
        }
        if (plans.isEmpty()) {
            call("reassignInventoryReservations", request);
        } else {
            request.put("allocationPlanAndItemList", plans);
            call("reassignInventoryReservationsByAllocationPlan", request);
        }
        return new LinkedHashMap<>();
    }

    /** applications/product/minilang/product/inventory/InventoryServices.xml:498-612; inlines checkOrderIsOnBackOrder; reservation services return 501. */
    private Map<String, Object> reassignInventoryReservations(Map<String, Object> input) {
        String query = "select r, i from OrderItemShipGrpInvRes r join InventoryItem i "
                + "on i.inventoryItemId = r.inventoryItemId where i.productId = :product "
                + "and i.facilityId = :facility and i.inventoryItemTypeId = 'NON_SERIAL_INV_ITEM' "
                + "and ((:fromDate is not null and r.currentPromisedDate > :fromDate) "
                + "or r.quantityNotAvailable > 0 or i.availableToPromiseTotal is null "
                + "or i.availableToPromiseTotal < 0) "
                + "order by r.priority, r.currentPromisedDate, r.reservedDatetime, r.sequenceId";
        var reservationQuery = entityManager.createQuery(query)
                .setParameter("product", input.get("productId"))
                .setParameter("facility", input.get("facilityId"))
                .setParameter("fromDate", input.get("fromDate"));
        List<?> rows = reservationQuery.getResultList();
        List<Object> privileged = new ArrayList<>();
        List<Object> reservations = new ArrayList<>();
        for (Object result : rows) {
            Object[] pair = (Object[]) result;
            Object reservation = pair[0];
            String picklistQuery = "select pi from PicklistItem pi join PicklistBin pb "
                    + "on pb.picklistBinId = pi.picklistBinId join Picklist p "
                    + "on p.picklistId = pb.picklistId where pi.orderId = :order "
                    + "and pi.orderItemSeqId = :item and pi.shipGroupSeqId = :ship "
                    + "and pi.inventoryItemId = :inventory and p.statusId not in "
                    + "('PICKLIST_CANCELLED', 'PICKLIST_PICKED')";
            List<?> picklists = entityManager.createQuery(picklistQuery)
                    .setParameter("order", get(reservation, "orderId"))
                    .setParameter("item", get(reservation, "orderItemSeqId"))
                    .setParameter("ship", get(reservation, "shipGroupSeqId"))
                    .setParameter("inventory", get(reservation, "inventoryItemId")).getResultList();
            if (picklists.isEmpty()) {
                Object priorityOrderId = input.get("priorityOrderId");
                Object priorityOrderItemSeqId = input.get("priorityOrderItemSeqId");
                if (same(priorityOrderId, get(reservation, "orderId"))
                        && same(priorityOrderItemSeqId, get(reservation, "orderItemSeqId"))) {
                    privileged.add(reservation);
                } else {
                    reservations.add(reservation);
                }
            }
        }
        List<Object> allReservations = new ArrayList<>(privileged);
        allReservations.addAll(reservations);
        for (Object reservation : allReservations) {
            inventoryReserveServices.execute("cancelOrderItemShipGrpInvRes",
                    "cancelOrderItemShipGrpInvRes", values("orderId", get(reservation, "orderId"),
                            "orderItemSeqId", get(reservation, "orderItemSeqId"),
                            "inventoryItemId", get(reservation, "inventoryItemId"),
                            "shipGroupSeqId", get(reservation, "shipGroupSeqId")));
        }
        Set<Object> touched = new LinkedHashSet<>();
        for (Object reservation : allReservations) {
            if (decimal(get(reservation, "quantityNotAvailable")).signum() > 0) {
                touched.add(get(reservation, "orderId"));
            }
            Object order = findByPk("OrderHeader", values("orderId", get(reservation, "orderId")));
            inventoryReserveServices.execute("reserveProductInventoryByFacility", "reserveProductInventory",
                    values("productId", input.get("productId"), "orderId", get(reservation, "orderId"),
                            "orderItemSeqId", get(reservation, "orderItemSeqId"),
                            "quantity", get(reservation, "quantity"),
                            "reservedDatetime", get(reservation, "reservedDatetime"),
                            "reserveOrderEnumId", get(reservation, "reserveOrderEnumId"),
                            "requireInventory", "N", "shipGroupSeqId", get(reservation, "shipGroupSeqId"),
                            "sequenceId", get(reservation, "sequenceId"), "facilityId", input.get("facilityId"),
                            "priority", get(order, "priority")));
        }
        Set<Object> noLongerBackordered = new LinkedHashSet<>();
        for (Object orderId : touched) {
            if (!isBackOrder(orderId)) {
                noLongerBackordered.add(orderId);
            }
        }
        return noLongerBackordered.isEmpty()
                ? new LinkedHashMap<>()
                : values("noLongerOnBackOrderIdSet", noLongerBackordered);
    }

    /** applications/product/minilang/product/inventory/InventoryServices.xml:614-704; inlines checkOrderIsOnBackOrder; reserveProductInventoryByFacility returns 501. */
    private Map<String, Object> reassignInventoryReservationsByAllocationPlan(Map<String, Object> input) {
        Object value = input.get("allocationPlanAndItemList");
        Timestamp current = now();
        Set<Object> touched = new LinkedHashSet<>();
        if (value instanceof Iterable<?> plans) {
            for (Object plan : plans) {
                Object orderId = get(plan, "orderId");
                Object orderItemSeqId = get(plan, "orderItemSeqId");
                Object order = findByPk("OrderHeader", values("orderId", orderId));
                Object store = findByPk("ProductStore", values("productStoreId", get(order, "productStoreId")));
                if (!"Y".equals(get(store, "allocateInventory"))) {
                    continue;
                }
                Object orderItem = findByPk("OrderItem",
                        values("orderId", orderId, "orderItemSeqId", orderItemSeqId));
                Timestamp reserveAfter = timestamp(get(orderItem, "reserveAfterDate"));
                if (reserveAfter != null && reserveAfter.after(current)) {
                    continue;
                }
                Object allocated = get(plan, "allocatedQuantity");
                if (decimal(allocated).signum() > 0) {
                    touched.add(orderId);
                }
                List<?> shipGroups = find("OrderItemShipGroup", values("orderId", orderId));
                Object shipGroup = shipGroups.isEmpty() ? null : shipGroups.get(0);
                List<?> oldReservations = find("OrderItemShipGrpInvRes",
                        values("orderId", orderId, "orderItemSeqId", orderItemSeqId,
                                "shipGroupSeqId", get(shipGroup, "shipGroupSeqId")));
                BigDecimal alreadyReserved = ZERO;
                for (Object reservation : oldReservations) {
                    alreadyReserved = alreadyReserved.add(decimal(get(reservation, "quantity"))
                            .subtract(decimal(get(reservation, "quantityNotAvailable"))));
                }
                BigDecimal toReserve = decimal(allocated).subtract(alreadyReserved);
                inventoryReserveServices.execute("reserveProductInventoryByFacility", "reserveProductInventory",
                        values("productId", input.get("productId"), "orderId", orderId,
                                "orderItemSeqId", orderItemSeqId, "quantity", toReserve,
                                "reservedDatetime", current, "requireInventory", "Y",
                                "shipGroupSeqId", get(shipGroup, "shipGroupSeqId"),
                                "facilityId", input.get("facilityId"),
                                "priority", get(plan, "prioritySeqId") == null
                                        ? null : String.valueOf(get(plan, "prioritySeqId"))));
            }
        }
        Set<Object> noLongerBackordered = new LinkedHashSet<>();
        for (Object orderId : touched) {
            if (!isBackOrder(orderId)) {
                noLongerBackordered.add(orderId);
            }
        }
        return noLongerBackordered.isEmpty()
                ? new LinkedHashMap<>()
                : values("noLongerOnBackOrderIdSet", noLongerBackordered);
    }

    private boolean isBackOrder(Object orderId) {
        Long count = entityManager.createQuery("select count(r) from OrderItemShipGrpInvRes r "
                        + "where r.orderId = :orderId and r.quantityNotAvailable is not null "
                        + "and r.quantityNotAvailable > 0", Long.class)
                .setParameter("orderId", orderId).getSingleResult();
        return count > 0;
    }

    /** applications/product/minilang/product/inventory/InventoryServices.xml:706-737; dispatches reassignInventoryReservations for negative reservations. */
    private Map<String, Object> balanceOrderItemsWithNegativeReservations(Map<String, Object> input) {
        Object order = require(findByPk("OrderHeader",
                Map.of("orderId", input.get("orderId"))), "OrderHeader");
        Object store = findByPk("ProductStore", Map.of("productStoreId", get(order, "productStoreId")));
        if (!"Y".equals(get(store, "balanceResOnOrderCreation"))) {
            return new LinkedHashMap<>();
        }
        String query = "select oi, r, i from OrderItem oi join OrderItemShipGrpInvRes r "
                + "on r.orderId = oi.orderId and r.orderItemSeqId = oi.orderItemSeqId "
                + "join InventoryItem i on i.inventoryItemId = r.inventoryItemId "
                + "where r.orderId = :orderId and r.quantityNotAvailable > 0";
        List<?> rows = entityManager.createQuery(query)
                .setParameter("orderId", input.get("orderId")).getResultList();
        for (Object row : rows) {
            Object[] triple = (Object[]) row;
            Timestamp fromDate = timestamp(get(triple[0], "shipBeforeDate"));
            call("reassignInventoryReservations", values("productId", get(triple[2], "productId"),
                    "facilityId", get(triple[2], "facilityId"),
                    "fromDate", fromDate == null ? now() : fromDate));
        }
        return new LinkedHashMap<>();
    }

    /** applications/product/minilang/product/inventory/InventoryServices.xml:740-748 */
    private Map<String, Object> createInventoryTransfer(Map<String, Object> input) {
        Object transfer = newEntity("InventoryTransfer");
        Map<String, Object> entityFields = new LinkedHashMap<>(input);
        entityFields.remove("xferQty");
        setFields(transfer, "createInventoryTransfer", entityFields, false);
        String id = sequences.nextId("InventoryTransfer");
        set(transfer, "inventoryTransferId", id);
        entityManager.persist(transfer);
        return values("inventoryTransferId", id);
    }

    /** applications/product/minilang/product/inventory/InventoryServices.xml:749-768 */
    private Map<String, Object> updateInventoryTransfer(Map<String, Object> input) {
        Object transfer = require(findByPk("InventoryTransfer",
                Map.of("inventoryTransferId", input.get("inventoryTransferId"))), "InventoryTransfer");
        Object status = input.get("statusId");
        if (!empty(status) && findByPk("StatusValidChange",
                values("statusId", get(transfer, "statusId"), "statusIdTo", status)) == null) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "ERROR: Changing the status from "
                    + get(transfer, "statusId") + " to " + status + " is not allowed.");
        }
        setFields(transfer, "updateInventoryTransfer", input, false);
        return new LinkedHashMap<>();
    }

    /** applications/product/minilang/product/inventory/InventoryServices.xml:770-877; skips the unused lookup and dispatches createInventoryTransfer. */
    private Map<String, Object> createInventoryTransfersForProduct(Map<String, Object> input) {
        Object product = require(findByPk("Product", Map.of("productId", input.get("productId"))), "Product");
        Object productType = require(findByPk("ProductType",
                values("productTypeId", get(product, "productTypeId"))), "ProductType");
        BigDecimal requested = decimal(input.get("quantity"));
        BigDecimal remaining = requested;
        if ("N".equals(get(productType, "isPhysical"))) {
            remaining = ZERO;
        } else {
            String reserveOrder = String.valueOf(input.getOrDefault("reserveOrderEnumId", ""));
            String orderBy;
            if ("INVRO_GUNIT_COST".equals(reserveOrder)) {
                orderBy = "i.unitCost desc";
            } else if ("INVRO_LUNIT_COST".equals(reserveOrder)) {
                orderBy = "i.unitCost asc";
            } else if ("INVRO_FIFO_EXP".equals(reserveOrder)) {
                orderBy = "i.expireDate asc";
            } else if ("INVRO_LIFO_EXP".equals(reserveOrder)) {
                orderBy = "i.expireDate desc";
            } else if ("INVRO_LIFO_REC".equals(reserveOrder)) {
                orderBy = "i.datetimeReceived desc";
            } else {
                orderBy = "i.datetimeReceived asc";
            }
            List<Object> locationTypeIds = new ArrayList<>();
            for (Object enumeration : find("Enumeration", Map.of("enumTypeId", "FACLOC_TYPE"))) {
                locationTypeIds.add(get(enumeration, "enumId"));
            }
            locationTypeIds.add(null);
            for (Object locationTypeId : locationTypeIds) {
                String query = "select i, fl from InventoryItem i left join FacilityLocation fl "
                        + "on fl.facilityId = i.facilityId and fl.locationSeqId = i.locationSeqId "
                        + "where i.productId = :product and i.facilityId = :facility "
                        + "and i.availableToPromiseTotal > 0";
                if (!empty(input.get("containerId"))) {
                    query += " and i.containerId = :container";
                }
                if (!empty(locationTypeId)) {
                    query += " and fl.locationTypeEnumId = :locationType";
                }
                query += " order by " + orderBy;
                var queryBuilder = entityManager.createQuery(query)
                        .setParameter("product", input.get("productId"))
                        .setParameter("facility", input.get("facilityId"));
                if (!empty(input.get("containerId"))) {
                    queryBuilder.setParameter("container", input.get("containerId"));
                }
                if (!empty(locationTypeId)) {
                    queryBuilder.setParameter("locationType", locationTypeId);
                }
                List<?> inventoryItemsAndLocations = queryBuilder.getResultList();
                for (Object row : inventoryItemsAndLocations) {
                    Object[] itemAndLocation = (Object[]) row;
                    Object item = itemAndLocation[0];
                    if (!"NON_SERIAL_INV_ITEM".equals(get(item, "inventoryItemTypeId"))) {
                        continue;
                    }
                    BigDecimal available = decimal(get(item, "availableToPromiseTotal"));
                    BigDecimal quantity = remaining.compareTo(available) > 0 ? available : remaining;
                    Map<String, Object> transfer = values(
                            "inventoryItemId", get(item, "inventoryItemId"),
                            "locationSeqId", get(item, "locationSeqId"),
                            "statusId", empty(input.get("statusId")) ? "IXF_REQUESTED" : input.get("statusId"),
                            "facilityId", input.get("facilityId"),
                            "facilityIdTo", input.get("facilityIdTo"),
                            "locationSeqIdTo", input.get("locationSeqIdTo"),
                            "sendDate", input.get("sendDate"), "xferQty", quantity);
                    call("createInventoryTransfer", transfer);
                    remaining = remaining.subtract(quantity);
                    if (remaining.compareTo(ZERO) == 0) {
                        break;
                    }
                }
                if (remaining.compareTo(ZERO) == 0) {
                    break;
                }
            }
        }
        if (remaining.signum() > 0) {
            throw new MigrationException(HttpStatus.BAD_REQUEST,
                    "Requested quantity [" + requested + "] could not be transferred because ["
                            + remaining + "] units of inventory is unavailable.");
        }
        return values("quantityNotTransferred", remaining);
    }

    /** applications/product/minilang/product/inventory/InventoryServices.xml:879-913 */
    private Map<String, Object> changeOwnerUponIssuance(Map<String, Object> input) {
        Object issuance = require(findByPk("ItemIssuance",
                Map.of("itemIssuanceId", input.get("itemIssuanceId"))), "ItemIssuance");
        Object item = findByPk("InventoryItem", Map.of("inventoryItemId", get(issuance, "inventoryItemId")));
        if (item == null || !"SERIALIZED_INV_ITEM".equals(get(item, "inventoryItemTypeId"))) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> updateContext = values("inventoryItemId", get(item, "inventoryItemId"));
        Object order = findByPk("OrderHeader", Map.of("orderId", get(issuance, "orderId")));
        if (order != null) {
            List<?> roles = find("OrderRole", values("orderId", get(order, "orderId"),
                    "roleTypeId", "END_USER_CUSTOMER"));
            Object store = findByPk("ProductStore", Map.of("productStoreId", get(order, "productStoreId")));
            if (!roles.isEmpty() && (empty(get(store, "setOwnerUponIssuance"))
                    || "Y".equals(get(store, "setOwnerUponIssuance")))) {
                updateContext.put("ownerPartyId", get(roles.get(0), "partyId"));
            }
        }
        call("updateInventoryItem", updateContext);
        return new LinkedHashMap<>();
    }

    /** applications/product/minilang/product/inventory/InventoryServices.xml:914-952 */
    private Map<String, Object> setOrderReservationPriority(Map<String, Object> input) {
        Object order = require(findByPk("OrderHeader",
                Map.of("orderId", input.get("orderId"))), "OrderHeader");
        Object priority = empty(input.get("priority")) ? "2" : input.get("priority");
        set(order, "priority", priority);
        for (Object reservation : find("OrderItemShipGrpInvRes",
                Map.of("orderId", input.get("orderId")))) {
            set(reservation, "priority", priority);
        }
        if (!empty(input.get("priority"))) {
            String query = "select o, r, i from OrderItemShipGrpInvRes r join OrderItem o "
                    + "on o.orderId = r.orderId and o.orderItemSeqId = r.orderItemSeqId "
                    + "join InventoryItem i "
                    + "on i.inventoryItemId = r.inventoryItemId where r.orderId = :orderId";
            List<?> rows = entityManager.createQuery(query)
                    .setParameter("orderId", input.get("orderId")).getResultList();
            for (Object row : rows) {
                Object[] triple = (Object[]) row;
                call("reassignInventoryReservations", values("productId", get(triple[0], "productId"),
                        "facilityId", get(triple[2], "facilityId")));
            }
        }
        return new LinkedHashMap<>();
    }

    /** applications/product/minilang/product/inventory/InventoryServices.xml:953-974; dispatches getInventoryAvailableByFacility and updateProductFacility. */
    private Map<String, Object> setLastInventoryCount(Map<String, Object> input) {
        Object item = require(findByPk("InventoryItem",
                Map.of("inventoryItemId", input.get("inventoryItemId"))), "InventoryItem");
        Map<String, Object> key = values("productId", get(item, "productId"),
                "facilityId", get(item, "facilityId"));
        Object productFacility = findByPk("ProductFacility", key);
        if (productFacility != null) {
            Map<String, Object> availability = call("getInventoryAvailableByFacility", key);
            call("updateProductFacility", values("productId", get(item, "productId"),
                    "facilityId", get(item, "facilityId"),
                    "lastInventoryCount", availability.get("availableToPromiseTotal")));
        }
        return new LinkedHashMap<>();
    }

    /** applications/product/minilang/product/inventory/InventoryServices.xml:976-983 */
    private Map<String, Object> createInventoryItemLabelAppl(Map<String, Object> input) {
        Object label = require(findByPk("InventoryItemLabel",
                Map.of("inventoryItemLabelId", input.get("inventoryItemLabelId"))), "InventoryItemLabel");
        Object application = newEntity("InventoryItemLabelAppl");
        setFields(application, "createInventoryItemLabelAppl", input, true);
        setFields(application, "createInventoryItemLabelAppl", input, false);
        set(application, "inventoryItemLabelTypeId", get(label, "inventoryItemLabelTypeId"));
        entityManager.persist(application);
        return new LinkedHashMap<>();
    }

    private void discontinueProductSales(String productId) {
        Timestamp current = now();
        Object product = findByPk("Product", Map.of("productId", productId));
        if (product != null) {
            set(product, "salesDiscontinuationDate", current);
        }
        List<?> activeCategoryMemberships = entityManager.createQuery(
                        "select m from ProductCategoryMember m where m.productId = :productId "
                                + "and m.thruDate is null", entities.entityClass("ProductCategoryMember"))
                .setParameter("productId", productId).getResultList();
        for (Object row : activeCategoryMemberships) {
            set(row, "thruDate", current);
        }
        List<?> activeAssociations = entityManager.createQuery(
                        "select a from ProductAssoc a where a.productIdTo = :productId "
                                + "and a.thruDate is null", entities.entityClass("ProductAssoc"))
                .setParameter("productId", productId).getResultList();
        for (Object row : activeAssociations) {
            set(row, "thruDate", current);
        }
    }

    private Object findByPk(String name, Map<String, ?> keys) {
        return entities.findByPrimaryKey(entities.entityClass(name), keys);
    }

    private List<?> find(String name, Map<String, ?> filters) {
        return entities.findByEquality(entities.entityClass(name), filters);
    }

    private Object newEntity(String name) {
        return entities.newEntity(entities.entityClass(name));
    }

    private Object require(Object entity, String name) {
        if (entity == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "Unknown " + name);
        }
        return entity;
    }

    private Object get(Object entity, String field) {
        if (entity instanceof Map<?, ?> map) {
            return map.get(field);
        }
        if (entity instanceof Object[] row) {
            for (Object value : row) {
                Object found = get(value, field);
                if (found != null) {
                    return found;
                }
            }
            return null;
        }
        return entity == null ? null : entities.get(entity, field);
    }

    private void set(Object entity, String field, Object value) {
        entities.set(entity, field, value);
    }

    private Map<String, Object> call(String serviceName, Map<String, ?> input) {
        Map<String, Object> result = new LinkedHashMap<>(
                dispatcher.execute("product", serviceName, serviceInput(serviceName, input)));
        result.remove("responseMessage");
        return result;
    }

    private Map<String, Object> serviceInput(String name, Map<String, ?> input) {
        Map<String, Object> result = new LinkedHashMap<>();
        ProductServiceMetadata.SimpleServiceMetadata simple =
                ProductServiceMetadata.simpleServices().get(name);
        if (simple != null) {
            simple.params().stream().filter(p -> input.containsKey(p.name()))
                    .filter(p -> inputMode(p.mode())).forEach(p -> result.put(p.name(), input.get(p.name())));
            return result;
        }
        ServiceCatalog.ServiceDefinition definition = ServiceCatalog.all().get(name);
        if (definition != null) {
            definition.params().stream().filter(p -> input.containsKey(p.name()))
                    .filter(p -> inputMode(p.mode())).forEach(p -> result.put(p.name(), input.get(p.name())));
            return result;
        }
        input.forEach(result::put);
        return result;
    }

    private void setFields(Object entity, String service, Map<String, ?> input, boolean primary) {
        Set<String> keys = Set.copyOf(entities.primaryKeys(entity.getClass()));
        serviceInput(service, input).forEach((name, value) -> {
            if (keys.contains(name) == primary) {
                set(entity, name, value);
            }
        });
    }

    private static boolean inputMode(String mode) {
        return "IN".equalsIgnoreCase(mode) || "INOUT".equalsIgnoreCase(mode);
    }

    private static boolean empty(Object value) {
        return value == null || value instanceof String text && text.isEmpty();
    }

    private static BigDecimal decimal(Object value) {
        return value == null || value.toString().isBlank() ? ZERO : new BigDecimal(value.toString());
    }

    private static Timestamp timestamp(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Timestamp ts) {
            return ts;
        }
        if (value instanceof Date date) {
            return new Timestamp(date.getTime());
        }
        return Timestamp.valueOf(value.toString());
    }

    private static Timestamp now() {
        return Timestamp.from(Instant.now());
    }

    private static Map<String, Object> values(Object... pairs) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            if (pairs[i + 1] != null) {
                result.put(pairs[i].toString(), pairs[i + 1]);
            }
        }
        return result;
    }

    private static boolean same(Object left, Object right) {
        if (left instanceof BigDecimal leftNumber && right != null) {
            return leftNumber.compareTo(decimal(right)) == 0;
        }
        return left == null ? right == null : left.equals(right);
    }

    private boolean active(Object row) {
        Timestamp current = now();
        Timestamp from = timestamp(get(row, "fromDate"));
        Timestamp thru = timestamp(get(row, "thruDate"));
        return (from == null || !from.after(current)) && (thru == null || thru.after(current));
    }

    private boolean hasProductType(Object child, String target) {
        Set<Object> seen = new HashSet<>();
        Object current = child;
        while (current != null && seen.add(current)) {
            if (target.equals(current)) {
                return true;
            }
            Object type = findByPk("ProductType", Map.of("productTypeId", current));
            current = type == null ? null : get(type, "parentTypeId");
        }
        return false;
    }
}
