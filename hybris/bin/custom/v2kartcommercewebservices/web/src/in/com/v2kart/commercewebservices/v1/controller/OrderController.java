/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
package in.com.v2kart.commercewebservices.v1.controller;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.data.OrderHistoriesData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import in.com.v2kart.commercewebservices.constants.YcommercewebservicesConstants;
import in.com.v2kart.commercewebservices.formatters.WsDateFormatter;
import in.com.v2kart.commercewebservices.order.data.OrderCancellationData;
import in.com.v2kart.commercewebservices.order.data.OrderCancelledData;
import in.com.v2kart.commercewebservices.order.data.OrderRefundAmount;
import in.com.v2kart.commercewebservices.order.data.OrderReturnableData;
import in.com.v2kart.commercewebservices.order.data.OrderReturnedData;
import in.com.v2kart.commercewebservices.order.data.ReasonsData;
import in.com.v2kart.commercewebservices.order.data.TrackOrderData;
import in.com.v2kart.commercewebservices.queues.data.OrderStatusUpdateElementData;
import in.com.v2kart.commercewebservices.queues.data.OrderStatusUpdateElementDataList;
import in.com.v2kart.commercewebservices.queues.impl.OrderStatusUpdateQueue;
import in.com.v2kart.facades.cancel.data.CancelData;
import in.com.v2kart.facades.cancel.data.CancelEntryData;
import in.com.v2kart.facades.order.V2OrderFacade;
import in.com.v2kart.facades.order.V2OrderReturnFacade;
import in.com.v2kart.facades.order.cancel.V2OrderCancellationFacade;
import in.com.v2kart.facades.order.data.ReturnData;
import in.com.v2kart.facades.order.data.ReturnEntryData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Web Service Controller for the ORDERS resource. All methods check orders of the current user. Methods require basic authentication and
 * are restricted to https channel.
 */
@Controller("orderControllerV1")
@RequestMapping(value = "/{baseSiteId}/orders")
public class OrderController extends BaseController {
    @Resource(name = "orderFacade")
    private V2OrderFacade orderFacade;
    @Resource(name = "orderCancellationFacade")
    private V2OrderCancellationFacade orderCancellationFacade;
    @Resource(name = "orderReturnFacade")
    private V2OrderReturnFacade orderReturnFacade;
    @Resource(name = "wsDateFormatter")
    private WsDateFormatter wsDateFormatter;
    @Resource(name = "orderStatusUpdateQueue")
    private OrderStatusUpdateQueue orderStatusUpdateQueue;
    @Resource(name = "enumerationService")
    private EnumerationService enumerationService;
    private static final String ORDER_STATUS_NOTCOMPLETED = "recentOrders";
    private static final String HOLD_ACTION_STRING = "HOLD";

    /**
     * Web service for getting current user's order information by order code.<br>
     * Sample call: https://localhost:9002/rest/v1/mysite/orders/1234 <br>
     * This method requires authentication.<br>
     * Method type : <code>GET</code>.<br>
     * Method is restricted for <code>HTTPS</code> channel.
     *
     * @param code
     *        - order code - must be given as path variable.
     *
     * @return {@link OrderData} as response body.
     */
    @Secured("ROLE_CUSTOMERGROUP")
    @RequestMapping(value = "/{code}", method = RequestMethod.GET)
    @ResponseBody
    public OrderData getOrder(@PathVariable final String code) {
        return orderFacade.getOrderDetailsForCode(code);

    }

    /**
     * Web service for getting current user's order information by order code.<br>
     * Sample call: https://localhost:9002/rest/v1/mysite/orders/order/1234/cancel <br>
     * This method requires authentication.<br>
     * Method type : <code>GET</code>.<br>
     * Method is restricted for <code>HTTPS</code> channel.
     *
     * @param code
     *        - order code - must be given as path variable.
     *
     * @return {@link OrderData} as response body.
     */
    @Secured("ROLE_CUSTOMERGROUP")
    @RequestMapping(value = "/order/{orderCode}/cancelOrder", method = RequestMethod.GET)
    @ResponseBody
    public OrderCancellationData cancelOrder(@PathVariable("orderCode") final String orderCode) {
        final List<CancelEntryData> entries = populateOrderCancelData(orderFacade.getAllCancellableEntries(orderCode));
        final OrderCancellationData orderCancellationData = new OrderCancellationData();
        orderCancellationData.setEntries(entries);
        orderCancellationData.setReasons(getAllCancelReasons());
        return orderCancellationData;
    }

    /**
     * @param jsOnData
     * @return
     * @throws CMSItemNotFoundException
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Secured("ROLE_CUSTOMERGROUP")
    @ResponseBody
    @RequestMapping(value = "/order/cancel/getRefundAmount", method = RequestMethod.POST)
    public OrderRefundAmount getRefundAmount(@RequestParam final String jsOnData) throws CMSItemNotFoundException, JsonParseException,
            JsonMappingException, IOException {

        OrderCancelledData orderCancelledData = new OrderCancelledData();
        final ObjectMapper objectMapper = new ObjectMapper();

        orderCancelledData = objectMapper.readValue(jsOnData, OrderCancelledData.class);
        final CancelData cancelData = new CancelData();
        cancelData.setOrderCode(orderCancelledData.getOrderCode());
        final String isFullCancel = orderCancelledData.getIsFull();
        if (isFullCancel != null && !isFullCancel.isEmpty() && isFullCancel.equalsIgnoreCase("true")) {
            cancelData.setIsFull(Boolean.TRUE);
            cancelData.setNote(orderCancelledData.getNotes());
            cancelData.setReason(orderCancelledData.getReason());
        } else {

            cancelData.setCancelledEntries(orderCancelledData.getEntries());
            cancelData.setIsFull(Boolean.FALSE);
        }
        final String amount = orderCancellationFacade.getRefundAmount(cancelData).getFormattedValue();
        final OrderRefundAmount refundAmount = new OrderRefundAmount();
        refundAmount.setAmount(amount);
        return refundAmount;
    }

    /**
     * @param jsOnData
     * @throws CMSItemNotFoundException
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Secured("ROLE_CUSTOMERGROUP")
    @ResponseBody
    @RequestMapping(value = "/order/cancelOrder", method = RequestMethod.POST)
    public void cancelOrders(@RequestParam final String jsOnData) throws CMSItemNotFoundException, JsonParseException,
            JsonMappingException, IOException {
        OrderCancelledData orderCancelledData = new OrderCancelledData();
        final ObjectMapper objectMapper = new ObjectMapper();

        orderCancelledData = objectMapper.readValue(jsOnData, OrderCancelledData.class);

        final CancelData cancelData = new CancelData();
        cancelData.setOrderCode(orderCancelledData.getOrderCode());
        final String isFullCancel = orderCancelledData.getIsFull();
        if (isFullCancel != null && !isFullCancel.isEmpty() && isFullCancel.equalsIgnoreCase("true")) {
            cancelData.setNote(orderCancelledData.getNotes());
            cancelData.setReason(orderCancelledData.getReason());
            orderCancellationFacade.cancelFullOrder(cancelData);
        } else {
            cancelData.setCancelledEntries(orderCancelledData.getEntries());
            orderCancellationFacade.cancelPartialOrder(cancelData);
        }
    }

    protected List<ReasonsData> getAllCancelReasons() {
        final Map<String, String> cancelReasons = new HashMap<String, String>();
        final List<CancelReason> enumerationValues = enumerationService.getEnumerationValues(CancelReason.class);
        final Iterator<CancelReason> iterator = enumerationValues.iterator();
        while (iterator.hasNext()) {
            final CancelReason reason = iterator.next();
            cancelReasons.put(reason.getCode().toLowerCase(), reason.getCode());
        }
        return populateSelectBoxForString(orderCancellationFacade.filterCancelReasons(cancelReasons));
    }

    /**
     * @param orderCode
     * @return OrderReturnableData
     * @throws CMSItemNotFoundException
     */
    @Secured("ROLE_CUSTOMERGROUP")
    @ResponseBody
    @RequestMapping(value = "/order/{orderCode}/returnOrder", method = RequestMethod.GET)
    public OrderReturnableData returnOrderPage(@PathVariable("orderCode") final String orderCode) throws CMSItemNotFoundException {
        final OrderReturnableData orderReturnableData = new OrderReturnableData();
        final List<ReturnEntryData> returnableEntries = populateOrderReturnData(orderReturnFacade.getAllReturnableOrderEntries(orderCode));
        orderReturnableData.setEntries(returnableEntries);
        orderReturnableData.setReasons(getAllRefundReasons());
        return orderReturnableData;

    }

    /**
     * @param jsOnData
     * @return
     * @throws CMSItemNotFoundException
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Secured("ROLE_CUSTOMERGROUP")
    @ResponseBody
    @RequestMapping(value = "/order/return/getRefundAmount", method = RequestMethod.POST)
    public OrderRefundAmount getReturnRefundAmount(@RequestParam final String jsOnData) throws CMSItemNotFoundException,
            JsonParseException, JsonMappingException, IOException {
        OrderReturnedData orderReturnedData = new OrderReturnedData();
        final ObjectMapper objectMapper = new ObjectMapper();

        orderReturnedData = objectMapper.readValue(jsOnData, OrderReturnedData.class);
        final ReturnData returnData = new ReturnData();

        returnData.setOrderCode(orderReturnedData.getOrderCode());
        final String isFull = orderReturnedData.getIsFull();
        if (isFull != null && !isFull.isEmpty() && isFull.equalsIgnoreCase("true")) {
            returnData.setIsFull(Boolean.TRUE);
            returnData.setReturnAction(orderReturnedData.getReturnAction());
            returnData.setNote(orderReturnedData.getNotes());
            returnData.setReason(orderReturnedData.getReason());
        } else {

            returnData.setReturnedEntries(orderReturnedData.getEntries());
            returnData.setIsFull(Boolean.FALSE);
        }
        final String amount = orderReturnFacade.getRefundAmount(returnData).getFormattedValue();
        final OrderRefundAmount refundAmount = new OrderRefundAmount();
        refundAmount.setAmount(amount);
        return refundAmount;
    }

    /**
     * @param jsOnData
     * @throws CMSItemNotFoundException
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Secured("ROLE_CUSTOMERGROUP")
    @ResponseBody
    @RequestMapping(value = "/order/returnOrder", method = RequestMethod.POST)
    public void returnOrder(@RequestParam final String jsOnData) throws CMSItemNotFoundException, JsonParseException, JsonMappingException,
            IOException {

        OrderReturnedData orderReturnedData = new OrderReturnedData();
        final ObjectMapper objectMapper = new ObjectMapper();

        orderReturnedData = objectMapper.readValue(jsOnData, OrderReturnedData.class);

        final ReturnData returnData = new ReturnData();
        returnData.setOrderCode(orderReturnedData.getOrderCode());
        final String isFullReturn = orderReturnedData.getIsFull();
        if (isFullReturn != null && !isFullReturn.isEmpty() && isFullReturn.equalsIgnoreCase("true")) {
            returnData.setReturnAction(HOLD_ACTION_STRING);
            returnData.setNote(orderReturnedData.getNotes());
            returnData.setReason(orderReturnedData.getReason());
            orderReturnFacade.processFullOrderReturn(returnData);
        } else {
            returnData.setReturnedEntries(orderReturnedData.getEntries());
            orderReturnFacade.processPartialOrderReturn(returnData);
        }
    }

    /*
     * method to get all refund reasons
     */
    protected List<ReasonsData> getAllRefundReasons() {
        final Map<String, String> refundReasons = new HashMap<String, String>();
        final List<RefundReason> enumerationValues = enumerationService.getEnumerationValues(RefundReason.class);
        final Iterator<RefundReason> iterator = enumerationValues.iterator();
        while (iterator.hasNext()) {
            final RefundReason reason = iterator.next();
            refundReasons.put(reason.getCode().toLowerCase(), reason.getCode());
        }
        return populateSelectBoxForString(orderReturnFacade.filterRefundReasons(refundReasons));
    }

    private List<ReasonsData> populateSelectBoxForString(final Map<String, String> seletedData) {
        final List<ReasonsData> reasonsdataList = new ArrayList<ReasonsData>();
        for (final Entry<String, String> entry : seletedData.entrySet()) {
            final ReasonsData reasonsData = new ReasonsData();
            reasonsData.setReasonKey(entry.getKey());
            reasonsData.setReasonValue(entry.getValue());
            reasonsdataList.add(reasonsData);
        }
        return reasonsdataList;
    }

    /**
     * Web service for getting order information by order GUID.<br>
     * Sample call: https://localhost:9002/rest/v1/mysite/orders/byGuid/ea134b389f3d04b4821f51aa55c79f0766974f5f<br>
     * Method type : <code>GET</code>.<br>
     * Method is restricted for <code>HTTPS</code> channel.
     *
     * @param guid
     *        - order guid - must be given as path variable.
     *
     * @return {@link OrderData} as response body.
     */
    @Secured("ROLE_CLIENT")
    @RequestMapping(value = "/byGuid/{guid}", method = RequestMethod.GET)
    @ResponseBody
    public OrderData getOrderByGuid(@PathVariable final String guid) {
        return orderFacade.getOrderDetailsForGUID(guid);
    }

    /**
     * Web service for getting current user's order history data.<br>
     * Sample call: https://localhost:9002/rest/v1/mysite/orders?statuses=COMPLETED,CANCELLED&pageSize=5&currentPage=0 <br>
     * This method requires authentication.<br>
     * Method type : <code>GET</code>.<br>
     * Method is restricted for <code>HTTPS</code> channel.
     *
     * @param statuses
     *        - filter for order statuses- optional
     * @param currentPage
     *        - pagination parameter- optional
     * @param pageSize
     *        - {@link PaginationData} parameter - optional
     * @param sort
     *        - sort criterion
     *
     * @return {@link OrderData} as response body.
     */

    @Secured("ROLE_CUSTOMERGROUP")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public OrderHistoriesData getPagedOrdersForStatuses(@RequestParam(required = false) final String status,

    @RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,

    @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,

    @RequestParam(required = false) final String sort) {
        final PageableData pageableData = new PageableData();
        pageableData.setCurrentPage(currentPage);
        pageableData.setPageSize(pageSize);
        pageableData.setSort(sort);

        final OrderHistoriesData orderHistoriesData;
        if (null != status && status.equalsIgnoreCase(ORDER_STATUS_NOTCOMPLETED)) {
            final List<OrderStatus> filteredOrderStatus = orderFacade.getFilteredOrderStatuses(OrderStatus.COMPLETED);
            orderHistoriesData = createOrderHistoriesData(orderFacade.getPagedOrderHistoryForStatuses(pageableData,
                    filteredOrderStatus.toArray(new OrderStatus[filteredOrderStatus.size()])));
        } else {
            orderHistoriesData = createOrderHistoriesData(orderFacade.getPagedOrderHistoryForStatuses(pageableData));
        }
        for (int i = 0; i < orderHistoriesData.getOrders().size(); i++) {
            final String code = orderHistoriesData.getOrders().get(i).getOrderData().getCode();
            final OrderData orderData = orderHistoriesData.getOrders().get(i).getOrderData();
            final List<CancelEntryData> entries = populateOrderCancelData(orderFacade.getAllCancellableEntries(code));
            final Boolean isCanceleable = orderFacade.isOrderCancellable(orderData) && setOrderCancelable(orderData, entries);
            orderData.setIsCancelable(isCanceleable);
            final Boolean isReturnable = orderReturnFacade.setOrderReturnable(code);
            orderData.setIsReturnable(isReturnable);
        }

        return orderHistoriesData;
    }

    /*
     * @Secured("ROLE_CUSTOMERGROUP")
     *
     * @RequestMapping(value = "/history", method = RequestMethod.GET)
     *
     * @ResponseBody public OrderHistoriesData getOrderHistory(@RequestParam(required = false) final String status,
     *
     * @RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
     *
     * @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
     *
     * @RequestParam(required = false) final String sort) { final PageableData pageableData = new PageableData();
     * pageableData.setCurrentPage(currentPage); pageableData.setPageSize(pageSize); pageableData.setSort(sort); final OrderHistoriesData
     * orderHistoriesData; if (null != status && status.equalsIgnoreCase(ORDER_STATUS_NOTCOMPLETED)) { final List<OrderStatus>
     * filteredOrderStatus = orderFacade.getFilteredOrderStatuses(OrderStatus.COMPLETED); orderHistoriesData =
     * createOrderHistoriesData(orderFacade.getPagedOrderHistoryForStatuses(pageableData, filteredOrderStatus.toArray(new
     * OrderStatus[filteredOrderStatus.size()]))); } else { orderHistoriesData =
     * createOrderHistoriesData(orderFacade.getPagedOrderHistoryForStatuses(pageableData)); } return orderHistoriesData; }
     */

    /**
     * Web service handler for order status update feed. Returns only elements from the current baseSite newer than specified timestamp.
     * Sample Call: http://localhost:9001/rest/v1/{SITE}/orders/statusFeed<br>
     * This method requires trusted client authentication.<br>
     * Method type : <code>GET</code>.<br>
     * Method is restricted for <code>HTTPS</code> channel.
     *
     * @param timestamp
     *        - time in ISO-8601 format
     * @return {@link in.com.v2kart.commercewebservices.queues.data.OrderStatusUpdateElementDataList}
     */
    @Secured("ROLE_TRUSTED_CLIENT")
    @RequestMapping(value = "/statusFeed", method = RequestMethod.GET)
    @ResponseBody
    public OrderStatusUpdateElementDataList expressUpdate(@RequestParam final String timestamp, @PathVariable final String baseSiteId) {
        final Date timestampDate = wsDateFormatter.toDate(timestamp);
        final OrderStatusUpdateElementDataList orderStatusUpdateDataList = new OrderStatusUpdateElementDataList();
        orderStatusUpdateDataList.setOrderStatusUpdateElements(orderStatusUpdateQueue.getItems(timestampDate));
        filterOrderStatusQueue(orderStatusUpdateDataList, baseSiteId);
        return orderStatusUpdateDataList;
    }

    private void filterOrderStatusQueue(final OrderStatusUpdateElementDataList orderStatusUpdateDataList, final String baseSiteId) {
        final Iterator<OrderStatusUpdateElementData> dataIterator = orderStatusUpdateDataList.getOrderStatusUpdateElements().iterator();
        while (dataIterator.hasNext()) {
            final OrderStatusUpdateElementData orderStatusUpdateData = dataIterator.next();
            if (!baseSiteId.equals(orderStatusUpdateData.getBaseSiteId())) {
                dataIterator.remove();
            }
        }
    }

    protected Set<OrderStatus> extractOrderStatuses(final String statuses) {
        final String statusesStrings[] = statuses.split(YcommercewebservicesConstants.OPTIONS_SEPARATOR);

        final Set<OrderStatus> statusesEnum = new HashSet<>();
        for (final String status : statusesStrings) {
            statusesEnum.add(OrderStatus.valueOf(status));
        }
        return statusesEnum;
    }

    protected OrderHistoriesData createOrderHistoriesData(final SearchPageData<OrderHistoryData> result) {
        final OrderHistoriesData orderHistoriesData = new OrderHistoriesData();

        orderHistoriesData.setOrders(result.getResults());
        orderHistoriesData.setSorts(result.getSorts());
        orderHistoriesData.setPagination(result.getPagination());

        return orderHistoriesData;
    }

    /**
     * @param cancelableEntries
     * @param orderDetails
     */
    private Boolean setOrderCancelable(final OrderData orderDetails, final List<CancelEntryData> cancelleableEntries) {
        Boolean result = Boolean.TRUE;
        // whole order is only cancelable when
        // there is no consignment and
        // no order entry or line item is cancelled before
        if (cancelleableEntries != null && cancelleableEntries.size() != 0) {
            if ((orderDetails.getConsignments().size() != 0)) {
                if (ConsignmentStatus.READY_TO_DISPATCH.equals(orderDetails.getConsignments().get(0).getStatus())) {
                    result = Boolean.FALSE;
                }
            } else {
                result = Boolean.TRUE;
            }
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/track/order", method = RequestMethod.POST)
    public TrackOrderData TrackOrder(@RequestParam(required = true) final String orderCode,
            @RequestParam(required = true) final String emailId) throws CMSItemNotFoundException {
        final TrackOrderData trackOrderData = new TrackOrderData();
        try {
            final OrderData orderDetails = orderFacade.getOrderDetailsForCodeWithoutUser(orderCode);

            if (orderDetails.getUser().getUid().equals(emailId)) {
                trackOrderData.setOrderData(orderDetails);
            } else {
                trackOrderData.setStatus("Order is not Linked to given EmailId");
                trackOrderData.setOrderData(new OrderData());
            }
        } catch (final UnknownIdentifierException | IllegalArgumentException | ModelNotFoundException e) {
            trackOrderData.setStatus("Order Id is not Valid");
            trackOrderData.setOrderData(new OrderData());
        }
        return trackOrderData;

    }

    private List<ReturnEntryData> populateOrderReturnData(final Map<OrderEntryData, Long> returnableEntries) {
        final List<ReturnEntryData> returnEntries = new ArrayList<ReturnEntryData>();
        for (final Map.Entry<OrderEntryData, Long> entry : returnableEntries.entrySet()) {
            final ReturnEntryData returnEntry = new ReturnEntryData();
            returnEntry.setMaxQuantity(entry.getValue());
            returnEntry.setOrderEntry(entry.getKey().getEntryNumber());
            returnEntry.setProductName(entry.getKey().getProduct().getName().toUpperCase());
            returnEntry.setReturnAction(HOLD_ACTION_STRING);
            returnEntries.add(returnEntry);
        }
        return returnEntries;
    }

    private List<CancelEntryData> populateOrderCancelData(final Map<OrderEntryData, Long> canceleableEntries) {
        if (canceleableEntries != null && canceleableEntries.size() != 0) {
            final List<CancelEntryData> entries = new ArrayList<CancelEntryData>(canceleableEntries.size());
            for (final Map.Entry<OrderEntryData, Long> entry : canceleableEntries.entrySet()) {
                final CancelEntryData cancelData = new CancelEntryData();
                cancelData.setProductName(entry.getKey().getProduct().getName().toUpperCase());
                cancelData.setMaxQuantity(entry.getValue());
                cancelData.setOrderEntry(entry.getKey().getEntryNumber());
                entries.add(cancelData);
            }
            return entries;
        }
        return null;
    }
}
