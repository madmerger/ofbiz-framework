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
package org.apache.ofbiz.migration.common;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SequenceService {
    private final JdbcTemplate jdbcTemplate;
    private final EntityRegistry entityRegistry;

    public SequenceService(JdbcTemplate jdbcTemplate, EntityRegistry entityRegistry) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityRegistry = entityRegistry;
    }

    @Transactional
    public String nextId(String sequenceName) {
        List<Long> current = jdbcTemplate.query(
                "select cast(SEQ_ID as bigint) from SEQUENCE_VALUE_ITEM where SEQ_NAME = ? for update",
                (result, row) -> result.getLong(1), sequenceName);
        if (current.isEmpty()) {
            jdbcTemplate.update("insert into SEQUENCE_VALUE_ITEM (SEQ_NAME, SEQ_ID) values (?, ?)", sequenceName, 10000);
            return "10000";
        }
        long next = current.get(0) + 1;
        jdbcTemplate.update("update SEQUENCE_VALUE_ITEM set SEQ_ID = ? where SEQ_NAME = ?", next, sequenceName);
        return Long.toString(next);
    }

    public String nextSubSequence(String entityName, Map<String, ?> parentKeys, String sequenceField) {
        Class<?> type = entityRegistry.entityClass(entityName);
        List<String> keys = entityRegistry.primaryKeys(type);
        if (!keys.contains(sequenceField)) {
            throw new MigrationException(HttpStatus.BAD_REQUEST, "Not a primary key: " + sequenceField);
        }
        Map<String, Object> parent = new LinkedHashMap<>();
        for (String key : keys) {
            if (key.equals(sequenceField)) {
                continue;
            }
            Object value = parentKeys.get(key);
            if (value == null || value.toString().isBlank()) {
                throw new MigrationException(HttpStatus.BAD_REQUEST, "Missing primary key: " + key);
            }
            parent.put(key, value);
        }
        Long maximum = entityRegistry.maxNumericFieldByEquality(type, sequenceField, parent);
        long max = maximum == null ? 0 : maximum;
        return String.format("%05d", max + 1);
    }
}
