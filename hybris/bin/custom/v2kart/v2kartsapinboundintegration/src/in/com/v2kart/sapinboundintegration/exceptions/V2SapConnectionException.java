package in.com.v2kart.sapinboundintegration.exceptions;

/**
 * V2SapConnectionException Clas
 * 
 * @author satvir_nagarro
 * 
 */
public class V2SapConnectionException extends V2SapIntegrationException {

    /** SerialVersionUID */
    private static final long serialVersionUID = -2459649415127378409L;

    /**
     * Constructor with Message
     * 
     * @param message
     */
    public V2SapConnectionException(final String message) {
        super(message);
    }

    /**
     * Constructor with Message and Throwable
     * 
     * @param message
     * @param throwable
     */
    public V2SapConnectionException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
