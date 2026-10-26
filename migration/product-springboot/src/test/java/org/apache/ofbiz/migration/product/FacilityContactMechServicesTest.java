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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.ofbiz.migration.common.EntityRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
        classes = ProductMigrationApplication.class,
        properties = "migration.security.require-https=false")
@AutoConfigureMockMvc
class FacilityContactMechServicesTest {
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final Timestamp START = Timestamp.valueOf("2020-01-02 03:04:05");
    private static final String[] ADMIN_AUTHORITIES = {
        "OFBTOOLS_ADMIN", "FACILITY_ADMIN", "FACILITY_CREATE", "FACILITY_UPDATE", "FACILITY_DELETE"
    };
    private static final String[] READONLY_AUTHORITIES = {"OFBTOOLS_VIEW", "FACILITY_VIEW"};

    @Autowired
    private EntityRegistry entities;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    void createsContactMechFacilityRelationAndPurposeWhenContactIdIsOmitted() throws Exception {
        Map<String, Object> input = input(
                "facilityId", "FAC_CREATE_01",
                "contactMechTypeId", "TELECOM_NUMBER",
                "infoString", "555-0101",
                "contactMechPurposeTypeId", "PRIMARY");
        MvcResult response = submit("createFacilityContactMech", input)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contactMechId").isNotEmpty())
                .andReturn();

        String contactMechId = responseId(response, "contactMechId");
        assertFalse(contactMechId.isBlank());
        Object contactMech = entities.findByPrimaryKey(
                entities.entityClass("ContactMech"), Map.of("contactMechId", contactMechId));
        assertNotNull(contactMech);
        assertEquals("TELECOM_NUMBER", entities.get(contactMech, "contactMechTypeId"));
        List<?> facilityRows = entities.findByEquality(
                entities.entityClass("FacilityContactMech"),
                Map.of("facilityId", "FAC_CREATE_01", "contactMechId", contactMechId));
        assertEquals(1, facilityRows.size());
        List<?> purposes = entities.findByEquality(
                entities.entityClass("FacilityContactMechPurpose"),
                Map.of("facilityId", "FAC_CREATE_01", "contactMechId", contactMechId));
        assertEquals(1, purposes.size());
        assertEquals("PRIMARY", entities.get(purposes.get(0), "contactMechPurposeTypeId"));
    }

    @Test
    @Transactional
    void createContactMechRequiresContactMechTypeWhenGeneratingAnId() throws Exception {
        submit("createFacilityContactMech", input("facilityId", "FAC_CREATE_MISSING_TYPE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("Missing required service input parameter: contactMechTypeId"));
    }

    @Test
    @Transactional
    void createFacilityContactMechRequiresFacilityPermission() throws Exception {
        submit("createFacilityContactMech",
                        input("facilityId", "FAC_READONLY", "contactMechTypeId", "TELECOM_NUMBER"),
                        READONLY_AUTHORITIES)
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void updateContactMechChangesIdAndMovesFacilityPurposes() throws Exception {
        seedContactMech("CM_UPDATE_OLD", "TELECOM_NUMBER", "old value");
        seedFacilityContactMech("FAC_UPDATE_01", "CM_UPDATE_OLD", START);
        seedPurpose("FAC_UPDATE_01", "CM_UPDATE_OLD", "PRIMARY", START);

        MvcResult response = submit(
                        "updateFacilityContactMech",
                        input(
                                "facilityId", "FAC_UPDATE_01",
                                "contactMechId", "CM_UPDATE_OLD",
                                "contactMechTypeId", "TELECOM_NUMBER",
                                "infoString", "new value"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contactMechId").isNotEmpty())
                .andReturn();

        String newContactMechId = responseId(response, "contactMechId");
        assertNotEquals("CM_UPDATE_OLD", newContactMechId);
        Object oldFacilityContactMech = entities.findByPrimaryKey(
                entities.entityClass("FacilityContactMech"),
                input("facilityId", "FAC_UPDATE_01", "contactMechId", "CM_UPDATE_OLD", "fromDate", START));
        assertNotNull(entities.get(oldFacilityContactMech, "thruDate"));
        List<?> newFacilityContactMechs = entities.findByEquality(
                entities.entityClass("FacilityContactMech"),
                Map.of("facilityId", "FAC_UPDATE_01", "contactMechId", newContactMechId));
        assertEquals(1, newFacilityContactMechs.size());
        Object newContactMech = entities.findByPrimaryKey(
                entities.entityClass("ContactMech"), Map.of("contactMechId", newContactMechId));
        assertEquals("new value", entities.get(newContactMech, "infoString"));

        Object oldPurpose = entities.findByPrimaryKey(
                entities.entityClass("FacilityContactMechPurpose"),
                input(
                        "facilityId", "FAC_UPDATE_01",
                        "contactMechId", "CM_UPDATE_OLD",
                        "contactMechPurposeTypeId", "PRIMARY",
                        "fromDate", START));
        assertNotNull(entities.get(oldPurpose, "thruDate"));
        List<?> newPurposes = entities.findByEquality(
                entities.entityClass("FacilityContactMechPurpose"),
                Map.of("facilityId", "FAC_UPDATE_01", "contactMechId", newContactMechId));
        assertEquals(1, newPurposes.size());
        assertEquals("PRIMARY", entities.get(newPurposes.get(0), "contactMechPurposeTypeId"));
    }

    @Test
    @Transactional
    void updateFacilityContactMechRejectsMissingActiveRelation() throws Exception {
        submit(
                        "updateFacilityContactMech",
                        input(
                                "facilityId", "FAC_UPDATE_MISSING",
                                "contactMechId", "CM_UPDATE_MISSING",
                                "contactMechTypeId", "TELECOM_NUMBER"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(
                        "ERROR: Cannot update specified contact info because it does not correspond to the specified party."));
    }

    @Test
    @Transactional
    void updateFacilityContactMechRequiresContactMechType() throws Exception {
        submit(
                        "updateFacilityContactMech",
                        input("facilityId", "FAC_UPDATE_TYPE", "contactMechId", "CM_UPDATE_TYPE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("Missing required service input parameter: contactMechTypeId"));
    }

    @Test
    @Transactional
    void updateFacilityContactMechRequiresFacilityPermission() throws Exception {
        submit(
                        "updateFacilityContactMech",
                        input(
                                "facilityId", "FAC_UPDATE_READONLY",
                                "contactMechId", "CM_UPDATE_READONLY",
                                "contactMechTypeId", "TELECOM_NUMBER"),
                        READONLY_AUTHORITIES)
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void deleteFacilityContactMechExpiresActiveRelation() throws Exception {
        seedFacilityContactMech("FAC_DELETE_01", "CM_DELETE_01", START);

        submit("deleteFacilityContactMech", input(
                        "facilityId", "FAC_DELETE_01",
                        "contactMechId", "CM_DELETE_01"))
                .andExpect(status().isOk());

        Object row = entities.findByPrimaryKey(
                entities.entityClass("FacilityContactMech"),
                input("facilityId", "FAC_DELETE_01", "contactMechId", "CM_DELETE_01", "fromDate", START));
        assertNotNull(entities.get(row, "thruDate"));
    }

    @Test
    @Transactional
    void deleteFacilityContactMechReturnsErrorListWithoutChangingRowsWhenMissing() throws Exception {
        submit("deleteFacilityContactMech", input(
                        "facilityId", "FAC_DELETE_MISSING",
                        "contactMechId", "CM_DELETE_MISSING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorMessageList[0]")
                        .value("ERROR: Could not delete contact info (party contact mech not found)"));
        assertEquals(
                0,
                entities.findByEquality(
                                entities.entityClass("FacilityContactMech"),
                                Map.of(
                                        "facilityId", "FAC_DELETE_MISSING",
                                        "contactMechId", "CM_DELETE_MISSING"))
                        .size());
    }

    @Test
    @Transactional
    void createFacilityPostalAddressPersistsAddressAndFacilityRelation() throws Exception {
        MvcResult response = submit(
                        "createFacilityPostalAddress",
                        input(
                                "facilityId", "FAC_POSTAL_CREATE",
                                "address1", "1 Main Street",
                                "city", "Springfield",
                                "postalCode", "12345",
                                "countryGeoId", "USA",
                                "stateProvinceGeoId", "CA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contactMechId").isNotEmpty())
                .andReturn();

        String contactMechId = responseId(response, "contactMechId");
        Object postalAddress = entities.findByPrimaryKey(
                entities.entityClass("PostalAddress"), Map.of("contactMechId", contactMechId));
        assertEquals("1 Main Street", entities.get(postalAddress, "address1"));
        Object contactMech = entities.findByPrimaryKey(
                entities.entityClass("ContactMech"), Map.of("contactMechId", contactMechId));
        assertEquals("POSTAL_ADDRESS", entities.get(contactMech, "contactMechTypeId"));
        assertEquals(
                1,
                entities.findByEquality(
                                entities.entityClass("FacilityContactMech"),
                                Map.of("facilityId", "FAC_POSTAL_CREATE", "contactMechId", contactMechId))
                        .size());
    }

    @Test
    @Transactional
    void createFacilityPostalAddressRequiresStateForUnitedStates() throws Exception {
        submit(
                        "createFacilityPostalAddress",
                        input(
                                "facilityId", "FAC_POSTAL_STATE",
                                "address1", "1 Main Street",
                                "city", "Springfield",
                                "postalCode", "12345",
                                "countryGeoId", "USA"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("State is missing, and is required for an address in the United States."));
    }

    @Test
    @Transactional
    void createFacilityPostalAddressRequiresPostalCodeInPartyService() throws Exception {
        submit(
                        "createFacilityPostalAddress",
                        input(
                                "facilityId", "FAC_POSTAL_ZIP",
                                "address1", "1 Main Street",
                                "city", "Springfield"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("Missing required service input parameter: postalCode"));
    }

    @Test
    @Transactional
    void createFacilityPostalAddressGeoPointBranchRequiresDataSourceId() throws Exception {
        submit(
                        "createFacilityPostalAddress",
                        input(
                                "facilityId", "FAC_POSTAL_GEO",
                                "address1", "1 Main Street",
                                "city", "Springfield",
                                "postalCode", "12345",
                                "latitude", "37.25",
                                "longitude", "-93.25"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("Missing required service input parameter: dataSourceId"));
    }

    @Test
    @Transactional
    void createFacilityPostalAddressRequiresAddress1() throws Exception {
        submit(
                        "createFacilityPostalAddress",
                        input("facilityId", "FAC_POSTAL_ADDRESS1", "city", "Springfield", "postalCode", "12345"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("Missing required service input parameter: address1"));
    }

    @Test
    @Transactional
    void updateFacilityPostalAddressCreatesNewAddressAndSwitchesFacilityRelation() throws Exception {
        seedContactMech("CM_POSTAL_OLD", "POSTAL_ADDRESS", null);
        seedPostalAddress("CM_POSTAL_OLD", "old address", "Old City", "11111");
        seedFacilityContactMech("FAC_POSTAL_UPDATE", "CM_POSTAL_OLD", START);

        MvcResult response = submit(
                        "updateFacilityPostalAddress",
                        input(
                                "facilityId", "FAC_POSTAL_UPDATE",
                                "contactMechId", "CM_POSTAL_OLD",
                                "address1", "new address",
                                "city", "New City",
                                "postalCode", "22222"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contactMechId").isNotEmpty())
                .andReturn();

        String newContactMechId = responseId(response, "contactMechId");
        assertNotEquals("CM_POSTAL_OLD", newContactMechId);
        Object postalAddress = entities.findByPrimaryKey(
                entities.entityClass("PostalAddress"), Map.of("contactMechId", newContactMechId));
        assertEquals("new address", entities.get(postalAddress, "address1"));
        Object oldFacilityRow = entities.findByPrimaryKey(
                entities.entityClass("FacilityContactMech"),
                input(
                        "facilityId", "FAC_POSTAL_UPDATE",
                        "contactMechId", "CM_POSTAL_OLD",
                        "fromDate", START));
        assertNotNull(entities.get(oldFacilityRow, "thruDate"));
        assertEquals(
                1,
                entities.findByEquality(
                                entities.entityClass("FacilityContactMech"),
                                Map.of("facilityId", "FAC_POSTAL_UPDATE", "contactMechId", newContactMechId))
                        .size());
    }

    @Test
    @Transactional
    void updateFacilityPostalAddressRejectsMissingPostalAddress() throws Exception {
        submit(
                        "updateFacilityPostalAddress",
                        input(
                                "facilityId", "FAC_POSTAL_MISSING",
                                "contactMechId", "CM_POSTAL_MISSING",
                                "address1", "1 Main Street",
                                "city", "Springfield",
                                "postalCode", "12345"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Value not found, cannot update"));
    }

    @Test
    @Transactional
    void createFacilityTelecomNumberPersistsNumberAndFacilityRelation() throws Exception {
        MvcResult response = submit(
                        "createFacilityTelecomNumber",
                        input(
                                "facilityId", "FAC_TELECOM_CREATE",
                                "countryCode", "1",
                                "areaCode", "555",
                                "contactNumber", "0100",
                                "askForName", "Office"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contactMechId").isNotEmpty())
                .andReturn();

        String contactMechId = responseId(response, "contactMechId");
        Object telecomNumber = entities.findByPrimaryKey(
                entities.entityClass("TelecomNumber"), Map.of("contactMechId", contactMechId));
        assertEquals("0100", entities.get(telecomNumber, "contactNumber"));
        Object contactMech = entities.findByPrimaryKey(
                entities.entityClass("ContactMech"), Map.of("contactMechId", contactMechId));
        assertEquals("TELECOM_NUMBER", entities.get(contactMech, "contactMechTypeId"));
        assertEquals(
                1,
                entities.findByEquality(
                                entities.entityClass("FacilityContactMech"),
                                Map.of("facilityId", "FAC_TELECOM_CREATE", "contactMechId", contactMechId))
                        .size());
    }

    @Test
    @Transactional
    void updateFacilityTelecomNumberCreatesReplacementWhenNumberChanges() throws Exception {
        seedContactMech("CM_TELECOM_CHANGED", "TELECOM_NUMBER", null);
        seedTelecomNumber("CM_TELECOM_CHANGED", "555", "0100", "Office");
        seedFacilityContactMech("FAC_TELECOM_CHANGED", "CM_TELECOM_CHANGED", START);

        MvcResult response = submit(
                        "updateFacilityTelecomNumber",
                        input(
                                "facilityId", "FAC_TELECOM_CHANGED",
                                "contactMechId", "CM_TELECOM_CHANGED",
                                "countryCode", "1",
                                "areaCode", "555",
                                "contactNumber", "0200",
                                "askForName", "Office"))
                .andExpect(status().isOk())
                .andReturn();
        String newContactMechId = responseId(response, "contactMechId");
        assertNotEquals("CM_TELECOM_CHANGED", newContactMechId);
        Object telecomNumber = entities.findByPrimaryKey(
                entities.entityClass("TelecomNumber"), Map.of("contactMechId", newContactMechId));
        assertEquals("0200", entities.get(telecomNumber, "contactNumber"));
        assertEquals(
                1,
                entities.findByEquality(
                                entities.entityClass("FacilityContactMech"),
                                Map.of("facilityId", "FAC_TELECOM_CHANGED", "contactMechId", newContactMechId))
                        .size());
    }

    @Test
    @Transactional
    void updateFacilityTelecomNumberKeepsIdWhenNumberIsUnchanged() throws Exception {
        seedContactMech("CM_TELECOM_SAME", "TELECOM_NUMBER", null);
        seed(
                "TelecomNumber",
                input(
                        "contactMechId", "CM_TELECOM_SAME",
                        "countryCode", "1",
                        "areaCode", "555",
                        "contactNumber", "0100",
                        "askForName", "Office"));
        seedFacilityContactMech("FAC_TELECOM_SAME", "CM_TELECOM_SAME", START);

        submit(
                        "updateFacilityTelecomNumber",
                        input(
                                "facilityId", "FAC_TELECOM_SAME",
                                "contactMechId", "CM_TELECOM_SAME",
                                "countryCode", "1",
                                "areaCode", "555",
                                "contactNumber", "0100",
                                "askForName", "Office"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contactMechId").value("CM_TELECOM_SAME"));
        assertEquals(
                2,
                entities.findByEquality(
                                entities.entityClass("FacilityContactMech"),
                                Map.of("facilityId", "FAC_TELECOM_SAME", "contactMechId", "CM_TELECOM_SAME"))
                        .size());
    }

    @Test
    @Transactional
    void createFacilityEmailAddressCreatesContactAndFacilityRelation() throws Exception {
        MvcResult response = submit(
                        "createFacilityEmailAddress",
                        input("facilityId", "FAC_EMAIL_CREATE", "emailAddress", "office@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contactMechId").isNotEmpty())
                .andReturn();
        String contactMechId = responseId(response, "contactMechId");
        Object contactMech = entities.findByPrimaryKey(
                entities.entityClass("ContactMech"), Map.of("contactMechId", contactMechId));
        assertEquals("EMAIL_ADDRESS", entities.get(contactMech, "contactMechTypeId"));
        assertEquals("office@example.com", entities.get(contactMech, "infoString"));
        assertEquals(
                1,
                entities.findByEquality(
                                entities.entityClass("FacilityContactMech"),
                                Map.of("facilityId", "FAC_EMAIL_CREATE", "contactMechId", contactMechId))
                        .size());
    }

    @Test
    @Transactional
    void updateFacilityEmailAddressCreatesReplacementContactMech() throws Exception {
        seedContactMech("CM_EMAIL_OLD", "EMAIL_ADDRESS", "old@example.com");
        seedFacilityContactMech("FAC_EMAIL_UPDATE", "CM_EMAIL_OLD", START);

        MvcResult response = submit(
                        "updateFacilityEmailAddress",
                        input(
                                "facilityId", "FAC_EMAIL_UPDATE",
                                "contactMechId", "CM_EMAIL_OLD",
                                "emailAddress", "new@example.com"))
                .andExpect(status().isOk())
                .andReturn();

        String newContactMechId = responseId(response, "contactMechId");
        assertNotEquals("CM_EMAIL_OLD", newContactMechId);
        Object contactMech = entities.findByPrimaryKey(
                entities.entityClass("ContactMech"), Map.of("contactMechId", newContactMechId));
        assertEquals("new@example.com", entities.get(contactMech, "infoString"));
    }

    @Test
    @Transactional
    void emailServicesRejectMalformedAddress() throws Exception {
        submit(
                        "createFacilityEmailAddress",
                        input("facilityId", "FAC_EMAIL_INVALID", "emailAddress", "not-an-email"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("PartyEmailAddressNotFormattedCorrectly"));
    }

    @Test
    @Transactional
    void updateFacilityEmailAddressRejectsMalformedAddress() throws Exception {
        submit(
                        "updateFacilityEmailAddress",
                        input(
                                "facilityId", "FAC_EMAIL_INVALID_UPDATE",
                                "contactMechId", "CM_EMAIL_INVALID_UPDATE",
                                "emailAddress", "not-an-email"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("PartyEmailAddressNotFormattedCorrectly"));
    }

    @Test
    @Transactional
    void createEmailRequiresCreatePermissionForNestedFacilityContactMech() throws Exception {
        submit(
                        "createFacilityEmailAddress",
                        input("facilityId", "FAC_EMAIL_PERMISSION", "emailAddress", "office@example.com"),
                        "OFBTOOLS_VIEW",
                        "FACILITY_UPDATE")
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void createFacilityContactMechPurposeReturnsFromDate() throws Exception {
        MvcResult response = submit(
                        "createFacilityContactMechPurpose",
                        input(
                                "facilityId", "FAC_PURPOSE_CREATE",
                                "contactMechId", "CM_PURPOSE_CREATE",
                                "contactMechPurposeTypeId", "PRIMARY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromDate").isNotEmpty())
                .andReturn();

        String fromDate = JSON.readTree(response.getResponse().getContentAsString())
                .path("fromDate")
                .asText();
        assertFalse(fromDate.isBlank());
        List<?> purposes = entities.findByEquality(
                entities.entityClass("FacilityContactMechPurpose"),
                Map.of(
                        "facilityId", "FAC_PURPOSE_CREATE",
                        "contactMechId", "CM_PURPOSE_CREATE",
                        "contactMechPurposeTypeId", "PRIMARY"));
        assertEquals(1, purposes.size());
    }

    @Test
    @Transactional
    void createFacilityContactMechPurposeRejectsActiveDuplicate() throws Exception {
        seedPurpose("FAC_PURPOSE_DUP", "CM_PURPOSE_DUPLICATE", "PRIMARY", START);

        submit(
                        "createFacilityContactMechPurpose",
                        input(
                                "facilityId", "FAC_PURPOSE_DUP",
                                "contactMechId", "CM_PURPOSE_DUPLICATE",
                                "contactMechPurposeTypeId", "PRIMARY"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("Could not create new purpose, a purpose with that type already exists"));
    }

    @Test
    @Transactional
    void createFacilityContactMechPurposeRequiresPurposeType() throws Exception {
        submit(
                        "createFacilityContactMechPurpose",
                        input("facilityId", "FAC_PURPOSE_TYPE", "contactMechId", "CM_PURPOSE_TYPE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void deleteFacilityContactMechPurposeExpiresExistingPurpose() throws Exception {
        seedPurpose("FAC_PURPOSE_DELETE", "CM_PURPOSE_DELETE", "PRIMARY", START);

        submit(
                        "deleteFacilityContactMechPurpose",
                        input(
                                "facilityId", "FAC_PURPOSE_DELETE",
                                "contactMechId", "CM_PURPOSE_DELETE",
                                "contactMechPurposeTypeId", "PRIMARY",
                                "fromDate", START.toString()))
                .andExpect(status().isOk());

        Object purpose = entities.findByPrimaryKey(
                entities.entityClass("FacilityContactMechPurpose"),
                input(
                        "facilityId", "FAC_PURPOSE_DELETE",
                        "contactMechId", "CM_PURPOSE_DELETE",
                        "contactMechPurposeTypeId", "PRIMARY",
                        "fromDate", START));
        assertNotNull(entities.get(purpose, "thruDate"));
    }

    @Test
    @Transactional
    void deleteFacilityContactMechPurposeRejectsMissingPurpose() throws Exception {
        submit(
                        "deleteFacilityContactMechPurpose",
                        input(
                                "facilityId", "FAC_PURPOSE_MISSING",
                                "contactMechId", "CM_PURPOSE_MISSING",
                                "contactMechPurposeTypeId", "PRIMARY",
                                "fromDate", START.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Unable to locate purpose; cannot delete"));
    }

    private ResultActions submit(String serviceName, Map<String, ?> parameters) throws Exception {
        return submit(serviceName, parameters, ADMIN_AUTHORITIES);
    }

    private ResultActions submit(String serviceName, Map<String, ?> parameters, String... authorities)
            throws Exception {
        SimpleGrantedAuthority[] grantedAuthorities = Arrays.stream(authorities)
                .map(SimpleGrantedAuthority::new)
                .toArray(SimpleGrantedAuthority[]::new);
        return mockMvc.perform(post("/api/product/services/" + serviceName)
                .with(user("facility").authorities(grantedAuthorities))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.writeValueAsBytes(parameters)));
    }

    private void seedContactMech(String contactMechId, String type, String infoString) {
        Map<String, Object> values = input(
                "contactMechId", contactMechId,
                "contactMechTypeId", type);
        if (infoString != null) {
            values.put("infoString", infoString);
        }
        seed("ContactMech", values);
    }

    private void seedPostalAddress(String contactMechId, String address1, String city, String postalCode) {
        seed(
                "PostalAddress",
                input(
                        "contactMechId", contactMechId,
                        "address1", address1,
                        "city", city,
                        "postalCode", postalCode));
    }

    private void seedTelecomNumber(
            String contactMechId, String areaCode, String contactNumber, String askForName) {
        seed(
                "TelecomNumber",
                input(
                        "contactMechId", contactMechId,
                        "areaCode", areaCode,
                        "contactNumber", contactNumber,
                        "askForName", askForName));
    }

    private void seedFacilityContactMech(String facilityId, String contactMechId, Timestamp fromDate) {
        seed(
                "FacilityContactMech",
                input(
                        "facilityId", facilityId,
                        "contactMechId", contactMechId,
                        "fromDate", fromDate));
    }

    private void seedPurpose(String facilityId, String contactMechId, String purposeTypeId, Timestamp fromDate) {
        seed(
                "FacilityContactMechPurpose",
                input(
                        "facilityId", facilityId,
                        "contactMechId", contactMechId,
                        "contactMechPurposeTypeId", purposeTypeId,
                        "fromDate", fromDate));
    }

    private void seed(String entityName, Map<String, ?> fields) {
        Object entity = entities.newEntity(entities.entityClass(entityName));
        fields.forEach((name, value) -> entities.set(entity, name, value));
        entityManager.persist(entity);
        entityManager.flush();
    }

    private static String responseId(MvcResult result, String field) throws Exception {
        JsonNode output = JSON.readTree(result.getResponse().getContentAsString());
        return output.path(field).asText();
    }

    private static Map<String, Object> input(Object... keyValues) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int index = 0; index < keyValues.length; index += 2) {
            result.put((String) keyValues[index], keyValues[index + 1]);
        }
        return result;
    }
}
