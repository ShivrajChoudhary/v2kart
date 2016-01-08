/**
 * 
 */
package in.com.v2kart.facades.order.impl;

import in.com.v2kart.facades.order.V2OrderFacade;
import in.com.v2kart.facades.order.data.OrderModificationRecordData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.impl.DefaultOrderFacade;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ordercancel.OrderCancelCancelableEntriesStrategy;
import de.hybris.platform.ordermodify.model.OrderModificationRecordModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.store.BaseStoreModel;

/**
 * @author shubhammaheshwari
 *
 */
public class V2OrderFacadeImpl extends DefaultOrderFacade implements V2OrderFacade {

    private EnumerationService enumerationService;
    private OrderCancelCancelableEntriesStrategy cancelableEntriesStrategy;
    private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;
    private Converter<OrderModificationRecordModel, OrderModificationRecordData> orderModificationRecordConverter;

    public Converter<OrderModificationRecordModel, OrderModificationRecordData> getOrderModificationRecordConverter() {
	return orderModificationRecordConverter;
    }

    public void setOrderModificationRecordConverter(
	    Converter<OrderModificationRecordModel, OrderModificationRecordData> orderModificationRecordConverter) {
	this.orderModificationRecordConverter = orderModificationRecordConverter;
    }

    public EnumerationService getEnumerationService() {
	return enumerationService;
    }

    public OrderCancelCancelableEntriesStrategy getCancelableEntriesStrategy() {
	return cancelableEntriesStrategy;
    }

    public void setCancelableEntriesStrategy(OrderCancelCancelableEntriesStrategy cancelableEntriesStrategy) {
	this.cancelableEntriesStrategy = cancelableEntriesStrategy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * in.com.v2kart.facades.order.V2OrderFacade#getAllOrderStatusExcept(de.
     * hybris.platform.core.enums.OrderStatus[])
     */
    @Override
    public List<OrderStatus> getFilteredOrderStatuses(final OrderStatus... exceptOrderStatus) {
	List<OrderStatus> orderStatuses = enumerationService.getEnumerationValues(OrderStatus.class);
	for (OrderStatus status : exceptOrderStatus) {
	    if (orderStatuses.contains(status)) {
		orderStatuses.remove(status);
	    }
	}
	return orderStatuses;
    }

    public Converter<AbstractOrderEntryModel, OrderEntryData> getOrderEntryConverter() {
	return orderEntryConverter;
    }

    public void setOrderEntryConverter(Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter) {
	this.orderEntryConverter = orderEntryConverter;
    }

    public void setEnumerationService(EnumerationService enumerationService) {
	this.enumerationService = enumerationService;
    }

    @Override
    public boolean isOrderCancellable(final OrderData orderData) {
	boolean cancellable = true;
	if (orderData.getStatus().equals(OrderStatus.CANCELLED)
		|| orderData.getStatus().equals(OrderStatus.READY_TO_SHIP)
		|| orderData.getStatus().equals(OrderStatus.DISPATCHED)
		|| orderData.getStatus().equals(OrderStatus.COMPLETED)
		|| orderData.getStatus().equals(OrderStatus.RETURN_SENT_TO_SAP_FAILED)) {
	    cancellable = false;
	}
	return cancellable;
    }

    @Override
    public Map<OrderEntryData, Long> getAllCancellableEntries(final String orderCode) {
	Map<OrderEntryData, Long> cancellabeEntriesData = new HashMap<OrderEntryData, Long>();
	Map<AbstractOrderEntryModel, Long> cancelableEntries = null;
	final OrderModel order = getOrderForCode(orderCode);
	if (null != order) {
	    cancelableEntries = cancelableEntriesStrategy.getAllCancelableEntries(order, null);
	    if (cancelableEntries != null && !cancelableEntries.isEmpty()) {
		for (Map.Entry<AbstractOrderEntryModel, Long> entry : cancelableEntries.entrySet()) {
		    cancellabeEntriesData.put(getOrderEntryConverter().convert(entry.getKey()), entry.getValue());
		}
	    }
	}
	return cancellabeEntriesData;

    }

    private OrderModel getOrderForCode(final String code) {
	final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();
	final OrderModel orderModel = getCheckoutCustomerStrategy().isAnonymousCheckout() ? getCustomerAccountService()
		.getOrderDetailsForGUID(code, baseStoreModel) : getCustomerAccountService().getOrderForCode(
		(CustomerModel) getUserService().getCurrentUser(), code, baseStoreModel);
	if (orderModel == null) {
	    throw new UnknownIdentifierException("Order with orderGUID " + code
		    + " not found for current user in current BaseStore");
	}
	return orderModel;
    }

    @Override
    public OrderData getOrderDetailsForCode(final String code) {
	final OrderModel orderModel = getOrderForCode(code);
	if (orderModel == null) {
	    throw new UnknownIdentifierException("Order with orderGUID " + code
		    + " not found for current user in current BaseStore");
	}
	OrderData orderData = getOrderConverter().convert(orderModel);
	if (CollectionUtils.isNotEmpty(orderModel.getModificationRecords())) {
	    orderData.setModificationRecords(Converters.convertAll(orderModel.getModificationRecords(),
		    getOrderModificationRecordConverter()));
	}
	return orderData;
    }

}
