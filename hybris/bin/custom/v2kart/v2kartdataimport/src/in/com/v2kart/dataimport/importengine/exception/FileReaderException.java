package in.com.v2kart.dataimport.importengine.exception;

/**
 * The Class FileReaderException.
 * 
 * @author gaurav2007
 */
public class FileReaderException extends Exception {

    /**
     * Instantiates a new file reader exception.
     */
    public FileReaderException() {
        super();
    }

    /**
     * Instantiates a new file reader exception.
     * 
     * @param message
     *        the message
     */
    public FileReaderException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new file reader exception.
     * 
     * @param throwable
     *        the throwable
     */
    public FileReaderException(final Throwable throwable) {
        super(throwable);
    }

    /**
     * Instantiates a new file reader exception.
     * 
     * @param message
     *        the message
     * @param throwable
     *        the throwable
     */
    public FileReaderException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

}
