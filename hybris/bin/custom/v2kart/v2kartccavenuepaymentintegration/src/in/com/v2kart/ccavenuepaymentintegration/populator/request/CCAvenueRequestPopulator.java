/**
 *
 */
package in.com.v2kart.ccavenuepaymentintegration.populator.request;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.request.AbstractRequestPopulator;
import de.hybris.platform.acceleratorservices.payment.data.CustomerBillToData;
import de.hybris.platform.acceleratorservices.payment.data.CustomerShipToData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import in.com.v2kart.ccavenuepaymentintegration.constants.PaymentConstants.PaymentProperties.CCAVENUE;
import in.com.v2kart.ccavenuepaymentintegration.data.request.CCAvenuePaymentRequest;

/**
 * The Class CCAvenueRequestPopulator.
 *
 * @author yamini2280
 */
public class CCAvenueRequestPopulator extends AbstractRequestPopulator<CCAvenuePaymentRequest, PaymentData> {

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
     */
    @Override
    public void populate(final CCAvenuePaymentRequest source, final PaymentData target) throws ConversionException {
        validateParameterNotNull(source, "Parameter [CCAvenuePaymentRequest] source cannot be null");
        validateParameterNotNull(target, "Parameter [PaymentData] target cannot be null");
        validateParameterNotNull(source.getCustomerBillToData(), "customerBillToData cannot be null");

        addRequestQueryParam(target, CCAVENUE.RequestParameters.MERCHANT_ID.getValue(), source.getMerchantId());
        addRequestQueryParam(target, CCAVENUE.RequestParameters.REDIRECT_URL.getValue(), source.getRedirectUrl());
        addRequestQueryParam(target, CCAVENUE.RequestParameters.CANCEL_URL.getValue(), source.getCancelUrl());
        addRequestQueryParam(target, CCAVENUE.RequestParameters.AMOUNT.getValue(), source.getAmount());
        addRequestQueryParam(target, CCAVENUE.RequestParameters.ORDER_ID.getValue(), source.getOrderId());
        addRequestQueryParam(target, CCAVENUE.RequestParameters.CURRENCY.getValue(), source.getCurrency());
        addRequestQueryParam(target, CCAVENUE.RequestParameters.LANGUAGE.getValue(), source.getLanguage());
        addRequestQueryParam(target, CCAVENUE.RequestParameters.PAYMENT_OPTION.getValue(), source.getPaymentOption());
        populateBillingInfo(source.getCustomerBillToData(), target);
        populateShippingInfo(source.getCustomerShipToData(), target);
        addRequestQueryParam(target, CCAVENUE.RequestParameters.MERCHANT_PARAM_1.getValue(), source.getMerchantParam1());
        addRequestQueryParam(target, CCAVENUE.RequestParameters.MERCHANT_PARAM_2.getValue(), source.getMerchantParam2());
        addRequestQueryParam(target, CCAVENUE.RequestParameters.MERCHANT_PARAM_3.getValue(), source.getMerchantParam3());
        addRequestQueryParam(target, CCAVENUE.RequestParameters.MERCHANT_PARAM_4.getValue(), source.getMerchantParam4());
        addRequestQueryParam(target, CCAVENUE.RequestParameters.MERCHANT_PARAM_5.getValue(), source.getMerchantParam5());

        target.setPostUrl(source.getRequestUrl());

    }

    /**
     * Populate shipping info.
     *
     * @param source
     *        the customer ship to data
     * @param target
     *        the target
     */
    private void populateShippingInfo(final CustomerShipToData source, final PaymentData target) {
        validateParameterNotNull(target, "Parameter [PaymentData] target cannot be null");
        if (null != source) {
            addRequestQueryParam(target, CCAVENUE.RequestParameters.DELIVERY_CITY.getValue(), source.getShipToCity());
            addRequestQueryParam(target, CCAVENUE.RequestParameters.DELIVERY_COUNTRY.getValue(), source.getShipToCountry());
            addRequestQueryParam(target, CCAVENUE.RequestParameters.DELIVERY_NAME.getValue(), source.getShipToFirstName());
            addRequestQueryParam(target, CCAVENUE.RequestParameters.DELIVERY_TELEPHONE.getValue(), source.getShipToPhoneNumber());
            addRequestQueryParam(target, CCAVENUE.RequestParameters.DELIVERY_ZIP.getValue(), source.getShipToPostalCode());
            addRequestQueryParam(target, CCAVENUE.RequestParameters.DELIVERY_STATE.getValue(), source.getShipToState());
            addRequestQueryParam(target, CCAVENUE.RequestParameters.DELIVERY_ADDRESS.getValue(),
                    source.getShipToStreet1() + " " + source.getShipToStreet2());
        }
    }

    /**
     * Populate billing info.
     *
     * @param source
     *        the customer bill to data
     * @param target
     *        the target
     */
    private void populateBillingInfo(final CustomerBillToData source, final PaymentData target) {
        validateParameterNotNull(source, "Parameter [CustomerBillToData] source cannot be null");
        validateParameterNotNull(target, "Parameter [PaymentData] target cannot be null");

        addRequestQueryParam(target, CCAVENUE.RequestParameters.BILLING_CITY.getValue(), source.getBillToCity());
        addRequestQueryParam(target, CCAVENUE.RequestParameters.BILLING_COUNTRY.getValue(), source.getBillToCountry());
        addRequestQueryParam(target, CCAVENUE.RequestParameters.BILLING_EMAIL.getValue(), source.getBillToEmail());
        addRequestQueryParam(target, CCAVENUE.RequestParameters.BILLING_NAME.getValue(),
                source.getBillToFirstName() + " " + source.getBillToLastName());
        addRequestQueryParam(target, CCAVENUE.RequestParameters.BILLING_TELEPHONE.getValue(), source.getBillToPhoneNumber());
        addRequestQueryParam(target, CCAVENUE.RequestParameters.BILLING_ZIP.getValue(), source.getBillToPostalCode());
        addRequestQueryParam(target, CCAVENUE.RequestParameters.BILLING_STATE.getValue(), source.getBillToState());
        addRequestQueryParam(target, CCAVENUE.RequestParameters.BILLING_ADDRESS.getValue(),
                source.getBillToStreet1() + " " + source.getBillToStreet2());

    }
}
