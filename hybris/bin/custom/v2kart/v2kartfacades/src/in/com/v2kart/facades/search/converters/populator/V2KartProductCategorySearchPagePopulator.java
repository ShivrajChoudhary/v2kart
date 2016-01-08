/**
 * 
 */
package in.com.v2kart.facades.search.converters.populator;

import de.hybris.platform.commercefacades.search.converters.populator.ProductCategorySearchPagePopulator;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;

/**
 * This class override the product Category Search Page Populator and set Stats Info List
 * 
 * @author Nagarro-Dev
 * 
 */
public class V2KartProductCategorySearchPagePopulator extends ProductCategorySearchPagePopulator {

    @Override
    public void populate(final ProductCategorySearchPageData source, final ProductCategorySearchPageData target) {
        super.populate(source, target);
        target.setStatsInfoList(source.getStatsInfoList());
    }
}
