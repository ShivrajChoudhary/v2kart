/**
 * 
 */
package in.com.v2kart.dataimport.context;

import in.com.v2kart.importlog.enums.ImportLogEventType;

/**
 * ImportCommandContext - Context information for import job commands.
 */
public class ImportCommandContext {

    /**
     * Import command identifier
     */
    private String identifier;

    /**
     * Import command log event type
     */
    private ImportLogEventType logEventType;

    /**
     * Gets the identifier.
     * 
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the identifier.
     * 
     * @param identifier
     *        the identifier to set
     */
    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    /**
     * Gets the log event type.
     * 
     * @return the logEventType
     */
    public ImportLogEventType getLogEventType() {
        return logEventType;
    }

    /**
     * Sets the log event type.
     * 
     * @param logEventType
     *        the logEventType to set
     */
    public void setLogEventType(final ImportLogEventType logEventType) {
        this.logEventType = logEventType;
    }

}
