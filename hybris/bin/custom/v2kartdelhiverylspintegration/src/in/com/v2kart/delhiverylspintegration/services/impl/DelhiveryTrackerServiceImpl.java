/**
 * 
 */
package in.com.v2kart.delhiverylspintegration.services.impl;

import in.com.v2kart.delhiverylspintegration.dao.DelhiveryTrackerDao;
import in.com.v2kart.delhiverylspintegration.data.request.DelhiveryOrderTrackerRequest;
import in.com.v2kart.delhiverylspintegration.data.response.DelhiveryOrderTrackerResponse;
import in.com.v2kart.delhiverylspintegration.enums.Verbose;
import in.com.v2kart.delhiverylspintegration.services.DelhiveryTrackerService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.util.Config;

/**
 * @author vikrant2480
 * 
 */
public class DelhiveryTrackerServiceImpl implements DelhiveryTrackerService {

    private static final Logger LOG = Logger.getLogger(DelhiveryTrackerServiceImpl.class);

    private static final String ORDER_TRACKER_URL = Config.getParameter("delhivery.order.tracker.url");

    private static final String TOKEN = Config.getParameter("delhivery.token");

    private DelhiveryTrackerDao delhiveryTrackerDao;

    private RestTemplate restTemplateClient;

    /**
     * @param delhiveryTrackerDao
     *        the delhiveryTrackerDao to set
     */
    @Required
    public void setDelhiveryTrackerDao(final DelhiveryTrackerDao delhiveryTrackerDao) {
        this.delhiveryTrackerDao = delhiveryTrackerDao;
    }

    /**
     * @param restTemplateClient
     *        the restTemplateClient to set
     */
    @Required
    public void setRestTemplateClient(final RestTemplate restTemplateClient) {
        this.restTemplateClient = restTemplateClient;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.delhiverylspintegration.services.DelhiveryTrackerService#getOrderConsignmentTrackerResponse(in.com.v2kart.
     * delhiverylspintegration.data.request.DelhiveryOrderTrackerRequest)
     */
    @Override
    public DelhiveryOrderTrackerResponse getOrderConsignmentTrackerResponse(final DelhiveryOrderTrackerRequest delhiveryOrderTrackerRequest) {
        final Map<String, String> requestParameters = new HashMap<>();
        requestParameters.put("token", delhiveryOrderTrackerRequest.getToken());
        requestParameters.put("waybill", StringUtils.join(delhiveryOrderTrackerRequest.getWayBills(), ","));
        DelhiveryOrderTrackerResponse delhiveryOrderTrackerResponse = null;
        try {
            delhiveryOrderTrackerResponse = restTemplateClient.getForObject(ORDER_TRACKER_URL,
                    DelhiveryOrderTrackerResponse.class, requestParameters);
        } catch (RestClientException e) {
            LOG.error("Error occured while getting response from Delhivery" + e);
        }
        return delhiveryOrderTrackerResponse;

    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.delhiverylspintegration.services.DelhiveryTrackerService#getOrderConsignmentTrackerRequest(java.util.List)
     */
    @Override
    public DelhiveryOrderTrackerRequest getOrderConsignmentTrackerRequest(final List<ConsignmentModel> consignments) {
        final List<String> wayBills = new ArrayList<>();
        for (final ConsignmentModel consignment : consignments) {
            wayBills.add(consignment.getTrackingID());
        }
        LOG.debug("DELHIVERY LSP TRACKING ID's :"+wayBills);
        final DelhiveryOrderTrackerRequest delhiveryOrderTrackerRequest = new DelhiveryOrderTrackerRequest(TOKEN, wayBills,
                Verbose.META_INFO);
        return delhiveryOrderTrackerRequest;
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.core.services.AbstractLSPTrackerService#updateOrderConsignmentStatus(java.util.List)
     */
    @Override
    public void updateOrderConsignmentStatus(final List<ConsignmentModel> consignments) {
        final DelhiveryOrderTrackerRequest delhiveryOrderTrackerRequest = getOrderConsignmentTrackerRequest(consignments);
        final DelhiveryOrderTrackerResponse delhiveryOrderTrackerResponse = getOrderConsignmentTrackerResponse(delhiveryOrderTrackerRequest);
        if(delhiveryOrderTrackerResponse!=null){
        final String delhiveryResponseError = delhiveryOrderTrackerResponse.getError();
        if (null != delhiveryResponseError) {
            LOG.error("Error occured while getting response from DELHIVERY " + delhiveryResponseError);
        } else {
            delhiveryTrackerDao.updateOrderConsignmentStatus(delhiveryOrderTrackerResponse);
        }
    }
        }
}
