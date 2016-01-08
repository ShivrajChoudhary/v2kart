/**
 * 
 */
package in.com.v2kart.ccavenuepaymentintegration.services.impl;

import de.hybris.platform.util.Config;

import in.com.v2kart.ccavenuepaymentintegration.constants.PaymentConstants.PaymentProperties.CCAVENUE;
import in.com.v2kart.core.exception.V2Exception;
import in.com.v2kart.core.payment.services.V2EncryptionService;

import java.util.Map;

import org.apache.log4j.Logger;

import com.ccavenue.security.AesCryptUtil;


/**
 * The Class CCAvenueEncryptionServiceImpl.
 * 
 * @author yamini2280
 */
public class CCAvenueEncryptionServiceImpl implements V2EncryptionService
{

	private static final Logger LOG = Logger.getLogger(CCAvenueEncryptionServiceImpl.class);

	private final AesCryptUtil aesCryptUtil = new AesCryptUtil(Config.getParameter(CCAVENUE.WORKING_KEY));

	/*
	 * (non-Javadoc)
	 * 
	 * @see in.com.v2kart.core.payment.services.V2EncryptionService#generateEncryptedRequest(java.util.Map)
	 */
	@Override
	public String getRequestHash(final Map<String, String> parameters) throws V2Exception
	{
		if (!areValidValues(parameters))
		{
			throw new V2Exception("Some parameters for hash generation are missing.");
		}
		return generateHash(parameters);
	}

	/**
	 * Are valid values.
	 * 
	 * @param parameters
	 *           the parameters
	 * @return true, if successful
	 */
	private boolean areValidValues(final Map<String, String> parameters)
	{
		boolean isValid = true;
		if (parameters.size() > 0)
		{
			for (final String key : parameters.keySet())
			{
				if (isEmpty(parameters.get(key)))
				{
					isValid = false;
					break;
				}
			}
		}
		return isValid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see in.com.v2kart.core.payment.services.V2EncryptionService#getResponseHash(java.util.Map)
	 */
	@Override
	public String getResponseHash(final Map<String, String> parameters) throws V2Exception
	{
		throw new UnsupportedOperationException();
	}

	private String generateHash(final Map<String, String> parameters) throws V2Exception
	{
		final StringBuilder request = new StringBuilder();
		for (final String key : parameters.keySet())
		{
			request.append(key).append(CCAVENUE.KEY_VALUE_PAIR_TOKEN).append(parameters.get(key)).append(CCAVENUE.ATTRIBUTE_TOKEN);
		}
		return aesCryptUtil.encrypt(request.toString());
	}

	/**
	 * Checks if is empty.
	 * 
	 * @param value
	 *           the value
	 * @return true, if is empty
	 */
	private boolean isEmpty(final String value)
	{
		if (value == null || value.trim().equals(""))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

}
