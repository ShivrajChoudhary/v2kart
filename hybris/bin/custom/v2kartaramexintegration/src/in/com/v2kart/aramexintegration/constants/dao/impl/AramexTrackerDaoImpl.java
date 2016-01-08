package in.com.v2kart.aramexintegration.constants.dao.impl;

import in.com.v2kart.aramexintegration.constants.dao.AramexTrackerDao;
import in.com.v2kart.aramexintegration.ws.consignment.TrackingResult;
import in.com.v2kart.core.email.impl.V2DeliveryConfirmationNotificationSender;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
 * 
 * @author vikrant2480
 * 
 */
public class AramexTrackerDaoImpl implements AramexTrackerDao {
	private final Logger LOG = Logger.getLogger(AramexTrackerDaoImpl.class);

    private FlexibleSearchService flexibleSearchService;

    private ModelService modelService;
    
    private V2DeliveryConfirmationNotificationSender v2DeliveryConfirmationNotificationSender;
    
//    private final static String DELIVERED_CODE = "SHDL";
    private final static String DELIVERED_CODE = "SH005";

    private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    
    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.aramexintegration.constants.dao.AramexTrackerDao#updateOrderConsignmentStatus(java.util.List)
     */
    @Override
    public void updateOrderConsignmentStatus(List<TrackingResult> trackingResults) {
       LOG.debug("TrackingResult size :"+trackingResults.size());
       ConsignmentModel consignmentModel =null;
       String deliveredStatus=null;
       for (TrackingResult trackingResult : trackingResults) {
            final String awb = trackingResult.getWaybillNumber();
            final String updatedCode = trackingResult.getUpdateCode();
            final String updatedLocation = trackingResult.getUpdateLocation();
            if (LOG.isDebugEnabled()) {
				LOG.debug("AraMex LSP Response of awb[" + awb + "],updatedCode["
						+ updatedCode + "],updatedLocation[" + updatedLocation
						+ "]");
			}
   
            final String query = "select {c.pk} from {Consignment as c join V2LogisticServiceProvider as lspsp on {c.lsp}={lspsp.pk}} where {trackingID}=?trackingID and {lspsp.code}='ARAMEX'";
            final Map<String, String> map = new HashMap<>();
            map.put("trackingID", awb);
            final FlexibleSearchQuery flexiblesearchquery = new FlexibleSearchQuery(query, map);
            final ConsignmentModel consignment = flexibleSearchService.searchUnique(flexiblesearchquery);
            consignment.setStatus(ConsignmentStatus.valueOf(updatedCode));
            consignment.setCurrentLocation(updatedLocation);
         //   consignment.setCompletionDate(new Date());
            modelService.save(consignment);
            // trigger delivery confirmation email and sms
            if(updatedCode.equals(DELIVERED_CODE)){
            	consignmentModel=consignment;
            	deliveredStatus=updatedCode;
            	v2DeliveryConfirmationNotificationSender.triggerDeliveryConfirmationNotification(consignment);
            }
        }
       if(deliveredStatus!=null && deliveredStatus.equals(DELIVERED_CODE)){
    	   if(consignmentModel!=null){
    	       consignmentModel.setStatus(ConsignmentStatus.DELIVERED);
    	       consignmentModel.setCompletionDate(new Date());
    	       modelService.save(consignmentModel);
    	   }
       }
    }
    

    /**
     * 
     * @param flexibleSearchService
     */
    @Required
    public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    /**
     * 
     * @param modelService
     */
    @Required
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    
    /**
     * @param v2DeliveryConfirmationNotificationSender
     */
    @Required
    public void setV2DeliveryConfirmationNotificationSender(V2DeliveryConfirmationNotificationSender v2DeliveryConfirmationNotificationSender) {
        this.v2DeliveryConfirmationNotificationSender = v2DeliveryConfirmationNotificationSender;
    }

}
