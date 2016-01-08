/**
 * 
 */
package in.com.v2kart.ccavenuepaymentintegration.services.impl;

import de.hybris.platform.util.Config;

import in.com.v2kart.ccavenuepaymentintegration.constants.PaymentConstants.PaymentProperties.CCAVENUE;
import in.com.v2kart.core.payment.services.PaymentResponseService;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import com.ccavenue.security.AesCryptUtil;


/**
 * The Class CCAvenuePaymentResponseServiceImpl.
 * 
 * @author yamini2280
 */
public class CCAvenuePaymentResponseServiceImpl implements PaymentResponseService
{

	private final AesCryptUtil aesCryptUtil = new AesCryptUtil(Config.getParameter(CCAVENUE.WORKING_KEY));

	/*
	 * (non-Javadoc)
	 * 
	 * @see in.com.v2kart.core.payment.services.PaymentResponseService#getRequestParameterMap(javax.servlet.http.
	 * HttpServletRequest)
	 */
	@Override
	public Map<String, String> getRequestParameterMap(final HttpServletRequest request) throws Exception
	{
		final Map<String, String> map = new HashMap<String, String>();

		final String decryptedResponse = aesCryptUtil.decrypt(request.getParameter(CCAVENUE.ENCRYPTED_RESPONSE_PARAM));
		final StringTokenizer tokenizer = new StringTokenizer(decryptedResponse, CCAVENUE.ATTRIBUTE_TOKEN);

		while (tokenizer.hasMoreTokens())
		{
			final String pair = tokenizer.nextToken();
			if (pair != null)
			{
				final StringTokenizer parameterToken = new StringTokenizer(pair, CCAVENUE.KEY_VALUE_PAIR_TOKEN);
				if (parameterToken.hasMoreTokens())
				{
					final String name = parameterToken.nextToken();
					if (parameterToken.hasMoreTokens())
					{
						final String value = parameterToken.nextToken();
						map.put(name, value);
					}
				}
			}
		}
		return map;
	}

}
