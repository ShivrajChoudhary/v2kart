/**
 *
 */
package in.com.v2kart.v2kartpayupaymentintegration.services.impl;

import in.com.v2kart.core.payment.services.PaymentResponseService;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * The Class PAYUPaymentResponseServiceImpl.
 *
 * @author gaurav2007
 */
public class PAYUPaymentResponseServiceImpl implements PaymentResponseService {

    @Override
    public Map<String, String> getRequestParameterMap(final HttpServletRequest request) throws Exception {
        final Map<String, String> map = new HashMap<String, String>();

        final Enumeration myEnum = request.getParameterNames();
        while (myEnum.hasMoreElements()) {
            final String paramName = (String) myEnum.nextElement();
            final String paramValue = request.getParameter(paramName);
            map.put(paramName, paramValue);
        }
        return map;
    }

}
