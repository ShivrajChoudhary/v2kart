/**
 * 
 */
package in.com.v2kart.v2kartebspaymentintegration.data.response;

/**
 * @author pankajaggarwal
 * 
 */
public class EBSActionResponse {

    private String transactionId;
    private String paymentId;
    private String amount;
    private String mode;
    private String referenceNo;
    private String transactionType;
    private String status;
    private String dateTime;
    private String response;
    private String error;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(final String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(final String paymentId) {
        this.paymentId = paymentId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(final String amount) {
        this.amount = amount;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(final String mode) {
        this.mode = mode;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(final String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(final String transactionType) {
        this.transactionType = transactionType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(final String dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * @return the response
     */
    public String getResponse() {
        return response;
    }

    /**
     * @param response
     *        the response to set
     */
    public void setResponse(final String response) {
        this.response = response;
    }

    /**
     * @return the error
     */
    public String getError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(String error) {
        this.error = error;
    }

}
