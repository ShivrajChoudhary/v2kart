/**
 *
 */
package in.com.v2kart.v2kartpayupaymentintegration.populator.response;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.request.AbstractRequestPopulator;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import in.com.v2kart.core.model.PAYUPaymentResponseInfoModel;
import in.com.v2kart.v2kartpayupaymentintegration.constants.PaymentConstants.PaymentProperties.PAYU;

/**
 * The Class PAYUPaymentResponseDataPopulator.
 *
 * @author gaurav2007
 */
public class PAYUPaymentResponseDataPopulator extends AbstractRequestPopulator<PAYUPaymentResponseInfoModel, PaymentData> {

    /*
     * (non-Javadoc)
     *
     * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
     */
    @Override
    public void populate(final PAYUPaymentResponseInfoModel source, final PaymentData target) throws ConversionException {
        validateParameterNotNull(source, "Parameter [PAYUPaymentResponseInfoModel] source cannot be null");
        validateParameterNotNull(target, "Parameter [PaymentData] target cannot be null");

        addRequestQueryParam(target, PAYU.ResponseParameters.AMOUNT.getValue(), source.getAmount());
        addRequestQueryParam(target, PAYU.ResponseParameters.TRANSACTION_ID.getValue(), source.getTxnid());

        addRequestQueryParam(target, PAYU.ResponseParameters.HASH.getValue(), source.getHash());
        addRequestQueryParam(target, PAYU.ResponseParameters.ADD_ON.getValue(), source.getAddedon());
        addRequestQueryParam(target, PAYU.ResponseParameters.BANK_CODE.getValue(), source.getBankcode());
        addRequestQueryParam(target, PAYU.ResponseParameters.BANK_REFERENCE_NUMBER.getValue(), source.getBankRefNumber());
        addRequestQueryParam(target, PAYU.ResponseParameters.CARD_NUMBER.getValue(), source.getCardnum());
        addRequestQueryParam(target, PAYU.ResponseParameters.DISCOUNT.getValue(), source.getDiscount());
        addRequestQueryParam(target, PAYU.ResponseParameters.ERROR.getValue(), source.getError());
        addRequestQueryParam(target, PAYU.ResponseParameters.ERROR_MESSAGE.getValue(), source.getErrorMessage());
        addRequestQueryParam(target, PAYU.ResponseParameters.MIH_PAY_ID.getValue(), source.getMihpayid());
        addRequestQueryParam(target, PAYU.ResponseParameters.MODE.getValue(), source.getMode());
        addRequestQueryParam(target, PAYU.ResponseParameters.NAME_ON_CARD.getValue(), source.getNameOnCard());
        addRequestQueryParam(target, PAYU.ResponseParameters.NET_AMOUNT_DEBIT.getValue(), source.getNetAmountDebit());
        addRequestQueryParam(target, PAYU.ResponseParameters.PAYMENT_SOURCE.getValue(), source.getPaymentSource());
        addRequestQueryParam(target, PAYU.ResponseParameters.PG_TYPE.getValue(), source.getPgType());
        addRequestQueryParam(target, PAYU.ResponseParameters.STATUS.getValue(), source.getStatus());
        addRequestQueryParam(target, PAYU.ResponseParameters.UNMAPPED_STATUS.getValue(), source.getUnmappedStatus());

        addRequestQueryParam(target, PAYU.ResponseParameters.KEY.getValue(), source.getKey());
        addRequestQueryParam(target, PAYU.ResponseParameters.PRODUCT_INFO.getValue(), source.getProductInfo());
        addRequestQueryParam(target, PAYU.ResponseParameters.FIRST_NAME.getValue(), source.getFirstname());
        addRequestQueryParam(target, PAYU.ResponseParameters.EMAIL.getValue(), source.getEmail());
        addRequestQueryParam(target, PAYU.ResponseParameters.PHONE.getValue(), source.getPhone());

        addRequestQueryParam(target, PAYU.ResponseParameters.UDF_1.getValue(), source.getUdf1());
        addRequestQueryParam(target, PAYU.ResponseParameters.UDF_2.getValue(), source.getUdf2());
        addRequestQueryParam(target, PAYU.ResponseParameters.UDF_3.getValue(), source.getUdf3());
        addRequestQueryParam(target, PAYU.ResponseParameters.UDF_4.getValue(), source.getUdf4());
        addRequestQueryParam(target, PAYU.ResponseParameters.UDF_5.getValue(), source.getUdf5());

    }
}
