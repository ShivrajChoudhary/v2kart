/**
 * 
 */
package in.com.v2kart.delhiverylspintegration.services;

import de.hybris.platform.ordersplitting.model.ConsignmentModel;

import in.com.v2kart.core.services.LSPTrackerService;
import in.com.v2kart.delhiverylspintegration.data.request.DelhiveryOrderTrackerRequest;
import in.com.v2kart.delhiverylspintegration.data.response.DelhiveryOrderTrackerResponse;

import java.util.List;

/**
 * Delhivery Tracker service for LSP Integration
 * 
 * @author vikrant2480
 * 
 */
public interface DelhiveryTrackerService extends LSPTrackerService {

    /**
     * Get response from REST call to Delhivery package/order tracker API
     * 
     * @param delhiveryOrderTrackerRequest
     *        : request containing AWB's, token and verbose
     * @return : delhiveryOrderTrackerResponse containing the AWB's status and current location
     */
    DelhiveryOrderTrackerResponse getOrderConsignmentTrackerResponse(final DelhiveryOrderTrackerRequest delhiveryOrderTrackerRequest);

    /**
     * Make request for REST call to Delhivery package/order tracker API
     * 
     * @param consignments
     *        : containing the list of waybills(AWB's)
     * @return : delhiveryOrderTrackerRequest containing the token, wayBills and verbose
     */
    DelhiveryOrderTrackerRequest getOrderConsignmentTrackerRequest(final List<ConsignmentModel> consignments);
}
