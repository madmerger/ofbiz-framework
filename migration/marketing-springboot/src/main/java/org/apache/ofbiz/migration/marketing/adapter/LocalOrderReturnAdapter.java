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
