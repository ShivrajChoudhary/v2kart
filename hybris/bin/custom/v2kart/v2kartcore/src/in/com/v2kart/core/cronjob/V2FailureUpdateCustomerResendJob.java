
package in.com.v2kart.core.cronjob;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import in.com.v2kart.core.dao.V2CustomerDao;
import in.com.v2kart.sapinboundintegration.exceptions.V2SapConnectionException;
import in.com.v2kart.sapinboundintegration.services.V2CustomerSapIntegrationService;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * The class <Code> V2FailureUpdateCustomerResendJob </Code> contains a job for fetching all SAP FAILURE Updates Customers which are not
 * older than 15 days and then send/post again to SAP as per associated cronjob expression.
 * <code>failure-updatedcustomer-resend-to-sap-cronjob</code>.
 * 
 * @author Nagarro_Devraj
 * @version 1.0
 */
public class V2FailureUpdateCustomerResendJob extends AbstractJobPerformable<CronJobModel> {
    /** Logger Instance for this class */
    private final Logger LOG = Logger.getLogger(V2FailureUpdateCustomerResendJob.class);

    /** V2CustomerDao bean injection */
    private V2CustomerDao v2CustomerDao;

    /** V2CustomerSapIntegrationService bean injection */
    private V2CustomerSapIntegrationService v2CustomerSapInboundIntegrationService;

    /** {@inheritDoc} */
    @Override
    public PerformResult perform(final CronJobModel paramT) {
        LOG.info("Attempting to synchronize updated customers with SAP");
        boolean success = true;
        final List<CustomerModel> failureCustomersUpdateList = v2CustomerDao.findCustomerToResendSap( Boolean.TRUE);

        LOG.info(failureCustomersUpdateList.size() + " customer(s) to synchronize.");

        try {
            for (final CustomerModel customerModel : failureCustomersUpdateList) {
                v2CustomerSapInboundIntegrationService.updateCustomer(customerModel);
            }
        } catch (final V2SapConnectionException sape) {
            success = false;
            LOG.error("Error in sending Failure Customers to SAP", sape);
        }

        if (success) {
            return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
        }
        return new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
    }

   
    public V2CustomerDao getV2CustomerDao() {
        return v2CustomerDao;
    }


    public void setV2CustomerDao(V2CustomerDao v2CustomerDao) {
        this.v2CustomerDao = v2CustomerDao;
    }


    public V2CustomerSapIntegrationService getV2CustomerSapInboundIntegrationService() {
        return v2CustomerSapInboundIntegrationService;
    }


    public void setV2CustomerSapInboundIntegrationService(V2CustomerSapIntegrationService v2CustomerSapInboundIntegrationService) {
        this.v2CustomerSapInboundIntegrationService = v2CustomerSapInboundIntegrationService;
    }
}
