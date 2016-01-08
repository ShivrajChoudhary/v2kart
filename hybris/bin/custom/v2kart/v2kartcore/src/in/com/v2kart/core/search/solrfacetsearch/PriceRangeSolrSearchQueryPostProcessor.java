package in.com.v2kart.core.search.solrfacetsearch;

import org.apache.solr.client.solrj.SolrQuery;

import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SolrQueryPostProcessor;

/**
 * This class is the Solr search Query Post processor class for handling priceValue and filter price queries
 * 
 * @author Nagarro-Dev
 * 
 */
public class PriceRangeSolrSearchQueryPostProcessor implements SolrQueryPostProcessor {

    public static String priceRange = null;

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.solrfacetsearch.search.SolrQueryPostProcessor#process(org.apache.solr.client.solrj.SolrQuery,
     * de.hybris.platform.solrfacetsearch.search.SearchQuery)
     */
    @Override
    public SolrQuery process(SolrQuery query, SearchQuery paramSearchQuery) {
        query.setGetFieldStatistics(true);
        query.setGetFieldStatistics("priceValue_inr_double");
        filterPriceQuery(query);
        return query;
    }

    /**
     * This method is used to update the price filter and add a new query for price
     * 
     * @param query
     *        SolrQuery
     */
    private void filterPriceQuery(SolrQuery query) {
        final String[] filters = query.getFilterQueries();
        if (filters != null) {
            int index = 0;
            // look for required filter
            for (final String filter : filters) {
                if (filter.contains("price")) {
                    // Example : Convert "(wholeWeightInGrams_double:2\-5)" to
                    if(filter.contains("priceRange")){
                        
                    } else {
                    String newfilter = filter.replace(":", ":[");
                    newfilter = newfilter.replace("\\", "");
                    newfilter = newfilter.replace("-", " TO ");
                    newfilter = newfilter.replace(")", "])");
                    filters[index] = newfilter;
                    break;
                    }
                }
                index++;
            }
        }
    }

}
