/**
 * 
 */
package in.com.v2kart.delhiverylspintegration.enums;

/**
 * @author vikrant2480
 * 
 */
public enum Verbose
{
    META_INFO(0),
    META_INFO_SCAN_INFO(1),
    META_INFO_SCAN_INFO_CONSIGNEE_DETAILS(2);

    private final int value;

    Verbose(final int value)
    {
        this.value = value;
    }

    /**
     * @return the value
     */
    public int getValue()
    {
        return value;
    }
}
