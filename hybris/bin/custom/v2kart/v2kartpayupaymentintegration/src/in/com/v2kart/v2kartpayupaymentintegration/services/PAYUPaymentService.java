/**
 *
 */
package in.com.v2kart.v2kartpayupaymentintegration.services;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;

import in.com.v2kart.core.payment.services.V2PaymentService;
import in.com.v2kart.v2kartpayupaymentintegration.data.request.PAYUPaymentRequest;
import in.com.v2kart.v2kartpayupaymentintegration.data.response.PAYUPaymentResponse;

import java.util.Map;

/**
 * The Interface PAYUPaymentService.
 * 
 * @author gaurav2007
 */
public interface PAYUPaymentService extends V2PaymentService {

    /**
     * Gets the payment info.
     * 
     * @param guid
     *        the guid
     * @return V2PayUInfoModel
     */
    Map<String, String> getPaymentInfo(final String guid);

    /**
     * Save payment response received from PAYU
     * 
     * @param parameters
     *        , map having key value pairs of the response
     */
    void savePaymentInfo(final Map<String, String> parameters);

    /**
     * To capture the transaction details for the order
     * 
     * @param customerModel
     *        , customer model for which transaction is made
     * @param parameters
     *        , map of parameters received in response
     * @param cartModel
     *        , cart for which transaction is made
     * @return Payment Response
     */
    PAYUPaymentResponse capture(final CustomerModel customerModel, final Map<String, String> parameters, final CartModel cartModel);

    /**
     * Begin hop create payment.
     * 
     * @param customer
     *        the customer
     * @param enforcedPaymentMethod
     *        the enforced payment method
     * @param addressData
     *        the address data
     * @param successUrl
     *        the success url
     * @param cancelUrl
     *        the cancel url
     * @param failureUrl
     *        the failure url
     * @param pg
     *        the pg
     * @param phoneNumber
     *        the phone number
     * @return the payment request
     */
    PAYUPaymentRequest beginHopCreatePayment(final CustomerModel customer, final String enforcedPaymentMethod,
            final AddressData addressData, final String successUrl, final String cancelUrl, final String failureUrl, final String pg,
            final String phoneNumber);

    boolean refundOrCancelPayment(final String mihPayId, final double amount);

}
