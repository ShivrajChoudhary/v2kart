/**
 * 
 */
package in.com.v2kart.core.search.solrfacetsearch.strategies;

import in.com.v2kart.core.constants.V2kartCoreConstants;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.exceptions.NoValidSolrConfigException;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.impl.DefaultSolrFacetSearchConfigSelectionStrategy;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;

/**
 * Custom Solr Facet selection strategy created to load data from indexes other than default index used
 * for storefront. By default Facet Search Config is linked to base site and a base site can have only
 * one config attached to it. This strategy picks a variant index specific to a site based on index name
 * 
 * @author shubhammaheshwari
 * 
 */
public class V2SolrFacetSearchConfigSelectionStrategy extends DefaultSolrFacetSearchConfigSelectionStrategy {

    @Override
    public SolrFacetSearchConfigModel getCurrentSolrFacetSearchConfig() throws NoValidSolrConfigException {
        SolrFacetSearchConfigModel result = null;
        final String baseSiteName = getCurrentBaseSiteName();
        /*
         * Each site can have its own variant index. So we follow a convention to create index name as <site name>_VariantIndex
         */
        if (baseSiteName != null) {
            result = getFacetSearchConfigDao().findSolrFacetSearchConfigByName(baseSiteName + V2kartCoreConstants.VARIANT_INDEX_SUFFIX);
        }

        if (result == null) {
            result = super.getCurrentSolrFacetSearchConfig();
        }

        return result;
    }

    protected String getCurrentBaseSiteName() {
        final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();
        if (currentBaseSite != null) {
            return currentBaseSite.getUid();
        }
        return null;
    }
}
