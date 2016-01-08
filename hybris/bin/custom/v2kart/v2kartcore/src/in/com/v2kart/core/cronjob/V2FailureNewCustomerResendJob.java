/**
 * 
 */
package in.com.v2kart.core.cronjob;

import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import in.com.v2kart.core.dao.V2CustomerDao;
import in.com.v2kart.sapinboundintegration.exceptions.V2SapConnectionException;
import in.com.v2kart.sapinboundintegration.services.V2CustomerSapIntegrationService;
import in.com.v2kart.sapinboundintegration.ws.customer.CustCreateReq;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * The class <Code> V2FailureNewCustomerResendJob </Code> contains a job for fetching all SAP FAILURE New Customer which are not much older
 * than 15 days and then send/post again to SAP as per cronjob expression in file <code>failure-newcustomer-resend-to-sap-cronjob</code>.
 * 
 * @author Nagarro_devraj802
 * @version 1.0
 * 
 */
public class V2FailureNewCustomerResendJob extends AbstractJobPerformable<CronJobModel> {
    /** Logger Instance for this class */
    private final Logger LOG = Logger.getLogger(V2FailureNewCustomerResendJob.class);

    /** V2CustomerSapIntegrationService bean injection */
    private V2CustomerSapIntegrationService v2CustomerSapInboundIntegrationService;

    /** AbstractPopulatingConverter bean injection */
    private AbstractPopulatingConverter<CustomerModel, CustCreateReq> converter;

    /** V2CustomerDao bean injection */
    private V2CustomerDao v2CustomerDao;

    /** {@inheritDoc} */
    @Override
    public PerformResult perform(final CronJobModel paramT) {
        LOG.info("New Customer Failure Resend Job Starts...");
        boolean success = true;
        final List<CustomerModel> failureCustomersList = v2CustomerDao.findCustomerToResendSap(Boolean.FALSE);

        try {
            for (final CustomerModel customerModel : failureCustomersList) {
                v2CustomerSapInboundIntegrationService.createCustomer(customerModel);
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

    /**
     * @return the converter
     */
    public AbstractPopulatingConverter<CustomerModel, CustCreateReq> getConverter() {
        return converter;
    }

    /**
     * @return the v2CustomerSapInboundIntegrationService
     */
    public V2CustomerSapIntegrationService getV2CustomerSapInboundIntegrationService() {
        return v2CustomerSapInboundIntegrationService;
    }

    /**
     * @param v2CustomerSapInboundIntegrationService the v2CustomerSapInboundIntegrationService to set
     */
    public void setV2CustomerSapInboundIntegrationService(V2CustomerSapIntegrationService v2CustomerSapInboundIntegrationService) {
        this.v2CustomerSapInboundIntegrationService = v2CustomerSapInboundIntegrationService;
    }

    /**
     * @return the v2CustomerDao
     */
    public V2CustomerDao getV2CustomerDao() {
        return v2CustomerDao;
    }

    /**
     * @param v2CustomerDao the v2CustomerDao to set
     */
    public void setV2CustomerDao(V2CustomerDao v2CustomerDao) {
        this.v2CustomerDao = v2CustomerDao;
    }

    /**
     * @param converter the converter to set
     */
    public void setConverter(AbstractPopulatingConverter<CustomerModel, CustCreateReq> converter) {
        this.converter = converter;
    }
}
