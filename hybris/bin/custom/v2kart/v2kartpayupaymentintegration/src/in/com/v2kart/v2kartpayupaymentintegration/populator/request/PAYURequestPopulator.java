/**
 *
 */
package in.com.v2kart.v2kartpayupaymentintegration.populator.request;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.request.AbstractRequestPopulator;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import in.com.v2kart.v2kartpayupaymentintegration.constants.PaymentConstants.PaymentProperties.PAYU;
import in.com.v2kart.v2kartpayupaymentintegration.data.request.PAYUPaymentRequest;

/**
 * The Class PAYURequestPopulator.
 *
 * @author gaurav2007
 */
public class PAYURequestPopulator extends AbstractRequestPopulator<PAYUPaymentRequest, PaymentData> {

    /*
     * (non-Javadoc)
     *
     * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
     */
    @Override
    public void populate(final PAYUPaymentRequest source, final PaymentData target) throws ConversionException {
        validateParameterNotNull(source, "Parameter [PAYUPaymentRequest] source cannot be null");
        validateParameterNotNull(target, "Parameter [PaymentData] target cannot be null");

        addRequestQueryParam(target, PAYU.RequestParameters.KEY.getValue(), source.getMerchantKey());
        addRequestQueryParam(target, PAYU.RequestParameters.TRANSACTION_ID.getValue(), source.getTransactionId());
        addRequestQueryParam(target, PAYU.RequestParameters.AMOUNT.getValue(), source.getAmount());
        addRequestQueryParam(target, PAYU.RequestParameters.PRODUCT_INFO.getValue(), source.getOrderDescription());
        addRequestQueryParam(target, PAYU.RequestParameters.FIRST_NAME.getValue(), source.getFirstName());
        addRequestQueryParam(target, PAYU.RequestParameters.EMAIL.getValue(), source.getEmail());
        addRequestQueryParam(target, PAYU.RequestParameters.PHONE.getValue(), source.getPhone());
        addRequestQueryParam(target, PAYU.RequestParameters.SUCCESS_URL.getValue(), source.getSuccessUrl());
        addRequestQueryParam(target, PAYU.RequestParameters.FAILURE_URL.getValue(), source.getFailureUrl());
        addRequestQueryParam(target, PAYU.RequestParameters.CANCEL_URL.getValue(), source.getCancelUrl());
        addRequestQueryParam(target, PAYU.RequestParameters.ENFORCE_PAYMENT_METHOD.getValue(), source.getEnforcedPaymentMethod());
        addRequestQueryParam(target, PAYU.RequestParameters.SALT.getValue(), source.getSalt());
        addRequestQueryParam(target, PAYU.RequestParameters.PG.getValue(), source.getPg());
        addRequestQueryParam(target, PAYU.RequestParameters.BANK_CODE.getValue(), source.getEnforcedPaymentMethod());
        addRequestQueryParam(target, PAYU.RequestParameters.UDF_1.getValue(), source.getUdf1());
        addRequestQueryParam(target, PAYU.RequestParameters.UDF_2.getValue(), source.getUdf2());
        addRequestQueryParam(target, PAYU.RequestParameters.UDF_3.getValue(), source.getUdf3());
        addRequestQueryParam(target, PAYU.RequestParameters.UDF_4.getValue(), source.getUdf4());
        addRequestQueryParam(target, PAYU.RequestParameters.UDF_5.getValue(), source.getUdf5());
        addRequestQueryParam(target, PAYU.RequestParameters.UDF_6.getValue(), source.getUdf6());
        addRequestQueryParam(target, PAYU.RequestParameters.UDF_7.getValue(), source.getUdf7());
        addRequestQueryParam(target, PAYU.RequestParameters.UDF_8.getValue(), source.getUdf8());
        addRequestQueryParam(target, PAYU.RequestParameters.UDF_9.getValue(), source.getUdf9());
        addRequestQueryParam(target, PAYU.RequestParameters.UDF_10.getValue(), source.getUdf10());
        target.setPostUrl(source.getRequestUrl());

    }
}
