package in.com.v2kart.core.order.cancel.payment.adapter.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.OrderCancelRequest;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;

public interface V2OrderCancelPaymentServiceAdapter {
    
    public OrderCancelRecordEntryModel recalculateOrderAndModifyPayments(final OrderCancelRequest orderCancelRequest, OrderModel refundOrderPreview,final OrderCancelRecordEntryModel cancelRequestRecordEntry);

}
