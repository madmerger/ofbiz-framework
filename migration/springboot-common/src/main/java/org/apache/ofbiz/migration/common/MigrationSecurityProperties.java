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

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "migration.security")
public class MigrationSecurityProperties {
    private List<String> basePermissions = new ArrayList<>(List.of("OFBTOOLS"));
    private List<String> sensitiveEntities = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private String servicePermissionPrefix = "MIGRATION";
    private String trustedProxies = "a^";
    private boolean requireHttps;

    public List<String> getBasePermissions() { return basePermissions; }
    public void setBasePermissions(List<String> basePermissions) { this.basePermissions = basePermissions; }
    public List<String> getSensitiveEntities() { return sensitiveEntities; }
    public void setSensitiveEntities(List<String> sensitiveEntities) { this.sensitiveEntities = sensitiveEntities; }
    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }
    public String getServicePermissionPrefix() { return servicePermissionPrefix; }
    public void setServicePermissionPrefix(String servicePermissionPrefix) {
        this.servicePermissionPrefix = servicePermissionPrefix;
    }
    public String getTrustedProxies() { return trustedProxies; }
    public void setTrustedProxies(String trustedProxies) { this.trustedProxies = trustedProxies; }
    public boolean isRequireHttps() { return requireHttps; }
    public void setRequireHttps(boolean requireHttps) { this.requireHttps = requireHttps; }

    public boolean isSensitiveEntity(String name) {
        return sensitiveEntities.stream().anyMatch(entity -> entity.equalsIgnoreCase(name));
    }

    public static class User {
        private String username;
        private String password;
        private String passwordHash;
        private List<String> authorities = new ArrayList<>();
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getPasswordHash() { return passwordHash; }
        public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
        public List<String> getAuthorities() { return authorities; }
        public void setAuthorities(List<String> authorities) { this.authorities = authorities; }
    }
}
