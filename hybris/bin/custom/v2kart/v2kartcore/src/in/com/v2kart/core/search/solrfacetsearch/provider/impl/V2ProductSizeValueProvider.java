/**
 * 
 */
package in.com.v2kart.core.search.solrfacetsearch.provider.impl;

import in.com.v2kart.core.model.V2kartSizeVariantProductModel;
import in.com.v2kart.core.model.V2kartStyleVariantProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;
import de.hybris.platform.variants.model.VariantProductModel;

/**
 * @author shubhammaheshwari
 * 
 */
public class V2ProductSizeValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider {

    private FieldNameProvider fieldNameProvider;

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
        final List<FieldValue> fieldValues = new ArrayList<FieldValue>();
        if (null != model && model instanceof V2kartStyleVariantProductModel) {
            V2kartStyleVariantProductModel v2kartStyleVariantProductModel = (V2kartStyleVariantProductModel) model;
            Collection<VariantProductModel> v2SizeVariantProducts = v2kartStyleVariantProductModel.getVariants();
            if(v2SizeVariantProducts.size() > 0){
                Iterator<VariantProductModel> variantProductIterator = v2SizeVariantProducts.iterator();
                while(variantProductIterator.hasNext()){
                    V2kartSizeVariantProductModel v2KartSizeVariantProductModel = (V2kartSizeVariantProductModel)variantProductIterator.next();
                    String value = v2KartSizeVariantProductModel.getSize();
                    if (null != value && !value.equals("NA")&&!value.equals("A")) {
                        String fieldName = fieldNameProvider.getFieldName(indexedProperty, null,
                                FieldNameProvider.FieldType.INDEX);
                        fieldValues.add(new FieldValue(fieldName, value));
                    }
                }
            }
        } else {
            throw new FieldValueProviderException("Cannot evalute size of non product item");
        }
        return fieldValues;
    }

    public void setFieldNameProvider(FieldNameProvider fieldNameProvider) {
        this.fieldNameProvider = fieldNameProvider;
    }
}
