/**
 *
 */
package in.com.v2kart.v2kartpayupaymentintegration.populator.response;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.response.AbstractResultPopulator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import in.com.v2kart.v2kartpayupaymentintegration.constants.PaymentConstants.PaymentProperties.PAYU;
import in.com.v2kart.v2kartpayupaymentintegration.data.response.PAYUPaymentResponse;

import java.util.Map;

/**
 * The Class PAYUResponsePaymentInfoPopulator.
 *
 * @author gaurav2007
 */
public class PAYUResponsePaymentInfoPopulator extends AbstractResultPopulator<Map<String, String>, PAYUPaymentResponse> {

    /*
     * (non-Javadoc)
     *
     * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
     */
    @Override
    public void populate(final Map<String, String> source, final PAYUPaymentResponse target) throws ConversionException {
        validateParameterNotNull(source, "Parameter [Map<String, String>] source cannot be null");
        validateParameterNotNull(target, "Parameter [PAYUPaymentResponse] target cannot be null");

        target.setStatus(source.get(PAYU.ResponseParameters.HASH.getValue()));
        target.setAmount(source.get(PAYU.ResponseParameters.AMOUNT.getValue()));
        target.setTxnid(source.get(PAYU.ResponseParameters.TRANSACTION_ID.getValue()));
        target.setEmail(source.get(PAYU.ResponseParameters.EMAIL.getValue()));
        target.setErrorMessage(source.get(PAYU.ResponseParameters.FIRST_NAME.getValue()));
        target.setKey(source.get(PAYU.ResponseParameters.KEY.getValue()));
        target.setProductInfo(source.get(PAYU.ResponseParameters.PRODUCT_INFO.getValue()));
        target.setStatus(source.get(PAYU.ResponseParameters.STATUS.getValue()));

        target.setAddedon(source.get(PAYU.ResponseParameters.ADD_ON.getValue()));
        target.setBankcode(source.get(PAYU.ResponseParameters.BANK_CODE.getValue()));
        target.setBankRefNumber(source.get(PAYU.ResponseParameters.BANK_REFERENCE_NUMBER.getValue()));
        target.setCardnum(source.get(PAYU.ResponseParameters.CARD_NUMBER.getValue()));
        target.setDiscount(source.get(PAYU.ResponseParameters.DISCOUNT.getValue()));
        target.setError(source.get(PAYU.ResponseParameters.ERROR.getValue()));
        target.setErrorMessage(source.get(PAYU.ResponseParameters.ERROR_MESSAGE.getValue()));
        target.setMihpayid(source.get(PAYU.ResponseParameters.MIH_PAY_ID.getValue()));
        target.setMode(source.get(PAYU.ResponseParameters.MODE.getValue()));
        target.setNameOnCard(source.get(PAYU.ResponseParameters.NAME_ON_CARD.getValue()));
        target.setNetAmountDebit(source.get(PAYU.ResponseParameters.NET_AMOUNT_DEBIT.getValue()));
        target.setPaymentSource(source.get(PAYU.ResponseParameters.PAYMENT_SOURCE.getValue()));
        target.setPgType(source.get(PAYU.ResponseParameters.PG_TYPE.getValue()));
        target.setPhone(source.get(PAYU.ResponseParameters.PHONE.getValue()));
        target.setUnmappedStatus(source.get(PAYU.ResponseParameters.UNMAPPED_STATUS.getValue()));

        target.setUdf1(source.get(PAYU.ResponseParameters.UDF_1.getValue()));
        target.setUdf2(source.get(PAYU.ResponseParameters.UDF_2.getValue()));
        target.setUdf3(source.get(PAYU.ResponseParameters.UDF_3.getValue()));
        target.setUdf4(source.get(PAYU.ResponseParameters.UDF_4.getValue()));
        target.setUdf5(source.get(PAYU.ResponseParameters.UDF_5.getValue()));
    }
}
