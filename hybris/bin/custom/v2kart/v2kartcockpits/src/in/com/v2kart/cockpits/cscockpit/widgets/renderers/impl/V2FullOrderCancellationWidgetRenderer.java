package in.com.v2kart.cockpits.cscockpit.widgets.renderers.impl;

import in.com.v2kart.cockpits.cscockpit.widgets.controllers.impl.V2CancellationController;
import in.com.v2kart.cockpits.cscockpit.widgets.listeners.OrderCancellationConfirmationPopupCloseListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.api.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Window;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.cockpit.model.meta.PropertyDescriptor;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.services.config.EditorRowConfiguration;
import de.hybris.platform.cockpit.services.values.ObjectValueContainer;
import de.hybris.platform.cockpit.services.values.ObjectValueContainer.ObjectValueHolder;
import de.hybris.platform.cockpit.widgets.InputWidget;
import de.hybris.platform.cockpit.widgets.Widget;
import de.hybris.platform.cockpit.widgets.WidgetConfig;
import de.hybris.platform.cockpit.widgets.WidgetContainer;
import de.hybris.platform.cockpit.widgets.impl.DefaultWidgetContainer;
import de.hybris.platform.cockpit.widgets.impl.DefaultWidgetFactory;
import de.hybris.platform.cockpit.widgets.models.impl.DefaultListWidgetModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.V2StoreCreditPaymentInfoModel;
import de.hybris.platform.cscockpit.exceptions.ValidationException;
import de.hybris.platform.cscockpit.utils.LabelUtils;
import de.hybris.platform.cscockpit.widgets.controllers.CancellationController;
import de.hybris.platform.cscockpit.widgets.controllers.ReturnsController;
import de.hybris.platform.cscockpit.widgets.renderers.impl.FullOrderCancellationWidgetRenderer;
import de.hybris.platform.cscockpit.widgets.renderers.utils.edit.BasicPropertyDescriptor;
import de.hybris.platform.cscockpit.widgets.renderers.utils.edit.BasicPropertyEditorRowConfiguration;
import de.hybris.platform.ordercancel.OrderCancelRequest;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

/**
 * Renderer to modify displaying the default full order cancellation popup.
 * 
 * @author pravesh.gupta@nagarro.com
 */
public class V2FullOrderCancellationWidgetRenderer extends FullOrderCancellationWidgetRenderer {

    private static final Logger LOGGER = Logger.getLogger(V2FullOrderCancellationWidgetRenderer.class);
    private static final String WALLET = "V2-Wallet";
    private static final String CASH = "CASH";
    private transient List<PropertyDescriptor> orderCancelRequestProperties;
    private transient Map<PropertyDescriptor, EditorRowConfiguration> cancelRequestRowConfigurations;
    private Map<String, String> v2CancelReasons;

    public Map<String, String> getV2CancelReasons() {
        return v2CancelReasons;
    }

    public void setV2CancelReasons(Map<String, String> v2CancelReasons) {
        this.v2CancelReasons = v2CancelReasons;
    }

    /**
     * Same as parent although do not show message box on creation of order cancellation request instead shows a confirmation popup widget.
     * 
     * @see de.hybris.platform.cscockpit.widgets.renderers.impl.FullOrderCancellationWidgetRenderer#createContentInternal(de.hybris.platform.
     *      cockpit.widgets.InputWidget, org.zkoss.zk.ui.api.HtmlBasedComponent)
     */
    @Override
    protected HtmlBasedComponent createContentInternal(
            final InputWidget<DefaultListWidgetModel<OrderCancelRequest>, CancellationController> widget,
            final HtmlBasedComponent rootContainer) {
        final Div container = new Div();
        container.setSclass("partialOrderCancellationWidget");
        try {
            Map cancelableEntries = ((CancellationController) widget.getWidgetController()).getCancelableOrderEntries();
            if (cancelableEntries != null && !cancelableEntries.isEmpty()) {

                final Div orderCancelRequestBox = new Div();
                orderCancelRequestBox.setParent(container);
                final ObjectValueContainer orderCancelRequestObjectValueContainer = createOrderCancelRequestEditors(widget,
                        orderCancelRequestBox);

                Div refundOptionRadioBox = new Div();
                refundOptionRadioBox.setParent(container);

                final Div cancellationOrderButtonBox = new Div();
                cancellationOrderButtonBox.setParent(container);
                cancellationOrderButtonBox.setVisible(true);
                cancellationOrderButtonBox.setSclass("cancellationOrderButtonBox");

                final Button attemptCancellationButton = new Button(LabelUtils.getLabel(widget, "cancelOrderButton", new Object[0]));
                attemptCancellationButton.setVisible(true);
                attemptCancellationButton.setParent(cancellationOrderButtonBox);
                attemptCancellationButton.addEventListener("onClick",
                        createAttemptCancellationEventListener(widget, orderCancelRequestObjectValueContainer));

                OrderModel orderModel = ((OrderModel) ((CancellationController) widget.getWidgetController()).getOrder().getObject());

                Radiogroup refundOptionRadioGroup = new Radiogroup();
                Radio refundToWalletRadio = new Radio(LabelUtils.getLabel(widget, "refundToWallet", new Object[0]));
                refundToWalletRadio.setParent(refundOptionRadioGroup);

                boolean result = true;
                for (PaymentTransactionModel transactionModel : orderModel.getPaymentTransactions()) {
                    for (PaymentTransactionEntryModel transactionEntryModel : transactionModel.getEntries()) {
                        if (PaymentTransactionType.CAPTURE.equals(transactionEntryModel.getType())) {
                            if (WALLET.equalsIgnoreCase(transactionModel.getPaymentProvider())
                                    || CASH.equalsIgnoreCase(transactionModel.getPaymentProvider())) {
                                result = false;
                            }
                        }
                    }
                }
                if (result) {
                    Radio refundToGatewayRadio = new Radio(LabelUtils.getLabel(widget, "refundToGateway", new Object[0]));
                    refundToGatewayRadio.setParent(refundOptionRadioGroup);
                    refundToGatewayRadio.addEventListener(Events.ON_CHECK,
                            createCancelRequestToGatewayEventListener(widget, orderCancelRequestObjectValueContainer));
                }
                refundOptionRadioGroup.setParent(refundOptionRadioBox);
                refundToWalletRadio.addEventListener(Events.ON_CHECK,
                        createCancelRequestToWalletEventListener(widget, orderCancelRequestObjectValueContainer));

            } else {
                Label dummyLabel = new Label(LabelUtils.getLabel(widget, "cantCancel", new Object[0]));
                dummyLabel.setSclass("csCantCancel");
                dummyLabel.setParent(container);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to get cancelable entries", e);
        }
        return container;
    }

    protected class ReturnRequestToGatewayEventListener
            implements EventListener
    {
        private final InputWidget<DefaultListWidgetModel<OrderCancelRequest>, CancellationController> widget;
        private final ObjectValueContainer orderCancelRequestObjectValueContainer;

        public ReturnRequestToGatewayEventListener(InputWidget<DefaultListWidgetModel<OrderCancelRequest>, CancellationController> widget2,
                ObjectValueContainer orderCancelRequestObjectValueContainer)
        {
            this.widget = (InputWidget<DefaultListWidgetModel<OrderCancelRequest>, CancellationController>) widget2;
            this.orderCancelRequestObjectValueContainer = orderCancelRequestObjectValueContainer;
        }

        protected ObjectValueContainer getReturnObjectValueContainers() {
            return orderCancelRequestObjectValueContainer;
        }

        public void onEvent(Event event)
                throws Exception
        {

            for (ObjectValueHolder objectValueHolder : orderCancelRequestObjectValueContainer.getAllValues()) {
                if (objectValueHolder.getPropertyDescriptor().getQualifier().equalsIgnoreCase("V2OrderCancelRequest.returnToWallet")) {
                    objectValueHolder.setLocalValue(false);
                } else if (objectValueHolder.getPropertyDescriptor().getQualifier()
                        .equalsIgnoreCase("V2OrderCancelRequest.returnToGateway")) {
                    objectValueHolder.setLocalValue(true);
                }

            }

        }
    }

    protected class ReturnRequestToWalletEventListener
            implements EventListener
    {
        private final InputWidget<DefaultListWidgetModel<OrderCancelRequest>, CancellationController> widget;
        private final ObjectValueContainer orderCancelRequestObjectValueContainer;

        public ReturnRequestToWalletEventListener(InputWidget<DefaultListWidgetModel<OrderCancelRequest>, CancellationController> widget2,
                ObjectValueContainer orderCancelRequestObjectValueContainer)
        {
            this.widget = (InputWidget<DefaultListWidgetModel<OrderCancelRequest>, CancellationController>) widget2;
            this.orderCancelRequestObjectValueContainer = orderCancelRequestObjectValueContainer;
        }

        protected ObjectValueContainer getReturnObjectValueContainers() {
            return orderCancelRequestObjectValueContainer;
        }

        public void onEvent(Event event)
                throws Exception
        {

            for (ObjectValueHolder objectValueHolder : orderCancelRequestObjectValueContainer.getAllValues()) {
                if (objectValueHolder.getPropertyDescriptor().getQualifier().equalsIgnoreCase("V2OrderCancelRequest.returnToGateway")) {
                    objectValueHolder.setLocalValue(false);
                } else if (objectValueHolder.getPropertyDescriptor().getQualifier().equalsIgnoreCase("V2OrderCancelRequest.returnToWallet")) {
                    objectValueHolder.setLocalValue(true);
                }
            }

        }
    }

    private EventListener createCancelRequestToWalletEventListener(
            InputWidget<DefaultListWidgetModel<OrderCancelRequest>, CancellationController> widget,
            ObjectValueContainer orderCancelRequestObjectValueContainer) {

        return new ReturnRequestToWalletEventListener(widget, orderCancelRequestObjectValueContainer);
    }

    private EventListener createCancelRequestToGatewayEventListener(
            InputWidget<DefaultListWidgetModel<OrderCancelRequest>, CancellationController> widget,
            ObjectValueContainer orderCancelRequestObjectValueContainer) {

        return new ReturnRequestToGatewayEventListener(widget, orderCancelRequestObjectValueContainer);

    }

    /**
     * Handles the full cancellation request creation. Create full cancellation preview. If successful creates the confirmation popup window
     * else shows the error message
     * 
     * @see de.hybris.platform.cscockpit.widgets.renderers.impl.FullOrderCancellationWidgetRenderer#handleAttemptCancellationEvent(de.hybris.
     *      platform.cockpit.widgets.InputWidget, org.zkoss.zk.ui.event.Event,
     *      de.hybris.platform.cockpit.services.values.ObjectValueContainer)
     */
    @Override
    protected void handleAttemptCancellationEvent(
            final InputWidget<DefaultListWidgetModel<OrderCancelRequest>, CancellationController> widget, final Event event,
            final ObjectValueContainer orderCancelRequestObjectValueContainer) throws Exception {
        try {
            final OrderCancelRequest orderCancelRequest = ((V2CancellationController) widget.getWidgetController())
                    .createFullCancellationPreview(orderCancelRequestObjectValueContainer);
            if (orderCancelRequest != null) {
                createCancelConfirmationPopupWindow(widget, getPopupWidgetHelper().getCurrentPopup().getParent());
            } else {
                Messagebox.show(LabelUtils.getLabel(widget, "failedToValidate", new Object[0]),
                        LabelUtils.getLabel(widget, "failedToValidate", new Object[0]), 1, "z-msgbox z-msgbox-error");
            }
        } catch (final ValidationException e) {
            LOGGER.info("Could not create the full confirmation popup due to validation errors.", e);
            Messagebox.show(
                    new StringBuilder(String.valueOf(e.getMessage())).append(
                            e.getCause() != null ? new StringBuilder(" - ").append(e.getCause().getMessage()).toString() : "").toString(),
                    LabelUtils.getLabel(widget, "failedToValidate", new Object[0]), 1, "z-msgbox z-msgbox-error");
        }
    }

    /**
     * Creates the cancel confirmation popup window.
     * 
     * @param parentWidget
     *        the parent widget
     * @param parentWindow
     *        the parent window
     * @return the window
     */
    protected Window createCancelConfirmationPopupWindow(final InputWidget parentWidget, final Component parentWindow) {
        final WidgetContainer widgetContainer = new DefaultWidgetContainer(new DefaultWidgetFactory());
        final Widget popupWidget = createPopupWidget(widgetContainer, "v2FullOrderCancelConfirmationWidgetConfig",
                "csFullOrderCancelConfirmationWidget-Popup");
        final Window popup = new Window();
        popup.setSclass("csRefundConfirmationWidget");
        popup.appendChild(popupWidget);
        popup.addEventListener("onClose", new OrderCancellationConfirmationPopupCloseListener(parentWidget, parentWindow, widgetContainer,
                popup) {
            @Override
            public void handleOrderCancellationConfirmationPopupCloseEvent(final Event event, final InputWidget parentWidget,
                    final WidgetContainer widgetContainer, final Component parentWindow, final Window popup) {
                ((V2CancellationController) parentWidget.getWidgetController()).cleanUp();
                widgetContainer.cleanup();
                parentWindow.removeChild(popup);
            }
        });
        popup.setTitle(LabelUtils.getLabel(popupWidget, "popup.fullOrderCancelConfirmationTitle", new Object[0]));
        popup.setParent(parentWindow);
        popup.doHighlighted();
        popup.setClosable(true);
        popup.setWidth("900px");
        return popup;
    }

    /**
     * Creates the popup widget.
     * 
     * @param widgetContainer
     *        the widget container
     * @param widgetConfig
     *        the widget config
     * @param popupCode
     *        the popup code
     * @return the widget
     */
    protected Widget createPopupWidget(final WidgetContainer widgetContainer, final String widgetConfig, final String popupCode) {
        final WidgetConfig popupWidgetConfig = (WidgetConfig) SpringUtil.getBean(widgetConfig);
        final Map widgetMap = widgetContainer.initialize(Collections.singletonMap(popupCode, popupWidgetConfig));
        return (Widget) widgetMap.get(popupCode);
    }

    @Override
    protected List<CancelReason> getAllCancelReasons() {
        List<CancelReason> filteredCancelReasons = new ArrayList();
        List<CancelReason> cancelReasons = super.getAllCancelReasons();
        for (CancelReason cancelReason : cancelReasons) {
            if (getV2CancelReasons().containsKey(cancelReason.getCode())) {
                filteredCancelReasons.add(cancelReason);
            }
        }
        return filteredCancelReasons;
    }

    public List<PropertyDescriptor> getOrderCancelRequestProperties2() {

        return this.orderCancelRequestProperties;
    }

    @Override
    public List<PropertyDescriptor> getOrderCancelRequestProperties() {

        if (this.orderCancelRequestProperties == null)
        {
            this.orderCancelRequestProperties = new ArrayList();

            BasicPropertyDescriptor spdReason = new BasicPropertyDescriptor("OrderCancelRequest.cancelReason", "ENUM");
            spdReason.setAvailableValues(Arrays.asList(getAllCancelReasons().toArray()));

            spdReason.setOccurrence(PropertyDescriptor.Occurrence.REQUIRED);
            this.orderCancelRequestProperties.add(spdReason);
            this.orderCancelRequestProperties.add(new BasicPropertyDescriptor("OrderCancelRequest.notes", "TEXT"));
            this.orderCancelRequestProperties.add(new BasicPropertyDescriptor("V2OrderCancelRequest.returnToWallet", "BOOLEAN"));
            this.orderCancelRequestProperties.add(new BasicPropertyDescriptor("V2OrderCancelRequest.returnToGateway", "BOOLEAN"));

        }
        return this.orderCancelRequestProperties;
    }

    @Override
    protected Map<PropertyDescriptor, EditorRowConfiguration> getOrderCancelRequestPropertyEditorRowConfigurations() {
        if (this.cancelRequestRowConfigurations == null)
        {
            this.cancelRequestRowConfigurations = new HashMap();
            for (PropertyDescriptor property : getOrderCancelRequestProperties())
            {
                if (property.getQualifier().equals("V2OrderCancelRequest.returnToWallet")
                        || property.getQualifier().equals("V2OrderCancelRequest.returnToGateway")) {
                    this.cancelRequestRowConfigurations.put(property, new BasicPropertyEditorRowConfiguration(false, true, null, property));
                } else {
                    this.cancelRequestRowConfigurations.put(property, new BasicPropertyEditorRowConfiguration(true, true, null, property));
                }
            }
        }

        return this.cancelRequestRowConfigurations;
    }
}
