/**
 *
 */
package in.com.v2kart.dotzotlspintegration.dao.impl;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import in.com.v2kart.core.email.impl.V2DeliveryConfirmationNotificationSender;
import in.com.v2kart.dotzotlspintegration.dao.DotzotTrackerDao;
import in.com.v2kart.dotzotlspintegration.ws.consignment.Consignment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

/**
 * @author arunkumar
 * 
 */
public class DotzotTrackerDaoImpl implements DotzotTrackerDao {

    private FlexibleSearchService flexibleSearchService;

    private ModelService modelService;

    private V2DeliveryConfirmationNotificationSender v2DeliveryConfirmationNotificationSender;

    private final static String DELIVERED_CODE = "Delivered";

    private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dotzotlspintegration.dao.DotzotTrackerDao#updateOrderConsignmentStatus(java.util.List)
     */
    @Override
    public void updateOrderConsignmentStatus(final List<Consignment> trackingResults) {

        for (final Consignment consignmentResult : trackingResults) {

            final String awb = consignmentResult.getDOCKNO();
            final String updatedCode = consignmentResult.getCURRENTSTATUS();
            final String updatedLocation = consignmentResult.getCURRLOC();
            final String deliveryDate = consignmentResult.getDELIVEREDON();

            final String query = "select {c.pk} from {Consignment as c join V2LogisticServiceProvider as lspsp on {c.lsp}={lspsp.pk}} where {trackingID}=?trackingID and {lspsp.code}='DOTZOT'";
            final Map<String, String> map = new HashMap<String, String>();
            map.put("trackingID", awb);
            final FlexibleSearchQuery flexiblesearchquery = new FlexibleSearchQuery(query, map);
            final ConsignmentModel consignment = flexibleSearchService.searchUnique(flexiblesearchquery);
            consignment.setStatus(ConsignmentStatus.valueOf(updatedCode));
            consignment.setCurrentLocation(updatedLocation);
          //  consignment.setCompletionDate(convertStringToDate(deliveryDate));
            modelService.save(consignment);

            // trigger delivery confirmation email and sms
            if (updatedCode.equals(DELIVERED_CODE)) {
                consignment.setCompletionDate(new Date());
                modelService.save(consignment);

                v2DeliveryConfirmationNotificationSender.triggerDeliveryConfirmationNotification(consignment);
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
     * 
     * @param flexibleSearchService
     */
    @Required
    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    /**
     * 
     * @param modelService
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
            final V2DeliveryConfirmationNotificationSender v2DeliveryConfirmationNotificationSender) {
        this.v2DeliveryConfirmationNotificationSender = v2DeliveryConfirmationNotificationSender;
    }

}
