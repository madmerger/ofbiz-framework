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
package org.apache.ofbiz.migration.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.apache.ofbiz.migration.common.MigrationException;
import org.apache.ofbiz.migration.product.generated.ServiceCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
        classes = ProductMigrationApplication.class,
        properties = "migration.security.require-https=false")
@AutoConfigureMockMvc
class ShipmentStatusPermissionTest {
    private static final ObjectMapper JSON = new ObjectMapper();

    @Autowired
    private ProductEntityAutoRegistration registration;

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    void ac01_packedShipmentCannotBeDeletedThroughEntityAutoService() throws Exception {
        persistShipment("SHIP_PACKED_01", "SHIPMENT_PACKED", "Packed");
        ServiceCatalog.ServiceDefinition delete =
                ProductTestSupport.matching("Shipment", "delete", "facility");
        Map<String, Object> input = ProductTestSupport.input(delete, entities, "packed", false);
        input.put("shipmentId", "SHIP_PACKED_01");

        mockMvc.perform(post("/api/product/services/" + delete.name())
                        .with(user("admin").authorities(facilityAuthorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(input)))
                .andExpect(status().isConflict())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Packed [SHIPMENT_PACKED] status.")));
    }

    @Test
    @Transactional
    void ac02_shippedAndDeliveredWrappersRejectTheirRestrictedTransitions() throws Exception {
        persistShipment("SHIP_SHIPPED_01", "SHIPMENT_SHIPPED", "Shipped");
        MigrationException shipped = assertThrows(MigrationException.class,
                () -> registration.checkShipmentStatus("checkCanChangeShipmentStatusShipped",
                        Map.of("shipmentId", "SHIP_SHIPPED_01")));
        assertEquals(409, shipped.getStatus().value());

        persistShipment("SHIP_DELIVERED_01", "SHIPMENT_DELIVERED", "Delivered");
        ServiceCatalog.ServiceDefinition update =
                ProductTestSupport.matching("ShipmentItem", "update", "facility");
        Map<String, Object> input = ProductTestSupport.input(update, entities, "delivered", true);
        input.put("shipmentId", "SHIP_DELIVERED_01");

        mockMvc.perform(post("/api/product/services/" + update.name())
                        .with(user("admin").authorities(facilityAuthorities()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsBytes(input)))
                .andExpect(status().isConflict())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Delivered [SHIPMENT_DELIVERED] status.")));
    }

    private void persistShipment(String shipmentId, String statusId, String description) {
        Object status = entities.newEntity(entities.entityClass("StatusItem"));
        entities.set(status, "statusId", statusId);
        entities.set(status, "description", description);
        entityManager.persist(status);

        Object shipment = entities.newEntity(entities.entityClass("Shipment"));
        entities.set(shipment, "shipmentId", shipmentId);
        entities.set(shipment, "statusId", statusId);
        entityManager.persist(shipment);
        entityManager.flush();
    }

    private static GrantedAuthority[] facilityAuthorities() {
        return java.util.Arrays.stream(new String[] {
            "OFBTOOLS_ADMIN", "FACILITY_ADMIN", "PRODUCT_ADMIN", "FACILITY_CREATE",
            "FACILITY_UPDATE", "FACILITY_DELETE", "PRODUCT_SENSITIVE_VIEW"
        }).map(SimpleGrantedAuthority::new).toArray(GrantedAuthority[]::new);
    }
}
