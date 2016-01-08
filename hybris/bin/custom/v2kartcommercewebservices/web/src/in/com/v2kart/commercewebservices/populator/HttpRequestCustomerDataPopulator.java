/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
package in.com.v2kart.commercewebservices.populator;

import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


/**
 * Populates {@link CustomerData} instance based on http request parameters:<br>
 * Does not populate address {@link AddressData} information
 * <ul>
 * <li>titleCode</li>
 * <li>firstName</li>
 * <li>lastName</li>
 * <li>language</li>
 * <li>currency</li>
 * </ul>
 *
 */
@Component("httpRequestCustomerDataPopulator")
public class HttpRequestCustomerDataPopulator extends AbstractPopulatingConverter<HttpServletRequest, CustomerData>
{

	private static final String TITLECODE = "titleCode";
	private static final String FIRSTNAME = "firstName";
	private static final String LASTNAME = "lastName";
	private static final String LANGUAGE = "language";
	private static final String CURRENCY = "currency";
	private static final String MOBILE_NUMBER = "mobileNumber";
	private static final String DATE_OF_BIRTH = "dateOfBirth";
	private static final String GENDER = "gender";
	private static final String MARITAL_STATUS = "maritalStatus";


	@Override
	protected CustomerData createTarget()
	{
		return new CustomerData();
	}

	@Override
	public void populate(final HttpServletRequest source, final CustomerData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setTitleCode(StringUtils.defaultString(source.getParameter(TITLECODE), target.getTitleCode()));
		target.setFirstName(StringUtils.defaultString(source.getParameter(FIRSTNAME), target.getFirstName()));
		target.setLastName(StringUtils.defaultString(source.getParameter(LASTNAME), target.getLastName()));
		target.setMobileNumber(StringUtils.defaultString(source.getParameter(MOBILE_NUMBER), target.getMobileNumber()));
		target.setGender(StringUtils.defaultString(source.getParameter(GENDER), target.getGender()));
		target.setMaritalStatus(StringUtils.defaultString(source.getParameter(MARITAL_STATUS), target.getMaritalStatus()));

		if (source.getParameter(DATE_OF_BIRTH) != null)
		{
			target.setDateOfBirth(converToDate(source.getParameter(DATE_OF_BIRTH)));
		}
		if (source.getParameter(CURRENCY) != null)
		{
			final CurrencyData currency = new CurrencyData();
			currency.setIsocode(source.getParameter(CURRENCY));
			target.setCurrency(currency);
		}

		if (source.getParameter(LANGUAGE) != null)
		{
			final LanguageData language = new LanguageData();
			language.setIsocode(source.getParameter(LANGUAGE));
			target.setLanguage(language);
		}

	}

	public Date converToDate(final String dateInString)
	{
		final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		try
		{

			final Date date = formatter.parse(dateInString);
			System.out.println(date);
			System.out.println(formatter.format(date));
			return date;
		}
		catch (final ParseException e)
		{
			return null;
		}
	}

}
