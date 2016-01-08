/**
 *
 */
package in.com.v2kart.checkoutaddon.storefront.forms;

/**
 * The Class FGPaymentInfoForm.
 *
 * @author Anuj Kumar
 */
public class V2PaymentInfoForm extends V2AddressForm {

    private String enforcedPaymentMethod;
    private Boolean isUsingShippingAddress = Boolean.TRUE;
    private Boolean isUsingWallet = Boolean.FALSE;

    /**
     * Gets the enforced payment method.
     *
     * @return the enforcedPaymentMethod
     */
    public String getEnforcedPaymentMethod() {
        return enforcedPaymentMethod;
    }

    /**
     * Sets the enforced payment method.
     *
     * @param enforcedPaymentMethod
     *        the enforcedPaymentMethod to set
     */
    public void setEnforcedPaymentMethod(final String enforcedPaymentMethod) {
        this.enforcedPaymentMethod = enforcedPaymentMethod;
    }

    /**
     * Gets the checks if is using shipping address.
     *
     * @return the isUsingShippingAddress
     */
    public Boolean getIsUsingShippingAddress() {
        return isUsingShippingAddress;
    }

    /**
     * Sets the checks if is using shipping address.
     *
     * @param isUsingShippingAddress
     *        the isUsingShippingAddress to set
     */
    public void setIsUsingShippingAddress(final Boolean isUsingShippingAddress) {
        this.isUsingShippingAddress = isUsingShippingAddress;
    }

    /**
     * Gets the checks if is using wallet.
     *
     * @return the isUsingWallet
     */
    public Boolean getIsUsingWallet() {
        return isUsingWallet;
    }

    /**
     * Sets the checks if is using wallet.
     *
     * @param isUsingWallet
     *        the isUsingWallet to set
     */
    public void setIsUsingWallet(final Boolean isUsingWallet) {
        this.isUsingWallet = isUsingWallet;
    }

}
