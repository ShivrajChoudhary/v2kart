package in.com.v2kart.core.search.solrfacetsearch;

import org.apache.solr.client.solrj.SolrQuery;

import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SolrQueryPostProcessor;

/**
 * 
 * Solr query post processor to apply filters on solr query
 * 
 * @author vikrant2480
 * 
 */
public class V2SolrQueryPostProcessor implements SolrQueryPostProcessor {

    @Override
    public SolrQuery process(final SolrQuery query, final SearchQuery solrSearchQuery) {
        StringBuilder customFilterQueryBuilder = new StringBuilder("");

        // to filter only those products which have price information for all size variants. If any one(or all) size variant doesn't have
        // price information it is filtered out
        if (!(solrSearchQuery.getFacetSearchConfig().getName().equalsIgnoreCase("v2kartCsIndex"))) {
            customFilterQueryBuilder.append("displayProductWithPrices_boolean:true");
            query.addFilterQuery(customFilterQueryBuilder.toString());
        }
        customFilterQueryBuilder = new StringBuilder("");

        // to filter only those products which have stocks for any one of the size variant products. If all size variant products have
        // OUTOFSTOCK status, they are filtered out
        /*customFilterQueryBuilder.append("availability_boolean:true");
        query.addFilterQuery(customFilterQueryBuilder.toString());*/
        return query;
    }

}
