/**
 * 
 */
package in.com.v2kart.v2kartebspaymentintegration.populator.response;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.response.AbstractResultPopulator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import in.com.v2kart.v2kartebspaymentintegration.constants.PaymentConstants.PaymentProperties.EBS;
import in.com.v2kart.v2kartebspaymentintegration.data.response.EBSPaymentResponse;

import java.util.Map;

/**
 * @author vikrant2480
 * 
 */
public class EBSResponsePaymentInfoPopulator extends AbstractResultPopulator<Map<String, String>, EBSPaymentResponse> {

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
     */
    @Override
    public void populate(final Map<String, String> source, final EBSPaymentResponse target) throws ConversionException {
        validateParameterNotNull(source, "Parameter [Map<String, String>] source cannot be null");
        validateParameterNotNull(target, "Parameter [EBSResponsePaymentInfoPopulator] target cannot be null");

        target.setResponseCode(source.get(EBS.ResponseParameters.RESPONSE_CODE.getValue()));
        target.setResponseMessage(source.get(EBS.ResponseParameters.RESPONSE_MESSAGE.getValue()));
        target.setDateCreated(source.get(EBS.ResponseParameters.DATE_CREATED.getValue()));
        target.setPaymentId(source.get(EBS.ResponseParameters.PAYMENT_ID.getValue()));
        target.setMerchantReferenceNo(source.get(EBS.ResponseParameters.MERCHANT_REFERENCE_NUMBER.getValue()));
        target.setMode(source.get(EBS.ResponseParameters.MODE.getValue()));
        target.setName(source.get(EBS.ResponseParameters.BILLING_NAME.getValue()));
        target.setAddress(source.get(EBS.ResponseParameters.BILLING_ADDRESS.getValue()));
        target.setCity(source.get(EBS.ResponseParameters.BILLING_CITY.getValue()));
        target.setState(source.get(EBS.ResponseParameters.BILLING_STATE.getValue()));
        target.setPostalCode(source.get(EBS.ResponseParameters.BILLING_POSTAL_CODE.getValue()));
        target.setCountry(source.get(EBS.ResponseParameters.BILLING_COUNTRY.getValue()));
        target.setPhone(source.get(EBS.ResponseParameters.BILLING_PHONE.getValue()));
        target.setEmail(source.get(EBS.ResponseParameters.BILLING_EMAIL.getValue()));
        target.setTxnid(source.get(EBS.ResponseParameters.TRANSACTION_ID.getValue()));
        target.setIsFlagged(source.get(EBS.ResponseParameters.IS_FLAGGED.getValue()));
        target.setDescripton(source.get(EBS.ResponseParameters.DESCRIPTION.getValue()));
        target.setPaymentMode(EBS.ResponseParameters.PAYMENT_METHOD.getValue());
        target.setAmount(source.get(EBS.ResponseParameters.AMOUNT.getValue()));
    }
}
