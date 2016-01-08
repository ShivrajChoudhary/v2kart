package in.com.v2kart.core.delivery.dao;

import de.hybris.platform.commerceservices.delivery.dao.CountryZoneDeliveryModeDao;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.jalo.order.delivery.JaloDeliveryModeException;
import de.hybris.platform.util.PriceValue;

/**
 * V2CountryZoneDeliveryModeDao performs various Dao operations pertaining to ZoneDeliveryModes
 * 
 * @author Rampal Lather
 *
 */

public interface V2CountryZoneDeliveryModeDao extends CountryZoneDeliveryModeDao {

    PriceValue getMinimumAmountForFreeShipping(DeliveryModeModel deliveryMode, AbstractOrderModel order) throws JaloDeliveryModeException;
}
