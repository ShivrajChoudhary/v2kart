package in.com.v2kart.core.search.solrfacetsearch.provider.impl;

import in.com.v2kart.core.model.V2kartStyleVariantProductModel;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.promotions.model.ProductPercentageDiscountPromotionModel;
import de.hybris.platform.promotions.model.ProductPromotionModel;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.variants.model.VariantProductModel;

public class V2DiscountedPriceValueProvider extends V2AbstractPriceInfoValueProvider {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Double addFieldValueForProduct(ProductModel productModel) {
        Double discountedPriceValue = 0.0;
        if (null != productModel) {
            List<PriceInformation> priceInformations = this.getPriceService().getPriceInformationsForProduct(productModel);
            if (!CollectionUtils.isEmpty(priceInformations)) {
                PriceInformation price = priceInformations.get(0);
                PriceValue priceValue = price.getPriceValue();
                Double discount = findPercentageDiscount(productModel);
                discountedPriceValue = calculateCustomerSellingPrice(priceValue, discount);
            }
        }
        return discountedPriceValue;
    }

    private Double findPercentageDiscount(ProductModel productModel) {
        Double discount = 0.0;
        if (null != productModel) {
            BaseSiteModel baseSite = this.getBaseSiteService().getCurrentBaseSite();
            if (null != baseSite) {
                final PromotionGroupModel defaultPromotionGroup = baseSite.getDefaultPromotionGroup();
                final Date currentTimeRoundedToMinute = DateUtils.round(this.getTimeService().getCurrentTime(), Calendar.MINUTE);
                if (null != defaultPromotionGroup && productModel instanceof VariantProductModel) {

                    final ProductModel styleProduct = ((VariantProductModel) productModel).getBaseProduct();
                    if (styleProduct instanceof V2kartStyleVariantProductModel) {
                        final List<ProductPromotionModel> promotions = this.getPromotionService().getProductPromotions(
                                Collections.singletonList(defaultPromotionGroup), styleProduct, true,
                                currentTimeRoundedToMinute);

                        if (!CollectionUtils.isEmpty(promotions)) {
                            for (ProductPromotionModel productPromotionModel : promotions) {
                                if (productPromotionModel instanceof ProductPercentageDiscountPromotionModel) {
                                    discount = ((ProductPercentageDiscountPromotionModel) productPromotionModel)
                                            .getPercentageDiscount();
                                }
                            }
                        }
                    }
                }
            }
        }
        return discount;
    }

    private Double calculateCustomerSellingPrice(PriceValue mrp, Double discount) {
        double csp = 0.0;
        if (null != mrp && null != discount) {
            double price = mrp.getValue();
            csp = price - (price * (discount / 100));
        }
        return csp;
    }

}
