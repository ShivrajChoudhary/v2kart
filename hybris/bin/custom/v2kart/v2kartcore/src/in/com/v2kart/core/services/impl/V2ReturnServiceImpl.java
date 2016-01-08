package in.com.v2kart.core.services.impl;

//import in.com.v2kart.core.order.executor.impl.V2SapInboundSaleOrderIntegrationService;
import in.com.v2kart.core.process.V2RefundOrderProcessor;
import in.com.v2kart.core.services.V2RefundService;
import in.com.v2kart.core.services.V2ReturnService;
import in.com.v2kart.facades.order.data.ReturnData;
import in.com.v2kart.facades.order.data.ReturnEntryData;
import in.com.v2kart.fulfilmentprocess.model.V2OrderModificationRefundInfoModel;
import in.com.v2kart.sapinboundintegration.services.V2SapInboundSaleOrderIntegrationService;
import in.com.v2kart.sapinboundintegration.ws.orderreturn.SOReturnRes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.acceleratorservices.orderprocessing.model.OrderModificationProcessModel;
import de.hybris.platform.basecommerce.enums.OrderEntryStatus;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.returns.OrderReturnRecordsHandlerException;
import de.hybris.platform.returns.impl.DefaultReturnService;
import de.hybris.platform.returns.model.OrderReturnRecordEntryModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.util.Config;

/**
 * Implementation of V2kart Return services
 * 
 * @author shailja gupta
 * 
 */
public class V2ReturnServiceImpl extends DefaultReturnService implements
        V2ReturnService {

    @Resource
    private OrderService orderService;

    private static final Logger LOG = Logger
            .getLogger(V2ReturnServiceImpl.class);

    private EnumerationService enumerationService;
    private CalculationService calculationService;
    private V2RefundService v2RefundService;
    private V2RefundOrderProcessor v2RefundOrderProcessor;
    private BusinessProcessService businessProcessService;
    private final String VISIBILITY_KEY = "order.return.visibility.days";
    private final static Integer VISIBLILTY_NO_OF_DAYS = 15;
    private final String WALLET_MODE = "WALLET";

    @Autowired
    private V2SapInboundSaleOrderIntegrationService v2SapInboundSaleOrderIntegrationService;

    public BusinessProcessService getBusinessProcessService() {
        return businessProcessService;
    }

    public void setBusinessProcessService(
            BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }

    public V2RefundOrderProcessor getV2RefundOrderProcessor() {
        return v2RefundOrderProcessor;
    }

    public void setV2RefundOrderProcessor(
            V2RefundOrderProcessor v2RefundOrderProcessor) {
        this.v2RefundOrderProcessor = v2RefundOrderProcessor;
    }

    public CalculationService getCalculationService() {
        return calculationService;
    }

    public void setCalculationService(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    public V2RefundService getV2RefundService() {
        return v2RefundService;
    }

    public void setV2RefundService(V2RefundService v2RefundService) {
        this.v2RefundService = v2RefundService;
    }

    public EnumerationService getEnumerationService() {
        return enumerationService;
    }

    public void setEnumerationService(EnumerationService enumerationService) {
        this.enumerationService = enumerationService;
    }

    /**
     * Process full order return from storefront
     * 
     * @param order
     * @param returnData
     */
    @Override
    public void processFullOrderReturn(OrderModel order, ReturnData returnData) {
        LOG.debug("*****************FULL Return from Website START***********");
        List<ReturnEntryData> refundDetailsList = getReturnEntriesForFullReturn(
                order, returnData);
        ReturnRequestModel request = generateReturnRequest(order);

        OrderModel refundOrderPreview = getRefundService()
                .createRefundOrderPreview(order);

        try {
            sendReturnInitiationMail(order);
            refundOrderPreview.setTotalGiftWrapPrice(order.getTotalGiftWrapPrice());
            refundOrderPreview = updateOrderAsPerReturnRequest(
                    refundOrderPreview,
                    getReturnEntriesForFullReturn(refundOrderPreview,
                            returnData), order);

            // TODO send order refund initiated mail....
            processRefundOrder(refundOrderPreview);
            boolean success = false;
            SOReturnRes soReturnRes = v2SapInboundSaleOrderIntegrationService.returnErpSales(request);
            for (final SOReturnRes.OrderReturnRes orderModRes : soReturnRes.getOrderReturnRes()) {
                if (StringUtils.isNotEmpty(orderModRes.getRespCode())) {
                    LOG.info("Order Return SAP Response: " + orderModRes.getRespMsg());
                    if ("S".equalsIgnoreCase(orderModRes.getRespCode())) {
                        success = true;
                    }
                }
            }
            if (success) {
                OrderReturnRecordEntryModel returnRecordEntry = ((V2RefundService) getV2RefundService())
                        .getOrderReturnRecordEntry(refundOrderPreview, request);
                getRefundService().apply(getRefundEntries(order, refundDetailsList, request), order);
                getV2RefundService().applyRefunds(refundOrderPreview, request);
                sendOrderFullyRefundedMail(order, returnRecordEntry);
            }
        } catch (OrderReturnRecordsHandlerException e) {
            LOG.info("Refund for given full return request" + request.getCode()
                    + "cannot be calculated.", e);
        } finally {
            getModelService().remove(refundOrderPreview);
        }
    }

    private void sendOrderFullyRefundedMail(OrderModel order,
            OrderReturnRecordEntryModel returnRecordEntry) {
        final OrderModificationProcessModel orderModificationProcessModel = getBusinessProcessService()
                .createProcess(
                        "sendOrderRefundEmailProcess-"
                                + returnRecordEntry.getCode() + "-"
                                + System.currentTimeMillis(),
                        "sendOrderRefundEmailProcess");
        orderModificationProcessModel.setOrder(order);
        orderModificationProcessModel
                .setOrderModificationRecordEntry(returnRecordEntry);
        getModelService().save(orderModificationProcessModel);
        getBusinessProcessService().startProcess(orderModificationProcessModel);
        LOG.debug("*****************FULL Return from Website END***********");
    }

    /**
     * Get Return Entries from ReturnData for full return
     * 
     * @param order
     * @param returnData
     * @return
     */
    private List<ReturnEntryData> getReturnEntriesForFullReturn(
            OrderModel order, ReturnData returnData) {
        List<ReturnEntryData> refundDetailsList = new ArrayList<ReturnEntryData>();
        for (AbstractOrderEntryModel orderEntry : order.getEntries()) {
            ReturnEntryData returnEntry = new ReturnEntryData();
            returnEntry.setOrderEntry(orderEntry.getEntryNumber());
            returnEntry.setNote(returnData.getNote());
            returnEntry.setReason(returnData.getReason());
            returnEntry.setReturnAction(returnData.getReturnAction());
            returnEntry.setMaxQuantity(orderEntry.getQuantity());
            returnEntry.setQuantity(orderEntry.getQuantity());
            refundDetailsList.add(returnEntry);
        }
        return refundDetailsList;
    }

    /**
     * Process partial order return from storefront
     * 
     * @param order
     * @param returnData
     */
    @Override
    public void processPartialOrderReturn(OrderModel order,
            ReturnData returnData) {
        LOG.debug("*****************Partail Return from Website START***********");
        OrderModel refundOrderPreview = null;
        ReturnRequestModel request = generateReturnRequest(order);
        try {
            List<ReturnEntryData> refundDetailsList = returnData
                    .getReturnedEntries();
            refundOrderPreview = getRefundService().createRefundOrderPreview(
                    order);
            sendReturnInitiationMail(order);
            refundOrderPreview = updateOrderAsPerReturnRequest(
                    refundOrderPreview, returnData.getReturnedEntries(), order);
            processRefundOrder(refundOrderPreview);
            // hit sap call here
            boolean success = false;
            SOReturnRes soReturnRes = v2SapInboundSaleOrderIntegrationService.returnErpSales(request);
            for (final SOReturnRes.OrderReturnRes orderModRes : soReturnRes.getOrderReturnRes()) {
                if (StringUtils.isNotEmpty(orderModRes.getRespCode())) {
                    LOG.info("Order Return SAP Response: " + orderModRes.getRespMsg());
                    if ("S".equalsIgnoreCase(orderModRes.getRespCode())) {
                        success = true;
                    }
                }
            }
            if (success) {
                OrderReturnRecordEntryModel returnRecordEntry = ((V2RefundService) getV2RefundService())
                        .getOrderReturnRecordEntry(refundOrderPreview, request);
                getRefundService().apply(getRefundEntries(order, refundDetailsList, request), order);
                getV2RefundService().applyRefunds(refundOrderPreview, request);
                sendOrderFullyRefundedMail(order, returnRecordEntry);
            }
        } catch (OrderReturnRecordsHandlerException e) {
            LOG.info(
                    "Refund for given partial return request"
                            + request.getCode() + "cannot be calculated.", e);
        } finally {
            getModelService().remove(refundOrderPreview);
        }
        LOG.debug("*****************Partail Return from Website END***********");
    }

    private void sendOrderPartiallyRefundedEmail(OrderModel order,
            OrderReturnRecordEntryModel returnRecordEntry) {
        final OrderModificationProcessModel orderModificationProcessModel = getBusinessProcessService()
                .createProcess(
                        "sendOrderPartiallyRefundedEmailProcess-"
                                + returnRecordEntry.getCode() + "-"
                                + System.currentTimeMillis(),
                        "sendOrderPartiallyRefundedEmailProcess");
        orderModificationProcessModel.setOrder(order);
        orderModificationProcessModel
                .setOrderModificationRecordEntry(returnRecordEntry);
        getModelService().save(orderModificationProcessModel);
        getBusinessProcessService().startProcess(orderModificationProcessModel);
    }

    protected void sendReturnInitiationMail(OrderModel order) {
        final OrderProcessModel orderProcessModel = (OrderProcessModel) getBusinessProcessService()
                .createProcess(
                        "orderRefundInitiationEmailProcess-" + order.getCode()
                                + "-" + System.currentTimeMillis(),
                        "orderRefundInitiationEmailProcess");
        orderProcessModel.setOrder(order);
        getModelService().save(orderProcessModel);
        getBusinessProcessService().startProcess(orderProcessModel);

    }

    /**
     * Return tentative amount to be refunded to customer on request of return
     */
    @Override
    public Double getRefundAmount(OrderModel order, ReturnData returnData) {
        OrderModel refundOrderPreview = getRefundService()
                .createRefundOrderPreview(order);
        if (returnData.getIsFull()) {
            refundOrderPreview = updateOrderAsPerReturnRequest(
                    refundOrderPreview,
                    getReturnEntriesForFullReturn(refundOrderPreview,
                            returnData), order);
        } else {
            refundOrderPreview = updateOrderAsPerReturnRequest(
                    refundOrderPreview, returnData.getReturnedEntries(), order);
        }
        V2OrderModificationRefundInfoModel refundInfo = null;
        Double refundAmount = 0d;
        try {
            refundInfo = getV2RefundOrderProcessor()
                    .calculateRefundInfoForReturn(refundOrderPreview, true);
            refundAmount = refundInfo.getAmountTobeRefunded().doubleValue();
        } finally {
            getModelService().remove(refundInfo);
            getModelService().remove(refundOrderPreview);
        }
        return refundAmount;
    }

    /**
     * Get all the returnable entries for an order
     */
    @Override
    public Map<AbstractOrderEntryModel, Long> getAllReturnableOrderEntries(
            OrderModel order) {
        return getAllReturnableEntries(order);
    }

    /**
     * Generate return request for a given order
     * 
     * @param order
     * @return
     */
    private ReturnRequestModel generateReturnRequest(OrderModel order) {
        ReturnRequestModel request = createReturnRequest(order);
        String rmaString = createRMA(request);
        request.setRMA(rmaString);
        return request;
    }

    /**
     * Return {@link AbstractOrderEntryModel } from OrderModel by its entryId.
     * 
     * @param orderModel
     * 
     * @param entryId
     *        to get AbstractOrderEntryModel
     * @return AbstractOrderEntryModel
     */
    private AbstractOrderEntryModel getOrderEntryByEntryId(
            final OrderModel orderModel, final Integer entryId) {
        ServicesUtil.validateParameterNotNull(orderModel,
                "Order must not be null");

        for (final AbstractOrderEntryModel oem : orderModel.getEntries()) {
            if (entryId == oem.getEntryNumber()) {
                return oem;
            }
        }
        return null;
    }

    /**
     * Update RefundOrderPreview as per Return request
     * 
     * @param refundOrderPreview
     * @param returnableEntries
     * @return
     */
    private OrderModel updateOrderAsPerReturnRequest(
            final OrderModel refundOrderPreview,
            final List<ReturnEntryData> returnableEntries, final OrderModel order) {
        for (ReturnEntryData returnEntry : returnableEntries) {
            if (returnEntry.getQuantity() == null) {
                returnEntry.setQuantity(0l);
            }
            AbstractOrderEntryModel orderEntry = getOrderEntryByEntryId(
                    refundOrderPreview, returnEntry.getOrderEntry());
            long previousQuantity = orderEntry.getQuantity().longValue();
            if (returnEntry.getQuantity() <= returnEntry.getMaxQuantity()) {
                orderEntry.setQuantity(Long.valueOf(previousQuantity
                        - returnEntry.getQuantity()));

                if (previousQuantity == returnEntry.getQuantity()) {
                    orderEntry.setQuantityStatus(OrderEntryStatus.DEAD);
                }
                getModelService().save(orderEntry);
            }
        }
        refundOrderPreview.setCalculated(Boolean.FALSE);
        getModelService().save(refundOrderPreview);
        // Recalculate Promotion details
        this.v2RefundService
                .recalculatePromotionsForReturnPreview(refundOrderPreview);
        // refundOrderPreview.setTotalGiftWrapPrice(order.getTotalGiftWrapPrice());
        // getModelService().save(refundOrderPreview);
        return refundOrderPreview;
    }

    /**
     * Process refund for given order and return request.
     */
    @Override
    public void processRefundOrder(OrderModel orderPreview) {
        if (getV2RefundOrderProcessor() != null) {
            V2OrderModificationRefundInfoModel refundInfo = getV2RefundOrderProcessor()
                    .calculateRefundInfoForReturn(orderPreview, true);// TODO
            // getV2RefundOrderProcessor().process(orderPreview,
            // refundInfo.getAmountTobeRefunded());
        }
    }

    /**
     * Get Refund entries for given request.
     * 
     * @param order
     * @param refundDetailsList
     * @param request
     * @return
     */
    private List<RefundEntryModel> getRefundEntries(final OrderModel order,
            final List<ReturnEntryData> refundDetailsList,
            final ReturnRequestModel request) {
        final List<RefundEntryModel> refundEntries = new ArrayList<RefundEntryModel>();
        for (ReturnEntryData returnEntryData : refundDetailsList) {
            AbstractOrderEntryModel orderEntryModel = getOrderEntryByEntryId(
                    order, returnEntryData.getOrderEntry());
            if (orderEntryModel != null) {
                RefundEntryModel refundEntry = createRefund(
                        request,
                        orderEntryModel,
                        returnEntryData.getNote(),
                        Long.valueOf(returnEntryData.getQuantity()),
                        getEnumerationService().getEnumerationValue(
                                ReturnAction.class,
                                returnEntryData.getReturnAction()),
                        getEnumerationService()
                                .getEnumerationValue(RefundReason.class,
                                        returnEntryData.getReason()));
                refundEntries.add(refundEntry);
            } else {
                LOG.error((new StringBuilder(
                        "Failed to find orderEntry with entry number ["))
                        .append(returnEntryData.getOrderEntry()).append("]")
                        .toString());
            }
        }
        return refundEntries;

    }

    /**
     * Check whether order is returnable or not.
     */
    @Override
    public Boolean isOrderReturnable(OrderModel order) {
        Boolean isReturnable = Boolean.FALSE;
        Date startDate = null;
        Date endDate = null;
        List<Date> consignmentDates = new ArrayList<Date>();
        Map<AbstractOrderEntryModel, Long> returnableEntries = getAllReturnableOrderEntries(order);
        if ((order.getStatus().equals(OrderStatus.COMPLETED) || order.getStatus().equals(OrderStatus.RETURN_SENT_TO_SAP_FAILED))
                && (returnableEntries != null && returnableEntries.size() != 0)) {

            if (order.getConsignments() != null && order.getConsignments().size() != 0) {
            	isReturnable = Boolean.TRUE;
            	isReturnable= returnOrderVisiblity(order);
             /*     for (ConsignmentModel consignment : order.getConsignments()) { 
                	  if (null != consignment.getCompletionDate())
                  consignmentDates.add(consignment.getCompletionDate()); 
                	  } 
                  if (consignmentDates.size() > 0) {
                  Collections.sort(consignmentDates); 
                  startDate = consignmentDates.get(0); 
                  final Calendar date = Calendar.getInstance();
                  date.setTime(startDate); 
                  date.add(Calendar.DAY_OF_YEAR, Config.getInt( VISIBILITY_KEY, VISIBLILTY_NO_OF_DAYS)); 
                  endDate =date.getTime(); 
                  Date currentDate = new Date(); 
                  if ((currentDate.getTime() >= startDate.getTime()) && currentDate.getTime() <= endDate.getTime()) 
                  { 
                 isReturnable = Boolean.TRUE; 
                 } 
                }
             */   
            
            }
        }
        return isReturnable;
    }

    public boolean returnOrderVisiblity(OrderModel order){
        Boolean isReturnable = Boolean.TRUE;
        Date startDate = null;
        Date endDate = null;
        List<Date> consignmentDates = new ArrayList<Date>();
   
    	for (ConsignmentModel consignment : order.getConsignments()) { 
         	  if (null != consignment.getCompletionDate())
           consignmentDates.add(consignment.getCompletionDate()); 
         	  
    	}  if (consignmentDates.size() > 0) {
           Collections.sort(consignmentDates); 
           startDate = consignmentDates.get(0); 
           final Calendar date = Calendar.getInstance();
           date.setTime(startDate); 
           date.add(Calendar.DAY_OF_YEAR, Config.getInt( VISIBILITY_KEY, VISIBLILTY_NO_OF_DAYS)); 
           endDate =date.getTime(); 
           Date currentDate = new Date(); 
           if ((currentDate.getTime() >= startDate.getTime()) && currentDate.getTime() <= endDate.getTime()) 
           { 
          isReturnable = Boolean.TRUE; 
          LOG.info("RETURN VISIBILITY STATUS of completion date :"+isReturnable);
           } else {
        	  isReturnable=Boolean.FALSE;
          }
    } 
    	LOG.info("RETURN VISIBILITY STATUS :"+isReturnable);
           return isReturnable;
    }
    // protected AbstractOrderEntryModel getOrderEntry(RefundEntryModel refund,
    // AbstractOrderModel order) {
    // AbstractOrderEntryModel refundOrderEntry = refund.getOrderEntry();
    // AbstractOrderEntryModel ret = null;
    // for (AbstractOrderEntryModel original : order.getEntries()) {
    // if (!(original.equals(refundOrderEntry)))
    // continue;
    // ret = original;
    // break;
    // }
    //
    // return ret;
    // }
}
