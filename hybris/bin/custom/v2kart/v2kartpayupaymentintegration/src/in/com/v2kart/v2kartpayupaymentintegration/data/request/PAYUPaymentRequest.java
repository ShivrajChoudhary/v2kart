/**
 *
 */
package in.com.v2kart.v2kartpayupaymentintegration.data.request;

import de.hybris.platform.acceleratorservices.payment.data.HostedOrderPageRequest;

/**
 * The Class PAYUPaymentRequest.
 *
 * @author Anuj
 */
public class PAYUPaymentRequest extends HostedOrderPageRequest {

    private String firstName;
    private String email;
    private String salt;
    private String amount;
    private String cancelUrl;
    private String successUrl;
    private String failureUrl;
    private String requestUrl;
    private String transactionId;
    private String merchantKey;
    private String phone;
    private String orderDescription;
    private String enforcedPaymentMethod;
    private String bankCode;

    /**
     * Value for payment gateway
     */
    private String pg;

    /**
     * The user (merchant) defines these fields. The field data is passed along with a transaction request and then returned in the
     * transaction response. Merchant should ensure that field is left blank when no data is need to be passed. These fields are for
     * merchant to pass additional transaction related information like customer email id, contact number etc
     *
     * Used to save title, first name, last name and mobile number separated by ';' string, if billing address is different from shipping
     * address
     */
    private String udf1;

    /**
     * Used to save address separated by ';' string, if billing address is different from shipping address
     */
    private String udf2;

    /**
     * Used to save city, pincode and state separated by ';' string, if billing address is different from shipping address
     */
    private String udf3;

    /**
     * Used to save payment mode
     */
    private String udf4;

    private String udf5;
    private String udf6;
    private String udf7;
    private String udf8;
    private String udf9;
    private String udf10;

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
     *        the amount to set
     */
    public final void setAmount(final String amount) {
        this.amount = amount;
    }

    /**
     * Gets the first name.
     *
     * @return the firstName
     */
    public final String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name.
     *
     * @param firstName
     *        the firstName to set
     */
    public final void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the email.
     *
     * @return the email
     */
    public final String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     *
     * @param email
     *        the email to set
     */
    public final void setEmail(final String email) {
        this.email = email;
    }

    /**
     * Gets the salt.
     *
     * @return the salt
     */
    public final String getSalt() {
        return salt;
    }

    /**
     * Sets the salt.
     *
     * @param salt
     *        the salt to set
     */
    public final void setSalt(final String salt) {
        this.salt = salt;
    }

    /**
     * Gets the cancel url.
     *
     * @return the cancelUrl
     */
    public final String getCancelUrl() {
        return cancelUrl;
    }

    /**
     * Sets the cancel url.
     *
     * @param cancelUrl
     *        the cancelUrl to set
     */
    public final void setCancelUrl(final String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }

    /**
     * Gets the success url.
     *
     * @return the successUrl
     */
    public final String getSuccessUrl() {
        return successUrl;
    }

    /**
     * Sets the success url.
     *
     * @param successUrl
     *        the successUrl to set
     */
    public final void setSuccessUrl(final String successUrl) {
        this.successUrl = successUrl;
    }

    /**
     * Gets the failure url.
     *
     * @return the failureUrl
     */
    public final String getFailureUrl() {
        return failureUrl;
    }

    /**
     * Sets the failure url.
     *
     * @param failureUrl
     *        the failureUrl to set
     */
    public final void setFailureUrl(final String failureUrl) {
        this.failureUrl = failureUrl;
    }

    /**
     * Gets the request url.
     *
     * @return the requestUrl
     */
    @Override
    public final String getRequestUrl() {
        return requestUrl;
    }

    /**
     * Sets the request url.
     *
     * @param requestUrl
     *        the requestUrl to set
     */
    @Override
    public final void setRequestUrl(final String requestUrl) {
        this.requestUrl = requestUrl;
    }

    /**
     * Gets the transaction id.
     *
     * @return the transactionId
     */
    public final String getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the transaction id.
     *
     * @param transactionId
     *        the transactionId to set
     */
    public final void setTransactionId(final String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Gets the merchant key.
     *
     * @return the merchantKey
     */
    public final String getMerchantKey() {
        return merchantKey;
    }

    /**
     * Sets the merchant key.
     *
     * @param merchantKey
     *        the merchantKey to set
     */
    public final void setMerchantKey(final String merchantKey) {
        this.merchantKey = merchantKey;
    }

    /**
     * Gets the phone.
     *
     * @return the phone
     */
    public final String getPhone() {
        return phone;
    }

    /**
     * Sets the phone.
     *
     * @param phone
     *        the phone to set
     */
    public final void setPhone(final String phone) {
        this.phone = phone;
    }

    /**
     * Gets the order description.
     *
     * @return the orderDescription
     */
    public final String getOrderDescription() {
        return orderDescription;
    }

    /**
     * Sets the order description.
     *
     * @param orderDescription
     *        the orderDescription to set
     */
    public final void setOrderDescription(final String orderDescription) {
        this.orderDescription = orderDescription;
    }

    /**
     * Gets the enforced payment method.
     *
     * @return the enforcedPaymentMethod
     */
    public final String getEnforcedPaymentMethod() {
        return enforcedPaymentMethod;
    }

    /**
     * Sets the enforced payment method.
     *
     * @param enforcedPaymentMethod
     *        the enforcedPaymentMethod to set
     */
    public final void setEnforcedPaymentMethod(final String enforcedPaymentMethod) {
        this.enforcedPaymentMethod = enforcedPaymentMethod;
    }

    /**
     * Gets the bank code.
     *
     * @return the bankCode
     */
    public final String getBankCode() {
        return bankCode;
    }

    /**
     * Sets the bank code.
     *
     * @param bankCode
     *        the bankCode to set
     */
    public final void setBankCode(final String bankCode) {
        this.bankCode = bankCode;
    }

    /**
     * Gets the pg.
     *
     * @return the pg
     */
    public final String getPg() {
        return pg;
    }

    /**
     * Sets the pg.
     *
     * @param pg
     *        the pg to set
     */
    public final void setPg(final String pg) {
        this.pg = pg;
    }

    /**
     * Gets the udf1.
     *
     * @return the udf1
     */
    public final String getUdf1() {
        return udf1;
    }

    /**
     * Sets the udf1.
     *
     * @param udf1
     *        the udf1 to set
     */
    public final void setUdf1(final String udf1) {
        this.udf1 = udf1;
    }

    /**
     * Gets the udf2.
     *
     * @return the udf2
     */
    public final String getUdf2() {
        return udf2;
    }

    /**
     * Sets the udf2.
     *
     * @param udf2
     *        the udf2 to set
     */
    public final void setUdf2(final String udf2) {
        this.udf2 = udf2;
    }

    /**
     * Gets the udf3.
     *
     * @return the udf3
     */
    public final String getUdf3() {
        return udf3;
    }

    /**
     * Sets the udf3.
     *
     * @param udf3
     *        the udf3 to set
     */
    public final void setUdf3(final String udf3) {
        this.udf3 = udf3;
    }

    /**
     * Gets the udf4.
     *
     * @return the udf4
     */
    public final String getUdf4() {
        return udf4;
    }

    /**
     * Sets the udf4.
     *
     * @param udf4
     *        the udf4 to set
     */
    public final void setUdf4(final String udf4) {
        this.udf4 = udf4;
    }

    /**
     * Gets the udf5.
     *
     * @return the udf5
     */
    public final String getUdf5() {
        return udf5;
    }

    /**
     * Sets the udf5.
     *
     * @param udf5
     *        the udf5 to set
     */
    public final void setUdf5(final String udf5) {
        this.udf5 = udf5;
    }

    /**
     * Gets the udf6.
     *
     * @return the udf6
     */
    public final String getUdf6() {
        return udf6;
    }

    /**
     * Sets the udf6.
     *
     * @param udf6
     *        the udf6 to set
     */
    public final void setUdf6(final String udf6) {
        this.udf6 = udf6;
    }

    /**
     * Gets the udf7.
     *
     * @return the udf7
     */
    public final String getUdf7() {
        return udf7;
    }

    /**
     * Sets the udf7.
     *
     * @param udf7
     *        the udf7 to set
     */
    public final void setUdf7(final String udf7) {
        this.udf7 = udf7;
    }

    /**
     * Gets the udf8.
     *
     * @return the udf8
     */
    public final String getUdf8() {
        return udf8;
    }

    /**
     * Sets the udf8.
     *
     * @param udf8
     *        the udf8 to set
     */
    public final void setUdf8(final String udf8) {
        this.udf8 = udf8;
    }

    /**
     * Gets the udf9.
     *
     * @return the udf9
     */
    public final String getUdf9() {
        return udf9;
    }

    /**
     * Sets the udf9.
     *
     * @param udf9
     *        the udf9 to set
     */
    public final void setUdf9(final String udf9) {
        this.udf9 = udf9;
    }

    /**
     * Gets the udf10.
     *
     * @return the udf10
     */
    public final String getUdf10() {
        return udf10;
    }

    /**
     * Sets the udf10.
     *
     * @param udf10
     *        the udf10 to set
     */
    public final void setUdf10(final String udf10) {
        this.udf10 = udf10;
    }

}
