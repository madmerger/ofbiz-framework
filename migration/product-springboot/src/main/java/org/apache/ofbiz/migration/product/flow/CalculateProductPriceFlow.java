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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.ofbiz.migration.product.flow;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class CalculateProductPriceFlow {
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final EntityRegistry entities;

    public CalculateProductPriceFlow(EntityRegistry entities) {
        this.entities = entities;
    }

    /**
     * Port of PriceServices.java:84-239, 260-326, 344-432.
     */
    public Map<String, Object> execute(Map<String, Object> input) {
        String productId = required(input, "productId");
        Timestamp at = timestamp(input.get("atDate"));
        Class<?> productType = entities.entityClass("Product");
        Object product = entities.findByPrimaryKey(productType, Map.of("productId", productId));
        if (product == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "Product not found: " + productId);
        }

        String storeGroup = nonEmpty(value(input.get("productStoreGroupId")));
        Object store = null;
        String productStoreId = nonEmpty(value(input.get("productStoreId")));
        if (productStoreId != null) {
            store = entities.findByPrimaryKey(
                    entities.entityClass("ProductStore"), Map.of("productStoreId", productStoreId));
        }
        if (storeGroup == null && store != null) {
            storeGroup = nonEmpty(value(entities.get(store, "primaryStoreGroupId")));
            if (storeGroup == null) {
                storeGroup = activeStoreGroup(productStoreId, at);
            }
        }
        if (storeGroup == null) {
            storeGroup = "_NA_";
        }
        String currency = nonEmpty(value(input.get("currencyUomId")));
        if (currency == null && store != null) {
            currency = nonEmpty(value(entities.get(store, "defaultCurrencyUomId")));
        }
        if (currency == null) {
            currency = "USD";
        }
        String purpose = nonEmpty(value(input.get("productPricePurposeId")));
        if (purpose == null) {
            purpose = "PURCHASE";
        }
        String term = nonEmpty(value(input.get("termUomId")));

        List<Object> prices = productPrices(productId, purpose, currency, storeGroup, term, at);
        List<Object> virtualPrices = List.of();
        String virtualProductId = variantVirtualProductId(product, at);
        if (virtualProductId != null) {
            virtualPrices = productPrices(virtualProductId, purpose, currency, storeGroup, term, at);
        }

        Map<String, Object> selected = new LinkedHashMap<>();
        for (String priceType : List.of("DEFAULT_PRICE", "LIST_PRICE", "AVERAGE_COST", "COMPETITIVE_PRICE",
                "PROMO_PRICE", "MINIMUM_PRICE", "MAXIMUM_PRICE", "WHOLESALE_PRICE", "SPECIAL_PROMO_PRICE")) {
            Object price = firstPrice(prices, priceType);
            if (price == null) {
                price = firstPrice(virtualPrices, priceType);
            }
            if (price != null) {
                selected.put(priceType, price);
            }
        }

        if ("Y".equals(value(entities.get(product, "isVirtual"))) && !selected.containsKey("DEFAULT_PRICE")) {
            Object cheapest = lowestPricedVariant(productId, purpose, currency, storeGroup, term, at);
            if (cheapest != null) {
                Map<String, Object> variantPrices = cheapest instanceof Map<?, ?> map ? castMap(map) : Map.of();
                variantPrices.forEach(selected::putIfAbsent);
            }
        }

        BigDecimal defaultPrice = amount(selected.get("DEFAULT_PRICE"));
        BigDecimal listPrice = amountOrNull(selected.get("LIST_PRICE"));
        boolean validPriceFound = selected.containsKey("DEFAULT_PRICE");
        BigDecimal maximum = amountOrNull(selected.get("MAXIMUM_PRICE"));
        BigDecimal minimum = amountOrNull(selected.get("MINIMUM_PRICE"));
        if (maximum != null && defaultPrice.compareTo(maximum) > 0) {
            defaultPrice = maximum;
        }
        if (minimum != null && defaultPrice.compareTo(minimum) < 0) {
            defaultPrice = minimum;
            validPriceFound = true;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("price", defaultPrice);
        result.put("basePrice", defaultPrice);
        result.put("defaultPrice", defaultPrice);
        result.put("listPrice", listPrice);
        result.put("averageCost", selected.get("AVERAGE_COST"));
        result.put("competitivePrice", selected.get("COMPETITIVE_PRICE"));
        result.put("promoPrice", selected.get("PROMO_PRICE"));
        result.put("specialPromoPrice", selected.get("SPECIAL_PROMO_PRICE"));
        result.put("isSale", false);
        result.put("validPriceFound", validPriceFound);
        result.put("currencyUsed", currency);
        result.put("orderItemPriceInfos", List.of());
        return result;
    }

    private List<Object> productPrices(
            String productId, String purpose, String currency, String storeGroup, String term, Timestamp at) {
        Map<String, Object> filters = new LinkedHashMap<>();
        filters.put("productId", productId);
        filters.put("currencyUomId", currency);
        filters.put("productStoreGroupId", storeGroup);
        List<Object> found = new ArrayList<>(entities.findByEquality(entities.entityClass("ProductPrice"), filters));
        if ("PURCHASE".equals(purpose)) {
            found.removeIf(price -> {
                Object rowPurpose = entities.get(price, "productPricePurposeId");
                return rowPurpose != null && !purpose.equals(rowPurpose.toString());
            });
        } else {
            found.removeIf(price -> !purpose.equals(value(entities.get(price, "productPricePurposeId"))));
        }
        found.removeIf(price -> term != null && !term.equals(value(entities.get(price, "termUomId"))));
        found.removeIf(price -> !active(price, at));
        found.sort(Comparator.comparing(
                price -> dateValue(entities.get(price, "fromDate")),
                Comparator.nullsLast(Comparator.reverseOrder())));
        return found;
    }

    private Object firstPrice(List<Object> prices, String type) {
        return prices.stream()
                .filter(price -> type.equals(value(entities.get(price, "productPriceTypeId"))))
                .findFirst()
                .map(price -> entities.get(price, "price"))
                .orElse(null);
    }

    private Object lowestPricedVariant(
            String productId, String purpose, String currency, String storeGroup, String term, Timestamp at) {
        Map<String, Object> assocFilter = Map.of("productId", productId, "productAssocTypeId", "PRODUCT_VARIANT");
        List<Object> variants = new ArrayList<>(entities.findByEquality(entities.entityClass("ProductAssoc"), assocFilter));
        variants.removeIf(assoc -> !active(assoc, at));
        variants.sort(Comparator.comparing(
                assoc -> dateValue(entities.get(assoc, "fromDate")),
                Comparator.nullsLast(Comparator.reverseOrder())));
        BigDecimal lowest = null;
        Map<String, Object> best = null;
        for (Object assoc : variants) {
            String variantId = value(entities.get(assoc, "productIdTo"));
            if (variantId == null) {
                continue;
            }
            Object variant = entities.findByPrimaryKey(entities.entityClass("Product"), Map.of("productId", variantId));
            if (variant == null) {
                continue;
            }
            Date discontinuation = dateValue(entities.get(variant, "salesDiscontinuationDate"));
            if (discontinuation != null && !discontinuation.after(at)) {
                continue;
            }
            List<Object> variantPrices = productPrices(variantId, purpose, currency, storeGroup, term, at);
            BigDecimal variantDefault = amountOrNull(firstPrice(variantPrices, "DEFAULT_PRICE"));
            if (variantDefault != null && (lowest == null || variantDefault.compareTo(lowest) < 0)) {
                lowest = variantDefault;
                best = new LinkedHashMap<>();
                for (String type : List.of("DEFAULT_PRICE", "LIST_PRICE", "AVERAGE_COST", "COMPETITIVE_PRICE",
                        "PROMO_PRICE", "MINIMUM_PRICE", "MAXIMUM_PRICE", "WHOLESALE_PRICE", "SPECIAL_PROMO_PRICE")) {
                    Object price = firstPrice(variantPrices, type);
                    if (price != null) {
                        best.put(type, price);
                    }
                }
            }
        }
        return best;
    }

    private String activeStoreGroup(String storeId, Timestamp at) {
        if (storeId == null) {
            return null;
        }
        List<Object> members = new ArrayList<>(entities.findByEquality(
                entities.entityClass("ProductStoreGroupMember"), Map.of("productStoreId", storeId)));
        members.removeIf(member -> !active(member, at));
        members.sort(Comparator.comparing(
                        member -> amountOrNull(entities.get(member, "sequenceNum")),
                        Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(member -> dateValue(entities.get(member, "fromDate")),
                        Comparator.nullsLast(Comparator.reverseOrder())));
        return members.isEmpty() ? null : value(entities.get(members.get(0), "productStoreGroupId"));
    }

    private String variantVirtualProductId(Object product, Timestamp at) {
        if (!"Y".equals(value(entities.get(product, "isVariant")))) {
            return null;
        }
        String productId = value(entities.get(product, "productId"));
        List<Object> associations = new ArrayList<>(entities.findByEquality(entities.entityClass("ProductAssoc"),
                Map.of("productIdTo", productId, "productAssocTypeId", "PRODUCT_VARIANT")));
        associations.removeIf(assoc -> !active(assoc, at));
        return associations.isEmpty() ? null : value(entities.get(associations.get(0), "productId"));
    }

    private boolean active(Object entity, Timestamp at) {
        Date from = dateValue(entities.get(entity, "fromDate"));
        Date thru = dateValue(entities.get(entity, "thruDate"));
        return (from == null || !from.after(at)) && (thru == null || thru.after(at));
    }

    private static Timestamp timestamp(Object input) {
        if (input == null) {
            return new Timestamp(System.currentTimeMillis());
        }
        String value = input.toString();
        try {
            return Timestamp.valueOf(value);
        } catch (IllegalArgumentException ignored) {
            return Timestamp.from(Instant.parse(value));
        }
    }

    private static Date dateValue(Object value) {
        return value instanceof Date date ? date : null;
    }

    private static BigDecimal amount(Object value) {
        BigDecimal amount = amountOrNull(value);
        return amount == null ? ZERO : amount;
    }

    private static BigDecimal amountOrNull(Object value) {
        if (value instanceof BigDecimal amount) {
            return amount;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        if (value != null) {
            return new BigDecimal(value.toString());
        }
        return null;
    }

    private static String required(Map<String, Object> input, String name) {
        String value = value(input.get(name));
        if (value == null || value.isBlank()) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Missing required input: " + name);
        }
        return value;
    }

    private static String value(Object input) {
        return input == null ? null : input.toString();
    }

    private static String nonEmpty(String input) {
        return input == null || input.isBlank() ? null : input;
    }

    private static Map<String, Object> castMap(Map<?, ?> map) {
        Map<String, Object> converted = new LinkedHashMap<>();
        map.forEach((key, value) -> converted.put(key.toString(), value));
        return converted;
    }
}
