package in.com.v2kart.storefront.forms;
/**
 * Form for order modification.
 * @author shailjagupta
 *
 */
public class OrderModificationForm {
    //order full modified or not
    private String isFull;
    // reson for modification
    private String reason;
    // ordercode
    private String orderCode;
    // extra notes
    private String note;

    public String getIsFull() {
        return isFull;
    }

    public void setIsFull(String isFull) {
        this.isFull = isFull;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
