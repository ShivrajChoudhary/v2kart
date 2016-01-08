package in.com.v2kart.facades.populators;

import java.util.Arrays;

import javax.annotation.Resource;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;

public class WishlistEntryPopulator implements Populator<Wishlist2EntryModel, OrderEntryData> {
    @Resource(name = "accProductFacade")
    private ProductFacade productFacade;

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
     */
    @Override
    public void populate(final Wishlist2EntryModel source, final OrderEntryData target) throws ConversionException {

        final ProductData productData = productFacade.getProductForOptions(source.getProduct(), Arrays.asList(ProductOption.BASIC,
                ProductOption.PRICE, ProductOption.SUMMARY, ProductOption.DESCRIPTION, ProductOption.REVIEW, ProductOption.STOCK));

        target.setProduct(productData);
        target.setBasePrice(productData.getPrice());

    }

}
