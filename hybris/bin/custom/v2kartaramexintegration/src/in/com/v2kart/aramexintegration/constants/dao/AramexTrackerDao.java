package in.com.v2kart.aramexintegration.constants.dao;

import in.com.v2kart.aramexintegration.ws.consignment.TrackingResult;

import java.util.List;

/**
 * ARAMEX tracker DAO for LSP integration
 * 
 * @author vikrant2480
 * 
 */
public interface AramexTrackerDao {

    /**
     * Update the consignment status and its current location
     * 
     * @param trackingResults
     *        : response from aramex containing list of AWB's and their updated statuses and updated locations
     */
    void updateOrderConsignmentStatus(final List<TrackingResult> trackingResults);

}
