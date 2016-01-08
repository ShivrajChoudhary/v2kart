/**
 * 
 */
package in.com.v2kart.core.payment.data.response;

import de.hybris.platform.acceleratorservices.payment.data.AbstractPaymentResult;

/**
 * The Class V2PaymentResponse.
 * 
 * @author Anuj
 */
public class V2PaymentResponse extends AbstractPaymentResult {

    private String amount;
    private String txnid;
    private String authId;

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
     * Gets the txnid.
     * 
     * @return the txnid
     */
    public final String getTxnid() {
        return txnid;
    }

    /**
     * Sets the txnid.
     * 
     * @param txnid
     *        the txnid to set
     */
    public final void setTxnid(final String txnid) {
        this.txnid = txnid;
    }

    /**
     * Gets the auth id.
     * 
     * @return the authId
     */
    public final String getAuthId() {
        return authId;
    }

    /**
     * Sets the auth id.
     * 
     * @param authId
     *        the authId to set
     */
    public final void setAuthId(final String authId) {
        this.authId = authId;
    }

}
