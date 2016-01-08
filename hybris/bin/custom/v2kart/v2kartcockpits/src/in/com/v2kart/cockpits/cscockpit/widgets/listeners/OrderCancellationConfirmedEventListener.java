package in.com.v2kart.cockpits.cscockpit.widgets.listeners;

import in.com.v2kart.cockpits.cscockpit.widgets.controllers.impl.V2CancellationController;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.widgets.InputWidget;
import de.hybris.platform.cockpit.widgets.models.impl.DefaultListWidgetModel;

public abstract class OrderCancellationConfirmedEventListener implements EventListener {

    private static final Logger LOGGER = Logger.getLogger(OrderCancellationConfirmedEventListener.class);

    private final transient InputWidget widget;

    public OrderCancellationConfirmedEventListener(final InputWidget<DefaultListWidgetModel<TypedObject>, V2CancellationController> widget) {
        this.widget = widget;
    }

    @Override
    public void onEvent(final Event event) throws Exception {
        try {
            handleCancelConfirmedEvent(widget, event);
        } catch (final InterruptedException interruptedException) {
            final String errorMsg = "Could not handle order cancel event properly.";
            LOGGER.error(errorMsg);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(errorMsg, interruptedException);
            }
        }
    }

    public abstract void handleCancelConfirmedEvent(InputWidget<DefaultListWidgetModel<TypedObject>, V2CancellationController> widget,
            Event event) throws InterruptedException;
}
