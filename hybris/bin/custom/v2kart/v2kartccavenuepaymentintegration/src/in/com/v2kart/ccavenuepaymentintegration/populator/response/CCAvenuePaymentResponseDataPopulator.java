/**
 * 
 */
package in.com.v2kart.ccavenuepaymentintegration.populator.response;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.request.AbstractRequestPopulator;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import in.com.v2kart.ccavenuepaymentintegration.constants.PaymentConstants.PaymentProperties.CCAVENUE;
import in.com.v2kart.core.model.CCAvenuePaymentResponseInfoModel;


/**
 * The Class CCAvenuePaymentResponseDataPopulator.
 * 
 * @author yamini2280
 */
public class CCAvenuePaymentResponseDataPopulator extends AbstractRequestPopulator<CCAvenuePaymentResponseInfoModel, PaymentData>
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final CCAvenuePaymentResponseInfoModel source, final PaymentData target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter [CCAvenuePaymentResponseInfoModel] source cannot be null");
		validateParameterNotNull(target, "Parameter [PaymentData] target cannot be null");

		addRequestQueryParam(target, CCAVENUE.ResponseParameters.STATUS_CODE.getValue(), source.getStatusCode());
		addRequestQueryParam(target, CCAVENUE.ResponseParameters.STATUS_MESSAGE.getValue(), source.getStatusMessage());
		addRequestQueryParam(target, CCAVENUE.ResponseParameters.FAILURE_MESSAGE.getValue(), source.getFailureMessage());

		addRequestQueryParam(target, CCAVENUE.ResponseParameters.AMOUNT.getValue(), source.getAmount());
		addRequestQueryParam(target, CCAVENUE.ResponseParameters.BANK_REFERENCE_NUMBER.getValue(), source.getBankRefNo());
		addRequestQueryParam(target, CCAVENUE.ResponseParameters.CARD_NAME.getValue(), source.getCardName());
		addRequestQueryParam(target, CCAVENUE.ResponseParameters.CURRENCY.getValue(), source.getCurrency());
		addRequestQueryParam(target, CCAVENUE.ResponseParameters.ORDER_ID.getValue(), source.getTxnid());
		addRequestQueryParam(target, CCAVENUE.ResponseParameters.ORDER_STATUS.getValue(), source.getOrderStatus());
		addRequestQueryParam(target, CCAVENUE.ResponseParameters.PAYMENT_MODE.getValue(), source.getPaymentMode());
		addRequestQueryParam(target, CCAVENUE.ResponseParameters.TRACKING_ID.getValue(), source.getTrackingId());
		addRequestQueryParam(target, CCAVENUE.ResponseParameters.OFFER_TYPE.getValue(), source.getOfferType());
		addRequestQueryParam(target, CCAVENUE.ResponseParameters.OFFER_CODE.getValue(), source.getOfferCode());
		addRequestQueryParam(target, CCAVENUE.ResponseParameters.VAULT.getValue(), source.getVault());
		addRequestQueryParam(target, CCAVENUE.ResponseParameters.DISCOUNT_VALUE.getValue(), source.getDiscountValue());

		addRequestQueryParam(target, CCAVENUE.ResponseParameters.MERCHANT_PARAM_1.getValue(), source.getMerchantParam1());
		addRequestQueryParam(target, CCAVENUE.ResponseParameters.MERCHANT_PARAM_2.getValue(), source.getMerchantParam2());
		addRequestQueryParam(target, CCAVENUE.ResponseParameters.MERCHANT_PARAM_3.getValue(), source.getMerchantParam3());
		addRequestQueryParam(target, CCAVENUE.ResponseParameters.MERCHANT_PARAM_4.getValue(), source.getMerchantParam4());
		addRequestQueryParam(target, CCAVENUE.ResponseParameters.MERCHANT_PARAM_5.getValue(), source.getMerchantParam5());

	}
}
