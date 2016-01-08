/**
 *
 */
package in.com.v2kart.v2kartpayupaymentintegration.facades.impl;

import de.hybris.platform.acceleratorfacades.payment.data.PaymentSubscriptionResultData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentSubscriptionResultItem;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import in.com.v2kart.core.exception.V2Exception;
import in.com.v2kart.core.payment.services.V2EncryptionService;
import in.com.v2kart.facades.payment.impl.V2PaymentFacadeImpl;
import in.com.v2kart.v2kartpayupaymentintegration.constants.PaymentConstants.PaymentProperties.PAYU;
import in.com.v2kart.v2kartpayupaymentintegration.data.request.PAYUPaymentRequest;
import in.com.v2kart.v2kartpayupaymentintegration.data.response.PAYUPaymentResponse;
import in.com.v2kart.v2kartpayupaymentintegration.enums.PAYUDecisionEnum;
import in.com.v2kart.v2kartpayupaymentintegration.facades.PAYUPaymentFacade;
import in.com.v2kart.v2kartpayupaymentintegration.services.PAYUPaymentService;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * The Class PAYUPaymentFacadeImpl.
 *
 * @author Anuj
 */
public class PAYUPaymentFacadeImpl extends V2PaymentFacadeImpl implements PAYUPaymentFacade {

    private static final Logger LOG = Logger.getLogger(PAYUPaymentFacadeImpl.class);

    private PAYUPaymentService payUPaymentService;
    private V2EncryptionService payUEncryptionService;
    private Converter<PAYUPaymentRequest, PaymentData> payUPaymentRequestDataConverter;

    @Override
    public Map<String, String> getPaymentInfo(final String txnId) {
        return getPayUPaymentService().getPaymentInfo(txnId);
    }

    @Override
    public PaymentData beginHopCreatePayment(final String enforcedPaymentMethod, final AddressData addressData, final String pg,
            final String phoneNumber) throws V2Exception {
        final CustomerModel customerModel = getCurrentUserForCheckout();

        final PAYUPaymentRequest request = getPayUPaymentService().beginHopCreatePayment(customerModel, enforcedPaymentMethod, addressData,
                getPaymentUrl(getSiteConfigService().getProperty(PAYU.HOP_POST_SUCCESS_URL)),
                getPaymentUrl(getSiteConfigService().getProperty(PAYU.HOP_POST_FAILURE_URL)),
                getPaymentUrl(getSiteConfigService().getProperty(PAYU.HOP_POST_CANCEL_URL)), pg, phoneNumber);

        PaymentData paymentData = getPayUPaymentRequestDataConverter().convert(request);
        if (paymentData == null) {
            paymentData = new PaymentData();
            paymentData.setParameters(new HashMap<String, String>());
        }
        final HashMap<String, String> parameters = (HashMap<String, String>) paymentData.getParameters();
        parameters.put(PAYU.RequestParameters.HASH.getValue(), getPayUEncryptionService().getRequestHash(parameters));
        return paymentData;
    }

    @Override
    public void savePaymentInfo(final Map<String, String> parameters) {
        getPayUPaymentService().savePaymentInfo(parameters);

    }

    @Override
    public PaymentSubscriptionResultData completeHopCreatePayment(final Map<String, String> parameters, final boolean saveInAccount,
            final CartModel cartModel) throws V2Exception {
        final String transactionId = parameters.get(PAYU.ResponseParameters.TRANSACTION_ID.getValue());
        if (!transactionId.contains(cartModel.getCode())) {
            LOG.error("Transaction id does not match for transaction " + transactionId);
            throw new V2Exception("Transaction id does not match!!");
        }
        final Map<String, String> hashParameters = new HashMap<String, String>(parameters);
        hashParameters.put(PAYU.ResponseParameters.SALT.getValue(), getSiteConfigService().getProperty(PAYU.HOP_PAYMENT_MERCHANT_SALT));
        final String responseHash = getPayUEncryptionService().getResponseHash(hashParameters);
        if (!responseHash.equals(parameters.get(PAYU.ResponseParameters.HASH.getValue()))) {
            LOG.error("Calculated hash does not match with the hash received in response.");
            throw new V2Exception("Calculated hash does not match with the hash received in response.");
        }

        final CustomerModel customerModel = getCurrentUserForCheckout();

        final PAYUPaymentResponse response = getPayUPaymentService().capture(customerModel, parameters, cartModel);

        final PaymentSubscriptionResultItem paymentSubscriptionResultItem = new PaymentSubscriptionResultItem();
        paymentSubscriptionResultItem.setSuccess(PAYUDecisionEnum.SUCCESS.getValue().equalsIgnoreCase(response.getStatus()));
        paymentSubscriptionResultItem.setDecision(response.getStatus());

        return getPaymentSubscriptionResultDataConverter().convert(paymentSubscriptionResultItem);

    }

    /**
     * @return the payUPaymentService
     */
    public PAYUPaymentService getPayUPaymentService() {
        return payUPaymentService;
    }

    /**
     * @param payUPaymentService
     *        the payUPaymentService to set
     */
    public void setPayUPaymentService(final PAYUPaymentService payUPaymentService) {
        this.payUPaymentService = payUPaymentService;
    }

    /**
     * @return the payUEncryptionService
     */
    public V2EncryptionService getPayUEncryptionService() {
        return payUEncryptionService;
    }

    /**
     * @param payUEncryptionService
     *        the payUEncryptionService to set
     */
    public void setPayUEncryptionService(final V2EncryptionService payUEncryptionService) {
        this.payUEncryptionService = payUEncryptionService;
    }

    /**
     * @return the payUPaymentRequestDataConverter
     */
    public Converter<PAYUPaymentRequest, PaymentData> getPayUPaymentRequestDataConverter() {
        return payUPaymentRequestDataConverter;
    }

    /**
     * @param payUPaymentRequestDataConverter
     *        the payUPaymentRequestDataConverter to set
     */
    public void setPayUPaymentRequestDataConverter(final Converter<PAYUPaymentRequest, PaymentData> payUPaymentRequestDataConverter) {
        this.payUPaymentRequestDataConverter = payUPaymentRequestDataConverter;
    }

}
