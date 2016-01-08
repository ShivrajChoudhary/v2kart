package in.com.v2kart.ecomlspintegration.dao;

import in.com.v2kart.ecomlspintegration.data.response.EcomexpressObjectsType;

/**
 * @author arunkumar
 * 
 */
public interface EcomTrackerDao {

    /**
     * Update the consignment status and its current location
     * 
     * @param EcomOrderTrackerResponse
     *        : response from REST call to Ecom Express package/order tracker API
     */
    void updateOrderConsignmentStatus(final EcomexpressObjectsType ecomOrderTrackerResponse);
}
