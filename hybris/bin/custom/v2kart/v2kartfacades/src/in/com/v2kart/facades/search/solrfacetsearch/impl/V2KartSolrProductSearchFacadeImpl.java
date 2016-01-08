package in.com.v2kart.facades.search.solrfacetsearch.impl;

import in.com.v2kart.facades.search.V2KartProductSearchFacade;

import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.solrfacetsearch.impl.DefaultSolrProductSearchFacade;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.solrfacetsearch.model.config.SolrValueRangeModel;

/**
 * This class contains method for text searching product data.
 * 
 * @author Nagarro-Dev
 * 
 */
public class V2KartSolrProductSearchFacadeImpl<ITEM extends ProductData> extends DefaultSolrProductSearchFacade<ProductData> implements
        V2KartProductSearchFacade<ITEM> {

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Override
    public String getSolrPriceRange(String searchQuery) {
        String priceRange = null;
        String[] values = searchQuery.split("-");
        if (values != null && values.length <= 2) {
            values = searchQuery.split("price:");
            if (values != null && values.length > 1) {
                SolrValueRangeModel solrValueRange = new SolrValueRangeModel();
                int endIndex = values[1].indexOf(":");
                if (endIndex > 0) {
                    solrValueRange.setName(values[1].substring(0, endIndex).trim());
                } else {
                    solrValueRange.setName(values[1].trim());
                }
                solrValueRange = flexibleSearchService.getModelByExample(solrValueRange);
                if (solrValueRange != null) {
                    priceRange = solrValueRange.getFrom() + " TO "
                            + solrValueRange.getTo();
                }
            }
        }
        return priceRange;
    }
}
