package in.com.v2kart.cockpits.cscockpit.widgets.renderers.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.api.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.impl.api.InputElement;

import de.hybris.platform.cockpit.model.meta.ObjectType;
import de.hybris.platform.cockpit.model.meta.PropertyDescriptor;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.services.config.ColumnConfiguration;
import de.hybris.platform.cockpit.services.config.EditorConfiguration;
import de.hybris.platform.cockpit.services.config.EditorRowConfiguration;
import de.hybris.platform.cockpit.services.config.impl.PropertyColumnConfiguration;
import de.hybris.platform.cockpit.services.values.ObjectValueContainer;
import de.hybris.platform.cockpit.services.values.ObjectValueContainer.ObjectValueHolder;
import de.hybris.platform.cockpit.session.UISessionUtils;
import de.hybris.platform.cockpit.widgets.InputWidget;
import de.hybris.platform.cockpit.widgets.models.ListWidgetModel;
import de.hybris.platform.cockpit.widgets.models.impl.DefaultListWidgetModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.V2StoreCreditPaymentInfoModel;
import de.hybris.platform.cscockpit.utils.CockpitUiConfigLoader;
import de.hybris.platform.cscockpit.utils.LabelUtils;
import de.hybris.platform.cscockpit.utils.comparators.TypedObjectOrderEntryNumberComparator;
import de.hybris.platform.cscockpit.widgets.controllers.ReturnsController;
import de.hybris.platform.cscockpit.widgets.renderers.impl.ReturnRequestCreateWidgetRenderer;
import de.hybris.platform.cscockpit.widgets.renderers.utils.edit.BasicPropertyDescriptor;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;

public class V2RefundRequestCreateWidgetRenderer extends ReturnRequestCreateWidgetRenderer {

    private transient List<ObjectValueContainer> returnObjectValueContainers;

    protected List<ObjectValueContainer> getReturnObjectValueContainers() {
        return returnObjectValueContainers;
    }

    protected void setReturnObjectValueContainers(List<ObjectValueContainer> returnObjectValueContainers) {
        this.returnObjectValueContainers = returnObjectValueContainers;
    }

    private static final Logger LOG = Logger.getLogger(V2RefundRequestCreateWidgetRenderer.class);
    private static final String WALLET = "v2-Wallet";
    private static final String CASH = "CASH";

    @Override
    protected HtmlBasedComponent createContentInternal(
            InputWidget<DefaultListWidgetModel<TypedObject>, ReturnsController> widget,
            HtmlBasedComponent rootContainer) {

        Div content = new Div();

        returnObjectValueContainers = new ArrayList();

        if (((ReturnsController) widget.getWidgetController()).canReturn())

        {
            Div returnableContainer = new Div();
            returnableContainer.setSclass("csListboxContainer");
            returnableContainer.setParent(content);

            InputElement hiddenInput = new Textbox();
            hiddenInput.setVisible(false);
            hiddenInput.setFocus(true);
            hiddenInput.setParent(returnableContainer);

            Listbox listBox = new Listbox();
            listBox.setParent(returnableContainer);
            listBox.setVflex(false);
            listBox.setFixedLayout(true);
            listBox.setSclass("csWidgetListbox");

            renderListbox(listBox, widget);

            OrderModel orderModel = ((OrderModel) ((ReturnsController) widget.getWidgetController()).getCurrentOrder().getObject());

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
                        createReturnRequestToGatewayEventListener(widget, returnObjectValueContainers));
            }

            refundOptionRadioGroup.setParent(content);
            refundToWalletRadio.addEventListener(Events.ON_CHECK,
                    createReturnRequestToWalletEventListener(widget, returnObjectValueContainers));

            Div requestCreationContent = new Div();
            requestCreationContent.setSclass("csReturnRequestActions");
            requestCreationContent.setParent(content);

            Button createButton = new Button(LabelUtils.getLabel(widget, "createButton", new Object[0]));
            createButton.setParent(requestCreationContent);
            createButton.addEventListener("onClick",
                    createReturnRequestCreateEventListener(widget, returnObjectValueContainers));
        }
        else
        {
            Label dummyLabel = new Label(LabelUtils.getLabel(widget, "cantReturn", new Object[0]));
            dummyLabel.setSclass("csCantReturn");
            dummyLabel.setParent(content);
        }

        // return content;

        // Div content2 = (Div) super.createContentInternal(widget, rootContainer);

        return content;
    }

    private EventListener createReturnRequestToWalletEventListener(
            InputWidget<DefaultListWidgetModel<TypedObject>, ReturnsController> widget,
            List<ObjectValueContainer> returnObjectValueContainers2) {

        return new ReturnRequestToWalletEventListener(widget, returnObjectValueContainers2);
    }

    private EventListener createReturnRequestToGatewayEventListener(
            InputWidget<DefaultListWidgetModel<TypedObject>, ReturnsController> widget,
            List<ObjectValueContainer> returnObjectValueContainers2) {

        return new ReturnRequestToGatewayEventListener(widget, returnObjectValueContainers2);
    }

    @Override
    protected void renderListbox(Listbox listBox, InputWidget<DefaultListWidgetModel<TypedObject>, ReturnsController> widget) {
        // super.renderListbox(listBox, widget);

        ListWidgetModel widgetModel = (ListWidgetModel) widget.getWidgetModel();
        if (widgetModel == null)
            return;
        Map returnableOrderEntries = ((ReturnsController) widget.getWidgetController()).getReturnableOrderEntries();
        if ((returnableOrderEntries == null) || (returnableOrderEntries.isEmpty()))
            return;
        try
        {
            List<ColumnConfiguration> columns = getColumnConfigurations(getListConfigurationCode(),
                    getListConfigurationType());
            if (!(CollectionUtils.isNotEmpty(columns)))
                return;
            Listhead headRow = new Listhead();
            headRow.setParent(listBox);

            Listheader colEntryNoHeader = new Listheader(LabelUtils.getLabel(widget, "entryNumber", new Object[0]));
            colEntryNoHeader.setWidth("20px");
            colEntryNoHeader.setParent(headRow);

            Listheader colProductHeader = new Listheader(LabelUtils.getLabel(widget, "productInfo", new Object[0]));
            colProductHeader.setWidth("300px");
            colProductHeader.setParent(headRow);

            Listheader colMaxQtyHeader = new Listheader(LabelUtils.getLabel(widget, "maxQty", new Object[0]));
            colMaxQtyHeader.setWidth("55px");
            colMaxQtyHeader.setParent(headRow);

            for (ColumnConfiguration col : columns)
            {
                Listheader colHeader = new Listheader(getPropertyRendererHelper().getPropertyDescriptorName(col));
                colHeader.setParent(headRow);
                colHeader.setTooltiptext(col.getName());

            }

            EditorConfiguration editorConf = CockpitUiConfigLoader.getEditorConfiguration(
                    UISessionUtils.getCurrentSession(), getListEditorConfigurationCode(), getListConfigurationType());

            List editorMapping = getPropertyEditorHelper().getAllEditorRowConfigurations(
                    editorConf);

            List<TypedObject> orderEntries = new ArrayList<TypedObject>(returnableOrderEntries.keySet());
            Collections.sort(orderEntries, TypedObjectOrderEntryNumberComparator.INSTANCE);

            for (TypedObject item : orderEntries)
            {
                Listitem row = new Listitem();
                row.setSclass("listbox-row-item");
                row.setParent(listBox);

                Listcell entryNoCell = new Listcell();
                entryNoCell.setParent(row);
                Div entryNoDiv = new Div();
                entryNoDiv.setParent(entryNoCell);
                entryNoDiv.setSclass("editorWidgetEditor");
                Label entryNoLabel = new Label(String.valueOf(((AbstractOrderEntryModel) item.getObject())
                        .getEntryNumber()));
                entryNoLabel.setParent(entryNoDiv);

                Listcell productCell = new Listcell();
                productCell.setParent(row);
                Div productDiv = new Div();
                productDiv.setParent(productCell);
                productDiv.setSclass("editorWidgetEditor");
                List columnsOrderEntry = getColumnConfigurations(
                        getProductInfoConfigurationCode(), item.getType().getCode());
                getPropertyRendererHelper().buildPropertyValuesFromColumnConfigs(item, columnsOrderEntry, productDiv);

                Listcell maxQtyCell = new Listcell();
                maxQtyCell.setParent(row);
                Div maxQtyDiv = new Div();
                maxQtyDiv.setParent(maxQtyCell);
                maxQtyDiv.setSclass("editorWidgetEditor");
                Label maxQtyLabel = new Label(String.valueOf(returnableOrderEntries.get(item)));
                maxQtyLabel.setParent(maxQtyDiv);

                TypedObject returnEntryObject = null;
                ObjectType returnEntryType = getCockpitTypeService().getObjectType(getListConfigurationType());
                ObjectValueContainer returnEntryValueContainer = buildReturnEntryValueContainer(item, returnEntryType,
                        returnEntryType.getPropertyDescriptors(), getSystemService().getAvailableLanguageIsos());
                returnObjectValueContainers.add(returnEntryValueContainer);

                for (ColumnConfiguration column : columns)
                {
                    if (!(column instanceof PropertyColumnConfiguration))
                        continue;
                    PropertyColumnConfiguration col = (PropertyColumnConfiguration) column;

                    PropertyDescriptor propertyDescriptor = col.getPropertyDescriptor();
                    EditorRowConfiguration editorRowConfiguration = getPropertyEditorHelper()
                            .findConfigurationByPropertyDescriptor(editorMapping, propertyDescriptor);
                    if (editorRowConfiguration == null)
                        continue;
                    Listcell cell = new Listcell();
                    cell.setParent(row);
                    Div editorDiv = new Div();
                    editorDiv.setParent(cell);
                    editorDiv.setSclass("editorWidgetEditor");
                    renderEditor(editorDiv, editorRowConfiguration, returnEntryObject, returnEntryValueContainer,
                            propertyDescriptor, widget);

                }

            }

        } catch (Exception e)
        {
            LOG.error("failed to render return entries list", e);
        }

    }

    protected class ReturnRequestToWalletEventListener
            implements EventListener
    {
        private final InputWidget<DefaultListWidgetModel<TypedObject>, ReturnsController> widget;
        private final List<ObjectValueContainer> returnObjectValueContainers;

        public ReturnRequestToWalletEventListener(InputWidget<DefaultListWidgetModel<TypedObject>, ReturnsController> widget2,
                List<ObjectValueContainer> returnObjectValueContainers)
        {
            this.widget = (InputWidget<DefaultListWidgetModel<TypedObject>, ReturnsController>) widget2;
            this.returnObjectValueContainers = returnObjectValueContainers;
        }

        protected List<ObjectValueContainer> getReturnObjectValueContainers() {
            return returnObjectValueContainers;
        }

        public void onEvent(Event event)
                throws Exception
        {
            for (ObjectValueContainer ovc : this.returnObjectValueContainers) {
                for (ObjectValueHolder objectValueHolder : ovc.getAllValues()) {
                    if (objectValueHolder.getPropertyDescriptor().getQualifier().equalsIgnoreCase("ReturnEntry.returnToWallet")) {
                        objectValueHolder.setLocalValue(true);
                    }
                }

            }
        }
    }

    protected class ReturnRequestToGatewayEventListener
            implements EventListener
    {
        private final InputWidget<DefaultListWidgetModel<TypedObject>, ReturnsController> widget;
        private final List<ObjectValueContainer> returnObjectValueContainers;

        public ReturnRequestToGatewayEventListener(InputWidget<DefaultListWidgetModel<TypedObject>, ReturnsController> widget2,
                List<ObjectValueContainer> returnObjectValueContainers)
        {
            this.widget = (InputWidget<DefaultListWidgetModel<TypedObject>, ReturnsController>) widget2;
            this.returnObjectValueContainers = returnObjectValueContainers;
        }

        protected List<ObjectValueContainer> getReturnObjectValueContainers() {
            return returnObjectValueContainers;
        }

        public void onEvent(Event event)
                throws Exception
        {
            for (ObjectValueContainer ovc : this.returnObjectValueContainers) {
                for (ObjectValueHolder objectValueHolder : ovc.getAllValues()) {
                    if (objectValueHolder.getPropertyDescriptor().getQualifier().equalsIgnoreCase("ReturnEntry.returnToGateway")) {
                        objectValueHolder.setLocalValue(true);
                    }
                }

            }
        }
    }

    @Override
    protected ObjectValueContainer buildObjectValueContainer(ObjectType type, List<PropertyDescriptor> propertyDescriptors,
            Set<String> languageISOCodes) {

        BasicPropertyDescriptor propertyDescriptorForWallet = new BasicPropertyDescriptor("ReturnEntry.returnToWallet", "BOOLEAN");
        BasicPropertyDescriptor propertyDescriptorForGateway = new BasicPropertyDescriptor("ReturnEntry.returnToGateway", "BOOLEAN");
        propertyDescriptors.add(propertyDescriptorForWallet);
        propertyDescriptors.add(propertyDescriptorForGateway);

        ObjectValueContainer currentObjectValues = new ObjectValueContainer(type, null);

        for (PropertyDescriptor pd : propertyDescriptors)
        {
            if (pd.isLocalized())
            {
                for (String langIso : languageISOCodes)
                {
                    currentObjectValues.addValue(pd, langIso, getDefaultValue(pd));
                }
            }
            else
            {
                Object currentValue = getDefaultValue(pd);
                currentObjectValues.addValue(pd, null, currentValue);
            }
        }

        return currentObjectValues;

        // return super.buildObjectValueContainer(type, propertyDescriptors, languageISOCodes);
    }
}
