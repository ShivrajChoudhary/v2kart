/**
 * 
 */
package in.com.v2kart.core.payment.services;

import in.com.v2kart.core.exception.V2Exception;

import java.util.Map;

/**
 * @author Anuj
 * 
 */
public interface V2EncryptionService {

    /**
     * For calculating request hash
     * 
     * @param parameters
     *        , parameters to be included in hash
     * @return hash value
     * @throws V2Exception
     */
    String getRequestHash(Map<String, String> parameters) throws V2Exception;

    /**
     * For calculating response hash
     * 
     * @param parameters
     *        , parameters to be included in hash
     * @return hash value
     * @throws V2Exception
     */
    String getResponseHash(Map<String, String> parameters) throws V2Exception;

}
