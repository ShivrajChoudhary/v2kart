package in.com.v2kart.core.serviceability.daos;

import in.com.v2kart.core.model.ServiceabilityAreaModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.store.BaseStoreModel;


/**
 * @author Anuj
 *
 */
public interface V2ServiceabilityDao {

    /**
     * Check delivery option for pin code.
     * @return true, if successful
     */
    public boolean checkDeliveryOptionForPinCode(final String pinCode, boolean isNet, final CountryModel countryModel,
            final CurrencyModel currencyModel, final BaseStoreModel baseStoreModel);

    /**
     * Check delivery option for pin code.
     * 
     * @param pinCode
     *        the pin code
     * @return true, if successful
     */
    boolean checkDeliveryOptionForPinCode(final String pinCode);
    /**
     * Gets the serviceability area for pincode.
     * 
     * @param pincode
     *        the pincode
     * @return the serviceability area for pincode
     */
    ServiceabilityAreaModel getServiceabilityAreaForPincode(final String pincode);
}
