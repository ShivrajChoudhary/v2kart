package in.com.v2kart.facades.populators;


import de.hybris.platform.commercefacades.order.converters.populator.OrderEntryPopulator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;

public class V2OrderEntryPopulator extends OrderEntryPopulator {

    
    @Override
    public void populate(final AbstractOrderEntryModel source, final OrderEntryData target)
    {
        super.populate(source, target);
        target.setGiftWrap(source.isGiftWrap());
    }
}
