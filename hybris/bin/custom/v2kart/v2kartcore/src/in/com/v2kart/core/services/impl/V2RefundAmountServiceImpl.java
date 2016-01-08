package in.com.v2kart.core.services.impl;

import in.com.v2kart.core.process.V2RefundOrderProcessor;
import in.com.v2kart.core.services.V2RefundAmountService;
import in.com.v2kart.core.services.V2RefundService;
import in.com.v2kart.fulfilmentprocess.model.V2OrderModificationRefundInfoModel;

import java.util.List;

import org.apache.log4j.Logger;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.ordercancel.OrderCancelEntry;
import de.hybris.platform.ordercancel.OrderCancelRequest;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * implementation of v2refundamountservice
 * 
 * @author shailjagupta
 *
 */

public class V2RefundAmountServiceImpl implements V2RefundAmountService {

    private V2RefundService refundService;
    private ModelService modelService;
    private CalculationService calculationService;
    private V2RefundOrderProcessor v2RefundOrderProcessor;
    
    
    public V2RefundOrderProcessor getV2RefundOrderProcessor() {
        return v2RefundOrderProcessor;
    }

    public void setV2RefundOrderProcessor(V2RefundOrderProcessor v2RefundOrderProcessor) {
        this.v2RefundOrderProcessor = v2RefundOrderProcessor;
    }


    private static final Logger LOG = Logger.getLogger(V2RefundAmountServiceImpl.class);

    public CalculationService getCalculationService() {
        return calculationService;
    }

    public void setCalculationService(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public V2RefundService getRefundService() {
        return refundService;
    }

    public void setRefundService(V2RefundService refundService) {
        this.refundService = refundService;
    }

    @Override
    public Double getRefundAmount(final List<OrderCancelEntry> cancelledEntries, final OrderModel order, final Boolean isFull) {
        OrderModel refundPreview = null;
        Double refundAmount = 0d;
        /*final PaymentInfoModel orderPaymentInfo = order.getPaymentInfo();
        if (orderPaymentInfo.getPaymentMode() != null) {
            if ((orderPaymentInfo.getPaymentMode().getMode()).equals("CASH")) {
                LOG.info("No payment transactions found for order " + order.getCode());
                return refundAmount;
            }
        }*/
        if (!isFull) {
            OrderCancelRequest partial = new OrderCancelRequest(order, cancelledEntries);
            refundPreview = getRefundService().createOrderPreviewAsPerCancellationRequest(partial);
        } else {
            OrderCancelRequest full = new OrderCancelRequest(order);
            refundPreview = getRefundService().createOrderPreviewAsPerCancellationRequest(full);
        }
        V2OrderModificationRefundInfoModel refundInfo=null;
        try {
            refundInfo=getV2RefundOrderProcessor().calculateRefundInfoForCancellation(refundPreview, true, isFull);
            refundAmount=refundInfo.getAmountTobeRefunded().doubleValue();
        } finally {
            getModelService().remove(refundInfo);
            getModelService().remove(refundPreview);
        }
        return refundAmount;
    }

}
