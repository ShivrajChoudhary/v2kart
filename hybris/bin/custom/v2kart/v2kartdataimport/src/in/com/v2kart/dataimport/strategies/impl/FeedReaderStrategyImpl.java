package in.com.v2kart.dataimport.strategies.impl;

import in.com.v2kart.dataimport.context.ImportCommandContext;
import in.com.v2kart.dataimport.context.ImportCommandContextHolder;
import in.com.v2kart.dataimport.dto.BaseDto;
import in.com.v2kart.dataimport.exceptions.FeedSourceReadException;
import in.com.v2kart.dataimport.exceptions.ImportEngineRuntimeException;
import in.com.v2kart.dataimport.importengine.FileReader;
import in.com.v2kart.dataimport.strategies.FeedReaderStrategy;
import in.com.v2kart.importlog.data.ImportLogEventData;
import in.com.v2kart.importlog.enums.ImportLogEventHint;
import in.com.v2kart.importlog.enums.ImportLogEventStatus;
import in.com.v2kart.importlog.services.ImportLogService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Predicate;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultiset;

import de.hybris.platform.validation.enums.Severity;

/**
 * implementation of FeedReaderStrategy interface.
 */
public class FeedReaderStrategyImpl implements FeedReaderStrategy {

    private static final Logger LOG = Logger.getLogger(FeedReaderStrategyImpl.class);

    @Autowired
    private ImportLogService importLogService;

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dataimport.strategies.FeedReaderStrategy#readFeed(in.com.v2kart.dataimport.importengine .FileReader)
     */
    @Override
    public <T extends BaseDto> List<T> readFeed(final FileReader reader) {
        return this.readFeed(reader, (Predicate<T>) null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dataimport.strategies.FeedReaderStrategy#readFeed(in.com.v2kart.dataimport.importengine .FileReader,
     * com.google.common.base.Predicate)
     */
    @Override
    public <T extends BaseDto> List<T> readFeed(final FileReader reader, final Predicate<T> predicate) {
        final List<T> items = new ArrayList<>();
        final List<Integer> recordsBeingFiltered = new ArrayList<>();
        try {
            reader.open();
            T nextItem = null;
            while ((nextItem = (T) reader.getNext()) != null) {
                if (predicate == null || predicate.apply(nextItem)) {
                    items.add(nextItem);
                } else {
                    recordsBeingFiltered.add(Integer.valueOf(nextItem.getRowIndex()));
                }
            }
        } catch (final FeedSourceReadException e) {
            throw e;
        } catch (final Exception e) {
            throw new ImportEngineRuntimeException("FAILED: failed to read/process csv file : " + reader.getDefaultFileName(), e);
        } finally {
            try {
                reader.close();
            } catch (final Exception e) {
                LOG.error("failed to close product stream", e);
            }
        }
        // log about filtered records
        String logMessage = null;
        if (!recordsBeingFiltered.isEmpty()) {
            logMessage = "Records skipped from processing are " + recordsBeingFiltered.toString();
            LOG.info(logMessage);
            this.logInfoToService(logMessage);
        }
        // log summary info
        logMessage = String.format("Total no of successfully parsed feeds are  : %1$d %n %2$s", Integer.valueOf(items.size()),
                reader.getReaderSummaryInfo());
        LOG.info(logMessage);
        this.logInfoToService(logMessage);
        return items;
    }

    /**
     * Log info to service.
     * 
     * @param message
     *        the message
     */
    private void logInfoToService(final String message) {
        final ImportLogEventData eventData = new ImportLogEventData();
        final ImportCommandContext commandContext = ImportCommandContextHolder.getContext();
        eventData.setEventType(commandContext.getLogEventType());
        eventData.setThreadIdentifier(commandContext.getIdentifier());
        eventData.setMessage(message);
        eventData.setSeverity(Severity.INFO);
        eventData.setEventStatus(ImportLogEventStatus.SUCCESS);
        eventData.setEventHint(ImportLogEventHint.FILE_READ_SUMMARY);
        importLogService.logEvent(eventData);
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dataimport.strategies.FeedReaderStrategy#readFeed(in.com.v2kart.dataimport.importengine .FileReader,
     * com.google.common.collect.Ordering)
     */
    @Override
    public <T extends BaseDto> Collection<T> readFeed(final FileReader reader, final Ordering<T> ordering) {
        return this.readFeed(reader, ordering, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see in.com.v2kart.dataimport.strategies.FeedReaderStrategy#readFeed(in.com.v2kart.dataimport.importengine .FileReader,
     * com.google.common.collect.Ordering, com.google.common.base.Predicate)
     */
    @Override
    public <T extends BaseDto> Collection<T> readFeed(final FileReader reader, final Ordering<T> ordering, final Predicate<T> predicate) {
        final TreeMultiset<T> items = TreeMultiset.create(ordering);
        final List<Integer> recordsBeingFiltered = new ArrayList<>();
        try {
            reader.open();
            T nextItem = null;
            while ((nextItem = (T) reader.getNext()) != null) {
                if (predicate == null || predicate.apply(nextItem)) {
                    items.add(nextItem);
                } else {
                    recordsBeingFiltered.add(Integer.valueOf(nextItem.getRowIndex()));
                }
            }
        } catch (final FeedSourceReadException e) {
            throw e;
        } catch (final Exception e) {
            throw new ImportEngineRuntimeException("FAILED: failed to read/process csv file : " + reader.getDefaultFileName(), e);
        } finally {
            try {
                reader.close();
            } catch (final Exception e) {
                LOG.error("failed to close product stream", e);
            }
        }
        // log about filtered records
        String logMessage = null;
        if (!recordsBeingFiltered.isEmpty()) {
            logMessage = "Records skipped from processing are " + recordsBeingFiltered.toString();
            LOG.info(logMessage);
            this.logInfoToService(logMessage);
        }
        // log summary info
        logMessage = String.format("Total no of successfully parsed feeds are  : %1$d %n %2$s", Integer.valueOf(items.size()),
                reader.getReaderSummaryInfo());
        LOG.info(logMessage);
        this.logInfoToService(logMessage);
        return items;
    }

    /**
     * @return the ImportLogService
     */
    public ImportLogService getImportLogService() {
        return importLogService;
    }

    /**
     * @param ImportLogService
     *        the ImportLogService to set
     */
    public void setImportLogService(final ImportLogService importLogService) {
        this.importLogService = importLogService;
    }

}
