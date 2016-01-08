/**
 * 
 */
package in.com.v2kart.dataimport.exceptions;

/**
 * @author ashish2483
 * 
 */
public class FeedSourceReadException extends ImportEngineRuntimeException {

    /**
     * Instantiates a new feed source read exception.
     * 
     * @param fileName
     *        the file name
     * @param throwable
     *        the throwable
     */
    public FeedSourceReadException(final String fileName, final Throwable throwable) {
        super("Failed to read feeds from : " + fileName, throwable);
    }

    /**
     * Instantiates a new feed source read exception.
     * 
     * @param fileName
     *        the file name
     */
    public FeedSourceReadException(final String fileName) {
        super("Failed to read feeds from : " + fileName);
    }

}
