package in.com.v2kart.core.search.solrfacetsearch.provider.impl;

import in.com.v2kart.core.model.V2kartStyleVariantProductModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.PromotionCodeValueProvider;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.promotions.model.ProductPromotionModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.variants.model.VariantProductModel;

public class V2PromotionCodeValueProvider extends PromotionCodeValueProvider {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.PromotionCodeValueProvider#createFieldValue(de.hybris.platform
     * .core.model.product.ProductModel, de.hybris.platform.solrfacetsearch.config.IndexConfig,
     * de.hybris.platform.solrfacetsearch.config.IndexedProperty)
     */
    @Override
    protected List<FieldValue> createFieldValue(ProductModel product, IndexConfig indexConfig, IndexedProperty indexedProperty) {
        List<FieldValue> fieldValues = new ArrayList<>(0);
        BaseSiteModel baseSiteModel = indexConfig.getBaseSite();
        if (baseSiteModel != null && baseSiteModel.getDefaultPromotionGroup() != null)
        {
            Iterator<ProductPromotionModel> styleIterator = getPromotionsService().getProductPromotions(
                    Collections.singletonList(baseSiteModel.getDefaultPromotionGroup()), product).iterator();
            if (styleIterator.hasNext())
            {
                // get promotions from style variant product
                ProductPromotionModel promotion = (ProductPromotionModel) styleIterator.next();
                addFieldValues(fieldValues, indexedProperty, null, promotion.getCode());
            } else {
                // get promotions from size variant products
                if (product instanceof V2kartStyleVariantProductModel) {
                    final List<VariantProductModel> sizeVariants = (List<VariantProductModel>) product.getVariants();
                    for (VariantProductModel sizeVariant : sizeVariants) {
                        Iterator<ProductPromotionModel> sizeIterator = getPromotionsService().getProductPromotions(
                                Collections.singletonList(baseSiteModel.getDefaultPromotionGroup()), sizeVariant).iterator();
                        if (sizeIterator.hasNext()) {
                            ProductPromotionModel promotion = (ProductPromotionModel) sizeIterator.next();
                            addFieldValues(fieldValues, indexedProperty, null, promotion.getCode());
                        }
                    }
                }

            }
        }
        return fieldValues;
    }

}
