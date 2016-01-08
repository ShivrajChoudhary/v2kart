package in.com.v2kart.core.order.cancel.payment.adapter.impl;

import in.com.v2kart.fulfilmentprocess.model.V2OrderModificationRefundInfoModel;
import in.com.v2kart.fulfilmentprocess.request.V2OrderCancelRequest;
import in.com.v2kart.core.process.V2RefundOrderProcessor;
import in.com.v2kart.core.services.V2RefundService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.ordercancel.OrderCancelRequest;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.payment.AdapterException;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Adapter Implementation to handle order cancellation request.
 * 
 * @author Nagarro_Devraj
 * @Since 1.2
 * 
 */
public class V2OrderCancelPaymentServiceAdapterImpl implements
		V2OrderCancelPaymentServiceAdapter {

	private static final Logger LOG = Logger
			.getLogger(V2OrderCancelPaymentServiceAdapterImpl.class);

	private final String WALLET_MODE = "WALLET";

	/** CalculationService bean injection */
	private CalculationService calculationService;

	/** RefundOrderProcessor bean injection */
	private V2RefundOrderProcessor v2RefundOrderProcessor;

	public V2RefundOrderProcessor getV2RefundOrderProcessor() {
		return v2RefundOrderProcessor;
	}

	public void setV2RefundOrderProcessor(
			V2RefundOrderProcessor v2RefundOrderProcessor) {
		this.v2RefundOrderProcessor = v2RefundOrderProcessor;
	}

	/** ModelService bean injection */
	private ModelService modelService;

	private V2RefundService refundService;

	public V2RefundService getRefundService() {
		return refundService;
	}

	public void setRefundService(V2RefundService refundService) {
		this.refundService = refundService;
	}

	/**
	 * API is used to recalculate Order And Modify Payments.
	 * 
	 * @param orderCancelRequest
	 */
	@Override
	public OrderCancelRecordEntryModel recalculateOrderAndModifyPayments(
			final OrderCancelRequest orderCancelRequest,
			OrderModel refundOrderPreview,
			OrderCancelRecordEntryModel cancelRequestRecordEntry) {
		OrderModel order = orderCancelRequest.getOrder();
		LOG.info("Recalculating order as per order cancellation request.");
		// String paymentTo=((V2OrderCancelRequest)
		// orderCancelRequest).getRefundInto();
		boolean refundToWallet = true;
		/*
		 * if(paymentTo!=null && paymentTo.isEmpty()){
		 * if(WALLET_MODE.equals(paymentTo)){ refundToWallet=true; } }
		 */

		if (orderCancelRequest instanceof V2OrderCancelRequest) {
			if (((V2OrderCancelRequest) orderCancelRequest)
					.getReturnToGateway() != null) {
				if (((V2OrderCancelRequest) orderCancelRequest)
						.getReturnToGateway() == true) {
					refundToWallet = false;
				}
			}

		}

		try {
			V2OrderModificationRefundInfoModel refundInfo = null;

			if (orderCancelRequest.isPartialCancel()) {
				refundInfo = getV2RefundOrderProcessor()
						.calculateRefundInfoForCancellation(refundOrderPreview,
								refundToWallet, false);
			} else {
				refundInfo = getV2RefundOrderProcessor()
						.calculateRefundInfoForCancellation(refundOrderPreview,
								refundToWallet, true);
				// TODO
			}
			cancelRequestRecordEntry.setOrderModificationRefundInfo(refundInfo);
			this.modelService.save(cancelRequestRecordEntry);
			calculationService.recalculate(order);
		} catch (CalculationException ce) {
			final String message = String.format(
					"Error occurred while calcelling Order[%s]", order);
			LOG.error(message, ce);
			throw new AdapterException(message, ce);
		}
		return cancelRequestRecordEntry;
	}

	public CalculationService getCalculationService() {
		return calculationService;
	}

	public void setCalculationService(CalculationService calculationService) {
		this.calculationService = calculationService;
	}

	public ModelService getModelService() {
		return modelService;
	}

	/**
	 * @param modelService
	 *            the modelService to set
	 */
	@Required
	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}
}
