/**
 * 
 */
package in.com.v2kart.v2kartpayupaymentintegration.data.response;

/**
 * @author pankajaggarwal
 * 
 */
public class PayUAbstractCommandResponse {

    private String status;
    private String msg;

    /**
     * @param status
     * @param msg
     */
    public PayUAbstractCommandResponse(final String status, final String msg) {
        super();
        this.status = status;
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PayUAbstractCommandResponse [status=" + status + ", msg=" + msg
                + "]";
    }
}
