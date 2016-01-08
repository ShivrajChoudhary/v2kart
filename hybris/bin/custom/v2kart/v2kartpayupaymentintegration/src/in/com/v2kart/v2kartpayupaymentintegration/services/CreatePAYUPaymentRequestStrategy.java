/**
 *
 */
package in.com.v2kart.v2kartpayupaymentintegration.services;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.CustomerModel;

import in.com.v2kart.v2kartpayupaymentintegration.data.request.PAYUPaymentRequest;

/**
 * The Interface CreatePAYUPaymentRequestStrategy.
 * 
 * @author gaurav2007
 */
public interface CreatePAYUPaymentRequestStrategy {

    /**
     * Creates PAYU Payment request.
     * 
     * @param requestUrl
     *        the request url
     * @param successUrl
     *        the success url
     * @param cancelUrl
     *        the cancel url
     * @param failureUrl
     *        the failure url
     * @param customer
     *        the customer
     * @param merchantKey
     *        the merchant key
     * @param enforcedPaymentMethod
     *        the enforced payment method
     * @param addressData
     *        the address data
     * @param salt
     *        the salt
     * @param pg
     *        the pg
     * @param phoneNumber
     *        the phone number
     * @return the payment request
     */
    PAYUPaymentRequest createPaymentRequest(String requestUrl, String successUrl, String cancelUrl, String failureUrl,
            CustomerModel customer, String merchantKey, String enforcedPaymentMethod, AddressData addressData, String salt, String pg,
            String phoneNumber);
}
