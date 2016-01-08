/**
 * 
 */
package in.com.v2kart.aramexintegration.service.impl;

import in.com.v2kart.aramexintegration.constants.dao.AramexTrackerDao;
import in.com.v2kart.aramexintegration.exception.V2AramexIntegrationException;
import in.com.v2kart.aramexintegration.service.AramexTrackerService;
import in.com.v2kart.aramexintegration.ws.consignment.ArrayOfKeyValueOfstringArrayOfTrackingResultmFAkxlpY;
import in.com.v2kart.aramexintegration.ws.consignment.ArrayOfNotification;
import in.com.v2kart.aramexintegration.ws.consignment.ArrayOfTrackingResult;
import in.com.v2kart.aramexintegration.ws.consignment.ArrayOfstring;
import in.com.v2kart.aramexintegration.ws.consignment.ClientInfo;
import in.com.v2kart.aramexintegration.ws.consignment.Notification;
import in.com.v2kart.aramexintegration.ws.consignment.ObjectFactory;
import in.com.v2kart.aramexintegration.ws.consignment.ShipmentTrackingRequest;
import in.com.v2kart.aramexintegration.ws.consignment.ShipmentTrackingResponse;
import in.com.v2kart.aramexintegration.ws.consignment.TrackingResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.WebServiceFaultException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.util.Config;

/**
 * @author shubhammaheshwari
 * 
 */
public class AramexTrackerServiceImpl implements AramexTrackerService {

    /** Logger instance */
    private static final Logger LOG = Logger.getLogger(AramexTrackerServiceImpl.class);

    private final static String ARAMEX_COUNTRY_CODE = Config.getParameter("consignment.tracking.aramex.countrycode");
    private final static String ARAMEX_ACCOUNT_ENTITY = Config.getParameter("consignment.tracking.aramex.accountentity");
    private final static String ARAMEX_ACCOUNT_NUMBER = Config.getParameter("consignment.tracking.aramex.accountnumber");
    private final static String ARAMEX_ACCOUNT_PIN = Config.getParameter("consignment.tracking.aramex.accountpin");
    private final static String ARAMEX_USERNAME = Config.getParameter("consignment.tracking.aramex.username");
    private final static String ARAMEX_PASSWORD = Config.getParameter("consignment.tracking.aramex.password");
    private final static String ARAMEX_VERSION = Config.getParameter("consignment.tracking.aramex.version");
    private final static String SOAP_ACTION_CALLBACK_URL = Config.getParameter("aramex.soap.action.callback.url");

    /** WebServiceTemplate */
    private WebServiceTemplate wsClient;
    /** Default Customer Search URI */
    private String defaultConsignmentTrackURI;

    private final ObjectFactory objectFactory = new ObjectFactory();

    private AramexTrackerDao aramexTrackerDao;

    /*
     * (non-Javadoc)
     * 
     * @see
     * in.com.v2kart.aramexintegration.service.AramexTrackerService#getOrderConsignmentTrackerResponse(in.com.v2kart.aramexintegration.ws
     * .consignment.ShipmentTrackingRequest)
     */
   @Override
    public Map<String ,List<TrackingResult>> getOrderConsignmentTrackerResponse(final ShipmentTrackingRequest shipmentTrackingRequest)
            throws V2AramexIntegrationException {

    	Map<String ,List<TrackingResult>> maptrackingResults = new HashMap<String ,List<TrackingResult>>(0);
        List<TrackingResult> trackingResults = new ArrayList<>(0);
        ShipmentTrackingResponse shipmentTrackingResponse = null;

        try {
            LOG.info("Sending Consignment Search Request");
            SoapActionCallback soapActionCallback = new SoapActionCallback(SOAP_ACTION_CALLBACK_URL);
            shipmentTrackingResponse = (ShipmentTrackingResponse) wsClient.marshalSendAndReceive(defaultConsignmentTrackURI,
                    shipmentTrackingRequest, soapActionCallback);
        } catch (final WebServiceFaultException faultException) {
            throw new V2AramexIntegrationException("Error response received ", faultException);
        } catch (final WebServiceException wsException) {
            throw new V2AramexIntegrationException("Error getting response from web service ", wsException);
        }
        if (!shipmentTrackingResponse.isHasErrors()) {
            final JAXBElement<ArrayOfKeyValueOfstringArrayOfTrackingResultmFAkxlpY> jaxbTrackingResults = shipmentTrackingResponse
                    .getTrackingResults();
            List<ArrayOfKeyValueOfstringArrayOfTrackingResultmFAkxlpY.KeyValueOfstringArrayOfTrackingResultmFAkxlpY> trackingResultsList = jaxbTrackingResults
                    .getValue().getKeyValueOfstringArrayOfTrackingResultmFAkxlpY();
            LOG.debug("TOTAL trackingResultsList is ["+trackingResultsList.size()+"]");
            if (trackingResultsList.size() > 0) {
                final Iterator<ArrayOfKeyValueOfstringArrayOfTrackingResultmFAkxlpY.KeyValueOfstringArrayOfTrackingResultmFAkxlpY> trackingResultIterator = trackingResultsList
                        .iterator();
                while (trackingResultIterator.hasNext()) {
                    ArrayOfKeyValueOfstringArrayOfTrackingResultmFAkxlpY.KeyValueOfstringArrayOfTrackingResultmFAkxlpY keyValueOfstringArrayOfTrackingResultmFAkxlpY = (ArrayOfKeyValueOfstringArrayOfTrackingResultmFAkxlpY.KeyValueOfstringArrayOfTrackingResultmFAkxlpY) trackingResultIterator
                            .next();
                    LOG.debug("ALL AWB NUMBER is :["+keyValueOfstringArrayOfTrackingResultmFAkxlpY.getKey()+"]");
                    ArrayOfTrackingResult arrayOfTrackingResult = keyValueOfstringArrayOfTrackingResultmFAkxlpY.getValue();
                    trackingResults = arrayOfTrackingResult.getTrackingResult();
                maptrackingResults.put(keyValueOfstringArrayOfTrackingResultmFAkxlpY.getKey(), trackingResults);
                }
            }
        } else {
            LOG.error("Error occured while getting response from Aramex. Error is : ");
            JAXBElement<ArrayOfNotification> jaxbNotifications = shipmentTrackingResponse.getNotifications();
            List<Notification> notifications = jaxbNotifications.getValue().getNotification();
            int i = 1;
            for (Notification notification : notifications) {
                LOG.error("Notification " + i + " code :" + notification.getCode());
                LOG.error("Notification " + i + " message" + notification.getMessage());
                i++;
            }
        }
        if (LOG.isDebugEnabled()) {
        	LOG.debug("TOTAL WAYBILL RESOPNSE MAP:["+maptrackingResults.size()+"]");
        	LOG.debug("TOTAL WAYBILL RESOPNSE LIST:["+trackingResults.size()+"]");
        	for(TrackingResult track:trackingResults){
			LOG.debug("AraMex LSP Response of awb[" + track.getWaybillNumber() + "],updatedCode["
					+ track.getUpdateCode() + "]");
        	}
        	}
      //  return trackingResults;
       return maptrackingResults;

    }

    /**
     * @return ArrayOfstring
     */
    private ArrayOfstring populateShippments(final List<ConsignmentModel> consignments) {
        final ArrayOfstring shipments = objectFactory.createArrayOfstring();
        final List<String> wayBills = shipments.getString();
        for (final ConsignmentModel consignment : consignments) {
        	wayBills.add(consignment.getTrackingID());
        }
        LOG.debug("ARAMEX TrackingID's:"+wayBills);
        return shipments;
    }

    /**
     * @return ClientInfo
     */
    private ClientInfo populateClientInfo() {
        final ClientInfo clientInfo = objectFactory.createClientInfo();
        clientInfo.setAccountCountryCode(ARAMEX_COUNTRY_CODE);
        clientInfo.setAccountEntity(ARAMEX_ACCOUNT_ENTITY);
        clientInfo.setAccountNumber(ARAMEX_ACCOUNT_NUMBER);
        clientInfo.setAccountPin(ARAMEX_ACCOUNT_PIN);
        clientInfo.setPassword(ARAMEX_PASSWORD);
        clientInfo.setUserName(ARAMEX_USERNAME);
        clientInfo.setVersion(ARAMEX_VERSION);
        return clientInfo;
    }

    public void setWsClient(final WebServiceTemplate wsClient) {
        this.wsClient = wsClient;
    }

    public void setDefaultConsignmentTrackURI(final String defaultConsignmentTrackURI) {
        this.defaultConsignmentTrackURI = defaultConsignmentTrackURI;
    }

    @Override
    public void updateOrderConsignmentStatus(List<ConsignmentModel> consignments) {
    	for(ConsignmentModel consignment :consignments){
        final ShipmentTrackingRequest shipmentTrackingRequest = getOrderConsignmentTrackerRequest(Arrays.asList(consignment));
        try {
            final Map<String ,List<TrackingResult>> trackingResults = getOrderConsignmentTrackerResponse(shipmentTrackingRequest);
        
            for(Map.Entry<String, List<TrackingResult>> entry :trackingResults.entrySet()){
            	String key = entry.getKey();
            	LOG.debug("Mapping Key :"+key);
            	List<TrackingResult> values=entry.getValue();
                 aramexTrackerDao.updateOrderConsignmentStatus(values);
            }
        } catch (V2AramexIntegrationException e) {
            LOG.info("Error occured while updating consignment status from ARAMEX" + e);
        }
    	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.aramexintegration.service.AramexTrackerService#getOrderConsignmentTrackerRequest(java.util.List)
     */
    @Override
    public ShipmentTrackingRequest getOrderConsignmentTrackerRequest(final List<ConsignmentModel> consignments) {
        // create shippment tracking request
        final ShipmentTrackingRequest shipmentTrackingRequest = objectFactory.createShipmentTrackingRequest();
        // create client Info
        final ClientInfo clientInfo = populateClientInfo();
        // create Shippments
        final ArrayOfstring shippments = populateShippments(consignments);

        // set client info in the request
        final JAXBElement<ClientInfo> wrappedClientInfo = objectFactory.createShipmentTrackingRequestClientInfo(clientInfo);
        shipmentTrackingRequest.setClientInfo(wrappedClientInfo);

        // set shippments in the request
        final JAXBElement<ArrayOfstring> wrappedShipments = objectFactory.createShipmentTrackingRequestShipments(shippments);
        shipmentTrackingRequest.setShipments(wrappedShipments);

        // TODO last update only
        return shipmentTrackingRequest;
    }

    @Required
    public void setAramexTrackerDao(AramexTrackerDao aramexTrackerDao) {
        this.aramexTrackerDao = aramexTrackerDao;
    }
}
