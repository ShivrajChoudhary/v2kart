/**
 *
 */
package in.com.v2kart.ccavenuepaymentintegration.data.request;

import de.hybris.platform.acceleratorservices.payment.data.CustomerBillToData;
import de.hybris.platform.acceleratorservices.payment.data.CustomerShipToData;
import de.hybris.platform.acceleratorservices.payment.data.HostedOrderPageRequest;

/**
 * The Class CCAvenuePaymentRequest.
 *
 * @author yamini2280
 */
public class CCAvenuePaymentRequest extends HostedOrderPageRequest {

    private CustomerShipToData customerShipToData;

    private CustomerBillToData customerBillToData;

    private String merchantId;

    private String amount;

    private String orderId;

    private String redirectUrl;

    private String cancelUrl;

    private String currency;

    private String language;

    private String paymentOption;

    private String merchantParam1;

    private String merchantParam2;

    private String merchantParam3;

    private String merchantParam4;

    private String merchantParam5;

    /**
     * Gets the customer ship to data.
     *
     * @return the customer ship to data
     */
    public final CustomerShipToData getCustomerShipToData() {
        return customerShipToData;
    }

    /**
     * Sets the customer ship to data.
     *
     * @param customerShipToData
     *        the new customer ship to data
     */
    public final void setCustomerShipToData(final CustomerShipToData customerShipToData) {
        this.customerShipToData = customerShipToData;
    }

    /**
     * Gets the customer bill to data.
     *
     * @return the customer bill to data
     */
    public final CustomerBillToData getCustomerBillToData() {
        return customerBillToData;
    }

    /**
     * Sets the customer bill to data.
     *
     * @param customerBillToData
     *        the new customer bill to data
     */
    public final void setCustomerBillToData(final CustomerBillToData customerBillToData) {
        this.customerBillToData = customerBillToData;
    }

    /**
     * Gets the merchant id.
     *
     * @return the merchant id
     */
    public final String getMerchantId() {
        return merchantId;
    }

    /**
     * Sets the merchant id.
     *
     * @param merchantId
     *        the new merchant id
     */
    public final void setMerchantId(final String merchantId) {
        this.merchantId = merchantId;
    }

    /**
     * Gets the amount.
     *
     * @return the amount
     */
    public final String getAmount() {
        return amount;
    }

    /**
     * Sets the amount.
     *
     * @param amount
     *        the new amount
     */
    public final void setAmount(final String amount) {
        this.amount = amount;
    }

    /**
     * Gets the order id.
     *
     * @return the order id
     */
    public final String getOrderId() {
        return orderId;
    }

    /**
     * Sets the order id.
     *
     * @param orderId
     *        the new order id
     */
    public final void setOrderId(final String orderId) {
        this.orderId = orderId;
    }

    /**
     * Gets the redirect url.
     *
     * @return the redirect url
     */
    public final String getRedirectUrl() {
        return redirectUrl;
    }

    /**
     * Sets the redirect url.
     *
     * @param redirectUrl
     *        the new redirect url
     */
    public final void setRedirectUrl(final String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    /**
     * Gets the cancel url.
     *
     * @return the cancel url
     */
    public final String getCancelUrl() {
        return cancelUrl;
    }

    /**
     * Sets the cancel url.
     *
     * @param cancelUrl
     *        the new cancel url
     */
    public final void setCancelUrl(final String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }

    /**
     * Gets the currency.
     *
     * @return the currency
     */
    public final String getCurrency() {
        return currency;
    }

    /**
     * Sets the currency.
     *
     * @param currency
     *        the new currency
     */
    public final void setCurrency(final String currency) {
        this.currency = currency;
    }

    /**
     * Gets the language.
     *
     * @return the language
     */
    public final String getLanguage() {
        return language;
    }

    /**
     * Sets the language.
     *
     * @param language
     *        the new language
     */
    public final void setLanguage(final String language) {
        this.language = language;
    }

    /**
     * @return the paymentOption
     */
    public String getPaymentOption() {
        return paymentOption;
    }

    /**
     * @param paymentOption
     *        the paymentOption to set
     */
    public void setPaymentOption(final String paymentOption) {
        this.paymentOption = paymentOption;
    }

    /**
     * Gets the merchant param1.
     *
     * @return the merchantParam1
     */
    public String getMerchantParam1() {
        return merchantParam1;
    }

    /**
     * Sets the merchant param1.
     *
     * @param merchantParam1
     *        the merchantParam1 to set
     */
    public void setMerchantParam1(final String merchantParam1) {
        this.merchantParam1 = merchantParam1;
    }

    /**
     * Gets the merchant param2.
     *
     * @return the merchantParam2
     */
    public String getMerchantParam2() {
        return merchantParam2;
    }

    /**
     * Sets the merchant param2.
     *
     * @param merchantParam2
     *        the merchantParam2 to set
     */
    public void setMerchantParam2(final String merchantParam2) {
        this.merchantParam2 = merchantParam2;
    }

    /**
     * Gets the merchant param3.
     *
     * @return the merchantParam3
     */
    public String getMerchantParam3() {
        return merchantParam3;
    }

    /**
     * Sets the merchant param3.
     *
     * @param merchantParam3
     *        the merchantParam3 to set
     */
    public void setMerchantParam3(final String merchantParam3) {
        this.merchantParam3 = merchantParam3;
    }

    /**
     * Gets the merchant param4.
     *
     * @return the merchantParam4
     */
    public String getMerchantParam4() {
        return merchantParam4;
    }

    /**
     * Sets the merchant param4.
     *
     * @param merchantParam4
     *        the merchantParam4 to set
     */
    public void setMerchantParam4(final String merchantParam4) {
        this.merchantParam4 = merchantParam4;
    }

    /**
     * Gets the merchant param5.
     *
     * @return the merchantParam5
     */
    public String getMerchantParam5() {
        return merchantParam5;
    }

    /**
     * Sets the merchant param5.
     *
     * @param merchantParam5
     *        the merchantParam5 to set
     */
    public void setMerchantParam5(final String merchantParam5) {
        this.merchantParam5 = merchantParam5;
    }

}
