/**
 * 
 */
package in.com.v2kart.dotzotlspintegration.service;

import de.hybris.platform.ordersplitting.model.ConsignmentModel;

import in.com.v2kart.core.services.LSPTrackerService;
import in.com.v2kart.dotzotlspintegration.exception.V2DotzotIntegrationException;
import in.com.v2kart.dotzotlspintegration.ws.consignment.Consignment;
import in.com.v2kart.dotzotlspintegration.ws.consignment.TrackConsignmentHeaderNew;

import java.util.List;

/**
 * @author arunkumar
 * 
 */
public interface DotzotTrackerService extends LSPTrackerService {
    /**
     * @param consignments
     * @return ConsignmentTrackEventsDetailsNew
     */
    TrackConsignmentHeaderNew getOrderConsignmentTrackerRequest(final List<ConsignmentModel> consignments);

    List<Consignment> getOrderConsignmentTrackerResponse(TrackConsignmentHeaderNew consignmentRequest) throws V2DotzotIntegrationException;
}
