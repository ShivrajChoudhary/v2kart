package in.com.v2kart.core.order;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import in.com.v2kart.core.V2CartService;
import in.com.v2kart.core.delivery.dao.impl.V2CountryZoneDeliveryModeDaoImpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.order.impl.DefaultCartService;
import de.hybris.platform.util.PriceValue;

/**
 * 
 * @author Rampal Lather
 * 
 */
public class V2CartServiceImpl extends DefaultCartService implements V2CartService {
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(V2CartServiceImpl.class);

    private V2CountryZoneDeliveryModeDaoImpl countryZoneDeliveryModeDao;

    // step 1 : delegate to Dao
    @Override
    public PriceValue getShopMinimumForFreeDelivery(AbstractOrderModel order) {

        try {
            final DeliveryModeModel deliveryMode = order.getDeliveryMode();
            validateParameterNotNullStandardMessage("deliveryMode", deliveryMode);

            return countryZoneDeliveryModeDao.getMinimumAmountForFreeShipping(deliveryMode, order);
        } catch (final Exception e) {
            LOG.warn("Could not find the amount required for Free Shipping for order [" + order.getCode() + "] due to : " + e.getMessage()
                    + "... skipping!");
            return new PriceValue(order.getCurrency().getIsocode(), 0.0, order.getNet().booleanValue());
        }
    }

    @Required
    public void setCountryZoneDeliveryModeDao(V2CountryZoneDeliveryModeDaoImpl countryZoneDeliveryModeDao) {
        this.countryZoneDeliveryModeDao = countryZoneDeliveryModeDao;
    }
}
