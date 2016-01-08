/**
 * 
 */
package in.com.v2kart.core.payment.services;

import in.com.v2kart.core.payment.data.response.V2PaymentResponse;

import java.util.Map;

/**
 * The Interface V2PaymentResponseStrategy.
 * 
 * @author Anuj
 */
public interface V2PaymentResponseStrategy<T extends V2PaymentResponse> {

    /**
     * To convert map of strings into response.
     * 
     * @param responseParameters
     *        , map containing key value pairs for response
     * @return Payment Response
     */
    T interpretResponse(Map<String, String> responseParameters);
}
