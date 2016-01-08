/**
 * 
 */
package in.com.v2kart.ccavenuepaymentintegration.services;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.CustomerModel;

import in.com.v2kart.ccavenuepaymentintegration.data.request.CCAvenuePaymentRequest;


/**
 * The Interface CreateCCAvenuePaymentRequestStrategy.
 * 
 * @author yamini2280
 */
public interface CreateCCAvenuePaymentRequestStrategy
{

	/**
	 * Creates CCAvenue Payment request.
	 * 
	 * @param requestUrl
	 *           the request url
	 * @param successUrl
	 *           the success url
	 * @param cancelUrl
	 *           the cancel url
	 * @param customer
	 *           the customer
	 * @param merchantKey
	 *           the merchant key
	 * @param enforcedPaymentMethod
	 *           the enforced payment method
	 * @param addressData
	 *           the address data
	 * @param currency
	 *           the currency
	 * @param language
	 *           the language
	 * @param phoneNumber
	 * @return the V2 payment request
	 */
	CCAvenuePaymentRequest createPaymentRequest(String requestUrl, String successUrl, String cancelUrl, CustomerModel customer,
			String merchantKey, String enforcedPaymentMethod, AddressData addressData, String currency, String language,
			String phoneNumber);
}
