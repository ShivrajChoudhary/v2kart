package in.com.v2kart.cockpits.cscockpit.widgets.controllers;

import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.services.values.ObjectValueContainer;
import de.hybris.platform.cockpit.services.values.ValueHandlerException;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.cscockpit.exceptions.ValidationException;
import de.hybris.platform.cscockpit.widgets.controllers.CustomerCreateController;

/**
 * 
 * @author busamkumar
 *
 */
public interface V2CustomerCreateController extends CustomerCreateController {
    
    /**
     * Method for creating customer and applying validations on the customer fields.
     */
    public TypedObject createNewV2Customer(ObjectValueContainer paramObjectValueContainer, String paramString)
            throws DuplicateUidException, ValueHandlerException, ValidationException;

}
