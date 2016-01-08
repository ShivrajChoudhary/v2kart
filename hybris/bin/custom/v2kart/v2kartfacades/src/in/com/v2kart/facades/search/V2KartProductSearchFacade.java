
package in.com.v2kart.facades.search;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;

/**
 * FF Product search facade interface.
 * 
 *
 * @param <ITEM> The type of the product result items
 */
public interface V2KartProductSearchFacade<ITEM extends ProductData> extends ProductSearchFacade<ProductData>
{
    public String getSolrPriceRange(String searchQuery);
}
