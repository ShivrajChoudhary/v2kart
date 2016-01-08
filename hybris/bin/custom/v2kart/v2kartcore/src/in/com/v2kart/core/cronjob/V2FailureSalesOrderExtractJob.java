package in.com.v2kart.core.cronjob;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import in.com.v2kart.sapinboundintegration.exceptions.V2SapConnectionException;
import in.com.v2kart.sapinboundintegration.order.daos.V2OrderDao;
import in.com.v2kart.sapinboundintegration.services.V2SapInboundSaleOrderIntegrationService;
import in.com.v2kart.sapinboundintegration.ws.order.SOCreateRes;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * <V2FailureSalesOrderExtractJob> fetches SAP FAILURE orders which are not much older than 15 days and then send/post again then to SAP
 * 
 * @author Nagarro_Devraj
 * @Version 1.0
 * 
 */
public class V2FailureSalesOrderExtractJob extends AbstractJobPerformable<CronJobModel> {

    /** Logger Instance for this class */
    private final Logger LOG = Logger.getLogger(V2FailureSalesOrderExtractJob.class);

    /** V2OrderDao bean injection */
    private V2OrderDao v2OrderDao;

    /** V2SapInboundSaleOrderIntegrationService bean injection */
    private V2SapInboundSaleOrderIntegrationService v2SapInboundSaleOrderIntegrationService;

    /** {@inheritDoc} */
    @Override
    public PerformResult perform(final CronJobModel cronJobModel) {
        LOG.info("FAILURE Sales Order Extract Job starts...");
        boolean success = true;
        // fetch Orders that are FAILURE and less than 15 days old
        final List<OrderModel> failureOrders = v2OrderDao.findSalesFailureOrders();
        if (failureOrders != null && !failureOrders.isEmpty()) {
            for (final OrderModel orderModel : failureOrders) {
                if (this.isCustomerCreatedInERP((CustomerModel) orderModel.getUser())) {
                    try {
                        final SOCreateRes soCreateResponse = v2SapInboundSaleOrderIntegrationService.updateErpSales(orderModel);
                        for (final SOCreateRes.OrderCreationRes orderCreationRes : soCreateResponse.getOrderCreationRes()) {
                        LOG.debug("RESPONSE FOR RETRIGGER SALES ORDER ["+orderModel.getCode()+"] IS :"+soCreateResponse != null?orderCreationRes.getRespCode():"null");
                        	if ("F".equalsIgnoreCase(orderCreationRes.getRespCode()) || soCreateResponse == null) {
                                success = false;
                            }
                        }
                    } catch (final V2SapConnectionException sape) {
                        success = false;
                        LOG.error("Error in sending Failure Sales orders to SAP", sape);
                    }
                }
            }
        }

        if (success) {
            return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
        }
        return new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
    }

    /**
     * Returns <Code>TRUE</Code> if customer sync with ERP System <Code>FALSE</Code>otherwise.
     * 
     * @param customer
     *        to check customer is sync with ERP or not.
     * @return TRUE if customer sync with ERP System FALSE otherwise
     */
    private boolean isCustomerCreatedInERP(final CustomerModel customer) {
        if (customer != null) {
            if (customer.getSapCustomerId() != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param v2OrderDao
     *        the v2OrderDao to set
     */
    @Required
    public void setV2OrderDao(V2OrderDao v2OrderDao) {
        this.v2OrderDao = v2OrderDao;
    }

    /**
     * @param v2SapInboundSaleOrderIntegrationService
     *        the v2SapInboundSaleOrderIntegrationService to set
     */
    @Required
    public void setV2SapInboundSaleOrderIntegrationService(V2SapInboundSaleOrderIntegrationService v2SapInboundSaleOrderIntegrationService) {
        this.v2SapInboundSaleOrderIntegrationService = v2SapInboundSaleOrderIntegrationService;
    }
}
