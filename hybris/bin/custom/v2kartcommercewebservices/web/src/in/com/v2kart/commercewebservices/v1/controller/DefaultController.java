/**
 *
 */
package in.com.v2kart.commercewebservices.v1.controller;

import in.com.v2kart.commercewebservices.exceptions.UnknownResourceException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class DefaultController
{
	@RequestMapping
	public void defaultRequest(final HttpServletRequest request)
	{
		throw new UnknownResourceException("There is no resource for path " + request.getRequestURI());
	}
}
