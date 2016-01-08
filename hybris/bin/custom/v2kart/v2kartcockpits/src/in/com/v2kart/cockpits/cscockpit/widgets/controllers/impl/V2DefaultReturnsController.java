package in.com.v2kart.cockpits.cscockpit.widgets.controllers.impl;

import in.com.v2kart.core.process.V2RefundOrderProcessor;
import in.com.v2kart.core.services.V2CalculationService;
import in.com.v2kart.core.services.V2RefundService;
import in.com.v2kart.core.services.V2ReturnService;
import in.com.v2kart.fulfilmentprocess.model.V2OrderModificationRefundInfoModel;
import in.com.v2kart.sapinboundintegration.services.V2SapInboundSaleOrderIntegrationService;
import in.com.v2kart.sapinboundintegration.ws.orderreturn.SOReturnRes;
import in.com.v2kart.v2kartebspaymentintegration.facades.EBSPaymentFacade;
import in.com.v2kart.v2kartpayupaymentintegration.services.PAYUPaymentService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.acceleratorservices.orderprocessing.model.OrderModificationProcessModel;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.services.values.ObjectValueContainer;
import de.hybris.platform.cockpit.services.values.ObjectValueContainer.ObjectValueHolder;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cscockpit.exceptions.ResourceMessage;
import de.hybris.platform.cscockpit.exceptions.ValidationException;
import de.hybris.platform.cscockpit.utils.SafeUnbox;
import de.hybris.platform.cscockpit.widgets.controllers.impl.DefaultReturnsController;
import de.hybris.platform.ordercancel.OrderUtils;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.returns.OrderReturnRecordsHandlerException;
import de.hybris.platform.returns.model.OrderReturnRecordEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

/**
 * Class <Code>V2DefaultReturnsController</Code> contains methods related to Returns Controller. It also contains API that is responsible
 * for creating refund request.
 * 
 * @author Nagarro_Devraj802
 * @since 1.2
 * 
 */
public class V2DefaultReturnsController extends DefaultReturnsController {

    /** Application log file. */
    private static final Logger LOG = Logger.getLogger(V2DefaultReturnsController.class);

    private static final String SUCCESS = "SUCCESS";

    private static final Object TRUE = "true";

    private static final String REFUND_TO_GATEWAY = "gateway";

    private static final Object PAYU_CC = "PAYUCC";

    private static final Object EBS_DC = "EBSDC";

    private static final String EBS = "EBS";

    private static final String PAYU = "PAYU";

    /** RefundOrderProcessor bean injection */
    private V2RefundOrderProcessor v2RefundOrderProcessor;

    private V2ReturnService v2ReturnService;

    private V2RefundService v2RefundService;

    private BusinessProcessService businessProcessService;

    private V2CalculationService calculationService;

    public V2CalculationService getCalculationService() {
        return calculationService;
    }

    public void setCalculationService(V2CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    private EBSPaymentFacade ebsPaymentFacade;

    private PAYUPaymentService payuPaymentService;

    private CommonI18NService commonI18NService;

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public PAYUPaymentService getPayuPaymentService() {
        return payuPaymentService;
    }

    public void setPayuPaymentService(PAYUPaymentService payuPaymentService) {
        this.payuPaymentService = payuPaymentService;
    }

    public BusinessProcessService getBusinessProcessService() {
        return businessProcessService;
    }

    public void setBusinessProcessService(BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }

    public V2RefundService getV2RefundService() {
        return v2RefundService;
    }

    public V2RefundOrderProcessor getV2RefundOrderProcessor() {
        return v2RefundOrderProcessor;
    }

    public void setV2RefundOrderProcessor(V2RefundOrderProcessor v2RefundOrderProcessor) {
        this.v2RefundOrderProcessor = v2RefundOrderProcessor;
    }

    public void setV2RefundService(V2RefundService v2RefundService) {
        this.v2RefundService = v2RefundService;
    }

    public V2ReturnService getV2ReturnService() {
        return v2ReturnService;
    }

    @Autowired
    private V2SapInboundSaleOrderIntegrationService v2SapInboundSaleOrderIntegrationService;

    public void setV2ReturnService(V2ReturnService v2ReturnService) {
        this.v2ReturnService = v2ReturnService;
    }

    /** {@inheritDoc} */
    @Override
    public TypedObject createRefundRequest() {
        ReturnRequestModel refundRequest = null;
        try {
            OrderModel orderModel = getOrderModel();
            refundRequest = getReturnService().createReturnRequest(orderModel);
            String rmaString = getReturnService().createRMA(refundRequest);
            refundRequest.setRMA(rmaString);
            V2OrderModificationRefundInfoModel refundInfoModel = getV2RefundOrderProcessor().calculateRefundInfoForReturn(
                    refundOrderPreview, true);
            /*
             * if (refundInfoModel.getRefundToWallet() == true) { getV2RefundOrderProcessor().process(refundOrderPreview,
             * refundInfoModel.getAmountTobeRefunded()); } else if (refundInfoModel.getRefundToPaymentGateway() == true) {
             * 
             * if (PAYU_CC.equals(orderModel.getPaymentInfo().getPaymentMode().getMode ())) { String mihPayId =
             * orderModel.getPaymentTransactions().get(0 ).getEntries().get(0).getRequestId(); if (mihPayId == null) { throw new
             * Exception("Refund Exception : Failed to fetch mihPayId id for order " + orderModel.getCode()); } if
             * (getPayuPaymentService().refundOrCancelPayment(mihPayId, refundInfoModel.getAmountTobeRefunded().doubleValue())) { final
             * PaymentTransactionModel transaction = createPaymentTransaction(refundOrderPreview.getOriginalVersion(), PAYU);
             * createTransactionEntry(transaction, refundInfoModel.getAmountTobeRefunded(), Currency.getInstance(refundOrderPreview
             * .getOriginalVersion().getCurrency().getIsocode()), mihPayId, PaymentTransactionType.REFUND_FOLLOW_ON); } } else if
             * (EBS_DC.equals (orderModel.getPaymentInfo().getPaymentMode().getMode())) {
             * 
             * EBSActionRequest request = ebsPaymentFacade.processRefund(refundOrderPreview, refundInfoModel); final PaymentTransactionModel
             * transaction = createPaymentTransaction(refundOrderPreview.getOriginalVersion(), EBS); createTransactionEntry(transaction,
             * refundInfoModel.getAmountTobeRefunded(), Currency.getInstance(refundOrderPreview
             * .getOriginalVersion().getCurrency().getIsocode()), request.getPaymentId(), PaymentTransactionType.REFUND_FOLLOW_ON); } }
             */
            boolean success = false;
            applyRefunds(orderModel, refundRequest, this.refundDetailsList, true);
            SOReturnRes soReturnRes = v2SapInboundSaleOrderIntegrationService.returnErpSales(refundRequest);
            for (final SOReturnRes.OrderReturnRes orderModRes : soReturnRes.getOrderReturnRes()) {
                if (StringUtils.isNotEmpty(orderModRes.getRespCode())) {
                    LOG.info("Order Return SAP Response is [" + orderModRes.getRespCode() + "] and msg : " + orderModRes.getRespMsg());
                    if ("S".equalsIgnoreCase(orderModRes.getRespCode())) {
                        success = true;
                    }
                }
            }
            
            if (success) {
                OrderReturnRecordEntryModel returnRecordEntry = ((V2RefundService) getV2RefundService())
                        .getOrderReturnRecordEntry(refundOrderPreview, refundRequest);
                getV2RefundService().applyRefunds(refundOrderPreview, refundRequest);
                // refundOrderProcessor.process(refundOrderPreview);
                OrderModificationProcessModel orderModificationProcessModel = null;
                if (!(OrderUtils.hasLivingEntries(orderModel))) {
                    orderModificationProcessModel = getBusinessProcessService()
                            .createProcess(
                                    "sendOrderRefundEmailProcess-" + returnRecordEntry.getCode() + "-"
                                            + System.currentTimeMillis(), "sendOrderRefundEmailProcess");
                } else {
                    orderModificationProcessModel = getBusinessProcessService().createProcess(
                            "sendOrderPartiallyRefundedEmailProcess-" + returnRecordEntry.getCode() + "-"
                                    + System.currentTimeMillis(), "sendOrderPartiallyRefundedEmailProcess");
                }
                orderModificationProcessModel.setOrder(orderModel);
                orderModificationProcessModel.setOrderModificationRecordEntry(returnRecordEntry);
                getModelService().save(orderModificationProcessModel);
                getBusinessProcessService().startProcess(orderModificationProcessModel);
            } else {
                OrderModificationProcessModel orderModificationProcessModel = null;
                if (!(OrderUtils.hasLivingEntries(orderModel))) {
                    orderModificationProcessModel = getBusinessProcessService()
                            .createProcess(
                                    "sendOrderRefundEmailProcess-" + refundRequest.getCode() + "-"
                                            + System.currentTimeMillis(), "sendOrderRefundEmailProcess");
                } else {
                    orderModificationProcessModel = getBusinessProcessService().createProcess(
                            "sendOrderPartiallyRefundedEmailProcess-" + refundRequest.getCode() + "-"
                                    + System.currentTimeMillis(), "sendOrderPartiallyRefundedEmailProcess");
                }
                orderModificationProcessModel.setOrder(orderModel);
                orderModificationProcessModel.setEndMessage("FAILURE TO SEND SAP");
                orderModificationProcessModel.setState(ProcessState.ERROR);
                getModelService().save(orderModificationProcessModel);
                getBusinessProcessService().startProcess(orderModificationProcessModel);
            }
            TypedObject refundRequestObject = getCockpitTypeService().wrapItem(refundRequest);
            this.refundDetailsList.clear();
            this.refundDetailsList = null;
            deleteRefundOrderPreview();
            return refundRequestObject;
        } catch (OrderReturnRecordsHandlerException e) {
            LOG.info("Failed to create refundRecordEntry for " + refundRequest.getCode() + ".", e);
        } catch (Exception exception) {
            LOG.error("Failed to create refund request", exception);
        }
        return null;
    }

    private PaymentTransactionEntryModel createTransactionEntry(PaymentTransactionModel transaction, BigDecimal amountTobeRefunded,
            Currency currency,
            String paymentId, PaymentTransactionType tnxType) {
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
        entry.setRequestId(paymentId);
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
    protected OrderModel getOrderModel() {
        OrderModel order = super.getOrderModel();
        return getCalculationService().recalculatePromotionsForOriginalOrder(order);
    }

    @Override
    public TypedObject createRefundOrderPreview(List<ObjectValueContainer> refundEntriesValueContainers) {
        try
        {
            this.refundDetailsList = new HashMap();

            Map returnableOrderEntries = getReturnableOrderEntries();
            for (ObjectValueContainer ovc : refundEntriesValueContainers)
            {
                TypedObject orderEntry = (TypedObject) ovc.getObject();
                if (!(returnableOrderEntries.containsKey(orderEntry)))
                    continue;
                long expectedQty = SafeUnbox.toLong((Long) getPropertyValue(ovc, "ReturnEntry.expectedQuantity")
                        .getCurrentValue());
                RefundReason reason = (RefundReason) getPropertyValue(ovc, "RefundEntry.reason").getCurrentValue();
                ReturnAction action = (ReturnAction) getPropertyValue(ovc, "ReturnEntry.action").getCurrentValue();
                String notes = (String) getPropertyValue(ovc, "ReturnEntry.notes").getCurrentValue();
                if ((expectedQty <= 0L) || (expectedQty > SafeUnbox.toLong((Long) returnableOrderEntries.get(orderEntry)))
                        || (reason == null) ||
                        (action == null))
                    continue;
                this.refundDetailsList.put(
                        Long.valueOf(SafeUnbox.toInt(((AbstractOrderEntryModel) orderEntry.getObject()).getEntryNumber())),
                        new RefundDetails(expectedQty, reason, action, notes));

            }

            OrderModel orderModel = getOrderModel();

            this.refundOrderPreview = getRefundService().createRefundOrderPreview(orderModel);
            fixOrderEntryNumbers(orderModel, this.refundOrderPreview);
            ReturnRequestModel refundRequest = getReturnService().createReturnRequest(this.refundOrderPreview);

            for (ObjectValueContainer objectValueContainer : refundEntriesValueContainers) {

                for (ObjectValueHolder objectValueHolder : objectValueContainer.getAllValues()) {
                    if (objectValueHolder.getPropertyDescriptor().getQualifier().equalsIgnoreCase("ReturnEntry.returnToGateway")) {
                        if (objectValueHolder.getLocalValue() != null) {
                            if (objectValueHolder.getLocalValue().toString().equals(TRUE)) {
                                refundRequest.setReturnMethod(REFUND_TO_GATEWAY);
                                getModelService().save(refundRequest);
                                break;
                            }
                        }
                    }
                }
            }

            applyRefunds(this.refundOrderPreview, refundRequest, this.refundDetailsList, false);

            return getCockpitTypeService().wrapItem(this.refundOrderPreview);
        } catch (Exception e)
        {
            LOG.error("failed to create refund order", e);

            if (this.refundDetailsList != null)
            {
                this.refundDetailsList.clear();
                this.refundDetailsList = null;
            }

            deleteRefundOrderPreview();
        }

        // return null;

        return getCockpitTypeService().wrapItem(getV2RefundService().recalculatePromotionsForPreview(refundOrderPreview));

    }

    @Override
    public boolean validateCreateRefundRequest(List<ObjectValueContainer> refundEntriesValueContainers) throws ValidationException {
        // TODO Auto-generated method stub

        boolean result = false;
        List<ResourceMessage> errorMessages = new ArrayList<>();
        Map<TypedObject, Long> returnableOrderEntries = getReturnableOrderEntries();
        long okCount = 0L;
        for (ObjectValueContainer ovc : refundEntriesValueContainers)
        {
            List<ResourceMessage> entryErrorMessages = new ArrayList<>();
            boolean entryProcessed = false;
            TypedObject orderEntry = (TypedObject) ovc.getObject();
            int entryNumber = SafeUnbox.toInt(((AbstractOrderEntryModel) orderEntry.getObject()).getEntryNumber());
            if (returnableOrderEntries.containsKey(orderEntry))
            {
                ObjectValueContainer.ObjectValueHolder expectedQty = getPropertyValue(ovc, "ReturnEntry.expectedQuantity");
                if ((expectedQty != null) && (expectedQty.getCurrentValue() instanceof Long))
                {
                    long expectedQtyValue = SafeUnbox.toLong((Long) expectedQty.getCurrentValue());
                    if (expectedQtyValue != 0L)
                    {
                        if (expectedQtyValue < 0L)
                        {
                            entryErrorMessages.add(
                                    new ResourceMessage("returnEntry.validation.expectedQuantity.negative", Arrays
                                            .asList(new Integer[] { Integer.valueOf(entryNumber) })));
                        }
                        if ((expectedQtyValue > 0L) && (SafeUnbox.toLong((Long) returnableOrderEntries.get(orderEntry)) < expectedQtyValue))
                        {
                            entryErrorMessages.add(
                                    new ResourceMessage("returnEntry.validation.expectedQuantity.gtMaxQty", Arrays
                                            .asList(new Integer[] { Integer.valueOf(entryNumber) })));
                        }
                        ObjectValueContainer.ObjectValueHolder reason = getPropertyValue(ovc, "RefundEntry.reason");
                        if ((reason == null) || (reason.getCurrentValue() == null))
                        {
                            entryErrorMessages.add(new ResourceMessage("returnEntry.validation.reason.missing", Arrays
                                    .asList(new Integer[] {
                                            Integer.valueOf(entryNumber) })));
                        }
                        ObjectValueContainer.ObjectValueHolder action = getPropertyValue(ovc, "ReturnEntry.action");
                        if ((action == null) || (action.getCurrentValue() == null))
                        {
                            entryErrorMessages.add(new ResourceMessage("returnEntry.validation.action.missing", Arrays
                                    .asList(new Integer[] {
                                            Integer.valueOf(entryNumber) })));
                        }
                        entryProcessed = true;
                    }
                }

            }
            else
            {
                entryErrorMessages.add(new ResourceMessage("returnEntry.validation.product.notReturnable", Arrays.asList(new Integer[] {
                        Integer.valueOf(entryNumber) })));
            }
            if (!(entryErrorMessages.isEmpty()))
            {
                errorMessages.addAll(entryErrorMessages);
            }
            if ((!(entryProcessed)) || (!(entryErrorMessages.isEmpty())))
                continue;
            okCount += 1L;
        }
        if ((okCount == 0L) && (errorMessages.isEmpty()))
        {
            errorMessages.add(new ResourceMessage("refundRequest.validation.noneSelected"));
        }
        if (okCount > 0L) {
            for (ObjectValueContainer container : refundEntriesValueContainers) {
                ObjectValueHolder objectValueHolder = getPropertyValue(container, "ReturnEntry.returnToGateway");
                if (objectValueHolder.getLocalValue() == Boolean.TRUE) {
                    result = true;

                }
                ObjectValueHolder valueHolder = getPropertyValue(container, "ReturnEntry.returnToWallet");
                if (valueHolder.getLocalValue() == Boolean.TRUE) {
                    result = true;
                }
            }
            if (result == false) {
                errorMessages.add(
                        new ResourceMessage("Select atleast one returnTo option (Wallet or Gateway)"));
            }
        }
        if (!(errorMessages.isEmpty()))
        {
            throw new ValidationException(errorMessages);
        }
        return result;
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
}
