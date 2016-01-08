/**
 * 
 */
package in.com.v2kart.ecomlspintegration.services;

import de.hybris.platform.ordersplitting.model.ConsignmentModel;

import in.com.v2kart.core.services.LSPTrackerService;
import in.com.v2kart.ecomlspintegration.data.request.EcomOrderTrackerRequest;
import in.com.v2kart.ecomlspintegration.data.response.EcomexpressObjectsType;

import java.util.List;

/**
 * @author arunkumar
 * 
 */
public interface EcomTrackerService extends LSPTrackerService {

    /**
     * Make request for REST call to Ecom Express package/order tracker API
     * 
     * @param consignments
     * @return EcomOrderTrackerRequest containing username, password, waybills
     */
    EcomOrderTrackerRequest getOrderConsignmentTrackerRequest(final List<ConsignmentModel> consignments);

    /**
     * Get response from REST call to Ecom Express package/order tracker API
     * 
     * @param ecomOrderTrackerRequest
     *        : request containing AWB's, token and verbose
     * @return : ecomOrderTrackerResponse containing the AWB's status and current location
     */
    EcomexpressObjectsType getOrderConsignmentTrackerResponse(final EcomOrderTrackerRequest ecomOrderTrackerRequest);

}
