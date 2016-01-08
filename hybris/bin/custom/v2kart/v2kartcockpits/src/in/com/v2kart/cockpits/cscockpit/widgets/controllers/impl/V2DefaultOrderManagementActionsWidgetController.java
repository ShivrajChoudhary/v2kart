package in.com.v2kart.cockpits.cscockpit.widgets.controllers.impl;

import in.com.v2kart.core.services.V2ReturnService;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cscockpit.widgets.controllers.impl.DefaultOrderManagementActionsWidgetController;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
/**
 * V2kart implementation of OrderManagementActionsWidgetController
 * @author shailjagupta
 *
 */
public class V2DefaultOrderManagementActionsWidgetController extends DefaultOrderManagementActionsWidgetController {

    private V2ReturnService v2ReturnService;

    public V2ReturnService getV2ReturnService() {
        return v2ReturnService;
    }

    public void setV2ReturnService(V2ReturnService v2ReturnService) {
        this.v2ReturnService = v2ReturnService;
    }

    @Override
    public boolean isFullCancelPossible() {
        return super.isFullCancelPossible() && isOrderCancellable((OrderModel) getOrder().getObject())
                && getOrderCancelleable((OrderModel) getOrder().getObject());
    }

    @Override
    public boolean isPartialCancelPossible() {
        return super.isPartialCancelPossible() && getOrderCancelleable((OrderModel) getOrder().getObject());
    }

    @Override
    public boolean isRefundPossible() {
        return super.isRefundPossible() && getV2ReturnService().isOrderReturnable((OrderModel) getOrder().getObject());
    }

    /**
     * @param cancelableEntries
     * @param orderDetails
     */
    private Boolean getOrderCancelleable(final OrderModel order) {
        Boolean result = Boolean.TRUE;
        // whole order is only cancelable when
        // there is no consignment and
        // no order entry or line item is cancelled before

        if ((order.getConsignments() != null && order.getConsignments().size() != 0)) {
            for (ConsignmentModel consignment : order.getConsignments()) {
                if (ConsignmentStatus.READY_TO_DISPATCH.equals(consignment.getStatus())) {
                    result = result && Boolean.FALSE;
                }
            }
        } else {
            result = Boolean.TRUE;
        }
        return result;
    }

    private Boolean isOrderCancellable(final OrderModel order) {
        Boolean cancellable = true;
        if (order.getStatus().equals(OrderStatus.CANCELLED) || order.getStatus().equals(OrderStatus.READY_TO_SHIP)
                || order.getStatus().equals(OrderStatus.DISPATCHED) || order.getStatus().equals(OrderStatus.COMPLETED)) {
            cancellable = false;
        }
        return cancellable;
    }
}
