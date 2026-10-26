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
package org.apache.ofbiz.migration.product.simple;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.product.generated.ProductServiceMetadata;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ProductSimpleServiceExecutor {
    public Map<String, Object> execute(
            String serviceName,
            ProductServiceMetadata.SimpleServiceMetadata metadata,
            SimpleMethodFile methodFile,
            Map<String, ?> input) {
        Map<String, Object> converted = new LinkedHashMap<>();
        input.forEach(converted::put);
        for (ProductServiceMetadata.SimpleParam parameter : metadata.params()) {
            String mode = parameter.mode().toUpperCase(Locale.ROOT);
            if (!mode.equals("IN") && !mode.equals("INOUT")) {
                continue;
            }
            Object value = converted.get(parameter.name());
            if (value == null || value instanceof String text && text.isBlank()) {
                if (!parameter.optional()) {
                    throw new MigrationException(HttpStatus.BAD_REQUEST,
                            "Missing required service input parameter: " + parameter.name());
                }
                continue;
            }
            converted.put(parameter.name(), convert(parameter.name(), parameter.type(), value));
        }
        return methodFile.execute(serviceName, metadata.invoke(), converted);
    }

    private static Object convert(String name, String type, Object value) {
        String normalized = type.toLowerCase(Locale.ROOT);
        try {
            return switch (normalized) {
                case "string", "java.lang.string", "id", "id-long", "indicator",
                        "very-long", "comment", "description", "name", "url", "email" ->
                        value instanceof Boolean bool && normalized.equals("indicator")
                                ? bool ? "Y" : "N"
                                : value instanceof String ? value : String.valueOf(value);
                case "boolean", "java.lang.boolean", "boolean-primitive" -> toBoolean(value);
                case "integer", "int", "java.lang.integer" -> new BigDecimal(value.toString()).intValueExact();
                case "long", "java.lang.long" -> new BigDecimal(value.toString()).longValueExact();
                case "short", "java.lang.short" -> new BigDecimal(value.toString()).shortValueExact();
                case "bigdecimal", "java.math.bigdecimal", "numeric", "fixed-point", "currency-amount",
                        "quantity", "floating-point", "number" -> new BigDecimal(value.toString());
                case "timestamp", "java.sql.timestamp", "date-time" -> toTimestamp(value);
                case "date", "java.sql.date" -> Date.valueOf(value instanceof LocalDate date
                        ? date
                        : LocalDate.parse(value.toString()));
                case "time", "java.sql.time" -> Time.valueOf(value instanceof java.time.LocalTime time
                        ? time
                        : java.time.LocalTime.parse(value.toString()));
                case "java.util.map", "map" -> requireType(name, value, Map.class);
                case "java.util.list", "list" -> requireType(name, value, java.util.List.class);
                default -> value;
            };
        } catch (ArithmeticException | IllegalArgumentException | DateTimeParseException exception) {
            throw new MigrationException(HttpStatus.BAD_REQUEST,
                    "Invalid value for service input parameter: " + name);
        }
    }

    private static Boolean toBoolean(Object value) {
        if (value instanceof Boolean bool) {
            return bool;
        }
        String text = value.toString().toLowerCase(Locale.ROOT);
        if (text.equals("true") || text.equals("y")) {
            return true;
        }
        if (text.equals("false") || text.equals("n")) {
            return false;
        }
        throw new IllegalArgumentException("Expected boolean");
    }

    private static Timestamp toTimestamp(Object value) {
        if (value instanceof Timestamp timestamp) {
            return timestamp;
        }
        if (value instanceof Instant instant) {
            return Timestamp.from(instant);
        }
        String text = value.toString();
        try {
            return Timestamp.from(Instant.parse(text));
        } catch (DateTimeParseException ignored) {
            return Timestamp.valueOf(LocalDateTime.parse(text));
        }
    }

    private static Object requireType(String name, Object value, Class<?> type) {
        if (type.isInstance(value)) {
            return value;
        }
        throw new MigrationException(HttpStatus.BAD_REQUEST,
                "Invalid value for service input parameter: " + name);
    }
}
