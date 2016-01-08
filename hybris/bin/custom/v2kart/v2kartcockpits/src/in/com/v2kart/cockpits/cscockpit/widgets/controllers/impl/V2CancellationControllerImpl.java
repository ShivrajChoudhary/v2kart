package in.com.v2kart.cockpits.cscockpit.widgets.controllers.impl;

import in.com.v2kart.core.exception.V2Exception;
import in.com.v2kart.core.order.cancel.payment.adapter.impl.V2OrderCancelPaymentServiceAdapter;
import in.com.v2kart.core.order.executor.impl.V2ImmediateCancelRequestExecutor;
import in.com.v2kart.core.services.V2RefundAmountService;
import in.com.v2kart.core.services.V2RefundService;
import in.com.v2kart.fulfilmentprocess.request.V2OrderCancelRequest;
import in.com.v2kart.fulfilmentprocess.strategy.impl.V2SetPartialCancellledStrategy;
import in.com.v2kart.v2kartebspaymentintegration.constants.PaymentConstants.PaymentProperties.EBS;
import in.com.v2kart.v2kartebspaymentintegration.data.response.EBSActionResponse;
import in.com.v2kart.v2kartebspaymentintegration.facades.EBSPaymentFacade;
import in.com.v2kart.v2kartpayupaymentintegration.services.PAYUPaymentService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.basecommerce.enums.OrderCancelEntryStatus;
import de.hybris.platform.basecommerce.enums.OrderCancelState;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.services.values.ObjectValueContainer;
import de.hybris.platform.cockpit.services.values.ObjectValueContainer.ObjectValueHolder;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cscockpit.exceptions.CancelableOrderDenialReasonsException;
import de.hybris.platform.cscockpit.exceptions.ResourceMessage;
import de.hybris.platform.cscockpit.exceptions.ValidationException;
import de.hybris.platform.cscockpit.utils.SafeUnbox;
import de.hybris.platform.cscockpit.widgets.controllers.impl.DefaultCancellationController;
import de.hybris.platform.cscockpit.widgets.controllers.impl.DefaultCancellationController.CancelEntryDetails;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.ordercancel.CancelDecision;
import de.hybris.platform.ordercancel.OrderCancelDeniedException;
import de.hybris.platform.ordercancel.OrderCancelEntry;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.ordercancel.OrderCancelRecordsHandler;
import de.hybris.platform.ordercancel.OrderCancelRequest;
import de.hybris.platform.ordercancel.OrderCancelRequestExecutor;
import de.hybris.platform.ordercancel.OrderCancelStateMappingStrategy;
import de.hybris.platform.ordercancel.OrderUtils;
import de.hybris.platform.ordercancel.exceptions.OrderCancelRecordsHandlerException;
import de.hybris.platform.ordercancel.impl.denialstrategies.StateMappingStrategyDependent;
import de.hybris.platform.ordercancel.impl.orderstatechangingstrategies.SetCancellledStrategy;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

/**
 * Implementation for controller for partial and full cancellation.
 * 
 * @author pravesh.gupta@nagarro.com
 */
public class V2CancellationControllerImpl extends DefaultCancellationController implements V2CancellationController {

    private static final Logger LOGGER = Logger.getLogger(V2CancellationControllerImpl.class);

    private static final String CANCELLED = "Cancelled";

    private static final Object EBS_DC = "EBSDC";

    private static final Object PAYU_CC = "PAYUCC";

    private static final String PAYU = "PAYU";

    private static final String EBS = "EBS";

    private V2RefundService refundService;
    private V2RefundAmountService refundAmountService;
    private transient OrderCancelRequest orderCancelRequest;
    private transient ObjectValueContainer orderCancelRequestOVC;
    private transient List<ObjectValueContainer> orderCancelEntryOVCs;
    private OrderCancelRecordsHandler orderCancelRecordsHandler;
    private OrderCancelStateMappingStrategy stateMappingStrategy;
    private V2ImmediateCancelRequestExecutor cancelRequestExecutor;
    private V2OrderCancelPaymentServiceAdapter paymentServiceAdapter;
    private SetCancellledStrategy completeCancelStatusChangeStrategy;
    private V2SetPartialCancellledStrategy partialCancelStatusChangeStrategy;
    private CalculationService calculationService;
    private EBSPaymentFacade ebsPaymentFacade;
    private PAYUPaymentService payuPaymentService;
    private CommonI18NService commonI18NService;

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public CalculationService getCalculationService() {
        return calculationService;
    }

    public void setCalculationService(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    public V2SetPartialCancellledStrategy getPartialCancelStatusChangeStrategy() {
        return partialCancelStatusChangeStrategy;
    }

    public void setPartialCancelStatusChangeStrategy(V2SetPartialCancellledStrategy partialCancelStatusChangeStrategy) {
        this.partialCancelStatusChangeStrategy = partialCancelStatusChangeStrategy;
    }

    public SetCancellledStrategy getCompleteCancelStatusChangeStrategy() {
        return completeCancelStatusChangeStrategy;
    }

    public void setCompleteCancelStatusChangeStrategy(SetCancellledStrategy completeCancelStatusChangeStrategy) {
        this.completeCancelStatusChangeStrategy = completeCancelStatusChangeStrategy;
    }

    public V2OrderCancelPaymentServiceAdapter getPaymentServiceAdapter() {
        return paymentServiceAdapter;
    }

    public void setPaymentServiceAdapter(V2OrderCancelPaymentServiceAdapter paymentServiceAdapter) {
        this.paymentServiceAdapter = paymentServiceAdapter;
    }

    public OrderCancelStateMappingStrategy getStateMappingStrategy() {
        return stateMappingStrategy;
    }

    public void setStateMappingStrategy(OrderCancelStateMappingStrategy stateMappingStrategy) {
        this.stateMappingStrategy = stateMappingStrategy;
    }

    public OrderCancelRecordsHandler getOrderCancelRecordsHandler() {
        return orderCancelRecordsHandler;
    }

    public void setOrderCancelRecordsHandler(OrderCancelRecordsHandler orderCancelRecordsHandler) {
        this.orderCancelRecordsHandler = orderCancelRecordsHandler;
    }

    /**
     * Sets the refund service.
     * 
     * @param refundService
     *        the new refund service
     */
    @Required
    public void setRefundService(final V2RefundService refundService) {
        this.refundService = refundService;
    }

    /**
     * Sets the refund amount service.
     * 
     * @param refundAmountService
     *        the new refund amount service
     */
    @Required
    public void setRefundAmountService(final V2RefundAmountService refundAmountService) {
        this.refundAmountService = refundAmountService;
    }

    @Override
    public Double getCancellationRefundAmount(final boolean isFull) {
        return getRefundAmountService().getRefundAmount(orderCancelRequest.getEntriesToCancel(), getOrderModel(), isFull);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * in.com.v2kart.cockpits.cscockpit.widgets.controllers.impl.V2CancellationController#createFullCancellationPreview(de.hybris.platform
     * .cockpit.services.values.ObjectValueContainer)
     */
    @Override
    public OrderCancelRequest createFullCancellationPreview(final ObjectValueContainer cancelRequest) throws ValidationException {
        final OrderModel orderModel = getRefundService().recalculatePromotionsForPreview(getOrderModel());
        if (orderModel != null && validateCreateCancellationRequest(orderModel, cancelRequest)) {
            orderCancelRequest = buildCancelRequest(orderModel, cancelRequest);
            orderCancelRequestOVC = cancelRequest;
        }
        return orderCancelRequest;
    }

    @Override
    protected OrderCancelRequest buildCancelRequest(OrderModel order, ObjectValueContainer cancelRequest) {

        String notes = (String) getPropertyValue(cancelRequest, "OrderCancelRequest.notes").getCurrentValue();
        CancelReason reason = (CancelReason) getPropertyValue(cancelRequest, "OrderCancelRequest.cancelReason")
                .getCurrentValue();
        Boolean returnToWallet = (Boolean) getPropertyValue(cancelRequest, "V2OrderCancelRequest.returnToWallet").getCurrentValue();
        Boolean returnToGateway = (Boolean) getPropertyValue(cancelRequest, "V2OrderCancelRequest.returnToGateway").getCurrentValue();

        if (order != null)
        {
            V2OrderCancelRequest v2OrderCancelRequest = new V2OrderCancelRequest(order);
            v2OrderCancelRequest.setCancelReason(reason);
            v2OrderCancelRequest.setNotes(notes);
            v2OrderCancelRequest.setReturnToGateway(returnToGateway);
            v2OrderCancelRequest.setReturnToWallet(returnToWallet);
            return (OrderCancelRequest) v2OrderCancelRequest;
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * in.com.v2kart.cockpits.cscockpit.widgets.controllers.impl.V2CancellationController#createPartialCancellationPreview(java.util.List,
     * de.hybris.platform.cockpit.services.values.ObjectValueContainer)
     */
    @Override
    public OrderCancelRequest createPartialCancellationPreview(final List<ObjectValueContainer> orderEntryCancelRecords,
            final ObjectValueContainer cancelRequest) throws ValidationException, OrderCancelException {
        final OrderModel orderModel = getRefundService().recalculatePromotionsForPreview(getOrderModel());
        try {
            final Map<TypedObject, Long> cancelableOrderEntries = getCancelableOrderEntries();
            if (orderModel != null && validateCreateCancellationRequest(orderModel, cancelableOrderEntries, orderEntryCancelRecords)) {
                final Map<TypedObject, CancelEntryDetails> cancelEntries = getCancelEntries(orderEntryCancelRecords, cancelableOrderEntries);
                orderCancelRequest = buildCancelRequest(orderModel, cancelEntries, cancelRequest);
                orderCancelRequestOVC = cancelRequest;
                orderCancelEntryOVCs = orderEntryCancelRecords;
            }
        } catch (final CancelableOrderDenialReasonsException e) {
            throw new OrderCancelException(orderModel.getCode(), "Failed to cancel", e);
        }
        return orderCancelRequest;
    }

    @Override
    public TypedObject requestFullCancellation() throws OrderCancelException, ValidationException {
        TypedObject orderCancelRecord = null;
        if (orderCancelRequestOVC != null) {
            try {
                orderCancelRecord = createOrderCancellationRequest(orderCancelRequestOVC);
            } finally {
                cleanUp();
            }
        }
        return orderCancelRecord;
    }

    @Override
    public TypedObject createOrderCancellationRequest(ObjectValueContainer cancelRequest) throws OrderCancelException, ValidationException {

        TypedObject order = getOrder();
        if ((order != null) && (order.getObject() instanceof OrderModel))
        {
            OrderModel orderModel = (OrderModel) order.getObject();
            if (validateCreateCancellationRequest(orderModel, cancelRequest))
            {
                try
                {
                    OrderCancelRequest request = buildCancelRequest(orderModel, cancelRequest);
                    if (request != null)
                    {
                        OrderCancelRecordEntryModel orderRequestRecord = null;
                        if (((V2OrderCancelRequest) request).getReturnToWallet() == true) {
                            orderRequestRecord = getOrderCancelService().requestOrderCancel(request,
                                    getUserService().getCurrentUser());
                        } else if (((V2OrderCancelRequest) request).getReturnToGateway() == true) {
                            orderRequestRecord = requestOrderCancelForGateway(request, getUserService().getCurrentUser());
                        }
                        if (OrderCancelEntryStatus.DENIED.equals(orderRequestRecord.getCancelResult()))
                        {
                            String orderCode = ((OrderModel) order.getObject()).getCode();
                            String message = "";
                            if (orderRequestRecord.getRefusedMessage() != null)
                            {
                                message = message + orderRequestRecord.getRefusedMessage();
                            }
                            if (orderRequestRecord.getFailedMessage() != null)
                            {
                                message = message + orderRequestRecord.getFailedMessage();
                            }

                            throw new OrderCancelException(orderCode, message);
                        }
                        return getCockpitTypeService().wrapItem(orderRequestRecord);
                    }
                } catch (OrderCancelException ocEx)
                {
                    throw ocEx;
                } catch (Exception e)
                {
                    LOGGER.warn("Failed to cancel [" + orderModel + "]", e);
                    throw new OrderCancelException("Failed to cancel", e);
                }
            }
        }
        return null;
    }

    private OrderCancelRecordEntryModel requestOrderCancelForGateway(OrderCancelRequest orderCancelRequest, UserModel requestor)
            throws OrderCancelException, V2Exception, CalculationException {
        CancelDecision cancelDecision = getOrderCancelService().isCancelPossible(orderCancelRequest.getOrder(), requestor,
                orderCancelRequest
                        .isPartialCancel(), orderCancelRequest.isPartialEntryCancel());
        OrderCancelRecordEntryModel result = null;
        if (cancelDecision.isAllowed())
        {
            result = this.orderCancelRecordsHandler.createRecordEntry(orderCancelRequest, requestor);
            OrderCancelState currentCancelState = this.stateMappingStrategy.getOrderCancelState(orderCancelRequest.getOrder());
            processCancelRequestForGateway(orderCancelRequest, result);
        }
        else
        {
            throw new OrderCancelDeniedException(orderCancelRequest.getOrder().getCode(), cancelDecision);
        }
        return result;
    }

    private void processCancelRequestForGateway(OrderCancelRequest orderCancelRequest, OrderCancelRecordEntryModel cancelRequestRecordEntry)
            throws V2Exception, OrderCancelException, CalculationException {

        getCancelRequestExecutor().sendOrderCancelRequestInitiationMail(orderCancelRequest, cancelRequestRecordEntry);
        OrderModel orderPreview = getRefundService().createOrderPreviewAsPerCancellationRequest(orderCancelRequest);
        cancelRequestRecordEntry = getPaymentServiceAdapter().recalculateOrderAndModifyPayments(orderCancelRequest,
                orderPreview, cancelRequestRecordEntry);

        OrderModel order = orderCancelRequest.getOrder();
        getCancelRequestExecutor().modifyOrderAccrodingToRequest(orderCancelRequest);
        getModelService().refresh(order);

        if (!(OrderUtils.hasLivingEntries(order))) {
            if (getCompleteCancelStatusChangeStrategy() != null) {
                this.completeCancelStatusChangeStrategy.changeOrderStatusAfterCancelOperation(cancelRequestRecordEntry,
                        true);
            }
        } else if (getPartialCancelStatusChangeStrategy() != null) {
            getPartialCancelStatusChangeStrategy()
                    .changeOrderStatusAfterCancelOperation(cancelRequestRecordEntry, true);
        }

        final String previousOrderState = order.getStatus().getCode();

        if (!(previousOrderState.equals(OrderStatus.PENDING.getCode()))) {
            if (this.paymentServiceAdapter != null) {
                // TODO
                if (cancelRequestRecordEntry.getModificationRecord() != null) {
                    // this.sendOrderCancleDataToSAP(cancelRequestRecordEntry, orderCancelRequest,orderPreview);
                    try {

                        OrderModel orderModel = orderPreview.getOriginalVersion();

                        if (PAYU_CC.equals(orderModel.getPaymentInfo().getPaymentMode().getMode())) {
                            String mihPayId = orderModel.getPaymentTransactions().get(0).getEntries().get(0).getRequestId();
                            if (mihPayId == null) {
                                throw new V2Exception("Failed to retrieve mihPayId id for this order");
                            }
                            getPayuPaymentService().refundOrCancelPayment(mihPayId, cancelRequestRecordEntry
                                    .getOrderModificationRefundInfo().getAmountTobeRefunded().doubleValue());

                            final PaymentTransactionModel transaction = createPaymentTransaction(orderModel, PAYU);
                            createTransactionEntry(transaction,
                                    cancelRequestRecordEntry
                                            .getOrderModificationRefundInfo().getAmountTobeRefunded(),
                                    Currency.getInstance(orderModel.getCurrency().getIsocode()),
                                    mihPayId,
                                    PaymentTransactionType.CANCEL);
                        } else if (EBS_DC.equals(orderModel.getPaymentInfo().getPaymentMode().getMode())) {

                            EBSActionResponse response = getEbsPaymentFacade().initiateCancelRequest(orderPreview,
                                    String.valueOf(cancelRequestRecordEntry
                                            .getOrderModificationRefundInfo().getAmountTobeRefunded()));

                            final PaymentTransactionModel transaction = createPaymentTransaction(orderModel, EBS);

                            if (CANCELLED.equalsIgnoreCase(response.getTransactionType())) {
                                createTransactionEntry(transaction,
                                        BigDecimal.valueOf(Double.parseDouble(response.getAmount())),
                                        Currency.getInstance(orderModel.getCurrency().getIsocode()),
                                        response.getPaymentId(),
                                        PaymentTransactionType.CANCEL);
                            }

                        }
                        orderCancelRequest.getOrder().setCalculated(Boolean.FALSE);
                        getModelService().save(orderCancelRequest.getOrder());
                        // Recalculate Promotion details
                        getRefundService().recalculatePromotionsForPreview(orderCancelRequest.getOrder());
                        getCalculationService().recalculate(order);
                        getModelService().refresh(order);
                    } catch (CalculationException e) {
                        LOGGER.error("Error recalculating new order", e);
                    }
                    this.orderCancelRecordsHandler.updateRecordEntry(getCancelRequestExecutor().makeInternalResponse(orderCancelRequest,
                            true,
                            null));
                }
            } else if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Missing OrderCancelPaymentServiceAdapter!");
            }
        }

        getCancelRequestExecutor().sendOrderCancelCompletionMail(orderCancelRequest, cancelRequestRecordEntry, order);

    }

    private PaymentTransactionEntryModel createTransactionEntry(PaymentTransactionModel transaction, BigDecimal amountTobeRefunded,
            Currency currency,
            String mihPayId, PaymentTransactionType tnxType) {
        final PaymentTransactionEntryModel entry = getModelService()
                .create(PaymentTransactionEntryModel.class);
        // final String newEntryCode = getNewEntryCode(transaction);
        // entry.setRequestId(storeCreditRequestId);
        entry.setAmount(amountTobeRefunded);
        if (currency != null) {
            entry.setCurrency(getCommonI18NService().getCurrency(currency.getCurrencyCode()));
        }
        entry.setPaymentTransaction(transaction);
        entry.setTransactionStatus(TransactionStatus.ACCEPTED.toString());
        entry.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.toString());
        entry.setType(tnxType);
        entry.setCode("Code");// TODO
        entry.setTime(new Date());
        entry.setRequestId(mihPayId);
        getModelService().save(entry);
        return entry;

    }

    private PaymentTransactionModel createPaymentTransaction(OrderModel originalOrder, String paymentProvider) {
        final PaymentTransactionModel transaction = getModelService().create(PaymentTransactionModel.class);
        transaction.setCode(paymentProvider + originalOrder.getUser().getUid() + "-" + UUID.randomUUID());
        this.getModelService().save(transaction);
        transaction.setOrder(originalOrder);
        transaction.setPaymentProvider(paymentProvider);
        this.getModelService().save(transaction);
        return transaction;
    }

    @Override
    public TypedObject requestPartialCancellation() throws OrderCancelException, ValidationException {
        TypedObject orderCancelRecord = null;
        if (orderCancelRequestOVC != null && orderCancelEntryOVCs != null || !orderCancelEntryOVCs.isEmpty()) {
            try {
                orderCancelRecord = super.createPartialOrderCancellationRequest(orderCancelEntryOVCs, orderCancelRequestOVC);
            } finally {
                cleanUp();
            }
        }
        return orderCancelRecord;
    }

    @Override
    public void cleanUp() {
        orderCancelEntryOVCs = null;
        orderCancelRequestOVC = null;
        orderCancelRequest = null;
    }

    protected OrderModel getOrderModel() {
        final TypedObject order = getOrder();
        OrderModel orderModel = null;
        if (order != null && order.getObject() instanceof OrderModel) {
            orderModel = (OrderModel) order.getObject();
        }
        return orderModel;
    }

    /**
     * Gets the cancel entries.
     * 
     * @param orderEntryCancelRecords
     *        the order entry cancel records
     * @param cancelableOrderEntries
     *        the cancelable order entries
     * @return the cancel entries
     */
    protected Map<TypedObject, CancelEntryDetails> getCancelEntries(final List<ObjectValueContainer> orderEntryCancelRecords,
            final Map<TypedObject, Long> cancelableOrderEntries) {
        final Map<TypedObject, CancelEntryDetails> cancelEntries = new HashMap<>();
        for (final ObjectValueContainer ovc : orderEntryCancelRecords) {
            final TypedObject orderEntry = (TypedObject) ovc.getObject();
            if (cancelableOrderEntries.containsKey(orderEntry)) {
                final long cancelRequestQuantity = SafeUnbox.toLong((Long) getPropertyValue(ovc, "OrderCancelEntry.cancelQuantity")
                        .getCurrentValue());
                final CancelReason reason = (CancelReason) getPropertyValue(ovc, "OrderCancelEntry.cancelReason").getCurrentValue();
                final String notes = (String) getPropertyValue(ovc, "OrderCancelEntry.notes").getCurrentValue();
                if (cancelRequestQuantity > 0L && cancelRequestQuantity <= SafeUnbox.toLong(cancelableOrderEntries.get(orderEntry))) {
                    final CancelEntryDetails cancelEntry = new CancelEntryDetails(orderEntry, cancelRequestQuantity, reason, notes);
                    cancelEntries.put(orderEntry, cancelEntry);
                }
            }
        }
        return cancelEntries;
    }

    @Override
    protected boolean validateCreateCancellationRequest(OrderModel orderModel, ObjectValueContainer cancelRequest)
            throws ValidationException {

        boolean result = false;
        List<ResourceMessage> errorMessages = new ArrayList<>();

        CancelReason reason = (CancelReason) getPropertyValue(cancelRequest, "OrderCancelRequest.cancelReason")
                .getCurrentValue();
        if (reason == null)
        {
            errorMessages.add(new ResourceMessage("cancelRecordEntry.validation.cancelRequestReason.missingReason"));
        }

        ObjectValueHolder objectValueHolder = getPropertyValue(cancelRequest, "V2OrderCancelRequest.returnToGateway");
        if (objectValueHolder.getLocalValue() == Boolean.TRUE) {
            result = true;
        }

        ObjectValueHolder valueHolder = getPropertyValue(cancelRequest, "V2OrderCancelRequest.returnToWallet");
        if (valueHolder.getLocalValue() == Boolean.TRUE) {
            result = true;
        }
        if (result == false) {
            errorMessages.add(new ResourceMessage("Select atleast one returnTo option (Wallet or Gateway)"));
        }

        if (!(errorMessages.isEmpty()))
        {
            throw new ValidationException(errorMessages);
        }

        return true;
    }

    @Override
    protected OrderCancelRequest buildCancelRequest(OrderModel order, Map<TypedObject, CancelEntryDetails> entriesToCancel,
            ObjectValueContainer cancelRequest) {
        // TODO Auto-generated method stub
        // return super.buildCancelRequest(order, entriesToCancel, cancelRequest);

        String notes = (String) getPropertyValue(cancelRequest, "OrderCancelRequest.notes").getCurrentValue();
        CancelReason reason = (CancelReason) getPropertyValue(cancelRequest, "OrderCancelRequest.cancelReason")
                .getCurrentValue();

        if ((order != null) && (entriesToCancel != null) && (!(entriesToCancel.isEmpty())))
        {
            List orderCancelEntries = new ArrayList();

            for (TypedObject orderEntry : entriesToCancel.keySet())
            {
                if (!(orderEntry.getObject() instanceof OrderEntryModel))
                    continue;
                OrderEntryModel entryModel = (OrderEntryModel) orderEntry.getObject();
                CancelEntryDetails cancelEntry = (CancelEntryDetails) entriesToCancel.get(orderEntry);

                OrderCancelEntry orderCancelEntry = new OrderCancelEntry(entryModel, cancelEntry.getQuantity(),
                        cancelEntry.getNotes(), cancelEntry.getReason());
                orderCancelEntries.add(orderCancelEntry);

            }

            //Boolean returnToWallet = (Boolean) getPropertyValue(cancelRequest, "V2OrderCancelRequest.returnToWallet").getCurrentValue();
            //Boolean returnToGateway = (Boolean) getPropertyValue(cancelRequest, "V2OrderCancelRequest.returnToGateway").getCurrentValue();

            V2OrderCancelRequest v2OrderCancelRequest = new V2OrderCancelRequest(order, orderCancelEntries);
            v2OrderCancelRequest.setCancelReason(reason);
            v2OrderCancelRequest.setNotes(notes);
            //v2OrderCancelRequest.setReturnToGateway(returnToGateway);
            //v2OrderCancelRequest.setReturnToWallet(returnToWallet);
            return (OrderCancelRequest) v2OrderCancelRequest;

        }
        return null;
    }

    /**
     * Gets the refund service.
     * 
     * @return the refund service
     */
    protected V2RefundService getRefundService() {
        return refundService;
    }

    protected ObjectValueContainer getOrderCancelRequestOVC() {
        return orderCancelRequestOVC;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.cockpits.cscockpit.widgets.controllers.impl.V2CancellationController#getOrderCancelEntryOVCs()
     */
    protected List<ObjectValueContainer> getOrderCancelEntryOVCs() {
        return orderCancelEntryOVCs;
    }

    /**
     * Gets the refund amount service.
     * 
     * @return the refund amount service
     */
    protected V2RefundAmountService getRefundAmountService() {
        return refundAmountService;
    }

    /**
     * @return the cancelRequestExecutor
     */
    public V2ImmediateCancelRequestExecutor getCancelRequestExecutor() {
        return cancelRequestExecutor;
    }

    /**
     * @param cancelRequestExecutor
     *        the cancelRequestExecutor to set
     */
    public void setCancelRequestExecutor(V2ImmediateCancelRequestExecutor cancelRequestExecutor) {
        this.cancelRequestExecutor = cancelRequestExecutor;
    }

    /**
     * @return the ebsPaymentFacade
     */
    public EBSPaymentFacade getEbsPaymentFacade() {
        return ebsPaymentFacade;
    }

    /**
     * @param ebsPaymentFacade
     *        the ebsPaymentFacade to set
     */
    public void setEbsPaymentFacade(EBSPaymentFacade ebsPaymentFacade) {
        this.ebsPaymentFacade = ebsPaymentFacade;
    }

    /**
     * @return the payuPaymentService
     */
    public PAYUPaymentService getPayuPaymentService() {
        return payuPaymentService;
    }

    /**
     * @param payuPaymentService
     *        the payuPaymentService to set
     */
    public void setPayuPaymentService(PAYUPaymentService payuPaymentService) {
        this.payuPaymentService = payuPaymentService;
    }

}
