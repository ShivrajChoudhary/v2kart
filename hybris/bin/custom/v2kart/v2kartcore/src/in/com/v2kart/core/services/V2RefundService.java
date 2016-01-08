/**
 * 
 */
package in.com.v2kart.core.services;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.OrderCancelRequest;
import de.hybris.platform.returns.OrderReturnRecordsHandlerException;
import de.hybris.platform.returns.model.OrderReturnRecordEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;

/**
 * @author Nagarro_devraj802
 * @since 1.2
 * 
 */
public interface V2RefundService {
    /**
     * Create Full Order Refund preview
     * 
     * @param originalOrderModel
     * @return Preview Oreder
     */
    OrderModel createFullOrderRefundPreview(final OrderModel originalOrderModel);

    /**
     * Create Partial Order Refund preview
     * 
     * @param originalOrderModel
     * @return Preview Oreder
     */
    OrderModel createPartialOrderRefundPreview(final OrderModel originalOrderModel, final OrderCancelRequest orderCancelRequest);
    
    /**
     * 
     * @param originalOrderModel
     * @return
     */
    OrderModel createOrderPreview(final OrderModel originalOrderModel);
    
    /**
     * API used to create Order Preview As Per Cancellation Request
     * 
     * @param orderCancelRequest
     * @return
     */
    OrderModel createOrderPreviewAsPerCancellationRequest(final OrderCancelRequest orderCancelRequest);
    
    /**
     * API to recalculate promotions applied on order.
     * @param order
     * @return
     */
    OrderModel recalculatePromotionsForPreview(final OrderModel order);
    
    /** 
     * Apply refunds and return Return Records
     * @param previewOrder
     * @param request
     * @return
     * @throws OrderReturnRecordsHandlerException
     */
    OrderReturnRecordEntryModel getOrderReturnRecordEntry(OrderModel previewOrder, ReturnRequestModel request) throws OrderReturnRecordsHandlerException;
    
    void applyRefunds(OrderModel previewOrder, ReturnRequestModel request);
    
    public OrderModel recalculatePromotionsForReturnPreview(OrderModel order);
    
    
}
