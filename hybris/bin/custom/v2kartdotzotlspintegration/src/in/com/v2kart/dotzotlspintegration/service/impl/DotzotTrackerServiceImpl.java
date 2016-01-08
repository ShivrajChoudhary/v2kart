/**
 * 
 */
package in.com.v2kart.dotzotlspintegration.service.impl;

import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.util.Config;

import in.com.v2kart.dotzotlspintegration.dao.DotzotTrackerDao;
import in.com.v2kart.dotzotlspintegration.exception.V2DotzotIntegrationException;
import in.com.v2kart.dotzotlspintegration.service.DotzotTrackerService;
import in.com.v2kart.dotzotlspintegration.ws.consignment.Consignment;
import in.com.v2kart.dotzotlspintegration.ws.consignment.ObjectFactory;
import in.com.v2kart.dotzotlspintegration.ws.consignment.TrackConsignmentHeaderNew;
import in.com.v2kart.dotzotlspintegration.ws.consignment.TrackConsignmentHeaderNewResponse;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;

/**
 * @author arunkumar
 * 
 */
public class DotzotTrackerServiceImpl implements DotzotTrackerService {

    private static final Logger LOG = Logger.getLogger(DotzotTrackerServiceImpl.class);

    private static final String DOTZOT_USERNAME = Config.getParameter("consignment.tracking.dotzot.username");
    private static final String DOTZOT_PASSWORD = Config.getParameter("consignment.tracking.dotzot.password");
    private static final String DOTZOT_CLIENTID = Config.getParameter("consignment.tracking.dotzot.clientId");

    /** WebServiceTemplate */
    private WebServiceTemplate wsClient;
    /** Default Customer Search URI */
    private String defaultConsignmentTrackURI;

    private String soapActionCallbackUrl;

    private DotzotTrackerDao dotzotTrackerDao;

    private final ObjectFactory objectFactory = new ObjectFactory();

    public void setWsClient(final WebServiceTemplate wsClient) {
        this.wsClient = wsClient;
    }

    public void setSoapActionCallbackUrl(final String soapActionCallbackUrl) {
        this.soapActionCallbackUrl = soapActionCallbackUrl;
    }

    public void setDefaultConsignmentTrackURI(final String defaultConsignmentTrackURI) {
        this.defaultConsignmentTrackURI = defaultConsignmentTrackURI;
    }

    @Required
    public void setDotzotTrackerDao(final DotzotTrackerDao dotzotTrackerDao) {
        this.dotzotTrackerDao = dotzotTrackerDao;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.core.services.LSPTrackerService#updateOrderConsignmentStatus(java.util.List)
     */
    @Override
    public void updateOrderConsignmentStatus(final List<ConsignmentModel> consignments) {
        final TrackConsignmentHeaderNew consignmentTrackRequest = getOrderConsignmentTrackerRequest(consignments);
        try {
            final List<Consignment> consigmentsResponse = getOrderConsignmentTrackerResponse(consignmentTrackRequest);
            dotzotTrackerDao.updateOrderConsignmentStatus(consigmentsResponse);
        } catch (final V2DotzotIntegrationException e) {
            LOG.info("Error occured while updating consignment status from DOTZOT" + e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dotzotlspintegration.service.DotzotTrackerService#getOrderConsignmentTrackerRequest(java.util.List)
     */
    @Override
    public TrackConsignmentHeaderNew getOrderConsignmentTrackerRequest(final List<ConsignmentModel> consignments) {
        final TrackConsignmentHeaderNew consignmentTrackRequest = objectFactory.createTrackConsignmentHeaderNew();
        String trackingId = "";
        consignmentTrackRequest.setClientId(DOTZOT_CLIENTID);
        consignmentTrackRequest.setUserName(DOTZOT_USERNAME);
        consignmentTrackRequest.setPassword(DOTZOT_PASSWORD);

        for (final ConsignmentModel consignment : consignments) {
            trackingId = consignment.getTrackingID() + "," + trackingId;
        }
        consignmentTrackRequest.setDOCNO(trackingId);
        return consignmentTrackRequest;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * in.com.v2kart.dotzotlspintegration.service.DotzotTrackerService#getOrderConsignmentTrackerResponse(in.com.v2kart.dotzotlspintegration
     * .ws.consignment.TrackConsignmentHeaderNew)
     */
    @Override
    public List<Consignment> getOrderConsignmentTrackerResponse(final TrackConsignmentHeaderNew consignmentRequest)
            throws V2DotzotIntegrationException {
        final SoapActionCallback soapActionCallback = new SoapActionCallback(soapActionCallbackUrl);
        List<Consignment> consignment = null;
        TrackConsignmentHeaderNewResponse consignmentTrackResponse = null;
        try {
            consignmentTrackResponse = (TrackConsignmentHeaderNewResponse) wsClient.marshalSendAndReceive(defaultConsignmentTrackURI,
                    consignmentRequest, soapActionCallback);
        } catch (final WebServiceException faultException) {
            throw new V2DotzotIntegrationException("Error response received ", faultException);
        }
        if (null != consignmentTrackResponse.getTrackConsignmentHeaderNewResult()) {
            consignment = consignmentTrackResponse.getTrackConsignmentHeaderNewResult().getConsignment();
        }
        return consignment;
    }
}
