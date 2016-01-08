package in.com.v2kart.core.search.solrfacetsearch.provider.impl;

import in.com.v2kart.core.model.V2kartStyleVariantProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;
import de.hybris.platform.variants.model.VariantProductModel;

public class V2DisplayProductWithPricesValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider {

    private PriceService priceService;

    private FieldNameProvider fieldNameProvider;

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.hybris.platform.solrfacetsearch.provider.FieldValueProvider#getFieldValues(de.hybris.platform.solrfacetsearch.config.IndexConfig,
     * de.hybris.platform.solrfacetsearch.config.IndexedProperty, java.lang.Object)
     */
    @Override
    public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty, final Object model)
            throws FieldValueProviderException {
        final List<FieldValue> fieldValues = new ArrayList<>(0);
        if (model instanceof V2kartStyleVariantProductModel) {
            V2kartStyleVariantProductModel v2kartStyleVariantProductModel = (V2kartStyleVariantProductModel) model;
            List<VariantProductModel> v2SizeVariantProducts = (List<VariantProductModel>) v2kartStyleVariantProductModel.getVariants();

            boolean hasPriceInformation = true;

            // if style variant doesn't have size variant, then check for price information at style variant
            if (CollectionUtils.isEmpty(v2SizeVariantProducts)) {
                if (CollectionUtils.isEmpty(priceService.getPriceInformationsForProduct(v2kartStyleVariantProductModel))) {
                    hasPriceInformation = false;
                }
            } else {
                // if style variant has size variants, then check price information at size variant
                for (VariantProductModel variantProduct : v2SizeVariantProducts) {
                    if (CollectionUtils.isEmpty(priceService.getPriceInformationsForProduct(variantProduct))) {
                        hasPriceInformation = false;
                        break;
                    }
                }
            }

            if (Boolean.TRUE.equals(hasPriceInformation)) {
                String fieldName = fieldNameProvider.getFieldName(indexedProperty, null,
                        FieldNameProvider.FieldType.INDEX);
                fieldValues.add(new FieldValue(fieldName, Boolean.TRUE));
            }
        }
        return fieldValues;
    }

    /**
     * 
     * @param priceService
     */
    @Required
    public void setPriceService(PriceService priceService) {
        this.priceService = priceService;
    }

    /**
     * 
     * @param fieldNameProvider
     */
    @Required
    public void setFieldNameProvider(FieldNameProvider fieldNameProvider) {
        this.fieldNameProvider = fieldNameProvider;
    }

}
