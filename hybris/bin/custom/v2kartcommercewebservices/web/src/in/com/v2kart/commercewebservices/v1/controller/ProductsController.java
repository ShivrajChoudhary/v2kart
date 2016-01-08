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
 *
 *
 */
package in.com.v2kart.commercewebservices.v1.controller;

import de.hybris.platform.acceleratorcms.model.components.CMSTabParagraphComponentModel;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.catalog.CatalogFacade;
import de.hybris.platform.commercefacades.product.ProductExportFacade;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.commercefacades.product.data.ProductReferencesData;
import de.hybris.platform.commercefacades.product.data.ProductResultData;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.product.data.SuggestionData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.AutocompleteSuggestionData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commercefacades.storefinder.StoreFinderStockFacade;
import de.hybris.platform.commercefacades.storefinder.data.StoreFinderStockSearchPageData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.StockSystemException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;
import de.hybris.platform.variants.model.VariantProductModel;

import in.com.v2kart.commercewebservices.color.V2HexCodeData;
import in.com.v2kart.commercewebservices.color.V2HexCodeDataList;
import in.com.v2kart.commercewebservices.common.StatusData;
import in.com.v2kart.commercewebservices.component.product.data.CodAndServiceableProductResponse;
import in.com.v2kart.commercewebservices.constants.YcommercewebservicesConstants;
import in.com.v2kart.commercewebservices.formatters.WsDateFormatter;
import in.com.v2kart.commercewebservices.product.data.ProductAndReferenceData;
import in.com.v2kart.commercewebservices.product.data.ProductDataList;
import in.com.v2kart.commercewebservices.product.data.SuggestionDataList;
import in.com.v2kart.commercewebservices.queues.data.ProductExpressUpdateElementData;
import in.com.v2kart.commercewebservices.queues.data.ProductExpressUpdateElementDataList;
import in.com.v2kart.commercewebservices.queues.impl.ProductExpressUpdateQueue;
import in.com.v2kart.commercewebservices.stock.CommerceStockFacade;
import in.com.v2kart.commercewebservices.util.ws.SearchQueryCodec;
import in.com.v2kart.commercewebservices.validator.PointOfServiceValidator;
import in.com.v2kart.core.dao.V2HexCodesDao;
import in.com.v2kart.core.email.V2HtmlMailSender;
import in.com.v2kart.core.model.V2HexCodesModel;
import in.com.v2kart.core.model.V2kartSizeVariantProductModel;
import in.com.v2kart.core.model.V2kartStyleVariantProductModel;
import in.com.v2kart.core.process.email.context.V2CustomerNotificationEmailContext;
import in.com.v2kart.core.serviceability.V2ServiceabilityService;
import in.com.v2kart.facades.cart.impl.DefaultV2CartFacade;
import in.com.v2kart.facades.core.data.V2CustomerNotificationData;
import in.com.v2kart.facades.wishlist.WishlistFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;


/**
 * Web Services Controller to expose the functionality of the {@link ProductFacade} and SearchFacade.
 */
@Controller("productsControllerV1")
@RequestMapping(value = "/{baseSiteId}/products")
public class ProductsController extends BaseController
{
	private static final String BASIC_OPTION = "BASIC";
	private static final String MAX_INTEGER = "2147483647";
	private static final String DEFAULT_PAGE_VALUE = "0";
	private static final int CATALOG_ID_POS = 0;
	private static final int CATALOG_VERSION_POS = 1;
	private static final Logger LOG = Logger.getLogger(ProductsController.class);
	@Resource
	StoreFinderStockFacade storeFinderStockFacade;
	@Resource(name = "cwsProductFacade")
	private ProductFacade productFacade;
	@Resource(name = "cwsProductExportFacade")
	private ProductExportFacade productExportFacade;
	@Resource(name = "cwsSearchQueryCodec")
	private SearchQueryCodec<SolrSearchQueryData> searchQueryCodec;
	@Resource(name = "wsDateFormatter")
	private WsDateFormatter wsDateFormatter;
	@Resource(name = "productSearchFacade")
	private ProductSearchFacade<ProductData> productSearchFacade;
	@Resource(name = "solrSearchStateConverter")
	private Converter<SolrSearchQueryData, SearchStateData> solrSearchStateConverter;
	@Resource(name = "httpRequestReviewDataPopulator")
	private Populator<HttpServletRequest, ReviewData> httpRequestReviewDataPopulator;
	@Resource(name = "reviewValidator")
	private Validator reviewValidator;
	@Resource(name = "productExpressUpdateQueue")
	private ProductExpressUpdateQueue productExpressUpdateQueue;
	@Resource(name = "catalogFacade")
	private CatalogFacade catalogFacade;
	@Resource(name = "commerceStockFacade")
	private CommerceStockFacade commerceStockFacade;
	@Resource(name = "pointOfServiceValidator")
	private PointOfServiceValidator pointOfServiceValidator;
	@Resource(name = "userService")
	private UserService userService;
	@Resource(name = "sessionService")
	private SessionService sessionService;
	@Resource(name = "htmlMailSender")
	private V2HtmlMailSender v2HtmlMailSender;

	@Resource(name = "httpRequestNotifyCustomerDataPopulator")
	private Populator<HttpServletRequest, V2CustomerNotificationData> httpRequestNotifyCustomerDataPopulator;

	@Resource(name = "customerNotificationValidator")
	private Validator customerNotificationValidator;

	@Resource(name = "wishlistFacade")
	private WishlistFacade wishlistFacade;

	@Resource(name = "commerceWebServicesCartFacade")
	private DefaultV2CartFacade cartFacade;

	@Resource(name = "cmsComponentService")
	private CMSComponentService cmsComponentService;

	@Resource(name = "serviceabilityService")
	private V2ServiceabilityService v2ServiceabilityService;

	@Resource(name = "cmsSiteService")
	private CMSSiteService cmsSiteService;

	@Autowired
	private V2HexCodesDao hexCodesDao;

	private String websiteUrl;

	private static final String ANONYMOUS_USER = "Anonymous user";

	private static final String EMAIL_A_FRIEND_BODY = "Email_A_Friend_Body";

	@Resource(name = "productService")
	private ProductService productService;

	protected ProductService getProductService()
	{
		return productService;
	}

	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * Web service handler for search. Implementation has to catch up once the SearchFacade exists.
	 *
	 * @param query
	 *           serialized query in format: freeTextSearch:sort:facetKey1:facetValue1:facetKey2:facetValue2
	 * @param currentPage
	 *           the current result page requested
	 * @param pageSize
	 *           the number of results returned per page
	 * @param sort
	 *           sorting method applied to the display search results
	 * @return {@link FacetSearchPageData}
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ProductSearchPageData<SearchStateData, ProductData> searchProducts(@RequestParam(required = false) final String query,
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_VALUE) final int currentPage,
			@RequestParam(required = false, defaultValue = "20") final int pageSize,
			@RequestParam(required = false) final String sort)
	{
		final SolrSearchQueryData searchQueryData = searchQueryCodec.decodeQuery(query);
		final PageableData pageable = new PageableData();
		pageable.setCurrentPage(currentPage);
		pageable.setPageSize(pageSize);
		pageable.setSort(sort);

		return productSearchFacade.textSearch(solrSearchStateConverter.convert(searchQueryData), pageable);
	}



	@ResponseBody
	@RequestMapping(value = "/getHexCodesList", method = RequestMethod.GET)
	public V2HexCodeDataList getHexCodesList()
	{
		final V2HexCodeDataList dataList = new V2HexCodeDataList();
		final List<V2HexCodesModel> hexCodes = hexCodesDao.findHexCodeForColor();


		final List<V2HexCodeData> hexCodeList = new ArrayList<V2HexCodeData>();

		for (final V2HexCodesModel model : hexCodes)
		{
			final V2HexCodeData hexCodeData = new V2HexCodeData();
			hexCodeData.setColor(model.getColor());
			hexCodeData.setHexCode(model.getHexCode());
			//			dataList.getHexCodes().add(hexCodeData);
			hexCodeList.add(hexCodeData);
		}
		dataList.setHexCodes(hexCodeList);
		return dataList;
	}

	/**
	 * Web service handler for the product serviceable call.
	 *
	 * @param pinCode
	 * @return Serviceable Product List
	 */

	@ResponseBody
	@RequestMapping(value = "/getServiceableCheck/{pinCode}", method = RequestMethod.GET)
	public CodAndServiceableProductResponse isCartServiceable(@PathVariable final String pinCode)
	{
		final CodAndServiceableProductResponse codAndServiceableResponse = new CodAndServiceableProductResponse();
		codAndServiceableResponse.setIsServiceable(v2ServiceabilityService.isProductServicableForPinCode(pinCode));
		codAndServiceableResponse.setIsCodAvailable(v2ServiceabilityService.isCodAvailableForArea(pinCode));
		codAndServiceableResponse.setPinCode(pinCode);
		return codAndServiceableResponse;
	}

	/**
	 * Web service handler for getting stock level in a given store.<br/>
	 * Sample Call: http://localhost:9001/rest/v1/:site/products/:code/stock?storeName=
	 *
	 * @param storeName
	 *           name of the store
	 * @return {@link StockData}
	 * @throws WebserviceValidationException
	 * @throws StockSystemException
	 */
	@RequestMapping(value = "/{productCode}/stock", method = RequestMethod.GET)
	@ResponseBody
	public StockData getStockData(@PathVariable final String baseSiteId, @PathVariable final String productCode,
			@RequestParam(required = true) final String storeName) throws WebserviceValidationException, StockSystemException
	{
		final Errors errors = new BeanPropertyBindingResult(storeName, "storeName");
		pointOfServiceValidator.validate(storeName, errors);
		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}

		if (!commerceStockFacade.isStockSystemEnabled(baseSiteId))
		{
			throw new StockSystemException("Stock system is not enabled on this site", StockSystemException.NOT_ENABLED, baseSiteId);
		}
		return commerceStockFacade.getStockDataForProductAndPointOfService(productCode, storeName);
	}

	/**
	 * Web service handler for product export. If no 'options' query parameter is defined, it will assume BASIC. The
	 * options are turned into a Set<ProductOption> and passed on to the facade. <br>
	 * Sample Call: http://localhost:9001/rest/v1/{SITE}/products/export/full
	 *
	 * @param currentPage
	 *           - index position of the first Product, which will be included in the returned List
	 * @param pageSize
	 *           - number of Products which will be returned in each page
	 * @param options
	 *           - a String enumerating the detail level, values are BASIC, PROMOTIONS, STOCK, REVIEW, CLASSIFICATION,
	 *           REFERENCES. Combine by using a ',', which needs to be encoded as part of a URI using URLEncoding: %2C
	 * @return {@link ProductDataList}
	 */
	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(value = "/export/full", method = RequestMethod.GET)
	@ResponseBody
	public ProductDataList exportProducts(
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_VALUE) final int currentPage,
			@RequestParam(required = false, defaultValue = MAX_INTEGER) final int pageSize,
			@RequestParam(required = false, defaultValue = BASIC_OPTION) final String options,
			@RequestParam(required = false) final String catalog, @RequestParam(required = false) final String version)
	{
		final Set<ProductOption> opts = extractOptions(options);

		final ProductResultData products = productExportFacade.getAllProductsForOptions(catalog, version, opts, currentPage,
				pageSize);

		// addUrlsToResult(catalog, version, products);
		final ProductDataList result = convertResultset(currentPage, pageSize, catalog, version, products);

		return result;
	}

	/**
	 * Web service handler for incremental product export. Timestamp specifies which product to export. If no 'options'
	 * query parameter is defined, it will assume BASIC. The options are turned into a Set<ProductOption> and passed on
	 * to the facade. <br>
	 * Sample Call: http://localhost:9001/rest/v1/{SITE}/products/export/incremental
	 *
	 * @param currentPage
	 *           - index position of the first Product, which will be included in the returned List
	 * @param pageSize
	 *           - number of Products which will be returned in each page
	 * @param options
	 *           - a String enumerating the detail level, values are BASIC, PROMOTIONS, STOCK, REVIEW, CLASSIFICATION,
	 *           REFERENCES. Combine by using a ',', which needs to be encoded as part of a URI using URLEncoding: %2C
	 * @param catalog
	 *           catalog from which get products
	 * @param version
	 *           version of catalog
	 * @param timestamp
	 *           time in ISO-8601 format
	 * @return {@link ProductDataList}
	 */
	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(value = "/export/incremental", method = RequestMethod.GET)
	@ResponseBody
	public ProductDataList exportProducts(
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_VALUE) final int currentPage,
			@RequestParam(required = false, defaultValue = MAX_INTEGER) final int pageSize,
			@RequestParam(required = false, defaultValue = BASIC_OPTION) final String options,
			@RequestParam(required = false) final String catalog, @RequestParam(required = false) final String version,
			@RequestParam final String timestamp)
	{
		final Set<ProductOption> opts = extractOptions(options);

		final Date timestampDate;
		try
		{
			timestampDate = wsDateFormatter.toDate(timestamp);
		}
		catch (final IllegalArgumentException e)
		{
			throw new RequestParameterException("Wrong time format. The only accepted format is ISO-8601.",
					RequestParameterException.INVALID, "timestamp", e);
		}

		final ProductResultData modifiedProducts = productExportFacade.getOnlyModifiedProductsForOptions(catalog, version,
				timestampDate, opts, currentPage, pageSize);

		return convertResultset(currentPage, pageSize, catalog, version, modifiedProducts);
	}

	/**
	 * Web service handler for export product references. Reference type specifies which references to return. If no
	 * 'options' query parameter is defined, it will assume BASIC. The options are turned into a Set<ProductOption> and
	 * passed on to the facade. Sample Call:
	 * http://localhost:9001/rest/v1/{SITE}/products/export/references/{code}?referenceType
	 * =CROSSELLING&catalog=hwcatalog&version=Online
	 *
	 * @param code
	 *           - product code
	 * @param referenceType
	 *           - reference type according to enum ProductReferenceTypeEnum
	 * @param pageSize
	 *           - number of Products which will be returned in each page
	 * @param options
	 *           - a String enumerating the detail level, values are BASIC, PROMOTIONS, STOCK, REVIEW, CLASSIFICATION,
	 *           REFERENCES. Combine by using a ',', which needs to be encoded as part of a URI using URLEncoding: %2C
	 * @return collection of {@link ProductReferenceData}
	 */
	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(value = "/export/references/{code}", method = RequestMethod.GET)
	@ResponseBody
	public ProductReferencesData exportProductReferences(@PathVariable final String code,
			@RequestParam(required = false, defaultValue = MAX_INTEGER) final int pageSize,
			@RequestParam(required = false, defaultValue = BASIC_OPTION) final String options,
			@RequestParam final String referenceType)
	{
		final List<ProductOption> opts = Lists.newArrayList(extractOptions(options));
		final ProductReferenceTypeEnum referenceTypeEnum = ProductReferenceTypeEnum.valueOf(referenceType);

		final List<ProductReferenceData> productReferences = productFacade.getProductReferencesForCode(code, referenceTypeEnum,
				opts, Integer.valueOf(pageSize));

		final ProductReferencesData productReferencesData = new ProductReferencesData();
		productReferencesData.setReferences(productReferences);

		return productReferencesData;
	}

	/**
	 * Web service handler for product express update. Returns only elements newer than timestamp. Sample Call:
	 * http://localhost:9001/rest/v1/{SITE}/products/expressUpdate<br>
	 * This method requires trusted client authentication.<br>
	 * Method type : <code>GET</code>.<br>
	 * Method is restricted for <code>HTTPS</code> channel.
	 *
	 * @param timestamp
	 *           - time in ISO-8601 format
	 * @param catalog
	 *           - the product catalog to return queue for. If not set all products from all catalogs in queue will be
	 *           returned. Format: catalogId:catalogVersion
	 * @return {@link ProductExpressUpdateElementDataList}
	 */
	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(value = "/expressUpdate", method = RequestMethod.GET)
	@ResponseBody
	public ProductExpressUpdateElementDataList expressUpdate(@RequestParam final String timestamp,
			@RequestParam(required = false) final String catalog) throws RequestParameterException
	{
		final Date timestampDate;
		try
		{
			timestampDate = wsDateFormatter.toDate(timestamp);
		}
		catch (final IllegalArgumentException e)
		{
			throw new RequestParameterException("Wrong time format. The only accepted format is ISO-8601.",
					RequestParameterException.INVALID, "timestamp", e);
		}

		final ProductExpressUpdateElementDataList productExpressUpdateDataList = new ProductExpressUpdateElementDataList();
		productExpressUpdateDataList.setProductExpressUpdateElements(productExpressUpdateQueue.getItems(timestampDate));
		filterExpressUpdateQueue(productExpressUpdateDataList, validateAndSplitCatalog(catalog));
		return productExpressUpdateDataList;
	}

	private void filterExpressUpdateQueue(final ProductExpressUpdateElementDataList productExpressUpdateDataList,
			final List<String> catalogInfo)
	{
		if (catalogInfo.size() == 2 && StringUtils.isNotEmpty(catalogInfo.get(CATALOG_ID_POS))
				&& StringUtils.isNotEmpty(catalogInfo.get(CATALOG_VERSION_POS))
				&& CollectionUtils.isNotEmpty(productExpressUpdateDataList.getProductExpressUpdateElements()))
		{
			final Iterator<ProductExpressUpdateElementData> dataIterator = productExpressUpdateDataList
					.getProductExpressUpdateElements().iterator();
			while (dataIterator.hasNext())
			{
				final ProductExpressUpdateElementData productExpressUpdateElementData = dataIterator.next();
				if (!catalogInfo.get(CATALOG_ID_POS).equals(productExpressUpdateElementData.getCatalogId())
						|| !catalogInfo.get(CATALOG_VERSION_POS).equals(productExpressUpdateElementData.getCatalogVersion()))
				{
					dataIterator.remove();
				}
			}
		}
	}

	protected List<String> validateAndSplitCatalog(final String catalog) throws RequestParameterException
	{
		final List<String> catalogInfo = new ArrayList<>();
		if (StringUtils.isNotEmpty(catalog))
		{
			catalogInfo.addAll(Lists.newArrayList(Splitter.on(':').trimResults().omitEmptyStrings().split(catalog)));
			if (catalogInfo.size() == 2)
			{
				catalogFacade.getProductCatalogVersionForTheCurrentSite(catalogInfo.get(CATALOG_ID_POS),
						catalogInfo.get(CATALOG_VERSION_POS), Collections.EMPTY_SET);
			}
			else if (!catalogInfo.isEmpty())
			{
				throw new RequestParameterException("Invalid format. You have to provide catalog as 'catalogId:catalogVersion'",
						RequestParameterException.INVALID, "catalog");
			}
		}
		return catalogInfo;
	}

	private ProductDataList convertResultset(final int page, final int pageSize, final String catalog, final String version,
			final ProductResultData modifiedProducts)
	{
		final ProductDataList result = new ProductDataList();
		result.setProducts(modifiedProducts.getProducts());
		if (pageSize > 0)
		{
			result.setTotalPageCount((modifiedProducts.getTotalCount() % pageSize == 0) ? modifiedProducts.getTotalCount()
					/ pageSize : modifiedProducts.getTotalCount() / pageSize + 1);
		}
		result.setCurrentPage(page);
		result.setTotalProductCount(modifiedProducts.getTotalCount());
		result.setCatalog(catalog);
		result.setVersion(version);
		return result;
	}

	protected Set<ProductOption> extractOptions(final String options)
	{
		final String optionsStrings[] = options.split(YcommercewebservicesConstants.OPTIONS_SEPARATOR);

		final Set<ProductOption> opts = new HashSet<ProductOption>();
		for (final String option : optionsStrings)
		{
			opts.add(ProductOption.valueOf(option));
		}
		return opts;
	}

	/**
	 * Web service handler for the getProductByCode call. If no 'options' query parameter is defined, it will assume
	 * BASIC. The options are turned into a Set<ProductOption> and passed on to the facade. Sample Call:
	 * http://localhost:9001/rest/v1/{SITE}/products/{CODE}?options=BASIC%2CPROMOTIONS Keep in mind ',' needs to be
	 * encoded as %2C
	 *
	 * @param code
	 *           - the unique code used to identify a product
	 * @param options
	 *           - a String enumerating the detail level, values are BASIC, PROMOTIONS, STOCK, REVIEW, CLASSIFICATION,
	 *           REFERENCES. Combine by using a ',', which needs to be encoded as part of a URI using URLEncoding: %2C
	 * @return the ProdcutData DTO which will be marshaled to JSON or XML based on Accept-Header
	 */
	@RequestMapping(value = "/{code}", method = RequestMethod.GET)
	@ResponseBody
	public ProductAndReferenceData getProductByCode(@PathVariable final String code,
			@RequestParam(required = false, defaultValue = BASIC_OPTION) final String options)
	{
		ProductData productData = new ProductData();
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getProductByCode: code=" + code + " | options=" + options);
		}

		final Set<ProductOption> opts = extractOptions(options);
		ProductModel productModel = getProductService().getProductForCode(code);

		// if size variant product doesn't have sizes, redirect style variant product to size variant product.
		if (productModel instanceof V2kartStyleVariantProductModel)
		{
			final List<VariantProductModel> sizeVariants = new ArrayList<VariantProductModel>(productModel.getVariants());
			if (CollectionUtils.isNotEmpty(sizeVariants) && sizeVariants.size() == 1)
			{
				final ProductModel sizeVariantProduct = sizeVariants.get(0);
				if (sizeVariantProduct instanceof V2kartSizeVariantProductModel)
				{
					final String size = ((V2kartSizeVariantProductModel) sizeVariantProduct).getSize();
					if (size.equalsIgnoreCase("NA") || size.equalsIgnoreCase("N/A") || size.equalsIgnoreCase("A"))
					{
						productModel = sizeVariantProduct;
					}
				}
			}
		}

		productData = productFacade.getProductForOptions(productModel, opts);
		final List<ProductReferenceData> productReferences = productFacade.getProductReferencesForCode(code,
				Arrays.asList(ProductReferenceTypeEnum.SIMILAR, ProductReferenceTypeEnum.ACCESSORIES),
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE), null);
		// to set the shipping details
		try
		{
			final CMSTabParagraphComponentModel deliveryInfoComponent = (CMSTabParagraphComponentModel) cmsComponentService
					.getSimpleCMSComponent("shippingTab");

			productData.setShippingDetails(deliveryInfoComponent.getContent());
			productData.setShippingDetails(productData.getShippingDetails().replaceAll("<", "&lt;"));
			productData.setShippingDetails(productData.getShippingDetails().replaceAll(">", "&gt;"));
			productData.setShippingDetails(productData.getShippingDetails().replaceAll("\"", "'"));


		}
		catch (final CMSItemNotFoundException e)
		{
			// YTODO Auto-generated catch block
			e.printStackTrace();
		}
		final ProductAndReferenceData productAndReferenceData = new ProductAndReferenceData();
		productAndReferenceData.setProductData(productData);
		productAndReferenceData.setProductReferences(productReferences);
		return productAndReferenceData;
	}

	/**
	 * Web service handler for giving the auto complete suggestions as List<String>
	 *
	 * @param term
	 *           - the term that user inputs for search
	 * @param max
	 *           - the limit of the suggestions
	 * @return the list of auto suggestions
	 */
	@RequestMapping(value = "/suggest", method = RequestMethod.GET)
	@ResponseBody
	public SuggestionDataList getSuggestions(@RequestParam(required = true, defaultValue = " ") final String term,
			@RequestParam(required = true, defaultValue = "10") final int max)
	{
		final List<SuggestionData> suggestions = new ArrayList<SuggestionData>();
		final List<AutocompleteSuggestionData> autoSuggestions;
		if (max < productSearchFacade.getAutocompleteSuggestions(term).size())
		{
			autoSuggestions = productSearchFacade.getAutocompleteSuggestions(term).subList(0, max);
		}
		else
		{
			autoSuggestions = productSearchFacade.getAutocompleteSuggestions(term);
		}
		for (final AutocompleteSuggestionData autoSuggestion : autoSuggestions)
		{
			final SuggestionData suggestionData = new SuggestionData();
			suggestionData.setValue(autoSuggestion.getTerm());
			suggestions.add(suggestionData);
		}
		final SuggestionDataList suggestionDataList = new SuggestionDataList();
		suggestionDataList.setSuggestions(suggestions);
		return suggestionDataList;
	}

	/**
	 * Web service handler for the postReview call. Review will be posted as anonymous principal. Method uses
	 * {@link in.com.v2kart.populator.HttpRequestReviewDataPopulator} to populate review data from request parameters.
	 * <p/>
	 * There is no default validation for the posted value!
	 * <p/>
	 * Request Method:
	 * <code>POST<code> Sample Call: http://localhost:9001/rest/v1/{SITE}/products/{CODE}/review Request
	 * parameters:
	 * <ul>
	 * <li>rating (required)</li>
	 * <li>headline</li>
	 * <li>comment</li>
	 * <li>alias</li>
	 * </ul>
	 *
	 * @param code
	 *           - the unique code used to identify a product
	 * @param request
	 * @return the ReviewData DTO which will be marshaled to JSON or XML based on Accept-Header
	 */
	@RequestMapping(value = "/{code}/reviews", method = RequestMethod.POST)
	@ResponseBody
	public ReviewData createReview(@PathVariable final String code, final HttpServletRequest request)
			throws WebserviceValidationException
	{
		final ReviewData reviewData = new ReviewData();
		httpRequestReviewDataPopulator.populate(request, reviewData);
		final Errors errors = new BeanPropertyBindingResult(reviewData, "reviewData");

		reviewValidator.validate(reviewData, errors);

		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}

		return productFacade.postReview(code, reviewData);
	}

	/**
	 * Web service handler for searching product's stock level sorted by distance from specific location passed by the
	 * free-text parameter. Sample Call: http://localhost:9001/rest/v1/{SITE}/products/{CODE}/nearLocation
	 *
	 * @param code
	 *           - the unique code used to identify a product * - the unique code used to identify a product
	 * @param latitude
	 *           - location's latitude
	 * @param longitude
	 *           - location's longitude
	 * @param query
	 *           - free-text location
	 * @return the StoreFinderStockSearchPageData of ProductData objects sorted by distance from location ascending
	 */
	@RequestMapping(value = "/{code}/nearLocation", method = RequestMethod.GET)
	@ResponseBody
	public StoreFinderStockSearchPageData<ProductData> searchProductStockByLocation(@PathVariable final String code,
			@RequestParam(required = false) final String query, @RequestParam(required = false) final Double latitude,
			@RequestParam(required = false) final Double longitude,
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_VALUE) final int currentPage,
			@RequestParam(required = false, defaultValue = MAX_INTEGER) final int pageSize)
	{
		if (StringUtils.isNotBlank(query))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("getProductStockByLocation: code=" + code + " | location=" + query);
			}

			final Set<ProductOption> opts = extractOptions(BASIC_OPTION);

			return this.storeFinderStockFacade.productSearch(query, productFacade.getProductForCodeAndOptions(code, opts),
					createPageableData(currentPage, pageSize));
		}
		else if (latitude != null && longitude != null)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("getProductStockByLocationGeoCode: code=" + code + " | latitude=" + latitude + " | longitude=" + longitude);
			}
			final Set<ProductOption> opts = extractOptions(BASIC_OPTION);

			return this.storeFinderStockFacade.productSearch(createGeoPoint(latitude, longitude),
					productFacade.getProductForCodeAndOptions(code, opts), createPageableData(currentPage, pageSize));
		}
		else
		{
			final Set<ProductOption> opts = extractOptions(BASIC_OPTION);
			return this.storeFinderStockFacade.productSearch("", productFacade.getProductForCodeAndOptions(code, opts),
					createPageableData(currentPage, pageSize));
		}

	}

	/**
	 * Web service handler for searching product's stock level sorted by distance from specific location passed by the
	 * free-text parameter. Sample Call: http://localhost:9001/rest/v1/{SITE}/products/{CODE}/nearLatLong
	 *
	 * @param code
	 *           - the unique code used to identify a product
	 * @param latitude
	 *           - location's latitude
	 * @param longitude
	 *           - location's longitude
	 * @return the StoreFinderStockSearchPageData of ProductData objects sorted by distance from location ascending
	 */
	@RequestMapping(value = "/{code}/nearLatLong", method = RequestMethod.GET)
	@ResponseBody
	public StoreFinderStockSearchPageData<ProductData> searchProductStockByLocationGeoCode(@PathVariable final String code,
			@RequestParam(required = true) final Double latitude, @RequestParam(required = true) final Double longitude,
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_VALUE) final int currentPage,
			@RequestParam(required = false, defaultValue = MAX_INTEGER) final int pageSize)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getProductStockByLocationGeoCode: code=" + code + " | latitude=" + latitude + " | longitude=" + longitude);
		}
		final Set<ProductOption> opts = extractOptions(BASIC_OPTION);

		return this.storeFinderStockFacade.productSearch(createGeoPoint(latitude, longitude),
				productFacade.getProductForCodeAndOptions(code, opts), createPageableData(currentPage, pageSize));
	}

	private PageableData createPageableData(final int currentPage, final int pageSize)
	{
		final PageableData pageable = new PageableData();

		pageable.setCurrentPage(currentPage);
		pageable.setPageSize(pageSize);
		return pageable;
	}

	private GeoPoint createGeoPoint(final Double latitude, final Double longitude)
	{
		final GeoPoint point = new GeoPoint();
		point.setLatitude(latitude.doubleValue());
		point.setLongitude(longitude.doubleValue());

		return point;
	}

	/**
	 * Web service for adding items to wishlist.<br>
	 * This method requires authentication.<br>
	 * Method type : <code>GET</code>.<br>
	 * Method is restricted for <code>HTTPS</code> channel.
	 *
	 * @return cart data with applied voucher.
	 * @throws CommerceCartModificationException
	 */

	@RequestMapping(value = "/add-to-wishlist", method = RequestMethod.GET)
	@Secured("ROLE_CUSTOMERGROUP")
	@ResponseBody
	public StatusData addWishlistItem(@RequestParam(value = "productCode", required = true) final String productCode,
			@RequestParam(value = "entryNumber", required = false) final String entryNumber)
			throws CommerceCartModificationException
	{
		final StatusData statusData = new StatusData();
		statusData.setStatus(Boolean.FALSE);

		if (StringUtils.isNotBlank(productCode))
		{
			wishlistFacade.addWishlistEntry(productCode);
			statusData.setStatus(Boolean.TRUE);
			statusData.setMessage(getMessage("wishlist.add.product.from.pdp"));
		}
		if (!(null == entryNumber))
		{
			final long l = Long.parseLong(entryNumber);
			cartFacade.updateCartEntry(l, 0);
			statusData.setMessage(getMessage("wishlist.add.product.from.cart"));
		}

		return statusData;
	}

	@RequestMapping(value = "/emailAFriend", method = RequestMethod.POST)
	public @ResponseBody StatusData notifyCustomereMail(final HttpServletRequest request,
			@RequestParam(value = "productCode", required = true) final String productCode)
	{
		final V2CustomerNotificationData notificationData = new V2CustomerNotificationData();
		httpRequestNotifyCustomerDataPopulator.populate(request, notificationData);
		final Errors errors = new BeanPropertyBindingResult(notificationData, "notificationData");
		customerNotificationValidator.validate(notificationData, errors);

		websiteUrl = Config.getParameter("website." + cmsSiteService.getCurrentSite().getUid() + ".https");

		notificationData.setSite(cmsSiteService.getCurrentSite().getUid());

		final ProductModel product = productService.getProductForCode(productCode);
		notificationData.setName(product.getName());

		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}

		if (notificationData.getMediaUrl() != null && notificationData.getMediaUrl().contains("http"))
		{
			notificationData.setMediaUrl(notificationData.getMediaUrl());
		}
		else if (StringUtils.isNotEmpty(notificationData.getMediaUrl()))
		{
			notificationData.setMediaUrl(websiteUrl.concat(notificationData.getMediaUrl()));
		}
		else
		{
			// if product has no image
			notificationData.setMediaUrl("");
		}
		final String productPrice = Double.toString(notificationData.getNotificationPrice());
		notificationData.setProductPrice(productPrice);
		final String currentUserEmailId = notificationData.getCurrentUserEmailId();
		if (currentUserEmailId == null)
		{
			notificationData.setCurrentUserEmailId(ANONYMOUS_USER);
			notificationData.setCurrentUserName("User");
		}
		else
		{
			notificationData.setCurrentUserEmailId(notificationData.getCurrentUserEmailId());
			final UserModel user = userService.getUserForUID(notificationData.getCurrentUserEmailId());
			notificationData.setCurrentUserName(user.getName());
		}
		final StatusData statusData = new StatusData();
		statusData.setStatus(Boolean.TRUE);
		try
		{
			sendMail(EMAIL_A_FRIEND_BODY, notificationData, notificationData.getName());
		}
		catch (final Exception ex)
		{
			LOG.info("Failed to send email a friend mail.", ex);
			errors.reject("emailSendError", "emailSendError");
			statusData.setStatus(Boolean.FALSE);
		}
		return statusData;
	}

	private void sendMail(final String templateCode, final V2CustomerNotificationData notificationData, final String mailSubject)
			throws EmailException
	{

		final UserModel saleUser = userService.getAdminUser();
		// Execute each request in separate thread
		sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				try
				{

					/*
					 * final CMSSiteModel cmsSiteModel = (CMSSiteModel)baseSiteService.getCurrentBaseSite();
					 * 
					 * final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion(cmsSiteModel
					 * .getDefaultCatalog().getId(), catalogVersionName);
					 * catalogVersionService.setSessionCatalogVersions(Collections.singletonList(catalogVersionModel));
					 * baseSiteService.setCurrentBaseSite(cmsSiteModel, true);
					 * i18nService.setCurrentLocale(commonI18NService.
					 * getLocaleForLanguage(cmsSiteModel.getDefaultLanguage())); final CMSSiteModel cmsSite = (CMSSiteModel)
					 */
					final VelocityContext notifyCustomerEmailContext = new V2CustomerNotificationEmailContext(notificationData);
					final String to = notificationData.getEmailId();
					v2HtmlMailSender.sendEmail(v2HtmlMailSender.createHtmlEmail(notifyCustomerEmailContext, templateCode, mailSubject,
							to));

				}
				catch (final Exception ex)
				{
					LOG.error("Error on sending email :" + ex.getMessage());

				}
				return saleUser;
			}
		}, saleUser);

	}

	/*
	 * @RequestMapping(value = "/reference/{code}/", method = RequestMethod.GET)
	 * 
	 * @ResponseBody public ProductData getProductReferences(@PathVariable final String code) { final
	 * List<ProductReferenceData> productReferences = productFacade.getProductReferencesForCode(code,
	 * Arrays.asList(ProductReferenceTypeEnum.SIMILAR, ProductReferenceTypeEnum.ACCESSORIES),
	 * Arrays.asList(ProductOption.BASIC, ProductOption.PRICE), null); }
	 */
}
