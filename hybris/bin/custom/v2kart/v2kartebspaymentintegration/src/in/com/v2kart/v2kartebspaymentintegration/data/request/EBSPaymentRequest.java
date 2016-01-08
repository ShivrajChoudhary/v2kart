/**
 * 
 */
package in.com.v2kart.v2kartebspaymentintegration.data.request;

import de.hybris.platform.acceleratorservices.payment.data.HostedOrderPageRequest;

/**
 * @author vikrant2480
 * 
 */
public class EBSPaymentRequest extends HostedOrderPageRequest
{
    private String accountId;
    private String referenceNumber;
    private String amount;
    private String mode;
    private String description;
    private String returnUrl;
    private String billingName;
    private String billingAddress;
    private String billingCity;
    private String billingState;
    private String billingCountry;
    private String billingPostalCode;
    private String billingPhoneNumber;
    private String billingEmailId;
    private String key;

    private String paymentId;

    /**
     * @return the accountId
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * @param accountId
     *        the accountId to set
     */
    public void setAccountId(final String accountId) {
        this.accountId = accountId;
    }

    /**
     * @return the referenceNumber
     */
    public String getReferenceNumber() {
        return referenceNumber;
    }

    /**
     * @param referenceNumber
     *        the referenceNumber to set
     */
    public void setReferenceNumber(final String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    /**
     * @return the amount
     */
    public String getAmount() {
        return amount;
    }

    /**
     * @param amount
     *        the amount to set
     */
    public void setAmount(final String amount) {
        this.amount = amount;
    }

    /**
     * @return the mode
     */
    public String getMode() {
        return mode;
    }

    /**
     * @param mode
     *        the mode to set
     */
    public void setMode(final String mode) {
        this.mode = mode;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *        the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return the returnUrl
     */
    public String getReturnUrl() {
        return returnUrl;
    }

    /**
     * @param returnUrl
     *        the returnUrl to set
     */
    public void setReturnUrl(final String returnUrl) {
        this.returnUrl = returnUrl;
    }

    /**
     * @return the billingName
     */
    public String getBillingName() {
        return billingName;
    }

    /**
     * @param billingName
     *        the billingName to set
     */
    public void setBillingName(final String billingName) {
        this.billingName = billingName;
    }

    /**
     * @return the billingAddress
     */
    public String getBillingAddress() {
        return billingAddress;
    }

    /**
     * @param billingAddress
     *        the billingAddress to set
     */
    public void setBillingAddress(final String billingAddress) {
        this.billingAddress = billingAddress;
    }

    /**
     * @return the billingCity
     */
    public String getBillingCity() {
        return billingCity;
    }

    /**
     * @param billingCity
     *        the billingCity to set
     */
    public void setBillingCity(final String billingCity) {
        this.billingCity = billingCity;
    }

    /**
     * @return the billingState
     */
    public String getBillingState() {
        return billingState;
    }

    /**
     * @param billingState
     *        the billingState to set
     */
    public void setBillingState(final String billingState) {
        this.billingState = billingState;
    }

    /**
     * @return the billingCountry
     */
    public String getBillingCountry() {
        return billingCountry;
    }

    /**
     * @param billingCountry
     *        the billingCountry to set
     */
    public void setBillingCountry(final String billingCountry) {
        this.billingCountry = billingCountry;
    }

    /**
     * @return the billingPostalCode
     */
    public String getBillingPostalCode() {
        return billingPostalCode;
    }

    /**
     * @param billingPostalCode
     *        the billingPostalCode to set
     */
    public void setBillingPostalCode(final String billingPostalCode) {
        this.billingPostalCode = billingPostalCode;
    }

    /**
     * @return the billingPhoneNumber
     */
    public String getBillingPhoneNumber() {
        return billingPhoneNumber;
    }

    /**
     * @param billingPhoneNumber
     *        the billingPhoneNumber to set
     */
    public void setBillingPhoneNumber(final String billingPhoneNumber) {
        this.billingPhoneNumber = billingPhoneNumber;
    }

    /**
     * @return the billingEmailId
     */
    public String getBillingEmailId() {
        return billingEmailId;
    }

    /**
     * @param billingEmailId
     *        the billingEmailId to set
     */
    public void setBillingEmailId(final String billingEmailId) {
        this.billingEmailId = billingEmailId;
    }

    /**
     * @param key
     *        the key to set
     */
    public void setKey(final String key) {
        this.key = key;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @return the paymentId
     */
    public String getPaymentId() {
        return paymentId;
    }

    /**
     * @param paymentId the paymentId to set
     */
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
}
