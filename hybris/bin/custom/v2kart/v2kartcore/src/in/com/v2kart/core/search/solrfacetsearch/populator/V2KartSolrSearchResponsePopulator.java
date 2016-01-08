/**
 * 
 */
package in.com.v2kart.core.search.solrfacetsearch.populator;

import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.converters.Populator;

/**
 * This class set Stats Info List in the solr search response
 * 
 * @author Nagarro-Dev
 * 
 */
public class V2KartSolrSearchResponsePopulator implements Populator<SolrSearchResponse, ProductCategorySearchPageData> {

    @Override
    public void populate(SolrSearchResponse source, ProductCategorySearchPageData target) {
        if (source.getStatsInfoList() != null && source.getStatsInfoList().size() > 0) {
            target.setStatsInfoList(source.getStatsInfoList());
        }
    }
}
