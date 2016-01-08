/**
 * 
 */
package in.com.v2kart.dataimport.exceptions;

/**
 * The Class FeedPersistanceException.
 * 
 * @author ashish2483
 */
public class FeedPersistanceException extends ImportEngineRuntimeException {

    /**
     * The Enum FailureCause.
     */
    public static enum FailureCause {
        DISTRIBUTION_CHANNEL_NOT_FOUND("Distribution Channel not found for row no.:%s"), ORDER_HEADER_REASON_NOT_FOUND(
                "Order Header Reason not found for row no.:%s"), DELIVERY_BLOCK_REASON_NOT_FOUND(
                "Delivery Block Reason not found row number: %s"), ORDER_ENTRY_NOT_FOUND(
                "Order entry not found for order with row number: %s"), ORDER_CUSTOMER_NOT_FOUND("Customer Not Found for row number:%s"), DELIVERY_TIME_NOT_FOUND(
                "Delivery Time not found for row number:%s"), OTHERS("Failed to process CSV/Excel feed for Row Number:%s"), UNIT_NOT_FOUND(
                "Unit not found for row number:%s"), ORDER_LINE_PRODUCT_NOT_FOUND("Product not found for row number:%s"), PRODUCT_NOT_FOUND(
                "Product Not Found for rows: %s"), BASE_PRODUCT_NOT_FOUND("Base Product Not Found for rows: %s"), PAYMENT_MODE_NOT_FOUND(
                "Payment Mode not found for row number: %s"), CUSTOMER_NOT_FOUND("Customer Not Found for rows: %s"), WAREHOUSE_NOT_FOUND(
                "Warehouse Not Found for rows: %s"), INVALID_ARTICLE_TYPE("Invalid Article Type for rows: %s"), CATEGORY_FOR_BRAND_NOT_FOUND(
                "Category for Brand Not Found for rows: %s"), OWNER_NOT_FOUND("Cannot save entity. Owning Entity Not Found for rows: %s"), IMAGE_NOT_FOUND(
                "Image not found for row number: %s"), CATEGORY_NOT_FOUND("Category not Found for rows: %s"), STOCK_CREATION_DATE_ERROR(
                "Stock creation date and time has incorrect format for rows :%s"), CATALOG_VERSION_NOT_FOUND(
                "No catalog found for given format for rows: %s");

        private String message;

        /**
         * Instantiates a new failure cause.
         * 
         * @param message
         *        the message
         */
        private FailureCause(final String message) {
            this.message = message;
        }

        /**
         * Gets the message.
         * 
         * @return the message
         */
        public String getMessage() {
            return message;
        }

    }

    /**
     * feedRowNumber instance
     */
    private final int feedRowNumber;

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
     * Overloaded constructor.
     * 
     * @param failureCause
     *        the failure cause
     * @param feedRowNumber
     *        the feed row number
     * @param throwable
     *        the throwable
     */
    public FeedPersistanceException(final FailureCause failureCause, final int feedRowNumber, final Throwable throwable) {
        super(String.format(failureCause.getMessage(), Integer.valueOf(feedRowNumber)));
        this.feedRowNumber = feedRowNumber;
        this.failureCause = failureCause;
    }

    /**
     * Overloaded constructor.
     * 
     * @param message
     *        the message
     * @param failureCause
     *        the failure cause
     * @param feedRowNumber
     *        the feed row number
     * @param throwable
     *        the throwable
     */
    public FeedPersistanceException(final String message, final FailureCause failureCause, final int feedRowNumber,
            final Throwable throwable) {
        super(message, throwable);
        this.feedRowNumber = feedRowNumber;
        this.failureCause = failureCause;
    }

    /**
     * Overloaded constructor.
     * 
     * @param message
     *        the message
     * @param failureCause
     *        the failure cause
     * @param feedRowNumber
     *        the feed row number
     */
    public FeedPersistanceException(final String message, final FailureCause failureCause, final int feedRowNumber) {
        super(message);
        this.feedRowNumber = feedRowNumber;
        this.failureCause = failureCause;
    }

    /**
     * Overloaded constructor.
     * 
     * @param failureCause
     *        the failure cause
     * @param feedRowNumber
     *        the feed row number
     */
    public FeedPersistanceException(final FailureCause failureCause, final int feedRowNumber) {
        super(String.format(failureCause.getMessage(), Integer.valueOf(feedRowNumber)));
        this.feedRowNumber = feedRowNumber;
        this.failureCause = failureCause;
    }

}
