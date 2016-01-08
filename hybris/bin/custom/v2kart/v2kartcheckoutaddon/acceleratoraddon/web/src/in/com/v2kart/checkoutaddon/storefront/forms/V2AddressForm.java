/**
 *
 */
package in.com.v2kart.checkoutaddon.storefront.forms;

import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;

/**
 * @author Anuj Kumar
 * 
 */
public class V2AddressForm extends AddressForm
{

    private String mobileno;
    private String countryName;
    private String smsmobileno;

    /**
     * @return smsmobileno
     */
    public String getSmsmobileno() {
        return smsmobileno;
    }

    /**
     * @param smsmobileno
     */
    public void setSmsmobileno(final String smsmobileno) {
        this.smsmobileno = smsmobileno;
    }

    /**
     * @return the mobileno
     */
    public String getMobileno()
    {
        return mobileno;
    }

    /**
     * @param mobileno
     *        the mobileno to set
     */
    public void setMobileno(final String mobileno)
    {
        this.mobileno = mobileno;
    }

    /**
     * @return the countryName
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * @param countryName
     *        the countryName to set
     */
    public void setCountryName(final String countryName) {
        this.countryName = countryName;
    }

}
