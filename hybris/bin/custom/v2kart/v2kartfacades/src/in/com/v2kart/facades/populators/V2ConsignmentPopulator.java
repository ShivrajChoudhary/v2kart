package in.com.v2kart.facades.populators;

import de.hybris.platform.commercefacades.order.converters.populator.ConsignmentPopulator;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;

/**
 * @author shubhammaheshwari
 *
 */
public class V2ConsignmentPopulator extends ConsignmentPopulator {

    @Override
    public void populate(final ConsignmentModel source, final ConsignmentData target) {
        super.populate(source, target);
        target.setTrackingID(source.getTrackingID());
        target.setStatus(source.getStatus());
        target.setCurrentLocation(source.getCurrentLocation());
        if(null != source.getLsp()){
            target.setLspProvider(source.getLsp().getName());
            target.setTrackingUrl(source.getLsp().getTrackingUrl());
        }
        target.setStatusDisplay(source.getStatusDisplay());
    }
}
