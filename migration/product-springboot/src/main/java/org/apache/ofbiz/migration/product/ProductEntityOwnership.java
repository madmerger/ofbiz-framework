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

import jakarta.persistence.EntityManager;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.ofbiz.migration.product.generated.ProductServiceMetadata;
import org.springframework.stereotype.Component;

@Component
public class ProductEntityOwnership {
    private final EntityManager entityManager;

    public ProductEntityOwnership(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Set<String> ownerBases(String requestedName) {
        String canonicalName = entityManager.getMetamodel().getEntities().stream()
                .map(entity -> entity.getJavaType().getSimpleName())
                .filter(name -> name.equalsIgnoreCase(requestedName))
                .findFirst()
                .orElse(null);
        if (canonicalName == null) {
            return null;
        }
        Set<String> owners = ProductServiceMetadata.entityOwners().get(canonicalName);
        if (owners == null) {
            return null;
        }
        return owners.stream()
                .map(owner -> owner.toUpperCase(Locale.ROOT))
                .collect(Collectors.toSet());
    }
}
