/**
 * 
 */
package in.com.v2kart.v2kartebspaymentintegration.enums;

/**
 * @author vikrant2480
 * 
 */
public enum EBSDEecisionEnum {

    SUCCESS("0"), FAILURE("1");

    private String value;

    /**
     * Instantiates a new decision enum.
     * 
     * @param value
     *        the value
     */
    EBSDEecisionEnum(final String value) {
        this.value = value;
    }

    /**
     * Gets the value.
     * 
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value.
     * 
     * @param value
     *        the value to set
     */
    public void setValue(final String value) {
        this.value = value;
    }

}
