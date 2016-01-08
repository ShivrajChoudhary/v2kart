package in.com.v2kart.cockpits.cscockpit.widgets.listeners;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Window;

import de.hybris.platform.cockpit.widgets.InputWidget;
import de.hybris.platform.cockpit.widgets.WidgetContainer;
import de.hybris.platform.cscockpit.widgets.controllers.ReturnsController;
import de.hybris.platform.cscockpit.widgets.renderers.impl.AbstractCreateWidgetRenderer;

public abstract class OrderCancellationConfirmationPopupCloseListener implements EventListener {

    private InputWidget parentWidget;
    private Component parentWindow;
    private WidgetContainer widgetContainer;
    private Window popup;

    public OrderCancellationConfirmationPopupCloseListener(final InputWidget parentWidget, final Component parentWindow,
            final WidgetContainer widgetContainer, final Window popup) {
        this.parentWidget = parentWidget;
        this.parentWindow = parentWindow;
        this.widgetContainer = widgetContainer;
        this.popup = popup;
    }

    @Override
    public void onEvent(Event event) throws Exception {
        handleOrderCancellationConfirmationPopupCloseEvent(event, parentWidget, widgetContainer, parentWindow, popup);
    }

    public abstract void handleOrderCancellationConfirmationPopupCloseEvent(Event event, InputWidget parentWidget,
            WidgetContainer widgetContainer, Component parentWindow, Window popup);
}
