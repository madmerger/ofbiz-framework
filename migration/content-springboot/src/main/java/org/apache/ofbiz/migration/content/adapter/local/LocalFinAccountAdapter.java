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
package org.apache.ofbiz.migration.content.adapter.local;

import java.util.Map;
import org.apache.ofbiz.migration.content.ContentEntityStore;
import org.apache.ofbiz.migration.content.FinAccountPort;
import org.springframework.stereotype.Component;

/** 同一 DB の FinAccount を参照する local adapter。 */
@Component
public final class LocalFinAccountAdapter implements FinAccountPort {
    private final ContentEntityStore store;

    public LocalFinAccountAdapter(ContentEntityStore store) {
        this.store = store;
    }

    @Override
    public boolean isActiveGiftCertificateCode(String code) {
        return code != null && store.findList("FinAccount", Map.of(
                "finAccountTypeId", "GIFTCERT_ACCOUNT", "statusId", "FNACT_ACTIVE", "finAccountCode", code)).size() > 0;
    }
}
