/**
 * 
 */
package in.com.v2kart.v2kartebspaymentintegration.services;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.CustomerModel;

import in.com.v2kart.v2kartebspaymentintegration.data.request.EBSActionRequest;
import in.com.v2kart.v2kartebspaymentintegration.data.request.EBSPaymentRequest;

/**
 * @author vikrant2480
 * 
 */
public interface CreateEBSPaymentRequestStrategy
{
    EBSPaymentRequest createPaymentRequest(String secureKey, String requestUrl, String accountId, String mode, String description,
            String returnUrl, AddressData billingAddressData, String phoneNumber, CustomerModel customer);

    EBSActionRequest createActionRequest(String action, String secureKey, String requestUrl, String accountId, String paymentId,
            String refundAmount, String orderCode);
}
