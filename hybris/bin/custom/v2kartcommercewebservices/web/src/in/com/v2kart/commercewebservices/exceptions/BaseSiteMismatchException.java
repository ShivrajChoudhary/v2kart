/**
 * 
 */
package in.com.v2kart.commercewebservices.exceptions;

import javax.servlet.ServletException;


public class BaseSiteMismatchException extends ServletException
{
	public BaseSiteMismatchException(final String baseSiteIdInRequest, final String baseSiteIdInCart)
	{
		super("Base site '" + baseSiteIdInRequest + "' from the current request does not match with base site '" + baseSiteIdInCart
				+ "' from the cart!");
	}
}
