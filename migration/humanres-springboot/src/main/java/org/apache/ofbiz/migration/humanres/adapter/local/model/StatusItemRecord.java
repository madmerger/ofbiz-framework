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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "LocalStatusItem")
@Table(name = "STATUS_ITEM")
public class StatusItemRecord {
    @Id
    @Column(name = "STATUS_ID", length = 20)
    String statusId;
    @Column(name = "STATUS_TYPE_ID", length = 20)
    String statusTypeId;
    @Column(name = "DESCRIPTION", length = 255)
    String description;
    @Column(name = "SEQUENCE_ID", length = 20)
    String sequenceId;

    protected StatusItemRecord() {}

    public StatusItemRecord(String statusId, String type, String description, String sequenceId) {
        this.statusId = statusId;
        statusTypeId = type;
        this.description = description;
        this.sequenceId = sequenceId;
    }
}
