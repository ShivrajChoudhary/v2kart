/**
 * 
 */
package in.com.v2kart.importlog.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import in.com.v2kart.importlog.data.ImportLogEventData;
import in.com.v2kart.importlog.model.log.ImportLogEventModel;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.util.Assert;

/**
 * <LogEventReversePopulator> populator that populates ImportLogEventModel from ImportLogEventData for persisting it to data base.
 * 
 * @author Nagarro-Dev
 */
public class LogEventReversePopulator implements Populator<ImportLogEventData, ImportLogEventModel>
{

    /**
    * 
    */
    private static final int MESSAGE_LENGTH_LIMIT = 50000;

    @Override
    public void populate(final ImportLogEventData logEvent, final ImportLogEventModel logModel) throws ConversionException
    {

        Assert.notNull(logEvent, "Parameter logEvent cannot be null.");
        Assert.notNull(logModel, "Parameter logModel cannot be null.");
        logModel.setEventType(logEvent.getEventType());
        logModel.setEventStatus(logEvent.getEventStatus());
        logModel.setSeverity(logEvent.getSeverity());
        logModel.setExceptionTrace(logEvent.getException() == null ? null : this.getFormatedMessage(ExceptionUtils
                .getStackTrace(logEvent.getException())));
        logModel.setEventHint(logEvent.getEventHint());
        logModel.setMessage(this.getFormatedMessage(logEvent.getMessage().toString()));
        logModel.setThreadIdentifier(logEvent.getThreadIdentifier());
    }

    /**
     * @param message
     * @return String - formatted & trimmed message
     */
    public String getFormatedMessage(final String message)
    {
        String formattedMessage = null;
        if (message == null)
        {
            formattedMessage = "";
        }
        else if (message.length() > MESSAGE_LENGTH_LIMIT)
        {
            formattedMessage = message.substring(0, MESSAGE_LENGTH_LIMIT) + "Check the log file to view complete details.";
        }
        else
        {
            formattedMessage = message;
        }
        return formattedMessage;
    }
}
