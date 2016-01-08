/**
 * @author shubhammaheshwari
 * 
 */
package in.com.v2kart.aramexintegration.exception;

public class V2AramexIntegrationException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public V2AramexIntegrationException(final String message) {
        super(message);
    }

    public V2AramexIntegrationException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

}
