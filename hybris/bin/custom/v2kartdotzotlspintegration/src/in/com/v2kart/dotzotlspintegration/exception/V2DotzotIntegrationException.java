/**
 * 
 */
package in.com.v2kart.dotzotlspintegration.exception;

/**
 * @author arunkumar
 * 
 */
public class V2DotzotIntegrationException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public V2DotzotIntegrationException(final String message) {
        super(message);
    }

    public V2DotzotIntegrationException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
