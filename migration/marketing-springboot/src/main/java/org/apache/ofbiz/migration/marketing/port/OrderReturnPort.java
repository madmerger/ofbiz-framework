package org.apache.ofbiz.migration.marketing.port;

import java.util.List;

/**
 * Order component data used by tracking code return processing (ReturnHeader / ReturnItem).
 */
public interface OrderReturnPort {

    boolean returnExists(String returnId);

    List<ReturnOrderLine> returnOrderLines(String returnId);

    record ReturnOrderLine(String orderId, String orderItemSeqId) { }
}
