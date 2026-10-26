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
package org.apache.ofbiz.migration.workeffort;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestController
public final class WorkEffortMultiController {
    private final MigrationServiceDispatcher dispatcher;
    private final Environment environment;
    private final TransactionTemplate transactionTemplate;

    public WorkEffortMultiController(
            MigrationServiceDispatcher dispatcher,
            Environment environment,
            PlatformTransactionManager transactionManager) {
        this.dispatcher = dispatcher;
        this.environment = environment;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @PostMapping("/api/workeffort/services/{serviceName}/multi")
    public List<Map<String, Object>> multi(
            @PathVariable String serviceName, @RequestBody List<Map<String, Object>> rows) {
        return transactionTemplate.execute(transactionStatus -> {
            if (!"workeffort".equals(environment.getProperty("migration.security.component"))) {
                throw new MigrationException(HttpStatus.NOT_FOUND, "Unknown component: workeffort");
            }
            List<Map<String, Object>> results = new ArrayList<>();
            List<Map<String, Object>> errors = new ArrayList<>();
            HttpStatus firstStatus = HttpStatus.BAD_REQUEST;
            boolean hasConflict = false;
            for (int index = 0; index < rows.size(); index++) {
                try {
                    results.add(dispatcher.execute("workeffort", serviceName, rows.get(index)));
                } catch (MigrationException exception) {
                    HttpStatus status = exception.getStatus();
                    if (errors.isEmpty()) firstStatus = status;
                    hasConflict |= status == HttpStatus.CONFLICT;
                    errors.add(rowError(index, exception.getMessage()));
                } catch (RuntimeException exception) {
                    if (errors.isEmpty()) firstStatus = HttpStatus.BAD_REQUEST;
                    errors.add(rowError(index, exception.getMessage()));
                }
            }
            if (!errors.isEmpty()) {
                throw new WorkEffortMultiException(hasConflict ? HttpStatus.CONFLICT : firstStatus, errors);
            }
            return results;
        });
    }

    private static Map<String, Object> rowError(int index, String message) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("index", index);
        error.put("errorMessage", message);
        return error;
    }
}

@RestControllerAdvice
class WorkEffortMultiExceptionHandler {
    @ExceptionHandler(WorkEffortMultiException.class)
    ResponseEntity<Map<String, Object>> handle(WorkEffortMultiException exception) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("responseMessage", "error");
        body.put("errorMessage", exception.getMessage());
        body.put("errors", exception.getErrors());
        return ResponseEntity.status(exception.getStatus()).body(body);
    }
}
