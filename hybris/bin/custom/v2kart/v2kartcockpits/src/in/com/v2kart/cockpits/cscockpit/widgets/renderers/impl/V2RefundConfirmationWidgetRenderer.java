package in.com.v2kart.cockpits.cscockpit.widgets.renderers.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.widgets.ListboxWidget;
import de.hybris.platform.cockpit.widgets.models.impl.DefaultListWidgetModel;
import de.hybris.platform.cscockpit.utils.LabelUtils;
import de.hybris.platform.cscockpit.widgets.controllers.ReturnsController;
import de.hybris.platform.cscockpit.widgets.renderers.impl.RefundConfirmationWidgetRenderer;
import de.hybris.platform.returns.model.ReturnRequestModel;

public class V2RefundConfirmationWidgetRenderer extends RefundConfirmationWidgetRenderer {

    @Override
    protected void handleRefundConfirmedEvent(ListboxWidget<DefaultListWidgetModel<TypedObject>, ReturnsController> widget, Event event)
            throws Exception {

        final Logger LOGGER = Logger.getLogger(V2RefundConfirmationWidgetRenderer.class);

        try {
            TypedObject returnRequest = ((ReturnsController) widget.getWidgetController()).createRefundRequest();
            if (returnRequest != null)
            {
                if (getPopupWidgetHelper().getCurrentPopup() != null)
                {
                    List<Component> children = getPopupWidgetHelper().getCurrentPopup().getParent().getChildren();
                    for (Component c : children)
                    {
                        if (!(c instanceof Window))
                            continue;
                        Events.postEvent(new Event("onClose", c));
                    }
                }

                ReturnRequestModel returnRequestModel = (ReturnRequestModel) returnRequest.getObject();
                Messagebox.show(LabelUtils.getLabel(widget, "rmaNumber", new Object[] { returnRequestModel.getRMA() }),
                        LabelUtils.getLabel(widget, "rmaNumberTitle", new Object[0]), 1, "z-msgbox z-msgbox-information");

                ((ReturnsController) widget.getWidgetController()).dispatchEvent(null, widget, null);
            }
            else
            {
                Messagebox.show(LabelUtils.getLabel(widget, "error", new Object[0]), LabelUtils.getLabel(widget, "failed", new Object[0]),
                        1,
                        "z-msgbox z-msgbox-error");
            }
        } catch (Exception e) {
            LOGGER.error("Could not cancel full order.", e);
            createExceptionMessagebox(e.getMessage(), "failedToCancel", e, widget);
        }

    }

    protected void createExceptionMessagebox(final String message, final String title, final Exception e,
            final ListboxWidget<DefaultListWidgetModel<TypedObject>, ReturnsController> widget) throws InterruptedException {
        Messagebox.show(
                new StringBuilder(String.valueOf(message)).append(
                        e.getCause() != null ? new StringBuilder(" - ").append(e.getCause().getMessage()).toString() : "")
                        .toString(), LabelUtils.getLabel(widget, title, new Object[0]), 1, "z-msgbox z-msgbox-error");
    }

}
