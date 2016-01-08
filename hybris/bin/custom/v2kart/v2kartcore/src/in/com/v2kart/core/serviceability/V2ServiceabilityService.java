package in.com.v2kart.core.serviceability;

/**
 * @author arunkumar
 *
 */
public interface V2ServiceabilityService {
    /**
     * Checks if is product servicable for pin code.
     * 
     * @param pinCode
     *        the pin code
     *        countryIsocode
     *        the country isocode
     * @return true, if is product servicable for pin code
     */
    public boolean isProductServicableForPinCode(final String pinCode,final String countryIsocode) ;
    
    /**
     * Checks if is product servicable for pin code.
     * 
     * @param pinCode
     *        the pin code
     * @return true, if is product servicable for pin code
     */
    boolean isProductServicableForPinCode(final String pinCode);
    
    
    /**
     * Checks if is COD available for area.
     * 
     * @param pincode
     *        the pincode
     * @return true, if is code available for area
     */    
    boolean isCodAvailableForArea(final String pincode);
    
}


