/**
 * 
 */
package in.com.v2kart.v2kartebspaymentintegration.services.impl;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;

import in.com.v2kart.v2kartebspaymentintegration.constants.PaymentConstants.PaymentProperties.EBS;
import in.com.v2kart.v2kartebspaymentintegration.data.request.EBSActionRequest;
import in.com.v2kart.v2kartebspaymentintegration.data.request.EBSPaymentRequest;
import in.com.v2kart.v2kartebspaymentintegration.services.CreateEBSPaymentRequestStrategy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author vikrant2480
 * 
 */
public class CreateEBSPaymentRequestStrategyImpl implements CreateEBSPaymentRequestStrategy {

    private static final Logger LOG = Logger.getLogger(CreateEBSPaymentRequestStrategyImpl.class);

    private CartService cartService;
    private CustomerEmailResolutionService customerEmailResolutionService;

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.v2kartebspaymentintegration.services.CreateEBSPaymentRequestStrategy#createPaymentRequest(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String, de.hybris.platform.commercefacades.user.data.AddressData,
     * de.hybris.platform.commercefacades.user.data.AddressData, java.lang.String)
     */
    @Override
    public EBSPaymentRequest createPaymentRequest(final String secureKey, final String requestUrl, final String accountId,
            final String mode, final String description, final String returnUrl, final AddressData billingAddressData,
            final String phoneNumber, final CustomerModel customer) {
        final CartModel cartModel = cartService.getSessionCart();
        if (cartModel == null) {
            return null;
        }

        final EBSPaymentRequest request = new EBSPaymentRequest();

        request.setAccountId(accountId);
        request.setReferenceNumber(cartModel.getCode() + "_" + System.currentTimeMillis());
        request.setAmount(String.valueOf(cartModel.getTotalPayableBalance()));
        request.setMode(mode);
        request.setDescription(description);
        request.setRequestUrl(requestUrl);

        request.setBillingName(getName(billingAddressData.getFirstName(), billingAddressData.getLastName()));
        request.setBillingAddress(getAddress(billingAddressData.getLine1(), billingAddressData.getLine2()));
        request.setBillingCity(billingAddressData.getTown());
        if (null != billingAddressData.getRegion()) {
            request.setBillingState(billingAddressData.getRegion().getName());
            request.setBillingCountry(billingAddressData.getRegion().getCountryIso());
        }
        request.setBillingPostalCode(billingAddressData.getPostalCode());
        request.setBillingPhoneNumber(billingAddressData.getPhone());

        request.setBillingEmailId(customerEmailResolutionService.getEmailForCustomer(customer));

        request.setKey(secureKey);
        request.setReturnUrl(returnUrl);
        return request;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.v2kartebspaymentintegration.services.CreateEBSPaymentRequestStrategy#createRefundRequest(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public EBSActionRequest createActionRequest(final String action, final String secureKey, final String requestUrl,
            final String accountId, final String paymentId,
            final String refundAmount, final String orderCode) {
        final EBSActionRequest request = new EBSActionRequest();

        request.setAction(action);
        request.setAccountId(accountId);
        request.setAmount(refundAmount);
        request.setRequestUrl(requestUrl);
        request.setSecretKey(secureKey);
        request.setPaymentId(paymentId);
        request.setOrderCode(orderCode);
        return request;
    }

    /**
     * Gets the name
     * 
     * @param firstName
     * @param lastName
     * @return
     */
    private String getName(final String firstName, final String lastName) {
        final StringBuilder name = new StringBuilder();
        return name.append(firstName).append(EBS.FIELD_SEPERATOR).append(lastName).toString();
    }

    /**
     * Gets the address
     * 
     * @param line1
     * @param line2
     * @return
     */
    private String getAddress(final String line1, final String line2) {
        final StringBuilder name = new StringBuilder();
        return name.append(line1).append(EBS.FIELD_SEPERATOR).append(line2).toString();
    }

    /**
     * @param cartService
     *        the cartService to set
     */
    @Required
    public void setCartService(final CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * @param customerEmailResolutionService
     *        the customerEmailResolutionService to set
     */
    @Required
    public void setCustomerEmailResolutionService(final CustomerEmailResolutionService customerEmailResolutionService) {
        this.customerEmailResolutionService = customerEmailResolutionService;
    }
}
