package in.com.v2kart.sapoutboundintegration.services.Impl;

import static org.springframework.util.Assert.notNull;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;

import in.com.v2kart.core.dao.V2RefundEntryDao;
import in.com.v2kart.core.dao.V2ReturnRequestDao;
import in.com.v2kart.core.payment.services.V2PaymentService;
import in.com.v2kart.core.sms.V2SmsService;
import in.com.v2kart.core.sms.populator.V2UserSmsDataMapPopulator;
import in.com.v2kart.sapinboundintegration.order.daos.V2AcceleratorConsignmentDao;
import in.com.v2kart.sapinboundintegration.order.daos.V2OrderDao;
import in.com.v2kart.sapinboundintegration.response.data.FeedStatusCode;
import in.com.v2kart.sapoutboundintegration.services.V2ConsignmentUpdateService;
import in.com.v2kart.sapoutboundintegration.services.V2SapOrderUpdateIntegrationService;
import in.com.v2kart.sapoutboundintegration.ws.order.returnupdate.RSoOrderStatusUpdateReq;
import in.com.v2kart.sapoutboundintegration.ws.order.returnupdate.RSoOrderStatusUpdateRes;
import in.com.v2kart.sapoutboundintegration.ws.order.update.ObjectFactory;
import in.com.v2kart.sapoutboundintegration.ws.order.update.SoOrderStatusUpdateReq;
import in.com.v2kart.sapoutboundintegration.ws.order.update.SoOrderStatusUpdateRes;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

/**
 * Implements the {@link V2SapOrderUpdateIntegrationService}
 * 
 * @author satvir_nagarro
 * 
 */
public class V2SapOrderUpdateIntegrationServiceImpl implements V2SapOrderUpdateIntegrationService {

    /** Logger instance for this class */
    private final Logger LOG = LoggerFactory.getLogger(V2SapOrderUpdateIntegrationServiceImpl.class);

    /** ModelService bean injection */
    private ModelService modelService;
    @Autowired
    private V2RefundEntryDao v2RefundEntryDao;

    /** V2ConsignmentUpdateService bean injection */
    private V2ConsignmentUpdateService v2ConsignmentUpdateService;

    /** Constants used for StoreCredit */
    public final static String STORE_CREDIT = "StoreCredit";

    /** V2OrderDao bean injection */
    private V2OrderDao v2OrderDao;

    @Autowired
    private V2ReturnRequestDao v2ReturnRequestDao;

    /** V2AcceleratorConsignmentDao bean injection */
    private V2AcceleratorConsignmentDao v2AcceleratorConsignmentDao;
    @Autowired
    private V2PaymentService v2PaymentService;

    /** V2SmsSservice bean injection */

    private V2SmsService v2SmsService;
    /** V2UserSmsDataMapPopulator bean injection */

    private V2UserSmsDataMapPopulator v2UserSmsDataMapPopulator;
    /** businessProcessService bean injection */

    private BusinessProcessService businessProcessService;

    private final ObjectFactory objectFactory = new ObjectFactory();

    @Override
    public SoOrderStatusUpdateRes updateOrderStatusFromErp(final SoOrderStatusUpdateReq orderStatusUpdate) {
        notNull(orderStatusUpdate, "orderStatusUpdate may not be null!");
        SoOrderStatusUpdateRes response = null;
        final String orderCode = orderStatusUpdate.getSONo();
        final String consignmentCode = orderStatusUpdate.getDeliveryNo();
        OrderModel order = null;
        OrderStatus orderStatus = OrderStatus.READY_TO_SHIP;
        try {
            try {
                order = v2OrderDao.findOrderByCode(orderCode);
            } catch (final ModelNotFoundException mnE) {
                LOG.error(String.format("No Order exits for order status update for order code [%s]", orderCode), mnE);
                response = this.createResponse(FeedStatusCode.ERROR.toString(),
                        String.format("No Order exits for order status update for order code [%s]", orderCode));
                return response;
            }

            ConsignmentModel consignment = null;
            try {
                consignment = v2AcceleratorConsignmentDao.findConsignmentByCodeAndOrderCode(consignmentCode, orderCode);
            } catch (final ModelNotFoundException e) {
                LOG.info("No delivery for code [{}] exits for order [{}] in the system", consignmentCode, orderCode);
                if (!order.getConsignments().isEmpty()) {
                    LOG.error(String.format("Already contains one Delivery/Consignment for order status update for order code [%s]",
                            orderCode), e);
                    response = this.createResponse(FeedStatusCode.ERROR.toString(), String.format(
                            "Already contains one Delivery/Consignment for order status update for order code [%s]", orderCode));
                    return response;
                }
            }
            ConsignmentStatus consignmentStatus = null;
            if (consignment == null) { // if new delivery (Consignment) created in SAP
                consignment = v2ConsignmentUpdateService.createConsignment(order, orderStatusUpdate);
                consignmentStatus = consignment.getStatus();
                LOG.info("Delivery for delivery code [{}] created successfully for order [{}]", consignmentCode, orderCode);
                response = this.createResponse(FeedStatusCode.SUCCESS.toString(),
                        String.format("Delivery for delivery code [{}] created successfully for order [{}]", consignmentCode, orderCode));
                LOG.info("Delivery for delivery code [{}] created successfully for order [{}]", consignmentCode, orderCode);
            } else {
                // TODO
                if (!ConsignmentStatus.DISPATCHED.toString().equals(orderStatusUpdate.getDeliveryStatus())) {
                    response = this
                            .createResponse(
                                    FeedStatusCode.ERROR.toString(),
                                    String.format(
                                            "Delivery for delivery code [%s] already there for order [%s], and this request must contains Status as DISPATCHED only",
                                            consignmentCode, orderCode));
                    LOG.info(
                            "Delivery for delivery code [{}] already there for order [{}], and this request must contains Status as DISPATCHED only",
                            consignmentCode, orderCode);
                    return response;
                }
                orderStatus = OrderStatus.DISPATCHED;
                consignmentStatus = v2ConsignmentUpdateService.updateConsignment(order, consignment, orderStatusUpdate);
                response = this.createResponse(
                        FeedStatusCode.SUCCESS.toString(),
                        String.format("Delivery status [%s] for delivery code [%s] updated successfully for order [%s]",
                                consignmentStatus.getCode(), consignmentCode, orderCode));
                LOG.info("Delivery status [{}] for delivery code [{}] updated successfully for order [{}]", consignmentStatus.getCode(),
                        consignmentCode, orderCode);
            }
            // Commit stock here for this consignment/Delivery When Invoicing is done and its status is READY_TO_DISPATCH
            if (ConsignmentStatus.READY_TO_DISPATCH.equals(consignmentStatus)) {
                // trigger smss and email from here
                triggerDeliverySentNotificationToCustomer(consignment);
                v2ConsignmentUpdateService.commitStock(consignment);
                LOG.info("Stock for Delivery code [{}] committed permanently for order [{}]", consignmentCode, orderCode);
            }

            // update order final Status here based on all consignment Statuses.
            this.setOrderStatus(order, orderStatus);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Order Status [{}] updated successfully for order [{}]", order.getStatus(), orderCode);
            }
        } catch (final Exception e) {
            LOG.error(String.format("Error in proccessing order status update for order [%s]", orderCode), e);
            response = this.createResponse(FeedStatusCode.ERROR.toString(),
                    String.format("Error in proccessing order status update for order [%s]", orderCode));
        }
        return response;
    }

    @Override
    public RSoOrderStatusUpdateRes updateReturnOrderStatusFromErp(final RSoOrderStatusUpdateReq orderStatusUpdate) {
        notNull(orderStatusUpdate, "orderStatusUpdate may not be null!");
        final RSoOrderStatusUpdateRes response = new RSoOrderStatusUpdateRes();
        final String returnRMACode = orderStatusUpdate.getRSONo();
        final ReturnRequestModel returnRequest = v2ReturnRequestDao.getReturnRequestFromRMA(returnRMACode);
        final List<RefundEntryModel> refundEntryList = v2RefundEntryDao.getRefundEntryList(returnRequest);

        for (final RefundEntryModel refundEntry : refundEntryList) {
            final ReturnStatus returnStatus = ReturnStatus.valueOf(orderStatusUpdate.getRequestStatus());
            refundEntry.setStatus(returnStatus);
            modelService.save(refundEntry);
        }
        OrderModel order = null;
        // final OrderStatus orderStatus = OrderStatus.READY_TO_SHIP;
        try {
            order = returnRequest.getOrder();
        } catch (final ModelNotFoundException mnE) {
            LOG.error(String.format("No Order exits for order status update for order code [%s]"));
            response.setRespCode("");
            response.setRespMsg(String.format("No Order exits for order status update for order code"));
            return response;
        }

        // payment transaction
        final OrderModel originalOrder = order;
        final PaymentTransactionModel transaction = createPaymentTransactionForStoreCredit(originalOrder);
        // Debugging...

        final BigDecimal amount = new BigDecimal(orderStatusUpdate.getRefundAmount());
        v2PaymentService.refundFollowOn(order, transaction, amount, Currency.getInstance(originalOrder.getCurrency().getIsocode()));

        return response;

    }

    private PaymentTransactionModel createPaymentTransactionForStoreCredit(final OrderModel order) {
        final PaymentTransactionModel transaction = modelService.create(PaymentTransactionModel.class);
        transaction.setCode("storecredit" + order.getUser().getUid() + "-" + UUID.randomUUID());
        this.modelService.save(transaction);
        transaction.setOrder(order);
        transaction.setPaymentProvider(STORE_CREDIT);
        this.modelService.save(transaction);
        return transaction;
    }

    /**
     * @param consignment
     */

    private void triggerDeliverySentNotificationToCustomer(final ConsignmentModel consignmentModel) {
        final AbstractOrderModel orderModel = consignmentModel.getOrder(); // triggering sms for ORDER_DISPATCHED
        v2SmsService.sendSms(v2UserSmsDataMapPopulator.createV2OrderDispatchedSmsDataMap(consignmentModel),
                "Order_Dispatched_message_template", ((CustomerModel) orderModel.getUser()).getMobileNumber());

        final ConsignmentProcessModel consignmentProcessModel = businessProcessService.createProcess("sendDeliveryEmailProcess-"
                + consignmentModel.getCode() + "-" + System.currentTimeMillis(), "sendDeliveryEmailProcess");
        consignmentProcessModel.setConsignment(consignmentModel);
        modelService.save(consignmentProcessModel);
        businessProcessService.startProcess(consignmentProcessModel);

    }

    private SoOrderStatusUpdateRes createResponse(final String resCode, final String resMsg) {
        final SoOrderStatusUpdateRes response = objectFactory.createSoOrderStatusUpdateRes();
        response.setRespCode(resCode);
        response.setRespMsg(resMsg);
        return response;
    }

    private void setOrderStatus(final OrderModel order, final OrderStatus orderStatus) {
        order.setStatus(orderStatus);
        modelService.save(order);
        modelService.refresh(order);
    }

    /**
     * @param modelService
     *        the modelService to set
     */
    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    /**
     * @param v2ConsignmentUpdateService
     *        the v2ConsignmentUpdateService to set
     */
    @Required
    public void setV2ConsignmentUpdateService(final V2ConsignmentUpdateService v2ConsignmentUpdateService) {
        this.v2ConsignmentUpdateService = v2ConsignmentUpdateService;
    }

    /**
     * @param v2OrderDao
     *        the v2OrderDao to set
     */
    @Required
    public void setV2OrderDao(final V2OrderDao v2OrderDao) {
        this.v2OrderDao = v2OrderDao;
    }

    /**
     * @param v2AcceleratorConsignmentDao
     *        the v2AcceleratorConsignmentDao to set
     */
    @Required
    public void setV2AcceleratorConsignmentDao(final V2AcceleratorConsignmentDao v2AcceleratorConsignmentDao) {
        this.v2AcceleratorConsignmentDao = v2AcceleratorConsignmentDao;
    }

    /**
     * @param v2SmsService
     */
    @Required
    public void setV2SmsService(final V2SmsService v2SmsService) {
        this.v2SmsService = v2SmsService;
    }

    /**
     * @param v2UserSmsDataMapPopulator
     */
    @Required
    public void setV2UserSmsDataMapPopulator(final V2UserSmsDataMapPopulator v2UserSmsDataMapPopulator) {
        this.v2UserSmsDataMapPopulator = v2UserSmsDataMapPopulator;
    }

    /**
     * @param businessProcessService
     */
    @Required
    public void setBusinessProcessService(final BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }

}
