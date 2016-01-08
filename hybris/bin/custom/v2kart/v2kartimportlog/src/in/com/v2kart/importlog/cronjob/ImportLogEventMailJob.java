/**
 * 
 */
package in.com.v2kart.importlog.cronjob;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import in.com.v2kart.importlog.email.ImportLogEventMailSender;
import in.com.v2kart.importlog.model.log.ImportLogEventMailCronJobModel;
import in.com.v2kart.importlog.model.log.ImportLogEventModel;
import in.com.v2kart.importlog.services.ImportLogService;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * <ImportLogEventMailJob> fetch and sends Log Events in the mail.
 * 
 * @author Nagarro-Dev.
 */
public class ImportLogEventMailJob extends AbstractJobPerformable<ImportLogEventMailCronJobModel>
{

    /** Logger instance */
    private static final Logger LOG = Logger.getLogger(ImportLogEventMailJob.class);

    /** ImportLogService instance */
    private ImportLogService importLogService;

    /** logEventMailSender instance */
    private ImportLogEventMailSender importLogEventMailSender;

    @Override
    public PerformResult perform(final ImportLogEventMailCronJobModel cronJob)
    {

        final Date lastRunTime = getLatestRunTime(cronJob);
        final Date currentDateTime = new Date();

        LOG.info("Log Events Extract and mailing starts...");

        try
        {
            final List<ImportLogEventModel> logEvents = importLogService.getLogEventsByTypeAndStatus(cronJob.getEventTypes(),
                    cronJob.getEventStatuses(), lastRunTime, currentDateTime);
            if (logEvents != null && !logEvents.isEmpty())
            {
                importLogEventMailSender.sendEmail(importLogEventMailSender.createLogEventsEmail(logEvents));
            }
            setLastRunDate(cronJob, currentDateTime);
        } catch (final Exception e)
        {
            LOG.error("Failed to fetch log events data and mail", e);
        }

        // save the last run date
        modelService.save(cronJob);

        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    /**
     * Get the date when the job last ran
     */
    private Date getLatestRunTime(final ImportLogEventMailCronJobModel jobModel)
    {
        Date lastRunDate = jobModel.getLastRunDate();

        if (lastRunDate == null)
        {
            lastRunDate = new Date(0l); // set it back to 1970 so we can get all updates since the start of the
            // system
        }
        return lastRunDate;
    }

    /** Set the date when the job last ran */
    private void setLastRunDate(final ImportLogEventMailCronJobModel jobModel, final Date date)
    {
        jobModel.setLastRunDate(date);
    }

    /**
     * @return the ImportLogService
     */
    public ImportLogService getImportLogService()
    {
        return importLogService;
    }

    /**
     * @param importLogService
     *        the ImportLogService to set
     */
    public void setImportLogService(final ImportLogService importLogService)
    {
        this.importLogService = importLogService;
    }

    /**
     * @return the ImportLogEventMailSender
     */
    public ImportLogEventMailSender getImportLogEventMailSender()
    {
        return importLogEventMailSender;
    }

    /**
     * @param importLogEventMailSender
     *        the ImportLogEventMailSender to set
     */
    public void setImportLogEventMailSender(final ImportLogEventMailSender importLogEventMailSender)
    {
        this.importLogEventMailSender = importLogEventMailSender;
    }

}
