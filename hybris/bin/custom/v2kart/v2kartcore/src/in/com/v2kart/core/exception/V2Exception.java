/**
 * 
 */
package in.com.v2kart.core.exception;

/**
 * The Class V2Exception.
 * 
 * @author Anuj Kumar
 */
public class V2Exception extends Exception {

   

    /**
     * @param message
     *        sets the error message
     */
    public V2Exception(final String message) {
        super(message);
    }

    /**
     * @param message
     *        sets the error message
     * @param exp
     *        use for the stacktrace
     */
    public V2Exception(final String message, final Throwable exp) {
        super(message, exp);
    }
}
