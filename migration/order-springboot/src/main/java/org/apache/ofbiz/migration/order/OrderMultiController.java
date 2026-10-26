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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.apache.ofbiz.migration.order;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class OrderMultiController {
    private final MigrationServiceDispatcher dispatcher;
    private final ServiceHandlerRegistry registry;
    private final TransactionTemplate transactionTemplate;

    public OrderMultiController(
            MigrationServiceDispatcher dispatcher,
            ServiceHandlerRegistry registry,
            PlatformTransactionManager transactionManager) {
        this.dispatcher = dispatcher;
        this.registry = registry;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @PostMapping("/api/order/services/{serviceName}/multi")
    public ResponseEntity<?> executeMulti(
            @PathVariable("serviceName") String serviceName, @RequestBody List<Map<String, Object>> inputs) {
        if (registry.find("order", serviceName).isEmpty() && registry.handler("order", serviceName).isEmpty()) {
            return failure(HttpStatus.NOT_FOUND, List.of(Map.of(
                    "index", -1,
                    "status", HttpStatus.NOT_FOUND.value(),
                    "errorMessage", "Unknown service: " + serviceName)));
        }
        MultiResult result = transactionTemplate.execute(status -> executeInTransaction(status, serviceName, inputs));
        if (result == null) {
            return failure(HttpStatus.INTERNAL_SERVER_ERROR, List.of(Map.of(
                    "index", -1,
                    "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "errorMessage", "Service failed")));
        }
        if (!result.errors().isEmpty()) {
            HttpStatus status = result.errors().stream()
                    .anyMatch(error -> HttpStatus.CONFLICT.value() == (int) error.get("status"))
                            ? HttpStatus.CONFLICT
                            : HttpStatus.valueOf((int) result.errors().get(0).get("status"));
            return failure(status, result.errors());
        }
        return ResponseEntity.ok(result.results());
    }

    private MultiResult executeInTransaction(
            TransactionStatus transactionStatus, String serviceName, List<Map<String, Object>> inputs) {
        List<Map<String, Object>> results = new ArrayList<>();
        List<Map<String, Object>> errors = new ArrayList<>();
        for (int index = 0; index < inputs.size(); index++) {
            try {
                results.add(dispatcher.execute("order", serviceName, inputs.get(index)));
            } catch (RuntimeException exception) {
                HttpStatus status = exception instanceof MigrationException migrationException
                        ? migrationException.getStatus() : HttpStatus.BAD_REQUEST;
                Map<String, Object> error = new LinkedHashMap<>();
                error.put("index", index);
                error.put("status", status.value());
                error.put("errorMessage", exception.getMessage() == null ? "Service failed" : exception.getMessage());
                errors.add(error);
            }
        }
        if (!errors.isEmpty()) {
            transactionStatus.setRollbackOnly();
        }
        return new MultiResult(results, errors);
    }

    private static ResponseEntity<Map<String, Object>> failure(
            HttpStatus status, List<Map<String, Object>> errors) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("responseMessage", "error");
        body.put("errorMessage", "One or more service calls failed");
        body.put("errors", errors);
        return ResponseEntity.status(status).body(body);
    }

    private record MultiResult(List<Map<String, Object>> results, List<Map<String, Object>> errors) { }
}
