/**
 * 
 */
package in.com.v2kart.dotzotlspintegration.dao;

import in.com.v2kart.dotzotlspintegration.ws.consignment.Consignment;

import java.util.List;

/**
 * DOTZOT tracker for LSP.
 * 
 * @author arunkumar
 * 
 */
public interface DotzotTrackerDao {

    /**
     * Update the consignment status and its current location
     * 
     * @param trackingResults
     *        : response from dotzot containing list of AWB's and their updated statuses and updated locations
     */
    void updateOrderConsignmentStatus(final List<Consignment> trackingResults);

}
