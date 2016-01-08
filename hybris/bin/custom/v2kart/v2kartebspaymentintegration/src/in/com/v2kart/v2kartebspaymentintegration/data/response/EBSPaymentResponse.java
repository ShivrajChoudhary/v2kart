/**
 * 
 */
package in.com.v2kart.v2kartebspaymentintegration.data.response;

import in.com.v2kart.core.payment.data.response.V2PaymentResponse;

/**
 * @author vikrant2480
 * 
 */
public class EBSPaymentResponse extends V2PaymentResponse
{
    private String responseCode;
    private String responseMessage;
    private String dateCreated;
    private String paymentId;
    private String merchantReferenceNo;
    private String mode;
    private String name;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String phone;
    private String email;
    private String transactionId;
    private String isFlagged;
    private String descripton;
    private String paymentMode;

    /**
     * @return the responseCode
     */
    public String getResponseCode() {
        return responseCode;
    }

    /**
     * @param responseCode
     *        the responseCode to set
     */
    public void setResponseCode(final String responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * @return the responseMessage
     */
    public String getResponseMessage() {
        return responseMessage;
    }

    /**
     * @param responseMessage
     *        the responseMessage to set
     */
    public void setResponseMessage(final String responseMessage) {
        this.responseMessage = responseMessage;
    }

    /**
     * @return the dateCreated
     */
    public String getDateCreated() {
        return dateCreated;
    }

    /**
     * @param dateCreated
     *        the dateCreated to set
     */
    public void setDateCreated(final String dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * @return the paymentId
     */
    public String getPaymentId() {
        return paymentId;
    }

    /**
     * @param paymentId
     *        the paymentId to set
     */
    public void setPaymentId(final String paymentId) {
        this.paymentId = paymentId;
    }

    /**
     * @return the merchantReferenceNo
     */
    public String getMerchantReferenceNo() {
        return merchantReferenceNo;
    }

    /**
     * @param merchantReferenceNo
     *        the merchantReferenceNo to set
     */
    public void setMerchantReferenceNo(final String merchantReferenceNo) {
        this.merchantReferenceNo = merchantReferenceNo;
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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *        the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address
     *        the address to set
     */
    public void setAddress(final String address) {
        this.address = address;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city
     *        the city to set
     */
    public void setCity(final String city) {
        this.city = city;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state
     *        the state to set
     */
    public void setState(final String state) {
        this.state = state;
    }

    /**
     * @return the postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @param postalCode
     *        the postalCode to set
     */
    public void setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country
     *        the country to set
     */
    public void setCountry(final String country) {
        this.country = country;
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
     * @return the isFlagged
     */
    public String getIsFlagged() {
        return isFlagged;
    }

    /**
     * @param isFlagged
     *        the isFlagged to set
     */
    public void setIsFlagged(final String isFlagged) {
        this.isFlagged = isFlagged;
    }

    /**
     * @return the descripton
     */
    public String getDescripton() {
        return descripton;
    }

    /**
     * @param descripton
     *        the descripton to set
     */
    public void setDescripton(final String descripton) {
        this.descripton = descripton;
    }

    /**
     * @return the paymentMode
     */
    public String getPaymentMode() {
        return paymentMode;
    }

    /**
     * @param paymentMode
     *        the paymentMode to set
     */
    public void setPaymentMode(final String paymentMode) {
        this.paymentMode = paymentMode;
    }
}
