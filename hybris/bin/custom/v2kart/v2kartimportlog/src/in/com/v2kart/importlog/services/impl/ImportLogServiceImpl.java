/**
 * 
 */
package in.com.v2kart.importlog.services.impl;

import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.validation.enums.Severity;

import in.com.v2kart.importlog.dao.ImportLogDao;
import in.com.v2kart.importlog.data.ImportLogEventData;
import in.com.v2kart.importlog.enums.ImportLogEventStatus;
import in.com.v2kart.importlog.enums.ImportLogEventType;
import in.com.v2kart.importlog.model.log.ImportLogEventModel;
import in.com.v2kart.importlog.services.ImportLogService;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * Service to create and getting of LogEvents.
 * 
 * @author Nagarro-Dev.
 */
public class ImportLogServiceImpl implements ImportLogService
{

    @Resource
    private ModelService modelService;

    private Converter<ImportLogEventData, ImportLogEventModel> logEventReverseConverter;

    @Resource
    ImportLogDao importLogDao;

    @Override
    public void logEvent(final ImportLogEventData logEvent)
    {
        final ImportLogEventModel logModel = this.modelService.create(ImportLogEventModel.class);
        logEventReverseConverter.convert(logEvent, logModel);
        this.modelService.save(logModel);
        this.modelService.refresh(logModel);
    }

    @Override
    public void logEvent(final ImportLogEventType eventType, final ImportLogEventStatus eventStatus, final Object message)
    {
        createLogEvent(eventType, eventStatus, message, null, null);
    }

    @Override
    public void logEvent(final ImportLogEventType eventType, final ImportLogEventStatus eventStatus, final Object message,
            final Severity severity)
    {
        createLogEvent(eventType, eventStatus, message, severity, null);
    }

    @Override
    public void logEvent(final ImportLogEventType eventType, final ImportLogEventStatus eventStatus, final Object message,
            final Severity severity, final Throwable exception)
    {
        createLogEvent(eventType, eventStatus, message, severity, exception);
    }

    @Override
    public List<ImportLogEventModel> getLogEventsByTypeAndStatus(final List<ImportLogEventType> eventTypes,
            final List<ImportLogEventStatus> eventStatuses, final Date from, final Date to)
    {
        return importLogDao.getLogEventsByTypeAndStatus(eventTypes, eventStatuses, from, to);
    }

    @Override
    public List<ImportLogEventModel> getLogEventsByType(final ImportLogEventType eventType, final Date from, final Date to)
    {
        return importLogDao.getLogEventsByType(eventType, from, to);
    }

    /**
     * @return the logEventReverseConverter
     */
    public Converter<ImportLogEventData, ImportLogEventModel> getLogEventReverseConverter()
    {
        return logEventReverseConverter;
    }

    /**
     * @param logEventReverseConverter
     *        the logEventReverseConverter to set
     */
    public void setLogEventReverseConverter(final Converter<ImportLogEventData, ImportLogEventModel> logEventReverseConverter)
    {
        this.logEventReverseConverter = logEventReverseConverter;
    }

    /**
     * @param eventType
     * @param eventStatus
     * @param message
     * @param severity
     * @param exception
     */
    private void createLogEvent(final ImportLogEventType eventType, final ImportLogEventStatus eventStatus,
            final Object message, final Severity severity, final Throwable exception)
    {
        final ImportLogEventModel logModel = this.modelService.create(ImportLogEventModel.class);
        logModel.setEventType(eventType);
        logModel.setEventStatus(eventStatus);
        logModel.setMessage(message == null ? null : message.toString());
        logModel.setSeverity(severity);
        logModel.setExceptionTrace(exception == null ? null : ExceptionUtils.getStackTrace(exception));
        this.modelService.save(logModel);
        this.modelService.refresh(logModel);
    }
}
