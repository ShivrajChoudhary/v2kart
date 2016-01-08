/**
 * 
 */
package in.com.v2kart.v2kartpayupaymentintegration.services;

import in.com.v2kart.core.exception.V2Exception;
import in.com.v2kart.core.payment.services.V2EncryptionService;

import java.util.Map;

/**
 * @author pankajaggarwal
 * 
 */
public interface PAYUEncryptionService extends V2EncryptionService {

    /**
     * For calculating request hash for Command Execution
     * 
     * @param parameters
     * @return
     * @throws V2Exception
     */
    String getRequestHashForCommandExecution(final Map<String, String> parameters) throws V2Exception;

}
