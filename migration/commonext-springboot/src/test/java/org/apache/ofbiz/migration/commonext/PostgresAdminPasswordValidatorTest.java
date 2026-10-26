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
package org.apache.ofbiz.migration.commonext;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.apache.ofbiz.migration.common.MigrationSecurityProperties;
import org.junit.jupiter.api.Test;

class PostgresAdminPasswordValidatorTest {
    @Test
    void rejectsPasswordStartingWithBrace() {
        PostgresAdminPasswordValidator validator = validatorWithPassword("{x}");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class, validator::afterPropertiesSet);

        assertEquals("COMMONEXT_ADMIN_PASSWORD must not start with '{'", exception.getMessage());
    }

    @Test
    void acceptsPasswordNotStartingWithBrace() {
        PostgresAdminPasswordValidator validator = validatorWithPassword("postgres-test-password");

        assertDoesNotThrow(validator::afterPropertiesSet);
    }

    private PostgresAdminPasswordValidator validatorWithPassword(String password) {
        MigrationSecurityProperties.User user = new MigrationSecurityProperties.User();
        user.setPassword(password);
        MigrationSecurityProperties properties = new MigrationSecurityProperties();
        properties.setUsers(List.of(user));
        return new PostgresAdminPasswordValidator(properties);
    }
}
