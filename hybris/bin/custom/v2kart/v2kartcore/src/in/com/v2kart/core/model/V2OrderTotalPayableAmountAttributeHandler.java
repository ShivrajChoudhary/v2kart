package in.com.v2kart.core.model;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.V2StoreCreditPaymentInfoModel;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;

/**
 * 
 * <code>V2OrderTotalPayableAmountAttributeHandler</code> is the handler for dynamic attribute <b>totalpayableAmount</b> defined in
 * {@link AbstractOrderModel}.
 * 
 * @author Nagarro_Devraj
 * @Since 1.2
 * 
 */
public class V2OrderTotalPayableAmountAttributeHandler implements DynamicAttributeHandler<Integer, AbstractOrderModel> {

    /** {@inheritDoc} */
    @Override
    public Integer get(final AbstractOrderModel order) {
        int totalPayableBalance = 0;
        totalPayableBalance = (int) calculatetotalPayableBalance(order);
        return totalPayableBalance;
    }

    /** {@inheritDoc} */
    @Override
    public void set(final AbstractOrderModel model, final Integer value) {
        // To be added.
    }

    /**
     * API used to calculate total Payable Balance
     * 
     * @param orderModel
     * @return
     */
    private double calculatetotalPayableBalance(final AbstractOrderModel orderModel) {
        double totalPayableBalance = orderModel.getTotalPrice();
        if (null != orderModel && null != orderModel.getStoreCreditPaymentInfo()) {
            PaymentInfoModel storeCreditPaymentInfo =  orderModel.getStoreCreditPaymentInfo();
            if(null!=storeCreditPaymentInfo && storeCreditPaymentInfo instanceof V2StoreCreditPaymentInfoModel){
                V2StoreCreditPaymentInfoModel v2StoreCreditPaymentInfoModel=(V2StoreCreditPaymentInfoModel)storeCreditPaymentInfo;
                totalPayableBalance = totalPayableBalance - v2StoreCreditPaymentInfoModel.getStoreCreditAmount().doubleValue();
            }
        }
        return Math.max(0,totalPayableBalance);
    }
}
