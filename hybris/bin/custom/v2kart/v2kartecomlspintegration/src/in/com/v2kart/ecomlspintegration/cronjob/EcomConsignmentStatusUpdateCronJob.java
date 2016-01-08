/**
 * 
 */
package in.com.v2kart.ecomlspintegration.cronjob;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.search.SearchResult;

import in.com.v2kart.ecomlspintegration.services.EcomTrackerService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author arunkumar
 * 
 */
public class EcomConsignmentStatusUpdateCronJob extends
		AbstractJobPerformable<CronJobModel> {

	/** Logger Instance for this class */
	private final Logger LOG = Logger
			.getLogger(EcomConsignmentStatusUpdateCronJob.class);

	private EcomTrackerService ecomTrackerService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform
	 * (de.hybris.platform.cronjob.model.CronJobModel)
	 */
	@Override
    public PerformResult perform(final CronJobModel arg0) {
        final PerformResult result = new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);

        final String query = "select {c.pk} from {Consignment as c join V2LogisticServiceProvider as lspsp on {c.lsp}={lspsp.pk}} where {trackingID} IS NOT NULL and {lspsp.code}='ECOM EXPRESS'";
        final Map<String, String> map = new HashMap<>();
        final SearchResult<ConsignmentModel> consignments = flexibleSearchService.search(query, map);
        final List<ConsignmentModel> consignmentsList = getConsignmentForLSPStatus(consignments.getResult());
        if (CollectionUtils.isNotEmpty(consignmentsList)) {
            ecomTrackerService.updateOrderConsignmentStatus(consignmentsList);
        }
        return result;
    }

	/**
	 * 
	 * @param consignmentsList
	 * @return
	 */
	private List<ConsignmentModel> getConsignmentForLSPStatus(
			List<ConsignmentModel> consignmentsList) {
		List<ConsignmentModel> lspConsignmentsList = new ArrayList<ConsignmentModel>();
		for (ConsignmentModel consignments : consignmentsList) {
			final AbstractOrderModel orderModel = consignments.getOrder();
			 if(!(orderModel.getStatus().equals(OrderStatus.COMPLETED))){
				lspConsignmentsList.add(consignments);
			}
		}
		return lspConsignmentsList;
	}

	public EcomTrackerService getEcomTrackerService() {
		return ecomTrackerService;
	}

	@Required
	public void setEcomTrackerService(
			final EcomTrackerService ecomTrackerService) {
		this.ecomTrackerService = ecomTrackerService;
	}

}
