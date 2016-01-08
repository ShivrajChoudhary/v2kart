/**
 * 
 */
package in.com.v2kart.core.payment.data.request;

import de.hybris.platform.acceleratorservices.payment.data.HostedOrderPageRequest;

/**
 * @author Anuj
 * 
 */
public class V2PaymentRequest extends HostedOrderPageRequest {
    private String firstName;
    private String email;
    private String salt;
    private String cancelUrl;
    private String successUrl;
    private String failureUrl;
    private String requestUrl;
    private Double amount;
    private String transactionId;
    private String merchantKey;
    private String phone;
    private String orderDescription;
    private String enforcedPaymentMethod;

    /**
     * Used to save title, customer and mobile number name seperated by '_' character , if billing address is different from shipping
     * address
     */
    private String udf1;

    /**
     * Used to save address, if billing address is different from shipping address
     */
    private String udf2;

    /**
     * Used to save city, if billing address is different from shipping address
     */
    private String udf3;

    /**
     * Used to save pincode, if billing address is different from shipping address
     */
    private String udf4;

    /**
     * Used to save state, if billing address is different from shipping address
     */
    private String udf5;

    /**
     * Value for payment gateway
     */
    private String pg;
    private String bankCode;

    /**
     * @param bankCode
     *        the bankCode to set
     */
    public void setBankCode(final String bankCode) {
        this.bankCode = bankCode;
    }

    /**
     * @return the bankCode
     */
    public String getBankCode() {
        return bankCode;
    }

    /**
     * @return the pg
     */
    public String getPg() {
        return pg;
    }

    /**
     * @param pg
     *        the pg to set
     */
    public void setPg(final String pg) {
        this.pg = pg;
    }

    /**
     * @return the cancelUrl
     */
    public String getCancelUrl() {
        return cancelUrl;
    }

    /**
     * @param cancelUrl
     *        the cancelUrl to set
     */
    public void setCancelUrl(final String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }

    /**
     * @return the successUrl
     */
    public String getSuccessUrl() {
        return successUrl;
    }

    /**
     * @param successUrl
     *        the successUrl to set
     */
    public void setSuccessUrl(final String successUrl) {
        this.successUrl = successUrl;
    }

    /**
     * @return the failureUrl
     */
    public String getFailureUrl() {
        return failureUrl;
    }

    /**
     * @param failureUrl
     *        the failureUrl to set
     */
    public void setFailureUrl(final String failureUrl) {
        this.failureUrl = failureUrl;
    }

    /**
     * @return the amount
     */
    public Double getAmount() {
        return amount;
    }

    /**
     * @param amount
     *        the amount to set
     */
    public void setAmount(final Double amount) {
        this.amount = amount;
    }

    /**
     * @return the transactionId
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * @param transactionId
     *        the transactionId to set
     */
    public void setTransactionId(final String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * @return the merchantKey
     */
    public String getMerchantKey() {
        return merchantKey;
    }

    /**
     * @param merchantKey
     *        the merchantKey to set
     */
    public void setMerchantKey(final String merchantKey) {
        this.merchantKey = merchantKey;
    }

    /**
     * @return the orderDescription
     */
    public String getOrderDescription() {
        return orderDescription;
    }

    /**
     * @param orderDescription
     *        the orderDescription to set
     */
    public void setOrderDescription(final String orderDescription) {
        this.orderDescription = orderDescription;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone
     *        the phone to set
     */
    public void setPhone(final String phone) {
        this.phone = phone;
    }

    /**
     * @return the enforcedPaymentMethod
     */
    public String getEnforcedPaymentMethod() {
        return enforcedPaymentMethod;
    }

    /**
     * @param enforcedPaymentMethod
     *        the enforcedPaymentMethod to set
     */
    public void setEnforcedPaymentMethod(final String enforcedPaymentMethod) {
        this.enforcedPaymentMethod = enforcedPaymentMethod;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName
     *        the firstName to set
     */
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     *        the email to set
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * @return the requestUrl
     */
    @Override
    public String getRequestUrl() {
        return requestUrl;
    }

    /**
     * @param requestUrl
     *        the requestUrl to set
     */
    @Override
    public void setRequestUrl(final String requestUrl) {
        this.requestUrl = requestUrl;
    }

    /**
     * @return the salt
     */
    public String getSalt() {
        return salt;
    }

    /**
     * @param salt
     *        the salt to set
     */
    public void setSalt(final String salt) {
        this.salt = salt;
    }

    /**
     * @return the udf1 , title name and mobile number
     */
    public String getUdf1() {
        return udf1;
    }

    /**
     * @param udf1
     *        the udf1 to set , title name and mobile number
     */
    public void setUdf1(final String udf1) {
        this.udf1 = udf1;
    }

    /**
     * @return the udf2 , address
     */
    public String getUdf2() {
        return udf2;
    }

    /**
     * @param udf2
     *        the udf2 to set , address
     */
    public void setUdf2(final String udf2) {
        this.udf2 = udf2;
    }

    /**
     * @return the udf3 , city
     */
    public String getUdf3() {
        return udf3;
    }

    /**
     * @param udf3
     *        the udf3 to set , city
     */
    public void setUdf3(final String udf3) {
        this.udf3 = udf3;
    }

    /**
     * @return the udf4 , pincode
     */
    public String getUdf4() {
        return udf4;
    }

    /**
     * @param udf4
     *        the udf4 to set , pincode
     */
    public void setUdf4(final String udf4) {
        this.udf4 = udf4;
    }

    /**
     * @return the udf5 , state
     */
    public String getUdf5() {
        return udf5;
    }

    /**
     * @param udf5
     *        the udf5 to set , state
     */
    public void setUdf5(final String udf5) {
        this.udf5 = udf5;
    }

}
