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
package org.apache.ofbiz.migration.humanres.adapter.local.model;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity(name = "LocalStatusValidChange")
@Table(name = "STATUS_VALID_CHANGE")
@IdClass(StatusValidChangeRecord.Key.class)
public class StatusValidChangeRecord {
    @Id
    @Column(name = "STATUS_ID", length = 20)
    String statusId;
    @Id
    @Column(name = "STATUS_ID_TO", length = 20)
    String statusIdTo;
    @Column(name = "TRANSITION_NAME", length = 100)
    String transitionName;

    protected StatusValidChangeRecord() {}

    public StatusValidChangeRecord(String statusId, String statusIdTo, String name) {
        this.statusId = statusId;
        this.statusIdTo = statusIdTo;
        transitionName = name;
    }

    public static class Key implements Serializable {
        private static final long serialVersionUID = 1L;
        public String statusId;
        public String statusIdTo;

        public Key() {}

        @Override
        public boolean equals(Object other) {
            return other instanceof Key key
                    && Objects.equals(statusId, key.statusId)
                    && Objects.equals(statusIdTo, key.statusIdTo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(statusId, statusIdTo);
        }
    }
}
