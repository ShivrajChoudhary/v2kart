/**
 * 
 */
package in.com.v2kart.cockpits.cscockpit.widgets.renderers.impl;

import org.zkoss.zk.ui.api.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;

import de.hybris.platform.cockpit.session.UISessionUtils;
import de.hybris.platform.cockpit.util.UITools;
import de.hybris.platform.cockpit.widgets.Widget;
import de.hybris.platform.cscockpit.utils.LabelUtils;
import de.hybris.platform.cscockpit.widgets.controllers.OrderManagementActionsWidgetController;
import de.hybris.platform.cscockpit.widgets.models.impl.OrderItemWidgetModel;
import de.hybris.platform.cscockpit.widgets.renderers.impl.AbstractCsWidgetRenderer;
import de.hybris.platform.cscockpit.widgets.renderers.utils.PopupWidgetHelper;

/**
 * @author shubhammaheshwari
 * 
 */
public class V2OrderManagementActionsWidgetRenderer extends
        AbstractCsWidgetRenderer<Widget<OrderItemWidgetModel, OrderManagementActionsWidgetController>> {
    protected static final String CSS_ORDER_MANAGEMENT_ACTIONS_WIDGET = "orderManagementActionsWidget";
    protected static final String CSS_FULL_CANCEL_POPUP = "csFullCancelPopup";
    protected static final String COCKPIT_ID_CANCEL_ORDER = "Order_Order_Management_Cancel_Order_button";
    private PopupWidgetHelper popupWidgetHelper;

    public V2OrderManagementActionsWidgetRenderer() {
    }

    protected PopupWidgetHelper getPopupWidgetHelper() {
        return popupWidgetHelper;
    }

    public void setPopupWidgetHelper(PopupWidgetHelper popupWidgetHelper) {
        this.popupWidgetHelper = popupWidgetHelper;
    }

    protected HtmlBasedComponent createContentInternal(Widget<OrderItemWidgetModel, OrderManagementActionsWidgetController> widget,
            HtmlBasedComponent rootContainer) {
        Div component = new Div();
        component.setSclass(CSS_ORDER_MANAGEMENT_ACTIONS_WIDGET);
        createButton(widget, component, "cancelWholeOrder", "csFullOrderCancellationWidgetConfig", "csFullOrderCancel-Popup",
                CSS_FULL_CANCEL_POPUP, "popup.fullCancellationRequestCreate",
                !((OrderManagementActionsWidgetController) widget.getWidgetController()).isFullCancelPossible());
        createButton(widget, component, "cancelPartialOrder", "csPartialOrderCancellationWidgetConfig",
                "csPartialOrderCancellationWidgetConfig-Popup", "csPartialCancelPopup", "popup.partialCancellationRequestCreate",
                !(((OrderManagementActionsWidgetController) widget.getWidgetController()).isPartialCancelPossible()));
        createButton(widget, component, "refundOrder", "csRefundRequestCreateWidgetConfig", "csRefundRequestCreateWidget-Popup",
                "csReturnRequestCreateWidget", "popup.refundRequestCreate",
                !(((OrderManagementActionsWidgetController) widget.getWidgetController()).isRefundPossible()));
        return component;
    }

    protected void createButton(final Widget<OrderItemWidgetModel, OrderManagementActionsWidgetController> widget, final Div container,
            String buttonLabelName, final String springWidgetName, final String popupCode, final String cssClass,
            final String popupTitleLabelName, boolean disabled) {
        EventListener eventListener = new EventListener() {

            public void onEvent(Event event) throws Exception {
                handleButtonClickEvent(widget, event, container, springWidgetName, popupCode, cssClass, popupTitleLabelName);
            }

        };
        createButton(widget, container, buttonLabelName, eventListener, disabled);
    }

    protected void handleButtonClickEvent(Widget<OrderItemWidgetModel, OrderManagementActionsWidgetController> widget, Event event,
            Div container, String springWidgetName, String popupCode, String cssClass, String popupTitleLabelName) {
        getPopupWidgetHelper().createPopupWidget(container, springWidgetName, popupCode, cssClass,
                LabelUtils.getLabel(widget, popupTitleLabelName, new Object[0]), 1000);
    }

    protected void createButton(Widget<OrderItemWidgetModel, OrderManagementActionsWidgetController> widget, Div container,
            String buttonLabelName, EventListener eventListener, boolean disabled) {
        Button button = new Button();
        if (UISessionUtils.getCurrentSession().isUsingTestIDs()) {
            if ("cancelWholeOrder".equals(buttonLabelName)) {
                UITools.applyTestID(button, COCKPIT_ID_CANCEL_ORDER);
            } else if ("cancelPartialOrder".equals(buttonLabelName)) {
                UITools.applyTestID(button, "Order_Order_Management_Partial_Cancel_Order_button");
            } else if ("refundOrder".equals(buttonLabelName)) {
                UITools.applyTestID(button, "Order_Order_Management_Refund_Order_button");
            }
        }
        button.setLabel(LabelUtils.getLabel(widget, buttonLabelName, new Object[0]));
        button.setParent(container);
        button.setDisabled(disabled);
        button.addEventListener("onClick", eventListener);
    }

}