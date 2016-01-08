package in.com.v2kart.sapinboundintegration.exceptions;

/**
 * V2SapIntegrationException Class
 * 
 * @author satvir_nagarro
 */
public abstract class V2SapIntegrationException extends RuntimeException {

    /** Serial Version UID */
    private static final long serialVersionUID = -570919178996069962L;

    /**
     * Constructor with Message and Throwable
     * 
     * @param message
     * @param throwable
     */
    public V2SapIntegrationException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Constructor with Message
     * 
     * @param message
     */
    public V2SapIntegrationException(final String message) {
        super(message);
    }
}
