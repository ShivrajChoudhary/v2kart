/** 
 * 
 */
package in.com.v2kart.aramexintegration.service;

import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import in.com.v2kart.aramexintegration.exception.V2AramexIntegrationException;
import in.com.v2kart.aramexintegration.ws.consignment.ShipmentTrackingRequest;
import in.com.v2kart.aramexintegration.ws.consignment.ShipmentTrackingResponse;
import in.com.v2kart.aramexintegration.ws.consignment.TrackingResult;
import in.com.v2kart.core.services.LSPTrackerService;

/**
 * Aramex Tracker service for LSP Integration
 * 
 * @author shubhammaheshwari
 */
public interface AramexTrackerService extends LSPTrackerService {

    /**
     * Make request for SOAP call
     * 
     * @param consignments
     *        : containing the list of waybills(AWB's)
     * @return : shipmentTrackingRequest containing client info, shipments (AWB's)
     */
    ShipmentTrackingRequest getOrderConsignmentTrackerRequest(final List<ConsignmentModel> consignments);

    /**
     * Get response from SOAP call
     * 
     * @param shipmentTrackingRequest
     *        : request containing client info, shipments(AWB's)
     * @return : List of TrackingResult containing updated code and updated location for AWB's
     * @throws V2AramexIntegrationException
     */
//    List<TrackingResult> getOrderConsignmentTrackerResponse(final ShipmentTrackingRequest shipmentTrackingRequest)
 //           throws V2AramexIntegrationException;
    Map<String ,List<TrackingResult>> getOrderConsignmentTrackerResponse(final ShipmentTrackingRequest shipmentTrackingRequest)
    		throws V2AramexIntegrationException;

}
