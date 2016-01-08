/**
 * 
 */
package in.com.v2kart.storefront.forms;

/**
 * @author shubhammaheshwari
 *
 */
public class V2NotifyCustomerForm {
    private String message;
    private String emailId;
    private String productCode;
    private String type;
    private String url;
    private String notificationPrice;
    private String name;
    private String mediaUrl;
    private String productPrice;
    private String currentUserEmailId;
    private String currentUserName;

    /**
     * @param currentUserEmailId
     *        the currentUserEmailId to set
     */
    public void setCurrentUserEmailId(final String currentUserEmailId) {
        this.currentUserEmailId = currentUserEmailId;
    }

    /**
     * @return the currentUserEmailId
     */
    public String getCurrentUserEmailId() {
        return currentUserEmailId;
    }

    /**
     * @param currentUserName
     *        the currentUserName to set
     */
    public void setCurrentUserName(final String currentUserName) {
        this.currentUserName = currentUserName;
    }

    /**
     * @return the currentUserName
     */
    public String getCurrentUserName() {
        return currentUserName;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message
     *        the message to set
     */
    public void setMessage(final String message) {
        this.message = message;
    }

    /**
     * @return the emailId
     */
    public String getEmailId() {
        return emailId;
    }

    /**
     * @param emailId
     *        the emailId to set
     */
    public void setEmailId(final String emailId) {
        this.emailId = emailId;
    }

    /**
     * @return the productCode
     */
    public String getProductCode() {
        return productCode;
    }

    /**
     * @param productCode
     *        the productCode to set
     */
    public void setProductCode(final String productCode) {
        this.productCode = productCode;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *        the type to set
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     *        the url to set
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * @return the notificationPrice
     */
    public String getNotificationPrice() {
        return notificationPrice;
    }

    /**
     * @param notificationPrice
     *        the notificationPrice to set
     */
    public void setNotificationPrice(final String notificationPrice) {
        this.notificationPrice = notificationPrice;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *        the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the mediaUrl
     */
    public String getMediaUrl() {
        return mediaUrl;
    }

    /**
     * @param mediaUrl
     *        the mediaUrl to set
     */
    public void setMediaUrl(final String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    /**
     * @return the productPrice
     */
    public String getProductPrice() {
        return productPrice;
    }

    /**
     * @param productPrice
     *        the productPrice to set
     */
    public void setProductPrice(final String productPrice) {
        this.productPrice = productPrice;
    }
}
