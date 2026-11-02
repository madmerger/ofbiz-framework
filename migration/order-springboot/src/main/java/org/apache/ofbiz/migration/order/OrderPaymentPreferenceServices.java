/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.ofbiz.migration.order;

import jakarta.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.common.SequenceService;
import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;

@Service
public class OrderPaymentPreferenceServices {
    private final OrderEntitySupport data;
    private final SequenceService sequences;
    private final ServiceHandlerRegistry registry;

    public OrderPaymentPreferenceServices(OrderEntitySupport data, SequenceService sequences, ServiceHandlerRegistry registry) {
        this.data = data;
        this.sequences = sequences;
        this.registry = registry;
    }

    @PostConstruct
    void register() {
        registry.registerHandler("order", "changeOrderPaymentStatus", "OFBTOOLS_VIEW", this::changeStatus);
    }

    public Map<String, Object> changeStatus(Map<String, Object> input) {
        String id = input.get("orderPaymentPreferenceId") == null ? null : input.get("orderPaymentPreferenceId").toString();
        if (id == null || id.isBlank()) throw new MigrationException(HttpStatus.BAD_REQUEST, "orderPaymentPreferenceId is required");
        Object preference = data.require("OrderPaymentPreference", Map.of("orderPaymentPreferenceId", id));
        Object orderId = data.get(preference, "orderId");
        data.require("OrderHeader", Map.of("orderId", orderId));
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("orderId", orderId);
        fields.put("orderPaymentPreferenceId", id);
        fields.put("statusId", data.get(preference, "statusId"));
        fields.put("statusUserLogin", data.get(preference, "createdByUserLogin"));
        fields.put("changeReason", input.get("changeReason"));
        Object previous = data.find("OrderStatus", Map.of("orderId", orderId, "orderPaymentPreferenceId", id)).stream()
                .max(Comparator.comparing(row -> (Timestamp) data.get(row, "statusDatetime"))).orElse(null);
        if (previous != null && fields.entrySet().stream().allMatch(field -> Objects.equals(field.getValue(), data.get(previous, field.getKey())))) {
            return Map.of();
        }
        fields.put("orderStatusId", sequences.nextId("OrderStatus"));
        fields.put("statusDatetime", Timestamp.from(Instant.now()));
        data.create("OrderStatus", fields);
        return Map.of();
    }
}
