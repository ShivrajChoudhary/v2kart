/**
 * 
 */
package in.com.v2kart.ccavenuepaymentintegration.services;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;

import in.com.v2kart.ccavenuepaymentintegration.data.request.CCAvenuePaymentRequest;
import in.com.v2kart.ccavenuepaymentintegration.data.response.CCAvenuePaymentResponse;
import in.com.v2kart.core.payment.services.V2PaymentService;

import java.util.Map;


/**
 * The Interface CCAvenuePaymentService.
 * 
 * @author yamini2280
 */
public interface CCAvenuePaymentService extends V2PaymentService
{

	/**
	 * Gets the payment info.
	 * 
	 * @param guid
	 *           the guid
	 * @return V2PayUInfoModel
	 */
	Map<String, String> getPaymentInfo(final String guid);

	/**
	 * Save payment response received from CCAvenue
	 * 
	 * @param parameters
	 *           , map having key value pairs of the response
	 */
	void savePaymentInfo(final Map<String, String> parameters);

	/**
	 * To capture the transaction details for the order
	 * 
	 * @param customerModel
	 *           , customer model for which transaction is made
	 * @param parameters
	 *           , map of parameters received in response
	 * @param cartModel
	 *           , cart for which transaction is made
	 * @return Payment Response
	 */
	CCAvenuePaymentResponse capture(final CustomerModel customerModel, final Map<String, String> parameters,
			final CartModel cartModel);

	/**
	 * Begin hop create payment.
	 * 
	 * @param customer
	 *           the customer
	 * @param enforcedPaymentMethod
	 *           the enforced payment method
	 * @param addressData
	 *           the address data
	 * @param successUrl
	 *           the success url
	 * @param cancelUrl
	 *           the cancel url
	 * @param phoneNumber
	 * @return the payment request
	 */
	CCAvenuePaymentRequest beginHopCreatePayment(final CustomerModel customer, final String enforcedPaymentMethod,
			final AddressData addressData, final String successUrl, final String cancelUrl, String phoneNumber);

}
