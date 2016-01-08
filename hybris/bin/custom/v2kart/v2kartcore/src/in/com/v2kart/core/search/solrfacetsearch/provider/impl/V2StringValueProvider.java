/**
 * 
 */
package in.com.v2kart.core.search.solrfacetsearch.provider.impl;

import java.util.ArrayList;
import java.util.Collection;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.impl.SpELValueProvider;
import de.hybris.platform.variants.model.VariantProductModel;

/**
 * @author shubhammaheshwari
 * V2StringValueProvider created to index the property of the base product (StyleVariant) for CSCockpit
 */
public class V2StringValueProvider extends SpELValueProvider {

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.hybris.platform.solrfacetsearch.provider.FieldValueProvider#getFieldValues(de.hybris.platform.solrfacetsearch.config.IndexConfig,
     * de.hybris.platform.solrfacetsearch.config.IndexedProperty, java.lang.Object)
     */
    @Override
    public Collection<FieldValue> getFieldValues(IndexConfig indexConfig, IndexedProperty indexedProperty, Object model)
            throws FieldValueProviderException {
        Collection<FieldValue> fieldValues = new ArrayList<FieldValue>();
        if (null != model && model instanceof ProductModel) {
            VariantProductModel variantProductModel = (VariantProductModel) model;
            ProductModel baseProduct = variantProductModel.getBaseProduct();
            if (null != baseProduct) {
                fieldValues = super.getFieldValues(indexConfig, indexedProperty, baseProduct);
            }
        } else {
            throw new FieldValueProviderException("Cannot evalute size of non product item");
        }
        return fieldValues;
    }
}
