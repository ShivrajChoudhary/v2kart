/**
 * 
 */
package in.com.v2kart.ccavenuepaymentintegration.enums;

/**
 * The Enum CCAvenueDecisionEnum.
 * 
 * @author yamini2280
 */
public enum CCAvenueDecisionEnum
{

	SUCCESS("Success"), FAILURE("Failure"), ABORTED("Aborted"), INVALID("Invalid");

	private String value;

	/**
	 * Instantiates a new V2 decision enum.
	 * 
	 * @param value
	 *           the value
	 */
	CCAvenueDecisionEnum(final String value)
	{
		this.value = value;
	}

	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *           the value to set
	 */
	public void setValue(final String value)
	{
		this.value = value;
	}

}
