/**
 * 
 */
package in.com.v2kart.dataimport.commands;

import in.com.v2kart.dataimport.ImportDataSummaryInfo;
import in.com.v2kart.dataimport.context.ImportCommandContext;
import in.com.v2kart.dataimport.context.ImportCommandContextHolder;
import in.com.v2kart.dataimport.exceptions.FeedSourceReadException;
import in.com.v2kart.importlog.data.ImportLogEventData;
import in.com.v2kart.importlog.enums.ImportLogEventHint;
import in.com.v2kart.importlog.enums.ImportLogEventStatus;
import in.com.v2kart.importlog.enums.ImportLogEventType;
import in.com.v2kart.importlog.services.ImportLogService;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.validation.enums.Severity;

/**
 * Abstract implementation of ImportCommand interface. It further abstract the execution flow in to three parts
 * 
 * init() - function to invoke initialization process if any importData() - function to trigger import process cleanUp() - function for
 * release resource or any clean up activity
 * 
 * It also provide dummy/empty implementation of init and cleanUp functions.
 * 
 */
public abstract class AbstractImportCommand implements ImportCommand {

    /**
     * Logger instance
     */
    private static final Logger LOG = Logger.getLogger(AbstractImportCommand.class);

    /**
     * ImportLogEventType type
     */
    protected ImportLogEventType logEventType;

    @Autowired
    protected ImportLogService importLogService;

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dataimport.commands.ImportCommand#execute()
     */
    @Override
    public final boolean execute() throws Exception {
        ImportDataSummaryInfo importDataSummaryInfo = null;
        String logMessage = null;
        final String clazName = this.getClass().getSimpleName();
        this.initializeContext();
        try {
            LOG.info("Starting import command : " + this.logEventType);
            LOG.debug("Invoking command init function for " + clazName);
            init();
            LOG.debug("Executing import data process for " + clazName);
            importDataSummaryInfo = importData();
            LOG.debug("Executing clean up process for " + clazName);
            cleanUp();
            logMessage = String.format("%1$S executed successfully;%n %2$s %n", this.logEventType, importDataSummaryInfo);
            LOG.info(logMessage);
            this.logInfoToService(logMessage);
            return importDataSummaryInfo.isUpdatePerformed();
        } catch (final FeedSourceReadException exception) {
            throw exception;
        } catch (final Exception exception) {
            logMessage = String.format("%1$S FAILED;%nImport summary details %2$s %n", this.logEventType, importDataSummaryInfo);
            LOG.error(logMessage, exception);
            this.logErrorToService(exception, logMessage);
            throw exception;
        } finally {
            // clearing up context
            ImportCommandContextHolder.clearContext();
        }
    }

    /**
     * logToService method to persist log event to database
     * 
     * @param exception
     * @param message
     */
    private void logErrorToService(final Exception exception, final String message) {
        final ImportLogEventData eventData = new ImportLogEventData();
        eventData.setException(exception);
        eventData.setMessage(message);
        eventData.setSeverity(Severity.ERROR);
        eventData.setEventStatus(ImportLogEventStatus.FAILED);
        final ImportCommandContext commandContext = ImportCommandContextHolder.getContext();
        eventData.setEventType(commandContext.getLogEventType());
        eventData.setThreadIdentifier(commandContext.getIdentifier());
        eventData.setEventHint(ImportLogEventHint.IMPORT_SUMMARY);
        importLogService.logEvent(eventData);
    }

    /**
     * Function to initialize ImportCommandContext
     * 
     * @return ImportCommandContext instance
     */
    private ImportCommandContext initializeContext() {
        final ImportCommandContext commandContext = ImportCommandContextHolder.getContext();
        final String identifier = String.format("%1$S | %2$tD %2$tT", logEventType, new Date());
        commandContext.setIdentifier(identifier);
        commandContext.setLogEventType(getLogEventType());
        return commandContext;
    }

    /**
     * @param message
     */
    private void logInfoToService(final String message) {
        final ImportLogEventData eventData = new ImportLogEventData();
        final ImportCommandContext commandContext = ImportCommandContextHolder.getContext();
        eventData.setEventType(commandContext.getLogEventType());
        eventData.setThreadIdentifier(commandContext.getIdentifier());
        eventData.setMessage(message);
        eventData.setSeverity(Severity.INFO);
        eventData.setEventStatus(ImportLogEventStatus.SUCCESS);
        eventData.setEventHint(ImportLogEventHint.IMPORT_SUMMARY);
        importLogService.logEvent(eventData);
    }

    /**
     * @return the logEventType
     */
    public final ImportLogEventType getLogEventType() {
        return logEventType;
    }

    /**
     * @param logEventType
     *        the logEventType to set
     */
    public final void setLogEventType(final ImportLogEventType logEventType) {
        this.logEventType = logEventType;
    }

    /**
    * 
    */
    protected void cleanUp() {
        // Empty implementaion

    }

    /**
    * 
    */
    protected void init() {
        // Empty implementaion

    }

    protected abstract ImportDataSummaryInfo importData() throws Exception;

}
