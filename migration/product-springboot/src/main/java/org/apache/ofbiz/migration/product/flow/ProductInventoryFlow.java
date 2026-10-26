/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ofbiz.migration.product.flow;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.SequenceService;
import org.apache.ofbiz.migration.product.ProductPermissions;
import org.apache.ofbiz.migration.product.adapter.AccountingPort;
import org.apache.ofbiz.migration.product.simple.InventoryServices;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProductInventoryFlow {
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private final EntityRegistry entities;
    private final EntityManager entityManager;
    private final SequenceService sequences;
    private final AccountingPort accounting;
    private final ProductPermissions permissions;
    private final InventoryServices inventoryServices;

    public ProductInventoryFlow(
            EntityRegistry entities,
            EntityManager entityManager,
            SequenceService sequences,
            AccountingPort accounting,
            ProductPermissions permissions,
            InventoryServices inventoryServices) {
        this.entities = entities;
        this.entityManager = entityManager;
        this.sequences = sequences;
        this.accounting = accounting;
        this.permissions = permissions;
        this.inventoryServices = inventoryServices;
    }

    /**
     * Port of ProductServicesScript.groovy:36-95. Product keyword indexing,
     * category side effects from ECAs, and external search indexing are no-ops.
     */
    @Transactional
    public Map<String, Object> createProduct(Map<String, Object> input) {
        if (!permissions.hasPermission("CATALOG", "CREATE")
                && !permissions.hasPermission("CATALOG_ROLE", "CREATE")) {
            throw new MigrationException(HttpStatus.FORBIDDEN, "ProductCatalogCreatePermissionError");
        }
        String productTypeId = required(input, "productTypeId");
        String internalName = required(input, "internalName");
        String productId = text(input.get("productId"));
        if (productId == null) {
            productId = sequences.nextId("Product");
        } else if (invalidDatabaseId(productId)) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Invalid productId");
        } else if (find("Product", Map.of("productId", productId)) != null) {
            throw new MigrationException(HttpStatus.CONFLICT, "Duplicate productId");
        }
        if (find("ProductType", Map.of("productTypeId", productTypeId)) == null) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Unknown productTypeId: " + productTypeId);
        }
        Object product = entities.newEntity(entities.entityClass("Product"));
        copyKnownFields(product, input, Set.of("createdDate", "createdByUserLogin",
                "lastModifiedDate", "lastModifiedByUserLogin", "isVirtual", "isVariant", "billOfMaterialLevel"));
        entities.set(product, "productId", productId);
        entities.set(product, "productTypeId", productTypeId);
        entities.set(product, "internalName", internalName);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        String userLoginId = currentUserLoginId();
        entities.set(product, "createdDate", now);
        entities.set(product, "lastModifiedDate", now);
        entities.set(product, "createdByUserLogin", userLoginId);
        entities.set(product, "lastModifiedByUserLogin", userLoginId);
        entities.set(product, "isVirtual", defaultText(input.get("isVirtual"), "N"));
        entities.set(product, "isVariant", defaultText(input.get("isVariant"), "N"));
        entities.set(product, "billOfMaterialLevel",
                input.get("billOfMaterialLevel") == null ? 0L : input.get("billOfMaterialLevel"));
        entityManager.persist(product);
        return Map.of("productId", productId, "sideEffectNotes",
                "ProductKeyword indexing and Product SECA/ECA side effects are no-ops.");
    }

    /**
     * Port of InventoryServicesScript.groovy:92-167. Party accounting
     * preferences are read from their entity; product-cost calculation remains
     * behind AccountingPort when unitCost is omitted.
     */
    @Transactional
    public Map<String, Object> createInventoryItem(Map<String, Object> input) {
        String productId = required(input, "productId");
        String facilityId = required(input, "facilityId");
        String inventoryItemTypeId = required(input, "inventoryItemTypeId");
        Object product = find("Product", Map.of("productId", productId));
        if (product == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "Product not found: " + productId);
        }
        if (find("InventoryItemType", Map.of("inventoryItemTypeId", inventoryItemTypeId)) == null) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Unknown inventoryItemTypeId: " + inventoryItemTypeId);
        }
        String lotId = text(input.get("lotId"));
        String lotRule = text(entities.get(product, "lotIdFilledIn"));
        if ("Mandatory".equals(lotRule) && lotId == null) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "ProductLotIdMandatory");
        }
        if ("Forbidden".equals(lotRule) && lotId != null) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "ProductLotIdForbidden");
        }
        Object facility = find("Facility", Map.of("facilityId", facilityId));
        if (facility == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "Facility not found: " + facilityId);
        }
        String ownerPartyId = defaultText(input.get("ownerPartyId"), text(entities.get(facility, "ownerPartyId")));
        if (ownerPartyId == null) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "FacilityInventoryItemsMissingOwnerPartyId");
        }
        String currencyUomId = text(input.get("currencyUomId"));
        if (currencyUomId == null) {
            Object preferences = find("PartyAcctgPreference", Map.of("partyId", ownerPartyId));
            currencyUomId = preferences == null ? "USD" : text(entities.get(preferences, "baseCurrencyUomId"));
            if (currencyUomId == null) {
                currencyUomId = "USD";
            }
        }
        BigDecimal unitCost = decimal(input.get("unitCost"));
        if (unitCost == null) {
            Map<String, Object> cost = accounting.invoke("getProductCost", Map.of(
                    "productId", productId, "currencyUomId", currencyUomId, "costComponentTypePrefix", "EST_STD"));
            unitCost = decimal(cost.get("productCost"));
        }
        if (unitCost == null) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "FacilityInventoryItemsMissingUnitCost");
        }
        if (unitCost.compareTo(ZERO) < 0) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "FacilityInventoryItemsNegativeUnitCost");
        }
        if (lotId != null && !"Y".equals(input.get("isReturned"))) {
            if (find("Lot", Map.of("lotId", lotId)) == null) {
                Object lot = entities.newEntity(entities.entityClass("Lot"));
                entities.set(lot, "lotId", lotId);
                entityManager.persist(lot);
            }
        }
        Object inventoryItem = entities.newEntity(entities.entityClass("InventoryItem"));
        copyKnownFields(inventoryItem, input,
                Set.of("inventoryItemId", "quantityOnHandTotal", "availableToPromiseTotal", "datetimeReceived"));
        String inventoryItemId = sequences.nextId("InventoryItem");
        entities.set(inventoryItem, "inventoryItemId", inventoryItemId);
        entities.set(inventoryItem, "productId", productId);
        entities.set(inventoryItem, "facilityId", facilityId);
        entities.set(inventoryItem, "inventoryItemTypeId", inventoryItemTypeId);
        entities.set(inventoryItem, "ownerPartyId", ownerPartyId);
        entities.set(inventoryItem, "currencyUomId", currencyUomId);
        entities.set(inventoryItem, "unitCost", unitCost);
        entities.set(inventoryItem, "datetimeReceived", timestamp(input.get("datetimeReceived")));
        entityManager.persist(inventoryItem);
        return Map.of("inventoryItemId", inventoryItemId);
    }

    /**
     * Port of ShipmentReceiptServices.groovy:65-193. Inventory balances are
     * maintained from InventoryItemDetail; accounting and receipt ECAs are
     * documented no-ops in this migration slice.
     */
    @Transactional
    public Map<String, Object> receiveInventoryProduct(Map<String, Object> input) {
        BigDecimal accepted = requiredDecimal(input, "quantityAccepted");
        BigDecimal rejected = decimal(input.get("quantityRejected"));
        if (rejected == null) {
            rejected = ZERO;
        }
        if (accepted.signum() < 0 || rejected.signum() < 0
                || (accepted.signum() == 0 && rejected.signum() == 0)) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "ProductNoItemsToAcceptOrReject");
        }
        String productId = required(input, "productId");
        String inventoryType = required(input, "inventoryItemTypeId");
        required(input, "facilityId");
        if (find("Product", Map.of("productId", productId)) == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "Product not found: " + productId);
        }
        boolean serialized = "SERIALIZED_INV_ITEM".equals(inventoryType);
        BigDecimal loopCount = serialized && accepted.signum() > 0
                ? accepted : BigDecimal.ONE;
        if (serialized
                && (text(input.get("serialNumber")) != null || text(input.get("currentInventoryItemId")) != null)
                && accepted.compareTo(BigDecimal.ONE) > 0) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "FacilityReceiveInventoryProduct");
        }
        String statusId = text(input.get("statusId"));
        if ("NON_SERIAL_INV_ITEM".equals(inventoryType)) {
            statusId = switch (statusId == null ? "" : statusId) {
                case "INV_DEFECTIVE" -> "INV_NS_DEFECTIVE";
                case "INV_ON_HOLD" -> "INV_NS_ON_HOLD";
                case "INV_RETURNED" -> "INV_NS_RETURNED";
                default -> null;
            };
        }
        String inventoryItemId = null;
        String lastReceiptId = null;
        int count = loopCount.intValueExact();
        BigDecimal receivedEach = serialized && accepted.signum() > 0 ? BigDecimal.ONE : accepted;
        Object oldOwnerPartyId = null;
        for (int i = 0; i < count; i++) {
            Map<String, Object> itemInput = new LinkedHashMap<>(input);
            itemInput.put("statusId", statusId);
            String currentInventoryItemId = text(input.get("currentInventoryItemId"));
            if (receivedEach.signum() > 0) {
                if (currentInventoryItemId == null) {
                    Map<String, Object> created = createInventoryItem(itemInput);
                    inventoryItemId = text(created.get("inventoryItemId"));
                } else {
                    requireInventoryItemUpdatePermission();
                    itemInput.put("inventoryItemId", currentInventoryItemId);
                    oldOwnerPartyId = callInventoryService("updateInventoryItem", itemInput).get("oldOwnerPartyId");
                    inventoryItemId = currentInventoryItemId;
                }
                if (serialized
                        && text(input.get("returnId")) == null) {
                    Object inventoryItem = find("InventoryItem", Map.of("inventoryItemId", inventoryItemId));
                    String currentStatus = text(entities.get(inventoryItem, "statusId"));
                    if (!"INV_PROMISED".equals(currentStatus) && !"INV_ON_HOLD".equals(currentStatus)) {
                        requireInventoryItemUpdatePermission();
                        callInventoryService("updateInventoryItem", Map.of(
                                "inventoryItemId", inventoryItemId, "statusId", "INV_AVAILABLE"));
                    }
                }
            }
            String receiptId = sequences.nextId("ShipmentReceipt");
            if (receivedEach.signum() > 0 && !serialized) {
                createReceiptDetail(itemInput, inventoryItemId, receivedEach, rejected, receiptId);
            }
            createReceipt(itemInput, inventoryItemId, receivedEach, rejected, receiptId);
            if (serialized && inventoryItemId != null) {
                // Mirrors applications/product/entitydef/eecas.xml:52-55.
                callInventoryService("updateSerializedInventoryTotals",
                        Map.of("inventoryItemId", inventoryItemId));
            }
            if (accepted.signum() > 0) {
                Map<String, Object> balanceInput = new LinkedHashMap<>(input);
                balanceInput.put("inventoryItemId", inventoryItemId);
                callInventoryService("balanceInventoryItems", balanceInput);
            }
            lastReceiptId = receiptId;
            rejected = ZERO;
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("inventoryItemId", inventoryItemId == null ? "" : inventoryItemId);
        result.put("receiptId", lastReceiptId == null ? "" : lastReceiptId);
        result.put("successMessageList", inventoryItemId == null ? java.util.List.of()
                : java.util.List.of("Received " + receivedEach + " of " + productId
                        + " in inventory item " + inventoryItemId + "."));
        result.put("sideEffectNotes", "Receipt accounting and receipt SECA/ECA callbacks are no-ops.");
        if (oldOwnerPartyId != null) {
            result.put("oldOwnerPartyId", oldOwnerPartyId);
        }
        return result;
    }

    private void createReceiptDetail(
            Map<String, Object> input, String inventoryItemId, BigDecimal accepted, BigDecimal rejected, String receiptId) {
        Object inventoryItem = find("InventoryItem", Map.of("inventoryItemId", inventoryItemId));
        Object detail = entities.newEntity(entities.entityClass("InventoryItemDetail"));
        Map<String, Object> parents = Map.of("inventoryItemId", inventoryItemId);
        entities.set(detail, "inventoryItemId", inventoryItemId);
        entities.set(detail, "inventoryItemDetailSeqId",
                sequences.nextSubSequence("InventoryItemDetail", parents, "inventoryItemDetailSeqId"));
        entities.set(detail, "effectiveDate", new Timestamp(System.currentTimeMillis()));
        entities.set(detail, "quantityOnHandDiff", accepted);
        entities.set(detail, "availableToPromiseDiff", accepted);
        entities.set(detail, "unitCost", entities.get(inventoryItem, "unitCost"));
        for (String field : Set.of("orderId", "orderItemSeqId", "shipGroupSeqId", "shipmentId",
                "returnId", "returnItemSeqId", "workEffortId", "reasonEnumId", "description")) {
            if (input.get(field) != null) {
                entities.set(detail, field, input.get(field));
            }
        }
        entityManager.persist(detail);
        BigDecimal oldQoh = decimal(entities.get(inventoryItem, "quantityOnHandTotal"));
        BigDecimal oldAtp = decimal(entities.get(inventoryItem, "availableToPromiseTotal"));
        entities.set(inventoryItem, "quantityOnHandTotal", (oldQoh == null ? ZERO : oldQoh).add(accepted));
        entities.set(inventoryItem, "availableToPromiseTotal", (oldAtp == null ? ZERO : oldAtp).add(accepted));
        entityManager.flush();
        entities.set(detail, "receiptId", receiptId);
    }

    private void createReceipt(
            Map<String, Object> input, String inventoryItemId, BigDecimal accepted, BigDecimal rejected, String receiptId) {
        Object receipt = entities.newEntity(entities.entityClass("ShipmentReceipt"));
        copyKnownFields(receipt, input,
                Set.of("receiptId", "inventoryItemId", "quantityAccepted", "quantityRejected", "datetimeReceived"));
        entities.set(receipt, "receiptId", receiptId);
        if (inventoryItemId != null) {
            entities.set(receipt, "inventoryItemId", inventoryItemId);
        }
        entities.set(receipt, "productId", input.get("productId"));
        entities.set(receipt, "quantityAccepted", accepted);
        entities.set(receipt, "quantityRejected", rejected);
        entities.set(receipt, "datetimeReceived", timestamp(input.get("datetimeReceived")));
        entities.set(receipt, "receivedByUserLoginId", currentUserLoginId());
        entityManager.persist(receipt);
    }

    private void requireInventoryItemUpdatePermission() {
        if (!permissions.hasPermission("CATALOG", "UPDATE")
                && !permissions.hasPermission("FACILITY", "UPDATE")) {
            throw new MigrationException(HttpStatus.FORBIDDEN, "ProductCatalogCreatePermissionError");
        }
    }

    private Map<String, Object> callInventoryService(String serviceName, Map<String, Object> input) {
        return inventoryServices.execute(serviceName, serviceName, input);
    }

    private static Timestamp timestamp(Object value) {
        if (value == null) {
            return new Timestamp(System.currentTimeMillis());
        }
        if (value instanceof Timestamp timestamp) {
            return timestamp;
        }
        if (value instanceof Date date) {
            return new Timestamp(date.getTime());
        }
        String text = value.toString();
        try {
            return Timestamp.from(Instant.parse(text));
        } catch (DateTimeParseException exception) {
            try {
                return Timestamp.valueOf(text);
            } catch (IllegalArgumentException invalidTimestamp) {
                throw new MigrationException(HttpStatus.BAD_REQUEST, "Invalid datetimeReceived");
            }
        }
    }

    private Object find(String entityName, Map<String, ?> keys) {
        return entities.findByPrimaryKey(entities.entityClass(entityName), keys);
    }

    private void copyKnownFields(Object entity, Map<String, Object> input, Set<String> excluded) {
        Set<String> fields = entities.toMap(entity).keySet();
        input.forEach((name, value) -> {
            if (value != null && fields.contains(name) && !excluded.contains(name)) {
                entities.set(entity, name, value);
            }
        });
    }

    private String currentUserLoginId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? "admin" : authentication.getName();
    }

    private static String required(Map<String, Object> input, String name) {
        String value = text(input.get(name));
        if (value == null) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Missing required parameter: " + name);
        }
        return value;
    }

    private static BigDecimal requiredDecimal(Map<String, Object> input, String name) {
        BigDecimal value = decimal(input.get(name));
        if (value == null) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Missing required parameter: " + name);
        }
        return value;
    }

    private static BigDecimal decimal(Object value) {
        if (value == null) {
            return null;
        }
        return value instanceof BigDecimal decimal ? decimal : new BigDecimal(value.toString());
    }

    private static String defaultText(Object value, String fallback) {
        String text = text(value);
        return text == null ? fallback : text;
    }

    private static String text(Object value) {
        if (value == null || value.toString().isBlank()) {
            return null;
        }
        return value.toString();
    }

    private static boolean invalidDatabaseId(String value) {
        return value.contains(" ") || value.contains("\"") || value.contains("'")
                || value.contains("&") || value.contains("?") || value.contains("<")
                || value.contains(">") || value.contains("\\") || value.contains("/");
    }
}
