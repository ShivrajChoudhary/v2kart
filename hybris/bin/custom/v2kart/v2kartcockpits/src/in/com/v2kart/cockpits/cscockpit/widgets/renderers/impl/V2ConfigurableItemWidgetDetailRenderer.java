package in.com.v2kart.cockpits.cscockpit.widgets.renderers.impl;

import java.util.List;

import org.zkoss.zk.ui.api.HtmlBasedComponent;
import org.zkoss.zul.Div;

import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.services.config.ColumnConfiguration;
import de.hybris.platform.cockpit.widgets.Widget;
import de.hybris.platform.cscockpit.utils.CssUtils;
import de.hybris.platform.cscockpit.utils.ObjectGetValueUtils;
import de.hybris.platform.cscockpit.widgets.renderers.details.impl.ConfigurableItemWidgetDetailRenderer;

public class V2ConfigurableItemWidgetDetailRenderer extends ConfigurableItemWidgetDetailRenderer {

    private static final String EMAIL = "Email";
    private static final String CUSTOMER_ID = "Customer ID";

    @Override
    public HtmlBasedComponent createContent(Object context, TypedObject item, Widget widget) {
        Div detailContainer = new Div();

        if (item != null)
        {
            detailContainer.setSclass("csObject" + CssUtils.sanitizeForCss(item.getType().getCode()) +
                    "Container");
            List<ColumnConfiguration> columns = getColumnConfigurations(item);
            String customerId = null;
            for (ColumnConfiguration col : columns) {
                if ((getPropertyRendererHelper().getPropertyDescriptorName(col)).equalsIgnoreCase(EMAIL)) {
                    customerId = ObjectGetValueUtils.getValue(col.getValueHandler(), item);
                }
            }

            for (ColumnConfiguration col : columns)
            {
                if ((getPropertyRendererHelper().getPropertyDescriptorName(col)).equalsIgnoreCase(CUSTOMER_ID) && customerId != null) {
                    renderRow(getPropertyRendererHelper().getPropertyDescriptorName(col),
                            customerId, detailContainer);
                } else {
                    renderRow(getPropertyRendererHelper().getPropertyDescriptorName(col),
                            ObjectGetValueUtils.getValue(col.getValueHandler(), item), detailContainer);
                }
            }
        }
        else
        {
            detailContainer.setSclass("csObject" + CssUtils.sanitizeForCss(getConfigurationCode()) +
                    "Container");
        }
        return detailContainer;
    }
}
