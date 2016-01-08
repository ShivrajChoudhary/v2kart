/**
 * 
 */
package in.com.v2kart.ccavenuepaymentintegration.facades;

import de.hybris.platform.acceleratorfacades.payment.data.PaymentSubscriptionResultData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.CartModel;

import in.com.v2kart.core.exception.V2Exception;
import in.com.v2kart.facades.payment.V2PaymentFacade;

import java.util.Map;


/**
 * The Interface CCAvenuePaymentFacade.
 * 
 * @author yamini2280
 */
public interface CCAvenuePaymentFacade extends V2PaymentFacade
{

	/**
	 * To get payment response received
	 * 
	 * @param txnId
	 *           , transaction id for which response to be received
	 * @return map of parameters
	 */
	Map<String, String> getPaymentInfo(String txnId);

	/**
	 * To save payment response received
	 * 
	 * @param parameters
	 *           , parameters received
	 */
	void savePaymentInfo(final Map<String, String> parameters);

	/**
	 * To complete the payment flow once response recived from payment gateway
	 * 
	 * @param parameters
	 *           , parameters received
	 * @param saveInAccount
	 * @param cartModel
	 *           , cart
	 * @return result data
	 * @throws V2Exception
	 */
	PaymentSubscriptionResultData completeHopCreatePayment(final Map<String, String> parameters, final boolean saveInAccount,
			final CartModel cartModel) throws V2Exception;

	/**
	 * Begin hop create payment.
	 * 
	 * @param enforcedPaymentMethod
	 *           the enforced payment method
	 * @param addressData
	 *           the address data
	 * @param phoneNumber
	 *           phone number
	 * @return the payment data
	 * @throws V2Exception
	 *            the V2 exception
	 */
	PaymentData beginHopCreatePayment(String enforcedPaymentMethod, AddressData addressData, String phoneNumber)
			throws V2Exception;

}
