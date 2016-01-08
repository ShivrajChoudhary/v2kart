package in.com.v2kart.core.process;

import in.com.v2kart.fulfilmentprocess.model.V2OrderModificationRefundInfoModel;

import java.math.BigDecimal;

import de.hybris.platform.core.model.order.OrderModel;

/**
 * 
 * @author shailjagupta
 * 
 */
public interface V2RefundOrderProcessor {
    /**
     * API to process refund for given return/cancel request
     * 
     * @param ordermodel
     * @param refundAmount
     */
    public void process(OrderModel ordermodel, BigDecimal refundAmount);

    /**
     * API to calculate amount to be refunded on cancellation request.
     * 
     * @param refundOrderPreview
     * @param refundToWallet
     * @param isFullCancel
     * @return V2OrderModificationRefundInfoModel
     */
    public V2OrderModificationRefundInfoModel calculateRefundInfoForCancellation(final OrderModel refundOrderPreview,
            final boolean refundToWallet, boolean isFullCancel);

    /**
     * API to calculate amount to be refunded on return request.
     * 
     * @param refundOrderPreview
     * @param refundToWallet
     * @return V2OrderModificationRefundInfoModel
     */
    public V2OrderModificationRefundInfoModel calculateRefundInfoForReturn(OrderModel refundOrderPreview, boolean refundToWallet);
}
