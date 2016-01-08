package in.com.v2kart.cockpits.cscockpit.widgets.renderers.impl;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.api.HtmlBasedComponent;
import org.zkoss.zul.Div;

import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.widgets.InputWidget;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.cscockpit.widgets.controllers.CustomerController;
import de.hybris.platform.cscockpit.widgets.models.impl.CustomerItemWidgetModel;
import de.hybris.platform.cscockpit.widgets.renderers.impl.CustomerDetailsEditWidgetRenderer;

/**
 * Class V2CustomerDetailsEditWidgetRenderer extends CustomerDetailsEditWidgetRenderer to change the Css for the section rendered for
 * readonly customer details
 * 
 * @author shivanichopra
 * 
 */
public class V2CustomerDetailsEditWidgetRenderer extends CustomerDetailsEditWidgetRenderer {

    private static final Logger LOG = Logger.getLogger(V2CustomerDetailsEditWidgetRenderer.class);

    /**
     * This method overrides the super renderFooter to add custom css class ro the footerContent div.This div contains all the readonly
     * details for the customer.
     * 
     * @param content
     *        the html component
     * @param widget
     *        the edit customer details widget
     */
    protected void renderFooter(HtmlBasedComponent content,
            InputWidget<CustomerItemWidgetModel, CustomerController> widget) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("V2CustomerDetailsEditWidgetRenderer: Rendering customer readonly details widget");
        }
        Div footerContent = new Div();
        footerContent.setSclass("csCustomerFooter");
        footerContent.setStyle("height:auto");
        footerContent.setParent(content);

        TypedObject customer = ((CustomerItemWidgetModel) widget.getWidgetModel()).getCustomer();
        if ((customer == null) || (!(customer.getObject() instanceof CustomerModel)))
            return;
        HtmlBasedComponent detailContent = getFooterRenderer().createContent(null, customer, widget);
        if (detailContent == null)
            return;
        footerContent.appendChild(detailContent);
    }

}
