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
package org.apache.ofbiz.migration.order;

import org.apache.ofbiz.migration.common.ServiceHandlerRegistry;
import org.springframework.stereotype.Component;

@Component
public class OrderServiceHandlers {
    public OrderServiceHandlers(
            ServiceHandlerRegistry registry, OrderFlowService flows, OrderSimpleServices simpleServices) {
        register(registry, "storeOrder", "CREATE", flows::storeOrder);
        register(registry, "changeOrderStatus", "UPDATE", flows::changeOrderStatus);
        register(registry, "changeOrderItemStatus", "UPDATE", flows::changeOrderItemStatus);
        register(registry, "cancelOrderItem", "UPDATE", flows::cancelOrderItem);
        register(registry, "cancelOrderItemNoActions", "UPDATE", flows::cancelOrderItemNoActions);
        register(registry, "quickShipEntireOrder", "UPDATE", flows::quickShipEntireOrder);
        register(registry, "createInvoiceForOrder", "UPDATE", flows::createInvoiceForOrder);

        register(registry, "orderAdjustmentPermissionCheck", "VIEW", simpleServices::orderAdjustmentPermissionCheck);
        register(registry, "updateOrderStatusFromReceipt", "UPDATE", simpleServices::updateOrderStatusFromReceipt);
        register(registry, "returnPermissionCheck", "VIEW", simpleServices::returnPermissionCheck);
        register(registry, "custRequestPermissionCheck", "VIEW", simpleServices::custRequestPermissionCheck);
        register(registry, "createCustRequest", "CREATE", simpleServices::createCustRequest);
        register(registry, "updateCustRequest", "UPDATE", simpleServices::updateCustRequest);
        register(registry, "deleteCustRequest", "DELETE", simpleServices::deleteCustRequest);
        registry.registerHandler("order", "copyCustRequestItem", "ORDERMGR_CRQ_CREATE",
                simpleServices::copyCustRequestItem);
        register(registry, "checkStatusCustRequest", "VIEW", simpleServices::checkStatusCustRequest);
        register(registry, "updateCustRequestLastModifiedDate", "UPDATE",
                simpleServices::updateCustRequestLastModifiedDate);
        register(registry, "createCustRequestNote", "CREATE", simpleServices::createCustRequestNote);
        register(registry, "updateCustRequestNote", "UPDATE", simpleServices::updateCustRequestNote);
        register(registry, "createCustRequestItemNote", "CREATE", simpleServices::createCustRequestItemNote);
        register(registry, "createCustRequestFromCart", "CREATE", simpleServices::createCustRequestFromCart);
        register(registry, "createCustRequestFromShoppingList", "CREATE",
                simpleServices::createCustRequestFromShoppingList);
        register(registry, "getCustRequestsByRole", "VIEW", simpleServices::getCustRequestsByRole);
        register(registry, "setCustRequestStatus", "UPDATE", simpleServices::setCustRequestStatus);
        register(registry, "createCustRequestFromCommEvent", "CREATE",
                simpleServices::createCustRequestFromCommEvent);
        register(registry, "createRequirement", "CREATE", simpleServices::createRequirement);
    }

    private static void register(
            ServiceHandlerRegistry registry,
            String serviceName,
            String permission,
            ServiceHandlerRegistry.SimpleServiceHandler handler) {
        registry.registerHandler("order", serviceName, permission, handler);
    }
}
