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
package org.apache.ofbiz.migration.marketing.adapter;

import java.util.List;
import java.util.Map;

import org.apache.ofbiz.migration.datamodel.entity.ReturnHeader;
import org.apache.ofbiz.migration.datamodel.entity.ReturnItem;
import org.apache.ofbiz.migration.marketing.MarketingData;
import org.apache.ofbiz.migration.marketing.port.OrderReturnPort;
import org.springframework.stereotype.Component;

/**
 * Local adapter for order component return data.
 */
@Component
public class LocalOrderReturnAdapter implements OrderReturnPort {

    private final MarketingData data;

    public LocalOrderReturnAdapter(MarketingData data) {
        this.data = data;
    }

    @Override
    public boolean returnExists(String returnId) {
        return data.one(ReturnHeader.class, Map.of("returnId", returnId)).isPresent();
    }

    @Override
    public List<ReturnOrderLine> returnOrderLines(String returnId) {
        return data.query(ReturnItem.class,
                        "select r from ReturnItem r where r.returnId = ?1 order by r.returnItemSeqId", returnId)
                .stream()
                .filter(item -> item.getOrderId() != null)
                .map(item -> new ReturnOrderLine(item.getOrderId(), item.getOrderItemSeqId()))
                .toList();
    }
}
