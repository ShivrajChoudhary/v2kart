/**
 *
 */
package in.com.v2kart.v2kartpayupaymentintegration.enums;

/**
 * The Enum PAYUDecisionEnum.
 * 
 * @author gaurav2007
 */
public enum PAYUDecisionEnum {

    SUCCESS("Success"), FAILURE("Failure");

    private String value;

    /**
     * Instantiates a new decision enum.
     * 
     * @param value
     *        the value
     */
    PAYUDecisionEnum(final String value) {
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
