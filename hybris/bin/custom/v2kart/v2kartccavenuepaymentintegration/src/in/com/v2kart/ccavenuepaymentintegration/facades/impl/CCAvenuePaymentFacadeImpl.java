/**
 *
 */
package in.com.v2kart.ccavenuepaymentintegration.facades.impl;

import de.hybris.platform.acceleratorfacades.payment.data.PaymentSubscriptionResultData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentSubscriptionResultItem;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import in.com.v2kart.ccavenuepaymentintegration.constants.PaymentConstants.PaymentProperties.CCAVENUE;
import in.com.v2kart.ccavenuepaymentintegration.data.request.CCAvenuePaymentRequest;
import in.com.v2kart.ccavenuepaymentintegration.data.response.CCAvenuePaymentResponse;
import in.com.v2kart.ccavenuepaymentintegration.enums.CCAvenueDecisionEnum;
import in.com.v2kart.ccavenuepaymentintegration.facades.CCAvenuePaymentFacade;
import in.com.v2kart.ccavenuepaymentintegration.services.CCAvenuePaymentService;
import in.com.v2kart.core.exception.V2Exception;
import in.com.v2kart.core.payment.services.V2EncryptionService;
import in.com.v2kart.facades.payment.impl.V2PaymentFacadeImpl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * The Class CCAvenuePaymentFacadeImpl.
 * 
 * @author yamini2280
 */
public class CCAvenuePaymentFacadeImpl extends V2PaymentFacadeImpl implements CCAvenuePaymentFacade {

    private static final Logger LOG = Logger.getLogger(CCAvenuePaymentFacadeImpl.class);

    private CCAvenuePaymentService ccavenuePaymentService;

    private V2EncryptionService ccavenueEncryptionService;

    private Converter<CCAvenuePaymentRequest, PaymentData> ccavenuePaymentRequestDataConverter;

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.facades.payment.V2PaymentFacade#getPaymentInfo(java.lang.String)
     */
    @Override
    public Map<String, String> getPaymentInfo(final String txnId) {
        return getCcavenuePaymentService().getPaymentInfo(txnId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.ccavenuepaymentintegration.facades.CCAvenuePaymentFacade#beginHopCreatePayment(java.lang.String ,
     * de.hybris.platform.commercefacades.user.data.AddressData, java.lang.String)
     */
    @Override
    public PaymentData beginHopCreatePayment(final String enforcedPaymentMethod, final AddressData addressData, final String phoneNumber)
            throws V2Exception {
        final CustomerModel customerModel = getCurrentUserForCheckout();

        final CCAvenuePaymentRequest request = getCcavenuePaymentService().beginHopCreatePayment(customerModel, enforcedPaymentMethod,
                addressData, getPaymentUrl(getSiteConfigService().getProperty(CCAVENUE.HOP_POST_SUCCESS_URL)),
                getPaymentUrl(getSiteConfigService().getProperty(CCAVENUE.HOP_POST_CANCEL_URL)), phoneNumber);

        PaymentData paymentData = getCcavenuePaymentRequestDataConverter().convert(request);
        if (paymentData == null) {
            paymentData = new PaymentData();
            paymentData.setParameters(new HashMap<String, String>());
        }
        final HashMap<String, String> parameters = (HashMap<String, String>) paymentData.getParameters();
        parameters.put(CCAVENUE.RequestParameters.ENCRYPTED_REQUEST.getValue(), getCcavenueEncryptionService().getRequestHash(parameters));
        parameters.put(CCAVENUE.RequestParameters.ACCESS_CODE.getValue(), getSiteConfigService().getProperty(CCAVENUE.HOP_ACCESS_CODE));
        return paymentData;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.facades.payment.V2PaymentFacade#savePaymentInfo(java.util.Map)
     */
    @Override
    public void savePaymentInfo(final Map<String, String> parameters) {
        try
		{
			getCcavenuePaymentService().savePaymentInfo(parameters);
		}
		catch (Exception e)
		{
			LOG.error("Error Occured while saving payment information");
			e.printStackTrace();
		}

    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.ccavenuepaymentintegration.facades.CCAvenuePaymentFacade#completeHopCreatePayment(java.util .Map, boolean,
     * de.hybris.platform.core.model.order.CartModel)
     */
    @Override
    public PaymentSubscriptionResultData completeHopCreatePayment(final Map<String, String> parameters, final boolean saveInAccount,
            final CartModel cartModel) throws V2Exception {
        final String transactionId = parameters.get(CCAVENUE.ResponseParameters.ORDER_ID.getValue());
        if (!transactionId.contains(cartModel.getCode())) {
            LOG.error("Transaction id does not match for transaction " + transactionId);
            throw new V2Exception("Transaction id does not match!!");
        }
        final CustomerModel customerModel = getCurrentUserForCheckout();

        final CCAvenuePaymentResponse response = getCcavenuePaymentService().capture(customerModel, parameters, cartModel);

        final PaymentSubscriptionResultItem paymentSubscriptionResultItem = new PaymentSubscriptionResultItem();
        paymentSubscriptionResultItem.setSuccess(CCAvenueDecisionEnum.SUCCESS.getValue().equalsIgnoreCase(response.getOrderStatus()));
        paymentSubscriptionResultItem.setDecision(response.getStatusMessage());
        paymentSubscriptionResultItem.setResultCode(response.getStatusCode());
        return getPaymentSubscriptionResultDataConverter().convert(paymentSubscriptionResultItem);

    }

    /**
     * Gets the ccavenue payment service.
     * 
     * @return the ccavenuePaymentService
     */
    public CCAvenuePaymentService getCcavenuePaymentService() {
        return ccavenuePaymentService;
    }

    /**
     * Sets the ccavenue payment service.
     * 
     * @param ccavenuePaymentService
     *        the ccavenuePaymentService to set
     */
    public void setCcavenuePaymentService(final CCAvenuePaymentService ccavenuePaymentService) {
        this.ccavenuePaymentService = ccavenuePaymentService;
    }

    /**
     * Gets the ccavenue encryption service.
     * 
     * @return the ccavenueEncryptionService
     */
    public V2EncryptionService getCcavenueEncryptionService() {
        return ccavenueEncryptionService;
    }

    /**
     * Sets the ccavenue encryption service.
     * 
     * @param ccavenueEncryptionService
     *        the ccavenueEncryptionService to set
     */
    public void setCcavenueEncryptionService(final V2EncryptionService ccavenueEncryptionService) {
        this.ccavenueEncryptionService = ccavenueEncryptionService;
    }

    /**
     * Gets the ccavenue payment request data converter.
     * 
     * @return the ccavenuePaymentRequestDataConverter
     */
    public Converter<CCAvenuePaymentRequest, PaymentData> getCcavenuePaymentRequestDataConverter() {
        return ccavenuePaymentRequestDataConverter;
    }

    /**
     * Sets the ccavenue payment request data converter.
     * 
     * @param ccavenuePaymentRequestDataConverter
     *        the ccavenuePaymentRequestDataConverter to set
     */
    public void setCcavenuePaymentRequestDataConverter(
            final Converter<CCAvenuePaymentRequest, PaymentData> ccavenuePaymentRequestDataConverter) {
        this.ccavenuePaymentRequestDataConverter = ccavenuePaymentRequestDataConverter;
    }

}
