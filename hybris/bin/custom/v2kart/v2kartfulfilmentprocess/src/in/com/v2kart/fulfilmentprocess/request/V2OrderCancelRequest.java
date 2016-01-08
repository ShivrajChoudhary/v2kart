package in.com.v2kart.fulfilmentprocess.request;

import java.util.List;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.OrderCancelEntry;
import de.hybris.platform.ordercancel.OrderCancelRequest;

public class V2OrderCancelRequest extends OrderCancelRequest {

    private String refundInto;
    private Boolean returnToWallet;
    private Boolean returnToGateway;

    public Boolean getReturnToWallet() {
        return returnToWallet;
    }

    public void setReturnToWallet(Boolean returnToWallet) {
        this.returnToWallet = returnToWallet;
    }

    public Boolean getReturnToGateway() {
        return returnToGateway;
    }

    public void setReturnToGateway(Boolean returnToGateway) {
        this.returnToGateway = returnToGateway;
    }

    public String getRefundInto() {
        return refundInto;
    }

    public void setRefundInto(String refundInto) {
        this.refundInto = refundInto;
    }

    public V2OrderCancelRequest(OrderModel order, List<OrderCancelEntry> orderCancelEntries) {
        super(order, orderCancelEntries);
    }

    public V2OrderCancelRequest(OrderModel order) {
        super(order);
    }
}
