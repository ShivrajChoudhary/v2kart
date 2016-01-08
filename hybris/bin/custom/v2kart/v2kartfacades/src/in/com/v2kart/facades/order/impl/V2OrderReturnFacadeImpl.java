package in.com.v2kart.facades.order.impl;

import in.com.v2kart.core.services.V2ReturnService;
import in.com.v2kart.facades.order.V2OrderReturnFacade;
import in.com.v2kart.facades.order.data.ReturnData;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

/**
 * Implementation of V2OrderReturnFacade
 * 
 * @author shailjagupta
 *
 */
public class V2OrderReturnFacadeImpl implements V2OrderReturnFacade {

    private UserService userService;
    private CustomerAccountService customerAccountService;
    private BaseStoreService baseStoreService;
    private CheckoutCustomerStrategy checkoutCustomerStrategy;
    private EnumerationService enumerationService;
    private V2ReturnService returnService;
    private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;
    private Converter<OrderModel, OrderData> orderConverter;
    private PriceDataFactory priceDataFactory;
    private Map<String, String> v2RefundReasons;

    public Map<String, String> getV2RefundReasons() {
        return v2RefundReasons;
    }

    @Required
    public void setV2RefundReasons(Map<String, String> v2RefundReasons) {
        this.v2RefundReasons = v2RefundReasons;
    }

    public PriceDataFactory getPriceDataFactory() {
        return priceDataFactory;
    }

    public void setPriceDataFactory(PriceDataFactory priceDataFactory) {
        this.priceDataFactory = priceDataFactory;
    }

    public Converter<OrderModel, OrderData> getOrderConverter() {
        return orderConverter;
    }

    public void setOrderConverter(Converter<OrderModel, OrderData> orderConverter) {
        this.orderConverter = orderConverter;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public CustomerAccountService getCustomerAccountService() {
        return customerAccountService;
    }

    public void setCustomerAccountService(CustomerAccountService customerAccountService) {
        this.customerAccountService = customerAccountService;
    }

    public BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    public void setBaseStoreService(BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    public CheckoutCustomerStrategy getCheckoutCustomerStrategy() {
        return checkoutCustomerStrategy;
    }

    public void setCheckoutCustomerStrategy(CheckoutCustomerStrategy checkoutCustomerStrategy) {
        this.checkoutCustomerStrategy = checkoutCustomerStrategy;
    }

    public EnumerationService getEnumerationService() {
        return enumerationService;
    }

    public void setEnumerationService(EnumerationService enumerationService) {
        this.enumerationService = enumerationService;
    }

    public V2ReturnService getReturnService() {
        return returnService;
    }

    public void setReturnService(V2ReturnService returnService) {
        this.returnService = returnService;
    }

    /**
     * API to get tentative amount to be refunded after return
     * 
     * @param returnData
     * @return Price data
     */
    @Override
    public PriceData getRefundAmount(ReturnData returnData) {
        OrderModel order = getOrderForCode(returnData.getOrderCode());
        Double amount = getReturnService().getRefundAmount(order, returnData);
        return getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(amount), order.getCurrency());
    }

    /**
     * API to process Full Order Return
     * 
     * @param returnData
     */
    @Override
    public void processFullOrderReturn(ReturnData returnData) {
        OrderModel order = getOrderForCode(returnData.getOrderCode());
        getReturnService().processFullOrderReturn(order, returnData);
    }

    /**
     * API to process Partial Order Return
     * 
     * @param returnData
     */
    @Override
    public void processPartialOrderReturn(ReturnData returnData) {
        OrderModel order = getOrderForCode(returnData.getOrderCode());
        getReturnService().processPartialOrderReturn(order, returnData);

    }

    /**
     * API to get all the returnable Entries for given ordercode
     * 
     * @param orderCode
     * @return
     */
    @Override
    public Map<OrderEntryData, Long> getAllReturnableOrderEntries(String orderCode) {
        Map<OrderEntryData, Long> returnableEntriesData = new HashMap<OrderEntryData, Long>();
        OrderModel order = getOrderForCode(orderCode);
        Map<AbstractOrderEntryModel, Long> returnableEntries = getReturnService().getAllReturnableOrderEntries(order);
        if (returnableEntries != null && returnableEntries.size() != 0) {
            for (Map.Entry<AbstractOrderEntryModel, Long> entry : returnableEntries.entrySet()) {
                returnableEntriesData.put(getOrderEntryConverter().convert(entry.getKey()), entry.getValue());
            }
        }
        return returnableEntriesData;
    }

    public Converter<AbstractOrderEntryModel, OrderEntryData> getOrderEntryConverter() {
        return orderEntryConverter;
    }

    public void setOrderEntryConverter(Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter) {
        this.orderEntryConverter = orderEntryConverter;
    }

    /**
     * Get OrderModel for given ordercode
     * 
     * @param code
     * @return
     */
    private OrderModel getOrderForCode(final String code) {
        final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();
        final OrderModel orderModel = getCheckoutCustomerStrategy().isAnonymousCheckout() ? getCustomerAccountService()
                .getOrderDetailsForGUID(code, baseStoreModel) : getCustomerAccountService().getOrderForCode(
                (CustomerModel) getUserService().getCurrentUser(), code, baseStoreModel);
        return orderModel;
    }

    /**
     * API to check whether order is returnable or not. Also assure that return button is visible only for 60 days after
     * the completion of order.
     * 
     * @param orderCode
     * @return
     */
    @Override
    public Boolean setOrderReturnable(String orderCode) {
        OrderModel order = getOrderForCode(orderCode);
        return getReturnService().isOrderReturnable(order);
    }

    @Override
    public Map<String, String> filterRefundReasons(Map<String, String> refundReasonMap) {
        Map<String, String> refundReasons = new HashMap<String, String>();

        for (Map.Entry<String, String> entry : refundReasonMap.entrySet()) {
            if (getV2RefundReasons().containsKey(entry.getKey())) {
                refundReasons.put(entry.getKey(), getV2RefundReasons().get(entry.getKey()));
            }

        }
        return refundReasons;
    }

}
