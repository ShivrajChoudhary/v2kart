package in.com.v2kart.cockpits.cscockpit.widgets.renderers.impl;

import in.com.v2kart.cockpits.cscockpit.widgets.controllers.impl.V2DefaultCustomerCreateController;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Messagebox;

import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.services.values.ObjectValueContainer;
import de.hybris.platform.cockpit.services.values.ValueHandlerException;
import de.hybris.platform.cockpit.widgets.InputWidget;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.cscockpit.exceptions.ValidationException;
import de.hybris.platform.cscockpit.utils.LabelUtils;
import de.hybris.platform.cscockpit.widgets.controllers.CustomerCreateController;
import de.hybris.platform.cscockpit.widgets.models.impl.DefaultMasterDetailListWidgetModel;
import de.hybris.platform.cscockpit.widgets.renderers.impl.CustomerCreateWidgetRenderer;

/**
 * Class overridden for calling the overridden create new customer method.
 * 
 * @author busamkumar
 *
 */
public class V2CustomerCreateWidgetRenderer extends CustomerCreateWidgetRenderer{
    
    private static final Logger LOG = Logger.getLogger(V2CustomerCreateWidgetRenderer.class);
    
    @Override
    protected void handleCreateItemEvent(InputWidget<DefaultMasterDetailListWidgetModel<TypedObject>, CustomerCreateController> widget,
            Event event, ObjectValueContainer customerObjectValueContainer, String customerConfigurationTypeCode) throws Exception {
        
        try {
            TypedObject customerItem = ((V2DefaultCustomerCreateController) widget.getWidgetController()).createNewV2Customer(
                    customerObjectValueContainer, customerConfigurationTypeCode);
            if (customerItem != null)
                getItemAppender().add(customerItem, 1L);
        } catch (DuplicateUidException e) {
            Messagebox.show(e.getMessage(), LabelUtils.getLabel(widget, "accountAlreadyExists", new Object[0]), 1,
                    "z-msgbox z-msgbox-error");
            LOG.debug("unable to create item", e);
        } catch (ValueHandlerException ex) {
            Messagebox.show(ex.getMessage(), LabelUtils.getLabel(widget, "unableToCreateCustomer", new Object[0]), 1,
                    "z-msgbox z-msgbox-error");
            LOG.debug("unable to create item", ex);
        } catch (ValidationException ex) {
            Messagebox
                    .show((new StringBuilder(String.valueOf(ex.getMessage()))).append(
                            ex.getCause() != null ? (new StringBuilder(" - ")).append(ex.getCause().getMessage()).toString() : "").toString(),
                            LabelUtils.getLabel(widget, "failedToValidate", new Object[0]), 1, "z-msgbox z-msgbox-error");
        }
        
    }

}
