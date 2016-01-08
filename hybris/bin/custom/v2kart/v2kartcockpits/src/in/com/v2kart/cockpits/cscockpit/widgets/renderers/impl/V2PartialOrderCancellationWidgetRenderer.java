package in.com.v2kart.cockpits.cscockpit.widgets.renderers.impl;

import in.com.v2kart.cockpits.cscockpit.widgets.controllers.impl.V2CancellationController;
import in.com.v2kart.cockpits.cscockpit.widgets.listeners.OrderCancellationConfirmationPopupCloseListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.api.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.cockpit.model.meta.PropertyDescriptor;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.services.config.EditorRowConfiguration;
import de.hybris.platform.cockpit.services.values.ObjectValueContainer;
import de.hybris.platform.cockpit.session.UISessionUtils;
import de.hybris.platform.cockpit.widgets.InputWidget;
import de.hybris.platform.cockpit.widgets.Widget;
import de.hybris.platform.cockpit.widgets.WidgetConfig;
import de.hybris.platform.cockpit.widgets.WidgetContainer;
import de.hybris.platform.cockpit.widgets.impl.DefaultWidgetContainer;
import de.hybris.platform.cockpit.widgets.impl.DefaultWidgetFactory;
import de.hybris.platform.cockpit.widgets.models.impl.DefaultListWidgetModel;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.cscockpit.exceptions.ValidationException;
import de.hybris.platform.cscockpit.utils.CockpitUiConfigLoader;
import de.hybris.platform.cscockpit.utils.LabelUtils;
import de.hybris.platform.cscockpit.utils.SafeUnbox;
import de.hybris.platform.cscockpit.widgets.controllers.CancellationController;
import de.hybris.platform.cscockpit.widgets.renderers.impl.PartialOrderCancellationWidgetRenderer;
import de.hybris.platform.cscockpit.widgets.renderers.utils.edit.PojoPropertyRendererUtil;
import de.hybris.platform.ordercancel.OrderCancelEntry;
import de.hybris.platform.ordercancel.OrderCancelRequest;

/**
 * Renderer for displaying of partial cancellation popup.
 *
 * @author pravesh.gupta@nagarro.com
 */
public class V2PartialOrderCancellationWidgetRenderer extends PartialOrderCancellationWidgetRenderer {

    private static final Logger LOGGER = Logger.getLogger(V2PartialOrderCancellationWidgetRenderer.class);
    private transient List orderCancelEntryObjectValueContainers;
    private Map<String, String> v2CancelReasons;	

    public Map<String, String> getV2CancelReasons() {
		return v2CancelReasons;
	}

	public void setV2CancelReasons(Map<String, String> v2CancelReasons) {
		this.v2CancelReasons = v2CancelReasons;
	}

	@Override
    protected HtmlBasedComponent createContentInternal(
            final InputWidget<DefaultListWidgetModel<TypedObject>, CancellationController> widget, final HtmlBasedComponent rootContainer) {
        orderCancelEntryObjectValueContainers = new ArrayList();
        final Div mainContainer = new Div();
        mainContainer.setSclass("partialOrderCancellationWidget");
        try {
            final Map cancelableEntries = widget.getWidgetController().getCancelableOrderEntries();
            if (cancelableEntries != null && !cancelableEntries.isEmpty()) {
                final HtmlBasedComponent container = new Div();
                container.setSclass("csListboxContainer");
                mainContainer.appendChild(container);
                final Listbox listBox = new Listbox();
                listBox.setParent(container);
                listBox.setVflex(false);
                listBox.setFixedLayout(true);
                listBox.setSclass("csWidgetListbox");
                renderListbox(listBox, widget, cancelableEntries);
                if (listBox.getItemCount() > 0 && listBox.getSelectedIndex() <= 0) {
                    listBox.setSelectedIndex(0);
                }
                final Div submitCancellationFormContainer = new Div();
                submitCancellationFormContainer.setParent(container);
                submitCancellationFormContainer.setSclass("partialOrderCancellationFooter");
                final Div orderCancelRequestBox = new Div();
                orderCancelRequestBox.setParent(submitCancellationFormContainer);
                final ObjectValueContainer orderCancelRequestObjectValueContainer = createOrderCancelRequestEditors(widget,
                        orderCancelRequestBox);
                final Div cancellationOrderButtonBox = new Div();
                cancellationOrderButtonBox.setParent(submitCancellationFormContainer);
                cancellationOrderButtonBox.setSclass("cancellationOrderButtonBox");
                final Button attemptCancellationButton = new Button(LabelUtils.getLabel(widget, "cancelOrderButton", new Object[0]));
                attemptCancellationButton.setParent(cancellationOrderButtonBox);
                attemptCancellationButton.addEventListener(
                        "onClick",
                        createAttemptCancellationEventListener(widget, orderCancelRequestObjectValueContainer,
                                orderCancelEntryObjectValueContainers));
            } else {
                final Label dummyLabel = new Label(LabelUtils.getLabel(widget, "cantCancel", new Object[0]));
                dummyLabel.setSclass("csCantCancel");
                dummyLabel.setParent(mainContainer);
            }
        } catch (final Exception e) {
            LOGGER.error("Failed to get cancelable entries", e);
        }
        return mainContainer;
    }

    @Override
    protected void handleAttemptCancellationEvent(final InputWidget<DefaultListWidgetModel<TypedObject>, CancellationController> widget,
            final Event event, final ObjectValueContainer orderCancelRequestOVC,
            final List<ObjectValueContainer> orderEntryCancelRecordEntries) throws Exception {
        try {
            final OrderCancelRequest orderCancelRequest = ((V2CancellationController) widget.getWidgetController())
                    .createPartialCancellationPreview(orderEntryCancelRecordEntries, orderCancelRequestOVC);
            if (orderCancelRequest != null) {
                createCancelConfirmationPopupWindow(widget, getPopupWidgetHelper().getCurrentPopup().getParent());
            } else {
                Messagebox.show(LabelUtils.getLabel(widget, "failedToValidate", new Object[0]),
                        LabelUtils.getLabel(widget, "failedToValidate", new Object[0]), 1, "z-msgbox z-msgbox-error");
            }
        } catch (final ValidationException e) {
            LOGGER.info("Could not create the partial confirmation popup due to validation errors.", e);
            Messagebox.show(
                    new StringBuilder(String.valueOf(e.getMessage())).append(
                            e.getCause() != null ? new StringBuilder(" - ").append(e.getCause().getMessage()).toString() : "").toString(),
                    LabelUtils.getLabel(widget, "failedToValidate", new Object[0]), 1, "z-msgbox z-msgbox-error");
        }
    }

    @Override
    protected void renderCancelableOrderEntry(final InputWidget widget, final Listitem parent, final TypedObject orderEntry,
            final Long cancelableAmount, final List columns, final Map editorMapping) {
        if (orderEntry != null && orderEntry.getObject() instanceof OrderEntryModel) {
            final OrderEntryModel orderEntryModel = (OrderEntryModel) orderEntry.getObject();
            final long cancelableAmountLong = SafeUnbox.toLong(cancelableAmount);
            if (cancelableAmountLong > 0L) {
                final Listcell entryNoCell = new Listcell();
                entryNoCell.setParent(parent);
                final Div entryNoDiv = new Div();
                entryNoDiv.setParent(entryNoCell);
                entryNoDiv.setSclass("editorWidgetEditor");
                final Label entryNoLabel = new Label(String.valueOf(orderEntryModel.getEntryNumber()));
                entryNoLabel.setParent(entryNoDiv);
                Listcell cell = new Listcell();
                cell.setSclass("productDetailsCell");
                cell.setParent(parent);
                getPropertyRendererHelper().buildPropertyValuesFromColumnConfigs(
                        orderEntry,
                        CockpitUiConfigLoader.getDirectVisibleColumnConfigurations(CockpitUiConfigLoader.getColumnGroupConfiguration(
                                UISessionUtils.getCurrentSession(), getProductInfoConfigurationCode(), orderEntry.getType().getCode())),
                        cell);
                cell = new Listcell();
                cell.setSclass("maxCancellationQtyCell");
                cell.setParent(parent);
                final Label maxQuantityLabel = new Label(String.valueOf(cancelableAmount));
                maxQuantityLabel.setParent(cell);
                final ObjectValueContainer orderCancelEntryObjectValueContainer = buildOrderEntryCancelRecordEntryObjectValueContainer(
                        orderEntry, null, columns, Collections.EMPTY_SET);
                orderCancelEntryObjectValueContainers.add(orderCancelEntryObjectValueContainer);
                PropertyDescriptor propertyDescriptor;
                Div editorDiv;
                for (final Iterator iterator = columns.iterator(); iterator.hasNext(); PojoPropertyRendererUtil.renderEditor(editorDiv,
                        propertyDescriptor, (EditorRowConfiguration) editorMapping.get(propertyDescriptor), null,
                        orderCancelEntryObjectValueContainer, widget, false)) {
                    propertyDescriptor = (PropertyDescriptor) iterator.next();
                    cell = new Listcell();
                    cell.setParent(parent);
                    editorDiv = new Div();
                    editorDiv.setParent(cell);
                    editorDiv.setSclass("editorWidgetEditor");
                }

            }
        }
    }

    /**
     * Creates the confirmation popup window.
     *
     * @param parentWidget
     *        Parent widget to new popup to be created
     * @param parentWindow
     *        Parent window of the popup to be created
     * @return The created popup window
     */
    protected Window createCancelConfirmationPopupWindow(final InputWidget parentWidget, final Component parentWindow) {
        final WidgetContainer widgetContainer = new DefaultWidgetContainer(new DefaultWidgetFactory());
        final Widget popupWidget = createPopupWidget(widgetContainer, "v2PartialOrderCancelConfirmationWidgetConfig",
                "csPartialOrderCancelConfirmationWidget-Popup");
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
        popup.setTitle(LabelUtils.getLabel(popupWidget, "popup.partialOrderCancelConfirmationTitle", new Object[0]));
        popup.setParent(parentWindow);
        popup.doHighlighted();
        popup.setClosable(true);
        popup.setWidth("900px");
        return popup;
    }

    /**
     * Creates the popup for confirmation the {@link WidgetConfig} and then initializes the {@link WidgetContainer}
     *
     * @param widgetContainer
     *        Container of the popup that is initialized
     * @param widgetConfig
     *        Code of widget config defined in resource file.
     * @param popupCode
     *        Code of the popup
     * @return The popup widget created
     */
    protected Widget createPopupWidget(final WidgetContainer widgetContainer, final String widgetConfig, final String popupCode) {
        final WidgetConfig popupWidgetConfig = (WidgetConfig) SpringUtil.getBean(widgetConfig);
        final Map widgetMap = widgetContainer.initialize(Collections.singletonMap(popupCode, popupWidgetConfig));
        return (Widget) widgetMap.get(popupCode);
    }

    /**
     * Gets orderEntries
     *
     * @return {@link List} of {@link ObjectValueContainer} containing {@link OrderCancelEntry}
     */
    protected List getOrderCancelEntryObjectValueContainers() {
        return orderCancelEntryObjectValueContainers;
    }
    
    @Override
    protected List<CancelReason> getAllCancelReasons() {
    	List<CancelReason> filteredCancelReasons= new ArrayList();
		List<CancelReason> cancelReasons=super.getAllCancelReasons();
		for(CancelReason cancelReason: cancelReasons){
			if(getV2CancelReasons().containsKey(cancelReason.getCode())){
				filteredCancelReasons.add(cancelReason);
			}
		}
		return filteredCancelReasons;
	}
    
    
}
