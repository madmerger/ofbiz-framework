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
package org.apache.ofbiz.migration.product;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("local & !postgres & !prod & !production")
public class ProductLocalSeed implements CommandLineRunner {
    private final EntityRegistry entities;
    private final EntityManager entityManager;

    public ProductLocalSeed(EntityRegistry entities, EntityManager entityManager) {
        this.entities = entities;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedTypesAndStatuses();
        seedDemoOrder();
    }

    private void seedTypesAndStatuses() {
        seed("StatusType", Map.of("statusTypeId", "SHIPMENT_STATUS"),
                "description", "Demo shipment status", "hasTable", "N");
        seed("StatusType", Map.of("statusTypeId", "ORDER_STATUS"),
                "description", "Demo order status", "hasTable", "N");
        seed("StatusType", Map.of("statusTypeId", "ORDER_ITEM_STATUS"),
                "description", "Demo order item status", "hasTable", "N");
        seed("StatusType", Map.of("statusTypeId", "INVENTORY_ITEM_STTS"),
                "description", "Demo inventory status", "hasTable", "N");
        seed("StatusType", Map.of("statusTypeId", "INV_SERIALIZED_STTS"),
                "description", "Demo serialized inventory status", "hasTable", "N",
                "parentTypeId", "INVENTORY_ITEM_STTS");
        seed("StatusType", Map.of("statusTypeId", "INV_NON_SER_STTS"),
                "description", "Demo non-serialized inventory status", "hasTable", "N",
                "parentTypeId", "INVENTORY_ITEM_STTS");

        status("SHIPMENT_INPUT", "SHIPMENT_STATUS", "Input", "01");
        status("SHIPMENT_SCHEDULED", "SHIPMENT_STATUS", "Scheduled", "02");
        status("SHIPMENT_PICKED", "SHIPMENT_STATUS", "Picked", "03");
        status("SHIPMENT_PACKED", "SHIPMENT_STATUS", "Packed", "04");
        status("SHIPMENT_SHIPPED", "SHIPMENT_STATUS", "Shipped", "05");
        status("SHIPMENT_DELIVERED", "SHIPMENT_STATUS", "Delivered", "06");
        status("SHIPMENT_CANCELLED", "SHIPMENT_STATUS", "Cancelled", "99");
        transition("SHIPMENT_INPUT", "SHIPMENT_SCHEDULED", "Schedule");
        transition("SHIPMENT_INPUT", "SHIPMENT_PICKED", "Pick");
        transition("SHIPMENT_INPUT", "SHIPMENT_PACKED", "Pack");
        transition("SHIPMENT_SCHEDULED", "SHIPMENT_PICKED", "Pick");
        transition("SHIPMENT_SCHEDULED", "SHIPMENT_PACKED", "Pack");
        transition("SHIPMENT_PICKED", "SHIPMENT_PACKED", "Pack");
        transition("SHIPMENT_PACKED", "SHIPMENT_SHIPPED", "Ship");
        transition("SHIPMENT_SHIPPED", "SHIPMENT_DELIVERED", "Deliver");
        transition("SHIPMENT_INPUT", "SHIPMENT_CANCELLED", "Cancel");
        transition("SHIPMENT_SCHEDULED", "SHIPMENT_CANCELLED", "Cancel");
        transition("SHIPMENT_PICKED", "SHIPMENT_CANCELLED", "Cancel");

        status("ORDER_CREATED", "ORDER_STATUS", "Created", "01");
        status("ORDER_PROCESSING", "ORDER_STATUS", "Processing", "04");
        status("ORDER_APPROVED", "ORDER_STATUS", "Approved", "05");
        status("ORDER_SENT", "ORDER_STATUS", "Sent", "03");
        status("ORDER_HOLD", "ORDER_STATUS", "Held", "06");
        status("ORDER_COMPLETED", "ORDER_STATUS", "Completed", "10");
        status("ORDER_CANCELLED", "ORDER_STATUS", "Cancelled", "99");
        status("ITEM_CREATED", "ORDER_ITEM_STATUS", "Created", "01");
        status("ITEM_APPROVED", "ORDER_ITEM_STATUS", "Approved", "05");
        status("ITEM_COMPLETED", "ORDER_ITEM_STATUS", "Completed", "10");
        status("ITEM_REJECTED", "ORDER_ITEM_STATUS", "Rejected", "98");
        status("ITEM_CANCELLED", "ORDER_ITEM_STATUS", "Cancelled", "99");
        transition("ITEM_CREATED", "ITEM_APPROVED", "Approve Item");
        transition("ITEM_CREATED", "ITEM_REJECTED", "Reject Item");
        transition("ITEM_CREATED", "ITEM_CANCELLED", "Cancel Item");
        transition("ITEM_APPROVED", "ITEM_COMPLETED", "Complete Item");
        transition("ITEM_APPROVED", "ITEM_CANCELLED", "Cancel Item");
        transition("ITEM_COMPLETED", "ITEM_APPROVED", "Approve Item");
        status("INV_ON_ORDER", "INV_SERIALIZED_STTS", "On Order", "01");
        status("INV_AVAILABLE", "INV_SERIALIZED_STTS", "Available", "02");
        status("INV_PROMISED", "INV_SERIALIZED_STTS", "Promised", "03");
        status("INV_DELIVERED", "INV_SERIALIZED_STTS", "Delivered", "04");
        status("INV_ACTIVATED", "INV_SERIALIZED_STTS", "Activated", "05");
        status("INV_DEACTIVATED", "INV_SERIALIZED_STTS", "Deactivated", "06");
        status("INV_ON_HOLD", "INV_SERIALIZED_STTS", "On Hold", "07");
        status("INV_BEING_TRANSFERED", "INV_SERIALIZED_STTS", "Being Transferred", "08");
        status("INV_BEING_TRANS_PRM", "INV_SERIALIZED_STTS", "Being Transferred Promised", "09");
        status("INV_RETURNED", "INV_SERIALIZED_STTS", "Returned", "20");
        status("INV_DEFECTIVE", "INV_SERIALIZED_STTS", "Defective", "21");
        status("INV_NS_ON_HOLD", "INV_NON_SER_STTS", "On Hold Non-Serialized", "01");
        status("INV_NS_DEFECTIVE", "INV_NON_SER_STTS", "Defective Non-Serialized", "02");
        status("INV_NS_RETURNED", "INV_NON_SER_STTS", "Returned Non-Serialized", "03");

        seed("ProductType", Map.of("productTypeId", "GOOD"),
                "description", "Demo good", "hasTable", "N", "isPhysical", "Y", "isDigital", "N");
        seed("ProductType", Map.of("productTypeId", "FINISHED_GOOD"),
                "description", "Demo finished good", "hasTable", "N", "isPhysical", "Y", "isDigital", "N",
                "parentTypeId", "GOOD");
        seed("InventoryItemType", Map.of("inventoryItemTypeId", "NON_SERIAL_INV_ITEM"),
                "description", "Demo non-serialized inventory", "hasTable", "N");
        seed("InventoryItemType", Map.of("inventoryItemTypeId", "SERIALIZED_INV_ITEM"),
                "description", "Demo serialized inventory", "hasTable", "N");
        seed("ShipmentType", Map.of("shipmentTypeId", "OUTGOING_SHIPMENT"),
                "description", "Demo outgoing shipment", "hasTable", "N");
        seed("ShipmentType", Map.of("shipmentTypeId", "SALES_SHIPMENT"),
                "description", "Demo sales shipment", "hasTable", "N", "parentTypeId", "OUTGOING_SHIPMENT");
        seed("FacilityType", Map.of("facilityTypeId", "WAREHOUSE"),
                "description", "Demo warehouse");
        seed("PartyType", Map.of("partyTypeId", "PARTY_GROUP"),
                "description", "Demo organization");
        seed("Party", Map.of("partyId", "E2E_COMPANY"),
                "partyTypeId", "PARTY_GROUP");
        seed("PartyGroup", Map.of("partyId", "E2E_COMPANY"),
                "groupName", "E2E Demo Company");
        seed("UserLogin", Map.of("userLoginId", "admin"),
                "partyId", "E2E_COMPANY", "enabled", "Y");
        seed("PartyAcctgPreference", Map.of("partyId", "E2E_COMPANY"),
                "baseCurrencyUomId", "USD");
    }

    private void seedDemoOrder() {
        seed("Facility", Map.of("facilityId", "E2E_WH"),
                "facilityTypeId", "WAREHOUSE", "facilityName", "E2E Demo Warehouse", "ownerPartyId", "E2E_COMPANY",
                "defaultInventoryItemTypeId", "NON_SERIAL_INV_ITEM");
        seed("ProductStore", Map.of("productStoreId", "E2E_STORE"),
                "storeName", "E2E Demo Store", "reserveInventory", "Y",
                "reserveOrderEnumId", "INVRO_FIFO_REC", "inventoryFacilityId", "E2E_WH", "requireInventory", "N");
        Timestamp now = new Timestamp(System.currentTimeMillis());
        seed("OrderHeader", Map.of("orderId", "E2E_ORDER"),
                "orderTypeId", "SALES_ORDER", "statusId", "ORDER_APPROVED", "productStoreId", "E2E_STORE",
                "orderDate", now, "entryDate", now, "currencyUom", "USD");
        seed("OrderItem", Map.of("orderId", "E2E_ORDER", "orderItemSeqId", "00001"),
                "statusId", "ITEM_APPROVED", "productId", "E2E_PROD", "quantity", BigDecimal.valueOf(2));
        seed("OrderItemShipGroup", Map.of("orderId", "E2E_ORDER", "shipGroupSeqId", "00001"),
                "facilityId", "E2E_WH", "maySplit", "N");
        seed("OrderItemShipGroupAssoc",
                Map.of("orderId", "E2E_ORDER", "orderItemSeqId", "00001", "shipGroupSeqId", "00001"),
                "quantity", BigDecimal.valueOf(2));
    }

    private void status(String id, String type, String description, String sequenceId) {
        seed("StatusItem", Map.of("statusId", id),
                "statusTypeId", type, "description", description, "statusCode", id.substring(id.lastIndexOf('_') + 1),
                "sequenceId", sequenceId);
    }

    private void transition(String from, String to, String name) {
        seed("StatusValidChange", Map.of("statusId", from, "statusIdTo", to), "transitionName", name);
    }

    private void seed(String entityName, Map<String, Object> keys, Object... fields) {
        Class<?> type = entities.entityClass(entityName);
        if (entities.findByPrimaryKey(type, keys) != null) {
            return;
        }
        Object entity = entities.newEntity(type);
        keys.forEach((name, value) -> entities.set(entity, name, value));
        for (int index = 0; index < fields.length; index += 2) {
            entities.set(entity, fields[index].toString(), fields[index + 1]);
        }
        entityManager.persist(entity);
    }
}
