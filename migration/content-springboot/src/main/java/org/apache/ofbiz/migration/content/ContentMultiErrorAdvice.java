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
package org.apache.ofbiz.migration.content;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.ofbiz.migration.common.MigrationServiceDispatcher;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * /multi の失敗応答。いずれかの行が重複作成（ServiceValueFound）なら 409、それ以外は 400 を返す。
 * トランザクションは共通 MigrationServiceDispatcher#executeMulti で全件ロールバックされる。
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public final class ContentMultiErrorAdvice {
    @ExceptionHandler(MigrationServiceDispatcher.MultiServiceException.class)
    public ResponseEntity<Map<String, Object>> multiError(MigrationServiceDispatcher.MultiServiceException exception) {
        boolean conflict = exception.getErrors().stream()
                .anyMatch(error -> "ServiceValueFound".equals(error.get("errorMessage")));
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("responseMessage", "error");
        body.put("errorMessage", exception.getMessage());
        body.put("errors", exception.getErrors());
        return ResponseEntity.status(conflict ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST).body(body);
    }
}
