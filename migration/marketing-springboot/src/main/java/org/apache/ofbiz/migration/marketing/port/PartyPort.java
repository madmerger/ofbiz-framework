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
package org.apache.ofbiz.migration.marketing.port;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Party component services used by marketing (getPartyEmail, createPartyEmailAddress, createPerson, ...).
 */
public interface PartyPort {

    Optional<String> primaryEmailContactMechId(String partyId);

    List<EmailContact> findActiveEmailContacts(String partyId, String email, String contactMechPurposeTypeId);

    Optional<EmailContact> findActiveEmailContact(String partyId, String contactMechId);

    Optional<String> contactMechTypeId(String contactMechId);

    Optional<String> infoString(String contactMechId);

    String createPartyEmailAddress(String partyId, String email, String contactMechPurposeTypeId, Timestamp fromDate);

    String createPerson(Map<String, Object> person);

    /** partyId of the UserLogin, as OFBiz userLogin.partyId. */
    Optional<String> loginPartyId(String userLoginId);

    void ensurePartyRole(String partyId, String roleTypeId);

    void createPartyRelationship(
            String partyIdFrom, String partyIdTo, String roleTypeIdFrom, String roleTypeIdTo, String typeId);

    void updatePartyContactMech(
            String partyId, String contactMechId, String newContactMechId, String contactMechPurposeTypeId);

    void deletePartyContactMech(String partyId, String contactMechId);

    void setPartyStatus(String partyId, String statusId);

    record EmailContact(String partyId, String contactMechId, String infoString) { }
}
