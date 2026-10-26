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
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.apache.ofbiz.migration.product.simple;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Objects;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** Port of ProductUomFormulas.xml:39-155. */
@Component
public class ProductUomFormulas implements SimpleMethodFile {
    private static final int SCALE = 15;
    private final EntityRegistry entities;

    public ProductUomFormulas(EntityRegistry entities) {
        this.entities = entities;
    }

    @Override
    public Map<String, Object> execute(String serviceName, String methodName, Map<String, Object> input) {
        Map<?, ?> arguments = requireMap(input.get("arguments"), "arguments");
        Map<?, ?> conversionParameters = requireMap(arguments.get("conversionParameters"), "conversionParameters");
        Object productId = conversionParameters.get("productId");
        if (productId == null || productId.toString().isBlank()) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Product-based UoM conversion requires a valid productId");
        }
        Object product = entities.findByPrimaryKey(entities.entityClass("Product"), Map.of("productId", productId));
        if (product == null) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Product-based UoM conversion requires a valid productId");
        }
        Map<?, ?> conversion = requireMap(arguments.get("uomConversion"), "uomConversion");
        BigDecimal conversionFactor = decimal(conversion.get("conversionFactor"), "conversionFactor");
        Object fromUom = findUom(arguments.get("uomId"));
        Object toUom = findUom(arguments.get("uomIdTo"));
        String fromType = fromUom == null ? null : Objects.toString(entities.get(fromUom, "uomTypeId"), null);
        String toType = toUom == null ? null : Objects.toString(entities.get(toUom, "uomTypeId"), null);

        BigDecimal productFactor = conversionFactor;
        if (!Objects.equals(fromType, toType)) {
            BigDecimal fromValue = productValue(product, fromType);
            BigDecimal toValue = productValue(product, toType);
            BigDecimal ratio = toValue.divide(fromValue, SCALE, RoundingMode.HALF_UP);
            productFactor = conversionFactor.multiply(ratio).setScale(SCALE, RoundingMode.HALF_UP);
        }
        BigDecimal converted = decimal(arguments.get("originalValue"), "originalValue")
                .multiply(productFactor).setScale(SCALE, RoundingMode.HALF_UP);
        return Map.of("convertedValue", converted);
    }

    private Object findUom(Object uomId) {
        if (uomId == null || uomId.toString().isBlank()) {
            return null;
        }
        return entities.findByPrimaryKey(entities.entityClass("Uom"), Map.of("uomId", uomId));
    }

    private BigDecimal productValue(Object product, String uomType) {
        String field = switch (uomType == null ? "" : uomType) {
            case "LENGTH_MEASURE" -> "productDepth";
            case "AREA_MEASURE" -> null;
            case "VOLUME_DRY_MEASURE" -> null;
            case "WEIGHT_MEASURE" -> "productWeight";
            default -> null;
        };
        if ("AREA_MEASURE".equals(uomType)) {
            return decimal(entities.get(product, "productDepth"), "productDepth")
                    .multiply(decimal(entities.get(product, "productWidth"), "productWidth"))
                    .setScale(SCALE, RoundingMode.HALF_UP);
        }
        if ("VOLUME_DRY_MEASURE".equals(uomType)) {
            return decimal(entities.get(product, "productDepth"), "productDepth")
                    .multiply(decimal(entities.get(product, "productWidth"), "productWidth"))
                    .multiply(decimal(entities.get(product, "productHeight"), "productHeight"))
                    .setScale(SCALE, RoundingMode.HALF_UP);
        }
        return field == null ? BigDecimal.ONE : decimal(entities.get(product, field), field);
    }

    private static BigDecimal decimal(Object value, String name) {
        if (value == null) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Invalid product-based UoM value: " + name);
        }
        try {
            return value instanceof BigDecimal decimal ? decimal : new BigDecimal(value.toString());
        } catch (NumberFormatException exception) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Invalid product-based UoM value: " + name);
        }
    }

    private static Map<?, ?> requireMap(Object value, String name) {
        if (value instanceof Map<?, ?> map) {
            return map;
        }
        throw new MigrationException(HttpStatus.BAD_REQUEST, "Missing product-based UoM input: " + name);
    }
}
