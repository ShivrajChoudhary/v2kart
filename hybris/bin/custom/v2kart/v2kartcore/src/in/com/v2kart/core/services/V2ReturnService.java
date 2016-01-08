package in.com.v2kart.core.services;

import in.com.v2kart.facades.order.data.ReturnData;

import java.util.Map;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;

/**
 * API 's Return module in v2kart
 * 
 * @author shailjagupta
 *
 */
public interface V2ReturnService {

    /**
     * Process full order return from storefront
     * 
     * @param order
     * @param returnData
     */
    public void processFullOrderReturn(final OrderModel order, final ReturnData returnData);

    /**
     * Process partial order return from storefront
     * 
     * @param order
     * @param returnData
     */
    public void processPartialOrderReturn(final OrderModel order, final ReturnData returnData);

    /**
     * Get all returnable entries for order
     * 
     * @param order
     * @return
     */
    public Map<AbstractOrderEntryModel, Long> getAllReturnableOrderEntries(final OrderModel order);

    /**
     * Get tenative refund amount to be displayed on storefront
     * 
     * @param order
     * @param returnData
     * @return
     */
    public Double getRefundAmount(final OrderModel order, final ReturnData returnData);

    /**
     * Process refund for given return request
     * 
     * @param order
     * @param isCancelRequest
     */
    public void processRefundOrder(OrderModel order);

    /**
     * Return wheter order is returnable or not
     * 
     * @param order
     * @return
     */
    public Boolean isOrderReturnable(OrderModel order);

}
