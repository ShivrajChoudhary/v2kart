/**
 * 
 */
package in.com.v2kart.v2kartebspaymentintegration.facades;

import de.hybris.platform.acceleratorfacades.payment.data.PaymentSubscriptionResultData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;

import in.com.v2kart.core.exception.V2Exception;
import in.com.v2kart.fulfilmentprocess.model.V2OrderModificationRefundInfoModel;
import in.com.v2kart.v2kartebspaymentintegration.data.request.EBSActionRequest;
import in.com.v2kart.v2kartebspaymentintegration.data.response.EBSActionResponse;

import java.util.Map;

/**
 * @author vikrant2480
 * 
 */
public interface EBSPaymentFacade {

    PaymentData beginHopCreatePayment(final String description, final AddressData billingAddressData, final String phoneNumber)
            throws V2Exception;

    void savePaymentInfo(final Map<String, String> parameters);

    PaymentSubscriptionResultData completeHopCreatePayment(final Map<String, String> parameters, final boolean saveInAccount,
            final CartModel cartModel) throws V2Exception;

    EBSActionRequest createActionRequest(final OrderModel orderModel, final String refundAmount, final String action) throws V2Exception;

    /**
     * @param refundOrderPreview
     * @param refundInfoModel
     * @return
     * @throws V2Exception
     */
    EBSActionRequest processRefund(final OrderModel refundOrderPreview, final V2OrderModificationRefundInfoModel refundInfoModel)
            throws V2Exception;

    EBSActionResponse initiateCancelRequest(final OrderModel orderModel, final String refundAmount) throws V2Exception;
}
