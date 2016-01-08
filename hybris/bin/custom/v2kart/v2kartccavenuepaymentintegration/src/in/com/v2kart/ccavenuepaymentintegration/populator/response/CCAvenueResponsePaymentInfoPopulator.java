/**
 *
 */
package in.com.v2kart.ccavenuepaymentintegration.populator.response;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.response.AbstractResultPopulator;
import de.hybris.platform.acceleratorservices.payment.data.CustomerBillToData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import in.com.v2kart.ccavenuepaymentintegration.constants.PaymentConstants.PaymentProperties.CCAVENUE;
import in.com.v2kart.ccavenuepaymentintegration.data.response.CCAvenuePaymentResponse;

import java.util.Map;

/**
 * The Class CCAvenueResponsePaymentInfoPopulator.
 *
 * @author yamini2280
 */
public class CCAvenueResponsePaymentInfoPopulator extends AbstractResultPopulator<Map<String, String>, CCAvenuePaymentResponse> {

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
     */
    @Override
    public void populate(final Map<String, String> source, final CCAvenuePaymentResponse target) throws ConversionException {
        validateParameterNotNull(source, "Parameter [Map<String, String>] source cannot be null");
        validateParameterNotNull(target, "Parameter [CCAvenueResponsePaymentInfoPopulator] target cannot be null");

        target.setAmount(source.get(CCAVENUE.ResponseParameters.AMOUNT.getValue()));
        target.setBankRefNo(source.get(CCAVENUE.ResponseParameters.BANK_REFERENCE_NUMBER.getValue()));
        target.setCardName(source.get(CCAVENUE.ResponseParameters.CARD_NAME.getValue()));
        target.setCurrency(source.get(CCAVENUE.ResponseParameters.CURRENCY.getValue()));
        target.setTxnid(source.get(CCAVENUE.ResponseParameters.ORDER_ID.getValue()));
        target.setOrderStatus(source.get(CCAVENUE.ResponseParameters.ORDER_STATUS.getValue()));
        target.setPaymentMode(source.get(CCAVENUE.ResponseParameters.PAYMENT_MODE.getValue()));
        target.setTrackingId(source.get(CCAVENUE.ResponseParameters.TRACKING_ID.getValue()));
        target.setOfferType(source.get(CCAVENUE.ResponseParameters.OFFER_TYPE.getValue()));
        target.setOfferCode(source.get(CCAVENUE.ResponseParameters.OFFER_CODE.getValue()));
        target.setVault(source.get(CCAVENUE.ResponseParameters.VAULT.getValue()));
        target.setDiscountValue(source.get(CCAVENUE.ResponseParameters.DISCOUNT_VALUE.getValue()));

        target.setStatusCode(source.get(CCAVENUE.ResponseParameters.STATUS_CODE.getValue()));
        target.setStatusMessage(source.get(CCAVENUE.ResponseParameters.STATUS_MESSAGE.getValue()));
        target.setFailureMessage(source.get(CCAVENUE.ResponseParameters.FAILURE_MESSAGE.getValue()));

        target.setMerchantParam1(source.get(CCAVENUE.ResponseParameters.MERCHANT_PARAM_1.getValue()));
        target.setMerchantParam2(source.get(CCAVENUE.ResponseParameters.MERCHANT_PARAM_2.getValue()));
        target.setMerchantParam3(source.get(CCAVENUE.ResponseParameters.MERCHANT_PARAM_3.getValue()));
        target.setMerchantParam4(source.get(CCAVENUE.ResponseParameters.MERCHANT_PARAM_4.getValue()));
        target.setMerchantParam5(source.get(CCAVENUE.ResponseParameters.MERCHANT_PARAM_5.getValue()));

        target.setCustomerBillToData(getCustomerBillToData(source));
    }

    /**
     * Gets the customer bill to data.
     * 
     * @param source
     *        the source
     * @return the customer bill to data
     */
    private CustomerBillToData getCustomerBillToData(final Map<String, String> source) {
        final CustomerBillToData billToData = new CustomerBillToData();
        billToData.setBillToCity(source.get(CCAVENUE.ResponseParameters.BILLING_CITY.getValue()));
        billToData.setBillToCountry(source.get(CCAVENUE.ResponseParameters.BILLING_COUNTRY.getValue()));
        billToData.setBillToEmail(source.get(CCAVENUE.ResponseParameters.BILLING_EMAIL.getValue()));
        billToData.setBillToFirstName(source.get(CCAVENUE.ResponseParameters.BILLING_NAME.getValue()));
        billToData.setBillToPhoneNumber(source.get(CCAVENUE.ResponseParameters.BILLING_TELEPHONE.getValue()));
        billToData.setBillToPostalCode(source.get(CCAVENUE.ResponseParameters.BILLING_ZIP.getValue()));
        billToData.setBillToState(source.get(CCAVENUE.ResponseParameters.BILLING_STATE.getValue()));
        billToData.setBillToStreet1(source.get(CCAVENUE.ResponseParameters.BILLING_ADDRESS.getValue()));
        return billToData;
    }

}
