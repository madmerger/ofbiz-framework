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
package org.apache.ofbiz.migration.product;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
        classes = ProductMigrationApplication.class,
        properties = "migration.security.require-https=false")
@AutoConfigureMockMvc
class UpdateShipmentFlowTest {
    private static final ObjectMapper JSON = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void statusTransitionRecordsHistoryAndReturnsPreviousFields() throws Exception {
        persistShipment("FLOW_SHIP_01", "SHIPMENT_INPUT", "SALES_SHIPMENT");
        persistTransition("SHIPMENT_INPUT", "SHIPMENT_PACKED");
        String eventDate = "2020-01-02 03:04:05";

        mockMvc.perform(post("/api/product/services/updateShipment")
                        .with(user("admin").authorities(authorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(Map.of(
                                "shipmentId", "FLOW_SHIP_01",
                                "statusId", "SHIPMENT_PACKED",
                                "eventDate", eventDate))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.oldStatusId").value("SHIPMENT_INPUT"))
                .andExpect(jsonPath("$.shipmentTypeId").value("SALES_SHIPMENT"))
                .andExpect(jsonPath("$.sideEffectNotes[0]").value(
                        "501: checkAndUpdateWorkEffort (work-effort integration is not ported)"))
                .andExpect(jsonPath("$.sideEffectNotes[1]").value("No-op: createInvoicesFromShipment"));

        var shipment = entities.findByPrimaryKey(entities.entityClass("Shipment"), Map.of("shipmentId", "FLOW_SHIP_01"));
        org.junit.jupiter.api.Assertions.assertEquals("SHIPMENT_PACKED", entities.get(shipment, "statusId"));
        org.junit.jupiter.api.Assertions.assertEquals(1, entities.findByEquality(
                entities.entityClass("ShipmentStatus"), Map.of(
                        "shipmentId", "FLOW_SHIP_01", "statusId", "SHIPMENT_PACKED")).size());
        var statusHistory = entities.findByEquality(entities.entityClass("ShipmentStatus"), Map.of(
                "shipmentId", "FLOW_SHIP_01", "statusId", "SHIPMENT_PACKED")).get(0);
        org.junit.jupiter.api.Assertions.assertEquals(
                java.sql.Timestamp.valueOf(eventDate), entities.get(statusHistory, "statusDate"));
    }

    @Test
    @Transactional
    void invalidTransitionReturnsErrorWithoutChangingShipment() throws Exception {
        persistShipment("FLOW_SHIP_02", "SHIPMENT_INPUT", "SALES_SHIPMENT");

        mockMvc.perform(post("/api/product/services/updateShipment")
                        .with(user("admin").authorities(authorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(Map.of(
                                "shipmentId", "FLOW_SHIP_02", "statusId", "SHIPMENT_SHIPPED"))))
                .andExpect(status().isConflict());

        var shipment = entities.findByPrimaryKey(entities.entityClass("Shipment"), Map.of("shipmentId", "FLOW_SHIP_02"));
        org.junit.jupiter.api.Assertions.assertEquals("SHIPMENT_INPUT", entities.get(shipment, "statusId"));
    }

    @Test
    @Transactional
    void shippedTransitionReturnsNotificationNoOpNote() throws Exception {
        persistShipment("FLOW_SHIP_03", "SHIPMENT_PACKED", "SALES_SHIPMENT");
        persistTransition("SHIPMENT_PACKED", "SHIPMENT_SHIPPED");

        mockMvc.perform(post("/api/product/services/updateShipment")
                        .with(user("admin").authorities(authorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(Map.of(
                                "shipmentId", "FLOW_SHIP_03", "statusId", "SHIPMENT_SHIPPED"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.oldStatusId").value("SHIPMENT_PACKED"))
                .andExpect(jsonPath("$.sideEffectNotes[1]").value(
                        "No-op: sendShipmentCompleteNotification"));
    }

    private void persistShipment(String shipmentId, String statusId, String shipmentTypeId) {
        Object shipment = entities.newEntity(entities.entityClass("Shipment"));
        entities.set(shipment, "shipmentId", shipmentId);
        entities.set(shipment, "statusId", statusId);
        entities.set(shipment, "shipmentTypeId", shipmentTypeId);
        entityManager.persist(shipment);
        entityManager.flush();
    }

    private void persistTransition(String from, String to) {
        if (!entities.findByEquality(entities.entityClass("StatusValidChange"),
                Map.of("statusId", from, "statusIdTo", to)).isEmpty()) {
            return;
        }
        Object transition = entities.newEntity(entities.entityClass("StatusValidChange"));
        entities.set(transition, "statusId", from);
        entities.set(transition, "statusIdTo", to);
        entityManager.persist(transition);
        entityManager.flush();
    }

    private static SimpleGrantedAuthority[] authorities() {
        return java.util.Arrays.stream(new String[] {
            "OFBTOOLS_VIEW", "FACILITY_VIEW", "FACILITY_CREATE", "FACILITY_UPDATE"
        }).map(SimpleGrantedAuthority::new).toArray(SimpleGrantedAuthority[]::new);
    }
}
