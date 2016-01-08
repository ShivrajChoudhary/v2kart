/**
 * 
 */
package in.com.v2kart.importlog.dao;

import de.hybris.platform.servicelayer.internal.dao.Dao;

import in.com.v2kart.importlog.enums.ImportLogEventStatus;
import in.com.v2kart.importlog.enums.ImportLogEventType;
import in.com.v2kart.importlog.model.log.ImportLogEventModel;

import java.util.Date;
import java.util.List;

/**
 * <ImportLogDao> Handles creation and getting of LogEvents in database.
 * 
 * @author Nagarro-Dev.
 */
public interface ImportLogDao extends Dao
{

    /**
     * 
     * Returns a {@link LogEventModel list}. List of the LogEventModel based on given input event type, status and date time from and to.
     * 
     * @param eventTypes
     *        type of the event.
     * @param eventStatus
     *        list of statuses of the event
     * @param from
     *        date time from event to be fetched
     * @param to
     *        date time to event to be fetched
     * @return List<LogEventModel> List of the events.
     */
    public List<ImportLogEventModel> getLogEventsByTypeAndStatus(final List<ImportLogEventType> eventTypes,
            final List<ImportLogEventStatus> eventStatus, final Date from, final Date to);

    /**
     * 
     * Returns a {@link LogEventModel list}. List of the LogEventModel based on given input event type and date time from and to.
     * 
     * @param eventType
     *        type of the event.
     * @param from
     *        date time from event to be fetched
     * @param to
     *        date time to event to be fetched
     * @return List<LogEventModel> List of the events.
     */
    public List<ImportLogEventModel> getLogEventsByType(ImportLogEventType eventType, final Date from, final Date to);

}
