/**
 * 
 */
package in.com.v2kart.v2kartpayupaymentintegration.data.response;

/**
 * @author pankajaggarwal
 * 
 */
public class PayURefundCommandResponse extends PayUAbstractCommandResponse {

    private String error_code;
    private String txn_update_id;
    private String bank_ref_num;
    private String mihpayid;
    private String request_id;

    /**
     * @param error_code
     * @param txn_update_id
     * @param bank_ref_num
     * @param mihpayid
     * @param request_id
     */
    public PayURefundCommandResponse(final String status, final String msg, final String error_code, final String txn_update_id,
            final String bank_ref_num,
            final String mihpayid, final String request_id) {
        super(status, msg);
        this.error_code = error_code;
        this.txn_update_id = txn_update_id;
        this.bank_ref_num = bank_ref_num;
        this.mihpayid = mihpayid;
        this.request_id = request_id;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(final String error_code) {
        this.error_code = error_code;
    }

    public String getTxn_update_id() {
        return txn_update_id;
    }

    public void setTxn_update_id(final String txn_update_id) {
        this.txn_update_id = txn_update_id;
    }

    public String getBank_ref_num() {
        return bank_ref_num;
    }

    public void setBank_ref_num(final String bank_ref_num) {
        this.bank_ref_num = bank_ref_num;
    }

    public String getMihpayid() {
        return mihpayid;
    }

    public void setMihpayid(final String mihpayid) {
        this.mihpayid = mihpayid;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(final String request_id) {
        this.request_id = request_id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.v2kartpayupaymentintegration.data.response.PayUAbstractCommandResponse#toString()
     */
    @Override
    public String toString() {
        return "PayURefundCommandResponse [error_code=" + error_code
                + ", Status=" + getStatus() + ", Message=" + getMsg()
                + ", txn_update_id=" + txn_update_id + ", bank_ref_num="
                + bank_ref_num + ", mihpayid=" + mihpayid + ", request_id="
                + request_id + "]";
    }

}
