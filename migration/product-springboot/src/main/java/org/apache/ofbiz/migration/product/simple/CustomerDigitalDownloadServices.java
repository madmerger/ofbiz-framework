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

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.SequenceService;
import org.apache.ofbiz.migration.product.adapter.ProductUnportedPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Port of applications/product/minilang/product/product/CustomerDigitalDownloadServices.xml:21-198.
 *
 * <p>{@code userLogin} is resolved as the {@code UserLogin} row whose userLoginId is the authenticated
 * principal name; a missing row behaves like a {@code userLogin.partyId} that is empty.
 */
@Component
public class CustomerDigitalDownloadServices extends UnportedSimpleMethodFile {
    /** ProductUiLabels.xml ProductMustBeAssociatedWithSupplier (en). */
    static final String MUST_BE_ASSOCIATED_WITH_SUPPLIER =
            "Cannot update this Product: you are not associated with it as the Supplier";
    /** ProductUiLabels.xml ProductCannotAssociatedToContent (en). */
    static final String CANNOT_ASSOCIATE_TO_CONTENT =
            "Cannot add this Content: you are not associated with it as an Owner";

    private final EntityRegistry entities;
    private final SequenceService sequences;
    private final EntityManager entityManager;

    public CustomerDigitalDownloadServices(ProductUnportedPort unportedPort, EntityRegistry entities,
            SequenceService sequences, EntityManager entityManager) {
        super(unportedPort);
        this.entities = entities;
        this.sequences = sequences;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public Map<String, Object> execute(String serviceName, String methodName, Map<String, Object> input) {
        return switch (methodName) {
            case "createCustomerDigitalDownloadProduct" -> createCustomerDigitalDownloadProduct(input);
            case "updateCustomerDigitalDownloadProduct" -> updateCustomerDigitalDownloadProduct(input);
            case "deleteCustomerDigitalDownloadProduct" -> deleteCustomerDigitalDownloadProduct(input);
            case "addCustomerDigitalDownloadProductFile" -> addCustomerDigitalDownloadProductFile(input);
            case "removeCustomerDigitalDownloadProductFile" -> removeCustomerDigitalDownloadProductFile(input);
            default -> super.execute(serviceName, methodName, input);
        };
    }

    /** Port of CustomerDigitalDownloadServices.xml:23-67. */
    private Map<String, Object> createCustomerDigitalDownloadProduct(Map<String, Object> parameters) {
        Timestamp nowTimestamp = nowTimestamp();
        String userLoginPartyId = userLoginPartyId();

        Object createProductMap = makeValue("Product");
        String productId = sequences.nextId("Product");
        set(createProductMap, "productId", productId);
        set(createProductMap, "productName", parameters.get("productName"));
        set(createProductMap, "internalName", parameters.get("productName"));
        set(createProductMap, "description", parameters.get("description"));
        set(createProductMap, "productTypeId", "DIGITAL_GOOD");
        createValue("Product", createProductMap);

        Object currencyUomId = isEmpty(parameters.get("currencyUomId")) ? "USD" : parameters.get("currencyUomId");

        Object createProductPriceMap = makeValue("ProductPrice");
        set(createProductPriceMap, "productId", productId);
        set(createProductPriceMap, "productPriceTypeId", "DEFAULT_PRICE");
        set(createProductPriceMap, "productPricePurposeId", "PURCHASE");
        set(createProductPriceMap, "currencyUomId", currencyUomId);
        set(createProductPriceMap, "productStoreGroupId", "_NA_");
        set(createProductPriceMap, "fromDate", nowTimestamp);
        set(createProductPriceMap, "price", parameters.get("price"));
        createValue("ProductPrice", createProductPriceMap);

        Object createProductSupplierMap = makeValue("SupplierProduct");
        set(createProductSupplierMap, "productId", productId);
        set(createProductSupplierMap, "partyId", userLoginPartyId);
        set(createProductSupplierMap, "currencyUomId", currencyUomId);
        set(createProductSupplierMap, "minimumOrderQuantity", BigDecimal.ONE);
        set(createProductSupplierMap, "availableFromDate", nowTimestamp);
        createValue("SupplierProduct", createProductSupplierMap);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("productId", productId);
        result.put("currencyUomId", entities.get(createProductSupplierMap, "currencyUomId"));
        result.put("minimumOrderQuantity", entities.get(createProductSupplierMap, "minimumOrderQuantity"));
        result.put("availableFromDate", entities.get(createProductSupplierMap, "availableFromDate"));

        Object productStore = entityOne("ProductStore", parameters);
        if (productStore != null && !isEmpty(entities.get(productStore, "digProdUploadCategoryId"))) {
            // As in the minilang source, fromDate (part of the primary key) is not set here, so OFBiz fails
            // the create-value; createValue reproduces that failure and the whole service rolls back.
            Object addProductToCategoryMap = makeValue("ProductCategoryMember");
            set(addProductToCategoryMap, "productId", productId);
            set(addProductToCategoryMap, "productCategoryId", entities.get(productStore, "digProdUploadCategoryId"));
            createValue("ProductCategoryMember", addProductToCategoryMap);
        }
        return result;
    }

    /** Port of CustomerDigitalDownloadServices.xml:68-111. */
    private Map<String, Object> updateCustomerDigitalDownloadProduct(Map<String, Object> input) {
        Map<String, Object> parameters = new LinkedHashMap<>(input);
        String userLoginPartyId = userLoginPartyId();
        // <set field="parameters.partyId" from-field="userLogin.partyId"/>: set-if-null defaults to false.
        // partyId is not a declared IN attribute, so OFBiz service validation never lets a caller supply it.
        parameters.remove("partyId");
        if (userLoginPartyId != null) {
            parameters.put("partyId", userLoginPartyId);
        }
        Object supplierProduct = entityOne("SupplierProduct", parameters);

        List<String> errors = new ArrayList<>();
        if (supplierProduct == null) {
            errors.add(MUST_BE_ASSOCIATED_WITH_SUPPLIER);
        }
        checkErrors(errors);

        if (!isEmpty(parameters.get("productName")) || !isEmpty(parameters.get("description"))) {
            Object product = entityOne("Product", parameters);
            requireValue(product, "product");
            if (!isEmpty(parameters.get("productName"))) {
                set(product, "productName", parameters.get("productName"));
            }
            if (!isEmpty(parameters.get("description"))) {
                set(product, "description", parameters.get("description"));
            }
            storeValue(product);
        }

        if (!isEmpty(parameters.get("price"))) {
            Map<String, Object> fields = new LinkedHashMap<>();
            fields.put("productId", parameters.get("productId"));
            fields.put("productPriceTypeId", "DEFAULT_PRICE");
            fields.put("productPricePurposeId", "PURCHASE");
            fields.put("productStoreGroupId", "_NA_");
            List<Object> productPriceList = entityAnd("ProductPrice", fields);
            Object productPrice = productPriceList.isEmpty() ? null : productPriceList.get(0);
            requireValue(productPrice, "productPrice");
            set(productPrice, "price", parameters.get("price"));
            storeValue(productPrice);
        }
        return new LinkedHashMap<>();
    }

    /** Port of CustomerDigitalDownloadServices.xml:112-141. */
    private Map<String, Object> deleteCustomerDigitalDownloadProduct(Map<String, Object> parameters) {
        Timestamp nowTimestamp = nowTimestamp();
        List<Object> supplierProductList = supplierProductsOfUser(parameters);

        List<String> errors = new ArrayList<>();
        if (supplierProductList.isEmpty()) {
            errors.add(MUST_BE_ASSOCIATED_WITH_SUPPLIER);
        }
        checkErrors(errors);

        for (Object supplierProduct : supplierProductList) {
            set(supplierProduct, "availableThruDate", nowTimestamp);
            storeValue(supplierProduct);
        }

        // <field-map field-name="productId"/> has no from-field, so it reads the env field "productId".
        // This method never sets that field (only parameters.productId exists), so OFBiz queries
        // productId IS NULL, which matches no ProductCategoryMember row and expires nothing.
        Map<String, Object> memberFields = new LinkedHashMap<>();
        memberFields.put("productId", null);
        List<Object> productCategoryMemberList = entityAnd("ProductCategoryMember", memberFields);
        for (Object productCategoryMember : productCategoryMemberList) {
            set(productCategoryMember, "thruDate", nowTimestamp);
            storeValue(productCategoryMember);
        }
        return new LinkedHashMap<>();
    }

    /** Port of CustomerDigitalDownloadServices.xml:143-177. */
    private Map<String, Object> addCustomerDigitalDownloadProductFile(Map<String, Object> parameters) {
        Timestamp nowTimestamp = nowTimestamp();
        List<Object> supplierProductList = supplierProductsOfUser(parameters);
        List<String> errors = new ArrayList<>();
        if (supplierProductList.isEmpty()) {
            errors.add(MUST_BE_ASSOCIATED_WITH_SUPPLIER);
        }

        Map<String, Object> roleFields = new LinkedHashMap<>();
        roleFields.put("partyId", userLoginPartyId());
        roleFields.put("contentId", parameters.get("contentId"));
        roleFields.put("roleTypeId", "OWNER");
        List<Object> contentRoleList = entityAnd("ContentRole", roleFields);
        if (contentRoleList.isEmpty()) {
            errors.add(CANNOT_ASSOCIATE_TO_CONTENT);
        }

        checkErrors(errors);

        Object productContent = makeValue("ProductContent");
        set(productContent, "productId", parameters.get("productId"));
        set(productContent, "contentId", parameters.get("contentId"));
        set(productContent, "productContentTypeId", "DIGITAL_DOWNLOAD");
        set(productContent, "fromDate", nowTimestamp);
        createValue("ProductContent", productContent);
        return new LinkedHashMap<>();
    }

    /** Port of CustomerDigitalDownloadServices.xml:178-197. */
    private Map<String, Object> removeCustomerDigitalDownloadProductFile(Map<String, Object> parameters) {
        Timestamp nowTimestamp = nowTimestamp();
        List<Object> supplierProductList = supplierProductsOfUser(parameters);

        List<String> errors = new ArrayList<>();
        if (supplierProductList.isEmpty()) {
            errors.add(MUST_BE_ASSOCIATED_WITH_SUPPLIER);
        }
        checkErrors(errors);

        Object productContent = entityOne("ProductContent", parameters);
        requireValue(productContent, "productContent");
        set(productContent, "thruDate", nowTimestamp);
        storeValue(productContent);
        return new LinkedHashMap<>();
    }

    /** The SupplierProduct entity-and shared by CustomerDigitalDownloadServices.xml:116-119, 147-150, 182-185. */
    private List<Object> supplierProductsOfUser(Map<String, Object> parameters) {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("productId", parameters.get("productId"));
        fields.put("partyId", userLoginPartyId());
        return entityAnd("SupplierProduct", fields);
    }

    private String userLoginPartyId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            return null;
        }
        Object userLogin = entities.findByPrimaryKey(
                entities.entityClass("UserLogin"), Map.of("userLoginId", authentication.getName()));
        if (userLogin == null) {
            return null;
        }
        Object partyId = entities.get(userLogin, "partyId");
        return isEmpty(partyId) ? null : partyId.toString();
    }

    /** minilang entity-one with the default auto-field-map: primary key fields are read from parameters. */
    private Object entityOne(String entityName, Map<String, Object> parameters) {
        Class<?> type = entities.entityClass(entityName);
        Map<String, Object> keys = new LinkedHashMap<>();
        for (String key : entities.primaryKeys(type)) {
            Object value = parameters.get(key);
            if (value == null) {
                return null;
            }
            keys.put(key, value);
        }
        return entities.findByPrimaryKey(type, keys);
    }

    /**
     * minilang entity-and. A null field-map value becomes an IS NULL condition in OFBiz; every field queried by
     * this file is a NOT NULL primary key column, so such a condition can never match.
     */
    private List<Object> entityAnd(String entityName, Map<String, Object> fields) {
        if (fields.containsValue(null)) {
            return List.of();
        }
        @SuppressWarnings("unchecked")
        Class<Object> type = (Class<Object>) entities.entityClass(entityName);
        return entities.findByEquality(type, fields);
    }

    private Object makeValue(String entityName) {
        return entities.newEntity(entities.entityClass(entityName));
    }

    private void set(Object value, String field, Object fieldValue) {
        if (fieldValue != null) {
            entities.set(value, field, fieldValue);
        }
    }

    /** minilang create-value: OFBiz rejects the insert when a primary key column is null. */
    private void createValue(String entityName, Object value) {
        for (String key : entities.primaryKeys(value.getClass())) {
            if (isEmpty(entities.get(value, key))) {
                throw new MigrationException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failure in create operation for entity [" + entityName + "]: primary key field "
                                + key + " is null");
            }
        }
        entityManager.persist(value);
    }

    private void storeValue(Object value) {
        entityManager.merge(value);
    }

    /** store-value fails when entity-one / first-from-list left the value field empty. */
    private static void requireValue(Object value, String valueField) {
        if (value == null) {
            throw new MigrationException(HttpStatus.NOT_FOUND, "Entity value not found with name: " + valueField);
        }
    }

    private static void checkErrors(List<String> errors) {
        if (!errors.isEmpty()) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, String.join(" ", errors));
        }
    }

    private static Timestamp nowTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    private static boolean isEmpty(Object value) {
        return value == null || value instanceof String text && text.isEmpty();
    }
}
