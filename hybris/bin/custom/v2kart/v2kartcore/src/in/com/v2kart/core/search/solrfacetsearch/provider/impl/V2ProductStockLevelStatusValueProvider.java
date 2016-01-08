package in.com.v2kart.core.search.solrfacetsearch.provider.impl;

import in.com.v2kart.core.model.V2kartStyleVariantProductModel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.ProductStockLevelStatusValueProvider;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.variants.model.VariantProductModel;

/**
 * Field value provider to check stock status of product
 * 
 * @author vikrant2480
 * 
 */
public class V2ProductStockLevelStatusValueProvider extends ProductStockLevelStatusValueProvider {

    private static final long serialVersionUID = 1L;

    @Override
    protected List<FieldValue> createFieldValue(ProductModel product, BaseStoreModel baseStore, IndexedProperty indexedProperty) {
        final List<FieldValue> fieldValues = new ArrayList<>();

        if (baseStore != null) {
            if (product instanceof V2kartStyleVariantProductModel) {
                final V2kartStyleVariantProductModel styleVariantProduct = (V2kartStyleVariantProductModel) product;
                final List<VariantProductModel> sizeVariants = new ArrayList<VariantProductModel>(styleVariantProduct.getVariants());

                // if style variant doesn't have size variants, check stock status at style variant
                if (CollectionUtils.isEmpty(sizeVariants)) {
                    final StockLevelStatus stockLevelStatus = getProductStockLevelStatus(styleVariantProduct, baseStore);
                    if (stockLevelStatus != null && !stockLevelStatus.equals(StockLevelStatus.OUTOFSTOCK)) {
                        addFieldValues(fieldValues, indexedProperty, Boolean.TRUE);
                    }
                } else {
                    // if style variant has size variants, check stock status at size variants
                    // If all size variants has stock level status as OUTOFSTOCK, set the indexed property TRUE to filter those products at
                    // solr query post processor
                    boolean isOutOfStockVariants = true;
                    for (VariantProductModel sizeVariant : sizeVariants) {
                        final StockLevelStatus stockLevelStatus = getProductStockLevelStatus(sizeVariant, baseStore);
                        if (stockLevelStatus != null && stockLevelStatus.equals(StockLevelStatus.OUTOFSTOCK)) {
                            isOutOfStockVariants = isOutOfStockVariants && true;
                        } else {
                            isOutOfStockVariants = false;
                        }
                    }

                    // if at least one size product has stock level status not equal to OUTOFSTOCK, set indexed property to TRUE
                    if (Boolean.FALSE.equals(isOutOfStockVariants)) {
                        addFieldValues(fieldValues, indexedProperty, Boolean.TRUE);
                    }
                }
            }
        } else {
            addFieldValues(fieldValues, indexedProperty, Boolean.TRUE);
        }

        return fieldValues;
    }
}
