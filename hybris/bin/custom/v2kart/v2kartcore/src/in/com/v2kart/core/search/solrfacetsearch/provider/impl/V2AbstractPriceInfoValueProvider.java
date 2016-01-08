package in.com.v2kart.core.search.solrfacetsearch.provider.impl;

import in.com.v2kart.core.model.V2kartStyleVariantProductModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;
import de.hybris.platform.variants.model.VariantProductModel;

public abstract class V2AbstractPriceInfoValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider,
        Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private PromotionsService promotionService;

    private BaseSiteService baseSiteService;

    private TimeService timeService;

    private PriceService priceService;

    private FieldNameProvider fieldNameProvider;

    private CommonI18NService commonI18NService;

    public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty, final Object model)
            throws FieldValueProviderException {
        final List<FieldValue> fieldValues = new ArrayList<FieldValue>();
        if (null != model && model instanceof ProductModel) {
            ProductModel productModel = (ProductModel) model;
            if (productModel instanceof V2kartStyleVariantProductModel) {
                V2kartStyleVariantProductModel v2kartStyleVariantProductModel = (V2kartStyleVariantProductModel) model;
                Collection<VariantProductModel> v2SizeVariantProducts = v2kartStyleVariantProductModel.getVariants();
                if (v2SizeVariantProducts.size() > 0) {
                    // logic changed iterate over the size variant find price if found one then index and break the loop
                    Iterator<VariantProductModel> variantProducts = v2SizeVariantProducts.iterator();
                    ProductModel variantProduct = null;
                    boolean priceIndexedForSizeVariant = false;
                    while (variantProducts.hasNext()) {
                        variantProduct = variantProducts.next();
                        Collection<CurrencyModel> currencies = indexConfig.getCurrencies();
                        if (!CollectionUtils.isEmpty(currencies)) {
                            for (CurrencyModel currencyModel : currencies) {
                                commonI18NService.setCurrentCurrency(currencyModel);
                                Double value = addFieldValueForProduct(variantProduct);
                                if (null != value) {
                                    String fieldName = fieldNameProvider.getFieldName(indexedProperty, currencyModel.getIsocode(),
                                            FieldNameProvider.FieldType.INDEX);
                                    fieldValues.add(new FieldValue(fieldName, value));
                                    // set break loop to true
                                    priceIndexedForSizeVariant = true;
                                }
                            }
                        }
                        if (priceIndexedForSizeVariant) {
                            // reset the priceIndexedForSizeVariant for next
                            priceIndexedForSizeVariant = false;
                            // break the loop after first price is indexed for size variant.
                            break;
                        }
                    }
                }
            }

        } else {
            throw new FieldValueProviderException("Cannot evaluate price of non-product item");
        }
        return fieldValues;
    }

    public abstract Double addFieldValueForProduct(ProductModel productModel);

    public void setPromotionService(PromotionsService promotionService) {
        this.promotionService = promotionService;
    }

    public void setBaseSiteService(BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }

    public void setPriceService(PriceService priceService) {
        this.priceService = priceService;
    }

    public void setFieldNameProvider(FieldNameProvider fieldNameProvider) {
        this.fieldNameProvider = fieldNameProvider;
    }

    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public PromotionsService getPromotionService() {
        return promotionService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public TimeService getTimeService() {
        return timeService;
    }

    public PriceService getPriceService() {
        return priceService;
    }

}
