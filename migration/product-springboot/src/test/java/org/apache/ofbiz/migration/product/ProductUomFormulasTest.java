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
package org.apache.ofbiz.migration.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.product.simple.ProductUomFormulas;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
        classes = ProductMigrationApplication.class,
        properties = "migration.security.require-https=false")
class ProductUomFormulasTest {
    @Autowired
    private ProductUomFormulas formulas;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void ac01_convertsProductBasedMeasuresUsingTheProductDimensions() {
        Object product = entities.newEntity(entities.entityClass("Product"));
        entities.set(product, "productId", "UOM_PRODUCT");
        entities.set(product, "productDepth", new BigDecimal("2"));
        entities.set(product, "productWidth", new BigDecimal("3"));
        entities.set(product, "productHeight", new BigDecimal("4"));
        entityManager.persist(product);

        Object length = entities.newEntity(entities.entityClass("Uom"));
        entities.set(length, "uomId", "UOM_LENGTH");
        entities.set(length, "uomTypeId", "LENGTH_MEASURE");
        entityManager.persist(length);
        Object each = entities.newEntity(entities.entityClass("Uom"));
        entities.set(each, "uomId", "UOM_EACH");
        entities.set(each, "uomTypeId", "QUANTITY_MEASURE");
        entityManager.persist(each);
        entityManager.flush();

        Map<String, Object> arguments = Map.of(
                "conversionParameters", Map.of("productId", "UOM_PRODUCT"),
                "uomConversion", Map.of("conversionFactor", new BigDecimal("1.5")),
                "uomId", "UOM_LENGTH",
                "uomIdTo", "UOM_EACH",
                "originalValue", new BigDecimal("2"));
        Map<String, Object> output = formulas.execute(
                "convertUomProduct", "convertUomProduct", Map.of("arguments", arguments));

        assertEquals(new BigDecimal("1.500000000000000"), output.get("convertedValue"));
    }

    @Test
    void ac02_requiresAProductIdForProductBasedConversion() {
        Map<String, Object> arguments = Map.of("conversionParameters", Map.of());
        assertThrows(MigrationException.class, () ->
                formulas.execute("convertUomProduct", "convertUomProduct", Map.of("arguments", arguments)));
    }
}
