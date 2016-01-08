package in.com.v2kart.facades.populators;

import de.hybris.platform.commercefacades.order.converters.populator.DeliveryModePopulator;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;

public class V2DeliveryModePopulator extends DeliveryModePopulator{

    @Override
    public void populate(final DeliveryModeModel source, final DeliveryModeData target)
    {
    super.populate(source, target);
    target.setIsPickUp(source.getIsPickUp());
    }
}
 