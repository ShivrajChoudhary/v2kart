/**
 *
 */
package in.com.v2kart.v2kartpayupaymentintegration.data.response;

import in.com.v2kart.core.payment.data.response.V2PaymentResponse;

/**
 * The Class PAYUPaymentResponse.
 *
 * @author gaurav2007
 */
public class PAYUPaymentResponse extends V2PaymentResponse {

    private String phone;
    private String addedon;
    private String key;
    private String unmappedStatus;
    private String pgType;
    private String status;
    private String discount;
    private String bankRefNumber;
    private String email;
    private String mihpayid;
    private String paymentSource;
    private String mode;
    private String bankcode;
    private String netAmountDebit;
    private String error;
    private String cardnum;
    private String errorMessage;
    private String firstname;
    private String nameOnCard;
    private String productInfo;

    /**
     * Same udf1 value that merchant has sent in the initial transaction request to Payment Gateway
     */
    private String udf1;

    /**
     * Same udf2 value that merchant has sent in the initial transaction request to Payment Gateway
     */
    private String udf2;

    /**
     * Same udf3 value that merchant has sent in the initial transaction request to Payment Gateway
     */
    private String udf3;

    /**
     * Same udf4 value that merchant has sent in the initial transaction request to Payment Gateway
     */
    private String udf4;

    /**
     * Same udf5 value that merchant has sent in the initial transaction request to Payment Gateway
     */
    private String udf5;

    /**
     * Same udf6 value that merchant has sent in the initial transaction request to Payment Gateway
     */
    private String udf6;

    /**
     * Same udf7 value that merchant has sent in the initial transaction request to Payment Gateway
     */
    private String udf7;

    /**
     * Same udf8 value that merchant has sent in the initial transaction request to Payment Gateway
     */
    private String udf8;

    /**
     * Same udf9 value that merchant has sent in the initial transaction request to Payment Gateway
     */
    private String udf9;

    /**
     * Same udf10 value that merchant has sent in the initial transaction request to Payment Gateway
     */
    private String udf10;

    /**
     * @return the error
     */
    public final String getError() {
        return error;
    }

    /**
     * @param error
     *        the error to set
     */
    public final void setError(final String error) {
        this.error = error;
    }

    /**
     * Gets the udf1.
     *
     * @return the udf1, billing address
     */
    public final String getUdf1() {
        return udf1;
    }

    /**
     * Sets the udf1.
     *
     * @param udf1
     *        the udf1 to set (billing address)
     */
    public final void setUdf1(final String udf1) {
        this.udf1 = udf1;
    }

    /**
     * Gets the udf2.
     *
     * @return the udf2, udf2
     */
    public final String getUdf2() {
        return udf2;
    }

    /**
     * Sets the udf2.
     *
     * @param udf2
     *        the udf2 to set udf2
     */
    public final void setUdf2(final String udf2) {
        this.udf2 = udf2;
    }

    /**
     * Gets the udf3.
     *
     * @return the udf3, udf3
     */
    public final String getUdf3() {
        return udf3;
    }

    /**
     * Sets the udf3.
     *
     * @param udf3
     *        the udf3 to set udf3
     */
    public final void setUdf3(final String udf3) {
        this.udf3 = udf3;
    }

    /**
     * Gets the udf4.
     *
     * @return the udf4, udf4
     */
    public final String getUdf4() {
        return udf4;
    }

    /**
     * Sets the udf4.
     *
     * @param udf4
     *        the udf4 to set udf4
     */
    public final void setUdf4(final String udf4) {
        this.udf4 = udf4;
    }

    /**
     * Gets the udf5.
     *
     * @return the udf5, hash
     */
    public final String getUdf5() {
        return udf5;
    }

    /**
     * Sets the udf5.
     *
     * @param udf5
     *        the udf5 to set hash
     */
    public final void setUdf5(final String udf5) {
        this.udf5 = udf5;
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
     * @return the addedon
     */
    public String getAddedon() {
        return addedon;
    }

    /**
     * @param addedon
     *        the addedon to set
     */
    public void setAddedon(final String addedon) {
        this.addedon = addedon;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key
     *        the key to set
     */
    public void setKey(final String key) {
        this.key = key;
    }

    /**
     * @return the unmappedStatus
     */
    public String getUnmappedStatus() {
        return unmappedStatus;
    }

    /**
     * @param unmappedStatus
     *        the unmappedStatus to set
     */
    public void setUnmappedStatus(final String unmappedStatus) {
        this.unmappedStatus = unmappedStatus;
    }

    /**
     * @return the pgType
     */
    public String getPgType() {
        return pgType;
    }

    /**
     * @param pgType
     *        the pgType to set
     */
    public void setPgType(final String pgType) {
        this.pgType = pgType;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     *        the status to set
     */
    public void setStatus(final String status) {
        this.status = status;
    }

    /**
     * @return the discount
     */
    public String getDiscount() {
        return discount;
    }

    /**
     * @param discount
     *        the discount to set
     */
    public void setDiscount(final String discount) {
        this.discount = discount;
    }

    /**
     * @return the bankRefNumber
     */
    public String getBankRefNumber() {
        return bankRefNumber;
    }

    /**
     * @param bankRefNumber
     *        the bankRefNumber to set
     */
    public void setBankRefNumber(final String bankRefNumber) {
        this.bankRefNumber = bankRefNumber;
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
     * @return the mihpayid
     */
    public String getMihpayid() {
        return mihpayid;
    }

    /**
     * @param mihpayid
     *        the mihpayid to set
     */
    public void setMihpayid(final String mihpayid) {
        this.mihpayid = mihpayid;
    }

    /**
     * @return the paymentSource
     */
    public String getPaymentSource() {
        return paymentSource;
    }

    /**
     * @param paymentSource
     *        the paymentSource to set
     */
    public void setPaymentSource(final String paymentSource) {
        this.paymentSource = paymentSource;
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
     * @return the bankcode
     */
    public String getBankcode() {
        return bankcode;
    }

    /**
     * @param bankcode
     *        the bankcode to set
     */
    public void setBankcode(final String bankcode) {
        this.bankcode = bankcode;
    }

    /**
     * @return the netAmountDebit
     */
    public String getNetAmountDebit() {
        return netAmountDebit;
    }

    /**
     * @param netAmountDebit
     *        the netAmountDebit to set
     */
    public void setNetAmountDebit(final String netAmountDebit) {
        this.netAmountDebit = netAmountDebit;
    }

    /**
     * @return the cardnum
     */
    public String getCardnum() {
        return cardnum;
    }

    /**
     * @param cardnum
     *        the cardnum to set
     */
    public void setCardnum(final String cardnum) {
        this.cardnum = cardnum;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage
     *        the errorMessage to set
     */
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * @return the firstname
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * @param firstname
     *        the firstname to set
     */
    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    /**
     * @return the nameOnCard
     */
    public String getNameOnCard() {
        return nameOnCard;
    }

    /**
     * @param nameOnCard
     *        the nameOnCard to set
     */
    public void setNameOnCard(final String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    /**
     * @return the productInfo
     */
    public String getProductInfo() {
        return productInfo;
    }

    /**
     * @param productInfo
     *        the productInfo to set
     */
    public void setProductInfo(final String productInfo) {
        this.productInfo = productInfo;
    }

    /**
     * @return the udf6
     */
    public String getUdf6() {
        return udf6;
    }

    /**
     * @param udf6
     *        the udf6 to set
     */
    public void setUdf6(final String udf6) {
        this.udf6 = udf6;
    }

    /**
     * @return the udf7
     */
    public String getUdf7() {
        return udf7;
    }

    /**
     * @param udf7
     *        the udf7 to set
     */
    public void setUdf7(final String udf7) {
        this.udf7 = udf7;
    }

    /**
     * @return the udf8
     */
    public String getUdf8() {
        return udf8;
    }

    /**
     * @param udf8
     *        the udf8 to set
     */
    public void setUdf8(final String udf8) {
        this.udf8 = udf8;
    }

    /**
     * @return the udf9
     */
    public String getUdf9() {
        return udf9;
    }

    /**
     * @param udf9
     *        the udf9 to set
     */
    public void setUdf9(final String udf9) {
        this.udf9 = udf9;
    }

    /**
     * @return the udf10
     */
    public String getUdf10() {
        return udf10;
    }

    /**
     * @param udf10
     *        the udf10 to set
     */
    public void setUdf10(final String udf10) {
        this.udf10 = udf10;
    }

}
