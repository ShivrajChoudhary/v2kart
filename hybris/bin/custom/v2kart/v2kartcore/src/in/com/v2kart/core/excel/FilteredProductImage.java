package in.com.v2kart.core.excel;

import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;

/**
 * @author Shivraj
 * 
 * For Filtering the product Gallery Image
 *
 */
public interface FilteredProductImage {
 
    ProductCategorySearchPageData<SearchStateData, ProductData, CategoryData> filterAllProductMedia( ProductCategorySearchPageData<SearchStateData, ProductData, CategoryData> allProduct);
    ProductSearchPageData<SearchStateData, ProductData> filterAllProductMedia(ProductSearchPageData<SearchStateData, ProductData> searchPageData);
}
