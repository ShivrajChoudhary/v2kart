/**
 * 
 */
package in.com.v2kart.delhiverylspintegration.dao.impl;

import in.com.v2kart.core.email.impl.V2DeliveryConfirmationNotificationSender;
import in.com.v2kart.delhiverylspintegration.dao.DelhiveryTrackerDao;
import in.com.v2kart.delhiverylspintegration.data.response.DelhiveryOrderTrackerResponse;
import in.com.v2kart.delhiverylspintegration.data.response.Shipment;
import in.com.v2kart.delhiverylspintegration.data.response.ShipmentData;
import in.com.v2kart.delhiverylspintegration.data.response.StatusDetail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

/**
 * @author vikrant2480
 * 
 */
public class DelhiveryTrackerDaoImpl implements DelhiveryTrackerDao {
	/** Logger Instance for this class */
	private final Logger LOG = Logger
			.getLogger(DelhiveryTrackerDaoImpl.class);
    private FlexibleSearchService flexibleSearchService;

    private ModelService modelService;

    private V2DeliveryConfirmationNotificationSender v2DeliveryConfirmationNotificationSender;

    private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * in.com.v2kart.delhiverylspintegration.dao.DelhiveryTrackerDao#updateOrderConsignmentStatus(in.com.v2kart.delhiverylspintegration.
     * data.response.DelhiveryOrderTrackerResponse)
     */
    @Override
    public void updateOrderConsignmentStatus(final DelhiveryOrderTrackerResponse delhiveryOrderTrackerResponse) {
        ShipmentData[] shipmentDataResponse = delhiveryOrderTrackerResponse.getShipmentData();
        if (null != shipmentDataResponse) {
            final List<ShipmentData> shipmentDatas = new ArrayList<>(Arrays.asList(shipmentDataResponse));
            for (final ShipmentData shipmentData : shipmentDatas) {
                final Shipment shipment = shipmentData.getShipment();
                if (null != shipment) {
                    final String awb = shipment.getAwb();
                    String deliveryDate  = null;
                    try{
                        deliveryDate  = shipment.getDestinationRecievedDate().toString();
                    }catch(NullPointerException e){
                        //TODO
                    }
                    
                    final StatusDetail statusDetail = shipment.getStatus();
                    if (null != statusDetail) {
                        final String statusLocation = statusDetail.getStatusLocation();
                        final String status = statusDetail.getStatus();

                        final String query = "select {c.pk} from {Consignment as c join V2LogisticServiceProvider as lspsp on {c.lsp}={lspsp.pk}} where {trackingID}=?trackingID and {lspsp.code}='DELHIVERY'";
                        final Map<String, String> map = new HashMap<>();
                        map.put("trackingID", awb);
                        final FlexibleSearchQuery flexiblesearchquery = new FlexibleSearchQuery(query, map);
                        final ConsignmentModel consignment = flexibleSearchService.searchUnique(flexiblesearchquery);
 
                   //     consignment.setCompletionDate(convertStringToDate(deliveryDate));
                        consignment.setStatus(ConsignmentStatus.valueOf(status));
                        consignment.setCurrentLocation(statusLocation);
                        modelService.save(consignment);
                    
                        if (LOG.isDebugEnabled()) {
                			LOG.debug("Delhivary LSP status of tracking ID ["+awb+"] is["+status+"]");
                		}
                        // trigger delivery confirmation email and sms if status is delivered
                        if (status.equalsIgnoreCase(ConsignmentStatus.DELIVERED.getCode())) {
                        	consignment.setCompletionDate(new Date());
                        	  modelService.save(consignment);
                        	v2DeliveryConfirmationNotificationSender.triggerDeliveryConfirmationNotification(consignment);
                        }
                    }
                }
            }
        }
    }

    /**
     * @param deliveryDate
     * @return delivery date of consignment
     */
    Date convertStringToDate(final String deliveryDate) {
        try {
            final Date date = formatter.parse(deliveryDate);
            return date;
        } catch (final ParseException | NullPointerException e) {
            final Date currentDate = new Date();
            return currentDate;
        }
    }
    
    /**
     * @param flexibleSearchService
     *        the flexibleSearchService to set
     */
    @Required
    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    /**
     * @param modelService
     *        the modelService to set
     */
    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    /**
     * @param v2DeliveryConfirmationNotificationSender
     */
    @Required
    public void setV2DeliveryConfirmationNotificationSender(
            V2DeliveryConfirmationNotificationSender v2DeliveryConfirmationNotificationSender) {
        this.v2DeliveryConfirmationNotificationSender = v2DeliveryConfirmationNotificationSender;
    }

}
