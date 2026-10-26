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
package org.apache.ofbiz.migration.humanres;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.humanres.generated.ServiceCatalog;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HumanResServiceDispatcher {
    private final EntityAutoService entityAutoService;
    private final HumanResBusinessServices businessServices;
    private final EntityRegistry entityRegistry;

    public HumanResServiceDispatcher(
            EntityAutoService entityAutoService,
            HumanResBusinessServices businessServices,
            EntityRegistry entityRegistry) {
        this.entityAutoService = entityAutoService;
        this.businessServices = businessServices;
        this.entityRegistry = entityRegistry;
    }

    @Transactional
    public Map<String, Object> execute(String serviceName, Map<String, ?> input) {
        var entityAutoDefinition = ServiceCatalog.find(serviceName);
        String sensitiveResultEntity = entityAutoDefinition
                .map(ServiceCatalog.ServiceDefinition::entity)
                .filter(name -> !name.isBlank())
                .orElseGet(() -> businessServices.sensitiveResultEntity(serviceName));
        if (sensitiveResultEntity != null) {
            entityRegistry.requireSensitiveView(entityRegistry.entityClass(sensitiveResultEntity));
        }
        if (entityAutoDefinition.isPresent()) {
            return entityAutoService.execute(serviceName, input);
        }
        if (businessServices.supports(serviceName)) {
            return businessServices.execute(serviceName, input);
        }
        throw HumanResException.notFound("Unknown service: " + serviceName);
    }

    @Transactional
    public List<Map<String, Object>> executeMulti(String serviceName, List<Map<String, Object>> rows) {
        if (!ServiceCatalog.find(serviceName).isPresent() && !businessServices.supports(serviceName)) {
            throw HumanResException.notFound("Unknown service: " + serviceName);
        }
        List<Map<String, Object>> results = new ArrayList<>();
        List<Map<String, Object>> errors = new ArrayList<>();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        for (int index = 0; index < rows.size(); index++) {
            try {
                results.add(execute(serviceName, rows.get(index)));
            } catch (HumanResException exception) {
                status = exception.getStatus().is4xxClientError() ? exception.getStatus() : HttpStatus.BAD_REQUEST;
                Map<String, Object> rowError = new LinkedHashMap<>();
                rowError.put("index", index);
                rowError.put("errorMessage", exception.getMessage());
                rowError.put("status", status.value());
                errors.add(rowError);
            }
        }
        if (!errors.isEmpty()) {
            throw new MultiServiceException(status, errors);
        }
        return results;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getHrTree(String partyId) {
        return businessServices.getHrTree(partyId);
    }
}
