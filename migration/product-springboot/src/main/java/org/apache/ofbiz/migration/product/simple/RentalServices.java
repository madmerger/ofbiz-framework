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

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.product.adapter.ProductUnportedPort;
import org.springframework.stereotype.Component;

/** Port of RentalServices.xml:24-67. */
@Component
public class RentalServices extends UnportedSimpleMethodFile {
    private final EntityRegistry entities;

    public RentalServices(ProductUnportedPort unportedPort, EntityRegistry entities) {
        super(unportedPort);
        this.entities = entities;
    }

    @Override
    public Map<String, Object> execute(String serviceName, String methodName, Map<String, Object> input) {
        if ("getProductFirstRelatedFixedAsset".equals(serviceName)) {
            return firstRelatedFixedAsset(input);
        }
        return super.execute(serviceName, methodName, input);
    }

    private Map<String, Object> firstRelatedFixedAsset(Map<String, Object> input) {
        Object productId = input.get("productId");
        var related = entities.findByEquality(
                entities.entityClass("FixedAssetProduct"), Map.of("productId", productId));
        Map<String, Object> output = new LinkedHashMap<>();
        output.put("productId", productId);
        if (!related.isEmpty()) {
            output.put("fixedAssetId", entities.get(related.get(0), "fixedAssetId"));
        }
        return output;
    }
}
