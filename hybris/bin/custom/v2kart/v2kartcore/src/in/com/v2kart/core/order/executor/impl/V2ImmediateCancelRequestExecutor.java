/**
 *
 */
package in.com.v2kart.core.order.executor.impl;

import in.com.v2kart.core.order.cancel.payment.adapter.impl.V2OrderCancelPaymentServiceAdapter;
import in.com.v2kart.core.order.cancel.payment.adapter.impl.V2OrderCancelPaymentServiceAdapterImpl;
import in.com.v2kart.core.order.cancel.releasestock.V2CancelOrderEntryStockHandler;
import in.com.v2kart.core.process.V2RefundOrderProcessor;
import in.com.v2kart.core.services.V2CalculationService;
import in.com.v2kart.core.services.V2RefundService;
import in.com.v2kart.fulfilmentprocess.strategy.impl.V2SetPartialCancellledStrategy;
import in.com.v2kart.sapinboundintegration.exceptions.V2SapConnectionException;
import in.com.v2kart.sapinboundintegration.services.V2SapInboundSaleOrderIntegrationService;
import in.com.v2kart.sapinboundintegration.ws.ordermodification.SOModifyCancelRes;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.acceleratorservices.orderprocessing.model.OrderModificationProcessModel;
import de.hybris.platform.basecommerce.enums.OrderEntryStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.ordercancel.OrderCancelEntry;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.ordercancel.OrderCancelNotificationServiceAdapter;
import de.hybris.platform.ordercancel.OrderCancelRecordsHandler;
import de.hybris.platform.ordercancel.OrderCancelRequest;
import de.hybris.platform.ordercancel.OrderCancelRequestExecutor;
import de.hybris.platform.ordercancel.OrderCancelResponse;
import de.hybris.platform.ordercancel.OrderUtils;
import de.hybris.platform.ordercancel.exceptions.OrderCancelRecordsHandlerException;
import de.hybris.platform.ordercancel.impl.executors.NotificationServiceAdapterDependent;
import de.hybris.platform.ordercancel.impl.orderstatechangingstrategies.SetCancellledStrategy;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Class <Code>V2ImmediateCancelRequestExecutor</Code> is used to process order
 * Cancel Request. It contains API to process order cancel request.
 * 
 * @author Nagarro_Devraj
 * @since 1.2
 * 
 */
public class V2ImmediateCancelRequestExecutor implements OrderCancelRequestExecutor,
	NotificationServiceAdapterDependent {
    private static final Logger LOG = Logger.getLogger(V2ImmediateCancelRequestExecutor.class.getName());

    /** CalculationService bean injection */
    private V2CalculationService calculationService;

    public V2CalculationService getCalculationService() {
	return calculationService;
    }

    public void setCalculationService(V2CalculationService calculationService) {
	this.calculationService = calculationService;
    }

    /** SetCancellledStrategy bean injection */
    private SetCancellledStrategy completeCancelStatusChangeStrategy;

    /** OrderCancelRecordsHandler bean injection */
    private OrderCancelRecordsHandler orderCancelRecordsHandler;

    /** ModelService bean injection */
    private ModelService modelService;

    /** V2OrderCancelPaymentServiceAdapter bean injection */
    private V2OrderCancelPaymentServiceAdapter paymentServiceAdapter;

    /** V2RefundService bean injection */
    private V2RefundService v2RefundService;

    /** V2SetPartialCancellledStrategy bean injection */
    private V2SetPartialCancellledStrategy partialCancelStatusChangeStrategy;

    /** OrderCancelNotificationServiceAdapter bean injection */
    private OrderCancelNotificationServiceAdapter notificationServiceAdapter;

    /** BusinessProcessService bean injection */
    private BusinessProcessService businessProcessService;

    private V2SapInboundSaleOrderIntegrationService v2SapInboundSaleOrderIntegrationService;

    /** RefundOrderProcessor bean injection */
    private V2RefundOrderProcessor v2RefundOrderProcessor;
    /**
     * V2CancelOrderEntryStockHandler bean injection
     */
    private V2CancelOrderEntryStockHandler v2CancelOrderEntryStockHandler;

    public V2SapInboundSaleOrderIntegrationService getV2SapInboundSaleOrderIntegrationService() {
	return v2SapInboundSaleOrderIntegrationService;
    }

    public void setV2SapInboundSaleOrderIntegrationService(
	    V2SapInboundSaleOrderIntegrationService v2SapInboundSaleOrderIntegrationService) {
	this.v2SapInboundSaleOrderIntegrationService = v2SapInboundSaleOrderIntegrationService;
    }

    public OrderCancelNotificationServiceAdapter getNotificationServiceAdapter() {
	return notificationServiceAdapter;
    }

    /** {@inheritDoc} */
    @Override
    public void processCancelRequest(final OrderCancelRequest orderCancelRequest,
	    OrderCancelRecordEntryModel cancelRequestRecordEntry) throws OrderCancelException {
	OrderModel orderPreview = null;
	try {
	    sendOrderCancelRequestInitiationMail(orderCancelRequest, cancelRequestRecordEntry);
	    orderPreview = v2RefundService.createOrderPreviewAsPerCancellationRequest(orderCancelRequest);
	    cancelRequestRecordEntry = this.paymentServiceAdapter.recalculateOrderAndModifyPayments(orderCancelRequest,
		    orderPreview, cancelRequestRecordEntry);
	    modifyOrderAccrodingToRequest(orderCancelRequest);
	    OrderModel order = orderCancelRequest.getOrder();
	    final String previousOrderState = order.getStatus().getCode();
	    if (!(OrderUtils.hasLivingEntries(order))) {
		if (this.completeCancelStatusChangeStrategy != null) {
		    this.completeCancelStatusChangeStrategy.changeOrderStatusAfterCancelOperation(cancelRequestRecordEntry,
			    true);
		}
	    } else if (this.partialCancelStatusChangeStrategy != null) {
		this.partialCancelStatusChangeStrategy
			.changeOrderStatusAfterCancelOperation(cancelRequestRecordEntry, true);
	    }
	    if (!(previousOrderState.equals(OrderStatus.PENDING.getCode()))) {
		if (this.paymentServiceAdapter != null) {
		    // TODO
		    if (cancelRequestRecordEntry.getModificationRecord() != null) {
			this.sendOrderCancleDataToSAP(cancelRequestRecordEntry, orderCancelRequest, orderPreview);
			/*
			 * v2RefundOrderProcessor.process(orderPreview,
			 * cancelRequestRecordEntry
			 * .getOrderModificationRefundInfo()
			 * .getAmountTobeRefunded());// TODO
			 * this.orderCancelRecordsHandler
			 * .updateRecordEntry(makeInternalResponse
			 * (orderCancelRequest, true, null));
			 */
		    }
		} else if (LOG.isDebugEnabled()) {
		    LOG.debug("Missing OrderCancelPaymentServiceAdapter!");
		}
	    }
	    sendOrderCancelCompletionMail(orderCancelRequest, cancelRequestRecordEntry, order);
	} catch (CalculationException e) {
	    LOG.error("Error recalculating new order", e);
	} finally {
	    this.modelService.remove(orderPreview);
	}
    }

    public void sendOrderCancelCompletionMail(final OrderCancelRequest orderCancelRequest,
	    OrderCancelRecordEntryModel cancelRequestRecordEntry, OrderModel order) {
	if (orderCancelRequest.isPartialCancel()) {
	    final OrderModificationProcessModel orderModificationProcessModel = getBusinessProcessService()
		    .createProcess(
			    "sendOrderPartiallyCanceledEmailProcess-" + cancelRequestRecordEntry.getCode() + "-"
				    + System.currentTimeMillis(), "sendOrderPartiallyCanceledEmailProcess");
	    orderModificationProcessModel.setOrder(order);
	    orderModificationProcessModel.setOrderModificationRecordEntry(cancelRequestRecordEntry);
	    this.modelService.save(orderModificationProcessModel);
	    getBusinessProcessService().startProcess(orderModificationProcessModel);
	} else {
	    final OrderModificationProcessModel orderProcessModel = (OrderModificationProcessModel) getBusinessProcessService()
		    .createProcess(
			    "sendOrderCancelledEmailProcess-" + order.getCode() + "-" + System.currentTimeMillis(),
			    "sendOrderCancelledEmailProcess");
	    orderProcessModel.setOrder(order);
	    orderProcessModel.setOrderModificationRecordEntry(cancelRequestRecordEntry);
	    this.modelService.save(orderProcessModel);
	    getBusinessProcessService().startProcess(orderProcessModel);
	}
    }

    public void sendOrderCancelRequestInitiationMail(final OrderCancelRequest orderCancelRequest,
	    OrderCancelRecordEntryModel cancelRequestRecordEntry) {
	if (this.notificationServiceAdapter == null) {
	    LOG.info("order: " + orderCancelRequest.getOrder().getCode() + " is being processed for "
		    + ((!(orderCancelRequest.isPartialCancel())) ? "complete" : "partial") + " cancel.");
	} else {
	    final OrderModificationProcessModel orderProcessModel = (OrderModificationProcessModel) getBusinessProcessService()
		    .createProcess(
			    "sendOrderCancelPendingEmailProcess-" + orderCancelRequest.getOrder().getCode() + "-"
				    + System.currentTimeMillis(), "sendOrderCancelPendingEmailProcess");
	    orderProcessModel.setOrder(orderCancelRequest.getOrder());
	    orderProcessModel.setOrderModificationRecordEntry(cancelRequestRecordEntry);
	    this.modelService.save(orderProcessModel);
	    getBusinessProcessService().startProcess(orderProcessModel);

	}
    }

    public BusinessProcessService getBusinessProcessService() {
	return businessProcessService;
    }

    public void setBusinessProcessService(BusinessProcessService businessProcessService) {
	this.businessProcessService = businessProcessService;
    }

    /**
     * API is used to update order entry details as per order cancellation
     * request.
     * 
     * @param cancelRequest
     * @throws OrderCancelException
     * @throws CalculationException
     */
    public void modifyOrderAccrodingToRequest(OrderCancelRequest cancelRequest) throws OrderCancelException, CalculationException {
	for (OrderCancelEntry oce : cancelRequest.getEntriesToCancel()) {
	    AbstractOrderEntryModel orderEntry = oce.getOrderEntry();
	    long previousQuantity = orderEntry.getQuantity().longValue();

	    if (oce.getCancelQuantity() <= oce.getOrderEntry().getQuantity().longValue()) {
		orderEntry.setQuantity(Long.valueOf(previousQuantity - oce.getCancelQuantity()));

		if (previousQuantity == oce.getCancelQuantity()) {
		    orderEntry.setQuantityStatus(OrderEntryStatus.DEAD);
		}
		this.modelService.save(orderEntry);
	    } else {
		throw new OrderCancelException(cancelRequest.getOrder().getCode(), "Trying to cancel "
			+ oce.getCancelQuantity() + ", whereas orderEntry (" + orderEntry.getPk()
			+ ") has quantity of " + previousQuantity);
	    }
	}
	OrderModel order = cancelRequest.getOrder();
	this.modelService.save(order);
	// Recalculate Promotion details
	// this.v2RefundService.recalculatePromotions(order);
	this.v2RefundService.recalculatePromotionsForPreview(order);
	calculationService.calculateTotalsForCancellation(order, cancelRequest.isPartialCancel());
	this.modelService.refresh(order);
    }

    /**
     * API is used to send Order modification data to SAP and update order
     * record as per response from SAP.
     * 
     * @param result
     *            - OrderCancelRecordEntryModel data to send SAP
     * @return - TRUE if sent successfully - FALSE otherwise
     * 
     */
    private void sendOrderCancleDataToSAP(final OrderCancelRecordEntryModel result,
	    final OrderCancelRequest orderCancelRequest, final OrderModel refundOrderPreview) {
	LOG.info("Sending Order cancellation data to SAP...");
	StringBuilder respDesc = new StringBuilder();
	boolean success = false;
	// OrderModel order = orderCancelRequest.getOrder();
	try {
	    final SOModifyCancelRes soModifyCancelRes = v2SapInboundSaleOrderIntegrationService
		    .cancelModifyErpSales(result);
	    for (final SOModifyCancelRes.OrderModifyCancelRes orderModRes : soModifyCancelRes.getOrderModifyCancelRes()) {

		if (StringUtils.isNotEmpty(orderModRes.getRespCode())) {
		    respDesc.append("Cancellation resopnse : RespCode[" + orderModRes.getRespCode() + "] RespMsg [" + orderModRes.getRespMsg() + "]");
		    LOG.debug("Order cancellation SAP Response:" + orderModRes.getRespMsg());
		    if ("S".equalsIgnoreCase(orderModRes.getRespCode())) {
			success = true;
		    }
		}
	    }
	    refundOrderPreview.setSapResponseDescription(respDesc.toString());
	    if (success) {
		v2RefundOrderProcessor.process(refundOrderPreview, result.getOrderModificationRefundInfo()
			.getAmountTobeRefunded());// TODO
		this.orderCancelRecordsHandler.updateRecordEntry(makeInternalResponse(orderCancelRequest, true, null));
		LOG.debug("sending to releasing stock !!!!!!!!!!!");
		this.v2CancelOrderEntryStockHandler.releaseStockAfterOrderCancelling(result, orderCancelRequest);
		LOG.debug("released stock process done !!!!!!!!!!!");
	    } else {
		refundOrderPreview.setStatus(OrderStatus.CANCELLATION_SENT_TO_SAP_FAILED);
	    }

	} catch (final V2SapConnectionException sape) {
	    LOG.error("Error in sending Order cancellation data to SAP", sape);
	} catch (final OrderCancelRecordsHandlerException e) {
	    LOG.error("Error while updating order cancel record.", e);
	}
    }

    public V2RefundOrderProcessor getV2RefundOrderProcessor() {
	return v2RefundOrderProcessor;
    }

    public void setV2RefundOrderProcessor(V2RefundOrderProcessor v2RefundOrderProcessor) {
	this.v2RefundOrderProcessor = v2RefundOrderProcessor;
    }

    public OrderCancelResponse makeInternalResponse(OrderCancelRequest request, boolean success, String message) {
	if (request.isPartialCancel()) {
	    return new OrderCancelResponse(request.getOrder(), request.getEntriesToCancel(),
		    (success) ? OrderCancelResponse.ResponseStatus.partial : OrderCancelResponse.ResponseStatus.error,
		    message);

	}
	return new OrderCancelResponse(request.getOrder(), request.getEntriesToCancel(),
		(success) ? OrderCancelResponse.ResponseStatus.full : OrderCancelResponse.ResponseStatus.error, message);
    }

    @Required
    public void setCompleteCancelStatusChangeStrategy(SetCancellledStrategy completeCancelStatusChangeStrategy) {
	this.completeCancelStatusChangeStrategy = completeCancelStatusChangeStrategy;
    }

    @Required
    public void setOrderCancelRecordsHandler(OrderCancelRecordsHandler orderCancelRecordsHandler) {
	this.orderCancelRecordsHandler = orderCancelRecordsHandler;
    }

    @Required
    public void setModelService(ModelService modelService) {
	this.modelService = modelService;
    }

    @Required
    public void setPaymentServiceAdapter(V2OrderCancelPaymentServiceAdapterImpl paymentServiceAdapter) {
	this.paymentServiceAdapter = paymentServiceAdapter;
    }

    @Required
    public void setPartialCancelStatusChangeStrategy(V2SetPartialCancellledStrategy partialCancelStatusChangeStrategy) {
	this.partialCancelStatusChangeStrategy = partialCancelStatusChangeStrategy;
    }

    @Required
    public void setNotificationServiceAdapter(OrderCancelNotificationServiceAdapter notificationServiceAdapter) {
	this.notificationServiceAdapter = notificationServiceAdapter;
    }

    @Required
    public void setV2RefundService(V2RefundService v2RefundService) {
	this.v2RefundService = v2RefundService;
    }

    public V2CancelOrderEntryStockHandler getV2CancelOrderEntryStockHandler() {
	return v2CancelOrderEntryStockHandler;
    }

    public void setV2CancelOrderEntryStockHandler(
	    V2CancelOrderEntryStockHandler v2CancelOrderEntryStockHandler) {
	this.v2CancelOrderEntryStockHandler = v2CancelOrderEntryStockHandler;
    }
}
