/**
 * 
 */
package in.com.v2kart.v2kartebspaymentintegration.services;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;

import in.com.v2kart.v2kartebspaymentintegration.data.request.EBSActionRequest;
import in.com.v2kart.v2kartebspaymentintegration.data.request.EBSPaymentRequest;
import in.com.v2kart.v2kartebspaymentintegration.data.response.EBSPaymentResponse;

import java.util.Map;

/**
 * @author vikrant2480
 * 
 */
public interface EBSPaymentService {

    EBSPaymentRequest beginHopCreatePayment(final String description, final String returnUrl, final AddressData billingAddressData,
            final String phoneNumber, final CustomerModel customer);

    void savePaymentInfo(final Map<String, String> parameters);

    EBSPaymentResponse capture(final CustomerModel customerModel, final Map<String, String> parameters, final CartModel cartModel);

    EBSActionRequest createActionRequest(String paymentId, String refundAmount, String orderCode, String action);

    /**
     * @param txnid
     * @return
     */
    String getPaymentIdForTransaction(String txnid);

}
