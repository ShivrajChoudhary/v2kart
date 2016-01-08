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
package in.com.v2kart.storefront.controllers.pages;

import in.com.v2kart.core.excel.FilteredProductImage;
import in.com.v2kart.storefront.controllers.ControllerConstants;
import in.com.v2kart.storefront.controllers.pages.AbstractSearchPageController.SearchResultsData;
import in.com.v2kart.storefront.util.MetaSanitizerUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.hybris.platform.acceleratorcms.model.components.SearchBoxComponentModel;
import de.hybris.platform.acceleratorservices.controllers.page.PageType;
import de.hybris.platform.acceleratorservices.customer.CustomerLocationService;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.impl.SearchBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.util.XSSFilterUtil;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.AutocompleteResultData;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetRefinement;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;

@Controller
@Scope("tenant")
@RequestMapping("/search")
public class SearchPageController extends AbstractSearchPageController {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(SearchPageController.class);

    private static final String COMPONENT_UID_PATH_VARIABLE_PATTERN = "{componentUid:.*}";

    private static final String SEARCH_CMS_PAGE_ID = "search";
    private static final String NO_RESULTS_CMS_PAGE_ID = "searchEmpty";

    @Resource(name = "productSearchFacade")
    private ProductSearchFacade<ProductData> productSearchFacade;

    @Resource(name = "searchBreadcrumbBuilder")
    private SearchBreadcrumbBuilder searchBreadcrumbBuilder;

    @Resource(name = "customerLocationService")
    private CustomerLocationService customerLocationService;

    @Resource(name = "cmsComponentService")
    private CMSComponentService cmsComponentService;

    @Resource(name="filteredProductImage")
    private FilteredProductImage filteredProductImage;
	
   
    @RequestMapping(method = RequestMethod.GET, params = "!q")
    public String textSearch(@RequestParam(value = "text", defaultValue = "") final String searchText,
            @RequestParam(value = "min", required = false) String min,
            @RequestParam(value = "max", required = false) String max,
            final HttpServletRequest request,
            final Model model) throws CMSItemNotFoundException {
    	model.addAttribute("searchTextValue", searchText);
        if (StringUtils.isNotBlank(searchText)) {
            final PageableData pageableData = createPageableData(0, getSearchPageSize(), null, ShowMode.Page);

            final SearchStateData searchState = new SearchStateData();
            final SearchQueryData searchQueryData = new SearchQueryData();
            searchQueryData.setValue(XSSFilterUtil.filter(searchText));
            searchState.setQuery(searchQueryData);
    
            //filter all product galleryImage
        //    final ProductSearchPageData<SearchStateData, ProductData> searchPageData=filteredProductImage.filterAllProductMedia(productSearchFacade.textSearch(searchState,pageableData));
            final ProductSearchPageData<SearchStateData, ProductData> searchPageData = productSearchFacade.textSearch(searchState,
                    pageableData);
            populatePriceRange(searchPageData, model, priceRange);
            if (min == null && max == null) {
                Map<String, Object> map = model.asMap();
                min = String.valueOf(map.get("categoryMin"));
                max = String.valueOf(map.get("categoryMax"));
            }
            model.addAttribute("min", min);
            model.addAttribute("max", max);
            model.addAttribute("searchMode", "true");
            if (searchPageData == null) {
                storeCmsPageInModel(model, getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID));
            } else if (searchPageData.getKeywordRedirectUrl() != null) {
                // if the search engine returns a redirect, just
                return "redirect:" + searchPageData.getKeywordRedirectUrl();
            } else if (searchPageData.getPagination().getTotalNumberOfResults() == 0) {
                model.addAttribute("searchPageData", searchPageData);
                storeCmsPageInModel(model, getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID));
                updatePageTitle(searchText, model);
            } else {
                storeContinueUrl(request);
                populateModel(model, searchPageData, ShowMode.Page);
                storeCmsPageInModel(model, getContentPageForLabelOrId(SEARCH_CMS_PAGE_ID));
                updatePageTitle(searchText, model);
            }
            model.addAttribute("userLocation", customerLocationService.getUserLocation());
            getRequestContextData(request).setSearch(searchPageData);
            if (searchPageData != null) {
                model.addAttribute(WebConstants.BREADCRUMBS_KEY,
                        searchBreadcrumbBuilder.getBreadcrumbs(null, searchText, CollectionUtils.isEmpty(searchPageData.getBreadcrumbs())));
            }
        } else {
            storeCmsPageInModel(model, getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID));
        }
        model.addAttribute("pageType", PageType.PRODUCTSEARCH.name());
        model.addAttribute("metaRobots", "noindex,follow");

        final String metaDescription = MetaSanitizerUtil.sanitizeDescription(getMessageSource().getMessage(
                "search.meta.description.results", null, getI18nService().getCurrentLocale())
                + " "
                + searchText
                + " "
                + getMessageSource().getMessage("search.meta.description.on", null, getI18nService().getCurrentLocale())
                + " "
                + getSiteName());
        final String metaKeywords = MetaSanitizerUtil.sanitizeKeywords(searchText);
        setUpMetaData(model, metaKeywords, metaDescription);

        return getViewForPage(model);
    }

    @RequestMapping(method = RequestMethod.GET, params = "q")
    public String refineSearch(@RequestParam("q") final String searchQuery,
            @RequestParam(value = "page", defaultValue = "0") final int page,
            @RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
            @RequestParam(value = "sort", required = false) final String sortCode,
            @RequestParam(value = "min", required = false) String min,
            @RequestParam(value = "max", required = false) String max,
            @RequestParam(value = "text", required = false) final String searchText, final HttpServletRequest request, final Model model)
            throws CMSItemNotFoundException {
        final ProductSearchPageData<SearchStateData, ProductData> searchPageData = performSearch(searchQuery, page, showMode, sortCode,
                getSearchPageSize());
        populatePriceRange(searchPageData, model, priceRange);
        if (min == null && max == null && sortCode == null) {
            Map<String, Object> map = model.asMap();
            min = String.valueOf(map.get("categoryMin"));
            max = String.valueOf(map.get("categoryMax"));
        }
        model.addAttribute("min", min);
        model.addAttribute("max", max);
        model.addAttribute("searchMode", "true");
        populateModel(model, searchPageData, showMode);
        model.addAttribute("userLocation", customerLocationService.getUserLocation());

        if (searchPageData.getPagination().getTotalNumberOfResults() == 0) {
            updatePageTitle(searchPageData.getFreeTextSearch(), model);
            storeCmsPageInModel(model, getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID));
        } else {
            storeContinueUrl(request);
            storeCmsPageInModel(model, getContentPageForLabelOrId(SEARCH_CMS_PAGE_ID));
            updatePageTitle(searchPageData.getFreeTextSearch(), model);
        }
        model.addAttribute(WebConstants.BREADCRUMBS_KEY, searchBreadcrumbBuilder.getBreadcrumbs(null, searchPageData));
        model.addAttribute("pageType", PageType.PRODUCTSEARCH.name());

        final String metaDescription = MetaSanitizerUtil.sanitizeDescription(getMessageSource().getMessage(
                "search.meta.description.results", null, getI18nService().getCurrentLocale())
                + " "
                + searchText
                + " "
                + getMessageSource().getMessage("search.meta.description.on", null, getI18nService().getCurrentLocale())
                + " "
                + getSiteName());
        final String metaKeywords = MetaSanitizerUtil.sanitizeKeywords(searchText);
        setUpMetaData(model, metaKeywords, metaDescription);

        return getViewForPage(model);
    }

    protected ProductSearchPageData<SearchStateData, ProductData> performSearch(final String searchQuery, final int page,
            final ShowMode showMode, final String sortCode, final int pageSize) {
        final PageableData pageableData = createPageableData(page, pageSize, sortCode, showMode);

        final SearchStateData searchState = new SearchStateData();
        final SearchQueryData searchQueryData = new SearchQueryData();
        searchQueryData.setValue(searchQuery);
        searchState.setQuery(searchQueryData);
//filter all product galeryImage
  //      return filteredProductImage.filterAllProductMedia(productSearchFacade.textSearch(searchState, pageableData));
        return productSearchFacade.textSearch(searchState, pageableData);
    }

    // @ResponseBody
    @RequestMapping(value = "/results", method = RequestMethod.GET)
    public String jsonSearchResults(@RequestParam("q") final String searchQuery,
            @RequestParam(value = "page", defaultValue = "0") final int page,
            @RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
            @RequestParam(value = "sort", required = false) final String sortCode, final Model model) throws CMSItemNotFoundException {
        final ProductSearchPageData<SearchStateData, ProductData> searchPageData = performSearch(searchQuery, page, showMode, sortCode,
                getSearchPageSize());
        final SearchResultsData<ProductData> searchResultsData = new SearchResultsData<>();
        searchResultsData.setResults(searchPageData.getResults());
        searchResultsData.setPagination(searchPageData.getPagination());
        model.addAttribute("searchPageData", searchResultsData);

        return ControllerConstants.Views.Fragments.Product.ProductLister;
    }
 
    @ResponseBody
    @RequestMapping(value = "/mobile/results", method = RequestMethod.GET)
    public SearchResultsData<ProductData> jsonSearchResultsForMobile(@RequestParam("q") final String searchQuery,
    		@RequestParam(value = "page", defaultValue = "0") final int page,
    		@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
    		@RequestParam(value = "sort", required = false) final String sortCode, final Model model) throws CMSItemNotFoundException {
    	final ProductSearchPageData<SearchStateData, ProductData> searchPageData = performSearch(searchQuery, page, showMode, sortCode,
    			getSearchPageSize());
    	final SearchResultsData<ProductData> searchResultsData = new SearchResultsData<>();
    	searchResultsData.setResults(searchPageData.getResults());
    	searchResultsData.setPagination(searchPageData.getPagination());
    	return searchResultsData;
    }

    @ResponseBody
    @RequestMapping(value = "/facets", method = RequestMethod.GET)
    public FacetRefinement<SearchStateData> getFacets(@RequestParam("q") final String searchQuery,
            @RequestParam(value = "page", defaultValue = "0") final int page,
            @RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
            @RequestParam(value = "sort", required = false) final String sortCode) throws CMSItemNotFoundException {
        final SearchStateData searchState = new SearchStateData();
        final SearchQueryData searchQueryData = new SearchQueryData();
        searchQueryData.setValue(searchQuery);
        searchState.setQuery(searchQueryData);
//filter all product with galleryImage
  //      final ProductSearchPageData<SearchStateData, ProductData> searchPageData = filteredProductImage.filterAllProductMedia(productSearchFacade.textSearch(searchState,
   //             createPageableData(page, getSearchPageSize(), sortCode, showMode)));
        final ProductSearchPageData<SearchStateData, ProductData> searchPageData = productSearchFacade.textSearch(searchState,
        		                createPageableData(page, getSearchPageSize(), sortCode, showMode));
        final List<FacetData<SearchStateData>> facets = refineFacets(searchPageData.getFacets(),
                convertBreadcrumbsToFacets(searchPageData.getBreadcrumbs()));
        final FacetRefinement<SearchStateData> refinement = new FacetRefinement<>();
        refinement.setFacets(facets);
        refinement.setCount(searchPageData.getPagination().getTotalNumberOfResults());
        refinement.setBreadcrumbs(searchPageData.getBreadcrumbs());
        return refinement;
    }

    @ResponseBody
    @RequestMapping(value = "/autocomplete/" + COMPONENT_UID_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
    public AutocompleteResultData getAutocompleteSuggestions(@PathVariable final String componentUid,
            @RequestParam("term") final String term) throws CMSItemNotFoundException {
        final AutocompleteResultData resultData = new AutocompleteResultData();

        final SearchBoxComponentModel component = (SearchBoxComponentModel) cmsComponentService.getSimpleCMSComponent(componentUid);

        if (component.isDisplaySuggestions()) {
            resultData.setSuggestions(subList(productSearchFacade.getAutocompleteSuggestions(term), component.getMaxSuggestions()));
        }

        if (component.isDisplayProducts()) {
            resultData.setProducts(subList(productSearchFacade.textSearch(term).getResults(), component.getMaxProducts()));
        }

        return resultData;
    }

    protected <E> List<E> subList(final List<E> list, final int maxElements) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        if (list.size() > maxElements) {
            return list.subList(0, maxElements);
        }

        return list;
    }

    protected void updatePageTitle(final String searchText, final Model model) {
        storeContentPageTitleInModel(
                model,
                getPageTitleResolver().resolveContentPageTitle(
                        getMessageSource().getMessage("search.meta.title", null, getI18nService().getCurrentLocale()) + " " + searchText));
    }

    @RequestMapping(value = "/populateFacetsAndProducts", method = RequestMethod.GET)
    public String populateFacetsAndProductsOnFacetSelection(
            @RequestParam(value = "text", defaultValue = "") final String searchText,
            @RequestParam("q") String searchQuery, @RequestParam(value = "page", defaultValue = "0") final int page,
            @RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
            @RequestParam(value = "sort", required = false) final String sortCode,
            @RequestParam(value = "min", required = false) String min,
            @RequestParam(value = "max", required = false) String max,
            @SuppressWarnings("unused") final HttpServletRequest request, final Model model) throws CMSItemNotFoundException {
        final ProductSearchPageData<SearchStateData, ProductData> searchPageData = performSearch(searchQuery, page, showMode, sortCode,
                getSearchPageSize());
        populatePriceRange(searchPageData, model, priceRange);
        if (min == null && max == null && sortCode == null) {
            Map<String, Object> map = model.asMap();
            min = String.valueOf(map.get("categoryMin"));
            max = String.valueOf(map.get("categoryMax"));
        }
        model.addAttribute("min", min);
        model.addAttribute("max", max);
        model.addAttribute("searchMode", "true");
        populateModel(model, searchPageData, showMode);
        model.addAttribute("searchPageData", searchPageData);
        storeCmsPageInModel(model, getContentPageForLabelOrId(SEARCH_CMS_PAGE_ID));
        model.addAttribute("pageType", PageType.PRODUCTSEARCH.name());
        return "/pages/category/populateFacetsAndProducts";
    }
}
