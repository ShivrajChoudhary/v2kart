/**
 * 
 */
package in.com.v2kart.v2kartebspaymentintegration.populator.request;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.request.AbstractRequestPopulator;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import in.com.v2kart.v2kartebspaymentintegration.constants.PaymentConstants.PaymentProperties.EBS;
import in.com.v2kart.v2kartebspaymentintegration.data.request.EBSPaymentRequest;

/**
 * @author vikrant2480
 * 
 */
public class EBSRequestPopulator extends AbstractRequestPopulator<EBSPaymentRequest, PaymentData> {

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
     */
    @Override
    public void populate(final EBSPaymentRequest source, final PaymentData target) throws ConversionException {

        validateParameterNotNull(source, "Parameter [EBSPaymentRequest] source cannot be null");
        validateParameterNotNull(target, "Parameter [PaymentData] target cannot be null");

        addRequestQueryParam(target, EBS.RequestParameters.ACCOUNT_ID.getValue(), source.getAccountId());
        addRequestQueryParam(target, EBS.RequestParameters.REFERENCE_NUMBER.getValue(), source.getReferenceNumber());
        addRequestQueryParam(target, EBS.RequestParameters.AMOUNT.getValue(), source.getAmount());
        addRequestQueryParam(target, EBS.RequestParameters.MODE.getValue(), source.getMode());
        addRequestQueryParam(target, EBS.RequestParameters.DESCRIPTION.getValue(), source.getDescription());
        addRequestQueryParam(target, EBS.RequestParameters.RETURN_URL.getValue(), source.getReturnUrl());
        addRequestQueryParam(target, EBS.RequestParameters.BILLING_NAME.getValue(), source.getBillingName());
        addRequestQueryParam(target, EBS.RequestParameters.BILLING_ADDRESS.getValue(), source.getBillingAddress());
        addRequestQueryParam(target, EBS.RequestParameters.BILLING_CITY.getValue(), source.getBillingCity());
        addRequestQueryParam(target, EBS.RequestParameters.BILLING_STATE.getValue(), source.getBillingState());
        addRequestQueryParam(target, EBS.RequestParameters.BILLING_COUNTRY.getValue(), source.getBillingCountry());
        addRequestQueryParam(target, EBS.RequestParameters.BILLING_POSTAL_CODE.getValue(), source.getBillingPostalCode());
        addRequestQueryParam(target, EBS.RequestParameters.BILLING_PHONE.getValue(), source.getBillingPhoneNumber());
        addRequestQueryParam(target, EBS.RequestParameters.EMAIL.getValue(), source.getBillingEmailId());
        addRequestQueryParam(target, EBS.RequestParameters.KEY.getValue(), source.getKey());

        addRequestQueryParam(target, EBS.RequestParameters.PAYMENT_ID.getValue(), source.getPaymentId());

        target.setPostUrl(source.getRequestUrl());
    }

}
