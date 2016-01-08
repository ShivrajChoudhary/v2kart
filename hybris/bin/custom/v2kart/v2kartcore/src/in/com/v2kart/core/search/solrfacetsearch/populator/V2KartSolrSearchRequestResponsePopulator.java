/**
 * 
 */
package in.com.v2kart.core.search.solrfacetsearch.populator;

import in.com.v2kart.core.search.solrfacetsearch.impl.StatsInfo;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.apache.solr.client.solrj.response.QueryResponse;

import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SolrSearchRequestResponsePopulator;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import de.hybris.platform.solrfacetsearch.search.impl.SolrSearchResult;

/**
 * This class override the Solr Search Request Response Populator and set Stats Info List
 * 
 * @author Nagarro-Dev
 * 
 */
public class V2KartSolrSearchRequestResponsePopulator extends SolrSearchRequestResponsePopulator {

    @Override
    public void populate(SolrSearchRequest source, SolrSearchResponse target) {
        super.populate(source, target);

        populateStatsInfoList(target);
    }

    /**
     * This method is used to set Stats Info List into Solr Search Response
     * @param target
     */
    private void populateStatsInfoList(SolrSearchResponse target) {
        SearchResult searchResult = (SearchResult) target.getSearchResult();
        if (searchResult instanceof SolrSearchResult) {
            QueryResponse queryResponse = ((SolrSearchResult) searchResult).getQueryResponse();

            if (queryResponse.getFieldStatsInfo() != null) {
                List<StatsInfo> statsInfoList = new ArrayList<StatsInfo>();
                for (String fieldName : queryResponse.getFieldStatsInfo().keySet()) {
                    FieldStatsInfo value = queryResponse.getFieldStatsInfo().get(fieldName);
                    StatsInfo statsInfo = new StatsInfo();
                    statsInfo.setFieldName(fieldName);
                    statsInfo.setMax(value.getMax());
                    statsInfo.setMin(value.getMin());
                    statsInfoList.add(statsInfo);
                }

                target.setStatsInfoList(statsInfoList);
            }
        }
    }
}
