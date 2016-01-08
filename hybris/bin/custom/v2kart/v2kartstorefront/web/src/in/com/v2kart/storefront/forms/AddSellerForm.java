/**
 * 
 */

package in.com.v2kart.storefront.forms;

/**
 * @author
 * 
 */
public class AddSellerForm {

    String name;
    String email;
    String message;
    String phone;
    String category;

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
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     *        the email to set
     */
    public void setEmail(final String email) {
        this.email = email;
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
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone
     *        the phone to set
     */
    public void setPhone(final String phone) {
        this.phone = phone;
    }

    /**
     * @return category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    
}
