/**
 * 
 */
package in.com.v2kart.importlog.services;

import de.hybris.platform.validation.enums.Severity;

import in.com.v2kart.importlog.data.ImportLogEventData;
import in.com.v2kart.importlog.enums.ImportLogEventStatus;
import in.com.v2kart.importlog.enums.ImportLogEventType;
import in.com.v2kart.importlog.model.log.ImportLogEventModel;

import java.util.Date;
import java.util.List;

/**
 * Handles creation and getting of LogEvents.
 * 
 * @author Nagarro-Dev.
 */
public interface ImportLogService
{

    /**
     * 
     * Log an event in the database.
     * 
     * @param logEvent
     *        log event that is to be logged.
     */
    public void logEvent(final ImportLogEventData logEvent);

    /**
     * 
     * Log an event in the database.
     * 
     * @param eventType
     *        log event type.
     * @param eventStatus
     *        log event status.
     * @param message
     *        log event message.
     */
    public void logEvent(final ImportLogEventType eventType, final ImportLogEventStatus eventStatus, final Object message);

    /**
     * 
     * Log an event in the database.
     * 
     * @param eventType
     *        log event type.
     * @param eventStatus
     *        log event status.
     * @param message
     *        log event message.
     * @param severity
     *        log event message severity.
     */
    public void logEvent(final ImportLogEventType eventType, final ImportLogEventStatus eventStatus, final Object message,
            final Severity severity);

    /**
     * 
     * Log an event in the database.
     * 
     * @param eventType
     *        log event type.
     * @param eventStatus
     *        log event status.
     * @param message
     *        log event message.
     * @param severity
     *        log event message severity.
     * @param exception
     *        log event exception.
     */
    public void logEvent(final ImportLogEventType eventType, final ImportLogEventStatus eventStatus, final Object message,
            final Severity severity, Throwable exception);

    /**
     * 
     * Returns a {@link ImportLogEventModel list}. List of the LogEventModel based on given input event type, status and date time from and
     * to.
     * 
     * @param eventTypes
     *        type of the event.
     * @param eventStatuses
     *        list of statuses of the event
     * @param from
     *        date time from event to be fetched
     * @param to
     *        date time to event to be fetched
     * @return List<LogEventModel> List of the events.
     */
    public List<ImportLogEventModel> getLogEventsByTypeAndStatus(final List<ImportLogEventType> eventTypes,
            final List<ImportLogEventStatus> eventStatuses, final Date from, final Date to);

    /**
     * 
     * Returns a {@link ImportLogEventModel list}. List of the LogEventModel based on given input event type and date time from and to.
     * 
     * @param eventType
     *        type of the event.
     * @param from
     *        date time from event to be fetched
     * @param to
     *        date time to event to be fetched
     * @return List<LogEventModel> List of the events.
     */
    public List<ImportLogEventModel> getLogEventsByType(final ImportLogEventType eventType, final Date from, final Date to);

}
