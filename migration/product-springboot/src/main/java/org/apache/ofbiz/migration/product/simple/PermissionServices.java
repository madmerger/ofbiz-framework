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

import java.util.Map;
import org.apache.ofbiz.migration.product.adapter.ProductUnportedPort;
import org.springframework.stereotype.Component;

/** Port of PermissionServices.xml:24-27. */
@Component
public class PermissionServices extends UnportedSimpleMethodFile {
    public PermissionServices(ProductUnportedPort unportedPort) {
        super(unportedPort);
    }

    @Override
    public Map<String, Object> execute(String serviceName, String methodName, Map<String, Object> input) {
        if ("facilityPermissionCheck".equals(serviceName)) {
            return Map.of();
        }
        return super.execute(serviceName, methodName, input);
    }
}
