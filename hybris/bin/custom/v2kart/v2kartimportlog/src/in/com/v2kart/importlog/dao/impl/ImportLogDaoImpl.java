/**
 * 
 */
package in.com.v2kart.importlog.dao.impl;

import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import in.com.v2kart.importlog.dao.ImportLogDao;
import in.com.v2kart.importlog.enums.ImportLogEventStatus;
import in.com.v2kart.importlog.enums.ImportLogEventType;
import in.com.v2kart.importlog.model.log.ImportLogEventModel;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <ImportLogDaoImpl> DAO to create and getting of LogEvents.
 * 
 * @author Nagarro-Dev.
 */
public class ImportLogDaoImpl extends AbstractItemDao implements ImportLogDao
{

    private static final String FIND_LOGS_BY_TYPE_AND_STATUS_QUERY = "SELECT {pk} FROM {ImportLogEvent} WHERE {eventType} IN (?eventTypes) AND {eventStatus} IN (?eventStatus) AND {modifiedtime} > ?from AND {modifiedtime} <= ?to";

    private static final String FIND_LOGS_BY_TYPE_QUERY = "SELECT {pk} FROM {ImportLogEvent} WHERE {eventTypeokz..} IN (?eventTypes) AND {modifiedtime} > ?from AND {modifiedtime} <= ?to";

    @Override
    public List<ImportLogEventModel> getLogEventsByTypeAndStatus(final List<ImportLogEventType> eventTypes,
            final List<ImportLogEventStatus> eventStatus, final Date from, final Date to)
    {
        ServicesUtil.validateParameterNotNull(eventStatus, "eventStatuses must not be null");

        final Map queryParams = validateAndSetParameter(eventTypes, from, to);

        queryParams.put("eventStatus", eventStatus);
        final SearchResult result = getFlexibleSearchService().search(FIND_LOGS_BY_TYPE_AND_STATUS_QUERY, queryParams);
        return result.getResult();
    }

    @Override
    public List<ImportLogEventModel> getLogEventsByType(final ImportLogEventType eventType, final Date from, final Date to)
    {
        final Map queryParams = validateAndSetParameter(Collections.singletonList(eventType), from, to);
        final SearchResult result = getFlexibleSearchService().search(FIND_LOGS_BY_TYPE_QUERY, queryParams);
        return result.getResult();
    }

    /**
     * @param eventTypes
     * @param from
     * @param to
     * @return Map
     */
    private Map validateAndSetParameter(final List<ImportLogEventType> eventTypes, final Date from, final Date to)
    {
        ServicesUtil.validateParameterNotNull(eventTypes, "eventType must not be null");
        ServicesUtil.validateParameterNotNull(from, "from must not be null");
        ServicesUtil.validateParameterNotNull(to, "to must not be null");
        final Map queryParams = new HashMap();
        queryParams.put("eventTypes", eventTypes);
        queryParams.put("from", from);
        queryParams.put("to", to);
        return queryParams;
    }
}
