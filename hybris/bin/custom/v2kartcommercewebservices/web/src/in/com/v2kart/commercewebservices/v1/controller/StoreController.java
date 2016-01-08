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
package in.com.v2kart.commercewebservices.v1.controller;

import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.RegionDataList;

import in.com.v2kart.commercewebservices.order.data.CardTypeDataList;
import in.com.v2kart.commercewebservices.storesession.data.CurrencyDataList;
import in.com.v2kart.commercewebservices.storesession.data.LanguageDataList;
import in.com.v2kart.commercewebservices.user.data.CountryDataList;
import in.com.v2kart.commercewebservices.user.data.TitleDataList;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Main Controller for available data
 */
@Controller("storeControllerV1")
public class StoreController extends BaseController
{
	@Resource(name = "userFacade")
	private UserFacade userFacade;

	@Resource(name = "storeSessionFacade")
	private StoreSessionFacade storeSessionFacade;

	@Resource(name = "checkoutFacade")
	private CheckoutFacade checkoutFacade;

	@Resource(name = "i18NFacade")
	private I18NFacade i18NFacade;


	/**
	 * returns all languages defined for the base store.<br>
	 * Sample call : http://localhost:9001/rest/v1/{SITE}/languages<br>
	 * Request method : <code>GET</code>
	 * 
	 * @return list of languages
	 */
	@RequestMapping(value = "/{baseSiteId}/languages", method = RequestMethod.GET)
	@ResponseBody
	public LanguageDataList getLanguages()
	{
		final LanguageDataList languageDataList = new LanguageDataList();
		languageDataList.setLanguages(storeSessionFacade.getAllLanguages());
		return languageDataList;
	}

	/**
	 * returns all currencies defined for the current store.<br>
	 * Sample call : http://localhost:9001/rest/v1/{SITE}/currencies<br>
	 * Request method : <code>GET</code>
	 * 
	 * @return list of currencies
	 */
	@RequestMapping(value = "/{baseSiteId}/currencies", method = RequestMethod.GET)
	@ResponseBody
	public CurrencyDataList getCurrencies()
	{
		final CurrencyDataList currencyDataList = new CurrencyDataList();
		currencyDataList.setCurrencies(storeSessionFacade.getAllCurrencies());
		return currencyDataList;
	}

	/**
	 * returns all delivery countries defined for the curent store.<br>
	 * Sample call : http://localhost:9001/rest/v1/{SITE}/deliverycountries<br>
	 * Request method : <code>GET</code>
	 * 
	 * @return list of countries
	 */
	@RequestMapping(value = "/{baseSiteId}/deliverycountries", method = RequestMethod.GET)
	@ResponseBody
	public CountryDataList getDeliveryCountries()
	{
		final CountryDataList countryDataList = new CountryDataList();
		countryDataList.setCountries(checkoutFacade.getDeliveryCountries());
		return countryDataList;
	}

	/**
	 * returns all possible titles (code and name).<br>
	 * Sample call : http://localhost:9001/rest/v1/titles<br>
	 * Request method : <code>GET</code>
	 * 
	 * @return list of titles
	 */
	@RequestMapping(value = "/{baseSiteId}/titles", method = RequestMethod.GET)
	@ResponseBody
	public TitleDataList getTitles()
	{
		final TitleDataList titleDataList = new TitleDataList();
		titleDataList.setTitles(userFacade.getTitles());
		return titleDataList;
	}

	/**
	 * returns all possible card types.<br>
	 * Sample call : http://localhost:9001/rest/v1/cardtypes<br>
	 * Request method : <code>GET</code>
	 * 
	 * @return list of card types
	 */
	@RequestMapping(value = "/{baseSiteId}/cardtypes", method = RequestMethod.GET)
	@ResponseBody
	public CardTypeDataList getCardTypes()
	{
		final CardTypeDataList cardTypeDataList = new CardTypeDataList();
		cardTypeDataList.setCardTypes(checkoutFacade.getSupportedCardTypes());
		return cardTypeDataList;
	}



	/**
	 * returns region list defined for the country.<br>
	 * Sample call : http://localhost:9001/rest/v1/{SITE}/region/{countryIso}<br>
	 * Request method : <code>GET</code>
	 * 
	 * @return list of countries
	 */
	@RequestMapping(value = "/{baseSiteId}/region/{countryIso}", method = RequestMethod.GET)
	@ResponseBody
	public RegionDataList getRegion(@PathVariable final String countryIso)
	{
		final RegionDataList regionDataList = new RegionDataList();
		regionDataList.setRegion(i18NFacade.getRegionsForCountryIso(countryIso));
		return regionDataList;
	}
}
