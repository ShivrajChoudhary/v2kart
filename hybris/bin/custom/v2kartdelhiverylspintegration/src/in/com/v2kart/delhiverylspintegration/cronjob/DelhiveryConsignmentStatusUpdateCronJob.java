package in.com.v2kart.delhiverylspintegration.cronjob;

import in.com.v2kart.delhiverylspintegration.services.DelhiveryTrackerService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * Cronjob to update the consignment status by REST call to Delhivery.
 * 
 * @author vikrant2480
 * 
 */
public class DelhiveryConsignmentStatusUpdateCronJob extends AbstractJobPerformable<CronJobModel> {

    /** Logger Instance for this class */
    private final Logger LOG = Logger.getLogger(DelhiveryConsignmentStatusUpdateCronJob.class);

    private DelhiveryTrackerService delhiveryTrackerService;

    @Override
    public PerformResult perform(final CronJobModel arg0) {
        final PerformResult result = new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);

        final String query = "select {c.pk} from {Consignment as c join V2LogisticServiceProvider as lspsp on {c.lsp}={lspsp.pk}} where {trackingID} IS NOT NULL and {lspsp.code}='DELHIVERY'";
        final Map<String, String> map = new HashMap<>();
        final SearchResult<ConsignmentModel> consignments = flexibleSearchService.search(query, map);
        final List<ConsignmentModel> consignmentsList = getConsignmentForLSPStatus(consignments.getResult());
        
        if (CollectionUtils.isNotEmpty(consignmentsList)) {
            delhiveryTrackerService.updateOrderConsignmentStatus(consignmentsList);
        }
        return result;
    }
    
    /**
     * 
     * @param consignmentsList
     * @return
     */
    private List<ConsignmentModel> getConsignmentForLSPStatus(List<ConsignmentModel> consignmentsList){
    	 List<ConsignmentModel> lspConsignmentsList = new ArrayList<ConsignmentModel>();
    	 for(ConsignmentModel consignments : consignmentsList ){
    		 final AbstractOrderModel orderModel = consignments.getOrder();
    		 if(!(orderModel.getStatus().equals(OrderStatus.COMPLETED))){
    			 lspConsignmentsList.add(consignments);
    		 }
    	 }
    	 return lspConsignmentsList;
    }

    @Required
    public void setDelhiveryTrackerService(final DelhiveryTrackerService delhiveryTrackerService) {
        this.delhiveryTrackerService = delhiveryTrackerService;
    }

}
