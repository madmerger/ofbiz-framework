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
import java.util.Locale;
import java.util.Map;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.product.ProductPermissions;
import org.apache.ofbiz.migration.product.adapter.ProductUnportedPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** Port of PermissionServices.xml:24-27. */
@Component
public class PermissionServices extends UnportedSimpleMethodFile {
    private final ProductPermissions permissions;

    public PermissionServices(ProductUnportedPort unportedPort, ProductPermissions permissions) {
        super(unportedPort);
        this.permissions = permissions;
    }

    @Override
    public Map<String, Object> execute(String serviceName, String methodName, Map<String, Object> input) {
        if ("facilityPermissionCheck".equals(serviceName)) {
            Object actionValue = input.get("mainAction");
            if (actionValue == null || actionValue.toString().isBlank()) {
                throw new MigrationException(HttpStatus.BAD_REQUEST, "Permission main-action parameter missing!");
            }
            String action = actionValue.toString().replaceFirst("^_", "").toUpperCase(Locale.ROOT);
            String alternate = input.get("altPermission") == null
                    ? null
                    : input.get("altPermission").toString();
            boolean allowed = permissions.hasPermission("FACILITY", action)
                    || alternate != null && !alternate.isBlank() && permissions.hasPermission(alternate, action);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("hasPermission", allowed);
            if (!allowed) {
                String resource = input.get("resourceDescription") == null
                        ? "this operation"
                        : input.get("resourceDescription").toString();
                String alternatePermissions = alternate == null || alternate.isBlank()
                        ? ""
                        : ", " + alternate + "_" + action + ", " + alternate + "_ADMIN";
                result.put("failMessage", "Security Error: To run " + resource
                        + " you must have the one of the following permissions: FACILITY_" + action
                        + ", FACILITY_ADMIN" + alternatePermissions);
            }
            return result;
        }
        return super.execute(serviceName, methodName, input);
    }
}
