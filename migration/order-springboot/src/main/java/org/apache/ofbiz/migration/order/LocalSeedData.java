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
package org.apache.ofbiz.migration.order;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.persistence.EntityManager;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Component
@Profile({"local", "test"})
public class LocalSeedData implements CommandLineRunner {
    private static final Set<String> STATUS_TYPES =
            Set.of("ORDER_STATUS", "ORDER_ITEM_STATUS", "CUSTREQ_STTS", "REQUIREMENT_STATUS");

    private final EntityRegistry entities;
    private final EntityManager entityManager;

    public LocalSeedData(EntityRegistry entities, EntityManager entityManager) {
        this.entities = entities;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        seedStatusData();
        seedParties();
        seedProducts();
        seedOrderTypes();
        seedProductStore();
        seedSensitivePaymentPreference();
    }

    private void seedStatusData() throws Exception {
        Path root = Path.of(System.getProperty("ofbiz.root", "."));
        Path orderSeed = root.resolve("applications/datamodel/data/seed/OrderSeedData.xml");
        // applications/datamodel/data/seed/OrderSeedData.xml:111-153, 217-260 を原値のまま取り込む。
        List<Map<String, String>> statusItems = seedRows(orderSeed, "StatusItem");
        Set<String> statusIds = statusItems.stream()
                .filter(row -> STATUS_TYPES.contains(row.get("statusTypeId")))
                .map(row -> row.get("statusId"))
                .collect(Collectors.toSet());
        for (Map<String, String> status : statusItems) {
            if (STATUS_TYPES.contains(status.get("statusTypeId"))) {
                persistIfMissing("StatusItem", status);
            }
        }
        for (Map<String, String> transition : seedRows(orderSeed, "StatusValidChange")) {
            if (statusIds.contains(transition.get("statusId"))
                    && statusIds.contains(transition.get("statusIdTo"))) {
                persistIfMissing("StatusValidChange", transition);
            }
        }
    }

    private void seedParties() {
        for (String partyId : List.of("DemoCustomer", "Company")) {
            persistIfMissing("Party", Map.of(
                    "partyId", partyId,
                    "partyTypeId", "PARTY_GROUP",
                    "preferredCurrencyUomId", "USD",
                    "createdDate", Timestamp.from(java.time.Instant.now())));
            persistIfMissing("PartyGroup", Map.of(
                    "partyId", partyId,
                    "groupName", partyId.equals("Company") ? "デモ会社" : "デモ顧客"));
        }
    }

    private void seedProducts() {
        Timestamp fromDate = Timestamp.valueOf("2020-01-01 00:00:00");
        List<ProductFixture> products = List.of(
                new ProductFixture("DEMO_ORDER_PRODUCT_1", "デモ商品 1", new BigDecimal("25.00")),
                new ProductFixture("DEMO_ORDER_PRODUCT_2", "デモ商品 2", new BigDecimal("40.00")));
        for (ProductFixture product : products) {
            persistIfMissing("Product", Map.of(
                    "productId", product.id(),
                    "productTypeId", "FINISHED_GOOD",
                    "productName", product.name(),
                    "internalName", product.name(),
                    "isVirtual", "N",
                    "isVariant", "N"));
            persistIfMissing("ProductPrice", Map.of(
                    "productId", product.id(),
                    "productPriceTypeId", "DEFAULT_PRICE",
                    "productPricePurposeId", "PURCHASE",
                    "currencyUomId", "USD",
                    "productStoreGroupId", "_NA_",
                    "fromDate", fromDate,
                    "price", product.price()));
        }
    }

    private void seedOrderTypes() {
        persistIfMissing("OrderType", Map.of("orderTypeId", "SALES_ORDER", "description", "販売注文"));
        persistIfMissing("OrderType", Map.of("orderTypeId", "PURCHASE_ORDER", "description", "購買注文"));
    }

    private void seedProductStore() {
        persistIfMissing("ProductStore", Map.of(
                "productStoreId", "DEMO_ORDER_STORE",
                "storeName", "注文デモストア",
                "companyName", "注文デモストア",
                "payToPartyId", "Company",
                "defaultCurrencyUomId", "USD",
                "reserveInventory", "Y",
                "explodeOrderItems", "N"));
    }

    private void seedSensitivePaymentPreference() {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("orderPaymentPreferenceId", "ORDER-DEMO-PAYMENT");
        values.put("orderId", "ORDER-DEMO-PAYMENT");
        values.put("statusId", "PAYMENT_NOT_RECEIVED");
        values.put("paymentMethodTypeId", "CREDIT_CARD");
        values.put("maxAmount", BigDecimal.ZERO);
        values.put("createdDate", Timestamp.from(java.time.Instant.now()));
        persistIfMissing("OrderPaymentPreference", values);
    }

    private void persistIfMissing(String entityName, Map<String, ?> values) {
        Class<?> type = entities.entityClass(entityName);
        Map<String, Object> keys = new LinkedHashMap<>();
        for (String key : entities.primaryKeys(type)) {
            if (!values.containsKey(key)) {
                throw new IllegalArgumentException("シードの主キーがありません: " + entityName + "." + key);
            }
            keys.put(key, values.get(key));
        }
        if (entities.findByPrimaryKey(type, keys) != null) {
            return;
        }
        Object entity = entities.newEntity(type);
        for (Map.Entry<String, ?> value : values.entrySet()) {
            if (hasField(type, value.getKey())) {
                entities.set(entity, value.getKey(), value.getValue());
            }
        }
        entityManager.persist(entity);
    }

    private static boolean hasField(Class<?> type, String field) {
        return java.util.Arrays.stream(type.getMethods())
                .anyMatch(method -> method.getName().equals("set"
                        + Character.toUpperCase(field.charAt(0)) + field.substring(1)));
    }

    private static List<Map<String, String>> seedRows(Path file, String elementName) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        NodeList nodes = factory.newDocumentBuilder().parse(file.toFile()).getElementsByTagName(elementName);
        List<Map<String, String>> rows = new ArrayList<>();
        for (int index = 0; index < nodes.getLength(); index++) {
            Node node = nodes.item(index);
            if (!(node instanceof Element element)) {
                continue;
            }
            NamedNodeMap attributes = element.getAttributes();
            Map<String, String> row = new LinkedHashMap<>();
            for (int attributeIndex = 0; attributeIndex < attributes.getLength(); attributeIndex++) {
                Node attribute = attributes.item(attributeIndex);
                row.put(attribute.getNodeName(), attribute.getNodeValue());
            }
            rows.add(row);
        }
        return rows;
    }

    private record ProductFixture(String id, String name, BigDecimal price) { }
}
