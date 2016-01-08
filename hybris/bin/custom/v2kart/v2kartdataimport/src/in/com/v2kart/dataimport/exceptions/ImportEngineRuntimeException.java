/**
 * 
 */
package in.com.v2kart.dataimport.exceptions;

/**
 * @author ashish2483
 * 
 */
public class ImportEngineRuntimeException extends RuntimeException {

    public ImportEngineRuntimeException(final String message) {
        super(message);
    }

    /**
    * 
    */
    public ImportEngineRuntimeException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
