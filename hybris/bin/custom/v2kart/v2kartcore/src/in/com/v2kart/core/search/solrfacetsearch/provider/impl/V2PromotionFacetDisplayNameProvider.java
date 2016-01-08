package in.com.v2kart.core.search.solrfacetsearch.provider.impl;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.acceleratorservices.promotions.dao.PromotionsDao;
import de.hybris.platform.acceleratorservices.search.solrfacetsearch.provider.impl.PromotionFacetDisplayNameProvider;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

public class V2PromotionFacetDisplayNameProvider extends PromotionFacetDisplayNameProvider {
    private PromotionsDao promotionsDao;

    @Override
    public String getDisplayName(final SearchQuery query, final IndexedProperty property, final String facetValue) {
        final AbstractPromotionModel promotion = promotionsDao.getPromotionForCode(facetValue);
        if (promotion != null) {
            if (null == promotion.getName()) {
                return promotion.getTitle();
            }
            return promotion.getName();
        }
        return facetValue;
    }

    @Override
    protected PromotionsDao getPromotionsDao() {
        return promotionsDao;
    }

    @Override
    @Required
    public void setPromotionsDao(final PromotionsDao promotionsDao) {
        this.promotionsDao = promotionsDao;
    }
}
