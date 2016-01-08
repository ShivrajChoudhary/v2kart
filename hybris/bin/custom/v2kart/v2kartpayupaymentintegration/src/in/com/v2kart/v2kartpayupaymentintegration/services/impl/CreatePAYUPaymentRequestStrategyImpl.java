/**
 *
 */
package in.com.v2kart.v2kartpayupaymentintegration.services.impl;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;

import in.com.v2kart.v2kartpayupaymentintegration.constants.PaymentConstants.PaymentProperties.PAYU;
import in.com.v2kart.v2kartpayupaymentintegration.data.request.PAYUPaymentRequest;
import in.com.v2kart.v2kartpayupaymentintegration.services.CreatePAYUPaymentRequestStrategy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * The Class CreatePAYUPaymentRequestStrategyImpl.
 *
 * @author Anuj
 */
public class CreatePAYUPaymentRequestStrategyImpl implements CreatePAYUPaymentRequestStrategy {

    private static final Logger LOG = Logger.getLogger(CreatePAYUPaymentRequestStrategyImpl.class);

    private CartService cartService;
    private CustomerEmailResolutionService customerEmailResolutionService;

    @Override
    public PAYUPaymentRequest createPaymentRequest(final String requestUrl, final String successUrl, final String cancelUrl,
            final String failureUrl, final CustomerModel customer, final String merchantKey, final String enforcedPaymentMethod,
            final AddressData addressData, final String salt, final String pg, final String phoneNumber) {
        final CartModel cartModel = getCartService().getSessionCart();
        if (cartModel == null) {
            return null;
        }

        final PAYUPaymentRequest request = new PAYUPaymentRequest();
        request.setRequestUrl(requestUrl);
        /*
         * if (cartModel.getSecondaryPaymentInfo() == null) {
         * 
         * } else { request.setAmount(String.valueOf(cartModel.getTotalPrice().doubleValue() -
         * cartModel.getSecondaryPaymentInfo().getTotalAmount().doubleValue())); }
         */
        // Amount set to total payable dynamically calculated which is total minus wallet money if applied
        request.setAmount(String.valueOf(cartModel.getTotalPayableBalance()));
        request.setCancelUrl(cancelUrl);
        request.setSuccessUrl(successUrl);
        request.setEmail(getCustomerEmailResolutionService().getEmailForCustomer(customer));
        request.setEnforcedPaymentMethod(enforcedPaymentMethod);
        request.setFailureUrl(failureUrl);
        request.setFirstName(customer.getDisplayName());
        request.setMerchantKey(merchantKey);
        request.setOrderDescription(cartModel.getEntries().toString());
        request.setPhone(phoneNumber);
        request.setRequestUrl(requestUrl);
        request.setTransactionId(cartModel.getCode() + "_" + System.currentTimeMillis());
        request.setSalt(salt);
        request.setPg(pg);
        request.setBankCode(enforcedPaymentMethod);

        if (addressData != null) {
            request.setUdf1(getUdf1(addressData.getTitleCode(), addressData.getFirstName(), addressData.getLastName(),
                    addressData.getPhone()));
            request.setUdf2(getUdf2(addressData.getLine1(), addressData.getLine2()));
            final RegionData regionData = addressData.getRegion();
            String regionIsoCode = null;
            if (regionData != null) {
                regionIsoCode = regionData.getIsocode();
            }
            request.setUdf3(getUdf3(addressData.getTown(), addressData.getPostalCode(), regionIsoCode));
        }
        return request;
    }

    /**
     * Gets the udf1.
     *
     * @param title
     *        the title
     * @param firstName
     *        the first name
     * @param lastName
     *        the last name
     * @param mobileNo
     *        the mobile no
     * @return the udf1
     */
    private String getUdf1(final String title, final String firstName, final String lastName, final String mobileNo) {
        final StringBuilder udf1 = new StringBuilder();
        udf1.append(title).append(PAYU.FIELD_SEPERATOR);
        udf1.append(firstName).append(PAYU.FIELD_SEPERATOR);
        udf1.append(lastName).append(PAYU.FIELD_SEPERATOR);
        udf1.append(mobileNo);
        return udf1.toString();
    }

    /**
     * Gets the udf2.
     *
     * @param line1
     *        the line1
     * @param line2
     *        the line2
     * @param line3
     *        the line3
     * @return the udf2
     */
    private String getUdf2(final String line1, final String line2) {
        final StringBuilder udf1 = new StringBuilder();
        udf1.append(line1).append(PAYU.FIELD_SEPERATOR);
        udf1.append(line2);
        return udf1.toString();
    }

    /**
     * Gets the udf3.
     *
     * @param town
     *        the town
     * @param postalCode
     *        the postal code
     * @param regionIsoCode
     *        the region isoCode
     * @return the udf3
     */
    private String getUdf3(final String town, final String postalCode, final String regionIsoCode) {
        final StringBuilder udf1 = new StringBuilder();
        udf1.append(town).append(PAYU.FIELD_SEPERATOR);
        udf1.append(postalCode).append(PAYU.FIELD_SEPERATOR);
        udf1.append(regionIsoCode);
        return udf1.toString();
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

}
