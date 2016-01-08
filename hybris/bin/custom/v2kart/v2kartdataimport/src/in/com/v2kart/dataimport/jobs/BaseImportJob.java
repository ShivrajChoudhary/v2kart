package in.com.v2kart.dataimport.jobs;

import de.hybris.platform.commerceservices.setup.SetupSyncJobService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import in.com.v2kart.dataimport.commands.ImportCommand;
import in.com.v2kart.dataimport.exceptions.FeedSourceReadException;
import in.com.v2kart.dataimport.services.impl.BaseImportServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * BaseImportJob
 * 
 */
public class BaseImportJob extends AbstractJobPerformable<CronJobModel> {

    private static final int MAX_ERROR_COUNT = 5;

    private static final Logger LOG = Logger.getLogger(BaseImportJob.class);

    private static final String CATALOG_SYNC_CONFIG_FORMAT = "importEngine.%1$s.enableSync"; // %1
                                                                                             // placeholder
                                                                                             // is
                                                                                             // for
                                                                                             // job
                                                                                             // name

    private static final String MEDIA_CONVERSION_CONFIG_FORMAT = "importEngine.%1$s.enableMediaConversion";

    private static final String CATALOG_SYNC_DEFAULT_CONFIG_PARAM = String.format(CATALOG_SYNC_CONFIG_FORMAT, "default");

    private String mediaConversionJobCode;

    private List<ImportCommand> importCommandList;

    @Autowired
    private SetupSyncJobService setupSyncJobService;

    @Autowired
    private CronJobService cronJobService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private BaseImportServiceImpl baseImportService;

    /**
     * @return the configurationService
     */
    public final ConfigurationService getConfigurationService() {
        return configurationService;
    }

    /**
     * @param configurationService
     *        the configurationService to set
     */
    public final void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * @return the cronJobService
     */
    public final CronJobService getCronJobService() {
        return cronJobService;
    }

    /**
     * @param cronJobService
     *        the cronJobService to set
     */
    public final void setCronJobService(final CronJobService cronJobService) {
        this.cronJobService = cronJobService;
    }

    /**
     * @param setupSyncJobService
     *        the setupSyncJobService to set
     */
    public final void setSetupSyncJobService(final SetupSyncJobService setupSyncJobService) {
        this.setupSyncJobService = setupSyncJobService;
    }

    /**
     * @return the setupSyncJobService
     */
    public final SetupSyncJobService getSetupSyncJobService() {
        return setupSyncJobService;
    }

    @Override
    public final PerformResult perform(final CronJobModel cronJob) {
        PerformResult result = new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
        for (final ImportCommand importCommand : importCommandList) {
            try {
                int continuousErrorCount = 0;
                boolean isFirst = true;
                while (continuousErrorCount <= MAX_ERROR_COUNT) {
                    try {
                        importCommand.execute();
                        continuousErrorCount = 0;
                        isFirst = false;
                    } catch (final FeedSourceReadException exp) {
                        if (isFirst) {
                            LOG.info(exp.getMessage());
                            LOG.debug(exp);
                            isFirst = false;
                        }
                        continuousErrorCount++;
                    }
                }
            } catch (final Exception e) {
                LOG.error("Import FAILED : " + importCommand.getClass().getSimpleName(), e);
                result = new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
            }
        }
        if (result.getResult() == CronJobResult.SUCCESS) {
            this.runPostHandler(cronJob);
        }
        return result;
    }

    /**
     * Run post handler.
     * 
     * @param cronJob
     *        the cron job
     */
    private void runPostHandler(final CronJobModel cronJob) {
        final String syncConfigPropertyName = String.format(CATALOG_SYNC_CONFIG_FORMAT, cronJob.getCode());
        final Configuration configuration = this.getConfigurationService().getConfiguration();
        final boolean shouldRunSyncJob = configuration.getBoolean(syncConfigPropertyName,
                configuration.getBoolean(CATALOG_SYNC_DEFAULT_CONFIG_PARAM, false));
        final String mediaConversionConfigPropertyName = String.format(MEDIA_CONVERSION_CONFIG_FORMAT, cronJob.getCode());
        final boolean shouldRunMediaConversionJob = configuration.getBoolean(mediaConversionConfigPropertyName, false);
        // run media conversion job if configured true
        if (shouldRunMediaConversionJob) {
            LOG.info("Invoking media conversion job");
            final CronJobModel mediaConversionJob = this.cronJobService.getCronJob(mediaConversionJobCode);
            this.cronJobService.performCronJob(mediaConversionJob, true);
        }
        // sync product catalog
        if (shouldRunSyncJob) {
            LOG.info("Invoking catalog sync job");
            getSetupSyncJobService().executeCatalogSyncJob(baseImportService.getMasterCatalogName());
        }
    }

    /**
     * @return the importCommandList
     */
    public final List<ImportCommand> getImportCommandList() {
        return importCommandList;
    }

    /**
     * @param importCommandList
     *        the importCommandList to set
     */
    public final void setImportCommandList(final List<ImportCommand> importCommandList) {
        this.importCommandList = importCommandList;
    }

    @Value("${media.conversion.cronjob.name}")
    public void setMediaConversionJobCode(String mediaConversionJobCode) {
        this.mediaConversionJobCode = mediaConversionJobCode;
    }

}
