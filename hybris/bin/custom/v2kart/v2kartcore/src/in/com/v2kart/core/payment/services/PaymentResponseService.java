/**
 * 
 */
package in.com.v2kart.core.payment.services;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * The Interface PaymentResponseService.
 * 
 * @author gaurav2007
 */
public interface PaymentResponseService {

    /**
     * Gets the request parameter map.
     * 
     * @param request
     *        the request
     * @return the request parameter map
     * @throws Exception
     *         the exception
     */
    Map<String, String> getRequestParameterMap(HttpServletRequest request) throws Exception;

}
