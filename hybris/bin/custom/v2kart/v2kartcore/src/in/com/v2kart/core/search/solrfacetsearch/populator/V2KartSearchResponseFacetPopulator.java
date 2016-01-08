package in.com.v2kart.core.search.solrfacetsearch.populator;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchResponseFacetsPopulator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.TopValuesProvider;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.search.Facet;
import de.hybris.platform.solrfacetsearch.search.FacetValue;
import de.hybris.platform.solrfacetsearch.search.SearchResult;


public class V2KartSearchResponseFacetPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, INDEXED_TYPE_SORT_TYPE, ITEM> extends SearchResponseFacetsPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, INDEXED_TYPE_SORT_TYPE, ITEM>{

	private String categoryFacetName;
	private String baseCategoryName;
	
	@Resource(name = "commerceCategoryService")
	private CommerceCategoryService commerceCategoryService;
  
	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

    /* (non-Javadoc)
     * @see de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchResponseFacetsPopulator#buildFacetValues(de.hybris.platform.commerceservices.search.facetdata.FacetData, de.hybris.platform.solrfacetsearch.search.Facet, de.hybris.platform.solrfacetsearch.config.IndexedProperty, de.hybris.platform.solrfacetsearch.search.SearchResult, de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData)
     */
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected void buildFacetValues(FacetData<SolrSearchQueryData> facetData, Facet facet, IndexedProperty indexedProperty,
            SearchResult solrSearchResult, SolrSearchQueryData searchQueryData){
    	
		boolean showCustomCategory = configurationService.getConfiguration().getBoolean("v2kartcore.solr.facet.category", Boolean.TRUE);

		if (showCustomCategory && getCategoryFacetName().equalsIgnoreCase(facet.getName())) {
			buildCategoryFacetValues(facetData, facet, indexedProperty,
					solrSearchResult, searchQueryData);
		} else {
			buildNonCategoryFacetValues(facetData, facet, indexedProperty,
					solrSearchResult, searchQueryData);
		}
		
    }

	private void buildCategoryFacetValues(
			FacetData<SolrSearchQueryData> facetData, Facet facet,
			IndexedProperty indexedProperty, SearchResult solrSearchResult,
			SolrSearchQueryData searchQueryData) {
		
		List<String> subCategoriesCodes = new ArrayList<String>();
		List<CategoryModel> directSubCategories = new ArrayList<CategoryModel>();
		
		//get category code from facet 
		List<SolrSearchQueryTermData> filterTerms= searchQueryData.getFilterTerms();
		String categoryFilter = null;
		if(CollectionUtils.isNotEmpty(filterTerms)){
			for(SolrSearchQueryTermData solrSearchQueryTermData : filterTerms){
				if(getCategoryFacetName().equals(solrSearchQueryTermData.getKey())){
					categoryFilter = solrSearchQueryTermData.getValue();
//					break;
				}
			}
		}
		
		String category = null;
		if(StringUtils.isNotEmpty(categoryFilter) && subCategoriesCodes.isEmpty()){
			category = categoryFilter;
		}
		else if (searchQueryData.getCategoryCode() != null && subCategoriesCodes.isEmpty())
		{
			category = searchQueryData.getCategoryCode();
		} else{
			category = getBaseCategoryName();
		}
		
		CategoryModel filterCategoryModel = null;
		
		try {
			filterCategoryModel = commerceCategoryService.getCategoryForCode(category);
		} catch (Exception ex){
			filterCategoryModel = null;
		}
		
		if(filterCategoryModel == null) {
			filterCategoryModel = commerceCategoryService.getCategoryForCode(getBaseCategoryName());
		}
		
		directSubCategories = filterCategoryModel.getCategories();

		subCategoriesCodes = new ArrayList<String>(directSubCategories.size());
		for (final CategoryModel categoryModel : directSubCategories)
		{
			subCategoriesCodes.add(categoryModel.getCode());
		}

		List<FacetValue> facetValues = facet.getFacetValues();
		if ((facetValues == null) || (facetValues.isEmpty()))
		{
			return;
		}
		
		List<FacetValue> categoryFacetValuesData = new ArrayList<>();
		for (final FacetValue facetValue : facetValues) {
			if (subCategoriesCodes.contains(facetValue.getName())) {
				categoryFacetValuesData.add(facetValue);
			}
		}
		facetValues = categoryFacetValuesData;
		
		final List<FacetValueData<SolrSearchQueryData>> allFacetValues = new ArrayList<>(facetValues.size());
		for (final FacetValue facetValue : facetValues)
		{
			FacetValueData facetValueData  = buildFacetValue(facetData, facet, facetValue, solrSearchResult, searchQueryData);
			if (facetValueData == null)
			{
				continue;
			}
			allFacetValues.add(facetValueData);
		}

		facetData.setValues(allFacetValues);
		
		
		final TopValuesProvider topValuesProvider = getTopValuesProvider(indexedProperty);
		if ((isRanged(indexedProperty)) || (topValuesProvider == null))
		{
			return;
		}
		
		final List<FacetValue> topFacetValues = topValuesProvider.getTopValues(indexedProperty, facetValues);
		if (topFacetValues == null)
		{
			return;
		}
		final List topFacetValuesData = new ArrayList();
		for (final FacetValue facetValue : topFacetValues)
		{
			FacetValueData topFacetValueData = buildFacetValue(facetData, facet, facetValue, solrSearchResult, searchQueryData);
			if(topFacetValueData == null){
				continue;
			}
			topFacetValuesData.add(topFacetValueData);
		}

		facetData.setTopValues(topFacetValuesData);
	}

	private void buildNonCategoryFacetValues(
			FacetData<SolrSearchQueryData> facetData, Facet facet,
			IndexedProperty indexedProperty, SearchResult solrSearchResult,
			SolrSearchQueryData searchQueryData) {
		List<FacetValue> facetValues = facet.getFacetValues();
        List<FacetValue> facetRefineValue= new ArrayList(facetValues.size()); 
        if ((facetValues == null) || (facetValues.isEmpty()))
            return;
        List allFacetValues = new ArrayList(
                facetValues.size());

        for (FacetValue facetValue : facetValues)
        {
            FacetValueData facetValueData = buildFacetValue(facetData, facet, facetValue,
                    solrSearchResult, searchQueryData);
            if (facetValueData == null)
                continue;
            allFacetValues.add(facetValueData);
        
            facetRefineValue.add(facetValue);
         }

        facetData.setValues(allFacetValues);

        TopValuesProvider topValuesProvider = getTopValuesProvider(indexedProperty);
        if ((isRanged(indexedProperty)) || (topValuesProvider == null))
            return;
        
        List<FacetValue> topFacetValues = topValuesProvider.getTopValues(indexedProperty, facetRefineValue);
        if (topFacetValues == null)
            return;
        List topFacetValuesData = new ArrayList();
        for (FacetValue facetValue : topFacetValues)
        {
            FacetValueData topFacetValueData = buildFacetValue(facetData, facet, facetValue,
                    solrSearchResult, searchQueryData);
            if (topFacetValueData == null)
                continue;
            topFacetValuesData.add(topFacetValueData);
        }

        facetData.setTopValues(topFacetValuesData);
	}
	
	
	protected FacetValueData<SolrSearchQueryData> buildFacetValue(final FacetData<SolrSearchQueryData> facetData,
			final Facet facet,
			final FacetValue facetValue, final SearchResult solrSearchResult, final SolrSearchQueryData searchQueryData,
			final List<String> subCategoriesCodes)
	{
		if (subCategoriesCodes.contains(facetValue.getName()))
		{
			return super.buildFacetValue(facetData, facet, facetValue, solrSearchResult, searchQueryData);
		}
		else
		{
			return null;
		}

	}
	
	
	public String getCategoryFacetName() {
		return categoryFacetName;
	}

	public void setCategoryFacetName(String categoryFacetName) {
		this.categoryFacetName = categoryFacetName;
	}

	public String getBaseCategoryName() {
		return baseCategoryName;
	}

	public void setBaseCategoryName(String baseCategoryName) {
		this.baseCategoryName = baseCategoryName;
	}

    
}
