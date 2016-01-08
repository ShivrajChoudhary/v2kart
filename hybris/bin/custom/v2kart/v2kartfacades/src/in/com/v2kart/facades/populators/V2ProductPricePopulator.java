package in.com.v2kart.facades.populators;

import in.com.v2kart.core.model.V2kartStyleVariantProductModel;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPricePopulator;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.model.ProductPercentageDiscountPromotionModel;
import de.hybris.platform.promotions.model.ProductPromotionModel;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.variants.model.VariantProductModel;

public class V2ProductPricePopulator extends ProductPricePopulator<ProductModel, ProductData> {

    private PromotionsService promotionService;

    private BaseSiteService baseSiteService;

    private TimeService timeService;

    @Override
    public void populate(final ProductModel productModel, final ProductData productData) throws ConversionException {

        final PriceDataType priceType;
        final PriceInformation info;
        if (CollectionUtils.isEmpty(productModel.getVariants()))
        {
            priceType = PriceDataType.BUY;
            info = getCommercePriceService().getWebPriceForProduct(productModel);
        }
        else
        {
            priceType = PriceDataType.FROM;
            info = getCommercePriceService().getFromPriceForProduct(productModel);
        }

        if (info != null)
        {
            final BaseSiteModel baseSiteModel = getBaseSiteService().getCurrentBaseSite();
            final PriceData priceData = getPriceDataFactory().create(priceType, BigDecimal.valueOf(info.getPriceValue().getValue()),
                    info.getPriceValue().getCurrencyIso());
            productData.setPrice(priceData);
            if (null != baseSiteModel) {
                final PromotionGroupModel defaultPromotionGroup = baseSiteModel.getDefaultPromotionGroup();
                final Date currentTimeRoundedToMinute = DateUtils.round(getTimeService().getCurrentTime(), Calendar.MINUTE);
                if (null != defaultPromotionGroup) {

                    List<ProductPromotionModel> promotions = getPromotionService().getProductPromotions(
                            Collections.singletonList(defaultPromotionGroup), productModel, true, currentTimeRoundedToMinute);

                    if (!CollectionUtils.isEmpty(promotions)) {
                        // get promotions from size variant product
                        setProductPromotionalPrice(promotions, productData, priceType, info);
                    } else {
                        // if size variant product promotions are empty, then check for style variant product to get promotions
                        if (productModel instanceof VariantProductModel) {
                            final ProductModel styleProduct = ((VariantProductModel) productModel).getBaseProduct();
                            if (styleProduct instanceof V2kartStyleVariantProductModel) {
                                promotions = getPromotionService().getProductPromotions(
                                        Collections.singletonList(defaultPromotionGroup), styleProduct, true, currentTimeRoundedToMinute);
                                setProductPromotionalPrice(promotions, productData, priceType, info);
                            }
                        }
                    }
                }
            }
        }
        else
        {
            productData.setPurchasable(Boolean.FALSE);
        }

    }

    /*
     * Sets the discounted price of the product.
     */
    private void setProductPromotionalPrice(List<ProductPromotionModel> promotions, final ProductData productData,
            final PriceDataType priceType, final PriceInformation info) {
        for (ProductPromotionModel productPromotionModel : promotions) {
            if (productPromotionModel instanceof ProductPercentageDiscountPromotionModel) {
                Double discount = ((ProductPercentageDiscountPromotionModel) productPromotionModel).getPercentageDiscount();
                productData.setPercentageDiscount(discount.intValue());
                Double csp = calculateCustomerSellingPrice(info.getPriceValue(), discount);
                if (null != csp) {
                    final PriceData customerSellingPrice = getPriceDataFactory().create(priceType, BigDecimal.valueOf(csp),
                            info.getPriceValue().getCurrencyIso());
                    productData.setDiscountedPrice(customerSellingPrice);
                }
            }
        }
    }

    private Double calculateCustomerSellingPrice(PriceValue mrp, Double discount) {
        Double csp = null;
        if (null != mrp && null != discount) {
            double price = mrp.getValue();
            csp = price - (price * (discount / 100));
        }
        return csp;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public PromotionsService getPromotionService() {
        return promotionService;
    }

    public void setPromotionService(PromotionsService promotionService) {
        this.promotionService = promotionService;
    }

    public TimeService getTimeService() {
        return timeService;
    }

    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }

}
