/**
 * 
 */
package in.com.v2kart.ecomlspintegration.services.impl;

import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.util.Config;

import in.com.v2kart.ecomlspintegration.dao.EcomTrackerDao;
import in.com.v2kart.ecomlspintegration.data.request.EcomOrderTrackerRequest;
import in.com.v2kart.ecomlspintegration.data.response.EcomexpressObjectsType;
import in.com.v2kart.ecomlspintegration.services.EcomTrackerService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * @author arunkumar
 * 
 */
public class EcomTrackerServiceImpl implements EcomTrackerService {

    private static final Logger LOG = Logger.getLogger(EcomTrackerServiceImpl.class);

    private static final String USERNAME = Config.getParameter("ecom.order.tracker.username");

    private static final String PASSWORD = Config.getParameter("ecom.order.tracker.password");

    private static final String TRACKER_URL = Config.getParameter("ecom.order.tracker.url");

    private RestTemplate restTemplateClient;

    private EcomTrackerDao ecomTrackerDao;

    public EcomTrackerDao getEcomTrackerDao() {
        return ecomTrackerDao;
    }

    @Required
    public void setEcomTrackerDao(final EcomTrackerDao ecomTrackerDao) {
        this.ecomTrackerDao = ecomTrackerDao;
    }

    public RestTemplate getRestTemplateClient() {
        return restTemplateClient;
    }

    @Required
    public void setRestTemplateClient(final RestTemplate restTemplateClient) {
        this.restTemplateClient = restTemplateClient;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.core.services.LSPTrackerService#updateOrderConsignmentStatus(java.util.List)
     */
    @Override
    public void updateOrderConsignmentStatus(final List<ConsignmentModel> consignments) {
        final EcomOrderTrackerRequest ecomOrderTrackerRequest = getOrderConsignmentTrackerRequest(consignments);
        final EcomexpressObjectsType ecomOrderTrackerResponse = getOrderConsignmentTrackerResponse(ecomOrderTrackerRequest);
        ecomTrackerDao.updateOrderConsignmentStatus(ecomOrderTrackerResponse);
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.ecomlspintegration.services.EcomTrackerService#getOrderConsignmentTrackerRequest(java.util.List)
     */
    @Override
    public EcomOrderTrackerRequest getOrderConsignmentTrackerRequest(final List<ConsignmentModel> consignments) {
        final List<String> wayBills = new ArrayList<>();
        for (final ConsignmentModel consignment : consignments) {
            wayBills.add(consignment.getTrackingID());
        }
        final EcomOrderTrackerRequest ecomOrderTrackerRequest = new EcomOrderTrackerRequest(wayBills, USERNAME, PASSWORD);

        return ecomOrderTrackerRequest;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * in.com.v2kart.ecomlspintegration.services.EcomTrackerService#getOrderConsignmentTrackerResponse(in.com.v2kart.ecomlspintegration.
     * data.request.EcomOrderTrackerRequest)
     */
    @Override
    public EcomexpressObjectsType getOrderConsignmentTrackerResponse(final EcomOrderTrackerRequest ecomOrderTrackerRequest) {
        final Map<String, String> requestParameters = new HashMap<>();
        requestParameters.put("username", ecomOrderTrackerRequest.getUsername());
        requestParameters.put("password", ecomOrderTrackerRequest.getPassword());
        requestParameters.put("awb", StringUtils.join(ecomOrderTrackerRequest.getWayBills(), ","));
        EcomexpressObjectsType ecomOrderTrackerResponse = null;

        try {
            ecomOrderTrackerResponse = restTemplateClient.getForObject(TRACKER_URL, EcomexpressObjectsType.class, requestParameters);
        } catch (final RestClientException e) {
            LOG.error("Error occured while getting response from ECOM EXPRESS" + e);
        }

        return ecomOrderTrackerResponse;
    }
}
