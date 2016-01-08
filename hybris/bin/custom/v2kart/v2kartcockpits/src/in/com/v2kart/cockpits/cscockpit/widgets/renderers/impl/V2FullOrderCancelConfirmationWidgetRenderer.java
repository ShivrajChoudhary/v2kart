package in.com.v2kart.cockpits.cscockpit.widgets.renderers.impl;

import in.com.v2kart.cockpits.cscockpit.widgets.controllers.impl.V2CancellationController;
import in.com.v2kart.cockpits.cscockpit.widgets.listeners.OrderCancellationConfirmedEventListener;

import java.text.NumberFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.api.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.widgets.InputWidget;
import de.hybris.platform.cockpit.widgets.models.impl.DefaultListWidgetModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cscockpit.exceptions.ValidationException;
import de.hybris.platform.cscockpit.utils.LabelUtils;
import de.hybris.platform.cscockpit.widgets.renderers.impl.AbstractCreateWidgetRenderer;
import de.hybris.platform.cscockpit.widgets.renderers.utils.PopupWidgetHelper;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.FormatFactory;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;

/**
 * Renderer to display the full order cancellation confirmation popup.
 *
 * @author pravesh.gupta@nagarro.com
 */
public class V2FullOrderCancelConfirmationWidgetRenderer extends
        AbstractCreateWidgetRenderer<InputWidget<DefaultListWidgetModel<TypedObject>, V2CancellationController>> {

    private static final Logger LOGGER = Logger.getLogger(V2OrderManagementActionsWidgetRenderer.class);

    private PopupWidgetHelper popupWidgetHelper;
    private FormatFactory formatFactory;
    private CommonI18NService commonI18NService;
    private SessionService sessionService;

    /**
     * Sets the popup widget helper.
     *
     * @param popupWidgetHelper
     *        the new popup widget helper
     */
    @Required
    public void setPopupWidgetHelper(final PopupWidgetHelper popupWidgetHelper) {
        this.popupWidgetHelper = popupWidgetHelper;
    }

    /**
     * Sets the common i18 n service.
     *
     * @param commonI18NService
     *        the new common i18 n service
     */
    @Required
    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    /**
     * Sets the session service.
     *
     * @param sessionService
     *        the new session service
     */
    @Required
    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    /**
     * Sets the format factory.
     *
     * @param formatFactory
     *        the new format factory
     */
    @Required
    public void setFormatFactory(final FormatFactory formatFactory) {
        this.formatFactory = formatFactory;
    }

    /**
     * Creates the internal content of full order cancellation popup.
     *
     * @see de.hybris.platform.cscockpit.widgets.renderers.impl.AbstractCsWidgetRenderer#createContentInternal(de.hybris.platform.cockpit.widgets
     *      .Widget, org.zkoss.zk.ui.api.HtmlBasedComponent)
     */
    @Override
    protected HtmlBasedComponent createContentInternal(
            final InputWidget<DefaultListWidgetModel<TypedObject>, V2CancellationController> widget, final HtmlBasedComponent rootContainer) {
        final Div container = new Div();
        container.setSclass("refundConfirmationWidget");
        final Div originalOrderContent = new Div();
        originalOrderContent.setParent(container);

        final OrderModel originalOrderModel = (OrderModel) widget.getWidgetController().getOrder().getObject();
        final CurrencyModel currencyModel = originalOrderModel.getCurrency();
        final NumberFormat currencyInstance = (NumberFormat) getSessionService().executeInLocalView(new SessionExecutionBody() {
            @Override
            public Object execute() {
                getCommonI18NService().setCurrentCurrency(currencyModel);
                return getFormatFactory().createCurrencyFormat();
            }
        });

        final Double fullOrderRefundAmount = widget.getWidgetController().getCancellationRefundAmount(true);
        if (fullOrderRefundAmount != null) {
            final Div refundContent = new Div();
            refundContent.setParent(container);
            final String refundAmountText = LabelUtils.getLabel(widget, "refundAmount",
                    new Object[] { currencyInstance.format(fullOrderRefundAmount) });
            final Label refund = new Label(refundAmountText);
            refund.setParent(refundContent);
        }

        final Div confirmContent = new Div();
        confirmContent.setParent(container);
        final Button confirmButton = new Button(LabelUtils.getLabel(widget, "confirmButton", new Object[0]));
        confirmButton.setParent(confirmContent);
        confirmButton.addEventListener("onClick", createFullCancelConfirmedEventListener(widget));
        return container;
    }

    /**
     * Creates the full cancel confirmed event listener.
     *
     * @param widget
     *        the widget
     * @param cancelRequestValueContainer
     *        the cancel request value container
     * @return the event listener
     */
    protected EventListener createFullCancelConfirmedEventListener(
            final InputWidget<DefaultListWidgetModel<TypedObject>, V2CancellationController> widget) {
        final OrderCancellationConfirmedEventListener fullCancellationConfirmedEventListener = new OrderCancellationConfirmedEventListener(
                widget) {

            @Override
            public void handleCancelConfirmedEvent(final InputWidget<DefaultListWidgetModel<TypedObject>, V2CancellationController> widget,
                    final Event event) throws InterruptedException {
                try {
                    final TypedObject cancellationRequest = widget.getWidgetController().requestFullCancellation();
                    if (cancellationRequest != null) {
                        closePopups();
                        final OrderCancelRecordEntryModel orderCancelRecordEntryModel = (OrderCancelRecordEntryModel) cancellationRequest
                                .getObject();
                        Messagebox.show(
                                LabelUtils.getLabel(widget, "cancellationNumber", new Object[] { orderCancelRecordEntryModel.getCode() }),
                                LabelUtils.getLabel(widget, "cancellationNumberTitle", new Object[0]), 1, "z-msgbox z-msgbox-information");
                        widget.getWidgetController().dispatchEvent(null, widget, null);
                    } else {
                        Messagebox.show(LabelUtils.getLabel(widget, "errorCreatingRequest", new Object[0]),
                                LabelUtils.getLabel(widget, "failed", new Object[0]), 1, "z-msgbox z-msgbox-error");
                    }
                } catch (final ValidationException e) {
                    LOGGER.error("Could not cancel full order due to validation failure.", e);
                    createExceptionMessagebox(e.getMessage(), "failedToValidate", e, widget);
                } catch (final OrderCancelException e) {
                    LOGGER.error("Could not cancel full order.", e);
                    createExceptionMessagebox(e.getMessage(), "failedToCancel", e, widget);
                }
            }

            /**
             * Creates a message box for exception message display
             *
             * @param message
             * @param title
             * @param e
             * @param widget
             * @throws InterruptedException
             */
            protected void createExceptionMessagebox(final String message, final String title, final Exception e,
                    final InputWidget<DefaultListWidgetModel<TypedObject>, V2CancellationController> widget) throws InterruptedException {
                Messagebox.show(
                        new StringBuilder(String.valueOf(message)).append(
                                e.getCause() != null ? new StringBuilder(" - ").append(e.getCause().getMessage()).toString() : "")
                                .toString(), LabelUtils.getLabel(widget, title, new Object[0]), 1, "z-msgbox z-msgbox-error");
            }

            /**
             * Triggers the 'onClose' event for all the popups for the current popup parent's children that are instance of Window
             */
            private void closePopups() {
                if (getPopupWidgetHelper().getCurrentPopup() != null) {
                    final List<Component> children = getPopupWidgetHelper().getCurrentPopup().getParent().getChildren();
                    for (final Component component : children) {
                        if (component instanceof Window) {
                            Events.postEvent(new Event("onClose", component));
                        }
                    }
                }
            }
        };
        return fullCancellationConfirmedEventListener;
    }

    /**
     * Gets the popup widget helper.
     *
     * @return the popup widget helper
     */
    protected PopupWidgetHelper getPopupWidgetHelper() {
        return popupWidgetHelper;
    }

    /**
     * Gets the common i18 n service.
     *
     * @return the common i18 n service
     */
    protected CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    /**
     * Gets the session service.
     *
     * @return the session service
     */
    protected SessionService getSessionService() {
        return sessionService;
    }

    /**
     * Gets the format factory.
     *
     * @return the format factory
     */
    protected FormatFactory getFormatFactory() {
        return formatFactory;
    }
}
