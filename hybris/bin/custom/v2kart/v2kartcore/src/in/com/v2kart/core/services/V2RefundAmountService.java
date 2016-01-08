package in.com.v2kart.core.services;

import java.util.List;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.OrderCancelEntry;

/**
 * Service to get total refund amount on cancellation
 * 
 * @author shailjagupta
 *
 */
public interface V2RefundAmountService {
    /**
     * Method to return refund amount of an order to be cancelled.o
     * 
     * @param cancelledEntries
     * @param order
     * @return refund ammount
     */
    Double getRefundAmount(final List<OrderCancelEntry> cancelledEntries, final OrderModel order, final Boolean isFull);

}
