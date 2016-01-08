/**
 * 
 */
package in.com.v2kart.delhiverylspintegration.dao;

import in.com.v2kart.delhiverylspintegration.data.response.DelhiveryOrderTrackerResponse;

/**
 * Delhivery tracker DAO for LSP integration
 * 
 * @author vikrant2480
 * 
 */
public interface DelhiveryTrackerDao {

    /**
     * Update the consignment status and its current location
     * 
     * @param delhiveryOrderTrackerResponse
     *        : response from REST call to Delhivery package/order tracker API
     */
    void updateOrderConsignmentStatus(final DelhiveryOrderTrackerResponse delhiveryOrderTrackerResponse);
}
