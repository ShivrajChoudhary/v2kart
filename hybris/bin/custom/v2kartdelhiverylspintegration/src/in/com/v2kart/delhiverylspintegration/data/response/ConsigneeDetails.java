/**
 * 
 */
package in.com.v2kart.delhiverylspintegration.data.response;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author vikrant2480
 * 
 */
public class ConsigneeDetails {

    @JsonProperty("Name")
    private String name;

    @JsonProperty("PinCode")
    private String pinCode;

    @JsonProperty("Address1")
    private String[] address1;

    @JsonProperty("Address2")
    private String[] address2;

    @JsonProperty("Address3")
    private String address3;

    @JsonProperty("State")
    private String state;

    @JsonProperty("City")
    private String city;

    @JsonProperty("Country")
    private String country;

    @JsonProperty("Telephone1")
    private String[] telephone1;

    @JsonProperty("Telephone2")
    private String telephone2;

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
     * @return the pinCode
     */
    public String getPinCode() {
        return pinCode;
    }

    /**
     * @param pinCode
     *        the pinCode to set
     */
    public void setPinCode(final String pinCode) {
        this.pinCode = pinCode;
    }

    /**
     * @return the address1
     */
    public String[] getAddress1() {
        return address1;
    }

    /**
     * @param address1
     *        the address1 to set
     */
    public void setAddress1(final String[] address1) {
        this.address1 = address1;
    }

    /**
     * @return the address2
     */
    public String[] getAddress2() {
        return address2;
    }

    /**
     * @param address2
     *        the address2 to set
     */
    public void setAddress2(final String[] address2) {
        this.address2 = address2;
    }

    /**
     * @return the address3
     */
    public String getAddress3() {
        return address3;
    }

    /**
     * @param address3
     *        the address3 to set
     */
    public void setAddress3(final String address3) {
        this.address3 = address3;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state
     *        the state to set
     */
    public void setState(final String state) {
        this.state = state;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city
     *        the city to set
     */
    public void setCity(final String city) {
        this.city = city;
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country
     *        the country to set
     */
    public void setCountry(final String country) {
        this.country = country;
    }

    /**
     * @return the telephone1
     */
    public String[] getTelephone1() {
        return telephone1;
    }

    /**
     * @param telephone1
     *        the telephone1 to set
     */
    public void setTelephone1(final String[] telephone1) {
        this.telephone1 = telephone1;
    }

    /**
     * @return the telephone2
     */
    public String getTelephone2() {
        return telephone2;
    }

    /**
     * @param telephone2
     *        the telephone2 to set
     */
    public void setTelephone2(final String telephone2) {
        this.telephone2 = telephone2;
    }
}
