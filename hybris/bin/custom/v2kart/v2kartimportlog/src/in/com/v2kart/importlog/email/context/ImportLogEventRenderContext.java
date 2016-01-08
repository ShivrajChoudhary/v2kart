/**
 * 
 */
package in.com.v2kart.importlog.email.context;

import in.com.v2kart.importlog.model.log.ImportLogEventModel;

import java.util.List;

import org.apache.velocity.VelocityContext;

/**
 * 
 * @author Nagarro-Dev
 * 
 */
public class ImportLogEventRenderContext extends VelocityContext
{

    private final List<ImportLogEventModel> logEvents;

    /**
     * Constructor
     */
    public ImportLogEventRenderContext(final List<ImportLogEventModel> logEvents)
    {
        this.logEvents = logEvents;
    }

    /**
     * @return the logEvents
     */
    public List<ImportLogEventModel> getLogEvents()
    {
        return logEvents;
    }

}