package in.com.v2kart.facades.populators;

import de.hybris.platform.commercefacades.storelocator.converters.populator.PointOfServicePopulator;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

public class V2PointOfServicePopulator extends PointOfServicePopulator {

	@Override
	public void populate(final PointOfServiceModel source,
			final PointOfServiceData target) {
		super.populate(source, target);
		target.setPhoneNo(source.getPhoneNo());
	}
}
