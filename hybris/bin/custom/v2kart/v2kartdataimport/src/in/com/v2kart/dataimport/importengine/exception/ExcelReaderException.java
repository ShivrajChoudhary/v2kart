package in.com.v2kart.dataimport.importengine.exception;

/**
 * @author gaurav2007
 * 
 */
public class ExcelReaderException extends RuntimeException {

    /**
     * Instantiates a new excel reader exception.
     */
    public ExcelReaderException() {
        super();
    }

    /**
     * Instantiates a new excel reader exception.
     * 
     * @param message
     *        the message
     */
    public ExcelReaderException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new excel reader exception.
     * 
     * @param throwable
     *        the throwable
     */
    public ExcelReaderException(final Throwable throwable) {
        super(throwable);
    }

    /**
     * Instantiates a new excel reader exception.
     * 
     * @param message
     *        the message
     * @param throwable
     *        the throwable
     */
    public ExcelReaderException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

}
