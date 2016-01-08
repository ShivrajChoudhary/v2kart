package in.com.v2kart.facades.populators;

import in.com.v2kart.core.model.V2kartStyleVariantProductModel;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPromotionsPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.promotions.model.ProductPromotionModel;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.variants.model.VariantProductModel;

public class V2ProductPromotionsPopulator extends ProductPromotionsPopulator<ProductModel, ProductData> {

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.hybris.platform.commercefacades.product.converters.populator.ProductPromotionsPopulator#populate(de.hybris.platform.core.model
     * .product.ProductModel, de.hybris.platform.commercefacades.product.data.ProductData)
     */
    @Override
    public void populate(ProductModel productModel, ProductData productData) throws ConversionException {
        final BaseSiteModel baseSiteModel = getBaseSiteService().getCurrentBaseSite();
        if (baseSiteModel != null)
        {
            final PromotionGroupModel defaultPromotionGroup = baseSiteModel.getDefaultPromotionGroup();
            final Date currentTimeRoundedToMinute = DateUtils.round(getTimeService().getCurrentTime(), Calendar.MINUTE);

            if (defaultPromotionGroup != null)
            {
                // get promotions from size variant product
                List<ProductPromotionModel> promotions = getPromotionsService().getProductPromotions(
                        Collections.singletonList(defaultPromotionGroup), productModel, true, currentTimeRoundedToMinute);

                // if size variant product promotions are empty, then check for style variant product to get promotions
                if (CollectionUtils.isEmpty(promotions) && productModel instanceof VariantProductModel) {
                    final ProductModel styleProduct = ((VariantProductModel) productModel).getBaseProduct();
                    if (styleProduct instanceof V2kartStyleVariantProductModel) {
                        promotions = getPromotionsService().getProductPromotions(
                                Collections.singletonList(defaultPromotionGroup), styleProduct, true, currentTimeRoundedToMinute);
                    }
                }
                productData.setPotentialPromotions(Converters.convertAll(promotions, getPromotionsConverter()));
            }
        }
    }

}
