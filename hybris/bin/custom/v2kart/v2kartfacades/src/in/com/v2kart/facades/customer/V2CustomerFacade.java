/**
 * 
 */
package in.com.v2kart.facades.customer;

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;


/**
 * @author arunkumar
 *
 */
public interface V2CustomerFacade extends CustomerFacade {

    /**
     * update the List of phonenumbers in customer model
     * @param customerData
     */
    void updatePhoneNumbers(final CustomerData customerData, boolean retainOldVaue);
}
