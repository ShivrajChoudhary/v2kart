/**
 *
 */
package in.com.v2kart.ccavenuepaymentintegration.services.impl;

import de.hybris.platform.acceleratorservices.payment.data.CustomerBillToData;
import de.hybris.platform.acceleratorservices.payment.data.CustomerShipToData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import in.com.v2kart.ccavenuepaymentintegration.data.request.CCAvenuePaymentRequest;
import in.com.v2kart.ccavenuepaymentintegration.services.CreateCCAvenuePaymentRequestStrategy;
import in.com.v2kart.core.payment.constants.PaymentConstants.PaymentProperties;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * The Class CreateCCAvenuePaymentRequestStrategyImpl.
 * 
 * @author yamini2280
 */
public class CreateCCAvenuePaymentRequestStrategyImpl implements CreateCCAvenuePaymentRequestStrategy {

    private static final Logger LOG = Logger.getLogger(CreateCCAvenuePaymentRequestStrategyImpl.class);

    private CartService cartService;

    private CustomerEmailResolutionService customerEmailResolutionService;

    private Converter<AddressModel, CustomerBillToData> customerBillToDataConverter;
    private Converter<CartModel, CustomerShipToData> customerShipToDataConverter;

    private CommonI18NService commonI18NService;

    private Map<String, String> paymentOptionMappings;

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.ccavenuepaymentintegration.services.CreateCCAvenuePaymentRequestStrategy#createPaymentRequest (java.lang.String,
     * java.lang.String, java.lang.String, de.hybris.platform.core.model.user.CustomerModel, java.lang.String, java.lang.String,
     * de.hybris.platform.commercefacades.user.data.AddressData, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public CCAvenuePaymentRequest createPaymentRequest(final String requestUrl, final String successUrl, final String cancelUrl,
            final CustomerModel customer, final String merchantKey, final String enforcedPaymentMethod, final AddressData addressData,
            final String currency, final String language, final String phoneNumber) {
        final CartModel cartModel = getCartService().getSessionCart();
        if (cartModel == null) {
            return null;
        }

        final CCAvenuePaymentRequest request = new CCAvenuePaymentRequest();
        request.setRequestUrl(requestUrl);
        request.setCancelUrl(cancelUrl);
        request.setRedirectUrl(successUrl);
        request.setAmount(String.valueOf(cartModel.getTotalPrice()));
        request.setCurrency(currency);
        request.setLanguage(language);
        request.setMerchantId(merchantKey);
        request.setOrderId(cartModel.getCode() + "_" + System.currentTimeMillis());
        request.setPaymentOption(paymentOptionMappings.get(enforcedPaymentMethod));

        if (addressData != null) {
            request.setCustomerBillToData(getCustomerBillToData(addressData));
        } else {
            final AddressModel address = cartModel.getDeliveryAddress();
            request.setCustomerBillToData(getCustomerBillToDataConverter().convert(address));
        }
        request.getCustomerBillToData().setBillToEmail(getCustomerEmailResolutionService().getEmailForCustomer(customer));
        request.getCustomerBillToData().setBillToPhoneNumber(phoneNumber);
        final CountryModel countryModel = getCommonI18NService().getCountry(PaymentProperties.INDIA_COUNTRY_ISO);
        request.getCustomerBillToData().setBillToCountry(countryModel.getName());
        return request;
    }

    /**
     * Gets the customer bill to data.
     * 
     * @param addressData
     *        the address data
     * @return the customer bill to data
     */
    private CustomerBillToData getCustomerBillToData(final AddressData addressData) {
        final CustomerBillToData billToData = new CustomerBillToData();
        billToData.setBillToCity(addressData.getTown());
        billToData.setBillToFirstName(addressData.getFirstName());
        billToData.setBillToLastName(addressData.getLastName());
        billToData.setBillToStreet1(addressData.getLine1());
        billToData.setBillToStreet2(addressData.getLine2());
        final RegionData regionData = addressData.getRegion();
        String regionIsoCode = "MH";
        if (regionData != null) {
            regionIsoCode = regionData.getIsocode();
        }
        billToData.setBillToState(regionIsoCode);
        billToData.setBillToCountry(addressData.getCountry().getIsocode());
        billToData.setBillToPostalCode(addressData.getPostalCode());
        return billToData;
    }

    /**
     * Gets the cart service.
     * 
     * @return the cartService
     */
    public final CartService getCartService() {
        return cartService;
    }

    /**
     * Sets the cart service.
     * 
     * @param cartService
     *        the cartService to set
     */
    @Required
    public final void setCartService(final CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Gets the customer email resolution service.
     * 
     * @return the customerEmailResolutionService
     */
    public CustomerEmailResolutionService getCustomerEmailResolutionService() {
        return customerEmailResolutionService;
    }

    /**
     * Sets the customer email resolution service.
     * 
     * @param customerEmailResolutionService
     *        the customerEmailResolutionService to set
     */
    public void setCustomerEmailResolutionService(final CustomerEmailResolutionService customerEmailResolutionService) {
        this.customerEmailResolutionService = customerEmailResolutionService;
    }

    /**
     * @return the customerBillToDataConverter
     */
    public Converter<AddressModel, CustomerBillToData> getCustomerBillToDataConverter() {
        return customerBillToDataConverter;
    }

    /**
     * @param customerBillToDataConverter
     *        the customerBillToDataConverter to set
     */
    @Required
    public void setCustomerBillToDataConverter(final Converter<AddressModel, CustomerBillToData> customerBillToDataConverter) {
        this.customerBillToDataConverter = customerBillToDataConverter;
    }

    /**
     * @return the customerShipToDataConverter
     */
    public Converter<CartModel, CustomerShipToData> getCustomerShipToDataConverter() {
        return customerShipToDataConverter;
    }

    /**
     * @param customerShipToDataConverter
     *        the customerShipToDataConverter to set
     */
    @Required
    public void setCustomerShipToDataConverter(final Converter<CartModel, CustomerShipToData> customerShipToDataConverter) {
        this.customerShipToDataConverter = customerShipToDataConverter;
    }

    /**
     * Gets the common i18 n service.
     * 
     * @return the commonI18NService
     */
    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    /**
     * Sets the common i18 n service.
     * 
     * @param commonI18NService
     *        the commonI18NService to set
     */
    @Required
    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    /**
     * @return the paymentOptionMappings
     */
    public Map<String, String> getPaymentOptionMappings() {
        return paymentOptionMappings;
    }

    /**
     * @param paymentOptionMappings
     *        the paymentOptionMappings to set
     */
    @Required
    public void setPaymentOptionMappings(final Map<String, String> paymentOptionMappings) {
        this.paymentOptionMappings = paymentOptionMappings;
    }

}
