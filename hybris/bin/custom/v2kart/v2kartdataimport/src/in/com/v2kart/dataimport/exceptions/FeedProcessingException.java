/**
 * 
 */
package in.com.v2kart.dataimport.exceptions;

/**
 * The Class FeedProcessingException.
 * 
 * @author ashish2483
 */
public class FeedProcessingException extends ImportEngineRuntimeException {

    public static enum FailureCause {
        PARSING_ERROR, CONSTRAINT_VIOLATION, OTHERS;
    }

    /**
     * feedRowNumber instance
     */
    private final int feedRowNumber;

    /**
     * feed column reference
     */
    private final int columnNumber;

    /**
     * failureCause instance
     */
    private final FailureCause failureCause;

    /**
     * Gets the feed row number.
     * 
     * @return the feedRowNumber
     */
    public int getFeedRowNumber() {
        return feedRowNumber;
    }

    /**
     * Gets the failure cause.
     * 
     * @return the failureCause
     */
    public FailureCause getFailureCause() {
        return failureCause;
    }

    /**
     * Gets the column number.
     * 
     * @return the columnNumber
     */
    public int getColumnNumber() {
        return columnNumber;
    }

    /**
     * Instantiates a new feed processing exception.
     * 
     * @param failureCause
     *        the failure cause
     * @param feedRowNumber
     *        the feed row number
     * @param columnNumber
     *        the column number
     * @param throwable
     *        the throwable
     */
    public FeedProcessingException(final FailureCause failureCause, final int feedRowNumber, final int columnNumber,
            final Throwable throwable) {
        super("Failed to process CSV/Excel feed. FailureCause : " + failureCause + " , Row Num : " + feedRowNumber + " , Col Number : "
                + columnNumber, throwable);
        this.feedRowNumber = feedRowNumber;
        this.columnNumber = columnNumber;
        this.failureCause = failureCause;
    }

}
